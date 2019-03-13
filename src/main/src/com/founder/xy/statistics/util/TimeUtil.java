package com.founder.xy.statistics.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by Ethan on 2016/12/16.
 */
public class TimeUtil {
    public static final int TWEENTYFOURHOURS = 0;

    public static final int CURRENTWEEK = 1;

    public static final int CURRENTMONTH = 2;

    //String转Timestamp
    public static Timestamp StringToTimestamp(String strTime) throws ParseException{
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return StringToTimestamp(strTime, timeFormat);
    }
    public static Timestamp StringToTimestamp(String strTime, SimpleDateFormat timeFormat) throws ParseException {
        Date realTime = timeFormat.parse(strTime);
        Timestamp time = new Timestamp(realTime.getTime());
        return time;
    }

    //获取某月第一天
    public static Timestamp getMonthFirstDay(Calendar cal) {
        if( cal == null ){
            cal = Calendar.getInstance();
        }
        cal.set(Calendar.DATE, 1);
        return getBeginDate(cal);
    }

    //获取某月最后一天
    public static Timestamp getMonthLastDay(Calendar cal) {
        if( cal == null ){
            cal = Calendar.getInstance();
        }
        cal.add(Calendar.MONTH, 1);
        cal.set(Calendar.DATE, 1);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return getEndDate(cal);
    }

    //把日期的时间部分改为00:00:00
    public static Timestamp getBeginDate(Calendar ca) {
        ca.set(Calendar.HOUR_OF_DAY, 0);
        ca.clear(Calendar.MINUTE);
        ca.clear(Calendar.SECOND);
        return new Timestamp(ca.getTime().getTime());
    }

    //把日期的时间部分改为23:59:59
    public static Timestamp getEndDate(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return new Timestamp(cal.getTime().getTime());
    }
  //获取本周第一天
	public static Timestamp getWeekFirstDay(Calendar cal) {
        if( cal == null ){
            cal = Calendar.getInstance(Locale.CHINA);
        }
        // 获得今天是一周的第几天，星期日是第一天，星期二是第二天......
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1; // 因为按中国礼拜一作为第一天所以这里减1
        if (dayOfWeek == 0) 
        	cal.add(Calendar.DAY_OF_MONTH, -1);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
       // Date date = cal.getTime();
        return getBeginDate(cal);
    
	}
	//获取本周最后一天
	public static Timestamp getWeekLastDay(Calendar cal) {
        if( cal == null ){
            cal = Calendar.getInstance();
        }
        cal.setTime(getWeekFirstDay(null));  
        cal.add(Calendar.DAY_OF_WEEK, 7);
        return getEndDate(cal);
    }
	//获取当天0点
	public static Timestamp getTodayZero(Calendar cal) {

        if( cal == null ){
            cal = Calendar.getInstance();
        }
        cal.setTime(new Date());  
        return getBeginDate(cal);
    
	}
	//获取当天24点
	public static Timestamp getTodayTwe(Calendar cal) {

        if( cal == null ){
            cal = Calendar.getInstance();
        }
        cal.setTime(new Date());  
        return getEndDate(cal);
    
	}
}
