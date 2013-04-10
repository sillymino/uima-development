package org.barcelonamedia.uima.consumer.solrConsumer.analysis;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.FloatArrayFS;
import org.apache.uima.cas.IntArrayFS;
import org.apache.uima.cas.StringArrayFS;
import org.apache.uima.cas.Type;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;

import org.barcelonamedia.uima.consumer.solrConsumer.mapping.AnnotationDescription;
import org.barcelonamedia.uima.consumer.solrConsumer.mapping.AnnotationsDescription;
import org.barcelonamedia.uima.consumer.solrConsumer.mapping.FeatureDescription;
import org.barcelonamedia.uima.consumer.solrConsumer.mapping.FeaturesDescription;

/**
 * The Class AnnotationsIterator.
 */
public class AnnotationsIterator {

    /** The JCas object. */
    private JCas jCas;

    /** The annotation descriptions. */
    private Collection<AnnotationDescription> annotationDescriptions;

    /** The annotation description iterator. */
    private Iterator<AnnotationDescription> annotDescIterator;

    /** The current annotation description. */
    private AnnotationDescription currentAnnotDesc;

    /** The annotation iterator. */
    private Iterator<Annotation> annotationIterator;

    /** The current annotation. */
    private Annotation currentAnnotation;

    /** The feature value list of the current annotation. */
    private List<String> featureValueList;

    /** The feature values iterator. */
    private Iterator<String> featureValuesIterator;

    /** The string type. */
    private Type stringType;

    /**
     * The Class NotNullPredicate.
     */
    private class NotNullPredicate<T> implements Predicate<T> {

        /**
         * @see com.google.common.base.Predicate#apply(java.lang.Object)
         */
        public boolean apply(T object) {
            return object != null;
        }
    }

    /**
     * Instantiates a new annotations iterator.
     *
     * @param cas
     *            The cas from where the annotation are extracted.
     * @param annotationsDesc
     *            The annotations description of the annotations to be
     *            extracted.
     */
    public AnnotationsIterator(final JCas cas,
            final AnnotationsDescription annotationsDesc) {

        this.jCas = cas;
        this.annotationDescriptions = annotationsDesc
                .getAnnotationDescriptions();

        this.annotDescIterator = annotationDescriptions.iterator();
        this.annotationIterator = null;
        this.featureValuesIterator = null;
    }

