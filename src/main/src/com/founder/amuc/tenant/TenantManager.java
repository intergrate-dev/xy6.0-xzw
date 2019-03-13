package com.founder.amuc.tenant;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.founder.amuc.commons.Constant;
import com.founder.amuc.commons.InfoHelper;
import com.founder.e5.commons.DomHelper;
import com.founder.e5.commons.Pair;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.e5.dom.DocLibManager;
import com.founder.e5.sys.org.Org;
import com.founder.e5.sys.org.OrgManager;
import com.founder.e5.sys.org.RoleManager;
import com.founder.e5.sys.org.User;
import com.founder.e5.sys.org.UserManager;
import com.founder.e5.sys.org.UserRole;

/**
 * 租户管理
 * @author Gong Lijie
 * 2014-5-28
 */
public class TenantManager {
	public static final String DEFAULTCODE = "uc";
	
	//按租户分别建库的文档类型
	private String[] typeCodes = new String[]{
			Constant.DOCTYPE_MEMBER,
			Constant.DOCTYPE_EVENTTRADING,
			Constant.DOCTYPE_MEMBEREVENT,
			Constant.DOCTYPE_MEMBERSCORE,
			Constant.DOCTYPE_SCOREUNUSUAL,
			//Constant.DOCTYPE_MEMBERCONVERT,
			Constant.DOCTYPE_MEMBERRELATION,
			Constant.DOCTYPE_MEMBERTAG,
			Constant.DOCTYPE_MEMBERGROUP,
			Constant.DOCTYPE_MEMBERACTION,
			Constant.DOCTYPE_ACTION,
			Constant.DOCTYPE_SCORERULE,
			Constant.DOCTYPE_RULEPROCESS,
			Constant.DOCTYPE_DUPLICATIONRULE,
			Constant.DOCTYPE_DUPLICATIONDATA,
			//Constant.DOCTYPE_TAG,
			Constant.DOCTYPE_GROUP,
			Constant.DOCTYPE_LEVEL,
			Constant.DOCTYPE_RIGHTS,
			Constant.DOCTYPE_MEMBERTYPE,
			Constant.DOCTYPE_INCOMELEVEL,
			Constant.DOCTYPE_COSTDETAIL,
			Constant.DOCTYPE_DEPOSITLEVEL,
	};
	
	//使用独立的流程记录表的文档类型：会员、活动、会员关系
	private String[] seperateLogTypes = new String[]{
			Constant.DOCTYPE_MEMBER,
			Constant.DOCTYPE_ACTION,
			Constant.DOCTYPE_MEMBERRELATION,
	};
	
	/**
	 * 租户部署：
		1）	创建同名、同代号的机构
		2）	在机构下创建租户管理员的角色和账号。
			使用用户的扩展属性1来保存租户代号。租户管理员可以在前台添加用户，添加时用户的扩展属性1中自动放入租户代号
		3）	以租户代号为表名的前缀创建一套文档库，文档库的名称都以租户名为前缀。
	 * 
	 * @param tLibID
	 * @param tID
	 * @return false=已部署，true=部署完成
	 * @throws E5Exception 
	 */
	public boolean deploy(int tLibID, long tID) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = docManager.get(tLibID, tID);
		
		//判断是否已经部署
		if (doc.getInt("teDeployed") == 1) {
			return false;
		}
		
		Tenant t = new Tenant();
		t.setId(tID);
		t.setCode(doc.getString("teCode").toLowerCase());//确保租户的代号是小写的：作为文档库前缀，可保证唯一性
		t.setName(doc.getString("teName"));
		
		//创建机构、用户、角色
		createOrgUser(t);
		//创建文档库
		createLibs(t);
		
		//回写
		doc.set("teDeployed", 1);
		doc.set("teOrgID", t.getOrgID());
		docManager.save(doc);
		
