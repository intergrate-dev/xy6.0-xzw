package com.founder.xy.article.web;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.founder.e5.dom.DocLib;
import com.founder.xy.topics.dao.TopicsDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.Log;
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
import com.founder.e5.web.SysUser;
import com.founder.e5.web.WebUtil;
import com.founder.e5.workspace.ProcHelper;
import com.founder.e5.workspace.app.form.LogHelper;
import com.founder.xy.article.Article;
import com.founder.xy.article.ArticleManager;
import com.founder.xy.column.Column;
import com.founder.xy.column.ColumnReader;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.jms.PublishTrigger;
import com.founder.xy.jms.data.DocIDMsg;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;

import net.sf.json.JSONObject;

/**
 * 文章列表排序相关
 *
 * @author guzm
 */
@Controller
@RequestMapping("/xy/articleorder")
public class ArticleOrderController {
    @Autowired
    private ColumnReader colReader;
    @Autowired
    ArticleManager articleManager;

    @Autowired
    TopicsDAO topicsDAO;

    // 置顶需要的固定参数
    public static final double ORDER_STEP = 0.0000000005;
    protected Log log = Context.getLog("xy");

    /**
     * 用于更新排序按钮触发事件的跳转
     *
     * @param request
     * @param model
     * @param params
     * @return &DocLibID=6&FVID=6&UUID=1432186861306&siteID=1&blockID=5
     */
    @RequestMapping(value = "forward.do")
    public String forward(
            HttpServletRequest request, Model model,
            String params, String UUID, String DocLibID, String colID,
            String blockID, String groupID) {
        model.addAttribute("params", "xy/articleorder/update" + params
                + "Order"); // 转发请求的路径
        model.addAttribute("DocLibID", DocLibID); //
        List<String> ids = new ArrayList<>();
        ids.add(colID);
        ids.add(blockID);
        ids.add(groupID);
        model.addAttribute("colID", getValuableParam(ids)); // 栏目id
        model.addAttribute("UUID", UUID); // 栏目id
        return "/xy/UpdateOrder";
    }

    public String getValuableParam(List<String> data) {
        for (String metadata : data) {
            if (metadata != null && !"".equals(metadata)) {
                return metadata;
            }
        }
        return null;
    }

    /**
     * 发布库稿件更新排序
     */
    @RequestMapping(value = "updateArticleOrder.do", method = RequestMethod.POST)
    public void updateArticleOrder(
            HttpServletRequest request,
            HttpServletResponse response, int DocLibID, String jsonStr,
            String colID) {
        JSONObject json = new JSONObject();
        try {
            updateOrder(request, DocLibID, jsonStr, colID, "CLASS_1", "a_order",
                        DocTypes.COLUMN.typeID());
            json.put("status", 1);
        } catch (Exception e) {
            json.put("status", 0);
            json.put("info", "更新失败！请稍后再尝试！");
            json.put("stacktrace", e);
            log.error(e);

            e.printStackTrace();
        }

        // 向前台传返回值
        InfoHelper.outputJson(json.toString(), response);
    }

    /**
     * 页面区块稿件 更新顺序
     */
    @RequestMapping("updateBlockOrder.do")
    public void updateBlockOrder(
            HttpServletRequest request,
            HttpServletResponse response, int DocLibID, String jsonStr,
            String colID) {
        JSONObject json = new JSONObject();
        try {
            updateOrder(request, DocLibID, jsonStr, colID, "ba_blockID",
                        "ba_order", DocTypes.BLOCK.typeID());
            json.put("status", 1);
        } catch (Exception e) {
            json.put("status", 0);
            json.put("info", "更新失败！请稍后再尝试！");
            json.put("stacktrace", e);
            e.printStackTrace();
            log.error(e);
        }
        InfoHelper.outputJson(json.toString(), response);
    }

    /**
     * 微信菜单稿件 更新顺序
     */
    @RequestMapping("updateWXOrder.do")
    public void updateWxOrder(
            HttpServletRequest request,
            HttpServletResponse response, int DocLibID, String jsonStr,
            String colID) {
        JSONObject json = new JSONObject();
        try {
            updateOrder(request, DocLibID, jsonStr, colID, "wx_menuID",
                        "wx_order", DocTypes.WXMENU.typeID());
            json.put("status", 1);
        } catch (Exception e) {
            json.put("status", 0);
            json.put("info", "更新失败！请稍后再尝试！");
            json.put("stacktrace", e);
            e.printStackTrace();
            log.error(e);
        }
        InfoHelper.outputJson(json.toString(), response);
    }


    @RequestMapping("updatePaperLayoutOrder.do")
    public void updatePaperLayoutOrder(
            HttpServletRequest request,
            HttpServletResponse response, int DocLibID, String jsonStr,
            String colID) {
        JSONObject json = new JSONObject();
        try {
        updateOrder(request, DocLibID, jsonStr, colID, "pl_paperID",
                    "pl_order", DocTypes.PAPERLAYOUT.typeID());
        } catch (Exception e) {
            log.error(e);
            json.put("status", 0);
            json.put("info", "更新失败！请稍后再尝试！");
            json.put("stacktrace", e);
            e.printStackTrace();
        }
        InfoHelper.outputJson(json.toString(), response);
    }

    /**
     * 报纸稿件 更新顺序
     */
    @RequestMapping("updatePaperOrder.do")
    public void updatePaperOrder(
            HttpServletRequest request,
            HttpServletResponse response, int DocLibID, String jsonStr,
            String colID) {
        JSONObject json = new JSONObject();
        try {
            updateOrder(request, DocLibID, jsonStr, colID, "a_layoutID",
                        "a_order", DocTypes.PAPERARTICLE.typeID());
        } catch (Exception e) {
            log.error(e);
            json.put("status", 0);
            json.put("info", "更新失败！请稍后再尝试！");
            json.put("stacktrace", e);
            e.printStackTrace();
        }
        InfoHelper.outputJson(json.toString(), response);
    }

    /**
     * 选题 更新顺序
     */
    @RequestMapping("updateTopicOrder.do")
    public void updateTopicOrder(
            HttpServletRequest request,
            HttpServletResponse response, int DocLibID, String jsonStr,
            String colID) {
        JSONObject json = new JSONObject();
        try {
        updateOrder(request, DocLibID, jsonStr, colID, null, "t_order", -1);
    } catch (Exception e) {
        log.error(e);
        json.put("status", 0);
        json.put("info", "更新失败！请稍后再尝试！");
        json.put("stacktrace", e);
        e.printStackTrace();
    }
        InfoHelper.outputJson(json.toString(), response);
    }
    
