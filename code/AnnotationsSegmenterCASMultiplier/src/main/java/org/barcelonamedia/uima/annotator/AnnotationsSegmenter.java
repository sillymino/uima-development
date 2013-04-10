package org.barcelonamedia.uima.annotator;

import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasMultiplier_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.AbstractCas;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Type;
import org.apache.uima.examples.SourceDocumentInformation;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.CasCopier;
import org.uimafit.util.JCasUtil;


public class AnnotationsSegmenter extends JCasMultiplier_ImplBase{


	/* Correponds to a parameter that specifies SPLITTING_ANNOTATION to be used.
	 * The value of this variable is 'SplittingAnnotation' which is the name of 
	 * the parameter in the descriptor file that must be set.
	 * @see "/AnnotationsSegmenter/desc/AnnotationsSegmenter.xml"
	 */
	private static final String SPLITTING_ANNOTATION = "SplittingAnnotation";
	
	//Anotación a partir de la cual se crearán los nuevos CAS
	private String splittingAnnotation;
	
	//Tipo de la anotación de splitting
	Type splittingAnnotation_type;
	
	//Iterador sobre las anotaciones a partir de las cuales se crearán los nuevos CAS
	FSIterator<Annotation> splittingAnnotationIterator;
	
	private String docUri;
	
	private int documentSize;
	
	
	public void initialize(UimaContext aContext) throws ResourceInitializationException{
	    
		super.initialize(aContext);
		
		this.splittingAnnotation = (String) aContext.getConfigParameterValue(SPLITTING_ANNOTATION);
	}
	  
	@Override
	public boolean hasNext() throws AnalysisEngineProcessException{

		if(this.splittingAnnotationIterator == null){

			return false;
		}
		else{

			return this.splittingAnnotationIterator.hasNext();
		}
	}

	@Override
	public AbstractCas next() throws AnalysisEngineProcessException{
		
		Annotation splitAnnotation = (Annotation)this.splittingAnnotationIterator.next();
		
		CAS previousCAS = splitAnnotation.getCAS();
	    JCas jcas = getEmptyJCas();
	    
	    CasCopier casCopier = new CasCopier(previousCAS, jcas.getCas());
	    
	    try{
	    	
	    	Annotation annotation = new Annotation(splitAnnotation.getCAS().getJCas());
	    	
			List<Annotation> coveredAnnotations = JCasUtil.selectCovered(splitAnnotation.getCAS().getJCas(), 
																		(Class<Annotation>) annotation.getClass(), 
																		splitAnnotation.getBegin(), splitAnnotation.getEnd());
			
			
	    	jcas.setDocumentText(splitAnnotation.getCoveredText());
	    	// if original CAS had SourceDocumentInformation, also add SourceDocumentInformatio
	    	// to each segment
	      
	    	if(this.docUri != null){
	    	  
	    		SourceDocumentInformation sdi = new SourceDocumentInformation(jcas);
	    		sdi.setUri(this.docUri);
	    		sdi.setOffsetInSource(splitAnnotation.getBegin());
	    		sdi.setDocumentSize(this.documentSize);
	    		sdi.addToIndexes();	    		

	    		if(!this.splittingAnnotationIterator.hasNext()){
	    			
	    			sdi.setLastSegment(true);
	    		}
	    		
	    		for(Annotation coveredAnnotation : coveredAnnotations){
	    			
	    			if(coveredAnnotation.getType() != 
	    				previousCAS.getJCas().getAnnotationIndex(SourceDocumentInformation.type).getType()){
	    			
		    			Annotation newAnnotation = (Annotation) casCopier.copyFs(coveredAnnotation);
	    				
	    				int updatedBegin = newAnnotation.getBegin() - splitAnnotation.getBegin();
	    				int updatedEnd = newAnnotation.getEnd() - splitAnnotation.getBegin();
	    				
	    				newAnnotation.setBegin(updatedBegin);
	    				newAnnotation.setEnd(updatedEnd);
	    				newAnnotation.addToIndexes();
	    			}
				}
	    	}
	      	
	      	return jcas;
	    }
	    catch(Exception e){
	
	    	jcas.release();
	    	throw new AnalysisEngineProcessException(e);
	    }
	}

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException{

		
		this.splittingAnnotation_type = aJCas.getTypeSystem().getType(this.splittingAnnotation);
		this.splittingAnnotationIterator = aJCas.getAnnotationIndex(splittingAnnotation_type).iterator();
			
	    
	    // Retreive the filename of the input file from the CAS so that it can be added
	    // to each segment
	    FSIterator it = aJCas.getAnnotationIndex(SourceDocumentInformation.type).iterator();
	    
	    if(it.hasNext()){
	    	
	    	SourceDocumentInformation fileLoc = (SourceDocumentInformation) it.next();
	    	
	    	this.docUri = fileLoc.getUri();
	    	this.documentSize = fileLoc.getDocumentSize();
	    } 
	    else{
	    	
	    	this.docUri = null;
	    }
	}
}
