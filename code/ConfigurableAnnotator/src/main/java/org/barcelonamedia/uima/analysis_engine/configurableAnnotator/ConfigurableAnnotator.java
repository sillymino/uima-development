package org.barcelonamedia.uima.analysis_engine.configurableAnnotator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;

import org.barcelonamedia.uima.analysis_engine.configurableAnnotator.analysis.AnnotationsIterator;
import org.barcelonamedia.uima.analysis_engine.configurableAnnotator.analysis.FieldBuilder;
import org.barcelonamedia.uima.analysis_engine.configurableAnnotator.analysis.InvalidFeatureException;

import org.barcelonamedia.uima.analysis_engine.configurableAnnotator.mapping.AnnotationMapper;
import org.barcelonamedia.uima.analysis_engine.configurableAnnotator.mapping.AnnotationsDescription;
import org.barcelonamedia.uima.analysis_engine.configurableAnnotator.mapping.AnnotationsMapper;
import org.barcelonamedia.uima.analysis_engine.configurableAnnotator.mapping.ElementMapper;
import org.barcelonamedia.uima.analysis_engine.configurableAnnotator.mapping.FeatureMapper;
import org.barcelonamedia.uima.analysis_engine.configurableAnnotator.mapping.FeaturesMapper;
import org.barcelonamedia.uima.analysis_engine.configurableAnnotator.mapping.FieldDescription;
import org.barcelonamedia.uima.analysis_engine.configurableAnnotator.mapping.FieldMapper;
import org.barcelonamedia.uima.analysis_engine.configurableAnnotator.mapping.Locateable;
import org.barcelonamedia.uima.analysis_engine.configurableAnnotator.mapping.MappingFileReader;

import org.barcelonamedia.uima.analysis_engine.configurableAnnotator.types.AttributeField;
import org.barcelonamedia.uima.analysis_engine.configurableAnnotator.types.Field;

import org.barcelonamedia.uima.types.AttributeValue;


/**
 * The Class SolrConsumer. Retrieves the cas information described in a xml
 * mapping file and formats it into a valid solr xml. Also, can posts it to the
 * defined server or output the results to a file.
 */
public class ConfigurableAnnotator extends JCasAnnotator_ImplBase {

	private static final String DOC_ID_ANNOTATION_TYPE = "org.barcelonamedia.uima.types.StringAttributeValue";

	private static final String DOC_ID_FIELDNAME = "docId";

	/** The logger object. */
	private static final Logger logger = Logger.getLogger(ConfigurableAnnotator.class);

	/** The name of the mapping file parameter. */
	public static final String PARAM_MAPPINGFILE = "mappingFile";

	/** The name of the add docname option parameter. */
	public static final String PARAM_ADD_DOCNAME_FIELD = "addDocumentFilenameField";
	
	/** The name of the add docname option parameter. */
	public static final String PARAM_SEGMENT_TYPE_NAME = "segmentTypeName";

	/** The field descriptions. */
	private Collection<FieldDescription> fieldDescriptions;

	/** Name of the Type of the segment annotation **/
	private String segmentTypeName;
	
	/** Type of the segment annotation **/
	private Type segmentType;
	
	/** The field builder. */
	private FieldBuilder fieldBuilder;

	private Boolean addDocnameField;

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
	public final void initialize(UimaContext mContext) throws ResourceInitializationException{
		
		super.initialize(mContext);

		BasicConfigurator.configure();

		this.fieldBuilder = new FieldBuilder(logger);

		createFieldDescriptions(mContext);

		this.addDocnameField = false;
		
		Object addDocnameParam = mContext.getConfigParameterValue(PARAM_ADD_DOCNAME_FIELD);

		if (addDocnameParam != null){
			
			this.addDocnameField = (Boolean) addDocnameParam;
		}
		
		this.segmentTypeName = (String) mContext.getConfigParameterValue(PARAM_SEGMENT_TYPE_NAME);
		
		logger.info("initialize() - Add document name: " + addDocnameField);
	}


