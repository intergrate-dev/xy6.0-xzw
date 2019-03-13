package com.founder.xy.system.job;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.scheduler.BaseJob;
import com.founder.xy.article.Article;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.jpublish.PublishHelper;
import com.founder.xy.system.Tenant;
import com.founder.xy.system.site.Site;
import com.founder.xy.system.site.SiteManager;

import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 将每天发布的稿件生成XML输出到外网，供其他系统统计
 * 每10分钟更新一次
 * Created by Wenkx on 2016/9/28.
 */
public class dailyOutJob extends BaseJob {

    private String timeFormat = "yyyy-MM-dd'T'HH:mm:ss'+08:00'";
    public dailyOutJob() {
        super();
        log = Context.getLog("xy.dailyOut");
    }

    @Override
    protected void execute() throws E5Exception {
        //第一时间获取当前时间
        Timestamp nowTime = DateUtils.getTimestamp();


        log.info("-----每日发布稿件输出xml开始-----");
        String result;
        try {
            result = dailyOut(nowTime);
            log.info("-----" + result + "-----");
        } catch (Exception e) {
            log.error("-----" + e.getMessage() + "-----");
            e.printStackTrace();
        }
        log.info("-----每日发布稿件输出xml完毕-----");


    }


    private String dailyOut(Timestamp nowTime) throws Exception {
        //多站点情况
        SiteManager siteManager = (SiteManager)Context.getBean("siteManager");
        int siteLibID = LibHelper.getLibID(DocTypes.SITE.typeID(), Tenant.DEFAULTCODE);
        List<Site> siteList = siteManager.getSites(siteLibID);
        for(Site tempSite:siteList){
            //得到站点资源路径
            String[] sitePath = getsiteResPath(siteLibID,tempSite.getId());
            if( sitePath!=null && !StringUtils.isBlank(sitePath[0]) && !StringUtils.isBlank(sitePath[1]) ) {
            //读XML文件 获取并更新 lastUpdateTime
            //这里有一个BUG 如果刚好是新的一天起始，昨天最后一次更新后发布的稿件不会被统计，考虑到是凌晨，一般没有稿件发布，不再处理
                String xmlFilePath = sitePath[1] + File.separator
                        + "dailyArticles" + File.separator + DateUtils.format("yyyyMMdd") + ".xml";
                System.out.println("xmlFilePath = " + xmlFilePath);
                Timestamp lastTime = getLastUpdateTime(xmlFilePath,nowTime);
                for(int isApp = 0  ;isApp<2;isApp++ ) { //isApp : 0 WEB,1 APP
                    //获取 lastTime 到nowTime 间发布的稿件,先web,再APP
                    List<com.founder.e5.doc.Document> tenMinArts = getTenMinArts(nowTime,lastTime,tempSite.getId(), isApp);
                    if (tenMinArts!=null && tenMinArts.size() > 0) {
                        addArts(tenMinArts, xmlFilePath, isApp);
                    }
                }
                // 生成trs消息文件
                PublishHelper.writeTransPath(xmlFilePath,sitePath[0]);

            }

        }
        return "done!";
    }

    private Timestamp getLastUpdateTime(String xmlFilePath, Timestamp nowTime) throws Exception {
        Document document;
        File file = new File(xmlFilePath);
        Timestamp lastTime;
        if (!file.exists()) {
            document = DocumentHelper.createDocument();
            Element root = document.addElement("root");
            root.addElement("web");
            root.addElement("app");
            root.addAttribute("lastUpdateTime",DateUtils.format(nowTime,timeFormat));
            lastTime = Timestamp.valueOf(DateUtils.format(nowTime,"yyyy-MM-dd 00:00:00"));
        }
        else {
            document = new SAXReader().read(file);
            Element root = document.getRootElement();
            Date t0 =  DateUtils.parse(root.attribute("lastUpdateTime").getValue(),timeFormat);
            lastTime = new Timestamp(t0.getTime());
            root.attribute("lastUpdateTime").setValue(DateUtils.format(nowTime,timeFormat));
        }
        FileUtils.writeStringToFile(new File(xmlFilePath), format(document), "UTF-8");
        return lastTime;
    }

    private String[] getsiteResPath(int siteLibID, int siteID) {
    	return InfoHelper.readSiteInfo(siteID);
    }


