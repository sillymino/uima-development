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
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Logger;
import org.barcelonamedia.uima.lemmatizer.internal_ts.ConceptMapperLemma;



/**
 * Filter out Lemma annotations set by Lemmatator.xml. Precondition: each Lemma annotation has to coincide with a Token annotation. The precondition is met if
 * parameter TokenAnnotation = de.julielab.jules.types.Token in Lemmatator.xml.
 */
public class LemmaFilter extends CasAnnotator_ImplBase {

	/** POS Feature defined in descriptor file */
	private String posFeatureName;
	
	/** Lemma Feature defined in descriptor file */
	private String lemmaFeatureName;
	
	/** Lemma Confidence Feature defined in descriptor file */
	private String lemmaConfidenceFeatureName;
	
	/** Token type defined in descriptor file */
	private String tokenTypeName;
	
	/** Token type */
	private Type tokenType;
	
	/** POS Feature */
	private Feature posFeature;
	
	/** Lemma Feature */
	private Feature lemmaFeature;
	
	/** Lemma Confidence Feature */
	private Feature lemmaConfidenceFeature;

	/**
	 * if true: in case no lemma is found, the token value will be used as lemma. if false: in case no lemma is found, lemma = null.
	 **/
	private boolean printDefaultLemma;

	/** if set, only match initial characters of POS tag */
	private Integer matchChars;

	private Logger logger;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		this.logger = context.getLogger();
		
		this.posFeatureName = (String) context.getConfigParameterValue("posFeatureName");
		this.lemmaFeatureName = (String) context.getConfigParameterValue("lemmaFeatureName");
		this.lemmaConfidenceFeatureName = (String) context.getConfigParameterValue("lemmaConfidenceFeatureName");
		this.tokenTypeName = (String) context.getConfigParameterValue("tokenTypeName");
		
		this.printDefaultLemma = (Boolean) context.getConfigParameterValue("printDefaultLemma");
		this.matchChars = (Integer) context.getConfigParameterValue("matchChars");
	}
	
	/**
	* Initializes the type system.
	*/
	public void typeSystemInit(TypeSystem typeSystem) throws AnalysisEngineProcessException{
		
		this.tokenType = typeSystem.getType(this.tokenTypeName);
		
		this.posFeature = this.tokenType.getFeatureByBaseName(this.posFeatureName);
		this.lemmaFeature = this.tokenType.getFeatureByBaseName(this.lemmaFeatureName);
		this.lemmaConfidenceFeature = this.tokenType.getFeatureByBaseName(this.lemmaConfidenceFeatureName);
	}

	/**
	 * Filter superfluous lemmas from CAS and copylLemma info into Token.
	 */
	public void process(CAS aCAS) throws AnalysisEngineProcessException {
		
		//FSIndex cpLemmaIndex = null;
		Iterator<Annotation> tokenIt = null;
		
		try{
			
			//cpLemmaIndex = aCAS.getJCas().getAnnotationIndex(ConceptMapperLemma.type);
			tokenIt = aCAS.getJCas().getAnnotationIndex(this.tokenType).iterator();
			
			while (tokenIt.hasNext()) {
	
				Annotation token = (Annotation)tokenIt.next();
	
				String pos = token.getStringValue(this.posFeature);
				
				if (this.matchChars != null) { // limit POS tag
					pos = truncate(pos, this.matchChars);
				}
				boolean match = false;
				
				FSIterator<Annotation> cpLemmaSubiterator = aCAS.getJCas().getAnnotationIndex(ConceptMapperLemma.type).subiterator(token);
				
				//Iterator cpLemmaIt = cpLemmaIndex.iterator();
				while (cpLemmaSubiterator.hasNext()) {
					
					ConceptMapperLemma cpLemma = (ConceptMapperLemma) cpLemmaSubiterator.next();
					String lemmaPos = cpLemma.getPos();
					
					if (this.matchChars != null) { // limit POS tag
						lemmaPos = truncate(lemmaPos, this.matchChars);
					}
					
					if (cpLemma != null && sameOffsets(token, cpLemma) && pos.equals(lemmaPos)) {
	
						token.setStringValue(this.lemmaFeature, cpLemma.getValue());
						
						if(this.lemmaConfidenceFeature != null){
						
							token.setStringValue(this.lemmaConfidenceFeature, "100");
						}
						
						match = true;
						break;
					}
				}
	
				// default lemma to be added in case none of the lemmas in the CAS matches with POS
				if (printDefaultLemma && !match){
					
					token.setStringValue(this.lemmaFeature, token.getCoveredText());
					
					if(this.lemmaConfidenceFeature != null){
					
						token.setStringValue(this.lemmaConfidenceFeature, "0");
					}
				}
			}
			
			removeFromCAS(aCAS.getJCas().getAnnotationIndex(ConceptMapperLemma.type));
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
