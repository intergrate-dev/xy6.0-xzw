package com.founder.xy.wx;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;

import com.founder.e5.commons.Log;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.xy.commons.JsonHelper;
import com.founder.xy.commons.web.HTTPHelper;
import com.founder.xy.wx.data.Account;

/**
 * 专门与微信服务器交互的工具类
 */
public class WeixinAPI {
	//素材管理接口
	private static String UPLOAD_MATERIAL = 	"https://api.weixin.qq.com/cgi-bin/material/add_material?access_token=ACCESS_TOKEN";
	private static String UPLOAD_MPNEWS = 		"https://api.weixin.qq.com/cgi-bin/material/add_news?access_token=ACCESS_TOKEN";
	private static String GET_MATERIAL_LIST = 	"https://api.weixin.qq.com/cgi-bin/material/batchget_material?access_token=ACCESS_TOKEN";
	private static String DEL_MATERIAL = 		"https://api.weixin.qq.com/cgi-bin/material/del_material?access_token=ACCESS_TOKEN";
	
	//临时素材
	private static String UPLOAD_NEWS = 	"https://api.weixin.qq.com/cgi-bin/media/uploadnews?access_token=ACCESS_TOKEN";
	private static String UPLOAD_MEDIA = 	"https://api.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE";
	//图文消息正文内的图片
	private static String UPLOAD_IMG = 		"https://api.weixin.qq.com/cgi-bin/media/uploadimg?access_token=ACCESS_TOKEN";
	
	//群发接口
	private static String PREVIEW_MESSAGE = "https://api.weixin.qq.com/cgi-bin/message/mass/preview?access_token=ACCESS_TOKEN";
	private static String SEND_ALL = 		"https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token=ACCESS_TOKEN";
	private static String REMOVE_MASS = 	"https://api.weixin.qq.com/cgi-bin/message/mass/delete?access_token=ACCESS_TOKEN";

	//菜单管理接口
	private static String CREATE_MENU = 	"https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";
	private static String GET_MENU = 		"https://api.weixin.qq.com/cgi-bin/menu/get?access_token=ACCESS_TOKEN";
	private static String DEL_MENU = 		"https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=ACCESS_TOKEN";
	private static String GET_MENU_INFO = 	"https://api.weixin.qq.com/cgi-bin/get_current_selfmenu_info?access_token=ACCESS_TOKEN";
	
	private static Log log = Context.getLog("xy");

	/**
	* 上传素材（图片、音频）
	* @return 例：{
		  "media_id":MEDIA_ID,
		  "url":URL
		 }
	 */
	public static String uploadMaterial(File file,String accessToken) throws E5Exception {
		return uploadAccess(UPLOAD_MATERIAL, file, accessToken);
	}
	
	/**
	1、对于临时素材，每个素材（media_id）会在开发者上传或粉丝发送到微信服务器3天后自动删除（所以用户发送给开发者的素材，
		若开发者需要，应尽快下载到本地），以节省服务器资源。
	2、media_id是可复用的。
	3、素材的格式大小等要求与公众平台官网一致。具体是，图片大小不超过2M，支持bmp/png/jpeg/jpg/gif格式，语音大小不超过5M，
		长度不超过60秒，支持mp3/wma/wav/amr格式
	4、需使用https调用本接口。
	
	* @return  {"type":"TYPE","media_id":"MEDIA_ID","created_at":123456789}
	 */
	public static String uploadMedia(File file, String accessToken) throws E5Exception{
		return uploadAccess(UPLOAD_MEDIA.replace("TYPE", "image"), file, accessToken);
	}
	
	/**
	 * 上传图文消息内的图片获取URL【订阅号与服务号认证后均可用】
	 * @return	{"url":"http://mmbiz.qpic.cn/mmbiz/gLO17UPS6FS2xsypf378iaNhWacZ1G1UplZYWEYfwvuU6Ont96b1roYs CNFwaRrSaKTPCUdBK9DgEHicsKwWCBRQ/0"}
	 */
	public static String uploadImage(File file,String accessToken) throws E5Exception{
		return uploadAccess(UPLOAD_IMG, file, accessToken);
	}

	/**
	 * 上传图文消息素材
	 * @return 例：{
			"media_id":MEDIA_ID
		}
	 */
	public static String uploadGroup(String msg,String accessToken) throws E5Exception {
		return postAccess(UPLOAD_MPNEWS, msg, accessToken);
	}

