package com.founder.xy.system.job;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.dom.DocLib;
import com.founder.e5.scheduler.BaseJob;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.system.Tenant;

/**
 * 检查客户端登录信息表，删除所有最后修改时间在两周前的记录。
 */
public class AppLoginClean extends BaseJob{

	public AppLoginClean() {
		super();
		log = Context.getLog("xy.appLoginClean");
	}

	@Override
	protected void execute() throws E5Exception {
		log.info("开始清理客户端登录信息表");
		
		DocLib lib = LibHelper.getLib(DocTypes.APPLOGIN.typeID(), Tenant.DEFAULTCODE);
		
		String sql = "DELETE FROM " + lib.getDocLibTable() + " WHERE SYS_LASTMODIFIED<?";
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -13);
		
		InfoHelper.executeUpdate(lib.getDocLibID(), sql, new Object[]{
			new SimpleDateFormat("yyyy-MM-dd 00:00:00").format(cal.getTime())});
		log.info("清理完毕");
	}
}