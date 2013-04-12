package org.barcelonamedia.uima.consumer.solrConsumer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import org.apache.commons.io.FilenameUtils;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.ProcessTrace;

import org.barcelonamedia.uima.consumer.solrConsumer.analysis.AnnotationsIterator;
import org.barcelonamedia.uima.consumer.solrConsumer.analysis.DocumentBuilder;
import org.barcelonamedia.uima.consumer.solrConsumer.analysis.FieldBuilder;
import org.barcelonamedia.uima.consumer.solrConsumer.analysis.InvalidFeatureException;
import org.barcelonamedia.uima.consumer.solrConsumer.mapping.AnnotationMapper;
import org.barcelonamedia.uima.consumer.solrConsumer.mapping.AnnotationsDescription;
import org.barcelonamedia.uima.consumer.solrConsumer.mapping.AnnotationsMapper;
import org.barcelonamedia.uima.consumer.solrConsumer.mapping.ElementMapper;
import org.barcelonamedia.uima.consumer.solrConsumer.mapping.FeatureMapper;
import org.barcelonamedia.uima.consumer.solrConsumer.mapping.FeaturesMapper;
import org.barcelonamedia.uima.consumer.solrConsumer.mapping.FieldDescription;
import org.barcelonamedia.uima.consumer.solrConsumer.mapping.FieldMapper;
import org.barcelonamedia.uima.consumer.solrConsumer.mapping.MappingFileReader;
import org.barcelonamedia.uima.consumer.solrConsumer.posting.SimplePostWriter;
import org.barcelonamedia.uima.consumer.solrConsumer.posting.XMLWriter;
import org.barcelonamedia.uima.consumer.solrConsumer.types.Document;
import org.barcelonamedia.uima.consumer.solrConsumer.types.Field;

/**
 * The Class SolrConsumer. Retrieves the cas information described in a xml
 * mapping file and formats it into a valid solr xml. Also, can posts it to the
 * defined server or output the results to a file.
 */
public class SolrConsumer extends CasConsumer_ImplBase {

    /** The logger object. */
    private static final Logger logger = Logger.getLogger(SolrConsumer.class);

    /** The name of the mapping file parameter. */
    public static final String PARAM_MAPPINGFILE = "mappingFile";

    /** The name of the add docname option parameter. */
    public static final String PARAM_ADD_DOCNAME_FIELD = "addDocumentFilenameField";

    /** The name of the xml output file path parameter. */
    public static final String PARAM_XML_OUTPUTFILE = "xmlOutputFile";

    /** The name of the raw output file path parameter. */
    private static final String PARAM_RAW_OUTPUTFILE = "rawOutputFile";

    /** The name of the solr url parameter. */
    private static final String PARAM_URL = "solrURL";

    /** The name of the metadata parameter. */
    private static final String PARAM_METADATA = "metadata";

    /** The field descriptions. */
    private Collection<FieldDescription> fieldDescriptions;

    /** The field builder. */
    private FieldBuilder fieldBuilder;

    /** The document builder. */
    private DocumentBuilder documentBuilder;

    /** The xml out file. */
    private File xmlOutFile;

    /** The raw out file. */
    private File rawOutFile;

    /** The xml file writer. */
    private FileWriter xmlFileWriter;

    /** The raw file writer. */
    private FileWriter rawFileWriter;

    /** The solr url. */
    private String url;

    /** The add_docname_field. Defines if haves to put a docname field. */
    private boolean addDocnameField = false;

    /** The metadata_hash. */
    private Map<String, String> metadataMap;

    /**
     * Initialize the component. Retrieve the parameters and process them,
     * parsing the field descriptions and preparing the strutures needed to
     * process the documents.
     *
     * @param aContext
     *            The UIMA context.
     *
     * @throws ResourceInitializationException
     *             If an error occurs with some resource.
     *
     * @see org.apache.uima.analysis_component.AnalysisComponent_ImplBase#initialize(org.apache.uima.UimaContext)
     */
    public final void initialize()
            throws ResourceInitializationException {
        super.initialize();

        BasicConfigurator.configure();
        this.xmlFileWriter = null;

        parseParameters();

        createFieldDescriptions();

        this.fieldBuilder = new FieldBuilder(logger);
        this.documentBuilder = new DocumentBuilder();
    }

