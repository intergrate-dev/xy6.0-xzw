package com.founder.xy.jpublish.template;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class FreeMarkerManager {
	private static Configuration configuration;
	
	static{
		configuration = new Configuration(Configuration.VERSION_2_3_22);
		configuration.setCacheStorage(new freemarker.cache.MruCacheStorage(20, 250));
	}
	
	/**
	 * 将一段freemarker组件代码解析成最终网页结果
	 * @param coID 组件实例ID
	 * @param data 实际数据，如稿件内容、稿件列表等
	 * @param templateCode 模板代码
	 * @return
	 */
	public static String process(String coID, Map<String,Object> data,
			String templateCode) throws Exception {
		StringWriter result = new StringWriter();
		Template template = new Template(coID,templateCode,configuration);
		template.process(data, result);
		
		return result.toString();
	}
	
	public static void main(String[] args) throws Exception{
		String comID = "41";
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("title", "<div class=\"para\">插件式模板载入器：可以从任何源载入模板，如本地文件、数据库等等。</div>");
		String showCode = "${title?replace(\"<.*?>\",\"\",\"r\")}";
		System.out.print(process(comID,data,showCode));
//		Column column = new Column(1, "2", "3", true, true, 1);
//		List<Column> columns = new ArrayList<Column>();
//		columns.add(column);
//		data.put("columns", columns);
//		String showCode = "\\r\\n<ul>\\r\\n<#list columns as column>\\r\\n  <li><a href=\\\" ${column.url}\\\">${column.name}<\\/a><\\/li>\\r\\n</#list>\\r\\n<\\/ul>\\r\\n";
//		process(comID,data,showCode);
	}
}
