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

import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.SparseInstance;
import weka.core.converters.ConverterUtils.DataSource;

import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.ProcessTrace;

/**
 * @author Philip Ogren
 * 
 * This class writes out an WEKA ARFF data file based on annotations in the CAS that are of the types specified
 * in the WekaTypeSystem.xml descriptor file.  For information about the ARFF format please visit 
 * http://www.cs.waikato.ac.nz/~ml/weka/arff.html
 * 
 * This class assumes that the header for the ARFF file already exists and can be read in from a 
 * file.  If a header does not yet exist - but the annotations do - please see ARFFHeaderCasConsumer. 
 * When this annotator is initialized a local instance of weka.core.Instances is created using
 * an ARFF header file specified by the parameter ArffDataFile which should be set in the descriptor
 * file for this cas consumer.  The local instance of weka.core.Instances is populated with attributes
 * as processCas is called on each cas.  If an annotation in the CAS corresponds to an AttributeValue type 
 * (from WekaTypeSystem.xml) but is not defined by the ARFF header file, it will be ignored.     
 * Finally, the ARFF file is written out when collectionProcessComplete is called to
 * the file specified by the parameter ArffDataFile which should also be set in the descriptor file.
 * 
 * We have defined an ARFF header file as equivalent to an ARFF data file - only without the data.  Thus
 * this class assumes that the attribute-relations have already been specified and written to a file.  There
 * is an example ARFF header file in examples/Test/test_header.arff and a corresponding example ARFF data file 
 * called test_data.arff.
 * 
 */

public class ARFFDataFileCasConsumer extends CasConsumer_ImplBase{

	Instances wekaInstances;
	File dataFile;
	
	public static final String PARAM_ARFF_HEADER_FILE_NAME = "ArffHeaderFile";
	public static final String PARAM_ARFF_DATA_FILE_NAME = "ArffDataFile";
	

	public void initialize() throws ResourceInitializationException 
	{
		try
		{
			String arffHeaderFileName = (String) getConfigParameterValue(PARAM_ARFF_HEADER_FILE_NAME);
            
            DataSource source = new DataSource(arffHeaderFileName);
            wekaInstances = source.getDataSet();
            System.out.println("Weka Instances successfully instantiated from header file at "+ arffHeaderFileName);
            
			String arffDataFileName = (String) getConfigParameterValue(PARAM_ARFF_DATA_FILE_NAME);
			dataFile = new File(arffDataFileName);
			if(!dataFile.exists())
			{
				dataFile.createNewFile();
				System.out.println("ARFF data file created at "+dataFile.getPath());
			}
			else
			{
				System.out.println("ARFF data file opened at "+dataFile.getPath());
			}
		}
		catch(Exception ioe)
		{
			throw new ResourceInitializationException(ioe);
		}
	}

	public void processCas(CAS cas) throws ResourceProcessException 
	{
		try 
		{
			//SparseInstance wekaInstance = CAS2WekaInstance.toWekaInstance(cas, wekaInstances);
			DenseInstance wekaInstance = CAS2WekaInstance.toWekaInstance(cas, wekaInstances);
	
		 	wekaInstances.add(wekaInstance);
		}
		catch(Exception exception)
		{
			throw new ResourceProcessException(exception);
		}
	}

	public void collectionProcessComplete(ProcessTrace arg0) throws ResourceProcessException, IOException 
	{
		String arffText = wekaInstances.toString();
		BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile));
		writer.write(arffText);
		writer.flush();
		writer.close();
		System.out.println("ARFF data file written to "+dataFile.getPath());
	}

}
