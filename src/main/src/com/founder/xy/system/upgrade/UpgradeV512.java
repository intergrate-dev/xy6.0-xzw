package com.founder.xy.system.upgrade;

import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.doc.Document;
import com.founder.e5.dom.DocLib;
import com.founder.e5.dom.DocLibManager;
import com.founder.e5.dom.DocType;
import com.founder.e5.dom.DocTypeManager;
import com.founder.e5.flow.Flow;
import com.founder.e5.flow.FlowManager;
import com.founder.e5.flow.ProcManager;
import com.founder.e5.flow.ProcUnflow;
import com.founder.e5.listpage.ListPage;
import com.founder.e5.listpage.ListPageManager;
import com.founder.e5.scheduler.db.SysJob;
import com.founder.e5.scheduler.db.SysJobManager;
import com.founder.e5.sys.SysConfig;
import com.founder.e5.sys.SysConfigManager;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.system.Tenant;
import com.founder.xy.system.site.SiteManager;

/**
 * V5.1.2的升级类，V5.1.2自动升级模板中使用
 * @author Gong Lijie
 */
public class UpgradeV512 {
	
	public void init() {
		/*
		//先刷新缓存，以便读新添加的文档类型等
		try {
			CacheManager.refresh(DocTypeCache.class);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		*/
		
		upgrade();
		
		//直播功能多，单独处理
		upgradeLive();
		
		setWebRoot();
	}

