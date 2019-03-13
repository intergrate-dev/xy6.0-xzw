package com.founder.xy.article;

import java.text.SimpleDateFormat;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Component;

import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
/**
 * 草稿管理器
 * 
 * @author han.xf
 *
 */
@Component
public class DraftManager {
	/**
	 * 草稿保存
	 * @param doc 草稿
	 * @return 保存成功返回true
	 */
	public boolean save(Document doc){
		DocumentManager docManager = DocumentManagerFactory.getInstance();
	    try {
	        docManager.save(doc);
	        return true;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	/**
	 * 根据写稿人ID得到草稿列表
	 * @param authorID 写稿人ID
	 * @return 返回json数据
	 */
	public JSONArray findAllList(int docLibID, int authorID){
		JSONArray arr = new JSONArray() ;
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			Document[] docs = docManager.find(docLibID, "SYS_AUTHORID=? order by SYS_CREATED DESC", new Object[]{authorID});
			for (Document doc : docs) {
				JSONObject obj = new JSONObject() ;
				obj.put("docID", doc.getDocID()) ;
				obj.put("title", doc.getTopic()) ;
				obj.put("lastModifided", df.format(doc.getCreated())) ;
				
				arr.add(obj) ;
			}
		} catch (Exception e) {
			e.printStackTrace() ;
	    }
		return arr ;
	}
	
	/**
	 * 根据草稿ID删除草稿
	 * @param docID 草稿ID
	 * @return 删除成功返回true,失败返回false
	 */
	public boolean delete(int docLibID, long docID){
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			docManager.delete(docLibID, docID);
			return true;
		} catch (Exception e) {
			e.printStackTrace() ;
			return false ;
	    }
	}
}
