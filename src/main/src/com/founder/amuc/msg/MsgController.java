package com.founder.amuc.msg;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.founder.amuc.commons.Constant;
import com.founder.amuc.commons.FormHelper;
import com.founder.amuc.commons.InfoHelper;
import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.e5.dom.DocTypeField;
import com.founder.e5.dom.DocTypeReader;
import com.founder.e5.flow.Operation;
import com.founder.e5.flow.ProcReader;
import com.founder.e5.sys.SysConfigReader;
import com.founder.e5.web.BaseController;
import com.founder.e5.workspace.ProcHelper;
import com.founder.e5.workspace.param.AfterParam;
import com.founder.e5.workspace.param.ProcParam;
import com.founder.e5.workspace.service.AfterService;

/** 
 * @created 2014年10月8日 下午5:29:37 
 * @author  fanjc
 * 类说明 ： 
 */
@SuppressWarnings({"rawtypes", "unchecked", "unused"})
public class MsgController  extends BaseController{
  @Override
  protected void handle(HttpServletRequest request,
      HttpServletResponse response, Map model) throws Exception {
    String action = get(request, "a");
    
    if ("findM".equals(action)) {
      // 模糊匹配：收件人为会员
      findM(request, response, model);
    } else if ("send".equals(action)) {
      // 发送短信
      send(request, response, model);
    } else if ("reSend".equals(action)) {
      // 失败重发短信
      reSend(request, response, model);
    }
  }
  
  private void findM(HttpServletRequest request, HttpServletResponse response, Map model) throws Exception {
    //取得租户代号
    String tenantCode = InfoHelper.getTenantCode(request);
    String name = get(request, "q"); //固定参数名，q
    
    StringBuilder result = new StringBuilder();
    result.append("[");
    int i = 0;
    int maxCount = 20;
    
    DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER, tenantCode);
    String sql = "select SYS_DOCUMENTID,mName,mMobile from " + docLib.getDocLibTable() + " where (mName like ? or mMobile like ?) and SYS_DELETEFLAG=0 ";
    
    DBSession conn = null;
    IResultSet rs = null;
    try {
        conn = Context.getDBSession(docLib.getDsID());
        //按数据库类型获得不同的查询个数限制语句
        sql = conn.getDialect().getLimitString(sql, 0, maxCount);
        
        rs = conn.executeQuery(sql, new Object[]{name + "%", name + "%"});
      while (rs.next()) {
        if (i++ > 0) result.append(",");
        result.append("{key:\"").append(rs.getLong("SYS_DOCUMENTID"))
          .append("\",value:\"").append(rs.getString("mName"))
          .append("(").append(rs.getString("mMobile")).append(")\"}");
      }
    } catch (Exception e) {
      log.error(e);
    } finally {
      ResourceMgr.closeQuietly(rs);
      ResourceMgr.closeQuietly(conn);
    }
    result.append("]");
    
