#!/usr/bin/python 

"""
@author: David Garcia Narbona
@contact: david.garcian@barcelonamedia.org
@organization: BMCI
@version: 1.0
"""

import depgraph
import re
import math
import string
import my_personal_script


# everything in the global namespace is eval'ed during initialization
# by the Pythonnator UIMA annotator
def initialize(annotContext):

  global source
  global debug
  global ac

  global flag_all_f_features_needed
  global filtering_features_names

  global output_annotation_name
  global output_features_name
  
  global module
  
  
  ac = annotContext
  
  flag_all_f_features_needed = ac.extractIntegerValue("all_filtering_features_needed")
  prev_features = ac.extractStringValue("filtering_features")
  filtering_features_names = prev_features.split('#')
  
  output_annotation_name = ac.extractStringValue("output_annotation_name")
  output_features_name = ac.extractStringValue("output_features_name").split('#')
  
  source = ac.extractValue("SourceFile")
  debug = ac.extractIntegerValue("DebugLevel")
  
  module_name = ac.extractStringValue("provided_script")
  module = __import__(module_name)
  
  if debug > 0:
  
    print source + " :: initialize with:"
    print "> flag_all_f_features_needed: ", flag_all_f_features_needed
    print "> filtering_features_names: "  , filtering_features_names
    print "> output_annotation_name: ", output_annotation_name  
    print "> output_features_name: ", output_features_name
    print "> provided_script: ", module_name    
    print "-------------------------------\n" 



def typeSystemInit(ts):

  global source
  global debug
  global ac
  
  global filtering_features_names
  global filtering_features
  
  global output_annotation_name
  global output_features_name
  
  global SentenceType
  global TokenType
  global DeSRType
  global OutputAnnotationType
  
  global TokenLemmaFeat
  global TokenPOSTagFeat
  global DeSRHeadFeat
  global DeSRLabelFeat
  global TokenPOSTagFeat
  global OuputFeats
  
  
  if debug > 10:
    print source + ": Type sytem init called"
  
  SentenceType = ts.getType("org.barcelonamedia.uima.ts.Sentence")
  
  #Checks if SentenceType is a valid type
  if not SentenceType.isValid():

    error = source + ": org.barcelonamedia.uima.ts.Sentence is NOT found in type system!"
    ac.logError(error)
    raise Exception, error


  TokenType = ts.getType("org.barcelonamedia.uima.ts.Token")
  
  #Checks if TokenType is a valid type
  if not TokenType.isValid():

    error = source + ": org.barcelonamedia.uima.ts.Token is NOT found in type system!"
    ac.logError(error)
    raise Exception, error

  #Recupera la feature lemma
  TokenLemmaFeat = TokenType.getFeatureByBaseName("lemma")
      
  #Checks if TokenLemmaFeat is a valid feature
  if not TokenLemmaFeat.isValid():
    
    error = source + " \'lemma\' feature of " + TokenType.getName() + ": is NOT found in type system!"
    ac.logError(error)
    raise Exception, error

  #Recupera la feature posTag
  TokenPOSTagFeat = TokenType.getFeatureByBaseName("posTag")
      
  #Checks if TokenLemmaFeat is a valid feature
  if not TokenPOSTagFeat.isValid():
    
    error = source + " \'posTag\' feature of " + TokenType.getName() + ": is NOT found in type system!"
    ac.logError(error)
    raise Exception, error


  DeSRType = ts.getType("org.barcelonamedia.uima.ts.DeSR")
  
  #Checks if DeSRType is a valid type
  if not DeSRType.isValid():

    error = source + ": org.barcelonamedia.uima.ts.DeSR is NOT found in type system!"
    ac.logError(error)
    raise Exception, error

  #Recupera la feature head
  DeSRHeadFeat = DeSRType.getFeatureByBaseName("head_index")
      
  #Checks if TokenLemmaFeat is a valid feature
  if not DeSRHeadFeat.isValid():
    
    error = source + " \'head\' feature of " + DeSRType.getName() + ": is NOT found in type system!"
    ac.logError(error)
    raise Exception, error

  #Recupera la feature label
  DeSRLabelFeat = DeSRType.getFeatureByBaseName("label")
      
  #Checks if DeSRLabelFeat is a valid feature
  if not DeSRLabelFeat.isValid():
    
    error = source + " \'label\' feature of " + DeSRType.getName() + ": is NOT found in type system!"
    ac.logError(error)
    raise Exception, error

  OutputAnnotationType = ts.getType(output_annotation_name)
  
  
  #Checks if OutputAnnotationType is a valid type
  if not OutputAnnotationType.isValid():

    error = source + ": " + output_annotation_name + " is NOT found in type system!"
    ac.logError(error)
    raise Exception, error

  OuputFeats = {}
  
  for output_feature_name in output_features_name:
      
    if (len(output_feature_name) > 0):
      
      #Recupera la feature feature_name
      o_feat = OutputAnnotationType.getFeatureByBaseName(output_feature_name)
      
      #Checks if DependencyTreeNameFeat is a valid feature
      if not o_feat.isValid():
    
        error = source + " \'" + output_feature_name + "\' feature of " + OutputAnnotationType.getName() + ": is NOT found in type system!"
        ac.logError(error)
        raise Exception, error
        
      OuputFeats[output_feature_name] = o_feat


  
  #Example of a defined feature along with its value:
  #value=Negative

  filtering_features = []

  for f_feature_name in filtering_features_names:
      
    if (len(f_feature_name) > 0):
      
      feature_name = f_feature_name.split("=")[0]
      feature_value = f_feature_name.split("=")[1]
      
      #Recupera la feature feature_name
      feat = TokenType.getFeatureByBaseName(feature_name)
      
      #Checks if RefFeat is a valid feature
      if not feat.isValid():
    
        error = source + " " + feature_name + " feature of " + TokenType.getName() + ": is NOT found in type system!"
        ac.logError(error)
        raise Exception, error
        
      feat_data = {}
      feat_data['feature'] = feat
      feat_data['feat_value'] = feature_value   
        
      filtering_features.append(feat_data)



