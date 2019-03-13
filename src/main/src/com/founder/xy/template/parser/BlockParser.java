package com.founder.xy.template.parser;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.xy.template.ComponentObj;

@Component
public class BlockParser extends AbstractParser implements Parser{

	@Override
	public boolean parse(Document doc) {
		int templateType = 1; //0：模板，1：区块
		
		String templateContent = doc.getString("b_template");
		
		List<ComponentObj> coList = getComponentObjs(templateContent, doc, templateType);
		
		saveComponentObjs(coList, doc, templateType);
		
		//组件实例放入redis
		refreshCache(coList, doc.getDocID(), templateType);
		
		generateStaticPage(doc,templateContent,coList);
		
		return true;
	}
	
	//解析后的区块模板内容放在redis里
	private void generateStaticPage(Document doc,String content,List<ComponentObj> componentObjList){
		//替换模板代码为一个一个组件实例的引用
		for(ComponentObj co : componentObjList){
			content = StringUtils.replaceOnce(content,
					co.getAllcode(), "<XY-PARSE coid=\"" + co.getCoID() + "\"/>");
		}
		
		try {
			//RedisManager.hset(RedisKey.BLOCK_FILE_KEY, doc.getDocID(), content);
			
			//保存
			doc.set("b_templateParsed", content);
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			docManager.save(doc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}