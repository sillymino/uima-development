package org.barcelonamedia.uima.analysis_engine.configurableAnnotator.mapping;

import org.xml.sax.Attributes;

/**
 * The Class FieldMapper.
 */
public class FieldMapper implements ElementMapper<Locateable> {

    /**
     * The constant representing the name attribute of field element. Defines
     * the field name.
     */
    private static final String FIELD_NAME = "name";

    /**
     * The constant representing the type attribute of field element. Defines
     * the field annotation type.
     */
    private static final String FIELD_TYPE = "type";
    
    /**
     * Extracts the information from the field element attributes.
     *
     * @param attributes The element attributes.
     *
     * @return The field description.
     *
     * @see org.barcelonamedia.uima.analysis_engine.configurableAnnotator.mapping.ElementMapper#mapElement(org.xml.sax.Attributes)
     */
    public final FieldDescription mapElement(final Attributes attributes) {

        FieldDescription fieldDescription = new FieldDescription();
        for (int i = 0; i < attributes.getLength(); i++) {
            String name = attributes.getQName(i);
            String value = attributes.getValue(i);

            if (name.equals(FIELD_NAME)) {
                fieldDescription.setName(value);
            }
            if (name.equals(FIELD_TYPE)) {
                fieldDescription.setType(value);
            }
        }
        return fieldDescription;
    }
}
