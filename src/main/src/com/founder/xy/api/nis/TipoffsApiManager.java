package com.founder.xy.api.nis;

import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.CacheReader;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.sys.StorageDevice;
import com.founder.e5.workspace.ProcHelper;
import com.founder.xy.api.ApiManager;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.JsonHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;
import com.founder.xy.system.Tenant;
import com.founder.xy.system.site.SiteSolrServerCache;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.springframework.stereotype.Service;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TipoffsApiManager extends BaseApiManager {

    
    @SuppressWarnings("unchecked")
	public String setImgVioUrl(JSONObject obj, long articleID, int articleLibID) throws E5Exception {
		List<String> imgUrl = (List<String>)obj.get("imgUrl"); // 图片url
		JSONArray pics = savePics(imgUrl, articleID, articleLibID);
		JSONArray videos = saveVideos(obj, articleID, articleLibID);
		//JSONArray audios = saveAudios(obj, articleID, articleLibID);
		
		JSONObject result = new JSONObject();
		result.put("pics", pics);
		result.put("videos", videos);
		//result.put("audios", audios);
		return result.toString();
	}
    
    protected JSONArray savePics(List<String> imgUrl, long articleID, int articleLibID) throws E5Exception {
		JSONArray pics = new JSONArray();
		//imgUrl.add("图片存储;xy/201806/08/dc333e25-d80f-445c-92a5-926bec628fdc50c8f2cf-aee9-4fc9-a9bd-8a4e3710c219.png");

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
//			if(rootID == 0){
				doc.set("att_type", 1);		//附件类型
//			}else{
//				doc.set("att_type", 1);		//附件类型
//			}
			
			doc.set("att_url", url); 	//发布地址
			doc.set("att_path", path); 	//存储地址

			docManager.save(doc);

			/*JSONObject pic = new JSONObject();
			pic.put("imagePath", path);*/
			pics.add(path);
		}
		
		//取第一张图作为详情图
		String detailUrl = imgUrl.get(0);
		long DocID = InfoHelper.getNextDocID(DocTypes.NISATTACHMENT.typeID());
		int docLibID = LibHelper.getNisattachment();
		Document doc = docManager.newDocument(docLibID, DocID);
		ProcHelper.initDoc(doc);
		
		String path = deviceName + ";" + detailUrl.substring(webRootLen + 1);

		doc.set("att_articleID", articleID);
		doc.set("att_articleLibID", articleLibID);
		doc.set("att_type", 1);
		doc.set("att_url", detailUrl); 	//发布地址
		doc.set("att_path", path); 	//存储地址
		//生成随机文件名，UUID
		String fileName = randomName(path);
		
		//存储设备里的文件改名
		String newDetailUrl = changeFileName(path, fileName);
		doc.set("att_url", newDetailUrl); 	//发布地址
		docManager.save(doc);
		
		return pics;
	}
    
  //生成随机文件名，保持后缀不变
  		private String randomName(String srcPath) {
  			int pos = srcPath.lastIndexOf(".");
  			String ext = pos >= 0 ? srcPath.substring(pos).toLowerCase() : "";
  			String fileName = UUID.randomUUID() + ext;
  			
  			return fileName;
  		}
  		
  		//存储设备里的文件改名，以免以后被同名文件覆盖。（发布到外网后文件不带日期目录，更容易重名覆盖）
  		private String changeFileName(String srcPath, String newFileName) {
  			int pos = srcPath.indexOf(";");
  			String deviceName = srcPath.substring(0, pos); //存储设备名：附件存储
  			String relFilePath = srcPath.substring(pos + 1); //存储文件路径：201510/23/glj_ad9.jpg
  			
  			//存储的文件改名为uuid
  			String devicePath = InfoHelper.getDevicePath(InfoHelper.getDeviceByName(deviceName));
  			pos = relFilePath.lastIndexOf("/");
  			String newFilePath = relFilePath.substring(0, pos + 1) + newFileName;
  			System.out.println("newDetailUrl:"+deviceName + ";" + newFilePath);
  			System.out.println("destDir："+devicePath);
  			System.out.println("filename："+newFileName);
  			InfoHelper.copyFile(srcPath, devicePath, newFilePath);
  			
  			//新的存储路径
  			srcPath = deviceName + ";" + newFilePath;
  			return srcPath;
  		}
    
    @SuppressWarnings("unchecked")
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
			doc.set("att_urlPad", url); // 发布地址
			doc.set("att_path", path); 	//存储地址
			doc.set("att_path", path); 	//存储地址
			doc.set("att_duration", duration); 	//视频时长
			docManager.save(doc);
	
			//判断格式 mp3不再转码
			if (!url.toLowerCase().endsWith(".mp3")) {
				transCode(doc);
			
				try {
					snapMediaPicture(doc);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			JSONObject video = new JSONObject();
			video.put("urlApp", url);
			video.put("url", url);
			video.put("videoID", -1);
			videos.add(video);
		}
		return videos;
	}
    
    @SuppressWarnings("unchecked")
	protected JSONArray saveAudios(JSONObject obj, long articleID, int articleLibID) throws E5Exception {
		JSONArray audios = new JSONArray();
		
		List<String> audioUrl = (List<String>)obj.get("audioUrl"); // 音频url
		if (audioUrl == null || audioUrl.size() == 0)
			return audios;
		
		StorageDevice device = InfoHelper.getDevice("互动", "手机视频存储设备");
		if (device == null || StringUtils.isBlank(device.getHttpDeviceURL())) {
			throw new E5Exception("请检查是否配置了手机视频存储设备，以及它的http访问路径！");
		}
	
		String deviceName = device.getDeviceName();
		int webRootLen = device.getHttpDeviceURL().length();
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		for (String url : audioUrl) {
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
			doc.set("att_type", 6); // 附件类型
			doc.set("att_url", url); // 发布地址
			doc.set("att_path", path); 	//存储地址
			doc.set("att_path", path); 	//存储地址
			doc.set("att_duration", duration); 	//视频时长
			docManager.save(doc);
	
			JSONObject audio = new JSONObject();
//			video.put("urlApp", url);
//			video.put("url", url);
//			video.put("duration", duration);
			audio.put("videoUrl", url);
			audios.add(audio);
		}
		return audios;
	}
    

}
