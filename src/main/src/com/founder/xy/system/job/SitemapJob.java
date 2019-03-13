package com.founder.xy.system.job;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.Pair;
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
import com.founder.e5.scheduler.BaseJob;
import com.founder.xy.article.Article;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.jpublish.PublishHelper;
import com.founder.xy.system.site.DomainDir;
import com.founder.xy.system.site.DomainDirManager;

/**
 * 站点地图生成服务
 * @author Gong Lijie
 */
public class SitemapJob extends BaseJob{
	private String timeFormat = "yyyy-MM-dd'T'HH:mm:ss'+08:00'";
	
	public SitemapJob() {
		super();
		log = Context.getLog("xy.sitemap");
	}

	@Override
	protected void execute() throws E5Exception {
		log.info("开始站点地图生成");
		
		DocLib[] siteLibs = LibHelper.getLibs(DocTypes.SITE.typeID());
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		for (DocLib siteLib : siteLibs) {
			//一个租户的所有站点
			Document[] sites = docManager.find(siteLib.getDocLibID(), "site_seo is not null", null);
			for ( Document site : sites ) {
				try {
					oneSite(site);
				} catch (Exception e) {
					log.error(e);
				}
			}
		}
		log.info("站点地图生成完毕");
	}

	/**
	 */
	private void oneSite(Document site) throws Exception {
		//对每个站点，检查是否有seo配置，没有则不理会
		int dirWeb = 0, dirApp = 0;
		
		String config = site.getString("site_seo");
		if (!StringUtils.isBlank(config)) {
			JSONObject json = JSONObject.fromObject(config);
			if (json.containsKey("dirWeb")) dirWeb = json.getInt("dirWeb");
			if (json.containsKey("dirApp")) dirApp = json.getInt("dirApp");
		}
		if (dirWeb <=0 && dirApp <= 0) return;
		
		//开始做站点地图。取出所有稿件库
		log.info("站点：" + site.getString("site_name"));
		
		String tenantCode = LibHelper.getTenantCodeByLib(site.getDocLibID());
		List<DocLib> articleLibs = LibHelper.getLibs( DocTypes.ARTICLE.typeID(), tenantCode );

		//站点下的web稿件
		oneArticleLib(articleLibs.get(0), site, dirWeb);
		
		//站点下的app稿件
		oneArticleLib(articleLibs.get(1), site, dirApp);
	}
	
