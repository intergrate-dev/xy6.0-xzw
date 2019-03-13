package com.founder.xy.workspace.service;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import com.founder.xy.commons.InfoHelper;
import com.founder.xy.workspace.MainHelper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.ArrayUtils;

import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.dom.DocTypeField;
import com.founder.e5.workspace.service.DocListHelper;

import javax.servlet.http.HttpServletRequest;

/**
 * 二刀流列表查询：
 * 1）先查栏目稿件关联表，得到ID；再查稿件库得到其它字段。
 * 2）先查Solr，得到ID；再查稿件库得到其它字段。
 * 
 * @author Gong Lijie
 */
public class DocListTwoSteps extends DocListService{
	//查关联表时固定查询的字段
	protected static final String[] FIELDS_IN_RELTABLE = new String[]{
			"SYS_DOCUMENTID", "a_linkTitle", "a_order", "a_priority", "a_position", "a_attr",
	};
	//查关联表时固定的SQL语句的前半部分。select SYS_DOCUMENTID,a_linkTitle,a_order,a_priority,a_position ..
	protected static final String SQL_PREFIX_IN_RELTABLE;
	static {
		String sql = "select ";
		for (int i = 0; i < FIELDS_IN_RELTABLE.length; i++) {
			if (i > 0) sql += ",";
			sql += FIELDS_IN_RELTABLE[i];
		}
		sql += " from ";
		SQL_PREFIX_IN_RELTABLE = sql;
	}
	
	@Override
	public String getDocList() {
		throw new RuntimeException("TwoStepsDocList: NO getDocList() SUPPORT!");
	}
	
	/**
	 * 两段式列表查询：关联表时，或检索时
	 * @param needRel
	 * @param needSolr
	 * @return
	 */
	public String getDocList(boolean needRel, boolean needSolr) {
		//long t1 = System.currentTimeMillis();
		JSONObject result = null;
		
		if (needSolr) {
			SolrDocListService solrService = new SolrDocListService(); 
			solrService.init(param);
			
			result = solrService.firstQuery(needRel);
		} else {
			HttpServletRequest request = param.getRequest();
			int siteID = MainHelper.getSiteEnable(request);
			//先读栏目稿件关联表，得到ID和链接标题等有限的几个字段值
			if(("否".equals(InfoHelper.getConfig("全文检索", "是否按当前栏目检索")))&&param.getCondition().contains("@QUERYCODE@=qArticle")&&param.getCondition().contains("SYS_DOCUMENTID="))
				param.setRuleFormula("a_siteID_EQ_"+siteID);
			result = firstQuery();
		}
		
		//无查询结果时，直接返回
		int count = result.getInt("count");
		if (count == 0) return EMPTY_XML;
		
		//再读稿件库，补充更多字段
		JSONArray list = result.getJSONArray("list");
		secondQuery(list);
		
		String xml = assembleXML(count, list);
		
		//long t2 = System.currentTimeMillis();
		//System.out.println("doclist two steps (ms):" + (t2 - t1));
		
		return xml;
	}
	
	//---------------第一步查询。查关联表，得到稿件ID等有限的几个字段-----------
	
	//从关联表中先取出符合条件的稿件列表，已经整理好顺序
	private JSONObject firstQuery() {
		JSONObject result = new JSONObject();
		
		//先按where条件查个数，若是空，则返回
		int count = getCount();
		result.put("count", count);
		
		if (count > 0) {
			JSONArray list = getListFromRelTable();
			result.put("list", list);
		}
		
		return result;
	}
	
