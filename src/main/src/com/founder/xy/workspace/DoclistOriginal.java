package com.founder.xy.workspace;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.founder.e5.context.Context;
import com.founder.e5.listpage.cache.ListModeReader;
import com.founder.e5.workspace.controller.DocListHTMLController;
import com.founder.e5.workspace.param.DocListParam;
import com.founder.e5.workspace.service.DocListService;

/**
 * 源稿库  稿件列表
 * 
 * @author JiangYu
 */
@Controller
@RequestMapping("/xy")
public class DoclistOriginal extends DocListHTMLController {

	@SuppressWarnings("rawtypes")
	@Override
	@RequestMapping("doclistOrg.do")
	protected void handle(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception {
		
		listModeReader = (ListModeReader)Context.getBean(ListModeReader.class);
		
		super.handle(request, response, model);
		
	}
	
	@Override
	protected String getListResult(DocListParam param) {
		
		DocListService listService = null;
//		DocListService listService1 = null;
//		listService = (DocListService)Context.getBean("DoclistOriginalService");
		listService = (DocListService)Context.getBean("DocListService");
//		if (StringUtils.isBlank(param.getCondition()))
//			listService = (DocListService)Context.getBean("DoclistOriginalService");//DocListService
//		else
//			listService = (DocListService)Context.getBean("SearchDocListService");

		listService.init(param);
		String result = listService.getDocList();
		
		result = addParam(result, param);
		
		return result;
	}
	
}
