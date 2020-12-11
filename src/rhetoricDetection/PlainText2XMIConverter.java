package rhetoricDetection;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.XMLInputSource;

import de.aitools.ie.uima.type.core.SourceDocumentInformation;
import de.aitools.ie.uima.io.UIMAAnnotationFileWriter;


/**
 * Reads articles and converts them to xmi with features
 * 
 * Adapted to read text as well as XML documents (for NYT corpus).
 * 
 * @author team
 *
 */
public class PlainText2XMIConverter {

	// -------------------------------------------------------------------------
	// PARAMETERS
	// -------------------------------------------------------------------------

	/**
	 * The path of root directory of the files to be processed.
	 */
	private static final String INPUT_COLLECTION_DIR = 
//			"data/editorials/";
//			"data/sample-data/";
//			"data/stopwords/";
			"data/rhetorical-devices-recall/";
//			"data/10articlesForAnnotators/";
//			"data/isocolon-evaluation/isocolon-in-files/";
//			"data/isocolon-evaluation/all-but-isocolon/";
	/**
	 * The path of the XMI file of the collection reader to be used to iterate
	 * over all files to be processed.
	 */
	private static final String COLLECTION_READER_PATH = 
			//local run
//			"../aitools4-ie-uima/conf/uima-descriptors/collection-readers/" +
//			"UIMAPlainTextReader.xml";
			"conf/uima-descriptors/collection-readers/" +
			"UIMAPlainTextReader.xml";
//			"conf/uima-descriptors/collection-readers/" +
//			"UIMAXMLReader.xml";
			//FOR NYT CORPUS XML FORMAT ARTICLES
//			PlainText2XMIConverter.class.getResource("/uima-descriptors/collection-readers/" +
//					"UIMAXMLReader.xml").toString();
			//FOR TEXT FORMAT ARTICLES
//			PlainText2XMIConverter.class.getResource("/uima-descriptors/collection-readers/" +
//					"UIMAPlainTextReader.xml").toString();
	


	/**
	 * The path of the XMI file of the analysis engine to be used to process
	 * the files.
	 */

	private static final String ANALYSIS_ENGINE_PATH = 
			"conf/uima-descriptors/aggregate-AEs/"
			+ "PlainTextAnnotatorPipeline.xml";
//			new String(PlainText2XMIConverter.class.getResource("conf/uima-descriptors/aggregate-AEs/"
//					+ "PlainTextAnnotatorPipeline.xml").getPath());
//			PlainText2XMIConverter.class.getResource("/uima-descriptors/aggregate-AEs/"
//			+ "PlainTextAnnotatorPipeline.xml").toString();

	/**
	 * The path of the directory where the XMI files shall be written to
	 */
	private static final String OUTPUT_COLLECTION_DIR = 
//			"results/editorials-xmi/";
//			"results/temp-test/";
			"results/evaluation-xmi/";
//			"results/10articlesForAnnotators-xmi/";
//			"results/debates-xmi/";
//			"results/isocolon-evaluation-xmi/isocolon-xmi-files/";
//			"results/isocolon-evaluation-xmi/all-but-isocolon-xmi/";

    
	private static final List<String> documents2Ignore = Arrays.asList(new String[]{
		"others-class", "all", "cat_balance", "bicolon", "cat_repetition", 
		"isocolon-test-not-works", "tricolon", "tetracolon", "mesarchia", "mesodiplosis",
		"allWithoutIsocolon"});
	
	/**
     * The maximum number of files to be processed
     */
    private static final long maxFilesToProcess = 
    		1;
//    		Long.MAX_VALUE;
    
    /**
     * The type of the input folder hierarchical organization
     */
    private static final boolean inputInSeparateFolders = false;
	
	// -------------------------------------------------------------------------
	// PROCESSING
	// -------------------------------------------------------------------------
	
