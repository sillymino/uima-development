# -*- coding: utf-8 -*-

import copy
import nltk
from nltk.tree import *
from nltk.draw import tree
from nltk.parse import *

data = """word    relation    head
    Hotel    sentence    0
    limpio    S   1
    ,    f   4
    habitaciones    cc  2
    muy    sadv    4
    comodas    cc  2
    cuidando    sp  6
    el    spec    9
    detalle    sn  7
    ,    f   11
    insonorizadas    sn  9
    ,    f   11
    personal    s.a 14
    atento    sn  11
    y    coord   14
    precio    grup.nom    14
    adecuado    S   16
    .    f   1
    """

"""
Example of a node structure. Only for documentation purposes!
"""
__NODE = {	
	'word': 'house',
	'relation': 'NMOD',
	'head': 0,
	'attribute_1': 'value_1',
	'attribute_2': 'value_2',
	'attribute_n': 'value_n'
}

class DepGraph(DependencyGraph):

	"""
	Container for the nodes and labelled edges of a dependency graph structure. It implements some data retrieveing methods.
	Input is assumed to be a modified Malt-Tab format version.


	Malt-Tab modified version (tab separated), [] enclosed attributes are optional and user-definded:

	B{word}    B{relation}    B{head}   B{[tag]}  B{[lemma]}

	B{the}	     B{SBJ}	    B{2}	   B{NN}	  B{the}

	B{naw}	     B{ROOT}	    B{0}	   B{VBD}	  B{naw}

	B{his}	     B{OBJ}	    B{2}	   B{PRP$}	  B{his}

	B{papers}	 B{SBJ}	    B{5}	   B{NNS}	  B{paper}



	A node is a dict with at least 3 mandatory fields: I{word, relation} and I{head}. 

	__NODE = E{lb}

	I{'word': 'house',}

	I{'relation': 'NMOD',}

	I{'head': 0,}

	'attribute_1': 'value_1',

	'attribute_2': 'value_2',

	'attribute_n': 'value_n',

	... , ... , ...

	E{rb}


	@author: Bernat Grau
	@contact: bernat.grau@barcelonamedia.org
	@organization: Barcelona Mèdia Centre d'Innovació
	@version: 1.0
	"""


	########################################################################
	### Class attributes

	STOPFILTER_ATTRIBUTE = "filter_attribute"
	STOPFILTER_VALUE = "filter_value"

	RELATION_COLUMN = "relation"
	WORD_COLUMN = "word"
	HEAD_COLUMN = "head"

	ADDRESS_COLUMN = 'address'
	DEPS_COLUMN = "deps"

	########################################################################
	### Constructor

	def __init__(self, data, fixed_header=None):
		"""Builds the tree as a list of nodes.

		@type fixed_header: list
		@param fixed_header: List of the first three columns on Malt-Tab data. Overrides WORD_COLUMN, RELATION_COLUMN, HEAD_COLUMN

		"""
		# fixed_header describes the frist three mandatory columns in data in this order: WORD, RELATION, HEAD
		if fixed_header != None and len(fixed_header) == 3:
			# update fixed column names
			self.WORD_COLUMN = fixed_header[0]
			self.RELATION_COLUMN = fixed_header[1]
			self.HEAD_COLUMN = fixed_header[2]
		else:
			raise ValueError('Number of fields (%s) in fixed_header parameter incorrect! Must be 3! Not %d!' % (fixed_header, len(fixed_header))) 

		# build DependencyGraph
		DependencyGraph.__init__(self, data)

		#DependencyGraph adds a 'dummy' node on TOP of tree with data...  {'tag': 'TOP', 'word': None, 'rel': 'TOP', 'deps': [x], 'address': 0}
		# change 'rel' attribute to self.RELATION_COLUMN
		self.nodelist[0][self.RELATION_COLUMN] = self.nodelist[0]['rel']
		del self.nodelist[0]['rel']

		# add level information, height of each node
		self.__add_level_information(self.root, 0)

		#print self.left_children(self.root_index)
		#print self.get_cycle_path(self.nodelist[self.root_index],4)
		#tree = self._tree(self.root_index)
	

	########################################################################
	### Override methods

	def _parse(self, input):
		"""
		Parse input data and builds the dependency graph.
		Input data format must be:

		word	relation	head	[field_name]	[field_name] ....

		@USER	SBJ	2	NN	@USER

		naw	ROOT	0	VBD	naw

		...	...	...	...	...

		@type input: str
		@param input: String with custom Malt-Tab format data representing a dependency graph.
		@raise ValueError: if first three headers ar incorrect.
		"""

		lines = [DependencyGraph._normalize(line) for line in input.split('\n') if line.strip()] 
		temp = [] 
		# header names in first line!
		header = lines[0].split('\t')

		if self.__header_pass(header) == True:			
			# remove header names from the resto of	 lines
			lines = lines[1:]
			for index, line in enumerate(lines): 
				#print index, line 
				try: 
					cells = line.split('\t')
					nrCells = len(cells)
					"""
					if nrCells == 3: 
						word, tag, head = cells 
						rel = '' 
					elif nrCells == 4: 
						word, tag, head, rel = cells 
					elif nrCells == 10: 
						_, word, _, _, tag, _, head, rel, _, _ = cells 
					else: 
						raise ValueError('Number of tab-delimited fields (%d) not supported by CoNLL(10) or Malt-Tab(4) format' % (nrCells)) 

					head = int(head) 
					self.nodelist.append({'address': index + 1, 'word': word, 'tag': tag, 
										'head': head, 'rel': rel, 
										'deps': [d for (d,h) in temp if h == index + 1]}) 
					"""

					node = {}
					attributeIndex = 0
					for attribute in header:
						node[attribute] = cells[attributeIndex]
						attributeIndex = attributeIndex + 1
						if attribute == self.HEAD_COLUMN:
							node[attribute] = int(node[attribute])

					head = int(node[self.HEAD_COLUMN]) 
					node[self.ADDRESS_COLUMN] = index + 1
					node[self.DEPS_COLUMN] = [d for (d,h) in temp if h == index + 1]
					self.nodelist.append(node)

					#print node

					try: 
						self.nodelist[head][self.DEPS_COLUMN].append(index + 1) 
					except IndexError: 
						temp.append((index + 1, head)) 

				except ValueError: 
					break 

			root_address = self.nodelist[0][self.DEPS_COLUMN][0] 
			self.root = self.nodelist[root_address] 
		else:
			raise ValueError('First three header (referring to Word, Relation and Head) fields must be (%s). Actually yout first three data header fields are (%s, %s, %s)' % (header[0:3],self.WORD_COLUMN, self.RELATION_COLUMN, self.HEAD_COLUMN)) 
		

	def tree(self, fields=None):
		"""
		Recursive function for turning dependency graphs into NLTK trees. Return a L{__NODE} structure on each tree node.

		@type fields: list
		@param fields: attributes of node to keep when converting dependency graph into a tree
		@rtype: Tree
		@return: NLTK Tree
		"""

		node = self.root
		deps = node[self.DEPS_COLUMN]

		new_node = node
		if fields != None:
			new_node = self.__keep_node_attributes(node, fields)

		the_tree = Tree(new_node, [self._tree(i, fields) for i in deps])

		# remove 'deps' column
		if fields != None and not self.DEPS_COLUMN in fields:
			del new_node[self.DEPS_COLUMN]

		return the_tree

	def _tree(self, i, fields=None):
		node = self.nodelist[i]
		deps = node[self.DEPS_COLUMN]

		new_node = node
		if fields != None:
			new_node = self.__keep_node_attributes(node, fields)

		if len(deps) == 0:
			if fields != None and not self.DEPS_COLUMN in fields:
				del new_node[self.DEPS_COLUMN]
			return new_node
		else:
			the_tree = Tree(new_node, [self._tree(j, fields) for j in deps])
			if fields != None and not self.DEPS_COLUMN in fields:
				del new_node[self.DEPS_COLUMN]
			return the_tree
	
	########################################################################
	### Private / Internal methods

	def __keep_node_attributes(self, node, attributes_to_keep):
		"""
		Returns a copy of the node containing only the attributes specified in 'attributes_to_keep' parameter.

		@type node: L{__NODE}
		@type attributes_to_keep: list
		@param attributes_to_keep: list of node attributes to keep
		@return: node
		"""
		new_node = {self.DEPS_COLUMN:node[self.DEPS_COLUMN]}
		if attributes_to_keep != None and len(attributes_to_keep) > 0:			
			for node_attribute in attributes_to_keep:
				if node.has_key(node_attribute) == True:
					new_node[node_attribute] = node[node_attribute]
	
		return new_node

	def __header_pass(self, header):
		"""
		Verifies that headers' list contains a WORD, RELATION & HEAD in its first three fields.

		@type header: list
		@param header: List of strings with header names
		@rtype: boolean
		@return: True if test passed, False otherwise		
		@see: Default header values are defined in L{WORD_COLUMN}, L{RELATION_COLUMN}, L{HEAD_COLUMN}
		"""

		if len(header) >= 3:
			if self.HEAD_COLUMN in header and self.RELATION_COLUMN in header and self.WORD_COLUMN in header:
				return True
		else:
			return False

		return False

	def __test_stop_filter(self, node, stop_filter):
		"""
		Tests if a stop filter applies on the node

		@type node: L{__NODE}
		@param node: node to test
		@type stop_filter: dict {'filter_attribute':X, 'filter_value':Y}
		@param stop_filter: conditions for the node

		@rtype: boolean
		@return: True if node matches stop_filter, False otherwise
		"""
		stop = False

		if stop_filter != None and isinstance(stop_filter, dict) == True:
			if stop_filter.has_key(self.STOPFILTER_ATTRIBUTE) == True and stop_filter.has_key(self.STOPFILTER_VALUE) == True:
				if self.__node_has_attribute_value(node, stop_filter[self.STOPFILTER_ATTRIBUTE], stop_filter[self.STOPFILTER_VALUE]) == True:
					stop = True

		return stop

	def __get_leaf_nodes(self, nodelist):
		"""
		Returns only the leaf nodes of a nodelist

		@type nodelist: list of L{__NODE}
		@param nodelist: node list

		@rtype: list of L{__NODE}
		@return: original nodelist passed as parameter with non-leaf nodes removed
		"""
		leaf_nodes = []
		for node in nodelist:
			if node[self.DEPS_COLUMN] == None or len(node[self.DEPS_COLUMN]) == 0:
				leaf_nodes.append(node)

		return leaf_nodes
	
	def __add_level_information(self, node, level):
		"""
		Adds level (height of node) to node information

		@type node: L{__NODE}
		@param node: L{__NODE} with its related information
		@type level: int
		@param level: level to add to node information
		"""
		node['level'] = level
		for children_idx in node[self.DEPS_COLUMN]:
			children = self.nodelist[children_idx]
			self.__add_level_information(children, level+1)


	def __node_has_attribute(self, node, attribute):
		return node.has_key(attribute)

	def __node_has_attribute_value(self, node, attribute, value):
		"""
		Tests if the specified node (dict) contains the pair attribute-value

		@type node: L{__NODE}
		@param node: node of the graph with its related information
		@type attribute: str
		@param attribute: name of the attribute to search
		@type value: str
		@param value: value of the attribute

		@rtype: boolean
		@return: True if node has the pair attribute-value, False otherwise
		"""
		if node.has_key(attribute) == True:
			if node[attribute] == value:
				return True
		else:
			# raise error if attribtue not found in node!
			raise KeyError("Attribute '%s' not found" % (attribute))

		return False

	########################################################################
	### Public methods

	def draw(self, fields=None):
		"""
		Draws the current dependency graph (tree). By default all attribtues of the node are printed.

		@type fields: list
		@param fields: List of node fields to draw
		"""

		tree = self.tree(fields)
		tree.draw()

	def get_sisters(self, node):
		"""
		Gets the sister nodes of node

		@type node: L{__NODE}
		@param node: node

		@rtype: list of L{__NODE}
		@return: list of sister nodes
		"""
		sisters = []

		parent = self.get_parent(node)
		if parent != None:
			children = self.get_children(parent)
			for child in children:
				if node.has_key(self.ADDRESS_COLUMN) == True and child.has_key(self.ADDRESS_COLUMN) == True:
					if node[self.ADDRESS_COLUMN] != child[self.ADDRESS_COLUMN]:
						sisters.append(child)

		return sisters

	def get_parent(self, node):
		"""
		Gets the parent node (dict) of a given node

		@type node: L{__NODE}
		@param node: child node

		@rtype: L{__NODE}
		@return: parent node
		"""

		return self.nodelist[node[self.HEAD_COLUMN]]

	def get_children(self, node):
		"""
		Returns all the child nodes of a given node.

		@type node: L{__NODE}
		@param node: node

		@rtype: list of L{__NODE}
		@return: List of child nodes for the given node
		"""

		#return self.getDescendantsInLevel([], node[self.ADDRESS_COLUMN], node['level'],1)
		return self.get_descendants_at_distance(node, 1)

	def get_ascendant_at_level(self, node, level):
		"""
		Get the ascendant at a given level of the node.

		@type node: L{__NODE}
		@param node: node
		@type level: int
		@param level: number of levels up from node to its ascendant

		@rtype: L{__NODE}
		@return: ascendant node at level 'level' or None if not found
		"""

		if level > node['level']:
			return None

		while level > 0:
			if node[self.HEAD_COLUMN] != 0:
				node = self.nodelist[node[self.HEAD_COLUMN]]
				level = level - 1

		return node

	def get_all_ascendants(self, node):
		"""
		Gets all the ascendants of a given node.

		@type node: L{__NODE}
		@param node: node

		@rtype: list of L{__NODE}
		@return: List of all ascendant nodes for the given node, from its parent to the root of the tree
		"""

		nodeList = []
		while node[self.HEAD_COLUMN] != 0:
			parent = self.nodelist[node[self.HEAD_COLUMN]]
			nodeList.append(parent)
			node = parent

		return nodeList

	def get_all_descendants(self, node, nodeList=[]):
		"""
		Gets all descendants of the given node.

		@type node: L{__NODE}
		@param node: node

		@rtype:
		"""

		for children_index in node['deps']:
			children = self.nodelist[children_index]
			nodeList.append(children)
			self.get_all_descendants(children, nodeList)

		return nodeList

	def __recursive_get_descendants_at_distance(self, node, distance, start_level=None, nodeList=[]):
		"""
		Recursive function for L{self.get_descendants_at_distance} public method.
		"""
		if start_level == None:
			start_level = node['level']

		for childNode in node[self.DEPS_COLUMN]:

			child = self.nodelist[childNode]

			if child['level'] == start_level + distance:
				nodeList.append(child)

			if child['level'] < start_level + distance:
				self.__recursive_get_descendants_at_distance(child, distance, start_level, nodeList)

		return nodeList

	def get_descendants_at_distance(self, node, distance):
		"""
		Gets all descendants of a node at level 'n'

		@type node: L{__NODE}
		@param node: node
		@type distance: int
		@param distance: distance of descendanst from source node

		@rtype: list of L{__NODE}
		@return: list of all descendants nodes of source node at distance 'd'
		"""

		return self.__recursive_get_descendants_at_distance(node, distance, node['level'], [])

	def get_nodes_at_level(self, level):
		"""
		Gets all nodes at level 'n' from root

		@type level: int
		@param level: level of rtype

		@rtype: list of L{__NODE}
		@return: list of all nodes at level 'n' of the tree
		"""
		nodes = []
		for node in self.nodelist:
			if node.has_key('level') == True:
				if node['level'] == level:
					nodes.append(node)

		return nodes

        def get_shortest_path(self, nodeA, nodeB):
            """
            Gets the shortest path between two given nodes

            @type nodeA: L{__NODE}
            @param nodeA: node A
            @type nodeB: L{__NODE}
            @param nodeB: node B

            @rtype: list of L{__NODE}
            @return: list of all nodes composing the shortest path (None if there is no path between both nodes)
            """
            nodeA_all_ascendants_list = self.get_all_ascendants(nodeA)
            nodeB_all_ascendants_list = self.get_all_ascendants(nodeB)

            master_list = []
            slave_list = []
            
            reverse = False
            
            if len(nodeA_all_ascendants_list) > len(nodeB_all_ascendants_list):
                                                 
                master_list = copy.deepcopy(nodeA_all_ascendants_list)
                master_list.insert(0, copy.deepcopy(nodeA))
                
                slave_list = copy.deepcopy(nodeB_all_ascendants_list)
                slave_list.insert(0, copy.deepcopy(nodeB))
                
            else:
                
                master_list = copy.deepcopy(nodeB_all_ascendants_list)
                master_list.insert(0, copy.deepcopy((nodeB.copy())))
                
                slave_list = copy.deepcopy(nodeA_all_ascendants_list)
                slave_list.insert(0, copy.deepcopy(nodeA))
                
                reverse = True
             
            #----------------------------------------------
            '''
            print "\n> master list: ", 
            for node in master_list:
                
                print node

            print "\n> slave list: "
            for node in slave_list:
                
                print node
            '''
            #----------------------------------------------
            rc_node = None

            for nodeM in reversed(master_list):

                if slave_list:
                    
                    nodeS = slave_list[-1]
                    
                    if nodeM[self.ADDRESS_COLUMN] == nodeS[self.ADDRESS_COLUMN]:                
                            
                        nodeM['rn'] = True
                        rc_node = nodeM
                        
                        #print "pop and rc_node = ", rc_node
                        
                        master_list.pop(-1)
                        slave_list.pop(-1)
                        
            if rc_node == None:
                
                return None
            
            shortest_path = []
            shortest_path += master_list
            shortest_path.append(rc_node)
            shortest_path += slave_list[::-1]
            
            if reverse:
                
              shortest_path = shortest_path[::-1]
                        
            return shortest_path

	########################################################################
	### Top-down methods

	#def method_1_1(self, node, attribute, value):
        def have_daughter_with_attr_and_val(self, node, attribute, value):
		"""
		I'm the mother of a node with attribute 'a' and value 'v'

		@type node: L{__NODE}
		@param node: mother node
		@type attribute: str
		@param attribute: attribute name of the child
		@type value: str
		@param value: value of the attribute

		@rtype: boolean, list of L{__NODE}
		@return: True if node found, False otherwise + List of child nodes that matches attribute 'a' with value 'v'
		"""

		relation = False
		nodes_found = []
		# get node childrens
		children = self.get_children(node)
		for child in children:
			if self.__node_has_attribute_value(child, attribute, value) == True:
				relation = True
				dictionary = {'node':child, 'distance_from_node':child['level'] - node['level']}
				nodes_found.append(dictionary)

		return relation, nodes_found

	#def method_1_3(self, node, attribute, value, distance=None, stop_filter=None):
	def have_descendant_with_attr_and_val(self, node, attribute, value, distance=None, stop_filter=None):
		"""
		Have a descendant with has attribute 'a' and value 'v'. Optionally distance from mother to descendant can be specified as well as 
		a stop filter (attribute-value)

		@type node: L{__NODE}
		@param node: node
		@type attribute: str
		@param attribute: attribute name of the descendant
		@type value: str
		@param value: attribute value
		@type distance: int
		@param distance: maximum distance from source node to descendant
		@type stop_filter: dict {'filter_attribute':X, 'filter_value':Y}
		@param stop_filter: stops search when found node that matches it

		@rtype: boolean, list of L{__NODE}
		@return: True if node found, False otherwise + List of child nodes that matches attribute 'a' with value 'v'
		"""

		relation = False
		nodes_found = []

		# distance is optional, if not sepcified, get all descendants
		if distance == None:
			descendants = self.get_all_descendants(node, [])
		else:
			#descendants = self.getDescendantsInLevel([], node[self.ADDRESS_COLUMN], node['level'], distance)
			descendants = self.get_descendants_at_distance(node, distance)
	
		for descendant in descendants:
			if self.__node_has_attribute_value(descendant, attribute, value) == True:
				relation = True
				#node_found = descendant
				dictionary = {'node':descendant, 'distance_from_node':descendant['level'] - node['level']}
				nodes_found.append(dictionary)

		# apply stop filter for each node. steps:
		# 1) find cycle path frmo source node to target node
		# 2) test stop filter on this path
		# 3) if stop filter doesn't apply, add node from 'nodes_found' to 'new_nodes_found'
		new_nodes_found = []
		for result in nodes_found:
			nod = result['node']
			path = self.get_cycle_path(node, nod[self.ADDRESS_COLUMN])
			match_filter = False
			for path_node_index in path:
				path_node = self.nodelist[path_node_index]
				match_filter = match_filter | self.__test_stop_filter(path_node, stop_filter)

			if match_filter == False:
				insert = True
				for ni in new_nodes_found:
					if nod['address'] == ni['node']['address']:
						insert = False
				if insert == True:
					new_nodes_found.append(result)

		return relation, new_nodes_found

	#def method_1_2(self, node, relation_value):
	def have_daughter_in_dep_rel(self, node, relation_value):
		"""
		One of my children has a dependency relation of 'd' with me

		@type node: L{__NODE}
		@param node: node
		@type relation_value: str
		@param relation_value: type of relation

		@rtype: boolean, list of L{__NODE}
		@return: True if node found, False otherwise + List of nodes (children) that has dependency relation with its mother of 'd'
		"""

		relation = False
		nodes_found = []

		children = self.get_children(node)
		for child in children:
			if self.__node_has_attribute_value(child, self.RELATION_COLUMN, relation_value) == True:				
				relation = True
				dictionary = {'node':child, 'distance_from_node':child['level'] - node['level']}
				nodes_found.append(dictionary)

		return relation, nodes_found

	#def method_1_4(self, node, relation_value, distance=None, stop_filter=None):
	def have_descendant_in_dep_rel(self, node, relation_value, distance=None, stop_filter=None):
		"""
		Have a descendant the path of dependencies between us there's a dependency relation 'r'. Optionally maximum distance between node and descendant can be specified
		as well as a stop filter.

		@type node: L{__NODE}
		@param node: node
		@type relation_value: str
		@param relation_value: value of attribute L{RELATION_COLUMN}
		@type distance: int
		@param distance: maximum distance between node and descendant
		@type stop_filter: dict {'filter_attribute':X, 'filter_value':Y}
		@param stop_filter: stops search when found node that matches it

		@rtype: booelan, list of L{__NODE}
		@return: True if relation found, False otherwise + List of nodes 
		"""

		relation = False
		nodes_found = []

		# get ALL descendants
		if distance == None:
			descendants = self.get_all_descendants(node, [])
			# remove not leaf nodes
			new_descendants = []
			for descendant in descendants:
				if descendant[self.DEPS_COLUMN] == []:
					new_descendants.append(descendant)
			descendants = new_descendants
			#print new_descendants
		else:
			# get all descendants at level 'distance' from a node 
			descendants = self.get_descendants_at_distance(node, distance)

		for descendant in descendants:
			# get path (does not include target node)
			path = self.get_cycle_path(node, descendant[self.ADDRESS_COLUMN])
			# append target node to path
			path.append(descendant[self.ADDRESS_COLUMN])
			#reverse path (from target to source)
			path.reverse()
			relation = False
			for node_index in path:
				nodex = self.nodelist[node_index]
				if self.__node_has_attribute_value(nodex, self.RELATION_COLUMN, relation_value) == True:
					#print "found relation %s" % str(nodex)
					#relation = True
					dictionary = {'node':nodex, 'distance_from_node':nodex['level'] - node['level']}
					nodes_found.append(dictionary)
		
		# apply stop filter for each node. steps:
		# 1) find cycle path frmo source node to target node
		# 2) test stop filter on this path
		# 3) if stop filter doesn't apply, add node from 'nodes_found' to 'new_nodes_found'
		new_nodes_found = []
		#relation = False
		for result in nodes_found:
			nod = result['node']
			path = self.get_cycle_path(node, nod[self.ADDRESS_COLUMN])
			match_filter = False
			for path_node_index in path:
				path_node = self.nodelist[path_node_index]
				match_filter = match_filter | self.__test_stop_filter(path_node, stop_filter)

			if match_filter == False:
				insert = True
				for ni in new_nodes_found:
					if nod['address'] == ni['node']['address']:
						insert = False
				if insert == True:
					new_nodes_found.append(result)
					relation = True

		return relation, new_nodes_found

	def get_daughters_with_given_attr_and_value(self, node, attribute, value):
		"""
		Get all daughters which contains a given attribute and value

		@type node: L{__NODE}
		@param node: node
		@type attribute: str
		@param attribute: attribute name
		@type value: str
		@param value: attribute value

		@rtype: list of L{__NODE}
		@return: list of child nodes that contains the given attribute and value or empty list if none
		"""

		daughters = []

		# get children with attribute and value
		test, nodes = self.have_daughter_with_attr_and_val(node, attribute, value)
		if test == True:
			for node in nodes:
				daughters.append(node['node'])

		return daughters

	def get_daughters_in_given_dep_rel(self, node, relation_value, attribute=None, value=None):
		"""
		Get all daughters with dependency relation 'd'. Optionally attribute and value of daughters can be specified.
		If attribute and value are specifieed, all daughters that match dependency relation 'd' but not attribute/value 
		are removed from output list.

		@type node: L{__NODE}
		@param node: node
		@type relation_value: str
		@param relation_value: relation value of field L{RELATION_COLUMN}
		@type attribute: str
		@param attribute: attribute name
		@type value: str
		@param value: attribute value

		@rtype: list of L{__NODE}
		@return: list of daughter nodes that have dependency relation 'd' (and optionally matches attribute and value)
		"""

		daughters = []

		test, nodes = self.have_daughter_in_dep_rel(node, relation_value)
		if test == True:
			print nodes
			#filter results, get only the child who match attribute and value
			if attribute != None and value != None:
				for childnode in nodes:
					child = childnode['node']
					if self.__node_has_attribute_value(child, attribute, value) == True:
						daughters.append(child)
			else:
				for childnode in nodes:
					daughters.append(childnode['node'])

		return daughters

	def get_descendants_with_given_attr_and_value(self, node, attribute, value, distance=None, stop_filter=None):
		"""
		Get all descendants with given attribute and value. Optionally a maximum distance between node and descedant can be specified as well as
		a stop filter (attribute-value).

		@type node: L{__NODE}
		@param node: node
		@type attribute: str
		@param attribute: attribute name
		@type value: str
		@param value: attribute value
		@type distance: int
		@param distance: maximum distance from node to descendant
		@type stop_filter: dict {'filter_attribute':X, 'filter_value':Y}
		@param stop_filter: stops search when found node that matches it

		@rtype: list of L{__NODE}
		@return: list of descendants nodes that match criteria (node contains attribute-value, it's at a maximum distance of 'd' and there isn't any node between us that matches stop_filter)
		"""
		descendants = []

		test, nodes = self.have_descendant_with_attr_and_val(node, attribute, value, distance, stop_filter)
		if test == True:
			for childnode in nodes:
				child = childnode['node']
				descendants.append(child)

		return descendants

	def get_descendants_in_given_dep_rel(self, node, relation_value, attribute=None, value=None, distance=None, stop_filter=None):
		"""
		Get all descendants with dependency relation 'd'. Optionally a maximum distance between node and descendant can be specified, as well as an attribute-value for the descendant and a stop filter.

		@type node: L{__NODE}
		@param node: node
		@type relation_value: str
		@param relation_value: value of L{RELATION_COLUMN}
		@type attribute: str
		@param attribute: attribute of descendant node
		@type value: str
		@param value: attribtue value of descendant node
		@type distance: int
		@param distance: maximum distance between node and descendant node
		@type stop_filter: dict {'filter_attribute':X, 'filter_value':Y}
		@param stop_filter: stops search when found node that matches it

		@rtype: list of L{__NODE}
		@return: list of descendants that match criteria (dependency relation, contains attribute-value, it's at a maximum distance of 'd' and there isn't any node between us that matches stop_filter)
		"""

		descendants = []

		test, nodes = self.have_descendant_in_dep_rel(node, relation_value, distance, stop_filter)
		if test == True:
			# get only the nodes that match attribute and value, both parameters are optional
			if attribute != None and value != None:
				for childnode in nodes:
					child = childnode['node']
					if self.__node_has_attribute_value(child, attribute, value) == True:
						descendants.append(child)
			else:
				for childnode in nodes:
					descendants.append(childnode['node'])

		return descendants

	def get_value_of_given_attr_from_my_daughters(self, node, get_attribute, attribute=None, value=None):
		"""
		Get the value of a given attribute of my daughters. Optionally an attribtue-value for the daughtes can be specified.

		@type node: L{__NODE}
		@param node: node
		@type get_attribute: attribute to retrieve
		@param get_attribute: attribute name to retrieve its value
		@type attribute: str
		@param attribute: attribute name
		@type value: str
		@param value: attribute value

		@rtype: list of objects (str/int/...)
		@return: list of str/int/... (depending on get_attribute type) that match criteria
		"""

		values = []

		if attribute != None and value != None:
			# get all daughters that match attribute-value
			nodes = self.get_daughters_with_given_attr_and_value(node, attribute, value)
		else:
			nodes = self.get_children(node)

		if nodes != None and len(nodes) > 0:
			for node in nodes:
				if self.__node_has_attribute(node, get_attribute) == True:
					values.append(node[get_attribute])

		return values

	def get_dep_rel_from_my_daughters(self, node, attribute=None, value=None):
		"""
		Get the relation value of my daughters. Optionally an attribute and a value of the daughters can be specified.

		@type node: L{__NODE}
		@param node: node
		@type attribute: str
		@param attribute: attribute value of the daughter
		@type value: str
		@param value: attribute value

		@rtype: list of str
		@return: list of relation values of my daughters
		"""
		relations = []

		relations = self.get_value_of_given_attr_from_my_daughters(node, self.RELATION_COLUMN, attribute, value)

		return relations	

	def get_value_of_a_given_attr_from_my_descendants(self, node, get_attribute, distance=None, attribute=None, value=None, stop_filter=None):
		"""
		Get the value of a given attribute from my descendants. Optionally a maximum distance between node and descendant can be specified, as well as
		an attribute-value of the descendant and a stop_filter.

		@type node: L{__NODE}
		@param node: node
		@type get_attribute: str
		@param get_attribute: attribute name to get
		@type distance: int
		@param distance: maximum distance between source node and descendant
		@type attribute: str
		@param attribute: attribute name of the descendant node
		@type value: str
		@param value:  attribute value
		@type stop_filter: dict {'filter_attribute':X, 'filter_value':Y}
		@param stop_filter: stops search when found node that matches it

		@rtype: list of str/int/...
		@return: list of str/int/.. of the nodes attribtue
		"""
		values = []

		# get all descendants or descendants at a given distance
		if distance == None:
			descendants = self.get_all_descendants(node)
			descendants = self.__get_leaf_nodes(descendants)
		else:
			descendants = self.get_descendants_at_distance(node, distance)

		if descendants != None and len(descendants) > 0:
			for desc in descendants:
				desc_to_the_list = True
				# test if descendant has attribtue-value 
				if attribute != None and value != None:					
					desc_to_the_list = self.__node_has_attribute_value(desc, attribute, value)

				# test if in cycle path, stop_filter applies
				if stop_filter != None:
					path = self.get_cycle_path(node, desc[self.ADDRESS_COLUMN])
					path.append(node[self.ADDRESS_COLUMN])
					path.reverse()
					for node_in_path in path:
						node_path = self.nodelist[node_in_path]
						if self.__node_has_attribute_value(node_path, stop_filter[self.STOPFILTER_ATTRIBUTE], stop_filter[self.STOPFILTER_VALUE]) == True:
							desc_to_the_list = False
							break

				# add descendant attribute value to the list
				if desc_to_the_list == True:
					values.append(desc[get_attribute])

		return values

	def get_dep_rel_path_between_me_and_my_descendants(self, node, attribute=None, value=None, distance=None, stop_filter=None):
		"""
		Get the concatenation of relation values in path betweet me and my descendants. Optionally a maximum distance can be specified as wel as
		the tuple attribute-value, and a stop filter.

		@type node: L{__NODE}
		@param node: node
		@type attribute: str
		@param attribute: attribute name
		@type value: str
		@param value: attribute value
		@type distance: int
		@param distance: distance from node to descendant
		@type stop_filter: dict {'filter_attribute':X, 'filter_value':Y}
		@param stop_filter: stops search when found node that matches it

		@rtype: list of lists
		@return: list of lists. a list is returned for each path from descendant to source node
		"""
		paths = []

		# get all descendants or descendants at a given distance
		if distance == None:
			descendants = self.get_all_descendants(node)
			descendants = self.__get_leaf_nodes(descendants)
		else:
			descendants = self.get_descendants_at_distance(node, distance)

		if descendants != None and len(descendants) > 0:
			for desc in descendants:
				desc_to_the_list = True
				# test if descendant has attribtue-value 
				if attribute != None and value != None:					
					desc_to_the_list = self.__node_has_attribute_value(desc, attribute, value)
	
				the_path = self.get_cycle_path(node, desc[self.ADDRESS_COLUMN])
				the_path.append(desc[self.ADDRESS_COLUMN])
				the_path.reverse()
		
				if stop_filter != None:
					for node_in_path_index in the_path:
						node_path = self.nodelist[node_in_path_index]
						if self.__node_has_attribute_value(node_path, stop_filter[self.STOPFILTER_ATTRIBUTE], stop_filter[self.STOPFILTER_VALUE]) == True:
							desc_to_the_list = False
							break					

				if desc_to_the_list == True:
					mini_path = []
					for p in the_path:
						np = self.nodelist[p]
						#mini_path.append(np['word'])
						mini_path.append(np[self.RELATION_COLUMN])

					paths.append(mini_path)

		return paths

	########################################################################
	### Bottom-up methods

	def have_mother_with_attr_and_val(self, node, attribute, value):
		"""
		I'm the daughter of a node that has a given attribute and value

		@type node: L{__NODE}
		@param node: daughter node
		@type attribute: str
		@param attribute: attribute name
		@type value: str
		@param value: attribute value

		@rtype: boolean, parent node
		@return: True if test passed, False otherwise + List of nodes (only parent node)
		"""
		relation = False
		nodes_found = []

		# get parent
		parent = self.get_parent(node)
		# parent has value?
		if self.__node_has_attribute_value(parent, attribute, value) == True:
			relation = True
			dictionary = {'node':parent, 'distance_from_node':node['level'] - parent['level']}
			nodes_found.append(dictionary)

		return relation, nodes_found

	def have_mother_in_dep_rel(self, node, relation_value):
		"""
		My dependency relation with my mother is 'rel'

		@type node: L{__NODE}
		@param node: daughter node
		@type relation_value: str
		@param relation_value: value for the relation column
		@rtype: boolean
		@return: True if my dependency relation with my mother is 'rel', False otherwise

		@see: L{self.RELATION_COLUMN}
		"""
		relation = False

		if self.__node_has_attribute_value(node, self.RELATION_COLUMN, relation_value) == True:
			relation = True

		return relation

	def have_mother_in_dep_rel_with_her_mother(self, node, relation_value):
		"""
		The dependency relation of my mother with his mother is 'rel'

		@type node: L{__NODE}
		@param node: daughter node
		@type relation_value: str
		@param relation_value: value for the relation column
		@rtype: boolean
		@return: True, False

		@see: L{self.RELATION_COLUMN}
		"""
		relation = False

		mother = self.nodelist[node[self.HEAD_COLUMN]]
		if self.__node_has_attribute_value(mother, self.RELATION_COLUMN, relation_value) == True:
			relation = True

		return relation

	def have_ancestor_with_attr_and_val(self, node, attribute, value, distance=None, stop_filter=None):
		"""
		Have an ancestor with a given attribute and value, at, optionally distance 'd' and between us there isn't any node that matches stop_filter

		@type node: L{__NODE}
		@param node: node
		@type attribute: str
		@param attribute: attribute name
		@type value: str
		@param value: attribtue value
		@type distance: int
		@param distance: distance from ascendant to node
		@type stop_filter: dict {'filter_attribute':X, 'filter_value':Y}
		@param stop_filter: stops search when found node that matches it

		@rtype: boolean, list of L{__NODE}
		@return: True if ascendant(s) found, False otherwise + List if ascendants nodes that match criteria
		"""
		relation = False
		nodes_found = []

		if distance == None:
			ascendants = self.get_all_ascendants(node)
		else:
			ascendants = [self.get_ascendant_at_level(node, distance)]

		if ascendants != None:
			for ascendant in ascendants:
				# test if stop filter applies!
				if self.__test_stop_filter(ascendant, stop_filter) == True:
					break

				if self.__node_has_attribute_value(ascendant, attribute, value) == True:
					relation = True
					dictionary = {'node':ascendant, 'distance_from_node':ascendant['level'] - node['level']}
					nodes_found.append(dictionary)

		return relation, nodes_found
	
	def have_ancestor_in_dep_rel(self, node, relation_value, distance=None, stop_filter=None):
		"""
		Have an ancestor (at n levels of distance) with relation attribute value X. And optionally, there isn't any node in the path that matches stop_filter

		@type node: L{__NODE}
		@param node: node
		@type relation_value: str
		@param relation_value: value of L{self.RELATION_COLUMN} node attribute
		@type distance: int
		@param distance: distance from node to ancestor
		@type stop_filter: dict {'filter_attribute':X, 'filter_value':Y}
		@param stop_filter: stops search when found node that matches it

		@rtype: boolean, list of L{__NODE}
		@return: True if ancestor found, False otherwise + List of nodes that matches criteria

		"""
		relation = False
		nodes_found = []

		# get ascendant at level 'distance'
		#ascendant = self.get_ascendant_at_level(node, distance)

		if distance == None:
			#ascendant = self.nodelist[self.root_index]
			ascendant = self.root
		else:
			ascendant = self.get_ascendant_at_level(node, distance)

		path = self.get_cycle_path(ascendant, node[self.ADDRESS_COLUMN])
		path.append(node[self.ADDRESS_COLUMN])
		path.reverse()
		for node_index in path:
			nodex = self.nodelist[node_index]

			# test if stop filter applies!
			if self.__test_stop_filter(nodex, stop_filter) == True:
				relation = False
				nodes_found = []
				break

			if self.__node_has_attribute_value(nodex, self.RELATION_COLUMN, relation_value) == True:
				relation = True
				dictionary = {'node':nodex, 'distance_from_node':nodex['level'] - node['level']}
				nodes_found.append(dictionary)

		#print path

		return relation, nodes_found

	def have_ancestor_in_dep_rel_with_her_mother(self, node, relation_value, distance=None, stop_filter=None):
		"""
		Have an ancestor (at n levels of distance) which has a relation value of X with her mother. A stop filter can be specified.

		@type node: L{__NODE}
		@param node: node
		@type relation_value: str
		@param relation_value: value of L{self.RELATION_COLUMN} attribute
		@type distance: int
		@param distance: distance between node an ancestor's mother
		@type stop_filter: dict {'filter_attribute':X, 'filter_value':Y}
		@param stop_filter: stops search when found node that matches it

		@rtype: boolean, list of L{__NODE}
		@return: True if node found, False otherwise + List of nodes that matches criteria
		"""
		relation = False
		nodes_found = []

		# get ascendant at level 'distance'
		if distance == None:
			ascendant = self.root
		else:
			ascendant = self.get_ascendant_at_level(node, distance)

		path = self.get_cycle_path(ascendant, node[self.ADDRESS_COLUMN])
		path.append(node[self.ADDRESS_COLUMN])
		path.reverse()

		for node_index in path:
			ascendant = self.nodelist[node_index]

			# test if stop filter applies!
			if self.__test_stop_filter(ascendant, stop_filter) == True:
				relation = False
				nodes_found = []
				break

			# has parent
			if ascendant[self.HEAD_COLUMN] != 0:
				if self.__node_has_attribute_value(ascendant, self.RELATION_COLUMN, relation_value) == True:
					relation = True
					dictionary = {'node':ascendant, 'distance_from_node':ascendant['level'] - node['level']}
					nodes_found.append(dictionary)

		return relation, nodes_found

	def get_ancestors_with_given_attr_and_value(self, node, attribute, value, stop_filter=None):
		"""
		Get ancestor nodes with given attribute and value. Optionally, the nodes in the path between the nodes doesn't match the stop filter.

		@type node: L{__NODE}
		@param node: node
		@type attribute: node attribute
		@param attribute: attribute value
		@type stop_filter: dict {'filter_attribute':X, 'filter_value':Y}
		@param stop_filter: stops search when found node that matches it

		@rtype: list of L{__NODE}
		@return: list of nodes that match criteria, and ther is no node in th path between us that matches stop_filter
		"""
		ancestors = []

		test, nodes = self.have_ancestor_with_attr_and_val(node, attribute, value)
		if test == True:
			for nod_row in nodes:
				add_node = True
				nod = nod_row['node']
				the_path = self.get_cycle_path(nod, node[self.ADDRESS_COLUMN])
				the_path.reverse()
				print the_path
				for node_path_index in the_path:
					node_path = self.nodelist[node_path_index]
					if self.__test_stop_filter(node_path, stop_filter) == True:
						add_node = False
						break

				if add_node == True:
					ancestors.append(nod)

				#print nod
		return ancestors

	def get_value_of_given_attr_from_my_mother(self, node, attribute):
		"""
		Get the value of a given attribute of my mother.

		@type node: L{__NODE}
		@param node: node
		@type attribute: str
		@param attribute: attribute name

		@rtype: object
		@return: attribute value of mother node, None otherwise
		"""
		value = None

		mother = self.get_parent(node)
		if self.__node_has_attribute(mother, attribute) == True:
			value = mother[attribute]

		return value

	def get_dep_rel_from_my_mother(self, node):
		"""
		Get the relation attribute of me (with my mother).

		@type node: L{__NODE}
		@param node: node

		@rtype: str
		@return: relation value

		@see: L{self.RELATION_COLUMN}
		"""
		relation = None

		if self.__node_has_attribute(node, self.RELATION_COLUMN) == True:
			relation = node[self.RELATION_COLUMN]

		return relation

	def get_value_of_a_given_attr_from_my_ancestor(self, node, get_attribute, distance=None, attribute=None, value=None, stop_filter=None):
		"""
		Get the attribute value of my ancestors (at distance d, with a given attribute and value, and the nodes in the path between the us doesn't match the stop filter)

		@type node: L{__NODE}
		@param node: node
		@type get_attribute: str
		@param get_attribute: attribtue name to retrieve
		@type attribute: str
		@param attribute: ancestor node attribute
		@type value: str
		@param value: ancestor node attribute value
		@type stop_filter: dict {'filter_attribute':X, 'filter_value':Y}
		@param stop_filter: stops search when found node that matches it

		@rtype: list of object
		@return: list of values of get_attribute of all ancestors that match criteria
		"""

		values = []

		# get root node if distance==None, or ascendant at distance d
		if distance == None:
			ascendants = self.get_all_ascendants(node)
			ascendants.reverse()
			ascendant = ascendants[0]
		else:
			ascendant = self.get_ascendant_at_level(node, distance)

		if ascendant != None:
			#get path
			the_path = self.get_cycle_path(ascendant, node[self.ADDRESS_COLUMN])
			the_path.reverse()
			#print the_path

			for node_path_index in the_path:
				add_node = True
				node_path = self.nodelist[node_path_index]
				#print node_path

				# test if stop_filter applies
				if stop_filter != None:
					add_node = add_node & self.__test_stop_filter(node_path, stop_filter)

				# test if node has attribute
				if attribute != None and value != None:
					add_node = add_node & self.__node_has_attribute_value(node_path, attribute, value)

				#print add_node
				if add_node == True:
					if self.__node_has_attribute(node_path, get_attribute) == True:
						values.append(node_path[get_attribute])

		return values

	def get_dep_rel_path_between_me_and_my_ancestor(self, node, distance=None, attribute=None, value=None, stop_filter=None):
		values = []

		# get root node if distance==None, or ascendant at distance d
		if distance == None:
			ascendants = self.get_all_ascendants(node)
			ascendants.reverse()
			ascendant = ascendants[0]
		else:
			ascendant = self.get_ascendant_at_level(node, distance)

		if ascendant != None:
			the_path = self.get_cycle_path(ascendant, node[self.ADDRESS_COLUMN])
			the_path.reverse()
			#print the_path

			for node_path_index in the_path:
				add_node = True
				node_path = self.nodelist[node_path_index]

				if stop_filter != None:
					add_node = add_node & self.__test_stop_filter(node_path, stop_filter)

				# test if node has attribute
				if attribute != None and value != None:
					add_node = add_node & self.__node_has_attribute_value(node_path, attribute, value)		

				if add_node == True:
					#print node
					#print node_path
					mini_path = self.get_cycle_path(node_path, node[self.ADDRESS_COLUMN])
					mini_path.reverse()
					#print mini_path
					values.append(mini_path)

		return values

	########################################################################
	### Horizontal methods

	def have_sister_with_attr_and_val(self, node, attribute, value):
		"""
		I have a sister with a given attribute and value

		@type node: L{__NODE}
		@param node: node
		@type attribute: str
		@param attribute: attribute name
		@type value: str
		@param value: attribute value
		
		@rtype: boolean, list of L{__NODE}
		@return: True if node found, False otherwise + List of nodes that match criteria
		"""
		relation = False
		nodes_found = []

		# get parent if not ROOT!
		if node[self.HEAD_COLUMN] != 0:
			parent = self.nodelist[node[self.HEAD_COLUMN]]

			# get childs of parent (sisters!)
			sisters = self.get_children(parent)
			for sister in sisters:
				if sister != node:
					if self.__node_has_attribute_value(sister, attribute, value) == True:
						relation = True
						nodes_found.append(sister)

		return relation, nodes_found

	def get_sister_with_given_attr_and_val(self, node, attribute, value):
		"""
		Get sisters of the node with a given attribute and value

		@type node: L{__NODE}
		@param node: node
		@type attribute: str
		@param attribute: attribute name of the sister node
		@type value: str
		@param value: attribute value of the sister node

		@rtype: list of L{__NODE}
		@return: list of nodes (sisters) that match criteria
		"""
		sisters = []

		test, nodes = self.have_sister_with_attr_and_val(node, attribute, value)		
		if test == True:
			for sister in nodes:
				sisters.append(sister)

		return sisters

	def get_value_of_a_given_attr_from_my_syster(self, node, attribute):
		"""
		Get the value of a given attribute of my sisters.

		@type node: L{__NODE}
		@param node: node
		@type attribute: str
		@param attribute: attribute name of the sisters

		@rtype: list of objects
		@return: list of values of the sisters' attribute
		"""
		values = []

		sisters = self.get_sisters(node)
		for sister in sisters:
			values.append(sister[attribute])

		return values

	########################################################################
	### Vertical-horizontal methods

	def have_descendant_with_attr_and_val_whose_sister_has_attr_and_val(self, node, distance, attribute, value, sister_attribute, sister_value):
		"""
		Have a descendant at distance d with a given attribute-value which has a sister with the given attribtue-value

		@type node: L{__NODE}
		@param node: node
		@type distance: int
		@param distance: distance from node to descendant
		@type attribute: str
		@param attribute: descendant attribute name
		@type value: str
		@param value: descendant attribute value
		@type sister_attribute: str
		@param sister_attribute: descendant's sister attribute name
		@type sister_value: str
		@param sister_value: descendant's sister attribute value

		@rtype: boolean, list of L{__NODE}
		@return: True if node found, False otherwise + List of nodes that match criteria

		"""
		relation = False
		result_nodes = []

		first_relation, nodes_found = self.have_descendant_with_attr_and_val(node, attribute, value, distance)
		if first_relation == True and len(nodes_found) > 0:
			for dicty in nodes_found:
				one_node = dicty['node']
				relation, nodes2_found = self.have_sister_with_attr_and_val(one_node, sister_attribute, sister_value)
				desc = {'node':one_node, 'distance_from_node':one_node['level'] - node['level']}
				for nf in nodes2_found:
					dictionary = {'node':nf, 'distance_from_node':nf['level'] - node['level']}
					result_nodes.append((desc, dictionary))

		return relation, result_nodes

	def have_ancestor_with_attr_and_val_whose_sister_has_attr_and_val(self, node, distance, attribute, value, sister_attribute, sister_value):
		"""
		Have an ancestor at distance d with a given attribute-value which has a sister with the given attribtue-value

		@type node: L{__NODE}
		@param node: node
		@type distance: int
		@param distance: distance from node to ancestor
		@type attribute: str
		@param attribute: ancestor attribute name
		@type value: str
		@param value: ancestor attribute value
		@type sister_attribute: str
		@param sister_attribute: ancestor's sister attribute name
		@type sister_value: str
		@param sister_value: ancestor's sister attribute value

		@rtype: boolean, list of L{__NODE}
		@return: True if node found, False otherwise + List of nodes that match criteria

		"""
		relation = False
		result_nodes = []

		first_relation, nodes_found = self.have_ancestor_with_attr_and_val(node, attribute, value, distance)
		if first_relation == True:	
			for dicty in nodes_found:		
				one_node = dicty['node']
				relation, nodes2_found = self.have_sister_with_attr_and_val(one_node, sister_attribute, sister_value)
				asc = {'node':one_node, 'distance_from_node':one_node['level'] - node['level']}
				for nf in nodes2_found:
					dictionary = {'node':nf, 'distance_from_node':nf['level'] - node['level']}
					result_nodes.append((asc, dictionary))

		return relation, result_nodes

