package org.barcelonamedia.uima.consumer.solrConsumer.mapping;

import org.xml.sax.Attributes;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * The Class FeaturesMapper.
 */
public class FeaturesMapper implements ElementMapper<FeaturesDescription> {

    /** The constant string representing "yes". */
    private static final String YES = "yes";

    /** The constant string representing "true". */
    private static final String TRUE = "true";

    /**
     * The constant representing the separator attribute of field element.
     * Defines the separator to be used to merge the various annotation values.
     */
    private static final String FEATURE_SEPARATOR = "separator";

    /**
     * The constant representing the covered text attribute of field element.
     * Defines if the covered text must be added or not.
     */
    private static final String FEATURE_COVERED_TEXT = "coveredText";

    /**
     * Extract the information from the features element attributes.
     *
     * @param attributes The element attributes.
     *
     * @return featuresDescription The features description.
     *
     * @see org.barcelonamedia.uima.consumer.solrConsumer.mapping.ElementMapper#mapElement(org.xml.sax.Attributes)
     */
    public final FeaturesDescription mapElement(final Attributes attributes) {
        FeaturesDescription featuresDescription = new FeaturesDescription();
        for (int i = 0; i < attributes.getLength(); i++) {
            String name = attributes.getQName(i);
            String value = StringEscapeUtils.unescapeJava(attributes
                    .getValue(i));

            if (name.equals(FEATURE_SEPARATOR)) {
                featuresDescription.setSeparator(value);

            } else if (name.equals(FEATURE_COVERED_TEXT)) {
                if (value.equals(TRUE) || value.equals(YES)) {
                    featuresDescription.setExtractCoveredText(true);
                }
            }
        }

        return featuresDescription;
    }

}
