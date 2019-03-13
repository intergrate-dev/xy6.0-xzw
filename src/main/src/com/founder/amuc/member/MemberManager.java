package com.founder.amuc.member;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.founder.e5.sys.SysConfigReader;
import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItem;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Component;

import com.founder.amuc.collection.CollectHelper;
import com.founder.amuc.commons.BaseHelper;
import com.founder.amuc.commons.Constant;
import com.founder.amuc.commons.InfoHelper;
import com.founder.amuc.invitecode.InviteCodeManager;
import com.founder.amuc.member.input.HttpClientUtil;
import com.founder.xy.jpublish.PublishHelper;
import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.DomHelper;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
//import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.DataType;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.doc.util.E5docHelper;
import com.founder.e5.dom.DocLib;
import com.founder.e5.dom.DocTypeField;
import com.founder.e5.dom.DocTypeReader;
import com.founder.e5.flow.FlowNode;
import com.founder.e5.sys.StorageDevice;
import com.founder.e5.sys.StorageDeviceManager;
import com.founder.e5.sys.StorageDeviceReader;
import com.founder.e5.workspace.app.form.LogHelper;

/** 
 * @created 2014年12月18日 下午3:53:05 
 * @author  leijj
 * 类说明 ： 会员操作类
 */
@Component
public class MemberManager {
	/*
	 数据入库规则说明：
	1.	手机号为空：存入主表和原始表（均为潜在会员）
	2.	根据手机号做查询，如果主会员表（ucMember）中不存在，则存储到主会员表（ucMember）和会员原始表（gMemberPublish、gMemberSSO、gMemberCallcenter、gMemberExcel、gMemberAct）中。
	3.	根据手机号做查询，如果主会员表（ucMember）中已存在
	a)	采集程序：会员主表（ucMember）不动，根据oriID查询原始会员表（gMemberPublish、gMemberSSO、gMemberCallcenter）中是否存在，存在则进行修改，否则新增一条数据并记录“会员-原始数据关联”。
	b)	接口API：目前只有SSO系统有会员信息保存接口。SSO系统会传递原始会员oriID，故与采集程序做相同处理即可。
	会员主表（ucMember）不动，根据oriID查询原始会员表（gMemberSSO）中是否存在，存在则进行修改，否则新增一条数据并记录“会员-原始数据关联”。
	c)	Excel/活动：会员主表（ucMember）不动，原始会员表（gMemberExcel、gMemberAct）新增一条数据并记录“会员-原始数据关联”。
	 */
	public Document createMember(String tenantCode, Document member) throws E5Exception{
		String mobile = member.getString("mMobile");
		if (StringUtils.isBlank(mobile)) {//手机号为空
			member.set("mPotential", 1);//潜在会员
			member.set("SYS_CURRENTNODE", 2);//潜在会员的操作
			createMember(tenantCode, member, member, false);
		} else {//手机号不为空
			MemberReader memberReader = new MemberReader();
			Document oldMember = memberReader.getMemberByMobile(tenantCode, mobile);
			if(oldMember == null){//手机号不存在
				return createMember(tenantCode, member, member, true);
			} else {
				//手机号存在,如果新传入会员积分>0，则需将积分加到主表会员上
				int score = member.getInt("mScore");
				boolean flag = false;//是否对主会员计算积分
				if(score > 0){
					oldMember.set("mScore", score + oldMember.getInt("mScore"));
					flag = true;
				}
				
				return createMember(tenantCode, oldMember, member, flag);
			}
		}
		return null;
	}
	
