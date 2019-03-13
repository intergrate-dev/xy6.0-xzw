package com.founder.xy.system.job;

import VJVAS.holders.EncodeTask2Holder;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.e5.scheduler.BaseJob;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;
import com.founder.xy.system.Tenant;
import com.founder.xy.video.VideoManager;
import localhost.VJVASPortType;
import localhost.VJVodServicePortType;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.rpc.holders.IntHolder;
import javax.xml.rpc.holders.StringHolder;
import java.rmi.RemoteException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.founder.amuc.pay.alipay.config.AlipayConfig.key;

public class NisMonitor extends BaseJob {
    @Autowired
    VideoManager videoManager;
    
	public NisMonitor() {
		super();
		log = Context.getLog("xy.nisMonitor");
	}

	@Override
	protected void execute() throws E5Exception {
		log.info("---开始视频转码监控");
		if(videoManager == null){
			videoManager = (VideoManager) Context.getBean("videoManager");
		}
		DocLib[] taskLibs = LibHelper.getLibs(DocTypes.NISTASK.typeID());

		for (DocLib taskLib : taskLibs) {
			try {
				oneLib(taskLib);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		log.info("本轮监控完成");
	}
	
	/* 处理一个视频转码任务表
	 */
	private void oneLib(DocLib taskLib) throws RemoteException {
		//对应的视频库ID
//		int videoLibID = LibHelper.getLibIDByOtherLib(DocTypes.VIDEO.typeID(), taskLib.getDocLibID());
		
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
					//转码完成进行后续操作
					if (process == 100) {
						autoPublish(task);
						//转码已完成，删除转码任务监控表
						docManager.delete(task);
						//清理redis
						clearRedis(task);
					}
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
	
	private void autoPublish(Document task) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		
		int docLibID = LibHelper.getNisattachment();
		long docID = task.getDocID();
		Document attDoc = docManager.get(docLibID, docID);
		String fileDir = attDoc.getString("att_path").substring(attDoc.getString("att_path").lastIndexOf(";")+1);
		
		//纳加视频服务器密码
		String passWord = InfoHelper.getConfig("视频系统", "服务器密码");
		String playerUrl = InfoHelper.getConfig("视频系统", "视频播放地址");
		String transDisk = InfoHelper.getConfig("视频系统", "转码文件根目录");
		String mappedDisk = InfoHelper.getConfig("视频系统", "点播映射磁盘");
		
//		String format = InfoHelper.getConfig("视频系统", "转码文件后缀");

		String transPath = transDisk + "/" + "nis-video/"
				+ "trans/" + fileDir.substring(0,fileDir.lastIndexOf("."));
		
		VJVodServicePortType vodPortType = videoManager.getVodService();
		boolean bSuccess = true;
		Pattern hashPattern = Pattern.compile("^hash=\\s*([^&]*)");
		
//		String[] pubFileTypes = format.split(",");
		//目前只转成mp4
		String[] pubFileTypes = {"mp4"};
		
		for (int i = 0; i < pubFileTypes.length; i++) {
			StringHolder hash = new StringHolder();
			String path = transPath + "." + pubFileTypes[i];
			//需要对映射文件路径进行转换
			String filePath = null;
			if (mappedDisk != null && !mappedDisk.isEmpty()) {
				filePath = path.replaceFirst(Matcher.quoteReplacement(transDisk),
				                             Matcher.quoteReplacement(mappedDisk));
			} else {
				filePath = path;
			}
			// 发布状态返回值
			IntHolder publishStatusResult = new IntHolder();
			boolean flag = true;
			for (int x = 0, len = 3; x < len && flag; x++) {
				if (flag) {
					// 发布文件
					try {
						vodPortType.publishFile(passWord, filePath, hash,
						                        publishStatusResult);
						flag = false;
					} catch (RemoteException e) {
						flag = true;
						e.printStackTrace();
					} finally {

					}
				}else
					publishStatusResult.value = 0;
			}

			if (publishStatusResult.value == 0 || publishStatusResult.value == -32) {
				Matcher ma = hashPattern.matcher(hash.value);
				String hashCode = "";
				if (ma.find()) {
					hashCode = ma.group(1);
				}
				String player = playerUrl + hashCode;
				if ("flv".equals(pubFileTypes[i])) {
					attDoc.set("att_path", player + ".flv");
					attDoc.set("att_url", player + ".flv");
//					attDoc.set("v_hashcode", hashCode);
				} else if ("mp3".equals(pubFileTypes[i])) {
					attDoc.set("att_path", player + ".mp3");
					attDoc.set("att_url", player + ".mp3");
					attDoc.set("att_urlPad", player + ".mp3");
//					attDoc.set("v_hashcodeApp", hashCode);
//					attDoc.set("v_hashcode", hashCode);
				} else {
					attDoc.set("att_path", player + ".mp4");
					attDoc.set("att_url", player + ".mp4");
					attDoc.set("att_urlPad", player + ".mp4");
//					attDoc.set("v_hashcodeApp", hashCode);
				}
			} else {
				System.out.println("发布失败,返回值：" + publishStatusResult.value);
				bSuccess = false;
				break;
			}

			int mainDocLibID = attDoc.getInt("att_articleLibID");
			long mainDocID = attDoc.getLong("att_articleID");

			if(mainDocLibID == LibHelper.getLibID(DocTypes.TIPOFF.typeID(), Tenant.DEFAULTCODE)){//记者帮
				Document mainDoc = docManager.get(mainDocLibID, mainDocID);

				String att=mainDoc.getString("a_attachments");
				//附件字段的json
				JSONObject JSONatt = new JSONObject();

				JSONObject jsonObject=JSONObject.fromObject(att);
				JSONArray pics=jsonObject.getJSONArray("pics");
				JSONArray videos = new JSONArray();//视频json
				JSONObject video = new JSONObject();

				video.put("urlApp", attDoc.getString("att_url"));
				video.put("url", attDoc.getString("att_url"));
				video.put("videoID", -1);
				videos.add(video);

				JSONatt.put("pics",pics);
				JSONatt.put("videos",videos);
				mainDoc.set("a_attachments", JSONatt.toString());

				docManager.save(mainDoc);
			}


		}

		docManager.save(attDoc);
		
	}
	
	private void clearRedis(Document task) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		
		int attDocLibID = LibHelper.getNisattachment();
		long attDocID = task.getDocID();
		Document attDoc = docManager.get(attDocLibID, attDocID);
		
		int docLibID = attDoc.getInt("att_articleLibID");
		long docID = attDoc.getLong("att_articleID");
		Document doc = docManager.get(docLibID, docID);
		//
		int tipoffDocLibID=LibHelper.getTipoff();
		Document tipoffDoc=docManager.get(tipoffDocLibID,docID);



		RedisManager.clear(RedisManager.getKeyBySite(RedisKey.MY_TIPOFF_KEY,doc.getInt("t_siteID")) + tipoffDoc.getInt("SYS_AUTHORID"));
		RedisManager.clear(RedisKey.APP_TIPOFF_KEY+ docID);


	}
}
