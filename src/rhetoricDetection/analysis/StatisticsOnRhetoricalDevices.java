/**
 * 
 */
package rhetoricDetection.analysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Type;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.XMLInputSource;

import de.aitools.ie.uima.type.core.Paragraph;
import de.aitools.ie.uima.type.core.SourceDocumentInformation;
import rhetoricDetection.PlainText2XMIConverter;
import type.rhetoricDetection.RhetoricalDevice;

/**
 * This class determines the frequent patterns of rhetorical devices in 
 * the xmi corpus which contains the detected devices. 
 * 
 * It calculates metrics like individual frequency of RD, 
 * distribution in the first and last paragraph, combinations of RD by 2/3/4, 
 * average occurrence per sentence, and category distribution (i.e., balance, omission, repetition and custom schemes).
 * 
 * @author viorel.morari
 *
 */
public class StatisticsOnRhetoricalDevices {
	
	// -------------------------------------------------------------------------
	// PARAMETERS
	// -------------------------------------------------------------------------
	
	/**
	 * The path of root directory of the files to be analyzed.
	 */
	private static final String INPUT_COLLECTION_DIR = 
			"results/debates-xmi/";
	
	/**
	 * The path of the file to store the results in.
	 */
	private static final String RESULTS = 
			"/home/science-wordCount-stats.txt";

	/**
	 * The path of the XMI file of the collection reader to be used to iterate
	 * over all files to be analyzed.
	 */
	private static final String COLLECTION_READER_PATH = 
			"../aitools4-ie-uima/conf/uima-descriptors/collection-readers/" +
			"UIMAAnnotationFileReader.xml";
//			PlainText2XMIConverter.class.getResource("/uima-descriptors/collection-readers/"
//			+ "UIMAAnnotationFileReader.xml").toString();
	
	/**
	 * The path of the XMI file of the analysis engine to be used to analyze
	 * the files.
	 */
	private static final String ANALYSIS_ENGINE_PATH = 
			"conf/uima-descriptors/primitive-AEs/"
			+ "DummyStatisticsAE.xml";
//			PlainText2XMIConverter.class.getResource("/uima-descriptors/primitive-AEs/"
//			+ "DummyStatisticsAE.xml").toString();
	
    /**
     * The maximum number of files to process
     */
    private long maxFilesToProcess = Long.MAX_VALUE;
		
	/**
	 * Map to store the combinations of RD(rhetorical devices) by 2
	 */
	private Map<String, Integer> combinations2 = new HashMap<String, Integer>();
	
	/**
	 * Map to store the combinations of RD by 3
	 */
	private Map<String, Integer> combinations3 = new HashMap<String, Integer>();
	
	/**
	 * Map to store the combinations of RD by 4
	 */
	private Map<String, Integer> combinations4 = new HashMap<String, Integer>();
	
	/**
	 * Total number of RD detected
	 */
	private static int total = 0;
	
	/**
	 * Average of RD per sentence
	 */
	private static double avgPerSentence = 0;

	/**
	 * Main method
	 * 
	 * @param args
	 */
	public static void main(String [] args) {
		
		ArrayList<Map<String, ?>> results = new StatisticsOnRhetoricalDevices().countRhetoricalDevices(args[0]);
	}
	