	/** 
	* @author  leijj 
	* 功能： 保存会员
	* 1. 新增会员或者修改会员积分
	* 2. 新增原始会员表
	* 3. 新增会员原始会员关系
	* @param tenantCode
	* @param data
	* @param oriData
	* @param calScore 
	*/ 
	public Document createMember(String tenantCode, Document data, Document oriData, boolean calScore){
		DocumentManager docManager = DocumentManagerFactory.getInstance();

		//最后保存到数据库
		DBSession conn = null;
		try {
			//事务处理
	    	conn = Context.getDBSession();
	    	conn.beginTransaction();
	    	boolean isNew = data.isNew();
	    	//为新会员或者需要更新积分时
	    	if(isNew || (!isNew && calScore)){
	    		docManager.save(data, conn);
	    	}
	    	
    		if(calScore){//计算积分
    			saveScore(tenantCode, data, conn);
    		}
    		
    		String mSource = oriData.getString("mSource");
			String str1 = data.getString("SYS_CURRENTUSERNAME");
			if(str1 == null){
				str1 = "";
			}
			
			if("线下Excel".equals(mSource)){
				writeFlowRecord(data, str1,isNew ? "Excel导入-添加" : "Excel导入-修改", "");
			}else{
				writeFlowRecord(data, str1,isNew ? "接口-添加" : "接口-修改", "");
			}
			conn.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
			ResourceMgr.rollbackQuietly(conn);
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
		return data;
	}
	/** 
	* @author  leijj 
	* 功能： 日志记录
	* @param doc
	* @param source
	* @param operation
	* @param detail 
	*/ 
	protected void writeFlowRecord(Document doc, String source, String operation, String detail) {
		LogHelper.writeLog(doc.getDocLibID(), doc.getDocID(), source, 0, operation, detail);
	}
	
	/** 
	* @author  leijj 
	* 功能： 在积分记录表里加一条积分记录
	* @param tenantCode
	* @param member
	* @param conn
	* @throws E5Exception 
	*/ 
	protected void saveScore(String tenantCode, Document member, DBSession conn) throws E5Exception {
		int score = member.getInt("mScore");
		if (score <= 0) return;

		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBERSCORE, tenantCode);
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = docManager.newDocument(docLib.getDocLibID(), InfoHelper.getID(docLib.getDocTypeID()));
		
		doc.setFolderID(docLib.getFolderID());
		doc.setDeleteFlag(0);
		doc.set("msMember_ID", member.getDocID());
		doc.set("msMember", member.get("mName"));
		doc.set("msTenantCode", "uc");
		doc.set("msTime", DateUtils.getTimestamp());
		doc.set("msEvent", "（原始数据采集）");
		doc.set("msSource", member.get("mSource"));
		doc.set("msSource_ID", member.get("mSource_ID"));
		
		doc.set("msScore", score);
		
		docManager.save(doc, conn);
	}
	/** 
	* @author  leijj 
	* 功能： 修改会员原始表，并记录修改详情（修改时主会员表不做处理）
	* @param tenantCode
	* @param data
	* @throws E5Exception 
	*/ 
	public void updateMember(String tenantCode, Document data) throws E5Exception{
		int oriLibID = InfoHelper.getOriMemberLibID(data.getString("mSource"));
		//替换findMember 为  findMemberOri，返回原始会员表的一个对象
		//根据原始id、原始表名、来源查询原始会员
		Document oriMember = CollectHelper.findMemberOri(oriLibID, data.getString("mOriID"), data.getString("mOriTable"), data.getInt("mSource_ID"));
		//最后保存到数据库
		DBSession conn = null;
		try {
			//保存一份到会员原始表
			if (oriMember == null)
				return;
			
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			Document oldMember = docManager.newDocument(oriMember, oriLibID, 1);
			
			overwriteOri(oriLibID, data, oriMember);
			
			String diffOri = LogHelper.whatChanged(oldMember, oriMember);
			
			docManager.save(oriMember, conn);
			String str3 = oriMember.getString("SYS_CURRENTUSERNAME");
			if(str3 == null){
				str3 = "";
			}
			writeFlowRecord(oriMember, str3, "修改", diffOri);
			//设置ucMember主表，修改标记为1,更新修改来源id标志位
			Document ucMember =  CollectHelper.findUcMember(null,data.getString("mOriID"), data.getString("mOriTable"), data.getInt("mSource_ID"));
			if(ucMember != null){
				String mUpdateSource_ID = ucMember.getString("mUpdateSource_ID");
				if(!StringUtils.isBlank(mUpdateSource_ID)){//不为空则需判断追加
					if(!mUpdateSource_ID.contains((""+data.getInt("mSource_ID")))){
						ucMember.set("mUpdateSource_ID", mUpdateSource_ID+","+data.getInt("mSource_ID"));
					}
				}else{
					ucMember.set("mUpdateSource_ID", data.getInt("mSource_ID")+",");
				}
				ucMember.setHaveAttach(1);
				docManager.save(ucMember, conn);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			ResourceMgr.rollbackQuietly(conn);
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
	}
	
	//删除
	public String delete(String tenantCode, String oriID, String oriTable, String source) throws E5Exception {
		int sourceID = InfoHelper.getMemberSourceCat(source);
		int oriLibID = InfoHelper.getOriMemberLibID(sourceID);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			//替换findMember 为  findMemberOri，返回原始会员表对象
			Document memberOri = CollectHelper.findMemberOri(oriLibID, oriID, oriTable, sourceID);
			if(memberOri == null){
				return null;//原始会员表里找不到相应的数据，删除失败，返回。
			}
			//保存主会员表、会员原始表的关系
			DocLib mrDocLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBERORI, tenantCode);
			Document[] memberRefs = docManager.find(mrDocLib.getDocLibID(), "mrMemberOriLibID=? and mrMemberOriID=?",
					new Object[]{memberOri.getDocLibID(), memberOri.getDocID()});
			//删除会员原始表里的原始记录
			if (memberOri != null) {
				memberOri.setDeleteFlag(1);
				docManager.save(memberOri);
				
			}
			if(memberRefs != null && memberRefs.length > 0){
				for(Document memberRef : memberRefs){
					memberRef.setDeleteFlag(1);
					docManager.save(memberRef);
				}
			}
			
			//写日志
			String str4 = memberOri.getString("SYS_CURRENTUSERNAME");
			if(str4 == null){
				str4 = "";
			}
			writeFlowRecord(memberOri, str4, "删除", "删除会员原始记录");
			//writeFlowRecord(memberOri, source, "删除", "删除会员原始记录");
		} catch (E5Exception e) {
			//删除时出错的话，相对不影响系统，因此只记录即可
			return "删除会员时发生错误。原ID=" + oriID + "。" + e;
		}
		return null;
	}
	
	//修改会员原始记录的值
	protected void overwriteOri(int oriLibID, Document newData, Document member) throws E5Exception {
		DocTypeReader docTypeReader = (DocTypeReader)Context.getBean(DocTypeReader.class);
		DocTypeField[] fields = docTypeReader.getFieldsExt(1);
		for (DocTypeField field : fields) {
			if (field.getColumnCode() != null && newData.get(field.getColumnCode()) != null){
				
				int dataType = Integer.valueOf(DataType.getTypeCode(field.getDataType()));
				if (dataType == 4){
					member.set(field.getColumnCode(), newData.getInt(field.getColumnCode()));
				}else if (dataType == 6){
					member.set(field.getColumnCode(), newData.getFloat(field.getColumnCode()));
				}else{
					member.set(field.getColumnCode(), newData.get(field.getColumnCode()));
				}
			}
		}
		member.set("SYS_LASTMODIFIED", newData.get("SYS_LASTMODIFIED"));
	}
	
