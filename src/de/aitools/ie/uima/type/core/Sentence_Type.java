
/* First created by JCasGen Wed Feb 15 17:58:15 CET 2017 */
package de.aitools.ie.uima.type.core;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import de.aitools.ie.uima.type.supertype.GrammaticalUnit_Type;

/** A sentence.
 * Updated by JCasGen Thu Feb 16 01:02:58 CET 2017
 * @generated */
public class Sentence_Type extends GrammaticalUnit_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Sentence.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("de.aitools.ie.uima.type.core.Sentence");



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Sentence_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

  }
}



    