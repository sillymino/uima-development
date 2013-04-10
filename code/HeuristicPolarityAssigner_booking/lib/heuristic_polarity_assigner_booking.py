#!/usr/bin/python 

"""
@author: David Garcia Narbona
@contact: david.garcian@barcelonamedia.org
@organization: BMCI
@version: 1.0
"""

import string
import sys


# everything in the global namespace is eval'ed during initialization
# by the Pythonnator UIMA annotator
def initialize(annotContext):

  global source
  global debug
  global ac


  ac = annotContext
  
  source = ac.extractValue("SourceFile")
  debug = ac.extractIntegerValue("DebugLevel")
  
  if debug > 0:
  
    print source + " :: initialize with:"
    print "> debug: ", debug



def typeSystemInit(ts):

  global source
  global debug
  global ac

  global CuePolarityType
  global PolarType  
  global OpinionatedUnitType  

  global CueFeat
  global CuePolarityFeat
  global PolarPolarityFeat
  global CueBeginFeat
  global CueEndFeat
  global PathFeat
  global PolarityFeat
  
    
  if debug > 10:
    print source + ": Type sytem init called"


  CueType = ts.getType("org.barcelonamedia.uima.ts.opinion.Cue")
  
  #Checks if CueType is a valid type
  if not CueType.isValid():

    error = source + ": org.barcelonamedia.uima.ts.opinion.Cue is NOT found in type system!"
    ac.logError(error)
    raise Exception, error

  #Recupera la feature begin
  CueBeginFeat = CueType.getFeatureByBaseName("begin")
      
  #Checks if CueFeat is a valid feature
  if not CueBeginFeat.isValid():
    
    error = source + " \'begin\' feature of " + CueType.getName() + ": is NOT found in type system!"
    ac.logError(error)
    raise Exception, error

  #Recupera la feature polarity
  CueEndFeat = CueType.getFeatureByBaseName("end")
      
  #Checks if CueFeat is a valid feature
  if not CueEndFeat.isValid():
    
    error = source + " \'end\' feature of " + CueType.getName() + ": is NOT found in type system!"
    ac.logError(error)
    raise Exception, error


  CuePolarityType = ts.getType("org.barcelonamedia.uima.ts.acceso.CuePolarity")
  
  #Checks if CuePolarityType is a valid type
  if not CuePolarityType.isValid():

    error = source + ": org.barcelonamedia.uima.ts.acceso.CuePolarity is NOT found in type system!"
    ac.logError(error)
    raise Exception, error

  #Recupera la feature polarity
  CuePolarityFeat = CuePolarityType.getFeatureByBaseName("polarity")
      
  #Checks if CueFeat is a valid feature
  if not CuePolarityFeat.isValid():
    
    error = source + " \'polarity\' feature of " + CuePolarityType.getName() + ": is NOT found in type system!"
    ac.logError(error)
    raise Exception, error

 
  PolarType = ts.getType("org.barcelonamedia.uima.ts.opinion.Polar")
  
  #Checks if PolarType is a valid type
  if not PolarType.isValid():

    error = source + ": org.barcelonamedia.uima.ts.opinion.Polar is NOT found in type system!"
    ac.logError(error)
    raise Exception, error

  #Recupera la feature polarity
  PolarPolarityFeat = PolarType.getFeatureByBaseName("polarity")
      
  #Checks if PolarPolarityFeat is a valid feature
  if not PolarPolarityFeat.isValid():
    
    error = source + " \'polarity\' feature of " + PolarType.getName() + ": is NOT found in type system!"
    ac.logError(error)
    raise Exception, error


  OpinionatedUnitType = ts.getType("org.barcelonamedia.uima.ts.acceso.OpinionatedUnit")
  
  #Checks if OpinionatedUnitType is a valid type
  if not OpinionatedUnitType.isValid():

    error = source + ": org.barcelonamedia.uima.ts.acceso.OpinionatedUnit is NOT found in type system!"
    ac.logError(error)
    raise Exception, error

  #Recupera la feature Cue
  CueFeat = OpinionatedUnitType.getFeatureByBaseName("Cue")
      
  #Checks if CueFeat is a valid feature
  if not CueFeat.isValid():
    
    error = source + " \'Cue\' feature of " + OpinionatedUnitType.getName() + ": is NOT found in type system!"
    ac.logError(error)
    raise Exception, error

  #Recupera la feature PolarityFeat
  PolarityFeat = OpinionatedUnitType.getFeatureByBaseName("polarity")
      
  #Checks if PolarityFeat is a valid feature
  if not PolarityFeat.isValid():
    
    error = source + " \'polarity\' feature of " + OpinionatedUnitType.getName() + ": is NOT found in type system!"
    ac.logError(error)
    raise Exception, error



