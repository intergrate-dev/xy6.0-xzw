package com.founder.xy.api.nis;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.rpc.holders.ByteArrayHolder;
import javax.xml.rpc.holders.IntHolder;

import VJVAS.DoSystemFileOPIn;
import VJVAS.EncodeTaskParams;
import VJVAS.NsSnapMediaFilePicIn;
import com.founder.e5.commons.DateUtils;
import org.apache.commons.io.FilenameUtils;

import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.doc.util.E5docHelper;
import com.founder.e5.dom.DocLib;
import com.founder.e5.sys.StorageDevice;
import com.founder.e5.sys.StorageDeviceManager;
import com.founder.e5.sys.SysFactory;
import com.founder.e5.workspace.ProcHelper;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.redis.RedisManager;
import com.founder.xy.set.web.SensitiveWordControllerHelper;
import com.founder.xy.video.VideoManager;

import VJVAS.MediaFileInfo;
import VJVAS.holders.MediaFileInfoHolder;
import localhost.VJVASPortType;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 与外网api通讯的互动相关Api的基类
 */
@SuppressWarnings("unchecked")
public abstract class BaseApiManager {

	
	protected static final int LIST_COUNT = 20; //固定列表个数，避免恶意调用冲了redis中的缓存数据

	/**斜杠根据操作系统自适应*/
	protected static final String Slash = System.getProperty("file.separator");
	/**
	 * 图片和视频作为帖子的附件，存入互动附件表。
	 */
	public String setImgVioUrl(JSONObject obj, long articleID, int articleLibID) throws E5Exception {
		List<String> imgUrl = (List<String>)obj.get("imgUrl"); // 图片url
		JSONArray pics = savePics(imgUrl, articleID, articleLibID);
		JSONArray videos = saveVideos(obj, articleID, articleLibID);
		
		JSONObject result = new JSONObject();
		result.put("pics", pics);
		result.put("videos", videos);
		return result.toString();
	}

	protected JSONArray savePics(List<String> imgUrl, long articleID, int articleLibID) throws E5Exception {
		JSONArray pics = new JSONArray();

		if (imgUrl == null || imgUrl.size() == 0)
			return pics;
		
		StorageDevice device = InfoHelper.getDevice("互动", "手机图片存储设备");
		if (device == null || StringUtils.isBlank(device.getHttpDeviceURL())) {
			throw new E5Exception("请检查是否配置了手机图片存储设备，以及它的http访问路径！");
		}

		String deviceName = device.getDeviceName();
		int webRootLen = device.getHttpDeviceURL().length();

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		for (String url : imgUrl) {
			long DocID = InfoHelper.getNextDocID(DocTypes.NISATTACHMENT.typeID());
			int docLibID = LibHelper.getNisattachment();
			Document doc = docManager.newDocument(docLibID, DocID);
			ProcHelper.initDoc(doc);

			//在存储设备上的存放地址
			//把url(http://xxxxxxx/xy/201509/20/xxxxx.jpg)的头去掉，只留下相对路径
			String path = deviceName + ";" + url.substring(webRootLen + 1);

			doc.set("att_articleID", articleID);
			doc.set("att_articleLibID", articleLibID);
			doc.set("att_type", 1);		//附件类型
			doc.set("att_url", url); 	//发布地址
			doc.set("att_path", path); 	//存储地址

			docManager.save(doc);

			pics.add(path);
		}
		return pics;
	}
	
