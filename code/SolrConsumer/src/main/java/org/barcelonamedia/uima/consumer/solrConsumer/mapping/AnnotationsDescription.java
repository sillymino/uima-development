package org.barcelonamedia.uima.consumer.solrConsumer.mapping;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Instances of this class represent annotation descriptions in the mapping
 * file.
 */
public class AnnotationsDescription implements Locateable {

    /** The default annotation delimiter string. */
    public static final String DEFAULT_DELIMITER = "#";

    /** The separator. */
    private String separator;

    /** The annotation descriptions. */
    private Collection<AnnotationDescription> annotationDescriptions;

    /** The line. */
    private int line;

    /** The column. */
    private int column;

    /**
     * Instantiates a new annotations description.
     */
    public AnnotationsDescription() {
        super();
        setAnnotationDescriptions(new ArrayList<AnnotationDescription>());
    }

    /**
     * Sets the annotation descriptions.
     *
     * @param annotationDescriptionsParam
     *            the new annotation descriptions
     */
    public final void setAnnotationDescriptions(
            final Collection<AnnotationDescription> annotationDescriptionsParam) {
        this.annotationDescriptions = annotationDescriptionsParam;
    }

    /**
     * Gets the annotation descriptions.
     *
     * @return the annotation descriptions
     */
    public final Collection<AnnotationDescription> getAnnotationDescriptions() {
        return annotationDescriptions;
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
     * Sets the separator.
     *
     * @param concatString
     *            the new separator
     */
    public final void setSeparator(final String concatString) {
        this.separator = concatString;
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
        string += "concatString: " + separator + "; ";
        string += "annotationDescriptions" + annotationDescriptions + "; ";
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
