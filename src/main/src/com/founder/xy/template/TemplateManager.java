package com.founder.xy.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;

/**
 * 模板缓存刷新使用
 * @author Gong Lijie
 */
@Component
public class TemplateManager {

	public Map<Long, Template> getTemplates(){
		Map<Long, Template> templates = new HashMap<Long, Template>();
		
		int tplLibID = LibHelper.getTemplateLibID();
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			Document[] tpls = docManager.find(tplLibID, 
					"(t_expireDate is null or t_expireDate>?) and SYS_DELETEFLAG=0", 
					new Object[]{DateUtils.getTimestamp()});
			Template template = null;
			for (Document doc : tpls) {
				template = new Template(doc);
				templates.put(doc.getDocID(), template);
			}
		} catch (E5Exception e) {
			e.printStackTrace();
		}
		return templates;
	}
	
	/**
	 * 清理Redis里的过期模板（专题模板），包括模板文件、模板组件信息
	 */
	public void clearExpired() {
		List<Long> ids = getTemplatesExpired();
		for (long tID : ids) {
			RedisManager.hclear(RedisKey.TEMPLATE_FILE_KEY, tID);
			RedisManager.hclear(RedisKey.TEMPLATE_CO_KEY, tID);
		}
	}

	/**
	 * 取出过期的模板（专题模板），以备清理
	 * @return
	 */
	private List<Long> getTemplatesExpired(){
		List<Long> ids = new ArrayList<>();
		
		int tplLibID = LibHelper.getTemplateLibID();
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			Document[] tpls = docManager.find(tplLibID, "t_expireDate<?", new Object[]{DateUtils.getTimestamp()});
			for (Document doc : tpls) {
				ids.add(doc.getDocID());
			}
		} catch (E5Exception e) {
			e.printStackTrace();
		}
		return ids;
	}
}
