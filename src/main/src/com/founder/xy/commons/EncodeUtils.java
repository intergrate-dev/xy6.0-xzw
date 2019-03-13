package com.founder.xy.commons;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.founder.e5.commons.ResourceMgr;

public class EncodeUtils {

	/**
	 * 获取字符串的MD5值
	 */
	public static String getMD5(String str) {
		return getMD5(str.getBytes());
	}

	/**
	 * 获得MD5码
	 */
	public static String getMD5(byte[] data) {
		if (data == null) return null;
	
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("MD5");
			digest.update(data);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	
		return toHex(digest.digest());
	}

	/**
	 * 根据文件名获得文件MD5
	 */
	public static String fileToMD5(String fileName) {
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(fileName);
			return streamToMD5(inputStream);
		} catch (Exception e) {
			return null;
		} finally {
			ResourceMgr.closeQuietly(inputStream);
		}
	}

	/**
	 * 根据输入流获得MD5
	 */
	public static String streamToMD5(InputStream inputStream) {
		try {
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			
			byte[] buffer = new byte[1024];
			int numRead = 0;
			while ((numRead = inputStream.read(buffer)) > 0) {
				mdTemp.update(buffer, 0, numRead);
			}
			return toHexString(mdTemp.digest());
		} catch (Exception e) {
			return null;
		}
	}

	/** BASE64编码 */
	public static String encodeBase64(String str) {
		return new String(Base64.encodeBase64(str.getBytes()));
	}

	/** BASE64解码 */
	public static String decodeBase64(String str) {
		return new String(Base64.decodeBase64(str.getBytes()));
	}

	private static final String algorithm = "DESede";
	
	/**
	 * DESede算法加密
	 * @param keybyte
	 * @param src
	 * @return
	 */
	public static String encrypt(String key, String value) {
		try {
			// 生成密钥
			SecretKey secretKey = new SecretKeySpec(key.getBytes(), algorithm);

			// 加密
			Cipher c1 = Cipher.getInstance(algorithm);
			c1.init(Cipher.ENCRYPT_MODE, secretKey);
			return toHexString(c1.doFinal(value.getBytes()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * DESede算法解密
	 * @param key
	 * @param value
	 * @return
	 */
	public static String decrypt(String key, String value) {
		byte[] encryptByte = hex2Byte(value);
		
		try {
			// 生成密钥
			SecretKey secretKey = new SecretKeySpec(key.getBytes(), algorithm);
			// 解密
			Cipher c1 = Cipher.getInstance(algorithm);
			c1.init(Cipher.DECRYPT_MODE, secretKey);
			
			byte[] clearByte = c1.doFinal(encryptByte);

			return new String(clearByte);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 将byte数组转换成16进制的字符串
	 * @param ba byte数组
	 * @return String 16进制的字符串
	 */
	private static String toHex(byte[] ba) {
		if (ba == null) {
			return null;
		}
	
		int length = ba.length;
		if (length <= 0) {
			return "";
		}
	
		StringBuffer buf = new StringBuffer(length * 2);
		int i;
	
		for (i = 0; i < length; i++) {
			if (((int) ba[i] & 0XFF) < 0X10) {
				buf.append("0");
			}
			buf.append(Long.toString((int) ba[i] & 0XFF, 16));
		}
		return buf.toString();
	}

	private static byte[] hex2Byte(String hex) {// 转换成二进制
		byte[] _byte = new byte[hex.length() / 2];
		for (int i = 0; i < (hex.length() / 2); i++) {
			_byte[i] = (byte) Integer.parseInt(hex.substring(i * 2, i * 2 + 2), 16);
		}

		return _byte;
	}
	
	/**
	 * 将byte数组转换成16进制的字符串
	 * @param ba byte数组
	 * @return String 16进制的字符串
	 */
	private static String toHexString(byte[] md) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		int j = md.length;
		char str[] = new char[j * 2];
		for (int i = 0; i < j; i++) {
			byte byte0 = md[i];
			str[2 * i] = hexDigits[byte0 >>> 4 & 0xf];
			str[i * 2 + 1] = hexDigits[byte0 & 0xf];
		}
		return new String(str);
	}

	public static void main(String[] args) {
		System.out.println(toHexString(new byte[]{8,2}));
		String key = "ADKFDSLKAFDKAGSAGGHDKAFD";
		String value = "Founder123";
		
		String value1 = encrypt(key, value);
		System.out.println(value1);
		
		String value2 = decrypt(key, value1);
		System.out.println(value2);
	}
}
