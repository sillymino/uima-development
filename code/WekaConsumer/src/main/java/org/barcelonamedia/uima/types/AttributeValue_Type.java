
/* First created by JCasGen Mon Mar 29 10:16:29 CEST 2010 */
package org.barcelonamedia.uima.types;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Mon Mar 29 10:16:31 CEST 2010
 * @generated */
public class AttributeValue_Type extends Annotation_Type {
  /** @generated */
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (AttributeValue_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = AttributeValue_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new AttributeValue(addr, AttributeValue_Type.this);
  			   AttributeValue_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new AttributeValue(addr, AttributeValue_Type.this);
  	  }
    };
  /** @generated */
  public final static int typeIndexID = AttributeValue.typeIndexID;
  /** @generated 
     @modifiable */
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("org.barcelonamedia.uima.types.AttributeValue");
 
  /** @generated */
  final Feature casFeat_attributeName;
  /** @generated */
  final int     casFeatCode_attributeName;
  /** @generated */ 
  public String getAttributeName(int addr) {
        if (featOkTst && casFeat_attributeName == null)
      jcas.throwFeatMissing("attributeName", "org.barcelonamedia.uima.types.AttributeValue");
    return ll_cas.ll_getStringValue(addr, casFeatCode_attributeName);
  }
  /** @generated */    
  public void setAttributeName(int addr, String v) {
        if (featOkTst && casFeat_attributeName == null)
      jcas.throwFeatMissing("attributeName", "org.barcelonamedia.uima.types.AttributeValue");
    ll_cas.ll_setStringValue(addr, casFeatCode_attributeName, v);}
    
  
 
  /** @generated */
  final Feature casFeat_value;
  /** @generated */
  final int     casFeatCode_value;
  /** @generated */ 
  public String getValue(int addr) {
        if (featOkTst && casFeat_value == null)
      jcas.throwFeatMissing("value", "org.barcelonamedia.uima.types.AttributeValue");
    return ll_cas.ll_getStringValue(addr, casFeatCode_value);
  }
  /** @generated */    
  public void setValue(int addr, String v) {
        if (featOkTst && casFeat_value == null)
      jcas.throwFeatMissing("value", "org.barcelonamedia.uima.types.AttributeValue");
    ll_cas.ll_setStringValue(addr, casFeatCode_value, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public AttributeValue_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_attributeName = jcas.getRequiredFeatureDE(casType, "attributeName", "uima.cas.String", featOkTst);
    casFeatCode_attributeName  = (null == casFeat_attributeName) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_attributeName).getCode();

 
    casFeat_value = jcas.getRequiredFeatureDE(casType, "value", "uima.cas.String", featOkTst);
    casFeatCode_value  = (null == casFeat_value) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_value).getCode();

  }
}



    