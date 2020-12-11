package rhetoricDetection.aitools;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import de.aitools.ie.uima.type.arguana.Title;
import de.aitools.ie.uima.type.core.Paragraph;
import de.aitools.ie.uima.type.core.Sentence;


/**
 * Very simple and efficient paragraph splitter that defines a paragraph to end 
 * with a paragraph separator or a double line separator. Conversely, a single 
 * line break is not seen as a paragraph end.
 * 
 * Does not require any input annotations and produces paragraph annotations.
 * 
 * Works with texts from all domains and of all languages that split paragraphs 
 * as specified.
 * 
 * @author henning.wachsmuth
 *
 */
public class InfexBAParagraphSplitter extends JCasAnnotator_ImplBase {

	// -------------------------------------------------------------------------
	// UIMA PARAMETERS
	// -------------------------------------------------------------------------
	
	/**
	 * Parameter for the model file
	 */
	public static final String PARAM_MODEL_FILE = "DoubleLineBreaks";
	
	

	// -------------------------------------------------------------------------
	// VALUES
	// -------------------------------------------------------------------------
	
	/**
	 * Whether or not to only split paragraphs at double line breaks.
	 */
	private boolean onlySplitAtDoubleLineBreaks;
	
	

	// -------------------------------------------------------------------------
	// INITIALIZATION
	// -------------------------------------------------------------------------
	
	@Override
	public void initialize(UimaContext aContext) 
	throws ResourceInitializationException {
		super.initialize(aContext);
		this.onlySplitAtDoubleLineBreaks = 
			(Boolean) aContext.getConfigParameterValue(PARAM_MODEL_FILE);
	}	
	
	
	
	// -------------------------------------------------------------------------
	// PROCESSING
	// -------------------------------------------------------------------------
	
	@Override
	public void process(JCas jcas) {
		
		int endOfTitle = 0;
		char[] textContent = jcas.getDocumentText().toCharArray();
		
		//title iterator
		FSIterator<Annotation> titleIter = 
				jcas.getAnnotationIndex(Title.type).iterator();
		while (titleIter.hasNext()){
			Annotation title = titleIter.next();
			endOfTitle = title.getEnd();
		}
		
        // Start at first non-whitespace character after the title
        int paragraphBegin = endOfTitle;
        while (paragraphBegin < textContent.length && 
        	   Character.isWhitespace(textContent[paragraphBegin]))
			paragraphBegin++;
        int paragraphEnd = paragraphBegin+1;
        
        // Find all paragraphs
        while (paragraphEnd < textContent.length-1){	
        	if (this.characterIndicatesParagraphSeparator(textContent, 
        			paragraphEnd)){
        		// Create paragraph
        		Annotation paragraph = new Paragraph(jcas, paragraphBegin, 
        				paragraphEnd);
        		paragraph.addToIndexes();
        		
        		// Continue at next non-whitespace character
        		while (paragraphEnd < textContent.length-1 &&
        				Character.isWhitespace(textContent[paragraphEnd]))
        			paragraphEnd++;
        		paragraphBegin = paragraphEnd;
        	}
        	paragraphEnd++;
        }   
        if (paragraphBegin < paragraphEnd-1){
            // Create final paragraph
    		Annotation paragraph = new Paragraph(jcas, paragraphBegin, 
    				paragraphEnd+1);
    		paragraph.addToIndexes();
        }
	}
	
	/**
	 * Determines whether the character of the given text with index i
	 * is the first character of a paragraph separator.
	 * 
	 * @param text The text to be analyzed
	 * @param i The index of character
	 * @return Whether the character with index i indicates a paragraph 
	 *   separator
	 */
	private boolean characterIndicatesParagraphSeparator(char[] text, int i){
		// Character is end of text
		if (i+1 >= text.length)
			return true;
		
		// Split only at double line breaks
		if (this.onlySplitAtDoubleLineBreaks){
			// Paragraph separator with carriage return AND line feed
			if (text[i] == '\r' && text[i+1] == '\n'){
				int j = i+2;
				while (j< text.length && Character.isSpaceChar(text[j]))
					j++;
				if (j >= text.length-1) return true;
				return (text[j] == '\r' && text[j+1] == '\n');
			}
			// Paragraph separator with either Carriage Return OR Line Feed
			else if (text[i] == '\r' || text[i] == '\n'){
				char cur = text[i];
				int j = i+1;
				while (j< text.length && Character.isSpaceChar(text[j]))
					j++;
				if (j >= text.length) return true;
				return (text[j] == cur);
			}
		}
		// Split at each line break
		else{
			if (text[i] == '\n' && text[i+1] == '\r'){
				return true;
			}
		}
		// Not a paragraph separator
		return false;
	}
}