	/**
	 * 图片和视频作为帖子的附件，存入互动附件表。
	 */
	protected JSONArray saveVideos(JSONObject obj, long articleID, int articleLibID) throws E5Exception {
		JSONArray videos = new JSONArray();
		
		List<String> videoUrl = (List<String>)obj.get("videoUrl"); // 视频url
		if (videoUrl == null || videoUrl.size() == 0)
			return videos;
		
		StorageDevice device = InfoHelper.getDevice("互动", "手机视频存储设备");
		if (device == null || StringUtils.isBlank(device.getHttpDeviceURL())) {
			throw new E5Exception("请检查是否配置了手机视频存储设备，以及它的http访问路径！");
		}
	
		String deviceName = device.getDeviceName();
		int webRootLen = device.getHttpDeviceURL().length();
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		for (String url : videoUrl) {
			long DocID = InfoHelper.getNextDocID(DocTypes.NISATTACHMENT.typeID());
			int docLibID = LibHelper.getNisattachment();
			Document doc = docManager.newDocument(docLibID, DocID);
			ProcHelper.initDoc(doc);
	
			//在存储设备上的存放地址
			//把url(http://xxxxxxx/xy/201509/20/xxxxx.jpg)的头去掉，只留下相对路径
			String relativePath = url.substring(webRootLen + 1);
			String path = deviceName + ";" + relativePath;
			int duration = getVideoDuration(device, relativePath) ;
			
			doc.set("att_articleID", articleID);
			doc.set("att_articleLibID", articleLibID);
			doc.set("att_type", 2); // 附件类型
			doc.set("att_url", url); // 发布地址
			doc.set("att_path", path); 	//存储地址
			doc.set("att_path", path); 	//存储地址
			doc.set("att_duration", duration); 	//视频时长
			docManager.save(doc);

			JSONObject video = new JSONObject();
			video.put("urlApp", url);
			video.put("url", url);
			video.put("duration", duration);
			videos.add(video);
		}
		return videos;
	}
	
	protected String setImgUrl(String str, long articleID, int articleLibID) throws E5Exception {
		JSONArray jsonArr = JSONArray.fromObject(str);
		List<String> imgUrl = (List<String>) JSONArray.toCollection(jsonArr, String.class);// 图片url
		
		JSONArray pics = savePics(imgUrl, articleID, articleLibID);
		JSONArray videos = new JSONArray();
		
		JSONObject result = new JSONObject();
		result.put("pics", pics);
		result.put("videos", videos);
		return result.toString();
	}

