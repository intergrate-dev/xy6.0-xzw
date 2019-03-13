package com.founder.xy.pic.web;

import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.sys.StorageDevice;
import com.founder.e5.sys.StorageDeviceManager;
import com.founder.e5.sys.SysFactory;
import com.founder.e5.web.WebUtil;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.commons.PicHelper;
import com.founder.xy.pic.PicManager;
import com.founder.xy.system.Tenant;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.apache.commons.fileupload.FileItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

/**
 * 图片的相关操作
 */
@Controller
@RequestMapping("/xy/pic")
public class PicControl {

	@Autowired
	private PicManager picManager;

	/**
	 * 增加图片前处理取得参数
	 */
	@RequestMapping(value = "PreAdd.do")
	public ModelAndView preAdd(HttpServletRequest request,
							   HttpServletResponse response) throws Exception {

		//获取参数向后传
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("siteID", request.getParameter("siteID")); // 所属站点
		model.put("DocLibID", request.getParameter("DocLibID")); // 文档库ID
		model.put("FVID", request.getParameter("FVID")); // 文件夹ID
		model.put("UUID", request.getParameter("UUID"));
		model.put("fromPage", request.getParameter("fromPage"));
		model.put("p_catID", request.getParameter("groupID"));
		model.put("sessionID", request.getSession().getId());
		model.put("isNew", true);

		return new ModelAndView("/xy/pic/PicUpload_new", model);
	}

	/**
	 * flash图片上传处理
	 */
	@RequestMapping(value = "Upload.do")
	public void upload(HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		// 获取参数
		Map<String, Object> fieldMap = picManager.getFieldMap(request);
		String fileName = ((FileItem) fieldMap.get("Filedata")).getName();
		FileItem item = ((FileItem) fieldMap.get("Filedata"));
		// 图片旋转角度0~~~-270	0不动 -90转一次 -180转二次 -270转三次 0转四次
//		String rotation = (String)fieldMap.get("access2008_image_rotation");
		// 随机文件名
//		fileName = UUID.randomUUID().toString() + fileName.substring(fileName.lastIndexOf("."));
		//图片存储设备的名称
		StorageDevice device = InfoHelper.getPicDevice();

		//构造存储的路径和文件名，目录为201505/13/，文件名用uuid
		String savePath = InfoHelper.getPicSavePath(request) + fileName.substring(fileName.lastIndexOf("."));
		//开始存储到存储设备上
		StorageDeviceManager sdManager = SysFactory.getStorageDeviceManager();

		//获得文件流
		InputStream is = ((FileItem) fieldMap.get("Filedata")).getInputStream();

		int pos = fileName.lastIndexOf(".");
		String picSuffix = "";
		if (pos != -1) {
			picSuffix = fileName.substring(fileName.lastIndexOf("."));
		}


		int userMaxSize = 0;
		boolean zipFlag = false;
		String userMaxSizeStr = InfoHelper.getConfig("写稿", "上传图片大小限制");
		if (userMaxSizeStr != null && !"".equals(userMaxSizeStr)) {
			userMaxSize = Integer.parseInt(userMaxSizeStr);
			if (userMaxSize > 0)
				zipFlag = true;
		}
		if (zipFlag && !".gif".equals(picSuffix) && item.getSize() >1024 * 1024 * userMaxSize ) {
			File tmpFile = PicHelper.picZip(is,800,picSuffix);
			is = new FileInputStream(tmpFile);
		}
		try {
			sdManager.write(device, savePath, is);
		} finally {
			ResourceMgr.closeQuietly(is);
		}

		//加抽图任务
		InfoHelper.prepare4Extract(device, savePath);


		// 返回路径  传给challs_flash_onCompleteData
		String picPath = device.getDeviceName() + ";" + savePath;
		JSONObject json = new JSONObject();
		json.accumulate("picPath", picPath.replaceAll("\\\\", "/"));
		json.accumulate("isIndex", "");
		json.accumulate("pic", "");
		json.accumulate("content", "");
		InfoHelper.outputJson(json.toString(), response);
	}

//	/**
//	 *  处理图片排序
//	 */
//	@RequestMapping(value = "Sort.do")
//	public ModelAndView sort(HttpServletRequest request,
//			HttpServletResponse response) throws Exception {
//
//		//获取参数
//		String picInfoList = request.getParameter("data");
//		if(null == picInfoList || "".equals(picInfoList)){
//			picInfoList = "[]";
//		}
//		JSONArray json = JSONArray.fromObject(picInfoList);
//		@SuppressWarnings("unchecked")
//		List<JSONObject> dataArray = (List<JSONObject>)JSONArray.toCollection(json, JSONObject.class);
//		List<Map<String, String>> list = new ArrayList<Map<String,String>>();
//		Map<String, String> map = null;
//		int size = dataArray.size() - 1;
//
//		for (int i = 0; i < size; i++) {
//			map = new HashMap<String, String>();
//			map.put("left", (String)dataArray.get(i).get("left"));
//			map.put("top", (String)dataArray.get(i).get("top"));
//			map.put("isIndex", (String)dataArray.get(i).get("isIndex"));
//			map.put("picPath", (String)dataArray.get(i).get("picPath"));
//			map.put("content", trim((String)dataArray.get(i).get("content")));
//			map.put("pic", (String)dataArray.get(i).get("pic"));
//
//			list.add(map);
//		}
//
//		// 按照图片在页面中top和left来排序
//		Collections.sort(list, new Comparator<Map<String, String>>() {
//			public int compare(Map<String, String> m1, Map<String, String> m2) {
//				int result = new Integer(m1.get("top")).compareTo(new Integer(m2.get("top")));
//				if(result == 0){
//					return new Integer(m1.get("left")).compareTo(new Integer(m2.get("left")));
//				}
//				return result;
//			}
//		});
//
//        Map<String, Object> model = new HashMap<String, Object>();
//        model.put("picInfoList", list);
//        model.put("siteID", (String)dataArray.get(size).get("siteID"));
//        model.put("DocLibID", (String)dataArray.get(size).get("DocLibID"));
//        model.put("FVID", (String)dataArray.get(size).get("FVID"));
//        model.put("UUID", (String)dataArray.get(size).get("UUID"));
//        model.put("topic", (String)dataArray.get(size).get("topic"));
//        model.put("overall", (String)dataArray.get(size).get("overall"));
//        model.put("p_catID", (String)dataArray.get(size).get("p_catID"));
//        model.put("p_groupID", (String)dataArray.get(size).get("p_groupID"));
//        model.put("fromPage", (String)dataArray.get(size).get("fromPage"));
//
//		return new ModelAndView("", model);
//	}

