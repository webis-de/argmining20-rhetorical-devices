package rhetoricDetection.aitools;

import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import de.aitools.ie.uima.type.core.Constituent;
import de.aitools.ie.uima.type.core.Sentence;
import de.aitools.ie.uima.type.core.Token;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.IntPair;
/**
 * Wrapper of the StanfordParser that creates a constituency and dependency
 * parse tree. 
 * <p>
 * Requires sentence and token annotations.
 * Produces the depLabel and parent features of the {@link Token} annotations as
 * well as {@link Constituent} annotations.
 * </p><p>
 * The corresponding primitive analysis engine is thread safe.
 * </p><p>
 * For more details, see the
 * <a href="http://nlp.stanford.edu/software/lex-parser.shtml">Stanford Parser
 * Homepage</a>.
 * </p>
 * 
 * @author johannes.kiesel@uni-weimar.de
 *
 */
public class StanfordParser extends JCasAnnotator_ImplBase {

  // -------------------------------------------------------------------------
  // UIMA PARAMETERS
  // -------------------------------------------------------------------------

  public static final String PARAM_LEXICALIZED_PARSER_MODEL_PATHS =
      "StanfordParserModelPaths";
  
  // -------------------------------------------------------------------------
  // CONSTANTS
  // -------------------------------------------------------------------------

  protected static final String DEFAULT_LEXICALIZED_PARSER_MODEL_PATH =
      "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";

  // -------------------------------------------------------------------------
  // PARAMETERS
  // -------------------------------------------------------------------------
  
  protected LexicalizedParser lexicalizedParser;

  // -------------------------------------------------------------------------
  // KONSTRUCTORS
  // -------------------------------------------------------------------------
  
  /**
   * Creates a new parser must be initialized before use.
   * <p>
   * Use {@link #initialize(String...)} with null or an empty array to use the
   * default parser.
   * </p>
   * @see #DEFAULT_LEXICALIZED_PARSER_MODEL_PATH
   */
  public StanfordParser() {
    this.lexicalizedParser = null;
  }
  
  /**
   * Creates a new parser that uses the first valid lexicalized parser model
   * specified in the lexicalizedParserModelPaths.
   * @param lexicalizedParserModelPaths Paths that are tried for initializing
   * the lexicalized parser
   * @throws IllegalArgumentException If none of the given paths denotes a
   * valid lexicalized parser model
   */
  public StanfordParser(final String... lexicalizedParserModelPaths)
  throws IllegalArgumentException {
    this();
    this.initialize(lexicalizedParserModelPaths);
  }

  // -------------------------------------------------------------------------
  // INITIALIZATION
  // -------------------------------------------------------------------------

  @Override
  public void initialize(final UimaContext context)
  throws ResourceInitializationException {
    super.initialize(context);
    
    final String[] lexicalizedParserModelPaths = (String[])
        context.getConfigParameterValue(PARAM_LEXICALIZED_PARSER_MODEL_PATHS);
    
    try {
      this.initialize(lexicalizedParserModelPaths);
    } catch (final Throwable e) {
      throw new ResourceInitializationException(e);
    }
  }
  
  /**
   * Initializes this parser with the first valid lexicalized parser model
   * specified in the lexicalizedParserModelPaths.
   * <p>
   * Use null or an empty array as a parameter to load the default parser model.
   * </p>
   * @param lexicalizedParserModelPaths Paths that are tried for initializing
   * the lexicalized parser or null/empty array
   * @throws IllegalArgumentException If none of the given paths denotes a
   * valid lexicalized parser model or the default parser could not be loaded
   */
  public void initialize(final String... lexicalizedParserModelPaths)
  throws IllegalArgumentException {
    if (lexicalizedParserModelPaths == null
        || lexicalizedParserModelPaths.length == 0) {
      this.lexicalizedParser =
          this.loadLexicalizedParser(new String[] {
                DEFAULT_LEXICALIZED_PARSER_MODEL_PATH
             });
    } else {
      this.lexicalizedParser =
          this.loadLexicalizedParser(lexicalizedParserModelPaths);
    }
  }

