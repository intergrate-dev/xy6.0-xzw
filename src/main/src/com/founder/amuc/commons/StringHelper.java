package com.founder.amuc.commons;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.sys.SysConfigReader;

/**
 * @author leijj
 * @date 2014-9-10
 * Description:
 */
public class StringHelper {
	public static String removeStr(String str) {
		String dest = "";
		Pattern p = Pattern.compile("\r|\n");
		Matcher m = p.matcher(str);
		dest = m.replaceAll("<br/>");
		dest = dest.replaceAll("\t",  "&#9;");
		return dest;
	}

	public static String replaceStr(String str) {
		if(str == null) return "";

		return removeStr(str.replaceAll("\"", "'"));
	}
	
	public static String textFilter(String text){
		if(text == null || text.length() ==0 ) return text;
		text = text.replaceAll("&amp;", "&");
		text = text.replaceAll("&lt;", "<");
		text = text.replaceAll("&gt;", ">");
		text = text.replaceAll("&quot;", "\"");
		
		text = text.replaceAll("&nbsp;",  " ");
		text = text.replaceAll("&#9;",  "\t");
		text = text.replaceAll("\n","\r\n");
		
		return text;
	}
	
	public static String textReplace(String text){
		if(text == null || text.length() ==0 ) return text;
		text = text.replaceAll("&", "&amp;");
		
		//text = text.replaceAll("<br/>","\r\n");
		return text;
	}
	
	public static int StrToInt(String str){
		if(str == null || str.length() ==0 ) return 0;
		return Integer.valueOf(str);
	}
	
	/**
	 * 根据项目名和条目名称，从参数配置管理中获取条目的值
	 * @param project  项目名
	 * @param item  条目名
	 * @return 条目值
	 * @throws E5Exception
	 */
	public static String getSysConfigVal(String project,String item) throws E5Exception{
		SysConfigReader configReader = (SysConfigReader) Context.getBean(SysConfigReader.class);
		String sysConfigVal = configReader.get(1, project, item);
		return sysConfigVal;
	}
}
