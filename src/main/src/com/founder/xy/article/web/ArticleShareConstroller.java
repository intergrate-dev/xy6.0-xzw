package com.founder.xy.article.web;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.founder.e5.cat.CatManager;
import com.founder.e5.cat.CatReader;
import com.founder.e5.cat.Category;
import com.founder.e5.commons.DateUtils;
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
import com.founder.e5.dom.DocLib;
import com.founder.e5.web.SysUser;
import com.founder.e5.web.WebUtil;
import com.founder.e5.workspace.ProcHelper;
import com.founder.e5.workspace.app.form.LogHelper;
import com.founder.xy.article.Article;
import com.founder.xy.article.ArticleManager;
import com.founder.xy.column.Column;
import com.founder.xy.column.ColumnReader;
import com.founder.xy.commons.CatTypes;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.config.Channel;
import com.founder.xy.config.ConfigReader;
import com.founder.xy.jms.PublishTrigger;
import com.founder.xy.weibo.WeiboManager;
import com.founder.xy.wx.WeixinManager;
import com.founder.xy.wx.data.Account;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
/**
 * 优化ArticleOperation类,将推送功能拆分出来
 * 推送区块,推送客户端,推送微信，推送微博,推送渠道
 * @author han.xf
 *
 */
@Controller
@RequestMapping("/xy/article")
public class ArticleShareConstroller {
	@Autowired
    private ColumnReader colReader;
    
    @Autowired
    ArticleManager articleManager;

	@Autowired
	private WeiboManager wbManager;
	
	/**
     * 推送渠道
     */
    @RequestMapping("PushChannel.do")
    public ModelAndView pushChannel(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();
        int docLibID = WebUtil.getInt(request, "DocLibID", 0);
        String strDocIDs = WebUtil.get(request, "DocIDs");
        List<Long> pushedDocIDs = new ArrayList<>();
        List<Channel> chs = getPushChannels(docLibID, strDocIDs, request,pushedDocIDs);

        model.put("channels", chs);
        model.put("channelCount", chs.size());
        model.put("DocLibID", docLibID);
        model.put("DocIDs", strDocIDs);
        model.put("pushedDocIDsCount", pushedDocIDs.size());
        model.put("pushedDocIDs", pushedDocIDs.toString());
        model.put("UUID", WebUtil.get(request, "UUID"));
        model.put("siteID", WebUtil.get(request, "siteID"));

        return new ModelAndView("/xy/article/PushChannel", model);
    }
	
	/**
	 * 推送区块
	 */
	@RequestMapping("Push.do")
	public ModelAndView push(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    String docIDs = request.getParameter("DocIDs");
	    int siteID = WebUtil.getInt(request, "siteID", 0);
	    int docLibID = WebUtil.getInt(request, "DocLibID", 0);
	    Map<String, Object> model = new HashMap<String, Object>();
	
	    model.put("siteID", siteID);
	    model.put("docIDs", docIDs);
	    model.put("docLibID", docLibID);
	    return new ModelAndView("/xy/block/BlockSelect", model);
	}
	/**
     * 推送客户端
     */
    @RequestMapping("PushApp.do")
    public ModelAndView pushApp(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        int docLibID = WebUtil.getInt(request, "DocLibID", 0);
        long docID = WebUtil.getLong(request, "DocIDs", 0);

        Document article = docManager.get(docLibID, docID);

        model.put("description", article.getTopic());
        model.put("DocLibID", docLibID);
        model.put("DocIDs", docID);
        model.put("UUID", WebUtil.get(request, "UUID"));
        model.put("siteID", WebUtil.get(request, "siteID"));
        model.put("type", 0);
        model.put("cattypeID", CatTypes.CAT_PUSHREGION.typeID()) ;
        return new ModelAndView("/xy/article/PushApp", model);
    }
    
