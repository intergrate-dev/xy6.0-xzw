package com.founder.xy.statistics.web;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.founder.e5.commons.DomHelper;
import com.founder.e5.context.E5Exception;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;
import com.founder.xy.statistics.service.UtilService;
import com.founder.xy.statistics.util.TimeUtil;

/**
 * Created by Ethan on 2017/2/16.
 */

@Controller
@Scope("prototype")
@RequestMapping("/xy/statisticsutil")
public class UtilController {
    @Autowired
    UtilService utilService;

    @RequestMapping(value = "FindDepartment.do", method = RequestMethod.POST)
    public
    @ResponseBody
    Map<String, Object> findDepartment(HttpServletRequest request, @RequestBody Map<String, Object> inParam) throws E5Exception {
		//找到当前租户对应的根机构ID
		int orgID = rootOrgID();
		
        List<Map<String, Object>> departmentList = utilService.getAllDepartmentInfo(orgID);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("departmentData", departmentList);
        return resultMap;
    }
	@RequestMapping(value = "FindUserByDepartmentID.do", method = RequestMethod.POST)
    public
    @ResponseBody
    Map<String, Object> findUserByDepartmentID(@RequestBody Map<String, Object> inParam) throws E5Exception {
        String departmentID = MapUtils.getString(inParam, "departmentID");
        String siteID = MapUtils.getString(inParam, "siteID");
        List<Map<String, Object>> userList = utilService.getUserInfoList(departmentID, siteID);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("userData", userList);
        return resultMap;
    }

    @RequestMapping(value = "FindBatman.do", method = RequestMethod.POST)
    public
    @ResponseBody
    Map<String, Object> findBatman(@RequestBody Map<String, Object> inParam) throws E5Exception {
        String siteID = MapUtils.getString(inParam, "siteID");
        List<Map<String, Object>> userList = utilService.getBatmanInfoList(siteID);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("batmanData", userList);
        return resultMap;
    }

    @RequestMapping(value = "GetTimeSQL.do", method = RequestMethod.POST)
    public
    @ResponseBody
    Map<String, Object> getTimeSQL(@RequestBody Map<String, Object> inParam) throws E5Exception {
        Timestamp beginTime;
        Timestamp endTime;
        try{
            beginTime = TimeUtil.StringToTimestamp(MapUtils.getString(inParam, "beginTime").trim());
            endTime = TimeUtil.StringToTimestamp(MapUtils.getString(inParam, "endTime").trim());
        } catch (Exception e){
            throw new E5Exception("Invalid Time Data!");
        }
        String timeSQL = utilService.getTimeSQL(beginTime, endTime);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("timeSQL", timeSQL);
        return resultMap;
    }
    
    /**
     * 获取当前数据库类型
     * @return
     * @throws E5Exception
     */
    @RequestMapping(value = "GetDBtype.do", method = RequestMethod.POST)
    public
    @ResponseBody
    String getDBtype() throws E5Exception {
    	String dbType = DomHelper.getDBType();
        return dbType;
    }
    
    /**
     * 
     * @param inParam
     * @return 获取 当前用户ID、用户名和用户所在的部门ID、部门名称
     * @throws E5Exception
     */
    @RequestMapping(value = "FindUserIdByUserCode.do", method = RequestMethod.POST)
    public
    @ResponseBody
    Map<String, Object> findUserIdByUserCode(@RequestBody Map<String, Object> inParam) throws E5Exception {
        //String siteID = MapUtils.getString(inParam, "siteID");
        String userCode = MapUtils.getString(inParam, "userCode");
        List<Map<String, Object>> userList = utilService.getUserIDByUserCode(userCode);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("userData", userList);
        return resultMap;
    }
    
	private int rootOrgID() {
		//目前只用默认租户
		return 1;
		
		/*
		应根据session中的租户信息得到租户对应的机构ID
		Tenant tenant = tenantManager.get(code);
		return tenant.getOrgID();
		*/
	}
    
	/**
     * 
     * @param inParam
     * @return 获取 redis中记录的 上次回写数据的时间
     * @throws E5Exception
     */
    @RequestMapping(value = "findPreWriteBackTime.do", method = RequestMethod.POST)
    public
    @ResponseBody
    Map<String, Object> findPreWriteBackTime(@RequestBody Map<String, Object> inParam) throws E5Exception {
        
    	String writeBack = RedisManager.get(RedisKey.NIS_EVENT_WRITEBACK_TIME);
    	long preTime = Long.parseLong(writeBack);
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date= new Date(preTime);
        String dateValue = sdf.format(date);
    	
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("writeBackTime", dateValue);
        return resultMap;
    }
    
}
