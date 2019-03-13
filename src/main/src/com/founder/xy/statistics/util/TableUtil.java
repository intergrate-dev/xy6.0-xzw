package com.founder.xy.statistics.util;

import com.founder.e5.commons.DomHelper;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBType;
import com.founder.e5.dom.DocLib;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.LibHelper;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ethan on 2016/12/16.
 */

public class TableUtil {

    //确定查询的是web发布库还是app发布库
    public static String getArticleTableName(String tenantCode, String channelCode) throws E5Exception {
        List<DocLib> docLibList = LibHelper.getLibs(DocTypes.ARTICLE.typeID(), tenantCode);
        String tableName;
        if (channelCode.startsWith("channelWeb")) {
            tableName = LibHelper.getLibTable(docLibList.get(0).getDocLibID());
        } else if (channelCode.startsWith("channelApp")) {
            tableName = LibHelper.getLibTable(docLibList.get(1).getDocLibID());
        } else {
            tableName = null;
        }
        return tableName;
    }

    public static String getColumnTableName() throws E5Exception {
        return LibHelper.getLib(DocTypes.COLUMN.typeID()).getDocLibTable().toString();
    }

    public static Map<String, Object> getArticleRankingCondition(String columnIDs, String channel, String timeTag, Timestamp beginDate, Timestamp endDate) throws E5Exception {
        Map<String, Object> resultMap = new HashMap<>();
        StringBuilder columnCondition = new StringBuilder();
        switch (timeTag) {
            case "hour":
                resultMap.put("tableName", "xy_stathour");
                break;
            case "day":
                resultMap.put("tableName", "xy_stat");
                //添加时间戳查询条件
                appendTime(columnCondition, beginDate, endDate);
                break;
            default:
                throw new E5Exception("Wrong Time Tag!");
        }

        if (columnIDs == null || "".equals(columnIDs)) {
            columnCondition.append(" a.st_" + channel + "Col>0 ");
           /* columnCondition.append("(a.st_" + channel + "Col>0 or a.st_" + channel + "Col0>0 or a.st_" 
            		+ channel + "Col1>0 or a.st_" + channel + "Col2>0 or a.st_" + channel + "Col3>0 "
            				+ "or a.st_" + channel + "Col4>0)");*/
            resultMap.put("columnCondition", columnCondition);
        } else {
            //columnCondition.append("a.st_" + channel + "Col" + columnLv + "=" + columnID);
        	columnCondition.append("(a.st_" + channel + "Col in ( "+columnIDs+")"
        			+ " or a.st_" + channel + "Col0 in ( "+columnIDs+")"
        			+ " or a.st_" + channel + "Col1 in ( "+columnIDs+")"
        			+ " or a.st_" + channel + "Col2 in ( "+columnIDs+")"
        			+ " or a.st_" + channel + "Col3 in ( "+columnIDs+")"
        			+ " or a.st_" + channel + "Col4 in ( "+columnIDs+"))"
            		+ " and b.a_columnID in (" + columnIDs + ") ");
            resultMap.put("columnCondition", columnCondition);
        }
        return resultMap;
    }

    private static void appendTime(StringBuilder columnCondition, Timestamp beginDate, Timestamp endDate) {
    	if (isOracle()) {
        	columnCondition.append(" a.st_date BETWEEN to_date('" + formatToDays(beginDate) + "','yyyyMMdd hh24:mi:ss') AND to_date('" + formatToDays(endDate) + "','yyyyMMdd hh24:mi:ss') and ");
        }else{
        	columnCondition.append(" a.st_date between DATE_FORMAT('" + formatToDays(beginDate) + "', '%Y-%m-%d %T:')  and DATE_FORMAT('" + formatToDays(endDate) + "', '%Y-%m-%d %T:') and ");
        }
		
	}

	private static String formatToDays(Timestamp date) {
    	if(isOracle()){
	    	SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd HH:mm:ss"); 
	        return format.format(date);
        }else{
        	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
            return format.format(date);
        }
	}

	private static boolean isOracle() {
		String dbType = DomHelper.getDBType();
		//String dbType = DBType.ORACLE;
        return dbType.equals(DBType.ORACLE);
	}

	public static Map<String, Object> getColumnClickRankingCondition(String timeTag, Timestamp beginDate, Timestamp endDate) throws E5Exception {
        StringBuilder columnCondition = new StringBuilder();
        Map<String, Object> resultMap = new HashMap<>();
        switch (timeTag) {
            case "hour":
                resultMap.put("tableName", "xy_statcolhour");
                break;
            case "day":
                resultMap.put("tableName", "xy_statcol");
                //添加时间戳查询条件
                appendTime(columnCondition, beginDate, endDate);
                break;
            default:
                throw new E5Exception("Wrong Time Tag!");
        }
        resultMap.put("columnCondition", columnCondition);
        return resultMap;
    }

    public static Map<String, Object> getColumnSubscribeRankingCondition(String timeTag, Timestamp beginDate, Timestamp endDate) throws E5Exception {
        StringBuilder columnCondition = new StringBuilder();
        Map<String, Object> resultMap = new HashMap<>();
        switch (timeTag) {
            case "hour":
                resultMap.put("tableName", "xy_statcolhour");
                break;
            case "day":
                resultMap.put("tableName", "xy_statcol");
                //添加时间戳查询条件
                appendTime(columnCondition, beginDate, endDate);
                break;
            default:
                throw new E5Exception("Wrong Time Tag!");
        }
        resultMap.put("columnCondition", columnCondition);
        return resultMap;
    }

    public static Map<String, Object> getColumnArticleGeneralRankingCondition(String channelCode, String timeTag, Timestamp beginDate, Timestamp endDate) throws E5Exception {
        StringBuilder articleCondition = new StringBuilder();
        Map<String, Object> resultMap = new HashMap<>();
        switch (timeTag) {
            case "hour":
                resultMap.put("tableName", "xy_stathour");
                break;
            case "day":
                resultMap.put("tableName", "xy_stat");
                //添加时间戳查询条件
                appendTime(articleCondition, beginDate, endDate);
                break;
            default:
                throw new E5Exception("Wrong Time Tag!");
        }
        switch (channelCode) {
            case "channelWeb":
                articleCondition.append("a.st_webCol>0");
                resultMap.put("column", "st_webCol");
                break;
            case "channelApp":
                articleCondition.append("a.st_appCol>0");
                resultMap.put("column", "st_appCol");
                break;
            default:
                throw new E5Exception("Wrong Channel Code!");
        }
        resultMap.put("articleCondition", articleCondition);
        return resultMap;
    }
}
