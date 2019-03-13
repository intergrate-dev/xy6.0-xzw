package com.founder.xy.app;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Component;
/**
 * 
 * @author Feng 解析apk包公共类
 */
@Component
public class ApkUtils {
	/**
	 * 根据apk读取xml中的某信息
	 * 
	 * @throws IOException
	 */
	
	@SuppressWarnings("rawtypes")
	public static Map getApkInfo(String apkPath){
		SAXReader  builder = new SAXReader ();
	    Document document = null;
	    try{
	      document = builder.read(getXmlInputStream(apkPath));
	    }catch (Exception e) {
	      e.printStackTrace();
	    }
	    Element root = document.getRootElement();//跟节点-->manifest
	    

	    String s = root.attributes().toString();
	    String c[] = s.split(",");
	    String versionCode = null;
	    String versionName = null;
	    for(String a: c){
	      if(a.contains("versionCode")){
	        versionCode = a.substring(a.indexOf("versionCode")+19, a.lastIndexOf("\""));
	      }
	      if(a.contains("versionName")){
	        versionName = a.substring(a.indexOf("versionName")+19, a.lastIndexOf("\""));
	      }
	    }
	    Map<String,String> info = new HashMap<>();
	    info.put("versionCode", versionCode);
	    info.put("versionName", versionName);
	    return info;
	  }

	  private static InputStream getXmlInputStream(String apkPath) {
	    InputStream inputStream = null;
	    InputStream xmlInputStream = null;
	    ZipFile zipFile = null;
	    try {
	      zipFile = new ZipFile(apkPath);
	      ZipEntry zipEntry = new ZipEntry("AndroidManifest.xml");
	      inputStream = zipFile.getInputStream(zipEntry);
	      AXMLPrinter xmlPrinter = new AXMLPrinter();
	      xmlPrinter.startPrinf(inputStream);
	      xmlInputStream = new ByteArrayInputStream(xmlPrinter.getBuf().toString().getBytes("UTF-8"));
	    } catch (IOException e) {
	      e.printStackTrace();
	      try {
	        inputStream.close();
	        zipFile.close();
	      } catch (IOException e1) {
	        e1.printStackTrace();
	      }
	    }
	    return xmlInputStream;
	  }

/*
	public String getMd5(File file) throws FileNotFoundException {
		String value = null;
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
			MappedByteBuffer byteBuffer = in.getChannel().map(
					FileChannel.MapMode.READ_ONLY, 0, file.length());
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(byteBuffer);
			BigInteger bi = new BigInteger(1, md5.digest());
			value = bi.toString(16);
			clean(byteBuffer);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != in) {
				try {

					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return value;
	}*/

/*	// publishTo发布在拷贝文件后 buffer仍然有源文件的句柄，文件处于不可删除状态，需要clean
	@SuppressWarnings("unchecked")
	public static void clean(final Object buffer) throws Exception {
		AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				try {
					Method getCleanerMethod = buffer.getClass().getMethod(
							"cleaner", new Class[0]);
					getCleanerMethod.setAccessible(true);
					sun.misc.Cleaner cleaner = (sun.misc.Cleaner) getCleanerMethod
							.invoke(buffer, new Object[0]);
					cleaner.clean();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		});

	}*/
}
