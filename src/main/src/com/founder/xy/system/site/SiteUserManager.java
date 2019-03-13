package com.founder.xy.system.site;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.founder.e5.commons.DomHelper;
import com.founder.e5.sys.org.User;
import com.founder.e5.sys.org.UserReader;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.e5.dom.DocLibReader;
import com.founder.e5.sys.org.UserManager;
import com.founder.e5.workspace.ProcHelper;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.jms.PublishTrigger;
import com.founder.xy.jms.data.DocIDMsg;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * 站点用户管理器，负责站点用户的增删改等功能
 */
@Service
public class SiteUserManager {
	@Autowired
	UserManager userManager;
	
	/**
	 *  用户关联信息保存
	 */
	public void saveRelated(int userRelLibID, int userID, int siteID, int type, String ids) throws Exception {

		String table = LibHelper.getLibByID(userRelLibID).getDocLibTable();
		InfoHelper.executeUpdate(userRelLibID,
				"delete from " + table + " where SYS_DOCUMENTID=? and ur_siteID=? and ur_type=?",
				new Object[]{userID, siteID, type});
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		ids = InfoHelper.removeItemsFromIds(ids.replaceAll(" ", ""), "," , new String[]{""});
		int[] idArr = StringUtils.getIntArray(ids);
		if (idArr != null) {
			for (int id : idArr) {
				Document doc = docManager.newDocument(userRelLibID, userID);
				doc.set("ur_siteID", siteID);
				doc.set("ur_type", type);
				doc.set("ur_id", id);
				docManager.save(doc);
			}
		}
		cacheUserRelTrigger(userRelLibID);
	}
	
	/**
	 *  增加一个根栏目时，自动给用户加栏目可操作和可管理的权限
	 */
	public void addColumnRelated(int userRelLibID, int userID, int siteID, long colID, int channelType) throws E5Exception {

		//若是移动版，则“可操作栏目”类型为4
		int type = channelType == 0 ? 0 : 4;
		addRelated(userRelLibID, userID, siteID, colID, type);
		
		//若是移动版，则“可管理栏目”类型为5
		type = channelType == 0 ? 1 : 5;
		addRelated(userRelLibID, userID, siteID, colID, type);
	}
	
	/**
	 *  增加一个用户关联
	 */
	public void addRelated(int userRelLibID, int userID, int siteID, long relID, int type) throws E5Exception {

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = docManager.newDocument(userRelLibID, userID);
		doc.set("ur_siteID", siteID);
		doc.set("ur_type", type);
		doc.set("ur_id", relID);
		docManager.save(doc);
	}
	
