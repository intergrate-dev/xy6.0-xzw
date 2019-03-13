package com.founder.xy.commons;

import java.io.File;

public final class FilePathUtil {
	
	public static String normalPath(String folder, String fileName){
		String separator = "/";
		folder = folder.replace("/", separator);
		folder = folder.replace("\\", separator);
		fileName = fileName.replace("/", separator);
		fileName = fileName.replace("\\", separator);
		if(!folder.endsWith(separator)){
			folder += separator;
		}
		if(fileName.startsWith(separator)){
			fileName = fileName.substring(1);
		}
		return folder + fileName;
	}

	public static void write(String bytes, String path) throws Exception{
		
		File file = new File(path);
		File dir = file.getParentFile();
		if(!dir.exists()){
			dir.mkdirs();
		}
		if(!file.exists()){
			file.createNewFile();
		}
		org.apache.commons.io.FileUtils.writeStringToFile(file, bytes, "utf-8");
		
	}

	public static void write(byte[] bytes, String path) throws Exception {

		File file = new File(path);
		write(bytes, file);

	}

	public static void write(byte[] bytes, File file) throws Exception {

		File dir = file.getParentFile();
		if(!dir.exists()){
			dir.mkdirs();
		}
		if(!file.exists()){
			file.createNewFile();
		}
		org.apache.commons.io.FileUtils.writeByteArrayToFile(file, bytes);
		
	}
}
