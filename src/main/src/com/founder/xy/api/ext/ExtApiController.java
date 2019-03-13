package com.founder.xy.api.ext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.founder.xy.api.ArticleApiManager;
import com.founder.xy.commons.InfoHelper;

/**
 * 与App外网api通讯的Api 之 扩展功能api(人物、记者关注）
 * @author Gong Lijie
 */
@Controller
@RequestMapping("/api/app")
public class ExtApiController {
	@Autowired
	private ArticleApiManager apiManager;
	@Autowired
	private LeaderApiManager leaderApiManager;
	@Autowired
	private AuthorApiManager authorApiManager;

	/**
	 * 查看人物列表
	 */
	@RequestMapping("leaderView.do")
	public void leaderView(HttpServletRequest request,HttpServletResponse response,
			int start,int count, int siteID) throws Exception{
		boolean result = leaderApiManager.leaderList(siteID, start, count);
		InfoHelper.outputText(String.valueOf(result), response);
	}
	/**
	 * 查看地区人物列表
	 */
	@RequestMapping("regionLeaderView.do")
	public void regionLeaderView(HttpServletRequest request,HttpServletResponse response,
			int regionID, int siteID)throws Exception{
		boolean result = leaderApiManager.regionLeaderList(siteID, regionID);
		InfoHelper.outputText(String.valueOf(result), response);
	}
	/**
	 * 人物详情
	 */
	@RequestMapping("leader.do")
	public void leaderDetail(HttpServletRequest request, HttpServletResponse response,
			int id, int siteID)throws Exception{
		boolean result = leaderApiManager.leaderDetail(id);
		InfoHelper.outputText(String.valueOf(result), response);
	}

	/**
	 * 用户关注
	 */
	@RequestMapping("myAuthor.do")
	public void myAuthor(HttpServletRequest request,HttpServletResponse response,
			int siteID,int userID,String userName,int authorID,
			String authorName)throws Exception{
		boolean result = authorApiManager.myAuthor(userID, userName, authorID, authorName);
		InfoHelper.outputText(String.valueOf(result), response);
	}

	@RequestMapping("myAuthorCancel.do")
	public void myAuthorCancel(HttpServletRequest request,HttpServletResponse response,
			int siteID,int userID,int authorID)throws Exception{
		boolean result = authorApiManager.myAuthorCancel(userID, authorID);
		InfoHelper.outputText(String.valueOf(result), response);
	}

	/**
	 * 我的关注
	 */
	@RequestMapping("myAuthorView.do")
	public void myAuthorView(HttpServletRequest request,HttpServletResponse response,
			int id,int page, int siteID)throws Exception{
		boolean result = authorApiManager.myAuthorView(id, page);
		InfoHelper.outputText(String.valueOf(result), response);
	}

	/**
	 * 记者文章数、粉丝数
	 */
	@RequestMapping("authorCount.do")
	public void authorCount(HttpServletRequest request,HttpServletResponse response,
			int id, int siteID)throws Exception{
		boolean result = authorApiManager.authorCount(id);
		InfoHelper.outputText(String.valueOf(result), response);
	}

	/**
	 * 记者稿件列表
	 */
	@RequestMapping("authorArticles.do")
	public void authorArticles(HttpServletRequest request,HttpServletResponse response,
			int id,int start,int count, int siteID)throws Exception{
		boolean result = apiManager.getAuthorArticles(id, start, count);
		InfoHelper.outputText(String.valueOf(result), response);
	}

	@RequestMapping("isAttention.do")
	public void isAttention(HttpServletRequest request,HttpServletResponse response,
			int userID,int authorID, int siteID)throws Exception{
		boolean result = authorApiManager.setAuthorFans(authorID);
		InfoHelper.outputText(String.valueOf(result), response);
	}
}
