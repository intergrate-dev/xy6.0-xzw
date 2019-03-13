package com.founder.xy.config;

import java.lang.reflect.Field;

import com.founder.e5.commons.StringUtils;
import com.founder.e5.config.ConfigReader;

/**
 * 加密读取工具接口
 * @author Gong Lijie
 */
public class SecurityHelper {
	private static final String KEY_MODULE = "m";
	private static final String KEY_SITECOUNT = "u"; //站点个数
	private static final String KEY_USERCOUNT = "c"; //用户个数
	
	//各个加密点的编号常量
	private static final int MODULE_WEB = 1;	//WEB
	private static final int MODULE_APP = 2;	//App
	private static final int MODULE_NIS = 3;	//互动
//	private static final int MODULE_LEADER = 4;	//人物
//	private static final int MODULE_TOPIC = 5;	//选题
//	private static final int MODULE_VOTE = 6;	//投票
	private static final int MODULE_SPECIAL = 11;//专题
	private static final int MODULE_EPAPER = 12; //数字报
	private static final int MODULE_WBWX = 13; //微博微信
	private static final int MODULE_MAGAZINE = 14; //期刊
	
	private static final int MODULE_PAPERPAY = 15; //数字报收费墙
	private static final int MODULE_MEMBER = 16; //会员
	private static final int MODULE_LIVE = 17; //直播
	private static final int MODULE_BATMAN = 18; //通讯员投稿平台
	private static final int MODULE_PROOF = 19; //校对
	private static final int MODULE_SENSITIVE = 20; //敏感词
	private static final int MODULE_VIDEO = 21; //纳加视频
	private static final int MODULE_MUGEDA = 22; //H5木疙瘩
	
	/** 授权允许的站点数 */
	public static int getSiteCount() {
		String sValue = ConfigReader.getLicenseValue(KEY_SITECOUNT);
		int value = (sValue != null) ? Integer.parseInt(sValue) : 0;
		return value;
	}
	
	/** 授权允许的用户数 */
	public static int getUserCount() {
		String sValue = ConfigReader.getLicenseValue(KEY_USERCOUNT);
		int value = (sValue != null) ? Integer.parseInt(sValue) : 0;
		return value;
	}
	
	/** License有效时间 */
	public static String getLicenseDate() {
		return ConfigReader.getLicenseDate();
	}
	
	/** 有web 加密点 */
	public static boolean webUsable() {
		return moduleUsable(MODULE_WEB);
	}
	/** 有app加密点 */
	public static boolean appUsable() {
		return moduleUsable(MODULE_APP);
	}
	/** 有互动加密点 */
	public static boolean nisUsable() {
		return moduleUsable(MODULE_NIS);
	}
	/** 有专题加密点 */
	public static boolean specialUsable() {
		return moduleUsable(MODULE_SPECIAL);
	}
	/** 有数字报加密点 */
	public static boolean epaperUsable() {
		return moduleUsable(MODULE_EPAPER);
	}
	/** 有期刊加密点 */
	public static boolean magazineUsable() {
		return moduleUsable(MODULE_MAGAZINE);
	}
	/** 有微博微信加密点 */
	public static boolean wbwxUsable() {
		return moduleUsable(MODULE_WBWX);
	}
	
	/** 有数字报收费墙加密点 */
	public static boolean paperPayUsable() {
		return moduleUsable(MODULE_PAPERPAY);
	}
	/** 有会员加密点 */
	public static boolean memberUsable() {
		return moduleUsable(MODULE_MEMBER);
	}
	/** 有直播加密点 */
	public static boolean liveUsable() {
		return moduleUsable(MODULE_LIVE);
	}
	/** 有通讯员投稿平台加密点 */
	public static boolean batmanUsable() {
		return moduleUsable(MODULE_BATMAN);
	}
	/** 有校对加密点 */
	public static boolean proofUsable() {
		return moduleUsable(MODULE_PROOF);
	}
	/** 有敏感词加密点 */
	public static boolean sensitiveUsable() {
		return moduleUsable(MODULE_SENSITIVE);
	}
	/** 有纳加视频加密点 */
	public static boolean videoUsable() {
		return moduleUsable(MODULE_VIDEO);
	}
	/** 有H5木疙瘩加密点 */
	public static boolean mugedaUsable() {
		return moduleUsable(MODULE_MUGEDA);
	}
	/** 只有web渠道 */
	public static boolean onlyWeb() {
		return webUsable() && !appUsable();
	}
	/** 只有app渠道 */
	public static boolean onlyApp() {
		return appUsable() && !webUsable();
	}
	/**
	 * 检测功能点是否授权
	 * @param resourceId 功能点id
	 * @return true 已授权,false 未授权
	 */
	private static boolean moduleUsable(int resourceId){
		String m = ConfigReader.getLicenseValue(KEY_MODULE);
		m = "," + m + ",";
		return m.indexOf("," + resourceId + ",") >= 0;
	}
	
	/**
	 * 给出一个加密点控制字符串，判断系统的加密点是否符合条件
	 * 可用于配置文件读取时的加密点判断
	 * @param licenseID 加密点字符串，格式为：AD_IC|AD_CAT|AD_SOFT,ARTICLE
	 * @return
	 */
	@SuppressWarnings("unused")
	private static boolean hasLicense(String licenseID) {
		//空或者无加密点属性，则可见
		if (StringUtils.isBlank(licenseID)) return true;
		
		//配置模板中的加密点字符串格式：AD_IC|AD_CAT|AD_SOFT,ARTICLE
		//逗号表示and关系，|表示or关系
		String[] andLicenses = licenseID.split(",");
		for (String andLicense : andLicenses) {
			String[] orLicenses = andLicense.split("\\u007C"); //竖线
			boolean check = false;
			for (String orLicense : orLicenses) {
				int resourceId = licenseConstant(orLicense);
				if (moduleUsable(resourceId)) {
					check = true;
					break;
				}
			}
			if (!check) {
				return false;
			}
		}
		return true;
	}
	
	private static int licenseConstant(String orLicense) {
		try {
			Field res = SecurityHelper.class.getDeclaredField("MODULE_" + orLicense);
			int resourceId = res.getInt(null);
			
			return resourceId;
		} catch (Exception e) {
			int resourceId = Integer.parseInt(orLicense);
			return resourceId;
		}
	}
}
