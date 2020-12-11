package rhetoricDetection.features;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.jcas.tcas.DocumentAnnotation;

import de.aitools.ie.uima.feature.IFeatureType;
import de.aitools.ie.uima.type.arguana.Body;
import de.aitools.ie.uima.type.core.Paragraph;
import de.aitools.ie.uima.type.core.Sentence;

/**
 * Feature type for the existing rhetorical devices(RD) in text. RD can extend 
 * across a single as well as multiple sentences.  
 * 
 * The position is measured using 12 features that answer the following 
 * questions:
 * <UL>
 *   <LI>Is the sentence covering the annotation the {1st|2nd|last} sentence in 
 *       the whole text?</LI>
 *   <LI>What is the position of the sentence in the whole text, relative to 
 *       all other sentences in the paragraph?</LI>     
 *   <LI>Is the sentence covering the annotation the {1st|2nd|last} sentence in 
 *       the surrounding paragraph?</LI>
 *   <LI>What is the position of the sentence in the surrounding paragraph, 
 *       relative to all other sentences in the paragraph?</LI>    
 *   <LI>Is the paragraph covering the sentence with the annotation the 
 *       {1st|2nd|last} paragraph in the whole text?</LI>
 *   <LI>What is the position of the paragraph in the whole text, 
 *       relative to all other paragraph?</LI>       
 * </UL>
 * 
 * @author henning.wachsmuth
 *
 */
public class DevicesInText implements IFeatureType{

	// -------------------------------------------------------------------------
	// CONSTANTS
	// -------------------------------------------------------------------------
	
	/**
	 * The affix of the names of features of this type
	 */
	private static final String FEATURE_AFFIX = "SentencePosition_";
	
	
	
	// -------------------------------------------------------------------------
	// PROPERTIES
	// -------------------------------------------------------------------------

	/**
	 * Property key of title skipping
	 */
	private static final String PROPS_SKIP_TITLE = "sentenceposition_skiptitle";
	
	
	
	// -------------------------------------------------------------------------
	// VALUES AND STORAGE
	// -------------------------------------------------------------------------

	/**
	 * The names of the features, i.e., an ID for each value of the flow
	 */
	private List<String> featureNames;
	
	/**
	 * Whether or not to skip the title of texts
	 */
	private boolean skipTitle;
	
	
	
	// -------------------------------------------------------------------------
	// INTERFACE IMPLEMENTATION
	// -------------------------------------------------------------------------
	
	@Override
	public void initializeFeatureDetermination(Properties configurationProps){
		this.featureNames = new ArrayList<String>();
		
		this.featureNames.add(FEATURE_AFFIX + "1stSentenceInText");
		this.featureNames.add(FEATURE_AFFIX + "2ndSentenceInText");
		this.featureNames.add(FEATURE_AFFIX + "lastSentenceInText");
		this.featureNames.add(FEATURE_AFFIX + "relSentenceInText");
		
		this.featureNames.add(FEATURE_AFFIX + "1stSentenceInParagraph");
		this.featureNames.add(FEATURE_AFFIX + "2ndSentenceInParagraph");
		this.featureNames.add(FEATURE_AFFIX + "lastSentenceInParagraph");
		this.featureNames.add(FEATURE_AFFIX + "relSentenceInParagraph");

		this.featureNames.add(FEATURE_AFFIX + "1stParagraphInText");
		this.featureNames.add(FEATURE_AFFIX + "2ndParagraphInText");
		this.featureNames.add(FEATURE_AFFIX + "lastParagraphInText");
		this.featureNames.add(FEATURE_AFFIX + "relParagraphInText");
	}

	@Override
	public void updateCandidateFeatures(JCas jcas, int start, int end) {
		// No op, because the feature set is fixed
	}
	
	@Override
	public List<String> determineFeatures(Properties configurationProps, 
			Properties normalizationProps){
		// No op, because the feature set is fixed
		return featureNames;
	}
	
	@Override
	public void initializeFeatureComputation(List<String> allFeatureNames, 
			Properties configurationProps, Properties normalizationProps){
		this.skipTitle = Boolean.parseBoolean(
				configurationProps.getProperty(PROPS_SKIP_TITLE));
	}
	