		return true;
	}
	
	//创建租户的机构、用户、角色
	private void createOrgUser(Tenant t) throws E5Exception {
		//创建同名、同代号的机构
		Org org = new Org();
		org.setCode(t.getCode());
		org.setName(t.getName());
		org.setParentID(0);
		org.setProperty1(t.getCode());
		
		OrgManager orgManager = (OrgManager)Context.getBean(OrgManager.class);
		orgManager.create(org);
		
		t.setOrgID(org.getOrgID());

		//在机构下创建租户管理员的角色和账号
		String userName = "管理员";
		String userCode = t.getCode() + "admin";
		
		User user = new User();
		user.setOrgID(org.getOrgID());
		user.setUserName(userName);
		user.setUserCode(userCode);
		user.setUserPassword(userCode);
		user.setProperty1(t.getCode());
		
		UserManager userManager = (UserManager)Context.getBean(UserManager.class);
		userManager.create(user);
		
		RoleManager roleManager = (RoleManager)Context.getBean(RoleManager.class);
		int roleID = roleManager.create(org.getOrgID(), userName);

		//把角色和账号挂钩
		UserRole userRole = new UserRole();
		userRole.setRoleID(roleID);
		userRole.setRoleName(userName);
		userRole.setUserCode(userCode);
		userRole.setUserID(user.getUserID());
		userRole.setTimeType(0);
		userRole.setTimeValue(0);
		userRole.setStartDate("2014-1-1");
		userRole.setStartTime("0:0");
		userRole.setEndDate("2044-1-1");
		userRole.setEndTime("23:59");
		
		roleManager.grantRole(user.getUserID(), userRole);
	}
	
	/**
	 * 创建租户的一套文档库
	 * 做法：对一个文档类型，取出系统默认创建的文档库，把表名前缀从uc改成租户代号。
	 * 要求：系统内必须已经存在了默认文档库，不然不能正确创建
	 * @param t
	 * @throws E5Exception
	 */
	private void createLibs(Tenant t) throws E5Exception {
		DocLibManager libManager = (DocLibManager)Context.getBean(DocLibManager.class);
		
		for (String typeCode : typeCodes) {
			int docTypeID = InfoHelper.getTypeIDByCode(typeCode);
			//从文档类型下取已有的文档库，系统默认创建的uc为前缀的文档库是第一个
			DocLib[] libs = libManager.getByTypeID(docTypeID);
			
			DocLib lib = copyLib(t, libs[0]);
			String ddl = libManager.generateDDL(lib);
			
			libManager.create(ddl, lib, !contains(seperateLogTypes, typeCode));
		}
	}
	private DocLib copyLib(Tenant t, DocLib refLib) {
		DocLib lib = new DocLib();
		lib.setDocLibName(t.getName() + "-" + refLib.getDocLibName());
		lib.setDocLibTable(refLib.getDocLibTable().replace(DEFAULTCODE, t.getCode()));
		lib.setDocTypeID(refLib.getDocTypeID());
		lib.setDsID(refLib.getDsID());
		lib.setKeepDay(refLib.getKeepDay());
		
		return lib;
	}
	
	private boolean contains(String[] arr, String value) {
		if (arr == null) return false;
		
		for (int i = 0; i < arr.length; i++) {
			if (value == arr[i]) return true;
		}
		return false;
	}

	/**
	 * 根据代号读出租户记录
	 * @param tenantCode
	 * @return
	 * @throws E5Exception
	 */
	public Tenant get(String tenantCode) throws E5Exception {
		if (StringUtils.isBlank(tenantCode))
			tenantCode = DEFAULTCODE;
		
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_TENANT);
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] docs = docManager.find(docLib.getDocLibID(), "teCode=?", new Object[]{tenantCode});
		Document doc = null;
		if (docs.length == 0) {
			//按租户代号查不到，则可能是默认系统（单租户），添加记录。
			doc = docManager.newDocument(docLib.getDocLibID(), InfoHelper.getID(docLib.getDocTypeID()));
			doc.setFolderID(docLib.getFolderID());
			doc.setDeleteFlag(0);
			doc.set("teName", "默认");
			doc.set("teCode", tenantCode);
			doc.set("teDeployed", 1);
			doc.set("teScorePeriod", 0);
			doc.set("teScoreType", 0);
			doc.set("teScoreMonitor", 0);
			doc.set("teOrgID", 0);
			doc.set("teLevelPeriod", 0);
			docManager.save(doc);
		} else 
			doc = docs[0];
		
		return copy2Data(doc);
	}
	
	/**
	 * 取出所有租户。用于积分有效期计算时
	 * @return
	 * @throws E5Exception
	 */
	public List<Tenant> getAll() throws E5Exception {
		List<Tenant> ts = new ArrayList<Tenant>();
		
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_TENANT);
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] docs = docManager.find(docLib.getDocLibID(), "SYS_DELETEFLAG=0", null);
		for (Document doc : docs) {
			ts.add(copy2Data(doc));
		}
		return ts;
	}

	/**
	 * 判断租户代号是否已经存在
	 * @param tenantCode
	 * @return
	 * @throws E5Exception
	 */
	public boolean exist(String tenantCode) throws E5Exception {
		int docLibID = DomHelper.getDocLibIDByTypeCode(Constant.DOCTYPE_TENANT);
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] ts = docManager.find(docLibID, "teCode=? and SYS_DELETEFLAG=0", new Object[]{tenantCode});
		return (ts != null && ts.length > 0);
	}
	
	/**
	 * 租户管理员创建用户：创建在同一机构下、把租户代号写在扩展属性1中、 自动给新用户同样的角色。
	 * @param adminID 租户管理员ID
	 * @param name 新用户名称
	 * @param code 新用户登录代码
	 * @param pwd 口令
	 * @throws E5Exception
	 */
	public void addUser(int adminID, String name, String code, String pwd) throws E5Exception {
		//取得租户管理员
		UserManager userManager = (UserManager)Context.getBean(UserManager.class);
		User admin = userManager.getUserByID(adminID);
		
		//新用户
		User nu = new User();
		nu.setUserCode(code);
		nu.setUserName(name);
		nu.setUserPassword(pwd);
		nu.setOrgID(admin.getOrgID());//同样的机构
		nu.setProperty1(admin.getProperty1());//记录租户代号
		
		userManager.create(nu);
		
		//取出租户管理员的角色
		RoleManager roleManager = (RoleManager)Context.getBean(RoleManager.class);
		UserRole[] urs = roleManager.getUserRoles(adminID);
		//新用户和租户管理员一样的角色
		urs[0].setUserCode(nu.getUserCode());
		urs[0].setUserID(nu.getUserID());
		
		roleManager.grantRole(nu.getUserID(), urs[0]);
	}
	
	/**
	 * 多租户模式下取基础数据的键值对，用于会员表单中的下拉框（会员分类定义、收入水平定义等），以及会员分类权限设置
	 * @param tenantCode 租户代号
	 * @param typeCode 文档类型code
	 * @param field 指定获取列
	 * @return Pair列表，Pair=<docID, 列值>
	 * @throws E5Exception
	 */
	public List<Pair> getData(String tenantCode, String typeCode, String field) throws E5Exception {
		int docLibID = InfoHelper.getLibID(typeCode, tenantCode);
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] docs = docManager.find(docLibID, "SYS_DELETEFLAG=0", null);
		
		List<Pair> ps = new ArrayList<Pair>();
		for (Document doc : docs) {
			Pair data =  new Pair(String.valueOf(doc.getDocID()), doc.get(field));
			ps.add(data);
		}
		return ps;
	}
	
	/**
	 * 设置租户的积分有效期
	 * @param tenantCode 租户代号
	 * @return 2个元素的数组，[0]=有效期类型，[1]=有效期时间
	 * @throws E5Exception
	 */
	public void saveScorePeriod(Tenant t) throws E5Exception {
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_TENANT);
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = docManager.get(docLib.getDocLibID(), t.getId());
		
		doc.set("teScoreType", t.getType());
		doc.set("teScorePeriod", t.getScorePeriod());
		doc.set("teScoreMonitor", t.getScoreMonitor());
		doc.set("teLevelPeriod", t.getLevelPeriod());
		
		docManager.save(doc);
	}

	private Tenant copy2Data(Document doc) {
		Tenant t = new Tenant();
		t.setId(doc.getDocID());
		t.setCode(doc.getString("teCode"));
		t.setName(doc.getString("teName"));
		t.setOrgID(doc.getInt("teOrgID"));
		t.setScorePeriod(doc.getInt("teScorePeriod"));
		t.setType(doc.getInt("teScoreType"));
		t.setScoreMonitor(doc.getInt("teScoreMonitor"));
		t.setLevelPeriod(doc.getInt("teLevelPeriod"));
		
		return t;
	}
}
