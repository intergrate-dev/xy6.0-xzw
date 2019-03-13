package com.founder.xy.system.job;

import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.scheduler.BaseJob;
import com.founder.xy.article.Article;
import com.founder.xy.column.Column;
import com.founder.xy.column.ColumnReader;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.jpublish.PublishHelper;
import com.founder.xy.system.Tenant;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FacebookArticlesJob extends BaseJob {

    @Autowired
    private ColumnReader colReader;

    public FacebookArticlesJob() {
        super();
        log = Context.getLog("xy.facebookArticlesJob");
    }

    @Override
    protected void execute() throws E5Exception {
        log.info("---开始生成推送Facebook稿件文件任务---");

        createFacebookArticlesJson();

        log.info("---本轮生成推送Facebook稿件任务完成---");
    }

    private void createFacebookArticlesJson() {
        int articleLib = LibHelper.getArticleLibID();
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        JSONArray jsonArray = new JSONArray();
        long siteID = 1;

        String filterColumnIDs = InfoHelper.getConfig("其它","facebook稿件筛选栏目ID");
        List<Long> columnIDList = null;
        String sql = "";
        if(!StringUtils.isBlank(filterColumnIDs)&&!"0".equals(filterColumnIDs)){//需要筛选
            columnIDList = new ArrayList<>();
            getColumnIDs(filterColumnIDs, columnIDList);
            String columnIDs_sql = String.valueOf(columnIDList).replace("[","(").replace("]",")");
            System.out.println("筛选去除稿件栏目ID:"+columnIDs_sql);
            sql = "a_status=? and a_type=? and SYS_DELETEFLAG =0 "
                    + " and a_columnID not in " + columnIDs_sql
                    +" ORDER BY a_pubTime DESC,SYS_DOCUMENTID DESC LIMIT 0,100";
        }else{
            sql = "a_status=? and a_type=? and SYS_DELETEFLAG =0 ORDER BY a_pubTime DESC,SYS_DOCUMENTID DESC LIMIT 0,100";
        }

        Document[] docList = new Document[0];
        try {
            docList = docManager.find(articleLib,sql,
                    new Object[]{Article.STATUS_PUB_DONE,Article.TYPE_ARTICLE});
            if (docList!=null){
                System.out.println("查询出稿件数:"+docList.length);
            }
        } catch (E5Exception e) {
            e.printStackTrace();
        }

        for(Document document:docList){
                try {
                    JSONObject articleJson = new JSONObject();
                    articleJson.put("source",document.getString("a_source"));//文章来源
                    articleJson.put("title_section",document.getString("a_column"));//主栏目名称
                    articleJson.put("columnID",document.getString("a_columnID"));//主栏目ID
                    articleJson.put("title",document.getString("SYS_TOPIC"));//标题
                    articleJson.put("desc",document.getString("a_abstract"));//摘要
                    articleJson.put("link",document.getString("a_url"));//pc发布路径
                    articleJson.put("linkPad",document.getString("a_urlPad"));//触屏发布路径

                    Date created = document.getCreated();
                    Date publishTime = document.getTimestamp("A_PUBTIME");
                    Date lastModified = document.getLastmodified();
                    articleJson.put("created",created.getTime()/1000);//稿件创建时间
                    articleJson.put("publishTime",publishTime.getTime()/1000);//稿件发布时间
                    articleJson.put("lastModified",lastModified.getTime()/1000);//稿件最后修改时间

                    getArticleBodyAndImage(articleJson,document);

                    siteID = document.getLong("a_siteID");
                    jsonArray.add(articleJson);
                } catch (E5Exception e) {
                    e.printStackTrace();
                    continue;
                }
        }

        try {
            if(jsonArray.size()>0){
                createJsonFile(jsonArray,siteID);
            }
        } catch (E5Exception e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void getColumnIDs(String filterColumnIDs,List<Long> idList) {
        filterColumnIDs = filterColumnIDs.replace("，",",");
        String[] columnIDs = filterColumnIDs.split(",");
        int columnIDsLength = columnIDs.length;

        int colLibID = LibHelper.getLibID(DocTypes.COLUMN.typeID(),Tenant.DEFAULTCODE);
        for(int i=0;i<columnIDsLength;i++){
            long columnID = 0L;
            try{
                columnID = Long.parseLong(columnIDs[i]);
            }catch (Exception e){
                e.printStackTrace();
                continue;
            }
            if (!idList.contains(columnID)){
                idList.add(columnID);
            }
            getSubColumnID(columnID,colLibID,idList);
        }
    }

    private void getSubColumnID(long columnID,int colLibID,List<Long> idList) {
        try {
            if(colReader == null){
                colReader = (ColumnReader) Context.getBean("columnReader");
            }
            List<Column> subColumnList = colReader.getSub(colLibID,columnID);
            if(subColumnList!=null){
                int listSize = subColumnList.size();
                for(int i=0;i<listSize;i++){
                    long subColumID = subColumnList.get(i).getId();
                    if(!idList.contains(subColumID)){
                        idList.add(subColumID);
                    }
                    getSubColumnID(subColumID,colLibID,idList);
                }
            }
        } catch (E5Exception e) {
            e.printStackTrace();
        }
    }

    private void createJsonFile(JSONArray jsonArray, long siteID) throws E5Exception, IOException {
        //获取资源目录配置
        String root = InfoHelper.getConfig("发布服务", "发布根目录");

        String filePath = "";
        String osName = System.getProperties().getProperty("os.name");
        if(osName.indexOf("Window")!=0) {//linux
            filePath = root + "/fbia/merge/news_all.json";
        }else{
            filePath = "c:\\" + "newsjson\\news_all.json";;
        }

        FileUtils.writeStringToFile(new File(filePath), String.valueOf(jsonArray), "UTF-8");

        if(osName.indexOf("Window")!=0) {//linux
            //trans：生成分发信息文件
            PublishHelper.writeTransPath(filePath, root);
        }
    }

    private void getArticleBodyAndImage(JSONObject articleJson, Document document) throws E5Exception {
        //获取稿件对应list
        int docLibID = document.getDocLibID();
        int attLibId = LibHelper.getAttaLibID();
        long docID = document.getDocID();

        DocumentManager docManager = DocumentManagerFactory.getInstance();
        Document[] documents = docManager.find(attLibId,
                    "att_articleID=? and att_articleLibID=? and att_type =0 order by att_type asc,SYS_DOCUMENTID asc ",new Object[]{docID,docLibID});

        String content = document.getString("a_content");

        //处理返回字段image
        org.jsoup.nodes.Document contentHtml = Jsoup.parse(content);
        Elements images = contentHtml.select("img");
        String url = "";
        if(images!=null&&images.size()>0){
            Element img = images.get(0);
            url = img.attr("src").replace("../../xy/image.do?path=","");//内网图片
        }

        String image ="";
        for(Document docAtt:documents){
            String attPath = docAtt.getString("att_path");
            String attUrl = docAtt.getString("att_url");
            String attUrlPad = docAtt.getString("att_urlPad");

            String OutnetUrl = StringUtils.isBlank(attUrl)?StringUtils.getNotNull(attUrlPad):attUrl;
            if(url.equals(attPath)){
                image = OutnetUrl;
            }

            content = content.replace("../../xy/image.do?path="+attPath,OutnetUrl);
        }

        articleJson.put("body",content);
        articleJson.put("image",image);
    }
}
