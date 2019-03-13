package com.founder.xy.paper.web;

import java.io.InputStream;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.Pair;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.doc.util.E5docHelper;
import com.founder.e5.dom.DocLib;
import com.founder.e5.sys.StorageDevice;
import com.founder.e5.sys.StorageDeviceManager;
import com.founder.e5.sys.SysFactory;
import com.founder.e5.web.SysUser;
import com.founder.e5.web.WebUtil;
import com.founder.e5.web.util.UploadHelper;
import com.founder.e5.workspace.ProcHelper;
import com.founder.e5.workspace.app.form.LogHelper;
import com.founder.xy.article.Article;
import com.founder.xy.article.ArticleManager;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.FormViewHelper;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.config.Channel;
import com.founder.xy.config.ConfigReader;
import com.founder.xy.jms.PublishTrigger;
import com.founder.xy.jpublish.paper.PaperLayout;
import com.founder.xy.paper.PaperManager;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;
import com.founder.xy.set.web.AbstractResourcer;
import com.founder.xy.set.web.ResDir;
import com.founder.xy.system.Tenant;
import com.founder.xy.system.site.Site;
import com.founder.xy.system.site.SiteUserReader;
import com.founder.xy.workspace.form.FormSaver;

/**
 * 报纸相关操作
 * 
 * @author Gong Lijie
 */
@Controller
@RequestMapping("/xy/paper")
public class PaperController extends AbstractResourcer {

	@Autowired
	private PaperManager paperManager;

	@Autowired
	ArticleManager articleManager;
	/**
	 * 广告标记（用于标记报纸稿件是否为广告稿件）
	 */
	@RequestMapping(value = "Mark.do")
	public String mark(HttpServletRequest request, HttpServletResponse response) {

		String strDocIDs = WebUtil.get(request, "DocIDs");
		long[] docIDs = StringUtils.getLongArray(strDocIDs);
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = null;
		StringBuilder DocIDs = new StringBuilder();
		String layoutId=null;
		try {
			for(int i=0;i<docIDs.length;i++){
				doc = docManager.get(docLibID, docIDs[i]);
				boolean isKeep = !doc.isKeep();
				doc.setKeep(isKeep);
				docManager.save(doc);
				if (DocIDs.length() > 0)
					DocIDs.append(",");
				DocIDs.append(docIDs[i]);
				layoutId=doc.getString("a_layoutID");
			}
			RedisManager.clear(RedisKey.APP_PAPER_ARTICLELIST_KEY+layoutId);

		} catch (E5Exception e) {
			e.printStackTrace();
		}

		return "redirect:/e5workspace/after.do?UUID="
				+ WebUtil.get(request, "UUID") + "&DocIDs=" + DocIDs.toString();
	}

	/**
	 * 报纸删除
	 * 
	 * 清理redis里的key
	 */
	@RequestMapping(value = "Delete.do")
	public String deletePaper(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		long docID = WebUtil.getLong(request, "DocIDs", 0);

		String result = paperManager.delete(docLibID, docID);

		String url = "/e5workspace/after.do?UUID="
				+ WebUtil.get(request, "UUID");
		if (result == null)
			url += "&DocIDs=" + docID;
		else
			url += "&Info=" + URLEncoder.encode("操作失败：" + result, "UTF-8");
		return "redirect:" + url;
	}

	/**
	 * 版面删除
	 * 
	 * 删除版面、版面附件、版面的稿件、版面稿件的附件 清理redis里的key
	 */
	@RequestMapping(value = "DeleteLayout.do")
	public String deleteLayout(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		//long docID = WebUtil.getLong(request, "DocIDs", 0);
		long[] docIDs = StringUtils.getLongArray(WebUtil.get(request, "DocIDs"));

		List<Long> docIDlist = new ArrayList<>();
		String result = null;
		for(long docID : docIDs){
			result = paperManager.layoutDelete(docLibID, docID);
			if (result == null) 
				docIDlist.add(docID);
		}
		String url = "/e5workspace/after.do?UUID="
				+ WebUtil.get(request, "UUID");
		if (docIDlist.size() > 0)
			url += "&DocIDs=" + StringUtils.join(docIDlist.toArray(new Long[docIDlist.size()]), ",");
		else
			url += "&Info=" + URLEncoder.encode("操作失败：" + result, "UTF-8");
		return "redirect:" + url;
	}

