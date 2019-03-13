package com.founder.xy.nis.web;

import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.workspace.ProcHelper;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.nis.Vote;
import com.founder.xy.nis.VoteManager;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by guzm on 2015/8/3.
 */
@Controller
@RequestMapping("/xy/nis")
public class VoteController {

    @Autowired
    VoteManager voteManager;

/*    *//**
     * 初始化vote页面 - 查
     *
     * @param DocLibID
     * @param UUID
     * @param siteID
     * @param groupID
     * @param model
     * @return
     *//*
    @RequestMapping("vote.do")
    public String initVote(Long DocIDs, Integer DocLibID, String UUID, String siteID, String groupID,
            Model model) throws E5Exception {
        boolean isNew = true;
        //如果是修改操作
        if (DocIDs != null && DocIDs != 0) {
            //确定是修改操作
            isNew = false;
            //初始化参数
            Vote vote = voteManager.initVote(DocLibID, DocIDs);
            //初始化选项
            JSONArray voteOptionArray = voteManager.initVoteOptionArray(DocLibID, DocIDs);
            //放到model中
            model.addAttribute("vote", vote);
            model.addAttribute("voteOptionArray", voteOptionArray.toString());
        }
        //初始化e5穿过来的参数
        model.addAttribute("UUID", UUID);
        model.addAttribute("DocLibID", DocLibID);
        model.addAttribute("DocID", DocIDs);
        model.addAttribute("siteID", siteID);
        model.addAttribute("groupID", groupID);
        model.addAttribute("isNew", isNew);

        return "xy/nis/vote";
    }*/
    /**
     * 初始化vote页面 - 查
     *
     * @param DocLibID
     * @param UUID
     * @param siteID
     * @param groupID
     * @param model
     * @param type  0--新建或修改；1--详情页
     * @return
     */
    @RequestMapping("vote.do")
    public String initVote(Long DocIDs, Integer DocLibID, String UUID, String siteID, String groupID,
                           Model model, int type) throws E5Exception {
        boolean isNew = true;
        //如果是修改操作
        if (DocIDs != null && DocIDs != 0) {
            //确定是修改操作
            isNew = false;
            //初始化参数
            Vote vote = voteManager.initVote(DocLibID, DocIDs);
            //初始化选项
            JSONArray voteOptionArray = voteManager.initVoteOptionArrayNew(DocLibID, DocIDs);
            //放到model中
            model.addAttribute("vote", vote);
            model.addAttribute("voteOptionArray", voteOptionArray.toString());
        }
        //初始化e5穿过来的参数
        model.addAttribute("UUID", UUID);
        model.addAttribute("DocLibID", DocLibID);
        model.addAttribute("DocID", DocIDs);
        model.addAttribute("siteID", siteID);
        model.addAttribute("groupID", groupID);
        model.addAttribute("isNew", isNew);
        if(type == 1)
            return "xy/nis/voteView";
        return "xy/nis/vote";
    }

    /**
     * 上传图片
     *
     * @param response
     * @param request
     * @param imageName
     * @throws Exception
     */
    @RequestMapping(value = "uploadpic.do", method = RequestMethod.POST)
    public void uploadPic(HttpServletResponse response, HttpServletRequest request) throws Exception {
        // 初始化文件接收
        MultipartResolver resolver = new CommonsMultipartResolver(request.getSession()
                .getServletContext());
        MultipartHttpServletRequest multipartRequest = resolver.resolveMultipart(request);

        // 拿到前台传过来的数据
        MultipartFile file = multipartRequest.getFile("file");
        String fileName = file.getOriginalFilename();

        // 保存图片并获得图片名
        String imagePath = null;

        InputStream is = file.getInputStream();
        try {
            imagePath = voteManager.savePic(request, is, fileName);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(is);
        }

        //把图片的保存路径放到json当中，以便于以后做扩展
        JSONObject json = new JSONObject();
        json.put("imagePath", imagePath);

        InfoHelper.outputJson(json.toString(), response);
    }

    /**
     * 添加一个投票 - 增
     *
     * @param request
     * @param vote
     * @return
     * @throws E5Exception
     * @throws SQLException
     * @throws UnsupportedEncodingException
     *//*
    @RequestMapping(value = "addvote.do", method = RequestMethod.POST)
    public String addVote(HttpServletRequest request,
            Vote vote) throws E5Exception, SQLException, UnsupportedEncodingException {
        //把request中的选项组织成 Set<Document>
        Set<Document> voteOptionDocSet = voteManager.assembleVoteOptionSet(request, vote.getDocLibID(), vote.isNew());
        //设定作者与其id
        vote.setAuthor(ProcHelper.getUserName(request));
        vote.setAuthorId(ProcHelper.getUserID(request));
        //保存投票及其选项
        long docID = voteManager.saveVotes(vote, voteOptionDocSet);
        //隐去div并刷新列表
        return "redirect:/e5workspace/after.do?UUID=" + request.getParameter("UUID") + "&Info=" + URLEncoder.encode("操作成功", "UTF-8") + "&DocIDs=" + docID;
    }*/

    /**
     * 添加一个投票 - 增
     *
     * @param request
     * @param vote
     * @return
     * @throws E5Exception
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "addvote.do", method = RequestMethod.POST)
    public String addVote(HttpServletRequest request,
                          Vote vote) throws E5Exception, SQLException, UnsupportedEncodingException {
        //把request中的问题组织成 Set<Document>
        List<Document> voteOptionDocSet = voteManager.assembleVoteQuestionSet(request, vote.getDocLibID(), vote.isNew());
        //设定作者与其id
        vote.setAuthor(ProcHelper.getUserName(request));
        vote.setAuthorId(ProcHelper.getUserID(request));
        //保存投票及其选项
        long docID = voteManager.saveVotesNew(vote, voteOptionDocSet, request);
        //隐去div并刷新列表
        return "redirect:/e5workspace/after.do?UUID=" + request.getParameter("UUID") + "&Info=" + URLEncoder.encode("操作成功", "UTF-8") + "&DocIDs=" + docID;
    }

    /**
     * 删除一个投票或多个投票 - 删
     *
     * @param DocIDs
     * @param DocLibID
     * @param UUID
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping("deleteVote.do")
    public String deleteVote(String DocIDs, Integer DocLibID, String UUID) throws UnsupportedEncodingException {
        voteManager.deleteVote(DocIDs, DocLibID);
        return "redirect:/e5workspace/after.do?UUID=" + UUID + "&Info=" + URLEncoder.encode("操作成功", "UTF-8") + "&DocIDs=" + DocIDs;
    }

}
