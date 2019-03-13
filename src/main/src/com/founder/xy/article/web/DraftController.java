package com.founder.xy.article.web;

import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.web.SysUser;
import com.founder.e5.web.WebUtil;
import com.founder.e5.workspace.ProcHelper;
import com.founder.xy.article.DraftManager;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
/**
 * 草稿控制器
 * @author han.xf
 */
@Controller
@RequestMapping("/xy/article")
public class DraftController {
	@Autowired
	private DraftManager draftManager ;
	
	@RequestMapping("Draft.do")
	public void saveDraft(HttpServletRequest request,HttpServletResponse response)throws E5Exception{
		long docID = WebUtil.getLong(request, "docID", 0) ; //稿件ID
		String title = WebUtil.get(request, "title") ; //标题
		String content = WebUtil.get(request, "content") ; //内容
		SysUser user = ProcHelper.getUser(request) ;
		
		JSONObject obj = new JSONObject() ;
		if(user != null){
			int docLibID = getDraftLibID(request);
			
			DocumentManager docManager = DocumentManagerFactory.getInstance() ;
			Document draft = docManager.get(docLibID, docID) ; 
			if(draft == null){ //新增草稿
				draft = docManager.newDocument(docLibID, docID) ;
			}
			draft.setTopic(title) ;
			draft.setCreated(new Timestamp( System.currentTimeMillis() ));
			draft.setAuthors(user.getUserName()) ;
			draft.set("SYS_AUTHORID",user.getUserID()) ;
			draft.set("a_content",content) ;
			
			boolean result = draftManager.save(draft) ; //保存

			obj.put("info",result) ;
		}else{ //写稿人为空
			obj.put("info",false) ;
		}
		InfoHelper.outputJson(obj.toString(), response) ;
	}
	
	/**
	 * 根据草稿ID取得草稿
	 * @param docID 草稿ID
	 * @return 返回json数据
	 */
	@RequestMapping("DraftView.do")
	public void getDraft(HttpServletRequest request,HttpServletResponse response) throws E5Exception{
		long docID = WebUtil.getLong(request, "docID", 0) ;
		int userID = ProcHelper.getUserID(request) ;
		
		JSONObject obj = new JSONObject() ;
		if (userID > 0){
			int docLibID = getDraftLibID(request);
			
			DocumentManager docManager = DocumentManagerFactory.getInstance() ;
			Document draft = docManager.get(docLibID, docID) ; 
			if (draft != null && draft.getInt("SYS_AUTHORID") == userID){
				JSONObject _obj = new JSONObject() ;
				_obj.put("docID", draft.getDocID()) ;
				_obj.put("title", draft.getTopic()) ;
				_obj.put("content", draft.getString("a_content")) ;
				
				obj.put("info", true) ;
				obj.put("draft", _obj) ;
			}else{
				obj.put("info", false) ; //写稿人ID与稿件ID不同,非法操作
			}
		}else{ //写稿人为空
			obj.put("info", false) ;
		}
		InfoHelper.outputJson(obj.toString(), response) ;
	}
	
	@RequestMapping("DraftList.do")
	public void getDraftList(HttpServletRequest request,HttpServletResponse response){
		JSONObject obj = new JSONObject() ;
		int userID = ProcHelper.getUserID(request) ;
		if (userID > 0){
			int docLibID = getDraftLibID(request);
			JSONArray list = draftManager.findAllList(docLibID, userID);
			
			obj.put("info", true) ;
			obj.put("count", list.size()) ;
			obj.put("drafts", list) ;
		}else{ //非法操作,写稿人为空
			obj.put("info",false) ;
		}
		InfoHelper.outputJson(obj.toString(), response) ;
	}
	
	@RequestMapping("DeleteDraft.do")
	public void deleteDraft(HttpServletRequest request,HttpServletResponse response){
		int docLibID = getDraftLibID(request);
		long docID = WebUtil.getLong(request, "docID", 0) ;
		
		boolean rs = draftManager.delete(docLibID, docID) ;
		
		JSONObject obj = new JSONObject() ;
		obj.put("info", rs) ;
		
		InfoHelper.outputJson(obj.toString(), response) ;
	}
	
	private int getDraftLibID(HttpServletRequest request) {
		String tCode = InfoHelper.getTenantCode(request);
		return LibHelper.getLibID(DocTypes.DRAFT.typeID(), tCode);
	}
}
