package com.founder.xy.workspace.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.founder.e5.commons.DomHelper;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.flow.Flow;
import com.founder.e5.flow.FlowNode;
import com.founder.e5.flow.FlowReader;
import com.founder.e5.flow.Operation;
import com.founder.e5.flow.Proc;
import com.founder.e5.flow.ProcReader;
import com.founder.xy.commons.InfoHelper;

/**
 * 列表上的操作列的处理器
 * @author Gong Lijie
 */
@Service
public class ListProcService {
	
	private FlowReader flowReader;
	private ProcReader procReader;
	private ToolkitService tool;
	private ToolkitService toolFlow;
	
	public String getProcs(int roleID, int docLibID, int fvID, int opFlow) throws E5Exception {
		init();
		
		StringBuffer result = new StringBuffer(); 
		
//		getProcList(int roleID, int docLibID, int flowID, int flowNodeID, 
//					int fvID, boolean isQuery, boolean needCheckProc, int docCount)
		
		ToolkitService localTool = opFlow == 1 ? toolFlow : tool;
		int docTypeID = DomHelper.getDocTypeIDByLibID(docLibID);
		//取出无流程的操作
		List<Proc> procs = localTool.getProcList(roleID, docLibID, 0, 0, fvID, false, true, 1);
		
		result.append("[");
		result.append(filter(0, procs));
		
		Flow[] flows = flowReader.getFlows(docTypeID);
		if (flows != null) {
			for (Flow flow : flows) {
				int flowID = flow.getID();
				
				FlowNode[] nodes = flowReader.getFlowNodes(flowID);
				if (nodes == null) continue;
				
				//取出一个流程节点的操作
				for (FlowNode node : nodes) {
					int flowNodeID = node.getID();
					
					procs = localTool.getProcList(roleID, docLibID, 
							flowID, flowNodeID, 0, true, true, 1);
					
					result.append(",").append(filter(flowNodeID, procs));
				}
			}
		}
		result.append("]");
		return result.toString();
	}
	//过滤出列表上可显示的列
	private String filter(int flowNodeID, List<Proc> procs) throws E5Exception {
		StringBuffer result = new StringBuffer(); 
		
		result.append("{\"flowNode\":\"" + flowNodeID + "\",\"procs\":[");
		if (procs != null) {
			for(int i = 0;i < procs.size(); i++) {
				Proc proc = procs.get(i);
				if(proc == null) continue;
			
				Operation op = procReader.getOperation(proc.getOpID());
				if(op == null) continue;
	
				if ((op.getShowType() & 4) == 4 && op.getDealCount() > 0) {
					if (result.length() > 40) result.append(",");
					
					result.append(jsonProc(i, proc, flowNodeID, op));
				}
			}
		}
		result.append("]}");
		return result.toString();
	}
	
	private String jsonProc(int i, Proc proc, int flowNodeID, Operation op) {
		StringBuffer result = new StringBuffer();
		result.append("{\"index\":\"" + i)
				.append("\",\"name\":\"").append(InfoHelper.filter4Json(proc.getProcName()))
				.append("\",\"proctype\":\"").append(proc.getProcType())
				.append("\",\"procid\":\"").append(proc.getProcID())
				.append("\",\"flownode\":\"").append(flowNodeID);
		
		result.append("\",\"opid\":\"").append(op.getID())
				.append("\",\"opurl\":\"").append(InfoHelper.filter4Json(op.getCodeURL()))
				.append("\",\"callmode\":\"").append(op.getCallMode())
				.append("\",\"opheight\":\"").append(op.getHeight())
				.append("\",\"opwidth\":\"").append(op.getWidth())
				.append("\",\"needprompt\":\"").append(op.isNeedPrompt())
				.append("\",\"resizable\":\"").append(op.isResizable())
				.append("\",\"dealcount\":\"").append(op.getDealCount())
				.append("\",\"showtype\":\"").append(op.getShowType());
		result.append("\"}");
		
		return result.toString();
	}
	
	private void init() {
		if (flowReader == null)
			flowReader = (FlowReader)Context.getBean(FlowReader.class);
		if (procReader == null)
			procReader = (ProcReader)Context.getBean(ProcReader.class);
		if (tool == null)
			tool = new ToolkitService();//取当前系统配置的操作栏服务，其中做了稿件、草稿箱等的处理
		if (toolFlow == null)
			toolFlow = new ToolkitServiceFlow();
	}
}
