package com.founder.xy.wx;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.founder.e5.commons.StringUtils;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.wx.data.Account;
import com.founder.xy.wx.data.GroupArticle;

/** 
 * 微信工具
 */
public class WeixinUtil {
	/** 验证签名 */
	public static boolean checkSignature(String signature, String timestamp, String nonce) {
	    String token = getToken();
		String[] arr = new String[] { token, timestamp, nonce };
		
	    // 将token、timestamp、nonce三个参数进行字典序排序
	    Arrays.sort(arr);
	    
	    StringBuilder content = new StringBuilder();
	    for (int i = 0; i < arr.length; i++) {
	        content.append(arr[i]);
	    }
	    MessageDigest md = null;
	    String tmpStr = null;
	
	    try {
	        md = MessageDigest.getInstance("SHA-1");
	        // 将三个参数字符串拼接成一个字符串进行sha1加密
	        byte[] digest = md.digest(content.toString().getBytes());
	        tmpStr = byteToStr(digest);
	    } catch (NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    }
	    
	    // 将sha1加密后的字符串与signature对比，标识该请求来源于微信
	    return tmpStr != null ? tmpStr.equals(signature.toUpperCase()) : false;
	}

	/** 上传图文素材 */
	public static String groupMaterialUpload(Account account, String data) throws Exception{
		//上传图片
		String json = uploadImages(data, account.getAccessToken());
		
		//上传图文素材至微信服务器
		return WeixinAPI.uploadGroup(json, account.getAccessToken());
	}
	
	/** 删除图文素材*/
	public static String groupMaterialDelete(Account account, String media_id) throws Exception{
		return WeixinAPI.deleteGroup(media_id, account.getAccessToken());
	}
	
	/** 上传图文消息 */
	public static String groupUpload(Account account, String data) throws Exception{
		//上传图片
		String json = uploadImages(data, account.getAccessToken());
		
		//上传图文消息至微信服务器
		return WeixinAPI.uploadGroupTemp(json, account.getAccessToken());
	}
	
	/** 发布图文消息 */
	public static String groupPublish(Account account, String media_id) throws Exception{
		return WeixinAPI.publishGroup(media_id, account.getAccessToken());
	}
	
	/** 预览图文消息 */
	public static String groupPreview(Account account, String media_id, String wxName) throws Exception{
		return WeixinAPI.previewGroup(media_id, wxName, account.getAccessToken());
	}
	
	/** 删除图文消息 */
	public static String groupDelete(Account account, String media_id) throws Exception{
		return WeixinAPI.removeGroup(media_id, account.getAccessToken());
	}
	
	/** 将图文消息的本地图片上传至微信服务器，并且更换为真正的media_id */
	@SuppressWarnings("unchecked")
	private static String uploadImages(String data, String accessToken) throws Exception{
		
		//得到json对象
		JSONArray articles = JSONArray.fromObject(data);
		List<GroupArticle> list = (List<GroupArticle>) JSONArray.toCollection(articles, GroupArticle.class);
		
		String mediaID = null;
		for (GroupArticle article : list) {
			if (!StringUtils.isBlank(article.getPic())) {
				//读出封面图片文件
				String picPath = InfoHelper.getFilePathInDevice(article.getPic());
				File picFile = new File(picPath);
				
				//上传封面图片到微信
				String result = WeixinAPI.uploadMedia(picFile, accessToken);
				mediaID = JSONObject.fromObject(result).getString("media_id");
			}
			//上传正文中的附件
			String content = uploadImageInContent(article.getContent(), accessToken);
			
			//改变mediaID和content
			article.setMediaID(mediaID);
			article.setContent(content);
		}
		
		//改变路径后的稿件重新生成json
		
		articles = new JSONArray();
		for (GroupArticle article : list) {
			articles.add(article.json());
		}
		
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("articles", articles);
		
		return jsonObj.toString();
	}
	/**
	 * 
	* @Description: 上传正文中的图片并且替换图片路径
	 */
	private static String uploadImageInContent(String content,String accessToken) throws Exception {
		Document html = Jsoup.parse(content);
		Elements imgs = html.select("img");
		if (imgs.isEmpty()) return content;
		
		for (Element img : imgs) {
			String src = img.attr("src");
			
			int pos = src.indexOf("image.do?path=");
			String path  = null;
			if (pos < 0) {
				continue;
				/*
				if (src.indexOf(".qq.") < 0) //若是外网图片，则看是不是已上传的
					path = downloadImage(src);
				*/
			} else {
				//微信要求1M以下，所以改用抽图后的大图。不能用.2后缀，会报“错误类型”，所以使用.2.jpg
				path = src.substring(pos + "image.do?path=".length());
				path = InfoHelper.getFilePathInDevice(path) + ".2.jpg"; 
			}
			
			File tempFile = new File(path);

			String uploadResult = WeixinAPI.uploadImage(tempFile, accessToken);
			
			String imgUrl = JSONObject.fromObject(uploadResult).getString("url");
			img.attr("src", imgUrl);
		}
		return html.html();
	}

	//下载外网图片至服务器临时文件夹
	@SuppressWarnings("unused")
	private static String downloadImage(String imgUrl) throws Exception{
		/*
		//下载
		String fileName = imgUrl.substring(imgUrl.lastIndexOf("/")+1);
		String suffix;
		if(fileName.contains(".")){
			suffix = fileName.substring(fileName.lastIndexOf("."));
			fileName = fileName.substring(0, fileName.lastIndexOf("."));
			 
		}else{
			suffix = ".jpg";
		}
		// 如果图片名称带有无法保存的字符时
		if (fileName.matches(".*[:*?<>|\\/\\\\]+.*")){
			fileName = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
		}
		
		//TokenHelper.download(imgUrl,filePath,fileName);
		
		String filePath = getTempPath();//固定地址
		return filePath+"/"+fileName+suffix;
		*/
		return null;
	}
	
	/**
     * 将字节数组转换为十六进制字符串
     */
    private static String byteToStr(byte[] byteArray) {
        String strDigest = "";
        for (int i = 0; i < byteArray.length; i++) {
            strDigest += byteToHexStr(byteArray[i]);
        }
        return strDigest;
    }

    /**
     * 将字节转换为十六进制字符串
     */
    private static String byteToHexStr(byte mByte) {
        char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        char[] tempArr = new char[2];
        tempArr[0] = Digit[(mByte >>> 4) & 0X0F];
        tempArr[1] = Digit[mByte & 0X0F];

        return new String(tempArr);
    }
    
	//本系统的Token
    private static String getToken(){
    	return "xyv5";
    }
}
