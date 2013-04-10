

/* First created by JCasGen Mon Mar 29 10:16:29 CEST 2010 */
package org.barcelonamedia.uima.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** Corresponds to the ARFF numeric attributes.
 * Updated by JCasGen Mon Mar 29 10:16:31 CEST 2010
 * XML source: /home/roberto.carlini/eclipse_workspaces/uima_workspace/WekaConsumer/desc/cas_consumer/ARFFDataFileCasConsumer.xml
 * @generated */
public class NumericAttributeValue extends AttributeValue {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(NumericAttributeValue.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected NumericAttributeValue() {}
    
  /** Internal - constructor used by generator 
   * @generated */
  public NumericAttributeValue(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public NumericAttributeValue(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public NumericAttributeValue(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  @generated modifiable */
  private void readObject() {}
     
}

    