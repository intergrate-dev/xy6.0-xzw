package com.founder.xy.commons.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.founder.e5.workspace.ProcHelper;
import com.founder.xy.column.Pinyin4jUtil;
import com.founder.xy.system.site.SiteUserManager;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.founder.e5.cat.CatManager;
import com.founder.e5.cat.Category;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.xy.commons.CatTypes;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import org.springframework.web.bind.annotation.RequestParam;



@Controller
@RequestMapping("/xy/common/group")
public class GroupController {
	@Autowired
	private GroupManager groupManager;
	@Autowired
	private SiteUserManager userManager;
	/**
	 * 添加分组名称
	 * @param request
	 * @param response
	 * @param groupInfo
	 * @throws Exception
	 */
	@RequestMapping(value = "addGroupAjax.do", method = RequestMethod.POST)
	public void addGroup(HttpServletRequest request, HttpServletResponse response,
			GroupInfo groupInfo) throws Exception {
		//获取参数
		int catType = Integer.parseInt(groupInfo.getCategoryTypeId());
		int siteID = Integer.parseInt(groupInfo.getSiteID());
		String newGroupName = groupInfo.getNewGroupName();

		//1. 把当前siteID作为catCode，查找是否存在当前站点作为第一层分类
		CatManager catManager = (CatManager) Context.getBean(CatManager.class);
		Category siteCat = catManager.getCatByCode(catType, groupInfo.getSiteID());
		boolean found = find(catType, newGroupName, 0, siteID);

		//3. 在站点下检查是否有该名字的分组，有则不允许创建
		if (found) {
			groupInfo.getRt_operationResult().element(GroupInfo.RESULT, GroupInfo.HAS_THE_SAME_GROUP);
			groupInfo.getRt_operationResult().element(GroupInfo.MESSAGE, "对不起，已经有该组了！");
		} else {
			//4. 若没有，则创建
			Category cat = new Category();
			try {
				//赋值
				cat.setCatType(catType);
				cat.setParentID(siteCat.getCatID());
				cat.setCatName(newGroupName);

				String pinyin= Pinyin4jUtil.getQuanPin(newGroupName);
				String abbreviation=Pinyin4jUtil.getJianPin(newGroupName);

                cat.setCatCode(pinyin+","+abbreviation);

				//创建
				catManager.createCat(cat);

				groupInfo.getRt_operationResult().element(GroupInfo.RESULT, GroupInfo.SUCCESS);
				groupInfo.getRt_operationResult().element("groupID", cat.getCatID());
				groupInfo.getRt_operationResult().element(GroupInfo.MESSAGE, "创建分组成功！");
				//@wenkx 增加分组权限
				if("是".equals(InfoHelper.getConfig("其它", "是否启用分组权限管理")) &&
						(catType == CatTypes.CAT_SPECIAL.typeID() || catType == CatTypes.CAT_TEMPLATE.typeID() || catType == CatTypes.CAT_PHOTO.typeID())){
					int userID = ProcHelper.getUserID(request);
					int userRelLibID = LibHelper.getUserRelLibID(request);

					userManager.addRelated(userRelLibID, userID, siteID,
							cat.getCatID(),8);
				}
			} catch (Exception e) {
				e.printStackTrace();
				
				groupInfo.getRt_operationResult().element(GroupInfo.RESULT, GroupInfo.FAILURE);
				groupInfo.getRt_operationResult().element(GroupInfo.MESSAGE, "创建分组失败！");
			}
		}

		//ajax返回消息
		InfoHelper.outputJson(groupInfo.getRt_operationResult().toString(), response);
	}

