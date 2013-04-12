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

package org.barcelonamedia.uima;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.zip.GZIPInputStream;

import weka.classifiers.Classifier;

import org.apache.uima.resource.DataResource;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.SharedResourceObject;


/** 
 * @author Philip Ogren
 */

public class ClassifierResourceImpl implements ClassifierResource, SharedResourceObject
{
	Classifier classifier;
	
    public void load(DataResource dr) throws ResourceInitializationException
    {
    	try
    	{
	    	File file = new File(dr.getUri());
	    	InputStream is = new FileInputStream(file);
	    	if(dr.getUri().getRawPath().endsWith(".gz"))
	    	{
                is = new GZIPInputStream(is);
            }
            ObjectInputStream objectInputStream = new ObjectInputStream(is);
            classifier = (Classifier) objectInputStream.readObject();
            objectInputStream.close();
    	}
    	catch(Exception e)
    	{
    		throw new ResourceInitializationException(e);
    	}
    }

    public Classifier getClassifier()
    {
    	return classifier;
    }

}
