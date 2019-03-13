package com.founder.xy.system.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.flow.Flow;
import com.founder.e5.flow.FlowManager;
import com.founder.e5.flow.FlowNode;
import com.founder.e5.flow.Proc;
import com.founder.e5.flow.ProcManager;
import com.founder.e5.permission.Permission;
import com.founder.e5.permission.PermissionManager;
import com.founder.e5.web.BaseController;
import com.founder.e5.web.permission.DocTypeFlow;
import com.founder.e5.web.permission.FlowBundleDTO;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.system.ProcFlowList;

/**
 * 源稿库流程权限配置：主界面功能项权限 
 * 源稿库按分类赋予角色的流程权限
 * 
 * @author JiangYu
 */
@Controller
@RequestMapping("/xy/system")
public class ProcFlowOriginal extends BaseController {

	@Autowired
	private PermissionManager permissionManager;
	
	@Autowired
	private ProcManager procManager;
	
	@Autowired
	private FlowManager flowManager;
	
	/**
	 * 取出源稿库流程节点 和 当前角色的节点权限
	 * */
	@RequestMapping(value = "getOrgFlow.do")
	@ResponseBody
	public Map<String, Object> getOrgFlow(HttpServletRequest request,
			HttpServletResponse response, @RequestParam int colID,
			@RequestParam int roleID, @RequestParam int siteID) throws Exception {
		int colLibID = LibHelper.getLibID(DocTypes.ORIGINAL.typeID(),InfoHelper.getTenantCode(request));
		//ProcManager procManager = (ProcManager) Context.getBean("ProcManager");
		//FlowManager flowManager = (FlowManager) Context.getBean("FlowManager");
		Map<String, Object> model = new HashMap<String, Object>();
		Flow[] flowArr = flowManager.getFlows(colLibID);
		List<DocTypeFlow> retList = new ArrayList<DocTypeFlow>();
		DocTypeFlow dtf = new DocTypeFlow();
		dtf.setDocTypeID(colLibID);
		//dtf.setDocTypeName("原稿");
		if(flowArr!=null){
			for (int j = 0; j < flowArr.length; j++){
				FlowBundleDTO flow = new FlowBundleDTO();
				flow.setFlowID(flowArr[j].getID());
				flow.setFlowName(flowArr[j].getName());
				FlowNode[] nodes = flowManager.getFlowNodes(flow.getFlowID());
				if (nodes != null){
					List arrList = new ArrayList();
					for (int i = 0; i < nodes.length; i++){
//						FlowNodePermission fnd = new FlowNodePermission();
//						fnd.setFirst(nodes[i].getPreNodeID() == 0);
//						fnd.setNodeID(nodes[i].getID());
//						fnd.setNodeName(nodes[i].getName());
//						arrList.add(fnd);
						ProcFlowList procFlowList = new ProcFlowList();
						procFlowList.setFlowNodeID(nodes[i].getID());
						procFlowList.setFlowNodeName(nodes[i].getName());
						procFlowList.setProcList(getProcList(nodes[i].getID(), nodes[i].getName(), flow.getFlowID()));
						arrList.add(procFlowList);
					}
					flow.setProcList(arrList);
				}
				dtf.addFlow(flow);
			}
		}
		dtf.setUnflowArr(procManager.getUnflows(colLibID));
		if ((dtf.getFlows() != null && dtf.getFlows().size() > 0) || (dtf.getUnflowCount() > 0)){
			retList.add(dtf);
		}
		String unProcsIds = getProcs(roleID, colID + "OriginalUnFlow" + siteID);
		String procsIds = getProcs(roleID, colID + "OriginalFlow" + siteID);
		String procScopeID = getProcs(roleID, colID + "OriginalScope" + siteID);
		model.put("flowList", retList);
		model.put("unProcsIds", unProcsIds);
		model.put("procsIds", procsIds);
		model.put("procScopeID", procScopeID);
        return model;
	}
	
	/**
	 * 保存源稿栏目勾选 到 对应的角色
	 * */
	@RequestMapping(value = "saveOrgFlow.do")
	public void saveOrgFlow(HttpServletRequest request,
			HttpServletResponse response, @RequestParam int roleID,
			@RequestParam int colID, @RequestParam int siteID, @RequestParam String scope,
			@RequestParam String unIds, @RequestParam String ids)
			throws Exception {
		permissionManager.delete(roleID, colID + "OriginalUnFlow" + siteID);
		permissionManager.delete(roleID, colID + "OriginalFlow" + siteID);
		permissionManager.delete(roleID, colID + "OriginalScope" + siteID);
		
		if (!StringUtils.isBlank(unIds)) {
			Permission p = new Permission(roleID, colID + "OriginalUnFlow" + siteID, unIds, 1);
			permissionManager.save(p);
		}

		if (!StringUtils.isBlank(ids)) {
			Permission p = new Permission(roleID, colID + "OriginalFlow" + siteID, ids, 1);
			permissionManager.save(p);
		}
		
		Permission p = new Permission(roleID, colID + "OriginalScope" + siteID, scope, 1);
		permissionManager.save(p);
		
		// 填写系统记录
		StringBuffer info = new StringBuffer();
		info.append("给角色[").append(roleID).append("]设置源稿栏目流程权限");

		super.reportInfo(request, "源稿栏目流程权限设置", info.toString());

		InfoHelper.outputText("ok", response);
	}
	

	private List<Proc> getProcList(int flowNodeID, String flowNodeName, int flowID) throws E5Exception{
		DBSession conn = null;
        IResultSet rs = null;
        String sql = "select * from e5flow_procs where flowNodeID=? and flowID=? order by procID";
        List<Proc> resultList = new ArrayList<>();
        try {
            conn = Context.getDBSession();
            rs = conn.executeQuery(sql, new Object[]{flowNodeID,flowID});
            while (rs.next()) {
            	Proc proc = new Proc();
            	proc.setProcID(rs.getInt("procid"));
            	proc.setProcName(rs.getString("procname"));
            	resultList.add(proc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(rs);
            ResourceMgr.closeQuietly(conn);
        }
        return resultList;
	}
	
	/**
	 * 取出当前角色中  有权限的源稿分类栏目id字符串
	 * */
	public String getProcs(int roleID, String originalType) throws Exception {
		Permission[] permission = permissionManager.getPermissions(roleID, originalType);
		String resource = permission == null ? "" : permission[0].getResource();
		return resource;
	}
	
	@Override
	protected void handle(HttpServletRequest request,
			HttpServletResponse response,
			@SuppressWarnings("rawtypes") Map model) throws Exception {
	}
	
}
