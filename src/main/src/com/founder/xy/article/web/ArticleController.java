package com.founder.xy.article.web;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.founder.e5.cat.CatManager;
import com.founder.e5.cat.CatReader;
import com.founder.e5.cat.Category;
import com.founder.e5.commons.DateUtils;
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
import com.founder.e5.dom.DocLib;
import com.founder.e5.dom.DocLibReader;
import com.founder.e5.flow.FlowNode;
import com.founder.e5.flow.FlowReader;
import com.founder.e5.flow.Operation;
import com.founder.e5.flow.ProcFlow;
import com.founder.e5.flow.ProcReader;
import com.founder.e5.permission.FlowPermissionReader;
import com.founder.e5.sys.StorageDevice;
import com.founder.e5.sys.StorageDeviceManager;
import com.founder.e5.sys.SysFactory;
import com.founder.e5.web.SysUser;
import com.founder.e5.web.WebUtil;
import com.founder.e5.workspace.ProcHelper;
import com.founder.xy.article.Article;
import com.founder.xy.column.Column;
import com.founder.xy.column.ColumnReader;
import com.founder.xy.commons.CatTypes;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.commons.UrlHelper;
import com.founder.xy.config.Channel;
import com.founder.xy.config.ConfigReader;
import com.founder.xy.set.ExtField;
import com.founder.xy.set.ExtFieldReader;
import com.founder.xy.set.Source;
import com.founder.xy.set.SourceCache;
import com.founder.xy.system.site.SiteUserManager;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 稿件功能
 *
 * @author Gong Lijie
 */
@Controller
@RequestMapping("/xy/article")
public class ArticleController {
    @Autowired
    private ColumnReader colReader;
	@Autowired
    private ExtFieldReader extFieldReader;
	@Autowired
	private SiteUserManager siteUserManager;

    private String[] typeNames = new String[]{"文章", "组图", "视频", "专题", "链接",
    		"多标题", "直播", "活动", "广告","文件","话题", "全景图", "H5", "微信稿件"};
    /**
     * 写稿
     */
    @RequestMapping(value = "Article.do")
    public ModelAndView writeArticle(HttpServletRequest request, HttpServletResponse response,
    		RedirectAttributes attr) throws Exception {
        long docID = WebUtil.getLong(request, "DocIDs", 0);
        int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		int colID= WebUtil.getInt(request, "colID", 0);
		
		int userLibID = LibHelper.getUserExtLibID();
        boolean isNew = (docID == 0); //是否新写稿
		SysUser user = ProcHelper.getUser(request);
		Boolean isAdmin = siteUserManager.isAdmin(user.getUserID());
        Map<String, Object> model = new HashMap<>();
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        Document userDoc = docManager.get(userLibID, user.getUserID());
        try {
			pubAuditParam(model, docManager, request);     //判断发布和审核权限
		} catch (Exception e) {
        	//e.printStackTrace();
		}
        
        Article article;
        if (isNew) {
            article = assembleNewArticle(request, docLibID);
            article.setLiability(userDoc.getString("u_penName"));
        } else {
            Document doc = docManager.get(docLibID, docID);
            if (doc.getInt("a_type") == Article.TYPE_MULTITITLE) {
                int ch = WebUtil.getInt(request, "ch", 0);
                model.put("UUID", WebUtil.get(request, "UUID"));
                model.put("siteID", WebUtil.getInt(request, "siteID", 0));
                model.put("ch", ch);
                model.put("colID", doc.getString("a_columnID"));
                if(colID ==0 ){
                	model.put("currentColID", doc.getString("a_columnID"));
                } else {
                    model.put("currentColID", colID);
                }
                model.put("colName", doc.getString("a_column"));
                model.put("DocLibID", docLibID);
                model.put("DocIDs", docID);
                model.put("content", doc.getString("a_linkTitle"));
                model.put("op", 2);
                model.put("liability", doc.getString("a_liability"));

                return new ModelAndView("/xy/article/Compose", model);
            } else if (doc.getInt("a_type") == Article.TYPE_WXARTICLE) {
        		//return new ModelAndView("xy/article/OriginalWX", model);
        		return new ModelAndView("redirect:OriginalWX.do?DocLibID="+docLibID+"&DocIDs="+docID
        				+"&groupID="+WebUtil.getInt(request, "groupID", 0)
        				+"&siteID="+WebUtil.getInt(request, "siteID", 1));
            }
            article = prepareArticle(model, colID, doc);
        }
        //新建稿件，返回系统设置标题图尺寸
        setTitlePicSizes(model);
		// 取发布渠道。原稿中，不再显示已经发布的渠道
		List<Channel> chs = getChannels(isNew, article.getChannel());
		model.put("userPanName",userDoc.getString("u_penName"));
		model.put("userName", user.getUserName());
		model.put("userId",user.getUserID());
		model.put("colID", colID);
		model.put("channels", chs);
		model.put("channelCount", chs.size());
		model.put("isNew", isNew);
		model.put("siteID", WebUtil.get(request, "siteID"));
		model.put("groupID", WebUtil.getInt(request, "groupID", 0)); //源稿分类
		model.put("UUID", WebUtil.get(request, "UUID"));
		model.put("isAdmin",isAdmin);

		model.put("article", article);
		model.put("ch", WebUtil.get(request, "ch"));
		model.put("sessionID", request.getSession().getId()); // 解决Firefox下flash上传控件session丢失

		model.put("URL", InfoHelper.getConfig("工作台", "木疙瘩域"));

		putTradeParam(model, WebUtil.get(request, "siteID"));
		
		putConfigParam(model);
		
        return new ModelAndView("/xy/article/Article", model);
    }
    