	/**
	 * 删除图文消息素材
	 * @return 例：{
			"media_id":MEDIA_ID
		}
	 */
	public static String deleteGroup(String msg_id,String accessToken) throws E5Exception {
		String msg = "{\"msg_id\":" + msg_id + "}";
		return postAccess(DEL_MATERIAL, msg, accessToken);
	}
	
	/**
	 * 
	* 上传图文消息（类型是临时消息，3天后失效）
	* @return 例： {
		   "type":"news",
		   "media_id":"CsEf3ldqkAYJAU6EJeIkStVDSvffUJ54vqbThMgplD-VJXXof6ctX5fI6-aYyUiQ",
		   "created_at":1391857799
		}
	 */
	public static String uploadGroupTemp(String msg,String accessToken) throws E5Exception{
		return postAccess(UPLOAD_NEWS, msg, accessToken);
	}

	/**
	 * 发送图文消息
	 * @return 例：{
		   "errcode":0,
		   "errmsg":"send job submission success",
		   "msg_id":34182, 
		   "msg_data_id": 206227730
		}
	 */
	public static String publishGroup(String media_id,String accessToken) throws E5Exception {
		String msg = "{\"filter\":{\"is_to_all\":true},\"mpnews\":{\"media_id\":\"" + media_id
				+ "\"},\"msgtype\":\"mpnews\"}";
		
		return postAccess(SEND_ALL, msg, accessToken);
	}

	/**
	 *  1、只有已经发送成功的消息才能删除
		2、删除消息是将消息的图文详情页失效，已经收到的用户，还是能在其本地看到消息卡片。
		3、删除群发消息只能删除图文消息和视频消息，其他类型的消息一经发送，无法删除。
		4、如果多次群发发送的是一个图文消息，那么删除其中一次群发，就会删除掉这个图文消息也，导致所有群发都失效
	* @param msg_id 发送出去的消息ID
	* @return 例：{
		   "errcode":0,
		   "errmsg":"ok"
		}
	 */
	public static String removeGroup(String msg_id,String accessToken) throws E5Exception {
		String msg = "{\"msg_id\":" + msg_id + "}";
		return postAccess(REMOVE_MASS, msg, accessToken);
	}

	/**
	 * 
	 * 图文消息预览接口        
		开发者可通过该接口发送消息给指定用户，在手机端查看消息的样式和排版。
		为了满足第三方平台开发者的需求，在保留对openID预览能力的同时，增加了对指定微信号发送预览的能力，
		但该能力每日调用次数有限制（100次），请勿滥用。
	* @param media_id 
	* @param wx_name 接受预览消息的微信号（注意！必须为关注此公众平台的用户！）
	* @param account_id 公众平台账户id
	* @return 例：{
		   "errcode":0,
		   "errmsg":"preview success",
		   "msg_id":34182
		}
	 */
	public static String previewGroup(String media_id, String wx_name,String accessToken)
			throws E5Exception {
		String msg = "{ \"towxname\":\"" + wx_name
				+ "\", \"mpnews\":{\"media_id\":\"" + media_id
				+ "\"},\"msgtype\":\"mpnews\"}";
		return postAccess(PREVIEW_MESSAGE, msg, accessToken);
	}

	/**
	 * 文本消息预览接口
	 */
	public static String previewText(String message,String wx_name,String accessToken)
			throws E5Exception {
		String msg = "{ \"towxname\":\"" + wx_name
				+ "\", \"text\":{\"content\":\"" + message
				+ "\"},\"msgtype\":\"text\"}";
		return postAccess(PREVIEW_MESSAGE, msg, accessToken);
	}
	
	/**
	 * 
	* 获取素材列表
	* @param type 素材的类型，图片（image）、视频（video）、语音 （voice）、图文（news）
	* @param offset 从全部素材的该偏移位置开始返回，0表示从第一个素材 返回
	* @param count 返回素材的数量，取值在1到20之间
	* @param account_id 账户id
	 */
	public static String materialList(String type,int offset,int count,String accessToken) throws E5Exception{
		String msg = "{\"type\":\""+type+"\",\"offset\":"+offset+",\"count\":"+count+"}";
		return postAccess(GET_MATERIAL_LIST, msg, accessToken);
	}
	
	/**
	 * 自定义菜单创建接口
	 */
	public static String createMenu(Account account, String menuStr) throws E5Exception{
		//先删除菜单
		postAccess(DEL_MENU, null, account.getAccessToken());
		
		//再创建菜单
		return postAccess(CREATE_MENU, menuStr, account.getAccessToken());
	}
	
