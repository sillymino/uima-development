package org.barcelonamedia.uima.csvMetadataAnnotator;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;

import com.google.common.collect.ImmutableMap;

import au.com.bytecode.opencsv.CSVReader;


public class CSVMetadataAnnotator extends JCasAnnotator_ImplBase {

	private static final String DESCRIPTOR_NAME = "org.barcelonamedia.uima.csvMetadataAnnotator";
    private static final String CSV_FILE_URI_PARAM = "CSV_File_URI";
    private static final String SEPARATOR_PARAM = "field_separator";

    private final static ImmutableMap<String, Character> SEPARATOR_MAP = new ImmutableMap.Builder<String, Character>()
										                                                    .put("comma", new Character(','))
										                                                    .put("tab", new Character('\t'))
										                                                    .put("space", new Character(' '))
										                                                    .build();
    private Logger logger;
    private MetadataMap map;
    private boolean copyOffset;

    
    public void initialize(UimaContext aContext) throws ResourceInitializationException{
    	
        super.initialize(aContext);

        logger = aContext.getLogger();

        String separator_param = (String) aContext.getConfigParameterValue(SEPARATOR_PARAM);
        Character separator = SEPARATOR_MAP.get(separator_param);

        String csvFileURI = (String) aContext.getConfigParameterValue(CSV_FILE_URI_PARAM);
        
        try{
        	
            CSVReader csvReader;
            
            if(separator == null){
            	
                csvReader = new CSVReader(new FileReader(csvFileURI));
            } 
            else{
            	
                csvReader = new CSVReader(new FileReader(csvFileURI), separator.charValue());
            }
            
            logger.log(Level.INFO, "csv - initialize - csv file loaded");
            
            try{
            	
                map = new MetadataMap(csvReader);
                
                System.out.println("Map:");
                
                Iterator it = map.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pairs = (Map.Entry)it.next();
                    System.out.println(pairs.getKey() + " = " + pairs.getValue());
                }

                System.out.println("__________");
            }
            catch(IOException e){
            	
                logger.log(Level.SEVERE, e.getMessage());
                throw new ResourceInitializationException(e);
            }
            
            logger.log(Level.INFO, "csv - initialize - csv file processed");
            
        } 
        catch (FileNotFoundException e){
        	
            logger.log(Level.SEVERE, e.getMessage());
            throw new ResourceInitializationException(e);
        }
    }

    public void process(JCas jCas) throws AnalysisEngineProcessException{
    	
        String filterTypename = map.getFilterTypename();
        String featureName = map.getFeatureName();
        
        if(filterTypename != null && featureName != null){
        	
        	Type filterType = jCas.getTypeSystem().getType(filterTypename);
        	
            if(filterType == null){
            	
                String[] arguments =  {filterTypename, DESCRIPTOR_NAME};
                throw new AnalysisEngineProcessException("class_not_found", arguments);
            }
            
            Feature filterFeature = filterType.getFeatureByBaseName(featureName);
            
            if(filterFeature == null){
            	
                String[] arguments = {featureName, filterTypename};
                throw new AnalysisEngineProcessException("UNDEFINED_FEATURE", arguments);      
            }
            
            AnnotationIndex<Annotation> index = jCas.getAnnotationIndex(filterType);
            FSIterator<Annotation> iter = index.iterator();
            
            String annotationTypename;
            Type annotationType;
            Annotation annotation;
            
            while(iter.hasNext()){
            	
                Annotation annot = iter.next();
                String filterFeatureValue = annot.getFeatureValueAsString(filterFeature);
                                
                List<Metadata> metadataList = map.get(filterFeatureValue);
                                
                if(metadataList != null){
                	
                    for(Metadata meta: metadataList){                    	                 	
                        
                        annotationTypename = meta.getAnnotationType();
                        annotationType = jCas.getTypeSystem().getType(annotationTypename);
                        
                        if(annotationType == null){
                        	
                            String[] arguments =  {annotationTypename, DESCRIPTOR_NAME};
                            throw new AnalysisEngineProcessException("class_not_found", arguments);
                        }

                        //copyOffset is never initialised
                        if(copyOffset){
                        	
                            annotation = (Annotation) jCas.getCas().createAnnotation(annotationType, annot.getBegin(), annot.getEnd());
                        }
                        else{
                        	
                            annotation = (Annotation) jCas.getCas().createAnnotation(annotationType, 0, 0);                    
                        }                                
                        
                        HashMap<String, String> featuresQualifiedName = meta.getFeaturesQualifiedName();
                        Iterator<Map.Entry<String, String>> iterator = featuresQualifiedName.entrySet().iterator();
                        
                    	while(iterator.hasNext()){
                    		
                    		Map.Entry<String, String> pairs = (Map.Entry<String, String>)iterator.next();
                    		
                    		String featureQualifiedName = (String)pairs.getKey();
                    		String featureValue = (String)pairs.getValue();
                    		
                    		Feature feature = jCas.getCas().getTypeSystem().getFeatureByFullName(featureQualifiedName);
                             
                            annotation.setFeatureValueFromString(feature, featureValue);
                    	}
                    	
                        annotation.addToIndexes();
                    }
                }
            }
        }
    }
}
