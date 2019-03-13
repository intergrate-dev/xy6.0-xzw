package com.founder.xy.jpublish.paper;

import com.founder.e5.commons.ArrayUtils;
import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.Log;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.doc.util.E5docHelper;
import com.founder.xy.article.Article;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.jms.data.DocIDMsg;
import com.founder.xy.jpublish.PublishHelper;
import com.founder.xy.jpublish.data.Attachment;
import com.founder.xy.jpublish.data.PubArticle;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;
import com.founder.xy.system.Tenant;
import com.founder.xy.template.Template;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 数字报撤稿
 * 撤版
 * 撤指定版面及对应稿件（删除所有文件）
 * <p>
 * a根据发布规则找到版面对应文件，写空文件 生成trans消息文件
 * b找到属于此版面的稿件进行发布撤稿消息
 * c找到对应版面设置撤稿标记
 * <p>
 * <p>
 * 撤稿
 * 撤指定稿件（删除所有文件）
 * <p>
 * a找到对应的稿件附件
 * b根据发布规则找到稿件对应文件，写空文件 生成trans消息文件
 * c找到对应稿件设置撤稿标记
 * <p>
 * <p>
 * 撤当期报纸
 * <p>
 * a依次发布撤版消息
 * b修改papersInfo、period.xml等文件
 * <p>
 * Created by Wenkx on 2017/6/23.
 */
public class PaperRevoke {
    private PaperContext paperContext;
    private Log log = Context.getLog("xy.publish");

    /**
     * Generator boolean.
     *
     * @param data the data
     * @return the int
     */
    public boolean generator(DocIDMsg data) {

        switch (data.getType()) {
            case DocIDMsg.TYPE_REVOKE_PAPER:
                revokePaper(data);
                break;
            case DocIDMsg.TYPE_REVOKE_PAPER_LAYOUT:
                revokeLayout(data);
                break;
            case DocIDMsg.TYPE_REVOKE_PAPER_ARTICLE:
                revokePaperArticle(data);
                break;
        }
        return true;
    }

    /**
     * 报纸只有删除操作没有撤回操作
     * 撤一期报纸 同时删除报纸数据
     *
     * @param data 撤报纸消息
     */
    private void revokePaper(DocIDMsg data) {
        long layoutID = data.getDocID();
        String paperDate = data.getRelIDs();
        DocIDMsg repubData = new DocIDMsg(data.getDocLibID(), layoutID, paperDate);
        paperContext = new PaperContext();
        paperContext.init(repubData, log);
        List<PaperLayout> layouts = paperContext.getLayouts();
        for (int index = 0; index < 2; index++) {
            for (PaperLayout layout : layouts) {
                //撤掉对应版面
                revokeLayout(layout, index);
                changeStatus(layout.getDocLibID(),layout.getId(),"pl_status",0);
            }
            // 撤掉首页文件
            revokeIndexFile(index);
            //更新有关的xml文件
            deleteXmlNode();
        }
        //设置本期报纸状态
       // changePaperStatus();
        //删除本期报纸内网文件，数据库数据
        deleteDate();
    }

    /**
     * 删除本期报纸内网文件，数据库数据
     */
    private void deleteDate()  {
        PaperLayout paperLayout1 = paperContext.getLayouts().get(0);
        int paperID = paperLayout1.getPaperID();
        Date periodDate = paperLayout1.getDate();
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        //查出该期报纸的所有版面ID
        DBSession conn = null;
        int layoutLibID = LibHelper.getLibIDByOtherLib(DocTypes.PAPERLAYOUT.typeID(), paperContext.getMessage().getDocLibID());
        try {
            conn = E5docHelper.getDBSession(layoutLibID);
            conn.beginTransaction();
            List<String> deleteList = new ArrayList<>();
            String pDate = DateUtils.format(periodDate, "yyyyMMdd");
            for (PaperLayout paperLayout: paperContext.getLayouts()) {
                 //分别删除每个版面
                deleteLayout(layoutLibID, paperLayout.getId(), paperID, pDate, conn, docManager, deleteList);
            }
            //删除报纸日期
            String dateLib = LibHelper.getLibTable(LibHelper.getLibIDByOtherLib(DocTypes.PAPERDATE.typeID(),layoutLibID));
            String delDate = "delete from " + dateLib + " where pd_paperID=? and pd_date=?";
            InfoHelper.executeUpdate(delDate, new Object[]{paperID,periodDate}, conn);

            conn.commitTransaction();
            clear(deleteList);
        } catch (Exception e) {
            ResourceMgr.rollbackQuietly(conn);
            log.error(e);
        } finally {
            ResourceMgr.closeQuietly(conn);
        }

        RedisManager.clear(RedisKey.APP_PAPER_DATE_KEY+paperID);
    }

