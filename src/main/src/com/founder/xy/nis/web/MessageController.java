package com.founder.xy.nis.web;

import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.context.EUID;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.web.SysUser;
import com.founder.e5.workspace.ProcHelper;
import com.founder.e5.workspace.app.form.FormSaver;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.nis.ForumManager;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by isaac_gu on 2017/4/10.
 */
@Controller
@RequestMapping("/xy/nis")
public class MessageController {
    @Autowired
    ForumManager forumManager;

    @RequestMapping("Message.do")
    public String message(Model model, int DocLibID, String UUID, int siteID) {
        model.addAttribute("DocLibID", DocLibID);
        model.addAttribute("UUID", UUID);
        model.addAttribute("siteID", siteID);
        return "/xy/nis/Message";
    }

    @RequestMapping("messageSave.do")
    public String messageSave(
            HttpServletRequest request, int DocLibID, Long docID, String UUID) {
        try {
            Document doc = saveMessage(request, DocLibID, docID);
            docID = doc.getDocID();
            saveAttachments(doc);
            int result = forumManager.transWhenPass(doc);
            //发布后需要把附件表里面发布的地址放回到doc中
            if (result == 0) {
                changeAttachmentsUrl(DocLibID, docID, doc);
            }
            
            RedisManager.setTimeless(RedisKey.RED_DOT_MESSAGE, doc.getCreated().getTime() + "");
            RedisManager.clearKeyPages(RedisManager.getKeyBySite(RedisKey.APP_MESSAGE_LIST_KEY, doc.getInt("a_siteID")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/e5workspace/after.do?UUID=" + UUID + "&DocIDs=" + docID;
    }

    /**
     * 发布成功后，需要把发布后的路径，重新放回到attachments中
     * @param DocLibID
     * @param docID
     * @param doc
     * @throws E5Exception
     */
    private void changeAttachmentsUrl(int DocLibID, Long docID, Document doc) throws E5Exception {
        String attachments = doc.getString("a_attachments");
        if (!StringUtils.isBlank(attachments)) {
            JSONObject json = JSONObject.fromObject(attachments);
            if (json.containsKey("pics")) {
                JSONArray array = json.getJSONArray("pics");
                if (array != null && !array.isEmpty()) {
                    int attLibID = LibHelper.getLibIDByOtherLib(DocTypes.NISATTACHMENT.typeID(), DocLibID);
                    DocumentManager documentManager = DocumentManagerFactory.getInstance();
                    Document[] attDocs = documentManager.find(attLibID,
                                                              "att_articleID=? and att_articleLibID=? and att_type=?",
                                                              new Object[]{docID, DocLibID, 1});
                    JSONArray pics = new JSONArray();
                    if (attDocs != null && attDocs.length > 0) {
                        for(int i = 0, len = array.size(); i < len ; i++ ) {
                            for(int j = 0, jlen = attDocs.length; j < jlen ; j ++) {
                                if (!StringUtils.isBlank(array.getString(i)) && array.getString(i).equals(attDocs[j].getString("att_path"))) {
                                    pics.add(attDocs[j].getString("att_url"));
                                    break;
                                }
                            }
                        }
                        json.put("pics", pics);
                        doc.set("a_attachments", json.toString());
                        documentManager.save(doc);
                    }
                }
            }
        }
    }

    private void saveAttachments(Document doc) {
        int attLibID = LibHelper.getLibIDByOtherLib(DocTypes.NISATTACHMENT.typeID(), doc.getDocLibID());
        JSONObject json = JSONObject.fromObject(doc.getString("a_attachments"));
        if (json == null || !json.containsKey("pics")) {
            return;
        }
        JSONArray pics = json.getJSONArray("pics");
        if (pics == null || pics.isEmpty()) {
            return;
        }

        DocumentManager docManager = DocumentManagerFactory.getInstance();
        int attTypeID = DocTypes.NISATTACHMENT.typeID();
        try {
            long idStart = EUID.getID("DocID" + attTypeID, pics.size());
            for (int i = 0; i < pics.size(); i++) {
                String url = pics.getString(i);

                Document attach = docManager.newDocument(attLibID, idStart++);
                attach.set("att_articleID", doc.getDocID());
                attach.set("att_articleLibID", doc.getDocLibID());
                attach.set("att_type", 1); //0:文件，1:图片;2:视频
                attach.set("att_path", url);
                docManager.save(attach);
            }
        } catch (E5Exception e) {
            e.printStackTrace();
        }
    }

    private Document saveMessage(
            HttpServletRequest request, int DocLibID, Long docID ) throws Exception {
        DocumentManager documentManager = DocumentManagerFactory.getInstance();
        Document doc;
        if (docID != null && docID != 0) {
            doc = documentManager.get(DocLibID, docID);
        } else {
            docID = InfoHelper.getNextDocID(DocTypes.MESSAGE.typeID());
            doc = documentManager.newDocument(DocLibID, docID);
        }
        SysUser user = ProcHelper.getUser(request);
        doc.set("SYS_AUTHORS", user.getUserName());
        doc.set("a_userID", user.getUserID());
        FormSaver saver = (FormSaver) Context.getBean(FormSaver.class);
        saver.handle(doc, request);
        return doc;
    }

    @RequestMapping("MessageDelete.do")
    public String messageDelete(int DocLibID, String DocIDs, String UUID, int siteID) {
        DocumentManager documentManager = DocumentManagerFactory.getInstance();
        int[] docIDs = StringUtils.getIntArray(DocIDs);
        try {
            for (int docId : docIDs) {
                documentManager.delete(DocLibID, docId);
            }
        } catch (E5Exception e) {
            e.printStackTrace();
        }
        
        String key = RedisManager.getKeyBySite(RedisKey.APP_MESSAGE_LIST_KEY, siteID);
        RedisManager.clearKeyPages(key);
        
        return "redirect:/e5workspace/after.do?UUID=" + UUID + "&DocIDs=" + DocIDs;
    }
}
