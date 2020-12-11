

/* First created by JCasGen Wed Feb 15 17:58:15 CET 2017 */
package de.aitools.ie.uima.type.core;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import de.aitools.ie.uima.type.supertype.GrammaticalUnit;


/** A sequence of tokens as output by a constituency parser.
 * Updated by JCasGen Thu Feb 16 01:02:56 CET 2017
 * XML source: C:/Users/SpongeBob/workspace/thesis-rhetoric-detection/conf/uima-descriptors/type-systems/RhetoricalDevices.xml
 * @generated */
public class Constituent extends GrammaticalUnit {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Constituent.class);
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
  protected Constituent() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Constituent(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Constituent(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Constituent(JCas jcas, int begin, int end) {
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
  //* Feature: label

  /** getter for label - gets The label of this constituent as provided by the constituency parser.
   * @generated
   * @return value of the feature 
   */
  public String getLabel() {
    if (Constituent_Type.featOkTst && ((Constituent_Type)jcasType).casFeat_label == null)
      jcasType.jcas.throwFeatMissing("label", "de.aitools.ie.uima.type.core.Constituent");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Constituent_Type)jcasType).casFeatCode_label);}
    
  /** setter for label - sets The label of this constituent as provided by the constituency parser. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setLabel(String v) {
    if (Constituent_Type.featOkTst && ((Constituent_Type)jcasType).casFeat_label == null)
      jcasType.jcas.throwFeatMissing("label", "de.aitools.ie.uima.type.core.Constituent");
    jcasType.ll_cas.ll_setStringValue(addr, ((Constituent_Type)jcasType).casFeatCode_label, v);}    
   
    
  //*--------------*
  //* Feature: parent

  /** getter for parent - gets The parent of this constituent in the tree or null if this is the root.
   * @generated
   * @return value of the feature 
   */
  public Constituent getParent() {
    if (Constituent_Type.featOkTst && ((Constituent_Type)jcasType).casFeat_parent == null)
      jcasType.jcas.throwFeatMissing("parent", "de.aitools.ie.uima.type.core.Constituent");
    return (Constituent)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Constituent_Type)jcasType).casFeatCode_parent)));}
    
  /** setter for parent - sets The parent of this constituent in the tree or null if this is the root. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setParent(Constituent v) {
    if (Constituent_Type.featOkTst && ((Constituent_Type)jcasType).casFeat_parent == null)
      jcasType.jcas.throwFeatMissing("parent", "de.aitools.ie.uima.type.core.Constituent");
    jcasType.ll_cas.ll_setRefValue(addr, ((Constituent_Type)jcasType).casFeatCode_parent, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: treeDepth

  /** getter for treeDepth - gets The tree depth is defined as the length of the longest path from this node to a leaf node. Leaf nodes have depth zero.
   * @generated
   * @return value of the feature 
   */
  public int getTreeDepth() {
    if (Constituent_Type.featOkTst && ((Constituent_Type)jcasType).casFeat_treeDepth == null)
      jcasType.jcas.throwFeatMissing("treeDepth", "de.aitools.ie.uima.type.core.Constituent");
    return jcasType.ll_cas.ll_getIntValue(addr, ((Constituent_Type)jcasType).casFeatCode_treeDepth);}
    
  /** setter for treeDepth - sets The tree depth is defined as the length of the longest path from this node to a leaf node. Leaf nodes have depth zero. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setTreeDepth(int v) {
    if (Constituent_Type.featOkTst && ((Constituent_Type)jcasType).casFeat_treeDepth == null)
      jcasType.jcas.throwFeatMissing("treeDepth", "de.aitools.ie.uima.type.core.Constituent");
    jcasType.ll_cas.ll_setIntValue(addr, ((Constituent_Type)jcasType).casFeatCode_treeDepth, v);}    
  }

    