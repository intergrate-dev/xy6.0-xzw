package com.founder.xy.workspace.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.context.CacheReader;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.flow.Proc;
import com.founder.e5.flow.ProcFlow;
import com.founder.e5.flow.ProcOrder;
import com.founder.e5.flow.ProcUnflow;
import com.founder.e5.sys.org.Role;
import com.founder.e5.workspace.service.ToolkitServiceImpl;
import com.founder.xy.system.site.SiteUserCache;

public class ToolOriginalServiceFlow extends ToolkitServiceImpl {

	public String groupID;
	
	public String siteID;
	
	public int userLibID;
	
	public int userID;
	
	public ToolOriginalServiceFlow(String groupID, String siteID, int userLibID, int userID) {
		super();
		this.groupID = groupID;
		this.siteID = siteID;
		this.userLibID = userLibID;
		this.userID = userID;
	}

	@Override
	protected List getProcList(int roleID, int docLibID, int flowID, int flowNodeID, 
			int fvID, boolean isQuery, boolean needCheckProc, int docCount)
	{
		
		List returnList = new ArrayList();
		//一个用户可以授权多个角色
		Role[] roles = getRolesBySite(userLibID, userID, Integer.valueOf(siteID));
		if(roles.length==0) return returnList;
		
		try
		{
			//取节点操作的排序
			int docTypeID = docLibReader.get(docLibID).getDocTypeID();
			ProcOrder[] procOrders = procOrderReader.getProcOrders(docTypeID, flowNodeID);
			if (procOrders == null) return null;
			
			//取得当前节点下的所有流程和非流程操作，以备取单个操作。这样可以稍微加快速度，不必每次从头扫描缓存
			ProcFlow[] flowProcs = null;
//			flowNodeID==0非同一流程节点,flowNodeID!=0同一流程节点的id
			if (flowNodeID > 0) flowProcs = procReader.getProcs(flowNodeID);
			ProcUnflow[] unflowProcs = procReader.getUnflows(docTypeID);
			
			if(flowProcs==null){//当流程节点ID为空 过滤选中操作
				List<ProcUnflow> unfliows = new ArrayList<ProcUnflow>();
				for (int i = 0; i < unflowProcs.length; i++) {
					ProcUnflow procUnflow = unflowProcs[i];
					if(procUnflow.getIconID()!=0){
						unfliows.add(procUnflow);
					}
				}
				unflowProcs = unfliows.toArray(new ProcUnflow[unfliows.size()]);
			}
			
			//取得流程权限
			if (flowID == 0 && flowNodeID > 0)
				flowID = flowReader.getFlowNode(flowNodeID).getFlowID();
				
//			int perm = flowPermissionReader.get(roleID,flowID,flowNodeID);
//			int unflowPerm = flowPermissionReader.getUnflowPermission(roleID,docTypeID);
//			boolean canEditFolder = canEditFolder(roleID, fvID, isQuery);
			
			for (int j = 0; j < roles.length; j++) {
				roleID = roles[j].getRoleID();
				List<String> unflowCheck = getProcs(roleID, groupID, siteID, 0);
				List<String> flowCheck = getProcs(roleID, groupID, siteID, 1);
				//单个操作权限检查
				Proc proc = null;
				for(int i = 0;i < procOrders.length; i++)
				{
					proc = getProc(procOrders[i], flowProcs, unflowProcs);
					if (proc == null) continue;
					//操作权限
//					if (!hasPermission(proc, flowProcs, unflowProcs, perm, unflowPerm))
//						continue;
					
					if(checkProc(proc, unflowCheck, flowCheck)){
						//若要求检查操作的综合权限，则调用canShow
//						if (!needCheckProc || canShow(proc, canEditFolder, docCount, isQuery))
						if(!returnList.contains(proc))
						returnList.add(proc);
					}
					
				}
			}
			if(docCount!=1){
//				returnList.remove();
                for (int i=0;i<returnList.size();i++) {
                    Object o = returnList.get(i);
                    String s = o.toString();
                    if(s.contains("稿件详情")||s.contains("解锁")||s.contains("复制")||s.contains("流程记录")){
                        returnList.remove(o);
                        i--;
                    }
                }



			}
		} catch (E5Exception e) {
			log.error("[ToolkitService]Get Proc Exception! roleID=" + roleID 
					+ ",fvID=" + fvID + ",isQuery=" + isQuery 
					+ ",docLibID=" + docLibID + ",flowID=" + flowID 
					+ ",flowNodeID=" + flowNodeID, e);
		}
		return returnList;
	}
	
	private boolean checkProc(Proc proc, List<String> unperms, List<String> perms) {
		if(unperms.contains(String.valueOf(proc.getProcID()))||perms.contains(String.valueOf(proc.getProcID()))){
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param roleID
	 * @param groupID 源稿分类/栏目ID
	 * @param siteID
	 * @param type 0：获取非流程权限  1：获取流程权限
	 * @return
	 * @throws E5Exception
	 */
	private List<String> getProcs(int roleID, String groupID, String siteID, int type) throws E5Exception{
		DBSession conn = null;
        IResultSet rs = null;
        String sql = "select nresourceid from fsys_permission where nid=? and nresourcetype=?";
        List<String> list = new ArrayList<String>();
        //源稿库栏目分类权限：源稿分类栏目ID + OriginalUnFlow/OriginalFlow + 站点ID
        String permissionStr = groupID + (type==0?"OriginalUnFlow":"OriginalFlow") + siteID;
        try {
            conn = Context.getDBSession();
            rs = conn.executeQuery(sql, new Object[]{roleID, permissionStr});
            if (rs.next()) {
            	String idStr = rs.getString("nresourceid");
            	String[] procs = idStr.split(",");
            	if(procs!=null){
            		list = Arrays.asList(procs);
            	}
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(rs);
            ResourceMgr.closeQuietly(conn);
        }
        
        return list;
	}
	
	private Role[] getRolesBySite(int userLibID, int userID, int siteID) {
		SiteUserCache siteUserCache = (SiteUserCache)(CacheReader.find(SiteUserCache.class));
		int[] roleIDs =  siteUserCache.getRoles(userLibID, userID, siteID);
		if (roleIDs == null || roleIDs.length == 0) return null;

		Role[] roles = new Role[roleIDs.length];
		for (int i = 0; i < roles.length; i++) {
			roles[i] = new Role();
			roles[i].setRoleID(roleIDs[i]);
		}
		return roles;
	}
	
}
