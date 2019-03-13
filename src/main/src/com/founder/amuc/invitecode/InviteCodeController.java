package com.founder.amuc.invitecode;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.founder.amuc.commons.FormViewerHelper;
import com.founder.amuc.commons.InfoHelper;
import com.founder.xy.jpublish.PublishHelper;
import com.founder.e5.commons.DateUtils;
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
import com.founder.e5.web.BaseController;
import com.founder.e5.web.SysUser;
import com.founder.e5.workspace.ProcHelper;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.LibHelper;
import com.founder.e5.web.WebUtil;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import com.founder.xy.commons.JsonHelper;
import com.founder.e5.sys.StorageDevice;
import com.founder.e5.sys.StorageDeviceManager;
import com.founder.e5.sys.StorageDeviceReader;
import java.io.InputStream;
import com.founder.e5.commons.DomHelper;
import com.founder.e5.db.DBType;

@Controller
@RequestMapping("/amuc/invitecode")
public class InviteCodeController extends BaseController{
	
	@Autowired 
	private InviteCodeManager manager;
	
	
	
	
	public void handle(HttpServletRequest request, HttpServletResponse response, Map model) throws Exception {
		
		String action = request.getParameter("a");
		System.out.println(action);
		if("xAnalyze".equals(action)){
			xAnalyze(request, response,model);
		}
	}
	
