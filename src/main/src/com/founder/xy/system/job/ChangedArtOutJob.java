package com.founder.xy.system.job;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.doc.FlowRecord;
import com.founder.e5.doc.FlowRecordManager;
import com.founder.e5.doc.FlowRecordManagerFactory;
import com.founder.e5.scheduler.BaseJob;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.jpublish.PublishHelper;
import com.founder.xy.system.Tenant;
import com.founder.xy.system.site.Site;
import com.founder.xy.system.site.SiteManager;

/**
 * 将重改、删除稿件列表生成Json输出到外网，供其他系统统计
 * 每5分钟执行一次
 * @author kangxw
 */
public class ChangedArtOutJob extends BaseJob{

	private DocumentManager docManager = DocumentManagerFactory.getInstance();
	private FlowRecordManager  frManager = FlowRecordManagerFactory.getInstance();

	@Override
	protected void execute() throws E5Exception {
		//第一时间获取当前时间
        Timestamp nowTime = DateUtils.getTimestamp();
        Calendar b5Time = Calendar.getInstance();
        b5Time.add(Calendar.MINUTE, -5);// 5分钟之前的时间
        Timestamp startTime = new Timestamp(b5Time.getTimeInMillis());
        try {
			SiteManager siteManager = (SiteManager)Context.getBean("siteManager");
	        int siteLibID = LibHelper.getLibID(DocTypes.SITE.typeID(), Tenant.DEFAULTCODE);
	        List<Site> siteList = siteManager.getSites(siteLibID);
	        for(Site tempSite:siteList){
	            //得到站点资源路径
	            String[] sitePath = InfoHelper.readSiteInfo(tempSite.getId());
	            if( sitePath!=null && !StringUtils.isBlank(sitePath[0])) {
	                String jsonFilePath = sitePath[0] + File.separator+ "changedArt" + File.separator + "Site"+tempSite.getId() + File.separator + DateUtils.format(nowTime,"yyyy-MM-dd") + ".json";
	                File outFile = new File(jsonFilePath);
	                JSONArray jsonArray = new JSONArray();
	                if(outFile.exists()){
	                	String jsonStr = FileUtils.readFileToString(outFile, "UTF-8");
	                	if(!StringUtils.isBlank(jsonStr)){
	                		jsonArray = JSONArray.fromObject(jsonStr);
	                	}
	                }
	                JSONObject result = new JSONObject();
	                result.put("updatetime", DateUtils.format(nowTime, "yyyy-MM-dd HH:mm:ss"));
	                getChangedArtList(startTime, nowTime, tempSite.getId(), 0, result);
	                getChangedArtList(startTime, nowTime, tempSite.getId(), 1, result);
	                if (result.get("articles")!=null || result.get("apparticles")!=null) {
//	                	System.out.println("jsonFilePath = " + jsonFilePath);
//	                	System.out.println("sitePath[0] = " + sitePath[0]);
//	                	System.out.println(result.toString());
	                	jsonArray.add(result);
	                	FileUtils.writeStringToFile(new File(jsonFilePath), jsonArray.toString(), "UTF-8");
	                	// 生成json消息文件
	                	PublishHelper.writeTransPath(jsonFilePath,sitePath[0]);
	                }
	            }

	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 查询出某站点指定时间内有过修改的稿件
	 * @param startTime
	 * @param endTime
	 * @param siteID
	 * @param isApp 1为app； 0为web
	 * @param result
	 */
	private void getChangedArtList(Timestamp startTime, Timestamp endTime, int siteID, int isApp, JSONObject result){
		int LibID = isApp==0 ? LibHelper.getArticleLibID() : LibHelper.getArticleAppLibID();
        try {
        	//先查询出某站点指定时间内有过修改的稿件
        	Document[] arts = null;
        	if(Context.getDBType().equalsIgnoreCase("oracle")){
        		String conditions = "a_siteid = ? AND  SYS_LASTMODIFIED between to_date('"+formatDate(startTime)+"','yyyy-mm-dd hh24:mi:ss') AND to_date('"+formatDate(endTime)+"','yyyy-mm-dd hh24:mi:ss') ORDER BY SYS_LASTMODIFIED ASC";
        		Object[] params = new Object[]{siteID};
        		arts = docManager.find(LibID, conditions, params);
        	}else{
        		String conditions = "a_siteid = ? AND  SYS_LASTMODIFIED between ? AND ? ORDER BY SYS_LASTMODIFIED ASC";
        		Object[] params = new Object[]{siteID,startTime, endTime};
        		arts = docManager.find(LibID, conditions, params);
        	}
			if(arts.length>0){
				JSONArray changed = new JSONArray();
				for(Document art : arts){
					//判断修改是否影响web页面
					getArticleJson(art, startTime, endTime, changed);
				}
				if(changed.size()>0)
					result.put(isApp==0 ? "articles" :"apparticles", changed);
			}
		} catch (E5Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 判断是否有过修改
	 * @param doc
	 * @param startTime
	 * @param endTime
	 * @param changed
	 */
	private void getArticleJson(Document doc, Timestamp startTime, Timestamp endTime, JSONArray changed) {
		JSONObject article = new JSONObject();
		article.put("id", doc.getDocID());
		article.put("topic", doc.getTopic());
		try {
			//获取所有的流程记录，倒序排列
			FlowRecord[] frs = frManager.getAssociatedFRs(doc.getDocLibID(),doc.getDocID(), false);
			
			String sign = "";
			Timestamp pubTime = null;
			for (FlowRecord fr : frs) {
				//判断指定时间段内最新的操作是发布还是撤稿，如果没有这两个操作，认为web页面没有修改
				if(fr.getStartTime().before(endTime) && fr.getEndTime().after(startTime)){
					if(fr.getOperation().indexOf("发布") > -1){
						sign = "change";
						pubTime = fr.getEndTime();
						break;
					} 
					if(fr.getOperation().indexOf("撤稿") > -1){
						sign = "revoke";
						break;
					} 
				} 
			}
			//最新的操作为撤稿，认为web页面被撤回
			if (sign.equals("revoke")) {
				article.put("operation", sign);
				changed.add(article);
				return;
			}
			//最新的操作为发布,判断该发布操作前，是否还有发布操作：若有，认为web页面有过修改
			if (sign.equals("change")) {
				for (FlowRecord fr : frs) {
					if(fr.getEndTime().before(pubTime) && fr.getOperation().indexOf("发布") > -1){
						article.put("operation", sign);
						changed.add(article);
						return;
					}
				}
			}
		} catch (E5Exception e) {
			e.printStackTrace();
		} 
	}
	
	private String formatDate(Timestamp date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
        return format.format(date);
	}
}
