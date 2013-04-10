package org.barcelonamedia.uima.analysis_engine.configurableAnnotator.mapping;

/**
 * The Interface Locateable.
 */
public interface Locateable {

    /**
     * Sets the line number.
     *
     * @param lineNumber
     *            the new line number
     */
    void setLineNumber(int lineNumber);

    /**
     * Gets the line number.
     *
     * @return the line number
     */
    int getLineNumber();

    /**
     * Sets the column number.
     *
     * @param columnNumber
     *            the new column number
     */
    void setColumnNumber(int columnNumber);

    /**
     * Gets the column number.
     *
     * @return the column number
     */
    int getColumnNumber();
}
