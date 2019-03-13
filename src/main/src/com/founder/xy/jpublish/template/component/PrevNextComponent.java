package com.founder.xy.jpublish.template.component;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.CacheReader;
import com.founder.xy.column.Column;
import com.founder.xy.column.ColumnReader;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.jpublish.BaseDataCache;
import com.founder.xy.system.site.SiteRule;
import com.founder.xy.template.Template;

import org.json.JSONObject;

import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.xy.jpublish.ColParam;
import com.founder.xy.jpublish.data.BareArticle;
import com.founder.xy.jpublish.data.PubArticle;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 组件：上一篇下一篇
 */
public class PrevNextComponent extends AbstractComponent implements Component {
	public PrevNextComponent(ColParam param,JSONObject coJson) {
		super(coJson);
		this.param = param;
	}

	public String getComponentResult() throws Exception {
		getComponentData();
		return process();
	}

	protected void getComponentData() {
		PubArticle article = getArticle();
		if (article == null) return;
		
		int articleLibID = getArticleLibID();
        int attLibID = LibHelper.getLibIDByOtherLib(DocTypes.ATTACHMENT.typeID(), articleLibID);

		String tableName = getRelTableName(articleLibID); //取稿件库对应的关联表名
        PubArticle nextArt = calculatNextArt(article,tableName);
		if (nextArt != null) {
            if( StringUtils.isBlank(nextArt.getUrl())||StringUtils.isBlank(nextArt.getUrlPad())) {
                getUrl(nextArt);//地址为空的话 根据发布规则和模板后缀拼出地址
            }
            setPicUrl(nextArt, attLibID);//标题图片的url
			componentData.put("nextArt", nextArt);
		}

        PubArticle prevArt = calculatPrevArt(article,tableName);
		if (prevArt != null) {
            if( StringUtils.isBlank(prevArt.getUrl())||StringUtils.isBlank(prevArt.getUrlPad())) {
                getUrl(prevArt);//地址为空的话 根据发布规则和模板后缀拼出地址
            }
            setPicUrl(prevArt, attLibID);//标题图片的url
			componentData.put("prevArt", prevArt);
		}
	}



    protected PubArticle calculatNextArt(PubArticle article,String tableName){
		//从稿件所在主栏目的列表中取下一个
		String sql = " SELECT SYS_DOCUMENTID FROM "+tableName
				+ " WHERE CLASS_1=? and a_order=("
				+ " SELECT max(a_order) FROM "+tableName
				+ " WHERE CLASS_1=?"
				+ " AND a_order<(SELECT a_order FROM "+tableName+" WHERE SYS_DOCUMENTID=? and CLASS_1=?)"
				+ " AND a_status in (1,3))";
		return findArticle(sql, article);
	}
	
	protected PubArticle calculatPrevArt(PubArticle article,String tableName){
		//从稿件所在主栏目的列表中取上一个
		String sql = " SELECT SYS_DOCUMENTID FROM "+tableName
			+ " WHERE CLASS_1=? and a_order=("
			+ " SELECT min(a_order) FROM "+tableName
			+ " WHERE CLASS_1=?"
			+ " AND a_order>(SELECT a_order FROM "+tableName+" WHERE SYS_DOCUMENTID=? and CLASS_1=?)"
			+ " AND a_status in (1,3))";
		return findArticle(sql, article);
	}

