package com.founder.xy.wx.web;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.founder.e5.doc.DocID;
import com.founder.e5.dom.DocLib;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.founder.e5.commons.DomHelper;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.doc.util.E5docHelper;
import com.founder.e5.sys.StorageDeviceManager;
import com.founder.e5.sys.SysFactory;
import com.founder.e5.web.SysUser;
import com.founder.e5.web.WebUtil;
import com.founder.e5.workspace.ProcHelper;
import com.founder.e5.workspace.app.form.LogHelper;
import com.founder.xy.article.Article;
import com.founder.xy.article.ArticleManager;
import com.founder.xy.article.Original;
import com.founder.xy.column.Column;
import com.founder.xy.column.ColumnReader;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.config.SecurityHelper;
import com.founder.xy.jms.PublishTrigger;
import com.founder.xy.wx.WeixinManager;
import com.founder.xy.wx.data.Account;
import com.founder.xy.wx.data.Menu;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
@RequestMapping("/xy/wx")
public class WeixinController {
	
	@Autowired
	private WeixinManager wxManager;
	
	@Autowired
	private ColumnReader colReader;

	/**
	 * 微信图文详情操作
	 */
	@RequestMapping("WXGroup.do")
	public String group(HttpServletRequest request, Map<String, Object> model){
		int groupLibID = WebUtil.getInt(request, "DocLibID", 0);
		long groupID = WebUtil.getLong(request, "DocIDs", 0); //组ID
		int type = WebUtil.getInt(request, "type", 0); //0：新建或修改  1：图文详情
		int status = WebUtil.getInt(request,"status",0);
		boolean isNew = groupID == 0;

		int accountID = WebUtil.getInt(request, "groupID", 0); //账号ID，系统统一把左边树上的ID以GroupID参数传入
		int accouentLibID = LibHelper.getLibID(DocTypes.WXACCOUNT.typeID(), InfoHelper.getTenantCode(request));
		Account account = wxManager.getAccount(accouentLibID, accountID);
		Document group = null;
		if (groupID == 0) {
			groupID = InfoHelper.getNextDocID(DocTypes.WXGROUP.typeID());
		} else {
			group = wxManager.getDocument(groupLibID, groupID);//获取图文组对象
		}

		int groupStatus = group==null?0:group.getInt("wxg_status");
		model.put("siteID", WebUtil.get(request, "siteID"));
		model.put("accountID", accountID);
		model.put("accountName", account.getName());
		model.put("groupID", groupID);
		model.put("docLibID", groupLibID);
		model.put("groupStatus", groupStatus);
		model.put("isNew", isNew);
		model.put("UUID", WebUtil.get(request, "UUID"));
		if(groupStatus <= 1){
			SysUser user = ProcHelper.getUser(request);
			wxManager.setFlowPower(group, groupLibID, model,
					user.getUserID(), WebUtil.getInt(request, "siteID", 1));
		}

		model.put("useMugeda", SecurityHelper.mugedaUsable());
		model.put("status", status);
		if(type==0) return "xy/wx/WXGroup";
		model.put("hasSensitive", InfoHelper.sensitiveInArticle());
        model.put("hasIllegal", InfoHelper.illegalInArticle());
		return "xy/wx/WXGroupView";
	}

	/**
	 * 微信图文的提交保存
	 */
	@RequestMapping("WXGroupSave.do")
	public void groupSave(HttpServletRequest request, HttpServletResponse response) {
		int accountID = WebUtil.getInt(request, "accountID", 0);
		int groupLibID = WebUtil.getInt(request, "docLibID", 0);
		long groupID = WebUtil.getLong(request, "groupID", 0);
		String data = WebUtil.get(request, "data");
		boolean isNew = WebUtil.getBoolParam(request, "isNew");
		//boolean isCensorship = WebUtil.getBoolParam(request, "isCensorship");//是否带有送审

		Document[] oldDocs = getArticles(groupLibID, groupID);

		//前端传入的稿件列表
		JSONArray list = JSONArray.fromObject(data);
		//组织稿件列表
		List<Document> docs = prepareDocs(groupLibID, groupID, list);

		//遍历需要保存的doc文件组
        //需要保存到wx_article表中从groupwx中取出id然后保存
        for (Document docwxG :docs) {
            Long wx_articleID = Long.parseLong(docwxG.get("wx_articleID").toString());
            try {
            DocumentManager docManager1 = DocumentManagerFactory.getInstance();
                String sys_doclibid = docwxG.getString("sys_doclibid");
                DocLib[] libs1 = LibHelper.getLibs(DocTypes.WXARTICLE.typeID());
                int docLibID1 = libs1[0].getDocLibID();
            Document doc = docManager1.find(docLibID1, "wx_articleID=? order by SYS_DOCUMENTID",
                    new Object[]{wx_articleID})[0];

            JSONObject js = new JSONObject();
            //将公众号组的id放到list中
            List<Long> listGroupIds = new ArrayList<>();
            DocumentManager docManager = DocumentManagerFactory.getInstance();
            String belongto = doc.getString("wx_belongto");
            int docLibID = LibHelper.getLib(DocTypes.WXARTICLE.typeID()).getDocLibID();
            //如果稿件第一次加入到微信组
                if(belongto.isEmpty()){
                    listGroupIds.add(groupID);
                }else {
                    //如果稿件的groupId!=null,将belongto转换为list,将最新的不重复的微信组添加进去
                    JSONObject jsonBelongto = JSONObject.fromObject(belongto);
                    listGroupIds = (List<Long>) jsonBelongto.get("belongto");
                    //如果不重
                    if(!listGroupIds.contains(groupID)){
                        listGroupIds.add(groupID);
                    }
                }
            js.put("belongto",listGroupIds);
            doc.set("wx_belongto",js.toString());

                docManager.save(doc);
            } catch (E5Exception e) {
                e.printStackTrace();
            }
        }
		//组织图文组对象
		Document group = prepareGroup(accountID, groupLibID, groupID, isNew, 
				list, ProcHelper.getUser(request));
		
		//if(isCensorship){
		//	group.set("wxg_status", Original.STATUS_AUDITING);
		//	wxManager.setNextFlow(group, 0);
		//}
		
		//整体提交  成功以后删除旧稿件
		save(group, docs, oldDocs);
		
		//写日志
		LogHelper.writeLog(groupLibID, groupID, request, null);
		
		InfoHelper.outputText("ok", response);
	}

