package com.founder.amuc.action;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.founder.amuc.commons.Constant;
import com.founder.amuc.commons.InfoHelper;
import com.founder.e5.commons.DomHelper;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.DBType;
import com.founder.e5.db.IResultSet;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.LibHelper;

//@Component
public class ReadPermissionManager {

  public void getAllPermission(String tenantcode,DocLib paperDocLib,DocLib cardDocLib,DocLib mealDocLib,DocLib orderDocLib,DocLib permissionDocLib,int siteID) throws E5Exception {
    List<String> allPapers = null;
    List<String> chargeablePapers = null;
    List<String> freePapers = null;
    String dbType = DomHelper.getDBType();
    
    Map<String,String> paperMealMap = new HashMap();
    Map<String,String> paperCardMap = new HashMap();
    
    DocumentManager docManager = DocumentManagerFactory.getInstance();
    //所有报纸信息
    Document[] paperDocs = docManager.find(paperDocLib.getDocLibID(), "SYS_DELETEFLAG=0 and pa_status=0 and pa_siteID="+siteID,null);
    allPapers = new ArrayList<String>();
    for (Document doc : paperDocs) {
      allPapers.add(doc.get("pa_name").toString());
    }
    //所有收费报纸
    chargeablePapers = new ArrayList<String>();
    //所有报卡信息
    Document[] cardDocs = null;
    if (dbType.equals(DBType.ORACLE)) {
      cardDocs = docManager.find(cardDocLib.getDocLibID(), "SYS_DELETEFLAG = 0 and ((TO_CHAR(sysdate,'YYYY-MM-DD') <= pcExpireTime and TO_CHAR(sysdate,'YYYY-MM-DD') >= pcEffectTime) or (TO_CHAR(sysdate,'YYYY-MM-DD') <= TO_CHAR(SYS_CREATED+pcExpiryDate,'yyyy-mm-dd HH24:MI:SS'))) and pcStatus=1 and m_siteID="+siteID,null);
    }else{
      cardDocs = docManager.find(cardDocLib.getDocLibID(), "SYS_DELETEFLAG = 0 and ((date_format(now(),'%y-%m-%d') <= pcExpireTime and date_format(now(),'%y-%m-%d') >= pcEffectTime) or (date_format(now(),'%y-%m-%d') <= date_add(SYS_CREATED, INTERVAL pcExpiryDate DAY))) and pcStatus=1 and m_siteID="+siteID,null);
    }
    for(Document doc : cardDocs) {
      String[] papers = doc.get("pcPaperName").toString().split(",");
      List<String> newPaperList = new ArrayList<>(Arrays.asList(papers));
      for (String paperName : newPaperList) {
        if (paperCardMap.get(paperName) != null) {
          paperCardMap.put(paperName, paperCardMap.get(paperName) + "," + doc.get("pcNo").toString().substring(0,4));
        } else {
          paperCardMap.put(paperName, doc.get("pcNo").toString().substring(0,4));
        }
      }
      newPaperList.removeAll(chargeablePapers); 
      chargeablePapers.addAll(newPaperList);
    }
    //所有套餐信息
    Document[] mealDocs = null;
    if (dbType.equals(DBType.ORACLE)) {
    	mealDocs = docManager.find(mealDocLib.getDocLibID(), "SYS_DELETEFLAG=0 and ((TO_CHAR(sysdate,'YYYY-MM-DD') <= ExpireTime and TO_CHAR(sysdate,'YYYY-MM-DD') >= EffectTime) or (TO_CHAR(sysdate,'YYYY-MM-DD') <= TO_CHAR(CreationTime+SUBSTR(expiryDate,0,length(expiryDate)-1),'yyyy-mm-dd HH24:MI:SS'))) and m_siteID="+siteID,null);
      }else{
    	  mealDocs = docManager.find(mealDocLib.getDocLibID(), "SYS_DELETEFLAG=0 and ((date_format(now(),'%y-%m-%d') <= ExpireTime and date_format(now(),'%y-%m-%d') >= EffectTime) or (date_format(now(),'%y-%m-%d') <= date_add(CreationTime, INTERVAL expiryDate DAY))) and m_siteID="+siteID,null);
      }
    for(Document doc : mealDocs) {
      String[] papers = doc.get("setMealContent").toString().split(",");
      List<String> newPaperList = new ArrayList<>(Arrays.asList(papers));
      for (String paperName : newPaperList) {
        if (paperMealMap.get(paperName) != null) {
          paperMealMap.put(paperName, paperMealMap.get(paperName) + "," + doc.get("setMealNmae"));
        } else {
          paperMealMap.put(paperName, doc.get("setMealNmae").toString());
        }
      }
      newPaperList.removeAll(chargeablePapers);
      chargeablePapers.addAll(newPaperList);
    }
    freePapers = (List)((ArrayList)allPapers).clone();
    freePapers.removeAll(chargeablePapers);
    //报纸信息同步到阅读设置表
    //设置收费报纸的阅读权限
    for (String paperName : chargeablePapers) {
      Document paperDoc = docManager.find(paperDocLib.getDocLibID(), "pa_name=? and pa_siteID="+siteID, new Object[]{paperName})[0];
      paperDoc.set("pa_chargeable", 1);
      paperDoc.set("pa_meal", paperMealMap.get(paperName));
      paperDoc.set("pa_pcNo", paperCardMap.get(paperName));
      docManager.save(paperDoc);
    }
    //设置免费报纸的阅读权限
    for (String paperName : freePapers) {
      Document paperDoc = docManager.find(paperDocLib.getDocLibID(), "pa_name=? and pa_siteID="+siteID, new Object[]{paperName})[0];
      paperDoc.set("pa_chargeable", 0);
      paperDoc.set("pa_meal", "");
      paperDoc.set("pa_pcNo", "");
      docManager.save(paperDoc);
    }
  }

