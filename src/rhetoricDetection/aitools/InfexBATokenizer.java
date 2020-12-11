package rhetoricDetection.aitools;

import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import de.aitools.ie.uima.io.LexiconLoader;
import de.aitools.ie.uima.io.LowercaseTransformation;
import de.aitools.ie.uima.type.core.Sentence;
import de.aitools.ie.uima.type.core.Token;




/**
 * Effective but still efficient rule-based tokenizer that was developed 
 * in the InfexBA project and improved in the ArguAna project later on.
 * 
 * Requires sentence annotations and produces token annotations.
 * 
 * Targets at news articles primarily, but should also work well with less 
 * well-formatted texts likes reviews. Works with any whitespace-separated 
 * language, but uses a language-specific abbreviation lexicon, which can be set 
 * via a respective parameter. 
 * 
 * The essential rules for classifying whether to split the tokens at the 
 * current text position are:
 * <ol>
 *   <li>IF next character is whitespace
 *   	THEN split after current character</LI>
 *   <LI>IF next character is end of sentence
 *   	THEN split after current character</LI>
 *   
 *   <LI>IF current character is a letter
 *   	AND NOT next character is a letter or digit
 *   	AND NOT current character belongs to a URL
 *   	THEN
 *   	<OL>
 *   		<LI>IF NOT next character is hyphen or period
 *   			THEN split after current character</LI>
 *   		<LI>IF next character is hyphen 
 *   			AND NOT previous character is whitespace
 *   			THEN split after current character</LI>
 *   		<LI>IF next character is period
 *   			AND NOT previous character is whitespace
 *   			AND NOT period belongs to an abbreviation
 *   			THEN split after current character</LI>
 *   	</OL>
 *   
 *   <LI>IF current character is a digit
 *   	THEN
 *   	<OL>
 *   		<LI>IF next character is a comma
 *   			AND NOT next but one character is a digit
 *   			THEN split after current character</LI>
 *   		<LI>IF next character is a period
 *   			AND next but one is end of sentence
 *   			THEN split after current character</LI>
 *   		<LI>IF NOT next character is letter, digit, comma, or period
 *   			AND NOT current character belongs to a URL
 *   			THEN split after current character</LI>
 *   	</OL>
 *   
 *   <LI>IF current character is a hyphen
 *   	AND NOT current character belongs to a URL
 *   	AND NOT current character is first character of token
 *   	THEN split after current character</LI>
 *   
 *   <LI>IF current character is a period
 *   	AND NOT next character is a letter or digit
 *   	AND NOT current character belongs to a URL
 *   	THEN split after current character</LI>
 *   
 *   <LI>IF NOT current character is a letter, digit, comma, or period
 *   	AND NOT current character belongs to a URL
 *   	THEN split after current character</LI>
 * </ol>
 * 
 * @author henning.wachsmuth
 *
 */
public class InfexBATokenizer extends JCasAnnotator_ImplBase{

	// -------------------------------------------------------------------------
	// PARAMETERS
	// -------------------------------------------------------------------------
	
	/**
	 * The name of the parameter for the URL prefix lexicon
	 */
	public static final String PARAM_LEXICON_URL = 
			"URLPrefixLexicon";
	
	/**
	 * The name of the parameter for the abbreviation lexicon
	 */
	public static final String PARAM_LEXICON_ABBREVIATIONS = 
			"AbbreviationLexicon";
	
	

	// -------------------------------------------------------------------------
	// LEXICA
	// -------------------------------------------------------------------------
	
	/**
	 * The URL prefix lexicon
	 */
	private Set<String> urlPrefixLexicon;
	
	/**
	 * The abbreviation lexicon
	 */
	private Set<String> abbreviationLexicon;
		
	
	
	// -------------------------------------------------------------------------
	// INITIALIZATION
	// -------------------------------------------------------------------------
	
	@Override
	public void initialize(UimaContext aContext) 
	throws ResourceInitializationException {
		super.initialize(aContext);
		
		LexiconLoader loader = new LexiconLoader().withTransformation(new LowercaseTransformation());
		
		// Load lexicons
		String urlPath = 
				(String) aContext.getConfigParameterValue(PARAM_LEXICON_URL);
		String abbreviationPath = 
				(String) aContext.getConfigParameterValue(
						PARAM_LEXICON_ABBREVIATIONS);
		urlPrefixLexicon = loader.load(urlPath);
		abbreviationLexicon = loader.load(abbreviationPath);
	}
	
	
	// -------------------------------------------------------------------------
	// PROCESSING
	// -------------------------------------------------------------------------
	
	@Override
	public void process(JCas jcas){
		FSIterator<Annotation> sentenceIter = 
				jcas.getAnnotationIndex(Sentence.type).iterator();
		while (sentenceIter.hasNext()){
			Annotation sentence = sentenceIter.next();
			this.tokenizeSentence(jcas, sentence);
		}
	}
	
