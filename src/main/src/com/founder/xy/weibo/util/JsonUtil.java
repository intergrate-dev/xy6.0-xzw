package com.founder.xy.weibo.util;

import com.founder.e5.commons.StringUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class JsonUtil
{
	/**
	 * �ַ�ת����json����
	 */
	public static JSONObject getJson(String value) {
		if (!StringUtils.isBlank(value)) {
			return JSONObject.fromObject(value);
		} else
			return null;
	}
	/**
	 * ��json�ж�һ������
	 */
	public static JSONArray getJsonArray(String value) {
		try {
			if (!StringUtils.isBlank(value)) {
				return JSONArray.fromObject(value);
			} else
				return null;
		} catch (Exception e) {
			return null;
		}
	}
	/**
	 * ��json�ж�һ������
	 */
	public static JSONArray getJsonArray(JSONObject json, String key) {
		try {
			if (json.containsKey(key))
				return json.getJSONArray(key);
			else
				return null;
		} catch (Exception e) {
			return null;
		}
	}
	/**
	 * ��json�ж�һ������
	 */
	public static JSONObject getJsonObject(JSONObject json, String key) {
		if (json.containsKey(key))
			return json.getJSONObject(key);
		else
			return null;
	}

	/**
	 * ��json�ж�һ������
	 */
	public static long getLong(JSONObject json, String key) {
		try {
			String value = getString(json, key);
			return (StringUtils.isBlank(value)) ? 0 : Long.parseLong(value);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * ��json�ж�һ������
	 */
	public static int getInt(JSONObject json, String key) {
		try {
			String value = getString(json, key);
			return (StringUtils.isBlank(value)) ? 0 : Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	/**
	 * ��json�ж�string
	 */
	public static String getString(JSONObject json, String key) {
		if (json.containsKey(key))
			return json.getString(key);
		else
			return null;
	}

}
