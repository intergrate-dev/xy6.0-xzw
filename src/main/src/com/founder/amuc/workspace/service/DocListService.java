package com.founder.amuc.workspace.service;

import java.sql.SQLException;
import java.util.Date;

import com.founder.amuc.commons.Constant;
import com.founder.amuc.commons.InfoHelper;
import com.founder.amuc.tenant.TenantManager;
import com.founder.amuc.workspace.query.QueryParser;
import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.BaseField;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.IResultSet;
import com.founder.e5.dom.DocLib;
import com.founder.e5.permission.Permission;
import com.founder.e5.permission.PermissionReader;
import com.founder.e5.workspace.service.DefaultDocListService;
import com.founder.e5.workspace.service.DocListHelper;

/**
 * 列表查询的服务类，继承e5。
 * 主要用于处理增加的公式符号@DEPTS@和@MEMBERTYPES@
 * @author Gong Lijie
 */
public class DocListService extends DefaultDocListService{

	private String parseWhere(String formula) {
		//若是多租户，则不判断部门权限和会员分类权限
		String tenantCode = InfoHelper.getTenantCode(param.getRequest());
		if (StringUtils.isBlank(tenantCode) || tenantCode.equals(TenantManager.DEFAULTCODE)) {
			try {
				//部门权限
				if (formula.indexOf("@DEPTS@") >= 0) {
					String depts = getDepts(param.getUser().getRoleID(),param.getFvID());
					formula = formula.replace("@DEPTS@", depts);
				}
				//会员分类权限
				if (formula.indexOf("@MEMBERTYPES@") >= 0) {
					String depts = getMemberTypes(param.getUser().getRoleID());
					formula = formula.replace("@MEMBERTYPES@", depts);
				}
				//高级检索--群组--自定义部门权限 fanjc
				if (formula.indexOf("@GROUPDEPTS@") >= 0) {
					DocLib groupDocLib=InfoHelper.getLib(Constant.DOCTYPE_GROUP);
					String depts = getDepts(param.getUser().getRoleID(),groupDocLib.getDocLibID());
					formula = formula.replace("@GROUPDEPTS@", depts);
				}
				//高级检索--活动--自定义部门权限 fanjc
				if (formula.indexOf("@ACTIONDEPTS@") >= 0) {
					DocLib actionDocLib=InfoHelper.getLib(Constant.DOCTYPE_ACTION);
					String depts = getDepts(param.getUser().getRoleID(),actionDocLib.getDocLibID());
					formula = formula.replace("@ACTIONDEPTS@", depts);
				}
				
				//群成员部门、分类权限
				if(formula.indexOf("@GROUPMEMBERDEPTS@")>=0){
					DocLib docLib=InfoHelper.getLib(Constant.DOCTYPE_MEMBER);
					String depts=getGroupDepts(param.getUser().getRoleID(),docLib.getDocLibID());
					formula = formula.replace("@GROUPMEMBERDEPTS@", depts);
				}
				//活动成员部门、分类权限
				if(formula.indexOf("@ACTIONMEMBERDEPTS@")>=0){
					DocLib docLib=InfoHelper.getLib(Constant.DOCTYPE_MEMBER);
					String depts=getActionDepts(param.getUser().getRoleID(),docLib.getDocLibID());
					formula = formula.replace("@ACTIONMEMBERDEPTS@", depts);
				}
			} catch (E5Exception e) {
				e.printStackTrace();
			}
		} else {
			formula = formula.replace("@DEPTS@", "1=1");
			formula = formula.replace("@MEMBERTYPES@", "1=1");
			formula=formula.replace("@GROUPMEMBERDEPTS@", "1=1");
			formula=formula.replace("@ACTIONMEMBERDEPTS@", "1=1");
		}
		return formula;
	}
	//部门权限的查询串。不设部门时为0，都可查看
	private String getDepts(int roleID,int fvID) throws E5Exception {
		PermissionReader pReader = (PermissionReader)Context.getBean(PermissionReader.class);
		//UserReader userReader=(UserReader) Context.getBean(UserReader.class);
		Permission[] ps = pReader.getPermissions(roleID, "OrgPermission"+fvID);
		DocLib memberDocLib=InfoHelper.getLib(Constant.DOCTYPE_MEMBER);
		DocLib groupDocLib=InfoHelper.getLib(Constant.DOCTYPE_GROUP);
		DocLib actionDocLib=InfoHelper.getLib(Constant.DOCTYPE_ACTION);
		//Org org=userReader.getParentOrg(param.getUser().getUserID());//获取用户所在的部门
		if (ps != null&&memberDocLib.getDocLibID()==fvID) {
			return "mOrg_ID in (0," + ps[0].getResource() + ")";
		}else if(ps!=null&&groupDocLib.getDocLibID()==fvID){
			return "gOrg_ID in (0,"+ps[0].getResource()+")";
		}else if(ps!=null&&actionDocLib.getDocLibID()==fvID){
			//return "aOrgDept_ID in (0,"+ps[0].getResource()+")";
			//return "exists(select ID from MemberAction_Split(ucAction.aOrgDept_ID,',') where ID in("+org.getOrgID()+","+ps[0].getResource()+")) or aOrgDept_ID=''";
			return "exists(select ID from MemberAction_Split(ucAction.aOrgDept_ID,',') where ID in("+ps[0].getResource()+")) or aOrgDept_ID=''";
		}/*else if(ps==null&&actionDocLib.getDocLibID()==fvID){
			//return "exists(select ID from MemberAction_Split(ucAction.aOrgDept_ID,',') where ID in("+org.getOrgID()+")) or aOrgDept_ID=''";
		}*/
		return "1=1";
	}
	//会员分类权限的查询串。不设时为0，都可查看
	private String getMemberTypes(int roleID) throws E5Exception {
		PermissionReader pReader = (PermissionReader)Context.getBean(PermissionReader.class);
		Permission[] ps = pReader.getPermissions(roleID, "MemberTypePermission");
		if (ps != null) {
			return "mType_ID in (0," + ps[0].getResource() + ")";
		}
		return "mType_ID=0";
	}