	/**
	 * 保存到消息推送任务表
	 */
    @RequestMapping(value = "AddPushTask.do")
    public void addPushTask(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
    	DocumentManager docManager = DocumentManagerFactory.getInstance();
        String tenantCode = InfoHelper.getTenantCode(request);
    	int docLibID = WebUtil.getInt(request, "DocLibID", 0);
        long docID = WebUtil.getLong(request, "DocIDs", 0);
        long topicID = WebUtil.getLong(request, "topicID",0);
        int siteID = WebUtil.getInt(request, "siteID", 0);
        String pushTime = WebUtil.get(request, "pushTime");
        int type = WebUtil.getInt(request, "type",0);
        String description = WebUtil.get(request, "description");
        int targetUser = WebUtil.getInt(request,"targetUser",0) ;
        String regionIDS = WebUtil.getStringParam(request, "push_regionIDS") ;
        
        //获取到发布库的文章
        Document article = docManager.get(docLibID, docID);
        //获取消息推送任务的库ID
        int taskLibID = LibHelper.getLibID(DocTypes.PUSHTASK.typeID(), tenantCode);
        //创建新的消息推送任务
        Document task = docManager.newDocument(taskLibID);
        task.setTopic(description); //标题
        task.set("a_siteID", siteID);
        task.set("a_articleLibID", docLibID);
        task.set("a_articleID", docID);
        
        int atype = article.getInt("a_type");
        task.set("a_type", atype);
        
        if (atype == Article.TYPE_SPECIAL || atype == Article.TYPE_LIVE || atype == Article.TYPE_SUBJECT) {
        	//若是专题或直播，则没有发布地址，此时需传递对应栏目ID
        	String linkID = article.getString("a_linkID");
            task.set("a_url", linkID);
            task.set("a_urlPad", linkID);
        } else {
            task.set("a_url", article.getString("a_url"));
            task.set("a_urlPad", article.getString("a_urlPad"));
        }
        
        //若不为空，则设置定时发布
        if(StringUtils.isBlank(pushTime)){
        	task.set("push_time", DateUtils.getTimestamp());
        }else{
        	task.set("push_time", new Timestamp(DateUtils.parse(pushTime,"yyyy-MM-dd HH:mm").getTime()));
        }
        
        //若选择区域发送,则设置地区代码,若没有选择,则按全国推送处理
    	task.set("push_region",getPushRegionCode(targetUser, regionIDS));
        task.set("push_type", type);
        task.set("push_topicID", topicID);
        
        docManager.save(task);
        
        if (topicID > 0) {
        	docLibID = LibHelper.getLibID(DocTypes.TOPIC.typeID(), tenantCode);
        	docID = topicID;
        }
        LogHelper.writeLog(docLibID, docID, request, null); //写日志
        
    	InfoHelper.outputText("ok", response);   
    }
    
    /**
     * 推送微信
     */
    @RequestMapping("PushWX.do")
    public ModelAndView pushWX(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        String DocIDs = request.getParameter("DocIDs");
        long[]  docIDs = StringUtils.getLongArray(DocIDs);
        int siteID = WebUtil.getInt(request, "siteID", 0);
        int docLibID = WebUtil.getInt(request, "DocLibID", 0);
        int articleLibID = LibHelper.getLibIDByOtherLib(DocTypes.PAPERARTICLE.typeID(), docLibID);
        long tmpPadID = 0;
        if( docLibID != articleLibID ){//稿件库
            int colID = WebUtil.getInt(request, "colID", 0);
            int colLibID = LibHelper.getLibID(DocTypes.COLUMN.typeID(), InfoHelper.getTenantCode(request));
            Column col = colReader.get(colLibID, colID);
            tmpPadID = col.getTemplateArticlePad();
        }else {//数字报稿件库
        	Document paperArticle = docManager.get(docLibID, docIDs[0]);
        	int paperID = paperArticle.getInt("a_paperID");
        	int paperDocLibID = LibHelper.getLibID(DocTypes.PAPER.typeID(), InfoHelper.getTenantCode(request));
        	Document paper = docManager.get(paperDocLibID, paperID);
        	tmpPadID = paper.getInt("pa_templateArticlePad_ID");
        }

		WeixinManager wxManager = (WeixinManager)Context.getBean("weixinManager");
		List<Account> acs = wxManager.getAccounts(InfoHelper.getTenantCode(request), siteID, true, false);
		
		model.put("tmpPadID", tmpPadID);
		model.put("accounts", acs);
        model.put("siteID", siteID);
        model.put("docIDs", DocIDs);
        model.put("docLibID", docLibID);
        model.put("UUID", WebUtil.get(request, "UUID"));
        
        return new ModelAndView("/xy/article/WXSelect", model);
    }
    
