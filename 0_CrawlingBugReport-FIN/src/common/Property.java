package common;

/**
 * Copyright (c) 2014 by Software Engineering Lab. of Sungkyunkwan University. All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its documentation for
 * educational, research, and not-for-profit purposes, without fee and without a signed licensing agreement,
 * is hereby granted, provided that the above copyright notice appears in all copies, modifications, and distributions.
 */
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;


public class Property {
	final static public String SWT = "swt";
	final static public String UI = "ui";
	final static public String JDT = "jdt";
	final static public String BIRT = "birt";	
	final static public String ASPECTJ = "aspectj";

	 private static String targetProduct;
	 private static String targetSince;
	 private static String targetUntil;
	 private static String targetResolution;
	 private static String gitPosition;
	 private static Boolean targetStruct;
	 
	 private static String outputPath;
	
	
		
	private static Property p = null;
		
	private static String readProperty(String key) {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream("blp.properties"));
		} catch (IOException e) {
		}

		return properties.getProperty(key);
	}

	private Property() {
		// Do nothing
	}
	
	public static void createInstance(String targetProduct, String targetSince, String targetUntil, String targetResolution, String gitPosition, String outputPath, Boolean targetStruct) {
		if (null == p) {
			p = new Property(targetProduct, targetSince, targetUntil,targetResolution, gitPosition, outputPath,targetStruct);
		} else {
			p.setValues(targetProduct, targetSince, targetUntil, targetResolution, gitPosition, outputPath,targetStruct);
		}
	}
	
	private void setValues(String targetProduct2, String targetSince, String targetUntil, String targetResolution, String gitPosition, String outputPath, Boolean targetStruct2) {
		setTargetProduct(targetProduct2);
		setTargetSince(targetSince);
		setTargetUntil(targetUntil);
		setTargetResolution(targetResolution);
		setGitPosition(gitPosition);
		setOutputPath(outputPath);
		setTargetStruct(targetStruct2);
		
	}

	public Property(String targetProduct2, String targetSince, String targetUntil, String targetResolution, String gitPosition, String outputPath, Boolean targetStruct2) {
		setTargetProduct(targetProduct2);
		setTargetSince(targetSince);
		setTargetUntil(targetUntil);
		setTargetResolution(targetResolution);
		setGitPosition(gitPosition);
		setOutputPath(outputPath);
		setTargetStruct(targetStruct2);
	}

	public static Property loadInstance(String targetProduct) throws Exception {
		if (null == p) {
			p = new Property();
		}
		
		targetProduct = targetProduct.toUpperCase();
		targetSince = Property.readProperty("TARGET_SINCE");
		targetUntil = Property.readProperty("TARGET_UNTIL");
		targetResolution = Property.readProperty("TARGET_RESOLUTION");
		gitPosition = Property.readProperty("TARGET_GIT_POSITION");		 
		outputPath = Property.readProperty("OUTPUT_XML_PATH");;
		targetStruct = Boolean.parseBoolean(Property.readProperty("TARGET_STRUCT"));
		p.setValues(targetProduct, targetSince, targetUntil, targetResolution, gitPosition, outputPath, targetStruct);
		
		return p;
	}
	
	public static Property loadInstance() throws Exception {
		String targetProduct = Property.readProperty("TARGET_PRODUCT");
		return loadInstance(targetProduct);
	}
	
	public static Property getInstance() throws Exception {
		loadInstance();
		return p;
	}
	

	public static Property getP() {
		return p;
	}

	public static void setP(Property p) {
		Property.p = p;
	}

	public static String getTargetProduct() {
		return targetProduct;
	}

	public void setTargetProduct(String targetProduct) {
		this.targetProduct = targetProduct;
	}
	
	public static String getTargetSince() {
		return targetSince;
	}

	public static void setTargetSince(String targetSince) {
		Property.targetSince = targetSince;
	}

	public static String getTargetUntil() {
		return targetUntil;
	}

	public static void setTargetUntil(String targetUntil) {
		Property.targetUntil = targetUntil;
	}

	public static String getTargetResolution() {
		return targetResolution;
	}

	public static void setTargetResolution(String targetResolution) {
		Property.targetResolution = targetResolution;
	}

	public static String getGitPosition() {
		return gitPosition;
	}

	public static void setGitPosition(String gitPosition) {
		Property.gitPosition = gitPosition;
	}
	

	public static String getOutputPath() {
		return outputPath;
	}

	public static void setOutputPath(String outputPath) {
		Property.outputPath = outputPath;
	}

	public static Boolean getTargetStruct() {
		return targetStruct;
	}

	public static void setTargetStruct(Boolean targetStruct) {
		Property.targetStruct = targetStruct;
	}
	
}