    /**
     * Checks for next information in the iterator.
     *
     * @return true, if successful.
     */
    public final boolean hasNext() {
        if (featureValuesIterator == null || !featureValuesIterator.hasNext()) {
            if (annotationIterator == null || !annotationIterator.hasNext()) {
                if (!annotDescIterator.hasNext()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Next feature value.
     *
     * @return The string value of the feature
     *
     * @throws InvalidFeatureException
     *             If the feature described is invalid (the annotation doesn't
     *             have this feature).
     * @throws InexistentAnnotationException
     *             If the document being processed has no annotation of a type
     *             defined.
     */
    public final String next() throws InvalidFeatureException,
            InexistentAnnotationException {
        while (featureValuesIterator == null || !featureValuesIterator.hasNext()) {
            while (annotationIterator == null || !annotationIterator.hasNext()) {
                if (!annotDescIterator.hasNext()) {
                    return null;
                }
                currentAnnotDesc = annotDescIterator.next();
                setAnnotationIterator();
            }
            currentAnnotation = annotationIterator.next();
            featureValueList = getFeatureValuesList(currentAnnotation,
                    currentAnnotDesc);
            featureValuesIterator = featureValueList.iterator();
        }
        return featureValuesIterator.next();
    }

    /**
     * Sets the annotation iterator.
     *
     * @throws InexistentAnnotationException
     *             If the document being processed has no annotation of a type
     *             defined.
     */
    private void setAnnotationIterator() throws InexistentAnnotationException {
        Type annotationType = jCas.getTypeSystem().getType(
                currentAnnotDesc.getType());
        FSIterator<Annotation> it = jCas.getAnnotationIndex(annotationType).iterator();

        annotationIterator = Iterators.filter(it,
                new NotNullPredicate<Annotation>());
        if (!annotationIterator.hasNext()) {
            throw new InexistentAnnotationException("The annotation type '"
                    + currentAnnotDesc.getType()
                    + "' does not exists in this document.");
        }
    }

    /**
     * Gets the feature values list.
     *
     * @param annotation
     *            The annotation.
     * @param annotationDesc
     *            The annotation description.
     *
     * @return The feature values list of the features defined in the
     *         description.
     *
     * @throws InvalidFeatureException
     *             If the feature described is invalid (the annotation doesn't
     *             have this feature).
     */
    private List<String> getFeatureValuesList(final Annotation annotation,
            final AnnotationDescription annotationDesc)
            throws InvalidFeatureException {

        FeaturesDescription featsDesc = annotationDesc
                .getFeaturesDescriptions();
        return composeList(annotation, featsDesc);
    }

    /**
     * Compose features values list.
     *
     * @param annotation
     *            The annotation.
     * @param featsDesc
     *            The features description.
     *
     * @return The list of the values of the features.
     *
     * @throws InvalidFeatureException
     *             If the feature described is invalid (the annotation doesn't
     *             have this feature).
     */
    private List<String> composeList(final Annotation annotation,
            final FeaturesDescription featsDesc) throws InvalidFeatureException {

        List<String> values = new LinkedList<String>();

        if (featsDesc == null) {
            String value = annotation.getCoveredText();
            values.add(value.replaceAll("\\s",
                    FeaturesDescription.MULTI_WORD_SEPARATOR));

        } else {

            Collection<FeatureDescription> featureDescriptions;
            featureDescriptions = featsDesc.getFeatureDescriptions();

            if (featureDescriptions == null || featureDescriptions.size() < 0) {
                String value = annotation.getCoveredText();
                values.add(value.replaceAll("\\s",
                        FeaturesDescription.MULTI_WORD_SEPARATOR));

            } else {
                if (featsDesc.extractCoveredText()) {
                    String value = annotation.getCoveredText();
                    values.add(value.replaceAll("\\s",
                            FeaturesDescription.MULTI_WORD_SEPARATOR));
                }

                extractValues(annotation, featureDescriptions, values);

                String separator = featsDesc.getSeparator();
                if (separator != null) {
                    StringBuilder concatenation = new StringBuilder();
                    Iterator<String> it = values.iterator();
                    String value;
                    while (it.hasNext()) {
                        value = it.next();
                        concatenation.append(value);
                        if (it.hasNext()) {
                            concatenation.append(separator);
                        }
                    }
                    values.clear();
                    values.add(concatenation.toString());
                }

            }
        }

        return values;
    }

    /**
     * Extract feature annotation values.
     *
     * @param annotation
     *            The annotation.
     * @param featureDescriptions
     *            The feature descriptions.
     * @param values
     *            The list of the values of the features.
     *
     * @throws InvalidFeatureException
     *             If the feature described is invalid (the annotation doesn't
     *             have this feature).
     */
    private void extractValues(final Annotation annotation,
            final Collection<FeatureDescription> featureDescriptions,
            final List<String> values) throws InvalidFeatureException {

        Type annotationType = annotation.getType();
        validate(featureDescriptions, annotationType);

        stringType = jCas.getTypeSystem().getType(CAS.TYPE_NAME_STRING);

        for (FeatureDescription featDesc : featureDescriptions) {
            extractFeatureValues(annotation, featDesc, values);
        }
    }

    /**
     * Validate that the features described in featureDescriptions belongs to
     * that annotation type.
     *
     * @param featureDescriptions
     *            The feature descriptions.
     * @param annotationType
     *            The annotation type.
     *
     * @throws InvalidFeatureException
     *             If the feature described is invalid (the annotation doesn't
     *             have this feature).
     */
    final void validate(
            final Collection<FeatureDescription> featureDescriptions,
            final Type annotationType) throws InvalidFeatureException {

        for (FeatureDescription featureDesc : featureDescriptions) {
            Feature feature = annotationType.getFeatureByBaseName(featureDesc
                    .getFeatureName());
            if (feature == null) {
                throw new InvalidFeatureException("Type "
                        + annotationType.getName() + " has no feature "
                        + featureDesc.getFeatureName() + ".");
            }
        }
    }

    /**
     * Extract feature values.
     *
     * @param annotation
     *            The annotation.
     * @param featDesc
     *            The feature descriptions.
     * @param values
     *            The list of the values of the features.
     *
     * @throws InvalidFeatureException
     *             If the feature described is invalid (the annotation doesn't
     *             have this feature).
     */
    private void extractFeatureValues(final Annotation annotation,
            final FeatureDescription featDesc, final List<String> values)
            throws InvalidFeatureException {

        // logger.info("Extracting Features from " + annotation.getType());

        Type annotationType = annotation.getType();
        Feature feat = annotationType.getFeatureByBaseName(featDesc
                .getFeatureName());
        String rangeTypeName = feat.getRange().getName();

        if (jCas.getTypeSystem().subsumes(stringType, feat.getRange())) {
            String str = annotation.getStringValue(feat);
            if (str == null) {
                // TODO: If string is null...?
            } else {
                values.add(str);
            }

        } else if (CAS.TYPE_NAME_INTEGER.equals(rangeTypeName)) {
            values.add(String.valueOf(annotation.getIntValue(feat)));

        } else if (CAS.TYPE_NAME_FLOAT.equals(rangeTypeName)) {
            values.add(String.valueOf(annotation.getFloatValue(feat)));

        } else if (CAS.TYPE_NAME_STRING_ARRAY.equals(rangeTypeName)) {
            StringArrayFS arrayFS = (StringArrayFS) annotation
                    .getFeatureValue(feat);
            if (arrayFS == null) {
                // TODO: If FSArray is null...?
            } else {
                String[] vals = arrayFS.toArray();
                for (String str : vals) {
                    values.add(str);
                }
            }

        } else if (CAS.TYPE_NAME_INTEGER_ARRAY.equals(rangeTypeName)) {
            IntArrayFS arrayFS = (IntArrayFS) annotation.getFeatureValue(feat);
            if (arrayFS == null) {
                // TODO: If FSArray is null...?
            } else {
                int[] vals = arrayFS.toArray();
                for (int i : vals) {
                    values.add(String.valueOf(i));
                }
            }

        } else if (CAS.TYPE_NAME_FLOAT_ARRAY.equals(rangeTypeName)) {
            FloatArrayFS arrayFS = (FloatArrayFS) annotation
                    .getFeatureValue(feat);
            if (arrayFS == null) {
                // TODO: If FSArray is null...?
            } else {
                float[] vals = arrayFS.toArray();
                for (float f : vals) {
                    values.add(String.valueOf(f));
                }
            }

        } else if (CAS.TYPE_NAME_BOOLEAN.equals(rangeTypeName)) {
            values.add(String.valueOf(annotation.getBooleanValue(feat)));

        } else { // non-primitive type
            FeatureStructure nonPrimitive = annotation.getFeatureValue(feat);
            processNonPrimitive(nonPrimitive, featDesc
                    .getAnnotationDescription(), values);
        }
    }

    /**
     * Process a non primitive feature structure.
     *
     * @param nonPrimitive
     *            The non_primitive feature structure.
     * @param annotationDescription
     *            The annotation description.
     * @param values
     *            The list of the values of the features.
     *
     * @throws InvalidFeatureException
     *             If the feature structure is unknown and can't be processed.
     */
    private void processNonPrimitive(final FeatureStructure nonPrimitive,
            final AnnotationDescription annotationDescription,
            final List<String> values) throws InvalidFeatureException {

        if (nonPrimitive == null) {
            // TODO: If non-primitive type is null...?

        } else if (nonPrimitive instanceof Annotation) {
            // logger.info("Non Primitive -> Annotation: " +
            // non_primitive.getType());

            Annotation a = (Annotation) nonPrimitive;
            values.addAll(getFeatureValuesList(a, annotationDescription));

        } else if (nonPrimitive instanceof FSArray) {
            FeatureStructure[] fsArray = ((FSArray) nonPrimitive).toArray();
            for (FeatureStructure fs : fsArray) {
                processNonPrimitive(fs, annotationDescription, values);
            }
        } else {
            throw new InvalidFeatureException(
                    "Unknown FeatureStructure, type  " + nonPrimitive.getType()
                            + ".");
        }
    }

    /**
     * Unsupported.
     */
    public final void remove() {
        throw new UnsupportedOperationException();
    }

}