	/**
	 * Splits the given sentence of the given JCas object into tokens. That 
	 * is, it creates a new annotation for every found token and adds it to the 
	 * indices. Whitespaces are skipped.
	 * 
	 * @param jcas The JCas object
	 * @param sentence The sentence
	 */
	private void tokenizeSentence(JCas jcas, Annotation sentence){
		int tokenBegin = sentence.getBegin();
		// Skip maybe possible whitespace at beginning of sentence
		while (tokenBegin < sentence.getEnd() && Character.isWhitespace(
						jcas.getDocumentText().charAt(tokenBegin)))
			tokenBegin++;
		while (tokenBegin < sentence.getEnd()){
			// Compute next token
			int tokenEnd = getEndOfToken(jcas, sentence, tokenBegin);
			Annotation token = createToken(jcas, tokenBegin, tokenEnd);
			token.addToIndexes();
			// Skip all whitespace characters between two tokens
			tokenBegin = token.getEnd();
			while (tokenBegin < sentence.getEnd() && Character.isWhitespace(
					jcas.getDocumentText().charAt(tokenBegin)))
				tokenBegin++;
		}
	}
	
	/**
	 * Determines the end index of the token starting at the given 
	 * index <code>begin</code>. This method based on several heuristic rules. 
	 * Notice that most rules implicitly use the notion that a token just ended
	 * beforehand.
	 *  
	 * @param jcas The JCas object
	 * @param sentence The sentence to find the next token in
	 * @param begin The start index of the sentence to be found
	 * @return The end index of the current token
	 */
	private int getEndOfToken(JCas jcas, Annotation sentence, int begin){
		String text = jcas.getDocumentText();
		boolean isURL = false;
		
		// Search for end index of current token
		for (int end = begin; end<sentence.getEnd(); end++){
			// Set characters
			char cur = text.charAt(end); 
			char succ = ' ';
			if (end<sentence.getEnd()-1) succ = text.charAt(end+1);
			char succ2 = ' ';
			if (end<sentence.getEnd()-2) succ2 = text.charAt(end+2);
			char prev = ' ';
			if (end>0) prev = text.charAt(end-1);
			

			// Always split if whitespace follows or if end of sentence
			if (Character.isWhitespace(succ) || end == sentence.getEnd()-1) 
				return end+1;
			
			// URL has special rule
			if (!Character.isSpaceChar(succ2) 
					&& urlPrefixLexicon.contains(text.substring(begin, end+2)))
				isURL = true;
			
			// Maybe Split after letter if special character follows and token 
			// does not refer to a URL
			if (Character.isLetter(cur) && !Character.isLetterOrDigit(succ) 
					&& !isURL){
				// Split if following character is neither period nor hyphen
				if (!this.isHyphen(succ) && succ != '.')
					return end+1;
				// Split if hyphen follows and letter belongs to a word
				else if (this.isHyphen(succ) && !Character.isWhitespace(prev))
					return end+1;
				// Split if period follows and letter belongs to a 
				// non-abbreviation word
				else if (succ == '.' && !Character.isWhitespace(prev) 
						&& !periodBelongsToAnAcronym(text, sentence, end+1)){
					return end+1;
				}
			}
			
			// Split after digit...
			else if (Character.isDigit(cur)){
				// ... if comma follows but does not belong to number
				if (succ == ',' && !Character.isDigit(succ2)) 
					return end+1;
				// ... or if period follows that does not refer to an ordinal 
				//     number
				if (succ == '.' && sentence.getEnd() == end+2)
					return end+1;
				// ... or if digit is followed by a special character that does 
				//     not belong to a url
				else if (!Character.isLetterOrDigit(succ) 
						&& succ != ',' && succ != '.' && !isURL)
					return end+1;
			}
			
			// Split after hyphen if it does not connect a single character to 
			// the following text		
			else if (this.isHyphen(cur) && !isURL){
				if (begin+1 != end) return end+1;
			}
			
			// Split after period if special character follows
			else if (cur == '.' && !Character.isDigit(succ) && !isURL)
				return end+1;
			
			
			// Mail address is interpreted like a URL
			else if (succ == '@') 
				isURL = true;
			
			// Split after a non-comma special character if it does not belong 
			// to a URL
			else if (!Character.isLetterOrDigit(cur) && cur != ',' && cur != '.' 
					&& !isURL)
				return end+1;
		}
		return sentence.getEnd();
	}
	
	
	/**
	 * Creates and returns a token annotation.
	 * @param jcas The JCas object
	 * @param begin The begin index of the token
	 * @param end The end index of the token
	 * @return The token
	 */
	private Annotation createToken(JCas jcas, int begin, int end){
			return new Token(jcas, begin, end);
	}
	
	/**
	 * Determines whether the period at index <code>i</code> belongs to an 
	 * acronym that is on the used acronym list.
	 * 
	 * @param text The text to be analyzed
	 * @param i The index of the period
	 * @return <code>true</code> iff. period belongs to an acronym
	 */
	private boolean periodBelongsToAnAcronym(String text, Annotation sentence, 
			int i){
		while (i < sentence.getEnd() && 
				(Character.isLetter(text.charAt(i)) || text.charAt(i) == '.'))
			i++;
		int begin = i-1;
		while(begin > 0 
				&& (Character.isLetter(text.charAt(begin)) 
						|| text.charAt(begin) == '.'))
			begin--;
		return this.abbreviationLexicon.contains(text.substring(begin+1,i));	
	}

	
	
	// -------------------------------------------------------------------------
	// PROCESSING
	// -------------------------------------------------------------------------

	/**
	 * Determines whether <code>c</code> is one of the
	 * versions of hyphens.
	 * 
	 * @param c The character
	 * @return <code>true</code> iff. <code>c</code> is a hyphen
	 */
	private boolean isHyphen(char c){
		return c == '-' || c == '\u2012' || c == '\u2013' || c == '\u2014' || 
				c == '\u2015';
	}
}
