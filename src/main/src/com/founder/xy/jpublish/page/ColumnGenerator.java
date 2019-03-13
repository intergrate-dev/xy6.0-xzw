package com.founder.xy.jpublish.page;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.context.E5Exception;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.jms.data.DocIDMsg;
import com.founder.xy.jpublish.ColParam;
import com.founder.xy.jpublish.PublishHelper;
import com.founder.xy.jpublish.context.ColumnContext;
import com.founder.xy.jpublish.data.PubArticle;
import com.founder.xy.jpublish.template.ComponentFactory;
import com.founder.xy.jpublish.template.component.ArticleListPageComponent;
import com.founder.xy.jpublish.template.component.Component;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;
import com.founder.xy.template.Template;

/**
 * 栏目发布生成器，静态栏目页
 * @author Gong Lijie
 */
public class ColumnGenerator extends AbstractGenerator{
	private ColumnContext context;
	private boolean preview;
	private String[] previewPages = new String[2]; //为预览生成的页面
	
	/**
	 * 栏目页生成
	 * @param data
	 * @return
	 */
	public boolean generator(DocIDMsg data){
		if (log.isDebugEnabled()) log.debug("---栏目页发布 " + data.getDocLibID() + "," + data.getDocID());
		
		return generatorFiles(data);
	}
	/**
	 * 栏目页预览，生成的内容
	 * @return 网页html内容，两个元素的数组，第一个元素是网站版html，第二个元素是触屏版html
	 * @throws E5Exception 
	 */
	public String[] preview(DocIDMsg data) throws E5Exception{
		preview = true;
		
		boolean previewResult = generatorFiles(data);
		if (previewResult)
			return previewPages;
		else {
			throw new E5Exception("预览失败");
		}
	}
	
	private boolean generatorFiles(DocIDMsg data) {
		context = new ColumnContext();
		context.init(data);

		if("specialPreview".equals(data.getRelIDs())){
			param = new ColParam(data.getDocLibID(), 0, 0);
		}
		else
			param = new ColParam(data.getDocLibID(), data.getDocID(), 0);
		if (!"specialPreview".equals(data.getRelIDs()) && context.getColumn() == null) {
			System.out.println("栏目怎么是空的？");
			return false;
		} else {
			if (log.isDebugEnabled()) log.debug("栏目:" + context.getColumn().getName());
		}
		
		return pubByTemplates();
	}
	
	//按模板发布
	private boolean pubByTemplates() {
		int pubResult = PubArticle.SUCCESS;
		
		boolean hasTemplate = false;
		
		//按模板发布栏目页
		Template[] templates = context.getTemplate();
		for (int i = 0; i < templates.length; i++) {
			if (templates[i] != null) hasTemplate = true;
			
			pubResult = pubChannel(templates[i], i);
			if (pubResult != PubArticle.SUCCESS) break;
		}
		
		//真正按模板发布了栏目页后，更新栏目的最新发栏目页字段
		if (!preview && hasTemplate && pubResult == PubArticle.SUCCESS) {
			changeColumnPubTime();
		}
		return pubResult == PubArticle.SUCCESS;
	}
	private int pubChannel(Template template, int index) {
		String pageDirs = getPageDir(index);
		
		//若没有配置发布规则，则该渠道不继续发布
		if (!preview && pageDirs == null) {
			if (log.isDebugEnabled()) log.debug(getChannelName(index) + ":没有发布目录");
			return PubArticle.SUCCESS;
		}
		
		int success = PubArticle.SUCCESS;
		
		if (template != null) {
			// 最后发布稿件
			success = pubPage(pageDirs, template, index);
			if (success == PubArticle.SUCCESS) {
				if (log.isDebugEnabled())
					log.debug(getChannelName(index) + "：已发布 " + pageDirs);
				else
					log.info(pageDirs);
			}
		} else {
			if (log.isDebugEnabled())
				log.debug(getChannelName(index) + ":无模板，不发布");
		}
		return success;
	}
	//文件发布过程
	private int pubPage(String articleDir, Template template, int index) {
		try {
			String[] pageContent = applyTemplate(template, index);
			if (preview) {
				if (pageContent.length > 0)
					previewPages[index] = pageContent[0]; //若是预览，则把生成网页的第一页保存下来
				return PubArticle.SUCCESS;
			} else {
				return pushFile(pageContent, articleDir);
			}
		} catch (Exception e) {
			log.error("发布失败：" + e.getLocalizedMessage(), e);
			
			return PubArticle.ERROR_PUBLISH;
		}
	}

	//栏目页发布成功后，修改栏目的“最新栏目页发布时间”字段
	private void changeColumnPubTime() {
		int colLibID = context.getMessage().getDocLibID();
		try {
			String sql = "update " + LibHelper.getLibTable(colLibID)
					+ " set col_pubTimeColumn=? where SYS_DOCUMENTID=?";
			InfoHelper.executeUpdate(colLibID, sql, 
					new Object[]{DateUtils.getTimestamp(), context.getMessage().getDocID()});
		} catch (E5Exception e) {
			e.printStackTrace();
		}
	}
	
	private String getPageDir(int index) {
		if (index == 0) {
			return context.getPageDir("column");
		} else {
			return context.getPageDir("columnPad");
		}
	}
	private String getPageUrl(int index) {
		if (index == 0) {
			return context.getPageDir("url");
		} else {
			return context.getPageDir("urlPad");
		}
	}
	//套用模板，得到结果
	private String[] applyTemplate(Template template, int index) throws Exception{
		//模板内容
		String templateContent = getTemplateContent(template);
		
		String url = getPageUrl(index);
		String[] pages = mergeContent(templateContent, url);

		return pages;
	}

