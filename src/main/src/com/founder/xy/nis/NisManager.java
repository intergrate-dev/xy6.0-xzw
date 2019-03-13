package com.founder.xy.nis;

import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;
/**
 * Created by yu.feng on 2017/9/1.
 */
@Component
public class NisManager {

    public String getExternal() throws E5Exception{

        DBSession conn = null;
        IResultSet rs = null;
        try{
            conn = Context.getDBSession();

            StringBuilder sql = new StringBuilder();
            //以后多租户要改
            sql.append("select te_externals from g_tenant where te_code = 'xy'");
            rs = conn.executeQuery(sql.toString(), new Object[]{});
            if(rs.next()) {
//                JSONObject jsonObj = JSONObject.fromObject(rs.getString("te_externals"));
//                JSONArray array = jsonObj.getJSONArray("externals");
                return rs.getString("te_externals");
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(rs);
            ResourceMgr.closeQuietly(conn);
        }
        return null;
    }
}
