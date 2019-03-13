package com.founder.xy.system.check;

import com.founder.e5.commons.StringUtils;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.config.SecurityHelper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * License查看页面
 * @author han.xf
 *
 */
@Controller
@RequestMapping("/xy/system")
public class LicenseConstroller {
	/**主界面功能项配置的保存*/
	@RequestMapping(value = "LicenseView.do")
	public void mainPermissionSubmit(HttpServletRequest request,HttpServletResponse response){
		JSONObject obj = new JSONObject() ;
		int siteCount = SecurityHelper.getSiteCount() ; //授权允许的站点数 
		int userCount = SecurityHelper.getUserCount() ; //授权允许的用户数 
		JSONArray channel = new JSONArray() ;  //支持渠道
		channel.add(this.assemble("WEB",SecurityHelper.webUsable())) ;
		channel.add(this.assemble("APP",SecurityHelper.appUsable())) ;

		JSONArray module = new JSONArray() ; //功能模块

		module.add(this.assemble("专题设计",SecurityHelper.specialUsable())) ;
		module.add(this.assemble("数字报",SecurityHelper.epaperUsable())) ;
		module.add(this.assemble("数字报收费墙",SecurityHelper.paperPayUsable())) ;
		module.add(this.assemble("会员",SecurityHelper.memberUsable())) ;
		module.add(this.assemble("互动(问政、问吧、活动、投票)",SecurityHelper.nisUsable())) ;
		
		module.add(this.assemble("直播",SecurityHelper.liveUsable())) ;
		module.add(this.assemble("通讯员投稿平台",SecurityHelper.batmanUsable())) ;
		module.add(this.assemble("校对",SecurityHelper.proofUsable())) ;
		module.add(this.assemble("敏感词",SecurityHelper.sensitiveUsable())) ;
		module.add(this.assemble("纳加视频",SecurityHelper.videoUsable())) ;
		
		module.add(this.assemble("H5木疙瘩",SecurityHelper.mugedaUsable())) ;
		module.add(this.assemble("微博微信",SecurityHelper.wbwxUsable())) ;
		
		obj.put("expireTime", StringUtils.getNotNull(SecurityHelper.getLicenseDate()));
		obj.put("siteCount", siteCount) ;
		obj.put("userCount", userCount) ;
		obj.put("channel", channel) ;
		obj.put("module", module) ;
		InfoHelper.outputJson(obj.toString(), response);
	}

	private  JSONObject assemble(String item, boolean usable){
		JSONObject json = new JSONObject() ;
		json.put("item",item) ;
		json.put("usable",usable) ;
		return json ;
	}
}
