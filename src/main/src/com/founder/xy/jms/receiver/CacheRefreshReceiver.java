package com.founder.xy.jms.receiver;

import com.founder.e5.context.CacheManager;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.permission.merge.PermissionCache;
import com.founder.e5.sys.org.OrgRoleUserCache;
import com.founder.xy.block.BlockCache;
import com.founder.xy.column.Column;
import com.founder.xy.column.ColumnCache;
import com.founder.xy.column.ColumnReader;
import com.founder.xy.commons.BaseCache;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.jms.data.DocIDMsg;
import com.founder.xy.jpublish.BaseDataCache;
import com.founder.xy.set.ExtFieldCache;
import com.founder.xy.set.SourceCache;
import com.founder.xy.system.site.SiteUserCache;

/**
 * Web系统内使用的消息接收器。
 * 当系统部署为多个App Server时，数据修改造成的缓存刷新必须在每个server上都执行一遍，因此使用消息发布订阅的形式。
 * 
 * 几种消息的接收：栏目修改、模板修改、模板挂接栏目、扩展字段、站点发布规则等。
 * 参考发布服务中的同名类。
 * @author Gong Lijie
 */
public class CacheRefreshReceiver {
	private boolean startup = false; //判断系统是否启动了
	
	/** 接收一条消息，分配给一个任务进行处理 */
	public void receive(DocIDMsg data) {
		System.out.println(Thread.currentThread().getName() + ":" + data.getDocLibID() + "," + data.getDocID());
		
		//系统还未启动完，消息丢弃（因为都是刷新缓存的消息，而系统启动时肯定会刷新缓存，所以这些消息可忽略）
		if (!checkStartup())
			return;
		
		switch (data.getType()) {
			case DocIDMsg.TYPE_COLUMN: {
				runColumn(data);
				break;
			}
			case DocIDMsg.TYPE_TEMPLATE_GRANT: {
				runTemplateGrant(data);
				break;
			}
			/** 刷新扩展字段缓存 */
			case DocIDMsg.TYPE_EXTFIELD: {
				refreshCache(ExtFieldCache.class, data);
				break;
			}
			/** 刷新来源缓存 */
			case DocIDMsg.TYPE_SOURCE: {
				refreshCache(SourceCache.class, data);
				break;
			}
			/** 只刷新栏目缓存，不判断其它 */
			case DocIDMsg.TYPE_COLUMN_REFRESHONLY: {
				refreshCache(ColumnCache.class, data);
				break;
			}
			/** 刷新机构用户缓存 */
			case DocIDMsg.TYPE_ORGUSER: {
				refreshOrgUser(data);
				break;
			}
			/** 刷新用户关联信息缓存 */
			case DocIDMsg.TYPE_USERREL: {
				refreshUserRel(data);
				break;
			}
			/** 前台设置权限并刷新时 */
			case DocIDMsg.TYPE_PERMISSION: {
				refreshPermission();
				break;
			}
			
			case DocIDMsg.TYPE_SITE: {
				runBaseData(data, 0);
				break;
			}
			case DocIDMsg.TYPE_DOMAINDIR: {
				runBaseData(data, 1);
				break;
			}
			case DocIDMsg.TYPE_SITERULE: {
				runBaseData(data, 2);
				break;
			}
			case DocIDMsg.TYPE_TEMPLATE: {
				runBaseData(data, 3);
				break;
			}
			case DocIDMsg.TYPE_COLUMN_SYNC: {
				runColumnSync(data);
				break;
			}
			case DocIDMsg.TYPE_BLOCK: {
				refreshCache(BlockCache.class, data);
				break;
			}
			case DocIDMsg.TYPE_CREATE_SITE: {
				refreshCache(ColumnCache.class, data);
				//新建站点需要刷新较多缓存，先后之间有依赖，不能使用多线程
				runBaseData(data, -1);
				break;
			}
			default: {
				break;
			}
		}
	}
	
	//检查系统是否启动完毕
	private boolean checkStartup() {
		if (!startup) {
			try {
				//试着找一个缓存类，以判断是否启动完毕
				if (CacheManager.find(ColumnCache.class) != null) {
					startup = true; //可能有并发，都是设为true，不影响
				}
			} catch (Exception e) {
			}
		}
		return startup;
	}
	
	/*
	 * 栏目变化
	 * 
	 * 2）检查栏目的父节点是否有变化，若有变化（拖拽改变父栏目），则刷新相关的栏目时间
	 * 3）刷新发布服务中使用的栏目缓存。
	 * 4）检查栏目的修改时间是否有变化，若有变化，则刷新父栏目的子栏目列表（for App）
	 */
	private void runColumn(DocIDMsg data) {
		//在刷新栏目缓存前，用旧缓存判断是否有栏目相关的变化
		//boolean needRefreshRelated = needRefreshRelated(data);
		
		//刷新栏目缓存
		refreshCache(ColumnCache.class, data);
		
		/*
		 * 似乎WEB系统里用不到这个缓存，只在发布服务中才用到？
		//若设置了栏目模板，则可能改变栏目相关，需重刷栏目相关缓存
		if (needRefreshRelated) {
			BaseDataCache cache = (BaseDataCache)CacheManager.find(BaseDataCache.class);
			
			int tLibID = LibHelper.getLibIDByOtherLib(DocTypes.TEMPLATE.typeID(), data.getDocLibID());
			cache.refreshColumnRelating(tLibID, data.getDocLibID());
		}
		*/
	}
	
