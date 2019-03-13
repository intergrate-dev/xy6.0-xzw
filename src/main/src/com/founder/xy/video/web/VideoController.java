package com.founder.xy.video.web;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.rpc.ServiceException;
import javax.xml.rpc.holders.IntHolder;
import javax.xml.rpc.holders.StringHolder;

import org.apache.tools.ant.types.FlexInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.founder.e5.cat.CatManager;
import com.founder.e5.cat.CatReader;
import com.founder.e5.cat.Category;
import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.Pair;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.doc.util.E5docHelper;
import com.founder.e5.flow.FlowNode;
import com.founder.e5.flow.FlowReader;
import com.founder.e5.sys.StorageDevice;
import com.founder.e5.web.WebUtil;
import com.founder.e5.workspace.app.form.FormSaver;
import com.founder.xy.commons.CatTypes;
import com.founder.xy.commons.EncodeUtils;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.video.VideoManager;

import VJVAS.MediaFileInfo;
import VJVAS.UploadSession;
import VJVAS.UploadSessionFile;
import VJVAS.holders.ArrayOfuploadSessionFileHolder;
import VJVAS.holders.MediaFileInfoHolder;
import VJVAS.holders.UploadSessionHolder;
import localhost.VJVASPortType;
import localhost.VJVodServicePortType;
import net.sf.json.JSONObject;

/**
 * 视频的相关操作
 */
@Controller
@RequestMapping("/xy/video")
public class VideoController {
	@Autowired
	VideoManager videoManager;
	
	/**斜杠根据操作系统自适应*/
	public static final String Slash = System.getProperty("file.separator");

	@RequestMapping(value = "Add.do")
	public ModelAndView upload(
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		//纳加视频服务器密码
		String passWord = InfoHelper.getConfig("视频系统", "服务器密码");

		String uploadPort = InfoHelper.getConfig("视频系统", "上传服务地址");
		String uploadDisk = InfoHelper.getConfig("视频系统", "上传文件根目录");
		String transDisk = InfoHelper.getConfig("视频系统", "转码文件根目录");


		Boolean isAudio = WebUtil.getInt(request, "isAudio", 0) == 1;
		//获取参数向后传
		Map<String, Object> model = new HashMap<String, Object>();
		VJVASPortType vasPortType = videoManager.getVasMisService();

		String tenantCode = InfoHelper.getTenantCode(request);

		long uploadTime = System.currentTimeMillis();
		// 文件存在路径
		String fileDatePath = DateUtils.format("yyyyMM"+Slash+"dd"+Slash);

		String fileMd5Path = EncodeUtils.getMD5(String.valueOf(uploadTime + '|'
				                                                    + new Random().nextInt(1)));

		String dstDir = uploadDisk + tenantCode + Slash + "source" + Slash + fileDatePath + fileMd5Path;
		String transDir = transDisk + tenantCode + Slash + "trans" + Slash + fileDatePath + fileMd5Path;
		// 创建存放目录
		videoManager.createFolder(dstDir, vasPortType);
		// 创建转码生成文件目录
		videoManager.createFolder(transDir, vasPortType);
		// 创建上传会话
		int sessionID;
		if (isAudio) {
			sessionID = vasPortType.createUploadSession(passWord, transDir,
			                                            "", 60);
		} else {
			sessionID = vasPortType.createUploadSession(passWord, dstDir,
			                                            "", 60);
		}
		model.put("sessionID", sessionID);
		model.put("uploadPort", uploadPort);
		model.put("isAudio", isAudio);

		return new ModelAndView("/xy/video/VideoUpload", model);
	}

