/** 
 * EntityAnnotator.java
 * 
 * Copyright (c) 2006, JULIE Lab. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0 
 *
 * Author: tomanek
 * 
 * Current version: 2.2
 * Since version:   1.0
 *
 * Creation date: Nov 29, 2006 
 * 
 * This is an UIMA wrapper for the JULIE NETagger. It produces named entity annotations, 
 * given sentence and token annotations.
 **/

package de.julielab.jules.ae.netagger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.CasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.analysis_engine.annotator.AnnotatorConfigurationException;
import org.apache.uima.analysis_engine.annotator.AnnotatorContextException;
import org.apache.uima.analysis_engine.annotator.AnnotatorInitializationException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JFSIndexRepository;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import de.julielab.jnet.tagger.JNETException;
import de.julielab.jnet.tagger.NETagger;
import de.julielab.jnet.tagger.Unit;
//TODO: make abbreviation functionality independent of type system
//import de.julielab.jules.types.Abbreviation;
//import de.julielab.jules.utility.AnnotationTools;
import edu.umass.cs.mallet.base.fst.CRF4;
import edu.umass.cs.mallet.base.types.Alphabet;


public class EntityAnnotator extends CasAnnotator_ImplBase {

	private static final String COMPONENT_ID = "de.julielab.jules.ae.netagger.EntityAnnotator";

	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = Logger.getLogger(EntityAnnotator.class);

	private final static String OUTSIDE_LABEL = "O"; // default outside label

	private static final String TEXTUAL_REPRESENTATION_FEATURE = "TextualRepresentationFeature";
	private static final String SPECIFIC_TYPE_FEATURE = "SpecificTypeFeature";
	private static final String COMPONENT_ID_FEATURE = "ComponentIDFeature";
	private static final String CONFIDENCE_FEATURE = "ConfidenceFeature";

	protected String sentence_type;
	protected String token_type;

	protected Type sentenceType;
	protected Type tokenType;

	private String TextualRepresentationFeatureName;
	private String SpecificTypeFeatureName;
	private String ComponentIDFeatureName;
	private String ConfidenceFeatureName;

	// which NE label (key) is to be mapped to which UIMA type (value)
	private HashMap<String, String> entityMap;

	private NETagger tagger;

	protected boolean expandAbbr = false;
	
	protected boolean ignoreTokenText = false;

	protected boolean consistPreservation = false;

	protected boolean showSegmentConf = false;

	protected TreeSet<String> entityMentionTypes = null; // the EntityMention subtypes to be
	// filled

	protected NegativeList negativeList;

	Properties featureConfig = null;
	ArrayList<String> activatedMetas = null;
	HashMap<String, Iterator<Annotation>> annotationIterators = null;
	HashMap<String, String> valueMethods = null;

	/**
	 * Initialisiation of UIMA-JNET. Reads in and checks descriptor's parameters.
	 * 
	 * @throws ResourceInitializationException
	 */
	public void initialize(UimaContext aContext) throws ResourceInitializationException {

		LOGGER.info("initialize() - initializing UIMA-JNET...");

		// invoke default initialization
		super.initialize(aContext);

		// get modelfilename from parameters
		try {

			// compulsory param: ModelFilename
			setModel(aContext);

			// compulsory param: EntityTypes
			setEntityTypes(aContext);

			// non-compulsory param: show segment confidence
			setShowSegmentConfidence(aContext);

			// non-compulsory param: NegativeList
			setNegativeList(aContext);

			// compulsory param: ExpandAbbreviations
			Object tmp = aContext.getConfigParameterValue("ExpandAbbreviations");
			if (tmp != null) {
				expandAbbr = (Boolean) tmp;
			}
			// compulsory param: ignoreTokenText
			tmp = aContext.getConfigParameterValue("ignoreTokenText");
			if (tmp != null) {
				this.ignoreTokenText = (Boolean) tmp;
			}
			//Retrieve input type system params
			this.sentence_type = (String) aContext.getConfigParameterValue("sentence_type");
			this.token_type = (String) aContext.getConfigParameterValue("token_type");

			//Retrieve features to be set
			this.TextualRepresentationFeatureName = (String) aContext.getConfigParameterValue(TEXTUAL_REPRESENTATION_FEATURE);
			this.SpecificTypeFeatureName = (String) aContext.getConfigParameterValue(SPECIFIC_TYPE_FEATURE);
			this.ComponentIDFeatureName = (String) aContext.getConfigParameterValue(COMPONENT_ID_FEATURE);
			this.ConfidenceFeatureName = (String) aContext.getConfigParameterValue(CONFIDENCE_FEATURE);

			// consistency preservation
			tmp = aContext.getConfigParameterValue("ConsistencyPreservation");
			if (tmp != null) {
				consistPreservation = (Boolean) tmp;
			}

			// show configuration
			LOGGER.info("initialize() - abbreviation expansion: " + expandAbbr);
			LOGGER.info("initialize() - negative list: " + ((negativeList != null) ? true : false));
			LOGGER.info("initialize() - show confidence: " + showSegmentConf);
			LOGGER.info("initialize() - consistency preservation: " + consistPreservation);

		}
		catch(AnnotatorContextException e){
			throw new ResourceInitializationException();
		}
		catch(AnnotatorConfigurationException e){
			throw new ResourceInitializationException();
		}
		catch(AnnotatorInitializationException e){
			throw new ResourceInitializationException();
		}
	}