	/**
	 * 刊期删除
	 * 
	 * 参照版面删除清理刊期下所有版面的数据，再删除刊期表数据。 清理redis里的key
	 */
	@RequestMapping(value = "DeleteDate.do")
	public void deletePaperDate(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		int layoutLibID = WebUtil.getInt(request, "layoutLibID", 0);
		int paperID = WebUtil.getInt(request, "paper", 0);
		String paperDate = WebUtil.get(request, "date");

		// 得到报纸库ID
		int paperLibID = LibHelper.getLibIDByOtherLib(DocTypes.PAPER.typeID(),
				layoutLibID);

		// 调用删除操作
		String result = paperManager.revokeByDate(paperLibID, paperID, paperDate);

		// 在报纸上添加操作日志
		if (result == null) {
			LogHelper.writeLog(paperLibID, paperID,
					ProcHelper.getUser(request), "删除刊期", "刊期:" + paperDate);
		}

		InfoHelper.outputText(result, response);
	}

	/**
	 * 报纸稿件删除
	 * 
	 * 删除附件 清理redis里的key
	 */
	@RequestMapping(value = "DeleteArticle.do")
	public String deleteArticle(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		long[] docIDs = StringUtils
				.getLongArray(WebUtil.get(request, "DocIDs"));

		StringBuilder successDocIDs = new StringBuilder();
		String result = null;
		for (long docID : docIDs) {
			result = paperManager.articleDelete(docLibID, docID);
			if (result != null)
				break;
			if (successDocIDs.length() > 0)
				successDocIDs.append(",");
			successDocIDs.append(docID);
		}

		String url = "/e5workspace/after.do?UUID="
				+ WebUtil.get(request, "UUID");
		if (successDocIDs.length() > 0)
			url += "&DocIDs=" + successDocIDs.toString();
		if (result != null)
			url += "&Info=" + URLEncoder.encode("操作失败：" + result, "UTF-8");
		return "redirect:" + url;
	}

	/**
	 * 报纸稿件下版
	 * 撤回发布页面，将稿件状态设置为已下版
	 * 清除redis中的key
	 */
	@RequestMapping(value = "RevokeArticle.do")
	public String revokeArticle(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		long[] docIDs = StringUtils
				.getLongArray(WebUtil.get(request, "DocIDs"));

		StringBuilder successDocIDs = new StringBuilder();
		String result = null;
		for (long docID : docIDs) {
			result = paperManager.revokeArticle(docLibID, docID);
			if (result != null)
				break;
			if (successDocIDs.length() > 0)
				successDocIDs.append(",");
			successDocIDs.append(docID);
		}

		String url = "/e5workspace/after.do?UUID="
				+ WebUtil.get(request, "UUID");
		if (successDocIDs.length() > 0)
			url += "&DocIDs=" + successDocIDs.toString() + "&Info="
					+ URLEncoder.encode("操作完成。请重发版面。", "UTF-8");
		;
		if (result != null)
			url += "&Info=" + URLEncoder.encode("操作失败：" + result, "UTF-8");
		return "redirect:" + url;
	}

	/**
	 * 版面重新发布
	 */
	@RequestMapping(value = "Publish.do")
	public String publish(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		long docID = WebUtil.getLong(request, "DocIDs", 0);

		paperManager.layoutPublish(docLibID, docID);

		String url = "/e5workspace/after.do?UUID="
				+ WebUtil.get(request, "UUID") + "&DocIDs=" + docID + "&Info="
				+ URLEncoder.encode("操作完成", "UTF-8");
		return "redirect:" + url;
	}

	/**
	 * 报纸稿件上版，没有真正发布，只是将状态设置为未发布，需要重发版面才能发布
	 */
	@RequestMapping(value = "PublishArticle.do")
	public String publishArticle(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		long[] docIDs = StringUtils
				.getLongArray(WebUtil.get(request, "DocIDs"));
		StringBuilder successDocIDs = new StringBuilder();
		String result = null;
		for (long docID : docIDs) {
			result = paperManager.publishArticle(docLibID, docID);
			if (result != null)
				break;
			if (successDocIDs.length() > 0)
				successDocIDs.append(",");
			successDocIDs.append(docID);
			//同时上版转版的稿件
			paperManager.publishTransArticle(docLibID, docID, successDocIDs);
		}
		

		String url = "/e5workspace/after.do?UUID="
				+ WebUtil.get(request, "UUID");
		if (successDocIDs.length() > 0)
			url +=  "&DocLibID="+ docLibID +
					"&DocIDs=" + successDocIDs.toString() + "&Info="
					+ URLEncoder.encode("操作完成。请重发版面。", "UTF-8");
		if (result != null)
			url += "&Info=" + URLEncoder.encode("操作失败：" + result, "UTF-8");
		return "redirect:" + url;
	}

