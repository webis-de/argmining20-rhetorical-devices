package rhetoricDetection.aitools;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import de.aitools.ie.uima.type.arguana.Body;
import de.aitools.ie.uima.type.arguana.Title;


/**
 * Very simple and efficient splitter that assumes the beginning of the text as 
 * a title ending before the first sequence of two line separators. The rest 
 * after these separators is assumed to be the body of the text.
 * 
 * Does not require any input annotations and produces title and body 
 * annotations.
 * 
 * The algorithm has been specifically developed for the English hotel reviews 
 * from the ArguAnA TripAdvisor corpus (Wachsmuth et. al., CICLing 2014), but 
 * works with all texts that conform with the mentioned assumptions.
 * 
 * @author henning.wachsmuth
 *
 */
public class ArguAnaTitleAndBodySplitter extends JCasAnnotator_ImplBase {	
	
	// -------------------------------------------------------------------------
	// INITIALIZATION
	// -------------------------------------------------------------------------
	
	@Override
	public void initialize(UimaContext aContext) 
	throws ResourceInitializationException {
		super.initialize(aContext);
	}
	
	
	
	// -------------------------------------------------------------------------
	// PROCESSING
	// -------------------------------------------------------------------------
	
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		// Don't do anything if corpus already contains title annotations
		FSIterator<Annotation> titleIter = 
			jcas.getAnnotationIndex(Title.type).iterator();
		if (titleIter.hasNext())
			return;
		
		// Get start and end of text
		String text = jcas.getDocumentText();
		int beginOfText = 0;
		while (beginOfText < text.length() && 
				Character.isWhitespace(text.charAt(beginOfText)))
			beginOfText++;
		int endOfText = text.length();
		while (endOfText > 0 && 
				Character.isWhitespace(text.charAt(endOfText-1)))
			endOfText--;
		
		String [] segments = text.split("\n");
		String [] titleLines = segments[0].split("\n");
		// No title if there is only one segment or if title has too many lines
		if (segments.length == 1 || titleLines.length > 2){
			// All is body
			Annotation body = new Body(jcas, beginOfText, endOfText);
			body.addToIndexes(jcas);
		} else{
			// End index is end of first segment
			int endOfTitle = segments[0].length();
			// Create title
			Annotation title = new Title(jcas, beginOfText, endOfTitle);
			title.addToIndexes(jcas);
			// Now find start of body
			int beginOfBody = endOfTitle+1;
			while (beginOfBody < text.length() && 
					Character.isWhitespace(text.charAt(beginOfBody)))
				beginOfBody++;
			// Create body
			if (beginOfBody < endOfText){
				Annotation body = new Body(jcas, beginOfBody, endOfText);
				body.addToIndexes(jcas);
			}
		}
	}
}	



















