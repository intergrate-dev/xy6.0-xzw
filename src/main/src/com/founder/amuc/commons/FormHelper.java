package com.founder.amuc.commons;
/**
 * @author leijj
 * @date 2014-9-4
 * Description:
 */
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import net.sf.json.JSONObject;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DataType;
import com.founder.e5.db.DataType.DataTypes;
import com.founder.e5.doc.Document;
import com.founder.e5.dom.DocLibReader;
import com.founder.e5.dom.DocTypeField;
import com.founder.e5.dom.DocTypeReader;
import com.founder.e5.dom.FieldType;
import com.founder.e5.flow.Flow;
import com.founder.e5.flow.FlowNode;
import com.founder.e5.flow.FlowReader;
import com.founder.e5.web.SysUser;
import com.founder.e5.workspace.ProcHelper;
@SuppressWarnings("rawtypes")
public class FormHelper {
	public static final String SUBLISTKEY = "subList";
	
	/**
	 * 获取表单元素的 JSON对象
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public static JSONObject getFormJSONObj(List items) throws Exception{
		if(items == null || items.size() <= 0) return null;
		
		Map<String, Object> formEL = new HashMap<String, Object>();
		Iterator itemIt = items.iterator();
		while (itemIt.hasNext()) {
			FileItem item = (FileItem) itemIt.next();
			if(item == null) break;
			if (item.isFormField()) {
			    String fileName = item.getFieldName();
			    String value = item.getString("UTF-8");
			    if(fileName != null && fileName.length() > 0){
			    	formEL.put(fileName, value);//对表单字段赋值
			    }
		    }
		}
		JSONObject json = JSONObject.fromObject(formEL);
		return json;
	}
	
	/**
	 * 获取界面表单元素的json字符串
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public static String getFormJSONStr(List items) throws Exception{
		JSONObject json = getFormJSONObj(items);
		if(json == null) return "";
		
		return json.toString();
	}
	
	/**
	 * 获取form表单元素:enctype="multipart/form-data"
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public static List getFileItem(HttpServletRequest request) throws Exception {
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if (isMultipart) {
			ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
			upload.setHeaderEncoding("UTF-8");
			upload.setSizeMax(500000000);
			return upload.parseRequest(request);
		} else {
			return null;
		}
	}
	
	/**
	 * 根据字段名或者相应form表单值，并对doc赋值
	 * @param doc
	 * @param request
	 * @param docTypeField
	 * @throws E5Exception
	 */
	public static void setFieldValue(Document doc, HttpServletRequest request, DocTypeField docTypeField) throws E5Exception {
		setFieldValue(doc, request, docTypeField, true);
	}
	
	/**
	 * 一个字段值填写
	 * @param doc
	 * @param request
	 * @param docTypeField
	 * @param direct 字符串类型，是否直接保存。对于非定制表单的操作，不单独处理字符串类型
	 * @throws E5Exception
	 */
	public static void setFieldValue(Document doc, HttpServletRequest request, DocTypeField docTypeField,boolean direct) throws E5Exception {
		String columnCode = docTypeField.getColumnCode();
		String value = get(request, columnCode);
		setFieldValue(doc, docTypeField, request, value, direct);
	}
	/**
	 * 根据传入的json组装数据
	 * @param doc
	 * @param request
	 * @param docTypeField
	 * @param direct 字符串类型，是否直接保存。对于非定制表单的操作，不单独处理字符串类型
	 */
	public static void setJsonValue(Document doc, JSONObject request, DocTypeField docTypeField,boolean direct) {
		String columnCode = docTypeField.getColumnCode();
		if(request.containsKey(columnCode)){
			String value = request.getString(columnCode);
			if(value != null){
				setFieldValue(doc, docTypeField, null, value, direct);
			}
		}
	}
	
	/**
	 * 根据传入的json组装数据
	 * @param doc
	 * @param request
	 * @param docTypeField
	 */
	public static void setJsonValue(Document doc, JSONObject request, DocTypeField docTypeField) {
		String columnCode = docTypeField.getColumnCode();
		if(request.containsKey(columnCode)){
			String value = request.getString(columnCode);
			if(value != null){
				setFieldValue(doc, docTypeField, null, value, true);
			}
		}
	}
	