	@RequestMapping(value = "getInitParam.do")
	public void getInitParam(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String videoSize = InfoHelper.getConfig("视频系统", "默认分辨率");
		String resWidth = videoSize.substring(0, videoSize.lastIndexOf("*"));
		String resHeight = videoSize.substring(videoSize.lastIndexOf("*") + 1, videoSize.length());
		String bitrate = InfoHelper.getConfig("视频系统", "默认码率");
		String format = InfoHelper.getConfig("视频系统", "转码文件后缀");
        String VJ = InfoHelper.getConfig("视频系统", "是否经过纳加方式");
		
		
		
		JSONObject json = new JSONObject();
		json.put("resWidth", resWidth);
		json.put("resHeight", resHeight);
		json.put("bitrate", bitrate);
		json.put("format", format);
		json.put("VJ", VJ);//是否经过纳加方式

		InfoHelper.outputJson(json.toString(), response);
	}

	@RequestMapping(value = "getUploadFileInfo.do")
	public void getUploadFileInfo(
			HttpServletRequest request, HttpServletResponse response,
			@RequestParam("sessionID") int sessionID) throws RemoteException {
		//纳加视频服务器密码
		String passWord = InfoHelper.getConfig("视频系统", "服务器密码");

		VJVASPortType vasPortType = null;
		// 纳加点播管理操作
		vasPortType = videoManager.getVasMisService();

		String fileName = "";
		String path = "";
		String transPath = "";
		String time = "";
		String uploadDisk = InfoHelper.getConfig("视频系统", "上传文件根目录");
		String transDisk = InfoHelper.getConfig("视频系统", "转码文件根目录");
		String tenantCode = InfoHelper.getTenantCode(request);
		IntHolder usflResult = new IntHolder();
		// 上传文件列表
		ArrayOfuploadSessionFileHolder usfl = new ArrayOfuploadSessionFileHolder();
		// 调用上传会话文件列表
		vasPortType.getUploadSessionFiles(passWord, sessionID,
		                                  usfl, usflResult);

		if (usflResult.value == 0) {
			UploadSessionFile[] usflArr = usfl.value;
			if (usflArr.length > 0) {
				//暂且这样，因为默认上传视频文件为一个
				UploadSessionFile file = usflArr[0];
				fileName = file.getPath().substring(1, file.getPath().length());

				//计算出视频的上传文件路径以及生成的转码文件路径
				IntHolder ushResult = new IntHolder();
				UploadSessionHolder ush = new UploadSessionHolder();

				vasPortType.getUploadSession(passWord, sessionID, ush, ushResult);

				if (ushResult.value == 0) {
					UploadSession us = ush.value;
					String dstDir = us.getDstDir();
					path = dstDir + Slash + fileName;

					String rootDir = uploadDisk + tenantCode + Slash + "source" + Slash;
					String fileDir = dstDir.substring(rootDir.length(), dstDir.length());
					String transDir = transDisk + tenantCode + Slash + "trans" + Slash + fileDir;

					//由于转码文件生成的比较多
					transPath = transDir + Slash + fileName.substring(0, fileName.lastIndexOf("."));
					if (path.toLowerCase().endsWith("mp3")) {
						transPath = path.substring(0, fileName.lastIndexOf("."));
					}
				}

				//获取视频的播放时长
				IntHolder mfiResult = new IntHolder();
				MediaFileInfoHolder mfi = new MediaFileInfoHolder();

				vasPortType.getMediaFileInfo(passWord, path, mfi, mfiResult);
				if (mfiResult.value == 0) {
					MediaFileInfo mediaInfo = mfi.value;
					time = InfoHelper.formatTime(mediaInfo.getDuration());
				}
			}
		}

		//视频文件信息获取完毕后，将上传session关闭掉
		vasPortType.deleteUploadSession(passWord, sessionID);

		JSONObject json = new JSONObject();
		json.put("fileName", fileName);
		json.put("path", path);
		json.put("transPath", transPath);
		json.put("time", time);

		InfoHelper.outputJson(json.toString(), response);
	}

