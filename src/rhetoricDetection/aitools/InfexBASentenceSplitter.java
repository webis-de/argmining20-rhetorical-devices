package rhetoricDetection.aitools;

import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import de.aitools.ie.uima.io.LexiconLoader;
import de.aitools.ie.uima.io.LowercaseTransformation;
import de.aitools.ie.uima.type.core.Sentence;

/**
 * Effective but still efficient rule-based sentence splitter that was 
 * developed in the InfexBA project and improved in the ArguAna project later 
 * on. 
 * 
 * Does not require any input annotations and produces sentence annotations.
 * 
 * Targets at news articles primarily, but should also work well with less 
 * well-formatted texts likes reviews. Works with any whitespace-separated 
 * language, but uses a language-specific abbreviation lexicon, which can be
 * set via a respective parameter. 
 * 
 * <UL>
 * <LI><B>IF</B> the current character is a period, which...<BR> 
 * 	... does not belong to a single character, <B>AND</B><BR>
 * 	... does not belong to an ordinal number, <B>AND</B><BR>
 * 	... does not belong to an abbreviation <B>AND</B><BR>
 * 	... does not belong to a URL <B>AND</B><BR>
 * 	... does not belong to an ellipsis that is not followed by an upper-case 
 *      character <B>AND</B><BR>
 * 	... is not followed by a closing bracket <B>AND</B><BR>
 * 	... is not followed by a comma<BR>
 * <B>THEN</B> the sentence ends after the period or its possibly succeeding 
 *             quotation mark.</LI>
 * 
 * <LI><B>IF</B> the current character is a question or an exclamation mark, 
 *               which...
 *   <BR>
 * 	... does not belong to a URL <B>AND</B><BR>
 * 	... is not followed by any sentence end mark <B>AND</B><BR>
 * 	... is not followed by a closing bracket <B>AND</B><BR>
 * 	... is not followed by a comma<BR>
 * <B>THEN</B> the sentence ends after the period or its possibly succeeding 
 *             quotation mark.</LI>
 * 
 * <LI><B>IF</B> the following character is a line break, which is...<BR> 
 * 	... preceded by a line that is completely in brackets <B>OR</B><BR>
 * 	... preceded by a line that is completely in upper-case <B>OR</B><BR>
 * 	... preceded by a line that does not start with a word <B>OR</B><BR>
 * 	... preceded by a line that ends with a URL <B>OR</B><BR>
 * 	... preceded by a line that ends with a sequence of hyphens <B>OR</B><BR>
 * 	... followed by a line that starts with a bulletpoint <B>OR</B><BR>
 * 	... followed by a line that is a blank line<BR>
 * <B>THEN</B> the sentence ends after the preceding line.</LI>
 * </UL>
 * 
 * @author henning.wachsmuth
 *
 */
public class InfexBASentenceSplitter extends JCasAnnotator_ImplBase{
    
	// -------------------------------------------------------------------------
	// PARAMETERS
	// -------------------------------------------------------------------------
	
	/**
	 * The name of the parameter for the URL prefix lexicon
	 */
	public static final String PARAM_LEXICON_PREFIX = "URLPrefixLexicon";

	/**
	 * The name of the parameter for the URL suffix lexicon
	 */
	public static final String PARAM_LEXICON_SUFFIX = "URLSuffixLexicon";
	
	/**
	 * The name of the parameter for the abbreviation lexicon
	 */
	public static final String PARAM_LEXICON_ABBREVS = "AbbreviationLexicon";
	
	/**
	 * The name of the parameter that specifies to always split after a line
	 * break.
	 */
	public static final String PARAM_SPLIT_AFTER_ALL_BREAKS = 
			"SplitAfterAllLineBreaks";

	
	// -------------------------------------------------------------------------
	// CONSTANTS
	// -------------------------------------------------------------------------
	
	/**
	 * Lower bound of range in which numbers are assumed to be possible
	 * ordinal numbers.
	 */
	private static final int ORDINAL_LOWER_BOUND = 1;
	
