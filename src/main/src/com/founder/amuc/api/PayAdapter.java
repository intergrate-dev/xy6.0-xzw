package com.founder.amuc.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.founder.amuc.commons.InfoHelper;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.LibHelper;
import com.founder.amuc.commons.DateFormatAmend;

@Controller
@RequestMapping("/api/pay")
public class PayAdapter {
	/**
	 * 支付宝电脑网站异步通知接口
	 */
	@RequestMapping("/createpayrecordpc.do")
	public void createpayrecord(HttpServletRequest request, HttpServletResponse response, Map model) throws Exception {
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/html;charset=UTF-8");
		String payType = request.getParameter("payType");
		String payID = request.getParameter("payID");
		String payNumber = request.getParameter("payNumber");
		String payMoney = request.getParameter("payMoney");
		String payChannel = request.getParameter("payChannel");
		String payTime = request.getParameter("payTime");
		String payOrder = request.getParameter("payOrder");
		JSONObject obj = new JSONObject();
		if (StringUtils.isBlank(payType) || StringUtils.isBlank(payID) || StringUtils.isBlank(payNumber)
				|| StringUtils.isBlank(payMoney) || StringUtils.isBlank(payChannel) || StringUtils.isBlank(payTime)
				|| StringUtils.isBlank(payOrder) || payType.equals("") || payID.equals("") || payNumber.equals("")
				|| payMoney.equals("") || payChannel.equals("") || payTime.equals("") || payOrder.equals("")) {
			obj.put("code", "1000");
			obj.put("msg", "参数错误");
			outputJson(String.valueOf(obj), response);
			return;
		}
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib DocLib = LibHelper.getLib(DocTypes.MEMBERORDERS.typeID(), "xy");
		Document[] orders = docManager.find(DocLib.getDocLibID(), " SYS_DELETEFLAG=0 and orderNum=? ",new Object[] {payOrder});
		int m_siteID = Integer.parseInt(orders[0].getString("m_siteID"));
		
		DocLib docLib = LibHelper.getLib(DocTypes.PAYLOG.typeID(), "xy");
		Document doc = docManager.newDocument(docLib.getDocLibID(), InfoHelper.getID(docLib.getDocTypeID()));
		doc.setDeleteFlag(0);
		doc.setFolderID(docLib.getFolderID());
		doc.set("payType", payType);
		doc.set("payID", payID);
		doc.set("payNumber", payNumber);
		doc.set("payMoney", payMoney);
		doc.set("payChannel", payChannel);
		doc.set("payTime", DateFormatAmend.timeStampDispose(payTime));
		doc.set("payOrder", payOrder);
		doc.set("m_siteID", m_siteID);
		docManager.save(doc);
		orders[0].set("payStatus", 1);
		orders[0].set("orderStatus", 1);
		orders[0].set("payTime", DateFormatAmend.timeStampDispose(payTime));
		docManager.save(orders[0]);
		obj.put("code", "1001");
		obj.put("msg", "记录存入成功");

		outputJson("success", response);
	}
	
	/**
	 * 支付宝手机异步通知接口
	 */
	@RequestMapping("/createpayrecordapp.do")
	public void createpayrecordapp(HttpServletRequest request, HttpServletResponse response, Map model) throws Exception {
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/html;charset=UTF-8");
		String payType = request.getParameter("payType");
		String payID = request.getParameter("payID");
		String payNumber = request.getParameter("payNumber");
		String payMoney = request.getParameter("payMoney");
		String payChannel = request.getParameter("payChannel");
		String payTime = request.getParameter("payTime");
		String payOrder = request.getParameter("payOrder");
		JSONObject obj = new JSONObject();
		if (StringUtils.isBlank(payType) || StringUtils.isBlank(payID) || StringUtils.isBlank(payNumber)
				|| StringUtils.isBlank(payMoney) || StringUtils.isBlank(payChannel) || StringUtils.isBlank(payTime)
				|| StringUtils.isBlank(payOrder) || payType.equals("") || payID.equals("") || payNumber.equals("")
				|| payMoney.equals("") || payChannel.equals("") || payTime.equals("") || payOrder.equals("")) {
			obj.put("code", "1000");
			obj.put("msg", "参数错误");
			outputJson(String.valueOf(obj), response);
			return;
		}
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib DocLib = LibHelper.getLib(DocTypes.MEMBERORDERS.typeID(), "xy");
		Document[] orders = docManager.find(DocLib.getDocLibID(), " SYS_DELETEFLAG=0 and orderNum=? ",new Object[] {payOrder});
		int m_siteID = Integer.parseInt(orders[0].getString("m_siteID"));
		
		DocLib docLib = LibHelper.getLib(DocTypes.PAYLOG.typeID(), "xy");
		Document doc = docManager.newDocument(docLib.getDocLibID(), InfoHelper.getID(docLib.getDocTypeID()));
		doc.setDeleteFlag(0);
		doc.setFolderID(docLib.getFolderID());
		doc.set("payType", payType);
		doc.set("payID", payID);
		doc.set("payNumber", payNumber);
		doc.set("payMoney", payMoney);
		doc.set("payChannel", payChannel);
		doc.set("payTime", DateFormatAmend.timeStampDispose(payTime));
		doc.set("payOrder", payOrder);
		doc.set("m_siteID", m_siteID);
		docManager.save(doc);
		orders[0].set("payStatus", 1);
		orders[0].set("orderStatus", 1);
		orders[0].set("payTime", DateFormatAmend.timeStampDispose(payTime));
		docManager.save(orders[0]);
		obj.put("code", "1001");
		obj.put("msg", "记录存入成功");

		outputJson("success", response);
	}
	
	/**
	 * 支付信息获取接口
	 */
	@RequestMapping("/getpaylog.do")
	public void getpaylog(HttpServletRequest request, HttpServletResponse response, Map model) throws E5Exception{
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/html;charset=UTF-8");
		String orderNum = request.getParameter("orderNum");
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib docLib = LibHelper.getLib(DocTypes.PAYLOG.typeID(), "xy");
		Document[] doc = docManager.find(docLib.getDocLibID(), " SYS_DELETEFLAG=0 and payOrder=? ",new Object[] {orderNum});
		JSONObject obj = new JSONObject();
		if (doc != null && doc.length > 0) {
			obj.put("payTime", doc[0].getString("payTime"));
			obj.put("payNumber", doc[0].getString("payNumber"));
			obj.put("payMoney", doc[0].getString("payMoney"));
			obj.put("payID", doc[0].getString("payID"));
			obj.put("payType", doc[0].getString("payType"));
			outputJson(String.valueOf(obj), response);
			return;
		}
		outputJson("没有此订单", response);
		
	}
	


	/** 向response输出json数据 */
	public static void outputJson(String result, HttpServletResponse response) {
		if (result == null)
			return;

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
