package com.founder.xy.column;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.CacheManager;
import com.founder.e5.context.CacheReader;
import com.founder.e5.context.E5Exception;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.config.TabHelper;
import com.founder.xy.jpublish.BaseDataCache;
import com.founder.xy.system.site.SiteRule;
import com.founder.xy.system.site.SiteUserReader;
import com.founder.xy.template.Template;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ColumnReader {

	@Autowired
	private SiteUserReader userReader;
	
	/**
	 * 获得聚合某栏目的所有栏目集合
	 */
	public Set<Long> getAggregators(int colLibID, long columnID) {
		ColumnCache columnCache = (ColumnCache) (CacheReader.find(ColumnCache.class));
		Set<Long> aggregatedSet = columnCache.getAggregators(colLibID, columnID);
		
		return aggregatedSet;
	}
	
	/**
	 * 根据ID得到栏目对象
	 */
	public Column get(int colLibID, long colID) throws E5Exception {
		ColumnCache columnCache = (ColumnCache) (CacheReader.find(ColumnCache.class));
		return columnCache.get(colLibID, colID);
	}
	
	/**
	 * 取栏目的子栏目
	 */
	public List<Column> getSub(int colLibID, long parentID) throws E5Exception {
		ColumnCache columnCache = (ColumnCache) (CacheReader.find(ColumnCache.class));
		return columnCache.getSub(colLibID, parentID);
	}

	/**
	 * 取根栏目。channel=0表示Web栏目，1表示App栏目
	 */
	public List<Column> getRoot(int siteID, int colLibID, int channel) throws E5Exception {
		ColumnCache columnCache = (ColumnCache) (CacheReader.find(ColumnCache.class));
		List<Column> cols = columnCache.getSub(colLibID, 0);
		List<Column> result = new ArrayList<>();
		for (Column col : cols) {
			if (col.getSiteID() == siteID && col.getChannel() == channel) {
				result.add(col);
			}
		}
		return result;
	}

	/** 读一个站点下的审批栏目 */
	public List<Column> getAuditColumns(int colLibID, int siteID) {
		ColumnCache columnCache = (ColumnCache) (CacheReader.find(ColumnCache.class));
		return columnCache.getAuditColumns(colLibID, siteID);
	}

	/**
	 * 取用户的可操作的栏目
	 */
	public Column[] getOpColumns(int colLibID, int userID, int siteID, int channelType, int roleID) throws E5Exception {
		//若是移动版，则可操作栏目的type是4
		int type = channelType == 0 ? 0 : 4;
		int userLibID = LibHelper.getLibIDByOtherLib(DocTypes.USEREXT.typeID(), colLibID);
		long[] ids = userReader.getRelated(userLibID, userID, siteID, type);
		
		//取出按角色设置的栏目权限，准备合并
		String sourceType = getSourceType(siteID,type,"Op");
		long[] roleColIDs = TabHelper.getColIDsByRole(roleID,sourceType);
		
		//权限合并
		ids = unite(ids,roleColIDs);
		
		return getColumns(colLibID, ids);
	}

	//两个long数组取并集
	private long[] unite(long[] data1,long[] data2){
		Set<Long> set = new HashSet<>();
		
		for (int i = 0; data1 != null && i < data1.length; i++) {
			set.add(data1[i]);
		}
		for (int i = 0; data2 != null && i < data2.length; i++) {
			set.add(data2[i]);
		}

		long[] result = new long[set.size()];
		int j = 0;
		for (long i : set) {
			result[j] = i;
			j++;
		}
		return result;
	}

	private String getSourceType(int siteID, int channelType, String type) {
		String result = "Column"+type;
		switch (channelType){
			case 0:
				return result+"Web"+siteID;
			case 4:
				return result+"App"+siteID;
		}
		return result;
	}
	
	/**
	 * 取用户可操作的栏目ID
	 */
	public Long[] getOpColumnIds(int colLibID, int userID, int siteID, int channelType) throws E5Exception {
		//若是移动版，则可操作栏目的type是4
		int type = channelType == 0 ? 0 : 4;
		
		List<Long> idList = new ArrayList<Long>();
		int userLibID = LibHelper.getLibIDByOtherLib(DocTypes.USEREXT.typeID(), colLibID);
		long[] ids = userReader.getRelated(userLibID, userID, siteID, type);
		if(ids!=null&&ids.length>0){
			for (long id : ids) {
				idList.add(id);
				iterateColumnIds(id,colLibID,idList);
			}
		}
		return idList.toArray(new Long[idList.size()]);
	}
	
	//迭代子孙栏目ID
	public List<Long> iterateColumnIds(long id,int colLibID,List<Long> idList) throws E5Exception {
		List<Column> columns = getSub(colLibID, id);
		if(columns!=null){
			for (Column column : columns) {
				idList.add(column.getId());
				iterateColumnIds(column.getId(),colLibID,idList);
			}
		}
		return idList;
	}
	
	/**
     * 得到栏目的发布地址。
     * 返回两个字符串的数组，第一个表示网站版地址，第二个表示触屏版地址
     */
	public String[] getUrls(int colLibID, long colID) throws E5Exception {
		String[] result = new String[2];
		
		Column col = get(colLibID, colID);
        if (col != null) {
            int ruleLibID = LibHelper.getLibIDByOtherLib(DocTypes.SITERULE.typeID(), colLibID);

        	result[0] = getColumnUrl(ruleLibID, col.getPubRule(), colID, col.getFileName(), col.getTemplate());
        	result[1] = getColumnUrl(ruleLibID, col.getPubRulePad(), colID, col.getFileNamePad(), col.getTemplatePad());
        }
		return result;
	}
    /**
     * 得到栏目的相关信息。
     * 返回发布URL，栏目模板，发布规则等信息
     */
	public HashMap<String,String> getDetails(int colLibID, long colID) throws E5Exception {
		HashMap<String,String> result = new HashMap<String,String>();
		
		Column col = get(colLibID, colID);
        if (col != null) {
            int ruleLibID = LibHelper.getLibIDByOtherLib(DocTypes.SITERULE.typeID(), colLibID);
            
            String webColumnUrl = getColumnUrl(ruleLibID, col.getPubRule(), colID, col.getFileName(), col.getTemplate());
            String padColumnUrl = getColumnUrl(ruleLibID, col.getPubRulePad(), colID, col.getFileNamePad(), col.getTemplatePad());
        	BaseDataCache templateCache = (BaseDataCache)CacheManager.find(BaseDataCache.class);
        	int templateLibID =  LibHelper.getLibIDByOtherLib(DocTypes.TEMPLATE.typeID(), ruleLibID);
        	Template webTemplate = templateCache.getTemplateByID(templateLibID, col.getTemplate()); 
        	Template padTemplate = templateCache.getTemplateByID(templateLibID, col.getTemplatePad()); 
        	SiteRule webPubRule = templateCache.getSiteRuleByID(ruleLibID, col.getPubRule());
        	SiteRule padPubRule = templateCache.getSiteRuleByID(ruleLibID, col.getPubRulePad());
        	
        	result.put("colID", colID+"");
        	result.put("colName", col.getName());
        	result.put("colCascadeID", col.getCasIDs());
        	result.put("colCascadeName", col.getCasNames());
        
        	if(webColumnUrl!=null){
        		result.put("webColumnUrl", webColumnUrl);        		
        	}
        	else result.put("webColumnUrl", "");    
        	if(padColumnUrl!=null){
        		result.put("padColumnUrl", padColumnUrl);        		
        	}
        	else result.put("padColumnUrl", "");  
        	if(webTemplate!=null){
        		result.put("webTemplateID", webTemplate.getId()+"");
        		result.put("webTemplateName", webTemplate.getName());
        	}
        	
        	else{
        		result.put("webTemplateID", "");
        		result.put("webTemplateName", "");
        		}
        	if(padTemplate!=null){
        		result.put("padTemplateID", padTemplate.getId()+"");
        		result.put("padTemplateName", padTemplate.getName());
        	}
        	else{
        		result.put("padTemplateID", "");
        		result.put("padTemplateName", "");
        		}
        	if(webPubRule!=null){
        		result.put("webRuleID", webPubRule.getRuleID()+"");
        		result.put("webRuleName", webPubRule.getRuleName());
        		result.put("webRuleColumnUrl", webPubRule.getColumnDir());
        		result.put("webRuleArticleUrl", webPubRule.getArticleDir());
        		result.put("webRuleArticlePicUrl", webPubRule.getPhotoDir());
        		result.put("webRuleAttcUrl", webPubRule.getAttachDir());
        	}
        	else{
        		result.put("webRuleID", "");
        		result.put("webRuleName", "");
        		result.put("webRuleColumnUrl", "");
        		result.put("webRuleArticleUrl", "");
        		result.put("webRuleArticlePicUrl", "");
        		result.put("webRuleAttcUrl", "");
        	}
        	if(padPubRule!=null){
        		result.put("padRuleID", padPubRule.getRuleID()+"");
        		result.put("padRuleName", padPubRule.getRuleName());
        		result.put("padRuleColumnUrl", padPubRule.getColumnDir());
        		result.put("padRuleArticleUrl", padPubRule.getArticleDir());
        		result.put("padRuleArticlePicUrl", padPubRule.getPhotoDir());
        		result.put("padRuleAttcUrl", padPubRule.getAttachDir());
        	}
        	else{
        		result.put("padRuleID", "");
        		result.put("padRuleName", "");
        		result.put("padRuleColumnUrl", "");
        		result.put("padRuleArticleUrl", "");
        		result.put("padRuleArticlePicUrl", "");
        		result.put("padRuleAttcUrl", "");
        	}
        }
		return result;
	}
	

	
    /**
     * 得到确定的栏目发布地址，用于发布服务中取栏目页地址
     * 
     * @param colID 栏目ID
     * @param fileName 栏目页指定的发布名
     * @param template 栏目页的发布模板
     * @param rule 栏目页的发布规则
     * @return 栏目的发布url
     */
    public String getColumnUrl(long colID, String fileName, Template template, SiteRule rule) {
		//该栏目没有发布规则
		if (rule != null) {
		    String path = rule.getColumnDir();
		    if (!path.endsWith("/")) path += "/";
	
		    boolean colByDate = rule.isColumnByDate();
		    if (colByDate) path += DateUtils.format("yyyyMM/dd/");
		    
		    String ext =  "." + getSuffix(template);

			if (!StringUtils.isBlank(fileName)) {
				//栏目页优先使用栏目名称中的后缀名
				path += fileName ;
				//栏目名称中没有后缀名，使用模板后缀名
				if(!fileName.contains("."))
					path += ext;
			}
		    else {
		    	String prefix = InfoHelper.getConfig( "发布服务", "栏目生成页前缀");
				if (prefix == null || "".equals(prefix))
					prefix = "col";
				//大洋网希望栏目文件名称为 栏目ID,以$符号来标记
				else if("$".equals(prefix))
					prefix = "";
		        path += prefix + colID + ext;
		    }
		    return path;
		}
		return null;
	}



	//取出栏目页html路径
    private String getColumnUrl(int ruleLibID, long ruleID, long colID, String fileName, long templateID) throws E5Exception {
    	BaseDataCache templateCache = (BaseDataCache)CacheManager.find(BaseDataCache.class);
    	int templateLibID =  LibHelper.getLibIDByOtherLib(DocTypes.TEMPLATE.typeID(), ruleLibID);
    	Template template = templateCache.getTemplateByID(templateLibID, templateID);  	
    	if (ruleID > 0 && templateID > 0) {
    		SiteRule rule = templateCache.getSiteRuleByID(ruleLibID, ruleID);
        	return getColumnUrl(colID, fileName, template, rule);
        }
    	return null;
    }

	/**
	 * 由模板确定的发布文件的后缀，html/json/xml
	 */
	private String getSuffix(Template t) {
		if (t == null || StringUtils.isBlank(t.getFileType()))
			return "html";
		else
			return t.getFileType();
	}

	@SuppressWarnings("unchecked")
	private Column[] getColumns(int colLibID, long[] ids) throws E5Exception {
		if (ids == null)
			return null;

		List<Column> docs = new ArrayList<Column>();

		for (int i = 0; i < ids.length; i++) {
			Column col = get(colLibID, ids[i]);
			if (col != null)
				docs.add(col);
		}
		Collections.sort(docs, new Comparator<Column>() {
			public int compare(Column me1, Column me2) {
				return new Integer(me1.getOrder()).compareTo(me2.getOrder());
			}
		});
		
		return docs.toArray(new Column[0]);
	}
/**
 * 取用户收藏的栏目，根据id查找
 */
	public Column[] getPersonalCollection(int colLibID, int userID, int siteID,
			int type) throws E5Exception {
		int userLibID = LibHelper.getLibIDByOtherLib(DocTypes.USEREXT.typeID(), colLibID);
		long[] ids = userReader.getRelated(userLibID, userID, siteID, type);
		return getColumns(colLibID, ids);
	}
}
