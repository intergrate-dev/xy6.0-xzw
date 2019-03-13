package com.founder.xy.jpublish.template.component;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.xy.column.Column;
import com.founder.xy.column.ColumnReader;
import com.founder.xy.jpublish.ColParam;

/**
 * 组件：当前位置
 * @author Gong Lijie
 */
public class PositionComponent extends AbstractComponent implements Component{
	
	public PositionComponent(ColParam param, JSONObject comJson){
		super(comJson);
		
		this.param = param;
	}

	@Override
	public String getComponentResult() throws Exception {
		getComponentData();
		
		String result = process();
		return result;
	}

	@Override
	protected void getComponentData() {
		ColumnReader columnReader = (ColumnReader)Context.getBean("columnReader");
		List<Column> columns = new ArrayList<Column>();
		try {
			Column column = columnReader.get(param.getColLibID(), param.getColID());
			//父路径
			long[] colIDs = StringUtils.getLongArray(column.getCasIDs(), "~");
			
			//根本组件渠道、是否显示在导航栏目中判断
			int channel = super.getChannel();
			if (colIDs.length == 1) {
				if( (0 == channel && column.getIsIncom() == 1) || (1 == channel && column.getIsIncomPad() == 1))  {
					columns.add(column);
				}
			} else {
				for (long colID : colIDs) {
					Column col = columnReader.get(param.getColLibID(), colID);
					if( (0 == channel && col.getIsIncom() == 1) || (1 == channel && col.getIsIncomPad() == 1))  {
						columns.add(col);
					}
				}
			}
			
			//按稿件的发布时间取路径
			setColumnUrl(columns);
			
		} catch (E5Exception e) {
			e.printStackTrace();
		}
		componentData.put("columns", columns);
	}
}