# the process method is passed two parameters, the CAS and
# the ResultsSpecification
def process(tcas, rs):
  
  global source
  global debug
  global ac

  global CuePolarityType
  global PolarType  
  global OpinionatedUnitType
  
  global CuePolarityFeat
  global PolarPolarityFeat
  global PolarityFeat
  
  
  if debug > 10:
  
    print source + ": This is a process function"
    ac.logMessage("process called")
 
    
  index = tcas.getIndexRepository()
  

  opinionatedUnit_iterator = tcas.getAnnotationIndex(OpinionatedUnitType).iterator()

  while opinionatedUnit_iterator.isValid():
  
    opinionatedUnit_annotation = opinionatedUnit_iterator.get()
 
    cuePolarityAnnotation = getCuePolarityAnnFromOpinionatedUnit(tcas, CuePolarityType, opinionatedUnit_annotation)
               
    if cuePolarityAnnotation != None:
                 
      polarity = cuePolarityAnnotation.getStringValue(CuePolarityFeat)
                   
    else:
                     
      polar_annotations = getPolarAnnsFromOpinionatedUnitSpan(tcas, PolarType, opinionatedUnit_annotation)
      polarity = 0
                 
      if polar_annotations:                       
                   
        for polar_ann in polar_annotations:
                         
          polarity += int(polar_ann.getStringValue(PolarPolarityFeat))
              
        polarity = polarity/len(polar_annotations)
       
    opinionatedUnit_annotation.setStringValue(PolarityFeat, str(polarity))

    opinionatedUnit_iterator.moveToNext()



#Gets the CuePolarity annotation from opinionatedUnit_annotation
def getCuePolarityAnnFromOpinionatedUnit(tcas, CuePolarityType, opinionatedUnit_annotation):
  
  global CueFeat
  global CueBeginFeat
  global CueEndFeat
  
  cueAnn = opinionatedUnit_annotation.getFSValue(CueFeat)
  
  cue_begin = cueAnn.getIntValue(CueBeginFeat)
  cue_end = cueAnn.getIntValue(CueEndFeat)
  
  #Iterador sobre las anotaciones de tipo CuePolarityType
  cuePolarity_iterator = tcas.getAnnotationIndex(CuePolarityType).iterator()
            
  while cuePolarity_iterator.isValid():
    
    cuePolarity_annotation = cuePolarity_iterator.get()
       
    if (cuePolarity_annotation.getBeginPosition() == cue_begin) and (cuePolarity_annotation.getEndPosition() == cue_end):
    
      return cuePolarity_annotation
        
    cuePolarity_iterator.moveToNext()
    
  return None

  

#Gets the Polar annotations covered by opinionatedUnit_annotation
def getPolarAnnsFromOpinionatedUnitSpan(tcas, PolarType, opinionatedUnit_annotation):
  
  begin = opinionatedUnit_annotation.getBeginPosition()
  end = opinionatedUnit_annotation.getEndPosition()
  
  polar_annotations = []
  
  #Iterador sobre las anotaciones de tipo PolarityType
  polar_iterator = tcas.getAnnotationIndex(PolarType).iterator()
            
  while polar_iterator.isValid():
    
    polar_annotation = polar_iterator.get()
       
    if (polar_annotation.getBeginPosition() >= begin) and (polar_annotation.getEndPosition() <= end):
    
      polar_annotations.append(polar_annotation)
        
    polar_iterator.moveToNext()
    
  return polar_annotations
