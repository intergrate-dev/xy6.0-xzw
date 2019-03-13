package com.founder.xy.article.web;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.CacheReader;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.doc.util.E5docHelper;
import com.founder.e5.web.WebUtil;
import com.founder.e5.web.org.StringValueUtils;
import com.founder.e5.workspace.ProcHelper;
import com.founder.xy.article.ArticleManager;
import com.founder.xy.column.Column;
import com.founder.xy.column.ColumnReader;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.jpublish.BaseDataCache;
import com.founder.xy.system.site.SiteRule;
import com.founder.xy.template.Template;
import com.swetake.util.Qrcode;

/**
 * 二维码稿件
 * 
 * @author han xf
 */
@Controller
@RequestMapping("/xy/article")
public class ArticleQrCodeController {
	@Autowired
    private ColumnReader colReader;
	@Autowired
	private ArticleManager articleManager;
	
	/**
	 * 生成制定数量的空稿件
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "GeneratorEmptyArticleAction.do")
	public String GeneratorEmptyArticleAction(HttpServletRequest request,HttpServletResponse response)throws Exception {
		int num = WebUtil.getInt(request, "num", 0);//生成二维码稿件数量
		int ch = WebUtil.getInt(request, "ch", 0);// 渠道，web/app
		int docLibID = ch == 0 ? LibHelper.getArticleLibID() : LibHelper.getArticleAppLibID();
		
		StringBuffer strDocIDs = new StringBuffer() ;
		List<Document> articles = new ArrayList<Document>();
		for(int x = 0 ; x < num ; x++){
			Document article = this.assembleNewArticle(request) ;
			article.setTopic(article.getTopic()+x);
			article.set("a_linkTitle",article.getTopic()) ;
			articles.add(article) ;
			strDocIDs.append(article.getDocID()).append(",") ;
        }
		String error = this.save(docLibID, articles);
        String url = "/e5workspace/after.do?UUID=" + WebUtil.get(request, "UUID");
        if (error == null) {
            url += "&DocIDs=" + strDocIDs.toString()  + "&Info=" + URLEncoder.encode("操作完成", "UTF-8"); //操作成功
        } else {
            url += "&Info=" + URLEncoder.encode(error, "UTF-8");//有错误，需返回前台做提示
        }
        
        return "redirect:" + url ;
	}
	
	/**
	 * 将选中的稿件生成二维码,打包zip包返回
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "QrCodeZipDownloadAction.do")
	public void QrCodeZipDownloadAction(HttpServletRequest request,HttpServletResponse response)throws Exception{
		
		int colID = WebUtil.getInt(request, "colID", 0);// 栏目
		int ch = WebUtil.getInt(request, "ch", 0);// 渠道，web/app
		int docLibID = ch == 0 ? LibHelper.getArticleLibID() : LibHelper.getArticleAppLibID();
		String strDocIDs = WebUtil.get(request, "DocIDs");
        long[] docIDs = StringUtils.getLongArray(strDocIDs);
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        String sDate = DateUtils.format(new Date(),"yyyy-MM-dd") ;
		
		ByteArrayOutputStream bos0 = new ByteArrayOutputStream();
		ByteArrayOutputStream bos1 = new ByteArrayOutputStream();
		CheckedOutputStream cos = new CheckedOutputStream(bos0, new CRC32());
		ZipOutputStream zos = new ZipOutputStream(cos);
		zos.setEncoding("GBK");
		byte[] downbytes = null ;
		
		int colLibID = LibHelper.getColumnLibID(request);//栏目库ID
		Column col = colReader.get(colLibID, colID);
		String basePath = col.getCasNames()+"_"+sDate+File.separator;
		String zipName = "qrcode" + col.getCasNames() + "_" + sDate + ".zip" ;
		try {
			for (long docID : docIDs) {
				Document article = docManager.get(docLibID, docID);
	        	String url = ch == 0 ? article.getString("a_url") : article.getString("a_urlPad") ;
	        	if(!(url == null && "".equals(url))){
	        		String fileName = article.getTopic()+"_"+article.getDocID()+".png";
	        		this.encoderQRCode(url, bos1, "png", 7);
	        		byte[] data = bos1.toByteArray() ;
	        		ZipEntry entry = new ZipEntry(basePath + fileName);
	        		zos.putNextEntry(entry);
	        		zos.write(data, 0, data.length);
	        		zos.flush();
	        	}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			IOUtils.closeQuietly(zos);
			downbytes = bos0.toByteArray();
			IOUtils.closeQuietly(bos0);
			IOUtils.closeQuietly(bos1);
		}
		// 下载过程
		response.setContentType("application/zip; charset=gbk");
		response.setHeader("Content-Disposition","attachment; filename="+URLEncoder.encode(zipName, "UTF-8"));
		response.getOutputStream().write(downbytes) ;
	}
	
	/**
	 * 组装空稿件
	 * @param request
	 * @return
	 * @throws E5Exception
	 */
	private Document assembleNewArticle(HttpServletRequest request) throws Exception {
		int colLibID = LibHelper.getColumnLibID(request);//栏目库ID
		int ch = WebUtil.getInt(request, "ch", 0);// 渠道，web/app
		int docLibID = ch == 0 ? LibHelper.getArticleLibID() : LibHelper.getArticleAppLibID();
		int siteID = StringValueUtils.getInt(request.getParameter("siteID"), 1);
		int type = WebUtil.getInt(request, "type", 0);// 稿件类型（文章/组图/视频等）
		int colID = WebUtil.getInt(request, "colID", 0);// 栏目
		
		// 打开写稿界面时就预取稿件ID
		long docID = InfoHelper.getNextDocID(DocTypes.ARTICLE.typeID()); // 提前取稿件ID
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document article = docManager.newDocument(docLibID, docID);
		ProcHelper.initDoc(article,request);
		Column col = colReader.get(colLibID, colID);
		
		if (ch >= 0)
			article.set("a_channel", ch == 0 ? 1 : 2); // 渠道，按位
		if (col != null) {
			article.set("a_column", col.getCasNames());
			article.set("a_extFieldGroup", col.getExtFieldGroup());
			article.set("a_extFieldGroupID", col.getExtFieldGroupID());
		}
		String title = col.getCasNames()+DateUtils.format(new Date(),"yyyy-MM-dd")+"二维码";
		article.setTopic(title.replaceAll("[\n\r]", ""));
		article.set("a_linkTitle",article.getTopic()) ;
		
		article.set("a_siteID", siteID);
		article.set("a_type", type);
		article.set("a_columnID", colID);
		article.set("a_columnAll",colID) ;
		article.set("a_docLibID", docLibID);
		article.set("a_editor", ProcHelper.getUserName(request));
		article.setAuthors(ProcHelper.getUserName(request));
		article.set("SYS_AUTHORID", ProcHelper.getUserID(request));
		article.setHaveAttach(0);
		article.set("a_pubTime",DateUtils.getTimestamp()) ;
		article.set("a_realPubTime",DateUtils.getTimestamp()) ;
		article.set("a_order",this.articleManager.getNewOrder(article));
		this.getUrl(request, article);
		return article;
	}
	