	public Document updateSSOMember(String tenantCode, Document data){
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		//最后保存到数据库
		DBSession conn = null;
		try {
			//事务处理
	    	conn = Context.getDBSession();
	    	conn.beginTransaction();
	    	int oriLibID = InfoHelper.getOriMemberLibID(data.getString("mSource"));
	    	String tableName=InfoHelper.getDocTableName(oriLibID);
			
	    	 DocTypeField[] dirtyFields=docManager.getDirtyFields(data);
	    	 String idFieldName = E5docHelper.DOCUMENTID;
	    	 if(dirtyFields!=null&&dirtyFields.length>0){
	    		 StringBuffer sb = new StringBuffer();
	    		 sb.append("update ").append(tableName).append(" set ");
	    		 for (int i = 0; i < dirtyFields.length; i++) {
	    			DocTypeField field = dirtyFields[i];
	    			 String columnCode = field.getColumnCode();
	    			 Object columnValue = data.get(columnCode);
	    			 sb.append(columnCode).append("=").append("'"+columnValue+"'");
	    			 if (i < dirtyFields.length - 1) {
	    			  sb.append(",");
	    			 }
	    		 }
	    			sb.append(" where ").append(idFieldName).append("="+data.get(idFieldName));
	    			conn.executeDDL(sb.toString());
	    	 }
	    	//设置ucMember主表，修改标记为1
			Document ucMember =  CollectHelper.findUcMember(tenantCode,data.getString("mOriID"), data.getString("mOriTable"), data.getInt("mSource_ID"));
			if(ucMember != null){
				String mUpdateSource_ID = ucMember.getString("mUpdateSource_ID");
				if(!StringUtils.isBlank(mUpdateSource_ID)){//不为空则需判断追加
					if(!mUpdateSource_ID.contains((""+data.getInt("mSource_ID")))){
						ucMember.set("mUpdateSource_ID", mUpdateSource_ID+","+data.getInt("mSource_ID"));
					}
				}else{
					ucMember.set("mUpdateSource_ID", data.getInt("mSource_ID")+",");
				}
				ucMember.setHaveAttach(1);
				docManager.save(ucMember, conn);
			}
			conn.commitTransaction();
			
		} catch (Exception e) {
			e.printStackTrace();
			ResourceMgr.rollbackQuietly(conn);
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
		return data;
	}
	
	/**
	 * 南方日报：忘记密码
	 * @param mobile   手机号在amuc系统也是唯一标识
	 * @param password  密码
	 * @param TenantCode 租户code
	 * @return
	 * @throws E5Exception
	 */
	public String ForgetPassword(String mobile,String password,String TenantCode) throws E5Exception{
		
		Map<String, Object> maps = new HashMap<String, Object>();
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER, TenantCode);
		
		DBSession conn = null;
		try {
			//事务处理
	    	conn = Context.getDBSession();
	    	conn.beginTransaction();
	    	String [] column = {"mPassword","mStatus"};
	    	Document[] members = docManager.find(docLib.getDocLibID(), "SYS_DELETEFLAG = 0 and mMobile = ? ",new Object[]{mobile},column);
			if(members != null && members.length > 0){
				if(members[0].getInt("mStatus") == 1){
					members[0].set("mPassword", password);
					docManager.save(members[0],conn);
					conn.commitTransaction();
					maps.put("code", "1");
					maps.put("msg", "sucess");
				}else{
					maps.put("code", "0");
					maps.put("msg", "该会员被禁用");
				}
			}else{
				maps.put("code", "0");
				maps.put("msg", "fail");
			}
		} catch (Exception e) {
			e.printStackTrace();
			ResourceMgr.rollbackQuietly(conn);
		} finally {
			ResourceMgr.closeQuietly(conn);
		}	
		JSONObject result = JSONObject.fromObject(maps);
		return result.toString();
	}
	
	/**
	 * 南方日报和新华日报：修改资料
	 * @param uid amuc系统标识
	 * @return
	 * @throws E5Exception
	 */
	public String modifyMember(String uid,HashMap<String, String> memberData,String TenantCode) throws E5Exception{
		
		String deviceid = memberData.get("deviceid");
		String invitedcode = memberData.get("code");
		Map<String, Object> maps = new HashMap<String, Object>();
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER, TenantCode);
		
		//1.查询amuc系统中是否存在uid的会员，不存在返回0
		Document[] members = docManager.find(docLib.getDocLibID(), "SYS_DELETEFLAG = 0 and mStatus = 1 and SYS_DOCUMENTID = ? ",new Object[]{uid});
		//不存在id=uid的会员，返回失败标识0
		if(members == null || members.length == 0){
			maps.put("code", "0");
			maps.put("msg", "不存在id=uid的会员，返回失败标识0");
		}
		//2.存在的话修改会员信息
		if(members != null && members.length > 0){
			
			DBSession conn = null;
			try {
				conn = Context.getDBSession(); 
				conn.beginTransaction();
				if(!StringUtils.isBlank(invitedcode) && !StringUtils.isBlank(deviceid)){//邀请码和设备号均有值的情况下
					Document codeDoc = InviteCodeManager.isExistCode(invitedcode);
					if(codeDoc == null){
						maps.put("code", "0");
						maps.put("msg", "邀请码不存在");
					}else{
						//3.检查是否存在该会员的邀请码记录
						DocLib codelogDocLib = InfoHelper.getLib(Constant.DOCTYPE_INVITECODELOG);
						Document[] cldocs = docManager.find(codelogDocLib.getDocLibID(), "icImei = ? AND SYS_DELETEFLAG = 0 ", new Object[] { deviceid });
						if(cldocs != null && cldocs.length > 0){
							
							String code1 = cldocs[0].getString("icCode");
							if(code1.equals(invitedcode)){
								maps.put("code", "0");
								maps.put("msg", "该设备已经使用过该邀请码");
							}else{
								maps.put("code", "0");
								maps.put("msg", "该设备已经使用过其他邀请码");
							}
							
						}else{
							
							//4.组装修改app传来的会员信息
							modifyMember(members,memberData,docLib,conn);
							
							//5.新增邀请码记录
							Document codelogDoc = docManager.newDocument(codelogDocLib.getDocLibID(), InfoHelper.getID(codelogDocLib.getDocTypeID()));
							codelogDoc.setFolderID(codelogDocLib.getFolderID());
							codelogDoc.setDeleteFlag(0);
							codelogDoc.setLocked(false);
							Timestamp date = DateUtils.getTimestamp();
							codelogDoc.setCreated(date);
							codelogDoc.set("icCodeID", codeDoc.getDocID());
							codelogDoc.set("icCode", codeDoc.getString("icCode"));//邀请码
							codelogDoc.set("icImei", deviceid);//设备码
							codelogDoc.set("icInvitedID", uid);//会员id
							codelogDoc.set("icInvitedName", memberData.get("nickname"));
							docManager.save(codelogDoc,conn);
						
							//邀请码使用数量+1
							int icnum = codeDoc.getInt("icNum") + 1;
							codeDoc.set("icNum", icnum);
							docManager.save(codeDoc,conn);
							
							conn.commitTransaction();
							maps.put("code", "1");
							maps.put("msg", "修改资料成功");
						}
					}
				}else{//邀请码为空，不管设备号是否为空均只修改会员资料。 邀请码不为空，设备号为空，也只修改会员资料
					//新华app调用这个地方
					modifyMember(members,memberData,docLib,conn);
					conn.commitTransaction();
					maps.put("code", "1");
					maps.put("msg", "修改资料成功");
				}
			}catch (Exception e) {
				e.printStackTrace();
				maps.put("code", "0");
				maps.put("msg", "修改资料失败");
				ResourceMgr.closeQuietly(conn);
			} finally {
			     ResourceMgr.closeQuietly(conn);
			}
		}	
		