    /**
     * 扩展字段 更新顺序
     */
    @RequestMapping("updateExtFieldOrder.do")
    public void updateExtFieldOrder(
            HttpServletRequest request, HttpServletResponse response, int DocLibID, String jsonStr,
            String colID) {
        JSONObject json = new JSONObject();
        try {
            updateOrder(request, DocLibID, jsonStr, colID, "ext_groupID", "ext_order", DocTypes.EXTFIELD.typeID());
            PublishTrigger.otherData(DocLibID, Integer.parseInt(colID), DocIDMsg.TYPE_EXTFIELD);
        } catch (Exception e) {
            log.error(e);
            json.put("status", 0);
            json.put("info", "更新失败！请稍后再尝试！");
            json.put("stacktrace", e);
            e.printStackTrace();
        }
        InfoHelper.outputJson(json.toString(), response);
    }

    /**
     * 自定义模块项 更新顺序
     */
   @RequestMapping("updateModuleItemOrder.do")
    public void updateModuleItemOrder(
            HttpServletRequest request,
            HttpServletResponse response, int DocLibID, String jsonStr,
            String colID) {
        JSONObject json = new JSONObject();
        try {
        updateOrder(request, DocLibID, jsonStr, colID, "cmi_moduleID",
                    "cmi_order", DocTypes.COLMODULEITEM.typeID());
        } catch (Exception e) {
            log.error(e);
            json.put("status", 0);
            json.put("info", "更新失败！请稍后再尝试！");
            json.put("stacktrace", e);
            e.printStackTrace();
        }
        InfoHelper.outputJson(json.toString(), response);
    }
    
    /**
     * 获取日志信息
     */
    private String findColumnName(String colID) throws Exception {
        String sql = "SELECT  xc.col_name FROM xy_column xc  WHERE xc.SYS_DOCUMENTID = ?";
        DBSession conn = null;
        IResultSet rs = null;
        String colName = null;

        try {
            conn = Context.getDBSession();
            rs = conn.executeQuery(sql, new Object[]{Long.parseLong(colID)});
            // 查询最小order的值
            if (rs.next()) {
                colName = rs.getString("col_name");
            }
        } catch (SQLException e) {
            throw new E5Exception(e);
        } finally {
            ResourceMgr.closeQuietly(rs);
            ResourceMgr.closeQuietly(conn);
        }
        return colName;
    }

    /**
     * 更新相应id的order值，使其置顶 在末尾加了一个参数 原翔宇的置顶方法，已经被下面的方法替换
     */
    @SuppressWarnings("unused")
    private void moveArticlesToTop(
            int docLibID, String DocIDs, String colID,
            BigDecimal tmpMinOrderValue) throws E5Exception {
        // 更新order
        String relTable = InfoHelper.getRelTable(docLibID);
        String sql = "update " + relTable
                + " xa set a_order=? where SYS_DOCUMENTID=? and CLASS_1=?";
        String[] ids = DocIDs.split(",");
        BigDecimal tempOrder = new BigDecimal("" + ORDER_STEP);
        for (int j = 0; j < ids.length; j++) {
            BigDecimal tmpOrderValue = tmpMinOrderValue.subtract(tempOrder
                                                                         .multiply(new BigDecimal(
                                                                                 String.valueOf(ids.length - j))));
            InfoHelper.executeUpdate(docLibID, sql, new Object[]{tmpOrderValue, ids[j],
                    colID});
        }
    }

    /**
     * 置顶的order修改
     * 置顶逻辑：
     * 1）找出当前栏目的max order（实际上是负数表示，取的是min）
     * 2）若最高位不是1，则设最高位是1
     * 3）多个稿件同时置顶时，依次加步长
     */
    private void changeOrderTop(
            int docLibID, String DocIDs, long colID,
            BigDecimal tmpMinOrderValue) throws E5Exception {
    	
        // 更新order
        String relTable = InfoHelper.getRelTable(docLibID);
        String sql = "update " + relTable
                + " xa set a_order=?,a_priority='',a_position=0 where SYS_DOCUMENTID=? and CLASS_1=?";
        //稿件设置曾经置顶的标记
        String sql2 = "update " + LibHelper.getLibTable(docLibID)
                + " set a_onceTop=1 where SYS_DOCUMENTID=?";

        BigDecimal highBit = new BigDecimal(-100000000L); // 高位设1
        if (tmpMinOrderValue.compareTo(highBit) == 1) {
            // 若高位不是1，则设为1
            tmpMinOrderValue = tmpMinOrderValue.add(highBit);
        }

        String[] ids = DocIDs.split(",");
        BigDecimal tempOrder = new BigDecimal("" + ORDER_STEP);

        DBSession conn = E5docHelper.getDBSession(docLibID);
        try {
            conn.beginTransaction();
            for (int j = 0; j < ids.length; j++) {
                BigDecimal tmpOrderValue = tmpMinOrderValue.subtract(tempOrder
                                                                             .multiply(new BigDecimal(
                                                                                     String.valueOf(ids.length - j))));
                InfoHelper.executeUpdate(sql, new Object[]{tmpOrderValue, ids[j], colID}, conn);
                InfoHelper.executeUpdate(sql2, new Object[]{ids[j]}, conn);
            }
            conn.commitTransaction();
        } catch (Exception e) {
            ResourceMgr.rollbackQuietly(conn);
            throw new E5Exception(e);
        } finally {
            ResourceMgr.closeQuietly(conn);
        }
    }

    //取消置顶，把order改回稿件创建时计算的order数值
    private String cancelTop(int docLibID, String DocIDs, long colID) throws E5Exception {
        String changedIDs = null;
        //取消置顶，替换为稿件初始的Order//若原来没置顶，则不必操作
        String relTable = InfoHelper.getRelTable(docLibID);
        String sql = "update " + relTable + " set a_order=? where SYS_DOCUMENTID=? and CLASS_1=? and a_order<-100000000";

        long[] ids = StringUtils.getLongArray(DocIDs);
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        for (int j = 0; j < ids.length; j++) {
            Document doc = docManager.get(docLibID, ids[j]);
	        if(doc != null) {
	        	this.deleteExpireTime(docLibID, ids[j], colID);
	            int result = InfoHelper.executeUpdate(docLibID, sql,
	                    new Object[]{doc.getBigDecimal("a_order"), ids[j], colID});
	            if (result > 0)
	                changedIDs = (changedIDs == null) ? String.valueOf(ids[j]) : changedIDs + "," + ids[j];
            }
        }
        return changedIDs;
    }

    /**
     * 获得关联表中最小的order值
     *
     * @param pushColID
     * @return
     * @throws E5Exception
     */
    private BigDecimal readMinOrder(int docLibID, long pushColID)
            throws E5Exception {
        String relTable = InfoHelper.getRelTable(docLibID);
        String sql = "select min(a_order) from " + relTable + " where CLASS_1=?";
        Object[] params = new Object[]{pushColID};

        BigDecimal minOrderBD = query(docLibID, sql, params);
        return minOrderBD;
    }

