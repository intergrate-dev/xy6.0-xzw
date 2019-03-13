package com.founder.xy.api.jabbar;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import com.founder.e5.context.E5Exception;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.founder.e5.web.org.StringValueUtils;
import com.founder.e5.workspace.ProcHelper;
import com.founder.xy.commons.InfoHelper;

/**
 * 与天钩系统接口的Api
 */
@Controller
@RequestMapping("/api/jabbar")
public class JabbarApiController {

    @Autowired
    private JabbarApiManager jabbarApiManager;

    /**
     * 获取所有站点栏目树结构 channel：0 web库栏目树， 1 app库栏目树
     *
     * @param response
     * @throws E5Exception
     */
    @RequestMapping(value = "getAllSiteNodeTreeXml.do")
    public void getAllSiteNodeTreeXml(HttpServletRequest request, HttpServletResponse response) throws E5Exception {
        String siteInfoXml = "";

        int channel = StringValueUtils.getInt(request.getParameter("channel"), 0);
        int siteID = StringValueUtils.getInt(request.getParameter("SiteID"), 0);
        int parentID = StringValueUtils.getInt(request.getParameter("ObjectID"), 0);
        int userID = ProcHelper.getUserID(request);
        int roleID = ProcHelper.getRoleID(request);

        if (0 == siteID) {// 获取站点信息时 先检测账号
            if (jabbarApiManager.jabbarLogin(request) != 0) {// 登陆失败
                siteInfoXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><LIST><ITEM id=" + -1
                        + "><TITLE><![CDATA[登陆失败]]></TITLE></ITEM></LIST>";
            } else { // 登陆成功
                siteInfoXml = jabbarApiManager.getSiteInfo(request);
            }
        } else if (siteID > 0) {
            // 获取栏目信息
            siteInfoXml = jabbarApiManager.getSiteNodeInfo(userID, siteID, parentID, channel, roleID);
        }

        InfoHelper.outputText(siteInfoXml, response);
    }


}
