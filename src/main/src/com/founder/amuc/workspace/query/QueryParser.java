package com.founder.amuc.workspace.query;

import com.founder.amuc.commons.Constant;
import com.founder.amuc.commons.InfoHelper;
import com.founder.e5.commons.Pair;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.E5Exception;
import com.founder.e5.dom.DocLib;
import com.founder.e5.dom.DocTypeField;
import com.founder.e5.workspace.query.QueryFormParser;
/** 
 * @created 2014年12月2日 上午10:12:24 
 * @author  leijj
 * 类说明 ： 定制的查询条件在跟着DocList提交到后台时的解析
 */
public class QueryParser extends QueryFormParser {
	/**
	 * 按查询条件组织成SQL片段，这是解析的最终结果
	 * @param docTypeID
	 * @param conditions
	 * @return
	 */
	public String getSQL(int docTypeID, String conditions, String tenantCode) {
		//读出界面上查询条件中输入的参数
		Pair[] params = getParameters(conditions);
		
		//取得定制的查询条件的字段
		String formCode = getParameter(params, "@QUERYCODE@");
		DocTypeField[] fields = fetchQueryFields(docTypeID, formCode);
		if (fields == null) return null;
		
		StringBuilder result = new StringBuilder();
		//按每个字段读参数值
		for (DocTypeField field : fields) {
			String query = null;
			//不是本文档类型的字段，则需要上层应用自己处理
			if (field.getDocTypeID() != docTypeID) {
				query = getSQLOtherType(docTypeID, field, params,  tenantCode);
			} else {
				if (field.getColumnCode().equalsIgnoreCase("pcExpiryDate") && field.getDataType().equalsIgnoreCase("VARCHAR")) {
					field.setDataType("INTEGER");
				}
				query = getSQLByField(field, params);
			}
			if (StringUtils.isBlank(query)) continue;
			
        	if (result.length() > 0) result.append(" and ");
        	result.append(query);
		}
		return result.toString();
	}
	/**
	 * 解析非本表字段的查询条件
	 * 
	 * @param field
	 * @param params
	 * @return
	 */
	protected String getSQLOtherType(int srcDocTypeID, DocTypeField field, Pair[] params, String tenantCode) {
		StringBuilder result = new StringBuilder();
		String query = getSQLByField(field, params);
		if (StringUtils.isBlank(query))
			return null;
		//会员查询
		if (srcDocTypeID == InfoHelper.getTypeIDByCode(Constant.DOCTYPE_MEMBER)) {
			if (field.getDocTypeID() == InfoHelper.getTypeIDByCode(Constant.DOCTYPE_MEMBERTAG)) {
				//会员标签查询
				try {
					DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBERTAG, tenantCode);
					String table = InfoHelper.getDocTableName(docLib.getDocLibID());
					//会员的标签
					result.append("SYS_DOCUMENTID IN(SELECT mtMemberID FROM ").append(table)
						.append(" WHERE ").append(query).append(")");
				} catch (E5Exception e) {
					e.printStackTrace();
				}
			}
		}
		return result.toString();
	}
}