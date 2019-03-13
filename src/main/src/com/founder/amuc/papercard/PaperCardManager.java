package com.founder.amuc.papercard;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.founder.amuc.commons.Constant;
import com.founder.amuc.commons.InfoHelper;
import com.founder.amuc.msg.SendMsg;
import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;

public class PaperCardManager {
  
  /**
   * 报卡激活（二期）
   * @param uid
   * @param pcno
   * @return
   * @throws E5Exception 
   */
  public String activate2(String uid, String ssoid, String pcno, String password, String tenantcode) throws E5Exception {
    JSONObject obj = new JSONObject();
    if(StringUtils.isBlank(uid) && StringUtils.isBlank(ssoid)){
      obj.put("code", "1000");
      obj.put("msg", "参数错误");
      return obj.toJSONString();
    }
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    DocumentManager docManager = DocumentManagerFactory.getInstance();
    DocLib pcdocLib = InfoHelper.getLib(Constant.DOCTYPE_PAPERCARD,tenantcode);
    DocLib pcdocLibLog = InfoHelper.getLib(Constant.DOCTYPE_PAPERCARDLOG,tenantcode);
    Document mDoc = null;
    if(!StringUtils.isBlank(uid)){
      mDoc = getMemberByid(uid, tenantcode);
    }else if(!StringUtils.isBlank(ssoid)){
      mDoc = getMemberByssoid(ssoid, tenantcode);
    }
    
    if( mDoc == null){
      obj.put("code", "1001");
      obj.put("msg", "系统内不存在该用户");
      return obj.toJSONString();
    }
    if( mDoc != null && mDoc.getInt("mStatus") == 0 ){
      obj.put("code", "1002");
      obj.put("msg", "该用户被禁用");
      return obj.toJSONString();
    }else{
      String pccondition = "SYS_DELETEFLAG = 0 and pcNo = ? and pcPassword = ?";
      Document[] pcards = docManager.find(pcdocLib.getDocLibID(),pccondition, new Object[] { pcno, password });
      String sql = "SYS_DELETEFLAG = 0 and pcNo = ? and pcPassword = ? and date_format(now(),'%y-%m-%d') <= pcExpireTime and date_format(now(),'%y-%m-%d') >= pcEffectTime ";
      Document[] pcardsql = docManager.find(pcdocLib.getDocLibID(),sql, new Object[] { pcno, password });
      if(pcards != null && pcards.length > 0){
        Document doc = pcards[0];
        if(doc.getInt("pcMember_ID") != 0 && !(mDoc.getDocID()+"").equalsIgnoreCase(doc.getInt("pcMember_ID")+"")){
          //"报卡已绑定其它用户"
          obj.put("code", "1003");
          obj.put("msg", "报卡已绑定其他用户");
        }else if(pcardsql == null && pcardsql.length <= 0){
          obj.put("code", "1003");
          obj.put("msg", "报卡已过期");
        }else{
          if(doc.getString("pcActiveStatus").equals("未激活")){
            if(doc.getString("pcStatus").equals("有效")){
              if(password.equals(doc.getString("pcPassword"))){
                Document docLog = docManager.newDocument(pcdocLibLog.getDocLibID());
                docLog.set("status", "已激活");
                docLog.set("operation", "激活");
                docLog.set("SYS_CREATED", df.format(new Date()));
                docLog.setFolderID(pcdocLibLog.getFolderID());
                docLog.set("cardPrefix", pcno.substring(0,13));
                docLog.set("operationNum", "1");
                docLog.set("pcTotalMoney", doc.get("pcMoney"));  //报卡总额
                docLog.set("effectTime", doc.get("pcEffectTime"));
                docLog.set("expireTime", doc.get("pcExpireTime"));
                docLog.set("paperName", doc.get("pcPaperName"));
                docLog.set("detail", "成功激活1张报卡");
                docLog.set("operator", "后台："+doc.get("pcOperator"));
                docLog.set("operatorid", doc.get("pcOperatorID"));
                docManager.save(docLog);  //保存到pccardlog
                
                doc.set("pcActiveStatus", "激活");
                //doc.set("pcActiveTime", df.format(new Date()));
                doc.set("pcMember_ID", mDoc.getDocID());
                doc.set("pcMember", mDoc.get("mName"));
                doc.set("pcMobile", mDoc.get("mMobile"));
                System.out.println(mDoc.get("mMobile"));
                docManager.save(doc);   //更新pccard
                obj.put("code", "1004");
                obj.put("msg", "报卡激活成功");
                
                  try {
                    sendMessage(docLog.get("paperName").toString(),doc.get("pcMember").toString(),docLog.get("operator").toString(),doc.get("pcMobile").toString());
                  } catch (Exception e) {
                  e.printStackTrace();
                  throw new E5Exception("send message error");
                  }
              }else{
                obj.put("code", "1006");
                obj.put("msg", "报卡密码有误");
              }
              
            }else{
              obj.put("code", "1007");
              obj.put("msg", "报卡无效");
            }
          }else{
            obj.put("code", "1005");
            obj.put("msg", "报卡已激活");
          }
        }
      }else{
        obj.put("code", "1006");
        obj.put("msg", "报卡号或密码不正确");
      }
    }
    return obj.toJSONString();
  }
  /**
   * 获取当前用户持有的报卡
   * @param uid
   * @param tenantcode
   * @throws E5Exception
   */
  public String getCurPCard(String uid, String tenantcode) throws E5Exception {

    DocumentManager docManager = DocumentManagerFactory.getInstance();
    DocLib pcdocLib = InfoHelper.getLib(Constant.DOCTYPE_PAPERCARD,tenantcode);
    JSONObject obj = new JSONObject();
    Document mDoc = getMemberByid(uid, tenantcode);
    if( mDoc == null){
      obj.put("code", "1001");
      obj.put("msg", "不存在该账户");
      return obj.toJSONString();
    }
    if( mDoc != null && mDoc.getInt("mStatus") == 0 ){
      obj.put("code", "1002");
      obj.put("msg", "该用户被禁用");
      return obj.toJSONString();
    }
    if (mDoc != null && mDoc.getInt("mStatus") == 1) {
      // 根据uid查询报卡表
      String pccondition = "SYS_DELETEFLAG = 0 and pcMember_ID = ? ";
      Document[] pcards = docManager.find(pcdocLib.getDocLibID(),pccondition, new Object[] { uid });
      if (pcards != null && pcards.length > 0) {
        JSONArray pcarry = new JSONArray();
        for (Document pcard : pcards) {
          JSONObject pcobj = new JSONObject();
          pcobj.put("no", pcard.getString("pcNo"));
          pcobj.put("active",("激活").equals(pcard.getString("pcActiveStatus")) ? 0 : 1 );
          pcarry.add(pcobj);
        }
        obj.put("code", "1003");
        obj.put("msg", "获取报卡成功");
        obj.put("num", pcards.length);
        obj.put("card", pcarry);
      } else {
        obj.put("code", "1004");
        obj.put("msg", "该用户未绑定报卡");
      }
    }
    return obj.toJSONString();
  }
  
