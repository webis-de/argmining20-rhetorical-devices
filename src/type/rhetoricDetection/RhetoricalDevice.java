

/* First created by JCasGen Sun May 14 18:02:29 CEST 2017 */
package type.rhetoricDetection;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** Type system for all the implemented rhetorical devices.
 * Updated by JCasGen Tue Feb 26 23:13:10 CET 2019
 * XML source: C:/Users/viorel.morari/java-workspace/thesis-rhetoric-backend-web/conf/uima-descriptors/type-systems/RhetoricTypeSystem.xml
 * @generated */
public class RhetoricalDevice extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(RhetoricalDevice.class);
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
  protected RhetoricalDevice() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public RhetoricalDevice(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public RhetoricalDevice(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public RhetoricalDevice(JCas jcas, int begin, int end) {
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
  //* Feature: deviceType

  /** getter for deviceType - gets Type of rhetorical device.
   * @generated
   * @return value of the feature 
   */
  public String getDeviceType() {
    if (RhetoricalDevice_Type.featOkTst && ((RhetoricalDevice_Type)jcasType).casFeat_deviceType == null)
      jcasType.jcas.throwFeatMissing("deviceType", "type.rhetoricDetection.RhetoricalDevice");
    return jcasType.ll_cas.ll_getStringValue(addr, ((RhetoricalDevice_Type)jcasType).casFeatCode_deviceType);}
    
  /** setter for deviceType - sets Type of rhetorical device. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setDeviceType(String v) {
    if (RhetoricalDevice_Type.featOkTst && ((RhetoricalDevice_Type)jcasType).casFeat_deviceType == null)
      jcasType.jcas.throwFeatMissing("deviceType", "type.rhetoricDetection.RhetoricalDevice");
    jcasType.ll_cas.ll_setStringValue(addr, ((RhetoricalDevice_Type)jcasType).casFeatCode_deviceType, v);}    
   
    
  //*--------------*
  //* Feature: category

  /** getter for category - gets 
   * @generated
   * @return value of the feature 
   */
  public String getCategory() {
    if (RhetoricalDevice_Type.featOkTst && ((RhetoricalDevice_Type)jcasType).casFeat_category == null)
      jcasType.jcas.throwFeatMissing("category", "type.rhetoricDetection.RhetoricalDevice");
    return jcasType.ll_cas.ll_getStringValue(addr, ((RhetoricalDevice_Type)jcasType).casFeatCode_category);}
    
  /** setter for category - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setCategory(String v) {
    if (RhetoricalDevice_Type.featOkTst && ((RhetoricalDevice_Type)jcasType).casFeat_category == null)
      jcasType.jcas.throwFeatMissing("category", "type.rhetoricDetection.RhetoricalDevice");
    jcasType.ll_cas.ll_setStringValue(addr, ((RhetoricalDevice_Type)jcasType).casFeatCode_category, v);}    
  }

    