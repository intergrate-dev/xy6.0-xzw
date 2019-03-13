package com.founder.xy.article.web;

import java.util.Date;
import java.util.Set;

import org.springframework.stereotype.Component;

import net.sf.json.JSONObject;

import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.db.DataType;
import com.founder.e5.db.DataType.DataTypes;
import com.founder.e5.doc.Document;
import com.founder.e5.dom.DocTypeField;
import com.founder.e5.dom.DocTypeReader;
import com.founder.e5.dom.FieldType;
import com.founder.e5.workspace.app.form.FormSaver;

/**
 * 表单提交。
 * 继承FormSaver，把从request读数据改成从json中读，并把fillValues变成public
 * @author Gong Lijie
 */
@Component
public class ArticleSaver extends FormSaver{

	public ArticleSaver() {
		super();
		docTypeReader = (DocTypeReader)Context.getBean(DocTypeReader.class);
	}

	/**
	 * 表单数据填写
	 * 传入Document对象，按表单的定制字段进行填写
	 * @param doc
	 * @param request
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void fillValues(Document doc, JSONObject request, int roleID) throws Exception {
		
		DocTypeField[] fields = docTypeReader.getFields(doc.getDocTypeID());
		Set<String> keys = request.keySet();
		for (String key : keys) {
			DocTypeField field = getField(fields, key);
			if (field != null)
				setValue(field, doc, request, false);
		}
	}
	
	/**
	 * 一个字段值填写
	 * @param docTypeField
	 * @param doc
	 * @param request
	 * @param direct 字符串类型，是否直接保存。对于非定制表单的操作，不单独处理字符串类型
	 */
	protected void setValue(DocTypeField docTypeField, Document doc, JSONObject request, boolean direct) {
		String columnCode = docTypeField.getColumnCode();
		String value = null;
		try {
			value = request.getString(columnCode);
		} catch (Exception e) {
		}
		
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
				if ("on".equals(value) || "1".equals(value))
					doc.set(columnCode, 1);
				else
					doc.set(columnCode, 0);
			}else {
				doc.set(columnCode,getInt(value));
			}
			break;
		/* 长整数:0,1,2  */
		case LONG:
			if(docTypeField.getEditType()==2){
				if ("on".equals(value) || "1".equals(value))
					doc.set(columnCode, 1);
				else
					doc.set(columnCode, 0);
			}else {
				doc.set(columnCode, getLong(value));
			}
			break;
		/* 实数:0,1 */
		case FLOAT:
			doc.set(columnCode, getFloat(value));
			break;
		/* 双精度实数:0,1  */
		case DOUBLE:
			doc.set(columnCode, getFloat(value));
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
			Date date = parseDate(value, dataTypes);
			doc.set(columnCode, date);
			break;
		/* BFILE:0 */
		case EXTFILE:
			doc.set(columnCode, value);
			break;
		default:
			break;
		}
	}
	protected void setVarcharValue(String columnCode, DocTypeField docTypeField, Document doc, JSONObject request){
		String value = null;
		try {
			value = request.getString(columnCode);
		} catch (Exception e) {
		}
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
		case DocTypeField.EDITTYPE_DEPT_MULTI: //部门（部门树，可多选）	类似上面
		case DocTypeField.EDITTYPE_USER_MULTI: //用户（用户树，可多选）	类似上面
		case DocTypeField.EDITTYPE_ROLE_MULTI: //角色（角色树，可多选）
		case DocTypeField.EDITTYPE_FREE_AUTOCOMPLETE_KEYVALUE:
		case DocTypeField.EDITTYPE_TREE_SELECT:
		case DocTypeField.EDITTYPE_OTHER_DATA:
			for (int i = 0; i < extFields.length; i++) {
				String code = columnCode + extFields[i];
				value = getString(request, code);
				if (value == null) continue;
				
				if (extTypes[i].equals(DataType.VARCHAR))
					doc.set(code, value);
				else if (extTypes[i].equals(DataType.INTEGER)) {
					if (StringUtils.isBlank(value))
						doc.set(code, null);
					else
						doc.set(code, Integer.parseInt(value));
				}
			}
			break;
		case DocTypeField.EDITTYPE_TREE: //分类（分类树）
		case DocTypeField.EDITTYPE_TREE_MULTI: //分类（分类树，可多选）	类似上面
			for (int i = 0; i < extFields.length; i++) {
				String code = columnCode + extFields[i];
				value = getString(request, code);
				if (value == null)
					continue;
				
				//对栏目ID，因为存的是整数，所以要单独处理
				if (code.equals("a_columnID")) {
					int id = StringUtils.isBlank(value) ? 0 : Integer.parseInt(value);
					doc.set("a_columnID", id);
				} else {
					if (extTypes[i].equals(DataType.VARCHAR))
						doc.set(columnCode + extFields[i], value);
					else if (extTypes[i].equals(DataType.INTEGER)) {
						if (StringUtils.isBlank(value))
							doc.set(code, null);
						else
							doc.set(code, Integer.parseInt(value));
					}
				}
			}
			break;
		case DocTypeField.EDITTYPE_MULTI: //多选	多行select
		case DocTypeField.EDITTYPE_MULTI_DYNAMIC: {
			String[] values = (String[])request.getJSONArray(columnCode).toArray();
			doc.set(columnCode, StringUtils.join(values));
			
			break;
		}
		case DocTypeField.EDITTYPE_MULTI_CHECKBOX:
		case DocTypeField.EDITTYPE_MULTI_CHECKBOX_DYNAMIC: {	
			String[] values = (String[])request.getJSONArray(columnCode).toArray();
			doc.set(columnCode, join(values));
			
			break;
		}
		case DocTypeField.EDITTYPE_ADDRESS: //地址拆分（分开填写方式，分为：省,市,区/县,街道,楼号）	地址：_____省____市___区/县____街道____楼
			StringBuilder address = new StringBuilder();
			for (int i = 1; i < extFields.length; i++) {
				String code = columnCode + extFields[i];
				
				String _value = getString(request, code);
				if (_value == null)
					continue;
				
				doc.set(code, _value);
				address.append(value);
			}
			doc.set(columnCode, address.toString());
			
			break;
		case DocTypeField.EDITTYPE_DATE_SPLIT: //日期拆分（分开填写方式，分为：年,月,日）	日期：____年__月___日	
			StringBuilder date = new StringBuilder();
			for (int i = 1; i < extFields.length; i++) {
				String code = columnCode + extFields[i];
				
				String _value = getString(request, code);
				if (_value == null)
					continue;
				
				if (StringUtils.isBlank(_value))
					doc.set(code, 0);
				else
					doc.set(code, Integer.parseInt(_value));
				
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
	//从json中读string
	private String getString(JSONObject json, String key) {
		if (json.containsKey(key))
			return json.getString(key);
		else
			return null;
	}
}
