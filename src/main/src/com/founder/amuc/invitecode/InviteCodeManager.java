package com.founder.amuc.invitecode;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.LibHelper;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.founder.amuc.commons.Constant;
import com.founder.amuc.commons.InfoHelper;
import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.context.EUID;
import com.founder.e5.db.DBSession;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
/**
 * @author fanjc
 * 邀请码操作类
 */
@Component
public class InviteCodeManager {

	/**
	 * 检查邀请码是否存在
	 * @param invitedcode
	 * @return
	 * @throws E5Exception 
	 */
	public static Document isExistCode(String invitedcode) throws E5Exception {
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_INVITECODE, null);
		String icodegql = "SYS_DELETEFLAG = 0 and icCode = ? ";
		Document[] icodedocs = docManager.find(docLib.getDocLibID(), icodegql,new Object[]{invitedcode});
		if(icodedocs != null && icodedocs.length > 0){
			return icodedocs[0];
		}
		return null;
	}
	
	public List<String> getCodeList(String icLevel1Index,String icLevel2Index,int icGenerateNum,String icIdex){
		
		List<String> CodeList = new ArrayList<String>();
		if(icGenerateNum == 1){
			CodeList.add(icLevel1Index+icLevel2Index);
		}else{
			String temp = icIdex;
			for (int i = 0 ;i < icGenerateNum ;i++) {
				CodeList.add(icLevel1Index + temp);
				temp = String.valueOf(increStrsys(temp));
			}
		}
		return CodeList;
	}
	public List<String> getCodeList3(String icLevel1Index,String icLevel2Index,String icLevel3Index,int icGenerateNum,String icIdex){
		List<String> CodeList = new ArrayList<String>();
		if(icGenerateNum == 1){
			CodeList.add(icLevel1Index+icLevel2Index+icLevel3Index);
		}else{
			String temp = icIdex;
			for (int i = 0 ;i < icGenerateNum ;i++) {
				CodeList.add(icLevel1Index + icLevel2Index + temp);
				temp = String.valueOf(increStrsys(temp));
			}
		}
		return CodeList;
	}
	public List<String> getCodeList4(String icLevel1Index,String icLevel2Index,String icLevel3Index,String icLevel4Index,int icGenerateNum,String icIdex){
		List<String> CodeList = new ArrayList<String>();
		if(icGenerateNum == 1){
			CodeList.add(icLevel1Index+icLevel2Index+icLevel3Index+icLevel4Index);
		}else{
			String temp = icIdex;
			for (int i = 0 ;i < icGenerateNum ;i++) {
				CodeList.add(icLevel1Index + icLevel2Index + icLevel3Index + temp);
				temp = String.valueOf(increStrsys(temp));
			}
		}
		return CodeList;
	}
	/**
	 * 根据uid、aid判断是否使用过邀请码
	 * @param code
	 * @param aid
	 * @return
	 * @throws E5Exception
	 */
	public JSONObject IsUsedCode(String uid, String code,String aid) throws E5Exception{
		
		JSONObject result = new JSONObject();
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib memdocLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER, null);
		DocLib codedocLib = InfoHelper.getLib(Constant.DOCTYPE_INVITECODE, null);
		DocLib codelogdocLib = InfoHelper.getLib(Constant.DOCTYPE_INVITECODELOG, null);
		//1.根据uid检查会员是否存在
		String membersql = "SYS_DELETEFLAG = 0 and SYS_DOCUMENTID = ? ";
		Document[] docs = docManager.find(memdocLib.getDocLibID(), membersql, new Object[]{uid});
		if(docs != null && docs.length > 0){
			//2.根据code检查邀请码是否存在
			String icodegql = "SYS_DELETEFLAG = 0 and icCode = ? and icType = ? ";
			int icType =  code.trim().length() == 6 ? 0 : 1;
			String codeName = (icType == 0 ? "邀请码" : "抽奖码");
			Document[] icodedocs = docManager.find(codedocLib.getDocLibID(), icodegql,new Object[]{ code,icType});
			if(icodedocs != null && icodedocs.length > 0){
				//3.检查该会员是否使用过邀请码参加过该活动
				String icodelogsql = "SYS_DELETEFLAG = 0 and icInvitedID = ? and icGivingID = ? ";
				Document[] iclogdocs = docManager.find(codelogdocLib.getDocLibID(), icodelogsql,new Object[]{uid,aid});
				if(iclogdocs != null && iclogdocs.length > 0){
					result.put("code", "1001");
					result.put("msg", "本次活动抽奖机会已用完。别灰心！下次活动再来过！");//该用户已使用过抽奖码参与抽奖活动:
				}else{
					result.put("code", "1002");
					result.put("msg", "该用户没有使用过"+codeName+"参与抽奖活动");
					
					//增加一条该用户该活动的邀请码使用记录
					insertInviteCodeLog(icodedocs[0].getDocID(), code, uid, docs[0].getString("mName"), aid);
				}
			}else{
				result.put("code", "1003");
				result.put("msg", codeName+"不存在");
			}
		
		}else{
			result.put("code", "1004");
			result.put("msg", "您还没注册南方日报APP或者没有绑定手机号信息！");
		}
		
		return result;
	}
	
	/**
	 * 增加邀请码使用日志记录，并更新邀请码使用数量
	 * @throws E5Exception 
	 */
	public void insertInviteCodeLog(Long icCodeID,String icCode,String memberID,String mName,String aid) throws E5Exception{
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib codedocLib = InfoHelper.getLib(Constant.DOCTYPE_INVITECODE, null);
		DocLib codelogdocLib = InfoHelper.getLib(Constant.DOCTYPE_INVITECODELOG, null);
		
	
		DBSession conn = null;
		try {
			conn = Context.getDBSession(); 
			conn.beginTransaction();
			
			Document  iclogdoc = InviteCodeManager.newData(codelogdocLib, 0);
			iclogdoc.set("icCodeID", icCodeID);
			iclogdoc.set("icCode", icCode);
			iclogdoc.set("icInvitedID", memberID);
			iclogdoc.set("icInvitedName", mName);
			iclogdoc.set("icGivingID", aid);
			docManager.save(iclogdoc);
			
			String icodegql = "SYS_DELETEFLAG = 0 and icCode = ? and icType = ? ";
			int icType =  icCode.trim().length() == 6 ? 0 : 1;
			Document[] icodedocs = docManager.find(codedocLib.getDocLibID(), icodegql,new Object[]{ icCode,icType});
			if(icodedocs != null && icodedocs.length > 0){
				int num = icodedocs[0].getInt("icNum") + 1;
				icodedocs[0].set("icNum", num);
				docManager.save(icodedocs[0]);
			}
			conn.commitTransaction();
		}catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			e.printStackTrace();
        } finally {
        	ResourceMgr.closeQuietly(conn);
        }
	}
	
	/**
	 * 使用邀请码
	 * @param uid 用户在amuc系统里的标识
	 * @param inviteCode 邀请码
	 * @param TenantCode 租户code
	 * @return
	 * @throws E5Exception 
	 */
	public String useInviteCode(String uid, String inviteCode,String TenantCode) throws E5Exception{
		
		JSONObject result = new JSONObject();
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib icdocLib = InfoHelper.getLib(Constant.DOCTYPE_INVITECODE, TenantCode);
		DocLib iclogdocLib = InfoHelper.getLib(Constant.DOCTYPE_INVITECODELOG, TenantCode);
		DocLib memberdocLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER, TenantCode);
		
		Document[] memdocs = docManager.find(memberdocLib.getDocLibID(), "SYS_DELETEFLAG = 0 and SYS_DOCUMENTID = ? and mStatus = 1 ", new Object[]{uid});
		if(memdocs != null && memdocs.length > 0){
			//查找没有过期的邀请码
			Document[] icdocs = docManager.find(icdocLib.getDocLibID(), "SYS_DELETEFLAG = 0 and icExpiredDay > 0 and icCode = ?", new Object[]{inviteCode});
			if(icdocs != null && icdocs.length > 0){
				//查找当前用户是否使用过的记录
				Document[] iclogdocs = docManager.find(iclogdocLib.getDocLibID(), "SYS_DELETEFLAG = 0 and icCodeID = ? and icInvitedID = ? ",  new Object[]{ icdocs[0].getDocID(), uid});
				if(iclogdocs != null && iclogdocs.length > 0){
					
					result.put("code", "0");
					result.put("msg", "该用户已使用此邀请码");
					
				}else{
					/*//邀请码状态修改
					icdocs[0].set("icStatus", 1);
					docManager.save(icdocs[0]);*/
					
					//增加一条邀请码使用记录（邀请码使用记录表）
					Document  iclogdoc = InviteCodeManager.newData(iclogdocLib, 0);
					iclogdoc.set("icCodeID", icdocs[0].getDocID());
					iclogdoc.set("icCode", inviteCode);
					iclogdoc.set("icInvitedID", uid);
					iclogdoc.set("icInvitedName", memdocs[0].getString("mName"));
					docManager.save(iclogdoc);
					
					//组装返回信息
					result.put("code", "1");
					result.put("msg", "正常使用邀请码");
				}
			}else{
				result.put("code", "0");
				result.put("msg", "没有该邀请码");
			}
			/*
			 * //如果该邀请码有设置积分规则
			int icBonusScore = icdocs[0].getInt("icBonusScore");
			if( icBonusScore > 0){
				memdocs[0].set("mScore", icBonusScore + memdocs[0].getInt("mScore") );
				docManager.save(memdocs[0]);
			}*/
			
		}else{
			result.put("code", "1");
			result.put("msg", "uid:"+uid+"的用户在系统中不存在");
		}
		
		return result.toString();
	}
	/**
	 * 单独为APP生成邀请码
	 * @param uid 邀请人用户在amuc系统的标识
	 * @return
	 * @throws E5Exception 
	 */
	public String GenerateIcCodeForSingle(String uid, int length, String tenantcode) throws E5Exception {
		
		JSONObject result = new JSONObject();
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib IcdocLib = InfoHelper.getLib(Constant.DOCTYPE_INVITECODE, tenantcode);
		DocLib MdocLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER, tenantcode);
		
		//1.判断该uid 是否在系统会员表里存在
		Document memberdoc = docManager.get(MdocLib.getDocLibID(), Integer.parseInt(uid));
		if(memberdoc == null){
			result.put("code", "0");
			result.put("msg", "系统中不存在uid:"+uid+"的会员");
			
		}else{
			//2.判断该uid是否已经生成了邀请码，如果有直接返回。
			Document[] icodeDocs = docManager.find(IcdocLib.getDocLibID(), "SYS_DELETEFLAG=0 and icInviterID=?",  new Object[]{uid});
			if(icodeDocs != null  && icodeDocs.length > 0){
				
				result.put("code", icodeDocs[0].getString("icCode"));
				result.put("msg", "系统中存在,获取邀请码成功");
			}else{//3.如果该uid没有生成过邀请码,创建新的邀请码
				DBSession conn = null;
				try {
					conn = Context.getDBSession(); 
					conn.beginTransaction();
					long icdocid = InfoHelper.getID(IcdocLib.getDocTypeID());
					
					String invitedcode = "SYS" + icdocid + GenerateCode(length);
					String iccode = invitedcode.substring(0,15);//截取邀请码为15位长度
					
					Document doc = docManager.newDocument(IcdocLib.getDocLibID(), icdocid);
					doc.setFolderID(IcdocLib.getFolderID());
					doc.setDeleteFlag(0);
					doc.setLocked(false);
					Timestamp date = DateUtils.getTimestamp();
					doc.setCreated(date);
					
					doc.set("icCode", iccode);
					//doc.set("icBonusScore", icBonusScore);
					doc.set("icExpiredDay", 7);
					doc.set("icStatus", 0);
					doc.set("icInviterID", memberdoc.getDocID());//邀请人编号
					doc.set("icInviterName", memberdoc.get("mName"));//邀请人名称
					doc.set("icType", 1);
					
					docManager.save(doc,conn);
					conn.commitTransaction();
					result.put("code", iccode);
					result.put("msg", "获取邀请码成功");
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
	 * 系统后台生成邀请码
	 * @param icNum 数量 
	 * @param icRule 生成规则
	 * @return
	 */
	public String GenerateIcCodeForGroup(String icRule , int icNum ,long docid)  {

		
		String Prefix = icRule.replaceAll("#", "");//邀请码前缀
		
		int length = GetSameCharNum(icRule, "#");//随机位数
		String code = GenerateCode(length);//生成随机邀请码
		String result = Prefix + docid + code ; 
		
		//对邀请码进行统一长度处理
		int len = Prefix.length() + length;
		String gcode = result.substring(0,len);
		
		return gcode;
	}
	/**
	 * 根据现场需求修改后的系统生成邀请码
	 * 系统后台生成邀请码
	 * @param icRule 生成规则
	 * @param num 邀请码数量
	 * @return
	 * @throws Exception 
	 */
	public List<String> SysCreateIcCode(String icRule,int num, DocLib IcDocLib) throws Exception  {
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib IcIddocLib = InfoHelper.getLib(Constant.DOCTYPE_INVITECODEID,null);//邀请码标识文档类型
		
		String Prefix = icRule.replaceAll("#", "");//邀请码前缀
		List<String> listcode = new ArrayList<String>();
		
		//取出邀请码标识（该标识每生成一个都会自增长）
		Document[] iciddoc = docManager.find(IcIddocLib.getDocLibID(), "SYS_DELETEFLAG=0 and icCodeIdentiType = 'sys' ",  null);
	
		if(iciddoc!=null && iciddoc.length > 0){
			String precode = iciddoc[0].getString("icCodeIdenti");
			listcode.add(Prefix + precode);
			for(int i = 0 ; i < num-1; i++){
				String tempc = String.valueOf(increStr(precode));
				precode = tempc;
				listcode.add(Prefix + tempc);
			}
			//更新邀请码标识
			iciddoc[0].set("icCodeIdenti", String.valueOf(increStr(precode)));
			docManager.save(iciddoc[0]);
		}
		
		return listcode;
		
	}
	/**
	 * 南方现场修改
	 * app调用接口生成邀请码 方法2
	 * 2015-09-17 
	 * @return
	 */
	public String AppCreateIcCode(String uid, String tenantcode) throws E5Exception {
		
		JSONObject result = new JSONObject();
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib IcdocLib = InfoHelper.getLib(Constant.DOCTYPE_INVITECODE,tenantcode);//邀请码文档类型
		DocLib MdocLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER, tenantcode);//会员文档类型
		DocLib IcIddocLib = InfoHelper.getLib(Constant.DOCTYPE_INVITECODEID,tenantcode);//邀请码标识文档类型
		
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
						String iccode = this.geneRanCode(iciddoc[0], 7);

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
	 * 生成邀请码 
	 * @param length #的个数
	 * @return
	 */
	public String GenerateCode(int length) {
		StringBuilder val = new StringBuilder("");
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
			// 输出字母还是数字
			if ("char".equalsIgnoreCase(charOrNum)) {
				// 输出是大写字母还是小写字母
				int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
				val.append((char) (random.nextInt(26) + temp));
			} else if ("num".equalsIgnoreCase(charOrNum)) {
				val.append(String.valueOf(random.nextInt(10)));
			}
		}
		return val.toString();
	}

	/**
	 * 获取字符串中同一个字符(#)出现的个数
	 * @param str 原始字符串
	 * @param target  目标元素
	 * @return
	 */
	private int GetSameCharNum(String str, String target) {

		// 使用平台默认的字符集将此 String 解码为字节序列，并将结果存储到一个新的字节数组中。
		byte[] temp = str.getBytes();
		int count = 0;
		// 遍历数组的每一个元素，也就是字符串中的每一个字母
		for (int i = 0; i < temp.length; i++) {
			if (temp[i] == ('#')) {
				count++;// 计数器加一
			}
		}
		return count;
	}
	
	public static Document newData(DocLib docLib, long docID) throws E5Exception {
		
		if (docID == 0){
			docID = InfoHelper.getID(docLib.getDocTypeID());
		}
		if(docID==1){
			docID=EUID.getID(docLib.getDocLibTable());
		}
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		
		Document doc = docManager.newDocument(docLib.getDocLibID(), docID);
		doc.setFolderID(docLib.getFolderID());
		doc.setDeleteFlag(0);
		doc.setLocked(false);
		
		Timestamp date = DateUtils.getTimestamp();
		doc.setCreated(date);
		
		return doc;
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

	public String geneRanCode(Document document, int length) throws E5Exception {
		StringBuffer buffer = new StringBuffer();
		for(int k=0; k<length; k++) {
			buffer.append(new Random().nextInt(10));
		}
		if(this.codeIsRepeate(document, buffer.toString())){
			geneRanCode(document, 7);
		}
		return buffer.toString();
	}

	private boolean codeIsRepeate(Document document, String code) throws E5Exception {
		int docTypeID = DocTypes.MEMBERINVITECODE.typeID();
		DocLib docLib = LibHelper.getLib(docTypeID, "xy");
		Document[] icodeDocs = DocumentManagerFactory.getInstance().find(docLib.getDocLibID(), "SYS_DELETEFLAG=0 and icCode=?",  new Object[]{code});
		return icodeDocs != null && icodeDocs.length > 0 ? true : false;
	}

}