	/**
	 * 保存稿件,多个稿件的统一提交， 出错时返回错误信息
	 * @param article 空稿件
	 * @return
	 */
	private String save(int docLibID, List<Document> articles) {
	    DocumentManager docManager = DocumentManagerFactory.getInstance();
	    //同时修改多个稿件，使用事务
	    DBSession conn = null;
	    try {
	        conn = E5docHelper.getDBSession(docLibID);
	        conn.beginTransaction();
	
	        for (Document article : articles) {
	            docManager.save(article, conn);
	        }
	        conn.commitTransaction();
	        return null;
	    } catch (Exception e) {
	        ResourceMgr.rollbackQuietly(conn);
	        e.printStackTrace();
	        return "操作中出现错误：" + e.getLocalizedMessage();
	    } finally {
	        ResourceMgr.closeQuietly(conn);
	    }
	}
	
	/**
	 * 生成二维码(QRCode)图片
	 * @param content 存储内容
	 * @param output 输出流
	 * @param imgType 图片类型
	 * @param size 二维码尺寸
	 */
	public void encoderQRCode(String content, OutputStream output, String imgType, int size) {
		try {
			BufferedImage bufImg = this.qRCodeCommon(content, imgType, size);
			// 生成二维码QRCode图片
			ImageIO.write(bufImg, imgType, output);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 生成二维码(QRCode)图片的公共方法
	 * @param content 存储内容
	 * @param imgType 图片类型
	 * @param size 二维码尺寸
	 * @return
	 */
	private BufferedImage qRCodeCommon(String content, String imgType, int size) {
		BufferedImage bufImg = null;
		try {
			Qrcode qrcodeHandler = new Qrcode();
			// 设置二维码排错率，可选L(7%)、M(15%)、Q(25%)、H(30%)，排错率越高可存储的信息越少，但对二维码清晰度的要求越小
			qrcodeHandler.setQrcodeErrorCorrect('M');
			qrcodeHandler.setQrcodeEncodeMode('B');
			// 设置设置二维码尺寸，取值范围1-40，值越大尺寸越大，可存储的信息越大
			qrcodeHandler.setQrcodeVersion(size);
			// 获得内容的字节数组，设置编码格式
			byte[] contentBytes = content.getBytes("utf-8");
			// 图片尺寸
			int imgSize = 67 + 12 * (size - 1);
			bufImg = new BufferedImage(imgSize, imgSize, BufferedImage.TYPE_INT_RGB);
			Graphics2D gs = bufImg.createGraphics();
			// 设置背景颜色
			gs.setBackground(Color.WHITE);
			gs.clearRect(0, 0, imgSize, imgSize);

			// 设定图像颜色> BLACK
			gs.setColor(Color.BLACK);
			// 设置偏移量，不设置可能导致解析出错
			int pixoff = 2;
			// 输出内容> 二维码
			if (contentBytes.length > 0 && contentBytes.length < 800) {
				boolean[][] codeOut = qrcodeHandler.calQrcode(contentBytes);
				for (int i = 0; i < codeOut.length; i++) {
					for (int j = 0; j < codeOut.length; j++) {
						if (codeOut[j][i]) {
							gs.fillRect(j * 3 + pixoff, i * 3 + pixoff, 3, 3);
						}
					}
				}
			} else {
				throw new Exception("QRCode content bytes length = " + contentBytes.length + " not in [0, 800].");
			}
			gs.dispose();
			bufImg.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bufImg;
	}
	
	private void getUrl(HttpServletRequest request,Document article) throws Exception{
		int colLibID = LibHelper.getColumnLibID(request);//栏目库ID
		int colID = WebUtil.getInt(request, "colID", 0);// 栏目
		Column column = colReader.get(colLibID, colID);
		
        SiteRule[] pubRules = getPubRules(column,article.getDocLibID());
        Timestamp time = article.getTimestamp("a_pubTime");
        Date pubTime = (time != null) ? new Date(time.getTime()) : new Date();
		String format = InfoHelper.getConfig( "发布服务", "稿件日期目录格式")==null?"yyyyMM/dd":InfoHelper.getConfig( "发布服务", "稿件日期目录格式");
		String datePath = DateUtils.format(pubTime, format);
        long tID0 = column.getTemplateArticle();
        long tID1 = column.getTemplateArticlePad();
        int[] templateIDs = new int[2];
		templateIDs[0] = article.getInt("a_templateID");
		templateIDs[1] = article.getInt("a_templatePadID"); //稿件单独指定的模板ID
        if (templateIDs[0] > 0) tID0 = templateIDs[0];
        if (templateIDs[1] > 0) tID1 = templateIDs[1];
        //缓存中取出模板对象
        Template[] template = new Template[2];
        int libID = LibHelper.getLibIDByOtherLib(DocTypes.TEMPLATE.typeID(), article.getDocLibID());
        BaseDataCache templateCache = (BaseDataCache) (CacheReader.find(BaseDataCache.class));
        template[0] = templateCache.getTemplateByID(libID, tID0);
        template[1] = templateCache.getTemplateByID(libID, tID1);
        
        String urlWeb = getArticleUrl(pubRules[0], datePath, template[0],colID,article);
        String urlPad = getArticleUrl(pubRules[1], datePath, template[1],colID,article);
        article.set("a_url",urlWeb) ;
        article.set("a_urlPad",urlPad) ;
        
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
	
    private String getArticleUrl(SiteRule siteRule, String datePath, Template template,int colID, Document doc){
		if (siteRule == null || template == null) return "";
		String prefix = InfoHelper.getConfig( "发布服务", "稿件生成页前缀");

		StringBuffer url = new StringBuffer();
		if(siteRule != null){
			url.append(siteRule.getArticleDir());
			if(siteRule.isArticleByDate()){
				url.append("/" + datePath);
			}
			if (prefix == null || "".equals(prefix)) {
				url.append("/c" + doc.getDocID() + "." + getSuffix(template));
			} else {if(prefix.contains("$"))
				prefix = parserPrefix(prefix,doc);
				url.append("/" + prefix + doc.getDocID() + "." + getSuffix(template));
			}

		}
		return url.toString();
	}
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private String parserPrefix(String prefix, Document doc) {
		Map map = new HashMap();
		//先把栏目ID和栏目名称放进去，以后看需求添加
		String columns = doc.getString("a_column");		
		String column = columns.lastIndexOf("~")==-1?columns:columns.substring(columns.lastIndexOf("~")+1, columns.length());
		map.put("article.columnID", getInt(doc.getInt("a_columnID")));
		map.put("article.column", column);

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
		if (t == null || StringUtils.isBlank(t.getFileType())){
			return "html";
		}
		else{
			return t.getFileType();
		}
	}
	private int getInt(int value) {
		if (value < 0) return 0;
		return value;
	}
}
