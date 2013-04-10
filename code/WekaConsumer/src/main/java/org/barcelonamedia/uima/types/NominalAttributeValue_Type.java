
/* First created by JCasGen Mon Mar 29 10:16:29 CEST 2010 */
package org.barcelonamedia.uima.types;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;

/** 
 * Updated by JCasGen Mon Mar 29 10:16:31 CEST 2010
 * @generated */
public class NominalAttributeValue_Type extends AttributeValue_Type {
  /** @generated */
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (NominalAttributeValue_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = NominalAttributeValue_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new NominalAttributeValue(addr, NominalAttributeValue_Type.this);
  			   NominalAttributeValue_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new NominalAttributeValue(addr, NominalAttributeValue_Type.this);
  	  }
    };
  /** @generated */
  public final static int typeIndexID = NominalAttributeValue.typeIndexID;
  /** @generated 
     @modifiable */
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("org.barcelonamedia.uima.types.NominalAttributeValue");



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public NominalAttributeValue_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

  }
}



    