package com.founder.xy.commons.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.founder.e5.cat.CatManager;
import com.founder.e5.cat.CatReader;
import com.founder.e5.cat.Category;
import com.founder.e5.commons.DomHelper;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLibReader;
import com.founder.e5.dom.DocTypeReader;
import com.founder.e5.sys.StorageDeviceManager;
import com.founder.e5.sys.SysFactory;
import com.founder.e5.web.WebUtil;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.jms.PublishTrigger;
import com.founder.xy.jms.data.DocIDMsg;
import com.founder.xy.set.ExtFieldManager;
import com.founder.xy.set.SourceManager;

/**
 * 系统通用的数据方法：
 * 
 * 1）按站点判断名称是否重复
 * 2）彻底删除
 * 
 * @author Gong Lijie
 */
@Controller
@RequestMapping("/xy")
public class DataController {
	@Autowired
	private ExtFieldManager extFieldManager;
	@Autowired
	private SourceManager sourceManager;
	
	/**
	 * 查重（与E5平台的查重相比，增加了站点的条件）
	 * 
	 * 返回值：重复，返回1；不重复，返回0
	 * 
	 * url：xy/Duplicate.do?DocLibID=&DocIDs=&siteID=&value=
	 */
	@RequestMapping(value = "Duplicate.do")
	protected void duplicate(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		long docID = WebUtil.getLong(request, "DocIDs", 0);

		String code = getDocTypeCode(docLibID); //代表文档类型，用来区分名称字段、站点字段
		String value = WebUtil.get(request, "value");
		String field = WebUtil.get(request, "field"); //优先使用指定的查重字段，用于一个表单中有多个查重字段的场景

		DocTypes oneType = Enum.valueOf(DocTypes.class, code);
		if (StringUtils.isBlank(field))
			field = oneType.dupField();

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] docs = null;
		String sql = "";
		if (oneType == DocTypes.DOMAINDIR) {
			//域名目录的重名检查
			long parentID = WebUtil.getLong(request, "parentID", 0);
			sql = field + "=? and dir_parentID=? and SYS_DOCUMENTID<>?";
			docs = docManager.find(docLibID, sql, new Object[] { value, parentID, docID });
			
		} else if (oneType != DocTypes.SOURCE && !StringUtils.isBlank(oneType.groupField())) {
			//若是带分组的文档类型，就检查在分组下是否重复（来源除外）
			int groupID = WebUtil.getInt(request, "groupID", 0);
			sql = field + "=? and " + oneType.groupField() + "=? and SYS_DOCUMENTID<>?";
			docs = docManager.find(docLibID, sql, new Object[] { value, groupID, docID });
			
		} else if (oneType == DocTypes.MOBILEOS || oneType == DocTypes.PAPER) {
			//移动平台设置、报纸：不判断删除了的
			int siteID = WebUtil.getInt(request, "siteID", 0);
			
			sql = field + "=? and " + oneType.siteField() + "=? and SYS_DOCUMENTID<>? and SYS_DELETEFLAG=0";
			docs = docManager.find(docLibID, sql, new Object[] { value, siteID, docID });
		} else {
			int siteID = WebUtil.getInt(request, "siteID", 0);
			
			sql = field + "=? and " + oneType.siteField() + "=? and SYS_DOCUMENTID<>?";
			docs = docManager.find(docLibID, sql, new Object[] { value, siteID, docID });
		}