  /**
   * Initializes this parser with the first valid lexicalized parser model
   * specified in the paths.
   * @param paths Paths that are tried for initializing the lexicalized parser
   * @return the parser
   * @throws IllegalArgumentException If none of the given paths denotes a
   * valid lexicalized parser model
   */
  protected LexicalizedParser loadLexicalizedParser(final String[] paths)
  throws IllegalArgumentException {
    for (final String path : paths) {
      try {
        final ObjectInputStream parserStream =
            IOUtils.readStreamFromString(path);
        try {
          return LexicalizedParser.loadModel(parserStream);
        } finally {
          parserStream.close();
        }
      } catch (final Exception e) {
        // Path not found... still okay... try next
      }
    }
    throw new IllegalArgumentException(
        "No lexicalized parser model found for paths: "
            + Arrays.toString(paths));
  }

  // -------------------------------------------------------------------------
  // GETTERS
  // -------------------------------------------------------------------------

  /**
   * Gets the lexicalized parser.
   * @return The parser
   * @throws IllegalStateException If this parser was not yet
   * {@link #initialize(String...)}d
   */
  public LexicalizedParser getLexicalizedParser()
  throws IllegalStateException {
    if (this.lexicalizedParser == null) {
      throw new IllegalStateException(
          this.getClass().getName() + " not yet initialized");
      
    }
    return this.lexicalizedParser;
  }
  
  // -------------------------------------------------------------------------
  // PROCESSING
  // -------------------------------------------------------------------------

  /**
   * Creates the constituents tree from the sentence that consists of given
   * tokens.
   * @param tokens The tokens of the sentence
   * @return The constituents tree
   * @throws IllegalStateException When this parser was not
   * {@link #initialize(String...)}d
   */
  public Tree parseConstituentsTree(final List<CoreLabel> tokens)
  throws IllegalStateException {
    final Tree tree = this.getLexicalizedParser().apply(tokens);
    tree.setSpans();
    return tree;
  }
  
  /**
   * Extracts all typed dependencies from given constituents tree.
   * @param constituentsTree The constituents tree
   * @return The list of typed dependencies
   * @see #parseConstituentsTree(List)
   */
  public Collection<TypedDependency> parseDependencies(
      final Tree constituentsTree) {
    final TreebankLanguagePack treeBank = new PennTreebankLanguagePack();
    final GrammaticalStructureFactory structureFactory =
        treeBank.grammaticalStructureFactory();
    final GrammaticalStructure structure =
        structureFactory.newGrammaticalStructure(constituentsTree);
    return structure.typedDependenciesCollapsed();
  }

  @Override
  public void process(final JCas jCas) throws AnalysisEngineProcessException {
    final FSIterator<Annotation> sentenceIterator =
        jCas.getAnnotationIndex(Sentence.type).iterator();
    while (sentenceIterator.hasNext()) {
      final Sentence sentence = (Sentence) sentenceIterator.next();
      final List<Token> tokens = this.getTokensInSentence(jCas, sentence);
      final Tree constituentsTree = this.parseConstituentsTree(
          this.tokensToCoreLabels(tokens));
      final Collection<TypedDependency> dependencies =
          this.parseDependencies(constituentsTree);
    
      final Constituent noParent = null;
      this.addConstituentsRecursive(jCas, tokens, constituentsTree, noParent);
      this.addDependencies(tokens, dependencies);
    }
  }
  
