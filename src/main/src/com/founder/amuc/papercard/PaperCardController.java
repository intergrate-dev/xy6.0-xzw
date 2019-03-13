package com.founder.amuc.papercard;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.founder.amuc.commons.InfoHelper;
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
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.LibHelper;
import com.founder.amuc.commons.DateFormatAmend;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import javax.swing.JOptionPane;
//import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import com.founder.e5.cat.CatReader;
import com.founder.e5.cat.CatType;
import com.founder.e5.cat.Category;

@Controller
@RequestMapping("/amuc/papercard")
public class PaperCardController extends BaseController {

	protected void handle(HttpServletRequest request, HttpServletResponse response, Map model) throws Exception {
		String action = get(request, "a");
		if ("pcardInit".equals(action)) {
			// 报卡生成页面初始化
			pcardInit(request, response, model);
		}
	}

	@RequestMapping("/pcardInit.do")
	private ModelAndView pcardInit(HttpServletRequest request, HttpServletResponse response, Map<String, Object> model)
			throws Exception {

		DocLib docLib = LibHelper.getLib(DocTypes.MEMBERPAPERCARD.typeID(), "xy");
		long docID = getInt(request, "DocIDs", 0);
		int p = getInt(request, "p", 0);
		model.put("DocLibID", docLib.getDocLibID());
		model.put("DocIDs", docID);
		model.put("FVID", docLib.getFolderID());
		model.put("UUID", get(request, "UUID"));

		String VIEWNAME = "";
		if (p == 0) {
			VIEWNAME = "amuc/papercard/papercardInit"; // 新版报卡生成
		} else if (p == 1) {
			VIEWNAME = "amuc/papercard/papercardInit2"; // 石油报版本报卡生成
		} else if (p == 2) {
			VIEWNAME = "amuc/papercard/papercardChange"; // 报卡修改
		}

		model.put("@VIEWNAME@", VIEWNAME);// 跳转到jsp页面
		return new ModelAndView(viewName, model);// 跳转到jsp页面
	}