    /**
     * Parse the parameters.
     *
     * @throws ResourceInitializationException
     *             if some resource initialization fails.
     */
    private void parseParameters() throws ResourceInitializationException {

        // Parse add_document parameter, defines if haves to put a docname
        // field.
        parseAddDocument();

        // Parse the metadata parameter, containing the metadata pairs to be
        // added
        parseMetadata();

        // Parse the output file paths parameters and creates a writer for it.
        parseOutfiles();

        // Parse solr host & port parameters
        parseSolrURL();
    }

    /**
     * Parse the add_document parameter.
     */
    private void parseAddDocument() {
        Object addDocnameParam = getUimaContext().getConfigParameterValue(
                PARAM_ADD_DOCNAME_FIELD);

        if (addDocnameParam != null) {
            this.addDocnameField = (Boolean) addDocnameParam;
        }
        logger.info("initialize() - Add document name: " + addDocnameField);
    }

    /**
     * Parse the metadata parameter.
     *
     * @throws ResourceInitializationException
     *             If the array is not in a key-value pairs form.
     */
    private void parseMetadata() throws ResourceInitializationException {
        Object metadata = getUimaContext().getConfigParameterValue(PARAM_METADATA);
        this.metadataMap = generateMetadataHash((String[]) metadata);

        logger.info("initialize() - Metadata: " + metadataMap);
    }

    /**
     * Generate metadata hash from an string array of key-value pairs.
     *
     * @param info
     *            the string array of key-value pairs.
     *
     * @return the metadata into a map
     *
     * @throws ResourceInitializationException
     *             If the array is not in a key-value pairs form.
     */
    private Map<String, String> generateMetadataHash(final String[] info)
            throws ResourceInitializationException {

        HashMap<String, String> metadata = new HashMap<String, String>();
        if (info != null) {
            if (info.length % 2 != 0) {
                throw new ResourceInitializationException("The metadata array"
                        + " is not composed by key-value pairs.",
                        new Object[] { PARAM_METADATA });
            } else {
                int i = 0;
                while (i < info.length) {
                    metadata.put(info[i], info[i + 1]);
                    i += 2;
                }
            }
        }
        return metadata;
    }

    /**
     * Parse the outfile parameter and prepares it to write to.
     *
     * @throws ResourceInitializationException
     *             the resource initialization exception
     */
    private void parseOutfiles() throws ResourceInitializationException {

        String outPath = (String) getUimaContext().getConfigParameterValue(
                PARAM_XML_OUTPUTFILE);

        if (outPath != null) {

            // If specified output directory does not exist, try to create it
            this.xmlOutFile = new File(outPath.trim());
            if (this.xmlOutFile.getParentFile() != null
                    && !this.xmlOutFile.getParentFile().exists()) {
                if (!this.xmlOutFile.getParentFile().mkdirs()) {
                    throw new ResourceInitializationException(
                            ResourceInitializationException.RESOURCE_DATA_NOT_VALID,
                            new Object[] { outPath, PARAM_XML_OUTPUTFILE });
                }
            }
            logger.info("initialize() - XML out File: " + this.xmlOutFile);

            try {
                this.xmlFileWriter = new FileWriter(this.xmlOutFile);
            } catch (IOException e) {
                throw new ResourceInitializationException(e);
            }
        }

        outPath = (String) getUimaContext().getConfigParameterValue(
                PARAM_RAW_OUTPUTFILE);

        if (outPath != null) {

            // If specified output directory does not exist, try to create it
            this.rawOutFile = new File(outPath.trim());
            if (this.rawOutFile.getParentFile() != null
                    && !this.rawOutFile.getParentFile().exists()) {
                if (!this.rawOutFile.getParentFile().mkdirs()) {
                    throw new ResourceInitializationException(
                            ResourceInitializationException.RESOURCE_DATA_NOT_VALID,
                            new Object[] { outPath, PARAM_RAW_OUTPUTFILE });
                }
            }
            logger.info("initialize() - Raw out File: " + this.rawOutFile);

            try {
                this.rawFileWriter = new FileWriter(this.rawOutFile);
            } catch (IOException e) {
                throw new ResourceInitializationException(e);
            }
        }

    }

