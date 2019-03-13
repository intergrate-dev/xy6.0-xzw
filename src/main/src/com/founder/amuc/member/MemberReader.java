package com.founder.amuc.member;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.founder.amuc.commons.Constant;
import com.founder.amuc.commons.InfoHelper;
import com.founder.amuc.tenant.TenantManager;
import com.founder.amuc.workspace.service.ExtractDocListService;
import com.founder.e5.commons.DomHelper;
import com.founder.e5.commons.Pair;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.e5.dom.DocTypeField;
import com.founder.e5.dom.DocTypeReader;
import com.founder.e5.sys.SysConfigReader;
import com.founder.e5.web.SysUser;
import com.founder.e5.workspace.ProcHelper;
import com.founder.e5.workspace.param.DocListParam;
import com.founder.e5.workspace.service.DocListService;

/**
 * 会员相关读取器：
 * 1）根据来源系统的原ID查找会员，需要判断是否经过查重合并（入库时使用）
 * 2）取会员字段
 * @author Gong Lijie
 * 2014-6-23
 */
public class MemberReader {
	/**
	 * 取出所有会员字段（查重规则定义时、导入会员匹配字段时使用）
	 * @return
	 * @throws E5Exception
	 */
	public List<Pair> getFields() throws E5Exception {
		int docTypeID = DomHelper.getDocTypeByCode(Constant.DOCTYPE_MEMBER).getDocTypeID();
		DocTypeReader docTypeReader = (DocTypeReader)Context.getBean(DocTypeReader.class);
		DocTypeField[] fields = docTypeReader.getFieldsExt(docTypeID);
		
		List<Pair> result = new ArrayList<Pair>();
		if (fields != null && fields.length > 0) {
			for (DocTypeField field : fields) {
				if (canShow(field))
					result.add(new Pair(field.getColumnCode(), field.getColumnName()));
			}
		}
		return result;
	}
	