	/**
	 * Initializes the type system.
	 */
	public void typeSystemInit(TypeSystem typeSystem) throws AnalysisEngineProcessException{

		LOGGER.info("typeSystemInit() - Loading provided types...");

		this.sentenceType = typeSystem.getType(this.sentence_type);
		this.tokenType = typeSystem.getType(this.token_type);
		//this.posType = typeSystem.getType(this.pos_type);

		LOGGER.info("typeSystemInit() - Types loaded.");
	}

	/**
	 * get initialization of meta information which is used later to get token level meta-info
	 */
	private void retrieveMetaInformation(JCas aJCas) throws AnalysisEngineProcessException{

		JFSIndexRepository indexes = aJCas.getJFSIndexRepository();
		this.featureConfig = this.tagger.getFeatureConfig();
		this.activatedMetas = new ArrayList<String>();
		this.annotationIterators = new HashMap<String, Iterator<Annotation>>();
		this.valueMethods = new HashMap<String, String>();
		Enumeration keys = this.featureConfig.keys();

		// reading which meta datas are enabled
		while (keys.hasMoreElements()){

			String key = (String) keys.nextElement();
			String meta = "";
			if (key.matches("[A-Za-z]+_feat_enabled") && featureConfig.getProperty(key).matches("true")){
				
				meta = key.substring(0, key.indexOf("_feat_enabled"));
				this.activatedMetas.add(meta);
			}
		}

		// obtaining iterators over meta data information		
		for(String activedMeta : activatedMetas){

			try{

				annotationIterators.put(activedMeta, aJCas.getAnnotationIndex(aJCas.getTypeSystem().getType(featureConfig.getProperty(activedMeta + "_feat_data"))).iterator());
				this.valueMethods.put(activedMeta, featureConfig.getProperty(activedMeta + "_feat_valMethod"));				
			}
			catch(Exception e){

				System.err.println("ERROR: " + e.getMessage());
				throw new AnalysisEngineProcessException();
			}
		}
	}

	/**
	 * sets the entity types to be used for different predicted labels
	 */
	private void setEntityTypes(UimaContext aContext) throws ResourceInitializationException,
	AnnotatorContextException, AnnotatorConfigurationException {

		entityMentionTypes = new TreeSet<String>();

		// get entity types from descriptorentityMap = new HashMap<String, String>();
		String[] entityTypes;
		Object o = aContext.getConfigParameterValue("EntityTypes");
		if (o != null) {
			entityTypes = (String[]) o;
		} else {
			LOGGER.error("setEntityTypes() - descriptor incomplete, entity types not specified!");
			throw new AnnotatorConfigurationException();
		}

		// build hasmap from it
		entityMap = new HashMap<String, String>();
		for (int i = 0; i < entityTypes.length; i++) {
			String entityParts[] = entityTypes[i].split("=");
			// format <NE label, UIMA type>
			entityMap.put(entityParts[0], entityParts[1]);
			entityMentionTypes.add(entityParts[1]);
		}

		// test if all labels given through the descriptor exist in the tagger's
		// OutputAlphabet
		CRF4 model = tagger.getModel();
		int j = 0;
		if (model != null) {
			Alphabet alpha = model.getOutputAlphabet();
			Object modelLabels[] = alpha.toArray();

			for (int i = 0; i < entityTypes.length; i++) {
				String[] entityParts = entityTypes[i].split("=");
				boolean entityFound = false;

				for (j = 0; j < modelLabels.length; j++) {
					if (entityParts[0].equals(modelLabels[j])) {
						entityFound = true;
					}
				}
				if (!entityFound) {// this does not happen if we find our label
					LOGGER.error("setEntityTypes() - Could not find entity label \"" + entityParts[0]
							+ "\" from descriptor in the tagger's OutputAlphabet.", null);
					throw new AnnotatorConfigurationException();
				}
			}
		}
		System.out.println(entityMentionTypes.toString());
	}

