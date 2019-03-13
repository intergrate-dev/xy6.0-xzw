package com.founder.xy.article;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlReturnUpdateCount;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.DomHelper;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.CacheReader;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.doc.FlowRecord;
import com.founder.e5.doc.FlowRecordManager;
import com.founder.e5.doc.FlowRecordManagerFactory;
import com.founder.e5.doc.util.E5docHelper;
import com.founder.e5.dom.DocLib;
import com.founder.e5.flow.FlowNode;
import com.founder.e5.flow.FlowReader;
import com.founder.e5.flow.Operation;
import com.founder.e5.flow.ProcFlow;
import com.founder.e5.flow.ProcReader;
import com.founder.e5.flow.ProcUnflow;
import com.founder.e5.permission.Permission;
import com.founder.e5.permission.PermissionManager;
import com.founder.e5.sys.org.Role;
import com.founder.e5.web.SysUser;
import com.founder.e5.web.WebUtil;
import com.founder.e5.workspace.ProcHelper;
import com.founder.e5.workspace.app.form.LogHelper;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.config.Channel;
import com.founder.xy.config.ConfigReader;
import com.founder.xy.jms.PublishTrigger;
import com.founder.xy.jms.data.ArticleMsg;
import com.founder.xy.system.site.SiteUserCache;
import com.founder.xy.system.site.SiteUserManager;

/**
 * 原稿库 管理器
 * 
 * @author JiangYu
 */
@Component
public class OriginalManager {

	@Autowired
	private SiteUserManager userManager;

	@Autowired
	private ArticleManager articleManager;
	
	@Autowired
	private PermissionManager permissionManager;
	
	/**
	 * 判断下一个流程  是否是最后一个流程
	 */
	public boolean checkFlowNode(Document doc) throws E5Exception {
		int flowID = doc.getInt("SYS_CURRENTFLOW");
		int flowNodeID = doc.getInt("SYS_CURRENTNODE");
		// 一个流程的所有的流程节点
		FlowReader flowReader = (FlowReader) Context.getBean(FlowReader.class);
		FlowNode[] nodes = flowReader.getFlowNodes(flowID);
		FlowNode curNode = getCurrentNode(nodes, flowNodeID);
		FlowNode nextNode = getNextNode(nodes, curNode.getID());
		if(nextNode.getName() == nodes[nodes.length-1].getName()){
			return true;
		}
		return false;
	}

