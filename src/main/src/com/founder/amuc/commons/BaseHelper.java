package com.founder.amuc.commons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.founder.e5.app.App;
import com.founder.e5.app.AppManager;
import com.founder.e5.cat.CatReader;
import com.founder.e5.cat.CatType;
import com.founder.e5.cat.Category;
import com.founder.e5.commons.Log;
import com.founder.e5.commons.Pair;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBType;
import com.founder.e5.dom.DocLib;
import com.founder.e5.dom.DocLibReader;
import com.founder.e5.dom.DocType;
import com.founder.e5.dom.DocTypeReader;
import com.founder.e5.dom.FolderReader;
import com.founder.e5.dom.FolderView;
import com.founder.e5.dom.queryForm.QueryForm;
import com.founder.e5.dom.queryForm.QueryFormReader;
import com.founder.e5.flow.Flow;
import com.founder.e5.flow.FlowNode;
import com.founder.e5.flow.FlowReader;
import com.founder.e5.listpage.cache.ListMode;
import com.founder.e5.listpage.cache.ListModeReader;
import com.founder.e5.sys.SysConfigReader;
import com.founder.e5.workspace.app.form.FormParam;
import com.founder.e5.workspace.app.form.FormViewer;

/**
 * 与E5底层信息相关的辅助类。
 * 
 * 处理文档类型、文档库、文件夹、分类、流程、参数等几个功能
 * @author Gong Lijie
 */
public class BaseHelper {
	private static Log log = Context.getLog("oms");

	private static DocTypeReader docTypeReader = (DocTypeReader) Context.getBean(DocTypeReader.class);
	private static DocLibReader docLibReader = (DocLibReader) Context.getBean(DocLibReader.class);
	private static CatReader catReader = (CatReader) Context.getBean(CatReader.class);
	private static FlowReader flowReader = (FlowReader) Context.getBean(FlowReader.class);
	private static FolderReader folderReader = (FolderReader) Context.getBean("FolderReader");

	private static String dbType;	//数据库类型
	private static Map<String, Integer> apps = new HashMap<String, Integer>(4);
	
	// -----------------------------------------------
	// ----读取文档类型的方法
	// ---------------------------------------
	public static DocType getDocType(String docTypeName) {
		DocType docType = null;
		try {
			docType = docTypeReader.get(docTypeName);
		} catch (E5Exception e) {
		}
		return docType;
	}

	public static int getDocTypeID(String docTypeName) {
		DocType docType = getDocType(docTypeName);
		if (docType == null)
			return 0;
		return docType.getDocTypeID();
	}
	
	
	public static int getDocTypeIDByLibID(int docLibID) {
		try {
			DocLib docLib = docLibReader.get(docLibID);
			if (docLib != null)
				return docLib.getDocTypeID();
		} catch (Exception e) {
			log.error("[InfoHelper.getDocTypeIDByLibID]docLibID=" + docLibID, e);
		}
		return 0;
	}

