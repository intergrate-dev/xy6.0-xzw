package com.founder.xy.workspace;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.founder.e5.context.Context;
import com.founder.e5.flow.ProcGroupReader;
import com.founder.e5.personality.PersonalSettingReader;
import com.founder.e5.workspace.ProcHelper;
import com.founder.e5.workspace.controller.ToolkitGroupController;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.workspace.service.ToolOriginalServiceFlow;

/**
 * 原稿库操作按钮控制。
 * 用于在原稿库使用 源稿栏目分类授权流程操作时  操作按钮的显示\隐藏
 * @author JiangYu
 */
@Controller
@RequestMapping("/xy")
public class ToolOriginalFlow extends ToolkitGroupController  {
	@SuppressWarnings("rawtypes")
	@Override
	@RequestMapping("toolOriginalFlow.do")
	protected void handle(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception {

		String groupID = get(request, "groupID");
		
		String siteID = get(request, "siteID");
		
		int userLibID = LibHelper.getUserExtLibID(request);
		int userID = ProcHelper.getUserID(request);
		
		service = new ToolOriginalServiceFlow(groupID, siteID, userLibID, userID);
		
		procGroupReader = (ProcGroupReader)Context.getBean(ProcGroupReader.class);
		psReader = (PersonalSettingReader)Context.getBean(PersonalSettingReader.class);
		
		super.handle(request, response, model);
	}
}
