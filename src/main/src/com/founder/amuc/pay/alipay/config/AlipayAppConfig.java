package com.founder.amuc.pay.alipay.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import com.founder.amuc.commons.BaseHelper;
import com.founder.amuc.charge.ChargeConfigController;

/* *
 *类名：AlipayAppConfig
 *功能：基础配置类，手机支付
 *详细：设置帐户有关信息及返回路径
 */

public class AlipayAppConfig {

	// 支付宝分配给开发者的应用ID
	public static String app_id = ChargeConfigController.getConfig("支付宝", "app_id");
	
	// 签名方式
	public static String seller_id = ChargeConfigController.getConfig("支付宝", "seller_id");
	
	// 接口校验私钥
	public static String app_private_key = ChargeConfigController.getConfig("支付宝", "app_private_key");

	// 接口校验公钥
    public static String alipay_public_key = ChargeConfigController.getConfig("支付宝", "alipay_public_key");

	// 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
	public String notify_url = BaseHelper.getConfig("翔宇CMS", "互动", "外网Api地址") + "/amuc/api/pay/createpayrecordapp";;

	// 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
	public String return_url = BaseHelper.getConfig("翔宇CMS", "互动", "外网Api地址") +"/amuc/api/order/alipayApp";

	// 接口参数编码方式
	public static String charset = "UTF-8";
	
	// 签名方式
	public static String sign_type = "RSA2";
	
	
	
	//初始化支付的参数
	/*static{
			String configFile = "amuc-template/amuc-pay-config.properties";
	        InputStream in = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(configFile);
			Properties properties = new Properties();
			try {
				properties.load(in);
				//加载支付宝手机网站支付所需参数
				AlipayAppConfig.seller_id=properties.getProperty("seller_id");
				AlipayAppConfig.app_id=properties.getProperty("app_id");
				AlipayAppConfig.app_private_key=properties.getProperty("app_private_key");
				AlipayAppConfig.alipay_public_key=properties.getProperty("alipay_public_key");
			
			} catch (IOException e) {
				e.printStackTrace();
			}
		}*/
	
}

