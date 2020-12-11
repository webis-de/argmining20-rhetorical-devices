

/* First created by JCasGen Wed Feb 15 17:58:15 CET 2017 */
package de.aitools.ie.uima.type.core;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import de.aitools.ie.uima.type.supertype.Classification;


/** The classification of the readability of a text. Different readability scores can be set as features of a respective annotation.
 * Updated by JCasGen Thu Feb 16 01:02:57 CET 2017
 * XML source: C:/Users/SpongeBob/workspace/thesis-rhetoric-detection/conf/uima-descriptors/type-systems/RhetoricalDevices.xml
 * @generated */
public class Readability extends Classification {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Readability.class);
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
  protected Readability() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Readability(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Readability(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Readability(JCas jcas, int begin, int end) {
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
  //* Feature: automatedReadabilityIndex

  /** getter for automatedReadabilityIndex - gets The Automated Readability Index defined as
4.71*characters/words + 0.5*words/sentences - 21.43.
   * @generated
   * @return value of the feature 
   */
  public double getAutomatedReadabilityIndex() {
    if (Readability_Type.featOkTst && ((Readability_Type)jcasType).casFeat_automatedReadabilityIndex == null)
      jcasType.jcas.throwFeatMissing("automatedReadabilityIndex", "de.aitools.ie.uima.type.core.Readability");
    return jcasType.ll_cas.ll_getDoubleValue(addr, ((Readability_Type)jcasType).casFeatCode_automatedReadabilityIndex);}
    
  /** setter for automatedReadabilityIndex - sets The Automated Readability Index defined as
4.71*characters/words + 0.5*words/sentences - 21.43. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setAutomatedReadabilityIndex(double v) {
    if (Readability_Type.featOkTst && ((Readability_Type)jcasType).casFeat_automatedReadabilityIndex == null)
      jcasType.jcas.throwFeatMissing("automatedReadabilityIndex", "de.aitools.ie.uima.type.core.Readability");
    jcasType.ll_cas.ll_setDoubleValue(addr, ((Readability_Type)jcasType).casFeatCode_automatedReadabilityIndex, v);}    
   
    
  //*--------------*
  //* Feature: colemanLiauIndex

  /** getter for colemanLiauIndex - gets The Coleman Liau Index defined as
5.89*characters/words - 0.3*sentences/(100*words) - 15.8.
   * @generated
   * @return value of the feature 
   */
  public double getColemanLiauIndex() {
    if (Readability_Type.featOkTst && ((Readability_Type)jcasType).casFeat_colemanLiauIndex == null)
      jcasType.jcas.throwFeatMissing("colemanLiauIndex", "de.aitools.ie.uima.type.core.Readability");
    return jcasType.ll_cas.ll_getDoubleValue(addr, ((Readability_Type)jcasType).casFeatCode_colemanLiauIndex);}
    
  /** setter for colemanLiauIndex - sets The Coleman Liau Index defined as
5.89*characters/words - 0.3*sentences/(100*words) - 15.8. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setColemanLiauIndex(double v) {
    if (Readability_Type.featOkTst && ((Readability_Type)jcasType).casFeat_colemanLiauIndex == null)
      jcasType.jcas.throwFeatMissing("colemanLiauIndex", "de.aitools.ie.uima.type.core.Readability");
    jcasType.ll_cas.ll_setDoubleValue(addr, ((Readability_Type)jcasType).casFeatCode_colemanLiauIndex, v);}    
   
    
  //*--------------*
  //* Feature: fleschKincaidGradeLevel

  /** getter for fleschKincaidGradeLevel - gets The Flesh Kincaid Grade level defined as: 
11.8*syllables/words + 0.39*words/sentences - 15.59
   * @generated
   * @return value of the feature 
   */
  public double getFleschKincaidGradeLevel() {
    if (Readability_Type.featOkTst && ((Readability_Type)jcasType).casFeat_fleschKincaidGradeLevel == null)
      jcasType.jcas.throwFeatMissing("fleschKincaidGradeLevel", "de.aitools.ie.uima.type.core.Readability");
    return jcasType.ll_cas.ll_getDoubleValue(addr, ((Readability_Type)jcasType).casFeatCode_fleschKincaidGradeLevel);}
    
  /** setter for fleschKincaidGradeLevel - sets The Flesh Kincaid Grade level defined as: 
11.8*syllables/words + 0.39*words/sentences - 15.59 
   * @generated
   * @param v value to set into the feature 
   */
  public void setFleschKincaidGradeLevel(double v) {
    if (Readability_Type.featOkTst && ((Readability_Type)jcasType).casFeat_fleschKincaidGradeLevel == null)
      jcasType.jcas.throwFeatMissing("fleschKincaidGradeLevel", "de.aitools.ie.uima.type.core.Readability");
    jcasType.ll_cas.ll_setDoubleValue(addr, ((Readability_Type)jcasType).casFeatCode_fleschKincaidGradeLevel, v);}    
   
    
  //*--------------*
  //* Feature: fleschKincaidReadingEaseScore

  /** getter for fleschKincaidReadingEaseScore - gets The Flesh Kincaid Reading-Ease Score defined as
206.835 - 84.6*syllables/words - 1.015*words/sentences
   * @generated
   * @return value of the feature 
   */
  public double getFleschKincaidReadingEaseScore() {
    if (Readability_Type.featOkTst && ((Readability_Type)jcasType).casFeat_fleschKincaidReadingEaseScore == null)
      jcasType.jcas.throwFeatMissing("fleschKincaidReadingEaseScore", "de.aitools.ie.uima.type.core.Readability");
    return jcasType.ll_cas.ll_getDoubleValue(addr, ((Readability_Type)jcasType).casFeatCode_fleschKincaidReadingEaseScore);}
    
  /** setter for fleschKincaidReadingEaseScore - sets The Flesh Kincaid Reading-Ease Score defined as
206.835 - 84.6*syllables/words - 1.015*words/sentences 
   * @generated
   * @param v value to set into the feature 
   */
  public void setFleschKincaidReadingEaseScore(double v) {
    if (Readability_Type.featOkTst && ((Readability_Type)jcasType).casFeat_fleschKincaidReadingEaseScore == null)
      jcasType.jcas.throwFeatMissing("fleschKincaidReadingEaseScore", "de.aitools.ie.uima.type.core.Readability");
    jcasType.ll_cas.ll_setDoubleValue(addr, ((Readability_Type)jcasType).casFeatCode_fleschKincaidReadingEaseScore, v);}    
   
    
  //*--------------*
  //* Feature: gunningFogIndex

  /** getter for gunningFogIndex - gets The Gunning Fog Index defined as
0.4 * (words/sentences + 100*(words-with-3-or-more-syllables/words))
   * @generated
   * @return value of the feature 
   */
  public double getGunningFogIndex() {
    if (Readability_Type.featOkTst && ((Readability_Type)jcasType).casFeat_gunningFogIndex == null)
      jcasType.jcas.throwFeatMissing("gunningFogIndex", "de.aitools.ie.uima.type.core.Readability");
    return jcasType.ll_cas.ll_getDoubleValue(addr, ((Readability_Type)jcasType).casFeatCode_gunningFogIndex);}
    
  /** setter for gunningFogIndex - sets The Gunning Fog Index defined as
0.4 * (words/sentences + 100*(words-with-3-or-more-syllables/words)) 
   * @generated
   * @param v value to set into the feature 
   */
  public void setGunningFogIndex(double v) {
    if (Readability_Type.featOkTst && ((Readability_Type)jcasType).casFeat_gunningFogIndex == null)
      jcasType.jcas.throwFeatMissing("gunningFogIndex", "de.aitools.ie.uima.type.core.Readability");
    jcasType.ll_cas.ll_setDoubleValue(addr, ((Readability_Type)jcasType).casFeatCode_gunningFogIndex, v);}    
   
    
  //*--------------*
  //* Feature: lix

  /** getter for lix - gets The LIX is defined as
words/sentences + 100*(words-with-6-or-more-characters/words)
   * @generated
   * @return value of the feature 
   */
  public double getLix() {
    if (Readability_Type.featOkTst && ((Readability_Type)jcasType).casFeat_lix == null)
      jcasType.jcas.throwFeatMissing("lix", "de.aitools.ie.uima.type.core.Readability");
    return jcasType.ll_cas.ll_getDoubleValue(addr, ((Readability_Type)jcasType).casFeatCode_lix);}
    
  /** setter for lix - sets The LIX is defined as
words/sentences + 100*(words-with-6-or-more-characters/words) 
   * @generated
   * @param v value to set into the feature 
   */
  public void setLix(double v) {
    if (Readability_Type.featOkTst && ((Readability_Type)jcasType).casFeat_lix == null)
      jcasType.jcas.throwFeatMissing("lix", "de.aitools.ie.uima.type.core.Readability");
    jcasType.ll_cas.ll_setDoubleValue(addr, ((Readability_Type)jcasType).casFeatCode_lix, v);}    
   
    
  //*--------------*
  //* Feature: mcAlpineEFlawScore

  /** getter for mcAlpineEFlawScore - gets McAlpine EFLAW Score defined as
(words+words-with-at-most-3-characters)/sentences
   * @generated
   * @return value of the feature 
   */
  public double getMcAlpineEFlawScore() {
    if (Readability_Type.featOkTst && ((Readability_Type)jcasType).casFeat_mcAlpineEFlawScore == null)
      jcasType.jcas.throwFeatMissing("mcAlpineEFlawScore", "de.aitools.ie.uima.type.core.Readability");
    return jcasType.ll_cas.ll_getDoubleValue(addr, ((Readability_Type)jcasType).casFeatCode_mcAlpineEFlawScore);}
    
  /** setter for mcAlpineEFlawScore - sets McAlpine EFLAW Score defined as
(words+words-with-at-most-3-characters)/sentences 
   * @generated
   * @param v value to set into the feature 
   */
  public void setMcAlpineEFlawScore(double v) {
    if (Readability_Type.featOkTst && ((Readability_Type)jcasType).casFeat_mcAlpineEFlawScore == null)
      jcasType.jcas.throwFeatMissing("mcAlpineEFlawScore", "de.aitools.ie.uima.type.core.Readability");
    jcasType.ll_cas.ll_setDoubleValue(addr, ((Readability_Type)jcasType).casFeatCode_mcAlpineEFlawScore, v);}    
   
    
  //*--------------*
  //* Feature: rix

  /** getter for rix - gets The RIX is defined as
words-with-more-than-6-characters/sentences
   * @generated
   * @return value of the feature 
   */
  public double getRix() {
    if (Readability_Type.featOkTst && ((Readability_Type)jcasType).casFeat_rix == null)
      jcasType.jcas.throwFeatMissing("rix", "de.aitools.ie.uima.type.core.Readability");
    return jcasType.ll_cas.ll_getDoubleValue(addr, ((Readability_Type)jcasType).casFeatCode_rix);}
    
  /** setter for rix - sets The RIX is defined as
words-with-more-than-6-characters/sentences 
   * @generated
   * @param v value to set into the feature 
   */
  public void setRix(double v) {
    if (Readability_Type.featOkTst && ((Readability_Type)jcasType).casFeat_rix == null)
      jcasType.jcas.throwFeatMissing("rix", "de.aitools.ie.uima.type.core.Readability");
    jcasType.ll_cas.ll_setDoubleValue(addr, ((Readability_Type)jcasType).casFeatCode_rix, v);}    
   
    
  //*--------------*
  //* Feature: smogGrade

  /** getter for smogGrade - gets The SMOG Grade defined as
sqrt((words-with-3-or-more-syllables/sentences) * 30) + 3
   * @generated
   * @return value of the feature 
   */
  public double getSmogGrade() {
    if (Readability_Type.featOkTst && ((Readability_Type)jcasType).casFeat_smogGrade == null)
      jcasType.jcas.throwFeatMissing("smogGrade", "de.aitools.ie.uima.type.core.Readability");
    return jcasType.ll_cas.ll_getDoubleValue(addr, ((Readability_Type)jcasType).casFeatCode_smogGrade);}
    
  /** setter for smogGrade - sets The SMOG Grade defined as
sqrt((words-with-3-or-more-syllables/sentences) * 30) + 3 
   * @generated
   * @param v value to set into the feature 
   */
  public void setSmogGrade(double v) {
    if (Readability_Type.featOkTst && ((Readability_Type)jcasType).casFeat_smogGrade == null)
      jcasType.jcas.throwFeatMissing("smogGrade", "de.aitools.ie.uima.type.core.Readability");
    jcasType.ll_cas.ll_setDoubleValue(addr, ((Readability_Type)jcasType).casFeatCode_smogGrade, v);}    
   
    
  //*--------------*
  //* Feature: strainIndex

  /** getter for strainIndex - gets The Strain Index defined as
syllables-in-first-3-sentences/10
   * @generated
   * @return value of the feature 
   */
  public double getStrainIndex() {
    if (Readability_Type.featOkTst && ((Readability_Type)jcasType).casFeat_strainIndex == null)
      jcasType.jcas.throwFeatMissing("strainIndex", "de.aitools.ie.uima.type.core.Readability");
    return jcasType.ll_cas.ll_getDoubleValue(addr, ((Readability_Type)jcasType).casFeatCode_strainIndex);}
    
  /** setter for strainIndex - sets The Strain Index defined as
syllables-in-first-3-sentences/10 
   * @generated
   * @param v value to set into the feature 
   */
  public void setStrainIndex(double v) {
    if (Readability_Type.featOkTst && ((Readability_Type)jcasType).casFeat_strainIndex == null)
      jcasType.jcas.throwFeatMissing("strainIndex", "de.aitools.ie.uima.type.core.Readability");
    jcasType.ll_cas.ll_setDoubleValue(addr, ((Readability_Type)jcasType).casFeatCode_strainIndex, v);}    
  }

    