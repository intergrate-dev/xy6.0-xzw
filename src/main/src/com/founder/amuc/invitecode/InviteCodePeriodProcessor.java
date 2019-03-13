package com.founder.amuc.invitecode;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.founder.amuc.commons.Constant;
import com.founder.amuc.commons.InfoHelper;
import com.founder.amuc.tenant.Tenant;
import com.founder.amuc.tenant.TenantManager;
import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.Log;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;

/**
 * 计算邀请码有效期的处理器
 * @author fan jingcheng
 * 2015-08-28
 */
public class InviteCodePeriodProcessor {
	
	private final String SQL_INVITECODE = "select SYS_DOCUMENTID from ucInviteCode where icExpiredDay > 0 and SYS_DELETEFLAG = 0";
	private Log log = Context.getLog("amuc.invitecode");
	/**
	 * 计算邀请码有效期（天数）
	 * @throws E5Exception 
	 * @throws Exception 
	 */
	public void process() throws E5Exception{
		log.info("------开始计算邀请码有效期服务------" + DateUtils.getTimestamp());
		
		TenantManager tManager = (TenantManager)Context.getBean(TenantManager.class);
		List<Tenant> ts = tManager.getAll();
		for (Tenant tenant : ts) {
			//按租户的设置进行处理
			dealPeriod(tenant);
		}
		
		log.info("------结束计算邀请码有效期服务------" + DateUtils.getTimestamp());
	}
	private void dealPeriod(Tenant tenant) throws E5Exception  {
		
		//取出所有在有效期内的邀请码
		List<Long> inviteCodes = getInvitecode(tenant);
		
		//对每条记录进行有效期计算
		for (Long docID : inviteCodes) {
			
			updatePeriod(docID,tenant.getCode());
		}
		
	}
	private void updatePeriod(Long id,String code) throws E5Exception {
		
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_INVITECODE, code);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = docManager.get(docLib.getDocLibID(), id);
		
		int icExpiredDay = doc.getInt("icExpiredDay");
		
		Timestamp createTime = doc.getTimestamp("SYS_CREATED");
		Date crTime = doc.getDate("SYS_CREATED");
		//计算当前日期和邀请码创建日期差几天,更新有效期字段
		int subtraction = subtraction(createTime,icExpiredDay,crTime);
				
		doc.set("icExpiredDay", subtraction);
		
		docManager.save(doc);
	}
	/**
	 * 计算当前日期和邀请码创建日期差几天,更新有效期字段
	 * @param createTime
	 * @param icExpiredDay
	 * @return
	 * @throws ParseException 
	 * @throws Exception 
	 */
	private int subtraction(Timestamp createTime, int icExpiredDay,Date crTime) {
		
		//1.按照日期+时间去计算，存在邀请码有效期已过但仍能使用的漏洞
		/*Date nowday = new  Date();
		long createtime = createTime.getTime();
		long currenttime = nowday.getTime();
		long sub = currenttime - createtime ; 
		long subday = sub / (24 * 3600 * 1000) ;
		int result = (int)(icExpiredDay - subday);*/
		
		//2.仅使用日期去计算。生成邀请码的当天是有效期第一天
		
		int result = 0 ;
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");   
		try {
			System.out.println("当前时间："+sdf.format(new Date()));
			System.out.println("生成时间：" +sdf.format(crTime));
			
			Date now1 = sdf.parse(sdf.format(new Date()));
			Date now2 = sdf.parse(sdf.format(crTime));
			
			System.out.println("当前时间："+now1.getTime());
			System.out.println("生成时间："+now2.getTime());
			System.out.println("crTime时间："+crTime.getTime());
			System.out.println((now1.getTime() - now2.getTime()) / (24 * 3600 * 1000));
			
			result = icExpiredDay - (int)(now1.getTime() - now2.getTime()) / (24 * 3600 * 1000);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return result >=0 ? result : 0 ;
	}
	/**
	 * 取出所有在有效期内的邀请码记录（即有效期  > 0 ）
	 * @throws E5Exception 
	 */
	private List<Long> getInvitecode(Tenant t) throws E5Exception{
		List<Long> inviteCodes = new ArrayList<Long>();
		
		String sql = _sqlInviteCode(t);
		DBSession conn = null;
		IResultSet rs = null;
		try {
			conn = Context.getDBSession();
			rs = conn.executeQuery(sql, null);
			while (rs.next()) {
				inviteCodes.add(rs.getLong("SYS_DOCUMENTID"));
			}
		} catch (Exception e) {
			throw new E5Exception(e);
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}
		return inviteCodes;
	}
	
	private String _sqlInviteCode(Tenant t) throws E5Exception {
		return InfoHelper.replaceSQL(t.getCode(), SQL_INVITECODE, Constant.DOCTYPE_INVITECODE, "ucInviteCode");
	}
}
