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
  
  global annotationToFilter_name
  global referenceAnnotation_name
  global polarAnnotation_name
  global annotationToFilter_feat_name
  global ref_annotation_feat_name
  global polarAnnotation_feat_name
  
  
  ac = annotContext
  
  source = ac.extractValue("SourceFile")
  debug = ac.extractIntegerValue("DebugLevel")
  
  annotationToFilter_name = "org.barcelonamedia.uima.ts.acceso.TargetPolarity"
  referenceAnnotation_name = "org.barcelonamedia.uima.types.NominalAttributeValue"
  polarAnnotation_name = "org.barcelonamedia.uima.ts.opinion.Polar"
  
  annotationToFilter_feat_name = "polarity"
  ref_annotation_feat_name = "value"
  polarAnnotation_feat_name = "polarity"
  
  if debug > 0:
  
    print source + ": initialize with:"


def typeSystemInit(ts):

  global source
  global debug
  global ac
  
  global annotationToFilter_name
  global referenceAnnotation_name
  global polarAnnotation_name
  global annotationToFilter_feat_name
  global ref_annotation_feat_name
  global polarAnnotation_feat_name
  
  global annotationToFilterType
  global referenceAnnotationType
  global polarAnnotationType
  global annotationToFilter_feat
  global ref_annotation_feat
  global polarAnnotation_feat
  
  
  if debug > 10:
    print source + ": Type sytem init called"

  
  #Retrieves annotationToFilter type
  annotationToFilterType = ts.getType(annotationToFilter_name)
  
  #Checks if annotationToFilter is a valid type
  if not annotationToFilterType.isValid():

    error = source + ": " + annotationToFilter_name + " is NOT found in type system!"
    ac.logError(error)
    raise Exception, error
    
  annotationToFilter_feat = annotationToFilterType.getFeatureByBaseName(annotationToFilter_feat_name)
  
  #Checks if annotationToFilter_feat is a valid feature
  if not annotationToFilter_feat.isValid():
  
    error = source + ": " + annotationToFilter_feat_name + " feature: is NOT found in type system!"
    ac.logError(error)
    raise Exception, error  
  #-----------------------------------------------------------------------------------------------------
  
  #Retrieves the referenceAnnotation type
  referenceAnnotationType = ts.getType(referenceAnnotation_name)
  
  #Checks if referenceAnnotation is a valid type
  if not referenceAnnotationType.isValid():          
    
    error = source + ": " + referenceAnnotation_name + " is NOT found in type system!"
    ac.logError(error)
    raise Exception, error    
  
  ref_annotation_feat = referenceAnnotationType.getFeatureByBaseName(ref_annotation_feat_name)
  
  #Checks if ref_annotation_feat is a valid feature
  if not ref_annotation_feat.isValid():
  
    error = source + ": " + ref_annotation_feat_name + " feature: is NOT found in type system!"
    ac.logError(error)
    raise Exception, error
  #-----------------------------------------------------------------------------------------------------

  #Retrieves the polarAnnotation type
  polarAnnotationType = ts.getType(polarAnnotation_name)
  
  #Checks if referenceAnnotation is a valid type
  if not polarAnnotationType.isValid():          
    
    error = source + ": " + polarAnnotation_name + " is NOT found in type system!"
    ac.logError(error)
    raise Exception, error    
  
  polarAnnotation_feat = polarAnnotationType.getFeatureByBaseName(polarAnnotation_feat_name)
  
  #Checks if polarAnnotation_feat is a valid feature
  if not polarAnnotation_feat.isValid():
  
    error = source + ": " + polarAnnotation_feat_name + " feature: is NOT found in type system!"
    ac.logError(error)
    raise Exception, error

  
#
# the process method is passed two parameters, the CAS and
# the ResultsSpecification
def process(tcas, rs):
  
  global source
  global debug
  global ac
  
  global annotationToFilterType
  global referenceAnnotationType
  global polarAnnotationType
  global annotationToFilter_feat
  global ref_annotation_feat
  global polarAnnotation_feat
  
  if debug > 10:
  
    print source + ": This is a process function"
    ac.logMessage("process called")

  
  ref_iterator = tcas.getAnnotationIndex(referenceAnnotationType).iterator()
    
  while ref_iterator.isValid():

    ref_annotation = ref_iterator.get()
    ref_feat_value = ref_annotation.getStringValue(ref_annotation_feat)
    
    #--------------------------------------------------------------------------------
    polarFlag = False
    polar_iterator = tcas.getAnnotationIndex(polarAnnotationType).iterator()
    
    if polar_iterator.isValid():

        polarFlag = True
    #--------------------------------------------------------------------------------
    
    toFilter_iterator = tcas.getAnnotationIndex(annotationToFilterType).iterator()
    
    while toFilter_iterator.isValid():
        
        toFilter_annotation = toFilter_iterator.get()
        toFilter_feat_value = toFilter_annotation.getStringValue(annotationToFilter_feat)
        
        if not((ref_feat_value.lower()[0:3] == toFilter_feat_value.lower()[0:3]) and polarFlag): #normalize polarity annotations to 'pos','neg','neu' and compare

            toFilter_annotation.setStringValue(annotationToFilter_feat, "Neutral")            
        
        toFilter_iterator.moveToNext()
    
    ref_iterator.moveToNext()
  