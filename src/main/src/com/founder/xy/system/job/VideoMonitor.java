package com.founder.xy.system.job;

import java.rmi.RemoteException;

import javax.xml.rpc.holders.IntHolder;

import localhost.VJVASPortType;

import org.springframework.beans.factory.annotation.Autowired;

import VJVAS.holders.EncodeTask2Holder;

import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.e5.flow.FlowNode;
import com.founder.e5.flow.FlowReader;
import com.founder.e5.scheduler.BaseJob;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.video.VideoManager;

/**
 * 视频转码监控服务
 * @author Gong Lijie
 */
public class VideoMonitor extends BaseJob{
    @Autowired
    VideoManager videoManager;
    
	public VideoMonitor() {
		super();
		log = Context.getLog("xy.videoMonitor");
	}

	@Override
	protected void execute() throws E5Exception {
		//log.info("---开始视频转码监控");
		if(videoManager == null){
			videoManager = (VideoManager)Context.getBean("videoManager");
		}
		DocLib[] taskLibs = LibHelper.getLibs(DocTypes.VIDEOTASK.typeID());
		
		for (DocLib taskLib : taskLibs) {
			try {
				oneLib(taskLib);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		
		//log.info("本轮监控完成");
	}
	
	/* 处理一个视频转码任务表
	 */
	private void oneLib(DocLib taskLib) throws RemoteException {
		//对应的视频库ID
		int videoLibID = LibHelper.getLibIDByOtherLib(DocTypes.VIDEO.typeID(), taskLib.getDocLibID());
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			//查出转码任务
			Document[] tasks = docManager.find(taskLib.getDocLibID(), "SYS_DELETEFLAG=0", null);
			if (tasks == null || tasks.length == 0)
				return;
			
			//对每条记录，查询纳加视频系统，读出转码进度，写回任务表
			for (Document task : tasks) {
				String taskID = task.getString("v_taskID");
				if( taskID != null && !"".equals(taskID)){
					//读出转码进度
					int process =  getProcess(taskID);
					//写视频表
					setProcess(task, process, videoLibID);
				} else {
					docManager.delete(task);
				}

			}
		} catch (E5Exception e) {
			log.error("处理过程中出现错误: " + e.getLocalizedMessage(), e);
		}
	}
	
	//按任务编号查询纳加视频系统，读出转码进度。若找不到该转码任务，认为完成了，返回100
	private int getProcess(String taskID) throws RemoteException {
		int[] taskIDs = StringUtils.getIntArray(taskID);
		float sum = 0;
		// 返回状态
		IntHolder result = new IntHolder();
		// 编码状态
		EncodeTask2Holder et = new EncodeTask2Holder();
		String passWord = InfoHelper.getConfig("视频系统", "服务器密码");
		VJVASPortType vasPortType = videoManager.getVasMisService();
		for (int i = 0; i < taskIDs.length; i++) {	
			//任务编号
			int taskId = taskIDs[i];
			// 获得编码任务
			vasPortType.getEncodeTask2(passWord,taskId,et,result);
			if (result.value >= 0) {
				//编码状态为2，表示完成编码 
				if (et.value.getState() == 2) {
					//设置该任务状态为true,true表示任务完成
					sum = sum + 1;
				}else {//其他表示编码还未完成
					sum = sum + et.value.getFProgress();
				}
			} else {
				sum = sum + 1;
			}
		}
		
		int taskProgress = (int) (sum*100);
		int taskNum = taskIDs.length;
		return taskProgress/taskNum;
	}
	
	private void setProcess(Document task, int process, int videoLibID) throws E5Exception {
		//取出视频
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document video = docManager.get(videoLibID, task.getDocID());
		if (video == null) {
			//转码已完成，删除转码任务监控表
			docManager.delete(task);
			return;
		}
		
		//设置转码进度
		video.set("v_process", process);
		
		if (process == 100) {
			if (video.getInt("v_status") == 0) {
				//视频改为已转码
				video.set("v_status", 1);
				//设为下一个流程节点
				FlowReader flowReader = (FlowReader)Context.getBean(FlowReader.class);
				FlowNode nextNode = flowReader.getNextFlowNode(video.getCurrentNode());
				if (nextNode != null) {
					video.setCurrentNode(nextNode.getID());
					video.setCurrentStatus(nextNode.getWaitingStatus());
				}
			}
			//转码已完成，删除转码任务监控表
			docManager.delete(task);
		}
		
		//保存视频修改
		docManager.save(video);
	}
}
