package org.barcelonamedia.uima;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import org.apache.uima.pear.tools.PackageBrowser;
import org.apache.uima.pear.tools.PackageInstaller;
import org.apache.uima.pear.tools.PackageInstallerException;

public class PearInstallCLI {

	public static void installPear(File installDir, File pearFile, boolean doVerification) {

		try {
			// install PEAR package
			PackageBrowser instPear = PackageInstaller.installPackage(
					installDir, pearFile, doVerification);

			// retrieve installed PEAR data
			// PEAR package classpath
			String classpath = instPear.buildComponentClassPath();
			// PEAR package datapath
			String datapath = instPear.getComponentDataPath();
			// PEAR package main component descriptor
			String mainComponentDescriptor = instPear
			.getInstallationDescriptor().getMainComponentDesc();
			// PEAR package component ID
			String mainComponentID = instPear
			.getInstallationDescriptor().getMainComponentId();
			// PEAR package pear descriptor
			String pearDescPath = instPear.getComponentPearDescPath();

			// print out settings
			System.out.println("PEAR package class path: " + classpath);
			System.out.println("PEAR package datapath: " + datapath);
			System.out.println("PEAR package mainComponentDescriptor: " 
					+ mainComponentDescriptor);
			System.out.println("PEAR package mainComponentID: " 
					+ mainComponentID);
			System.out.println("PEAR package specifier path: " + pearDescPath); 	

		} catch (PackageInstallerException ex) {
			// catch PackageInstallerException - PEAR installation failed
			ex.printStackTrace();
			System.out.println("PEAR installation failed");
		} catch (IOException ex) {
			ex.printStackTrace();
			System.out.println("Error retrieving installed PEAR settings");
		}
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		OptionParser parser = new OptionParser();
		OptionSpec<File> infile = parser.acceptsAll(Arrays.asList("f","file"), "PEAR file")
		.withRequiredArg().ofType( File.class ).describedAs( "filename" );
		OptionSpec<File> outdir = parser.acceptsAll(Arrays.asList("d","dir"), "installation directory")
		.withRequiredArg().ofType( File.class ).describedAs( "directory (default: use PEAR name)" );
		parser.acceptsAll(Arrays.asList("c", "check", "verify"), "do verification");
		parser.acceptsAll(Arrays.asList("v", "verbose"), "be more verbose");
		parser.acceptsAll(Arrays.asList( "h", "?" ), "show help");
		OptionSet options = parser.parse(args);
		if (options.has( "?" ) || !options.has(infile)) {
			parser.printHelpOn( System.out );
			return;
		}
		//assert(options.has("file"));
		//assert(options.has("dir"));
		File pearFile = options.valueOf(infile);
		File installDir;
		if (options.has(outdir)){
			installDir = options.valueOf(outdir);
		} else {
			installDir = new File(pearFile.getName().replace(".pear", ""));
		}
		boolean doVerification = false;
		if (options.has("check")) {
			doVerification = true;
		}
		installPear(installDir,pearFile,doVerification);
	}

}
