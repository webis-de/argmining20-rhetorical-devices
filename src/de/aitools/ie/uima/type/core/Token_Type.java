
/* First created by JCasGen Wed Feb 15 17:58:15 CET 2017 */
package de.aitools.ie.uima.type.core;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import de.aitools.ie.uima.type.supertype.GrammaticalUnit_Type;

/** A token, i.e., a word, number, or the like.
 * Updated by JCasGen Thu Feb 16 01:02:58 CET 2017
 * @generated */
public class Token_Type extends GrammaticalUnit_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Token.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("de.aitools.ie.uima.type.core.Token");
 
  /** @generated */
  final Feature casFeat_lemma;
  /** @generated */
  final int     casFeatCode_lemma;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getLemma(int addr) {
        if (featOkTst && casFeat_lemma == null)
      jcas.throwFeatMissing("lemma", "de.aitools.ie.uima.type.core.Token");
    return ll_cas.ll_getStringValue(addr, casFeatCode_lemma);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setLemma(int addr, String v) {
        if (featOkTst && casFeat_lemma == null)
      jcas.throwFeatMissing("lemma", "de.aitools.ie.uima.type.core.Token");
    ll_cas.ll_setStringValue(addr, casFeatCode_lemma, v);}
    
  
 
  /** @generated */
  final Feature casFeat_stem;
  /** @generated */
  final int     casFeatCode_stem;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getStem(int addr) {
        if (featOkTst && casFeat_stem == null)
      jcas.throwFeatMissing("stem", "de.aitools.ie.uima.type.core.Token");
    return ll_cas.ll_getStringValue(addr, casFeatCode_stem);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setStem(int addr, String v) {
        if (featOkTst && casFeat_stem == null)
      jcas.throwFeatMissing("stem", "de.aitools.ie.uima.type.core.Token");
    ll_cas.ll_setStringValue(addr, casFeatCode_stem, v);}
    
  
 
  /** @generated */
  final Feature casFeat_pos;
  /** @generated */
  final int     casFeatCode_pos;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getPos(int addr) {
        if (featOkTst && casFeat_pos == null)
      jcas.throwFeatMissing("pos", "de.aitools.ie.uima.type.core.Token");
    return ll_cas.ll_getStringValue(addr, casFeatCode_pos);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setPos(int addr, String v) {
        if (featOkTst && casFeat_pos == null)
      jcas.throwFeatMissing("pos", "de.aitools.ie.uima.type.core.Token");
    ll_cas.ll_setStringValue(addr, casFeatCode_pos, v);}
    
  
 
  /** @generated */
  final Feature casFeat_chunk;
  /** @generated */
  final int     casFeatCode_chunk;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getChunk(int addr) {
        if (featOkTst && casFeat_chunk == null)
      jcas.throwFeatMissing("chunk", "de.aitools.ie.uima.type.core.Token");
    return ll_cas.ll_getStringValue(addr, casFeatCode_chunk);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setChunk(int addr, String v) {
        if (featOkTst && casFeat_chunk == null)
      jcas.throwFeatMissing("chunk", "de.aitools.ie.uima.type.core.Token");
    ll_cas.ll_setStringValue(addr, casFeatCode_chunk, v);}
    
  
 
  /** @generated */
  final Feature casFeat_morph;
  /** @generated */
  final int     casFeatCode_morph;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getMorph(int addr) {
        if (featOkTst && casFeat_morph == null)
      jcas.throwFeatMissing("morph", "de.aitools.ie.uima.type.core.Token");
    return ll_cas.ll_getStringValue(addr, casFeatCode_morph);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setMorph(int addr, String v) {
        if (featOkTst && casFeat_morph == null)
      jcas.throwFeatMissing("morph", "de.aitools.ie.uima.type.core.Token");
    ll_cas.ll_setStringValue(addr, casFeatCode_morph, v);}
    
  
 
  /** @generated */
  final Feature casFeat_parent;
  /** @generated */
  final int     casFeatCode_parent;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getParent(int addr) {
        if (featOkTst && casFeat_parent == null)
      jcas.throwFeatMissing("parent", "de.aitools.ie.uima.type.core.Token");
    return ll_cas.ll_getRefValue(addr, casFeatCode_parent);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setParent(int addr, int v) {
        if (featOkTst && casFeat_parent == null)
      jcas.throwFeatMissing("parent", "de.aitools.ie.uima.type.core.Token");
    ll_cas.ll_setRefValue(addr, casFeatCode_parent, v);}
    
  
 
  /** @generated */
  final Feature casFeat_depLabel;
  /** @generated */
  final int     casFeatCode_depLabel;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getDepLabel(int addr) {
        if (featOkTst && casFeat_depLabel == null)
      jcas.throwFeatMissing("depLabel", "de.aitools.ie.uima.type.core.Token");
    return ll_cas.ll_getStringValue(addr, casFeatCode_depLabel);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setDepLabel(int addr, String v) {
        if (featOkTst && casFeat_depLabel == null)
      jcas.throwFeatMissing("depLabel", "de.aitools.ie.uima.type.core.Token");
    ll_cas.ll_setStringValue(addr, casFeatCode_depLabel, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Token_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_lemma = jcas.getRequiredFeatureDE(casType, "lemma", "uima.cas.String", featOkTst);
    casFeatCode_lemma  = (null == casFeat_lemma) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_lemma).getCode();

 
    casFeat_stem = jcas.getRequiredFeatureDE(casType, "stem", "uima.cas.String", featOkTst);
    casFeatCode_stem  = (null == casFeat_stem) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_stem).getCode();

 
    casFeat_pos = jcas.getRequiredFeatureDE(casType, "pos", "uima.cas.String", featOkTst);
    casFeatCode_pos  = (null == casFeat_pos) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_pos).getCode();

 
    casFeat_chunk = jcas.getRequiredFeatureDE(casType, "chunk", "uima.cas.String", featOkTst);
    casFeatCode_chunk  = (null == casFeat_chunk) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_chunk).getCode();

 
    casFeat_morph = jcas.getRequiredFeatureDE(casType, "morph", "uima.cas.String", featOkTst);
    casFeatCode_morph  = (null == casFeat_morph) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_morph).getCode();

 
    casFeat_parent = jcas.getRequiredFeatureDE(casType, "parent", "de.aitools.ie.uima.type.core.Token", featOkTst);
    casFeatCode_parent  = (null == casFeat_parent) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_parent).getCode();

 
    casFeat_depLabel = jcas.getRequiredFeatureDE(casType, "depLabel", "uima.cas.String", featOkTst);
    casFeatCode_depLabel  = (null == casFeat_depLabel) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_depLabel).getCode();

  }
}



    