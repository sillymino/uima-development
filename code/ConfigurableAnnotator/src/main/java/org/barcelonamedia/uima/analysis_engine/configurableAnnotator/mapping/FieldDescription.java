package org.barcelonamedia.uima.analysis_engine.configurableAnnotator.mapping;

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

    private String type;

    /**
     * Instantiates a new field description.
     *
     * @param nameParam
     *            The field name.
     */
    public FieldDescription(final String nameParam, final String type) {
        this();
        this.name = nameParam;
        this.type = type;
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
        return this.name;
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
        return this.annotationsDescriptions;
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
        string += "name: " + this.name + "; ";
        string += "annotationsDescriptions" + this.annotationsDescriptions + "; ";
        string += "}";
        return string;
    }

    /**
     * Gets the column number.
     *
     * @return the column number
     *
     * @see org.barcelonamedia.uima.analysis_engine.configurableAnnotator.mapping.Locateable#getColumnNumber()
     */
    public final int getColumnNumber() {
        return this.column;
    }

    /**
     * Gets the line number.
     *
     * @return the line number
     *
     * @see org.barcelonamedia.uima.analysis_engine.configurableAnnotator.mapping.Locateable#getLineNumber()
     */
    public final int getLineNumber() {
        return this.line;
    }

    /**
     * Sets the column number.
     *
     * @param columnNumber the column number
     *
     * @see org.barcelonamedia.uima.analysis_engine.configurableAnnotator.mapping.Locateable#setColumnNumber(int)
     */
    public final void setColumnNumber(final int columnNumber) {
        this.column = columnNumber;
    }

    /**
     * Sets the line number.
     *
     * @param lineNumber the line number
     *
     * @see org.barcelonamedia.uima.analysis_engine.configurableAnnotator.mapping.Locateable#setLineNumber(int)
     */
    public final void setLineNumber(final int lineNumber) {
        this.line = lineNumber;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
