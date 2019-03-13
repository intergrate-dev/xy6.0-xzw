package com.founder.xy.column.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.founder.e5.cat.Category;
import com.founder.e5.commons.Pair;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.flow.Flow;
import com.founder.e5.flow.FlowManager;
import com.founder.e5.permission.Permission;
import com.founder.e5.permission.PermissionReader;
import com.founder.e5.sys.org.User;
import com.founder.e5.sys.org.UserReader;
import com.founder.e5.web.SysUser;
import com.founder.e5.web.WebUtil;
import com.founder.e5.workspace.ProcHelper;
import com.founder.e5.workspace.app.form.FormSaver;
import com.founder.e5.workspace.app.form.LogHelper;
import com.founder.xy.column.Column;
import com.founder.xy.column.ColumnManager;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.commons.web.GroupManager;
import com.founder.xy.jms.PublishTrigger;
import com.founder.xy.set.web.AbstractResourcer;
import com.founder.xy.set.web.ResDir;
import com.founder.xy.system.site.ColumnUser;
import com.founder.xy.system.site.Site;
import com.founder.xy.system.site.SiteUserManager;
import com.founder.xy.system.site.SiteUserReader;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 栏目各功能的集中处理器，包含栏目树、栏目表单提交等功能
 * 
 * @author Gong Lijie
 */
@Controller
@RequestMapping("/xy/column")
public class ColumnController extends AbstractResourcer {

	@Autowired
	private ColumnManager colManager;
	@Autowired
	private SiteUserManager userManager;
	@Autowired
	private GroupManager groupManager;

	/** 栏目树（第一层以及子层），无权限判断 */
	@RequestMapping(value = "Tree.do", params = "parentID")
	public void tree(HttpServletRequest request, HttpServletResponse response,
			@RequestParam int siteID, @RequestParam int parentID) throws Exception {

		ColumnTreeHelper.initIcon(request);
		
		int colLibID = LibHelper.getColumnLibID(request);
		
		Document[] cols = null;
		if (parentID == 0) {
			int channelType = WebUtil.getInt(request, "ch", 0);
			cols = colManager.getRoot(colLibID, siteID, channelType);
		} else {
			cols = colManager.getSub(colLibID, parentID);
		}
		String result = ColumnTreeHelper.jsonTree(cols);

		InfoHelper.outputJson(result, response);
	}

	/** 栏目树（第一层），带管理权限 */
	@RequestMapping(value = "Tree.do", params = "admin")
	public void treeAdmin(HttpServletRequest request, HttpServletResponse response,
			@RequestParam int siteID) throws Exception {

		ColumnTreeHelper.initIcon(request);
		
		int userID = ProcHelper.getUserID(request);
		int channelType = WebUtil.getInt(request, "ch", 0);
		
		int colLibID = LibHelper.getColumnLibID(request);
		
		//若是站点管理员，则可管理所有栏目
		if (isAdmin(userID)) {
			Document[] cols = colManager.getRoot(colLibID, siteID, channelType);
			String result = ColumnTreeHelper.jsonTree(cols);

			InfoHelper.outputJson(result, response);
		} else {
			int roleID = ProcHelper.getRoleID(request);
			Document[] cols = colManager.getAdminColumns(colLibID, userID, siteID,channelType,roleID);
			String result = jsonTreeWithParent(cols);

			InfoHelper.outputJson(result, response);
		}
	}

	/** 栏目树（第一层），带操作权限 */
	@RequestMapping(value = "Tree.do", params = "op")
	public void treeOp(HttpServletRequest request, HttpServletResponse response,
			@RequestParam int siteID) throws Exception {

		ColumnTreeHelper.initIcon(request);
		
		int userID = ProcHelper.getUserID(request);
		int channelType = WebUtil.getInt(request, "ch", 0);
		int colLibID = LibHelper.getColumnLibID(request);
		int roleID = ProcHelper.getRoleID(request);
		//若是站点管理员，则可操作所有栏目
		if (isAdmin(userID)) {
			Document[] cols = colManager.getRoot(colLibID, siteID, channelType);
			String result = ColumnTreeHelper.jsonTree(cols);
			InfoHelper.outputJson(result, response);
		}else {
			Document[] cols = colManager.getOpColumns(colLibID, userID, siteID, channelType, roleID);
			String result = jsonTreeWithParent(cols);
			InfoHelper.outputJson(result, response);
		}
	}

	/** 栏目树查找 */
	@RequestMapping(value = "Find.do")
	public void find(HttpServletRequest request, HttpServletResponse response,
			@RequestParam int siteID, @RequestParam String q) throws Exception {

		int colLibID = LibHelper.getColumnLibID(request);
		
		//TODO：判断权限
		int ch = WebUtil.getInt(request, "ch", 0);
		if (q != null) q = q.trim();
		boolean flag=q.matches("[0-9]+");
		Document[] cols = colManager.find(colLibID, siteID, q, ch,flag);
		String result = ColumnTreeHelper.json(cols);
		InfoHelper.outputJson(result, response);
	}

	/** 栏目树拖放 */
	@RequestMapping(value = "Drag.do")
	public void drag(HttpServletRequest request, HttpServletResponse response,
			@RequestParam int srcID, @RequestParam int destID, @RequestParam String moveType)
			throws Exception {

		int type = ("inner".equals(moveType)) ? 0 : (("prev".equals(moveType)) ? 1 : 2);
		int colLibID = LibHelper.getColumnLibID(request);
		
		String result = "ok";
		try {
			colManager.move(colLibID, srcID, destID, type);
			
			PublishTrigger.column(colLibID, srcID);
		} catch (Exception e) {
			result = e.getLocalizedMessage();
		}

		String position = ("inner".equals(moveType)) ? "之下" : (("prev".equals(moveType)) ? "之前"
				: "之后");
		log(request, colLibID, srcID, "移动", "移动到节点" + destID + position);

		InfoHelper.outputJson(result, response);
	}

