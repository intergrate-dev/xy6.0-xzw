package com.founder.amuc.order;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.founder.amuc.commons.InfoHelper;
import com.founder.e5.cat.CatReader;
import com.founder.e5.cat.Category;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.e5.web.BaseController;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.LibHelper;
import com.founder.amuc.commons.DateFormatAmend;
@Controller
@RequestMapping("/amuc/order")
public class OrdersController extends BaseController {
	
	protected void handle(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception {

		String action = get(request, "a");
		if ("detailInit".equals(action)) {
			// 订单详情页面初始化
			detailInit(request, response, model);
		}
	}
	/**
	 * 生成订单
	 * @return
	 * @throws E5Exception 
	 */
	@RequestMapping("/create.do")
	public void create(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception {
		String meal = get(request, "meal");
		String users = get(request, "users");
		String pay = get(request, "pay");
		String operator = get(request, "operator");
		String total = get(request, "total");
		String orderSource = get(request, "orderSource");
		int siteID = Integer.parseInt(request.getParameter("siteID"));
		JSONObject obj = new JSONObject();
		JSONObject mealsObj = JSONObject.fromObject(meal);
		JSONArray myJsonArray = JSONArray.fromObject(users);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib docLib = LibHelper.getLib(DocTypes.MEMBERORDERS.typeID(),"xy");
		for(int i=0;i<myJsonArray.size();i++){
			
			SimpleDateFormat sdf1 = new SimpleDateFormat( "yyyyMMddhhmmss" );
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String num = sdf1.format(new Date());
			String orderNum = new String(num);
			Document mDoc = getMemberBymobile(myJsonArray.getJSONObject(i).getString("mobile"),siteID, "xy");
			Document doc = docManager.newDocument(docLib.getDocLibID(),InfoHelper.getID(docLib.getDocTypeID()));
			doc.setDeleteFlag(0);
			doc.setFolderID(docLib.getFolderID());
			doc.set("m_siteID", siteID);
			doc.set("orderNum", orderNum);
			doc.set("total", total);
			doc.set("userName", mDoc.getString("mName"));//userName
			doc.set("realName", myJsonArray.getJSONObject(i).getString("realName"));
			doc.set("mobile", myJsonArray.getJSONObject(i).getString("mobile"));
			doc.set("mail", mDoc.getString("mEmail"));	
			doc.set("createTime", DateFormatAmend.timeStampDispose((df.format(new Date()))));  //创建时间
			if(pay.equals("免费")){
				doc.set("payStatus", 1);
				doc.set("orderStatus", 4);
			}else{
				doc.set("payStatus", 0);
				doc.set("orderStatus", 3);
			}
			doc.set("payWay", pay);
			doc.set("payWay_ID", getCatElementId(pay));
			doc.set("invoice", 0);  //发票
			doc.set("setMealName", mealsObj.getString("setMealName"));						
			doc.set("setMealID", mealsObj.getString("setMealID"));
			doc.set("orderSource", StringUtils.isBlank(orderSource) ? 0 : Integer.parseInt(orderSource));		
			doc.set("operator", operator);
			
			docManager.save(doc);
			obj.put("code", "1003");
			obj.put("msg", "生成订单成功");
		}
		output(obj.toString(), response);
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
	
	@RequestMapping("/checkuser.do")
	public void checkuser(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception {
		String mobile = get(request, "mobile");
		int siteID = Integer.parseInt(request.getParameter("siteID"));
		JSONObject obj = new JSONObject();
		Document mDoc = getMemberBymobile(mobile,siteID, "xy");
		if( mDoc == null){
			obj.put("code", "1001");
			obj.put("msg", "不存在该账户");
			//return obj.toString();
			output(obj.toString(), response);
			return;
		}
		if( mDoc != null && mDoc.getInt("mStatus") == 0 ){
			obj.put("code", "1002");
			obj.put("msg", "用户被禁用");
			//return obj.toString();
			output(obj.toString(), response);
			return;
		}
		obj.put("code", "1003");
		obj.put("mail", mDoc.get("mEmail"));
		obj.put("userName", mDoc.get("mName"));
		obj.put("msg", "用户存在");
		//return obj.toString();
		output(obj.toString(), response);
		return;
	}
	
	/**
	 * 根据手机查询会员
	 * 返回：会员members对象
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
		String[] column = { "mName", "mMobile", "mStatus", "mEmail"};
		Document[] members = docManager.find(mdocLib.getDocLibID(), mcondition,new Object[] { mMobile,siteID }, column);
		if (members != null && members.length > 0) {
			return members[0];
		}else{
			return null;
		}
	}
	@RequestMapping("/detailInit.do")
	private ModelAndView detailInit(HttpServletRequest request,
			HttpServletResponse response, @SuppressWarnings("rawtypes") Map model) throws Exception {

		DocLib docLib = LibHelper.getLib(DocTypes.MEMBERORDERS.typeID(),"xy");
		long docID = getInt(request, "DocIDs", 0);
		model.put("DocLibID", docLib.getDocLibID());
		model.put("DocIDs", docID);
		model.put("FVID", docLib.getFolderID());
		model.put("UUID", get(request, "UUID"));
		
		String VIEWNAME = "";
		VIEWNAME = "amuc/order/detail";  //订单详情页面
		
		model.put("@VIEWNAME@", VIEWNAME);// 跳转到jsp页面
		return new ModelAndView(viewName,model);//跳转到jsp页面
	}
	
	@RequestMapping("/cancel.do")
	public void cancel(HttpServletRequest request,
			HttpServletResponse response, @SuppressWarnings("rawtypes") Map model) throws Exception {
		
		String orderNum = get(request, "orderNum");
		String docid = get(request, "docid");
		JSONObject obj = new JSONObject();
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib docLib = LibHelper.getLib(DocTypes.MEMBERORDERS.typeID(),"xy");
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
			doc[0].set("orderStatus", 2);
			/*doc[0].set("orderStatus_ID", getCatElementId(doc[0].getString("orderStatus"),"orderStatus_ID", docLib.getDocTypeID()));//下拉1
*/			docManager.save(doc[0]);
			obj.put("code", "1000");
			obj.put("msg", "取消订单成功");
			output(obj.toString(), response);
		}
		obj.put("code", "1001");
		obj.put("msg", "取消订单失败");
		output(obj.toString(), response);
	}
	
	@RequestMapping("/getMsg.do")
	public void getMsg(HttpServletRequest request,
			HttpServletResponse response, @SuppressWarnings("rawtypes") Map model) throws Exception {
		
		String orderNum = get(request, "orderNum");
		String docid = get(request, "docid");
	    JSONObject obj1 = new JSONObject();
	    JSONObject obj = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib docLib = LibHelper.getLib(DocTypes.MEMBERORDERS.typeID(),"xy");
		DocLib doclibpay = LibHelper.getLib(DocTypes.PAYLOG.typeID(),"xy");
		String sql = "";
		String sqlpay = "";
		Document[] doc;
		Document[] docpay;
		if(StringUtils.isBlank(docid)){
			sql = " SYS_DELETEFLAG = 0 and orderNum = ? ";
			doc = docManager.find(docLib.getDocLibID(), sql, new Object[]{orderNum});
		}else{
			sql = " SYS_DELETEFLAG = 0 and SYS_DOCUMENTID = ? ";
			doc = docManager.find(docLib.getDocLibID(), sql, new Object[]{docid});
		}
		if (doc != null && doc.length > 0) {	
			obj1.put("orderNum", doc[0].get("orderNum"));
			orderNum = String.valueOf(doc[0].get("orderNum"));
			obj1.put("userName", doc[0].get("userName"));
			obj1.put("realName", doc[0].get("realName"));
			obj1.put("mobile", doc[0].get("mobile"));
			obj1.put("mail", doc[0].get("mail"));
			obj1.put("createTime", doc[0].getString("createTime"));
			obj1.put("orderStatus", doc[0].get("orderStatus"));
			obj1.put("payStatus", doc[0].get("payStatus"));			
			obj1.put("payWay", doc[0].get("payWay"));
			obj1.put("invoice", doc[0].get("invoice"));
			obj1.put("taxpayer", doc[0].get("taxpayer"));
			obj1.put("unitNum", doc[0].get("unitNum"));
			obj1.put("unitAddr", doc[0].get("unitAddr"));
			obj1.put("orderSource", doc[0].get("orderSource"));
			obj1.put("total", doc[0].get("total"));
			obj1.put("setMealID", doc[0].get("setMealID"));
			obj1.put("payTime", String.valueOf(doc[0].get("createTime")).substring(0, String.valueOf(doc[0].get("createTime")).length()-2));
			if(!String.valueOf(doc[0].get("payWay")).equals("免费")){
				sqlpay = " SYS_DELETEFLAG = 0 and payOrder = ? ";
				docpay = docManager.find(doclibpay.getDocLibID(), sqlpay, new Object[]{orderNum});
				if(docpay != null && docpay.length>0){
					obj1.put("payNumber",docpay[0].get("payNumber"));
					obj1.put("payType",docpay[0].get("payType"));
					obj1.put("payTime", String.valueOf(docpay[0].get("payTime")).substring(0,String.valueOf(docpay[0].get("payTime")).length()-2));
				}
			}
			jsonArray.add(obj1);
			
			obj.put("code", "1000");
			obj.put("msg", "获取订单信息成功");
			obj.put("data", jsonArray);
			output(obj.toString(), response);
		}
		obj.put("code", "1001");
		obj.put("msg", "不存在该账户");
		output(obj.toString(), response);
	}
	
	@RequestMapping("/regain.do")
	public void regain(HttpServletRequest request,
			HttpServletResponse response, @SuppressWarnings("rawtypes") Map model) throws Exception {
		
		String orderNum = get(request, "orderNum");
		String docid = get(request, "docid");
		JSONObject obj = new JSONObject();
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib docLib = LibHelper.getLib(DocTypes.MEMBERORDERS.typeID(),"xy");
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
			doc[0].set("orderStatus", 3);
			/*doc[0].set("orderStatus_ID", getCatElementId(doc[0].getString("orderStatus"),"orderStatus_ID", docLib.getDocTypeID()));//下拉1
*/			docManager.save(doc[0]);
			obj.put("code", "1000");
			obj.put("msg", "重新下单成功");
			output(obj.toString(), response);
		}
		obj.put("code", "1001");
		obj.put("msg", "重新下单失败");
		output(obj.toString(), response);
	}
	
	public void renew(HttpServletRequest request,
			HttpServletResponse response, @SuppressWarnings("rawtypes") Map model) throws Exception {
		
		String ssoid = get(request, "ssoid");
		String setmealid = get(request, "setmealid");
		DocLib docLib = LibHelper.getLib(DocTypes.MEMBERSETMEAL.typeID(),"xy");
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		String sql = " SYS_DELETEFLAG = 0 and SYS_DOCUMENTID = ? ";
		Document[] document = docManager.find(docLib.getDocLibID(), sql, new Object[]{setmealid});
		String sql2 ="";
		if(StringUtils.isBlank(document[0].getString("ExpireTime"))){
			sql2 = " SYS_DELETEFLAG = 0 and Status = '在售' and date_format(now(),'%y') = date_format(CreationTime,'%y') and setMealContent = ? and '"+document[0].getString("CreationTime")+"' < CreationTime ";
		}else{
			sql2 = " SYS_DELETEFLAG = 0 and Status = '在售' and date_format(now(),'%y') = date_format(CreationTime,'%y') and setMealContent = ? and '"+document[0].getString("ExpireTime")+"' < ExpireTime ";
		}
		
		Document[] document2 = docManager.find(docLib.getDocLibID(), sql2, new Object[]{document[0].getString("setMealContent")});
		JSONObject obj = new JSONObject();
		if(document2 != null && document2.length > 0){
			DocLib docLibM = LibHelper.getLib(DocTypes.MEMBER.typeID(),"xy");
			Document[] docM = docManager.find(docLibM.getDocLibID(), " SYS_DELETEFLAG = 0 and uid_sso = ? ", new Object[]{ssoid});
			DocLib docLibO = LibHelper.getLib(DocTypes.MEMBERORDERS.typeID(),"xy");
			String sqlO = " SYS_DELETEFLAG = 0 and mobile = ? and setMealID = ? and orderStatus='已提交' and payStatus='已支付'  ";
			Document[] docO = docManager.find(docLibO.getDocLibID(), sqlO, new Object[]{docM[0].get("mMobile"),setmealid});
			if(docO != null && docO.length > 0){
				Document doc = docManager.newDocument(docLibO.getDocLibID());
				SimpleDateFormat sdf1 = new SimpleDateFormat( "yyyyMMddhhmmss" );
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String num = sdf1.format(new Date());
				String orderNum = new String(num);
				doc.setDeleteFlag(0);
				doc.setFolderID(docLibO.getFolderID());
				doc.set("orderNum", orderNum);
				doc.set("total", document2[0].getString("setMealMoney"));
				doc.set("userName", docO[0].getString("userName"));//userName
				doc.set("realName", docO[0].getString("realName"));
				doc.set("mobile", docO[0].getString("mobile"));
				doc.set("mail", docO[0].getString("mail"));						
				doc.set("createTime", df.format(new Date()));  //创建时间
				doc.set("orderStatus", 3);
				doc.set("payStatus", 0);
				doc.set("payWay", docO[0].getString("payWay"));
				doc.set("payWay_ID", getCatElementId(docO[0].getString("payWay")));//下拉3
				doc.set("invoice", 0);  //发票
				doc.set("setMealName", document2[0].getString("setMealNmae"));						
				doc.set("setMealID", document2[0].getString("SYS_DOCUMENTID"));
				doc.set("orderSource", docO[0].getInt("orderSource"));
				doc.set("operator", docO[0].getString("operator"));
				docManager.save(doc);
				obj.put("code", "1000");
				obj.put("msg", "续订成功");
			}else{
				obj.put("code", "1001");
				obj.put("msg", "没有可用订单");
			}
		}else{
			obj.put("code", "1001");
			obj.put("msg", "没有可续订套餐");
		}
		output(obj.toString(), response);
	}
	
	/**
	 * 根据分类类型名称、文档类型字段名称、文档类型ID获取分类选项ID
	 * @param catElementName
	 * @param docTypeFieldName
	 * @param docTypeId
	 * @return
	 */
	@SuppressWarnings("finally")
	public int getCatElementId(String catElementName,String docTypeFieldName,int docTypeId){
		
		String sql="SELECT entry_id FROM  category_other WHERE wt_type=(select options from dom_doctypefields where doctypeid="+docTypeId+" and columncode='"+docTypeFieldName+"')  and entry_name='"+catElementName+"'";
		DBSession dbSession = null;
		IResultSet rs = null;
		String options=null;
		try {
			dbSession = com.founder.e5.context.Context.getDBSession();
			rs = dbSession.executeQuery(sql);
			while(rs.next()){
				options= rs.getString(1);
			}
		} catch (E5Exception e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
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
	
	//发票状态更改，未开改为已开，已开改为未开
	@RequestMapping("/changeInvoice.do")
	public void changeInvoice(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception {
		String DocIDs = get(request, "DocIDs");
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib docLibOrder = LibHelper.getLib(DocTypes.MEMBERORDERS.typeID(),"xy");
		String sql = " SYS_DELETEFLAG = 0 and SYS_DOCUMENTID = ? ";
		for (String id : DocIDs.split(",")) {
			Document[] docs = docManager.find(docLibOrder.getDocLibID(), sql, new Object[]{id});
			if(docs != null && docs.length > 0){
				docs[0].set("invoice", (docs[0].getString("invoice").equals("0")?1:0));
				docManager.save(docs[0]);
				output("@refresh@", response);
			}
		}
	}
}
