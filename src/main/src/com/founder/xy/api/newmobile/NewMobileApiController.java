package com.founder.xy.api.newmobile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.context.E5Exception;
import com.founder.e5.sys.StorageDeviceManager;
import com.founder.e5.sys.SysFactory;
import com.founder.xy.commons.InfoHelper;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by yu.feng on 2017/9/21.
 */
@Controller
@RequestMapping("api/newMobile")
public class NewMobileApiController {

    @Autowired
    private NewMobileApiManager newMobileApiManager;

    @RequestMapping(value = "getPubArticles.do")
    public void getPubArticles(HttpServletRequest request, HttpServletResponse response, int userID, String data) throws E5Exception {

        String result = newMobileApiManager.getPubArticles(userID,data);
        InfoHelper.outputJson(result, response);
    }

    @RequestMapping(value = "getTabs.do")
    public void getTabs(HttpServletRequest request, HttpServletResponse response, int userID, String data) throws E5Exception {

        String result = newMobileApiManager.getTabs(userID,data);
        InfoHelper.outputJson(result, response);
    }

    @RequestMapping(value = "addArticle.do")
    public void addArticle(HttpServletRequest request, HttpServletResponse response, int userID, String data) throws E5Exception {

        String result = newMobileApiManager.addArticle(userID,data);
        InfoHelper.outputJson(result, response);
    }

    @RequestMapping(value = "uploadImage.do")
    public void uploadImage(HttpServletResponse response, HttpServletRequest request)
            throws Exception {

        String result = newMobileApiManager.uploadImage(request);
        InfoHelper.outputJson(result, response);

    }

    /**
     * 稿件详情接口，审核稿件详情接口，撤稿中心稿件详情接口 （共用）
     * @author kangxw
     * @param request
     * @param response
     * @param data
     * @throws Exception
     */
    @RequestMapping(value = "getArticleDetail.do")
    public void getArticleDetail(HttpServletRequest request, HttpServletResponse response, String data) throws Exception {
        String result = newMobileApiManager.getArticleDetail(request, data);
        InfoHelper.outputJson(result, response);
    }

