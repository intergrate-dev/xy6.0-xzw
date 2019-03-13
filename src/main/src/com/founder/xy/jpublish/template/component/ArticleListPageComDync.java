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
import com.founder.xy.jpublish.data.BareArticle;
import com.founder.xy.jpublish.data.PubArticle;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;
import com.founder.xy.set.ExtField;
import com.founder.xy.set.ExtFieldReader;

/**
 * 组件：（动态）分页稿件列表
 * @author Gong Lijie
 */
public class ArticleListPageComDync extends AbstractComponent implements Component{
	
	//稿件类型常量对应
	protected static final HashMap<String, Integer> articleTypes = new HashMap<String, Integer>();
	static {
		articleTypes.put("article", 0);
		articleTypes.put("pic", 1);
		articleTypes.put("video", 2);
		articleTypes.put("special", 3);
		articleTypes.put("link", 4);
		articleTypes.put("multi", 5);
	}
	protected long[] colIDs;
	protected String key;
	protected boolean preview;
	
	protected static final String SQL_ONE_ID = "select SYS_DOCUMENTID,a_linkTitle,a_order from DOM_REL_Web where CLASS_1=? and a_status=1 and SYS_DELETEFLAG=0 order by a_order";
	protected static final String SQL_ONE_ID_TYPE = "select SYS_DOCUMENTID,a_linkTitle,a_order from DOM_REL_Web where CLASS_1=? and a_status=1 and a_type=? and SYS_DELETEFLAG=0 order by a_order";
	protected static final String SQL_IDS = "select SYS_DOCUMENTID,a_linkTitle,a_order from DOM_REL_Web where CLASS_1 in (@COLIDS@) and a_status=1 and a_type=? and SYS_DELETEFLAG=0 order by a_order";

	protected static final String SQL_ONE_ID_ALL = "select count(*) from DOM_REL_Web where CLASS_1=? and a_status=1 and SYS_DELETEFLAG=0";
	protected static final String SQL_ONE_ID_TYPE_ALL = "select count(*) from DOM_REL_Web where CLASS_1=? and a_status=1 and a_type=? and SYS_DELETEFLAG=0";
	protected static final String SQL_IDS_ALL = "select count(*) from DOM_REL_Web where CLASS_1 in (@COLIDS@) and a_status=1 and a_type=? and SYS_DELETEFLAG=0";
	