    private BigDecimal query(int docLibID, String sql, Object[] params)
            throws E5Exception {
        DBSession conn = null;
        IResultSet rs = null;
        try {
            conn = E5docHelper.getDBSession(docLibID);
            rs = conn.executeQuery(sql, params);
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
        } catch (SQLException e) {
            throw new E5Exception(e);
        } finally {
            ResourceMgr.closeQuietly(rs);
            ResourceMgr.closeQuietly(conn);
        }
        return null;
    }

    /**
     * 更新排序
     *
     * @param request
     * @param DocLibID
     * @param jsonStr
     * @param colID
     * @param colLable
     * @param orderLable
     * @param typeId
     * @throws E5Exception
     */
    @SuppressWarnings("unchecked")
	private void updateOrder( HttpServletRequest request, int DocLibID,
            String jsonStr, String colID, String colLable, String orderLable,
            int typeId) throws Exception {
        // 获得json以及栏目id
        JSONObject json = JSONObject.fromObject(jsonStr); // <old,new>
        String relTable = "CLASS_1".equals(colLable)
                ? InfoHelper.getRelTable(DocLibID)
                : LibHelper.getLibByID(DocLibID).getDocLibTable();

        // 从数据库当中查询相应文章的ba_order字段，组成一个Map<articleID:String, a_order:BigDecimal>
        Map<String, BigDecimal> orderMap = findTableOrderMap(colID, json,
                                                             relTable, colLable, orderLable);
        // 根据map更新数据库xy_blockarticle 的ba_blockID
        updateArticleOrderToDB(DocLibID, colID, json, orderMap, relTable, colLable,
                               orderLable);

        String opinion = "";
        // 触发
        if (typeId == DocTypes.BLOCK.typeID()) {
            //区块内容排序：重新发布
            int _libId = LibHelper.getLibIDByOtherLib(typeId, DocLibID);
            PublishTrigger.block(_libId, Integer.parseInt(colID));
        } else if (typeId == DocTypes.COLUMN.typeID()) {
            //发布库稿件排序：重新发布
            int colLibID = LibHelper.getLibIDByOtherLib(
                    DocTypes.COLUMN.typeID(), DocLibID);
            PublishTrigger.articleOrder(colLibID, Integer.parseInt(colID));

            String colName = getColumnName(colLibID, Integer.parseInt(colID));
            if (!StringUtils.isBlank(colName)) {
                opinion = "栏目：" + colName;
            }
        } else if (typeId == DocTypes.WXMENU.typeID()) {
            //微信菜单稿件排序：重新发布
            int _libId = LibHelper.getLibIDByOtherLib(typeId, DocLibID);
            PublishTrigger.wx(_libId, Integer.parseInt(colID));
        } else if(typeId==DocTypes.PAPERLAYOUT.typeID()){
            //数字报版面更新排序：清空redis
            Iterator<String> it = json.keys();
            String id="";
            if(it.hasNext()){
                id=it.next();
            }
            DocumentManager docManager=DocumentManagerFactory.getInstance();
            Document doc=docManager.get(DocLibID,Long.valueOf(id));
            String date=doc.getString("pl_date").replace("-","");

            RedisManager.clear(RedisKey.APP_PAPER_LAYOUT_KEY + colID + "."+date);
        }

        // 写日志
        Iterator<String> it = json.keys();
        SysUser sysUser = ProcHelper.getUser(request);
        while (it.hasNext()) {
            LogHelper.writeLog(DocLibID, Long.parseLong(it.next()), sysUser,
                               "更新排序", opinion);
        }
    }

    private String getColumnName(int colLibID, long colID) throws E5Exception {
        Column col = colReader.get(colLibID, colID);
        return (col == null) ? "" : col.getName();
    }

    /**
     * 从数据库当中查询相应文章的a_order字段，组成一个Map<articleID:String, a_order:BigDecimal>
     *
     * @param colID 栏目id 对应数据库当中的 class_1字段
     * @param json  从前台获得的顺序列表
     * @return
     * @throws E5Exception
     */
    @SuppressWarnings("unchecked")
    private Map<String, BigDecimal> findTableOrderMap(
            String colID,
            JSONObject json, String dbTable, String colLable, String orderLable)
            throws E5Exception {
        // 先选出来id：order值
        StringBuilder sb = new StringBuilder();
        List<String> paramList = new LinkedList<>();

        Iterator<String> it = json.keys();
        // 生成条件
        while (it.hasNext()) {
            sb.append(",?");
            paramList.add(it.next());
        }
        if (colLable != null)
            paramList.add(colID);

        colLable = colLable == null ? "" : " and " + colLable + "=?";

        // 组成一个Map<articleID:String, a_order:BigDecimal>
        Map<String, BigDecimal> orderMap = new HashMap<String, BigDecimal>();
        String sql = "select SYS_DOCUMENTID, " + orderLable + " from "
                + dbTable + " where SYS_DOCUMENTID in ( "
                + sb.toString().substring(1) + " ) " + colLable;

        DBSession conn = null;
        IResultSet rs = null;

        try {
            conn = Context.getDBSession();
            rs = conn.executeQuery(sql, paramList.toArray());
            // 把值放到map当中
            while (rs.next()) {
                orderMap.put(rs.getLong("SYS_DOCUMENTID") + "",
                             rs.getBigDecimal(orderLable));
            }
        } catch (SQLException e) {
            throw new E5Exception(e);
        } finally {
            ResourceMgr.closeQuietly(rs);
            ResourceMgr.closeQuietly(conn);
        }
        return orderMap;
    }

    /**
     * 根据map更新关联表的 a_order
     *
     * @param json
     * @param orderMap 存放原顺序map
     * @throws E5Exception
     */
    @SuppressWarnings("unchecked")
    private void updateArticleOrderToDB(
            int docLibID, String colID, JSONObject json,
            Map<String, BigDecimal> orderMap, String dbTable, String colLable,
            String orderLable) throws E5Exception {
        String id = null;
        BigDecimal order = null;
        Iterator<String> it = json.keys();
        String updateId = null;
        colLable = colLable == null ? "" : " and " + colLable + "=?";
        String sql = "update " + dbTable + " xa set " + orderLable
                + "=? where SYS_DOCUMENTID=? " + colLable;
        while (it.hasNext()) {
            // id
            id = it.next();
            // 找对方的order
            order = orderMap.get(id + "");
            updateId = json.getString(id);
            if (!"".equals(colLable))
                InfoHelper.executeUpdate(sql, new Object[]{order, updateId,
                        colID});
            else
                InfoHelper.executeUpdate(docLibID, sql, new Object[]{order, updateId});
        }
    }
    
