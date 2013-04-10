

/* First created by JCasGen Mon Oct 01 16:33:34 CEST 2012 */
package org.barcelonamedia.uima.lemmatizer.internal_ts;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** Temporary annotation for lemma output of ConceptMapper.
 * Updated by JCasGen Mon Oct 01 16:33:35 CEST 2012
 * XML source: /NAS_Backup/jens.grivolla/workspace/bm-uima/Lemmatizer/desc/ae/bm-lemmatizer-internal-types.xml
 * @generated */
public class ConceptMapperLemma extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(ConceptMapperLemma.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated  */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected ConceptMapperLemma() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public ConceptMapperLemma(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public ConceptMapperLemma(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public ConceptMapperLemma(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  @generated modifiable */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: pos

  /** getter for pos - gets 
   * @generated */
  public String getPos() {
    if (ConceptMapperLemma_Type.featOkTst && ((ConceptMapperLemma_Type)jcasType).casFeat_pos == null)
      jcasType.jcas.throwFeatMissing("pos", "org.barcelonamedia.uima.lemmatizer.internal_ts.ConceptMapperLemma");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ConceptMapperLemma_Type)jcasType).casFeatCode_pos);}
    
  /** setter for pos - sets  
   * @generated */
  public void setPos(String v) {
    if (ConceptMapperLemma_Type.featOkTst && ((ConceptMapperLemma_Type)jcasType).casFeat_pos == null)
      jcasType.jcas.throwFeatMissing("pos", "org.barcelonamedia.uima.lemmatizer.internal_ts.ConceptMapperLemma");
    jcasType.ll_cas.ll_setStringValue(addr, ((ConceptMapperLemma_Type)jcasType).casFeatCode_pos, v);}    
   
    
  //*--------------*
  //* Feature: value

  /** getter for value - gets 
   * @generated */
  public String getValue() {
    if (ConceptMapperLemma_Type.featOkTst && ((ConceptMapperLemma_Type)jcasType).casFeat_value == null)
      jcasType.jcas.throwFeatMissing("value", "org.barcelonamedia.uima.lemmatizer.internal_ts.ConceptMapperLemma");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ConceptMapperLemma_Type)jcasType).casFeatCode_value);}
    
  /** setter for value - sets  
   * @generated */
  public void setValue(String v) {
    if (ConceptMapperLemma_Type.featOkTst && ((ConceptMapperLemma_Type)jcasType).casFeat_value == null)
      jcasType.jcas.throwFeatMissing("value", "org.barcelonamedia.uima.lemmatizer.internal_ts.ConceptMapperLemma");
    jcasType.ll_cas.ll_setStringValue(addr, ((ConceptMapperLemma_Type)jcasType).casFeatCode_value, v);}    
  }

    