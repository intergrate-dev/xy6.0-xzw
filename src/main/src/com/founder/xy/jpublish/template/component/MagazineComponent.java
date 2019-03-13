package com.founder.xy.jpublish.template.component;

import org.json.JSONObject;

import com.founder.xy.jpublish.ColParam;

/**
 * 组件：期刊一期，包含columns对象（MagazineColumn列表）、magazine对象（期刊名、刊期）
 * @author Gong Lijie
 */
public class MagazineComponent extends AbstractComponent implements Component{
	
	public MagazineComponent(ColParam param,JSONObject comJson){
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
