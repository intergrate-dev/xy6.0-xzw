package com.founder.xy.workspace.service;

import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.BaseField;
import com.founder.e5.context.Context;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.workspace.service.DocListHelper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.ArrayUtils;
import sun.security.util.Length;

import java.util.Iterator;


public class DocListTopicMerge extends DocListService{
	//固定查询字段
	protected static final String[] FIELDS_IN_RELTABLE = new String[]{
			"a_status","SYS_TOPIC","SYS_CREATED","SYS_AUTHORS","a_articleCount",
			"a_lastPubTime","SYS_DOCUMENTID","SYS_DOCLIBID","a_color","a_icon"
	};
	//查关联表时固定的SQL语句的前半部分。select SYS_DOCUMENTID...
	protected static final String SQL_PREFIX_IN_RELTABLE;
	static {
		String sql = "select ";
		for (int i = 0; i < FIELDS_IN_RELTABLE.length; i++) {
			if (i > 0&&!FIELDS_IN_RELTABLE[i].equals("a_articleCount")&&!FIELDS_IN_RELTABLE[i].equals("a_icon")) sql += ",";
			if(FIELDS_IN_RELTABLE[i].equals("a_lastPubTime")){
                sql += "DATE_FORMAT(a_lastPubTime, '%Y-%m-%d %H:%i:%S') as a_lastPubTime";
            }else if(FIELDS_IN_RELTABLE[i].equals("SYS_CREATED")){
                sql += "DATE_FORMAT(SYS_CREATED, '%Y-%m-%d %H:%i:%S') as SYS_CREATED";
            }else if(FIELDS_IN_RELTABLE[i].equals("a_articleCount")||FIELDS_IN_RELTABLE[i].equals("a_icon")){

			}else{
				sql += FIELDS_IN_RELTABLE[i];
			}
		}
		SQL_PREFIX_IN_RELTABLE = sql;
	}
	
	@Override
	public String getDocList() {
		throw new RuntimeException("TwoStepsDocList: NO getDocList() SUPPORT!");
	}

	/**
	 * 两段式列表查询：关联表时，或检索时
	 * @return
	 */
	public String getDocList(boolean activityRel, String type) {
		JSONObject result = null;

		//1查询表符合条件数据数量
		//无查询结果时，直接返回
		int count = getListCount(type);
		if (count == 0) return EMPTY_XML;

		//2查看表数据
        JSONArray list = getListFromRelTable(type);

		String xml = assembleXML(count, list);
		return xml;
	}

	private JSONArray getListFromRelTable(String type) {
        String orderSql;
        if(StringUtils.isBlank(getOrderBy())){
            orderSql = " a.sys_documentid desc";
        }else{
            orderSql = getOrderBy();
        }

		String sql = SQL_PREFIX_IN_RELTABLE
                + " ,(SELECT count(1) from xy_topicrelart b,xy_article c where a.SYS_DOCUMENTID=b.a_topicID "
                + " and b.a_articleID=c.SYS_DOCUMENTID and c.SYS_DELETEFLAG=0 and b.a_channel=1)+(SELECT "
                + " count(1) from xy_topicrelart b,xy_articleapp c where a.SYS_DOCUMENTID=b.a_topicID and "
                + " b.a_articleID=c.SYS_DOCUMENTID and c.SYS_DELETEFLAG=0 and b.a_channel=2) as a_articleCount"
				+ " ,(select col_name from xy_columntopic d where d.sys_documentid = a.a_groupID) as a_icon "
                + " from xy_topics a"
                + " where " + getWhere(type)
                + " order by "+ orderSql;
		JSONArray list = query(sql, param.getBegin(), param.getCount());
        return list;
	}