	/**
	 * Upper bound of range in which numbers are assumed to be possible
	 * ordinal numbers.
	 */
	private static final int ORDINAL_UPPER_BOUND = 100;

	/**
	 * The character assumed to be after the last character of a
	 * document.
	 */
	private static final char TEXT_END_CHARACTER = '\n';
	
	

	// -------------------------------------------------------------------------
	// VALUES AND REFERENCES
	// -------------------------------------------------------------------------
	
	/**
	 * The URL prefix lexicon
	 */
	private Set<String> urlPrefixLexicon;
	
	/**
	 * The URL suffix lexicon
	 */
	private Set<String> urlSuffixLexicon;
	
	/**
	 * The abbreviation lexicon
	 */
	private Set<String> abbreviationLexicon;
		
	/**
	 * Indicates whether to split after all line breaks (true) or only those
	 * that fulfill some additional properties (false).
	 */
	private boolean splitAfterAllLineBreaks;
	

	// -------------------------------------------------------------------------
	// INITIALIZATION
	// -------------------------------------------------------------------------
	
	@Override
	public void initialize(UimaContext aContext) 
	throws ResourceInitializationException {
		super.initialize(aContext);

		LexiconLoader loader = new LexiconLoader();
		LexiconLoader lowerLoader = new LexiconLoader().withTransformation(new LowercaseTransformation());
		
		// Load lexicons
		String prefixPath = 
				(String) aContext.getConfigParameterValue(PARAM_LEXICON_PREFIX);
		String suffixPath = 
				(String) aContext.getConfigParameterValue(PARAM_LEXICON_SUFFIX);
		String abbreviationPath = 
			(String) aContext.getConfigParameterValue(PARAM_LEXICON_ABBREVS);
		this.urlPrefixLexicon = lowerLoader.load(prefixPath);
		this.urlSuffixLexicon = lowerLoader.load(suffixPath);
		this.abbreviationLexicon = loader.load(abbreviationPath);
		// Load other values
		this.splitAfterAllLineBreaks = (Boolean) 
				aContext.getConfigParameterValue(PARAM_SPLIT_AFTER_ALL_BREAKS);
	}
	

	// -------------------------------------------------------------------------
	// PROCESSING
	// -------------------------------------------------------------------------
	
	@Override
	public void process(JCas jcas){
		// Convert to array for efficiency
        char[] text = jcas.getDocumentText().toCharArray();
		// Skip leading whitespaces
		int index = 0;
		while (index < text.length && Character.isWhitespace(text[index]))
			index++;
		// Now find all sentences
		while (index < text.length-1){
			int end = determineSentenceEnd(text, index);
			// [CHANGE] from August 2015: Trim sentences at the end
			while (end > 0 && Character.isWhitespace(text[end-1])){
				end--;
			}
			Annotation sentence = new Sentence(jcas, index, end);
			sentence.addToIndexes();
			// Skip every space between two sentences
			index = end;
			while (index < text.length && Character.isWhitespace(text[index])){
				index++;
			}
		}
	}

