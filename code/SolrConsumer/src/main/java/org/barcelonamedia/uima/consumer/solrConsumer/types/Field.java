package org.barcelonamedia.uima.consumer.solrConsumer.types;

/**
 * The Class Field.
 */
public class Field {

    /** The name. */
    private String name;

    /** The value. */
    private String value;
    
    /**
     * Instantiates a new field.
     *
     * @param fieldName the field name
     * @param fieldValue the field value
     */
    public Field(final String fieldName, final String fieldValue) {
        this.name = fieldName;
        this.value = fieldValue;
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
}
