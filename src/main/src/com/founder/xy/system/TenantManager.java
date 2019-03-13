package com.founder.xy.system;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import com.founder.e5.commons.ArrayUtils;
import com.founder.e5.commons.DomHelper;
import com.founder.e5.commons.Pair;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.CacheManager;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.ColumnInfo;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.DataType;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.e5.dom.DocLibManager;
import com.founder.e5.dom.DocType;
import com.founder.e5.dom.DocTypeField;
import com.founder.e5.dom.DocTypeReader;
import com.founder.e5.dom.FolderViewCache;
import com.founder.e5.dom.facade.EUIDFacade;
import com.founder.e5.rel.model.RelTable;
import com.founder.e5.rel.model.RelTableDocLibFields;
import com.founder.e5.rel.model.RelTableDocLibVO;
import com.founder.e5.rel.model.RelTableField;
import com.founder.e5.rel.model.RelTableVO;
import com.founder.e5.rel.service.RelTableCache;
import com.founder.e5.rel.service.RelTableDocLibFieldsManager;
import com.founder.e5.rel.service.RelTableDocLibManager;
import com.founder.e5.rel.service.RelTableManager;
import com.founder.e5.sys.org.Org;
import com.founder.e5.sys.org.OrgManager;
import com.founder.e5.sys.org.OrgRoleUserCache;
import com.founder.e5.sys.org.RoleManager;
import com.founder.e5.sys.org.User;
import com.founder.e5.sys.org.UserManager;
import com.founder.e5.sys.org.UserRole;
import com.founder.xy.commons.CatTypes;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.config.Channel;
import com.founder.xy.config.ConfigReader;

/**
 * 租户管理
 * @author Gong Lijie
 * 2014-5-28
 */
@Component
public class TenantManager {
	/**全局文档类型，也就是不按租户分别建库的文档类型*/
	private int[] globalTypes;
	
	/**归档时需要的文档类型*/
	private int[] archiveTypes;
	
	/**使用独立的流程记录表的文档类型*/
	private int[] seperateLogTypes;
	
	/**
	 * 租户部署：
		1）	创建同名、同代号的机构
		2）	在机构下创建租户管理员账号。
			使用用户的扩展属性1来保存租户代号。租户管理员可以在前台添加用户，添加时用户的扩展属性1中自动放入租户代号
		3）	以租户代号为表名的前缀创建一套文档库，文档库的名称都以租户名为前缀。
	 * 
	 * @param tLibID
	 * @param tID
	 * @return false=已部署，true=部署完成
	 * @throws E5Exception 
	 */
	public boolean deploy(int tLibID, long tID, int datasource) throws E5Exception {
		init();
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = docManager.get(tLibID, tID);
		
		//判断是否已经部署
		if (doc.getInt("te_deployed") == 1) {
			return false;
		}
		
		Tenant t = new Tenant();
		t.setId(tID);
		t.setCode(doc.getString("te_code").toLowerCase());//确保租户的代号是小写的：作为文档库前缀，可保证唯一性
		t.setName(doc.getString("te_name"));
		
		//创建文档库，并刷新文档库缓存
		createLibs(t, datasource);
		refreshCache(FolderViewCache.class);
		
		//创建分类关联表对应，并刷新缓存
		createRelLibs(datasource, t.getCode());
		refreshCache(RelTableCache.class);

		//创建机构、用户、角色，并刷新缓存
		createOrgUser(t);
		refreshCache(OrgRoleUserCache.class);
		
		//回写
		doc.set("te_deployed", 1);
		doc.set("te_orgID", t.getOrgID());
		docManager.save(doc);
		
		return true;
	}
	
	/**
	 * 部署归档库
	 * @param tLibID
	 * @param tID
	 * @return
	 * @throws E5Exception
	 */
	public boolean deployArchive(int tLibID, long tID, int datasource) throws E5Exception {
		init();
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = docManager.get(tLibID, tID);
		
		//判断是否已经部署
		if (doc != null && doc.getInt("te_archiveDeployed") == 1) {
			return false;
		}
		
		String code = (doc == null) ? null
				: doc.getString("te_code").toLowerCase();//确保租户的代号是小写的：作为文档库前缀，可保证唯一性
		createArchiveLibs(code, datasource);
		
		//回写
		if (doc != null) {
			doc.set("te_archiveDeployed", 1);
			docManager.save(doc);
		}
		
		return true;
	}
	
