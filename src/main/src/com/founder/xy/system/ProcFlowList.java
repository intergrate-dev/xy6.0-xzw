package com.founder.xy.system;

import java.io.Serializable;
import java.util.List;

import com.founder.e5.flow.Proc;

public class ProcFlowList implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private long flowNodeID;
	private String flowNodeName;
	private List<Proc> procList;
	
	public long getFlowNodeID() {
		return flowNodeID;
	}
	public void setFlowNodeID(long flowNodeID) {
		this.flowNodeID = flowNodeID;
	}
	public String getFlowNodeName() {
		return flowNodeName;
	}
	public void setFlowNodeName(String flowNodeName) {
		this.flowNodeName = flowNodeName;
	}
	public List<Proc> getProcList() {
		return procList;
	}
	public void setProcList(List<Proc> procList) {
		this.procList = procList;
	}
	
}
