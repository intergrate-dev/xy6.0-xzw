package com.founder.xy.jpublish;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.founder.e5.context.Cache;
import com.founder.e5.context.CacheManager;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.dom.DocLibManager;
import com.founder.xy.column.Column;
import com.founder.xy.column.ColumnCache;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.system.site.DomainDir;
import com.founder.xy.system.site.Site;
import com.founder.xy.system.site.SiteManager;
import com.founder.xy.system.site.SiteRule;
import com.founder.xy.system.site.SiteRuleManager;
import com.founder.xy.template.Template;
import com.founder.xy.template.TemplateManager;

/**
 * 发布服务中使用的基础数据缓存，包括站点、发布目录、发布规则、模板
 * 供发布服务使用
 * 供互动系统审批通过时发布直播/论坛图片时（读站点目录）使用
 * @author Gong Lijie
 */
public class BaseDataCache implements Cache {
	//站点根地址 <docLibID, <siteID, siteWebRoot>>
	private Map<Integer, Map<Integer, Site>> sites = new HashMap<>();
	
	//发布规则 <docLibID, <ruleID, rule>>
	private Map<Integer, Map<Long, SiteRule>> siteRules = new HashMap<>(); 
	
	//发布目录 <docLibID, <dirID, dir>>
	private Map<Integer, Map<Long, DomainDir>> dirs = new HashMap<>(); 
	
	//模板 <docLibID, <templateID, template>>
	private Map<Integer, Map<Long, Template>> templates = new HashMap<>();
	
	//栏目影响到的其它栏目（栏目页上显示某栏目里的稿件列表） <docLibID, <columnID, set>>
	private Map<Integer, Map<Long, Set<Long>>> columns_relating = new HashMap<>();

	/** 启动时执行的全部缓存刷新 */
	@Override
	public void refresh() throws E5Exception {
		int[] docLibIDs = getDocLibs(DocTypes.SITE.typeID());
		for (int docLibID : docLibIDs) {
			refreshSite(docLibID);
		}
		
		docLibIDs = getDocLibs(DocTypes.DOMAINDIR.typeID());
		for (int docLibID : docLibIDs) {
			refreshDir(docLibID);
		}
		
		docLibIDs = getDocLibs(DocTypes.SITERULE.typeID());
		for (int docLibID : docLibIDs) {
			refreshRule(docLibID);
		}
		
		docLibIDs = getDocLibs(DocTypes.TEMPLATE.typeID());
		for (int docLibID : docLibIDs) {
			refreshTemplate(docLibID);
			
			//这是重启服务时的刷新，要保证栏目缓存和模板缓存先完成刷新
			int colLibID = LibHelper.getLibIDByOtherLib(DocTypes.COLUMN.typeID(), docLibID);
			refreshColumnRelating(docLibID, colLibID);
		}
	}
	