	/** 功能升级 */
	private void upgrade() {
		ProcManager procManager = (ProcManager)Context.getBean(ProcManager.class);
		try {
			//禁言：“解禁”操作改为“删除”，实际上升级模板里有“删除”，因此把原解禁操作删掉
			ProcUnflow proc = procManager.getUnflow(getDocTypeID("SHUTUP"), "解禁");
			if (proc != null) {
				procManager.deleteUnflow(proc.getProcID());
			}
			
			//互动问答：从非流程操作中去掉“回答”
			proc = procManager.getUnflow(getDocTypeID("QA"), "回答");
			if (proc != null) {
				procManager.deleteUnflow(proc.getProcID());
			}
			
			//稿件：去掉“栏目稿件设置”操作
			proc = procManager.getUnflow(getDocTypeID("ARTICLE"), "栏目稿件设置");
			if (proc != null) {
				procManager.deleteUnflow(proc.getProcID());
			}
			
			//删除参数：直播按话题分组、先发后审
			SysConfigManager configManager = (SysConfigManager)Context.getBean(SysConfigManager.class);
			SysConfig[] configs = configManager.get();
			if (configs != null) {
				for (SysConfig config : configs) {
					if (config.getItem().equals("直播按话题分组")) {
						configManager.delete(config.getSysConfigID());
					}
					if (config.getItem().equals("先发后审")) {
						configManager.delete(config.getSysConfigID());
					}
				}
			}
			
			//去掉无用的后台任务
			SysJobManager jobManager = (SysJobManager)Context.getBean(SysJobManager.class);
			SysJob[] jobs = jobManager.getJobs();
			if (jobs != null) {
				for (SysJob job : jobs) {
					if (job.getName().equals("归档服务") || job.getName().equals("图片文件清理服务")) {
						jobManager.deleteJob(job.getJobID());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** 直播功能升级 */
	private void upgradeLive() {
		int docTypeID = getDocTypeID("LIVE");
		int liveItemTypeID = getDocTypeID("LIVEITEM");
		
		if (docTypeID == 0 || liveItemTypeID == 0) return;
		
		DocLib liveLib = getLib(docTypeID);
		DocLib liveItemLib = getLib(liveItemTypeID);
		
		//去掉直播流程，以及流程下的所有操作
		removeLiveFlows(docTypeID);
		
		//删除列表：直播审核列表、直播跟帖列表
		removeLiveLists(docTypeID);
		
		//去掉直播的流程状态
		removeLiveFlowStatus(liveLib);
		
		//直播和它的报道都放在同一个表中。现在升级时需要转移已有的数据
		transferLiveItems(liveLib, liveItemLib);
			
		//直播报道的EUID初始化
		initLiveItemID(docTypeID, liveItemTypeID, liveLib);
	}
	
	private void setWebRoot() {
		SiteManager siteManager = (SiteManager)Context.getBean("siteManager");
		try {
			Document[] sites = siteManager.getSites(Tenant.DEFAULTCODE);
			if (sites.length > 0) {
				String webRoot = sites[0].getString("site_webRoot");
				SysConfigManager configManager = (SysConfigManager)Context.getBean(SysConfigManager.class);
				SysConfig config = new SysConfig();
				config.setAppID(1);
				config.setProject("发布服务");
				config.setItem("发布根目录");
				config.setValue(webRoot);
				
				configManager.create(config);
			}
		} catch (E5Exception e) {
			e.printStackTrace();
		}
	}

	//去掉直播流程，以及流程下的所有操作。去掉非流程操作：“查看评论”
	private void removeLiveFlows(int docTypeID) {
		FlowManager flowManager = (FlowManager)Context.getBean(FlowManager.class);
		try {
			Flow[] flows = flowManager.getFlows(docTypeID);
			if (flows != null && flows.length > 0) {
				for (Flow flow : flows) {
					flowManager.deleteFlow(flow.getID());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			//去掉非流程操作：“查看评论”
			ProcManager procManager = (ProcManager)Context.getBean(ProcManager.class);
			ProcUnflow proc = procManager.getUnflow(docTypeID, "查看评论");
			if (proc != null) {
				procManager.deleteUnflow(proc.getProcID());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//删除列表：直播审核列表、直播跟帖列表
	private void removeLiveLists(int docTypeID) {
		try {
			ListPageManager listManager = (ListPageManager)Context.getBean(ListPageManager.class);
			ListPage[] listPages = listManager.get(docTypeID);
			if (listPages != null) {
				for (ListPage listPage : listPages) {
					if (listPage.getListName().equals("直播审核列表") || listPage.getListName().equals("直播跟帖列表")) {
						listManager.delete(listPage);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//去掉直播的流程状态
	private void removeLiveFlowStatus(DocLib liveLib) {
		DBSession conn = null;
		try {
			conn = Context.getDBSession(liveLib.getDsID());
			String sql = "update " + liveLib.getDocLibTable()
					+ " set SYS_CURRENTFLOW=0, SYS_CURRENTNODE=0";
			conn.executeUpdate(sql, null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
	}

	//直播和它的报道都放在同一个表中。现在升级时需要转移已有的数据
	private void transferLiveItems(DocLib liveLib, DocLib liveItemLib) {
		DBSession conn = null;
		try {
			conn = Context.getDBSession(liveLib.getDsID());
			String sql = "insert into " + liveItemLib.getDocLibTable()
					+ "(SYS_DOCUMENTID,SYS_DOCLIBID,SYS_FOLDERID,SYS_CREATED,SYS_AUTHORS,SYS_AUTHORID,"
					+ "a_rootID,a_status,a_sourceType,a_content,a_attachments)"
					+ "select SYS_DOCUMENTID," + liveItemLib.getDocLibID() + "," + liveItemLib.getFolderID()
					+ ",SYS_CREATED,SYS_AUTHORS,SYS_AUTHORID,a_rootID,a_status,a_sourceType,a_content,a_attachments"
					+ " from " + liveLib.getDocLibTable()
					+ " where a_parentID>0";
			conn.executeUpdate(sql, null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
	}

	//直播报道的EUID初始化
	private void initLiveItemID(int docTypeID, int liveItemTypeID,
			DocLib liveLib) {
		DBSession conn = null;
		try {
			conn = Context.getDBSession(liveLib.getDsID());
			long itemID = InfoHelper.getNextDocID(docTypeID);
			String sql = "insert into E5ID(E5IDENTIFIER,E5VALUE) values(?,?)";
			conn.executeUpdate(sql, new Object[]{"DocID" + liveItemTypeID, itemID});
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
	}

	private DocLib getLib(int docTypeID) {
		DocLibManager docLibReader = (DocLibManager)Context.getBean(DocLibManager.class);
		try {
			DocLib[] docLibs =  docLibReader.getByTypeID(docTypeID);
			if (docLibs != null && docLibs.length > 0) {
				return docLibs[0];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private int getDocTypeID(String code) {
		DocTypeManager docTypeReader = (DocTypeManager)Context.getBean(DocTypeManager.class);
		try {
			DocType docType =  docTypeReader.getByCode(code);
			if (docType != null) {
				return docType.getDocTypeID();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
}
