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
import sys
import codecs


# everything in the global namespace is eval'ed during initialization
# by the Pythonnator UIMA annotator
def initialize(annotContext):

  global source
  global debug
  global ac
  
  global params
  global paths_rules


  ac = annotContext
  
  source = ac.extractValue("SourceFile")
  debug = ac.extractIntegerValue("DebugLevel")
  
  params = {}
  params['origin_type_name'] = ac.extractStringValue("originTypeName")
  params['destination_type_name'] = ac.extractStringValue("destinationTypeName")
  params['relation_type_name'] = ac.extractStringValue("relationTypeName")
  params['origin_feat_name'] = ac.extractStringValue("originFeatName")
  params['destination_feat_name'] = ac.extractStringValue("destinationFeatName")
  params['path_feat_name'] = ac.extractStringValue("pathFeatName")
  
  mirror_text = ac.extractIntegerValue("mirrorText")
  
  if mirror_text == 0:  
    params['mirror_text'] = False
    
  else:
    params['mirror_text'] = True
      
  
  paths_rules_file = ac.extractStringValue("pathsRulesFile")  
  
  file = open(paths_rules_file, 'r')
  paths_rules = []
  
  for path_rule in file:
  
    paths_rules.append(path_rule.rstrip('\r\n'))
  
  file.close()
  
  if debug > 0:
  
    print source + " :: initialize with:"
    print "> debug:", debug
    print "> originTypeName:", params['origin_type_name']
    print "> destinationTypeName:", params['destination_type_name']
    print "> relationTypeName:", params['relation_type_name']
    print "> originFeatName:", params['origin_feat_name']
    print "> destinationFeatName:", params['destination_feat_name']
    print "> pathFeatName:", params['path_feat_name']
    print "> mirror_text:", params['mirror_text']
    print "> pathsRulesFile:"
    
    for path_rule in paths_rules:
        
      print path_rule