	/**
	 * 重新发布一期报纸，重发首页、版面页和版面下未发布和已发布状态的稿件、更新xml文件
	 */
	@RequestMapping(value = "PublishDate.do")
	public void publishDate(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		int layoutLibID = WebUtil.getInt(request, "layoutLibID", 0);
		int paperID = WebUtil.getInt(request, "paper", 0);
		String paperDate = WebUtil.get(request, "date");

		// 得到报纸库ID
		int paperLibID = LibHelper.getLibIDByOtherLib(DocTypes.PAPER.typeID(),
				layoutLibID);

		paperManager.pubByDate(paperLibID, paperID, paperDate);

		// 在报纸上添加操作日志
		LogHelper.writeLog(paperLibID, paperID, ProcHelper.getUser(request),
				"刊期发布", "刊期:" + paperDate);

		InfoHelper.outputText(null, response);
	}

    /**
     * 撤一期报纸，暂不启用
     */
    @RequestMapping(value = "RevokeDate.do")
    public void revokeDate(HttpServletRequest request,
                            HttpServletResponse response) throws Exception {
        int layoutLibID = WebUtil.getInt(request, "layoutLibID", 0);
        int paperID = WebUtil.getInt(request, "paper", 0);
        String paperDate = WebUtil.get(request, "date");

        // 得到报纸库ID
        int paperLibID = LibHelper.getLibIDByOtherLib(DocTypes.PAPER.typeID(),
                layoutLibID);

        paperManager.revokeByDate(paperLibID, paperID, paperDate);

        // 在报纸上添加操作日志
        LogHelper.writeLog(paperLibID, paperID, ProcHelper.getUser(request),
                "刊期撤回", "刊期:" + paperDate);

        InfoHelper.outputText(null, response);
    }

	/**
	 * 版面撤版，撤回版面和版面下的稿件页，并将版面和稿件状态设置为撤版/下版
	 */
	@RequestMapping(value = "RevokeLayout.do")
	public String revokeLayout(HttpServletRequest request,
							   HttpServletResponse response) throws Exception {
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		long docID = WebUtil.getLong(request, "DocIDs", 0);

		paperManager.revokeLayout(docLibID, docID);

		String url = "/e5workspace/after.do?UUID="
				+ WebUtil.get(request, "UUID") + "&DocIDs=" + docID + "&Info="
				+ URLEncoder.encode("操作完成，请重发本期报纸", "UTF-8");
		return "redirect:" + url;
	}


	/**
	 * 版面发布页查看
	 */
	@RequestMapping(value = "View.do")
	public ModelAndView view(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		long docID = WebUtil.getLong(request, "DocIDs", 0);

		String[] urls = paperManager.layoutUrls(docLibID, docID);

		Map<String, Object> model = new HashMap<String, Object>();
		if (!StringUtils.isBlank(urls[0]))
			model.put("path0", urls[0]);
		if (!StringUtils.isBlank(urls[1]))
			model.put("path1", urls[1]);

		// 复用栏目的查看
		return new ModelAndView("/xy/article/ColumnView", model);
	}

