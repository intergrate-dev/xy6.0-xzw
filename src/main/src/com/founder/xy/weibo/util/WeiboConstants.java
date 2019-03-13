package com.founder.xy.weibo.util;

import com.founder.e5.commons.FileUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * @Description: TODO
 * 
 * @author: tongxs
 * @date: 2015-10-10 ����10:57:06
 */
public class WeiboConstants {
	
	 //微博key不能变
	 private static final byte[] WeiBoKey = new byte[]{123,-67,-20,-2,32,-5,-9,79,-99,54,44,11,-78,16,-12,32,-23,-29,25,-51,-68,22,107,41};
	private static final String WeiBoKeyFile = "weibo-key.license";

	public final static String WEIBO_GET_IMAGE_URL = "/weibo/getImage.do" ;
	public final static String WEIBO_GET_ATTACH_URL = "/newsedit/editor/GetAttachment.do";
	public  static String FOUNDER_APPID=null;
	public  static String FOUNDER_APPSCRECT=null;
	static
	{
		try
		{
			String ecryptString = readLicenseFromFile();
			String planTxt = decrypt(ecryptString);
			genAppKey(planTxt);
			
		}
		catch(Exception ex)
		{
			System.out.println("没有向方正申请微信key，无法使用微信");
		}
		
	}
	static private void genAppKey(String plainText)
	{
		String modules[] = plainText.substring(2, plainText.length()).split("#");
		for (int i = 0; i < modules.length; i++)
		{
			if (modules[i].length() <= 2)
				continue;

			int s = modules[i].indexOf("=");
			if(s<0)
				continue;
			String key = modules[i].substring(0, s);
			String value = modules[i].substring(s + 1, modules[i].length());
			if(key.equals("bbb"))
			{
				FOUNDER_APPID=value;
			}

			if(key.equals("ddd"))
			{
				FOUNDER_APPSCRECT=value;
			}

		}
	}

	static private String readLicenseFromFile() throws Exception
	{
		String encryptStr = null;
		encryptStr = FileUtils.readClassPathFile(WeiBoKeyFile);

		if (encryptStr == null)
			throw new Exception("No license!");

		return encryptStr;
	}

	static  String decrypt(String encryptString) 
	   {
		String algorithm = "DESede";
		   //密钥1
		 SecretKey secretKey = new SecretKeySpec(WeiBoKey,algorithm);
		 String result = null;
		   try
		   {
			   byte[] encryptByte = hex2byte(encryptString);
			   
			   Cipher c1 = Cipher.getInstance(algorithm);
			   c1.init(Cipher.DECRYPT_MODE, secretKey);
			   byte[] clearByte = c1.doFinal(encryptByte);
			   
			   result = new String(clearByte);
		   }
		   catch(Exception e)
		   {
			   e.printStackTrace();
		   }
		   return result;
	   }
	
	
	static 	private byte[] hex2byte(String hex) //转换成二进制
	{
		byte[] _byte = new byte[hex.length()/2];
		for(int i=0;i<(hex.length()/2);i++)
		{
			_byte[i] = (byte)Integer.parseInt(hex.substring(i*2,i*2+2),16);
		}
		
		return _byte;
	}
}