if __name__ == "__main__":
  
  dg = DepGraph(data,['word','relation', 'head'])
      
  shortest_path = dg.get_shortest_path(dg.nodelist[17], dg.nodelist[1])

  if shortest_path != None:

    print "\n--------"
                
    for node in shortest_path:
            
      print ">node:",node
        
      #----------------------
        
    print "\n-------------\n"
      
    tag_string = []
  
    for node in shortest_path:    
    
      if "rn" in node:
    
        tag_string.append("")
      
      else:
    
        tag_string.append(node['relation'])
      
    print "|".join(tag_string)
    
	#print dir(dg)
	#print dg.left_children(2), dg.right_children(2)
	#print dg.have_daughter_with_attr_and_val(dg.nodelist[8], "tag", "NNP")
	#print dg.have_descendant_with_attr_and_val(dg.nodelist[8], "tag", "JJ", 2)
	#print dg.have_descendant_with_attr_and_val(dg.nodelist[8], "tag", "CD", distance=None, stop_filter={dg.STOPFILTER_ATTRIBUTE:'tag',dg.STOPFILTER_VALUE:'JJ'})

	#print dg.have_daughter_in_dep_rel(dg.nodelist[8], "VC")
	#print dg.have_descendant_in_dep_rel(dg.nodelist[8], "VMOD", distance=None, stop_filter={dg.STOPFILTER_ATTRIBUTE:'tag',dg.STOPFILTER_VALUE:'VdB'})
	#print dg.get_daughters_with_given_attr_and_value(dg.nodelist[8], "tag", "NNP")
	#print dg.get_daughters_in_given_dep_rel(dg.nodelist[8], "SUB", 'tag', 'NNP')
	#print dg.get_descendants_with_given_attr_and_value(dg.nodelist[8], "tag", "CD", distance=None)
	#print dg.get_descendants_in_given_dep_rel(dg.nodelist[8], "NMOD", attribute="tag", value="CD", stop_filter={dg.STOPFILTER_ATTRIBUTE:'word',dg.STOPFILTER_VALUE:'Vinken'})
	#print dg.get_value_of_given_attr_from_my_daughters(dg.nodelist[8], 'word', attribute='relation', value='SUB')

	#print dg.get_dep_rel_from_my_daughters(dg.nodelist[8])
	#print dg.get_dep_rel_from_my_daughters(dg.nodelist[8])
	#print dg.get_dep_rel_from_my_daughters(dg.nodelist[8])
	#dg.draw()
	#print dg.get_descendants_at_distance(dg.nodelist[8],2)
	#print dg.get_descendants_at_distance(dg.nodelist[8],2)


	#print dg.get_value_of_given_attr_from_my_daughters(dg.nodelist[8], 'word', attribute='relation', value='SUB')
	#print dg.get_dep_rel_from_my_daughters(dg.nodelist[5])

	#print dg.get_value_of_a_given_attr_from_my_descendants(dg.nodelist[8], 'word')

	#print dg.get_dep_rel_path_between_me_and_my_descendants(dg.nodelist[8])
	#print dg.get_sister_with_given_attr_and_val(dg.nodelist[1], 'tag', ',')
	#print dg.get_dep_rel_path_between_me_and_my_ancestor(dg.nodelist[4], distance=None, attribute='tag', value='NNP')

	#dg.draw(['word', 'relation'])

	#print dg.have_mother_with_attr_and_val(dg.nodelist[2], "relation", "ROOT")
	#print dg.have_mother_in_dep_rel(dg.nodelist[2], "SUB")
	#print dg.have_mother_in_dep_rel_with_her_mother(dg.nodelist[1], "SUB")

	#print dg.have_ancestor_with_attr_and_val(dg.nodelist[4], 'lemma', 'be', distance=None, stop_filter={dg.STOPFILTER_ATTRIBUTE:'tag',dg.STOPFILTER_VALUE:'NNS'})
	#print dg.have_ancestor_in_dep_rel(dg.nodelist[4], 'NMOD', distance=2, stop_filter={dg.STOPFILTER_ATTRIBUTE:'relation',dg.STOPFILTER_VALUE:'AMOD'})
	#print dg.have_ancestor_in_dep_rel_with_her_mother(dg.nodelist[4], 'SUB', distance=None, stop_filter={dg.STOPFILTER_ATTRIBUTE:'relation',dg.STOPFILTER_VALUE:'AMOD'})

	#print dg.have_sister_with_attr_and_val(dg.nodelist[8], 'relation', 'NMOD')

	#print dg.have_descendant_with_attr_and_val_whose_sister_has_attr_and_val(dg.nodelist[8], 2, 'relation', 'NMOD', 'relation', 'NMOD')
	#print dg.have_ancestor_with_attr_and_val_whose_sister_has_attr_and_val(dg.nodelist[4], 2, dg.RELATION_COLUMN, 'NMOD', dg.RELATION_COLUMN, 'NMOD')

	#dg.draw(['lemma', 'head', 'de'])
	#newtree = dg.tree(['lemma'])
	#print newtree

	#print dg.get_all_descendants(dg.nodelist[5])
	#print dg.get_all_ascendants(dg.nodelist[10])

	#print dg.getNodesInLevel(1)
	#print dg.getNodesInLevel(0)
	#print dg.get_descendants_at_distance(dg.nodelist[2], 1)
	#print dg.get_children(dg.nodelist[15])
