package com.founder.xy.article;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.founder.e5.cat.CatManager;
import com.founder.e5.cat.Category;
import com.founder.e5.context.Context;
import com.founder.e5.web.SysUser;
import com.founder.e5.workspace.app.form.LogHelper;
import com.founder.xy.commons.CatTypes;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;

/**
 * 扩展字段的挂接栏目
 * 
 * @author Isaac_Gu
 */
@Service
public class GrantColumnService {

	@Autowired
	private GrantColumnDao grantColumnDao;

	/**
	 * 在xy_column 表中 根据 col_extField_ID = parameters.groupId 来查找所有的column
	 * @param parameters
	 * @param channel 
	 * @return
	 */
	public GrantInfo findGrantedColumns(GrantInfo parameters, int channel)
			throws Exception {
		parameters = grantColumnDao.findGrantedColumns(parameters,channel);
		return parameters;
	}

	public GrantInfo grantColumns(GrantInfo parameters) throws Exception {
		//不进行操作，直接返回
		if (parameters == null) {
			parameters = new GrantInfo();
			parameters.setRt_operationResult("对不起，无法获取参数，请重新进行操作！");
			return parameters;
		}
		//不进行操作，直接返回
		if (parameters.getIds() == null && parameters.getNewIds() == null) {
			parameters.setRt_operationResult("NoOperation");
			return parameters;
		}
//		String ids = parameters.getCh().equals("0")? parameters.getIds_0() : parameters.getIds_1();
		//获得添加列表和删除列表
		Map<String, List<String>> addAndDelListMap = InfoHelper.assembleAddAndDelArray(
				parameters.getIds(), parameters.getNewIds());

		//进行添加与删除操作
		if (addAndDelListMap.get("addList") != null && !addAndDelListMap.get("addList").isEmpty()) {
			grantColumnDao.addNewColumns(parameters, addAndDelListMap.get("addList"));
		}
		if (addAndDelListMap.get("delList") != null && !addAndDelListMap.get("delList").isEmpty()) {
			grantColumnDao.delColumns(parameters, addAndDelListMap.get("delList"));
		}
		if (addAndDelListMap != null && !addAndDelListMap.isEmpty()) {
			parameters.setRt_AddAndDelMap(addAndDelListMap);
		}

		parameters.setRt_operationResult("success");
		return parameters;
	}

	/**
	 * 当挂接栏目操作成功之后，写栏目日志，包括添加操作和删除操作的日志
	 * @param request
	 * @param sysUser
	 * @param parameters
	 * @throws Exception
	 */
	public void writeColumnLog(HttpServletRequest request, SysUser sysUser,
			GrantInfo parameters) throws Exception {
		//获取添加栏目的列表
		List<String> addList = parameters.getRt_AddAndDelMap().get("addList");
		int libID = LibHelper.getLibIDByOtherLib(DocTypes.COLUMN.typeID(), 
				Integer.parseInt(parameters.getDocLibID()));

		//取组的名称
		int catID = Integer.parseInt(parameters.getGroupID());
		int catTypeID = CatTypes.CAT_EXTFIELD.typeID();
		CatManager catManager = (CatManager) Context.getBean(CatManager.class);
		Category cat = catManager.getCat(catTypeID, catID);
		String catName = cat==null?"":cat.getCatName();

		//如果添加列表不为空，为每一个栏目添写日志
		if (addList != null && !addList.isEmpty()) {
			for (String id : addList) {
				if (id != null && !id.trim().equals("")) {
					LogHelper.writeLog(libID, Long.parseLong(id), sysUser, "稿件扩展字段挂接栏目",
							"挂接的是：【" + catName + "(" + parameters.getGroupID() + ")】");
				}
			}
		}
		//获取删除栏目的列表
		List<String> delList = parameters.getRt_AddAndDelMap().get("delList");
		if (delList != null && !delList.isEmpty()) {
			for (String id : delList) {
				if (id != null && !id.trim().equals("")) {
					LogHelper.writeLog(libID, Long.parseLong(id), sysUser, "稿件扩展字段挂接栏目",
							"取消挂接的是：【" + catName + "(" + parameters.getGroupID() + ")】");
				}
			}
		}

	}

}
