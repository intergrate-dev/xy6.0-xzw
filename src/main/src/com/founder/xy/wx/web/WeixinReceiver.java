package com.founder.xy.wx.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.founder.xy.commons.InfoHelper;
import com.founder.xy.wx.WeixinUtil;

/**
 * 接收微信服务器发来的消息
 * @author Gong Lijie
 */
@Controller
@RequestMapping("/wxreceiver.do")
public class WeixinReceiver {
	
	/**
	 * 微信接入验证
	 */
	@RequestMapping(method=RequestMethod.GET)
	public void signature(HttpServletRequest request, HttpServletResponse response,
			@RequestParam String signature, @RequestParam String timestamp,
			@RequestParam String nonce, @RequestParam String echostr){
		System.out.println(signature);
		System.out.println(timestamp);
		System.out.println(nonce);
		
		boolean valid = WeixinUtil.checkSignature(signature, timestamp, nonce);
		System.out.println(valid);
		
		if (valid)
			InfoHelper.outputText(echostr, response);
	}
	
	/**
	 * 微信消息接收
	 */
	@RequestMapping(method=RequestMethod.POST)
	public void receiver(HttpServletRequest request, HttpServletResponse response){
		System.out.println("POST");
	}
}
