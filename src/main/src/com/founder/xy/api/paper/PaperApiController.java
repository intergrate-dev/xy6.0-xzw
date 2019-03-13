package com.founder.xy.api.paper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.founder.e5.context.E5Exception;
import com.founder.xy.commons.InfoHelper;
/**
 * 提供数字报相关api,与外网api通讯
 */
@Controller
@RequestMapping("/api/app")
public class PaperApiController {
	@Autowired
	private PaperApiManager paperApiManager;
	
	/**
	 * 获取站点下的报纸
	 */
	@RequestMapping(value="getPapers.do")
	public void getPapers(HttpServletRequest request,HttpServletResponse response,
			int siteID) throws E5Exception{
		boolean result = paperApiManager.getPapers(siteID);
		InfoHelper.outputJson(String.valueOf(result), response);
	}
	/**
	 * 获取报纸最近的30个期次
	 */
	@RequestMapping(value = "getPaperDates.do")
	public void getPaperDates(HttpServletRequest request,HttpServletResponse response,
			int siteID,int paperID,int start, int count) throws E5Exception{
		boolean result = paperApiManager.getPaperDates(siteID, paperID,start,count);
		InfoHelper.outputJson(String.valueOf(result), response);
	}
	/**
	 * 获取一期报纸下的所有期次
	 */
	@RequestMapping(value = "getPaperLayouts.do")
	public void getPaperLayouts(HttpServletRequest request,HttpServletResponse response,
			int siteID,int paperID,String date) throws E5Exception{
		String result = paperApiManager.getPaperLayouts(siteID, paperID, date);
		InfoHelper.outputJson(result, response);
	}
	/**
	 * 获取期次稿件列表
	 */
	@RequestMapping(value = "getPaperArticles.do")
	public void getPaperArticles(HttpServletRequest request,HttpServletResponse response,
			int siteID,int id) throws E5Exception{
		boolean result = paperApiManager.getPaperArticles(siteID, id);
		InfoHelper.outputJson(String.valueOf(result), response);
	}
	/**
	 * 获取稿件详细信息
	 */
	@RequestMapping(value = "getPaperArticle.do")
	public void getPaperArticle(HttpServletRequest request,HttpServletResponse response,
			int siteID,int id) throws E5Exception{
		boolean result = paperApiManager.getPaperArticle(siteID, id);
		InfoHelper.outputJson(String.valueOf(result), response);
	}
}