	/**
	 * 组装表单数据共用方法
	 * @param doc
	 * @param docTypeField
	 * @param request
	 * @param value
	 * @param direct 字符串类型，是否直接保存。对于非定制表单的操作，不单独处理字符串类型
	 */
	public static void setFieldValue(Document doc,DocTypeField docTypeField, HttpServletRequest request,String value, boolean direct) {
		String columnCode = docTypeField.getColumnCode();
		
		DataTypes dataTypes = DataType.DataTypes.valueOf(docTypeField.getDataType());
		switch (dataTypes) {
			/* 字符型:0,1*/
			case CHAR:
				doc.set(columnCode, value);
				break;
			/* 变长字符型:0,1,7,21,24,25,26,27,28,29,30,6,33,34,35 */
			case VARCHAR:
				if (direct)
					doc.set(columnCode, value);
				else
					setVarcharValue(columnCode, docTypeField, doc, request);
				break;
			/* 整数:0,1,2 */
			case INTEGER:
				if(docTypeField.getEditType()==2){
					if ("on".equals(value))
						doc.set(columnCode, 1);
					else
						doc.set(columnCode, 0);
				}else {
					doc.set(columnCode, getInt(value));
				}
				break;
			/* 长整数:0,1,2  */
			case LONG:
				if(docTypeField.getEditType()==2){
					if ("on".equals(value))
						doc.set(columnCode, 1);
					else
						doc.set(columnCode, 0);
				}else {
					doc.set(columnCode, getInt(value));
				}
				break;
			/* 实数:0,1 */
			case FLOAT:
				doc.set(columnCode, getFloat(value));
				break;
			/* 双精度实数:0,1  */
			case DOUBLE:
				doc.set(columnCode, getDouble(value));
				break;
			/* BLOB:0 */
			case BLOB:
				doc.set(columnCode, value);
				break;
			/* CLOB:21 */
			case CLOB:
				doc.set(columnCode, value);
				break;
			
			case DATE:/* DATE:0,1*/
			case TIME:/* TIME:0,1 */
			case TIMESTAMP:/* DATETIME:0,1*/
				if(value != null){
					Date date = parseDate(value, dataTypes);
					doc.set(columnCode, date);
				}
				break;
			/* BFILE:0 */
			case EXTFILE:
				doc.set(columnCode, value);
				break;
			default:
				break;
		}
	}
	
