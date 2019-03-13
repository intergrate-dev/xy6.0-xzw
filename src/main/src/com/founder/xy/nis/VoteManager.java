package com.founder.xy.nis;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.doc.util.E5docHelper;
import com.founder.e5.sys.StorageDevice;
import com.founder.e5.sys.StorageDeviceManager;
import com.founder.e5.sys.SysFactory;
import com.founder.e5.web.WebUtil;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.commons.web.FilePublishHelper;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

import java.io.InputStream;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by isaac on 2015/8/10.
 */
@Component
public class VoteManager {
    /**
     * 初始化投票 - 从数据库中查找
     *
     * @param voteParam
     * @return
     * @throws E5Exception
     */
    public Vote initVote(int docLibID, long docIDs) throws E5Exception {
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        //从数据库中获得投票的doc
        Document doc = docManager.get(docLibID, docIDs);
        return new Vote(doc);
    }

    /**
     * 初始化投票选项
     *
     * @param request
     * @return
     */
    public JSONArray initVoteOptionArray(int docLibID, long docIDs) throws E5Exception {
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        //从数据库
        int docLibId = LibHelper.getLibIDByOtherLib(DocTypes.VOTEOPTION.typeID(), docLibID);
        Document[] docs = docManager.find(docLibId, "vote_voteID=?", new Object[]{docIDs});
        //拼装成set
        JSONArray voteOptionArray = new JSONArray();
        if (docs != null && docs.length > 0) {
            JSONObject _vo;
            for (Document doc : docs) {
                _vo = new JSONObject();
                _vo.put("docID", doc.getDocID());
                _vo.put("vote_count", doc.getString("vote_count"));
                _vo.put("vote_countInitial", doc.getString("vote_countInitial"));
                _vo.put("vote_option", doc.getString("vote_option"));
                _vo.put("vote_picUrl", doc.getString("vote_picUrl"));
                _vo.put("vote_voteID", doc.getString("vote_voteID"));

                voteOptionArray.add(_vo);
            }
        }
        return voteOptionArray;
    }

    /**
     * 从request中获得选项的set
     *
     * @param request
     * @param docLibID
     * @param aNew
     * @return
     * @throws E5Exception
     */
    public Set<Document> assembleVoteOptionSet(HttpServletRequest request, int docLibID,
                                               boolean aNew) throws E5Exception {
        //选项set
        Set<Document> resultDocSet = new HashSet<>();
        Document voteOptionDoc;
        //判断是否有选项
        String optionNo = request.getParameter("optionNo");
        //循环的次数
        String lastOptionNo = request.getParameter("lastOptionNo");
        if (optionNo != null && !"".equals(optionNo)) {
            int voteOptionLibID = LibHelper.getLibIDByOtherLib(DocTypes.VOTEOPTION.typeID(), docLibID);
            for (int no = 0, size = Integer.parseInt(lastOptionNo) + 1; no <= size; no++) {
                //为节省表空间，若扩展字段没填值，则不保存。
                if (!StringUtils.isBlank(request.getParameter("vote_option_" + no))) {
                    DocumentManager docManager = DocumentManagerFactory.getInstance();
                    //如果是新建，就初始化新模型；如果是修改就从数据库当中获得bean
                    if (aNew || request.getParameter("docID_" + no) == null || "".equals(request.getParameter("docID_" + no))) {
                        long voteOptionID = InfoHelper.getNextDocID(DocTypes.VOTEOPTION.typeID());
                        voteOptionDoc = docManager.newDocument(voteOptionLibID, voteOptionID);
                    } else {
                        voteOptionDoc = docManager.get(voteOptionLibID, Long.parseLong(request.getParameter("docID_" + no)));
                    }
                    //赋上从前台拿过来的值
                    voteOptionDoc.set("vote_option", request.getParameter("vote_option_" + no));
                    voteOptionDoc.set("vote_countInitial",
                            Integer.parseInt(request.getParameter("vote_countInitial_" + no) == null
                                    || "".equals(request.getParameter("vote_countInitial_" + no)) ? "0" : request.getParameter("vote_countInitial_" + no)));
                    voteOptionDoc.set("vote_picUrl", request.getParameter("vote_picUrl_" + no));
                    //放到set中
                    resultDocSet.add(voteOptionDoc);
                }
            }
        }
        return resultDocSet;
    }