    private JSONArray query(String sql, int paramBegin, int paramCount) {
		JSONArray result = new JSONArray();

		DBSession db = null;
		IResultSet rs = null;
		try {
			db = Context.getDBSession(docLib.getDsID());
			sql = db.getDialect().getLimitString(sql, paramBegin, paramCount);
			rs = db.executeQuery(sql, null);
			while (rs.next()) {
				JSONObject json = new JSONObject();
                String trParam = "<DocLibID>1O2r3d4e5r6I7D</DocLibID><DocID>1I2D3</DocID>";
                for (String field : FIELDS_IN_RELTABLE) {
                    if(!field.equals("SYS_DOCLIBID")){
                        json.put(field, rs.getString(field));
                    }
					if(field.equals("SYS_DOCUMENTID")){
                        trParam = trParam.replace("1I2D3",rs.getString(field));
                    }else if(field.equals("SYS_DOCLIBID")){
                        trParam = trParam.replace("1O2r3d4e5r6I7D",rs.getString(field));
                    }
				}
                json.put("trParam", trParam);
				json.put("DocOrder", String.valueOf(++paramBegin));

				result.add(json);
			}
		} catch (Exception e) {
			log.error("[TwoStepsDocList.getDocList(sql)]", e);
			log.error("[TwoStepsDocList.getDocList(sql)]--" + sql);
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(db);
		}
		return result;
	}


	private String assembleXML(int count, JSONArray list) {
		StringBuffer sbResult = new StringBuffer(1000);
		sbResult.append("<DocList><TotalSum>").append(count).append("</TotalSum>");
		try {
			//取出无权限的字段
			String[] fieldDisables = getFieldsDisabled();

			for (int i = 0; i < list.size(); i++) {
				JSONObject ele = (JSONObject)list.get(i);

				@SuppressWarnings("unchecked")
				Iterator<String> keys = (Iterator<String>)ele.keys();

				sbResult.append("<DocItem>");
                sbResult.append(ele.get("trParam"));
				while (keys.hasNext()) {
					String field = keys.next();
					sbResult.append("<").append(field).append(">");

					//只显示有权限的字段的值
					if (!ArrayUtils.contains(fieldDisables, field)) {
						sbResult.append(DocListHelper.parseXMLMark(ele.getString(field)));
					}

					sbResult.append("</").append(field).append(">");
				}
				sbResult.append("</DocItem>");
			}
			sbResult.append("</DocList>");
			return sbResult.toString();
		} catch (Exception e) {
			log.error("[TwoStepsDocList.getXML]", e);
			return EMPTY_XML;
		}
	}

    /**
     *查询稿件表和app稿件表中符合话题条件的数据数量
     */
    private int getListCount(String type) {
        String sql = "select count(1) from xy_topics a where " + getWhere(type);
        int count = queryCount(sql);

        return count;
    }

    private int queryCount(String countSql) {
        int count = 0;
        DBSession db = null;
        IResultSet rs = null;
        try {
            db = Context.getDBSession(docLib.getDsID());
            rs = db.executeQuery(countSql, null);
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception e) {
            log.error("[TwoStepsDocList.getDocList(sql)]", e);
            log.error("[TwoStepsDocList.getDocList(sql)]--" + countSql);
        } finally {
            ResourceMgr.closeQuietly(rs);
            ResourceMgr.closeQuietly(db);
        }

        return count;
    }

	public String getWhere(String type) {
		if (where != null) return where;
		//规则过滤器条件
		String rule_filter = ruleFormulaString();
		//检索条件
		rule_filter += queryString();


		//最后加上DELETEFLAG
		if (rule_filter.indexOf(BaseField.DELETEFLAG.getName()) < 0)
			rule_filter += " AND " + BaseField.DELETEFLAG.getName() + "=0";

		//去掉最前面的AND
		if (rule_filter.startsWith(" AND ")) rule_filter = rule_filter.substring(5);

		where = rule_filter;
        if(where.contains("a_1groupID")){
            where = where.replace("a_1groupID","a_groupID");
            return where;
        }

		//话题库，查看话题组下话题特殊处理
        String whereSql = "";
        if("2".equals(type)){
            if(where.contains("a_status")||where.contains("SYS_CREATED")||where.contains("SYS_TOPIC")||where.contains("SYS_DOCUMENTID")){
                where = where.replace("and", "AND");
                String[] whereSqlArray = where.split("AND");
                int arrLength = whereSqlArray.length;
                for(int i=0;i<arrLength;i++){
                    String field = whereSqlArray[i];
                    if(!field.contains("a_groupID")){
                        if(field.contains("SYS_DOCUMENTID")){
                            field = field.trim().replace("SYS_DOCUMENTID="," SYS_DOCUMENTID like '%")+"%'";
                        }
                        whereSql += "AND" + field;
                    }
                }
                where = whereSql.substring(3);
            }
        }

        return where;
    }
}
