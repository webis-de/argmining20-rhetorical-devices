
/* First created by JCasGen Mon Jan 30 01:15:55 CET 2017 */
package type.rhetoricDetection;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** A suplimentary type of verb annotations used inside Passive Voice detection Ruta rule. It is used as a separated type system because of restriction in the ruta rule.
 * Updated by JCasGen Tue Feb 26 23:13:10 CET 2019
 * @generated */
public class verbsPassiveVoice_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = verbsPassiveVoice.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("type.rhetoricDetection.verbsPassiveVoice");



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public verbsPassiveVoice_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

  }
}



    