	/**
	 * set and load the JNET model
	 */
	private void setModel(UimaContext aContext) throws AnnotatorConfigurationException, AnnotatorContextException,
	AnnotatorInitializationException {

		// get model filename
		String modelFilename = "";
		Object o = aContext.getConfigParameterValue("ModelFilename");
		if (o != null) {
			modelFilename = (String) o;
		} else {
			LOGGER.error("setModel() - descriptor incomplete, no model file specified!");
			throw new AnnotatorConfigurationException();
		}

		// produce an instance of JNET with this model
		tagger = new NETagger();
		try {
			LOGGER.debug("setModel() -  loading JNET model...");
			File modelPath = new File(modelFilename);
			tagger.readModel(modelPath.getAbsolutePath());
		} catch (Exception e) {
			LOGGER.error("setModel() - Could not load JNET model: " + e.getMessage(), e);
			throw new AnnotatorInitializationException();
		}
	}

	/**
	 * set and initialize negative, if it should be used
	 */
	private void setNegativeList(UimaContext aContext) throws AnnotatorConfigurationException,
	AnnotatorContextException {
		Object o = aContext.getConfigParameterValue("NegativeList");
		if (o != null) {
			// if NegativeList is set, make the respective object
			File listFile = new File((String) o);
			try {
				negativeList = new NegativeList(listFile);
			} catch (IOException e) {
				LOGGER.error("setNegativeList() - specified negative list file cannot be read: " + e.getMessage());
				throw new AnnotatorConfigurationException();
			}
			LOGGER.debug("setNegativeList() - using negative list: " + listFile);
		}
	}

	/**
	 * set whether confidence should be estimated for entities
	 */
	private void setShowSegmentConfidence(UimaContext aContext) throws AnnotatorContextException {
		Object o = aContext.getConfigParameterValue("ShowSegmentConfidence");
		if (o != null) {
			showSegmentConf = (Boolean) o;
		}
		LOGGER.debug("setShowSegmentConfidence() - show segment confidence: " + showSegmentConf);
	}

	/**
	 * process current CAS. In case, abbreviation expansion is turned on, the abbreviation is
	 * replaced by its full form which is used during prediction. The labels of this full form are
	 * then applied to the original, short form.
	 */
	public void process(CAS aCas) throws AnalysisEngineProcessException {

		LOGGER.debug("process() - processing next document");

		JCas aJCas;
		try {
			aJCas = aCas.getJCas();
		} catch (CASException e1) {
			throw new AnalysisEngineProcessException(e1);
		}

		retrieveMetaInformation(aJCas);

		for (Annotation sentence : aJCas.getAnnotationIndex(this.sentenceType)) {

			// get tokens, abbreviations, and metas for this sentence
			Class<Annotation> tokenClass = (Class<Annotation>) aJCas.getCas().createAnnotation(this.tokenType, 0, 0).getClass();
			List<Annotation> tokenList = UIMAUtils.getAnnotations(aJCas, sentence, tokenClass);

			List<HashMap<String, String>> metaList = getMetaList(tokenList);

			if (tokenList.size() != metaList.size()) {
				LOGGER.error("process() - token list, and meta list for this sentence not of same size!");
				throw new AnalysisEngineProcessException();
			}
			// make the Sentence object
			de.julielab.jnet.tagger.Sentence unitSentence = createUnitSentence(tokenList, aJCas, metaList);

			LOGGER.debug("process() - original sentence: " + sentence.getCoveredText());
			StringBuffer unitS = new StringBuffer();
			for (Unit unit : unitSentence.getUnits()) {
				unitS.append(unit.getRep() + " ");
			}
			LOGGER.debug("process() - sentence for prediction: " + unitSentence.toString());

			// predict with JNET
			try {
				tagger.predict(unitSentence, showSegmentConf);
			} catch (JNETException e) {
				LOGGER.error("process() - predicting with JNET failed: " + e.getMessage());
				throw new AnalysisEngineProcessException();
			}

			// remove duplicated tokens which might occur when abbreviation expansion enabled
			if (expandAbbr) {
				unitSentence = removeDuplicatedTokens(unitSentence);
			}
			LOGGER.debug("process() - sentence with labels: " + unitSentence.toString());

			// write predicted labels to CAS
			writeToCAS(unitSentence, aJCas);

		}

		// now do consistency preservation over whole document
		if (consistPreservation) {
			LOGGER.debug("process() - running consistency preservation");
			ConsistencyPreservation.doStringBased(aJCas, entityMentionTypes);
			//ConsistencyPreservation.doAbbreviationBased(aJCas, entityMentionTypes);
		}
	}