	/**
	 * Main method for getting various statistics on Rhetorical Devices
	 * in the input corpus of preprocessed files.
	 * 
	 * 
	 * @param inputPath
	 * 					- input folder which contains the xmi with RD annotations
	 * @return Saves the list of arrays consisting of obtained results, 
	 * 		   i.e. total count, individual count, count in the first/last paragraph   
	 */
	private ArrayList<Map<String, ?>> countRhetoricalDevices(String inputPath){
		
		ArrayList<Map<String, ?>> statsResults = new ArrayList<Map<String, ?>>();
		
//		File dir = new File(INPUT_COLLECTION_DIR);
//		File[] inputFolders = dir.listFiles();
//		for (File folder : inputFolders) {
//			System.out.println("Current folder..."+ folder.getName());
//			total = 0;
//			avgPerSentence = 0;

//			statsResults.clear();
//			combinations2.clear();
//			combinations3.clear();
//			combinations4.clear();
			int sentenceCount = 0;
			int totalSentenceCount = 0;
			List<Annotation> paragraphs = new ArrayList<Annotation>();
			List<Annotation> sentences = new ArrayList<Annotation>();
			List<Annotation> rhetoricAnnotations = new ArrayList<Annotation>();
			List<RhetoricalDevice> rhetoricalDevices = new ArrayList<RhetoricalDevice>();
			Map<String, Integer> individualCount = new HashMap<String, Integer>();
			Map<String, Integer> firstParagraphCount = new HashMap<String, Integer>();
			Map<String, Integer> lastParagraphCount = new HashMap<String, Integer>();
			Map<String, Integer> categoriesDistribution = new HashMap<String, Integer>();
			Map<String, Integer> combinationsByTwo = new HashMap<String, Integer>();
			Map<String, Integer> combinationsByThree = new HashMap<String, Integer>();
			Map<String, Integer> combinationsByFour = new HashMap<String, Integer>();
			Map<String, Double> normalizedIndividualCount = new HashMap<String, Double>();
			Map<String, Integer> sortedMap2 = new HashMap<String, Integer>();
			Map<String, Integer> sortedMap3 = new HashMap<String, Integer>();
			Map<String, Integer> sortedMap4 = new HashMap<String, Integer>();
			// Initialize UIMA engines for German texts
			CollectionReader cr = this.createCollectionReader(
					COLLECTION_READER_PATH, inputPath);
			AnalysisEngine ae = this.createAnalysisEngine(ANALYSIS_ENGINE_PATH);
	        // Start processing
	        int processed = 0;
	        try{
	            // Iterate over all texts
	            CAS aCAS = ae.newCAS();
	            while (cr.hasNext() && processed<maxFilesToProcess){                  
	                // Get and preprocess current text
	                cr.getNext(aCAS);
	                JCas jcas = aCAS.getJCas();
	                printFileName(jcas);
	                rhetoricalDevices.clear();
	                //get the paragraphs in the current document
	                paragraphs = getClassAnnotations(jcas, 
							"de.aitools.ie.uima.type.core.Paragraph");
	                //get the sentences in the current document
	                sentences = getClassAnnotations(jcas, 
							"de.aitools.ie.uima.type.core.Sentence");
	                //get the number of sentences in the current document
	                sentenceCount = sentences.size();
	                totalSentenceCount+=sentenceCount;
	                System.out.println("Sentences: "+sentenceCount);
	                
	                //get the rhetorical devices annotations in the current document
	                rhetoricAnnotations = getClassAnnotations(jcas, 
							"type.rhetoricDetection.RhetoricalDevice");
	                
	                //convert Annotation type to RhetoricalDevices type
	                for (Annotation device : rhetoricAnnotations) {
						rhetoricalDevices.add((RhetoricalDevice) device);
					}
	                //get the map containing individual count of each rhetorical device
	                individualCount = getDeviceCount2Map(rhetoricalDevices, individualCount);
	                //get the map containing number of occurrences in the first paragraph
	                firstParagraphCount = getStatsOnParagraph(paragraphs, rhetoricalDevices, firstParagraphCount, true);
	                //get the map containing number of occurrences in the last paragraph
	                lastParagraphCount = getStatsOnParagraph(paragraphs, rhetoricalDevices, lastParagraphCount, false);
	                //get the map containing the distribution per category
	                categoriesDistribution = getCategoryDistribution(rhetoricalDevices, categoriesDistribution);
	                //get the map containing the combinations of two rhetorical devices
	                combinationsByTwo = getStatsRhetCombOnSentence(sentences, rhetoricalDevices, combinations2, 2);
	                //get the map containing the combinations of three rhetorical devices
	                combinationsByThree = getStatsRhetCombOnSentence(sentences, rhetoricalDevices, combinations3, 3);
	                //get the map containing the combinations of four rhetorical devices
	                combinationsByFour = getStatsRhetCombOnSentence(sentences, rhetoricalDevices, combinations4, 4);
	                //normalize individual count of rhetorical devices by doc length
	                normalizedIndividualCount = normalizeIndividualCount(rhetoricalDevices, normalizedIndividualCount, sentenceCount, processed);
	                
	                processed++;
	            }
			 } catch (Exception ex){
		         System.out.println(processed);
		         ex.printStackTrace();
		     }
	        
	        
	        for (int value : individualCount.values()) {
				total+=value;
			}
	        for (double value : normalizedIndividualCount.values()) {
				avgPerSentence+=value;
			}
	        for (String key : normalizedIndividualCount.keySet()) {
	        	normalizedIndividualCount.put(key, normalizedIndividualCount.get(key)/processed);
			}
	        System.out.println("processsed "+processed);
	        System.out.println("Total sentences: "+totalSentenceCount);
	        avgPerSentence = avgPerSentence/processed;
	        
	        //sort the 'combinationsByTwo' hashmap by value descending
	        sortedMap2 = sortMapByValue(combinationsByTwo);
	        //sort the 'combinationsByTwo' hashmap by value descending
	        sortedMap3 = sortMapByValue(combinationsByThree);
	        //sort the 'combinationsByTwo' hashmap by value descending
	        sortedMap4 = sortMapByValue(combinationsByFour);
	        //gather resulting hasmaps to output hashmap
	        statsResults.add(individualCount);
	        statsResults.add(firstParagraphCount);
	        statsResults.add(lastParagraphCount);
	        statsResults.add(sortedMap2);
	        statsResults.add(sortedMap3);
	        statsResults.add(sortedMap4);
	        statsResults.add(normalizedIndividualCount);
	        statsResults.add(categoriesDistribution);
	        
	        
	        //printStats(statsResults);
//	        saveStats(statsResults, RESULTS);
//	        saveStats(statsResults, RESULTS+folder.getName()+".txt");
//		}
		

		return statsResults;
	}
	
	
	/**
     * Returns a map containing the amount of each rhetorical device
     * encountered in the corpus
     * 
     * @param jcas The JCas object
     * @param classType The target class type
     * @return The class annotations
     */
    private Map<String, Integer> getDeviceCount2Map(List<RhetoricalDevice> devices, Map<String, Integer> resultMap){
        
    	for (int i = 0; i < devices.size(); i++) {
        	if (resultMap.get(devices.get(i).getDeviceType())!=null) {
        		resultMap.put(devices.get(i).getDeviceType(), resultMap.get(devices.get(i).getDeviceType())+1);
    		}
        	else{
        		resultMap.put(devices.get(i).getDeviceType(), 1);
        	}
		}
        
        return resultMap;
    }
	