	/**
	 * 读栏目稿件关联表，得到稿件ID、稿件顺序等几个字段
	 */
	private JSONArray getListFromRelTable() {
		//在栏目稿件关联表中读出符合条件的稿件
		String sql = SQL_PREFIX_IN_RELTABLE + tableName + " where " + getWhere() + " order by " + getOrderBy();
		JSONArray list = query(sql, param.getBegin(), param.getCount());
		
		if (list.isEmpty() || !needFixPosition()) return list;
		
		//读固定位置的稿件，若没有固定位置的稿件，不必处理
		sql = SQL_PREFIX_IN_RELTABLE + tableName + " where a_position>0 and " + getWhere() + " order by a_position";
		JSONArray posList =  query(sql, 0, 1000);
		if (posList == null || posList.isEmpty()) return list;
		
		//对每个固定位置的稿件进行处理
		for (Object object : posList) {
			JSONObject posOne = (JSONObject)object;
			fixOnePosition(list, posOne);
		}
		
		//若是加了或减了固定位稿件，则整理序号
		for (int i = 0; i < list.size(); i++) {
			JSONObject ele = (JSONObject)list.get(i);
			
			int oldOrder = Integer.parseInt(ele.getString("DocOrder"));
			int newOrder = param.getBegin() + i + 1;
			if (oldOrder != newOrder)
				ele.put("DocOrder", String.valueOf(newOrder));
		}
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
				for (String field : FIELDS_IN_RELTABLE) {
					json.put(field, rs.getString(field));
				}
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

	/**
	 * 处理一个固定位稿件：列表中有，则先删掉。然后加到固定位
	 */
	private void fixOnePosition(JSONArray list, JSONObject posOne) {
		long docID = Long.parseLong(posOne.getString("SYS_DOCUMENTID"));
		
		//先把原列表中的删掉
		for (int i = 0; i < list.size(); i++) {
			JSONObject ele = (JSONObject)list.get(i);
			long curDocID = Long.parseLong(ele.getString("SYS_DOCUMENTID"));
			if (curDocID == docID) {
				list.remove(i); //删除
				break;
			}
		}
		
		//若是第一页，则加进去。其它页只删不加，因为固定位稿件都在第一页显示
		if (param.getBegin() == 0) {
			int pos = Integer.parseInt(posOne.getString("a_position"));
			if (list.size() >= pos)
				list.add(pos - 1, posOne);
			else
				list.add(posOne);
		}
	}
	//判断是否需要按固定位置显示：是稿件、是按栏目显示、无查询条件
	private boolean needFixPosition() {
		return StringUtils.isBlank(param.getCondition());
	}
	
	//------------------第二步查询。按ID查稿件库，得到其它字段------------------
	
	private void secondQuery(JSONArray list) {
		//再按得到的稿件ID查询稿件库
		String sql = secondSQL(list);
	
		DBSession db = null;
		IResultSet rs = null;
		List<DocTypeField> fieldArr = getFields(param.getFields());
		try {
			db = Context.getDBSession(docLib.getDsID());
			rs = db.executeQuery(sql, null);
			
			while (rs.next()) {
				fillOne(rs, list, fieldArr);
			}
		} catch (Exception e) {
			log.error("[TwoStepsDocList.getDocList(sql)]", e);
			log.error("[TwoStepsDocList.getDocList(sql)]--" + sql);
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(db);
		}
	}
	//按稿件库里查到的数据补充已有的数据
	private void fillOne(IResultSet rs, JSONArray list,
			List<DocTypeField> fieldArr) throws SQLException {
		//按稿件ID找到list中的稿件
		long docID = rs.getLong("SYS_DOCUMENTID");
		JSONObject oneDoc = findArticleInList(list, docID);
		if (oneDoc == null)
			return;
		
		//补充新字段
		for (DocTypeField field : fieldArr) {
			if (!ArrayUtils.contains(FIELDS_IN_RELTABLE, field.getColumnCode()))
				oneDoc.put(field.getColumnCode(), getValue(rs, field));
		}
		oneDoc.put("DocID", String.valueOf(docID));
		oneDoc.put("DocLibID", String.valueOf(docLib.getDocLibID()));
	}
	
	private JSONObject findArticleInList(JSONArray list, long docID) {
		for (int i = 0; i < list.size(); i++) {
			JSONObject ele = (JSONObject)list.get(i);
			
			long curDocID = Long.parseLong(ele.getString("SYS_DOCUMENTID"));
			if (curDocID == docID) {
				return ele;
			}
		}
		return null;
	}

	private String secondSQL(JSONArray list) {
		//where SYS_DOCUMENTID in (1,2,3,4)
		String where = " where SYS_DOCUMENTID in (";
		for (int i = 0; i < list.size(); i++) {
			if (i > 0) where += ",";
			
			JSONObject ele = (JSONObject)list.get(i);
			where += ele.getString("SYS_DOCUMENTID");
		}
		where += ")";
		
		//select ...... from .... where ....
		String sql = "select " + getSelectField()
				+ " from " + docLib.getDocLibTable()
				+ where;
		return sql;
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
}
