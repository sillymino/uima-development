/*
 * Copyright: (c) 2004-2006 Mayo Foundation for Medical Education and
 * Research (MFMER).  All rights reserved.  MAYO, MAYO CLINIC, and the
 * triple-shield Mayo logo are trademarks and service marks of MFMER.
 *
 * Except as contained in the copyright notice above, the trade names, 
 * trademarks, service marks, or product names of the copyright holder shall
 * not be used in advertising, promotion or otherwise in connection with
 * this Software without prior written authorization of the copyright holder.
 * 
 * Licensed under the Eclipse Public License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 * 		http://www.eclipse.org/legal/epl-v10.html
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.barcelonamedia.uima.consumer.WekaConsumer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instances;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.jcas.JFSIndexRepository;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.ProcessTrace;

import org.barcelonamedia.uima.types.AttributeValue;
import org.barcelonamedia.uima.types.DateAttributeValue;
import org.barcelonamedia.uima.types.NominalAttributeValue;
import org.barcelonamedia.uima.types.NumericAttributeValue;
import org.barcelonamedia.uima.types.StringAttributeValue;

/**
 * @author Philip Ogren
 */

public class ARFFHeaderFileCasConsumer extends CasConsumer_ImplBase
{
	public static final String PARAM_ARFF_HEADER_FILE_NAME = "ArffHeaderFile";
	public static final String PARAM_ARFF_RELATION = "ArffRelation";
	
	File arffHeaderFile;
	String arffRelation;
	
	Set<String> numericAttributes;
	Set<String> dateAttributes;
	Set<String> stringAttributes;
    Map<String, HashSet<String>> nominalAttributes;
    String dateFormat;

	
	public void initialize() throws ResourceInitializationException 
	{
		try
		{
			String arffHeaderFileName = (String) getConfigParameterValue(PARAM_ARFF_HEADER_FILE_NAME);
			arffHeaderFile = new File(arffHeaderFileName);
			if(!arffHeaderFile.exists())
			{
				arffHeaderFile.createNewFile();
				System.out.println("ARFF Headerfile created at " + arffHeaderFile.getPath());
			}
			else
			{
				System.out.println("ARFF Headerfile opened at " + arffHeaderFile.getPath());
			}
			arffRelation = (String) getConfigParameterValue(PARAM_ARFF_RELATION);

		}
		catch(Exception ioe)
		{
			throw new ResourceInitializationException(ioe);
		}
		
		dateFormat = "MM/dd/yyy";
		
		numericAttributes = new HashSet<String>();
        dateAttributes = new HashSet<String>();
        stringAttributes = new HashSet<String>();
		nominalAttributes = new HashMap<String, HashSet<String>>();
	}

	public void processCas(CAS cas) throws ResourceProcessException 
	{
		try 
		{
			JCas jcas;
			jcas = cas.getJCas();
			
		 	JFSIndexRepository indexes = jcas.getJFSIndexRepository();
		 	FSIndex<Annotation> fsIndex = indexes.getAnnotationIndex(AttributeValue.type);
		 	FSIterator<Annotation> attributeIterator = fsIndex.iterator();
		 	while(attributeIterator.hasNext())
		 	{
		 		AttributeValue attributeValue = (AttributeValue) attributeIterator.next();
				String attributeName = attributeValue.getAttributeName();
				if(attributeValue instanceof NumericAttributeValue)
                {
                    numericAttributes.add(attributeName);
                }
                else if (attributeValue instanceof DateAttributeValue)
				{
					dateAttributes.add(attributeName);
				}
                else if (attributeValue instanceof StringAttributeValue)
                {
                    stringAttributes.add(attributeName);
                }
				else if(attributeValue instanceof NominalAttributeValue)
				{
					if(!nominalAttributes.containsKey(attributeName))
					{
						nominalAttributes.put(attributeName, new HashSet<String>());
					}
					String value = ((NominalAttributeValue)attributeValue).getValue();
					((HashSet<String>)nominalAttributes.get(attributeName)).add(value);
				}
			}
		}
		catch(Exception exception)
		{
			throw new ResourceProcessException(exception);
		}
	}

	public void collectionProcessComplete(ProcessTrace arg0) throws ResourceProcessException, IOException 
	{
		int capacity = numericAttributes.size()
                     + dateAttributes.size()
		             + stringAttributes.size()
		             + nominalAttributes.size();
		
		FastVector wekaAttributes = new FastVector(capacity);
		
		Iterator<String> numericAttributesIterator = numericAttributes.iterator();
		while (numericAttributesIterator.hasNext()) 
		{
			String attributeName = (String) numericAttributesIterator.next();
			Attribute attribute = new Attribute(attributeName);
			wekaAttributes.addElement(attribute);
		}
		
        Iterator<String> dateAttributesIterator = dateAttributes.iterator();
        while (dateAttributesIterator.hasNext()) 
        {
            String attributeName = (String) dateAttributesIterator.next();
            Attribute attribute = new Attribute(attributeName, dateFormat);
            wekaAttributes.addElement(attribute);
        }
        
        Iterator<String> stringAttributesIterator = stringAttributes.iterator();
        while (stringAttributesIterator.hasNext()) 
        {
            String attributeName = (String) stringAttributesIterator.next();
            Attribute attribute = new Attribute(attributeName, (FastVector) null);
            wekaAttributes.addElement(attribute);
        }
        
		Iterator<String> nominalAttributesIterator = nominalAttributes.keySet().iterator();
		while (nominalAttributesIterator.hasNext()) 
		{
			String attributeName = (String) nominalAttributesIterator.next();
			Set<String> attributeValues = (Set<String>) nominalAttributes.get(attributeName);
			Iterator<String> valuesIterator = attributeValues.iterator();
			FastVector nominalValues = new FastVector(attributeValues.size());
			while(valuesIterator.hasNext())
			{
				nominalValues.addElement(valuesIterator.next());
			}
			Attribute attribute = new Attribute(attributeName, nominalValues);
			wekaAttributes.addElement(attribute);
		}

		Instances wekaInstances = new Instances(arffRelation, wekaAttributes, 0);
		String arffText = wekaInstances.toString();
		BufferedWriter writer = new BufferedWriter(new FileWriter(arffHeaderFile));
		writer.write(arffText);
		writer.flush();
		writer.close();
		System.out.println("ARFF Headerfile written to "+arffHeaderFile.getPath());
	}
	

}