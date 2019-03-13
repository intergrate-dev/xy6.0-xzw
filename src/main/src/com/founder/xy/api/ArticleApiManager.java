package com.founder.xy.api;

import com.founder.e5.cat.CatManager;
import com.founder.e5.cat.CatReader;
import com.founder.e5.cat.Category;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.CacheManager;
import com.founder.e5.context.CacheReader;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.xy.api.nis.ActivityApiManager;
import com.founder.xy.api.nis.BaseApiManager;
import com.founder.xy.api.nis.QAApiManager;
import com.founder.xy.api.nis.SubjectApiManager;
import com.founder.xy.article.Article;
import com.founder.xy.article.ArticleInfo;
import com.founder.xy.column.Column;
import com.founder.xy.column.ColumnReader;
import com.founder.xy.commons.*;
import com.founder.xy.jpublish.BaseDataCache;
import com.founder.xy.jpublish.ColParam;
import com.founder.xy.jpublish.template.ComponentFactory;
import com.founder.xy.jpublish.template.component.ArticleListPageComDync;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;
import com.founder.xy.system.Tenant;
import com.founder.xy.system.site.SiteManager;
import com.founder.xy.system.site.SiteRule;
import com.founder.xy.system.site.SiteSolrServerCache;
import com.founder.xy.template.Template;
import com.founder.xy.wx.WeixinAPI;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 与外网api通讯的基础Api，主要是稿件相关功能
 */
@Service
public class ArticleApiManager extends BaseApiManager{
    @Autowired
    private ColumnReader colReader;

    private CatReader catReader;
    private CatManager catManager;

    /**
     * 刷新Redis缓存中的稿件列表组件 取组件实例的发布结果
     *
     * @param coID     当前组件实例ID，模板中要写入。组件里可能指定了查询的栏目ID（可能多个）。
     * @param colLibID 栏目库ID
     * @param colID    当前栏目页的栏目ID，用于组件实例里没指定栏目ID时
     * @param page     翻页页数
     * @return
     */
    public String articleListRefresh(
            int coID, int colLibID, long colID,
            int page, int siteID) {
        ColParam param = new ColParam(colLibID, colID, page);

        String co = RedisManager.hget(RedisKey.CO_KEY, coID);
        if (co != null) {
            ArticleListPageComDync component = (ArticleListPageComDync) ComponentFactory
                    .newComponent(param, co);
            return component.readPageData();
        }
        return null;
    }

    /**
     * 修改solr的tag字段分词<field name="a_tag" type="text_ws" indexed="true"
     * stored="true"/> 得到内容标签列表 访问全文检索服务（全文检索服务中，稿件的内容标签字段设置为多值字段），得到数据
     */
    public String tagArticles(String tag, int siteID, int ch, int page) {
        if (page < 1)
            page = 1;

        int count = ApiManager.ALIST_COUNT;
        int start = (page - 1) * count;

        String qStr = "a_status:\"1\" AND SYS_DELETEFLAG:\"0\" AND";
        tag = tag.trim().replaceAll(" +", " ");
        if (tag.contains(" ")) {
            String[] tagArr = tag.split(" ");
            String sql = "";
            for (String str : tagArr) {
                sql += " OR a_tag:" + str;
            }
            qStr += "(" + sql.substring(4) + ")";
        } else {
            qStr += " a_tag:" + tag;
        }

        List<Long> docIDs = solrQuery(qStr, siteID, ch, start, count,false, true);

        // 根据ID读稿件库，返回json
        return solrResult(docIDs, ch);
    }

    /**
     * 修改solr的a_tradeID字段分词<field name="a_tradeID" type="text_ws" indexed="true"
     * stored="true"/> 得到分类列表 访问全文检索服务（全文检索服务中，稿件的行业分类设置为多值字段），得到数据
     */
    public String tradeArticles(String tradeIDs, int siteID, int ch, int page) {
        if (page < 1)
            page = 1;

        int count = ApiManager.ALIST_COUNT;
        int start = (page - 1) * count;

        String qStr = "a_status:\"1\" AND SYS_DELETEFLAG:\"0\" AND";
        tradeIDs = tradeIDs.trim();
        if (tradeIDs.contains(",")) {
            String[] tradeArr = tradeIDs.split(",");
            String sql = "";
            for (String str : tradeArr) {
                sql += " AND a_tradeID:" + str;
            }
            qStr += "(" + sql.substring(5) + ")";
        } else {
            qStr += " a_tradeID:" + tradeIDs;
        }

        List<Long> docIDs = solrQuery(qStr, siteID, ch, start, count,false, true);

        // 根据ID读稿件库，返回json
        return solrResult(docIDs, ch);
    }
    /**
     * 作者检索
     */
    public String authorArticles(String author, int siteID, int page)
            throws E5Exception {
        if (page < 1)
            page = 1;

        int count = ApiManager.ALIST_COUNT;
        int start = (page - 1) * count;

        int artLibID = getArticleLibID();

        String sql = "SELECT SYS_DOCUMENTID,a_order FROM "
                + LibHelper.getLibTable(artLibID)
                + " WHERE SYS_AUTHORS=? AND a_status=1 ORDER BY a_pubTime DESC";
        Object[] params = new Object[]{author};

        List<ArticleInfo> articleInfos = queryArticleIDs(sql, params, start, count);

        JSONArray jsonArr = listArticles(articleInfos, artLibID, -1);

        return jsonArr.toString();
    }

	/**
     * 通过关键词搜索APP发布库中某个栏目下的稿件
     */
    public String search(
            long colID, String key, int siteID, int start,
            int count) throws E5Exception {
        count = ApiManager.ALIST_COUNT;// 固定个数，避免恶意api调用冲了redis里的数据
        String qStr = "CLASS_1:\"" + colID
                + "\" AND a_status:\"1\" AND SYS_DELETEFLAG:\"0\"";
        if (null != key && !"".equals(key)) {
            qStr += " AND text:\"" + ClientUtils.escapeQueryChars(key)
                    + "\"";
        }

        int ch = 1;
        List<Long> docIDs = solrQuery(qStr, siteID, ch, start, count,true, true);

        // 根据ID读稿件库，返回json
        return solrResult(docIDs, ch);
    }

    /**
     * 通过关键词搜索APP发布库中某个栏目及子孙栏目下的稿件
     */
    public String searchAll(
            int colLibID, long colID, String key, int siteID,
            int start, int count) throws E5Exception {
        count = ApiManager.ALIST_COUNT; // 固定20个，避免恶意api调用冲了redis里的数据

        List<Column> columns = new ArrayList<Column>();
        getSonColumns(columns, colLibID, colID);
        StringBuffer qStrBuffer = new StringBuffer();

        //查询dom_rel_app表会有关联表重复数据，这里如果传了大于零的colID则查询dom_rel_app，否则查articleapp表
        boolean isrel = true;
        // 如果全库查询则无需查询出栏目ID
        if (colID > 0) {
            qStrBuffer.append("(CLASS_1:\"" + colID + "\"");
            for (Column column : columns) {
                if (this.isNotForbidden(column)) {
                    qStrBuffer.append(" OR CLASS_1:\"" + column.getId() + "\"");
                }

            }
            qStrBuffer.append(") AND a_status:\"1\" AND SYS_DELETEFLAG:\"0\"");
        } else {
            qStrBuffer.append("a_status:\"1\" AND SYS_DELETEFLAG:\"0\"");
            isrel = false;
        }
        if (null != key && !"".equals(key)) {
            qStrBuffer.append(" AND text:\""
                                      + ClientUtils.escapeQueryChars(key) + "\"");
        }
        qStrBuffer.append("AND a_type:(0 OR 1 OR 2 OR 4 OR 6 OR 12)");

        int ch = 1;
        List<Long> docIDs = solrQuery(qStrBuffer.toString(), siteID, ch, start,
                                      count,isrel, false);

        // 根据ID读稿件库，返回json
        return solrResult(docIDs, ch);
    }


    /**
     * 通过关键词搜索Web发布库中某个栏目及子孙栏目下的稿件
     */
    public String searchWebArticle(
            int colLibID, long colID, String key, int siteID,
            int start, int count) throws E5Exception {
        count = ApiManager.ALIST_COUNT; // 固定20个，避免恶意api调用冲了redis里的数据

        List<Column> columns = new ArrayList<Column>();
        getSonColumns(columns, colLibID, colID);
        StringBuffer qStrBuffer = new StringBuffer();

        //查询dom_rel_web表会有关联表重复数据，这里如果传了大于零的colID则查询dom_rel_web，否则查article表
        boolean isrel = true;
        // 如果全库查询则无需查询出栏目ID
        if (colID > 0) {
            qStrBuffer.append("(CLASS_1:\"" + colID + "\"");
            for (Column column : columns) {
                if (this.isNotForbidden(column)) {
                    qStrBuffer.append(" OR CLASS_1:\"" + column.getId() + "\"");
                }

            }
            qStrBuffer.append(") AND a_status:\"1\" AND SYS_DELETEFLAG:\"0\"");
        } else {
            qStrBuffer.append("a_status:\"1\" AND SYS_DELETEFLAG:\"0\"");
            isrel = false;
        }
        if (null != key && !"".equals(key)) {
            qStrBuffer.append(" AND text:\""
                    + ClientUtils.escapeQueryChars(key) + "\"");
        }

        int ch = 0;
        List<Long> docIDs = solrQuery(qStrBuffer.toString(), siteID, ch, start,
                count,isrel, false);

        // 根据ID读稿件库，返回json
        return solrResult(docIDs, ch);
    }



    // -------------以下为App准备----------------

    /**
     * 组织一篇稿件的json，放入Redis
     *
     * @param docLibID
     * @param docID
     * @param curColID 稿件的来源栏目ID（如，是从首页栏目进来的）
     * @return
     * @throws E5Exception
     */
    public boolean getArticle(int docLibID, long docID, long curColID)
            throws E5Exception {
        String key = RedisKey.APP_ARTICLE_KEY + docID;
        String value = RedisManager.get(key);

        JSONObject article = null;
        if (value == null) {
            article = ArticleJsonHelper.article(docLibID, docID);
        } else {
            article = JSONObject.fromObject(value);
        }


        setPresentColumn(article,curColID);
        // 设置广告
        if (article != null) {
            setPageAdv(article, curColID);// 加广告
            RedisManager.set(key, article.toString());
            return true;
        } else {
            return false;
        }
    }

    // 稿件详情json中，添加当前栏目名
    private static void setPresentColumn(JSONObject redisJson, long curColID)
            throws E5Exception {
        if (curColID <= 0)
            return;

        ColumnReader colReader = (ColumnReader) Context.getBean("columnReader");
        Column col = colReader.get(LibHelper.getColumnLibID(), curColID);

        if (null != col) {
            redisJson.put("presentColumnName",
                    StringUtils.getNotNull(col.getName())); // 当前栏目名
        }
    }