    /**
     * 专题更新
     * @param colID 当前栏目ID
     * @param docID 当前稿件ID 
     * @param docLibID 稿件库ID
     * @return
     * @throws E5Exception
     */
    @RequestMapping("specialRefresh.do")
    public ModelAndView specialRefresh(HttpServletRequest request,long colID,
        @RequestParam("DocIDs")long docID,@RequestParam("DocLibID")int docLibID) throws E5Exception{
	    ModelAndView mav = new ModelAndView() ;
	    DocumentManager docManager = DocumentManagerFactory.getInstance() ;
	    Document doc = docManager.get(docLibID, docID) ; 
	    int colLibID = LibHelper.getColumnLibID(request) ;
	    Document[] specials = this.findSpecial(colLibID, docLibID, colID) ;
	    StringBuffer str = new StringBuffer() ;
	    if(specials != null && specials.length > 0){
	        for(Document s:specials){
	        	str.append(s.getString("a_columnID")).append(":").append(s.getDocID()).append("|") ;
	        }
	        mav.addObject("str",str.toString()) ;
	        mav.addObject("flag",true) ;
	        mav.addObject("linkTitle", this.getLinkTitle(docLibID, colID, docID)) ; 
	        mav.addObject("order", doc.getDouble("a_order")) ;
	        mav.addObject("docLibID", docLibID) ;
	        mav.addObject("siteID", WebUtil.get(request, "siteID")) ;
	        mav.addObject("docID",docID) ;
	        mav.addObject("colID", WebUtil.getLong(request, "colID", 0)) ;
	        mav.addObject("siteID", WebUtil.get(request, "siteID"));
	    }else{ //不存在专题稿
	        mav.addObject("docID",docID) ;
	        mav.addObject("flag",false) ;
	    }
	    mav.addObject("UUID", WebUtil.get(request, "UUID"));
	    mav.setViewName("/xy/article/SpecialRefresh");
	    return  mav;
    }
    
