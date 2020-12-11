/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package rhetoricDetection.aitools.collection;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceConfigurationException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.FileUtils;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

import de.aitools.ie.uima.type.core.SourceDocumentInformation;
import rhetoricDetection.SyntacticRhetoricSimpleAnnotator;

/**
 * Reads all texts from a given collection of plain text files 
 * (with suffix "txt") and inserts the text into a UIMA CAS object. 
 * 
 * The text files are taken from a specified directory and all its subdirectories. 
 * 
 * This collection reader is a modified version of one of the UIMA 
 * example collection readers.
 * 
 * @author henning.wachsmuth
 */
public class UIMAPlainTextReader extends CollectionReader_ImplBase {
	
	// -------------------------------------------------------------------------
	// UIMA PARAMETERS
	// -------------------------------------------------------------------------

	/**
	 * Parameter that specifies the path of the directory with the texts to be
	 * processed.
	 */
	public static final String PARAM_INPUTDIR = "InputDirectory";

	/**
	 * Parameter that specifies the encoding of the texts to be processed.
	 */
	public static final String PARAM_ENCODING = "Encoding";

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
	public static String SUFFIX = ".txt";
	
	
	
	// -------------------------------------------------------------------------
	// REFERENCES
	// -------------------------------------------------------------------------

	/**
	 * The list of files to be loaded
	 */
	private List<File> mFiles;

	/**
	 * The encoding of the files
	 */
	private String mEncoding;

	/**
	 * The current index of reading
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
//		dir = ClassLoader.getSystemResource(dir).getPath();
		File directory = new File(dir);
	    // if input directory does not exist, throw exception
	    if (!directory.exists() || !directory.isDirectory()) {
	      throw new ResourceInitializationException(
	    		  ResourceConfigurationException.DIRECTORY_NOT_FOUND,
	              new Object[] {PARAM_INPUTDIR, this.getMetaData().getName(), 
	    				  directory.getPath() });
	    }

  		// Now add all files
  		boolean includeSubdir = 
  				(Boolean) getConfigParameterValue(PARAM_SUBDIRS);
  		this.mFiles = this.addFilesRecursively(directory, includeSubdir);
  		
  		// Now sort files
  		boolean sort = (Boolean) getConfigParameterValue(PARAM_SORT);
  		if (sort){
  			this.mFiles = this.sortFilesByAbsolutePath(this.mFiles);
  		}

  		// Set current number of read documents and the encoding
  		this.mCurrentIndex = 0;
		String encoding = (String) getConfigParameterValue(PARAM_ENCODING);
		if (encoding != null){
			this.mEncoding = encoding;
		}
	}

	/**
	 * @see org.apache.uima.collection.CollectionReader#hasNext()
	 */
	public boolean hasNext() {
	    return this.mCurrentIndex < this.mFiles.size();
	}

	/**
	 * @see org.apache.uima.collection.CollectionReader#getNext(
	 *   org.apache.uima.cas.CAS)
	 */
	public void getNext(CAS aCAS) throws IOException, CollectionException {
	    // Open input stream to file
	    File file = (File) this.mFiles.get(this.mCurrentIndex++);
	    String text = FileUtils.file2String(file, this.mEncoding);
//	    String text = "This is a sample sentence. If this works, I will be happy!";
//	    System.out.println("Input: "+text);
	      // Put document in CAS
	    aCAS.reset();
	    // Notice: Here an invalid character is replaced (the first quotes hold
	    // NOT the empty string!!!
	    text = text.replace("", "");
	    //instantiate SyntacticRhetoricSimpleAnnotator class for getAuxLongForm method call
	    SyntacticRhetoricSimpleAnnotator sra = new SyntacticRhetoricSimpleAnnotator();
	    //replace the contracted forms (i.e. "don't") of auxiliary verbs with the
		//corresponding long forms (i.e. "do not").
	    text = sra.getAuxLongForm(text);
//	    System.out.println(text);
	    aCAS.setDocumentText(text);
	    // Add source information to document
	    try {
	    	JCas jcas = aCAS.getJCas();
	    	
	    	SourceDocumentInformation sdi = new SourceDocumentInformation(jcas);
	    	sdi.setUri(file.getAbsolutePath());
	    	sdi.setOffsetInSource(0);
	    	sdi.setDocumentSize(jcas.getDocumentText().length());
	    	sdi.setLastSegment(this.mCurrentIndex == this.mFiles.size());
	    	sdi.addToIndexes();
	    } catch (Exception ex){
	    	ex.printStackTrace();
	    }
	}

	/**
	 * @see org.apache.uima.collection.base_cpm.BaseCollectionReader#close()
	 */
	public void close() throws IOException {
		  // Do nothing here
	}

	/**
	 * @see org.apache.uima.collection.base_cpm.BaseCollectionReader#getProgress()
	 */
	public Progress[] getProgress() {
	    return new Progress[] { 
	    		new ProgressImpl(this.mCurrentIndex, this.mFiles.size(), 
	    				Progress.ENTITIES) 
	    	};
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
	    return mFiles.size();
	}
}
