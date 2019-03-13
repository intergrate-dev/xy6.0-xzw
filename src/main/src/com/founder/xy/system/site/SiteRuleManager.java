package com.founder.xy.system.site;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;

/**
 * 发布规则的管理器
 */
@Component
public class SiteRuleManager {
	
	private static String SQL_DELETENOTUSEDRULE = "delete from xy_siterule where SYS_DOCUMENTID = ? ";
	
	
	public void deleteSiteRule(long docID) throws E5Exception {
		int siteRuleLibID = LibHelper.getSiteRuleLibID();

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		
		docManager.delete(siteRuleLibID, docID);
	}
	
	public void deleteNotUsedRule(long docID) throws E5Exception {
		
		InfoHelper.executeUpdate(SQL_DELETENOTUSEDRULE, new Object[] {docID});
	}
	
	public String getRuleNameByID(long docID) throws E5Exception {
		int siteRuleLibID = LibHelper.getSiteRuleLibID();
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document rule = docManager.get(siteRuleLibID, docID);
		String name = "";
		if(rule != null){
			name = rule.getString("rule_name");
		}
		return name;
	}
	//判断发布规则是否被引用
	public boolean ruleUsed(int ruleLibID, long ruleId) throws E5Exception {
		int columnLibID = LibHelper.getLibIDByOtherLib(DocTypes.COLUMN.typeID(), ruleLibID);
		String sql = "col_pubRule_ID=? or col_pubRulePad_ID=?";
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] rules = docManager.find(columnLibID, sql, new Object[]{ruleId, ruleId});
		
		return (rules != null && rules.length > 0);
	}
	
	/**
	 * 读所有的站点发布规则。给缓存刷新使用的方法
	 * @return
	 */
	public Map<Long, SiteRule> getSiteRules(){
		Map<Long, SiteRule> siteRules = new HashMap<Long, SiteRule>();
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			DocLib[] libs = LibHelper.getLibs(DocTypes.SITERULE.typeID());
			
			for (DocLib docLib : libs) {
				Document[] rules = docManager.find(docLib.getDocLibID(), "SYS_DELETEFLAG=0", null);
				
				int dirLibID = LibHelper.getLibIDByOtherLib(DocTypes.DOMAINDIR.typeID(), docLib.getDocLibID());
				for (Document doc : rules) {
					SiteRule siteRule = assembleRule(doc, dirLibID);
					siteRules.put(doc.getDocID(), siteRule);
				}
			}
		} catch (E5Exception e) {
			e.printStackTrace();
		}
		return siteRules;
	}

	private SiteRule assembleRule(Document doc, int dirLibID) {
		SiteRule siteRule = new SiteRule();
		
		siteRule.setRuleID(doc.getDocID());
		siteRule.setSiteID(doc.getInt("rule_siteID"));
		siteRule.setRuleName(doc.getString("rule_name"));
		
		siteRule.setColumnDir(doc.getString("rule_column_dir"));
		siteRule.setColumnDirID(doc.getLong("rule_column_dir_ID"));
		
		siteRule.setArticleDir(doc.getString("rule_article_dir"));
		siteRule.setArticleDirID(doc.getLong("rule_article_dir_ID"));
		
		siteRule.setPhotoDir(doc.getString("rule_photo_dir"));
		siteRule.setPhotoDirID(doc.getLong("rule_photo_dir_ID"));
		
		siteRule.setAttachDir(doc.getString("rule_attach_dir"));
		siteRule.setAttachDirID(doc.getLong("rule_attach_dir_ID"));
		
		siteRule.setArticleByDate(doc.getBoolean("rule_article_date"));
		siteRule.setColumnByDate(doc.getBoolean("rule_column_date"));
		
		return siteRule;
	}
}