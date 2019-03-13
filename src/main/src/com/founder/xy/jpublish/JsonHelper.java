package com.founder.xy.jpublish;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 读json中的数据
 * @author Gong Lijie
 */
public class JsonHelper {
	
	public static int getInt(JSONObject data, String key) {
		return getInt(data, key, 0);
	}
	public static int getInt(JSONObject data, String key, int defaultValue) {
		try {
			if (data != null && data.has(key))
				return data.getInt(key);
		} catch (JSONException e) {
		}
		return defaultValue;
	}

	public static long getLong(JSONObject data, String key) {
		return getLong(data, key, 0);
	}
	public static long getLong(JSONObject data, String key, long defaultValue) {
		try {
			if (data != null && data.has(key))
				return data.getLong(key);
		} catch (JSONException e) {
		}
		return defaultValue;
	}

	public static String getString(JSONObject data, String key) {
		return getString(data, key, null);
	}
	public static String getString(JSONObject data, String key, String defaultValue) {
		try {
			if (data != null && data.has(key))
				return data.getString(key);
		} catch (JSONException e) {
		}
		return defaultValue;
	}
	public static JSONArray getArray(JSONObject data, String key) {
		try {
			if (data != null && data.has(key))
				return data.getJSONArray(key);
		} catch (JSONException e) {
		}
		return null;
	}
}
