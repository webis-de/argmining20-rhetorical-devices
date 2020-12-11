

/* First created by JCasGen Wed Feb 15 17:58:16 CET 2017 */
package type.rhetoricDetection;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;
import de.aitools.ie.uima.type.core.Constituent;


/** The conditional clause of the If conditional sentence.
 * Updated by JCasGen Tue Feb 26 23:13:10 CET 2019
 * XML source: C:/Users/viorel.morari/java-workspace/thesis-rhetoric-backend-web/conf/uima-descriptors/type-systems/RhetoricTypeSystem.xml
 * @generated */
public class Pclause extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Pclause.class);
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
  protected Pclause() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Pclause(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Pclause(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Pclause(JCas jcas, int begin, int end) {
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
  //* Feature: parent

  /** getter for parent - gets Pointer to the If-conditional sentence.
   * @generated
   * @return value of the feature 
   */
  public int getParent() {
    if (Pclause_Type.featOkTst && ((Pclause_Type)jcasType).casFeat_parent == null)
      jcasType.jcas.throwFeatMissing("parent", "type.rhetoricDetection.Pclause");
    return jcasType.ll_cas.ll_getIntValue(addr, ((Pclause_Type)jcasType).casFeatCode_parent);}
    
  /** setter for parent - sets Pointer to the If-conditional sentence. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setParent(int v) {
    if (Pclause_Type.featOkTst && ((Pclause_Type)jcasType).casFeat_parent == null)
      jcasType.jcas.throwFeatMissing("parent", "type.rhetoricDetection.Pclause");
    jcasType.ll_cas.ll_setIntValue(addr, ((Pclause_Type)jcasType).casFeatCode_parent, v);}    
  }

    