package com.founder.xy.workspace.service;

import com.founder.e5.commons.ArrayUtils;
import com.founder.e5.flow.Proc;
import com.founder.e5.flow.ProcUnflow;
import com.founder.xy.commons.DocTypes;

/**
 * 操作栏服务：
 * 1、用于“审核稿件”界面：只显示流程操作（驳回、发布）
 * 2、用于“我的”界面：对原稿，显示除了“新建”外的操作，对发布库，显示流程操作，但不包括“合成多标题”
 * @author Gong Lijie
 * 2015-5-21
 */
public class ToolkitServiceFlow extends ToolkitService {
	
	@Override
	protected boolean hasPermission(Proc proc, Proc[] flowProcs, Proc[] unflowProcs,
			int perm, int unflowPerm)
	{
		if (proc.getProcType() == Proc.PROC_UNFLOW) {
			if (((ProcUnflow)proc).getDocTypeID() == DocTypes.ORIGINAL.typeID()) {
				//若是原稿，则允许显示除了新建以外的所有操作
				return (proc.getIconID() <= 0);
			}
			//非流程操作，只能显示“日志”、“解锁”、“关联”、“复制”
			return (proc.getOpID() == op_log || proc.getOpID() == op_unlock || proc.getOpID() == op_rel || proc.getOpID() == op_copy);
			//return (proc.getOpID() == op_log || proc.getOpID() == op_unlock || "关联".equals(proc.getProcName()) || "复制".equals(proc.getProcName()));
		} else {
			//“我的”里不显示“合成多标题”、各推送
			if ( ArrayUtils.contains(op_hide_when_self, proc.getOpID())) return false;
			
			//按权限显示流程操作
			return hasPermissionFlow(proc, perm, flowProcs);
		}
	}
}
