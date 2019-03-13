package com.founder.xy.article.web;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.DomHelper;
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
import com.founder.e5.flow.FlowNode;
import com.founder.e5.flow.FlowReader;
import com.founder.e5.sys.StorageDevice;
import com.founder.e5.sys.StorageDeviceManager;
import com.founder.e5.sys.SysFactory;
import com.founder.e5.web.SysUser;
import com.founder.e5.web.WebUtil;
import com.founder.e5.workspace.ProcHelper;
import com.founder.e5.workspace.app.form.LogHelper;
import com.founder.xy.article.Article;
import com.founder.xy.article.ArticleManager;
import com.founder.xy.article.SignedChInfo;
import com.founder.xy.article.SubmitInfo;
import com.founder.xy.column.Column;
import com.founder.xy.column.ColumnReader;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.config.Channel;
import com.founder.xy.config.ConfigReader;
import com.founder.xy.jms.PublishTrigger;
import com.founder.xy.jms.data.ArticleMsg;
import com.founder.xy.jms.data.DocIDMsg;
import com.founder.xy.jpublish.JsoupHelper;
import com.founder.xy.jpublish.page.ColumnGenerator;
import com.founder.xy.system.site.Site;
import com.founder.xy.system.site.SiteUserManager;
import com.founder.xy.system.site.SiteUserReader;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 稿件操作功能
 *
 * @author Deng Chaochen
 */
@Controller
@RequestMapping("/xy/article")
public class ArticleOperationController {
	@Autowired
	private ColumnReader colReader;

	@Autowired
	ArticleManager articleManager;

	@Autowired
	private SiteUserManager userManager;

	/**
	 * 稿件驳回记录原因
	 */
    @RequestMapping(value = "Reject.do")
    public void reject(HttpServletRequest request, HttpServletResponse response,
            @RequestParam String DocIDs, @RequestParam String DocLibID) throws Exception {
        int docLibID = Integer.parseInt(DocLibID);
        long[] docIDs = StringUtils.getLongArray(DocIDs);
        //读出稿件
        List<Document> articles = getPublishDoc(docLibID,docIDs,true);//读出稿件
        StringBuilder docidstr= new StringBuilder();
        StringBuilder doclibstr= new StringBuilder();
        for (Document article : articles) {
            article.set("a_status", Article.STATUS_REJECTED);
            docidstr.append(article.getDocID()).append(",");
            doclibstr.append(article.getDocLibID()).append(",");
        }
        
        //同时修改多个稿件，使用事务
        String error = save(docLibID, articles);
        if (error == null) {
        	JSONObject rs = new JSONObject();
        	rs.put("rs", "success");
        	rs.put("docidstr", docidstr.toString());
        	rs.put("doclibstr", doclibstr.toString());
        	
            InfoHelper.outputJson(rs.toString(), response);
        } else {
            InfoHelper.outputJson(error, response);
        }
    }

	/**
	 * 提交审稿，发布
	 */
	@RequestMapping(value = "Transfer.do")
	public String transfer(HttpServletRequest request, HttpServletResponse response, @RequestParam int status)
			throws Exception {
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		String strDocIDs = WebUtil.get(request, "DocIDs");
		long[] docIDs = StringUtils.getLongArray(strDocIDs);
		List<Document> articles = getPublishDoc(docLibID,docIDs,false);//读出稿件
        StringBuilder docidstr= new StringBuilder();
        StringBuilder doclibstr= new StringBuilder();
		for (Document article : articles) {
			article.set("a_status", status);
			if (status == Article.STATUS_PUB_ING) {
				SysUser user = ProcHelper.getUser(request);
				article.set("a_lastPublish", user.getUserName());
				article.set("a_lastPublishID", user.getUserID());
			}
			// 处理定时发布,若稿件已经设置为发布，并且发布时间置后，则改成定时发布状态
			articleManager.changeTimedPublish(article);
            docidstr.append(article.getDocID()).append(",");
            doclibstr.append(article.getDocLibID()).append(",");
		}

		// 同时修改多个稿件，使用事务
		String error = save(docLibID, articles);

		// 调用after.do进行后处理：改变流程状态、解锁、刷新列表
		String url = "/e5workspace/after.do?UUID=" + WebUtil.get(request, "UUID");

		if (error == null) {
			// 保存成功后，触发发布消息
			if (status == Article.STATUS_PUB_ING) {
				articleManager.publish(articles);
			}
			url += "&DocIDs=" + docidstr.toString() + "&DocLibID=" + doclibstr.toString() + "&Info=" + URLEncoder.encode("操作完成", "UTF-8"); //操作成功
		} else {
			url += "&Info=" + URLEncoder.encode(error, "UTF-8");// 有错误，需返回前台做提示
		}
		return "redirect:" + url;
	}

    private List<Document> getPublishDoc(int docLibID,long[] docIDs,boolean changePushCol) throws Exception {
    	List<Document> articles = new ArrayList<Document>();
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		int colLibID = LibHelper.getLibIDByOtherLib(DocTypes.COLUMN.typeID(), docLibID);
		for(long docID : docIDs){
			Document article = docManager.get(docLibID, docID);
			articles.add(article);
			if(docLibID == LibHelper.getArticleLibID()){
				long colID = article.getLong("a_columnID");
				Column col = colReader.get(colLibID, colID);
				if(col != null && col.getPushColumn() > 0){
				    //如果自动同步的app栏目已经删除，则不需要自动同步数据到这个app栏目下的对应稿件
                    Column pushCol = colReader.get(colLibID, col.getPushColumn());
					if(changePushCol||pushCol!=null){
						Document appArticle = docManager.get(LibHelper.getArticleAppLibID(), docID);
						if(appArticle!=null) articles.add(appArticle);
					}
				}
			}
		}
		return articles;
    }
	
	/**
	 * APP推荐模块获得渠道
	 */
	@RequestMapping("RecommendChannel.do")
	public void RecommendChannel(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int docLibID = WebUtil.getInt(request, "DocLibID", 1);
		String strDocIDs = WebUtil.get(request, "DocID");
		String result = getRecommendChannel(docLibID, strDocIDs, request);

		InfoHelper.outputJson(result, response);
	}

	@RequestMapping(value = "DealChannel.do")
	public void dealChannel(HttpServletRequest request, HttpServletResponse response) throws Exception {
		JSONObject params = JSONObject.fromObject(WebUtil.get(request, "paramData"));
		String error = saveChannelArticles(params, request);
		if (error == null) {
			InfoHelper.outputText("ok", response);
		} else {
			InfoHelper.outputText("Failed", response);
		}
	}

	/**
	 * 彻底删除发布稿（在撤稿中心）
	 */
	@RequestMapping(value = { "RevokeDelete.do" })
	public void revokeDelete(HttpServletRequest request, HttpServletResponse response) throws Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		String strDocIDs = WebUtil.get(request, "DocIDs");
		long[] docIDs = StringUtils.getLongArray(strDocIDs);
		long[] originalIDs = new long[docIDs.length];
		ArticleMsg[] articleMsgs = new ArticleMsg[docIDs.length];

		// 取出稿件的原稿ID，以及获取彻底删除的稿件信息，准备好撤稿消息需要的数据ArticleMsg（PublishTrigger.getArticleMsg）
		for (int i = 0; i < docIDs.length; i++) {
			Document doc = docManager.get(docLibID, docIDs[i]);
			originalIDs[i] = doc.getLong("a_originalID");

			articleMsgs[i] = PublishTrigger.getArticleMsg(doc);
		}

