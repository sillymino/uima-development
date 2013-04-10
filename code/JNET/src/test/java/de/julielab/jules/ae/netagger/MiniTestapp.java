/** 
 * MiniTestapp.java
 * 
 * Copyright (c) 2007, JULIE Lab. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0 
 *
 * Author: faessler
 * 
 * Current version: 2.2
 * Since version:   1.0
 *
 * Creation date: 09.08.2007 
 * 
 * A small UIMA-Pipeline for better testing purposes.
 * A result XMI is also written. Can be viewed e.g. with UIMA's annotationViewer.
 **/

/**
 * 
 */
package de.julielab.jules.ae.netagger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JFSIndexRepository;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.CasCreationUtils;
import org.apache.uima.util.XMLInputSource;
import org.apache.uima.util.XMLSerializer;
import org.xml.sax.SAXException;


public class MiniTestapp {

	//private static final Logger LOGGER = Logger.getLogger(EntityAnnotatorTest.class);

	private static final String TEST_XMI_IN = "src/test/resources/17088488.xmi";
	
	private static final String TEST_XMI_OUT= "src/test/resources/miniapp_out.xmi";

	private static final String ANNOTATOR_DESC = "src/test/resources/EntityAnnotatorTest.xml";

	public static void main(String args[]) throws Exception {

		// reading XMI file
		File filename = new File(TEST_XMI_IN);

		CAS cas = CasCreationUtils.createCas(UIMAFramework.getXMLParser()
				.parseAnalysisEngineDescription(
								new XMLInputSource(ANNOTATOR_DESC)));
		initCas(cas, filename);

		JCas aJCas = cas.getJCas();

		JFSIndexRepository indexes = aJCas.getJFSIndexRepository();
		//Iterator sentenceIter = indexes.getAnnotationIndex(Sentence.type).iterator();

		// producing entity-ae

		AnalysisEngine entityAE;
		ResourceSpecifier spec;

		spec = UIMAFramework.getXMLParser().parseResourceSpecifier(
				new XMLInputSource(ANNOTATOR_DESC));
		entityAE = UIMAFramework.produceAnalysisEngine(spec);

		// process document
		entityAE.process(cas);
		
		// write results to new XMI
		if ((new File(TEST_XMI_OUT)).isFile()) {
			(new File(TEST_XMI_OUT)).delete();
		}
		writeCasToXMI(cas, TEST_XMI_OUT);
	}

	
	/**
	 * @param cas
	 */
	private static void initCas(CAS cas, File filename) throws Exception {
		//LOGGER.info("Reading test document");
		FileInputStream fis = new FileInputStream(filename.getAbsolutePath());
		XmiCasDeserializer.deserialize(fis, cas);
	}

	/**
	 * writes produced annotations to CAS
	 */
	private static void writeCasToXMI(CAS cas, String filename) throws CASException,
			IOException, SAXException {
		// now write CAS
		FileOutputStream fos = new FileOutputStream(filename);
		XmiCasSerializer ser = new XmiCasSerializer(cas.getTypeSystem());
		XMLSerializer xmlSer = new XMLSerializer(fos, false);
		ser.serialize(cas, xmlSer.getContentHandler());
	}

}