	/**
	 * Determines the end of the sentence of the given text that starts at the
	 * given index "begin".
	 *  
	 * @param text The text as a char array
	 * @param begin The start index of the sentence to be found
	 * @return A sentence annotation
	 */
	private int determineSentenceEnd(char[] text, int begin){
		// Helper for special cases: upper case, url, and bracketed sentences
		boolean isUpperCase = Character.isUpperCase(text[begin]);
		boolean isURL = isStartOfURL(text, begin);
		boolean isInBrackets = this.isOpeningBracket(text[begin]);
		
		// Search for end index of current sentence
		for (int end = begin; end<text.length-1; end++){
			char cur = text[end]; 
			char succ = TEXT_END_CHARACTER;
			if (end<text.length-1) 
				succ = text[end+1];

			// Maybe Split after period
			if (cur == '.' 
					&& !this.periodBelongsToSingleCharacter(text, end)
					&& !this.periodBelongsToAnOrdinalNumber(text, end)
					&& !this.periodBelongsToAnAcronym(text, end)
					&& !isURL
					&& !this.isClosingBracket(succ)
					&& !(succ == ',')){
				// Split if period does not belong to an ellipsis...
				if (!this.periodBelongsToAnEllipsis(text, end)
						// ... or the next non-space character is upper-case
						|| this.upperCaseFollows(text, end+1)){
					// Include possible end quotation mark in sentence
					if (this.isQuotationMark(succ)) 
						return end+2;
					else return end+1;
				}
			}
			
			// Maybe split after unambiguous end mark
			if (this.isUmambiguousEndMark(cur)
					&& !this.isFullStop(succ)
					&& !this.isClosingBracket(succ)
					&& !(succ == ',')
					&& !isURL){
				if (this.isQuotationMark(succ)) 
					return end+2;
				else return end+1;
			}

			// Maybe split because of following line break
			if (Character.isWhitespace(succ) && !Character.isSpaceChar(succ)){
				// [CHANGE] from September 2015: Always split if param says so
				if (this.splitAfterAllLineBreaks
						// Split if this sentence is an upper case sentence...
						|| isUpperCase		
						// ... or if it is preceded by a URL
						|| isURL 
						// ... or it does not start with a word (e.g. a date...)
						|| (!Character.isLetter(text[begin]) 
								&& !this.isQuotationMark(text[begin]))
						// ... or it is completely bracketed
						|| (isInBrackets && this.isClosingBracket(cur))
						// ... or it is a title line...	
						|| this.nextLineStartsWithBullet(text, end+1)
						// ... or next line is another bulletpoint...
						|| (this.isBullet(text[begin]) 
								&& this.nextLineStartsWithSameCharacter(text, 
										text[begin], end))
						// ... or next line is a blank line
						|| this.nextLineIsBlankLine(text, end)
						// ... it ends with a sequence of hyphens
						|| this.precededByHyphens(text, end)
						){	
					// Go back to last letter (etc.) and split
					while (end > 0 && Character.isWhitespace(text[end]))
						end -=1;
					return end+1;
				}
			}
			
			// Set helpers
			if (Character.isLowerCase(cur)) 
				isUpperCase = false;
			if (this.isClosingBracket(cur)) 
				isInBrackets = false;
			if (isURL && this.isEndOfURL(text, end+1))
				isURL = false;
			if (this.isStartOfURL(text, end))
				isURL = true;
			if (this.isDomainOfURL(text, end+1))
				isURL = true;
		}
		
		// If no end was found before, take the rest
		return text.length;		
	}
	
	

	// -------------------------------------------------------------------------
	// DETERMINERS FOR URLS
	// -------------------------------------------------------------------------
	
	/**
	 * Determines whether the character sequence starting at the given index "i" 
	 * could be the beginning of a URL (such as "http:").
	 * 
	 * @param text The text to be analyzed
	 * @param i The index of the current character
	 * @return Whether the text sequence starting at index "i" is the start of a 
	 *   URL
	 */
	private boolean isStartOfURL(char [] text, int i){
		if (i < text.length - 10){
			for (int j=4; j<7; j++){
				StringBuilder sb = new StringBuilder();
				for (int k=i; k<i+j; k++)
					sb.append(text[k]);
				if (isInLexicon(urlPrefixLexicon, sb.toString(), false, true))
					return true;
			}
		}
		return false;
	}
	