    /**
     * 修改报纸状态
     */
    private void changePaperStatus() {
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        int dateLibID = LibHelper.getLibIDByOtherLib(DocTypes.PAPERDATE.typeID(),
                paperContext.getMessage().getDocLibID());
        try {
            Document[] dates = docManager.find(dateLibID, "pd_paperID=? and pd_date=?",
                    new Object[]{paperContext.getMessage().getDocID(),
                            DateUtils.parse(paperContext.getMessage().getRelIDs(), "yyyyMMdd")
                    });
            if (dates.length > 0) {
                dates[0].set("pd_status", 7);
                docManager.save(dates[0]);
            }
        } catch (E5Exception e) {
            log.error(e);
        }
    }

    /**
     * 撤版
     *
     * @param data 撤版消息
     */
    private void revokeLayout(DocIDMsg data) {
        Document layoutDoc = getDocumnet(data);
        int paperLibID = LibHelper.getLibIDByOtherLib(DocTypes.PAPER.typeID(), data.getDocLibID());
        DocIDMsg repubData = new DocIDMsg(paperLibID, layoutDoc.getInt("pl_paperID"), data.getRelIDs());
        paperContext = new PaperContext();
        paperContext.init(repubData, log);
        for (PaperLayout paperLayout : paperContext.getLayouts()) {
            //从paperContext找到要撤的PaperLayout 不用再次去找附件和稿件内容
            if (paperLayout.getId() == layoutDoc.getDocID())
                for (int index = 0; index < 2; index++) {
                    revokeLayout(paperLayout, index);
                    // 版面状态设置为已撤版
                    changeStatus(paperLayout.getDocLibID(),paperLayout.getId(),"pl_status",7);
                }
        }
        //重发报纸
        //PublishHelper.paperPublish(repubData);
    }

    /**
     * 撤稿
     *
     * @param data 撤稿消息
     */
    private void revokePaperArticle(DocIDMsg data) {
        Document article = getDocumnet(data);
        Document paperLayout = getDocument(LibHelper.getLibIDByOtherLib(DocTypes.PAPERLAYOUT.typeID(), data.getDocLibID()), article.getLong("a_layoutID"));
        int paperLibID = LibHelper.getLibIDByOtherLib(DocTypes.PAPER.typeID(), data.getDocLibID());
        DocIDMsg repubData = new DocIDMsg(paperLibID, paperLayout.getInt("pl_paperID"), data.getRelIDs());
        paperContext = new PaperContext();
        paperContext.init(repubData, log);
        //找到对应的稿件（包括转版的）
        List<PaperArticle> paperArticles = getPaperArticles(article);
        for(PaperArticle paperArticle :paperArticles) {
            for (int index = 0; index < 2; index++) {
                revokePaperArticle(paperArticle, index);
            }
            // 稿件状态设置为已下版
            changeStatus(paperArticle.getDocLibID(), paperArticle.getId(), "a_status", 7);
        }
        //重发版面
        //PublishHelper.paperPublish(new DocIDMsg(paperLibID, paperLayout.getInt("pl_paperID"), null));
    }

    /**
     * 撤掉一个版面
     *
     * @param layout 被撤的版面
     * @param index 渠道 0 web ;1 wap
     */
    private void revokeLayout(PaperLayout layout, int index) {
        if(layout.getStatus() != Article.STATUS_PUB_DONE)
            return;
        //找到对应稿件 撤稿
        List<PaperArticle> articles = layout.getArticles();
        for (PaperArticle article : articles) {
            revokePaperArticle(article, index);
            // 稿件状态设置为未发布
            changeStatus(article.getDocLibID(),article.getId(),"a_status",0);
        }
        // 撤版面附件
        for (Attachment att : layout.getAttachments()) {
            revokeAttachment(att, index);
        }
        // 撤版面文件
        revokeLayoutFile(layout, index);
        //清除redisKey
        RedisManager.clear(RedisKey.APP_PAPER_LAYOUT_KEY + layout.getPaperID() + "." + paperContext.getMessage().getRelIDs());
        RedisManager.clear(RedisKey.APP_PAPER_ARTICLELIST_KEY + layout.getId());
    }

