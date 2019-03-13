/**
 * @author guzm
 */
package com.founder.xy.set.web;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.founder.e5.commons.Pair;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.context.Context;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.sys.StorageDeviceManager;
import com.founder.e5.sys.SysFactory;
import com.founder.e5.web.WebUtil;
import com.founder.e5.workspace.app.form.FormSaver;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;

/**
 * 来源管理，表单提交时发布图标。
 * 
 * @author Gong Lijie
 */
@Controller
@RequestMapping("xy/source")
public class SourceController extends AbstractResourcer {

	/** 表单提交，图标发布到外网 */
	@RequestMapping("formSubmit.do")
	public String formSubmit(HttpServletRequest request,
			HttpServletResponse response, Map<String, Object> model)
			throws Exception {

		// 检查文件名的合法性
		String filePath = request.getParameter("src_icon");
		if (!isImgFile(filePath)) {
			model.put("error", "对不起，请上传jpg,gif,png格式");
			return "/xy/site/error";
		}

		int docLibID = docLibID(request);
		long docID = docID(request);
		int siteID = WebUtil.getInt(request, "src_siteID", 0);

		// 检查站点的资源目录是否已配置
		ResDir siteDir = getSiteDirs(filePath, docLibID, docID, siteID,
				"src_icon");
		if (siteDir.noSiteDir) {
			model.put("error", "请先检查站点的资源目录设置");
			return "/xy/site/error";
		}

		// 保存表单
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
		// 发布到外网
		pubAndWriteUrl(siteDir, filePath, docLibID, docID, "src_iconUrl");

		String url = returnUrl(request, docID, changed);
		return url;
	}

    /**
     * 按ID得到来源名
     */
    @RequestMapping(value = "/findSource.do", method = RequestMethod.GET)
    public void findTemplate(
            HttpServletRequest request, HttpServletResponse response,
            int docLibID, long docID) throws Exception {

        DocumentManager docManager = DocumentManagerFactory.getInstance();
        Document doc = docManager.get(docLibID, docID);

        JSONObject json = new JSONObject();
        json.put("id", docID);
        json.put("name", doc.getString("src_name"));
        json.put("url", doc.getString("src_url"));
        
        InfoHelper.outputJson(json.toString(), response);
    }

	/**
	 * 已废弃：改为直接上传图片（南方新闻可能还用）
	 * 
	 * 来源管理新建与修改页面，初始化icon图标
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "initIconListAjax.do", method = RequestMethod.POST)
	public void initIconList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		JSONArray list = new JSONArray();
		Object res_type = request.getParameter("res_type");
		if (res_type != null) {
			// 获取iconlist
			int docLibID = LibHelper.getResourceLibID();
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			// 来源图标在公共资源里面的code是3 是否需要改？ FIXME
			Document[] cols = docManager.find(docLibID,
					"res_type=? and SYS_DELETEFLAG=0",
					new Object[] { res_type });
			// 只要附件列表
			String filePath = "";
			if (cols != null && cols.length > 0) {
				for (Document doc : cols) {
					// filePath = doc.get("res_file") + "";
					// filePath = (filePath).substring(filePath.indexOf(";") +
					// 1);
					filePath = doc.get("res_dir") + "/"
							+ doc.get("res_fileName");
					list.add(filePath);
				}
			}
		}

		InfoHelper.outputJson(list.toString(), response);

	}

	/**
	 * 已废弃（南方新闻可能还用） 显示用户上传图片
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "icon.do", method = RequestMethod.GET)
	protected void icon(HttpServletRequest request, HttpServletResponse response) {
		// 获取附件的路径path
		String path = request.getParameter("path");
		int pos = path.indexOf(";");
		// 类型
		String deviceName = path.substring(0, pos);
		// 保存路径
		String savePath = path.substring(pos + 1);

		InputStream in = null;
		OutputStream out = null;
		StorageDeviceManager sdManager = SysFactory.getStorageDeviceManager();
		try {
			// 显示图片的header
			String CONTENT_TYPE = "image/jpeg; charset=UTF-8";
			response.setContentType(CONTENT_TYPE);
			response.setHeader("Cache-Control", "no-store");
			response.setHeader("Pragma", "no-cache");
			response.setDateHeader("Expires", 0);

			in = sdManager.read(deviceName, savePath);
			out = response.getOutputStream();
			IOUtils.copy(in, out);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(in);
			ResourceMgr.closeQuietly(out);
		}
	}
}
