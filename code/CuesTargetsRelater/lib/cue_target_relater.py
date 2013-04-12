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
import MySQLdb as mdb
import codecs


# everything in the global namespace is eval'ed during initialization
# by the Pythonnator UIMA annotator
def initialize(annotContext):

  global source
  global debug
  global ac
  
  #global log
  
  
  ac = annotContext
  
  source = ac.extractValue("SourceFile")
  debug = ac.extractIntegerValue("DebugLevel")
  
  #log=codecs.open('./log.txt', 'w',encoding="utf-8")
  
  if debug > 0:
  
    print source + " :: initialize with:"
    print "> debug: ", debug


def typeSystemInit(ts):

  global source
  global debug
  global ac
  global con
  global good_file
  global bad_file
  
  global SourceDocumentType 
  global SentenceType
  global TokenType
  global DeSRType
  global CueType
  global TargetType
  global CuePolarityType
  global PolarType
  global ChunkNPType
  
  global OpinionatedUnitType
  
  global DocumentUriFeat
  global TokenLemmaFeat
  global TokenPOSTagFeat
  global DeSRHeadFeat
  global DeSRLabelFeat 
  global CueFeat
  global TargetFeat
  global CuePolarityFeat
  global PolarPolarityFeat
  global PathFeat
  global PolarityFeat
  global IntensityFeat

  global TokenValueFeat
  
    
  if debug > 10:
    print source + ": Type sytem init called"
 
 
  SourceDocumentType = ts.getType("org.apache.uima.examples.SourceDocumentInformation")
  #Checks if SourceDocumentType is a valid type
  if not SourceDocumentType.isValid():

    error = source + ": org.apache.uima.examples.SourceDocumentInformation is NOT found in type system!"
    ac.logError(error)
    raise Exception, error
  #Recupera la feature posTag
  DocumentUriFeat = SourceDocumentType.getFeatureByBaseName("uri")
      
  #Checks if TokenLemmaFeat is a valid feature
  if not DocumentUriFeat.isValid():
    
    error = source + " \'uri\' feature of " + SourceDocumentType.getName() + ": is NOT found in type system!"
    ac.logError(error)
    raise Exception, error
  
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
  TokenValueFeat = TokenType.getFeatureByBaseName("value")
      
  #Checks if TokenValueFeat is a valid feature
  if not TokenValueFeat.isValid():
    
    error = source + " \'value\' feature of " + TokenType.getName() + ": is NOT found in type system!"
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
      
  #Checks if DeSRHeadFeat is a valid feature
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


  CueType = ts.getType("org.barcelonamedia.uima.ts.opinion.Cue")
  
  #Checks if CueType is a valid type
  if not CueType.isValid():

    error = source + ": org.barcelonamedia.uima.ts.opinion.Cue is NOT found in type system!"
    ac.logError(error)
    raise Exception, error


  TargetType = ts.getType("org.barcelonamedia.uima.ts.opinion.Target")
  
  #Checks if TargetType is a valid type
  if not TargetType.isValid():

    error = source + ": org.barcelonamedia.uima.ts.opinion.Target is NOT found in type system!"
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


  ChunkNPType = ts.getType("org.barcelonamedia.uima.ts.ChunkNP")
  
  #Checks if ChunkNPType is a valid type
  if not ChunkNPType.isValid():

    error = source + ": org.barcelonamedia.uima.ts.ChunkNP is NOT found in type system!"
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

  #Recupera la feature Target
  TargetFeat = OpinionatedUnitType.getFeatureByBaseName("Target")
      
  #Checks if TargetFeat is a valid feature
  if not TargetFeat.isValid():
    
    error = source + " \'Target\' feature of " + OpinionatedUnitType.getName() + ": is NOT found in type system!"
    ac.logError(error)
    raise Exception, error

  #Recupera la feature PolarityFeat
  PolarityFeat = OpinionatedUnitType.getFeatureByBaseName("polarity")
      
  #Checks if PolarityFeat is a valid feature
  if not PolarityFeat.isValid():
    
    error = source + " \'polarity\' feature of " + OpinionatedUnitType.getName() + ": is NOT found in type system!"
    ac.logError(error)
    raise Exception, error

  #Recupera la PathFeat label
  PathFeat = OpinionatedUnitType.getFeatureByBaseName("path")
      
  #Checks if DeSRLabelFeat is a valid feature
  if not PathFeat.isValid():
    
    error = source + " \'path\' feature of " + OpinionatedUnitType.getName() + ": is NOT found in type system!"
    ac.logError(error)
    raise Exception, error

  #Recupera la feature intensity
  IntensityFeat = OpinionatedUnitType.getFeatureByBaseName("intensity")
      
  #Checks if IntensityFeat is a valid feature
  if not IntensityFeat.isValid():
    
    error = source + " \'intensity\' feature of " + OpinionatedUnitType.getName() + ": is NOT found in type system!"
    ac.logError(error)
    raise Exception, error
    
  try:

    con = mdb.connect('localhost', 'sm', 
        's0c14lm3d14', 'sm_annotations')
        
  except mdb.Error, e:
      print "Error %d: %s" % (e.args[0], e.args[1])

  good_file=codecs.open('./good.txt', 'w',encoding="utf-8")
  bad_file=codecs.open('./bad.txt', 'w',encoding="utf-8")