		JSONObject result = JSONObject.fromObject(maps);
		return result.toString();
	}
	
	/**
	 * 南方日报：修改会员资料增加邀请码字段
	 * @param members
	 * @param memberData
	 * @param docLib
	 * @param conn
	 * @throws ParseException
	 * @throws E5Exception
	 */
	private void modifyMember(Document[] members,
			HashMap<String, String> memberData, DocLib docLib,DBSession conn) throws E5Exception {
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		
		if(!StringUtils.isBlank(memberData.get("nickname")) ){
			members[0].set("mNickname",memberData.get("nickname"));
			members[0].set("mName",memberData.get("nickname"));
				
		}
		if(!StringUtils.isBlank(memberData.get("password")) ){
			members[0].set("mPassword", memberData.get("password"));	
		}
		if(!StringUtils.isBlank(memberData.get("email"))){
			members[0].set("mEmail", memberData.get("email"));	
		}else{
			members[0].set("mEmail","");
		}
		if(!StringUtils.isBlank(memberData.get("sex"))){
			members[0].set("mSex", Integer.parseInt(memberData.get("sex")));	
		}else{
			members[0].set("mSex", 0);	
		}
		if(!StringUtils.isBlank(memberData.get("birthday"))){//生日
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date date;
			try {
				date = dateFormat.parse(memberData.get("birthday"));
				members[0].set("mBirthday", date);	
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
		}
		if(!StringUtils.isBlank(memberData.get("region"))){//地区分类
			members[0].set("mRegion", memberData.get("region"));	
		}
		if(!StringUtils.isBlank(memberData.get("address"))){//通讯地址
			members[0].set("mAddress", memberData.get("address"));	
		}else{
			members[0].set("mAddress", "");	
		}
		
		//MemberHelper.dealID(members[0], docLib, "");//第三个参数废弃不用
		if(conn == null){
			docManager.save(members[0]);
		}else{
			docManager.save(members[0],conn);
		}
		
	}

	/**
	 * 南方日报：第三方账号登录接口
	 * @param type 第三方账号类型
	 * @param oid  第三方账号唯一标识
	 * @param name 第三方账号昵称
	 * @param tenantcode
	 * @return
	 * @throws Exception 
	 */
	public String loginByOther(int type,String oid,String name,String tenantcode) throws Exception{
		
		Map<String, Object> maps = new HashMap<String, Object>();
		String code = "";
		StringBuilder msg = new StringBuilder();
		
		if(StringUtils.isBlank(oid) ||  StringUtils.isBlank(name)){
		    maps.put("code", "0");
		    maps.put("msg", "第三方账号或昵称为空");
		    JSONObject jsonres = JSONObject.fromObject(maps);
			return jsonres.toString();
		}
		if(type > 3 || type <= 0){//1 新浪微博  2 腾讯QQ 3 微信
			maps.put("code", "0");
			maps.put("msg", "第三方账号类型不对");
			JSONObject jsonres = JSONObject.fromObject(maps);
			return jsonres.toString();
		}
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER, tenantcode);
		
		StringBuilder condition = new StringBuilder();
		switch (type) {//1 新浪微博  2 腾讯QQ 3 微信
			case 1: 
				condition.append(" mWeiboUid = ?  and SYS_DELETEFLAG = 0 ");
				break;
			case 2:
				condition.append(" mQq = ?  and SYS_DELETEFLAG = 0 ");
				break;
			case 3:
				condition.append(" mWechatId = ?  and SYS_DELETEFLAG = 0 ");
				break;
			default:
				break;
		}
		String [] columns = {"mName","mMobile","mEmail"};
		Document[] members = docManager.find(docLib.getDocLibID(), condition.toString(), new Object[]{oid},columns);
		if(members !=null && members.length > 0){
			//不做任何处理
			code =  ""+members[0].getDocID();
		}else{
			//调用sso同步接口
        	String provider = null;
        	String uname = null;
        	//第三方登录类型。1：新浪微博，2：腾讯QQ，3：微信。新浪微博：sina_weibo，腾讯QQ：tencent_QQ，微信：tencent_wechat
        	if(type == 1){
        		provider = "sina_weibo";
        		uname = "wb-"+oid;
        	}else if(type == 2){
        		provider = "tencent_QQ";
        		uname = "qq-"+oid;
        	}else if(type == 3){
        		provider = "tencent_wechat";
        		uname = "wx-"+oid;
        	}
        	List<NameValuePair> params = new ArrayList<NameValuePair>(); 
        	params.add(new BasicNameValuePair("username",uname));  //用户名
        	params.add(new BasicNameValuePair("nickname",name));  //昵称
        	params.add(new BasicNameValuePair("provider",provider));  //登录方式。
        	params.add(new BasicNameValuePair("oauthUid",oid));  //第三方用户标示
        	String res = new HttpClientUtil().callSsoAPI("/api/syn/loginByOther", params);
			JSONObject resJson = JSONObject.fromObject(res);
			if(!"1".equals(resJson.getString("code"))){
				maps.put("code", 0);
				maps.put("msg", "调用SSO同步接口失败");
				JSONObject jsonstr = JSONObject.fromObject(maps);
				return jsonstr.toString();
			}	
			
			//新建一条记录，并设置mName=name
			
			FlowNode flowNode = DomHelper.getFlowNode(docLib.getDocTypeID());
			Document  doc = CollectHelper.newData(docLib, 0, flowNode);
			
			if(type == 1){
				doc.set("mWeiboUid", oid);
				doc.set("mWeiboName", name);
			}else if(type == 2){
				doc.set("mQq", oid);
				doc.set("mQQname", name);
			}else if(type == 3){
				doc.set("mWechatId", oid);
				doc.set("mWechatName", name);
			}
			doc.set("mName", name);
			doc.set("mNickname", name);
			doc.set("mStatus", 1);
			doc.set("mSource", "线上API");
			doc.set("mSource_ID", InfoHelper.getMemberSourceCat("线上API"));
			
			docManager.save(doc);
			code = "" + doc.getDocID();
		}
		maps.put("code", code);
		maps.put("msg", msg.toString());
		JSONObject result = JSONObject.fromObject(maps);
		return result.toString();
	}
	
	/**
	 * 南方日报：登录接口
	 * @param mobile
	 * @param email
	 * @param password
	 * @return
	 * @throws E5Exception 
	 */
	public String Login4south (String mobile,String email,String password,String tenantcode) throws E5Exception{
		
		Map<String, Object> maps = new HashMap<String, Object>();
		String code = "";
		StringBuilder msg = new StringBuilder();
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER, tenantcode);
		
		StringBuilder condition = new StringBuilder();
		String param = "";
		if(!StringUtils.isBlank(mobile)){
			condition.append(" mMobile = ? and SYS_DELETEFLAG = 0 and mStatus = 1 ");
			param = mobile;
		}else{
			if(!StringUtils.isBlank(email)){
				condition.append(" mEmail = ? and SYS_DELETEFLAG = 0 and mStatus = 1 ");
				param = email;
			}else{
				return "false";
			}
		}
		String [] columns = {"mName","mMobile","mEmail","mSex","mHead","mBirthday","mRegion","mAddress","mPassword"};
		Document[] members = docManager.find(docLib.getDocLibID(), condition.toString(), new Object[]{param},columns);
		if(members != null && members.length > 0 ){
			//检查密码是否正确
			String mPassWord = members[0].getString("mPassword");
			//String MD5Str = DigestUtils.md5Hex(password);
			if(password.equals(mPassWord)){
				
				Map<String, Object> memberinfo = new HashMap<String, Object>();
				memberinfo.put("id", members[0].getString("SYS_DOCUMENTID"));
				memberinfo.put("name", members[0].getString("mName"));
				memberinfo.put("mobile", members[0].getString("mMobile"));
				memberinfo.put("email", members[0].getString("mEmail"));
				memberinfo.put("sex", members[0].getInt("mSex") == 1 ? "男": members[0].getInt("mSex") == 2 ? "女":"未知");
				memberinfo.put("head", members[0].getString("mHead"));
				memberinfo.put("birthday", members[0].getDate("mBirthday")==null?"":members[0].getDate("mBirthday").toString());
				memberinfo.put("region", members[0].getString("mRegion"));
				memberinfo.put("address", members[0].getString("mAddress"));
				
				maps.put("member", memberinfo);
				code = "1";
				msg.append("登录成功");
			}else{
				code = "0";
				msg.append("登录失败，密码不正确");
			}
		}else{
			code = "0";
			msg.append("登录失败，不存在该账户或被禁用");
		} 
		maps.put("code", code);
		maps.put("msg", msg.toString());
		JSONObject result = JSONObject.fromObject(maps);  
		return result.toString();
	}
	
	/**
	 * 南方日报：注册接口
	 * @param nickname
	 * @param mobile
	 * @param email
	 * @param password
	 * @param tenantcode
	 * @param invitedcode
	 * @param deviceid
	 * @return
	 * @throws E5Exception
	 */
	public String register(String nickname,String mobile,String email,String password,String tenantcode,String invitedcode,String deviceid) throws E5Exception{
		
		Map<String, Object> maps = new HashMap<String, Object>();
		String code = "";
		long uid = -1;
		StringBuilder msg = new StringBuilder();
		//1.判断邀请码是否存在系统中,不存在返回
		Document codeDoc = null;
		if(!StringUtils.isBlank(invitedcode)){
			codeDoc = InviteCodeManager.isExistCode(invitedcode);
			if(codeDoc == null ){
				maps.put("code", 0);
				maps.put("msg", "邀请码不存在");
				JSONObject jsonstr = JSONObject.fromObject(maps);
				return jsonstr.toString();
			}
		}
				
		//2.根据手机号判断是否系统中已存在拥有该手机号的会员
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER, tenantcode);
		String conditions = " mMobile = ? and SYS_DELETEFLAG = 0 ";
		String[] columns = {"mName"};
		Document[] members = docManager.find(docLib.getDocLibID(), conditions, new Object[]{mobile},columns);
		if(members != null && members.length > 0){
			msg.append("该手机号已注册");
			code =  "0";
		}else{
			DBSession conn = null;
			try {
				conn = Context.getDBSession(); 
				conn.beginTransaction();
				//新增一条会员记录
				FlowNode flowNode = DomHelper.getFlowNode(docLib.getDocTypeID());
				Document  doc = CollectHelper.newData(docLib, 0, flowNode);
				doc.set("mMobile", mobile);
				doc.set("mName", nickname);
				doc.set("mNickname", nickname);
				doc.set("mEmail", email);
				doc.set("mPassword", password);//DigestUtils.md5Hex(password)
				doc.set("mStatus", 1);
				doc.set("mSource", "线上API");
				doc.set("mSource_ID", InfoHelper.getMemberSourceCat("线上API"));
				docManager.save(doc,conn);
				
				//如果邀请码存在且设备码不为空,则添加邀请码使用记录、邀请码使用数量+1
				if(!StringUtils.isBlank(invitedcode) && !StringUtils.isBlank(deviceid)){
					//增加该会员的邀请码使用记录
					DocLib codelogDocLib = InfoHelper.getLib(Constant.DOCTYPE_INVITECODELOG);
					Document codelogDoc = docManager.newDocument(codelogDocLib.getDocLibID(), InfoHelper.getID(codelogDocLib.getDocTypeID()));
					codelogDoc.setFolderID(codelogDocLib.getFolderID());
					codelogDoc.setDeleteFlag(0);
					codelogDoc.setLocked(false);
					Timestamp date = DateUtils.getTimestamp();
					codelogDoc.setCreated(date);
					codelogDoc.set("icCodeID", codeDoc.getDocID());
					codelogDoc.set("icCode", invitedcode);
					codelogDoc.set("icInvitedID", doc.getDocID());
					codelogDoc.set("icInvitedName", nickname);
					codelogDoc.set("icImei", deviceid);
					docManager.save(codelogDoc,conn);
				
					//邀请码使用数量+1
					int icnum = codeDoc.getInt("icNum") + 1;
					codeDoc.set("icNum", icnum);
					docManager.save(codeDoc,conn);
				}
				conn.commitTransaction();
				msg.append("注册成功");
				code = "1";
				uid = doc.getDocID();
				
			}catch (Exception e) {
				ResourceMgr.rollbackQuietly(conn);
				e.printStackTrace();
	        } finally {
	        	ResourceMgr.closeQuietly(conn);
	        }
		}
		maps.put("code", code);
		maps.put("msg",msg.toString());
		maps.put("uid",uid);
		JSONObject result = JSONObject.fromObject(maps);
		return result.toString();
	}
	
	/**
	 * 新华日报：注册接口增强版
	 * @param nickname
	 * @param mobile
	 * @param email
	 * @param password
	 * @return
	 * @throws E5Exception 
	 */
	public String registerEx(String nickname,String mobile,String email,String password,String tenantcode) throws E5Exception{
		
		Map<String, Object> maps = new HashMap<String, Object>();
		String code = "";
		StringBuilder msg = new StringBuilder();
		
		//1.根据手机号判断是否系统中已存在拥有该手机号的会员
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER, tenantcode);
		String conditions = " mMobile = ? and SYS_DELETEFLAG = 0 ";
		String[] columns = {"mName"};
		Document[] members = docManager.find(docLib.getDocLibID(), conditions, new Object[]{mobile}, columns);