	public ArticleListPageComDync(ColParam param,JSONObject comJson){
		this(param, comJson, false);
	}
	public ArticleListPageComDync(ColParam param,JSONObject comJson, boolean preview){
		super(comJson);
		
		this.param = param;
		this.preview = preview;
		
		colIDs = getColIDs();
		key = getKey();
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
	
	/**
	 * 给分页稿件列表使用的方法。并不是模板套用，而是js方式访问时，只读数据，不需要套模板
	 */
	public String readPageData() {
		String value = RedisManager.get(key);
		
		//若栏目都没更新，则不需要刷新，从缓存中取即可
		if (needRefresh(value)) {
			//若栏目刷新了，或缓存中无数据，则读一次数据，放入缓存
			getComponentData();
			
			long time = System.currentTimeMillis();
			
			JSONObject result = new JSONObject();
			result.put("time", time); //最新读数据的时间
			
			result.put("articles", getData("articles"));//当前页的稿件列表
			result.put("page", param.getPage());	//当前页数
			result.put("pages", getData("pages")); //总页数
			
			value = result.toString();
			
			//加入缓存
			RedisManager.setLonger(key, value);
		}
		return value;
	}

	@Override
	protected void getComponentData() {
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
		List<PubArticle> articles = getArticles(articleLibID, docIDs, titles);
		
		componentData.put("articles", articles);
		
		//取总页数
		getPages();
		
		//当前所在的栏目ID，分页js需要
		componentData.put("currentColumn", param.getColLibID() + "," + param.getColID());
	}
	protected String getSQLAndFillParams(List<Object> params) {
		int type = getTypes();
		
		//拼SQL和参数
		String sql = null;
		if (colIDs.length == 1) {
			sql = (type < 0) ? SQL_ONE_ID : SQL_ONE_ID_TYPE;
			params.add(colIDs[0]);
		} else {
			sql = SQL_IDS.replace("@COLIDS@", StringUtils.join(colIDs));
			if (type < 0) {
				sql = sql.replace("and a_type=?", "");
			}
		}
		if (type >= 0) {
			params.add(type);
		}
		
		//若是预览，则SQL中去掉a_status=1的已发布限制
		if (preview) {
			if (sql.indexOf("and a_status=1") > 0)
				sql.replace("and a_status=1", "");
			else if (sql.indexOf("a_status=1 and") > 0)
				sql.replace("a_status=1 and", "");
			else if (sql.indexOf("where a_status=1") > 0)
				sql.replace("where a_status=1", "");

			//System.out.println("ArticleListPageComDync预览时sql:" + sql);
		}
		return sql;
	}
	
	protected String process() throws Exception{
		String result = super.process();
		
		//缓存
		JSONObject resultJson = new JSONObject();
		resultJson.put("time", System.currentTimeMillis());
		resultJson.put("data", result);
		if(!preview) {//预览时，图片地址取的内网地址，不放入到redis中
			RedisManager.setLonger(key, resultJson.toString());
		}
		return result;
	}

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

	//取总页数
	protected void getPages() {
		//检查showall参数
		//if (!dataJSON.has("showall") || !"true".equals(dataJSON.getString("showall")))
		//	return;
		
		int type = getTypes();
		
		//拼SQL和参数
		String sql = null;
		Object[] params = null;
		if (colIDs.length == 1) {
			sql = (type < 0) ? SQL_ONE_ID_ALL : SQL_ONE_ID_TYPE_ALL;
			params = (type < 0) ? new Object[]{colIDs[0]} : new Object[]{colIDs[0], type};
		} else {
			sql = SQL_IDS_ALL.replace("@COLIDS@", StringUtils.join(colIDs));
			if (type < 0) {
				sql = sql.replace("and a_type=?", "");
			} else {
				params = new Object[]{type};
			}
		}
		long all = queryPages(sql, params);
		
		int pageCount = JsonHelper.getInt(dataJSON, "count", 10);
		
		long pages = (long)Math.ceil((double)all / pageCount);
		
		componentData.put("pages", pages);
	}
	
	protected void queryArticles(int articleLibID, String sql, Object[] params, 
			List<Long> docIDs, List<String> titles) {
		int start = JsonHelper.getInt(dataJSON, "start", 0);
		int count = JsonHelper.getInt(dataJSON, "count", 10); //每页条数
		
		//处理翻页
		if (param.getPage() > 0) {
			start = (param.getPage() - 1) * count;
		}
		
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
			System.out.println("ArticleListPageComDync.queryArticles exception:" + e.getLocalizedMessage() + ".SQL:" + sql);
			System.out.println(sql);
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
			//读出当前栏目的发布地址
			Column currentCol = columnReader.get(param.getColLibID(), param.getColID());
			String[] currentUrls = columnReader.getUrls(currentCol.getLibID(), currentCol.getId());
			
			//读出稿件
			Document[] docs = docManager.get(articleLibID, idArr);
			
			List<PubArticle> articles = new ArrayList<>();
			//组装稿件列表，替换链接标题、栏目名、栏目发布地址等
			for (int i = 0; i < docs.length; i++) {
				PubArticle a = new PubArticle(docs[i]);
				
				a.setTitle(titles.get(i));		//改为链接标题
				a.setContent(null); 			//稿件列表不需要提供内容，清掉以缩减大小
				setPicUrl(a, attLibID, channel);//标题图片的url
				
				//填写栏目名、栏目发布地址等信息
				setColumnNameUrls(columnReader, channel, currentUrls, a);
				
				//增加扩展字段
				HashMap<String, String> extFields = getExtFields(a,currentCol);
				a.setExtFields(extFields);
				
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
	protected void setColumnNameUrls(ColumnReader columnReader, int channel, String[] currentUrls, BareArticle a) {
		try {
			Column column = columnReader.get(param.getColLibID(), a.getColumnID());
			if (column == null) {
				//主栏目为空时，设置成当前栏目
				column = columnReader.get(param.getColLibID(), param.getColID());
				System.out.printf("稿件" + a.getId() + "主栏目（栏目id  " + a.getColumnID() + " ）为空!!请检查栏目是否已被删除");
			}
			a.setColumn(column.getName());    //修改栏目的名字为当前稿件栏目名
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
	//设置标题图片的url
	protected void setPicUrl(BareArticle a, int attLibID, int channel) {
		if (StringUtils.isBlank(a.getPicBig())
				&& StringUtils.isBlank(a.getPicMiddle())
				&& StringUtils.isBlank(a.getPicSmall()))
			return;
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			Document[] atts = docManager.find(attLibID, "att_articleID=? and att_articleLibID=? and att_type>=2",
					new Object[]{a.getId(), a.getDocLibID()});
			if (atts != null) {
				for (Document att : atts) {
					String url = (channel == 0) ? att.getString("att_url") : att.getString("att_urlPad");
					
					//预览：图片用未发布地址
					if (preview) {
						String path = att.getString("att_path");
						if (!path.startsWith("http"))
							url = "../../xy/image.do?path=" + path; 
					}
					
					int type = att.getInt("att_type");
					
					if (type == 2) a.setPicBig(url);
					else if (type == 3) a.setPicMiddle(url);
					else if (type == 4) a.setPicSmall(url);
				}
			}
		} catch (E5Exception e) {
			e.printStackTrace();
		}
	}
	

	//查询用的稿件类型参数
	protected int getTypes() {
		String articletype = JsonHelper.getString(dataJSON, "articletype");
		
		if ("all".equals(articletype))
			return -1;
		else
			return articleTypes.get(articletype);
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