    /**
     * 保存投票
     *
     * @param vote
     * @param voteOptionDocSet
     * @return
     * @throws E5Exception
     * @throws SQLException
     */
    public long saveVotes(Vote vote, Set<Document> voteOptionDocSet) throws E5Exception, SQLException {
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        //获得投票的libID
        int voteLibID = LibHelper.getLibIDByOtherLib(DocTypes.VOTE.typeID(), vote.getDocLibID());
        Document voteDoc;
        Long voteID;
        //获得voteDoc
        if (vote.isNew()) {
            voteID = InfoHelper.getNextDocID(DocTypes.VOTE.typeID());
            voteDoc = docManager.newDocument(voteLibID, voteID);
        } else {
            voteDoc = docManager.get(voteLibID, vote.getDocID());
        }
        //重新赋值
        voteDoc.set("vote_siteID", vote.getVote_siteID());
        voteDoc.set("vote_groupID", vote.getVote_groupID());
        voteDoc.set("vote_topic", vote.getVote_topic());
        voteDoc.set("vote_type", vote.getVote_type());
        voteDoc.set("vote_selectLimited", vote.getVote_selectLimited());
        voteDoc.set("SYS_AUTHORS", vote.getAuthor());
        voteDoc.set("SYS_AUTHORID", vote.getAuthorId());
        if (vote.getVote_endDate() != null && !"".equals(vote.getVote_endDate())) {
            Timestamp endDateTime = new Timestamp(
                    DateUtils.parse(vote.getVote_endDate(), "yyyy-MM-dd HH:mm").getTime());
            voteDoc.set("vote_endDate", endDateTime);
        } else {
            voteDoc.set("vote_endDate", null);
        }
        //开始保存操作
        DBSession conn = null;
        try {
            conn = E5docHelper.getDBSession(vote.getDocLibID());
            conn.beginTransaction();

            //如果有删除的选项，先删除
            if (vote.getDeleteOptionIds() != null && !"".equals(vote.getDeleteOptionIds())) {
                if (vote.getDeleteOptionIds().indexOf(",") == 0)
                    vote.setDeleteOptionIds(vote.getDeleteOptionIds().substring(1));
                long[] _ids = StringUtils.getLongArray(vote.getDeleteOptionIds());
                int voteOptionLibID = LibHelper.getLibIDByOtherLib(DocTypes.VOTEOPTION.typeID(), voteLibID);
                for (long _id : _ids) {
                    docManager.delete(voteOptionLibID, _id, conn);
                }
            }
            //保存vote
            docManager.save(voteDoc, conn);
            //保存选项
            for (Document d : voteOptionDocSet) {
                d.set("vote_voteID", voteDoc.getDocID());
                docManager.save(d, conn);
            }
            conn.commitTransaction();
        } catch (Exception e) {
            ResourceMgr.rollbackQuietly(conn);
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(conn);
        }
        return voteDoc.getDocID();
    }

    /**
     * 保存图片
     *
     * @param request
     * @param is
     * @param fileName
     * @return
     * @throws E5Exception
     */
    public String savePic(HttpServletRequest request, InputStream is, String fileName)
            throws E5Exception {
        // 附件存储设备的名称
        StorageDevice device = InfoHelper.getPicDevice();
        // 构造存储的路径和文件名，目录为201505/13/，文件名用uuid
        String savePath = InfoHelper.getPicSavePath(request)
                + fileName.substring(fileName.lastIndexOf("."));
        // 开始存储到存储设备上
        StorageDeviceManager sdManager = SysFactory.getStorageDeviceManager();
        sdManager.write(device, savePath, is);

        return device.getDeviceName() + ";" + savePath;
    }