	/**
	 * 保存完毕后，进行转码
	 */
	@RequestMapping(value = "FormSave.do")
	public String formSave(HttpServletRequest request, HttpServletResponse response) throws Exception {
		FormSaver formSaver = (FormSaver) Context.getBean(FormSaver.class);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = null;
		Pair changed;
		String url = "/e5workspace/after.do?UUID=" + WebUtil.get(request, "UUID");
		String VJ = InfoHelper.getConfig("视频系统", "是否经过纳加方式");
		try {
			changed = formSaver.handleChanged(request);
		} catch (Exception e) {
			e.printStackTrace();
			url += "&Info=" + URLEncoder.encode("操作失败", "UTF-8");
			return "redirect:" + url;
		}
		
		String id = request.getParameter("DocIDs");	
		boolean isNew = (StringUtils.isBlank(id) || "0".equals(id));
		
		long docID = Long.parseLong(changed.getKey());
		int docLibID = LibHelper.getVideoLibID();


		if (isNew) {
			//如果是新上传的视频，则必须要经过转码阶段
			doc = docManager.get(docLibID, docID);
		} else {
			//如果是修改视频，码率、分辨率（宽高）和视频本身发生变化需要转码
			doc = docManager.get(docLibID, Long.parseLong(id));
		}
        
		//不经过纳加方式，视频直接设置为已转码
		if (VJ.equals("否")){
			doc.set("v_status", 1);
			//设为下一个流程节点
			FlowReader flowReader = (FlowReader)Context.getBean(FlowReader.class);
			FlowNode nextNode = flowReader.getNextFlowNode(doc.getCurrentNode());
			if (nextNode != null) {
				doc.setCurrentNode(nextNode.getID());
				doc.setCurrentStatus(nextNode.getWaitingStatus());
			}
			docManager.save(doc);
			if (!doc.getString("v_picPath").isEmpty()) {
				videoManager.copyPicture(request, doc);
			}
			if (!StringUtils.isBlank(doc.getString("v_path"))){
				String v_path = doc.getString("v_path");
				String v_transPath = doc.getString("v_transPath");
				String rand = v_path.substring(v_path.substring(0, v_path.lastIndexOf("/")).lastIndexOf("/"), v_path.lastIndexOf("/"));
				String filename = v_path.substring(v_path.lastIndexOf("/"), v_path.lastIndexOf("."));
			    v_path = v_path.replace(filename, rand);
			    v_transPath = v_transPath.replace(filename, rand);
			    doc.set("v_path", v_path);
			    doc.set("v_transPath",v_transPath );
			    docManager.save(doc);
			}
		} 
		else{
			//无法判断修改的时候是否重新上传视频，所以不怕麻烦再来一次转码
			//判断格式 mp3不再转码，直接设置各属性值
			if (StringUtils.isBlank(doc.getString("v_path")) || !doc.getString("v_path").toLowerCase().endsWith(".mp3")) {
				videoManager.transCode(doc);
	
				//获取视频的播放时长
				IntHolder mfiResult = new IntHolder();
				MediaFileInfoHolder mfi = new MediaFileInfoHolder();
	
				//纳加视频服务器密码
				String passWord = InfoHelper.getConfig("视频系统", "服务器密码");
	
				VJVASPortType vasPortType = null;
				// 纳加点播管理操作
				vasPortType = videoManager.getVasMisService();
				vasPortType.getMediaFileInfo(passWord, doc.getString("v_path"), mfi, mfiResult);
				String time = null;
				if (mfiResult.value == 0) {
					MediaFileInfo mediaInfo = mfi.value;
					time = InfoHelper.formatTime(mediaInfo.getDuration());
	
				}
				doc.set("v_time", time);
				if (doc.getString("v_picPath").isEmpty()) {
					//判断有无截图
					videoManager.snapMediaPicture(request, doc);
				} else {
					videoManager.copyPicture(request, doc);
				}
			}
			else if (doc.getInt("v_status") == 0) {
				//音频直接改为已转码
				doc.set("v_status", 1);
				//设为下一个流程节点
				FlowReader flowReader = (FlowReader)Context.getBean(FlowReader.class);
				FlowNode nextNode = flowReader.getNextFlowNode(doc.getCurrentNode());
				if (nextNode != null) {
					doc.setCurrentNode(nextNode.getID());
					doc.setCurrentStatus(nextNode.getWaitingStatus());
				}
				docManager.save(doc);
			}
			
		}
		url += "&DocIDs=" + docID; 
		return "redirect:" + url;
	}

