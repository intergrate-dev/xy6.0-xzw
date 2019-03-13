package com.founder.xy.jpublish.template.component;

import org.json.JSONObject;

import com.founder.xy.jpublish.ColParam;

/**
 * 组件：报纸稿件，只包含一个PaperArticle对象
 * @author Gong Lijie
 */
public class PaperArticleComponent extends AbstractComponent implements Component{
	
	public PaperArticleComponent(ColParam param,JSONObject comJson){
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
