package com.founder.xy.video.web;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.rpc.holders.IntHolder;
import javax.xml.rpc.holders.StringHolder;

import localhost.VJVodServicePortType;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.flow.FlowNode;
import com.founder.e5.flow.FlowReader;
import com.founder.e5.sys.StorageDevice;
import com.founder.e5.web.SysUser;
import com.founder.e5.web.WebUtil;
import com.founder.e5.workspace.ProcHelper;
import com.founder.xy.article.Article;
import com.founder.xy.column.Column;
import com.founder.xy.column.ColumnReader;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.EncodeUtils;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.config.Channel;
import com.founder.xy.config.ConfigReader;
import com.founder.xy.video.VideoManager;

/**
 * 视频稿件相关操作
 * @author kangxw
 *
 */
@Controller
@RequestMapping("/xy/video")
public class VideoArticleSubmitController {

	@Autowired
	VideoManager videoManager;
	
	/**
	 * 获取视频的标题和说明，用于填充到表单
	 * @param request
	 * @param response
	 * @param DocID
	 * @param DocLibID
	 * @throws Exception
	 */
	@RequestMapping(value = "toVideoArticlePub.do")
	public ModelAndView getVideoArticleInfo(HttpServletRequest request, HttpServletResponse response,
			@RequestParam long DocIDs, @RequestParam int DocLibID,@RequestParam int siteID,
			@RequestParam String UUID) throws Exception {
		Map<String,Object> model = new HashMap<>();
		model.put("UUID", UUID);
		model.put("siteID", siteID);
		model.put("videoID", DocIDs);
		model.put("videoLibID", DocLibID);
		
		model.put("type", Article.TYPE_ARTICLE);
		model.put("webDocLibID", LibHelper.getArticleLibID());
		model.put("appDocLibID", LibHelper.getArticleAppLibID());
		model.put("DocID", InfoHelper.getNextDocID(DocTypes.ARTICLE.typeID()));
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document video = docManager.get(DocLibID, DocIDs);
		if(video.getInt("v_isAutoTitle") == 1){
			model.put("title", video.getTopic());
		}else{
			model.put("title", "");
		}
		if(video.getInt("v_isAutoAbstract") == 1){
			model.put("content", video.getString("v_content"));
		}else{
			model.put("content", "");
		}
		model.put("author", video.getAuthors());
		SysUser user = ProcHelper.getUser(request);
		model.put("editor", user.getUserName());
		
		model.put("isNew", true);
		Channel[] allChs = ConfigReader.getChannels();
		int ch = allChs[0].getId();
		model.put("ch", ch);
		
		if (ch >= 0)
			model.put("channel", (int) Math.pow(2, ch));
		
		String smallRadio = InfoHelper.getConfig("写稿", "标题图片尺寸-小");
        String midRadio = InfoHelper.getConfig("写稿", "标题图片尺寸-中");
        String bigRadio = InfoHelper.getConfig("写稿", "标题图片尺寸-大");
        model.put("smallRadio", smallRadio);
        model.put("midRadio", midRadio);
        model.put("bigRadio", bigRadio);
		
		return new ModelAndView("/xy/video/VideoArticlePub",model);
	}
	
	@RequestMapping(value = "getVideoUrlInfo.do")
	@ResponseBody
	public void getVideoUrl(HttpServletRequest request, HttpServletResponse response,
			@RequestParam long DocID, @RequestParam int DocLibID) throws Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document video = docManager.get(DocLibID, DocID);
		
		//已转码的稿件先发布
		if(video.getInt("v_status") == 1){
			this.publish(DocLibID,DocID);
		}
		
		video = docManager.get(DocLibID, DocID);
		JSONObject json = new JSONObject();
		if(video.getInt("v_status") == 2){
			json.put("success", true);
			json.put("webUrl", video.getString("v_url"));
			json.put("appUrl", video.getString("v_urlApp"));
		}else{
			json.put("success", true);
		}
		InfoHelper.outputJson(json.toString(), response);
	}
	
	private void publish(int DocLibID,long DocID) throws E5Exception{
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document video = docManager.get(DocLibID, DocID);
		
		//是否经过纳加
		boolean isvms = "是".equals(InfoHelper.getConfig("视频系统", "是否经过纳加方式"));
		
		if(isvms){ // 经过纳加
			String passWord = InfoHelper.getConfig("视频系统", "服务器密码");
			String playerUrl = InfoHelper.getConfig("视频系统", "视频播放地址");
			String transDisk = InfoHelper.getConfig("视频系统", "转码文件根目录");
			String mappedDisk = InfoHelper.getConfig("视频系统", "点播映射磁盘");
			
			String transDirFile = video.getString("v_transPath");//视频转码地址
			String format = video.getString("v_format");//视频格式
			String[] pubFileTypes = format.split(",");
			
			VJVodServicePortType vodPortType = videoManager.getVodService();
			Pattern hashPattern = Pattern.compile("^hash=\\s*([^&]*)");
			
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
					break;
				}
			}
		}
		else{ //不经过纳加
			StorageDevice device = InfoHelper.getVideoDevice();
			String httpPath = device.getHttpDeviceURL();
			if(!httpPath.endsWith("/")) httpPath += "/";
			String ftpPath = device.getFtpDeviceURL();
			if(!ftpPath.endsWith("/")) ftpPath += "/";
			String vPath = video.getString("v_path");
			String puburl = vPath.replace(ftpPath, httpPath);
			video.set("v_url",  puburl);
			video.set("v_urlApp", puburl);
		}
		video.set("v_status", 2);
		
		int currNode = video.getCurrentNode();
		FlowReader flowReader = (FlowReader) Context.getBean(FlowReader.class);
		FlowNode node = flowReader.getNextFlowNode(currNode);
		video.setCurrentNode(node.getID());
		video.setCurrentStatus(node.getDoingStatus());
		docManager.save(video);
	}
	
	@RequestMapping(value = "getNextDocID.do")
	public void getNextDocID(HttpServletRequest request, HttpServletResponse response) throws Exception {
		JSONObject json = new JSONObject();
		json.put("docID", InfoHelper.getNextDocID(DocTypes.ARTICLE.typeID()));
		InfoHelper.outputJson(json.toString(), response);
	}
	
}
