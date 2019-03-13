package com.founder.xy.workspace.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import com.founder.e5.commons.DomHelper;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.dom.DocLib;
import com.founder.e5.flow.Operation;
import com.founder.e5.flow.Proc;
import com.founder.e5.flow.ProcReader;
import com.founder.e5.workspace.service.ToolkitServiceImpl;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.config.ConfigReader;
import com.founder.xy.config.SecurityHelper;

/**
 * 修改E5的操作按钮读取逻辑：
 * 1、去掉文件夹权限判断。本系统中不使用文件夹权限
 * @author Gong Lijie
 */
public class ToolkitService extends ToolkitServiceImpl {
	protected int op_log = 0; //日志操作
	protected int op_unlock = 0; //解锁操作（只能显示流程操作的场景中，应该还能显示解锁、日志）
	protected int op_rel = 0; //关联操作
	protected int op_copy = 0; //复制操作
	protected int[] op_hide_when_self = null; //“我的”界面不能显示的：合成多标题、推送
	
	protected int[] opIDs_hide_when_web = null; //web发布渠道时不显示的操作，如推送客户端
	protected int[] opIDs_hide_when_app = null; //app发布渠道时不显示的操作，如推送区块
	protected int[] opIDs_hide_when_one_channel = null; //只有一个渠道时不显示的操作，“推送渠道”
	
	//不判断文件夹权限
	public boolean canEditFolder(int roleID, int fvID, boolean isQuery) {
		return true;
	}
	
	//改成public，方便取列表列上的操作时使用
	@SuppressWarnings("unchecked")
	public List<Proc> getProcList(int roleID, int docLibID, int flowID, int flowNodeID, 
			int fvID, boolean isQuery, boolean needCheckProc, int docCount) {
		init();
		
		List<Proc> procs = super.getProcList(roleID, docLibID, flowID, flowNodeID, fvID, isQuery, needCheckProc, docCount);
		filterProcs(docLibID, procs);
		
		return procs;
	}
	
	/**
	 * 过滤操作
	 * @param procs
	 */
	private void filterProcs(int docLibID, List<Proc> procs) {
		//只有稿件才需要按渠道检查操作是否显示
		if (DomHelper.getDocTypeIDByLibID(docLibID) == DocTypes.ARTICLE.typeID()) {
			if (ConfigReader.onlyWeb()) {
				filterProcs(opIDs_hide_when_web, procs);
				filterProcs(opIDs_hide_when_one_channel, procs);
			} else if (ConfigReader.onlyApp()) {
				filterProcs(opIDs_hide_when_app, procs);
				filterProcs(opIDs_hide_when_one_channel, procs);
			} else {
				//系统有web和app稿件库时，进一步确定是web还是app，再做操作过滤。
				try {
					String tCode = LibHelper.getTenantCodeByLib(docLibID);
					List<DocLib> libs = LibHelper.getLibs(DocTypes.ARTICLE.typeID(), tCode);
					if (libs.get(0).getDocLibID() == docLibID) {
						//若是第一个稿件库，则表示web
						filterProcs(opIDs_hide_when_web, procs);
					} else if (libs.get(1).getDocLibID() == docLibID) {
						//若是第2个稿件库，则表示app库
						filterProcs(opIDs_hide_when_app, procs);
					}
				} catch (E5Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	private void filterProcs(int[] opIDs, List<Proc> procs) {
		if (procs == null || opIDs == null) return;
		
		int i = 0;
		while (i < procs.size()){
			if (ArrayUtils.contains(opIDs, procs.get(i).getOpID())) {
				procs.remove(i);
			} else {
				i++;
			}
		}
	}
	private synchronized void init() {
		if (op_log > 0) return;
		
		try {
			int docTypeID = InfoHelper.getArticleTypeID();
			
			ProcReader procReader = (ProcReader)Context.getBean(ProcReader.class);
			Operation[] ops = procReader.getOperations(docTypeID);
			if (ops == null) return;
			
			//Web稿件库里隐藏的操作
			List<String> webHideList = new ArrayList<String>(Arrays.asList("推送(客户端)", "推送稿件一览", "稿件位置", 
					"新增广告", "新增全景图", "专题更新", "自定义推荐模块"));
			//App稿件库里隐藏的操作
			List<String> appHideList = new ArrayList<String>(Arrays.asList("推送(区块)", "合成多标题", 
					"稿件属性", "二维码", "下载二维码"));
			
			if (!SecurityHelper.specialUsable()) {
				webHideList.add("新增专题");
			}
			
			if (!SecurityHelper.mugedaUsable()) {
				appHideList.add("新增H5");
			}
			//没有直播加密点
			if (!SecurityHelper.liveUsable()) {
				webHideList.add("新增直播");
				appHideList.add("新增直播");
			}
			//没有视频密点
			if (!SecurityHelper.videoUsable()) {
				webHideList.add("新增视频");
				appHideList.add("新增视频");
			}
			
			opIDs_hide_when_web = findProcs(ops, webHideList.toArray(new String[0]));
			opIDs_hide_when_app = findProcs(ops, appHideList.toArray(new String[0]));
			op_hide_when_self = findProcs(ops, new String[]{
					"合成多标题", "推送(微信)", "推送(微博)", "推送(区块)", });
			
			String[] procNames2 = {"推送(渠道)"};
			opIDs_hide_when_one_channel = findProcs(ops, procNames2);
			
			//找出日志操作的OperationID
			for (int i = 0; i < ops.length; i++) {
				if (ops[i].getName().equals("日志")) {
					op_log = ops[i].getID();
				}
				if (ops[i].getName().equals("解锁")) {
					op_unlock = ops[i].getID();
				}
				if (ops[i].getName().equals("关联") || ops[i].getName().equals("关联栏目")) {
					op_rel = ops[i].getID();
				}
				if (ops[i].getName().equals("复制")) {
					op_copy = ops[i].getID();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//按名字查找稿件的操作，得到操作ID数组
	private int[] findProcs(Operation[] ops, String[] names) {
		List<Integer> list = new ArrayList<>();
		for (int i = 0; i < ops.length; i++) {
			if (ArrayUtils.contains(names, ops[i].getName())) {
				list.add(ops[i].getID());
			}
		}
		return InfoHelper.getIntArray(list);
	}
}