	//创建租户的机构、用户、角色
	private void createOrgUser(Tenant t) throws E5Exception {
		//创建同名、同代号的机构
		Org org = createOrg(t);
		
		t.setOrgID(org.getOrgID());

		//在机构下创建租户管理员的角色和账号
		createUserAdmin(t);
	}
	//创建租户的机构
	private Org createOrg(Tenant t) throws E5Exception {
		//创建同名、同代号的机构
		Org org = new Org();
		org.setCode(t.getCode());
		org.setName(t.getName());
		org.setParentID(0);
		org.setProperty1(t.getCode());
		
		OrgManager orgManager = (OrgManager)Context.getBean(OrgManager.class);
		orgManager.create(org);

		return org;
	}
	//创建租户管理员
	private void createUserAdmin(Tenant t) throws E5Exception {
		String tenantCode = t.getCode();
		
		//创建租户管理员
		String userName = "管理员";
		String userCode = tenantCode + "admin";
		
		int docTypeID = DocTypes.USEREXT.typeID();
		int docLibID = LibHelper.getLibID(docTypeID, tenantCode);
		long docID = InfoHelper.getNextDocID(docTypeID);
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document userExt = docManager.newDocument(docLibID, docID);
		userExt.setFolderID(DomHelper.getFVIDByDocLibID(docLibID));
		
		userExt.set("u_name", userName);
		userExt.set("u_code", userCode);
		userExt.set("u_isAdmin", 1);
		docManager.save(userExt);
		
		//创建E5账号
		User user = new User();
		user.setUserID((int)docID);
		user.setOrgID(0); //系统不允许用户出现在机构树上，因此把用户的orgID设为0
		user.setUserName(userName);
		user.setUserCode(userCode);
		user.setUserPassword(userCode);
		user.setProperty1(tenantCode); //扩展字段1：租户代号
		user.setProperty2("1"); //扩展字段2：租户管理员
		
		UserManager userManager = (UserManager)Context.getBean(UserManager.class);
		userManager.create(user);
		
		RoleManager roleManager = (RoleManager)Context.getBean(RoleManager.class);
//		int roleID = roleManager.create(t.getOrgID(), userName);
		int roleID = 1; //使用系统初始化时加载的角色1“管理员”，也是默认站点的第一个角色
		
		//把角色和账号挂钩
		UserRole userRole = new UserRole();
		userRole.setRoleID(roleID);
		userRole.setRoleName(userName);
		userRole.setUserCode(userCode);
		userRole.setUserID(user.getUserID());
		userRole.setTimeType(0);
		userRole.setTimeValue(0);
		userRole.setStartDate("2015-1-1");
		userRole.setStartTime("0:0");
		userRole.setEndDate("2045-1-1");
		userRole.setEndTime("23:59");
		
		roleManager.grantRole(user.getUserID(), userRole);
	}
	
	/**
	 * 创建租户的一套文档库
	 * 做法：对一个文档类型，取出系统默认创建的文档库，把表名前缀从xy改成租户代号。
	 * 要求：系统内必须已经存在了默认文档库，不然不能正确创建
	 * @param t
	 * @throws E5Exception
	 */
	private void createLibs(Tenant t, int datasource) throws E5Exception {
		DocLibManager libManager = (DocLibManager)Context.getBean(DocLibManager.class);
		DocTypeReader docTypeReader = (DocTypeReader)Context.getBean(DocTypeReader.class);
		DocType[] allTypes = docTypeReader.getTypes(1);
				
		for (DocType docType : allTypes) {
			int docTypeID = docType.getDocTypeID();
			
			//若是全局文档类型，则不创建
			if (ArrayUtils.contains(globalTypes, docTypeID))
				continue;
			
			//从文档类型下取默认租户的已有文档库，以作参考
			List<DocLib> libs = LibHelper.getLibs(docTypeID, Tenant.DEFAULTCODE);
			
			for (DocLib docLib : libs) {
				DocLib lib = copyLib(t, docLib);
				lib.setDsID(datasource);
				String ddl = libManager.generateDDL(lib);
				
				libManager.create(ddl, lib, !ArrayUtils.contains(seperateLogTypes, docTypeID));
			}
		}
	}
	
