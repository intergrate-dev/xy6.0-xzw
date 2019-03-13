package com.founder.amuc.workspace;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.founder.amuc.commons.Constant;
import com.founder.amuc.commons.InfoHelper;
import com.founder.e5.commons.DomHelper;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.e5.web.BaseController;

/**
 * 细览中的相关数据列表呈现。
 * 这里准备列表参数
 * @author huyong
 * 2017-3-6
 */
@Controller
@RequestMapping("/amuc")
public class ViewRelativeController extends BaseController{

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected void handle(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception {
		
	}
	
	@RequestMapping("/ViewRelative.do")
	public ModelAndView viewRelativeList(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
		
		String tenantCode = InfoHelper.getTenantCode(request);
		int flag = getInt(request, "flag");
		String docID = request.getParameter("DocIDs");
		
		String formula = null;
		boolean showSearchBar = false;
		boolean showToolkit=false;
		int docTypeID = 0;
		String typeCode = "";
		String listName = null;
		
		switch (flag) {
		case 2:	//会员的交易类行为
			typeCode = Constant.DOCTYPE_EVENTTRADING;
			formula = "eMemberID_EQ_" + docID;
			showSearchBar = true;
			break;
		case 3:	//会员的非交易类行为
			typeCode = Constant.DOCTYPE_MEMBEREVENT;
			formula = "eMemberID_EQ_" + docID;
			showSearchBar = true;
			break;
		case 4:	//会员的积分
			typeCode = Constant.DOCTYPE_MEMBERSCORE;
			formula = "msMember_ID_EQ_" + docID + "_AND_msType_LE_4";
			listName = "会员积分列表";
			break;
		case 6:	//会员的兑换记录
			typeCode = Constant.DOCTYPE_MEMBERSCORE;
			docTypeID = InfoHelper.getTypeIDByCode(typeCode);
			formula = "SYS_DELETEFLAG_EQ_0_SPC__AND__SPC_msMember_ID_EQ_" + docID + "_AND_msType_EQ_3";
			listName = "会员兑换列表";
			break;
		case 8:
			typeCode = Constant.DOCTYPE_COLLECTION;
			docTypeID = InfoHelper.getTypeIDByCode(typeCode);
			formula = "SYS_DELETEFLAG_EQ_0_SPC__AND__SPC_cUserId_EQ_" + docID;
			listName = "收藏列表";
			break;
		case 91:	//邀请码使用表
			typeCode = Constant.DOCTYPE_INVITECODELOG;
			formula = "SYS_DELETEFLAG_EQ_0_SPC__AND__SPC_icCodeID_EQ_" + docID;
			showSearchBar = true;
			break;
		default:
			break;
		}
		docTypeID = InfoHelper.getTypeIDByCode(typeCode);
		
		DocLib docLib = InfoHelper.getLib(typeCode, tenantCode);
		int docLibID = docLib.getDocLibID();
		int folderID = DomHelper.getFVIDByDocLibID(docLibID);
		int listID = (listName == null)
				? DomHelper.getListID(docTypeID)
				: DomHelper.getListID(docTypeID, listName);
		
	    Map<String, Object> model = new HashMap<String, Object>();
		model.put("docTypeID", docTypeID);
		model.put("docLibID", docLibID);
		model.put("folderID", folderID);
		model.put("listID", listID);
		model.put("formula", formula);
		model.put("showSearchBar", showSearchBar);
		model.put("showToolkit", showToolkit);
		
		return new ModelAndView("/amuc/ViewRelative", model);
	}
}
