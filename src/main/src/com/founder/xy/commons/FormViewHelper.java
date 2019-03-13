package com.founder.xy.commons;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.founder.e5.context.E5Exception;
import com.founder.e5.workspace.app.form.FormParam;
import com.founder.e5.workspace.app.form.FormViewer;

/**
 * 表单定制辅助类，用于包含定制表单+自定义逻辑的操作界面。
 * @author Gong Lijie
 */
public class FormViewHelper {

	public static String[] getFormJsp(int docLibID, long docID, String formCode, String uuid) throws E5Exception {
		FormParam param = new FormParam();
		param.setDocLibID(docLibID);
		param.setFormCode(formCode);
		param.setDocID(docID);
		param.setFvID(LibHelper.getLibByID(docLibID).getFolderID());
		param.setUuid(uuid);
		
		String content = FormViewer.getFormJsp(param);
		
		Document html = Jsoup.parse(content);
		
		String[] result = new String[2];
		result[0] = html.select("head").get(0).html(); //取定制的表单中的引用js和css
		result[1] = html.select("form").get(0).html(); //取定制的表单中的form里的内容（字段定义）
		
		return result;
	}
}