	/**
	 * 上传完成后插入数据库
	 */
	@RequestMapping(value = "InsertDB.do")
	public void insertDB(HttpServletRequest request,
						 HttpServletResponse response) throws Exception {

		JSONArray json = JSONArray.fromObject(request.getParameter("data"));
		@SuppressWarnings("unchecked")
		List<JSONObject> dataArray = (List<JSONObject>)JSONArray.toCollection(json, JSONObject.class);
		List<Map<String, String>> list = new ArrayList<Map<String,String>>();
		Map<String, String> map = null;
		int size = dataArray.size() - 1;

		for (int i = 0; i < size; i++) {
			map = new HashMap<String, String>();
			map.put("left", (String)dataArray.get(i).get("left"));
			map.put("top", (String)dataArray.get(i).get("top"));
			map.put("isIndex", getIndexNum((String)dataArray.get(i).get("isIndex")));
			map.put("picPath", (String)dataArray.get(i).get("picPath"));
			map.put("content", trim((String)dataArray.get(i).get("content")));

			list.add(map);
		}

		// 按照图片在页面中top和left来排序
		Collections.sort(list, new Comparator<Map<String, String>>() {
			public int compare(Map<String, String> m1, Map<String, String> m2) {
				int result = new Integer(m1.get("top")).compareTo(new Integer(m2.get("top")));
				if(result == 0){
					return new Integer(m1.get("left")).compareTo(new Integer(m2.get("left")));
				}
				return result;
			}
		});

		String rtnStr = "";
		String p_groupID = (String)dataArray.get(size).get("p_groupID");
		if(null != p_groupID && !"".equals(p_groupID)){ // 点编辑
			rtnStr = picManager.modifyPic(dataArray.get(size), list);
		}else{ // 新增
			rtnStr = picManager.addPic(dataArray.get(size), list);
		}

		InfoHelper.outputJson(rtnStr, response);	//操作成功
	}

	/**
	 * 删除选中图片
	 */
	@RequestMapping(value = "Delete.do")
	public void delete(HttpServletRequest request,
					   HttpServletResponse response) throws Exception {

		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		long[] docIDs = StringUtils.getLongArray(WebUtil.get(request, "DocIDs"));
		try {
			picManager.delPics(docLibID, docIDs);

			InfoHelper.outputText("@refresh@", response);	//操作成功
		} catch (Exception e) {
			InfoHelper.outputText("@refresh@操作出现错误：" + e.getLocalizedMessage(), response);	//操作失败
		}
	}

	/**
	 * 一览页面点编辑初期处理(设索引图、添加图片说明......)
	 */
	@RequestMapping(value = "EditBtn.do")
	public ModelAndView editBtn(HttpServletRequest request,
								HttpServletResponse response) throws Exception {

		List<Map<String, String>> list = picManager.editPic(request.getParameter("DocIDs"));

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("siteID", request.getParameter("siteID"));
		model.put("DocLibID", request.getParameter("DocLibID"));
		model.put("FVID", request.getParameter("FVID"));
		model.put("UUID", request.getParameter("UUID"));
		model.put("topic", list.get(0).get("topic"));
		model.put("overall", list.get(0).get("overall"));
		model.put("p_catID", list.get(0).get("p_catID"));
		model.put("p_groupID", list.get(0).get("p_groupID"));
		model.put("p_articleID", list.get(0).get("p_articleID"));
		model.put("fromPage", request.getParameter("fromPage"));
		model.put("sessionID", request.getSession().getId());
		model.put("picInfoList", JSONArray.fromObject(list).toString());
		model.put("isNew", false);
		return new ModelAndView("/xy/pic/PicUpload_new", model);
	}

