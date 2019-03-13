package com.founder.xy.set.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.founder.e5.commons.Pair;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.web.WebUtil;
import com.founder.e5.workspace.app.form.FormSaver;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;

/**
 * 领导人功能入口
 */
@Controller
@RequestMapping("/xy/leader")
public class LeaderController extends AbstractResourcer {

	/** 表单提交，头像发布到外网 */
	@RequestMapping("formSubmit.do")
	public String formSubmit(HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> model) throws Exception {

		//检查文件名的合法性
		String filePath =  request.getParameter("l_icon");
		if (!isImgFile(filePath)){
			model.put("error", "对不起，请上传jpg,gif,png格式");
			return "/xy/site/error";
		}
		
		int docLibID = docLibID(request);
		long docID = docID(request);
		int siteID = WebUtil.getInt(request, "l_siteID", 0);
		
		//检查站点的资源目录是否已配置
		ResDir siteDir = getSiteDirs(filePath, docLibID, docID, siteID, "l_icon");
		if (siteDir.noSiteDir) {
			model.put("error", "请先检查站点的资源目录设置");
			return "/xy/site/error";
		}
		
		//保存表单
		Pair changed = null;
		FormSaver formSaver = (FormSaver) Context.getBean(FormSaver.class);
		try {
			if (docID == 0) {
				docID = formSaver.handle(request);
			} else {
				changed = formSaver.handleChanged(request);
			}
		} catch (Exception e) {
			model.put("error", e.getLocalizedMessage());
			return "/xy/site/error";
		}
		
		//清除Redis
		String key = RedisManager.getKeyBySite(RedisKey.APP_LEADERLIST_KEY, siteID);
		RedisManager.clearKeys(key);
		RedisManager.clear(RedisKey.APP_LEADER_KEY + docID);
		
		//发布到外网
		pubAndWriteUrl(siteDir, filePath, docLibID, docID, "l_iconUrl");
		
		String url = returnUrl(request, docID, changed);
		return url;
	}

	/** 删除 */
	@RequestMapping("delete.do")
	public String delete(HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> model) throws Exception {
		long[] docIDs = StringUtils.getLongArray(WebUtil.get(request, "DocIDs"));
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		int siteID = WebUtil.getInt(request, "siteID", 0);
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		
		for (long docID : docIDs) {
			docManager.delete(docLibID, docID);
		}
		
		//领导人删除时，清理redis
		String key = RedisManager.getKeyBySite(RedisKey.APP_LEADERLIST_KEY, siteID);
		RedisManager.clearKeys(key);
		
        String url = "/e5workspace/after.do?UUID=" + WebUtil.get(request, "UUID")
        		+ "&DocIDs=" + WebUtil.get(request, "DocIDs");
        return "redirect:" + url;
	}
}