	/**
	 *  用户关联信息读取。把多条关联ID拼成字符串，比较适合前端使用。
	 * @param userID
	 * @param siteID
	 * @param type 关联类型：0-可操作的栏目；1-可管理的栏目；2-视频分类；3-页面区块组；10-微信账号(公众号)
	 */
	public String getRelated(int userRelLibID, int userID, int siteID, int type) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] rels = docManager.find(userRelLibID, "SYS_DOCUMENTID=? and ur_type=? and ur_siteID=?",
				new Object[]{userID, type, siteID});
		StringBuilder result = new StringBuilder();
		for (int i = 0; (rels != null) && i < rels.length; i++) {
			if (i > 0) result.append(",");
			result.append(rels[i].getInt("ur_id"));
		}
		return result.toString();
	}
	
	/** 根据userID，取出用户可管理的站点的列表 */
	public List<Site> getUserSites(int userLibID, int userID) {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		List<Site> sites = new ArrayList<Site>();
		try {
			Document[] users = docManager.find(userLibID, "SYS_DOCUMENTID=?", new Object[]{userID});
			if (users.length > 0){
				int siteLibID = LibHelper.getLibIDByOtherLib(DocTypes.SITE.typeID(), userLibID);
				
				sites = getSiteList(siteLibID, users[0]);
			}
		} catch (E5Exception e) {
			e.printStackTrace();
		}
		return sites;
	}
	
	//查已有用户数
	public int getUserCounts(int userLibID) throws Exception {
		DBSession conn = null;
		IResultSet rs = null;
		try {
			DocLibReader docLibReader = (DocLibReader)Context.getBean(DocLibReader.class);
			DocLib userLib = docLibReader.get(userLibID);
			
			String sql = "select count(*) from " + userLib.getDocLibTable();
			
			conn = Context.getDBSession(userLib.getDsID());
			rs = conn.executeQuery(sql, null);
			if (rs.next())
				return rs.getInt(1);
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}
		return 0;
	}
	
	/** 取出所有用户可管理的站点的列表，封装成Map。key为userID */
	public Map<Integer, int[]> getUserSites(int userLibID){
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Map<Integer, int[]> map = new HashMap<Integer, int[]>();
		try {
			//未被删除的
			Document[] users = docManager.find(userLibID, 
					"SYS_DELETEFLAG=0", null);
			for (int i = 0; i < users.length; i++) {
				Document user = users[i];
				
				String siteIDs = user.getString("u_siteIDs");
				if (!StringUtils.isBlank(siteIDs)) {
					siteIDs = siteIDs.substring(1, siteIDs.length()-1);
				}
				int[] sites = StringUtils.getIntArray(siteIDs);
				
				map.put((int) user.getDocID(), sites);
			}
		} catch (E5Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	
	/**
	 * 取用户可管理的站点
	 */
	private List<Site> getSiteList(int siteLibID, Document user){
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		List<Site> sites = new ArrayList<Site>();
		try {
			String siteIDs = user.getString("u_siteIDs");
			if (StringUtils.isBlank(siteIDs))
				return null;
			
			siteIDs = siteIDs.substring(1, siteIDs.length()-1);
			
			long[] siteIDLong = StringUtils.getLongArray(siteIDs);
			
			Document[] siteArr = docManager.get(siteLibID, siteIDLong);
			if (siteArr != null) {
				for (int j = 0; j < siteArr.length; j++) {
					if (siteArr[j] == null) continue;
					
					Site s = new Site(siteArr[j].getString("site_name"), (int)siteArr[j].getDocID());
					sites.add(s);
				}
			}
		} catch (E5Exception e) {
			e.printStackTrace();
		}
		return sites;
	}
	

	/** 取出所有用户 site和role的json串，封装成Map。key为userID */
	public Map<Integer, String> getSiteAndRoles(int userLibID){
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Map<Integer, String> map = new HashMap<Integer, String>();
		try {
			//未被删除的
			Document[] users = docManager.find(userLibID, "SYS_DELETEFLAG=?", new Object[]{0});
			for (int i = 0; i < users.length; i++) {
				Document user = users[i];
				map.put((int) user.getDocID(), user.getString("u_siteRoleIDs"));
			}
		} catch (E5Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	 *  取出有权用户信息
	 */
	public List<ColumnUser> getColumnUserInfoList(int userRelLibID, String colID, String siteID,
			String roleType) throws Exception {

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] userRel = docManager.find(userRelLibID, "ur_id=? AND ur_type=? AND ur_siteID=?",
				new Object[] {colID, roleType, siteID});
		
		String sysId = "";
		int length = userRel.length;
		if(0 == length){
			return new ArrayList<ColumnUser>();
		}
		for (int i = 0; i < length; i++) {
			sysId += "," + userRel[i].getInt("SYS_DOCUMENTID");
		}
		
		sysId = "(" + sysId.substring(1) + ")";
		
		int userLibID = LibHelper.getLibIDByOtherLib(DocTypes.USEREXT.typeID(), userRelLibID);
		Document[] urs = docManager.find(userLibID, "SYS_DOCUMENTID IN " + sysId + " ORDER BY u_code", null);
		
		length = urs.length;
		ColumnUser bean = null;
		List<ColumnUser> rtnList = new ArrayList<ColumnUser>();
		
		for (int i = 0; i < length; i++) {
			bean = new ColumnUser();
			
			bean.setSysId(urs[i].getString("SYS_DOCUMENTID")); // 文档ID
			bean.setUserCode(urs[i].getString("u_code")); // 用户名
			bean.setUserName(urs[i].getString("u_name")); // 真实姓名
			bean.setPenName(urs[i].getString("u_penName")); // 	笔名
			bean.setOrg(urs[i].getString("u_org")); // 	所在部门（机构）
			
			rtnList.add(bean);
		}
		return rtnList;
	}
	
	/**
	 *  删除有权限用户
	 */
	public void delColumnUser(int userRelLibID, String sysId, String colID, String siteID,
			String roleType) throws Exception {

		String table = LibHelper.getLibByID(userRelLibID).getDocLibTable();
		
		String sql = "DELETE FROM " + table + " where SYS_DOCUMENTID IN (" + sysId 
				+ ") AND ur_id=? AND ur_siteID=? AND ur_type=?";

		InfoHelper.executeUpdate(userRelLibID, sql.toString(), new Object[]{colID, siteID, roleType});
		
		cacheUserRelTrigger(userRelLibID);
	}
	
	/**
	 *  取出无权限用户信息
	 */
	public List<ColumnUser> getNotColumnUserInfoList(int userRelLibID, String colID,
			String siteID, String roleType) throws E5Exception {

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] userRel = docManager.find(userRelLibID, "ur_id=? AND ur_type=? AND ur_siteID=?",
				new Object[] {colID, roleType, siteID});
		
		String sysId = "";
		int length = userRel.length;
		
		if(0 == length){
			return new ArrayList<ColumnUser>();
		}
		
		for (int i = 0; i < length; i++) {
			sysId += "," + userRel[i].getInt("SYS_DOCUMENTID");
		}
		
		sysId = "(" + sysId.substring(1) + ")";
		
		int userLibID = LibHelper.getLibIDByOtherLib(DocTypes.USEREXT.typeID(), userRelLibID);
		Document[] urs = docManager.find(userLibID, "SYS_DOCUMENTID NOT IN "
					+ sysId + " ORDER BY u_code", null);
		
		length = urs.length;
		ColumnUser bean = null;
		List<ColumnUser> rtnList = new ArrayList<ColumnUser>();
		
		for (int i = 0; i < length; i++) {
			bean = new ColumnUser();
			
			bean.setSysId(urs[i].getString("SYS_DOCUMENTID")); // 文档ID
			bean.setUserCode(urs[i].getString("u_code")); // 用户名
			bean.setUserName(urs[i].getString("u_name")); // 真实姓名
			bean.setPenName(urs[i].getString("u_penName")); // 	笔名
			bean.setOrg(urs[i].getString("u_org")); // 	所在部门（机构）
			
			rtnList.add(bean);
		}
		return rtnList;
	}
	
	/**
	 *  新增有权限用户
	 */
	public void addColumnUser(int userRelLibID, String sysId, String colID, String siteID,
			String roleType) throws Exception {
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		
		DBSession conn = null;
		Document doc = null;
		try {
	    	// 多个用户ID
			String[] sysIdArr = sysId.split(",");
			int length = sysIdArr.length;
			
	    	conn = Context.getDBSession();
	    	conn.beginTransaction();
			// 多个用户循环插入DB
			for (int i = 0; i < length; i++) {
				doc = docManager.newDocument(userRelLibID, Long.parseLong(sysIdArr[i]));
				ProcHelper.initDoc(doc);
				
				doc.set("ur_id", colID);
				doc.set("ur_siteID", siteID);
				doc.set("ur_type", roleType);
				
				docManager.save(doc, conn);
			}
			conn.commitTransaction();
			
			cacheUserRelTrigger(userRelLibID);
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			throw new E5Exception(e);
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
	}
	/**
	 * 发出用户缓存刷新消息，刷新用户相关数据
	 * @param userRelLibID  用户关联信息
	 */
	public void cacheUserRelTrigger(int userRelLibID) {
		int userLibID = LibHelper.getLibIDByOtherLib(DocTypes.USEREXT.typeID(), userRelLibID);
		PublishTrigger.otherData(userLibID, 0, DocIDMsg.TYPE_USERREL);
	}
	
	/**
	 *  按组织关联信息
	 */
	public Map<String, long[]> getRels(int userRelLibID) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] users = docManager.find(userRelLibID, "1=1 order by ur_id", null);
		
		Map<String, List<Long>> map = new HashMap<String, List<Long>>();
		int length = users.length;
		for (int i = 0; i < length; i++) {
			Document user = users[i];
			String key = user.getDocID() + "_" + user.getInt("ur_siteID") + "_" + user.getInt("ur_type");
			long urId = user.getLong("ur_id");
			
			if(map.containsKey(key)){
				if(!map.get(key).contains(urId)) { // 不添加重复的数据进入list
					map.get(key).add(urId);
				}
			}else{
				List<Long> list = new ArrayList<Long>();
				list.add(urId);
				map.put(key, list);
			}
		}
		
		Map<String, long[]> rtnMap = new HashMap<String, long[]>();
		for (String key : map.keySet()) {
			rtnMap.put(key, InfoHelper.getLongArray(map.get(key)));
		}
		
		return rtnMap;
	}
	
	/**
	 * 通过用户ID得到用户的部门ID
	 */
	public Document getUser(int userLibID, int userID) {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			return docManager.get(userLibID, userID);
		} catch (E5Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 按用户名查用户，用于写稿中查记者
	 */
	public Document getUserByName(int userLibID, String userName, int siteID) {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			Document[] users = docManager.find(userLibID, "u_name=? and u_siteID=?", new Object[]{userName, siteID});
			if (users != null && users.length > 0)
				return users[0];
		} catch (E5Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 取站点下的所有用户，用于通讯员的对应用户
	 */
	public Document[] getUsers(int userLibID, int siteID) {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			return docManager.find(userLibID, "u_siteID=?", new Object[]{siteID});
		} catch (E5Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 取站点下的所有单位，用于通讯员的对应用户
	 */
	public Document[] getCorporations(int userLibID, int siteID, String q) {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			return docManager.find(userLibID, "corp_name like ? and corp_siteID=?",
					new Object[]{"%" + q + "%", siteID});
		} catch (E5Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 根据单位名称取站点下单位，用于通讯员校验单位名称是否存在
	 */
	public Document[] getCorporation(int userLibID, int siteID, String corpName) {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			return docManager.find(userLibID, "corp_name = ? and corp_siteID=?",
					new Object[]{corpName, siteID});
		} catch (E5Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Boolean permissionCopy(HttpServletRequest request, @RequestParam long srcID, @RequestParam String destIDs) throws E5Exception {
		int userRelLibID = LibHelper.getUserRelLibID(request);
		//取除了收藏之外的所有权限
		String selectSQL = "SELECT ur_siteID,ur_id,ur_type FROM xy_userrel WHERE ur_type NOT IN (6,7) AND SYS_DOCUMENTID = ?";
		//插入新权限
		String insertSQL = "INSERT INTO  xy_userrel (SYS_DOCLIBID,SYS_FOLDERID,SYS_DELETEFLAG,SYS_DOCUMENTID,ur_siteID,ur_id,ur_type) VALUES (" +
				userRelLibID+" , "+ DomHelper.getFVIDByDocLibID(userRelLibID)+
				",0,?,?,?,?)";
		long[] dsetIDArr = StringUtils.getLongArray(destIDs);
		//转换下 防止SQL注入
		String dsetIDStr = StringUtils.join(dsetIDArr);
		//删除目标用户的除收藏外的所有权限
		String delSQL = "DELETE FROM xy_userrel WHERE SYS_DOCUMENTID IN (" +dsetIDStr +") AND ur_type NOT IN (6,7)";

		Boolean flag = true;
		DBSession conn = null;
		IResultSet rs = null;
		PreparedStatement ps;
		try {
			conn = Context.getDBSession();
			ps = conn.getConnection().prepareStatement(insertSQL);
			//取除了收藏之外的所有权限
			rs = conn.executeQuery(selectSQL, new Object[]{srcID});
			while (rs.next()) {
				//生成batSQL
				for(long detsID :dsetIDArr) {
					ps.setLong(1,detsID);
					ps.setLong(2,rs.getLong(1));
					ps.setInt(3,rs.getInt(2));
					ps.setInt(4,rs.getInt(3));
					ps.addBatch();
				}
			}
			conn.beginTransaction();
			//删除目标用户的除收藏外的所有权限
			conn.executeUpdate(delSQL,null);
			// 执行batsql
			ps.executeBatch();
			conn.commitTransaction();
			cacheUserRelTrigger(userRelLibID);
		} catch (SQLException e) {
			ResourceMgr.rollbackQuietly(conn);
			e.printStackTrace();
			flag = false;
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}
		return flag;
	}


	/**
	 * 判断当前用户是否管理员。
	 * @param userID
	 * @return
	 */
	public boolean isAdmin(int userID) {
		UserReader userReader = (UserReader)Context.getBean(UserReader.class);
		try {
			User user = userReader.getUserByID(userID);
			return "1".equals(user.getProperty2());
		} catch (E5Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 取出所有用户的用户ID,userCode,userName
	 * @param userLibID 用户LibID
	 * @param siteID_siteName
	 * @param roleID_roleName
	 * @param fileName
	 * @param filePath
	 * @return 用户ID,userCode,userName JSONArray格式
	 */
	public void getAllUser(int userLibID, HashMap<Long, String> siteID_siteName, HashMap<Integer, String> roleID_roleName, String fileName, String filePath) {

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		String[] FILE_HEADER = {"id","userCode","userName","siteID","siteName","roles"};
		File file = new File(filePath,fileName);
		//初始化csvformat
		CSVFormat formator = CSVFormat.DEFAULT.withRecordSeparator("\n");
		CSVPrinter printer = null;
		try {
			FileOutputStream fos = new FileOutputStream(file);
			// 写BOM
			fos.write(new byte[] { (byte)0xEF, (byte)0xBB, (byte)0xBF });
			// 创建字节流输出对象
			OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
			// Apache Commons CSV打印对象
			 printer = new CSVPrinter(osw,formator);
			//写入列头数据
			 printer.printRecord((Object[]) FILE_HEADER);
			//未被删除的
			Document[] users = docManager.find(userLibID, "SYS_DELETEFLAG=?", new Object[]{0});
			for (Document user : users) {
				List<String> records = new ArrayList<>();
				records.add(user.getDocID()+"");
				records.add(user.getString("u_code"));
				records.add( user.getString("u_name"));
				String userRoleStr = user.getString("u_siteRoleIDs");
				if(!StringUtils.isBlank(userRoleStr)){
					JSONArray allRoles = JSONArray.fromObject(userRoleStr);
					for (Object oneSiteRole1: allRoles) {
						JSONObject oneSiteRole = JSONObject.fromObject(oneSiteRole1);
						JSONArray roles = oneSiteRole.getJSONArray("v");
						records.add(oneSiteRole.optString("k"));
						records.add(siteID_siteName.get(oneSiteRole.optLong("k")));
						StringBuilder roleNames= new StringBuilder();
						for (Object roleID1:roles) {
							int roleID = Integer.parseInt(((String) roleID1).replace("\"",""));
							roleNames.append(roleID_roleName.get(roleID)).append(",");
						}
						if(roleNames.toString().endsWith(","))
							roleNames = new StringBuilder(roleNames.substring(0, roleNames.length() - 1));
						records.add( roleNames.toString());
						printer.printRecord(records);
						records.remove(5);
						records.remove(4);
						records.remove(3);
					}
				}
			}
		} catch (E5Exception | IOException e) {
			e.printStackTrace();
		}finally {
			try {
				assert printer != null;
				printer.flush();
				printer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		
	}
}