	private void setTitlePicSizes(Map<String, Object> model) {
        // 初始化图片的比例
        String smallRadio = InfoHelper.getConfig("写稿", "标题图片尺寸-小");
        String midRadio = InfoHelper.getConfig("写稿", "标题图片尺寸-中");
        String bigRadio = InfoHelper.getConfig("写稿", "标题图片尺寸-大");
        model.put("smallRadio", smallRadio);
        model.put("midRadio", midRadio);
        model.put("bigRadio", bigRadio);
    }

	/**
     * 根据type获取模板类型列表，根据分组，组成一级树
     * @param siteID 站点
     * @throws Exception
     */
    @RequestMapping(value = "getAllSrc.do")
    public void getAllSour(HttpServletRequest request, HttpServletResponse response,
                             @RequestParam int siteID) throws Exception {

        int docLibID = LibHelper.getSourceLibID();
        String sql = null;
        int catType = CatTypes.CAT_SOURCE.typeID();
        Category[] groups = InfoHelper.getCatGroups(request, catType, siteID);
        if (groups != null && groups.length != 0) {
            JSONArray Allsrcs = new JSONArray();
            DocumentManager docManager = DocumentManagerFactory.getInstance();
            for (Category group : groups) {
                sql = "src_groupID=" + group.getCatID();
                Document[] docs = docManager.find(docLibID, sql, null);
                if (docs != null && docs.length > 0) {
                    JSONObject json = getAllSourJSON(group, docs);
                    Allsrcs.add(json);
                }
            }
        InfoHelper.outputJson(Allsrcs.toString(), response);
        }
    }

    /**
     * 取正文图片（组图稿）
     */
    @RequestMapping(value = "Pics.do")
    public void getPics(HttpServletRequest request, HttpServletResponse response,
            @RequestParam("DocIDs") long docID, @RequestParam("DocLibID") int docLibID)
            throws Exception {

        int attLibID = LibHelper.getAttaLibID(request);
        String sql = "att_articleID=? and att_articleLibID=? and att_type=0 order by att_order";

        DocumentManager docManager = DocumentManagerFactory.getInstance();
        Document[] atts = docManager.find(attLibID, sql, new Object[]{docID, docLibID});

        String result = "[";
        if (atts != null) {
            for (int j = 0; j < atts.length; j++) {
                if (j > 0) result += ",";

                String picInfo = "";
                long picID = atts[j].getLong("att_objID");
                if (picID > 0) {
                    int picLibID = atts[j].getInt("att_objLibID");
                    picInfo = "\",\"pic\":\"" + picLibID + "," + picID;
                }
                result += "{\"path\":\"" + InfoHelper.filter4Json(atts[j].getString("att_path"))
                        + picInfo
                        + "\",\"content\":\"" + InfoHelper.filter4Json(atts[j].getString("att_content"))
                        + "\",\"isIndexed\":\"" + atts[j].getInt("att_indexed") + "\"}";
            }
        }
        result += "]";
        InfoHelper.outputJson(result, response);
    }

    /**
     * 取视频稿
     */
    @RequestMapping(value = "Videos.do")
    public void getVideos(HttpServletRequest request, HttpServletResponse response,
            @RequestParam("DocIDs") long docID, @RequestParam("DocLibID") int docLibID)
            throws Exception {

        int attLibID = LibHelper.getAttaLibID(request);
        String sql = "att_articleID=? and att_articleLibID=? and att_type=1 order by att_order";

        DocumentManager docManager = DocumentManagerFactory.getInstance();
        Document[] atts = docManager.find(attLibID, sql, new Object[]{docID, docLibID});

        String result = "[";
        if (atts != null) {
        	if(atts.length>0){
        		String att_path = atts[0].getString("att_path");
        		if(!att_path.startsWith("http")){
					result = "[{\"url\":\""+att_path+"\",\"urlApp\":\""+att_path+"\",\"videoID\":\"\",\"videoName\":\"\"}]";
					InfoHelper.outputJson(result, response);
					return;
				}
			}

            for (int j = 0; j < atts.length; j++) {
                if (j > 0) result += ",";
                
                String videoID = "";
                String vID = atts[j].getString("att_objID");
                String vLibID = atts[j].getString("att_objLibID");
                if (!StringUtils.isBlank(vID)) {
                	videoID = vLibID + "," + vID;
                }
                Document doc = docManager.get(Integer.parseInt(vLibID),Long.parseLong(vID));
                String videoName = StringUtils.getNotNull(doc.getTopic());
                CatReader catReader = (CatReader)Context.getBean(CatManager.class);
        		Category cat = catReader.getCat(CatTypes.CAT_VIDEO.typeID(), doc.getInt("v_catID"));
        		String catName = "";
        		if(cat!=null) {
        			catName = cat.getCascadeName();
        			if (StringUtils.isBlank(catName)) {
        				catName = StringUtils.getNotNull(cat.getCatName());
        			}
        		}
                
                result += "{\"url\":\"" + InfoHelper.filter4Json(atts[j].getString("att_url"))
                        + "\",\"urlApp\":\"" + InfoHelper.filter4Json(atts[j].getString("att_urlPad"))
                        + "\",\"videoID\":\"" + videoID
                        + "\",\"videoName\":\"" + catName+" > "+ videoName
                        + "\"}";
            }
        }
        result += "]";
        InfoHelper.outputJson(result, response);
    }

