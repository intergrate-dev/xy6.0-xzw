package com.founder.xy.pic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.stereotype.Service;

import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.workspace.ProcHelper;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
/**
 * 图片的管理器
 */
@Service
public class PicManager {

	/**
	 *  插入图片表
	 */
	public String addPic(JSONObject json, List<Map<String, String>> list) throws Exception {
		
		int docLibID = LibHelper.getPicLibID();
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		
		DBSession conn = null;
		String DocIDs = "";
		
    	int size = list.size();
		long groupID = 0;
		try {
	    	conn = Context.getDBSession();
	    	conn.beginTransaction();
			for (int i = 0; i < size; i++) {
				long DocID = InfoHelper.getNextDocID(DocTypes.PHOTO.typeID());
				DocIDs += "," + DocID;
				if (0 == i) {
					groupID = DocID;
				}
				Document doc = docManager.newDocument(docLibID, DocID);
				ProcHelper.initDoc(doc);
				
				doc.set("SYS_DELETEFLAG", i == 0 ? 0 : 2);
				doc.set("SYS_FOLDERID", Integer.parseInt(json.getString("FVID")));
				doc.set("SYS_TOPIC", trim((String)json.get("topic"))); // 组图说明
				doc.set("p_path", list.get(i).get("picPath"));
				doc.set("p_content", trim(list.get(i).get("content"))); // 每个图说明
				doc.set("p_groupOrder", i);
				doc.set("p_groupCount", size);
				doc.set("p_siteID", Integer.valueOf(json.getString("siteID")));
				doc.set("p_catID", json.getLong("p_catID"));
				doc.set("p_groupID", groupID); // 组图ID 设为第一张图的DocID 用于关联删除以及修改
				
				docManager.save(doc, conn);
			}
			conn.commitTransaction();
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			throw new E5Exception(e);
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
		
		return "{\"DocIDs\":\"" + DocIDs.substring(1)
				+ "\",\"docLibID\":\"" + docLibID
				+ "\",\"UUID\":\"" + (String)json.get("UUID") + "\"}";
	}
	
	/**
	 *  编辑图片表编辑按钮调用
	 */
	public List<Map<String, String>> editPic(String sysId) throws Exception {
		
		int docLibID = LibHelper.getPicLibID();
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] rels1 = docManager.find(docLibID, "SYS_DOCUMENTID = ?",
				new Object[]{sysId});
		
		Document[] rels = docManager.find(docLibID, "p_groupID = ? ORDER BY p_groupOrder",
				new Object[]{rels1[0].getInt("p_groupID")}); // 根据组ID找组图
		
		int length = rels.length;
		List<Map<String, String>> list = new ArrayList<Map<String,String>>(length);
		
		Map<String, String> map = null;
		for (int i = 0; i < length; i++) {
			map = new HashMap<String, String>();
			map.put("SYS_DOCUMENTID", String.valueOf(rels[i].getInt("SYS_DOCUMENTID")));
			map.put("p_groupID", String.valueOf(rels[i].getInt("p_groupID")));
			map.put("p_catID", String.valueOf(rels[i].getInt("p_catID")));
			map.put("p_groupOrder", String.valueOf(rels[i].getInt("p_groupOrder")));
			
			map.put("isIndex", getIndex(rels[i].getString("SYS_DELETEFLAG")));
			map.put("topic", rels[i].getString("SYS_TOPIC"));
			map.put("picPath", rels[i].getString("p_path"));
			map.put("pic", "");
			map.put("content", rels[i].getString("p_content"));
			
			map.put("p_articleID", rels[i].getString("p_articleID"));
			
			list.add(map);
		}
		
		return list;
	}
	
	/**
	 *  编辑页面点确定
	 */
	public String modifyPic(JSONObject json, List<Map<String, String>> list) throws Exception {
		
		String groupID = (String)json.get("p_groupID");
		
		int docLibID = LibHelper.getPicLibID();
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] oldPics = docManager.find(docLibID, "p_groupID = ?", new Object[]{groupID});
		
    	int groupCount = list.size();
		DBSession conn = null;
		String DocIDs = "";
		try {
	    	conn = Context.getDBSession();
	    	conn.beginTransaction();
	    	
			for (int i = 0; i < groupCount; i++) {
				Document doc = null;
				//从原来的第一张图开始存，若修改操作中加图片了，才新生成Document
				if (i < oldPics.length)
					doc = oldPics[i];
				else {
					long DocID = InfoHelper.getNextDocID(DocTypes.PHOTO.typeID());
					doc = docManager.newDocument(docLibID, DocID);
					ProcHelper.initDoc(doc);
					
					doc.set("SYS_FOLDERID", json.getInt("FVID"));
					doc.set("p_siteID", json.getInt("siteID")); //所属站点
					doc.set("p_groupID", groupID); //组图ID
					doc.set("p_catID", json.getLong("p_catID")); //图片分类
				}
				DocIDs += "," + doc.getDocID();
				
				doc.set("SYS_DELETEFLAG", i == 0 ? 0 : 2); //第一张图作为索引图
				doc.setTopic(trim(json.getString("topic")));
				doc.set("p_path", list.get(i).get("picPath"));
				doc.set("p_content", trim(list.get(i).get("content"))); // 每个图说明
				doc.set("p_groupOrder", i);
				doc.set("p_groupCount", groupCount);
				
				docManager.save(doc, conn);
			}
			conn.commitTransaction();
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			throw new E5Exception(e);
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
		
		//多余的图片要删除（修改操作中删图片了）
		for (int i = groupCount; i < oldPics.length; i++) {
			docManager.delete(oldPics[i]);
		}
		return "{\"DocIDs\":\"" + DocIDs.substring(1)
				+ "\",\"docLibID\":\"" + docLibID
				+ "\",\"UUID\":\"" + (String)json.get("UUID") + "\"}";
	}
	
	/**
	 *  删除组图
	 */
	public void delPics(int docLibID, long[] docIDs) throws Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		for (long docID : docIDs) {
			Document doc = docManager.get(docLibID, docID);
			if (doc != null) {
				String sql = "delete from " + LibHelper.getLibTable(docLibID) + " where p_groupID=?";
			
				InfoHelper.executeUpdate(docLibID, sql, new Object[]{doc.getInt("p_groupID")});
			}
		}
	}
	
	/**
	 *  获取所有input域数据
	 */
	public Map<String, Object> getFieldMap(HttpServletRequest request) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		
		ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
		upload.setHeaderEncoding("UTF-8");
		upload.setFileSizeMax(1024 * 1024 * 10); // 最大允许上传5M

		@SuppressWarnings("unchecked")
		List<FileItem> items = upload.parseRequest(request);
		int length = items.size();
		
		FileItem item = null;
		for (int i = 0; i < length; i++) {
			item = (FileItem) items.get(i);
			if("Filedata".equals(item.getFieldName())){
				rtnMap.put("Filedata", item);
			}else{
				rtnMap.put(item.getFieldName(), item.getString());
			}
		}
		return rtnMap;
	}
	
	/**
	 * 结果插入SYS_DELETEFLAG字段 0：索引图， 2：非索引图
	 */
	private String getIndex(String isIndex){
		if("0".equals(isIndex)){
			return "checked";
		}
		return "";
	}
	
	/**
	 * 去空格
	 */
	private String trim(String content){
		if(null != content){
			return content.trim();
		}
		return null;
	}
}