	/**
	 * 统计分析企业邀请码
	 * @throws Exception 
	 * xAnalyze.do
	 */
	@RequestMapping("/xAnalyze.do")
	public void xAnalyze(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception {
		
		int siteID = getInt(request, "siteID",1);
		String level1 = get(request, "level1");
		String level2 = get(request, "level2");
		String level3 = get(request, "level3");
		String type = get(request, "type");
		int docTypeID = DocTypes.MEMBERINVITECODE.typeID();
		DocLib docLib = LibHelper.getLib(docTypeID, "xy");
		StringBuilder sql = new StringBuilder();
		
		if("all".equals(type)){
			sql.append("select  icLevel1 level,sum(icNum) icNum ,sum(icMetrics) icMetrics from ").append(docLib.getDocLibTable())
				.append(" where SYS_DELETEFLAG = 0 and icStatus = 1 and m_siteID = "+siteID+" group by icLevel1Index,icLevel1");
		}else if("2".equals(type)){
			sql.append("select  icLevel2 level,sum(icNum) icNum ,sum(icMetrics) icMetrics from ").append(docLib.getDocLibTable())
			.append(" where SYS_DELETEFLAG = 0 and icStatus = 1 and icLevel2 !=''  and icLevel1 = '"+ level1 +"' and m_siteID = "+siteID+" group by icLevel2Index,icLevel2");
		}else if("3".equals(type)){
			sql.append("select  icLevel3 level,sum(icNum) icNum ,sum(icMetrics) icMetrics from ").append(docLib.getDocLibTable())
			.append(" where SYS_DELETEFLAG = 0 and icStatus = 1 and icLevel3 !='' and icLevel1 = '"+ level1 +"' and icLevel2= '"+level2 +"' and m_siteID = "+siteID+" group by icLevel3Index,icLevel3");
			
		}else if("4".equals(type)){
			sql.append("select  icLevel4 level,sum(icNum) icNum ,sum(icMetrics) icMetrics from ").append(docLib.getDocLibTable())
			.append(" where SYS_DELETEFLAG = 0 and icStatus = 1 and icLevel4 !='' and icLevel1 = '"+ level1 +"' and icLevel2= '"+level2 +"' and icLevel3= '"+level3 +"' and m_siteID = "+siteID+" group by icLevel4Index,icLevel4");
		} 
		List<String> AnaList = new ArrayList<String>();
		DBSession conn = null;
		IResultSet rs = null;
		try {
			conn = Context.getDBSession(); 
			rs = conn.executeQuery(sql.toString());
			while (rs.next()) {
				JSONObject obj = new JSONObject();
				obj.put("level", rs.getString("level"));
				obj.put("icNum", rs.getInt("icNum"));
				obj.put("icMetrics", rs.getInt("icMetrics"));
				
				AnaList.add(obj.toString());
			}
			
		} catch (Exception e) {
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}
		
		output(AnaList.toString(), response);
	}
	
	/**
	 * 统计分析企业邀请码和个人邀请码
	 * @throws Exception 
	 * xAnalyze.do
	 */
	@RequestMapping("/xpAnalyze.do")
	public void xpAnalyze(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception {
		String dbType = DomHelper.getDBType();
        if (dbType.equals(DBType.ORACLE)) {
        	xpAnalyze2(request,response,model);
        }else{
        	xpAnalyze1(request,response,model);
        }
	}
	public void xpAnalyze1(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception {
		
		int siteID = getInt(request, "siteID",1);
		String type = get(request, "type");	
		int code = getInt(request, "code");//识别企业邀请码--0和个人邀请码--1
		String starttime = get(request, "starttime");	
		String endtime = get(request, "endtime");	
		int docTypeID = DocTypes.MEMBERINVITECODE.typeID();
		DocLib docLib = LibHelper.getLib(docTypeID, "xy");
		StringBuilder sql1 = new StringBuilder();
		StringBuilder sql2 = new StringBuilder();
		StringBuilder sql3 = new StringBuilder();
		String qIcNum = "";
		String pIcNum = "";
		String qnames = "";
		String pnames = "";
		String sqltime = "";
		
		if("".equals(starttime) || "".equals(endtime)){
			sqltime = " and m_siteID = "+siteID+" ";
		}else{
			sqltime = " and SYS_CREATED between '"+starttime+"' and '"+endtime+"' and m_siteID = "+siteID+" ";
		}
		if("day".equals(type)){
			sql1.append("select sum(icNum) icNum, date_format(SYS_CREATED,'%Y-%m-%d') names  from ").append(docLib.getDocLibTable())
			.append(" where SYS_DELETEFLAG = 0 and icStatus = 1 and icInviterID = -1 "
					+ sqltime
					+ " GROUP BY date_format(SYS_CREATED,'%Y-%m-%d')");
			sql2.append("select sum(icNum) icNum, date_format(SYS_CREATED,'%Y-%m-%d') names  from ").append(docLib.getDocLibTable())
			.append(" where SYS_DELETEFLAG = 0 and icStatus = 1 and icInviterID > -1"
					+ sqltime
					+ " GROUP BY date_format(SYS_CREATED,'%Y-%m-%d')");
			//System.out.println(sql1);
			qIcNum = sqlAnalyze(request,sql1.toString());
			pIcNum = sqlAnalyze(request,sql2.toString());
		}else if("week".equals(type)){
			sql1.append("select sum(icNum) icNum, YEARWEEK(date_format(SYS_CREATED,'%Y-%m-%d')) names from ").append(docLib.getDocLibTable())
			.append(" where SYS_DELETEFLAG = 0 and icStatus = 1 and icInviterID = -1"
					+ sqltime
					+ " GROUP BY YEARWEEK(date_format(SYS_CREATED,'%Y-%m-%d'))");
			sql2.append("select sum(icNum) icNum, YEARWEEK(date_format(SYS_CREATED,'%Y-%m-%d')) names from ").append(docLib.getDocLibTable())
			.append(" where SYS_DELETEFLAG = 0 and icStatus = 1 and icInviterID > -1"
					+ sqltime
					+ " GROUP BY YEARWEEK(date_format(SYS_CREATED,'%Y-%m-%d'))");
			
			qIcNum = sqlAnalyze(request,sql1.toString());
			pIcNum = sqlAnalyze(request,sql2.toString());
			
		}else if("month".equals(type)){
			sql1.append("select sum(icNum) icNum, date_format(SYS_CREATED,'%Y-%m') names from ").append(docLib.getDocLibTable())
			.append(" where SYS_DELETEFLAG = 0 and icStatus = 1 and icInviterID = -1"
					+ sqltime
					+ " GROUP BY date_format(SYS_CREATED,'%Y-%m')");
			sql2.append("select sum(icNum) icNum, date_format(SYS_CREATED,'%Y-%m') names from ").append(docLib.getDocLibTable())
			.append(" where SYS_DELETEFLAG = 0 and icStatus = 1 and icInviterID > -1"
					+ sqltime
					+ " GROUP BY date_format(SYS_CREATED,'%Y-%m')");
			
			qIcNum = sqlAnalyze(request,sql1.toString());
			pIcNum = sqlAnalyze(request,sql2.toString());
			
		}else if("year".equals(type)){
			sql1.append("select sum(icNum) icNum, date_format(SYS_CREATED,'%Y') names from ").append(docLib.getDocLibTable())
			.append(" where SYS_DELETEFLAG = 0 and icStatus = 1 and icInviterID = -1"
					+ sqltime
					+ " GROUP BY date_format(SYS_CREATED,'%Y')");
			sql2.append("select sum(icNum) icNum, date_format(SYS_CREATED,'%Y') names from ").append(docLib.getDocLibTable())
			.append(" where SYS_DELETEFLAG = 0 and icStatus = 1 and icInviterID > -1"
					+ sqltime
					+ " GROUP BY date_format(SYS_CREATED,'%Y')");
			
			qIcNum = sqlAnalyze(request,sql1.toString());
			pIcNum = sqlAnalyze(request,sql2.toString());
		} 		
		output((code==0)?qIcNum:pIcNum, response);
	}
	public void xpAnalyze2(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception {
		
		int siteID = getInt(request, "siteID",1);
		String type = get(request, "type");	
		int code = getInt(request, "code");//识别企业邀请码--0和个人邀请码--1
		String starttime = get(request, "starttime");	
		String endtime = get(request, "endtime");	
		int docTypeID = DocTypes.MEMBERINVITECODE.typeID();
		DocLib docLib = LibHelper.getLib(docTypeID, "xy");
		StringBuilder sql1 = new StringBuilder();
		StringBuilder sql2 = new StringBuilder();
		StringBuilder sql3 = new StringBuilder();
		String qIcNum = "";
		String pIcNum = "";
		String qnames = "";
		String pnames = "";
		String sqltime = "";
		
		if("".equals(starttime) || "".equals(endtime)){
			sqltime = " and m_siteID = "+siteID+" ";
		}else{
			sqltime = " and SYS_CREATED between '"+starttime+"' and '"+endtime+"' and m_siteID = "+siteID+" ";
		}
		if("day".equals(type)){
			sql1.append("select sum(icNum) icNum, TO_CHAR(SYS_CREATED,'YYYY-MM-DD') names  from ").append(docLib.getDocLibTable())
			.append(" where SYS_DELETEFLAG = 0 and icStatus = 1 and icInviterID = -1 "
					+ sqltime
					+ " GROUP BY TO_CHAR(SYS_CREATED,'YYYY-MM-DD')");
			sql2.append("select sum(icNum) icNum, TO_CHAR(SYS_CREATED,'YYYY-MM-DD') names  from ").append(docLib.getDocLibTable())
			.append(" where SYS_DELETEFLAG = 0 and icStatus = 1 and icInviterID > -1"
					+ sqltime
					+ " GROUP BY TO_CHAR(SYS_CREATED,'YYYY-MM-DD')");
			//System.out.println(sql1);
			qIcNum = sqlAnalyze(request,sql1.toString());
			pIcNum = sqlAnalyze(request,sql2.toString());
		}else if("week".equals(type)){
			sql1.append("select sum(icNum) icNum, TO_CHAR(SYS_CREATED,'iw') names from ").append(docLib.getDocLibTable())
			.append(" where SYS_DELETEFLAG = 0 and icStatus = 1 and icInviterID = -1"
					+ sqltime
					+ " GROUP BY TO_CHAR(SYS_CREATED,'iw')");
			sql2.append("select sum(icNum) icNum, TO_CHAR(SYS_CREATED,'iw') names from ").append(docLib.getDocLibTable())
			.append(" where SYS_DELETEFLAG = 0 and icStatus = 1 and icInviterID > -1"
					+ sqltime
					+ " GROUP BY TO_CHAR(SYS_CREATED,'iw')");
			
			qIcNum = sqlAnalyze(request,sql1.toString());
			pIcNum = sqlAnalyze(request,sql2.toString());
			
		}else if("month".equals(type)){
			sql1.append("select sum(icNum) icNum, TO_CHAR(SYS_CREATED,'YYYY-MM') names from ").append(docLib.getDocLibTable())
			.append(" where SYS_DELETEFLAG = 0 and icStatus = 1 and icInviterID = -1"
					+ sqltime
					+ " GROUP BY TO_CHAR(SYS_CREATED,'YYYY-MM')");
			sql2.append("select sum(icNum) icNum, TO_CHAR(SYS_CREATED,'YYYY-MM') names from ").append(docLib.getDocLibTable())
			.append(" where SYS_DELETEFLAG = 0 and icStatus = 1 and icInviterID > -1"
					+ sqltime
					+ " GROUP BY TO_CHAR(SYS_CREATED,'YYYY-MM')");
			
			qIcNum = sqlAnalyze(request,sql1.toString());
			pIcNum = sqlAnalyze(request,sql2.toString());
			
		}else if("year".equals(type)){
			sql1.append("select sum(icNum) icNum, TO_CHAR(SYS_CREATED,'YYYY') names from ").append(docLib.getDocLibTable())
			.append(" where SYS_DELETEFLAG = 0 and icStatus = 1 and icInviterID = -1"
					+ sqltime
					+ " GROUP BY TO_CHAR(SYS_CREATED,'YYYY')");
			sql2.append("select sum(icNum) icNum, TO_CHAR(SYS_CREATED,'YYYY') names from ").append(docLib.getDocLibTable())
			.append(" where SYS_DELETEFLAG = 0 and icStatus = 1 and icInviterID > -1"
					+ sqltime
					+ " GROUP BY TO_CHAR(SYS_CREATED,'YYYY')");
			
			qIcNum = sqlAnalyze(request,sql1.toString());
			pIcNum = sqlAnalyze(request,sql2.toString());
		} 		
		output((code==0)?qIcNum:pIcNum, response);
	}
	public String sqlAnalyze(HttpServletRequest request,String sql) throws Exception {
		int docTypeID = DocTypes.MEMBERINVITECODE.typeID();
		DocLib docLib = LibHelper.getLib(docTypeID, "xy");
		JSONArray jsonArray = new JSONArray();  
		JSONArray jsonArray2 = new JSONArray();
		DBSession conn = null;
		IResultSet rs = null;
		try {
			conn = Context.getDBSession(); 
			rs = conn.executeQuery(sql.toString());
			while (rs.next()) {				
				jsonArray.add(rs.getString("icNum"));
				jsonArray2.add(rs.getString("names"));
			}
			
		} catch (Exception e) {
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}	
		JSONObject obj = new JSONObject();
		obj.put("icNum", jsonArray);
		obj.put("names", jsonArray2);
		return obj.toString();
	}
	/**
	 * 根据设备号和邀请码写入记录。
	 * 如果该设备号已经写入邀请码则不再写入。
	 * 如果邀请码不存在，返回。
	 * @return
	 * @throws E5Exception
	 */
	@RequestMapping("/codeImeiRecord.do")
	public String codeImeiRecord(HttpServletRequest request,
			HttpServletResponse response, Map model) throws E5Exception{
		
		String code = request.getParameter("code");
		String imei = request.getParameter("imei");
		String uid = request.getParameter("uid");
		String name = request.getParameter("name");
		JSONObject obj = new JSONObject();
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib docLib = LibHelper.getLib(DocTypes.MEMBER.typeID(), "xy");
		DocLib codeDocLib = LibHelper.getLib(DocTypes.MEMBERINVITECODE.typeID(), "xy");
		DocLib codelogDocLib = LibHelper.getLib(DocTypes.MEMBERINVITECODELOG.typeID(), "xy");
		
		Document[] cdocs = docManager.find(codeDocLib.getDocLibID(), "icCode = ? AND SYS_DELETEFLAG = 0 ", new Object[] {code});
		if(cdocs != null && cdocs.length > 0){
			//新锐app会员不能输入自己的邀请码
			Document[] cdocs1 = docManager.find(codeDocLib.getDocLibID(), "icCode = ? AND icInviterID = ? AND SYS_DELETEFLAG = 0 ", new Object[] {code,uid});
			//新锐app会员是否写过邀请码记录
			Document[] cldocs1 = docManager.find(codelogDocLib.getDocLibID(), "icInvitedID = ? AND SYS_DELETEFLAG = 0 ", new Object[] {uid});
			//新华app该设备号是否写过邀请码记录
			Document[] cldocs = docManager.find(codelogDocLib.getDocLibID(), "icImei = ? AND SYS_DELETEFLAG = 0 ", new Object[] {imei});
			//老用户不能使用邀请码
			Document[] doc1 = docManager.find(docLib.getDocLibID()," SYS_DELETEFLAG = 0 and SYS_CREATED < '1900-12-26 00:00:00' and SYS_DOCUMENTID = ? ", new Object[] {uid});
			
			if(!StringUtils.isBlank(uid) && doc1 != null && doc1.length > 0){
					
				obj.put("code", "1001");
				obj.put("msg", "邀请已注册会员无效");
			}else if(!StringUtils.isBlank(uid) && cdocs1 != null && cdocs1.length > 0){
					
				obj.put("code", "1001");
				obj.put("msg", "会员不能使用自己的邀请码");
			}else if(!StringUtils.isBlank(uid) && cldocs1 != null && cldocs1.length > 0){
					
				obj.put("code", "1001");
				obj.put("msg", "会员已经使用过邀请码");
			}else if(cldocs != null && cldocs.length > 0){
				
				obj.put("code", "1001");
				obj.put("msg", "该设备已经添加过邀请码使用记录");
			}else{
				
				DBSession conn = null;
				try {
					conn = Context.getDBSession(); 
					conn.beginTransaction();
					Document[] doc = docManager.find(docLib.getDocLibID()," SYS_DELETEFLAG = 0 and SYS_DOCUMENTID = ? ", new Object[] {uid});
					Document codelogDoc = docManager.newDocument(codelogDocLib.getDocLibID(), InfoHelper.getID(codelogDocLib.getDocTypeID()));
					codelogDoc.setFolderID(codelogDocLib.getFolderID());
					codelogDoc.setDeleteFlag(0);
					codelogDoc.setLocked(false);
					Timestamp date = DateUtils.getTimestamp();
					codelogDoc.setCreated(date);
					codelogDoc.set("icCodeID", cdocs[0].getDocID());
					codelogDoc.set("icCode", code);
					codelogDoc.set("icImei", imei);
					codelogDoc.set("icInvitedID", uid);
					if(doc != null && doc.length > 0){
						codelogDoc.set("icInvitedName", doc[0].getString("mNickname"));
					}
					
					docManager.save(codelogDoc,conn);
					
					//增加邀请码使用数量
					int icnum = cdocs[0].getInt("icNum") + 1;
					String member = cdocs[0].getString("icInviterID");
					cdocs[0].set("icNum", icnum);
					cdocs[0].set("icStatus", 1);
					docManager.save(cdocs[0],conn);
					
					conn.commitTransaction();
					obj.put("code", "1002");
					obj.put("member", (member==""||member==null)?"0":member);//邀请人id
					obj.put("msg", "该设备成功添加邀请码使用记录");
					
				}catch (Exception e) {
					ResourceMgr.rollbackQuietly(conn);
					e.printStackTrace();
		        } finally {
		        	ResourceMgr.closeQuietly(conn);
		        }
			}
			
		}else{
			obj.put("code", "1003");
			obj.put("msg", "邀请码不存在");
		}
		return obj.toString();
		
	}
	
	/**
	 * app调用接口生成邀请码
	 * 2015-09-17 
	 * @return
	 */
	@RequestMapping("/AppCreateIcCode.do")
	public String AppCreateIcCode(HttpServletRequest request,
			HttpServletResponse response, Map model) throws E5Exception {
		String uid = request.getParameter("uid");
		JSONObject result = new JSONObject();
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		int docTypeID = DocTypes.MEMBERINVITECODE.typeID();
		DocLib IcdocLib = LibHelper.getLib(docTypeID, "xy");//邀请码文档类型
		DocLib MdocLib = LibHelper.getLib(DocTypes.MEMBER.typeID(), "xy");//会员文档类型
		DocLib IcIddocLib = LibHelper.getLib(DocTypes.MEMBERINVITECODEID.typeID(),"xy");//邀请码标识文档类型
		
		//1.判断该uid 是否在系统会员表里存在
		Document memberdoc = docManager.get(MdocLib.getDocLibID(), Integer.parseInt(uid));
		if(memberdoc == null){
			result.put("code", "0");
			result.put("msg", "系统中不存在uid:"+uid+"的会员");
			
		}else{
			//2.存在会员后,判断该uid是否已经生成了邀请码，如果有直接返回。
			Document[] icodeDocs = docManager.find(IcdocLib.getDocLibID(), "SYS_DELETEFLAG=0 and icInviterID=?",  new Object[]{uid});
			if(icodeDocs != null  && icodeDocs.length > 0){
				
				result.put("code", icodeDocs[0].getString("icCode"));
				result.put("msg", "系统中存在,获取邀请码成功");
			}else{//3.如果该uid没有生成过邀请码,创建新的邀请码
				DBSession conn = null;
				try {
					//取出邀请码标识（该标识每生成一个都会自增长）
					Document[] iciddoc = docManager.find(IcIddocLib.getDocLibID(), "SYS_DELETEFLAG=0 and icCodeIdentiType = 'app' ",  null);
					if(iciddoc != null && iciddoc.length > 0){
						
						char[] iccodechars = increStr(iciddoc[0].getString("icCodeIdenti"));//取出邀请码标识,并增长
						
						//String iccode = InsertIntoCode(iccodechars);
						String iccode = manager.geneRanCode(iciddoc[0], 7);
						conn = Context.getDBSession();
						conn.beginTransaction();
						
						Document doc = docManager.newDocument(IcdocLib.getDocLibID(), InfoHelper.getID(IcdocLib.getDocTypeID()));
						doc.setFolderID(IcdocLib.getFolderID());
						doc.setDeleteFlag(0);
						doc.setLocked(false);
						Timestamp date = DateUtils.getTimestamp();
						doc.setCreated(date);
						
						doc.set("icCode", iccode);
						doc.set("icExpiredDay", 7);
						doc.set("icStatus", 0);
						doc.set("icInviterID", memberdoc.getDocID());//邀请人编号
						doc.set("icInviterName", memberdoc.get("mName"));//邀请人名称
						doc.set("icType", 1);
						//邀请码生成的类型 0-系统生成 1-会员生成
						doc.set("icCreateType", 1);
						
						docManager.save(doc,conn);
						
						//更新邀请码标识
						iciddoc[0].set("icCodeIdenti", String.valueOf(iccodechars));
						docManager.save(iciddoc[0],conn);
						
						conn.commitTransaction();
						result.put("code", iccode);
						result.put("msg", "获取邀请码成功");
						
					}else{
						result.put("code", 0);
						result.put("msg", "邀请码标识不存在,获取邀请码失败");
					}
					
				}catch (Exception e) {
					ResourceMgr.rollbackQuietly(conn);
					e.printStackTrace();
					result.put("code", "0");
					result.put("msg", "错误信息:"+e.getLocalizedMessage());
		        } finally {
		        	ResourceMgr.closeQuietly(conn);
		        }
			}
		}
		return result.toString();
		
	}

	/**
	 * 邀请码字符串递增
	 * @param codestr
	 * @return
	 */
	public static char[] increStr(String codestr) {
		
		if (codestr != null && codestr.length() > 0) {
			
			char[] charArray = codestr.toCharArray();
			AtomicInteger z = new AtomicInteger(0);
			for (int i = charArray.length - 1; i > -1; i--) {
				if(charArray[i] == '9'){
					charArray[i] = 'A';
					for(int j = charArray.length - 1; j >= i+1;j--){
						charArray[j] = '0';
					}
					break;
				}
				if (charArray[i] == 'Z' ) {
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
	
	/**
	 * 对app生成的邀请插入两个随机的大写字母
	 */
	public static String InsertIntoCode(char[] code){
		
		Random random = new Random();
		char [] res = new char[7];
		
		res[0] = code[0];
		res[1] = (char) (random.nextInt(26) + 65 );
		res[2] = code[1];
		res[3] = code[2];
		res[4] = (char) (random.nextInt(26) + 65 );
		res[5] = code[3];
		res[6] = code[4];
		
		return String.valueOf(res);
	}
	
	/**
	 * 邀请码生成页面初始化
	 * @param request
	 * @param response
	 * @param model
	 * @throws E5Exception
	 */
	@RequestMapping("/InviteCodeInit.do")
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private ModelAndView InviteCodeInit(HttpServletRequest request,
			HttpServletResponse response, Map model) throws E5Exception {
		
		int docTypeID = DocTypes.MEMBERINVITECODE.typeID();
		DocLib docLib = LibHelper.getLib(docTypeID, "xy");//邀请码文档类型
		long docID = getInt(request, "DocIDs", 0);
		int s = getInt(request, "s",0);//0表示南方，1表示新华，两者的邀请码生成页面不一样
		String  VIEWNAME = "";
		model.put("DocLibID", docLib.getDocLibID());
		model.put("DocIDs", docID);
		model.put("FVID", docLib.getFolderID());
		model.put("UUID", get(request, "UUID"));
		
		String[] formJsp = FormViewerHelper.getFormJsp(docLib.getDocLibID(), docID, "InviteCode", get(request, "UUID"));
		model.put("formHead", formJsp[0]);
		model.put("formContent", FormViewerHelper.delFormStr(formJsp[1]));
		if(s == 1){
			VIEWNAME = "amuc/invitecode/XGenerateInviteCode";
		}else{
			VIEWNAME = "amuc/invitecode/GenerateCode";
		}
		return new ModelAndView(VIEWNAME,model);//跳转到jsp页面
	
	}
	
	/**
	 * 生成邀请码
	 * @param request
	 * @param response
	 * @throws E5Exception 
	 * @throws IOException 
	 * @throws ServletException 
	 */
	@RequestMapping("/xgenerate.do")
	@SuppressWarnings("unchecked")
	private ModelAndView xgenerate(HttpServletRequest request,
			HttpServletResponse response,Map model) throws E5Exception, ServletException, IOException {
		
		//1.组装数据
		List<Document> CodeList = new ArrayList<Document>();
		CodeList = assembleCode(request);
		
		//2.save
		DBSession conn = null;
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		int docTypeID = DocTypes.MEMBERINVITECODE.typeID();
		DocLib docLib = LibHelper.getLib(docTypeID, "xy");//邀请码文档类型
		
		try {
			conn = Context.getDBSession(); 
			conn.beginTransaction();
			for (Document doc : CodeList) {
				docManager.save(doc,conn);
			}
			conn.commitTransaction();
			
		}catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			e.printStackTrace();
        	return null;
        } finally {
        	ResourceMgr.closeQuietly(conn);
        }
		
		String viewName = "redirect:/e5workspace/after.do?DocLibID=" + docLib.getDocLibID()
				+ "&DocIDs=" + 1 + "&UUID=" + get(request, "UUID");
		return new ModelAndView(viewName,model);//跳转到jsp页面
	}
	/**
	 * 生成邀请码，组装数据
	 * @param request
	 * @return
	 * @throws E5Exception 
	 */
	private List<Document> assembleCode(HttpServletRequest request) throws E5Exception {
		
		List<Document> codeList = new ArrayList<Document>();
		//1.获取参数
		String icType = get(request, "icType","");//层级类型
		String icLevel1 = get(request, "icLevel1");
		String icLevel1Index = get(request, "icLevel1Index");//代号
		String icLevel2 = get(request, "icLevel2","");
		String icLevel2Index = get(request, "icLevel2Index","");
		String icLevel3 = get(request, "icLevel3","");
		String icLevel3Index = get(request, "icLevel3Index","");
		String icLevel4 = get(request, "icLevel4","");
		String icLevel4Index = get(request, "icLevel4Index","");
				
		int icMetrics = getInt(request, "icMetrics",0);
		String icInviterName = get(request, "icInviterName");
		int icGenerateNum = getInt(request, "icGenerateNum",0); //生成数量
		int icExpiredDay = getInt(request, "icExpiredDay",7);//邀请码有效期天数
		//int siteID = Integer.parseInt(request.getParameter("siteID"));
		int siteID = getInt(request, "siteID",1);
		String icIdex = "00";//get(request, "icIdex");自增长索引
		
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		int docTypeID = DocTypes.MEMBERINVITECODE.typeID();
		DocLib docLib = LibHelper.getLib(docTypeID, "xy");//邀请码文档类型
		SysUser user = ProcHelper.getUser(request);
		String temp = icIdex;
		for(int j = 0 ;j < icGenerateNum ; j++){
				
				Document doc = docManager.newDocument(docLib.getDocLibID(), InfoHelper.getID(docLib.getDocTypeID())); 
				doc.setFolderID(docLib.getFolderID());
				doc.setDeleteFlag(0);
				doc.setLocked(false);
				doc.setAuthors(user.getUserName());
				doc.set("icType", icType);
				doc.set("icUnit", icLevel1);
				//doc.set("icUnit_ID", CatHelper.getCatIDByName(30,icLevel1));
				doc.set("icLevel1", icLevel1);
				doc.set("icLevel2", icLevel2);
				doc.set("icLevel3", icLevel3);
				doc.set("icLevel4", icLevel4);
				doc.set("icLevel1Index", icLevel1Index);
				doc.set("icLevel2Index", icLevel2Index);
				doc.set("icLevel3Index", icLevel3Index);
				doc.set("icLevel4Index", icLevel4Index);
				doc.set("icMetrics", icMetrics);
				doc.set("icInviterName", icInviterName);
				doc.set("icExpiredDay", icExpiredDay);
				doc.set("icInviterID", -1);
				//邀请码在生成的时候，默认为未使用状态
				doc.set("icStatus", 0);
				doc.set("m_siteID", siteID);
				
				if("2".equals(icType)){
					doc.set("icCode", icLevel1Index+temp);
					doc.set("icLevel2Index", temp);
				}else if("3".equals(icType)){
					doc.set("icCode", icLevel1Index + icLevel2Index + temp);
					doc.set("icLevel3Index", temp);
				}else if("4".equals(icType)){
					doc.set("icCode", icLevel1Index+icLevel2Index+icLevel3Index+temp);
					doc.set("icLevel4Index", temp);
				}
				temp = String.valueOf(increStrsys(temp));
				codeList.add(doc);
		}
		
		
		return codeList;
	}
	/**
	 * 数字类型的字符串自增长
	 * @param codestr
	 * @return
	 */
	public static char[] increStrsys(String codestr) {
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
	
	/**
	 * 邀请码编码树增加
	 * @param 
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping("/addTree.do")
	public void addTree(HttpServletRequest request,
			HttpServletResponse response, @SuppressWarnings("rawtypes") Map model) throws Exception {
		response.setHeader("Access-Control-Allow-Origin","*");
		response.setContentType("text/html;charset=UTF-8");
		String pid = request.getParameter("pid");
		String icLevel = request.getParameter("icLevel");
		String icLevelIndex = request.getParameter("icLevelIndex");
		String icHierarchy = request.getParameter("icHierarchy");
		Map<String, Object> maps = new HashMap<String, Object>();
		
		SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DocLib amLib = LibHelper.getLib(DocTypes.MEMBERINVITECODETREE.typeID(),"xy");
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document amDoc = docManager.newDocument(amLib.getDocLibID(), InfoHelper.getID(amLib.getDocTypeID()));
		amDoc.setFolderID(amLib.getFolderID());
		amDoc.setDeleteFlag(0);
		//amDoc.setLocked(false);
		amDoc.set("pid", pid);
		amDoc.set("icLevel", icLevel);
		amDoc.set("icLevelIndex", icLevelIndex);
		amDoc.set("icHierarchy", icHierarchy);
		//amDoc.set("cCreated", time.format(new Date()));
		docManager.save(amDoc);
		maps.put("code", "0001");
		maps.put("msg", "添加成功");
		JSONObject result = JSONObject.fromObject(maps);
		output(String.valueOf(result), response);		
	}
	
	/**
	 * 修改邀请码编码树
	 * @param 
	 * @return
	 * @throws E5Exception 
	 */
	@RequestMapping("/modifyTree.do")
	public void delTree(HttpServletRequest request,
			HttpServletResponse response, @SuppressWarnings("rawtypes") Map model) throws Exception {
		
		response.setHeader("Access-Control-Allow-Origin","*");
		response.setContentType("text/html;charset=UTF-8");
		Map<String, Object> maps = new HashMap<String, Object>();
		String id = request.getParameter("id");
		String icLevel = request.getParameter("icLevel");
		String icLevelIndex = request.getParameter("icLevelIndex");
		
		DocLib docLib = LibHelper.getLib(DocTypes.MEMBERINVITECODETREE.typeID(),"xy");
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] docs = docManager.find(docLib.getDocLibID()," SYS_DELETEFLAG=0 and SYS_DOCUMENTID=" + id,new Object[] {});
		if(docs != null && docs.length > 0){
			for(int i=0;i<docs.length;i++){
				docs[i].set("icLevel", icLevel);
				docs[i].set("icLevelIndex", icLevelIndex);
				docManager.save(docs[i]);
				maps.put("code", "0001");
				maps.put("msg", "修改成功");
			}
		}else{
			maps.put("code", "0000");
			maps.put("msg", "修改失败");
		}
		
		JSONObject result = JSONObject.fromObject(maps);
		output(String.valueOf(result), response);
	}
	
	/**
	 * 删除邀请码编码树
	 * @param 
	 * @return
	 * @throws E5Exception 
	 */
	@RequestMapping("/delTree.do")
	public void modifyTree(HttpServletRequest request,
			HttpServletResponse response, @SuppressWarnings("rawtypes") Map model) throws Exception {
		
		response.setHeader("Access-Control-Allow-Origin","*");
		response.setContentType("text/html;charset=UTF-8");
		Map<String, Object> maps = new HashMap<String, Object>();
		String id = request.getParameter("id");
		
		DocLib docLib = LibHelper.getLib(DocTypes.MEMBERINVITECODETREE.typeID(),"xy");
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] docs = docManager.find(docLib.getDocLibID()," SYS_DELETEFLAG=0 and SYS_DOCUMENTID=" + id,new Object[] {});
		if(docs != null && docs.length > 0){
			for(int i=0;i<docs.length;i++){
				docs[i].set("SYS_DELETEFLAG", 1);
				docManager.save(docs[i]);
				maps.put("code", "0001");
				maps.put("msg", "删除成功");
			}
		}else{
			maps.put("code", "0000");
			maps.put("msg", "删除失败");
		}
		
		JSONObject result = JSONObject.fromObject(maps);
		output(String.valueOf(result), response);
	}
	
	/**
	 * 获取邀请码编码树
	 * @param 
	 * @return
	 * @throws E5Exception 
	 */
	@RequestMapping("/getTree.do")
	public void getTree(HttpServletRequest request,
			HttpServletResponse response, @SuppressWarnings("rawtypes") Map model) throws Exception {
		response.setHeader("Access-Control-Allow-Origin","*");
		response.setContentType("text/html;charset=UTF-8");
		JSONObject Json = new JSONObject();  
		JSONArray JsonArray = new JSONArray(); 
		
		DocLib docLib = LibHelper.getLib(DocTypes.MEMBERINVITECODETREE.typeID(),"xy");
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] docs = docManager.find(docLib.getDocLibID()," SYS_DELETEFLAG=0 ",new Object[] {});
		if(docs != null && docs.length > 0){
			for(int i=0;i<docs.length;i++){
				Json.put("id",docs[i].getInt("SYS_DOCUMENTID"));
				Json.put("pid",docs[i].getInt("pid"));
				Json.put("icLevel",docs[i].getString("icLevel"));
				Json.put("hierarchy",docs[i].getInt("icHierarchy"));
				Json.put("icLevelIndex",docs[i].getString("icLevelIndex"));
				JsonArray.add(Json);
			}
		}
		
		output(String.valueOf(JsonArray), response);
	}
	@RequestMapping("/del.do")
	private void deleteInvitecode(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		String DocIDs = get(request, "DocIDs");
		if(DocIDs == null || DocIDs.length() == 0) return;
		
		DocLib docLib = LibHelper.getLib(DocTypes.MEMBERINVITECODE.typeID(),"xy");
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] docs = docManager.find(docLib.getDocLibID()," SYS_DOCUMENTID in(" 
		    + DocIDs + ") and SYS_DELETEFLAG=0 and icStatus = 1",new Object[] {});
		if(docs != null && docs.length > 0){
			output("已使用邀请码不能删除！", response);
		}else{
			Document[] doc = docManager.find(docLib.getDocLibID()," SYS_DOCUMENTID in(" 
	    	        + DocIDs + ") and SYS_DELETEFLAG=0 and icStatus = 0 ",new Object[] {});
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

	/**
	 * 获取level1
	 * @param 
	 * @return
	 * @throws E5Exception 
	 */
	@RequestMapping("/getLevel1.do")
	public void getLevel1(HttpServletRequest request,
			HttpServletResponse response, @SuppressWarnings("rawtypes") Map model) throws Exception {
		response.setHeader("Access-Control-Allow-Origin","*");
		response.setContentType("text/html;charset=UTF-8");
		//int siteID = Integer.parseInt(request.getParameter("siteID"));
		int siteID = getInt(request, "siteID",1);
		JSONObject Json = new JSONObject();
		JSONArray JsonArray = new JSONArray(); 
		
		DocLib docLib = LibHelper.getLib(DocTypes.MEMBERINVITECODETREE.typeID(),"xy");
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] docs = docManager.find(docLib.getDocLibID()," SYS_DELETEFLAG=0 and icHierarchy = 1 and pid != 0 ",new Object[] {});
		if(docs != null && docs.length > 0){
			for(int i=0;i<docs.length;i++){
				Json.put("icLevel1",docs[i].getString("icLevel"));
				JsonArray.add(docs[i].getString("icLevel"));
			}
		}
		
		output(String.valueOf(JsonArray), response);
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
    
    /**
	 * 邀请码修改页面初始化
	 * @param request
	 * @param response
	 * @param model
	 * @throws E5Exception
	 */
    @RequestMapping("/InviteCodeUpdateInit.do")
	private ModelAndView InviteCodeUpdateInit(HttpServletRequest request,
			HttpServletResponse response, Map model) throws E5Exception {
		
		DocLib docLib = LibHelper.getLib(DocTypes.MEMBERINVITECODE.typeID(),"xy");
		long docID = getInt(request, "DocIDs", 0);
		int s = getInt(request, "s",0);//0表示南方，1表示新华，两者的邀请码生成页面不一样
		model.put("DocLibID", docLib.getDocLibID());
		model.put("DocIDs", docID);
		model.put("FVID", docLib.getFolderID());
		model.put("UUID", get(request, "UUID"));
		String VIEWNAME = "";
		if(s == 1){
			if(docID>0){
				DocumentManager docManager = DocumentManagerFactory.getInstance();
				Document doc = docManager.get(docLib.getDocLibID(), docID);
				
				model.put("icType",doc.getString("icType"));
				model.put("icLevel1",doc.getString("icLevel1"));
				model.put("icLevel1Index",doc.getString("icLevel1Index"));
				model.put("icLevel2",doc.getString("icLevel2"));
				model.put("icLevel2Index",doc.getString("icLevel2Index"));
				model.put("icLevel3",doc.getString("icLevel3"));
				model.put("icLevel3Index",doc.getString("icLevel3Index"));
				model.put("icLevel4",doc.getString("icLevel4"));
				model.put("icLevel4Index",doc.getString("icLevel4Index"));
				
				model.put("icMetrics",doc.getInt("icMetrics"));
				model.put("icInviterName",doc.getString("icInviterName"));
				model.put("icExpiredDay",doc.getInt("icExpiredDay"));
			}
			//model.put("@VIEWNAME@", "amuc/invitecode/XUpdateInviteCode");//跳转到jsp页面
			VIEWNAME = "amuc/invitecode/XUpdateInviteCode";
		}
		else if(s == 0){
			if(docID>0){
				DocumentManager docManager = DocumentManagerFactory.getInstance();
				Document doc = docManager.get(docLib.getDocLibID(), docID);
				
				
			}
			
			model.put("@VIEWNAME@", "amuc/invitecode/UpdateCode");//跳转到jsp页面
		}
		return new ModelAndView(VIEWNAME,model);//跳转到jsp页面
	}
    
    /**
	 * 新华日报app修改邀请码
	 * @param request
	 * @param response
	 * @param model
	 * @throws E5Exception 
	 */
    @RequestMapping("/xupdate.do")
	private ModelAndView xupdate(HttpServletRequest request,
			HttpServletResponse response, Map model) throws E5Exception {
		
		int icType = getInt(request, "icType",2);//层级类型
		
		String icLevel1 = get(request, "icLevel1");
		String icLevel1Index = get(request, "icLevel1Index");
		String icLevel2 = get(request, "icLevel2","");
		String icLevel2Index = get(request, "icLevel2Index","");
		String icLevel3 = get(request, "icLevel3","");
		String icLevel3Index = get(request, "icLevel3Index","");
		String icLevel4 = get(request, "icLevel4","");
		String icLevel4Index = get(request, "icLevel4Index","");
		int icExpiredDay = getInt(request, "icExpiredDay",7);
		
		int icMetrics = getInt(request, "icMetrics",0);
		String icInviterName = get(request, "icInviterName");
		DocLib docLib = LibHelper.getLib(DocTypes.MEMBERINVITECODE.typeID(),"xy");
		long docID = getInt(request, "DocIDs", 0);
		int siteID = getInt(request, "siteID",1);
		
		if(docID>0){
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			Document doc = docManager.get(docLib.getDocLibID(), docID);
			
			doc.set("icCode", icLevel1Index+icLevel2Index+icLevel3Index+icLevel4Index);
			doc.set("icType", icType);
			doc.set("icLevel1", icLevel1);
			doc.set("icLevel2", icLevel2);
			doc.set("icLevel3", icLevel3);
			doc.set("icLevel4", icLevel4);
			
			doc.set("icLevel1Index", icLevel1Index);
			doc.set("icLevel2Index", icLevel2Index);
			doc.set("icLevel3Index", icLevel3Index);
			doc.set("icLevel4Index", icLevel4Index);
			
			doc.set("icMetrics", icMetrics);
			doc.set("icInviterName", icInviterName);
			doc.set("icExpiredDay", icExpiredDay);
			doc.set("m_siteID", siteID);
			docManager.save(doc);
		}
		
		String viewName = "redirect:/e5workspace/after.do?DocLibID=" + docLib.getDocLibID()
				+ "&DocIDs=" + docID + "&UUID=" + get(request, "UUID");
		//model.put("@VIEWNAME@", viewName);
		return new ModelAndView(viewName,model);//跳转到jsp页面
	}
    
    @RequestMapping("/upload.do")
	private void upload(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception {
    	Map<String, Object> maps = new HashMap<String, Object>();
    	String code = "";
		StringBuilder msg = new StringBuilder();
    	FileItem file = getFileItem(request);

		if (file==null||file.getSize() <= 0) {
			maps.put("msg", "上传头像接口：图片文件为空");
			maps.put("code", "0");
			JSONObject jsonres = JSONObject.fromObject(maps);
			outputJson(String.valueOf(jsonres), response);
			return;
		}
		HashMap<String, String> resultUp  =  uploadPortraitImp(file);
		if("sucess".equals(resultUp.get("code"))){
			
			saveScoreConfig(request,resultUp.get("httppath"));
			code = "1";
			msg.append(resultUp.get("httppath"));
			
		}else{
			code = "0";
			msg.append(resultUp.get("error"));
		}
		maps.put("code", code);
		maps.put("msg", msg.toString());
		JSONObject result = JSONObject.fromObject(maps);
		outputJson(String.valueOf(result), response);
    }
    
    public HashMap<String, String> uploadPortraitImp(FileItem file) throws Exception{
		
		//1.上传头像图片到头像发布服务器
		StorageDeviceReader deviceReader = (StorageDeviceReader)com.founder.e5.context.Context.getBean(StorageDeviceReader.class);
		StorageDevice device = deviceReader.getByName("头像存储");
		StorageDeviceManager deviceManager = (StorageDeviceManager)com.founder.e5.context.Context.getBean(StorageDeviceManager.class);
		
		HashMap<String, String> resu = new HashMap<String, String>();
		
		try {
			InputStream in = file.getInputStream();
			
			String fileName = file.getName();
			String reg = ".+(.JPEG|.jpeg|.JPG|.jpg|.GIF|.gif|.BMP|.bmp|.PNG|.png)$";
	        Pattern pattern = Pattern.compile(reg);
	        Matcher matcher = pattern.matcher(fileName.toLowerCase());
	        //System.out.println("--------------------"+matcher.find());
	        //判断图片后缀
	        if(matcher.find()){
	        	SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmm");
				String newPrefix = formatter.format(new Date());
				String fileExt = fileName.substring(fileName.lastIndexOf(".")+1).toLowerCase();
				String resfilename = "portraitat" + newPrefix + DateUtils.getTimestamp().getTime() + "." + fileExt;
	 			
				deviceManager.write(device, resfilename , in);
				
				String imgHttppath = device.getHttpDeviceURL()+"/"+resfilename;
				String imgFtppath = device.getFtpDeviceURL()+"/"+resfilename;
				//增加trans
				String webroot_pic = device.getNtfsDevicePath();
				if(webroot_pic != null){
					Date date = Calendar.getInstance().getTime();
					String ymdAddrExt = new SimpleDateFormat("yyyyMM~dd~").format(date);
					int index = webroot_pic.lastIndexOf("/");
					String nisPic = webroot_pic.substring(index+1);
					PublishHelper.writePath(nisPic + "~" + resfilename,PublishHelper.getTransPath());
				}
				resu.put("code", "sucess");
				resu.put("httppath", imgHttppath);
				resu.put("ftppath", imgFtppath);
	        }else{
	        	resu.put("code", "fail");
				resu.put("error", "头像文件格式不正确");
	        }
				
			
		} catch (Exception e) {
			e.printStackTrace();
			resu.put("code", "fail");
			resu.put("error", "头像文件上传失败");
		}
		return resu;		
	}
    
    //保存App的参数配置
  	@RequestMapping(value = {"SaveAppConfig.do"})
  	public void saveScoreConfig(HttpServletRequest request,String httppath) throws Exception {
  		//int siteLibID = WebUtil.getInt(request, "siteLibID", 0);
  		int siteID = WebUtil.getInt(request, "siteID", 1);
  		//int siteID = request.getParameter("siteID");
  		//int siteLibID = request.getParameter("siteLibID");
  		String AppName = WebUtil.get(request, "AppName");
  		
  		//取出站点的配置json，替换其中的积分设置json。
  		DocumentManager docManager = DocumentManagerFactory.getInstance();
  		DocLib docLib = LibHelper.getLib(DocTypes.SITE.typeID(),"xy");
  		Document site = docManager.get(docLib.getDocLibID(), siteID);
  		String siteConfig = site.getString("site_config");
  		
  		JSONObject jsonConfig = null;
  		JSONObject jsonobj = null;
  		if (StringUtils.isBlank(siteConfig)) {
  			jsonConfig = new JSONObject();
  		} else {
  			jsonConfig = JsonHelper.getJson(siteConfig);
  		}
  		if(jsonConfig.has("member")){
  			jsonobj = JsonHelper.getJson(jsonConfig.getString("member"));
  		}else{
  			jsonobj = new JSONObject();
  		}
  		
  		jsonobj.put("AppName", AppName);
  		jsonobj.put("AppHttppath", httppath);
  		jsonConfig.put("member", jsonobj);
  		site.set("site_config", jsonConfig.toString());
  		
  		docManager.save(site);
  	}
  	
  	/**
	 * 获取上传文件
	 * 
	 * @param request
	 * @return
	 * @throws FileUploadException
	 */
	@SuppressWarnings("unchecked")
	private static FileItem getFileItem(HttpServletRequest request) throws Exception {
		ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
		upload.setHeaderEncoding("UTF-8");
		upload.setSizeMax(10000000L);

		List<FileItem> items = upload.parseRequest(request);
		if(items.size()==0){
			return null;
		}
		FileItem file = (FileItem) items.get(0);
		return file;
	}
}