def typeSystemInit(ts):

  global source
  global debug
  global ac
  
  global params
  
  global types
  global features

  
  if debug > 10:
    print source + ": Type sytem init called" 
 
  types = {}
  features = {}
  
  sourceDocumentTypeName = "org.apache.uima.examples.SourceDocumentInformation"
  sentenceTypeName = "org.barcelonamedia.uima.ts.Sentence"
  tokenTypeName = "org.barcelonamedia.uima.ts.Token"
  desrTypeName = "org.barcelonamedia.uima.ts.DeSR"
   
  
  SentenceType = ts.getType(sentenceTypeName)
  
  #Checks if SentenceType is a valid type
  if not SentenceType.isValid():

    error = source + ":", sentenceTypeName, "was NOT found in type system!"
    ac.logError(error)
    raise Exception, error

  types['SentenceType'] = SentenceType
   
  
  TokenType = ts.getType(tokenTypeName)
  
  #Checks if TokenType is a valid type
  if not TokenType.isValid():

    error = source + ":", tokenTypeName, "was NOT found in type system!"
    ac.logError(error)
    raise Exception, error

  types['TokenType'] = TokenType

  #Recupera la feature lemma
  TokenValueFeat = TokenType.getFeatureByBaseName("value")
      
  #Checks if TokenValueFeat is a valid feature
  if not TokenValueFeat.isValid():
    
    error = source + " \'value\' feature of " + TokenType.getName() + ": is NOT found in type system!"
    ac.logError(error)
    raise Exception, error

  features['TokenValueFeat'] = TokenValueFeat 

  #Recupera la feature lemma
  TokenLemmaFeat = TokenType.getFeatureByBaseName("lemma")
      
  #Checks if TokenLemmaFeat is a valid feature
  if not TokenLemmaFeat.isValid():
    
    error = source + " \'lemma\' feature of " + TokenType.getName() + ": is NOT found in type system!"
    ac.logError(error)
    raise Exception, error

  features['TokenLemmaFeat'] = TokenLemmaFeat 
  
  #Recupera la feature posTag
  TokenPOSTagFeat = TokenType.getFeatureByBaseName("posTag")
      
  #Checks if TokenPOSTagFeat is a valid feature
  if not TokenPOSTagFeat.isValid():
    
    error = source + " \'posTag\' feature of " + TokenType.getName() + ": is NOT found in type system!"
    ac.logError(error)
    raise Exception, error

  features['TokenPOSTagFeat'] = TokenPOSTagFeat 
  
  
  DeSRType = ts.getType(desrTypeName)
  
  #Checks if DeSRType is a valid type
  if not DeSRType.isValid():

    error = source + ":", desrTypeName, "was NOT found in type system!"
    ac.logError(error)
    raise Exception, error

  types['DeSRType'] = DeSRType
  
  #Recupera la feature head
  DeSRHeadFeat = DeSRType.getFeatureByBaseName("head_index")
      
  #Checks if DeSRHeadFeat is a valid feature
  if not DeSRHeadFeat.isValid():
    
    error = source + " \'head\' feature of " + DeSRType.getName() + ": is NOT found in type system!"
    ac.logError(error)
    raise Exception, error

  features['DeSRHeadFeat'] = DeSRHeadFeat 
  
  #Recupera la feature label
  DeSRLabelFeat = DeSRType.getFeatureByBaseName("label")
      
  #Checks if DeSRLabelFeat is a valid feature
  if not DeSRLabelFeat.isValid():
    
    error = source + " \'label\' feature of " + DeSRType.getName() + ": is NOT found in type system!"
    ac.logError(error)
    raise Exception, error

  features['DeSRLabelFeat'] = DeSRLabelFeat 
  
  
  originType = ts.getType(params['origin_type_name'])
  
  #Checks if originType is a valid type
  if not originType.isValid():

    error = source + ":", params['origin_type_name'], "was NOT found in type system!"
    ac.logError(error)
    raise Exception, error

  types['originType'] = originType
  
  
  destinationType = ts.getType(params['destination_type_name'])
  
  #Checks if destinationType is a valid type
  if not destinationType.isValid():

    error = source + ":", params['destination_type_name'], "was NOT found in type system!"
    ac.logError(error)
    raise Exception, error

  types['destinationType'] = destinationType
  
  
  relationType = ts.getType(params['relation_type_name'])
  
  #Checks if relationType is a valid type
  if not relationType.isValid():

    error = source + ":", params['relation_type_name'], "was NOT found in type system!"
    ac.logError(error)
    raise Exception, error
  
  types['relationType'] = relationType  
  
  #Recupera la feature origin_feat_name
  originFeat = relationType.getFeatureByBaseName(params['origin_feat_name'])
      
  #Checks if originFeat is a valid feature
  if not originFeat.isValid():
    
    error = source + " \'",params['origin_feat_name'],"\' feature of " + relationType.getName() + ": is NOT found in type system!"
    ac.logError(error)
    raise Exception, error

  features['originFeat'] = originFeat 

  #Recupera la feature destination_feat_name
  destinationFeat = relationType.getFeatureByBaseName(params['destination_feat_name'])
      
  #Checks if destinationFeat is a valid feature
  if not destinationFeat.isValid():
    
    error = source + " \'",params['destination_feat_name'],"\' feature of " + relationType.getName() + ": is NOT found in type system!"
    ac.logError(error)
    raise Exception, error

  features['destinationFeat'] = destinationFeat
  
  #Recupera la pathFeat label
  if len(params['path_feat_name']) > 0:
      
    pathFeat = relationType.getFeatureByBaseName(params['path_feat_name'])
      
    #Checks if pathFeat is a valid feature
    if not pathFeat.isValid():
    
      error = source + " \'",params['path_feat_name'],"\' feature of " + relationType.getName() + ": is NOT found in type system!"
      ac.logError(error)
      raise Exception, error

    features['pathFeat'] = pathFeat
  
  
  
