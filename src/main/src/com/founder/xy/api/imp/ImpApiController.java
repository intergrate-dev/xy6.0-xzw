package com.founder.xy.api.imp;

import javax.servlet.http.HttpServletResponse;

import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.founder.e5.context.E5Exception;
import com.founder.xy.commons.InfoHelper;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 稿件库和外接业务系统对接,主要通过XML方式进行对接。
 * 如：九洲音像出版公司移动APP
 * 
 * @author JiangYu  2017-08-02
 */
@Controller
@RequestMapping("/api/import")
public class ImpApiController {

	@Autowired
	private ImpApiManager impApiManager;

	/**
	 * 根据一个栏目ID 获取子栏目信息
	 * @param response
	 * @param eid 外部系统ID
	 * @param time 请求接口时间
	 * @param sign 认证参数串
	 * @param data 业务参数
	 * @throws E5Exception
	 */
	@RequestMapping(value = "getSiteNodeTreeXml.do")
	public void getSiteNodeTreeXml(HttpServletResponse response, int eid, long time,
								   String sign, String data) throws E5Exception{
		String result = impApiManager.addChildrenNode(eid, time, sign, data);
		InfoHelper.outputJson(result, response);
	}

//	private String toHex(byte buffer[]) {
//		StringBuffer sb = new StringBuffer(buffer.length * 2);
//		for (int i = 0; i < buffer.length; i++) {
//			sb.append(Character.forDigit((buffer[i] & 240) >> 4, 16));
//			sb.append(Character.forDigit(buffer[i] & 15, 16));
//		}
//		return sb.toString();
//	}


//	private static String toHexString(byte[] md) {
//		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
//				'a', 'b', 'c', 'd', 'e', 'f' };
//		int j = md.length;
//		char str[] = new char[j * 2];
//		for (int i = 0; i < j; i++) {
//			byte byte0 = md[i];
//			str[2 * i] = hexDigits[byte0 >>> 4 & 0xf];
//			str[i * 2 + 1] = hexDigits[byte0 & 0xf];
//		}
//		return new String(str);
//	}
	
//	/**
//	 * 稿件的增、删、改操作
//	 * @param response
//	 * @param op 稿件操作     1：新增：2：修改；3：删除
//	 * @param userID  用户ID
//	 * @param articleInfo  稿件信息的xml格式字符串
//	 * @param token 预留参数
//	 * @throws E5Exception
//	 */
//	@RequestMapping(value = "impArticle.do")
//	public void impArticle(HttpServletResponse response,
//			int op, int userID, String articleInfo, String token) throws E5Exception{
//		String result = impApiManager.parseImpXml(op, userID, articleInfo);
//		InfoHelper.outputText(result, response);
//	}

	/**
	 * 稿件的新增操作
	 * @param response
	 * @param eid 外部系统ID
	 * @param time 请求接口时间
	 * @param sign 认证参数串
	 * @param data 业务参数
	 * @throws E5Exception
	 */
	@RequestMapping(value = "addArticle.do")
	public void addArticle(HttpServletResponse response, int eid, long time, String sign,
						   String data) throws E5Exception{

		String result = impApiManager.addArticle(eid, time, sign, data);
		InfoHelper.outputJson(result, response);
	}

	@RequestMapping(value = "updateArticle.do")
	public void updateArticle(HttpServletResponse response, int eid, long time, String sign,
						   String data) throws E5Exception{

		String result = impApiManager.updateArticle(eid, time, sign, data);
		InfoHelper.outputJson(result, response);
	}

	@RequestMapping(value = "delArticle.do")
	public void delArticle(HttpServletResponse response, int eid, long time, String sign,
							  String data) throws E5Exception{

		String result = impApiManager.delArticle(eid, time, sign, data);
		InfoHelper.outputJson(result, response);
	}

	@RequestMapping(value = "getPubArticles.do")
	public void getPubArticles(HttpServletResponse response, int eid, long time, String sign,
						   String data) throws E5Exception{

		String result = impApiManager.getPubArticles(eid, time, sign, data);
		InfoHelper.outputJson(result, response);
	}

//	private String extSystemAuth(int eid, long time, String sign, String data){
//
//		String result = null;
//
//		//根据eid得到esecret
//		String esecret = RedisManager.hget(RedisKey.EXTERNAL_KEY,eid);
//
//		if(esecret == null){
//
//			result = "非法系统";
//			return result;
//		}
//
//		long timeInterval = expireTime * 1000 * 30;
//		long currentTime = System.currentTimeMillis();
//		if((currentTime-time-timeInterval) > 0){
//			result = "请求超时，可能发生盗链";
//			return result;
//		}
//
//		StringBuffer tmpSign = new StringBuffer();
//		tmpSign.append("eid=").append(eid).append("&time=").append(time).append("&data=").append(data).append("&esecret=").append(esecret);
//
//		System.out.println("tmpSign---->"+tmpSign.toString());
//
//		String currentSign = null;
//		try {
//			MessageDigest md = MessageDigest.getInstance("SHA-1");
//			md.update(tmpSign.toString().getBytes("UTF-8"));
//			currentSign = toHex(md.digest());
//		} catch (NoSuchAlgorithmException e) {
//			e.printStackTrace();
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//
//		System.out.println("currentSign---->"+currentSign);
//
//		if(currentSign == null || !currentSign.equals(sign)){
//			result = "身份认证失败";
//			return result;
//		}
//
//		return result;
//	}
}