	@RequestMapping("/CreatePaperCode.do")
	private ModelAndView createPaperCard(HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> model) throws Exception {

		String[] pcPaperName1=request.getParameterValues("pcPaperName");
		String pcPaperName = getCheckboxVal(request, "pcPaperName");
		String pcEffectTime = get(request, "pcEffectTime", ""); // 生效时间
		String pcExpireTime = get(request, "pcExpireTime", ""); // 失效时间
		String pcOperator = get(request, "pcOperator", ""); // 操作人
		String pcOperatorID = get(request, "pcOperatorID", "");
		String pcTypeCode = get(request, "pcTypeCode", ""); // 报纸种类代码
		String pcArea = get(request, "pcArea", ""); // 报纸地区代码
		String pcTotal1 = get(request, "pcTotal", ""); // 生成数量
		String pcExpiryDate = get(request, "pcExpiryDate", ""); // 连续自然日
		String pcMoney = get(request, "pcMoney", ""); // 报卡金额
		String pcReadway = getCheckboxVal(request, "pcReadway"); // 阅读渠道
		String pcRemind = getCheckboxVal(request, "pcRemind"); // 过期提醒
		String pcPay = get(request, "pcPay", ""); // 支付方式
		String pcTotalMoney = get(request, "pcTotalMoney", ""); // 报卡总额
		int siteID = Integer.parseInt(request.getParameter("siteID"));

		// 检查参数
		if (StringUtils.isBlank(pcPay) || StringUtils.isBlank(pcReadway) || StringUtils.isBlank(pcTypeCode)
				|| StringUtils.isBlank(pcMoney) || StringUtils.isBlank(pcTotal1) || StringUtils.isBlank(pcOperator)
				|| StringUtils.isBlank(pcOperatorID) || StringUtils.isBlank(pcArea)) {
			return new ModelAndView("", model);
		}
		
		String pcNo = "";

		SimpleDateFormat sdf1 = new SimpleDateFormat("yyMMdd");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String pcEffectTime1 = "";
		try {
			pcEffectTime1 = sdf1.format(new Date());
		} catch (Exception e) {
			e.printStackTrace();
			return new ModelAndView("", model);// 跳转到jsp页面
		}
		pcNo += pcTypeCode + pcArea + pcEffectTime1;
		pcNo = pcNo.replace("4", "H");
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib docLib = LibHelper.getLib(DocTypes.MEMBERPAPERCARD.typeID(), "xy");
		DocLib docLib_log = LibHelper.getLib(DocTypes.MEMBERPAPERCARDLOG.typeID(), "xy");
		DocLib docLib_typecode = LibHelper.getLib(DocTypes.MEMBERTYPECODE.typeID(), "xy");
		Document[] doc_typecode = docManager.find(docLib_typecode.getDocLibID(),
				" SYS_DELETEFLAG = 0 and pcTypeCode = ? ", new Object[] { pcTypeCode });
		if (doc_typecode == null || doc_typecode.length <= 0) {
			Document doc_typecode1 = docManager.newDocument(docLib_typecode.getDocLibID(),
					InfoHelper.getID(docLib_typecode.getDocTypeID()));
			doc_typecode1.setDeleteFlag(0);
			doc_typecode1.setFolderID(docLib_typecode.getFolderID());
			doc_typecode1.set("pcTypeCode", pcTypeCode);
			docManager.save(doc_typecode1);
		}

		/*String sqlNum = "SELECT sum(operationNum) num FROM xy_memberpapercard_log WHERE SYS_DELETEFLAG = 0 and cardPrefix like '"
				+ pcNo + "%'";
		int operationNum = selectCount(sqlNum);*/
		Document[] xy_memberpapercard_log = docManager.find(docLib.getDocLibID(),
				"  SYS_DELETEFLAG = 0 and pcNo like '"+ pcNo + "%' ", new Object[] {});
		int operationNum = xy_memberpapercard_log.length;

		int pcTotal = Integer.parseInt(pcTotal1);
		String curtime = df.format(new Date());
		String doclibs = "";

		if (pcTotal > 0 && pcTotal < 99999) {
			String init = "";
			if (operationNum > 0) {
				String baseStr = "000000";
				init = baseStr.substring(String.valueOf(operationNum).length()) + operationNum;
			} else {
				init = "000000";
			}
			int i = 0;
			try {
				for (i = 0; i < pcTotal; i++) {
					Document doc = docManager.newDocument(docLib.getDocLibID(),
							InfoHelper.getID(docLib.getDocTypeID()));
					doc.setDeleteFlag(0);
					doc.set("SYS_CREATED", DateFormatAmend.timeStampDispose(curtime));
					doc.set("pcPaperMark", pcPaperName1.length);
					doc.set("pcEffectTime", DateFormatAmend.DateDispose(pcEffectTime));
					doc.set("pcExpireTime", DateFormatAmend.DateDispose(pcExpireTime));
					doc.set("pcPaperName", pcPaperName);
					doc.set("pcOperator", pcOperator);
					doc.set("pcExpiryDate", pcExpiryDate == "" ? "--" : pcExpiryDate);
					doc.set("pcMoney", pcMoney);
					doc.set("pcReadway", Integer.parseInt(pcReadway));
					doc.set("pcRemind", pcRemind);
					doc.set("pcPay", pcPay);
					doc.set("pcPay_ID", getCatElementId(pcPay));// 下拉
					doc.set("pcStatus", 1);
					doc.set("m_siteID", siteID);
					doc.set("pcActiveStatus", "未激活");
					doc.setFolderID(docLib.getFolderID());
					// 报卡
					init = String.valueOf(increStrsys(init));
					String pcNo1 = new String(pcNo + init);
					int pcPassword = new Random().nextInt(899999) + 100000;
					doc.set("pcPassword", pcPassword);
					doc.set("pcNo", pcNo1);
					docManager.save(doc);
					Document[] doc1 = docManager.find(docLib.getDocLibID(), " SYS_DELETEFLAG = 0 and pcNo = ? ",
							new Object[] { pcNo1 });
					String DocID = doc1[0].getString("SYS_DOCUMENTID");
					doclibs = doclibs+","+DocID;
					
				}
				doclibs = doclibs.substring(1, doclibs.length());
				savePaperRecord(docManager, docLib_log, curtime, pcNo, pcTotal, pcEffectTime, pcExpireTime, pcTotal,
						pcOperator, pcOperatorID, pcPaperName, pcTotalMoney,siteID);
			} catch (Exception e) {
				savePaperRecord(docManager, docLib_log, curtime, pcNo, pcTotal, pcEffectTime, pcExpireTime, i,
						pcOperator, pcOperatorID, pcPaperName, pcTotalMoney,siteID);
				String viewName = "redirect:/e5workspace/after.do?DocLibID=" + docLib.getDocLibID() + "&DocIDs=" + doclibs
						+ "&UUID=" + get(request, "UUID");
				model.put("Info", "生成失败，请稍后重试");
				model.put("@VIEWNAME@", viewName);
				e.printStackTrace();
			}

		} else {
			savePaperRecord(docManager, docLib_log, curtime, pcNo, pcTotal, pcEffectTime1, pcExpireTime, 0, pcOperator,
					pcOperatorID, pcPaperName, pcTotalMoney,siteID);
			String viewName = "redirect:/e5workspace/after.do?DocLibID=" + docLib.getDocLibID() + "&DocIDs=" + doclibs
					+ "&UUID=" + get(request, "UUID");
			model.put("Info", "请正确输入生成数量");
			model.put("@VIEWNAME@", viewName);
			return new ModelAndView(viewName, model);
		}
		String viewName = "redirect:/e5workspace/after.do?DocLibID=" + docLib.getDocLibID() + "&DocIDs=" +  doclibs + "&UUID="
				+ get(request, "UUID");
		model.put("@VIEWNAME@", viewName);
		return new ModelAndView(viewName, model);
	}

