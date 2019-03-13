package com.founder.xy.api.newmobile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.founder.e5.context.E5Exception;
import com.founder.xy.commons.InfoHelper;

/**
 * @author JiangYu 2018.03.20
 */
@Controller
@RequestMapping("api/newMobile")
public class OriginalApiController {

	@Autowired
	private OriginalApiManager originalApiManager;
	
	/**
	 * 源稿审核通过接口
	 * @param request
	 * @param response
	 * @param userID
	 * @param data
	 * @throws Exception
	 */
	@RequestMapping(value = "transferOriginal.do")
    public void transferOriginal(HttpServletRequest request, HttpServletResponse response, 
    		int userID, String data) throws Exception {
		String result = originalApiManager.transfer(data, userID, true);
		InfoHelper.outputJson(result, response);
	}
	
	/**
	 * 源稿驳回接口
	 * @param request
	 * @param response
	 * @param userID
	 * @param data
	 * @throws Exception
	 */
	@RequestMapping(value = "rejectOriginal.do")
    public void rejectOriginal(HttpServletRequest request, HttpServletResponse response, 
    		int userID, String data) throws Exception {
		String result = originalApiManager.transfer(data, userID, false);
		InfoHelper.outputJson(result, response);
	}
	/**
	 * 源稿驳回接口
	 * @param request
	 * @param response
	 * @param userID
	 * @param data
	 * @throws Exception
	 */
	@RequestMapping(value = "rejectOriginalFir.do")
    public void rejectOriginalFir(HttpServletRequest request, HttpServletResponse response,
    		int userID, String data) throws Exception {
		String result = originalApiManager.rejectOriginalFir(data, userID, false);
		InfoHelper.outputJson(result, response);
	}

	/**
	 * 源稿审核栏目列表接口
	 * @param request
	 * @param response
	 * @param userID
	 * @param data
	 * @throws E5Exception
	 */
	@RequestMapping(value = "getOriAuditCats.do")
    public void getOriAuditCats(HttpServletRequest request, HttpServletResponse response, 
    		int userID, String data) throws E5Exception {
        String result = originalApiManager.getOriAuditCats(data, userID);
        InfoHelper.outputJson(result, response);
    }

	/**
	 * 源稿列表接口
	 * @param request
	 * @param response
	 * @param userID
	 * @param data
	 * @throws E5Exception
	 */
	@RequestMapping(value = "getOriginalArticles.do")
    public void getOriginalArticles(HttpServletRequest request, HttpServletResponse response, 
    		int userID, String data) throws E5Exception {
		String tenantCode = InfoHelper.getTenantCode(request);
		String result = originalApiManager.getOriginalArticles(data, userID, tenantCode);
        InfoHelper.outputJson(result, response);
	}
	
	/**
	 * 源稿详情页接口
	 * @param request
	 * @param response
	 * @param userID
	 * @param data
	 * @throws E5Exception
	 */
	@RequestMapping(value = "getOriginalDetail.do")
    public void getOriginalDetail(HttpServletRequest request, HttpServletResponse response, 
    		int userID, String data) throws Exception {
		String result = originalApiManager.getOriginalDetail(data, userID);
        InfoHelper.outputJson(result, response);
	}

	@RequestMapping(value = "getDocId.do")
	public void getDocId(HttpServletRequest request, HttpServletResponse response,
								  int userID, String data) throws Exception {
		String result = originalApiManager.getDocId(data, userID);
		InfoHelper.outputJson(result, response);
	}




	
}