# the process method is passed two parameters, the CAS and
# the ResultsSpecification
def process(tcas, rs):
  
  global source
  global debug
  global ac
 
  global types
  global features

  
  if debug > 10:
  
    print source + ": This is a process function"
    ac.logMessage("process called")
  
   
  #---------------------------------------------------------------------
  # Procesaso de recuperacion de datos de las anotaciones para poder generar
  # el arbol mediante el objeto depgraph    
    
  index = tcas.getIndexRepository()
  
  #Iterador sobre las anotaciones de tipo Sentence
  sentence_iterator = tcas.getAnnotationIndex(types['SentenceType']).iterator()
  
  while sentence_iterator.isValid():   
    
    #Indice del token dentro de la frase  
    token_index = 0
    
    #Lista que guarda todos los tokens con los atributos necesarios para generar el arbol de dependencias
    tokens_data = []
  
    sentence_annotation = sentence_iterator.get()

    #Iterador sobre las anotaciones de tipo Token de la Sentence    
    token_iterator = tcas.getAnnotationIndex(types['TokenType']).iterator()

      
    while token_iterator.isValid():

      token_annotation = token_iterator.get()

      if (token_annotation.getBeginPosition() >= sentence_annotation.getBeginPosition() and token_annotation.getEndPosition() <= sentence_annotation.getEndPosition()): 

        token_data = {}
        token_data['begin'] = token_annotation.getBeginPosition()
        token_data['end'] = token_annotation.getEndPosition()
        token_data['word'] = token_annotation.getCoveredText()
        token_data['tag'] = token_annotation.getStringValue(features['TokenPOSTagFeat'])
        token_data['lemma'] = token_annotation.getStringValue(features['TokenLemmaFeat'])

        #Iterador sobre las anotaciones de tipo DeSR del Token
        desr_iterator = tcas.getAnnotationIndex(types['DeSRType']).iterator()

        while desr_iterator.isValid():

          desr_annotation = desr_iterator.get()

          if (desr_annotation.getBeginPosition() == token_annotation.getBeginPosition() and desr_annotation.getEndPosition() == token_annotation.getEndPosition()):          
 
            token_data['relation'] = desr_annotation.getStringValue(features['DeSRLabelFeat'])
            token_data['head'] = desr_annotation.getIntValue(features['DeSRHeadFeat'])
            
            if (desr_annotation.getStringValue(features['DeSRLabelFeat']) == 'ROOT'):
              token_data['head'] = 0
              
            else:
              token_data['head'] = desr_annotation.getIntValue(features['DeSRHeadFeat'])
              
            break
            
          desr_iterator.moveToNext()
      
        tokens_data.append(token_data)

        #Valor por defecto de la feature 'value' de la anotacion token_annotation
        token_annotation.setStringValue(features['TokenValueFeat'], "_")
                
        token_index += 1

      token_iterator.moveToNext()      
    #---------------------------------------------------------------------
    
    
    #Dependency Tree
    data = formatDependencyTreeData(tokens_data)
    dependencyGraph = depgraph.DepGraph(data, ['word','relation', 'head'])
    #---------------------------------------------------------------------


    #---------------------------------------------------------------------   
                    
    #Iterador sobre las anotaciones de tipo originType de la Sentence    
    origin_iterator = tcas.getAnnotationIndex(types['originType']).iterator()

    origins_tokens = []
      
    while origin_iterator.isValid():
      
      origin_annotation = origin_iterator.get()
        
      if (origin_annotation.getBeginPosition() >= sentence_annotation.getBeginPosition() and origin_annotation.getEndPosition() <= sentence_annotation.getEndPosition()):   
          
        origin_tokens_indexes = getTokenIndexes(origin_annotation, tokens_data)
 
        origin_data = {}
        origin_data['annotation'] = origin_annotation
        origin_data['coveredText'] = origin_annotation.getCoveredText()
        origin_data['tokens_indexes'] = origin_tokens_indexes
        origin_data['begin'] = origin_annotation.getBeginPosition()
        origin_data['end'] = origin_annotation.getEndPosition()

        origins_tokens.append(origin_data)
    
      origin_iterator.moveToNext()
        
        
    #Iterador sobre las anotaciones de tipo destinationType de la Sentence    
    destination_iterator = tcas.getAnnotationIndex(types['destinationType']).iterator()
        
    destinations_tokens = []
      
    while destination_iterator.isValid():
          
      destination_annotation = destination_iterator.get()
      
      if (destination_annotation.getBeginPosition() >= sentence_annotation.getBeginPosition() and destination_annotation.getEndPosition() <= sentence_annotation.getEndPosition()):   
          
        destination_tokens_indexes = getTokenIndexes(destination_annotation, tokens_data)
      
        destination_data = {}
        destination_data['annotation'] = destination_annotation
        destination_data['coveredText'] = destination_annotation.getCoveredText()
        destination_data['tokens_indexes'] = destination_tokens_indexes
        destination_data['begin'] = destination_annotation.getBeginPosition()
        destination_data['end'] = destination_annotation.getEndPosition()

        destinations_tokens.append(destination_data)

      destination_iterator.moveToNext()
      
         
    # Algoritmo principal --------------------------------------------------------     
    for origin_tokens in origins_tokens:
            
      #Guarda la nodeList anterior, para retornar el camino mas corto entre un origin y un destination
      #La inicializa a un tamanyo maximo.
      prev_NodeList = [0] * 100
 
      tag_path = ""
      
      destination_candidate = None
      
      #Distancia de los tokens en el texto
      prev_tokens_distance = 0
    
      for destination_tokens in destinations_tokens:

        for origin_token_index in origin_tokens['tokens_indexes']:


          #Normaliza los indices de los tokens porque depgraph inicia indices de tokens en 1, no en 0
          origin_token_index += 1           
          
          for destination_token_index in destination_tokens['tokens_indexes']:


            #Normaliza los indices de los tokens porque depgraph inicia indices de tokens en 1, no en 0
            destination_token_index += 1
            
            # Joan, some nodes are out the sentence boundaries 
            if ((len(dependencyGraph.nodelist) <= origin_token_index) or (len(dependencyGraph.nodelist) <= destination_token_index)):
                
			         print  >> sys.stderr,"ERROR: index out of range :\n", origin_token_index , destination_token_index,len(dependencyGraph.nodelist)
			         print  >> sys.stderr,"in dependency graph :\n", dependencyGraph.nodelist
			         break

            nodeList = dependencyGraph.get_shortest_path(dependencyGraph.nodelist[origin_token_index], dependencyGraph.nodelist[destination_token_index])
            
            tokens_distance = abs(origin_token_index - destination_token_index)

            if (nodeList != None) and ((len(nodeList) < len(prev_NodeList) or (len(nodeList) == len(prev_NodeList) and prev_tokens_distance > tokens_distance))):
                
              tmp_tag_path = tagPath(nodeList)
                
              if isValidPath(tmp_tag_path):       
                  
                tag_path = tmp_tag_path      
   
                prev_NodeList = nodeList
                prev_tokens_distance = tokens_distance
               
                destination_candidate = destination_tokens

        
      #if len(tag_path) > 0 and isValidPath(tag_path):
      if len(tag_path) > 0:     

        #Crea nueva annotation del tipo relationType
        begin = min(origin_tokens['begin'], destination_candidate['begin'])
        end = max(origin_tokens['end'], destination_candidate['end'])
                 
        relationAnnotation = tcas.createAnnotation(types['relationType'], begin, end)
                 
        relationAnnotation.setFSValue(features['originFeat'], origin_tokens['annotation'])
        relationAnnotation.setFSValue(features['destinationFeat'], destination_candidate['annotation'])
        
        if len(params['path_feat_name']) > 0:
        
          relationAnnotation.setStringValue(features['pathFeat'], tag_path)
  
        #Adds new annotation into CAS
        index.addFS(relationAnnotation)
  
        origin_tokens['destinationCoveredText'] = destination_candidate['coveredText']
        destination_candidate['originCoveredText'] = origin_tokens['coveredText']

    #-------------------------------------------------------------
        
    
    #--------------------------------------------------------------
    # Actualiza feature 'value' de los tokens con el coveredText de las anotaciones originType y destinationType
    
    if params['mirror_text']:
        
      token_index = 0
    
      token_iterator.moveToFirst()
    
      while token_iterator.isValid():

        token_annotation = token_iterator.get()
      
        if (token_annotation.getBeginPosition() >= sentence_annotation.getBeginPosition() and token_annotation.getEndPosition() <= sentence_annotation.getEndPosition()):
        
          for destination_tokens in destinations_tokens:

            if token_index in destination_tokens['tokens_indexes'] and "destinationCoveredText" in destination_tokens:

              token_annotation.setStringValue(features['TokenValueFeat'], destination_tokens['destinationCoveredText'])
            
        
          for destination_tokens in destinations_tokens:
      
            if token_index in destination_tokens['tokens_indexes'] and "originCoveredText" in destination_tokens:

              token_annotation.setStringValue(features['TokenValueFeat'], destination_tokens['originCoveredText'])

          token_index += 1
      
        token_iterator.moveToNext()
    #-----------------------------------------------------------
    
    sentence_iterator.moveToNext()



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


