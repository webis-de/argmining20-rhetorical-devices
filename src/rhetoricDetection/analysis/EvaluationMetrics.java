/**
 * 
 */
package rhetoricDetection.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Type;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.XMLInputSource;

import de.aitools.ie.uima.type.core.SourceDocumentInformation;
import rhetoricDetection.SyntacticRhetoricSimpleAnnotator;
import type.rhetoricDetection.RhetoricalDevice;

/**
 * This class is used to assess the performance of our system
 * in terms of recall/precision/f1-score
 * 
 * @author viorel.morari
 *
 */
public class EvaluationMetrics {

	// -------------------------------------------------------------------------
	// PARAMETERS
	// -------------------------------------------------------------------------
		
		/**
		 * The path of root directory of the files to be analyzed.
		 */
		private static final String INPUT_COLLECTION_DIR = 
				"results/evaluation-xmi/";
//				"results/10articlesForAnnotators-xmi/";
//				"results/isocolon-evaluation-xmi/isocolon-xmi-files/";
//				"results/isocolon-evaluation-xmi/all-but-isocolon-xmi/";

		/**
		 * The path of the XMI file of the collection reader to be used to iterate
		 * over all files to be analyzed.
		 */
		private static final String COLLECTION_READER_PATH = 
				"../aitools4-ie-uima/conf/uima-descriptors/collection-readers/" +
				"UIMAAnnotationFileReader.xml";
		
		/**
		 * The path of the XMI file of the analysis engine to be used to analyze
		 * the files.
		 */
		private static final String ANALYSIS_ENGINE_PATH = 
				"conf/uima-descriptors/primitive-AEs/"
				+ "DummyStatisticsAE.xml";
		
	    /**
	     * The maximum number of instances per class to be used for the feature set
	     * and the datasets
	     */
	    private long maxTrainingInstances = 
//	    		1;
	    		Long.MAX_VALUE;
	    
	    
	    private String deviceToCalculatePrecissionFor = "Asyndeton";
	    
	    /**
	     * Directory containing true positive instances of the targeted device
	     */
	    private static final String TP_DIRECTORY = 			
	    		"data/rhetorical-devices-recall/";
	    
	    /**
	     * The maximum number of instances per class to be used for the feature set
	     * and the datasets
	     */
	    private static final String TP_FILE = 			
	    		"asyndeton";
	    
	    //CLASS TO EXCLUDE
	    
		private static final List<String> devices2Ignore = Arrays.asList(new String[]{
				"enumeration", "asyndeton", "1cat_repetition"});
	    
	    /**
	     * The maximum number of instances per class to be used for the feature set
	     * and the datasets
	     */
	    private static final String EXCLUDE_TP_FILE1 = 			
	    		"if-cond-three.txt";
	    
	    /**
	     * The maximum number of instances per class to be used for the feature set
	     * and the datasets
	     */
	    private static final String EXCLUDE_TP_FILE2 = 			
	    		"tetracolon.txt";
	    
	
	// -------------------------------------------------------------------------
	// PROCESSING	
	// -------------------------------------------------------------------------
	    
    /**
     * Determines precision value of a specific rhetorical device
     */   
	private void calculatePrecission(){
		
		List<Annotation> rhetoricAnnotations = new ArrayList<Annotation>();
		List<RhetoricalDevice> rhetoricalDevices = new ArrayList<RhetoricalDevice>();
		List<String> tpSentences = new ArrayList<String>();
		List<String> isocolonNeg = new ArrayList<String>();
		//true positive sentences that can be ignored --> allow for overlappings between RD
		List<String> tpSentencesToExclude1 = new ArrayList<String>();
		List<String> tpSentencesToExclude2 = new ArrayList<String>();
		
//		devices2Ignore.add(TP_FILE);
		
		int allAnnotatedInstances = 0;
		int falseInstances = 0;
		int trueInstances = 0;
		
		tpSentences = getTruePositives(TP_DIRECTORY, TP_FILE+".txt");
		
		// Initialize UIMA engines for German texts
				CollectionReader cr = this.createCollectionReader(
						COLLECTION_READER_PATH, INPUT_COLLECTION_DIR);
				AnalysisEngine ae = this.createAnalysisEngine(ANALYSIS_ENGINE_PATH);
				
		        // Start processing
		        int processed = 0;
		        try{
		            // Iterate over all texts
		            CAS aCAS = ae.newCAS();
		            while (cr.hasNext() && processed<maxTrainingInstances){
		            	
		                // Get and preprocess current text
		                cr.getNext(aCAS);
		                JCas jcas = aCAS.getJCas();
//		                System.out.println(getFileName(jcas));

		            	if (devices2Ignore.contains(this.getFileName(jcas))) {
							continue;
						}
		                
		            	rhetoricalDevices.clear();
		                //get the rhetorical devices annotations in the current document
		                rhetoricAnnotations = getClassAnnotations(jcas, 
								"type.rhetoricDetection.RhetoricalDevice");
		                //convert Annotation type to RhetoricalDevices type
		                for (Annotation device : rhetoricAnnotations) {
							rhetoricalDevices.add((RhetoricalDevice) device);
						}
		                
		                
		    		    //tpSentencesToExclude1 = getTruePositives(TP_DIRECTORY, EXCLUDE_TP_FILE1);
		    		    //tpSentencesToExclude2 = getTruePositives(TP_DIRECTORY, EXCLUDE_TP_FILE2);
//	    		        StringBuilder sb = new StringBuilder();
	    		        System.out.println("FP in "+ getFileName(jcas) + ":\n");
		    	        for (RhetoricalDevice device : rhetoricalDevices) {
		    				if (device.getDeviceType().equals(deviceToCalculatePrecissionFor)) {
//		    					sb.append(device.getCoveredText()+"^");
		    					System.out.println(device.getCoveredText());
//		    					System.out.print("^");
		    					allAnnotatedInstances++;
//		    					isocolonNeg.add(device.getCoveredText());
		    					//System.out.println(device.getDeviceType()+": "+device.getCoveredText());
//		    					if (tpSentences.contains(device.getCoveredText())) {
//		    						trueInstances++;
//		    					}
//		    					else{
//		    					//else if(!tpSentencesToExclude1.contains(device.getCoveredText())){
//		    						falseInstances++;
//		    						System.out.println("Negative: "+device.getDeviceType()+": "+device.getCoveredText());
//		    					}
		    				}
		    			}
//		                System.out.println(sb);
		                processed++;
		            }
				 } catch (Exception ex){
			         System.out.println(processed);
			         ex.printStackTrace();
			     }
	        
//		        for (String string : isocolonNeg) {
//					System.out.println("Neg Iso: "+string);
//				}
		        
	        System.out.println("\nTP: "+tpSentences.size());
	        System.out.println("FP: "+allAnnotatedInstances);
		
	}
	