	/** 栏目删除 */
	@RequestMapping(value = "Delete.do")
	public void delete(HttpServletRequest request, @RequestParam long colID,
			HttpServletResponse response) throws Exception {
		int type = WebUtil.getInt(request, "type", 0);
		try {
			int colLibID = LibHelper.getColumnLibID(request);
			
			if (type == 1) {
				colManager.outOfDate(colLibID, colID);
				log(request, colLibID, colID, "设为僵尸栏目", null);
			} else {
				colManager.delete(colLibID, colID);
				log(request, colLibID, colID, "删除", null);
			}

			PublishTrigger.column(colLibID, colID);

			InfoHelper.outputText("ok", response);
		} catch (Exception e) {
			InfoHelper.outputText(e.getLocalizedMessage(), response);
		}
	}

	/** 栏目复制前检查下栏目是否有栏目模板,是否有模板组权限，新的模板和栏目的默认名称等 */
	@RequestMapping(value = "CopyInfo.do")
	public ModelAndView copyInfo(HttpServletRequest request, @RequestParam long colID) throws E5Exception {
		int colLibID = LibHelper.getColumnLibID(request);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document oldColumn = docManager.get(colLibID, colID);
		int temDoclibID = LibHelper.getTemplateLibID();

		HashMap<String,Object> model = new HashMap<>();
		int siteID = oldColumn.getInt("col_siteID");

		model.put("newColName","（复制）"+oldColumn.getString("col_name"));
		int col_template_ID = oldColumn.getInt("col_template_ID");

		if(col_template_ID>0){
			Document doc = docManager.get(temDoclibID,oldColumn.getInt("col_template_ID"));
			model.put("webTemGroupID",doc.getInt("t_groupID"));
			model.put("webTem",groupManager.hasGroupPower(request,siteID,doc.getInt("t_groupID")));
			model.put("newWebTemName","（复制）"+doc.getString("t_name"));
		}
		else model.put("webTem",false );


		int col_templatePad_ID = oldColumn.getInt("col_templatePad_ID");
		if(col_templatePad_ID>0){
			Document doc = docManager.get(temDoclibID,oldColumn.getInt("col_templatePad_ID"));
			model.put("padTemGroupID",doc.getInt("t_groupID"));
			model.put("padTem",groupManager.hasGroupPower(request,siteID,doc.getInt("t_groupID")));
			model.put("newPadTemName","（复制）"+doc.getString("t_name"));
		}
		else
			model.put("padTem",false);
		model.put("siteID",StringUtils.isBlank(oldColumn.getString("col_siteID")));
		model.put("temDoclibID",LibHelper.getTemplateLibID());
		return new ModelAndView("/xy/column/ColumnCopy", model);
	}

	/** 栏目复制 */
	@RequestMapping(value = "Copy.do")
	public void copy(HttpServletRequest request, @RequestParam long colID,@RequestParam String colName,
			HttpServletResponse response) throws Exception {
		try {
			int colLibID = LibHelper.getColumnLibID(request);
			Boolean copyTemp = WebUtil.getBoolParam(request,"copyTem",false);
			String newColName = WebUtil.getStringParam(request,"newColName");
			String newTemName[] = {WebUtil.getStringParam(request,"newWebTemName"),WebUtil.getStringParam(request,"newPadTemName")};
			Document newCol = colManager.copy(colLibID, colID,newColName,copyTemp,newTemName);

			//若是复制的根栏目，则需要加权限
			if (newCol.getInt("col_parentID") == 0) {
				int userRelLibID = LibHelper.getUserRelLibID(request);
				userManager.addColumnRelated(userRelLibID, ProcHelper.getUserID(request),
						newCol.getInt("col_siteID"), newCol.getDocID(), newCol.getInt("col_channel"));
				//刷新用户权限缓存
				userManager.cacheUserRelTrigger(userRelLibID);
			}
			//发送发布消息
			PublishTrigger.column(colLibID, newCol.getDocID());

			String detail = colName + "(id=" + colID + ") --> " + newColName + "(id=" + newCol.getDocID() + ")";
			log(request, colLibID, colID, "复制", detail);
			log(request, colLibID, newCol.getDocID(), "复制", detail);

			InfoHelper.outputText("ok", response);
		} catch (Exception e) {
			InfoHelper.outputText(e.getLocalizedMessage(), response);
		}
	}

	/** 已删除栏目恢复 */
	@RequestMapping(value = "Restore.do")
	public void restore(HttpServletRequest request, @RequestParam long colID,
			HttpServletResponse response) throws Exception {
		try {
			int colLibID = LibHelper.getColumnLibID(request);
			
			if (colManager.parentDeleted(colLibID, colID)) {
				InfoHelper.outputText("请选择父栏目进行恢复", response);
				return;
			} else {
				colManager.restore(colLibID, colID);
				
				PublishTrigger.column(colLibID, colID);

				log(request, colLibID, colID, "恢复", null);

				InfoHelper.outputText("ok", response);
			}
		} catch (Exception e) {
			InfoHelper.outputText(e.getLocalizedMessage(), response);
		}
	}