	/**
	 * 取某文档类型下的文档库。若该文档类型下有多个文档库，则返回第一个库的ID
	 * 
	 * @param docTypeName
	 * @return
	 */
	public static int getDocLibID(String docTypeName) {
		return getDocLibID(getDocTypeID(docTypeName));
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
			log.error("Error when get lib by doctype:" + docTypeID, e);
		}
		return 0;
	}

	// ---------------------------------------
	// ----流程获取
	// ---------------------------------------
	/**
	 * 取某文档类型的第一个流程的初始节点
	 * 
	 * @param docTypeID
	 *            文档类型ID
	 * @return 流程初始节点对象
	 */
	public static FlowNode getFlowNode(String docTypeName) {
		int docTypeID = getDocTypeID(docTypeName);

		return getFlowNode(docTypeID);
	}

	/**
	 * 取某文档类型的第一个流程的初始节点
	 * 
	 * @param docTypeID
	 *            文档类型ID
	 * @return 流程初始节点对象
	 */
	public static FlowNode getFlowNode(int docLibID) {
		try {
			// 若没有设置文档库对应的流程，则取其所在文档类型的流程
			int docTypeID = docLibReader.get(docLibID).getDocTypeID();
			Flow[] flowArr = flowReader.getFlows(docTypeID);
			if (flowArr == null)
				return null;

			return flowReader.getFlowNode(flowArr[0].getFirstFlowNodeID());
		} catch (E5Exception e) {
			log.error("[InfoHelper.getFlowNode]docLibID:" + docLibID, e);
		}
		return null;
	}

	/**
	 * 取某文档类型的某流程的初始节点，该流程名称已知
	 * 
	 * @param docTypeID
	 *            文档类型ID
	 * @param flowName
	 *            流程名称
	 * @return 流程初始节点对象
	 */
	public static FlowNode getFlowNode(int docTypeID, String flowName) {
		try {
			Flow flow = flowReader.getFlow(docTypeID, flowName);
			if (flow == null)
				return null;

			return flowReader.getFlowNode(flow.getFirstFlowNodeID());
		} catch (E5Exception e) {
			log.error("[InfoHelper.getFlowNode]docTypeID:" + docTypeID, e);
		}
		return null;
	}

	// ---------------------------------------
	// ----定义分类相关的通用方法
	// ---------------------------------------
	/**
	 * 根据分类类型的名称，获取分类类型的ID
	 * 
	 * @param catName
	 * @return
	 */
	public static int getCatTypeID(String catTypeName) {
		CatType catType;
		try {
			catType = catReader.getType(catTypeName);
		} catch (E5Exception e) {
			catType = null;
		}
		if (catType == null)
			return 0;
		return catType.getCatType();
	}
	
	/**
	 * 根据分类类型Id，以及该分类名称 获取分类ID
	 * @param catTypeID
	 * @param catName
	 * @return
	 */
	public static int getCatID(int catTypeID, String catName)
	{
		if( catTypeID == 0 || StringUtils.isBlank( catName ))
		{
			return 0;
		}
		
		try 
		{
			Category[] cats = catReader.getCats(catTypeID);
			if( cats!=null && cats.length!=0 )
			{
				for( int i=0; i<cats.length; i++ )
				{
					if( cats[i].getCatName().endsWith( catName ) )
					{
						return cats[i].getCatID();
					}
				}
			}
		} 
		catch (E5Exception e) 
		{
			log.error( e );
		}
		return 0;
	}
	/**
	 * 根据分类的ID串，得到名字串，以在界面上显示分类名称
	 * 
	 * @param catIDs
	 *            ID串用分号分隔多个分类，每个分类ID中保存的是级联ID 如：31_100_323;31_103_2445;
	 * @return
	 */
	public static String getCatNames(int catType, String catIDs) {
		if (log.isDebugEnabled())
			log.debug("[GetCatNames]:" + catIDs);

		String[] catIDArray = StringUtils.split(catIDs, ";");
		if (catIDArray == null)
			return "";

		StringBuffer sbResult = new StringBuffer();
		int count = 0;
		Category cat = null;
		for (int i = 0; i < catIDArray.length; i++) {
			int[] idArray = StringUtils.getIntArray(catIDArray[i], "_");
			if (idArray == null)
				continue;

			try {
				cat = catReader.getCat(catType, idArray[idArray.length - 1]);
				if (cat == null)
					continue;
			} catch (E5Exception e) {
				log.error("Error in [getCatNames]", e);
				continue;
			}

			count++;
			if (count > 1)
				sbResult.append(",");

			sbResult.append(cat.getCatName());
		}
		return sbResult.toString();
	}

	/**
	 * 读取文件夹的路径，如：
	 * 待编文档库-总编室-预稿
	 * @param fvID
	 * @return
	 */
	public static String getFolderPath(int fvID) 
	{
		try 
		{
			FolderReader folderReader = (FolderReader) Context.getBean(FolderReader.class);
			FolderView folder = folderReader.get(fvID);
			if( folder == null )
			{
				return ("");
			}
			
			int[] parentIDs = folder.getParents();
			if( parentIDs==null || parentIDs.length==0 )
			{
				return folder.getFVName();
			}
			
			StringBuffer result = new StringBuffer(100);
			FolderView parent = null;
			for (int i = 0; i < parentIDs.length; i++) {
				if (i > 0) result.append("～");
				parent = folderReader.get(parentIDs[i]);
				if (parent != null) result.append(parent.getFVName());
				else result.append("NULL");
			}
			if (parentIDs.length > 0) result.append("～");
			result.append(folder.getFVName());
			return result.toString();
		} 
		catch (E5Exception e)
		{
			
		}
		return "";
	}
	/**
	 * 根据FVID取得文件夹名
	 * 
	 * @param fvID
	 * @return 文件夹名称
	 */
	public static String getFolderNameByID(int fvID) {
		FolderView folder = null;
		try {
			FolderReader folderReader = (FolderReader) Context.getBean(FolderReader.class);
			folder = folderReader.get(fvID);
		} catch (E5Exception e) {
		}
		if (folder != null)
			return folder.getFVName();

		return null;
	}
	
	public static int getRootFolderID(int docLibID) {
		try {
			DocLib docLib = docLibReader.get(docLibID);
			if (docLib != null)
				return docLib.getFolderID();
		} catch (Exception e) {
			log
					.error("[InfoHelper.getDocTypeIDByLibID]docLibID="
							+ docLibID, e);
		}
		return 0;

	}
	

	/**
	 * 得到文件夹级联名称，输出格式如下：实时新闻->晚报组
	 * 
	 * @param folderID
	 * @return
	 */
	public static String getFolderCasName(int folderID) {

		try {
			FolderView folder = folderReader.get(folderID);
			String folderName = folder.getFVName();
			int parentID = folder.getParentID();
			String folderCasName = folderName;
			while (parentID != 0) {
				FolderView tmpFolder = folderReader.get(parentID);
				String tmpFolderName = tmpFolder.getFVName();
				parentID = tmpFolder.getParentID();
				folderCasName = tmpFolderName + "->" + folderCasName;

			}
			return folderCasName;
		} catch (E5Exception ex) {
			log.error("Error when getFolderCasName by FVID:" + folderID, ex);
			return "";
		}
	}

	/**
	 * 根据 docLibID 取得文档库名
	 * 
	 * @param fvID
	 * @return 文件夹名称
	 */
	public static String getDocLibNameByID(int docLibID) {
		DocLib docLib = null;
		try {
			docLib = docLibReader.get(docLibID);
		} catch (E5Exception e) {
		}
		if (docLib != null)
			return docLib.getDocLibName();

		return null;
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
			log.error(e);
		}
		return docLibID;
	}

	public static int getFVIDByDocLibID(int docLibID) throws E5Exception {
		return docLibReader.get(docLibID).getFolderID();
	}
	

	/**
	 * 读取一个系统参数
	 * @param project
	 * @param item
	 * @return
	 */
	public static String getConfig(String project, String item) {
		return getConfig(0, project, item);
	}
	/**
	 * 读取一个系统参数
	 * @param appID
	 * @param project
	 * @param item
	 * @return
	 */
	public static String getConfig(int appID, String project, String item) {
		try {
			SysConfigReader sysReader = (SysConfigReader) Context.getBean(SysConfigReader.class);
			return sysReader.get(appID, project, item);
		} catch (Exception e) {
			log.error("读取系统参数失败:" + appID + "," + project + "," + item, e);
			return "";
		}
	}
	public static String getConfig(String appName, String project, String item) {
		int appID = getAppID(appName);
		SysConfigReader sysReader = (SysConfigReader) Context.getBean(SysConfigReader.class);
		try {
			return sysReader.get(appID, project, item);
		} catch (Exception e) {
			log.error("读取系统参数失败:" + appID + "," + project + "," + item, e);
			return "";
		}
	}
	
	/**
	 * 获取当前数据库类型。
	 * 是按中心数据源进行的数据库类型判断。
	 * 注意：若系统里包括多种数据库类型，这样的判断过于简单，可能不正确。
	 * @return String DBType中定义的各数据库类型字符串
	 */
	public static String getDBType() {
		if (dbType != null) return dbType;
		
		dbType = Context.getDBType();
		return dbType;
	}
	
	/**
	 * 取数据库中读当前时间的函数
	 * 可用于专题细览的过滤器
	 * @param dbType
	 * @return
	 */
	public static String getNowByDBType(String dbType) {
		if (dbType.equalsIgnoreCase(DBType.ORACLE)) {
			return "SYSDATE";
		} else if (dbType.equalsIgnoreCase(DBType.SQLSERVER)) {
			return "GETDATE()";
		} else if (dbType.equalsIgnoreCase(DBType.MYSQL)) {
			return "SYSDATE()";
		} else {
			throw new RuntimeException("NOT SUPPORT DBTYPE in getNowByDBType:" + dbType);
		}
	}
	//取应用子系统，只做一次
	public static int getAppID(String appName) {
		Integer app = apps.get(appName);
		if (app == null) {
			AppManager appManager = (AppManager)Context.getBean(AppManager.class);
			try {
				App[] appArr = appManager.get();
				for (App app2 : appArr) {
					apps.put(app2.getName(), app2.getAppID());
				}
			} catch (E5Exception e) {
				e.printStackTrace();
			}
			app = apps.get(appName);
			if (app == null) return 0;
		}
		return app.intValue();
	}
	
	//-------------------取特定的列表方式-------------------
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
	 * 取某文档类型的缺省列表方式（第一个）
	 * @param docTypeID
	 * @return
	 */
	public static int getListID(int docTypeID) {
		ListModeReader listReader = (ListModeReader)Context.getBean(ListModeReader.class);
		ListMode[] pages = listReader.getListModes(docTypeID, 0, 0);
		if (pages != null && pages.length > 0) {
			return pages[0].getListId();
		} else {
			return 0;
		}
	}
	
	//-------------------------取特定的查询条件-----------------------
	/**
	 * 取某文档类型的指定名称的列表方式，可用于操作中显示列表的情形。
	 * @param docTypeID
	 * @param listName
	 * @return
	 * @throws E5Exception 
	 */
	public static QueryForm getQuery(int docTypeID, String formCode) throws E5Exception {
		QueryFormReader listReader = (QueryFormReader)Context.getBean(QueryFormReader.class);
		QueryForm pages = listReader.get(docTypeID, formCode);
		if (pages != null) {
			return pages;
		} else {
			return null;
		}
	}
	public static QueryForm getQuery(int docTypeID) throws E5Exception {
		QueryFormReader listReader = (QueryFormReader)Context.getBean(QueryFormReader.class);
		QueryForm[] pages = listReader.getByDocType(docTypeID);
		if (pages != null && pages.length > 0) {
			return pages[0];
		} else {
			return null;
		}
	}
	public static int getQueryID(int docTypeID, String formCode) throws E5Exception {
		QueryFormReader listReader = (QueryFormReader)Context.getBean(QueryFormReader.class);
		QueryForm pages = listReader.get(docTypeID, formCode);
		if (pages != null) {
			return pages.getId();
		} else {
			return 0;
		}
	}
	
	//-------------------------取表单内容-----------------------
	/**
	 * 取某文档类型的指定名称的列表方式，可用于操作中显示列表的情形。
	 * @param docTypeID
	 * @param listName
	 * @return
	 * @throws E5Exception 
	 */
	public static String getFormJsp(int docLibID, String formCode) throws E5Exception {
		FormParam param = new FormParam();
		param.setDocLibID(docLibID);
		param.setFormCode(formCode);
		param.setFvID(docLibReader.get(docLibID).getFolderID());
		
		return FormViewer.getFormJsp(param);
	}
	
	public static List<Pair> getFormJsp(int docLibID, String docIDs, String formCode, String uuid) throws E5Exception {
		String[] docIds = docIDs.split(",");
		String headStr = "";
		List<Pair> list = new ArrayList<Pair>();
		for (String docId : docIds) {
			if(!StringUtils.isBlank(docId)){
				String[] result = getFormJsp(docLibID,Long.parseLong(docId),formCode,uuid);
				headStr = result[0];
				list.add(new Pair(docId, result[1]));
			}
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
	
	public static String getRootRealPath() {
		String classPath = (new BackResult(true)).getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
	    String webPath = classPath.substring(1, classPath.indexOf("WEB-INF"));
	    if(webPath.endsWith("/") || webPath.endsWith("\\")){
	    return webPath;
	    }else {
			return webPath +"/";
		}
	}
}