		String result = (docs == null || docs.length == 0) ? "0" : "1";
		InfoHelper.outputText(result, response);
	}

	/**
	 * 显示用户的上传图片
	 * @param request
	 * @param response
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping(value = "image.do", method = RequestMethod.GET)
	protected void image(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		//获取附件的路径path
		String path = request.getParameter("path");
		//path = new String(path.getBytes("ISO-8859-1"),"UTF-8");
		int pos = path.indexOf(";");
		if (pos < 0) {
			noPic(request, response);
			return;
		}
		
		//类型
		String deviceName = path.substring(0, pos);
		//保存路径
		String savePath = path.substring(pos + 1);

		InputStream in = null;
		OutputStream out = null;
		StorageDeviceManager sdManager = SysFactory.getStorageDeviceManager();
		try {
			//显示图片的header
			String CONTENT_TYPE = "image/jpeg; charset=UTF-8";
			response.setContentType(CONTENT_TYPE);
			response.setHeader("Cache-Control", "no-store");
			response.setHeader("Pragma", "no-cache");
			response.setDateHeader("Expires", 0);
			
			in = sdManager.read(deviceName, savePath);
			out = response.getOutputStream();
			IOUtils.copy(in, out);
		} catch (Exception e) {
			noPic(request, response);
		} finally {
			ResourceMgr.closeQuietly(in);
			ResourceMgr.closeQuietly(out);
		}
	}
	/**
	 * 显示用户的上传图片
	 * @param request
	 * @param response
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping(value = "file.do", method = RequestMethod.GET)
	protected void file(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		//获取附件的路径path
		String path = request.getParameter("path");
		int pos = path.indexOf(";");
		if (pos < 0) {
			noPic(request, response);
			return;
		}
		
		//类型
		String deviceName = path.substring(0, pos);
		//保存路径
		String savePath = path.substring(pos + 1);
		//取文件名
		pos = savePath.lastIndexOf('/');
		String filename = savePath.substring(pos+1);

		InputStream in = null;
		OutputStream out = null;
		StorageDeviceManager sdManager = SysFactory.getStorageDeviceManager();
		try {
			//显示图片的header
			String CONTENT_TYPE = "application/octet-stream; charset=UTF-8";
			response.setContentType(CONTENT_TYPE);
			response.setHeader("Cache-Control", "no-store");
			response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes()));
			response.addHeader("Pragma", "no-cache");
			response.setDateHeader("Expires", 0);
			
			in = sdManager.read(deviceName, savePath);
			out = response.getOutputStream();
			IOUtils.copy(in, out);
		} catch (Exception e) {
			noPic(request, response);
		} finally {
			ResourceMgr.closeQuietly(in);
			ResourceMgr.closeQuietly(out);
		}
	}

	protected void noPic(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			if (request.getParameter("title") != null) {
				response.sendRedirect("../images/notitle.png");
			} else {
				response.sendRedirect("../images/nopic.png");
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	

	/**
	 * 从附件存储路径中拆出附件的文件名。 附件存储路径的格式是“附件存储;201504/18/glj_myfilename.ext”，需要拆出"myfilename.ext"
	 */
	protected String getFileNameFromPath(String savePath) {
		String fileName = savePath.substring(savePath.lastIndexOf("/") + 1);
		fileName = fileName.substring(fileName.indexOf("_") + 1);
		return fileName;
	}
	
	/**
	 * 彻底删除
	 */
	@RequestMapping(value = "Delete.do")
	protected void delete(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		String strDocIDs = WebUtil.get(request, "DocIDs");
		long[] docIDs = StringUtils.getLongArray(strDocIDs);
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		for (long docID : docIDs) {
			docManager.delete(docLibID, docID);
		}
		
		int docTypeID = DomHelper.getDocTypeIDByLibID(docLibID);
		//扩展字段定义、来源：刷新缓存
		if (docTypeID == DocTypes.EXTFIELD.typeID()) {
			PublishTrigger.otherData(docLibID, 0, DocIDMsg.TYPE_EXTFIELD);
		} else if (docTypeID == DocTypes.SOURCE.typeID()) {
			PublishTrigger.otherData(docLibID, 0, DocIDMsg.TYPE_SOURCE);
		}
		
		InfoHelper.outputText("@refresh@", response);
	}
	/**
	 * 分类读取：栏目类型、栏目样式，用于栏目APP属性设置表单中的下拉框
	 */
	@RequestMapping(value = "Cats.do")
	public void readCats(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int catType = WebUtil.getInt(request, "catType", 0);
		if (catType == 0) return;
		
		int siteID = WebUtil.getInt(request, "siteID", 1);
		String tenantCode = InfoHelper.getTenantCode(request);
		
		int rootID = InfoHelper.getSiteCatID(tenantCode, catType, siteID);
		
		CatReader catReader = (CatReader)Context.getBean(CatManager.class);
		Category[] cats = catReader.getSubCats(catType, rootID);
		
		String result = json(cats);
		InfoHelper.outputJson(result, response);
	}
	private String getDocTypeCode(int docLibID) throws E5Exception {
		DocLibReader libReader = (DocLibReader) Context.getBean(DocLibReader.class);
		int docTypeID = libReader.get(docLibID).getDocTypeID();
	
		DocTypeReader docTypeReader = (DocTypeReader) Context.getBean(DocTypeReader.class);
		return docTypeReader.get(docTypeID).getCode();
	}

	private String json(Category[] cats) {
		StringBuilder result = new StringBuilder();
		result.append("[");
		if (cats != null) {
			for (Category cat : cats) {
				if (result.length() > 1) result.append(",");
				result.append("{\"key\":\"").append(String.valueOf(cat.getCatID()))
					.append("\",\"value\":\"").append(InfoHelper.filter4Json(cat.getCatName()))
					.append("\",\"catID\":\"").append(String.valueOf(cat.getCatID()))
					.append("\",\"catName\":\"").append(InfoHelper.filter4Json(cat.getCatName()))
					.append("\"}");
			}
		}
		result.append("]");
		
		return result.toString();
	}
}