	//群成员部门权限和分类权限
	private String getGroupDepts(int roleID,int fvID) throws E5Exception {
		PermissionReader pReader = (PermissionReader)Context.getBean(PermissionReader.class);
		Permission[] ps = pReader.getPermissions(roleID, "OrgPermission"+fvID);
		Permission[] ps2 = pReader.getPermissions(roleID, "MemberTypePermission");
		DocLib memberDocLib=InfoHelper.getLib(Constant.DOCTYPE_MEMBER);
		//Org org=userReader.getParentOrg(param.getUser().getUserID());//获取用户所在的部门
		String result="1=1";
		StringBuffer sql=new StringBuffer("select DISTINCT SYS_DOCUMENTID from "+memberDocLib.getDocLibTable()+" where 1=1");
		if(ps!=null||ps2!=null){
			if(ps!=null){
				sql.append(" and mOrg_ID in(0,"+ps[0].getResource()+")");
			}
			if(ps2!=null){
				sql.append(" and mType_ID in(0,"+ps2[0].getResource()+")");
			}
			result= "mgMemberID in (" + sql.toString() + ")";
		}
		
		return result;
	}
		
	//活动成员部门权限和分类权限
	private String getActionDepts(int roleID,int fvID) throws E5Exception {
		PermissionReader pReader = (PermissionReader)Context.getBean(PermissionReader.class);
		Permission[] ps = pReader.getPermissions(roleID, "OrgPermission"+fvID);
		Permission[] ps2 = pReader.getPermissions(roleID, "MemberTypePermission");
		DocLib memberDocLib=InfoHelper.getLib(Constant.DOCTYPE_MEMBER);
		//Org org=userReader.getParentOrg(param.getUser().getUserID());//获取用户所在的部门
		String result="1=1";
		StringBuffer sql=new StringBuffer("select DISTINCT SYS_DOCUMENTID from "+memberDocLib.getDocLibTable()+" where 1=1");
		if(ps!=null||ps2!=null){
			if(ps!=null){
				sql.append(" and mOrg_ID in(0,"+ps[0].getResource()+")");
			}
			if(ps2!=null){
				sql.append(" and mType_ID in(0,"+ps2[0].getResource()+")");
			}
			result= "maMemberID in (" + sql.toString() + ")";
		}
		
		return result;
	}
		