	private DocLib copyLib(Tenant t, DocLib refLib) {
		DocLib lib = new DocLib();
		lib.setDocLibName(t.getName() + "-" + refLib.getDocLibName());
		lib.setDocLibTable(refLib.getDocLibTable().replace(Tenant.DEFAULTCODE, t.getCode()));
		lib.setDocTypeID(refLib.getDocTypeID());
		lib.setKeepDay(refLib.getKeepDay());
		
		return lib;
	}
	
	//创建分类关联表对应
	private void createRelLibs(int datasource, String tenantCode) throws E5Exception {
		//创建分类关联表
		RelTable[] relTables = createRelTable(datasource, tenantCode);
		
		//创建对应
		RelTableDocLibManager relTableDocLibManager = 
				(RelTableDocLibManager)Context.getBean(RelTableDocLibManager.class);
		RelTableDocLibFieldsManager relTableDocLibFieldsManager = 
				(RelTableDocLibFieldsManager)Context.getBean(RelTableDocLibFieldsManager.class);
		
		int docTypeID = DocTypes.ARTICLE.typeID();
		int catTypeID = CatTypes.CAT_COLUMNARTICLE.typeID();
		
		//取出默认租户的稿件库ID
		List<DocLib> docLibs = LibHelper.getLibs(docTypeID, Tenant.DEFAULTCODE);
		int docLibID = docLibs.get(0).getDocLibID();
		
		//取出默认稿件库的对应字段，以作参考
		RelTableDocLibFields[] tmp = relTableDocLibFieldsManager.
				getRelTableDocLibFields(docLibID, catTypeID);
		List<RelTableDocLibFields> fieldsList = Arrays.asList(tmp);
	
		/**
		 * 按渠道做稿件库的对应。
		 * 1）取出默认稿件库的关联对应的属性，以作参考
		 * 2）取出租户的稿件库（多渠道有多个稿件库）
		 * 3）按渠道进行每个稿件库的对应
		 */
		RelTableDocLibVO vo = relTableDocLibManager.getRelTableDocLib(docLibID, catTypeID);
		docLibs = LibHelper.getLibs(docTypeID, tenantCode);
		Channel[] allChs = ConfigReader.getChannels();
		for (int i = 0; i < allChs.length; i++) {
        	if (allChs[i] == null) continue;
        	
			DocLib lib = docLibs.get(i);
			vo.setDocLib(lib);
			vo.setDocLibId(lib.getDocLibID());
			vo.setRelTableId(relTables[i].getId());
			vo.setRelTable(relTables[i]);
			
		    relTableDocLibManager.saveRelTableDocLib(vo, fieldsList);
		}
	}

	//创建栏目稿件关联表
	private RelTable[] createRelTable(int dsID, String tenantCode) throws E5Exception {
		RelTableManager relTableManager = (RelTableManager)Context.getBean(RelTableManager.class);
		DocTypeReader docTypeReader = (DocTypeReader)Context.getBean(DocTypeReader.class);

		int docTypeID = DocTypes.ARTICLE.typeID();
		DocTypeField[] docTypeFields = docTypeReader.getFields(docTypeID);
		
		// 取出稿件的已有的栏目稿件关联表作为参考，第一个就是Web稿件库的关联表
		RelTable[] relTables = relTableManager.getRelTables(docTypeID);
		RelTable webRelTable = relTables[0];

		//取出已有关联表的字段，从中找出在稿件类型中定义的字段，以及扩展的字段
		List<RelTableField> allFields = relTableManager.getTableFields(webRelTable.getId());
		List<RelTableField> otherFieldList = new ArrayList<RelTableField>();
		int[] typeFields = null;
		RelTableField[] otherFields = null;
		
		List<Integer> tmp = new ArrayList<Integer>();
		for (RelTableField field : allFields) {
			int fieldID = findFieldID(docTypeFields, field.getFieldName());
			if (fieldID > 0)
				tmp.add(fieldID);
			else {
				//分类关联表扩展出来的字段。没有设置字段长度，需补上
				if (field.getDataType().equals(DataType.VARCHAR)) {
					field.setDataLength(255);
				} else if (field.getDataType().equals(DataType.INTEGER)) {
					field.setDataLength(10);
				}
				otherFieldList.add(field);
			}
		}
		typeFields = InfoHelper.getIntArray(tmp);
		otherFields = otherFieldList.toArray(new RelTableField[0]);
		
		//按渠道创建栏目稿件关联表
		Channel[] allChs = ConfigReader.getChannels();
		RelTable[] result = new RelTable[allChs.length];
		for (int i = 0; i < allChs.length; i++) {
        	if (allChs[i] == null) continue;
        	
			RelTableVO vo = new RelTableVO();
			vo.setDsID(dsID);
			vo.setRefDocTypeID(docTypeID);
			vo.setFiledIds(typeFields);
			vo.setTableName("DOM_REL_" + allChs[i].getCode() + "_" + tenantCode);
			
			String ddl = relTableManager.genCreateDDL(vo);

			RelTable table = new RelTable();
			table.setDsID(dsID);
			table.setRefDocTypeID(docTypeID);
			table.setTableName(vo.getTableName());
			table.setName(tenantCode + "-稿件栏目关联表" + allChs[i].getCode());
			try {
				table.setId(EUIDFacade.getID(EUIDFacade.IDTYPE_REL_TABLE));

				relTableManager.createRelTable(ddl, table);
				appendRelTable(table, otherFields);
				
				result[i] = table;
			} catch (Exception e) {
				throw new E5Exception(e);
			}
		}
		return result;
	}

