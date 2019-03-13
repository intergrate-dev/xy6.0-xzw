package com.founder.xy.jpublish.template.component;

import org.json.JSONObject;

import com.founder.xy.jpublish.ColParam;

/**
 * 组件：报纸首页，只包含一个PaperLayout列表
 * @author Gong Lijie
 */
public class PaperHomeComponent extends AbstractComponent implements Component{
	
	public PaperHomeComponent(ColParam param,JSONObject comJson){
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