	/**
	 * 复写此方法，去掉SYS_DOCLIB/SYS_FOLDERID的条件。
	 * 在本系统里，不创建子文件夹，因此不必把FOLDERID作为缺省条件。
	 */
	public String getWhere() {
		if (where != null) return where;
		
		StringBuffer sbResult = new StringBuffer(300);

		//规则过滤器条件
		String rule_filter = ruleFormulaString();
		rule_filter = parseWhere(rule_filter);
		
		sbResult.append(rule_filter);

		//检索条件
		sbResult.append(queryString());

		//最后加上DELETEFLAG
		if (sbResult.toString().indexOf(BaseField.DELETEFLAG.getName()) < 0)
			sbResult.append(" AND ").append(BaseField.DELETEFLAG.getName()).append("=0");
		
		//去掉最前面的AND
		where = sbResult.toString();
		if (where.startsWith(" AND ")) where = where.substring(5);
		
		return where;
	}
	//覆写getValue，当读年龄时，改成根据生日来计算
	protected String getValue(IResultSet rs, String columnCode, String fieldType) {
		if (columnCode.equals("mAge")) {
			try {
				Date birthday = rs.getDate("mBirthday");
				if (birthday == null) return "";
				
				int year = yearInterval(DateUtils.getDate(), birthday);
				return String.valueOf(year);
			} catch (SQLException e) {
				e.printStackTrace();
				return "";
			}
		} else {
			return super.getValue(rs, columnCode, fieldType);
		}
	}
	//两个日期的相差年数
	private int yearInterval(Date date1, Date date2) {
		long intervalMilli = date1.getTime() - date2.getTime();
		return (int) (intervalMilli / (24 * 60 * 60 * 1000 * 365L));
	}
	
	//覆写getSelectField方法，当会员列表上有年龄没有生日时，手工把生日加入select字段。
	public String getSelectField() {
		if (fields != null) return fields;
		
		StringBuffer sbFields = new StringBuffer(300);
		sbFields.append(BaseField.DOCUMENTID.getName()).append(",");
		String[] fieldArr = param.getFields();
		String strUpperField;
		
		boolean hasAge = false, hasBirthday = false;
		
		for (int i = 0; i < fieldArr.length; i++) {
			if (fieldArr[i].equals("mAge")) {
				hasAge = true;
			} else if (fieldArr[i].equals("mBirthday")) {
				hasBirthday = true;
			}
			
			strUpperField = fieldArr[i].toUpperCase();
			if (!strUpperField.equals(BaseField.DOCUMENTID.getName()) 
					&& (!strUpperField.equals(BaseField.DOCLIBID.getName())))
				//sbFields.append(strUpperField).append(",");
				sbFields.append(fieldArr[i]).append(",");
		}
		if (hasAge && !hasBirthday) {
			sbFields.append("mBirthday,");
		}
		sbFields.append(BaseField.DOCLIBID.getName()).append(" ");
		
		fields = sbFields.toString();
		
		return fields;
	}
	
	/**
	 * 检索条件
	 * @return
	 */
	protected String queryString() {
		if (StringUtils.isBlank(param.getCondition())) return "";
		
		if (!param.getCondition().startsWith("@QUERYCODE@=")){
			return " AND " + DocListHelper.parseSQL(param.getCondition(), param.getUser());
		} else {
			//增加对定制的查询条件的处理
			QueryParser parser = (QueryParser)Context.getBean(QueryParser.class);
			String tenantCode = InfoHelper.getTenantCode(param.getRequest());
			String query = parser.getSQL(docLib.getDocTypeID(), param.getCondition(), tenantCode);
			
			if (!StringUtils.isBlank(query))
				return " AND " + query;
			else
				return "";
		}
	}
}
