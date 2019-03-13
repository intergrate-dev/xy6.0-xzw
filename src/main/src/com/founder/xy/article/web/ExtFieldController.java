package com.founder.xy.article.web;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.founder.e5.commons.StringUtils;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.web.SysUser;
import com.founder.e5.workspace.ProcHelper;
import com.founder.xy.article.GrantColumnService;
import com.founder.xy.article.GrantInfo;
import com.founder.xy.column.Column;
import com.founder.xy.column.ColumnReader;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.config.ConfigReader;
import com.founder.xy.jms.PublishTrigger;
import com.founder.xy.set.ExtField;
import com.founder.xy.set.ExtFieldReader;

/**
 * 稿件扩展字段
 */
@Controller
@RequestMapping("/xy/extfield")
public class ExtFieldController {
	@Autowired
	private GrantColumnService grantColumnService;
	@Autowired
	private ColumnReader colReader;
	@Autowired
	private ExtFieldReader extFieldReader;

	/**
	 * 给组挂接新的栏目 - 初始化 1. 操作成功 返回success 2. 没有操作 返回success ？ FIXME 3. 操作失败 返回failure
	 * 
	 * @param parameters
	 * @throws Exception
	 */
	@RequestMapping("findGrantedColumns.do")
	public String findGrantedColumns(GrantInfo parameters, Map<String, Object> model)
			throws Exception {
		parameters = grantColumnService.findGrantedColumns(parameters , 0);
		model.put("ids_0", parameters.getRt_ids());
		
		parameters = grantColumnService.findGrantedColumns(parameters , 1);
		model.put("ids_1", parameters.getRt_ids());
		
		model.put("type", "admin");
		model.put("channels", ConfigReader.getChannels());

		return "/xy/article/GrantColumn";
	}

	/**
	 * 扩展字段挂接新的栏目 1. 操作成功 返回success 2. 没有操作 返回success ？ FIXME 3. 操作失败 返回failure
	 * 
	 * @param parameters
	 * @throws Exception
	 */
	@RequestMapping(value = "grantColumnsAjax.do", method = RequestMethod.POST)
	public void grantColumnsSubmit(HttpServletRequest request, HttpServletResponse response,
			GrantInfo parameters) throws Exception {
		//进行挂接栏目的操作
		parameters = grantColumnService.grantColumns(parameters);

		//如果操作成功，在栏目里写日志
		if (parameters.getRt_operationResult() != null
				&& parameters.getRt_operationResult().trim().equals("success")) {
			SysUser sysUser = ProcHelper.getUser(request);
			grantColumnService.writeColumnLog(request, sysUser, parameters);
		}
		
		int colLibID = LibHelper.getColumnLibID(request);
		PublishTrigger.columnRefresh(colLibID, 0);
		
		InfoHelper.outputText(parameters.getRt_operationResult(), response);
	}

	/**
	 * 初始化 稿件 - 写稿件 - 扩展字段模块
	 * 
	 * 1. 到xy_column表当中查看col_extField_ID是否有值 - 看看栏目有没有扩展字段 
	 * 2. 如果有的话，去xy_extfield表中查询ext_groupId=col_extField_ID的值， 
	 * 3. 如果没有的话，返回结果为 failure
	 * 
	 * @param request
	 * @param response
	 * @param parameters
	 * @throws Exception
	 */
	@RequestMapping(value = "initExtFieldAjax.do", method = RequestMethod.POST)
	public void initExtField(HttpServletRequest request, HttpServletResponse response,
			GrantInfo parameters) throws Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();

		long groupID = 0;
		int docLibID = 0;

		//如果是自定义扩展字段
		if (parameters.getExtFieldGroupID() != null && !"0".equals(parameters.getExtFieldGroupID())) {
			groupID = Long.parseLong(parameters.getExtFieldGroupID());
		} else if (!StringUtils.isBlank(parameters.getColID())){
			//看看栏目有没有扩展字段
			int colLibID = LibHelper.getColumnLibID(request);
			Column col = colReader.get(colLibID, Long.parseLong(parameters.getColID()));
			if (col != null)
				groupID = col.getExtFieldGroupID();
		}
		//如果有扩展字段，放到一个List<Map>当中 返回
		if (groupID > 0) {
			//获取稿件扩展字段的值 - 当用户修改以前的稿件时，到稿件扩展字段获取扩展字段的值
			docLibID = LibHelper.getArticleExtLibID();
			Document[] valueCols = docManager.find(docLibID, "ext_articleID=? and ext_articleLibID=?",
					new Object[] { Integer.parseInt(parameters.getDocIDs()), Integer.parseInt(parameters.getDocLibID())});
			//值的map
			Map<String, String> valueMap = new HashMap<>();
			if (valueCols != null && valueCols.length > 0) {
				valueMap = new HashMap<>();
				//给值map赋值，以便于取
				for (Document col : valueCols) {
					valueMap.put(((String) col.get("EXT_CODE")), ((String) col.get("EXT_VALUE")));
				}
			}

			//从缓存当中获得groupid相对应的set
	        String tenantCode = InfoHelper.getTenantCode(request);
	        int extLibID = LibHelper.getLibID(DocTypes.EXTFIELD.typeID(), tenantCode);
	        
			Set<ExtField> extFieldSet = extFieldReader.getFields(extLibID, groupID);
			Set<ExtField> exSet = new LinkedHashSet<>();
			if (extFieldSet != null) {
				for (ExtField ee : extFieldSet) {
					ExtField e = (ExtField) ee.clone();
					e.setExt_value(e.getExt_code() == null ? "" : valueMap.get(e.getExt_code()));
					exSet.add(e);
				}
			}
			parameters.getRt_operationJson().element("result", "success");
			parameters.getRt_operationJson().element("list", exSet);
		}

		if (parameters.getRt_operationJson().isNullObject()
				|| parameters.getRt_operationJson().isEmpty()) {
			parameters.getRt_operationJson().element("result", "failure");
		}
		InfoHelper.outputJson(parameters.getRt_operationJson().toString(), response);

	}
}