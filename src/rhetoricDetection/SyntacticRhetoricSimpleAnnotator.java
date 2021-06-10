/**
 * 
 */
package rhetoricDetection;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceConfigurationException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.ruta.engine.Ruta;
import org.apache.uima.ruta.engine.RutaEngine;
import org.apache.uima.util.InvalidXMLException;

import de.aitools.ie.uima.io.LexiconLoader;
import de.aitools.ie.uima.type.core.Constituent;
import de.aitools.ie.uima.type.core.Sentence;
import de.aitools.ie.uima.type.core.Token;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import type.rhetoricDetection.IfConditional;
import type.rhetoricDetection.Pclause;
import type.rhetoricDetection.Qclause;
import type.rhetoricDetection.RhetoricalDevice;
import type.rhetoricDetection.zeugmaConstituent;



/**
 * @author viorel.morari
 *
 * This class finds and annotates rhetorical devices existing in the input text.
 * Class consists of methods which include the detection algorithms specific 
 * for each rhetorical device regarded in this work. Part of the algorithms - 
 * easier to adapt to Ruta - are written as Ruta rules.
 *
 */
public class SyntacticRhetoricSimpleAnnotator extends JCasAnnotator_ImplBase {
	
	
	// -------------------------------------------------------------------------
	// VALUES
	// -------------------------------------------------------------------------
    
    //types of verbs defined in PennTreebank
    private static final Set<String> CLAUSE_VERB_TYPES =
	  	      new TreeSet<>(Arrays.asList(new String[] {
	  	          "VB", "VBD", "VBN", "VBP", "VBZ", "MD"
	  	}));
    
	//types of nouns defined in PennTreebank
	private static final Set<String> CLAUSE_NOUN_TYPES =
	  	      new TreeSet<>(Arrays.asList(new String[] {
	  	          "NN", "NNS", "NNP", "NNPS"}));
	
	//parameter for stopwords lexicon
    private static final String PARAM_STOPWORDS = "StopwordsLexicon";
    //set to store the stopwords lexicon
    private Set<String> stopwordsLexicon = new HashSet<String>();
    
    /**
     * Sets the allowed deviation from the length of the reference sentence 
     * (by default - 3, i.e. input sentences will be checked for isocolon iff 
     * all sentences length lie in the interval [starting sentence length - 3, 
     * starting sentence length + 3] )
     */
    private static final int isocolonDeviationFactor = 3;
    
    /**
     * Sets the minimal isocolon coverage in the sentence to be considered valid
     * By default, an isocolon which takes more than half of the sentence(=55) 
     * is considered valid
     */
    private static final int isocolonMinCoverage = 55;
    
	// -------------------------------------------------------------------------
	// INITIALIZATION
	// -------------------------------------------------------------------------
	
	@Override
	public void initialize(UimaContext aContext) 
	throws ResourceInitializationException {
		super.initialize(aContext);
		
		LexiconLoader loader = new LexiconLoader();
		
		// Load lexicons
		String stopwordsPath =
				(String) aContext.getConfigParameterValue(PARAM_STOPWORDS);
		
		this.stopwordsLexicon = loader.load(stopwordsPath);
	}
	
	
	// -------------------------------------------------------------------------
	// PROCESSING
	// -------------------------------------------------------------------------
	