	/**
	 * removes duplicate tokens in a unit sentence (i.e., tokens having the same offset position).
	 * This is necessary if abbreviations in sentence were expanded for prediction. Then,
	 * afterwards, this method needs to be called before writing the prediction into the CAS. When
	 * tokens within abbreviation long form differ in their prediction, the outside label is assumed
	 * for the abbreviation!
	 */
	protected de.julielab.jnet.tagger.Sentence removeDuplicatedTokens(de.julielab.jnet.tagger.Sentence unitSentence) {
		de.julielab.jnet.tagger.Sentence newUnitSentence = new de.julielab.jnet.tagger.Sentence();
		String lastPos = null;
		Unit lastUnit = null;
		TreeSet<String> lastLabels = new TreeSet<String>();
		for (int k = 0; k < unitSentence.getUnits().size(); k++) {
			Unit unit = unitSentence.get(k);
			lastLabels.add(unit.getLabel());
			String currPos = unit.begin + "@" + unit.end;
			if (lastPos != null && (lastPos.equals(currPos))) {
				lastLabels.add(unit.getLabel());
				if (lastLabels.size() > 1) {
					lastUnit.setLabel(OUTSIDE_LABEL);
					// JNET accordingly!
				}
			} else {
				// write unit and probably change label of previous unit
				newUnitSentence.add(unit);
				lastLabels = new TreeSet<String>();
				lastLabels.add(unit.getLabel());
			}
			lastPos = currPos;
			lastUnit = unit;
		}
		return newUnitSentence;
	}