  /**
   * 报卡激活
   * @param uid
   * @param pcno
   * @return
   * @throws E5Exception 
   */
  public String activate(String uid, String pcno, String tenantcode) throws E5Exception {
    
    DocumentManager docManager = DocumentManagerFactory.getInstance();
    DocLib pcdocLib = InfoHelper.getLib(Constant.DOCTYPE_PAPERCARD,tenantcode);
    JSONObject obj = new JSONObject();
    Document mDoc = getMemberByid(uid, tenantcode);
    if( mDoc == null){
      obj.put("code", "1001");
      obj.put("msg", "系统内不存在该用户");
      return obj.toJSONString();
    }
    if( mDoc != null && mDoc.getInt("mStatus") == 0 ){
      obj.put("code", "1002");
      obj.put("msg", "该用户被禁用");
      return obj.toJSONString();
    }
    if(mDoc != null && mDoc.getInt("mStatus") == 1){
      String pccondition = "SYS_DELETEFLAG = 0 and pcNo = ? ";
      Document[] pcards = docManager.find(pcdocLib.getDocLibID(),pccondition, new Object[] { pcno });
      if(pcards != null && pcards.length > 0){
        Document doc = pcards[0];
        if(doc.getInt("pcMember_ID") !=  Integer.parseInt(uid)){
          //"报卡已绑定其它用户"
          obj.put("code", "1003");
          obj.put("msg", "报卡已绑定其它用户");
        }else{
          if(doc.getString("pcActiveStatus").equals("未激活")){
            doc.set("pcActiveStatus", "激活");
            docManager.save(doc);
            obj.put("code", "1004");
            obj.put("msg", "报卡激活成功");
          }else{
            obj.put("code", "1005");
            obj.put("msg", "报卡已激活");
          }
        }
      }else{
        obj.put("code", "1006");
        obj.put("msg", "报卡不存在");
      }
    }
    return obj.toJSONString();
  }
  /**
   * 
   * 获取当前用户持有的报卡(二期)
   * @param uid
   * @param tenantcode
   * @throws E5Exception
   */
  public String getCurPCard2(String uid,String ssoid, String tenantcode) throws E5Exception {

    DocumentManager docManager = DocumentManagerFactory.getInstance();
    DocLib pcdocLib = InfoHelper.getLib(Constant.DOCTYPE_PAPERCARD,tenantcode);
    JSONObject obj = new JSONObject();
    Document mDoc = null;
    if(!StringUtils.isBlank(uid)){
      mDoc = getMemberByid(uid, tenantcode);
    }else if(!StringUtils.isBlank(ssoid)){
      mDoc = getMemberByssoid(ssoid, tenantcode);
    }else{
      obj.put("code", "1000");
      obj.put("msg", "参数错误");
      return obj.toJSONString();
    }
    if( mDoc == null){
      obj.put("code", "1001");
      obj.put("msg", "不存在该账户");
      return obj.toJSONString();
    }
    if( mDoc != null && mDoc.getInt("mStatus") == 0 ){
      obj.put("code", "1002");
      obj.put("msg", "该用户被禁用");
      return obj.toJSONString();
    }
    if (mDoc != null && mDoc.getInt("mStatus") == 1) {
      uid = mDoc.getDocID()+"";
      //获取当前时间
    //  Timestamp curTime = DateUtils.getTimestamp();
      // 根据uid查询报卡表
      String pccondition = "SYS_DELETEFLAG = 0 and pcMember_ID = ? ";
      Document[] pcards = docManager.find(pcdocLib.getDocLibID(),pccondition, new Object[] { uid });
/*      String pccondition = "SYS_DELETEFLAG = 0 and pcMember_ID = ? and pcExpireTime >= ?";
      Document[] pcards = docManager.find(pcdocLib.getDocLibID(),pccondition, new Object[] { uid, curTime });
*/      if (pcards != null && pcards.length > 0) {
        JSONArray pcarry = new JSONArray();
        for (Document pcard : pcards) {
          JSONObject pcobj = new JSONObject();
          pcobj.put("no", pcard.getString("pcNo"));
          //pcobj.put("active",("激活").equals(pcard.getString("pcActiveStatus")) ? 0 : 1 );
          pcobj.put("active", pcard.getString("pcActiveStatus"));
          pcobj.put("paperName", pcard.getString("pcPaperName"));
          String effTime = pcard.getString("pcEffectTime");
          String effectTime = "";
          if(effTime!=null && effTime.trim().length()>0){
            effectTime = pcard.getString("pcEffectTime").substring(0, 10);
          }
          String expireTime = pcard.getString("pcExpireTime").substring(0, 10);
          pcobj.put("effectTime", effectTime);
          pcobj.put("expireTime", expireTime);
          pcarry.add(pcobj);
        }
        obj.put("code", "1003");
        obj.put("msg", "获取报卡成功");
        obj.put("num", pcards.length);
        obj.put("card", pcarry);
      } else {
        obj.put("code", "1004");
        obj.put("msg", "该用户未绑定报卡");
      }
    }
    return obj.toJSONString();
  }

