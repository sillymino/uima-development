package org.barcelonamedia.uima.consumer.solrConsumer.mapping;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Instances of this class represent feature descriptions in the mapping file.
 */
public class FeaturesDescription implements Locateable {

    /** The multiword separator constant. */
    public static final String MULTI_WORD_SEPARATOR = "_";

    /** The separator string. */
    private String separator;

    /** The extract covered text option. */
    private boolean extractCoveredText;

    /** The feature descriptions. */
    private Collection<FeatureDescription> featureDescriptions;

    /** The line. */
    private int line;

    /** The column. */
    private int column;

    /**
     * Instantiates a new features description.
     */
    public FeaturesDescription() {
        featureDescriptions = new ArrayList<FeatureDescription>();
    }

    /**
     * Sets the feature descriptions.
     *
     * @param featureDescriptionsParam
     *            the new feature descriptions
     */
    public final void setFeatureDescriptions(
            final Collection<FeatureDescription> featureDescriptionsParam) {
        this.featureDescriptions = featureDescriptionsParam;
    }

    /**
     * Gets the feature descriptions.
     *
     * @return the feature descriptions
     */
    public final Collection<FeatureDescription> getFeatureDescriptions() {
        return featureDescriptions;
    }

    /**
     * Sets the separator.
     *
     * @param separatorParam
     *            the new separator
     */
    public final void setSeparator(final String separatorParam) {
        this.separator = separatorParam;
    }

    /**
     * Gets the separator.
     *
     * @return the separator
     */
    public final String getSeparator() {
        return separator;
    }

    /**
     * Sets the extract covered text option.
     *
     * @param extractCoveredTextParam
     *            the new extract covered text
     */
    public final void setExtractCoveredText(final boolean extractCoveredTextParam) {
        this.extractCoveredText = extractCoveredTextParam;
    }

    /**
     * Get the extract covered text option.
     *
     * @return The extract covered text option.
     */
    public final boolean extractCoveredText() {
        return extractCoveredText;
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
        string += "separator: " + separator + "; ";
        string += "extractCoveredText" + extractCoveredText + "; ";
        string += "featureDescriptions" + featureDescriptions + "; ";
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