    output(result.toString(), response);
  }
  /**
   * 发送短信，根据规则开关确定具体发送流程
   * @param request
   * @param response
   * @param model
   * @throws Exception
   */
  private void send(HttpServletRequest request, HttpServletResponse response, Map model) throws Exception{
    
    String msgSendRule = getSenRule();
    if("0".equals(msgSendRule)){
      send0(request, response, model);
    }else if ("1".equals(msgSendRule)){
      send1(request, response, model);
    }else if ("2".equals(msgSendRule)){
      send2(request, response, model);
    }
  }
  /**
   * 获取短信发送规则
   * @return
   * @throws E5Exception
   */
  private String getSenRule() throws E5Exception{
    int appID = 1;
    SysConfigReader configReader = (SysConfigReader)Context.getBean(SysConfigReader.class);
    String msgSendRule = configReader.get(appID, "会员中心", "短信发送规则");
    return msgSendRule;
  }
  /**
   * 发送短信(规则0)基于动态链接库发送短信
   * @param request
   * @param response
   * @param model
   * @throws Exception 
   */
  private void send0(HttpServletRequest request, HttpServletResponse response, Map model) throws Exception{
    
    DocumentManager docManager = DocumentManagerFactory.getInstance();
    
    //1.组装所有收件人、活动下所有收件人信息
    Map<Integer, String> memberMap = new HashMap<Integer, String>();
    if(assembleMembers(request)!=null){
      memberMap.putAll(assembleMembers(request));//组装所有收件人信息
    }
    Map<Integer, String> actmap= assembleMembersByA(request);
    if(actmap != null){
      memberMap.putAll(actmap);//组装活动下所有收件人信息
    }
    //2.保存短信主体信息：短信内容、时间、发送人
    Document msgDoc = saveMsg(request,memberMap.size());
    //3.对所有收件人发送短信
    Map<Integer, Integer> msgStatusMap = new HashMap<Integer, Integer>();
    String messageCon = get(request, "msgContent");//短信内容
    sendMSG(memberMap,messageCon,msgStatusMap);
    
    //记录群发短信的状态：全部发送成功，部分发送成功
    if(msgStatusMap.containsValue(0)){//发送状态有失败的
      msgDoc.set("msgSendStatus", 2);
    }else{
      msgDoc.set("msgSendStatus", 1);
    }
    docManager.save(msgDoc);
    
    //4.保存记录到短信记录表-日志
    MsgLog(request,memberMap,msgDoc.getDocID(),msgStatusMap);
    
    //5.刷新页面
    model.put("needRefresh", "true"); //是否需要刷新列表
    String viewName = "redirect:/e5workspace/after.do?DocLibID=" + msgDoc.getDocLibID()
        + "&DocIDs="
       + msgDoc.getDocID()+ "&UUID=" + get(request,"UUID");
    model.put("@VIEWNAME@", viewName);
    
  }
  /**
   * 发送短信(规则1)基于数据库接口发送短信
   * @param request
   * @param response
   * @param model
   * @throws Exception 
   */
  private void send1(HttpServletRequest request, HttpServletResponse response, Map model) throws Exception{
    
    //1.组装所有收件人、活动下所有收件人信息
    Map<Integer, String> memberMap = new HashMap<Integer, String>();
    if(assembleMembers(request)!=null){
      memberMap.putAll(assembleMembers(request));//组装所有收件人信息
    }
    Map<Integer, String> actmap= assembleMembersByA(request);
    if(actmap != null){
      memberMap.putAll(actmap);//组装活动下所有收件人信息
    }
    //2.保存短信主体信息：短信内容、时间、发送人
    Document msgDoc = saveMsg(request,memberMap.size());
    
    //3.保存记录到短信日志表
    Map<Integer, Long> msgLog = MsgLog(request,memberMap,msgDoc.getDocID(),null);
    //4.对所有收件人发送短信
    InsertMSG(memberMap, msgLog, msgDoc, model);//将记录插入到指定表中
    
    /*
    model.put("sendSuccess", "true");
    model.put("result", "群发全部成功!");
    model.put("@VIEWNAME@", "/amuc/msg/MsgResult");
    afterProcess(request, model);
    */
    
    //5.刷新页面
    model.put("needRefresh", "true"); //是否需要刷新列表
    String viewName = "redirect:/e5workspace/after.do?DocLibID=" + msgDoc.getDocLibID()
        + "&DocIDs="+ msgDoc.getDocID()+ "&UUID=" + get(request,"UUID");
    model.put("@VIEWNAME@", viewName);
  
  }
  
  /**
   * 发送短信(规则2)基于云服务发送短信
   * @param request
   * @param response
   * @param model
   * @throws Exception 
   */
  private void send2(HttpServletRequest request, HttpServletResponse response, Map model) throws Exception{
    
    //1.组装所有收件人、活动下所有收件人信息
    Map<Integer, String> memberMap = new HashMap<Integer, String>();
    if(assembleMembers(request)!=null){
      memberMap.putAll(assembleMembers(request));//组装所有收件人信息
    }
    Map<Integer, String> actmap= assembleMembersByA(request);
    if(actmap != null){
      memberMap.putAll(actmap);//组装活动下所有收件人信息
    }
    //2.保存短信主体信息：短信内容、时间、发送人
    Document msgDoc = saveMsg(request,memberMap.size());
    
    //3.保存记录到短信日志表
    Map<Integer, Long> msgLog = MsgLog(request,memberMap,msgDoc.getDocID(),null);
    //4.对所有收件人发送短信    
    Map<Integer, Integer> msgStatusMap = new HashMap<Integer, Integer>();
    String messageCon = get(request, "msgContent");//短信内容
    sendMSG2(memberMap,messageCon,msgStatusMap); 
    
    //5.保存记录到短信记录表-日志
    MsgLog(request,memberMap,msgDoc.getDocID(),msgStatusMap);
    
    //6.刷新页面
    model.put("needRefresh", "true"); //是否需要刷新列表
    String viewName = "redirect:/e5workspace/after.do?DocLibID=" + msgDoc.getDocLibID()
        + "&DocIDs="+ msgDoc.getDocID()+ "&UUID=" + get(request,"UUID");
    model.put("@VIEWNAME@", viewName);
  
  }
  
  /**
   * 1.保存短信相关信息到短信表中：短信内容，时间、发送人
   * @param request
   * @return
   * @throws E5Exception
   */
  private Document saveMsg(HttpServletRequest request,int sendNum) throws E5Exception {
    
    String msgContent = get(request, "msgContent");//短信内容
    
    String tenantCode = InfoHelper.getTenantCode(request);//取得租户代号
    DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_MSG, tenantCode);//短信表
    
    DocumentManager docManager = DocumentManagerFactory.getInstance();
    Document doc = docManager.newDocument(docLib.getDocLibID(), InfoHelper.getID(docLib.getDocTypeID()));
    doc.setFolderID(docLib.getFolderID());
    doc.setDeleteFlag(0);
    doc.setLocked(false);
    assembleDoc(doc, request);//组装
    doc.set("SYS_CREATED", DateUtils.getTimestamp());
    doc.set("msgCreatUser", ProcHelper.getUser(request).getUserName());
    doc.set("msgContent", msgContent);
    doc.set("msgSendTime", DateUtils.getTimestamp());
    doc.set("msgSendNum", sendNum);//保存短信发送数量
    docManager.save(doc);
    return doc;
  }

  private Map<Integer, String> assembleMembers(HttpServletRequest request){
    String[] members = request.getParameterValues("divemMember"+"hiddenVal");
    if(members == null || members.length == 0) return null;
    
    Map<Integer, String> memberMap = new HashMap<Integer, String>();
    for(int i = 0; i < members.length; i++){
      String member = members[i];
      String[] memberArr = member.split(";");
      if(memberArr != null && memberArr.length == 2){//id=;value=
        int id = Integer.valueOf(memberArr[0].replace("id=", ""));
        String value = memberArr[1];
        memberMap.put(id, value.replace("value=", ""));
      }
    }
    return memberMap;
  }
  private Map<Integer, String> assembleMembersByA(HttpServletRequest request) throws E5Exception{
    String[] actions = request.getParameterValues("divemAction"+"hiddenVal");
    
    if(actions == null || actions.length == 0) return null;
    
    Map<Integer, String> memberMap = new HashMap<Integer, String>();
    for(int i = 0; i < actions.length; i++){
      String action = actions[i];
      String[] actionArr = action.split(";");
      if(actionArr != null && actionArr.length == 2){//id=;value=
        int actID = Integer.valueOf(actionArr[0].replace("id=", ""));
        memberMap.putAll(getMemberByAid(request, actID));
      }
    }
    return memberMap;
  }
  /**
   * 根据活动id找到相应的会员
   * @param request
   * @param actID
   * @return
   * @throws E5Exception
   */
  private Map<Integer, String> getMemberByAid(HttpServletRequest request, int actID) throws E5Exception{
    Map<Integer, String> memberMap = new HashMap<Integer, String>();
    DocLib amDocLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBERACTION, InfoHelper.getTenantCode(request));//活动会员
    DocumentManager docManager = DocumentManagerFactory.getInstance();
    Document[] amDocs = docManager.find(amDocLib.getDocLibID(), "SYS_DELETEFLAG=0 AND maActionID = " + actID, null);
    if(amDocs != null && amDocs.length > 0){
      for(Document amDoc : amDocs){
        StringBuilder value = new StringBuilder("");
        value.append(amDoc.getString("maMemberName")).append("（").append(amDoc.getString("maMemberMobile")).append("）");
        memberMap.put(amDoc.getInt("maMemberID"), value.toString());
      }
    }
    return memberMap;
  }
  /**
   * 保存短信日志
   * @param request
   * @param memberMap
   * @param msgid
   * @throws E5Exception
   */
  private Map<Integer, Long> MsgLog(HttpServletRequest request,Map<Integer, String> memberMap ,long msgid, Map<Integer, Integer> msgStatusMap) throws E5Exception {
    
    Map<Integer, Long> msgLogMap = new HashMap<Integer, Long>();
    Set<Integer> keys = memberMap.keySet();
    for (Integer key_id : keys) {
      String membervalue = memberMap.get(key_id);
      int msgstatus = 2; //预设发送状态默认值2,2代表发送中
      if(msgStatusMap != null)
        msgstatus= msgStatusMap.get(key_id);
      long docid = saveMsgLog(request,msgid,key_id,membervalue,msgstatus);//保存短信日志
      msgLogMap.put(key_id, docid);
    }
    return msgLogMap;
  }
  private long saveMsgLog(HttpServletRequest request,long msgid,int key_id,String membervalue,int msgstatus) throws E5Exception {
    
    DocLib msglDocLib = InfoHelper.getLib(Constant.DOCTYPE_MSGLOG, InfoHelper.getTenantCode(request));//短信日志
    DocumentManager docManager = DocumentManagerFactory.getInstance();
    Document doc = docManager.newDocument(msglDocLib.getDocLibID(), InfoHelper.getID(msglDocLib.getDocTypeID()));
    doc.setFolderID(msglDocLib.getFolderID());
    doc.setDeleteFlag(0);
    doc.setLocked(false);
    assembleDoc(doc, request);//组装
    
    doc.set("SYS_CREATED", DateUtils.getTimestamp());
    doc.set("msglStatus", msgstatus);
    doc.set("msglReceiver", membervalue);
    doc.set("msglMemberID",key_id );
    doc.set("msglMsgID", msgid);
    docManager.save(doc);
    return doc.getDocID();
  }

  /**
   * 拆分、组装收件人到List
   * @param emMember
   * @param emAction
   * @throws E5Exception 
   */
  private void BreakAndsave(HttpServletRequest request ,String emMember, String emAction) throws E5Exception {
    List<String> receivers = new ArrayList<String>();
    String temp[]=emMember.split(";");  
    for (String string : temp) {//遍历，将收件人加入list
      receivers.add(string);
    }
    String action[]=emAction.split(";");
    for (String act : action) {//遍历，将活动中的收件人加入list
      //根据活动id查询收件人并加入到list中
      receivers = addlist(request,receivers,act);
    }
    
  }
  /**
   * 根据活动id查询收件人并加入到list中
   * @param receivers
   * @param act
   * @throws E5Exception 
   */
  private List<String>  addlist(HttpServletRequest request,List<String> receivers, String act) throws E5Exception {
    String actna = act.substring(act.indexOf("(")+1,act.indexOf(")"));
    List<String> actmber = new ArrayList<String>();
    List<String> comblist = new ArrayList<String>();
    DocumentManager docManager = DocumentManagerFactory.getInstance();
    String tenantCode = InfoHelper.getTenantCode(request);
    DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBERACTION, tenantCode);//活动表
    Document[] docs = docManager.find(docLib.getDocLibID(), "maActionID = ? and  SYS_DELETEFLAG=0 ", new Object[]{actna});
    if (docs != null && docs.length > 0){
      for (Document doc : docs) {
        actmber.add(doc.getString("maMemberName")+"("+doc.getString("maMemberMobile")+")");
      }
      //将不重复的收件人加到receivers-List.
      comblist = combine(receivers,actmber);
    }
    return comblist;
  }

  private List<String> combine(List<String> receivers, List<String> actmber) {
    for (String string : actmber) {
      if(!receivers.contains(string))
        receivers.add(string);
    }
    return receivers;
  }

  /**
   * 从界面上取除系统字段外其他的所有字段组装
   * @param doc
   * @param request
   * @throws E5Exception
   */
  private void assembleDoc(Document doc, HttpServletRequest request) throws E5Exception {
    DocTypeField[] fields = docTypeReader.getFieldsExt(doc.getDocTypeID());
    if(fields != null && fields.length > 0){
      for(DocTypeField field : fields){
        FormHelper.setFieldValue(doc, request, field);
      }
    }
  }
  /**
   * 根据短信id取出短信内容
   * @param msglMsgID tenantCode
   * @return
   * @throws E5Exception 
   */
  private Document gMsgContent(int msglMsgID,String tenantCode,DocLib docLib) throws E5Exception {
    String msgContent ="";
    DocumentManager docManager = DocumentManagerFactory.getInstance();
    Document[] docs = docManager.find(docLib.getDocLibID(), "SYS_DOCUMENTID = ?  ", new Object[]{msglMsgID});
    if (docs != null && docs.length > 0){
      return docs[0];
    }
    return null;
  }
  /**
   * 调用短信猫api接口发送短信，并保存发送状态
   * @param memberMap  接收短信的会员集合
   * @param messageCon 短信内容
   * @param msgStatusMap 短信状态集合
   * @throws Exception
   */
  private void sendMSG(Map<Integer, String> memberMap, String messageCon, Map<Integer, Integer> msgStatusMap) throws Exception {
    
    SendMessage msgSend = new SendMessage();
    msgSend.setMessage(messageCon);
    msgSend.serviceStart();
    Set<Integer> keys = memberMap.keySet();
    for (Integer key_id : keys) {
      String membervalue = memberMap.get(key_id);
      String mobile = membervalue.substring(membervalue.indexOf("("), membervalue.length()-1);
      int status = msgSend.sendMessage(mobile);
      msgStatusMap.put(key_id, status);//保存发送短信后的状态
    }
    msgSend.serviceStop();
  }
  
  /**
   * 调用云服务，并保存发送状态
   * @param memberMap  接收短信的会员集合
   * @param messageCon 短信内容
   * @param msgStatusMap 短信状态集合
   * @throws Exception
   */
  private void sendMSG2(Map<Integer, String> memberMap, String messageCon, Map<Integer, Integer> msgStatusMap) throws Exception {
    
    Set<Integer> keys = memberMap.keySet();
    for (Integer key_id : keys) {
      String membervalue = memberMap.get(key_id);
      String mobile = membervalue.substring(membervalue.indexOf("("), membervalue.length()-1);
      int status = SendMsg.sendMsg(mobile, messageCon);
      msgStatusMap.put(key_id, status);//保存发送短信后的状态
    }
  }
  
  /**
   * 失败重发
   * @param request
   * @param response
   * @param model
   * @throws Exception
   */
  private void reSend(HttpServletRequest request, HttpServletResponse response, Map model) throws Exception{
    String msgSendRule = getSenRule();
    if("0".equals(msgSendRule)){
      reSend0(request, response, model);
    }else if("1".equals(msgSendRule)){
      reSend1(request, response, model);
    }
  }
  /**
   * 失败重发(规则1)数据库接口
   * @param request
   * @param response
   * @param model
   * @throws E5Exception 
   * @throws SQLException 
   */
  private void reSend1(HttpServletRequest request,
      HttpServletResponse response, Map model) throws E5Exception, SQLException {
    
    String tenantCode = InfoHelper.getTenantCode(request);//取出租户
    DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_MSG, tenantCode);//短信表
    int DocIDs = getInt(request, "DocIDs");//短信docid
    Document msgDoc = gMsgContent(DocIDs,tenantCode,docLib);//取出短信文档对象
    
    //组装需要失败重发（短信）的会员
    Map<Integer, String> memberMap = new HashMap<Integer, String>();
    memberMap.putAll(getReSendMember(request,DocIDs));
    if(memberMap == null || memberMap.size() == 0){
      
      model.put("ReSendFaild", true);
      model.put("result", "短信群发成功，无需重发");
      model.put("@VIEWNAME@", "/amuc/msg/MsgResult");
      System.out.println("短信群发成功，无需重发!"+DateUtils.getTimestamp());
      return ;
    }
    
    Map<Integer, Long> msgLog = getMsgLogID(request,memberMap,DocIDs);
    
    //发送短信,将记录插入到指定表中
    InsertMSG(memberMap,msgLog,msgDoc,model);
    
    //5.刷新页面
    model.put("needRefresh", "true"); //是否需要刷新列表
    String viewName = "redirect:/e5workspace/after.do?DocLibID=" + docLib
        + "&DocIDs="+DocIDs+ "&UUID=" + get(request,"UUID");
    model.put("@VIEWNAME@", viewName);
  }

  /**
   * 失败重发(规则0)动态链接库
   * @param request
   * @param response
   * @param model
   * @throws Exception 
   */
  private void reSend0(HttpServletRequest request, HttpServletResponse response, Map model) throws Exception{
    
    String tenantCode = InfoHelper.getTenantCode(request);//取出租户
    int DocIDs = getInt(request, "DocIDs");//短信docid
    DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_MSG, tenantCode);//短信表
    Document msgDoc = gMsgContent(DocIDs,tenantCode,docLib);//取出短信内容
    
    //组装需要失败重发（短信）的会员
    Map<Integer, String> memberMap = new HashMap<Integer, String>();
    memberMap.putAll(getReSendMember(request,DocIDs));
    if(memberMap == null || memberMap.size() == 0){
      model.put("ReSendFaild", true);
      model.put("result", "短信群发成功，无需重发");
      model.put("@VIEWNAME@", "/amuc/msg/MsgResult");
      System.out.println("短信群发成功，无需重发!"+DateUtils.getTimestamp());
      return ;
    }
    //发送短信
    Map<Integer, Integer> msgStatusMap = new HashMap<Integer, Integer>();
    sendMSG(memberMap,msgDoc.getString("msgContent"),msgStatusMap);
    
    //更新短信日志表
    UpdateMsgLog(request,DocIDs,memberMap,msgStatusMap);
    
    //5.刷新页面
    model.put("needRefresh", "true"); //是否需要刷新列表
    String viewName = "redirect:/e5workspace/after.do?DocLibID=" + docLib
        + "&DocIDs="+DocIDs+ "&UUID=" + get(request,"UUID");
    model.put("@VIEWNAME@", viewName);
  }
  
  /**
   * 取出失败重发的会员集合
   * @param request
   * @param DocIDs
   * @return
   * @throws E5Exception
   */
  private Map<Integer, String> getReSendMember(HttpServletRequest request, int DocIDs) throws E5Exception {
    
    Map<Integer, String> memberMap = new HashMap<Integer, String>();
    DocumentManager docManager = DocumentManagerFactory.getInstance();
    String tenantCode = InfoHelper.getTenantCode(request);
    DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_MSGLOG, tenantCode);//短信日志表
    //根据短信ID和短信发送状态，取出所有发送失败的会员
    Document[] docs = docManager.find(docLib.getDocLibID(), "msglMsgID = ? and msglStatus = 3 and SYS_DELETEFLAG = 0 ", new Object[]{DocIDs});
    if (docs != null && docs.length > 0){
      for (Document doc : docs) {
        int msglMemberID =  doc.getInt("msglMemberID");//会员DocID
        String msglReceiver =  doc.getString("msglReceiver");//收件人
        memberMap.put(msglMemberID, msglReceiver);
      }
    }
    return memberMap;
  }
  
  /**
   * 更新短信日志表（针对失败重发）
   * @param docIDs 短信id
   * @param memberMap 会员集合
   * @param msgStatusMap 短信状态集合
   * @throws E5Exception 
   */
  private void UpdateMsgLog(HttpServletRequest request,int docIDs, Map<Integer, String> memberMap,
      Map<Integer, Integer> msgStatusMap) throws E5Exception {
    
    DocumentManager docManager = DocumentManagerFactory.getInstance();
    String tenantCode = InfoHelper.getTenantCode(request);
    DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_MSGLOG, tenantCode);//短信日志表
    Document[] docs  = null;
    Set<Integer> keys = memberMap.keySet();
    for (Integer key_id : keys) {
      //根据短信ID和会员ID共同确定一条短信日志记录
      docs = docManager.find(docLib.getDocLibID(), "msglMsgID = ? and msglMemberID = ? and SYS_DELETEFLAG = 0 ", new Object[]{docIDs,key_id});
      if (docs != null && docs.length > 0){
        docs[0].set("msglStatus", msgStatusMap.get(key_id));
      }
      docManager.save(docs[0]);
    }

  }
  /**
   * 将短信记录插入到指定表中，以实现发送短信功能
   * Msg-内容,Mbno-手机号码 ,如果sendtime不填，默认为当前时间，即马上发送
   * @param memberMap  收短信会员集合
   * @param messageCon 短信内容
   * @throws SQLException 
   * @throws E5Exception 
   */
  private void InsertMSG(Map<Integer, String> memberMap,Map<Integer, Long> msgLog,Document msgDoc,Map model) throws SQLException, E5Exception {
    
    String[] config = getMsgDB();
    if(config == null || config.length <=0 ){
      model.put("DBSendFaild", true);
      model.put("result", "数据库参数配置出错，参数不能为空!");
      model.put("@VIEWNAME@", "/amuc/msg/MsgResult");
      System.out.println("数据库参数配置出错，参数不能为空!"+DateUtils.getTimestamp());
      return ;
    }
    Connection conn = null;
    Statement stmt = null;
    try{
      Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");//加载数据库引擎，返回给定字符串名的类
      conn = DriverManager.getConnection("jdbc:sqlserver://"+config[0]+":1433;DatabaseName="+config[1],config[2],config[3]);//连接数据库对象
      stmt = conn.createStatement();//创建SQL命令对象
      conn.setAutoCommit(false);
      
      Set<Integer> keys = memberMap.keySet();
      for (Integer key_id : keys) {
        StringBuffer sql = new StringBuffer();
        String membervalue = memberMap.get(key_id);
        String mobile = membervalue.substring(membervalue.indexOf("(")+1, membervalue.length()-1);
        sql.append("insert into OutBox (").append(" Msg,Mbno,Report,V1 ");
        sql.append(") values('");
        sql.append(msgDoc.getString("msgContent"));
        sql.append("' , '").append(mobile);
        sql.append("' , 1,").append(msgLog.get(key_id)).append(")");
        stmt.addBatch(sql.toString());
      }
      stmt.executeBatch();  
      conn.commit();
    }
    catch(Exception e){
      model.put("DBSendFaild", true);
      model.put("result", "数据库参数配置出错");
      model.put("@VIEWNAME@", "/amuc/msg/MsgResult");
      System.out.println("数据库参数配置出错!"+DateUtils.getTimestamp());
      return ;
      //e.printStackTrace();
    }finally{
      ResourceMgr.closeQuietly(stmt);
          ResourceMgr.closeQuietly(conn);
    }
  }
  /** 
  * 功能：群发短信后续刷新操作列表
  * @param request
  * @param model
  * @throws E5Exception 
  */ 
  protected void afterProcess(HttpServletRequest request, Map model) throws E5Exception{
    AfterService service = (AfterService) Context.getBean(AfterService.class);
    String uuid = get(request,"UUID");
    
    //从session中按uuid取得操作前保存的参数
    ProcParam beforeParam = (ProcParam)request.getSession().getAttribute(uuid);
    if (beforeParam == null) return;
    
    AfterParam afterParam = new AfterParam();
    afterParam.setDocLibIDs(beforeParam.getDocLibIDs());
    afterParam.setDocIDs(beforeParam.getDocIDs());  
    //取得后清除session
    request.getSession().removeAttribute(uuid);
    
    try {
      service.process(afterParam, beforeParam);
    } catch (Exception e) {
      log.error("[AfterProcess.service]Exception", e);      
    }
    
    //最后做窗口控制
    ProcReader procReader = (ProcReader)Context.getBean("ProcReader");    
    Operation operation = procReader.getOperation(beforeParam.getOpID());
    
    model.put("needRefresh", String.valueOf(operation.isNeedRefresh()));
    model.put("callMode", String.valueOf(operation.getCallMode()));
  }
  /**
   * 获取短信数据库
   * @return
   * @throws E5Exception
   */
  private String[] getMsgDB() throws E5Exception{
    int appID = 1;
    SysConfigReader configReader = (SysConfigReader)Context.getBean(SysConfigReader.class);
    String msgSendRule = configReader.get(appID, "会员中心", "短信数据库选择");
    if(msgSendRule == null || "".equals(msgSendRule) ||msgSendRule.length()<=0){
      return null;
    }
    String[] dbconfig = msgSendRule.split(";");
    return dbconfig;
  }
  /**
   * 获取会员id与短信日志id的集合
   * @param request
   * @param memberMap
   * @param docIDs
   * @return
   * @throws E5Exception 
   */
  private Map<Integer, Long> getMsgLogID(HttpServletRequest request,
      Map<Integer, String> memberMap, int docIDs) throws E5Exception {
    
    Map<Integer, Long> msgLogid = new HashMap<Integer, Long>();
    DocumentManager docManager = DocumentManagerFactory.getInstance();
    String tenantCode = InfoHelper.getTenantCode(request);
    DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_MSGLOG, tenantCode);//短信日志表
    
    Set<Integer> keys = memberMap.keySet();
    for (Integer key_id : keys) {
      //根据短信ID和会员ID，取出短信记录ID
      Document[] docs = docManager.find(docLib.getDocLibID(), "msglMsgID = ? and msglMemberID = ? and SYS_DELETEFLAG = 0 ", new Object[]{docIDs,key_id});
      if (docs != null && docs.length > 0){
        msgLogid.put(key_id, docs[0].getDocID());
      }
    }
    
    return msgLogid;
  }
  private DocTypeReader docTypeReader = (DocTypeReader)Context.getBean(DocTypeReader.class);
}
