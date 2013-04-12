

/* First created by JCasGen Tue Jun 15 16:17:39 CEST 2010 */
package org.barcelonamedia.uima.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Mon Mar 25 11:07:50 CET 2013
 * XML source: /home/joan.codina/uima_svn/code/WekaClassifier/desc/WekaClassifier.xml
 * @generated */
public class AttributeValue extends Annotation {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(AttributeValue.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected AttributeValue() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public AttributeValue(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public AttributeValue(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public AttributeValue(JCas jcas, int begin, int end) {
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
     
 
    
  //*--------------*
  //* Feature: componentId

  /** getter for componentId - gets 
   * @generated */
  public String getComponentId() {
    if (AttributeValue_Type.featOkTst && ((AttributeValue_Type)jcasType).casFeat_componentId == null)
      jcasType.jcas.throwFeatMissing("componentId", "org.barcelonamedia.uima.types.AttributeValue");
    return jcasType.ll_cas.ll_getStringValue(addr, ((AttributeValue_Type)jcasType).casFeatCode_componentId);}
    
  /** setter for componentId - sets  
   * @generated */
  public void setComponentId(String v) {
    if (AttributeValue_Type.featOkTst && ((AttributeValue_Type)jcasType).casFeat_componentId == null)
      jcasType.jcas.throwFeatMissing("componentId", "org.barcelonamedia.uima.types.AttributeValue");
    jcasType.ll_cas.ll_setStringValue(addr, ((AttributeValue_Type)jcasType).casFeatCode_componentId, v);}    
   
    
  //*--------------*
  //* Feature: attributeName

  /** getter for attributeName - gets 
   * @generated */
  public String getAttributeName() {
    if (AttributeValue_Type.featOkTst && ((AttributeValue_Type)jcasType).casFeat_attributeName == null)
      jcasType.jcas.throwFeatMissing("attributeName", "org.barcelonamedia.uima.types.AttributeValue");
    return jcasType.ll_cas.ll_getStringValue(addr, ((AttributeValue_Type)jcasType).casFeatCode_attributeName);}
    
  /** setter for attributeName - sets  
   * @generated */
  public void setAttributeName(String v) {
    if (AttributeValue_Type.featOkTst && ((AttributeValue_Type)jcasType).casFeat_attributeName == null)
      jcasType.jcas.throwFeatMissing("attributeName", "org.barcelonamedia.uima.types.AttributeValue");
    jcasType.ll_cas.ll_setStringValue(addr, ((AttributeValue_Type)jcasType).casFeatCode_attributeName, v);}    
   
    
  //*--------------*
  //* Feature: value

  /** getter for value - gets 
   * @generated */
  public String getValue() {
    if (AttributeValue_Type.featOkTst && ((AttributeValue_Type)jcasType).casFeat_value == null)
      jcasType.jcas.throwFeatMissing("value", "org.barcelonamedia.uima.types.AttributeValue");
    return jcasType.ll_cas.ll_getStringValue(addr, ((AttributeValue_Type)jcasType).casFeatCode_value);}
    
  /** setter for value - sets  
   * @generated */
  public void setValue(String v) {
    if (AttributeValue_Type.featOkTst && ((AttributeValue_Type)jcasType).casFeat_value == null)
      jcasType.jcas.throwFeatMissing("value", "org.barcelonamedia.uima.types.AttributeValue");
    jcasType.ll_cas.ll_setStringValue(addr, ((AttributeValue_Type)jcasType).casFeatCode_value, v);}    
  }

    