package com.founder.xy.api.info;

import com.founder.e5.context.E5Exception;
import com.founder.xy.api.info.InfoApiManager;
import com.founder.xy.commons.InfoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by yu.feng on 2017/9/4.
 */
@Controller
@RequestMapping("/api/info")
public class InfoApiController {

    @Autowired
    private InfoApiManager infoApiManager;

    @RequestMapping(value = "getDep.do")
    public void getDep(HttpServletResponse response, int eid, long time,
                                   String sign, String data) throws E5Exception {
        String result = infoApiManager.getDep(eid, time, sign, data);
        InfoHelper.outputJson(result, response);
    }

    @RequestMapping(value = "getSites.do")
    public void getSites(HttpServletResponse response, int eid, long time,
                       String sign, String data) throws E5Exception {
        String result = infoApiManager.getSites(eid, time, sign, data);
        InfoHelper.outputJson(result, response);
    }

    @RequestMapping(value = "getUsersByOrg.do")
    public void getUsersByOrg(HttpServletResponse response, int eid, long time,
                         String sign, String data) throws E5Exception {
        String result = infoApiManager.getUsersByOrg(eid, time, sign, data);
        InfoHelper.outputJson(result, response);
    }

    @RequestMapping(value = "getUsersBySite.do")
    public void getUsersBySite(HttpServletResponse response, int eid, long time,
                         String sign, String data) throws E5Exception {
        String result = infoApiManager.getUsersBySite(eid, time, sign, data);
        InfoHelper.outputJson(result, response);
    }
}
