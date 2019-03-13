package com.founder.xy.workspace.service;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.founder.e5.context.Context;
import com.founder.e5.flow.Operation;
import com.founder.e5.flow.Proc;
import com.founder.e5.flow.ProcReader;
import com.founder.e5.workspace.service.ToolkitServiceImpl;
import com.founder.xy.commons.DocTypes;

/**
 * 移动采编中，打开稿件详情后可显示的稿件操作
 * @author Gong Lijie
 */
public class ToolkitServiceArticleApp extends ToolkitServiceImpl {
	//可以在移动采编客户端的稿件详情中执行的操作
	private static JSONArray OpsCanShow = new JSONArray();
	static {
		OpsCanShow.add(oneProc("edit", "修改"));
		OpsCanShow.add(oneProc("delete", "删除"));
		OpsCanShow.add(oneProc("publish", "发布"));
		OpsCanShow.add(oneProc("revoke", "撤稿"));
		OpsCanShow.add(oneProc("republish", "重发"));
		OpsCanShow.add(oneProc("transfer", "提交"));
		OpsCanShow.add(oneProc("reject", "驳回"));
		OpsCanShow.add(oneProc("pushapp", "推送(客户端)"));
		OpsCanShow.add(oneProc("record", "日志"));
		
		//读出每个操作对应的操作ID
		try {
			ProcReader procReader = (ProcReader)Context.getBean(ProcReader.class);
			Operation[] ops = procReader.getOperations(DocTypes.ARTICLE.typeID());
			if (ops != null) {
				for (Object oneProc : OpsCanShow) {
					JSONObject proc = (JSONObject)oneProc;
					proc.put("opID", getOpID(ops, proc.getString("name")));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static JSONObject oneProc(String code, String name) {
		JSONObject proc = new JSONObject();
		proc.put("code", code);
		proc.put("name", name);
		proc.put("opID", 0);
		return proc;
	}
	
	//按名字查找稿件的操作，得到操作ID数组
	private static int getOpID(Operation[] ops, String name) {
		for (int i = 0; i < ops.length; i++) {
			if (ops[i].getName().equals(name))
				return ops[i].getID();
		}
		return 0;
	}

	/**
	 * 得到稿件在客户端详情页可以显示的操作列表。
	 * 格式如：[
	 *    {code:"edit", name:"修改"},
	 *    {code:"delete", name:"删除"}
	 * ]
	 */
	@SuppressWarnings("unchecked")
	public JSONArray getProcList(int roleID, int docLibID, int flowID, int flowNodeID) {
		
		List<Proc> procs = super.getProcList(roleID, docLibID, flowID, flowNodeID, 0, false, false, 1);
		if (procs == null) return null;
		
		JSONArray result = new JSONArray();
		for (Object opCanShow : OpsCanShow) {
			JSONObject json = (JSONObject)opCanShow;
			int opID = json.getInt("opID");
		
			for (Proc proc : procs) {
				//逐个检查有权限的操作，若是移动App中可显示的，则复制一份json对象（避免误改）
				if (proc.getOpID() == opID) {
					result.add(clone(json));
					break;
				}
			}
		}
		return result;
	}
	
	//不判断文件夹权限
	public boolean canEditFolder(int roleID, int fvID, boolean isQuery) {
		return true;
	}

	private JSONObject clone(JSONObject one) {
		JSONObject newone = new JSONObject();
		newone.put("name", one.getString("name"));
		newone.put("code", one.getString("code"));
		
		return newone;
	}
}