	/**
	 * 刷新一个租户的站点数据，提供租户的站点库ID
	 * @param docLibID
	 */
	public void refreshSite(int docLibID) {
		SiteManager siteManager = (SiteManager)Context.getBean("siteManager");
		Map<Integer, Site> map = new HashMap<>();
		
		try {
			List<Site> oneLibsites = siteManager.getSites(docLibID);
			for (Site site : oneLibsites) {
				map.put(site.getId(), site);
			}
			
			sites.put(docLibID, map);
		} catch (E5Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 刷新一个租户的发布目录数据，提供租户的发布目录库ID
	 * @param docLibID
	 */
	public void refreshDir(int docLibID) {
		SiteManager siteManager = (SiteManager)Context.getBean("siteManager");
		Map<Long, DomainDir> map = siteManager.getDomainDirs(docLibID);
		
		dirs.put(docLibID, map);
	}
	/**
	 * 刷新一个租户的发布规则数据，提供租户的发布规则库ID
	 * @param docLibID
	 */
	public void refreshRule(int docLibID) {
		SiteRuleManager siteRuleManager = (SiteRuleManager)Context.getBean("siteRuleManager");
		Map<Long, SiteRule> map = siteRuleManager.getSiteRules();

		//加上目录
		int dirLibID = LibHelper.getLibIDByOtherLib(DocTypes.DOMAINDIR.typeID(), docLibID);
		for (long ruleID : map.keySet()) {
			SiteRule siteRule = map.get(ruleID);
			
			siteRule.setColumnPath(getRulePath(dirLibID, siteRule.getColumnDirID()));
			siteRule.setArticlePath(getRulePath(dirLibID, siteRule.getArticleDirID()));
			siteRule.setPhotoPath(getRulePath(dirLibID, siteRule.getPhotoDirID()));
			siteRule.setAttachPath(getRulePath(dirLibID, siteRule.getAttachDirID()));
		}
		
		siteRules.put(docLibID, map);
	}
	/**
	 * 刷新一个租户的模板数据，提供租户的模板库ID
	 * @param docLibID
	 */
	public void refreshTemplate(int docLibID) {
		TemplateManager templateManager = (TemplateManager)Context.getBean("templateManager");
		Map<Long, Template> map = templateManager.getTemplates();
		
		templates.put(docLibID, map);
	}
	
	//刷新栏目相关更新缓存（模板文件变化时、栏目选了不同的栏目模板时）
	public void refreshColumnRelating(int tLibID, int colLibID) {
		Map<Long, Set<Long>> result = new HashMap<>();
		
		ColumnCache columnCache = (ColumnCache)CacheManager.find(ColumnCache.class);
		Column[] columns = columnCache.get(colLibID);
		
		for (Column column : columns) {
			set(tLibID, column.getTemplate(), column.getId(), result);
			set(tLibID, column.getTemplatePad(), column.getId(), result);
		}
		
		//按栏目缓存
		columns_relating.put(colLibID, result);
	}
	
	/**
	 * 读一个栏目会影响到的其它栏目（影响栏目页展示）
	 */
	public long[] getColumnRelating(int colLibID, long colID) {
		Map<Long, Set<Long>> oneLib = columns_relating.get(colLibID);
		if (oneLib == null) return null;
		
		Set<Long> oneCol = oneLib.get(colID);
		if (oneCol == null) return null;
		
		long[] result = new long[oneCol.size()];
		int i = 0;
		for (Long l : oneCol) {
			result[i++] = l;
		}
		
		return result;
	}

	/** 读站点目录 */
	public String getSiteWebRootByID(int libID, int siteID){
		return sites.get(libID).get(siteID).getWebRoot();
	}
	
	public DomainDir getDir(int libID, long dirID){
		return dirs.get(libID).get(dirID);
	}
	public SiteRule getSiteRuleByID(int libID, long ruleID){
		return siteRules.get(libID).get(ruleID);
	}

	public Template getTemplateByID(int libID, long templateID){
		return templates.get(libID).get(templateID);
	}

	@Override
	public void reset() {
		
	}

	private int[] getDocLibs(int docTypeID) throws E5Exception {
		DocLibManager docLibManager = (DocLibManager)Context.getBean(DocLibManager.class);
		int[] docLibIDs = docLibManager.getIDsByTypeID(docTypeID);
	
		return docLibIDs;
	}
	
	/**
	 * 得到规则磁盘存储的路径
	 */
	private String getRulePath(int dirLibID, long dirID){
		DomainDir dir = getDir(dirLibID, dirID);
		return (dir == null) ? "" : dir.getPath();
	}

	//按一个模板设置栏目相关
	private void set(int tLibID, long tID, long colID, Map<Long, Set<Long>> result) {
		if (tID == 0) return;
		
		//取出栏目的受影响的栏目ID
		Template t = getTemplateByID(tLibID, tID);
		if (t == null) return;
		
		long[] colRelated = t.getColRelated();
		if (colRelated == null || colRelated.length == 0) return;
		
		//每个受影响的栏目ID的对应set中，添加当前栏目
		for (long relatedID : colRelated) {
			if (relatedID == colID) continue;//若栏目模板中引用了自身栏目ID，需过滤掉
			
			Set<Long> rel = result.get(relatedID);
			if (rel == null) {
				rel = new HashSet<>();
			}
			rel.add(colID);
			
			result.put(relatedID, rel);
		}
	}
	
}