//		Document[] members = docManager.find(docLib.getDocLibID(), conditions, new Object[]{mobile});
		if(members != null && members.length > 0){
			msg.append("该手机号已注册");
			code =  "0";
		}else{
			//新建一条记录
			FlowNode flowNode = DomHelper.getFlowNode(docLib.getDocTypeID());
			Document  doc = CollectHelper.newData(docLib, 0, flowNode);
			doc.set("mMobile", mobile);
			doc.set("mName", nickname);
			doc.set("mNickname", nickname);
			doc.set("mEmail", email);
			doc.set("mPassword", password);//DigestUtils.md5Hex(password)
			doc.set("mStatus", 1);
			doc.set("mSource", "线上API");
			doc.set("mSource_ID", InfoHelper.getMemberSourceCat("线上API"));
			
			docManager.save(doc);
			msg.append("注册成功");
			code = "1";
			maps.put("nickname", nickname);
			maps.put("mobile", mobile);
			maps.put("email", email);
			maps.put("password", password);
			maps.put("uid", doc.getDocID());
		}
		
		maps.put("code", code);
		maps.put("msg",msg.toString());
		JSONObject result = JSONObject.fromObject(maps);
		return result.toString();
	}
	
	
	public String registerEx(String ssoid,String username, String nickname,String mobile,String email,String password,String tenantcode) throws E5Exception{
		
		Map<String, Object> maps = new HashMap<String, Object>();
		String code = "";
		StringBuilder msg = new StringBuilder();
		
		//1.根据手机号判断是否系统中已存在拥有该手机号的会员
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER, tenantcode);
		String conditions = " mMobile = ? and SYS_DELETEFLAG = 0 ";
		String[] columns = {"mName"};
		Document[] members = docManager.find(docLib.getDocLibID(), conditions, new Object[]{mobile}, columns);
