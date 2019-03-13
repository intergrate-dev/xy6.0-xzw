package com.founder.xy.template;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;

/**
 * 组件实例
 */
public class ComponentObj {
	public static final int TYPE_COLUMNLIST = 0;
	public static final int TYPE_ARTICLELIST = 1;
	public static final int TYPE_ARTICLE = 2;
	public static final int TYPE_POSITION = 3;
	public static final int TYPE_ARTICLELISTPAGE = 4;
	public static final int TYPE_BLOCK = 5;
	
	public static final int TYPE_BLOCKARTICLE = 6;//在区块的模板代码中使用的，区块稿件列表组件
	public static final int TYPE_PREVNEXTARTICLE = 7;
	
	public static final int TYPE_PAPERLAYOUT = 8;
	public static final int TYPE_PAPERARTICLE = 9;
	public static final int TYPE_PAPERHOME = 12; //报纸的首页组件
	
	public static final int TYPE_MAGAZINE = 10;
	public static final int TYPE_MAGAZINEARTICLE = 11;
	public static final int TYPE_ADVCOLUMNLIST = 13;
	
	private long coID;
	private long templateID;
	private int type;
	private int templateType;
	private String data;
	private String code;
	private String allcode;
	
	private static Map<String,Integer> typeMap = new HashMap<String,Integer>();
	static{
		typeMap.put("columnlist", TYPE_COLUMNLIST); 	//栏目列表
		typeMap.put("articlelist", TYPE_ARTICLELIST); 	//稿件列表
		typeMap.put("article", TYPE_ARTICLE);			//稿件内容
		typeMap.put("position", TYPE_POSITION);			//当前位置
		typeMap.put("block", TYPE_BLOCK);				//页面区块
		typeMap.put("articlelistpage", TYPE_ARTICLELISTPAGE);//分页稿件列表
		
		typeMap.put("blockarticlelist", TYPE_BLOCKARTICLE);//区块稿件列表
		typeMap.put("prevnext", TYPE_PREVNEXTARTICLE);
		
		typeMap.put("paperlayout", TYPE_PAPERLAYOUT); //报纸版面
		typeMap.put("paperarticle", TYPE_PAPERARTICLE);//报纸稿件
		typeMap.put("paperhome", TYPE_PAPERHOME);//报纸首页
		
		typeMap.put("magazine", TYPE_MAGAZINE); //期刊一期
		typeMap.put("magazinearticle", TYPE_MAGAZINEARTICLE);//期刊稿件
		typeMap.put("advcolumnlist", TYPE_ADVCOLUMNLIST);//高级栏目列表组件
	}
	
	public ComponentObj(){
		super();
	}
	
	public ComponentObj(Document doc){
		this.coID = doc.getDocID();
		this.templateID = doc.getLong("co_templateID");
		this.type = doc.getInt("co_type");
		this.templateType = doc.getInt("co_templateType");
		this.data = doc.getString("co_data");
		this.code = doc.getString("co_code");
	}

	public long getCoID() {
		return coID;
	}

	public void setCoID(long comID) {
		this.coID = comID;
	}

	public long getTemplateID() {
		return templateID;
	}

	public void setTemplateID(long co_templateID) {
		this.templateID = co_templateID;
	}

	public int getType() {
		return type;
	}

	public void setType(String co_type) {
		this.type = typeMap.get(co_type);
	}

	public int getTemplateType() {
		return templateType;
	}

	public void setTemplateType(int co_templateType) {
		this.templateType = co_templateType;
	}

	public String getData() {
		return data;
	}

	public void setData(String co_data) {
		this.data = co_data;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String co_code) {
		this.code = co_code;
	}
	
	public String getAllcode() {
		return allcode;
	}

	public void setAllcode(String co_allcode) {
		this.allcode = co_allcode;
	}

	public Document covert2Document(){
		long docID = InfoHelper.getNextDocID(DocTypes.COMPONENTOBJ.typeID());
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = null;
		try {
			doc = docManager.newDocument(LibHelper.getComponentObjLibID(), docID);
		} catch (E5Exception e) {
			e.printStackTrace();
		}
		doc.set("co_templateID", this.templateID);
		doc.set("co_templateType", this.templateType);
		doc.set("co_type", this.type);
		doc.set("co_data", this.data);
		doc.set("co_code", this.code);
		return doc;
	}
	
	public String covert2Json(){
		JSONObject json = new JSONObject();
		json.put("comID", this.coID);
		json.put("templateID", this.templateID);
		json.put("type", this.type);
		json.put("data", this.data);
		json.put("code", this.code);
		return json.toString();
	}
}