	/**
	 * 去空格 结果插入SYS_DELETEFLAG字段 0：索引图， 2：非索引图
	 */
	private String getIndexNum(String isIndex){
		if("checked".equals(isIndex)){
			return "0";
		}
		return "2";
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

	/**
	 * 取图片库的组图
	 */
	@RequestMapping(value = "getPics.do")
	public void getPics(HttpServletRequest request, HttpServletResponse response,
						@RequestParam("DocID") long docID, @RequestParam("DocLibID") int docLibID)
			throws Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = docManager.get(docLibID, docID);
		int groupID = doc.getInt("p_groupID");
		if (groupID <= 0){
			InfoHelper.outputJson("[]", response);
		}

		String sql = "p_groupID=? order by p_groupOrder";
		Document[] docs = docManager.find(docLibID, sql, new Object[] { groupID });

		String result = "[";
		if (docs != null) {
			for (int j = 0; j < docs.length; j++) {
				if (j > 0)
					result += ",";
				result += "{\"path\":\"" + InfoHelper.filter4Json(docs[j].getString("p_path"))
						+ "\",\"content\":\"" + InfoHelper.filter4Json(docs[j].getString("p_content"))
						+ "\",\"DocLibID\":\"" + docs[j].getDocLibID()
						+ "\",\"DocID\":\"" + docs[j].getDocID() + "\"}";
			}
		}
		result += "]";
		InfoHelper.outputJson(result, response);
	}

	@RequestMapping("checkExtractIsFinished.do")
	public void checkExtractIsFinished(HttpServletRequest request, HttpServletResponse response, String paths) {
		JSONObject json = new JSONObject();
		json.put("code", 0);
		try {
			boolean isFinished = true;
			JSONArray pathJArray = JSONArray.fromObject(paths);
			String path;
			for (int i = 0, len = pathJArray.size(); i < len; i++) {
				path = pathJArray.getString(i);
				//判断文件有没有
				File file = new File(InfoHelper.getFilePathInDevice(path)+".2.jpg");
				if (!file.exists()) {
					isFinished = false;
					break;
				}
			}
			json.put("isFinished", isFinished);
		} catch (Exception e) {
			e.printStackTrace();
			json.put("code", 1);
			json.put("error", e.getMessage());
			json.put("msg", "查询文件时出错！");
		}

		//返回结果
		InfoHelper.outputJson(json.toString(), response);
	}

	@RequestMapping("savePics.do")
	public void savePics(HttpServletResponse response, String data) {
		String result = "";
		JSONObject json = JSONObject.fromObject(data);
		if (json.containsKey("list")) {
			JSONArray array = json.getJSONArray("list");
			List<Map<String, String>> list = JSONArray.toList(array, new HashMap(), new JsonConfig());
			JSONObject doc = json.getJSONObject("doc");
			String p_groupID = doc.getString("p_groupID");
			try {
				if (null != p_groupID && !"".equals(p_groupID)) { // 点编辑
					result = picManager.modifyPic(doc, list);
				} else { // 新增
					result = picManager.addPic(doc, list);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		InfoHelper.outputJson(result, response);    //操作成功
	}
	/**
	 * 取图片库的最新6个上传图片
	 */
	@RequestMapping(value = "getNewPics.do")
	public void getNewPics(HttpServletRequest request, HttpServletResponse response,
						@RequestParam(value="siteID",defaultValue = "1") int siteID)
			throws Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();

		int docLibID= LibHelper.getLibID(DocTypes.PHOTO.typeID(), Tenant.DEFAULTCODE);

		String sql = "p_siteID=? order by SYS_CREATED DESC LIMIT 0,6";
		Document[] docs = docManager.find(docLibID, sql, new Object[] { siteID });

		String result = "[";
		if (docs != null) {
			for (int j = 0; j < docs.length; j++) {
				if (j > 0)
					result += ",";
				result += "{\"path\":\"" + InfoHelper.filter4Json(docs[j].getString("p_path"))
						+ "\",\"content\":\"" + InfoHelper.filter4Json(docs[j].getString("p_content"))
						+ "\",\"DocLibID\":\"" + docs[j].getDocLibID()
						+ "\",\"DocID\":\"" + docs[j].getDocID() + "\"}";
			}
		}
		result += "]";
		InfoHelper.outputJson(result, response);
	}

}