  public Map<String, Object> getUserPermission(String uid,String tenantcode,DocLib paperDocLib,DocLib cardDocLib,DocLib mealDocLib,DocLib mDocLib,DocLib orderDocLib,DocLib permissionDocLib,int siteID) throws E5Exception {
    List<String> allPapers = null;
    List<String> chargeablePapers = null;
    List<String> paidPapers = null;
    List<String> unpaidPapers = null;
    List<String> freePapers = null;
    DBSession dbSession = null;
    IResultSet rs = null;
    String dbType = DomHelper.getDBType();
    DocumentManager docManager = DocumentManagerFactory.getInstance();
    //所有报纸信息
    Document[] paperDocs = docManager.find(paperDocLib.getDocLibID(), "SYS_DELETEFLAG=0 and pa_status=0 and pa_siteID="+siteID,null);
    allPapers = new ArrayList<String>();
    for (Document doc : paperDocs) {
      allPapers.add(doc.get("pa_name").toString());
    }
    //所有收费报纸
    chargeablePapers = new ArrayList<String>();
    //所有报卡信息
    Document[] cardDocs = null;
    if (dbType.equals(DBType.ORACLE)) {
    	cardDocs = docManager.find(cardDocLib.getDocLibID(), "SYS_DELETEFLAG = 0 and ((TO_CHAR(sysdate,'YYYY-MM-DD') <= pcExpireTime and TO_CHAR(sysdate,'YYYY-MM-DD') >= pcEffectTime) or (TO_CHAR(sysdate,'YYYY-MM-DD') <= TO_CHAR(SYS_CREATED+pcExpiryDate,'yyyy-mm-dd HH24:MI:SS'))) and pcStatus=1 and m_siteID="+siteID,null);
      }else{
    	  cardDocs = docManager.find(cardDocLib.getDocLibID(), "SYS_DELETEFLAG = 0 and ((date_format(now(),'%y-%m-%d') <= pcExpireTime and date_format(now(),'%y-%m-%d') >= pcEffectTime) or (date_format(now(),'%y-%m-%d') <= date_add(SYS_CREATED, INTERVAL pcExpiryDate DAY))) and pcStatus=1 and m_siteID="+siteID,null);
      }
    for(Document doc : cardDocs) {
      String[] papers = doc.get("pcPaperName").toString().split(",");
      List<String> newPaperList = new ArrayList<>(Arrays.asList(papers));
      newPaperList.removeAll(chargeablePapers); 
      chargeablePapers.addAll(newPaperList);
    }
    //所有套餐信息
    Document[] mealDocs = null;
    if (dbType.equals(DBType.ORACLE)) {
    	mealDocs = docManager.find(mealDocLib.getDocLibID(), "SYS_DELETEFLAG=0 and ((TO_CHAR(sysdate,'YYYY-MM-DD') <= ExpireTime and TO_CHAR(sysdate,'YYYY-MM-DD') >= EffectTime) or (TO_CHAR(sysdate,'YYYY-MM-DD') <= TO_CHAR(CreationTime+SUBSTR(expiryDate,0,length(expiryDate)-1),'yyyy-mm-dd HH24:MI:SS'))) and m_siteID="+siteID,null);
      }else{
    	  mealDocs = docManager.find(mealDocLib.getDocLibID(), "SYS_DELETEFLAG=0 and ((date_format(now(),'%y-%m-%d') <= ExpireTime and date_format(now(),'%y-%m-%d') >= EffectTime) or (date_format(now(),'%y-%m-%d') <= date_add(CreationTime, INTERVAL expiryDate DAY))) and m_siteID="+siteID,null);
      }
    for(Document doc : mealDocs) {
      String[] papers = doc.get("setMealContent").toString().split(",");
      List<String> newPaperList = new ArrayList<>(Arrays.asList(papers));
      newPaperList.removeAll(chargeablePapers);
      chargeablePapers.addAll(newPaperList);
    }
    freePapers = (List)((ArrayList)allPapers).clone();
    freePapers.removeAll(chargeablePapers);
    //结果集合
    Map<String, Object> result = new LinkedHashMap<String, Object>();
    //所有会员信息
    Document[] memberDocs = null;
    if (uid != null) {
      memberDocs = docManager.find(mDocLib.getDocLibID(), "SYS_DELETEFLAG=0 and SYS_DOCUMENTID=? and m_siteID="+siteID, new Object[]{uid});
    }
    if (uid != null && memberDocs != null && memberDocs.length > 0) {
      // is member
      Document memberDoc = memberDocs[0];
      result.put("isMember", true);
      //已付费报纸
      paidPapers = new ArrayList<String>();
      //所有会员报卡信息
      Document[] mcardDocs = null;
      if (dbType.equals(DBType.ORACLE)) {
    	  mcardDocs = docManager.find(cardDocLib.getDocLibID(), "SYS_DELETEFLAG = 0 and pcMember_ID = ? and ((TO_CHAR(sysdate,'YYYY-MM-DD') <= pcExpireTime and TO_CHAR(sysdate,'YYYY-MM-DD') >= pcEffectTime) or (TO_CHAR(sysdate,'YYYY-MM-DD') <= TO_CHAR(SYS_CREATED+pcExpiryDate,'yyyy-mm-dd HH24:MI:SS'))) and pcStatus=1 and pcActiveStatus='激活' and m_siteID="+siteID,new Object[]{uid});
        }else{
        	mcardDocs = docManager.find(cardDocLib.getDocLibID(), "SYS_DELETEFLAG = 0 and pcMember_ID = ? and ((date_format(now(),'%y-%m-%d') <= pcExpireTime and date_format(now(),'%y-%m-%d') >= pcEffectTime) or (date_format(now(),'%y-%m-%d') <= date_add(SYS_CREATED, INTERVAL pcExpiryDate DAY))) and pcStatus=1 and pcActiveStatus='激活' and m_siteID="+siteID,new Object[]{uid});
        }
      for(Document doc : mcardDocs) {
        String[] papers = doc.get("pcPaperName").toString().split(",");
        List<String> newPaperList = new ArrayList<>(Arrays.asList(papers));
        newPaperList.removeAll(paidPapers); 
        paidPapers.addAll(newPaperList);
      }
      //所有会员订单的套餐信息
      List<String> mmealIds = new ArrayList<String>();
      Document[] morderDocs = docManager.find(orderDocLib.getDocLibID(), "SYS_DELETEFLAG=0 and mobile=? and payStatus=1 and m_siteID="+siteID,new Object[]{memberDoc.get("mMobile")});
      for (Document morder : morderDocs) {
        String[] ids = morder.get("setMealID").toString().split(",");
        List<String> newMealIds = new ArrayList<>(Arrays.asList(ids));
        newMealIds.removeAll(mmealIds);
        mmealIds.addAll(newMealIds);
      }
      for (String id : mmealIds) {
    	  Document[] mmealDocs = null;
    	  if (dbType.equals(DBType.ORACLE)) {
    		  mmealDocs = docManager.find(mealDocLib.getDocLibID(), "SYS_DELETEFLAG=0 and SYS_DOCUMENTID = ? and ((TO_CHAR(sysdate,'YYYY-MM-DD') <= ExpireTime and TO_CHAR(sysdate,'YYYY-MM-DD') >= EffectTime) or (TO_CHAR(sysdate,'YYYY-MM-DD') <= TO_CHAR(CreationTime+SUBSTR(expiryDate,0,length(expiryDate)-1),'yyyy-mm-dd HH24:MI:SS'))) and m_siteID="+siteID,new Object[]{id});
            }else{
            	mmealDocs = docManager.find(mealDocLib.getDocLibID(), "SYS_DELETEFLAG=0 and SYS_DOCUMENTID = ? and ((date_format(now(),'%y-%m-%d') <= ExpireTime and date_format(now(),'%y-%m-%d') >= EffectTime) or (date_format(now(),'%y-%m-%d') <= date_add(CreationTime, INTERVAL expiryDate DAY))) and m_siteID="+siteID,new Object[]{id});
            }
        if ( mmealDocs !=null && mmealDocs.length > 0) {
          Document mmealDoc = mmealDocs[0];
          String[] papers = mmealDoc.get("setMealContent").toString().split(",");
          List<String> newPaperList = new ArrayList<>(Arrays.asList(papers));
          newPaperList.removeAll(paidPapers);
          paidPapers.addAll(newPaperList);
        }
      }
      unpaidPapers = (List)((ArrayList)chargeablePapers).clone();
      unpaidPapers.removeAll(paidPapers);
      //设置结果集合
      List<Object> paidResultList = new ArrayList<Object>();
      List<Object> unpaidResultList = new ArrayList<Object>();
      List<Object> freeResultList = new ArrayList<Object>();
      if (paidPapers.size() > 0) {
        for (String paperName : paidPapers) {
          Map<String, Object> paidResult = new LinkedHashMap<String, Object>();
          Document paidPaper = findDoc(paperDocs,"pa_name","string",paperName);
          if (paidPaper != null) {
            paidResult.put("paperName", paidPaper.getString("pa_name"));
            //获得刊期
            //String pdSqlp = "SELECT pd_date FROM xy_paperdate pd, xy_paper p WHERE p.SYS_DOCUMENTID = pd.pd_paperID AND p.pa_name = '"+paidPaper.getString("pa_name")+"' order by pd_date desc limit 365 ;";
            //paidResult.put("available_pd", getPd(pdSqlp));
            paidResult.put("available_pd", getPdate(paidPaper.getString("pa_name"),"",siteID));
            paidResultList.add(paidResult);
          }
        }
      }
      result.put("paid", paidResultList);
      if (unpaidPapers.size() > 0) {
        for (String paperName : unpaidPapers) {
          Map<String, Object> unpaidResult = new LinkedHashMap<String, Object>();
          //Document unpaidPermission = findDoc(permissionDocs,"paper_name","string",paperName);
          Document unpaidPaper = findDoc(paperDocs,"pa_name","string",paperName);
          if (unpaidPaper != null) {
            unpaidResult.put("paperName", unpaidPaper.getString("pa_name"));
            unpaidResult.put("paperStatus", "unpaid");
            unpaidResult.put("freeDays", unpaidPaper.getInt("pa_chargeGracedays"));
            //获得刊期
            //String pdSqlUnp = "SELECT pd_date FROM xy_paperdate pd, xy_paper p WHERE p.SYS_DOCUMENTID = pd.pd_paperID AND p.pa_name = '"+unpaidPaper.getString("pa_name")+"'  AND pd_date > DATE_SUB(CURDATE(),INTERVAL "+unpaidPaper.getInt("pa_chargeGracedays")+" DAY) order by pd_date desc limit 365 ;";
            //unpaidResult.put("available_pd", getPd(pdSqlUnp));
            String pdSqlUnp = " AND pd_date > DATE_SUB(CURDATE(),INTERVAL "+unpaidPaper.getInt("pa_chargeGracedays")+" DAY) ";
            unpaidResult.put("available_pd", getPdate(unpaidPaper.getString("pa_name"),pdSqlUnp,siteID));
            unpaidResultList.add(unpaidResult);
          }
        }
      }
      result.put("unpaid", unpaidResultList);
      if (freePapers.size() > 0) {
        for (String paperName : freePapers) {
          Map<String, Object> freeResult = new LinkedHashMap<String, Object>();
          Document freePaper = findDoc(paperDocs,"pa_name","string",paperName);
          if (freePaper != null) {
            freeResult.put("paperName", freePaper.getString("pa_name"));
            //获得刊期
            //String pdSqlf = "SELECT pd_date FROM xy_paperdate pd, xy_paper p WHERE p.SYS_DOCUMENTID = pd.pd_paperID AND p.pa_name = '"+freePaper.getString("pa_name")+"' order by pd_date desc limit 365 ;";
            //freeResult.put("available_pd", getPd(pdSqlf));
            freeResult.put("available_pd", getPdate(freePaper.getString("pa_name"),"",siteID));
            freeResultList.add(freeResult);
          }
        }
      }
      result.put("free", freeResultList);
    } else {
      //not member
      result.put("isMember", false);
      //设置结果集合
      List<Object> chargeableResultList = new ArrayList<Object>();
      List<Object> freeResultList = new ArrayList<Object>();
      //freePapers
      if (freePapers.size() > 0) {
        for (String paperName : freePapers) {
          Map<String, Object> freeResult = new LinkedHashMap<String, Object>();
          Document freePaper = findDoc(paperDocs,"pa_name","string",paperName);
          //Document freePermission = findDoc(permissionDocs,"paper_name","string",paperName);
          if (freePaper != null) {
            freeResult.put("paperName", freePaper.getString("pa_name"));
            if (freePaper.getInt("pa_freeGracedays") != 0) {
              //非会员不免费
              freeResult.put("isNonmemberFree", false);
              freeResult.put("freeDays", freePaper.getInt("pa_freeGracedays"));
              //获得刊期
              //String pdSqlf = "SELECT pd_date FROM xy_paperdate pd, xy_paper p WHERE p.SYS_DOCUMENTID = pd.pd_paperID AND p.pa_name = '"+freePaper.getString("pa_name")+"'  AND pd_date > DATE_SUB(CURDATE(),INTERVAL "+freePaper.getInt("pa_freeGracedays")+" DAY) order by pd_date desc limit 365 ;";
              //freeResult.put("available_pd", getPd(pdSqlf));
              String pdSqlf = " AND pd_date > DATE_SUB(CURDATE(),INTERVAL "+freePaper.getInt("pa_freeGracedays")+" DAY) ";
              freeResult.put("available_pd", getPdate(freePaper.getString("pa_name"),pdSqlf,siteID));
            } else {
              //非会员免费
              freeResult.put("isNonmemberFree", true);
              //获得刊期
              //String pdSqlf = "SELECT pd_date FROM xy_paperdate pd, xy_paper p WHERE p.SYS_DOCUMENTID = pd.pd_paperID AND p.pa_name = '"+freePaper.getString("pa_name")+"' order by pd_date desc limit 365 ;";
              //freeResult.put("available_pd", getPd(pdSqlf));
              freeResult.put("available_pd", getPdate(freePaper.getString("pa_name"),"",siteID));
            }
            freeResultList.add(freeResult);
          }
        }
      }
      result.put("free", freeResultList);
      //chargeablePapers
      if (chargeablePapers.size() > 0) {
        for (String paperName : chargeablePapers) {
          Map<String, Object> chargeableResult = new LinkedHashMap<String, Object>();
          Document chargeablePaper = findDoc(paperDocs,"pa_name","string",paperName);
          //Document chargeablePermission = findDoc(permissionDocs,"paper_name","string",paperName);
          if (chargeablePaper != null) {
            chargeableResult.put("paperName", chargeablePaper.getString("pa_name"));
            chargeableResult.put("freeDays", chargeablePaper.getInt("pa_chargeGracedays"));
            //获得刊期
            //String pdSqlc = "SELECT pd_date FROM xy_paperdate pd, xy_paper p WHERE p.SYS_DOCUMENTID = pd.pd_paperID AND p.pa_name = '"+chargeablePaper.getString("pa_name")+"'  AND pd_date > DATE_SUB(CURDATE(),INTERVAL "+chargeablePaper.getInt("pa_chargeGracedays")+" DAY) order by pd_date desc limit 365 ;"; 
            //chargeableResult.put("available_pd", getPd(pdSqlc));
            String pdSqlc = " AND pd_date > DATE_SUB(CURDATE(),INTERVAL "+chargeablePaper.getInt("pa_chargeGracedays")+" DAY) ";
            chargeableResult.put("available_pd", getPdate(chargeablePaper.getString("pa_name"),pdSqlc,siteID));
            chargeableResultList.add(chargeableResult);
          }
        }
      }
      result.put("chargeable", chargeableResultList);
    }
    return result;
  }
  
