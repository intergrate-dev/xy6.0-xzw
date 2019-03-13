package com.founder.xy.jpublish.page;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.founder.e5.commons.Log;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.context.Context;
import com.founder.e5.sys.StorageDeviceManager;
import com.founder.e5.sys.SysFactory;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.jpublish.ColParam;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;
import com.founder.xy.template.ComponentObj;
import com.founder.xy.template.Template;
import com.founder.xy.template.parser.TemplateParser;

/**
 * 发布生成器的基类
 * @author Gong Lijie
 */
public abstract class AbstractGenerator {
	//模板组件
	protected static final Pattern PARSER_PATTERN = Pattern.compile("<XY-PARSE[\\s]*?coid=\"([\\s\\S]*?)\"[\\s]*?/>");
	
	protected ColParam param;
	protected Log log = Context.getLog("xy.publish");

	/**
	 * 取模板文件的内容，读/写Redis缓存
	 * @param template
	 * @return
	 */
	protected String getTemplateContent(Template template){
		String tID = String.valueOf(template.getId());
		
		String templateContent = RedisManager.hget(RedisKey.TEMPLATE_FILE_KEY, tID);
		if (templateContent == null || StringUtils.isBlank(templateContent)) {
			String path = template.getFilePath() + ".parsed";
			TemplateParser templateParser = (TemplateParser)Context.getBean("templateParser");
			String filePath = InfoHelper.getFilePathInDevice(path);
			File templateParsedFile = new File(filePath);
			//如果模板没有解析，需要重新解析
			if(!templateParsedFile.exists()){
				templateParser.parse(template.covert2Document());
				templateContent = readFileContent(path);
			}else{
				templateContent = readFileContent(path);
				RedisManager.hset(RedisKey.TEMPLATE_FILE_KEY, tID, templateContent);
				//把模板组件也刷新到redis里
				List<ComponentObj> coList = templateParser.getComponentObjs(template.getId(), 0);
				templateParser.refreshCache(coList, template.getId(), 0);
			}
		}
		return templateContent;
	}
	protected String readFileContent(String path) {
		int pos = path.indexOf(";");
		
		String deviceName = path.substring(0, pos);
		String savePath = path.substring(pos + 1);
		
		InputStream in = null;
		StorageDeviceManager sdManager = SysFactory.getStorageDeviceManager();
		try {
			in = sdManager.read(deviceName, savePath);
			return IOUtils.toString(in, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			ResourceMgr.closeQuietly(in);
		}
	}
	
	public String getChannelName(int index) {
		return (index == 0) ? "主版" : "触屏版";
	}
}