    /**
     * 获取图片流接口
     * @author kangxw
     * @param request
     * @param response
     */
    @RequestMapping(value = "getImage.do", method = RequestMethod.GET)
    public void getImage(HttpServletRequest request, HttpServletResponse response) {
        //获取附件的路径path
        String path = request.getParameter("path");
        String from = request.getParameter("from");
        int pos = path.indexOf(";");
        if (pos < 0) {
            if(from != null && "newMobile".equals(from)){
                return;
            }
            noPic(request, response);
            return;
        }

        //类型
        String deviceName = path.substring(0, pos);
        //保存路径
        String savePath = path.substring(pos + 1);

        InputStream in = null;
        OutputStream out = null;
        StorageDeviceManager sdManager = SysFactory.getStorageDeviceManager();
        try {
            //显示图片的header
            String CONTENT_TYPE = "image/jpeg; charset=UTF-8";
            response.setContentType(CONTENT_TYPE);
            response.setHeader("Cache-Control", "no-store");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);

            in = sdManager.read(deviceName, savePath);
            out = response.getOutputStream();
            IOUtils.copy(in, out);
        } catch (Exception e) {
            if(from != null && "newMobile".equals(from)){
                return;
            }
            noPic(request, response);
        } finally {
            ResourceMgr.closeQuietly(in);
            ResourceMgr.closeQuietly(out);
        }
    }
    protected void noPic(HttpServletRequest request, HttpServletResponse response) {
        try {
            if (request.getParameter("title") != null) {
                response.sendRedirect("../../images/notitle.png");
            } else {
                response.sendRedirect("../../images/nopic.png");
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * 稿件检索接口（发布库）
     * @author kangxw
     * @param request
     * @param response
     * @param data
     * @throws Exception
     */
    @RequestMapping(value = "pubSearch.do")
    public void pubSearch(HttpServletRequest request, HttpServletResponse response, String data) throws Exception {
    	String result = newMobileApiManager.pubSearch(request, data);
    	InfoHelper.outputJson(result, response);
    }

    /**
     * 撤稿中心检索接口
     * @author kangxw
     * @param request
     * @param response
     * @param data
     * @throws Exception
     */
    @RequestMapping(value = "revokeSearch.do")
    public void revokeSearch(HttpServletRequest request, HttpServletResponse response, String data) throws Exception {
    	String result = newMobileApiManager.revokeSearch(request, data);
    	InfoHelper.outputJson(result, response);
    }

    @RequestMapping(value = "getPubCols.do")
    public void getPubCols(HttpServletRequest request, HttpServletResponse response, int userID, String data) throws E5Exception {

        String result = newMobileApiManager.getPubCols(userID,data);
        InfoHelper.outputJson(result, response);
    }

    /**
     * 稿件流程记录接口
     * @author kangxw
     * @param request
     * @param response
     * @param data
     * @throws Exception
     */
    @RequestMapping(value = "flowRecordList.do")
    public void flowRecordList(HttpServletRequest request, HttpServletResponse response, String data) throws Exception {
    	String result = newMobileApiManager.flowRecordList(data);
    	InfoHelper.outputJson(result, response);
    }

    /**
     * 稿件推送客户端接口
     * @author kangxw
     * @param request
     * @param response
     * @param data
     * @throws Exception
     */
    @RequestMapping(value = "pushApp.do")
    public void pushApp(HttpServletRequest request, HttpServletResponse response, String data) throws Exception {
    	String result = newMobileApiManager.pushApp(request, data);
    	InfoHelper.outputJson(result, response);
    }

    @RequestMapping(value = "colIsOp.do")
    public void colIsOp(HttpServletRequest request, HttpServletResponse response, int userID, String data) throws E5Exception {

        String result = newMobileApiManager.colIsOp(userID,data);
        InfoHelper.outputJson(result, response);
    }

    /**
     * 栏目检索接口
     * @author kangxw
     * @param request
     * @param response
     * @param data
     * @throws Exception
     */
    @RequestMapping(value = "colSearch.do")
    public void colSearch(HttpServletRequest request, HttpServletResponse response, int userID, String data) throws Exception {
    	String result = newMobileApiManager.colSearch(request, data, userID);
    	InfoHelper.outputJson(result, response);
    }

    @RequestMapping(value = "deleteArticle.do")
    public void deleteArticle(HttpServletRequest request, HttpServletResponse response, int userID, String data) throws E5Exception {

        String result = newMobileApiManager.deleteArticle(userID,data);
        InfoHelper.outputJson(result, response);
    }
    
    /**
     * 审核全部稿件列表
     * @author kangxw
     * @param request
     * @param response
     * @param data
     * @throws Exception
     */
    @RequestMapping(value = "getAuditArticles.do")
    public void getAuditArticles(HttpServletRequest request, HttpServletResponse response, String data) throws Exception {
    	String result = newMobileApiManager.getAuditArticles(request, data);
    	InfoHelper.outputJson(result, response);
    }

    @RequestMapping(value = "pubArticle.do")
    public void pubArticle(HttpServletRequest request, HttpServletResponse response, int userID, String data) throws E5Exception {

        String result = newMobileApiManager.pubArticle(userID,data);
        InfoHelper.outputJson(result, response);
    }

    /**
     * 稿件审核通过接口
     * @author kangxw
     * @param request
     * @param response
     * @param userID
     * @param data
     * @throws Exception
     */
    @RequestMapping(value = "transferArticle.do")
    public void transferArticle(HttpServletRequest request, HttpServletResponse response, int userID, String data) throws Exception {
    	String result = newMobileApiManager.transfer(data, userID, true);
    	InfoHelper.outputJson(result, response);
    }
    /**
     * 稿件审核驳回接口
     * @author kangxw
     * @param request
     * @param response
     * @param userID
     * @param data
     * @throws Exception
     */
    @RequestMapping(value = "rejectArticle.do")
    public void rejectArticle(HttpServletRequest request, HttpServletResponse response, int userID, String data) throws Exception {
    	String result = newMobileApiManager.transfer(data, userID, false);
    	InfoHelper.outputJson(result, response);
    }

    @RequestMapping(value = "revokeArticle.do")
    public void revokeArticle(HttpServletRequest request, HttpServletResponse response, int userID, String data) throws E5Exception {

        String result = newMobileApiManager.revokeArticle(userID,data);
        InfoHelper.outputJson(result, response);
    }

    @RequestMapping(value = "getAuditCols.do")
    public void getAuditCols(HttpServletRequest request, HttpServletResponse response, int userID, String data) throws E5Exception {

        String result = newMobileApiManager.getAuditCols(userID,data);
        InfoHelper.outputJson(result, response);
    }

    /**
     * 视频分组接口
     * @author kangxw
     * @param request
     * @param response
     * @param data
     * @throws Exception
     */
    @RequestMapping(value = "getVideoGroup.do")
    public void getVideoGroup(HttpServletRequest request, HttpServletResponse response, String data) throws Exception {
    	String result = newMobileApiManager.getVideoGroup(data, request);
    	InfoHelper.outputJson(result, response);
    }
    /**
     * 视频列表接口
     * @author kangxw
     * @param request
     * @param response
     * @param userID
     * @param data
     * @throws Exception
     */
    @RequestMapping(value = "getVideos.do")
    public void getVideos(HttpServletRequest request, HttpServletResponse response, int userID, String data) throws Exception {
    	String result = newMobileApiManager.getVideos(data, userID);
    	InfoHelper.outputJson(result, response);
    }
    /**
     * 视频发布接口
     * @author kangxw
     * @param request
     * @param response
     * @param userID
     * @param data
     * @throws Exception
     */
    @RequestMapping(value = "pubVideo.do")
    public void pubVideo(HttpServletRequest request, HttpServletResponse response, int userID, String data) throws Exception {
    	String result = newMobileApiManager.pubVideo(data, userID, true);
    	InfoHelper.outputJson(result, response);
    }
    /**
     * 视频取消发布接口
     * @author kangxw
     * @param request
     * @param response
     * @param userID
     * @param data
     * @throws Exception
     */
    @RequestMapping(value = "revokeVideo.do")
    public void revokeVideo(HttpServletRequest request, HttpServletResponse response, int userID, String data) throws Exception {
    	String result = newMobileApiManager.pubVideo(data, userID, false);
    	InfoHelper.outputJson(result, response);
    }
    /**
     * 上传音频接口
     * @author kangxw
     * @param request
     * @param response
     * @param userID
     * @param data
     * @throws Exception
     */
    @RequestMapping(value = "uploadAudio.do")
    public void uploadAudio(HttpServletRequest request, HttpServletResponse response, int userID, String data) throws Exception {
    	String result = newMobileApiManager.uploadVideo(request, false);
    	InfoHelper.outputJson(result, response);
    }
    /**
     * 上传视频接口
     * @author kangxw
     * @param request
     * @param response
     * @param userID
     * @param data
     * @throws Exception
     */
    @RequestMapping(value = "uploadVideo.do")
    public void uploadVideo(HttpServletRequest request, HttpServletResponse response, int userID, String data) throws Exception {
    	String result = newMobileApiManager.uploadVideo(request, true);
    	InfoHelper.outputJson(result, response);
    }

    @RequestMapping(value = "getUrl.do")
    public void getUrl(HttpServletRequest request, HttpServletResponse response) throws E5Exception {
        String result = newMobileApiManager.getUrl();
        InfoHelper.outputJson(result, response);
    }
    /**
     * 我的个人信息接口
     * @author kangxw
     * @param request
     * @param response
     * @param userID
     * @throws Exception
     */
    @RequestMapping(value = "myInfo.do")
    public void myInfo(HttpServletRequest request, HttpServletResponse response, int userID) throws Exception {
    	String result = newMobileApiManager.myInfo(userID);
    	InfoHelper.outputJson(result, response);
    }

    /**
     * 获取后台参数配置中，融合媒体移动采编 的参数
     * @author kangxw
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "getVideoSocketParams.do")
    public void getVideoSocketParams(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	String result = newMobileApiManager.getVideoSocketParams();
    	InfoHelper.outputJson(result, response);
    }

    @RequestMapping(value = "revokeDelete.do")
    public void revokeDelete(HttpServletRequest request, HttpServletResponse response, int userID, String data) throws E5Exception {

        String result = newMobileApiManager.revokeDelete(userID,data);
        InfoHelper.outputJson(result, response);
    }

}