	/**
	 * Takes all info about meta data and generates the corresponding unit sequence represented by a
	 * Sentence object. Abbreviation is expanded when specified in descriptor. Only abbreviations
	 * which span over single tokens can be interpreted here. Other case (which is very rare and
	 * thus probably not relevant) is ignored!
	 * 
	 * @param tokenList
	 *            a list of Token objects of the current sentence
	 * @param JCas
	 *            the CAS we are working on
	 * @param metaList
	 *            a Arraylist of meta-info HashMaps which specify the meta information of the
	 *            respective token
	 * @return an array of two sequences of units containing all available meta data for the
	 *         corresponding tokens. In the first sequence, abbreviations are expanded to their
	 *         fullform. In the second sequence, the tokens are of their original form.
	 */
	protected de.julielab.jnet.tagger.Sentence createUnitSentence(List<Annotation> tokenList, JCas JCas,
			List<HashMap<String, String>> metaList) {

		de.julielab.jnet.tagger.Sentence unitSentence = new de.julielab.jnet.tagger.Sentence();
		//TODO: make abbreviation functionality independent of type system
		//ArrayList<Abbreviation> abbreviationList = getAbbreviationList(tokenList, JCas);

		for (int i = 0; i < tokenList.size(); i++) {
			Annotation token = tokenList.get(i);
			HashMap<String, String> metas = metaList.get(i);
			//TODO: make abbreviation functionality independent of type system
			//Abbreviation abbreviation = abbreviationList.get(i);
			String tokenRepresentation = new String();
			
			if(!this.ignoreTokenText){
				
				tokenRepresentation = token.getCoveredText();
			}
			else{
				
				tokenRepresentation = "_";
			}

			// no abbrevs were expanded here
			Unit unit = new de.julielab.jnet.tagger.Unit(token.getBegin(), token.getEnd(), tokenRepresentation,
					"", metas);
			unitSentence.add(unit);
			//TODO: make abbreviation functionality independent of type system
			// for abbreviation expansion, if there is an abbreviation on the current token
			/*			if (expandAbbr == true && abbreviation != null) {
				if (abbreviation.getDefinedHere()) {
					// when abbreviation is defined here: ignore this token
					tokenRepresentation = null;
				} else {
					// abbreviation only used here: replace token representation by full form
					tokenRepresentation = abbreviation.getTextReference().getCoveredText();
				}
			}

			// now make JNET Unit object for this token and add to Sentence
			if (tokenRepresentation != null) {
				if (tokenRepresentation.equals(token.getCoveredText())) {
					// no abbrevs were expanded here
					Unit unit = new de.julielab.jnet.tagger.Unit(token.getBegin(), token.getEnd(), tokenRepresentation,
									"", metas);
					unitSentence.add(unit);
				} else {
					//TODO: make abbreviation functionality independent of type system
					// abbrev was expanded, so we probably need to make more than one units
					ArrayList<Annotation> abbrevTokens = new ArrayList<Annotation>();
					try {

						abbrevTokens = (ArrayList) UIMAUtils.getAnnotations(JCas, 
																abbreviation.getTextReference(), 
																(AnnotationTools.getAnnotationByClassName(JCas, this.token_type)).getClass());
					}
					catch (SecurityException e) {

						e.printStackTrace();
					}
					catch (IllegalArgumentException e) {

						e.printStackTrace();
					}
					catch (ClassNotFoundException e) {

						e.printStackTrace();
					}
					catch (NoSuchMethodException e) {

						e.printStackTrace();
					}
					catch (InstantiationException e) {

						e.printStackTrace();
					}
					catch (IllegalAccessException e) {

						e.printStackTrace();
					}
					catch (InvocationTargetException e) {

						e.printStackTrace();
					}

					if (abbreviation.getTextReference().getCoveredText().length() > 0 && abbrevTokens.size() == 0) {
						// white space tokenization when no tokens found on abbreviation full form,
						// which
						// typically is because full form didn't start/end on token boundaries
						StringTokenizer st = new StringTokenizer(tokenRepresentation);
						while (st.hasMoreTokens()) {
							String fullformToken = st.nextToken();
							Unit unit = new de.julielab.jnet.tagger.Unit(token.getBegin(), token.getEnd(),
											fullformToken, "", metas);
							unitSentence.add(unit);
						}
					} else {
						// tokens within full form
						for (Annotation abbrevToken : abbrevTokens) {
							Unit unit = new de.julielab.jnet.tagger.Unit(token.getBegin(), token.getEnd(), abbrevToken
											.getCoveredText(), "", metas);
							unitSentence.add(unit);
						}
					}
				}
			}*/
		}

		// clean-up: remove tokens with consecutive brackets
		if (expandAbbr) {
			unitSentence = removeConsecutiveBrackets(unitSentence);
		}
		
		return unitSentence;
	}

	/**
	 * remove consecutive (opening and closing) brackets from unit sentence. This is necessary after
	 * an abbreviation that is introduced is removed.
	 * 
	 * @param unitSentence
	 *            the original unit sentence to be modified
	 * @return
	 */
	private de.julielab.jnet.tagger.Sentence removeConsecutiveBrackets(de.julielab.jnet.tagger.Sentence unitSentence) {
		de.julielab.jnet.tagger.Sentence finalUnitSentence = new de.julielab.jnet.tagger.Sentence();
		for (int i = 0; i < unitSentence.getUnits().size(); i++) {
			Unit currentUnit = unitSentence.getUnits().get(i);
			if ((i + 1) < unitSentence.getUnits().size()) {
				Unit nextUnit = unitSentence.getUnits().get(i + 1);
				if ((currentUnit.getRep().equals("(") && nextUnit.getRep().equals(")"))
						|| (currentUnit.getRep().equals("[")) && nextUnit.getRep().equals("]")) {
					// if this unit is a bracket and next unit too -> ignore this and next unit
					i = i + 1;
					continue;
				}
			}
			finalUnitSentence.add(currentUnit);
		}
		return finalUnitSentence;
	}

