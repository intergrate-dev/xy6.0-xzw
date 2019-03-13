package com.founder.xy.app.web;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.web.WebUtil;
import com.founder.e5.workspace.ProcHelper;
import com.founder.xy.app.MobileAppManager;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.system.Tenant;
import com.founder.xy.system.site.DomainDirManager;

@Controller
@RequestMapping("/xy")
public class MobileAppController {

	@Autowired
	private MobileAppManager mobileAppManager;
	@Autowired
	private DomainDirManager domainDirManager;

	/**
	 * 保存app
	 **/
	@RequestMapping(value = "mobileAppformSave.do")
	public String formSave(HttpServletRequest request,
			HttpServletResponse response) throws E5Exception,
			UnsupportedEncodingException {
		String url = "/e5workspace/after.do?UUID="
				+ WebUtil.get(request, "UUID");

		String tenantCode = Tenant.DEFAULTCODE;
		int docLibID = LibHelper.getLibID(DocTypes.MOBILEAPP.typeID(),
				tenantCode);
		long docID = InfoHelper.getNextDocID(DocTypes.MOBILEAPP.typeID());

		try {
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			Document doc = docManager.newDocument(docLibID, docID);
			ProcHelper.initDoc(doc);
			int maType = Integer.parseInt(request.getParameter("ma_type"));

			doc.set("ma_type",
					StringUtils.getNotNull(request.getParameter("ma_type")));
			doc.set("ma_name",
					StringUtils.getNotNull(request.getParameter("ma_name")));
			doc.set("ma_moID",
					StringUtils.getNotNull(request.getParameter("ma_moID")));
			doc.set("ma_appKey",
					StringUtils.getNotNull(request.getParameter("ma_appKey")));
			doc.set("ma_description", StringUtils.getNotNull(request
					.getParameter("ma_description")));
			// 0位IOS 不作md5处理
			if (maType == 1) {
				doc.set("ma_md5",
						StringUtils.getNotNull(request.getParameter("ma_md5")));
			}
			docManager.save(doc);
		} catch (Exception e) {
			url += "&Info=" + URLEncoder.encode("操作失败", "UTF-8");
			return "redirect:" + url;
		}
		url += "&DocIDs=" + docID;
		return "redirect:" + url;
	}

	/**
	 * 删除app
	 **/
	@RequestMapping(value = "mobileAppDelete.do")
	public String appDelete(HttpServletRequest request,
			HttpServletResponse response) throws E5Exception,
			UnsupportedEncodingException {

		String tenantCode = Tenant.DEFAULTCODE;
		int appLibID = LibHelper.getLibID(DocTypes.MOBILEAPP.typeID(),
				tenantCode);
		int packageLibID = LibHelper.getLibID(DocTypes.MOBILEPACKAGE.typeID(),
				tenantCode);

		// app删除
		String delAppSql = "delete from " + LibHelper.getLibTable(appLibID)
				+ " where SYS_DOCUMENTID=?";

		// 包删除
		String delPackageSql = "delete from "
				+ LibHelper.getLibTable(packageLibID) + " where mp_maId=?";

		Object[] ob = new Object[] { request.getParameter("id") };
		// 同时修改多个稿件，使用事务
		DBSession conn = null;
		try {
			conn = Context.getDBSession();
			conn.beginTransaction();
			InfoHelper.executeUpdate(delAppSql, ob, conn);
			InfoHelper.executeUpdate(delPackageSql, ob, conn);
			conn.commitTransaction();
			return null;
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			return "操作中出现错误：" + e.getLocalizedMessage();
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
	}
}
