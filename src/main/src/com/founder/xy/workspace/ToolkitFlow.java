package com.founder.xy.workspace;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.founder.e5.context.Context;
import com.founder.e5.flow.ProcGroupReader;
import com.founder.e5.personality.PersonalSettingReader;
import com.founder.e5.workspace.controller.ToolkitGroupController;
import com.founder.xy.workspace.service.ToolkitServiceFlow;

/**
 * 操作栏控制，只显示流程操作。
 * 用于在“待审核稿件”界面只显示“发布”和“驳回”操作
 * @author Gong Lijie
 */
@Controller
@RequestMapping("/xy")
public class ToolkitFlow extends ToolkitGroupController {
	@SuppressWarnings("rawtypes")
	@Override
	@RequestMapping("toolkitFlow.do")
	protected void handle(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception {

		if (service == null) {
			service = new ToolkitServiceFlow();
			procGroupReader = (ProcGroupReader)Context.getBean(ProcGroupReader.class);
			psReader = (PersonalSettingReader)Context.getBean(PersonalSettingReader.class);
		}
		
		super.handle(request, response, model);
	}
}
