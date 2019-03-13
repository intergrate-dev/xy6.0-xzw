package com.founder.amuc.commons;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DateUtil{
	 public static int days(int year,int month)
		{
		  int days = 0;
		  if(month!=2)
		  {
			   switch(month)
			   {
				   case 1:
				   case 3:
				   case 5:
				   case 7:
				   case 8:
				   case 10:
				   case 12:
					   days = 31 ;
					  break;
				   case 4:
				   case 6:
				   case 9:
				   case 11:
					   days = 30;
			   }
		  }
		  else
		  {
		
		   if(year%4==0 && year%100!=0 || year%400==0)
		    days = 29;
		   else  days = 28;
		
		  }
		  return days;
		 }
	 
	 public static int daysNum(String dateArg){
		    Calendar calendar = new GregorianCalendar();  
		    SimpleDateFormat sdf = new SimpleDateFormat("", Locale.CHINESE);  
		    sdf.applyPattern("yyyy-MM"); // 201203格式  
		    try {  
		        System.out.println(sdf.parse(dateArg));  
		        calendar.setTime(sdf.parse(dateArg));  
		    } catch (ParseException e) {  
		        e.printStackTrace();  
		    }  
		    int num2 = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);  
		    return num2;
	 }
	 public static String YearNum(){
		    SimpleDateFormat sdf = new SimpleDateFormat("", Locale.CHINESE);  
		    sdf.applyPattern("yyyy"); // 201203格式  
		    return sdf.format(new Date());
	 }
	 public static String MonthNum(){
		 SimpleDateFormat sdf = new SimpleDateFormat("", Locale.CHINESE);  
		 sdf.applyPattern("MM"); // 201203格式  
		 return sdf.format(new Date());
	 }
	 public static String DayNum(){
		 SimpleDateFormat sdf = new SimpleDateFormat("", Locale.CHINESE);  
		 sdf.applyPattern("dd"); // 201203格式  
		 return sdf.format(new Date());
	 }
	 public static void main(String[] args){
		 
		 daysNum("2011-02");
		 System.out.println(DayNum());
	 }
}