	/**
	 * 取视频时长
	 */
	protected int getVideoDuration(StorageDevice device, String relativePath){
		StorageDeviceManager sdManager = SysFactory.getStorageDeviceManager();
		StorageDevice device_vio = InfoHelper.getVideoDevice();
		String target = "nis-video/" + relativePath ;
		String uploadDisk = InfoHelper.getConfig("视频系统", "上传文件根目录");
		String dstDir = uploadDisk + "/" + target ;
		
		VideoManager videoManager = (VideoManager) Context.getBean("videoManager");
		VJVASPortType vasPortType = null;
		String passWord = InfoHelper.getConfig("视频系统", "服务器密码");
		
		IntHolder mfiResult = new IntHolder();
		MediaFileInfoHolder mfi = new MediaFileInfoHolder();
		
		vasPortType = videoManager.getVasMisService();
		try {
			InputStream fis = sdManager.read(device, relativePath) ;
			sdManager.write(device_vio, target, fis);
			
			vasPortType.getMediaFileInfo(passWord, dstDir, mfi, mfiResult);
			if (mfiResult.value == 0) {
				MediaFileInfo mediaInfo = mfi.value;
				return mediaInfo.getDuration() ;
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (E5Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	//视频转码
	protected void transCode(Document doc) throws E5Exception {
		String uploadDisk = InfoHelper.getConfig("视频系统", "上传文件根目录");
		String transDisk = InfoHelper.getConfig("视频系统", "转码文件根目录");

		String fileDir = doc.getString("att_path").substring(doc.getString("att_path").lastIndexOf(";")+1);

		String path =normalize(uploadDisk + Slash  + "nis-video" + Slash + fileDir);
		//由于转码文件生成的比较多
		String transPath =normalize( transDisk + Slash + "nis-video" + Slash
				+ "trans" + Slash + fileDir.substring(0,fileDir.lastIndexOf(".")));

		String passWord = InfoHelper.getConfig("视频系统", "服务器密码");
		//获取纳加视频管理服务
		VideoManager videoManager = (VideoManager) Context.getBean("videoManager");
		VJVASPortType vasPortType = videoManager.getVasMisService();
		//转码任务的IDs
		StringBuffer taskIds = new StringBuffer();
		//编码参数
		EncodeTaskParams in = new EncodeTaskParams();
		in.setInputFile(path);
		// 文件操作参数
		DoSystemFileOPIn fileOperationParameter = new DoSystemFileOPIn();
		// 不覆盖
		fileOperationParameter.setBOverwrite(false);
		// 创建目录
		fileOperationParameter.setOperation(1);
		String sep = InfoHelper.getConfig("视频系统", "上传文件根目录").startsWith("/") ? "/" : "\\" ;
		fileOperationParameter.setPath1(transPath.substring(0,transPath.lastIndexOf(sep)));
		System.out.println("-------------------------"+System.getProperty("file.separator")+"==================transPath："+transPath+"-----transPath"+transPath);
		// webservice创建目录
		try {
			vasPortType.doSystemFileOP(passWord,fileOperationParameter);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		String videoSize = InfoHelper.getConfig("视频系统", "默认分辨率");
		String bitrate = InfoHelper.getConfig("视频系统", "默认码率");
//		String format = InfoHelper.getConfig("视频系统", "转码文件后缀");

		// 音频编码
		in.setAudioCodec(videoManager.AUDIOCODEC);
		// 音频码率
		in.setAudioBitrate(videoManager.AUDIOBITRATE);
		// 音频采样
		in.setAudioSamplerate(videoManager.AUDIOSAMPLERATE);

		// 视频编码
		in.setVideoCodec(videoManager.VIDEOCODEC);
		// 视频码率
		in.setVideoBitrate(Integer.parseInt(bitrate));
		// 视频帧率
		in.setFrameRate(videoManager.VIDEOFRAMERATE);
		// 设置视频大小
		in.setVideoSize(videoSize);

//		String[] pubFileTypes = format.split(",");
		//目前只转成mp4
		String[] pubFileTypes = {"mp4"};

		for (int i = 0; i < pubFileTypes.length; i++) {
			//设置输出文件
			String outputFile =  transPath + "." + pubFileTypes[i];
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


	private String normalize(String path) {
		String uploadDisk = InfoHelper.getConfig("视频系统", "上传文件根目录");
		if(!uploadDisk.startsWith("/")) {
			path = FilenameUtils.separatorsToWindows(path);
		}else {
			path = FilenameUtils.separatorsToUnix(path);
		}
		return path;
	}

	//创建或者更新转码任务表
	private void saveTask(long docID, String taskIds) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		int taskLibID = LibHelper.getNisTaskLibID();

		Document task = docManager.get(taskLibID, docID);
		System.out.println("-----------开始更新或者创建转码任务-----------------------");
		if (task == null){
			Document newTask = docManager.newDocument(taskLibID, docID);
			newTask.set("v_taskID", taskIds);
			newTask.set("v_process", 0);
			docManager.save(newTask);
			System.out.println("----------创建完成-----------------------");
		} else {
			task.set("v_taskID", taskIds);
			task.set("v_process", 0);
			docManager.save(task);
			System.out.println("----------更新完成-----------------------");
		}
	}

	//从视频文件中抽图出来
	public void snapMediaPicture(Document doc) throws RemoteException, E5Exception {
		//手机图片存储设备的名称
		StorageDevice device = InfoHelper.getDevice("互动", "手机图片存储设备");

		String uploadDisk = InfoHelper.getConfig("视频系统", "上传文件根目录");

		String fileDir = doc.getString("att_path").substring(doc.getString("att_path").lastIndexOf(";")+1);

		String path = uploadDisk + Slash + "nis-video" + Slash + fileDir;

		String savePath = snapMediaPicture(path, device);

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		String picPath = device.getDeviceName() + ";" + savePath;
		String picUrl = device.getHttpDeviceURL() + "/" + savePath;
		doc.set("att_picPath", picPath);
		doc.set("att_picUrl", picUrl);
		doc.set("att_picUrlPad", picUrl);
		docManager.save(doc);

		//加抽图任务
		InfoHelper.prepare4Extract(device, savePath);
	}
	/** 抽关键帧 */
	public String snapMediaPicture(String videoPath, StorageDevice device) throws RemoteException, E5Exception {
		String passWord = InfoHelper.getConfig("视频系统", "服务器密码");
		//获取视频管理服务
		VideoManager videoManager = (VideoManager) Context.getBean("videoManager");
		VJVASPortType vasPortType = videoManager.getVasMisService();

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
		String savePath = DateUtils.format("yyyyMM/dd/") + UUID.randomUUID().toString().replaceAll("\\\\", "/") + ".jpg";

		//开始存储到存储设备上
		if (device == null) device = InfoHelper.getDevice("互动", "手机图片存储设备");
		StorageDeviceManager sdManager = SysFactory.getStorageDeviceManager();
		InputStream is = new ByteArrayInputStream(pic.value);
		try {
			sdManager.write(device, savePath, is);
		} finally {
			ResourceMgr.closeQuietly(is);
		}

		return savePath;
	}
	/**
	 * 提取出相同代码
	 */
	protected Document setCommonField(JSONObject obj, DocumentManager docManager,
			long docID, int docLibID, String type) throws E5Exception {
		Document doc = docManager.newDocument(docLibID, docID);
		ProcHelper.initDoc(doc);
		doc.set("a_siteID", obj.getInt("siteID"));
		String content = StringUtils.getNotNull(obj.getString("content"));
		doc.set("a_content", content);
		doc.set("a_longitude", obj.getDouble("longitude"));
		doc.set("a_latitude", obj.getDouble("latitude"));
		doc.set("a_location", StringUtils.getNotNull(obj.getString("location")));
		
		if (obj.getString("phone") != null) {
			//活动的评论报名：存活动的评论报名的手机号，项目中用。产品中已废弃这种形式。
			doc.set("a_location", StringUtils.getNotNull(obj.getString("phone")));
		}
		doc.set("SYS_AUTHORID", obj.getLong("userID"));
		String userName = StringUtils.getNotNull(obj.getString("userName")); // 用户名
		if(null == userName || "".equals(userName)){
			doc.set("SYS_AUTHORS", StringUtils.getNotNull(obj.getString("userOtherID")));
		} else {
			doc.set("SYS_AUTHORS", userName);
		}
		// 检查敏感词
		checkSensitive(type, doc);
		
		return doc;
	}

	/**
	 * 论坛、评论、直播提交时，
	 * 2）调用写稿中的检查敏感词逻辑（敏感词服务器地址和端口也使用写稿服务的）
	 * 3）若含，则设置a_isSensitive=1
	 */
	protected void checkSensitive(String type, Document doc){
		if (type.matches("discuss|live|forum")) {
			if (InfoHelper.sensitiveInNis()) {
				String content = doc.getString("a_content");
				
				/*JSONObject resultJson = sensitiveControl.getSensiWord(content);
				boolean hasSensitive = (resultJson != null) && "yes".equals(resultJson.getString("status"));*/

				String jsonStr = SensitiveWordControllerHelper.sensitive("checkSensitive", "1", null, content);
				//判断字符串是否为空
				if (!StringUtils.isBlank(jsonStr) ) {
					JSONObject json = JSONObject.fromObject(jsonStr);
					//判断是否有敏感词
					if (json.has("code")) {
						int code = json.getInt("code");
						//有
						if (code == 1) {
							//处理内容 - 把敏感词加上标签
							content = handleContentWithTag(content, json, 1, "sensitiveWord");
							content = handleContentWithTag(content, json, 2, "illegalWord");
							if (json.has("type")) {
								doc.set("a_isSensitive", json.getInt("type"));
								doc.set("a_sensitiveContent", content);
							}
						} else {
							doc.set("a_isSensitive", 0);
						}
					}
				}
			}
		}
	}

	protected void checkSensitiveDelay(String type, JSONObject obj){
		if (type.matches("discuss|live|forum")) {
			if (InfoHelper.sensitiveInNis()) {
				String content = obj.getString("content");
				
				/*JSONObject resultJson = sensitiveControl.getSensiWord(content);
				boolean hasSensitive = (resultJson != null) && "yes".equals(resultJson.getString("status"));
				
				if (hasSensitive)
					obj.put("a_isSensitive", 1);*/

                String jsonStr = SensitiveWordControllerHelper.sensitive("checkSensitive", "1", null, content);
                //判断字符串是否为空
                if (!StringUtils.isBlank(jsonStr) ) {
                    JSONObject json = JSONObject.fromObject(jsonStr);
                    //判断是否有敏感词
                    if (json.has("code")) {
                        int code = json.getInt("code");
                        //有
                        if (code == 1) {
                            //处理内容 - 把敏感词加上标签
                            content = handleContentWithTag(content, json, 1, "sensitiveWord" );
                            content = handleContentWithTag(content, json, 2, "illegalWord");
                            if (json.has("type")) {
                                obj.put("a_isSensitive", json.getInt("type"));
                                obj.put("a_sensitiveContent", content);
                            }

                        }else {
							obj.put("a_isSensitive", 0);
						}
                    }
                }
			}else {
				obj.put("a_isSensitive", 0);
			}
		}
	}
	/**
	 * 取互动的附件
	 */
	protected JSONArray jsonAttachments(int articleLibID, long articleID) throws E5Exception {
		int attLibID = LibHelper.getLibIDByOtherLib(DocTypes.NISATTACHMENT.typeID(), articleLibID);
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] docs = docManager.find(attLibID, "att_articleID=? and att_articleLibID=?",
				new Object[]{articleID, articleLibID});
		JSONArray inJsonArr = new JSONArray();
		for (Document doc : docs) {
			int type = doc.getInt("att_type");
			JSONObject _inJson = new JSONObject();
			_inJson.put("type", type);
			_inJson.put("url", StringUtils.getNotNull(doc.getString("att_url")));
			if (type == 2) { //视频时长
				_inJson.put("duration", doc.getInt("att_duration"));
			}

			inJsonArr.add(_inJson);
		}
		return inJsonArr;
	}
	
	/**
	 * 读整数，<0时返回0
	 */
	protected int getInt(IResultSet rs, String field) {
		int value = 0;
		try {
			value = rs.getInt(field);
			if (value <  -100) value = 0;
		} catch (Exception e) {
		}
		return value;
	}
	
	/**
	 * 读整数，<0时返回0
	 */
	protected int getInt(Document rs, String field) {
		int value = 0;
		try {
			value = rs.getInt(field);
			if (value <  -100) value = 0;
		} catch (Exception e) {
		}
		return value;
	}
	
	/**
	 * 读long，<-1时返回0（官方回复时userid为-1）
	 */
	protected long getLong(IResultSet rs, String field) {
		long value = 0;
		try {
			value = rs.getLong(field);
			if (value < -100 ) value = 0;
		} catch (SQLException e) {
		}
		return value;
	}
	
	/**
	 * 读long，<-1时返回0（官方回复时userid为-1）
	 */
	protected long getLong(Document rs, String field) {
		long value = 0;
		try {
			value = rs.getLong(field);
			if (value <  -100) value = 0;
		} catch (Exception e) {
		}
		return value;
	}

	/**
	 * 从一个JSONArray中取前几个数据
	 * @param list JSONArray的字符串形式，或者{list:[]}格式的字符串
	 * @param count 指定要取的个数
	 * @return JSONArray
	 */
	protected JSONArray getSomeFromList(String list, int count) {
		//读列表的前几个做为结果
        JSONArray jsonNewArr = new JSONArray();
		try {
			if (list != null && !"".equals(list)) {
				JSONArray jsonArr = getJSONArray(list);
			    count = count > jsonArr.size() ? jsonArr.size() : count;
			    for (int i = 0; i < count; i++) {
			        jsonNewArr.add(jsonArr.get(i));
			    }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonNewArr;
	}

	/**
	 * 从Redis的list中取前几个数据
	 * @param count 指定要取的个数
	 */
	protected JSONArray getSomeFromRedisList(String key, int count) {
    	//list的最后一个元素是记录位置索引的，所以要加判断
    	long len = RedisManager.llen(key);
    	if (count >= len) count = (int)len - 1;
		
		JSONArray result = new JSONArray();
		if(count > 0){
			List<String> list = RedisManager.lrange(key, 0, count - 1);
			result.addAll(list);
		}
		return result;
	}
	
	protected List<Long> queryOneField(DocLib docLib, String sql, Object[] params, int start, int count) {
		sql = getLimitSQL(docLib.getDocLibID(), sql, start, count);
        System.out.println(sql);
		
		DBSession conn = null;
		IResultSet rs = null;
		List<Long> ids = new ArrayList<>();
		try {
			conn = Context.getDBSession(docLib.getDsID());
			rs = conn.executeQuery(sql, params);
			while (rs.next()) {
				ids.add(rs.getLong(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}
		
		return ids;
	}
	
	protected String getLimitSQL(int docLibID, String sql, int start, int count) {
		DBSession conn = null;
		try {
			conn = E5docHelper.getDBSession(docLibID);
			return conn.getDialect().getLimitString(sql, start, count);
		} catch (E5Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
	}

	// 取出标题图片url，写入稿件json，从标题图大到小
	/**
	 * 从附件的JSONArray中读出imgUrl，用于列表api中使用
	 * @param atts
	 * @return
	 */
	protected String getImgUrlFromAtts(JSONArray atts) {
		if (atts == null || atts.isEmpty()) return "";
		
		String imgUrl = null;
		
		imgUrl = getAttUrlByType(atts, 3); // 标题图片-大 ，用抽图后的
		if (imgUrl != null)
			return imgUrl + ".0";
		
		if (imgUrl == null)
			imgUrl = getAttUrlByType(atts, 4); // 标题图片-中
		if (imgUrl == null)
			imgUrl = getAttUrlByType(atts, 5); // 标题图片-小
		if (imgUrl == null)
			imgUrl = getAttUrlByType(atts, 1); // 普通图片
		if (imgUrl == null)
			imgUrl = "";
		
		return imgUrl;
	}

	// 取出标题图片url，写入稿件json，从标题图大到小
	/**
	 * 从附件的JSONArray中读出imgUrl，用于列表api中使用
	 * @param atts
	 * @return
	 */
	protected JSONArray getImgUrlsFromAtts(JSONArray atts) {
		JSONArray result = new JSONArray();
		if (atts == null || atts.isEmpty()) return result;
		
		for (Object att : atts) {
			JSONObject json = (JSONObject)att;
			result.add(json.getString("url"));
		}
		return result;
	}

	/**
	 * 从JSONArray中读出一个指定附件类型的url
	 * @param atts
	 * @param type
	 * @return
	 */
	protected String getAttUrlByType(JSONArray atts, int type) {
		for (Object att : atts) {
			JSONObject json = (JSONObject)att;
			if (json.getInt("type") == type)
				return json.getString("url");
		}
		
		return null;
	}

	protected JSONArray getJSONArray(String list) {
		try {
			return JSONArray.fromObject(list);
		} catch (Exception e) {
			JSONObject json = JSONObject.fromObject(list);
			return json.getJSONArray("list");
		}
	}
	protected String handleContentWithTag(String content, JSONObject json, int type, String key) {
		if(json.has(key)) {
			JSONObject sensitiveWord = json.getJSONObject(key);
			Object sen = sensitiveWord.get("keywords");
			//keywords 可能不是JSONArray 而是 String
			JSONArray senArray = null;
			if (sen instanceof JSONArray) {
				senArray = JSONArray.fromObject(sen);
			} else if( sen instanceof String){
				senArray = new JSONArray();
				senArray.add(sen);
			}
	
			boolean hasWord = false;
			for (Object object : senArray) {
				String senword = (String)object;
				content = content.replaceAll(senword, "<span class=\"" + key + "\">" + senword + "</span>");
				hasWord = true;
			}
			if (hasWord) {
				if (json.has("type")) {
					int _type = json.getInt("type");
					_type += type;
					json.put("type", _type);
				} else {
					json.put("type", type);
				}
			}
		}
	    return content;
	}
}
