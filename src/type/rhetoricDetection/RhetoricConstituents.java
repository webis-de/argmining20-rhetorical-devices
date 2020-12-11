

/* First created by JCasGen Sat May 13 15:24:45 CEST 2017 */
package type.rhetoricDetection;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** Type system for helping constituents/units in detection of rhetorical devices.
 * Updated by JCasGen Tue Feb 26 23:13:10 CET 2019
 * XML source: C:/Users/viorel.morari/java-workspace/thesis-rhetoric-backend-web/conf/uima-descriptors/type-systems/RhetoricTypeSystem.xml
 * @generated */
public class RhetoricConstituents extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(RhetoricConstituents.class);
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
  protected RhetoricConstituents() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public RhetoricConstituents(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public RhetoricConstituents(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public RhetoricConstituents(JCas jcas, int begin, int end) {
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
  //* Feature: unitType

  /** getter for unitType - gets Type of the rhetoric constituent.
   * @generated
   * @return value of the feature 
   */
  public String getUnitType() {
    if (RhetoricConstituents_Type.featOkTst && ((RhetoricConstituents_Type)jcasType).casFeat_unitType == null)
      jcasType.jcas.throwFeatMissing("unitType", "type.rhetoricDetection.RhetoricConstituents");
    return jcasType.ll_cas.ll_getStringValue(addr, ((RhetoricConstituents_Type)jcasType).casFeatCode_unitType);}
    
  /** setter for unitType - sets Type of the rhetoric constituent. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setUnitType(String v) {
    if (RhetoricConstituents_Type.featOkTst && ((RhetoricConstituents_Type)jcasType).casFeat_unitType == null)
      jcasType.jcas.throwFeatMissing("unitType", "type.rhetoricDetection.RhetoricConstituents");
    jcasType.ll_cas.ll_setStringValue(addr, ((RhetoricConstituents_Type)jcasType).casFeatCode_unitType, v);}    
  }

    