    @RequestMapping(value = "DealWXPush.do")
    public void dealWXPush(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
    	DocumentManager docManager = DocumentManagerFactory.getInstance();
    	JSONArray params = JSONArray.fromObject(WebUtil.get(request, "paramData")); 
   
    	long[] docIDs = StringUtils.getLongArray(WebUtil.getStringParam(request, "DocIDs"));
        int docLibID = WebUtil.getInt(request, "DocLibID", 0);
        
        List<Document> articles = new ArrayList<Document>();
        for (int i = 0; i < docIDs.length; i++) {
            long articleID = docIDs[i];
            Document article = docManager.get(docLibID, articleID);
            articles.add(article);
        }
        SysUser sysUser = ProcHelper.getUser(request);
        int wxLibID = LibHelper.getLibID(DocTypes.WXARTICLE.typeID(), InfoHelper.getTenantCode(request));
        String error = savePushWXArticles(wxLibID, params, articles,sysUser);
       
        if (error == null) {
            InfoHelper.outputText("ok", response);
        } else {
            InfoHelper.outputText("Failed", response);
        }

    }

	/**
     * 推送微博
     */
    @RequestMapping(value = "PushWB.do")
    public ModelAndView pushWB(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        int docID = WebUtil.getInt(request, "DocIDs", 0);
        int siteID = WebUtil.getInt(request, "siteID", 0);
        int docLibID = WebUtil.getInt(request, "DocLibID", 0);
        WeiboManager wbManager = (WeiboManager)Context.getBean("weiboManager");
        model.put("accounts", wbManager.getAccounts(InfoHelper.getTenantCode(request), siteID));
        
        StringBuilder textarea = new StringBuilder();
        Document article = docManager.get(docLibID, docID);
        
        //从稿件中组织微博内容
        String text = null;
        String stract = article.getString("a_abstract");
        if (StringUtils.isBlank(stract)){
            String content = article.getTopic();
        	if (content != null){
        		text = StringUtils.xhtml2Text(content);
        	} else {
        		text = "";
        	}
        } else {
        	text = StringUtils.xhtml2Text(stract);
        }
        int strlength = ( text.length() > 139) ? 139 : text.length();
    	textarea.append(text.substring(0, strlength));
    	
        textarea.append("\r\n查看原文");
        //组织查看原文链接
        String urlPad = article .getString("a_urlPad");
        if (StringUtils.isBlank(urlPad)){
            String url = article.getString("a_url");
        	textarea.append(url);
        } else {
        	textarea.append(urlPad);
        }
        //组织视频
        int attLibID = LibHelper.getAttaLibID(request);
        String sql = "att_articleID=? and att_articleLibID=? and att_type=? order by att_order";
        Document[] atts = docManager.find(attLibID, sql, new Object[]{docID, docLibID,1});
        for (Document _a : atts) {
            if(_a != null){
                textarea.append("\r\n");
            	textarea.append("视频");
                String atturl = article.getString("att_url");
                String atturlPad = article .getString("att_urlPad");
                if ("".equals(atturlPad) || atturlPad == null){
                	textarea.append(atturl);
                }else{
                	textarea.append(atturlPad);
                }
            }
        }
        //组织不超过九个的图片附件
        JSONArray imgArr = new JSONArray();
        JSONObject json = null;
        Document[] pics = docManager.find(attLibID, sql, new Object[]{docID, docLibID,0});
        
        int i = 0;
        int length = 0;
        while (length < 10 && i < pics.length){
           	String pic = pics[i].getString("att_path");
        	if (!pic.startsWith("http")){
            	json = new JSONObject();
                json.element("id", pics[i].getDocID());
                json.element("imgUrl", "../../xy/image.do?path=" + pic);   
                imgArr.add(json);
                length ++;
        	}
        	i++;
        }
        String description = StringUtils.textFilter(textarea.toString()).trim();
        model.put("description", description);
        model.put("imgs", imgArr);
        model.put("siteID", siteID);
        model.put("docID", docID);
        model.put("docLibID", docLibID);
        model.put("UUID", WebUtil.get(request, "UUID"));
        
        return new ModelAndView("/xy/article/WBSelect", model);
    }
    	
