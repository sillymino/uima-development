package org.barcelonamedia.uima.consumer.solrConsumer.mapping;

import org.xml.sax.Attributes;

/**
 * The Class FeatureMapper.
 */
public class FeatureMapper implements ElementMapper<FeatureDescription> {

    /**
     * The constant string representing the name attribute of feature element.
     * Defines the feature name to search for.
     */
    private static final String FEATURE_NAME = "name";

    /**
     * Extract the information from the feature element attributes.
     *
     * @param attributes The element attributes.
     *
     * @return The feature description.
     *
     * @see org.barcelonamedia.uima.consumer.solrConsumer.mapping.ElementMapper#mapElement(org.xml.sax.Attributes)
     */
    public final FeatureDescription mapElement(final Attributes attributes) {
        FeatureDescription featureDescription = new FeatureDescription();
        for (int i = 0; i < attributes.getLength(); i++) {
            String name = attributes.getQName(i);
            String value = attributes.getValue(i);

            if (name.equals(FEATURE_NAME)) {
                featureDescription.setFeatureName(value);
            }
        }
        return featureDescription;
    }

}