	/**
	 * Main processing method
	 */
//	public void processCollection(String input, String output){
	public void processCollection(){
		//System.out.println("Input: "+COLLECTION_READER_PATH +"\n Output: "+ANALYSIS_ENGINE_PATH);
		// Initialize UIMA engines for German texts
		CollectionReader cr = this.createCollectionReader(
				COLLECTION_READER_PATH, INPUT_COLLECTION_DIR);
		AnalysisEngine ae = this.createAnalysisEngine(ANALYSIS_ENGINE_PATH);
		
		// Initialize output file writer
		UIMAAnnotationFileWriter xmiWriter = new UIMAAnnotationFileWriter();
		
		long time = System.currentTimeMillis();
		
		// Iterate with collection reader over collection and process each text
		// with analysis engine
		System.out.println("Processing collection...");
		
		//file processed counter
		int counter = 0;

  		try{
  			// Create CAS object only once to avoid memory overhead
  			CAS aCAS = ae.newCAS();
  			// Stepwise process each text managed by the collection reader
  			// limit the number of processed files to one file by counter<1
  			while (cr.hasNext() && counter<maxFilesToProcess){  
  				
  				// Store current text in JCas object
  				cr.getNext(aCAS);
  				JCas jcas = aCAS.getJCas();
  				
  				// Get file name (created by collection reader)
  				SourceDocumentInformation sdi = (SourceDocumentInformation)
  						jcas.getAnnotationIndex(SourceDocumentInformation.type)
  						.iterator().next();
				String inputFileName = new File(sdi.getUri()).getName().split("\\.")[0];
  				
  				if (jcas.getDocumentText() == null || documents2Ignore.contains(inputFileName)) {
					continue;
				}
  				
  				// Add annotations to text with aggregate analysis engine
  				try {
					ae.process(jcas);
				} catch (OutOfMemoryError e) {
					// TODO Auto-generated catch block
					System.err.println("File skipped due to large memory usage");
					continue;
				}
  				
  				//case when the input documents are contained within separate folders
  				if (inputInSeparateFolders) {

  					String inputFileParentName = new File(sdi.getUri()).getParentFile().getName();
  					//FOR ADDITIONAL PARENT FOLDER STRUCTURE
/*  					String inputFileParentParentName = new File(sdi.getUri()).getParentFile().getParentFile().getName();
  					createDirectory(new File(output+inputFileParentParentName));
  					createDirectory(new File(output+inputFileParentParentName + File.separator + inputFileParentName));
  					xmiWriter.write(jcas.getCas(), output+inputFileParentParentName + File.separator + inputFileParentName+System.getProperty("file.separator"), 
  							inputFileName.substring(0,inputFileName.indexOf('.'))+".xmi");*/
  					
  					createDirectory(new File(OUTPUT_COLLECTION_DIR+inputFileParentName));
  					// Write text with annotations to file - case when file names are not distinct
  					xmiWriter.write(jcas.getCas(), OUTPUT_COLLECTION_DIR+inputFileParentName+System.getProperty("file.separator"), 
  							inputFileName +".xmi");
  					// Print progress
  					//System.out.println(inputFileName.substring(inputFileName.lastIndexOf("\\", inputFileName.lastIndexOf("\\")-1)+1, 
  					//		inputFileName.lastIndexOf("\\"))+".xmi");
  					System.out.println(inputFileName +".xmi");
				} 
  				//case when the input documents are contained within one folder
  				else {
					// Write text with annotations to file - case when file names are distinct
	  				xmiWriter.write(jcas.getCas(), OUTPUT_COLLECTION_DIR, 
			  						inputFileName +".xmi");
					// Print progress
	  				System.out.println(inputFileName +".xmi");
				}
  				counter++;
  			}
  		} catch (Exception ex){
  			ex.printStackTrace();
  		}
		
  		//estimate run time
		time = System.currentTimeMillis() - time;
		System.out.println("\nfinished in " + (time/1000.0) + "s\n");

		// Destroy UIMA engines
		cr.destroy();
		ae.destroy();
	}


