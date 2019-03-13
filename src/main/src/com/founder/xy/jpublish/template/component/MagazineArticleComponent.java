package com.founder.xy.jpublish.template.component;

import org.json.JSONObject;

import com.founder.xy.jpublish.ColParam;

/**
 * 组件：期刊稿件，只包含一个MagazineArticle对象
 * @author Gong Lijie
 */
public class MagazineArticleComponent extends AbstractComponent implements Component{
	
	public MagazineArticleComponent(ColParam param,JSONObject comJson){
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