# the process method is passed two parameters, the CAS and
# the ResultsSpecification
def process(tcas, rs):
  
  global source
  global debug
  global ac
 
  global SourceDocumentType 
  global SentenceType
  global TokenType
  global DeSRType
  global CueType
  global TargetType
  global CuePolarityType
  global PolarType
  global ChunkNPType
  
  global OpinionatedUnitType
  
  global TokenLemmaFeat
  global TokenPOSTagFeat
  global DeSRHeadFeat
  global DeSRLabelFeat
  
  global TokenValueFeat 
  global CueFeat
  global TargetFeat
  global CuePolarityFeat
  global PolarPolarityFeat
  global PathFeat
  global PolarityFeat
  global IntensityFeat  
  
  global con
  
  #global log
  
  
  if debug > 10:
  
    print source + ": This is a process function"
    ac.logMessage("process called")
    
  # get the docuement id
  #Iterador sobre las anotaciones de tipo Sentence
  SourceDocument_iterator = tcas.getAnnotationIndex(SourceDocumentType).iterator()

  while SourceDocument_iterator.isValid():  
   SourceDocument_annotation = SourceDocument_iterator.get()
   document_id=SourceDocument_annotation.getStringValue(DocumentUriFeat)
   SourceDocument_iterator.moveToNext() 
   
   
  #---------------------------------------------------------------------
  # Procesaso de recuperacion de datos de las anotaciones para poder generar
  # el arbol mediante el objeto depgraph    
    
  index = tcas.getIndexRepository()
  
  #Iterador sobre las anotaciones de tipo Sentence
  sentence_iterator = tcas.getAnnotationIndex(SentenceType).iterator()

  while sentence_iterator.isValid():

    #Indice del token dentro de la frase  
    token_index = 0
    
    #Lista que guarda todos los tokens con los atributos necesarios para generar el arbol de dependencias
    tokens_data = []
  
    sentence_annotation = sentence_iterator.get()

    #Iterador sobre las anotaciones de tipo Token de la Sentence    
    token_iterator = tcas.getAnnotationIndex(TokenType).iterator()
    
    #log.write("\n-------------------------------------------------\n") 
    #log.write("sentence: " + sentence_annotation.getCoveredText() + "\n")
      
    while token_iterator.isValid():

      token_annotation = token_iterator.get()

      if (token_annotation.getBeginPosition() >= sentence_annotation.getBeginPosition() and token_annotation.getEndPosition() <= sentence_annotation.getEndPosition()): 

        token_data = {}
        token_data['begin'] = token_annotation.getBeginPosition()
        token_data['end'] = token_annotation.getEndPosition()
        token_data['word'] = token_annotation.getCoveredText()
        token_data['tag'] = token_annotation.getStringValue(TokenPOSTagFeat)
        token_data['lemma'] = token_annotation.getStringValue(TokenLemmaFeat)

        #Iterador sobre las anotaciones de tipo DeSR del Token
        desr_iterator = tcas.getAnnotationIndex(DeSRType).iterator()

        while desr_iterator.isValid():

          desr_annotation = desr_iterator.get()

          if (desr_annotation.getBeginPosition() == token_annotation.getBeginPosition() and desr_annotation.getEndPosition() == token_annotation.getEndPosition()):          
 
            token_data['relation'] = desr_annotation.getStringValue(DeSRLabelFeat)
            token_data['head'] = desr_annotation.getIntValue(DeSRHeadFeat)
            if (desr_annotation.getStringValue(DeSRLabelFeat) == 'ROOT'):
              token_data['head'] = 0
            else:
              token_data['head'] = desr_annotation.getIntValue(DeSRHeadFeat)
            break
            
          desr_iterator.moveToNext()
      
        tokens_data.append(token_data)

        #Valor por defecto de la feature 'value' de la anotacion token_annotation
        token_annotation.setStringValue(TokenValueFeat, "_")
                
        token_index += 1

      token_iterator.moveToNext()
      
      
    #---------------------------------------------------------------------
    #Dependency Tree
    data = formatDependencyTreeData(tokens_data)
    dependencyGraph = depgraph.DepGraph(data, ['word','relation', 'head'])
    #---------------------------------------------------------------------


    #---------------------------------------------------------------------   
                    
    #Iterador sobre las anotaciones de tipo Cue de la Sentence    
    cue_iterator = tcas.getAnnotationIndex(CueType).iterator()

    cues_tokens = []
      
    while cue_iterator.isValid():
      
      cue_annotation = cue_iterator.get()
        
      if (cue_annotation.getBeginPosition() >= sentence_annotation.getBeginPosition() and cue_annotation.getEndPosition() <= sentence_annotation.getEndPosition()):   
          
        cue_tokens_indexes = getTokenIndexes(cue_annotation, tokens_data)
 
        cue_data = {}
        cue_data['annotation'] = cue_annotation
        cue_data['coveredText'] = cue_annotation.getCoveredText()
        cue_data['tokens_indexes'] = cue_tokens_indexes
        cue_data['begin'] = cue_annotation.getBeginPosition()
        cue_data['end'] = cue_annotation.getEndPosition()

        cues_tokens.append(cue_data)
          
      cue_iterator.moveToNext()
        
        
    #Iterador sobre las anotaciones de tipo Target de la Sentence    
    target_iterator = tcas.getAnnotationIndex(TargetType).iterator()
        
    targets_tokens = []
      
    while target_iterator.isValid():
          
      target_annotation = target_iterator.get()
      
      if (target_annotation.getBeginPosition() >= sentence_annotation.getBeginPosition() and target_annotation.getEndPosition() <= sentence_annotation.getEndPosition()):   
          
        target_tokens_indexes = getTokenIndexes(target_annotation, tokens_data)
      
        target_data = {}
        target_data['annotation'] = target_annotation
        target_data['coveredText'] = target_annotation.getCoveredText()
        target_data['tokens_indexes'] = target_tokens_indexes
        target_data['begin'] = target_annotation.getBeginPosition()
        target_data['end'] = target_annotation.getEndPosition()

        targets_tokens.append(target_data)
        
      target_iterator.moveToNext()

    # Algoritmo principal --------------------------------------------------------     
    for cue_tokens in cues_tokens:
            
      #Guarda la nodeList anterior, para retornar el camino mas corto entre un cue y un target
      #La inicializa a un tamanyo maximo.
      prev_NodeList = [0] * 100
 
      tag_path = ""
      
      target_candidate = None
      
      #Distancia de los tokens en el texto
      prev_tokens_distance = 0
      
      #print "\n> loop cue"
      
      for target_tokens in targets_tokens:
        
        #print ">> loop target"
        
        for cue_token_index in cue_tokens['tokens_indexes']:
          #print ">> cue token:",cue_token_index
          #Joan Normaliza los indices de los tokens porque depgraph inicia indices de tokens en 1, no en 0
          cue_token_index += 1 
          
          
          for target_token_index in target_tokens['tokens_indexes']:
            #print ">> target token:",target_token_index
            #Normaliza los indices de los tokens porque depgraph inicia indices de tokens en 1, no en 0
            target_token_index += 1
            
            # Joan, some nodes are out the sentence boundaries 
            if ((len(dependencyGraph.nodelist) <= cue_token_index) or  (len(dependencyGraph.nodelist) <= target_token_index)):
                
			         print  >> sys.stderr,"ERROR: index out of range :\n", cue_token_index , target_token_index,len(dependencyGraph.nodelist)
			         print  >> sys.stderr,"in dependency graph :\n", dependencyGraph.nodelist
			         break
            
            
            nodeList = dependencyGraph.get_shortest_path(dependencyGraph.nodelist[cue_token_index], dependencyGraph.nodelist[target_token_index])
            
            tokens_distance = abs(cue_token_index - target_token_index)
            #print ">nodeList:",nodeList
            #print ">tokens_distance:",tokens_distance
            #Mira si debe eliminar nodeList
            #if (nodeList != None) and (len(nodeList) < len(prev_NodeList)) and clean_path_between_annotations(nodeList, cue_tokens, target_tokens):
            if (nodeList != None) and ((len(nodeList) < len(prev_NodeList) or (len(nodeList) == len(prev_NodeList) and prev_tokens_distance > tokens_distance))):
               reviewers_validation = checkDatabase(cue_tokens, target_tokens, document_id)
               printPathToFile(cue_tokens, target_tokens, nodeList, sentence_annotation, reviewers_validation)
               
               #if reviewers_validation:
               tag_path = tagPath(nodeList)
               prev_NodeList = nodeList
               prev_tokens_distance = tokens_distance
               #print ">tag_path:",tag_path
               
               target_candidate = target_tokens
               
      if len(tag_path) > 0:

        #Crea nueva annotation del tipo OpinionatedUnitType
        begin = min(cue_tokens['begin'], target_candidate['begin'])
        end = max(cue_tokens['end'], target_candidate['end'])
             
        # polaridad                 
        cuePolarityAnnotation = getCuePolarityAnnFromCueAnn(tcas, CuePolarityType, cue_tokens['annotation'])
               
        if cuePolarityAnnotation != None:
                 
          polarity = cuePolarityAnnotation.getStringValue(CuePolarityFeat)
                   
        else:
                     
          polar_annotations = getPolarAnnsFromOpinionatedUnitSpan(tcas, PolarType, begin, end)
          polarity = 0
                 
          if polar_annotations:                       
                   
            for polar_ann in polar_annotations:
                         
              polarity += int(polar_ann.getStringValue(PolarPolarityFeat))
              
            polarity = polarity/len(polar_annotations)
        #-------------------------------------------------------

        #cueA = tcas.createAnnotation(ArgumentMentionType, cue_tokens['begin'], cue_tokens['end'])
        #cueA.setFSValue(RefFeat, cue_tokens['annotation'])
        #cueA.setStringValue(RoleFeat, "Cue")
                 
        #targetA = tcas.createAnnotation(ArgumentMentionType, target_candidate['begin'], target_candidate['end'])
        #targetA.setFSValue(RefFeat, target_candidate['annotation'])
        #targetA.setStringValue(RoleFeat, "Target")
                 
        opinionatedUnitA = tcas.createAnnotation(OpinionatedUnitType, begin, end)
                 
        opinionatedUnitA.setFSValue(CueFeat, cue_tokens['annotation'])
        opinionatedUnitA.setFSValue(TargetFeat, target_candidate['annotation'])
        opinionatedUnitA.setStringValue(PathFeat, tag_path)
        
        if polarity > 0:
        
            opinionatedUnitA.setStringValue(PolarityFeat, "positive")
        
        elif polarity < 0:
            
            opinionatedUnitA.setStringValue(PolarityFeat, "negative")
            
        else:
            
            opinionatedUnitA.setStringValue(PolarityFeat, "neutral")

        #Adds new annotation into CAS
        index.addFS(opinionatedUnitA)
  
        cue_tokens['targetCoveredText'] = target_candidate['coveredText']
        target_candidate['cueCoveredText'] = cue_tokens['coveredText']
        #--------------------------------------------------------------
               
      #log.write("> cue " + cue_tokens['coveredText'] + " linked to target " + target_tokens['coveredText'] + "\n")

    #-------------------------------------------------------------
        
    
    #--------------------------------------------------------------
    # Actualiza feature 'value' de los tokens con el coveredText de los Target y Cues
    token_index = 0
    
    token_iterator.moveToFirst()
    
    #log.write("\n")
    
    while token_iterator.isValid():

      token_annotation = token_iterator.get()
      
      if (token_annotation.getBeginPosition() >= sentence_annotation.getBeginPosition() and token_annotation.getEndPosition() <= sentence_annotation.getEndPosition()):
        
        for cue_tokens in cues_tokens:
            
          #log.write(">> process cue_tokens[" + cue_tokens['coveredText'] + "  with token_indexes" + str(cue_tokens['tokens_indexes']) + "\n")

          #print "-> token[", token_index, "]:",token_annotation.getCoveredText(),"  ::  cue_tokens_indexes:",cue_tokens['tokens_indexes']
          if token_index in cue_tokens['tokens_indexes'] and "targetCoveredText" in cue_tokens:
        
            #log.write("\n> token[" + str(token_index) + "]"+ token_annotation.getCoveredText() + "\n")
            #log.write("********* 1 " + cue_tokens['targetCoveredText'] + "\n")
            token_annotation.setStringValue(TokenValueFeat, cue_tokens['targetCoveredText'])
            
        
        for target_tokens in targets_tokens:
      
          if token_index in target_tokens['tokens_indexes'] and "cueCoveredText" in target_tokens:
            
            #log.write("> token[" + str(token_index) + "]" + token_annotation.getCoveredText() + "\n")
            #log.write("> ******* 2 " + target_tokens['cueCoveredText'] + "\n")
            token_annotation.setStringValue(TokenValueFeat, target_tokens['cueCoveredText'])

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


