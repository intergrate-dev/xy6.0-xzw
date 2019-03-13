package com.founder.xy.jpublish.wx;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.founder.e5.context.E5Exception;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.jms.data.DocIDMsg;
import com.founder.xy.jpublish.PublishHelper;
import com.founder.xy.jpublish.data.BareArticle;
import com.founder.xy.jpublish.data.PubArticle;
import com.founder.xy.jpublish.page.AbstractGenerator;
import com.founder.xy.jpublish.template.ComponentFactory;
import com.founder.xy.jpublish.template.component.Component;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;

/**
 * 微信菜单稿件发布生成器
 * @author Gong Lijie
 */
public class WXGenerator extends AbstractGenerator{
	private WXContext context;
	
	public int generator(DocIDMsg data){
		if (log.isDebugEnabled()) log.debug("---------微信菜单稿件发布 " + data.getDocLibID() + "," + data.getDocID());
		if (data.getDocID() < 0) return PubArticle.ERROR_NO_DATA;
		
		context = new WXContext();
		context.init(data);
		
		//先发布标题图片
		int success = pubPics();
		
		// 最后发布稿件
		if (success == PubArticle.SUCCESS)
			success = pubPage();
		
		if (success == PubArticle.SUCCESS) {
			log.debug("：已发布 " + context.getMenu().getPath());
		}
		return success;
	}
	
	//文件发布过程
	private int pubPage() {
		try {
			String pageContent = applyTemplate();
			
			return pushFile(pageContent, context.getMenu().getPath());
		} catch (Exception e) {
			log.error("发布失败：" + e.getLocalizedMessage(), e);
			
			return PubArticle.ERROR_PUBLISH;
		}
	}
	
	//发布标题图片
	private int pubPics() {
		try {
			//sql：修改图片地址为外网地址
			String tCode = LibHelper.getTenantCodeByLib(context.getMessage().getDocLibID());
			String table = LibHelper.getLibTable(DocTypes.WXARTICLE.typeID(), tCode);
			String sql = "update " + table + " set wx_pic=? where SYS_DOCUMENTID=?";
			
			//从发布地址得到url的根，以便修改图片的发布url
			String blockUrl = context.getMenu().getUrl();
			String urlRoot = blockUrl.substring(0, blockUrl.lastIndexOf("/") + 1);
			
			//检查并发布每个稿件的标题图片
			List<BareArticle> articles = context.getArticles();
			for (BareArticle ba : articles) {
				pubPicOne(ba, sql, urlRoot);
			}
			return PubArticle.SUCCESS;
		} catch (Exception e) {
			log.error("标题图片发布失败：" + e.getLocalizedMessage(), e);
			
			return PubArticle.ERROR_BLOCK_PIC;
		}
	}
	
	//检查并发布标题图片
	private void pubPicOne(BareArticle ba, String sql, String urlRoot) throws E5Exception {
		String picPath = ba.getPicBig(); //标题图片，存在这个属性里
		
		//无标题图片、或已发布
		if (picPath != null) picPath = picPath.trim();
		if (StringUtils.isBlank(picPath) || picPath.toLowerCase().startsWith("http")) return;
		
		int pos = picPath.indexOf(";");
		if (pos < 0) return;
		
		String fileName = picPath.substring(pos + 1); //xy/201511/05/..............jpg
		
		//复制图片
		String dir = context.getMenu().getDir();
		InfoHelper.copyFile(picPath, dir, fileName);//复制图片
		
		//trans：生成分发信息文件
		String path = dir + "/" + fileName;
		PublishHelper.writeTransPath(path, context.getSiteRoot());
		
		//图片的外网地址保存到区块内容表中
		picPath = urlRoot + fileName;
		InfoHelper.executeUpdate(context.getMessage().getDocLibID(), sql, new Object[]{picPath, ba.getId()});
		
		//改对象中的图片地址为外网地址
		ba.setPicBig(picPath);
	}
	
	//套用模板，得到结果
	private String applyTemplate() throws Exception{
		String template = getTemplateContent(context.getTemplate());

		StringBuffer pageContent = new StringBuffer();
		
		Matcher componentMatcher = PARSER_PATTERN.matcher(template);
		String cr = null;
		while (componentMatcher.find()) {
			cr = getComponentResult(componentMatcher.group(1));
			componentMatcher.appendReplacement(pageContent, cr);
		}
		componentMatcher.appendTail(pageContent);
		
		return pageContent.toString();
	}
	
	private String getComponentResult(String comID) throws Exception{
		String componentObj = RedisManager.hget(RedisKey.CO_KEY, comID);
		if (componentObj == null) {
			return "";
		} else {
			Component component = ComponentFactory.newComponent(param, componentObj);
			component.setData("articles", context.getArticles());
			
			return component.getComponentResult();
		}
	}
	
	//把文件存储到外网
	private int pushFile(String pageContent,String pathName) throws Exception{
		if(StringUtils.isEmpty(pathName)){
			log.error("发布路径为空，发布失败！");
			return PubArticle.ERROR_NO_PUBDIR;
		}
		FileUtils.writeStringToFile(new File(pathName), pageContent, "UTF-8");
		
		//trans：生成分发信息文件
		PublishHelper.writeTransPath(pathName, context.getSiteRoot());
		
		return PubArticle.SUCCESS;
	}
}
