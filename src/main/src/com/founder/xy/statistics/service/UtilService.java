package com.founder.xy.statistics.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import com.founder.amuc.commons.DateUtil;
import com.founder.e5.commons.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.founder.e5.commons.DomHelper;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBType;
import com.founder.xy.statistics.dao.UtilDAO;

/**
 * Created by Ethan on 2017/2/16.
 */
@Service
@Scope("prototype")
public class UtilService {
    @Autowired
    UtilDAO utilDAO;

    public List<Map<String, Object>> getAllDepartmentInfo(int orgID) throws E5Exception {
        return utilDAO.getAllDepartmentInfo(orgID);
    }

    public List<Map<String, Object>> getUserInfoList(String departmentID,String siteID) throws E5Exception {
        return utilDAO.getUserInfoByDepartmentID(departmentID,siteID);
    }

    public List<Map<String, Object>> getBatmanInfoList(String siteID) throws E5Exception {
        return utilDAO.getBatmanInfo(siteID);
    }

    //TODO 其它时间函数同理优化
    public String getTimeSQL(Timestamp beginTime, Timestamp endTime) throws E5Exception {
        StringBuilder returnSQL = new StringBuilder();
        String dbType = DomHelper.getDBType();
        if (dbType.equals(DBType.ORACLE)) {
            returnSQL.append("TO_DATE('" + DateUtils.format(beginTime, "yyyy-MM-dd HH:mm:ss")+ "','yyyy-mm-dd hh24:mi:ss')");
            returnSQL.append("_AND_");
            returnSQL.append("TO_DATE('" + DateUtils.format(endTime, "yyyy-MM-dd HH:mm:ss") + "','yyyy-mm-dd hh24:mi:ss')");
        } else {
            returnSQL.append("'" + beginTime + "'");
            returnSQL.append("_AND_");
            returnSQL.append("'" + endTime + "'");
        }
        return returnSQL.toString();
    }

	public List<Map<String, Object>> getUserIDByUserCode(String userCode) throws E5Exception {
		return utilDAO.getUserIDByUserCode(userCode);
	}
}