    @RequestMapping(value = "DealWBPush.do")
    public void dealWBPush(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
    	DocumentManager docManager = DocumentManagerFactory.getInstance();
    	JSONArray paramWB = JSONArray.fromObject(WebUtil.get(request, "paramWB")); 
    	JSONArray paramPic = JSONArray.fromObject(WebUtil.get(request, "paramPic"));
    	String content = WebUtil.getStringParam(request, "content");
    	long docID = WebUtil.getLong(request, "DocIDs", 0);
        int docLibID = WebUtil.getInt(request, "DocLibID", 0);
        
        Document article = docManager.get(docLibID, docID);
    
        //组织微博图片数据
        int attaLibID = LibHelper.getLibID(DocTypes.ATTACHMENT.typeID(), InfoHelper.getTenantCode(request));
        StringBuilder  attas = new StringBuilder();
        attas.append("{\"pics\":[");
        for(Object object : paramPic){
        	JSONObject pic = JSONObject.fromObject(object);
        	int picID = getInt(pic,"id");
        	Document doc = docManager.get(attaLibID, picID);
        	
        	attas.append("\"").append(doc.get("att_path")).append("\",");
        }
        attas.deleteCharAt(attas.length()-1);
        attas.append("],\"videos\":[]}");
        
        SysUser sysUser = ProcHelper.getUser(request);
        int wbLibID = LibHelper.getLibID(DocTypes.WBARTICLE.typeID(), InfoHelper.getTenantCode(request));
        String error = savePushWBArticles(wbLibID, paramWB, attas.toString(), content, article, sysUser);
       
        if (error == null) {
            InfoHelper.outputText("ok", response);
        } else {
            InfoHelper.outputText("Failed", response);
        }
    }

	private String savePushWXArticles(int wxLibID, JSONArray params, List<Document> articles,SysUser sysUser) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		List<Document> wxarticles = new ArrayList<Document>();
		int wxMenuLibID = LibHelper.getLibIDByOtherLib(DocTypes.WXMENU.typeID(), wxLibID);
		for( Document article : articles){
		    for(Object object : params){
		    	JSONObject menu = JSONObject.fromObject(object); 
	            long wxarticleID = InfoHelper.getNextDocID(DocTypes.WXARTICLE.typeID());
	            Document wxarticle = docManager.newDocument(wxLibID, wxarticleID);
	            wxarticle.setAuthors(article.getAuthors());
	            wxarticle.setTopic(article.getTopic());
	            
	            int menuID = getInt(menu,"id");
	            boolean needAudit = false;
	            if (menuID > 0) {
					Document block = docManager.get(wxMenuLibID, menuID);
					needAudit = block.getInt("wxm_audit") == 1;
	            }
	            wxarticle.set("wx_menuID", menuID);
	            wxarticle.set("wx_accountID", getString(menu,"accountID"));
				//根据区块是否审核判断稿件的流程状态
				if (needAudit){
					ProcHelper.initDoc(wxarticle);
				}else{
					wxarticle.setCurrentFlow(0);
					wxarticle.setCurrentNode(0);
					wxarticle.setCurrentStatus("已发布");
				}
				
	            wxarticle.setFolderID(DomHelper.getFVIDByDocLibID(wxLibID));
	            wxarticle.set("wx_url", article.getString("a_urlPad"));
	            wxarticle.set("wx_abstract", article.getString("a_abstract"));
	            
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
		    }
		}
	