	/**
	 * 
	* 自定义菜单查询接口
	* @param accessToken 账户APP_ID
	 */
	public static String getMenu(String accessToken) throws E5Exception{
		String url = GET_MENU.replace("ACCESS_TOKEN", accessToken);
		PostMethod post = new PostMethod(url);
		return post(post);
	}
	
	/**
	* 1、第三方平台开发者可以通过本接口，在旗下公众号将业务授权给你后，立即通过本接口检测公众号的自定义菜单配置，并通过接口再次给公众号设置好自动回复规则，以提升公众号运营者的业务体验。
	2、本接口与自定义菜单查询接口的不同之处在于，本接口无论公众号的接口是如何设置的，都能查询到接口，而自定义菜单查询接口则仅能查询到使用API设置的菜单配置。
	3、认证/未认证的服务号/订阅号，以及接口测试号，均拥有该接口权限。
	4、从第三方平台的公众号登录授权机制上来说，该接口从属于消息与菜单权限集。
	5、本接口中返回的mediaID均为临时素材（通过素材管理-获取临时素材接口来获取这些素材），每次接口调用返回的mediaID都是临时的、不同的，在每次接口调用后3天有效，若需永久使用该素材，需使用素材管理接口中的永久素材。
	 */
	public static String getMenuInfo(String accessToken) throws E5Exception {
		String result = "";
		String url = GET_MENU_INFO.replace("ACCESS_TOKEN", accessToken);
		GetMethod get = new GetMethod(url);
		try {
			result = post(get);
		} catch (E5Exception e) {
			throw new E5Exception("获取微信菜单信息失败！" + e.getLocalizedMessage(), e);
		} finally {
			get.releaseConnection();
		}
		
		return result;
	}

	private static String uploadAccess(String url, File file, String accessToken) throws E5Exception{
		url = url.replace("ACCESS_TOKEN", accessToken);
		
		PostMethod post = new PostMethod(url);
		String result = "";
		try {
			Part[] parts = { new FilePart("media", file.getName(), file,
					"multipart/form-data", "UTF-8") };
			
			MultipartRequestEntity reqEntity = new MultipartRequestEntity(parts, post.getParams());
			post.setRequestEntity(reqEntity);
			
			result = post(post);
			
		} catch (FileNotFoundException e) {
			throw new E5Exception(e);
		} finally {
			if (post != null) {
				post.releaseConnection();
			}
		}
		return result;
	}

	private static String postAccess(String url, String msg,String accessToken) throws E5Exception{
		url = url.replace("ACCESS_TOKEN", accessToken);
		PostMethod post = new PostMethod(url);
		try {
			if (msg != null) {
				StringRequestEntity entity = new StringRequestEntity(msg, "text/json", "UTF-8");
				post.setRequestEntity(entity);
			}
			
			return post(post);
		} catch (UnsupportedEncodingException e) {
			throw new E5Exception(e);
		} finally {
			if (post != null) {
				post.releaseConnection();
			}
		}
	}
	
	public static String getAccess(String url) throws E5Exception{
		GetMethod get = new GetMethod(url);
		try {
			return post(get);
		} catch (Exception e) {
			throw new E5Exception(e);
		} finally {
			if (get != null) get.releaseConnection();
		}
	}

	// 处理返回结果
	private static String post(HttpMethod method) throws E5Exception {
		int statusCode = 0;
		try {
			statusCode = HTTPHelper.sendRequestWithProxy(method);
		} catch (Exception e) {
			throw new E5Exception("提交请求异常！" + e.getLocalizedMessage(), e);
		}
		
		if (statusCode == HttpStatus.SC_OK) {
			String responseStr = null;
			try {
				responseStr = method.getResponseBodyAsString();
			} catch (IOException e) {
				throw new E5Exception("获取微信服务器返回数据异常！" + e.getLocalizedMessage(), e);
			}
			
			JSONObject jsonObj = JSONObject.fromObject(responseStr);
			int errcode = JsonHelper.getInt(jsonObj, "errcode");
			
			if (errcode != 0) {
				log.error("发送失败！错误码：" + errcode);
				
				WeixinErrorParser.parseMessageAndThrow(responseStr);
			} else {
				log.debug("发送成功！" + jsonObj.toString());
			}
			
			return responseStr;
		} else {
			throw new E5Exception("向微信服务器发送请求错误！HttpStatusCode：" + statusCode);
		}
	}
}
