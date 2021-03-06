/** 
 * NGramGenerator.java
 * 
 * Copyright (c) 2008, JULIE Lab. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0 
 *
 * Author: tomanek
 * 
 * Current version: 2.3
 * Since version:   2.2
 *
 * Creation date: Feb 27, 2008 
 * 
 * generates different kinds of ngrams
 **/

package de.julielab.jnet.tagger;

import java.util.ArrayList;

public class NGramGenerator {

	
	/**
	 * generates ngrams of all sizes specified in ngramSizes
	 * @param tokens tokens of the sentence
	 * @param currPos the current position relative to which the ngrams are to be build
	 * @param the ngramSizs of the ngrams
	 */
	public static ArrayList<String> generateTokenNGrams(String[] tokens, int currPos, int[] ngramSizes) {
		ArrayList<String> allNGrams = new ArrayList<String>();
		for (int i = 0; i < ngramSizes.length; i++) {
			allNGrams.addAll(generateTokenNGrams(tokens, currPos, ngramSizes[i]));
		}
		return allNGrams;
	}
	
	/**
	 * generates ngrams of size ngramSize
	 * @param tokens tokens of the sentence
	 * @param currPos the current position relative to which the ngrams are to be build
	 * @param ngramSize the size of the ngrams
	 */
	public static ArrayList<String> generateTokenNGrams(String[] tokens, int currPos, int ngramSize) {

		if (currPos > (tokens.length-1))
			return null;
		
		int minStart = Math.max(0, currPos - ngramSize + 1);
		int maxStart = Math.min(currPos, tokens.length - 1);

		ArrayList<String> ngrams = new ArrayList<String>();
		
		for (int i = minStart; i <= maxStart; i++) {
			if (i + ngramSize <= tokens.length) {
				StringBuffer ngram = new StringBuffer();
				for (int j = 0; j < ngramSize; j++) {
					ngram.append(tokens[i + j] + " ");
				}
				ngrams.add(ngram.toString().trim());
			}
		}
		return ngrams;
	}

	public static void main(String[] args) {
		String[] tokens = new String[] { "0", "1", "2", "3", "4","5" };
		//System.out.println(generateTokenNGrams(tokens, 2, 3));
		System.out.println(generateTokenNGrams(tokens, 2, new int[]{2,3,4}));
	}
}
