package org.barcelonamedia.uima.consumer.solrConsumer.mapping;

import org.apache.commons.lang.StringEscapeUtils;
import org.xml.sax.Attributes;

/**
 * The Class AnnotationsMapper.
 */
public class AnnotationsMapper implements ElementMapper<AnnotationsDescription> {

    /**
     * The constant string representing the separator attribute of annotations
     * element. Defines the separator string to be used to merge the various
     * features values extracted.
     */
    private static final String ANNOTATION_VALUE_SEPARATOR = "separator";

    /**
     * Extract the information from annotations element attributes.
     *
     * @param attributes The element attributes.
     *
     * @return The annotations description.
     *
     * @see org.barcelonamedia.uima.consumer.solrConsumer.mapping.ElementMapper#mapElement(org.xml.sax.Attributes)
     */
    public final AnnotationsDescription mapElement(final Attributes attributes) {

        AnnotationsDescription annotationsDescription = new AnnotationsDescription();
        for (int i = 0; i < attributes.getLength(); i++) {
            String name = attributes.getQName(i);
            String value = StringEscapeUtils.unescapeJava(attributes
                    .getValue(i));

            if (name.equals(ANNOTATION_VALUE_SEPARATOR)) {
                annotationsDescription.setSeparator(value);
            }
        }
        return annotationsDescription;
    }

}
