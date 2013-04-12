

/* First created by JCasGen Tue Jun 15 16:17:39 CEST 2010 */
package org.barcelonamedia.uima.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** Corresponds to the Solr field.
 * Updated by JCasGen Mon Mar 25 11:07:50 CET 2013
 * XML source: /home/joan.codina/uima_svn/code/WekaClassifier/desc/WekaClassifier.xml
 * @generated */
public class SolrField extends AttributeValue {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(SolrField.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected SolrField() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public SolrField(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public SolrField(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public SolrField(JCas jcas, int begin, int end) {
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

    