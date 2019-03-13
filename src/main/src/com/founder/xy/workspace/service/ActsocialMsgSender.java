package com.founder.xy.workspace.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.cloopen.rest.sdk.CCPRestSDK;
import com.founder.xy.commons.InfoHelper;

/**
 * 发送短信的类
 * @author fanjc
 */
public class ActsocialMsgSender {

	private Logger logger = LoggerFactory.getLogger(ActsocialMsgSender.class);
	public static CCPRestSDK restAPI = null;
	static{
		Properties props = new Properties();
		try {
			props = PropertiesLoaderUtils.loadAllProperties("msgsend.properties");

		} catch (IOException e) {
			e.printStackTrace();
		}
		/*System.out.println(props.getProperty("SERVER"));
		System.out.println(props.getProperty("PORT"));
		System.out.println(props.getProperty("ACCOUNT_SID"));
		System.out.println(props.getProperty("AUTH_TOKEN"));
		System.out.println(props.getProperty("AppId"));*/
		
		// 初始化SDK
		restAPI = new CCPRestSDK();

		// ******************************注释*********************************************
		// *初始化服务器地址和端口 *
		// *沙盒环境（用于应用开发调试）：restAPI.init("sandboxapp.cloopen.com", "8883");*
		// *生产环境（用户应用上线使用）：restAPI.init("app.cloopen.com", "8883"); *
		// *******************************************************************************
		restAPI.init(props.getProperty("SERVER"), props.getProperty("PORT"));
		// ******************************注释*********************************************
		// *初始化主帐号和主帐号令牌,对应官网开发者主账号下的ACCOUNT SID和AUTH TOKEN *
		// *ACOUNT SID和AUTH TOKEN在登陆官网后，在“应用-管理控制台”中查看开发者主账号获取*
		// *参数顺序：第一个参数是ACOUNT SID，第二个参数是AUTH TOKEN。 *
		// *******************************************************************************
		restAPI.setAccount(props.getProperty("ACCOUNT_SID"),props.getProperty("AUTH_TOKEN"));
		// ******************************注释*********************************************
		// *初始化应用ID *
		// *测试开发可使用“测试Demo”的APP ID，正式上线需要使用自己创建的应用的App ID *
		// *应用ID的获取：登陆官网，在“应用-应用列表”，点击应用名称，看应用详情获取APP ID*
		// *******************************************************************************
		restAPI.setAppId(props.getProperty("AppId"));
	}

	/**
	 * 发送手机验证码
	 * @param mobile 手机号
	 * @param code 验证码
	 */
	@SuppressWarnings("unchecked")
	public void sendMsgCode(String mobile, String code) {

		if(restAPI == null){
			logger.error("restAPI为空,配置文件读取失败");
		} 
		HashMap<String, Object> result = null;
		// ******************************注释****************************************************************
		// *调用发送模板短信的接口发送短信 *
		// *参数顺序说明： *
		// *第一个参数:是要发送的手机号码，可以用逗号分隔，一次最多支持100个手机号 *
		// *第二个参数:是模板ID，在平台上创建的短信模板的ID值；测试的时候可以使用系统的默认模板，id为1。 *
		// *系统默认模板的内容为“【云通讯】您使用的是云通讯短信模板，您的验证码是{1}，请于{2}分钟内正确输入”*
		// *第三个参数是要替换的内容数组。 *
		// **************************************************************************************************
		String SMSTemplateID = InfoHelper.getConfig("短信验证","短信模板ID");
		result = restAPI.sendTemplateSMS(mobile, SMSTemplateID, new String[] {code, "1" });

		System.out.println("SDKTestGetSubAccounts result=" + result);
		if ("000000".equals(result.get("statusCode"))) {
			// 正常返回输出data包体信息（map）
			HashMap<String, Object> data = (HashMap<String, Object>) result.get("data");
			Set<String> keySet = data.keySet();
			for (String key : keySet) {
				Object object = data.get(key);
				logger.info(key + " = " + object);
			}
		} else {
			// 异常返回输出错误码和错误信息
			logger.error("错误码=" + result.get("statusCode") + " 错误信息= "+ result.get("statusMsg"));
		}
	}
}