	/**
	 * Parses the mapping field parameter and creates the field descriptions.
	 *
	 * @throws ResourceInitializationException
	 *             If the mapping file parameter is not set, or if there is a
	 *             parse error
	 */
	private void createFieldDescriptions(UimaContext mContext) throws ResourceInitializationException{

		String mappingFilePath = (String) mContext.getConfigParameterValue(PARAM_MAPPINGFILE);

		try{
			
			MappingFileReader mappingFileReader = createMappingFileReader();

			logger.info("initialize() - Mapping File: " + mappingFilePath);

			File mappingFile = new File(mappingFilePath);
			this.fieldDescriptions = mappingFileReader.readFieldDescriptionsFromFile(mappingFile);
		} 
		catch(IOException e){
			
			throw new ResourceInitializationException(e);
		} 
		catch(ParserConfigurationException e){
			
			throw new ResourceInitializationException(e);
		} 
		catch(SAXException e){
			
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
	private MappingFileReader createMappingFileReader() throws ParserConfigurationException, SAXException, IOException {

		SAXParser parser = SAXParserFactory.newInstance().newSAXParser();

		Map<String, ElementMapper<Locateable>> elementMappers;
		elementMappers = new HashMap<String, ElementMapper<Locateable>>();
		elementMappers.put(MappingFileReader.FIELD, new FieldMapper());
		elementMappers.put(MappingFileReader.ANNOTATIONS, new AnnotationsMapper());
		elementMappers.put(MappingFileReader.ANNOTATION, new AnnotationMapper());
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
	public final void process(JCas jCas) throws AnalysisEngineProcessException{

		this.segmentType = jCas.getTypeSystem().getType(this.segmentTypeName);
		
		Iterator<Annotation> seg_type_iterator = jCas.getAnnotationIndex(this.segmentType).iterator();		

		while(seg_type_iterator.hasNext()){
			
			AnnotationFS seg_annotation = (Annotation)seg_type_iterator.next();
			
			Collection<Field> fields = new ArrayList<Field>();

			if(addDocnameField){
				
				fields.add(createDocIdField(jCas));            
			}        

			TypeSystem typeSystem = jCas.getTypeSystem();
			
			// iterate over field descriptions from mapping file
			for (FieldDescription fieldDescription : fieldDescriptions){
				
				AnnotationsDescription annotations = fieldDescription.getAnnotationsDescriptions();
				
				// create an iterator to extract the information according to
				// the annotation descriptions
				AnnotationsIterator annotationsIterator;
				annotationsIterator = new AnnotationsIterator(jCas, annotations, seg_annotation);
				
				try{
					
					fields.addAll(fieldBuilder.createFields(typeSystem, annotationsIterator, annotations, fieldDescription));
				}
				catch (InvalidFeatureException e){
					
					logger.error("processCas(CAS)", e);
					throw new AnalysisEngineProcessException(e);
				}
				catch (Exception e){
					
					logger.error(e.getMessage());                    
					throw new AnalysisEngineProcessException(e);
				}
			}

			addFieldsAsAnnotations(fields, jCas.getCas(), seg_annotation);
		}
	}

	private AttributeField createDocIdField(JCas jCas){

		// Extract the document name and add it to the metadata
		Type srcDocInfo = jCas.getTypeSystem().getType("org.apache.uima.examples.SourceDocumentInformation");
		AnnotationIndex<Annotation> docInfoAnnotList = jCas.getAnnotationIndex(srcDocInfo);
		Annotation infoDoc = docInfoAnnotList.iterator().next();
		String documentName = infoDoc.getFeatureValueAsString(srcDocInfo.getFeatureByBaseName("uri"));		
		Type docIdType = jCas.getTypeSystem().getType(DOC_ID_ANNOTATION_TYPE);
		
		return new AttributeField(DOC_ID_FIELDNAME, documentName, docIdType);
	}

	private void addFieldsAsAnnotations(Collection<Field> fields, CAS cas, AnnotationFS seg_annotation){
		
		Type annotationType;
		AttributeValue annotation;
		
		for(Field field: fields){
			
			annotationType = ((AttributeField) field).type();
			
			if(field.getBegin() != null){
				
				annotation = (AttributeValue) cas.createAnnotation(annotationType, field.getBegin(), field.getEnd());            	
			}
			else{
				
				annotation = (AttributeValue) cas.createAnnotation(annotationType, seg_annotation.getBegin(), seg_annotation.getEnd());
			}
			
			annotation.setAttributeName(field.name());
			annotation.setValue(field.stringValue());
			annotation.addToIndexes();
		}       
	}
}
