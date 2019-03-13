package com.founder.xy.workspace.service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.founder.e5.commons.DomHelper;
import com.founder.e5.context.BaseField;
import com.founder.e5.context.CacheReader;
import com.founder.e5.context.Context;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.permission.Permission;
import com.founder.e5.permission.PermissionManager;
import com.founder.e5.sys.org.Role;
import com.founder.e5.web.SysUser;
import com.founder.e5.web.WebUtil;
import com.founder.e5.workspace.service.DefaultDocListService;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.system.site.SiteUserCache;

@Component("DoclistOriginalService")
public class DoclistOriginalService extends DefaultDocListService {

	@Autowired
	private PermissionManager permissionManager;
	
	public String whereInfo;
	
	@Override
	public String getDocList(int count, String sql){
		DBSession db = null;
		try
		{
			StringBuilder sb = new StringBuilder("SELECT * FROM xy_original WHERE ");
			sb.append(whereInfo);
			int orderIndex = sql.indexOf("order by");
			String sqlEnd = sql.substring(orderIndex);
			sb.append(sqlEnd);
			
			db = Context.getDBSession(docLib.getDsID());
			sql = sb.toString();
			sql = db.getDialect().getLimitString(sql, 0, param.getBegin() + param.getCount());
			if (log.isInfoEnabled()) log.info("DocList SQL : " + sql);
			
			IResultSet rs = db.executeQuery(sql, null);
			String result = getXML(count, rs);
			rs.close();
			return result;
		}
		catch (Exception e)
		{
			log.error("[DocListService.getDocList(sql)]", e);
			log.error("[DocListService.getDocList(sql)]--" + sql);
			return EMPTY_XML;
		}
		finally
		{
			try{db.close();}catch(Exception e1){e1.printStackTrace();}
		}	
	}
	
	/**
	 * 取出当前角色中  有权限的源稿分类栏目id字符串
	 * */
	public String getProcs(int roleID, String originalType) throws Exception {
		Permission[] permission = permissionManager.getPermissions(roleID, originalType);
		String resource = permission == null ? "" : permission[0].getResource();
		return resource;
	}
	
	private Role[] getRolesBySite(int userLibID, int userID, int siteID) {
		SiteUserCache siteUserCache = (SiteUserCache)(CacheReader.find(SiteUserCache.class));
		int[] roleIDs =  siteUserCache.getRoles(userLibID, userID, siteID);
		if (roleIDs == null || roleIDs.length == 0) return null;

		Role[] roles = new Role[roleIDs.length];
		for (int i = 0; i < roles.length; i++) {
			roles[i] = new Role();
			roles[i].setRoleID(roleIDs[i]);
		}
		return roles;
	}
	
	@Override
	public String getCountSQL() {
		String tableName = getTable();
		SysUser user = param.getUser();
		String ruleFormula = param.getRuleFormula().replace("a_catID_EQ_", "");
		String catID = ruleFormula.substring(0,ruleFormula.indexOf("_"));
		String siteID = WebUtil.get( param.getRequest(), "siteID");
		int userLibID = LibHelper.getUserExtLibID(param.getRequest());
		Role[] roles = getRolesBySite(userLibID, user.getUserID(), Integer.valueOf(siteID));
		boolean scopeFlag = false;
		for (Role role : roles) {
			String procScopeID = null;
			try {
				procScopeID = getProcs(role.getRoleID(), catID + "OriginalScope" + siteID);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if("1".equals(procScopeID)){
				scopeFlag = true;
				break;
			}
		}
		//String where = getWhere();
		StringBuilder sb = new StringBuilder();
		sb.append(" a_catID = ");
		sb.append(catID);
		sb.append(" AND a_siteID = ");
		sb.append(siteID);
		sb.append(" AND SYS_DELETEFLAG = 0 ");
		sb.append(getSqlMiddle(scopeFlag, user));
		
		if (!StringUtils.isBlank(param.getCondition())){
//			try {
//				String condition = URLDecoder.decode(param.getCondition(), "UTF-8") + " ";
//			} catch (UnsupportedEncodingException e) {
//				e.printStackTrace();
//			}
			String dbType = DomHelper.getDBType();
			
			String condition = param.getCondition();
			System.out.println(condition);
			condition = condition.substring(condition.indexOf("&"));
			condition = condition.replace("&", " AND ");
			if(condition.contains("a_keyword")){
				String keywordStr = condition.substring(condition.indexOf("a_keyword=") + 10);
				if(keywordStr.contains("AND")){
					keywordStr = keywordStr.substring(0, keywordStr.indexOf("AND"));
				}
				condition = condition.replace("a_keyword="+keywordStr, "a_keyword like '%"+ keywordStr +"%'");
			}
			if(condition.contains("SYS_CREATED_0")){
				String startTime = condition.substring(condition.indexOf("SYS_CREATED_0=") + 14);
				if(startTime.contains("AND")){
					startTime = startTime.substring(0, startTime.indexOf("AND"));
					//startTime = startTime.replace("%2000:00:00", " ");
				}
				if("mysql".equalsIgnoreCase(dbType)){
					condition = condition.replace("SYS_CREATED_0="+startTime, 
							" SYS_CREATED >= str_to_date('"+ startTime +"', '%Y-%m-%d %H:%i:%s') ");
				}else{
					condition = condition.replace("SYS_CREATED_0="+startTime, 
							" SYS_CREATED >= to_date('"+ startTime +"', 'yyyy-MM-dd hh:mm:ss') ");
				}
				condition = condition.replace("%20", " ");
			}
			if(condition.contains("SYS_CREATED_1")){
				String endTime = condition.substring(condition.indexOf("SYS_CREATED_1=") + 14);
				if(endTime.contains("AND")){
					endTime = endTime.substring(0, endTime.indexOf("AND"));
				}
				if("mysql".equalsIgnoreCase(dbType)){
					condition = condition.replace("SYS_CREATED_1="+endTime, 
							" SYS_CREATED < str_to_date('"+ endTime +"', '%Y-%m-%d %H:%i:%s') ");
				}else{
					condition = condition.replace("SYS_CREATED_1="+endTime, 
							" SYS_CREATED < to_date('"+ endTime +"', 'yyyy-MM-dd hh:mm:ss') ");
				}
				condition = condition.replace("%20", " ");
			}
			System.out.println(condition);
			sb.append(condition + " ");
			
		}
		
		where = sb.toString();
		whereInfo = where;
		
		StringBuffer sbSQL = new StringBuffer(500);
		if (isRelationTable())
			sbSQL.append("select count(distinct ");
		else
			sbSQL.append("select count(");
			sbSQL.append(BaseField.DOCUMENTID.getName());
		sbSQL.append(") from ").append(tableName);
		if (where != null && !"".equals(where.trim()))
			sbSQL.append(" where ").append(where);
		return sbSQL.toString();
	}
	
	private String getSqlMiddle(boolean scopeFlag, SysUser user) {
		String sqlMiddle = null;
		if(scopeFlag) {
			sqlMiddle = " AND SYS_DOCUMENTID NOT IN (select SYS_DOCUMENTID from xy_original where SYS_AUTHORID != "+user.getUserID()
					+ " AND A_STATUS = 0) ";
		}else{
			sqlMiddle = " AND SYS_AUTHORID = " + user.getUserID() + " ";
		}
		return sqlMiddle;
	}
	
}
