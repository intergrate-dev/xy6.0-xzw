package com.founder.amuc.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.founder.amuc.invitecode.InviteCodeManager;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.founder.amuc.commons.HTTPHelper;
import com.founder.amuc.commons.InfoHelper;
import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.LibHelper;
import com.founder.e5.web.WebUtil;

@Controller
@RequestMapping("/api/invitecode")
public class InviteCodeAdapter{
	//@Context
	HttpServletResponse response;

    @Autowired
    private InviteCodeManager manager;

	/**
	 * 根据设备号和邀请码写入记录。
	 * 如果该设备号已经写入邀请码则不再写入。
	 * 如果邀请码不存在，返回。
	 * @param code
	 * @param imei
	 * @return
	 * @throws E5Exception
	 */
	@RequestMapping("/codeImeiRecord.do")
	public void codeImeiRecord(HttpServletRequest request,
			HttpServletResponse response, Map model) throws E5Exception{
    response.setHeader("Access-Control-Allow-Origin", "*");
    try {
      //HTTPHelper.checkValid(request);
      JSONObject obj = new JSONObject();
      String code = request.getParameter("code");
      String imei = request.getParameter("imei");
      String uid = request.getParameter("uid");
      String name = request.getParameter("name");
      int siteID = WebUtil.getInt(request, "siteID", 1);

      DocumentManager docManager = DocumentManagerFactory.getInstance();
      DocLib docLib = LibHelper.getLib(DocTypes.MEMBER.typeID(), "xy");
      DocLib codeDocLib = LibHelper.getLib(DocTypes.MEMBERINVITECODE.typeID(),
          "xy");
      DocLib codelogDocLib = LibHelper.getLib(
          DocTypes.MEMBERINVITECODELOG.typeID(), "xy");

      Document[] cdocs = docManager.find(codeDocLib.getDocLibID(),
          "icCode = ? AND SYS_DELETEFLAG = 0 and m_siteID = ? ", new Object[] { code, siteID });
      if (cdocs != null && cdocs.length > 0) {
        // 新锐app会员不能输入自己的邀请码
        Document[] cdocs1 = docManager.find(codeDocLib.getDocLibID(),
            "icCode = ? AND icInviterID = ? AND SYS_DELETEFLAG = 0 and m_siteID = ? ",
            new Object[] { code, uid ,siteID});
        // 新锐app会员是否写过邀请码记录
        Document[] cldocs1 = docManager.find(codelogDocLib.getDocLibID(),
            "icInvitedID = ? AND SYS_DELETEFLAG = 0 and m_siteID = ? ", new Object[] { uid ,siteID});
        // 新华app该设备号是否写过邀请码记录
        Document[] cldocs = docManager.find(codelogDocLib.getDocLibID(),
            "icImei = ? AND SYS_DELETEFLAG = 0 and m_siteID = ? ", new Object[] { imei ,siteID});
        // 老用户不能使用邀请码
        Document[] doc1 = docManager
            .find(
                docLib.getDocLibID(),
                " SYS_DELETEFLAG = 0 and SYS_CREATED < '1900-12-26 00:00:00' and SYS_DOCUMENTID = ? and m_siteID = ? ",
                new Object[] { uid ,siteID});

        if (!StringUtils.isBlank(uid) && doc1 != null && doc1.length > 0) {

          obj.put("code", "1001");
          obj.put("msg", "邀请已注册会员无效");
        } else if (!StringUtils.isBlank(uid) && cdocs1 != null
            && cdocs1.length > 0) {

          obj.put("code", "1001");
          obj.put("msg", "会员不能使用自己的邀请码");
        } else if (!StringUtils.isBlank(uid) && cldocs1 != null
            && cldocs1.length > 0) {

          obj.put("code", "1001");
          obj.put("msg", "会员已经使用过邀请码");
        } else if (cldocs != null && cldocs.length > 0) {

          obj.put("code", "1001");
          obj.put("msg", "该设备已经添加过邀请码使用记录");
        } else {

          DBSession conn = null;
          try {
            conn = Context.getDBSession();
            conn.beginTransaction();
            Document[] doc = docManager.find(docLib.getDocLibID(),
                " SYS_DELETEFLAG = 0 and SYS_DOCUMENTID = ? and m_siteID = ? ",
                new Object[] { uid, siteID});
            Document codelogDoc = docManager.newDocument(
                codelogDocLib.getDocLibID(),
                InfoHelper.getID(codelogDocLib.getDocTypeID()));
            codelogDoc.setFolderID(codelogDocLib.getFolderID());
            codelogDoc.setDeleteFlag(0);
            codelogDoc.setLocked(false);
            Timestamp date = DateUtils.getTimestamp();
            codelogDoc.setCreated(date);
            codelogDoc.set("icCodeID", cdocs[0].getDocID());
            codelogDoc.set("icCode", code);
            codelogDoc.set("icImei", imei);
            codelogDoc.set("icInvitedID", uid);
            codelogDoc.set("m_siteID", siteID);
            if (doc != null && doc.length > 0) {
              codelogDoc.set("icInvitedName", doc[0].getString("mName"));
            }

            docManager.save(codelogDoc, conn);

            // 增加邀请码使用数量
            int icnum = cdocs[0].getInt("icNum") + 1;
            String member = cdocs[0].getString("icInviterID");
            cdocs[0].set("icNum", icnum);
            cdocs[0].set("icStatus", 1);
            docManager.save(cdocs[0], conn);

            conn.commitTransaction();
            obj.put("code", "1002");
            obj.put("member", (member == "" || member == null) ? "0" : member);// 邀请人id
            obj.put("msg", "该设备成功添加邀请码使用记录");

          } catch (Exception e) {
            ResourceMgr.rollbackQuietly(conn);
            e.printStackTrace();
          } finally {
            ResourceMgr.closeQuietly(conn);
          }
        }

      } else {
        obj.put("code", "1003");
        obj.put("msg", "邀请码不存在");
      }
      outputJson(String.valueOf(obj), response);
    } catch (Exception e) {
      throw new E5Exception(e.getMessage(),e.getCause());
    }
		
	}
	