	/** 栏目表单：取来源分组和扩展字段分组 */
	@RequestMapping(value = "Group.do")
	public void group(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int siteID = WebUtil.getInt(request, "siteID", 0);
		if (siteID == 0) return;
		
		String code = WebUtil.get(request, "code");

		Category[] groups = InfoHelper.getCatGroups(request, code, siteID);

		StringBuilder result = new StringBuilder();
		result.append("[");

		//若是稿件扩展字段组，则加一个空组
		if (code.equals("EXTFIELD")) {
			result.append("{\"key\":\"\",\"value\":\"\"}");
		}

		if (groups != null) {
			for (Category group : groups) {
				if (result.length() > 1)
					result.append(",");
				result.append("{\"key\":\"").append(String.valueOf(group.getCatID()))
						.append("\",\"value\":\"")
						.append(InfoHelper.filter4Json(group.getCatName())).append("\"}");
			}
		}
		result.append("]");

		InfoHelper.outputJson(result.toString(), response);
	}

	/** 栏目表单保存。自己处理保存后的写日志、刷新栏目树、自动设置根栏目权限 */
	@RequestMapping(value = "FormSubmit.do")
	public ModelAndView formSubmit(HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> model)
			throws Exception {

		//检查上传图标的文件名合法性
		String filePath0 =  request.getParameter("col_iconBig");
		String filePath1 =  request.getParameter("col_iconSmall");
		if (!isImgFile(filePath0) || !isImgFile(filePath1)){
			model.put("error", "对不起，请上传jpg,gif,png格式");
			return new ModelAndView("/xy/column/Submit", model);
		}
		int colLibID = docLibID(request);
		long docID = docID(request);
		int siteID = WebUtil.getInt(request, "col_siteID", 0);
		
		//检查站点的资源目录是否已配置
		ResDir siteDir = getSiteDirs(filePath0, filePath1, colLibID, docID, siteID);
		if (siteDir.noSiteDir) {
			model.put("error", "请先检查站点的资源目录设置");
			return new ModelAndView("/xy/column/Submit", model);
		}
		
		boolean isNew = docID == 0;
		
		FormSaver formSaver = (FormSaver) Context.getBean(FormSaver.class);

		Pair changed;
		try {
			changed = formSaver.handleChanged(request);
			String colName =request.getParameter("col_name");
			
			//若是栏目，则给父栏目的childCount+1，设置级联ID和级联名称
			docID = Long.parseLong(changed.getKey());
			fill4Form(colLibID, isNew, docID,colName);
		} catch (Exception e) {
			if (isNew && docID > 0) { //新建栏目在加其它数据时异常，则删掉刚新建的栏目
				colManager.undoSave(colLibID, docID);
			}
			model.put("error", e.getLocalizedMessage());
			return new ModelAndView("/xy/column/Submit", model);
		}

		//栏目本身的信息保存完毕后的处理：栏目图标发布到外网、发出消息、写日志、设权限
		afterFormSave(request, filePath0, filePath1, colLibID, docID, 
				siteDir, isNew, changed);

		//返回
		model.put("colID", docID);
		model.put("colName", request.getParameter("col_name"));
		model.put("parentID", request.getParameter("col_parentID"));
		model.put("isNew", isNew);
		model.put("isBat", "false");
		//是否需要定位到新建的栏目节点上
		model.put("needLocation", "true".equals(request.getParameter("needLocation")));

		return new ModelAndView("/xy/column/Submit", model);
	}

	//批量添加子栏目
    @RequestMapping(value = "FormSubmitBat.do")
    public ModelAndView formSubmitBat(HttpServletRequest request, HttpServletResponse response,
                                   Map<String, Object> model)
            throws Exception {

		//检查上传图标的文件名合法性
       String filePath0 =  request.getParameter("col_iconBig");
        String filePath1 =  request.getParameter("col_iconSmall");
        if (!isImgFile(filePath0) || !isImgFile(filePath1)){
            model.put("error", "对不起，请上传jpg,gif,png格式");
            return new ModelAndView("/xy/column/Submit", model);
        }

        int colLibID = docLibID(request);
        long docID = docID(request);
        int siteID = WebUtil.getInt(request, "col_siteID", 0);

        //检查站点的资源目录是否已配置
        ResDir siteDir = getSiteDirs(filePath0, filePath1, colLibID, docID, siteID);
        if (siteDir.noSiteDir) {
            model.put("error", "请先检查站点的资源目录设置");
            return new ModelAndView("/xy/column/Submit", model);
        }

        boolean isNew = docID == 0;

        FormSaver formSaver = (FormSaver) Context.getBean(FormSaver.class);

        Pair changed;
		String [] colNames =  request.getParameter("col_name").split(";");
		for(String colName :colNames) {
			request.setAttribute("col_name", colName);
			try {
				changed = formSaver.handleChanged(request);

				//若是栏目，则给父栏目的childCount+1，设置级联ID和级联名称
				fill4Form(colLibID, isNew, Long.parseLong(changed.getKey()),colName);
			} catch (Exception e) {
				model.put("error", e.getLocalizedMessage());
				return new ModelAndView("/xy/column/Submit", model);
			}

			//发布到外网
			pubAndWriteUrl(siteDir, filePath0, colLibID, docID, "col_iconBig");
			pubAndWriteUrl(siteDir, filePath1, colLibID, docID, "col_iconSmall");

			//写操作日志
			String procName = isNew ? "创建" : "修改";
			docID = Long.parseLong(changed.getKey());
			log(request, colLibID, docID, procName, changed.getStringValue());


			//新建子栏目时，复制父栏目的模板和发布规则等属性
			copyFromParent(colLibID, docID);

			//发送消息给发布服务
			PublishTrigger.column(colLibID, docID);

			//返回
			model.put("colID", docID);
			model.put("colName", colName);
			model.put("parentID", request.getParameter("col_parentID"));
			model.put("isNew", isNew);
			model.put("isBat", "true");
		}
        return new ModelAndView("/xy/column/Submit",model);
    }
	/** 栏目表单中：取出稿件的流程 */
	@RequestMapping(value = "Flows.do")
	public void flows(HttpServletRequest request, HttpServletResponse response) throws Exception {

		int docTypeID = InfoHelper.getArticleTypeID();
		FlowManager flowManager = (FlowManager) Context.getBean(FlowManager.class);
		Flow[] flows = flowManager.getFlows(docTypeID);

		StringBuilder result = new StringBuilder();
		result.append("[");

		for (Flow flow : flows) {
			if (result.length() > 1)
				result.append(",");
			result.append("{\"key\":\"").append(String.valueOf(flow.getID()))
					.append("\",\"value\":\"").append(InfoHelper.filter4Json(flow.getName()))
					.append("\"}");
		}
		result.append("]");

		InfoHelper.outputJson(result.toString(), response);
	}

