package com.founder.xy.set;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;

/**
 * 来源管理器
 */
@Component
public class SourceManager {

	/** 查询 并制作根据组id就可以获得sourceset的方法 */
	public Map<Long, Set<Source>> findSourceMap(int srcLibID) throws E5Exception {
		Map<Long, Set<Source>> sourceMap = new HashMap<>();
		Set<Source> sourceSet = null;
		Source source = null;
		
		//取出全部来源
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		String conditions = "SYS_DELETEFLAG=0";
		Document[] documents = docManager.find(srcLibID, conditions, new Object[] {});
		//如果数据库中有来源，组织扩展字段的map
		if (documents != null && documents.length > 0) {
			for (Document doc : documents) {
				source = assembleSource(doc);
				long _groupId = doc.getLong("src_groupID");
				//如果map中有这一项，拿出来set，并把source加到set里面
				if (sourceMap.containsKey(_groupId)) {
					sourceSet = sourceMap.get(_groupId);
					sourceSet.add(source);
				} else {
					//如果没有，初始化一个set，然后把source放到里面，在把他们压到map中
					sourceSet = new HashSet<>();
					sourceSet.add(source);
					sourceMap.put(_groupId, sourceSet);
				}
			}
		}
		return sourceMap;
	}

	private Source assembleSource(Document doc) {
		if (doc == null)
			return null;
		Source s = new Source();
		s.setId(doc.getDocID());
		s.setLibID(doc.getDocLibID());
		s.setDescription(doc.getString("src_description"));
		s.setGroupID(doc.getLong("src_groupID"));
		s.setIcon(doc.getString("src_icon"));
		s.setName(doc.getString("src_name"));
		s.setSiteID(doc.getLong("src_siteID"));
		s.setUrl(doc.getString("src_url"));
		return s;
	}

	public Map<Long, Set<Source>> findColumnSourceSetMap(int colLibID, Map<Long, Set<Source>> KGroupVSourceSetMap)
			throws E5Exception {
		Map<Long, Set<Source>> KColumnVSourceSetMap = new HashMap<>();
		//取出来所有栏目
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		String conditions = "SYS_DELETEFLAG=0";
		Document[] columnDocs = docManager.find(colLibID, conditions, new Object[] {});
		//如果有栏目的话, 取出groupId，拿出set 合并成一个新的set
		if (columnDocs != null && columnDocs.length > 0) {
			Long _columnId = null;
			String _groupIds = "";
			String[] _groupIdArr = null;
			Set<Source> _SourceSet = null;
			Set<Source> _mainSourceSet = null;
			//遍历栏目
			for (Document column : columnDocs) {
				_columnId = column.getDocID();
				_groupIds = column.getString("col_source");
				//如果有来源组
				if (_groupIds != null && !"".equals(_groupIds)) {
					_groupIdArr = _groupIds.split(",");
					for (String _groupId : _groupIdArr) {
						_SourceSet = KGroupVSourceSetMap.get(Long.parseLong(_groupId.trim()));
						//如果map中有相应的set， 取出来然后把_SourceSet添进去
						if (KColumnVSourceSetMap.containsKey(_columnId)) {
							_mainSourceSet = KColumnVSourceSetMap.get(_columnId);
							if (_mainSourceSet != null)
								_mainSourceSet.addAll(_SourceSet);
						} else {
							KColumnVSourceSetMap.put(_columnId, _SourceSet);
						}
					}
				}
			}
		}

		return KColumnVSourceSetMap;
	}

	public boolean isExist(Integer docLibId, String sensitivename,String type) throws E5Exception {
		boolean status = false;
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Object[] params = new Object[]{sensitivename,type};
		String conditions = " sen_word=? and sen_type= ?";
		Document[] doc = docManager.find(docLibId, conditions, params);
		if (doc != null && doc.length > 0) {
			status = true;
		}
		return status;
	}

	public String importXmlSave(String filePath,String xmlName){
		/*File file=new File(SourceManager.class.getResource("../xmlName").getFile());*/
		File file=new File(filePath+"\\"+xmlName);
		org.w3c.dom.Document document;
		DocumentManager docManager=DocumentManagerFactory.getInstance();
		Document doc;
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder builder;
		Element element;
		JSONArray jsonArr=new JSONArray();
		String str=null;
		try
		{
			// 建立DocumentBuilder对象
			builder = builderFactory.newDocumentBuilder();
			// 用DocumentBuilder对象的parse方法引入文件建立Document对象
			document = builder.parse(file);

			NodeList person = document.getElementsByTagName("Rule");

			for(int i=0;i<person.getLength();i++) {
				element=(Element)person.item(i);
				String value=element.getTextContent();
				String type=element.getAttribute("type");
				if(isExist(LibHelper.getLib(DocTypes.SENSITIVE.typeID()).getDocLibID(),value,type))
					continue;
				long docID = InfoHelper.getNextDocID(DocTypes.SENSITIVE.typeID());
				doc=docManager.newDocument(LibHelper.getLib(DocTypes.SENSITIVE.typeID()).getDocLibID(),docID);
				doc.set("sen_word",value);
				doc.set("sen_type",type);

				docManager.save(doc);

				type=type.equals("0")?"4":"5";
				JSONObject jsonObj=new JSONObject();
				jsonObj.put("id",docID);
				jsonObj.put("value",value);
				jsonObj.put("type",type);

				jsonArr.add(jsonObj);

			}

			//向敏感词服务器发送请求存储敏感词信息
			 str=jsonArr.toString();



		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace();
		}
		catch (SAXException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			System.err.println("找不到你指定的文件！");
			e.printStackTrace();
		}
		catch (E5Exception e) {
			e.printStackTrace();
		}
		return str;
	}

}
