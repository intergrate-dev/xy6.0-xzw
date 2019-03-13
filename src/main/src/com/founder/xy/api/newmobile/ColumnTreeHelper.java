package com.founder.xy.api.newmobile;

import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.xy.column.Column;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.*;

/**
 * Created by yu.feng on 2017/12/19.
 */
public class ColumnTreeHelper {

    /**
     * 不判断权限的栏目树的json，比较简单
     */
    public static JSONArray jsonTree(Document[] cols) throws E5Exception {
        JSONArray jsonArr  = new JSONArray();

        for (Document col : cols) {
            JSONObject json = jsonOneCol(col);
            jsonArr.add(json);
        }
        return jsonArr;
    }

    private static JSONObject jsonOneCol(Document col) {
        return jsonOneCol(col,false);
    }

    private static JSONObject jsonOneCol(Document col,boolean isSpecial) {

        JSONObject json = new JSONObject();

        json.put("columnID", col.getDocID());
        json.put("columnName", col.getString("col_name"));
        json.put("isParent", col.getInt("col_childCount") > 0 ? "true" : "false");
        json.put("parentID", col.getString("col_parentID"));

        if(isSpecial) {
            String linkUrl = col.getString("col_linkUrl");
            if(linkUrl==null || "".equals(linkUrl))
                json.put("noCheck", "true");
        }else {
            json.put("noCheck", "false");
        }

        json.put("children","[]");
        return json;
    }

    /**
     * 带父节点（无权限）的栏目树的json
     */
    public static JSONArray jsonTreeWithParent(List<Column> roots) throws E5Exception {
        JSONArray jsonArr  = new JSONArray();

        if (roots != null) {
            for (Column col : roots) {
                JSONObject json = jsonOneCol(col, true);
                jsonArr.add(json);
            }
        }
        return jsonArr;
    }

    //处理一个栏目的json转换
    private static JSONObject jsonOneCol(Column col, boolean check) throws E5Exception {
        return jsonOneCol(col,check,false);
    }

    //处理一个栏目的json转换
    private static JSONObject jsonOneCol(Column col, boolean check,boolean isSpecial) throws E5Exception {

        JSONObject json = new JSONObject();
        json.put("columnID", col.getId());
        json.put("columnName", col.getName());
        json.put("isParent", col.isExpandable());
        json.put("parentID", col.getParentID());
        if (check && !col.isEnable()){
            json.put("noCheck", "true");//no checkbox
        }else{
            json.put("noCheck", "false");
        }

        if(isSpecial){
            String linkUrl = col.getLinkUrl();
            if(linkUrl==null||"".equals(linkUrl))
                json.put("noCheck", "true");
        }
//        if (col.isExpandable()) json.put("isParent", "true");//有子节点，可展开

        //子栏目
        if (col.getChildren() != null) {
            JSONArray children = new JSONArray();

            List<Column> cols = col.getChildren();
            for (int i = 0; i < cols.size(); i++) {
                Column son = cols.get(i);
                children.add(jsonOneCol(son,check,isSpecial));
            }
            json.put("children", children);
        }else {
            json.put("children","[]");
        }

        return json;
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
     * 不判断权限的栏目树的json，比较简单
     */
    public static JSONArray jsonTree(List<Column> cols) throws E5Exception {
        JSONArray jsonArr  = new JSONArray();

        for (Column col : cols) {
            JSONObject json = jsonOneCol(col, false);
            jsonArr.add(json);
        }
        return jsonArr;
    }

}