	/** 栏目管理菜单初始化：读栏目库ID，并取出栏目操作的权限（创建根栏目、按栏目发布、设置扩展属性） */
	@RequestMapping(value = "MenuInit.do")
	public void menuInit(HttpServletRequest request, HttpServletResponse response) throws Exception {
		//若是站点管理员，则可管理所有栏目
		int p = 0;
		int userID = ProcHelper.getUserID(request);
		if (isAdmin(userID)) {
			p = 0;
		} else {
			int roleID = ProcHelper.getRoleID(request);
			int docTypeID = DocTypes.COLUMN.typeID();

			PermissionReader pr = (PermissionReader) Context.getBean(PermissionReader.class);
			Permission perm = pr.get(roleID, "UNFLOW", String.valueOf(docTypeID));
			p = (perm == null) ? 0 : perm.getPermission();
		}

		int colLibID = LibHelper.getColumnLibID(request);

		String result = "{\"docLibID\":\"" + colLibID;
		//按位读权限。E5里非流程操作是反式存储的，也就是无权限的才存为1
		result += "\",\"p0\":\"" + ((p & 1) == 0) //创建根栏目
				+ "\",\"p1\":\"" + ((p & 2) == 0) //设置扩展属性
				+ "\",\"p2\":\"" + ((p & 4) == 0) //回收站
				+ "\",\"p3\":\"" + ((p & 8) == 0) //按栏目发布
				+ "\",\"p4\":\"" + ((p & 16) == 0) //设置可操作用户
				+ "\",\"p5\":\"" + ((p & 32) == 0) //设置可管理用户
				+ "\",\"p6\":\"" + ((p & 64) == 0) //删除栏目
				+ "\"}";

		InfoHelper.outputJson(result, response);
	}

	// 列出所有有操作或管理权限用户
	@RequestMapping(value = "InitColumnUser.do")
	public ModelAndView initColumnUser(HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Map<String, Object> model = new HashMap<String, Object>();
		// 栏目ID
		String colID = request.getParameter("colID");
		// 站点ID
		String siteID = request.getParameter("siteID");
		// 权限种别
		String roleType = request.getParameter("roleType");

		int userRelLibID = LibHelper.getUserRelLibID(request);
		List<ColumnUser> userInfoList = userManager.getColumnUserInfoList(userRelLibID, colID, siteID, roleType);

		model.put("userInfoList", userInfoList);
		model.put("colID", colID);
		model.put("siteID", siteID);
		model.put("roleType", roleType);

		return new ModelAndView("/xy/column/ColumnUser", model);
	}

	// 删除操作或管理权限用户
	@RequestMapping(value = "DelColumnUser.do")
	public ModelAndView delColumnUser(HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Map<String, Object> model = new HashMap<String, Object>();
		// 用户sysId
		String sysId = request.getParameter("sysId").substring(1); // 删除第一个逗号
		// 防止sql注入攻击
		sysId = StringUtils.join(StringUtils.getLongArray(sysId, ","));
		// 栏目ID
		String colID = request.getParameter("colID");
		// 站点ID
		String siteID = request.getParameter("siteID");
		// 权限种别
		String roleType = request.getParameter("roleType");

		// 删除有权限用户
		int userRelLibID = LibHelper.getUserRelLibID(request);
		userManager.delColumnUser(userRelLibID, sysId, colID, siteID, roleType);
		// 再检索
		List<ColumnUser> userInfoList = userManager.getColumnUserInfoList(userRelLibID, colID, siteID, roleType);

		model.put("userInfoList", userInfoList);
		model.put("colID", colID);
		model.put("siteID", siteID);
		model.put("roleType", roleType);

		return new ModelAndView("/xy/column/ColumnUser", model);
	}

	// 列出所有没有操作或管理权限用户
	@RequestMapping(value = "InitAddColumnUser.do")
	public ModelAndView initAddColumnUser(HttpServletRequest request, HttpServletResponse response)
			throws E5Exception {

		Map<String, Object> model = new HashMap<String, Object>();
		// 栏目ID
		String colID = request.getParameter("colID");
		// 站点ID
		String siteID = request.getParameter("siteID");
		// 权限种别
		String roleType = request.getParameter("roleType");

		int userRelLibID = LibHelper.getUserRelLibID(request);
		List<ColumnUser> userInfoList = userManager.getNotColumnUserInfoList(userRelLibID, colID, siteID,
				roleType);

		model.put("userInfoList", userInfoList);
		model.put("colID", colID);
		model.put("siteID", siteID);
		model.put("roleType", roleType);

		return new ModelAndView("/xy/column/AddColumnUser", model);
	}

	// 增加操作权限用户
	@RequestMapping(value = "AddColumnUser.do")
	public String addColumnUser(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// 用户sysId
		String sysId = request.getParameter("sysId").substring(1); // 删除第一个逗号
		// 栏目ID
		String colID = request.getParameter("colID");
		// 站点ID
		String siteID = request.getParameter("siteID");
		// 权限种别
		String roleType = request.getParameter("roleType");

		// 新增有权限用户
		int userRelLibID = LibHelper.getUserRelLibID(request);
		userManager.addColumnUser(userRelLibID, sysId, colID, siteID, roleType);
		
		return "redirect:InitColumnUser.do?colID=" + colID + "&siteID=" + siteID + "&roleType="
				+ roleType;
	}
	
