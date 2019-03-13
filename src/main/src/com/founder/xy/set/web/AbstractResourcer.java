package com.founder.xy.set.web;

import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import com.founder.e5.commons.Pair;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.web.WebUtil;
import com.founder.xy.commons.web.FilePublishHelper;
import com.founder.xy.system.site.DomainDirManager;

/**
 * 把文件发布到外网的公用基类，用在公共资源管理、移动平台设置、领导人头像管理等有发布文件的类
 * @author Gong Lijie
 */
public abstract class AbstractResourcer {
	@Autowired
	protected DomainDirManager dirManager;
	
	/**
	 * 把文件发布到外网指定的网络路径，并生成分发文件
	 * @param srcPath 内网的存储位置。如：附件存储;201506/15/glj_abcccc.jpg
	 * @param destDir 目标目录
	 * @param fileName 送到外网时保存的文件名，如abcccc.jpg
	 * @param webRoot 站点根目录，用于生成trans信息文件时做路径参考
	 */
	protected void publishTo(String srcPath, String destDir, String fileName, String webRoot) {
		FilePublishHelper.publishTo(srcPath, destDir, fileName, webRoot);
	}

	/**
	 * 根据发布目录ID，得到站点的目录、发布目录的目录。
	 * 用于指定发布目录的场合，如公共资源、移动平台设置。
	 * 
	 * 返回格式如：[z:\webroot, /xy/resource]
	 */
	protected String[] getDirs(int dirID) {
		return dirManager.getDirs(dirID);
	}
	
	/**
	 * 检查扩展名
	 * @param fileName
	 * @return
	 */
	protected boolean isExtension(String fileName, String ext) {
		if (!StringUtils.isBlank(fileName)){
			String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length())
					.trim().toLowerCase();
			return(ext.equals(fileExt));
		}
		return false;
	}
	
	/**
	 * 检查是否图片文件，没有文件时认为正确
	 */
	protected boolean isImgFile(String fileName) {
		return FilePublishHelper.isImgFile(fileName);
	}
	
	protected int docLibID(HttpServletRequest request) {
		return WebUtil.getInt(request, "DocLibID", 0);
	}
	
	protected long docID(HttpServletRequest request) {
		return WebUtil.getLong(request, "DocID", 0);
	}
	
	/**
	 * 生成随机文件名，沿用原来的相对路径
	 * @param fileName
	 */
	protected String randomFileName(String fileName) {
		return FilePublishHelper.randomFileName(fileName);
	}
	
	/**
	 * 若需要提交文件，先检查站点的资源文件目录是否已设置
	 */
	protected ResDir getSiteDirs(String filePath, int docLibID, long docID, int siteID, String fieldName) throws E5Exception {
		ResDir result = new ResDir();
		//修改时若改变了头像才发布
		if (!StringUtils.isBlank(filePath)) {
			
			String oldIcon = null;
			Document ownerDoc = null;
			if (docID > 0) {
				DocumentManager docManager = DocumentManagerFactory.getInstance();
				ownerDoc = docManager.get(docLibID, docID);
				oldIcon = ownerDoc.getString(fieldName);
			}
			//若不是原来的文件，则需发布
			if (!filePath.equals(oldIcon)) {
				result = FilePublishHelper.getSiteDirs(siteID);
				result.ownerDoc = ownerDoc;
			}
		}
		return result;
	}
	
	/**
	 * 根据站点得到站点下资源文件的存放目录和发布Url
	 * 返回格式如：["z:\webroot/xy/resource", "http://172.19.33.95/resource"]
	 */
	protected String[] readSiteInfo(int siteID) {
		return FilePublishHelper.readSiteInfo(siteID);
	}

	/**
	 * 发布文件、修改记录中的发布地址
	 */
	protected void pubAndWriteUrl(ResDir siteDir, String filePath, int docLibID, long docID, String fieldName) {
		if (siteDir.dirs != null && !StringUtils.isBlank(filePath)) {
			if (filePath.startsWith("http")) return;
			
			String fileName = randomFileName(filePath); //随机文件名
			
			String[] dirs = siteDir.dirs;
			publishTo(filePath, dirs[1], fileName, dirs[0]);
			
			String iconUrl = dirs[2] + "/" + fileName;
			
			//记录新的发布地址
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			try {
				if (siteDir.ownerDoc == null) siteDir.ownerDoc = docManager.get(docLibID, docID);
				siteDir.ownerDoc.set(fieldName, iconUrl);
				docManager.save(siteDir.ownerDoc);
			} catch (E5Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	/**
	 * 返回的url，调用after.do
	 */
	protected String returnUrl(HttpServletRequest request, long docID, Pair changed) throws Exception {
		String url = "redirect:/e5workspace/after.do?UUID=" + request.getParameter("UUID")
				+ "&DocIDs=" + docID;
		if (changed != null)
			url += "&Opinion=" + URLEncoder.encode(changed.getStringValue(), "UTF-8");
		return url;
	}
}