	/*
	 * 栏目同步到子栏目时，刷新栏目缓存、以及相关缓存
	 */
	private void runColumnSync(DocIDMsg data) {
		//刷新栏目缓存
		refreshCache(ColumnCache.class, data);
		
		/* 似乎WEB系统里用不到这个缓存，只在发布服务中才用到？
		//刷新栏目相关缓存
		BaseDataCache cache = (BaseDataCache)CacheManager.find(BaseDataCache.class);
		int tLibID = LibHelper.getLibIDByOtherLib(DocTypes.TEMPLATE.typeID(), data.getDocLibID());
		cache.refreshColumnRelating(tLibID, data.getDocLibID());
		*/
	}
	
	/**
	 * 模板挂接栏目：刷新栏目缓存
	 */
	private void runTemplateGrant(DocIDMsg data) {
		try {
			int colLibID = LibHelper.getLibIDByOtherLib(DocTypes.COLUMN.typeID(), data.getDocLibID());
			refreshCache(ColumnCache.class, new DocIDMsg(colLibID, 0, null));
			
			//若是栏目模板，则可能改变栏目相关，需重刷缓存
			tryRefreshColumnRelating(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 刷新站点用户的缓存（包括E5机构用户缓存、站点用户缓存）
	 */
	private void refreshOrgUser(DocIDMsg data) {
		try {
			CacheManager.refresh(OrgRoleUserCache.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		refreshCache(SiteUserCache.class, data);
	}
	private void refreshUserRel(DocIDMsg data) {
		try {
			SiteUserCache cache = (SiteUserCache)CacheManager.find(SiteUserCache.class);
			cache.refreshRel(data.getDocLibID());
		} catch (E5Exception e) {
			e.printStackTrace();
		}
	}
	
	private void refreshPermission() {
		try {
			CacheManager.refresh(OrgRoleUserCache.class);
			CacheManager.refresh(PermissionCache.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void refreshCache(Class<?> c, DocIDMsg data) {
		BaseCache cache = (BaseCache)CacheManager.find(c);
		try {
			cache.refresh(data.getDocLibID());
		} catch (E5Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 刷新缓存
	 */
	private void runBaseData(DocIDMsg data, int type) {
		BaseDataCache cache = (BaseDataCache)CacheManager.find(BaseDataCache.class);
		
		switch (type) {
		case -1://刷新全部缓存
			try {
				cache.refresh();
			} catch (E5Exception e) {
				e.printStackTrace();
			}
			break;
		case 0://刷新站点缓存
			cache.refreshSite(data.getDocLibID());
			break;
		case 1://刷新发布目录缓存
			cache.refreshDir(data.getDocLibID());
			break;
		case 2://刷新发布规则缓存
			cache.refreshRule(data.getDocLibID());
			break;
		case 3: //刷新模板缓存
			cache.refreshTemplate(data.getDocLibID());
			
			//若是栏目模板，则可能改变栏目相关，需重刷缓存
			tryRefreshColumnRelating(data);
			break;
		default:
			break;
		}
	}
	
	/** 判断是否需要刷新相关栏目。若栏目模板有改变，则需要*/
	@SuppressWarnings("unused")
	private boolean needRefreshRelated(DocIDMsg data) {
		boolean needRefreshRelated = false;
		
		ColumnReader colReader = (ColumnReader)Context.getBean("columnReader");
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			Column col = colReader.get(data.getDocLibID(), data.getDocID());//缓存中的旧栏目
			if (col != null) {
				//若是已有栏目，则判断是否栏目模板发生了变化
				Document doc = docManager.get(data.getDocLibID(), data.getDocID());
				needRefreshRelated = (doc.getInt("col_template_ID") != col.getTemplate()
						|| doc.getInt("col_templatePad_ID") != col.getTemplatePad());
			} else {
				//若是新增栏目，则判断是否设置了栏目模板
				Document doc = docManager.get(data.getDocLibID(), data.getDocID());
				needRefreshRelated = (doc.getInt("col_template") > 0
						|| doc.getInt("col_templatePad") > 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return needRefreshRelated;
	}

	private void tryRefreshColumnRelating(DocIDMsg data) {
		/*
		 * 似乎WEB系统里用不到这个缓存，只在发布服务中才用到？
		BaseDataCache cache = (BaseDataCache)CacheManager.find(BaseDataCache.class);
		
		Template t = cache.getTemplateByID(data.getDocLibID(), data.getDocID());
		if (t != null) {
			//若是栏目模板，则可能改变栏目相关，需重刷缓存
			if (t.getType() == 0) {
				int colLibID = LibHelper.getLibIDByOtherLib(DocTypes.COLUMN.typeID(), data.getDocLibID());
				cache.refreshColumnRelating(data.getDocLibID(), colLibID);
			}
		}
		*/
	}
}