	/**
	 * Serves for getting statistics within 
	 * the first and last paragraph in the document
	 * 
	 * @param paragraphs
	 * 				- The list with document paragraphs
	 * @param deviceList
	 * 				- The list of rhetorical devices in the document
	 * @param firstP
	 * 				- Flag for selecting between obtaining the stats 
	 * 				  on the first or the last paragraph
	 * 				  - true - first paragraph
	 * 				  - false - last paragraph
	 * @return resultMap
	 * 				- map to which paragraph distribution stats have been appended
	 */
	private Map<String, Integer> getStatsOnParagraph(List<Annotation> paragraphs, 
			List<RhetoricalDevice> deviceList, Map<String, Integer> resultMap, boolean firstP){
		
		//first paragraph
		Annotation firstParagraph = null;
		//last paragraph
		Annotation lastParagraph = null;
		try {
			firstParagraph = paragraphs.get(0);
			lastParagraph = paragraphs.get(paragraphs.size()-1);
		} catch (IndexOutOfBoundsException e) {
			// TODO Auto-generated catch block
			System.out.println("No paragraph annotations");
			return resultMap;
			//e.printStackTrace();
		}
		//get begin and end of the paragraphs
		int startOfFirstParagraph = firstParagraph.getBegin();
		int endOfFirstParagraph = firstParagraph.getEnd();
		int startOfLastParagraph = lastParagraph.getBegin();
		int endOfLastParagraph = lastParagraph.getEnd();
		
		for (RhetoricalDevice device : deviceList) {
			//check device boundaries
			if ((device.getBegin()>startOfFirstParagraph &&
					device.getEnd()<endOfFirstParagraph) && firstP) {

	        	if (resultMap.get(device.getDeviceType())!=null) {
	        		resultMap.put(device.getDeviceType(), resultMap.get(device.getDeviceType())+1);
	    		}
	        	else{
	        		resultMap.put(device.getDeviceType(), 1);
	        	}
			}
			else if((device.getBegin()>startOfLastParagraph &&
					device.getEnd()<endOfLastParagraph) && !firstP){
	        	if (resultMap.get(device.getDeviceType())!=null) {
	        		resultMap.put(device.getDeviceType(), resultMap.get(device.getDeviceType())+1);
	    		}
	        	else{
	        		resultMap.put(device.getDeviceType(), 1);
	        	}
			}
		}
		return resultMap;
	}
	
	
	/**
	 * Determines distribution of RD as combinations of 2/3/4 RD
	 * 
	 * @param sentences
	 * 				- The list with document sentences
	 * @param deviceList
	 * 				- The list of rhetorical devices in the document
	 * @param combinations
	 * 				- map to contain the found combinations of RD
	 * @param type
	 * 				- combination size (i.e., 2,3 or 4)			  
	 */
	private Map<String, Integer> getStatsRhetCombOnSentence(List<Annotation> sentences, 
			List<RhetoricalDevice> deviceList, Map<String, Integer> combinations, int type){
		
		int startOfSentence = 0;
		int endOfSentence = 0;
		RhetoricalDevice firstDevice;
		RhetoricalDevice secondDevice;
		RhetoricalDevice thirdDevice;
		RhetoricalDevice fourthDevice;
		//iterate over sentences
		for (Annotation sentence : sentences) {
			//get start and end of current sentence
			startOfSentence = sentence.getBegin();
			endOfSentence = sentence.getEnd();
			//iterate over RDs
			for (int i=0;i<deviceList.size();i++) {
				firstDevice = deviceList.get(i);
				if (firstDevice.getBegin()>=startOfSentence && 
						firstDevice.getEnd()<=endOfSentence) {
						String elem1 = firstDevice.getDeviceType();
					for (int j = i+1; j < deviceList.size(); j++) {
						secondDevice = deviceList.get(j);
						if (secondDevice.getBegin()>=startOfSentence && 
								secondDevice.getEnd()<=endOfSentence) {
							String elem2 = secondDevice.getDeviceType();
							//combinations by 2
							if (type==2) {
								String key = elem1+" - "+elem2;
								if (combinations.containsKey(key)) {
									combinations.put(key, combinations.get(key)+1);
								} else {
									combinations.put(key, 1);
								}
							}
							//get the third device in paragraph to be used in comb. by 4
							else if (type>=3) {
								for (int k = j+1; k < deviceList.size(); k++) {
									thirdDevice = deviceList.get(k);
									if (thirdDevice.getBegin()>=startOfSentence && 
											thirdDevice.getEnd()<=endOfSentence) {
										String elem3 = thirdDevice.getDeviceType();
									//combinations by 4
									if (type==4) {
										for (int l = k+1; l < deviceList.size(); l++) {
											fourthDevice = deviceList.get(l);
											if (fourthDevice.getBegin()>=startOfSentence && 
													fourthDevice.getEnd()<=endOfSentence) {
												String elem4 = fourthDevice.getDeviceType();
												
												String key = elem1+" - "+elem2+" - "+elem3+" - "+elem4;
												if (combinations.containsKey(key)) {
													combinations.put(key, combinations.get(key)+1);
												} else {
													combinations.put(key, 1);
												}
											}
										}
									//combinations by 3
									} else if (type==3){
										String key = elem1+" - "+elem2+" - "+elem3;
										if (combinations.containsKey(key)) {
											combinations.put(key, combinations.get(key)+1);
										} else {
											combinations.put(key, 1);
										}
									}
									}
								}
							}			
						}
					}
				}
			}
		}
		return combinations;
	}
	
	
	/**
     * Returns a map containing the amount of each rhetorical device
     * encountered in the corpus
     * 
     * @param devices 
     * 				- The list of rhetorical devices in the document
     * @param resultMap 
     * 				- map to which individual RD distribution stats will be appended
     * @param sentencesInDoc 
     * 				- number of sentences in document (for normalization)
     * @param docsInCorpus 
     * 				- number of documents in corpus (for normalization)
     * @return resultMap
     * 				- map to which individual RD distribution stats have been appended
     */
    private Map<String, Double> normalizeIndividualCount(List<RhetoricalDevice> devices,
    		Map<String, Double> resultMap, int sentencesInDoc, int docsInCorpus){
        
    	Map<String, Integer> individualDeviceCount = new HashMap<String, Integer>();
    	
    	individualDeviceCount = getDeviceCount2Map(devices, individualDeviceCount);
        //double normValue;
    	//individualDeviceCount.forEach((k,v)-> System.out.println("individual: "+k+", "+v));
    	individualDeviceCount.forEach((k,v)->{
    		
    		if (!resultMap.containsKey(k)) {
    			double normValue = (double)v/sentencesInDoc;
    			resultMap.put(k, normValue);
    		}
    		else{
    			double normValue = resultMap.get(k) + ((double)v/sentencesInDoc);
    			resultMap.put(k, normValue);
    		}
    	});
    	
        return resultMap;
    }

    
	/**
     * Returns a map containing the distribution of rhetorical devices per category
     * Categories: 
     * 				1.) Schemes of balance - 5
     * 				2.) Schemes of omission - 4
     * 				3.) Schemes of repetition - 8
     * 				4.) Custom schemes - 10
     * 
     * @param devices 
     * 				- The list of rhetorical devices in the document
     * @param resultMap 
     * 				- map to which category distribution of RDs will be appended
     * @return resultMap 
     * 				- map to which category distribution of RDs have been appended
     */
    private Map<String, Integer> getCategoryDistribution(List<RhetoricalDevice> devices,
    		Map<String, Integer> resultMap){
        
    	for (int i = 0; i < devices.size(); i++) {
        	if (resultMap.get(devices.get(i).getCategory())!=null) {
        		resultMap.put(devices.get(i).getCategory(), resultMap.get(devices.get(i).getCategory())+1);
    		}
        	else{
        		resultMap.put(devices.get(i).getCategory(), 1);
        	}
		}
        return resultMap;
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
     * Prints the resultMap with stats in console
     * 
     * @param statsResults
     * 					- map containing all the distribution stats of RDs
     */
    public void printStats(ArrayList<Map<String, ?>> statsResults) {
    	System.out.println();
    	
    	System.out.format("+--------------------+----------------+-------------+%n");
		System.out.format("|       Name         | All Occurences |   Coverage  |%n");
		System.out.format("+--------------------+----------------+-------------+%n");
		
		statsResults.get(0).forEach((k,v)-> System.out.format("| %-18s |%15d |%12.2f |%n", 
				k, v, ((double)((Integer)v).intValue()*100)/total));
		
		
		System.out.format("+--------------------+----------------+-------------+%n");
		System.out.format("| Total number %22d | %11s |%n", total, "100%");
		System.out.format("+--------------------+----------------+-------------+%n");
		
		System.out.format("+--------------------+----------------+%n");
		System.out.format("|       Name         | First Paragraph|%n");
		System.out.format("+--------------------+----------------+%n");
		
		statsResults.get(1).forEach((k,v)-> System.out.format("| %-18s |%15d |%n", k, v));
		
		System.out.format("+--------------------+----------------+%n");
		
		System.out.format("+--------------------+----------------+%n");
		System.out.format("|       Name         | Last Paragraph |%n");
		System.out.format("+--------------------+----------------+%n");
		
		statsResults.get(2).forEach((k,v)-> System.out.format("| %-18s |%15d |%n", k, v));
		
		System.out.format("+--------------------+----------------+%n");
		
		System.out.format("+----------------------------------------+----------------+%n");
		System.out.format("|            Combinations                | All Occurences |%n");
		System.out.format("+----------------------------------------+----------------+%n");
		
		statsResults.get(3).forEach((k,v)-> {
			if ((Integer)v>4) {
				System.out.format("| %-38s |%15d |%n", k, v);
		}});
		
		System.out.format("+----------------------------------------+----------------+%n");
		
		System.out.format("+-----------------------------------------------------+----------------+%n");
		System.out.format("|                      Combinations                   | All Occurences |%n");
		System.out.format("+-----------------------------------------------------+----------------+%n");
		
		statsResults.get(4).forEach((k,v)-> {
			if ((Integer)v>4) {
				System.out.format("| %-51s |%15d |%n", k, v);
		}});
		
		System.out.format("+-----------------------------------------------------+----------------+%n");
		
		System.out.format("+---------------------------------------------------------------------------+----------------+%n");
		System.out.format("|                               Combinations                                | All Occurences |%n");
		System.out.format("+---------------------------------------------------------------------------+----------------+%n");
		
		statsResults.get(5).forEach((k,v)-> {
			if ((Integer)v>4) {
				System.out.format("| %-73s |%15d |%n", k, v);
		}});
		
		System.out.format("+---------------------------------------------------------------------------+----------------+%n");
		
		System.out.format("+--------------------+---------------------+%n");
		System.out.format("|      Name          |  Avg. per sentence  |%n");
		System.out.format("+--------------------+---------------------+%n");
		
		statsResults.get(6).forEach((k,v)-> System.out.format("| %-18s |%20.3f |%n", k, v));
		
		System.out.format("+--------------------+---------------------+%n");
		System.out.format("| Total                             %6.2f |%n", avgPerSentence);
		System.out.format("+--------------------+---------------------+%n");
		
		System.out.format("+--------------------+----------------+%n");
		System.out.format("|      Category      | All Occurences |%n");
		System.out.format("+--------------------+----------------+%n");
		
		statsResults.get(7).forEach((k,v)-> System.out.format("| %-18s |%15d |%n", k, v));
		
		System.out.format("+--------------------+----------------+%n");

    }
    
	/**
	 * Saves the stats to an external file
	 * 
	 * @param stats
	 * 				- map containing the stats
	 * @param criterion
	 * 				- type of stats (i.e. taxonomicClassifiers, typesOfMaterial)
	 * @param outFile 
	 * 				- path to the output file
	 */
	private void saveStats(ArrayList<Map<String, ?>> statsResults, String outFile) {
		
		PrintWriter pw = null;
		
        try {
			pw = new PrintWriter(new File(outFile));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        StringBuilder sb = new StringBuilder();
        
        sb.append("individual count");
        sb.append('\n');
        sb.append("Name");
        sb.append('$');
        sb.append("All Occurences");
        sb.append('$');
        sb.append("Coverage");
        sb.append('\n');
        
        statsResults.get(0).forEach((k,v)-> {
			//if (k instanceof String) {
				sb.append(k);
				sb.append('$');
				sb.append(v);
				sb.append('$');
				sb.append(Math.round(((double)((Integer)v).intValue()*100)/total * 100d)/100d);
				sb.append('\n');
        });
        
        sb.append("Total");
        sb.append('$');
        sb.append('$');
        sb.append(total);
        
        sb.append('\n');
        sb.append('\n');
        sb.append("occurences in the first paragraph");
        sb.append('\n');
        sb.append("Name");
        sb.append('$');
        sb.append("First Paragraph");
        sb.append('\n');
        
        statsResults.get(1).forEach((k,v)-> {
			//if (k instanceof String) {
				sb.append(k);
				sb.append('$');
				sb.append(v);
				sb.append('\n');
        });
        
        sb.append('\n');
        sb.append("occurences in the last paragraph");
        sb.append('\n');
        sb.append("Name");
        sb.append('$');
        sb.append("Last Paragraph");
        sb.append('\n');
        
        statsResults.get(2).forEach((k,v)-> {
			//if (k instanceof String) {
				sb.append(k);
				sb.append('$');
				sb.append(v);
				sb.append('\n');
        });
        
        sb.append('\n');
        sb.append("combinations of 2");
        sb.append('\n');
        sb.append("Combinations");
        sb.append('$');
        sb.append("All Occurences");
        sb.append('\n');
        
        statsResults.get(3).forEach((k,v)-> {
			if ((Integer)v>4) {
				sb.append(k);
				sb.append('$');
				sb.append(v);
				sb.append('\n');
        	}
        });
        
        sb.append('\n');
        sb.append("combinations of 3");
        sb.append('\n');
        sb.append("Combinations");
        sb.append('$');
        sb.append("All Occurences");
        sb.append('\n');
        
        statsResults.get(4).forEach((k,v)-> {
			if ((Integer)v>4) {
				sb.append(k);
				sb.append('$');
				sb.append(v);
				sb.append('\n');
        	}
        });
        
        sb.append('\n');
        sb.append("combinations of 4");
        sb.append('\n');
        sb.append("Combinations");
        sb.append('$');
        sb.append("All Occurences");
        sb.append('\n');
        
        statsResults.get(5).forEach((k,v)-> {
			if ((Integer)v>4) {
				sb.append(k);
				sb.append('$');
				sb.append(v);
				sb.append('\n');
        	}
        });
        
        sb.append('\n');
        sb.append("average number of devices per sentence");
        sb.append('\n');
        sb.append("Name");
        sb.append('$');
        sb.append("Avg. per Sentence");
        sb.append('\n');
        
        statsResults.get(6).forEach((k,v)-> {
				sb.append(k);
				sb.append('$');
				sb.append(Math.round((double)v * 1000d)/1000d);
				sb.append('\n');
        });
        
        sb.append("Total");
        sb.append('$');
        sb.append(Math.round(avgPerSentence * 100d)/100d);
        
        sb.append('\n');
        sb.append('\n');
        sb.append("distribution by category of rhetoric");
        sb.append('\n');
        sb.append("Category");
        sb.append('$');
        sb.append("All Occurences");
        sb.append('\n');
        
        statsResults.get(7).forEach((k,v)-> {
				sb.append(k);
				sb.append('$');
				sb.append(v);
				sb.append('\n');
        });
		
		
		pw.write(sb.toString());
        pw.close();
	}    
    
	// -------------------------------------------------------------------------
	// HELPER
	// -------------------------------------------------------------------------
	
    
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
     * Prints file name of CAS object
     * 
     * @param jcas The JCas object
     */
    public void printFileName(JCas jcas) {
    	System.out.println();
    	SourceDocumentInformation sdi = (SourceDocumentInformation)
					jcas.getAnnotationIndex(SourceDocumentInformation.type)
					.iterator().next();
		String inputFileName = new File(sdi.getUri()).getName();
		System.out.print(inputFileName);
    }
	
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
     * Sort map by value descending
     * 
     * @param map2Sort
     * 				- map to be sorted
     * @return sortedMap
     * 				- sorted map
     */
    private Map<String, Integer> sortMapByValue(Map<String, Integer> map2Sort){
    	
    	Comparator<Entry<String, Integer>> byValue = (entry1, entry2) -> entry1.getValue().compareTo(
                entry2.getValue());
        Map<String, Integer> sortedMap = 
        		map2Sort.entrySet().stream()
        	    .sorted(byValue.reversed())
        	    .collect(Collectors.toMap(Entry::getKey, Entry::getValue,
        	                              (e1, e2) -> e1, LinkedHashMap::new));
        return sortedMap;
    }
    
    
}
