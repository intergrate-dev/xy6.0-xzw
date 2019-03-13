package com.founder.amuc.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import net.sf.json.JSONArray;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.founder.amuc.action.ReadPermissionManager;
import com.founder.amuc.commons.Constant;
import com.founder.amuc.commons.InfoHelper;
import com.founder.e5.commons.Log;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.context.E5Exception;
import com.founder.e5.dom.DocLib;

/**
 * 阅读设置接口
 * @author yanhe
 * @created 2017年4月12日
 */
@Controller
@RequestMapping("/api/read")
public class ReadPermissionAdapter {
  private Log log = com.founder.e5.context.Context.getLog("amuc.api");

  
  /** 
  * @author  yanhe 
  * 功能：根据uid获得用户阅读设置权限
  * 访问方式：GET <webroot>/api/read/getUserPermission.do?uid=
  * @return 用户能看到的所有报纸基本信息和阅读权限设置
  * @throws E5Exception 
  */ 
  @RequestMapping(value="/getUserPermission.do")
  public void userPermission(HttpServletRequest request,
      HttpServletResponse response, Map model) throws E5Exception{
    response.setHeader("Access-Control-Allow-Origin","*");
    int siteID = Integer.parseInt(request.getParameter("siteID"));
    String uid = request.getParameter("uid");
    String tenantcode = InfoHelper.getTenantCode(request);
    DocLib paperDocLib = InfoHelper.getLib(Constant.DOCTYPE_PAPER, tenantcode);
    DocLib cardDocLib = InfoHelper.getLib(Constant.DOCTYPE_PAPERCARD, tenantcode);
    DocLib mealDocLib = InfoHelper.getLib(Constant.DOCTYPE_SETMEAL, tenantcode);
    DocLib mDocLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER, tenantcode);
    DocLib orderDocLib = InfoHelper.getLib(Constant.DOCTYPE_ORDERS, tenantcode);
    DocLib permissionDocLib = InfoHelper.getLib(Constant.DOCTYPE_READPERMISSION, tenantcode);
    ReadPermissionManager controller = (ReadPermissionManager)com.founder.e5.context.Context.getBean(ReadPermissionManager.class);
    Map<String,Object> result = controller.getUserPermission1(uid,tenantcode,paperDocLib,cardDocLib,mealDocLib,mDocLib,orderDocLib,permissionDocLib,siteID);
    if (result != null && result.size() > 0) {
      String json = JSONArray.fromObject(result).toString();
      if(json.startsWith("[") && json.endsWith("]")) 
        outputString(json.substring(1, json.length() - 1), response);
    }
  }
  
   /** 
    * @author  yanhe 
    * 功能：刷新所有报纸的阅读设置
    * 访问方式：GET <webroot>/api/read/getAllPermission.do?
    * @return void
    * @throws E5Exception 
    */ 
    @RequestMapping(value="/getAllPermission.do")
    public void allPermission(HttpServletRequest request,
        HttpServletResponse response, Map model) throws E5Exception {
      response.setHeader("Access-Control-Allow-Origin","*");
      int siteID = Integer.parseInt(request.getParameter("siteID"));
      String tenantcode = InfoHelper.getTenantCode(request);
      DocLib paperDocLib = InfoHelper.getLib(Constant.DOCTYPE_PAPER, tenantcode);
      DocLib cardDocLib = InfoHelper.getLib(Constant.DOCTYPE_PAPERCARD, tenantcode);
      DocLib mealDocLib = InfoHelper.getLib(Constant.DOCTYPE_SETMEAL, tenantcode);
      DocLib orderDocLib = InfoHelper.getLib(Constant.DOCTYPE_ORDERS, tenantcode);
      DocLib permissionDocLib = InfoHelper.getLib(Constant.DOCTYPE_READPERMISSION, tenantcode);
      ReadPermissionManager controller = (ReadPermissionManager)com.founder.e5.context.Context.getBean(ReadPermissionManager.class);
      controller.getAllPermission(tenantcode,paperDocLib,cardDocLib,mealDocLib,orderDocLib,permissionDocLib,siteID);
    }  

     public static void outputString(String result, HttpServletResponse response) {
       if (result == null) return;
       
       response.setContentType("application/json; charset=UTF-8");

       PrintWriter out = null;
       try {
         out = response.getWriter();
         out.write(result);
       } catch (IOException e) {
         e.printStackTrace();
       } finally {
         ResourceMgr.closeQuietly(out);
       }
     }
}