	/**
	 * 上传图文素材（上传至微信公众平台）
	 * @throws E5Exception 
	 */
	@RequestMapping(value="GroupMaterialUpload.do", produces="text/json;charset=UTF-8")
	public void groupMaterialUpload(HttpServletRequest request, HttpServletResponse response) 
			throws E5Exception{
		int groupLibID = WebUtil.getInt(request, "DocLibID", 0);
		long groupID = WebUtil.getInt(request, "DocIDs", 0);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document group = docManager.get(groupLibID, groupID);
		
		String data = group.getString("wxg_members");
		data = data.replaceAll("((\\\\t)|(\\\\n))*", "");
		
		String result = wxManager.groupMaterialUpload(group, data);
		
		//写日志
		if ("ok".equals(result))
			LogHelper.writeLog(groupLibID, groupID, ProcHelper.getUser(request), "发布", null);
		
		InfoHelper.outputText(result, response);
	}
	
	/**
	 * 删除图文素材（取消上传）
	 * @throws E5Exception 
	 */
	@RequestMapping(value="GroupMaterialDelete.do", produces="text/json;charset=UTF-8")
	public String groupMaterialDelete(HttpServletRequest request, HttpServletResponse response) throws Exception{

		String strDocIDs = WebUtil.get(request, "DocIDs");
		String url = "../../e5workspace/after.do?UUID=" + WebUtil.get(request, "UUID")
				+ "&DocIDs=" + strDocIDs;;
		int groupLibID = WebUtil.getInt(request, "DocLibID", 0);
		long groupID = WebUtil.getLong(request, "DocIDs", 0);
		
		String error = wxManager.groupMaterialDelete(groupLibID, groupID);
		url += "&Info=" + URLEncoder.encode(error, "UTF-8");
	    return "redirect:" + url;
	}
	
	/**
	 * 图文消息群发
	 */
	@RequestMapping(value="GroupPublish.do", produces="text/json;charset=UTF-8")
	public void groupPublish(HttpServletRequest request, HttpServletResponse response){
		int accountID = WebUtil.getInt(request, "accountID", 0);
		int groupLibID = WebUtil.getInt(request, "docLibID", 0);
		long groupID = WebUtil.getLong(request, "groupID", 0);
		
		String data = WebUtil.get(request, "data");
		data = data.replaceAll("((\\\\t)|(\\\\n))*", "");
		
		String result = wxManager.groupPublish(accountID, groupLibID, groupID, data);
		
		//写日志
		if ("ok".equals(result))
			LogHelper.writeLog(groupLibID, groupID, ProcHelper.getUser(request), "发布", null);
		
		InfoHelper.outputText(result, response);
	}

	/** 图文消息预览 */
	@RequestMapping(value="GroupPreview.do")
	public void groupPreview(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("wx_name") String wx_name) {
		
		int accountID = WebUtil.getInt(request, "accountID", 0);
		int groupLibID = WebUtil.getInt(request, "docLibID", 0);
		long groupID = WebUtil.getLong(request, "groupID", 0);
		
		String data = WebUtil.get(request, "data");
		data = data.replaceAll("((\\\\t)|(\\\\n))*", "");
		
		String result = wxManager.groupPreview(accountID, groupLibID, groupID, data, wx_name);
		
		InfoHelper.outputText(result, response);
	}