	/**
	 * build an arraylist of abbreviation objects, one for each token. If there is no abbreviation
	 * on current token, null is added to the list.
	 * 
	 * @param tokenList
	 * @param JCas
	 * @return
	 */
	//TODO: make abbreviation functionality independent of type system
	/*
	private ArrayList<Abbreviation> getAbbreviationList(ArrayList<Annotation> tokenList, JCas JCas) {
		ArrayList<Abbreviation> abbreviationList = new ArrayList<Abbreviation>();

		for (Annotation token : tokenList) {
			ArrayList<Abbreviation> abbreviations = (ArrayList<Abbreviation>) UIMAUtils.getAnnotations(JCas, token, (new Abbreviation(JCas, 0, 0)).getClass());
			if (abbreviations != null && abbreviations.size() > 0) {
				abbreviationList.add(abbreviations.get(0));
			} else {
				abbreviationList.add(null);
			}
		}
		return abbreviationList;
	}
	 */
	/**
	 * create an ArrayList of meta-info HashMaps, e.g., one such HashMap for each token which was
	 * given as input.
	 * 
	 * @param tokenList
	 *            the tokens for which we want meta infos
	 * @return
	 */
	private List<HashMap<String, String>> getMetaList(List<Annotation> tokenList) {
		List<HashMap<String, String>> metaList = new ArrayList<HashMap<String, String>>();
		Interval[] metaAnnotationValues = new Interval[activatedMetas.size()];
		
		for (int i = 0; i < metaAnnotationValues.length; i++) {
			metaAnnotationValues[i] = null;
		}
		
		// add meta info for each token to list
		for (AnnotationFS token : tokenList){
			
			metaList.add(getMetas(token, metaAnnotationValues));
		}
		
		return metaList;
	}

	/**
	 * Extracts the meta data for a token out of the annotationIterators.
	 * 
	 * @param tokenIter -
	 *            iterator over token annotations
	 * @param sentEnd -
	 *            end of the currently examined sentence
	 * @param annotationIterators -
	 *            an ArrayList of iterators over all meta datas
	 * @param activatedMetas -
	 *            an ArrayList containing the names of all activated meta datas
	 * @param valueMethods -
	 *            An ArrayList of method names. The corresponding methods serve to obtain the
	 *            annotation value of an annotation object.
	 * @param featureConfig -
	 *            a Properties object that represents the feature configuration of the used model
	 * @return metaInfos - a HashMap of pairs (<name of meta info> , <meta info>), e.g. (pos, NN)
	 *         whereas the meta info name is found within the feature configuration
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private HashMap<String, String> getMetas(AnnotationFS token, Interval[] metaAnnotationValues){

		HashMap<String, String> metaInfos = new HashMap<String, String>();

		// hack for easy JUnit testing without all the meta-infos
		if(this.featureConfig == null){
			
			return metaInfos;
		}

		try{

			int i = 0;
			
			for(String activatedMeta : this.activatedMetas){
				
				if(this.annotationIterators.get(activatedMeta).hasNext() && metaAnnotationValues[i] == null){
					
					Annotation ann = (Annotation) this.annotationIterators.get(activatedMeta).next();
					String valueMethodName = this.valueMethods.get(activatedMeta);
					Method valueMethod = ann.getClass().getMethod(valueMethodName);
					
					metaAnnotationValues[i] = new Interval(ann.getBegin(), ann.getEnd(), "" + valueMethod.invoke(ann, (Object[]) null));
				}

				if(metaAnnotationValues[i] != null && metaAnnotationValues[i].isIn(token.getBegin(), token.getEnd())){
					
					String metaName = this.featureConfig.getProperty(activatedMeta + "_feat_unit");
					
					if (this.featureConfig.getProperty(activatedMeta + "_begin_flag").equals("true") && metaAnnotationValues[i].getBegin() == token.getBegin()){
						metaInfos.put(metaName, "B_" + metaAnnotationValues[i].getAnnotation());
					}
					else{
						
						metaInfos.put(metaName, metaAnnotationValues[i].getAnnotation());
					}
				}
				
				i++;
			}
		}
		catch(Exception e){
			
			LOGGER.warn("getMetas() - failed getting meta information for current token. No metas used!");
			LOGGER.warn("Exception message: " + e.getMessage());
			metaInfos = new HashMap<String, String>();
		}

		return metaInfos;
	}

	/**
	 * creates the respective uima annotations from JNET's predictions. Therefore, we loop over
	 * JNET's Sentence objects which contain predictions/labels for each Unit (i.e., for each
	 * token).
	 * 
	 * @param unitSentence
	 *            the current Sentence object
	 * @param aJCas
	 *            the cas to write the annotation to
	 */
	public void writeToCAS(de.julielab.jnet.tagger.Sentence unitSentence, JCas aJCas) {
		String lastLabel = OUTSIDE_LABEL;
		int lastStart = 0;
		int lastEnd = 0;
		double conf = -1;
		double lastConf = -1;

		de.julielab.jnet.tagger.Unit unit = null;
		for (int i = 0; i < unitSentence.size(); i++) {
			unit = unitSentence.get(i);
			String label = unit.getLabel();
			conf = unit.getConfidence();

			if (lastLabel.equals(OUTSIDE_LABEL) && !label.equals(OUTSIDE_LABEL)) {
				// new entity starts
				lastStart = unit.begin;
			} else if ((!lastLabel.equals(OUTSIDE_LABEL) && !label.equals(OUTSIDE_LABEL) && !lastLabel.equals(label))
					|| (!lastLabel.equals(OUTSIDE_LABEL) && label.equals(OUTSIDE_LABEL))) {
				// entity is finished, add annotation to CAS
				addAnnotation(aJCas, lastStart, lastEnd, lastLabel, lastConf);
				lastStart = unit.begin;
			}

			lastLabel = label;
			lastEnd = unit.end;
			lastConf = conf;

			if (i == unitSentence.size() - 1) {
				// last unit handled separately, add annotation to CAS
				if (!label.equals(OUTSIDE_LABEL)) {
					lastEnd = unit.end;
					addAnnotation(aJCas, lastStart, lastEnd, lastLabel, lastConf);

				}
			}
		}
	}