		String error = save(wxLibID,wxarticles);
	
		if ( error == null ){
			//触发菜单更新
			for(Object object : params){
				JSONObject menu = JSONObject.fromObject(object); 
				int menuID = getInt(menu,"id");
				if (menuID > 0) {
					Document block = docManager.get(wxMenuLibID, menuID);
					boolean needAudit = block.getInt("wxm_audit") == 1;
					if (!needAudit){
						PublishTrigger.wx(wxMenuLibID, menuID);
					}
				}
			}
			
	        int paLibID = LibHelper.getLibIDByOtherLib(DocTypes.PAPERARTICLE.typeID(), wxLibID);
	        
			long columnID = 0;
			boolean bPaper = false;
	        String operate = null;
	        String log = null;
	        
	        for (Document article : articles) {
	        	if( paLibID == article.getDocLibID()){
	        		bPaper = true;
	        		if ( log == null ){//稿件都是一个版面的所以收集一次日志就行
	        			log = "来自数字报：" + article.getString("a_paper") + "~" + article.getString("a_pubTime").substring(0,10) + "~" + article.getString("a_layout");
	        			operate = "数字报推送";
	        		}
	        	} else {
	        		columnID = article.getLong("a_columnID");
	        	}
	        	
	            LogHelper.writeLog(article.getDocLibID(), article.getDocID(), sysUser, "推送(微信)",null);
	        }
			
	        if (!bPaper){
		        int colLibID = LibHelper.getLibIDByOtherLib(DocTypes.COLUMN.typeID(), wxLibID);
		        Column col = colReader.get(colLibID, columnID);
		        log = "来自栏目：" + col.getCasNames();
		        operate = "发布库推送";
	        }
	
	        for (Document wxarticle : wxarticles) {
	            LogHelper.writeLog(wxarticle.getDocLibID(), wxarticle.getDocID(), sysUser, operate, log);
	        }
		} 
		return error;
	}

	private String savePushWBArticles(int wbLibID, JSONArray paramWB,
			String attas, String content, Document article, SysUser sysUser) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		List<Document> wbarticles = new ArrayList<Document>();
		for(Object object : paramWB){
	    	JSONObject wb = JSONObject.fromObject(object); 
            long wbarticleID = InfoHelper.getNextDocID(DocTypes.WBARTICLE.typeID());
            Document wbarticle = docManager.newDocument(wbLibID, wbarticleID);
            wbarticle.setAuthors(article.getAuthors());
            int wbaccountID = getInt(wb,"id");
            wbarticle.set("wb_accountID", wbaccountID);
            wbarticle.set("wb_content", content);
            wbarticle.set("wb_attachments", attas);
            wbarticles.add(wbarticle);
		}
		
		String error = save(wbLibID,wbarticles);
		
		if ( error == null ){
			for(Document wbarticle : wbarticles ){
				wbManager.publish(wbarticle.getDocLibID(), wbarticle.getDocID());
			}
	        LogHelper.writeLog(article.getDocLibID(), article.getDocID(), sysUser, "推送(微博)",null);
	        
	        int paLibID = LibHelper.getLibIDByOtherLib(DocTypes.PAPERARTICLE.typeID(), wbLibID);
	        String log = null;
	        String operate = null;
        	if( paLibID == article.getDocLibID()){
        		log = "来自数字报：" + article.getString("a_paper") + "~" + article.getString("a_pubTime").substring(0,10) + "~" + article.getString("a_layout");
        		operate = "数字报推送";
        	} else {
		        int colLibID = LibHelper.getLibIDByOtherLib(DocTypes.COLUMN.typeID(), wbLibID);
		        Column col = colReader.get(colLibID, article.getLong("a_columnID"));
		        log = "来自栏目：" + col.getCasNames();
		        operate = "发布库推送";
        	}

	        for (Document wbarticle : wbarticles) {
	            LogHelper.writeLog(wbarticle.getDocLibID(), wbarticle.getDocID(), sysUser, operate ,log);
	        }
		} 
		return error;
	}
	private List<Channel> getPushChannels(int docLibID, String DocID, HttpServletRequest request, List<Long> pushedDocIDs) throws E5Exception {
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        String tenantCode = InfoHelper.getTenantCode(request);
        Channel[] allChs = ConfigReader.getChannels();
        //取出租户下的发布库。渠道代号是从0开始，正好与发布库一一对应
        List<DocLib> articleLibs = LibHelper.getLibs(DocTypes.ARTICLE.typeID(), tenantCode);
        List<Channel> chs = new ArrayList<Channel>();
        for (int i = 0; i < allChs.length; i++) {
            if (allChs[i] != null && articleLibs.get(i).getDocLibID() != docLibID) {
                Document[] docs = docManager.get(articleLibs.get(i).getDocLibID(), StringUtils.getLongArray(DocID));
                for(Document doc:docs) {
                    if (doc != null) {
                        pushedDocIDs.add(doc.getDocID());
                    }
                }
                if(pushedDocIDs.size()==0){
                    chs.add(allChs[i]);
                }

            }
        }
        return chs;
    }
	
	/**
	 * 多个稿件的统一提交， 出错时返回错误信息
	 */
	private String save(int docLibID, List<Document> articles) {
		if (articles == null || articles.size() == 0) return null;
		
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
	
	private String getPushRegionCode(int targetUser,String regionIDS)throws E5Exception{
		JSONArray codeJson = new JSONArray() ;
        if(targetUser == 1 && regionIDS != null && !"".equals(regionIDS)){
    		String[] ids = regionIDS.split(";") ;
    		Map<String,String> pIdsMap = new HashMap<>() ;
    		List<Integer> idslList = new ArrayList<>() ;
    		for(String i : ids){
    			//代码中的省份id
    			if(!i.contains("_")){
    				pIdsMap.put(i, "") ;
    				idslList.add(Integer.parseInt(i)) ;
        		}
    		}
    		Map<String,String> cIdsMap = new HashMap<>() ;
    		CatReader catReader = (CatReader) Context.getBean(CatManager.class);
    		for(String c : ids){
        		if(c.contains("_")){
        			//去除已有省份下的城市
        			String pID = c.split("_")[0] ;
        			String cID = c.split("_")[1] ;
        			if(pIdsMap.get(pID) == null){
        				if(cIdsMap.get(pID) == null){
        					cIdsMap.put(pID, cID) ;
        				}else{
        					cIdsMap.put(pID,cID + ";" + cIdsMap.get(pID));
        				}
        			}
        		}
        	}
    		Set<String> pIDSet = cIdsMap.keySet() ;
    		Iterator<String> iter = pIDSet.iterator() ;
    		while(iter.hasNext()){
    			String pID = iter.next() ;
    			Category[] cats = catReader.getChildrenCats(CatTypes.CAT_PUSHREGION.typeID(), Integer.parseInt(pID)) ;
    			String[] cIDs = cIdsMap.get(pID).split(";") ;
    			if(cIDs.length == cats.length){
    				idslList.add(Integer.parseInt(pID)) ;
    			}else{
    				for(String c : cIDs){
    					idslList.add(Integer.parseInt(c)) ;
    				}
    			}
    		}
    		Iterator<Integer> idIter = idslList.iterator() ;
    		
    		while(idIter.hasNext()){
    			Integer id = idIter.next() ;
    			codeJson.add(catReader.getCat(CatTypes.CAT_PUSHREGION.typeID(),id).getCatCode()) ;
    		}
    	}
        return codeJson.toString() ;
	}
	
	//从json中读int
    private int getInt(JSONObject json, String key) {
        String value = getString(json, key);
        return (StringUtils.isBlank(value)) ? 0 : Integer.parseInt(value);
    }
  //从json中读string
    private String getString(JSONObject json, String key) {
        if (json.containsKey(key))
            return json.getString(key);
        else
            return null;
    }
}
