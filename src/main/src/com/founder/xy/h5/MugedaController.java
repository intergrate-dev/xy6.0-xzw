package com.founder.xy.h5;

import com.founder.e5.doc.Document;
import com.founder.e5.web.SysUser;
import com.founder.e5.workspace.ProcHelper;
import com.founder.xy.article.web.ArticleServiceHelper;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.system.site.SiteUserManager;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.jsoup.helper.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * Created by isaac_gu on 2017/2/22.
 */
@Controller
@RequestMapping("/xy/h5")
public class MugedaController {
    public static String tokenUrl = "URL/partner/auth/KEY";
    public static String saveImageUrl = "URL/myani/saveimage?signature=SIGNATURE&mts=MTS&openid=OPENID&domain=DOMAIN";
    public static String saveVideoUrl = "URL/myani/savevideo?signature=SIGNATURE&mts=MTS&openid=OPENID&domain=DOMAIN";
    public static String openMgdPage = "URL/anilist.php?ui=noleftbar&signature=SIGNATURE&mts=MTS&openid=OPENID&domain=DOMAIN";
    public static String getPublishList = "URL/partner/publish?signature=SIGNATURE&mts=MTS&openid=OPENID&domain=DOMAIN&page=PAGE&cpp=CPP";

    @Autowired
    SiteUserManager manager;

    /**
     * 登录木疙瘩，并获得我的作品页面
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping("/loginMugeda.do")
    public String loginMugeda(
            HttpServletRequest request, HttpServletResponse response) {
        Mugeda mugeda = getMugeda(request, "login");
        //如果没有配置木疙瘩，提示出错信息
        if (!StringUtil.isBlank(mugeda.getErrorMsg())) {
            InfoHelper.outputText(mugeda.getErrorMsg(), response);
            return null;
        }
        return "redirect:" + tokenUrl.replace("URL", mugeda.buildUrl()).replace("KEY", mugeda.buildFinalKey());
    }

    private Mugeda getMugeda(HttpServletRequest request, String type) {
        SysUser user = ProcHelper.getUser(request);
        Document userDoc = manager.getUser(LibHelper.getUserExtLibID(), user.getUserID());
        String group = userDoc.getString("u_org");
        int role = userDoc.getInt("u_roleH5");
        return new Mugeda(user.getUserName(), user.getUserID() + "", type, role, group);
    }

    /**
     * H5作品库
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping("/mugedaWorksLib.do")
    public String mugedaWorksLib(
            HttpServletRequest request, HttpServletResponse response) {
        Mugeda mugeda = getMugeda(request, "works");
        //如果没有配置木疙瘩，提示出错信息
        if (!StringUtil.isBlank(mugeda.getErrorMsg())) {
            InfoHelper.outputText(mugeda.getErrorMsg(), response);
            return null;
        }
        String url = openMgdPage.replace("URL", mugeda.buildUrl()).replace("SIGNATURE", mugeda.getSignature())
                .replace("MTS", mugeda.getTimestamp() + "").replace("OPENID", mugeda.getOpenid())
                .replace("DOMAIN", mugeda.getDomain());
        return "redirect:" + url;
    }

    /**
     * 访问作品列表 - 返回JSON
     *
     * @param request   --
     * @param response  --
     * @param page      分页
     * @param cpp       每页条数
     * @param title     标题名
     * @param startDate 开始时间
     * @param endDate   截止时间
     * @throws Exception
     */
    @RequestMapping(value = "/mugedaPublishList.do", produces = "text/plain;charset=UTF-8")
    public void mugedaPublishList(
            HttpServletRequest request, HttpServletResponse response,
            @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer cpp,
            String title, String startDate, String endDate) {
        Mugeda mugeda = getMugeda(request, "list");
        if (!StringUtil.isBlank(mugeda.getErrorMsg())) {
            InfoHelper.outputText(mugeda.getErrorMsg(), response);
            return;
        }

        // 获得url
        String url = getPublishList.replace("URL", mugeda.buildUrl()).replace("SIGNATURE", mugeda.getSignature())
                .replace("MTS", mugeda.getTimestamp() + "").replace("OPENID", mugeda.getOpenid())
                .replace("DOMAIN", mugeda.getDomain()).replace("PAGE", page + "").replace("CPP", cpp + "")
                + buildUrl(title, startDate, endDate);

        //发送Get请求到木疙瘩
        JSONObject json = ArticleServiceHelper.executeHttpRequest(new HttpGet(url), true);
        if (json == null) {
            json = new JSONObject();
            json.put("msg", "访问木疙瘩API服务器出错！请稍候再试！");
        }
        InfoHelper.outputJson(json.toString(), response);
    }

    @RequestMapping("/queryUrl.do")
    public void queryUrl(HttpServletResponse response) {
        JSONObject json = new JSONObject();
        String key = InfoHelper.getConfig("H5", "木疙瘩域");
        if (!StringUtils.isBlank(key)) {
            json.put("code", 0);
            json.put("url", key);
        }else{
            json.put("code", 1);
            json.put("error", "系统后台未配置木疙瘩域！");
        }
        InfoHelper.outputJson(json.toString(), response);
    }


    private String buildUrl(String title, String startDate, String endDate) {
        StringBuilder sb = new StringBuilder();
        if (!StringUtils.isBlank(title)) {
            sb.append("&title=").append(title.replaceAll(" ", "%20"));
        }
        if (!StringUtils.isBlank(startDate)) {
            sb.append("&startDate=").append(startDate);
        }
        if (!StringUtils.isBlank(endDate)) {
            sb.append("&endDate=").append(endDate);
        }
        return sb.toString();
    }

}

