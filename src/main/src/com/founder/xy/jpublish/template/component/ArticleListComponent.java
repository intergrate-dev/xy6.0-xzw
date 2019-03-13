package com.founder.xy.jpublish.template.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.xy.column.Column;
import com.founder.xy.column.ColumnReader;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.jpublish.ColParam;
import com.founder.xy.jpublish.JsonHelper;
import com.founder.xy.jpublish.data.PubArticle;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;

/**
 * 组件：稿件列表
 * @author Gong Lijie, Guo Qixun
 */
public class ArticleListComponent extends AbstractComponent implements Component{
	
	//稿件类型常量对应
	protected static final HashMap<String, String> articleTypes = new HashMap<>();
	static {
		articleTypes.put("article", "0");
		articleTypes.put("pic", "1");
		articleTypes.put("video", "2");
		articleTypes.put("special", "3");
		articleTypes.put("link", "4");
		articleTypes.put("multi", "5");
	}
	//稿件属性常量对应
	protected static final HashMap<String, String> articleAttrs = new HashMap<>();
	static {
		articleAttrs.put("COMMON", "63");
		articleAttrs.put("HEADLINE", "62");
		articleAttrs.put("PIC", "61");
		articleAttrs.put("IMPORTANT", "64");
		articleAttrs.put("OTHER", "65");
	}
	//标题图类型常量对应
	protected static final HashMap<String, String> titlePic = new HashMap<>();
	static {
		titlePic.put("a_picBig", "4,5,6,7");
		titlePic.put("a_picMiddle", "2,3,6,7");
		titlePic.put("a_picSmall", "1,3,5,7");
		titlePic.put("a_null", "0");
	}
	
	protected long[] colIDs;
	protected String key;
	protected boolean preview;
	
	protected static final String SQL_NO_ID = "select SYS_DOCUMENTID,a_linkTitle,a_order from DOM_REL_Web where a_status=1 and a_attr in (@ATTR@) and a_hasTitlePic in (@TITLEPIC@) and SYS_DELETEFLAG=0 order by (@ORDER)";
	protected static final String SQL_NO_ID_TYPE = "select SYS_DOCUMENTID,a_linkTitle,a_order from DOM_REL_Web where a_status=1 and a_type in (@TYPE@) and a_hasTitlePic in (@TITLEPIC@) and a_attr in (@ATTR@) and SYS_DELETEFLAG=0 order by (@ORDER)";
	protected static final String SQL_ONE_ID = "select SYS_DOCUMENTID,a_linkTitle,a_order from DOM_REL_Web where CLASS_1=? and a_status=1 and a_attr in (@ATTR@) and a_hasTitlePic in (@TITLEPIC@) and SYS_DELETEFLAG=0 order by (@ORDER)";
	protected static final String SQL_ONE_ID_TYPE = "select SYS_DOCUMENTID,a_linkTitle,a_order from DOM_REL_Web where CLASS_1=? and a_status=1 and a_type in (@TYPE@) and a_hasTitlePic in (@TITLEPIC@) and a_attr in (@ATTR@) and SYS_DELETEFLAG=0 order by (@ORDER)";
	protected static final String SQL_IDS = "select SYS_DOCUMENTID,a_linkTitle,a_order from DOM_REL_Web where CLASS_1 in (@COLIDS@) and a_status=1 and a_type in (@TYPE@) and a_hasTitlePic in (@TITLEPIC@) and a_attr in (@ATTR@) and SYS_DELETEFLAG=0 order by (@ORDER)";

	protected static final String SQL_ONE_ID_ALL = "select count(*) from DOM_REL_Web where CLASS_1=? and a_status=1 and SYS_DELETEFLAG=0";
	protected static final String SQL_ONE_ID_TYPE_ALL = "select count(*) from DOM_REL_Web where CLASS_1=? and a_status=1 and a_type=? and SYS_DELETEFLAG=0";
	protected static final String SQL_IDS_ALL = "select count(*) from DOM_REL_Web where CLASS_1 in (@COLIDS@) and a_status=1 and a_type=? and SYS_DELETEFLAG=0";
	