	private void savePaperRecord(DocumentManager docManager, DocLib docLib_log, String curtime, String pcNo,
			int pcTotal, String pcEffectTime, String pcExpireTime, int status, String pcOperator, String pcOperatorID,
			String paperName, String pcTotalMoney, int siteID) throws Exception {
		try {
			Document docLog = docManager.newDocument(docLib_log.getDocLibID());
			docLog.set("SYS_CREATED", DateFormatAmend.timeStampDispose(curtime));
			docLog.setFolderID(docLib_log.getFolderID());
			docLog.set("cardPrefix", pcNo);
			docLog.set("operationNum", pcTotal);
			docLog.set("pcTotalMoney", pcTotalMoney); // 报卡总额
			docLog.set("effectTime", DateFormatAmend.DateDispose(pcEffectTime));
			docLog.set("expireTime", DateFormatAmend.DateDispose(pcExpireTime));
			docLog.set("paperName", paperName);
			docLog.set("operation", "新建");
			docLog.set("status", "已售");// 对应状态
			docLog.set("m_siteID", siteID);
			if (status == pcTotal) {
				docLog.set("detail", "成功生成" + status + "张");
			} else {
				docLog.set("detail", "失败，仅生成" + status + "张");
			}
			docLog.set("operator", "后台：" + pcOperator);
			docLog.set("operatorid", pcOperatorID);
			docManager.save(docLog);
		} catch (E5Exception e) {
			e.printStackTrace();
		}
	}

	private String getCheckboxVal(HttpServletRequest request, String val) {
		String vals = "";

		for (String val1 : request.getParameterValues(val)) {
			vals += val1 + ",";
		}
		if (vals.indexOf(",") > 0) {

			vals = vals.substring(0, vals.length() - 1);
		}
		return vals;
	}