# the process method is passed two parameters, the CAS and
# the ResultsSpecification
def process(tcas, rs):
  
  global source
  global debug
  global ac

  global module

  global filtering_features
  
  global SentenceType
  global TokenType
  global DeSRType
  global OutputAnnotationType
  
  global TokenLemmaFeat
  global TokenPOSTagFeat
  global DeSRHeadFeat
  global DeSRLabelFeat
  global OuputFeats
  
  
  if debug > 10:
  
    print source + ": This is a process function"
    ac.logMessage("process called")
    
    
  #filtering_feature_matches = False
  index = tcas.getIndexRepository()
  
  #Iterador sobre las anotaciones de tipo Sentence
  sentence_iterator = tcas.getAnnotationIndex(SentenceType).iterator()

  while sentence_iterator.isValid():

    #Indice del token dentro de la frase  
    token_index = 0
    
    #Lista que guarda todos los tokens con los atributos necesarios para generar el arbol de dependencias
    tokens_data = []
    
    #Lista que guarda los tokens que pasan el filtro y se van a procesar
    target_tokens = []
  
    sentence_annotation = sentence_iterator.get()

    #Iterador sobre las anotaciones de tipo Token de la Sentence
    #token_iterator = sentence_annotation.subiterator(TokenType)
    
    token_iterator = tcas.getAnnotationIndex(TokenType).iterator()

    while token_iterator.isValid():

      token_annotation = token_iterator.get()

      if (token_annotation.getBeginPosition() >= sentence_annotation.getBeginPosition() and token_annotation.getEndPosition() <= sentence_annotation.getEndPosition()): 

        token_data = {}
        token_data['word'] = token_annotation.getCoveredText()
        token_data['tag'] = token_annotation.getStringValue(TokenPOSTagFeat)
        token_data['lemma'] = token_annotation.getStringValue(TokenLemmaFeat)

        #Iterador sobre las anotaciones de tipo DeSR del Token
        #desr_iterator = token_annotation.subiterator(DeSRType)
        desr_iterator = tcas.getAnnotationIndex(DeSRType).iterator()

        while desr_iterator.isValid():

          desr_annotation = desr_iterator.get()

          if (desr_annotation.getBeginPosition() == token_annotation.getBeginPosition() and desr_annotation.getEndPosition() == token_annotation.getEndPosition()):          
 
            token_data['relation'] = desr_annotation.getStringValue(DeSRLabelFeat)
            token_data['head'] = desr_annotation.getIntValue(DeSRHeadFeat)
            
            break
            
          desr_iterator.moveToNext()
      
        tokens_data.append(token_data)


        isTarget = False   
      
        for feat in filtering_features:
      
          #Recupera el valor de la feature de la annotation
          featValue = token_annotation.getStringValue(feat['feature'])
          
          if re.match(feat['feat_value'], featValue):
            
            isTarget = True

            if not flag_all_f_features_needed:

              break
        
          else:
            
            if flag_all_f_features_needed:
              
              isTarget = False  
              break
        
        if isTarget:
        
          target_token = {}
          target_token['index'] = token_index
          target_token['begin'] = token_annotation.getBeginPosition()
          target_token['end'] = token_annotation.getEndPosition()

          target_tokens.append(target_token)

        token_index += 1

      token_iterator.moveToNext()
      
    sentence_iterator.moveToNext()
    
    
    #Dependency Tree
    data = formatDependencyTreeData(tokens_data)
    dependencyGraph = depgraph.DepGraph(data, ['word','relation', 'head'])
    
    
    #Procesado de arbol de dependencias
    for target_token in target_tokens:

      token_index = target_token['index']
      ref_begin = target_token['begin']
      ref_end = target_token['end']

      values = module.extract_values(dependencyGraph, token_index)
      
      #Crea nueva annotation
      fs = tcas.createAnnotation(OutputAnnotationType, ref_begin, ref_end)

      for v in values.keys():
          
        fs.setStringValue(OuputFeats[v], str(values[v]))

            
      #Adds new annotation into CAS
      index.addFS(fs)
      


#Formats data needed for Dependency Tree from a Python dictionary to the proper format
def formatDependencyTreeData(data_dics):
    
  formatted_data = "word\trelation\thead\ttag\tlemma\n"
    
  for data in data_dics:

    formatted_data += data['word'] + "\t"
    formatted_data += data['relation'] + "\t"
    formatted_data += str(data['head']) + "\t"
    formatted_data += data['tag'] + "\t"
    formatted_data += data['lemma'] + "\n"
    
    
  return formatted_data

  