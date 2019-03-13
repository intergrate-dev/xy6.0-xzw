package com.founder.amuc.jobs;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import com.founder.amuc.commons.Constant;
import com.founder.amuc.commons.InfoHelper;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.e5.scheduler.BaseJob;
import com.founder.xy.system.site.SiteManager;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.commons.DocTypes;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderPaperStaticJob extends BaseJob{
	@Autowired
	private SiteManager siteManager;
	private String SQL_VALID_SITES = "site_status is null or site_status=0";

	@Override
	protected void execute() throws E5Exception {
		System.out.println("---------订单报卡统计存库任务  开始---------");
		SimpleDateFormat sim=new SimpleDateFormat("yyy-MM-dd");
		Date date=new Date();
		String endTime=sim.format(date);
		Calendar   calendar   =   new   GregorianCalendar(); 
		calendar.setTime(date); 
		calendar.add(calendar.DATE,-1);
		date=calendar.getTime();
		String beginTime=sim.format(date);
		
		int siteLibID = LibHelper.getLibID(DocTypes.SITE.typeID(), "xy");
		int siteCount = getSiteCount(siteLibID);//获取系统站点总数
		DocLib orderDocLib = InfoHelper.getLib(Constant.DOCTYPE_ORDERS);
		DocLib SetMealDocLib = InfoHelper.getLib(Constant.DOCTYPE_SETMEAL);
		DocLib PaperCardDocLib = InfoHelper.getLib(Constant.DOCTYPE_PAPERCARD);
		DocLib TypeCodeDocLib = InfoHelper.getLib(Constant.DOCTYPE_TYPECODE);
		DocLib OrderStaticDocLib = null;
		DocLib PaperCardDocSTATICDocLib = null;
		DocLib[] StaticDocLibs = InfoHelper.getLibsByCode(Constant.DOCTYPE_STATIC);
		
		for (DocLib docLib : StaticDocLibs) {
			if(docLib.getDocLibName().equals("会员订单统计")){
				OrderStaticDocLib=docLib;
			}
			if(docLib.getDocLibName().equals("会员报卡统计")){
				PaperCardDocSTATICDocLib=docLib;
			}
		}
		//站点循环，每个站点查询存储一次
		Document[] docs = getSites("xy");
		for(int i=0;i<siteCount;i++){
			
			int siteID = Integer.parseInt(docs[i].getString("SYS_DOCUMENTID"));
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			Document[] orders = docManager.find(orderDocLib.getDocLibID(),
					" SYS_DELETEFLAG=0  and payTime>=?  and payTime<? and payStatus = ? and m_siteID=? ",
					new Object[] { beginTime,endTime,"1",siteID});
			int setMealnumber=0;
			double setMealMoney=0;
			Map<Long, Long> map=new HashMap<Long, Long>();
			for (Document document : orders) {
			String	ids=(String) document.get("setMealId");
				for (String id : ids.split(",")) {
					setMealnumber+=1;
					if(map.get(Long.parseLong(id))==null){
						map.put(Long.parseLong(id),(long)1);
					}else{
					map.put(Long.parseLong(id), map.get(Long.parseLong(id))+1);
					}
				}
				
			}
			StringBuffer str=new StringBuffer();
			str.append("[");
			for (long docid : map.keySet()) {
				Document doc=docManager.get(SetMealDocLib.getDocLibID(), docid);
				double money = doc.getDouble("setMealMoney");
				setMealMoney+=money*map.get(docid);
				str.append("{id:").append(docid).append(",data:{number:").append(map.get(docid)).append(",money:").append(money*map.get(docid)).append("}},");
			}
			str.append("]");
			if(OrderStaticDocLib!=null){
			Document OrderStaticDoc = docManager.newDocument(OrderStaticDocLib.getDocLibID());
			OrderStaticDoc.set("Time", beginTime);
			OrderStaticDoc.set("Data", str.toString());
			OrderStaticDoc.set("Totality", setMealnumber);
			OrderStaticDoc.set("TotalMoney", setMealMoney);
			OrderStaticDoc.set("m_siteID", siteID);
			docManager.save(OrderStaticDoc);
			}
			Document[]  PaperCards = docManager.find(PaperCardDocLib.getDocLibID(),
					"pcStatus = ?  and SYS_DELETEFLAG=0  and activeTime>=?  and activeTime<? and m_siteID=? and pcActiveStatus=?",
					new Object[] { "1" ,beginTime,endTime,siteID,"激活"});
			int totalCardNumber=0;
			BigDecimal totalCardMoney= new BigDecimal("0");
			Map<String, Object> papermap=new HashMap<String, Object>();
			for (Document document : PaperCards) {
			String	pcNo=(String) document.get("pcNo");
					pcNo=pcNo.substring(0, 4);
					totalCardNumber += 1;
					totalCardMoney = totalCardMoney.add(document.getBigDecimal("pcMoney"));
					if(papermap.get(pcNo+"number")==null){
						papermap.put(pcNo+"number",1);
						papermap.put(pcNo+"money",document.getBigDecimal("pcMoney"));
					}else{
						papermap.put(pcNo+"number",Integer.parseInt( papermap.get(pcNo+"number").toString())+1);
						papermap.put(pcNo+"money",new BigDecimal(papermap.get(pcNo+"money").toString()).add(document.getBigDecimal("pcMoney")));
				}
				
			}
			
			StringBuffer stc=new StringBuffer();
			stc.append("[");
			for (String code : papermap.keySet()) {
				if(code.indexOf("number")>0){
					code=code.replace("number", "");
					
					Document[]  codedoc = docManager.find(TypeCodeDocLib.getDocLibID(),"pcTypeCode = ?",
							new Object[] {code});
					if(codedoc!=null&&codedoc.length>0){
							stc.append("{id:").append(codedoc[0].getDocID()).append(",data:{number:").append(papermap.get(code+"number")).append(",money:").append(papermap.get(code+"money")).append("}},");
			
					}
				}
			}
			stc.append("]");
			if(PaperCardDocSTATICDocLib!=null){
				Document PaperCardDocSTATICDoc = docManager.newDocument(PaperCardDocSTATICDocLib.getDocLibID());
				PaperCardDocSTATICDoc.set("Time", beginTime);
				PaperCardDocSTATICDoc.set("Data", stc.toString());
				PaperCardDocSTATICDoc.set("Totality", totalCardNumber);
				PaperCardDocSTATICDoc.set("TotalMoney", totalCardMoney.setScale(2, BigDecimal.ROUND_HALF_UP));
				PaperCardDocSTATICDoc.set("m_siteID", siteID);
				docManager.save(PaperCardDocSTATICDoc);
			}
		
		}
		System.out.println("---------订单报卡统计存库任务  结束---------");
	}
	
	/**
	 * 取所有的站点
	 */
	public Document[] getSites(String tenantCode) throws E5Exception {
		int siteLibID = LibHelper.getLibID(DocTypes.SITE.typeID(), tenantCode);
	
		DocumentManager docManager = DocumentManagerFactory.getInstance();	
		Document[] sites = docManager.find(siteLibID, SQL_VALID_SITES, null);
		return sites;
	}

	/**
	 * 取站点数
	 */
	public int getSiteCount(int siteLibID) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			Document[] docs = docManager.find(siteLibID, "SYS_DELETEFLAG=0", null);
			return docs.length;
		} catch (E5Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

}
