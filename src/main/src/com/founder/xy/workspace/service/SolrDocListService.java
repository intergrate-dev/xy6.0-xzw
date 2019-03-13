package com.founder.xy.workspace.service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.founder.xy.commons.InfoHelper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.ModifiableSolrParams;

import com.founder.e5.context.CacheReader;
import com.founder.xy.system.site.SiteSolrServerCache;

/**
 * solr文档查询列表服务类
 * @author Guo Qixun, Gong Lijie
 */
public class SolrDocListService extends DocListTwoSteps {
	private String[] fields;
	
	//查Solr稿件库时固定查询的字段
	private static final String[] FIELDS_IN_SOLR = new String[]{
			"SYS_DOCUMENTID"
	};
	
	//查Solr关联表时固定查询的字段
	private static final String[] FIELDS_IN_SOLR_REL = new String[]{
			"SYS_DOCUMENTID", "a_linkTitle", "a_order",
	};
	
	protected JSONObject firstQuery(boolean isRel) {
		fields = isRel ? FIELDS_IN_SOLR_REL : FIELDS_IN_SOLR;
		//查询Solr库
		QueryResponse response = null;
		
		SolrServer server = getSolrServer();
		ModifiableSolrParams params = getSolrParams();
		params.set("q",params.get("q")+" AND a_siteID:\"" + param.getRequest().getParameter("siteID")+"\"");
		try {
			response = server.query(params);
		} catch (SolrServerException e) {
			log.error("solr connect error:", e);
		}
		
		//若没有结果，则返回空
		JSONObject result = new JSONObject();
		if (response == null) {
			result.put("count", 0);
			return result;
		}
		SolrDocumentList list = response.getResults();
		long count = list.getNumFound();
		if (count == 0) {
			result.put("count", 0);
			return result;
		}
		
		//把Solr检索结果组织成json返回
		JSONArray jsonList = assembleJSONArray(list);
		result.put("count", count);
		result.put("list", jsonList);
		
		return result;
	}

	/**
	 * 组织solr查询参数
	 * @return ModifiableSolrParams
	 */
	private ModifiableSolrParams getSolrParams(){
		ModifiableSolrParams params = new ModifiableSolrParams();
		//查询字段
		String fields = StringUtils.join(this.fields,",");
		params.set("fl", fields);
		params.set("q", "*:*");
		
		StringBuffer qBuffer = new StringBuffer();
		String where = getWhere();
		if (where != null && !"".equals(where.trim())){
			String[] conditions = parseWhereConditions(where);
			for(int i=0;i<conditions.length;i++){
				String condition = StringUtils.trim(conditions[i]);
				String[] fq = condition.split("=");
				if (fq.length == 2 && "a_columnID".equals(fq[0]) && "0".equals(fq[1])) {
					continue;
				}
				if(fq.length == 2&&(!"否".equals(InfoHelper.getConfig("全文检索", "是否按当前栏目检索"))||!"CLASS_1".equals(fq[0]))){
                    qBuffer.append(fq[0] + ":\"" + ClientUtils.escapeQueryChars(fq[1]) + "\"");
                    qBuffer.append(" AND ");
                }else if(StringUtils.contains(condition, "like")){
                    //(SYS_TOPIC like '%草稿箱%')
					String f = StringUtils.trim(StringUtils.substringBetween(condition, "(","like"));
					if("SYS_TOPIC".equals(f)){
						f = "text";
					}
					String v = StringUtils.substringBetween(condition, "%");
					if("AND".equals(v) || "OR".equals(v) || "NOT".equals(v)){
						continue;
					}
					qBuffer.append(f+":\""+ClientUtils.escapeQueryChars(v)+"\"");
					qBuffer.append(" AND ");
				}else if(StringUtils.contains(condition, "in")){
					String[] inCondition = condition.split("in");
					String f = StringUtils.trim(inCondition[0]);
					String v = StringUtils.substringBetween(condition, "(",")");
					qBuffer.append("(");
					String[] values = v.split(",");
					for(int j=0;j<values.length;j++){
						qBuffer.append(f+":"+ClientUtils.escapeQueryChars(values[j]));
						if(j < values.length-1){
							qBuffer.append(" OR ");
						}
					}
					qBuffer.append(")");
					qBuffer.append(" AND ");
				}else if(StringUtils.contains(condition, "between")){
					String[] dateCondition = condition.split("between");
					String f = StringUtils.trim(dateCondition[0]);
					String startDate = StringUtils.substringBetween(dateCondition[1], "'").substring(0,10)+"T00:00:00Z";
					String endDate = StringUtils.substringBetween(conditions[i+1], "'").substring(0,10)+"T23:59:59Z";
					qBuffer.append(f+":["+startDate+" TO "+endDate+"]");
					qBuffer.append(" AND ");
					i=i+1;
				}
			}
			String qStr = StringUtils.stripEnd(qBuffer.toString(), " AND ");
			params.set("q", qStr);
		}
		String orderBy = getOrderBy();
		if (orderBy != null && !"".equals(orderBy)){
			String[] orderBys = orderBy.split(",");
			for(String sort : orderBys){
				params.set("sort", sort);
			}
		}
		params.set("start", param.getBegin());
		params.set("rows", param.getCount());
		return params;
	}
	
