package rhetoricDetection.aitools.collection;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceConfigurationException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;
import org.xml.sax.SAXException;

import de.aitools.ie.uima.type.core.SourceDocumentInformation;

/**
 * Reads all texts from a given collection of UIMA XMI files and inserts the 
 * text into a UIMA CAS object. 
 * 
 * The XMI files are taken from a specified directory and all its 
 * subdirectories. 
 * 
 * All annotations stored in the XMI file are added to the CAS object.
 * 
 * This collection reader is a modified version of one of the UIMA 
 * example collection readers.
 * 
 * @author henning.wachsmuth
 *
 */
public class UIMAAnnotationFileReader extends CollectionReader_ImplBase {
	
	// -------------------------------------------------------------------------
	// UIMA PARAMETERS
	// -------------------------------------------------------------------------

  	/**
  	 * Parameter that specifies the path of the directory with the texts to be
	 * processed.
  	 */
  	public static final String PARAM_INPUTDIR = "InputDirectory";

  	/**
  	 * Parameter that specifies whether subdirectories of the specified 
  	 * directory shall also be processed.
  	 */
  	public static final String PARAM_SUBDIRS = "IncludeSubdirectories";
  	
  	/**
  	 * Parameter that specifies whether to sort the files to be read according
  	 * to their absolute file path in increasing order.
  	 */
  	public static final String PARAM_SORT = "SortFilesByPath";
  	
  	
	// -------------------------------------------------------------------------
	// CONSTANTS
	// -------------------------------------------------------------------------

	/**
	 * Suffix of plain text files
	 */
	public static String SUFFIX = ".xmi";
	
	
	
	// -------------------------------------------------------------------------
	// REFERENCES
	// -------------------------------------------------------------------------
  	
	/**
	 * List of files to be read
	 */
  	private List<File> mFiles;

  	/**
  	 * Current index in reading process
  	 */
  	private int mCurrentIndex;

  	
	// -------------------------------------------------------------------------
	// INTERFACE IMPLEMENTATION
	// -------------------------------------------------------------------------

  	/**
  	 * @see org.apache.uima.collection.CollectionReader_ImplBase#initialize()
  	 */
  	public void initialize() throws ResourceInitializationException {
  		// Load input directory
  		String dir = ((String) getConfigParameterValue(PARAM_INPUTDIR)).trim();
//  		dir = ClassLoader.getSystemResource(dir).getPath();
  		File directory = new File(dir);
	    // If input directory does not exist, throw exception
	    if (!directory.exists() || !directory.isDirectory()) {
	      throw new ResourceInitializationException(
	    		  ResourceConfigurationException.DIRECTORY_NOT_FOUND,
	              new Object[] { PARAM_INPUTDIR, this.getMetaData().getName(), 
	    				  directory.getPath() });
	    }
  		// Now add all files
  		boolean includeSubdir = 
  				(Boolean) getConfigParameterValue(PARAM_SUBDIRS);
  		this.mFiles = this.addFilesRecursively(directory, includeSubdir);
  		
  		// Now sort files
  		boolean sort = (Boolean) getConfigParameterValue(PARAM_SORT);
  		if (sort){
  			this.mFiles = this.sortFilesByAbsolutePath(mFiles);
  		}
  		
  		// Set current number of read documents
  		this.mCurrentIndex = 0;
  	}
  	
  	/**
     * @see org.apache.uima.collection.CollectionReader#hasNext()
     */
    public boolean hasNext() {
    	return mCurrentIndex < mFiles.size();
    }
  	
    /**
     * @see org.apache.uima.collection.CollectionReader#getNext(org.apache.uima.cas.CAS)
     */
    public void getNext(CAS aCAS) throws IOException, CollectionException {
    	File currentFile = (File) this.mFiles.get(mCurrentIndex++);
    	try (FileInputStream is = new FileInputStream(currentFile);){
    		// Get text and annotations from cas
			XmiCasDeserializer.deserialize(is, aCAS);
			JCas jcas = aCAS.getJCas();
		    // Add source information to document
			SourceDocumentInformation sdi = new SourceDocumentInformation(jcas);
	    	sdi.setUri(currentFile.getAbsolutePath());
	    	sdi.setOffsetInSource(0);
	    	sdi.setDocumentSize(jcas.getDocumentText().length());
	    	sdi.setLastSegment(this.mCurrentIndex == this.mFiles.size());
	    	sdi.addToIndexes();
		} catch (SAXException e) {
			throw new CollectionException(e);
		} catch (CASException e) {
			throw new CollectionException(e);
		}
    }
    
	
    /**
     * @see org.apache.uima.collection.base_cpm.BaseCollectionReader#close()
     */
    public void close() throws IOException {
    	// Do nothing
    }

    /**
     * @see org.apache.uima.collection.base_cpm.BaseCollectionReader#getProgress()
     */
    public Progress[] getProgress() {
      return new Progress[] { new ProgressImpl(this.mCurrentIndex, 
    		  this.mFiles.size(), Progress.ENTITIES) };
    }
  	
  	
    
	// -------------------------------------------------------------------------
	// ADDITIONAL METHODS
	// -------------------------------------------------------------------------
  	
  	/**
  	 * Recursively collects all XMI files in the directory and all its direct 
  	 * and indirect subdirectories. Returns these XMI files.
  	 * 
  	 * @param directory The directory
  	 * @param includeSubdirs Whether subdirectories shall also be processed. 
  	 * @return The collected XMI files
  	 */
  	private List<File> addFilesRecursively(File directory, 
  			boolean includeSubdirs){
  		List<File> files = new ArrayList<File>();
  		if (directory.exists() && directory.isDirectory()){
	  		File [] allFiles = directory.listFiles();
	  		for (int i = 0; i < allFiles.length; i++) {
	  			if (allFiles[i].isDirectory() && includeSubdirs){
	  				files.addAll(this.addFilesRecursively(allFiles[i],
  							includeSubdirs));
	  			} if (allFiles[i].getName().toLowerCase().endsWith(SUFFIX)) {
	  				files.add(allFiles[i]);
	  			}
	  		}
  		}
  		return files;
  	}
  	
  	/**
  	 * Sorts and return the list of files according to their absolute file paths
  	 * in increasing order.
  	 *  
  	 * @param files The files
  	 * @return The sorted files
  	 */
  	private List<File> sortFilesByAbsolutePath(List<File> files){
  		TreeMap<String, File> sortedFileMap = new TreeMap<String, File>();
  		for (File file : files) {
			sortedFileMap.put(file.getAbsolutePath(), file);
		}
  		List<File> sortedFiles = new ArrayList<File>();
  		for (String path : sortedFileMap.keySet()) {
  			sortedFiles.add(sortedFileMap.get(path));
		}
  		return sortedFiles;
  	}
  	
	/**
	 * Gets the total number of documents that will be returned by this 
	 * collection reader. 
	 * 
	 * @return The number of documents in the collection
	 */
	public int getNumberOfDocuments() {
	    return this.mFiles.size();
	}

	   	    
}
