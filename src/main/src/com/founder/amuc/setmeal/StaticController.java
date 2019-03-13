package com.founder.amuc.setmeal;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.founder.amuc.commons.Constant;
import com.founder.amuc.commons.DateFormatAmend;
import com.founder.amuc.commons.InfoHelper;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.e5.web.BaseController;

@Controller
@RequestMapping("/amuc/setmeal")
public class StaticController extends BaseController{

	
	DocLib OrderStaticDocLib = null;
	DocLib PaperCardDocSTATICDocLib = null;

	
	@Override
	@RequestMapping("/Static.do")
	protected void handle(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception {
		
		DocLib[] StaticDocLibs = InfoHelper.getLibsByCode(Constant.DOCTYPE_STATIC);
		for (DocLib docLib : StaticDocLibs) {
			if(docLib.getDocLibName().equals("会员订单统计")){
				OrderStaticDocLib=docLib;
			}
			if(docLib.getDocLibName().equals("会员报卡统计")){
				PaperCardDocSTATICDocLib=docLib;
			}
		}
		
		int docid=0;
		if(request.getParameter("a").equals("findorder")){
			docid=OrderStaticDocLib.getDocLibID();
			
		}else if(request.getParameter("a").equals("findPaperCard")){
			
			docid=PaperCardDocSTATICDocLib.getDocLibID();
		}else if(request.getParameter("a").equals("findAllSetMeal")){
			
			findAllSetMeal(request,response);
			
		}else if(request.getParameter("a").equals("findAllTypeCode")){
			
			findAllTypeCode(request,response);
			
		}
		if(docid!=0){
			JSONObject map = new JSONObject();
			map.put("code", 0);
			Object beginTime=request.getParameter("beginTime");
			Object endTime=request.getParameter("endTime");
			if(beginTime!=null&&endTime!=null)
			{
				int siteID = Integer.parseInt(request.getParameter("siteID"));
				map.put("data",findOrderStatic(docid,beginTime.toString(),endTime.toString(),siteID) );
			}else{
				map.put("code", 1);	
				map.put("data", "错误");
			}
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			out.println(map.toString());
			out.flush();
			out.close();
		}
		
		
		
		
	}
	private void findAllTypeCode(HttpServletRequest request,
			HttpServletResponse response)  throws Exception{
		int siteID = Integer.parseInt(request.getParameter("siteID"));
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		List<Map<String,String>> list=new ArrayList<Map<String,String>>();
		DocLib DocLib = InfoHelper.getLib(Constant.DOCTYPE_TYPECODE);
		Document[] orders = docManager.find(DocLib.getDocLibID(), "SYS_DELETEFLAG=0 order by SYS_DOCUMENTID",new Object[] {});
		for (Document document : orders) {
			Map<String,String> map=new HashMap<String, String>();
			map.put("id", document.getDocID()+"");
			map.put("name", document.getString("pcTypeCode"));
		
			list.add(map);
		}
		JSONObject map = new JSONObject();
		map.put("data", list);
		map.put("code", 0);
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println(map.toString());
		out.flush();
		out.close();
		
	}
	private void findAllSetMeal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		int siteID = Integer.parseInt(request.getParameter("siteID"));
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		List<Map<String,String>> list=new ArrayList<Map<String,String>>();
		DocLib DocLib = InfoHelper.getLib(Constant.DOCTYPE_SETMEAL);
		Document[] orders = docManager.find(DocLib.getDocLibID(), " m_siteID = ? and SYS_DELETEFLAG=0 order by SYS_DOCUMENTID",new Object[] {siteID});
		for (Document document : orders) {
			Map<String,String> map=new HashMap<String, String>();
			map.put("id", document.getDocID()+"");
			map.put("name", document.getString("setMealNmae"));
		
			list.add(map);
		}
		JSONObject map = new JSONObject();
		map.put("data", list);
		map.put("code", 0);
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println(map.toString());
		out.flush();
		out.close();
	}
	private List<Map<String,String>> findOrderStatic(int doclibid,String beginTime,String endTime,int siteID) throws Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		
		List<Map<String,String>> list=new ArrayList<Map<String,String>>();
		Document[] orders = docManager.find(doclibid,
				 "SYS_DELETEFLAG=0  and Time>=?  and Time<=? and m_siteID=? order by Time",
				new Object[] { DateFormatAmend.DateDispose(beginTime),DateFormatAmend.DateDispose(endTime),siteID});
		for (Document document : orders) {
			Map<String,String> map=new HashMap<String, String>();
			map.put("Time", document.getString("Time"));
			map.put("Data", document.getString("Data"));
			map.put("Totality", document.getString("Totality"));
			map.put("TotalMoney", document.getString("TotalMoney"));
			list.add(map);
		}
				return list;
	}
	
	
}