    /**
     * 专题更新
     * @param docLibID 稿件库ID
     * @param str 专题栏目ID与专题稿件ID,格式:colID:docID|colID:docID|
     * @param order 稿件排序参数
     * @throws Exception
     */
    @RequestMapping("dealSpecialRefresh.do")
    public String dealSpecialRefresh(HttpServletRequest request,HttpServletResponse response,
        @RequestParam("docLibID")int docLibID,String str,double order )throws Exception{
    	String linkTitle = WebUtil.get(request, "linkTitle") ;
    	DocumentManager docManager = DocumentManagerFactory.getInstance();
	    DBSession conn = null;
	    String[] rs = str.split("\\|") ;
	    try {
	    	String relTable = InfoHelper.getRelTable(docLibID);
	    	boolean isConvert = WebUtil.getInt(request,"isCovert",0) == 1 ; //是否覆盖专题链接标题
	    	conn = Context.getDBSession();
	    	conn.beginTransaction();
	    	for(String s : rs){
		    	String colID = s.split(":")[0] ;
		    	long specialID = Long.parseLong(s.split(":")[1]) ;
			    if(isConvert){
			    	String sql = "update "+ relTable +" set a_linkTitle=?,a_order=? where SYS_DOCUMENTID=? and CLASS_1=?" ;
			    	conn.executeUpdate(sql, new Object[]{linkTitle,order,specialID,colID}) ;
			    	Document doc = docManager.get(docLibID, specialID);
			    	doc.setTopic(linkTitle);
			    	docManager.save(doc,conn);
			    	RedisManager.clear(RedisKey.APP_ARTICLELIST_ONE_KEY + specialID);
			    }else{
			    	String sql = "update "+ relTable +" set a_order=? where SYS_DOCUMENTID=? and CLASS_1=?" ;
			    	conn.executeUpdate(sql, new Object[]{order,specialID,colID}) ;
			    }
	    	}
	    	conn.commitTransaction();
		} catch (Exception e) {
			log.error(e);
            ResourceMgr.rollbackQuietly(conn);
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
	    // 写日志
	    int colLibID = LibHelper.getLibIDByOtherLib(DocTypes.COLUMN.typeID(),docLibID);
	    for(String s : rs){
	    	String colID = s.split(":")[0] ;
		    PublishTrigger.articleOrder(colLibID, Integer.parseInt(colID));
	    }
	    long docID = WebUtil.getLong(request, "docID",0) ;
	    String colID = WebUtil.get(request, "colID") ;
	    String opinion = java.net.URLEncoder.encode("栏目:" + findColumnName(colID), "UTF-8") ;
        String url = "redirect:/e5workspace/after.do?UUID=" + WebUtil.get(request, "UUID") + "&DocIDs="
                + docID + "&DocLibID=" + docLibID + "&colID=" + colID
                + "&siteID=" + WebUtil.get(request, "siteID")  + "&Opinion=" + opinion;
        return url;
    }
    
    /**
     * 递归寻找该栏目所属的专题稿
     * @param colLibID 栏目库ID
     * @param docLibID 稿件库ID
     * @param colID 当前栏目ID
     * @return 如果专题稿存在,返回专题稿数组,否则返回null
     * @throws E5Exception
     */
    private Document[] findSpecial(int colLibID,int docLibID,long colID) throws E5Exception{
    	if(colID > 0){
    		DocumentManager docManager = DocumentManagerFactory.getInstance();
        	Document[] specials = docManager.find(docLibID, " a_type = ? and a_linkID = ?" , new Object[]{Article.TYPE_SPECIAL,colID}) ;
        	if(specials == null || specials.length == 0){
        		Document column = docManager.get(colLibID, colID); //当前栏目
        	    int parentID = column.getInt("col_parentID"); //父栏目ID
        		return findSpecial(colLibID,docLibID,parentID) ; //可能返回的是空数组,需判断数组长度
        	}
        	return specials ;
    	}else{
    		return null ;
    	}
    }
    
    /**
     * 稿件置顶
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "moveTop.do")
    public ModelAndView moveTop(HttpServletRequest request,HttpServletResponse response)throws Exception{
    	ModelAndView mav = new ModelAndView() ;
    	
        int docLibID = WebUtil.getInt(request, "DocLibID", 0);
        long docID = WebUtil.getLong(request, "DocIDs", 0);
    	long colID = WebUtil.getLong(request, "colID", 0) ;
        
    	int expireLibID = LibHelper.getLibIDByOtherLib(DocTypes.ARTICLEEXPIRE.typeID(),docLibID);
    	DocumentManager docManager = DocumentManagerFactory.getInstance();
        Document doc = docManager.get(expireLibID, docID);
        int isExpire = doc == null ? 0 : 1;
        String expireTime= doc == null ? "" : DateUtils.format(doc.getDate("a_expireTime"), "yyyy-MM-dd HH:mm") ;
    	
        mav.addObject("isExpire", isExpire) ;
        mav.addObject("expireTime", expireTime) ;
    	mav.addObject("siteID", WebUtil.get(request, "siteID"));
    	mav.addObject("UUID", WebUtil.get(request, "UUID")) ;
    	mav.addObject("docLibID", docLibID) ;
        mav.addObject("colID", colID) ;
        mav.addObject("docIDs", docID) ;
        mav.setViewName("/xy/article/MoveTop");
    	return mav ;
    }

    /**
     * 稿件置顶 文章置顶 置顶时，a_order的最高位数字设为1
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "dealMoveTop.do")
    public String dealMoveTop(HttpServletRequest request,HttpServletResponse response) throws Exception {
    	int docLibID = WebUtil.getInt(request, "docLibID", 0);
    	long colID = WebUtil.getLong(request, "colID", 0) ;
        String docID = WebUtil.get(request, "docIDs");
    	
    	BigDecimal tmpMinOrderValue = readMinOrder(docLibID, colID);
        // 更新数据库
        changeOrderTop(docLibID, docID, colID, tmpMinOrderValue);
        updateExpire(request, docLibID, colID, docID);
        
        int colLibID = LibHelper.getColumnLibID(request);
        PublishTrigger.articleOrder(colLibID, colID);
        
    	Column column = colReader.get(colLibID, colID);
    	long pushColID = column.getPushColumn();
    	if(pushColID > 0) {
    		int appLibID = LibHelper.getArticleAppLibID();
            
    		tmpMinOrderValue = readMinOrder(appLibID, pushColID);
            changeOrderTop(appLibID, docID, pushColID, tmpMinOrderValue);
            updateExpire(request, appLibID, pushColID, docID);
            PublishTrigger.articleOrder(colLibID, pushColID);
    	}
        
        String opinion = findColumnName(colID+"");
        opinion = java.net.URLEncoder.encode("栏目:" + opinion, "UTF-8");
        String url = "redirect:/e5workspace/after.do?UUID=" + WebUtil.get(request, "UUID") + "&DocIDs="
                + docID + "&DocLibID=" + docLibID + "&colID=" + colID
                + "&siteID=" + WebUtil.get(request, "siteID") + "&Opinion=" + opinion;
        return url;
    }
    
    private void updateExpire(HttpServletRequest request, int docLibID, long colID, String docID) throws Exception{
    	String expireTime = WebUtil.get(request, "expireTime") +":00"; //过期时间
    	int isExpire = WebUtil.getInt(request, "isExpire", 0);
    	
    	//过期时间
        int expireLibID = LibHelper.getLibIDByOtherLib(DocTypes.ARTICLEEXPIRE.typeID(),docLibID);        
        DocumentManager docManager = DocumentManagerFactory.getInstance() ;
        this.deleteExpireTime(docLibID, Long.parseLong(docID), colID);
        if(isExpire == 1){
			
			Document expire = docManager.newDocument(expireLibID,Long.parseLong(docID)) ;
			expire.set("a_columnID", colID);
			expire.set("a_expireTime", Timestamp.valueOf(expireTime));
			expire.set("a_type", 0);
			expire.set("a_docLibID", docLibID);
			docManager.save(expire);
        }
    }
    
    /**
     * 取消置顶，改回原计算方法
     */
    @RequestMapping(value = "cancelTop.do")
    public String cancelTop(
            HttpServletRequest request, HttpServletResponse response,
            @RequestParam("DocIDs") String docIDs,
            @RequestParam("DocLibID") int docLibID,
            long colID) throws Exception {
    	int colLibID = LibHelper.getLibIDByOtherLib(DocTypes.COLUMN.typeID(), docLibID);

        // 更新数据库
        docIDs = cancelTop(docLibID, docIDs, colID);
        
    	Column column = colReader.get(colLibID, colID);
    	long pushColID = column.getPushColumn();
        //发布消息，重发栏目页
        PublishTrigger.articleOrder(colLibID, colID);
        
        if(pushColID > 0) {
        	int appLibID = LibHelper.getArticleAppLibID();
        	cancelTop(appLibID, docIDs, pushColID);
        	PublishTrigger.articleOrder(colLibID, pushColID);
        }

        // 日志
        String opinion = findColumnName(colID+"");
        opinion = java.net.URLEncoder.encode("栏目：" + opinion, "UTF-8");

        String url = "redirect:/e5workspace/after.do?UUID=" + WebUtil.get(request, "UUID")
                + "&Opinion=" + opinion;
        if (docIDs != null)
            url += "&DocIDs=" + docIDs;

        return url;
    }
    
    /**
     * 稿件固定位置
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "articlePosition.do")
    public ModelAndView articlePosition(HttpServletRequest request,HttpServletResponse response)throws Exception{
    	ModelAndView mav = new ModelAndView() ;
    	int docLibID = WebUtil.getInt(request, "DocLibID", 0);
        long docID = WebUtil.getLong(request, "DocIDs", 0);
        long colID = WebUtil.getLong(request, "colID", 0) ;
        //取得稿件的固定位置,链接标题
        DBSession conn = null;
        IResultSet rs = null;
        String a_linkTitle = "";
        int a_position = 0;
        int isExpire = 0 ;
        String expireTime = "" ;
        String relTable = InfoHelper.getRelTable(docLibID);
        
        try {
        	int expireLibID = LibHelper.getLibIDByOtherLib(DocTypes.ARTICLEEXPIRE.typeID(),docLibID);
        	DocumentManager docManager = DocumentManagerFactory.getInstance();
            Document doc = docManager.get(expireLibID, docID);
            isExpire = doc == null ? 0 : 1;
            expireTime= doc == null ? "" : DateUtils.format(doc.getDate("a_expireTime"), "yyyy-MM-dd HH:mm") ;
            
            String sql = "select a_linkTitle,a_position from "
                    + relTable + " where SYS_DOCUMENTID=? and CLASS_1=?";
            conn = Context.getDBSession();
            rs = conn.executeQuery(sql, new Object[]{docID, colID});
            if (rs.next()) {
                a_linkTitle = rs.getString("a_linkTitle");
                a_position = rs.getInt("a_position");
            }

        } catch (SQLException e) {
            throw new E5Exception(e);
        } finally {
            ResourceMgr.closeQuietly(rs);
            ResourceMgr.closeQuietly(conn);
        }
        
    	mav.addObject("siteID", WebUtil.get(request, "siteID"));
    	mav.addObject("UUID", WebUtil.get(request, "UUID")) ;
    	mav.addObject("docLibID", docLibID) ;
        mav.addObject("colID", colID) ;
        mav.addObject("docID", docID) ;
        mav.addObject("linkTitle", a_linkTitle) ;
        mav.addObject("position", a_position) ;
        mav.addObject("isExpire", isExpire) ;
        mav.addObject("expireTime", expireTime) ;
        mav.setViewName("/xy/article/ArticlePosition");
    	return mav ;
    }
    
    /**
     * 稿件固定位置
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "updateArticlePosition.do")
    public String updateArticlePosition(HttpServletRequest request,HttpServletResponse response)throws Exception{
    	int docLibID = WebUtil.getInt(request, "docLibID", 0);
    	String colID = WebUtil.get(request, "colID") ;
        long docID = WebUtil.getLong(request, "docID",0);
    	String expireTime = WebUtil.get(request, "expireTime") ;
    	int isExpire = WebUtil.getInt(request, "isExpire", 0);
    	int position = WebUtil.getInt(request, "position", 0) ;
    	
    	String relTable = InfoHelper.getRelTable(docLibID);
    	if (position > 0) { //固定位置上可能已经存在稿件
            String sql = "update " + relTable + " set a_position=0 where a_position=? and CLASS_1=?";
            InfoHelper.executeUpdate(docLibID, sql, new Object[]{position, colID});
        }
    	//更新数据库
        String sql = "update " + relTable + " set a_position=?, a_order=?,a_priority='' where SYS_DOCUMENTID=? and CLASS_1=? ";
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        Document doc = docManager.get(docLibID, docID);
        InfoHelper.executeUpdate(docLibID, sql,new Object[]{position,doc.getBigDecimal("a_order"), docID, colID});
        
        //过期时间
        int expireLibID = LibHelper.getLibIDByOtherLib(DocTypes.ARTICLEEXPIRE.typeID(),docLibID);
        this.deleteExpireTime(docLibID, docID, Long.parseLong(colID));
		if(isExpire == 1 && position > 0){
			Document expire = docManager.newDocument(expireLibID,docID) ;
			expire.set("a_columnID", colID);
			expire.set("a_expireTime", expireTime);
			expire.set("a_type", 1);
			expire.set("a_docLibID", docLibID);
			docManager.save(expire);
		}
        
	    // 日志
        int colLibID = LibHelper.getLibIDByOtherLib(DocTypes.COLUMN.typeID(), docLibID);
        PublishTrigger.articleOrder(colLibID, Integer.parseInt(colID));
        String opinion = findColumnName(colID);
        opinion = java.net.URLEncoder.encode("栏目:" + opinion, "UTF-8");
        String url = "redirect:/e5workspace/after.do?UUID=" + WebUtil.get(request, "UUID") + "&DocIDs="
                + docID + "&DocLibID=" + docLibID + "&colID=" + colID
                + "&siteID=" + WebUtil.get(request, "siteID") + "&Opinion=" + opinion;
        return url;
    }
    
    /**
     * 稿件优先级
     * @throws Exception
     */
    @RequestMapping(value = "articlePriority.do")
    public ModelAndView articlePriority(HttpServletRequest request,HttpServletResponse response)throws Exception{
    	ModelAndView mav = new ModelAndView() ;
    	int docLibID = WebUtil.getInt(request, "DocLibID", 0);
        long docID = WebUtil.getLong(request, "DocIDs", 0);
    	long colID = WebUtil.getLong(request, "colID", 0) ;
        
    	DBSession conn = null;
        IResultSet rs = null;
        String a_linkTitle = "";
        Double a_order = 0d;
        String a_priority = "";
        Date a_pubTime = null;
        Date a_pubDate = null;
        int a_position = 0;
        String relTable = InfoHelper.getRelTable(docLibID);
        try {
            String sql = "select a_linkTitle,a_order,a_priority,a_pubTime,a_position from "
                    + relTable + " where SYS_DOCUMENTID=? and CLASS_1=?";
            conn = Context.getDBSession();
            rs = conn.executeQuery(sql, new Object[]{docID, colID});
            if (rs.next()) {
                a_linkTitle = rs.getString("a_linkTitle");
                a_order = rs.getDouble("a_order");
                a_priority = org.apache.commons.lang.StringUtils.isEmpty(rs.getString("a_priority")) ? "0a" : rs
                        .getString("a_priority");
                a_pubTime = rs.getTimestamp("a_pubTime");
                a_pubDate = rs.getDate("a_pubTime");
                a_position = rs.getInt("a_position");
            }

        } catch (SQLException e) {
            throw new E5Exception(e);
        } finally {
            ResourceMgr.closeQuietly(rs);
            ResourceMgr.closeQuietly(conn);
        }
        mav.addObject("siteID", WebUtil.get(request, "siteID"));
    	mav.addObject("UUID", WebUtil.get(request, "UUID")) ;
    	mav.addObject("docLibID", docLibID) ;
        mav.addObject("colID", WebUtil.getLong(request, "colID", 0)) ;
        mav.addObject("docID", docID) ;
        
        mav.addObject("linkTitle", a_linkTitle);
        mav.addObject("pubTime", a_pubTime);
        mav.addObject("pubDate", a_pubDate);
        mav.addObject("priority", a_priority);
        mav.addObject("priority_level", a_priority.substring(a_priority.length()-1));
        int status = 0 ;
        if(a_order < -100000000){
        	status = 1 ; //已置顶
        }else if(a_position > 0){
        	status = 2 ; //已设置固定位置
        }
        mav.addObject("status", status);
        
        mav.setViewName("/xy/article/ArticlePriority");
    	return mav ;
    }
    
    /**
     * 稿件优先级
     * @throws Exception
     */
    @RequestMapping(value = "updateArticlePriority.do")
    public String updateArticlePriority(HttpServletRequest request,HttpServletResponse response)throws Exception{
    	int docLibID = WebUtil.getInt(request, "docLibID", 0);
    	String colID = WebUtil.get(request, "colID") ;
        long docID = WebUtil.getLong(request, "docID",0);
        
    	String pubTime = WebUtil.get(request, "pubTime") ;
    	String priorityDay = WebUtil.get(request, "priorityDay") ;
    	String priorityLevel = WebUtil.get(request, "priorityLevel").toLowerCase() ;
    	
        int overDays = InfoHelper.daysBetween(pubTime, priorityDay); // 领先天数
	    int _priority = 0; // 转换后的优先级 - 用于计算顺序
        String priority = "0a"; // 合成后的优先级 - 用于更新数据库
        if (priorityLevel != null && !priorityLevel.isEmpty() && priorityLevel.length() == 1) {
	        _priority = priorityLevel.charAt(0) - 97;// 转换优先级
            priority = overDays + priorityLevel;// 合成优先级
            if ("0a".equals(priority)) priority = ""; //若是0a，则不显示
        }
        //重新计算Order
        Calendar cal = Calendar.getInstance();
        cal.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(pubTime));
        double order = articleManager.createDisplayOrder(cal, overDays, _priority, docID);
        
        //更新数据库
    	String relTable = InfoHelper.getRelTable(docLibID);
    	String sql = "update " + relTable + " set a_priority=?,a_order=? where SYS_DOCUMENTID=? and CLASS_1=?";
        InfoHelper.executeUpdate(docLibID, sql,new Object[]{priority, order, docID, colID});
        
	    // 触发器
        int colLibID = LibHelper.getLibIDByOtherLib(DocTypes.COLUMN.typeID(), docLibID);
        PublishTrigger.articleOrder(colLibID, Integer.parseInt(colID));
        
        // 日志
        String opinion = "栏目：" + findColumnName(colID);
        opinion = java.net.URLEncoder.encode(opinion, "UTF-8");
        
        String url = "redirect:/e5workspace/after.do?UUID=" + WebUtil.get(request, "UUID") + "&DocIDs="
                + docID + "&DocLibID=" + docLibID + "&colID=" + colID
                + "&siteID=" + WebUtil.get(request, "siteID") + "&Opinion=" + opinion;
        return url;
    }