	private int findFieldID(DocTypeField[] fields, String name) {
		for (int i = 0; i < fields.length; i++) {
			if (name.equalsIgnoreCase(fields[i].getColumnCode()))
				return fields[i].getFieldID();
		}
		return 0;
	}

	private void appendRelTable(RelTable table, RelTableField[] fields)
			throws Exception {
		if (fields == null || fields.length == 0) return;
		
		String tableName = table.getTableName();

		DBSession dbsession = null;
		try {
			dbsession = Context.getDBSession(table.getDsID());

			for (int i = 0; i < fields.length; i++) {
				RelTableField field = fields[i];

				ColumnInfo c = new ColumnInfo();
				c.setName(field.getFieldName());
				c.setDataLength(field.getDataLength());
				c.setDataPrecision(field.getScale());
				c.setE5TypeName(field.getDataType());
				c.setDefaultValue(field.getDefaultValue());
				c.setNullable((field.getIsNull() == 1));

				String ddl = dbsession.getDialect().getColumnDDL(c);

				StringBuffer alterSQLBuf = new StringBuffer();
				alterSQLBuf.append("alter table ").append(tableName).append(" add ").append(ddl);

				dbsession.executeUpdate(alterSQLBuf.toString(), null);
			}
		} finally {
			ResourceMgr.closeQuietly(dbsession);
		}
	}

	//创建归档相关文档库
	private void createArchiveLibs(String code, int datasource) throws E5Exception {
		DocLibManager libManager = (DocLibManager)Context.getBean(DocLibManager.class);
		
		for (int docTypeID : archiveTypes) {
			//从文档类型下取已有的文档库（以租户代号为前缀）
			List<DocLib> libs = LibHelper.getLibs(docTypeID, code);
			
			for (DocLib docLib : libs) {
				DocLib lib = copyLib4Archive(docLib);
				lib.setDsID(datasource);
				
				String ddl = libManager.generateDDL(lib);
				
				libManager.create(ddl, lib, 
						!ArrayUtils.contains(seperateLogTypes, docTypeID));
			}
		}
	}

	//复制文档库作为归档文档库
	private DocLib copyLib4Archive(DocLib refLib) {
		DocLib lib = new DocLib();
		lib.setDocLibName(refLib.getDocLibName() + "(归档)");
		lib.setDocLibTable("archive_" + refLib.getDocLibTable());
		lib.setDocTypeID(refLib.getDocTypeID());
		lib.setKeepDay(refLib.getKeepDay());
		
		return lib;
	}
	