	public ArticleListComponent(ColParam param,JSONObject comJson){
		this(param, comJson, false);
	}
	
	public ArticleListComponent(ColParam param,JSONObject comJson, boolean preview){
		super(comJson);
		
		this.param = param;
		//当前栏目稿件列表指定的栏目ID（可能有多个）
		colIDs = getColIDs();
		
		//得到redis里保存稿件列表json的key：co.articlelist.<当前页数>.<组件实例ID>.<colIDs>
		key = getKey();
		
		this.preview = preview;
	}
	
	@Override
	public String getComponentResult() throws Exception {
		String value = RedisManager.get(key);
		
		if (preview || needRefresh(value)) {
			getComponentData();
			
			return process();
		} else {
			JSONObject json = new JSONObject(value);
			return JsonHelper.getString(json, "data");
		}
	}
	
	@Override
	protected void getComponentData() {
		List<PubArticle> articles = getListData();
		
		componentData.put("articles", articles);
		
		//当前所在的栏目ID，分页js需要
		componentData.put("currentColumn", param.getColLibID() + "," + param.getColID());
	}

	protected List<PubArticle> getListData() {
		//拼SQL和参数
		List<Object> paramArr = new ArrayList<>();
		
		String sql = getSQLAndFillParams(paramArr);
		Object[] params = paramArr.toArray(new Object[0]);
		
		//查询关联表，得到稿件ID、链接标题、稿件库ID
		int articleLibID = getArticleLibID();
		
		List<Long> docIDs = new ArrayList<>();
		List<String> titles = new ArrayList<>();
		queryArticles(articleLibID, sql, params, docIDs, titles);
		
		//查询稿件库，得到所有数据
		return getArticles(articleLibID, docIDs, titles);
	}
	protected String getSQLAndFillParams(List<Object> params) {
		String type = getTypes();
		String  attrs = getAttrs();
		String order = getOrder();
		String tpic = getTitlePic();
		
		String sql;
		if (colIDs[0] == 0&&colIDs.length == 1) {
			sql = (StringUtils.isBlank(type)) ? SQL_NO_ID : SQL_NO_ID_TYPE;
		} else if (colIDs.length == 1) {
			sql = (StringUtils.isBlank(type)) ? SQL_ONE_ID : SQL_ONE_ID_TYPE;
			params.add(colIDs[0]);
		} else {
			sql = SQL_IDS.replace("@COLIDS@", StringUtils.join(colIDs));
			if (StringUtils.isBlank(type)) {
				sql = sql.replace("and a_type in (@TYPE@)", "");
			}
		}
		if (!StringUtils.isBlank(type)) {
			//替换稿件类型条件
			sql = sql.replace("@TYPE@", type);
		}

		if (StringUtils.isBlank(attrs)) {
			//替换稿件属性条件
			sql = sql.replace("and a_attr in (@ATTR@)", "");
		} else {
			//没有设置稿件属性，去掉条件
			sql = sql.replace("@ATTR@", attrs);
		}
		if (StringUtils.isBlank(tpic)) {
			//替换标题图属性条件
			sql = sql.replace(" and a_hasTitlePic in (@TITLEPIC@)", "");
		} else {
			//没有设置标题图，去掉条件
			sql = sql.replace("@TITLEPIC@", tpic);
		}
		sql = sql.replace("(@ORDER)",order);
		
		//若是预览，则SQL中去掉a_status=1的已发布限制
		if (preview) {
			if (sql.indexOf("and a_status=1") > 0)
				sql = sql.replace("and a_status=1", "");
			else if (sql.indexOf("a_status=1 and") > 0)
				sql = sql.replace("a_status=1 and", "");
			else if (sql.indexOf("where a_status=1") > 0)
				sql = sql.replace("where a_status=1", "");

			//System.out.println("预览时sql:" + sql);
		}
		System.out.println("sql:" + sql);
		return sql;
	}
	private String getTitlePic() {
		StringBuilder result= new StringBuilder();
		if(dataJSON.isNull("titlePic")) return "";
		if(StringUtils.isBlank(JsonHelper.getString(dataJSON, "titlePic")))
			return "";
		String[] pics = JsonHelper.getString(dataJSON, "titlePic").split("\\|");
		for(String titlepics:pics) {
			//仅处理正确标题图，错误标题图不作处理
			if (titlePic.containsKey(titlepics))
				result.append(",").append(titlePic.get(titlepics));
			else return "";
		}
		//截掉开始的逗号
		return result.substring(1);
	}

