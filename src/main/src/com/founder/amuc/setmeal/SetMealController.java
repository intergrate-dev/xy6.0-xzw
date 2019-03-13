package com.founder.amuc.setmeal;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import net.sf.json.JSONObject;

import com.founder.amuc.commons.Constant;
import com.founder.amuc.commons.DateFormatAmend;
import com.founder.amuc.commons.InfoHelper;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.e5.web.BaseController;
import com.founder.e5.web.SysUser;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.LibHelper;
import com.founder.e5.commons.DomHelper;
import com.founder.e5.db.DBType;

@Controller
@RequestMapping("/amuc/setmeal")
public class SetMealController extends BaseController {
	
	protected void handle(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception {
		String action = get(request, "a");

		if (action.equals("CreateSetMeal")) {
			CreateSetMeal(request, response, model);

		}

		if (action.equals("UpdateSetMeal")) {
			UpdateSetMeal(request, response, model);

		}
	}
	@RequestMapping("/CreateSetMeal.do")
	private ModelAndView CreateSetMeal(HttpServletRequest request,
			HttpServletResponse response,
			 Map model) throws Exception {

		SysUser user = getUserInfo(request);
		//String setMealContent=get(request, "setMealContent").toString();
		String[] setMealContent1=request.getParameterValues("setMealContent");
		String setMealContent = ValuesDispose(request.getParameterValues("setMealContent"));
		int siteID = Integer.parseInt(request.getParameter("siteID"));
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		String reminds = ValuesDispose(request.getParameterValues("Remind"));
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		int docTypeID = DocTypes.MEMBERSETMEAL.typeID();
		DocLib docLib = LibHelper.getLib(docTypeID, "xy");
		Document doc = docManager.newDocument(docLib.getDocLibID(),InfoHelper.getID(docLib.getDocTypeID()));
		doc.set("Remind", reminds);
		doc.set("CreationTime",timeStampDispose(doc.getCreated()));
		doc.setFolderID(docLib.getFolderID());
		doc.set("Member", user.getUserName());
		doc.set("Member_ID", user.getUserID());
		doc.set("Operator", user.getUserName());
		doc.set("Status", get(request, "Status"));
		doc.set("Channel", get(request, "Channel"));
		doc.set("m_siteID", siteID);
		doc.set("paperNumber", setMealContent1.length);
		doc.set("setMealNmae", get(request, "setMealNmae"));
		doc.set("setMealMoney", get(request, "setMealMoney"));
		doc.set("EffectTime",DateFormatAmend.DateDispose(get(request, "EffectTime").toString()));
		doc.set("ExpireTime",DateFormatAmend.DateDispose(get(request, "ExpireTime").toString()));
		doc.set("setMealContent", setMealContent);
		doc.set("expiryDate",get(request, "EffectTime").toString().equals("") ? get(request,"expiryDate")+"天" : "---");
		try {
			docManager.save(doc);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String saveFlag = get(request, "flag");
		if (saveFlag != null && saveFlag.equals("0")) {		
		  model.put("needRefresh", "true"); // 是否需要刷新列表
		  String viewName = "redirect:/e5workspace/after.do?DocLibID="
				+ doc.getDocLibID() + "&DocIDs=1&UUID=" + get(request, "UUID");
		  return new ModelAndView(viewName,model);//跳转到jsp页面
		} else {
		  String viewName = "redirect:/amuc/setmeal/NewSetMeal.jsp?DocLibID="
					+ doc.getDocLibID() + "&FVID=" 
					+ get(request, "FVID") + "&DocIDs="+get(request, "DocIDs")+"&UUID="
					+ get(request, "UUID") + "&HasChange=1" + "&siteID=" + siteID;

		  model.put("needRefresh", "true");
		  return new ModelAndView(viewName,model);//跳转到jsp页面
        }
	}
	@RequestMapping("/UpdateSetMeal.do")
	private ModelAndView UpdateSetMeal(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception {

		SysUser user = getUserInfo(request);
		long DocID = Long.parseLong(request.getParameter("docid"));
		int docLib = Integer.parseInt(request.getParameter("doclib"));
		String[] setMealContent1=request.getParameterValues("setMealContent");
		String setMealContent = ValuesDispose(request.getParameterValues("setMealContent"));
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		String reminds = ValuesDispose(request.getParameterValues("Remind"));

		Document doc = docManager.get(docLib, DocID);

		doc.set("Remind", reminds);
		doc.set("Operator", user.getUserName());
		doc.set("Status", get(request, "Status"));
		doc.set("Channel", get(request, "Channel"));
		doc.set("Channel", get(request, "Channel"));
		doc.set("setMealNmae", get(request, "setMealNmae"));
		doc.set("paperNumber", setMealContent1.length);
		doc.set("setMealMoney",Double.parseDouble(get(request, "setMealMoney")));
		doc.set("EffectTime",DateFormatAmend.DateDispose(get(request, "EffectTime").toString()));
		doc.set("ExpireTime",DateFormatAmend.DateDispose(get(request, "ExpireTime").toString()));
		doc.set("setMealContent", setMealContent);
		doc.set("expiryDate",get(request, "EffectTime").toString().equals("") ? get(request,"expiryDate")+"天" : "---");
try {
	docManager.save(doc);
} catch (Exception e) {
	e.printStackTrace();
}
	
		model.put("needRefresh", "true"); // 是否需要刷新列表
		String viewName = "redirect:/e5workspace/after.do?DocLibID="
				+ doc.getDocLibID() + "&DocIDs="+get(request, "DocIDs")+"&UUID=" + get(request, "UUID");
		return new ModelAndView(viewName,model);//跳转到jsp页面

	}

	
    
	private Timestamp timeStampDispose(Timestamp timeStamp) throws Exception {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String s = df.format(timeStamp);
		return Timestamp.valueOf(s);
	}
	
	private String ValuesDispose(String[] parameterValues) {
		String values = "";
		for (String remind : parameterValues) {
			values += remind + ",";
		}
		if (values.indexOf(",") > 0) {
			values = values.substring(0, values.length() - 1);
		}
		return values;
	}
	
	@RequestMapping("/FindSetMeal.do")
	public void FindSetMeal(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception{
		int siteID = Integer.parseInt(request.getParameter("siteID"));
		JSONObject map = new JSONObject();
		DocLib docLib = LibHelper.getLib(DocTypes.MEMBERSETMEAL.typeID(),"xy");
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			String dbType = DomHelper.getDBType();
			String sql = "";
			if (dbType.equals(DBType.ORACLE)) {
				sql = " Status = ? and m_siteID= ? and SYS_DELETEFLAG=0 and ((TO_CHAR(sysdate,'YYYY-MM-DD') <= ExpireTime and TO_CHAR(sysdate,'YYYY-MM-DD') >= EffectTime) or (expiryDate <> '---')) ";
			}else{
				sql = " Status = ? and m_siteID= ? and SYS_DELETEFLAG=0 and ((date_format(now(),'%Y-%m-%d') <= ExpireTime and date_format(now(),'%Y-%m-%d') >= EffectTime) or (expiryDate <> '---')) ";
			}
			Document[] docs = docManager.find(docLib.getDocLibID(),	sql, new Object[] { "在售",siteID });
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			for (Document document : docs) {
				Map<String, Object> doc = new HashMap<String, Object>();
				doc.put("docid", document.getDocID());
				doc.put("setMealNmae", document.getString("setMealNmae"));
				doc.put("paperNumber", document.getString("paperNumber"));
				doc.put("setMealMoney", document.getString("setMealMoney"));
				doc.put("EffectTime", document.getString("EffectTime"));
				doc.put("ExpireTime", document.getString("ExpireTime"));
				doc.put("expiryDate", document.getString("expiryDate"));
				doc.put("setMealContent", document.getString("setMealContent"));
				list.add(doc);
			}
			map.put("code", 1);
			map.put("data", list);
		} catch (Exception e) {
			map.put("code", 0);
			map.put("data", "错误");
		}		
		
		//return map.toString();	
		output(map.toString(), response);
	}
	@RequestMapping("/del.do")
	private void deleteSetMeal(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		String setMealIDs = get(request, "DocIDs");
		if(setMealIDs == null || setMealIDs.length() == 0) return;
		
		DocLib docLib = LibHelper.getLib(DocTypes.MEMBERSETMEAL.typeID(),"xy");
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] docs = docManager.find(docLib.getDocLibID()," SYS_DOCUMENTID in(" 
			    + setMealIDs + ") and SYS_DELETEFLAG=0 and Status = '在售'",new Object[] {});
		
		DocLib docLibOrder = LibHelper.getLib(DocTypes.MEMBERORDERS.typeID(),"xy");
		String sql1 = " SYS_DELETEFLAG = 0 and setMealID LIKE '%"+setMealIDs+"%' ";
		Document[] document = docManager.find(docLibOrder.getDocLibID(), sql1, new Object[]{});
		
		if(docs != null && docs.length > 0){
			output("在售套餐不能删除！", response);
		}else if(document != null && document.length > 0){
			output("已售出套餐不能删除！", response);
		}else{
			Document[] doc = docManager.find(docLib.getDocLibID()," SYS_DOCUMENTID in(" 
				    + setMealIDs + ") and SYS_DELETEFLAG=0 and Status <> '在售'",new Object[] {});
			if(doc != null && doc.length > 0){
				for(int i=0;i<doc.length;i++){
					//System.out.println(doc[i].getString("SYS_DELETEFLAG"));
					doc[i].set("SYS_DELETEFLAG", 1);
					docManager.save(doc[i]);
				}
				output("@refresh@", response);
			}
		}
	}

	private String getUTF8StringFromGBKString(String gbkStr) {  
        try {  
            return new String(getUTF8BytesFromGBKString(gbkStr), "UTF-8");  
        } catch (UnsupportedEncodingException e) {  
            throw new InternalError();  
        }  
    }  
      
    private byte[] getUTF8BytesFromGBKString(String gbkStr) {  
        int n = gbkStr.length();  
        byte[] utfBytes = new byte[3 * n];  
        int k = 0;  
        for (int i = 0; i < n; i++) {  
            int m = gbkStr.charAt(i);  
            if (m < 128 && m >= 0) {  
                utfBytes[k++] = (byte) m;  
                continue;  
            }  
            utfBytes[k++] = (byte) (0xe0 | (m >> 12));  
            utfBytes[k++] = (byte) (0x80 | ((m >> 6) & 0x3f));  
            utfBytes[k++] = (byte) (0x80 | (m & 0x3f));  
        }  
        if (k < utfBytes.length) {  
            byte[] tmp = new byte[k];  
            System.arraycopy(utfBytes, 0, tmp, 0, k);  
            return tmp;  
        }  
        return utfBytes;  
    }
    
    @RequestMapping("/FindSetMealByIds.do")
    public void FindSetMealByIds(HttpServletRequest request,
			HttpServletResponse response, @SuppressWarnings("rawtypes") Map model) throws Exception{
		
    	String ids = get(request, "ids");
		JSONObject map = new JSONObject();
		map.put("code", 0);
		DocLib docLib = LibHelper.getLib(DocTypes.MEMBERSETMEAL.typeID(),"xy");
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
		output(map.toString(), response);
	}
}
