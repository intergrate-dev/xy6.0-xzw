/*package com.founder.amuc.order;

import java.io.IOException;
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








import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeWapPayRequest;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
import com.founder.amuc.commons.Constant;
import com.founder.amuc.commons.InfoHelper;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class OrdersManager {
	
	*//**
	 * 生成订单
	 * @return
	 * @throws E5Exception 
	 *//*
	public String create(String meal, String users, String pay,String operator,String total, String orderSource , String tenantcode) throws E5Exception {
		
		JSONObject obj = new JSONObject();
		JSONObject mealsObj = JSONObject.fromObject(meal);
		JSONArray myJsonArray = JSONArray.fromObject(users);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_ORDERS,tenantcode);
		for(int i=0;i<myJsonArray.size();i++){
			
			SimpleDateFormat sdf1 = new SimpleDateFormat( "yyyyMMddhhmmss" );
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String num = sdf1.format(new Date());
			//String init = "000000";
			//init = String.valueOf(increStrsys(init));		
			//String orderNum = new String(num+init);
			String orderNum = new String(num);
			Document mDoc = getMemberBymobile(myJsonArray.getJSONObject(i).getString("mobile"), tenantcode);
			Document doc = docManager.newDocument(docLib.getDocLibID(),InfoHelper.getID(docLib.getDocTypeID()));
			doc.setDeleteFlag(0);
			doc.setFolderID(docLib.getFolderID());
			doc.set("orderNum", orderNum);
			doc.set("total", total);
			doc.set("userName", mDoc.getString("mName"));//userName
			doc.set("realName", myJsonArray.getJSONObject(i).getString("realName"));
			doc.set("mobile", myJsonArray.getJSONObject(i).getString("mobile"));
			doc.set("mail", mDoc.getString("mEmail"));						
			doc.set("createTime", df.format(new Date()));  //创建时间
			doc.set("orderStatus", "已提交");
			doc.set("payStatus", "未支付");
			doc.set("payWay", pay);
			doc.set("invoice", "未开");  //发票
			doc.set("setMealName", mealsObj.getString("setMealName"));						
			doc.set("setMealID", mealsObj.getString("setMealID"));
			doc.set("orderSource", StringUtils.isBlank(orderSource)?"系统内创建":orderSource);						
			doc.set("operator", operator);
			
			docManager.save(doc);
			obj.put("code", "1003");
			obj.put("msg", "生成订单成功");
		}
		return obj.toString();
	}
	
	private char[] increStrsys(String codestr) {
		if (codestr != null && codestr.length() > 0) {
			
			char[] charArray = codestr.toCharArray();
			AtomicInteger z = new AtomicInteger(0);
			for (int i = charArray.length - 1; i > -1; i--) {
				if (charArray[i] == '9' ) {
					z.set(z.incrementAndGet());
				} else {
					if (z.intValue() > 0 || i == charArray.length - 1) {
						
						AtomicInteger atomic = new AtomicInteger(charArray[i]);
						charArray[i] = (char) atomic.incrementAndGet();
						z.set(0);
						for(int j = charArray.length - 1; j >= i+1;j--){
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
	
	public String checkuser(String mobile, String tenantcode) throws E5Exception {
		JSONObject obj = new JSONObject();
		Document mDoc = getMemberBymobile(mobile, tenantcode);
		if( mDoc == null){
			obj.put("code", "1001");
			obj.put("msg", "不存在该账户");
			return obj.toString();
		}
		if( mDoc != null && mDoc.getInt("mStatus") == 0 ){
			obj.put("code", "1002");
			obj.put("msg", "用户被禁用");
			return obj.toString();
		}
		obj.put("code", "1003");
		obj.put("mail", mDoc.get("mEmail"));
		obj.put("userName", mDoc.get("mName"));
		obj.put("msg", "用户存在");
		return obj.toString();
	}
	
	*//**
	 * 根据手机查询会员
	 * 返回：会员members对象
	 * @param uid
	 * @param tenantcode
	 * @return
	 * @throws E5Exception
	 *//*
	private Document getMemberBymobile(String mMobile, String tenantcode) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib mdocLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER, tenantcode);
		// 查询系统中是否存在uid的会员
		String mcondition = "mMobile = ? and SYS_DELETEFLAG = 0";
		String[] column = { "mName", "mMobile", "mStatus", "mEmail"};
		Document[] members = docManager.find(mdocLib.getDocLibID(), mcondition,new Object[] { mMobile }, column);
		if (members != null && members.length > 0) {
			return members[0];
		}else{
			return null;
		}
	}
	
	public String getMsg(String orderNum, String docid, HttpServletRequest request, String tenantcode) throws E5Exception {
		
	    JSONObject obj1 = new JSONObject();
	    JSONObject obj = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_ORDERS,tenantcode);
		String sql = "";
		Document[] doc;
		if(StringUtils.isBlank(docid)){
			sql = " SYS_DELETEFLAG = 0 and orderNum = ? ";
			doc = docManager.find(docLib.getDocLibID(), sql, new Object[]{orderNum});
		}else{
			sql = " SYS_DELETEFLAG = 0 and SYS_DOCUMENTID = ? ";
			doc = docManager.find(docLib.getDocLibID(), sql, new Object[]{docid});
		}
		//Document[] doc = docManager.find(docLib.getDocLibID(), " SYS_DELETEFLAG = 0 and SYS_DOCUMENTID = ? ", new Object[]{docid});
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
			return obj.toString();
		}
		obj.put("code", "1001");
		obj.put("msg", "不存在该账户");
		return obj.toString();
	}
	
	public String cancel(String orderNum, String docid, HttpServletRequest request, String tenantcode) throws E5Exception {
		
		JSONObject obj = new JSONObject();
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_ORDERS,tenantcode);
		String sql = "";
		Document[] doc;
		if(StringUtils.isBlank(docid)){
			sql = " SYS_DELETEFLAG = 0 and orderNum = ? ";
			doc = docManager.find(docLib.getDocLibID(), sql, new Object[]{orderNum});
		}else{
			sql = " SYS_DELETEFLAG = 0 and SYS_DOCUMENTID = ? ";
			doc = docManager.find(docLib.getDocLibID(), sql, new Object[]{docid});
		}
		
		if (doc != null && doc.length > 0) {		
			doc[0].set("orderStatus", "已取消");
			docManager.save(doc[0]);
			obj.put("code", "1000");
			obj.put("msg", "取消订单成功");
			return obj.toString();
		}
		obj.put("code", "1001");
		obj.put("msg", "取消订单失败");
		return obj.toString();
	}
	
	public String del(String orderNum, String docid, HttpServletRequest request, String tenantcode) throws E5Exception {
		
		JSONObject obj = new JSONObject();
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_ORDERS,tenantcode);
		String sql = "";
		Document[] doc;
		if(StringUtils.isBlank(docid)){
			sql = " SYS_DELETEFLAG = 0 and orderNum = ? ";
			doc = docManager.find(docLib.getDocLibID(), sql, new Object[]{orderNum});
		}else{
			sql = " SYS_DELETEFLAG = 0 and SYS_DOCUMENTID = ? ";
			doc = docManager.find(docLib.getDocLibID(), sql, new Object[]{docid});
		}
		
		if (doc != null && doc.length > 0) {		
			doc[0].set("SYS_DELETEFLAG", 1);
			docManager.save(doc[0]);
			obj.put("code", "1000");
			obj.put("msg", "删除订单成功");
			return obj.toString();
		}
		obj.put("code", "1001");
		obj.put("msg", "删除订单失败");
		return obj.toString();
	}

	public String getMessages(String uid, String orderNum, HttpServletRequest request, String tenantcode) throws E5Exception {
	
	    JSONObject obj1 = new JSONObject();
	    JSONObject obj = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib docLibM = InfoHelper.getLib(Constant.DOCTYPE_MEMBER,tenantcode);
		Document[] docM = docManager.find(docLibM.getDocLibID(), " SYS_DELETEFLAG = 0 and uid_sso = ? ", new Object[]{uid});
		if (docM != null && docM.length > 0) {
			DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_ORDERS,tenantcode);
			String sql;
			Document[] doc;
			if(StringUtils.isBlank(orderNum)){
				sql = " SYS_DELETEFLAG = 0 and mobile = ? order by createTime desc ";
				doc = docManager.find(docLib.getDocLibID(), sql, new Object[]{docM[0].get("mMobile")});
			}else{
				sql = " SYS_DELETEFLAG = 0 and orderNum=? and mobile = ? order by createTime desc ";
				doc = docManager.find(docLib.getDocLibID(), sql, new Object[]{orderNum,docM[0].get("mMobile")});
			}	
			if (doc != null && doc.length > 0) {	
				for(int i=0;i<doc.length;i++){
					obj1.put("orderNum", doc[i].get("orderNum"));	
					obj1.put("createTime", doc[i].getString("createTime"));
					obj1.put("realName", doc[0].get("realName"));
					obj1.put("mobile", doc[0].get("mobile"));
					obj1.put("payStatus", doc[i].get("payStatus"));
					obj1.put("orderStatus", doc[i].get("orderStatus"));
					obj1.put("payWay", doc[0].get("payWay"));
					obj1.put("invoice", doc[0].get("invoice"));
					obj1.put("total", doc[i].get("total"));
					JSONObject map = OrdersManager.FindSetMeal(doc[i].getString("setMealID"),tenantcode);
					obj1.put("setMeal", map.get("data"));
					jsonArray.add(obj1);
				}	
				obj.put("code", "1000");
				obj.put("msg", "获取订单信息成功");
				obj.put("data", jsonArray);
				return obj.toString();
			}else{
				obj.put("code", "1001");
				obj.put("msg", "会员没有订单");
				return obj.toString();
			}
		}
		obj.put("code", "1002");
		obj.put("msg", "会员不存在");	
		return obj.toString();
	}
	
	public static JSONObject FindSetMeal(String ids, String tenantcode) throws E5Exception{

		JSONObject map = new JSONObject();
		map.put("code", 0);
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_SETMEAL,tenantcode);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			if (ids != null && ids.trim() != "") {
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				for (String id : ids.split(",")) {
					Document document = docManager.get(docLib.getDocLibID(),
							Long.parseLong(id));
					if (document != null) {
						Map<String, Object> doc = new HashMap<String, Object>();
						doc.put("docid", document.getDocID());
						doc.put("setMealNmae",
								document.getString("setMealNmae"));
						doc.put("paperNumber",
								document.getString("paperNumber"));
						doc.put("setMealMoney",
								document.getString("setMealMoney"));
						doc.put("EffectTime", document.getString("EffectTime"));
						doc.put("ExpireTime", document.getString("ExpireTime"));
						doc.put("expiryDate", document.getString("expiryDate"));
						doc.put("setMealContent",
								document.getString("setMealContent"));
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
	
	public String regain(String orderNum, String docid, HttpServletRequest request, String tenantcode) throws E5Exception {
		
		JSONObject obj = new JSONObject();
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_ORDERS,tenantcode);
		String sql = "";
		Document[] doc;
		if(StringUtils.isBlank(docid)){
			sql = " SYS_DELETEFLAG = 0 and orderNum = ? ";
			doc = docManager.find(docLib.getDocLibID(), sql, new Object[]{orderNum});
		}else{
			sql = " SYS_DELETEFLAG = 0 and SYS_DOCUMENTID = ? ";
			doc = docManager.find(docLib.getDocLibID(), sql, new Object[]{docid});
		}
		
		if (doc != null && doc.length > 0) {		
			doc[0].set("orderStatus", "已提交");
			docManager.save(doc[0]);
			obj.put("code", "1000");
			obj.put("msg", "重新下单成功");
			return obj.toString();
		}
		obj.put("code", "1001");
		obj.put("msg", "重新下单失败");
		return obj.toString();
	}

	public void pay(String orderNum,String mealName,String mealMoney, String docid, String tenantcode,
			HttpServletRequest request, HttpServletResponse httpResponse) {
		String URL="https://openapi.alipaydev.com/gateway.do";
		String APP_ID="2016080100143490";
		String APP_PRIVATE_KEY="MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCPUyLcCpWQGEMxr949rLkLaQ3Yvf+W/8khSZqHMzCJBTYJXh7EPxfhheGC8jijz5uLb5NO5MPhxH6njGYabNj7tPcP8MCwWFzF3SUO79uEPUuJBxB3lJs6Pp80Q+3MG0kWaNfX2NGATeTgQis4d4XzSnJ9X4mRAKeABCsYgf/GJDTTNNX5sN3UVWRJIj6+BnpazokhDo8sCPuTUOfgk7sl65PHxqgiHkNNn4YT7RbS85lBGKJz5oMtfu7cBvwy9ZCXQISgOzwmeDygcP0ei5Z7o2VE0D/A9RJgyCj2NJH/ThKRGBGl4C7BtJwY1aRKRUAawYbyCjzGvUqyo0m+WAivAgMBAAECggEABpz/bsfOPuffsds7lfzyTOR8DxQ1RvJmhXKv/q0OAESNScFeChAfOfjDfxdOJcDenIB5e5pyr2gxr+l6jJ2ZJYQkIkgpm/l3zml13EqVBnUNlzUMPXhN8VISZob74f8gUDhmPT9z60es1+w1slVRiPhWcBKIdnhhQjbvBiJ5etr/J5tsbTvHa/XsEWndTXH2rOjHJH7kBP4kMWHqXubmOdPfzvmkKqbspzfzQ6TAI9/St6o0pyloDgK2GtRD9vlEDZO/si7V25OXjzlUitCUW+e5lh/Uydvk3KPB6f9NrTPOlf5MlxzUjw8TKC+CW5atZ/WQEBmUS2+YIu00GOeSgQKBgQDAWdSZU3jBuXmi/+UCAeA1fEvUHSZotDf5lszVWUt4R0UKxJm27JuJsf9NnOPz6matLJ+1YgTRv9PIv3dpgxfeSMPjxjW7PznxVhA/sK42rXR9AvwBuBZ5qfXS0QWBnUpK88eXoNr+ct6nLg6Lj8dkdahXQE/Wn3jcCDrnEA0ipQKBgQC+wETOM78qWWPhLErkEuZCQW9mr2rQzlJDCuX0qQmFLbX5Mchxhddwn0y1c6XwD9WBy5cJZgZUsdYk9xyK9ojIwPVo0KoSTr1Z851fnjFsSyDluu/DTz7fx7NONVWx3SX/rHo/UW3Z99cfpXj/aHT4NGx8a9Ud8MIZHnFFuCQBwwKBgQCQb+QQ7nkzH0+TcoxIoZf3EcrvEKSS4yFVLkPJwS/Gtd3GZDL5BVsWXq7TXQhfVJidWXDkByKPTnh9uii09lep9wIBvAkE/klIS9QPv2BSdpWOefs6Xz2hRlrtXz+/QJlVBxHNbmCDyUYgS5loeyLC8Qbj3csV4tIih5uTfp4ecQKBgGetj3hYbe4xufT4oxgGIbsfX9J7Q96MVe/0q3poqjEF4GO6qEzzx16CZyo02pt5r72lj8le98/u/QsIeS4aw8wB/SFkURw0SfzrSb1f+VL4HsBe89S/bNgq7g1zNcm//thRwfxKYR/Y8sdTpEmGWBVUQSb7YVGtu4H1pvGTHZcLAoGAbQ9lrr0YuljmlG8O6rxeSKUeqTYziGl8vhGSxzQu/IUlpIIlXdzyLOwOuFkV1X53tpc7jkzRCB+VHCfDcR8BhPiZ5+RjqYrR/h00x62+wPoN/bNYQ5SyjehttzOfdQ9AtbHBXxDfIviXprp1hpogGQFOt2yEka+9OZRiwOAiyuE=";
		String ALIPAY_PUBLIC_KEY="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAj1Mi3AqVkBhDMa/ePay5C2kN2L3/lv/JIUmahzMwiQU2CV4exD8X4YXhgvI4o8+bi2+TTuTD4cR+p4xmGmzY+7T3D/DAsFhcxd0lDu/bhD1LiQcQd5SbOj6fNEPtzBtJFmjX19jRgE3k4EIrOHeF80pyfV+JkQCngAQrGIH/xiQ00zTV+bDd1FVkSSI+vgZ6Ws6JIQ6PLAj7k1Dn4JO7JeuTx8aoIh5DTZ+GE+0W0vOZQRiic+aDLX7u3Ab8MvWQl0CEoDs8Jng8oHD9HouWe6NlRNA/wPUSYMgo9jSR/04SkRgRpeAuwbScGNWkSkVAGsGG8go8xr1KsqNJvlgIrwIDAQAB";
		String CHARSET="UTF-8";
		String SIGN_TYPE="RSA2";
		AlipayClient alipayClient = new DefaultAlipayClient(URL, APP_ID, APP_PRIVATE_KEY, "json", CHARSET, ALIPAY_PUBLIC_KEY, SIGN_TYPE); //获得初始化的AlipayClient
		AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();//创建API对应的request
		alipayRequest.setReturnUrl("http://172.19.33.38:9081/amucsite/colfees/finishOrderApp.html");
		alipayRequest.setNotifyUrl("http://172.19.33.38:9081/amucsite/colfees/finishOrderApp.html");//在公共参数中设置回跳和通知地址
		alipayRequest.setBizContent("{" +
				"\"out_trade_no\":\""+orderNum+"\"," +
				"\"total_amount\":\""+mealMoney+"\"," +
				"\"subject\":\""+mealName+"\"," +
				"\"seller_id\":\"2088102169494978\"," +
				"\"product_code\":\"QUICK_WAP_PAY\"" +
				"}");//填充业务参数
		String form;
		try {
			form = alipayClient.pageExecute(alipayRequest).getBody();
			httpResponse.setContentType("text/html;charset=utf-8");
			httpResponse.getWriter().write(form);//直接将完整的表单html输出到页面
			httpResponse.getWriter().flush();
		} catch (AlipayApiException e) {
			e.printStackTrace();
		} //调用SDK生成表单
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String judge(String ssoid, String paperName, HttpServletRequest request, String tenantcode) throws E5Exception {
		
		JSONObject obj1 = new JSONObject();
	    JSONObject obj = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib docLibM = InfoHelper.getLib(Constant.DOCTYPE_MEMBER,tenantcode);
		Document[] docM = docManager.find(docLibM.getDocLibID(), " SYS_DELETEFLAG = 0 and uid_sso = ? ", new Object[]{ssoid});
		if (docM != null && docM.length > 0) {
			DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_ORDERS,tenantcode);
			String sql;
			Document[] doc;
			sql = " SYS_DELETEFLAG = 0 and mobile = ? and orderStatus='已提交' and payStatus='已支付'  ";
			doc = docManager.find(docLib.getDocLibID(), sql, new Object[]{docM[0].get("mMobile")});
			if (doc != null && doc.length > 0) {	
				for(int i=0;i<doc.length;i++){
					boolean result = OrdersManager.FindSetMeal1(doc[i].getString("setMealID"),paperName,tenantcode);
					if(result){
						obj.put("code", "1000");
						obj.put("msg", "报纸可用");
						return obj.toString();							
					}
				}
				obj.put("code", "1001");
				obj.put("msg", "报纸不可用");
				return obj.toString();
			}else{
				obj.put("code", "1001");
				obj.put("msg", "会员没有可用订单");
				return obj.toString();
			}
		}
		obj.put("code", "1001");
		obj.put("msg", "会员不存在");	
		return obj.toString();
	}
	public static boolean FindSetMeal1(String ids, String paperName, String tenantcode) throws E5Exception{

		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_SETMEAL,tenantcode);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		JSONArray list = new JSONArray();
		boolean result = false;
		try {
			if (ids != null && ids.trim() != "") {
				for (String id : ids.split(",")) {

					String sql = " SYS_DELETEFLAG = 0 and SYS_DOCUMENTID = ? and date_format(now(),'%y-%m-%d') <= ExpireTime and  date_format(now(),'%y-%m-%d') >= EffectTime and setMealContent LIKE '%"+paperName+"%' ";
					Document[] document = docManager.find(docLib.getDocLibID(), sql, new Object[]{Long.parseLong(id)});

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
}
*/