	// -------------------------------------------------------------------------
	// INITIALIZATION
	// -------------------------------------------------------------------------

	/**
	 * Returns the collection reader in the file with the given path to be used 
	 * to iterate over the input directory with the given path. 
	 * 
	 * @param crPath The path of the collection reader
	 * @param inputDir The path of the input directory
	 * @return The collection reader.
	 */
	private CollectionReader createCollectionReader(String crPath, 
			String inputDir){
		System.out.print("Initializing \"" + crPath + "\"...");
		long count = System.currentTimeMillis();
		CollectionReader cr = null;
		try{
			// Create collection reader from XML descriptor
			XMLInputSource xmlInputSource = new XMLInputSource(crPath);
			ResourceSpecifier specifier = 
				UIMAFramework.getXMLParser().parseResourceSpecifier(
						xmlInputSource);
			cr = UIMAFramework.produceCollectionReader(specifier);
			
			// Change parameter values and, therefore, reconfigure reader
			cr.setConfigParameterValue("InputDirectory", inputDir);
			cr.setConfigParameterValue("IncludeSubdirectories", true);
			cr.reconfigure();
		} catch (Exception ex){
			ex.printStackTrace();
		}
		count = System.currentTimeMillis() - count;
		System.out.println("\nfinished in " + (count/1000.0) + "s\n");
		return cr;
	}

	/**
	 * Creates and returns the analysis engine that refers to the specified
	 * descriptor file path using the specified lexicon for abbreviations.
	 * 
	 * @param aePath The path of descriptor file the analysis engine
	 * @return The analysis engine
	 */
	private AnalysisEngine createAnalysisEngine(String aePath){
		System.out.print("Initializing \"" + aePath + "\"...");
		long count = System.currentTimeMillis();
		AnalysisEngine ae = null;
		try{
			// Create analysis engine from XML descriptor
			XMLInputSource xmlInputSource = new XMLInputSource(aePath);
			ResourceSpecifier specifier = 
				UIMAFramework.getXMLParser().parseResourceSpecifier(
						xmlInputSource);
			ae = UIMAFramework.produceAnalysisEngine(specifier);
			ae.reconfigure();
		} catch (Exception ex){
			ex.printStackTrace();
		}
		count = System.currentTimeMillis() - count;
		System.out.println("\nfinished in " + (count/1000.0) + "s\n");
		return ae;
	}

	// -------------------------------------------------------------------------
	// HELPER
	// -------------------------------------------------------------------------
	
	
	/**
	 * Get the nth occurrence index of a character in a string
	 * It is used at getting the right name for the output files
	 * case when input files' names are irrelevant
	 * 
	 * @param str 
	 * 				- input string
	 * @param c
	 * 				- character to find the index for
	 * @param n
	 * 				- nth occurrence of the searched char
	 * @return pos
	 * 				- index of the nth occurrence of the searched char
	 *
	 */
	public int nthLastIndexOf(String str, char c, int n) {
        if (str == null || n < 1)
            return -1;
        int pos = str.length();
        while (n-- > 0 && pos != -1)
            pos = str.lastIndexOf(c, pos - 1);
        return pos;
	}
	
	/**
	 * Method to create a new directory(if it doesn't exist) for article category 
	 * 
	 * @param dirName
	 * 				- path to create new directories in
	 */
	private static void createDirectory(File dirName) {
		if (!dirName.exists()) {
			try {
				dirName.mkdir();
			} catch (SecurityException se) {
				// handle it
				System.out.println("Security exception!");
			}
		}
	}
	
	// -------------------------------------------------------------------------
	// MAIN
	// -------------------------------------------------------------------------
	
	/**
	 * Starts the collection processing.
	 * 
	 * @param args Not used
	 */
	public static void main(String [] args){
		PlainText2XMIConverter spc = new PlainText2XMIConverter();
		
//		spc.processCollection(args[0], args[1]);
		spc.processCollection();
	}
}
