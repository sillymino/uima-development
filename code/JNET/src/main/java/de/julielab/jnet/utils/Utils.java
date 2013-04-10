/** 
 * IOUtils.java
 * 
 * Copyright (c) 2006, JULIE Lab. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0 
 *
 * Author: tomanek
 * 
 * Current version: 2.3
 * Since version:   2.2
 *
 * Creation date: Nov 1, 2006 
 * 
 * Some more utils for JNET.
 * 
 **/

package de.julielab.jnet.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Utils {

	/**
	 * shuffles the contents of a file on a sentence level
	 * 
	 * @param inputFile
	 * @param outputFile
	 */
	public static void ShuffleFileContents(File inputFile, File outputFile) {
		ArrayList<String> lines = readFile(inputFile);
		Collections.shuffle(lines);
		writeFile(outputFile, lines);
	}

	/**
	 * reads file into ArrayList. Each line is one element (String).
	 * 
	 * @param filename
	 *            full path
	 */
	public static ArrayList<String> readFile(File filename) {
		ArrayList<String> lines = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			try {
				String line = "";
				while ((line = br.readLine()) != null) {
					lines.add(line);
				}
				br.close();
			} catch (IOException e) {
				System.err.println("Read error " + e);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lines;
	}

	/**
	 * writes ArrayList into file. Here, we assume that each element of the
	 * ArrayList is a String, which we write as new line into the file.
	 * 
	 * @param filename
	 *            full path
	 */
	public static void writeFile(File filename, ArrayList lines) {
		try {
			FileWriter fw = new FileWriter(filename);
			for (int i = 0; i < lines.size(); i++)
				fw.write((String) lines.get(i) + "\n");
			fw.close();
		} catch (IOException e) {
			System.err.println("error writing file");
			e.printStackTrace();
		}
	}


	public static void writeFile(File filename, String myString) {
		try {
			FileWriter fw = new FileWriter(filename);
			fw.write(myString + "\n");
			fw.close();
		} catch (IOException e) {
			System.err.println("error writing file");
			e.printStackTrace();
		}
	}

}
