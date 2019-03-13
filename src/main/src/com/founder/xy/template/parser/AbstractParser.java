package com.founder.xy.template.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

import org.json.JSONArray;
import org.json.JSONObject;

import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.db.DBSession;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.doc.util.E5docHelper;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;
import com.founder.xy.template.ComponentObj;

public abstract class AbstractParser implements Parser{
	
	/**
	 * 解析出组件实例
	 */
	protected List<ComponentObj> getComponentObjs(String content, Document doc, int templateType) {
		List<ComponentObj> componentObjList = new ArrayList<ComponentObj>();
		
		if (!StringUtils.isBlank(content)) {
			//所在模板的发布渠道
			int channel = doc.getInt("t_channel");
			if (channel < 0) channel = doc.getInt("b_channel");
			
			Matcher contentMatcher = PARSER_PATTERN.matcher(content);
			ComponentObj componentObj = null;
			while(contentMatcher.find()){
				componentObj = new ComponentObj();
				componentObj.setTemplateID(doc.getDocID()); //组件实例对应的模板ID或区块ID
				componentObj.setTemplateType(templateType);
				
				String type = contentMatcher.group(1);
				if (type != null) type = type.toLowerCase();
				componentObj.setType(type); //组件实例的类型
				
				String data = contentMatcher.group(2);
				if (StringUtils.isBlank(data)) data = "{}";
				
				JSONObject json = new JSONObject(data);
				json.put("channel", channel);
				data = json.toString();
				
				componentObj.setData(data); //组件实例的参数定义
				componentObj.setCode(contentMatcher.group(3)); //组件实例内包含的代码
				componentObj.setAllcode(contentMatcher.group(0));
				
				componentObjList.add(componentObj);
			}
		}
		return componentObjList;
	}
	
	/**
	 * 通过数据库查询出组件实例
	 * @param templateID
	 * @param templateType
	 * @return
	 */
	public List<ComponentObj> getComponentObjs(long templateID, int templateType){
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] coDocs = null;
		List<ComponentObj> componentObjList = new ArrayList<ComponentObj>();
		try{
			coDocs = docManager.find(LibHelper.getComponentObjLibID(), 
					"co_templateID=? and co_templateType=?", new Object[]{templateID, templateType});
			for(Document doc : coDocs){
				ComponentObj co = new ComponentObj(doc);
				componentObjList.add(co);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return componentObjList;
	}
	
	
	private JSONArray getColumnid(String data){
		JSONObject dataJson = new JSONObject(data);
		return dataJson.getJSONArray("columnid");
	}
	
	/**
	 * 存入数据库
	 */
	protected void saveComponentObjs(List<ComponentObj> componentObjList, Document doc, int templateType){
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DBSession conn = null;
		try {
			conn = E5docHelper.getDBSession(LibHelper.getComponentObjLibID());
			conn.beginTransaction();
			
			//清空该模板下的组件记录
			InfoHelper.executeUpdate(DELETE_COMPONENTOBJS, new Object[]{doc.getDocID(), templateType}, conn);
			
			Document doc1 ;
			Set<Long> columnidSet = new HashSet<>();
			JSONArray columnids;
			for (int i = 0, size = componentObjList.size(); i < size; i++) {
				ComponentObj componentObj = componentObjList.get(i);
				//提取栏目模板中稿件列表和稿件分页列表组件中的columnid
				if(templateType == 0){
					if(ComponentObj.TYPE_ARTICLELIST == componentObj.getType() || ComponentObj.TYPE_ARTICLELISTPAGE == componentObj.getType()){
						columnids = getColumnid(componentObj.getData());
						for (int j = 0; j <columnids.length() ; j++) {
							if(columnids.getLong(j)>0){
								columnidSet.add(columnids.getLong(j));
							}
						}
					}
				}
				doc1 = componentObj.covert2Document();
				
				docManager.save(doc1, conn);
				
				componentObj.setCoID(doc1.getDocID());
			}
			
			//修改模板的相关栏目
			StringBuffer columnidBuffer = new StringBuffer();
			if(columnidSet.size() > 0){
				for(long id : columnidSet){
					if (columnidBuffer.length() > 0) columnidBuffer.append(",");
					columnidBuffer.append(id);
				}
			}
			InfoHelper.executeUpdate(UPDATE_TEMPLATE, new Object[]{columnidBuffer.toString(),doc.getDocID()}, conn);
			//提交transaction
			conn.commitTransaction();
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			e.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
	}
	
	public void refreshCache(List<ComponentObj> coList, long templateID, int templateType){
		String tID = String.valueOf(templateID);
		String key = (templateType == 0) ? RedisKey.TEMPLATE_CO_KEY : RedisKey.BLOCK_CO_KEY;
		
		//先把模板原来的组件实例从Redis缓存中去掉
		String comID = RedisManager.hget(key, tID);
		if (comID != null){
			RedisManager.hclear(RedisKey.CO_KEY, comID.split(","));
		}
		
		//添加新的组件实例的缓存
		StringBuffer comIDs = new StringBuffer();
		for(ComponentObj co : coList){
			RedisManager.hset(RedisKey.CO_KEY, co.getCoID(), co.covert2Json());
			
			comIDs.append(",").append(co.getCoID());
		}
		
		if (coList.size() > 0){
			RedisManager.hset(key, tID, comIDs.substring(1));
		}else{
			RedisManager.hclear(key, tID);
		}
	}
}
