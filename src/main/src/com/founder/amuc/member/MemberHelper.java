package com.founder.amuc.member;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import net.sf.json.JSONArray;

import com.founder.amuc.commons.Constant;
import com.founder.amuc.commons.InfoHelper;
import com.founder.amuc.tenant.TenantManager;
import com.founder.e5.cat.CatReader;
import com.founder.e5.cat.Category;
import com.founder.e5.commons.DomHelper;
import com.founder.e5.commons.Pair;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.e5.dom.DocTypeField;
import com.founder.e5.flow.Flow;
import com.founder.e5.flow.FlowNode;
import com.founder.e5.flow.FlowReader;
import com.founder.e5.sys.SysConfig;
import com.founder.e5.sys.SysConfigReader;

/**
 * @author leijj
 * @date 2014-8-15 Description:
 */
public class MemberHelper {
	public static void setPotential(Document member) throws E5Exception {
		MemberReader memberReader = new MemberReader();
		int oldPotential = member.getInt("mPotential");
		boolean isFormal = memberReader.isFormal(member);
		// String mobile = member.getString("mMobile");
		// 手机号存在且符合正式会员有效性规则时才是正式会员
		// if(!StringUtils.isBlank(mobile) && isFormal){
		if (isFormal) {
			member.set("mPotential", 0);// 正式会员
		} else if (!(oldPotential == 0))
			member.set("mPotential", 1);// 潜在会员

		setFlowNode(member, member.getInt("mPotential"));// 设置流程节点
	}