	/**
	 * 日期、时间、日期时间的解析
	 * @param value
	 * @param dataTypes
	 * @return
	 */
	protected static Date parseDate(String value, DataTypes dataTypes) {
		if ("".equals(value)) return null;
		
		try {
			switch (dataTypes) {
				case DATE:
					return dateFormat.parse(value);
				case TIME:
					return timeFormat.parse(value);
				case TIMESTAMP:
					if (value.length() > 10)
						return datetimeFormat.parse(value);
					else
						return dateFormat.parse(value);
				default:
					return null;
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected static void setVarcharValue(String columnCode, DocTypeField docTypeField, Document doc, HttpServletRequest request){
		String value = get(request, columnCode);
		if (value == null) {
			value = "";
		}
		FieldType fieldType = FieldType.get(docTypeField.getEditType());
		String[] extFields = fieldType.getExtFields();
		String[] extTypes = fieldType.getExtTypes();
		
		switch (docTypeField.getEditType()) 
		{
		case DocTypeField.EDITTYPE_FREE: //任意填写（单行）
		case DocTypeField.EDITTYPE_FREE_AUTOCOMPLETE:
			
		case DocTypeField.EDITTYPE_ENUM: //单选（下拉框方式）	下拉框 select
		case DocTypeField.EDITTYPE_SELECT:
		case DocTypeField.EDITTYPE_SELECT_RADIO:
		case DocTypeField.EDITTYPE_SELECT_RADIO_DYNAMIC:
						
		case DocTypeField.EDITTYPE_BOOLEAN: //单选（是/否选择）	复选框，打钩 or 不打钩
			
		case DocTypeField.EDITTYPE_FREE_LINES: //任意填写（多行）	Textarea	
		case DocTypeField.EDITTYPE_EMAIL: //电子邮件（专用文本，自动进行格式验证）
		case DocTypeField.EDITTYPE_PHONE: //固定电话（专用文本，自动进行格式验证）
		case DocTypeField.EDITTYPE_MOBILE: //手机（专用文本，自动进行格式验证）
			doc.set(columnCode, value);
			break;
		case DocTypeField.EDITTYPE_DEPT: //部门（部门树）	一个输入框用于显示，一个按钮用于弹出选择树：
		case DocTypeField.EDITTYPE_USER: //用户（用户: //）	类似上面
		case DocTypeField.EDITTYPE_ROLE: //角色（角色树）
		case DocTypeField.EDITTYPE_TREE: //分类（分类树）
		case DocTypeField.EDITTYPE_TREE_MULTI: //分类（分类树，可多选）	类似上面
		case DocTypeField.EDITTYPE_DEPT_MULTI: //部门（部门树，可多选）	类似上面
		case DocTypeField.EDITTYPE_USER_MULTI: //用户（用户树，可多选）	类似上面
		case DocTypeField.EDITTYPE_ROLE_MULTI: //角色（角色树，可多选）
		case DocTypeField.EDITTYPE_FREE_AUTOCOMPLETE_KEYVALUE:
		case DocTypeField.EDITTYPE_TREE_SELECT:
		case DocTypeField.EDITTYPE_OTHER_DATA:
			for (int i = 0; i < extFields.length; i++) {
				value = request.getParameter(columnCode + extFields[i]);
				if (extTypes[i].equals(DataType.VARCHAR))
					doc.set(columnCode + extFields[i], value);
				else if (extTypes[i].equals(DataType.INTEGER)) {
					if (StringUtils.isBlank(value))
						doc.set(columnCode + extFields[i], null);
					else
						doc.set(columnCode + extFields[i], Integer.parseInt(value));
				}
			}
			break;
		case DocTypeField.EDITTYPE_MULTI: //多选	多行select
		case DocTypeField.EDITTYPE_MULTI_DYNAMIC:
		{
			String[] values = request.getParameterValues(columnCode);
			doc.set(columnCode, StringUtils.join(values));
			
			break;
		}
		case DocTypeField.EDITTYPE_MULTI_CHECKBOX:
		case DocTypeField.EDITTYPE_MULTI_CHECKBOX_DYNAMIC:
		{	
			String[] values = request.getParameterValues(columnCode);
			doc.set(columnCode, join(values));
			
			break;
		}
		case DocTypeField.EDITTYPE_ADDRESS: //地址拆分（分开填写方式，分为：省,市,区/县,街道,楼号）	地址：_____省____市___区/县____街道____楼
			StringBuilder address = new StringBuilder();
			for (int i = 1; i < extFields.length; i++) {
				value = request.getParameter(columnCode + extFields[i]);
				doc.set(columnCode + extFields[i], value);
				address.append(value);
			}
			doc.set(columnCode, address.toString());
			
			break;
		case DocTypeField.EDITTYPE_DATE_SPLIT: //日期拆分（分开填写方式，分为：年,月,日）	日期：____年__月___日	
			StringBuilder date = new StringBuilder();
			for (int i = 1; i < extFields.length; i++) {
				String _value = request.getParameter(columnCode + extFields[i]);
				if (StringUtils.isBlank(_value))
					doc.set(columnCode + extFields[i], 0);
				else
					doc.set(columnCode + extFields[i], Integer.parseInt(_value));
				
				if (!StringUtils.isBlank(_value)) {
					if (i > 1) date.append("-");
					date.append(_value);
				}
			}
			doc.set(columnCode, date.toString());

			break;
		default:
			break;
		}
	}
	protected final static String get(HttpServletRequest request, String key) {
		return request.getParameter(key);
	}
	protected final int getInt(HttpServletRequest request, String key) {
		return Integer.parseInt(get(request, key));
	}
	protected final int getInt(HttpServletRequest request, String key, int defaultValue) {
		try {
			return Integer.parseInt((String)request.getParameter(key));
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	protected static Double getDouble(String value){
		Double d = (double) 0;
		try {
			d = new Double((value==null||"".equals(value))?"0":value);
		} catch (Exception e) {
		}
    	return d;
    }
	//字符串连接，去掉空值
	protected static String join(String[] values) {
		StringBuilder result = new StringBuilder();
		for (String value : values) {
			if (StringUtils.isBlank(value))
				continue;
			
			if (result.length() > 0)
				result.append(",");
			result.append(value);
		}
		return result.toString();
	}

	/**
	 * 根据流程名称得到需要的流程结点。<br/>
	 * 新建的文档提交时，一些平台字段必须填写。此方法用来自动填写这些平台字段。<br/>
	 * 此方法传入的doc对象得到文档库ID，根据文档库ID得到文档类型，
	 * 从request中得到用户信息。<br/>
	 * 若必要，从request的参数FVID得到文件夹ID并赋值。<br/>
	 * 另外还自动填写删除标记、锁状态、创建时间、最后修改时间
	 * @param doc
	 * @param request
	 * @param flowName：流程名称
	 * @throws Exception
	 */
	public static void initDoc(Document doc, HttpServletRequest request, String flowName)
	throws Exception
	{
		int docLibID = doc.getDocLibID();
		DocLibReader docLibReader = (DocLibReader)Context.getBean(DocLibReader.class);
		int docTypeID = docLibReader.get(docLibID).getDocTypeID();

		FlowReader flowReader = (FlowReader)Context.getBean(FlowReader.class);
		Flow flow = flowReader.getFlow(docTypeID, flowName);
		int flowID = 0, flowNodeID = 0;
		String status = null;
		if (flow != null)
		{
			flowID = flow.getID();
			FlowNode[] nodes = flowReader.getFlowNodes(flowID);
			if (nodes != null)
			{
				flowNodeID = nodes[0].getID();
				status = nodes[0].getWaitingStatus();
			}
		}
		SysUser user = ProcHelper.getUser(request);
		doc.setAuthors(user.getUserName());
		doc.setCurrentFlow(flowID);
		doc.setCurrentNode(flowNodeID);
		doc.setCurrentStatus(status);
		
		doc.setCurrentUserID(user.getUserID());
		doc.setCurrentUserName(user.getUserName());
		
		if (doc.getFolderID() < 1){
			String fvID = request.getParameter("FVID");
			doc.setFolderID(Integer.parseInt((fvID == null || "".equals(fvID)) ? "0" : fvID));
		}
			
		
		doc.setDeleteFlag(0);
		doc.setLocked(false);
		doc.setCreated(DateUtils.getTimestamp());
		doc.setLastmodified(DateUtils.getTimestamp());
		
	}
	
	public static int getInt(String value){
    	int res = 0;
    	try {
			res = new Integer((value==null||"".equals(value))?"0":value);
		} catch (Exception e) {
			
		}
    	return res;
    }
	public static float getFloat(String value){
		float res = 0;
    	try {
			res = new Float((value==null||"".equals(value))?"0":value);
		} catch (Exception e) {
			
		}
    	return res;
    }
	/** 
	* @author  leijj 
	* 功能： 根据doc组装界面字段
	* @param doc
	* @param request
	* @param oper
	* @throws E5Exception 
	*/ 
	public static void assembleDoc(Document doc, HttpServletRequest request) throws E5Exception {
		DocTypeReader docTypeReader = (DocTypeReader)Context.getBean(DocTypeReader.class);
		DocTypeField[] fields = docTypeReader.getFieldsExt(doc.getDocTypeID());
		if(fields != null && fields.length > 0){
			for(DocTypeField field : fields){
				setFieldValue(doc, request, field, false);
			}
		}
	}
	
	protected static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	protected static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
	protected static SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
}