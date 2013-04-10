package org.barcelonamedia.uima.consumer.solrConsumer.mapping;

/**
 * Instances of this class represent field descriptions in the mapping file.
 */
public class FieldDescription implements Locateable {

    /** The default field delimiter constant. */
    public static final String DEFAULT_DELIMITER = "";

    /** The field name. */
    private String name;

    /** The annotations descriptions. */
    private AnnotationsDescription annotationsDescriptions;

    /** The line. */
    private int line;

    /** The column. */
    private int column;

    /**
     * Instantiates a new field description.
     *
     * @param nameParam
     *            The field name.
     */
    public FieldDescription(final String nameParam) {
        this();
        this.name = nameParam;
    }

    /**
     * Instantiates a new field description.
     */
    public FieldDescription() {
    }

    /**
     * Gets the name.
     *
     * @return The field name.
     */
    public final String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param nameParam
     *            The new field name.
     */
    public final void setName(final String nameParam) {
        this.name = nameParam;
    }

    /**
     * Gets the annotations descriptions.
     *
     * @return the annotations descriptions
     */
    public final AnnotationsDescription getAnnotationsDescriptions() {
        return annotationsDescriptions;
    }

    /**
     * Sets the annotations descriptions.
     *
     * @param annotationsDefinitionAL
     *            the new annotations descriptions
     */
    public final void setAnnotationsDescriptions(
            final AnnotationsDescription annotationsDefinitionAL) {
        this.annotationsDescriptions = annotationsDefinitionAL;
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
        string += "name: " + name + "; ";
        string += "annotationsDescriptions" + annotationsDescriptions + "; ";
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
