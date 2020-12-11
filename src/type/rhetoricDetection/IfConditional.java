

/* First created by JCasGen Sat Feb 11 18:05:34 CET 2017 */
package type.rhetoricDetection;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** Type system for if-conditional sentences. It is used in detection if-conditionals of all types in the text. Separate type system is created because it has distinct features.
 * Updated by JCasGen Tue Feb 26 23:13:09 CET 2019
 * XML source: C:/Users/viorel.morari/java-workspace/thesis-rhetoric-backend-web/conf/uima-descriptors/type-systems/RhetoricTypeSystem.xml
 * @generated */
public class IfConditional extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(IfConditional.class);
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
  protected IfConditional() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public IfConditional(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public IfConditional(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public IfConditional(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  private void readObject() {/*default - does nothing empty block */}
     
  public IfConditional(JCas jcas, int begin, int end, String Pclause, String Qclause, String Rclause) {
	    super(jcas);
	    setBegin(begin);
	    setEnd(end);
	    setPclause(Pclause);
	    setQclause(Qclause);
	    setRclause(Rclause);
	    readObject();
	  } 
  
    
  //*--------------*
  //* Feature: Pclause

  /** getter for Pclause - gets Conditional(P) clause in a sentence (i.e. If P, then Q).
   * @generated
   * @return value of the feature 
   */
  public String getPclause() {
    if (IfConditional_Type.featOkTst && ((IfConditional_Type)jcasType).casFeat_Pclause == null)
      jcasType.jcas.throwFeatMissing("Pclause", "type.rhetoricDetection.IfConditional");
    return jcasType.ll_cas.ll_getStringValue(addr, ((IfConditional_Type)jcasType).casFeatCode_Pclause);}
    
  /** setter for Pclause - sets Conditional(P) clause in a sentence (i.e. If P, then Q). 
   * @generated
   * @param v value to set into the feature 
   */
  public void setPclause(String v) {
    if (IfConditional_Type.featOkTst && ((IfConditional_Type)jcasType).casFeat_Pclause == null)
      jcasType.jcas.throwFeatMissing("Pclause", "type.rhetoricDetection.IfConditional");
    jcasType.ll_cas.ll_setStringValue(addr, ((IfConditional_Type)jcasType).casFeatCode_Pclause, v);}    
   
    
  //*--------------*
  //* Feature: Qclause

  /** getter for Qclause - gets Head(Q) clause in a sentence (If P, then Q).
   * @generated
   * @return value of the feature 
   */
  public String getQclause() {
    if (IfConditional_Type.featOkTst && ((IfConditional_Type)jcasType).casFeat_Qclause == null)
      jcasType.jcas.throwFeatMissing("Qclause", "type.rhetoricDetection.IfConditional");
    return jcasType.ll_cas.ll_getStringValue(addr, ((IfConditional_Type)jcasType).casFeatCode_Qclause);}
    
  /** setter for Qclause - sets Head(Q) clause in a sentence (If P, then Q). 
   * @generated
   * @param v value to set into the feature 
   */
  public void setQclause(String v) {
    if (IfConditional_Type.featOkTst && ((IfConditional_Type)jcasType).casFeat_Qclause == null)
      jcasType.jcas.throwFeatMissing("Qclause", "type.rhetoricDetection.IfConditional");
    jcasType.ll_cas.ll_setStringValue(addr, ((IfConditional_Type)jcasType).casFeatCode_Qclause, v);}    
   
    
  //*--------------*
  //* Feature: Rclause

  /** getter for Rclause - gets Remaining part of the conditional sentence after extracting the P and Q clauses.
   * @generated
   * @return value of the feature 
   */
  public String getRclause() {
    if (IfConditional_Type.featOkTst && ((IfConditional_Type)jcasType).casFeat_Rclause == null)
      jcasType.jcas.throwFeatMissing("Rclause", "type.rhetoricDetection.IfConditional");
    return jcasType.ll_cas.ll_getStringValue(addr, ((IfConditional_Type)jcasType).casFeatCode_Rclause);}
    
  /** setter for Rclause - sets Remaining part of the conditional sentence after extracting the P and Q clauses. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setRclause(String v) {
    if (IfConditional_Type.featOkTst && ((IfConditional_Type)jcasType).casFeat_Rclause == null)
      jcasType.jcas.throwFeatMissing("Rclause", "type.rhetoricDetection.IfConditional");
    jcasType.ll_cas.ll_setStringValue(addr, ((IfConditional_Type)jcasType).casFeatCode_Rclause, v);}    
   
    
  //*--------------*
  //* Feature: matchId

  /** getter for matchId - gets The Id of the sentence.
   * @generated
   * @return value of the feature 
   */
  public int getMatchId() {
    if (IfConditional_Type.featOkTst && ((IfConditional_Type)jcasType).casFeat_matchId == null)
      jcasType.jcas.throwFeatMissing("matchId", "type.rhetoricDetection.IfConditional");
    return jcasType.ll_cas.ll_getIntValue(addr, ((IfConditional_Type)jcasType).casFeatCode_matchId);}
    
  /** setter for matchId - sets The Id of the sentence. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setMatchId(int v) {
    if (IfConditional_Type.featOkTst && ((IfConditional_Type)jcasType).casFeat_matchId == null)
      jcasType.jcas.throwFeatMissing("matchId", "type.rhetoricDetection.IfConditional");
    jcasType.ll_cas.ll_setIntValue(addr, ((IfConditional_Type)jcasType).casFeatCode_matchId, v);}    
  }

    