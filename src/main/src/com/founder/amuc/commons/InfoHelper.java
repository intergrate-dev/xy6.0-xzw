package com.founder.amuc.commons;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;

import com.founder.amuc.tenant.TenantManager;
import com.founder.amuc.commons.DocTypes;
import com.founder.e5.commons.DomHelper;
import com.founder.e5.commons.Pair;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.context.EUID;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.DataType;
import com.founder.e5.db.IResultSet;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.e5.dom.DocLibReader;
import com.founder.e5.dom.DocType;
import com.founder.e5.dom.DocTypeField;
import com.founder.e5.dom.DocTypeReader;
import com.founder.e5.dom.FolderReader;
import com.founder.e5.listpage.cache.ListMode;
import com.founder.e5.listpage.cache.ListModeReader;
import com.founder.e5.personality.PersonalSetting;
import com.founder.e5.personality.PersonalSettingManager;
import com.founder.e5.sys.SysConfigReader;
import com.founder.e5.workspace.app.form.FormParam;
import com.founder.e5.workspace.app.form.FormViewer;

import com.founder.xy.commons.LibHelper;
import com.founder.xy.system.Tenant;

/**
 * 系统通用类 定义通用的方法、常量等
 * 
 * @author Gong Lijie
 */
public class InfoHelper {
	
	private static DocTypeReader docTypeReader = (DocTypeReader) Context.getBean(DocTypeReader.class);
	private static DocLibReader docLibReader = (DocLibReader) Context.getBean(DocLibReader.class);
	
	/**
	 * 取一个数据来源在分类中的ID
	 * @param source 数据来源名称
	 * @return
	 */
	public static int getMemberSourceCat(String source) {//会员数据来源根分类名称
		int catTypeID = DomHelper.getCatTypeID(Constant.SOURCE_MEMBER);
		return DomHelper.getCatID(catTypeID, source);
	}
	public static int getEventSourceCat(String source) {//行为数据来源根分类名称
		int catTypeID = DomHelper.getCatTypeID(Constant.SOURCE_EVENT);
		return DomHelper.getCatID(catTypeID, source);
	}
	public static int getEventTypeCat(String source) {//行为数据来源根分类名称
		int catTypeID = DomHelper.getCatTypeID(Constant.EVENT_TYPE);
		return DomHelper.getCatID(catTypeID, source);
	}
	/**
	 * 根据文档类型编码得到ID
	 * @param typeCode 在Constant中有常量定义
	 * @return
	 */
	public static int getTypeIDByCode(String typeCode) {
		if (typeCode == null)
			return 0;
		
		DocType docType = DomHelper.getDocTypeByCode(typeCode);
		if (docType == null)
			return 0;
		else
			return docType.getDocTypeID();
	}
	/**
	 * 根据文档类型获得对应的文档库，适用于全局文档库
	 * @param tenantCode
	 * @return
	 * @throws E5Exception
	 */
	public static DocLib getLib(String typeCode) throws E5Exception{
		DocLib[] docLibs = getLibsByCode(typeCode);
		if (docLibs != null) {
			return docLibs[0];
		}
		return null;
	}
	/**
	 * 根据租户代号和文档类型获得对应的文档库
	 * @param typeCode
	 * @param tenantCode
	 * @return
	 * @throws E5Exception
	 */
	public static DocLib getLib(String typeCode, String tenantCode) throws E5Exception{
		//根据文档类型Code得到ID
		int docTypeID = getTypeIDByCode(typeCode);
		
		//现在tenantCode无用了，系统内只使用默认租户。
		return LibHelper.getLib(docTypeID, Tenant.DEFAULTCODE);
	}
	
	/**
	 * 根据租户代号和文档类型获得对应的文档库ID
	 * @param typeCode
	 * @param tenantCode
	 * @return
	 * @throws E5Exception
	 */
	public static int getLibID(String typeCode, String tenantCode) throws E5Exception{
		DocLib docLib = getLib(typeCode, tenantCode);
		return docLib.getDocLibID();
	}
	