	/** 报纸表单保存，发布图标 */
	@RequestMapping(value = "formSubmit.do")
	public String formSubmit(HttpServletRequest request,
			HttpServletResponse response, Map<String, Object> model)
			throws Exception {

		// 检查上传图标的文件名合法性
		String filePath0 = request.getParameter("pa_iconBig");
		String filePath1 = request.getParameter("pa_iconSmall");
		if (!isImgFile(filePath0) || !isImgFile(filePath1)) {
			model.put("error", "对不起，请上传jpg,gif,png格式");
			return "/xy/site/error";
		}

		int docLibID = docLibID(request);
		long docID = docID(request);
		int siteID = WebUtil.getInt(request, "pa_siteID", 0);

		// 检查站点的资源目录是否已配置
		ResDir siteDir0 = null, siteDir1 = null;
		siteDir0 = getSiteDirs(filePath0, docLibID, docID, siteID, "pa_iconBig");
		if (!siteDir0.noSiteDir)
			siteDir1 = getSiteDirs(filePath1, docLibID, docID, siteID,
					"pa_iconSmall");
		if (siteDir0.noSiteDir || siteDir1.noSiteDir) {
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

		// 清除Redis
		RedisManager.clear(RedisKey.APP_PAPER_KEY + siteID);

		// 发布到外网
		pubAndWriteUrl(siteDir0, filePath0, docLibID, docID, "pa_iconBig");
		pubAndWriteUrl(siteDir1, filePath1, docLibID, docID, "pa_iconSmall");

		String url = returnUrl(request, docID, changed);
		return url;
	}

	/**
	 * 报纸稿件修改
	 */
	@RequestMapping(value = "Article.do")
	public ModelAndView article(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();

		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		long docID = WebUtil.getLong(request, "DocIDs", 0);
		String uuid = WebUtil.get(request, "UUID");

		// 取出定制的表单，数组里0是js和css等引用文件语句，1是form里的字段内容
		String[] jsp = FormViewHelper.getFormJsp(docLibID, docID,
				"formPaperArticle", uuid);
		model.put("formHead", jsp[0]);
		model.put("formContent", jsp[1]);

		return new ModelAndView("/xy/paper/Article", model);
	}

	/**
	 * 报纸稿件图片修改
	 */
	@RequestMapping(value = "Attachment.do")
	public ModelAndView attachment(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		int docLibID = 0;

		long docID = WebUtil.getLong(request, "DocIDs", 0);
		String uuid = WebUtil.get(request, "UUID");
		List<Object> list = new ArrayList<Object>();
		int paperAttDocLibID = LibHelper.getLibID(
				DocTypes.PAPERATTACHMENT.typeID(), Tenant.DEFAULTCODE);
		DocumentManager documentManager = DocumentManagerFactory.getInstance();
		Document[] docs = documentManager
				.find(paperAttDocLibID,
						"att_articleID=? and att_type=0 and sys_deleteflag =0 order by att_order asc",
						new Object[] { docID });
		List<Integer> documentIDs = new ArrayList<Integer>();
		for (int i = 0; i < docs.length; i++) {
			Document doc = docs[i];
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("SYS_DOCUMENTID", doc.getInt("SYS_DOCUMENTID"));
			documentIDs.add(doc.getInt("SYS_DOCUMENTID"));
			map.put("SYS_FOLDERID", doc.getInt("SYS_FOLDERID"));
			map.put("att_articleID", doc.getInt("att_articleID"));
			map.put("att_articleLibID", doc.getInt("att_articleLibID"));
			map.put("att_path", doc.getString("att_path"));
			String content = doc.getString("att_content");
			if (content.indexOf("<p>") != -1 && content.indexOf("</p>") != -1) {
				content = content.substring((content.indexOf("<p>") + 3),
						content.indexOf("</p>"));
			}
			map.put("att_content", content);
			map.put("att_order", doc.getInt("att_order"));
			docLibID = doc.getInt("SYS_DOCLIBID");
			list.add(map);
		}
		model.put("list", list);
		model.put("documentIDs", documentIDs.toString().replace("[", "")
				.replace("]", ""));
		model.put("docLibID", docLibID);
		model.put("uuid", uuid);
		return new ModelAndView("/xy/paper/Attachment", model);
	}

	/**
	 * 报纸稿件图片修改保存
	 */
	@RequestMapping(value = "SaveAttachment.do")
	public void saveAttachment(HttpServletRequest request,
			HttpServletResponse response) {
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		String data = WebUtil.getStringParam(request, "data");
		if ("".equals(data) || data == null) {
			InfoHelper.outputText("empty", response);
			return;
		}
		String[] documentIDs = data.split(","); // 注意分隔符是需要转译
		DBSession conn = null;
		IResultSet rs = null;
		try {
			conn = E5docHelper.getDBSession(docLibID);
			conn.beginTransaction();
			String dateLib = LibHelper.getLibTable(docLibID);
			String sql = "UPDATE "
					+ dateLib
					+ " SET att_path = ?, att_content = ?,att_order = ? WHERE SYS_DOCUMENTID = ?";
			for (String id : documentIDs) {
				id = id.trim();
				String content = request.getParameter("Att_Content" + id);
				if (content.indexOf("<p>") == -1
						&& content.indexOf("</p>") == -1) {
					content = "<p>" + content + "</p>";
				}
				InfoHelper.executeUpdate(sql,
						new Object[] { request.getParameter("Path" + id),
								content,
								request.getParameter("Att_Order" + id), id },
						conn);
			}
			conn.commitTransaction();
			String url = "e5workspace/after.do?UUID="
					+ WebUtil.get(request, "UUID");
			InfoHelper.outputText(url, response);
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			InfoHelper.outputText("error", response);
			LogHelper.writeLog(docLibID, docID(null), request, "保存到数据库失败");
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}
	}

	/**
	 * 上传图片至报纸存储
	 */
	@RequestMapping(value = "Data.do")
	public void uploadPic(HttpServletRequest request,
			HttpServletResponse response) {
		String savePath = null;
		InputStream in = null;
		InputStream in1 = null;
		StorageDevice device = null;
		try {
			FileItem file = UploadHelper.getFileItem(request);

			String fullName = file.getName();

			// 附件存储设备的名称
			device = InfoHelper.getPaperDevice();

			// 构造存储的路径和文件名，文件名用uuid
			String path = WebUtil.getStringParam(request, "docPath");
			if ("".equals(path) || path == null) {
				return;
			}
			String reg = "(\\d{4})\\/(\\d{1,2})\\/(\\d{1,2})\\/";// 日期正则表达式
			Pattern pattern = Pattern.compile(reg);
			Matcher matcher = pattern.matcher(path);// 使用正则表达式判断日期
			while (matcher.find()) {
				path = matcher.group();
				break;
			}
			savePath = path + UUID.randomUUID().toString()
					+ fullName.substring(fullName.lastIndexOf("."));
			String savePath1 = savePath + ".1";
			// 开始存储到存储设备上
			StorageDeviceManager sdManager = SysFactory
					.getStorageDeviceManager();
			in = file.getInputStream();
			in1 = file.getInputStream();
			sdManager.write(device, savePath, in);
			sdManager.write(device, savePath1, in1);
		} catch (Exception e) {
			// 0表示失败
			String result = "0;" + e.getLocalizedMessage();
			InfoHelper.outputText(result, response);
		} finally {
			ResourceMgr.closeQuietly(in);
			ResourceMgr.closeQuietly(in1);
		}

		// 1表示成功，返回“存储设备;路径”
		String result = "1;" + device.getDeviceName() + ";" + savePath;
		result = result.replaceAll("\\\\", "/");
		InfoHelper.outputText(result, response);
	}

	/**
	 * 报纸稿件选用
	 * 如果op=1，则可跨站点选用
	 */
	@RequestMapping(value = { "Select.do" })
	public ModelAndView select(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		String strDocIDs = WebUtil.get(request, "DocIDs");
		int op = WebUtil.getInt(request, "op", 0);

		Channel[] allChs = ConfigReader.getChannels();
		List<Channel> chs = new ArrayList<Channel>();
		for (int i = 0; i < allChs.length; i++) {
			chs.add(allChs[i]);
		}
		model.put("channels", chs);
		model.put("channelCount", chs.size());
		model.put("DocLibID", docLibID);
		model.put("DocIDs", strDocIDs);
		model.put("UUID", WebUtil.get(request, "UUID"));
		model.put("siteID", WebUtil.get(request, "siteID"));
		if(op==1){
			SiteUserReader siteUserReader = (SiteUserReader) Context.getBean("siteUserReader");
			List<Site> siteList = siteUserReader.getSites(LibHelper.getUserExtLibID(request), ProcHelper.getUserID(request));
			model.put("siteList", siteList);
			model.put("siteCount", siteList.size());
			model.put("isSiteSelect", true);
		}else{
			model.put("isSiteSelect", false);
		}

		return new ModelAndView("/xy/paper/Select", model);
	}
	
	/**
	 * 报纸稿件选用：选用的提交
	 */
	@RequestMapping(value = "paperSelect.do")
	public void paperSelect(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		long[] docIDs = StringUtils
				.getLongArray(WebUtil.get(request, "DocIDs"));
		SysUser sysUser = ProcHelper.getUser(request);
		String tenantCode = InfoHelper.getTenantCode(request);

		List<DocLib> articleLibs = LibHelper.getLibs(DocTypes.ARTICLE.typeID(),
				tenantCode);

		JSONObject params = JSONObject.fromObject(WebUtil.get(request,
				"paramData"));

		StringBuilder successDocIDs = new StringBuilder();
		String error = null;
		for (long docID : docIDs) {
			error = saveArticles(params, docLibID, docID, articleLibs, sysUser);
			if (error != null)
				break;

			if (successDocIDs.length() > 0)
				successDocIDs.append(",");
			successDocIDs.append(docID);
		}

		if (error == null) {
			InfoHelper.outputText("ok", response);
		} else {
			InfoHelper.outputText("Failed", response);
		}
	}

	/**
	 * 报纸的叠管理
	 */
	@RequestMapping(value = "Piles.do")
	public String piles(HttpServletRequest request,
			HttpServletResponse response, Map<String, Object> model)
			throws Exception {
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		long docID = WebUtil.getLong(request, "DocIDs", 0);

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document paper = docManager.get(docLibID, docID);

		String piles = WebUtil.get(request, "piles");
		if (piles != null) {
			// 是提交保存
			paper.set("pa_piles", piles);
			docManager.save(paper);

			String url = "/e5workspace/after.do?UUID="
					+ WebUtil.get(request, "UUID") + "&DocIDs=" + docID;
			return "redirect:" + url;
		} else {
			// 是操作窗口显示
			piles = paper.getString("pa_piles");
			model.put("piles", piles);

			return "/xy/paper/Piles";
		}
	}

	/**
	 * 版面的修改叠操作
	 */
	@RequestMapping(value = "PileChange.do")
	public String pileChange(HttpServletRequest request,
			HttpServletResponse response, Map<String, Object> model)
			throws Exception {
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		long docID = WebUtil.getLong(request, "DocIDs", 0);

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document layout = docManager.get(docLibID, docID);

		String pile = WebUtil.get(request, "pile");
		if (pile != null) {
			// 是提交保存
			layout.set("pl_pile", pile);
			docManager.save(layout);

			String url = "/e5workspace/after.do?UUID="
					+ WebUtil.get(request, "UUID") + "&DocIDs=" + docID;
			return "redirect:" + url;
		} else {
			// 是操作窗口显示
			int paperLibID = LibHelper.getLibID(DocTypes.PAPER.typeID(),
					InfoHelper.getTenantCode(request));
			int paperID = layout.getInt("pl_paperID");
			Document paper = docManager.get(paperLibID, paperID);

			model.put("piles", paper.getString("pa_piles")); // 报纸的所有叠
			model.put("pile", layout.getString("pl_pile")); // 当前所在叠

			return "/xy/paper/PileChange";
		}
	}

	private String saveArticles(JSONObject form, int docLibID, long docID,
			List<DocLib> articleLibs, SysUser sysUser) throws Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document paperArticle = docManager.get(docLibID, docID);

		List<Document> chArticles = new ArrayList<Document>();
		// 取出租户下的发布库。渠道代号是从0开始，正好与发布库一一对应
		StringBuffer paperArticleLog = new StringBuffer();

		Channel[] chs = ConfigReader.getChannels();
		int siteID = getInt(form, "siteID");
		for (int i = 0; i < chs.length; i++) {
			if (chs[i] == null)
				continue;
			int id = chs[i].getId();
			// 勾选渠道
			boolean checked = "on".equals(getString(form, "channel" + id));
			if (checked) {
				// 选择的主栏目ID
				int colID = getInt(form, id + "_columnID");
				if (colID > 0) {
					long newID = InfoHelper.getNextDocID(DocTypes.ARTICLE
							.typeID());
					Document chArticle = docManager.newDocument(paperArticle,
							articleLibs.get(i).getDocLibID(), newID);

					chArticle.setFolderID(0); // 清空文件夹ID，以便initDoc方法赋值
					chArticle.set("a_url", ""); //清空PC版发布地址
					chArticle.set("a_urlPad", ""); //清空触屏版发布地址
					ProcHelper.initDoc(chArticle);
					chArticle.set("a_columnID", colID);
					chArticle.set("a_column", getString(form, id + "_column"));
					chArticle.set("a_siteID", siteID);

					// 设置关联栏目
					String relColID = getString(form, id + "_columnRelID");
					if (relColID != null) {
						chArticle.set("a_columnRelID", relColID);
						chArticle.set("a_columnRel",
								getString(form, id + "_columnRel"));
					}
					chArticle.set("a_pubTime", DateUtils.getTimestamp()); // 发布时间
					chArticle.set("a_linkTitle", paperArticle.getTopic());
					// 设置顺序
					double order = articleManager.getNewOrder(chArticle);
					chArticle.set("a_order", order);

					// 所有栏目设置
					articleManager.setColumnAll(chArticle);
					articleManager.tryPublish(chArticle, colID,
							sysUser.getRoleID());

					if (paperArticleLog.length() != 0) {
						paperArticleLog.append("；");
					}

					paperArticleLog.append("签发到").append(chs[i].getName())
							.append("发布库");

					// 给发布库稿件设置a_channel,a_channel值等于2的id次方
					chArticle.set("a_channel", (int) Math.pow(2, id));

					chArticles.add(chArticle);
				}
			}
		}
		String error = saveChannels(chArticles, paperArticle);

		if (error == null) {
			// 写发布库稿件日志
			for (Document article : chArticles) {
				LogHelper.writeLog(article.getDocLibID(), article.getDocID(),
						sysUser, "数字报选用", "");
			}
			// 写数字报稿件库日志
			LogHelper.writeLog(docLibID, docID, sysUser, "选用",
					paperArticleLog.toString());
			// 触发发布
			for (Document article : chArticles) {
				if (Article.STATUS_PUB_ING == article.getInt("a_status")) {
					PublishTrigger.article(article);
				}
			}
			return null;
		}
		return error;
	}

