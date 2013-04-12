#!/usr/bin/python 

"""
@author: David Garcia Narbona
@contact: david.garcian@barcelonamedia.org
@organization: BMCI
@version: 1.0
"""

import re
import math
import string


# everything in the global namespace is eval'ed during initialization
# by the Pythonnator UIMA annotator
def initialize(annotContext):

  global source
  global debug
  global ac
  global new_feature
  global ref_feature
  global features
  
  ac = annotContext
  new_feature = ac.extractStringValue("new_feature")
  ref_feature = ac.extractStringValue("ref_feature")
  
  prev_features = ac.extractStringValue("filtering_features")
  features = prev_features.split('#')
  
  source = ac.extractValue("SourceFile")
  debug = ac.extractIntegerValue("DebugLevel")
  
  if debug > 0:
  
    print source + ": initialize with:"
    print new_feature
    print ref_feature 
    print features  


def typeSystemInit(ts):

  global source
  global debug
  global ac
  
  global new_feature
  global ref_feature
  global features
  global filtering_features
  global PCtype
  global PCfeat
  global PCvalue
  global RefType
  global RefFeat
  
  if debug > 10:
    print source + ": Type sytem init called"
  
  #Example of a defined feature along with its value:
  #org.barcelonamedia.uima.types.NominalAttributeValue:value=Negative
  
  #Retrieves the new annotation type
  new_annotation_name = new_feature.split(":")[0]
  PCtype = ts.getType(new_annotation_name)
  
  #Checks if PCtype is a valid type
  if not PCtype.isValid():

    error = source + ": " + new_annotation_name + " is NOT found in type system!"
    ac.logError(error)
    raise Exception, error
    
  new_feature_name_value = new_feature.split(":")[1]
  PCfeat = PCtype.getFeatureByBaseName(new_feature_name_value.split("=", 1)[0])
  
  #Checks if PCfeat is a valid feature
  if not PCfeat.isValid():
  
    error = source + ": " + new_feature_name_value.split("=", 1)[0] + " feature: is NOT found in type system!"
    ac.logError(error)
    raise Exception, error  
  
  PCvalue = new_feature_name_value.split("=", 1)[1]
  #-----------------------------------------------------------------------------------------------------
  
  #Retrieves the ref annotation type
  ref_annotation_name = ref_feature.split(":")[0]
  RefType = ts.getType(ref_annotation_name)
  
  #Checks if RefType is a valid type
  if not RefType.isValid():          
    
    error = source + ": " + ref_annotation_name + " is NOT found in type system!"
    ac.logError(error)
    raise Exception, error    
  
  ref_feature_name = ref_feature.split(":")[1]
  RefFeat = RefType.getFeatureByBaseName(ref_feature_name)
  
  #Checks if RefFeat is a valid feature
  if not RefFeat.isValid():
  
    error = source + ": " + ref_feature_name + " feature: is NOT found in type system!"
    ac.logError(error)
    raise Exception, error
  #-----------------------------------------------------------------------------------------------------

  filtering_features = []

  for feature in features:
      
    if (len(feature) > 0):
      
      annotation_name = feature.split(":")[0]
    
      feature_name_value = feature.split(":")[1]
      
      feature_name = feature_name_value.split("=", 1)[0]
      feature_value = feature_name_value.split("=", 1)[1]
  
      #Recupera del Type System el tipo especificado en annotation_name
      annotation_type = ts.getType(annotation_name)
  
      #Checks if annotation_type is a valid type
      if not annotation_type.isValid():
          
        error = source + " " + annotation_name + ": is NOT found in type system!"
        ac.logError(error)
        raise Exception, error
      
      #Recupera la feature feature_name
      feat = annotation_type.getFeatureByBaseName(feature_name)
      
      #Checks if RefFeat is a valid feature
      if not feat.isValid():
    
        error = source + " " + feature_name + ": is NOT found in type system!"
        ac.logError(error)
        raise Exception, error
        
      feat_data = {}
      feat_data['type'] = annotation_type
      feat_data['feature'] = feat
      feat_data['feat_value'] = feature_value   
        
      filtering_features.append(feat_data)
  
#
# the process method is passed two parameters, the CAS and
# the ResultsSpecification
def process(tcas, rs):
  
  global source
  global debug
  global ac
  
  global filtering_features
  global PCtype
  global PCfeat
  global PCvalue
  global RefType
  global RefFeat
  
  if debug > 10:
  
    print source + ": This is a process function"
    ac.logMessage("process called")
    
  filtering_feature_matches = False
  index = tcas.getIndexRepository()
  
  for feat in filtering_features:

    #Iterador sobre las anotaciones
    iterator = tcas.getAnnotationIndex(feat['type']).iterator()
    
    while iterator.isValid() and not filtering_feature_matches:
          
      annotation = iterator.get()
            
      #Recupera el valor de la feature de la annotation
      featValue = annotation.getStringValue(feat['feature'])
      
      if re.match(feat['feat_value'], featValue):
                    
        filtering_feature_matches = True
        break
          
      iterator.moveToNext()
      
      
  ref_iterator = tcas.getAnnotationIndex(RefType).iterator()
  
  if ref_iterator.isValid():
    
    ref_annotation = ref_iterator.get()
      
    ref_begin = ref_annotation.getBeginPosition()
    ref_end = ref_annotation.getEndPosition()
        
  else:    
    
    error = source + ": ref_annotation missing!"
    ac.logError(error)
    raise Exception, error
    
  if filtering_feature_matches:
    
    value = ref_annotation.getStringValue(RefFeat)
    
  else:
      
    value = PCvalue
    
  #Crea nueva annotation del tipo chunkVP
  fs = tcas.createAnnotation(PCtype, ref_begin, ref_end)
  fs.setStringValue(PCfeat, value)
            
  #Adds new annotation into CAS
  index.addFS(fs)
  