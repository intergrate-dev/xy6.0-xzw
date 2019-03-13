package com.founder.xy.jpublish.template;

import com.founder.xy.jpublish.template.component.*;
import org.json.JSONObject;

import com.founder.xy.commons.InfoHelper;
import com.founder.xy.jpublish.ColParam;
import com.founder.xy.template.ComponentObj;

public class ComponentFactory {
	public static Component newComponent(ColParam param,String componentObj){
		return newComponent(param, componentObj, false);
	}
	
	public static Component newComponent(ColParam param,String componentObj, boolean preview){
		JSONObject comJson = new JSONObject(componentObj);
		Component component = null;
		
		switch(comJson.getInt("type")){
		case ComponentObj.TYPE_COLUMNLIST:
			component = new ColumnListComponent(param,comJson);
			break;
		case ComponentObj.TYPE_ARTICLELIST:
			component = new ArticleListComponent(param,comJson, preview);
			break;
		case ComponentObj.TYPE_ARTICLE:
			component = new ArticleComponent(param,comJson,preview);
			break;
		case ComponentObj.TYPE_POSITION:
			component = new PositionComponent(param,comJson);
			break;
		case ComponentObj.TYPE_ARTICLELISTPAGE:
			if ("是".equals(InfoHelper.getConfig("发布服务", "动态发布")))
				component = new ArticleListPageComDync(param,comJson, preview);
			else
				component = new ArticleListPageComponent(param,comJson, preview);
			break;
		case ComponentObj.TYPE_BLOCK:
			component = new BlockComponent(param, comJson);
			break;
		case ComponentObj.TYPE_BLOCKARTICLE:
			component = new BlockArticleComponent(comJson);
			break;
		case ComponentObj.TYPE_PREVNEXTARTICLE:
			component = new PrevNextComponent(param,comJson);
			break;
		case ComponentObj.TYPE_PAPERLAYOUT:
			component = new PaperLayoutComponent(param,comJson);
			break;
		case ComponentObj.TYPE_PAPERARTICLE:
			component = new PaperArticleComponent(param,comJson);
			break;
		case ComponentObj.TYPE_PAPERHOME:
			component = new PaperHomeComponent(param,comJson);
			break;
		case ComponentObj.TYPE_MAGAZINE:
			component = new MagazineComponent(param,comJson);
			break;
		case ComponentObj.TYPE_MAGAZINEARTICLE:
			component = new MagazineArticleComponent(param,comJson);
			break;
		case ComponentObj.TYPE_ADVCOLUMNLIST:
			component = new AdvColumnListComponent(param,comJson,preview);
			break;
		}
		return component;
	}
}
