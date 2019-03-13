package com.founder.xy.column.web;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.web.WebUtil;
import com.founder.xy.column.Column;
import com.founder.xy.commons.InfoHelper;

/**
 * 栏目树的辅助类，形成json。用于栏目管理树和栏目缓存树
 * 
 * @author Gong Lijie
 */
public class ColumnTreeHelper {
	private static String iconColRoot; //栏目树的图标
	private static String iconCol;
	private static String iconDisabled;

	/**
	 * 不判断权限的栏目树的json，比较简单
	 */
	public static String jsonTree(Document[] cols) throws E5Exception {
		JSONArray jsonArr  = new JSONArray();
		
		for (Document col : cols) {
			JSONObject json = jsonOneCol(col);
			jsonArr.add(json);
		}
		return jsonArr.toString();
	}
	/**
	 * 不判断权限的栏目树的json，比较简单,专题栏目导航专用
	 */
	public static String specialJsonTree(Document[] cols) throws E5Exception {
		JSONArray jsonArr  = new JSONArray();

		for (Document col : cols) {
			JSONObject json = jsonOneCol(col,true);
			jsonArr.add(json);
		}
		return jsonArr.toString();
	}
	/**
	 * 不判断权限的栏目树的json，比较简单
	 */
	public static String jsonTree(List<Column> cols) throws E5Exception {
		JSONArray jsonArr  = new JSONArray();
		
		for (Column col : cols) {
			JSONObject json = jsonOneCol(col, false);
			jsonArr.add(json);
		}
		return jsonArr.toString();
	}
	
	/**
	 * 带父节点（无权限）的栏目树的json
	 */
	public static String jsonTreeWithParent(List<Column> roots) throws E5Exception {
		JSONArray jsonArr  = new JSONArray();
		
		if (roots != null) {
			for (Column col : roots) {
				JSONObject json = jsonOneCol(col, true);
				jsonArr.add(json);
			}
		}
		return jsonArr.toString();
	}

	/**
	 * 带父节点（无权限）的栏目树的json,专题栏目导航用
	 */
	public static String specialJsonTreeWithParent(List<Column> roots) throws E5Exception {
		JSONArray jsonArr  = new JSONArray();

		if (roots != null) {
			for (Column col : roots) {
                //检查子栏目是否有linURL
				JSONObject json = jsonOneCol(col, true, true);
				jsonArr.add(json);
			}
		}
		return jsonArr.toString();
	}
	/**
	 * 查找结果的json，格式为[{key,value},{key,value},...]
	 */
	public static String json(Document[] cols) throws E5Exception {
		StringBuilder result = new StringBuilder();
		result.append("[");
	
		int count = 0;
		for (int i = 0; i < cols.length; i++) {
			Document col = cols[i];
			
			if (result.length() > 1) result.append(",");
			result.append("{\"value\":\"")
					.append(InfoHelper.filter4Json(col.getString("col_name")))
					.append("\",\"key\":\"").append(col.getString("col_cascadeID")).
					append("\",\"id\":\"").append(col.getString("SYS_DOCUMENTID")).append("\"}");
			
			//返回个数不超过20个
			if (++count >= 20)
				break;
		}
		result.append("]");
	
		return result.toString();
	}

	/**
	 * 按设置的栏目顺序读出栏目
	 */
	public static List<Column> sortColByOrder(Map<Integer, Column> map) {
		if (map == null || map.isEmpty()) {
			return null;
		}
		List<Column> cols = Arrays.asList(map.values().toArray(new Column[0]));
		Collections.sort(cols, new Comparator<Column>() {
			public int compare(Column me1, Column me2) {
				return new Integer(me1.getOrder()).compareTo(new Integer(me2.getOrder()));
			}
		});
		return cols;
	}

	/**
	 * 初始化栏目图标的路径
	 */
	public synchronized static void initIcon(HttpServletRequest request) {
		if (iconColRoot == null) {
			String root = "/";//WebUtil.getRoot(request);
			iconColRoot = root + "xy/img/home.png";
			iconCol = root + "xy/img/col.png";
			iconDisabled = root + "xy/img/disable.png";
		}
	}

	//处理一个栏目的json转换
	private static JSONObject jsonOneCol(Column col, boolean check) throws E5Exception {
		return jsonOneCol(col,check,false);
	}
	//处理一个栏目的json转换
	private static JSONObject jsonOneCol(Column col, boolean check,boolean isSpecial) throws E5Exception {
		String icon = (col.getParentID() == 0) ? iconColRoot : iconCol;
		if (col.isForbidden()) icon = iconDisabled;

		JSONObject json = new JSONObject();
		json.put("id", col.getId());
		json.put("name", col.getName());
		json.put("title", col.getName() + " [" + col.getId() + "]");
		json.put("casID", col.getCasIDs());
		json.put("casName", col.getCasNames());
		json.put("icon", icon);
        if (check && !col.isEnable())
            json.put("nocheck", "true");//no checkbox

        if(isSpecial){
			String linkUrl = col.getLinkUrl();
			if(linkUrl==null||"".equals(linkUrl))
				json.put("nocheck", "true");
		}
		if (col.isExpandable()) json.put("isParent", "true");//有子节点，可展开

		//子栏目
		if (col.getChildren() != null) {
			JSONArray children = new JSONArray();

			List<Column> cols = col.getChildren();
			for (int i = 0; i < cols.size(); i++) {
				Column son = cols.get(i);
				children.add(jsonOneCol(son,check,isSpecial));
			}
			json.put("children", children);
		}

		return json;
	}

	private static JSONObject jsonOneCol(Document col) {
		return jsonOneCol(col,false);
	}

	private static JSONObject jsonOneCol(Document col,boolean isSpecial) {
		String icon = (col.getInt("col_parentID") == 0) ? iconColRoot : iconCol;
		if (col.getInt("col_status") == 1) icon = iconDisabled;

		String name = col.getString("col_name");

		JSONObject json = new JSONObject();
		json.put("id", col.getDocID());
		json.put("name", name);
		json.put("title", name + " [" + col.getDocID() + "]");
		json.put("casID", col.getString("col_cascadeID"));
		json.put("casName", col.getString("col_cascadeName"));
		json.put("code", col.getString("col_code"));
		json.put("isParent", col.getInt("col_childCount") > 0 ? "true" : "false");
		json.put("icon", icon);
		if(isSpecial) {
            String linkUrl = col.getString("col_linkUrl");
			if(linkUrl==null || "".equals(linkUrl))
				json.put("nocheck", "true");
		}
		return json;
	}
	
	/**
	 * 把我的收藏栏目数组转换成json格式的字符串
	 */
	public static String parseMyCollToJOSN(Column[] columns){
		if(columns==null){
			return null;
		}
		JSONArray array = new JSONArray();
		for (int i=0; i<columns.length; i++) {
			JSONObject json = new JSONObject();
			json.put("id", columns[i].getId());
			json.put("name",columns[i].getName());
			json.put("casID",columns[i].getCasIDs());
			array.add(json);
		}
		return array.toString();
	}
}
