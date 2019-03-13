package com.founder.xy.jpublish.template.component;

import org.json.JSONObject;

/**
 * 组件：区块稿件列表
 * 
<FOUNDER-XY type="blockarticlelist">
<ul>
<#list articles as article>
	<li><a href=" ${article.url}">${article.title}</a></li>
</#list>
</ul>
</FOUNDER-XY>
 * @author Gong Lijie
 */
public class BlockArticleComponent extends AbstractComponent implements Component{
	
	public BlockArticleComponent(JSONObject comJson){
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