	/**
	 * 根据租户代号和文档类型获得对应的文档库表名
	 * @param typeCode
	 * @param tenantCode
	 * @return
	 * @throws E5Exception
	 */
	public static String getLibTable(String typeCode, String tenantCode) throws E5Exception{
		DocLib docLib = getLib(typeCode, tenantCode);
		return docLib.getDocLibTable();
	}
	
	/**
	 * 按文档类型Code取得所有的文档库
	 */
	public static DocLib[] getLibsByCode(String typeCode) throws E5Exception {
		DocLibReader docLibReader = (DocLibReader)Context.getBean(DocLibReader.class);
		return docLibReader.getByTypeID(getTypeIDByCode(typeCode));
	}
	/**
	 * 取会员原始表
	 */
	public static int getOriMemberLibID() throws E5Exception {
		DocLib[] docLibs = getLibsByCode(Constant.DOCTYPE_MEMBER);
		if (docLibs == null || docLibs.length == 0)
			return 0;
		
		for (DocLib docLib : docLibs) {
			if (docLib.getDocLibTable().equals("gMemberOri")) {
				return docLib.getDocLibID();
			}
		}
		return 0;
	}
	
	public static int getOriMemberLibID(String source) throws E5Exception {
		int sourceID = getMemberSourceCat(source);
		return getOriMemberLibID(sourceID);
	}
	/** 
	* @author  leijj 
	* 功能： 根据会员数据来源获取会员原始表DocLib
	* @param source
	* @return
	* @throws E5Exception 
	*/ 
	public static int getOriMemberLibID(int sourceID) throws E5Exception {
		DocLib[] docLibs = getLibsByCode(Constant.DOCTYPE_MEMBER);
		if (docLibs == null || docLibs.length == 0)
			return 0;
		
		DocLib[] mcDocLibs = getLibsByCode(Constant.DOCTYPE_MEMBERCAT);
		if (mcDocLibs == null || mcDocLibs.length == 0)
			return 0;
		
		String mcTable = "";
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] mcDocs = docManager.find(mcDocLibs[0].getDocLibID(), "mcSource_ID=?", new Object[]{sourceID});
		if(mcDocs != null && mcDocs.length > 0){
			mcTable = mcDocs[0].getString("mcTable");
		}
		if(StringUtils.isBlank(mcTable)){
			return 0;
		}
		for (DocLib docLib : docLibs) {
			if (mcTable.equals(docLib.getDocLibTable())) {
				return docLib.getDocLibID();
			}
		}
		return 0;
	}
	/**
	 * 本系统内的SYS_DOCUMENTID的计数
	 * @param docTypeID 按文档类型ID各自计数
	 * @return
	 * @throws E5Exception
	 */
	public static long getID(int docTypeID) throws E5Exception {
		return EUID.getID("DocID" + docTypeID);
	}
	public static long getID(DBSession conn, int docTypeID) throws E5Exception {
		return EUID.getID(conn, "DocID" + docTypeID);
	}
	
	/**
	 * 从session中取得租户代号
	 * @param request
	 * @return
	 */
	public static String getTenantCode(HttpServletRequest request) {
		
		Tenant tenant = (Tenant)request.getSession().getAttribute("tenant");
		String tCode = tenant != null ? tenant.getCode() : "";
		
		if (StringUtils.isBlank(tCode))
			tCode = TenantManager.DEFAULTCODE;
		
		return tCode;
	}

	/**
	 * 通用的executeUpdate方法
	 * @throws Exception 
	 */
	public static int executeUpdate(String sql, Object[] params) throws E5Exception {
		DBSession conn = null;
		try {
	    	conn = Context.getDBSession();
	    	return conn.executeUpdate(sql, params);
		} catch (SQLException e) {
			if (params != null)
				for (int i = 0; i < params.length; i++) {
					System.out.println("params[" + i + "]:" + params[i]);
				}
			throw new E5Exception(e);
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
	}
	public static int executeUpdate(String sql, Object[] params, DBSession conn) throws E5Exception {
		try {
	    	return conn.executeUpdate(sql, params);
		} catch (SQLException e) {
			if (params != null)
				for (int i = 0; i < params.length; i++) {
					System.out.println("params[" + i + "]:" + params[i]);
				}
			throw new E5Exception(e);
		}
	}
	public static String getFields(int docTypeID) throws E5Exception {
		//得到所有的非平台字段
		DocTypeField[] fields = docTypeReader.getFieldsExt(docTypeID);
		
		StringBuilder result = new StringBuilder();
		for (DocTypeField field : fields) {
			if(!canShow(field)){
				continue;
			}
			String columnCode = field.getColumnCode();
			String columnName = field.getColumnName();
			String fieldType = field.getDataType();
			String enumOption = field.getOptions();
			String selUrl = field.getUrl();
			int fieldID = field.getFieldID();
			result.append(",{\"fieldID\":\""+Integer.toString(fieldID)+"\",\"columnCode\":\""+columnCode+"\",\"columnName\":\""+columnName)
				.append("\",\"fieldType\":\""+fieldType+"\",\"enumOption\":\""+enumOption+"\",\"selUrl\":\""+selUrl+"\"}");
		}
		return "["+result.toString().substring(1)+"]";
	}
	public static boolean canShow(DocTypeField field) {
		String code = field.getColumnCode();
		if (code.equals("mName")//会员名称
				|| code.equals("mHobby")//兴趣爱好
				|| code.equals("mRegion")//所在地区
				|| code.equals("mScore")//积分
				|| code.equals("mSex") //性别
				|| code.equals("mMarriage") //婚姻状况
				|| code.equals("mEducation") //学历 
				|| code.equals("mJob") //职业 
				|| code.equals("mCardType")//证件类型 
				|| code.equals("mCardNo") //证件号码
				|| code.equals("mBirthday") //生日
				|| code.equals("mWeiboUid") //新浪微博号id
				|| code.equals("mWeiboName") //新浪微博名称
				|| code.equals("mWechatId") //微信号id
				|| code.equals("mWechatName")//微信号名称
				|| code.equals("mQq") //QQ号
				|| code.equals("mQQname") //QQ昵称
				|| code.equals("mStatus") //账号状态
				|| code.equals("mAddress")//会员地址
				|| code.equals("mMobile") //移动电话
				|| code.equals("mEmail") //电子邮箱
				)
			return true;
		else
			return false;
	}
	/**
	 * 取个人配置
	 */
	public static PersonalSetting getMySetting(int userID,String item){
		return getMySetting(userID,0,item);
	}
	
	/**
	 * 取个人配置
	 */
	public static PersonalSetting getMySetting(int userID,int roleID,String item){
		PersonalSetting ps = null;
		String sql = "select USERID,ROLEID,CONFIGITEM,CONFIGVALUE,EXT1,EXT2,EXT3,EXT4,EXT5 from FSYS_PERSONSETTING where USERID=? and CONFIGITEM=?";
		Object[] params = new Object[]{userID,item};
		if(roleID>0){
			sql +=" and ROLEID=? ";
			params = new Object[]{userID,item,roleID};
		}
		
		DBSession conn = null;
    	IResultSet rows = null;
		try {
	    	conn = Context.getDBSession();
	    	rows = conn.executeQuery(sql,params);
	    	if(rows.next()){
	    		ps = new PersonalSetting();
	    		ps.setUserID(userID);
	    		ps.setRoleID(roleID);
	    		ps.setItem(item);
	    		ps.setValue(rows.getString("CONFIGVALUE"));
	    		ps.setExt1(rows.getString("EXT1"));
	    		ps.setExt2(rows.getString("EXT2"));
	    		ps.setExt3(rows.getString("EXT3"));
	    		ps.setExt4(rows.getString("EXT4"));
	    		ps.setExt5(rows.getString("EXT5"));
	    	}
	    }catch(Exception e) {
	    } finally {
	    	ResourceMgr.closeQuietly(rows);
			ResourceMgr.closeQuietly(conn);
		}
		return ps;
	}
	
	public static void saveMySetting(int userID, String item, String value) throws E5Exception{
		PersonalSetting ps = new PersonalSetting();
		ps.setUserID(userID);
		ps.setRoleID(0);
		ps.setItem(item);
		ps.setValue(value);
		
		PersonalSettingManager psManager = (PersonalSettingManager)Context.getBean(PersonalSettingManager.class);
		psManager.save(ps);
	}
	
	public static List<Pair> getFormJsp(int docLibID, String docIDs, String formCode, String uuid) throws E5Exception {
		DocLib memberLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER);
		int memberLibID = memberLib.getDocLibID();
		long[] docIds = getDupMemberIDS(docLibID, Long.parseLong(docIDs));
		String headStr = "";
		List<Pair> list = new ArrayList<Pair>();
		for (long docId : docIds) {
			String[] result = getFormJsp(memberLibID,docId,formCode,uuid);
			headStr = result[0];
			list.add(new Pair(String.valueOf(docId), result[1]));
		}
		
		list.add(0, new Pair("head", headStr));
		return list;
	}
	
	public static String[] getFormJsp(int docLibID, long docID, String formCode, String uuid) throws E5Exception {
		FormParam param = new FormParam();
		param.setDocLibID(docLibID);
		param.setFormCode(formCode);
		param.setDocID(docID);
		param.setFvID(docLibReader.get(docLibID).getFolderID());
		param.setUuid(uuid);
		
		String jsp = FormViewer.getFormJsp(param);
		
		int begin = jsp.indexOf("<body>");
		int end = jsp.lastIndexOf("</body>");
		
		String[] result = new String[2];
		result[1] = jsp.substring(begin + 6, end);
		
		begin = jsp.indexOf("<head>");
		end = jsp.indexOf("</head>");
		
		result[0] = jsp.substring(begin + 6, end);
		
		return result;
	}
	
	public static String getFormContent(String jsp) {
		int begin = jsp.indexOf("<input");
		int end = jsp.lastIndexOf("</form>");
		return jsp.substring(begin,end);
	}
	
	public static long[] getDupMemberIDS(int docLibID, long docID) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document dupData = docManager.get(docLibID, docID);
		long[] memberIDs = StringUtils.getLongArray(dupData.getString("ddMemberIDs"));
		return memberIDs;
	}
	/**
	 * 得到文档库类型在数据库中的表名称
	 * @param docLibID
	 * 				文档库id
	 * @return
	 * 				物理表名称,格式：DOM_x_DOCLIB
	 * @throws E5Exception
	 */
	public static String getDocTableName(int docLibID){
		
		DocLibReader reader = (DocLibReader)Context.getBean(DocLibReader.class);
	    try {
			return reader.get(docLibID).getDocLibTable();
		} catch (E5Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 多租户模式下，按租户替换sql语句中的表名
	 */
	public static String replaceSQL(String tenantCode, String sql, String typeCode, String table) throws E5Exception {
		if (!TenantManager.DEFAULTCODE.equals(tenantCode)) {
			DocLib docLib = InfoHelper.getLib(typeCode, tenantCode);
			sql = sql.replace(table, docLib.getDocLibTable());
		}
		return sql;
	}
	
	/**
	 * 取某文档类型的指定名称的列表方式，可用于操作中显示列表的情形。
	 * @param docTypeID
	 * @param listName
	 * @return
	 */
	public static int getListID(int docTypeID, String listName) {
		ListModeReader listReader = (ListModeReader)Context.getBean(ListModeReader.class);
		ListMode[] pages = listReader.getListModes(docTypeID, 0, 0);
		if (pages != null && pages.length > 0) {
			for (ListMode listMode : pages) {
				if (listMode.getListName().equals(listName))
					return listMode.getListId();
			}
			return pages[0].getListId();
		} else {
			return 0;
		}
	}
	
	/** 
	* @author  leijj 
	* 功能：获取会员数据来源。 由于分为会员数据来源和行为数据来源，则保存行为数据来源时需要取会员对应的数据来源
	* @param eSource
	* @return
	* @throws E5Exception 
	*/ 
	public static String getMSource(String eSource) throws E5Exception{
		int appID = 1;
		String project = "数据采集";
		String item = "行为与会员数据来源对应关系";
		SysConfigReader configReader = (SysConfigReader)Context.getBean(SysConfigReader.class);
		String value = configReader.get(appID, project, item);
		if(value != null && value.length() > 0){
			String[] sourceArr = value.split(",");
			if(sourceArr != null && sourceArr.length > 0){
				for(String source : sourceArr){
					String[] emSource = source.split("=");
					if(emSource != null && emSource.length == 2){
						if(emSource[0].equals(eSource))
							return emSource[1];
					}
				}
			}			
		}
		return "";
	}
	
	/** 
	* @author  leijj 
	* 功能：获取会员默认分类信息
	* @param eSource
	* @return
	* @throws E5Exception 
	*/ 
	public static String getMType() throws E5Exception{
		SysConfigReader configReader = (SysConfigReader) Context.getBean(SysConfigReader.class);
		String mTypeValue = configReader.get(1, "数据采集", "会员分类默认值");
		return mTypeValue;
	}
	/** 
	* @author  leijj 
	* 功能： 
	* @param eSource
	* @return
	* @throws E5Exception 
	*/ 
	public static int importRule() throws E5Exception{
		SysConfigReader configReader = (SysConfigReader) Context.getBean(SysConfigReader.class);
		String affixRuleValue = configReader.get(1, "会员导入", "会员导入规则");
		if(!StringUtils.isBlank(affixRuleValue)) return Integer.parseInt(affixRuleValue);
		return 0;
	}
	
	/**
	 * @author  fanjc
	 * 功能：获取会员每日积分上限，该值可配置
	 * @return
	 * @throws E5Exception
	 */
	public static int getMScore() throws E5Exception{
		
		SysConfigReader configReader = (SysConfigReader) Context.getBean(SysConfigReader.class);
		String mScoreValue = configReader.get(1, "会员中心", "每日积分上限");
		return Integer.valueOf(mScoreValue);
	}
	
	/**
	 * @author  fanjc
	 * 功能：获取内推或外推活动的url
	 * @param item item=内推或外推两个值
	 * @return
	 * @throws E5Exception
	 */
	public static String getActionUrl(String item) throws E5Exception{
		
		SysConfigReader configReader = (SysConfigReader) Context.getBean(SysConfigReader.class);
		String ActionUrl = configReader.get(1, "推广活动", item);
		return ActionUrl;
	}
	
	/** 
	* @author  leijj 
	* 功能： 动态取id值
	* @param field
	* @param text
	* @param webRoot
	* @return 
	 * @throws E5Exception 
	*/ 
	public static String getIdValue(DocTypeField field, String text, String webRoot) throws E5Exception {
		String ret = accessUrl(field.getUrl(), webRoot);
		Pair[] options = json2Options(ret);
		
		return _findOption(options, text);
	}
	protected static String _findOption(Pair[] options, String text) {
		if (options != null) {
			for (Pair option : options) {
				if (option.getValue().equals(text) || option.getKey().equals(text))
					return option.getKey();
			}
		}
		return null;
	}
	protected static Pair[] json2Options(String ret) {
		if (StringUtils.isBlank(ret)) return null;
		
		return (Pair[]) JSONArray.toArray(JSONArray.fromObject(ret), Pair.class);
	}
	
	
	
	/**  
	 * 程序中访问http数据接口  
	 */  
	protected static String accessUrl(String urlStr, String webRoot) {
		if (StringUtils.isBlank(urlStr)) return null;
		
		urlStr = urlStr.replaceAll("&amp;", "&");
		if (urlStr.indexOf("http://") < 0) {
			urlStr = webRoot + urlStr;
		}
		
		/** 网络的url地址 */
		URL url = null;
		/** 输入流 */
		BufferedReader in = null;
		StringBuffer sb = new StringBuffer();
		String str = null;
		try {
			url = new URL(urlStr);
			in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
			while ((str = in.readLine()) != null) {
				sb.append(str);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(in);
		}
		return sb.toString();
	}
	
	/**
	 * 获取列表导出数量
	 * @return
	 * @throws E5Exception
	 */
	public static int exportCount() throws E5Exception{
		SysConfigReader configReader = (SysConfigReader) Context.getBean(SysConfigReader.class);
		String exportCount = configReader.get(1, "会员中心", "列表导出数量");
		if(!StringUtils.isBlank(exportCount)) return Integer.parseInt(exportCount);
		return 0;
	}
	
	/**
	 * 根据文件夹ID取得库ID
	 */
	public static int getDocLibIDByFVID(int fvID) {
		int docLibID = 0;
		FolderReader folderReader = (FolderReader) Context.getBean(FolderReader.class);
		try {
			docLibID = folderReader.get(fvID).getDocLibID();
		} catch (E5Exception e) {
			e.printStackTrace();
		}
		return docLibID;
	}
	
	public static int getDocTypeIDByLibID(int docLibID) {
		try {
			DocLib docLib = docLibReader.get(docLibID);
			if (docLib != null)
				return docLib.getDocTypeID();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	/**
	 * 检查关键属性的修改情况。
	 * @param doc
	 * @return
	 * @throws E5Exception 
	 */
	public static String whatChanged(Document doc) throws E5Exception {
		//根据新doc，取得数据库中的老doc，准备做属性比较。
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document oldDoc = docManager.get(doc.getDocLibID(), doc.getDocID());
		
		int docTypeID = getDocTypeIDByLibID( doc.getDocLibID());
		
		//得到所有的非平台字段
		DocTypeField[] fields = docTypeReader.getFieldsExt(docTypeID);
		
		StringBuilder result = new StringBuilder(100);
		//每个非平台字段进行新旧值比较
		for (DocTypeField field : fields) {
			result.append(compare(oldDoc, doc, field));
		}
		return result.toString();
/*
		if (docTypeID == getDocTypeID(DOCTYPE_CUSTOMER)) {
		}
		else if (docTypeID == getDocTypeID(DOCTYPE_CONTACT)) {
		}
		else if (docTypeID == getDocTypeID(DOCTYPE_LISTING)) {
		}
		else if (docTypeID == getDocTypeID(DOCTYPE_PACT)) {
		}
*/
	}
	
	/**
	 * 新旧属性值比较
	 * @param oldDoc
	 * @param newDoc
	 * @param field
	 * @return
	 */
	private static String compare(Document oldDoc, Document newDoc, DocTypeField field)
	{
		String fieldType = field.getDataType();
		//String columnCode = field.getColumnCode();
		/*
		 * 目前都作为字符串读出来进行比较。
		 * 保留单独按确定类型进行比较的可能性,因此每个类型写单独判断语句。
		 * 
		 * 最常用的类型最先判断：整数、变长字符串
		 */
		try {
			if (fieldType.equals(DataType.VARCHAR)) {
				return diffString(oldDoc, newDoc, field);
			}
			
			else if (fieldType.equals(DataType.INTEGER)) {
				return diffString(oldDoc, newDoc, field);
			}
			//日期时间
			else if (fieldType.equals(DataType.TIMESTAMP) 
					|| fieldType.equals(DataType.DATE)
					|| fieldType.equals(DataType.TIME))
			{
				return diffString(oldDoc, newDoc, field);
			}
			//大文本
			else if (fieldType.equals(DataType.CLOB)) {
				String oldValue = oldDoc.getString(field.getColumnCode());
				String newValue = newDoc.getString(field.getColumnCode());
				if (oldValue == null) oldValue = "";
				if (newValue == null) newValue = "";

				StringBuilder result = new StringBuilder(100);
				if (!oldValue.equals(newValue))
					result.append("[ ").append(field.getColumnName()).append(" ] 修改(大文本)");
				return result.toString();
			}
			//二进制的不比较
			else if (fieldType.equals(DataType.BLOB)
					|| fieldType.equals(DataType.EXTFILE))
			{
				return "";
			}
			else {
				/*
				DataType.CHAR;
				DataType.DOUBLE;
				DataType.FLOAT;
				DataType.LONG;
				*/
				return diffString(oldDoc, newDoc, field);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	//拼串：字段不同
		private static String diffString(Document oldDoc, Document newDoc, DocTypeField field) {
			String oldValue = oldDoc.getString(field.getColumnCode());
			String newValue = newDoc.getString(field.getColumnCode());
			if (oldValue == null) oldValue = "";
			if (newValue == null) newValue = "";
			if("CUST_TYPE".equals(field.getColumnCode())){
				if("1".equals(oldValue))
					oldValue = "渠道";
				if("0".equals(oldValue))
					oldValue = "直客";			
				if("1".equals(newValue))
					newValue = "渠道";
				if("0".equals(newValue))
					newValue = "直客";
			}
			else if("CONTACT_GENDER".equals(field.getColumnCode())){
				if("1".equals(oldValue))
					oldValue = "女";
				if("0".equals(oldValue))
					oldValue = "男";			
				if("1".equals(newValue))
					newValue = "女";
				if("0".equals(newValue))
					newValue = "男";
			}
			else if("LIST_TYPE".equals(field.getColumnCode())){
				if("1".equals(oldValue))
					oldValue = "渠道报备";
				if("0".equals(oldValue))
					oldValue = "直客报备";			
				if("1".equals(newValue))
					newValue = "渠道报备";
				if("0".equals(newValue))
					newValue = "直客报备";
			}
			else if("PACT_READTYPE".equals(field.getColumnCode())){
				if("1".equals(oldValue))
					oldValue = "指定对象可读";
				if("0".equals(oldValue))
					oldValue = "全部可读";			
				if("1".equals(newValue))
					newValue = "指定对象可读";
				if("0".equals(newValue))
					newValue = "全部可读";
			}
			
			StringBuilder result = new StringBuilder(100);
			if (!oldValue.equals(newValue))
				result.append("[ ").append(field.getColumnName()).append(" ] ")
					.append(oldValue).append("---->").append(newValue).append("；");
			return result.toString();
		}
		
		/**
		 * 取某文档类型下的文档库。若该文档类型下有多个文档库，则返回第一个库的ID
		 * 
		 * @param docTypeID
		 * @return
		 */
		public static int getDocLibID(int docTypeID) {
			try {
				int[] libArr = docLibReader.getIDsByTypeID(docTypeID);
				if (libArr == null || libArr.length == 0)
					return 0;

				return libArr[0];
			} catch (E5Exception e) {
				e.printStackTrace();
				//log.error("Error when get lib by doctype:" + docTypeID, e);
			}
			return 0;
		}
		/**
		 * @return 消息文档库kid
		 */
		public static int getMessageDocLibID(){
			return getDocLibID(DocTypes.DOCTYPE_MESSAGE.typeID());
		}
		
		/**
		 * @return 菜单管理文档库kid
		 */
		public static int getMenuDocLibID(){
			return getDocLibID(DocTypes.DOCTYPE_MENU.typeID());
		}

		/**
		 * @return 微信活动kid
		 */
		public static int getGivingDocLibID(){
			return getDocLibID(DocTypes.DOCTYPE_GIVING.typeID());
		}
		
		/**
		 * 
		 * @return 奖品
		 */
		public static int getPrizeDocLibID(){
			return getDocLibID(DocTypes.DOCTYPE_GIVINGGOODS.typeID());
		}
		
		/**
		 * @return 微信活动日志文档库kid
		 */
		public static int getGivingLogDocLibID(){
			return getDocLibID(DocTypes.DOCTYPE_GIVINGLOG.typeID());
		}
		
		/**
		 * 
		 * @return 客户文档库id
		 */
		public static int getCustomerDocLibID(){
			return getDocLibID(DocTypes.DOCTYPE_CUSTOMER.typeID());
		}
		
		public static int getFVIDByDocLibID(int docLibID) throws E5Exception {
			DocLibReader libReader = (DocLibReader) Context.getBean(DocLibReader.class);
			return libReader.get(docLibID).getFolderID();
		}
}