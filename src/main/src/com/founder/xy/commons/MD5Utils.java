package com.founder.xy.commons;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

public class MD5Utils {

	public static void main(String[] args) throws IOException {

	}

	/**
	 * 根据文件名获得文件MD5
	 * 
	 * @param fileName
	 * @return
	 */
	public static String fileNameToMD5(String fileName) {
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(fileName);
			return streamToMD5(inputStream);
		} catch (Exception e) {
			return null;
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 根据输入流获得文件MD5
	 * 
	 * @param inputStream
	 * @return
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

	/**
	 * 获得的MD5码
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static String encryptByMD5(byte[] data) throws Exception {
		if (data == null)
			return null;

		MessageDigest digest = null;
		digest = MessageDigest.getInstance("MD5");
		digest.update(data);

		return toHex(digest.digest());
	}

	/**
	 * 将byte数组转换成16进制的字符串
	 * 
	 * @param ba
	 *            byte数组
	 * 
	 * @return String 16进制的字符串
	 */
	public static final String toHex(byte[] ba) {
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

	/**
	 * 获取str字符串的MD5值
	 * 
	 * @param str
	 * @return
	 */
	public static String getMD5(String str) {
		String md5Info = "";
		byte[] content = null;
		try {
			content = str.getBytes();
			md5Info = encryptByMD5(content);
		} catch (Exception ex) {
		} finally {

			content = null;
		}
		return md5Info;
	}

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

}