    /**
     * Parse solr URL.
     */
    private void parseSolrURL() {
        this.url = (String) getUimaContext().getConfigParameterValue(PARAM_URL);
    }

    /**
     * Parses the mapping field parameter and creates the field descriptions.
     *
     * @throws ResourceInitializationException
     *             If the mapping file parameter is not set, or if there is a
     *             parse error
     */
    private void createFieldDescriptions()
            throws ResourceInitializationException {

        String mappingFilePath = (String) getUimaContext().getConfigParameterValue(
                PARAM_MAPPINGFILE);

        try {
            MappingFileReader mappingFileReader = createMappingFileReader();

            logger.info("initialize() - Mapping File: " + mappingFilePath);

            File mappingFile = new File(mappingFilePath);
            this.fieldDescriptions = mappingFileReader
                    .readFieldDescriptionsFromFile(mappingFile);

        } catch (IOException e) {
            throw new ResourceInitializationException(e);
        } catch (ParserConfigurationException e) {
            throw new ResourceInitializationException(e);
        } catch (SAXException e) {
            throw new ResourceInitializationException(e);
        }
    }

    /**
     * Creates the mapping file reader.
     *
     * @return the mapping file reader
     *
     * @throws ParserConfigurationException
     *             Parser configuration error.
     * @throws SAXException
     *             Parse error.
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private MappingFileReader createMappingFileReader()
            throws ParserConfigurationException, SAXException, IOException {

        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();

        Map<String, ElementMapper<?>> elementMappers;
        elementMappers = new HashMap<String, ElementMapper<?>>();
        elementMappers.put(MappingFileReader.FIELD, new FieldMapper());
        elementMappers.put(MappingFileReader.ANNOTATIONS,
                new AnnotationsMapper());
        elementMappers
                .put(MappingFileReader.ANNOTATION, new AnnotationMapper());
        elementMappers.put(MappingFileReader.FEATURES, new FeaturesMapper());
        elementMappers.put(MappingFileReader.FEATURE, new FeatureMapper());

        return new MappingFileReader(parser, elementMappers);
    }

    /**
     * Process the CAS, extracting the annotation information described in the
     * mapping file and stored as annotation descriptions. Also, retrieves the
     * document name. After this, formats the information using the parttern of
     * the mapping file, adding the metadata. Finally outputs the result to an
     * xml file or posts it to solr or both.
     *
     * @param cas
     *            the cas
     *
     * @throws ResourceProcessException
     *             the analysis engine process exception
     *
     * @see org.apache.uima.analysis_component.CasAnnotator_ImplBase#process(org.apache.uima.cas.CAS)
     */
    public final void processCas(final CAS cas)
            throws ResourceProcessException {

        try {
            JCas jCas = cas.getJCas();

            String documentName = null;
            if (addDocnameField) {
                // Extract the document name and add it to the metadata
                Type srcDocInfo = jCas.getTypeSystem().getType(
                        "org.apache.uima.examples.SourceDocumentInformation");
                AnnotationIndex<Annotation> docInfoAnnotList = jCas
                        .getAnnotationIndex(srcDocInfo);
                Annotation infoDoc = docInfoAnnotList.iterator()
                        .next();
                documentName = infoDoc.getFeatureValueAsString(srcDocInfo
                        .getFeatureByBaseName("uri"));
                metadataMap.put("id", FilenameUtils.getName(documentName));
            }
            logger.info("process() - Metadata: " + metadataMap);

            Collection<Field> fields = new ArrayList<Field>();
            // iterate over field descriptions from mapping file
            for (FieldDescription fieldDescription : fieldDescriptions) {
                AnnotationsDescription annotations = fieldDescription
                        .getAnnotationsDescriptions();
                // create an iterator to extract the information according to
                // the annotation descriptions
                AnnotationsIterator annotationsIterator;
                annotationsIterator = new AnnotationsIterator(jCas, annotations);

                fields.addAll(fieldBuilder.createFields(annotationsIterator,
                        annotations, fieldDescription));
            }

            Document document = documentBuilder.createDocument(fields);

            toFileOutput(document);

            postToSolr(document);

        } catch (CASException e) {
            logger.error("processCas(CAS)", e);
            throw new ResourceProcessException(e);
        } catch (InvalidFeatureException e) {
            logger.error("processCas(CAS)", e);
            throw new ResourceProcessException(e);
        }
    }