    /**
     * 删除投票
     *
     * @param DocIDs
     * @param DocLibID
     */
    public void deleteVote(String DocIDs, Integer DocLibID) {
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        //如果需要删除的id为空，不做操作
        if (DocIDs == null || "".equals(DocIDs)) return;

        //获得需要删除的id
        long[] ids = StringUtils.getLongArray(DocIDs);

        DBSession conn = null;
        try {
            conn = E5docHelper.getDBSession(DocLibID);
            conn.beginTransaction();
            //遍历删除
            for (long id : ids) {
                Document[] vos = docManager.find(DocLibID, "vote_rootid=?", new Object[]{id}, conn);
                for (Document d : vos) {
                    //先删除选项
                    int voteOptionLibID = LibHelper.getLibIDByOtherLib(DocTypes.VOTEOPTION.typeID(), DocLibID);
                    Document[] vops = docManager.find(voteOptionLibID, "vote_voteID=?", new Object[]{d.getDocID()}, conn);
                    for (Document vop : vops) {
                        docManager.delete(voteOptionLibID, vop.getDocID(), conn);
                    }
                    //其次删除问题
                    docManager.delete(DocLibID, d.getDocID(), conn);
                }
                //最后删除投票
                docManager.delete(DocLibID, id, conn);
            }
            conn.commitTransaction();
        } catch (Exception e) {
            ResourceMgr.rollbackQuietly(conn);
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(conn);
        }
    }

    public List<Document> assembleVoteQuestionSet(HttpServletRequest request,
                                                  int docLibID, boolean aNew) throws E5Exception {
        //选项set
        List<Document> resultDocSet = new ArrayList<Document>();
        Document voteOptionDoc;
        //判断是否有选项
        String questionNo = request.getParameter("questionNo");
        //循环的次数
        String lastQuestionNo = request.getParameter("lastQuestionNo");
        if (questionNo != null && !"".equals(questionNo)) {
            int voteOptionLibID = LibHelper.getLibIDByOtherLib(DocTypes.VOTE.typeID(), docLibID);
            for (int no = 0, size = Integer.parseInt(lastQuestionNo) + 1; no <= size; no++) {
                //为节省表空间，若扩展字段没填值，则不保存。
                if (!StringUtils.isBlank(request.getParameter("vote_question_" + no))) {
                    DocumentManager docManager = DocumentManagerFactory.getInstance();
                    //如果是新建，就初始化新模型；如果是修改就从数据库当中获得bean
                    if (aNew || request.getParameter("docID_" + no) == null || "".equals(request.getParameter("docID_" + no))) {
                        long voteOptionID = InfoHelper.getNextDocID(DocTypes.VOTE.typeID());
                        voteOptionDoc = docManager.newDocument(voteOptionLibID, voteOptionID);
                    } else {
                        voteOptionDoc = docManager.get(voteOptionLibID, Long.parseLong(request.getParameter("docID_" + no)));
                    }
                    //赋上从前台拿过来的值
                    //voteOptionDoc.set("vote_siteID", WebUtil.getInt(request, "vote_siteID", 0));
                    voteOptionDoc.set("vote_topic", request.getParameter("vote_question_" + no));
                    voteOptionDoc.set("vote_type", WebUtil.getInt(request, "vote_type_" + no, 0));
                    voteOptionDoc.set("vote_selectLimited", WebUtil.getInt(request, "vote_selectLimited_" + no, 0));
                    //放到set中
                    resultDocSet.add(voteOptionDoc);
                }
            }
        }
        return resultDocSet;
    }

