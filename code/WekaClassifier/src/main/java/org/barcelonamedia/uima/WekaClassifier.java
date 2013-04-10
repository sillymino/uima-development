package org.barcelonamedia.uima;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.cas.FSIndexRepository;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JFSIndexRepository;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;
import org.barcelonamedia.uima.types.AttributeValue;
import org.barcelonamedia.uima.types.DateAttributeValue;
import org.barcelonamedia.uima.types.NominalAttributeValue;
import org.barcelonamedia.uima.types.NumericAttributeValue;
import org.barcelonamedia.uima.types.StringAttributeValue;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;
import weka.core.converters.ConverterUtils.DataSource;


public class WekaClassifier extends JCasAnnotator_ImplBase {

	/**
	 * Correponds to a parameter that points to an arff header file.
	 * The value of this variable is 'ArffHeaderFile' which is the name of 
	 * the parameter in the descriptor file that must be set.
	 * @see "descriptors/analysis_engine/WekaClassifier.xml"
	 */
	public static final String PARAM_ARFF_HEADER_FILE_NAME = "ArffHeaderFile";

	/**
	 * Correponds to a parameter that specifies the name of the class attribute.
	 * The value of this variable is 'ClassAttribute' which is the name of 
	 * the parameter in the descriptor file that must be set.
	 * @see "descriptors/analysis_engine/WekaClassifier.xml"
	 */
	public static final String PARAM_CLASS_NAME = "ClassAttribute";
	
	/**
	 * Correponds to a parameter that specifies the annotation generated by the 
	 * analysis engine containing Weka classifier output.
	 * The value of this variable is 'OutputAnnotation' which is the name of 
	 * the parameter in the descriptor file that must be set.
	 * @see "descriptors/analysis_engine/WekaClassifier.xml"
	 */
	private static final String OUTPUT_ANNOTATION = "OutputAnnotation";
	
	/**
	 * Correponds to a parameter that specifies the annotation generated by the 
	 * analysis engine containing Weka classifier output.
	 * The value of this variable is 'OutputAnnotation' which is the name of 
	 * the parameter in the descriptor file that must be set.
	 * @see "descriptors/analysis_engine/WekaClassifier.xml"
	 */
	private static final String ATTRIBUTE_NAME_FEATURE_NAME = "AttributeNameFeatureName";
	
	/**
	 * Correponds to a parameter that specifies the annotation generated by the 
	 * analysis engine containing Weka classifier output.
	 * The value of this variable is 'OutputAnnotation' which is the name of 
	 * the parameter in the descriptor file that must be set.
	 * @see "descriptors/analysis_engine/WekaClassifier.xml"
	 */
	private static final String VALUE_FEATURE_NAME = "ValueFeatureName";
	
	/**
	 * Correponds to a parameter that specifies the annotation generated by the 
	 * analysis engine containing Weka classifier output.
	 * The value of this variable is 'OutputAnnotation' which is the name of 
	 * the parameter in the descriptor file that must be set.
	 * @see "descriptors/analysis_engine/WekaClassifier.xml"
	 */
	private static final String COMPONENT_ID_FEATURE_NAME = "ComponentIdFeatureName";

	/**
	 * The name of the resource that instantiates a Weka Classifier.  
	 * The value of this variable is 'Classifier' and must be the name
	 * of a bound resource.  
	 * @see "descriptors/analysis_engine/ClassifierAnnotator.xml"
	 */
//	private static final String RESOURCE_CLASSIFER = "Classifier";
	   private static final String CLASSIFER = "WekaModelFile";
	/**
	 * Correponds to a parameter that specifies if regression is being carried out.
	 * The value of this variable is 'regression' which is the name of 
	 * the parameter in the descriptor file that must be set.
	 * @see "descriptors/analysis_engine/WekaClassifier.xml"
	*/
	private static final String REGRESSION = "regression";	
	/**
	 * Correponds to a parameter that specifies the Annotation to use
	 * in order to extract the vector. When this annotation is specified, then
	 * the weka classifier is apllied to each base annottion element."
	 */
	public static final String PARAM_BASE_ANNOTATION = "BaseAnnotation";	
	
