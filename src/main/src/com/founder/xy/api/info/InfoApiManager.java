package com.founder.xy.api.info;

import bsh.StringUtil;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.doc.Document;
import com.founder.e5.sys.org.Org;
import com.founder.e5.sys.org.OrgManager;
import com.founder.xy.column.Column;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.ExternalSystemAuth;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;
import com.founder.xy.system.Tenant;
import com.founder.xy.system.site.Site;
import com.founder.xy.system.site.SiteManager;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by yu.feng on 2017/9/4.
 */
@Service
public class InfoApiManager {

    @Autowired
    private SiteManager siteManager;

    public String getDep(int eid, long time, String sign, String data) throws E5Exception {
        //打印参数
        System.out.println("method---->getDep");
        System.out.println("eid---->"+eid);
        System.out.println("time---->"+time);
        System.out.println("sign---->"+sign);
        System.out.println("data---->"+data);
        //进行身份和安全认证
        String authResult = ExternalSystemAuth.extSystemAuth(eid, time, sign, data);
        if(authResult == null){
            //身份和安全认证成功，进行取机构业务
            // 从取根部门开始取部门树
            JSONArray result = getSubOrgs(1);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("result","success");
            jsonObject.put("dep",result);
            return jsonObject.toString();
        } else {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("result","error");
            jsonObject.put("dep",authResult);
            return jsonObject.toString();
        }

    }

    private JSONArray getSubOrgs(int orgID) throws E5Exception {
        JSONArray result = new JSONArray();

        OrgManager orgManger = (OrgManager) Context.getBean(OrgManager.class);
        Org[] orgs = orgID == 0 ? null : orgManger.getNextChildOrgs(orgID);
        if (orgs != null) {
            for (Org org : orgs) {
                JSONObject json = new JSONObject();
                json.put("depID", org.getOrgID());
                json.put("depName", org.getName());
//                json.put("title", org.getName() + " [" + org.getOrgID() + "]");
                json.put("depCode", org.getCode());
//                json.put("icon", "../../images/org.gif");

                if (orgManger.getNextChildOrgs(org.getOrgID()) != null){
                    //有子节点
                    JSONArray jsonArray = getSubOrgs(org.getOrgID());
                    json.put("dep", jsonArray);
                }
                result.add(json);
            }
        }
        return result;
    }

