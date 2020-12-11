

/* First created by JCasGen Wed Feb 15 17:58:15 CET 2017 */
package de.aitools.ie.uima.type.core;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import de.aitools.ie.uima.type.supertype.GrammaticalUnit;


/** A token, i.e., a word, number, or the like.
 * Updated by JCasGen Thu Feb 16 01:02:58 CET 2017
 * XML source: C:/Users/SpongeBob/workspace/thesis-rhetoric-detection/conf/uima-descriptors/type-systems/RhetoricalDevices.xml
 * @generated */
public class Token extends GrammaticalUnit {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Token.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected Token() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Token(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Token(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Token(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** 
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable 
   */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: lemma

  /** getter for lemma - gets The lemma of a token, i.e., its lexicon form.
   * @generated
   * @return value of the feature 
   */
  public String getLemma() {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_lemma == null)
      jcasType.jcas.throwFeatMissing("lemma", "de.aitools.ie.uima.type.core.Token");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Token_Type)jcasType).casFeatCode_lemma);}
    
  /** setter for lemma - sets The lemma of a token, i.e., its lexicon form. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setLemma(String v) {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_lemma == null)
      jcasType.jcas.throwFeatMissing("lemma", "de.aitools.ie.uima.type.core.Token");
    jcasType.ll_cas.ll_setStringValue(addr, ((Token_Type)jcasType).casFeatCode_lemma, v);}    
   
    
  //*--------------*
  //* Feature: stem

  /** getter for stem - gets The stem of a token.
   * @generated
   * @return value of the feature 
   */
  public String getStem() {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_stem == null)
      jcasType.jcas.throwFeatMissing("stem", "de.aitools.ie.uima.type.core.Token");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Token_Type)jcasType).casFeatCode_stem);}
    
  /** setter for stem - sets The stem of a token. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setStem(String v) {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_stem == null)
      jcasType.jcas.throwFeatMissing("stem", "de.aitools.ie.uima.type.core.Token");
    jcasType.ll_cas.ll_setStringValue(addr, ((Token_Type)jcasType).casFeatCode_stem, v);}    
   
    
  //*--------------*
  //* Feature: pos

  /** getter for pos - gets The part-of-speech tag of a token.
   * @generated
   * @return value of the feature 
   */
  public String getPos() {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_pos == null)
      jcasType.jcas.throwFeatMissing("pos", "de.aitools.ie.uima.type.core.Token");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Token_Type)jcasType).casFeatCode_pos);}
    
  /** setter for pos - sets The part-of-speech tag of a token. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setPos(String v) {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_pos == null)
      jcasType.jcas.throwFeatMissing("pos", "de.aitools.ie.uima.type.core.Token");
    jcasType.ll_cas.ll_setStringValue(addr, ((Token_Type)jcasType).casFeatCode_pos, v);}    
   
    
  //*--------------*
  //* Feature: chunk

  /** getter for chunk - gets The chunk tag of a token.
   * @generated
   * @return value of the feature 
   */
  public String getChunk() {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_chunk == null)
      jcasType.jcas.throwFeatMissing("chunk", "de.aitools.ie.uima.type.core.Token");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Token_Type)jcasType).casFeatCode_chunk);}
    
  /** setter for chunk - sets The chunk tag of a token. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setChunk(String v) {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_chunk == null)
      jcasType.jcas.throwFeatMissing("chunk", "de.aitools.ie.uima.type.core.Token");
    jcasType.ll_cas.ll_setStringValue(addr, ((Token_Type)jcasType).casFeatCode_chunk, v);}    
   
    
  //*--------------*
  //* Feature: morph

  /** getter for morph - gets The morphological tag of a token.
   * @generated
   * @return value of the feature 
   */
  public String getMorph() {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_morph == null)
      jcasType.jcas.throwFeatMissing("morph", "de.aitools.ie.uima.type.core.Token");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Token_Type)jcasType).casFeatCode_morph);}
    
  /** setter for morph - sets The morphological tag of a token. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setMorph(String v) {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_morph == null)
      jcasType.jcas.throwFeatMissing("morph", "de.aitools.ie.uima.type.core.Token");
    jcasType.ll_cas.ll_setStringValue(addr, ((Token_Type)jcasType).casFeatCode_morph, v);}    
   
    
  //*--------------*
  //* Feature: parent

  /** getter for parent - gets The parent token of the token in the dependency graph of the associated sentence.
   * @generated
   * @return value of the feature 
   */
  public Token getParent() {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_parent == null)
      jcasType.jcas.throwFeatMissing("parent", "de.aitools.ie.uima.type.core.Token");
    return (Token)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Token_Type)jcasType).casFeatCode_parent)));}
    
  /** setter for parent - sets The parent token of the token in the dependency graph of the associated sentence. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setParent(Token v) {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_parent == null)
      jcasType.jcas.throwFeatMissing("parent", "de.aitools.ie.uima.type.core.Token");
    jcasType.ll_cas.ll_setRefValue(addr, ((Token_Type)jcasType).casFeatCode_parent, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: depLabel

  /** getter for depLabel - gets The dependency label of the token in the dependency graph of the associated sentence.
   * @generated
   * @return value of the feature 
   */
  public String getDepLabel() {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_depLabel == null)
      jcasType.jcas.throwFeatMissing("depLabel", "de.aitools.ie.uima.type.core.Token");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Token_Type)jcasType).casFeatCode_depLabel);}
    
  /** setter for depLabel - sets The dependency label of the token in the dependency graph of the associated sentence. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setDepLabel(String v) {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_depLabel == null)
      jcasType.jcas.throwFeatMissing("depLabel", "de.aitools.ie.uima.type.core.Token");
    jcasType.ll_cas.ll_setStringValue(addr, ((Token_Type)jcasType).casFeatCode_depLabel, v);}    
  }

    