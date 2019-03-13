package com.founder.xy.commons;

import org.apache.commons.codec.binary.Base64;

public class Base64Decoder {
	
	public static byte[] decode(byte[] val){
		
		Base64 base64 = new Base64();
		return base64.decode(val);
		
	}
	
	public static byte[] decode(String val){
		
		byte[] bytes = val.getBytes();
		return decode(bytes);
		
	}
	
	public static byte[] encode(String val){
		
		byte[] bytes = val.getBytes();
		Base64 base64 = new Base64();
		return base64.encode(bytes);
		
	}
	
	public static byte[] encode(byte[] val){
		
		Base64 base64 = new Base64();
		return base64.encode(val);
		
	}

}
