package com.founder.xy.jpublish.context;

import org.apache.commons.lang.StringUtils;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.context.CacheReader;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.xy.column.ColumnReader;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.jms.data.DocIDMsg;
import com.founder.xy.jpublish.BaseDataCache;
import com.founder.xy.system.site.SiteRule;
import com.founder.xy.template.Template;

/**
 * 栏目页发布的上下文环境
 * @author Gong Lijie
 */
public class ColumnContext extends AbstractContext{
	private DocIDMsg message;
	private ColumnReader columnReader;
	
	public void init(DocIDMsg message){
		this.message = message;
		if ("specialPreview".equals(message.getRelIDs())){
			//专题预览时 栏目ID为专题模板ID
			int libID = LibHelper.getLibIDByOtherLib(DocTypes.TEMPLATE.typeID(), message.getDocLibID());
			BaseDataCache templateCache = (BaseDataCache) (CacheReader.find(BaseDataCache.class));
			this.template = new Template[]{templateCache.getTemplateByID(libID, message.getDocID())};
			return;
		}

		columnReader = (ColumnReader)Context.getBean("columnReader");
		try {
			column = columnReader.get(message.getDocLibID(), message.getDocID());
			if (column == null) return;
		} catch (E5Exception e) {
			e.printStackTrace();
		}

		//设置栏目生成模板
		setTemplate();
		
		setPageDir();

	}
	
	public DocIDMsg getMessage(){
		return message;
	}
	
	private void setTemplate() {
		int libID = LibHelper.getLibIDByOtherLib(DocTypes.TEMPLATE.typeID(), message.getDocLibID());
		
		BaseDataCache templateCache = (BaseDataCache) (CacheReader.find(BaseDataCache.class));
		
		Template[] template = new Template[2];
		template[0] = templateCache.getTemplateByID(libID, column.getTemplate());
		template[1] = templateCache.getTemplateByID(libID, column.getTemplatePad());
		
		this.template = template;
	}
	
	private void setPageDir(){
		int libID = LibHelper.getLibIDByOtherLib(DocTypes.SITERULE.typeID(), message.getDocLibID());
		
		BaseDataCache siteRuleCache = (BaseDataCache) (CacheReader.find(BaseDataCache.class));
		SiteRule siteRule0 = siteRuleCache.getSiteRuleByID(libID, column.getPubRule());
		SiteRule siteRule1 = siteRuleCache.getSiteRuleByID(libID, column.getPubRulePad());
		
		String rootPath = getRootPath(column.getSiteID(), message.getDocLibID());
		pageDirMap.put("root", rootPath);
		
		pageDirMap.put("column", getColumnDir(rootPath, siteRule0, 0, template[0]));
		pageDirMap.put("columnPad", getColumnDir(rootPath, siteRule1, 1, template[1]));
		
		//把发布地址也读出来
		String url = columnReader.getColumnUrl(column.getId(), column.getFileName(), template[0], siteRule0);
		String urlPad = columnReader.getColumnUrl(column.getId(), column.getFileNamePad(), template[1], siteRule1);
		pageDirMap.put("url", url);
		pageDirMap.put("urlPad", urlPad);
	}
	
	private String getColumnDir(String siteWebRoot, SiteRule siteRule,int type, Template template){
		StringBuffer dir = new StringBuffer();
		if (siteRule != null) {
			dir.append(siteWebRoot).append(siteRule.getColumnPath());
			
			if (siteRule.isColumnByDate()) {
				String datePath = DateUtils.format("yyyyMM/dd");
				dir.append("/").append(datePath);
			}

			String ext =  "." + getSuffix(template);

			String fileName = (type == 0) ? column.getFileName() : column.getFileNamePad();
			if (!StringUtils.isBlank(fileName)) {
				//栏目页优先使用栏目名称中的后缀名
				dir.append("/" + fileName);
				//栏目名称中没有后缀名，使用模板后缀名
				if(!fileName.contains("."))
					dir.append(ext);
			} else {
				String prefix = InfoHelper.getConfig( "发布服务", "栏目生成页前缀");
				if (prefix == null || "".equals(prefix))
					prefix = "col";
				//大洋网希望栏目文件名称为 栏目ID,以$符号来标记
				else if("$".equals(prefix))
					prefix = "";

				dir.append("/" + prefix + column.getId() + ext);
			}
		}
		return dir.toString();
	}


}