    /**
     * 获取某个栏目下指定数目的稿件列表，放入Redis的列表中。
     * 列表从左到右是[article0, article1, article2, ...., indexJson]
     */
    public boolean getArticles(int colLibID, long colID, int start,int typeScreen) {
    	//避免并发读，加锁和多做一次判断
        String key = RedisKey.APP_ARTICLELIST_AD_KEY + colID + "." + start;
        if(typeScreen==1)
            key=RedisKey.APP_ARTICLELIST_AD_WX_KEY+ colID + "." + start;
        if (RedisManager.exists(key)) return true;

        int count = ApiManager.CACHE_LENGTH; // 使用固定长度的缓存
        //组织一个Hash，存放每个稿件ID在Redis List里的index <aid, index>
        JSONObject idAndIndex = new JSONObject();
        try {
            int artAppLibID = getArticleAppLibByColLib(colLibID);

            //读关联表，得到正确顺序的稿件ID、稿件位置、稿件是否置顶
            List<ArticleInfo> articleInfos = getArticleIDsByCol(artAppLibID, colID, start, count,typeScreen);

            //根据稿件ID得到稿件的列表
            JSONArray jsonArr = listArticles(articleInfos, artAppLibID, colID);

            //201条字符串列表，先是200条稿件，最后面放的是索引JSON对象
            String[] articleListInRedisList = new String[jsonArr.size() + 1];
            for (int i = 0; i < jsonArr.size(); i++) {
                //新增读取主栏目ColumnStyle
                String stringArticle = jsonArr.getString(i);
                JSONObject jsonArticle = JSONObject.fromObject(stringArticle);
                String mainColID = jsonArticle.getString("colID");
                //通过主栏目ID获取columnStyle
                String columnStyle = getColumnStyle(mainColID);
                jsonArticle.put("columnStyle",columnStyle);

            	articleListInRedisList[i] = jsonArticle.toString();
				idAndIndex.put(articleInfos.get(i).getDocID(), i);
            }
            articleListInRedisList[jsonArr.size()] = idAndIndex.toString();

            //加入Redis
			//RedisManager.set(key, redisJson.toString());

            System.out.println("1---key:"+key);
            System.out.println("2---getArticles.do接口数据："+Arrays.toString(articleListInRedisList));
            System.out.println("3---时间："+new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(new Date()));

            RedisManager.resetLongList(key, articleListInRedisList);

	        return true;
		} catch (E5Exception e) {
			e.printStackTrace();
	        return false;
		}
    }

    private String getColumnStyle(String mainColID) {
        if(StringUtils.isBlank(mainColID)){
            return "";
        }
        int colLibID = LibHelper.getLibID(DocTypes.COLUMN.typeID(), Tenant.DEFAULTCODE);
        long id = Long.valueOf(mainColID);
        Column column = null;
        try {
            column = colReader.get(colLibID, id);
        } catch (E5Exception e) {
            e.printStackTrace();
        }
        if (column != null) {
            String columnStyle = getCatCode(CatTypes.CAT_COLUMNSTYLE.typeID(), column.getAppStyleID());
            return columnStyle;
        }
        return "";
    }

    /**
     * 获取一个栏目下所有子栏目的最新稿件一览，指定每个栏目读取数目，放入Redis type：0-每个子栏目取3条最新稿件，1-取10条，2-取20条
     */
    public boolean getArticlesInSubColumns(int siteID, long colID, int type)
            throws E5Exception {
        if (colID <= 0)
            return false; // 必须指定父栏目

        // 先判断站点ID是否存在，避免用无效站点ID对缓存攻击
        if (siteID <= 0)
            siteID = 1; // 若没有siteID，则当做默认站点
        int siteLibID = LibHelper.getLibID(DocTypes.SITE.typeID(),
                                           Tenant.DEFAULTCODE);
        if (!siteExist(siteLibID, siteID))
            return false;

        // 读出子栏目
        int colLibID = LibHelper.getLibID(DocTypes.COLUMN.typeID(),
                                          Tenant.DEFAULTCODE);
        List<Column> subList = colReader.getSub(colLibID, colID);
        if (subList == null)
            return false;

        if (type != 0 && type != 1)
            type = 2;
        int count = (type == 0) ? getSubscribeArticleCount(siteID)
        		: (type == 1) ? 10 : 20; // 稿件列表条数

        JSONArray columnList = new JSONArray();
        // 对每个子栏目，读稿件列表
        if (subList != null) {
            for (Column column : subList) {
                if (this.isNotForbidden(column)) {
                    JSONObject columnInfo = colCommonFields(column);
                    JSONArray articles = readArticlesByCol(column.getId(), count);

                    JSONObject one = new JSONObject();
                    one.put("column", columnInfo);
                    one.put("list", articles);

                    columnList.add(one);
                }
            }
        }

        JSONObject result = new JSONObject();
        result.put("list", columnList);

        String key = RedisKey.APP_ARTICLELIST_SUBCOLUMN_KEY + colID + "." + type;
        RedisManager.set(key, result.toString());
        return true;
    }

    /**
     * 获取最热稿件列表，放入Redis
     */
    public boolean getArticlesHot(int colLibID, int siteID, int type)
            throws E5Exception {
        int artAppLibID = getArticleAppLibByColLib(colLibID);

        return getHotArticlesApp(artAppLibID, siteID, type);
    }

	/**
     * 记者稿件
     */
    public boolean getAuthorArticles(int id, int start, int count)
            throws E5Exception {
        count = ApiManager.ALIST_COUNT; // 固定个数，避免恶意调用冲了redis里的数据

        String tenantCode = Tenant.DEFAULTCODE;
        List<DocLib> articleLibs = LibHelper.getLibs(DocTypes.ARTICLE.typeID(),
                                                     tenantCode);
        int artAppLibID = articleLibs.get(1).getDocLibID();

        String sql = "select SYS_DOCUMENTID,a_order from "
                + LibHelper.getLibTable(artAppLibID)
                + " where SYS_AUTHORID=? and a_status=1 order by a_pubTime desc";
        Object[] params = new Object[]{id};

        List<ArticleInfo> articleInfos = queryArticleIDs(sql, params, start, count);

        JSONArray jsonArr = listArticles(articleInfos, artAppLibID, -1);

        // 数据放入redis
        RedisManager.set(RedisKey.APP_AUTHOR_ARTICLES_KEY + id + "." + start,
                         jsonArr.toString());
        return true;
    }

    /**
     * 我订阅的栏目和稿件列表
     */
    public String subscribeView(int siteID, int userID, String columnId, String device) throws E5Exception {
        JSONArray jsonArr = new JSONArray();

        int colLibID = LibHelper.getLibID(DocTypes.COLUMN.typeID(),Tenant.DEFAULTCODE);

        //筛选出当前栏目下的订阅子栏目
        List<Column> mycolumnids = null ;
        String records = null ;
        if(userID<1){
        	getSubColIDsByDevice(device);
            records = RedisManager.hget(RedisKey.MY_COLUMN_KEY, device);
        }else{
        	getSubColIDs(userID);
            records = RedisManager.hget(RedisKey.MY_COLUMN_KEY, userID);
        }
        mycolumnids = getMyColumns(records, Long.parseLong(columnId), siteID);

        //取出系统配置的订阅栏目的稿件显示个数
//        int count = getSubscribeArticleCount(siteID);

        for (int i = mycolumnids.size() - 1; i >= 0; i--) {
        	Column column = mycolumnids.get(i);

            // 得到栏目信息
            JSONObject columnInfo = colCommonFields(column);

            //当栏目删除时不再添加
            if (1 == columnInfo.getInt("isdelete"))
                continue;
            if (columnInfo.getBoolean("isForbidden"))
                continue;
            long rssCount = getColSubscribeCount(colLibID, column.getId());
            columnInfo.put("rssCount", rssCount);

            //栏目下稿件最新发布时间，因为发布稿件后不会更新colReader,所以单独来写
            String colPubTime = getColPubTime(colLibID, column.getId());
            columnInfo.put("colPubTime", colPubTime);

//            JSONArray articles = readArticlesByCol(column.getId(), count);

            JSONObject one = new JSONObject();
            one.put("column", columnInfo);
            one.put("list", "[]");

            jsonArr.add(one);
        }
        return jsonArr.toString();
    }

    private String getColPubTime(int colLibID, long id) {
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        Document doc = null;
        try {
            doc = docManager.get(colLibID, id);
        } catch (E5Exception e) {
            e.printStackTrace();
        }
        if(doc==null){
            return "";
        }else{
            return String.valueOf(doc.get("col_pubTime")==null?"":doc.get("col_pubTime"));
        }
    }