	/**
	 * 根据代号读出租户记录
	 */
	public Tenant get(String tenantCode) {
		if (StringUtils.isBlank(tenantCode))
			tenantCode = Tenant.DEFAULTCODE;
		
		DocLib docLib = LibHelper.getLib(DocTypes.TENANT.typeID());
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = null;
		try {
			Document[] docs = docManager.find(docLib.getDocLibID(), "te_code=?", new Object[]{tenantCode});
			if (docs.length == 0) {
				//按租户代号查不到，则可能是默认系统（单租户），添加记录。
				doc = initDefaultTenant(tenantCode, docLib);
			} else {
				doc = docs[0];
			}
			return copy2Tenant(doc);
		} catch (E5Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 取出所有租户。
	 */
	public List<Tenant> getAll() throws E5Exception {
		List<Tenant> ts = new ArrayList<Tenant>();
		
		DocLib docLib = LibHelper.getLib(DocTypes.TENANT.typeID());
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] docs = docManager.find(docLib.getDocLibID(), "SYS_DELETEFLAG=0", null);
		for (Document doc : docs) {
			ts.add(copy2Tenant(doc));
		}
		return ts;
	}
	
	/**
	 * 多租户模式下取基础数据的键值对
	 * @param tenantCode 租户代号
	 * @param docTypeID 文档类型
	 * @param field 指定获取列
	 * @return Pair列表，Pair=<docID, 列值>
	 * @throws E5Exception
	 */
	public List<Pair> getData(String tenantCode, int docTypeID, String field) throws E5Exception {
		int docLibID = LibHelper.getLibID(docTypeID, tenantCode);
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] docs = docManager.find(docLibID, "SYS_DELETEFLAG=0", null);
		
		List<Pair> ps = new ArrayList<Pair>();
		for (Document doc : docs) {
			Pair data =  new Pair(String.valueOf(doc.getDocID()), doc.get(field));
			ps.add(data);
		}
		return ps;
	}
	
	private Tenant copy2Tenant(Document doc) {
		Tenant t = new Tenant();
		t.setId(doc.getDocID());
		t.setCode(doc.getString("te_code"));
		t.setName(doc.getString("te_name"));
		t.setOrgID(doc.getInt("te_orgID"));
		
		return t;
	}
	
	private void init() {
		if (globalTypes == null) {
			globalTypes = new int[]{
					DocTypes.TENANT.typeID(),
			};
			seperateLogTypes = new int[]{
					DocTypes.ARTICLE.typeID(),
					DocTypes.ORIGINAL.typeID(),
					DocTypes.PHOTO.typeID(),
					DocTypes.VIDEO.typeID(),
					DocTypes.SPECIAL.typeID(),
					DocTypes.BLOCKARTICLE.typeID(),
					DocTypes.COLUMN.typeID(),
					DocTypes.USEREXT.typeID(),
					DocTypes.TEMPLATE.typeID(),
					DocTypes.EXTFIELD.typeID(),
					DocTypes.BLOCK.typeID(),
					DocTypes.VOTE.typeID(),
					DocTypes.LIVE.typeID(),
					DocTypes.TOPIC.typeID(),
					DocTypes.LEADER.typeID(),
					DocTypes.WXGROUP.typeID(),
					DocTypes.WXMENU.typeID(),
					DocTypes.WXARTICLE.typeID(),
					DocTypes.WBARTICLE.typeID(),
			};
			archiveTypes = new int[]{
					DocTypes.ORIGINAL.typeID(),
					DocTypes.ARTICLE.typeID(),
					DocTypes.ARTICLEEXT.typeID(),	//稿件扩展字段
					DocTypes.ATTACHMENT.typeID(),	//附件
					DocTypes.WIDGET.typeID(),		//挂件
					DocTypes.ARTICLEREL.typeID()	//相关稿件
			};
		}
	}

	//初始化默认租户：加根机构、加租户记录
	private Document initDefaultTenant(String tenantCode, DocLib docLib) throws E5Exception {
		String defaultName = "默认";
		
		//创建租户记录
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = docManager.newDocument(docLib.getDocLibID(), InfoHelper.getNextDocID(docLib.getDocTypeID()));
		doc.setFolderID(docLib.getFolderID());
		doc.setDeleteFlag(0);
		doc.set("te_name", defaultName);
		doc.set("te_code", tenantCode);
		doc.set("te_deployed", 1);
		doc.set("te_orgID", 1); //以系统初始化时的第一个机构“默认根机构”为默认租户的根节点
		docManager.save(doc);
		
		return doc;
	}
	
	private void refreshCache(Class<?> clazz) {
		try {
			CacheManager.refresh(clazz);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