#Returns de tag path from a nodeList
def tagPath(nodeList):
    
  tag_string = []
  
  for node in nodeList:

    tag = node['relation']
    
    if "rn" in node:

        tag_string.append("|")
      
    else:
        
      tag_string.append("|" + node['relation'])
        
  return "".join(tag_string)
    

#Returns a list of token indexes delimited by the span of the annotation 
def getTokenIndexes(annotation, tokens_data):
    
    token_indexes = []
    
    ann_begin = annotation.getBeginPosition()
    ann_end = annotation.getEndPosition()
    
    token_index = 0
    
    for token_data in tokens_data:
        
      tk_begin = token_data['begin']
      tk_end = token_data['end']

      if tk_begin>= ann_begin and tk_end <= ann_end:

        token_indexes.append(token_index)
      
      token_index += 1
    
    return token_indexes


#Checks if the token path (nodeList) from annotation A to annotation B has no internal 
#node in any of the specified annotations
def clean_path_between_annotations(nodeList, annA_tokens, annB_tokens):
    
    path_index = 0
    path_len = len(nodeList)
    
    for node in nodeList:
    
      path_token_index = node['address']
      
      #Normaliza los indices de los tokens porque depgraph inicia indices de tokens en 1, no en 0
      path_token_index -= 1
      
      if not (path_index == 0 or path_index == (path_len - 1)):
      
        if (path_token_index in annA_tokens['tokens_indexes']) or (path_token_index in annB_tokens['tokens_indexes']):
            
          return False
      
      path_index += 1
      
    return True


#Checks whether path is a valid path taking into account paths_rules
def isValidPath(path):
    
    global paths_rules

    if len(paths_rules) == 0:
        
      return True
      
    for path_rule in paths_rules:

      if re.match(path_rule, path):

        return True
      
    return False
