package com.founder.xy.jpublish.template.component;

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
import com.founder.xy.jpublish.data.AdvColumnList;
import com.founder.xy.jpublish.data.BareArticle;
import com.founder.xy.jpublish.data.PubArticle;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

/**
 * 高级栏目组件
 * Created by Wenkx on 2017/3/6.
 */
public class AdvColumnListComponent extends AbstractComponent implements Component {

    //TODO 将部分数据存到redis中？
    //稿件类型常量对应
    private static final HashMap<String, Integer> articleTypes = new HashMap<>();

    static {
        articleTypes.put("article", 0);
        articleTypes.put("pic", 1);
        articleTypes.put("video", 2);
        articleTypes.put("special", 3);
        articleTypes.put("link", 4);
        articleTypes.put("multi", 5);
    }

    //稿件属性常量对应
    protected static final HashMap<String, String> articleAttrs = new HashMap();
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
    protected boolean preview;

    protected static final String SQL = "select SYS_DOCUMENTID,a_linkTitle,a_order from DOM_REL_Web where a_status=1  and a_type in (@TYPE@) and a_hasTitlePic in (@TITLEPIC@) and CLASS_1= @COLIDS@ and a_attr in (@ATTR@) order by (@ORDER)";

    public AdvColumnListComponent(ColParam param, JSONObject comJson) {
        this(param,comJson,false);
    }


    public AdvColumnListComponent(ColParam param, JSONObject comJson, boolean preview) {
        super(comJson);
        this.param = param;
        this.preview = preview;
    }

    @Override
    public String getComponentResult() throws Exception {
        getComponentData();
        return process();
    }

    @Override
    protected void getComponentData() {
        long colID = getColID();
        String columntype = JsonHelper.getString(dataJSON, "columntype");
        List<AdvColumnList> currentCols = new ArrayList<>();
        List<Column> cols = getColumnsByDefault(columntype, colID);
        colsToAdvColumns(currentCols, cols);
        componentData.put("columns", currentCols);
    }

    //模板中没指定组件的栏目ID时，取栏目列表
    private List<Column> getColumnsByDefault(String columntype, long colID) {
        ColumnReader columnReader = (ColumnReader) Context.getBean("columnReader");
        Column column;
        try {
            column = columnReader.get(param.getColLibID(), colID);
            if (column == null) return null;
            //按数据类型取不同的栏目
            if (StringUtils.isBlank(columntype) || "self".equals(columntype)) {
                List<Column> columns = new ArrayList<>();
                columns.add(column);
                return columns;
            } else if ("son".equals(columntype)) {
                return columnReader.getSub(param.getColLibID(), colID);
            } else if ("root".equals(columntype)) {
                String casIDs = column.getCasIDs();
                long rootColID = Long.parseLong(casIDs.contains("~") ? casIDs.substring(0, casIDs.indexOf("~")) : casIDs);
                List<Column> columns = new ArrayList<>();
                columns.add(columnReader.get(param.getColLibID(), rootColID));
                return columns;
            } else if ("parent".equals(columntype)) {
                if (column.getParentID() > 0) {
                    List<Column> columns = new ArrayList<>();
                    columns.add(columnReader.get(param.getColLibID(), column.getParentID()));
                    return columns;
                } else
                    return null;
            } else {
                //brother
                if (column.getParentID() > 0)
                    return columnReader.getSub(param.getColLibID(), column.getParentID());
                else
                    return columnReader.getRoot(column.getSiteID(), param.getColLibID(), column.getChannel());
            }
        } catch (E5Exception e) {
            e.printStackTrace();
            return null;
        }
    }

/*    private List<AdvColumnList> getSubCol(long colID, int level) {
        if (level > 0) {
            List<AdvColumnList> subColumns = doGetSub(colID);
            level--;
            List<AdvColumnList> nowLevel = subColumns;
            List<AdvColumnList> nextLevel = new ArrayList<>();
            while (level > 0 && nowLevel.size() > 0) {
                for (AdvColumnList subColumn : nowLevel) {
                    List<AdvColumnList> nextSubColumns = doGetSub(subColumn.getId());
                    subColumn.setSubColumns(nextSubColumns);
                    nextLevel.addAll(nextSubColumns);
                }
                nowLevel = nextLevel;
                level--;
            }
            return subColumns;
        }
        return null;
    }*/

    protected long getColID(){
        JSONArray columnid = JsonHelper.getArray(dataJSON, "columnid");
        if (columnid == null || columnid.length() == 0) {
           return param.getColID();
        } else {
            return columnid.getLong(0);
            }
    }


/*    private List<AdvColumnList> doGetSub(long colID) {
        //1 取得子栏目列表
        List<AdvColumnList> subColumns = new ArrayList<>();
        ColumnReader columnReader = (ColumnReader) Context.getBean("columnReader");
        try {
            List<Column> columns = columnReader.getSub(param.getColLibID(), colID);
            colsToAdvColumns(subColumns, columns);
        } catch (E5Exception e) {
            e.printStackTrace();
        }
        return subColumns;
    }*/

    private void colsToAdvColumns(List<AdvColumnList> advColumnLists, List<Column> columns) {
        // 移除被禁用的栏目
        removeFobidden(columns);
        setColumnUrl(columns);
        if(columns==null) return;
        for (Column column : columns) {
            AdvColumnList advColumnList = new AdvColumnList();
            // 组装成高级栏目对象
            copyColumnInfo(advColumnList, column);
            // 栏目循环取稿件列表（部分信息需要代码生成）
            List<PubArticle> bareArticles = getListData(column.getId());
            advColumnList.setArticles(bareArticles);
            advColumnLists.add(advColumnList);
        }
    }

