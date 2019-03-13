package com.founder.xy.api.newmobile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.founder.e5.context.E5Exception;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;

/**
 * @author JiangYu 2018.04.04
 */
@Controller
@RequestMapping("api/newMobile")
public class WeixinApiController {

	@Autowired
	private WeixinApiManager weixinApiManager;
	
	/**
	 * 微信账号（公众号）列表接口
	 * @param request
	 * @param response
	 * @param userID
	 * @param data
	 * @throws E5Exception
	 */
	@RequestMapping(value = "getWxAccounts.do")
    public void getWxAccounts(HttpServletRequest request, HttpServletResponse response, 
    		int userID, String data) throws E5Exception {
		int userRelLibID = LibHelper.getUserRelLibID(request);
        String result = weixinApiManager.getWxAccounts(data, userID, userRelLibID);
        InfoHelper.outputJson(result, response);
    }
	
	/**
	 * 微信图文组 列表接口
	 * @param request
	 * @param response
	 * @param userID
	 * @param data
	 * @throws E5Exception
	 */
	@RequestMapping(value = "getWxGroupArticles.do")
    public void getWxGroupArticles(HttpServletRequest request, HttpServletResponse response, 
    		int userID, String data) throws E5Exception {
		String result = weixinApiManager.getWxGroupArticles(data, userID);
        InfoHelper.outputJson(result, response);
	}
	
	/**
	 * 微信图文详情接口
	 * @param request
	 * @param response
	 * @param userID
	 * @param data
	 * @throws E5Exception
	 */
	@RequestMapping(value = "getWxGroupArticleDetail.do")
    public void getWxGroupArticleDetail(HttpServletRequest request, HttpServletResponse response, 
    		int userID, String data) throws E5Exception {
		String result = weixinApiManager.getWxGroupArticleDetail(data, userID);
        InfoHelper.outputJson(result, response);
	}
	/**
	 * 微信图文详情上一篇下一篇
	 * @param request
	 * @param response
	 * @param userID
	 * @param data
	 * @throws E5Exception
	 */
	@RequestMapping(value = "getWxGroupArticleDetailUPOrDown.do")
    public void getWxGroupArticleDetailUPOrDown(HttpServletRequest request, HttpServletResponse response,
    		int userID, String data) throws E5Exception {
		String result = weixinApiManager.getWxGroupArticleDetailUPOrDown(data, userID);
        InfoHelper.outputJson(result, response);
	}

	/**
	 * 微信图文组 审核全部通过 接口
	 * @param request
	 * @param response
	 * @param userID
	 * @param data
	 * @throws Exception
	 */
	@RequestMapping(value = "transferGroup.do")
    public void transferGroup(HttpServletRequest request, HttpServletResponse response, 
    		int userID, String data) throws Exception {
		String result = weixinApiManager.transfer(data, userID, true);
		InfoHelper.outputJson(result, response);
	}
	
	/**
	 * 微信图文组 全部驳回 接口
	 * @param request
	 * @param response
	 * @param userID
	 * @param data
	 * @throws Exception
	 */
	@RequestMapping(value = "rejectGroup.do")
    public void rejectGroup(HttpServletRequest request, HttpServletResponse response, 
    		int userID, String data) throws Exception {
		String result = weixinApiManager.transfer(data, userID, false);
		InfoHelper.outputJson(result, response);
	}
	
	/**
	 * 微信图文组中 单篇稿件 审核通过\驳回 接口
	 * @param request
	 * @param response
	 * @param userID
	 * @param data
	 * @throws Exception
	 */
	@RequestMapping(value = "transferGroupOne.do")
    public void transferGroupOne(HttpServletRequest request, HttpServletResponse response, 
    		int userID, String data) throws Exception {
		String result = weixinApiManager.transferOne(data, userID, true);
		InfoHelper.outputJson(result, response);
	}
	
	/**
	 * 微信图文组中 单篇稿件 审核通过\驳回 接口
	 * @param request
	 * @param response
	 * @param userID
	 * @param data
	 * @throws Exception
	 */
	@RequestMapping(value = "rejectGroupOne.do")
    public void rejectGroupOne(HttpServletRequest request, HttpServletResponse response, 
    		int userID, String data) throws Exception {
		String result = weixinApiManager.transferOne(data, userID, false);
		InfoHelper.outputJson(result, response);
	}
	
}
