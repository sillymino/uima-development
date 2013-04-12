package org.barcelonamedia.uima.csvMetadataAnnotator;

import java.util.HashMap;


public class Metadata{

    private String annotationType;
    private HashMap<String, String> featuresQualifiedName;
    
    
    public Metadata(String annotationType, HashMap<String, String> featuresQualifiedName){
    	
        this.annotationType = annotationType;
        this.featuresQualifiedName = featuresQualifiedName;
    }

    public String getAnnotationType(){
    	
        return this.annotationType;
    }

    public void setAnnotationType(String annotationType){
    	
        this.annotationType = annotationType;
    }

    public HashMap<String, String> getFeaturesQualifiedName(){
    	
        return this.featuresQualifiedName;
    }

    public void setFeaturesQualifiedName(HashMap<String, String> featuresQualifiedName){
    	
        this.featuresQualifiedName = featuresQualifiedName;
    }
}