#Gets the CuePolarity annotation covering 'cueAnn'
def getCuePolarityAnnFromCueAnn(tcas, CuePolarityType, cueAnn):
  
  #Iterador sobre las anotaciones de tipo CuePolarityType
  cuePolarity_iterator = tcas.getAnnotationIndex(CuePolarityType).iterator()
            
  while cuePolarity_iterator.isValid():
    
    cuePolarity_annotation = cuePolarity_iterator.get()      
       
    if (cuePolarity_annotation.getBeginPosition() == cueAnn.getBeginPosition()) and (cuePolarity_annotation.getEndPosition() == cueAnn.getEndPosition()):
    
      return cuePolarity_annotation
        
    cuePolarity_iterator.moveToNext()
    
  return None
  

#Gets the Polar annotations covered by 'targetAnn'
def getPolarAnnsFromOpinionatedUnitSpan(tcas, PolarType, begin, end):
  
  polar_annotations = []
  
  #Iterador sobre las anotaciones de tipo PolarityType
  polar_iterator = tcas.getAnnotationIndex(PolarType).iterator()
            
  while polar_iterator.isValid():
    
    polar_annotation = polar_iterator.get()
       
    if (polar_annotation.getBeginPosition() >= begin) and (polar_annotation.getEndPosition() <= end):
    
      polar_annotations.append(polar_annotation)
        
    polar_iterator.moveToNext()
    
  return polar_annotations
  

