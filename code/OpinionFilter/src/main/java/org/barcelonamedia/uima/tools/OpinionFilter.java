package org.barcelonamedia.uima.tools;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.CasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.cas.FSIndexRepository;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.util.JCasUtil;



/**
 * Filter out Lemma annotations set by Lemmatator.xml. Precondition: each Lemma annotation has to coincide with a Token annotation. The precondition is met if
 * parameter TokenAnnotation = de.julielab.jules.types.Token in Lemmatator.xml.
 */
public class OpinionFilter extends CasAnnotator_ImplBase {

	/** Internal annotation type defined in descriptor file */
	private String internalTypeName;
	
	/** Token type defined in descriptor file */
	private String tokenTypeName;
	
	/** External annotation type defined in descriptor file */
	private String externalTypeName;
	
	/** POS Feature defined in descriptor file */
	private String matchingFeatureName;
	
	/** POS Feature defined in descriptor file */
	String[] copyFeaturesName;
	
	
	/** Internal type */
	private Type internalType;
	
	/** Token type */
	private Type tokenType;
	
	/** External type */
	private Type externalType;

	/** Internal annotation internalMatchingFeature Feature */
	private Feature internalMatchingFeature;
	
	/** Token annotation tokenMatchingFeature Feature */
	private Feature tokenMatchingFeature;
	
	/** Internal annotation copy Features */
	private ArrayList<Feature> internalCopyFeatures;
	
	/** External annotation copy Features */
	private ArrayList<Feature> externalCopyFeatures;

	
	/** if set, only match initial characters of POS tag */
	private Integer matchChars;


	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException{
		
		super.initialize(context);
		
		this.internalTypeName = (String) context.getConfigParameterValue("internalTypeName");
		this.tokenTypeName = (String) context.getConfigParameterValue("tokenTypeName");
		this.externalTypeName = (String) context.getConfigParameterValue("externalTypeName");
		
		this.matchingFeatureName = (String) context.getConfigParameterValue("matchingFeatureName");
		
		this.copyFeaturesName = (String[]) context.getConfigParameterValue("copyFeaturesNames");

		this.matchChars = (Integer) context.getConfigParameterValue("matchChars");
		
		this.internalCopyFeatures = new ArrayList<Feature>();
		this.externalCopyFeatures = new ArrayList<Feature>();
	}
	
	/**
	* Initializes the type system.
	*/
	public void typeSystemInit(TypeSystem typeSystem) throws AnalysisEngineProcessException{
		
		this.internalType = typeSystem.getType(this.internalTypeName);
		this.tokenType = typeSystem.getType(this.tokenTypeName);
		this.externalType = typeSystem.getType(this.externalTypeName);
		
		this.internalMatchingFeature = this.internalType.getFeatureByBaseName(this.matchingFeatureName);
		this.tokenMatchingFeature = this.tokenType.getFeatureByBaseName(this.matchingFeatureName);
		
		for(int index=0; index < this.copyFeaturesName.length; index++){
			
			this.internalCopyFeatures.add(this.internalType.getFeatureByBaseName(this.copyFeaturesName[index]));
			this.externalCopyFeatures.add(this.externalType.getFeatureByBaseName(this.copyFeaturesName[index]));
		}
	}

	/**
	 * Filter superfluous lemmas from CAS and copylLemma info into Token.
	 */
	public void process(CAS aCAS) throws AnalysisEngineProcessException {
		
		Iterator<Annotation> internalTypeIt = null;
		
		System.out.println("process");
		
		try{
			
			FSIndexRepository indexes = aCAS.getJCas().getIndexRepository();
			internalTypeIt = aCAS.getJCas().getAnnotationIndex(this.internalType).iterator();
			
			while(internalTypeIt.hasNext()){
	
				Annotation internalAnnotation = (Annotation)internalTypeIt.next();
	
				String internalMatchingFeatureValue = internalAnnotation.getStringValue(this.internalMatchingFeature);
				
				if(this.matchChars != null){
					
					internalMatchingFeatureValue = truncate(internalMatchingFeatureValue, this.matchChars);
				}
				
				Annotation token = (Annotation) aCAS.getJCas().getCas().createAnnotation(aCAS.getTypeSystem().getType(this.tokenTypeName), 0, 0);
				List<Annotation> tokenAnnotations = JCasUtil.selectCovered(aCAS.getJCas(), (Class<Annotation>) token.getClass(), internalAnnotation.getBegin(), internalAnnotation.getEnd());
				
				for(Annotation tokenAnnotation : tokenAnnotations){
					
					//Annotation tokenAnnotation = (Annotation) tokenAnnotationSubiterator.next();
					String tokenMatchingFeatureValue = tokenAnnotation.getStringValue(this.tokenMatchingFeature);
					
					if(this.matchChars != null){
						
						internalMatchingFeatureValue = truncate(internalMatchingFeatureValue, this.matchChars);
					}
					
					System.out.println("tokenMatchingFeatureValue: " + tokenMatchingFeatureValue);
					System.out.println("internalMatchingFeatureValue: " + internalMatchingFeatureValue);
					
					if(internalAnnotation != null && sameOffsets(tokenAnnotation, internalAnnotation) && tokenMatchingFeatureValue.equals(internalMatchingFeatureValue)){
						
						System.out.println("create annotation " + this.externalType.getName());
						
						AnnotationFS externalAnnotation = aCAS.createAnnotation(this.externalType, tokenAnnotation.getBegin(),tokenAnnotation.getEnd());
						
						for(int index=0; index < this.copyFeaturesName.length; index++){
							
							String value = internalAnnotation.getStringValue(this.internalCopyFeatures.get(index));
							externalAnnotation.setStringValue(this.externalCopyFeatures.get(index), value);
							
							System.out.println("create feature :: value" + value);
						}

						indexes.addFS(externalAnnotation);
					}
				}
			}
			
			removeFromCAS(aCAS.getJCas().getAnnotationIndex(this.internalType));
		}
		catch (CASRuntimeException e) {

			e.printStackTrace();
		}
		catch (CASException e) {

			e.printStackTrace();
		}
	}

	private boolean sameOffsets(Annotation annot1, Annotation annot2) {
		if (annot1.getBegin() == annot2.getBegin() && annot1.getEnd() == annot2.getEnd()) {
			return true;
		}
		return false;
	}
	
	private void removeFromCAS(FSIndex index) {
		List<Annotation> eliminationList = new ArrayList<Annotation>();
		Iterator it = index.iterator();
		while (it.hasNext()) {
			eliminationList.add((Annotation)it.next());
		}
		for (Annotation annot : eliminationList) {
			annot.removeFromIndexes();
		}
	}
	
	/**
	 * Truncates s to fit within len. If s is null, null is returned.
	 **/
	private String truncate(String s, int len) { 
	  if (s == null) return null;
	  return s.substring(0,Math.min(len, s.length()));
	}
}
