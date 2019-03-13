package com.founder.xy.system.job;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
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
import com.founder.e5.scheduler.BaseJob;
import com.founder.xy.commons.CatTypes;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.system.Tenant;


public class batmanSynJob extends BaseJob{
	public batmanSynJob() {
		super();
		log = Context.getLog("xy.batmanSyn");
	}
	
	@Override
	protected void execute() throws E5Exception {


			log.info("-----通讯员同步开始-----");
			String result;
			try {
				result = batman();
				log.info("-----"+result+"-----");
			} catch (ParseException | MalformedURLException | DocumentException | URISyntaxException e) {
				
				e.printStackTrace();
			}			
			log.info("-----通讯员同步完毕-----");
	
	
}
	public String batman() throws ParseException, DocumentException, E5Exception, URISyntaxException, MalformedURLException {
		Boolean hasNext = true;
		String result = "同步失败";

		int corpLibID = LibHelper.getLibID(DocTypes.CORPORATION.typeID(), Tenant.DEFAULTCODE);
		int batmanLibID = LibHelper.getLibID(DocTypes.BATMAN.typeID(), Tenant.DEFAULTCODE);
		int page = 0;
		int size = 1000;// 每次取1000条数据，后期应改到配置文件中
		// 发送请求 返回数据
		while (hasNext) {
			hasNext=false;
			String batmanURL = InfoHelper.getConfig("通讯员", "通讯员同步接口") + "?page=" + page + "&size=" + size
					+ "&type=batman";
			
			String batmanXML = getData(batmanURL);
			page++;
			// 检测是否有错误
			result = isSucess(batmanXML);
			if (result==null) {		
				// 检查是否还有后续文件
				hasNext = checkHasNext(batmanXML);
				// 先保存单位信息
				List<com.founder.e5.doc.Document> corporations;

				corporations = parseCorporationXML(batmanXML);
				if (corporations==null){
					result="同步失败,请向将单位地区，单位类别，单位行业等分类信息补全";
					return result;
				}
				 result = saveDocument(corpLibID,corporations);
				
				if (result == null) {
					// 再保存通讯员信息
					List<com.founder.e5.doc.Document> batmans;
					batmans = parseBatmanXML(batmanXML);
					 result = saveDocument(batmanLibID,batmans);
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

	public static String getData(String url) throws ParseException, DocumentException, URISyntaxException, MalformedURLException {
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
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
		List<com.founder.e5.doc.Document> corporations = new ArrayList<>();
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
		HashMap<String, Integer> corp_typesHashMap = new HashMap<>();
		for (Category corp_type : corp_types) {
			corp_typesHashMap.put(corp_type.getCatName(), corp_type.getCatID());
		}
		Category[] corp_trades = catReader.getCats(CatTypes.CAT_TRADE.typeID());
		HashMap<String, Integer> corp_tradesHashMap = new HashMap<>();
		for (Category corp_trade : corp_trades) {
			corp_tradesHashMap.put(corp_trade.getCatName(), corp_trade.getCatID());
		}

		Category[] corp_regions = catReader.getCats(CatTypes.CAT_REGION.typeID());
		HashMap<String, String> corp_regionsHashMap = new HashMap<>();
		for (Category corp_region : corp_regions) {
			corp_regionsHashMap.put(corp_region.getCatName(), corp_region.getCascadeID());
		}
		Category[] corp_stocks = catReader.getCats(CatTypes.CAT_STOCK.typeID());
		HashMap<String, Integer> corp_stocksHashMap = new HashMap<>();
		for (Category corp_stock : corp_stocks) {
			corp_stocksHashMap.put(corp_stock.getCatName(), corp_stock.getCatID());
		}
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(new StringReader(batmanXML));
		List<Element> parameterList = document.selectNodes("//result/success/body/batmans/batman");
		for (Element parameter : parameterList) {

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
				String batmanID = parameter.element("batmanid").getText();
				corporation.set("corp_name", corp_name);
				corporation.set("sys_folderID", FVID);
				String bminctype = parameter.element("bminctype").getText();
				corporation.set("corp_type", bminctype);
				if (corp_typesHashMap.containsKey(bminctype)) {
					corporation.set("corp_type_ID", corp_typesHashMap.get(bminctype));
				}
				
				else {
					log.error("通讯员"+batmanID+" 单位类型分类中没有 "+bminctype);
					return null;
				}
				

				String bmregion = parameter.element("bmregion").getText();
				corporation.set("corp_region", bmregion);
				if (corp_regionsHashMap.containsKey(bmregion)) {
					corporation.set("corp_regionID", corp_regionsHashMap.get(bmregion));
				}
				else {
					log.error("通讯员"+batmanID+" 地区分类中没有 "+bmregion);
					return null;
				}
				String bmhangye = parameter.element("bmhangye").getText();
				corporation.set("corp_trade", bmhangye);
				if (corp_tradesHashMap.containsKey(bmhangye)) {
					corporation.set("corp_trade_ID", corp_tradesHashMap.get(bmhangye));
				}
				else {
					log.error("通讯员"+batmanID+" 单位行业分类中没有 "+bmhangye);
					return null;
				}
				String bmssqk = parameter.element("bmssqk").getText();
				corporation.set("corp_stock", bmhangye);
				if (corp_stocksHashMap.containsKey(bmssqk)) {
					corporation.set("corp_stock_ID", corp_stocksHashMap.get(bmssqk));
				}
				else {
					log.error("通讯员"+batmanID+" 单位上市情况分类中没有 "+bmssqk);
					return null;
				}	
				corporation.set("corp_siteID", 1);
				if(parameter.element("siteID")!=null){
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
			if (error!=null){
				Element errorMsg = (Element) document.selectSingleNode("//result/errorMsg");
				return error.getText()+": "+errorMsg.getText();
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private List<com.founder.e5.doc.Document> parseBatmanXML(String batmanXML) throws DocumentException, E5Exception {
		List<com.founder.e5.doc.Document> batmans = new ArrayList<>();
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
			} else { // 不存在，使用新ID
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
			// TODO siteID 对应用户？
			batman.set("bm_name", parameter.element("bmrealname").getText());
			batman.set("bm_address", parameter.element("bmaddress").getText());
			batman.set("bm_zip", parameter.element("bmzipcode").getText());
			batman.set("bm_duty", parameter.element("bmduty").getText());
			batman.set("bm_phone", parameter.element("bmhandphone").getText());
			batman.set("bm_email", parameter.element("bmemail").getText());
			batman.set("bm_weibo", parameter.element("bmweibo").getText());
			batman.set("bm_weixin", parameter.element("bmweixin").getText());
			batman.set("bm_siteID", 1);
			if(parameter.element("siteID")!=null){
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
	 * @param DocumentList
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

}