    private void copyColumnInfo(AdvColumnList advColumnList, Column column) {
        advColumnList.setId(column.getId());
        advColumnList.setLibID(column.getLibID());
        advColumnList.setName(column.getName());
        advColumnList.setKeyword(column.getKeyword());
        advColumnList.setDescription(column.getDescription());
        advColumnList.setChannel(column.getChannel());
        advColumnList.setCasIDs(column.getCasIDs());
        advColumnList.setUrl(column.getUrl());
        advColumnList.setUrlPad(column.getUrlPad());
    }

    private void removeFobidden(List<Column> columns) {
        if (columns == null) return;

        int i = 0;
        while (i < columns.size()) {
            Column col = columns.get(i);
            if (col.isForbidden())
                columns.remove(i);
            else
                i++;
        }
    }

    private List<PubArticle> getListData(long colID) {
        String type = getTypes();
        String  attrs = getAttrs();
        String order = getOrder();
        String tpic = getTitlePic();
        //拼SQL和参数
        Object[] params = null;

        String sql = SQL.replace("@COLIDS@", colID + "");
        if (StringUtils.isBlank(type)) {
            sql = sql.replace("and a_type in (@TYPE@)", "");
        } else {
            sql = sql.replace("@TYPE@", type);
        }
        if (StringUtils.isBlank(attrs)) {
            sql = sql.replace("and a_attr in (@ATTR@)", "");
        } else {
            sql = sql.replace("@ATTR@", attrs);
        }
        if (StringUtils.isBlank(tpic)) {
			//替换标题图属性条件
			sql = sql.replace(" and a_hasTitlePic in (@TITLEPIC@)", "");
		} else {
			//没有设置标题图，去掉条件
			sql = sql.replace("@TITLEPIC@", tpic);
		}
        sql = sql.replace("(@ORDER)", order);

        //若是预览，则SQL中去掉a_status=1的已发布限制
        if (preview) {
            if (sql.indexOf("and a_status=1") > 0)
                sql = sql.replace("and a_status=1", "");
            else if (sql.indexOf("a_status=1 and") > 0)
                sql = sql.replace("a_status=1 and", "");
            //System.out.println("预览时sql:" + sql);
        }
        System.out.println("sql:" + sql);

        //查询关联表，得到稿件ID、链接标题、稿件库ID
        int articleLibID = getArticleLibID();
        List<Long> docIDs = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        queryArticles(articleLibID, sql, params, docIDs, titles);
        //查询稿件库，得到所有数据
        return getArticles(articleLibID, docIDs, titles,colID);
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


	private void queryArticles(int articleLibID, String sql, Object[] params,
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
            e.printStackTrace();
            System.out.println(sql);
        } finally {
            ResourceMgr.closeQuietly(rs);
            ResourceMgr.closeQuietly(db);
        }
    }

    protected List<PubArticle> getArticles(int articleLibID, List<Long> docIDs, List<String> titles, long colID) {
        long[] idArr = InfoHelper.getLongArray(docIDs);

        int attLibID = LibHelper.getLibIDByOtherLib(DocTypes.ATTACHMENT.typeID(), articleLibID);
        int channel = super.getChannel();

        DocumentManager docManager = DocumentManagerFactory.getInstance();
        ColumnReader columnReader = (ColumnReader) Context.getBean("columnReader");
        try {
            //读出当前栏目的发布地址
            Column currentCol = columnReader.get(param.getColLibID(), colID);

            //读出稿件
            Document[] docs = docManager.get(articleLibID, idArr);

            List<PubArticle> articles = new ArrayList<>();
            //组装稿件列表，替换链接标题、栏目名、栏目发布地址等
            for (int i = 0; i < docs.length; i++) {
            	PubArticle a = new PubArticle(docs[i]);

                a.setTitle(titles.get(i));        //改为链接标题
                //a.setContent(null); 			//稿件列表不需要提供内容，清掉以缩减大小
                setPicUrl(a, attLibID, channel);//标题图片的url

                if (currentCol != null) {

                    String[] currentUrls = columnReader.getUrls(currentCol.getLibID(), currentCol.getId());
                    a.setCurrentColName(currentCol.getName());
                    a.setCurrentColIcon(currentCol.getIconBig());
                    setColumnNameUrls(columnReader, channel, currentUrls, a);
                }
                //填写栏目名、栏目发布地址等信息
                
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

    //设置图片的url
    protected void setPicUrl(BareArticle a, int attLibID, int channel) {
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
                    else if (type == 0) {


                        String content = a.getContent().replace("../../xy/image.do?path=" + path, url);
                        a.setContent(content);
                    }
                }
            }
        } catch (E5Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 填写栏目名、栏目发布地址等信息
     */
    private void setColumnNameUrls(ColumnReader columnReader, int channel, String[] currentUrls, BareArticle a) {
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

    private String getOrder() {
        return JsonHelper.getString(dataJSON, "order", "a_order");
    }

    protected int getStart(int count) {
        int start = JsonHelper.getInt(dataJSON, "start", 0);

        //处理翻页
        if (param.getPage() > 0) {
            start = (param.getPage() - 1) * count;
        }
        return start;
    }

    /**
     * 需要读出的个数
     */
    protected int getCount() {
        return JsonHelper.getInt(dataJSON, "count", 10);
    }


}