    /**
     * 取消稿件优先级
     * @throws Exception
     */
    @RequestMapping(value = "cancelArticlePriority.do")
    public String cancelArticlePriority(HttpServletRequest request,HttpServletResponse response)throws Exception{
        int docLibID = WebUtil.getInt(request, "DocLibID", 0);
        String colID = WebUtil.get(request, "colID") ;
        long docID = WebUtil.getLong(request, "DocIDs",0);

        // 日志
        String opinion = "栏目：" + findColumnName(colID);
        opinion = java.net.URLEncoder.encode(opinion, "UTF-8");

        String url = "redirect:/e5workspace/after.do?UUID=" + WebUtil.get(request, "UUID")  + "&DocLibID=" + docLibID + "&colID=" + colID
                + "&siteID=" + WebUtil.get(request, "siteID") + "&Opinion=" + opinion;
        if(cancelPriority(docLibID,colID,docID))
            url += "&DocIDs=" + docID;

        return url;

    }

    private boolean cancelPriority(int docLibID, String colID, long docID) throws Exception {
        Calendar ca = Calendar.getInstance();
        ca.setTime(DateUtils.getTimestamp());
        //重新计算Order
        double order = articleManager.createDisplayOrder(ca, 0, 0, docID);
        //更新数据库
        String relTable = InfoHelper.getRelTable(docLibID);
        String sql = "update " + relTable + " set a_priority=null,a_order=? where SYS_DOCUMENTID=? and CLASS_1=? and a_priority IS NOT NULL";
        int result = InfoHelper.executeUpdate(docLibID, sql, new Object[]{ order, docID, colID});
        if(result>0) {
            // 触发器
            int colLibID = LibHelper.getLibIDByOtherLib(DocTypes.COLUMN.typeID(), docLibID);
            PublishTrigger.articleOrder(colLibID, Integer.parseInt(colID));
        }
        return result>0;
    }


