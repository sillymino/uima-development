package org.barcelonamedia.uima.consumer.solrConsumer.analysis;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

import org.barcelonamedia.uima.consumer.solrConsumer.mapping.AnnotationsDescription;
import org.barcelonamedia.uima.consumer.solrConsumer.mapping.FieldDescription;
import org.barcelonamedia.uima.consumer.solrConsumer.types.Field;

/**
 * The Class FieldBuilder.
 */
public class FieldBuilder {

    /** The logger. */
    private Logger logger = null;

    /**
     * Instantiates a new field builder.
     */
    public FieldBuilder() {
    }

    /**
     * Instantiates a new field builder.
     *
     * @param l
     *            The logger.
     */
    public FieldBuilder(final Logger l) {
        this.logger = l;
    }

    /**
     * Creates the fields and return them as a collection.
     *
     * @param annotationsValueIterator
     *            The annotations value iterator.
     * @param annotations
     *            The annotations description.
     * @param fieldDescription
     *            The field description.
     *
     * @return The Field collection.
     *
     * @throws InvalidFeatureException
     *             If an annotation type doesn't have a defined feature.
     */
    public final Collection<Field> createFields(
            final AnnotationsIterator annotationsValueIterator,
            final AnnotationsDescription annotations,
            final FieldDescription fieldDescription)
            throws InvalidFeatureException {

        String annotationDelimiter = annotations.getSeparator();

        String fieldName = fieldDescription.getName();

        if (annotationDelimiter == null) {
            return getFieldValues(fieldName, annotationsValueIterator);

        } else {
            return getFieldValues(fieldName, annotationsValueIterator,
                    annotationDelimiter);
        }
    }

    /**
     * Retrieves the annotation values and creates a field object for every
     * value. If any value is null, will be ignored!
     *
     * @param fieldName
     *            The field name.
     * @param annotationsValueIterator
     *            The annotations value iterator.
     *
     * @return The Field collection.
     *
     * @throws InvalidFeatureException
     *             If an annotation type doesn't have a defined feature.
     */
    private Collection<Field> getFieldValues(final String fieldName,
            final AnnotationsIterator annotationsValueIterator)
            throws InvalidFeatureException {

        Collection<Field> fields = new ArrayList<Field>();

        String value;
        while (annotationsValueIterator.hasNext()) {
            try {
                value = annotationsValueIterator.next();
                if (!(value == null)) {
                    fields.add(new Field(fieldName, value));
                }
            } catch (InexistentAnnotationException e) {
                // Ignore the inexistent annotations
                if (!(logger == null)) {
                    logger.info(e);
                }
            }

        }

        return fields;
    }

    /**
     * Retrieves the annotation values, concatenates them and create one single
     * field with the concatenation.
     *
     * @param fieldName
     *            The field name.
     * @param annotationsValueIterator
     *            The annotations value iterator.
     * @param annotationDelimiter
     *            The annotation value delimiter.
     *
     * @return The Field collection.
     *
     * @throws InvalidFeatureException
     *             If an annotation type doesn't have a defined feature.
     */
    private Collection<Field> getFieldValues(final String fieldName,
            final AnnotationsIterator annotationsValueIterator,
            final String annotationDelimiter) throws InvalidFeatureException {

        Collection<Field> fieldValues = new ArrayList<Field>();

        String value = concatenate(annotationsValueIterator,
                annotationDelimiter);

        fieldValues.add(new Field(fieldName, value));

        return fieldValues;
    }

    /**
     * Concatenate the annotation values.
     *
     * @param iterator
     *            The annotations value iterator.
     * @param delimiter
     *            The annotation value delimiter.
     *
     * @return The values concatenation string.
     *
     * @throws InvalidFeatureException
     *             If an annotation type doesn't have a defined feature.
     */
    private String concatenate(final AnnotationsIterator iterator,
            final String delimiter) throws InvalidFeatureException {

        StringBuilder concatenation = new StringBuilder();
        String value;
        while (iterator.hasNext()) {
            try {
                value = iterator.next();
                concatenation.append(value);
                if (iterator.hasNext()) {
                    concatenation.append(delimiter);
                }
            } catch (InexistentAnnotationException e) {
                // Ignore the inexistent annotations
                if (!(logger == null)) {
                    logger.info(e);
                }
            }
        }
        return concatenation.toString();

    }
}
