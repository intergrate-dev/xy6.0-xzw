package com.founder.amuc.commons;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class DateFormatAmend {
	public static Object DateDispose(String string) throws Exception {
		SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd");

		return (string.equals("")||string==null) ? null : sim.parse(string);
	}
	
	public static Object timeStampDispose(String timeStamp) throws Exception {
		return Timestamp.valueOf(timeStamp);
	}
}