	/** 图文消息撤回 
	 * @throws Exception */
	@RequestMapping(value="GroupRevoke.do", produces="text/json;charset=UTF-8")
	public String groupRevoke(HttpServletRequest request, HttpServletResponse response) throws Exception{
		int groupLibID = WebUtil.getInt(request, "DocLibID", 0);
		long groupID = WebUtil.getLong(request, "DocIDs", 0);
		
		String error = wxManager.groupRevoke(groupLibID, groupID);
		
        //调用after.do进行后处理：改变流程状态、解锁、刷新列表
        String url = "/e5workspace/after.do?UUID=" + WebUtil.get(request, "UUID");
        if (error == null) {
            url += "&DocIDs=" + groupID; //操作成功
        } else {
            url += "&Info=" + URLEncoder.encode(error, "UTF-8");//有错误，需返回前台做提示
        }
        return "redirect:" + url;
	}

	/** 图文消息删除 
	 * @throws Exception */
	@RequestMapping(value="GroupDelete.do", produces="text/json;charset=UTF-8")
	public String groupDelete(HttpServletRequest request, HttpServletResponse response) throws Exception{
		int groupLibID = WebUtil.getInt(request, "DocLibID", 0);
		long groupID = WebUtil.getLong(request, "DocIDs", 0);
		
		String error = wxManager.groupDelete(groupLibID, groupID);
		
        //调用after.do进行后处理：改变流程状态、解锁、刷新列表
        String url = "/e5workspace/after.do?UUID=" + WebUtil.get(request, "UUID");
        if (error == null) {
            url += "&DocIDs=" + groupID; //操作成功
        } else {
            url += "&Info=" + URLEncoder.encode(error, "UTF-8");//有错误，需返回前台做提示
        }
        return "redirect:" + url;
	}

	/**
	 * 读图文稿件，用于图文编辑
	 */
	@RequestMapping("GroupArticles.do")
	public void getGroupArticles(HttpServletRequest request, HttpServletResponse response){
		int groupLibID = WebUtil.getInt(request, "groupLibID", 0);
		long groupID = WebUtil.getLong(request, "groupID", 0); //组ID
        //从70中取出docs
		Document[] docs = getArticles(groupLibID, groupID);
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		JSONArray articles = new JSONArray();
		if (docs != null) {
			for (Document doc : docs) {
				JSONObject one = new JSONObject();//返回微信图文组中的 每一个图文对象
				one.put("groupArticleID", doc.getDocID());
				one.put("groupArticleLibID", doc.getDocLibID());
				one.put("status", doc.getInt("wx_status"));
				one.put("title", doc.getTopic());
				one.put("content", doc.getString("wx_content"));
				one.put("pic", doc.getString("wx_pic"));
				one.put("url", doc.getString("wx_url"));
				one.put("abstract", doc.getString("wx_abstract"));
				one.put("date", format.format(doc.getLastmodified()));
				one.put("checkID", doc.getString("wx_articleID"));
				one.put("checkLibID", doc.getString("wx_articleLibID"));
				
				articles.add(one);
			}
		}
		
		InfoHelper.outputJson(articles.toString(), response);
	}
	
	/**
	 * 读微信公众号下的素材列表
	 */
	@RequestMapping("GroupListDetail.do")
	public void getGroupListDetail(HttpServletRequest request, HttpServletResponse response){
		String sqlDetail = WebUtil.get(request, "sqlDetail");
		int page = WebUtil.getInt(request, "page", 1);
        int count = 20;//默认每页20条数据
		String returnStr = wxManager.getGroupListDetail(sqlDetail, page, count);
		InfoHelper.outputJson(returnStr, response);
	}
	
