
/* First created by JCasGen Sat May 13 15:24:45 CEST 2017 */
package type.rhetoricDetection;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** Type system for helping constituents/units in detection of rhetorical devices.
 * Updated by JCasGen Tue Feb 26 23:13:10 CET 2019
 * @generated */
public class RhetoricConstituents_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = RhetoricConstituents.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("type.rhetoricDetection.RhetoricConstituents");
 
  /** @generated */
  final Feature casFeat_unitType;
  /** @generated */
  final int     casFeatCode_unitType;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getUnitType(int addr) {
        if (featOkTst && casFeat_unitType == null)
      jcas.throwFeatMissing("unitType", "type.rhetoricDetection.RhetoricConstituents");
    return ll_cas.ll_getStringValue(addr, casFeatCode_unitType);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setUnitType(int addr, String v) {
        if (featOkTst && casFeat_unitType == null)
      jcas.throwFeatMissing("unitType", "type.rhetoricDetection.RhetoricConstituents");
    ll_cas.ll_setStringValue(addr, casFeatCode_unitType, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public RhetoricConstituents_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_unitType = jcas.getRequiredFeatureDE(casType, "unitType", "uima.cas.String", featOkTst);
    casFeatCode_unitType  = (null == casFeat_unitType) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_unitType).getCode();

  }
}



    