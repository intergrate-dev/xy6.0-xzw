package com.founder.xy.system.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.founder.e5.cat.CatManager;
import com.founder.e5.cat.CatType;
import com.founder.e5.cat.Category;
import com.founder.e5.context.Context;
import com.founder.e5.web.WebUtil;
import com.founder.e5.web.cat.service.CatTreeService;
import com.founder.e5.web.util.HtmlEncoder;
import com.founder.xy.commons.CatTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.config.SecurityHelper;
import com.founder.xy.workspace.MainHelper;

/**
 * 前台管理分类
 * @author Gong Lijie
 */
@Controller
@RequestMapping("/xy/system/")
public class CatManageController {
	
	/**分类树*/
	@RequestMapping(value = "CatTree.do")
	public void catTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
		CatManager catManager = (CatManager)Context.getBean(CatManager.class);
		
    	int catType = WebUtil.getInt(request, "catType", 0);//当前分类类型
    	if (catType == 0) {
    		int siteID = MainHelper.getSiteEnable(request);
    		List<CatType> types = new ArrayList<>();
    		/*
    		 * 栏目类型和栏目样式不再按站点区分，也不再在前台管理，改为系统管理端统一管理
    		if (SecurityHelper.appUsable()) {
    			types.add(catManager.getType(CatTypes.CAT_COLUMN.typeID()));//栏目类型
    			types.add(catManager.getType(CatTypes.CAT_COLUMNSTYLE.typeID()));//栏目样式
    		}
    		*/
    		//前台可管理的互动分类
    		if (SecurityHelper.appUsable() && SecurityHelper.nisUsable()) {
    			types.add(catManager.getType(CatTypes.CAT_DISCUSSTYPE.typeID()));//话题分类
    			types.add(catManager.getType(CatTypes.CAT_QA.typeID()));//问答分类
    		}
    		//有Web时，前台可管理的分类
    		if (SecurityHelper.webUsable()) {
    			types.add(catManager.getType(CatTypes.CAT_ARTICLETRADE.typeID()));//稿件行业
    		}
			
			StringBuffer sbTree = new StringBuffer(200);
			sbTree.append("<tree>");
			String tenantCode = InfoHelper.getTenantCode(request);
			for (int i = 0; i < types.size(); i++) {
				CatType type = types.get(i);
				int rootSiteCatID = getSiteCatID(tenantCode, type.getCatType(), siteID);
				sbTree.append(assembleOneType(type, siteID, rootSiteCatID));
			}
			sbTree.append("</tree>");
			
			InfoHelper.outputXML(sbTree.toString(), response);
    	} else {
    		//一个分类类型下的分类树
	    	int catID   = WebUtil.getInt(request, "catID", 0);  //上级分类ID        	
	    	Category[] cats = catManager.getSubCats(catType, catID, null);
	    	
	    	CatTreeService.setWebRoot(WebUtil.getRoot(request));
	    	String tree = CatTreeService.getInstance().getManagerCatTree(cats, catID, catType,false);
	    	tree = tree.replace("../e5cat/CatTreeView", "CatTree");
			
			response.setContentType("text/xml; charset=UTF-8");
			try {
				PrintWriter out = response.getWriter();
				out.write(tree);
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
	}
	
	/**
	 * 读站点根分类
	 */
	private int getSiteCatID(String tenantCode, int catTypeID, int siteID) {
		int rootID = InfoHelper.getSiteCatID(tenantCode, catTypeID, siteID);
		if (rootID == -1) {
			//若没找到，则创建站点根分类
			Category rootSiteCat = InfoHelper.createSiteCat(tenantCode, catTypeID, siteID);
			return rootSiteCat.getCatID();
		} else {
			return rootID;
		}
	}

	private String assembleOneType(CatType catType, int siteID, int rootCatID) {
		String url = "CatTree.do?catType=" + catType.getCatType() + "&amp;catID=" + rootCatID;
		
		StringBuffer sbTree = new StringBuffer(200);
		sbTree.append("<tree text=\"").append(HtmlEncoder.encode(catType.getName()))
			.append("\" src=\"").append(url)
			.append("\" catType=\"").append(catType.getCatType())
			.append("\" catID=\"").append(rootCatID)
			.append("\" oncontextmenu=\"popmenu(2, this); return false;\"")
			;
		sbTree.append(" icon=\"../../images/blue_folder.gif\"")
			.append(" openIcon=\"../../images/blue_folderopen.gif\"")	
			.append(" fileIcon=\"../../images/blue_folder.gif\"")
			.append("/>");
		return sbTree.toString();
	}
}
