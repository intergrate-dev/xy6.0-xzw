package com.founder.xy.commons;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.founder.e5.commons.DomHelper;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.dom.DocLib;
import com.founder.e5.dom.DocLibReader;
import com.founder.xy.system.Tenant;

/**
 * 库ID辅助类
 * @author Gong Lijie
 */
public class LibHelper {
	
	/**
	 * 读出文档类型的所有文档库
	 */
	public static DocLib[] getLibs(int docTypeID) throws E5Exception{
		DocLibReader docLibReader = (DocLibReader)Context.getBean(DocLibReader.class);
		return docLibReader.getByTypeID(docTypeID);
	}
	//--------------多租户--------------
	/**
	 * 根据租户代号和文档类型获得对应的文档库
	 */
	public static List<DocLib> getLibs(int docTypeID, String tenantCode){
		List<DocLib> result = new ArrayList<DocLib>();
		try {
			DocLibReader docLibReader = (DocLibReader)Context.getBean(DocLibReader.class);
			
			//读出文档类型的所有文档库
			DocLib[] docLibs = docLibReader.getByTypeID(docTypeID);
			if (docLibs != null) {
				//全局表
				if (docLibs.length == 1 && docLibs[0].getDocLibTable().startsWith("g_")) {
					result.add(docLibs[0]);
					return result;
				}
				if (StringUtils.isBlank(tenantCode))
					tenantCode = Tenant.DEFAULTCODE;
				
				for (DocLib docLib : docLibs) {
					if (docLib.getDocLibTable().startsWith(tenantCode + "_")) {
						result.add(docLib);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * 根据租户代号和文档类型获得对应的文档库
	 */
	public static DocLib getLib(int docTypeID, String tenantCode){
		List<DocLib> result = getLibs(docTypeID, tenantCode);
		if (result == null || result.size() == 0)
			return null;
		else
			return result.get(0);
	}
	
	/**
	 * 根据租户代号和文档类型获得对应的文档库ID
	 */
	public static int getLibID(int docTypeID, String tenantCode) {
		DocLib docLib = getLib(docTypeID, tenantCode);
		return docLib.getDocLibID();
	}
	
	/**
	 * 根据租户代号和文档类型获得对应的文档库表名
	 */
	public static String getLibTable(int docTypeID, String tenantCode) throws E5Exception{
		DocLib docLib = getLib(docTypeID, tenantCode);
		return docLib.getDocLibTable();
	}
	
	/**
	 * 根据租户代号和文档类型获得对应的文档库表名
	 */
	public static String getLibTable(int docLibID) throws E5Exception{
		DocLib docLib = getLibByID(docLibID);
		return docLib.getDocLibTable();
	}
	
	/**
	 * 给出一个文档库（不能是归档库），找到同租户下的其它文档库。
	 * 
	 * 原理是：
	 * 1）找到文档库的tableName，如xy_article，截取出前缀xy，作为租户代号。
	 * 2）按租户代号查出指定文档类型的库ID
	 */
	public static int getLibIDByOtherLib(int docTypeID, int otherLibID) {
		try {
			//找到租户代号
			String tenantCode = getTenantCodeByLib(otherLibID);
			
			return getLibID(docTypeID, tenantCode);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * 找到一个文档库所在的租户代号
	 * 
	 * 原理是：找到文档库的tableName，如xy_article，截取出前缀xy，作为租户代号。
	 */
	public static String getTenantCodeByLib(int docLibID) throws E5Exception {
		DocLibReader docLibReader = (DocLibReader)Context.getBean(DocLibReader.class);
		DocLib otherLib = docLibReader.get(docLibID);
		//找到文档库表名中的租户代号
		String tenantCode = otherLib.getDocLibTable().split("_")[0];
		return tenantCode;
	}
	
	/** 取栏目库ID，以session中的租户代号为区分 */
	public static int getColumnLibID(HttpServletRequest request) throws E5Exception {
		return getLibID(DocTypes.COLUMN.typeID(), InfoHelper.getTenantCode(request));
	}
	
	//--------------end.多租户--------------
	
	/** 取栏目库ID，以session中的租户代号为区分 */
	public static int getColumnLibID() {
		return DomHelper.getDocLibID(DocTypes.COLUMN.typeID());
	}
	// 先这样做，以后多租户下要修改
	public static int getMobileosID() {
		return DomHelper.getDocLibID(DocTypes.MOBILEOS.typeID());
	}
	
	// 先这样做，以后多租户下要修改? 
	public static int getPraiseID() {
		return DomHelper.getDocLibID(DocTypes.PRAISE.typeID());
	}
	
	// 先这样做，以后多租户下要修改?
	public static int getNisattachment() {
		return DomHelper.getDocLibID(DocTypes.NISATTACHMENT.typeID());
	}
	
	public static int getNisQaID() {
		return DomHelper.getDocLibID(DocTypes.QA.typeID());
	}

	public static int getNisActivityID() {
		return DomHelper.getDocLibID(DocTypes.ACTIVITY.typeID());
	}
	// 先这样做，以后多租户下要修改? 
	public static int getTipoff() {
		return DomHelper.getDocLibID(DocTypes.TIPOFF.typeID());
	}
	
	// 先这样做，以后多租户下要修改? 
	public static int getDiscuss() {
		return DomHelper.getDocLibID(DocTypes.DISCUSS.typeID());
	}
	
	// 先这样做，以后多租户下要修改? 
	public static int getNisShutup() {
		return DomHelper.getDocLibID(DocTypes.SHUTUP.typeID());
	}
	
	// 先这样做，以后多租户下要修改? 
	public static int getVote() {
		return DomHelper.getDocLibID(DocTypes.VOTE.typeID());
	}
	
	// 先这样做，以后多租户下要修改? 
	public static int getVoteResult() {
		return DomHelper.getDocLibID(DocTypes.VOTERESULT.typeID());
	}
	
	// 先这样做，以后多租户下要修改?
	public static int getVoteOption() {
		return DomHelper.getDocLibID(DocTypes.VOTEOPTION.typeID());
	}
	
	// 先这样做，以后多租户下要修改?
	public static int getSubscribe() {
		return DomHelper.getDocLibID(DocTypes.SUBSCRIBE.typeID());
	}
	
	// 先这样做，以后多租户下要修改? 
	public static int getTopic() {
		return DomHelper.getDocLibID(DocTypes.TOPIC.typeID());
	}
	// 先这样做，以后多租户下要修改? 
	public static int getLive() {
		return DomHelper.getDocLibID(DocTypes.LIVE.typeID());
	}

	public static int getLiveItem() {
		return DomHelper.getDocLibID(DocTypes.LIVEITEM.typeID());
	}
	
	// 先这样做，以后多租户下要修改?
	public static int getFeedback() {
		return DomHelper.getDocLibID(DocTypes.FEEDBACK.typeID());
	}
	
	// 先这样做，以后多租户下要修改?
	public static int getApplogin() {
		return DomHelper.getDocLibID(DocTypes.APPLOGIN.typeID());
	}
	
	/** 取稿件库ID */
	public static int getArticleLibID() {
		return DomHelper.getDocLibID(DocTypes.ARTICLE.typeID());
	}
	
	/** 取移动端稿件库ID */
	public static int getArticleAppLibID() {
		return getArticleAppLib().getDocLibID();
	}
	
	/** 取原稿库ID */
	public static int getOriginalLibID() {
		return DomHelper.getDocLibID(DocTypes.ORIGINAL.typeID());
	}

	/** 取域名目录库ID */
	public static int getDomainDirLibID() {
		return DomHelper.getDocLibID(DocTypes.DOMAINDIR.typeID());
	}
	
	/** 取站点发布规则库ID */
	public static int getSiteRuleLibID() {
		return DomHelper.getDocLibID(DocTypes.SITERULE.typeID());
	}

	/** 取站点库ID */
	public static int getSiteLibID() {
		return DomHelper.getDocLibID(DocTypes.SITE.typeID());
	}
	
	/** 取用户扩展属性表的库ID */
	public static int getUserExtLibID() {
		return DomHelper.getDocLibID(DocTypes.USEREXT.typeID());
	}
	
	/** 取用户扩展属性表的库ID */
	public static int getUserExtLibID(HttpServletRequest request) {
		return getLibID(DocTypes.USEREXT.typeID(), InfoHelper.getTenantCode(request));
	}
	
	/** 取用户关联表的库ID */
	public static int getUserRelLibID() {
		return DomHelper.getDocLibID(DocTypes.USERREL.typeID());
	}
	/** 取用户关联表的库ID */
	public static int getUserRelLibID(HttpServletRequest request) {
		return getLibID(DocTypes.USERREL.typeID(), InfoHelper.getTenantCode(request));
	}
	
	/** 取来源表的库ID */
	public static int getSourceLibID() {
		return DomHelper.getDocLibID(DocTypes.SOURCE.typeID());
	}
	
	/** 取来页面区域表的库ID */
	public static int getBlockLibID() {
		return DomHelper.getDocLibID(DocTypes.BLOCK.typeID());
	}
	
	/** 取来页面区域内容的库ID */
	public static int getBlockArticleLibID() {
		return DomHelper.getDocLibID(DocTypes.BLOCKARTICLE.typeID());
	}
	
	/** 取公共资源表的库ID */
	public static int getResourceLibID() {
		return DomHelper.getDocLibID(DocTypes.RESOURCE.typeID());
	}
	
	/** 取模版表的库ID */
	public static int getTemplateLibID() {
		return DomHelper.getDocLibID(DocTypes.TEMPLATE.typeID());
	}
	
	/**
	 * 扩展字段定义库ID
	 * @return
	 */
	public static int getExtFieldLibID() {
		return DomHelper.getDocLibID(DocTypes.EXTFIELD.typeID());
	}
	
	/**
	 * 稿件扩展字段库ID
	 * @return
	 */
	public static int getArticleExtLibID() {
		return DomHelper.getDocLibID(DocTypes.ARTICLEEXT.typeID());
	}
	
	/**
	 * 图片库ID
	 * @return
	 */
	public static int getPicLibID() {
		return DomHelper.getDocLibID(DocTypes.PHOTO.typeID());
	}
	
	/**
	 * 附件库ID
	 * @return
	 */
	public static int getAttaLibID() {
		return DomHelper.getDocLibID(DocTypes.ATTACHMENT.typeID());
	}
	public static int getAttaLibID(HttpServletRequest request) {
		return getLibID(DocTypes.ATTACHMENT.typeID(), InfoHelper.getTenantCode(request));
	}
	
	/**
	 * 视频库ID
	 * @return
	 */
	public static int getVideoLibID() {
		return DomHelper.getDocLibID(DocTypes.VIDEO.typeID());
	}
	
	/**
	 * 视频转码任务库ID
	 * @return
	 */
	public static int getVideoTaskLibID() {
		return DomHelper.getDocLibID(DocTypes.VIDEOTASK.typeID());
	}
	
	/**
	 * 稿件内容挂件库ID
	 * @return
	 */
	public static int getWidgetLibID() {
		return DomHelper.getDocLibID(DocTypes.WIDGET.typeID());
	}
	
	/**
	 * 模板组件实例表的库ID
	 * @return
	 */
	public static int getComponentObjLibID() {
		return DomHelper.getDocLibID(DocTypes.COMPONENTOBJ.typeID());
	}
	
	/**
	 * 相关稿件
	 * @return
	 */
	public static int getArticleRelLibID() {
		return DomHelper.getDocLibID(DocTypes.ARTICLEREL.typeID());
	}


	/**
	 * 互动转码任务库ID
	 * @return
	 */
	public static int getNisTaskLibID() {
		return DomHelper.getDocLibID(DocTypes.NISTASK.typeID());
	}

	/**
	 * 按文档库名读ID
	 */
	public static int getLibIDByName(String libName) {
		DocLibReader libReader = (DocLibReader)Context.getBean(DocLibReader.class);
		try {
			return libReader.getByName(libName).getDocLibID();
		} catch (E5Exception e) {
			System.out.println("找不到文档库：" + libName + "。错误：" + e.getLocalizedMessage());
			return 0;
		}
	}
	
	/**
	 * 根据文档类型编码获得对应的文档库
	 */
	public static DocLib getLib(int docTypeID) {
		DocLibReader docLibReader = (DocLibReader)Context.getBean(DocLibReader.class);
		try {
			DocLib[] docLibs =  docLibReader.getByTypeID(docTypeID);
			if (docLibs != null) {
				return docLibs[0];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 根据文档类型编码获得对应的文档库
	 */
	public static DocLib getLibByID(int docLibID) {
		DocLibReader docLibReader = (DocLibReader)Context.getBean(DocLibReader.class);
		try {
			return docLibReader.get(docLibID);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 得到默认的App稿件库
	 */
	public static DocLib getArticleAppLib() {
		try {
			DocLib[] articleLibs = getLibs(DocTypes.ARTICLE.typeID());
			if (articleLibs != null) {
				return articleLibs[1];
			}
		} catch (E5Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