	//同步到子孙栏目
	@RequestMapping(value = "Sync.do")
	public void syncChildren(HttpServletRequest request, HttpServletResponse response) {
		int colLibID = WebUtil.getInt(request, "DocLibID", 0);
		long colID = WebUtil.getLong(request, "DocIDs", 0);
		String detail = "同步";
		int syncType = WebUtil.getInt(request, "SyncType", 0);
		//站点发布规则
		int siterule = WebUtil.getInt(request, "siterule", 0);
		detail +=siterule==0?"":"站点发布规则，";
		//栏目模板
		int columnpl = WebUtil.getInt(request, "columnpl", 0);
		detail +=columnpl==0?"":"栏目模板，";
		//稿件模板
		int articlepl = WebUtil.getInt(request, "articlepl", 0);
		detail +=articlepl==0?"":"稿件模板，";
		//组图模板
		int picpl = WebUtil.getInt(request, "picpl", 0);
		detail +=picpl==0?"":"组图模板，";
		//视频模板
		int videopl = WebUtil.getInt(request, "videopl", 0);
		detail +=videopl==0?"":"视频模板，";
		//是否在导航栏显示
		int isShowInNav = WebUtil.getInt(request, "isShowInNav", 0);
		detail +=isShowInNav==0?"":"是否在导航栏显示，";
		//栏目类型
		int columntype = WebUtil.getInt(request, "columntype", 0);
		detail +=columntype==0?"":"栏目类型，";
		//栏目样式
		int columnstyle = WebUtil.getInt(request, "columnstyle", 0);
		detail +=columnstyle==0?"":"栏目样式，";
		//栏目头条个数
		int columntopcount = WebUtil.getInt(request, "columntopcount", 0);
		detail +=columntopcount==0?"":"栏目头条个数，";
		//所属流程
		int process = WebUtil.getInt(request, "process", 0);
		detail +=process==0?"":"所属流程，";
		//来源组
		int sourcegroup = WebUtil.getInt(request, "sourcegroup", 0);
		detail +=sourcegroup==0?"":"来源组 ";
		//检索别名
		int searchName = WebUtil.getInt(request, "searchName", 0);
		detail +=searchName==0?"":"检索别名 ";

		detail +="到子孙栏目";
		try {
			colManager.syncChildren(colLibID, colID, syncType, 
					siterule, columnpl, articlepl, picpl, videopl, isShowInNav, columntype, 
					columnstyle, columntopcount, process, sourcegroup, searchName);
			PublishTrigger.columnSync(colLibID, colID);
			//日志
			log(request, colLibID, colID, "同步到子孙栏目", detail);
			InfoHelper.outputText("ok", response);
		} catch (Exception e) {
			InfoHelper.outputText(e.getLocalizedMessage(), response);
		}
	}
	/**
	 * 栏目名称查重（扩展E5平台的查重）
	 * 返回值：重复，返回1；不重复，返回0
	 */
	@RequestMapping(value = "Duplicate.do")
	public void duplicate(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		long docID = WebUtil.getLong(request, "DocIDs", 0);
		long parentID = WebUtil.getLong(request, "parentID", 0);
		int siteID = WebUtil.getInt(request, "siteID", 0);
		int ch = WebUtil.getInt(request, "ch", 0);
		String value = WebUtil.get(request, "value");

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] docs = null;
		
		String sql = "col_name=? and col_parentID=? and col_channel=? and col_siteID=? and SYS_DOCUMENTID<>?";
		docs = docManager.find(docLibID, sql, new Object[] { value, parentID, ch, siteID, docID });