	/**
	 * app调用接口生成邀请码
	 * 2015-09-17 
	 * @param length #的个数
	 * @return
	 */
  @RequestMapping("/getInviteCode.do")
  public void getInviteCode(HttpServletRequest request, HttpServletResponse response) 
		  throws E5Exception {
    response.setHeader("Access-Control-Allow-Origin", "*");
    try {
      JSONObject result = new JSONObject();
      String uid = request.getParameter("uid");
      int siteID = WebUtil.getInt(request, "siteID", 1);
      result = new JSONObject();
      DocumentManager docManager = DocumentManagerFactory.getInstance();
      int docTypeID = DocTypes.MEMBERINVITECODE.typeID();
      DocLib IcdocLib = LibHelper.getLib(docTypeID, "xy");// 邀请码文档类型
      DocLib MdocLib = LibHelper.getLib(DocTypes.MEMBER.typeID(), "xy");// 会员文档类型
      DocLib IcIddocLib = LibHelper.getLib(
          DocTypes.MEMBERINVITECODEID.typeID(), "xy");// 邀请码标识文档类型

      // 1.判断该uid 是否在系统会员表里存在
      Document memberdoc = docManager.get(MdocLib.getDocLibID(),
          Integer.parseInt(uid));
      if (memberdoc == null) {
        result.put("code", "0");
        result.put("msg", "系统中不存在uid:" + uid + "的会员");

      } else {
        // 2.存在会员后,判断该uid是否已经生成了邀请码，如果有直接返回。
        Document[] icodeDocs = docManager.find(IcdocLib.getDocLibID(),
            "SYS_DELETEFLAG=0 and icInviterID=? and m_siteID=?", new Object[] { uid, siteID});
        if (icodeDocs != null && icodeDocs.length > 0) {

          result.put("code", icodeDocs[0].getString("icCode"));
          result.put("msg", "系统中存在,获取邀请码成功");
        } else {// 3.如果该uid没有生成过邀请码,创建新的邀请码
          DBSession conn = null;
          try {
            // 取出邀请码标识（该标识每生成一个都会自增长）
            Document[] iciddoc = docManager.find(IcIddocLib.getDocLibID(),
                "SYS_DELETEFLAG=0 and icCodeIdentiType = 'app' ", null);
            if (iciddoc != null && iciddoc.length > 0) {

              char[] iccodechars = increStr(iciddoc[0]
                  .getString("icCodeIdenti"));// 取出邀请码标识,并增长

              //String iccode = InsertIntoCode(iccodechars);
              String iccode = manager.geneRanCode(iciddoc[0], 7);
              conn = Context.getDBSession();
              conn.beginTransaction();

              Document doc = docManager.newDocument(IcdocLib.getDocLibID(),
                  InfoHelper.getID(IcdocLib.getDocTypeID()));
              doc.setFolderID(IcdocLib.getFolderID());
              doc.setDeleteFlag(0);
              doc.setLocked(false);
              Timestamp date = DateUtils.getTimestamp();
              doc.setCreated(date);

              doc.set("icCode", iccode);
              doc.set("icExpiredDay", 7);
              doc.set("icStatus", 0);
              doc.set("icInviterID", memberdoc.getDocID());// 邀请人编号
              doc.set("icInviterName", memberdoc.get("mNickname"));// 邀请人名称
              doc.set("icType", 1);
              // 邀请码生成的类型 0-系统生成 1-会员生成
              doc.set("icCreateType", 1);
              doc.set("m_siteID", siteID);

              docManager.save(doc, conn);

              // 更新邀请码标识
              iciddoc[0].set("icCodeIdenti", String.valueOf(iccodechars));
              docManager.save(iciddoc[0], conn);

              conn.commitTransaction();
              result.put("code", iccode);
              result.put("msg", "获取邀请码成功");

            } else {
              result.put("code", 0);
              result.put("msg", "邀请码标识不存在,获取邀请码失败");
            }

          } catch (Exception e) {
            ResourceMgr.rollbackQuietly(conn);
            e.printStackTrace();
            result.put("code", "0");
            result.put("msg", "错误信息:" + e.getLocalizedMessage());
          } finally {
            ResourceMgr.closeQuietly(conn);
          }
        }
      }

      outputJson(String.valueOf(result), response);
    } catch (Exception e) {
      throw new E5Exception(e.getMessage(), e.getCause());
    }
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
	 * 获取用户邀请记录接口
	 * @param mobile
	 * @param email
	 * @param password
	 * @return
	 * @throws E5Exception 
	 */
  @RequestMapping("/getInviteRecord.do")
  public void getInviteRecord(HttpServletRequest request,
      HttpServletResponse response, Map model) throws E5Exception {
    response.setHeader("Access-Control-Allow-Origin", "*");
    try {
      //HTTPHelper.checkValid(request);
      String uid = request.getParameter("uid");
      int siteID = WebUtil.getInt(request, "siteID", 1);
      Map<String, Object> maps = new HashMap<String, Object>();
      String code = "";
      String icCode = "";
      StringBuilder msg = new StringBuilder();
      DocumentManager docManager = DocumentManagerFactory.getInstance();
      int docTypeID = DocTypes.MEMBERINVITECODE.typeID();
      DocLib IcdocLib = LibHelper.getLib(docTypeID, "xy");// 邀请码文档类型
      Document[] icodeDocs = docManager.find(IcdocLib.getDocLibID(),
          "SYS_DELETEFLAG=0 and icInviterID=? and m_siteID=?", new Object[] { uid, siteID});
      if (icodeDocs != null && icodeDocs.length > 0) {
        icCode = icodeDocs[0].getString("icCode");
      } else {
        code = "0";
        msg.append("用户没有生成邀请码");
      }
      DocLib docLib = LibHelper.getLib(DocTypes.MEMBERINVITECODELOG.typeID(),
          "xy");

      JSONObject Json = new JSONObject();
      JSONArray JsonArray = new JSONArray();
      String[] columns = { "icInvitedName", "SYS_CREATED" };
      Document[] members = docManager.find(docLib.getDocLibID(),
          " SYS_DELETEFLAG = 0 and icCode = ? and m_siteID = ? ", new Object[] { icCode, siteID},
          columns);
      if (members != null && members.length > 0) {
        for (int i = 0; i < members.length; i++) {
          Json.put("name", members[i].getString("icInvitedName"));
          Json.put("time", members[i].getString("SYS_CREATED"));
          JsonArray.add(Json);
        }

        code = "1";
        maps.put("record", JsonArray);
        msg.append("获取记录成功");
      } else {
        code = "0";
        msg.append("获取记录失败");
      }
      maps.put("code", code);
      maps.put("msg", msg.toString());
      JSONObject result = JSONObject.fromObject(maps);
      outputJson(String.valueOf(result), response);
    } catch (Exception e) {
      throw new E5Exception(e.getMessage(),e.getCause());
    }
  }
	/** 向response输出json数据 */
	public static void outputJson(String result, HttpServletResponse response) {
		if (result == null) return;
		
		response.setContentType("application/json; charset=UTF-8");

		PrintWriter out = null;
		try {
			out = response.getWriter();
			out.write(result);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(out);
		}
	}
	
	@RequestMapping("/siteConf.do")
	private void siteConf(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception {
		response.setHeader("Access-Control-Allow-Origin", "*");
		int siteID = WebUtil.getInt(request, "siteID", 1);
    	Map<String, Object> maps = new HashMap<String, Object>();
    	DocumentManager docManager = DocumentManagerFactory.getInstance();
  		DocLib docLib = LibHelper.getLib(DocTypes.SITE.typeID(),"xy");
  		Document site = docManager.get(docLib.getDocLibID(), siteID);
  		String siteConfig = site.getString("site_config");
		
		outputJson(String.valueOf(siteConfig), response);
    }
	
	@RequestMapping("/logoConf.do")
	private void logoConf(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception {
		response.setHeader("Access-Control-Allow-Origin", "*");
		int siteID = WebUtil.getInt(request, "siteID", 1);
    	Map<String, Object> maps = new HashMap<String, Object>();
    	DocumentManager docManager = DocumentManagerFactory.getInstance();
  		DocLib docLib = LibHelper.getLib(DocTypes.MOBILEOS.typeID(),"xy");
  		Document[] docs = docManager.find(docLib.getDocLibID(),
  	            "SYS_DELETEFLAG=0 and os_siteID=?", new Object[] {siteID});
  		
  		String dir = docs[0].getString("os_dir") + "/";
  		String os_logoPic1 = docs[0].getString("os_logoPic");
  		String os_appDownload = docs[0].getString("os_appDownload");
  		String os_appDownloadImage1 = docs[0].getString("os_appDownloadImage");
  		String os_appDownloadName = docs[0].getString("os_appDownloadName");
  		String os_name = docs[0].getString("os_name");
  		String os_logoPic = dir + os_logoPic1.substring(os_logoPic1.lastIndexOf("/") + 1);
  		String os_appDownloadImage = dir + os_appDownloadImage1.substring(os_appDownloadImage1.lastIndexOf("/") + 1);
  		
  		JSONObject jsonObject = new JSONObject();  
        jsonObject.put("AppName", os_name);  
        jsonObject.put("AppHttppath", os_logoPic); 
        jsonObject.put("AppDownload", os_appDownload);
        jsonObject.put("AppDownloadName", os_appDownloadName); 
        jsonObject.put("AppDownloadImage ", os_appDownloadImage);
		
		outputJson(String.valueOf(jsonObject), response);
    }
}
