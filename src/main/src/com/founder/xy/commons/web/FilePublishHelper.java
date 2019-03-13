package com.founder.xy.commons.web;

import java.util.UUID;

import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.jpublish.PublishHelper;
import com.founder.xy.set.web.ResDir;

/**
 * 文件发布到外网的工具类
 * @author Gong Lijie
 */
public class FilePublishHelper {

	/**
	 * 发布文件，返回发布地址
	 */
	public static String pubAndGetUrl(int siteID, String filePath) {
		if (!StringUtils.isBlank(filePath)) {
			if (filePath.startsWith("http")) return filePath;
			
			ResDir siteDir = getSiteDirs(siteID);
			if (siteDir.dirs != null) {
				String relativePath = filePath.substring(filePath.indexOf(";") + 1); 
				
				String[] dirs = siteDir.dirs;
				publishTo(filePath, dirs[1], relativePath, dirs[0]);
				
				String iconUrl = dirs[2] + "/" + relativePath;
				return iconUrl;
			}
		}
		return filePath;
	}
	
	/**
	 * 把文件发布到外网指定的网络路径，并生成分发文件
	 * @param srcPath 内网的存储位置。如：附件存储;201506/15/glj_abcccc.jpg
	 * @param destDir 目标目录
	 * @param fileName 送到外网时保存的文件名（带相对路径），如abcccc.jpg，可null，此时保持原相对路径和文件名
	 * @param webRoot 站点根目录，用于生成trans信息文件时做路径参考
	 */
	public static void publishTo(String srcPath, String destDir, String fileName, String webRoot) {
		InfoHelper.copyFile(srcPath, destDir, fileName);
		writeResTrans(srcPath, fileName, destDir, webRoot);
	}
	
    /**
	 * 检查是否图片文件，没有文件时认为正确
	 */
	public static boolean isImgFile(String fileName) {
		if(!StringUtils.isBlank(fileName)){
			String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length())
					.trim().toLowerCase();
			return ( "jpg".equalsIgnoreCase(fileExt)
					|| "png".equalsIgnoreCase(fileExt)
					|| "jpeg".equalsIgnoreCase(fileExt)
					|| "gif".equalsIgnoreCase(fileExt)
					);
		}
		return true;
	}
	
	/**
	 * 生成随机文件名，沿用原来的相对路径
	 * @param fileName
	 */
	public static String randomFileName(String fileName) {
		int pos = fileName.lastIndexOf(".");
		String ext = pos >= 0 ? fileName.substring(pos).toLowerCase() : "";
		
		String relativePath = fileName.substring(fileName.indexOf(";") + 1, fileName.lastIndexOf("/") + 1); 
		return relativePath + UUID.randomUUID() + ext;
	}
	
	/**
	 * 若需要提交文件，先检查站点的资源文件目录是否已设置
	 */
	public static ResDir getSiteDirs(int siteID) {
		ResDir result = new ResDir();
		
		String[] dirs = readSiteInfo(siteID);
		result.noSiteDir = (StringUtils.isBlank(dirs[0]) || StringUtils.isBlank(dirs[1]) || StringUtils.isBlank(dirs[2]));
		result.dirs = dirs;
		return result;
	}
	
	/**
	 * 根据站点得到站点下资源文件的存放目录和发布Url
	 * 返回格式如：["z:\webroot/xy/resource", "http://172.19.33.95/resource"]
	 */
	public static String[] readSiteInfo(int siteID) {
		return InfoHelper.readSiteInfo(siteID);
	}

	/** 写trans文件 */
	private static void writeResTrans(String path, String fileName, String destPath, String siteWebRoot) {
		int pos = path.indexOf(";");
		if (pos < 0) return;
		
		String savePath = path.substring(pos + 1);
		if (!destPath.endsWith("/")) destPath += "/";
		destPath += (fileName == null) ? savePath : fileName;
		
		//trans分发信息
		PublishHelper.writeTransPath(destPath, siteWebRoot);
	}
}
