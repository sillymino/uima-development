package org.barcelonamedia.uima.consumer.solrConsumer.mapping;

import org.xml.sax.Attributes;

/**
 * The Class FieldMapper.
 */
public class FieldMapper implements ElementMapper<FieldDescription> {

    /**
     * The constant representing the name attribute of field element. Defines
     * the field name.
     */
    private static final String FIELD_NAME = "name";

    /**
     * Extracts the information from the field element attributes.
     *
     * @param attributes The element attributes.
     *
     * @return The field description.
     *
     * @see org.barcelonamedia.uima.consumer.solrConsumer.mapping.ElementMapper#mapElement(org.xml.sax.Attributes)
     */
    public final FieldDescription mapElement(final Attributes attributes) {

        FieldDescription fieldDescription = new FieldDescription();
        for (int i = 0; i < attributes.getLength(); i++) {
            String name = attributes.getQName(i);
            String value = attributes.getValue(i);

            if (name.equals(FIELD_NAME)) {
                fieldDescription.setName(value);
            }
        }
        return fieldDescription;
    }
}
