package com.founder.xy.template.parser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.sys.StorageDeviceManager;
import com.founder.e5.sys.SysFactory;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;
import com.founder.xy.template.ComponentObj;

@Component
public class TemplateParser extends AbstractParser implements Parser{
	
	@Override
	public boolean parse(Document doc) {
		int templateType = 0; //0：模板，1：区块
		String newrltPath = null;
		Document tdoc = null;
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		int docLibID = LibHelper.getTemplateLibID();
		try {
			tdoc = docManager.get(docLibID,  doc.getDocID());
		} catch (E5Exception e) {
			System.out.println("---模板为空：TemplateParser.java：42---");
		}
		System.out.println("---TemplateParser.java：42---siteID："+tdoc.getString("t_siteID"));
		String reliPath = formatDateByCreate(tdoc) + tdoc.getDocID();
		System.out.println("---TemplateParser.java：42---reliPath："+reliPath);
		String siteID = tdoc.getString("t_siteID");
		if(!StringUtils.isBlank(siteID)){
			String[] siteDir = readSiteInfo(Integer.parseInt(siteID));
			newrltPath = siteDir[2] +"/templateRes/" + reliPath + "/" ;
		}
		String templateContent = getTemplateContent(doc.getString("t_file"));
		
		//处理文件中相对路径
		if(!StringUtils.isBlank(templateContent) && !StringUtils.isBlank(reliPath)){//(src|SRC|href|HREF)(=|\s=|=\s|\s=\s)(\"|\'|\"\s|\'\s)(\.\.|\.)
			templateContent = replaceSrc(templateContent, newrltPath);
			templateContent = replaceHref(templateContent, newrltPath);
			templateContent = replaceURL(templateContent, newrltPath);
			templateContent = replaceBackGround(templateContent, newrltPath);
			
		}
		
		List<ComponentObj> coList = getComponentObjs(templateContent, doc, templateType);
		
		saveComponentObjs(coList, doc, templateType);
		
		refreshCache(coList, doc.getDocID(), templateType);
		
		generateStaticPage(doc,templateContent,coList);
		
		return true;
	}

	private String formatDateByCreate(Document doc) {
		Timestamp createdTime = doc.getTimestamp("SYS_CREATED");
		String date = DateUtils.format(createdTime, "yyyyMM/dd/");  
		return date;
	}

	private String[] readSiteInfo(int siteID) {
		return InfoHelper.readSiteInfo(siteID);
	}

	//解析后的模板文件放在同一个目录下，以.parsed为后缀；并放入redis中
	private void generateStaticPage(Document doc,String content,List<ComponentObj> componentObjList){
		//替换模板代码为一个一个组件实例的引用
		for(ComponentObj co : componentObjList){
			content = StringUtils.replaceOnce(content,
					co.getAllcode(), "<XY-PARSE coid=\"" + co.getCoID() + "\"/>");
		}
		
		String path = doc.getString("t_file")+".parsed";
		int pos = path.indexOf(";");
		String deviceName = path.substring(0, pos);
		String savePath = path.substring(pos + 1);
		
		InputStream in = null;
		try {
			//存储解析后的模板文件
			in = new ByteArrayInputStream(content.getBytes("UTF-8"));
			
			StorageDeviceManager sdManager = SysFactory.getStorageDeviceManager();
			sdManager.write(deviceName, savePath, in);
			
			//放到redis里
			RedisManager.hset(RedisKey.TEMPLATE_FILE_KEY, doc.getDocID(), content);
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
			//为防止存储设备是编码的，先解码
			deviceName = URLDecoder.decode(deviceName, "UTF-8");
			
			in = sdManager.read(deviceName, savePath);
			templateContent = IOUtils.toString(in,"UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(in);
		}
		return templateContent;
	}

	public void parse(Document doc, String[] siteDir, String reliPath) {
		System.out.println("---TemplateParser.java：145---开始解析模板：");
		//获取站点和路径失败时再请求一次
		if(StringUtils.isBlank(reliPath) || siteDir.length <= 0){
			reliPath = formatDateByCreate(doc) + doc.getDocID();
			String siteID = doc.getString("t_siteID");
			if(!StringUtils.isBlank(siteID)){
				siteDir = readSiteInfo(Integer.parseInt(siteID));
			}
			System.out.println("---TemplateParser.java：145---siteID：" + siteID);
		}
		int templateType = 0; //0：模板，1：区块
		String newrltPath = null;
		newrltPath = siteDir[2] +"/templateRes/" + reliPath + "/" ;
		System.out.println("---TemplateParser.java：151---newrltPath：" + newrltPath);
		
		String templateContent = getTemplateContent(doc.getString("t_file"));
		
		//处理文件中相对路径
		if(!StringUtils.isBlank(templateContent) && !StringUtils.isBlank(reliPath)){//(src|SRC|href|HREF)(=|\s=|=\s|\s=\s)(\"|\'|\"\s|\'\s)(\.\.|\.)
			templateContent = replaceSrc(templateContent, newrltPath);
			templateContent = replaceHref(templateContent, newrltPath);
			templateContent = replaceURL(templateContent, newrltPath);
			templateContent = replaceBackGround(templateContent, newrltPath);
		}
		
		List<ComponentObj> coList = getComponentObjs(templateContent, doc, templateType);
		
		saveComponentObjs(coList, doc, templateType);
		
		refreshCache(coList, doc.getDocID(), templateType);
		
		generateStaticPage(doc,templateContent,coList);
		
	}

	private String replaceBackGround(String templateContent, String newrltPath) {
		System.out.println("---TemplateParser.java：173---开始解析替换路径");
		newrltPath = "background=\""+newrltPath;//.substring(0, newrltPath.lastIndexOf("/resource"))
		 String regEx="background\\s?=\\s?[\"']\\./";  
	        Pattern p = Pattern.compile(regEx);  
	        Matcher m = p.matcher(templateContent);  
	        return m.replaceAll(newrltPath);
		// return templateContent.replaceAll(regEx, newrltPath);
	}

	private String replaceSrc(String templateContent, String newrltPath) {
		newrltPath = "src=\""+newrltPath;//.substring(0, newrltPath.lastIndexOf("/resource"))
		 String regEx="src\\s?=\\s?[\"']\\./";  
	        Pattern p = Pattern.compile(regEx);  
	        Matcher m = p.matcher(templateContent);  
	        return m.replaceAll(newrltPath);
		// return templateContent.replaceAll(regEx, newrltPath);
	}
	
	private String replaceHref(String templateContent, String newrltPath) {
		newrltPath = "href=\""+newrltPath;//.substring(0, newrltPath.lastIndexOf("/resource"))
		 String regEx="href\\s?=\\s?[\"']\\./";  
	        Pattern p = Pattern.compile(regEx);  
	        Matcher m = p.matcher(templateContent);  
	        return m.replaceAll(newrltPath);
		// return templateContent.replaceAll(regEx, newrltPath);
	}
	
	private String replaceURL(String templateContent, String newrltPath) {
		newrltPath = "url("+newrltPath;//.substring(0, newrltPath.lastIndexOf("/resource"))
		 String regEx="url\\s?\\(\\s?\\./";  
	        Pattern p = Pattern.compile(regEx);  
	        Matcher m = p.matcher(templateContent);  
	        return m.replaceAll(newrltPath);
	}
}