    public long saveVotesNew(Vote vote, List<Document> voteOptionDocSet, HttpServletRequest request) throws E5Exception {
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        //获得投票的libID
        int voteLibID = LibHelper.getLibIDByOtherLib(DocTypes.VOTE.typeID(), vote.getDocLibID());
        Document voteDoc;
        Long voteID;
        //获得voteDoc
        if (vote.isNew()) {
            voteID = InfoHelper.getNextDocID(DocTypes.VOTE.typeID());
            voteDoc = docManager.newDocument(voteLibID, voteID);
        } else {
            voteDoc = docManager.get(voteLibID, vote.getDocID());
        }
        //重新赋值
        voteDoc.set("vote_siteID", vote.getVote_siteID());
        voteDoc.set("vote_groupID", vote.getVote_groupID());
        voteDoc.set("vote_topic", vote.getVote_topic());
        voteDoc.set("vote_type", vote.getVote_type());
        voteDoc.set("vote_selectLimited", vote.getVote_selectLimited());
        voteDoc.set("SYS_AUTHORS", vote.getAuthor());
        voteDoc.set("SYS_AUTHORID", vote.getAuthorId());
        voteDoc.set("vote_cycle", vote.getVote_cycle());
        voteDoc.set("vote_cyclenum", vote.getVote_cyclenum());
        voteDoc.set("vote_needlogin", vote.getVote_needlogin());
        voteDoc.set("vote_onlyapp", vote.getVote_onlyapp());
        if (vote.getVote_endDate() != null && !"".equals(vote.getVote_endDate())) {
            Timestamp endDateTime = new Timestamp(
                    DateUtils.parse(vote.getVote_endDate(), "yyyy-MM-dd HH:mm").getTime());
            voteDoc.set("vote_endDate", endDateTime);
        } else {
            voteDoc.set("vote_endDate", null);
        }
        //开始保存操作
        DBSession conn = null;
        try {
            conn = E5docHelper.getDBSession(vote.getDocLibID());
            conn.beginTransaction();

            //如果有删除的问题，先删除
            if (vote.getDeleteQuestionIds() != null && !"".equals(vote.getDeleteQuestionIds())) {
                if (vote.getDeleteQuestionIds().indexOf(",") == 0)
                    vote.setDeleteOptionIds(vote.getDeleteQuestionIds().substring(1));
                long[] _ids = StringUtils.getLongArray(vote.getDeleteQuestionIds());
                for (long _id : _ids) {
                    docManager.delete(voteLibID, _id, conn);
                    delOptionByQId(_id, voteLibID);
                }
            }
            //如果有删除的选项，先删除
            if (vote.getDeleteOptionIds() != null && !"".equals(vote.getDeleteOptionIds())) {
                if (vote.getDeleteOptionIds().indexOf(",") == 0)
                    vote.setDeleteOptionIds(vote.getDeleteOptionIds().substring(1));
                long[] _ids = StringUtils.getLongArray(vote.getDeleteOptionIds());
                int voteOptionLibID = LibHelper.getLibIDByOtherLib(DocTypes.VOTEOPTION.typeID(), voteLibID);
                for (long _id : _ids) {
                    docManager.delete(voteOptionLibID, _id, conn);
                }
            }
            //保存vote
            docManager.save(voteDoc, conn);
            int i =0;
            //保存选项
            for (Document d : voteOptionDocSet) {
                d.set("vote_rootid", voteDoc.getDocID());
                docManager.save(d, conn);
                saveOptions(d, request, vote.isNew(), i, vote.getVote_siteID());
                i++;
            }
            conn.commitTransaction();
        } catch (Exception e) {
            ResourceMgr.rollbackQuietly(conn);
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(conn);
        }
        return voteDoc.getDocID();
    }

    private void delOptionByQId(long _id, int voteLibID) throws E5Exception {
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        int voteOptionLibID = LibHelper.getLibIDByOtherLib(DocTypes.VOTEOPTION.typeID(), voteLibID);
        Document[] docs = docManager.find(voteOptionLibID, " vote_voteID=? ", new Object[]{_id});
        if(docs.length>0)
            for(Document doc : docs)
                docManager.delete(doc);

    }