	/**
	 * 解析where条件
	 * @param where
	 * @return 
	 */
	private String[] parseWhereConditions(String where){
		Pattern p = Pattern.compile("'[\\s\\S]*?'");
		Matcher m = p.matcher(where);
		int i = 0;
		Map<String,String> solrStringVlaueMap = new HashMap<String,String>();
		while(m.find()){
			where = StringUtils.replaceOnce(where, m.group(), "solrStringVlaue"+i);
			solrStringVlaueMap.put("solrStringVlaue"+i, m.group());
			i++;
		}
		String[] conditionsTmp = where.split("[A|a][N|n][D|d]");
		String[] conditions = new String[conditionsTmp.length];
		for(int j=0;j<conditionsTmp.length;j++){
			for(String key : solrStringVlaueMap.keySet()){
				conditionsTmp[j] = StringUtils.replace(conditionsTmp[j], key, solrStringVlaueMap.get(key));
			}
			conditions[j] = conditionsTmp[j];
		}
		return conditions;
	}

	private JSONArray assembleJSONArray(SolrDocumentList list) {
		JSONArray result = new JSONArray();
		
		int begin = param.getBegin();
		try {
			for (SolrDocument doc : list){
				if (doc.isEmpty()) continue;
				
				JSONObject json = new JSONObject();
				
				for (String field : fields) {
					json.put(field, doc.getFirstValue(field).toString());
				}
				json.put("DocOrder", String.valueOf(++begin));
				
				result.add(json);
			}
		} catch (Exception e) {
			log.error("[SolrDocListService.getXML]", e);
		}
		return result;
	}

	private SolrServer getSolrServer() {
		int siteID = Integer.parseInt(param.getRequest().getParameter("siteID"));

		SiteSolrServerCache siteSolrServerCache = (SiteSolrServerCache) (CacheReader.find(SiteSolrServerCache.class));
		SolrServer server = null;
		String table = getTable();
		if (table.endsWith("_article")) {
			//Web稿件库
			server = siteSolrServerCache.getSolrArticleServerBySiteID(siteID);
		} else if (table.endsWith("_articleApp")) {
			//App稿件库
			server = siteSolrServerCache.getSolrArticleAppServerBySiteID(siteID);
		} else if (table.startsWith("DOM_REL_Web")) {
			//Web发布库（按栏目关联） 不跨栏目检索时
			if (!"否".equals(InfoHelper.getConfig("全文检索", "是否按当前栏目检索")))
				server = siteSolrServerCache.getSolrServerBySiteID(siteID);
			//跨栏目检索时，直接检索稿件表，不检索关联稿件
			else server = siteSolrServerCache.getSolrArticleServerBySiteID(siteID);
		} else if (table.startsWith("DOM_REL_App")) {
			if (!"否".equals(InfoHelper.getConfig("全文检索", "是否按当前栏目检索")))
				//App发布库（按栏目关联）
				server = siteSolrServerCache.getSolrAppServerBySiteID(siteID);
				//跨栏目检索时，直接检索稿件表，不检索关联稿件
			else server = siteSolrServerCache.getSolrArticleAppServerBySiteID(siteID);
		} else {
			//原稿库
			server = siteSolrServerCache.getSolrOriginalServerBySiteID(siteID);
		}
		return server;
	}
}
