package com.founder.xy.workspace;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.founder.e5.commons.DomHelper;
import com.founder.e5.web.WebUtil;
import com.founder.xy.commons.DocTypes;

/**
 * 细览入口
 */
@Controller
@RequestMapping("/xy")
public class ViewController {
	
	@RequestMapping("View.do")
	public void entry(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		int docTypeID = DomHelper.getDocTypeIDByLibID(docLibID);
		
		//若是稿件或原稿，则显示稿件细览
		if (docTypeID == DocTypes.ARTICLE.typeID() || docTypeID == DocTypes.ORIGINAL.typeID()) {
			String url = "article/View.do?" + request.getQueryString();
			response.sendRedirect(url);
		} else {
			response.sendRedirect("../e5workspace/DocView.do?" + request.getQueryString());
		}
	}
}