    /**
     * 推荐稿件列表
     */
    public String  getHistoryRecList(String data, int start, int count, int siteID) {
        String tenantCode = Tenant.DEFAULTCODE;
        List<DocLib> articleLibs = LibHelper.getLibs(DocTypes.ARTICLE.typeID(),
                tenantCode);
        int artAppLibID = articleLibs.get(1).getDocLibID(); // App发布库，是第二个稿件库

        String result = null;
        try {
                JSONArray recArticles =(JSONArray)getRecData(data,start,count,"推荐历史");
                if(recArticles!=null&&recArticles.size()>0){
                    List<ArticleInfo> articleInfos=getArticleInfoByID(recArticles,artAppLibID);
                    JSONArray jsonArr = listArticles(articleInfos, artAppLibID, -1);
                    JSONObject jsonObj=new JSONObject();
                    jsonObj.put("list",jsonArr);
                    result=jsonObj.toString();
                }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }
    /**
     * 推荐稿件列表
     */
    public String getRecommendList(String data, int start, int count, int siteID) {

        String tenantCode = Tenant.DEFAULTCODE;
        List<DocLib> articleLibs = LibHelper.getLibs(DocTypes.ARTICLE.typeID(),
                tenantCode);
        int artAppLibID = articleLibs.get(1).getDocLibID(); // App发布库，是第二个稿件库
        String result = null;
        try {
            JSONObject obj = JSONObject.fromObject(data);
            String userId = obj.getString("uid");

            if (start == 0) {
                JSONArray recArticles = (JSONArray)getRecData(data,start,count,"文章推荐");
                if (recArticles == null)
                    return recomendListWithHot(data, count, siteID, artAppLibID);
                else
                    result = recomendList(recArticles, data, count, siteID, userId, artAppLibID);
            } else {
                result = recomendListWithCache(data, start, count, siteID, userId, artAppLibID);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            result = recomendListWithHot(data, count, siteID, artAppLibID);
        }
        return result;
    }

    /**
     * 文章关键词
     */
    public boolean getRecKeywords(String data){
        JSONObject obj = JSONObject.fromObject(data);
        String key=RedisKey.APP_ARTICLE_WORDS_KEY+obj.getString("aid");
        try{
            JSONObject jsonObject=(JSONObject)getRecData(data,0,0,"文章关键词");
            JSONObject jsonObj=new JSONObject();
            jsonObj.put("fileId",obj.getString("aid"));
            jsonObj.put("Data",jsonObject);

            RedisManager.set(key,jsonObj.toString());
        }catch(Exception e){
            e.printStackTrace();
        }
        return true;
    }
    /**
	 * 得到栏目推荐模块信息
	 */
	public boolean getColumnModules(long colID, int siteID) {
	    String key = RedisKey.APP_MODULE_LIST + colID;

	    JSONArray jsonArr = new JSONArray();
	    DocumentManager docManager = DocumentManagerFactory.getInstance();
	    try {
	        Document[] docs = docManager.find(LibHelper.getLibID(DocTypes.COLMODULE.typeID(), Tenant.DEFAULTCODE),
	                                          "cm_columnID=? order by cm_position", new Object[]{colID});
	        for (Document doc : docs) {
	            JSONObject jsonObj = new JSONObject();

	            jsonObj.put("type", doc.getInt("cm_type"));
	            jsonObj.put("moduleID", doc.getLong("SYS_DOCUMENTID"));
	            jsonObj.put("name", doc.getString("cm_name"));
	            jsonObj.put("count", doc.getInt("cm_count"));
	            jsonObj.put("position", doc.getInt("cm_position") - 1); //设置时从1开始，app处理时从0开始
	            jsonObj.put("mark", doc.getString("cm_mark"));
	            jsonObj.put("showMore", doc.getInt("cm_showMore"));

	            //对应栏目信息
            	JSONObject jsonCol = new JSONObject();
	            long targetColID = doc.getLong("cm_targetColumnID");
	            if (targetColID > 0) {
	            	jsonCol.put("id", targetColID);

	            	int colLibID = LibHelper.getLibID(DocTypes.COLUMN.typeID(), Tenant.DEFAULTCODE);
		            Column column = colReader.get(colLibID, targetColID);
		            if (column != null) {
		            	String catCode = getCatCode(CatTypes.CAT_COLUMNSTYLE.typeID(), column.getAppStyleID());
		            	jsonCol.put("icon", column.getIconSmall());
		            	jsonCol.put("style", catCode);
		            }
	            }
	            jsonObj.put("targetColumn", jsonCol);

	            jsonArr.add(jsonObj);
	        }
	        RedisManager.set(key, jsonArr.toString());

	        return true;
	    } catch (E5Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}

	//读用户订阅的栏目ID
	public boolean getSubColIDs(long userID) {
	    String key = RedisKey.MY_COLUMN_KEY;
	    String records = RedisManager.hget(key, userID);
	    if (records == null) {
	        DocumentManager docManager = DocumentManagerFactory.getInstance();
	        int docLibID = LibHelper.getLibID(DocTypes.SUBSCRIBE.typeID(),Tenant.DEFAULTCODE);
	        try {
	        	String sql = "SYS_AUTHORID=? and SYS_DELETEFLAG=0 and sub_type=1" ;
				Document[] docs = docManager.find(docLibID, sql,new Object[]{userID});
				String value = "";
				if (docs.length > 0) {
				    for (int i = 0; i < docs.length; i++) {
				        if (i == 0) {
				            value = "" + docs[i].getLong("sub_topicID");
				        } else {
				            value = value + "," + docs[i].getLong("sub_topicID");
				        }
				    }
				}
				RedisManager.hset(key, userID, value);
			} catch (E5Exception e) {
				e.printStackTrace();
			}
	    }
	    return true;
	}

	//读游客用户订阅的栏目ID
	public boolean getSubColIDsByDevice(String device) {
	    String key = RedisKey.MY_COLUMN_KEY;
	    String records = RedisManager.hget(key, device);
	    if (records == null) {
	        DocumentManager docManager = DocumentManagerFactory.getInstance();
	        int docLibID = LibHelper.getLibID(DocTypes.SUBSCRIBE.typeID(),Tenant.DEFAULTCODE);
	        try {
	        	String sql = "sub_device=? and SYS_DELETEFLAG=0 and sub_type=1" ;
				Document[] docs = docManager.find(docLibID, sql,new Object[]{device});
				String value = "";
				if (docs.length > 0) {
				    for (int i = 0; i < docs.length; i++) {
				        if (i == 0) {
				            value = "" + docs[i].getLong("sub_topicID");
				        } else {
				            value = value + "," + docs[i].getLong("sub_topicID");
				        }
				    }
				}
				RedisManager.hset(key, device, value);
			} catch (E5Exception e) {
				e.printStackTrace();
			}
	    }
	    return true;
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

	/**
	 * 推荐模块项列表
	 */
	public boolean getModuleView(long cmID) {
	    String key = RedisKey.APP_MODULEITEM_LIST + cmID;
        DocumentManager docManager = DocumentManagerFactory.getInstance();

	    try {
	        int cmLibID = LibHelper.getLibID(DocTypes.COLMODULE.typeID(), Tenant.DEFAULTCODE);
	        DocLib cmiLibID = LibHelper.getLib(DocTypes.COLMODULEITEM.typeID(), Tenant.DEFAULTCODE);

	        //读推荐模块，得到限制的个数、类型、对应栏目ID
	        Document moduleDoc = docManager.get(cmLibID, cmID);

	        int count = moduleDoc.getInt("cm_count");
	        int type = moduleDoc.getInt("cm_type");
	        long columnId = moduleDoc.getLong("cm_targetColumnID");

		    JSONObject jsonObj = new JSONObject();
	        jsonObj.put("type", type);

	    	//根据栏目得到siteID，以便后面区分
	    	int siteID = getSiteIDByColumn(moduleDoc.getLong("cm_columnID"));

	    	//读推荐模块项
	    	String sql = "select cmi_targetID from " + cmiLibID.getDocLibTable()
	    			+ " where cmi_moduleID=? and SYS_DELETEFLAG=0 order by cmi_order desc";
			List<Long> ids = queryOneField(cmiLibID, sql, new Object[]{cmID}, 0, count);
	        if (ids.size() == 0) {
	        	//无手工设置的推荐项
	            String list = getModuleItemsAuto(siteID, columnId, type, count);
	            jsonObj.put("list", list);
	        } else {
	        	//有手工设置的推荐项
	            JSONArray jsonArr = getModuleItems(siteID, columnId, type, count, ids);
	            jsonObj.put("list", jsonArr.toString());
	        }

            //推荐项在Redis里保存短的时间，以便显示最新的栏目里的内容，以及更新计数。则外网api就不更新计数了。
            RedisManager.setOneMinute(key, jsonObj.toString());
	    } catch (E5Exception e) {
	        e.printStackTrace();
	    }
	    return true;
	}

	private int getArticleLibID() {
		String tenantCode = Tenant.DEFAULTCODE;
	    List<DocLib> articleLibs = LibHelper.getLibs(DocTypes.ARTICLE.typeID(),
	                                                 tenantCode);
	    int artLibID = articleLibs.get(0).getDocLibID();
		return artLibID;
	}

	private int getArticleAppLibByColLib(int colLibID) throws E5Exception {
		String tenantCode = LibHelper.getTenantCodeByLib(colLibID);
	    List<DocLib> articleLibs = LibHelper.getLibs(DocTypes.ARTICLE.typeID(),
	                                                 tenantCode);
	    int artAppLibID = articleLibs.get(1).getDocLibID();
		return artAppLibID;
	}

	/**
	 * 取App稿件库的热点稿件
	 */
	private boolean getHotArticlesApp(int artAppLibID, int siteID, int type)
			throws E5Exception {
		String key = RedisManager.getKeyBySite(RedisKey.APP_ARTICLELIST_HOT_KEY, siteID) + type;
	    if (RedisManager.exists(key)) {
	    	return true;
	    }

	    List<Long> docIDs = getArticleHotDocIDs(siteID, type, artAppLibID);
	    List<ArticleInfo> articleInfos = packDocIDs(docIDs);

	    // 按ID列表读稿件详细内容，组织成json数据
	    JSONArray jsonArr = listArticles(articleInfos, artAppLibID, -1);

	    JSONObject redisJson = new JSONObject();
	    redisJson.put("list", jsonArr);

	    RedisManager.set(key, redisJson.toString());
	    return true;
	}

	/** 取出系统配置的订阅栏目的稿件显示个数，默认为3 */
	private int getSubscribeArticleCount(int siteID) {
		try {
			SiteManager siteManager = (SiteManager)Context.getBean("siteManager");
			return siteManager.getSubscribeArticleCount(siteID);
		} catch (Exception e) {
			e.printStackTrace();
			return 3;
		}
	}

	private List<Column> getGrandsonColumns(int siteID, int colLibID, long colID) {
	    List<Column> list = new ArrayList<>();

	    if (siteID <= 0)
	        siteID = 1; // 若没有siteID，则当做默认站点
	    // 先判断站点ID是否存在，避免用无效站点ID对缓存攻击
	    int siteLibID = LibHelper.getLibIDByOtherLib(DocTypes.SITE.typeID(),
	                                                 colLibID);
	    if (!siteExist(siteLibID, siteID))
	        return list;

	    try {
			List<Column> subCols = colReader.getSub(colLibID, colID);
			if (subCols != null) {
				for (Column sub : subCols) {
					List<Column> grandSonCols = colReader.getSub(colLibID, sub.getId());
					if (grandSonCols != null)
				    for (Column grandSon : grandSonCols) {
				        list.add(grandSon);
				    }
				}
			}
		} catch (E5Exception e) {
			e.printStackTrace();
		}
	    return list;

	}

    private List<Column> getMyColumns(String records, long columnID, int siteID) {
        List<Column> myColumns = new ArrayList<>();

        if (records != null && !"".equals(records)) {
            int colLibID = LibHelper.getLibID(DocTypes.COLUMN.typeID(),Tenant.DEFAULTCODE);
//            List<Column> columns = getGrandsonColumns(siteID, colLibID, columnID);

            //筛选出当前栏目下的订阅子栏目
            String[] ids = records.split(",");
            for(int i=0;i<ids.length;i++){
                Column column = null;
                try {
                    column = colReader.get(colLibID, Long.parseLong(ids[i]));
                    if(column!=null&&column.getIsdelete()!=1){
                        myColumns.add(column);
                    }
                } catch (E5Exception e) {
                    e.printStackTrace();
                }
            }
//            for (Column column : columns) {
////                if (ArrayUtils.contains(ids, String.valueOf(column.getId()))) {
//                }
//			}
        }
        return myColumns;
    }

    private List<ArticleInfo> getArticleIDsByCol(int artAppLibID, long colID,
    		int start, int count,int typeScreen) throws E5Exception {
        // 取出稿件ID列表
        String tableName = InfoHelper.getRelTable(artAppLibID);// 取出栏目稿件关联表的表名
        String sql = "SELECT SYS_DOCUMENTID,a_order FROM " + tableName
                + " WHERE CLASS_1=? and a_status=? ORDER BY a_order";
        Object[] params = new Object[]{colID, Article.STATUS_PUB_DONE};

        if(typeScreen==1){
            sql = "SELECT SYS_DOCUMENTID,a_order FROM " + tableName
                    + " WHERE CLASS_1=? and a_status=? and a_type in (0,1,2,3) ORDER BY a_order";
        }

        List<ArticleInfo> articleInfos = queryArticleIDs(sql, params, start, count);
        // 调整固定位置：把稿件ID列表按固定位置进行调整
        changePositions(colID, start, articleInfos, tableName);

        return articleInfos;
    }

    /**
     * 读出热点新闻稿件的ID，按点击数排序。取固定个数ALIST_COUNT
     */
    private List<Long> getArticleHotDocIDs(int siteID, int type, int artAppLibID)
            throws E5Exception {
        Calendar cal = Calendar.getInstance();
        String day_to = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime());
        cal.add(Calendar.HOUR, getArticleHotPeriod(type));
        String day_from = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime());

        DocumentManager docManager = DocumentManagerFactory.getInstance();
        String condition = "(a_pubTime BETWEEN ? AND ?) AND a_status=? AND a_siteID=? "
        		+ "order by (a_countClick+a_countShareClick+a_countClickInitial) desc";
        Document[] docs = docManager.find(artAppLibID, condition,
                      new Object[]{day_from, day_to, Article.STATUS_PUB_DONE, siteID});

        List<Long> docIDs = new ArrayList<>();
        for (int j = 0; j < docs.length && j < ApiManager.ALIST_COUNT; j++) {
            docIDs.add(docs[j].getDocID());
		}
        return docIDs;
    }

    private int getArticleHotPeriod(int type) {
        int period = -24; // 24小时内的热点新闻
        if (1 == type)
            period *= 2; // 48小时内的
        else if (2 == type)
            period *= 3; // 最近3天的
        else if (3 == type)
            period *= 7; // 最近7天的
        /*
         * else if(4 == type) period *= 15; else if(5 == type) period *= 30;
		 * else if(6 == type) period *= 91; else if(7 == type) period *= 182;
		 * else if(8 == type) period *= 365;
		 */
        return period;
    }

    /**
	 * 为“我订阅的栏目”读最新稿件列表 先从redis缓存中读该栏目的稿件列表，若有则直接读最前面的几条。 若没有才读库。
	 */
	private JSONArray readArticlesByCol(long colID, int count) {
	    String key = RedisKey.APP_ARTICLELIST_AD_KEY + colID + ".0";
		if (!RedisManager.exists(key)) {
			getArticles(LibHelper.getColumnLibID(), colID, 0,0);
		}

	    return getSomeFromRedisList(key, count);
	}

    /**
     * 按搜索语句进行solr搜索
     * @param qStr 搜索语句
     * @param siteID 站点ID
     * @param ch 渠道
     * @param start 起始位置
     * @param count 数量
     * @param isrel 是否搜索关联表
     * @oaram filterArticleType 只查看文章稿件和组图稿件
     * @return
     */
    private List<Long> solrQuery(
            String qStr, int siteID, int ch, int start,
            int count, boolean isrel, boolean filterArticleType) {
        ModifiableSolrParams params = new ModifiableSolrParams();
        params.set("q", "a_siteID:"+"\""+ siteID+"\" AND "+qStr);
        params.set("sort", "a_order asc");
        params.set("start", start);
        params.set("rows", count);

        SiteSolrServerCache siteSolrServerCache = (SiteSolrServerCache) (CacheReader
                .find(SiteSolrServerCache.class));
        siteSolrServerCache.getSolrAppServerBySiteID(siteID);
        SolrServer server;
        if (isrel) {
            server = (ch == 1) ? siteSolrServerCache
                    .getSolrAppServerBySiteID(siteID) : siteSolrServerCache
                    .getSolrServerBySiteID(siteID);
        } else {
            server = (ch == 1) ? siteSolrServerCache
                    .getSolrArticleAppServerBySiteID(siteID) : siteSolrServerCache
                    .getSolrArticleServerBySiteID(siteID);
        }
        QueryResponse response = null;
        try {
            response = server.query(params);
        } catch (SolrServerException e) {
            e.printStackTrace();
        }

        SolrDocumentList list = response.getResults();
        System.out.print(list.size());

        List<Long> docIDs = new ArrayList<>();
        for (SolrDocument doc : list) {
            int type = (Integer) doc.getFieldValue("a_type");
            if (filterArticleType&&type > 1)
                continue; // 只返回文章稿
            long fileId = Long.parseLong(String.valueOf(doc.getFieldValue("SYS_DOCUMENTID")));
            if (!docIDs.contains(fileId))
                docIDs.add(fileId);
        }

        return docIDs;
    }

    // 组装solr查询的结果，返回稿件列表json
    private String solrResult(List<Long> docIDs, int ch) {
        String tenantCode = Tenant.DEFAULTCODE;

        List<DocLib> articleLibs = LibHelper.getLibs(DocTypes.ARTICLE.typeID(),
                                                     tenantCode);
        int articleLibID = articleLibs.get(ch).getDocLibID();

        List<ArticleInfo> articleInfos = packDocIDs(docIDs);

        JSONArray jsonArr = listArticles(articleInfos, articleLibID, 0);

        return jsonArr.toString();
    }

    // 根据条件查找稿件ID：用于栏目稿件列表和记者稿件列表
    private List<ArticleInfo> queryArticleIDs(String sql, Object[] params, int start, int count)
    		throws E5Exception {
        List<ArticleInfo> docIDs = new ArrayList<>();

        DBSession conn = null;
        IResultSet rs = null;
        try {
            conn = Context.getDBSession();
            sql = conn.getDialect().getLimitString(sql, start, count);

            rs = conn.executeQuery(sql, params);
            while (rs.next()) {
                ArticleInfo info = new ArticleInfo();
                info.setDocID(rs.getLong("SYS_DOCUMENTID"));
                
                BigDecimal highBit = new BigDecimal(-100000000L);
                String order = rs.getString("a_order");
                BigDecimal OrderBD = new BigDecimal(order);
                Boolean isTop = OrderBD.compareTo(highBit) <= 0 ? true : false;
                info.setTop(isTop);
                
                docIDs.add(info);
            }
        } catch (Exception e) {
            throw new E5Exception(e);
        } finally {
            ResourceMgr.closeQuietly(rs);
            ResourceMgr.closeQuietly(conn);
        }
        return docIDs;
    }

    /**
     * 固定位置稿件
     * 若start=0，则查出稿件关联表里a_position>0的稿件，调用articleOne生成稿件的json，
     * 并插入到其a_position指定的位置（注意这是从1开始的，1应该插到第0个位置）
     */
    private void changePositions(
            long colID, int start, List<ArticleInfo> articleInfos,
            String tableName) throws E5Exception {
        if (start == 0 && articleInfos.size() > 0) {
            // 读出有固定位置的稿件的ID
        	List<ArticleInfo> fixedIDs = queryPositions(colID, tableName);
            if (fixedIDs.size() > 0) {
            	// 先从list里删掉
                for (int i = 0; i < fixedIDs.size(); i++) {
                	removeOneFixed(articleInfos, fixedIDs.get(i).getDocID());
				}
                
                // 再插到指定位置
                for (int i = 0; i < fixedIDs.size(); i++) {
                	ArticleInfo oneFix = fixedIDs.get(i);
                	int pos = oneFix.getPosition() - 1;
                    if (pos >= articleInfos.size()) {
                    	articleInfos.add(oneFix);
                    } else {
                    	articleInfos.add(pos, oneFix);
                    }
                }
            }
        }
        /*
         * 列表长度改为后200，不必再删掉200以后的列表里的固定位置稿，一般走不到 else if(start > 0){ for (int i
		 * = 0; i < jsonArr.size(); i++) {
		 * if(((JSONObject)jsonArr.get(i)).getInt("position") > 0)
		 * jsonArr.remove(i--); } }
		 */
    }
    
    private void removeOneFixed(List<ArticleInfo> articleIDs, long docID) {
    	for (int i = 0; i < articleIDs.size(); i++) {
			if (articleIDs.get(i).getDocID() == docID) {
				articleIDs.remove(i);
				return;
			}
		}
    }

    // 查询固定位置的稿件ID
    private List<ArticleInfo> queryPositions(
            long colID, String tableName) {
    	List<ArticleInfo> fixedIDs = new ArrayList<>();
    			
        IResultSet rs = null;
        DBSession conn = null;
        String sql = "SELECT SYS_DOCUMENTID, a_position FROM "
                + tableName
                + " WHERE a_status=1 and CLASS_1=? and a_position>0 ORDER BY a_position";
        try {
            conn = Context.getDBSession();
            rs = conn.executeQuery(sql, new Object[]{colID});
            while (rs.next()) {
            	ArticleInfo one = new ArticleInfo();
            	one.setDocID(rs.getLong("SYS_DOCUMENTID"));
            	one.setPosition(rs.getInt("a_position"));
            	
            	fixedIDs.add(one);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(rs);
            ResourceMgr.closeQuietly(conn);
        }
        return fixedIDs;
    }

    /**
     * 通过栏目ID，得到子孙栏目
     *
     * @throws E5Exception
     */
    private void getSonColumns(List<Column> columns, int colLibID, long colID)
            throws E5Exception {
        List<Column> subList = colReader.getSub(colLibID, colID);
        if (subList != null && subList.size() > 0) {
            columns.addAll(subList);
            for (Column col : subList) {
                if (this.isNotForbidden(col)) {
                    getSonColumns(columns, colLibID, col.getId());
                }
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
        inJson.put("description", StringUtils.getNotNull(bean.getDescription())); // 栏目描述
        inJson.put("linkUrl", StringUtils.getNotNull(bean.getLinkUrl())); // 外链地址
        inJson.put("isForbidden", bean.isForbidden());
        inJson.put("casNames", StringUtils.getNotNull(bean.getCasNames())); // 栏目路径以~分割
        inJson.put("rssCount", bean.getRssCount()); //栏目订阅数
        inJson.put("appFixed", bean.getAppFixed()); //固定位置？
        inJson.put("appShow", bean.getAppShow()); //默认显示？

        // 栏目类型（资讯类、影视图片类、专题类、便民服务类）
        inJson.put("columnType", getCatCode(CatTypes.CAT_COLUMN.typeID(), bean.getAppTypeID()));

        // 栏目样式
        inJson.put("columnStyle", getCatCode(CatTypes.CAT_COLUMNSTYLE.typeID(), bean.getAppStyleID()));
        return inJson;
    }

    /**
     * 取分类的分类码
     */
    private String getCatCode(int catTypeID, int catID) {
        try {
            if (catReader == null) catReader = (CatReader) Context.getBean(CatReader.class);
            Category cat = catReader.getCat(catTypeID, catID);
            if (cat != null)
                return cat.getCatCode();

            //若缓存中没有，可能是新加的，用CatManager取一次
            if (catManager == null) catManager = (CatManager) Context.getBean(CatManager.class);
            cat = catManager.getCat(catTypeID, catID);
            return (cat == null) ? "" : cat.getCatCode();
        } catch (E5Exception e) {
            System.out.println(e.getLocalizedMessage());
            return "";
        }
    }

    /**
     * 设置稿件页的广告： 1）取当前栏目的文章广告，而不是文章所在的主栏目的文章广告 2）若是组图，则取组图页广告，其它类型下取文章页广告
     */
    private void setPageAdv(JSONObject article, long colID) {
        int mainColID = article.getInt("columnID");
        if (colID <= 0)
            colID = mainColID;

        // 并发时有问题，影响不大，忽略
        int type = article.getInt("articleType");
        getAdvs(colID,type,article.getInt("fileId"));
        if (type == Article.TYPE_PIC) {
            article.put("adv",
                        RedisManager.get(RedisKey.ADV_PAGE_ALBUM_KEY + colID));
        } else {
            article.put("adv",
                        RedisManager.get(RedisKey.ADV_PAGE_KEY + colID));
        }

    }

    // 按colID再设一次广告，供外网api调用（广告系统中没有栏目ID，放进redis的key中是栏目名）
    private void putPageAdvByColID(long colID, String colName) {
        try {
            String key = RedisKey.ADV_PAGE_KEY + colID;
            if (!RedisManager.exists(key)) {
                String value = RedisManager
                        .get(RedisKey.ADV_PAGE_KEY + colName);
                if (value != null)
                    RedisManager.set(key, value);
            }

            key = RedisKey.ADV_PAGE_ALBUM_KEY + colID;
            if (!RedisManager.exists(key)) {
                String value = RedisManager.get(RedisKey.ADV_PAGE_ALBUM_KEY
                                                        + colName);
                if (value != null)
                    RedisManager.set(key, value);
            }
        } catch (Exception e) {
        }
    }

    /**
     * 组装稿件列表 colID为-1时表示不是根据栏目获取的列表
     */
    private JSONArray listArticles(List<ArticleInfo> articleInfos, int artAppLibID, long colID) {
        JSONArray jsonArr = new JSONArray();
        try {
        	String tenantCode = LibHelper.getTenantCodeByLib(artAppLibID);
        	
            int attLibID = LibHelper.getLibID(DocTypes.ATTACHMENT.typeID(), tenantCode);
    		int artExtLibID = LibHelper.getLibID(DocTypes.ARTICLEEXT.typeID(), tenantCode);
    		
            for (ArticleInfo articleInfo : articleInfos) {
            	long docID = articleInfo.getDocID();
                JSONObject inJson = listArticleOne(artAppLibID, docID, colID, attLibID, artExtLibID);
                if (inJson != null){
                	inJson.put("isTop",articleInfo.isTop());
                	inJson.put("position",articleInfo.getPosition());
                    jsonArr.add(inJson);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArr;
    }

    /**
     * 读稿件列表 之 读一个稿件的json
     * <p>
     * 先从Redis里读稿件内容，若没有，则读数据库，形成json再放入Redis
     */
    private JSONObject listArticleOne(
            int docLibID, long docID, long colID,
            int attLibID, int artExtLibID) {

    	// 先从redis里读稿件内容（发布时自动添加，一天后失效）
        String key = RedisKey.APP_ARTICLELIST_ONE_KEY + docID;
        
    	List<DocLib> articleLibs = LibHelper.getLibs(DocTypes.ARTICLE.typeID(),
                Tenant.DEFAULTCODE);
    	int artLibID = articleLibs.get(0).getDocLibID();
    	if(artLibID == docLibID){//web
    		key = RedisKey.WEB_ARTICLELIST_ONE_KEY + docID;
    	}
        
        String article = RedisManager.get(key);

        JSONObject inJson = null;
        if (article == null) {
            // 若redis里没有，从数据库中，再保存到redis里
            inJson = ArticleJsonHelper.listArticleOne(docLibID, docID, attLibID, artExtLibID);
            if (inJson != null) RedisManager.setLonger(key, inJson.toString());
        } else {
            inJson = JSONObject.fromObject(article);
        }
        if (-1 != colID) // 获取最热稿件列表colID == -1
            changeArticleOneByCol(docLibID, inJson, colID);

        return inJson;
    }

    /**
     * 稿件列表：按当前栏目修改稿件（是否关联稿件、url中加colID）
     */
    private void changeArticleOneByCol(
            int docLibID, JSONObject inJson,
            long colID) {
        if (inJson == null)
            return;

        // 如果是关联过来的稿件，isRel=1;
        int mainColID = inJson.getInt("colID");
        if (colID > 0 && mainColID != colID) {
            int colLibID = LibHelper.getLibIDByOtherLib(
                    DocTypes.COLUMN.typeID(), docLibID);
            if (colExist(colLibID, mainColID))
                inJson.put("isRel", 1);
        }

        int type = inJson.getInt("articleType");
        if (type < Article.TYPE_SPECIAL || type == Article.TYPE_ACTIVITY
                || type == Article.TYPE_PANORAMA || type == Article.TYPE_FILE) {
            String url = inJson.getString("contentUrl");
            // 稿件内容url，带当前栏目ID，以便稿件详情页里按当前栏目显示广告信息（如：首页的广告与其它栏目不同）
            url += "&colID=" + ((colID > 0) ? colID : mainColID);
            inJson.put("contentUrl", url);
        }
    }

    private String getColName(long curColID) {
        String colName = null;
        try {
            Column col1 = colReader.get(LibHelper.getColumnLibID(), curColID);
            if (col1 != null)
                colName = col1.getName();
        } catch (E5Exception e) {
            e.printStackTrace();
        }
        return colName;
    }

    private boolean colExist(int colLibID, long colID) {
        try {
            if (colReader.get(colLibID, colID) != null)
                return true;
            ;
        } catch (E5Exception e) {
        }
        return false;
    }

	private List<Long> getRecArticleIds(JSONArray recArticles) throws Exception {
        List<Long> ids = new ArrayList<Long>();
        for (int i = 0; i < recArticles.size(); i++) {
            JSONObject obj = recArticles.getJSONObject(i);
            ids.add(obj.getLong("aid"));
        }
        return ids;
    }

    private String recomendList(
	        JSONArray recArticles, String data, int count,
	        int siteID, String userId, int artAppLibID) throws Exception {
	    JSONObject jsonObj = new JSONObject();
	    long timeMillis = System.currentTimeMillis();
	    jsonObj.put("version", String.valueOf(timeMillis));
	    List<Long> docIds = getRecArticleIds(recArticles);
	    int size = docIds.size();
	    jsonObj.put("total", String.valueOf(size));
	    if (size <= count) {
	        jsonObj.put("hasMore", "false");
	        jsonObj.put("num", String.valueOf(docIds.size()));
	    } else {
	        jsonObj.put("num", String.valueOf(count));
	        jsonObj.put("hasMore", "true");
	        // 推荐超过count数则将稿件id缓存到redis中
	        JSONObject cacheObj = new JSONObject();
	        cacheObj.put("version", String.valueOf(timeMillis));
	        cacheObj.put("total", docIds.size());
	        cacheObj.put("list", docIds);
	        
	        RedisManager.set(RedisKey.NIS_EVENT_RECOMMEND + userId, cacheObj.toString());
	        docIds = docIds.subList(0, count);
	    }
	    List<ArticleInfo> articleInfos = packDocIDs(docIds);
	    JSONArray jsonArray = listArticles(articleInfos, artAppLibID, -1);
	    jsonObj.put("list", jsonArray.toString());
	    return jsonObj.toString();
	}

	private String recomendListWithHot(String data, int count, int siteID, int artAppLibID) {
        try {
        	boolean readHot = getHotArticlesApp(artAppLibID, siteID, 1);
        	if (readHot) {
                String key = RedisManager.getKeyBySite(RedisKey.APP_ARTICLELIST_HOT_KEY, siteID) + 1;
                String hotList = RedisManager.get(key);
                
                JSONObject jsonObj = JsonHelper.getJson(hotList);
                int size = jsonObj.getJSONArray("list").size();
                
                jsonObj.put("version", String.valueOf(System.currentTimeMillis()));
                jsonObj.put("hasMore", "false");
                jsonObj.put("total", size);
                jsonObj.put("num", size);
                
                return jsonObj.toString();
        	}
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("version", String.valueOf(System.currentTimeMillis()));
        jsonObj.put("hasMore", "false");
        jsonObj.put("total", 0);
        jsonObj.put("num", 0);
        return jsonObj.toString();
    }

    @SuppressWarnings("rawtypes")
	private String recomendListWithCache(
	        String data, int start, int count,
	        int siteID, String userId, int artAppLibID) throws Exception {
	    JSONObject jsonObj = new JSONObject();
	    
	    String cache = RedisManager.get(RedisKey.NIS_EVENT_RECOMMEND + userId);
	    if (cache != null) {
		    JSONObject cacheObj = JSONObject.fromObject(cache);
		    int total = cacheObj.getInt("total");
		    List idList = (List) cacheObj.get("list");
		    
		    jsonObj.put("version", cacheObj.getString("version"));
		    jsonObj.put("total", String.valueOf(total));
		    
		    List<Long> docIds = new ArrayList<Long>();
		    for (int i = 0; i < idList.size(); i++) {
		        Integer id = (Integer) idList.get(i);
		        docIds.add(Long.valueOf(id));
		    }
		    if (start > docIds.size()) {
		        return recomendListWithHot(data, count, siteID, artAppLibID);
		    }
		    int num = start + count;
		    if (total <= num) {
		        jsonObj.put("num", String.valueOf(total - start));
		        jsonObj.put("hasMore", "false");
		        docIds = docIds.subList(start, docIds.size());
		    } else {
		        jsonObj.put("num", String.valueOf(count));
		        jsonObj.put("hasMore", "true");
		        docIds = docIds.subList(start, num);
		    }
		    List<ArticleInfo> articleInfos = packDocIDs(docIds);
		    JSONArray jsonArray = listArticles(articleInfos, artAppLibID, -1);
		    
		    jsonObj.put("list", jsonArray.toString());
	    } else {
	        jsonObj.put("num", "0");
	        jsonObj.put("hasMore", "false");
	    }
	    return jsonObj.toString();
	}

	private static List<ArticleInfo> packDocIDs(List<Long> docIDs) {
        List<ArticleInfo> articleInfos = new ArrayList<>();
        for (long docID : docIDs) {
            ArticleInfo articleInfo = new ArticleInfo();
            articleInfo.setDocID(docID);
            
            articleInfos.add(articleInfo);
        }
        return articleInfos;
    }

    private Object getRecData(String data,int start,int count,String type) throws Exception {
        String url=null;
        if(type.equals("文章推荐")){
            url = InfoHelper.getConfig("互动", "会员推荐地址");
        }else if(type.equals("推荐历史")){
            url = InfoHelper.getConfig("互动", "历史推荐地址");
        }else if(type.equals("文章关键词")){
            url=InfoHelper.getConfig("互动", "文章关键词获取地址");
        }

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        JSONObject obj = JSONObject.fromObject(data);
        params.add(new BasicNameValuePair("appid", obj.getString("appid")));
        params.add(new BasicNameValuePair("dev", obj.getString("dev")));
        params.add(new BasicNameValuePair("t", obj.getString("t")));
        params.add(new BasicNameValuePair("uid", obj.getString("uid")));
        params.add(new BasicNameValuePair("aid", obj.getString("aid")));
        params.add(new BasicNameValuePair("bid", obj.getString("bid")));
        params.add(new BasicNameValuePair("cname", obj.getString("cname")));
        params.add(new BasicNameValuePair("separator", obj
                .getString("separator")));
        params.add(new BasicNameValuePair("rule", obj.getString("rule")));
        params.add(new BasicNameValuePair("rule_view", obj
                .getString("rule_view")));
        params.add(new BasicNameValuePair("param_view", obj
                .getString("param_view")));
        params.add(new BasicNameValuePair("row", InfoHelper.getConfig("互动",
                "会员推荐条数")));
        params.add(new BasicNameValuePair("attrs", obj.getString("attrs")
                + ",ptime"));
        params.add(new BasicNameValuePair("debug", obj.getString("debug")));
        params.add(new BasicNameValuePair("start", String.valueOf(start)));
        params.add(new BasicNameValuePair("count", String.valueOf(count)));
        return getRemotingData(data,url,params,type);
    }
    private Object getRemotingData(String data,String url, List<NameValuePair> params,String type)throws Exception {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            try {
                response = httpClient.execute(httpPost);
                HttpEntity entity = response.getEntity();
                String value = EntityUtils.toString(entity);
                if (value != null && !"".equals(value)) {
                    JSONObject json = JSONObject.fromObject(value);
                    if (json.containsKey("Code")&&!json.getString("Code").equals("1402")) {
                        System.out.println(json.getString("Msg"));
                        return null;
                    } else {
                        if (json.containsKey("status")&&"402".equals(json.getString("status"))) {
                            System.out.println(json.getString("msg"));
                            return null;
                        } else {
                            String result = json.getString("data");
                            if (data == null || data == ""
                                    || "[]".equals(result))
                                return null;
                            if(type.equals("文章关键词"))
                                return JSONObject.fromObject(result);
                            return JSONArray.fromObject(result);
                        }
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("请求异常:error_1");
            } finally {
                response.close();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("请求异常:error_2");
        } finally {
            httpClient.close();
        }
        return null;
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

    private int getSiteIDByColumn(long colID) {
		int colLibID = LibHelper.getLibID(DocTypes.COLUMN.typeID(), Tenant.DEFAULTCODE);
		try {
			Column column = colReader.get(colLibID, colID);
			return column.getSiteID();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	private JSONObject getColumnOneJson(Column column, int colLibID) {
	    JSONObject inJson = colCommonFields(column);
	
	    // 栏目订阅数
	    long rssCount = getColSubscribeCount(colLibID, column.getId());
	    inJson.put("rssCount", rssCount);
	
	    return inJson;
	}

    private JSONObject getColumnJson(Column column, int colLibID) {
        JSONObject inJson = colCommonFields(column);

        return inJson;
    }

	private JSONArray getModuleItems(int siteID, long columnId, int type,
			int count, List<Long> ids) throws E5Exception {
		JSONArray jsonArr = new JSONArray();
		
        String tenantCode = Tenant.DEFAULTCODE;
		String iconUrl = UrlHelper.apiUserIcon();
		
    	SubjectApiManager subjectApiManager = (SubjectApiManager)Context.getBean("subjectApiManager");
    	ActivityApiManager activeApiManager = (ActivityApiManager)Context.getBean("activityApiManager");
    	QAApiManager qaApiManager = (QAApiManager)Context.getBean("QAApiManager");
    	
		for (long targetID : ids) {
		    JSONObject jsonObject = null;
		    switch (type) {
		        case 0:
		            List<DocLib> articleLibs = LibHelper.getLibs(DocTypes.ARTICLE.typeID(), tenantCode);
	
		            int artAppLibID = articleLibs.get(1).getDocLibID();
		            int attLibID = LibHelper.getLibID(DocTypes.ATTACHMENT.typeID(), tenantCode);
		            int artExtLibID = LibHelper.getLibID(DocTypes.ARTICLEEXT.typeID(), tenantCode);
	
		            jsonObject = listArticleOne(artAppLibID, targetID, columnId, attLibID, artExtLibID);
		            break;
		        case 1:
		            int colLibID = LibHelper.getLibID(DocTypes.COLUMN.typeID(), tenantCode);
		            Column column = colReader.get(colLibID, targetID);
	
		            jsonObject = getColumnOneJson(column, colLibID);
		            break;
		        case 2: {
		            int docLibID = LibHelper.getLibID(DocTypes.SUBJECT.typeID(), tenantCode);
		            jsonObject = subjectApiManager.assembleSubject(iconUrl, docLibID, targetID);
		            break;
		        }
		        case 3: {
		            int docLibID = LibHelper.getLibID(DocTypes.QA.typeID(), tenantCode);
		        	jsonObject = qaApiManager.assembleQA(siteID, docLibID, targetID);
		            break;
		        }
		        case 4: {
		            int docLibID = LibHelper.getLibID(DocTypes.ACTIVITY.typeID(), tenantCode);
		        	jsonObject = activeApiManager.assembleActivity(siteID, docLibID, targetID);
		            break;
		        }
                case 5: {
                    int colLibID1 = LibHelper.getLibID(DocTypes.COLUMN.typeID(), tenantCode);
                    Column column1 = colReader.get(colLibID1, targetID);
                    jsonObject = getColumnJson(column1, colLibID1);
                    break;
                }
		    }
		    if(jsonObject != null) jsonArr.add(jsonObject);
		}
		return jsonArr;
	}

	private String getModuleItemsAuto(int siteID, long columnId, int type,
			int count) throws E5Exception {
		String list = "";
		switch (type) {
		    case 0:
		        list = getModlByArticle(columnId, count);
		        break;
		    case 1:
		        list = getModlBySubscription(count, columnId);
		        break;
		    case 2:
		        list = getModlBySubject(siteID, count, -1);
		        break;
		    case 3:
		        list = getModlByQA(siteID, count, -1);
		        break;
		    case 4:
		        list = getModlByActivity(siteID, count, -1);
		        break;
		}
		return list;
	}

	//当推荐模块无内容时调取普通稿件列表内容
    private String getModlByArticle(long columnID, int count) {
		JSONArray jsonNewArr = readArticlesByCol(columnID, count);
        return jsonNewArr.toString();
    }

    //当推荐模块无内容时调取普通订阅栏目列表
    private String getModlBySubscription(int count, long columnID) {
        // 读出子栏目
        int colLibID = LibHelper.getLibID(DocTypes.COLUMN.typeID(),
                                          Tenant.DEFAULTCODE);
        JSONArray jsonArr = new JSONArray();
        try {
            List<Column> subList = colReader.getSub(colLibID, columnID);
            // 子栏目列表
            int i = 0;
            if (subList != null) {
                for (Column column : subList) {
                    if (this.isNotForbidden(column)) {
                        if (i >= count) break;
                        i++;
                        JSONObject inJson = getColumnOneJson(column, colLibID);
                        jsonArr.add(inJson);
                    }
                }
            }
        } catch (E5Exception e) {
            e.printStackTrace();
        }
        return jsonArr.toString();
    }

    //当推荐模块无内容时调取普通问吧列表
    private String getModlBySubject(int siteID, int count, long docID) throws E5Exception {
    	SubjectApiManager activeApiManager = (SubjectApiManager)Context.getBean("subjectApiManager");
        return activeApiManager.getModuleList(siteID, count);
    }

    //当推荐模块无内容时调取普通问答列表
    private String getModlByQA(int siteID, int count, long contentID) throws E5Exception {
    	QAApiManager activeApiManager = (QAApiManager)Context.getBean("QAApiManager");
        return activeApiManager.getModuleList(siteID, count);
    }

    //当推荐模块无内容时调取普通活动列表
	private String getModlByActivity(int siteID, int count, long contentID) throws E5Exception {
    	ActivityApiManager activeApiManager = (ActivityApiManager)Context.getBean("activityApiManager");
        return activeApiManager.getModuleList(siteID, count);
    }
	
    public String collectArticles(String artcileIDs, int ch) {
    	
        List<Long> docIDs = new ArrayList<>();
        long[] fileIdIDs = StringUtils.getLongArray(artcileIDs);
        for (long fileId : fileIdIDs) {
            if (!docIDs.contains(fileId))
                docIDs.add(fileId);
        }
        // 根据ID读稿件库，返回json
        return solrResult(docIDs, ch);
    }

    private List<ArticleInfo> getArticleInfoByID(JSONArray recArticles,int artAppLibID) throws E5Exception{
        List<ArticleInfo> articleInfos = new ArrayList<>();

        DocumentManager docManager=DocumentManagerFactory.getInstance();
        for(int i=0;i<recArticles.size();i++){
            Document doc=docManager.get(artAppLibID,recArticles.getLong(i));
            ArticleInfo info = new ArticleInfo();
            info.setDocID(doc.getLong("SYS_DOCUMENTID"));

            BigDecimal highBit = new BigDecimal(-100000000L);
            String order = doc.getString("a_order");
            BigDecimal OrderBD = new BigDecimal(order);
            Boolean isTop = OrderBD.compareTo(highBit) <= 0 ? true : false;
            info.setTop(isTop);

            articleInfos.add(info);
        }
        return articleInfos;
    }

    public boolean getAdvs(long columnID,int type,int fileID){
        //根据请求设置查询条件和key值
        String condition="ad_columnID=? and ad_type in(?,?,?) ";
        Object [] objs=new Object[]{columnID,1,2,5};
        String key=RedisKey.ADV_COLUMN_LIST_KEY+columnID;
        if(type>=0){
            condition="ad_columnID=? and ad_type=? ";
            if(type==1){
                objs=new Object[]{columnID,4};
                key=RedisKey.ADV_PAGE_ALBUM_KEY+columnID;
            }else{
                /*condition+=" and ad_linkID=? ";*/
                objs=new Object[]{columnID,3};
                key=RedisKey.ADV_PAGE_KEY+columnID;
            }
        }
        condition+=" and ad_status=0 order by ad_order asc,SYS_CREATED desc";

        if(null!=RedisManager.get(key))
            return true;

        try{
            int ADDoclibID=LibHelper.getLibID(DocTypes.AD.typeID(),Tenant.DEFAULTCODE);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            JSONArray jsonArr=new JSONArray();
            long nowtime=df.parse(df.format(new Date())).getTime();
            DocumentManager docManager=DocumentManagerFactory.getInstance();
            Document[] docs=docManager.find(ADDoclibID,condition,objs);
            for(Document doc:docs){
                //通过开始截止日期筛选有效广告
                long begintime=doc.getDate("ad_beginDate").getTime();
                long endtime=doc.getDate("ad_endDate").getTime();
                if(begintime>nowtime||endtime<nowtime) continue;
                JSONObject jsonObj= assembleAD(doc);
                jsonArr.add(jsonObj);
            }
            RedisManager.set(key,jsonArr.toString());
        }catch (Exception e){
            e.printStackTrace();
        }

        return true;
    }

    public JSONObject assembleAD(Document doc)  {
        JSONObject data = new JSONObject();
        try {
            data.put("advID", doc.getDocID());
            data.put("title", StringUtils.getNotNull(doc.getString("ad_topic")));
            data.put("type", doc.getInt("ad_type"));
            data.put("imgUrl", StringUtils.getNotNull(doc.getString("ad_picUrl")));
            data.put("contentUrl", StringUtils.getNotNull(doc.getString("ad_linkUrl")));
            data.put("sizeScale",doc.getInt("ad_sizeScale"));
            data.put("startTime", doc.getString("ad_beginDate"));
            data.put("endTime", doc.getString("ad_endDate"));
            data.put("pageTime", doc.getInt("ad_showTime"));
            data.put("adOrder", doc.getInt("ad_order"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;

    }

    /**
     * 获取微信分享的jsapi_ticket，并缓存到Redis中
     * @throws E5Exception
     */
    public String getJsApiTicket() throws E5Exception {
    	String JSAPI_TICKET_URL = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=<ACCESS_TOKEN>&type=jsapi";
    	DocumentManager docManager = DocumentManagerFactory.getInstance();
    	DocLib[] docLibIDs = LibHelper.getLibs(DocTypes.WXACCOUNT.typeID()); // 多租户
		for (DocLib docLib : docLibIDs) {
			// 读微信账号表，取出所有微信账号
			Document[] docs = docManager.find(docLib.getDocLibID(), "SYS_DELETEFLAG=0", null);
			for (Document wxDoc : docs) {
				String ticketJson = null;
				try {
					ticketJson = WeixinAPI.getAccess(JSAPI_TICKET_URL.replace("<ACCESS_TOKEN>", wxDoc.getString("wxa_accessToken")));
					JSONObject ticketObj = JSONObject.fromObject(ticketJson);
					if(/*RedisManager.ttl("wx_jsapi_appid")<=600 && */ticketObj.getInt("errcode")==0) {
						RedisManager.set("wx_jsapi_appid", wxDoc.getString("wxa_appID"), 119*60);
						RedisManager.set("wx_jsapi_ticket", ticketObj.getString("ticket"), 119*60);
						return wxDoc.getString("wxa_appID");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null;
    }
    
    /**
     * 查看热门稿件列表，并放到redis中
     * @param siteID
     * @param articleType
     * @param timeType
     * @param orderType
     * @return
     */
    public boolean getNewArticleHotList(int siteID, int articleType, int timeType, int orderType,int channel) {
        String key;
        if(channel==1){
            key = RedisManager.getKeyBySite(RedisKey.APP_NEW_HOT_ARTICLELIST_KEY, siteID)+ articleType +"."+ timeType +"."+ orderType;
        }else{
            key = RedisManager.getKeyBySite(RedisKey.WEB_NEW_HOT_ARTICLELIST_KEY, siteID)+ articleType +"."+ timeType +"."+ orderType;
        }

        if (RedisManager.exists(key)) return true;

        String sql = getQueryNewArticleHotSql(siteID,articleType,timeType,orderType,channel);

        JSONArray jsonArray = new JSONArray();
        IResultSet rs = null;
        DBSession conn = null;
        try {
            conn = Context.getDBSession();
            rs = conn.executeQuery(sql);
            while (rs.next()) {
                JSONObject jsonObject = new JSONObject();
                long id = rs.getLong("SYS_DOCUMENTID");
                jsonObject.put("fileId",id);
                jsonObject.put("articleType",rs.getString("a_type"));
                jsonObject.put("title",rs.getString("sys_topic"));
                jsonObject.put("arthorName",rs.getString("sys_authors"));
                jsonObject.put("colID",rs.getString("a_columnID"));
                jsonObject.put("colName",rs.getString("a_column"));
                jsonObject.put("publishtime",rs.getString("a_pubTime"));
//                jsonObject.put("countClick",rs.getString("totalClick"));
//                jsonObject.put("countShare",rs.getString("totalShare"));
//                jsonObject.put("countDiscuss",rs.getString("totalDiscuss"));
                JSONObject jsonObject1 = getTopicNameByID(id,channel);
                jsonObject.put("topicID",jsonObject1.getString("topicID"));
                jsonObject.put("topicName",jsonObject1.getString("topicName"));

                jsonObject.put("url",rs.getString("a_url"));
                jsonObject.put("urlPad",rs.getString("a_urlPad"));
                jsonObject.put("linkID",StringUtils.getNotNull(rs.getString("a_linkID")));
                jsonObject.put("linkName",StringUtils.getNotNull(rs.getString("a_linkName")));

                String url;
                int type = rs.getInt("a_type");
                if (type >= Article.TYPE_SPECIAL && type != Article.TYPE_ACTIVITY
                        && type != Article.TYPE_PANORAMA && type != Article.TYPE_FILE) {
                    url = StringUtils.getNotNull(rs.getString("a_urlPad"));
                    jsonObject.put("isBigPic", 0);// 原大图稿件标记。避免app旧版在直播稿、链接稿等时闪退
                } else {
                    // 稿件内容url，带当前栏目ID，以便稿件详情页里按当前栏目显示广告信息（如：首页的广告与其它栏目不同）
                    url = UrlHelper.getArticleContentUrl(id);
                    jsonObject.put("isBigPic", rs.getInt("a_isBigPic")<0?0:rs.getInt("a_isBigPic"));// 大图稿件
                }
                jsonObject.put("bigPic", rs.getInt("a_isBigPic")<0?0:rs.getInt("a_isBigPic"));// 大图稿件
                jsonObject.put("contentUrl", url);

                int colID = rs.getInt("a_columnID");
                String colColor = "";
                String colNameReal = "";
                ColumnReader colReader = (ColumnReader) Context.getBean("columnReader");
                Column col = colReader.get(LibHelper.getColumnLibID(), colID);
                if(col!=null){
                    colColor = col.getColor();
                    colNameReal = col.getName();
                }
                jsonObject.put("colColor", colColor);
                jsonObject.put("colNameReal", colNameReal);

                int docLibID = rs.getInt("sys_doclibid");

                listArticleAttExtField(jsonObject,id,docLibID);

                jsonObject.put("picBig", getPicUrlByID(id,channel));

                // 初始阅读数，用于外网api从缓存redis读实际阅读数时相加
                // 显示阅读数：数据库中的初始阅读数+实际阅读数+分享页的阅读数。在外网api里会替换成实时的阅读数，取自redis缓存
                int countClickInitial = getInt(rs.getInt("a_countClickInitial"));
                int countClickReal = getInt(rs.getInt("a_countClick"));
                int countShareClick = getInt(rs.getInt("a_countShareClick"));
                int countClickAll = countClickInitial + countClickReal + countShareClick;

                jsonObject.put("countClick", countClickAll); // 所有点击数
                jsonObject.put("countClickInitial", countClickInitial); // 初始点击数
                jsonObject.put("countClickReal", countClickReal); // 实际点击数
                jsonObject.put("countShareClick", countShareClick);// 分享点击数

                jsonObject.put("countDiscuss", getlong(rs.getLong("a_countDiscuss")));
                jsonObject.put("countPraise", getlong(rs.getLong("a_countPraise")));
                jsonObject.put("countShare", getlong(rs.getLong("a_countShare")));
                //组图稿件，增加返回字段content;其他稿件不增加
                if(type==Article.TYPE_PIC){
                    jsonObject.put("picContent",StringUtils.getNotNull(rs.getString("a_content")));
                }else{
                    jsonObject.put("picContent","");
                }

                //新增需求字段
                addColumnUrl(jsonObject,col,colReader);

                jsonArray.add(jsonObject);
            }
            if(jsonArray.size()>0){
                JSONObject redisJson= new JSONObject();
                redisJson.put("list",jsonArray);
                RedisManager.set(key,redisJson.toString(),300);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            ResourceMgr.closeQuietly(rs);
            ResourceMgr.closeQuietly(conn);
        }

        return true;
    }

    private void addColumnUrl(JSONObject jsonObject, Column column, ColumnReader colReader) {
        if(column!=null){
            int columnLibID = column.getLibID();
            int templateLibID = LibHelper.getLibIDByOtherLib(DocTypes.TEMPLATE.typeID(), columnLibID);

            BaseDataCache baseDataCache = (BaseDataCache) (CacheReader.find(BaseDataCache.class));

            Template[] template = new Template[2];
            template[0] = baseDataCache.getTemplateByID(templateLibID, column.getTemplate());
            template[1] = baseDataCache.getTemplateByID(templateLibID, column.getTemplatePad());

            int siteRuleLibID = LibHelper.getLibIDByOtherLib(DocTypes.SITERULE.typeID(), columnLibID);

            SiteRule siteRule0 = baseDataCache.getSiteRuleByID(siteRuleLibID, column.getPubRule());
            SiteRule siteRule1 = baseDataCache.getSiteRuleByID(siteRuleLibID, column.getPubRulePad());

            //把发布地址也读出来
            String url = colReader.getColumnUrl(column.getId(), column.getFileName(), template[0], siteRule0);
            String urlPad = colReader.getColumnUrl(column.getId(), column.getFileNamePad(), template[1], siteRule1);
            jsonObject.put("columnUrl", StringUtils.isBlank(url)?"":url);
            jsonObject.put("columnUrlPad", StringUtils.isBlank(urlPad)?"":urlPad);
        }else{
            jsonObject.put("columnUrl", "");
            jsonObject.put("columnUrlPad", "");
        }
    }


    /**
     * 设置附件表和扩展字段表中的字段
     */
    private static void listArticleAttExtField(JSONObject inJson, long docID, int docLibID)
            throws E5Exception {
        Object[] params = new Object[] { docID, docLibID };
        int attDocLibID = LibHelper.getLibID(DocTypes.ATTACHMENT.typeID(), Tenant.DEFAULTCODE);
        // 附件表
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        Document[] docs = docManager.find(attDocLibID,
                "att_articleID=? and att_articleLibID=? ORDER BY att_order",
                params);

        int length = 0;
        for (Document doc : docs) {
            String url = StringUtils.getNotNull(doc.getString("att_urlPad"));

            int type = doc.getInt("att_type");
            if (0 == type) {
                if (length < 3)
                    inJson.put("pic" + length, url);// 列出前三个图片的url，外网地址
                length++;
            } else if (1 == type) {
                inJson.put("videoUrl", url); // 稿件对应视频的url，视频稿件时使用，附件表中查，外网地址
                inJson.put("duration",doc.getInt("att_duration")); //视频时长。若多个视频附件时会重复替换到最后一个
            } else if (2 == type) {
                inJson.put("picBig", url); // big
            } else if (3 == type) {
                inJson.put("picMiddle", url); // middle
            } else if (4 == type) {
                inJson.put("picSmall", url); // small
            }
        }
        inJson.put("picCount", String.valueOf(length)); // 正文图片/组图图片数量
    }

    
    private String getPicUrlByID(long id, int channel) {
        String sql = "select att_urlPad from xy_attachment where att_articleID = ? and att_articleLibID = ? " +
                " and att_urlPad like 'http%' and att_type in ('2','3','4') order by att_type desc limit 1 ";

        String tenantCode = Tenant.DEFAULTCODE;
        List<DocLib> articleLibs = LibHelper.getLibs(DocTypes.ARTICLE.typeID(), tenantCode);
        int artAppLibID;
        if(channel==1){//app
            artAppLibID = articleLibs.get(1).getDocLibID();
        }else{//web
            artAppLibID = articleLibs.get(0).getDocLibID();
        }
        Object[] param = new Object[]{id,artAppLibID};

        String result = getSqlResult(sql, param);

        return result;
    }

    private JSONObject getTopicNameByID(long id, int channel) {

        String sql = "select a_topicID,a_topicName from xy_topicrelart where a_articleID = ? and a_channel = ? limit 1";
        int channelValue;
        if(channel==1){//app
            channelValue = 2;
        }else{//web
            channelValue = 1;
        }

        Object[] param = new Object[]{id,channelValue};
        JSONObject jsonObject = getSqlResult2(sql, param);

        return jsonObject;
    }

    private String getSqlResult(String sql, Object[] param){
        String resultValue = "";
        IResultSet rs = null;
        DBSession conn = null;
        try {
            conn = Context.getDBSession();
            rs = conn.executeQuery(sql,param);
            while (rs.next()) {
                resultValue = rs.getString(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(rs);
            ResourceMgr.closeQuietly(conn);
        }

        return resultValue;
    }

    private JSONObject getSqlResult2(String sql, Object[] param){
        IResultSet rs = null;
        DBSession conn = null;
        JSONObject jsonObject = new JSONObject();
        String topicID = "";
        String topicName = "";
        try {
            conn = Context.getDBSession();
            rs = conn.executeQuery(sql,param);
            while (rs.next()) {
                topicID = rs.getString("a_topicID");
                topicName = rs.getString("a_topicName");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(rs);
            ResourceMgr.closeQuietly(conn);
        }
        jsonObject.put("topicID",topicID);
        jsonObject.put("topicName",topicName);

        return jsonObject;
    }

    private String getQueryNewArticleHotSql(int siteID, int articleType, int timeType, int orderType,int channel) {
        String isVideoArticle;
        if(articleType==2){//百格视频
            isVideoArticle = "=";
        }else{//新闻
            isVideoArticle = "!=";
        }

        String articleTableName;
        if(channel==1){//app
            articleTableName = " xy_articleapp ";
        }else{//Web
            articleTableName = " xy_article ";
        }

        String orderName;
        if(orderType==3){
            orderName = "totalShare";
        }else if(orderType==2){
            orderName = "totalDiscuss";
        }else{
            orderName = "totalClick";
        }

        String timeCondition="";
        String tableName = "xy_stathour";
        Calendar date = Calendar.getInstance();
        if(timeType==3){//过去3天
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            date.add(Calendar.DAY_OF_MONTH, -4);
            Date nowdate=date.getTime();
            String time = sdf.format(nowdate);
            tableName = "xy_stat";
            timeCondition = " and st_date > '" + time +"'";
        }else if(timeType==2){//过去24小时,无需处理直接查xy_stathour表

        }else{//过去6小时
            int hourNow = date.get(Calendar.HOUR_OF_DAY);
            date.add(Calendar.HOUR_OF_DAY, -7);
            int hourBefore6 = date.get(Calendar.HOUR_OF_DAY);

            String orAnd = " and ";
            if(hourBefore6>hourNow){
                orAnd = " or ";
                timeCondition = " and (st_hour > "+ hourBefore6 + orAnd +" st_hour <" + hourNow+")";
            }else{
                timeCondition = " and st_hour > "+ hourBefore6 + orAnd +" st_hour <" + hourNow;
            }

            //过去6小时，需要特殊处理，如果过去6小时，热门稿件数量小于20；则查询过去12小时数据
            int hotArticleCount = getHotArticleCount(siteID, isVideoArticle, articleTableName,timeCondition);
            if(hotArticleCount<20){
                date.add(Calendar.HOUR_OF_DAY, -6);
                int hourBefore12= date.get(Calendar.HOUR_OF_DAY);
                String orAnd2 = " and ";
                if(hourBefore12>hourNow){
                    orAnd2 = " or ";
                    timeCondition = " and (st_hour > "+ hourBefore12 + orAnd2 +" st_hour <" + hourNow+")";
                }else{
                    timeCondition = " and st_hour > "+ hourBefore12 + orAnd2 +" st_hour <" + hourNow;
                }
            }

        }

        String sql = "select * from (";
        sql += " select a.a_countShare,a.a_countPraise,a.a_countDiscuss,a.a_countShareClick,a.a_countClick,a.a_countClickInitial,sys_doclibid,a_isBigPic,a_linkName,a_urlPad,a_url,a_linkID,SYS_DOCUMENTID,a_type,sys_topic,sys_authors,a_columnID,a_column,a_pubTime,a_content,"
                +"sum(b.st_countClick) as totalClick,sum(b.st_countDiscuss) as totalDiscuss,sum(b.st_countShare) as totalShare"
                +" from "+articleTableName+" a,"
                + tableName
                +" b where a.SYS_DOCUMENTID = b.st_id " + timeCondition
                +" and a_status = "+ Article.STATUS_PUB_DONE
                +" and a_type "+ isVideoArticle + Article.TYPE_VIDEO
                +" and a_siteID = "+ siteID
                +" group by SYS_DOCUMENTID ";
        sql += " ) c  ";
        sql +=" order by "+ orderName +" desc,totalClick desc, sys_documentid asc";
        sql +=" limit 0,20";

        return sql;
    }

    private int getHotArticleCount(int siteID, String isVideoArticle, String articleTableName,String timeCondition) {
        StringBuffer sql = new StringBuffer();
        sql.append("select count(1) from (");
        sql.append(" select sys_documentid from ");
        sql.append(articleTableName);
        sql.append(" a,xy_stathour b");
        sql.append(" where a.SYS_DOCUMENTID = b.st_id ");
        sql.append(timeCondition);
        sql.append(" and a_status = "+Article.STATUS_PUB_DONE);
        sql.append(" and a_type "+ isVideoArticle + Article.TYPE_VIDEO);
        sql.append(" and a_siteID = "+ siteID);
        sql.append(" group by SYS_DOCUMENTID");
        sql.append(") d");

        IResultSet rs = null;
        DBSession conn = null;
        int count = 0;
        try {
            conn = Context.getDBSession();
            rs = conn.executeQuery(sql.toString());
            while (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(rs);
            ResourceMgr.closeQuietly(conn);
        }

        return count;
    }


    public boolean getSubColArticles(int colLibID, long colID, int siteID) throws E5Exception {
    	String key = RedisKey.APP_ARTICLELIST_SUBCOL_KEY + colID;
        //避免并发读，加锁和多做一次判断
        if (RedisManager.exists(key)){
        	return true;
        }
        
        if (colID <= 0){
        	return false; // 必须指定父栏目
        }

        // 先判断站点ID是否存在，避免用无效站点ID对缓存攻击
        if (siteID <= 0){
        	siteID = 1; // 若没有siteID，则当做默认站点
        }
        int siteLibID = LibHelper.getLibID(DocTypes.SITE.typeID(),
                                           Tenant.DEFAULTCODE);
        if (!siteExist(siteLibID, siteID)){
            return false;
        }

        // 读出子栏目
        List<Column> subList = colReader.getSub(colLibID, colID);
        if (subList == null){
            return false;
        }
        
        int count = 1; // 稿件列表条数

        JSONArray columnList = new JSONArray();
        // 对每个子栏目，读稿件列表
        if (subList != null) {
            for (Column column : subList) {
                if (this.isNotForbidden(column)) {
                    JSONObject columnInfo = colCommonFields(column);
                    JSONArray articles = readArticlesByCol(column.getId(), count);

                    JSONObject one = new JSONObject();
                    one.put("column", columnInfo);
                    one.put("list", articles);

                    columnList.add(one);
                }
            }
        }

        JSONObject result = new JSONObject();
        result.put("list", columnList);

        RedisManager.set(key, result.toString());
        return true;
    }
    
    public boolean getTopicArticles(long topicID, int start, int siteID, int type,int channel) {
    	//避免并发读，加锁和多做一次判断
    	String key = RedisManager.getKeyBySite(RedisKey.APP_ARTICLELIST_TOPIC_KEY, siteID) + channel + "." + topicID + "." + type + "." + start;
        
        if (RedisManager.exists(key)) return true;
        
        int count = ApiManager.CACHE_LENGTH; // 使用固定长度的缓存
        //组织一个Hash，存放每个稿件ID在Redis List里的index <aid, index>
        JSONObject idAndIndex = new JSONObject();
        try {
            int artLibID = getArticleLibByChannel(channel);

            //读关联表，得到正确顺序的稿件ID、稿件是否置顶
            List<ArticleInfo> articleInfos = getArticleIDsByTopic(topicID, start, siteID, count, type, channel);

            //根据稿件ID得到稿件的列表
            JSONArray jsonArr = listArticles(articleInfos, artLibID, -1);

            //201条字符串列表，先是200条稿件，最后面放的是索引JSON对象
            String[] articleListInRedisList = new String[jsonArr.size() + 1];
            for (int i = 0; i < jsonArr.size(); i++) {
                //新增读取主栏目ColumnStyle
                String stringArticle = jsonArr.getString(i);
                JSONObject jsonArticle = JSONObject.fromObject(stringArticle);
                String mainColID = jsonArticle.getString("colID");
                //通过主栏目ID获取columnStyle
                String columnStyle = getColumnStyle(mainColID);
                jsonArticle.put("columnStyle",columnStyle);

            	articleListInRedisList[i] = jsonArticle.toString();
				idAndIndex.put(articleInfos.get(i).getDocID(), i);
            }
            articleListInRedisList[jsonArr.size()] = idAndIndex.toString();
            
            //加入Redis
			//RedisManager.set(key, redisJson.toString());
            RedisManager.resetLongList(key, articleListInRedisList);
            
	        return true;
		} catch (E5Exception e) {
			e.printStackTrace();
	        return false;
		}
    }
    
    private int getArticleLibByChannel(int channel) throws E5Exception {
	    List<DocLib> articleLibs = LibHelper.getLibs(DocTypes.ARTICLE.typeID(),
	                                                 Tenant.DEFAULTCODE);
	    int artLibID = articleLibs.get(channel).getDocLibID();
		return artLibID;
	}
    
    private List<ArticleInfo> getArticleIDsByTopic(long topicID, int start, 
    		int siteID, int count,int type, int channel) throws E5Exception {
        // 取出稿件ID列表
        String sql = "SELECT a_articleID as SYS_DOCUMENTID,a_order FROM xy_topicrelart WHERE a_topicID=? and a_status=? and a_channel=? and a_siteID=?";
        Object[] params = null;

        if(type == 0){
            sql += " and a_type not in (?,?) ORDER BY a_order";
            params = new Object[]{topicID, Article.STATUS_PUB_DONE,channel+1,siteID,Article.TYPE_VIDEO,Article.TYPE_PIC};
        }else if(type != 100){
        	sql += " and a_type=? ORDER BY a_order";
        	params = new Object[]{topicID, Article.STATUS_PUB_DONE,channel+1,siteID,type};
        }else{
        	sql += " ORDER BY a_order";
        	params = new Object[]{topicID, Article.STATUS_PUB_DONE,channel+1,siteID};
        }

        List<ArticleInfo> articleInfos = queryArticleIDs(sql, params, start, count);

        return articleInfos;
    }
    
    public String getArticleCountInfo(long colID, long docID) {
		//先从Redis中查找
		String key = RedisKey.NIS_EVENT_ARTICLE+ docID;
        long click = getEventCount(key, "c");
        long share = getEventCount(key, "s");
		long discuss = getEventCount(key, "d");
		//如果Redis中没有，从数据库中查
		if (click==0 || share==0 || discuss==0) {
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			int docLibID = LibHelper.getArticleLibID();
			//从栏目中判断是web还是app库
			try {
				int colLibID = LibHelper.getColumnLibID();
				Document col = docManager.get(colLibID, colID);
				if (col.getInt("col_channel")==1) 
					docLibID = LibHelper.getArticleAppLibID();
			} catch (Exception e) {}
			//从数据库中查点击量等值
			try {
				Document doc = docManager.get(docLibID, docID);
				long cc = doc.getLong("a_countClick");
				if(cc>click) click = cc;
				long sc = doc.getLong("a_countShare");
				if(sc>share) share = sc;
				long dc = doc.getLong("a_countDiscuss");
				if(dc>discuss) discuss = dc;
			} catch (Exception e) {}
		}
		JSONObject ret = new JSONObject();
		ret.put("id", docID);
		ret.put("click_count", click);
		ret.put("share_count", share);
		ret.put("comment_count", discuss);
		
		return ret.toString();
	}
	
	private long getEventCount(String key, String field) {
		String count = RedisManager.hget(key, field);
		if (count != null) {
			return Long.parseLong(count);
		}else{
			return 0;
		}
	}

    public String subscribeXY(int siteID, int userID, String columnId, String device) {
        JSONArray jsonArr = new JSONArray();

        List<Column> mycolumnids = null ;
        String records = null ;
        if(userID<1){
            getSubColIDsByDevice(device);
            records = RedisManager.hget(RedisKey.MY_COLUMN_KEY, device);
        }else{
            getSubColIDs(userID);
            records = RedisManager.hget(RedisKey.MY_COLUMN_KEY, userID);
        }
        mycolumnids = getMyColumns(records, Long.parseLong(columnId), siteID);

        for (int i = mycolumnids.size() - 1; i >= 0; i--) {
            Column column = mycolumnids.get(i);

            JSONObject columnInfo = new JSONObject();//端上不用修改
            columnInfo.put("columnId",column.getId());

            if (1 == column.getIsdelete())
                continue;
            if (column.isForbidden())
                continue;

            JSONObject one = new JSONObject();
            one.put("column", columnInfo);

            jsonArr.add(one);
        }
        return jsonArr.toString();
    }

    private static int getInt(int value) {
        if (value < 0)
            return 0;
        return value;
    }

    private static long getlong(long value) {
        if (value < 0)
            return 0;
        return value;
    }
}