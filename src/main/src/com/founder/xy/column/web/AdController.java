package com.founder.xy.column.web;

import java.net.URLEncoder;
import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.founder.e5.commons.Pair;
import com.founder.e5.context.Context;
import com.founder.e5.workspace.app.form.FormSaver;
import com.founder.xy.commons.web.FilePublishHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.web.SysUser;
import com.founder.e5.web.WebUtil;
import com.founder.e5.workspace.ProcHelper;
import com.founder.e5.workspace.app.form.LogHelper;
import com.founder.xy.column.Column;
import com.founder.xy.column.ColumnReader;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;

/**
 * 广告功能
 * @author Gong Lijie
 */
@Controller
@RequestMapping("/xy/column")
public class AdController {
    @Autowired
    private ColumnReader colReader;

	/**
	 * 复制：广告复制到栏目下
	 */
	@RequestMapping(value = "AdCopy.do")
	public void copy(HttpServletRequest request, HttpServletResponse response,
			@RequestParam int DocLibID, @RequestParam long DocIDs, 
	        @RequestParam String colIDs) throws Exception {
		
		String tCode = InfoHelper.getTenantCode(request);
		SysUser sysUser = ProcHelper.getUser(request);
		int colLibID = LibHelper.getColumnLibID(request);
		int[] colIDArr = StringUtils.getIntArray(colIDs);
		Timestamp now = DateUtils.getTimestamp();
		
		String destColumns = null;
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			Document refAd = docManager.get(DocLibID, DocIDs);
			String colName = getColumnName(colLibID, refAd.getInt("ad_columnID"));
			int type = refAd.getInt("ad_type");
			
			for (int i = 0; i < colIDArr.length; i++) {
				long id = InfoHelper.getNextDocID(DocTypes.AD.typeID());

				Document newAd = docManager.newDocument(refAd, DocLibID, id);
				newAd.setCreated(now);
				newAd.setLastmodified(now);
				newAd.set("ad_columnID", colIDArr[i]);

				docManager.save(newAd);

				//清理redis
				clearAdvKey(type, colIDArr[i], tCode);
				
				//复制后的广告写日志
				LogHelper.writeLog(DocLibID, id, sysUser, "复制", "来自栏目：" + colName + "，ID：" + DocIDs);
				
				//准备给原广告的日志信息
				String destCol = getColumnName(colLibID, colIDArr[i]);
				destColumns = (i == 0) ? destCol : destColumns + "," + destCol;
			}
			// 操作成功后写日志
			InfoHelper.outputText("success复制到：" + destColumns, response);
		} catch (Exception e) {
			InfoHelper.outputText("操作中出现错误：" + e.getLocalizedMessage(), response);
		}
	}
	/**
	 * 读租户的图片尺寸设置
	 */
	@RequestMapping(value = "PicSizes.do")
	public void getPicSizes(HttpServletRequest request, HttpServletResponse response) throws Exception {
		/*
		Tenant tenant = InfoHelper.getTenant(request);
			
		String result = tenant.getPicAdStartSize()
				+ "," + tenant.getPicAdLoopSize()
				+ "," + tenant.getPicAdListSize()
				+ "," + tenant.getPicAdArticleSize()
				+ "," + tenant.getPicAdPicSize()
				;
		InfoHelper.outputText(result, response);
		*/
	}

	/**
	 * 广告删除
	 * 
	 * 清理redis里的key
	 */
	@RequestMapping(value = "AdDelete.do")
	public String delete(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		long[] docIDs = StringUtils.getLongArray(WebUtil.get(request, "DocIDs"));
		
        String url = "/e5workspace/after.do?UUID=" + WebUtil.get(request, "UUID");
        
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		int type = 0;
		int colID = 0;
		try {
			for (long docID : docIDs) {
				Document doc = docManager.get(docLibID, docID);
				type = doc.getInt("ad_type");
				colID = doc.getInt("ad_columnID");
				
				docManager.delete(doc);
			}
			clearAdvKey(type, colID, InfoHelper.getTenantCode(request));
			
        	url += "&DocIDs=" + WebUtil.get(request, "DocIDs");
		} catch (Exception e) {
        	url += "&Info=" + URLEncoder.encode("操作失败：" + e.getLocalizedMessage(), "UTF-8");
		}
		
        return "redirect:" + url;
	}
	/**
	 * 广告提交
	 *
	 * 清理redis里的key
	 */
	@RequestMapping(value = "AdSubmit.do")
	public String AdSubmit(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		long docID = WebUtil.getLong(request, "DocID", 0);
		int siteID = WebUtil.getInt(request, "siteID", 1);

		String url = "/e5workspace/after.do?UUID=" + WebUtil.get(request, "UUID")+ "&DocIDs=" + docID;

		//保存表单
		FormSaver formSaver = (FormSaver) Context.getBean(FormSaver.class);
		Pair changed = null;
		int fileId=0;
		int type = 0;
		int colID = 0;
		try {
			if (docID == 0) {
				docID = formSaver.handle(request);
			} else {
				formSaver.handleChanged(request);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//将图片发布到外网资源目录并保存图片路径
		DocumentManager docManager=DocumentManagerFactory.getInstance();
		Document doc=docManager.get(docLibID,docID);

		String picUrl=doc.getString("ad_picUrl");
		fileId=doc.getInt("ad_linkID");
		type = doc.getInt("ad_type");
		colID = doc.getInt("ad_columnID");
		System.out.println("siteID>>>"+siteID);
		picUrl= FilePublishHelper.pubAndGetUrl(siteID, picUrl);
		doc.set("ad_picUrl",picUrl);
		docManager.save(doc);

		clearAdvKey(type, colID, InfoHelper.getTenantCode(request));
		if(fileId!=0){
			//清除指定稿件id
			clearAdvArticleKey(type,colID,fileId);
		}
		return "redirect:" + url;
	}
	
	//清理redis里的key
	private void clearAdvKey(int type, int colID, String tenantCode) throws E5Exception {
		switch (type) {
		case 0:
			RedisManager.clear(RedisKey.ADV_STARTUP_KEY + tenantCode);
			break;
		case 1:
			RedisManager.clear(RedisKey.ADV_COLUMN_LIST_KEY + colID);
			break;
		case 2:
			RedisManager.clear(RedisKey.ADV_COLUMN_LIST_KEY + colID);
			break;
		case 3:
			RedisManager.clear(RedisKey.ADV_PAGE_KEY + colID);
			break;
		case 4:
			RedisManager.clear(RedisKey.ADV_PAGE_ALBUM_KEY + colID);
			break;
		case 5:
			RedisManager.clear(RedisKey.ADV_COLUMN_LIST_KEY + colID);
			break;
		default:
			break;
		}
	}
	//清理指定稿件广告
	private  void clearAdvArticleKey(int type, int colID,int fileID) throws E5Exception{
		switch (type){
			case 3:
				RedisManager.clear(RedisKey.ADV_PAGE_KEY + colID+"."+fileID);
			case 4:
				RedisManager.clear(RedisKey.ADV_PAGE_ALBUM_KEY + colID+"."+fileID);
		}
	}
	
	private String getColumnName(int colLibID, long colID) throws E5Exception {
	    Column col = colReader.get(colLibID, colID);
	    return (col == null) ? "" : col.getCasNames();
	}
}
