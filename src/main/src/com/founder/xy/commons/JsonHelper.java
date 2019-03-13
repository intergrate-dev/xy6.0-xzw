package com.founder.xy.commons;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.founder.e5.commons.StringUtils;

/**
 * Json辅助
 * @author Gong Lijie
 */
public class JsonHelper {

	/**
	 * 字符串转换成json对象
	 */
	public static JSONObject getJson(String value) {
		if (!StringUtils.isBlank(value)) {
			return JSONObject.fromObject(value);
		} else
			return null;
	}
	/**
	 * 字符串转换成JSONArray对象
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
	 * 从json中读一个JSONArray参数
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
	 * 从json中读一个参数
	 */
	public static JSONObject getJsonObject(JSONObject json, String key) {
		if (json.containsKey(key))
			return json.getJSONObject(key);
		else
			return null;
	}

	/**
	 * 从json中读一个参数
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
	 * 从json中读一个参数
	 */
	public static int getInt(JSONObject json, String key) {
		return getInt(json, key, 0);
	}

	/**
	 * 从json中读一个参数
	 */
	public static int getInt(JSONObject json, String key, int defaultValue) {
		try {
			String value = getString(json, key);
			return (StringUtils.isBlank(value)) ? defaultValue : Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	/**
	 * 从json中读string
	 */
	public static String getString(JSONObject json, String key) {
		if (json.containsKey(key))
			return json.getString(key);
		else
			return null;
	}
}
