package com.founder.xy.video;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.xml.rpc.ServiceException;
import javax.xml.rpc.holders.ByteArrayHolder;
import javax.xml.rpc.holders.IntHolder;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;

import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.sys.StorageDevice;
import com.founder.e5.sys.StorageDeviceManager;
import com.founder.e5.sys.SysFactory;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;

import VJVAS.DoSystemFileOPIn;
import VJVAS.EncodeTaskParams;
import VJVAS.NsSnapMediaFilePicIn;
import VJVAS.holders.SystemDiskInfoHolder;
import localhost.VAS;
import localhost.VJVASLocator;
import localhost.VJVASPortType;
import localhost.VJVodServiceLocator;
import localhost.VJVodServicePortType;
import localhost.VodService;


/**
 * 视频管理器
 * 
 * @author Deng Chaochen
 */
@Component
public class VideoManager {
	
	/**视频编码*/
	public static final String VIDEOCODEC = "h264";
	/**视频帧率*/
	public static final float VIDEOFRAMERATE = 15.0f;
	/**音频编码*/
	public static final String AUDIOCODEC = "aac";
	/**音频码率*/
	public static final int AUDIOBITRATE = 32;
	/**音频采样率*/
	public static final int AUDIOSAMPLERATE = 22050;
	
	//获取纳加管理操作
	public VJVASPortType getVasMisService(){
		//管理服务地址
		String misUrl = InfoHelper.getConfig("视频系统", "管理服务地址");
		VAS vasService = new VJVASLocator();
		
		VJVASPortType vasPortType = null;
		// 纳加管理操作
		try {
			vasPortType = vasService.getVJVAS(new URL(misUrl));
		} catch (MalformedURLException | ServiceException e) {
			e.printStackTrace();
		}
		
		return vasPortType;
	}

	public VJVodServicePortType getVodService(){
		//点播服务地址
		String vodUrl = InfoHelper.getConfig("视频系统", "点播服务地址");
		VodService vodService = new VJVodServiceLocator();	
		localhost.VJVodServicePortType vodPortType = null;

		try {
			vodPortType = vodService.getVJVodService(new URL(vodUrl));
		} catch (MalformedURLException | ServiceException e) {
			e.printStackTrace();
		}
		
		return (VJVodServicePortType) vodPortType;
	}
	
