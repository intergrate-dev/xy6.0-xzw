package com.founder.xy.api;

import com.founder.e5.cat.CatManager;
import com.founder.e5.cat.CatReader;
import com.founder.e5.cat.Category;
import com.founder.e5.commons.DomHelper;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.CacheManager;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.xy.article.Article;
import com.founder.xy.column.Column;
import com.founder.xy.column.ColumnManager;
import com.founder.xy.column.ColumnReader;
import com.founder.xy.commons.*;
import com.founder.xy.jpublish.BaseDataCache;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;
import com.founder.xy.system.Tenant;
import com.founder.xy.system.site.Site;
import com.founder.xy.system.site.SiteManager;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 与外网api通讯的基础Api，主要是稿件相关功能
 */
@Service
public class ApiManager {
    @Autowired
    private ColumnReader colReader;
    @Autowired
    private ColumnManager colManager;
    @Autowired
    private SiteManager siteManager;

    public static final int ALIST_COUNT = 20; // 一般列表一次获取的数量
    public static final int CACHE_LENGTH = 200; //长列表依次获取的数量
    /**
     * 获取启动页，放入Redis
     */
    public boolean getMobiles(long appID) throws E5Exception {
    	//读移动App
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        int mobilesLibID = LibHelper.getMobileosID();
        Document doc = docManager.get(mobilesLibID, appID);

        if (null == doc) return false ;  //传入参数错误
        
        JSONObject redisJson = new JSONObject();
        
        getGeneral(doc, redisJson);
        
        getRelArticle(doc.getLong("os_articleID"), redisJson);
        
        int siteID = doc.getInt("os_siteID");
        setSiteConfig(siteID, redisJson);
        
        getParams(redisJson);

        //桓台项目，需要知道话题下面每一项的id，以获得groupId
        getCatCode(redisJson);

        redisJson.put("backGroundPic", getBackGroundPic(appID));

        RedisManager.set(RedisKey.APP_START_KEY + appID, redisJson.toString());
        return true;
    }

    public String getBackGroundPic(long appID) {
        int docLibID = LibHelper.getMobileosID();
        String picUrl = "";
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        try {
            Document doc = docManager.get(docLibID, appID);
            String dir = StringUtils.getNotNull(doc.getString("os_dir")) + "/";
            if(doc != null){
                picUrl = StringUtils.getNotNull(doc.getString("os_picBackGround"));
                //判断是否新上传了文件，若有新上传的文件，需检查资源目录
                if (!StringUtils.isBlank(picUrl)) {
                    picUrl = dir + picUrl.substring(picUrl.lastIndexOf("/") + 1);
                }
            }
        } catch (E5Exception e) {
            e.printStackTrace();
        }
        return picUrl;
    }
    