//		Document[] members = docManager.find(docLib.getDocLibID(), conditions, new Object[]{mobile});
		if(members != null && members.length > 0){
			msg.append("该手机号已注册");
			code =  "0";
		}else{
			//新建一条记录
			FlowNode flowNode = DomHelper.getFlowNode(docLib.getDocTypeID());
			Document  doc = CollectHelper.newData(docLib, 0, flowNode);
			doc.set("uid_sso", ssoid);
			doc.set("mMobile", mobile);
			doc.set("mName", username);
			doc.set("mNickname", nickname);
			doc.set("mEmail", email);
			doc.set("mPassword", password);//DigestUtils.md5Hex(password)
			doc.set("mStatus", 1);
			doc.set("mSource", "线上API");
			doc.set("mSource_ID", InfoHelper.getMemberSourceCat("线上API"));
			
			docManager.save(doc);
			msg.append("注册成功");
			code = "1";
			maps.put("nickname", nickname);
			maps.put("mobile", mobile);
			maps.put("email", email);
			maps.put("password", password);
			maps.put("uid", doc.getDocID());
		}
		maps.put("code", code);
		maps.put("msg",msg.toString());
		JSONObject result = JSONObject.fromObject(maps);
		return result.toString();
	}
	
	/**
	 * 通过sso接口调用的会员注册
	 * @param m_siteID
	 * @param ssoid
	 * @param username
	 * @param nickname
	 * @param mobile
	 * @param email
	 * @param password
	 * @param tenantcode
	 * @return
	 * @throws E5Exception
	 */
	public String registerEx(String m_siteID, String ssoid, String username, String nickname, String mobile,
			String email, String password, String headImg, String tenantcode) throws E5Exception {

		Map<String, Object> maps = new HashMap<String, Object>();
		String code = "";
		StringBuilder msg = new StringBuilder();
		// 1.根据手机号判断是否系统中已存在拥有该手机号的会员
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER, tenantcode);
		String conditions = null;
		String value = null;
		String[] columns = { "mName" };
		if (!StringUtils.isBlank(mobile)) {
			conditions = " mMobile = ? and SYS_DELETEFLAG = 0 ";
			value = mobile;
		} else {
			if (!StringUtils.isBlank(email)) {
				conditions = " mEmail = ? and SYS_DELETEFLAG = 0 ";
				value = email;
			}
		}
		Document[] members = null;
		if (!StringUtils.isBlank(conditions)) {
			members = docManager.find(docLib.getDocLibID(), conditions, new Object[] {value}, columns);
		}
		// Document[] members = docManager.find(docLib.getDocLibID(),
		// conditions, new Object[]{mobile});
		if (members != null && members.length > 0) {
			msg.append("该手机号或邮箱已注册");
			code = "0";
		} else {
			// 新建一条记录
			FlowNode flowNode = DomHelper.getFlowNode(docLib.getDocTypeID());
			Document doc = CollectHelper.newData(docLib, 0, flowNode);
			doc.set("uid_sso", ssoid);
			doc.set("mMobile", mobile);
			doc.set("mName", username);
			doc.set("mNickname", nickname);
			doc.set("mEmail", email);
			doc.set("mPassword", password);// DigestUtils.md5Hex(password)
			doc.set("mStatus", 1);
			doc.set("mSource", "线上API");
			doc.set("mSource_ID", InfoHelper.getMemberSourceCat("线上API"));
			doc.set("m_siteID", m_siteID);
			doc.set("mHead", headImg);

			docManager.save(doc);
			msg.append("注册成功");
			code = "1";
			maps.put("nickname", nickname);
			maps.put("mobile", mobile);
			maps.put("email", email);
			maps.put("password", password);
			maps.put("uid", doc.getDocID());
			maps.put("head", headImg);
		}
		maps.put("code", code);
		maps.put("msg", msg.toString());
		JSONObject result = JSONObject.fromObject(maps);
		return result.toString();
	}
	/**
	 * 南方日报：上传头像接口
	 * @param uid 会员在amuc系统的标识
	 * @param file 上传的图片文件
	 * @param tenantcode  租户代号
	 * @return
	 * @throws E5Exception
	 * @throws IOException
	 */
	public String uploadPortrait(String uid,FileItem file,String tenantcode) throws E5Exception, IOException {
		Map<String, Object> maps = new HashMap<String, Object>();
		String code = "";
		String msg = "";
		//StringBuilder msg = new StringBuilder();

		//先查询是否有该会员,有的话,上传头像图片，然后再更新会员表里的相应头像的url
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER, tenantcode);
		Document[] members = docManager.find(docLib.getDocLibID(), " SYS_DELETEFLAG = 0 and SYS_DOCUMENTID = ? ", new Object[]{uid});
		String headImg = "";
		if (members != null && members.length > 0) {
			long memberId = members[0].getDocID();
			HashMap<String, String> resultUp = uploadPortraitImp(file, memberId);
			System.out.println("--------------- MemberManager uploadPortrait, resultUp: " + resultUp);
			if ("sucess".equals(resultUp.get("code"))) {
				headImg = resultUp.get("httppath");
				members[0].set("mHead", headImg);
				docManager.save(members[0]);
				code = "1";
				//msg.append(resultUp.get("httppath"));
				//msg.append("上传成功");
				msg = "上传成功";
			} else {
				code = "0";
				//msg.append(resultUp.get("error"));
				msg = resultUp.get("error");
			}
		} else {
			code = "0";
			//msg.append("不存在id=" + uid + "的会员");
			msg = "不存在id=" + uid + "的会员";
		}

		maps.put("headImg", headImg);
		maps.put("code", code);
		maps.put("msg", msg.toString());
		//JSONArray jsonarray = JSONArray.fromObject(maps);
		JSONObject result = JSONObject.fromObject(maps);
		return result.toString();

	}
	
	/**
	 * 上传头像实现方法
	 * @param file
	 * @param memberId amuc系统的唯一标识
	 * @return
	 * @throws E5Exception
	 */
	public HashMap<String, String> uploadPortraitImp(FileItem file,long memberId) throws E5Exception{
		System.out.println("--------------- MemberManager uploadPortraitImp, memberId: " + memberId + ", file: " + file.getFieldName());
		
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
	        //判断图片后缀
	        if(matcher.find()){
	        	SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmm",Locale.CHINESE);
				String newPrefix = formatter.format(new Date());
				String fileExt = fileName.substring(fileName.lastIndexOf(".")+1).toLowerCase();
				String resfilename = "portrait" + memberId + "at" + newPrefix + DateUtils.getTimestamp().getTime() + "." + fileExt;
	 			
				deviceManager.write(device, resfilename , in);
							
				//增加trans
				String ntfsDevicePath = device.getNtfsDevicePath();
				String webroot = BaseHelper.getConfig("翔宇CMS", "发布服务", "发布根目录");
				System.out.println("--------------- MemberManager uploadPortraitImp, webroot: " + webroot);
				if(ntfsDevicePath != null){
					ntfsDevicePath = ntfsDevicePath.substring(webroot.length() + 1);
					ntfsDevicePath = org.apache.commons.lang.StringUtils
							.strip(ntfsDevicePath, "/")
							.replace("/", "~");
					System.out.println("--------------- MemberManager uploadPortraitImp, ntfsDevicePath: " + ntfsDevicePath +
							", resfilename: " + resfilename + ", transPath: " + PublishHelper.getTransPath());
					PublishHelper.writePath(ntfsDevicePath + "~" + resfilename,PublishHelper.getTransPath());
				}

				String imgHttppath = device.getHttpDeviceURL()+"/"+resfilename;
				String imgFtppath = device.getFtpDeviceURL()+"/"+resfilename;
				resu.put("code", "sucess");
				resu.put("httppath", imgHttppath);
				resu.put("ftppath", imgFtppath);
				System.out.println("--------------- MemberManager PublishHelper.writePath complete, imgFtppath: " + imgFtppath);
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
	
	/**
	 * 获取头像文件
	 * @param id
	 * @param tenantcode
	 * @return
	 * @throws E5Exception 
	 */
	public String getPortrait(String id , String tenantcode) throws E5Exception{
		
		Map<String, Object> maps = new HashMap<String, Object>();
		String code = "";
		StringBuilder msg = new StringBuilder();
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER, tenantcode);
		String [] columns = {"mHead"};
		Document[] members = docManager.find(docLib.getDocLibID(), " SYS_DELETEFLAG = 0 and SYS_DOCUMENTID = ? ", new Object[]{id},columns);
		if(members != null && members.length > 0){
			String headpath = members[0].getString("mHead");
			code = "1";
			msg.append(headpath);
		}else{
			code = "0";
			msg.append("不存在该会员");
		}
		
		maps.put("code", code);
		maps.put("msg", msg.toString());
		JSONObject result = JSONObject.fromObject(maps);
		return result.toString();
	}
	/**
	 * 获取用户信息接口
	 * @param ssoid
	 * @param tenantcode
	 * @return
	 * @throws E5Exception 
	 */
	public String getUserMessage(String ssoid,String tenantcode) throws E5Exception{
		
		Map<String, Object> maps = new HashMap<String, Object>();
		String code = "";
		StringBuilder msg = new StringBuilder();
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER, tenantcode);
		
		Document[] members = docManager.find(docLib.getDocLibID(), " SYS_DELETEFLAG = 0 and uid_sso = ? ", new Object[]{ssoid});
		if(members != null && members.length > 0 ){
		
				maps.put("uid", members[0].getString("SYS_DOCUMENTID"));
				maps.put("mScore", members[0].getString("mScore"));
				maps.put("username", members[0].getString("mName"));
				maps.put("nickname", members[0].getString("mNickname"));
				maps.put("phone", members[0].getString("mMobile"));
				maps.put("sex", members[0].getString("mSex"));
				maps.put("birthday", members[0].getString("mBirthday"));
				maps.put("mHead", members[0].getString("mHead"));
				maps.put("address", members[0].getString("mAddress"));
				code = "1";
				msg.append("获取成功");
		}else{
			code = "0";
			msg.append("该用户不存在");
		} 
		maps.put("code", code);
		maps.put("msg", msg.toString());
		JSONObject result = JSONObject.fromObject(maps);  
		return result.toString();
	}

	public String setDefaultImg() throws E5Exception {
		SysConfigReader configReader = (SysConfigReader) Context.getBean(SysConfigReader.class);
		String rootPath = configReader.get(1, "会员中心", "根地址");
		return rootPath + "mempic/default/head_img.jpg";
	}

	public Boolean isThirdAccount(Document mDoc) {
		String mName = mDoc.getString("mName");
		System.out.println("================ MemberManager isThirdAccount, member'mName: " + mName +
				", provider: " + mDoc.getString("provider"));
		return !StringUtils.isBlank(mDoc.getString("provider")) || (!StringUtils.isBlank(mName) && (mName.indexOf("gp-") != -1 || mName.indexOf("fb-") != -1));
	}

	public String registerExByOther(String m_siteID, String ssoid, String username, String nickname, String mobile, String email,
									String password, String headImg, String provider, String oauthid, String tenantcode) throws E5Exception {
		System.out.println("================ MemberManager registerExByOther  enry ,provider: " + provider + ", oauthid: " + oauthid + ", nickname: " + nickname);
		Map<String, Object> maps = new HashMap<String, Object>();
		// 1.根据手机号判断是否系统中已存在拥有该手机号的会员
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER, tenantcode);
		String[] columns = { "mName" };
		String conditions = " provider = ? and oauthid = ? and SYS_DELETEFLAG = 0 ";
		//String conditions = " provider = ? and oauth_ID = ? and SYS_DELETEFLAG = 0 ";

		Document doc = null;
		Document[] members = docManager.find(docLib.getDocLibID(), conditions, new Object[] {provider, oauthid}, columns);
		if (members == null || members.length == 0) {
			// 新建一条记录
			FlowNode flowNode = DomHelper.getFlowNode(docLib.getDocTypeID());
			doc = CollectHelper.newData(docLib, 0, flowNode);
			doc.set("uid_sso", ssoid);
			doc.set("mMobile", mobile);
			doc.set("mName", username);
			doc.set("mNickname", nickname);
			doc.set("mEmail", email);
			doc.set("mPassword", password);// DigestUtils.md5Hex(password)
			doc.set("mStatus", 1);
			doc.set("mSource", "线上API");
			doc.set("mSource_ID", InfoHelper.getMemberSourceCat("线上API"));
			doc.set("m_siteID", m_siteID);
			doc.set("mHead", headImg);
			doc.set("provider", provider);
			doc.set("oauthid", oauthid);
			//doc.set("oauth_ID", oauthid);

			docManager.save(doc);
			System.out.println("================ MemberManager registerExByOther  save doc");
			/*msg.append("注册成功");
			code = "1";
			maps.put("nickname", nickname);
			maps.put("mobile", mobile);
			maps.put("email", email);
			maps.put("password", password);
			maps.put("uid", doc.getDocID());
			maps.put("head", headImg);
			maps.put("provider", provider);
			maps.put("oauthid", oauthid);*/
		} else {
			doc = members[0];
			System.out.println("================ 改第三方账户已注册，uid：" + doc.getDocID());
		}
		maps.put("nickname", nickname);
		maps.put("mobile", mobile);
		maps.put("email", email);
		maps.put("password", password);
		maps.put("uid", doc.getDocID());
		maps.put("head", headImg);
		maps.put("provider", provider);
		maps.put("oauthid", oauthid);

		maps.put("code", "1");
		maps.put("msg", "注册成功");
		JSONObject result = JSONObject.fromObject(maps);
		System.out.println("================ MemberManager registerExByOther  result: " + result.toString());
		return result.toString();
	}
}