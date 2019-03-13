package com.founder.xy.api.am;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;


import com.founder.e5.commons.Log;
import com.founder.e5.sys.org.User;
import com.founder.e5.sys.org.UserReader;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.founder.e5.cat.CatManager;
import com.founder.e5.cat.CatReader;
import com.founder.e5.cat.Category;
import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.DomHelper;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.doc.util.E5docHelper;
import com.founder.e5.flow.FlowNode;
import com.founder.e5.flow.FlowReader;
import com.founder.e5.sys.StorageDevice;
import com.founder.e5.sys.StorageDeviceManager;
import com.founder.e5.sys.SysFactory;
import com.founder.e5.web.org.StringValueUtils;
import com.founder.xy.article.Article;
import com.founder.xy.article.ArticleManager;
import com.founder.xy.column.Column;
import com.founder.xy.column.ColumnReader;
import com.founder.xy.commons.CatTypes;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.jms.PublishTrigger;
import com.founder.xy.system.Tenant;

import cnml.apiengine.baseException.CNMLException;
import cnml.apifactory.CNMLAPI;
import cnml.apifactory.CNMLAPIFactory;
import cnml.node.CNML;
import cnml.node.ContentItem;
import cnml.node.Contents;
import cnml.node.Creator;
import cnml.node.Creators;
import cnml.node.DescriptionMetaGroup;
import cnml.node.Envelop;
import cnml.node.ExtendedTitle;
import cnml.node.ExtendedTitles;
import cnml.node.HeadLine;
import cnml.node.Item;
import cnml.node.ItemSource;
import cnml.node.Items;
import cnml.node.Keyword;
import cnml.node.Keywords;
import cnml.node.MetaInfo;
import cnml.node.Name;
import cnml.node.SourceInfo;
import cnml.node.SubHeadLine;
import cnml.node.Titles;
import cnml.node.TransferTime;

@Service
public class AmApiManager {
	@Autowired
	private ColumnReader colReader;

	@Autowired
	private ArticleManager articleManager;
	private Log log = Context.getLog("xy");
	/** CNML关键字分隔符 */
	private static final String[] SEPARATOR_CNML_KEYWORDS = { "-", "~", "-",
			"·", "－" };
	/** 新华网三期稿源库关键字分隔符 */
	private static final String SEPARATOR_CMS4_KEYWORDS = ",";

	/**
	 * 根据parentId查找并添加子节点元素
	 * 
	 * @param ele
	 *            节点元素
	 * @param subList
	 *            栏目列表数据
	 * @param parentId
	 *            父栏目id
	 * @throws E5Exception
	 */
	public void addChildrenNode(Element ele, int colLibID,
			List<Column> subList, long parentId) throws E5Exception {
		if (subList != null) {
			for (Column column : subList) {
				if (column.getParentID() == parentId) {
					Element subEle = ele.addElement("node");
					subEle.addAttribute("id", column.getId() + "");
					subEle.addAttribute("name", column.getName());
					if (parentId == 0) {
						subEle.addAttribute("type", "channel");
					} else {
						subEle.addAttribute("type", "column");
					}
					// 递归添加子节点
					addChildrenNode(subEle, colLibID,
							colReader.getSub(colLibID, column.getId()),
							column.getId());
				}
			}
		}
	}

