package com.founder.xy.jpublish.template.component;

import com.founder.xy.commons.DocTypes;
import org.json.JSONObject;

import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.xy.column.Column;
import com.founder.xy.column.ColumnReader;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.jpublish.ColParam;
import com.founder.xy.jpublish.JsonHelper;
import com.founder.xy.jpublish.data.PubArticle;

/**
 * 稿件内容组件
 */
public class ArticleComponent extends AbstractComponent implements Component{
	protected boolean preview;

	public ArticleComponent(ColParam param, JSONObject comJson){
		this(param,comJson,false);
	}

	public ArticleComponent(ColParam param, JSONObject comJson, boolean preview){
		super(comJson);
		this.preview = preview;
	}
	
	@Override
	public String getComponentResult() throws Exception{
		try {
		getComponentData();
		}catch (Exception e){
			e.printStackTrace();
			return "";
		}
		return process();
	}

	@Override
	protected void getComponentData() {
		//若指定了ID，则不使用传入的article对象
		long docID = getArticleID();
		if (docID > 0) {
			article = getArticle(LibHelper.getArticleLibID(), docID);
		}
		//设置文中图片地址
		setPicUrl();
		article = setMasterColUrl(article);
		
		componentData.put("article", article);
	}
	
	
	
	private PubArticle setMasterColUrl(PubArticle article) {
		int channel = super.getChannel();
		ColumnReader columnReader = (ColumnReader)Context.getBean("columnReader");
		try {
			Column column = columnReader.get(LibHelper.getColumnLibID(),article.getColumnID() );
			if(column==null )
			{
				System.out.printf("稿件" + article.getId() +"主栏目（栏目id  "+article.getColumnID()+" ）为空!!请检查栏目是否已被删除");
			}
			String[] masterUrls = columnReader.getUrls(column.getLibID(), column.getId());			
			if (channel != 0) channel = 1;			
			article.setMasterColUrl(masterUrls[channel]);
			article.setMasterColIcon(column.getIconBig());
		} catch (E5Exception e) {
			e.printStackTrace();
		}
		return article;
	}

	//取组件实例指定的稿件ID
	private long getArticleID() {
		String id = JsonHelper.getString(dataJSON, "articleid");
		try {
			return StringUtils.isBlank(id) ? 0 : Long.parseLong(id);
		} catch (NumberFormatException e) {
			System.out.println("【稿件内容组件】读稿件ID异常:" + e.getLocalizedMessage());
			return 0;
		}
	}
	//按指定ID读稿件
	private PubArticle getArticle(int docLibID, long docID) {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			Document articleDoc = docManager.get(docLibID, docID);
			if (articleDoc != null){
				return new PubArticle(articleDoc);
			}
		} catch (E5Exception e) {
			System.out.println("【稿件内容组件】读稿件异常，docID:" + docID + "," + e.getLocalizedMessage());
		}
		return null;
	}



	//设置图片的url
	protected void setPicUrl() {
		int attLibID = LibHelper.getLibIDByOtherLib(DocTypes.ATTACHMENT.typeID(), article.getDocLibID());
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			Document[] atts = docManager.find(attLibID, "att_articleID=? and att_articleLibID=?",
					new Object[]{article.getId(), article.getDocLibID()});
			if (atts != null) {
				for (Document att : atts) {
					String url = (super.getChannel() == 0) ? att.getString("att_url") : att.getString("att_urlPad");

					String path = att.getString("att_path");
					//预览：图片用未发布地址
					if (preview) {
						if (!path.startsWith("http"))
							url = "../../xy/image.do?path=" + path;
					}
					int type = att.getInt("att_type");
					if (type == 2) article.setPicBig(url);
					else if (type == 3) article.setPicMiddle(url);
					else if (type == 4) article.setPicSmall(url);
						//替换正文中的图片地址
					else if(type==0){
						String content = article.getContent().replace("../../xy/image.do?path="+path, url);
						article.setContent(content);
					}
				}
			}
		} catch (E5Exception e) {
			e.printStackTrace();
		}
	}
}
