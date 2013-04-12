package org.barcelonamedia.uima.analysis_engine.configurableAnnotator.types;

/**
 * The Class Field.
 */
public class Field {

    /** The name. */
    protected String name;

    /** The value. */
    protected String value;
    
    protected Integer begin;
    protected Integer end;
    
    /**
     * Instantiates a new field.
     *
     * @param fieldName the field name
     * @param fieldValue the field value
     */
    public Field(final String fieldName, final String fieldValue, int begin, int end) {
        this.name = fieldName;
        this.value = fieldValue;
        this.begin = begin;
        this.end = end;
    }

    /**
     * Instantiates a new field.
     *
     * @param fieldName the field name
     * @param fieldValue the field value
     */
    public Field(final String fieldName, final String fieldValue) {
        this.name = fieldName;
        this.value = fieldValue;
        this.begin = null;
        this.end = null;
    }

    /**
     * Name.
     *
     * @return the string
     */
    public final String name() {
        return this.name.intern();
    }
    
    /**
     * String value.
     *
     * @return the string
     */
    public final String stringValue() {
        return this.value;
    }

		public Integer getBegin() {
			return begin;
		}

		public Integer getEnd() {
			return end;
		}
}
