package org.barcelonamedia.uima.analysis_engine.configurableAnnotator.mapping;

/**
 * Instances of this class represent annotation descriptions in the mapping
 * file.
 */
public class AnnotationDescription implements Locateable {

    /** The annotation type. */
    private String type;

    /** The position. */
    private String position;

    /** The features descriptions. */
    private FeaturesDescription featuresDescriptions;

    /** The sofa. */
    private String sofa;

    /** The line. */
    private int line;

    /** The column. */
    private int column;

    /**
     * Instantiates a new annotation description.
     */
    public AnnotationDescription() {
    }

    /**
     * Instantiates a new annotation description.
     *
     * @param typeParam The annotation type.
     */
    public AnnotationDescription(final String typeParam) {
        this();
        this.type = typeParam;
    }

    /**
     * Gets the annotation type.
     *
     * @return The annotation type.
     */
    public final String getType() {
        return type;
    }

    /**
     * Sets the annotation type.
     *
     * @param typeParam the new annotation type.
     */
    public final void setType(final String typeParam) {
        this.type = typeParam;
    }

    /**
     * Gets the features descriptions.
     *
     * @return The features descriptions.
     */
    public final FeaturesDescription getFeaturesDescriptions() {
        return featuresDescriptions;
    }

    /**
     * Sets the features descriptions.
     *
     * @param featuresDefinitionAL The new features descriptions.
     */
    public void setFeaturesDescriptions(final FeaturesDescription featuresDefinitionAL) {
        this.featuresDescriptions = featuresDefinitionAL;
    }

    /**
     * Gets the sofa.
     *
     * @return The sofa.
     */
    public final String getSofa() {
        return sofa;
    }

    /**
     * Sets the sofa.
     *
     * @param sofaParam The new sofa.
     */
    public final void setSofa(final String sofaParam) {
        this.sofa = sofaParam;
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
        string += "type: " + type + "; ";
        string += "featuresDescriptions" + featuresDescriptions + "; ";
        string += "sofa: " + sofa + "; ";
        string += "}";
        return string;
    }

    /**
     * Gets the position.
     *
     * @return the position
     */
    public final String getPosition() {
        return position;
    }

    /**
     * Sets the position.
     *
     * @param positionParam the new position
     */
    public final void setPosition(final String positionParam) {
        this.position = positionParam;
    }

    /**
     * Gets the column number.
     *
     * @return the column number
     *
     * @see org.barcelonamedia.uima.analysis_engine.configurableAnnotator.mapping.Locateable#getColumnNumber()
     */
    public final int getColumnNumber() {
        return column;
    }

    /**
     * Gets the line number.
     *
     * @return the line number
     *
     * @see org.barcelonamedia.uima.analysis_engine.configurableAnnotator.mapping.Locateable#getLineNumber()
     */
    public final int getLineNumber() {
        return line;
    }

    /**
     * Sets the column number.
     *
     * @param columnNumber the column number
     *
     * @see org.barcelonamedia.uima.analysis_engine.configurableAnnotator.mapping.Locateable#setColumnNumber(int)
     */
    public final void setColumnNumber(final int columnNumber) {
        column = columnNumber;
    }

    /**
     * Sets the line number.
     *
     * @param lineNumber the line number
     *
     * @see org.barcelonamedia.uima.analysis_engine.configurableAnnotator.mapping.Locateable#setLineNumber(int)
     */
    public final void setLineNumber(final int lineNumber) {
        line = lineNumber;
    }
}