    //获取稿件的链接标题
    private String getLinkTitle(int docLibID, long colID, long docID) {
		String relTable = InfoHelper.getRelTable(docLibID);
		String SQL =  "select  a_linkTitle from " + relTable
				+ " where SYS_DOCUMENTID=? and CLASS_1=?";
        Object[] params = new Object[]{docID,colID};
        DBSession db = null;
        IResultSet rs = null;
        String result ="";
        try {
            db = InfoHelper.getDBSession(docLibID);
            rs = db.executeQuery(SQL, params);
            while (rs.next()) {
                result = rs.getString("a_linkTitle");
            }
        } catch (Exception e) {
        	log.error(e);
        } finally {
            ResourceMgr.closeQuietly(rs);
            ResourceMgr.closeQuietly(db);
        }
		return result;
	}





    private void deleteExpireTime(int docLibID,long docID,long colID){
    	 int expireLibID = LibHelper.getLibIDByOtherLib(DocTypes.ARTICLEEXPIRE.typeID(),docLibID);
         DBSession conn = null;
 	    try {
 	    	String tableName = LibHelper.getLibTable(expireLibID) ;
 	        conn = E5docHelper.getDBSession(expireLibID);
 	        String sql = " DELETE FROM " + tableName + "  WHERE SYS_DOCUMENTID = ? AND a_columnID = ? " ;
 	        conn.beginTransaction();
 	        conn.executeUpdate(sql, new Object[]{docID,colID}) ;
 	        conn.commitTransaction();
	    } catch (Exception e) {
	    	log.error(e);
	        ResourceMgr.rollbackQuietly(conn);
	    } finally {
	        ResourceMgr.closeQuietly(conn);
	    }
    }

    /**
     * 话题稿件更新排序
     */
    @RequestMapping(value = "updateTopicArticleOrder.do", method = RequestMethod.POST)
    public void updateTopicArticleOrder(
            HttpServletRequest request,
            HttpServletResponse response, String jsonStr, String topicID, String channel) {
        JSONObject json = new JSONObject();
        try {
            String docIDs = updateTopicrelartOrder(request,jsonStr,topicID,channel);
            json.put("status", 1);

            //清一下话题稿件缓存；
            topicsDAO.clearTopicRedisCache(docIDs,topicID,channel);
        } catch (Exception e) {
            json.put("status", 0);
            json.put("info", "更新失败！请稍后再尝试！");
            json.put("stacktrace", e);
            log.error(e);

            e.printStackTrace();
        }

        // 向前台传返回值
        InfoHelper.outputJson(json.toString(), response);
    }

    /**
     * 话题稿件更新排序方法
     */
    private String updateTopicrelartOrder(HttpServletRequest request, String jsonStr,String topicID, String channel) throws Exception {

        JSONObject json = JSONObject.fromObject(jsonStr);

        // 从数据库当中查询相应文章的ba_order字段，组成一个Map<articleID:String, a_order:BigDecimal>
        Map<String, BigDecimal> orderMap = findTopicArticleOrderMap(json,topicID,channel);
        //12312312312313123 topicID
        // 根据map更新数据库排序字段
        updateTopicArticleOrderToDB(json, orderMap, topicID,channel);

        Set<String> idSet = orderMap.keySet();

        String[] idsArray = idSet.toArray(new String[idSet.size()]);

        String ids = StringUtils.join(idsArray,",");

        return ids;
    }

    /**
     * 话题稿件更新排序方法
     */
    private Map<String,BigDecimal> findTopicArticleOrderMap(JSONObject json, String topicID, String channel) throws E5Exception {
        // 先选出来id：order值
        StringBuilder sb = new StringBuilder();
        List<String> paramList = new LinkedList<>();

        Iterator<String> it = json.keys();
        // 生成条件
        while (it.hasNext()) {
            sb.append(",?");
            paramList.add(it.next());
        }
        Map<String, BigDecimal> orderMap = new HashMap<String, BigDecimal>();
        String sql = "select a_articleID, a_order" + " from xy_topicrelart where a_articleID in ( "
                + sb.toString().substring(1) + " ) and a_topicID = "+ topicID +" and a_channel = "+channel;

        DBSession conn = null;
        IResultSet rs = null;
        try {
            conn = Context.getDBSession();
            rs = conn.executeQuery(sql, paramList.toArray());
            // 把值放到map当中
            while (rs.next()) {
                orderMap.put(rs.getLong("a_articleID") + "",
                        rs.getBigDecimal("a_order"));
            }
        } catch (SQLException e) {
            throw new E5Exception(e);
        } finally {
            ResourceMgr.closeQuietly(rs);
            ResourceMgr.closeQuietly(conn);
        }
        return orderMap;
    }