	private String[] mergeContent(String templateContent, String url) throws Exception {
		List<StringBuffer> allPages = new ArrayList<>();
		allPages.add(new StringBuffer()); //缺省是一页
		
		int lastAppendPosition = 0; //指示在模板文件中复制的位置
		
		Matcher componentMatcher = PARSER_PATTERN.matcher(templateContent);
		while (componentMatcher.find()) {
			String[] cr = getComponentResult(componentMatcher.group(1));
			
			//若遇到了分页稿件列表组件，则模板替换后是多页，此时要复制成多份。复制之前的页面Html。
			//这里简单地认为一个页面只有一个分页组件。未处理多个分页组件的情景。
			if (cr.length > 1) {
				String page0 = allPages.get(0).toString();
				for (int i = 1; i < cr.length; i++) {
					allPages.add(i, new StringBuffer(page0));
				}
			}
			
			String headerPart = componentMatcher.start() > lastAppendPosition 
					? templateContent.substring(lastAppendPosition, componentMatcher.start())
					: "";
			for (int i = 0; i < allPages.size(); i++) {
				//String content = cr[i].replace("$", "\\$"); //注意$符号
				//componentMatcher.appendReplacement(now, content);
				
				StringBuffer now = allPages.get(i);
				if (cr.length > 1) {
					now.append(headerPart).append(cr[i]);
					//对分页稿件列表组件，加翻页区域
					now.append(pageArea(cr.length, i, url));
				} else if(cr.length==1){
					now.append(headerPart).append(cr[0]);
				}
				else
					now.append(headerPart).append("");
			}
			lastAppendPosition = componentMatcher.end();
		}
		
		//添加模板最后部分
		String tail = templateContent.substring(lastAppendPosition);
		
		String[] result = new String[allPages.size()];
		for (int i = 0; i < allPages.size(); i++) {
			result[i] = allPages.get(i).append(tail).toString();
		}
		
		return result;
	}
	
	private String[] getComponentResult(String comID) throws Exception{
		String componentObj = RedisManager.hget(RedisKey.CO_KEY, comID);
		if (componentObj == null) {
			return new String[]{""};
		} else {
			Component component = ComponentFactory.newComponent(param, componentObj, preview);
			//若是分页稿件列表
			if (component instanceof ArticleListPageComponent) {
				return ((ArticleListPageComponent)component).getComponentResults();
			} else {
				return new String[]{component.getComponentResult()};
			}
		}
	}
	//分页区域的html代码
	private String pageArea(int pages, int current, String url) {
		if ("是".equals(InfoHelper.getConfig("发布服务", "分页样式采用旧版翔宇样式"))) {
			if (pages <= 1) return "";
			String result = "<div width=\"100%\" id=\"autopage\"><center>";
			for (int i = 0; i < pages; i++) {
				if (i == current) {
					result += "<span>" + (i + 1) + "</span>&nbsp;&nbsp;";
				} else {
					result += "<a href='" + urlOfSplitPages(i, url) + "'>" + (i + 1) + "</a>&nbsp;&nbsp;";
				}
			}
			result += "</center> <br/><br/></div>";
			return result;
		} else {
			if (pages <= 1) return "";

			String result = "<ul pages='" + pages + "' current='" + (current + 1) + "' class='pages'>";

			if (current > 0)
				result += "<li class='page-previous'><a href='" + urlOfSplitPages(current - 1, url) + "'>上一页</a></li>";
			for (int i = 0; i < pages; i++) {
				result += "<li class='page page" + (i + 1);
				if (i == current) {
					result += " page-active";
					result += "'><a href='javascript:void(0)'>" + (i + 1) + "</a></li>";
				} else {
					result += "'><a href='" + urlOfSplitPages(i, url) + "'>" + (i + 1) + "</a></li>";
				}
			}
			if (current < pages - 1)
				result += "<li class='page-next'><a href='" + urlOfSplitPages(current + 1, url) + "'>下一页</a></li>";

			result += "</ul>";
			return result;
		}
	}
	private String urlOfSplitPages(int page,String url) {
		if (page == 0)
			return url;
		return pathOfSplit(url, page);
	}
	//把文件存储到外网
	private int pushFile(String[] pageContent,String pathName) throws Exception{
		if(StringUtils.isEmpty(pathName)){
			log.error("发布路径为空，发布失败！");
			return PubArticle.ERROR_NO_PUBDIR;
		}
		for (int i = 0; i < pageContent.length; i++) {
			String thisPath = pathOfSplit(pathName, i);
			FileUtils.writeStringToFile(new File(thisPath), pageContent[i], "UTF-8");
			//trans：生成分发信息文件
			String root = context.getPageDir("root");
			PublishHelper.writeTransPath(thisPath, root);
		}
		
		return PubArticle.SUCCESS;
	}
	
	//分页文件名（后缀部分加页号）
	private String pathOfSplit(String pathName, int pageNo) {
		//第一页，路径不变
		if (pageNo == 0)
			return pathName;
		
		int pos = pathName.lastIndexOf(".");
		return pathName.substring(0, pos) + "_" + (pageNo + 1) + pathName.substring(pos);
	}
	
}