    public String getSites(int eid, long time, String sign, String data) throws E5Exception {
        //打印参数
        System.out.println("method---->getSites");
        System.out.println("eid---->"+eid);
        System.out.println("time---->"+time);
        System.out.println("sign---->"+sign);
        System.out.println("data---->"+data);
        //进行身份和安全认证
        String authResult = ExternalSystemAuth.extSystemAuth(eid, time, sign, data);
        if(authResult == null){
            //身份和安全认证成功，进行取站点业务
            String tenantCode = Tenant.DEFAULTCODE;

            Document[] docs = siteManager.getSites(tenantCode);

            JSONArray jsonArray = new JSONArray();

            for (int j = 0; j < docs.length; j++) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("siteID",docs[j].get("SYS_DOCUMENTID"));
                jsonObject.put("siteName",docs[j].get("site_name"));
                jsonObject.put("status",docs[j].get("site_status"));

                jsonArray.add(jsonObject);
            }

            JSONObject json = new JSONObject();
            json.put("result","success");
            json.put("site",jsonArray);
            return json.toString();
        } else {
            JSONObject json = new JSONObject();
            json.put("result","error");
            json.put("site",authResult);
            return json.toString();
        }

    }

    public String getUsersByOrg(int eid, long time, String sign, String data) throws E5Exception {
        //打印参数
        System.out.println("method---->getUsersByOrg");
        System.out.println("eid---->"+eid);
        System.out.println("time---->"+time);
        System.out.println("sign---->"+sign);
        System.out.println("data---->"+data);
        //进行身份和安全认证
        String authResult = ExternalSystemAuth.extSystemAuth(eid, time, sign, data);
        if(authResult == null){
            //身份和安全认证成功，进行取用户业务
            JSONObject jsonObject = JSONObject.fromObject(data);
            boolean getAll = false;
            if(jsonObject.size()<=0){
                getAll = true;
            }

            DBSession conn = null;
			IResultSet rs = null;
            JSONArray jsonArray = new JSONArray();
			try{
				conn = Context.getDBSession();
				StringBuilder sql = new StringBuilder();

				if(getAll){
                    sql.append("select SYS_DOCUMENTID,u_siteID,u_name,u_code,u_orgID,u_org,u_iconUrl from xy_userExt where SYS_DELETEFLAG=0");
                    rs = conn.executeQuery(sql.toString(), new Object[]{});
                }else {
				    int depID = jsonObject.getInt("depID");
                    sql.append("select SYS_DOCUMENTID,u_siteID,u_name,u_code,u_orgID,u_org,u_iconUrl from xy_userExt where u_orgID=? AND SYS_DELETEFLAG=0");
                    rs = conn.executeQuery(sql.toString(), new Object[]{depID});
                }

                while (rs.next()) {
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("userID", rs.getInt("SYS_DOCUMENTID"));
                    jsonObj.put("siteID", rs.getInt("u_siteID"));
                    jsonObj.put("userName", StringUtils.getNotNull(rs.getString("u_name")));
                    jsonObj.put("userCode", StringUtils.getNotNull(rs.getString("u_code")));
                    jsonObj.put("orgID", rs.getInt("u_orgID"));
                    jsonObj.put("orgName", StringUtils.getNotNull(rs.getString("u_org")));
                    jsonObj.put("iconUrl", StringUtils.getNotNull(rs.getString("u_iconUrl")));

                    jsonArray.add(jsonObj);
                }
            } catch (Exception e){
				e.printStackTrace();
                JSONObject json = new JSONObject();
                json.put("result","error");
                json.put("user","查询时出错");
                return json.toString();
			} finally {
				ResourceMgr.closeQuietly(rs);
				ResourceMgr.closeQuietly(conn);
			}

            JSONObject json = new JSONObject();
            json.put("result","success");
            json.put("user",jsonArray);
            return json.toString();
        } else {
            JSONObject json = new JSONObject();
            json.put("result","error");
            json.put("user",authResult);
            return json.toString();
        }

    }

    public String getUsersBySite(int eid, long time, String sign, String data) throws E5Exception {
        //打印参数
        System.out.println("method---->getUsersBySite");
        System.out.println("eid---->"+eid);
        System.out.println("time---->"+time);
        System.out.println("sign---->"+sign);
        System.out.println("data---->"+data);
        //进行身份和安全认证
        String authResult = ExternalSystemAuth.extSystemAuth(eid, time, sign, data);
        if(authResult == null){
            //身份和安全认证成功，进行取用户业务
            JSONObject jsonObject = JSONObject.fromObject(data);
            boolean getAll = false;
            if(jsonObject.size()<=0){
                getAll = true;
            }

            DBSession conn = null;
            IResultSet rs = null;
            JSONArray jsonArray = new JSONArray();
            try{
                conn = Context.getDBSession();
                StringBuilder sql = new StringBuilder();

                if(getAll){
                    sql.append("select SYS_DOCUMENTID,u_siteID,u_name,u_code,u_orgID,u_org,u_iconUrl from xy_userExt where SYS_DELETEFLAG=0");
                    rs = conn.executeQuery(sql.toString(), new Object[]{});
                }else {
                    int siteID = jsonObject.getInt("siteID");
                    sql.append("select SYS_DOCUMENTID,u_siteID,u_name,u_code,u_orgID,u_org,u_iconUrl from xy_userExt where u_siteID=? AND SYS_DELETEFLAG=0");
                    rs = conn.executeQuery(sql.toString(), new Object[]{siteID});
                }

                while (rs.next()) {
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("userID", rs.getInt("SYS_DOCUMENTID"));
                    jsonObj.put("siteID", rs.getInt("u_siteID"));
                    jsonObj.put("userName", StringUtils.getNotNull(rs.getString("u_name")));
                    jsonObj.put("userCode", StringUtils.getNotNull(rs.getString("u_code")));
                    jsonObj.put("orgID", rs.getInt("u_orgID"));
                    jsonObj.put("orgName", StringUtils.getNotNull(rs.getString("u_org")));
                    jsonObj.put("iconUrl", StringUtils.getNotNull(rs.getString("u_iconUrl")));

                    jsonArray.add(jsonObj);
                }
            } catch (Exception e){
                e.printStackTrace();
                JSONObject json = new JSONObject();
                json.put("result","error");
                json.put("user","查询时出错");
                return json.toString();
            } finally {
                ResourceMgr.closeQuietly(rs);
                ResourceMgr.closeQuietly(conn);
            }

            JSONObject json = new JSONObject();
            json.put("result","success");
            json.put("user",jsonArray);
            return json.toString();
        } else {
            JSONObject json = new JSONObject();
            json.put("result","error");
            json.put("user",authResult);
            return json.toString();
        }

    }

}