    private void saveOptions(Document doc, HttpServletRequest request, boolean aNew, int i, int siteID) throws NumberFormatException, E5Exception {
        if (!StringUtils.isBlank(request.getParameter("vote_options_" + i))) {
            JSONArray arr = JSONArray.fromObject(request.getParameter("vote_options_" + i));
            if(arr != null && arr.size() > 0){
                int docLibID = LibHelper.getLibIDByOtherLib(DocTypes.VOTE.typeID(), doc.getDocLibID());
                int voteOptionLibID = LibHelper.getLibIDByOtherLib(DocTypes.VOTEOPTION.typeID(), docLibID);
                for (int no = 0; no < arr.size(); no++) {
                    JSONObject json = JSONObject.fromObject(arr.get(no));
                    Document voteOptionDoc = null;
                    //为节省表空间，若扩展字段没填值，则不保存。
                    if (json != null) {
                        DocumentManager docManager = DocumentManagerFactory.getInstance();
                        //如果是新建，就初始化新模型；如果是修改就从数据库当中获得bean
                        if (aNew || !json.containsKey("docID") || "".equals(json.get("docID"))) {
                            long voteOptionID = InfoHelper.getNextDocID(DocTypes.VOTEOPTION.typeID());
                            voteOptionDoc = docManager.newDocument(voteOptionLibID, voteOptionID);
                        } else {
                            voteOptionDoc = docManager.get(voteOptionLibID, Long.parseLong(json.getString("docID")));
                        }
                        //赋上从前台拿过来的值
                        voteOptionDoc.set("vote_option", json.get("vote_option"));
                        voteOptionDoc.set("vote_countInitial",
                                Integer.parseInt(json.getString("vote_countInitial") == null
                                        || "".equals(json.getString("vote_countInitial")) ? "0" : json.getString("vote_countInitial")));
                        voteOptionDoc.set("vote_picUrl", json.get("vote_picUrl"));
                        voteOptionDoc.set("vote_voteID", doc.getDocID());
                        //保存至数据库
                        docManager.save(voteOptionDoc);
                        if(!StringUtils.isBlank(json.getString("vote_picUrl")))
                            //发布
                            publishTo(siteID, json.getString("vote_picUrl"));
                    }
                }
            }
        }
    }

    //发布
    private void publishTo(int siteID, String srcPath) {
        String[] dirs = InfoHelper.readSiteInfo(siteID);
        String destPath = dirs == null ? null : dirs[1];
        FilePublishHelper.publishTo(srcPath, destPath, null, dirs[0]);
    }

    public JSONArray initVoteOptionArrayNew(Integer docLibID, Long docIDs) throws E5Exception {
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        JSONArray arr = new JSONArray();
        Document[] votes = docManager.find(docLibID, " vote_rootid=? ", new Object[]{docIDs});
        if (votes != null && votes.length > 0) {
            JSONObject json;
            for (Document vote : votes) {
                json = new JSONObject();
                json.put("docID", vote.getDocID());
                json.put("vote_question", vote.getString("vote_topic"));
                json.put("vote_type", vote.getString("vote_type"));
                json.put("vote_selectLimited", vote.getString("vote_selectLimited"));

                //从数据库
                int docLibId = LibHelper.getLibIDByOtherLib(DocTypes.VOTEOPTION.typeID(), docLibID);
                Document[] docs = docManager.find(docLibId, "vote_voteID=?", new Object[]{vote.getDocID()});
                //拼装成set
                JSONArray voteOptionArray = new JSONArray();
                // 创建一个数值格式化对象

                NumberFormat numberFormat = NumberFormat.getInstance();

                // 设置精确到小数点后2位

                numberFormat.setMaximumFractionDigits(2);

                int num = 0;
                if (docs != null && docs.length > 0) {
                    JSONObject _vo;
                    for (Document doc : docs) {
                        num = num + doc.getInt("vote_count");
                    }
                    for (Document doc : docs) {
                        _vo = new JSONObject();
                        _vo.put("docID", doc.getDocID());
                        _vo.put("vote_count", doc.getString("vote_count"));
                        _vo.put("vote_countInitial", doc.getString("vote_countInitial"));
                        _vo.put("vote_option", doc.getString("vote_option"));
                        _vo.put("vote_picUrl", doc.getString("vote_picUrl"));
                        _vo.put("vote_voteID", doc.getString("vote_voteID"));
                        _vo.put("vote_rate", num>0?numberFormat.format((float)doc.getInt("vote_count")/(float)num*100) + "%":0);

                        voteOptionArray.add(_vo);
                    }
                }
                json.put("vote_options", voteOptionArray);
                arr.add(json);
            }
        }
        return arr;
    }
}
