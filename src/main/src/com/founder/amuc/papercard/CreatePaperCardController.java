package com.founder.amuc.papercard;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.net.ntp.TimeStamp;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.founder.e5.context.Context;

import org.springframework.beans.factory.annotation.Autowired;

import com.founder.amuc.commons.Constant;
import com.founder.amuc.commons.FormViewerHelper;
import com.founder.amuc.commons.InfoHelper;
import com.founder.amuc.commons.RedisManager;
import com.founder.amuc.commons.JedisClient;
import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.DomHelper;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.e5.dom.queryForm.QueryForm;
import com.founder.e5.web.BaseController;
import com.founder.e5.web.DomInfo;
import com.founder.e5.workspace.query.QueryFormParser;

public class CreatePaperCardController extends BaseController {
	@Autowired
	JedisClient jedisClient;
  PaperCardManager manager = null;

  public void setManager(PaperCardManager manager) {
    this.manager = manager;
  }

  QueryFormParser queryFormParser = null;
  
  @SuppressWarnings("rawtypes")
  protected void handle(HttpServletRequest request,
      HttpServletResponse response, Map model) throws Exception {

    String action = get(request, "a");
    if ("pcardInit".equals(action)) {
      // 报卡生成页面初始化
      pcardInit(request, response, model);
    } else if ("pcardchange".equals(action)) {
      //修改报卡
      pcardchange(request, response, model);
    }  else if ("create".equals(action)) {
      //生成报卡
      create(request, response, model);
    }  else if ("create2".equals(action)) {
      //生成报卡
      create2(request, response, model);
    } else if ("createSysNumber".equals(action)) {
      createSysNumber(request, response, model);
    } else if ("InitQueryPaperCard".equals(action)) {
      InitQueryPaperCard(request , model);
    } else if ("QueryPaperCard".equals(action)) {
      QueryPaperCard(request, response, model);
    } else if ("QueryPaperLogCard".equals(action)) {
      QueryPaperLogCard(request, response, model);
    } else if ("pcardLog".equals(action)) {
      pcardLog(request, response, model);
    }
  }
  //查询列表
    private void QueryPaperLogCard(HttpServletRequest request,
        HttpServletResponse response, @SuppressWarnings("rawtypes") Map model) throws Exception {
      DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_PAPERCARDLOG);
      //查询列表展示
      //DocLib mDocLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER);
      DomInfo domInfo = new DomInfo();
      domInfo.setDocTypeID(docLib.getDocTypeID());
      domInfo.setDocLibID(docLib.getDocLibID());
      domInfo.setFolderID(docLib.getFolderID());
      String xnbIDSql = null;
      String setRule = null;
      //组装前台的查询条件
      String condition = getRrCondition(request);
      if(!condition.equals("null")){
        xnbIDSql = "SELECT SYS_DOCUMENTID from "+docLib.getDocLibTable()+" where " +  condition 
            + " and @GROUPDEPTS@  ";
        setRule = "SYS_DOCUMENTID_SPC_IN(" + xnbIDSql + ")_SPC_";
      }
      domInfo.setRule(setRule);
      String listName="报卡日志列表";
      domInfo.setListID(DomHelper.getListID(docLib.getDocTypeID(), listName));
      
