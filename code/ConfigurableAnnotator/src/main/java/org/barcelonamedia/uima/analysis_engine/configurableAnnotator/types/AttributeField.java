package org.barcelonamedia.uima.analysis_engine.configurableAnnotator.types;

import org.apache.uima.cas.Type;

public class AttributeField extends Field {

    private Type type;

    public AttributeField(String fieldName, String fieldValue, Type annotationType) {
      super(fieldName, fieldValue);
      this.type = annotationType;
  }

    public AttributeField(String fieldName, String fieldValue, Type annotationType, int begin, int end) {
      super(fieldName, fieldValue, begin, end);
      this.type = annotationType;
  }

    /**
     * Type.
     *
     * @return the type
     */
    public final Type type() {
        return this.type;
    }
    
}
