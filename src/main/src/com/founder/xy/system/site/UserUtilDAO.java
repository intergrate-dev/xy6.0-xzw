package com.founder.xy.system.site;

import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ethan on 2017/2/16.
 */
@Component
public class UserUtilDAO {

    public List<Map<String, String>> getUserCodeByToken(String token) throws E5Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("select USERCODE,TELHOMENUMBER from fsys_user ");
        sql.append("where BPNUMBER = ?");
        DBSession conn = null;
        IResultSet resultSet = null;
        List<Map<String, String>> returnList = new ArrayList<>();
        try {
            conn = Context.getDBSession();
            resultSet = conn.executeQuery(sql.toString(), new Object[]{token});
            while (resultSet.next()) {
                Map<String, String> rowData = new HashMap<>();
                rowData.put("userCode", resultSet.getString("USERCODE"));
                rowData.put("lastmodified", resultSet.getString("TELHOMENUMBER")); //token最后更新时间
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
}