	/**
	 * Create annotation CAS. The label predicted by JNET is written to specificType.
	 * 
	 * @param aJCas
	 *            the cas to write the annotation to
	 * @param start
	 *            begin offset of annotation
	 * @param end
	 *            end offset of annotation
	 * @param label
	 *            label for this entity mention
	 * @param confidence
	 *            certainty of JNET on this label
	 */
	private void addAnnotation(JCas aJCas, int start, int end, String label, double confidence) {

		// check against negative list whether this annotation should be really added
		String coveredText = aJCas.getDocumentText().substring(start, end);
		if (negativeList != null && negativeList.contains(coveredText, label)) {
			LOGGER.debug("addAnnotation() - ignoring current entity mention as contained in negativeList");
			return; // ignore this entity mention
		}

		// create EntityMention object
		Annotation entityMention = null;
		String entityType;

		if ((entityType = (String) entityMap.get(label)) != null) {
			// if the predicted label is in the entityTypes map -> make a new EntityMention

			try {

				//entity = (EntityMention) AnnotationTools.getAnnotationByClassName(aJCas, entityType);
				//entity = (EntityMention) aJCas.getAnnotationIndex(aJCas.getTypeSystem().getType(entityType)).iterator().next();
				entityMention = (Annotation) aJCas.getCas().createAnnotation(aJCas.getTypeSystem().getType(entityType), start, end);

				if(this.TextualRepresentationFeatureName != null){

					//entityMention.setTextualRepresentation(aJCas.getDocumentText().substring(start, end));

					Feature textualRepresentationFeature = entityMention.getType().getFeatureByBaseName(this.TextualRepresentationFeatureName);
					entityMention.setFeatureValueFromString(textualRepresentationFeature, aJCas.getDocumentText().substring(start, end));
				}

				if(this.SpecificTypeFeatureName != null){

					//entityMention.setSpecificType(label);

					Feature specificTypeFeature = entityMention.getType().getFeatureByBaseName(this.SpecificTypeFeatureName);
					entityMention.setFeatureValueFromString(specificTypeFeature, label);
				}

				if(this.ComponentIDFeatureName != null){

					//entityMention.setComponentId(COMPONENT_ID);

					Feature componentIdFeature = entityMention.getType().getFeatureByBaseName(this.ComponentIDFeatureName);
					entityMention.setFeatureValueFromString(componentIdFeature, COMPONENT_ID);
				}				

				if(showSegmentConf && this.ConfidenceFeatureName != null){

					//entityMention.setConfidence(confidence + "");

					Feature confidenceFeature = entityMention.getType().getFeatureByBaseName(this.ConfidenceFeatureName);
					entityMention.setFeatureValueFromString(confidenceFeature, confidence + "");

				}

				entityMention.addToIndexes();

			} 
			catch (Exception e){
				LOGGER.error("addAnnotation() - could not generate new EntityMention", e);
			}
		} 
		else{
			LOGGER.debug("addAnnotation() - ommitted entity mention for label: " + label);
		}

	}
}