    /**
     * Collects all the true positive instances of one RD
     * 
     * @param folder
     * 				- input folder
     * @param file
     * 				- file with tp sentences of certain RD
     */   
	private List<String> getTruePositives(String folder, String file){
		BufferedReader br;
		String str;
		List<String> docSentences = new ArrayList<String>();
		File dir = new File(folder);
		  File[] directoryListing = dir.listFiles();
		  if (directoryListing != null) {
				try {
					br = new BufferedReader(
							   new InputStreamReader(
					                      new FileInputStream(folder+file), "UTF8"));
					while ((str = br.readLine()) != null) {
						docSentences.add(str);
					}
					br.close();
					
	
				}catch (FileNotFoundException | UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
		  } else {
		    //the case where dir is not really a directory
			  System.out.println("Directory doesn't exist!");
		  }
		  return docSentences;
	}
	
	
	// -------------------------------------------------------------------------
	// HELPER
	// -------------------------------------------------------------------------
	/**
     * Returns all annotations of the given JCas object that refer to the given
     * target class type.
     * 
     * @param jcas The JCas object
     * @param classType The target class type
     * @return The class annotations
     */
    private List<Annotation> getClassAnnotations(JCas jcas, String classType){
        List<Annotation> classInstances = new ArrayList<Annotation>();
		Type type = jcas.getTypeSystem().getType(classType);
		FSIterator<Annotation> classIter = jcas.getAnnotationIndex(type).iterator();
        while (classIter.hasNext()){
            classInstances.add(classIter.next());
        }
        return classInstances;
    }

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
            XMLInputSource xmlInputSource = new XMLInputSource(crPath);
            ResourceSpecifier specifier = 
                UIMAFramework.getXMLParser().parseResourceSpecifier(
                        xmlInputSource);
            cr = UIMAFramework.produceCollectionReader(specifier);
            cr.setConfigParameterValue("InputDirectory", inputDir);
            cr.setConfigParameterValue("IncludeSubdirectories", true);
            cr.reconfigure();
            count = System.currentTimeMillis() - count;
            System.out.println("\nfinished in " + (count/1000.0) + "s\n");
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return cr;
    }
    
    /**
     * Creates and returns the analysis engine that refers to the specified
     * descriptor file path
     * 
     * @param aePath The path of descriptor file the analysis engine
     * @return The analysis engine
     */
    private AnalysisEngine createAnalysisEngine(String aePath){
        System.out.print("Initializing \"" + aePath + "\"...");
        long count = System.currentTimeMillis();
        AnalysisEngine ae = null;
        try{
            XMLInputSource xmlInputSource = new XMLInputSource(aePath);
            ResourceSpecifier specifier = 
                UIMAFramework.getXMLParser().parseResourceSpecifier(
                        xmlInputSource);
            ae = UIMAFramework.produceAnalysisEngine(specifier);
            ae.reconfigure();
            count = System.currentTimeMillis() - count;
            System.out.println("\nfinished in " + (count/1000.0) + "s\n");
        } catch (Exception ex){
            System.out.println("\nfailed\n");
            ex.printStackTrace();
        }
        return ae;
    }
    
    /**
     * Returns file name of CAS object
     * 
     * @param jcas The JCas object
     * @return 
     */
    public String getFileName(JCas jcas) {
    	System.out.println();
    	SourceDocumentInformation sdi = (SourceDocumentInformation)
					jcas.getAnnotationIndex(SourceDocumentInformation.type)
					.iterator().next();
		String inputFileName = new File(sdi.getUri()).getName().split("\\.")[0];
		
		return inputFileName;
    }

	
	/** Main method to start evaluation.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		EvaluationMetrics metrics = new EvaluationMetrics();
		metrics.calculatePrecission();
	}

}