	/**
	* Parámetro que indica si se hace regression en la clasificación de WEKA 
	*/
	private Boolean regression;

	private Instances wekaInstances;
	private Classifier classifier;
	
	String outputAnnotation;
	String attributeNameFeatureName;
	String valueFeatureName;
	String componentIdFeatureName;
    String baseAnnotation;
	private Logger logger;
	

	
	public void initialize (UimaContext aContext) throws ResourceInitializationException{

		System.out.println(">> initialize...");
		
		super.initialize(aContext);
		this.logger = aContext.getLogger();

		try{
			System.out.println(">> try......");
			
			String arffHeaderFileName = (String) getContext().getConfigParameterValue(PARAM_ARFF_HEADER_FILE_NAME);
			String className = (String) getContext().getConfigParameterValue(PARAM_CLASS_NAME);
			
			this.outputAnnotation = (String) getContext().getConfigParameterValue(OUTPUT_ANNOTATION);
			this.baseAnnotation = ((String) getContext().getConfigParameterValue(PARAM_BASE_ANNOTATION)).trim();
			
			this.attributeNameFeatureName = (String) getContext().getConfigParameterValue(ATTRIBUTE_NAME_FEATURE_NAME);
			this.valueFeatureName = (String) getContext().getConfigParameterValue(VALUE_FEATURE_NAME);
			this.componentIdFeatureName = (String) getContext().getConfigParameterValue(COMPONENT_ID_FEATURE_NAME);
			
			System.out.println(">> Reading regression attribute...");
			
			this.regression = (Boolean) getContext().getConfigParameterValue(REGRESSION);
			
			System.out.println(">> regression attribute : " + this.regression);		

			DataSource source = new DataSource(arffHeaderFileName);
			wekaInstances = source.getDataSet();

			if(wekaInstances == null){
				
				System.out.println("wekaInstances == null!");
			}
			
			System.out.println("Weka Instances successfully instantiated from header file at "+ arffHeaderFileName);
			
			System.out.println(">> classifier Resource Path: "+ (String) getContext().getConfigParameterValue(CLASSIFER));
			String classifierResource = (String) getContext().getConfigParameterValue(CLASSIFER);
			classifier = (Classifier) weka.core.SerializationHelper.read(classifierResource);
			//ClassifierResource classifierResource2 = (ClassifierResource) getContext().getResourceObject(RESOURCE_CLASSIFER); 
			// classifier = classifierResource2.getClassifier();

			Attribute classAttribute = wekaInstances.attribute(className);
			logger.log(Level.INFO,"class attribute = "+classAttribute.name()+"\n");
			wekaInstances.setClass(classAttribute);    
			logger.log(Level.INFO,"WekaClassifier initialized\n");
		}
		catch(Exception e){

			e.printStackTrace();
			throw new ResourceInitializationException(e);
		}
	}

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {

		//SparseInstance wekaInstance = null;
		DenseInstance wekaInstance = null;
		
		try{
			
			FSIndexRepository indexes = jCas.getIndexRepository();
			
			Type outputAnnotation_type = jCas.getTypeSystem().getType(this.outputAnnotation);
			Type baseAnnotation_type= jCas.getTypeSystem().getType(this.baseAnnotation);
			Feature attributeName_feat = outputAnnotation_type.getFeatureByBaseName(this.attributeNameFeatureName);
			Feature value_feat = outputAnnotation_type.getFeatureByBaseName(this.valueFeatureName);
			Feature componentId_feat = outputAnnotation_type.getFeatureByBaseName(this.componentIdFeatureName);

			if (this.baseAnnotation!="") {
	            for (Annotation baseValue: jCas.getAnnotationIndex(baseAnnotation_type)){
					//logger.log(Level.INFO,">process starts "+wekaInstances.numAttributes()+" "+wekaInstances.toString()+" "+wekaInstances.toSummaryString()+"\n");	
					wekaInstance = CAS2WekaInstance.toWekaInstance(jCas.getCas(), wekaInstances,baseValue.getBegin(),baseValue.getEnd());
					//logger.log(Level.INFO,"\nwekaInstance "+wekaInstance.toString()+" "+wekaInstance.numAttributes()+" "+wekaInstance.numValues()+"\n");
					classify (wekaInstance ,jCas,baseValue.getBegin(),baseValue.getEnd(),outputAnnotation_type,value_feat,attributeName_feat, componentId_feat, indexes);
			 	}
			} else { // classify the full document
			   Annotation docAnn = (Annotation)jCas.getDocumentAnnotationFs();				
				wekaInstance = CAS2WekaInstance.toWekaInstance(jCas.getCas(), wekaInstances);
				//logger.log(Level.INFO,"\nwekaInstance "+wekaInstance.toString()+" "+wekaInstance.numAttributes()+" "+wekaInstance.numValues()+"\n");
				classify (wekaInstance,jCas , docAnn.getBegin(),docAnn.getEnd(),outputAnnotation_type,value_feat,attributeName_feat, componentId_feat, indexes);
			}

		}
		catch(IndexOutOfBoundsException e)
		{						
			System.err.println(this.getClass().getName() + " - process() - " + e.getStackTrace().toString());
			logger.log(Level.WARNING, this.getClass().getName() + " - process() - " + e.getMessage());
			
			System.err.println("WekaInstance : " + wekaInstance.toString());
			logger.log(Level.WARNING, "WekaInstance : " + wekaInstance.toString());
		}
		catch(Exception e)
		{
			System.err.println(e.getMessage());

			throw new AnalysisEngineProcessException(e);
		}
	}
	
