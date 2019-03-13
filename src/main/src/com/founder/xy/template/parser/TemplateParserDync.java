package com.founder.xy.template.parser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.doc.Document;
import com.founder.e5.sys.StorageDeviceManager;
import com.founder.e5.sys.SysFactory;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;
import com.founder.xy.template.ComponentObj;

/**
 * @deprecated
 * 模板解析，栏目页解析为jsp
 */
@Component
public class TemplateParserDync extends AbstractParser implements Parser{
	
	@Override
	public boolean parse(Document doc) {
		int templateType = 0;
		
		String templateContent = getTemplateContent(doc.getString("t_file"));
		List<ComponentObj> componentObjList = getComponentObjs(templateContent, doc, templateType);
		saveComponentObjs(componentObjList, doc, templateType);
		refreshCache(componentObjList, doc.getDocID(), templateType);
		if(doc.getInt("t_type")==0){
			generateDynamicPage(doc,templateContent,componentObjList);
		} else {
			generateStaticPage(doc,templateContent,componentObjList);
		}
		return true;
	}
	//动态栏目页：形成jsp
	private void generateDynamicPage(Document doc,String templateContent,List<ComponentObj> componentObjList){
		StringBuffer pageBuffer = new StringBuffer();
		pageBuffer.append("<%@page import=\"com.founder.xy.jpublish.page.ColParam\"%>\n");
		pageBuffer.append("<%@page import=\"com.founder.xy.jpublish.page.PublishReader\"%>\n\n");
		
		pageBuffer.append("<%ColParam param = PublishReader.getParam(request,<XY-PARSE-COLLIBID/>,<XY-PARSE-COLID/>);%>\n");
		
		for (ComponentObj componentObj : componentObjList) {
			templateContent = StringUtils.replaceOnce(templateContent,componentObj.getAllcode(),
					"<%out.print(PublishReader.read(param," + componentObj.getCoID() + "));%>");
		}
		
		pageBuffer.append(templateContent);
		
		String path = doc.getString("t_file")+".parsed";
		int pos = path.indexOf(";");
		String deviceName = path.substring(0, pos);
		String savePath = path.substring(pos + 1);
		InputStream in = null;
		try {
			in = new ByteArrayInputStream(pageBuffer.toString().getBytes("UTF-8"));
			StorageDeviceManager sdManager = SysFactory.getStorageDeviceManager();
			sdManager.write(deviceName, savePath, in);
			RedisManager.hset(RedisKey.TEMPLATE_FILE_KEY, doc.getDocID(), pageBuffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(in);
		}
	}
	
	void generateStaticPage(Document doc,String templateContent,List<ComponentObj> componentObjList){
		StringBuffer pageBuffer = new StringBuffer();
		for(ComponentObj componentObj : componentObjList){
			templateContent = StringUtils.replaceOnce(templateContent,componentObj.getAllcode(),"<XY-PARSE coid=\""+componentObj.getCoID()+"\"/>");
		}
		pageBuffer.append(templateContent);
		String path = doc.getString("t_file")+".parsed";
		int pos = path.indexOf(";");
		String deviceName = path.substring(0, pos);
		String savePath = path.substring(pos + 1);
		InputStream in = null;
		try {
			in = new ByteArrayInputStream(pageBuffer.toString().getBytes("UTF-8"));
			StorageDeviceManager sdManager = SysFactory.getStorageDeviceManager();
			sdManager.write(deviceName, savePath, in);
			RedisManager.hset(RedisKey.TEMPLATE_FILE_KEY, doc.getDocID(), pageBuffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(in);
		}
	}
	
	private String getTemplateContent(String path){
		String templateContent = "";
		int pos = path.indexOf(";");
		//类型
		String deviceName = path.substring(0, pos);
		//保存路径
		String savePath = path.substring(pos + 1);
		InputStream in = null;
		StorageDeviceManager sdManager = SysFactory.getStorageDeviceManager();
		try {
			in = sdManager.read(deviceName, savePath);
			templateContent = IOUtils.toString(in,"UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(in);
		}
		return templateContent;
	}
}
