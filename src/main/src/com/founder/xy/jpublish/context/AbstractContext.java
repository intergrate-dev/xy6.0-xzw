package com.founder.xy.jpublish.context;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.founder.e5.context.CacheReader;
import com.founder.xy.column.Column;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.jpublish.BaseDataCache;
import com.founder.xy.template.Template;

/**
 * 发布服务的上下文环境
 */
public abstract class AbstractContext {
	
	Column column;
	
	//生成模板
	Template[] template;
	
	//本页生成路径
	Map<String,String> pageDirMap = new HashMap<String,String>();
	
	public Column getColumn() {
		return column;
	}

	public Template[] getTemplate() {
		return template;
	}
	
	public String getPageDir(String key) {
		return pageDirMap.get(key);
	}
	
	protected String getRootPath(int siteID, int refLibID) {
		int libID = LibHelper.getLibIDByOtherLib(DocTypes.SITE.typeID(), refLibID);
		
		BaseDataCache siteCache = (BaseDataCache) (CacheReader.find(BaseDataCache.class));
		return siteCache.getSiteWebRootByID(libID, siteID);
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
}
