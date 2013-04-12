#!/usr/bin/python 

"""
@author: David Garcia Narbona
@contact: david.garcian@barcelonamedia.org
@organization: BMCI
@version: 1.0
"""

import depgraph


#Method to be implemented by the end user
def extract_values(dependencyGraph, token_index):
    
  values = {}
    
  values['mother_lemma'] = dependencyGraph.get_value_of_given_attr_from_my_mother(dependencyGraph.nodelist[token_index], "lemma")
  
    
  return values

  