	public void classify(DenseInstance wekaInstance,JCas jCas, int begin,int end, Type outputAnnotation_type,Feature value_feat,Feature attributeName_feat, Feature componentId_feat,FSIndexRepository indexes) throws AnalysisEngineProcessException {
		try{
		//logger.log(Level.INFO,"\nwekaInstance "+wekaInstance.toString()+" "+wekaInstance.numAttributes()+" "+wekaInstance.numValues()+"\n");
		
	 	double classification = classifier.classifyInstance(wekaInstance);

		//logger.log(Level.INFO,"classification: "+classification+"\n");
		
		//logger.log(Level.INFO,"classification="+classification+" ("+classificationValue+" for "+classAttribute.name()+")\n");
		//doesn't need to be a NominalAttribute!  Should find out what the classAttribute type is
		
		
		AnnotationFS outputAnnotation = jCas.getCas().createAnnotation(outputAnnotation_type, begin,end);
		Attribute classAttribute = wekaInstances.classAttribute();
		

		if(regression){

			outputAnnotation.setFeatureValueFromString(value_feat, Double.toString(classification));
		}
		else{
			
			String classificationValue = classAttribute.value((int)classification);
			outputAnnotation.setFeatureValueFromString(attributeName_feat, classAttribute.name());
			outputAnnotation.setFeatureValueFromString(value_feat, classificationValue);
			
			logger.log(Level.INFO, outputAnnotation_type.getName() + " " + classAttribute.name() + "  " + classificationValue+"\n");
	    }
		
		outputAnnotation.setFeatureValueFromString(attributeName_feat, classAttribute.name());
		outputAnnotation.setFeatureValueFromString(componentId_feat, this.getClass().getName());

		indexes.addFS(outputAnnotation);
		//logger.log(Level.INFO,"process ends\n");
	}
	catch(IndexOutOfBoundsException e)
	{						
		System.err.println(this.getClass().getName() + " - process() - " + e.getStackTrace().toString());
		logger.log(Level.WARNING, this.getClass().getName() + " - process() - " + e.getMessage());
		
		System.err.println("WekaInstance : " + wekaInstance.toString());
		logger.log(Level.WARNING, "WekaInstance : " + wekaInstance.toString());
	}
	catch(Exception e)
	{
		System.err.println(e.getMessage());

		throw new AnalysisEngineProcessException(e);
	}
	}


}
