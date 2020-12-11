package rhetoricDetection.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.XMLInputSource;

import de.aitools.ie.uima.feature.flow.internal.Flow;
import de.aitools.ie.uima.feature.flow.internal.FlowCreator;
import de.aitools.ie.uima.feature.flow.internal.FlowMetrics;
import de.aitools.ie.uima.feature.flow.internal.FlowTransformer;

/**
 * This class processes a given corpus in order to find common flows of certain
 * unit classes like (local sentiment or discourse relations). It computes their
 * cooccurrence with certain classes of the respective texts. Several metrics
 * are computed on this basis and are then printed to screen. Especially, a 
 * number of recall and precision values are output. For a description on how
 * they are computed, see Wachsmuth et. al. (2014): "A Review Corpus for 
 * Argumentation Analysis".
 * <BR><BR>
 * The class contains a number of parameters that specify how to compute the
 * flows and from what input data.
 * 
 * @author henning.wachsmuth
 *
 */
public class FlowCounter {

	// -------------------------------------------------------------------------
	// INPUT PARAMETERS
	// -------------------------------------------------------------------------

	/**
	 * The relative path of the directory of the corpus to be processed.
	 */
	public static final String CORPUS_PATH = 
//		"data/corpora/ArgumentAnnotatedEssays-v2/xmi/train/"; 
		"/home/genre-word-xmi/editorial-wordCount-subsample";		

	/**
	 * The maximum number of texts to be processed from the specified corpus.
	 */
	private static final long MAX_TEXTS = 
		Long.MAX_VALUE;
	
	/**
	 * The relative path of the collection reader to be used to process the
	 * corpus.
	 */
	private static final String CR_PATH = 
			"../aitools4-ie-uima/conf/uima-descriptors/collection-readers/" +
			"UIMAAnnotationFileReader.xml";

	/**
	 * The relative path of the analysis engine to be used to preprocess the
	 * corpus. 
	 */
	private static final String AE_PATH = 
			"conf/uima-descriptors/primitive-AEs/"
			+ "DummyStatisticsAE.xml";

	
	// -------------------------------------------------------------------------
	// FLOW PARAMETERS
	// -------------------------------------------------------------------------
	
	/**
	 * The type of the annotation that contains the text to compute the flow
	 * from. If the text is computed from the text in a JCas object, this string
	 * needs to be the empty string
	 */
	private static String FLOW_LEVEL = 
		"";											// Text level
//		"de.aitools.ie.uima.type.core.Paragraph";	// Paragraph level
//		"de.aitools.ie.uima.type.core.Sentence";	// Sentence level
	
	/**
	 * The transformations applied to the flows.
	 */
	private static final String FLOW_TRANSFORMATIONS = 
//			"";							
			"change";							
//			"lessclasses";
	
	/**
	 * The parameters of the considered flow transformations
	 */
	private static final String FLOW_TRANSFORMATION_PARAMS = 
		"";
//		"";		
//		"none";
	
	
	
	// -------------------------------------------------------------------------
	// UNIT CLASS PARAMETERS
	// -------------------------------------------------------------------------
	
	/**
	 * The parent type of the annotations that represent the unit classes to 
	 * find patterns for. If there is only type (without child types), the
	 * child types array should be left empty.
	 */
	private static String PARENT_UNIT_CLASS_TYPE = 
		"type.rhetoricDetection.RhetoricalDevice";
//		"de.aitools.ie.uima.type.arguana.Statement";	
	
	/**
	 * The feature of the type of the annotations that represent the parent
	 * unit classes. Use empty string if the annotation itself respresents the 
	 * class.
	 * 
	 */
	public static String PARENT_UNIT_CLASS_FEATURE = 
		"deviceType";
//		"";
	
	/**
	 * What unit classes to consider and how to name them. Given in the form:
	 * original-unit-class-name:mapped-unit-class-name.
	 * 
	 * If a unit class shall be ignored, there should be no entry for that class
	 * in this mapping.
	 */
	public static final String UNIT_CLASS_MAPPING = 
		"IfCondZero:IFC0, IfCondOne:IFC1, IfCondTwo:IFC2, IfCondThree:IFC3,"
		+ "IfCounterfactual:COUNTERF, UnlessConditional:UNLESS,"
		+ "WhetherConditional:WHETHER, TripleEnumeration:ENUM, Isocolon:ISOC,"
		+ "Asyndeton:ASYN, Polysyndeton:POLYS, Diacope:DIACOPE, Epanalepsis:EPANA,"
		+ "Epizeugma:EPIZG, Hypozeugma:HYPOZG, Anadiplosis:ANADI, Epiphoza:EPIPHZ,"
		+ "Epizeuxis:EPIZX, Mesarchia:MESAR, Mesodiplosis:MESO, Pysma:PYSMA,"
		+ "comparativeAdj:COMPADJ, comparativeAdv:COMPADV, superlativeAdj:SUPERADJ,"
		+ "superlativeAdv:SUPERADV, PassiveVoice:PV";
//		"anecdote:AN,assumption:AS,common-ground:CO,other:OT,statistics:ST,testimony:TE"; 	// all ADU types
//		"anecdote:AN,common-ground:CO,statistics:ST,testimony:TE"; 	// only ADU evidence types
//		"positive:pos,negative:neg,Fact:neu";	// Opinions and facts
	