		String error = deleteOriginals(docLibID, docIDs);
		if (error == null) {
			// 由于彻底删除了稿件，所以需要手动存日志
			for (long docID : docIDs) {
				LogHelper.writeLog(docLibID, docID, request, "彻底删除，保留日志");
			}

			// 发布撤稿消息
			for (ArticleMsg articleMsg : articleMsgs) {
				PublishTrigger.articleDelete(articleMsg);
			}

			// 修改原稿库中的原稿的渠道
			int originalLibID = LibHelper.getLibIDByOtherLib(DocTypes.ORIGINAL.typeID(), docLibID);

			List<Document> originals = new ArrayList<Document>();
			for (int i = 0; i < docIDs.length; i++) {
				if (originalIDs[i] > 0 && originalIDs[i] == docIDs[i]) {
					Document original = docManager.get(originalLibID, originalIDs[i]);
					if (original != null) {
						int channel = original.getInt("a_channel");
						if (channel < 0)
							channel = 0;
						// 去掉当前渠道：第0位表示WEB版，第1位表示App版
						channel = channel ^ (int) Math.pow(2, articleManager.getChannelForLib(request, docLibID));
						original.set("a_channel", channel);
						originals.add(original);
					}
				}
			}
			save(originalLibID, originals);
		}
		InfoHelper.outputText("@refresh@", response);
	}

	/**
	 * 稿件删除操作：原稿会彻底删除，发布库稿件会设删除标记，回收站会彻底删除
	 */
	@RequestMapping(value = { "Delete.do" })
	public String originalDelete(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		String strDocIDs = WebUtil.get(request, "DocIDs");
		long[] docIDs = StringUtils.getLongArray(strDocIDs);
		long colID = WebUtil.getLong(request, "colID", 0);
		int isGarbage = WebUtil.getInt(request, "g", 0); // 是垃圾箱的彻底删除

		if (isGarbage == 1 || DomHelper.getDocTypeIDByLibID(docLibID) == DocTypes.ORIGINAL.typeID()) {
			// 若是回收站的稿件，修改原稿库中的原稿的渠道
			if (isGarbage == 1) {
				changeOriginalCh(request, docLibID, docIDs);
			}
			// 稿件是源稿，彻底删除
			deleteOriginals(docLibID, docIDs);
			// 由于彻底删除了稿件，所以需要手动存日志
			for (long docID : docIDs) {
				LogHelper.writeLog(docLibID, docID, request, "彻底删除，保留日志");
			}

			InfoHelper.outputText("@refresh@", response);
			return null;
		} else {
			// 发布库的未发布稿件 删除处理
			deleteArticles(docLibID, docIDs, colID);
	        int colLibID = LibHelper.getLibIDByOtherLib(DocTypes.COLUMN.typeID(), docLibID);
	        Column col = colReader.get(colLibID, colID);
	        if(col.getPushColumn() > 0 && docLibID == LibHelper.getArticleLibID()){
	        	deleteArticles(LibHelper.getArticleAppLibID(), docIDs, col.getPushColumn());
	        }
			// 调用after.do进行后处理：改变流程状态、解锁、刷新列表、写日志
			String url = "/e5workspace/after.do?UUID=" + WebUtil.get(request, "UUID") + "&DocIDs=" + strDocIDs;
			if (colID > 0) {
				String colName = getColumnName(colLibID, colID);
				if (!StringUtils.isBlank(colName)) {
					colName = "栏目：" + colName;
					url += "&Opinion=" + URLEncoder.encode(colName, "UTF-8");
				}
			}
			return "redirect:" + url;
		}
	}

	/**
	 * 源稿签发
	 */
	@RequestMapping(value = { "OriginalSubmit.do" })
	public ModelAndView originalSubmit(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		String strDocIDs = WebUtil.get(request, "DocIDs");
		String tenantCode = InfoHelper.getTenantCode(request);
		Document original = docManager.get(docLibID, Long.parseLong(strDocIDs));
		int channel = original.getInt("a_channel");
		if (channel < 0)
			channel = 0;
		// 取发布渠道。原稿中，不再显示已经发布的渠道
		List<Channel> chs = getChannels(channel);

		// 取已发布发布渠道。在界面中显示已发布的信息。
		List<SignedChInfo> originaledchs = getSignedChs(channel, Long.parseLong(strDocIDs), tenantCode);

		model.put("originaledchs", originaledchs);
		model.put("originaledchsCount", originaledchs.size());
		model.put("channels", chs);
		model.put("channelCount", chs.size());
		model.put("DocLibID", docLibID);
		model.put("DocIDs", strDocIDs);
		model.put("UUID", WebUtil.get(request, "UUID"));
		model.put("siteID", WebUtil.get(request, "siteID"));

		return new ModelAndView("/xy/article/OriginalSign", model);
	}

	@RequestMapping(value = "OriginalSign.do")
	public void originalSign(HttpServletRequest request, HttpServletResponse response) throws Exception {
		JSONObject params = JSONObject.fromObject(WebUtil.get(request, "paramData"));

		String error = signChannelArticles(params, request);

		if (error == null) {
			InfoHelper.outputText("ok", response);
		} else {
			InfoHelper.outputText("Failed", response);
		}
	}

	/**
	 * 多个稿件的统一提交， 出错时返回错误信息
	 */
	private String save(int docLibID, List<Document> articles) {
		if (articles == null || articles.size() == 0)
			return null;

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		// 同时修改多个稿件，使用事务
		DBSession conn = null;
		try {
			conn = E5docHelper.getDBSession(docLibID);
			conn.beginTransaction();

			for (Document article : articles) {
				docManager.save(article, conn);
			}
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

	private String getRecommendChannel(int docLibID, String DocID, HttpServletRequest request) throws E5Exception {
		String tenantCode = InfoHelper.getTenantCode(request);
		Channel[] allChs = ConfigReader.getChannels();
		List<DocLib> articleLibs = LibHelper.getLibs(DocTypes.ARTICLE.typeID(), tenantCode);
		Channel chs = null;
		for (int i = 0; i < allChs.length; i++) {
			if (allChs[i] != null && articleLibs.get(i).getDocLibID() == LibHelper.getArticleAppLibID()) {
				chs = allChs[i];
			}
		}
		JSONObject jsonObj = new JSONObject();
		if (chs != null) {
			jsonObj.put("id", chs.getId());
			jsonObj.put("name", chs.getName());
			jsonObj.put("code", chs.getCode());
		}
		return jsonObj.toString();
	}

	private String saveChannelArticles(JSONObject form, HttpServletRequest request) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		String tenantCode = InfoHelper.getTenantCode(request);

		long[] docIDs = StringUtils.getLongArray(WebUtil.getStringParam(request, "DocIDs"));
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		Document[] articles = docManager.get(docLibID, docIDs);
		SysUser sysUser = ProcHelper.getUser(request);

		int originalLibID = LibHelper.getLibIDByOtherLib(DocTypes.ORIGINAL.typeID(), docLibID);
		Document[] originaldocs = docManager.get(originalLibID, docIDs);
		HashMap<Long, Document> originaldocMap = new HashMap<>();
		for (Document originaldoc : originaldocs) {
			if (originaldoc != null) {
				originaldocMap.put(originaldoc.getDocID(), originaldoc);
			}
		}
		// 取出租户下的发布库。渠道代号是从0开始，正好与发布库一一对应
		List<DocLib> articleLibs = LibHelper.getLibs(DocTypes.ARTICLE.typeID(), tenantCode);
		Channel[] chs = ConfigReader.getChannels();
		// 同时修改多个稿件，使用事务
		DBSession conn = null;
		try {
			conn = E5docHelper.getDBSession(docLibID);
			conn.beginTransaction();
			for (Document article : articles) {
				if (article.getInt("a_type") == Article.TYPE_SPECIAL)
					continue; // 不支持专题稿的推送渠道操作
				StringBuffer articleLog = new StringBuffer();
				Document originaldoc = originaldocMap.containsKey(article.getDocID())
						? originaldocMap.get(article.getDocID()) : null;
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
							String colName = getString(form, id + "_column");

							int currentChannel = (int) Math.pow(2, id);

							Document chArticle = docManager.newDocument(article, articleLibs.get(i).getDocLibID(),
									article.getDocID());
							chArticle.setFolderID(0); // 清空文件夹ID，以便initDoc方法赋值
							ProcHelper.initDoc(chArticle);

							chArticle.set("a_pubTime", DateUtils.getTimestamp()); // 发布时间
							// 设置主栏目
							chArticle.set("a_column", colName);
							chArticle.set("a_columnID", colID);
							// 清空关联栏目
							chArticle.set("a_columnRel", "");
							chArticle.set("a_columnRelID", "");

							articleManager.setColumnAll(chArticle);

							// 填写“原稿ID”字段为选定稿件的原稿ID
							chArticle.set("a_originalID", article.getString("a_originalID"));
							chArticle.set("a_channel", currentChannel);
							chArticle.set("a_status", Article.STATUS_PUB_NOT);
							int _type = chArticle.getInt("a_type");
							if (_type != Article.TYPE_LINK && _type != Article.TYPE_LIVE && _type != Article.TYPE_AD
									&& _type != Article.TYPE_H5) {
								chArticle.set("a_url", "");
								chArticle.set("a_urlPad", "");
							}
							// 按主栏目设置流程。
							articleManager.setFlowByColumn(chArticle);

							docManager.save(chArticle);
							// 若稿件ID与它的原稿ID相同，则找到原稿，设置a_channel字段

							if (article.getDocID() == chArticle.getInt("a_originalID") && originaldoc != null) {
								int channel = originaldoc.getInt("a_channel");
								if (channel < 0)
									channel = 0;
								channel = channel | currentChannel;
								originaldoc.set("a_channel", channel);
							}
							// 保存扩展字段
							saveExtFields(chArticle, article, conn);
							// 保存挂件(无组图稿、视频稿复制，只需要复制挂件中的附件)所以重新写一个方法
							saveChannelWidgets(chArticle, article, conn);
							// 保存附件
							saveAttachments(chArticle, article, conn);

							// 日志
							if (articleLog.length() > 0)
								articleLog.append("、");
							articleLog.append(chs[i].getName() + ": " + colName);
							// 为渠道下的新稿件写日志
							LogHelper.writeLog(chArticle.getDocLibID(), chArticle.getDocID(), sysUser, "推送",
									"来自栏目：" + article.getString("a_column"));
						}
					}
				}
				if (originaldoc != null)
					docManager.save(originaldoc, conn);
				// 推送完毕后，写日志
				LogHelper.writeLog(article.getDocLibID(), article.getDocID(), sysUser, "推送(渠道)",
						"推送到：" + articleLog.toString());
			}
			conn.commitTransaction();
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			return "操作中出现错误：" + e.getLocalizedMessage();
		} finally {
			ResourceMgr.closeQuietly(conn);
		}

		return null;
	}

	// 推送(渠道)挂件表目前只需要复制其中的附件即w_type=0；先这么做，若客户要求自动找到相关稿件和挂件的对应渠道内的稿件，再加。
	private void saveChannelWidgets(Document article, Document srcArticle, DBSession conn) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();

		Document[] widgets = docManager.find(
				LibHelper.getLibIDByOtherLib(DocTypes.WIDGET.typeID(), srcArticle.getDocLibID()),
				"w_articleLibID=? and w_articleID=? and w_type=?",
				new Object[] { srcArticle.getDocLibID(), srcArticle.getDocID(), 0 });
		if (widgets != null) {
			for (Document widget : widgets) {
				long docID = InfoHelper.getNextDocID(DocTypes.WIDGET.typeID());
				Document copywidget = docManager.newDocument(widget, widget.getDocLibID(), docID);
				copywidget.set("w_articleLibID", article.getDocLibID());
				copywidget.set("w_articleID", article.getDocID());

				docManager.save(copywidget, conn);
			}
		}

	}

	/**
	 * 删除多个稿件以及把稿件对应的挂件、附件、扩展字段都删掉
	 * 
	 * @throws E5Exception
	 */
	private String deleteOriginals(int docLibID, long[] docIDs) throws E5Exception {
		String tCode = LibHelper.getTenantCodeByLib(docLibID);

		// 稿件删除语句。用sql执行而不是调用docManager.delete，是避免同时清理日志
		String delSql = "delete from " + LibHelper.getLibTable(docLibID) + " where SYS_DOCUMENTID=?";

		// 删除稿件对应的挂件的SQL
		String delWidSql = "delete from " + LibHelper.getLibTable(DocTypes.WIDGET.typeID(), tCode)
				+ " where w_articleID=? and w_articleLibID=?";
		// 删除稿件对应附件的SQL
		String delAttaSql = "delete from " + LibHelper.getLibTable(DocTypes.ATTACHMENT.typeID(), tCode)
				+ " where att_articleID=? and att_articleLibID=?";
		// 删除稿件对应扩展字段的SQL
		String delExtSql = "delete from " + LibHelper.getLibTable(DocTypes.ARTICLEEXT.typeID(), tCode)
				+ " where ext_articleID=? and ext_articleLibID=?";

		// 同时修改多个稿件，使用事务
		DBSession conn = null;
		try {
			List<String> deleteList = getFilesToDelete(docLibID, docIDs);
			conn = E5docHelper.getDBSession(docLibID);
			conn.beginTransaction();
			for (long docID : docIDs) {
				InfoHelper.executeUpdate(delSql, new Object[] { docID }, conn);
				// 删除对应的挂件、附件、扩展字段
				InfoHelper.executeUpdate(delWidSql, new Object[] { docID, docLibID }, conn);
				InfoHelper.executeUpdate(delAttaSql, new Object[] { docID, docLibID }, conn);
				InfoHelper.executeUpdate(delExtSql, new Object[] { docID, docLibID }, conn);
			}
			conn.commitTransaction();
			for (String path : deleteList) {
				clear(InfoHelper.getFilePathInDevice(path));
			}
			return null;
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			return "操作中出现错误：" + e.getLocalizedMessage();
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
	}

	private List<String> getFilesToDelete(int docLibID, long[] docIDs) throws E5Exception {
		int widgetLibID = LibHelper.getWidgetLibID();
		int attLibID = LibHelper.getAttaLibID();

		String wsql = " w_articleID = ? and w_articleLibID = ? and w_type = 0 ";
		String asql = " att_articleID = ? and att_articleLibID = ? and att_type <> 1 and (att_objID is null or att_objID = 0) ";

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		List<String> deleteList = new ArrayList<>();
		for (long docID : docIDs) {
			Document[] widgets = docManager.find(widgetLibID, wsql, new Object[] { docID, docLibID });
			Document[] atts = docManager.find(attLibID, asql, new Object[] { docID, docLibID });

			for (Document widget : widgets) {
				deleteList.add(widget.getString("w_path"));
			}
			for (Document att : atts) {
				String path = att.getString("att_path");
				if (StringUtils.isBlank(path))
					continue;

				String fileName = path.substring(path.lastIndexOf("/") + 1);

				if (att.getInt("att_type") == 0) {
					if (!path.startsWith("http")) { // 有可能是外网图片
						deleteList.add(path);
					}
				} else if (fileName.startsWith("t0_") || fileName.startsWith("t1_") || fileName.startsWith("t2_")) {
					deleteList.add(path);
				}
			}
		}
		return deleteList;
	}

	// 删掉没被引用的文件，同时删掉抽图文件
	private void clear(String path) {
		File file = new File(path);
		try {
			if (file.exists())
				file.delete();

			String fileName = file.getCanonicalPath();

			file = new File(fileName + ".2");
			if (file.exists())
				file.delete();

			file = new File(fileName + ".2.jpg");
			if (file.exists())
				file.delete();

			file = new File(fileName + ".1");
			if (file.exists())
				file.delete();

			file = new File(fileName + ".1.jpg");
			if (file.exists())
				file.delete();

			file = new File(fileName + ".0");
			if (file.exists())
				file.delete();

			file = new File(fileName + ".0.jpg");
			if (file.exists())
				file.delete();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 回收站稿件对应的原稿库中的稿件去掉当前渠道
	 * 
	 * @param request
	 * @param docLibID
	 * @param docIDs
	 * @throws E5Exception
	 */
	private void changeOriginalCh(HttpServletRequest request, int docLibID, long[] docIDs) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		int originalLibID = LibHelper.getLibIDByOtherLib(DocTypes.ORIGINAL.typeID(), docLibID);

		List<Document> originals = new ArrayList<Document>();
		for (int i = 0; i < docIDs.length; i++) {
			Document original = docManager.get(originalLibID, docIDs[i]);
			if (original != null) {
				int channel = original.getInt("a_channel");
				if (channel < 0)
					channel = 0;
				// 去掉当前渠道：第0位表示WEB版，第1位表示App版
				channel = channel ^ (int) Math.pow(2, articleManager.getChannelForLib(request, docLibID));
				original.set("a_channel", channel);
				originals.add(original);
			}
		}
		save(originalLibID, originals);
	}

	/**
	 * 发布库的未发布稿件 当前栏目是稿件的主栏目，则设置稿件DeleteFlag=1 当前栏目不是稿件的主栏目按解除关联处理
	 *
	 * @param DocLibID
	 * @param docIDs
	 * @param colID
	 * @throws E5Exception
	 */
	private void deleteArticles(int DocLibID, long[] docIDs, long colID) throws E5Exception {
		int colLibID = LibHelper.getLibIDByOtherLib(DocTypes.COLUMN.typeID(), DocLibID);
		String colName = getColumnName(colLibID, colID);

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		List<Document> articles = new ArrayList<Document>();
		for (long docID : docIDs) {
			boolean isRel = false;
			Set<Long> relSet = new TreeSet<Long>();

			Document article = docManager.get(DocLibID, docID);
			if (article == null)
				continue;
			long columnID = article.getLong("a_columnID");
			// 关联栏目
			if (colID > 0) { // colID=0，可能是在“我的稿件”里
				String relColumnId = article.getString("a_columnRelID");
				if (!StringUtils.isBlank(relColumnId)) {
					long[] ids = StringUtils.getLongArray(relColumnId);
					for (long id : ids) {
						if (id != colID) {
							relSet.add(id);
						} else {
							isRel = true;
						}
					}
				}
			}

			if (isRel) {
				// 当前栏目是稿件的关联栏目,修改稿件的关联栏目ID和关联栏目名称
				dealRelCols(colName, article, relSet);
				// 处理一下所有栏目,修改a_columnAll字段去掉该栏目ID
				dealAllCols(colID, article);
			} else {
				if (colID == 0 || colID == columnID) {// colID=0，可能是在“我的稿件”里
					// 当前栏目是稿件的主栏目，则设置稿件DeleteFlag=1
					article.setDeleteFlag(1);
				} else {
					// 栏目是一个聚合栏目，修改a_columnAll字段去掉该栏目ID。同样，若稿件已发布，则触发消息
					dealAllCols(colID, article);
				}
			}
			articles.add(article);
		}
		// 使用事务保存
		save(DocLibID, articles);
	}

	// 当前栏目是稿件的关联栏目,修改稿件的关联栏目ID和关联栏目名称
	private void dealRelCols(String colName, Document article, Set<Long> relSet) {
		String relColumn = article.getString("a_columnRel");
		Set<String> relNameSet = new TreeSet<String>();
		if (!StringUtils.isBlank(relColumn)) {
			String[] names = StringUtils.split(relColumn, ",");
			for (String name : names) {
				if (!name.equalsIgnoreCase(colName)) {
					relNameSet.add(name);
				}
			}
		}
		StringBuilder column_relId = new StringBuilder();
		for (Long l : relSet) {
			if (column_relId.length() > 0)
				column_relId.append(",");
			column_relId.append(l);
		}

		StringBuilder column_rel = new StringBuilder();
		for (String s : relNameSet) {
			if (column_rel.length() > 0)
				column_rel.append(",");
			column_rel.append(s);
		}
		article.set("a_columnRel", column_rel.toString());
		article.set("a_columnRelID", column_relId.toString());
	}

	// 该栏目是一个聚合栏目，修改a_columnAll字段去掉该栏目ID
	private void dealAllCols(long colID, Document article) {
		Set<Long> allIdSet = new TreeSet<Long>();
		String columnAllId = article.getString("a_columnAll");
		if (!StringUtils.isBlank(columnAllId)) {
			long[] ids = StringUtils.getLongArray(columnAllId, ";");
			for (long id : ids) {
				if (id != colID) {
					allIdSet.add(id);
				}
			}
		}
		StringBuilder column_allId = new StringBuilder();
		for (Long l : allIdSet) {
			if (column_allId.length() > 0)
				column_allId.append(";");
			column_allId.append(l);
		}
		article.set("a_columnAll", column_allId.toString());
	}

	private List<SignedChInfo> getSignedChs(int channel, long DocID, String tenantCode) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Channel[] allChs = ConfigReader.getChannels();
		List<DocLib> articleLibs = LibHelper.getLibs(DocTypes.ARTICLE.typeID(), tenantCode);

		List<SignedChInfo> chs = new ArrayList<SignedChInfo>();
		for (int i = 0; i < allChs.length; i++) {
			if (allChs[i] != null && ((int) Math.pow(2, i) & channel) == (int) Math.pow(2, i)) {
				Document chArticle = docManager.get(articleLibs.get(i).getDocLibID(), DocID);
				SignedChInfo c = new SignedChInfo(allChs[i].getId(), allChs[i].getCode(), allChs[i].getName(),
						chArticle.getString("a_column"), chArticle.getString("a_columnRel"));
				chs.add(c);
			}
		}
		return chs;
	}

	private List<Channel> getChannels(int channel) {
		Channel[] allChs = ConfigReader.getChannels();

		List<Channel> chs = new ArrayList<Channel>();
		for (int i = 0; i < allChs.length; i++) {
			if (allChs[i] != null && ((int) Math.pow(2, i) & channel) == 0) {
				chs.add(allChs[i]);
			}
		}
		return chs;
	}

	// 签发源稿向选中的渠道
	private String signChannelArticles(JSONObject form, HttpServletRequest request) throws Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		String tenantCode = InfoHelper.getTenantCode(request);
		int docID = WebUtil.getInt(request, "DocIDs", 0);
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		Document originaldoc = docManager.get(docLibID, docID);

		SysUser sysUser = ProcHelper.getUser(request);
		int roleID = sysUser.getRoleID();

		List<Document> chArticles = new ArrayList<Document>();
		// 取出租户下的发布库。渠道代号是从0开始，正好与发布库一一对应
		List<DocLib> articleLibs = LibHelper.getLibs(DocTypes.ARTICLE.typeID(), tenantCode);
		// 源稿日志
		StringBuffer originalLog = new StringBuffer();

		Channel[] chs = ConfigReader.getChannels();
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
					Document chArticle = docManager.newDocument(originaldoc, articleLibs.get(i).getDocLibID(), docID);
					chArticle.setFolderID(0); // 清空文件夹ID，以便initDoc方法赋值
					ProcHelper.initDoc(chArticle);
					chArticle.set("a_columnID", colID);
					chArticle.set("a_column", getString(form, id + "_column"));
					chArticle.set("a_originalID", docID);// 原稿ID

					// 设置关联栏目
					String relColID = getString(form, id + "_columnRelID");
					if (relColID != null) {
						chArticle.set("a_columnRelID", relColID);
						chArticle.set("a_columnRel", getString(form, id + "_columnRel"));
					}
					chArticle.set("a_pubTime", DateUtils.getTimestamp()); // 发布时间
					chArticle.set("a_linkTitle", originaldoc.getTopic());

					// 设置顺序
					double order = articleManager.getNewOrder(chArticle);
					chArticle.set("a_order", order);

					// 所有栏目设置
					articleManager.setColumnAll(chArticle);
					articleManager.tryPublish(chArticle, colID, roleID);

					int channel = originaldoc.getInt("a_channel");
					if (channel < 0)
						channel = 0;
					channel = channel | (int) Math.pow(2, id);
					originaldoc.set("a_channel", channel);

					if (originalLog.length() != 0) {
						originalLog.append("；");
					}

					originalLog.append("签发到").append(chs[i].getCode()).append("发布库");

					// 给发布库稿件设置a_channel,a_channel值等于2的id次方
					chArticle.set("a_channel", (int) Math.pow(2, id));
					if (chArticle.getInt("SYS_AUTHORID") > 0) {
						Document user = userManager.getUser(LibHelper.getUserExtLibID(),
								chArticle.getInt("SYS_AUTHORID"));
						chArticle.set("a_orgID", user.get("u_orgID"));
					}
					chArticles.add(chArticle);
				}
			}
		}
		chArticles.add(originaldoc);
		String error = saveChannels(chArticles);

		if (error == null) {
			// 写发布库稿件日志
			int chSize = chArticles.size() - 1;
			for (int i = 0; i < chSize; i++) {
				Document article = chArticles.get(i);
				LogHelper.writeLog(article.getDocLibID(), article.getDocID(), sysUser, "签发（原稿）", "");
			}
			// 写原稿库日志
			LogHelper.writeLog(docLibID, docID, sysUser, "签发", originalLog.toString());

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
	private String saveChannels(List<Document> articleList) {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DBSession conn = null;

		try {
			conn = E5docHelper.getDBSession(articleList.get(0).getDocLibID());
			conn.beginTransaction();

			// 新生成的渠道稿件，最后一个为原稿不需要保存挂件附件
			int chSize = articleList.size() - 1;
			for (int i = 0, size = articleList.size(); i < size; i++) {
				Document article = articleList.get(i);
				// 保存稿件
				docManager.save(article, conn);

				if (i < chSize) {
					// 保存扩展字段
					saveExtFields(article, articleList.get(chSize), conn);
					// 保存挂件
					saveWidgets(article, articleList.get(chSize), conn);
					// 保存附件
					saveAttachments(article, articleList.get(chSize), conn);
				}
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

	private void saveArticleRel(Document article, Document srcArticle, DBSession conn) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] rels = docManager.find(
				LibHelper.getLibIDByOtherLib(DocTypes.ARTICLEREL.typeID(), srcArticle.getDocLibID()),
				"a_articleLibID=? and a_articleID=?", new Object[] { srcArticle.getDocLibID(), srcArticle.getDocID() });
		if (rels != null) {
			for (Document rel : rels) {
				long docID = InfoHelper.getNextDocID(DocTypes.ARTICLEREL.typeID());
				Document copyrel = docManager.newDocument(rel, rel.getDocLibID(), docID);
				copyrel.set("a_articleLibID", article.getDocLibID());
				copyrel.set("a_articleID", article.getDocID());

				docManager.save(copyrel, conn);
			}
		}
	}

	/** 复制出的新稿件保存附件 */
	private void saveAttachments(Document article, Document srcArticle, DBSession conn) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] attas = docManager.find(
				LibHelper.getLibIDByOtherLib(DocTypes.ATTACHMENT.typeID(), srcArticle.getDocLibID()),
				"att_articleLibID=? and att_articleID=?",
				new Object[] { srcArticle.getDocLibID(), srcArticle.getDocID() });
		if (attas != null) {
			for (Document atta : attas) {
				long docID = InfoHelper.getNextDocID(DocTypes.ATTACHMENT.typeID());
				Document copyatta = docManager.newDocument(atta, atta.getDocLibID(), docID);
				copyatta.set("att_articleLibID", article.getDocLibID());
				copyatta.set("att_articleID", article.getDocID());

				String att_path = atta.getString("att_path"); //图片存储;xy/201705/05/44e17293-1091-46df-b8e0-6060bd38d6d54a7276a6-9851-4778-a63a-e14dd94b04a5.jpg
				
				if(att_path.lastIndexOf(".") == -1){//百格视频ID
					copyatta.set("att_path", att_path);
				}else{
					String suffix = att_path.substring(att_path.lastIndexOf("."));

					int pos = att_path.indexOf(";");
					
					//类型
					String deviceName = att_path.substring(0, pos);
					//保存路径
					String savePath = att_path.substring(pos + 1);
					//复制出的新路径
					String copyPath = InfoHelper.getPicSavePath(null) + UUID.randomUUID() + suffix;
					
					InputStream in = null;
					OutputStream out = null;
					StorageDeviceManager sdManager = SysFactory.getStorageDeviceManager();
					try {
						in = sdManager.read(deviceName, savePath);
						sdManager.write(deviceName, copyPath, in);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						ResourceMgr.closeQuietly(in);
						ResourceMgr.closeQuietly(out);
					}
					StorageDevice device = InfoHelper.getDeviceByName(deviceName);
					InfoHelper.prepare4Extract(device, copyPath);
					String newPath = deviceName + ";" +copyPath;
					// 将图片路径设置成新文件名
					copyatta.set("att_path", newPath);
					String content = article.getString("a_content");
					content = content.replace(att_path, newPath);
					article.set("a_content", content);
				}
				
				copyatta.set("att_type", atta.getInt("att_type")); // 附件类型
				copyatta.set("att_objID", atta.getLong("att_objID")); // 对应的视频等ID
				copyatta.set("att_objLibID", atta.getInt("att_objLibID"));

				docManager.save(copyatta, conn);
			}
		}
	}

	private void saveWidgets(Document article, Document srcArticle, DBSession conn) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();

		Document[] widgets = docManager.find(
				LibHelper.getLibIDByOtherLib(DocTypes.WIDGET.typeID(), srcArticle.getDocLibID()),
				"w_articleLibID=? and w_articleID=?", new Object[] { srcArticle.getDocLibID(), srcArticle.getDocID() });
		if (widgets != null) {
			for (Document widget : widgets) {
				long docID = InfoHelper.getNextDocID(DocTypes.WIDGET.typeID());
				Document copywidget = docManager.newDocument(widget, widget.getDocLibID(), docID);
				copywidget.set("w_articleLibID", article.getDocLibID());
				copywidget.set("w_articleID", article.getDocID());

				docManager.save(copywidget, conn);
			}
		}
	}

	private void saveExtFields(Document article, Document srcArticle, DBSession conn) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();

		Document[] extFields = docManager.find(
				LibHelper.getLibIDByOtherLib(DocTypes.ARTICLEEXT.typeID(), srcArticle.getDocLibID()),
				"ext_articleLibID=? and ext_articleID=?",
				new Object[] { srcArticle.getDocLibID(), srcArticle.getDocID() });
		if (extFields != null) {
			for (Document extField : extFields) {
				long docID = InfoHelper.getNextDocID(DocTypes.ARTICLEEXT.typeID());
				Document copyExtField = docManager.newDocument(extField, extField.getDocLibID(), docID);
				copyExtField.set("ext_articleLibID", article.getDocLibID());
				copyExtField.set("ext_articleID", article.getDocID());

				docManager.save(copyExtField, conn);
			}
		}
	}

	// 传入主栏目的ID，关联栏目的IDs,Names，目的是用来判断关联栏目中是否包含有主栏目，若是有，需要把主栏目信息去掉。
	private String[] dealRelCol(int mainColID, String relColIDs, String relColNames) throws Exception {
		String[] relCol = new String[2];
		// 判断关联栏目中是否包含有子栏目
		int[] refCols = StringUtils.getIntArray(relColIDs);
		String[] relNames = StringUtils.split(relColNames, ",");
		// 为关联栏目入数据库格式化，strRefColIds格式：1,2,3,4
		StringBuilder strRefColIds = new StringBuilder();
		// 为关联栏目名字入数据库格式化，strRefColNames格式：栏目1,栏目2,栏目3,栏目4
		StringBuilder strRefColNames = new StringBuilder();
		// 使用此循环的前提是ids数组与names数组是对应的
		for (int i = 0; i < refCols.length; i++) {
			// 整理关联栏目信息，要判断里面是否包含主栏目
			int refCol = refCols[i];
			if (refCol != mainColID) {
				if (strRefColIds.length() != 0) {
					strRefColIds.append(",");
					strRefColNames.append(",");
				}
				strRefColIds.append(refCol);
				strRefColNames.append(relNames[i]);
			}
		}
		relCol[0] = strRefColIds.toString();
		relCol[1] = strRefColNames.toString();
		return relCol;
	}

	/**
	 * 稿件撤回 revoke
	 */
	@RequestMapping(value = "Revoke.do")
	public void revoke(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		String strDocIDs = WebUtil.get(request, "DocIDs");
		String detail = WebUtil.get(request, "Detail", "");
		// 后期可以在这里加参数配置
		if (!StringUtils.isBlank(strDocIDs))
			strDocIDs = getLinkDocIDs(docLibID, strDocIDs);
		long[] docIDs = StringUtils.getLongArray(strDocIDs);
		FlowReader flowReader = (FlowReader) Context.getBean(FlowReader.class);
        List<Document> articles = getPublishDoc(docLibID,docIDs,true);//读出稿件
        StringBuilder docidstr= new StringBuilder();
        StringBuilder doclibstr= new StringBuilder();
        for (Document article : articles) {
			if (article != null) {
				// 获得当前稿件所处流程的ID，然后找到这个流程的第一个节点，作为稿件的当前节点，同时设置稿件的当前状态
				int curflowID = article.getCurrentFlow();
				FlowNode[] nodes = flowReader.getFlowNodes(curflowID);
				article.setCurrentNode(nodes[0].getID());
				article.setCurrentStatus(nodes[0].getWaitingStatus());
				// 解锁操作
				article.setLocked(false);
				article.set("a_status", Article.STATUS_REVOKE);
                docidstr.append(article.getDocID()).append(",");
                doclibstr.append(article.getDocLibID()).append(",");
			}
		}
		// 同时修改多个稿件，使用事务
		String error = save(docLibID, articles);

		String uuid = WebUtil.get(request, "UUID");
		if (error == null) {
			// 发布撤稿消息
			articleManager.revoke(articles);

			if (StringUtils.isBlank(uuid)) {
				// 撤稿中心调用的操作，手工写操作日志、返回
				SysUser sysUser = ProcHelper.getUser(request);
				writeRevokeLog(sysUser, docLibID, docIDs, detail);
			}
			
        	JSONObject rs = new JSONObject();
        	rs.put("rs", "success");
        	rs.put("docidstr", docidstr.toString());
        	rs.put("doclibstr", doclibstr.toString());

            InfoHelper.outputJson(rs.toString(), response);
		} else {
        	InfoHelper.outputJson(error, response);
		}
	}

	private String getLinkDocIDs(int docLibID, String strDocIDs) throws E5Exception {
		String sql = "SELECT sys_documentID FROM " + LibHelper.getLibTable(docLibID)
				+ " WHERE sys_deleteFlag = 0 AND a_originalID in ( " + strDocIDs + " ) AND a_type = "
				+ Article.TYPE_LINK + " AND a_status = " + Article.STATUS_PUB_DONE;
		DBSession db = null;
		IResultSet rs = null;
		try {
			db = InfoHelper.getDBSession(docLibID);
			rs = db.executeQuery(sql);
			while (rs.next()) {
				strDocIDs = strDocIDs + "," + rs.getLong("SYS_DOCUMENTID");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(sql);
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(db);
		}
		return strDocIDs;
	}

	/**
	 * 跨站点推稿
	 * 
	 * 获取站点ID与名称 两种模式： 1）跨站点复制 2）跨站点关联
	 * 
	 * @throws E5Exception
	 */
	@RequestMapping(value = "SiteArticlePush.do")
	public void SiteArticlePush(HttpServletRequest request, HttpServletResponse response) throws E5Exception {

		JSONArray arr = new JSONArray();

		int userLibID = LibHelper.getUserExtLibID(request);
		SysUser sysUser = ProcHelper.getUser(request);
		int userID = sysUser.getUserID();
		SiteUserReader siteUserReader = (SiteUserReader) Context.getBean("siteUserReader");
		List<Site> sites = siteUserReader.getSites(userLibID, userID);

		for (Site site : sites) {
			int siteID = site.getId();
			String siteName = site.getName();

			JSONObject json = new JSONObject();
			json.put("id", siteID);
			json.put("name", siteName);
			arr.add(json);
		}

		InfoHelper.outputJson(arr.toString(), response);

	}

	/**
	 * 稿件复制与关联
	 * 
	 * @throws Exception 
	 * 
	 */
	@RequestMapping(value = "CopyRel.do")
	public void CopyRel(HttpServletRequest request, HttpServletResponse response, @RequestParam String DocIDs,
			@RequestParam String DocLibID, @RequestParam String colIDs, @RequestParam long oldColID,
			@RequestParam String choice,@RequestParam(required = false) String puborApr) throws Exception {
		if (choice.equals("copy")) {
			// 进行稿件复制操作
			copy(request,response,DocIDs,DocLibID, colIDs,puborApr);
		} else if (choice.equals("Rel")) {
			// 进行稿件关联操作
			relcolumn(request, response, DocIDs, DocLibID, colIDs, oldColID);
		}

	}

	/**
	 * 复制稿件：多篇稿件复制到一个栏目下
	 * 
	 * 两种使用场景： 1）稿件的复制。这是界面选中待复制到的栏目后的后台处理。 2）原稿的复制。这是直接作为操作按钮的响应url
	 */
	@RequestMapping(value = "copy.do")
	public void copy(HttpServletRequest request, HttpServletResponse response, @RequestParam String DocIDs,
			@RequestParam String DocLibID, @RequestParam String colID, @RequestParam(required = false) String puborApr)
			throws Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		int docLibID = Integer.parseInt(DocLibID);
		long[] docIDs = StringUtils.getLongArray(DocIDs);
		long[] colIDs = StringUtils.getLongArray(colID);
		if (docIDs.length <= 0) {
			InfoHelper.outputText("Failed", response);
			return;
		}

		for (long columnID : colIDs) {
			if (columnID > 0) {
				// 稿件的复制

				// 取出现有的主栏目ID，判断选中的是否是自己所述的主栏目
				long oldColID = 0;
				Document oldCol = docManager.get(docLibID, docIDs[0]);
				if (oldCol != null) {
					oldColID = Long.parseLong(oldCol.getString("a_columnID"));
				}
				if (oldColID == columnID) {
					InfoHelper.outputText("samecol", response);
					return;
				}

				String error = saveCopy(request, docLibID, docIDs, columnID, puborApr);

				if (error == null) {
					// 操作成功后写日志
					StringBuilder operationResult = new StringBuilder();
					String colName = getColumnName(LibHelper.getColumnLibID(request), columnID);
					operationResult.append("复制到：").append(colName);

					InfoHelper.outputText("success" + operationResult.toString(), response);
				} else {
					InfoHelper.outputText("Failed", response);
				}
			} else {
				// 原稿的复制
				String error = saveCopy(request, docLibID, docIDs, columnID, "0");

				String url = getAfterUrl(request, DocIDs, null, error);
				response.sendRedirect(url);
			}
		}
	}

	private String getAfterUrl(HttpServletRequest request, String DocIDs, String opinion, String error)
			throws Exception {
		return getAfterUrl(WebUtil.get(request, "UUID"), DocIDs, opinion, error);
	}

	private String getAfterUrl(String UUID, String DocIDs, String opinion, String error) throws Exception {
		String url = "/e5workspace/after.do?UUID=" + UUID;
		if (error == null) {
			url += "&DocIDs=" + DocIDs; // 操作成功
			if (opinion != null) {
				url += "&Opinion=" + URLEncoder.encode(opinion, "UTF-8");
			}
		} else {
			url += "&Info=" + URLEncoder.encode(error, "UTF-8");// 有错误，需返回前台做提示
		}
		return url;
	}

	/**
	 * 移动稿件 多篇稿件移动到一个栏目下
	 *
	 * @param DocIDs
	 *            多篇的稿件ID
	 * @param DocLibID
	 *            稿件库ID
	 */
	@RequestMapping(value = "move.do")
	public void move(HttpServletRequest request, HttpServletResponse response, @RequestParam String DocIDs,
			@RequestParam String DocLibID, @RequestParam long colID) throws Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		int docLibID = Integer.parseInt(DocLibID);
		long[] docIDs = StringUtils.getLongArray(DocIDs);
		if (docIDs.length <= 0) {
			InfoHelper.outputText("Failed", response);
			return;
		}

		// 取出现有的主栏目ID，判断选中的是否是自己所述的主栏目
		long oldColID = 0;
		String oldcolName = "";
		Document article = docManager.get(docLibID, docIDs[0]);
		if (article != null) {
			oldColID = Long.parseLong(article.getString("a_columnID"));
			oldcolName = article.getString("a_column");
		}
		if (oldColID == colID) {
			InfoHelper.outputText("samecol", response);
			return;
		}

		// 移动的稿件的信息的修改
		List<Document> articles = fillMoveList(docIDs, docLibID, colID);

		// 同时修改多个稿件,使用事物保存稿件的主栏目、关联栏目、所有栏目信息
		String error = save(docLibID, articles);

		// 操作成功后写日志
		StringBuilder operationResult = new StringBuilder();
		if (error == null) {
			operationResult.append("来自：").append(oldcolName);
			InfoHelper.outputText("success" + operationResult.toString(), response);
		} else {
			InfoHelper.outputText("Failed", response);
		}
	}

	/**
	 * 推荐稿件 将稿件推荐到其他栏目，在其他栏目下生成链接稿件
	 * 
	 * @param DocIDs
	 *            多篇的稿件ID
	 * @param DocLibID
	 *            稿件库ID
	 * @param colIDs
	 *            多个栏目ID
	 */
	@RequestMapping(value = "recommend.do")
	public void recommend(HttpServletRequest request, HttpServletResponse response, @RequestParam String DocIDs,
			@RequestParam String DocLibID, @RequestParam String colIDs) throws Exception {
		int[] articleIDs = StringUtils.getIntArray(DocIDs);
		int artLibID = Integer.parseInt(DocLibID);
		int colLibID = LibHelper.getColumnLibID(request);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		for (int articleID : articleIDs) {
			List<Document> articles = new ArrayList<>();
			Document oldArticle = docManager.get(artLibID, articleID);
			String[] newrefCols = colIDs.split(",");
			// 获取新的关联栏目名称
			StringBuilder newrefColsId = new StringBuilder();
			StringBuilder newrefColsName = new StringBuilder();
			StringBuilder details = new StringBuilder();
			details.append("推荐到：");
			for (int i = 0; i < newrefCols.length; i++) {
				if (i > 0) {
					newrefColsId.append(",");
					newrefColsName.append(",");
					details.append("，");
				}
				String colName = getColumnName(colLibID, Integer.parseInt(newrefCols[i]));

				newrefColsId.append(newrefCols[i]);
				newrefColsName.append(colName);
				details.append(colName).append("(id=").append(newrefCols[i]).append(")");
				// 打开写稿界面时就预取稿件ID
				long docID = InfoHelper.getNextDocID(DocTypes.ARTICLE.typeID()); // 提前取稿件ID
				com.founder.e5.doc.Document newArticle = docManager.newDocument(artLibID, docID);
				newArticle.set("a_originalID", articleID);
				newArticle.set("a_columnID", newrefCols[i]);
				newArticle.set("a_column", colName);
				newArticle = prepublish(newArticle, oldArticle, request);
				articles.add(newArticle);
			}
			// 保存链接稿件
			String error = saveRecommend(artLibID, oldArticle, articles);
			if (error == null) {
				// 为每个稿件写日志
				writeRecLog(ProcHelper.getUser(request), oldArticle, articles, details.toString());
			} else {
				InfoHelper.outputText("Failed" + error, response);
				return;
			}
		}
		InfoHelper.outputText("success", response); // 操作成功
	}

	private String saveRecommend(int docLibID, Document oldArticle, List<Document> articles) {
		if (articles == null || articles.size() == 0)
			return null;

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		// 同时修改多个稿件，使用事务
		DBSession conn = null;
		try {
			conn = E5docHelper.getDBSession(docLibID);
			conn.beginTransaction();

			for (Document article : articles) {
				docManager.save(article, conn);
				saveExtFields(article, oldArticle, conn);
				saveAttachments(article, oldArticle, conn);
			}
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

	/**
	 * 关联栏目 多篇稿件关联到多个栏目下
	 * 
	 * @param DocIDs
	 *            多篇的稿件ID
	 * @param DocLibID
	 *            稿件库ID
	 * @param colIDs
	 *            多个栏目ID
	 */
	@RequestMapping(value = "relColumn.do")
	public void relcolumn(HttpServletRequest request, HttpServletResponse response, @RequestParam String DocIDs,
			@RequestParam String DocLibID, @RequestParam String colIDs, @RequestParam long oldColID) throws Exception {
		int[] articleIDs = StringUtils.getIntArray(DocIDs);
		int artLibID = Integer.parseInt(DocLibID);
		int colLibID = LibHelper.getColumnLibID(request);

		List<Document> articles = new ArrayList<>();
		List<String> logdetails = new ArrayList<>();
		List<String> newcolList = new ArrayList<>();

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		relColumn(articles, logdetails, newcolList, colLibID, colIDs, artLibID, articleIDs, docManager, oldColID);
		String error = save(artLibID, articles);

		if (error == null) {
			// 检查，若稿件已发布，则发布消息
			publishRel(articles, newcolList);

			// 更新排序
			updateRelOrder(articles, newcolList);

			// 为每个稿件写日志
			writeRelLog(ProcHelper.getUser(request), articles, logdetails);

			InfoHelper.outputText("success", response); // 操作成功
		} else {
			InfoHelper.outputText("Failed" + error, response);
		}
	}

	private void updateRelOrder(List<Document> articles, List<String> newcolList) {
		if (articles.size() > 0 && newcolList.size() > 0) {
			try {
				StringBuffer sql = null;
				for (int i = 0; i < articles.size(); i++) {
					String colIDs = newcolList.get(i);
					if (!StringUtils.isBlank(colIDs)) {
						Document article = articles.get(i);
						// String tableName =
						// LibHelper.getLibTable(article.getDocLibID());
						String tableName = InfoHelper.getRelTable(article.getDocLibID());
						Document newArt = (Document) article.clone();
						newArt.set("a_pubTime", DateUtils.getTimestamp());
						double order = articleManager.getNewOrder(newArt);
						sql = new StringBuffer("UPDATE ");
						sql.append(tableName).append(" set a_order=? where SYS_DOCUMENTID=? and CLASS_1 in (")
								.append(colIDs).append(")");
						InfoHelper.executeUpdate(sql.toString(), new Object[] { order, article.getDocID() });
					}

				}

			} catch (E5Exception | CloneNotSupportedException e) {
				e.printStackTrace();
			}

		}

	}

	private void relColumn(List<Document> articles, List<String> logdetails, List<String> newcolList, int colLibID,
			String colIDs, int artLibID, int[] articleIDs, DocumentManager docManager, long oldColID) throws Exception {
		HashMap<Integer, Integer> docID_attrMap = getDocID_attrMap(artLibID, oldColID, articleIDs);

		for (int articleID : articleIDs) {
			Document article = docManager.get(artLibID, articleID);

			// 清楚关联栏目中的主栏目
			String cols = article.getString("a_columnID");
			String oldrefColsId = article.getString("a_columnRelID");
			if (oldrefColsId != null && !oldrefColsId.isEmpty()) {
				cols = cols + "," + oldrefColsId;
			}
			// 清理掉主栏目、已有的关联栏目
			String clearResult = clearCols(cols, colIDs);
			if (clearResult == null || clearResult.isEmpty())
				continue;

			String oldrefColsName = article.getString("a_columnRel");
			String[] newrefCols = clearResult.split(",");
			// 获取新的关联栏目名称
			StringBuffer newrefColsId = new StringBuffer();
			StringBuffer newrefColsName = new StringBuffer();
			StringBuffer details = new StringBuffer();
			for (int i = 0; i < newrefCols.length; i++) {
				if (i > 0) {
					newrefColsId.append(",");
					newrefColsName.append(",");
					details.append("，");
				}
				String colName = getColumnName(colLibID, Integer.parseInt(newrefCols[i]));

				newrefColsId.append(newrefCols[i]);
				newrefColsName.append(colName);
				details.append(colName).append("(id=").append(newrefCols[i]).append(")");
			}
			if (oldrefColsId != null && !oldrefColsId.isEmpty()) {
				article.set("a_columnRelID", oldrefColsId + "," + newrefColsId.toString());
				article.set("a_columnRel", oldrefColsName + "," + newrefColsName.toString());
			} else {
				article.set("a_columnRelID", newrefColsId.toString());
				article.set("a_columnRel", newrefColsName.toString());
			}
			// 先按a_columnAll拆解得到columnID的TreeSet（集合），然后把新设置的栏目ID加到Set里，最后把集合元素拼起来得到a_columnAl
			setColumnAll(article, newrefColsId.toString());

			// 做稿件关联操作的时候，给稿件的”最后修改时间“字段赋一下值，改为最新时间，便于更新solr
			article.setLastmodified(DateUtils.getTimestamp());
			article.set("a_attr", docID_attrMap.get(articleID)); // 稿件关联到其他栏目，稿件属性默认为普通稿件

			articles.add(article);
			//newcolList.add(clearResult);
	        //返回的新栏目ID需包含聚合的栏目
	        long[] colAllArr = StringUtils.getLongArray(article.getString("a_columnAll"), ";");
	        long[] colRelArr = StringUtils.getLongArray(article.getString("a_columnRelID"), ",");
	        long[] refCols   = StringUtils.getLongArray(colIDs);
	        long colID = article.getInt("a_columnID");
	        String colAggStr = "";
	        for(int i = 0; i < colAllArr.length; i++){
	        	if(ArrayUtils.contains(refCols, colAllArr[i])) continue;
	        	if(ArrayUtils.contains(colRelArr, colAllArr[i])) continue;
	        	if(colID == colAllArr[i]) continue;
	        	if(colAggStr.length() > 0) colAggStr += ",";
	        	colAggStr += colAllArr[i];
	        }
	        if(!StringUtils.isBlank(clearResult) && !StringUtils.isBlank(colAggStr)){
	        	clearResult += "," + colAggStr;
	        }else if(StringUtils.isBlank(clearResult) && !StringUtils.isBlank(clearResult)){
	        	clearResult = colAggStr;
	        }
			newcolList.add(clearResult);
			logdetails.add(details.toString());
		}
	}

	private HashMap<Integer, Integer> getDocID_attrMap(int artLibID, long colID, int[] articleIDs) {
		HashMap<Integer, Integer> docID_attrMap = new HashMap<>();
		if (colID == -1)
			return docID_attrMap;
		String attrSQL = "SELECT SYS_DOCUMENTID, a_attr FROM " + InfoHelper.getRelTable(artLibID)
				+ " WHERE class_1 = ? AND  SYS_DOCUMENTID IN ( " + StringUtils.join(articleIDs, ",") + " )";
		DBSession db = null;
		IResultSet rs = null;

		DocLib docLib = LibHelper.getLibByID(artLibID);
		try {
			db = Context.getDBSession(docLib.getDsID());
			rs = db.executeQuery(attrSQL, new Object[] { colID });
			while (rs.next()) {
				docID_attrMap.put(rs.getInt("SYS_DOCUMENTID"), rs.getInt("a_attr"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(db);
		}
		return docID_attrMap;
	}

	/**
	 * 操作：解除关联
	 */
	@RequestMapping(value = "RemoveRel.do")
	public String removeRel(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int DocIDs = WebUtil.getInt(request, "DocIDs", 0);
		int DocLibID = WebUtil.getInt(request, "DocLibID", 0);
		long colID = WebUtil.getLong(request, "colID", 0);

		String url = "redirect:/e5workspace/after.do?UUID=" + WebUtil.get(request, "UUID");

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document article = docManager.get(DocLibID, DocIDs);

		// 主栏目不可解除关联
		if (colID == article.getLong("a_columnID")) {
			String opinion = java.net.URLEncoder.encode("主栏目不可解除关联", "UTF-8");
			url += "&Info=" + opinion;
			return url;
		}

		int colLibID = LibHelper.getColumnLibID(request);
		String colName = getColumnName(colLibID, colID);

		// 关联栏目
		Set<Long> relSet = new TreeSet<Long>();
		boolean isRel = isRel(article, colID, relSet);

		if (isRel) {
			// 当前栏目是稿件的关联栏目,修改稿件的关联栏目ID和关联栏目名称
			dealRelCols(colName, article, relSet);
		}
		// 聚合栏目改宿主后，不是关联栏目，不需处理columnRel,但需要处理columnAll modify by kangxw at
		// 2017-10-31 17:51:41
		// 处理一下所有栏目,修改a_columnAll字段去掉该栏目ID
		dealAllCols(colID, article);

		docManager.save(article);

		// 若稿件已发布，调用PubTrigger. articleOrder触发消息
		if (article.getInt("a_status") == Article.STATUS_PUB_DONE) {
			PublishTrigger.articleOrder(colLibID, colID);
		}

		url += "&DocIDs=" + DocIDs + "&Opinion=" + URLEncoder.encode("栏目：" + colName, "UTF-8");
		return url;
	}

	@RequestMapping(value = "relSiteCloumn.do")
	public String relSiteCloumn(HttpServletRequest request, HttpServletResponse response, @RequestParam String DocIDs,
			@RequestParam String DocLibID, @RequestParam String colID, @RequestParam int siteID,
			@RequestParam String UUID) throws Exception {
		if (DocIDs == null || DocIDs.isEmpty()) {
			String url = "/e5workspace/after.do?UUID=" + UUID;
			return "redirect:" + url;
		}

		int[] articleIDs = StringUtils.getIntArray(DocIDs);
		int artLibID = Integer.parseInt(DocLibID);
		int colLibID = LibHelper.getColumnLibID(request);

		List<Document> articles = new ArrayList<>();
		List<String> logdetails = new ArrayList<>();
		List<String> newcolList = new ArrayList<>();

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		// 跨站点关联可能会从多个栏目下选稿关联，无法确定稿件栏目来源
		relColumn(articles, logdetails, newcolList, colLibID, colID, artLibID, articleIDs, docManager, -1);
		String error = save(artLibID, articles);

		String relTable = InfoHelper.getRelTable(artLibID);
		StringBuilder sql = new StringBuilder();
		List<Object> params = new ArrayList<>();
		sql.append("UPDATE ").append(relTable).append(" SET a_siteID = ? WHERE class_1 = ? and SYS_DOCUMENTID IN (");
		params.add(siteID);
		params.add(colID);
		for (int articleID : articleIDs) {
			sql.append("?,");
			params.add(articleID);
		}
		sql.deleteCharAt(sql.length() - 1).append(")");
		InfoHelper.executeUpdate(sql.toString(), params.toArray());

		String url = "/e5workspace/after.do?UUID=" + UUID;
		if (error == null) {
			// 检查，若稿件已发布，则发布消息
			publishRel(articles, newcolList);

			// 为每个稿件写日志
			int siteLibID = LibHelper.getLibIDByOtherLib(DocTypes.SITE.typeID(), artLibID);
			writeRelSiteLog(docManager, siteLibID, siteID, ProcHelper.getUser(request), articles, logdetails);

			url += "&DocIDs=" + DocIDs;
		}
		return "redirect:" + url;
	}

	@RequestMapping("SiteArticle.do")
	public String SiteArticle(HttpServletRequest request, HttpServletResponse response, @RequestParam String DocIDs,
			@RequestParam String DocLibID, @RequestParam long colID, @RequestParam int siteID,
			@RequestParam int originalSiteID, @RequestParam String UUID) throws Exception {
		if (DocIDs == null || DocIDs.isEmpty()) {
			String url = "/e5workspace/after.do?UUID=" + UUID;
			return "redirect:" + url;
		}

		int docLibID = Integer.parseInt(DocLibID);
		long[] docIDs = StringUtils.getLongArray(DocIDs);
		StringBuffer strDocIDs = new StringBuffer();
		List<String> logdetails = new ArrayList<>();
		long[] newDocIDs = new long[docIDs.length];
		DocumentManager docManager = DocumentManagerFactory.getInstance();

		int siteLibID = LibHelper.getLibIDByOtherLib(DocTypes.SITE.typeID(), docLibID);
		String originalSiteName = docManager.get(siteLibID, originalSiteID).getString("site_name");
		;

		// 同时修改多个稿件，使用事务
		DBSession conn = null;
		try {
			conn = E5docHelper.getDBSession(docLibID);
			conn.beginTransaction();

			for (int i = 0; i < docIDs.length; i++) {
				long articleID = docIDs[i];
				Document article = docManager.get(docLibID, articleID);
				long newArticleID = InfoHelper.getNextDocID(DocTypes.ARTICLE.typeID());
				Document newArt = docManager.newDocument(article, docLibID, newArticleID);
				newDocIDs[i] = newArticleID;
				Timestamp now = DateUtils.getTimestamp();
				newArt.setCreated(now);
				newArt.setLastmodified(now);
				newArt.setLocked(false);
				newArt.set("a_siteID", siteID);
				newArt.set("a_pubTime", now); // 发布时间
				newArt.set("a_status", Article.STATUS_PUB_NOT);
				newArt.set("a_priority", "");
				newArt.set("a_position", 0);
				newArt.set("a_order", articleManager.getNewOrder(newArt));
				// 设置主栏目
				int colLibID = LibHelper.getColumnLibID(request);
				String colName = getColumnCascadeName(colLibID, colID);
				newArt.set("a_column", colName);
				newArt.set("a_columnID", colID);
				// 清除关联栏目
				newArt.set("a_columnRel", "");
				newArt.set("a_columnRelID", "");
				// 清除发布地址
				newArt.set("a_url", "");
				newArt.set("a_urlPad", "");
				newArt.set("a_originalID", article.getString("a_originalID"));
				// 清除模板信息
				newArt.set("a_template", "");
				newArt.set("a_templateID", 0);
				newArt.set("a_templatePad", "");
				newArt.set("a_templatePadID", 0);
				// 完善栏目流程信息
				articleManager.setColumnAll(newArt);
				articleManager.setFlowByColumn(newArt);
				docManager.save(newArt, conn);
				saveAttachments(newArt, article, conn);

				logdetails.add("源:" + originalSiteName + " -> " + getColumnName(colLibID, article.getLong("a_columnID"))
						+ " -> 稿件ID=" + article.getDocID());
			}
			conn.commitTransaction();
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
		for (int i = 0; i < newDocIDs.length; i++) { // 写日志
			long newDocID = newDocIDs[i];
			strDocIDs.append(newDocID).append(",");
			LogHelper.writeLog(docLibID, newDocID, ProcHelper.getUser(request), "跨站点选稿", logdetails.get(i));
		}
		String url = "/e5workspace/after.do?UUID=" + UUID + "&DocIDs=" + strDocIDs.toString();
		return "redirect:" + url;
	}

	/**
	 * 复制稿件以及附件、挂件， 出错时返回错误信息
	 */
	private String saveCopy(HttpServletRequest request, int docLibID, long[] docIDs, long colID, String puborApr)
			throws Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();

		long[] newDocIDS = new long[docIDs.length];
		List<Document> newArticles = new ArrayList<Document>();
		List<Document> newAppArticles = new ArrayList<Document>();
		// 同时修改多个稿件，使用事务
		DBSession conn = null;
		try {
			conn = E5docHelper.getDBSession(docLibID);
			conn.beginTransaction();

			// 遍历取出稿件文件对象
			for (int i = 0; i < docIDs.length; i++) {
				long articleID = docIDs[i];
				Document article = docManager.get(docLibID, articleID);
				// 复制稿件
				long newArticleID = InfoHelper.getNextDocID(DocTypes.ARTICLE.typeID());
				newDocIDS[i] = newArticleID;
				Document newArt = docManager.newDocument(article, docLibID, newArticleID);
				newArt.setLocked(false);

				Timestamp now = DateUtils.getTimestamp();
				newArt.setCreated(now);
				newArt.setLastmodified(now);
				
				//改为先处理附件挂件，应为复制图片要重新抽图
				// 保存扩展字段
				saveExtFields(newArt, article, conn);
				// 保存挂件
				saveWidgets(newArt, article, conn);
				// 保存附件
				saveAttachments(newArt, article, conn);
				// 保存相关稿件
				saveArticleRel(newArt, article, conn);
				
				if (colID > 0) {
					newArt.set("a_pubTime", now); // 发布时间
					// 设置主栏目
					int colLibID = LibHelper.getColumnLibID(request);
					String colName = getColumnCascadeName(colLibID, colID);

					newArt.set("a_column", colName);
					newArt.set("a_columnID", colID);

					// 清空关联栏目
					newArt.set("a_columnRel", "");
					newArt.set("a_columnRelID", "");

					// 清空发布地址
					if (article.getInt("a_type") != Article.TYPE_LINK && article.getInt("a_type") != Article.TYPE_AD
							&& article.getInt("a_type") != Article.TYPE_H5) {
						newArt.set("a_url", "");
						newArt.set("a_urlPad", "");
					}
					newArt.set("a_status", Article.STATUS_PUB_NOT);

					// 填写“原稿ID”字段为选定稿件的原稿ID
					newArt.set("a_originalID", article.getString("a_originalID"));
					articleManager.setColumnAll(newArt);
					// 按主栏目设置流程
					articleManager.setFlowByColumn(newArt);

					if (puborApr != null && puborApr.equals("0")) {
						newArt.set("a_status", Article.STATUS_PUB_NOT);
					} else if (puborApr != null && puborApr.equals("1")) {
						newArt.set("a_status", Article.STATUS_PUB_ING);
						SysUser user = ProcHelper.getUser(request);
						newArt.set("a_lastPublish", user.getUserName());
						newArt.set("a_lastPublishID", user.getUserID());
					} else if (puborApr != null && puborApr.equals("2")) {
						newArt.set("a_status", Article.STATUS_AUDITING);
					}

					// 新稿件的order要重新计算
					double order = articleManager.getNewOrder(newArt);
					newArt.set("a_order", order);

					// 保存话题
					saveTopics(newArt, article, conn);
					
					Column column = colReader.get(colLibID, colID);
					long pushColID = column.getPushColumn();
					Column pushColumn = colReader.get(colLibID, pushColID);
					int appLibID = LibHelper.getArticleAppLibID();
					if(pushColID > 0 && article.getInt("a_channel") == 1 
							&& article.getInt("a_type") != Article.TYPE_SPECIAL&&pushColumn!=null) {
						Document newAppArt = docManager.newDocument(newArt, appLibID, newArt.getDocID());
						newAppArt.setLocked(false);
						newAppArt.set("a_column", getColumnCascadeName(colLibID, pushColID));
						newAppArt.set("a_columnID", pushColID);
						newAppArt.set("a_columnRel", "");
						newAppArt.set("a_columnRelID", "");
						newAppArt.set("a_channel", 2);
						
						articleManager.setColumnAll(newAppArt);
						// 按主栏目设置流程。
						articleManager.setFlowByColumn(newAppArt);
						
						if (puborApr != null && puborApr.equals("0")) {
							newAppArt.set("a_status", Article.STATUS_PUB_NOT);
						} else if (puborApr != null && puborApr.equals("1")) {
							newAppArt.set("a_status", Article.STATUS_PUB_ING);
							SysUser user = ProcHelper.getUser(request);
							newAppArt.set("a_lastPublish", user.getUserName());
							newAppArt.set("a_lastPublishID", user.getUserID());
						} else if (puborApr != null && puborApr.equals("2")) {
							newAppArt.set("a_status", Article.STATUS_AUDITING);
						}
						
						saveExtFields(newAppArt, newArt, conn);
						// 保存挂件
						saveWidgets(newAppArt, newArt, conn);
						// 保存附件
						saveAttachments(newAppArt, newArt, conn);
						// 保存相关稿件
						saveArticleRel(newAppArt, newArt, conn);
						// 保存话题
						saveTopics(newAppArt, newArt, conn);
						
						docManager.save(newAppArt, conn);
						
						if (puborApr != null && puborApr.equals("1")) {
							newAppArticles.add(newAppArt);
						}
					}
					if (puborApr != null && puborApr.equals("1")) {
						newArticles.add(newArt);
					}
				} else {
					newArt.set("a_channel", 0); // 原稿复制，清空已签发渠道的标记
				}
				docManager.save(newArt, conn);
			}
			conn.commitTransaction();
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			e.printStackTrace();
			return "操作中出现错误：" + e.getLocalizedMessage();
		} finally {
			ResourceMgr.closeQuietly(conn);
		}

		if (puborApr != null && puborApr.equals("1")) {
			articleManager.publish(newArticles);
			articleManager.publish(newAppArticles);
		}

		// 走到这里说明已经保存成功了，应该添加日志
		SysUser sysUser = ProcHelper.getUser(request);
		writeCopyLog(sysUser, newDocIDS, docIDs, docLibID);
		return null;
	}

	private void saveTopics(Document article, Document srcArticle, DBSession conn) throws E5Exception {
		String querySql = "SELECT a_topicID, a_topicName from xy_topicrelart where a_articleID=? and a_channel=?";
		Object[] param = new Object[]{srcArticle.getDocID(),srcArticle.getInt("a_channel")};
		IResultSet rs = null;
		String insertSql = "insert into xy_topicrelart(a_topicID,a_articleID,a_topicName,a_order,a_channel,a_siteID,a_type,a_status) values(?,?,?,?,?,?,?,?)";
		try {
			rs = conn.executeQuery(querySql, param);
			while (rs.next()) {
				InfoHelper.executeUpdate(insertSql, new Object[]{rs.getLong("a_topicID"), article.getDocID(),
						rs.getString("a_topicName"),  article.getDouble("a_order"),
						article.getInt("a_channel"), article.getInt("a_siteID"),
						article.getInt("a_type"), Article.STATUS_PUB_NOT}, conn);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(rs);
		}
	}

	/**
	 * 修改要移动稿件的主栏目信息
	 */
	private List<Document> fillMoveList(long[] docIDs, int docLibID, long colID) throws Exception {

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		int colLibID = LibHelper.getLibIDByOtherLib(DocTypes.COLUMN.typeID(), docLibID);
		String colName = getColumnName(colLibID, colID);

		List<Document> articles = new ArrayList<Document>();
		// 修改稿件的主栏目信息
		for (long articleID : docIDs) {
			Document article = docManager.get(docLibID, articleID);
			// 对于已发布的稿件，要发出撤稿消息，然后改变主栏目
			if (article.getInt("a_status") == Article.STATUS_PUB_DONE) {
				PublishTrigger.articleRevoke(article);
			}
			// 改变主栏目
			article.set("a_column", colName);
			article.set("a_columnID", colID);

			if (article.getInt("a_type") != Article.TYPE_LINK) {
				// 清空发布地址
				article.set("a_url", "");
				article.set("a_urlPad", "");
			}
			// 更新排序
			Document newArt = (Document) article.clone();
			newArt.set("a_pubTime", DateUtils.getTimestamp());
			article.set("a_order", articleManager.getNewOrder(newArt));

			articleManager.setColumnAll(article);
			articleManager.setFlowByColumn(article);
			articles.add(article);
		}

		return articles;
	}

	private String getColumnName(int colLibID, long colID) throws E5Exception {
		Column col = colReader.get(colLibID, colID);
		return (col == null) ? "" : col.getName();
	}

	private String getColumnCascadeName(int colLibID, long colID) throws E5Exception {
		Column col = colReader.get(colLibID, colID);
		return (col == null) ? "" : col.getCasNames();
	}

	private void setColumnAll(Document article, String newrefColsId) {
		// 去除原来的所有稿件ID
		Set<Long> allIdSet = new TreeSet<Long>();
		String columnAllId = article.getString("a_columnAll");
		if (!StringUtils.isBlank(columnAllId)) {
			long[] ids = StringUtils.getLongArray(columnAllId, ";");
			for (long id : ids) {
				allIdSet.add(id);
			}
		}
		// 添加新的稿件ID
		long[] newids = StringUtils.getLongArray(newrefColsId);
		for (long id : newids) {
			allIdSet.add(id);
		}
		//添加关联栏目的聚合栏目
		Long[] allIdArray = (Long[])allIdSet.toArray(new Long[0]);
        int colLibID = LibHelper.getLibIDByOtherLib(DocTypes.COLUMN.typeID(), article.getDocLibID());
        for (Long allId : allIdArray) {
          Set<Long> aggregatedSet = this.colReader.getAggregators(colLibID, allId.longValue());
          if (aggregatedSet != null) {
            for (Long id : aggregatedSet) {
              allIdSet.add(id);
            }
          }
        }
        
		StringBuilder column_allId = new StringBuilder();
		for (Long l : allIdSet) {
			if (column_allId.length() > 0)
				column_allId.append(";");
			column_allId.append(String.valueOf(l));
		}
		article.set("a_columnAll", column_allId.toString());
	}

	/**
	 * 去掉选择的栏目里面含有的主栏目以及已经关联好的关联栏目
	 */
	private String clearCols(String cols, String relColIDs) throws Exception {
		StringBuffer operationResult = new StringBuffer();
		int[] refCols = StringUtils.getIntArray(relColIDs);
		int[] clearIDs = StringUtils.getIntArray(cols);

		for (int i = 0; i < refCols.length; i++) {
			int refCol = refCols[i];
			boolean bequal = false;
			for (int j = 0; j < clearIDs.length; j++) {
				if (refCol == clearIDs[j]) {
					bequal = true;
				}
			}
			if (!bequal) {
				if (operationResult.length() != 0) {
					operationResult.append(",");
				}
				operationResult.append(refCol);
			}
		}
		return operationResult.toString();
	}

	private void publishRel(List<Document> articles, List<String> newcolList) throws E5Exception {
		for (int i = 0; i < articles.size(); i++) {
			Document article = articles.get(i);
			if (article != null && (Article.STATUS_PUB_DONE == article.getInt("a_status")
					|| Article.STATUS_PUB_ING == article.getInt("a_status"))) {
				int[] colIDs = StringUtils.getIntArray(newcolList.get(i));
				PublishTrigger.articleRel(article.getDocLibID(), article.getDocID(), colIDs);
			}
		}
	}

	private boolean isRel(Document article, long colID, Set<Long> relSet) {
		boolean isRel = false;
		String relColumnId = article.getString("a_columnRelID");
		if (!StringUtils.isBlank(relColumnId)) {
			long[] ids = StringUtils.getLongArray(relColumnId);
			for (long id : ids) {
				if (id != colID) {
					relSet.add(id);
				} else {
					isRel = true;
				}
			}
		}
		return isRel;
	}

	/**
	 * 当撤稿成功之后写日志
	 */
	private void writeRevokeLog(SysUser sysUser, int docLibID, long[] docIDs, String detail) throws Exception {
		for (long docID : docIDs) {
			LogHelper.writeLog(docLibID, docID, sysUser, "撤稿", detail);
		}
	}

	/**
	 * 当稿件复制成功之后写日志
	 */
	private void writeCopyLog(SysUser sysUser, long[] newDocIDS, long[] docIDs, int docLibID) throws Exception {
		for (int i = 0; i < newDocIDS.length; i++) {
			LogHelper.writeLog(docLibID, newDocIDS[i], sysUser, "复制", "源ID：" + docIDs[i]);
		}
	}

	/**
	 * 当稿件推荐成功之后写日志
	 */
	private void writeRecLog(SysUser sysUser, Document oldArticle, List<Document> articles, String logdetails) {
		LogHelper.writeLog(oldArticle.getDocLibID(), oldArticle.getDocID(), sysUser, "推荐栏目", logdetails);
		for (int i = 0; i < articles.size(); i++) {
			Document article = articles.get(i);
			LogHelper.writeLog(article.getDocLibID(), article.getDocID(), sysUser, "推荐",
					"源ID：" + oldArticle.getDocID());
		}
	}

	/**
	 * 当稿件关联栏目成功之后写日志
	 */
	private void writeRelLog(SysUser sysUser, List<Document> articles, List<String> details) throws Exception {
		for (int i = 0; i < articles.size(); i++) {
			Document article = articles.get(i);
			LogHelper.writeLog(article.getDocLibID(), article.getDocID(), sysUser, "关联栏目", "关联到：" + details.get(i));
		}
	}

	/**
	 * 当跨站点关联栏目成功之后写日志
	 */
	private void writeRelSiteLog(DocumentManager docManager, int siteLibID, int siteID, SysUser sysUser,
			List<Document> articles, List<String> details) throws Exception {
		for (int i = 0; i < articles.size(); i++) {
			Document article = articles.get(i);
			Document site = docManager.get(siteLibID, article.getInt("a_siteID"));
			Document relSite = docManager.get(siteLibID, siteID);
			StringBuffer detail = new StringBuffer();
			detail.append(site.getString("site_name")).append("(").append("id=").append(site.getDocID()).append(")");
			detail.append(":").append(article.getString("a_column")).append("(").append("id=")
					.append(article.getString("a_columnID")).append(")");
			detail.append(" 关联到  ");
			detail.append(relSite.getString("site_name")).append("(").append("id=").append(relSite.getDocID())
					.append(")");
			detail.append(":").append(details.get(i));
			LogHelper.writeLog(article.getDocLibID(), article.getDocID(), sysUser, "跨站点关联", detail.toString());
		}
	}

	/**
	 * 合成多标题
	 */
	@RequestMapping(value = "ComposeArt.do")
	public ModelAndView composeart(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();

		long[] DocIDs = StringUtils.getLongArray(request.getParameter("DocIDs"));
		int DocLibID = WebUtil.getInt(request, "DocLibID", 0);
		int colID = WebUtil.getInt(request, "colID", 0);

		int colLibID = LibHelper.getColumnLibID(request);
		String colName = getColumnName(colLibID, colID);

		int ch = WebUtil.getInt(request, "ch", 0);
		String artLinks = getArtlinkByIds(DocLibID, DocIDs, ch);
		model.put("UUID", WebUtil.get(request, "UUID"));
		model.put("siteID", WebUtil.getInt(request, "siteID", 0));
		model.put("ch", ch);
		model.put("colID", colID);
		model.put("colName", colName);
		model.put("DocLibID", DocLibID);
		model.put("DocIDs", request.getParameter("DocIDs"));
		model.put("content", artLinks);
		model.put("op", 1);
		model.put("currentColID", colID);

		return new ModelAndView("/xy/article/Compose", model);
	}

	/**
	 * 处理多标题
	 */
	@RequestMapping(value = "dealComposeArt.do")
	public void pubComposeArt(HttpServletRequest request, HttpServletResponse response, SubmitInfo parameters)
			throws Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		int docLibID = Integer.parseInt(parameters.getDocLibID());
		long docID = Long.parseLong(parameters.getDocIDs());
		SysUser sysUser = ProcHelper.getUser(request);
		int roleID = sysUser.getRoleID();

		int mainColID = Integer.parseInt(parameters.getMainColId());
		String mainColName = parameters.getMainColName();
		String relColIDs = parameters.getRefColIds();
		String[] relCol = null;
		if (!relColIDs.isEmpty()) {
			relCol = dealRelCol(mainColID, relColIDs, parameters.getRefColNames());
		}

		int currentColID = WebUtil.getInt(request, "currentColID", 0);
		// 获取是保存还是发布
		boolean bPublish = "true".equals(parameters.getIsTransfer());
		String linkTitle = parameters.getLinkTitle();

		Document article = docManager.get(docLibID, docID);
		if (article == null) {
			// 生成多标题新稿件，设置内容
			article = docManager.newDocument(Integer.parseInt(parameters.getDocLibID()),
					Long.parseLong(parameters.getDocIDs()));
			ProcHelper.initDoc(article, request);
			article.set("SYS_AUTHORID", sysUser.getUserID());
			article.set("a_editor", sysUser.getUserName());
			article.set("a_pubTime", DateUtils.getTimestamp()); // 发布时间
			article.set("a_type", Article.TYPE_MULTITITLE);
			article.set("a_siteID", parameters.getSiteID());

			// 设置顺序
			double order = articleManager.getNewOrder(article);
			article.set("a_order", order);

			// 设置主栏目
			article.set("a_column", parameters.getMainColName());
			article.set("a_columnID", parameters.getMainColId());
			// 设置关联栏目
			if (relCol != null) {
				article.set("a_columnRel", relCol[1]);
				article.set("a_columnRelID", relCol[0]);
			}
			// 所有栏目设置
			articleManager.setColumnAll(article);
		}

		// 设置这些稿件为多标题稿件,包括标题、链接标题的设置
		article.setTopic(linkTitle);
		article.set("a_linkTitle", linkTitle);

		articleManager.saveLinkTitle(article, currentColID, false);

		if (bPublish) {
			articleManager.tryPublish(article, Integer.parseInt(parameters.getMainColId()), roleID);
		} else {
			article.set("a_status", Article.STATUS_PUB_NOT);
		}
		docManager.save(article);

		if (bPublish) {
			// 执行发布操作
			PublishTrigger.article(article);
		}
		// 操作成功后写日志
		StringBuilder operationResult = new StringBuilder();
		if (relCol != null) {
			operationResult.append("【主栏目】：").append(mainColName).append(" 【关联栏目】：").append(relCol[1]);
		} else {
			operationResult.append("【主栏目】：").append(mainColName);
		}
		InfoHelper.outputText("success" + operationResult.toString(), response); // 操作成功
	}

	/**
	 * 获取标题链接
	 */
	@RequestMapping(value = "getArtLink.do")
	public void getartlink(HttpServletRequest request, HttpServletResponse response) throws Exception {

		long[] DocIDs = StringUtils.getLongArray(request.getParameter("DocIDs"));
		int DocLibID = WebUtil.getInt(request, "DocLibID", 0);

		int ch = WebUtil.getInt(request, "ch", 0);
		String artLinks = getArtlinkByIds(DocLibID, DocIDs, ch);
		if (artLinks == null) {
			InfoHelper.outputText("Failed", response);
		} else {
			InfoHelper.outputText("success" + artLinks, response);
		}
	}

	/**
	 * 处理多标题，保存发布前先获取一个新稿件ID
	 */
	@RequestMapping(value = "getNewDocID.do")
	public void getNewDocID(HttpServletRequest request, HttpServletResponse response, int DocLibID) throws Exception {
		long docID = InfoHelper.getNextDocID(DocTypes.ARTICLE.typeID()); // 提前取稿件ID
		System.out.println(docID);
		InfoHelper.outputText("success" + docID, response);
	}

	@RequestMapping(value = "pubByColumn.do")
	public ModelAndView pubByColumn(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();

		model.put("ch", WebUtil.get(request, "ch"));
		model.put("colID", WebUtil.get(request, "colID"));

		return new ModelAndView("/xy/article/pubByColumn", model);
	}

	@RequestMapping(value = "pubColOperation.do")
	public void pubColOperation(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int ch = WebUtil.getInt(request, "ch", 0);
		long colID = WebUtil.getLong(request, "colID", 0);
		int type = WebUtil.getInt(request, "type", 0);
		int count = WebUtil.getInt(request, "pubCount", 0);
		int pubContent = WebUtil.getInt(request, "pubContent", 0);
		Date begin = parseDate(request.getParameter("pubTime_from"));
		Date end = parseDate(request.getParameter("pubTime_to"));
		String tenantCode = InfoHelper.getTenantCode(request);
		SysUser sysUser = ProcHelper.getUser(request);

		// 取出租户下的发布库。渠道代号是从0开始，正好与发布库一一对应
		List<DocLib> articleLibs = LibHelper.getLibs(DocTypes.ARTICLE.typeID(), tenantCode);
		// 触发发布
		boolean result = PublishTrigger.columnPublish(articleLibs.get(ch).getDocLibID(), colID, begin, end, type,
				pubContent, count);
		if (result) {
			// 写栏目日志
			int colLibID = LibHelper.getColumnLibID(request);
			LogHelper.writeLog(colLibID, colID, sysUser, "按栏目发布", "");
			InfoHelper.outputText("ok", response);
		} else {
			InfoHelper.outputText("无法提交发布消息", response);
		}
	}

	/**
	 * 处理标题链接
	 */
	private String getArtlinkByIds(int DocLibID, long[] DocIDs, int ch) throws Exception {

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		StringBuilder operationResult = new StringBuilder();
		for (long articleID : DocIDs) {
			Document article = docManager.get(DocLibID, articleID);
			String sLinkTitle = article.getString("a_linkTitle");
			String sUrl = "";
			if (ch == 0) {
				sUrl = article.getString("a_url");
			} else {
				sUrl = article.getString("a_urlPad");
			}

			// 若是多标题稿件则只需要取出链接标题
			if (article.getInt("a_type") == 5) {
				operationResult.append(sLinkTitle);
			} else {
				operationResult.append(" <a href=\"").append(sUrl).append("\" target=\"_blank\">").append(sLinkTitle)
						.append("</a>");
			}
		}

		return operationResult.toString();
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

	private Date parseDate(String value) {
		if ("".equals(value) || value == null)
			return null;
		try {
			return new SimpleDateFormat("yyyy-MM-dd").parse(value);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 栏目预览
	 */
	@RequestMapping(value = "viewColumn.do")
	public ModelAndView viewColumn(HttpServletRequest request, HttpServletResponse response) throws Exception {
		long colID = WebUtil.getLong(request, "colID", 0);
		int colLibID = LibHelper.getLibID(DocTypes.COLUMN.typeID(), InfoHelper.getTenantCode(request));

		Map<String, Object> model = new HashMap<String, Object>();
		/*
		 * String[] urls = colReader.getUrls(colLibID, colID); if (urls[0] !=
		 * null) model.put("path0", urls[0]); if (urls[1] != null)
		 * model.put("path1", urls[1]);
		 */

		// 栏目页预览
		DocIDMsg data = new DocIDMsg(colLibID, colID, null);
		ColumnGenerator generator = new ColumnGenerator();
		String[] pages = generator.preview(data);

		if (pages[0] != null)
			model.put("page0", JsoupHelper.replaceImgSuffix(pages[0]));
		if (pages[1] != null)
			model.put("page1", JsoupHelper.replaceImgSuffix(pages[1]));
		return new ModelAndView("xy/article/ColumnPreview", model);
	}

	/**
	 * 栏目详情
	 */
	@RequestMapping(value = "columnDetails.do")
	public ModelAndView columnDetails(HttpServletRequest request, HttpServletResponse response) throws Exception {
		long colID = WebUtil.getLong(request, "colID", 0);
		int colLibID = LibHelper.getLibID(DocTypes.COLUMN.typeID(), InfoHelper.getTenantCode(request));

		Map<String, String> model = colReader.getDetails(colLibID, colID);

		return new ModelAndView("/xy/article/ColumnDetails", model);
	}

	private Document prepublish(Document newArticle, Document oldArticle, HttpServletRequest request)
			throws E5Exception {

		newArticle.set("a_editor", oldArticle.getString("a_editor"));
		newArticle.set("a_siteID", oldArticle.getInt("a_siteID"));
		newArticle.set("SYS_FOLDERID", oldArticle.getInt("SYS_FOLDERID"));
		newArticle.set("a_type", Article.TYPE_LINK);
		newArticle.set("a_linktitle", oldArticle.getString("a_linktitle"));
		newArticle.set("SYS_TOPIC", oldArticle.getString("SYS_TOPIC"));
		if (oldArticle.getInt("a_channel") == 1) { // web发布库
			newArticle.set("a_url", oldArticle.getString("a_url"));
			newArticle.set("a_urlPAD", oldArticle.getString("a_url"));
		} else { // app发布库
			newArticle.set("a_url", oldArticle.getString("a_urlPAD"));
			newArticle.set("a_urlPAD", oldArticle.getString("a_urlPAD"));
		}
		newArticle.set("a_channel", oldArticle.getString("a_channel"));
		newArticle.set("a_subTitle", oldArticle.getString("a_subTitle"));
		newArticle.set("a_leadTitle", oldArticle.getString("a_leadTitle"));
		newArticle.set("a_tag", oldArticle.getString("a_tag"));
		newArticle.set("a_collaborator", oldArticle.getString("a_collaborator"));
		newArticle.set("a_abstract", oldArticle.getString("a_abstract"));
		newArticle.set("a_source", oldArticle.getString("a_source"));
		newArticle.set("a_sourceID", oldArticle.getInt("a_sourceID"));

		newArticle.set("a_picSmall", oldArticle.getString("a_picSmall"));
		newArticle.set("a_picSmall", oldArticle.getString("a_picSmall"));
		newArticle.set("a_picMiddle", oldArticle.getString("a_picMiddle"));
		newArticle.set("a_picBig", oldArticle.getString("a_picBig"));
		newArticle.set("a_attr", oldArticle.getString("a_attr"));
		newArticle.set("SYS_AUTHORS", oldArticle.getString("SYS_AUTHORS"));
		newArticle.set("SYS_AUTHORID", oldArticle.getLong("SYS_AUTHORID"));
		com.founder.e5.web.SysUser user = ProcHelper.getUser(request);
		Timestamp now = DateUtils.getTimestamp();
		newArticle.setLastmodified(now);
		newArticle.setLocked(false);
		newArticle.setCurrentUserName(user.getUserName());
		newArticle.setCurrentUserID(user.getUserID());
		// 按主栏目设置流程。
		articleManager.setFlowByColumn(newArticle);
		newArticle.set("a_status", Article.STATUS_PUB_NOT);
		articleManager.setColumnAll(newArticle);
		// 设置发布时间
		newArticle.set("a_pubTime", now);
		newArticle.setCreated(now);
		// 设置顺序
		double order = articleManager.getNewOrder(newArticle);
		newArticle.set("a_order", order);

		return newArticle;
	}

	/**
	 * 稿件属性
	 */
	@RequestMapping("articleAttr.do")
	public ModelAndView articleAttr(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		long colID = WebUtil.getLong(request, "colID", 0);
		String DocIDs = WebUtil.get(request, "DocIDs");

		mav.addObject("siteID", WebUtil.get(request, "siteID"));
		mav.addObject("UUID", WebUtil.get(request, "UUID"));
		mav.addObject("docLibID", docLibID);
		mav.addObject("ch", WebUtil.getInt(request, "ch", 0));
		mav.addObject("colID", colID);
		mav.addObject("DocIDs", DocIDs);

		mav.setViewName("/xy/article/ArticleAttr");
		return mav;
	}

	/**
	 * 稿件属性
	 */
	@RequestMapping("dealArticleAttr.do")
	public String dealArticleAttr(HttpServletRequest request, HttpServletResponse response, @RequestParam String DocIDs)
			throws Exception {
		int docLibID = WebUtil.getInt(request, "docLibID", 0); // 稿件库ID
		long colID = WebUtil.getInt(request, "colID", 0); // 稿件所属栏目ID
		int a_attr = WebUtil.getInt(request, "a_attr", 0); // 稿件属性
		long[] docIDs = StringUtils.getLongArray(DocIDs);

		// 更新稿件属性
		String relTable = InfoHelper.getRelTable(docLibID);
		StringBuffer sql = new StringBuffer();
		List<Object> params = new ArrayList<>();
		sql.append("UPDATE ").append(relTable).append(" set a_attr=? where CLASS_1=? and SYS_DOCUMENTID in (");
		params.add(a_attr);
		params.add(colID);
		for (int i = 0; i < docIDs.length; i++) {
			sql.append("?,");
			params.add(docIDs[i]);
		}
		sql.deleteCharAt(sql.length() - 1).append(")");
		InfoHelper.executeUpdate(docLibID, sql.toString(), params.toArray());
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] articles = docManager.get(docLibID, docIDs);
		for (Document article : articles) {
			if (article.getInt("a_status") == Article.STATUS_PUB_DONE) {
				article.set("a_status", Article.STATUS_PUB_ING);
				PublishTrigger.article(article);
				break;
			}
		}
		int colLibID = LibHelper.getLibIDByOtherLib(DocTypes.COLUMN.typeID(), docLibID);
		String column = "栏目:" + this.getColumnName(colLibID, colID);
		String url = "/e5workspace/after.do?UUID=" + WebUtil.get(request, "UUID") + "&DocIDs=" + DocIDs + "&Opinion="
				+ URLEncoder.encode(column, "UTF-8");
		return "redirect:" + url;
	}

	/**
	 * 修改稿件的宿主栏目
	 */
	@RequestMapping("modifyMainColumn.do")
	public String mainColumn(HttpServletRequest request, HttpServletResponse response) throws Exception {

		int docLibID = WebUtil.getInt(request, "DocLibID", 0); // 稿件库ID
		long DocID = WebUtil.getInt(request, "DocIDs", 0); // 稿件ID
		long colID = WebUtil.getInt(request, "colID", 0); // 稿件所属栏目ID
		// 获取稿件
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = docManager.get(docLibID, DocID);

		// 获取稿件的栏目信息
		long columnID = doc.getLong("a_columnid");
		int colLibID = LibHelper.getColumnLibID(request);
		String columnRelID = doc.getString("a_columnrelid");
		String colunmRel = doc.getString("a_columnrel");
		if (colID != columnID) {
			// 替换稿件的主栏目ID
			doc.set("a_columnid", colID);
			// 获取稿件的现栏目名和愿栏目名
			String columnNameNew = getColumnName(colLibID, colID);
			String columnNameOld = getColumnName(colLibID, columnID);
			// 替换稿件的主栏目名
			String columnCascadeName = getColumnCascadeName(colLibID, colID);
			doc.set("a_column", columnCascadeName);
			if (columnRelID.indexOf(String.valueOf(colID)) > -1) {
				// 替换稿件的关联稿件ID
				columnRelID = columnRelID.replace(String.valueOf(colID), String.valueOf(columnID));
				doc.set("a_columnrelid", columnRelID);
				// 替换稿件的关联栏目名
				colunmRel = colunmRel.replace(columnNameNew, columnNameOld);
				doc.set("a_columnrel", colunmRel);
			}
			// 保存稿件
			docManager.save(doc);

			int channel = doc.getInt("a_channel");
			//改宿主，更改稿件主栏目后，清空一下稿件的缓存；在端上能够显示成正确的栏目
            if(channel==2){//app稿件
                articleManager.clearAppKey(doc);
            }

			// 记录日志
			// 调用after.do进行后处理：改变流程状态、解锁、刷新列表、写日志
			String url = "/e5workspace/after.do?UUID=" + WebUtil.get(request, "UUID") + "&DocIDs=" + DocID + "&Opinion="
					+ URLEncoder.encode("更改宿主栏目为：" + columnNameNew + "(id=" + colID + ")", "UTF-8");
			return "redirect:" + url;
		} else {
			InfoHelper.outputText("已是本栏目稿件，无需修改！", response);
			return null;
		}
	}
}