	/**
	 * 把所有渠道的稿件保存到数据库，作为一个事务
	 */
	private String saveChannels(List<Document> articleList,
			Document paperArticle) {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DBSession conn = null;

		try {
			conn = E5docHelper.getDBSession(articleList.get(0).getDocLibID());
			conn.beginTransaction();
			for (Document article : articleList) {
				saveAttachments(article, paperArticle, conn);
				docManager.save(article, conn);
			}
			// 提交transaction
			conn.commitTransaction();
			return null;
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			e.printStackTrace();
			return "操作中出现错误：" + e.getLocalizedMessage();
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
	}

	private void saveAttachments(Document article, Document paperArticle, DBSession conn) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] attas = docManager.find(
				LibHelper.getLibIDByOtherLib(DocTypes.PAPERATTACHMENT.typeID(),
						paperArticle.getDocLibID()),
				"att_articleLibID=? and att_articleID=? and att_type=0",
				new Object[] { paperArticle.getDocLibID(),paperArticle.getDocID() });
		if (attas != null) {
			StringBuilder pics = new StringBuilder() ;
			for (Document atta : attas) {
				long docID = InfoHelper.getNextDocID(DocTypes.ATTACHMENT.typeID());
				Document newatta = docManager.newDocument(atta,LibHelper.getAttaLibID(), docID);
				newatta.set("att_articleLibID", article.getDocLibID());
				newatta.set("att_articleID", article.getDocID());

				newatta.set("att_objID", 0); // 对应的图片ID
				newatta.set("att_objLibID", 0);

				docManager.save(newatta, conn);
				pics.append("<p style=\"text-align:center\">").append("<img src=\"../../xy/image.do?path=");
				pics.append(atta.getString("att_path"));
				pics.append("\" style=\"max-width: 100%;\"/>").append("</p>");
			}
			article.set("a_content",pics.append(article.getString("a_content")).toString());
		}
	}
	
	/**
	 * 复制时保存复制稿件的附件
	 */
	private void copyAttachments(Document article, Document paperArticle,
			DBSession conn) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] attas = docManager.find(
				LibHelper.getLibIDByOtherLib(DocTypes.PAPERATTACHMENT.typeID(),
						paperArticle.getDocLibID()),
				"att_articleLibID=? and att_articleID=? and att_type=0",
				new Object[] { paperArticle.getDocLibID(),
						paperArticle.getDocID() });
		if (attas != null) {
			for (Document atta : attas) {
				long docID = InfoHelper.getNextDocID(DocTypes.PAPERATTACHMENT
						.typeID());
				Document copyatta = docManager.newDocument(atta,
						atta.getDocLibID(), docID);
				copyatta.set("att_articleLibID", article.getDocLibID());
				copyatta.set("att_articleID", article.getDocID());

				copyatta.set("att_objID", 0); // 对应的图片ID
				copyatta.set("att_objLibID", 0);

				docManager.save(copyatta, conn);
			}
		}
	}

	/**
	 * 复制报纸稿件
	 */
	@RequestMapping(value = "Copy.do")
	public void copy(HttpServletRequest request, HttpServletResponse response,
			@RequestParam String DocIDs, @RequestParam String DocLibID,
			@RequestParam long colID) throws Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		int docLibID = Integer.parseInt(DocLibID);
		long[] docIDs = StringUtils.getLongArray(DocIDs);
		if (docIDs.length <= 0) {
			InfoHelper.outputText("Failed", response);
			LogHelper.writeLog(docLibID, docID(null), request, "文档ID为空");
			return;
		}
		// 稿件的复制
		// 取出现有的主栏目ID，判断选中的是否是自己所述的主栏目
		long oldColID = 0;
		Document oldCol = docManager.get(docLibID, docIDs[0]);
		if (oldCol != null) {
			oldColID = Long.parseLong(oldCol.getString("a_layoutID"));
		} else {
			LogHelper.writeLog(docLibID, docID(null), request, "取出主栏目失败");
		}
		if (oldColID == colID) {
			InfoHelper.outputText("samecol", response);
			return;
		}

		String error = saveCopy(request, docLibID, docIDs, colID);

		if (error == null) {
			// 操作成功后写日志
			StringBuilder operationResult = new StringBuilder();

			InfoHelper.outputText("success" + operationResult.toString(),
					response);
		} else {
			InfoHelper.outputText("Failed", response);
		}
	}

	/**
	 * 保存复制稿件
	 */
	private String saveCopy(HttpServletRequest request, int docLibID,
			long[] docIDs, long colID) throws Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		int layoutLibID = LibHelper.getLibID(DocTypes.PAPERLAYOUT.typeID(),
				Tenant.DEFAULTCODE);

		long[] newDocIDS = new long[docIDs.length];
		// 同时修改多个稿件，使用事务
		DBSession conn = null;
		try {
			conn = E5docHelper.getDBSession(docLibID);
			conn.beginTransaction();

			for (int i = 0; i < docIDs.length; i++) {
				long articleID = docIDs[i];
				Document article = docManager.get(docLibID, articleID);
				// 复制稿件
				long newArticleID = InfoHelper.getNextDocID(DocTypes.ARTICLE
						.typeID());
				newDocIDS[i] = newArticleID;
				Document newArt = docManager.newDocument(article, docLibID,
						newArticleID);

				Timestamp now = DateUtils.getTimestamp();
				newArt.setCreated(now);
				if (colID > 0) {
					newArt.set("a_pubTime", now); // 发布时间
					newArt.set("a_layoutID", colID);

					Document paperlayout = docManager.get(layoutLibID, colID);
					newArt.set("a_paper", paperlayout.getString("pl_paper"));
					newArt.set("a_paperID", paperlayout.getString("pl_paperID"));
					newArt.set("a_layout", paperlayout.getString("pl_layout"));
					// 清空发布地址
					newArt.set("a_url", "");
					newArt.set("a_urlPad", "");

					newArt.set("a_status", Article.STATUS_PUB_NOT);

					newArt.set("a_order", newArticleID);

				}
				docManager.save(newArt, conn);
				// 保存附件
				copyAttachments(newArt, article, conn);
			}
			conn.commitTransaction();
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			return "操作中出现错误：" + e.getLocalizedMessage();
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
		SysUser sysUser = ProcHelper.getUser(request);
		writeCopyLog(sysUser, newDocIDS, docIDs, docLibID);
		return null;
	}

	// 复制成功记录日志
	public void writeCopyLog(SysUser sysUser, long[] newDocIDS, long[] docIDs,
			int docLibID) throws Exception {
		for (int i = 0; i < newDocIDS.length; i++) {
			LogHelper.writeLog(docLibID, newDocIDS[i], sysUser, "复制", "源ID："
					+ docIDs[i]);
		}
	}

	// 从json中读int
	private int getInt(JSONObject json, String key) {
		String value = getString(json, key);
		return (StringUtils.isBlank(value)) ? 0 : Integer.parseInt(value);
	}

	// 从json中读string
	private String getString(JSONObject json, String key) {
		if (json.containsKey(key))
			return json.getString(key);
		else
			return null;
	}

	/**
	 * 稿件转版视图
	 */
	@RequestMapping(value = "LayoutArticleTransferView.do")
	public String layoutArticleTransferView(HttpServletRequest request,
			HttpServletResponse response, Map<String, Object> model)
			throws Exception {
		String tenantCode = InfoHelper.getTenantCode(request);
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		long docID = WebUtil.getLong(request, "DocIDs", 0);
		String uuid = WebUtil.get(request, "UUID");
		int siteID = WebUtil.getInt(request, "siteID", 0);
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document artDoc = docManager.get(docLibID, docID);
		List<PaperLayout> layouts = paperManager.getPaperLayouts(siteID, artDoc.getInt("a_paperID"), artDoc.getDate("a_pubTime"),tenantCode);
		
		model.put("layouts", layouts);
		
		model.put("id", artDoc.getDocID());
		model.put("title", artDoc.getTopic());
		model.put("layoutId", artDoc.getInt("a_layoutID"));
		model.put("layoutName", artDoc.getString("a_layout"));
		model.put("transStatus", artDoc.getInt("a_transStatus"));
		
		model.put("UUID", uuid);
		return "/xy/paper/TransferArticle";
	}
	
	/**
	 * 获取转版的所有稿件
	 */
	@RequestMapping(value = "getTransferLayoutArticles.do")
	@ResponseBody
	public void getRelArticles(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String tenantCode = InfoHelper.getTenantCode(request);
		long targetID = WebUtil.getLong(request, "targetID", 0);
		String result = paperManager.getRelPaperArticles(targetID,tenantCode);
		WebUtil.outputJson(result, response);
	}
	
	/**
	 * 获取某版次下的所有稿件
	 */
	@RequestMapping(value = "getLayoutArticles.do")
	@ResponseBody
	public void getLayoutArticles(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String tenantCode = InfoHelper.getTenantCode(request);
		int libID = LibHelper.getLibID(DocTypes.PAPERARTICLE.typeID(), tenantCode);
		long layoutID = WebUtil.getLong(request, "layoutID", 0);
		String keyword = WebUtil.get(request, "keyword");
		String result = paperManager.getLayoutArticles(libID, layoutID, keyword);
		WebUtil.outputJson(result, response);
	}
	
	/**
	 * 稿件转版操作
	 * flag : 1,合成; 2,还原;
	 */
	@RequestMapping(value = "TransferLayoutArticle.do")
	@ResponseBody
	public void transferLayoutArticle(HttpServletRequest request,
			HttpServletResponse response)throws Exception {
		long targetID = WebUtil.getLong(request, "targetID", 0);
		long articleID = WebUtil.getLong(request, "articleID", 0);
		int flag = WebUtil.getInt(request, "flag", 0);
		String tenantCode = InfoHelper.getTenantCode(request);
		String result = paperManager.transferArticle(flag, targetID, articleID, tenantCode);
		WebUtil.outputJson(result, response);
	}
}
