package com.founder.xy.jpublish.template.component;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.xy.column.Column;
import com.founder.xy.column.ColumnReader;
import com.founder.xy.jpublish.ColParam;
import com.founder.xy.jpublish.JsonHelper;

/**
 * 组件：栏目列表
 * @author Gong Lijie
 */
public class ColumnListComponent extends AbstractComponent implements Component{
	
	public ColumnListComponent(ColParam param,JSONObject comJson){
		super(comJson);
		this.param = param;
	}

	@Override
	public String getComponentResult() throws Exception {
		getComponentData();
		
		return process();
	}

	@Override
	protected void getComponentData() {
		JSONArray columnid = JsonHelper.getArray(dataJSON, "columnid");
		
		List<Column> columns = null;
		
		if (columnid != null && columnid.length() > 1){
			columns = new ArrayList<Column>();
			Column column = null;
			ColumnReader columnReader = (ColumnReader)Context.getBean("columnReader");
			try {
				for (int i = 0; i < columnid.length(); i++) {
					column = columnReader.get(param.getColLibID(), columnid.getLong(i));
					if (column != null) columns.add(column);
				}
			} catch (E5Exception e) {
				e.printStackTrace();
			}
		} else {
			//模板中没指定组件的栏目ID
			columns = getColumnsByDefault(dataJSON, columnid);
		}
		
		//去掉被禁用的栏目
		removeFobidden(columns);
		
		//按发布时间取路径
		setColumnUrl(columns);

		componentData.put("columns", columns);
	}
	
	//模板中没指定组件的栏目ID时，取栏目列表
	private List<Column> getColumnsByDefault(JSONObject dataJSON, JSONArray columnid) {
		ColumnReader columnReader = (ColumnReader)Context.getBean("columnReader");
		
		String columntype = JsonHelper.getString(dataJSON, "columntype");
		
		Column column = null;
		try {
			if (columnid == null || columnid.length() == 0 || columnid.getLong(0) == 0) {
				column = columnReader.get(param.getColLibID(), param.getColID());
			} else {
				column = columnReader.get(param.getColLibID(), columnid.getLong(0));
			}
			if (column == null) return null;
			
			//按数据类型取不同的栏目
			if (StringUtils.isBlank(columntype) || "self".equals(columntype)) {
				List<Column> columns = new ArrayList<Column>();
				columns.add(column);
				return columns;
			} else if ("son".equals(columntype)) {
				return columnReader.getSub(param.getColLibID(), column.getId());
			} else {
				//brother
				if (column.getParentID() > 0)
					return columnReader.getSub(param.getColLibID(), column.getParentID());
				else
					return columnReader.getRoot(column.getSiteID(), param.getColLibID(), column.getChannel());
			}
		} catch (E5Exception e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	/**
	 * 去掉被禁用的栏目
	 * @param columns
	 */
	private void removeFobidden(List<Column> columns) {
		if (columns == null) return;

		int i = 0;
		while (i < columns.size()) {
			Column col = columns.get(i);
			if (col.isForbidden())
				columns.remove(i);
			else
				i++;
		}
	}
}