  /**
   * Adds the constituent that corresponds to the current tree and all child
   * constituents to the JCas.
   * <p>
   * This will not add an extra constituent for leaf nodes in the tree, as they
   * would be the same as the tokens that we already have (the trees contain
   * an extra node as parent of each leaf node that gives its tag, and this
   * is added to the JCas). Because of this, the treeDepth of each constituent
   * is one less than the depth of the tree from which it is created.
   * </p>
   * @param jCas The JCas to add the constituents to
   * @param tokens The tokens from which the complete tree was created 
   * @param tree The (sub-)tree that corresponds to the current constituent 
   * @param parent The parent of the subtree or null if it is the complete tree
   */
  protected void addConstituentsRecursive(final JCas jCas,
      final List<Token> tokens, final Tree tree, final Constituent parent) {
    final int depth = tree.depth() - 1;
    if (depth < 0) { return; } // Not annotating tokens
    
    final IntPair span = tree.getSpan();
    final int begin = tokens.get(span.getSource()).getBegin();
    final int end = tokens.get(span.getTarget()).getEnd();
    final String label = tree.label().value();
    
    final Constituent constituent = new Constituent(jCas, begin, end);
    
    //list of clauses and sentences POStags needed for Ruta Rules
    //List<String> clauses = Arrays.asList("ADJP", "ADVP", "CONJP", "NP", "PP", "VP", "S", "SBAR");
    if (parent != null) { constituent.setParent(parent); }
    //allow only leaf nodes POStags and clauses used in Ruta
    //if (depth==0 || clauses.contains(label)) {
    	constituent.setTreeDepth(depth);
        constituent.setLabel(label);
	//}
    
    
    for (final Tree child : tree.children()) {
      this.addConstituentsRecursive(jCas, tokens, child, constituent);
    }
    
    constituent.addToIndexes();
  }
  
  /**
   * Adds the specified dependencies to the sentence tokens.
   * <p>
   * Dependencies that have "ROOT" as its governor are not added as we don't use
   * a root token.
   * </p>
   * @param tokens The tokens from which the complete tree was created
   * @param dependencies Dependency created by {@link #parseDependencies(Tree)}
   * from the same complete tree
   */
  protected void addDependencies(
      final List<Token> tokens,
      final Collection<TypedDependency> dependencies) {
    for (final TypedDependency dependency : dependencies) {
      this.addDependency(tokens, dependency);
    }
  }
  
  /**
   * Adds the specified dependency to the sentence tokens.
   * <p>
   * If the dependency has "ROOT" as its governor, this method does not add it
   * as we don't use a root token.
   * </p>
   * @param tokens The tokens from which the complete tree was created
   * @param dependency A dependency created by {@link #parseDependencies(Tree)}
   * from the same complete tree
   */
  protected void addDependency(
      final List<Token> tokens, final TypedDependency dependency) {
    if (dependency.gov().index() == 0) { return; } // No root dependency
    final Token dependent = tokens.get(dependency.dep().index() - 1);
    final Token governor = tokens.get(dependency.gov().index() - 1);
    dependent.setParent(governor);
    dependent.setDepLabel(dependency.reln().getShortName());
  }

  /**
   * Gets all tokens from the specified sentence.
   * @param jCas The JCas with {@link Token} annotations and that contains the
   * sentence
   * @param sentence The sentence
   * @return All tokens within the sentence
   */
  protected List<Token> getTokensInSentence(
      final JCas jCas, final Sentence sentence) {
    final List<Token> tokens = new ArrayList<>();
    
    final FSIterator<Annotation> tokenIterator =
        jCas.getAnnotationIndex(Token.type).subiterator(sentence);
    while (tokenIterator.hasNext()) {
      tokens.add((Token) tokenIterator.next());
    }

    return tokens;
  }

  /**
   * Creates {@link CoreLabel}s from {@link Token}s.
   * <p>
   * The created CoreLabels contain the text, begin, and end information of the
   * tokens. 
   * </p>
   * @param tokens The tokens
   * @return One CoreLabel for each Token in the same order
   */
  protected List<CoreLabel> tokensToCoreLabels(final Iterable<Token> tokens) {
    final CoreLabelTokenFactory tokenFactory = new CoreLabelTokenFactory();
    final List<CoreLabel> coreLabels = new ArrayList<CoreLabel>();
    
    for (final Token token : tokens) {
      final String text = token.getCoveredText();
      final int begin = token.getBegin();
      final int length = token.getEnd() - begin;
      coreLabels.add(tokenFactory.makeToken(text, begin, length));
    }

    return coreLabels;
  }

}