	/**
	 * The child types of the annotations that represent the unit classes to 
	 * find patterns for. If there is only a parent type, this array should be 
	 * left empty.
	 */
	private static String CHILD_UNIT_CLASS_TYPES = 
		""; // No relevant child annotation type
//		"de.aitools.ie.uima.type.arguana.Opinion,de.aitools.ie.uima.type.arguana.Fact";
	
	/**
	 * The feature of each child types of the annotations that represent the 
	 * unit classes to find patterns for. There should be one feature for each
	 * child type in the ordering of the child types.  Use empty string if a 
	 * type itself respresents the class.
	 */
	private static String CHILD_UNIT_CLASS_FEATURES = 
		""; // No relevant child annotation feature
//		"polarity,";

	
	// -------------------------------------------------------------------------
	// TEXT CLASS PARAMETERS
	// -------------------------------------------------------------------------
	
	/**
	 * The type of annotation that contains the text class to compute
	 * correlations with the found patterns for.
	 */
	private static final String TEXT_CLASS_TYPE = 
		"uima.tcas.DocumentAnnotation";	// Default type
//		"de.aitools.ie.uima.type.arguana.Sentiment";

	/**
	 * The feature of the type of the annotation that contains the text class to 
	 * compute correlations with the found patterns for.
	 */
	private static final String TEXT_CLASS_FEATURE = 
		""; // Default feature
//		"score";

	/**
	 * What text classes to consider and how to name them. Given in the form:
	 * original-text-class-name:mapped-unit-class-name.
	 * 
	 * Every original text class should have an entry here. If two or more
	 * original classes should be mapped to the same class, the entries should
	 * have the same right side.
	 */
	public static final String TEXT_CLASS_MAPPING =
		"DocumentAnnotation:text"; // Default mapping
//		"1.0:1.0,2.0:2.0,3.0:3.0,4.0:4.0,5.0:5.0,-1.0:UNK";

	
	
	// -------------------------------------------------------------------------
	// METRICS PARAMETERS
	// -------------------------------------------------------------------------

	/**
	 * The minimum recall of a flow pattern that is called a high recall
	 * pattern. The recall of a pattern is the fraction of instances of the
	 * pattern under all instances.
	 */
	public static double RECALL_THRESHOLD = 0.001;
	
	/**
	 * Whether to balance the computed metrics, such that each text class is
	 * represented equally. In case of balanced corpora, this does not change
	 * anything.
	 */
	public static boolean BALANCE_METRICS = 
			false;
//			true;
	
	
	
	
	// -------------------------------------------------------------------------
	// STORAGE AND REFERENCES
	// -------------------------------------------------------------------------
	
	/**
	 * The analysis engine to be used
	 */
	private AnalysisEngine ae;
	
	/**
	 * The collection reader to be used
	 */
	private CollectionReader cr;
	
	/**
	 * The flow creator
	 */
	private FlowCreator flowCreator;
	
	/**
	 * The flow transformer used to transform the original flow into the
	 * version to be used.
	 */
	private FlowTransformer flowTransformer;
	
	/**
	 * The mapping from original text class names to the text class names to be 
	 * used
	 */
	private Map<String, String> textClassMapping;

	
	
	// -------------------------------------------------------------------------
	// INITIALIZATIONS
	// -------------------------------------------------------------------------
	
	/**
	 * Initializes counters, UIMA engines and the like.
	 */
	public FlowCounter(){		
		this.ae = this.createAnalysisEngine(AE_PATH);
		this.cr = this.createCollectionReader(CR_PATH, CORPUS_PATH);
		this.flowCreator = new FlowCreator(PARENT_UNIT_CLASS_TYPE, 
				PARENT_UNIT_CLASS_FEATURE, CHILD_UNIT_CLASS_TYPES, 
				CHILD_UNIT_CLASS_FEATURES, UNIT_CLASS_MAPPING, 
				TEXT_CLASS_TYPE, TEXT_CLASS_FEATURE,
				TEXT_CLASS_MAPPING);
		this.flowTransformer = new FlowTransformer(
				FLOW_TRANSFORMATIONS, FLOW_TRANSFORMATION_PARAMS);
		this.textClassMapping = new HashMap<String, String>();
		for (String map : TEXT_CLASS_MAPPING.split(",")) {
			String [] values = map.split(":");
			textClassMapping.put(values[0], values[1]);
		}
	}
	
