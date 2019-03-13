package com.founder.amuc.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.founder.amuc.commons.Constant;
import com.founder.amuc.commons.InfoHelper;
import com.founder.amuc.commons.StringHelper;
import com.founder.amuc.duiba.CreditConsumeParams;
import com.founder.amuc.duiba.CreditConsumeResult;
import com.founder.amuc.duiba.CreditNotifyParams;
import com.founder.amuc.duiba.CreditTool;
import com.founder.amuc.duiba.DuibaManager;
import com.founder.amuc.commons.DocTypes;
import com.founder.xy.commons.LibHelper;

import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;

@Controller
@RequestMapping("/api/duiba")
public class DuibaAdapter {
	//private static DuibaManager dbManager = ContextLoader.getCurrentWebApplicationContext().getBean(DuibaManager.class); 
	
	@Autowired
	DuibaManager dbManager;
	/**
	 * 兑吧自动登录接口
	 * @throws E5Exception 
	 * @throws IOException 
	 */
	@RequestMapping("/autologin.do")
	public void autologin(HttpServletRequest request,
			HttpServletResponse response, Map<String, Object> model) throws Exception{
		request.setCharacterEncoding("UTF-8");
		String uid = request.getParameter("uid");
		String redirect = request.getParameter("redirect");
		
		String appKey = StringHelper.getSysConfigVal("会员中心", "兑吧appKey");
		String appSecret = StringHelper.getSysConfigVal("会员中心", "兑吧appSecret");
		String tenantCode = InfoHelper.getTenantCode(request);
		CreditTool tool=new CreditTool(appKey, appSecret);
		String credits = "0";
		if(StringUtils.isBlank(uid)){  //未登录用户
			uid = "not_login";
		}else{  //注册用户
			//DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER,tenantCode);
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			String condition = "SYS_DELETEFLAG = 0 and SYS_DOCUMENTID = ?";
			String[] column = {"mScore"};
			//Document[] docs = docManager.find(docLib.getDocLibID(), condition, new Object[]{uid},column);
			DocLib docLib = LibHelper.getLib(DocTypes.DOCTYPE_MEMBER.typeID(),"xy");
			Document[] docs = docManager.find(docLib.getDocLibID(),condition,new Object[] {uid},column);
			
			System.out.println(docs);
			credits = docs[0].getString("mScore");  //会员积分值
		}
		Map<String, String> params=new HashMap<String, String>();
		JSONObject json = new JSONObject();
		params.put("uid",uid);
		params.put("credits",credits);
		json.put("uid",uid);
		json.put("credits",credits);
		if(redirect!=null){
		    //redirect是目标页面地址，默认积分商城首页是：http://www.duiba.com.cn/chome/index
		    //此处请设置成一个外部传进来的参数，方便运营灵活配置
		    params.put("redirect",redirect);
		    json.put("redirect",redirect);
		}
		String url=tool.buildUrlWithSign("http://www.duiba.com.cn/autoLogin/autologin?",params);  //此url即为免登录url
		//response.sendRedirect(url);
		json.put("url",url);
		outputJson(String.valueOf(json), response);
	}

	/**
	 * 积分消费接口
	 * @throws E5Exception 
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping("/creditConsume.do")
	public void creditConsume(HttpServletRequest request,
			HttpServletResponse response, Map<String, Object> model) throws Exception{
		request.setCharacterEncoding("UTF-8");
		response.setContentType("application/json; charset=UTF-8");
		String tenantCode = InfoHelper.getTenantCode(request);
		String dlp = request.getParameter("developer");
		String appKey = "";		String appSecret = "";
		if("hengshi".equalsIgnoreCase(dlp)){//判段来源是衡实科技还是兑吧	，默认是兑吧
			appKey = StringHelper.getSysConfigVal("会员中心", "衡实科技appKey");
			appSecret = StringHelper.getSysConfigVal("会员中心", "衡实科技appSecret");
		}else{
			appKey = StringHelper.getSysConfigVal("会员中心", "兑吧appKey");
			appSecret = StringHelper.getSysConfigVal("会员中心", "兑吧appSecret");
		}
		CreditTool tool=new CreditTool(appKey, appSecret);
		String resultObj = "";
		try {
		    CreditConsumeParams params= tool.parseCreditConsume(request);//利用tool来解析这个请求
		    //TODO 开发者系统对uid用户扣除credits个积分，将订单信息存储进数据库中，并返回数据存储情况
		    JSONObject saveResult = dbManager.saveDBOrder(params,tenantCode);
		    String code = saveResult.getString("code");
		    if(code.equals("1")){  //添加订单成功
		    	String bizId = saveResult.getString("xtOrderNum");  //系统订单号
		    	long remNum = saveResult.getLong("remNum"); //用户积分余额
		    	CreditConsumeResult result=new CreditConsumeResult(true);
		    	result.setBizId(bizId);
		    	result.setCredits(remNum);
		    	resultObj = result.toString();
		    }else{  //添加订单失败
		    	String msg = saveResult.getString("msg"); //错误信息
		    	long remNum = saveResult.getLong("remNum"); //用户积分余额
		    	CreditConsumeResult result=new CreditConsumeResult(false);
		    	result.setErrorMessage(msg);
		    	result.setCredits(remNum);
		    	resultObj = result.toString();
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		}
		outputJson(String.valueOf(resultObj), response);
	}
	
	/**
	 * 兑换结果通知接口
	 * @param request
	 * @param response
	 * @throws E5Exception 
	 * @throws IOException 
	 */
	@RequestMapping("/creditNotify.do")
	public void creditNotify(HttpServletRequest request,
			HttpServletResponse response, Map<String, Object> model) throws Exception{
		request.setCharacterEncoding("UTF-8");
		/*
		*  兑换订单的结果通知请求的解析方法
		*  当兑换订单成功时，兑吧会发送请求通知开发者，兑换订单的结果为成功或者失败，如果为失败，开发者需要将积分返还给用户
		*/
		String tenantCode = InfoHelper.getTenantCode(request);
		String dlp = request.getParameter("developer");
		String appKey = "";		String appSecret = "";
		if("hengshi".equalsIgnoreCase(dlp)){//判段来源是衡实科技还是兑吧	，默认是兑吧
			appKey = StringHelper.getSysConfigVal("会员中心", "衡实科技appKey");
			appSecret = StringHelper.getSysConfigVal("会员中心", "衡实科技appSecret");
		}else{
			appKey = StringHelper.getSysConfigVal("会员中心", "兑吧appKey");
			appSecret = StringHelper.getSysConfigVal("会员中心", "兑吧appSecret");
		}
		CreditTool tool=new CreditTool(appKey, appSecret);
		String result = "";
		try {
		    CreditNotifyParams params= tool.parseCreditNotify(request);//利用tool来解析这个请求
		    result = dbManager.saveOrderStatus(params, tenantCode);
		} catch (Exception e) {
		    e.printStackTrace();
		}
		outputJson(String.valueOf(result), response);
	}
    /** 向response输出json数据 */
	public static void outputJson(String result, HttpServletResponse response) {
		if (result == null) return;
		
		response.setContentType("application/json; charset=UTF-8");

		PrintWriter out = null;
		try {
			out = response.getWriter();
			out.write(result);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(out);
		}
	}
}