	@Override
	public void process(JCas jcas){
		
		
		//sentence iterator
		FSIterator<Annotation> sentenceIter = 
				jcas.getAnnotationIndex(Sentence.type).iterator();
		List<String> ifSentences = new ArrayList<String>();
		List<Constituent> constituents = new ArrayList<Constituent>();
		List<Annotation> sentences = new ArrayList<Annotation>();
		List<RhetoricalDevice> rhetoricalDevices = new ArrayList<RhetoricalDevice>();
		List<zeugmaConstituent> zeugmaConstituents = new ArrayList<zeugmaConstituent>();
		// Initialize the tagger for comparative and superlative Adjective/Adverbs
		MaxentTagger tagger = new MaxentTagger("edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger");
		
		//iterates over the jcas sentences
		while (sentenceIter.hasNext()){
			Annotation sentence = sentenceIter.next();
			sentences.add(sentence);
			List<Token> tokens = new ArrayList<Token>();
			constituents.clear();

			//token iterator
			FSIterator<Annotation> tokenIter = 
					jcas.getAnnotationIndex(Token.type).subiterator(sentence);
			//iterate over the tokens
			while (tokenIter.hasNext()){
				tokens.add((Token) tokenIter.next());
			}
			//constituent iterator
			FSIterator<Annotation> constIter = 
					jcas.getAnnotationIndex(Constituent.type).subiterator(sentence);
			//iterate over the constituents
			while (constIter.hasNext()){
				constituents.add((Constituent) constIter.next());
			}
			
			//Creates annotations of if-conditional sentences; P and Q clauses
			getIfCondAnnotations(jcas, sentence, ifSentences);
			
			annotateEpanalepsis(jcas, tokens, sentence, stopwordsLexicon);
			
			//tag the current sentence used in identification of adjectives and adverbs
			String taggedSentence = tagger.tagTokenizedString(sentence.getCoveredText());
			//annotate comparative and superlative adjectives and adverbs
			annotateCompSuperAdjectiveAdverb(jcas, sentence, taggedSentence, "comparativeAdj", "JJR");
			annotateCompSuperAdjectiveAdverb(jcas, sentence, taggedSentence, "comparativeAdv", "RBR");
			annotateCompSuperAdjectiveAdverb(jcas, sentence, taggedSentence, "superlativeAdj", "JJS");
			annotateCompSuperAdjectiveAdverb(jcas, sentence, taggedSentence, "superlativeAdv", "RBS");
			
			//compute Zeugma constituents used in identification of hypo- and epizeugma
			getZeugmaConstituents(jcas, tokens);
			
			annotateWhether_UnlessCond(jcas, sentence);
			annotateEpizeuxis(jcas, sentence, tokens);
			annotateDiacope(jcas, tokens, sentence, stopwordsLexicon, 5);
			annotateEpizeugma(jcas, sentence, constituents);
		}

		annotateMEMA(jcas, sentences, stopwordsLexicon, 1);
		annotateMEMA(jcas, sentences, stopwordsLexicon, 2);
		annotateMEMA(jcas, sentences, stopwordsLexicon, 3);
		annotateMEMA(jcas, sentences, stopwordsLexicon, 4);
		
		//ignore documents with less than 3 sentences --> invalid for tetracolon
		for (int i = 0; i < sentences.size(); i++) {
			if (sentences.get(i).getCoveredText().length()<3) {
				sentences.remove(i);
			}
		}

		//number of sentences to be checked for isocolon
		int checkedSentences = 0;
		if (sentences.size()>=4) {
			checkedSentences = 3;
		} else if (sentences.size()==3) {
			checkedSentences = 2;
		} else if (sentences.size()==2) {
			checkedSentences = 1;
		}
		
		//ANNOTATE ISOCOLON
		//iscolon on the paragraph level
		if (sentences.size()>1) {
			for (int i = 0; i < sentences.size()-checkedSentences; i++) {
				//check for tetracolon
				if (checkedSentences==3) {
					if (annotateIsocolon(jcas, isocolonDeviationFactor, isocolonMinCoverage, sentences.get(i), sentences.get(i+1), sentences.get(i+2), sentences.get(i+3))) {
						i+=3;
						continue;
					}
					else if (annotateIsocolon(jcas, isocolonDeviationFactor, isocolonMinCoverage, sentences.get(i), sentences.get(i+1), sentences.get(i+2))) {
						i+=2;
						continue;
					}
					else if (annotateIsocolon(jcas, isocolonDeviationFactor, isocolonMinCoverage, sentences.get(i), sentences.get(i+1))) {
						i+=1;
						continue;
					}
				//check for tricolon
				} else if (checkedSentences==2) {
//					System.out.println("Sentences: "+sentences.size());
					if (annotateIsocolon(jcas, isocolonDeviationFactor, isocolonMinCoverage, sentences.get(i), sentences.get(i+1), sentences.get(i+2))) {
						i+=2;
						continue;
					}
					else if (annotateIsocolon(jcas, isocolonDeviationFactor, isocolonMinCoverage, sentences.get(i), sentences.get(i+1))) {
						i+=1;
						continue;
					}
				//check for bicolon
				} else if (checkedSentences==1) {
					if (annotateIsocolon(jcas, isocolonDeviationFactor, isocolonMinCoverage, sentences.get(i), sentences.get(i+1))) {
						i+=1;
						continue;
					}
				}
				
			}
		//iscolon on the sentence level	
		} else {
			for (int i = 0; i < sentences.size(); i++) {
				annotateIsocolon(jcas, isocolonDeviationFactor, isocolonMinCoverage, sentences.get(i));
			}
		}
		
		//-----------------------------------------------------
		// RUTA RULES
		//-----------------------------------------------------
		
		//Ruta Rule for annotating the specific Passive Voice verbs used in rulesPassiveVoice
		String verbsPassiveVoice = "STRINGLIST verbForms = {\"be\",\"been\",\"was\",\"is\",\"being\",\"am\",\"are\",\"were\"};"
				+ "Document{-> MARKFAST(verbsPassiveVoice, verbForms)};";
		//Ruta Rule for annotating the nouns
		String nouns = "STRINGLIST nounTags = {\"NN\", \"NNP\", \"NNS\", \"NNPS\", \"PRP\", \"PRP$\"};"
				+ "c:Constituent{INLIST(nounTags, c.label) -> Noun};";
		//Ruta Rule for annotating the conjunctions
		String conjunctions = "c:Constituent{c.label==\"CC\" -> Conj};";
		//Ruta Rule for annotating the verbs
		String verbs = "STRINGLIST verbTags = {\"VB\", \"VBD\", \"VBG\", \"VBN\", \"VBP\", \"VBZ\"};"
				+ "c:Constituent{INLIST(verbTags, c.label) -> Verb};";
		//Ruta Rule for annotating nominal and clausal subjects as dependents for a governor
		String dependents = "STRINGLIST dependentLabels = {\"nsubj\", \"nsubjpass\", \"csubj\", \"csubjpass\"};"
				+ "t:Token{INLIST(verbTags, t.depLabel) -> Dependent};";
		
		//Ruta Rule for annotating If-conditional sentences of type 0
		//Zero conditionals express general truths, events in which
		//the premise always causes the conclusion to happen.
		//POSTags Pattern: P clause <- VB/VBP/VBZ; Q clause <- VB/VBP/VBZ;
		String rulesIfCondZero = "STRING id;"
				+ "STRING parentP;"
				+ "STRING parentQ;"
				+ "STRINGLIST ifCondZeroPosList;"
				+ "BOOLEAN QclauseCond;"
				+ "FOREACH(ifCond) IfConditional{}{"
				+ "ifCond{-> QclauseCond = true};"
				+ "ifCond{->GETFEATURE(\"matchId\", id)};"
				+ "Pclause{->GETFEATURE(\"parent\", parentP)};"
				+ "Qclause{->GETFEATURE(\"parent\", parentQ)};"
				+ "ifCond{-> QclauseCond=false} <-"
				+ "{Qclause<-{(Constituent.label == \"MD\");};};"
				+ "ifCond{AND(IF(id == parentP), IF(id == parentQ), IF(QclauseCond == true))-> "
				+ "CREATE(RhetoricalDevice, \"deviceType\"=\"IfCondZero\", \"category\"=\"custom\")} <-"
				+ "{Pclause<-{c1:Constituent{CONTAINS(ifCondZeroPosList, c1.label)};} % "
				+ "Qclause<-{c2:Constituent{CONTAINS(ifCondZeroPosList, c2.label)};};};}";		
		
		//define the POSTags list which is given as a additional parameter at the execution
		List<String> ifCondZeroPosList = Arrays.asList(new String[] { "VB", "VBP", "VBZ" });
		Map<String, Object> ifZeroAdditionalParams = new HashMap<>();
		ifZeroAdditionalParams.put(RutaEngine.PARAM_VAR_NAMES, new String[] { "ifCondZeroPosList" });
		ifZeroAdditionalParams.put(RutaEngine.PARAM_VAR_VALUES, new String[] { StringUtils.join(ifCondZeroPosList, ",") });
		

		//Ruta Rule for annotating If-conditional sentences of type 1
		//First conditional is used to refer to situations which are
		//very likely (yet not guaranteed) to happen in the future.
		//POSTags Pattern: P clause <- VB/VBP/VBZ/VBG; Q clause <- MD + VB;
		String rulesIfCondOne = "STRING id;"
				+ "STRING parentP;"
				+ "STRING parentQ;"
				+ "STRINGLIST ifCondOnePosList;"
				+ "IfConditional{->GETFEATURE(\"matchId\", id)};"
				+ "Pclause{->GETFEATURE(\"parent\", parentP)};"
				+ "Qclause{->GETFEATURE(\"parent\", parentQ)};"
				+ "IfConditional{AND(IF(id == parentP), IF(id == parentQ))-> CREATE(RhetoricalDevice, \"deviceType\"=\"IfCondOne\", \"category\"=\"custom\")} <-"
				+ "{Pclause<-{c1:Constituent{CONTAINS(ifCondOnePosList, c1.label)};} % "
				+ "Qclause<-{Constituent.label == \"MD\" # Constituent.label == \"VB\";};};";		
		
		//define the POSTags list which is given as a additional parameter at the execution
		List<String> ifCondOnePosList = Arrays.asList(new String[] { "VB", "VBP", "VBZ", "VBG"});
		Map<String, Object> ifOneAdditionalParams = new HashMap<>();
		ifOneAdditionalParams.put(RutaEngine.PARAM_VAR_NAMES, new String[] { "ifCondOnePosList" });
		ifOneAdditionalParams.put(RutaEngine.PARAM_VAR_VALUES, new String[] { StringUtils.join(ifCondOnePosList, ",") });

		//Ruta Rule for annotating If-conditional sentences of type 2
		//Second conditional sentences expresses consequences that
		//are totally unrealistic or will not likely happen in the future.
		//POSTags Pattern: P clause <- VBD; Q clause <- MD + VB;
		String rulesIfCondTwo = "STRING id;"
				+ "STRING parentP;"
				+ "STRING parentQ;"
				+ "STRINGLIST ifCondTwoPosList;"
				+ "BOOLEAN QclauseCond;"
				+ "FOREACH(ifCond) IfConditional{}{"
				+ "ifCond{-> QclauseCond = true};"
				+ "ifCond{->GETFEATURE(\"matchId\", id)};"
				+ "Pclause{->GETFEATURE(\"parent\", parentP)};"
				+ "Qclause{->GETFEATURE(\"parent\", parentQ)};"
				+ "ifCond{-> QclauseCond=false} <-"
				+ "{Qclause<-{(Constituent.label == \"VBN\" | Constituent.label == \"VBD\");};};"
				+ "ifCond{AND(IF(id == parentP), IF(id == parentQ), IF(QclauseCond == true))-> CREATE(RhetoricalDevice, \"deviceType\"=\"IfCondTwo\",\"category\"=\"custom\")} <-"
				+ "{Pclause<-{c1:Constituent{CONTAINS(ifCondTwoPosList, c1.label)};} % "
				+ "Qclause<-{(Constituent.label == \"MD\" # Constituent.label == \"VB\");} ;};}";		
		
		//define the POSTags list which is given as a additional parameter at the execution
		List<String> ifCondTwoPosList = Arrays.asList(new String[] { "VBD"});
		Map<String, Object> ifTwoAdditionalParams = new HashMap<>();
		ifTwoAdditionalParams.put(RutaEngine.PARAM_VAR_NAMES, new String[] { "ifCondTwoPosList" });
		ifTwoAdditionalParams.put(RutaEngine.PARAM_VAR_VALUES, new String[] { StringUtils.join(ifCondTwoPosList, ",") });
				
		//Ruta Rule for annotating If-conditional sentences of type 3
		//Third conditional sentences are used to explain that present
		//circumstances would be different if something different had happened in the
		//past.
		//POSTags Pattern: P clause <- VBD + VBN; Q clause <- MD + VBN;
		String rulesIfCondThree = "STRING id;"
				+ "STRING parentP;"
				+ "STRING parentQ;"
				+ "STRINGLIST ifCondTwoPosList;"
				+ "IfConditional{->GETFEATURE(\"matchId\", id)};"
				+ "Pclause{->GETFEATURE(\"parent\", parentP)};"
				+ "Qclause{->GETFEATURE(\"parent\", parentQ)};"
				+ "IfConditional{AND(IF(id == parentP), IF(id == parentQ))-> CREATE(RhetoricalDevice, \"deviceType\"=\"IfCondThree\", \"category\"=\"custom\")} <-"
				+ "{Pclause<-{(Constituent.label == \"VBD\" # Constituent.label == \"VBN\");} % "
				+ "Qclause<-{Constituent.label == \"MD\" # (Constituent.label == \"VBN\" | "
				+ "Constituent.label == \"VBD\");};};";

		//Ruta Rule for annotating Counterfactuals with If in P clause
		//Counterfactuals are statements that examine how a hypothetical change 
		//in a past experience could have affected the outcome of that experience.
		//POSTags Pattern: P clause <- VBD + VBN; Q clause <- contains any modals;
		String rulesIfCounterfactual = "STRING id;"
				+ "STRING parentP;"
				+ "STRING parentQ;"
				+ "STRINGLIST counterFPosList;"
				+ "IfConditional{->GETFEATURE(\"matchId\", id)};"
				+ "Pclause{->GETFEATURE(\"parent\", parentP)};"
				+ "Qclause{->GETFEATURE(\"parent\", parentQ)};"
				+ "IfConditional{AND(IF(id == parentP), IF(id == parentQ))-> CREATE(RhetoricalDevice, \"deviceType\"=\"IfCounterfactual\", \"category\"=\"custom\")} <-"
				+ "{Pclause<-{c1:Constituent{CONTAINS(counterFPosList, c1.label)};} % "
				+ "Qclause<-{W{REGEXP(\"could|should|would\")};};};";

		//define the POSTags list which is given as a additional parameter at the execution
		List<String> counterFPosList = Arrays.asList(new String[] { "VBD", "VBN"});
		Map<String, Object> counterFAdditionalParams = new HashMap<>();
		counterFAdditionalParams.put(RutaEngine.PARAM_VAR_NAMES, new String[] { "counterFPosList" });
		counterFAdditionalParams.put(RutaEngine.PARAM_VAR_VALUES, new String[] { StringUtils.join(counterFPosList, ",") });	

		
		//Ruta Rule for annotating Asyndeton sentences
		//Asyndeton consists of omitting conjunctions between words, phrases, or clauses. (Silva Rhetoricae)
		//The rule looks for next pattern: commas separated by words/phrases
		String rulesAsyndeton = "STRINGLIST asyndetonPosList;"
				+ "Sentence{-> CREATE(RhetoricalDevice, \"deviceType\"=\"Asyndeton\", \"category\"=\"omission\")}"
				+ "<- {((W COMMA W COMMA) | (c1:Constituent{CONTAINS(asyndetonPosList,c1.label)} "
				+ "COMMA c1:Constituent{CONTAINS(asyndetonPosList,c1.label)} COMMA));};";
		
		//define the POSTags list which is given as a additional parameter at the execution
		List<String> asyndetonPosList = Arrays.asList(new String[] { "ADJP", "ADVP", "CONJP", "NP", "PP", "VP", "S", "SBAR"});
		Map<String, Object> asyndetonAdditionalParams = new HashMap<>();
		asyndetonAdditionalParams.put(RutaEngine.PARAM_VAR_NAMES, new String[] { "asyndetonPosList" });
		asyndetonAdditionalParams.put(RutaEngine.PARAM_VAR_VALUES, new String[] { StringUtils.join(asyndetonPosList, ",") });	
		
		
		//Ruta Rule for annotating Polysyndeton sentences
		//Polysyndeton is a rhetorical term which employs many
		//conjunctions between clauses, often slowing the tempo or rhythm. (Silva Rhetoricae)
		//The rule looks for next pattern: conjunctions separated by words/phrases
		String rulesPolysyndeton = "STRINGLIST polysyndetonPosList;"
				+ "Sentence{-> CREATE(RhetoricalDevice, \"deviceType\"=\"Polysyndeton\", \"category\"=\"repetition\")}"
				+ "<- {((W Constituent.label == \"CC\" W Constituent.label == \"CC\") | (c1:Constituent{CONTAINS(polysyndetonPosList,c1.label)} "
				+ "ANY? Constituent.label == \"CC\" c1:Constituent{CONTAINS(polysyndetonPosList,c1.label)} ANY? Constituent.label == \"CC\"));};";
						
		//define the POSTags list which is given as a additional parameter at the execution
		List<String> polysyndetonPosList = Arrays.asList(new String[] { "ADJP", "ADVP", "CONJP", "NP", "PP", "VP", "S", "SBAR"});
		Map<String, Object> polysyndetonAdditionalParams = new HashMap<>();
		polysyndetonAdditionalParams.put(RutaEngine.PARAM_VAR_NAMES, new String[] { "polysyndetonPosList" });
		polysyndetonAdditionalParams.put(RutaEngine.PARAM_VAR_VALUES, new String[] { StringUtils.join(polysyndetonPosList, ",") });	
		
		//create annotations of different types of verbs serving in detection of Epizeugma
		String verbsEpizeugma = "(Constituent.label==\"VB\" | Constituent.label==\"VBZ\" | "
				+ "Constituent.label==\"VBD\" | Constituent.label==\"VBG\" | Constituent.label==\"VBN\" "
				+ "| Constituent.label==\"VBP\"){->verbsEpizeugma};";
		
		
		//Ruta rule for annotating Hypozeugmas
		//Hypozeugma - placing last, in a construction containing several words or phrases 
		//of equal value, the word or words on which all of them depend. (Silva Rhetoricae)
		//The rule looks for next pattern: nouns and "comma" within the zeugma part of the sentence
//		String rulesHypozeugma = "STRINGLIST hypozeugmaPosList;"
//				+ "Sentence{-> CREATE(RhetoricalDevice, \"deviceType\"=\"Hypozeugma\", \"category\"=\"omission\")}"
//				+ "<- {zeugmaConstituent{CONTAINS(hypozeugmaPosList,Constituent.label), CONTAINS({COMMA, Constituent.label==\"CC\"});};};";
		String rulesHypozeugma = "STRINGLIST hypozeugmaPosList;"
				+ "Sentence{ -> CREATE(RhetoricalDevice, \"deviceType\"=\"Hypozeugma\", \"category\"=\"omission\")}"
				+ "<- {zeugmaConstituent{CONTAINS(Noun,2,100), CONTAINS(Noun, 25, 100, true), OR(CONTAINS(COMMA), CONTAINS(Conj))};};"
				+ "h:RhetoricalDevice.deviceType==\"Hypozeugma\"{CONTAINS(zeugmaConstituent,3,100) -> UNMARK(h)};";
		
		//define the POSTags list which is given as a additional parameter at the execution
//		List<String> hypozeugmaPosList = Arrays.asList(new String[] { "NN", "NNP", "NNS", "NNPS"});
//		Map<String, Object> hypozeugmaAdditionalParams = new HashMap<>();
//		hypozeugmaAdditionalParams.put(RutaEngine.PARAM_VAR_NAMES, new String[] { "hypozeugmaPosList" });
//		hypozeugmaAdditionalParams.put(RutaEngine.PARAM_VAR_VALUES, new String[] { StringUtils.join(hypozeugmaPosList, ",") });	

		//Ruta rule for annotating Enumerations
		//Enumeration is a rhetorical device used mainly to list a
		//series of details, words or phrases.
		//It is indended primarly for detecting triple enumeration but also works
		//for larger enumerations
		// The rule looks for the following pattern: "comma" and conjuction at a distance of at most 5 tokens
//		String rulesTripleEnum = "Sentence{-> CREATE(RhetoricalDevice, \"deviceType\"=\"TripleEnumeration\", \"category\"=\"parallelism\")}"
//				+ "<- {COMMA ANY[1,5]? Constituent.label == \"CC\";};";
		String rulesTripleEnum = "Sentence{-> CREATE(RhetoricalDevice, \"deviceType\"=\"TripleEnumeration\", \"category\"=\"parallelism\")}"
				+ "<- {COMMA ANY[1,4]{-PARTOF(Conj)} Conj;};";
		
		//Ruta rule for annotating Pysmas: the rule looks for consecutive 
		//sentences that end with question mark (from 2 up to 10 sentences)
		//Pysma is the act of asking multiple questions successively
		//(which would together require a complex reply).
		String rulesPysma = "(Sentence{ENDSWITH(QUESTION)})[2,10]{->MARKONCE(RhetoricalDevice), "
				+ "RhetoricalDevice.deviceType = \"Pysma\", RhetoricalDevice.category = \"parallelism\"};";
			
		//Ruta rule for annotating the Passive Voice sentences: it searches for combinations of 
		//forms of the verb "to be" and past participle of the main verb. 
		//The window size of [1,5] is interpreted by Ruta as 2 tokens after annotation of verbsPassiveVoice
		String rulesPassiveVoice = "Sentence{-> "
				+ "CREATE(RhetoricalDevice, \"deviceType\"=\"PassiveVoice\", \"category\"=\"custom\")} <- "
				+ "{(Constituent.label==\"VBN\"{NEAR(verbsPassiveVoice,1,5,false)});};";
		
		
		//execution functions of Ruta
		try {
			Ruta.apply(jcas.getCas(), nouns);
			Ruta.apply(jcas.getCas(), verbs);
			// deactivate annotation of dependents as it is not used
			// Ruta.apply(jcas.getCas(), dependents);
			Ruta.apply(jcas.getCas(), conjunctions);
			Ruta.apply(jcas.getCas(), verbsPassiveVoice);
			Ruta.apply(jcas.getCas(), rulesAsyndeton, asyndetonAdditionalParams);
			Ruta.apply(jcas.getCas(), rulesPolysyndeton, polysyndetonAdditionalParams);
			Ruta.apply(jcas.getCas(), rulesHypozeugma);
			Ruta.apply(jcas.getCas(), rulesIfCondZero, ifZeroAdditionalParams);
			Ruta.apply(jcas.getCas(), rulesIfCondOne, ifOneAdditionalParams);
			Ruta.apply(jcas.getCas(), rulesIfCondTwo, ifTwoAdditionalParams);
			Ruta.apply(jcas.getCas(), rulesIfCondThree);
			Ruta.apply(jcas.getCas(), rulesIfCounterfactual, counterFAdditionalParams);
			Ruta.apply(jcas.getCas(), rulesTripleEnum);
			Ruta.apply(jcas.getCas(), rulesPysma);
			Ruta.apply(jcas.getCas(), rulesPassiveVoice);
			
		} catch (AnalysisEngineProcessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidXMLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ResourceInitializationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ResourceConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		//test code to check annotations output
		FSIterator<Annotation> condZeroAnnotIter = jcas.getAnnotationIndex(RhetoricalDevice.type).iterator();
		while (condZeroAnnotIter.hasNext()){
			Annotation docAnnotation = condZeroAnnotIter.next();
			rhetoricalDevices.add((RhetoricalDevice) docAnnotation);
		}
		int counter = 0;
		for (RhetoricalDevice device : rhetoricalDevices) {
			if (device.getDeviceType().equals("TripleEnumeration")) {
				System.out.println(device.getDeviceType()+": "+device.getCoveredText());
				counter++;
			}
			
		}
		System.out.println("TripleEnumeration count : "+counter);
	}

	
   /**
   * Creates the annotations of if-conditional sentences; 
   * identifies and annotates the conditional(P) and head(Q) clauses in a conditional sentence.
   * Additionally it marks the remaining clause(if any) besides P and Q. 
   * @param jcas 
   * 			- The Jcas to add the annotations to
   * @throws IndexOutOfBoundsException
   * 			- if the substring of the Q clause fails
   */
	
	private void getIfCondAnnotations(JCas jcas, Annotation sentence, List<String> ifSentences){

		int beginOfSentence = sentence.getBegin();
		int endOfSentence = sentence.getEnd();
		int beginOfPclause = 0;
		int endOfPclause = 0;
		int beginOfQclause = 0;
		int endOfQclause = 0;
		int startTrimIndex = 0;
		String P_clause = "";
		String Q_clause = "";
		String R_clause = "";
		List<String> governors = new ArrayList<String>();
		List<String> governorsSorted = new ArrayList<String>();
		String condIndicator = "if";
		String currSentence = sentence.getCoveredText().toLowerCase();
		
		//consider only the sentences which do contain the "if" particle
		if (isMatch(currSentence, condIndicator) != -1) {
			ifSentences.add(currSentence);
			//get the list of all the governors within the nsubj relations in a sentence 
			//evoked by Stanford Dependency Parser 
			governors = getGovernors(jcas, sentence);
			//consider only the sentences which doesn't have more than 3 nsubj relations
			if (governors.size() >= 2) {
				//sort the governors based on their distance from the conditional clause
				governorsSorted = sortGovernors(currSentence, condIndicator, governors);
				//get the P clause by extracting the span of text 
				//between the "if" particle(uniquely identified with isMatch method)
				//and the next closest governor
				try {
					P_clause = currSentence.substring(
							isMatch(currSentence, condIndicator), 
							isMatch(currSentence, governorsSorted.get(0))+governorsSorted.get(0).length());
				} catch (StringIndexOutOfBoundsException e1) {
					System.out.println("**Exception occured: P clause skipped!");
					//e1.printStackTrace();
					
				}
				
				beginOfPclause = beginOfSentence + currSentence.indexOf(P_clause);
				endOfPclause = beginOfPclause + P_clause.length();
				
				//check if getting the next governor exists in the list
				//if (governors.size() >= 2) {
					String[] sentenceWords = currSentence.split("\\s+");
					
					for (int i = 0; i < sentenceWords.length; i++) {
					    //check for a non-word character and remove it(except quote - in case of "don't")
						sentenceWords[i] = sentenceWords[i].replaceAll("[\\W&&[^']]", "");
					}
					
					try {
						//check if exists and get the fourth token to the left of the governor
						if (Arrays.asList(sentenceWords).indexOf(governorsSorted.get(1))-4 >= 0) {
							startTrimIndex = Arrays.asList(sentenceWords).indexOf(governorsSorted.get(1))-4;
						}
						//get the third token to the left of the governor
						else if(Arrays.asList(sentenceWords).indexOf(governorsSorted.get(1))-3 >= 0){
							startTrimIndex = Arrays.asList(sentenceWords).indexOf(governorsSorted.get(1))-3;
						}
						//get the second token to the left of the governor
						else if(Arrays.asList(sentenceWords).indexOf(governorsSorted.get(1))-2 >= 0){
							startTrimIndex = Arrays.asList(sentenceWords).indexOf(governorsSorted.get(1))-2;
						}
						//get the first token to the left of the governor
						else{
							startTrimIndex = Arrays.asList(sentenceWords).indexOf(governorsSorted.get(1))-1;
						}

						String startTrimToken = sentenceWords[startTrimIndex];
						
						//get the Q clause by extracting the span of text 
						//between the startTrimToken(uniquely identified with isMatch method) 
						//and the next closest governor
						Q_clause = currSentence.substring(
								isMatch(currSentence, startTrimToken), 
								isMatch(currSentence, governorsSorted.get(1))+governorsSorted.get(1).length());
						beginOfQclause = beginOfSentence + currSentence.indexOf(Q_clause);
						endOfQclause = beginOfQclause + Q_clause.length();
						
					} catch (IndexOutOfBoundsException e) {
						System.out.println("**Exception occured: Q clause skipped!");
						//e.printStackTrace();
					}
				
				//check if the extraction of the remaining(R) clause is possible
					try {
						
						//get the R clause by extracting the span of text 
						//between the tenth char to the left of the governor and the next closest governor
						R_clause = currSentence.substring(
								currSentence.indexOf(governorsSorted.get(2))-10, 
								currSentence.indexOf(governorsSorted.get(2))+governorsSorted.get(2).length());
					} catch (IndexOutOfBoundsException e) {
						System.out.println("**Exception occured: R clause skipped! "+governors.size());
					}
					
				//create annotations of If-conditional sentence
				IfConditional ifCondSentence = new IfConditional(jcas, beginOfSentence, endOfSentence, P_clause, Q_clause, R_clause);
				//set the MathId feature used as parent indicator for P and Q clauses
				ifCondSentence.setMatchId(ifCondSentence.getAddress());
				//create annotations of P clause
				Pclause p_clause = new Pclause(jcas, beginOfPclause, endOfPclause);
				p_clause.setParent(ifCondSentence.getAddress());
				//create annotations of P clause
				Qclause q_clause = new Qclause(jcas, beginOfQclause, endOfQclause);
				q_clause.setParent(ifCondSentence.getAddress());
				ifCondSentence.addToIndexes(jcas);
				p_clause.addToIndexes(jcas);
				q_clause.addToIndexes(jcas);
			}
		}
	}
	
	/**
   * Creates the annotations of Constituents for Zeugma rhetorical device; 
   * a Constituent consists of the span of text between the subject and its next governor.
   * Constituents are later used in a Ruta Rule for detection of (Hypo/Meso)Zeugma syntactic pattern 
   * 
   * @param jcas 
   * 			- The Jcas to add the annotations to
   * @param tokens
   * 			- The list of tokens in the current sentence
   */
		
		private void getZeugmaConstituents(JCas jcas, List<Token> tokens){
		
			//Iterate over sentence tokens to catch the governors
			for (Token token : tokens) {
				int subjectStart = 0;
				int parentEnd = 0;
				//governors from nsubj, nsubjpass, csubj and csubjpass relations are valid
				//for more info visit http://nlp.stanford.edu/software/dependencies_manual.pdf
				if (token.getDepLabel() == "nsubj" || token.getDepLabel() == "nsubjpass" ||
						token.getDepLabel() == "csubj" || token.getDepLabel() == "csubjpass") {
					
					parentEnd = token.getParent().getEnd();
					subjectStart = token.getBegin();
					//Governors sentenceGovernors = new Governors(jcas, parentStart, parentEnd);
					//sentenceGovernors.addToIndexes(jcas);
					
					//sometimes the subject may be placed after its governor
					//i.e. Still, there are signs of hope. --> 
					//'are' is identified as governor whilst 'signs' is a subject
					//therefore we exclude this possibility because it does not fulfill
					//the zeugma conditions
					if (subjectStart<parentEnd) {
						zeugmaConstituent zeugmaConst = new zeugmaConstituent(jcas, subjectStart, parentEnd);
						zeugmaConst.addToIndexes(jcas);
//					}else {
//						List<Token> selectCovered = JCasUtil.selectCovered(jcas, Token.class, subjectStart, tokens.get(tokens.size()-1).getBegin());
//						for (Token token2 : selectCovered) {
//							if (token2.getDepLabel().equals("conj")) {
//								
//								
//							}
//						}
					}
				}
			}
		}
	
	/**
	   * Creates annotations of Epanalepsis;
	   * Epanalepsis repeats the beginning word of a clause or sentence at the end.
	   * 
	   * Example: Water alone dug this giant canyon; yes, just plain water. 
	   * 
	   *  @param jcas 
	   * 			- The Jcas to add the annotations to
	   *  @param tokens
	   *  			- The list of sentence tokens
	   *  @param sentence
	   *  			- The sentence under analysis
	   *  @param stopwords
	   *  			- the lexicon of stopwords to filter out 
	   *  				unintentional usage of this rhetorical device
	   */
			
		private void annotateEpanalepsis(JCas jcas, List<Token> tokens, 
				Annotation sentence, Set<String> stopwords){
			
			int startOfSentence = sentence.getBegin();
			int endOfSentence = sentence.getEnd();
			List<Token> tokensList = new ArrayList<Token>(tokens);
			List<String> tokensListToLowercase = new ArrayList<String>();
	    	List<String> firstWords = new ArrayList<String>();
	    	List<String> middleWords = new ArrayList<String>();
	    	List<String> lastWords = new ArrayList<String>();
	    				
			int middleStartPos, middleEndPos = 0;
			//set start position of the middle part of the sentence to be
			//the beginning of the second quarter of the tokens list(no stopwords) 
			middleStartPos = ((int) Math.round(tokensList.size()/5));
			//set start position of the middle part of the sentence to be
			//the end of the third quarter of the tokens list(no stopwords)
			middleEndPos = ((int) Math.round(tokensList.size()/5))*4;
			//System.out.println("middleStart: "+middleStartPos+"\nmiddleEnd: "+middleEndPos);
			if (middleStartPos==0 && tokensList.size()>=3) {
				middleStartPos=1;
				middleEndPos = tokensList.size()-1;
			}

			List<Token> firstTokens = new ArrayList<Token>(tokensList.subList(0, middleStartPos));
			List<Token> lastTokens = new ArrayList<Token>(tokensList.subList(middleEndPos, tokensList.size()));
			List<Token> middleTokens = new ArrayList<Token>(tokensList.subList(middleStartPos, middleEndPos));
			
			//collect all first tokens (except punctuation marks)
			firstTokens.forEach(token -> {
				if (!stopwords.contains(token.getCoveredText().toLowerCase())) {
					firstWords.add(token.getCoveredText().toLowerCase());
			}});
			//collect all middle tokens (except punctuation marks)
			middleTokens.forEach(token -> {
				if (!stopwords.contains(token.getCoveredText().toLowerCase())) {
					middleWords.add(token.getCoveredText().toLowerCase());
			}});
			//collect all last tokens (except punctuation marks)
			lastTokens.forEach(token -> {
				if (!stopwords.contains(token.getCoveredText().toLowerCase())) {
					lastWords.add(token.getCoveredText().toLowerCase());
			}});
			//check for intersections between first and last words of the input sentence
			if (!Collections.disjoint(firstWords, lastWords)) {
				RhetoricalDevice epanalepsis = new RhetoricalDevice(jcas, startOfSentence, endOfSentence);
				epanalepsis.setDeviceType("Epanalepsis");
				epanalepsis.setCategory("repetition");
				epanalepsis.addToIndexes(jcas);
			}
		}		
		
		
		/**
		   * Creates annotations of Mesarchia/Epiphoza/Mesoteleuton/Anadiplosis;
		   * 
		   * Mesarchia repeats the same word or words at the beginning and middle of successive sentences.
		   * Epiphoza repeats the same word or words at the end of successive sentences.
		   * Mesoteleuton repeats the same word or words in the middle and at the end of successive sentences.
		   * Anadiplosis repeats the last word (or phrase) from the previous line, clause, or sentence at the beginning of the next.
		   * 
		   *  @param jcas 
		   * 			- The Jcas to add the annotations to
		   *  @param sentences
		   *  			- The list of all the sentences in the document
		   *  @param stopwords
	       * 		    - The set of stopwords
		   *  @param type
		   *  			- Flag for selecting the desired type of annotations
		   *  				1 - annotate Mesarchia
		   *  				2 - annotate Epiphoza
		   *  				3 - annotate Mesoteleuton
		   *  				4 - annotate Anadiplosis
		   *   
		   */
				
			private void annotateMEMA(JCas jcas, List<Annotation> sentences, Set<String> stopwords, int type){
				
				List<Token> tokensSentence1 = new ArrayList<Token>();
				List<Token> tokensSentence2 = new ArrayList<Token>();
		    	List<String> firstWords1 = new ArrayList<String>();
		    	List<String> middleWords1 = new ArrayList<String>();
		    	List<String> lastWords1 = new ArrayList<String>();
		    	List<String> firstWords2 = new ArrayList<String>();
		    	List<String> middleWords2 = new ArrayList<String>();
		    	List<String> lastWords2 = new ArrayList<String>();
		    	List<String> firstWordsIntersection = new ArrayList<String>();
		    	List<String> middleWordsIntersection = new ArrayList<String>();
		    	List<String> lastWordsIntersection = new ArrayList<String>();
		    	
		    	int middleStartPos1, middleEndPos1, middleStartPos2, middleEndPos2 = 0;
		    	
		    	for (int i = 0; i < sentences.size()-1; i++) {
					
		    		firstWords1.clear();
		    		firstWords2.clear();
		    		middleWords1.clear();
		    		middleWords2.clear();
		    		lastWords1.clear();
		    		lastWords2.clear();
		    		tokensSentence1.clear();
		    		tokensSentence2.clear();
		    		
		    		Annotation sentence1 = sentences.get(i);
		    		Annotation sentence2 = sentences.get(i+1);
		    		
					int startOfDevice = sentence1.getBegin();
					int endOfDevice = sentence2.getEnd();
		    		
					//token iterator
					FSIterator<Annotation> tokenIter1 = 
							jcas.getAnnotationIndex(Token.type).subiterator(sentence1);
					//iterate over the tokens
					while (tokenIter1.hasNext()){
						tokensSentence1.add((Token) tokenIter1.next());
					}
					
					//token iterator
					FSIterator<Annotation> tokenIter2 = 
							jcas.getAnnotationIndex(Token.type).subiterator(sentence2);
					//iterate over the tokens
					while (tokenIter2.hasNext()){
						tokensSentence2.add((Token) tokenIter2.next());
					}
					
					//set start position of the middle part of the sentence to be
					//the beginning of the second quarter of the tokens list(no stopwords) 
					middleStartPos1 = ((int) Math.round(tokensSentence1.size()/4));
					middleStartPos2 = ((int) Math.round(tokensSentence2.size()/4));
					
					//set end position of the middle part of the sentence to be
					//the end of the third quarter of the tokens list(no stopwords)
					middleEndPos1 = ((int) Math.round(tokensSentence1.size()/4))*3;
					middleEndPos2 = ((int) Math.round(tokensSentence2.size()/4))*3;
					
					//get the first, middle and last tokens of sentence 1
					List<Token> firstTokens1 = new ArrayList<Token>(tokensSentence1.subList(0, middleStartPos1));
					List<Token> lastTokens1 = new ArrayList<Token>(tokensSentence1.subList(middleEndPos1, tokensSentence1.size()));
					List<Token> middleTokens1 = new ArrayList<Token>(tokensSentence1.subList(middleStartPos1, middleEndPos1));
					
					//get the first, middle and last tokens of sentence 2
					List<Token> firstTokens2 = new ArrayList<Token>(tokensSentence2.subList(0, middleStartPos2));
					List<Token> lastTokens2 = new ArrayList<Token>(tokensSentence2.subList(middleEndPos2, tokensSentence2.size()));
					List<Token> middleTokens2 = new ArrayList<Token>(tokensSentence2.subList(middleStartPos2, middleEndPos2));
					
					//pre-process tokens to eliminate the stopwords
					firstTokens1.forEach(token -> {
						if (!Pattern.matches("\\p{Punct}", token.getCoveredText())) {
//						if (token.getCoveredText().length()>1) {
							firstWords1.add(token.getCoveredText().toLowerCase());
					}});
					
					//collect all middle tokens (except punctuation marks) from sentence 1 
					middleTokens1.forEach(token -> {
						if (!Pattern.matches("\\p{Punct}", token.getCoveredText())) {
							middleWords1.add(token.getCoveredText().toLowerCase());
					}});
					//collect all last tokens (except punctuation marks) from sentence 1
					lastTokens1.forEach(token -> {
						if (!Pattern.matches("\\p{Punct}", token.getCoveredText())) {
							lastWords1.add(token.getCoveredText().toLowerCase());
					}});
					//collect all first tokens (except punctuation marks) from sentence 2
					firstTokens2.forEach(token -> {
						if (!Pattern.matches("\\p{Punct}", token.getCoveredText())) {
							firstWords2.add(token.getCoveredText().toLowerCase());
					}});
					//collect all middle tokens (except punctuation marks) from sentence 2
					middleTokens2.forEach(token -> {
						if (!Pattern.matches("\\p{Punct}", token.getCoveredText())) {
							middleWords2.add(token.getCoveredText().toLowerCase());
					}});
					//collect all last tokens (except punctuation marks) from sentence 2
					lastTokens2.forEach(token -> {
						if (!Pattern.matches("\\p{Punct}", token.getCoveredText())) {
							lastWords2.add(token.getCoveredText().toLowerCase());
					}});
					
					//ANNOTATION STEP
					//case of mesarchia
					if (type==1) {
						//collect first and middle words
						firstWordsIntersection = firstWords1.stream()
		                         .filter(firstWords2::contains)
		                         .collect(Collectors.toList());
						middleWordsIntersection = middleWords1.stream()
		                         .filter(middleWords2::contains)
		                         .collect(Collectors.toList());
						//annotate mesarchia if there is an intersection between first
						//and middle words of both sentences
						//if intersection is a single stopword - ignore it
						//if intersection is more than one stopword - annotate
						if (firstWordsIntersection.size() >= 2 && 
								middleWordsIntersection.size() >= 2) {
							RhetoricalDevice mesarchia = new RhetoricalDevice(jcas, startOfDevice, endOfDevice);
							mesarchia.setDeviceType("Mesarchia");
							mesarchia.setCategory("repetition");
							mesarchia.addToIndexes(jcas);
						}else if(firstWordsIntersection.size() == 1 && 
								middleWordsIntersection.size() >= 2){
							if(!stopwords.contains(firstWordsIntersection.get(0))){
								RhetoricalDevice mesarchia = new RhetoricalDevice(jcas, startOfDevice, endOfDevice);
								mesarchia.setDeviceType("Mesarchia");
								mesarchia.setCategory("repetition");
								mesarchia.addToIndexes(jcas);
							}
						} else if(firstWordsIntersection.size() >= 2 && 
								middleWordsIntersection.size() == 1){
							if(!stopwords.contains(middleWordsIntersection.get(0))){
								RhetoricalDevice mesarchia = new RhetoricalDevice(jcas, startOfDevice, endOfDevice);
								mesarchia.setDeviceType("Mesarchia");
								mesarchia.setCategory("repetition");
								mesarchia.addToIndexes(jcas);
							}
						} else if(firstWordsIntersection.size() == 1 && 
								middleWordsIntersection.size() == 1){
							if(!stopwords.contains(middleWordsIntersection.get(0)) && 
									!stopwords.contains(firstWordsIntersection.get(0))){
								RhetoricalDevice mesarchia = new RhetoricalDevice(jcas, startOfDevice, endOfDevice);
								mesarchia.setDeviceType("Mesarchia");
								mesarchia.setCategory("repetition");
								mesarchia.addToIndexes(jcas);
							}
						}
					}
					//case of epiphoza
					else if(type==2){
						//collect last words from both sentences
						lastWordsIntersection = lastWords1.stream()
		                         .filter(lastWords2::contains)
		                         .collect(Collectors.toList());
						if (lastWordsIntersection.size() >= 2) {
							RhetoricalDevice epiphoza = new RhetoricalDevice(jcas, startOfDevice, endOfDevice);
							epiphoza.setDeviceType("Epiphoza");
							epiphoza.setCategory("repetition");
							epiphoza.addToIndexes(jcas);
//							System.out.println("Epiphoza: "+sentence1.getCoveredText() + sentence2.getCoveredText());
						}
						//intersection is one word and it is not stopword
						else if(lastWordsIntersection.size() == 1){
							if(!stopwords.contains(lastWordsIntersection.get(0))){
								RhetoricalDevice epiphoza = new RhetoricalDevice(jcas, startOfDevice, endOfDevice);
								epiphoza.setDeviceType("Epiphoza");
								epiphoza.setCategory("repetition");
								epiphoza.addToIndexes(jcas);
							}
						}
					}
					//casee of mesodiplosis
					else if(type==3){
						//collect middle words from both sentences
						middleWordsIntersection = middleWords1.stream()
		                         .filter(middleWords2::contains)
		                         .collect(Collectors.toList());
						
						if (middleWordsIntersection.size() >= 2) {
							RhetoricalDevice mesoteleuton = new RhetoricalDevice(jcas, startOfDevice, endOfDevice);
							mesoteleuton.setDeviceType("Mesodiplosis");
							mesoteleuton.setCategory("repetition");
							mesoteleuton.addToIndexes(jcas);
						}
						//intersection is one word and it is not stopword
						else if(middleWordsIntersection.size() == 1){
							if(!stopwords.contains(middleWordsIntersection.get(0))){
								RhetoricalDevice mesoteleuton = new RhetoricalDevice(jcas, startOfDevice, endOfDevice);
								mesoteleuton.setDeviceType("Mesodiplosis");
								mesoteleuton.setCategory("repetition");
								mesoteleuton.addToIndexes(jcas);
							}
						}
					}
					//case of anadiplosis
					else if(type==4){
						//collect last words from both sentences
						lastWordsIntersection = lastWords1.stream()
		                         .filter(firstWords2::contains)
		                         .collect(Collectors.toList());
						if (lastWordsIntersection.size() >= 2) {
							RhetoricalDevice epadiplosis = new RhetoricalDevice(jcas, startOfDevice, endOfDevice);
							epadiplosis.setDeviceType("Anadiplosis");
							epadiplosis.setCategory("repetition");
							epadiplosis.addToIndexes(jcas);
						}
						//intersection is one word and it is not stopword
						else if(lastWordsIntersection.size() == 1){
							if(!stopwords.contains(lastWordsIntersection.get(0))){
								RhetoricalDevice epadiplosis = new RhetoricalDevice(jcas, startOfDevice, endOfDevice);
								epadiplosis.setDeviceType("Anadiplosis");
								epadiplosis.setCategory("repetition");
								epadiplosis.addToIndexes(jcas);
							}
						}
					}
					else{
						System.out.println("Invalid argument type! Please select between 1, 2, 3 or 4.");
					}
				}

			}		

			/**
			   * Creates annotations of comparative and superlative Adjectives/Adverbs;
			   * 
			   * This method is solely dependent on Stanford POS Tagger(v. 3.8.0) to annotate
			   * the words which are tagged as comparative and superlative adjectives/adverbs
			   * 
			   *  @param jcas 
			   * 			- The Jcas to add the annotations to
			   *  @param sentences
			   *  			- Currently analyzed sentence
			   *  @param taggedSentence
			   *  			- output tagged sentence from Stanford Tagger
			   *  @param deviceType
			   *  			- desired device type to annotate (i.e., 
			   *  				comparative Adjectives/Adverbs and superlative Adjectives/Adverbs)
			   *  				!IMPORTANT: this argument should be given as follows:
			   *  				for comparative Adjective - comparativeAdj
			   *  				for comparative Adverb    - comparativeAdv
			   *  				for superlative Adjective - superlativeAdj
			   *  				for superlative Adverb    - superlativeAdv
			   *  @param devicePOStag
			   *  			- the PennTreebank tag of the corresponding rhetorical device
			   *  				for comparative Adjective - JJR
			   *  				for comparative Adverb    - RBR
			   *  				for superlative Adjective - JJS
			   *  				for superlative Adverb    - RBS
			   *   
			   */
			private void annotateCompSuperAdjectiveAdverb(JCas jcas, Annotation sentence, 
					String taggedSentence, String deviceType, String devicePOStag){
				int startIdForNextOccurence = 0;
				int wordBegin = 0;
				int wordEnd = 0;
				String[] wordsPOS = taggedSentence.split("\\s+");
				for (int i = 0; i < wordsPOS.length; i++) {
					if (wordsPOS[i].contains(devicePOStag)) {
						//get the word alone, without its POS tag
						String word = wordsPOS[i].substring(0, wordsPOS[i].indexOf("_"));
						//get the starting position of the word in the document
						wordBegin = sentence.getCoveredText().
								indexOf(word, startIdForNextOccurence)+sentence.getBegin();
						//get the ending position of the word in the document
						wordEnd = wordBegin+word.length();
						//annotate the sentence
						RhetoricalDevice device = new RhetoricalDevice(jcas, sentence.getBegin(), sentence.getEnd());
						//annotate the word
//						RhetoricalDevice device = new RhetoricalDevice(jcas, wordBegin, wordEnd);
						device.setDeviceType(deviceType);
						device.setCategory("custom");
						device.addToIndexes(jcas);
						//update the starting index to find next occurrence in the sentence
						startIdForNextOccurence = wordBegin+1;
					}
					
				}
			}
			
	
	/**
	 * Creates annotation of Epizeugma;
	 * 
	 * Epizeugma - placing the verb that holds together the entire
	 *	sentence (made up of multiple parts that depend upon that verb) either at the
	 *	very beginning or the very ending of that sentence.
	 * 
	 * Example: Neither a borrower nor a lender be.
	 * 
	 * @param jcas 
	 * 			- The Jcas to add the annotations to
	 * @param constituents
	 *  			- The list with all the constituents in the current sentence
	 * @param sentence
	 *  			- The current sentence under analysis
	 */		
	
			private void annotateEpizeugma(JCas jcas, Annotation sentence, 
					List<Constituent> constituents){
				
				int startOfSentence = sentence.getBegin();
				int endOfSentence = sentence.getEnd();
				List<String> constituentsList = new ArrayList<String>();
				int middleStartPos, middleEndPos = 0;
				
				for (Constituent constituent : constituents) {
					if (constituent.getTreeDepth()==0) {
						constituentsList.add(constituent.getLabel());
					}
				}
					//set start position of the middle part of the sentence to be
					//the beginning of the second quarter of the tokens list(no stopwords) 
					middleStartPos = ((int) Math.round(constituentsList.size()/5));
					//set start position of the middle part of the sentence to be
					//the end of the third quarter of the tokens list(no stopwords)
					middleEndPos = ((int) Math.round(constituentsList.size()/5))*4;
					
					if (middleStartPos==0 && constituentsList.size()>=3) {
						middleStartPos=1;
						middleEndPos = constituentsList.size()-2;
					}

					List<String> firstConst = new ArrayList<String>(constituentsList.subList(0, middleStartPos));
					List<String> middleConst = new ArrayList<String>(constituentsList.subList(middleStartPos, middleEndPos));
					List<String> lastConst = new ArrayList<String>(constituentsList.subList(middleEndPos, constituentsList.size()));

					//ensure that the first part of the sentence doesn't contain verbs 
					if ((!Collections.disjoint(firstConst, CLAUSE_VERB_TYPES) && 
							(Collections.disjoint(lastConst, CLAUSE_VERB_TYPES) && 
									Collections.disjoint(middleConst, CLAUSE_VERB_TYPES))) ||
					//ensure that the last part of the sentence doesn't contain verbs		
							(!Collections.disjoint(lastConst, CLAUSE_VERB_TYPES) && 
							(Collections.disjoint(firstConst, CLAUSE_VERB_TYPES) && 
									Collections.disjoint(middleConst, CLAUSE_VERB_TYPES)))) {
						
						RhetoricalDevice epizeugma = new RhetoricalDevice(jcas, startOfSentence, endOfSentence);
						epizeugma.setDeviceType("Epizeugma");
						epizeugma.setCategory("omission");
						epizeugma.addToIndexes(jcas);
					}
			}
			
			
	/**
	   * Creates annotations of Diacope;
	   * Diacope - repetition of a word or phrase after an intervening 
	   * word or phrase as a method of emphasis.
	   * 
	   * Example: We will do it, I tell you; we will do it.
	   * 
	   *  @param jcas 
	   * 			- The Jcas to add the annotations to
	   *  @param tokens
	   *  			- The list with all the tokens in the current sentence
	   *  @param sentence
	   *  			- The current sentence under analysis
	   *  @param stopwords
	   *  			- The set of stopwords
	   */
			
			private void annotateDiacope(JCas jcas, List<Token> tokens, Annotation sentence, Set<String> stopwords, int interveningWordsWindow){
				
				int startOfSentence = sentence.getBegin();
				int endOfSentence = sentence.getEnd();
				int wordPairs = 0;
				List<String> tokensListToLowercase = new ArrayList<String>();
				String currString;
				int duplicateId;
				int currStringId;

				tokens.forEach(token -> {
					//filter our punctuation marks
					if (!Pattern.matches("\\p{Punct}", token.getCoveredText())) {
						tokensListToLowercase.add(token.getCoveredText().toLowerCase());
					}
				});
				
				for (int i = 0; i < tokensListToLowercase.size(); i++) {
					//filter out stopwords, non-alphanumeric chars and numbers
					if (!stopwords.contains(tokensListToLowercase.get(i)) && 
							tokensListToLowercase.get(i).matches("[A-Za-z]+")) {
						//store the current string
						currString = tokensListToLowercase.get(i);
						currStringId = tokensListToLowercase.indexOf(currString);
						//remove the current string from the list
						tokensListToLowercase.remove(currString);
						//decrement to compensate the removal
						i--;
						
						//check if repeating words exist
						if (tokensListToLowercase.contains(currString)) {
							//get the index of the repeated string
							//+1 to compensate the removal of the 'currString'
							duplicateId = tokensListToLowercase.indexOf(currString)+1;
							//check if the considered window doesn't exceed the length of the list with tokens
							if (currStringId+interveningWordsWindow+1<=tokensListToLowercase.size()) {
								//repeating word found if its index is within the specified window (one up to five tokens)
								if (duplicateId>currStringId+1 && duplicateId<currStringId+1+interveningWordsWindow) {
									wordPairs++;
								}
							//case when sentence is short consider the next duplicate as valid
							}else if(duplicateId>currStringId+1){
								wordPairs++;
							}
						} else {
							wordPairs = 0;
						}
						//annotate Diacope is there is at least one duplicate split by the intervening word or words
						if (wordPairs>0) {
							RhetoricalDevice diacope = new RhetoricalDevice(jcas, startOfSentence, endOfSentence);
							diacope.setDeviceType("Diacope");
							diacope.setCategory("repetition");
							diacope.addToIndexes(jcas);
							break;
						}
					}
				}
			}
		
		/**
		   * Creates the annotations of Whether...or and Unless conditional sentences; 
		   * It is a simple approach which is based solely on presence checking of "whether... or"
		   * and "unless" keywords in the sentence and their relative position with respect to each other
		   *  
		   * @param jcas 
		   * 			- The Jcas to add the annotations to
		   * @param sentence
		   * 			- The sentence under analysis
		   */
				
		private void annotateWhether_UnlessCond(JCas jcas, Annotation sentence){
		
			String whether= "whether";
			String or= "or";
			String unless = "unless";
			int beginOfSentence = sentence.getBegin();
			int endOfSentence = sentence.getEnd();
			String currSent = sentence.getCoveredText().toLowerCase();

			//check if the sentence contains the keywords("whether" and "or")
			//isMatch function tests the sentence for an exact match of the whole keyword in it
			if (isMatch(currSent, whether) != -1 &&
					isMatch(currSent, or) != -1) {
				int whetherInd = isMatch(currSent, whether);
				int orInd = isMatch(currSent, or);
				//annotate WhetherConditional if "or" keyword is placed after "whether"
				if (whetherInd<orInd) {
					RhetoricalDevice whetherCond = new RhetoricalDevice(jcas, beginOfSentence, endOfSentence);
					whetherCond.setDeviceType("WhetherConditional");
					whetherCond.setCategory("custom");
					whetherCond.addToIndexes(jcas);
				}
			}else if(isMatch(currSent, unless) != -1){
				RhetoricalDevice unlessCond = new RhetoricalDevice(jcas, beginOfSentence, endOfSentence);
				unlessCond.setDeviceType("UnlessConditional");
				unlessCond.setCategory("custom");
				unlessCond.addToIndexes(jcas);
			}
		}
		
		
		/**
		   * Creates annotations of Epizeuxis; 
		   * 
		   * Epizeuxis - repetition of a word or phrase in immediate succession, 
		   * typically within the same sentence, for vehemence or emphasis.
		   * 
		   * Example: The best way to describe this portion of South America is lush, lush, lush.
		   * 
		   *  @param jcas 
		   * 			- The Jcas to add the annotations to
		   *  @param tokens
		   * 			- The list of sentence tokens
		   *  @param sentence
		   *  			- The current sentence under analysis
		   */
				
		private void annotateEpizeuxis(JCas jcas, Annotation sentence, List<Token> tokens){
			
			List<Token> tokensList = new ArrayList<Token>(tokens);
			List<String> epizeuxisConst = new ArrayList<String>();
			int startOfSentence = sentence.getBegin();
			int endOfSentence = sentence.getEnd();
			
			boolean flagFirstIter = false;
			for (int i = 0; i < tokensList.size(); i++) {
				//remove non-alphanumeric chars
				if(Pattern.matches("\\p{Punct}", tokensList.get(i).getCoveredText())){
					tokensList.remove(i);
				}
			}
			for (int tokId = 0; tokId+1 < tokensList.size(); tokId++) {
				//check if the current token is identical to the next one
				if (tokensList.get(tokId).getCoveredText().toLowerCase().
					equals(tokensList.get(tokId+1).getCoveredText().toLowerCase())) {
					if (!flagFirstIter) {
						//add the starting token to the list
						epizeuxisConst.add(tokensList.get(tokId).getCoveredText().toLowerCase());
						flagFirstIter = true;
					}
					//ad the next tokens
					epizeuxisConst.add(tokensList.get(tokId+1).getCoveredText().toLowerCase());
				}
			}
			//annotate sentence with Epizeuxis if the list of epizeuxis constituents in not empty
			if (!epizeuxisConst.isEmpty()) {
				RhetoricalDevice epizeuxis = new RhetoricalDevice(jcas, startOfSentence, endOfSentence);
				epizeuxis.setDeviceType("Epizeuxis");
				epizeuxis.setCategory("repetition");
				epizeuxis.addToIndexes(jcas);
			}
		}

		/**
		   * Creates annotations of Isocolon; 
		   * 
		   * Isocolon - rhetorical device that involves a succession of sentences, 
		   * phrases and clauses of grammatically equal length.
		   * 
		   * Examples of isocolon may fall under any of the following types:
		   * 
		   * 	Bicolon: has two grammatically equal structures. 
		   * 			  An example for this is Harley Davidson's slogan "American by Birth. Rebel by Choice."
		   * 
		   * 	Tricolon: If there are three grammatically equal structures, it is called a tricolon. 
		   * 			   Such as: "That government of the people, by the people, and for the people 
		   * 			   shall not perish from the earth." (Abraham Lincoln)
		   * 
		   * 	Tetracolon: "I'll give my jewels for a set of beads, /My gorgeous palace for a hermitage, 
		   * 				  /My gay apparel for an almsman's gown, /My figured goblets for a dish of wood" 
		   * 				  (Richard II by William Shakespeare). 
		   * 				  This is an example of tetracolon, where four parallel grammatical 
		   * 				  structures are written in succession.
		   *  
		   *  @param jcas 
		   * 			- The Jcas to add the annotations to
		   *  @param sentences
		   * 			- The list of sentences under analysis;
		   * 				It works on the sentence level(checks for Isocolon inside one sentence) 
		   * 				as well as across multiple sentences given as arguments (2->bicolon, 3->tricolon, 4->tetracolon)
		   * @param deviationFactor
		   * 			- Sets the allowed deviation from the length of the reference sentence
		   * 				(by default - 2, i.e. input sentences will be checked for isocolon iff 
		   * 			  	all sentences length lie in the interval [starting sentence length - 2, 
		   * 			  	starting sentence length + 2] )
		   *  
		   *  @return true  - if isocolon identified
		   *  		  false - otherwise
		   */
				
		private boolean annotateIsocolon(JCas jcas, int deviationFactor, int isocolonMinCoverage, Annotation... sentences){
			
			List<String> prevTags = new ArrayList<String>();
			List<String> matchingLabelsList = new ArrayList<String>();
			List<Constituent> constituents = new ArrayList<Constituent>();
			List<Constituent> leafConstituents = new ArrayList<Constituent>();
			List<Integer> countConstInSentence = new ArrayList<Integer>();
			List<Integer> indicesOfDuplicates = new ArrayList<Integer>();
			ArrayList<List<Constituent>> listOfConst = new ArrayList<List<Constituent>>();
			
			String prevLabel = "";
			String currentLabel = "";
			int counter = 0;
			int type = 0;
			int constId = -1;
			int matchingLabels = 0;
			int coverage = 0;
			boolean firstIter = true;
			
			//check if the number of arguments cover the set of types(i.e. bicolon, tricolon, tetracolon)
			if (sentences.length>0 && sentences.length<5) {
				//iterator over arguments and get the list of constituents for each
				for (int i = 0; i < sentences.length; i++) {
					constituents.clear();
					leafConstituents.clear();
					
					//constituent iterator
					FSIterator<Annotation> constIter = 
							jcas.getAnnotationIndex(Constituent.type).subiterator(sentences[i]);
					//iterate over the constituents
					while (constIter.hasNext()){
						constituents.add((Constituent) constIter.next());
					}
					listOfConst.add(constituents);
					//collect leaf nodes of the parse tree
					constituents.forEach(label->{
						if(label.getTreeDepth()==0) {
							leafConstituents.add(label);
					}});
					//count number of leaf nodes in each sentence
					//to check the correspondence
					countConstInSentence.add(leafConstituents.size());
				}
			}
			else{
				System.out.println("Invalid number of arguments.");
			}
			//iterate over the lists of constituents
			for (int i = 0; i < listOfConst.size(); i++) {
				counter = 0;
				constId = -1;
				//create and keep a list of labels each next sentence to be compared with
				if (listOfConst.size()>1 && firstIter) {
					listOfConst.get(i).forEach(constituent->{
						if (constituent.getTreeDepth()==0) {
							//add modified label to previous tags list
							prevTags.add(label2SinglePOSType(constituent));
					}});
					firstIter = false;
					//check for condition fulfilment: 
					//the bi-tri-tetra-colons should consist of approximately equal sentences
					int lowerBound = countConstInSentence.get(0) - deviationFactor;
					int upperBound = countConstInSentence.get(0) + deviationFactor;
					for (int j = 1; j < countConstInSentence.size(); j++) {
						//fail detection if any of the sentences under analysis
						//lies outside of the allowed interval
						if (lowerBound > countConstInSentence.get(j) ||
								countConstInSentence.get(j) > upperBound) {
							return false;
						}
					}
				}
				else{
					//set the number of duplicate consecutive labels depending on 
					//the total number of the constituents in the sentence 
					if (listOfConst.get(i).size()<25) {
						matchingLabels = 1;
					}
					else{
						matchingLabels = 2;
					}
					//iterate over sentence consituents
					for (Constituent constituent : listOfConst.get(i)) {
						if (constituent.getTreeDepth()==0) {
							//reset constituent label to unique types of nouns and verbs
							currentLabel = label2SinglePOSType(constituent);	
							//check counter value at each COMMA since it works 
							//as a separator in a set of parallel clauses
							if (currentLabel.equals(",") || currentLabel.equals(":")) {
								if (counter>matchingLabels) {
									type++;
									//System.out.println("[comma]Counter: "+counter+" Type: "+type);
								}
								counter=0;
//								System.out.println("counter comma value: "+counter);
								continue;
							}
							//System.out.println("Constituent label: "+constituent.getLabel());
							
							//check if the current constituent label is contained by the clause/sentence it is compared against
							if (prevTags.contains(currentLabel)) {
//								System.out.println("prevTags:"+Arrays.toString(prevTags.toArray()));
//								System.out.println("Contained tag: "+constituent.getLabel()+" counter: "+counter+" type: "+type);
								//setting constituent id contained in the prevTags list
								//it is used as a reference point for the next labels that follow
								
								int currConstId = 0;
								//get the indices of the duplicate tags
								indicesOfDuplicates = getDuplicateIndices(prevTags, currentLabel);
								int k=0;
								//System.out.println("indicesOfDuplicates size: "+indicesOfDuplicates.size());
								if (constId == -1) {
									counter++;
//									System.out.println("Contained tag: "+currentLabel+" counter: "+counter+" at currConstId not set");
									constId = prevTags.indexOf(currentLabel);	
								}
								//if the reference point(label with which parallel 
								//clause/sentence starts) already exists
								else{
									do{
										//get the index of the current label in the list of previous tags
										//after the reference point in that list
										currConstId = indexStartingAt(prevTags, constId, currentLabel);
										//check iteratively each label in the list of duplicates(if any)
										constId = indicesOfDuplicates.get(k);
										k++;
										//break if the current label index is lower than one
										//to allow checking the while condition
										if (currConstId<1) {
//											System.out.println("Fail in if break");
											break;
										}
										else{
//											System.out.println("Try "+k+" currConstId "+currConstId+"constId "+constId+" indicesOfDuplicates size: "+indicesOfDuplicates+" main condition: "+prevTags.get(currConstId-1).equals(prevLabel));
										}
									}
									//ensure that the index of the current constituent(currConstId) is correctly 
									//determined(because duplicate labels may alter this) by checking if the last 
									//checked constituent has the index decremented by 1
									while(!(prevTags.get(currConstId-1).equals(prevLabel) || k>=indicesOfDuplicates.size()));
									//if the index of the current constituent(currConstId) is not correctly 
									//identified -> reset the reference point and counter
									if (currConstId<1) {
										if (counter>matchingLabels) {
											type++;
//											System.out.println("type increment in fail if: "+type);
										}
										counter=0;
										constId=-1;
										//build the list with the previous tags on-the-fly for isocolon inside the sentence
										if (listOfConst.size()==1) {
											prevTags.add(currentLabel);
										}
										continue;
									}
									//if the index of the current label is correctly determined
									//increment the match counter and update the reference point 'constId'
									else if(prevTags.get(currConstId-1).equals(prevLabel)) {
										counter++;
										constId = currConstId;
									}
									else{
										if (counter>matchingLabels) {
											type++;
										}
										counter=0;
										constId=-1;
									}
								}
							}
							else{
								//number of matched labels
								if (counter>matchingLabels) {
									type++;
								}
								counter=0;
								constId = -1;
							}
							//build the list with the previous tags on-the-fly for isocolon inside the sentence
							if (listOfConst.size()==1) {
								prevTags.add(currentLabel);
							}
							if (matchingLabels==1) {
								if (counter>=1) {
									matchingLabelsList.add(currentLabel);
								}
							} else {
								if (counter>=2) {
									if (counter==2) {
										matchingLabelsList.add(prevLabel);
									}
									matchingLabelsList.add(currentLabel);
								}
							}
							//save the last checked constituent label
							prevLabel = currentLabel;
						}
					}
				}

				//check the number of matches if the sentence ended
				if(counter>matchingLabels){
					type++;
				}
				//ANNOTATE INPUT SENTENCE AS ISOCOLON
				//single sentence
				if (listOfConst.size()==1) {
					//bicolon
					if (type==1) {
						coverage = getIsocolonCoverage(type, prevTags.size(), matchingLabelsList.size());
						if (coverage>=isocolonMinCoverage) {
							RhetoricalDevice bicolon = new RhetoricalDevice(jcas, sentences[0].getBegin(), sentences[0].getEnd());
							bicolon.setDeviceType("Isocolon");
							bicolon.setCategory("parallelism");
							bicolon.addToIndexes(jcas);
//							System.out.println("Bicolon in: "+sentences[0].getCoveredText());
//							System.out.println("prevTags:"+Arrays.toString(prevTags.toArray()));
//							PlainText2XMIConverter.isocolonCoverage.add(getIsocolonCoverage(type, prevTags.size(), matchingLabelsList.size()));
//							System.out.println("matching lables:"+ matchingLabelsList.size()+" prevTags: "+prevTags.size());
//							System.out.println("matching labels:"+Arrays.toString(matchingLabelsList.toArray()));
						}
					//tricolon
					} else if (type==2) {
						coverage = getIsocolonCoverage(type, prevTags.size(), matchingLabelsList.size());
						if (coverage>=isocolonMinCoverage) {
							RhetoricalDevice tricolon = new RhetoricalDevice(jcas, sentences[0].getBegin(), sentences[0].getEnd());
							tricolon.setDeviceType("Isocolon");
							tricolon.setCategory("parallelism");
							tricolon.addToIndexes(jcas);
						}
					//tetracolon
					} else if (type==3){
						coverage = getIsocolonCoverage(type, prevTags.size(), matchingLabelsList.size());
						if (coverage>=isocolonMinCoverage) {
							RhetoricalDevice tetracolon = new RhetoricalDevice(jcas, sentences[0].getBegin(), sentences[0].getEnd());
							tetracolon.setDeviceType("Isocolon");
							tetracolon.setCategory("parallelism");
							tetracolon.addToIndexes(jcas);
						}
					}
				}
				//multiple sentences - bicolon
				else if(listOfConst.size()==2 && type==1){
					coverage = getIsocolonCoverage(type, prevTags.size()*2, matchingLabelsList.size());
					if (coverage>=isocolonMinCoverage) {
						RhetoricalDevice bicolon = new RhetoricalDevice(jcas, sentences[0].getBegin(), sentences[1].getEnd());
						bicolon.setDeviceType("Isocolon");
						bicolon.setCategory("parallelism");
						bicolon.addToIndexes(jcas);
						return true;
					}

				}
				//multiple sentences - tricolon
				else if(listOfConst.size()==3 && type==2){
					coverage = getIsocolonCoverage(type, prevTags.size()*3, matchingLabelsList.size());
					if (coverage>=isocolonMinCoverage) {
						RhetoricalDevice tricolon = new RhetoricalDevice(jcas, sentences[0].getBegin(), sentences[2].getEnd());
						tricolon.setDeviceType("Isocolon");
						tricolon.setCategory("parallelism");
						tricolon.addToIndexes(jcas);
						return true;
					}

				}
				//multiple sentences - tetracolon
				else if(listOfConst.size()==4 && type>=3){
					coverage = getIsocolonCoverage(type, prevTags.size()*4, matchingLabelsList.size());
					if (coverage>=isocolonMinCoverage) {
						RhetoricalDevice tetracolon = new RhetoricalDevice(jcas, sentences[0].getBegin(), sentences[3].getEnd());
						tetracolon.setDeviceType("Isocolon");
						tetracolon.setCategory("parallelism");
						tetracolon.addToIndexes(jcas);
						return true;
					}

				}
			}
			return false;
		}
		
		/**
		 * Method to establish the coverage of equivalent structures in an isocolon.
		 * A high coverage increases the reliability of the detected instance. 
		*/
		private int getIsocolonCoverage(int type, int sentenceAllLabelsCount, int sentenceMatchingLabelsCount ){
			
			int sentenceAllMatchingLabelsCount = 0;
			int coverage = 0;
			//bicolon
			if (type==1) {
				sentenceAllMatchingLabelsCount = sentenceMatchingLabelsCount*2;
				coverage = ((int)Math.round((sentenceAllMatchingLabelsCount*100)/sentenceAllLabelsCount));
			}
			//tricolon
			else if(type==2){
				sentenceAllMatchingLabelsCount = sentenceMatchingLabelsCount+sentenceMatchingLabelsCount/2;
				coverage = ((int)Math.round((sentenceAllMatchingLabelsCount*100)/sentenceAllLabelsCount));
			}
			//tetracolon
			else if(type>=3){
				sentenceAllMatchingLabelsCount = sentenceMatchingLabelsCount+sentenceMatchingLabelsCount/3;
				coverage = ((int)Math.round((sentenceAllMatchingLabelsCount*100)/sentenceAllLabelsCount));
			}
			return coverage;
		}
		
		
		/**
		 * Annotator of Ploche as rhetorical device
		 * 
		 * !!! NOT USED --> very similar to Diacope
		 * 
		 * Ploche - The repetition of a single word for rhetorical emphasis.
		 * Example: Bread is bread indeed to a hungry, stomach. (JG Smith)
		 *
		 * @param jcas
		 * 			    - The Jcas to add the annotations to
		 * @param sentence 
		 * 				- The sentence under analysis
		 * @param tokens 
		 * 				- The list of sentence tokens
		 * @param stopwords 
		 * 				- The set with all the stopwords
		 */
		 
	    private void annotatePloche(JCas jcas, Annotation sentence, List<Token> tokens, Set<String> stopwords){
	    	
	    	List<String> tokensNoStopwords = new ArrayList<String>();
	    	int startOfSentence = sentence.getBegin();
	    	int endOfSentence = sentence.getEnd();
	    	
	    	for (Token token : tokens) {
	    		//check for presence of token in the stopwords list and for punctuation
				if (!stopwords.contains(token.getCoveredText().toLowerCase()) &&
						!Pattern.matches("\\p{Punct}", token.getCoveredText())) {
					tokensNoStopwords.add(token.getCoveredText().toLowerCase());
				}
			}
	    	
	    	Set<String> tokensNoStopwordsSet = new HashSet<String>(tokensNoStopwords);
	    	Map<String, Integer> mapTokensFrequency = new HashMap<String, Integer>();
	    	
	    	//determine frequency of each token in the current sentence and add to map
	    	for (String token : tokensNoStopwordsSet) {
	    		mapTokensFrequency.put(token, Collections.frequency(tokensNoStopwords, token));
	    	}
	    	//use TreeMap to sort in descending order
	    	TreeMap<String, Integer> tokensFreqOrdered = new TreeMap<String, Integer>(Collections.reverseOrder());
	    	tokensFreqOrdered.putAll(mapTokensFrequency);
	    	
	    	//Ploche detected if the first value in the TreeMap is greater than 1
	    	try {
				if (tokensFreqOrdered.get(tokensFreqOrdered.firstKey())>1) {
					RhetoricalDevice ploche = new RhetoricalDevice(jcas, startOfSentence, endOfSentence);
					ploche.setDeviceType("Ploche");
					ploche.setCategory("repetition");
					ploche.addToIndexes(jcas);
					System.out.println("Ploche: "+sentence.getCoveredText());
				}
			} catch (NoSuchElementException e) {
				System.out.println("Failed to get the first key in the map.");
				//e.printStackTrace();
			}
		 }
	  	
    
	// -------------------------------------------------------------------------
	// HELPER
	// -------------------------------------------------------------------------
    
	/**
	   * Search an element in a list starting at a specified first element in that list;
	   * It is used in annotateIsocolon method to get the index of the next duplicate label
	   * in the list of checked labels
	   * 
	   *  @param list 
	   * 			- The list to search in
	   *  @param start
	   * 			- The index of the element to start at
	   *  @param elem
	   * 			- The label to look for in the list
	   *  
	   *  @return index of the searched element in the list
	   *  		  -1 if no element was found
	   */

	public static int indexStartingAt(List<?> list, int start, String elem) {
	    int index = list.subList(start, list.size()).indexOf(elem);
	    return index != -1 ? index + start : -1;
	}
	
	
	/**
	   * Get the indices of the duplicate element in the list;
	   * It is used in annotateIsocolon method
	   * 
	   *  @param list 
	   * 			- The list with duplicates
	   *  @param elem
	   * 			- The duplicate element
	   *  
	   *  @return index of the duplicate element in the list
	   */
	public static List<Integer> getDuplicateIndices(List<String> list, String elem){
		List<Integer> indicesOfDuplicates = new ArrayList<Integer>();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).equals(elem)) {
				indicesOfDuplicates.add(i);
			}
		}
		return indicesOfDuplicates;
	}
	
	/**
	   * Reset Constituent label to unique types of nouns and verbs (for Isocolon);
	   * This is used to eliminate the confusion during the check of correspondence between clauses/sentences in Isocolon
	   * since two entities might be parallel even with different verb/noun types
	   * 
	   *  @param constituent
	   * 			- The constituent whose label to reset
	   * 
	   *  @return the corrected constituent label
	   */
	
	private String label2SinglePOSType(Constituent constituent){

		//correct label
		if (CLAUSE_VERB_TYPES.contains(constituent.getLabel())) {
			return "VB";
		}
		else if(CLAUSE_NOUN_TYPES.contains(constituent.getLabel())){
			return "NN";
		}
		else if(constituent.getLabel().equals("JJR")){
			return "JJ";
		}
		else if(constituent.getLabel().equals("RBR")){
			return "RB";
		}
		return constituent.getLabel();
	}
    
	/**
	  * Gets a list with all the governors in a sentence based on the nsubj relation
	  * evoked by the Stanford Dependency Parser
	  * It is used in IfConditional annotation method
	  * 
	  * @param jcas 
	  * 			- The Jcas to get the tokens from
	  * @param sentence
	  * 			- The sentence based on which the dependencies were computed
	  * 
	  * @return governors
	  * 			- The list containing all the governors in the checked sentence
	  */
	
	private List<String> getGovernors(JCas jcas, Annotation sentence){
		
		List<Token> tokens = new ArrayList<Token>();
		List<String> governors = new ArrayList<String>();
		
		FSIterator<Annotation> tokenIter = 
				jcas.getAnnotationIndex(Token.type).subiterator(sentence);
		//iterate over the tokens
		while (tokenIter.hasNext()){
			tokens.add((Token) tokenIter.next());
		}
		//Iterate over sentence tokens to catch the governors
		for (Token token : tokens) {
			//governors from nsubj, nsubjpass, csubj and csubjpass relations are valid
			//for more info visit http://nlp.stanford.edu/software/dependencies_manual.pdf
			if (token.getDepLabel() == "nsubj" || token.getDepLabel() == "nsubjpass" ||
					token.getDepLabel() == "csubj" || token.getDepLabel() == "csubjpass") {
				String parent = token.getParent().getCoveredText();
				//check if governor consists of letters
				if (parent.matches("[a-zA-Z]+")) {
					governors.add(parent);
				}
			}
		}
		return governors;
	}
	
	/**
	  * Sorts the extracted governors based on their distance from the conditional clause
	  * (i.e. the governor closest to the "if " particle and following after it will be the head of the list)
	  * It is used in IfConditional annotation method;
	  * 
	  * @param sentence 
	  * 			- The sentence based on which the dependencies were computed
	  * @param ifParticle
	  * 			- The "if " token
	  * @param governors
	  * 			- The list of governors
	  * 
	  * @return sortedVerbs
	  * 			- The list containing the governors based on their distance from the conditional clause
	  */
	
	private List<String> sortGovernors(String sentence, String ifParticle, List<String> governors){
		
		Map<Integer, String> gov2Index = new HashMap<Integer, String>(); 
		List<String> sortedVerbs = new ArrayList<String>(); 
		boolean flag = true;
		int govIndex = 0;
		for (String gov : governors) {
			//System.out.println("Governor: "+gov);
			//check if the governor follows after the "if" particle
			if (sentence.indexOf(gov, sentence.indexOf(ifParticle)) != -1){
				//store the governor index
				if (flag) {
					//first entry in the map is the closest governor to the 'if' particle
					govIndex = sentence.indexOf(gov, sentence.indexOf(ifParticle)) - 
							sentence.indexOf(ifParticle);
					govIndex = 0;
					gov2Index.put(govIndex, gov);
					flag = false;
				} else {
					govIndex = sentence.indexOf(gov, sentence.indexOf(ifParticle)) - 
							sentence.indexOf(ifParticle);
					gov2Index.put(govIndex, gov);
				}
				
				
			}
			//if the governor is placed before the "if" particle in the sentence
			//its index would be a high value
			else{
				govIndex = sentence.indexOf(ifParticle) - sentence.indexOf(gov);
				gov2Index.put(govIndex, gov);
				
			}
		}
		
		//TreeMap for sorting the keys
		Map<Integer, String> treeMap = new TreeMap<Integer, String>(gov2Index);
/*		for (Map.Entry<Integer, String> entry : treeMap.entrySet()) {
			System.out.println("\nTree map entries: "+entry);
		}*/
		for (String str : treeMap.values()) {
		    sortedVerbs.add(str);    
		}
		return sortedVerbs;
	}

    
	/**
	 * Checks whether the sentence contains the exact match of the subterm
	 *
	 * @param sentence - The sentence
	 * @param subterm - The current term
	 * @return index of the term - if sentence contains the tested term
	 * 		   -1 - otherwise
	 */
	
	private int isMatch(String sentence, String subterm){
		String pattern = "\\b"+subterm+"\\b";
        Pattern p=Pattern.compile(pattern);
        Matcher m=p.matcher(sentence);
        if(m.find()){
        	return m.start();
        }
        else{
        	return -1;
        }
	}
	
	/**
	 * Finds contracted forms of auxilliary verbs in a sentence and replaces them
	 * with the corresponding long forms.
	 * \S - is used as wildcard to match any non-white-space character
	 *
	 * @param sentence - The sentence under analysis
	 * 
	 * @return the sentence with long forms of verbs
	 */
	
	public String getAuxLongForm(String sentence){
		
		sentence = sentence.replaceAll("I['’`]m", "I am");
		sentence = sentence.replaceAll("You['’`]re", "You are");
		sentence = sentence.replaceAll("Aren['’`]t", "Are not");
		sentence = sentence.replaceAll("He['’`]s", "He is");
		sentence = sentence.replaceAll("Isn['’`]t", "Is not");
		sentence = sentence.replaceAll("She['’`]s", "She is");
		sentence = sentence.replaceAll("It['’`]s", "It is");
		sentence = sentence.replaceAll("We['’`]re", "We are");
		sentence = sentence.replaceAll("They['’`]re", "They are");
		sentence = sentence.replaceAll("I['’`]ve", "I have");
		sentence = sentence.replaceAll("You['’`]ve", "You have");
		sentence = sentence.replaceAll("We['’`]ve", "We have");
		sentence = sentence.replaceAll("They['’`]ve", "They have");
		sentence = sentence.replaceAll("I['’`]d", "I had");
		sentence = sentence.replaceAll("You['’`]d", "You had");
		sentence = sentence.replaceAll("He['’`]d", "He had");
		sentence = sentence.replaceAll("She['’`]d", "She had");
		sentence = sentence.replaceAll("It['’`]d", "It had");
		sentence = sentence.replaceAll("We['’`]d", "We had");
		sentence = sentence.replaceAll("They['’`]d", "They had");
		sentence = sentence.replaceAll("Wasn['’`]t", "Was not");
		sentence = sentence.replaceAll("Weren['’`]t", "Were not");
		sentence = sentence.replaceAll("We['’`]ve", "We have");
		sentence = sentence.replaceAll("Haven['’`]t", "Have not");
		sentence = sentence.replaceAll("Hasn['’`]t", "Has not");
		sentence = sentence.replaceAll("Hadn['’`]t", "Had not");
		sentence = sentence.replaceAll("Don['’`]t", "Do not");
		sentence = sentence.replaceAll("Doesn['’`]t", "Does not");
		sentence = sentence.replaceAll("Didn['’`]t", "Did not");
		sentence = sentence.replaceAll("Can['’`]t", "Cannot");
		sentence = sentence.replaceAll("Couldn['’`]t", "Could not");
		sentence = sentence.replaceAll("Mustn['’`]t", "Must not");
		sentence = sentence.replaceAll("Needn['’`]t", "Need not");
		sentence = sentence.replaceAll("Won['’`]t", "Will not");
		sentence = sentence.replaceAll("Wouldn['’`]t", "Would not");
		sentence = sentence.replaceAll("Shan['’`]t", "Shall not");
		sentence = sentence.replaceAll("Shouldn['’`]t", "Should not");
		sentence = sentence.replaceAll("Oughtn['’`]t", "Ought not");
		sentence = sentence.replaceAll("There['’`]s", "There is");
		sentence = sentence.replaceAll("That['’`]s", "That is");
		sentence = sentence.replaceAll("I['’`]ll", "I will");
		sentence = sentence.replaceAll("You['’`]ll", "You will");
		sentence = sentence.replaceAll("He['’`]ll", "He will");
		sentence = sentence.replaceAll("She['’`]ll", "She will");
		sentence = sentence.replaceAll("It['’`]ll", "It will");
		sentence = sentence.replaceAll("We['’`]ll", "We will");
		sentence = sentence.replaceAll("They['’`]ll", "They will");
		sentence = sentence.replaceAll("Who['’`]s", "Who is");
		sentence = sentence.replaceAll("Where['’`]s", "Where is");
		sentence = sentence.replaceAll("What['’`]s", "What is");
		sentence = sentence.replaceAll("Let['’`]s", "Let us");
		sentence = sentence.replaceAll("Could['’`]ve", "Could have");
		sentence = sentence.replaceAll("Might['’`]ve", "Might have");
		sentence = sentence.replaceAll("Must['’`]ve", "Must have");
		sentence = sentence.replaceAll("Should['’`]ve", "Should have");
		sentence = sentence.replaceAll("Would['’`]ve", "Would have");
		
		
		sentence = sentence.replaceAll("i['’`]m", "i am");
		sentence = sentence.replaceAll("you['’`]re", "you are");
		sentence = sentence.replaceAll("aren['’`]t", "are not");
		sentence = sentence.replaceAll("he['’`]s", "he is");
		sentence = sentence.replaceAll("isn['’`]t", "is not");
		sentence = sentence.replaceAll("she['’`]s", "she is");
		sentence = sentence.replaceAll("it['’`]s", "it is");
		sentence = sentence.replaceAll("we['’`]re", "we are");
		sentence = sentence.replaceAll("they['’`]re", "they are");
		sentence = sentence.replaceAll("i['’`]ve", "i have");
		sentence = sentence.replaceAll("you['’`]ve", "you have");
		sentence = sentence.replaceAll("we['’`]ve", "we have");
		sentence = sentence.replaceAll("they['’`]ve", "they have");
		sentence = sentence.replaceAll("i['’`]d", "i had");
		sentence = sentence.replaceAll("you['’`]d", "you had");
		sentence = sentence.replaceAll("he['’`]d", "he had");
		sentence = sentence.replaceAll("she['’`]d", "she had");
		sentence = sentence.replaceAll("it['’`]d", "it had");
		sentence = sentence.replaceAll("we['’`]d", "we had");
		sentence = sentence.replaceAll("they['’`]d", "they had");
		sentence = sentence.replaceAll("wasn['’`]t", "was not");
		sentence = sentence.replaceAll("weren['’`]t", "were not");
		sentence = sentence.replaceAll("we['’`]ve", "we have");
		sentence = sentence.replaceAll("haven['’`]t", "have not");
		sentence = sentence.replaceAll("hasn['’`]t", "has not");
		sentence = sentence.replaceAll("hadn['’`]t", "had not");
		sentence = sentence.replaceAll("don['’`]t", "do not");
		sentence = sentence.replaceAll("doesn['’`]t", "does not");
		sentence = sentence.replaceAll("didn['’`]t", "did not");
		sentence = sentence.replaceAll("can['’`]t", "cannot");
		sentence = sentence.replaceAll("couldn['’`]t", "could not");
		sentence = sentence.replaceAll("mustn['’`]t", "must not");
		sentence = sentence.replaceAll("needn['’`]t", "need not");
		sentence = sentence.replaceAll("won['’`]t", "will not");
		sentence = sentence.replaceAll("wouldn['’`]t", "would not");
		sentence = sentence.replaceAll("shan['’`]t", "shall not");
		sentence = sentence.replaceAll("shouldn['’`]t", "should not");
		sentence = sentence.replaceAll("oughtn['’`]t", "ought not");
		sentence = sentence.replaceAll("there['’`]s", "there is");
		sentence = sentence.replaceAll("that['’`]s", "that is");
		sentence = sentence.replaceAll("i['’`]ll", "i will");
		sentence = sentence.replaceAll("you['’`]ll", "you will");
		sentence = sentence.replaceAll("he['’`]ll", "he will");
		sentence = sentence.replaceAll("she['’`]ll", "she will");
		sentence = sentence.replaceAll("it['’`]ll", "it will");
		sentence = sentence.replaceAll("we['’`]ll", "we will");
		sentence = sentence.replaceAll("they['’`]ll", "they will");
		sentence = sentence.replaceAll("who['’`]s", "who is");
		sentence = sentence.replaceAll("where['’`]s", "where is");
		sentence = sentence.replaceAll("what['’`]s", "what is");
		sentence = sentence.replaceAll("let['’`]s", "let us");
		sentence = sentence.replaceAll("could['’`]ve", "could have");
		sentence = sentence.replaceAll("might['’`]ve", "might have");
		sentence = sentence.replaceAll("must['’`]ve", "must have");
		sentence = sentence.replaceAll("should['’`]ve", "should have");
		sentence = sentence.replaceAll("would['’`]ve", "would have");
				
		return sentence;
	}

}
