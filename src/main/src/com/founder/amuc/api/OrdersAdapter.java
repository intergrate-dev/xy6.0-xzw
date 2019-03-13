package com.founder.amuc.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.founder.amuc.commons.HTTPHelper;
import com.founder.amuc.commons.InfoHelper;
import com.founder.amuc.pay.alipay.config.AlipayAppConfig;
import com.founder.amuc.pay.alipay.config.AlipayConfig;
import com.founder.amuc.pay.alipay.util.AlipaySubmit;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.LibHelper;
import com.founder.e5.cat.CatReader;
import com.founder.e5.cat.CatType;
import com.founder.e5.cat.Category;
import com.founder.amuc.commons.BaseHelper;
import com.founder.amuc.commons.Constant;
import com.founder.amuc.commons.DateFormatAmend;

@Controller
@RequestMapping("/api/order")
public class OrdersAdapter {
	
	

	/**
	 * 生成订单
	 * 
	 * @return
	 * @throws E5Exception
	 */
  @RequestMapping("/create.do")
  public void create(HttpServletRequest request, HttpServletResponse response,
      @SuppressWarnings("rawtypes") Map model) throws Exception {
    response.setHeader("Access-Control-Allow-Origin", "*");
    response.setContentType("text/html;charset=UTF-8");
    //HTTPHelper.checkValid(request);
    String meal = request.getParameter("meal");
    String users = request.getParameter("users");
    String pay = request.getParameter("pay");
    String operator = request.getParameter("operator");
    String total = request.getParameter("total");
    String orderSource = request.getParameter("orderSource");
    int siteID = Integer.parseInt(request.getParameter("siteID"));
    String taxpayer = request.getParameter("taxpayer");//发票单位名称
    String unitNum = request.getParameter("unitNum");//单位税号
    String unitAddr = request.getParameter("unitAddr");//邮寄地址
    JSONObject obj = new JSONObject();
    JSONObject mealsObj = JSONObject.fromObject(meal);
    JSONArray myJsonArray = JSONArray.fromObject(users);
    DocumentManager docManager = DocumentManagerFactory.getInstance();
    DocLib docLib = LibHelper.getLib(DocTypes.MEMBERORDERS.typeID(), "xy");
    for (int i = 0; i < myJsonArray.size(); i++) {
      SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddhhmmss");
      SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      String num = sdf1.format(new Date());

      String orderNum = new String(num);
      Document mDoc = getMemberBymobile(
          myJsonArray.getJSONObject(i).getString("mobile"),siteID, "xy");
      Document doc = docManager.newDocument(docLib.getDocLibID(),
          InfoHelper.getID(docLib.getDocTypeID()));
      doc.setDeleteFlag(0);
      doc.setFolderID(docLib.getFolderID());
      doc.set("m_siteID", siteID);
      doc.set("orderNum", orderNum);
      doc.set("total", total);
      doc.set("userName", mDoc.getString("mName"));// userName
      doc.set("realName", myJsonArray.getJSONObject(i).getString("realName"));
      doc.set("mobile", myJsonArray.getJSONObject(i).getString("mobile"));
      doc.set("mail", mDoc.getString("mEmail"));
      doc.set("createTime", DateFormatAmend.timeStampDispose(df.format(new Date()))); // 创建时间
      doc.set("orderStatus", 0);
    /*  doc.set(
          "orderStatus_ID",
          getCatElementId(doc.getString("orderStatus"), "orderStatus_ID",
              docLib.getDocTypeID()));// 下拉1
*/      
      doc.set("payStatus", 0);
     /* doc.set(
          "payStatus_ID",
          getCatElementId(doc.getString("payStatus"), "payStatus_ID",
              docLib.getDocTypeID()));// 下拉2
*/      
      doc.set("payWay", pay);
      doc.set("payWay_ID",
          getCatElementId(pay));// 下拉3
     
      /*doc.set(
          "invoice_ID",
          getCatElementId(doc.getString("invoice"), "invoice_ID",
              docLib.getDocTypeID()));*/// 下拉4
     /* doc.set(
              "invoice_ID",
              getCatElementId("未开", "invoice_ID",
                  docLib.getDocTypeID()));// 下拉4
*/     
      doc.set("setMealName", mealsObj.getString("setMealName"));
      doc.set("setMealID", mealsObj.getString("setMealID"));
      doc.set("orderSource", StringUtils.isBlank(orderSource) ? 0
          : orderSource);
/*      doc.set(
          "orderSource_ID",
          getCatElementId(doc.getString("orderSource"), "orderSource_ID",
              docLib.getDocTypeID()));// 下拉5
*/      
      doc.set("operator", operator);
      if(taxpayer.equals("")||taxpayer==null){
    	  doc.set("invoice", 0); // 发票  
      }else{
    	  doc.set("invoice", 1); // 发票  
          doc.set("taxpayer", taxpayer);
          doc.set("unitNum", unitNum);
          doc.set("unitAddr", unitAddr);
      }

      docManager.save(doc);
      obj.put("code", "1003");
      obj.put("msg", "生成订单成功");
      obj.put("uid_sso",mDoc.getInt("uid_sso"));
	  obj.put("orderNum", orderNum);
    }

    outputJson(String.valueOf(obj), response);
  }

	private char[] increStrsys(String codestr) {
		if (codestr != null && codestr.length() > 0) {

			char[] charArray = codestr.toCharArray();
			AtomicInteger z = new AtomicInteger(0);
			for (int i = charArray.length - 1; i > -1; i--) {
				if (charArray[i] == '9') {
					z.set(z.incrementAndGet());
				} else {
					if (z.intValue() > 0 || i == charArray.length - 1) {

						AtomicInteger atomic = new AtomicInteger(charArray[i]);
						charArray[i] = (char) atomic.incrementAndGet();
						z.set(0);
						for (int j = charArray.length - 1; j >= i + 1; j--) {
							charArray[j] = '0';
						}
						break;
					}
				}
			}
			return (charArray);
		}
		return null;
	}