    private List<com.founder.e5.doc.Document> getTenMinArts(Timestamp nowTime, Timestamp lastTime , int siteID, int isApp) {
        //数据库选取稿件时 按栏目 发布时间 排序
        /*int interTime = InfoHelper.getConfig( "发布服务", "生成每日稿件XML时间间隔")==null? 10 :Integer.valueOf(InfoHelper.getConfig( "发布服务", "生成每日稿件XML时间间隔"));
        Timestamp lastTime =new Timestamp(nowTime.getTime() - interTime*60*1000);*/

        int LibID = isApp==0 ? LibHelper.getArticleLibID() : LibHelper.getArticleAppLibID();
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        try {
            com.founder.e5.doc.Document[] arts = docManager.find(LibID, "a_status = ? and  a_siteid = ? and  a_pubtime between ? and ? order by a_columnID, a_pubtime",
                    new Object[]{Article.STATUS_PUB_DONE,siteID,lastTime, nowTime});
            return Arrays.asList(arts);
        } catch (E5Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void addArts(List<com.founder.e5.doc.Document> tenMinArts, String xmlFilePath, int isApp) throws Exception {
        //获取今天的XML文件，没有则新建
        Document document;
        File file = new File(xmlFilePath);
        if (!file.exists()) {
            document = DocumentHelper.createDocument();
            Element root = document.addElement("root");
            root.addElement("web");
            root.addElement("app");
        }
        else {
            document = new SAXReader().read(file);
        }
        //
        String rootEleName = isApp==0 ? "web" : "app";
        Element root = document.getRootElement().element(rootEleName);
        for(com.founder.e5.doc.Document newArt : tenMinArts){
            setPicUrl(newArt,LibHelper.getAttaLibID(),isApp);
            String xpath = "//column[columnID = " + newArt.getString("a_columnID") + "]";
            Element col = (Element) root.selectSingleNode(xpath);
            String columns = newArt.getString("a_column");
            String column = columns.lastIndexOf("~")==-1?columns:columns.substring(columns.lastIndexOf("~")+1, columns.length());
            if (col==null){
                col = root.addElement("column");
                Element columnID =  col.addElement("columnID");
                columnID.setText(newArt.getString("a_columnID"));

                Element columnName =  col.addElement("columnName");

                columnName.setText(column);
                col.addElement("articles");
            }
            Element articles = col.element("articles");
            Element article = articles.addElement("article");

            Element type = article.addElement("type");
            type.setText("0");


            Element title = article.addElement("title");
            title.addCDATA(newArt.getTopic());


            Element source = article.addElement("source");
            source.setText(newArt.getString("a_source"));

            Element writer = article.addElement("writer");
            writer.setText(newArt.getString("a_editor"));

            Element artColName = article.addElement("channel");
            artColName.setText(column);

/*            Element editor = article.addElement("editor");
            editor.setText(newArt.getString("a_editor"));*/

            Element summary = article.addElement("brief");
            summary.addCDATA(newArt.getString("a_abstract"));

            Element subTitle = article.addElement("describe");
            subTitle.addCDATA(newArt.getString("a_subTitle"));

            Element author = article.addElement("author");
            author.setText(newArt.getAuthors());

            Element keyword = article.addElement("keyword");
            keyword.setText(newArt.getString("a_keyword"));

            Element picBig = article.addElement("cover");
            picBig.setText(newArt.getString("a_picBig"));

            Element content = article.addElement("content");
            content.addCDATA(newArt.getString("a_content"));

            Element pubTime = article.addElement("date");
            pubTime.setText(newArt.getString("a_pubTime").substring(0,20));

            Element multimediaLink = article.addElement("videourl");
            multimediaLink.setText(newArt.getString("a_multimediaLink"));

            Element contexturl = article.addElement("contexturl");
            contexturl.setText(newArt.getString("a_url"));

            Element urlPad = article.addElement("urlPad");
            urlPad.setText(newArt.getString("a_urlPad"));

            Element resId = article.addElement("resId");
            resId.setText(String.valueOf(newArt.getDocID()));

            Element resUrl = article.addElement("resUrl");
            resUrl.setText(newArt.getString("a_url"));
        }
        //稿件按栏目分类，分别插入到相应的XML栏目节点中
        FileUtils.writeStringToFile(new File(xmlFilePath), format(document), "UTF-8");
    }


    //格式化输出xml
    private String format(Document doc) throws Exception {
        // 输出格式
        OutputFormat formater = OutputFormat.createPrettyPrint();
        formater.setEncoding("UTF-8");
        // 输出(目标)
        StringWriter out = new StringWriter();
        XMLWriter writer = new XMLWriter(out, formater);
        // 输出格式化的串到目标中，执行后格式化后的串保存在out中。
        writer.write(doc);
        writer.close();

        String result = out.toString();
        out.close();

        return result;
    }


    //设置图片的url
    private void setPicUrl(com.founder.e5.doc.Document a, int attLibID, int isApp) {


        DocumentManager docManager = DocumentManagerFactory.getInstance();
        try {
            com.founder.e5.doc.Document[] atts = docManager.find(attLibID, "att_articleID=? and att_articleLibID=?",
                    new Object[]{a.getDocID(), a.getDocLibID()});
            if (atts != null) {
                for (com.founder.e5.doc.Document att : atts) {
                    String url = (isApp == 0) ? att.getString("att_url") : att.getString("att_urlPad");

                    int type = att.getInt("att_type");

                    if (type == 2) a.set("a_picBig",url);
                    else if (type == 3) a.set("a_picMiddle",url);
                    else if (type == 4) a.set("a_picSmall",url);
                        //替换正文中的图片地址
                    else if(type==0){
                        String path = att.getString("att_path");
                        String content = a.getString("a_content").replace("../../xy/image.do?path="+path, url);
                        a.set("a_content",content);
                    }
                }
            }
        } catch (E5Exception e) {
            e.printStackTrace();
        }
    }

}