package com.founder.xy.batman.web;



import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class UserEncryptUtil {
	private static final byte[] USER_PASSWORD_KEY = new byte[] { 64, 11, -110,
		11, 107, -2, -12, -83, 88, -118, 91, 84, 47, 84, 76, 98, 110, 84,
		-14, -48, 121, -14, 69, -94 };
private final static String algorithm = "DESede";
private final static SecretKey secretKey1 = new SecretKeySpec(USER_PASSWORD_KEY,
		algorithm);
static {
	Security.addProvider(new com.sun.crypto.provider.SunJCE());
}

private static final UserEncryptUtil instance = new UserEncryptUtil();
private UserEncryptUtil() {}
static UserEncryptUtil getInstance() { return instance; }

public static String encrypt(String plainString) {
	String result = null;
	try {
		Cipher c1 = Cipher.getInstance(algorithm);
		c1.init(Cipher.ENCRYPT_MODE, secretKey1);
		byte[] cipherByte = c1.doFinal(plainString.getBytes());
		result = byte2hex(cipherByte);
	} catch (Exception e) {
	}
	return result;
}
String decrypt(String encryptString) {
	String result = null;
	try {
		byte[] encryptByte = hex2byte(encryptString);

		Cipher c1 = Cipher.getInstance(algorithm);
		c1.init(Cipher.DECRYPT_MODE, secretKey1);
		byte[] clearByte = c1.doFinal(encryptByte);

		result = new String(clearByte);
	} catch (Exception e) {
	}
	return result;
}

private static String byte2hex(byte[] b) {
	String hs = "";
	String stmp = "";
	for (int n = 0; n < b.length; n++) {
		stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
		if (stmp.length() == 1)
			hs = hs + "0" + stmp;
		else
			hs = hs + stmp;
		if (n < b.length - 1)
			hs = hs + "";
	}
	return hs.toUpperCase();
}

private byte[] hex2byte(String hex) {
	byte[] _byte = new byte[hex.length() / 2];
	for (int i = 0; i < (hex.length() / 2); i++) {
		_byte[i] = (byte) Integer.parseInt(hex.substring(i * 2, i * 2 + 2), 16);
	}
	return _byte;
}
}
