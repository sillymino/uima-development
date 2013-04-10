package org.barcelonamedia.uima.consumer.solrConsumer.mapping;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Stack;

import javax.xml.parsers.SAXParser;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Build Object out of the Index Mapping File (XML).
 */
public class MappingFileReader extends DefaultHandler {

    /** The tag name for field definitions. */
    public static final String FIELD = "field";

    /** The tag name for annotations definitions. */
    public static final String ANNOTATIONS = "annotations";

    /** The tag name for annotation definitions. */
    public static final String ANNOTATION = "annotation";

    /** The tag name for features definitions. */
    public static final String FEATURES = "features";

    /** The tag name for feature definitions. */
    public static final String FEATURE = "feature";

    /** The field descriptions. */
    private Collection<FieldDescription> fieldDescriptions;

    /** The parser. */
    private SAXParser parser;

    /** The current field description. */
    private FieldDescription currentFieldDescription;

    /** The current annotations description. */
    private AnnotationsDescription currentAnnotationsDescription;

    /** The annotation stack. */
    private Stack<AnnotationDescription> annotationStack;

    /** The features stack. */
    private Stack<FeaturesDescription> featuresStack;

    /** The feature stack. */
    private Stack<FeatureDescription> featureStack;

    /** The current locator. */
    private Locator currentLocator;

    /** The element mappers. */
    private Map<String, ElementMapper<?>> elementMappers;

    /**
     * Instantiates a new mapping file reader.
     *
     * @param parserParam
     *            the parser
     * @param elementMappersParam
     *            the element mappers
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public MappingFileReader(final SAXParser parserParam,
                             final Map<String, ElementMapper<?>> elementMappersParam)
            throws IOException {
        super();
        fieldDescriptions = new ArrayList<FieldDescription>();
        annotationStack = new Stack<AnnotationDescription>();
        featuresStack = new Stack<FeaturesDescription>();
        featureStack = new Stack<FeatureDescription>();

        this.parser = parserParam;
        this.elementMappers = elementMappersParam;
    }

    /**
     * Read field descriptions from file.
     *
     * @param mappingFile
     *            the mapping file
     *
     * @return the collection< field description>
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws SAXException
     *             the SAX exception
     */
    public final Collection<FieldDescription> readFieldDescriptionsFromFile(final File mappingFile)
            throws IOException, SAXException {

        parser.parse(mappingFile, this);
        return fieldDescriptions;
    }

    /**
     * Sets the document locator.
     *
     * @param locator
     *            the locator
     *
     * @see org.xml.sax.helpers.DefaultHandler#setDocumentLocator(org.xml.sax.Locator)
     */
    @Override
    public final void setDocumentLocator(final Locator locator) {
        currentLocator = locator;
    }

    /**
     * Start element.
     *
     * @param uri
     *            the uri
     * @param localName
     *            the local name
     * @param qName
     *            the q name
     * @param attributes
     *            the attributes
     *
     * @throws SAXException
     *             the SAX exception
     *
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
     *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
    public final void startElement(final String uri,
                                   final String localName,
                                   final String qName,
                                   final Attributes attributes)
            throws SAXException {
        if (qName.equals(FIELD)) {
            addFieldDescription(attributes);
        } else if (qName.equals(ANNOTATIONS)) {
            addAnnotationsDescription(attributes);
        } else if (qName.equals(ANNOTATION)) {
            addAnnotationDescription(attributes);
        } else if (qName.equals(FEATURES)) {
            addFeaturesDescription(attributes);
        } else if (qName.equals(FEATURE)) {
            addFeatureDescription(attributes);
        }
    }

    /**
     * End element.
     *
     * @param uri
     *            the uri
     * @param localName
     *            the local name
     * @param name
     *            the name
     *
     * @throws SAXException
     *             the SAX exception
     *
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    @Override
    public final void endElement(final String uri,
                                 final String localName,
                                 final String name)
            throws SAXException {
        if (name.equals(ANNOTATION)) {
            if (!annotationStack.empty()) {
                annotationStack.pop();
            }
        } else if (name.equals(FEATURES)) {
            if (!featuresStack.empty()) {
                featuresStack.pop();
            }
        } else if (name.equals(FEATURE)) {
            if (!featureStack.empty()) {
                featureStack.pop();
            }
        }
    }

    /**
     * Adds the field description.
     *
     * @param attributes
     *            the attributes
     */
    private void addFieldDescription(final Attributes attributes) {
        ElementMapper<FieldDescription> fieldMapper;
        fieldMapper = (ElementMapper<FieldDescription>) elementMappers
                                                            .get(FIELD);
        currentFieldDescription = fieldMapper.mapElement(attributes);
        mapLocator(currentFieldDescription);
        fieldDescriptions.add(currentFieldDescription);
    }