	/**
	 * 修改分组名称
	 * @param request
	 * @param response
	 * @param groupInfo
	 * @throws Exception
	 */
	@RequestMapping(value = "modifyGroupAjax.do", method = RequestMethod.POST)
	public void modifyGroup(HttpServletRequest request, HttpServletResponse response,
			GroupInfo groupInfo) throws Exception {
		//初始化参数
		int catType = Integer.parseInt(groupInfo.getCategoryTypeId());
		int catID = Integer.parseInt(groupInfo.getGroupID());
		int siteID = Integer.parseInt(groupInfo.getSiteID());
		
		CatManager catManager = (CatManager) Context.getBean(CatManager.class);
		
		//在站点下检查是否有该名字的分组，有则不允许创建
		boolean found = find(catType, groupInfo.getNewGroupName(), catID, siteID);

		if (found) {
			groupInfo.getRt_operationResult().element(GroupInfo.RESULT, GroupInfo.FAILURE);
			groupInfo.getRt_operationResult().element(GroupInfo.MESSAGE, "对不起，已经有该分组了！");
		} else {
			Category cat = catManager.getCat(catType, catID);
			//如果有，修改该分组的名称
			if (cat != null) {
				try {
					cat.setCatName(groupInfo.getNewGroupName());
					//提交
					catManager.updateCat(cat);
					
					groupInfo.getRt_operationResult().element(GroupInfo.RESULT, GroupInfo.SUCCESS);
					groupInfo.getRt_operationResult().element(GroupInfo.MESSAGE, "修改分组名称成功！");
				} catch (Exception e) {
					e.printStackTrace();
					
					groupInfo.getRt_operationResult().element(GroupInfo.RESULT, GroupInfo.FAILURE);
					groupInfo.getRt_operationResult().element(GroupInfo.MESSAGE, "修改分组失败！");
				}
			} else {
				//如果没有这个分组，提示没有这个分组
				groupInfo.getRt_operationResult().element(GroupInfo.RESULT, GroupInfo.FAILURE);
				groupInfo.getRt_operationResult().element(GroupInfo.MESSAGE, "未找到该组！");
			}
		}
		InfoHelper.outputJson(groupInfo.getRt_operationResult().toString(), response);
	}
	