    /**
     * 取投票
     */
    @RequestMapping(value = "Votes.do")
    public void getVotes(HttpServletRequest request, HttpServletResponse response,
            @RequestParam("DocIDs") long docID, @RequestParam("DocLibID") int docLibID)
            throws Exception {

        JSONObject json = new JSONObject();
        //获得基本信息
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        Document doc = docManager.get(docLibID, docID);
        if (doc != null) {
            json.put("topic", doc.getString("vote_topic"));
            json.put("author", doc.getAuthors());
            json.put("createDate", DateUtils.format(doc.getCreated()));
            json.put("type", doc.getInt("vote_type") == 0 ? "单选" : "多选");
        }
        InfoHelper.outputJson(json.toString(), response);
    }

    @RequestMapping(value = "getWidgetInfo.do")
	public void getWidgetInfo(HttpServletRequest request, HttpServletResponse response,
	        @RequestParam("DocIDs") long DocIDs, @RequestParam("DocLibID") int docLibID)
	        throws Exception {
	    String result = "[";
	    //获取组图挂件的信息
	    String[] pics = null;
	    try {
	        pics = getwidgetObj(1, docLibID, DocIDs);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    result += "{\"pic\":\"" + pics[0] + "\"}";
	    result += ",{\"picLib\":\"" + pics[1] + "\"}";
	    //获取视频挂件的信息
	    String[] videos = null;
	    try {
	        videos = getwidgetObj(2, docLibID, DocIDs);
	    } catch (Exception e1) {
	        e1.printStackTrace();
	    }
	    result += ",{\"video\":\"" + videos[0] + "\"}";
	    result += ",{\"videoLib\":\"" + videos[1] + "\"}";
	    //获取投票挂件的信息
	    String[] votes = null;
	    try {
	        votes = getwidgetObj(3, docLibID, DocIDs);
	    } catch (Exception e1) {
	        e1.printStackTrace();
	    }
	    result += ",{\"vote\":\"" + votes[0] + "\"}";
	    result += ",{\"voteLib\":\"" + votes[1] + "\"}";
	    //获取附件的信息
	    String widAttas = getwidgetAtta(docLibID, DocIDs);
	    result += ",{\"attachments\":" + widAttas + "}";
	    result += "]";
	    InfoHelper.outputJson(result, response);
	}

	/**
	 * 取相关稿件信息
	 */
	@RequestMapping(value = "Rels.do")
	public void getRels(HttpServletRequest request, HttpServletResponse response,
	        @RequestParam("DocIDs") long docID, @RequestParam("DocLibID") int docLibID)
	        throws Exception {
	
	    int relLibID = LibHelper.getArticleRelLibID();
	    String sql = "a_articleID=? and a_articleLibID=?";
	
	    DocumentManager docManager = DocumentManagerFactory.getInstance();
	    Document[] rels = docManager.find(relLibID, sql, new Object[]{docID, docLibID});
	
	    String result = "[";
	    if (rels != null) {
	        for (int j = 0; j < rels.length; j++) {
	            if (j > 0)
	                result += ",";
	            Document article = docManager.get(rels[j].getDocLibID(), rels[j].getDocID());
	            //根据类型组装标题
	            String title = getComponentTitle(article);
	
	            result += "{\"id\":\"" + article.getInt("a_relID")
	                    + "\",\"title\":\""
	                    + InfoHelper.filter4Json(title)
	                    + "\",\"url\":\""
	                    + InfoHelper.filter4Json(article.getString("a_url"))
	                    + "\",\"urlPad\":\""
	                    + InfoHelper.filter4Json(article.getString("a_urlPad"))
	                    + "\",\"pubTime\":\""
	                    + InfoHelper.filter4Json(article.getString("a_pubTime"))
	                    + "\",\"colCasName\":\""
	                    + InfoHelper.filter4Json(article.getString("a_column"))
	                    + "\",\"lib\":\"" + article.getInt("a_relLibID") + "\"}";
	        }
	    }
	    result += "]";
	
	    InfoHelper.outputJson(result, response);
	}

	/**
	 * 取可能需要关联的稿件信息
	 */
	@RequestMapping(value = "RelsPossible.do")
	public void getRelsPossible(HttpServletRequest request, HttpServletResponse response,
	        @RequestParam("siteID") int siteID, @RequestParam("channel") int channel, @RequestParam("keyword") String keyword)
	        throws Exception {
	
		String result = ArticleServiceHelper.relsPossibleArticles(siteID, channel, keyword);
		
	    InfoHelper.outputJson(result, response);
	}
	
	@RequestMapping(value = "getRelArticle.do")
	public void getRelArticle(HttpServletRequest request, HttpServletResponse response,
	        @RequestParam("DocIDs") String DocIDs, @RequestParam("DocLibID") int docLibID)
	        throws Exception {
	    DocumentManager docManager = DocumentManagerFactory.getInstance();
	    long[] docIDs = StringUtils.getLongArray(DocIDs);
	
	    String result = "[";
	    for (int j = 0; j < docIDs.length; j++) {
	        if (j > 0)
	            result += ",";
	        Document article = docManager.get(docLibID, docIDs[j]);
	        //根据类型组装标题
	        String title = getComponentTitle(article);
	        result += "{\"id\":\"" + docIDs[j]
	                + "\",\"title\":\"" + InfoHelper.filter4Json(title)
	                + "\",\"url\":\"" + InfoHelper.filter4Json(article.getString("a_url"))
	                + "\",\"urlPad\":\"" + InfoHelper.filter4Json(article.getString("a_urlPad"))
	                + "\",\"pubTime\":\"" + formatDatetime(article.getString("a_pubTime"))
	                + "\",\"colCasName\":\"" + InfoHelper.filter4Json(article.getString("a_column"))
	                + "\",\"lib\":\"" + docLibID + "\"}";
	    }
	    result += "]";
	
	    InfoHelper.outputJson(result, response);
	}

	/**
	 * 获得附件的标题，编辑，时间，以及缩略图
	 */
	@RequestMapping("findAttachment.do")
	public void findAttachment(
	        HttpServletRequest request, HttpServletResponse response,
	        @RequestParam("DocIDs") long docID, @RequestParam("DocLibID") int docLibID, int type) throws Exception {
	    JSONObject json = new JSONObject();
	    //获得基本信息
	    DocumentManager docManager = DocumentManagerFactory.getInstance();
	    Document doc = docManager.get(docLibID, docID);
	    if (doc != null) {
	        json.put("topic", doc.getTopic());
	        json.put("author", doc.getAuthors());
	        json.put("createDate", DateUtils.format(doc.getCreated()));
	
	        //先查看是否有标题图，有的话直接用标题图
	        String _imagePath =
	                !"".equals(StringUtils.getNotNull(doc.getString("a_picSmall"))) ? doc.getString("a_picSmall") :
	                        !"".equals(StringUtils.getNotNull(doc.getString("a_picMiddle"))) ? doc.getString(
	                                "a_picMiddle")
	                                : !"".equals(StringUtils.getNotNull(doc.getString("a_picBig"))) ? doc.getString(
	                                "a_picBig") : "";
	        if (_imagePath != null && !"".equals(_imagePath)) {
	            json.put("imgPath", _imagePath);
	        }
	        else {
	            int attLibID = LibHelper.getAttaLibID(request);
	            String sql = "att_articleID=? and att_articleLibID=? and att_type=" + type + " order by att_order";
	            Document[] atts = docManager.find(attLibID, sql, new Object[]{docID, docLibID});
	            if (type == 0) {
	                for (Document _a : atts) {
	                    if (_a != null && _a.getString("att_path") != null && !"".equals(_a.getString("att_path"))) {
	                        json.put("imgPath", _a.getString("att_path"));
	                        break;
	                    }
	                }
	            }
	            else {
	                if (atts != null && atts.length > 0) {
	                    for (Document _a : atts) {
	                        if (_a != null && _a.getString("att_picPath") != null && !"".equals(
	                                _a.getString("att_picPath"))) {
	                            json.put("imgPath", _a.getString("att_picPath"));
	                            break;
	                        }
	                    }
	                }
	
	            }
	        }
	
	    }
	    InfoHelper.outputJson(json.toString(), response);
	
	}

	/**
     * 点击 切换模版时，初始化select框 到数据库中，根据发布渠道和类型查找出相应的模版，并返回到前台
     */
    @RequestMapping(value = "findTemplateData.do")
    public void findTemplateData(HttpServletRequest request, HttpServletResponse response,
            String dc, String articleType) throws Exception {
        JSONObject jsonp = new JSONObject();
        JSONArray jsonArr = new JSONArray();
        JSONObject json = null;
        // 查询出来并组织成json数据源
        if (dc != null && articleType != null) {
            dc = ("pc".equals(dc.toLowerCase()) ? "0" : "1");
            DocumentManager docManager = DocumentManagerFactory.getInstance();
            String sql = " t_channel=? and t_type=? and SYS_DELETEFLAG=0  ";
            //取巧做法：稿件类型+1=模板类型
            Document[] docs = docManager.find(LibHelper.getTemplateLibID(), sql, new Object[]{
                    Integer.parseInt(dc), Integer.parseInt(articleType) + 1});

            if (docs != null && docs.length > 0) {
                for (Document doc : docs) {
                    json = new JSONObject();
                    json.element("code", doc.getDocID());
                    json.element("name", doc.getString("t_name"));
                    jsonArr.add(json);
                }
            }
        }
        jsonp.element("jsonArr", jsonArr);
        InfoHelper.outputJson(jsonp.toString(), response);

    }

    /**
     * 查询所有扩展字段组
     */
    @RequestMapping("findExtOption.do")
    public void findExtOption(HttpServletRequest request, HttpServletResponse response, int siteID)
            throws Exception {
        // 获得所有分组的扩展字段

        Category[] cats = InfoHelper.getCatGroups(request, CatTypes.CAT_EXTFIELD.typeID(), siteID);

        JSONObject jsonp = new JSONObject();
        JSONArray jsonArr = new JSONArray();
        JSONObject json = null;
        if (cats != null && cats.length > 0) {
            for (Category cat : cats) {
                json = new JSONObject();
                json.element("code", cat.getCatID());
                json.element("name", cat.getCatName());
                jsonArr.add(json);
            }
        }
        jsonp.element("jsonArr", jsonArr);
        InfoHelper.outputJson(jsonp.toString(), response);
    }

    /**
     * 获取关键词
     */
    @RequestMapping("findKeyword.do")
    public void findKeyword(HttpServletRequest request, HttpServletResponse response, String title,
            String content) throws Exception {
        String result = ArticleServiceHelper.extractKeywords(content);
        InfoHelper.outputText(result, response);
    }

	/**
     * 获取摘要
     */
    @RequestMapping("findSummary.do")
    public void findSummary(HttpServletRequest request, HttpServletResponse response, String title,
            String content) throws Exception {
        // 从后台获得配置的url
        String result = ArticleServiceHelper.extractSummary(content);
        InfoHelper.outputText(result, response);
    }

    /**
	 * 切换扩展字段
	 */
	@RequestMapping("changeExtOption.do")
	public void changeExtOption(HttpServletRequest request, HttpServletResponse response,
	        Integer extGroupId) throws Exception {
	    JSONObject jsonp = new JSONObject();
	    // 从缓存当中获得groupid相对应的set
	
	    String tenantCode = InfoHelper.getTenantCode(request);
	    int docLibID = LibHelper.getLibID(DocTypes.EXTFIELD.typeID(), tenantCode);
	
	    Set<ExtField> extFieldSet = extFieldReader.getFields(docLibID, extGroupId);
	    jsonp.element("jsonArr", extFieldSet);
	    InfoHelper.outputJson(jsonp.toString(), response);
	}

	/**
	 * 按栏目取出可选来源，从数据库中读。
	 */
	@RequestMapping(value = "Source.do")
	public void source(HttpServletRequest request, HttpServletResponse response,
	        @RequestParam int colID) throws Exception {
	
	    int colLibID = LibHelper.getColumnLibID(request);
	    Document[] sources = getSources(colLibID, colID);
	
	    String result = "[";
	    if (sources != null) {
	        for (int i = 0; i < sources.length; i++) {
	            if (i > 0)
	                result += ",";
	
	            result += "{\"key\":\"" + sources[i].getDocID() + "\",\"value\":\""
	                    + InfoHelper.filter4Json(sources[i].getString("src_name")) + "\"}";
	        }
	    }
	    result += "]";
	
	    InfoHelper.outputJson(result, response);
	}

	/**
	 * 通过栏目取出来源列表 （从缓存当中）
	 */
	@RequestMapping(value = "SourceByCache.do")
	public void SourceByCache(HttpServletRequest request, HttpServletResponse response, Long colID,
	        String query) throws Exception {
	    // 为了避免获取不到参数
	    if (query == null || query.isEmpty()) {
	        query = request.getParameter("query");
	    }
	    if (colID == null) {
	        colID = Long.parseLong(request.getParameter("colID"));
	    }
	    String tenantCode = InfoHelper.getTenantCode(request);
	    int colLibID = LibHelper.getLibID(DocTypes.COLUMN.typeID(), tenantCode);
	    int srcLibID = LibHelper.getLibID(DocTypes.SOURCE.typeID(), tenantCode);
	
	    int[] groupIDs = getColSourceGroups(colLibID, colID);
	    Set<Source> sourceSet = getSourcesByCache(srcLibID, groupIDs);
	
	    // 创建json对象
	    JSONArray jsona = new JSONArray();
	    JSONObject jsonp = new JSONObject();
	    jsonp.accumulate("query", query);
	
	    JSONObject json = null;
	    // 遍历set
	    if (sourceSet != null && !sourceSet.isEmpty()) {
	        int i = 0;
	        for (Source source : sourceSet) {
	            // 如果包含字段，添加到json当中
	            if (source.getName() != null && !source.getName().isEmpty() && i++ < 20
	                    && source.getName().indexOf(query) != -1) {
	                json = new JSONObject();
	                json.element("data", source.getId());
	                json.element("value", source.getName());
	                json.element("url", source.getUrl());
	                jsona.add(json);
	            }
	        }
	    }
	    jsonp.accumulate("suggestions", jsona);
	    InfoHelper.outputJson(jsonp.toString(), response);
	}

	/**
	 * 上传附件
	 */
	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
	public void uploadFile(HttpServletResponse response, HttpServletRequest request) throws Exception {
	    MultipartResolver resolver = new CommonsMultipartResolver(request.getSession().getServletContext());
	    MultipartHttpServletRequest multipartRequest = resolver.resolveMultipart(request);
	
	    MultipartFile file = multipartRequest.getFile("file");
	    String fileName = file.getOriginalFilename();
	    // 随机文件名
	    fileName = UUID.randomUUID().toString() + fileName.substring(fileName.lastIndexOf("."));
	    //附件存储设备的名称
	    StorageDevice device = InfoHelper.getAttachDevice();
	    //构造存储的路径和文件名，目录为201505/13/，文件名用uuid
	    String savePath = DateUtils.format("yyyyMM/dd/") + fileName;
	
	    //开始存储到存储设备上
	    StorageDeviceManager sdManager = SysFactory.getStorageDeviceManager();
	    InputStream is = file.getInputStream();
	    try {
	        sdManager.write(device, savePath, is);
	    } finally {
	        ResourceMgr.closeQuietly(is);
	    }
	    String attaPath = device.getDeviceName() + ";" + savePath;
	
	    InfoHelper.outputText(attaPath, response);
	}
	
	private Article prepareArticle(Map<String, Object> model, int colID, Document doc) throws E5Exception {
		Article article = new Article(doc);
		
		// 解决反斜杠在前端界面转义的问题
		article.setContent(article.getContent().replaceAll("\\\\","&#92;")) ;
		article.setTopic(article.getTopic().replaceAll("\\\\","\\\\\\\\")) ;
	
		//取链接标题
		setLinkTitle(article, colID);

		//取话题
		setTopics(article);
		
		//APP专题稿需要获得栏目图标和描述
		if (doc.getInt("a_type") == Article.TYPE_SPECIAL && doc.getInt("a_channel") ==2) {
			int sColID = doc.getInt("a_linkID");
			if (sColID>0){
	            DocumentManager docManager = DocumentManagerFactory.getInstance();
				Document column =  docManager.get(LibHelper.getColumnLibID(),sColID);
				model.put("columnIcon",column.getString("col_iconBig"));
			}
		}
		return article;
	}

	//设置稿件的链接标题,稿件属性
	private void setLinkTitle(Article article, int colID) {
		colID = colID <= 0 ? article.getColumnID() : colID;
		if (colID <= 0) return;
		
		String relTable = InfoHelper.getRelTable(article.getDocLibID());
		String SQL =  "select  a_linkTitle, a_attr from " + relTable
				+ " where SYS_DOCUMENTID=? and CLASS_1=?";
	    Object[] params = new Object[]{article.getDocID(),colID};
	    DBSession db = null;
	    IResultSet rs = null;
	    String linkTitle = "" ;
	    int attr = 63 ;
	    try {
	        db = InfoHelper.getDBSession(article.getDocLibID());
	        rs = db.executeQuery(SQL, params);
	        while (rs.next()) {
	        	linkTitle = rs.getString("a_linkTitle");
	        	attr = rs.getInt("a_attr");
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	
	    } finally {
	        ResourceMgr.closeQuietly(rs);
	        ResourceMgr.closeQuietly(db);
	    }
	    article.setLinkTitle(linkTitle);
	    article.setAttr(attr);
	}

	//设置稿件的话题
	private void setTopics(Article article) {
		String SQL =  "select a_topicID, a_topicName from xy_topicrelart where a_articleID=? and a_channel=?";
		Object[] params = new Object[]{article.getDocID(),article.getChannel()};
		DBSession db = null;
		IResultSet rs = null;
//		int topicID = 0 ;
//		String topicName = "" ;
		JSONArray array = new JSONArray();
		try {
			db = InfoHelper.getDBSession(article.getDocLibID());
			rs = db.executeQuery(SQL, params);
			while (rs.next()) {
				JSONObject json = new JSONObject();
				json.put("topicID",rs.getInt("a_topicID"));
				json.put("topicName",rs.getString("a_topicName"));
//				linkTitle = rs.getString("a_linkTitle");
//				topicID = rs.getInt("a_attr");

				array.add(json);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(db);
		}
		article.setTopics(array.toString());
	}

	private JSONObject getAllSourJSON(Category group, Document[] docs) {
	    JSONObject jsonParent = new JSONObject();
	    jsonParent.put("groupID",group.getCatID());
	    jsonParent.put("groupName",group.getCatName());
	    JSONArray jsonSubs = new JSONArray();
	    for(Document doc :docs){
	        JSONObject jsonSub = new JSONObject();
	        jsonSub.put("srcID",doc.getDocID());
	        jsonSub.put("srcName",doc.getString("src_name"));
	        jsonSubs.add(jsonSub);
	    }
	    jsonParent.put("subs",jsonSubs.toString());
	    return  jsonParent;
	}

	/** 取发布渠道 */
	private List<Channel> getChannels(boolean isNew, int channel) {
	    Channel[] allChs = ConfigReader.getChannels();
	
	    if (isNew) return Arrays.asList(allChs);
	
	    List<Channel> chs = new ArrayList<Channel>();
	    for (int i = 0; i < allChs.length; i++) {
	        if (allChs[i] != null && ((int) Math.pow(2, i) & channel) == 0) {
	            chs.add(allChs[i]);
	        }
	    }
	    return chs;
	}
	/** 按栏目取来源 */
	private Document[] getSources(int colLibID, long colID) throws E5Exception {
	    int[] groupIDs = getColSourceGroups(colLibID, colID);
	    if (groupIDs == null || groupIDs.length == 0)
	        return null;
	
	    int docLibID = LibHelper.getSourceLibID();
	    String sql = null;
	    if (groupIDs.length == 1)
	        sql = "src_groupID=" + groupIDs[0];
	    else {
	        sql = "src_groupID in (" + StringUtils.join(groupIDs, ",") + ")";
	    }
	
	    DocumentManager docManager = DocumentManagerFactory.getInstance();
	    Document[] docs = docManager.find(docLibID, sql, null);
	
	    return docs;
	}
	/** 取出栏目的来源组ID */
	private int[] getColSourceGroups(int colLibID, long colID) throws E5Exception {
	    Column col = colReader.get(colLibID, colID);
	    if (col == null) return null;
	
	    String sourceGroups = col.getSourceGroupIDs(); // 栏目的来源组ID，多个
	    int[] groupIDs = StringUtils.getIntArray(sourceGroups);
	        return groupIDs;
	}

	private Set<Source> getSourcesByCache(int srcLibID, int[] groupIDs) throws E5Exception {
	    if (groupIDs == null || groupIDs.length == 0)
	        return null;
	
	    Set<Source> result = new HashSet<>();
	
	    // 从缓存当中获得groupid相对应的set
	    SourceCache cache = (SourceCache) (CacheReader.find(SourceCache.class));
	    for (int groupID : groupIDs) {
	        Set<Source> sourceSet = cache.getSourceSetByGroupId(srcLibID, groupID);
	        result.addAll(sourceSet);
		}
	    return result;
	}
	/** 组织新稿 */
    private Article assembleNewArticle(HttpServletRequest request, int docLibID) throws E5Exception {
        int colID = WebUtil.getInt(request, "colID", 0);// 栏目ID
        int ch = WebUtil.getInt(request, "ch", -1); // 渠道
        int type = WebUtil.getInt(request, "type", 0);// 稿件类型（文章/组图/视频等）

        int colLibID = LibHelper.getColumnLibID(request);

        // 打开写稿界面时就预取稿件ID
        long docID = InfoHelper.getNextDocID(DocTypes.ARTICLE.typeID()); // 提前取稿件ID

        Article article = new Article();
        //article.setAuthor(ProcHelper.getUserName(request));
        article.setEditor(ProcHelper.getUserName(request));
        article.setDocID(docID);
        article.setType(type);
        article.setColumnID(colID);
        article.setDocLibID(docLibID);
        if (ch >= 0)
            article.setChannel((int) Math.pow(2, ch)); //渠道，按位

        Column col = colReader.get(colLibID, colID);
        if (col != null) {
            article.setColumn(col.getCasNames());
            article.setExtFieldGroup(col.getExtFieldGroup());
            article.setExtFieldGroupID(col.getExtFieldGroupID());
        }
        return article;
    }
	/** 写稿时，读系统参数 */
    private void putConfigParam(Map<String, Object> model) {
        int sourceType = "可手填".equals(InfoHelper.getConfig("写稿", "来源填写方式")) ? 1 : 0;
        model.put("sourceType", sourceType);

		model.put("hasKeyword", !StringUtils.isBlank(UrlHelper.keyWordsServiceUrl()));
        model.put("hasSummary", !StringUtils.isBlank(UrlHelper.summaryServiceUrl()));
        model.put("hasSensitive", InfoHelper.sensitiveInArticle());
        model.put("hasIllegal", InfoHelper.illegalInArticle());

        //界面上是否显示“编辑样式”按钮
        boolean canEditStyle = "是".equals(InfoHelper.getConfig("写稿", "启用编辑样式"));
        model.put("canEditStyle", canEditStyle);

		String _videoPluginUrl = InfoHelper.getConfig("视频系统", "视频播放控件地址");
		model.put("videoPlugin", _videoPluginUrl);
    }
    
    /** 获取行业分类的分类类型ID、站点根分类ID 
     * @throws E5Exception */
    private void putTradeParam(Map<String, Object> model, String siteID) throws E5Exception {
    	int tradeCatType =  CatTypes.CAT_ARTICLETRADE.typeID();
    	model.put("tradeCatType", tradeCatType);
    	
		//1. 把当前siteID作为catCode，查找是否存在当前站点作为第一层分类
		CatManager catManager = (CatManager) Context.getBean(CatManager.class);
		Category siteCat = catManager.getCatByCode(tradeCatType, siteID);
		if(siteCat != null){
			model.put("tradeRootIDs", siteCat.getCatID());
		}else{
			model.put("tradeRootIDs", 0);
		}

	}
    
    /** 获取挂件下的附件 */
    private String getwidgetAtta(int docLibID, long docID) throws Exception {
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        int widgetLibID = LibHelper.getWidgetLibID();

        String sql = "w_type = ? "
                + "and w_articleID = ? "
                + "and w_articleLibID = ? ";
        Document[] docs = null;
        docs = docManager.find(widgetLibID, sql, new Object[]{0, docID, docLibID});

        String result = "[";
        if (docs != null) {
            for (int j = 0; j < docs.length; j++) {
                if (j > 0)
                    result += ",";
                result += "{\"path\":\"" + InfoHelper.filter4Json(docs[j].getString("w_path"))
                        + "\",\"content\":\""
                        + InfoHelper.filter4Json(docs[j].getString("w_content"))
                        + "\"}";
            }
        }
        result += "]";
        return result;
    }
    /**
     * 获取挂件
     * @param type     1为组图 2为视频
     * @param docLibID
     * @param docID    稿件ID
     */
    private String[] getwidgetObj(int type, int docLibID, long docID) throws Exception {
        String[] obj = new String[2];
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        int widgetLibID = LibHelper.getWidgetLibID();

        String sql = "w_type = ? "
                + "and w_articleID = ? "
                + "and w_articleLibID = ? ";
        Document[] docs = null;
        docs = docManager.find(widgetLibID, sql, new Object[]{type, docID, docLibID});
        if (docs != null && docs.length > 0) {
            obj[0] = docs[0].getString("w_objID");
            obj[1] = docs[0].getString("w_objLibID");
        } else {
            obj[0] = "";
            obj[1] = "";
        }

        return obj;
    }
    private String formatDatetime(String time) {
    	if (StringUtils.isBlank(time)) return "";

    	return time.substring(0, 19);
    }
   //获取类型+文章标题的标题
    private String getComponentTitle(Document article) {
        int type = article.getInt("a_type");

        String result = "[" + typeNames[type] + "] " + article.getTopic();
        return result;
    }
    
    private void pubAuditParam(Map<String, Object> model, DocumentManager docManager, HttpServletRequest request) throws Exception{
    	FlowPermissionReader fpReader = (FlowPermissionReader)Context.getBean(FlowPermissionReader.class);
    	long docID = WebUtil.getLong(request, "DocIDs", 0);
        int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		int colID= WebUtil.getInt(request, "colID", 0);
		
		FlowReader flowReader = (FlowReader) Context.getBean(FlowReader.class);
		int columnLibID = LibHelper.getColumnLibID();
		Document doc = docManager.get(docLibID, docID);
		try {
			if(colID == 0) colID = doc.getInt("A_COLUMNID");
			if(colID < 0){//源稿库 没必要继续
				model.put("pub_or_audit", 0);
				return;
			}
		} catch (Exception e) {
			model.put("pub_or_audit", 0);
			return;
		}
		Document colDoc = docManager.get(columnLibID, colID);
		FlowNode[] nodes = flowReader.getFlowNodes(colDoc.getInt("COL_FLOW_ID"));
		
		int roleID = ProcHelper.getRoleID(request);
        boolean isNew = (docID == 0); //是否新写稿
        boolean isDone= (doc != null && doc.getInt("A_STATUS") == Article.STATUS_PUB_DONE);//是否已发布
    	int pub_or_audit = 0;   //判断是否有送审或发布权限 0:无权限; 1:送审; 2:发布;
    	DocLibReader docTypeReader = (DocLibReader) Context.getBean(DocLibReader.class);
    	DocLib docLib = docTypeReader.get(docLibID);
    	ProcReader procReader = (ProcReader) Context.getBean(ProcReader.class);
    	int audOpId = 0;
    	int pubOpId = 0;
    	Operation[] operations = procReader.getOperations(docLib.getDocTypeID());
    	for(Operation operation : operations){
    		if(operation.getName().equals("提交")) audOpId = operation.getID();
    		if(operation.getName().equals("发布")) pubOpId = operation.getID();
    	}
    	ProcFlow[] procFlows = null;
    	if (isNew || isDone) { //如果是新稿件，或者已发布稿件，获取流程第一个节点的发布审核权限
    		procFlows = procReader.getProcs(nodes[0].getID());
    	}else{ //如果是修改稿件，获取稿件当前流程节点的发布审核权限
    		procFlows = procReader.getProcs(doc.getInt("sys_currentnode"));
    	}
    	ProcFlow audProc = null;
    	ProcFlow pubProc = null;
    	for(ProcFlow procFlow : procFlows){
    		if(procFlow.getOpID()==audOpId) audProc = procFlow;
    		if(procFlow.getOpID()==pubOpId) pubProc = procFlow;
    	}
		if(audProc!=null && fpReader.hasPermission(roleID, audProc)) pub_or_audit = 1;
		if(pubProc!=null && fpReader.hasPermission(roleID, pubProc)) pub_or_audit = 2;
    	model.put("pub_or_audit", pub_or_audit);
    }

}

