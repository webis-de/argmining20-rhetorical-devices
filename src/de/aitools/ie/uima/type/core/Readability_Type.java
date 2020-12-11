
/* First created by JCasGen Wed Feb 15 17:58:15 CET 2017 */
package de.aitools.ie.uima.type.core;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import de.aitools.ie.uima.type.supertype.Classification_Type;

/** The classification of the readability of a text. Different readability scores can be set as features of a respective annotation.
 * Updated by JCasGen Thu Feb 16 01:02:57 CET 2017
 * @generated */
public class Readability_Type extends Classification_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Readability.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("de.aitools.ie.uima.type.core.Readability");
 
  /** @generated */
  final Feature casFeat_automatedReadabilityIndex;
  /** @generated */
  final int     casFeatCode_automatedReadabilityIndex;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public double getAutomatedReadabilityIndex(int addr) {
        if (featOkTst && casFeat_automatedReadabilityIndex == null)
      jcas.throwFeatMissing("automatedReadabilityIndex", "de.aitools.ie.uima.type.core.Readability");
    return ll_cas.ll_getDoubleValue(addr, casFeatCode_automatedReadabilityIndex);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setAutomatedReadabilityIndex(int addr, double v) {
        if (featOkTst && casFeat_automatedReadabilityIndex == null)
      jcas.throwFeatMissing("automatedReadabilityIndex", "de.aitools.ie.uima.type.core.Readability");
    ll_cas.ll_setDoubleValue(addr, casFeatCode_automatedReadabilityIndex, v);}
    
  
 
  /** @generated */
  final Feature casFeat_colemanLiauIndex;
  /** @generated */
  final int     casFeatCode_colemanLiauIndex;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public double getColemanLiauIndex(int addr) {
        if (featOkTst && casFeat_colemanLiauIndex == null)
      jcas.throwFeatMissing("colemanLiauIndex", "de.aitools.ie.uima.type.core.Readability");
    return ll_cas.ll_getDoubleValue(addr, casFeatCode_colemanLiauIndex);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setColemanLiauIndex(int addr, double v) {
        if (featOkTst && casFeat_colemanLiauIndex == null)
      jcas.throwFeatMissing("colemanLiauIndex", "de.aitools.ie.uima.type.core.Readability");
    ll_cas.ll_setDoubleValue(addr, casFeatCode_colemanLiauIndex, v);}
    
  
 
  /** @generated */
  final Feature casFeat_fleschKincaidGradeLevel;
  /** @generated */
  final int     casFeatCode_fleschKincaidGradeLevel;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public double getFleschKincaidGradeLevel(int addr) {
        if (featOkTst && casFeat_fleschKincaidGradeLevel == null)
      jcas.throwFeatMissing("fleschKincaidGradeLevel", "de.aitools.ie.uima.type.core.Readability");
    return ll_cas.ll_getDoubleValue(addr, casFeatCode_fleschKincaidGradeLevel);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setFleschKincaidGradeLevel(int addr, double v) {
        if (featOkTst && casFeat_fleschKincaidGradeLevel == null)
      jcas.throwFeatMissing("fleschKincaidGradeLevel", "de.aitools.ie.uima.type.core.Readability");
    ll_cas.ll_setDoubleValue(addr, casFeatCode_fleschKincaidGradeLevel, v);}
    
  
 
  /** @generated */
  final Feature casFeat_fleschKincaidReadingEaseScore;
  /** @generated */
  final int     casFeatCode_fleschKincaidReadingEaseScore;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public double getFleschKincaidReadingEaseScore(int addr) {
        if (featOkTst && casFeat_fleschKincaidReadingEaseScore == null)
      jcas.throwFeatMissing("fleschKincaidReadingEaseScore", "de.aitools.ie.uima.type.core.Readability");
    return ll_cas.ll_getDoubleValue(addr, casFeatCode_fleschKincaidReadingEaseScore);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setFleschKincaidReadingEaseScore(int addr, double v) {
        if (featOkTst && casFeat_fleschKincaidReadingEaseScore == null)
      jcas.throwFeatMissing("fleschKincaidReadingEaseScore", "de.aitools.ie.uima.type.core.Readability");
    ll_cas.ll_setDoubleValue(addr, casFeatCode_fleschKincaidReadingEaseScore, v);}
    
  
 
  /** @generated */
  final Feature casFeat_gunningFogIndex;
  /** @generated */
  final int     casFeatCode_gunningFogIndex;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public double getGunningFogIndex(int addr) {
        if (featOkTst && casFeat_gunningFogIndex == null)
      jcas.throwFeatMissing("gunningFogIndex", "de.aitools.ie.uima.type.core.Readability");
    return ll_cas.ll_getDoubleValue(addr, casFeatCode_gunningFogIndex);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setGunningFogIndex(int addr, double v) {
        if (featOkTst && casFeat_gunningFogIndex == null)
      jcas.throwFeatMissing("gunningFogIndex", "de.aitools.ie.uima.type.core.Readability");
    ll_cas.ll_setDoubleValue(addr, casFeatCode_gunningFogIndex, v);}
    
  
 
  /** @generated */
  final Feature casFeat_lix;
  /** @generated */
  final int     casFeatCode_lix;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public double getLix(int addr) {
        if (featOkTst && casFeat_lix == null)
      jcas.throwFeatMissing("lix", "de.aitools.ie.uima.type.core.Readability");
    return ll_cas.ll_getDoubleValue(addr, casFeatCode_lix);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setLix(int addr, double v) {
        if (featOkTst && casFeat_lix == null)
      jcas.throwFeatMissing("lix", "de.aitools.ie.uima.type.core.Readability");
    ll_cas.ll_setDoubleValue(addr, casFeatCode_lix, v);}
    
  
 
  /** @generated */
  final Feature casFeat_mcAlpineEFlawScore;
  /** @generated */
  final int     casFeatCode_mcAlpineEFlawScore;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public double getMcAlpineEFlawScore(int addr) {
        if (featOkTst && casFeat_mcAlpineEFlawScore == null)
      jcas.throwFeatMissing("mcAlpineEFlawScore", "de.aitools.ie.uima.type.core.Readability");
    return ll_cas.ll_getDoubleValue(addr, casFeatCode_mcAlpineEFlawScore);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setMcAlpineEFlawScore(int addr, double v) {
        if (featOkTst && casFeat_mcAlpineEFlawScore == null)
      jcas.throwFeatMissing("mcAlpineEFlawScore", "de.aitools.ie.uima.type.core.Readability");
    ll_cas.ll_setDoubleValue(addr, casFeatCode_mcAlpineEFlawScore, v);}
    
  
 
  /** @generated */
  final Feature casFeat_rix;
  /** @generated */
  final int     casFeatCode_rix;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public double getRix(int addr) {
        if (featOkTst && casFeat_rix == null)
      jcas.throwFeatMissing("rix", "de.aitools.ie.uima.type.core.Readability");
    return ll_cas.ll_getDoubleValue(addr, casFeatCode_rix);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setRix(int addr, double v) {
        if (featOkTst && casFeat_rix == null)
      jcas.throwFeatMissing("rix", "de.aitools.ie.uima.type.core.Readability");
    ll_cas.ll_setDoubleValue(addr, casFeatCode_rix, v);}
    
  
 
  /** @generated */
  final Feature casFeat_smogGrade;
  /** @generated */
  final int     casFeatCode_smogGrade;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public double getSmogGrade(int addr) {
        if (featOkTst && casFeat_smogGrade == null)
      jcas.throwFeatMissing("smogGrade", "de.aitools.ie.uima.type.core.Readability");
    return ll_cas.ll_getDoubleValue(addr, casFeatCode_smogGrade);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSmogGrade(int addr, double v) {
        if (featOkTst && casFeat_smogGrade == null)
      jcas.throwFeatMissing("smogGrade", "de.aitools.ie.uima.type.core.Readability");
    ll_cas.ll_setDoubleValue(addr, casFeatCode_smogGrade, v);}
    
  
 
  /** @generated */
  final Feature casFeat_strainIndex;
  /** @generated */
  final int     casFeatCode_strainIndex;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public double getStrainIndex(int addr) {
        if (featOkTst && casFeat_strainIndex == null)
      jcas.throwFeatMissing("strainIndex", "de.aitools.ie.uima.type.core.Readability");
    return ll_cas.ll_getDoubleValue(addr, casFeatCode_strainIndex);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setStrainIndex(int addr, double v) {
        if (featOkTst && casFeat_strainIndex == null)
      jcas.throwFeatMissing("strainIndex", "de.aitools.ie.uima.type.core.Readability");
    ll_cas.ll_setDoubleValue(addr, casFeatCode_strainIndex, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Readability_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_automatedReadabilityIndex = jcas.getRequiredFeatureDE(casType, "automatedReadabilityIndex", "uima.cas.Double", featOkTst);
    casFeatCode_automatedReadabilityIndex  = (null == casFeat_automatedReadabilityIndex) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_automatedReadabilityIndex).getCode();

 
    casFeat_colemanLiauIndex = jcas.getRequiredFeatureDE(casType, "colemanLiauIndex", "uima.cas.Double", featOkTst);
    casFeatCode_colemanLiauIndex  = (null == casFeat_colemanLiauIndex) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_colemanLiauIndex).getCode();

 
    casFeat_fleschKincaidGradeLevel = jcas.getRequiredFeatureDE(casType, "fleschKincaidGradeLevel", "uima.cas.Double", featOkTst);
    casFeatCode_fleschKincaidGradeLevel  = (null == casFeat_fleschKincaidGradeLevel) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_fleschKincaidGradeLevel).getCode();

 
    casFeat_fleschKincaidReadingEaseScore = jcas.getRequiredFeatureDE(casType, "fleschKincaidReadingEaseScore", "uima.cas.Double", featOkTst);
    casFeatCode_fleschKincaidReadingEaseScore  = (null == casFeat_fleschKincaidReadingEaseScore) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_fleschKincaidReadingEaseScore).getCode();

 
    casFeat_gunningFogIndex = jcas.getRequiredFeatureDE(casType, "gunningFogIndex", "uima.cas.Double", featOkTst);
    casFeatCode_gunningFogIndex  = (null == casFeat_gunningFogIndex) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_gunningFogIndex).getCode();

 
    casFeat_lix = jcas.getRequiredFeatureDE(casType, "lix", "uima.cas.Double", featOkTst);
    casFeatCode_lix  = (null == casFeat_lix) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_lix).getCode();

 
    casFeat_mcAlpineEFlawScore = jcas.getRequiredFeatureDE(casType, "mcAlpineEFlawScore", "uima.cas.Double", featOkTst);
    casFeatCode_mcAlpineEFlawScore  = (null == casFeat_mcAlpineEFlawScore) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_mcAlpineEFlawScore).getCode();

 
    casFeat_rix = jcas.getRequiredFeatureDE(casType, "rix", "uima.cas.Double", featOkTst);
    casFeatCode_rix  = (null == casFeat_rix) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_rix).getCode();

 
    casFeat_smogGrade = jcas.getRequiredFeatureDE(casType, "smogGrade", "uima.cas.Double", featOkTst);
    casFeatCode_smogGrade  = (null == casFeat_smogGrade) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_smogGrade).getCode();

 
    casFeat_strainIndex = jcas.getRequiredFeatureDE(casType, "strainIndex", "uima.cas.Double", featOkTst);
    casFeatCode_strainIndex  = (null == casFeat_strainIndex) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_strainIndex).getCode();

  }
}



    