      QueryForm query = DomHelper.getQuery(docLib.getDocTypeID());
      if (query != null) {
        domInfo.setQueryID(query.getId());
        domInfo.setQueryScripts(StringUtils.split(query.getPathJS(), ","));
      }
      //domInfo.setQueryID(33);
      output(JSONObject.fromObject(domInfo).toString(), response);
    }
    
  //查询列表
  @SuppressWarnings("rawtypes")
  private void QueryPaperCard(HttpServletRequest request,
      HttpServletResponse response, Map model) throws Exception {
    DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_PAPERCARD);
    //查询列表展示
    //DocLib mDocLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER);
    DomInfo domInfo = new DomInfo();
    domInfo.setDocTypeID(docLib.getDocTypeID());
    domInfo.setDocLibID(docLib.getDocLibID());
    domInfo.setFolderID(docLib.getFolderID());
    String xnbIDSql = null;
    String setRule = null;
    //组装前台的查询条件
    String condition = getRrCondition(request);
    if(!condition.equals("null")){
      xnbIDSql = "SELECT SYS_DOCUMENTID from "+docLib.getDocLibTable()+" where " +  condition 
          + " and @GROUPDEPTS@  ";
      setRule = "SYS_DOCUMENTID_SPC_IN(" + xnbIDSql + ")_SPC_";
    }
    domInfo.setRule(setRule);
    String listName="报卡列表";
    domInfo.setListID(DomHelper.getListID(docLib.getDocTypeID(), listName));
    
    QueryForm query = DomHelper.getQuery(docLib.getDocTypeID());
    if (query != null) {
      domInfo.setQueryID(query.getId());
      domInfo.setQueryScripts(StringUtils.split(query.getPathJS(), ","));
    }
    //domInfo.setQueryID(32);
    output(JSONObject.fromObject(domInfo).toString(), response);
  }
  //查询条件
  private String getRrCondition(HttpServletRequest request){
    
    return "null";
  }
  
  //初始化查询页面
  @SuppressWarnings("unchecked")
  protected void InitQueryPaperCard(HttpServletRequest request, @SuppressWarnings("rawtypes") Map model) throws Exception {
    
    String tenantCode = InfoHelper.getTenantCode(request);
    String op = get(request, "op");
    DomInfo domInfo = null;
    
    if("paperCard".equals(op)){//按报卡检索
      domInfo = getDomInfo(request,tenantCode, Constant.DOCTYPE_PAPERCARD);
      model.put("domInfo", domInfo);
      //model.put("actionType",getCatType("paperCard"));
      model.put("@VIEWNAME@", "amuc/PaperCardQuery");
    }else if("paperCardLog".equals(op)){//按群组检索
      domInfo = getDomInfo(request,tenantCode, Constant.DOCTYPE_PAPERCARDLOG);
      model.put("domInfo", domInfo);
      //model.put("groupType",getCatType("paperCardLog"));
      model.put("@VIEWNAME@", "amuc/PaperCardLogQuery");
    }
  }

  /**
   * 根据一个文档类型，读取其文档类型ID、文档库ID、文件夹ID信息
   * @param docType
   * @return
   * @throws E5Exception
   */
  protected DomInfo getDomInfo(HttpServletRequest request, String tenantCode, String type) throws E5Exception {
    DomInfo domInfo = new DomInfo();
    String listName = "";
    DocLib docLib = null;
    //读文档库和文件夹
    //DocLib docLib = InfoHelper.getLib(type, tenantCode);
    if(Constant.DOCTYPE_PAPERCARD.equals(type)){//按报卡抽取
      docLib = InfoHelper.getLib(Constant.DOCTYPE_PAPERCARD, tenantCode);
      listName = "报卡列表";
    } else if(Constant.DOCTYPE_PAPERCARDLOG.equals(type)){//按报卡日志抽取
      docLib = InfoHelper.getLib(Constant.DOCTYPE_PAPERCARDLOG, tenantCode);
      listName = "报卡日志列表";
    }
    
    domInfo.setDocLibID(docLib.getDocLibID());
    domInfo.setFolderID(docLib.getFolderID());
    domInfo.setDocTypeID(docLib.getDocTypeID());
    domInfo.setRule("");

    //读缺省列表方式（文档类型的第一个）
    domInfo.setListID(InfoHelper.getListID(domInfo.getDocTypeID(), listName));
    
    //读缺省查询条件（文档类型的第一个）
    QueryForm query = DomHelper.getQuery(docLib.getDocTypeID());
    if (query != null) {
      domInfo.setQueryID(query.getId());
      domInfo.setQueryScripts(StringUtils.split(query.getPathJS(), ","));
    }
    
    return domInfo;
  }

  
  // 报卡生成页面初始化
  @SuppressWarnings("unchecked")
  private void pcardInit(HttpServletRequest request,
      HttpServletResponse response, @SuppressWarnings("rawtypes") Map model) throws E5Exception {

    DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_PAPERCARD,
        InfoHelper.getTenantCode(request));
    long docID = getInt(request, "DocIDs", 0);
    int p = getInt(request, "p", 0);
    model.put("DocLibID", docLib.getDocLibID());
    model.put("DocIDs", docID);
    model.put("FVID", docLib.getFolderID());
    model.put("UUID", get(request, "UUID"));

    String[] formJsp = FormViewerHelper.getFormJsp(docLib.getDocLibID(),
        docID, "FormPaperCard", get(request, "UUID"));
    model.put("formHead", formJsp[0]);
    model.put("formContent", FormViewerHelper.delFormStr(formJsp[1]));
    
    String VIEWNAME = "";
    if(p == 0){
      VIEWNAME = "amuc/papercard/papercardInit";  //新版报卡生成
    }else if(p == 1){
      VIEWNAME = "amuc/papercard/papercardInit2";  //石油报版本报卡生成
    }else if(p == 2){
      VIEWNAME = "amuc/papercard/papercardChange";  //报卡修改
    }
    
    model.put("@VIEWNAME@", VIEWNAME);// 跳转到jsp页面
  }
  /**
   *生成报卡（二期） 石油报
   * @param request
   * @param response
   * @param model
   * @throws E5Exception
   */
  @SuppressWarnings("unchecked")
  private void create(HttpServletRequest request,
      HttpServletResponse response, @SuppressWarnings("rawtypes") Map model) throws E5Exception {
    //String pcYear = get(request, "pcYear", "");
    String pcPaperMark = get(request, "pcPaperMark", "");
    String pcPaperName = get(request, "pcPaperName", "");
    String pcMultiple = get(request, "pcMultiple", "");
    //String pcExpireTime = get(request, "pcExpireTime", "");
    String pcEffectTime = get(request, "pcEffectTime", "");
    String pcOperator = get(request, "pcOperator", "");
    String pcOperatorID = get(request, "pcOperatorID", "");
    String pcArea = get(request, "pcArea", "");
    String pcTotal1 = get(request, "pcTotal", "");
    log.info("pcPaperName:"+pcPaperName+";pcTotal:"+pcTotal1+";pcEffectTime:"+pcEffectTime+";pcMultiple:"+pcMultiple);
    //检查参数
    if(StringUtils.isBlank(pcPaperMark)||StringUtils.isBlank(pcPaperName)
        ||StringUtils.isBlank(pcMultiple)||StringUtils.isBlank(pcEffectTime)
            ||StringUtils.isBlank(pcTotal1)||StringUtils.isBlank(pcOperator)
            ||StringUtils.isBlank(pcOperatorID)||StringUtils.isBlank(pcArea)){
      return ;
    }
    //失效时间
    int month = Integer.parseInt(pcMultiple)*6;
    //生成卡号
    //报卡号生成规则，如果生成规则发生改变，需要修改 根据卡号前缀读取订阅报纸接口，（接口：PaperCardAdapter里的readPaperByPcnoPrefix）
    String pcNo = "";
    String[] mark = pcPaperMark.split(",");
    int markLen = mark.length;
    switch (markLen) {
    case 1:
      pcNo = "S00"+mark[0];
      break;
    case 2:
      pcNo = "M0"+mark[0]+mark[1];
      break;
    case 3:
      pcNo = "M"+mark[0]+mark[1]+mark[2];
      break;
    case 4:
      pcNo = "A001";
      break;

    default:
      break;
    }
    
    if(StringUtils.isBlank(pcNo)){
      return;
    }
    SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd" ); 
    SimpleDateFormat sdf1 = new SimpleDateFormat( "yyMMdd" );
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String pcEffectTime1="";
    //String pcExpireTime1="";
    try {
      pcEffectTime1 = sdf1.format(sdf.parse(pcEffectTime));
      //pcExpireTime1 = sdf1.format(sdf.parse(pcExpireTime));
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }
    pcNo+=pcArea+pcEffectTime1+pcMultiple;
    pcNo = pcNo.replace("4", "H");
    //pcNo = doPcNo(pcNo);
    DocumentManager docManager = DocumentManagerFactory.getInstance();
    DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_PAPERCARD);
    DocLib docLib_log = InfoHelper.getLib(Constant.DOCTYPE_PAPERCARDLOG);
    String sql = "SELECT COUNT(SYS_DOCUMENTID) num FROM ucpapercard_log WHERE SYS_DELETEFLAG = 0 and cardPrefix like '"+pcNo+"%'";
    int num = selectCount(sql);
    if(num>0){
      String viewName = "redirect:/e5workspace/after.do?DocLibID=" + docLib.getDocLibID()
          + "&DocIDs=" + 1 + "&UUID=" + get(request, "UUID");
      model.put("Info", "同时间段相同报刊类型已存在，请从新选择生成规则！！！");
      model.put("@VIEWNAME@", viewName);
      return;
    }
    
    int pcTotal = Integer.parseInt(pcTotal1);
    //Timestamp curtime = DateUtils.getTimestamp();
    String curtime = df.format(new Date());
    String pcExpireTime = pcEffectTime;
    
    if(pcTotal>0 && pcTotal<99999){
      String init = "00000";
      int i = 0;
      try {
        //计算过期时间
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(pcEffectTime));
        cal.add(Calendar.MONTH,month);
        pcExpireTime = sdf.format(cal.getTime());
        
        for ( i=0 ; i < pcTotal; i++) {
            Document doc = docManager.newDocument(docLib.getDocLibID(),InfoHelper.getID(docLib.getDocTypeID()));
            doc.setDeleteFlag(0);
            //doc.setLocked(false);
            doc.set("SYS_CREATED", curtime);
            doc.set("pcPaperMark", pcPaperMark);
            doc.set("pcEffectTime", pcEffectTime);
            doc.set("pcExpireTime", pcExpireTime);
            doc.set("pcPaperName", pcPaperName);
            doc.set("pcPaperName", pcPaperName);
            doc.set("pcOperator", pcOperator);
            doc.set("pcMultiple", pcMultiple);
            doc.set("pcActiveStatus", "未激活");
            doc.setFolderID(docLib.getFolderID());
            //报卡
            init = String.valueOf(increStrsys(init));
            
            String init1 = new String(init.replace("4", "H"));
            String pcNo1 = new String(pcNo+init1);
            //检查pcNo是否有4，将4转换成H
            log.info(pcNo1);
            int pcPassword = new Random().nextInt(899999)+100000;
            doc.set("pcPassword", pcPassword);
            doc.set("pcNo", pcNo1);
            docManager.save(doc);
          }
        savePaperRecord(docManager, docLib_log, curtime, pcNo, pcTotal, pcEffectTime, pcExpireTime, pcTotal, pcOperator, pcOperatorID,pcPaperName,"");
      } catch (Exception e) {
        savePaperRecord(docManager, docLib_log, curtime, pcNo, pcTotal, pcEffectTime, pcExpireTime, i, pcOperator, pcOperatorID,pcPaperName,"");
        String viewName = "redirect:/e5workspace/after.do?DocLibID=" + docLib.getDocLibID()
            + "&DocIDs=" + 1 + "&UUID=" + get(request, "UUID");
        model.put("Info", "生成失败，请稍后重试");
        model.put("@VIEWNAME@", viewName);
        e.printStackTrace();
      }
      
    }else{
      savePaperRecord(docManager, docLib_log, curtime, pcNo, pcTotal, pcEffectTime1, pcExpireTime, 0, pcOperator, pcOperatorID,pcPaperName,"");
      String viewName = "redirect:/e5workspace/after.do?DocLibID=" + docLib.getDocLibID()
          + "&DocIDs=" + 1 + "&UUID=" + get(request, "UUID");
      model.put("Info", "请正确输入生成数量");
      model.put("@VIEWNAME@", viewName);
      return;
    }
    String viewName = "redirect:/e5workspace/after.do?DocLibID=" + docLib.getDocLibID()
        + "&DocIDs=" + 1 + "&UUID=" + get(request, "UUID");
    model.put("@VIEWNAME@", viewName);
  }
  
  private void savePaperRecord(DocumentManager docManager,DocLib docLib_log,String curtime,
      String pcNo,int pcTotal,String pcEffectTime,String pcExpireTime,int status,
      String pcOperator,String pcOperatorID,String paperName,String pcTotalMoney){
    try {
      Document docLog = docManager.newDocument(docLib_log.getDocLibID());
      docLog.set("SYS_CREATED", curtime);
      docLog.setFolderID(docLib_log.getFolderID());
      docLog.set("cardPrefix", pcNo);
      docLog.set("operationNum", pcTotal);
      docLog.set("pcTotalMoney", pcTotalMoney);  //报卡总额
      docLog.set("effectTime", pcEffectTime);
      docLog.set("expireTime", pcExpireTime);
      docLog.set("paperName", paperName);
      docLog.set("operation", "新建");
      docLog.set("status", "已售");//对应状态
      if(status==pcTotal){
        docLog.set("detail", "成功生成"+status+"张");
      }else{
        docLog.set("detail", "失败，仅生成"+status+"张");
      }
      docLog.set("operator", "后台："+pcOperator);
      docLog.set("operatorid", pcOperatorID);
      docManager.save(docLog);
    } catch (E5Exception e) {
      e.printStackTrace();
    }
  }
  
  //查询记录数
  private int selectCount(String querySql){
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
  
  // 生成报卡
  @SuppressWarnings({ "unchecked", "unused" })
  private void create1(HttpServletRequest request,
      HttpServletResponse response, @SuppressWarnings("rawtypes") Map model) throws E5Exception {

    String pcYear = get(request, "pcYear", "");
    String pcPaperMark = get(request, "pcPaperMark", "");
    String pcMember_ID = get(request, "pcMember_ID", "");
    String pcExpireTime = get(request, "pcExpireTime", "");
    
    //检查参数
    if(StringUtils.isBlank(pcMember_ID) || StringUtils.isBlank(pcExpireTime)){
      return ;
    }
    DocumentManager docManager = DocumentManagerFactory.getInstance();
    DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_PAPERCARD);
    DocLib mdocLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER);
    //检查会员是否存在
    String condition = "SYS_DELETEFLAG = 0 and SYS_DOCUMENTID = ?";
    String [] columns = {"mName","mMobile"};
    Document[] mdocs = docManager.find(mdocLib.getDocLibID(), condition, new Object[]{pcMember_ID},columns);
    if(mdocs == null || mdocs.length == 0){
      return;
    }else{
      //新建报卡记录
      Document doc = docManager.newDocument(docLib.getDocLibID(),InfoHelper.getID(docLib.getDocTypeID()));
      doc.setFolderID(docLib.getFolderID());
      doc.setDeleteFlag(0);
      doc.setLocked(false);
      doc.set("SYS_CREATED", DateUtils.getTimestamp());
      doc.set("pcYear", pcYear);
      doc.set("pcPaperMark", pcPaperMark);
      doc.set("pcMember_ID", pcMember_ID);
      doc.set("pcMember", mdocs[0].getString("mName"));
      doc.set("pcExpireTime", pcExpireTime);
      doc.set("pcActiveStatus", "未激活");

      //从redis中取出系统生成部分
      String pcSystemInput = jedisClient.lpop("amuc:papercard:sysnum");
      String pcNo = pcYear + pcPaperMark + pcSystemInput;
      doc.set("pcNo", pcNo);
      docManager.save(doc);
    }
    String viewName = "redirect:/e5workspace/after.do?DocLibID=" + docLib.getDocLibID()
        + "&DocIDs=" + 1 + "&UUID=" + get(request, "UUID");
    model.put("@VIEWNAME@", viewName);
  }
  
  //生成系统随机数
  @SuppressWarnings({ "unchecked", "rawtypes" })
  private void createSysNumber(HttpServletRequest request,
      HttpServletResponse response, Map model){
    
     List list = new ArrayList();
       String init = "000000";
       for (int i = 0 ;i < 100000 ;i++) {
         list.add( init );
         init = String.valueOf(increStrsys(init));
     }
       Collections.shuffle(list);  
       System.out.println(list);
       
       //写入redis 缓存
       jedisClient.lpushx("amuc:papercard:sysnum", "123","123");
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
  
  /**
   *生成报卡（三期） 
   * @param request
   * @param response
   * @param model
   * @throws E5Exception
   */
  @SuppressWarnings("unchecked")
  private void create2(HttpServletRequest request,
      HttpServletResponse response, @SuppressWarnings("rawtypes") Map model) throws E5Exception {
    //String pcYear = get(request, "pcYear", "");
    String pcPaperMark = get(request, "paperNumber", "");
    
    String pcPaperName=get(request, "pcPaperName").toString();
    
    //String pcPaperName = pcPaperName1.substring(0, pcPaperName1.length()-1);
    String pcMultiple = get(request, "pcMultiple", "");
    
    
    String pcEffectTime = get(request, "pcEffectTime", "");  // 生效时间
    String pcExpireTime = get(request, "pcExpireTime", "");  // 失效时间
    String pcOperator = get(request, "pcOperator", "");  // 操作人
    String pcOperatorID = get(request, "pcOperatorID", "");
    String pcTypeCode = get(request, "pcTypeCode", "");  //报纸种类代码
    String pcArea = get(request, "pcArea", "");  //报纸地区代码
    String pcTotal1 = get(request, "pcTotal", "");  //生成数量
    String pcExpiryDate = get(request, "pcExpiryDate", "");  //连续自然日
    String pcMoney = get(request, "pcMoney", "");  //报卡金额
    String pcReadway = getCheckboxVal(request,"pcReadway");  //阅读渠道
    String pcRemind = getCheckboxVal(request,"pcRemind");  //过期提醒
    String pcPay = get(request, "pcPay", "");  //支付方式
    //String pcStatus = get(request, "pcStatus", "");  //状态
    
    String pcTotalMoney = get(request, "pcTotalMoney", "");  //报卡总额
    
    log.info("pcExpiryDate:"+pcExpiryDate+";pcTotal:"+pcTotal1+";pcEffectTime:"+pcEffectTime+";pcExpireTime:"+pcExpireTime);
    //System.out.println("pcExpiryDate:"+pcExpiryDate+";pcTotal:"+pcTotal1+";pcEffectTime:"+pcEffectTime+";pcExpireTime:"+pcExpireTime);
    //检查参数
    if(StringUtils.isBlank(pcPay)||StringUtils.isBlank(pcReadway)
        ||StringUtils.isBlank(pcTypeCode)||StringUtils.isBlank(pcMoney)
            ||StringUtils.isBlank(pcTotal1)||StringUtils.isBlank(pcOperator)
            ||StringUtils.isBlank(pcOperatorID)||StringUtils.isBlank(pcArea)){
      return ;
    }
    if(pcEffectTime==""){
      pcEffectTime=null;
    }
    if(pcExpireTime==""){
      pcExpireTime=null;
    }
    String pcNo = "";
    SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd" ); 
    SimpleDateFormat sdf1 = new SimpleDateFormat( "yyMMdd" );
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String pcEffectTime1="";
    //String pcExpireTime1="";
    try {
      pcEffectTime1 = sdf1.format(new Date());
      //pcExpireTime1 = sdf1.format(sdf.parse(pcExpireTime));
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }
    pcNo+=pcTypeCode+pcArea+pcEffectTime1;
    pcNo = pcNo.replace("4", "H");
    //pcNo = doPcNo(pcNo);
    DocumentManager docManager = DocumentManagerFactory.getInstance();
    DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_PAPERCARD);
    DocLib docLib_log = InfoHelper.getLib(Constant.DOCTYPE_PAPERCARDLOG);
    DocLib docLib_typecode = InfoHelper.getLib(Constant.DOCTYPE_TYPECODE);
    Document[] doc_typecode = docManager.find(docLib_typecode.getDocLibID(), " SYS_DELETEFLAG = 0 and pcTypeCode = ? ", new Object[]{pcTypeCode});
    if(doc_typecode == null || doc_typecode.length <= 0){
      Document doc_typecode1 = docManager.newDocument(docLib_typecode.getDocLibID(),InfoHelper.getID(docLib_typecode.getDocTypeID()));
      doc_typecode1.setDeleteFlag(0);
      doc_typecode1.setFolderID(docLib_typecode.getFolderID());
      doc_typecode1.set("pcTypeCode", pcTypeCode);
      docManager.save(doc_typecode1);
    }
    
    //String sql = "SELECT COUNT(SYS_DOCUMENTID) num FROM ucpapercard_log WHERE SYS_DELETEFLAG = 0 and cardPrefix like '"+pcNo+"%'";
    //int num = selectCount(sql);
    String sqlNum = "SELECT sum(operationNum) num FROM ucpapercard_log WHERE SYS_DELETEFLAG = 0 and cardPrefix like '"+pcNo+"%'";   
    int operationNum = selectCount(sqlNum);
    /*if(num>0){
      String viewName = "redirect:/e5workspace/after.do?DocLibID=" + docLib.getDocLibID()
          + "&DocIDs=" + 1 + "&UUID=" + get(request, "UUID");
      model.put("Info", "同时间段相同报刊类型已存在，请从新选择生成规则！！！");
      model.put("@VIEWNAME@", viewName);
      return;
    }*/
    
    int pcTotal = Integer.parseInt(pcTotal1);
    String curtime = df.format(new Date());
    //String pcExpireTime = pcEffectTime;
    
    if(pcTotal>0 && pcTotal<99999){
      String init = "";
      if(operationNum>0){
        String baseStr="000000";
        init = baseStr.substring(String.valueOf(operationNum).length())+operationNum;
      }else{
        init = "000000";
      } 
      int i = 0;
      try {
        //计算过期时间
        //Calendar cal = Calendar.getInstance();
        //cal.setTime(sdf.parse(pcEffectTime));
        //cal.add(Calendar.MONTH,month);
        //pcExpireTime = sdf.format(cal.getTime());
        
        for ( i=0 ; i < pcTotal; i++) {
            Document doc = docManager.newDocument(docLib.getDocLibID(),InfoHelper.getID(docLib.getDocTypeID()));
            doc.setDeleteFlag(0);
            //doc.setLocked(false);
            //doc.setCreated(curtime);
            doc.set("SYS_CREATED", curtime);
            doc.set("pcPaperMark", pcPaperMark);
            doc.set("pcEffectTime", pcEffectTime);
            doc.set("pcExpireTime", pcExpireTime);
            doc.set("pcPaperName", pcPaperName);            
            doc.set("pcOperator", pcOperator);
            
            doc.set("pcExpiryDate", pcExpiryDate==""?"--":pcExpiryDate);
            doc.set("pcMoney", pcMoney);
            doc.set("pcReadway", pcReadway);
            doc.set("pcReadway_ID", manager.getCatElementId(pcReadway, "pcReadway_ID", docLib.getDocTypeID()));//下拉
            doc.set("pcRemind", pcRemind);
            doc.set("pcPay", pcPay);
            doc.set("pcPay_ID", manager.getCatElementId(pcPay,"pcPay_ID", docLib.getDocTypeID()));//下拉
            doc.set("pcStatus", "有效");
            doc.set("pcStatus_ID", manager.getCatElementId(doc.getString("pcStatus"), "pcStatus_ID", docLib.getDocTypeID()));//下拉
            
            doc.set("pcActiveStatus", "未激活");
            doc.setFolderID(docLib.getFolderID());
            //报卡
            init = String.valueOf(increStrsys(init));
            
            //String init1 = new String(init.replace("4", "H"));
            String pcNo1 = new String(pcNo+init);
            //检查pcNo是否有4，将4转换成H
            log.info(pcNo1);
            int pcPassword = new Random().nextInt(899999)+100000;
            doc.set("pcPassword", pcPassword);
            doc.set("pcNo", pcNo1);
            docManager.save(doc);
          }
        savePaperRecord(docManager, docLib_log, curtime, pcNo, pcTotal, pcEffectTime, pcExpireTime, pcTotal, pcOperator, pcOperatorID,pcPaperName,pcTotalMoney);
      } catch (Exception e) {
        savePaperRecord(docManager, docLib_log, curtime, pcNo, pcTotal, pcEffectTime, pcExpireTime, i, pcOperator, pcOperatorID,pcPaperName,pcTotalMoney);
        String viewName = "redirect:/e5workspace/after.do?DocLibID=" + docLib.getDocLibID()
            + "&DocIDs=" + 1 + "&UUID=" + get(request, "UUID");
        model.put("Info", "生成失败，请稍后重试");
        model.put("@VIEWNAME@", viewName);
        e.printStackTrace();
      }
      
    }else{
      savePaperRecord(docManager, docLib_log, curtime, pcNo, pcTotal, pcEffectTime1, pcExpireTime, 0, pcOperator, pcOperatorID,pcPaperName,pcTotalMoney);
      String viewName = "redirect:/e5workspace/after.do?DocLibID=" + docLib.getDocLibID()
          + "&DocIDs=" + 1 + "&UUID=" + get(request, "UUID");
      model.put("Info", "请正确输入生成数量");
      model.put("@VIEWNAME@", viewName);
      return;
    }
    String viewName = "redirect:/e5workspace/after.do?DocLibID=" + docLib.getDocLibID()
        + "&DocIDs=" + 1 + "&UUID=" + get(request, "UUID");
    model.put("@VIEWNAME@", viewName);
  }
  
  private String getCheckboxVal(HttpServletRequest request,String val){
    String vals = "";

    for (String val1 : request.getParameterValues(val)) {
      vals += val1 + ",";
    }
    if (vals.indexOf(",") > 0) {

      vals = vals.substring(0, vals.length() - 1);
    }
    return vals;
  }
  
  /**
   *修改报卡（三期） 
   * @param request
   * @param response
   * @param model
   * @throws E5Exception
   */
  @SuppressWarnings("unchecked")
  private void pcardchange(HttpServletRequest request,
      HttpServletResponse response, @SuppressWarnings("rawtypes") Map model) throws E5Exception {
    
    String docid = getCheckboxVal(request,"DocID");
    String pcReadway = getCheckboxVal(request,"pcReadway");  //阅读渠道
    String pcRemind = getCheckboxVal(request,"pcRemind");  //过期提醒
    String pcStatus = get(request, "pcStatus", "");  //状态
    
    log.info("pcReadway:"+pcReadway+";pcRemind:"+pcRemind+";pcStatus:"+pcStatus);
    //检查参数
    if(StringUtils.isBlank(pcReadway)||StringUtils.isBlank(pcRemind)
        ||StringUtils.isBlank(pcStatus)){
      return ;
    }
    
    DocumentManager docManager = DocumentManagerFactory.getInstance();
    DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_PAPERCARD);
    
    Document[] doc = docManager.find(docLib.getDocLibID(), " SYS_DELETEFLAG = 0 and SYS_DOCUMENTID = ? ", new Object[]{docid});
    doc[0].setDeleteFlag(0);
                      
    doc[0].set("pcReadway", pcReadway);
    doc[0].set("pcReadway_ID", manager.getCatElementId(pcReadway, "pcReadway_ID", docLib.getDocTypeID()));//下拉
    doc[0].set("pcRemind", pcRemind);
    doc[0].set("pcStatus", pcStatus);
    doc[0].set("pcStatus_ID", manager.getCatElementId(pcStatus, "pcStatus_ID", docLib.getDocTypeID()));//下拉
    doc[0].setFolderID(docLib.getFolderID());
    docManager.save(doc[0]);
          
    String viewName = "redirect:/e5workspace/after.do?DocLibID=" + docLib.getDocLibID()
        + "&DocIDs=" + docid + "&UUID=" + get(request, "UUID");
    model.put("@VIEWNAME@", viewName);
  }
  
  // 报卡日志详情页面初始化
    @SuppressWarnings("unchecked")
    private void pcardLog(HttpServletRequest request,
        HttpServletResponse response, @SuppressWarnings("rawtypes") Map model) throws E5Exception {

      JSONArray pcno = new JSONArray();
      DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_PAPERCARDLOG,
          InfoHelper.getTenantCode(request));
      long docID = getInt(request, "DocIDs", 0);
      
      String tenantcode = InfoHelper.getTenantCode(request);
      DocumentManager docManager = DocumentManagerFactory.getInstance();
      DocLib pcdocLib = InfoHelper.getLib(Constant.DOCTYPE_PAPERCARD,tenantcode);
      DocLib pcdocLibLog = InfoHelper.getLib(Constant.DOCTYPE_PAPERCARDLOG,tenantcode);
      String pccondition = " SYS_DELETEFLAG = 0 and SYS_DOCUMENTID = ? ";
      
      Document[] pcardLog = docManager.find(pcdocLibLog.getDocLibID(),pccondition, new Object[] {docID});
      String cardPrefix = pcardLog[0].getString("cardPrefix");
      String pccondition1 = " SYS_DELETEFLAG = 0 and pcNo like '"+cardPrefix+"%' ";
      Document[] pcards = docManager.find(pcdocLib.getDocLibID(),pccondition1, new Object[] {});
      for(int i=0;i<pcards.length;i++){
        pcno.add(pcards[i].get("pcNo"));
      }
      String VIEWNAME = "";
      VIEWNAME = "amuc/papercard/detail";  //订单详情页面
      
      model.put("DocLibID", docLib.getDocLibID());
      model.put("DocIDs", docID);
      model.put("FVID", docLib.getFolderID());
      model.put("UUID", get(request, "UUID"));
      model.put("PCNO", pcno.toString());
      model.put("@VIEWNAME@", VIEWNAME);// 跳转到jsp页面
    }
}