	@RequestMapping(value = "Publish.do")
	public String publish(
			HttpServletRequest request,
			HttpServletResponse response) throws MalformedURLException, ServiceException, E5Exception, UnsupportedEncodingException {
		//纳加视频服务器密码
		String passWord = InfoHelper.getConfig("视频系统", "服务器密码");
		String playerUrl = InfoHelper.getConfig("视频系统", "视频播放地址");
		String transDisk = InfoHelper.getConfig("视频系统", "转码文件根目录");
		String mappedDisk = InfoHelper.getConfig("视频系统", "点播映射磁盘");
		String VJ = InfoHelper.getConfig("视频系统", "是否经过纳加方式");

		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		String strDocIDs = WebUtil.get(request, "DocIDs");
		long[] docIDs = StringUtils.getLongArray(strDocIDs);
		DocumentManager docManager = DocumentManagerFactory.getInstance();

		VJVodServicePortType vodPortType = videoManager.getVodService();
		boolean bSuccess = true;
		Pattern hashPattern = Pattern.compile("^hash=\\s*([^&]*)");

		List<Document> videos = new ArrayList<Document>();
		for (long docID : docIDs) {
			Document video = docManager.get(docLibID, docID);
			String transDirFile = video.getString("v_transPath");//视频转码地址
			String format = video.getString("v_format");//视频格式
			String[] pubFileTypes = format.split(",");
            
			//不经过纳加方式，视频直接设置为已发布
			if (VJ.equals("否")){
//				StorageDevice device = InfoHelper.getVideoDevice();
//				String v_path = video.getString("v_path");
//				String httpurl = device.getHttpDeviceURL();
//				String ftpurl = device.getFtpDeviceURL();
//				v_path = v_path.replace(ftpurl,httpurl);
//
//				String rand = v_path.substring(v_path.substring(0, v_path.lastIndexOf("/")).lastIndexOf("/"), v_path.lastIndexOf("/"));
//			    String filename = v_path.substring(v_path.lastIndexOf("/"), v_path.lastIndexOf("."));
//			    v_path = v_path.replace(filename, rand);
//				video.set("v_url", v_path );
//				video.set("v_urlApp", v_path);
				
			}else{
			for (int i = 0; i < pubFileTypes.length; i++) {
				StringHolder hash = new StringHolder();
				String path = transDirFile + "." + pubFileTypes[i];
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
					}
				}

				if (publishStatusResult.value == 0 || publishStatusResult.value == -32) {
					Matcher ma = hashPattern.matcher(hash.value);
					String hashCode = "";
					if (ma.find()) {
						hashCode = ma.group(1);
					}
					String player = playerUrl + hashCode;
					if ("flv".equals(pubFileTypes[i])) {
						video.set("v_url", player + ".flv");
						video.set("v_hashcode", hashCode);
					} else if ("mp3".equals(pubFileTypes[i])) {
						video.set("v_url", player + ".mp3");
						video.set("v_urlApp", player + ".mp3");
						video.set("v_hashcodeApp", hashCode);
						video.set("v_hashcode", hashCode);
					} else {
						video.set("v_urlApp", player + ".mp4");
						video.set("v_hashcodeApp", hashCode);
					}
				} else {
					System.out.println("发布失败,返回值：" + publishStatusResult.value);
					bSuccess = false;
					break;
				}
			}
			}
			video.set("v_status", 2);
			videos.add(video);
		}

		String error = save(docLibID, videos);
		//调用after.do进行后处理：改变流程状态、解锁、刷新列表
		String url = "/e5workspace/after.do?UUID=" + WebUtil.get(request, "UUID");
		if (bSuccess && error == null) {
			url += "&DocIDs=" + request.getParameter("DocIDs") + "&Info=" + URLEncoder.encode("发布成功", "UTF-8"); //操作成功
		} else {
			url += "&Info=" + URLEncoder.encode("发布失败", "UTF-8");
		}

		return "redirect:" + url;
	}

	/**
	 * 视频取消发布
	 * 
	 * 
	 */
	@RequestMapping(value = "Revoke.do")
	public String revoke(
			HttpServletRequest request,
			HttpServletResponse response) throws MalformedURLException, ServiceException, E5Exception, UnsupportedEncodingException {
		String passWord = InfoHelper.getConfig("视频系统", "服务器密码");
		String transDisk = InfoHelper.getConfig("视频系统", "转码文件根目录");
		String mappedDisk = InfoHelper.getConfig("视频系统", "点播映射磁盘");

		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		String strDocIDs = WebUtil.get(request, "DocIDs");
		long[] docIDs = StringUtils.getLongArray(strDocIDs);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
        String VJ = InfoHelper.getConfig("视频系统", "是否经过纳加方式" );
		VJVodServicePortType vodPortType = videoManager.getVodService();

		boolean bSuccess = true;

		List<Document> videos = new ArrayList<Document>();
		for (long docID : docIDs) {
			Document video = docManager.get(docLibID, docID);
			String transDirFile = video.getString("v_transPath");
			String format = video.getString("v_format");
			String[] pubFileTypes = format.split(",");
            if(!VJ.equals("否")){
			for (int i = 0; i < pubFileTypes.length; i++) {
				StringHolder hash = new StringHolder();
				String path = transDirFile + "." + pubFileTypes[i];
				//需要对映射文件路径进行转换
				String filePath = "";
				if (mappedDisk != null && !mappedDisk.isEmpty()) {
					filePath = path.replaceFirst(Matcher.quoteReplacement(transDisk),
					                             Matcher.quoteReplacement(mappedDisk));
				} else {
					filePath = path;
				}
				// 取消发布状态返回值
				IntHolder unpublishStatusResult = new IntHolder();
				// 取消发布

				boolean flag = true;
				for (int x = 0, len = 3; x < len && flag; x++) {
					if (flag) {
						// 取消发布
						try {
							vodPortType.unpublishFile(passWord, filePath, hash,
							                          unpublishStatusResult);
							flag = false;
						} catch (RemoteException e) {
							flag = true;
							e.printStackTrace();
						} finally {

						}
					}
				}

				if (unpublishStatusResult.value != 0 && unpublishStatusResult.value != -35) {
					System.out.println("取消发布失败,返回值：" + unpublishStatusResult.value);
					bSuccess = false;
					break;
				}
			}
			video.set("v_status", 1);
			videos.add(video);
			}
            else{
    		video.set("v_status", 1);
    		videos.add(video);
            }
		}

		String error = save(docLibID, videos);
		//调用after.do进行后处理：改变流程状态、解锁、刷新列表
		String url = "/e5workspace/after.do?UUID=" + WebUtil.get(request, "UUID");
		if (bSuccess && error == null) {
			url += "&DocIDs=" + request.getParameter("DocIDs") + "&Info=" + URLEncoder.encode("取消发布成功", "UTF-8"); //操作成功
		} else {
			url += "&Info=" + URLEncoder.encode("取消发布失败", "UTF-8");
		}

		return "redirect:" + url;
	}

	
	
	/**
	 * 多个视频的统一提交， 出错时返回错误信息
	 */
	private String save(int docLibID, List<Document> videos) {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		//同时修改多个视频，使用事务
		DBSession conn = null;
		try {
			conn = E5docHelper.getDBSession(docLibID);
			conn.beginTransaction();

			for (Document video : videos) {
				docManager.save(video, conn);
			}
			conn.commitTransaction();
			return null;
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			return "操作中出现错误：" + e.getLocalizedMessage();
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
	}

	/**
	 * 读出视频控件播放器的Url，用于视频细览
	 */
	@RequestMapping(value = "Player.do")
	public void player(
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (InfoHelper.getConfig("视频系统", "是否经过纳加方式").equals("否")){ 
			String player = InfoHelper.getConfig("视频系统", "视频播放控件地址");
			StorageDevice device = InfoHelper.getVideoDevice();
			String previewPath = device.getHttpDeviceURL();
			String transRoot = device.getFtpDeviceURL();
			InfoHelper.outputText(player + "," + previewPath + "," + transRoot , response);
		}else{
			String player = InfoHelper.getConfig("视频系统", "视频播放控件地址");
			String previewPath = InfoHelper.getConfig("视频系统", "视频播放地址");
			String transRoot = InfoHelper.getConfig("视频系统", "转码文件根目录");
			InfoHelper.outputText(player + "," + previewPath + "," + transRoot, response);
		}

	}

	/**
	 * 取一个视频的确切信息，用于选择视频时
	 */
	@RequestMapping(value = "Info.do")
	public void getVideoInfo(
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		int docLibID = WebUtil.getInt(request, "docLibID", 0);
		long docID = WebUtil.getLong(request, "docID", 0);

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = docManager.get(docLibID, docID);

		String url = doc.getString("v_url");
		String urlApp = doc.getString("v_urlApp");
		if (StringUtils.isBlank(url)) url = "";
		if (StringUtils.isBlank(urlApp)) urlApp = "";

		String player = InfoHelper.getConfig("视频系统", "视频播放控件地址");
		
		String videoName = StringUtils.getNotNull(doc.getTopic());
		CatReader catReader = (CatReader)Context.getBean(CatManager.class);
		Category cat = catReader.getCat(CatTypes.CAT_VIDEO.typeID(), doc.getInt("v_catID"));
		String catName = "";
		if(cat!=null) {
			catName = cat.getCascadeName();
			if (StringUtils.isBlank(catName)) {
				catName = StringUtils.getNotNull(cat.getCatName());
			}
		}
				

		InfoHelper.outputText(player + "," + url + "," + urlApp + "," + catName + " > " + videoName, response);
	}

	@RequestMapping(value = "getFileUploadProgress.do")
	public void getFileUploadProgress(
			HttpServletRequest request, HttpServletResponse response,
			@RequestParam("sessionID") int sessionID)
			throws Exception {
		//纳加视频服务器密码
		String passWord = InfoHelper.getConfig("视频系统", "服务器密码");

		VJVASPortType vasPortType = null;
		// 纳加点播管理操作
		vasPortType = videoManager.getVasMisService();

		IntHolder usflResult = new IntHolder();
		// 上传文件列表
		ArrayOfuploadSessionFileHolder usfl = new ArrayOfuploadSessionFileHolder();
		// 调用上传会话文件列表
		vasPortType.getUploadSessionFiles(passWord, sessionID,
		                                  usfl, usflResult);

		int uploadFileProgress = 0;
		if (usflResult.value == 0) {
			UploadSessionFile[] usflArr = usfl.value;
			if (usflArr.length > 0) {
				//暂且这样，因为默认上传视频文件为一个
				UploadSessionFile file = usflArr[0];
				uploadFileProgress = file.getProgress();

			}
		}
		if (uploadFileProgress < 100) {
			InfoHelper.outputText("Failed", response);
		} else {
			InfoHelper.outputText("success", response);
		}

	}

	//上传控件界面的取消按钮，此时此刻，sessionID还未关闭，操作需要关闭上传session，删除系统文件
	@RequestMapping(value = "UploadCancel.do")
	public void uploadCancel(
			HttpServletRequest request, HttpServletResponse response,
			@RequestParam("sessionID") int sessionID) throws RemoteException {
		//纳加视频服务器密码
		String passWord = InfoHelper.getConfig("视频系统", "服务器密码");

		VJVASPortType vasPortType = null;
		// 纳加点播管理操作
		vasPortType = videoManager.getVasMisService();

		IntHolder usflResult = new IntHolder();
		// 上传文件列表
		ArrayOfuploadSessionFileHolder usfl = new ArrayOfuploadSessionFileHolder();
		// 调用上传会话文件列表
		vasPortType.getUploadSessionFiles(passWord, sessionID,
		                                  usfl, usflResult);

		int uploadFileProgress = 0;
		String fileName = "";
		String path = "";
		if (usflResult.value == 0) {
			UploadSessionFile[] usflArr = usfl.value;
			if (usflArr.length > 0) {
				//暂且这样，因为默认上传视频文件为一个
				UploadSessionFile file = usflArr[0];
				uploadFileProgress = file.getProgress();
				fileName = file.getPath().substring(1, file.getPath().length());

				//计算出视频的上传文件路径以及生成的转码文件路径
				IntHolder ushResult = new IntHolder();
				UploadSessionHolder ush = new UploadSessionHolder();

				vasPortType.getUploadSession(passWord, sessionID, ush, ushResult);

				if (ushResult.value == 0) {
					UploadSession us = ush.value;
					String dstDir = us.getDstDir();
					path = dstDir + Slash + fileName;
				}
			}
		}
		if (uploadFileProgress < 100) {
			//不知道需要不需要删除文件
		} else {
			vasPortType.deleteSystemFile(passWord, path);
		}

		vasPortType.deleteUploadSession(passWord, sessionID);

		InfoHelper.outputText("ok", response);
	}

	//表单界面的取消按钮，此时此刻，上传session已经关闭或者就没有上传视频文件，根据情况删除视频文件
	@RequestMapping(value = "VideoCancel.do")
	public void videoCancel(
			HttpServletRequest request, HttpServletResponse response,
			@RequestParam("path") String path) throws RemoteException {
		if (!path.isEmpty()) {
			//纳加视频服务器密码
			String passWord = InfoHelper.getConfig("视频系统", "服务器密码");
			VJVASPortType vasPortType = null;
			// 纳加点播管理操作
			vasPortType = videoManager.getVasMisService();
			vasPortType.deleteSystemFile(passWord, path);
		}
		InfoHelper.outputText("ok", response);
	}
	/**
   *   拼接视频文件上传路径
   */
	@RequestMapping(value = "queryVideoPath.do")
	public void queryVideoPath(HttpServletRequest request, HttpServletResponse response) {
		JSONObject json = new JSONObject();
		json.put("code", 0);
		try {
		String VJ = InfoHelper.getConfig("视频系统", "是否经过纳加方式");
		String uploadDisk = InfoHelper.getConfig("视频系统", "上传文件根目录");
		String fileDatePath = DateUtils.format("yyyyMM"+Slash+"dd"+Slash);
		long uploadTime = System.currentTimeMillis();
		String fileMd5Path = EncodeUtils.getMD5(String.valueOf(uploadTime + '|' + new Random().nextInt(1)));
		String tranDir = null;
		String dstDir = null;
		//实际存储路径
		StorageDevice device = InfoHelper.getVideoDevice();
		String videoPath = device.getFtpDeviceURL();
			if (VJ.equals("否")){
			dstDir = videoPath + Slash + "source" + Slash + fileDatePath + fileMd5Path;
			dstDir = dstDir.replaceAll("\\\\", "/");
			tranDir = dstDir;
			}else{
			dstDir = uploadDisk + Slash + "source" + Slash + fileDatePath + fileMd5Path;
			tranDir = uploadDisk + Slash + "trans" + Slash + fileDatePath + fileMd5Path;	
			}
			json.put("sourcePath", dstDir);
			json.put("tranPath", tranDir);	
			json.put("uploadPath", dstDir);
			json.put("realTranPath", tranDir);
		} catch (Exception e) {
			json.put("code", 1);
			json.put("error", "获得路径时出错！");
			e.printStackTrace();
		}
		

		InfoHelper.outputText(json.toString(), response);
	}
}