	/**
	 * 读一个图文素材的详情，用于图文编辑
	 */
	@RequestMapping("GroupMaterial.do")
	public void getGroupMaterial(HttpServletRequest request, HttpServletResponse response){
		int groupLibID = WebUtil.getInt(request, "materialLibID", 0);
		long groupID = WebUtil.getLong(request, "materialID", 0); //组ID
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			//从素材库中得到对应的稿件
			Document doc = docManager.get(groupLibID, groupID);
			if (doc != null) {
				doc = docManager.get(doc.getInt("wx_articleLibID"), doc.getLong("wx_articleID"));
			}
			
			JSONObject one = new JSONObject();
			if (doc != null) {
				String content = getArticleContent(doc);
				
				String url = doc.getString("a_urlPad");
				String abstract1 = doc.getString("a_abstract");
				if (StringUtils.isBlank(url))
					url = doc.getString("a_url"); //优先使用触屏url
				
				one.put("title", doc.getTopic());
				one.put("content", content);
				one.put("url", url);
				one.put("abstract", abstract1);
				one.put("docID", doc.getDocID());
				one.put("docLibID", doc.getDocLibID());
			}
			
			InfoHelper.outputJson(one.toString(), response);
		} catch (E5Exception e) {
			e.printStackTrace();
		}
	}
	
	//微信素材的稿件内容
	private String getArticleContent(Document doc) {
		String content = doc.getString("a_content");
		if (StringUtils.isBlank(content))
			content = doc.getString("a_abstract"); //没有内容，试着用摘要
		
		int type = doc.getInt("a_type");
		if (type == Article.TYPE_PIC || type == Article.TYPE_VIDEO || type == Article.TYPE_PANORAMA) {
			//若素材是图片稿、视频稿，则应拼到内容里
			ArticleManager articleManager = (ArticleManager)Context.getBean("articleManager");
			Document[] atts = null;
			try {
				atts = articleManager.getAttachments(doc.getDocLibID(), doc.getDocID());
			} catch (E5Exception e) {
				e.printStackTrace();
			}
			
			StringBuilder attContent = new StringBuilder();
			for (Document att : atts) {
				type = att.getInt("att_type");
				if (type == Article.ATTACH_PIC) {
					String url = att.getString("att_path");
					if (!url.startsWith("http"))
						url = "../image.do?path=" + url;
					attContent.append("<p><img src=\"" + url + "\" style=\"max-width:100%;\"/></p>");
					
					//图片说明
					String description = att.getString("att_content");
					if (!StringUtils.isBlank(description)) {
						attContent.append("<p>" + StringUtils.textToHTML(description) + "</p>");
					}
				} else if (type == Article.ATTACH_VIDEO) {
					String url = att.getString("att_urlPad");
					attContent.append("<p><video src=\"" + url + "\" style=\"max-width:100%;\"/></p>");
				}
			}
			content = attContent.toString() + content;
		}
		return content;
	}
	
	/**
	 * 微信自定义菜单操作
	 */
	@RequestMapping("WXMenu.do")
	public String menu(HttpServletRequest request, Map<String, Object> model){
		int accountLibID = WebUtil.getInt(request, "DocLibID", 0);
		int accountID = WebUtil.getInt(request, "DocIDs", 0); //账号ID
		
		model.put("accountID", accountID);
		model.put("docLibID", accountLibID);
		model.put("UUID", WebUtil.get(request, "UUID"));
		
		return "xy/wx/WXMenu";
	}
	
	/**
	 * 微信自定义菜单保存并发布
	 */
	@RequestMapping("WXMenuSave.do")
	public void menuSave(HttpServletRequest request, HttpServletResponse response){
		int accountLibID = WebUtil.getInt(request, "docLibID", 0);
		int accountID = WebUtil.getInt(request, "accountID", 0); //账号ID
		String menu = WebUtil.get(request, "menu");
		
		String result = wxManager.saveMenus(accountLibID, accountID, menu);
		
		//写日志
		LogHelper.writeLog(accountLibID, accountID, request, null);
		
		InfoHelper.outputJson(result, response);
	}
	
	/**
	 * 读已有的菜单
	 */
	@RequestMapping("Menus.do")
	public void getMenus(HttpServletRequest request, HttpServletResponse response){
		int accountLibID = WebUtil.getInt(request, "docLibID", 0);
		int accountID = WebUtil.getInt(request, "accountID", 0); //账号ID
		
		List<Menu> menus = wxManager.getMenus(accountLibID, accountID, true);
		
		JSONArray buttons = new JSONArray();
		
		for (Menu menu : menus) {
			buttons.add(menu.json());
		}
		InfoHelper.outputJson(buttons.toString(), response);
	}
	
	private void save(Document group, List<Document> docs, Document[] oldDocs) {
		DBSession conn = null;
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			conn = E5docHelper.getDBSession(group.getDocLibID());
			conn.beginTransaction();
			
			for (Document doc : docs) {
				docManager.save(doc, conn);
			}
			docManager.save(group, conn);
			
			for (Document doc : oldDocs) {
				docManager.delete(doc);
			}
			
			conn.commitTransaction();
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			
			e.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
	}

	//保存前：准备图文组对象
	private Document prepareGroup(int accountID, int groupLibID, long groupID,
			boolean isNew, JSONArray list, SysUser user) {
		Document group = null;
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			if (isNew) {
				group = docManager.newDocument(groupLibID, groupID);
				ProcHelper.initDoc(group);
				group.setCurrentUserID(user.getUserID());
				group.setCurrentUserName(user.getUserName());
				group.set("wxg_accountID", accountID);
			} else {
				group = docManager.get(groupLibID, groupID);
			}
		} catch (E5Exception e1) {
			e1.printStackTrace();
		}
		
		//为了扩展性，members字段里是一个JSONObject而不是JSONArray
		JSONObject members = new JSONObject();
		members.put("list", list);
		
		group.set("wxg_members", members.toString());
		
		return group;
	}

	//保存前：准备稿件列表
	private List<Document> prepareDocs(int groupLibID, long groupID,
			JSONArray list) {
		List<Document> docs = new ArrayList<>();
		int groupArticleTypeID = DocTypes.WXGROUPARTICLE.typeID();
		int groupArticleLibID = LibHelper.getLibIDByOtherLib(groupArticleTypeID, groupLibID);
		//修改时，读出旧的稿件列表  
		//Document[] oldDocs = getArticles(groupLibID, groupID);
		try {
			for (int i = 0; i < list.size(); i++) {
				JSONObject one = list.getJSONObject(i);
				
				//Document doc = findOneArticle(oldDocs, i, groupArticleLibID);
				Document doc = createOneArticle(groupArticleLibID);
				doc.setTopic(one.getString("title"));
				doc.set("wx_groupID", groupID);
				doc.set("wx_content", one.getString("content"));
				doc.set("wx_pic", one.getString("pic"));
				doc.set("wx_url", one.getString("url"));
				doc.set("wx_articleID", Long.parseLong(one.getString("checkID")));
				doc.set("wx_articleLibID", Integer.parseInt(one.getString("checkLibID")));
				doc.set("wx_status", one.getInt("status"));
				doc.set("wx_abstract",one.optString("abstract",doc.getString("wx_abstract")));
				docs.add(doc);
				
				one.remove("content"); //去掉content，只保留pic和title，最后存到图文组的wxg_members字段里
			}
		} catch (E5Exception e1) {
			e1.printStackTrace();
		}
		return docs;
	}

	/**
	 * 图文稿件保存时，new一个新稿
	 */
	private Document createOneArticle(int groupArticleLibID) throws E5Exception {
		int groupArticleTypeID = DocTypes.WXGROUPARTICLE.typeID();
		long docID = InfoHelper.getNextDocID(groupArticleTypeID);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = docManager.newDocument(groupArticleLibID, docID);
		ProcHelper.initDoc(doc);
		return doc;
	}
	
	/**
	 * 图文稿件保存时，先按顺序得到旧稿件，然后修改。若没有旧稿件，则new一个新稿
	 */
	@SuppressWarnings("unused")
	private Document findOneArticle(Document[] oldDocs, int index, int groupArticleLibID) throws E5Exception {
		if (oldDocs == null || oldDocs.length <= index) {
			int groupArticleTypeID = DocTypes.WXGROUPARTICLE.typeID();
			long docID = InfoHelper.getNextDocID(groupArticleTypeID);
			
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			Document doc = docManager.newDocument(groupArticleLibID, docID);
			ProcHelper.initDoc(doc);
			
			return doc;
		} else {
			return oldDocs[index];
		}
	}

	/**
	 * 读图文稿件
	 */
	private Document[] getArticles(int groupLibID, long groupID) {
		int groupArticleTypeID = DocTypes.WXGROUPARTICLE.typeID();
		int groupArticleLibID = LibHelper.getLibIDByOtherLib(groupArticleTypeID, groupLibID);
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			Document[] docs = docManager.find(groupArticleLibID, "wx_groupID=? order by SYS_DOCUMENTID", 
					new Object[]{groupID});
			return docs;
		} catch (E5Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
    @RequestMapping(value = "getInitCol.do")
    public void getInitCol(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
    	int userID = ProcHelper.getUserID(request);
		int ch = WebUtil.getInt(request, "ch", 0);
		int siteID = WebUtil.getInt(request, "siteID", 0);
		int colLibID = LibHelper.getColumnLibID(request);
		int roleID = ProcHelper.getRoleID(request);
		long colID = 0;
		String colName = null;

		Column[] cols = colReader.getOpColumns(colLibID, userID, siteID, ch, roleID);
		if(cols.length > 0){
			colID = cols[0].getId();
			colName = cols[0].getName();
		}
        JSONObject json = new JSONObject();
        json.put("colID", colID);
        json.put("colName", colName);
        InfoHelper.outputJson(json.toString(), response);
    }
    
	/** 打开选取稿件对话框
	 * */
	@RequestMapping(value = "select.do")
	public ModelAndView select(HttpServletRequest request) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		String tenantCode = InfoHelper.getTenantCode(request);
		
		model.put("siteID", WebUtil.getInt(request, "siteID", 0));
		model.put("UUID", WebUtil.get(request, "UUID"));
		int menuID = WebUtil.getInt(request, "groupID",0);
		int menuLibID = LibHelper.getLibID(DocTypes.WXMENU.typeID(), tenantCode);
		Document doc = docManager.get(menuLibID, menuID);
		model.put("menuID", WebUtil.getInt(request, "groupID", 0));
		model.put("accountID", doc.getInt("wxm_accountID"));

		int docLibID = LibHelper.getLibID(DocTypes.WXARTICLE.typeID(), tenantCode); 
		model.put("docLibID", docLibID);
		return new ModelAndView("/xy/wx/SelectArt", model);
	}
	
	/** 选稿的保存
	 * artIDs 发布库稿件IDs
	 * menuID 微信菜单ID
	 * */
	@RequestMapping(value = "DealSelect.do")
	public void dealSelect(HttpServletRequest request, HttpServletResponse response,
			@RequestParam int  artLibID, @RequestParam String artIDs,
			@RequestParam int menuID, @RequestParam int accountID) throws Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		List<Document> articles = new ArrayList<Document>();
		long[] aIDs = StringUtils.getLongArray(artIDs);
		int wxLibID = LibHelper.getLibID(DocTypes.WXARTICLE.typeID(), InfoHelper.getTenantCode(request));
		int blockLibID = LibHelper.getLibIDByOtherLib(DocTypes.WXMENU.typeID(), wxLibID);
		Document block = docManager.get(blockLibID, menuID);
		boolean needAudit = block.getInt("wxm_audit") == 1;
		for( long aID : aIDs){
			Document doc = docManager.get(artLibID, aID);
			articles.add(doc);
		}
		
		List<Document> wxarticles = new ArrayList<Document>();
		StringBuilder newDocIDs = new StringBuilder();
		for(Document article : articles){
            long wxarticleID = InfoHelper.getNextDocID(DocTypes.WXARTICLE.typeID());
            Document wxarticle = docManager.newDocument(wxLibID, wxarticleID);
            wxarticle.setAuthors(article.getAuthors());
            wxarticle.setTopic(article.getTopic());
            wxarticle.setFolderID(DomHelper.getFVIDByDocLibID(wxLibID));
            wxarticle.set("wx_menuID", menuID);
            wxarticle.set("wx_accountID", accountID);
            wxarticle.set("wx_url", article.getString("a_urlPad"));
            wxarticle.set("wx_abstract", article.getString("a_abstract"));
            //根据区块是否审核判断稿件的流程状态
			if (needAudit){
				ProcHelper.initDoc(wxarticle);
			}else{
				wxarticle.setCurrentFlow(0);
				wxarticle.setCurrentNode(0);
				wxarticle.setCurrentStatus("已发布");
			}
            
            String picSmall = article.getString("a_picSmall");
            String picMiddle = article.getString("a_picMiddle");
            String picBig = article.getString("a_picBig");
            if ( picSmall != null && !picSmall.isEmpty()){
            	wxarticle.set("wx_pic", picSmall);
            } else {
            	if( picMiddle !=null && !picSmall.isEmpty()){
            		wxarticle.set("wx_pic", picMiddle);
            	}else{
            		wxarticle.set("wx_pic", picBig);
            	}
            }
            wxarticle.set("wx_pubTime", article.getString("a_pubTime"));
            wxarticle.set("wx_order", wxarticleID);
            wxarticle.set("wx_subTitle", article.getString("a_subTitle"));
            wxarticle.set("wx_articleID", article.getDocID());
            wxarticle.set("wx_articleLibID", article.getDocLibID());
            wxarticles.add(wxarticle);
            
            newDocIDs.append(wxarticleID + ",");
		}
		
		String error = save(wxLibID,wxarticles);
		//成功了写日志
		if(error == null){
			//触发发布
			if(!needAudit){
				PublishTrigger.wx(blockLibID, menuID);
			}
			//写日志
			SysUser sysUser = ProcHelper.getUser(request);
			long columnID = 0;
	        for (Document article : articles) {
	        	columnID = article.getLong("a_columnID");
	            LogHelper.writeLog(article.getDocLibID(), article.getDocID(), sysUser, "选用到微信",null);
	        }
	        
	        int colLibID = LibHelper.getLibIDByOtherLib(DocTypes.COLUMN.typeID(), wxLibID);
	        Column col = colReader.get(colLibID, columnID);
	        String opnions = "来自栏目：" + col.getCasNames();
			InfoHelper.outputText("success"+newDocIDs.toString().substring(0, newDocIDs.length() - 1) + opnions, response);
		}else{
			InfoHelper.outputText("Failed", response);
		}
	}
	
    private String save(int docLibID, List<Document> articles) {
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        //同时修改多个稿件，使用事务
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
     * 取某站点下的微信公众号
     */
    @RequestMapping(value = "getAccounts.do")
    public ModelAndView getAccounts(HttpServletRequest request) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		String tenantCode = InfoHelper.getTenantCode(request);
		
		int docLibID = LibHelper.getLibID(DocTypes.WXACCOUNT.typeID(), tenantCode); 
		int siteID = WebUtil.getInt(request, "siteID", 0);
		SysUser user = ProcHelper.getUser(request);
		int checkType = WebUtil.getInt(request, "checkType", 0);
		int DocIDs = WebUtil.getInt(request, "DocIDs", 0);
		String selectIDs = WebUtil.getStringParam(request, "selectIDs");
		List<Account> accounts = null;
		
		model.put("siteID", siteID);
		model.put("userID", user.getUserID());
		model.put("checkType", checkType);
		model.put("DocIDs", DocIDs);
		model.put("checkIds", WebUtil.getStringParam(request, "checkIds"));
		model.put("docLibID", docLibID);
		model.put("selectIDs", selectIDs);
		if(checkType == 0){//取所有的微信公众号
			accounts = wxManager.getAccounts(tenantCode, siteID, false, false);
		} else if(checkType == 1){//取有权限的微信公众号
			accounts = wxManager.getAdminAccounts(tenantCode, user.getUserID(), siteID, false, false);
		}
		model.put("accounts", accounts);
		return new ModelAndView("/xy/wx/WeixinSelect", model);
	}
    
    /**
	 * 微信图文组  送审\审核通过\驳回
	 */
	@RequestMapping("WXdoflow.do")
	public void doflow(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		int groupLibID = WebUtil.getInt(request, "DocLibID", 0);
		long groupID = WebUtil.getInt(request, "DocIDs", 0);
		int type = WebUtil.getInt(request, "type", 0);// 0送审 1审核通过 2驳回
		int status = Original.STATUS_AUDITING;
		if(type==1) status = Original.STATUS_PUBNOT;
		if(type==2) status = Original.STATUS_REJECTED;
		Document[] groupArticles = getArticles(groupLibID, groupID);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		wxManager.setAllStatus(docManager, groupArticles, status);
		// 获取微信图文
		Document doc = docManager.get(groupLibID, groupID);
		doc.set("wxg_status", status);
		docManager.save(doc);
		InfoHelper.outputText("success", response);
	}
	
	/**
	 * 图文详情页中  图文组中的图文稿件  全部审核通过\全部驳回
	 */
	@RequestMapping("EditorAlldoflow.do")
	public void editorAlldoflow(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		int type = WebUtil.getInt(request, "Type", 0);// 1全部审核通过 2全部驳回
		if(type == 0 || type > 2){
			InfoHelper.outputText("failure", response);
			return;
		}
		int groupLibID = WebUtil.getInt(request, "GroupLibID", 0);
		long groupID = WebUtil.getInt(request, "GroupID", 0);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document group = docManager.get(groupLibID, groupID);
		Document[] groupArticles = getArticles(groupLibID, groupID);
		SysUser user = ProcHelper.getUser(request);
		group.setCurrentUserID(user.getUserID());
		group.setCurrentUserName(user.getUserName());
		int status = 0;
		if(type == 1){
			status = Original.STATUS_PUBNOT;
		} else if (type == 2){
			status = Original.STATUS_REJECTED;
		}
		wxManager.setAllStatus(docManager, groupArticles, status);
		group.set("wxg_status", status);
		wxManager.setNextFlow(group, type);
		docManager.save(group);
		
		String reason = WebUtil.get(request, "Reason");
		LogHelper.writeLog(groupLibID, groupID, user, type==1?"全部通过":"全部驳回", reason);
		
		InfoHelper.outputText("success", response);
	}
	
	/**
	 * 图文详情页中  图文组中的单个图文稿件  审核通过\驳回
	 */
	@RequestMapping("Editordoflow.do")
	public void editordoflow(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		int type = WebUtil.getInt(request, "Type", 0);// 1审核通过 2驳回
		if(type == 0 || type > 2){
			InfoHelper.outputText("failure", response);
			return;
		}
		int groupArticleLibID = WebUtil.getInt(request, "GroupArticleLibID", 0);
		long groupArticleID = WebUtil.getInt(request, "GroupArticleID", 0);
		int groupLibID = WebUtil.getInt(request, "GroupLibID", 0);
		long groupID = WebUtil.getInt(request, "GroupID", 0);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document groupArticle = docManager.get(groupArticleLibID, groupArticleID);
		Document group = docManager.get(groupLibID, groupID);
		SysUser user = ProcHelper.getUser(request);
		group.setCurrentUserID(user.getUserID());
		group.setCurrentUserName(user.getUserName());
		int status = 0;
		String returnStatus = "success";
		if(type == 1){
			//审核
			status = Original.STATUS_PUBNOT;
			groupArticle.set("wx_status", status);
			//向wxgrouparticle表保存
			docManager.save(groupArticle);
		}
		Document[] docs = getArticles(groupLibID, groupID);
		if(type == 2){
		    //驳回
			status = Original.STATUS_REJECTED;
			wxManager.setAllStatus(docManager, docs, status);
		}
		//判断图文组的状态
		boolean isAllpub = wxManager.isAllSameStatus(docs, status);
		if(isAllpub){//如果当前图文组中的稿件全部为同一状态  将这个图文组设置对应的流程
			wxManager.setNextFlow(group, type);
			group.set("wxg_status", status);
			docManager.save(group);
			//审批流程
            LogHelper.writeLog(groupLibID, groupID, user, type==1?"审核通过":"驳回", null);
            returnStatus = "successAll";
		}
		String reason = WebUtil.get(request, "Reason");
		LogHelper.writeLog(groupArticleLibID, groupArticleID, user, type==1?"审核通过":"驳回", reason);
		
		InfoHelper.outputText(returnStatus, response);
	}

	/**
	 * 图文编辑器中的 送审功能
	 */
	@RequestMapping("EditorCensorship.do")
	@ResponseBody
	public String editorCensorship(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		int groupLibID = WebUtil.getInt(request, "GroupLibID", 0);
		long groupID = WebUtil.getInt(request, "GroupID", 0);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document group = docManager.get(groupLibID, groupID);
		if(group.getInt("wxg_status") == Original.STATUS_AUDITING){
			return "failure";
		}
		SysUser user = ProcHelper.getUser(request);
		group.setCurrentUserID(user.getUserID());
		group.setCurrentUserName(user.getUserName());
		int status = Original.STATUS_AUDITING;
		Document[] docs = getArticles(groupLibID, groupID);
		wxManager.setAllStatus(docManager, docs, status);
		wxManager.setNextFlow(group, 0);
		group.set("wxg_status", status);
		docManager.save(group);
		
		String reason = WebUtil.get(request, "Reason");
		LogHelper.writeLog(groupLibID, groupID, user, "送审", reason);
		return "success";
	}
	
	/**
     * 微信公众号模糊查找功能
     */
    @RequestMapping(value = "find.do")
    public void find(HttpServletRequest request, HttpServletResponse response,
            @RequestParam int siteID, @RequestParam String q) throws Exception {
    	String tenantCode = InfoHelper.getTenantCode(request);
    	Document[] accounts = wxManager.find(siteID, q, tenantCode);
    	String result = json(accounts);
        InfoHelper.outputJson(result, response);
    }
    
    //查找结果的json，格式为[{key,value},{key,value},...]
    private String json(Document[] accounts) throws E5Exception {
        StringBuilder result = new StringBuilder();
        result.append("[");

        for (Document account : accounts) {
            if (result.length() > 1) result.append(",");

            result.append("{\"value\":\"").append(InfoHelper.filter4Json(account.getString("wxa_name")))
                    .append("\",\"key\":\"").append(String.valueOf(account.getDocID()))
                    .append("\"}");
        }
        result.append("]");

        return result.toString();
    }
    
    /**
     * 图文详情页中 全文复制
     */
    @RequestMapping(value = "EditorCopy.do",produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String editorCopy(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
    	String data = WebUtil.get(request, "Content");
		//微信图片发布目录
		String webroot = InfoHelper.getConfig("写稿服务", "微信图片发布目录");//存放微信稿件中图片的路径，用于微信稿件上传，例如：Z:/webroot/wxarticle/pic
		//微信图片外网地址
		String weburl = InfoHelper.getConfig("写稿服务", "微信图片外网地址"); //直接指向微信图片发布目录，例如：http://www.demo.com/wxarticle/pic
		if(!StringUtils.isBlank(webroot) && !StringUtils.isBlank(weburl)){
			if(!webroot.endsWith("/")) webroot += "/";
			if(!weburl.endsWith("/")) weburl += "/";
			// 开始存储到存储设备上
			StorageDeviceManager sdManager = SysFactory.getStorageDeviceManager();
			//正则匹配图片，复制图片到发布路径，并更换
			String regex = "src\\s*=\\s*\"?(.*?path=(.*?);(.*?))(\"|>|\\s+)";
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(data);
			while(m.find()){
				System.out.println(m.group());
				try {
					InputStream input = sdManager.read(m.group(2), m.group(3));
					OutputStream output = new FileOutputStream(webroot + m.group(3));
					IOUtils.copy(input, output);
					data = data.replace(m.group(1), weburl+m.group(3));
				} catch (Exception e) {}
			}
		}
		return data;
	}
    
    /**
     * 图文详情页中 修改稿件内容
     * @param request
     * @param response
     * @throws Exception
     */
    /*
    @RequestMapping(value = "EditContent.do")
    public void editContent(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		int checkLibID = WebUtil.getInt(request, "DocLibID", 0);
		long checkID = WebUtil.getInt(request, "DocIDs", 0);
		int wxLibID = WebUtil.getInt(request, "WxLibID", 0);
		long wxID = WebUtil.getInt(request, "WxID", 0);
		String content = WebUtil.get(request, "Content");
		// 获取微信图文
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = docManager.get(wxLibID, wxID);//获取微信图文对象
		doc.set("wx_content", content);
		docManager.save(doc);
		// 记录历史版本
		ArticleManager articleManager = (ArticleManager) Context.getBean(ArticleManager.class);
		SysUser user = ProcHelper.getUser(request);
		Document article = docManager.get(checkLibID, checkID);//获取来源库稿件对象
		articleManager.recordHistoryVersion(docManager, article, 
				InfoHelper.getTenantCode(request), "修改", user.getUserName());
		InfoHelper.outputText("success", response);
	}
    */
    
}