	private PubArticle findArticle(String sql, PubArticle article){
		long nextArtID = 0;
		
		DBSession conn = null;
		IResultSet rs = null;
		try {
			conn = Context.getDBSession();
			rs = conn.executeQuery(sql, 
					new Object[] {article.getColumnID(), article.getColumnID(), 
					article.getId(), article.getColumnID()});
			if (rs.next()) {
				nextArtID = rs.getLong("SYS_DOCUMENTID");
			} else {
				return null;
			}
		} catch (Exception exp) {
			exp.printStackTrace();
			return null;
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}
		
		Document articleDoc = null;
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			articleDoc = docManager.get(getArticleLibID(), nextArtID);
		} catch (E5Exception e) {
			e.printStackTrace();
		}
		return (articleDoc != null) ? new PubArticle(articleDoc) : null;
	}

	
	
    private void getUrl(PubArticle article) {

        Column column= readColumn(article);
        SiteRule[] pubRules = getPubRules(column,article.getDocLibID());
		String format = InfoHelper.getConfig( "发布服务", "稿件日期目录格式")==null?"yyyyMM/dd":InfoHelper.getConfig( "发布服务", "稿件日期目录格式");
		String datePath = DateUtils.format(article.getPubTime(), format);
        Template[] template = readTemplate(column, article);
        String urlWeb = getArticleUrl(pubRules[0], datePath, template[0],article);
        String urlPad = getArticleUrl(pubRules[1], datePath, template[1],article);
        if(StringUtils.isBlank(article.getUrl()))
            article.setUrl(urlWeb);
        if(StringUtils.isBlank(article.getUrlPad())){
            article.setUrlPad(urlPad);
        }

    }
	

	private String getArticleUrl(SiteRule siteRule, String datePath, Template template, PubArticle nearArticle){
		if (siteRule == null || template == null) return "";
		String prefix = InfoHelper.getConfig( "发布服务", "稿件生成页前缀");

		StringBuffer url = new StringBuffer();
		if(siteRule != null){
			url.append(siteRule.getArticleDir());
			if(siteRule.isArticleByDate()){
				url.append("/" + datePath);
			}
			if (prefix == null || "".equals(prefix)) {
				url.append("/c" + nearArticle.getId() + "." + getSuffix(template));
			} else {if(prefix.contains("$"))
				prefix = parserPrefix(prefix,nearArticle);
				url.append("/" + prefix + nearArticle.getId() + "." + getSuffix(template));
			}

		}
		return url.toString();
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private String parserPrefix(String prefix, PubArticle nearArticle) {
		Map map = new HashMap();
		//先把栏目ID和栏目名称放进去，以后看需求添加
		map.put("article.columnID", nearArticle.getColumnID());
		map.put("article.column", nearArticle.getColumn());

		String regex = "\\$\\{([^\\}]+)\\}";

		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(prefix);

		String g;
		while (m.find()) {
			g = m.group(1);
			if(!StringUtils.isBlank(g) && map.containsKey(g))
				prefix = m.replaceAll(map.get(g) + "");
			else prefix = m.replaceAll("");
			m = p.matcher(prefix);
		}
		return prefix;
	}

	/**
	 * 由模板确定的发布文件的后缀，html/json/xml
	 */
	protected String getSuffix(Template t) {
		if (t == null || StringUtils.isBlank(t.getFileType()))
			return "html";
		else
			return t.getFileType();
	}


    //读出主栏目
    private Column readColumn(BareArticle article) {
        try {
            int colLibID = LibHelper.getLibIDByOtherLib(DocTypes.COLUMN.typeID(), article.getDocLibID());

            ColumnReader columnReader = (ColumnReader)Context.getBean("columnReader");
            return columnReader.get(colLibID, article.getColumnID());
        } catch (E5Exception e) {
            System.out.println("读栏目信息时出错：" + e.getLocalizedMessage());
        }
        return null;
    }


    protected String getRootPath(int siteID, int refLibID) {
        int libID = LibHelper.getLibIDByOtherLib(DocTypes.SITE.typeID(), refLibID);

        BaseDataCache siteCache = (BaseDataCache) (CacheReader.find(BaseDataCache.class));
        return siteCache.getSiteWebRootByID(libID, siteID);
    }

    //按栏目读发布规则
    private SiteRule[] getPubRules(Column column, int docLibID) {
        SiteRule[] pubRules = new SiteRule[2];
        int libID = LibHelper.getLibIDByOtherLib(DocTypes.SITERULE.typeID(), docLibID);

        BaseDataCache siteRuleCache = (BaseDataCache) (CacheReader.find(BaseDataCache.class));
        pubRules[0] = siteRuleCache.getSiteRuleByID(libID, column.getPubRule());
        pubRules[1] = siteRuleCache.getSiteRuleByID(libID, column.getPubRulePad());

        return pubRules;
    }

    //读稿件模板（主版模板、触屏版模板）
    private Template[] readTemplate(Column column, PubArticle article) {
        //稿件的栏目中指定的模板。没有组图视频模板时都使用文章模板
        long tID0 = column.getTemplateArticle();
        long tID1 = column.getTemplateArticlePad();

        switch (article.getType()) {
            case 1://组图
                if (column.getTemplatePic() > 0) tID0 = column.getTemplatePic();
                if (column.getTemplatePicPad() > 0) tID1 = column.getTemplatePicPad();
                break;
            case 2://视频
                if (column.getTemplateVideo() > 0) tID0 = column.getTemplateVideo();
                if (column.getTemplateVideoPad() > 0) tID1 = column.getTemplateVideoPad();
                break;
            default:
                break;
        }

        //若稿件指定了模板，优先要用指定的模板
        if (article.getTemplateIDs()[0] > 0) tID0 = article.getTemplateIDs()[0];
        if (article.getTemplateIDs()[1] > 0) tID1 = article.getTemplateIDs()[1];

        //缓存中取出模板对象
        Template[] template = new Template[2];

        int libID = LibHelper.getLibIDByOtherLib(DocTypes.TEMPLATE.typeID(), article.getDocLibID());

        BaseDataCache templateCache = (BaseDataCache) (CacheReader.find(BaseDataCache.class));
        template[0] = templateCache.getTemplateByID(libID, tID0);
        template[1] = templateCache.getTemplateByID(libID, tID1);

        return template;
    }

	//设置图片的url
	protected void setPicUrl(BareArticle a, int attLibID) {
        int channel = getColumnChannel();
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			Document[] atts = docManager.find(attLibID, "att_articleID=? and att_articleLibID=?",
					new Object[]{a.getId(), a.getDocLibID()});
			if (atts != null) {
				for (Document att : atts) {
					String url = (channel == 0) ? att.getString("att_url") : att.getString("att_urlPad");

					int type = att.getInt("att_type");

					if (type == 2) a.setPicBig(url);
					else if (type == 3) a.setPicMiddle(url);
					else if (type == 4) a.setPicSmall(url);
						//替换正文中的图片地址
					else if(type==0){
						String path = att.getString("att_path");
						String content = a.getContent().replace("../../xy/image.do?path="+path, url);
						a.setContent(content);
					}
				}
			}
		} catch (E5Exception e) {
			e.printStackTrace();
		}
	}



}