	protected String getOrder() {
		String order = JsonHelper.getString(dataJSON, "order", "a_order");
		return order;
	}

	protected String process() throws Exception{
		String result = super.process();
		
		//缓存
		JSONObject resultJson = new JSONObject();
		resultJson.put("time", System.currentTimeMillis());
		resultJson.put("data", result);
		if(!preview) { //预览时，图片地址取的内网地址，不放入到redis中
			RedisManager.setLonger(key, resultJson.toString());
		}
		return result;
	}
	//子类调用时，不确定key的值,不在更新redis，由子类进行更新
    protected String processForSub() throws Exception{
        String result = super.process();
        return result;
    }

	/** 当前栏目稿件列表指定的栏目ID（可能有多个）*/
	protected long[] getColIDs(){
		JSONArray columnid = JsonHelper.getArray(dataJSON, "columnid");
	
		Set<Long> columnidSet = new HashSet<Long>();
		if (columnid == null || columnid.length() == 0) {
			columnidSet.add(param.getColID());
		} else {
			for (int i = 0; i < columnid.length(); i++) {
				columnidSet.add(columnid.getLong(i));
			}
		}
		/*
		ColumnReader columnReader = (ColumnReader)Context.getBean("columnReader");
		Column column = null;
		try{
			if (columnid == null || columnid.length() == 0) {
				column = columnReader.get(param.getColLibID(), param.getColID());
			} else {
				column = columnReader.get(param.getColLibID(), columnid.getLong(0));
			}
			columnidSet.add(new Long(column.getId()));
			
			String columntype = dataJSON.getString("columntype");
			
			if ("self".equals(columntype)) {
				columnidSet.add(new Long(column.getId()));
			} else if ("son".equals(columntype)) {
				//子栏目
				List<Column> sons = columnReader.getSub(param.getColLibID(), param.getColID());
				for(Column col : sons){
					columnidSet.add(new Long(col.getId()));
				}
			} else {
				//兄弟栏目：父栏目的子栏目
				List<Column> brothers = columnReader.getSub(param.getColLibID(), column.getParentID());
				for(Column col : brothers) {
					columnidSet.add(new Long(col.getId()));
				}
			}
		}catch (E5Exception e) {
			e.printStackTrace();
		}
		*/
		int i = 0;
		long[] result = new long[columnidSet.size()];
		
		for (long colID : columnidSet) {
			result[i++] = colID;
		}
		return result;
	}
	
	protected void queryArticles(int articleLibID, String sql, Object[] params, 
			List<Long> docIDs, List<String> titles) {
		int count = getCount(); //读的个数
		int start = getStart(count); //起始条数
		
		DBSession db = null;
		IResultSet rs = null;
		try {
			String tableName = getRelTableName(articleLibID); //取稿件库对应的关联表名
			sql = sql.replace("DOM_REL_Web", tableName); //替换为实际的关联表名
			
			db = InfoHelper.getDBSession(articleLibID);
			
			sql = db.getDialect().getLimitString(sql, start, count);
			
			rs = db.executeQuery(sql, params);
			while (rs.next()) {
				docIDs.add(rs.getLong("SYS_DOCUMENTID"));
				titles.add(rs.getString("a_linkTitle"));
			}
		} catch (Exception e) {
			System.out.println("ArticleListComponent.queryArticles exception:" + e.getLocalizedMessage() + ".SQL:" + sql);
			if (params != null) {
				for (int i = 0; i < params.length; i++) {
					System.out.println("param" + i + ":" + params[i]);
				}
			}
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(db);
		}
	}

