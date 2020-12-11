package rhetoricDetection.analysis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.XMLInputSource;


import de.aitools.ie.uima.type.core.Sentence;
import de.aitools.ie.uima.type.core.SourceDocumentInformation;

/**
 * Reads articles and converts them to xmi with features
 * 
 * Adapted to read text as well as XML documents (for NYT corpus).
 * 
 * @author team
 *
 */
public class NYTDocumentSelector {

		// -------------------------------------------------------------------------
		// PARAMETERS
		// -------------------------------------------------------------------------

		/**
		 * The path of root directory of the files to be processed.
		 */
		private static final String INPUT_COLLECTION_DIR = 
				"D:\\Documents\\An V Sem III (master)\\Project 2 - Evidence Detection\\Dataset\\HiWi project\\nyt-corpus\\temp-decompressed-files";
		/**
		 * The path of the XMI file of the collection reader to be used to iterate
		 * over all files to be processed.
		 */
		private static final String COLLECTION_READER_PATH = 
				"conf/uima-descriptors/collection-readers/" +
				"UIMAXMLReader.xml";
		/**
		 * The path of the XMI file of the analysis engine to be used to process
		 * the files.
		 */

		private static final String ANALYSIS_ENGINE_PATH = 
				"conf/uima-descriptors/aggregate-AEs/"
				+ "PreprocessingBasicPipeline.xml";

		/**
		 * The path of the directory where the XMI files shall be written to
		 */
		private static final String OUTPUT_COLLECTION_DIR = 
				"results/selectedDocuments/";

	    
		// -------------------------------------------------------------------------
		// PROCESSING
		// -------------------------------------------------------------------------
		
		/**
		 * Main processing method
		 */
		public void processCollection(){
			CollectionReader cr = this.createCollectionReader(
					COLLECTION_READER_PATH, INPUT_COLLECTION_DIR);
			AnalysisEngine ae = this.createAnalysisEngine(ANALYSIS_ENGINE_PATH);
			
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
	  			while (cr.hasNext()){  
	  				// Store current text in JCas object
	  				cr.getNext(aCAS);
	  				JCas jcas = aCAS.getJCas();
	  				
	  				if (jcas.getDocumentText() == null) {
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
	  				AnnotationIndex<Sentence> sentences = jcas.getAnnotationIndex(Sentence.type);
	  				// Get file name (created by collection reader)
	  				SourceDocumentInformation sdi = (SourceDocumentInformation)
	  						jcas.getAnnotationIndex(SourceDocumentInformation.type)
	  						.iterator().next();
	  				String inputFileName = new File(sdi.getUri()).getName().replace(".xml", ".txt");
					
	  				if (sentences.size() >= 40 && sentences.size() <= 60) {
	  					counter++;
						writeToFile(inputFileName, jcas.getDocumentText());
					}
	  				if (counter > 20) {
						break;
					}
	  				
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
		
		private void writeToFile(String fileName, String content) throws IOException {
			BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_COLLECTION_DIR+fileName));
			writer.write(content);
			writer.close();
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
			NYTDocumentSelector spc = new NYTDocumentSelector();
			
//			spc.processCollection(args[0], args[1]);
			spc.processCollection();
		}

}
