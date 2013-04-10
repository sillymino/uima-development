package org.barcelonamedia.uima.consumer.solrConsumer.mapping;

/**
 * Instances of this class represent feature descriptions in the mapping file.
 */
public class FeatureDescription implements Locateable {

    /** The feature name. */
    private String featureName;

    /** The annotation description. */
    private AnnotationDescription annotationDescription;

    /** The line. */
    private int line;

    /** The column. */
    private int column;

    /**
     * Instantiates a new feature description.
     */
    public FeatureDescription() {
    }

    /**
     * Instantiates a new feature description.
     *
     * @param featureNameParam
     *            the feature name
     */
    public FeatureDescription(final String featureNameParam) {
        super();
        this.featureName = featureNameParam;
    }

    /**
     * Sets the annotation description.
     *
     * @param annotationDescriptionParam
     *            the new annotation description
     */
    public final void setAnnotationDescription(
            final AnnotationDescription annotationDescriptionParam) {
        this.annotationDescription = annotationDescriptionParam;
    }

    /**
     * Gets the annotation description.
     *
     * @return the annotation description
     */
    public final AnnotationDescription getAnnotationDescription() {
        return annotationDescription;
    }

    /**
     * Gets the feature name.
     *
     * @return the feature name
     */
    public final String getFeatureName() {
        return featureName;
    }

    /**
     * Sets the feature name.
     *
     * @param featureNameParam
     *            the new feature name
     */
    public final void setFeatureName(final String featureNameParam) {
        this.featureName = featureNameParam;
    }

    /**
     * To string.
     *
     * @return The string representing the contents.
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public final String toString() {
        String string = "{";
        string += "name: " + featureName + "; ";
        string += "annotationDescription" + annotationDescription + "; ";
        string += "}";
        return string;
    }

    /**
     * Gets the column number.
     *
     * @return the column number
     *
     * @see org.barcelonamedia.uima.consumer.solrConsumer.mapping.Locateable#getColumnNumber()
     */
    public final int getColumnNumber() {
        return column;
    }

    /**
     * Gets the line number.
     *
     * @return the line number
     *
     * @see org.barcelonamedia.uima.consumer.solrConsumer.mapping.Locateable#getLineNumber()
     */
    public final int getLineNumber() {
        return line;
    }

    /**
     * Sets the column number.
     *
     * @param columnNumber the column number
     *
     * @see org.barcelonamedia.uima.consumer.solrConsumer.mapping.Locateable#setColumnNumber(int)
     */
    public final void setColumnNumber(final int columnNumber) {
        column = columnNumber;
    }

    /**
     * Sets the line number.
     *
     * @param lineNumber the line number
     *
     * @see org.barcelonamedia.uima.consumer.solrConsumer.mapping.Locateable#setLineNumber(int)
     */
    public final void setLineNumber(final int lineNumber) {
        line = lineNumber;
    }
}