	private void oneArticleLib(DocLib articleLib, Document site, int dirID) {
		if (dirID <= 0) return;
		
		//找到域对象
		DomainDir domain = getDomain(articleLib, dirID);
		if (domain == null) {
			log.error("设置了无效的域ID：" + dirID);
			return;
		}
		
		//在sitemap目录下，查找sitemap.xml文件
		String siteWebRoot = InfoHelper.getWebRoot(site.getDocID());
		if (StringUtils.isBlank(siteWebRoot))
			siteWebRoot = site.getString("site_webRoot");
		
		String path = siteWebRoot + domain.getPath();
		File indexFile = getIndexFile(path);
		
		//取出最后一个sitemap节点，得到序号（如2022）和上次查询时间T0
		org.dom4j.Document doc = parse(indexFile);
		Pair info = readIndex(doc);
		int number = Integer.parseInt(info.getKey()) + 1;
		Timestamp t0 = new Timestamp(((Date)info.getValue()).getTime());
		
		//读出稿件内容
		Timestamp t = DateUtils.getTimestamp();
		String content = readArticles(articleLib, t, t0, site.getDocID());
		
		//生成sitemap<No.+1>.xml
		String fileName = "sitemap" + number + ".xml";
		File sitemap = new File(path, fileName);
		try {
			FileUtils.write(sitemap, content);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//sitemap.xml文件里加新sitemap文件信息
		addSitemap(indexFile, doc, domain, fileName, t);
		
		//trans分发信息
		PublishHelper.writeTransPath(indexFile.getPath(), siteWebRoot);
		PublishHelper.writeTransPath(sitemap.getPath(), siteWebRoot);
	}
	
	//取出站点地图索引文件
	private File getIndexFile(String path) {
		File file = new File(path, "sitemap.xml");
		if (!file.exists()) {
			//加了xmlns后dom4j的selectNodes取不到对象
			//String empty = "<?xml version=\"1.0\" encoding=\"utf-8\"?><sitemapindex xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\"></sitemapindex>";
			String empty = "<?xml version=\"1.0\" encoding=\"utf-8\"?><sitemapindex></sitemapindex>";
			try {
				FileUtils.write(file, empty);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}
	
	//读索引文件中的最后一个站点地图信息:得到序号（如2022）和上次查询时间T0
	private Pair readIndex(org.dom4j.Document doc) {
	    @SuppressWarnings("unchecked")
		List<Element> last = doc.getRootElement().selectNodes("//sitemap[last()]");
	    if (last != null && last.size() > 0) {
	    	Element lastOne = last.get(0);
	    	String loc = lastOne.elementText("loc");//http://news.163.com/sitemap2022.xml
	    	String lastmod = lastOne.elementText("lastmod");//2015-12-03T00:02:00+08:00
	    	
	    	String number = loc.substring(loc.lastIndexOf("/sitemap") + 8, loc.lastIndexOf(".xml"));
	    	Date t0 = DateUtils.parse(lastmod, timeFormat);
	    	
	    	return new Pair(number, t0);
	    } else {
	    	return new Pair("0", new Date(0));
	    }
	}
	//取出当前时间T, 查找稿件发布库中[T0, T]之间的已发布稿件，按发布时间排序后，生成站点地图xml内容
	private String readArticles(DocLib articleLib, Timestamp t, Timestamp t0, long siteID) {
		String sql = "SELECT a_url,a_urlPad,a_pubTime,a_type FROM " + articleLib.getDocLibTable()
				+ " WHERE (a_pubTime between ? and ?) and a_status=1 and a_siteID=? ORDER BY a_pubTime";
		
		StringBuilder result = new StringBuilder();
		result.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		result.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\"");
		result.append(" xmlns:mobile=\"http://www.baidu.com/schemas/sitemap-mobile/1/\"");
		result.append(">\n");
		
		DBSession conn = null;
		IResultSet rs = null;
		try {
			conn = Context.getDBSession(articleLib.getDsID());
			rs = conn.executeQuery(sql.toString(), new Object[]{t0, t, siteID});
			while (rs.next()){
				int type = rs.getInt("a_type");
				if (type >= Article.TYPE_SPECIAL && type != Article.TYPE_ACTIVITY
						 && type != Article.TYPE_PANORAMA
						 && type != Article.TYPE_FILE)
					continue;
				
				String url = StringUtils.xmlFilter(rs.getString("a_url"));
				String urlPad = StringUtils.xmlFilter(rs.getString("a_urlPad"));
				String pubTime = DateUtils.format(rs.getTimestamp("a_pubTime"), timeFormat);
				
				if (!StringUtils.isBlank(url)) {
					result.append("<url>\n");
					result.append("		<loc>").append(url).append("</loc>\n");
					result.append("		<lastmod>").append(pubTime).append("</lastmod>\n");
					result.append("</url>\n");
				}
				if (!StringUtils.isBlank(urlPad)) {
					result.append("<url>\n");
					result.append("		<loc>").append(urlPad).append("</loc>\n");
					result.append("		<lastmod>").append(pubTime).append("</lastmod>\n");
					result.append("		<mobile:mobile type=\"mobile\"/>\n");
					result.append("</url>\n");
				}
			}
		} catch (Exception e) {
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}
		
		result.append("</urlset>");
		
		return result.toString();
	}

	private void addSitemap(File file, org.dom4j.Document doc, DomainDir domain, String fileName, Timestamp t) {
	    String lastmod = DateUtils.format(t, timeFormat);
	    
	    Element last = doc.getRootElement().addElement("sitemap");
	    last.addElement("loc").addText(domain.getUrl() + "/" + fileName);
	    last.addElement("lastmod").addText(lastmod);
	    
		try {
			FileUtils.write(file, doc.asXML());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private DomainDir getDomain(DocLib articleLib, int dirID) {
		int domainLibID = LibHelper.getLibIDByOtherLib(DocTypes.DOMAINDIR.typeID(), articleLib.getDocLibID());
		DomainDirManager dirManager = (DomainDirManager)Context.getBean("domainDirManager");
		try {
			return dirManager.getDomain(domainLibID, dirID);
		} catch (E5Exception e1) {
			e1.printStackTrace();
			return null;
		}
	}

	private org.dom4j.Document parse(File file) {
		SAXReader reader = new SAXReader();
	    try {
	    	return reader.read(file);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}
}