	@Override
	public List<Double> computeNormalizedFeatureValues(JCas jcas, int start,
			int end) {
		// First get relevant span
		Annotation relevantSpan = 
				new Annotation(jcas, 0, jcas.getDocumentText().length());
		int spanType = DocumentAnnotation.type;
		if (skipTitle){
			spanType = Body.type;
		} 
		FSIterator<Annotation> relevantSpanIter = 
				jcas.getAnnotationIndex(spanType).iterator();
		if (relevantSpanIter.hasNext()){
			relevantSpan = relevantSpanIter.next();
		}
		
		// Next, get number of sentences and paragraphs and relevant indices
		double sentences = 0.0;
		double sentenceIndex = 0.0;
		FSIterator<Annotation> sentenceIter = 
			jcas.getAnnotationIndex(Sentence.type).subiterator(relevantSpan);
		while (sentenceIter.hasNext()){
			Annotation sentence = sentenceIter.next();
			sentences++;
			if (sentence.getBegin() <= start && sentence.getEnd() >= end){
				sentenceIndex = sentences;
			}
		}
		double paragraphs = 0.0;
		double paragraphIndex = 0.0;
		double senInPars = 0.0;
		double senInParIndex = 0.0;
		FSIterator<Annotation> paragraphIter = 
			jcas.getAnnotationIndex(Paragraph.type).subiterator(relevantSpan);
		while (paragraphIter.hasNext()){
			Annotation paragraph = paragraphIter.next();
			paragraphs++;
			if (paragraph.getBegin() <= start && paragraph.getEnd() >= end){
				paragraphIndex = paragraphs;
				// Found paragraph, now find sentence in paragraph
				FSIterator<Annotation> senInParIter = jcas.getAnnotationIndex(
						Sentence.type).subiterator(paragraph);
				while (senInParIter.hasNext()){
					Annotation sentence = senInParIter.next();
					senInPars++;
					if (sentence.getBegin() <= start && 
							sentence.getEnd() >= end){
						senInParIndex = senInPars;
					}
				} 
			}
		}
		
		List<Double> featureValues = new ArrayList<Double>();	
		// Features: Sentence in text
		if (sentences == 0.0){
			featureValues.add(0.0);
			featureValues.add(0.0); 
			featureValues.add(0.0); 
			featureValues.add(0.0); 
		} else if (sentences == 1.0){
			featureValues.add(1.0);
			featureValues.add(0.0); 
			featureValues.add(1.0); 
			featureValues.add(1.0); 
		} else{
			if (sentenceIndex == 1.0){
				featureValues.add(1.0);
			} else featureValues.add(0.0);
			if (sentenceIndex == 2.0){
				featureValues.add(1.0);
			} else featureValues.add(0.0);
			if (sentenceIndex == sentences){
				featureValues.add(1.0);
			} else featureValues.add(0.0);
			featureValues.add(sentenceIndex / sentences);
		}
		
		// Features: Sentence in paragraph
		if (senInPars == 0.0){
			featureValues.add(0.0);
			featureValues.add(0.0); 
			featureValues.add(0.0); 
			featureValues.add(0.0); 
		} else if (senInPars == 1.0){
			featureValues.add(1.0);
			featureValues.add(0.0); 
			featureValues.add(1.0); 
			featureValues.add(1.0); 
		} else{
			if (senInParIndex == 1.0){
				featureValues.add(1.0);
			} else featureValues.add(0.0);
			if (senInParIndex == 2.0){
				featureValues.add(1.0);
			} else featureValues.add(0.0);
			if (senInParIndex == senInPars){
				featureValues.add(1.0);
			} else featureValues.add(0.0);
			featureValues.add(senInParIndex / senInPars);
		}
		
		// Features: Paragraph in text
		if (paragraphs == 0.0){
			featureValues.add(0.0);
			featureValues.add(0.0); 
			featureValues.add(0.0); 
			featureValues.add(0.0); 
		} else if (paragraphs == 1.0){
			featureValues.add(1.0);
			featureValues.add(0.0); 
			featureValues.add(1.0); 
			featureValues.add(1.0); 
		} else{
			if (paragraphIndex == 1.0){
				featureValues.add(1.0);
			} else featureValues.add(0.0);
			if (paragraphIndex == 2.0){
				featureValues.add(1.0);
			} else featureValues.add(0.0);
			if (paragraphIndex == paragraphs){
				featureValues.add(1.0);
			} else featureValues.add(0.0);
			featureValues.add(paragraphIndex / paragraphs);
		}

		return featureValues;
	}
}