		String result = (docs == null || docs.length == 0) ? "0" : "1";
		InfoHelper.outputText(result, response);
	}
	
	/**
	 * 保存前检查：栏目的发布规则是否可能造成发布覆盖
	 * 返回值：重复，返回>0；不重复，返回0
	 */
	@RequestMapping(value = "CheckRule.do")
	public void checkPubRule(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		long docID = WebUtil.getLong(request, "DocIDs", 0);
		int ruleID = WebUtil.getInt(request, "ruleID", 0);
		int type = WebUtil.getInt(request, "type", 0);
		String fileName = WebUtil.get(request, "fileName");

		//若发布规则ID相同，则网站文章和触屏文章会相互覆盖，不允许
		int result = sameRuleSelf(docLibID, docID, ruleID, type, fileName);
		
		//若指定了栏目名，则检查是否有其它同发布规则、同栏目名的存在
		if (result == 0 && !StringUtils.isBlank(fileName)) {
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			Document[] docs = null;
			
			String sql = (type == 0)
					? "col_fileName=? and col_pubRule_ID=? and SYS_DOCUMENTID<>?"
					: "col_fileNamePad=? and col_pubRulePad_ID=? and SYS_DOCUMENTID<>?";
			docs = docManager.find(docLibID, sql, new Object[] { fileName, ruleID, docID });
			result = (docs == null || docs.length == 0) ? 0 : 4;
		}
		InfoHelper.outputText(String.valueOf(result), response);
	}
	/**
	 * 栏目聚合
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws E5Exception
	 */
	@SuppressWarnings({"unused"})
	@RequestMapping(value = "initColumnAggregate.do")
	public String initColumnAggregate(HttpServletRequest request, HttpServletResponse response,
			Model model) throws E5Exception {
		//获取参数列表
		String DocLibID = request.getParameter("DocLibID");
		String DocIDs = request.getParameter("DocIDs");
		String siteID = request.getParameter("siteID");
		int colLibID = LibHelper.getColumnLibID(request);
		
		//传到前台
		model.addAttribute("type", "admin");
		model.addAttribute("DocIDs", DocIDs);
		model.addAttribute("DocLibID", DocLibID);
		model.addAttribute("siteID", siteID);
		model.addAttribute("ch", request.getParameter("ch")); //栏目的渠道
	
		return "/xy/column/ColumnAggregate";
	}
     
	
	/**
	 * 获取该栏目已经聚合的栏目
	 * 
	 * @param request
	 * @param response
	 * 
	 * @throws E5Exception
	 */
	@RequestMapping(value = "selectedColumnAggregate.do")
	@ResponseBody
	public void selectedColumnAggregate(HttpServletRequest request, HttpServletResponse response) throws E5Exception {
		//获取参数列表
		String DocIDs = request.getParameter("DocIDs");
		int colLibID = LibHelper.getColumnLibID(request);
				
		//获取栏目表的id
		String conditions = "SYS_DOCUMENTID=? and SYS_DELETEFLAG=0";
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] cols = docManager.find(colLibID, conditions, new Object[] {Long.parseLong(DocIDs) });
		JSONObject json = new JSONObject();
		if (cols != null && cols.length > 0 && !cols.equals("")) {
			String col_aggregateIDs = cols[0].getString("col_aggregateIDs");
			if (col_aggregateIDs != null && !col_aggregateIDs.equals("")) {
				long[] aggColIDs = StringUtils.getLongArray(col_aggregateIDs, ",");
				for (long aggColID : aggColIDs){
					Document col = docManager.get(colLibID, aggColID);
					int siteID = col.getInt("col_siteID"); 
					String oldIDs = json.optString("site"+siteID,null);
					if(StringUtils.isBlank(oldIDs)){
						 json.put("site"+siteID,""+aggColID);
					}else{
						json.put("site"+siteID,oldIDs+","+aggColID);
					}
				}
				
			}
		}
		
		InfoHelper.outputJson(json.toString(), response);	
	}
	/**
	 * 提交修改后的聚合结果
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("updateColumnAggregate.do")
	public void updateColumnAggregate(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		//获取column_id 和新聚合的id
		int colID = Integer.parseInt(request.getParameter("docIDs"));
		String newIds = request.getParameter("newIds");
		int colLibID = LibHelper.getColumnLibID(request);
		
		//去掉自身
		newIds = InfoHelper.removeItemsFromIds(newIds, ",", new String[]{"", colID +"" });
		colManager.changeAggregateIDs(colLibID, colID, newIds);
		
		PublishTrigger.columnRefresh(colLibID, colID);
		
		InfoHelper.outputText("success", response);
	}
	
	/**
	 * 栏目自动推送
	 */
	@RequestMapping(value = "initColumnPush.do")
	public String initColumnPush(HttpServletRequest request, HttpServletResponse response, Model model) throws E5Exception {
		String DocLibID = request.getParameter("DocLibID");
		String DocIDs = request.getParameter("DocIDs");
		String siteID = request.getParameter("siteID");
		int colLibID = LibHelper.getColumnLibID(request);
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] documents = docManager.find(colLibID, "SYS_DOCUMENTID=? and SYS_DELETEFLAG=0",new Object[] { Long.parseLong(DocIDs) });
		if (documents != null && documents.length > 0) {
			model.addAttribute("ids", documents[0].getLong("col_push_ID"));
		}
		model.addAttribute("type", "radio");
		model.addAttribute("DocIDs", DocIDs);
		model.addAttribute("DocLibID", DocLibID);
		model.addAttribute("siteID", siteID);
		model.addAttribute("ch", request.getParameter("ch")); //栏目的渠道
	
		return "/xy/column/ColumnPush";
	}
	
	/**
	 * 提交修改后的推送结果
	 */
	@RequestMapping("updateColumnPush.do")
	public void updateColumnPush(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int colID = Integer.parseInt(request.getParameter("docIDs"));
		long columnPushID = WebUtil.getLong(request, "newIds", 0);
		int colLibID = LibHelper.getColumnLibID(request);
		
		Document col = colManager.get(colLibID, colID);
		col.set("col_push_ID", columnPushID);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		docManager.save(col);
		
		PublishTrigger.columnRefresh(colLibID, colID);
		InfoHelper.outputText("success", response);
	}
	
	/**
	 * 跨站点栏目聚合
	 * 
	 * 获取站点ID与名称 
	 * 
	 * @throws E5Exception
	 */
	@RequestMapping(value = "ToSiteColumnAggregate.do")
	public void ToSiteColumnAggregate(HttpServletRequest request, HttpServletResponse response) throws E5Exception {

		JSONArray arr = new JSONArray();

		int userLibID = LibHelper.getUserExtLibID(request);
		SysUser sysUser = ProcHelper.getUser(request);
		int userID = sysUser.getUserID();
		SiteUserReader siteUserReader = (SiteUserReader) Context.getBean("siteUserReader");
		List<Site> sites = siteUserReader.getSites(userLibID, userID);

		for (Site site : sites) {
			int siteID = site.getId();
			String siteName = site.getName();

			JSONObject json = new JSONObject();
			json.put("id", siteID);
			json.put("name", siteName);
			arr.add(json);
		}

		InfoHelper.outputJson(arr.toString(), response);

	}

	
	/**
	 * 判断是否有栏目流程权限
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "canPuborApr.do")
	public void canPuborApr(HttpServletRequest request, HttpServletResponse response) throws Exception{

		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		int colID = WebUtil.getInt(request,"colID", 0);

		SysUser sysUser = ProcHelper.getUser(request);
		int roleID = sysUser.getRoleID();

		String canPuborApr = colManager.canPuborApr(docLibID, colID, roleID);

		InfoHelper.outputText(canPuborApr, response);
	}


	/**
	 * 判断一个栏目是否网站版和触屏版会发布覆盖
	 */
	private int sameRuleSelf(int docLibID, long docID, int ruleID, int type, String fileName) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document col = docManager.get(docLibID, docID);
		
		//若发布规则ID相同，则网站文章和触屏文章会相互覆盖，不允许
		int anotherRuleID = (type == 0) ? col.getInt("col_pubRulePad_ID") : col.getInt("col_pubRule_ID");
		if (anotherRuleID == ruleID) {
			return 1;
		} else if (anotherRuleID > 0){
			int ruleLibID = LibHelper.getLibIDByOtherLib(DocTypes.SITERULE.typeID(), docLibID);
			Document rule = docManager.get(ruleLibID, ruleID);
			Document anotherRule = docManager.get(ruleLibID, anotherRuleID);
			
			//若发布规则不同，但各规则里设置的稿件发布目录相同，且“是否按日期做目录”也相同
			if (rule.getInt("rule_article_dir_ID") == anotherRule.getInt("rule_article_dir_ID")
					&& rule.getInt("rule_article_date") == anotherRule.getInt("rule_article_date")
					) {
				return 2;
			}
			//判断栏目。若栏目名一样，检查是否会发布到同一个目录里
			String anotherFileName = (type == 0) ? col.getString("col_fileName") : col.getString("col_fileNamePad");
			if (fileName.equals(anotherFileName)
					&& rule.getInt("rule_column_dir_ID") == anotherRule.getInt("rule_column_dir_ID")
					&& rule.getInt("rule_column_date") == anotherRule.getInt("rule_column_date")
					) {
				return 3;
			}
		}
		return 0;
	}
	
	private void fill4Form(int colLibID, boolean isNew, long colID, String colName) throws E5Exception{
		colManager.fill4Form(colLibID, isNew, colID,colName);
	}

	private void afterFormSave(HttpServletRequest request, String filePath0,
			String filePath1, int colLibID, long docID, ResDir siteDir,
			boolean isNew, Pair changed) throws E5Exception {
		//发布到外网
		pubAndWriteUrl(siteDir, filePath0, colLibID, docID, "col_iconBig");
		pubAndWriteUrl(siteDir, filePath1, colLibID, docID, "col_iconSmall");

		//清空第三方使用的WEB栏目列表缓存
		int siteID = WebUtil.getInt(request, "col_siteID", 0);

        int channel = WebUtil.getInt(request, "col_channel", 0);
        if(channel==0){
            clearWebColumnListKey(siteID,docID,colLibID);
        }

		//写操作日志
		String procName = isNew ? "创建" : "修改";
		log(request, colLibID, docID, procName, changed.getStringValue());
	
		//若是增加了根栏目，则设置权限
		if (isNew) {
			int parentID = WebUtil.getInt(request, "col_parentID", 0);
			if (parentID == 0) {
				int channelType = WebUtil.getInt(request, "col_channel", 0);
				int userRelLibID = LibHelper.getUserRelLibID(request);
				userManager.addColumnRelated(userRelLibID, ProcHelper.getUserID(request),
						WebUtil.getInt(request, "col_siteID", 0), docID, channelType);
	
				//刷新用户权限缓存
				userManager.cacheUserRelTrigger(userRelLibID);
			} else {
				//新建子栏目时，复制父栏目的模板和发布规则等属性
				copyFromParent(colLibID, docID);
			}
		}
		//发送消息给发布服务
		PublishTrigger.column(colLibID, docID);
	}

	//带父节点（无权限）的栏目树的json
	private String jsonTreeWithParent(Document[] cols) throws E5Exception {
		List<Column> roots = getRoots(cols);

		return ColumnTreeHelper.jsonTreeWithParent(roots);
	}

	//根据指定的栏目，得到从根栏目开始的栏目树对象
	private List<Column> getRoots(Document[] cols) throws E5Exception {
		if (cols == null)
			return null;

		//保证顺序
		Map<Integer, Column> tree = new HashMap<Integer, Column>();

		for (Document col : cols) {
			//把无权限的父节点也带上
			int[] path = StringUtils.getIntArray(col.getString("col_cascadeID"), "~");
			Column pCol = tree.get(path[0]);
			if (pCol == null) {
				pCol = getCol(col.getDocLibID(), path[0]);
				tree.put(path[0], pCol);
			} else if (pCol.isEnable()) {
				//栏目拖动会造成栏目的父子结构发生变化。若发现父栏目已经有权限，则不必加子栏目
				continue;
			}

			if (path.length == 1) {
				pCol.setEnable(true);
				pCol.removeChildren(); //节点设置为enable后就可动态展开，不需要设置children
				continue;
			}

			Column parent = pCol;
			for (int i = 1; i < path.length; i++) {
				Column son = parent.getChild(path[i]);
				if (son == null) {
					son = getCol(col.getDocLibID(), path[i]);
					parent.addChild(son);
				} else if (son.isEnable()) {
					break;
				}
				//最后一级的栏目，是确实有权限的，所以enable=true
				if (i == path.length - 1) {
					son.setEnable(true);
				}
				parent = son;
			}
		}
		return ColumnTreeHelper.sortColByOrder(tree);
	}

	//根据栏目ID得到Col对象
	private Column getCol(int colLibID, long id) throws E5Exception {
		boolean enable = false;

		Document parent = colManager.get(colLibID, id);
		return new Column(id, parent.getString("col_name"), 
				parent.getString("col_cascadeID"), parent.getString("col_cascadeName"),
				enable, parent.getInt("col_childCount") > 0, 
				parent.getInt("col_displayOrder"), parent.getInt("col_status") > 0);
	}

	private void log(HttpServletRequest request, int colLibID, long docID, String procName, String detail) {

		SysUser user = ProcHelper.getUser(request);

		LogHelper.writeLog(colLibID, docID, user, procName, detail);
	}

	/**
	 * 判断当前用户是否管理员。
	 * @param userID
	 * @return
	 */
	private boolean isAdmin(int userID) {
		UserReader userReader = (UserReader)Context.getBean(UserReader.class);
		try {
			User user = userReader.getUserByID(userID);
			return "1".equals(user.getProperty2());
		} catch (E5Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 若需要提交文件，先检查站点的资源文件目录是否已设置
	 */
	private ResDir getSiteDirs(String filePath0, String filePath1, int docLibID, long docID, int siteID) throws E5Exception {
		ResDir result = new ResDir();
		//修改时若改变了头像才发布
		if (!StringUtils.isBlank(filePath0) || !StringUtils.isBlank(filePath1)) {
			
			String oldIcon0 = null;
			String oldIcon1 = null;
			if (docID > 0) {
				DocumentManager docManager = DocumentManagerFactory.getInstance();
				Document doc = docManager.get(docLibID, docID);
				oldIcon0 = doc.getString("col_iconBig");
				oldIcon1 = doc.getString("col_iconSmall");
				
				siteID = doc.getInt("col_siteID"); //不是新建的表单里没有siteID，所以要新取一次
				
				result.ownerDoc = doc;
			}
			//若大图标或小图标有变化，则需上传
			if (!StringUtils.isBlank(filePath0) && !filePath0.equals(oldIcon0)
					|| !StringUtils.isBlank(filePath1) && !filePath1.equals(oldIcon1)) {
				String[] dirs = readSiteInfo(siteID);
				result.noSiteDir = (StringUtils.isBlank(dirs[0]) || StringUtils.isBlank(dirs[1]) || StringUtils.isBlank(dirs[2]));
				result.dirs = dirs;
			}
		}
		return result;
	}

	/**
	 * 新建栏目时，若栏目不是根栏目，则复制父的属性
	 * @param docLibID
	 * @param docID
	 */
	private void copyFromParent(int docLibID, long docID) {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			Document doc = docManager.get(docLibID, docID);
			long parentID = doc.getLong("col_parentID");
			if (parentID > 0) {
				Document parent = docManager.get(docLibID, parentID);
				doc.setDeleteFlag(parent.getDeleteFlag());
				doc.set("col_template", parent.get("col_template"));
				doc.set("col_template_ID", parent.get("col_template_ID"));
				doc.set("col_templateArticle", parent.get("col_templateArticle"));
				doc.set("col_templateArticle_ID", parent.get("col_templateArticle_ID"));
				doc.set("col_templatePic", parent.get("col_templatePic"));
				doc.set("col_templatePic_ID", parent.get("col_templatePic_ID"));
				doc.set("col_templateVideo", parent.get("col_templateVideo"));
				doc.set("col_templateVideo_ID", parent.get("col_templateVideo_ID"));
				doc.set("col_pubRule", parent.get("col_pubRule"));
				doc.set("col_pubRule_ID", parent.get("col_pubRule_ID"));
				doc.set("col_templatePad", parent.get("col_templatePad"));
				doc.set("col_templatePad_ID", parent.get("col_templatePad_ID"));
				doc.set("col_templateArticlePad", parent.get("col_templateArticlePad"));
				doc.set("col_templateArticlePad_ID", parent.get("col_templateArticlePad_ID"));
				doc.set("col_templatePicPad", parent.get("col_templatePicPad"));
				doc.set("col_templatePicPad_ID", parent.get("col_templatePicPad_ID"));
				doc.set("col_templateVideoPad", parent.get("col_templateVideoPad"));
				doc.set("col_templateVideoPad_ID", parent.get("col_templateVideoPad_ID"));
				doc.set("col_pubRulePad", parent.get("col_pubRulePad"));
				doc.set("col_pubRulePad_ID", parent.get("col_pubRulePad_ID"));
				doc.set("col_appType", parent.get("col_appType"));
				doc.set("col_appType_ID", parent.get("col_appType_ID"));
				doc.set("col_appStyle", parent.get("col_appStyle"));
				doc.set("col_appStyle_ID", parent.get("col_appStyle_ID"));
				doc.set("col_topCount", parent.get("col_topCount"));
				
				docManager.save(doc);
			}
		} catch (E5Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value = "isOpCol.do")
	public void c(HttpServletRequest request, HttpServletResponse response,
			@RequestParam int siteID,@RequestParam long colID) throws Exception {
		int userID = ProcHelper.getUserID(request);
		int roleID = ProcHelper.getRoleID(request);
		int channelType = WebUtil.getInt(request, "ch", 0);
		int colLibID = LibHelper.getColumnLibID(request);
		Document[] ids = colManager.getOpColumns(colLibID, userID, siteID, channelType, roleID);
		String result = "false";
		for(Document id : ids) {
			if(colID == id.getDocID()){
				result = "true";
				break;
			}
		}
		InfoHelper.outputText(result, response);
	}

    //清空第三方使用的WEB栏目列表缓存
	private void clearWebColumnListKey(int siteID, long docID, int colLibID) {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			Document doc = docManager.get(colLibID, docID);
			long parentID = doc.getLong("col_parentID");
			RedisManager.hclear(RedisKey.WEB_COLLISTALL_KEY+siteID, parentID);
			if (parentID > 0) {
                clearWebColumnListKey(siteID,parentID,colLibID);
			}
		} catch (E5Exception e) {
			e.printStackTrace();
		}
	}
}
