
/* First created by JCasGen Sun May 14 18:02:29 CEST 2017 */
package type.rhetoricDetection;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** Type system for all the implemented rhetorical devices.
 * Updated by JCasGen Tue Feb 26 23:13:10 CET 2019
 * @generated */
public class RhetoricalDevice_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = RhetoricalDevice.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("type.rhetoricDetection.RhetoricalDevice");
 
  /** @generated */
  final Feature casFeat_deviceType;
  /** @generated */
  final int     casFeatCode_deviceType;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getDeviceType(int addr) {
        if (featOkTst && casFeat_deviceType == null)
      jcas.throwFeatMissing("deviceType", "type.rhetoricDetection.RhetoricalDevice");
    return ll_cas.ll_getStringValue(addr, casFeatCode_deviceType);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setDeviceType(int addr, String v) {
        if (featOkTst && casFeat_deviceType == null)
      jcas.throwFeatMissing("deviceType", "type.rhetoricDetection.RhetoricalDevice");
    ll_cas.ll_setStringValue(addr, casFeatCode_deviceType, v);}
    
  
 
  /** @generated */
  final Feature casFeat_category;
  /** @generated */
  final int     casFeatCode_category;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getCategory(int addr) {
        if (featOkTst && casFeat_category == null)
      jcas.throwFeatMissing("category", "type.rhetoricDetection.RhetoricalDevice");
    return ll_cas.ll_getStringValue(addr, casFeatCode_category);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setCategory(int addr, String v) {
        if (featOkTst && casFeat_category == null)
      jcas.throwFeatMissing("category", "type.rhetoricDetection.RhetoricalDevice");
    ll_cas.ll_setStringValue(addr, casFeatCode_category, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public RhetoricalDevice_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_deviceType = jcas.getRequiredFeatureDE(casType, "deviceType", "uima.cas.String", featOkTst);
    casFeatCode_deviceType  = (null == casFeat_deviceType) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_deviceType).getCode();

 
    casFeat_category = jcas.getRequiredFeatureDE(casType, "category", "uima.cas.String", featOkTst);
    casFeatCode_category  = (null == casFeat_category) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_category).getCode();

  }
}



    