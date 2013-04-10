
/* First created by JCasGen Tue Jun 15 16:17:39 CEST 2010 */
package org.barcelonamedia.uima.types;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;

/** 
 * Updated by JCasGen Mon Mar 25 11:07:50 CET 2013
 * @generated */
public class StringAttributeValue_Type extends AttributeValue_Type {
  /** @generated */
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (StringAttributeValue_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = StringAttributeValue_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new StringAttributeValue(addr, StringAttributeValue_Type.this);
  			   StringAttributeValue_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new StringAttributeValue(addr, StringAttributeValue_Type.this);
  	  }
    };
  /** @generated */
  public final static int typeIndexID = StringAttributeValue.typeIndexID;
  /** @generated 
     @modifiable */
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("org.barcelonamedia.uima.types.StringAttributeValue");



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public StringAttributeValue_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

  }
}



    