# returns true if the relationship between cue and target exists in the database.
def checkDatabase(cue_tokens, target_tokens,document_id ):
    global con
    try:
      cur = con.cursor()
      document_id=document_id.replace("file:///","")
      query = "SELECT * from reviews_booking_sp_pol_units_offsets where doc_id= '{0}' and q= {1} and t= {2} ".format(document_id,cue_tokens['begin'], target_tokens['begin'])
      # cur.execute("""SELECT * from reviews_booking_sp_pol_units_offsets where doc_id= % and q= % and t= % """  ,(document_id,cue_tokens['begin'], target_tokens['begin']))
      cur.execute(query)
    except mdb.Error, e:
	    print "Error doing query %s,  %d: %s" % (query,e.args[0], e.args[1])
	    return False 
	   
    numrows = int(cur.rowcount)
    cur.close()
    return numrows > 0


# prints in the corresponding file
def printPathToFile ( cue_tokens, target_tokens,nodeList,sentence_annotation,good):
  global good_file
  global bad_file
  if good:
    out_file=good_file
  else:
    out_file=bad_file
  print >>out_file, "_________________________________________________________"
  print >>out_file, sentence_annotation.getCoveredText()
  print >>out_file, "\n Cue["+cue_tokens['coveredText'] + "]  => target["+target_tokens['coveredText'] + "]"
  for node in nodeList:
    print >>out_file, "**","\t".join([unicode(x) for x in [node['address'],node['word'], node['relation'],node['head'] ] ])
    
  tag_string = tagPath(nodeList)
  
  print  >>out_file, "\n" + tag_string + "\n"
  print >>out_file, "_________________________________________________________\n"


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
