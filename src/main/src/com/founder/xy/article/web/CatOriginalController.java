package com.founder.xy.article.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.founder.e5.cat.CatReader;
import com.founder.e5.cat.Category;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.workspace.ProcHelper;
import com.founder.e5.workspace.app.CatRoleService;
import com.founder.xy.commons.CatTypes;
import com.founder.xy.commons.InfoHelper;

/**
 * 原稿分类树，用于主界面的原稿库
 * @author Gong Lijie
 */
@Controller
@RequestMapping("/xy/article")
public class CatOriginalController {
	private static String iconColRoot = "../xy/img/home.png";
	private static String iconCol = "../xy/img/col.png";
	
	/** 栏目树（第一层），带操作权限 */
	@RequestMapping(value = "CatTree.do")
	public void tree(
			@RequestParam(defaultValue="0") int catTypeID, 
			@RequestParam(defaultValue="0") int parentID,
			@RequestParam(defaultValue="1") int siteID,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		int roleID = ProcHelper.getRoleID(request);
		if (catTypeID == 0) catTypeID = CatTypes.CAT_ORIGINAL.typeID();
		
		Category[] cats = null;
		if (parentID == 0) {
			CatRoleService catRoleService = new CatRoleService();
			cats = catRoleService.getCatsByRole(roleID, catTypeID);
		} else {
			CatReader catReader = (CatReader)Context.getBean(CatReader.class);
			cats = catReader.getSubCats(catTypeID, parentID);
		}
		
		String result = jsonTreeWithParent(cats);
		
		InfoHelper.outputJson(result, response);
	}

	//带父节点（无权限）的栏目树的json
	private String jsonTreeWithParent(Category[] roots) throws E5Exception {
		JSONArray jsonArr  = new JSONArray();
		
		if (roots != null) {
			for (Category col : roots) {
				JSONObject json = jsonOneCol(col);
				jsonArr.add(json);
			}
		}
		return jsonArr.toString();
	}
	
	//处理一个栏目的json转换
	private static JSONObject jsonOneCol(Category col) throws E5Exception {
		String icon = (col.getParentID() == 0) ? iconColRoot : iconCol;

		JSONObject json = new JSONObject();
		json.put("id", col.getCatID());
		json.put("name", col.getCatName());
		json.put("title", col.getCatName() + " [" + col.getCatID() + "]");
		json.put("casID", col.getCascadeID());
		json.put("casName", col.getCascadeName());
		json.put("icon", icon);
		if (col.getChildCount() > 0) json.put("isParent", "true");//有子节点，可展开

		return json;
	}
}
