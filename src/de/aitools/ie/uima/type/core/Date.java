

/* First created by JCasGen Wed Feb 15 17:58:15 CET 2017 */
package de.aitools.ie.uima.type.core;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import de.aitools.ie.uima.type.supertype.NumericExpression;


/** A date.
 * Updated by JCasGen Thu Feb 16 01:02:57 CET 2017
 * XML source: C:/Users/SpongeBob/workspace/thesis-rhetoric-detection/conf/uima-descriptors/type-systems/RhetoricalDevices.xml
 * @generated */
public class Date extends NumericExpression {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Date.class);
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
  protected Date() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Date(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Date(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Date(JCas jcas, int begin, int end) {
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
  //* Feature: normalized

  /** getter for normalized - gets The normalized form of a date. Should be encoded as YYYY-MM-DD.
   * @generated
   * @return value of the feature 
   */
  public String getNormalized() {
    if (Date_Type.featOkTst && ((Date_Type)jcasType).casFeat_normalized == null)
      jcasType.jcas.throwFeatMissing("normalized", "de.aitools.ie.uima.type.core.Date");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Date_Type)jcasType).casFeatCode_normalized);}
    
  /** setter for normalized - sets The normalized form of a date. Should be encoded as YYYY-MM-DD. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setNormalized(String v) {
    if (Date_Type.featOkTst && ((Date_Type)jcasType).casFeat_normalized == null)
      jcasType.jcas.throwFeatMissing("normalized", "de.aitools.ie.uima.type.core.Date");
    jcasType.ll_cas.ll_setStringValue(addr, ((Date_Type)jcasType).casFeatCode_normalized, v);}    
  }

    