	/**
	 * Returns the collection reader in the file with the given path to be used 
	 * to iterate over the input directory with the given path. 
	 * 
	 * @param crPath The path of the collection reader
	 * @param inputDir The path of the input directory
	 * @return A preinitialized collection reader.
	 */
	private CollectionReader createCollectionReader(String crPath, 
			String inputDir){
		System.out.print("\nInitializing \"" + crPath + "\"...");
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
			System.out.println("\nfinished in " + (count/1000.0) + "s");
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
		System.out.print("\nInitializing \"" + aePath + "\"...");
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
			System.out.println("\nfinished in " + (count/1000.0) + "s");
		} catch (Exception ex){
			System.out.println("\nfailed");
			ex.printStackTrace();
		}
		return ae;
	}
	
	// -------------------------------------------------------------------------
	// MAIN FLOW METRICS COMPUTATION METHOD
	// -------------------------------------------------------------------------
	
	/**
	 * Computes all different flows in the given corpus (specified
	 * via a global parameter) together with their metrics.
	 * 
	 * The unit class type of the flows as well as the way flows are constructed
	 * are defined via globally defined parameters.
	 * 
	 * Also, the text class associated to each flow is set via a respective
	 * parameter.
	 * 
	 */
	public void computeFlowMetrics(){
		// First determine all flows and their associated text classes
		List<Flow> flows = this.determineAllFlows();
		List<String> textClasses = this.determineAllTextClasses();
		
		// Aggregate the flows and their class distributions
		Map<Flow, Map<String, Long>> aggregatedFlows =
			this.countFlowsPerTextClass(flows, textClasses);
		long totalNumberOfFlows = flows.size();
		if (BALANCE_METRICS){
			totalNumberOfFlows = 
				this.balanceClassDistribution(textClasses, aggregatedFlows);	
		}
		
		// Compute metrics for the aggregated flows
		List<FlowMetrics> flowsWithMetrics = 
			this.computeMetrics(totalNumberOfFlows, aggregatedFlows);
		flowsWithMetrics = this.orderByRecall(flowsWithMetrics);
		
		// Print
		System.out.println("\n\n" + FLOW_TRANSFORMATIONS.toUpperCase() + 
				" FLOWS");
		this.printOverallFlowMetrics(flowsWithMetrics, textClasses, 
				"Overall metrics of " + CORPUS_PATH);
		this.printFrequentFlows(flowsWithMetrics, 
				FLOW_TRANSFORMATIONS + " flows with R>=" + RECALL_THRESHOLD + 
				" in " + CORPUS_PATH);
	}

	
	
	// -------------------------------------------------------------------------
	// FLOW DETERMINATION
	// -------------------------------------------------------------------------
	
	/**
	 * Iterates over all texts of the global collection reader in order to 
	 * compute a flow for each text. 
	 * @return The determined flows
	 */
	private List<Flow> determineAllFlows(){
		List<Flow> flows = new ArrayList<Flow>();
		int index = 0;
		try {			
			System.out.print("\nDetermining " + FLOW_TRANSFORMATIONS + 
					" flows...");
			// Iterate over all input documents
	  		CAS aCAS = ae.newCAS();	
	  		JCas jcas;
			while (cr.hasNext() & index < MAX_TEXTS){
				// Get and process current text
				cr.getNext(aCAS);
				jcas = aCAS.getJCas();
				ae.process(jcas);
				// Determine and add flow of text
				flows.addAll(this.determineFlows(jcas));
				// Count
				index++;
				if (index % 100 == 0)
					System.out.print(".");
				if (index % 1000 == 0)
					System.out.print(index);
			}
			System.out.println("finished\n");
		} catch (Exception e) {
			System.out.println("failed at " + index);
			e.printStackTrace();
		}
		return flows;
	}


	/**
	 * Determines and returns the flows for the text in the given 
	 * JCas object. 
	 * 
	 * @param jcas The JCas object
	 * @return The sentiment flow
	 */
	private List<Flow> determineFlows(JCas jcas){
		List<Flow> flows = new ArrayList<Flow>();
		// Determine part of the text to compute a flow for
		if ("".equals(FLOW_LEVEL)){
			Flow flow = this.flowCreator.createFlow(jcas, new Annotation(jcas, 
					0, jcas.getDocumentText().length()));
			flow = this.flowTransformer.transformFlow(flow);
			flows.add(flow);
		}
		else{
			Type type = jcas.getTypeSystem().getType(FLOW_LEVEL);
			Type textClassType = jcas.getTypeSystem().getType(TEXT_CLASS_TYPE);
			FSIterator<Annotation> textClassTypeIter = 
					jcas.getAnnotationIndex(textClassType).iterator();
			if (!textClassTypeIter.hasNext()){
				return flows;
			}
			FSIterator<Annotation> textTypeIter = jcas.getAnnotationIndex(
					type).subiterator(textClassTypeIter.next());
			while (textTypeIter.hasNext()){
				Annotation textAnno = textTypeIter.next();
				Flow flow = this.flowCreator.createFlow(jcas, textAnno);
				flow = this.flowTransformer.transformFlow(flow);
				flows.add(flow);
			} 
			if (flows.size() == 0){
				flows.add(this.flowCreator.createFlow(jcas, 
						new Annotation(jcas, 0, 0)));
			}
		}
		return flows;
	}
	
	/**
	 * Iterates over all texts of the global collection reader in order to 
	 * determine the class of each text, stored in the feature with
	 * the given feature name of the annotiations of the type with the given 
	 * type name.
	 * 
	 * @param The type name
	 * @param The feature name
	 * @param useChangeFlow Whether to consider only flow changes
	 * @return The list of the scores of all texts
	 */
	private List<String> determineAllTextClasses(){
		List<String> textClasses = new ArrayList<String>();
		try {			
			System.out.print("\nDetermining text classes...");
			// Reset collection reader to beginning
			cr.reconfigure();
			// Iterate over all input documents
			int index = 0;
	  		CAS aCAS = ae.newCAS();	
	  		JCas jcas;
			while (cr.hasNext() & index < MAX_TEXTS){
				// Get and process current text
				cr.getNext(aCAS);
				jcas = aCAS.getJCas();
				ae.process(jcas);

				// Determine text class which is assumed to exist
				Type classType = jcas.getTypeSystem().getType(TEXT_CLASS_TYPE);
				FSIterator<Annotation> classIter = 
					jcas.getAnnotationIndex(classType).iterator();
				Annotation annotation = classIter.next();		
				String textClass = this.textClassMapping.get(
						this.getFeatureValue(annotation, 
								TEXT_CLASS_FEATURE));
				
				// Now add text class once for each used text fragment
				Type type;
				if (FLOW_LEVEL.equals("")){
					type = jcas.getTypeSystem().getType(
							"uima.tcas.DocumentAnnotation");
				} else type = jcas.getTypeSystem().getType(FLOW_LEVEL);
				FSIterator<Annotation> textTypeIter = 
					jcas.getAnnotationIndex(type).iterator();
				if (!textTypeIter.hasNext()){
					textClasses.add(textClass);
				}
				while (textTypeIter.hasNext()){
					textTypeIter.next();
					textClasses.add(textClass);
				} 


				// Count
				index++;
				if (index % 100 == 0)
					System.out.print(".");
				if (index % 1000 == 0)
					System.out.print(index);
			}
			ae.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return textClasses;
	}
	
	/**
	 * Returns the value of the feature with the given feature name of 
	 * given annotation name or null if not existing.
	 * 
	 * @param annotation The JCas object that specifies the type system
	 * @param featureName The feature name
	 * @return The feature
	 */
	private String getFeatureValue(Annotation annotation, String featureName){
		if ("".equals(featureName)){
			return annotation.getClass().getSimpleName();
		}
		List<Feature> features = annotation.getType().getFeatures();
		for (Feature feature : features) {
			if (feature.getShortName().equals(featureName)){
				return annotation.getFeatureValueAsString(feature);
			}
		}
		return null;
	}


	/**
	 * Balances the occurring flows, i.e., it changes the frequencies of 
	 * occuring flows such that each text class is represented with the same 
	 * frequency.
	 * Changing is done by multiplying the frequrencies. The resulting new total
	 * number of flows is finally returned, since it is used for computing
	 * relative recall and precision values later on.
	 * Balancing can be disabled via global parameter of this class.
	 * 
	 * @param textClasses The text classes
	 * @param occurringFlows The occuring flows
	 * @return The new total number of flows
	 */
	private long balanceClassDistribution(List<String> textClasses, 
			Map<Flow, Map<String, Long>> occurringFlows){
		// First determine class distribution
		Map<String, Integer> distribution = new HashMap<String, Integer>();
		for (String textClass : textClasses) {
			if (distribution.containsKey(textClass)){
				distribution.put(textClass, distribution.get(textClass)+1);
			} else{
				distribution.put(textClass, 1);
			}
		}
		// Next, compute balancing factor for each class
		Map<String, Long> factors = new HashMap<String, Long>();
		for (String factorClass : distribution.keySet()) {
			long factor = 1;
			for (String otherClass : distribution.keySet()) {
				if (!otherClass.equals(factorClass)){
					factor *= distribution.get(otherClass);
				}
			}
			factors.put(factorClass, factor);
		}
		// Then, multiply each frequency with the respective balancing factor
		for (Flow flow : occurringFlows.keySet()) {
			Map<String, Long> frequenciesPerClass = occurringFlows.get(flow);
			for (String textClass : factors.keySet()) {
				if (frequenciesPerClass.containsKey(textClass)){
					frequenciesPerClass.put(textClass, frequenciesPerClass.get(
									textClass)*factors.get(textClass));
				}
			}
		}
		// Finally, compute and return  balanced number of flows
		long balancedTotal = distribution.size();
		for (String textClass : distribution.keySet()) {
			balancedTotal *= distribution.get(textClass);
		}
		return balancedTotal;
	}
	
	
	
	// -------------------------------------------------------------------------
	// METRICS COMPUTATION
	// -------------------------------------------------------------------------
	
	/**
	 * Counts the occurrences of all flows, differentiating between the text 
	 * classes associated to the flows.
	 * 
	 * @param flows The flows
	 * @param textClasses The text classes
	 * @return The occurrences
	 */
	private Map<Flow, Map<String, Long>> countFlowsPerTextClass(
			List<Flow> flows, List<String> textClasses){
		// Counters for all text classes of a flow
		Map<Flow, Map<String, Long>> flowCounters = 
			new HashMap<Flow, Map<String, Long>>();
		
		// Look for pattern of each length up to given max length
		for (int i=0; i<flows.size(); i++) {
			Flow flow = flows.get(i);
			// Update counters of flows 
			if (flowCounters.containsKey(flow)){
				Map<String, Long> counters = flowCounters.get(flow);
				// Increase existing counter or add new counter
				if (counters.containsKey(textClasses.get(i))){
					counters.put(textClasses.get(i), 
							counters.get(textClasses.get(i))+1);
				}
				else counters.put(textClasses.get(i), 1L);
			}
			else{
				Map<String, Long> counters = new TreeMap<String, Long>();
				counters.put(textClasses.get(i), 1L);
				flowCounters.put(flow, counters);
			}
		}
		return flowCounters;
	}
	
	/**
	 * Derives metrics for all flows that are given with their counts for
	 * each text class. Uses the given total number of flows to compute the 
	 * recall of each pattern.
	 * 
	 * @param totalNumberOfFlows The total number of flows
	 * @param flowsWithCounts The patterns and their counters
	 * @return The metrics
	 */
	private List<FlowMetrics> computeMetrics(
			long totalNumberOfFlows, 
			Map<Flow, Map<String, Long>> flowsWithCounts){
		List<FlowMetrics> metrics = new ArrayList<FlowMetrics>();
		for (List<String> flow : flowsWithCounts.keySet()) {
			Map<String, Long> classCounts = flowsWithCounts.get(flow);
			// Compute recall value
			double occurrences = 0;
			for (long occurrence : classCounts.values()) {
				occurrences += occurrence;
			}
			double recall = occurrences / (double) totalNumberOfFlows;
			// Compute precision values
			TreeMap<String, Double> classToPrecision = 
				new TreeMap<String, Double>();
			for (String clazz : classCounts.keySet()) {
				double precision = 
					classCounts.get(clazz) / occurrences;
				classToPrecision.put(clazz, precision);
			}
			// Create and add pattern
			metrics.add(new FlowMetrics(flow, recall, 
					classToPrecision));
		}
		return metrics;
	}
	

	/**
	 * Computes and returns the number of different flows in the given ordered 
	 * list of flows (and their metrics) that have at least the specified 
	 * minimum recall.
	 * 
	 * @param orderedFlowMetrics The ordered flows with metrics
	 * @param minRecall The minimum recall
	 * @return The number of different flows
	 */
	private int computeFlowsWithMinRecall(
			List<FlowMetrics> orderedFlowMetrics, double minRecall){
		int flows = 0;
		for (FlowMetrics flowMetrics : orderedFlowMetrics) {
			if (flowMetrics.getRecall() < minRecall){
				break;
			}
			flows++;
		}
		return flows;
	}
	
	/**
	 * Computes and returns the number of different flows in the given 
	 * list of flows (and their metrics) that cooccur at least once with the
	 * given text class.
	 * 
	 * @param flowMetrics The flows with metrics
	 * @param textClass The text class
	 * @return The number of different flows
	 */
	private int computeFlowsWithTextClass(
			List<FlowMetrics> flowMetrics, String textClass){
		int flows = 0;
		for (FlowMetrics flow : flowMetrics) {
			if (flow.getClassToPrecision().containsKey(textClass)){
				flows++;
			}
		}
		return flows;
	}
	
	/**
	 * Computes and returns the total recall of the x most frequent flows in the 
	 * given ordered list of flows (with metrics).
	 * 
	 * @param orderedFlowMetrics The ordered flows with metrics
	 * @param topX The number of flows to compute the recall for
	 * @return The total recall
	 */
	private double computeRecallOfTopFlows(
			List<FlowMetrics> orderedFlowMetrics, int topX){
		double recall = 0.0;
		int index = 0;
		while (index < topX && index < orderedFlowMetrics.size()){
			recall += orderedFlowMetrics.get(index).getRecall();
			index++;
		}
		return recall;
	}
	
	/**
	 * Computes and returns the total recall of all flows in the 
	 * given ordered list of flows (with metrics) that have at least the
	 * specified minimum recall.
	 * 
	 * @param orderedFlowMetrics The ordered flows with metrics
	 * @param minRecall The minimum recall
	 * @return The total recall
	 */
	private double computeRecallOfFrequentFlows(
			List<FlowMetrics> orderedFlowMetrics, double minRecall){
		double recall = 0.0;
		for (FlowMetrics flowMetrics : orderedFlowMetrics) {
			if (flowMetrics.getRecall() < minRecall){
				break;
			}
			recall += flowMetrics.getRecall();
		}
		return recall;
	}

	/**
	 * Computes and returns the weighted average precision of the x most 
	 * frequent flows in the given ordered list of flows (with metrics). The
	 * precision values are weighted according to the recall of the respective
	 * flows.
	 * 
	 * @param orderedFlowMetrics The ordered flows with metrics
	 * @param topX The number of flows to compute the recall for
	 * @return The weighted precision
	 */
	private double computeWeightedPrecisionOfTopFlows(
			List<FlowMetrics> orderedFlowMetrics, int topX){
		double weightedPrecision = 0;
		// Sum up weighted precision of the top x flows
		int index = 0;
		while (index < topX && index < orderedFlowMetrics.size()){
			FlowMetrics flowMetrics = orderedFlowMetrics.get(index);
			// Determine maximum precision value of the current flow
			double maxPrecisionOfFlow = 0.0;
			for (double precision : flowMetrics.getClassToPrecision().values()){
				if (precision > maxPrecisionOfFlow){
					maxPrecisionOfFlow = precision;
				}
			}
			// Weight precision according to recall flow
			weightedPrecision += maxPrecisionOfFlow * flowMetrics.getRecall();
			index++;
		}
		// Now normalize value with total recall of the flows
		double recall = this.computeRecallOfTopFlows(orderedFlowMetrics, topX);
		return weightedPrecision /= recall;
	}
	
	/**
	 * Computes and returns the weighted average precision of all flows in the 
	 * given ordered list of flows (with metrics) that have at least the
	 * specified minimum recall. The precision values are weighted according to 
	 * the recall of the respective flows.
	 * 
	 * @param orderedFlowMetrics The ordered flows with metrics
	 * @param minRecall The minimum recall
	 * @return The weighted precision
	 */
	private double computeWeightedPrecisionOfFrequentFlows(
			List<FlowMetrics> orderedFlowMetrics, double minRecall){
		double weightedPrecision = 0;
		// Sum up weighted precision of all frequent flows
		for (FlowMetrics flowMetrics : orderedFlowMetrics) {
			if (flowMetrics.getRecall() < minRecall){
				break;
			}
			// Determine maximum precision value of the current flow
			double maxPrecisionOfFlow = 0.0;
			for (double precision : flowMetrics.getClassToPrecision().values()){
				if (precision > maxPrecisionOfFlow){
					maxPrecisionOfFlow = precision;
				}
			}
			// Weight precision according to recall flow
			weightedPrecision += maxPrecisionOfFlow * flowMetrics.getRecall();
		}
		// Now normalize value with total recall of the flows
		double recall = 
			this.computeRecallOfFrequentFlows(orderedFlowMetrics, minRecall);
		return weightedPrecision /= recall;
	}
	
	
	
	// -------------------------------------------------------------------------
	// PRINTING
	// -------------------------------------------------------------------------
	
	/**
	 * Prints all given flow metrics to screen using the given title to label
	 * the print out.
	 * 
	 * @param flowMetrics The flow metrics
	 * @param title The title
	 */
	private void printFrequentFlows(List<FlowMetrics> flowMetrics, String title){
		// Get set of possible scores
		TreeSet<String> possibleClasses = new TreeSet<String>();
		for (String mapping : TEXT_CLASS_MAPPING.split(",")) {
			possibleClasses.add(mapping.split(":")[1]);
		}
		// Determine number of flows with recall above treshold
		int above = 0;
		while (above < flowMetrics.size() && 
				flowMetrics.get(above).getRecall() >= RECALL_THRESHOLD){
			above++;
		}
		// Determine length of flows to be printed
		int maxLength = 0;
		for (int i=1; i<=flowMetrics.size(); i++){
			FlowMetrics flow = flowMetrics.get(i-1);
			if (flow.getRecall() >= RECALL_THRESHOLD
					&& flow.getFlow().toString().length() > maxLength){
				maxLength = flow.getFlow().toString().length();
			}
		}	
		// The print out
		System.out.println("\n\n" + above + " " + title);
		String balanced = "";
		if (!BALANCE_METRICS)
			balanced = "not ";
		System.out.println("(corpus has " + balanced + "been balanced)");
		for (int i=0; i<maxLength; i++){
			System.out.print("=");
		}
		System.out.println("================================================="
				+ "================");
		System.out.print("   #  Flow");
		for (int i=0; i<maxLength; i++){
			System.out.print(" ");
		}
		System.out.print("R");
		for (String clazz : possibleClasses) {
			System.out.print("\tP(" + clazz + ")");
		}
		System.out.println();
		for (int i=0; i<maxLength; i++){
			System.out.print("=");
		}
		System.out.println("================================================="
				+ "================");
		for (int i=1; i<=flowMetrics.size(); i++){
			FlowMetrics flow = flowMetrics.get(i-1);
			// Stop printing if recall to low
			if (flow.getRecall() < RECALL_THRESHOLD){
				break;
			}
			String flowString = flow.getFlow().toString();
			// Change flow string for output
			while (flowString.length() <= maxLength+3)
				flowString = flowString + " ";
			flowString = flowString.replaceAll("\\[", "\\(");
			flowString = flowString.replaceAll("\\]", "\\)");
			// Print
			if (i<1000) System.out.print(" ");
			if (i<100) System.out.print(" ");
			if (i<10) System.out.print(" ");
			System.out.print(i + "  ");
			System.out.print(flowString);
			System.out.print(this.roundValue(
					flow.getRecall()*100,1) + "%\t");
			for (String clazz : possibleClasses) {
				if (flow.getClassToPrecision().containsKey(clazz)){
					System.out.print(this.roundValue(
							flow.getClassToPrecision().get(clazz)*100,1) + 
							"%\t");	
				} else System.out.print("--\t");
			}
			System.out.println();
		}
		for (int i=0; i<maxLength; i++){
			System.out.print("=");
		}
		System.out.println("================================================="
				+ "================");
	}
	
	/**
	 * Rounds the given value to a number with the given number of decimal 
	 * places.
	 * 
	 * @param value The value
	 * @param decimalPlaces The number of decimal places
	 * @return The rounded value
	 */
	private double roundValue(double value, int decimalPlaces){
		double rounder = 1.0;
		for (int i=0; i<decimalPlaces; i++)
			rounder *= 10.0;
		return Math.round(value*rounder)/rounder;
	}
	
	
	private void printOverallFlowMetrics(List<FlowMetrics> orderedFlowMetrics, 
			List<String> textClasses, String title){
		// Corpus distribution
		int numberOfTexts = textClasses.size();
		Map<String, Integer> textsPerClass  = new TreeMap<String, Integer>();
		for (String textClass : textClasses) {
			if (!textsPerClass.containsKey(textClass)){
				textsPerClass.put(textClass, 1);
			} else{
				textsPerClass.put(textClass, textsPerClass.get(textClass)+1);
			}
		}
		
		// Total numbers of flows
		int flowsWith2PctRecall = this.computeFlowsWithMinRecall(
				orderedFlowMetrics, 0.02);
		int flowsWith1PctRecall = this.computeFlowsWithMinRecall(
				orderedFlowMetrics, 0.01);
		int flowsOccurringTwice = this.computeFlowsWithMinRecall(
				orderedFlowMetrics, 2.0/textClasses.size());
		int flowsOccurringOnce = this.computeFlowsWithMinRecall(
				orderedFlowMetrics, 1.0/textClasses.size());
		
		// Total numbers of flows per text class
		Map<String, Integer> flowsPerTextClass = new TreeMap<String, Integer>();
		for (String mapping : TEXT_CLASS_MAPPING.split(",")) {
			String textClass = mapping.split(":")[1];
			flowsPerTextClass.put(textClass, this.computeFlowsWithTextClass(
					orderedFlowMetrics, textClass));
		}
		
		// Recall of flows
		double recallAt10 = this.computeRecallOfTopFlows(
				orderedFlowMetrics, 10);
		double recallAt20 = this.computeRecallOfTopFlows(
				orderedFlowMetrics, 20);
		double recallAt2Pct = this.computeRecallOfFrequentFlows(
				orderedFlowMetrics, 0.02);
		double recallAt1Pct = this.computeRecallOfFrequentFlows(
				orderedFlowMetrics, 0.01);
		
		// Weighted precision
		double precisionAt10 = this.computeWeightedPrecisionOfTopFlows(
				orderedFlowMetrics, 10);
		double precisionAt20 = this.computeWeightedPrecisionOfTopFlows(
				orderedFlowMetrics, 20);
		double precisionAt2Pct = this.computeWeightedPrecisionOfFrequentFlows(
				orderedFlowMetrics, 0.02);
		double precisionAt1Pct = this.computeWeightedPrecisionOfFrequentFlows(
				orderedFlowMetrics, 0.01);
		double precisionOfRecur = this.computeWeightedPrecisionOfFrequentFlows(
				orderedFlowMetrics, 2.0/textClasses.size());
		double precisionOfAll = this.computeWeightedPrecisionOfFrequentFlows(
				orderedFlowMetrics, 1.0/textClasses.size());
		
		System.out.println("\n\n" + title);
		String balanced = "";
		if (!BALANCE_METRICS)
			balanced = "not ";
		System.out.println("(corpus has " + balanced + "been balanced)");
		System.out.println("=================================================");
		System.out.println("METRIC                                   VALUE   ");
		System.out.println("=================================================");
		System.out.println("Texts in the corpus                      " + numberOfTexts);
		System.out.println("-------------------------------------------------");
		for (String textClass : textsPerClass.keySet()) {
			System.out.println("Texts with class " + textClass + 
					"                     " + textsPerClass.get(textClass));	
		}		
		System.out.println("-------------------------------------------------");
		for (String textClass : flowsPerTextClass.keySet()) {
			System.out.println("Flows occurring with class " + textClass + 
					"           " + flowsPerTextClass.get(textClass));	
		}
		System.out.println("-------------------------------------------------");
		System.out.println("Flows with >= 2% recall                  " + flowsWith2PctRecall);
		System.out.println("Flows with >= 1% recall                  " + flowsWith1PctRecall);
		System.out.println("Flows occurring >= 2x                    " + flowsOccurringTwice);
		System.out.println("Flows occurring >= 1x                    " + flowsOccurringOnce);
		System.out.println("-------------------------------------------------");
		System.out.println("Total recall of top 10 flows             " + this.roundValue(100*recallAt10,1) + "%");
		System.out.println("Total recall of top 20 flows             " + this.roundValue(100*recallAt20,1) + "%");
		System.out.println("Total recall of flows with >= 2% recall  " + this.roundValue(100*recallAt2Pct,1) + "%");
		System.out.println("Total recall of flows with >= 1% recall  " + this.roundValue(100*recallAt1Pct,1) + "%");
		System.out.println("-------------------------------------------------");
		System.out.println("Weighted precision of top 10 flows       " + this.roundValue(100*precisionAt10,1) + "%");
		System.out.println("Weighted precision of top 20 flows       " + this.roundValue(100*precisionAt20,1) + "%");
		System.out.println("Wghtd. prec. of flows with >= 2% recall  " + this.roundValue(100*precisionAt2Pct,1) + "%");
		System.out.println("Wghtd. prec. of flows with >= 1% recall  " + this.roundValue(100*precisionAt1Pct,1) + "%");
		System.out.println("Wghtd. prec. of flows occurring >= 2x    " + this.roundValue(100*precisionOfRecur,1) + "%");
		System.out.println("Wghtd. prec. of flows occurring >= 1x    " + this.roundValue(100*precisionOfAll,1) + "%");
		System.out.println("=================================================");
	}
	
	/**
	 * Orders the given flow metrics by their recall and returns a new ordered
	 * list.
	 * 
	 * @param flowMetrics The flow metrics
	 * @return The ordered flow metrics
	 */
	private List<FlowMetrics> orderByRecall(List<FlowMetrics> flowMetrics){
		// Make one list for each recall
		TreeMap<Double, List<FlowMetrics>> frequencyMap = 
			new TreeMap<Double, List<FlowMetrics>>();
		for (FlowMetrics flow : flowMetrics) {
			if (!frequencyMap.containsKey(flow.getRecall())){
				frequencyMap.put(flow.getRecall(), new ArrayList<FlowMetrics>());
			} 
			frequencyMap.get(flow.getRecall()).add(flow);
		}
		// Now write ordered by recall
		List<FlowMetrics> orderedFlowMetrics = new ArrayList<FlowMetrics>();
		for (double recall : frequencyMap.descendingKeySet()) {
			List<FlowMetrics> recallMap = frequencyMap.get(recall);
			for (FlowMetrics flow : recallMap) {
				orderedFlowMetrics.add(flow);
			}
		}
		return orderedFlowMetrics;
	}
	
	
	
	// -------------------------------------------------------------------------
	// MAIN
	// -------------------------------------------------------------------------
	
	/**
	 * Creates an annotation counter and runs it.
	 * 
	 * @param args not used
	 */
	public static void main(String[] args) {	
		FlowCounter finder = new FlowCounter();
		finder.computeFlowMetrics();
	}
}

