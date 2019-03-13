package com.founder.xy.api.nis;

import com.founder.e5.context.E5Exception;
import com.founder.xy.commons.InfoHelper;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;

import static com.founder.xy.commons.web.HTTPHelper.checkValid;

/**
 * 用于直播api的接口
 */
@Controller
@RequestMapping("/api/app")
public class LiveApiController {

    @Autowired
    private LiveApiManager liveApiManager;

    /**
     * 提交直播的继续报道（现场图文直播员）
     */
    @RequestMapping(value = "live.do")
    public void live(HttpServletResponse response, String data) {
        JSONObject json = checkValid(data);
        if (json.getInt("code") == 0) {
            try {
                liveApiManager.commitLive(data);
                json.put("msg", "保存成功");
            } catch (E5Exception e) {
                json.put("code", 1);
                json.put("error", "保存失败");
                json.put("e", e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
        InfoHelper.outputText(json.toString(), response);
    }

    /**
     * 浏览直播
     */
    @RequestMapping(value = "liveView.do")
    public void liveView(HttpServletResponse response, long id, int page, int siteID) throws E5Exception {
        boolean result = liveApiManager.liveView(id, page);
        InfoHelper.outputText(String.valueOf(result), response);
    }

    @RequestMapping(value = "liveDetail.do")
    public void liveDetail(HttpServletResponse response, long id, int page, int siteID) throws E5Exception {
        String result = liveApiManager.liveDetail(id, page);
        InfoHelper.outputText(result, response);
    }

    /**
     * 直播预告列表Api
     *
     * @param response
     * @throws E5Exception
     */
    @RequestMapping(value = "liveComing.do")
    public void liveComing(HttpServletResponse response, int siteID) throws E5Exception {
        boolean result = liveApiManager.liveComing(siteID);
        InfoHelper.outputText(String.valueOf(result), response);
    }

    /**
     * 直播列表 - 只用于现场直播员，不做缓存
     *
     * @param response
     * @param data
     * @throws E5Exception
     */
    @RequestMapping(value = "lives.do")
    public void lives(HttpServletResponse response, String data) throws E5Exception {
        String result = "";
        JSONObject jsonobj = JSONObject.fromObject(data);
        int siteID=jsonobj.getInt("siteID");
        JSONObject json = checkValid(data);
        if (json.getInt("code") == 0) {
            result = liveApiManager.lives(siteID);
        } else {
            result = json.toString();
        }
        InfoHelper.outputText(result, response);
    }

    @RequestMapping(value = "allLives.do")
    public void allLives(HttpServletResponse response, String data) throws E5Exception {
        String result = "";
        JSONObject jsonobj = JSONObject.fromObject(data);
        int siteID=jsonobj.getInt("siteID");
        String status = jsonobj.getString("status");
        int lastID = jsonobj.getInt("lastID");
        JSONObject json = checkValid(data);
        if (json.getInt("code") == 0) {
            result = liveApiManager.allLives(siteID,status,lastID);
        } else {
            result = json.toString();
        }
        InfoHelper.outputText(result, response);
    }

    @RequestMapping(value = "saveLivePlaybackUrl.do")
    public void saveLivePlaybackUrl(HttpServletResponse response, String data) {
        JSONObject json = new JSONObject();
        try {
            liveApiManager.saveLivePlaybackUrl(data);
            json.put("msg", "保存成功");
        } catch (Exception e) {
            json.put("code", 1);
            json.put("error", "保存失败");
            json.put("e", e.getLocalizedMessage());
            e.printStackTrace();
        }
        InfoHelper.outputText(json.toString(), response);
    }


    @RequestMapping(value = "queryLiveStatus.do")
    public void queryLiveStatus(HttpServletResponse response, String data) {
        JSONObject json = checkValid(data);
        String result = "";
        try {
            if (json.getInt("code") == 0) {
                result = liveApiManager.queryLiveStatus(data);
            } else {
                result = json.toString();
            }
        } catch (Exception e) {
            json.put("code", 1);
            json.put("error", "出现异常");
            json.put("e", e.getLocalizedMessage());
            result = json.toString();
            e.printStackTrace();
        }
        InfoHelper.outputText(result, response);
    }

}