	public static void setFlowNode(Document member, int mPotential) throws E5Exception {
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER);
		FlowNode flowNode = null;
		if (mPotential == 0) {// 正式会员
			flowNode = DomHelper.getFlowNode(docLib.getDocTypeID());
		} else if (mPotential == 1) {// 潜在会员
			flowNode = getPotentialNode(docLib.getDocTypeID());
		}
		if (flowNode != null) {
			member.setCurrentFlow(flowNode.getFlowID());
			member.setCurrentNode(flowNode.getID());
			member.setCurrentStatus(flowNode.getWaitingStatus());
		}
	}

	// 读“潜在会员”流程节点
	protected static FlowNode getPotentialNode(int docTypeID) throws E5Exception {
		FlowReader flowReader = (FlowReader) Context.getBean(FlowReader.class);
		Flow[] flows = flowReader.getFlows(docTypeID);
		if (flows == null)
			return null;

		// 设为“潜在会员”流程节点
		FlowNode node = flowReader.getNextFlowNode(flows[0].getFirstFlowNodeID());
		if (node == null) {
			node = flowReader.getFlowNode(flows[0].getFirstFlowNodeID());
		}
		return node;
	}

	/**
	 * 根据系统参数项目和条目获取SysConfig
	 * 
	 * @param project
	 * @param item
	 * @return
	 * @throws E5Exception
	 */
	public static SysConfig getSysConfig(String project, String item) throws E5Exception {
		SysConfigReader sysConfigReader = (SysConfigReader) Context.getBean("SysConfigReader");
		SysConfig[] configs = sysConfigReader.getAppSysConfigs(1);
		if (configs != null && configs.length > 0) {
			for (SysConfig config : configs) {
				if (config.getProject().equals(project) && config.getItem().equals(item)) {
					return config;
				}
			}
		}
		return null;
	}

	/**
	 * 会员查重，根据手机号查找是否有重复的 此sql写法不能适配sqlserver、mysql、oracle 目前改为mysql
	 * 
	 * @param member
	 * @param fields
	 * @return
	 * @throws E5Exception
	 */
	public static long duplicateMemID(Document member, List<DocTypeField> fields) throws E5Exception {

		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER);
		String condition = "mMobile = ? and  SYS_DELETEFLAG=0 ";
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] docs = docManager.find(docLib.getDocLibID(), condition, null);
		long docID = 0;
		if (docs != null && docs.length > 0) {
			docID = docs[0].getLong("SYS_DOCUMENTID");
		}
		return docID;
		/*
		 * StringBuilder querySql = new StringBuilder(
		 * "SELECT SYS_DOCUMENTID FROM ").
		 * append(docLib.getDocLibTable()).append(" WHERE SYS_DELETEFLAG = 0");
		 * 
		 * long docID = 0; Connection conn = null; PreparedStatement state=null;
		 * ResultSet rs = null; try { DBSession
		 * dbSession=Context.getDBSession(docLib.getDsID());
		 * conn=dbSession.getConnection();
		 * 
		 * querySql.append(" and mMobile = ? limit 1");
		 * state=conn.prepareStatement(querySql.toString()); state.setObject(1,
		 * member.get("mMobile")); rs=state.executeQuery(); while (rs.next()) {
		 * docID = rs.getLong("SYS_DOCUMENTID"); } } catch (Exception e) {
		 * e.printStackTrace(); } finally { ResourceMgr.closeQuietly(rs);
		 * ResourceMgr.closeQuietly(state); ResourceMgr.closeQuietly(conn); }
		 * return docID;
		 */
		/*
		 * querySql.append(" ORDER BY SYS_DOCUMENTID DESC"); DocumentManager
		 * docManager = DocumentManagerFactory.getInstance(); Document[] docs =
		 * docManager.find(docLib.getDocLibID(), querySql.toString(), null);
		 * if(docs != null && docs.length > 0) return docs[0].getDocID();
		 * 
		 * return 0;
		 */
	}

	/**
	 * 对会员文档的字段（分类类型）的进行id进行赋值
	 * 
	 * @param member
	 * @param docLib
	 * @param webRoot
	 * @return
	 * @throws E5Exception
	 */
	public static Document dealID(Document member, DocLib docLib, String webRoot) throws E5Exception {

		CatReader catreader = (CatReader) Context.getBean(CatReader.class);// 取出数据来源
		HashMap<String, String> hs = new HashMap<String, String>();
		hs.put("mSource", "会员数据来源");
		hs.put("mEducation", "学历");
		hs.put("mRegion", "地区");
		hs.put("mCardType", "证件类型");

		Set<String> fields = hs.keySet();
		for (String key : fields) {
			if (member.get(key) != null && member.get(key) != "") {// 给数据来源id赋值
				Category[] category = catreader.getCats(hs.get(key));
				for (Category cat : category) {
					if (cat.getCatName().equals(member.get(key))) {
						if (key.equals("mRegion")) {
							member.set(key + "ID", "" + cat.getCatID());
						} else {
							member.set(key + "_ID", cat.getCatID());
						}
					}
				}
			} else {
				if (key.equals("mRegion")) {
					member.set(key + "ID", "" + 0);
				} else {
					member.set(key + "_ID", 0);
				}
			}
		}
		// 处理兴趣爱好,可多选
		if (member.get("mHobby") != null && !"".equals(member.get("mHobby"))) {
			String mHobby = member.getString("mHobby");
			StringBuilder mHobbyID = new StringBuilder();
			Category[] cats = catreader.getCats("兴趣爱好");
			for (Category cat : cats) {
				if (mHobby.contains(cat.getCatName())) {
					mHobbyID.append(cat.getCatID() + ";");
				}
			}
			member.set("mHobbyID", mHobbyID.toString());

		} else {
			member.set("mHobbyID", "" + 0);
		}
		return member;
	}

	public static String findOptionByUrl(DocTypeField field, String text, String webRoot) {
		Pair[] options = json2Options(field.getOptions());

		return _findOption(options, text);
	}

	protected static String _findOption(Pair[] options, String text) {
		if (options != null) {
			for (Pair option : options) {
				if (option.getValue().equals(text) || option.getKey().equals(text))
					return option.getKey();
			}
		}
		return null;
	}

	/**
	 * 把json字符串转换成Pair数组。
	 * 
	 * @param ret
	 *            格式为：[{key:"123",value:"张三"},{key:"124",value:"李四"},...]
	 * @return
	 */
	protected static Pair[] json2Options(String ret) {
		if (StringUtils.isBlank(ret))
			return null;

		return (Pair[]) JSONArray.toArray(JSONArray.fromObject(ret), Pair.class);
	}

	/**
	 * 程序中访问http数据接口
	 */
	protected static String accessUrl(String urlStr, String webRoot) {
		if (StringUtils.isBlank(urlStr))
			return null;

		urlStr = urlStr.replaceAll("&amp;", "&");
		if (urlStr.indexOf("http://") < 0) {
			urlStr = webRoot + urlStr;
		}

		/** 网络的url地址 */
		URL url = null;
		/** 输入流 */
		BufferedReader in = null;
		StringBuffer sb = new StringBuffer();
		String str = null;
		try {
			url = new URL(urlStr);
			in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
			while ((str = in.readLine()) != null) {
				sb.append(str);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(in);
		}
		return sb.toString();
	}

	/**
	 * 根据会员id获取会员文档（所有字段全部取出）
	 * 
	 * @param tenantCode
	 * @param uid
	 * @return
	 * @throws E5Exception
	 */
	public static Document getMember(String tenantCode, long uid) throws E5Exception {

		if (tenantCode == null)
			tenantCode = TenantManager.DEFAULTCODE;

		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER, tenantCode);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		String condition = "SYS_DELETEFLAG = 0 and SYS_DOCUMENTID = ?";
		Document[] docs = docManager.find(docLib.getDocLibID(), condition, new Object[] { uid });
		if (docs != null && docs.length > 0) {
			return docs[0];
		}
		return null;
	}

	/**
	 * 根据会员id获取会员文档（只取出需要的几个字段） 新华的第三方大转盘抽奖游戏接口有使用到
	 * 
	 * @param tenantCode
	 * @param uid
	 * @return
	 * @throws E5Exception
	 */
	public static Document getMember2(String tenantCode, long uid) throws E5Exception {

		if (tenantCode == null)
			tenantCode = TenantManager.DEFAULTCODE;

		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER, tenantCode);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		String condition = "SYS_DELETEFLAG = 0 and SYS_DOCUMENTID = ?";
		String[] column = { "mStatus", "mScore", "mName" };
		Document[] docs = docManager.find(docLib.getDocLibID(), condition, new Object[] { uid }, column);
		if (docs != null && docs.length > 0) {
			return docs[0];
		}
		return null;
	}

	/**
	 * 新华考试系统 根据会员手机号获取会员文档
	 * 
	 * @param tenantCode
	 * @param mobile
	 * @return
	 * @throws E5Exception
	 */
	public static Document getMemberByMobile(String tenantCode, String mobile) throws E5Exception {

		if (tenantCode == null)
			tenantCode = TenantManager.DEFAULTCODE;

		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER, tenantCode);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		String condition = "SYS_DELETEFLAG = 0 and mMobile = ?";
		String[] column = { "mStatus", "mScore", "mName" };
		Document[] docs = docManager.find(docLib.getDocLibID(), condition, new Object[] { mobile }, column);
		if (docs != null && docs.length > 0) {
			return docs[0];
		}
		return null;
	}

	/**
	 * 按条件查询会员
	 *
	 * @param tenantCode
	 * @param condition
	 * @param value
	 * @return
	 * @throws E5Exception
	 */
	public static Document getMemberByCondition(String tenantCode, String condition, String value) throws E5Exception {

		if (tenantCode == null)
			tenantCode = TenantManager.DEFAULTCODE;
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER, tenantCode);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		String sqlCondition = null;
		if (condition.equals("email")) {
			sqlCondition = "SYS_DELETEFLAG = 0 and mEmail = ?";
		}
		if (condition.equals("mobile")) {
			sqlCondition = "SYS_DELETEFLAG = 0 and mMobile = ?";
		}
		if (condition.equals("uid_sso")) {
			sqlCondition = "SYS_DELETEFLAG = 0 and uid_sso = ?";
		}
		String[] column = { "mStatus", "mScore", "mName" };
		Document[] docs = docManager.find(docLib.getDocLibID(), sqlCondition, new Object[] { value }, column);
		if (docs != null && docs.length > 0) {
			return docs[0];
		}
		return null;
	}
}