	/**
	 * 读站点参数，目前用于网站评论
	 */
	public boolean siteConf(int siteID) {
		if (siteID <= 0) siteID = 1;
		
		int siteLibID = LibHelper.getLibID(DocTypes.SITE.typeID(), Tenant.DEFAULTCODE);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			Document doc = docManager.get(siteLibID, siteID);
			String config = doc.getString("site_config");
			
			RedisManager.set(RedisKey.SITE_CONF_KEY + siteID, config);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public String getAppInfo() {
        return InfoHelper.getConfig("互动", "app版本信息");
    }

    /**
     * 获取分类，放入Redis
     */
    public boolean getCats(String code, int siteID) throws E5Exception {
        JSONObject json = null;
        JSONArray redisJsonArr = new JSONArray();

        int catTypeID = Enum.valueOf(CatTypes.class, "CAT_" + code).typeID();

        CatReader reader = (CatReader) Context.getBean("CatManager");
        // 取出站点根分类
        int rootID = -1;
        Category root = reader.getCatByCode(catTypeID, String.valueOf(siteID));
        if (root != null) {
            rootID = root.getCatID();
        } else {
            rootID = 0;
        }

        Category[] catArr = reader.getSubCats(catTypeID, rootID);
        if (null != catArr) {
            for (Category cat : catArr) {
                json = new JSONObject();
                json.put("catID", cat.getCatID()); // 分类ID
                json.put("catName", StringUtils.getNotNull(cat.getCatName())); // 分类名
                json.put("catCode", StringUtils.getNotNull(cat.getCatCode()));
                json.put("clickCount",cat.getPubLevel());
                // 若有子分类（如地区），加子分类数据
                addCatChildren(json, catTypeID, cat, "");

                redisJsonArr.add(json);
            }
        }
        String key = RedisManager.getCatKeyBySite(siteID, code);
        RedisManager.setTimeless(key, redisJsonArr.toString());
        return true;
    }

    /**
     * 获取我的分类
     */
    public String getMyCats(String code, int siteID, String keywords) throws E5Exception {
        JSONObject json = null;
        JSONArray jsonArr = new JSONArray();
        int catTypeID = Enum.valueOf(CatTypes.class, "CAT_" + code).typeID();

        CatReader reader = (CatReader) Context.getBean("CatManager");
        // 取出站点根分类
        int rootID = -1;
        Category root = reader.getCatByCode(catTypeID, String.valueOf(siteID));
        if (root != null) {
            rootID = root.getCatID();
        } else if (siteID == 1) {
            rootID = 0; // 兼容已有app：若是站点1，且没有“默认站点1”根分类，则读全部的分类
        } else {
            return null; // 传入参数错误
        }

        Category[] catArr = reader.getSubCats(catTypeID, rootID);
        if (null != catArr) {
            for (Category cat : catArr) {
                json = new JSONObject();
                json.put("catID", cat.getCatID()); // 分类ID
                json.put("catName", StringUtils.getNotNull(cat.getCatName())); // 分类名
                if(keywords.contains(String.valueOf(cat.getCatID()))){
                	json.put("catFlag", "true");//已订阅
                }else{
                	json.put("catFlag", "false");//未订阅
                }
                json.put("catCode", StringUtils.getNotNull(cat.getCatCode()));
                json.put("clickCount",cat.getPubLevel());
                // 若有子分类（如地区），加子分类数据
                addCatChildren(json, catTypeID, cat, keywords);

                jsonArr.add(json);
            }
        }
        return jsonArr.toString();
    }
    
    /**
     * 获取栏目的子孙栏目列表，放入Redis
     */
    public boolean getColumnsAll(int siteID, int colLibID, long colID)
            throws E5Exception {
        if (siteID <= 0)
            siteID = 1; // 若没有siteID，则当做默认站点

        // 先判断站点ID是否存在，避免用无效站点ID对缓存攻击
        int siteLibID = LibHelper.getLibIDByOtherLib(DocTypes.SITE.typeID(),colLibID);
        if (!siteExist(siteLibID, siteID))
            return false;

        List<Column> subList = getSubColumns(siteID, colLibID, colID);
        
        // 栏目最后修改时间
        long version = getColumnVersion(subList);
        // 子栏目列表
        JSONArray jsonArr = new JSONArray();
        if (subList != null && subList.size() > 0)
            jsonArr = getColumnsAllJson(siteID, colLibID, subList);

        JSONObject redisJson = new JSONObject();
        redisJson.put("version", version);
        redisJson.put("columns", jsonArr);

        String key = RedisManager.getKeyBySite(RedisKey.APP_COLLISTALL_KEY, siteID) + colID;
        RedisManager.set(key, redisJson.toString());
        return true;
    }

    /**
     * 通过父栏目ID获取所有的子栏目信息，放入Redis
     */
    public boolean getColumns(int siteID, int colLibID, long colID)
            throws E5Exception {
        if (siteID <= 0) siteID = 1; // 若没有siteID，则当做默认站点
        
        // 先判断站点ID是否存在，避免用无效站点ID对缓存攻击
        int siteLibID = LibHelper.getLibIDByOtherLib(DocTypes.SITE.typeID(), colLibID);
        if (!siteExist(siteLibID, siteID))
            return false;
        
        // List<Column> subList = (colID == 0) ? colReader.getRoot(siteID, colLibID, 1) : colReader.getSub(colLibID, colID);
        //分布式 无法使用Reader，所以直接从数据库中取对象
        List<Column> subList = getSubColumns(siteID, colLibID, colID);
        
        long version = getColumnVersion(subList);// 栏目最后修改时间
        
        // 子栏目列表
        JSONArray jsonArr = new JSONArray();
        JSONArray topArr = new JSONArray(); //置顶的栏目
        if (subList != null) {
            for (Column column : subList) {
                if (this.isNotForbidden(column)) {
                    JSONObject inJson = colCommonFields(column);

                    long parentColumnID = column.getId();
                    int subColumnCount = getSubColumnCount(siteID, colLibID, parentColumnID);
                    boolean hasSubColunm = false;
                    if(subColumnCount>0){
                        hasSubColunm = true;
                    }
                    inJson.put("hasSubColunm",hasSubColunm);

                    long rssCount = getColSubscribeCount(colLibID, column.getId());// 栏目订阅数
                    inJson.put("rssCount", rssCount);
                    //inJson.put("artCount", getArtCount(column.getId())); //文章数

                    jsonArr.add(inJson);
                    //置顶栏目
                    if (column.getAppFixed() == 1) {
                    	topArr.add(inJson);
                    }
                }
            }
        }

        JSONObject redisJson = new JSONObject();
        redisJson.put("version", version);
        redisJson.put("columns", jsonArr);
        redisJson.put("topColumns", topArr); //置顶栏目

        // siteID=1时，app.collist.<colID>
        // siteID>1时，app.collist.siteN.<colID>
        String key = RedisManager.getKeyBySite(RedisKey.APP_COLLIST_KEY, siteID) + colID;

        RedisManager.set(key, redisJson.toString());
        return true;
    }

    private int getSubColumnCount(int siteID, int colLibID, long parentColumnID) {
        int subColumnCount = 0;
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        try {
            String sql = "";
            boolean needForbidden = "是".equals(InfoHelper.getConfig("互动", "是否过滤禁用栏目"));
            if (needForbidden) {
                sql = "col_parentID=? AND col_siteID=? AND SYS_DELETEFLAG=0 and col_channel=1 and col_status=0 order by col_displayOrder asc";
            }else{
                sql = "col_parentID=? AND col_siteID=? AND SYS_DELETEFLAG=0 and col_channel=1 order by col_displayOrder asc";
            }
            Document[] docs = docManager.find(colLibID,sql,new Object[]{parentColumnID,siteID});
            subColumnCount = docs.length;
        } catch (E5Exception e) {
            e.printStackTrace();
        }

        return subColumnCount;
    }

    /**
     * 获取一个栏目
     */
    public boolean getColumn(int siteID, long colID) throws E5Exception {
        if (siteID <= 0)
            siteID = 1; // 若没有siteID，则当做默认站点

        Column col = colReader.get(LibHelper.getColumnLibID(), colID);

        JSONObject redisJson = colCommonFields(col);

        // put稿件数
        int artCount = this.getArtCount(colID);
        redisJson.put("artCount", artCount);

        RedisManager.set(RedisKey.APP_COL_KEY + colID, redisJson.toString());
        return true;
    }

        /*
    public String myColumnSubscribed(int authorID) throws Exception {
        int docLibID = LibHelper.getLibID(DocTypes.SUBSCRIBE.typeID(),
                                          Tenant.DEFAULTCODE);
        int colLibID = LibHelper.getLibID(DocTypes.COLUMN.typeID(),
                                          Tenant.DEFAULTCODE);

        // 查订阅表，找出我订阅的栏目的ID
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        Document[] colIDs = docManager.find(docLibID,
                                            "SYS_AUTHORID=? and sub_type=1", new Object[]{authorID},
                                            new String[]{"sub_topicID"});*//*
        List<Long> columnIDs=initMyColumnIDs(authorID);

        int count = 3;
        JSONArray jsonArr = new JSONArray();
        for (long columnID : columnIDs) {
            // 得到栏目信息
            JSONObject columnInfo = getColumnJson(colLibID, columnID);
            // 得到栏目下的稿件信息
            JSONArray articles = readArticlesByCol(colLibID, columnID, count);

            JSONObject one = new JSONObject();
            one.put("column", columnInfo);
            one.put("list", articles);

            jsonArr.add(one);
        }

        JSONObject rtnJson = new JSONObject();
        rtnJson.put("list", jsonArr);

        return rtnJson.toString();
    }*/

    /**
     * 订阅栏目查找 app用、订阅栏目分三层，根据根栏目，按栏目名查找第三级栏目
     */
    public String searchSubcribeCols(int siteID, String key, long colID)
            throws E5Exception {
        JSONArray result = new JSONArray();
        
        int colLibID = LibHelper.getLibID(DocTypes.COLUMN.typeID(), Tenant.DEFAULTCODE);
        try {
            // 先查出二级栏目
            List<Column> subs = colReader.getSub(colLibID, colID);

            // 根据二级栏目查三级栏目
            if (subs != null) {
            	for (Column sub : subs) {
            		List<Column> grandsons = colReader.getSub(colLibID, sub.getId());
	                if (grandsons != null) {
	                	for (Column grandson : grandsons) {
		                    // 根据关键字查找
		                    if (isNotForbidden(grandson) && grandson.getName().contains(key)) {
	                            result.add(colCommonFields(grandson));
		                    }
						}
	                }
	            }
           }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    //读用户订阅的栏目ID
    public boolean getSubColIDs(long userID) {
	    String key = RedisKey.MY_COLUMN_KEY;
	    String records = RedisManager.hget(key, userID);
	    if (records == null) {
	        DocumentManager docManager = DocumentManagerFactory.getInstance();
	        int docLibID = LibHelper.getLibID(DocTypes.SUBSCRIBE.typeID(),
	                                          Tenant.DEFAULTCODE);
	        try {
				Document[] docs = docManager.find(docLibID, "SYS_AUTHORID=? and sub_type=1",
						new Object[] {userID});
				String value = "";
				if (docs.length > 0) {
					for (int i = 0; i < docs.length; i++) {
						if (i > 0)
							value += ",";
						value += docs[i].getLong("sub_topicID");
					}
				}
				RedisManager.hset(key, userID, value);
			} catch (E5Exception e) {
				e.printStackTrace();
			}
	    }
	    return true;
	}

	/**
	 * 读所有站点的所有栏目信息，用于与第三方同步的场合。
	 * 无缓存。返回XML。
	 */
	public String getNodeTree() throws E5Exception {
	    org.dom4j.Document document = DocumentHelper.createDocument();
	    Element root = document.addElement("nodeTree");
	    // 获取所有站点数据
	    int siteLibID = LibHelper.getLibID(DocTypes.SITE.typeID(),
	                                       Tenant.DEFAULTCODE);
	    List<Site> siteList = siteManager.getSites(siteLibID);
	    for (int i = 0; i < siteList.size(); i++) {
	        // 创建站点节点元素
	        Site site = (Site) siteList.get(i);
	        Element siteEle = root.addElement("site");
	        siteEle.addAttribute("id", site.getId() + "");
	        siteEle.addAttribute("name", site.getName());
	        siteEle.addAttribute("type", "site");
	        siteEle.addAttribute("contentmatch", "");
	        siteEle.addAttribute("url", "");
	        // 获取站点下的所有栏目数据
	        int colLibID = LibHelper.getColumnLibID();
            Document [] docs=colManager.getRoot(site.getId(), colLibID,0);
	        List<Column> WebsubList = colReader.getRoot(site.getId(), colLibID,
	                                                    0);
	        WebsubList = this.isNotForbidden(WebsubList);
	        addChildrenNode(siteEle, colLibID, WebsubList, 0);
	        List<Column> APPsubList = colReader.getRoot(site.getId(), colLibID,
	                                                    1);
	
	        APPsubList = this.isNotForbidden(APPsubList);
	        addChildrenNode(siteEle, colLibID, APPsubList, 0);
	    }
	    return document.asXML();
	}

	// 获取栏目订阅数
	public long getColSubscribeCount(int docLibID, long id) {
	    long count = 0;
	    if (RedisManager.hget(RedisKey.NIS_EVENT_SUBSCRIBE_COLUMN, id) != null) {
	
	        count = Long.parseLong(RedisManager.hget(
	                RedisKey.NIS_EVENT_SUBSCRIBE_COLUMN, id));
	        if (count < 0) {
	            count = 0;
	        }
	    } else {
	        DocumentManager docManager = DocumentManagerFactory.getInstance();
	        Document doc = null;
	        try {
	            doc = docManager.get(docLibID, id);
	        } catch (E5Exception e) {
	            e.printStackTrace();
	        }
	        if (doc == null)
	            return 0;
	        count = doc.getLong("col_rssCount");// redis里改为保存实际订阅数
	        if (count < 0)
	            count = 0;
	        RedisManager.hset(RedisKey.NIS_EVENT_SUBSCRIBE_COLUMN, id,
	                          String.valueOf(count));
	    }
	    return count;
	}

	/*private String getQaLastTime(int userId) {
	    String qaTableName = getTableName(DocTypes.QA.typeID());
	    String result = null;
	    String sql = "SELECT " +
	            "qa.SYS_CREATED publishTime " +
	            "FROM " + qaTableName +
	            " qa WHERE qa.SYS_AUTHORID=? order by qa.SYS_CREATED desc";
	    Object[] params = new Object[]{userId};
	    DBSession conn = null;
	    IResultSet rs = null;
	    try {
	        conn = Context.getDBSession(LibHelper.getArticleLibID());
	        sql = conn.getDialect().getLimitString(sql, 0, 1);
	        rs = conn.executeQuery(sql, params);
	        while (rs.next()) {
	            result = rs.getString("publishTime");
	        }
	    } catch (SQLException | E5Exception e) {
	        log.error("-------------------" + "getQaLastTime - userId:" + userId);
	        e.printStackTrace();
	    } finally {
	        ResourceMgr.closeQuietly(rs);
	        ResourceMgr.closeQuietly(conn);
	    }
	
	    return result;
	}*/
	
	public boolean message(int page, int siteID) {
	    JSONObject json = new JSONObject();
	    JSONArray array = new JSONArray();
	    JSONObject tempJson;
	    json.put("code", 0);
	    DBSession conn = null;
		IResultSet rs = null;
	    try {
	    	conn = Context.getDBSession();
	    	StringBuilder sql = new StringBuilder();
	    	sql.append("SELECT SYS_DOCUMENTID,SYS_CREATED,a_content,a_attachments FROM ");
	    	sql.append(LibHelper.getLibTable(DocTypes.MESSAGE.typeID(), Tenant.DEFAULTCODE)) ;
	    	sql.append(" WHERE a_siteID=? ORDER BY SYS_CREATED DESC");
	    	String sqlStr = conn.getDialect().getLimitString(sql.toString(), page * ALIST_COUNT , ALIST_COUNT);
	    	rs = conn.executeQuery(sqlStr,new Object[]{siteID});
	    	while(rs.next()){
	    		tempJson = new JSONObject();
	    		tempJson.put("content", StringUtils.getNotNull(rs.getString("a_content")));
	    		tempJson.put("attachments", StringUtils.getNotNull(rs.getString("a_attachments")));
	    		tempJson.put("time", InfoHelper.formatDate(rs, "SYS_CREATED"));
	    		array.add(tempJson);
	    	}
	        json.put("list", array);
	        String key = RedisManager.getKeyBySite(RedisKey.APP_MESSAGE_LIST_KEY, siteID);
	        RedisManager.set(key + page, json.toString());
		    return true;
	    }catch (Exception e) {
	        e.printStackTrace();
	        return false ;
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}
	}

	private void getParams(JSONObject redisJson) {
		String rootUrl = InfoHelper.getConfig("互动", "外网资源地址");
		//redisJson.put("shareJumpUrl", UrlHelper.getShareJumpUrl(rootUrl));
		redisJson.put("articleShare", UrlHelper.getArticleShareUrl(rootUrl));
		redisJson.put("picShare", UrlHelper.getPicShareUrl(rootUrl));
		redisJson.put("videoShare", UrlHelper.getVideoShareUrl(rootUrl));
        redisJson.put("specialShare", UrlHelper.getSpecialShareUrl(rootUrl));
        redisJson.put("liveShare", UrlHelper.getLiveShareUrl(rootUrl));
        redisJson.put("webLiveShare", UrlHelper.getWebLiveShareUrl(rootUrl));
        redisJson.put("activityShare", UrlHelper.getActivityShareUrl(rootUrl));
        redisJson.put("subjectShare", UrlHelper.getSubjectShareUrl(rootUrl));
        redisJson.put("qaShare", UrlHelper.getQAShareUrl(rootUrl));
        
        //对新版本，不需要以上分享页地址，提供rootUrl后app统一调用触屏版即可
        redisJson.put("rootUrl", rootUrl);
        redisJson.put("amucUrl", rootUrl + "/amuc/");
        
        redisJson.put("ssoUrl", InfoHelper.getConfig("互动", "SSO访问地址"));
        redisJson.put("weatherUrl", InfoHelper.getConfig("互动", "天气服务访问地址"));
        redisJson.put("mallUrl", InfoHelper.getConfig("互动", "积分商城访问地址"));
        
        String paperPeriods = InfoHelper.getConfig("数字报", "非会员阅读天数");
        redisJson.put("paperPeriods", paperPeriods == null
                || paperPeriods == "" ? "1" : paperPeriods);
        redisJson.put("phoneNo", InfoHelper.getConfig("数字报", "报卡联系电话"));
        redisJson.put("canRead", InfoHelper.getConfig("数字报", "不登陆读报"));
        redisJson.put("paperUrl", InfoHelper.getConfig("数字报", "数字报接口地址"));
        
        redisJson.put("version", InfoHelper.getConfig("互动", "app版本信息"));
    }
	
    private List<Column> getSubColumns(int siteID, int colLibID, long colID)
			throws E5Exception {
		List<Column> subList = null;
	    if (colID == -1) {
            subList = getSubColumn(colLibID, colID,siteID);
            if (subList != null && subList.size() > 0) {
                long firstRootColumnID = subList.get(0).getId();
                subList = getSubColumn(colLibID, firstRootColumnID,siteID);
            }
	    }  else {
	        subList = getSubColumn(colLibID, colID,siteID);
	    }
		return subList;
	}

	private List<Column> getSubColumn(int colLibID, long colID,int siteID) {
	    List<Column> columns = new ArrayList<>();
	
	    DocumentManager docManager = DocumentManagerFactory.getInstance();
	    try {
	        Document[] docs = docManager.find(colLibID,
	                                          "col_parentID=? AND col_siteID=? AND SYS_DELETEFLAG=0 and col_channel=1 order by col_displayOrder asc",
	                                          new Object[]{colID,siteID});
	        for (Document doc : docs) {
	            Column column = new Column(doc);
	            columns.add(column);
	        }
	    } catch (E5Exception e) {
	        e.printStackTrace();
	    }
	
	    return columns;
	}

	private JSONArray getColumnsAllJson(
            int siteID, int colLibID,
            List<Column> subList) throws E5Exception {
        JSONArray jsonArr = new JSONArray();
        for (Column column : subList) {
            if (this.isNotForbidden(column)) {
                JSONObject inJson = colCommonFields(column);
                long _colID = column.getId();
                List<Column> _subList = (_colID == 0) ? colReader.getRoot(siteID,colLibID, 1) : colReader.getSub(colLibID,_colID);

                if (_subList != null && _subList.size() > 0) {
                    _subList = this.isNotForbidden(_subList);
                    inJson.put("columns", getColumnsAllJson(siteID, colLibID, _subList));
                }

                jsonArr.add(inJson);
            }
        }
        return jsonArr;
    }

    private long getColumnVersion(List<Column> subList) {
        if (subList == null || subList.size() == 0)
            return 0;

        List<Long> verList = new ArrayList<>();
        for (Column col : subList) {
            if (this.isNotForbidden(col)) {
                verList.add(col.getLastmodified());
            }
            
        }
        if(verList.size()<=0){
            return 0;
        }
        return Collections.max(verList);

    }

    // 子分类
    private void addCatChildren(JSONObject json, int catTypeID, Category cat, String keywords)
            throws E5Exception {
        if (cat.getChildCount() > 0) {
            CatReader reader = (CatReader) Context.getBean("CatManager");
            Category[] sonCats = reader.getSubCats(catTypeID, cat.getCatID());
            if (null != sonCats) {
                JSONArray sonJsonArr = new JSONArray();
                for (Category sonCat : sonCats) {
                    JSONObject sonJson = new JSONObject();
                    sonJson.put("catID", sonCat.getCatID()); // 分类ID
                    sonJson.put("catName",
                                StringUtils.getNotNull(sonCat.getCatName())); // 分类名
                    if(keywords.length()>0){
                    	if(keywords.contains(String.valueOf(sonCat.getCatID()))){
                        	sonJson.put("catFlag", "true");//已订阅
                        }else{
                        	sonJson.put("catFlag", "false");//未订阅
                        }
                    }
                    sonJson.put("clickCount",sonCat.getPubLevel());
                    sonJsonArr.add(sonJson);
                }
                json.put("children", sonJsonArr);
            }
        }
    }

    // 检查网站ID是否存在。若能从缓存中读到网站发布路径，则认为存在。
    private boolean siteExist(int siteLibID, int siteID) {
        BaseDataCache cache = (BaseDataCache) CacheManager
                .find(BaseDataCache.class);
        try {
            String root = cache.getSiteWebRootByID(siteLibID, siteID);
            return (root != null);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 设置栏目共通字段
     */
    private JSONObject colCommonFields(Column bean) {
        JSONObject inJson = new JSONObject();
        if (bean == null) return inJson;

        //栏目是否删除
        inJson.put("isdelete", bean.getIsdelete());

        inJson.put("columnId", bean.getId()); // 栏目ID
        inJson.put("columnName", StringUtils.getNotNull(bean.getName())); // 栏目名称
        inJson.put("topCount", bean.getTopCount()); // 栏目的头条个数
        inJson.put("phoneIcon", StringUtils.getNotNull(bean.getIconSmall())); // 栏目小图
        inJson.put("padIcon", StringUtils.getNotNull(bean.getIconBig())); // 栏目大图
        inJson.put("orderId", bean.getOrder()); // 顺序
        inJson.put("keyword", StringUtils.getNotNull(bean.getKeyword()));
        String description = bean.getDescription()
        		.replaceAll("<[a-zA-Z]+[1-9]?[^><]*>", "")
        		.replaceAll("</[a-zA-Z]+[1-9]?>", "");
        inJson.put("description", StringUtils.getNotNull(description)); // 栏目描述
        inJson.put("linkUrl", StringUtils.getNotNull(bean.getLinkUrl())); // 外链地址
        inJson.put("isForbidden", bean.isForbidden());
        inJson.put("casNames", StringUtils.getNotNull(bean.getCasNames())); // 栏目路径以~分割
        int colLibID = LibHelper.getColumnLibID();
        long rssCount = getColSubscribeCount(colLibID, bean.getId());
        inJson.put("rssCount", rssCount); //栏目订阅数
        inJson.put("appFixed", bean.getAppFixed()); //固定位置？
        inJson.put("appShow", bean.getAppShow()); //默认显示？
        inJson.put("synXCX", bean.getSynXCX()); //同步至小程序

        // 栏目类型（资讯类、影视图片类、专题类、便民服务类）
        inJson.put("columnType", InfoHelper.getCatCode(CatTypes.CAT_COLUMN.typeID(), bean.getAppTypeID()));

        // 栏目样式
        inJson.put("columnStyle", InfoHelper.getCatCode(CatTypes.CAT_COLUMNSTYLE.typeID(), bean.getAppStyleID()));

        inJson.put("code", StringUtils.getNotNull(bean.getCode()));
        return inJson;
    }

    /**
     * 按照设置的发布目录加上文件名合成完整的url
     */
    private void setStartUpPic(
            JSONObject redisJson, String dir,
            String picSmall, String picUrlMiddle, String picUrlBig) {

        // 从存储设备路径中取出图片地址
        if (picSmall.contains("/")) {
            picSmall = picSmall.substring(picSmall.lastIndexOf("/") + 1);
        }
        if (picUrlMiddle.contains("/")) {
            picUrlMiddle = picUrlMiddle
                    .substring(picUrlMiddle.lastIndexOf("/") + 1);
        }
        if (picUrlBig.contains("/")) {
            picUrlBig = picUrlBig.substring(picUrlBig.lastIndexOf("/") + 1);
        }

        // 若没有设置中图、小图，则与大图同地址
        if (StringUtils.isBlank(picSmall))
            picSmall = picUrlBig;
        if (StringUtils.isBlank(picUrlMiddle))
            picUrlMiddle = picUrlBig;

        if (!StringUtils.isBlank(picSmall)) {
            redisJson.put("picSmall", dir + picSmall);
        }
        if (!StringUtils.isBlank(picUrlMiddle)) {
            redisJson.put("picUrlMiddle", dir + picUrlMiddle);
        }
        if (!StringUtils.isBlank(picUrlBig)) {
            redisJson.put("picUrlBig", dir + picUrlBig);
        }
    }

    /**
     * 根据parentId查找并添加子节点元素
     *
     * @param ele        节点元素
     * @param //nodeList 栏目列表数据
     * @param parentId   父栏目id
     * @throws E5Exception
     */
    private void addChildrenNode(
            Element ele, int colLibID,
            List<Column> subList, long parentId) throws E5Exception {
        if (subList != null) {
            for (Column column : subList) {
                if (this.isNotForbidden(column)) {
                    if (column.getParentID() == parentId) {
                        Element subEle = ele.addElement("node");
                        subEle.addAttribute("id", column.getId() + "");
                        Date d = new Date(column.getLastmodified());
                        SimpleDateFormat sdf = new SimpleDateFormat(
                                "yyyy-MM-dd hh:mm:ss");
                        String s = sdf.format(d);
                        subEle.addAttribute("time", s);
                        subEle.addAttribute("parentid", parentId + "");
                        subEle.addAttribute("name", column.getName());
                        if (parentId == 0) {
                            subEle.addAttribute("type", "channel");
                        } else {
                            subEle.addAttribute("type", "column");
                        }
                        subEle.addAttribute("template", column.getTemplate() + "");
                        subEle.addAttribute("displayorder", column.getOrder() + "");
                        subEle.addAttribute("order", column.getOrder() + "");
                        subEle.addAttribute("url", column.getLinkUrl() + "");
                        subEle.addAttribute("specialnode", "");
                        subEle.addAttribute("attr", "");
                        // 递归添加子节点
                        addChildrenNode(subEle, colLibID,
                                        colReader.getSub(colLibID, column.getId()),
                                        column.getId());
                    }
                }
            }
        }
    }

    // 查询稿件数
    private int getArtCount(long colID) {
        IResultSet rs = null;
        DBSession conn = null;
        int count = 0;
        String sql = "select count(*) from xy_articleapp xya where xya.a_columnID=? and xya.a_status=1";
        try {
            conn = Context.getDBSession();
            rs = conn.executeQuery(sql, new Object[]{colID});
            if (rs.next())
                count = rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(rs);
            ResourceMgr.closeQuietly(conn);
        }
        return count;
    }

    private boolean isNotForbidden(Column column) {
        boolean needForbidden = "是".equals(InfoHelper.getConfig("互动", "是否过滤禁用栏目"));
        if (!needForbidden) {
            return true;
        }
        if (needForbidden && !column.isForbidden()) {
            return true;
        }
        return false;
    }

    private List<Column> isNotForbidden(List<Column> columns) {
        boolean needForbidden = "是".equals(InfoHelper.getConfig("互动", "是否过滤禁用栏目"));
        if (!needForbidden) {
            return columns;
        }
        List<Column> columnsNew = new ArrayList<Column>();
        for (Column column : columns) {
            if (isNotForbidden(column)) {
                columnsNew.add(column);
            }
        }
        return columnsNew;
    }
    
    /**
     * 处理启动页一些基本字段
     * @param doc
     * @param redisJson
     */
    private void getGeneral(Document doc,JSONObject redisJson){
    	int siteID = doc.getInt("os_siteID");
        redisJson.put("siteID", siteID);
        String dir = StringUtils.getNotNull(doc.getString("os_dir")) + "/";
        String templateZip = StringUtils.getNotNull(doc.getString("os_templateZip"));
        if (templateZip.contains("/")) {
            templateZip = templateZip.substring(templateZip.lastIndexOf("/") + 1);
            redisJson.put("template", dir + templateZip);
        }
        
        int pageType = doc.getInt("os_pageType");
        redisJson.put("pageType", pageType); // picLarge
        redisJson.put("pageTime", doc.getInt("os_pageTime")); // 单位是秒
        if (1 == pageType) {  // pageType=1时才有这个属性
            redisJson.put("pageUrl",StringUtils.getNotNull(doc.getString("os_pageUrl"))); 
        } else if (0 == pageType) {
            redisJson.put("picName",StringUtils.getNotNull(doc.getString("os_picName")));
            // 按照设置的发布目录加上文件名合成完整的url
            setStartUpPic(redisJson, dir, StringUtils.getNotNull(doc.getString("os_picSmall")),
                          StringUtils.getNotNull(doc.getString("os_picUrlMiddle")),
                          StringUtils.getNotNull(doc.getString("os_picUrlBig")));
        }
    }
    
    private void getCatCode(JSONObject redisJson){
    	int catTypeID = Enum.valueOf(CatTypes.class, "CAT_QA").typeID();
        CatManager reader = (CatManager) Context.getBean("CatManager");
        if (reader != null) {
            try {
                Category root = reader.getCatByCode(catTypeID, "1");
                if (root != null) {
                    Category[] catArr = reader.getSubCats(catTypeID, root.getCatID());
                    for (Category cat : catArr) {
                        String catCode = cat.getCatCode();
                        if ("5001".equals(catCode) || "5002".equals(catCode))
                            redisJson.put(cat.getCatCode(), cat.getCatID());
                    }
                }
            } catch (E5Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 得到站点的配置项
     */
    private void setSiteConfig(int siteID, JSONObject redisJson) throws E5Exception{
    	siteConf(siteID);
        
    	String siteConfig = RedisManager.get(RedisKey.SITE_CONF_KEY + siteID);
        if (!StringUtils.isBlank(siteConfig)) {
            JSONObject jsonConfig = JsonHelper.getJson(siteConfig);
            redisJson.put("siteConfig", jsonConfig);
        }
    }
    
	/**
     * 启动页处理关联稿件
     * @param os_articleID 关联稿件ID
     * @param redisJson
     * @throws E5Exception
     */
    private void getRelArticle(long os_articleID,JSONObject redisJson) throws E5Exception{
    	DocumentManager docManager = DocumentManagerFactory.getInstance();
    	
        if (os_articleID > 0) {
            Document doc = docManager.get(LibHelper.getArticleAppLibID(),os_articleID);
            if (null != doc) {
                JSONObject inJson = new JSONObject();
                inJson.put("fileId", os_articleID);
                inJson.put("title", doc.getString("SYS_TOPIC"));
                inJson.put("articleType", doc.getInt("a_type"));
                inJson.put("linkID", doc.getLong("a_linkID"));
                // 若是链接稿、广告稿，则直接填写url
                String url;
                int type = doc.getInt("a_type");
                if (type >= Article.TYPE_SPECIAL
                        && type != Article.TYPE_ACTIVITY
                        && type != Article.TYPE_PANORAMA
                        && type != Article.TYPE_FILE) {
                    url = StringUtils.getNotNull(doc.getString("a_urlPad"));
                } else {
                    // 稿件内容url，带当前栏目ID，以便稿件详情页里按当前栏目显示广告信息（如：首页的广告与其它栏目不同）
                    url = UrlHelper.getArticleContentUrl(os_articleID);
                }
                inJson.put("contentUrl", url);
                redisJson.put("article", inJson);
            }
        }
    }

    public String saveEmainSubscribe(int siteID, String name, String email) {
        email = email.trim();
        int count = getEmailSubscribeCount(email);
        JSONObject resultJson = new JSONObject();
        if(count>0){
            resultJson.put("success", false);
            resultJson.put("errorInfo", "此邮箱已订阅，请务重复订阅");
        }else{
            int emailLibId = LibHelper.getLibID(DocTypes.EMAILSUBSCRIBE.typeID(), Tenant.DEFAULTCODE);
            DocumentManager docManager = DocumentManagerFactory.getInstance();
            try {
                Document doc = docManager.newDocument(emailLibId);
                doc.set("a_siteID", siteID);
                doc.set("a_name", name);
                doc.set("a_email", email);

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");//设置日期格式
                Date date = new Date();
                doc.set("a_pubTime", df.format(date));

                int FVID = DomHelper.getFVIDByDocLibID(emailLibId);
                doc.set("sys_folderID", FVID);

                docManager.save(doc);
            } catch (E5Exception e) {
                e.printStackTrace();
                resultJson.put("success", false);
                resultJson.put("errorInfo", "保存数据库失败！");
                return resultJson.toString();
            }
            resultJson.put("success", true);
            resultJson.put("errorInfo", "");
        }
        return resultJson.toString();
    }

    public int getEmailSubscribeCount(String email){
        IResultSet rs = null;
        DBSession conn = null;
        int count = 0;
        String sql = "select count(*) from xy_emailsubscribe a where a.a_email = ? and a.sys_deleteflag = 0";
        try {
            conn = Context.getDBSession();
            rs = conn.executeQuery(sql, new Object[]{email});
            if (rs.next())
                count = rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(rs);
            ResourceMgr.closeQuietly(conn);
        }
        return count;
    }

    public boolean getExternalSystemAuth(int eid) {
        String result = NewExternalSystemAuth.extSystemAuth(eid);
        if(result == null){
            return true;
        }else{
            return false;
        }
    }

    public boolean getColumnsAllEasy(int siteID, long parentID,int colLibID) {
        if (siteID <= 0)
            siteID = 1;//默认站点

        //先判断站点ID是否存在，避免用无效站点ID对缓存攻击
        int siteLibID = LibHelper.getLibIDByOtherLib(DocTypes.SITE.typeID(),colLibID);
        if (!siteExist(siteLibID, siteID))
            return false;

        List<Column> subList = null;
        JSONArray jsonArr = new JSONArray();
        try {
            subList = getSubColumn(colLibID, parentID, siteID,0);
            // 子栏目列表
            if (subList != null && subList.size() > 0) {
                jsonArr = getColumnsAllJson(siteID, colLibID, subList,2);//2代表第三方百格视频接口
            }
        } catch (E5Exception e) {
            e.printStackTrace();
            return false;
        }
        JSONObject redisJson = new JSONObject();
        if(jsonArr.size()>0){
            redisJson.put("success",true);
            redisJson.put("columns", jsonArr);
            String key = RedisKey.WEB_COLLISTALL_KEY + siteID;
            RedisManager.hset(key, parentID, redisJson.toString());
            return true;
        }else{
            return false;
        }

    }

    /**
     * @param colLibID
     * @param parentID
     * @param siteID
     * @param channel 0web栏目 1app栏目
     * @return
     */
    private List<Column> getSubColumn(int colLibID, long parentID, int siteID, int channel) {
        List<Column> columns = new ArrayList<>();

        DocumentManager docManager = DocumentManagerFactory.getInstance();
        try {
            Document[] docs = docManager.find(colLibID,
                    "col_parentID=? AND col_siteID=? AND SYS_DELETEFLAG=0 and col_channel=? order by col_displayOrder asc",
                    new Object[]{parentID,siteID,channel});
            for (Document doc : docs) {
                Column column = new Column(doc);
                columns.add(column);
            }
        } catch (E5Exception e) {
            e.printStackTrace();
        }

        return columns;
    }

    private JSONArray getColumnsAllJson(int siteID, int colLibID, List<Column> subList, int type) throws E5Exception {
        JSONArray jsonArr = new JSONArray();
        for (Column column : subList) {
            long _colID = column.getId();

            JSONObject inJson = new JSONObject();
            inJson.put("columnId", _colID); // 栏目ID
            inJson.put("columnName", StringUtils.getNotNull(column.getName())); // 栏目名称

            List<Column> _subList = (_colID == 0) ? colReader.getRoot(siteID,colLibID, 0) : colReader.getSub(colLibID,_colID);

            if (_subList != null && _subList.size() > 0) {
                inJson.put("columns", getColumnsAllJson(siteID, colLibID, _subList,2));
            }

            jsonArr.add(inJson);
        }
        return jsonArr;
    }
}