	// 查询记录数
	private int selectCount(String querySql) {
		int num = 0;
		DBSession conn = null;
		IResultSet rs = null;
		try {
			conn = Context.getDBSession();
			rs = conn.executeQuery(querySql.toString());
			while (rs.next()) {
				num = rs.getInt("NUM");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}
		return num;
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

	@RequestMapping("/pcardchange.do")
	private ModelAndView pcardchange(HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> model) throws Exception {

		String docid = getCheckboxVal(request, "DocID");
		String pcReadway = getCheckboxVal(request, "pcReadway"); // 阅读渠道
		String pcRemind = getCheckboxVal(request, "pcRemind"); // 过期提醒
		String pcStatus = get(request, "pcStatus", ""); // 状态
		String pcRemarks = get(request, "pcRemarks", "");

		if (StringUtils.isBlank(pcReadway) || StringUtils.isBlank(pcRemind) || StringUtils.isBlank(pcStatus)) {
			return new ModelAndView("", model);// 跳转到jsp页面
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String curtime = df.format(new Date());
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib docLib = LibHelper.getLib(DocTypes.MEMBERPAPERCARD.typeID(), "xy");
		DocLib docLib_log = LibHelper.getLib(DocTypes.MEMBERPAPERCARDLOG.typeID(), "xy");

		Document[] doc = docManager.find(docLib.getDocLibID(), " SYS_DELETEFLAG = 0 and SYS_DOCUMENTID = ? ",
				new Object[] { docid });
		doc[0].setDeleteFlag(0);

		doc[0].set("pcReadway", Integer.parseInt(pcReadway));
		doc[0].set("pcRemind", pcRemind);
		doc[0].set("pcStatus", Integer.parseInt(pcStatus));
		doc[0].set("pcRemarks", pcRemarks);
		doc[0].setFolderID(docLib.getFolderID());
		docManager.save(doc[0]);

		String pcOperator = get(request, "pcOperatorName", ""); // 操作人
		String pcOperatorID = get(request, "pcOperatorID", "");
		String pcNo = String.valueOf(doc[0].get("pcNo"));
		String pcEffectTime = String.valueOf(doc[0].get("pcEffectTime"));
		String pcExpireTime = String.valueOf(doc[0].get("pcExpireTime"));
		String pcPaperName = String.valueOf(doc[0].get("pcPaperName"));
		String pcTotalMoney = String.valueOf(doc[0].get("pcMoney"));
		savemodifyPaperRecord(docManager, docLib_log, curtime, pcNo, 1, pcEffectTime, pcExpireTime, 1, pcOperator,
				pcOperatorID, pcPaperName, pcTotalMoney, Integer.parseInt(doc[0].getString("m_siteID")));
		String viewName = "redirect:/e5workspace/after.do?DocLibID=" + docLib.getDocLibID() + "&DocIDs=" + docid
				+ "&UUID=" + get(request, "UUID");
		model.put("@VIEWNAME@", viewName);
		return new ModelAndView(viewName, model);// 跳转到jsp页面
	}

	private void savemodifyPaperRecord (DocumentManager docManager, DocLib docLib_log, String curtime, String pcNo,
			int pcTotal, String pcEffectTime, String pcExpireTime, int status, String pcOperator, String pcOperatorID,
			String paperName, String pcTotalMoney,int siteID) throws Exception {
		try {
			Document docLog = docManager.newDocument(docLib_log.getDocLibID());
			docLog.set("SYS_CREATED", DateFormatAmend.timeStampDispose(curtime));
			docLog.setFolderID(docLib_log.getFolderID());
			docLog.set("cardPrefix", pcNo);
			docLog.set("operationNum", pcTotal);
			docLog.set("m_siteID", siteID);
			docLog.set("pcTotalMoney", pcTotalMoney); // 报卡总额
			if (pcEffectTime != "" && pcEffectTime != "null") {
				docLog.set("effectTime", DateFormatAmend.DateDispose(pcEffectTime));
				docLog.set("expireTime", DateFormatAmend.DateDispose(pcExpireTime));
			}
			docLog.set("paperName", paperName);
			docLog.set("operation", "修改");
			docLog.set("status", "已售");// 对应状态
			docLog.set("detail", "成功修改" + status + "张");
			docLog.set("operator", "后台：" + pcOperator);
			docLog.set("operatorid", pcOperatorID);
			docManager.save(docLog);
		} catch (E5Exception e) {
			e.printStackTrace();
		}
	}

	@RequestMapping("/pcardLog.do")
	private void pcardLog(HttpServletRequest request, HttpServletResponse response,
			@SuppressWarnings("rawtypes") Map model) throws Exception {

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib docLib = LibHelper.getLib(DocTypes.MEMBERPAPERCARD.typeID(), "xy");
		DocLib docLib_log = LibHelper.getLib(DocTypes.MEMBERPAPERCARDLOG.typeID(), "xy");
		long docID = getInt(request, "DocIDs", 0);
		// 查询系统中是否存在uid的会员
		JSONObject obj = new JSONObject();
		String condition = "SYS_DOCUMENTID = ? and SYS_DELETEFLAG = 0";
		String[] column = { "pcNo", "pcActiveStatus", "SYS_CREATED", "pcOperator", "pcMember", "pcActiveTime" };
		Document[] members = docManager.find(docLib.getDocLibID(), condition, new Object[] { docID }, column);
		if (members != null && members.length > 0) {

			obj.put("pcNo", members[0].getString("pcNo"));
			obj.put("pcActiveStatus", members[0].getString("pcActiveStatus"));
			obj.put("SYS_CREATED", members[0].getString("SYS_CREATED"));
			obj.put("pcOperator", members[0].getString("pcOperator"));
			obj.put("pcMember", members[0].getString("pcMember"));
			obj.put("pcActiveTime", members[0].getString("pcActiveTime"));
			String conditionLog = "cardPrefix = ? and SYS_DELETEFLAG = 0";
			String[] columnLog = { "SYS_CREATED", "operator" };
			Document[] membersLog = docManager.find(docLib_log.getDocLibID(), conditionLog,
					new Object[] { members[0].getString("pcNo") }, columnLog);
			// return obj.toString();
			int logLength = membersLog.length;
			if (logLength > 0) {
				JSONArray log = new JSONArray();
				obj.put("logLength", logLength);
				for (int j = 0; j < logLength; j++) {
					JSONObject LogCreate = new JSONObject();
					LogCreate.put("LogCreate", membersLog[j].getString("SYS_CREATED"));
					LogCreate.put("operator", membersLog[j].getString("operator").substring(3, membersLog[j].getString("operator").length()));
					log.add(LogCreate);			
				}
				obj.put("log", JSONArray.fromObject(log).toString());
			}

			output(obj.toString(), response);
		} else {
			return;
		}

	}

	@RequestMapping("/papercardLog.do")
	private ModelAndView papercardLog(HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> model) throws E5Exception {

		JSONArray pcno = new JSONArray();
		DocLib docLib = LibHelper.getLib(DocTypes.MEMBERPAPERCARDLOG.typeID(), "xy");
		long docID = getInt(request, "DocIDs", 0);

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib pcdocLib = LibHelper.getLib(DocTypes.MEMBERPAPERCARD.typeID(), "xy");
		DocLib pcdocLibLog = LibHelper.getLib(DocTypes.MEMBERPAPERCARDLOG.typeID(), "xy");
		String pccondition = " SYS_DELETEFLAG = 0 and SYS_DOCUMENTID = ? ";

		Document[] pcardLog = docManager.find(pcdocLibLog.getDocLibID(), pccondition, new Object[] { docID });
		String cardPrefix = pcardLog[0].getString("cardPrefix");
		String pccondition1 = " SYS_DELETEFLAG = 0 and pcNo like '" + cardPrefix + "%' ";
		Document[] pcards = docManager.find(pcdocLib.getDocLibID(), pccondition1, new Object[] {});
		for (int i = 0; i < pcards.length; i++) {
			pcno.add(pcards[i].get("pcNo"));
		}
		String VIEWNAME = "";
		VIEWNAME = "amuc/papercard/detail"; // 订单详情页面

		model.put("DocLibID", docLib.getDocLibID());
		model.put("DocIDs", docID);
		model.put("FVID", docLib.getFolderID());
		model.put("UUID", get(request, "UUID"));
		model.put("PCNO", pcno.toString());
		model.put("@VIEWNAME@", VIEWNAME);
		return new ModelAndView(VIEWNAME, model);// 跳转到jsp页面
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

	//选择报纸功能
	@RequestMapping("/selectPaper.do")
	private void selectPaper(HttpServletRequest request,
			HttpServletResponse response, @SuppressWarnings("rawtypes") Map model) throws Exception {
		long docID = getInt(request, "DocIDs", 0);
		int type = getInt(request, "type");//0报卡，1套餐
		int siteID = Integer.parseInt(request.getParameter("siteID"));
		JSONArray paper = new JSONArray();
		JSONObject obj = new JSONObject();
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		String condition1 = "SYS_DOCUMENTID = ? and m_siteID = ? and SYS_DELETEFLAG = 0";
		Document[] doc;
		String content="";
		if(type==0){
			DocLib pcdocLib = LibHelper.getLib(DocTypes.MEMBERPAPERCARD.typeID(),"xy");
			doc = docManager.find(pcdocLib.getDocLibID(), condition1,new Object[] { docID,siteID });
			if (doc != null && doc.length > 0) {
				content=doc[0].getString("pcPaperName");
			}
		}else if(type==1){
			DocLib setmelLib = LibHelper.getLib(DocTypes.MEMBERSETMEAL.typeID(),"xy");
			doc = docManager.find(setmelLib.getDocLibID(), condition1,new Object[] { docID,siteID });
			if (doc != null && doc.length > 0) {
				content=doc[0].getString("setMealContent");
			}
		}
		
		String[] sourceStrArray=content.split(",");
		DocLib docLib = LibHelper.getLib(DocTypes.PAPER.typeID(),"xy");
		String condition = " pa_siteID = ? and pa_status = 0 and SYS_DELETEFLAG = 0 ";
		Document[] papers = docManager.find(docLib.getDocLibID(), condition,new Object[] {siteID});
		if (papers != null && papers.length > 0) {
			for(int i=0;i<papers.length;i++){
			  int check=0;
			  for(int j=0;j<sourceStrArray.length;j++){
				  if(papers[i].getString("pa_name").equals(sourceStrArray[j])){
					  check=1;//0没选中，1选中
				
				  }
			  }
			  	obj.put("check", check);//0没选中，1选中
				obj.put("id", papers[i].getString("SYS_DOCUMENTID"));
				obj.put("pa_name", papers[i].getString("pa_name"));	
				paper.add(obj);
			}
			output(paper.toString(), response);
		}else{
			return ;
		}
	}
	
	//选择报纸功能
	@RequestMapping("/selectPaper1.do")
	private void selectPaper1(HttpServletRequest request,
			HttpServletResponse response, @SuppressWarnings("rawtypes") Map model) throws Exception {

		org.dom4j.Document dom=DocumentHelper.createDocument();//创建xml文件
		Element root=dom.addElement("tree");//添加根元素
        
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib docLib = LibHelper.getLib(DocTypes.PAPER.typeID(),"xy");
		JSONArray paper = new JSONArray();
		String condition = " pa_status = 0 and SYS_DELETEFLAG = 0 ";
		Document[] papers = docManager.find(docLib.getDocLibID(), condition,new Object[] {});
		if (papers != null && papers.length > 0) {
			for(int i=0;i<papers.length;i++){
				paper.add(papers[i].getString("pa_name"));
				
				org.dom4j.Element title=root.addElement("tree");
				title.addAttribute("text", papers[i].getString("pa_name"));
		        title.addAttribute("icon", "localhost");
		        title.addAttribute("catType", "16");
		        title.addAttribute("catID", papers[i].getString("SYS_DOCUMENTID"));
		        title.addAttribute("title", papers[i].getString("pa_name"));
		        title.addAttribute("cascadeName", papers[i].getString("pa_name"));
		        title.addAttribute("cascadeID", papers[i].getString("SYS_DOCUMENTID"));
		        title.addAttribute("click", "catClick(this)");
			}
			String xml=dom.asXML();
			output(xml.substring(38, xml.length()), response,true);
		}else{
			return ;
		}
	}
}
