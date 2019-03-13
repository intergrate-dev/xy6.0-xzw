package com.founder.xy.statistics.dao;

import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.sys.org.Org;
import com.founder.e5.sys.org.OrgManager;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ethan on 2017/2/16.
 */
@Repository
public class UtilDAO {

    public List<Map<String, Object>> getAllDepartmentInfo(int orgID) throws E5Exception {
		OrgManager orgManger = (OrgManager) Context.getBean(OrgManager.class);
		Org[] orgs = orgID == 0 ? null : orgManger.getChildOrgs(orgID);

        List<Map<String, Object>> returnList = new ArrayList<>();
        try {
        	if (orgs != null) {
    			for (Org org : orgs) {
                    Map<String, Object> rowData = new HashMap<>();
                    rowData.put("departmentID", org.getOrgID());
                    rowData.put("departmentName", org.getName());
                    rowData.put("parentID", org.getParentID());
                    returnList.add(rowData);
    			}
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return returnList;
    }

    public List<Map<String, Object>> getUserInfoByDepartmentID(String departmentID, String siteID) throws E5Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("select SYS_DOCUMENTID as user_id, u_name as user_name from xy_userext");
        sql.append(" where u_orgID = ? order by SYS_DOCUMENTID desc");
//        sql.append(" order by u_siteID=? desc,u_siteID,SYS_DOCUMENTID desc");
        //sql.append(" and u_siteID = ?");
        DBSession conn = null;
        IResultSet resultSet = null;
        List<Map<String, Object>> returnList = new ArrayList<>();
        try {
            conn = Context.getDBSession();
            resultSet = conn.executeQuery(sql.toString(), new Object[]{departmentID});
            while (resultSet.next()) {
                Map<String, Object> rowData = new HashMap<>();
                rowData.put("userID", resultSet.getString("user_id"));
                rowData.put("userName", resultSet.getString("user_name"));
                returnList.add(rowData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(resultSet);
            ResourceMgr.closeQuietly(conn);
        }
        return returnList;
    }

    public List<Map<String, Object>> getBatmanInfo(String siteID) throws E5Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("select SYS_DOCUMENTID as batman_id, bm_name as batman_name from xy_batman");
        sql.append(" where bm_siteID = ?");
        DBSession conn = null;
        IResultSet resultSet = null;
        List<Map<String, Object>> returnList = new ArrayList<>();
        try {
            conn = Context.getDBSession();
            resultSet = conn.executeQuery(sql.toString(), new Object[]{siteID});
            while (resultSet.next()) {
                Map<String, Object> rowData = new HashMap<>();
                rowData.put("batmanID", resultSet.getString("batman_id"));
                rowData.put("batmanName", resultSet.getString("batman_name"));
                returnList.add(rowData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(resultSet);
            ResourceMgr.closeQuietly(conn);
        }
        return returnList;
    }

    
	public List<Map<String, Object>> getUserIDByUserCode(String userCode) {
		StringBuilder sql = new StringBuilder();
        sql.append("select SYS_DOCUMENTID as user_id,u_name as user_name,u_orgID as department_id,u_org as department_name from xy_userext");
        sql.append(" where u_code = ?");
        DBSession conn = null;
        IResultSet resultSet = null;
        List<Map<String, Object>> returnList = new ArrayList<>();
        try {
            conn = Context.getDBSession();
            resultSet = conn.executeQuery(sql.toString(), new Object[]{userCode});
            while (resultSet.next()) {
                Map<String, Object> rowData = new HashMap<>();
                rowData.put("userID", resultSet.getString("user_id"));
                rowData.put("userName", resultSet.getString("user_name"));
                rowData.put("departmentID", resultSet.getString("department_id"));
                rowData.put("departmentName", resultSet.getString("department_name"));
                returnList.add(rowData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(resultSet);
            ResourceMgr.closeQuietly(conn);
        }
        return returnList;
	}

	public Map<String, String> getCurrentUserId(String userCode,
			String siteID) {
		StringBuilder sql = new StringBuilder();
        sql.append("select SYS_DOCUMENTID as user_id from xy_userext");
        sql.append(" where u_code = ? ");
//        sql.append(" where u_code = ? and u_siteID = ?");
        DBSession conn = null;
        IResultSet resultSet = null;
        Map<String, String> userMap = new HashMap<>();
        try {
            conn = Context.getDBSession();
            resultSet = conn.executeQuery(sql.toString(), new Object[]{userCode});
            while (resultSet.next()) {
            	userMap.put("userID", resultSet.getString("user_id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(resultSet);
            ResourceMgr.closeQuietly(conn);
        }
        return userMap;
	}

	
	
}