	//在站点下检查是否有该名字的分组
	private boolean find(int catTypeID, String name, int catID, int siteID) throws E5Exception {
		CatManager catManager = (CatManager) Context.getBean(CatManager.class);
		Category[] catArray = catManager.getCatsByName(catTypeID, name);
		if (catArray == null)
			return false;
		
		Category siteCat = catManager.getCatByCode(catTypeID, String.valueOf(siteID));
		for (Category cat : catArray) {
			if (cat.getParentID() == siteCat.getCatID()
					&& cat.getCatID() != catID
					&& cat.getCatName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 1. 来源组：在栏目上有挂接。若组下有来源，则不允许删除；对一个来源，删除时检查是否有稿件引用，若有则提示，可继续删除。0
	 * 2. 扩展字段组：在栏目上有挂接。删除时提示哪些栏目挂接了这个扩展字段组，可删除。
	 * 3. 模板组：栏目上有挂接模板。组下有模板，就不能删；模板删除时，显示引用此模板的栏目，有则不允许删除。
	 * 4. 公共资源：组下有公共资源，就不能删；单个可删。 0
	 * 5. 页面区块组：组下有，就不删；页面区块可删，删除的同时去掉区块的内容数据。0
	 * @param request
	 * @param response
	 * @param groupInfo
	 * @throws Exception
	 */

	//分发
	@RequestMapping(value = "deleteGroupAjax.do", method = RequestMethod.POST)
	public void deleteGroup(HttpServletRequest request, HttpServletResponse response,
			GroupInfo groupInfo) throws Exception {
		//1. 来源组：在栏目上有挂接。若组下有来源，则不允许删除；对一个来源，删除时检查是否有稿件引用，若有则提示，可继续删除。
		//如果是 来源， 若组下有来源，则不允许删除；
		if (groupInfo.getCategoryTypeId() != null
				&& groupInfo.getCategoryTypeId().trim().equals(CatTypes.CAT_SOURCE.typeID() + "")) {
			//deleteSourceGroup(parameters, response);
			int libID = LibHelper.getLibIDByOtherLib(DocTypes.SOURCE.typeID(), Integer.parseInt(groupInfo.getCategoryTypeId()));
			checkGroupItems(groupInfo, response, libID,
					DocTypes.SOURCE.groupField());
		}
		//3. 模板组：栏目上有挂接模板。组下有模板，就不能删；模板删除时，显示引用此模板的栏目，有则不允许删除。
		if (groupInfo.getCategoryTypeId() != null
				&& groupInfo.getCategoryTypeId().trim()
						.equals(CatTypes.CAT_TEMPLATE.typeID() + "")) {
			//deleteSourceGroup(parameters, response);
			int libID = LibHelper.getLibIDByOtherLib(DocTypes.TEMPLATE.typeID(), Integer.parseInt(groupInfo.getCategoryTypeId()));
			checkGroupItems(groupInfo, response, libID,
					DocTypes.TEMPLATE.groupField());
		}
		//4. 公共资源：组下有公共资源，就不能删；单个可删。
		else if (groupInfo.getCategoryTypeId() != null
				&& groupInfo.getCategoryTypeId().trim()
						.equals(CatTypes.CAT_RESOURCE.typeID() + "")) {
			int libID = LibHelper.getLibIDByOtherLib(DocTypes.RESOURCE.typeID(), Integer.parseInt(groupInfo.getCategoryTypeId()));
			checkGroupItems(groupInfo, response, libID,
					DocTypes.RESOURCE.groupField());
		}
		//5. 页面区块组：组下有，就不删；页面区块可删，删除的同时去掉区块的内容数据。
		else if (groupInfo.getCategoryTypeId() != null
				&& groupInfo.getCategoryTypeId().trim().equals(CatTypes.CAT_BLOCK.typeID() + "")) {
			int libID = LibHelper.getLibIDByOtherLib(DocTypes.BLOCK.typeID(), Integer.parseInt(groupInfo.getCategoryTypeId()));
			checkGroupItems(groupInfo, response, libID,
					DocTypes.BLOCK.groupField());
		}
		//2. 扩展字段组：在栏目上有挂接。删除时提示哪些栏目挂接了这个扩展字段组，可删除。
		else if (groupInfo.getCategoryTypeId() != null
				&& groupInfo.getCategoryTypeId().trim()
						.equals(CatTypes.CAT_EXTFIELD.typeID() + "")) {
			//可删除
			//deleteExtFieldGroup(groupInfo, response);
			//不可删除
			checkGroupItems(groupInfo, response, LibHelper.getColumnLibID(request),
					"col_extField_ID");
		}
		else if (groupInfo.getCategoryTypeId() != null
				&& groupInfo.getCategoryTypeId().trim().equals(CatTypes.CAT_PHOTO.typeID() + "")) {
			int libID = LibHelper.getLibIDByOtherLib(DocTypes.PHOTO.typeID(), Integer.parseInt(groupInfo.getCategoryTypeId()));
			checkGroupItems(groupInfo, response, libID,
					DocTypes.PHOTO.groupField());
		}
		else if (groupInfo.getCategoryTypeId() != null
				&& groupInfo.getCategoryTypeId().trim().equals(CatTypes.CAT_VIDEO.typeID() + "")) {
			int libID = LibHelper.getLibIDByOtherLib(DocTypes.VIDEO.typeID(), Integer.parseInt(groupInfo.getCategoryTypeId()));
			checkGroupItems(groupInfo, response, libID,
					DocTypes.VIDEO.groupField());
		}
		else if (groupInfo.getCategoryTypeId() != null
				&& groupInfo.getCategoryTypeId().trim().equals(CatTypes.CAT_VOTE.typeID() + "")) {
			int libID = LibHelper.getLibIDByOtherLib(DocTypes.VOTE.typeID(), Integer.parseInt(groupInfo.getCategoryTypeId()));
			checkGroupItems(groupInfo, response, libID,
					DocTypes.VOTE.groupField());
		}else if (groupInfo.getCategoryTypeId() != null
				&& groupInfo.getCategoryTypeId().trim().equals(CatTypes.CAT_SPECIAL.typeID() + "")) {
			int libID = LibHelper.getLibIDByOtherLib(DocTypes.SPECIAL.typeID(), Integer.parseInt(groupInfo.getCategoryTypeId()));
			checkGroupItems(groupInfo, response, libID,
					DocTypes.SPECIAL.groupField());
		}
		else {
			groupInfo.getRt_operationResult().element(GroupInfo.RESULT, GroupInfo.FAILURE);
			groupInfo.getRt_operationResult().element(GroupInfo.MESSAGE, "对不起，无法分辨该组的类型！无法删除！");
			InfoHelper.outputJson(groupInfo.getRt_operationResult().toString(), response);
		}
	}

	/**
	 * 执行删除操作
	 * @param groupInfo
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "doDeleteGroupAjax.do", method = RequestMethod.POST)
	public void deleteGroupDao(GroupInfo groupInfo, HttpServletResponse response)
			throws Exception {
		//如果参数不为空，进行 删除操作;
		if (groupInfo != null && groupInfo.getGroupID() != null
				&& groupInfo.getCategoryTypeId() != null
				&& !groupInfo.getGroupID().trim().isEmpty()
				&& !groupInfo.getCategoryTypeId().trim().isEmpty()) {

			boolean deleteCategoryResult = false;
			//若没有，删除
			try {
				CatManager catManager = (CatManager) Context.getBean(CatManager.class);
				catManager.deleteCat(Integer.parseInt(groupInfo.getCategoryTypeId()),
						Integer.parseInt(groupInfo.getGroupID()));
				deleteCategoryResult = true;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				//添加日志
				if (deleteCategoryResult) {
					groupInfo.getRt_operationResult().element(GroupInfo.RESULT, GroupInfo.SUCCESS);
					groupInfo.getRt_operationResult().element(GroupInfo.MESSAGE, "删除分组成功！");
				} else {
					groupInfo.getRt_operationResult().element(GroupInfo.RESULT, GroupInfo.FAILURE);
					groupInfo.getRt_operationResult().element(GroupInfo.MESSAGE, "删除分组失败！");
				}
			}
		} else {
			groupInfo.getRt_operationResult().element(GroupInfo.RESULT, GroupInfo.FAILURE);
			groupInfo.getRt_operationResult().element(GroupInfo.MESSAGE,
					"参数为空，无法删除！具体：groupID:" + groupInfo.getGroupID() + "; categoryID:"
							+ groupInfo.getCategoryTypeId() + ";");
		}
		InfoHelper.outputJson(groupInfo.getRt_operationResult().toString(), response);
	}

	/**
	 * //若组下有来源，则不允许删除；
	 * @param parameters
	 * @param response
	 * @throws Exception
	 */
	private void checkGroupItems(GroupInfo parameters, HttpServletResponse response,
			int docLibID, String groupField) throws Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] cols = docManager.find(docLibID, groupField + "=? and SYS_DELETEFLAG=0",
				new Object[] { parameters.getGroupID() });
		//若有，不删除
		if (cols != null && cols.length > 0) {
			parameters.getRt_operationResult().element(GroupInfo.RESULT,
					GroupInfo.SUCCESS);
			parameters.getRt_operationResult().element(GroupInfo.OPERATION,
					GroupInfo.GROUP_IS_RELATED_TO_OTHER_DATA);
			parameters.getRt_operationResult().element(GroupInfo.MESSAGE,
					"对不起，该分组已经关联到了其他资源，不能被删除！");
		} else {
			parameters.getRt_operationResult().element(GroupInfo.RESULT,
					GroupInfo.SUCCESS);
			parameters.getRt_operationResult().element(GroupInfo.OPERATION,
					GroupInfo.DELETE_OPERATION);
			parameters.getRt_operationResult().element(GroupInfo.MESSAGE, "您确定要删除该组吗？");
		}
		InfoHelper.outputJson(parameters.getRt_operationResult().toString(), response);
	}

	/**
	 * 扩展字段组：在栏目上有挂接。删除时提示哪些栏目挂接了这个扩展字段组，可删除。
	 * 先取出来挂接了这些扩展字段的栏目列表，发回给前台，然后让用户确定是否要删除；
	 * @param parameters
	 * @param response
	 */
	@SuppressWarnings("unused")
	private void deleteExtFieldGroup(HttpServletRequest request, GroupInfo parameters, HttpServletResponse response)
			throws Exception {
		//1. 查找挂接到扩展字段组栏目的list
		int docLibID = LibHelper.getColumnLibID(request);
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] cols = docManager
				.find(docLibID, "col_siteID=? and SYS_DELETEFLAG=0 and col_extField_ID=?",
						new Object[] { parameters.getSiteID(), parameters.getGroupID() });
		//如果有挂接的栏目，那返回给用户，让用户选择是否删除，并且把删除的url传回去
		if (cols != null && cols.length > 0) {
			StringBuilder columnNameListStr = new StringBuilder();
			for (Document col : cols) {
				columnNameListStr.append(",【" + col.get("col_name") + "】");
			}
			//传回去
			parameters.getRt_operationResult().element(GroupInfo.RESULT,
					GroupInfo.SUCCESS);
			parameters.getRt_operationResult().element(GroupInfo.OPERATION,
					GroupInfo.COMFIRM_DELETE);
			parameters.getRt_operationResult().element(GroupInfo.MESSAGE,
					"该扩展字段关联了以下栏目： " + columnNameListStr.substring(1).toString() + "；您确定要删除该组吗？");
		} else {
			parameters.getRt_operationResult().element(GroupInfo.RESULT,
					GroupInfo.SUCCESS);
			parameters.getRt_operationResult().element(GroupInfo.OPERATION,
					GroupInfo.DELETE_OPERATION);
			parameters.getRt_operationResult().element(GroupInfo.MESSAGE, "您确定要删除该组吗？");
		}
		InfoHelper.outputJson(parameters.getRt_operationResult().toString(), response);
	}

	//获取ztreeJSON格式的分组
	@RequestMapping(value = "getGroup.do", method = RequestMethod.POST)
	public void getGroup(HttpServletRequest request, HttpServletResponse response,
						 @RequestParam int siteID){
		//根据传入参数取得对应组数据
			JSONArray groupsJSON = groupManager.getJSONArr(request,siteID);
			InfoHelper.outputJson(groupsJSON.toString(), response);

	}
	/** 分组查找 */
	@RequestMapping(value = "Find.do")
	public void find(HttpServletRequest request, HttpServletResponse response,
					 @RequestParam (defaultValue = "1")int siteID,@RequestParam int catTypeID, @RequestParam String q) throws Exception {

		String result =groupManager.specialFind(siteID,catTypeID,q);
		InfoHelper.outputJson(result, response);
	}

}