	/**
	 * WEB稿件签发
	 * @param status
	 * 
	 * @param docXml
	 */
	public String publish(String docXml, String tenantCode, int status) {
		ExchangeData exchangeData = null;
		List<DocContent> docContentList = null;
		String result = null;
		
		SAXReader saxReader = new SAXReader();
		Document document = null;
		// 暂时配置的本地简单ftp，到现场配置正常的使用
		int docLibID = LibHelper.getArticleLibID();
		
		try {
			System.out.println("--docXml--AmApiManager.java:163:"+docXml);
			document = saxReader.read(new StringReader(docXml));
			exchangeData = parseExchangeXML(document,"",tenantCode,status,docLibID);
			docContentList = exchangeData.getDocContentList();
			// 保存稿件
			result = save(docContentList);
			if (result == null) {
				// 发布稿件
				triggerPublish(docContentList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return genResultXML(docContentList, result);
	}
	
	/**
	 * APP稿件签发
	 * @param status
	 *
	 * @param articleXmlUrl
	 */
	public String publishApp(String articleXmlUrl, String tenantCode, int status) {
		ExchangeData exchangeData = null;
		List<DocContent> docContentList = null;
		String result = null;
		
		SAXReader saxReader = new SAXReader();
		InputStream in = null;
		Document document = null;
		int docLibID = LibHelper.getArticleAppLibID();
	
		// 暂时配置的本地简单ftp，到现场配置正常的使用
		try {
			// 开始存储到存储设备上
			StorageDeviceManager sdManager = SysFactory.getStorageDeviceManager();
			// 存储设备的名称
			StorageDevice device = InfoHelper.getNewsEditDevice();
			try {
				in = sdManager.read(device, articleXmlUrl);
				document = saxReader.read(in);
			} finally {
				ResourceMgr.closeQuietly(in);//释放资源
			}
			exchangeData = parseExchangeXML(document,articleXmlUrl,tenantCode,status,docLibID);
			docContentList = exchangeData.getDocContentList();
			// 保存稿件
			result = save(docContentList);
			if (result == null) {
				// 发布稿件
				triggerPublish(docContentList);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return genResultXML(docContentList, result);
	}

	/**
	 * 保存稿件
	 * 
	 * @param docContentList
	 * @return
	 */
	private String save(List<DocContent> docContentList) {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DBSession conn = null;
		try {
			conn = E5docHelper.getDBSession(docContentList.get(0).getArticle()
					.getDocLibID());
			conn.beginTransaction();
			for (DocContent docContent : docContentList) {
				// 保存稿件
				docManager.save(docContent.getArticle(), conn);
				// 保存附件
				String error = saveAttachments(docContent.getAttachementList(),
						conn);
				// 删除老的附件
				if (error != null) {
					delOldAttachments(docContent.getOldAttachList(), conn);
				}

			}
			// 提交transaction
			conn.commitTransaction();
			return null;
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			e.printStackTrace();
			return e.getLocalizedMessage();
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
	}

	private String delOldAttachments(
			com.founder.e5.doc.Document[] oldAttachList, DBSession conn) {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		// 同时修改多个附件，使用事务
		try {
			conn.beginTransaction();

			for (com.founder.e5.doc.Document attachement : oldAttachList) {
				docManager.delete(attachement);
			}
			conn.commitTransaction();
			return null;
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			e.printStackTrace();
			return "操作中出现错误：" + e.getLocalizedMessage();
		} finally {
		}

	}

	private String saveAttachments(
			List<com.founder.e5.doc.Document> attachementList, DBSession conn) {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		// 同时修改多个附件，使用事务
		try {
			conn.beginTransaction();

			for (com.founder.e5.doc.Document attachement : attachementList) {
				docManager.save(attachement, conn);
			}
			conn.commitTransaction();
			return null;
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			e.printStackTrace();
			return "操作中出现错误：" + e.getLocalizedMessage();
		} finally {
		}

	}

	/**
	 * 发布稿件
	 * 
	 * @param docContentList
	 */
	private void triggerPublish(List<DocContent> docContentList) {
		for (DocContent docContent : docContentList) {
			com.founder.e5.doc.Document doc = docContent.getArticle();
			if (doc.getInt("a_status") == Article.STATUS_PUB_ING)
				PublishTrigger.article(doc);
		}
	}

	/**
	 * 解析数据接口xml
	 * @param document
	 * 
	 * @param docLibID
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ExchangeData parseExchangeXML(Document document,String articleXmlUrl,
			String tenantCode, int status, int docLibID) throws Exception {

		List<Element> parameterList = document
				.selectNodes("//Exchange/ExchangeInfo/Parameter");
		ExchangeData exchangeData = new ExchangeData();
		for (Element parameter : parameterList) {
			
			if ("currentUser".equals(parameter.attribute("name").getValue())) {
				String currentUser = parameter.getText();
				if (currentUser != null) {
					exchangeData.setCurrentUser(currentUser);
					exchangeData.setCurrentUserID(getUserIDByName(currentUser));
				}
			}
			if ("operationTime".equals(parameter.attribute("name").getValue())) {
				Date operationTime = DateUtils.parse(parameter.getText(),
						"yyyy-MM-dd HH:mm:ss");
				if (operationTime != null) {
					exchangeData.setOperationTime(operationTime);
				}
			}
		}
		List<Element> docList = document
				.selectNodes("//Exchange/ExchangeData/Doc");
		List<DocContent> docContentList = new ArrayList<DocContent>();
		for (Element element : docList) {
			docContentList.add(getDocContent(articleXmlUrl, exchangeData,
					element, tenantCode, status, docLibID));
		}
		exchangeData.setDocContentList(docContentList);
		return exchangeData;
	}

	/**
	 * 解析接口数据
	 * 
	 * @param exchangeData
	 * @param element
	 * @param docLibID
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private DocContent getDocContent(String articleXmlUrl,
			ExchangeData exchangeData, Element element,
			String tenantCode, int status, int docLibID) throws Exception {
		DocContent docContent = new DocContent();
		if (element.attribute("id") != null) {//拿<Doc>中的id属性值
			docContent.setId(element.attribute("id").getValue());
		}
		if (element.attribute("guid") != null) {//拿<Doc>中的guid属性值
			docContent.setGuid(element.attribute("guid").getValue());
		}
		List<Element> itemList = element.selectNodes("DocInfo/Item");
		long docID = 0;
		for (Element item : itemList) {
			if ("publishDocId".equals(item.attribute("name").getValue())) {
				try {
					docID = Long.parseLong(item.getText());
				} catch (Exception e) {
					docID = 0;
				}
				docContent.setPublishId(docID);
			} else if ("columnId".equals(item.attribute("name").getValue())) {
				int columnId = Integer.parseInt(item.getText());
				docContent.setColumnId(columnId);
			} else if ("publishTime".equals(item.attribute("name").getValue())) {
				if ("".equals(item.getText())) {
					docContent.setPublishTime(DateUtils.getTimestamp());
				} else {
					Timestamp pubTime = new Timestamp(DateUtils.parse(
							item.getText(), "yyyy-MM-dd HH:mm:ss").getTime());
					docContent.setPublishTime(pubTime);
				}
			} else if ("publishStatus"
					.equals(item.attribute("name").getValue())) {
				int publishStatus = Integer.parseInt(item.getText());
				docContent.setPublishStatus(publishStatus);
			} else if ("batmanId".equals(item.attribute("name").getValue())){
				int batmanId = Integer.parseInt(item.getText());
				docContent.setBatmanId(batmanId);
			} 
//			else if ("industry".equals(item.attribute("name").getValue())){
//				String industry = item.getText();
//				docContent.setIndustry(industry);
//			} else if ("docregion".equals(item.attribute("name").getValue())){
//				String docregion = item.getText();
//				docContent.setDocregion(docregion);
//			} else if ("category".equals(item.attribute("name").getValue())){
//				String category = item.getText();
//				docContent.setCategory(category);
//			}	
			
		}
		
		
		org.dom4j.Node docContentNode = element.selectSingleNode("DocContent");
		String cnmlStr = StringUtils.trim(StringUtils.substringBetween(
				docContentNode.asXML(), "<DocContent>", "</DocContent>"));
		com.founder.e5.doc.Document article = null;
		if (StringUtils.isNotEmpty(cnmlStr)) {
			article = getArticle(exchangeData, docContent, docLibID,articleXmlUrl);
			if (0 != status) {// 全媒体传过来的PublishStatus=0表示预发布
				articleManager.tryPublish(article, docContent.getColumnId(), true);
			} else {
				article.set("a_status", Article.STATUS_PUB_NOT);
			}
			InputStream cnmlInputStream = new ByteArrayInputStream(
					cnmlStr.getBytes("UTF-8"));
			parse(cnmlInputStream, article);//解析<CNML>…</CNML>
			if ("".equals(article.getString("a_editor"))) {
				article.set("a_editor", exchangeData.getCurrentUser());
			}
			
			docContent.setArticle(article);
		}

		// 取出已有附件
		com.founder.e5.doc.Document[] old = articleManager.getAttachments(
				article.getDocLibID(), article.getDocID());
		docContent.setOldAttachList(old);

		List<com.founder.e5.doc.Document> newAttaList = new ArrayList<com.founder.e5.doc.Document>();
		List<Element> attachList = element.selectNodes("Attaches/Attach");
		int attLibID = LibHelper.getLibIDByOtherLib(DocTypes.ATTACHMENT
				.typeID(), docContent.getArticle().getDocLibID());
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		
		String attachPicPath = "";
		//如果是APP
		if (articleXmlUrl != "") {
			attachPicPath = articleXmlUrl.substring(0,
					articleXmlUrl.lastIndexOf("."))
					+ "/";
		}
		
		Boolean firstPic=true;
		for (Element attach : attachList) {
			String attMediaType = attach.attribute("mediaType").getValue();

			int attachType = StringValueUtils.getInt(attach.attribute("type")
					.getValue(), -1);

			if ("Photo".equals(attMediaType)) {// 图片
				if (attachType == 0) {
					// 附件插入正文，稿件的附件表
					long attDocID = InfoHelper.getNextDocID(DocTypes.ATTACHMENT
							.typeID());
					com.founder.e5.doc.Document atta = docManager.newDocument(
							attLibID, attDocID);
					String attachHref = attach.attribute("href").getValue();

					String fileName = UUID.randomUUID().toString()
							+ attachHref.substring(attachHref.lastIndexOf("."));
					StorageDevice device = InfoHelper.getPicDevice();
					String savePath = tenantCode + "/"
							+ DateUtils.format("yyyyMM/dd/") + fileName;

					StorageDeviceManager sdManager = SysFactory.getStorageDeviceManager();
					StorageDevice newsDevice = InfoHelper.getNewsEditDevice();
					InputStream is = null;
					System.out.println(attachHref);
					try {
						is = sdManager.read(newsDevice, attachPicPath + attachHref);
						sdManager.write(device, savePath, is);
					} finally {
						ResourceMgr.closeQuietly(is);
					}

					String attaPath = device.getDeviceName() + ";" + savePath;

					atta.set("att_articleID", docContent.getArticle()
							.getDocID()); // 所属稿件
					atta.set("att_articleLibID", docContent.getArticle()
							.getDocLibID());
					atta.set("att_path", attaPath);
					atta.set("att_type", 0); // 0:正文图片;1:正文视频

					// 加抽图任务
					InfoHelper.prepare4Extract(device, savePath);

					// 替换文章内容
					String content = docContent.getArticle().getString(
							"a_content");
					content = StringUtils
							.replace(
									content,
									"sourcedescription=\"编辑提供的本地文件\" sourcename=\"本地文件\"",
									"");
					content = StringUtils.replace(content, "id=\"" + attachHref
							+ "\"", "");
					content = StringUtils.replace(content, attachHref,
							"../../xy/image.do?path=" + attaPath);
					//第一张图片设置为标题图片
					if(firstPic){
						firstPic=false;
						docContent.getArticle().set("a_picBig", attaPath);
						com.founder.e5.doc.Document _atta = docManager.newDocument(attLibID,
								InfoHelper.getNextDocID(DocTypes.ATTACHMENT.typeID()));
						_atta.set("att_articleID", docContent.getArticle().getDocID()); // 所属稿件
						_atta.set("att_articleLibID", docContent.getArticle().getDocLibID());
						_atta.set("att_path", attaPath);
						_atta.set("att_type", 2); // 0:正文图片;1:正文视频 2,3,4 大中小
						newAttaList.add(_atta);
						
						docContent.getArticle().set("a_picMiddle", attaPath);
						_atta = docManager.newDocument(attLibID,
								InfoHelper.getNextDocID(DocTypes.ATTACHMENT.typeID()));
						_atta.set("att_articleID", docContent.getArticle().getDocID()); // 所属稿件
						_atta.set("att_articleLibID", docContent.getArticle().getDocLibID());
						_atta.set("att_path", attaPath);
						_atta.set("att_type", 3); // 0:正文图片;1:正文视频 2,3,4 大中小
						newAttaList.add(_atta);
						
						docContent.getArticle().set("a_picSmall", attaPath);
						_atta = docManager.newDocument(attLibID,
								InfoHelper.getNextDocID(DocTypes.ATTACHMENT.typeID()));
						_atta.set("att_articleID", docContent.getArticle().getDocID()); // 所属稿件
						_atta.set("att_articleLibID", docContent.getArticle().getDocLibID());
						_atta.set("att_path", attaPath);
						_atta.set("att_type", 4); // 0:正文图片;1:正文视频 2,3,4 大中小
						newAttaList.add(_atta);
					}
					docContent.getArticle().set("a_content", content);
					newAttaList.add(atta);
				}
			}
		}
		docContent.setAttachementList(newAttaList);

		return docContent;
	}
	
	/**
	 * 稿件基本信息
	 * 
	 * @param exchangeData
	 * @param docContent
	 * @param docLibID
	 * @return
	 * @throws Exception
	 */
	private com.founder.e5.doc.Document getArticle(ExchangeData exchangeData,
			DocContent docContent, int docLibID, String articleXmlUrl) throws Exception {
		com.founder.e5.doc.Document article = null;
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		//防止删除稿件再次发布时，翔宇不将稿件移出回收站，全媒体的稿件一律视为新稿
		boolean isNew = true;
		long docID = docContent.getPublishId();
		if (isNew) {
			docID = InfoHelper.getNextDocID(DocTypes.ARTICLE.typeID());
			article = docManager.newDocument(docLibID, docID);
		} else {
			article = docManager.get(docLibID, docID);
			if (article == null) {
				article = docManager.newDocument(docLibID, docID);
			}
		}
		int FVID =  DomHelper.getFVIDByDocLibID(docLibID);
        article.set("sys_folderID", FVID);
		article.setCurrentUserName(exchangeData.getCurrentUser());
		article.set("SYS_AUTHORID",exchangeData.getCurrentUserID());
		article.setLastmodified(DateUtils.getTimestamp());
		article.set("a_columnID", docContent.getColumnId());
		int colLibID = LibHelper.getColumnLibID();//取栏目库ID
		//根据栏目库ID和栏目ID 获取栏目对象
		Column col = colReader.get(colLibID, docContent.getColumnId());
		article.set("a_siteID", col.getSiteID());
		article.set("a_column", col.getCasNames());
		article.set("a_pubTime", docContent.getPublishTime());
		article.set("a_type", Article.TYPE_ARTICLE);
		article.set("a_channel", articleXmlUrl==""?1:2);// 稿件的渠道

		//如果是通讯员投稿
		if(docContent.getBatmanId()!=0){
			//根据通讯员ID查询通讯员表
			int batmanLibID = LibHelper.getLibID(DocTypes.BATMAN.typeID(), Tenant.DEFAULTCODE);
			com.founder.e5.doc.Document[] batmanDoc = docManager.find(batmanLibID, "bm_amID=?",
					new Object[]{docContent.getBatmanId()});
			//根据通讯员表中的单位ID查询单位表
			int corporationID = LibHelper.getLibID(DocTypes.CORPORATION.typeID(), Tenant.DEFAULTCODE);
			com.founder.e5.doc.Document[] corporationDoc = docManager.find(corporationID, "SYS_DOCUMENTID=?",
					new Object[]{batmanDoc[0].get("bm_corporation_ID")});
			//得到稿件表的Document对象
			if (corporationDoc[0]!= null) { 
				article.set("SYS_AUTHORID", batmanDoc[0].getDocID());//通讯员ID
				article.set("a_sourceType", 3);//来源类型
				article.set("a_corpID", batmanDoc[0].get("bm_corporation_ID"));//单位ID
				article.set("a_corpTypeID", corporationDoc[0].get("corp_type_ID"));//单位类别ID
				article.set("a_corpTradeID", corporationDoc[0].get("corp_trade_ID"));//单位行业ID
				article.set("a_corpStock", corporationDoc[0].get("corp_stock"));//单位上市情况
				article.set("a_corpStockCode", corporationDoc[0].get("corp_stockCode"));//单位证券代码
				article.set("a_corpPark", corporationDoc[0].get("corp_park"));//单位园区
			 }
		}
			
		// 按主栏目设置流程。
		articleManager.setFlowByColumn(article);
		
		if (isNew) {
			articleManager.setColumnAll(article);
			article.set("a_linkTitle", article.getTopic()); // 链接标题
			// 设置顺序
			double order = articleManager.getNewOrder(article);
			article.set("a_order", order);
		}

		return article;
	}

	/**
	 * 解析cnml
	 * 
	 * @param cnmlInputStream
	 * @param article
	 * @throws CNMLException
	 */
	private void parse(InputStream cnmlInputStream,
			com.founder.e5.doc.Document article) throws CNMLException {
		CNMLAPI api = CNMLAPIFactory.getInstance();
		CNML cnml = api.parse(cnmlInputStream);
		Envelop envelop = cnml.getEnvelop();//cnml信封
		Items items = cnml.getItems();
		Item item = items.getItem(0);
		MetaInfo metoinfo = item.getMetaInfo();
		DescriptionMetaGroup descriptionMetaGroup = metoinfo
				.getDescriptionMetaGroup();
		String sourceName = getSourceName(descriptionMetaGroup);//来源名称
		article.set("a_source", sourceName);
		// 解析播发时间
		String transferTime = getTransferTime(envelop);
		Timestamp createTime = new Timestamp(DateUtils.parse(transferTime,
				"yyyy-MM-dd HH:mm:ss").getTime());
		article.setCreated(createTime);
		// 解析标题
		String title = getTitle(descriptionMetaGroup);
		title = title.replaceAll("[\n\r]", "");//过滤标题中的换行符
		article.setTopic(title);
		article.set("a_linkTitle", title);
		// 解析副标题
		String subTitle = getSubTitle(descriptionMetaGroup);
		article.set("a_subTitle", subTitle);
		// 解析摘要
		String abs = getAbstract(descriptionMetaGroup);
		article.set("a_abstract", abs);
		// 解析作者
		String owner = getOwner(descriptionMetaGroup);
		owner = owner.replaceAll("[?]", "").replaceAll("[？]", "");
		article.setAuthors(owner);
		// 解析编辑
		String editor = getEditor(descriptionMetaGroup);
		editor = editor.replaceAll("[?]", "").replaceAll("[？]", "");
		article.set("a_editor", editor);
		// 解析关键字
		String keyword = getKeyword(descriptionMetaGroup);
		article.set("a_keyword", keyword);
		// 解析和处理正文
		String content = getContent(item, ContentItem.TEXTCITYPE);
		article.set("a_content", content);
	}

	
	
	/**
	 * 生成返回信息
	 * 
	 * @param docContentList
	 * @return
	 */
	private String genResultXML(List<DocContent> docContentList, String result) {
		StringBuffer resultBuffer = new StringBuffer();
		resultBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		resultBuffer.append("<ExchangeResult>");
		if(docContentList != null && docContentList.size()>0)
			for (DocContent docContent : docContentList) {
				resultBuffer.append("<Doc><originalId>");
				resultBuffer.append(docContent.getId());
				resultBuffer.append("</originalId><guid>");
				resultBuffer.append(docContent.getGuid());
				resultBuffer.append("</guid><publishId>");
				resultBuffer.append(docContent.getArticle().getDocID());
				resultBuffer.append("</publishId><success>");
				if (result == null) {
					resultBuffer.append("true");
				} else {
					resultBuffer.append("false");
				}
				resultBuffer.append("</success><operationTime>");
				resultBuffer.append(DateUtils.format(new Date(),
						"yyyy-MM-dd HH:mm:ss"));
				resultBuffer
						.append("</operationTime><error><errorCode></errorCode><errorCause></errorCause></error></Doc>");
			}
		resultBuffer.append("</ExchangeResult>");
		return resultBuffer.toString();
	}

	/**
	 * 解析CNML稿件播发时间
	 * <p>
	 * CNML稿源播发时间格式：YYYY-MM-DDTHH:MI:SS+时区，例如2009-03-23T10:42:44+08:00
	 * 
	 * @param envelop
	 *            CNML信封
	 * @return 从CNML提取出来的播发时间信息
	 * */
	protected String getTransferTime(Envelop envelop) {
		String tt = "";

		try {
			TransferTime transferTime = envelop.getTransferTime();
			if (transferTime != null) {
				tt = transferTime.getText();
				tt = tt.substring(0, tt.indexOf("+")).replaceAll("T", " ");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return tt;
	}

	/**
	 * 解析标题
	 * 
	 * @param descriptionMetaGroup
	 *            CNML元数据信息
	 * @return 从CNML提取出来的标题
	 * */
	protected String getTitle(DescriptionMetaGroup descriptionMetaGroup) {
		String title = "";
		try {
			Titles titles = descriptionMetaGroup.getTitles();
			if (titles != null) {
				HeadLine headLine = titles.getHeadLine(0);
				if (headLine != null) {
					title = headLine.getText();
				}
			}
			return title;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 解析引题
	 * 
	 * @param descriptionMetaGroup
	 *            CNML元数据信息
	 * @return 从CNML提取出来的引题
	 * */
	@SuppressWarnings("rawtypes")
	protected String getIntroTitle(DescriptionMetaGroup descriptionMetaGroup) {
		String introTitle = "";
		try {
			Titles titles = descriptionMetaGroup.getTitles();
			if (titles != null) {
				ExtendedTitles extendedTitles = titles.getExtendedTitles();
				List extendedTitleList = extendedTitles.getExtendedTitleList();
				for (int i = 0; i < extendedTitleList.size(); i++) {
					if ("IntroTopic".equals(((ExtendedTitle) extendedTitleList
							.get(i)).getKindAttr())) {
						introTitle = ((ExtendedTitle) extendedTitleList.get(i))
								.getText();
						break;
					}
				}
			}
			return introTitle;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 解析副题
	 * 
	 * @param descriptionMetaGroup
	 *            CNML元数据信息
	 * @return 从CNML提取出来的副题
	 * */
	protected String getSubTitle(DescriptionMetaGroup descriptionMetaGroup) {
		String subTitle = "";
		try {
			Titles titles = descriptionMetaGroup.getTitles();
			if (titles != null) {
				SubHeadLine subHeadLine = titles.getSubHeadLine(0);
				if (subHeadLine != null) {
					subTitle = subHeadLine.getText();
				}
			}
			return subTitle;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 解析摘要
	 * 
	 * @param descriptionMetaGroup
	 *            CNML元数据信息
	 * @return 从CNML提取出来的摘要
	 * */
	protected String getAbstract(DescriptionMetaGroup descriptionMetaGroup) {
		String abs = "";
		try {
			abs = descriptionMetaGroup.getAbstractText(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return abs;
	}

	/**
	 * 解析作者
	 * 
	 * @param descriptionMetaGroup
	 *            CNML元数据信息
	 * @return 从CNML提取出来的作者
	 * */
	protected String getOwner(DescriptionMetaGroup descriptionMetaGroup) {
		String author = "";
		try {
			Creators creators = descriptionMetaGroup.getCreators();
			if (creators != null) {
				Creator creator = creators.getCreator(0);
				if (creator != null) {
					Name name = creator.getName(0);
					if (name != null) {
						author = name.getFullNameText(0);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return author;
	}

	/**
	 * 解析编辑
	 * 
	 * @param descriptionMetaGroup
	 *            CNML元数据信息
	 * @return 从CNML提取出来的编辑
	 * */
	protected String getEditor(DescriptionMetaGroup descriptionMetaGroup) {
		String editor = "";
		try {
			Creators creators = descriptionMetaGroup.getCreators();
			if (creators != null) {
				Creator creator = creators.getCreator(1);
				if (creator != null) {
					Name name = creator.getName(0);
					if (name != null) {
						editor = name.getFullNameText(0);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return editor;
	}

	/**
	 * 解析关键词
	 * 
	 * @param descriptionMetaGroup
	 *            CNML元数据信息
	 * @return 从CNML提取出来的关键词
	 * */
	@SuppressWarnings("rawtypes")
	protected String getKeyword(DescriptionMetaGroup descriptionMetaGroup) {
		String key = "";
		try {
			Keywords keywords = descriptionMetaGroup.getKeywords();
			if (keywords != null) {
				Keyword keyword = null;
				List list = keywords.getKeywordList();
				if (list != null) {
					String perKey = "";
					for (int k = 0, n = list.size(); k < n; k++) {
						keyword = keywords.getKeyword(k);
						if (keyword != null) {
							perKey = keyword.getText();
							if (StringUtils.isNotEmpty(perKey)) {
								for (int i = 0; i < SEPARATOR_CNML_KEYWORDS.length; i++) {
									perKey = perKey.replaceAll(
											SEPARATOR_CNML_KEYWORDS[i],
											SEPARATOR_CMS4_KEYWORDS);
								}
								key = key + perKey + SEPARATOR_CMS4_KEYWORDS;
							}
						}
					}
				}
			}
			if (!key.equals("")) {
				key = key.substring(0,
						key.length() - SEPARATOR_CMS4_KEYWORDS.length());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return key;
	}

	/**
	 * 解析来源名称
	 * 
	 * @param descriptionMetaGroup
	 *            CNML元数据信息
	 * @return 从CNML提取出来的来源名称信息
	 * */
	protected String getSourceName(DescriptionMetaGroup descriptionMetaGroup) {
		String sourceName = "";
		try {
			SourceInfo sourceInfo = descriptionMetaGroup.getSourceInfo(0);
			if (sourceInfo != null) {
				ItemSource itemSource = sourceInfo.getItemSource();
				if (itemSource != null) {
					sourceName = itemSource.getNameText(0);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sourceName == null ? "" : sourceName;
	}

	/**
	 * 解析正文
	 * 
	 * @param item
	 *            CNML元数据信息
	 * @param contentItemType
	 *            CNML内容项类型
	 * @return 从CNML提取出来的分类
	 * */
	@SuppressWarnings({ "rawtypes" })
	protected String getContent(Item item, int contentItemType) {
		String content = "";
		try {
			Contents contents = item.getContents();
			if (contents != null) {
				List itemList = contents.getContentItemList();
				if (itemList != null) {
					Iterator it = itemList.iterator();
					while (it.hasNext()) {
						ContentItem contentItem = (ContentItem) it.next();
						String mediaType = contentItem.getMetaInfo()
								.getCharacteristicMetaGroup().getMediaType()
								.getTopicRefAttr();
						if (contentItem != null) {
							if (contentItem.getType() == contentItemType) {
								// contentItem.getDataContent().getText();
								if ("Multimedia".equals(mediaType)) {
									content = contentItem.getDataContent().getXML();
								} else {
									content = contentItem.getDataContent().getText();
								}
								break;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 全媒体编辑器与翔宇编辑器插入图片的代码不同，造成稿件入到翔宇后，编辑器不能把图片设置为标题图片 by yhcao
		content = content.replaceAll(
				"type=\"image\" sourcedescription=\"编辑提供的本地文件\"", "");
		return content;
	}

	public String revoke(String articleId, int channel) {
		List<DocContent> docContentList = new ArrayList<DocContent>();
		String result = null;
		int docLibID = 0;
		if (channel == 0) {
			docLibID = LibHelper.getArticleLibID();
		} else {
			docLibID = LibHelper.getArticleAppLibID();
		}

		try {
			long[] docIDs = com.founder.e5.commons.StringUtils
					.getLongArray(articleId);
			FlowReader flowReader = (FlowReader) Context
					.getBean(FlowReader.class);
			List<com.founder.e5.doc.Document> articles = new ArrayList<com.founder.e5.doc.Document>();
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			for (long docID : docIDs) {
				com.founder.e5.doc.Document article = docManager.get(docLibID,
						docID);
				DocContent docContent = new DocContent();
				// 获得当前稿件所处流程的ID，然后找到这个流程的第一个节点，作为稿件的当前节点，同时设置稿件的当前状态
				int curflowID = article.getCurrentFlow();
				FlowNode[] nodes = flowReader.getFlowNodes(curflowID);
				article.setCurrentNode(nodes[0].getID());
				article.setCurrentStatus(nodes[0].getWaitingStatus());
				// 解锁操作
				article.setLocked(false);
				article.set("a_status", Article.STATUS_PUB_NOT);
				articles.add(article);
				docContent.setArticle(article);
				docContentList.add(docContent);
			}
			// 同时修改多个稿件，使用事务
			String error = save(docLibID, articles);

			if (error == null) {
				// 发布撤稿消息
				articleManager.revoke(articles);
			}

			// 发布库的未发布稿件 删除处理
			deleteArticles(docLibID, docIDs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return genResultXML(docContentList, result);
	}

	private String save(int docLibID, List<com.founder.e5.doc.Document> articles) {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		// 同时修改多个稿件，使用事务
		DBSession conn = null;
		try {
			conn = E5docHelper.getDBSession(docLibID);
			conn.beginTransaction();

			for (com.founder.e5.doc.Document article : articles) {
				docManager.save(article, conn);
			}
			conn.commitTransaction();
			return null;
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			e.printStackTrace();
			return "操作中出现错误：" + e.getLocalizedMessage();
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
	}

	public String getLoginResult() {
		StringBuffer resultBuffer = new StringBuffer();
		StorageDevice device = InfoHelper.getNewsEditDevice();
		resultBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		resultBuffer.append("<ExchangeResult>");
		resultBuffer.append("<Doc>");
		resultBuffer.append("<success>");
		resultBuffer.append("true");
		resultBuffer.append("</success><tokenId>");
		resultBuffer.append("0");
		resultBuffer.append("</tokenId><ftp>");
		resultBuffer.append("<url>");
		resultBuffer.append(device.getFtpDeviceURL());
		resultBuffer.append("</url><port>");
		resultBuffer.append("21");
		resultBuffer.append("</port><username>");
		resultBuffer.append(device.getUserName());
		resultBuffer.append("</username><password>");
		resultBuffer.append(device.getUserPassword());
		resultBuffer.append("</password></ftp>");
		resultBuffer.append("<error><errorCode></errorCode><errorCause></errorCause></error></Doc>");
		resultBuffer.append("</ExchangeResult>");
		return resultBuffer.toString();
	}

	private void deleteArticles(int DocLibID, long[] docIDs) throws E5Exception {
		int colLibID = LibHelper.getLibIDByOtherLib(DocTypes.COLUMN.typeID(),
				DocLibID);

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		List<com.founder.e5.doc.Document> articles = new ArrayList<com.founder.e5.doc.Document>();
		for (long docID : docIDs) {
			boolean isRel = false;
			Set<Long> relSet = new TreeSet<Long>();
			com.founder.e5.doc.Document article = docManager.get(DocLibID,
					docID);
			long colID = article.getLong("a_columnID");
			String colName = getColumnName(colLibID, colID);
			long columnID = article.getLong("a_columnID");

			// 关联栏目
			if (colID > 0) { // colID=0，可能是在“我的稿件”里
				String relColumnId = article.getString("a_columnRelID");
				if (!StringUtils.isBlank(relColumnId)) {
					long[] ids = com.founder.e5.commons.StringUtils
							.getLongArray(relColumnId);
					for (long id : ids) {
						if (id != colID) {
							relSet.add(id);
						} else {
							isRel = true;
						}
					}
				}
			}

			if (isRel) {
				// 当前栏目是稿件的关联栏目,修改稿件的关联栏目ID和关联栏目名称
				dealRelCols(colName, article, relSet);
				// 处理一下所有栏目,修改a_columnAll字段去掉该栏目ID
				dealAllCols(colID, article);
			} else {
				if (colID == 0 || colID == columnID) {// colID=0，可能是在“我的稿件”里
					// 当前栏目是稿件的主栏目，则设置稿件DeleteFlag=1
					article.setDeleteFlag(1);
				} else {
					// 栏目是一个聚合栏目，修改a_columnAll字段去掉该栏目ID。同样，若稿件已发布，则触发消息
					dealAllCols(colID, article);
				}
			}
			articles.add(article);
		}
		// 使用事务保存
		save(DocLibID, articles);
	}

	private String getColumnName(int colLibID, long colID) throws E5Exception {
		Column col = colReader.get(colLibID, colID);
		return (col == null) ? "" : col.getName();
	}

	// 当前栏目是稿件的关联栏目,修改稿件的关联栏目ID和关联栏目名称
	private void dealRelCols(String colName,
			com.founder.e5.doc.Document article, Set<Long> relSet) {
		String relColumn = article.getString("a_columnRel");
		Set<String> relNameSet = new TreeSet<String>();
		if (!StringUtils.isBlank(relColumn)) {
			String[] names = StringUtils.split(relColumn, ",");
			for (String name : names) {
				if (!name.equalsIgnoreCase(colName)) {
					relNameSet.add(name);
				}
			}
		}
		StringBuilder column_relId = new StringBuilder();
		for (Long l : relSet) {
			if (column_relId.length() > 0)
				column_relId.append(",");
			column_relId.append(l);
		}

		StringBuilder column_rel = new StringBuilder();
		for (String s : relNameSet) {
			if (column_rel.length() > 0)
				column_rel.append(",");
			column_rel.append(s);
		}
		article.set("a_columnRel", column_rel.toString());
		article.set("a_columnRelID", column_relId.toString());
	}

	// 该栏目是一个聚合栏目，修改a_columnAll字段去掉该栏目ID
	private void dealAllCols(long colID, com.founder.e5.doc.Document article) {
		Set<Long> allIdSet = new TreeSet<Long>();
		String columnAllId = article.getString("a_columnAll");
		if (!StringUtils.isBlank(columnAllId)) {
			long[] ids = com.founder.e5.commons.StringUtils.getLongArray(
					columnAllId, ";");
			for (long id : ids) {
				if (id != colID) {
					allIdSet.add(id);
				}
			}
		}
		StringBuilder column_allId = new StringBuilder();
		for (Long l : allIdSet) {
			if (column_allId.length() > 0)
				column_allId.append(";");
			column_allId.append(l);
		}
		article.set("a_columnAll", column_allId.toString());
	}
	
	
	/**
	 * 根据articleId查看当前稿件状态
	 * 
	 * @param articleId 稿件ID
	 *            
	 * @param channel 渠道ID
	 *            
	 * @throws E5Exception
	 */



	public String refresh(String articleId, int channel) {
		Document document = DocumentHelper.createDocument();
		document.setXMLEncoding("utf-8");
		Element root = document.addElement("LIST");		
		int docLibID = 0;
		if (channel == 0) {
			docLibID = LibHelper.getArticleLibID();
		} else {
			docLibID = LibHelper.getArticleAppLibID();
		}
		try {
			long[] docIDs = com.founder.e5.commons.StringUtils
					.getLongArray(articleId);
						
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			if(docIDs!=null){
			for (long docID : docIDs) {
				com.founder.e5.doc.Document article = docManager.get(docLibID,
						docID);
				Element item = root.addElement("article");
				item.addAttribute("docID",docID + "");
				item.addElement("columnID").addText(article.getLong("a_columnID")+"");
				item.addElement("column").addCDATA(article.getString("a_column"));
				item.addElement("status").addText(article.getInt("a_status")+"");
				}
			}
			
			} catch (Exception e) {
			e.printStackTrace();
		
			}
		return document.asXML();
			
	}


	public String batman()
			throws ParseException, DocumentException, E5Exception, URISyntaxException, MalformedURLException {
		Boolean hasNext = true;
		String result = "同步失败";

		int corpLibID = LibHelper.getLibID(DocTypes.CORPORATION.typeID(), Tenant.DEFAULTCODE);
		int batmanLibID = LibHelper.getLibID(DocTypes.BATMAN.typeID(), Tenant.DEFAULTCODE);
		int page = 0;
		int size = 1000;// 每次取1000条数据，后期应改到配置文件中
		// 发送请求 返回数据
		while (hasNext) {
			hasNext = false;
			String batmanURL = InfoHelper.getConfig("通讯员", "通讯员同步接口") + "?page=" + page + "&size=" + size
					+ "&type=batman";

			String batmanXML = getData(batmanURL);
			page++;
			// 检测是否有错误
			result = isSucess(batmanXML);
			if (result == null) {
				// 检查是否还有后续文件
				hasNext = checkHasNext(batmanXML);
				// 先保存单位信息
				List<com.founder.e5.doc.Document> corporations;

				corporations = parseCorporationXML(batmanXML);
				if (corporations == null) {
					result = "同步失败,请向将单位地区，单位类别，单位行业等分类信息补全";
					log.error(result);
					return result;
				}
				result = saveDocument(corpLibID, corporations);

				if (result == null) {
					// 再保存通讯员信息
					List<com.founder.e5.doc.Document> batmans;
					batmans = parseBatmanXML(batmanXML);
					result = saveDocument(batmanLibID, batmans);
				}
				if (result == null) {
					result = "同步成功";
				} else
					result = "同步失败";
			}
		}
		return result;
	}

	private Boolean checkHasNext(String batmanXML) throws DocumentException {

		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(new StringReader(batmanXML));
		Element parameter = (Element) document.selectSingleNode("//result/success/head/hasnext");
		if (parameter != null && "true".equals(parameter.getText())) {
			return true;
		}
		return false;
	}

	public  String getData(String url)
			throws ParseException, DocumentException, URISyntaxException, MalformedURLException {
		/**
		 * 发送 post请求访问本地应用并根据传递参数不同返回不同结果
		 */
		String result = "";
		// 创建默认的httpClient实例.
		CloseableHttpClient httpclient = HttpClients.createDefault();
		// 创建httppost
		HttpPost httppost = new HttpPost(new URI(url));
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(2000).setConnectTimeout(2000).build();
		httppost.setConfig(requestConfig);

		// 创建参数队列

		try {

			System.out.println("executing request " + httppost.getURI());
			CloseableHttpResponse response = httpclient.execute(httppost);
			try {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					String batmanXML = EntityUtils.toString(entity, "UTF-8");
					result = batmanXML;
				} else {
					int rtCode = response.getStatusLine().getStatusCode();
					result = "" + rtCode;
				}
			} finally {
				response.close();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			log.error(e.getLocalizedMessage());
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			log.error(e1.getLocalizedMessage());
		} catch (IOException e) {
			e.printStackTrace();
			log.error(e.getLocalizedMessage());
		} finally {
			// 关闭连接,释放资源
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;

	}

	@SuppressWarnings("unchecked")
	private List<com.founder.e5.doc.Document> parseCorporationXML(String batmanXML)
			throws DocumentException, E5Exception {
		List<com.founder.e5.doc.Document> corporations = new ArrayList<com.founder.e5.doc.Document>();
		// 取出库中已有的公司，按公司名：ID保存在HashMap中
		HashMap<String, Long> allcorporation = new HashMap<>();
		int corpLibID = LibHelper.getLibID(DocTypes.CORPORATION.typeID(), Tenant.DEFAULTCODE);
		int FVID = DomHelper.getFVIDByDocLibID(corpLibID);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		com.founder.e5.doc.Document[] corps = docManager.find(corpLibID, "SYS_DELETEFLAG=0 order by corp_name", null);
		for (com.founder.e5.doc.Document corp : corps) {
			allcorporation.put(corp.getString("corp_name"), corp.getLong("sys_documentid"));
		}
		// 取出单位ID，单位类别ID等分类信息，以ID:名称方式hash存放
		CatReader catReader = (CatReader) Context.getBean(CatManager.class);

		Category[] corp_types = catReader.getCats(CatTypes.CAT_CORPTYPE.typeID());
		HashMap<String, Integer> corp_typesHashMap = new HashMap<String, Integer>();
		for (Category corp_type : corp_types) {
			corp_typesHashMap.put(corp_type.getCatName(), corp_type.getCatID());
		}
		Category[] corp_trades = catReader.getCats(CatTypes.CAT_TRADE.typeID());
		HashMap<String, Integer> corp_tradesHashMap = new HashMap<String, Integer>();
		for (Category corp_trade : corp_trades) {
			corp_tradesHashMap.put(corp_trade.getCatName(), corp_trade.getCatID());
		}

		Category[] corp_regions = catReader.getCats(CatTypes.CAT_REGION.typeID());
		HashMap<String, String> corp_regionsHashMap = new HashMap<String, String>();
		for (Category corp_region : corp_regions) {
			corp_regionsHashMap.put(corp_region.getCatName(), corp_region.getCascadeID());
		}
		Category[] corp_stocks = catReader.getCats(CatTypes.CAT_STOCK.typeID());
		HashMap<String, Integer> corp_stocksHashMap = new HashMap<String, Integer>();
		for (Category corp_stock : corp_stocks) {
			corp_stocksHashMap.put(corp_stock.getCatName(), corp_stock.getCatID());
		}
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(new StringReader(batmanXML));
		List<Element> parameterList = document.selectNodes("//result/success/body/batmans/batman");
		for (Element parameter : parameterList) {
			String batmanID = parameter.element("batmanid").getText();
			long corporationID = 0;
			String corp_name = parameter.element("bmcompany").getText();
			if (!StringUtils.isBlank(corp_name)) {// 公司名称不能为空
				// 先对比名称，判断数据库中是否已有
				com.founder.e5.doc.Document corporation = null;
				if (allcorporation.containsKey(corp_name)) {// 已经存在，使用现有ID
					corporationID = allcorporation.get(corp_name);
					corporation = docManager.get(corpLibID, corporationID);
				} else { // 不存在，使用新ID
					corporationID = InfoHelper.getNextDocID(DocTypes.CORPORATION.typeID());
					corporation = docManager.newDocument(corpLibID, corporationID);
				}

				corporation.set("corp_name", corp_name);
				corporation.set("sys_folderID", FVID);
				String bminctype = parameter.element("bminctype").getText();
				corporation.set("corp_type", bminctype);
				if (corp_typesHashMap.containsKey(bminctype)) {
					corporation.set("corp_type_ID", corp_typesHashMap.get(bminctype));
				} else {
					System.out.println("通讯员"+batmanID+" 单位类型分类中没有 "+bminctype);
					log.error("通讯员"+batmanID+" 单位类型分类中没有 "+bminctype);
					return null;
				}
				String bmregion = parameter.element("bmregion").getText();
				corporation.set("corp_region", bmregion);
				if (corp_regionsHashMap.containsKey(bmregion)) {
					corporation.set("corp_regionID", corp_regionsHashMap.get(bmregion));
				} else{
					System.out.println("通讯员"+batmanID+" 地区分类中没有 "+bmregion);
					log.error("通讯员"+batmanID+" 地区分类中没有 "+bmregion);
					return null;
				}
				String bmhangye = parameter.element("bmhangye").getText();
				corporation.set("corp_trade", bmhangye);
				if (corp_tradesHashMap.containsKey(bmhangye)) {
					corporation.set("corp_trade_ID", corp_tradesHashMap.get(bmhangye));
				} else {
					System.out.println("通讯员"+batmanID+" 单位行业分类中没有 "+bmhangye);
					log.error("通讯员"+batmanID+" 单位行业分类中没有 "+bmhangye);
					return null;
				}

				String bmssqk = parameter.element("bmssqk").getText();
				corporation.set("corp_stock", bmhangye);
				if (corp_stocksHashMap.containsKey(bmssqk)) {
					corporation.set("corp_stock_ID", corp_stocksHashMap.get(bmssqk));
				} else{
					System.out.println("通讯员"+batmanID+" 单位上市情况分类中没有 "+bmssqk);
					log.error("通讯员"+batmanID+" 单位上市情况分类中没有 "+bmssqk);
					return null;
				}
				corporation.set("corp_siteID", 1);
				if (parameter.element("siteID") != null) {
					corporation.set("corp_siteID", parameter.element("siteID").getText());
				}

				corporation.set("corp_park", parameter.element("bmyuanqu").getText());
				corporation.set("corp_stock", parameter.element("bmssqk").getText());
				corporation.set("corp_stockCode", parameter.element("bmssdm").getText());
				String lastModifiedTime = parameter.element("lastmodified").getText();
				if (!StringUtils.isBlank(lastModifiedTime)) {
					Timestamp modifiedTime = new Timestamp(
							DateUtils.parse(lastModifiedTime, "yyyy-MM-dd HH:mm:ss").getTime());
					corporation.setLastmodified(modifiedTime);

				}

				corporations.add(corporation);
			}
		}

		return corporations;
	}

	private String isSucess(String batmanXML) throws DocumentException {
		// 长度小于4 返回的是错误代码
		if (batmanXML.length() < 4) {
			return batmanXML;
		} else {
			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(new StringReader(batmanXML));

			Element error = (Element) document.selectSingleNode("//result/error");
			if (error != null) {
				Element errorMsg = (Element) document.selectSingleNode("//result/errorMsg");
				return error.getText() + ": " + errorMsg.getText();
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private List<com.founder.e5.doc.Document> parseBatmanXML(String batmanXML) throws DocumentException, E5Exception {
		List<com.founder.e5.doc.Document> batmans = new ArrayList<com.founder.e5.doc.Document>();

		String usercode = InfoHelper.getConfig("通讯员","通讯员默认对应用户");
		HashMap<Integer, Long> allBatman = new HashMap<>();
		int batmanLibID = LibHelper.getLibID(DocTypes.BATMAN.typeID(), Tenant.DEFAULTCODE);
		int corpLibID = LibHelper.getLibID(DocTypes.CORPORATION.typeID(), Tenant.DEFAULTCODE);
		int FVID = DomHelper.getFVIDByDocLibID(batmanLibID);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		com.founder.e5.doc.Document[] batmansFromSQL = docManager.find(batmanLibID, "SYS_DELETEFLAG=0 order by bm_amID",
				null);
		for (com.founder.e5.doc.Document batman : batmansFromSQL) {// 取出库中已有的通讯员，按通讯员全媒体ID：通讯员翔宇ID保存在HashMap中
			allBatman.put(batman.getInt("bm_amID"), batman.getLong("sys_documentid"));
		}

		// 取出库中已有的公司，按公司名：ID保存在HashMap中
		HashMap<String, Long> allcorporation = new HashMap<>();
		com.founder.e5.doc.Document[] corps = docManager.find(corpLibID, "SYS_DELETEFLAG=0 order by corp_name", null);
		for (com.founder.e5.doc.Document corp : corps) {
			allcorporation.put(corp.getString("corp_name"), corp.getLong("sys_documentid"));
		}
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(new StringReader(batmanXML));
		List<Element> parameterList = document.selectNodes("//result/success/body/batmans/batman");
		for (Element parameter : parameterList) {
			// 先对比全媒体ID，判断数据库中是否已有
			long batmanID = 0;
			int bm_amID = Integer.parseInt(parameter.element("batmanid").getText());
			com.founder.e5.doc.Document batman = null;
			if (allBatman.containsKey(bm_amID)) {// 已经存在，使用现有ID
				batmanID = allBatman.get(bm_amID);
				batman = docManager.get(batmanLibID, batmanID);
			} else { // 不存在，使用新ID 这里和翔宇用户使用同样的ID类型,防止因为ID值相同导致稿件无法判断来源
				batmanID = InfoHelper.getNextDocID(DocTypes.USEREXT.typeID());
				batman = docManager.newDocument(batmanLibID, batmanID);

				// TODO 设置默认密码？
			}
			batman.set("bm_amID", bm_amID);
			batman.set("sys_folderID", FVID);
			batman.set("bm_code", parameter.element("bmname").getText());
			UserReader userReader = (UserReader)Context.getBean(UserReader.class);
			User curUser = userReader.getUserByCode(usercode);
			if(curUser!=null){
				batman.set("bm_user", usercode);
				batman.set("bm_user_ID", curUser.getUserID());
			}
			if(parameter.element("bmtype")!=null){
				batman.set("bm_type", parameter.element("bmtype").getText());
			}
			batman.set("bm_name", parameter.element("bmrealname").getText());
			batman.set("bm_address", parameter.element("bmaddress").getText());
			batman.set("bm_zip", parameter.element("bmzipcode").getText());
			batman.set("bm_duty", parameter.element("bmduty").getText());
			batman.set("bm_phone", parameter.element("bmhandphone").getText());
			batman.set("bm_email", parameter.element("bmemail").getText());
			batman.set("bm_weibo", parameter.element("bmweibo").getText());
			batman.set("bm_weixin", parameter.element("bmweixin").getText());
			batman.set("bm_siteID", 1);
			if (parameter.element("siteID") != null) {
				batman.set("bm_siteID", parameter.element("siteID").getText());
			}

			String bmcompany = parameter.element("bmcompany").getText();
			batman.set("bm_corporation", bmcompany);
			if (!StringUtils.isBlank(bmcompany)) {
				long bm_corporationID = allcorporation.get(bmcompany);
				batman.set("bm_corporation_ID", bm_corporationID);
			}
			String lastModifiedTime = parameter.element("lastmodified").getText();
			if (!StringUtils.isBlank(lastModifiedTime)) {
				Timestamp modifiedTime = new Timestamp(
						DateUtils.parse(lastModifiedTime, "yyyy-MM-dd HH:mm:ss").getTime());
				batman.setLastmodified(modifiedTime);

			}

			batmans.add(batman);
		}
		return batmans;
	}

	/**
	 * 保存
	 * 
	 * @param docLibID
	 * @return
	 */
	private String saveDocument(int docLibID, List<com.founder.e5.doc.Document> DocumentList) {
		DocumentManager docManager = DocumentManagerFactory.getInstance();

		DBSession conn = null;
		try {

			conn = E5docHelper.getDBSession(docLibID);
			conn.beginTransaction();
			for (com.founder.e5.doc.Document Document : DocumentList) {
				// 保存稿件
				docManager.save(Document, conn);
			}
			// 提交transaction
			conn.commitTransaction();
			return null;
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			e.printStackTrace();
			return e.getLocalizedMessage();
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
	}
	private Integer getUserIDByName(String userName){
		UserReader userReader = (UserReader) Context.getBean(UserReader.class);
		Integer userID = null;
		try {
			User[] users = userReader.getUsersByName(userName);
			if(users!=null && users.length>0)
				userID = users[0].getUserID();
		} catch (E5Exception e) {
		}
		return userID;
	}
}
