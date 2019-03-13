package com.founder.amuc.commons;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/** 
 * @created 2014年9月18日 下午6:17:31 
 * @author  leijj
 * 类说明 ： 
 */
public class DateHelper {
	/** 返回 date 加上 value 天后的日期（清除时间信息） */
	public final static Date addDate(Date date, int value)
	{
		return addDate(date, value, true);
	}

	/** 返回 date 加上 value 天后的日期，trimTime 指定是否清除时间信息 */
	public final static Date addDate(Date date, int value, boolean trimTime)
	{
		return addTime(date, Calendar.DATE, value, trimTime);

	}

	/** 返回 date 加上 value 个 field 时间单元后的日期（不清除时间信息） */
	public final static Date addTime(Date date, int field, int value)
	{
		return addTime(date, field, value, false);
	}
	/** 返回 date 加上 value 个 field 时间单元后的日期，trimTime 指定是否去除时间信息 */
	public final static Date addTime(Date date, int field, int value, boolean trimTime)
	{
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(field, value);

		if(trimTime)
		{
        		c.set(Calendar.HOUR, 0);
        		c.set(Calendar.MINUTE, 0);
        		c.set(Calendar.SECOND, 0);
        		c.set(Calendar.MILLISECOND, 0);
		}
		return c.getTime();
	}
	
	public final static Timestamp getAfter1Min(){
		Calendar c=Calendar.getInstance();  
		c.add(Calendar.MINUTE, 1); 
		Timestamp ts = new Timestamp(c.getTime().getTime());
		return ts;
	}
	
	/**
	 * 获取年月日时分秒时间戳
	 * @return
	 */
	public static String getSysTime(){
		Date dNow = new Date();
		//保存目录名称
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		//保存文件名称后缀
		SimpleDateFormat formatT = new SimpleDateFormat("HHmmss");
		
		StringBuilder sb = new StringBuilder();
		//当前日期
		sb.append(format.format(dNow));
		//当前时分秒
		sb.append(formatT.format(dNow));
		return sb.toString();
	}
	
	/**
	 * 获取年月日时分秒时间戳
	 * @return
	 */
	public static String getSysDate(){
		Date dNow = new Date();
		//保存目录名称
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		
		StringBuilder sb = new StringBuilder();
		//当前日期
		sb.append(format.format(dNow));
		return sb.toString();
	}
	
	public static String getSysDateTime() {
		return new Timestamp(System.currentTimeMillis()).toString().substring(0,19);
	}
	
	/**
	 * 获取日期
	 * @param dateTime
	 * @return
	 */
	public static String getSubDate(String dateTime){
		if(dateTime == null || dateTime.length() < 10)
			return "";
		
		return dateTime.substring(0, 10);
	}
	
	/**
	 * 获取日期
	 * @return
	 */
	public static String getSubDate(){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(new Date());
	}
	
	/**
	 * 获取指定格式的日期
	 * @return
	 */
	public static String getFormat(){
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return format.format(new Date());
	}
	
	/**
	 * 转换指定格式的日期
	 * @return
	 * @throws ParseException 
	 */
	public static Date covertFormat(String time) throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.parse(time);
	}
	/**
	 * 获取小时
	 * @param dateTime
	 * @return
	 */
	public static String getSubHour(String dateTime){
		if(dateTime == null || dateTime.length() < 19)
			return "";
		
		return dateTime.substring(11, 13);
	}
	
	/**
	 * 获取分钟
	 * @param dateTime
	 * @return
	 */
	public static String getSubMin(String dateTime){
		if(dateTime == null || dateTime.length() < 19)
			return "";
		
		return dateTime.substring(14, 16);
	}
	
	/**
	 * 字符串转换成日期
	 * @param str 日期类型的字符串
	 * @param sdf  格式：yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static Date StrToDate(String str,String sdf) {
	   SimpleDateFormat format = new SimpleDateFormat(sdf);
	   Date date = null;
	   try {
		   date = format.parse(str);
	   } catch (ParseException e) {
		   e.printStackTrace();
	   }
	   return date;
	}
	
	/**
	 * 日期转换成字符串
	 * @param date  日期
	 * @param sdf  格式：yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static String DateToStr(Date date,String sdf) {
	   SimpleDateFormat format = new SimpleDateFormat(sdf);
	   String str = format.format(date);
	   return str;
	}
}