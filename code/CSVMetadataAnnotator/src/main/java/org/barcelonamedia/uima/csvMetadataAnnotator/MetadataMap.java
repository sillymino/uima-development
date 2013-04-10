package org.barcelonamedia.uima.csvMetadataAnnotator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

public class MetadataMap extends HashMap<String, List<Metadata>> {

    private static final long serialVersionUID = 1L;
    
    private String filterTypename;
    private String featureName;
    
    
    public String getFilterTypename() {
        return this.filterTypename;
    }

    public void setFilterTypename(String filterTypename) {
        this.filterTypename = filterTypename;
    }
    
    public String getFeatureName() {
        return this.featureName;
    }

    public void setFeatureName(String featureName){
    	
        this.featureName = featureName;
    }

    public MetadataMap(CSVReader csvReader) throws IOException{
    	
        super();
        this.filterTypename = null;
        this.featureName = null;
        
        String typeAndFeature;
        String[] headerRow = csvReader.readNext();
        
        if(headerRow != null && headerRow.length > 0){
        	
            List<String> type_columns = new ArrayList<String>(Arrays.asList(headerRow));
            typeAndFeature = type_columns.remove(0);
            
            this.filterTypename = typeAndFeature.split(":")[0];
            this.featureName = typeAndFeature.split(":")[1];
            
            String[] row;
            
            while((row = csvReader.readNext()) != null){
            	
                List<String> value_list = new ArrayList<String>(Arrays.asList(row));
                
                String key = value_list.remove(0);
                
                List<Metadata> match_list = zip(new ArrayList<String>(type_columns), value_list);
                
                if(this.containsKey(key)){
                	
                	match_list.addAll(this.get(key));
                }
                
                this.put(key, match_list);
            }
        }
    }

    public static List<Metadata> zip(List<String> type_list, List<String> value_list){
        
        if (type_list.isEmpty() || value_list.isEmpty()) {
            return new ArrayList<Metadata>();
            
        }
        else{        	     	
        	
        	List<Metadata> metadatas = new ArrayList<Metadata>();
        	
        	for(int i=0; i<type_list.size(); i++){
        		
        		String annotationType = (String)type_list.get(i).split(":")[0];
        		String featureQualifiedName = (String)type_list.get(i);
        		String featureValue = (String)value_list.get(i);
        		
        		//Si existe añade feature
        		boolean metadataExists = false;
        		Iterator<Metadata> iterador = metadatas.iterator(); 
        		
        		while(iterador.hasNext()){  
        			
        			Metadata metadata = iterador.next();  
        		    
        			if(metadata.getAnnotationType().equals(annotationType)){
        				
        				metadata.getFeaturesQualifiedName().put(featureQualifiedName, featureValue);     
        				metadataExists = true;
        				break;
        			} 
        		}
        		
        		//Si no existe, la crea
        		if(!metadataExists){
        			
        			HashMap<String, String> featuresQualifiedName = new HashMap<String, String>();
        			featuresQualifiedName.put(featureQualifiedName, featureValue);     
        			
        			Metadata metadata = new Metadata(annotationType, featuresQualifiedName);
        			
        			metadatas.add(metadata);
        		}
        	}
        	
            return metadatas;
        }
    }
    
}
