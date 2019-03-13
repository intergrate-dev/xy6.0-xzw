package com.founder.xy.api;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.e5.flow.FlowNode;
import com.founder.e5.flow.FlowReader;
import com.founder.e5.sys.StorageDevice;
import com.founder.e5.workspace.ProcHelper;
import com.founder.xy.article.Article;
import com.founder.xy.column.Column;
import com.founder.xy.column.ColumnReader;
import com.founder.xy.commons.*;
import com.founder.xy.jms.PublishTrigger;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;
import com.founder.xy.system.Tenant;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ArticleImpManager extends AbstractArticleParser{

    @Autowired
    private ColumnReader colReader;

    public String deleteArticleForBig(String data) {
        JSONObject result = new JSONObject();
        JSONArray jsonArray;
        try{
            JSONObject jsonObject = JSONObject.fromObject(data);
            jsonArray = jsonObject.getJSONArray("Article");
            if(jsonArray.size()<1){
                result.put("success",false);
                result.put("message","传入参数data中没有稿件数据");
                return result.toString();
            }
        }catch (Exception e){
            e.printStackTrace();
            result.put("success",false);
            result.put("message","入参data格式错误");
            return result.toString();
        }

        List<JSONObject> resultList = deleteArticles(jsonArray);

        result.put("success",true);
        result.put("message","");
        result.put("ImpResult",resultList);

        return String.valueOf(result);
    }

    private List<JSONObject> deleteArticles(JSONArray jsonArray) {
        List<JSONObject> jsonObjectList = new ArrayList<JSONObject>();
        for(int i=0;i<jsonArray.size();i++){
            JSONObject resultDoc = deleteArticle(jsonArray.getJSONObject(i));//处理单个稿件
            jsonObjectList.add(resultDoc);
        }

        return jsonObjectList;
    }

    //处理单个稿件
    private JSONObject deleteArticle(JSONObject jsonObject) {
        //1校验基本数据
        JSONObject resultJson = new JSONObject();

        if(jsonObject.optLong("ArticleId") == 0L){
            resultJson.put("success", false);
            resultJson.put("infoID", jsonObject.getString("InfoID"));
            resultJson.put("errorCode", "107");
            resultJson.put("errorCause", "入参错误！");
            return resultJson;
        }

        //2取稿件实体
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        long docID = jsonObject.optLong("ArticleId");
        List<Document> articles = new ArrayList<>();
        try {
            DocLib[] docLibIDs = LibHelper.getLibs(DocTypes.ARTICLE.typeID());
            for(int i=0;i<docLibIDs.length;i++) {
                int docLibID = docLibIDs[i].getDocLibID();
                Document article = docManager.get(docLibID, docID);
                if(article != null){
                    articles.add(article);
                }
            }

            if(articles == null || articles.size() == 0){
                resultJson.put("success", false);
                resultJson.put("infoID", jsonObject.getString("InfoID"));
                resultJson.put("errorCode", "107");
                resultJson.put("errorCause", "没有有效的入参！");
                return resultJson;
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultJson.put("success", false);
            resultJson.put("infoID", jsonObject.getString("InfoID"));
            resultJson.put("errorCode", "111");
            resultJson.put("errorCause", "入参错误，入参数据转换成稿件数据时出错！");
            return resultJson;
        }

        //3撤稿删除
        FlowReader flowReader = (FlowReader) Context.getBean(FlowReader.class);
        try{
            for (Document article : articles) {
                if (article != null) {
                    // 获得当前稿件所处流程的ID，然后找到这个流程的第一个节点，作为稿件的当前节点，同时设置稿件的当前状态
                    int curflowID = article.getCurrentFlow();
                    FlowNode[] nodes = flowReader.getFlowNodes(curflowID);
                    article.setCurrentNode(nodes[0].getID());
                    article.setCurrentStatus(nodes[0].getWaitingStatus());
                    // 解锁操作
                    article.setLocked(false);
                    article.set("a_status", Article.STATUS_REVOKE);
                    article.setDeleteFlag(1);

                    docManager.save(article);
                    //撤稿
                    PublishTrigger.articleRevoke(article);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultJson.put("success", false);
            resultJson.put("infoID", jsonObject.getString("InfoID"));
            resultJson.put("errorCode", "112");
            resultJson.put("errorCause", "撤稿时出错！");
            return resultJson;
        }

        resultJson.put("success", true);
        resultJson.put("infoID", jsonObject.getString("InfoID"));
        resultJson.put("publishId", docID);

        return resultJson;
    }

    public String addArticleForBig(String data) {
        JSONObject result = new JSONObject();
        JSONArray jsonArray;
        try{
            JSONObject jsonObject = JSONObject.fromObject(data);
            jsonArray = jsonObject.getJSONArray("Article");
            if(jsonArray.size()<1){
                result.put("success",false);
                result.put("message","传入参数data中没有稿件数据");
                return result.toString();
            }
        }catch (Exception e){
            e.printStackTrace();
            result.put("success",false);
            result.put("message","入参data格式错误");
            return result.toString();
        }

        List<JSONObject> resultList = parseArticle(jsonArray);

        result.put("success",true);
        result.put("message","");
        result.put("ImpResult",resultList);

        return String.valueOf(result);
    }

    private List<JSONObject> parseArticle(JSONArray jsonArray) {
        List<JSONObject> jsonObjectList = new ArrayList<JSONObject>();
        for(int i=0;i<jsonArray.size();i++){
            JSONObject resultDoc = convertArticle(jsonArray.getJSONObject(i));//处理单个稿件
            jsonObjectList.add(resultDoc);
        }

        return jsonObjectList;
    }

    //处理单个稿件
    private JSONObject convertArticle(JSONObject jsonObject) {
        //1校验基本数据
        JSONObject resultJson = new JSONObject();
        JSONObject checkBaseDataResult = null;
        try {
            checkBaseDataResult = checkBaseData(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            resultJson.put("success", false);
            resultJson.put("infoID", jsonObject.getString("InfoID"));
            resultJson.put("errorCode", "107");
            resultJson.put("errorCause", "入参错误！");
            return resultJson;
        }
        if (!checkBaseDataResult.getBoolean("success")) {
            return checkBaseDataResult;
        }

        //2开始处理图片
        List<Document> articles = null;
        try {
            articles = checkPicData(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            resultJson.put("success",false);
            resultJson.put("infoID", jsonObject.getString("InfoID"));
            resultJson.put("errorCode","108");
            resultJson.put("errorCause","图片解析失败；");
            return resultJson;
        }

        //返回结果
        //3将数据放入Article中
        try {
            getArticleDoc(checkBaseDataResult, articles, jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            resultJson.put("success", false);
            resultJson.put("infoID", jsonObject.getString("InfoID"));
            resultJson.put("errorCode", "111");
            resultJson.put("errorCause", "入参错误，入参数据转换成稿件数据时出错！");
            return resultJson;
        }

        if (articles.size() < 1) {
            resultJson.put("success", false);
            resultJson.put("infoID", jsonObject.getString("InfoID"));
            resultJson.put("errorCode", "107");
            resultJson.put("errorCause", "没有有效的入参！");
            return resultJson;
        }

        //4处理稿件附件list
        List<Document> attDocList = null;
        try {
            attDocList = getAttachmentList(articles);
        } catch (E5Exception e) {
            e.printStackTrace();
            resultJson.put("success", false);
            resultJson.put("infoID", jsonObject.getString("InfoID"));
            resultJson.put("errorCode", "104");
            resultJson.put("errorCause", "附件操作失败！");
            return resultJson;
        }

        //5话题关联表操作
        String topicIDs = jsonObject.getString("TopicIDs");
        List<Object[]> topicRelartParamList = null;
        if (!StringUtils.isBlank(topicIDs)) {
            String topicJson = checkBaseDataResult.getString("topicsJson");
            topicRelartParamList = getTopicRelartParam(articles, topicIDs, topicJson);
        }


        //6保存稿件、附件、话题，在同一个事务下
        boolean saveSuccess = saveArticleAndAttachment(articles, attDocList, topicRelartParamList, jsonObject);
        if (!saveSuccess) {
            resultJson.put("success", false);
            resultJson.put("infoID", jsonObject.getString("InfoID"));
            resultJson.put("errorCode", "110");
            resultJson.put("errorCause", "保存数据失败！");
            return resultJson;
        }

        //7生成抽图文件
        for (int i = 0; i < attDocList.size(); i++) {
            Document attach = attDocList.get(i);
            int type = attach.getInt("att_type");
            String attPath = attach.getString("att_path");
            if (type == 1) {
                attPath = attach.getString("att_picPath");
            }
            extractingImg(attPath);
        }

        //8发布
        String docID = "";
        for (int i = 0; i < articles.size(); i++) {
            Document article = articles.get(i);
            docID = String.valueOf(article.getDocID());
            article.set("a_status", Article.STATUS_PUB_ING);
            PublishTrigger.article(article);
        }

        resultJson.put("success", true);
        resultJson.put("infoID", jsonObject.getString("InfoID"));
        resultJson.put("publishId", docID);

        return resultJson;
    }

    //处理话题稿件关联表参数
    private List<Object[]> getTopicRelartParam(List<Document> articles, String topicIDs, String topicJson) {
        List<Object[]> list = new ArrayList<>();
        String[] array_topicIDs = topicIDs.split(",");
        long articleID= articles.get(0).getDocID();
        double order = articles.get(0).getDouble("a_order");
        int siteID = articles.get(0).getInt("a_siteID");
        JSONObject topicNameJson = JSONObject.fromObject(topicJson);
        for(int i=0;i<array_topicIDs.length;i++){
            String topicID = array_topicIDs[i];

            Object[] objects1 = new Object[]{Integer.valueOf(topicID),articleID,topicNameJson.getString(topicID),
                    order,1,siteID,2,Article.STATUS_PUB_NOT};
            Object[] objects2 = new Object[]{Integer.valueOf(topicID),articleID,topicNameJson.getString(topicID),
                    order,2,siteID,2,Article.STATUS_PUB_NOT};
            list.add(objects1);
            list.add(objects2);
        }

        return list;
    }

    private boolean saveArticleAndAttachment(List<Document> articles, List<Document> attDocList, List<Object[]> topicRelartParamList, JSONObject jsonObject) {
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        DBSession conn = null;
        try {
            conn = Context.getDBSession();
            conn.beginTransaction();
            if(articles!=null){
                for (Document article : articles) {
                    docManager.save(article, conn);
                }
            }

            if(jsonObject.optLong("ArticleId") != 0L) {
                if(articles!=null){
                    String deleteAtt = "delete from xy_attachment where att_articleID=? and att_articleLibID=?";
                    for (Document article : articles) {
                        InfoHelper.executeUpdate(deleteAtt, new Object[]{article.getDocID(),article.getDocLibID()},conn);
                    }
                }
            }

            if(attDocList!=null){
                for (Document attach : attDocList) {
                    docManager.save(attach, conn);
                }
            }

            if(jsonObject.optLong("ArticleId") != 0L) {
                if(articles!=null) {
                    String queryTopics = "SELECT a_topicID, a_topicName from xy_topicrelart where a_articleID=? and a_channel=?";
                    String deleteTopics = "delete from xy_topicrelart where a_articleID=? and a_channel=?";
                    for (Document article : articles) {
                        Object[] param = new Object[]{article.getDocID(), article.getInt("a_channel")};
                        JSONArray topicsList = queryTopics(queryTopics, param);

                        if (topicsList != null && topicsList.size() > 0) {
                            for (int j = 0; j < topicsList.size(); j++) {
                                JSONObject topic = topicsList.getJSONObject(j);
                                long topicID = topic.getLong("topicID");

                                String key1 = RedisManager.getKeyBySite(RedisKey.APP_ARTICLELIST_TOPIC_KEY, Integer.valueOf(article.getInt("a_siteID")))
                                        + (article.getInt("a_channel") - 1) + "." + topicID + "." + 0;
                                String key2 = RedisManager.getKeyBySite(RedisKey.APP_ARTICLELIST_TOPIC_KEY, Integer.valueOf(article.getInt("a_siteID")))
                                        + (article.getInt("a_channel") - 1) + "." + topicID + "." + 1;
                                String key3 = RedisManager.getKeyBySite(RedisKey.APP_ARTICLELIST_TOPIC_KEY, Integer.valueOf(article.getInt("a_siteID")))
                                        + (article.getInt("a_channel") - 1) + "." + topicID + "." + 2;
                                String key4 = RedisManager.getKeyBySite(RedisKey.APP_ARTICLELIST_TOPIC_KEY, Integer.valueOf(article.getInt("a_siteID")))
                                        + (article.getInt("a_channel") - 1) + "." + topicID + "." + 100;

                                RedisManager.clearLongKeys(key1);
                                RedisManager.clearLongKeys(key2);
                                RedisManager.clearLongKeys(key3);
                                RedisManager.clearLongKeys(key4);
                            }

                            //删除话题
                            InfoHelper.executeUpdate(deleteTopics, new Object[]{article.getDocID(), article.getInt("a_channel")});
                        }
                    }
                }
            }

            if(topicRelartParamList!=null){
                String insertSql = "insert into xy_topicrelart(a_topicID,a_articleID,a_topicName,a_order,a_channel,a_siteID,a_type,a_status) values(?,?,?,?,?,?,?,?)";
                for(int i=0;i<topicRelartParamList.size();i++){
                    InfoHelper.executeUpdate(insertSql, topicRelartParamList.get(i));
                }
            }

            // 提交transaction
            conn.commitTransaction();
        } catch (Exception e) {
            ResourceMgr.rollbackQuietly(conn);
            e.printStackTrace();
            return false;
        } finally {
            ResourceMgr.closeQuietly(conn);
        }

        return true;
    }

    private JSONArray queryTopics(String sql, Object[] param) {
        JSONArray result = new JSONArray();

        DBSession db = null;
        IResultSet rs = null;
        try {
            db = Context.getDBSession();
            rs = db.executeQuery(sql, param);
            while (rs.next()) {
                JSONObject json = new JSONObject();
                json.put("topicID", rs.getLong("a_topicID"));
                json.put("topicName", rs.getString("a_topicName"));

                result.add(json);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(rs);
            ResourceMgr.closeQuietly(db);
        }
        return result;
    }

    //处理稿件附件list
    private List<Document> getAttachmentList(List<Document> articles) throws E5Exception {
        List<Document> attList = new ArrayList<>();
        for(int i=0;i<articles.size();i++){
            Document article = articles.get(i);
            String smallPic = article.getString("a_picSmall");
            String middlePic = article.getString("a_picMiddle");
            String bigPic = article.getString("a_picBig");
            String picPath = article.getString("a_picPath");
            String videoID = article.getString("VideoID");
            if(!StringUtils.isBlank(videoID)){
                addAttachment(videoID,picPath,1, attList,article);
            }
            if(!StringUtils.isBlank(smallPic)){
                addAttachment(smallPic,"",4, attList,article);
            }
            if(!StringUtils.isBlank(middlePic)){
                addAttachment(middlePic,"",3, attList,article);
            }
            if(!StringUtils.isBlank(bigPic)){
                addAttachment(bigPic,"",2, attList,article);
            }

        }
        return attList;
    }

    /**
     * @param attPath
     * @param picPath
     * @param type 1正文视频 2标题图大 3标题图中 4标题图小
     * @param attList
     * @param article
     */
    private void addAttachment(String attPath, String picPath, int type, List<Document> attList, Document article) throws E5Exception {
            DocumentManager docManager = DocumentManagerFactory.getInstance();
            int attLibID = LibHelper.getLibIDByOtherLib(DocTypes.ATTACHMENT.typeID(), article.getDocLibID());
            long attDocID = InfoHelper.getNextDocID(DocTypes.ATTACHMENT.typeID());
            Document attach = docManager.newDocument(attLibID, attDocID);

            attach.set("att_articleID", article.getDocID());
            attach.set("att_articleLibID", article.getDocLibID());
            attach.set("att_type",type);
            attach.set("att_path",attPath);
            if(type==1){
                attach.set("att_url",attPath);
                attach.set("att_urlPad",attPath);
                attach.set("att_picPath",picPath);
                attach.set("att_duration",article.get("duration"));
            }
            attList.add(attach);
    }

    //数据放入Article中
    private void getArticleDoc(JSONObject checkBaseDataResult, List<Document> articles, JSONObject jsonObject) throws Exception {
        for(int i=0;i<articles.size();i++){
            Document article = articles.get(i);

            if(article.getDocLibID()==1){
                article.set("a_channel",1);
                article.set("a_column",checkBaseDataResult.getString("column"));
                article.set("a_columnRel",checkBaseDataResult.getString("columnRel"));
                article.set("a_columnAll",checkBaseDataResult.getString("a_columnAll"));
                article.set("a_columnID",jsonObject.getLong("ColumnID"));
                article.set("a_columnRelID",jsonObject.getString("ColumnRelID"));
            }else{
                Column column = colReader.get(LibHelper.getColumnLibID(), jsonObject.getLong("ColumnID"));
                long colID = column.getPushColumn();
                Column columnApp = colReader.get(LibHelper.getColumnLibID(), colID);
                if(columnApp==null){//没有自动同步栏目，则取消自动同步
                    continue;
                }
                article.set("a_channel",2);
                article.set("a_columnID", colID);
                article.set("a_column", columnApp.getCasNames());
                article.set("a_columnAll",colID);
                article.set("a_columnRel","");
                article.set("a_columnRelID","");
            }

            article.set("a_siteID",jsonObject.getInt("SiteId"));

            article.set("SYS_TOPIC",jsonObject.getString("Title"));
            article.set("a_linkTitle", jsonObject.getString("Title"));

            article.set("a_subTitle",jsonObject.getString("Subtitle"));
            article.set("a_abstract",jsonObject.getString("Abstract"));
            String keyword = jsonObject.getString("Keyword");
            if(keyword.getBytes("UTF-8").length>1000){
                byte[] bytes = keyword.getBytes("UTF-8");
                int tempLen = new String(bytes, 0, 1000, "UTF-8").length();
                keyword = keyword.substring(0, tempLen);
            }

            article.set("a_keyword",keyword);
            article.set("a_tag",jsonObject.getString("Tag"));


            String nsdate = jsonObject.getString("Nsdate");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            if(!StringUtils.isBlank(nsdate)){
                nsdate = sdf.format(sdf.parse(nsdate));
            }else{
                Date date = new Date();
                nsdate = sdf.format(date);
            }
            if(jsonObject.optLong("ArticleId") == 0L){
                article.set("SYS_CREATED",nsdate);
            }

            article.set("a_source",jsonObject.getString("Source"));
            article.set("a_regionID",StringUtils.isBlank(jsonObject.getString("Region"))?null:jsonObject.getInt("Region"));

            article.set("SYS_AUTHORS",jsonObject.getString("Author"));
            article.set("a_editor",jsonObject.getString("Editor"));
            article.set("SYS_AUTHORID",StringUtils.isBlank(jsonObject.getString("userID"))?-1:jsonObject.getInt("userID"));
            article.set("a_liability",jsonObject.getString("Liability"));
            article.set("a_content",jsonObject.getString("Content"));

            article.set("a_type", 2);
            article.set("a_status", Article.STATUS_PUB_NOT);
            if(jsonObject.optLong("ArticleId") == 0L){
                article.set("a_pubTime", DateUtils.getTimestamp());
            }

            article.set("duration", jsonObject.getString("Duration"));
            article.set("VideoID",jsonObject.getString("OriginalId"));

            if(jsonObject.optLong("ArticleId") == 0L){
                double order = getNewOrder(article);
                article.set("a_order", order);
            }
        }

    }

    //生成图片
    private void convertImage(String name,String picStr, Document article) throws Exception {
        if(picStr.length()<=0){
            return;
        }
        byte[] bytes = Base64Decoder.decode(picStr);
        FileNamePair fileNamePair = generateFilePath(getImageType(bytes));
        FilePathUtil.write(bytes, fileNamePair.abstractPath);
        if(SmallTitlePic.equals(name)){
            article.set("a_picSmall", fileNamePair.recordPath);
        }else if(MiddleTitlePic.equals(name)){
            article.set("a_picMiddle", fileNamePair.recordPath);
        }else if(BigTitlePic.equals(name)){
            article.set("a_picBig", fileNamePair.recordPath);
        }else if(VideoPic.equals(name)){
            article.set("a_picPath", fileNamePair.recordPath);
        }
    }


    //校验基本数据
    private JSONObject checkBaseData(JSONObject jsonObject) throws Exception {
        JSONObject resultJson = new JSONObject();
        String errorCode = "";//错误代码
        String result = "";//错误信息

        int colLibID = LibHelper.getColumnLibID();
        String columnRel = "";
        String a_columnAll = "";
        String column = "";

        String InfoID = jsonObject.getString("InfoID");
        if(StringUtils.isBlank(InfoID)){
            result += "稿件同步信息ID不能为空；";
        }
        String OriginalId = jsonObject.getString("OriginalId");
        if(StringUtils.isBlank(OriginalId)){
            result += "视频ID不能为空；";
        }
        String SiteId = jsonObject.getString("SiteId");
        if(StringUtils.isBlank(OriginalId)){
            result += "站点ID不能为空；";
        }else{
            try {
                int int_siteID = Integer.valueOf(SiteId);
            }catch (Exception e){
                e.printStackTrace();
                result += "站点ID格式不正确；";
            }
        }
        String Title = jsonObject.getString("Title");
        if(StringUtils.isBlank(Title)){
            result += "标题不能为空；";
        }
        String BigTitlePic = jsonObject.getString("BigTitlePic");
        if(StringUtils.isBlank(BigTitlePic)){
            result += "大标题图不能为空；";
        }

        String ColumnID = jsonObject.getString("ColumnID");
        if(StringUtils.isBlank(ColumnID)){
            result += "主栏目ID不能为空；";
        }else{
            Column col = null;
            try {
                col = colReader.get(colLibID, Integer.parseInt(ColumnID));
            } catch (E5Exception e) {
                e.printStackTrace();
                result += "主栏目ID参数异常；";
            }
            if(col==null){
                result += "系统中未查询到该栏目ID:"+ColumnID+"；";
            }else{
                column = col.getCasNames();
            }
            a_columnAll +=";"+ColumnID;
        }
        String Duration = jsonObject.getString("Duration");
        if(StringUtils.isBlank(Duration)){
            result += "视频时长不能为空；";
        }else{
            try {
                int int_Duration = Integer.parseInt(Duration);
            }catch (Exception e){
                e.printStackTrace();
                result +="视频时长格式错误";
            }
        }
        String Region = jsonObject.getString("Region");
        if(!StringUtils.isBlank(Region)){
            try {
                int RegionID = Integer.parseInt(Region);
            }catch (Exception e){
                e.printStackTrace();
                result +="地市ID格式错误";
            }
        }
        String userID = jsonObject.getString("userID");
        if(!StringUtils.isBlank(userID)){
            try {
                int int_userID = Integer.parseInt(userID);
            }catch (Exception e){
                e.printStackTrace();
                result +="userID格式错误";
            }
        }


        String Nsdate = jsonObject.getString("Nsdate");
        if(!StringUtils.isBlank(Nsdate)){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            try {
                Nsdate = sdf.format(sdf.parse(Nsdate));
            } catch (Exception e) {
                e.printStackTrace();
                result +="稿件创建时间格式错误："+Nsdate;
            }
        }

        //校验栏目id
        String ColumnRelID = jsonObject.getString("ColumnRelID");
        if(!StringUtils.isBlank(ColumnRelID)){
            String[] columnArray = ColumnRelID.split(",");
            for(int i=0;i<columnArray.length;i++){
                try{
                    int colID = Integer.parseInt(columnArray[i]);
                    Column col = colReader.get(colLibID, Integer.parseInt(ColumnID));
                    if(col==null){
                        result += "系统中未查询到该栏目ID:"+colID+"；";
                    }else{
                        columnRel+=","+col.getName();
                        a_columnAll +=";"+colID;
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    result +="关联栏目ID格式错误";
                    break;
                }
            }
        }

        //校验话题id
        String TopicIDs = jsonObject.getString("TopicIDs");
        JSONObject topicsJson = new JSONObject();
        if(!StringUtils.isBlank(TopicIDs)){
            String[] topicIDArray = TopicIDs.split(",");
            DocumentManager docmanager = DocumentManagerFactory.getInstance();
            int topicsLibID = LibHelper.getLibID(DocTypes.TOPICS.typeID(), Tenant.DEFAULTCODE);
            for(int i=0;i<topicIDArray.length;i++){
                try {
                    long topicID = Long.parseLong(topicIDArray[i]);
                    Document doc = docmanager.get(topicsLibID, topicID);
                    if(doc==null){
                        result +="系统中未查询到该话题ID:"+topicID+"；";
                    }else{
                        topicsJson.put(topicID,doc.get("SYS_TOPIC"));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    result +="话题ID格式错误";
                    break;
                }
            }
        }

        if(!StringUtils.isBlank(result)){
            errorCode = "107";
            resultJson.put("success",false);
            resultJson.put("infoID",InfoID);
            resultJson.put("errorCode",errorCode);
            resultJson.put("errorCause",result);

            return resultJson;
        }

        if(columnRel.length()>0){
            columnRel = columnRel.substring(1);
        }
        if(a_columnAll.length()>0){
            a_columnAll = a_columnAll.substring(1);
        }

        resultJson.put("success",true);
        resultJson.put("columnRel",columnRel);
        resultJson.put("a_columnAll",a_columnAll);
        resultJson.put("column",column);
        resultJson.put("topicsJson",String.valueOf(topicsJson));

        return resultJson;
    }


    /**
     * 处理标题图及封面图
     */
    private List<Document> checkPicData(JSONObject jsonObject) throws Exception{

        List<Document> articles = new ArrayList<>();
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        long docID = 0L;
        boolean isNew = true;
        if(jsonObject.optLong("ArticleId") == 0L){//新增
            docID = InfoHelper.getNextDocID(DocTypes.ARTICLE.typeID()); // 提前取稿件ID
        }else {//修改
            docID = jsonObject.optLong("ArticleId");
            isNew = false;
        }

        DocLib[] docLibIDs = LibHelper.getLibs(DocTypes.ARTICLE.typeID());

        for(int i=0;i<docLibIDs.length;i++){
            int docLibID = docLibIDs[i].getDocLibID();
            Document article = null;
            if(isNew){
                article = docManager.newDocument(docLibID,docID);
                ProcHelper.initDoc(article);
            }else {
                article = docManager.get(docLibID,docID);
            }
            if(article!=null){
                articles.add(article);
            }
            Column column = colReader.get(LibHelper.getColumnLibID(), jsonObject.getLong("ColumnID"));
            long colID = column.getPushColumn();
            Column columnApp = colReader.get(LibHelper.getColumnLibID(), colID);
            if(columnApp==null){//没有自动同步栏目
                break;
            }
        }
        //处理图片路径
        StorageDevice device = InfoHelper.getPicDevice();
        setStoreBasePath(InfoHelper.getDevicePath(device));
        String startTime = InfoHelper.getConfig("写稿", "稿件顺序起点日期");
        setArticleTime(startTime);
        for(int i=0;i<articles.size();i++){
            Document article = articles.get(i);

            String SmallTitlePic = jsonObject.getString("SmallTitlePic").replace(" ","+");
            String MiddleTitlePic = jsonObject.getString("MiddleTitlePic").replace(" ","+");
            String BigTitlePic = jsonObject.getString("BigTitlePic").replace(" ","+");
            String VideoPic = jsonObject.getString("VideoPic").replace(" ","+");

            convertImage("smalltitlepic",SmallTitlePic,article);
            convertImage("middletitlepic",MiddleTitlePic,article);
            convertImage("bigtitlepic",BigTitlePic,article);
            convertImage("videopic",VideoPic,article);

        }

        return articles;
    }

}