	/**
	 * Determines whether the character sequence starting at the given index "i" 
	 * could be the domain name of a URL (such as ".com").
	 * 
	 * @param text The text to be analyzed
	 * @param i The index of the current character
	 * @return Whether the text sequence starting at index "i" is the head of a 
	 *   URL
	 */
	private boolean isDomainOfURL(char [] text, int i){
		if (text[i] == '.'){
			StringBuilder sb = new StringBuilder();
			if (i<text.length-2){
				sb.append(text[i]).append(text[i+1]).append(text[i+2]);
				if (isInLexicon(urlSuffixLexicon, sb.toString(), false, true)){
					return true;
				}
			}
			if (i<text.length-3){
				sb.append(text[i+3]);
				if (isInLexicon(urlSuffixLexicon, sb.toString(), false, true)){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Determines whether the character of given text at the given index denotes
	 * the first character after a URL.
	 * 
	 * @param text The text
	 * @param i The index
	 * @return Whether the character at index "i" denotes the end of a URL
	 */
	private boolean isEndOfURL(char [] text, int i){
		if (Character.isWhitespace(text[i])){ 
			return true;
		}
		return (i < text.length-1 
				&& this.isFullStop(text[i]) 
				&& Character.isWhitespace(text[i+1]));
	}
	
	

	// -------------------------------------------------------------------------
	// DETERMINERS FOR PERIOD REQUESTS
	// -------------------------------------------------------------------------

	/**
	 * Determines whether the period at index <code>i</code> belongs to 
	 * an ellipsis char sequence
	 * 
	 * @param text The text to be analyzed
	 * @param i The index of the current character
	 * @return Whether or not
	 */
	private boolean periodBelongsToAnEllipsis(char [] text, int i){
		return (i > 0 && text[i-1] == '.')
				|| (i > 1 && Character.isWhitespace(text[i-1])
						&& text[i-2] == '.')
				|| (i < text.length-1 && text[i+1] == '.')
				|| (i < text.length-2 && Character.isWhitespace(text[i+1])
						&& text[i+2] == '.');
	}
	
	/**
	 * Determines whether the period at index <code>i</code> belongs to a
	 * single character, i.e. neither a letter nor a number precedes that 
	 * character.
	 * 
	 * @param text The text to be analysed
	 * @param i The index of the current character
	 * @return <code>true</code> iff. preceding term is a single character
	 */
	private boolean periodBelongsToSingleCharacter(char [] text, int i){
		if (i<1)
			return false;
		if (i<2)
			return Character.isLetter(text[i-1]);
		return Character.isLetter(text[i-1]) 
				&& !(Character.isLetterOrDigit(text[i-2]) 
						|| this.isApostrophe(text[i-2]));
	}
	
	/**
	 * Determines whether the period at index <code>j</code> belongs to an 
	 * ordinal number in the range from <code>RANGE_LOWER_BOUND</code> to 
	 * <code>RANGE_UPPER_BOUND</code>. In this case, the number is assumed to be 
	 * an ordinal number.
	 * 
	 * @param text The text to be analyzed
	 * @param j The index of the period
	 * @return <code>true</code> iff. preceding term is a number in the demanded 
	 * range
	 */
	private boolean periodBelongsToAnOrdinalNumber(char [] text, int j){
		if (j==0)
			return false;
 		if (Character.isWhitespace(text[j+1])) return false;
		int i = j-1;
		if (!Character.isDigit(text[i])) return false;
		while (i>=0 && Character.isDigit(text[i]))
			i--;
		if (i>0 && Character.isLetter(text[i])) return false;
		StringBuilder sb = new StringBuilder();
		for (int k=i+1; k<j; k++)
			sb.append(text[k]);
		try{
			long number = Long.parseLong(sb.toString());
			return number >= ORDINAL_LOWER_BOUND 
					&& number <= ORDINAL_UPPER_BOUND;
		} catch (NumberFormatException nfe){
			return false;
		}
	}
	
	/**
	 * Determines whether the period at index <code>i</code> belongs to an 
	 * acronym that is on the used acronym list.
	 * 
	 * @param text The text to be analysed
	 * @param i The index of the period
	 * @return <code>true</code> iff. period belongs to an acronym
	 */
	private boolean periodBelongsToAnAcronym(char [] text, int i){
		int begin = i-1;
		while(begin > 0 
				&& (Character.isLetter(text[begin]) || text[begin] == '.')){
			begin--;
		}
		StringBuilder sb = new StringBuilder();
		for (int k=begin+1; k<i+1; k++)
			sb.append(text[k]);
		return isInLexicon(abbreviationLexicon, sb.toString(), false, false);	
	}
	
	
	
	// -------------------------------------------------------------------------
	// DETERMINERS FOR CHARACTER REQUESTS
	// -------------------------------------------------------------------------
	
	/**
	 * Returns whether an upper-case letter follows after the character of the
	 * given text at the given index "i" and possibly following whitespaces.
	 * 
	 * @param text The text
	 * @param i The index
	 * @return Whether or not an upper-case letter follows
	 */
	private boolean upperCaseFollows(char[] text, int i){
		while (i<text.length && (Character.isWhitespace(text[i])
				|| this.isQuotationMark(text[i]))){
			i++;
		}
		if (i<text.length)
			return Character.isUpperCase(text[i]);
		else return false;
	}
		
	/**
	 * Returns whether a sequence of at least two hyphens precedes the character 
	 * of the given text at the given index "i" and possibly preceding 
	 * whitespaces.
	 * 
	 * @param text The text
	 * @param i The index
	 * @return Whether a sequence of hyphens precedes
	 */
	private boolean precededByHyphens(char [] text, int i){
		while (i>2 && Character.isWhitespace(text[i]))
			i--;
		return this.isHyphen(text[i]) && this.isHyphen(text[i-1]);
	}


	
	// -------------------------------------------------------------------------
	// DETERMINERS FOR LINE REQUESTS
	// -------------------------------------------------------------------------
	
	/**
	 * Determines whether the next line after the character at position 
	 * <CODE>i</CODE> starts with a possible bullet symbol as given in 
	 * {@link de.sundn.afaiq.util.upb.efxtools.util.CharTools}.
	 * 
	 * @param text The text to be analyzed
	 * @param i The index of the current character
	 * @return <CODE>true</CODE> iff. the next line starts with a bullet symbol
	 */
	private boolean nextLineStartsWithBullet(char [] text, int i){
		if (i >= text.length-1) return false;
		while (i+1 < text.length && Character.isWhitespace(text[i]))
			i++;
		return this.isBullet(text[i]);
	}
	
	/**
	 * Determines if the next non-whitespace character equals the character
	 * given by <code>c</code>.
	 * 
	 * @param text The text to be analyzed
	 * @param c The character to be checked for equality
	 * @param i The index of the current character
	 * @return <code>true</code> iff. the two characters are equal
	 */
	private boolean nextLineStartsWithSameCharacter(char [] text, char c, 
			int i){
		if (i >= text.length-1) return false;
		int j = i;
		while (j+1<text.length && Character.isWhitespace(text[j+1]))
			j++;
		if (j+1 == text.length) return false;
		return c == text[j+1];
	}
	
	/**
	 * Determines whether the next line after the line break at index
	 * <code>i</code> is a blank line, i.e. it consists of an arbitrary
	 * amount of white spaces and nothing else.
	 * 
	 * @param text The text to be analyzed
	 * @param i The index of the first line break
	 * @return <code>true</code> iff. the next line is a blank line
	 */
	private boolean nextLineIsBlankLine(char [] text, int i){
		// Document uses Carriage Return AND Line Feed
		if (text[i] == '\r' && text[i+1] == '\n'){
			int j = i+2;
			while (j< text.length && Character.isSpaceChar(text[j]))
				j++;
			if (j >= text.length) return true;
			return (text[j] == '\r' && text[j+1] == '\n');
		}
		// Document uses either Carriage Return OR Line Feed
		else if (text[i] == '\r' || text[i] == '\n'){
			char cur = text[i];
			int j = i+1;
			while (j< text.length && Character.isSpaceChar(text[j]))
				j++;
			if (j >= text.length) return true;
			return (text[j] == cur);
		}
		// Other cases not supported at the moment
		return false;
	}
	
	

	// -------------------------------------------------------------------------
	// HELPERS
	// -------------------------------------------------------------------------
	
	/**
	 * Determines whether <code>c</code> is a period, a
	 * question mark or an exclamation mark.
	 * 
	 * @param c The character
	 * @return <code>true</code> iff. <code>c</code> is a
	 * full stop.
	 */
	private boolean isFullStop(char c){
		return (c == '.' || isUmambiguousEndMark(c));
	}	

	/**
	 * Determines whether <code>c</code> is one of the
	 * characters, which can be seen as unambiguous end
	 * mark, i.e. it is a question mark or an exclamation 
	 * mark.
	 * 
	 * @param c The character
	 * @return <code>true</code> iff. <code>c</code> is an unambiguous end mark
	 */
	private boolean isUmambiguousEndMark(char c){
		return (c == '?' || c == '!');
	}

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
	
	/**
	 * Determines whether <code>c</code> is any of the
	 * existing quotation mark characters.
	 * 
	 * @param c The character
	 * @return <code>true</code> iff. <code>c</code> is a quotation mark.
	 */
	private boolean isQuotationMark(char c){
		return c == '"' || c == '\u201C' || c == '\u201D' || 
			c == '\u201E' || c == '\u201F' || c == '\u2018'; 

		// Excluded:
		// || c == '\'' // Often used as an apostrophe.
		// || c == '\u2019' // Often used as an apostrophe.
	}
	
	/**
	 * Determine whether <code>c</code> is a character often used as an
	 * apostrophe.
	 * 
	 * @param c The character
	 * @return <code>true</code> iff. <code>c</code> is used apostrophe.
	 */
	private boolean isApostrophe(char c){
		return c == '\'' || c == '\u2019';
	}
	
	/**
	 * Determines whether <code>c</code> is one of the
	 * four types of an opening bracket.
	 * 
	 * @param c The character
	 * @return <code>true</code> iff. <code>c</code> is an opening bracket.
	 */
	private boolean isOpeningBracket(char c){
		return c == '(' || c == '[' || c == '{' || c == '<';
	}

	/**
	 * Determines whether <code>c</code> is one of the
	 * four types of a closing bracket.
	 * 
	 * @param c The character
	 * @return <code>true</code> iff. <code>c</code> is a closing bracket.
	 */
	private boolean isClosingBracket(char c){
		return c == ')' || c == ']' || c == '}' || c == '>';
	}
	
	/**
	 * Determines whether <code>c</code> is possible icon
	 * of a bulletpoint of an unordererd list.
	 * 
	 * @param c The character
	 * @return <code>true</code> iff. <code>c</code> is a bullet.
	 */
	private boolean isBullet(char c){
		return c == '-' || c == '*' || c == '+' || c == '\u2022' 
				|| c == '\u25E6' || c == '\u2012' || c == '\u2013' 
				|| c == '\u2014' || c == '\u2015';
	}
	
	/**
	 * Determines wheter <code>lexicon</code> contains <code>word</code>
	 * either <code>toUpperCase</code>, <code>toLowerCase</code>, or in its
	 * original form if the previous two parameters are both false.
	 * 
	 * @param lexicon The lexicon
	 * @param word The word to check against the lexicon
	 * @param toUpperCase Whether the word shall checked upper case
	 * @param toLowerCase Whether the word shall checked lower case
	 * @return <code>true</code> iff. <code>lexicon</code> contains
	 * 	 <code>word</code>.
	 */
	private boolean isInLexicon(Set<String> lexicon, String word,
			boolean toUpperCase, boolean toLowerCase){
		if (toUpperCase) 
			return lexicon.contains(word.toUpperCase());
		else if (toLowerCase)
			return lexicon.contains(word.toLowerCase());
		else
			return lexicon.contains(word);
	}

}
