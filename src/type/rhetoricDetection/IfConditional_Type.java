
/* First created by JCasGen Sat Feb 11 18:05:34 CET 2017 */
package type.rhetoricDetection;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** Type system for if-conditional sentences. It is used in detection if-conditionals of all types in the text. Separate type system is created because it has distinct features.
 * Updated by JCasGen Tue Feb 26 23:13:10 CET 2019
 * @generated */
public class IfConditional_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = IfConditional.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("type.rhetoricDetection.IfConditional");
 
  /** @generated */
  final Feature casFeat_Pclause;
  /** @generated */
  final int     casFeatCode_Pclause;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getPclause(int addr) {
        if (featOkTst && casFeat_Pclause == null)
      jcas.throwFeatMissing("Pclause", "type.rhetoricDetection.IfConditional");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Pclause);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setPclause(int addr, String v) {
        if (featOkTst && casFeat_Pclause == null)
      jcas.throwFeatMissing("Pclause", "type.rhetoricDetection.IfConditional");
    ll_cas.ll_setStringValue(addr, casFeatCode_Pclause, v);}
    
  
 
  /** @generated */
  final Feature casFeat_Qclause;
  /** @generated */
  final int     casFeatCode_Qclause;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getQclause(int addr) {
        if (featOkTst && casFeat_Qclause == null)
      jcas.throwFeatMissing("Qclause", "type.rhetoricDetection.IfConditional");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Qclause);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setQclause(int addr, String v) {
        if (featOkTst && casFeat_Qclause == null)
      jcas.throwFeatMissing("Qclause", "type.rhetoricDetection.IfConditional");
    ll_cas.ll_setStringValue(addr, casFeatCode_Qclause, v);}
    
  
 
  /** @generated */
  final Feature casFeat_Rclause;
  /** @generated */
  final int     casFeatCode_Rclause;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getRclause(int addr) {
        if (featOkTst && casFeat_Rclause == null)
      jcas.throwFeatMissing("Rclause", "type.rhetoricDetection.IfConditional");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Rclause);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setRclause(int addr, String v) {
        if (featOkTst && casFeat_Rclause == null)
      jcas.throwFeatMissing("Rclause", "type.rhetoricDetection.IfConditional");
    ll_cas.ll_setStringValue(addr, casFeatCode_Rclause, v);}
    
  
 
  /** @generated */
  final Feature casFeat_matchId;
  /** @generated */
  final int     casFeatCode_matchId;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getMatchId(int addr) {
        if (featOkTst && casFeat_matchId == null)
      jcas.throwFeatMissing("matchId", "type.rhetoricDetection.IfConditional");
    return ll_cas.ll_getIntValue(addr, casFeatCode_matchId);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setMatchId(int addr, int v) {
        if (featOkTst && casFeat_matchId == null)
      jcas.throwFeatMissing("matchId", "type.rhetoricDetection.IfConditional");
    ll_cas.ll_setIntValue(addr, casFeatCode_matchId, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public IfConditional_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_Pclause = jcas.getRequiredFeatureDE(casType, "Pclause", "uima.cas.String", featOkTst);
    casFeatCode_Pclause  = (null == casFeat_Pclause) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Pclause).getCode();

 
    casFeat_Qclause = jcas.getRequiredFeatureDE(casType, "Qclause", "uima.cas.String", featOkTst);
    casFeatCode_Qclause  = (null == casFeat_Qclause) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Qclause).getCode();

 
    casFeat_Rclause = jcas.getRequiredFeatureDE(casType, "Rclause", "uima.cas.String", featOkTst);
    casFeatCode_Rclause  = (null == casFeat_Rclause) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Rclause).getCode();

 
    casFeat_matchId = jcas.getRequiredFeatureDE(casType, "matchId", "uima.cas.Integer", featOkTst);
    casFeatCode_matchId  = (null == casFeat_matchId) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_matchId).getCode();

  }
}



    