  private Document findDoc(Document[] docs, String relationProperty, String relationPropertyType, String key) {
    for (Document doc : docs) {
      if (relationPropertyType.equalsIgnoreCase("string")) {
        if (doc.getString(relationProperty).equalsIgnoreCase(key)) {
          return doc;
        }
      }      
    }
    return null;
  }

  private List getPd(String sql) throws E5Exception {
    DBSession dbSession = null;
    IResultSet rs = null;
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    List<String> list = new ArrayList<String>();
    try {
      dbSession = Context.getDBSession();
      rs = dbSession.executeQuery(sql);
      while(rs.next()){
        Date pd = rs.getDate(1);
        list.add(df.format(pd));
      }
    } catch (E5Exception e) {
      e.printStackTrace();
      throw new E5Exception(e.getMessage(),e.getCause());
    } catch (SQLException e) {
      e.printStackTrace();
      throw new E5Exception(e.getMessage(),e.getCause());
    } finally {
      ResourceMgr.closeQuietly(rs);
      ResourceMgr.closeQuietly(dbSession);
    }
    return list;
  }
  
  private List getPdate(String paperName,String sql,int siteID) throws E5Exception {
	  
	DocLib docLibPAPER = LibHelper.getLib(DocTypes.PAPER.typeID(),"xy");
	DocLib docLibPAPERDATE = LibHelper.getLib(DocTypes.PAPERDATE.typeID(),"xy");
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    List<String> list = new ArrayList<String>();
    String dbType = DomHelper.getDBType();
	DocumentManager docManager = DocumentManagerFactory.getInstance();
	Document[] docsPAPER = docManager.find(docLibPAPER.getDocLibID()," SYS_DELETEFLAG=0 and pa_name = '"+paperName+"' and pa_siteID="+siteID,new Object[] {});
	if(docsPAPER != null && docsPAPER.length > 0){
		
		Document[] docsPAPERDATE = null;
	    if (dbType.equals(DBType.ORACLE)) {
	    	docsPAPERDATE = docManager.find(docLibPAPERDATE.getDocLibID()," rownum <= 365 and pd_paperID = "+docsPAPER[0].getString("SYS_DOCUMENTID")+" AND SYS_DELETEFLAG=0 "+sql+" order by pd_date desc ",new Object[] {});
	      }else{
	    	  docsPAPERDATE = docManager.find(docLibPAPERDATE.getDocLibID()," pd_paperID = "+docsPAPER[0].getString("SYS_DOCUMENTID")+" AND SYS_DELETEFLAG=0 "+sql+" order by pd_date desc limit 365 ",new Object[] {});
	      }
		if(docsPAPERDATE != null && docsPAPERDATE.length > 0){
			for(int i=0;i<docsPAPERDATE.length;i++){
				Date pd = docsPAPERDATE[i].getDate("pd_date");
		        list.add(df.format(pd));
			}	
			//return list;
		}
	}
	return list;
  }
  /**
   * 数字报阅读权限简单版，只有免费报纸和付费报纸
   * @param uid
   * @param tenantcode
   * @param paperDocLib
   * @param cardDocLib
   * @param mealDocLib
   * @param mDocLib
   * @param orderDocLib
   * @param permissionDocLib
   * @return
   * @throws E5Exception
   */
  public Map<String, Object> getUserPermission1(String uid,String tenantcode,DocLib paperDocLib,DocLib cardDocLib,DocLib mealDocLib,DocLib mDocLib,DocLib orderDocLib,DocLib permissionDocLib,int siteID) throws E5Exception {
	    List<String> allPapers = null;
	    List<String> chargeablePapers = null;
	    List<String> paidPapers = null;
	    List<String> freePapers = null;
	    DBSession dbSession = null;
	    IResultSet rs = null;
	    //设置结果集合
	    List<Object> paidResultList = new ArrayList<Object>();
	    List<Object> freeResultList = new ArrayList<Object>();
	    String dbType = DomHelper.getDBType();
	    DocumentManager docManager = DocumentManagerFactory.getInstance();
	    //所有报纸信息
	    Document[] paperDocs = docManager.find(paperDocLib.getDocLibID(), "SYS_DELETEFLAG=0 and pa_status=0 and pa_siteID="+siteID,null);
	    allPapers = new ArrayList<String>();
	    for (Document doc : paperDocs) {
	      allPapers.add(doc.get("pa_name").toString());
	    }
	    //所有收费报纸
	    chargeablePapers = new ArrayList<String>();
	    //所有报卡信息
	    Document[] cardDocs = null;
	    if (dbType.equals(DBType.ORACLE)) {
	    	cardDocs = docManager.find(cardDocLib.getDocLibID(), "SYS_DELETEFLAG = 0 and pcStatus=1 and m_siteID="+siteID,null);
	      }else{
	    	  cardDocs = docManager.find(cardDocLib.getDocLibID(), "SYS_DELETEFLAG = 0 and pcStatus=1 and m_siteID="+siteID,null);
	      }
	    for(Document doc : cardDocs) {
	      String[] papers = doc.get("pcPaperName").toString().split(",");
	      List<String> newPaperList = new ArrayList<>(Arrays.asList(papers));
	      newPaperList.removeAll(chargeablePapers); 
	      chargeablePapers.addAll(newPaperList);
	    }
	    //所有套餐信息
	    Document[] mealDocs = null;
	    if (dbType.equals(DBType.ORACLE)) {
	    	mealDocs = docManager.find(mealDocLib.getDocLibID(), "SYS_DELETEFLAG=0 and m_siteID="+siteID,null);
	      }else{
	    	  mealDocs = docManager.find(mealDocLib.getDocLibID(), "SYS_DELETEFLAG=0 and m_siteID="+siteID,null);
	      }
	    for(Document doc : mealDocs) {
	      String[] papers = doc.get("setMealContent").toString().split(",");
	      List<String> newPaperList = new ArrayList<>(Arrays.asList(papers));
	      newPaperList.removeAll(chargeablePapers);
	      chargeablePapers.addAll(newPaperList);
	    }
	    freePapers = (List)((ArrayList)allPapers).clone();
	    freePapers.removeAll(chargeablePapers);
	    //结果集合
	    Map<String, Object> result = new LinkedHashMap<String, Object>();
	    //所有会员信息
	    Document[] memberDocs = null;
	    if (uid != null) {
	      memberDocs = docManager.find(mDocLib.getDocLibID(), "SYS_DELETEFLAG=0 and SYS_DOCUMENTID=? and m_siteID=? ", new Object[]{uid,siteID});
	    }
	    if (uid != null && memberDocs != null && memberDocs.length > 0) {
	      // is member
	      Document memberDoc = memberDocs[0];
	      result.put("isMember", true);
	      //已付费报纸
	      paidPapers = new ArrayList<String>();
	      //所有会员报卡信息
	      Document[] mcardDocs = null;
	      if (dbType.equals(DBType.ORACLE)) {
	    	  mcardDocs = docManager.find(cardDocLib.getDocLibID(), "SYS_DELETEFLAG = 0 and pcMember_ID = ? and ((pcExpireTime <> '' AND TO_CHAR(sysdate,'YYYY-MM-DD') <= pcExpireTime and TO_CHAR(sysdate,'YYYY-MM-DD') >= pcEffectTime) or (pcExpiryDate <> '--' AND TO_CHAR(sysdate,'YYYY-MM-DD') <= TO_CHAR(activeTime+pcExpiryDate,'yyyy-mm-dd HH24:MI:SS'))) and pcStatus=1 and pcActiveStatus='激活' and m_siteID=? ",new Object[]{uid,siteID});
	        }else{
	        	mcardDocs = docManager.find(cardDocLib.getDocLibID(), "SYS_DELETEFLAG = 0 and pcMember_ID = ? and ((pcExpireTime <> '' AND date_format(now(),'%y-%m-%d') <= pcExpireTime and date_format(now(),'%y-%m-%d') >= pcEffectTime) or (pcExpiryDate <> '--' AND date_format(now(),'%y-%m-%d') <= date_add(activeTime, INTERVAL pcExpiryDate DAY))) and pcStatus=1 and pcActiveStatus='激活' and m_siteID=? ",new Object[]{uid,siteID});
	        }
	      for(Document doc : mcardDocs) {
	        String[] papers = doc.get("pcPaperName").toString().split(",");
	        List<String> newPaperList = new ArrayList<>(Arrays.asList(papers));
	        newPaperList.removeAll(paidPapers); 
	        paidPapers.addAll(newPaperList);
	      }
	      //所有会员订单的套餐信息
	      List<String> mmealIds = new ArrayList<String>();
	      Map<String, Object> idsAndTimes = new LinkedHashMap<String, Object>();//key=mealid;value=time
	      if (memberDoc.get("mMobile")!=null) {
	    	  Document[] morderDocs = docManager.find(orderDocLib.getDocLibID(), "SYS_DELETEFLAG=0 and mobile=? and payStatus=1 and m_siteID=?",new Object[]{memberDoc.get("mMobile"),siteID});
		      for (Document morder : morderDocs) {
		        String[] ids = morder.get("setMealID").toString().split(",");
		        String time = morder.getString("payTime");
		        List<String> newMealIds = new ArrayList<>(Arrays.asList(ids));
		        //newMealIds.removeAll(mmealIds);
		        //mmealIds.addAll(newMealIds);
		        for (String id : newMealIds) {
		        	idsAndTimes.put(id, time.substring(0, 19));
		        }
		      }
		      for (Map.Entry<String, Object> idAndTime :idsAndTimes.entrySet()) {
		    	  Document[] mmealDocs = null;
		    	  if (dbType.equals(DBType.ORACLE)) {
		    		  mmealDocs = docManager.find(mealDocLib.getDocLibID(), "SYS_DELETEFLAG=0 and SYS_DOCUMENTID = ? and ((ExpireTime <> '' AND TO_CHAR(sysdate,'YYYY-MM-DD') <= ExpireTime and TO_CHAR(sysdate,'YYYY-MM-DD') >= EffectTime) or (expiryDate <> '---' AND TO_CHAR(sysdate,'YYYY-MM-DD hh24:mi:ss') <= TO_CHAR(to_date('"+idAndTime.getValue()+"','yyyy-mm-dd HH24:MI:SS') + replace(expiryDate, '天', ''),'YYYY-MM-DD hh24:mi:ss'))) and m_siteID=?",new Object[]{idAndTime.getKey(),siteID});
		            }else{
		            	mmealDocs = docManager.find(mealDocLib.getDocLibID(), "SYS_DELETEFLAG=0 and SYS_DOCUMENTID = ? and ((ExpireTime <> '' AND date_format(now(),'%Y-%m-%d') <= ExpireTime and date_format(now(),'%Y-%m-%d') >= EffectTime) or (expiryDate <> '---' AND date_format(now(),'%Y-%m-%d %T') <= date_add('"+idAndTime.getValue()+"', INTERVAL expiryDate DAY))) and m_siteID=?",new Object[]{idAndTime.getKey(),siteID});
		            }
		        if ( mmealDocs !=null && mmealDocs.length > 0) {
		          Document mmealDoc = mmealDocs[0];
		          String[] papers = mmealDoc.get("setMealContent").toString().split(",");
		          List<String> newPaperList = new ArrayList<>(Arrays.asList(papers));
		          newPaperList.removeAll(paidPapers);
		          paidPapers.addAll(newPaperList);
		        }
		        
		      }
	      }
	      
	      if (paidPapers.size() > 0) {
	        for (String paperName : paidPapers) {
	          Map<String, Object> paidResult = new LinkedHashMap<String, Object>();
	          Document paidPaper = findDoc(paperDocs,"pa_name","string",paperName);
	          if (paidPaper != null) {
	            paidResult.put("paperName", paidPaper.getString("pa_name"));
	            //获得刊期
	            //String pdSqlp = "SELECT pd_date FROM xy_paperdate pd, xy_paper p WHERE p.SYS_DOCUMENTID = pd.pd_paperID AND p.pa_name = '"+paidPaper.getString("pa_name")+"' order by pd_date desc limit 365 ;";
	            //paidResult.put("available_pd", getPd(pdSqlp));
	            paidResult.put("available_pd", getPdate(paidPaper.getString("pa_name"),"",siteID));
	            paidResultList.add(paidResult);
	          }
	        }
	      }
	      result.put("chargeable", paidResultList);
	      
	    }else{
	    	  result.put("isMember", false);
	    } 
	    
	    if (freePapers.size() > 0) {
	        for (String paperName : freePapers) {
	          Map<String, Object> freeResult = new LinkedHashMap<String, Object>();
	          Document freePaper = findDoc(paperDocs,"pa_name","string",paperName);
	          if (freePaper != null) {
	            freeResult.put("paperName", freePaper.getString("pa_name"));
	            //获得刊期
	            //String pdSqlf = "SELECT pd_date FROM xy_paperdate pd, xy_paper p WHERE p.SYS_DOCUMENTID = pd.pd_paperID AND p.pa_name = '"+freePaper.getString("pa_name")+"' order by pd_date desc limit 365 ;";
	            //freeResult.put("available_pd", getPd(pdSqlf));
	            freeResult.put("available_pd", getPdate(freePaper.getString("pa_name"),"",siteID));
	            freeResultList.add(freeResult);
	          }
	        }
	      }
	    result.put("free", freeResultList);
	    return result;
	  }
}