	// 审批状态时，取下一个审批节点
	private FlowNode getNextNode(FlowNode[] nodes, int curNodeID) {
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i].getID() == curNodeID && i < nodes.length - 1)
				return nodes[i + 1];
		}
		return nodes[0];
	}

	// 审批状态时，取当前审批节点
	private FlowNode getCurrentNode(FlowNode[] nodes, int curNodeID) {
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i].getID() == curNodeID)
				return nodes[i];
		}
		return nodes[0]; // 新建稿被初始化为无流程的节点1，若不符合当前流程，改为当前流程的节点1
	}

	/**
	 * 获取一个源稿库稿件  可以送审的审核阶段
	 */
	public Set<String> throughFlowNodes(Document doc) throws E5Exception {
		Set<String> flowInfos = new HashSet<String>();
		int lastFlowNodeID = doc.getInt("a_lastFlowNode");
		if(lastFlowNodeID <= 0){
			return flowInfos;
		}
		int flowID = doc.getInt("SYS_CURRENTFLOW");
		int flowNodeID = doc.getInt("SYS_CURRENTNODE");
		// 一个流程的所有的流程节点
		FlowReader flowReader = (FlowReader) Context.getBean(FlowReader.class);
		FlowNode[] nodes = flowReader.getFlowNodes(flowID);
		FlowNode curNode = getCurrentNode(nodes, flowNodeID);
		while(true){
			FlowNode nextNode = getNextNode(nodes, curNode.getID());
			flowInfos.add(nextNode.getID() + ":" + nextNode.getName());
			if(nextNode.getID() == lastFlowNodeID) break;
			curNode = nextNode;
		}
		return flowInfos;
	}
	
	/**
	 * 多个稿件的统一提交， 出错时返回错误信息
	 */
	public String save(int docLibID, List<Document> articles) {
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

	/**
	 * 送审时 获得并筛选日志列表
	 */
	public List<FlowRecord> initLogRecord(Long DocIDs, int docLibId, int type)
			throws E5Exception {
		FlowRecordManager manager = FlowRecordManagerFactory.getInstance();
		FlowRecord[] orignalRecord = manager.getAssociatedFRs(docLibId, DocIDs, true);
		List<FlowRecord> logList = new ArrayList<FlowRecord>();
		if (orignalRecord != null) {
			for (int i = 0; i < orignalRecord.length; i++) {
				FlowRecord flowRecord = orignalRecord[i];
				if(type == 0){
					int nodeID = flowRecord.getLastFlowNode();
					if (nodeID > 0) {
						logList.add(flowRecord);
					}
				}else if(type == 1){
					logList.add(flowRecord);
				}
				
			}
		}
		return logList;
	}
	/**
	 * 送审时 获得并筛选日志列表
	 */
	public List<FlowRecord> getLogWxGroupArt(Long DocIDs,int wxGroupId){
        DBSession db = null;
        IResultSet rs = null;
        List<FlowRecord> logList = new ArrayList<FlowRecord>();
	    String sql = "select FLOWRECORDID,DOCUMENTID,DOCLIBID,OPERATORID,OPERATOR,OPERATION,STARTTIME," +
                "ENDTIME,FROMPOSITION,TOPOSITION,LASTFLOWNODE,CURFLOWNODE,DETAIL" +
                " from dom_flowrecords where documentid =" +DocIDs+ " AND detail = "+wxGroupId;
//        String limitString = db.getDialect().getLimitString(sql, 0, 1000);
        try {
            db = Context.getDBSession();
            rs = db.executeQuery(sql, null);
            while (rs.next()) {
                FlowRecord orignalRecord = new FlowRecord();
                orignalRecord.setOperator(rs.getString("OPERATOR"));
                orignalRecord.setStartTime(rs.getTimestamp("STARTTIME"));
                orignalRecord.setOperation(rs.getString("OPERATION"));
                orignalRecord.setDetail("");
                orignalRecord.setFromPosition("");
//                orignalRecord.setLastFlowNode();
//                orignalRecord.setCurrentFlowNode();
//                orignalRecord.setEndTime();
//                orignalRecord.setOperatorID();
//                orignalRecord.setToPosition();
//                orignalRecord.setDocLibID();
//                orignalRecord.set
                logList.add(orignalRecord);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(rs);
            ResourceMgr.closeQuietly(db);
        }
        return logList;
	}
    /**
     * 签发源稿向选中的渠道
     * 注意:request中的DocIDs字段属性通过request.setAttribute方法设置的
     * @param form
     * @param request
     * @param isHavePub
     * @return
     * @throws Exception
     */

	public String signChannelArticles(JSONObject form,HttpServletRequest request, boolean isHavePub) throws Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		String tenantCode = InfoHelper.getTenantCode(request);
//		int docID = WebUtil.getInt(request, "DocIDs", 0);
        int docID =(int) request.getAttribute("DocIDs");
        int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		Document originaldoc = docManager.get(docLibID, docID);

		SysUser sysUser = ProcHelper.getUser(request);
		//int roleID = sysUser.getRoleID();
		
		if(Original.TYPE_WEIXIN == originaldoc.getInt("a_type")){
			//签发微信稿件
			signChannelWeixins(form, docManager, originaldoc, tenantCode, sysUser);
			return null;
		}
		
		List<Document> chArticles = new ArrayList<Document>();
		// 取出租户下的发布库。渠道代号是从0开始，正好与发布库一一对应
		List<DocLib> articleLibs = LibHelper.getLibs(DocTypes.ARTICLE.typeID(),
				tenantCode);
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
					if (chs[i] != null && ((int) Math.pow(2, i) & originaldoc.getInt("a_channel")) == (int) Math.pow(2, i)) {
						Document article = docManager.get(articleLibs.get(i).getDocLibID(), docID);
						int orichannel = originaldoc.getInt("a_channel") - (id + 1);
						//撤稿
				    	if(article.getInt("a_status") == Article.STATUS_PUB_DONE){
				    		ArticleMsg articleMsg = PublishTrigger.getArticleMsg(article);
					    	PublishTrigger.articleDelete(articleMsg);
				    	}
				    	//删除发布库中的稿件
					    docManager.delete(article);
					    originaldoc.set("a_channel", orichannel);
					}
					Document chArticle = docManager.newDocument(originaldoc,
							articleLibs.get(i).getDocLibID(), docID);
					chArticle.setFolderID(0); // 清空文件夹ID，以便initDoc方法赋值
					ProcHelper.initDoc(chArticle);
					chArticle.set("a_columnID", colID);
					chArticle.set("a_column", getString(form, id + "_column"));
					chArticle.set("a_originalID", docID);// 原稿ID

					// 设置关联栏目
					String relColID = getString(form, id + "_columnRelID");
					if (relColID != null) {
						chArticle.set("a_columnRelID", relColID);
						chArticle.set("a_columnRel",
								getString(form, id + "_columnRel"));
					}
					chArticle.set("a_pubTime", DateUtils.getTimestamp()); // 发布时间
					chArticle.set("a_linkTitle", originaldoc.getTopic());

					// 设置顺序
					double order = articleManager.getNewOrder(chArticle);
					chArticle.set("a_order", order);

					// 所有栏目设置
					articleManager.setColumnAll(chArticle);
					tryPublish(chArticle, colID, docManager, isHavePub);
					int channel = originaldoc.getInt("a_channel");
					if (channel < 0)
						channel = 0;
					channel = channel | (int) Math.pow(2, id);
					originaldoc.set("a_channel", channel);

					if (originalLog.length() != 0) {
						originalLog.append("；");
					}

					originalLog.append("签发到").append(chs[i].getCode())
							.append("发布库");

					// 给发布库稿件设置a_channel,a_channel值等于2的id次方
					chArticle.set("a_channel", (int) Math.pow(2, id));
					if (chArticle.getInt("SYS_AUTHORID") > 0) {
						Document user = userManager.getUser(
								LibHelper.getUserExtLibID(),
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
				LogHelper.writeLog(article.getDocLibID(), article.getDocID(),
						sysUser, "签发（原稿）", "");
			}
			// 写原稿库日志
			LogHelper.writeLog(docLibID, docID, sysUser, "签发",
					originalLog.toString());

			// 触发发布
			for (Document article : chArticles) {
				if(article.getInt("A_ORIGINALID") < 0) continue;
				if (Article.STATUS_PUB_ING == article.getInt("a_status")) {
					PublishTrigger.article(article);
				}
			}

			return null;
		}
		return error;
	}

	private void tryPublish(Document doc,int colID, DocumentManager docManager,
			boolean isHavePub) throws E5Exception {
		//获取主栏目的对应流程ID
		int colLibID = LibHelper.getLibIDByOtherLib(DocTypes.COLUMN.typeID(), doc.getDocLibID());
		Document column = docManager.get(colLibID, colID);
		//int flowID = articleManager.getFlowID(colLibID, colID);
		int flowID = column.getInt("col_flow_ID");
		//一个流程的所有的流程节点
		FlowReader flowReader = (FlowReader)Context.getBean(FlowReader.class);
		FlowNode[] nodes = flowReader.getFlowNodes(flowID);
		if(isHavePub){
			FlowNode curNode = getCurrentNode(nodes, doc.getCurrentNode());
			if (1 == flowID){//无审批流程
				FlowNode nextNode = getNextNode(nodes, curNode.getID());
				initFlowDoc(doc, flowID, nextNode);
				doc.set("a_status", Article.STATUS_PUB_ING);
			}else{
				if(nodes[0].getID() == curNode.getID())
					curNode = getNextNode(nodes, curNode.getID());
				if (curNode != null) {
					initFlowDoc(doc, flowID, curNode);
					doc.set("a_status", Article.STATUS_AUDITING);
				}
			}
		} else {
			initFlowDoc(doc, flowID, nodes[0]);
			doc.set("a_status", Article.STATUS_PUB_NOT);
		}
	}

	//初始化稿件箱的流程节点
	private void initFlowDoc(Document doc,int flowID,FlowNode node) throws E5Exception {
		doc.setCurrentFlow(flowID);
		doc.setCurrentNode(node.getID());
		doc.setCurrentStatus(node.getWaitingStatus());
	}
	
	private void signChannelWeixins(JSONObject form, DocumentManager docManager,
			Document originaldoc, String tenantCode, SysUser sysUser) throws E5Exception {
		boolean checked = "on".equals(getString(form, "channel_weixin"));
		if (checked) {
			String weixinID = form.getString("weixinID");
			if(StringUtils.isBlank(weixinID)) return;
			List<Document> wxarticles = new ArrayList<Document>();
			long beginNewID = 0;
			int wxLibID = LibHelper.getLibID(DocTypes.WXARTICLE.typeID(), tenantCode);
			String[] accounts = weixinID.split(",");
			for (int i = 0; i < accounts.length; i++) {
				long wxarticleID = InfoHelper.getNextDocID(DocTypes.WXARTICLE.typeID());
				if(i==0) beginNewID = wxarticleID;
	            Document wxarticle = docManager.newDocument(wxLibID, wxarticleID);
	            wxarticle.setAuthors(originaldoc.getAuthors());
	            wxarticle.setTopic(originaldoc.getTopic());
	            wxarticle.set("wx_menuID", 0);
	            wxarticle.set("wx_accountID", accounts[i]);
	            wxarticle.setCurrentFlow(0);
				wxarticle.setCurrentNode(0);
				wxarticle.setCurrentStatus("已发布");
				wxarticle.setCurrentUserID(sysUser.getUserID());
				wxarticle.setCurrentUserName(sysUser.getUserName());
				wxarticle.setFolderID(DomHelper.getFVIDByDocLibID(wxLibID));
	            wxarticle.set("wx_url", originaldoc.getString("a_url"));
	            wxarticle.set("wx_abstract", originaldoc.getString("a_abstract"));
	            wxarticle.set("wx_pubTime", originaldoc.getLastmodified());
	            wxarticle.setLastmodified(originaldoc.getLastmodified());
	            wxarticle.set("wx_order", wxarticleID);
	            wxarticle.set("wx_subTitle", originaldoc.getString("a_subTitle"));
	            wxarticle.set("wx_articleID", originaldoc.getDocID());
	            wxarticle.set("wx_articleLibID", originaldoc.getDocLibID());
	            wxarticle.set("wx_pic", originaldoc.getString("a_picBig"));
	            wxarticles.add(wxarticle);
			}
			String error = save(wxLibID,wxarticles);
			
			if ( error == null ){
				removeSignedWeixin(originaldoc, weixinID, wxLibID, docManager, beginNewID);
				String weixinName = form.getString("weixinName");
				String[] accountNames = weixinName.split(",");
				for (String accountName : accountNames) {
		            LogHelper.writeLog(originaldoc.getDocLibID(), originaldoc.getDocID(), sysUser, "签发", "签发到微信公众号：" + accountName);
		        }
			}
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

	public void saveAttachments(Document article, Document srcArticle,
			DBSession conn) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] attas = docManager.find(LibHelper.getLibIDByOtherLib(
				DocTypes.ATTACHMENT.typeID(), srcArticle.getDocLibID()),
				"att_articleLibID=? and att_articleID=?", new Object[] {
						srcArticle.getDocLibID(), srcArticle.getDocID() });
		if (attas != null) {
			for (Document atta : attas) {
				long docID = InfoHelper.getNextDocID(DocTypes.ATTACHMENT
						.typeID());
				Document copyatta = docManager.newDocument(atta,
						atta.getDocLibID(), docID);
				copyatta.set("att_articleLibID", article.getDocLibID());
				copyatta.set("att_articleID", article.getDocID());

				copyatta.set("att_type", atta.getInt("att_type")); // 附件类型
				copyatta.set("att_objID", atta.getLong("att_objID")); // 对应的视频等ID
				copyatta.set("att_objLibID", atta.getInt("att_objLibID"));

				docManager.save(copyatta, conn);
			}
		}
	}

	public void saveWidgets(Document article, Document srcArticle,
			DBSession conn) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();

		Document[] widgets = docManager
				.find(LibHelper.getLibIDByOtherLib(DocTypes.WIDGET.typeID(),
						srcArticle.getDocLibID()),
						"w_articleLibID=? and w_articleID=?",
						new Object[] { srcArticle.getDocLibID(),
								srcArticle.getDocID() });
		if (widgets != null) {
			for (Document widget : widgets) {
				long docID = InfoHelper.getNextDocID(DocTypes.WIDGET.typeID());
				Document copywidget = docManager.newDocument(widget,
						widget.getDocLibID(), docID);
				copywidget.set("w_articleLibID", article.getDocLibID());
				copywidget.set("w_articleID", article.getDocID());

				docManager.save(copywidget, conn);
			}
		}
	}

	public void saveExtFields(Document article, Document srcArticle,
			DBSession conn) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();

		Document[] extFields = docManager.find(LibHelper.getLibIDByOtherLib(
				DocTypes.ARTICLEEXT.typeID(), srcArticle.getDocLibID()),
				"ext_articleLibID=? and ext_articleID=?", new Object[] {
						srcArticle.getDocLibID(), srcArticle.getDocID() });
		if (extFields != null) {
			for (Document extField : extFields) {
				long docID = InfoHelper.getNextDocID(DocTypes.ARTICLEEXT
						.typeID());
				Document copyExtField = docManager.newDocument(extField,
						extField.getDocLibID(), docID);
				copyExtField.set("ext_articleLibID", article.getDocLibID());
				copyExtField.set("ext_articleID", article.getDocID());

				docManager.save(copyExtField, conn);
			}
		}
	}

	public void saveLogs(Document article, Document srcArticle,
			SysUser sysUser) throws E5Exception {
		FlowRecordManager manager = FlowRecordManagerFactory.getInstance();
		FlowRecord[] orignalRecord = manager.getAssociatedFRs(srcArticle.getDocLibID(), 
				srcArticle.getDocID(), true);
		if(orignalRecord!=null){
			for (FlowRecord flowRecord : orignalRecord) {
				LogHelper.writeLog(article.getDocLibID(), article.getDocID(), 
						sysUser, flowRecord.getOperation(), flowRecord.getDetail());
			}
		}
		
	}
	
	public List<Channel> getChannels(int channel) {
		Channel[] allChs = ConfigReader.getChannels();

		List<Channel> chs = new ArrayList<Channel>();
		for (int i = 0; i < allChs.length; i++) {
			if (allChs[i] != null && ((int) Math.pow(2, i) & channel) == 0) {
				chs.add(allChs[i]);
			}
		}
		return chs;
	}

	// "预览"操作的参数组织
	public void getPreviewParam(HttpServletRequest request, Model model,
			int DocLibID, long DocIDs, Channel[] chs, Document doc) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();

		String status = "success";
		JSONObject result = new JSONObject();

		if (doc != null) {
			getPreviewParamUrl(request, DocLibID, DocIDs, chs, doc, result, docManager);
		}

		model.addAttribute("result", result);
		model.addAttribute("status", status);
	}

	// 不同稿件类型的预览链接不同
	private void getPreviewParamUrl(HttpServletRequest request, int DocLibID,
			long DocIDs, Channel[] chs, Document doc, JSONObject result, DocumentManager docManager)
			throws E5Exception {
		int _status = doc.getInt("a_status");
		int _type = doc.getInt("a_type");
		result.put("status", _status);
		result.put("atype", _type);
		//http://127.0.0.1:8080/xy/article/Preview.do?DocLibID=1&DocIDs=185710&FVID=1&UUID=1531210614247&siteID=100&colID=3114&ch=0
		int _colID = WebUtil.getInt(request, "refCol", 0); // 原稿预览时会指定参考栏目
		for (int i = 0; i < chs.length; i++) {
			boolean isHasColumn = false;
			String param = "&DocLibID=" + (chs[i].getId() == 0 ? LibHelper.getArticleLibID():LibHelper.getArticleAppLibID()) + "&DocIDs=" + DocIDs;
			if (_colID > 0) {
				param += "&refCol=" + _colID;
				isHasColumn = true;
			} else {// 源稿库预览
				// 状态是待审核   根据预签栏目展示
				if (_status == Original.STATUS_AUDITING) {
					String pubsign = doc.getString("a_pubsign");
					JSONObject params = JSONObject.fromObject(pubsign);
					if (params.containsKey(chs[i].getId() + "_columnID")) {
						String colID = params.getString(chs[i].getId() + "_columnID");
						if (!org.apache.commons.lang.StringUtils.isBlank(colID) && Integer.parseInt(colID) != 0) {
							param += "&refCol=" + colID;
							isHasColumn = true;
						}
					}
				// 状态是已签发 取已发布栏目信息
				} else if (_status == Original.STATUS_PUBDONE) {
					List<DocLib> articleLibs = LibHelper.getLibs(DocTypes.ARTICLE.typeID(), InfoHelper.getTenantCode(request));
					if (chs[i] != null && ((int) Math.pow(2, i) & doc.getInt("a_channel")) == (int) Math.pow(2, i)) {
						Document chArticle = docManager.get(articleLibs.get(i).getDocLibID(), DocIDs);
						Long _columnID = chArticle.getLong("a_columnID");
						param += "&refCol=" + _columnID;
						isHasColumn = true;
					}
				}
			}
			if (chs[i].getId() == 0 && isHasColumn) {
				result.put("webUrl", "../../xy/article/Preview.do?ch=0" + param);
			} else if (chs[i].getId() == 1 && isHasColumn) {
				result.put("appUrl", "../../xy/article/Preview.do?ch=1" + param);
			}
			
		}

	}

	/**
	 * 预签时 检查被选取的栏目ID
	 */
	public String checkPubsign(JSONObject params, Document doc) {
		StringBuilder sb = new StringBuilder();
		if(params.containsKey("channel_weixin")){
			boolean checked = "on".equals(getString(params, "channel_weixin"));
			if (!checked) return sb.toString();
			return params.getString("weixinID");
		}
		Channel[] chs = ConfigReader.getChannels();
		for (int i = 0; i < chs.length; i++) {
			boolean checked = "on".equals(getString(params,
					"channel" + chs[i].getId()));
			if (!checked)
				continue;
			if (params.containsKey(chs[i].getId() + "_columnID")) {
				String colID = params.getString(chs[i].getId() + "_columnID");
				if("0".equals(colID)) continue;
				if (org.apache.commons.lang.StringUtils.isNotBlank(colID)) {
					sb.append(colID);
				}
			}
		}
		return sb.toString();
	}

	public List<SignedChInfo> getSignedChs(int channel, long DocID,
			String tenantCode, String pubsign) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Channel[] allChs = ConfigReader.getChannels();
		List<DocLib> articleLibs = LibHelper.getLibs(DocTypes.ARTICLE.typeID(),
				tenantCode);
		boolean isHavePub = true;
		if(StringUtils.isBlank(pubsign)) isHavePub = false;
		
		List<SignedChInfo> chs = new ArrayList<SignedChInfo>();
		for (int i = 0; i < allChs.length; i++) {
			if (allChs[i] != null
					&& ((int) Math.pow(2, i) & channel) == (int) Math.pow(2, i)) {
				Document chArticle = docManager.get(articleLibs.get(i)
						.getDocLibID(), DocID);
				SignedChInfo c = new SignedChInfo(allChs[i].getId(),
						allChs[i].getCode(), allChs[i].getName(),
						chArticle.getString("a_column"),
						chArticle.getString("a_columnRel"));
				c.setColID(chArticle.getLong("a_columnID"));
				c.setColRelID(chArticle.getString("a_columnRelID"));
				chs.add(c);
			}else{
				SignedChInfo c = new SignedChInfo(allChs[i].getId(),
						allChs[i].getCode(), allChs[i].getName());
				if(isHavePub){
					recordPubSign(allChs[i], c, pubsign);
				} 
				chs.add(c);
			}
		}
		return chs;
	}
	
	private void recordPubSign(Channel channel, SignedChInfo c, String pubsign) {
		JSONObject params = JSONObject.fromObject(pubsign);
		if (channel != null && params.containsKey(channel.getId() + "_columnID")) {
			String colID = params.getString(channel.getId() + "_columnID");
			String col = params.getString(channel.getId() + "_column");
			String colRelID = params.getString(channel.getId() + "_columnRelID");
			String colRel = params.getString(channel.getId() + "_columnRel");
			c.setColPreID(Long.valueOf(colID));
			c.setColPreName(col);
			c.setColPreRelID(colRelID);
			c.setColPreRelName(colRel);
		} else if (params.containsKey("weixinID")){
			String weixinID = params.getString("weixinID");
			String weixinName = params.getString("weixinName");
			c.setColPreRelID(weixinID);
			c.setColPreRelName(weixinName);
		}
	}

	/**
	 * 查询某篇稿件 已签发的微信公众号信息
	 */
	public SignedChInfo getSignedWeixin(Document original) throws E5Exception {
		String sql = "select a.wx_accountID as weixinID, (select b.wxa_name from xy_wxaccount b where b.SYS_DOCUMENTID = a.wx_accountID) as weixinName "
			+ "from xy_wxarticle a where a.SYS_DELETEFLAG=0 and a.wx_menuID=0 "
			+ "and a.wx_articleID = ? and a.wx_articleLibID = ?";
		SignedChInfo c = new SignedChInfo(Original.TYPE_WEIXIN, null, null);
		DBSession conn = null;
	    IResultSet rs = null;
	    StringBuilder sbID = new StringBuilder();
	    StringBuilder sbName = new StringBuilder();
	    try {
	        conn = Context.getDBSession();
            rs = conn.executeQuery(sql, new Object[]{ original.getDocID(), original.getDocLibID() });
	        while (rs.next()) {
	        	sbID.append(rs.getLong("weixinID"));
	        	sbName.append(rs.getString("weixinName"));
	        	if(!rs.isLast()){
	        		sbID.append(",");
		        	sbName.append(",");
	        	}
	        }
	        c.setColRelID(sbID.toString());
	        c.setColRelName(sbName.toString());
	        if(c.getColRelID().length() <= 0){
	        	boolean isHavePub = true;
	        	String pubsign = original.getString("a_pubsign");
	    		if(StringUtils.isBlank(pubsign)) isHavePub = false;
	    		if(isHavePub) recordPubSign(null, c, pubsign);
	        }
	        
	    } catch (Exception e) {
	        throw new E5Exception(e);
	    } finally {
	        ResourceMgr.closeQuietly(rs);
	        ResourceMgr.closeQuietly(conn);
	    }
		return c;
	}

	/**
	 * 查询微信公众号的某一素材稿件
	 */
	public Document getWXdoc(DocumentManager docManager, int wxLibID, 
			long strDocID, int sourceLibID, long AccountID) throws E5Exception {
		Document[] cols = docManager
				.find(wxLibID, "SYS_DELETEFLAG=0 and wx_menuID=0 and wx_accountID = ? "
						+ "and wx_articleID = ? and wx_articleLibID = ?",
						new Object[] { AccountID, strDocID, sourceLibID});
		return cols[0];
	}
	
	/**
	 * 如果稿件已签发当前微信公众号  从公众号中删除
	 */
	public void removeSignedWeixin(Document original, String accounts, 
			int wxLibID, DocumentManager docManager, long beginNewID) throws E5Exception {
		Document[] cols = docManager
				.find(wxLibID, "SYS_DELETEFLAG=0 and wx_menuID=0 and wx_accountID in (" + accounts + ") "
						+ "and wx_articleID = ? and wx_articleLibID = ? and SYS_DOCUMENTID < ?",
						new Object[] { original.getDocID(), original.getDocLibID(), beginNewID });
		for (Document document : cols) {
			docManager.delete(document);
		}
	}
	
	/**
	 * 设置流程  驳回设置为第一个流程  其他设置为下一个流程
	 * type： 1审核通过  2驳回
	 * flownode：预设流程节点ID
	 */
	public void setNextFlow(Document document, int type, int flownode) {
		try {
			FlowReader flowReader = (FlowReader) Context.getBean(FlowReader.class);
			FlowNode nextNode = null;
			if(flownode > 0){
				//nextNode = flowReader.getNextFlowNode(flownode);
				nextNode = flowReader.getFlowNode(flownode);
			}else{
				FlowNode currNode = flowReader.getFlowNode(document.getCurrentNode());
				FlowNode[] nodes = flowReader.getFlowNodes(currNode.getFlowID());
				nextNode = type == 2 ? nodes[0] : flowReader.getNextFlowNode(currNode.getID());
			}
			document.setCurrentFlow(nextNode.getFlowID());
			document.setCurrentNode(nextNode.getID());
			document.setCurrentStatus(nextNode.getWaitingStatus());
			document.setLastmodified(new Timestamp((new Date()).getTime()));
		} catch (E5Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取历史记录分页列表
	 */
	public String getHistoryInfo(int page, int count, long docID, DocLib hisLib,int type,int wxGroupId) {
		JSONArray array = new JSONArray();
		JSONObject rtnJson = new JSONObject();

		DBSession conn = null;
		IResultSet rs = null;
		DBSession countdb = null;
        IResultSet countrs = null;
        IResultSet countrs1 = null;
		try {
			conn = Context.getDBSession(hisLib.getDsID());
			String sql = "SELECT * FROM xy_historyori WHERE a_originalID = " + docID +" AND wx_groupid IS Null" ;

            if(type==1&&wxGroupId!=0){
                sql += " union SELECT * FROM xy_historyori where wx_groupid ="+wxGroupId+" and a_originalID = " + docID  ;
            }
            sql+=" ORDER BY SYS_DOCUMENTID desc";


			sql = conn.getDialect().getLimitString(sql, (page - 1) * count, count);
			rs = conn.executeQuery(sql);
			while (rs.next()) {
				JSONObject json = new JSONObject();
				json.put("id", rs.getString("SYS_DOCUMENTID"));
				json.put("created", rs.getString("SYS_CREATED"));
				json.put("operation", rs.getString("a_operation"));
				json.put("operator", rs.getString("a_operator"));
				json.put("title", rs.getString("SYS_TOPIC"));
				json.put("status", rs.getString("SYS_CURRENTSTATUS"));
				json.put("abstract", rs.getString("a_abstract"));
				json.put("content", rs.getString("a_content"));
				json.put("url", rs.getString("a_url"));
				array.add(json);
			}
			int historyCount = 0;
			countdb = Context.getDBSession(hisLib.getDsID());
			countrs = countdb.executeQuery("SELECT COUNT(*) FROM xy_historyori WHERE a_originalID = " + docID+" AND wx_groupid IS Null");
			countrs1 = countdb.executeQuery("SELECT COUNT(*) FROM xy_historyori WHERE a_originalID = " + docID+" and wx_groupid ="+wxGroupId);
			if (countrs.next()) {
				historyCount = countrs.getInt(1);
            }
			if (countrs1.next()) {
				historyCount += countrs1.getInt(1);
            }
			int pagecount = historyCount/count;
			if(historyCount%count > 0) pagecount++;
			rtnJson.put("page", page);
			rtnJson.put("pagecount", pagecount);
			rtnJson.put("list", array);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
			ResourceMgr.closeQuietly(countrs);
			ResourceMgr.closeQuietly(countdb);
		}
		return rtnJson.toString();
	}
	
	/**
	 * 源稿库 添加当前流程下 有操作权限的流程节点
	 * @throws Exception 
	 */
	public void setFlowPower(long docID, int docLibID, Map<String, Object> model,
			int userID, boolean isNew, long catID, int siteID) {
		try {
			int userLibID = LibHelper.getUserExtLibID();
			Role[] roles = getRolesBySite(userLibID, userID, siteID);
			Set<String> list = new HashSet<>();
			for (Role role : roles) {
				String procStr = getProcs(role.getRoleID(), catID + "OriginalFlow" + siteID);
				String unprocStr = getProcs(role.getRoleID(), catID + "OriginalUnFlow" + siteID);
				if(procStr.length() > 0){
					String[] procids = procStr.split(",");
					for (String procid : procids) {
						list.add(procid);
					}
				}
				if(unprocStr.length() > 0){
					String[] procids = unprocStr.split(",");
					for (String procid : procids) {
						list.add(procid);
					}
				}
			}
			if(list.size() <= 0) return;
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			FlowReader flowReader = (FlowReader) Context.getBean(FlowReader.class);
			FlowNode currNode = null;
			if(isNew){
				int colLibID = LibHelper.getLibIDByOtherLib(DocTypes.COLUMNORI.typeID(), docLibID);
				Document cat = docManager.get(colLibID, catID);
				FlowNode[] nodes = flowReader.getFlowNodes(cat.getInt("col_flow_ID"));
				currNode = nodes[0];
				model.put("status", 0);
			}else{
				Document document = docManager.get(docLibID, docID);
				currNode = flowReader.getFlowNode(document.getCurrentNode());
				model.put("status", document.getInt("a_status"));
			}
			ProcReader procReader = (ProcReader)Context.getBean(ProcReader.class);
			ProcFlow[] procs = procReader.getProcs(currNode.getID());
			ProcUnflow[] unprocs = procReader.getUnflows(DocTypes.ORIGINAL.typeID());
			model.put("isCensorship", false);
			model.put("isPubCensorship", false);
			model.put("isCanSubmit", false);
			model.put("isCheckThrough", false);
			for (ProcFlow proc : procs) {
				Operation op = procReader.getOperation(proc.getOpID());
				if(op.getName().equals("送审")) {
					if(list.contains(String.valueOf(proc.getProcID()))) {
						model.put("isCensorship", true);
					}
				} else if(op.getName().equals("预签并送审")) {
					//String operation = proc.getProcName();
					if(list.contains(String.valueOf(proc.getProcID()))) {
						model.put("isPubCensorship", true);
					}
				} else if(op.getName().equals("提交")) {
					if(list.contains(String.valueOf(proc.getProcID()))) {
						model.put("isCanSubmit", true);
					}
				}
			}
			for (ProcUnflow unproc : unprocs) {
				Operation op = procReader.getOperation(unproc.getOpID());
				if(op.getName().equals("校对完成")) {
					if(list.contains(String.valueOf(unproc.getProcID()))) {
						model.put("isCheckThrough", true);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Role[] getRolesBySite(int userLibID, int userID, int siteID) {
		SiteUserCache siteUserCache = (SiteUserCache)(CacheReader.find(SiteUserCache.class));
		int[] roleIDs =  siteUserCache.getRoles(userLibID, userID, siteID);
		if (roleIDs == null || roleIDs.length == 0) return null;

		Role[] roles = new Role[roleIDs.length];
		for (int i = 0; i < roles.length; i++) {
			roles[i] = new Role();
			roles[i].setRoleID(roleIDs[i]);
		}
		return roles;
	}
	
	public String getProcs(int roleID, String originalType) throws Exception {
		Permission[] permission = permissionManager.getPermissions(roleID, originalType);
		String resource = permission == null ? "" : permission[0].getResource();
		return resource;
	}
	
}
