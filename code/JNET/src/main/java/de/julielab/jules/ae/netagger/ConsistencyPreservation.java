/** 
 * ConsistencyPreservation.java
 * 
 * Copyright (c) 2008, JULIE Lab. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0 
 *
 * Author: tomanek
 * 
 * Current version: 2.2.1
 * Since version:   2.2
 *
 * Helper functions for consistency preservation 
 **/

package de.julielab.jules.ae.netagger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JFSIndexRepository;
import org.apache.uima.jcas.tcas.Annotation;
import org.uimafit.util.JCasUtil;

import de.julielab.jules.utility.AnnotationTools;

public class ConsistencyPreservation {

	private static final String COMPONENT_ID = "de.julielab.jules.ae.netagger.ConsistencyPreservation";
	private static final Logger LOGGER = Logger.getLogger(ConsistencyPreservation.class);

	/**
	 * this method checks whether the full form (at the position where an abbreviation was
	 * introduced) of an abbreviation is labeled as an entity. If so, and the abbreviation was not
	 * labeled as an entity, the entity label is copied to the abbreviation. As only the full form
	 * where the abbreviation was introduced is considered, this method should be run AFTER e.g.
	 * doStringBased() which makes sure that all Strings get the same entity annotation.
	 * 
	 * @param aJCas
	 * @param entityMentionClassnames
	 *            the entity mention class names to be considered
	 * @throws AnalysisEngineProcessException
	 */
	/*public static void doAbbreviationBased(JCas aJCas, TreeSet<String> entityMentionClassnames)
					throws AnalysisEngineProcessException {

		//TODO needs to be checked for performance
		
		// make a set of Annotation objects for entity class names to be considered
		TreeSet<EntityMention> entityMentionTypes = null;
		try {
			entityMentionTypes = new TreeSet<EntityMention>();
			for (String className : entityMentionClassnames) {
				entityMentionTypes.add((EntityMention) AnnotationTools.getAnnotationByClassName(aJCas, className));
			}
		} catch (Exception e) {
			LOGGER
							.error("doAbbreviationBased() - could not get Annotation objects for specified entity mention class names!");
			throw new AnalysisEngineProcessException();
		}

		JFSIndexRepository indexes = aJCas.getJFSIndexRepository();
		Iterator abbrevIter = indexes.getAnnotationIndex(Abbreviation.type).iterator();

		// loop over all abbreviations
		while (abbrevIter.hasNext()) {
			Abbreviation abbrev = (Abbreviation)abbrevIter.next();
			Annotation fullFormAnnotation = abbrev.getTextReference();
			LOGGER.debug("doAbbreviationBased() - checking abbreviation: " + abbrev.getCoveredText());

			ArrayList<EntityMention> mentionList = new ArrayList<EntityMention>();

			// check whether abbreviation was identified as an entity mention of interest
			for (EntityMention mention : entityMentionTypes) {
				mentionList.addAll((ArrayList<EntityMention>) UIMAUtils.getAnnotations(aJCas, abbrev, mention
								.getClass()));
			}
			if (mentionList == null || mentionList.size() == 0) {
				// if the abbreviation has no entity annotation of the types of interest
				LOGGER.debug("doAbbreviationBased() -  no entity mentions of interest found on this abbreviation");
				

				ArrayList<EntityMention> fullFormMentionList = new ArrayList<EntityMention>();
				for (EntityMention mention : entityMentionTypes) {
					// check whether respective full form does have an entity annotation of interest
					fullFormMentionList.addAll((ArrayList<EntityMention>) UIMAUtils.getAnnotations(aJCas,
									fullFormAnnotation, mention.getClass()));
				}

				if (fullFormMentionList != null && fullFormMentionList.size() > 0) {
					// if we found an entity mention on the full form, add first entity mention to
					// abbreviation
					EntityMention refEntityMention = fullFormMentionList.get(0);
					LOGGER.debug("doAbbreviationBased() -  but found entity mention on full form");
					LOGGER.info ("doAbbreviationBased() -  adding annotation to unlabeled entity mention");
					try {
						EntityMention newEntityMention = (EntityMention) AnnotationTools.getAnnotationByClassName(
										aJCas, refEntityMention.getClass().getName());
						newEntityMention.setBegin(abbrev.getBegin());
						newEntityMention.setEnd(abbrev.getEnd());
						newEntityMention.setSpecificType(refEntityMention.getSpecificType());
						newEntityMention.setResourceEntryList(refEntityMention.getResourceEntryList());
						newEntityMention.setConfidence(refEntityMention.getConfidence());
						newEntityMention.setTextualRepresentation(abbrev.getCoveredText());
						newEntityMention.setComponentId(COMPONENT_ID + " Abbrev");
						newEntityMention.addToIndexes();
					} catch (Exception e) {
						LOGGER.error("doAbbreviationBased() - could not get create new entity mention annotation: "
										+ refEntityMention.getClass().getName());
						throw new AnalysisEngineProcessException();
					}
				}
			} else {
				// if there are interesting annotations on the abbreviation, check 
				// whether full form (where introduced) has the same annotation
				
				if (mentionList.size()>0) {
					LOGGER.debug("doAbbreviationBased() -  abbreviation has entity mentions of interest");
					ArrayList<EntityMention> fullFormMentionList = new ArrayList<EntityMention>();
					for (EntityMention mention : entityMentionTypes) {
						// check whether respective full form does have an entity annotation of interest
						fullFormMentionList.addAll((ArrayList<EntityMention>) UIMAUtils.getAnnotations(aJCas,
										fullFormAnnotation, mention.getClass()));
					}

					if (fullFormMentionList == null || fullFormMentionList.size() == 0) {
						// if full form has none, add one
						EntityMention refEntityMention = mentionList.get(0);
						LOGGER.debug("doAbbreviationBased() -  but reference full form has no entity mentions of interest");
						LOGGER.info ("doAbbreviationBased() -  adding annotation to unlabeled entity mention");
						try {
							EntityMention newEntityMention = (EntityMention) AnnotationTools.getAnnotationByClassName(
											aJCas, refEntityMention.getClass().getName());
							newEntityMention.setBegin(fullFormAnnotation.getBegin());
							newEntityMention.setEnd(fullFormAnnotation.getEnd());
							newEntityMention.setSpecificType(refEntityMention.getSpecificType());
							newEntityMention.setResourceEntryList(refEntityMention.getResourceEntryList());
							newEntityMention.setConfidence(refEntityMention.getConfidence());
							newEntityMention.setTextualRepresentation(abbrev.getCoveredText());
							newEntityMention.setComponentId(COMPONENT_ID + " Abbrev");
							newEntityMention.addToIndexes();
						} catch (Exception e) {
							LOGGER.error("doAbbreviationBased() - could not get create new entity mention annotation: "
											+ refEntityMention.getClass().getName());
							throw new AnalysisEngineProcessException();
						}
					}
					
				}
			}

		}
	}*/