  /**
   * 根据uid查询会员
   * 返回：会员members对象
   * @param uid
   * @param tenantcode
   * @return
   * @throws E5Exception
   */
  private Document getMemberByid(String uid, String tenantcode) throws E5Exception {
    DocumentManager docManager = DocumentManagerFactory.getInstance();
    DocLib mdocLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER, tenantcode);
    // 查询系统中是否存在uid的会员
    String mcondition = "SYS_DOCUMENTID = ? and SYS_DELETEFLAG = 0";
    String[] column = { "mName", "mMobile", "mStatus" };
    Document[] members = docManager.find(mdocLib.getDocLibID(), mcondition,new Object[] { uid }, column);
    if (members != null && members.length > 0) {
      return members[0];
    }else{
      return null;
    }
  }
  
  /**
   * 根据ssoid查询会员
   * 返回：会员members对象
   * @param uid
   * @param tenantcode
   * @return
   * @throws E5Exception
   */
  private Document getMemberByssoid(String ssoid, String tenantcode) throws E5Exception {
    DocumentManager docManager = DocumentManagerFactory.getInstance();
    DocLib mdocLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER, tenantcode);
    // 查询系统中是否存在uid的会员
    String mcondition = "uid_sso = ? and SYS_DELETEFLAG = 0";
    String[] column = { "mName", "mMobile", "mStatus" };
    Document[] members = docManager.find(mdocLib.getDocLibID(), mcondition,new Object[] { ssoid }, column);
    if (members != null && members.length > 0) {
      return members[0];
    }else{
      return null;
    }
  }
  /**
   * 报卡日志接口
   * 
   * @param docid
   * @param tenantcode
   * @return
   * @throws E5Exception
   */
  public String paperCardLog(String docid, String tenantcode) throws E5Exception {
    DocumentManager docManager = DocumentManagerFactory.getInstance();
    DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_PAPERCARD, tenantcode);
    // 查询系统中是否存在uid的会员
    JSONObject obj = new JSONObject();
    String condition = "SYS_DOCUMENTID = ? and SYS_DELETEFLAG = 0";
    String[] column = { "pcNo", "pcActiveStatus", "SYS_CREATED", "pcOperator","pcMember","pcActiveTime" };
    Document[] members = docManager.find(docLib.getDocLibID(), condition,new Object[] { docid }, column);
    if (members != null && members.length > 0) {

      obj.put("pcNo", members[0].getString("pcNo"));
      obj.put("pcActiveStatus", members[0].getString("pcActiveStatus"));
      obj.put("SYS_CREATED", members[0].getString("SYS_CREATED"));
      obj.put("pcOperator", members[0].getString("pcOperator"));
      obj.put("pcMember", members[0].getString("pcMember"));
      obj.put("pcActiveTime", members[0].getString("pcActiveTime"));
      return obj.toString();
    }else{
      return null;
    }
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
  
  /**
   * 发送短信(规则1)
   * @param request
   * @param response
   * @param model
   * @throws Exception 
   */
  private void sendMessage(String paperName,String pcMember,String operator,String pcMobile) throws Exception{
    
    //保存短信主体信息：短信内容、时间、发送人
    saveMsg(paperName,pcMember,operator);
    //保存记录到短信日志表
    saveMsgLog(paperName,pcMember,operator,pcMobile);
    //发送短信  
    String messageCon = "(" + pcMember + " 您好，" + paperName + " 报卡激活成功！)";
    int status = SendMsg.sendMsg(pcMobile, messageCon);
    
    //保存记录到短信记录表-日志
    saveMsgLog(paperName,pcMember,operator,pcMobile,status);
  
  }
  
  /**
   * 1.保存短信相关信息到短信表中：短信内容，时间、发送人
   * @param request
   * @return
   * @throws E5Exception
   */
  private Document saveMsg(String paperName,String pcMember,String operator) throws E5Exception {
        
    String tenantCode = "uc";//租户代号
    DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_MSG, tenantCode);//短信表
    
    DocumentManager docManager = DocumentManagerFactory.getInstance();
    Document doc = docManager.newDocument(docLib.getDocLibID(), InfoHelper.getID(docLib.getDocTypeID()));
    doc.setFolderID(docLib.getFolderID());
    doc.setDeleteFlag(0);
    doc.setLocked(false);
    doc.set("SYS_CREATED", DateUtils.getTimestamp());
    doc.set("msgCreatUser", operator);
    doc.set("msgContent", pcMember + " 您好，" + paperName + " 报卡激活成功！");
    doc.set("msgSendTime", DateUtils.getTimestamp());
    doc.set("msgSendNum", 1);//保存短信发送数量
    docManager.save(doc);
    return doc;
  }
  
    private long saveMsgLog(String paperName,String pcMember,String operator,String pcMobile,int status) throws E5Exception {
    
    DocLib msglDocLib = InfoHelper.getLib(Constant.DOCTYPE_MSGLOG, "uc");//短信日志
    DocumentManager docManager = DocumentManagerFactory.getInstance();
    Document doc = docManager.newDocument(msglDocLib.getDocLibID(), InfoHelper.getID(msglDocLib.getDocTypeID()));
    doc.setFolderID(msglDocLib.getFolderID());
    doc.setDeleteFlag(0);
    
    doc.setLocked(false); 
    doc.set("SYS_CREATED", DateUtils.getTimestamp());
    doc.set("msglStatus", status);
    doc.set("msglReceiver", pcMember + "(" + pcMobile + ")");
    docManager.save(doc);
    return doc.getDocID();
  }

  private long saveMsgLog(String paperName,String pcMember,String operator,String pcMobile) throws E5Exception {
    return saveMsgLog(paperName,pcMember,operator,pcMobile,2);
  }
}
