package com.founder.xy.jpublish.template.component;

import org.json.JSONObject;

import com.founder.xy.jpublish.ColParam;

public class TagListComponent extends AbstractComponent implements Component{
	
	public TagListComponent(ColParam param, JSONObject comJson){
		super(comJson);
	}

	@Override
	public String getComponentResult() {
		return null;
	}

	@Override
	protected void getComponentData() {
		//String tag = dataJSON.getString("tag");
//		ModifiableSolrParams params = new ModifiableSolrParams();
//		params.set("q", "tag:"+tag);
//		params.set("sort", "pubtime desc");
	}
}
