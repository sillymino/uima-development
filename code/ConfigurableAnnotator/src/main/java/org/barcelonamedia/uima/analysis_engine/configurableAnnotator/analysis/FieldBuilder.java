package org.barcelonamedia.uima.analysis_engine.configurableAnnotator.analysis;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.jcas.tcas.Annotation;
import org.barcelonamedia.uima.analysis_engine.configurableAnnotator.mapping.AnnotationsDescription;
import org.barcelonamedia.uima.analysis_engine.configurableAnnotator.mapping.FieldDescription;
import org.barcelonamedia.uima.analysis_engine.configurableAnnotator.types.AttributeField;
import org.barcelonamedia.uima.analysis_engine.configurableAnnotator.types.Field;

/**
 * The Class FieldBuilder.
 */
public class FieldBuilder {

    private static final String ATTR_VAL_TYPE = "org.barcelonamedia.uima.types.AttributeValue";
    
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
     * @throws Exception 
     */
    public final Collection<Field> createFields(final TypeSystem typeSystem,
            final AnnotationsIterator annotationsValueIterator,
            final AnnotationsDescription annotations,
            final FieldDescription fieldDescription)
            throws Exception {

        String annotationDelimiter = annotations.getSeparator();

        String fieldName = fieldDescription.getName();
        String type = fieldDescription.getType();
        
        Type annotationType = typeSystem.getType(type);
        Type AttributeValueType = typeSystem.getType(ATTR_VAL_TYPE);
        if (annotationType == null) {
            throw new Exception("Annotation '" + type + "' specified, but does not exist!");
        } else if ( !typeSystem.subsumes(AttributeValueType, annotationType)) {
            throw new Exception("Annotation '" + type
                                + "' specified, but does not inherit from '"
                                + ATTR_VAL_TYPE + "'!");
        }
        
        if (annotationDelimiter == null) {
            return getFieldValues(fieldName, annotationsValueIterator, annotationType);

        } else {
            return getFieldValues(fieldName, annotationsValueIterator, annotationType,
                    annotationDelimiter);
        }
    }

    /**
     * Retrieves the annotation values and creates a field object for every
     * value.
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
            final AnnotationsIterator annotationsValueIterator,
            final Type annotationType)
            throws InvalidFeatureException {
        Collection<Field> fields = new ArrayList<Field>();
        String value;
        while (annotationsValueIterator.hasNext()) {
            try {
                value = annotationsValueIterator.next();
                Annotation ann = annotationsValueIterator.getCurrentAnnotation();
                fields.add(new AttributeField(fieldName, value, annotationType, ann.getBegin(), ann.getEnd()));
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
     * @param annotationDelimiter2 
     *
     * @return The Field collection.
     *
     * @throws InvalidFeatureException
     *             If an annotation type doesn't have a defined feature.
     */
    private Collection<Field> getFieldValues(final String fieldName,
            final AnnotationsIterator annotationsValueIterator,
            final Type annotationType,            
            final String annotationDelimiter) throws InvalidFeatureException {

        Collection<Field> fieldValues = new ArrayList<Field>();

        String value = concatenate(annotationsValueIterator,
                annotationDelimiter);

        fieldValues.add(new AttributeField(fieldName, value, annotationType));

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