    /**
     * 话题稿件更新排序方法
     */
    private void updateTopicArticleOrderToDB(JSONObject json, Map<String,BigDecimal> orderMap, String topicID,String channel) throws E5Exception {
        String id = null;
        BigDecimal order = null;
        Iterator<String> it = json.keys();
        String updateId = null;
        String sql = "update xy_topicrelart xa set a_order "
                + "=? where a_articleID=? and a_topicID = ? and a_channel = ?";
        while (it.hasNext()) {
            id = it.next();
            // 找对方的order
            order = orderMap.get(id + "");
            updateId = json.getString(id);

            InfoHelper.executeUpdate(sql, new Object[]{order, updateId, topicID,channel});
        }
    }

    /**
     * 话题稿件置顶方法
     */
    @RequestMapping(value = "topicArticleMoveTop.do", method = RequestMethod.GET)
    public void topicArticleMoveTop(
            HttpServletRequest request,
            HttpServletResponse response, String docIDs, String topicID,String channel) {
        JSONObject json = new JSONObject();
        try {
            changeTopicrelartOrderTop(request,docIDs,topicID,channel);
            json.put("status", 1);
            json.put("info", "置顶成功！");

            //清一下话题稿件缓存；
            topicsDAO.clearTopicRedisCache(docIDs,topicID,channel);
            int chn = Integer.parseInt(channel);
            if(chn == 1){
                topicsDAO.clearTopicRedisCache(docIDs,topicID,String.valueOf(chn+1));
            } else {
                topicsDAO.clearTopicRedisCache(docIDs,topicID,String.valueOf(chn-1));
            }
        } catch (Exception e) {
            json.put("status", 0);
            json.put("info", "置顶失败！请稍后再尝试！");
            json.put("stacktrace", e);
            log.error(e);

            e.printStackTrace();
        }

        // 向前台传返回值
        InfoHelper.outputJson(json.toString(), response);
    }

    /**
     * 话题稿件置顶方法
     */
    private void changeTopicrelartOrderTop(HttpServletRequest request, String docIDs,String topicID,String channel) throws E5Exception {
//        String updateSql = "update xy_topicrelart set a_order = ? where a_articleID = ? and a_topicID = ? and a_channel = ?";
        String updateSql = "update xy_topicrelart set a_order = ? where a_articleID = ? and a_topicID = ?";

        BigDecimal tmpMinOrderValue = getTopicrelartMinOrder(topicID,channel);
        BigDecimal highBit = new BigDecimal(-100000000L); // 高位设1

        if (tmpMinOrderValue.compareTo(highBit) == 1) {
            // 若高位不是1，则设为1
            tmpMinOrderValue = tmpMinOrderValue.add(highBit);
        }

        String[] ids = docIDs.split(",");
        BigDecimal tempOrder = new BigDecimal("" + ORDER_STEP);

        DBSession conn = Context.getDBSession();
        try {
            conn.beginTransaction();
            for (int j = 0; j < ids.length; j++) {
                BigDecimal tmpOrderValue = tmpMinOrderValue.subtract(tempOrder
                        .multiply(new BigDecimal(
                                String.valueOf(ids.length - j))));
//                InfoHelper.executeUpdate(updateSql, new Object[]{tmpOrderValue, ids[j], topicID,channel});
                InfoHelper.executeUpdate(updateSql, new Object[]{tmpOrderValue, ids[j], topicID});
            }
            conn.commitTransaction();
        } catch (Exception e) {
            ResourceMgr.rollbackQuietly(conn);
            throw new E5Exception(e);
        } finally {
            ResourceMgr.closeQuietly(conn);
        }

    }

    /**
     * 话题稿件置顶方法
     */
    private BigDecimal getTopicrelartMinOrder(String topicID,String channel) throws E5Exception {
        String minOrderSql = "select min(a_order) from xy_topicrelart where a_topicID="+topicID+" and a_channel="+channel;

        BigDecimal a_order = new BigDecimal(0);
        DBSession conn = null;
        IResultSet rs = null;
        try {
            conn = Context.getDBSession();
            rs = conn.executeQuery(minOrderSql);
            if (rs.next()) {
                a_order = rs.getBigDecimal(1);
            }
        } catch (SQLException e) {
            throw new E5Exception(e);
        } finally {
            ResourceMgr.closeQuietly(rs);
            ResourceMgr.closeQuietly(conn);
        }
        return a_order;
    }

    /**
     * 话题稿件取消置顶方法
     */
    @RequestMapping(value = "topicArticleCancelTop.do", method = RequestMethod.GET)
    public void topicArticleCancelTop(
            HttpServletRequest request,
            HttpServletResponse response, String docIDs, String topicID,String channel) {
        JSONObject json = new JSONObject();
        try {
            changeTopicrelartCancelTop(request,docIDs,topicID,channel);
            json.put("status", 1);
            json.put("info", "取消置顶成功！");
            topicsDAO.clearTopicRedisCache(docIDs,topicID,channel);
            int chn = Integer.parseInt(channel);
            if(chn == 1){
                topicsDAO.clearTopicRedisCache(docIDs,topicID,String.valueOf(chn+1));
            } else {
                topicsDAO.clearTopicRedisCache(docIDs,topicID,String.valueOf(chn-1));
            }
        } catch (Exception e) {
            json.put("status", 0);
            json.put("info", "取消置顶失败！请稍后再尝试！");
            json.put("stacktrace", e);
            log.error(e);

            e.printStackTrace();
        }

        // 向前台传返回值
        InfoHelper.outputJson(json.toString(), response);
    }

    /**
     * 话题稿件取消置顶方法
     */
    private void changeTopicrelartCancelTop(HttpServletRequest request, String docIDs, String topicID, String channel) throws E5Exception {
//        String updateSql = "update xy_topicrelart set a_order = ? where a_articleID = ? and a_topicID = ? and a_channel = ? and a_order < -100000000";
        String updateSql = "update xy_topicrelart set a_order = ? where a_articleID = ? and a_topicID = ? and a_order < -100000000";
        long[] ids = StringUtils.getLongArray(docIDs);

        DocLib[] docLibIDs = LibHelper.getLibs(DocTypes.ARTICLE.typeID());
        int docLibID;
        if("1".equals(channel)){
            docLibID = docLibIDs[0].getDocLibID();
        }else{
            docLibID = docLibIDs[1].getDocLibID();
        }
        DBSession conn = Context.getDBSession();
        try {
            conn.beginTransaction();
            DocumentManager docManager = DocumentManagerFactory.getInstance();
            for (int j = 0; j < ids.length; j++) {
                Document doc = docManager.get(docLibID, ids[j]);
//                InfoHelper.executeUpdate(updateSql, new Object[]{doc.getBigDecimal("a_order"), ids[j], topicID, channel});
                InfoHelper.executeUpdate(updateSql, new Object[]{doc.getBigDecimal("a_order"), ids[j], topicID});
            }
            conn.commitTransaction();
        } catch (Exception e) {
            ResourceMgr.rollbackQuietly(conn);
            throw new E5Exception(e);
        } finally {
            ResourceMgr.closeQuietly(conn);
        }

    }

}