	public static void doStringBased(JCas aJCas, TreeSet<String> entityMentionClassnames) throws AnalysisEngineProcessException {
		//TODO needs to be checked for performance
		
		String text = aJCas.getDocumentText();

		for (String entityMentionClassname : entityMentionClassnames) {
			// loop over all entity types to be considered

			LOGGER.debug("doStringBased() - checking consistency for type: " + entityMentionClassname);
			HashMap<String, Annotation> entityMap = new HashMap<String, Annotation>();
			JFSIndexRepository indexes = aJCas.getJFSIndexRepository();

			try {
				//EntityMention myEntity = (EntityMention) AnnotationTools.getAnnotationByClassName(aJCas, entityMentionClassname);
				Annotation myEntity = (Annotation) aJCas.getCas().createAnnotation(aJCas.getTypeSystem().getType(entityMentionClassname), 0, 0);

				// loop over all entity annotations in document and put them in hashmap
				LOGGER.debug("doStringBased() - building entity map");
				Iterator entityIter = indexes.getAnnotationIndex(myEntity.getTypeIndexID()).iterator();
				
				while (entityIter.hasNext()) {
					Annotation entity = (Annotation)entityIter.next();
					entityMap.put(entity.getCoveredText(), entity);
				}

				// now search for strings not detected as this kind of entity
				LOGGER.debug("doStringBased() - searching for missed entities...");
				for (Iterator<String> iter = entityMap.keySet().iterator(); iter.hasNext();) {
					String entityString = iter.next();
					Annotation entity = entityMap.get(entityString);

					LOGGER.debug("doStringBased() - checking entity string: " + entityString);

					int pos = 0;
					int length = 0;
					while ((pos = text.indexOf(entityString, (pos + length))) > -1) {
						// for each position where we have found this entity string
						LOGGER.debug("doStringBased() - found string at pos: " + pos);

						// check whether there is already an annotation of this type
						//Annotation refEntity = (Annotation) AnnotationTools.getOverlappingAnnotation(aJCas, entityMentionClassname, pos, pos + entityString.length());
						Annotation refEntity = null;
						
						/*List<Annotation> coveredAnnotations = JCasUtil.selectCovered(aJCas, (Class<Annotation>)aJCas.getTypeSystem().getType(entityMentionClassname).getClass(), 
																			pos, pos + entityString.length());*/
				
						List<Annotation> coveredAnnotations = JCasUtil.selectCovered(aJCas, (Class<Annotation>) myEntity.getClass(), pos, pos + entityString.length());
						
						Iterator<Annotation> coveredAnnotationsIterator = coveredAnnotations.iterator();
						
						while(coveredAnnotationsIterator.hasNext()){
							
							Annotation annotation = (Annotation) coveredAnnotationsIterator.next();
							int a_begin = annotation.getBegin();
							int a_end = annotation.getEnd();
							
							if(a_begin == pos && a_end == (pos + entityString.length())){
								
								refEntity = annotation;
								break;
							}
						}

						if(refEntity == null){
							// if there is no annotation of same type on this text span yet...
							
							LOGGER.info("doStringBased() - adding annotation to unlabeled entity mention");
							//refEntity = (Annotation) AnnotationTools.getAnnotationByClassName(aJCas, entityMentionClassname);

							refEntity = (Annotation) entity.clone();
							refEntity.setBegin(pos);
							refEntity.setEnd(pos + entityString.length());
							
							/*refEntity = (Annotation) aJCas.getCas().createAnnotation(aJCas.getTypeSystem().getType(entityMentionClassname), 0, 0);
							refEntity.setBegin(pos);
							refEntity.setEnd(pos + entityString.length());
							
							refEntity.setSpecificType(entity.getSpecificType());
							refEntity.setResourceEntryList(entity.getResourceEntryList());
							refEntity.setConfidence(entity.getConfidence());
							refEntity.setTextualRepresentation(entity.getTextualRepresentation());
							refEntity.setComponentId(COMPONENT_ID + " String");*/
							
							refEntity.addToIndexes();
						}
						else{
							
							LOGGER.debug("doStringBased() - there is already an entity!");
						}

						length = entityString.length();
					}
				}
			}
			catch (Exception e) {
				LOGGER.error("doStringBased() - exception occured: " + e.getMessage());
				throw new AnalysisEngineProcessException();
			}
		}
	}
}
