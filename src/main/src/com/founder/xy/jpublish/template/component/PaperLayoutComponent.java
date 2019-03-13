package com.founder.xy.jpublish.template.component;

import org.json.JSONObject;

import com.founder.xy.jpublish.ColParam;

/**
 * 组件：报纸版面，只包含一个PaperLayout对象
 * @author Gong Lijie
 */
public class PaperLayoutComponent extends AbstractComponent implements Component{
	
	public PaperLayoutComponent(ColParam param,JSONObject comJson){
		super(comJson);
	}

	@Override
	public String getComponentResult() throws Exception {
		return process();
	}

	@Override
	protected void getComponentData() {
	}
}
