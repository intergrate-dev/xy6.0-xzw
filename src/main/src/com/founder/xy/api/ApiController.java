package com.founder.xy.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.founder.e5.context.E5Exception;
import com.founder.xy.commons.InfoHelper;

/**
 * 与外网api通讯的Api
 *
 * @author Gong Lijie
 */
@Controller
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private ArticleApiManager apiManager;

    /**
     * 刷新Redis缓存中的稿件列表组件
     * 这里要修改为返回true/false，由外网读redis得到数据，而不是直接返回json数据
     *
     * @return
     */
    @RequestMapping(value = "articleListRefresh.do")
    public void articleListRefresh(
            HttpServletResponse response,
            int coID, int colLibID, long colID, int page, int siteID) {

        String result = apiManager.articleListRefresh(coID, colLibID, colID, page, siteID);
        InfoHelper.outputJson(String.valueOf(result), response);
    }

    /**
     * 得到作者的稿件列表（网站版）
     */
    @RequestMapping(value = "authorArticles.do")
    public void authorArticles(
            HttpServletRequest request, HttpServletResponse response,
            String author, int siteID, int page) throws E5Exception {

        String result = apiManager.authorArticles(author, siteID, page);

        InfoHelper.outputJson(result, response);
    }

}
