package com.founder.amuc.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.founder.amuc.commons.HTTPHelper;
import com.founder.e5.commons.DomHelper;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBType;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.LibHelper;
import com.founder.amuc.commons.DateFormatAmend;

@Controller
@RequestMapping("/api/pcard")
public class PaperCardAdapter{
	
  @RequestMapping("/activatePaperCard.do")
  public void activatePaperCard(HttpServletRequest request,
      HttpServletResponse response, Map model) throws Exception {
    response.setHeader("Access-Control-Allow-Origin", "*");
    response.setContentType("text/html;charset=UTF-8");
    //HTTPHelper.checkValid(request);
    String uid = request.getParameter("uid");
    String ssoid = request.getParameter("ssoid");
    String pcno = request.getParameter("pcno");
    String password = request.getParameter("password");
    int siteID = Integer.parseInt(request.getParameter("siteID"));
    String dbType = DomHelper.getDBType();
    JSONObject obj = new JSONObject();
    if (StringUtils.isBlank(uid) && StringUtils.isBlank(ssoid)) {
      obj.put("code", "1000");
      obj.put("msg", "参数错误");
      outputJson(String.valueOf(obj), response);
      return;
    }
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    DocumentManager docManager = DocumentManagerFactory.getInstance();
    DocLib pcdocLib = LibHelper.getLib(DocTypes.MEMBERPAPERCARD.typeID(), "xy");
    DocLib pcdocLibLog = LibHelper.getLib(DocTypes.MEMBERPAPERCARDLOG.typeID(),
        "xy");
    Document mDoc = null;
    if (!StringUtils.isBlank(uid)) {
      mDoc = getMemberByid(uid,siteID,"xy");
    } else if (!StringUtils.isBlank(ssoid)) {
      mDoc = getMemberByssoid(ssoid,siteID,"xy");
    }

    if (mDoc == null) {
      obj.put("code", "1001");
      obj.put("msg", "系统内不存在该用户");
      outputJson(String.valueOf(obj), response);
      return;
    }
    if (mDoc != null && mDoc.getInt("mStatus") == 0) {
      obj.put("code", "1002");
      obj.put("msg", "该用户被禁用");
      outputJson(String.valueOf(obj), response);
      return;
    } else {
      String pccondition = "SYS_DELETEFLAG = 0 and pcNo = ? and pcPassword = ? and m_siteID = ? ";
      Document[] pcards = docManager.find(pcdocLib.getDocLibID(), pccondition,
          new Object[] { pcno, password,siteID });
      String sql = "";
      if (dbType.equals(DBType.ORACLE)) {
    	  sql = "SYS_DELETEFLAG = 0 and pcNo = ? and pcPassword = ? and m_siteID = ? and TO_CHAR(sysdate,'YYYY-MM-DD') <= pcExpireTime and pcExpireTime <> '' ";
      }else{
      	  sql = "SYS_DELETEFLAG = 0 and pcNo = ? and pcPassword = ? and m_siteID = ? and date_format(now(),'%y-%m-%d') <= pcExpireTime and pcExpireTime <> '' ";
      }
      Document[] pcardsql = docManager.find(pcdocLib.getDocLibID(), sql,
          new Object[] { pcno, password, siteID });
      if (pcards != null && pcards.length > 0) {
        Document doc = pcards[0];
        if (doc.getInt("pcMember_ID") != 0
            && !(mDoc.getDocID() + "").equalsIgnoreCase(doc
                .getInt("pcMember_ID") + "")) {
          // "报卡已绑定其它用户"
          obj.put("code", "1003");
          obj.put("msg", "报卡已绑定其他用户");
        } else if (pcardsql == null && pcardsql.length <= 0) {
          obj.put("code", "1003");
          obj.put("msg", "报卡已过期");
        } else {
          if (doc.getString("pcActiveStatus").equals("未激活")) {
            if (doc.getString("pcStatus").equals("1")) {
              if (password.equals(doc.getString("pcPassword"))) {
                Document docLog = docManager.newDocument(pcdocLibLog
                    .getDocLibID());
                docLog.set("status", "已激活");
                docLog.set("operation", "激活");
                docLog.set("SYS_CREATED", DateFormatAmend.timeStampDispose(df.format(new Date())));
                docLog.setFolderID(pcdocLibLog.getFolderID());
                docLog.set("cardPrefix", pcno.substring(0, 13));
                docLog.set("operationNum", "1");
                docLog.set("pcTotalMoney", doc.get("pcMoney")); // 报卡总额
                docLog.set("effectTime", doc.get("pcEffectTime"));
                docLog.set("expireTime", doc.get("pcExpireTime"));
                docLog.set("paperName", doc.get("pcPaperName"));
                docLog.set("detail", "成功激活1张报卡");
                docLog.set("operator", "后台：" + doc.get("pcOperator"));
                docLog.set("operatorid", doc.get("pcOperatorID"));
                docLog.set("m_siteID", siteID);
                docManager.save(docLog);

                doc.set("pcActiveStatus", "激活");
                doc.set("pcMember_ID", mDoc.getDocID());
                doc.set("activeTime", DateFormatAmend.timeStampDispose(df.format(new Date())));
                doc.set("pcMember", mDoc.get("mName"));
                doc.set("pcMobile", mDoc.get("mMobile"));
                System.out.println(mDoc.get("mMobile"));
                docManager.save(doc);
                obj.put("code", "1004");
                obj.put("msg", "报卡激活成功");
              } else {
                obj.put("code", "1006");
                obj.put("msg", "报卡密码有误");
              }

            } else {
              obj.put("code", "1007");
              obj.put("msg", "报卡无效");
            }
          } else {
            obj.put("code", "1005");
            obj.put("msg", "报卡已激活");
          }
        }
      } else {
        obj.put("code", "1006");
        obj.put("msg", "报卡号或密码不正确");
      }
    }

    outputJson(String.valueOf(obj), response);
  }
	
	/**
	 * 根据uid查询会员
	 * 返回：会员members对象
	 * @param uid
	 * @param tenantcode
	 * @return
	 * @throws E5Exception
	 */
	private Document getMemberByid(String uid,int siteID, String tenantcode) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib mdocLib = LibHelper.getLib(DocTypes.MEMBER.typeID(),"xy");
		// 查询系统中是否存在uid的会员
		String mcondition = "SYS_DOCUMENTID = ? and m_siteID = ? and SYS_DELETEFLAG = 0";
		String[] column = { "mName", "mMobile", "mStatus" };
		Document[] members = docManager.find(mdocLib.getDocLibID(), mcondition,new Object[] { uid,siteID }, column);
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
	private Document getMemberByssoid(String ssoid,int siteID, String tenantcode) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib mdocLib = LibHelper.getLib(DocTypes.MEMBER.typeID(),"xy");
		// 查询系统中是否存在uid的会员
		String mcondition = "uid_sso = ? and m_siteID = ? and SYS_DELETEFLAG = 0";
		String[] column = { "mName", "mMobile", "mStatus" };
		Document[] members = docManager.find(mdocLib.getDocLibID(), mcondition,new Object[] { ssoid,siteID }, column);
		if (members != null && members.length > 0) {
			return members[0];
		}else{
			return null;
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
	
}