	@RequestMapping("/checkuser.do")
	public void checkuser(HttpServletRequest request, HttpServletResponse response,
			@SuppressWarnings("rawtypes") Map model) throws Exception {
		response.setHeader("Access-Control-Allow-Origin","*");
		response.setContentType("text/html;charset=UTF-8");
		int siteID = Integer.parseInt(request.getParameter("siteID"));
		String mobile = request.getParameter("mobile");
		JSONObject obj = new JSONObject();
		Document mDoc = getMemberBymobile(mobile,siteID, "xy");
		if (mDoc == null) {
			obj.put("code", "1001");
			obj.put("msg", "不存在该账户");
			outputJson(String.valueOf(obj), response);
		}
		else if (mDoc != null && mDoc.getInt("mStatus") == 0) {
			obj.put("code", "1002");
			obj.put("msg", "用户被禁用");
			outputJson(String.valueOf(obj), response);
		} else {
			obj.put("code", "1003");
			obj.put("mail", mDoc.get("mEmail"));
			obj.put("userName", mDoc.get("mName"));
			obj.put("msg", "用户存在");
			outputJson(String.valueOf(obj), response);
		}
	}

	/**
	 * 根据手机查询会员 返回：会员members对象
	 * 
	 * @param uid
	 * @param tenantcode
	 * @return
	 * @throws E5Exception
	 */
	private Document getMemberBymobile(String mMobile, int siteID, String tenantcode) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib mdocLib = LibHelper.getLib(DocTypes.MEMBER.typeID(), "xy");
		// 查询系统中是否存在uid的会员
		String mcondition = "mMobile = ? and m_siteID = ? and SYS_DELETEFLAG = 0";
		String[] column = { "mName", "mMobile", "mStatus", "mEmail" ,"uid_sso"};
		Document[] members = docManager.find(mdocLib.getDocLibID(), mcondition, new Object[] { mMobile,siteID }, column);
		if (members != null && members.length > 0) {
			return members[0];
		} else {
			return null;
		}
	}

	@RequestMapping("/getMsg.do")
	public void getMsg(HttpServletRequest request, HttpServletResponse response,
			@SuppressWarnings("rawtypes") Map model) throws Exception {
		response.setHeader("Access-Control-Allow-Origin","*");
		response.setContentType("text/html;charset=UTF-8");

		String orderNum = request.getParameter("orderNum");
		String docid = request.getParameter("docid");
		JSONObject obj1 = new JSONObject();
		JSONObject obj = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib docLib = LibHelper.getLib(DocTypes.MEMBERORDERS.typeID(), "xy");
		String sql = "";
		Document[] doc;
		if (StringUtils.isBlank(docid)) {
			sql = " SYS_DELETEFLAG = 0 and orderNum = ? ";
			doc = docManager.find(docLib.getDocLibID(), sql, new Object[] { orderNum });
		} else {
			sql = " SYS_DELETEFLAG = 0 and SYS_DOCUMENTID = ? ";
			doc = docManager.find(docLib.getDocLibID(), sql, new Object[] { docid });
		}
		// Document[] doc = docManager.find(docLib.getDocLibID(), "
		// SYS_DELETEFLAG = 0 and SYS_DOCUMENTID = ? ", new Object[]{docid});
		if (doc != null && doc.length > 0) {
			obj1.put("orderNum", doc[0].get("orderNum"));
			obj1.put("userName", doc[0].get("userName"));
			obj1.put("realName", doc[0].get("realName"));
			obj1.put("mobile", doc[0].get("mobile"));
			obj1.put("mail", doc[0].get("mail"));
			obj1.put("createTime", doc[0].getString("createTime"));
			obj1.put("orderStatus", doc[0].get("orderStatus"));
			obj1.put("payStatus", doc[0].get("payStatus"));

			obj1.put("payWay", doc[0].get("payWay"));
			obj1.put("invoice", doc[0].get("invoice"));
			obj1.put("orderSource", doc[0].get("orderSource"));
			obj1.put("total", doc[0].get("total"));
			obj1.put("setMealID", doc[0].get("setMealID"));
			jsonArray.add(obj1);

			obj.put("code", "1000");
			obj.put("msg", "获取订单信息成功");
			obj.put("data", jsonArray);
			outputJson(String.valueOf(obj), response);
			return;
		}
		obj.put("code", "1001");
		obj.put("msg", "不存在该账户");
		outputJson(String.valueOf(obj), response);
	}
	
	@RequestMapping("/getMessagesPackage.do")
	public void getMessagesPackage(HttpServletRequest request, HttpServletResponse response,
			@SuppressWarnings("rawtypes") Map model) throws Exception {
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/html;charset=UTF-8");
		// HTTPHelper.checkValid(request);
		String orderNum = request.getParameter("orderNum");
		String uid = request.getParameter("uid");
		int siteID = Integer.parseInt(request.getParameter("siteID"));
		JSONObject obj1 = new JSONObject();
		JSONObject obj2 = new JSONObject();
		JSONObject obj3 = new JSONObject();
		JSONObject obj = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib docLibM = LibHelper.getLib(DocTypes.MEMBER.typeID(), "xy");
		Document[] docM = docManager.find(docLibM.getDocLibID(), " SYS_DELETEFLAG = 0 and uid_sso = ? and m_siteID = ? ",
				new Object[] { uid,siteID });
		if (docM != null && docM.length > 0) {
			DocLib docLib = LibHelper.getLib(DocTypes.MEMBERORDERS.typeID(), "xy");
			DocLib docLibPapercard = LibHelper.getLib(DocTypes.MEMBERPAPERCARD.typeID(), "xy");
			String sql;
			Document[] doc;
			String sqlpapercard = "SYS_DELETEFLAG = 0 and pcMember_ID = ? and pcActiveStatus = '激活' and m_siteID = ? ";
			Document[] docpapercard = docManager.find(docLibPapercard.getDocLibID(), sqlpapercard,
					new Object[] { docM[0].get("SYS_DOCUMENTID"),siteID });
			if (StringUtils.isBlank(orderNum)) {
				sql = " SYS_DELETEFLAG = 0 and mobile = ? and m_siteID = ? order by createTime desc ";
				doc = docManager.find(docLib.getDocLibID(), sql, new Object[] { docM[0].get("mMobile"),siteID });
			} else {
				sql = " SYS_DELETEFLAG = 0 and orderNum=? and mobile = ? and m_siteID = ? order by createTime desc ";
				doc = docManager.find(docLib.getDocLibID(), sql, new Object[] { orderNum, docM[0].get("mMobile"),siteID });
			}
			if ((doc != null && doc.length > 0)||(docpapercard != null && docpapercard.length > 0)) {
				for (int i = 0; i < doc.length; i++) {
					obj1.put("orderNum", doc[i].get("orderNum"));
					obj1.put("createTime", doc[i].getString("payTime"));
					obj1.put("realName", doc[i].get("realName"));
					obj1.put("mobile", doc[i].get("mobile"));
					obj1.put("payStatus", doc[i].get("payStatus"));
					obj1.put("orderStatus", doc[i].get("orderStatus"));
					obj1.put("payWay", doc[i].get("payWay"));
					obj1.put("invoice", doc[i].get("invoice"));
					obj1.put("total", doc[i].get("total"));
					JSONObject map = FindSetMeal(doc[i].getString("setMealID"), "xy");
					obj1.put("setMeal", map.get("data"));
					jsonArray.add(obj1);
				}
				for (int i = 0;i<docpapercard.length;i++){
					List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
					obj2.put("setMealMoney", "papercard");
					obj2.put("setMealNmae", docpapercard[i].get("pcNo"));
					obj2.put("setMealContent", docpapercard[i].get("pcPaperName"));
					obj2.put("expiryDate", docpapercard[i].get("pcExpiryDate"));
					obj2.put("EffectTime", docpapercard[i].getString("pcEffectTime"));
					obj2.put("ExpireTime", docpapercard[i].getString("pcExpireTime"));
					list.add(obj2);
					obj3.put("setMeal", list);
					obj3.put("createTime", docpapercard[i].getString("activeTime"));
					jsonArray.add(obj3);
				}
				obj.put("code", "1000");
				obj.put("msg", "获取订单信息成功");
				obj.put("data", jsonArray);
				outputJson(String.valueOf(obj), response);
				return;
			} else {
				obj.put("code", "1001");
				obj.put("msg", "会员没有订单");
				outputJson(String.valueOf(obj), response);
				return;
			}
		}

		obj.put("code", "1002");
		obj.put("msg", "会员不存在");
		outputJson(String.valueOf(obj), response);
	}

  @RequestMapping("/cancel.do")
  public void cancel(HttpServletRequest request, HttpServletResponse response,
      @SuppressWarnings("rawtypes") Map model) throws Exception {
    response.setHeader("Access-Control-Allow-Origin", "*");
    response.setContentType("text/html;charset=UTF-8");
    //HTTPHelper.checkValid(request);
    String orderNum = request.getParameter("orderNum");
    String docid = request.getParameter("docid");
    JSONObject obj = new JSONObject();
    DocumentManager docManager = DocumentManagerFactory.getInstance();
    DocLib docLib = LibHelper.getLib(DocTypes.MEMBERORDERS.typeID(), "xy");
    String sql = "";
    Document[] doc;
    if (StringUtils.isBlank(docid)) {
      sql = " SYS_DELETEFLAG = 0 and orderNum = ? ";
      doc = docManager.find(docLib.getDocLibID(), sql,
          new Object[] { orderNum });
    } else {
      sql = " SYS_DELETEFLAG = 0 and SYS_DOCUMENTID = ? ";
      doc = docManager.find(docLib.getDocLibID(), sql, new Object[] { docid });
    }

    if (doc != null && doc.length > 0) {
      doc[0].set("orderStatus", 2);
 /*     doc[0].set(
          "orderStatus_ID",
          getCatElementId(doc[0].getString("orderStatus"), "orderStatus_ID",
              docLib.getDocTypeID()));// 下拉1
*/      docManager.save(doc[0]);
      obj.put("code", "1000");
      obj.put("msg", "取消订单成功");
      outputJson(String.valueOf(obj), response);
      return;
    }

    obj.put("code", "1001");
    obj.put("msg", "取消订单失败");
    outputJson(String.valueOf(obj), response);
  }

  @RequestMapping("/del.do")
  public void del(HttpServletRequest request, HttpServletResponse response,
      @SuppressWarnings("rawtypes") Map model) throws Exception {
    response.setHeader("Access-Control-Allow-Origin", "*");
    response.setContentType("text/html;charset=UTF-8");
    //HTTPHelper.checkValid(request);
    String orderNum = request.getParameter("orderNum");
    String docid = request.getParameter("docid");
    JSONObject obj = new JSONObject();
    DocumentManager docManager = DocumentManagerFactory.getInstance();
    DocLib docLib = LibHelper.getLib(DocTypes.MEMBERORDERS.typeID(), "xy");
    String sql = "";
    Document[] doc;
    if (StringUtils.isBlank(docid)) {
      sql = " SYS_DELETEFLAG = 0 and orderNum = ? ";
      doc = docManager.find(docLib.getDocLibID(), sql,
          new Object[] { orderNum });
    } else {
      sql = " SYS_DELETEFLAG = 0 and SYS_DOCUMENTID = ? ";
      doc = docManager.find(docLib.getDocLibID(), sql, new Object[] { docid });
    }

    if (doc != null && doc.length > 0) {
      doc[0].set("SYS_DELETEFLAG", 1);
      docManager.save(doc[0]);
      obj.put("code", "1000");
      obj.put("msg", "删除订单成功");
      outputJson(String.valueOf(obj), response);
      return;
    }

    obj.put("code", "1001");
    obj.put("msg", "删除订单失败");
    outputJson(String.valueOf(obj), response);
  }

  @RequestMapping("/getMessages.do")
  public void getMessages(HttpServletRequest request,
      HttpServletResponse response, @SuppressWarnings("rawtypes") Map model)
      throws Exception {
    response.setHeader("Access-Control-Allow-Origin", "*");
    response.setContentType("text/html;charset=UTF-8");
    //HTTPHelper.checkValid(request);
    String orderNum = request.getParameter("orderNum");
    String uid = request.getParameter("uid");
    int siteID = Integer.parseInt(request.getParameter("siteID"));
    JSONObject obj1 = new JSONObject();
    JSONObject obj = new JSONObject();
    JSONArray jsonArray = new JSONArray();
    DocumentManager docManager = DocumentManagerFactory.getInstance();
    DocLib docLibM = LibHelper.getLib(DocTypes.MEMBER.typeID(), "xy");
    Document[] docM = docManager.find(docLibM.getDocLibID(),
        " SYS_DELETEFLAG = 0 and uid_sso = ? and m_siteID = ? ", new Object[] { uid,siteID });
    if (docM != null && docM.length > 0) {
      DocLib docLib = LibHelper.getLib(DocTypes.MEMBERORDERS.typeID(), "xy");
      String sql;
      Document[] doc;
      if (StringUtils.isBlank(orderNum)) {
        sql = " SYS_DELETEFLAG = 0 and mobile = ? and m_siteID = ? order by createTime desc ";
        doc = docManager.find(docLib.getDocLibID(), sql,
            new Object[] { docM[0].get("mMobile"),siteID });
      } else {
        sql = " SYS_DELETEFLAG = 0 and orderNum=? and mobile = ? and m_siteID = ? order by createTime desc ";
        doc = docManager.find(docLib.getDocLibID(), sql, new Object[] {
            orderNum, docM[0].get("mMobile"),siteID });
      }
      if (doc != null && doc.length > 0) {
        for (int i = 0; i < doc.length; i++) {
          obj1.put("orderNum", doc[i].get("orderNum"));
          obj1.put("createTime", doc[i].getString("createTime"));
          obj1.put("realName", doc[i].get("realName"));
          obj1.put("mobile", doc[i].get("mobile"));
          obj1.put("payStatus", doc[i].get("payStatus"));
          obj1.put("orderStatus", doc[i].get("orderStatus"));
          obj1.put("payWay", doc[i].get("payWay"));
          obj1.put("invoice", doc[i].get("invoice"));
          obj1.put("taxpayer", doc[i].get("taxpayer"));
		  obj1.put("unitNum", doc[i].get("unitNum"));
		  obj1.put("unitAddr", doc[i].get("unitAddr"));
          obj1.put("total", doc[i].get("total"));

          JSONObject map = FindSetMeal(doc[i].getString("setMealID"), "xy");
          obj1.put("setMeal", map.get("data"));
          jsonArray.add(obj1);
        }
        obj.put("code", "1000");
        obj.put("msg", "获取订单信息成功");
        obj.put("data", jsonArray);
        outputJson(String.valueOf(obj), response);
        return;
      } else {
        obj.put("code", "1001");
        obj.put("msg", "会员没有订单");
        outputJson(String.valueOf(obj), response);
        return;
      }
    }

    obj.put("code", "1002");
    obj.put("msg", "会员不存在");
    outputJson(String.valueOf(obj), response);
  }

	public static JSONObject FindSetMeal(String ids, String tenantcode) throws E5Exception {

		JSONObject map = new JSONObject();
		map.put("code", 0);
		DocLib docLib = LibHelper.getLib(DocTypes.MEMBERSETMEAL.typeID(), "xy");
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			if (ids != null && ids.trim() != "") {
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				for (String id : ids.split(",")) {
					Document document = docManager.get(docLib.getDocLibID(), Long.parseLong(id));
					if (document != null && document.getString("SYS_DELETEFLAG").equals("0")) {
						Map<String, Object> doc = new HashMap<String, Object>();
						doc.put("docid", document.getDocID());
						doc.put("setMealNmae", document.getString("setMealNmae"));
						doc.put("paperNumber", document.getString("paperNumber"));
						doc.put("setMealMoney", document.getString("setMealMoney"));
						doc.put("EffectTime", document.getString("EffectTime"));
						doc.put("ExpireTime", document.getString("ExpireTime"));
						String expiryDate = document.getString("expiryDate");
						if(expiryDate.indexOf("天")>0){
							expiryDate = expiryDate.substring(0, expiryDate.length()-1);
						}
						doc.put("expiryDate", expiryDate);
						doc.put("setMealContent", document.getString("setMealContent"));
						list.add(doc);
					}
				}
				map.put("data", list);
			} else {
				map.put("code", 2);
				map.put("data", "ids值为空");
			}
		} catch (Exception e) {
			map.put("code", 1);
			map.put("data", "错误");
		}
		return map;
	}

  @RequestMapping("/regain.do")
  public void regain(HttpServletRequest request, HttpServletResponse response,
      @SuppressWarnings("rawtypes") Map model) throws Exception {
    response.setHeader("Access-Control-Allow-Origin", "*");
    response.setContentType("text/html;charset=UTF-8");
    //HTTPHelper.checkValid(request);
    String orderNum = request.getParameter("orderNum");
    String docid = request.getParameter("docid");
    JSONObject obj = new JSONObject();
    DocumentManager docManager = DocumentManagerFactory.getInstance();
    DocLib docLib = LibHelper.getLib(DocTypes.MEMBERORDERS.typeID(), "xy");
    String sql = "";
    Document[] doc;
    if (StringUtils.isBlank(docid)) {
      sql = " SYS_DELETEFLAG = 0 and orderNum = ? ";
      doc = docManager.find(docLib.getDocLibID(), sql,
          new Object[] { orderNum });
    } else {
      sql = " SYS_DELETEFLAG = 0 and SYS_DOCUMENTID = ? ";
      doc = docManager.find(docLib.getDocLibID(), sql, new Object[] { docid });
    }

    if (doc != null && doc.length > 0) {
      doc[0].set("orderStatus", 3);
     /* doc[0].set(
          "orderStatus_ID",
          getCatElementId(doc[0].getString("orderStatus"), "orderStatus_ID",
              docLib.getDocTypeID()));// 下拉1
*/      docManager.save(doc[0]);
      obj.put("code", "1000");
      obj.put("msg", "重新下单成功");
      outputJson(String.valueOf(obj), response);
      return;
    }

    obj.put("code", "1001");
    obj.put("msg", "重新下单失败");
    outputJson(String.valueOf(obj), response);
  }

  

	public static boolean FindSetMeal1(String ids, String paperName, String tenantcode) throws E5Exception {

		DocLib docLib = LibHelper.getLib(DocTypes.MEMBERSETMEAL.typeID(), "xy");
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		JSONArray list = new JSONArray();
		boolean result = false;
		try {
			if (ids != null && ids.trim() != "") {
				for (String id : ids.split(",")) {

					String sql = " SYS_DELETEFLAG = 0 and SYS_DOCUMENTID = ? and date_format(now(),'%y-%m-%d') <= ExpireTime and  date_format(now(),'%y-%m-%d') >= EffectTime and setMealContent LIKE '%"
							+ paperName + "%' ";
					Document[] document = docManager.find(docLib.getDocLibID(), sql,
							new Object[] { Long.parseLong(id) });

					if (document != null && document.length > 0) {
						result = true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

  @RequestMapping("/renew1.do")
  public void renew1(HttpServletRequest request, HttpServletResponse response,
      @SuppressWarnings("rawtypes") Map model) throws Exception {
    response.setHeader("Access-Control-Allow-Origin", "*");
    response.setContentType("text/html;charset=UTF-8");
    //HTTPHelper.checkValid(request);
    String setmealid = request.getParameter("setmealid");
    String ssoid = request.getParameter("ssoid");
    int siteID = Integer.parseInt(request.getParameter("siteID"));
    DocLib docLib = LibHelper.getLib(DocTypes.MEMBERSETMEAL.typeID(), "xy");
    DocumentManager docManager = DocumentManagerFactory.getInstance();
    String sql = " SYS_DELETEFLAG = 0 and SYS_DOCUMENTID = ? ";
    Document[] document = docManager.find(docLib.getDocLibID(), sql,
        new Object[] { setmealid });
    String sql2 = "";
    if (StringUtils.isBlank(document[0].getString("ExpireTime"))) {
      sql2 = " SYS_DELETEFLAG = 0 and Status = '在售' and date_format(now(),'%y') = date_format(CreationTime,'%y') and setMealContent = ? and '"
          + document[0].getString("CreationTime") + "' < CreationTime ";
    } else {
      sql2 = " SYS_DELETEFLAG = 0 and Status = '在售' and date_format(now(),'%y') = date_format(CreationTime,'%y') and setMealContent = ? and '"
          + document[0].getString("ExpireTime") + "' < ExpireTime ";
    }

    Document[] document2 = docManager.find(docLib.getDocLibID(), sql2,
        new Object[] { document[0].getString("setMealContent") });
    JSONObject obj = new JSONObject();
    if (document2 != null && document2.length > 0) {
      DocLib docLibM = LibHelper.getLib(DocTypes.MEMBER.typeID(), "xy");
      Document[] docM = docManager.find(docLibM.getDocLibID(),
          " SYS_DELETEFLAG = 0 and uid_sso = ? ", new Object[] { ssoid });
      DocLib docLibO = LibHelper.getLib(DocTypes.MEMBERORDERS.typeID(), "xy");
      String sqlO = " SYS_DELETEFLAG = 0 and mobile = ? and setMealID = ? and payStatus=1  ";
      Document[] docO = docManager.find(docLibO.getDocLibID(), sqlO,
          new Object[] { docM[0].get("mMobile"), setmealid });
      if (docO != null && docO.length > 0) {
        Document doc = docManager.newDocument(docLibO.getDocLibID());
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddhhmmss");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String num = sdf1.format(new Date());
        String orderNum = new String(num);
        doc.setDeleteFlag(0);
        doc.setFolderID(docLibO.getFolderID());
        doc.set("orderNum", orderNum);
        doc.set("total", document2[0].getString("setMealMoney"));
        doc.set("userName", docO[0].getString("userName"));// userName
        doc.set("realName", docO[0].getString("realName"));
        doc.set("mobile", docO[0].getString("mobile"));
        doc.set("mail", docO[0].getString("mail"));
        doc.set("createTime", DateFormatAmend.timeStampDispose(df.format(new Date()))); // 创建时间
        doc.set("orderStatus", 4);
        doc.set("payStatus", 1);
        doc.set("payWay", docO[0].getString("payWay"));
        doc.set(
            "payWay_ID",
            getCatElementId(docO[0].getString("payWay")));// 下拉3
        doc.set("invoice", 0); // 发票
        doc.set("setMealName", document2[0].getString("setMealNmae"));
        doc.set("setMealID", document2[0].getString("SYS_DOCUMENTID"));
        doc.set("orderSource", docO[0].getString("orderSource"));
        doc.set("operator", docO[0].getString("operator"));
        docManager.save(doc);
        obj.put("code", "1000");
        obj.put("msg", "续订成功");
      } else {
        obj.put("code", "1001");
        obj.put("msg", "没有可用订单");
      }
    } else {
      obj.put("code", "1001");
      obj.put("msg", "没有可续订套餐");
    }

    outputJson(String.valueOf(obj), response);
  }

  @RequestMapping("/renew.do")
  public void renew(HttpServletRequest request, HttpServletResponse response,
      @SuppressWarnings("rawtypes") Map model) throws Exception {
    response.setHeader("Access-Control-Allow-Origin", "*");
    response.setContentType("text/html;charset=UTF-8");
    //HTTPHelper.checkValid(request);
    String setmealid = request.getParameter("setmealid");
    String ssoid = request.getParameter("ssoid");
    int siteID = Integer.parseInt(request.getParameter("siteID"));
    
    DocLib docLib = LibHelper.getLib(DocTypes.MEMBERSETMEAL.typeID(), "xy");
	DocumentManager docManager = DocumentManagerFactory.getInstance();
	String sql = " SYS_DELETEFLAG = 0 and SYS_DOCUMENTID = ? and m_siteID="+siteID;
	Document[] document = docManager.find(docLib.getDocLibID(), sql, new Object[]{setmealid});
	SimpleDateFormat sdf1 = new SimpleDateFormat( "yyyyMMddhhmmss" );
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	//String sql2 ="";
	JSONObject obj = new JSONObject();
	int expiryDate;
	String CreationTime;
	if(document == null || document.length <= 0){
		obj.put("code", "1001");
		obj.put("msg", "套餐不存在");
		outputJson(String.valueOf(obj), response);
		return ;
	}
	if(StringUtils.isBlank(document[0].getString("ExpireTime"))){
		int tian = days(document[0].getString("CreationTime"),df.format(new Date()));
		String expiryDate1 = document[0].getString("expiryDate");
		if(expiryDate1.indexOf("天")>0){
			expiryDate1 = expiryDate1.substring(0, expiryDate1.length()-1);
		}
		expiryDate = Integer.parseInt(expiryDate1);
		CreationTime = document[0].getString("CreationTime");
		if(tian<=expiryDate){
			expiryDate = expiryDate*2;
		}else{
			CreationTime = df.format(new Date());
		}
	}else{
		obj.put("code", "1001");
		obj.put("msg", "套餐不可续订");
		outputJson(String.valueOf(obj), response);
		return ;
	}
	
	DocLib docLibM = LibHelper.getLib(DocTypes.MEMBER.typeID(), "xy");
	Document[] docM = docManager.find(docLibM.getDocLibID(), " SYS_DELETEFLAG = 0 and uid_sso = ? ", new Object[]{ssoid});
	DocLib docLibO = LibHelper.getLib(DocTypes.MEMBERORDERS.typeID(), "xy");
	String sqlO = " SYS_DELETEFLAG = 0 and mobile = ? and setMealID = ? and payStatus=1  and m_siteID="+siteID;
	Document[] docO = docManager.find(docLibO.getDocLibID(), sqlO, new Object[]{docM[0].get("mMobile"),setmealid});
	if(docO != null && docO.length > 0){
		
		//新建套餐
		Document newSetmeal = docManager.newDocument(docLib.getDocLibID());
		newSetmeal.set("Remind", document[0].getString("Remind"));
		
		newSetmeal.set("CreationTime",CreationTime);
		newSetmeal.setFolderID(docLib.getFolderID());
		
		newSetmeal.set("Member", document[0].getString("Member"));
		newSetmeal.set("Member_ID", document[0].getString("Member_ID"));
		newSetmeal.set("Operator", document[0].getString("Operator"));
		newSetmeal.set("Status", document[0].getString("Status"));
		newSetmeal.set("Channel", document[0].getString("Channel"));
		newSetmeal.set("paperNumber", document[0].getString("paperNumber"));
		newSetmeal.set("setMealNmae", document[0].getString("setMealNmae"));
		newSetmeal.set("setMealMoney", document[0].getString("setMealMoney"));	
		newSetmeal.set("EffectTime",(document[0].getString("EffectTime")=="")?null:document[0].getString("EffectTime"));
		newSetmeal.set("ExpireTime",(document[0].getString("ExpireTime")=="")?null:document[0].getString("ExpireTime"));	
		newSetmeal.set("setMealContent", document[0].getString("setMealContent"));
		newSetmeal.set("m_siteID", siteID);
		newSetmeal.set("expiryDate",expiryDate+"天");
        docManager.save(newSetmeal);
		//新建套餐
        
		Document doc = docManager.newDocument(docLibO.getDocLibID());		
		String num = sdf1.format(new Date());
		String orderNum = new String(num);
		doc.setDeleteFlag(0);
		doc.setFolderID(docLibO.getFolderID());
		doc.set("orderNum", orderNum);
		doc.set("total", document[0].getString("setMealMoney"));
		doc.set("userName", docO[0].getString("userName"));//userName
		doc.set("realName", docO[0].getString("realName"));
		doc.set("mobile", docO[0].getString("mobile"));
		doc.set("mail", docO[0].getString("mail"));						
		doc.set("createTime", df.format(new Date()));  //创建时间
		doc.set("orderStatus", docO[0].getString("orderStatus"));
		doc.set("payStatus", 0);
		doc.set("payWay", docO[0].getString("payWay"));
		doc.set("payWay_ID",docO[0].getString("payWay_ID"));//下拉3
		doc.set("invoice", docO[0].getString("invoice"));  //发票
		doc.set("setMealName", document[0].getString("setMealNmae"));						
		doc.set("setMealID", newSetmeal.getString("SYS_DOCUMENTID"));
		doc.set("orderSource", docO[0].getString("orderSource"));
		doc.set("operator", docO[0].getString("operator"));
		doc.set("m_siteID", siteID);
		docManager.save(doc);
		obj.put("code", "1000");
		obj.put("msg", "续订成功");
	}else{
		obj.put("code", "1001");
		obj.put("msg", "没有可用订单");
	}
	outputJson(String.valueOf(obj), response);
	return ;
  }
  
//查询两个日期之间的天数
	public int days(String start, String end) throws E5Exception {
		
		String sql="SELECT (select datediff('"+end+"', '"+start+"')) AS tian ";
		DBSession dbSession = null;
		IResultSet rs = null;
		int days = 0;
			
		try {
			dbSession = Context.getDBSession();
			rs = dbSession.executeQuery(sql);
			while(rs.next()){	
				days=rs.getInt("tian");
			}	
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(dbSession);
		}
		return days;
	}
  
	/**
	 * 根据分类类型名称、文档类型字段名称、文档类型ID获取分类选项ID
	 * 
	 * @param catElementName
	 * @param docTypeFieldName
	 * @param docTypeId
	 * @return
	 */
	@SuppressWarnings("finally")
	public int getCatElementId(String catElementName, String docTypeFieldName, int docTypeId) {

		String sql = "SELECT entry_id FROM  category_other WHERE wt_type=(select options from dom_doctypefields where doctypeid="
				+ docTypeId + " and columncode='" + docTypeFieldName + "')  and entry_name='" + catElementName + "'";
		DBSession dbSession = null;
		IResultSet rs = null;
		String options = null;
		try {
			dbSession = com.founder.e5.context.Context.getDBSession();
			rs = dbSession.executeQuery(sql);
			while (rs.next()) {
				options = rs.getString(1);
			}
		} catch (E5Exception e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(dbSession);
			return Integer.parseInt(options);
		}
	}
	
	@SuppressWarnings("finally")
	public int getCatElementId(String catElementName) throws E5Exception{
		
		CatReader reader = (CatReader)com.founder.e5.context.Context.getBean("CatReader");
		Category[] cats = reader.getCats("支付方式");
		for(int i=0;i<cats.length;i++){
			//String catElementName = "免费";
			if(catElementName.equals(cats[i].getCatName())){
				return cats[i].getCatID();
			}
		}
		return 0;
		
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
	
	/*
	 * 支付宝电脑网站同步通知接口
	 */
	@RequestMapping("/alipayPc.do")
	  public void  alipayPc(HttpServletRequest request, HttpServletResponse response,@SuppressWarnings("rawtypes") Map model) throws IOException,E5Exception{
		  response.setHeader("Access-Control-Allow-Origin", "*");
		  response.setContentType("text/html;charset=UTF-8");
		  
		  String body=request.getParameter("body");
		  String buyer_email=request.getParameter("buyer_email");
		  String buyer_id=request.getParameter("buyer_id");
		  String exterface=request.getParameter("exterface");
		  String is_success=request.getParameter("is_success");
		  String notify_id=request.getParameter("notify_id");
		  
		  String out_trade_no=request.getParameter("out_trade_no");
		  String payment_type=request.getParameter("payment_type");
		  String trade_no=request.getParameter("trade_no");
		  
		  
		  
		  String trade_status=request.getParameter("trade_status");
		  String extra_common_param=request.getParameter("extra_common_param");
		  String [] common_param = extra_common_param.split("-");
		  String uid = common_param[0];
		  String siteID = common_param[1];
		  String curdate = common_param[2];
		  String paperLayout = common_param[3];
		  if(trade_status!=null && trade_status.equals("TRADE_SUCCESS")){
			  DocumentManager docManager = DocumentManagerFactory.getInstance();
			  DocLib docLib = LibHelper.getLib(DocTypes.MEMBERORDERS.typeID(), "xy");
			  String sql = " SYS_DELETEFLAG = 0 and orderNum = ? ";
			  Document[] doc = docManager.find(docLib.getDocLibID(), sql,new Object[] { out_trade_no });
			  if (doc != null && doc.length > 0) {
			      doc[0].set("payStatus", 1);
			      doc[0].set("orderStatus", 1);
			      docManager.save(doc[0]);
			  }
		  }
		  JSONObject obj = new JSONObject();
		obj.put("url",
				BaseHelper.getConfig("翔宇CMS", "互动", "外网资源地址") + "/amuc/pcColfees/finishOrder.html?uid=" + uid
						+ "&orderNum=" + out_trade_no + "&siteID=" + siteID  + "&curdate=" + curdate
						+ "&paperLayout=" + paperLayout);
		  outputJson(String.valueOf(obj), response);
	  }
	/*
	 * 支付宝手机同步通知接口
	 */
	@RequestMapping("/alipayApp.do")
	public void alipayApp(HttpServletRequest request, HttpServletResponse response,
			@SuppressWarnings("rawtypes") Map model) throws IOException, E5Exception {
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/html;charset=UTF-8");
		String out_trade_no = request.getParameter("out_trade_no");

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib docLib = LibHelper.getLib(DocTypes.MEMBERORDERS.typeID(), "xy");
		String sql = " SYS_DELETEFLAG = 0 and orderNum = ? ";
		Document[] doc = docManager.find(docLib.getDocLibID(), sql, new Object[] { out_trade_no });
		if (doc != null && doc.length > 0) {
			doc[0].set("payStatus", 1);
			doc[0].set("orderStatus", 1);
			docManager.save(doc[0]);
		}
		String moblie = doc[0].getString("mobile");
		String siteID = doc[0].getString("m_siteID");
		DocLib member = LibHelper.getLib(DocTypes.MEMBER.typeID(), "xy");
		Document[] docm = docManager.find(member.getDocLibID(), " SYS_DELETEFLAG = 0 and mMobile = ? ", new Object[] { moblie });
        String uid = docm[0].getString("uid_sso");
		JSONObject obj = new JSONObject();
		obj.put("url", BaseHelper.getConfig("翔宇CMS", "互动", "外网资源地址") + "/amuc/colfees/finishOrderApp.html?uid="
				+ uid + "&orderNum=" + out_trade_no + "&siteID=" + siteID);
		outputJson(String.valueOf(obj), response);
	}
	
	/**
	 * 支付宝手机网站支付接口
	 */
	@RequestMapping("/alipayAppPay.do")
	  public void pay(HttpServletRequest request, HttpServletResponse httpResponse,
	      @SuppressWarnings("rawtypes") Map model) {
		//获取业务参数
	    String orderNum = request.getParameter("orderNum");
	    String mealName = request.getParameter("mealName");
	    String mealMoney = request.getParameter("mealMoney");
	    String uid = request.getParameter("uid");
	    
	    AlipayAppConfig alipayAppConfig = new AlipayAppConfig();
	    String url = "https://openapi.alipay.com/gateway.do";
	    AlipayClient alipayClient = new DefaultAlipayClient(url, AlipayAppConfig.app_id,
	    		AlipayAppConfig.app_private_key, "json", AlipayAppConfig.charset, AlipayAppConfig.alipay_public_key, AlipayAppConfig.sign_type); // 获得初始化的AlipayClient
	    AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();// 创建API对应的request
	    alipayRequest.setReturnUrl(alipayAppConfig.return_url);
	    alipayRequest.setNotifyUrl(alipayAppConfig.notify_url);// 在公共参数中设置回跳和通知地址
	    JSONObject jsonobject=new JSONObject();
	    jsonobject.put("out_trade_no", orderNum);
	    jsonobject.put("total_amount", mealMoney);
	    jsonobject.put("subject", mealName);
	    jsonobject.put("seller_id", AlipayAppConfig.seller_id);
	    jsonobject.put("product_code", "QUICK_WAP_PAY");
	    jsonobject.put("passback_params", uid);
		alipayRequest.setBizContent(String.valueOf(jsonobject));
	    String form;
	    try {
	      form = alipayClient.pageExecute(alipayRequest).getBody();
	      httpResponse.setContentType("text/html;charset=utf-8");
	      httpResponse.getWriter().write(form);// 直接将完整的表单html输出到页面
	      httpResponse.getWriter().flush();// 调用SDK生成表单
	    } catch (AlipayApiException e) {
	      e.printStackTrace();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }

	  }
	
	/**
	 * 支付电脑网站支付接口
	 * @param WIDout_trade_no
	 * @param WIDsubject
	 * @param WIDtotal_fee
	 * @param WIDbody
	 * @param extra_common_param
	 * @param request
	 * @param response
	 */
	@RequestMapping("/alipayapi.do")
	public void alipayapi( HttpServletRequest request, HttpServletResponse httpResponse,
			@SuppressWarnings("rawtypes") Map model) throws IOException {
		String out_trade_no = request.getParameter("WIDout_trade_no");

		// 订单名称，必填
		String subject = request.getParameter("WIDsubject");

		// 付款金额，必填
		String total_fee = request.getParameter("WIDtotal_fee");

		// 商品描述，可空
		String body = request.getParameter("WIDbody");
		// 商品描述，可空
		String extra_common_param = request.getParameter("extra_common_param");
		AlipayConfig alipayConfig = new AlipayConfig();
		Map<String, String> sParaTemp = new HashMap<String, String>();
		sParaTemp.put("service", AlipayConfig.service);
		sParaTemp.put("partner", AlipayConfig.partner);
		sParaTemp.put("seller_id", AlipayConfig.seller_id);
		sParaTemp.put("_input_charset", AlipayConfig.input_charset);
		sParaTemp.put("payment_type", AlipayConfig.payment_type);
		sParaTemp.put("notify_url", alipayConfig.notify_url);
		sParaTemp.put("return_url", alipayConfig.return_url);
		sParaTemp.put("anti_phishing_key", AlipayConfig.anti_phishing_key);
		sParaTemp.put("exter_invoke_ip", AlipayConfig.exter_invoke_ip);
		sParaTemp.put("out_trade_no", out_trade_no);
		sParaTemp.put("subject", subject);
		sParaTemp.put("total_fee", total_fee);
		sParaTemp.put("body", body);
		sParaTemp.put("extra_common_param", extra_common_param);
		// 其他业务参数根据在线开发文档，添加参数.文档地址:https://doc.open.alipay.com/doc2/detail.htm?spm=a219a.7629140.0.0.O9yorI&treeId=62&articleId=103740&docType=1
		// 如sParaTemp.put("参数名","参数值");

		// 建立请求
		String sHtmlText = AlipaySubmit.buildRequest(sParaTemp, "get", "确认");
		System.out.println(extra_common_param);
		System.out.println(sHtmlText);
		httpResponse.setContentType("text/html;charset=utf-8");
		httpResponse.getWriter().write(sHtmlText);// 直接将完整的表单html输出到页面
		httpResponse.getWriter().flush();// 调用SDK生成表单

	}
	
	@RequestMapping("/getPcNoMsg.do")
	public void getPcNoMsg(HttpServletRequest request, HttpServletResponse response,
			@SuppressWarnings("rawtypes") Map model) throws Exception {
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/html;charset=UTF-8");
		String uid = request.getParameter("uid");
		int siteID = Integer.parseInt(request.getParameter("siteID"));
		JSONObject obj = new JSONObject();
		JSONObject pcobj = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib docLibM = LibHelper.getLib(DocTypes.MEMBER.typeID(), "xy");
		Document[] docM = docManager.find(docLibM.getDocLibID(), " SYS_DELETEFLAG = 0 and uid_sso = ? and m_siteID = ? ",
				new Object[] { uid,siteID });
		if (docM != null && docM.length > 0) {
			DocLib docLibPapercard = LibHelper.getLib(DocTypes.MEMBERPAPERCARD.typeID(), "xy");
			String sqlpapercard = "SYS_DELETEFLAG = 0 and pcMember_ID = ? and pcActiveStatus = '激活' and m_siteID = ? ";
			Document[] docpapercard = docManager.find(docLibPapercard.getDocLibID(), sqlpapercard,
					new Object[] { docM[0].get("SYS_DOCUMENTID"),siteID });
			if (docpapercard != null && docpapercard.length > 0) {
				for (int i = 0;i<docpapercard.length;i++){
					List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
					pcobj.put("pcNo", docpapercard[i].get("pcNo"));
					pcobj.put("pcPassword", docpapercard[i].get("pcPassword"));
					pcobj.put("pcPaperName", docpapercard[i].get("pcPaperName"));
					pcobj.put("pcExpiryDate", docpapercard[i].get("pcExpiryDate"));
					pcobj.put("pcEffectTime", docpapercard[i].getString("pcEffectTime"));
					pcobj.put("pcExpireTime", docpapercard[i].getString("pcExpireTime"));
					pcobj.put("activeTime", docpapercard[i].getString("activeTime"));
					jsonArray.add(pcobj);
				}
				obj.put("code", "1000");
				obj.put("msg", "获取报卡信息成功");
				obj.put("data", jsonArray);
				outputJson(String.valueOf(obj), response);
				return;
			} else {
				obj.put("code", "1001");
				obj.put("msg", "会员没有激活报卡");
				outputJson(String.valueOf(obj), response);
				return;
			}
		}

		obj.put("code", "1002");
		obj.put("msg", "会员不存在");
		outputJson(String.valueOf(obj), response);
	}

}
