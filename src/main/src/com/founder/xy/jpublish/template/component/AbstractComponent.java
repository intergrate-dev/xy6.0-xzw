package com.founder.xy.jpublish.template.component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.e5.rel.service.RelTableReader;
import com.founder.xy.column.Column;
import com.founder.xy.column.ColumnReader;
import com.founder.xy.commons.CatTypes;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.jpublish.ColParam;
import com.founder.xy.jpublish.JsonHelper;
import com.founder.xy.jpublish.data.PubArticle;
import com.founder.xy.jpublish.template.FreeMarkerManager;
import com.founder.xy.set.ExtField;
import com.founder.xy.set.ExtFieldReader;

public abstract class AbstractComponent implements Component{
	long comID;
	long templateID;
	int type;
	String data;
	JSONObject dataJSON;
	String code;
	ColParam param;
	
	PubArticle article;
	
	Map<String,Object> componentData = new HashMap<String,Object>();
	
	public AbstractComponent(JSONObject coJson){
		this.comID = coJson.getLong("comID");
		this.templateID = coJson.getLong("templateID");
		this.code = coJson.getString("code");
		this.type = coJson.getInt("type");
		this.data = coJson.getString("data");
		
		if (!StringUtils.isBlank(data))
			dataJSON = new JSONObject(data);
	}
	
	@Override
	public Object getData(String key) {
		return componentData.get(key);
	}
	
	public void setData(String key, Object data) {
		componentData.put(key, data);
	}
	
	/**
	 * 设置PubArticle对象（部分组件中没有该对象）
	 */
	public void setArticle(PubArticle article){
		this.article = article;
	}

	/**
	 * 得到PubArticle对象（部分组件中没有该对象）
	 */
	public PubArticle getArticle() {
		return article;
	}

	public int getType() {
		return type;
	}

	/**
	 * 组织数据，以便模板中取变量
	 */
	protected abstract void getComponentData();

	/**
	 * 转换Freemarker模板代码得到部分结果
	 */
	protected String process() throws Exception{
		//把组件实例ID也放进去，以便模板中特殊场景下使用。比如分页稿件列表中可能会用到。
		setData("coID", comID + "");
		String result ;
		try {
			result = FreeMarkerManager.process(comID + "", componentData, code);
		} catch (Exception e) {
			result = "<!-- \n模板 "+templateID +"中组件"+comID+"出现问题！\n" +
					data+"\n"+code+"\n"+e.toString()+ "\n -->";
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 取稿件栏目关联表的表名，用于查询
	 * @param docLibID
	 * @return
	 */
	protected String getRelTableName(int docLibID) {
		RelTableReader relReader = (RelTableReader)Context.getBean(RelTableReader.class);
		return relReader.getRelTableName(docLibID, CatTypes.CAT_COLUMNARTICLE.typeID());
	}
	
	/**
	 * 按渠道取稿件库ID
	 * @return
	 * @throws E5Exception
	 */
	protected int getArticleLibID() {
		int channel = getColumnChannel();
		try {
			String tenantCode = LibHelper.getTenantCodeByLib(param.getColLibID());
			List<DocLib> articleLibs = LibHelper.getLibs(DocTypes.ARTICLE.typeID(), tenantCode);
			
			return articleLibs.get(channel).getDocLibID();
		} catch (E5Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * 得到模板的发布渠道（网站、触屏）
	 */
	protected int getChannel() {
		//默认是1，兼容已有的app
		return JsonHelper.getInt(dataJSON, "channel", 1);
	}
	
	/**
	 * 判断栏目的渠道。稿件查询时以此判断是web发布库还是app发布库
	 */
	protected int getColumnChannel() {
		if (param.getColID() == 0) return 0;
		
		ColumnReader colReader = (ColumnReader)Context.getBean("columnReader");
		try {
			Column col = colReader.get(param.getColLibID(), param.getColID());
			return col.getChannel();
		} catch (E5Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * 设置栏目的url，用于组件：当前位置、栏目列表
	 */
	protected void setColumnUrl(List<Column> columns) {
		if (columns == null) return;
		
		/*
		//按稿件的发布时间取路径
		String datePath = (article == null) 
				? DateUtils.format("yyyyMM/dd") 
				: DateUtils.format(article.getPubTime(), "yyyyMM/dd");
		*/
		ColumnReader columnReader = (ColumnReader)Context.getBean("columnReader");
		for (Column column : columns) {
			try {
				//栏目设置了外部链接 则栏目URL指向外部链接
				String linkUrl = column.getLinkUrl();
				if(!StringUtils.isBlank(linkUrl)){
					column.setUrl(linkUrl);
					column.setUrlPad(linkUrl);
				}
				else {
					String[] urls = columnReader.getUrls(column.getLibID(), column.getId());
					if (urls[0] != null) column.setUrl(urls[0]);
					if (urls[1] != null) column.setUrlPad(urls[1]);
				}
			} catch (E5Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 获取稿件的扩展字段
	 * @param a
	 * @param column
	 * @return
	 */
	protected HashMap<String, String> getExtFields(PubArticle a, Column column) {
		int docLibID = a.getDocLibID();
		long docID = a.getId();
		//扩展字段
		int extGroupID = a.getExtFieldGroupID();
		if (extGroupID <= 0){
			extGroupID = column.getExtFieldGroupID();
		}
		
		HashMap<String, String> result = new HashMap<String, String>();
		
		if (extGroupID <= 0) return result;
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		int wLibID = LibHelper.getLibIDByOtherLib(DocTypes.ARTICLEEXT.typeID(), docLibID);
		int extLibID = LibHelper.getLibIDByOtherLib(DocTypes.EXTFIELD.typeID(), docLibID);
		try {
			ExtFieldReader extReader = (ExtFieldReader)Context.getBean("extFieldReader");
			Set<ExtField> fields = extReader.getFields(extLibID, extGroupID);
			if (fields == null) return result;
			
			Document[] rels = docManager.find(wLibID, "ext_articleID=? and ext_articleLibID=?", 
					new Object[]{docID, docLibID});
			
			if (rels != null && rels.length > 0) {
				for (Document rel : rels) {
					ExtField field = findName(fields, rel.getString("ext_code"));
					if (field != null) {
						result.put(field.getExt_name(), rel.getString("ext_value"));
					}
				}
			}
		} catch (E5Exception e) {
			System.err.println("读稿件的扩展字段信息时异常：" + e.getLocalizedMessage());
		}
		return result;
		
	}
	//按扩展字段code找到名称
	private ExtField findName(Set<ExtField> fields, String code) {
		for (ExtField extField : fields) {
			if (extField.getExt_code().equals(code))
				return extField;
		}
		return null;
	}
}
