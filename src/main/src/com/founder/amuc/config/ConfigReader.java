package com.founder.amuc.config;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.founder.amuc.commons.InfoHelper;
import com.founder.e5.commons.ResourceMgr;

/**
 * 解析app-config
 * 
 * 统一对WEB-INF/classes/app-template/app-config.xml的读取。
 * 配置文件若有改动，需重启应用服务器。
 * 
 * @author Gong Lijie
 */
@SuppressWarnings("unchecked")
public class ConfigReader {
	/**TAB*/
	private static List<Tab> tabList;
	/**View Tabs*/
	private static Hashtable<Integer, List<SubTab>> viewTabs;
	
	//-----------对外接口-----------------------------
	
	//读主TAB
	public static List<Tab> getTabs() {
		return tabList;
	}
	
	//读一个文档类型的细览菜单
	public static List<SubTab> getViewTabs(int docTypeID) {
		return viewTabs.get(docTypeID);
	}

	//------------以下是读配置文件的过程，一次性-------------
	static{
		String configFile = "amuc-template/amuc-config.xml";
		
        InputStream in = Thread.currentThread().getContextClassLoader()
			.getResourceAsStream(configFile);
	    if (in == null)
	    	throw new RuntimeException("No configuration file!!" + configFile);
	    
		SAXReader reader = new SAXReader();
		Document doc = null;
	    try {
	    	doc = reader.read(in);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(in);
		}
		
		tabList = readTabs(doc, "//tabs/tab");
		
		viewTabs = readViewTabs(doc, "//views/docType");
	}
	
	private static List<Tab> readTabs(Node doc, String rootName) {
		List<Tab> tabList = new ArrayList<Tab>(6);
		
		List<Element> list = doc.selectNodes(rootName);
		
		for (Element node : list) {
			List<SubTab> children = getSubTabs(node);
			if (children.size() == 0) {
				continue;
			}
			Tab tab = new Tab();
			tab.setId(node.attributeValue("id"));
			tab.setName(node.attributeValue("name"));
			tab.setUrl(node.attributeValue("url"));
			tab.setIcon(node.attributeValue("icon"));
			tab.setNoPermission("true".equalsIgnoreCase(node.attributeValue("noPermission")));
			tab.setSeperate("true".equals(node.attributeValue("seperate")));
			tab.setChildren(children);
			
			tabList.add(tab);
		}
		return tabList;
	}
	
	private static List<SubTab> getSubTabs(Node doc) {
		List<Element> list = doc.selectNodes("sub-tab");
		if (list == null || list.size() == 0)
			return null;
		String pId = ((Element) doc).attributeValue("id");
		String pName = ((Element) doc).attributeValue("name");
		List<SubTab> children = new ArrayList<SubTab>(7);
		for (Element node : list) {
			//文档类型
			String docTypeCode = node.attributeValue("docType");
			int docTypeID = InfoHelper.getTypeIDByCode(docTypeCode);
			
			SubTab tab = new SubTab();
			tab.setDocTypeID(docTypeID);
			tab.setDocTypeCode(docTypeCode);
			tab.setName(node.attributeValue("name"));
			tab.setId(node.attributeValue("id"));
			tab.setUrl(node.attributeValue("url"));
			tab.setNoPermission("true".equalsIgnoreCase(node.attributeValue("noPermission")));
			tab.setRule(node.attributeValue("rule"));
			tab.setQuery(node.attributeValue("query"));
			tab.setList(node.attributeValue("list"));
			tab.setSeperate("true".equals(node.attributeValue("seperate")));
			tab.setpId(pId);
			tab.setpName(pName);
			
			children.add(tab);
		}
		return children;
	}
	private static Hashtable<Integer, List<SubTab>> readViewTabs(Node doc, String rootName) {
		Hashtable<Integer, List<SubTab>> viewList = new Hashtable<Integer, List<SubTab>>(6);
		
		List<Element> list = doc.selectNodes(rootName);
		for (Element node : list) {
			List<SubTab> children = getSubTabs(node);
			if (children.size() == 0) {
				continue;
			}
			//初始化时就对Url的连接符号进行设置
			for (SubTab subTab : children) {
				if (subTab.getUrl().indexOf("?") > 0) {
					subTab.setUrl(subTab.getUrl() + "&");
				} else {
					subTab.setUrl(subTab.getUrl() + "?");
				}
			}
			int docTypeID = InfoHelper.getTypeIDByCode(node.attributeValue("name"));
			
			viewList.put(docTypeID, children);
		}
		return viewList;
	}
}