    /**
     * Adds the annotations description.
     *
     * @param attributes
     *            the attributes
     */
    private void addAnnotationsDescription(final Attributes attributes) {
        ElementMapper<AnnotationsDescription> annotationsMapper;
        annotationsMapper = (ElementMapper<AnnotationsDescription>) elementMappers
                                                                        .get(ANNOTATIONS);
        currentAnnotationsDescription = annotationsMapper
                .mapElement(attributes);
        mapLocator(currentAnnotationsDescription);
        currentFieldDescription
                .setAnnotationsDescriptions(currentAnnotationsDescription);
    }

    /**
     * Adds the annotation description.
     *
     * @param attributes
     *            the attributes
     */
    private void addAnnotationDescription(final Attributes attributes) {
        ElementMapper<AnnotationDescription> annotationMapper;
        annotationMapper = (ElementMapper<AnnotationDescription>) elementMappers
                                                                    .get(ANNOTATION);
        AnnotationDescription annotationDescription = annotationMapper
                .mapElement(attributes);
        mapLocator(annotationDescription);
        if (featureStack.empty()) {
            Collection<AnnotationDescription> annotationDescriptions;
            annotationDescriptions = currentAnnotationsDescription
                                            .getAnnotationDescriptions();
            annotationDescriptions.add(annotationDescription);
        } else {
            featureStack.peek().setAnnotationDescription(annotationDescription);
        }
        annotationStack.push(annotationDescription);
    }

    /**
     * Adds the features description.
     *
     * @param attributes
     *            the attributes
     */
    private void addFeaturesDescription(final Attributes attributes) {
        ElementMapper<FeaturesDescription> featuresMapper;
        featuresMapper = (ElementMapper<FeaturesDescription>) elementMappers
                                                                .get(FEATURES);
        FeaturesDescription featuresDescription = featuresMapper
                                                    .mapElement(attributes);
        mapLocator(featuresDescription);
        annotationStack.peek().setFeaturesDescriptions(featuresDescription);
        featuresStack.push(featuresDescription);
    }

    /**
     * Adds the feature description.
     *
     * @param attributes
     *            the attributes
     */
    private void addFeatureDescription(final Attributes attributes) {
        ElementMapper<FeatureDescription> featureMapper;
        featureMapper = (ElementMapper<FeatureDescription>) elementMappers
                                                                .get(FEATURE);
        FeatureDescription featureDescription = featureMapper
                .mapElement(attributes);
        mapLocator(featureDescription);
        Collection<FeatureDescription> featureDescriptions = featuresStack
                .peek().getFeatureDescriptions();
        featureDescriptions.add(featureDescription);
        featureStack.push(featureDescription);
    }

    /**
     * Map locator.
     *
     * @param locateable
     *            the locateable
     */
    private void mapLocator(final Locateable locateable) {
        if (currentLocator != null) {
            int lineNumber = currentLocator.getLineNumber();
            locateable.setLineNumber(lineNumber);
            int columnNumber = currentLocator.getColumnNumber();
            locateable.setColumnNumber(columnNumber);
        }
    }

}