	protected int getStart(int count) {
		int start = JsonHelper.getInt(dataJSON, "start", 0);
		
		//处理翻页
		if (param.getPage() > 0) {
			start = (param.getPage() - 1) * count;
		}
		return start;
	}
	
	/** 需要读出的个数 */
	protected int getCount() {
		return JsonHelper.getInt(dataJSON, "count", 10);
	}
	
	protected long queryPages(String sql, Object[] params) {
		long count = 0;
		
		DBSession db = null;
		IResultSet rs = null;
		try {
			int articleLibID = getArticleLibID();
			String tableName = getRelTableName(articleLibID); //取稿件库对应的关联表名
			sql = sql.replace("DOM_REL_Web", tableName); //替换为实际的关联表名
			
			db = InfoHelper.getDBSession(articleLibID);
			
			rs = db.executeQuery(sql, params);
			if (rs.next()) count = rs.getLong(1);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(sql);
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(db);
		}
		return count;
	}
	
	protected List<PubArticle> getArticles(int articleLibID, List<Long> docIDs, List<String> titles) {
		long[] idArr = InfoHelper.getLongArray(docIDs);

		int attLibID = LibHelper.getLibIDByOtherLib(DocTypes.ATTACHMENT.typeID(), articleLibID);
		int channel = super.getChannel();
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		ColumnReader columnReader = (ColumnReader)Context.getBean("columnReader");
		try {
			
			long[] colIDs = getColIDs(); 
			//读出当前栏目的发布地址
			Column currentCol = columnReader.get(param.getColLibID(), colIDs[0]);
			
			//读出稿件
			Document[] docs = docManager.get(articleLibID, idArr);
			
			List<PubArticle> articles = new ArrayList<>();
			//组装稿件列表，替换链接标题、栏目名、栏目发布地址等
			for (int i = 0; i < docs.length; i++) {
				PubArticle a = new PubArticle(docs[i]);
				
				a.setTitle(titles.get(i));		//改为链接标题
				//a.setContent(null); 			//稿件列表不需要提供内容，清掉以缩减大小
				setPicUrl(a, attLibID, channel);//标题图片的url
				
				if(currentCol != null) {
					
					String[] currentUrls = columnReader.getUrls(currentCol.getLibID(), currentCol.getId());
					a.setCurrentColName(currentCol.getName());
					a.setCurrentColIcon(currentCol.getIconBig());
					setColumnNameUrls(columnReader, channel, currentUrls, a);

					//增加扩展字段
					HashMap<String, String> extFields = getExtFields(a,currentCol);
					a.setExtFields(extFields);
				}
				//填写栏目名、栏目发布地址等信息

				articles.add(a);
			}
			return articles;
		} catch (E5Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 填写栏目名、栏目发布地址等信息
	 */
	protected void setColumnNameUrls(ColumnReader columnReader, int channel, String[] currentUrls, PubArticle a) {
		try {
			Column column = columnReader.get(param.getColLibID(), a.getColumnID());
			if (column == null) {
				//主栏目为空时，设置成当前栏目
				column = columnReader.get(param.getColLibID(), param.getColID());
				System.out.printf("稿件" + a.getId() + "主栏目（栏目id  " + a.getColumnID() + " ）为空!!请检查栏目是否已被删除");
			}
			a.setColumn(column.getName());    //栏目的名字为主栏目名
			//稿件所在栏目的发布地址
			String[] masterUrls = columnReader.getUrls(column.getLibID(), column.getId());

			if (channel != 0) channel = 1;

			a.setMasterColUrl(masterUrls[channel]);
			a.setMasterColIcon(column.getIconBig());
			if ((long) a.getColumnID() == param.getColID()) {
				a.setCurrentColUrl(masterUrls[channel]);
			} else {
				a.setCurrentColUrl(currentUrls[channel]);
			}
		} catch (E5Exception e) {
			e.printStackTrace();
		}
	}
	//设置图片的url
	protected void setPicUrl(PubArticle a, int attLibID, int channel) {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			Document[] atts = docManager.find(attLibID, "att_articleID=? and att_articleLibID=?",
					new Object[]{a.getId(), a.getDocLibID()});
			if (atts != null) {
				for (Document att : atts) {
					String url = (channel == 0) ? att.getString("att_url") : att.getString("att_urlPad");
					
					String path = att.getString("att_path");
					//预览：图片用未发布地址
					if (preview) {
						if (!path.startsWith("http"))
							url = "../../xy/image.do?path=" + path; 
					}
					int type = att.getInt("att_type");
					if (type == 2) a.setPicBig(url);
					else if (type == 3) a.setPicMiddle(url);
					else if (type == 4) a.setPicSmall(url);
					//替换正文中的图片地址
					else if(type==0){
						String content = a.getContent().replace("../../xy/image.do?path="+path, url);
						a.setContent(content);
					}
				}
			}
		} catch (E5Exception e) {
			e.printStackTrace();
		}
	}
	

	//查询用的稿件类型参数
	protected String getTypes() {
		String[] articletypes = JsonHelper.getString(dataJSON, "articletype").split("\\|");
		if(StringUtils.isBlank(JsonHelper.getString(dataJSON, "articletype")))
			return "";
		StringBuilder result = new StringBuilder();
		for(String articletype:articletypes) {
			if ("all".equals(articletype))
				return "";
			else
				result.append(",").append(articleTypes.get(articletype));
		}
		//截掉开始的逗号
		return result.substring(1);
	}
	
	//查询用的稿件属性参数
	protected String getAttrs() {
		StringBuilder result= new StringBuilder();
		if(dataJSON.isNull("article_attr")) return "";
		if(StringUtils.isBlank(JsonHelper.getString(dataJSON, "article_attr")))
			return "";
		String[] attrs = JsonHelper.getString(dataJSON, "article_attr").split("\\|");
		for(String articleAttr:attrs) {
			if ("all".equals(articleAttr))
				return "";
			else if (articleAttrs.containsKey(articleAttr))
				result.append(",").append(articleAttrs.get(articleAttr));
			else result.append(",").append(articleAttr);
		}
		//截掉开始的逗号
		return result.substring(1);
	}
	
	//得到redis里保存稿件列表json的key：co.articlelist.<当前页数>.<组件实例ID>.<指定栏目ID的串>
	protected String getKey() {
		String key = RedisKey.CO_ARTICLELIST_KEY;
		if (param != null) {
			key += "." + param.getPage();  //其中0是模板生成（第一）页时，>=1是翻页js调用时传入
		} else {
			key += ".0";
		}
		
		String idValue = StringUtils.join(colIDs);
		String field = comID + "." + idValue;
		
		key += "." + field;
		
		return key;
	}

	/**
	 * 稿件列表/分页稿件列表的是否有栏目更新的判断
	 */
	protected boolean needRefresh(String value){
		boolean needrRefreash = false;
		
		if (!StringUtils.isBlank(value)) {
			long coRefreashTime = (new JSONObject(value)).getLong("time");
			
			for (long colID : colIDs) {
				if(colID==0 && colIDs.length==1) {
					return true;
				}
				//判断每个栏目的更新时间
				String time = RedisManager.hget(RedisKey.COLUMNS_TIME_KEY, colID);
				if (time != null) {
					if (Long.parseLong(time) > coRefreashTime) {
						return true;
					}
				}
			}
		} else {
			needrRefreash = true;
		}
		return needrRefreash;
	}
}