    /**
     * Output the document as one or various files.
     *
     * @param document
     *            The document containing the extracted information
     *
     * @throws ResourceProcessException
     *             If the write to file process fails.
     */
    private void toFileOutput(final Document document)
            throws ResourceProcessException {

        if (xmlFileWriter != null) {
            try {
                XMLWriter.writeDoc(document, metadataMap, xmlFileWriter);
                xmlFileWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
                throw new ResourceProcessException(
                        ResourceProcessException.RESOURCE_DATA_NOT_VALID,
                        new Object[] { xmlOutFile, PARAM_XML_OUTPUTFILE });
            }
        }
        if (rawFileWriter != null) {
            try {
                String val;
                for (Object field : document.getFields()) {
                    val = ((Field) field).stringValue();

                    rawFileWriter.write(val);
                    rawFileWriter.write('\n');
                }
                rawFileWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
                throw new ResourceProcessException(
                        ResourceProcessException.RESOURCE_DATA_NOT_VALID,
                        new Object[] { rawOutFile, PARAM_RAW_OUTPUTFILE });
            }
        }
    }

    /**
     * Format the document as an xml and post it to solr.
     *
     * @param document
     *            The document containing the extracted information
     *
     * @throws ResourceProcessException
     *             If the post process fails.
     */
    private void postToSolr(final Document document)
            throws ResourceProcessException {

        if (this.url != null) {
            writeDocument(document, this.url);
            writeCommit(this.url);
        }
    }

    /**
     * Write document to solr post writer.
     *
     * @param document
     *            The document to post to solr
     * @param addr
     *            The solr address.
     *
     * @throws AnalysisEngineProcessException
     *             If the post process fails.
     */
    private void writeDocument(final Document document, final String addr)
            throws ResourceProcessException {

        SimplePostWriter postWriter = new SimplePostWriter(addr);
        try {
            XMLWriter.writeDoc(document, metadataMap, postWriter);
            postWriter.flush();
            postWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new ResourceProcessException(
                    ResourceProcessException.RESOURCE_DATA_NOT_VALID,
                    new Object[] { this.url, PARAM_URL });
        }
    }

    /**
     * Write commit to solr.
     *
     * @param addr
     *            The solr address.
     *
     * @throws AnalysisEngineProcessException
     *             If the post process fails.
     */
    private void writeCommit(final String addr)
            throws ResourceProcessException {
        SimplePostWriter postWriter = new SimplePostWriter(addr);
        try {
            postWriter.writeCommit();
            postWriter.flush();
            postWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new ResourceProcessException(
                    ResourceProcessException.RESOURCE_DATA_NOT_VALID,
                    new Object[] { this.url, PARAM_URL });
        }
    }

    /**
     * Close the file descriptor.
     *
     * @throws AnalysisEngineProcessException
     *             If some error occurs during analysis.
     *
     * @see org.apache.uima.analysis_component.AnalysisComponent_ImplBase#collectionProcessComplete()
     */
    public final void collectionProcessComplete(ProcessTrace pTrace)
            throws IOException, ResourceProcessException {

        super.collectionProcessComplete(pTrace);
        
        if (xmlFileWriter != null) {
            try {
                xmlFileWriter.close();
            } catch (IOException e) {
                logger.error("collectionProcessComplete()", e);
                throw new ResourceProcessException(e);
            }
        }

        if (rawFileWriter != null) {
            try {
                rawFileWriter.close();
            } catch (IOException e) {
                logger.error("collectionProcessComplete()", e);
                throw new ResourceProcessException(e);
            }
        }
    }
}