	//视频转码
	public void transCode(Document doc) throws E5Exception {	
		String passWord = InfoHelper.getConfig("视频系统", "服务器密码");
		//获取那几视频管理服务
		VJVASPortType vasPortType = getVasMisService();
		//转码任务的IDs
		StringBuffer taskIds = new StringBuffer();
		//编码参数
		EncodeTaskParams in = new EncodeTaskParams();
		String inputFile = normalize(doc.getString("v_path"));
		in.setInputFile(inputFile);
		// 文件操作参数
		DoSystemFileOPIn fileOperationParameter = new DoSystemFileOPIn();
		// 不覆盖
		fileOperationParameter.setBOverwrite(false);
		// 创建目录
		fileOperationParameter.setOperation(1);
		String transDirFile = normalize(doc.getString("v_transPath"));
		String sep = InfoHelper.getConfig("视频系统", "上传文件根目录").startsWith("/") ? "/" : "\\" ;
		fileOperationParameter.setPath1(transDirFile.substring(0,transDirFile.lastIndexOf(sep)));
		System.out.println("-------------------------"+System.getProperty("file.separator")+"==================transDirFile："+transDirFile+"-----transDirFile"+transDirFile);
		// webservice创建目录
		try {
			vasPortType.doSystemFileOP(passWord,fileOperationParameter);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		// 音频编码
		in.setAudioCodec(AUDIOCODEC);
		// 音频码率
		in.setAudioBitrate(AUDIOBITRATE);
		// 音频采样
		in.setAudioSamplerate(AUDIOSAMPLERATE);
		
		// 视频编码
		in.setVideoCodec(VIDEOCODEC);
		// 视频码率
		in.setVideoBitrate(doc.getInt("v_bitrate"));
		// 视频帧率
		in.setFrameRate(VIDEOFRAMERATE);
		// 设置视频大小
		in.setVideoSize(doc.getString("v_resWidth") + "*" + doc.getString("v_resHeight"));
		
		String[] pubFileTypes =  InfoHelper.getConfig("视频系统", "转码文件后缀").split(",");
		
		for (int i = 0; i < pubFileTypes.length; i++) {
			//设置输出文件
			String outputFile =  transDirFile + "." + pubFileTypes[i];
			System.out.println("-------------------------==================outputFile："+outputFile);

			in.setOutputFile(outputFile);
			// 编码id
			IntHolder nTaskId = new IntHolder();
			// 结果 //<0表示错误(-1/-2/-3)
			IntHolder result2 = new IntHolder();
			// 创建转码任务
			try {
				vasPortType.createEncodeTask(passWord, in, nTaskId,result2);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			System.out.println("-------------------------==================result2："+result2.value);
			if(result2.value == 0){	
				if(taskIds.length() > 0){
					taskIds.append(",");
				}
				taskIds.append(nTaskId.value);
				// 开启编码任务
				try {
					vasPortType.startEncodeTask(passWord, nTaskId.value);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}		
		}

		//开始转码后，创建或者更新转码任务表
		saveTask(doc.getDocID(),taskIds.toString());
	}
	
	//创建或者更新转码任务表
	private void saveTask(long docID, String taskIds) throws E5Exception {
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        int taskLibID = LibHelper.getVideoTaskLibID();
        
        Document task = docManager.get(taskLibID, docID);
        if (task == null){
        	Document newTask = docManager.newDocument(taskLibID, docID);
        	newTask.set("v_taskID", taskIds);
        	newTask.set("v_process", 0);
        	docManager.save(newTask);
        } else {
        	task.set("v_taskID", taskIds);
        	task.set("v_process", 0);
        	docManager.save(task);
        }  
	}
	
	private String normalize(String path) {
		String uploadDisk = InfoHelper.getConfig("视频系统", "上传文件根目录");
		if(!uploadDisk.startsWith("/")) {
			path = FilenameUtils.separatorsToWindows(path);
		}else {
			path = FilenameUtils.separatorsToUnix(path);
		}
		return path;
	}

	/**
	 * 创建目录
	 * 
	 * @param path
	 */
	public void createFolder(String path, VJVASPortType type)  throws Exception{
	   	//纳加视频服务器密码
    	String passWord = InfoHelper.getConfig("视频系统", "服务器密码");
    	
		// 匹配目录和盘符
		Pattern fileNamePattern = Pattern.compile("[^:^\\\\]*");
		java.util.regex.Matcher fileNameMatcher = fileNamePattern.matcher(path);

		List<String> fileList = new ArrayList<String>();

		// 当前目录
		String curFolder = null;

		/*
		 * 获取文件目录层级
		 */
		while (fileNameMatcher.find()) {

			String fileName = fileNameMatcher.group();
			if (curFolder == null && !"".equals(fileName)) {
				curFolder = fileName + ":\\";
				fileList.add(curFolder);
			} else if (curFolder != null && !"".equals(fileName)) {
				curFolder += (fileName + "\\");
				fileList.add(curFolder);
			}

		}

		String fileRoot = fileList.get(0);

		try {
			SystemDiskInfoHolder sdi = new SystemDiskInfoHolder();
			IntHolder result = new IntHolder();
			type.getSystemDiskInfo(passWord, fileRoot, sdi, result);

			if (result.value < 0) {
				throw new RuntimeException(fileRoot + "盘符不存在......");
			}

			DoSystemFileOPIn in = new DoSystemFileOPIn();

			for (int i = 1; i < fileList.size(); i++) {
				String childFolder = fileList.get(i);

				type.getSystemDiskInfo(passWord, childFolder, sdi, result);

				if (result.value < 0) {
					in.setOperation(1);
					in.setPath1(childFolder);
					type.doSystemFileOP(passWord, in);
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} 
	}

	//从视频文件中抽图出来
	public void snapMediaPicture(HttpServletRequest request, Document doc) throws RemoteException, E5Exception {
		//图片存储设备的名称
        StorageDevice device = InfoHelper.getPicDevice();
        
        String savePath = snapMediaPicture(request, doc.getString("v_path"), device);
        
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        String picPath = device.getDeviceName() + ";" + savePath;
        doc.set("v_picPath", picPath);
        docManager.save(doc);
        
		//加抽图任务
		InfoHelper.prepare4Extract(device, savePath);
	}
	
	/** 抽关键帧 */
	public String snapMediaPicture(HttpServletRequest request, String videoPath, StorageDevice device) throws RemoteException, E5Exception {
		String passWord = InfoHelper.getConfig("视频系统", "服务器密码");
		//获取视频管理服务
		VJVASPortType vasPortType = getVasMisService();
		
		// 创建抓图
		ByteArrayHolder pic = new ByteArrayHolder();
		// 抓图状态
		IntHolder mediaResult = new IntHolder();
		
		NsSnapMediaFilePicIn mediaParameter = new NsSnapMediaFilePicIn();
		// 设置抓图文件路径
		mediaParameter.setPath(videoPath);
		// 设置可以返回pic数据
		mediaParameter.setBReturnPicData(true);
		mediaParameter.setPicPath("");
		mediaParameter.setPosition("");
		mediaParameter.setSize("");

		vasPortType.snapMediaFilePicture(passWord, mediaParameter, pic, mediaResult);
		
        //构造存储的路径和文件名，目录为201505/13/，文件名用uuid
        String savePath = InfoHelper.getPicSavePath(request) + ".jpg";
        
		//开始存储到存储设备上
        if (device == null) device = InfoHelper.getPicDevice();
		StorageDeviceManager sdManager = SysFactory.getStorageDeviceManager();
        InputStream is = new ByteArrayInputStream(pic.value); 
        try {
        	sdManager.write(device, savePath, is);
        } finally {
        	ResourceMgr.closeQuietly(is);
        }
        
		return savePath;
	}
	
	public String getFormateTime(String time) {
		Pattern p = Pattern.compile("^\\d{2}:\\d{2}:\\d{2}$");
		Matcher matcher = p.matcher(time);
		if (!matcher.matches()) {
			int intHour = Integer.valueOf(time) / 3600;
			String hour = intHour > 0 ? (intHour >= 10 ? String.valueOf(intHour)
					: "0" + intHour) : "00";
			int intMinute = Integer.valueOf(time) / 60 % 60;
			String minute = intMinute > 0 ? (intMinute >= 10 ? String
					.valueOf(intMinute) : "0" + intMinute) : "00";
			int intSecond = Integer.valueOf(time) % 60;
			return hour + ":" + minute + ":"
					+ (intSecond >= 10 ? intSecond : ("0" + intSecond));
		} else {
			return time;
		}
	}

	//若上传了截图，则保存后，把这个文件复制到图片存储设备（原来是附件存储设备）
	public void copyPicture(HttpServletRequest request, Document doc) throws E5Exception {
		//开始存储到存储设备上
		StorageDeviceManager sdManager = SysFactory.getStorageDeviceManager();
		//图片存储设备的名称
        StorageDevice device = InfoHelper.getPicDevice();
        
        //取出附件存储的图片路径
        String srcPicPath = doc.getString("v_picPath");  
        //构造存储的路径和文件名，目录为201505/13/，文件名用uuid
        String savePath = InfoHelper.getPicSavePath(request) + srcPicPath.substring(srcPicPath.lastIndexOf("."));
        
		//附件存储设备的名称
        StorageDevice attDevice = InfoHelper.getAttachDevice();
        
        InputStream is = sdManager.read(attDevice, srcPicPath.substring(srcPicPath.lastIndexOf(";") + 1)); 
        try {
        	sdManager.write(device, savePath, is);
        } finally {
        	ResourceMgr.closeQuietly(is);
        }
        
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        String picPath = device.getDeviceName() + ";" + savePath;
        doc.set("v_picPath", picPath);
        docManager.save(doc);
		//加抽图任务
		InfoHelper.prepare4Extract(device, savePath);
	}
}