	/**
	 * 读会员积分，用于对外api
	 * @param source 可选
	 * @param oriID
	 * @param tenantCode 可选
	 * @param table 可选
	 * @return
	 * @throws E5Exception
	 */
	public long score(String source, String id, String tenantCode, String table) throws E5Exception{
		
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER,tenantCode);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		String [] columns = {"mScore"};
		String condition = "SYS_DELETEFLAG = 0 and SYS_DOCUMENTID = ?";
		Document[] members = docManager.find(docLib.getDocLibID(), condition, new Object[]{id},columns);
		if (members == null)
			return 0;
		else
			return members[0].getLong("mScore");
	}
	/**
	 * 从会员表中取会员
	 * @param source
	 * @param id
	 * @param tenantCode
	 * @param oriTable
	 * @return
	 * @throws E5Exception
	 */
	public Document findMemberById(String source, String id, String tenantCode, String oriTable) throws E5Exception{
		
		if (tenantCode == null) tenantCode = TenantManager.DEFAULTCODE;

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib mdocLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER, tenantCode);
		String sql="SYS_DOCUMENTID=? and SYS_DELETEFLAG=0";
		Object[] queryParams=new Object[]{id};
		Document[] members=docManager.find(mdocLib.getDocLibID(), sql, queryParams);
		if(members!=null&&members.length>0){
			Document member=members[0];
			return member;
		}
		return null;
	}
	/**
	 * 通过高级检索查找会员，指定需要读出的字段。
	 * 用于活动的抽取客户等场合。
	 * @param request
	 * @param fields
	 * @return
	 * @throws Exception
	 */
	public List<String[]> queryDatas(HttpServletRequest request, String[] fields) throws Exception {
		DocListParam param = assembleParam(request, fields);// 组装参数

		ExtractDocListService listService = new ExtractDocListService();
		listService.init(param);
		return listService.getDatas();
	}
	
	/**
	 * 从主备会员关系表中取会员
	 * @param source
	 * @param oriID
	 * @param tenantCode
	 * @param oriTable
	 * @return
	 * @throws E5Exception
	 */
	public Document findMemberByOri(String source, String oriID, String tenantCode, String oriTable) throws E5Exception{
		int sourceID = InfoHelper.getMemberSourceCat(source);
		if (sourceID == 0) return null;
		
		if (tenantCode == null) tenantCode = TenantManager.DEFAULTCODE;

		String sql = null;
		Object[] params = null;
		if (!StringUtils.isBlank(oriTable)) {
			sql = "mrOriID=? and mrOriTable=? and mrSource_ID=? and SYS_DELETEFLAG=0";
			params = new Object[]{oriID, oriTable, sourceID};
		} else {
			sql = "mrOriID=? and mrSource_ID=? and SYS_DELETEFLAG=0";
			params = new Object[]{oriID, sourceID};
		}
		
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBERORI, tenantCode);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] members = docManager.find(docLib.getDocLibID(), sql, params);
		if (members.length > 0) {
			Document member = members[0];
			while (member != null) {
				DocLib mdocLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER, tenantCode);
				String querySql="SYS_DOCUMENTID=? and SYS_DELETEFLAG=0";
				Object[] queryParams=new Object[]{member.get("mrMemberID")};
				Document[] members2=docManager.find(mdocLib.getDocLibID(), querySql, queryParams);
				if(members2!=null&&members2.length>0){
					Document member2=members2[0];
					while (member2 != null) {
						long mergeID = member2.getLong("mMergeID");
						if (mergeID == 0 || mergeID == member2.getDocID()) {
							return member2;
						} 
						member2 = docManager.get(mdocLib.getDocLibID(), mergeID);
					}
				}
				
			}
		}
		return null;
	}

	private boolean canShow(DocTypeField field) {
		String code = field.getColumnCode();
		if (code.equals("mScore")//积分
				|| code.equals("mExperience")//经验值
				|| code.equals("mActivity")//活跃度
				|| code.equals("mSatisfaction")//满意度
//				|| code.equals("mLevel") //等级
				|| code.equals("mOriID") //原始系统的ID，只在采集时赋值
				|| code.equals("mOriTable") //原始系统的Table，只在采集时赋值
				|| code.equals("mMergeID") //查重合并后的ID
				|| code.equals("mPotential")//是否潜在会员
				|| code.equals("mHead") //头像
				|| code.equals("mAge") //年龄，这是前端计算显示的字段
				)
			return false;
		else
			return true;
	}

	//创建一个调用列表服务的参数
	private DocListParam assembleParam(HttpServletRequest request, String[] fields) throws Exception {
		DocListParam param = new DocListParam();

		param.setDocLibID(Integer.parseInt((String)request.getParameter("queryLib")));
		param.setRuleFormula(request.getParameter("rule"));
		param.setCondition(request.getParameter("query"));
		param.setTableName(null);
		
		param.setUser(ProcHelper.getUser(request));//当前用户
		param.setRequest(request);

		param.setBegin(0);
		param.setCount(10000);
		param.setCountOfPage(10000);
		param.addOrderBy("SYS_DOCUMENTID", true);
		param.setFields(fields);
		return param;
	}
	
	/**
	 * @param source 数据来源，如“网站”
	 * @param id 在数据来源系统中的ID
	 * @param tc 租户代号，可选
	 * @param table 在数据来源系统中的会员表名，可选
	 * @param getAll 是否获取全部记录 0-否，1-是
	 * @param curPage 当前页,从0开始
	 * @param pageSize 每页的数量
	 * @return
	 * @throws E5Exception
	 */
	public String scoreList(String source, String oriID, String tenantCode, 
			String table, int getAll, int curPage, int pageSize) throws E5Exception {
		String result = null;
		Document member = findMemberById(source, oriID, tenantCode, table);
		
		if (member == null)
			return null;
		else{
			DocListService listService = (DocListService)Context.getBean("APIDocListService");
			DocListParam param = assembleMSParam(tenantCode, member.getDocID(), curPage, pageSize);
			listService.init(param);
			if(getAll == 1){//获取全部记录
				curPage = 0;
				pageSize = listService.getCount();
				param.setBegin(curPage * pageSize);
				param.setCount(pageSize);
				param.setCountOfPage(pageSize);
				listService.init(param);
			}
			result = listService.getDocList();
			System.out.println(result);
		}
		return result;
	}
	
	/**
	 * @param memberID
	 * @param curPage
	 * @param pageSize
	 * @return
	 * @throws E5Exception
	 */
	private DocListParam assembleMSParam(String tenantCode, long memberID, int curPage, int pageSize) throws E5Exception {
		if (tenantCode == null) tenantCode = TenantManager.DEFAULTCODE;
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBERSCORE, tenantCode);
		int docTypeID = docLib.getDocTypeID();
		DocTypeReader docTypeReader = (DocTypeReader)Context.getBean(DocTypeReader.class);
		DocTypeField[] fields = docTypeReader.getFieldsExt(docTypeID);
		String[] nfields = null;
		if (fields != null && fields.length > 0) {
			nfields = new String[fields.length];
			int i = 0;
			for (DocTypeField field : fields) {
				nfields[i] = field.getColumnCode();
				i++;
			}
		}
		DocListParam param = new DocListParam();

		param.setDocLibID(docLib.getDocLibID());
		param.setRuleFormula("");
		param.setCondition("SYS_DELETEFLAG=0 and msMember_ID=" + memberID);
		param.setTableName(null);
		
		param.setUser(new SysUser());//当前用户

		param.setBegin(curPage * pageSize);
		param.setCount(pageSize);
		param.setCountOfPage(pageSize);
		param.addOrderBy("SYS_DOCUMENTID", true);
		param.setFields(nfields);
		return param;
	}
	
	/**
	 * 是否正式会员，如果是指定数据来源的所选属性中有一个非空，则是正式会员
	 * @param member
	 * @return
	 * @throws E5Exception
	 */
	public boolean isFormal(Document member) throws E5Exception{
		int appID = 1;
		String project = "有效性规则";
		String sourceItem = "数据来源";
		String fieldsItem = "属性要求";
		
		SysConfigReader configReader = (SysConfigReader)Context.getBean(SysConfigReader.class);
		String sourceValue = configReader.get(appID, project, sourceItem);
		String fieldsValue = configReader.get(appID, project, fieldsItem);
		if(sourceValue != null && sourceValue.length() > 0 
				&& fieldsValue != null && fieldsValue.length() > 0 ){
			String[] sourceArr = sourceValue.split(",");
			String[] fieldsArr = fieldsValue.split(",");
			String mSource = member.getString("mSource");
			if(mSource != null && mSource.length() > 0 && sourceArr != null && sourceArr.length > 0){
				for(String source : sourceArr){
					if(mSource.equals(source) && fieldsArr != null && fieldsArr.length > 0){//判断是该数据源的，并且属性不为空
						for(String field : fieldsArr){
							if(member.get(field) != null){
								return true;
							}
						}
					}
				}
			}			
		}
		
		return false;
	}
	
	/** 
	* @author  leijj 
	* 功能： 根据手机号查询会员是否存在
	* @param tenantCode
	* @param mobile
	* @return
	* @throws E5Exception 
	*/ 
	/*public boolean existMobile(String tenantCode, String mobile) throws E5Exception {
		String sql = null;
		Object[] params = null;
		if (!StringUtils.isBlank(mobile)) {
			sql = "mMobile=? and SYS_DELETEFLAG=0";
			params = new Object[]{mobile};

			DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER, tenantCode);
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			Document[] members = docManager.find(docLib.getDocLibID(), sql, params);
			if (members.length > 0) {
				return true;
			}
		}
		return false;
	}*/
	
	/** 
	* @author  leijj 
	* 功能： 根据手机号查询会员是否存在
	* @param tenantCode
	* @param mobile
	* @return
	* @throws E5Exception 
	*/ 
	public Document getMemberByMobile(String tenantCode, String mobile) throws E5Exception {
		String sql = null;
		Object[] params = null;
		if (!StringUtils.isBlank(mobile)) {
			sql = "mMobile=? and SYS_DELETEFLAG=0";
			params = new Object[]{mobile};

			DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER, tenantCode);
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			Document[] members = docManager.find(docLib.getDocLibID(), sql, params);
			if (members.length > 0) {
				return members[0];
			}
		}
		return null;
	}
}