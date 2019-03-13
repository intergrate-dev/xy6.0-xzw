package com.founder.xy.commons;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

/**
 * 基于Ant的Zip压缩工具类
 */

public class AntZipUtils {
	
	public static final String FILE_SEPARATOR = File.separator;

    public static final String ENCODING_DEFAULT = "UTF-8";

    public static final int BUFFER_SIZE_DIFAULT = 2048;

    public static void makeZip(String[] inFilePaths, String zipPath)
            throws Exception {
        makeZip(inFilePaths, zipPath, ENCODING_DEFAULT);
    }

    public static void makeZip(
            String[] inFilePaths, String zipPath,
            String encoding) throws Exception {
        File[] inFiles = new File[inFilePaths.length];
        for (int i = 0; i < inFilePaths.length; i++) {
            inFiles[i] = new File(inFilePaths[i]);
        }
        makeZip(inFiles, zipPath, encoding);
    }

    public static void makeZip(File[] inFiles, String zipPath) throws Exception {
        makeZip(inFiles, zipPath, ENCODING_DEFAULT);
    }

    public static void makeZip(File[] inFiles, String zipPath, String encoding)
            throws Exception {
        ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(
                new FileOutputStream(zipPath)));
        zipOut.setEncoding(encoding);
        for (int i = 0; i < inFiles.length; i++) {
            File file = inFiles[i];
            if(file != null)
            doZipFile(zipOut, file, file.getParent());
        }
        zipOut.flush();
        zipOut.close();
    }

    private static void doZipFile(
            ZipOutputStream zipOut, File file,
            String dirPath) throws FileNotFoundException, IOException {
        if (file.isFile()) {
            BufferedInputStream bis = new BufferedInputStream(
                    new FileInputStream(file));
            String zipName = file.getPath().substring(dirPath.length());
//            while (zipName.charAt(0) == '\\' || zipName.charAt(0) == '/') {
            while (zipName.substring(0,1).equals(FILE_SEPARATOR)) {
                zipName = zipName.substring(1);
            }
            ZipEntry entry = new ZipEntry(zipName);
            zipOut.putNextEntry(entry);
            byte[] buff = new byte[BUFFER_SIZE_DIFAULT];
            int size;
            while ((size = bis.read(buff, 0, buff.length)) != -1) {
                zipOut.write(buff, 0, size);
            }
            zipOut.closeEntry();
            bis.close();
        } else {
            File[] subFiles = file.listFiles();
            if (subFiles.length == 0) {  //判断是否为空文件夹，如果为空则添加一个空目录
            	String zipName = file.getPath().substring(dirPath.length());
//                while (zipName.charAt(0) == '\\' || zipName.charAt(0) == '/') {
            	while (zipName.substring(0,1).equals(FILE_SEPARATOR)) {
                    zipName = zipName.substring(1);
                }
//                ZipEntry zipEntry = new ZipEntry(zipName + "/");  
                ZipEntry zipEntry = new ZipEntry(zipName + FILE_SEPARATOR);  
                zipOut.putNextEntry(zipEntry);  
                zipOut.closeEntry();  
                }
            else
	            for (File subFile : subFiles) {
	                doZipFile(zipOut, subFile, dirPath);
	            }
        }
    }

    public static void unZip(String zipFilePath, String storePath)
            throws IOException {
        unZip(new File(zipFilePath), storePath);
    }

    public static void unZip(File zipFile, String storePath) throws IOException {
        if (new File(storePath).exists()) {
            new File(storePath).delete();
        }
        new File(storePath).mkdirs();

        ZipFile zip = new ZipFile(zipFile);


        Enumeration<ZipEntry> entries = zip.getEntries();

        while (entries.hasMoreElements()) {
            ZipEntry zipEntry = entries.nextElement();
            if (zipEntry.isDirectory()) {
                // TODO
            } else {
                String zipEntryName = zipEntry.getName();

                //TODO
                //zipEntryName = zipEntryName.replaceAll("\\\\", "/");
                if (zipEntryName.indexOf(FILE_SEPARATOR) > 0) {

                    String zipEntryDir = zipEntryName.substring(0, zipEntryName
                            .lastIndexOf(FILE_SEPARATOR) + 1);
                    String unzipFileDir = storePath + FILE_SEPARATOR
                            + zipEntryDir;
                    File unzipFileDirFile = new File(unzipFileDir);
                    if (!unzipFileDirFile.exists()) {
                        unzipFileDirFile.mkdirs();
                    }
                }
                FileOutputStream fos = null;
                InputStream is = null;
                try {
                    is = zip.getInputStream(zipEntry);
                    fos = new FileOutputStream(new File(storePath
                                                                + FILE_SEPARATOR + zipEntryName));
                    byte[] buff = new byte[BUFFER_SIZE_DIFAULT];
                    int size;
                    while ((size = is.read(buff)) > 0) {
                        fos.write(buff, 0, size);
                    }
                } catch (IOException e) {

                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (fos != null) {
                            fos.flush();
                            fos.close();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            }
        }
        zip.close();
    }
    /**
     * 解压ZIP文件，并返回解压后文件下的文件路径
     * @param zipFile
     * @param storePath
     * @return
     * @throws IOException
     */
    public static String[] unZipForPath(File zipFile, String storePath) throws IOException {
    	String[] strs = null;
    	
        if (new File(storePath).exists()) {
            new File(storePath).delete();
        }
        new File(storePath).mkdirs();
        ZipFile zip = new ZipFile(zipFile);
        Enumeration<ZipEntry> entries = zip.getEntries();

        while (entries.hasMoreElements()) {
            ZipEntry zipEntry = entries.nextElement();
            String zipEntryName = zipEntry.getName();
            zipEntryName = zipEntryName.replaceAll("\\\\", "/");
            if (zipEntry.isDirectory()) {//解压，判断是否为目录，如果为目录则创建，否则继续
            	 File directory = new File(storePath, zipEntryName);
            	 if(!directory.exists())
            		 directory.mkdirs();
                 directory.setLastModified(zipEntry.getTime());
            } else {
                if (zipEntryName.indexOf(FILE_SEPARATOR) > 0) {

                    String zipEntryDir = zipEntryName.substring(0, zipEntryName
                            .lastIndexOf(FILE_SEPARATOR) + 1);
                    String unzipFileDir = storePath + FILE_SEPARATOR
                            + zipEntryDir;
                    File unzipFileDirFile = new File(unzipFileDir);
                    if (!unzipFileDirFile.exists()) {
                        unzipFileDirFile.mkdirs();
                    }
                }
                FileOutputStream fos = null;
                InputStream is = null;
                try {
                    is = zip.getInputStream(zipEntry);
                    File newFile = new File(storePath + FILE_SEPARATOR + zipEntryName);
                    if(newFile.exists())
                    	newFile.delete();
                    newFile.getParentFile().mkdirs();
                    fos = new FileOutputStream(newFile);
                    byte[] buff = new byte[BUFFER_SIZE_DIFAULT];
                    int size;
                    while ((size = is.read(buff)) > 0) {
                        fos.write(buff, 0, size);
                    }
                } catch (IOException e) {

                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (fos != null) {
                            fos.flush();
                            fos.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        zip.close();
        strs = readfile(storePath, strs);
        return strs;
    }

	private static String[] readfile(String storePath, String[] strs) {
		File file = new File(storePath);
		String str = "";
		if (!file.isDirectory()) {
		        System.out.println("文件");
		        System.out.println("path=" + file.getPath());
		        System.out.println("absolutepath=" + file.getAbsolutePath());
		        System.out.println("name=" + file.getName());

		} else if (file.isDirectory()) {
		        System.out.println("文件夹");
		        String[] filelist = file.list();
		        for (int i = 0; i < filelist.length; i++) {
		        	//TODO 
		                File readfile = new File(storePath + FILE_SEPARATOR + filelist[i]);
		                if (!readfile.isDirectory()) {
		                	if(str!=""){
		                		str +=";";
		                	}
		                	str +="1:"+ readfile.getPath();
		                } else if (readfile.isDirectory()) {
	                        if(str!=""){
		                        str += ";";
		                    }
	                        str +="2:"+ readfile.getPath();
		                }
		        }
		}
		strs = str.split(";");
		return strs;
	}


}