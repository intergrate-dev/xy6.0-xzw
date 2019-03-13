package com.founder.amuc.commons;
/**
 * @author leijj
 * @date 2014-9-1
 * Description:
 */
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.dom.DocLibReader;
import com.founder.e5.workspace.app.form.FormParam;
import com.founder.e5.workspace.app.form.FormViewer;

public class FormViewerHelper {
	private static DocLibReader docLibReader = (DocLibReader) Context.getBean(DocLibReader.class);
	
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
	
	public static String[] getFormJsp(int docLibID, String formCode) throws E5Exception {
		FormParam param = new FormParam();
		param.setDocLibID(docLibID);
		param.setFormCode(formCode);
		param.setFvID(docLibReader.get(docLibID).getFolderID());
		
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
	
	/**
	 * 去除定制表单中的form提交代码段和文档库的基础字段
	 * @param formStr
	 * @return
	 */
	public static String delFormStr(String formStr, int docLibID){
		if(formStr == null) return "";
		
		String formStrRtn = "";
		formStrRtn = delFormStr(delSysFieldStr(formStr, docLibID));
		return formStrRtn;
	}
	
	/**
	 * 去除定制表单中的form提交代码段
	 * @param formStr
	 * @return
	 */
	public static String delFormStr(String formStr){
		if(formStr == null) return "";
		
		String formStrRtn = "";
		formStrRtn = formStr.replaceAll("<iframe id=\"iframe\" style=\"display:none;\"></iframe>", "")
				.replaceAll("<form id=\"form\" method=\"post\" action=\"../../e5workspace/manoeuvre/FormSubmit.do\">", "")
				.replaceAll("</form>", "");
		return formStrRtn;
	}
	
	/**
	 * 去除定制表单中的系统字段代码
	 * @param formStr
	 * @return
	 */
	public static String delSysFieldStr(String formStr, int docLibID){
		if(formStr == null) return "";
		
		String formStrRtn = "";
		formStrRtn = formStr.replaceAll("<input type=\"hidden\" id=\"DocLibID\" name=\"DocLibID\" value=\""+docLibID+"\"/>", "")
			.replaceAll("<input type=\"hidden\" id=\"DocID\" name=\"DocID\" value=\"0\"/>", "")
			.replaceAll("<input type=\"hidden\" id=\"FVID\" name=\"FVID\" value=\"0\"/>", "")
			.replaceAll("<input type=\"hidden\" id=\"UUID\" name=\"UUID\" value=\"\"/>", "")
			.replaceAll("<input type=\"hidden\" id=\"FormID\" name=\"FormID\" value=\"0\"/>", "")
			.replaceAll("<input type=\"hidden\" id=\"SaveSubmit\" name=\"SaveSubmit\" value=\"\"/>", "");
		return formStrRtn;
	}
	
	public static String delSysFieldStr(String formStr, int docLibID, int docID, int FVID, String UUID, int invoiceFormID){
		if(formStr == null) return "";
		
		String formStrRtn = "";
		formStrRtn = formStr.replaceAll("<input type=\"hidden\" id=\"DocLibID\" name=\"DocLibID\" value=\""+docLibID+"\"/>", "")
			.replaceAll("<input type=\"hidden\" id=\"DocID\" name=\"DocID\" value=\""+docID+"\"/>", "")
			.replaceAll("<input type=\"hidden\" id=\"FVID\" name=\"FVID\" value=\""+FVID+"\"/>", "")
			.replaceAll("<input type=\"hidden\" id=\"UUID\" name=\"UUID\" value=\""+UUID+"\"/>", "")
			.replaceAll("<input type=\"hidden\" id=\"FormID\" name=\"FormID\" value=\""+invoiceFormID+"\"/>", "")
			.replaceAll("<input type=\"hidden\" id=\"SaveSubmit\" name=\"SaveSubmit\" value=\"\"/>", "");
		return formStrRtn;
	}
	
	/**
	 * 取定制的表单的中心区域，去掉Html头和body定义部分
	 * @param param
	 * @return
	 * @throws E5Exception
	 */
	public static String getFormContent(String jsp) throws E5Exception {
		if (jsp == null) return null;
		
		int begin = jsp.indexOf("<body>");
		int end = jsp.lastIndexOf("</body>");
		
		return jsp.substring(begin + 6, end);
	}
}