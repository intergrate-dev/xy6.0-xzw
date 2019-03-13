package com.founder.amuc.workspace.form;

import javax.servlet.http.HttpServletRequest;

import com.founder.amuc.commons.InfoHelper;
import com.founder.e5.commons.DomHelper;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.workspace.ProcHelper;
import com.founder.e5.workspace.app.form.FormSaver;

/**
 * 本系统使用的FormSaver
 * 1）使不同文档类型的文档ID单独计数
 * @author Gong Lijie
 */
public class NewFormSaver extends FormSaver{
	//继承并修改：每个文档类型使用单独的ID ----准备数据对象。若是新建，则做文档初始化准备
	protected Document prepareDoc(HttpServletRequest request) throws Exception {
		int docLibID = getInt(request, "DocLibID");
		long docID = getInt(request, "DocID", 0);
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc;
		if (docID > 0) {
			doc = docManager.get(docLibID, docID);
		}
		else {
			int docTypeID = DomHelper.getDocTypeIDByLibID(docLibID);
			long id = InfoHelper.getID(docTypeID);
			doc = docManager.newDocument(docLibID, id);
			
			int fvID = getInt(request, "FVID", 0);
			if (fvID > 0) doc.setFolderID(fvID);
			
			ProcHelper.initDoc(doc, request);
		}
		
		return doc;
	}
}