    /**
     * 撤报纸首页
     * @param index 渠道 0 web ;1 wap
     */
    private void revokeIndexFile(int index) {
        //无首页模板，不处理
        Template template = (index == 0) ? paperContext.getTemplateHome() : paperContext.getTemplateHomePad();
        if (template == null) return ;

        String layoutDir = (index == 0) ? paperContext.getPageDir("column") :paperContext.getPageDir("columnPad");
        //首页index.html放在版面同目录下
        String fileName = "index." + getSuffix(template);
        String filePath = layoutDir + fileName;
        overwrite(filePath);
    }


    //由模板确定的发布文件的后缀，html/json/xml
    private String getSuffix(Template t) {
        if (t == null || StringUtils.isBlank(t.getFileType()))
            return "html";
        else
            return t.getFileType();
    }

    /**
     * 更新报纸xml文件
     *
     */
    private void deleteXmlNode() {
        List<PaperLayout> layouts = paperContext.getLayouts();
        if (layouts.size() == 0) return;

        PaperLayout layout = layouts.get(0);

        //得到存放路径，不包括日一级路径
        String savePaths[] ={ paperContext.pageDirMap.get("column") ,paperContext.pageDirMap.get("columnPad")};
        PeriodFileDealer periodFileDealer = new PeriodFileDealer();
        String jsfilePath =paperContext.getPageDir("root") + File.separator+"papersInfo"+File.separator+ DateUtils.format(layouts.get(0).getDate(),"yyyyMMdd")+".js";
        String filePath =paperContext.getPageDir("root") + File.separator+"papersInfo"+File.separator+ DateUtils.format(layouts.get(0).getDate(),"yyyyMMdd")+".xml";

        try {
            String content;
            for(String savePath : savePaths){

                int pos = savePath.lastIndexOf("/", savePath.lastIndexOf("/") - 1);
                savePath = savePath.substring(0, pos) +"/period.xml";
                System.out.println("savePath = " +savePath);
                content= periodFileDealer.deletePeriodNode(layout,savePath);
                if (!StringUtils.isBlank(content)) {
                    pushFile(content, savePath);
                    log.info("更新period.xml：" + savePath);
                }
            }
            content = periodFileDealer.deletePaperinfo( layout,filePath);
            if (!StringUtils.isBlank(content)) {
                System.out.println("filePath = " + filePath);
                pushFile(content, filePath);
                pushFile("paperinfo(\""+content.replaceAll("\n","").replaceAll("\"","\\\\\"")+"\")", jsfilePath);
                log.info("更新paperInfo：" + filePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改文档状态
     * @param docLibID 文档库ID
     * @param docID 文档ID
     * @param field 字段名
     * @param value 字段值
     */

    private void changeStatus(int docLibID, long docID, String field, int value) {
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        try {
            Document doc = docManager.get(docLibID, docID);
            doc.set(field,value);
            docManager.save(doc);
        } catch (Exception e) {
            log.error("保存文档异常：" + e.getLocalizedMessage(), e);
        }
    }

    /**
     * 撤版面文件
     * @param layout 版面
     * @param index 渠道 0 web ;1 wap
     */

    private void revokeLayoutFile(PaperLayout layout, int index) {
        String url = layout.getUrl();
        String pubRuleUrl = paperContext.getPageUrl("column");
        String pubRulePath = paperContext.getPageDir("column");
        if (index == 1) {
            url = layout.getUrlPad();
            pubRuleUrl = paperContext.getPageUrl("columnPad");
            pubRulePath = paperContext.getPageDir("columnPad");
        }
        if(StringUtils.isNotBlank(url)) {
            String filePath = getFilePath(url, pubRuleUrl, pubRulePath);
            overwrite(filePath);
        }
    }





    /**
     * 根据稿件ID,在paperContext里面找到对应稿件 不用再次去找附件
     *
     * @param article 稿件 Document
     * @return 稿件PaperArticle
     */
    private List<PaperArticle> getPaperArticles(Document article) {
        int layoutID = article.getInt("A_LAYOUTID");
        PaperArticle art = null;
        for (PaperLayout paperLayout : paperContext.getLayouts()) {
            if (paperLayout.getId() == layoutID) {
                for (PaperArticle paperArticle : paperLayout.getArticles()) {
                    if (paperArticle.getId() == article.getDocID()) {
                        art =  paperArticle;
                    }
                }
            }
        }
        List <PaperArticle> result = new ArrayList<>();
        if(art!=null && art.getTransStatus()==1){
            for (PaperLayout paperLayout : paperContext.getLayouts()) {
                    for (PaperArticle paperArticle : paperLayout.getArticles()) {
                        if (ArrayUtils.contains(art.getTransArticleIDs(),paperArticle.getId())) {
                            result.add(paperArticle);
                        }
                    }
            }
        }
        else result.add(art);
        return result;
    }


    /**
     * @param data 撤稿消息
     * @return 文档 Document
     */
    private Document getDocumnet(DocIDMsg data) {
        return getDocument(data.getDocLibID(), data.getDocID());
    }

    private Document getDocument(int docLibID, long docID) {
        //从数据库中读当前版
        Document doc;
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        try {
            doc = docManager.get(docLibID, docID);
        } catch (Exception e) {
            log.error("读文档异常：" + e.getLocalizedMessage(), e);
            return null;
        }
        return doc;
    }

    /**
     * 撤掉一篇稿件
     *
     * @param article 被撤的稿件
     * @param index 渠道 0 web ;1 wap
     */
    private void revokePaperArticle(PaperArticle article, int index) {
        // 撤附件
        for (Attachment att : article.getAttachments()) {
            revokeAttachment(att, index);
        }
        //撤稿件
        revokeArticleFile(article, index);
        //删redis
        RedisManager.clear(RedisKey.APP_PAPER_ARTICLE_KEY + article.getId());

    }
    /**
     * 撤掉稿件页面
     *
     * @param article 被撤的稿件
     * @param index 渠道 0 web ;1 wap
     */
    private void revokeArticleFile(PaperArticle article, int index) {
        //撤稿件文件
        String url = article.getUrl();
        String pubRuleUrl = paperContext.getPageUrl("article");
        String pubRulePath = paperContext.getPageDir("article");
        if (index == 1) {
            url = article.getUrlPad();
            pubRuleUrl = paperContext.getPageUrl("articlePad");
            pubRulePath = paperContext.getPageDir("articlePad");
        }
        if(StringUtils.isNotBlank(url)) {
            String filePath = getFilePath(url, pubRuleUrl, pubRulePath);
            overwrite(filePath);
        }
    }

    /**
     *      *根据url、发布规则 找出稿件文件的目录地址
     * url是：http://172.19.33.95/content/201511/17/c1209.shtml
     * 发布规则的发布路径是：http://172.19.33.95/content
     * 截掉前缀后，得到相对路径和文件名
     * @param url url
     * @param pubRuleUrl 发布规则URL
     * @param pubRulePath  发布规则文件路径
     * @return 具体的文件目录地址
     */

    private String getFilePath(String url, String pubRuleUrl, String pubRulePath) {

        String relPath = url.substring(pubRuleUrl.length());
        return   pubRulePath + relPath;
    }

    /**
     * 撤掉附件文件
     *
     * @param att 被撤的附件
     * @param index 渠道 0 web ;1 wap
     */

    private void revokeAttachment(Attachment att, int index) {
        //若附件是外网图片，不需要删除
        if (att.getPath().toLowerCase().startsWith("http")) return;
        String picPath = paperContext.getPageDir("pic");
        String attPath = paperContext.getPageDir("att");
        if (index == 1) {
            picPath = paperContext.getPageDir("picPad");
            attPath = paperContext.getPageDir("attPad");
        }
        if(StringUtils.isNotBlank(attPath)) {
            if (att.getType() == Article.ATTACH_PIC || att.getType() == Article.ATTACH_LAYOUT_PIC) {
                overwrite(picPath + att.getFileName());
                overwrite(picPath + att.getFileName() + ".0");
                overwrite(picPath + att.getFileName() + ".1");
                overwrite(picPath + att.getFileName() + ".2");
            } else {
                overwrite(attPath + att.getFileName());
            }
        }
    }

    private void overwrite(String filePath) {
        //用空字符串写文件
        String siteWebRoot = paperContext.getPageDir("root");
        if (rewriteFile(filePath, siteWebRoot)) {
            //trans分发信息
            PublishHelper.writeTransPath(filePath, siteWebRoot);

            log.info("撤回" + filePath);
        }
    }

    //用空字符串写文件
    private boolean rewriteFile(String filePath, String siteWebRoot) {
        try {

            //不再判断内网是否有稿件页面文件，升级翔宇时老稿件没有内网页面

            File reFile = new File(siteWebRoot + File.separator + "deleted" + paperContext.getPaper().getSiteID() + ".html");
            if(!reFile.exists())
                reFile = new File(siteWebRoot + File.separator + "deleted.html");
            //System.out.println("reFilePath = "+reFilePath);
            if (reFile.exists()) {
                FileUtils.copyFile(reFile, new File(filePath));
            } else {
                FileUtils.writeStringToFile(new File(filePath), " ", false);
            }
            return true;
        } catch (IOException e) {
            System.out.println("撤稿覆盖空白文件时错误:" + filePath + "." + e.getLocalizedMessage());
        }
        return false;
    }

    //把文件存储到外网
    private int pushFile(String pageContent,String pathName) throws Exception{
        if(StringUtils.isEmpty(pathName)){
            log.error("发布路径为空，发布失败！");
            return PubArticle.ERROR_NO_PUBDIR;
        }
        FileUtils.writeStringToFile(new File(pathName), pageContent, "UTF-8");

        //trans：生成分发信息文件
        String root = paperContext.getPageDir("root");
        PublishHelper.writeTransPath(pathName, root);

        return PubArticle.SUCCESS;
    }


    //删除版面、版面附件、版面稿件、版面稿件附件
    private void deleteLayout(int layoutLibID, long layoutID, int paperID, String paperDate
            , DBSession conn, DocumentManager docManager, List<String> deleteList) throws Exception{
        String tenantCode = LibHelper.getTenantCodeByLib(layoutLibID);
        //取出版面下的稿件
        int articleLibID = LibHelper.getLibIDByOtherLib(DocTypes.PAPERARTICLE.typeID(), layoutLibID);

        Document[] articles = docManager.find(articleLibID, "a_layoutID=?", new Object[]{layoutID});

        //删除附件的SQL
        String attLib = LibHelper.getLibTable(DocTypes.PAPERATTACHMENT.typeID(), tenantCode);
        String delAtt = "delete from " + attLib + " where att_articleID=? and att_articleLibID=?";

        try {
            docManager.delete(layoutLibID, layoutID, conn); //删除版面
            InfoHelper.executeUpdate(delAtt,new Object[]{layoutID, layoutLibID}, conn);//删除版面附件

            //删除版面下的稿件和稿件的附件
            for (Document article : articles) {
                getFilesToDelete(article.getDocLibID(), article.getDocID(), deleteList, docManager);
                docManager.delete(article.getDocLibID(), article.getDocID(), conn);
                InfoHelper.executeUpdate(delAtt,new Object[]{article.getDocID(), article.getDocLibID()}, conn);
            }
        } catch (Exception e) {
            throw new E5Exception(e);
        }
    }

    private void getFilesToDelete(int docLibID, long docID,List<String> deleteList,DocumentManager docManager) throws E5Exception {
        int attLib = LibHelper.getLibID(DocTypes.PAPERATTACHMENT.typeID(), Tenant.DEFAULTCODE) ;
        Document[] atts = docManager.find(attLib, "att_articleID=? and att_articleLibID=?", new Object[]{docID,docLibID});
        for(Document att : atts){
            deleteList.add(att.getString("att_path"));
        }
    }

    //删掉没被引用的图片文件，同时删掉抽图文件
    private void clear(List<String> deleteList) {
        for(String path : deleteList){
            File file = new File(InfoHelper.getFilePathInDevice(path));
            try {
                if (file.exists()) file.delete();

                String fileName = file.getCanonicalPath();

                file = new File(fileName + ".2");
                if (file.exists()) file.delete();

                file = new File(fileName + ".2.jpg");
                if (file.exists()) file.delete();

                file = new File(fileName + ".1");
                if (file.exists()) file.delete();

                file = new File(fileName + ".1.jpg");
                if (file.exists()) file.delete();

                file = new File(fileName + ".0");
                if (file.exists()) file.delete();

                file = new File(fileName + ".0.jpg");
                if (file.exists()) file.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
