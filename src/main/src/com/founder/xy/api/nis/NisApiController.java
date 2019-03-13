
package com.founder.xy.api.nis;

import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.context.E5Exception;
import com.founder.e5.sys.StorageDevice;
import com.founder.e5.sys.StorageDeviceManager;
import com.founder.e5.sys.SysFactory;
import com.founder.xy.api.app.MobileAppApiManager;
import com.founder.xy.api.column.ColumnApiManager;
import com.founder.xy.api.ext.ArticleExtApiManager;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.jpublish.PublishHelper;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 提供互动相关api，与App外网api通讯
 */
@Controller
@RequestMapping("/api/app")
public class NisApiController {
	@Autowired
	private DiscussApiManager discussApiManager;
	@Autowired
	private ActivityApiManager activityApiManager;
	@Autowired
	private QAApiManager qaApiManager;
	@Autowired
	private OtherApiManager otherApiManager;
	@Autowired
	private EventApiManager eventManager;
	@Autowired
	private SubjectApiManager subjectApiManager;
	@Autowired
	private ArticleExtApiManager articleApiManager;
	@Autowired
	private ColumnApiManager columnApiManager;
	@Autowired
	private MobileAppApiManager mobileAppApiManager;
	@Autowired
	private TopicsApiManager topicsApiManager;

	/**
	 * 稿件点击事件/点赞事件
	 */
	@RequestMapping(value = "event.do")
	public void event(HttpServletResponse response, String data)
			throws E5Exception {
		boolean result = eventManager.event(data);
		InfoHelper.outputText(String.valueOf(result), response);
	}

	/**
	 * @deprecated 评论直接提交，已不再使用
	 */
	@RequestMapping(value = "discuss.do")
	public void discuss(HttpServletResponse response, String data)
			throws E5Exception {
		boolean result = discussApiManager.discuss(data);
		InfoHelper.outputText(String.valueOf(result), response);
	}

	/**
	 * 获取稿件的热门评论
	 */
	@RequestMapping(value = "discussHot.do")
	public void discussHot(HttpServletResponse response, long id,
			int source, int siteID) throws E5Exception, SQLException {
		boolean result = discussApiManager.getDiscussHot(id,
				source,siteID);
		InfoHelper.outputText(String.valueOf(result), response);
	}

	/**
	 * 评论列表
	 */
	@RequestMapping(value = "discussView.do")
	public void discussView(HttpServletResponse response, int id,
			@RequestParam(defaultValue = "0") int flat,
			int page, int source, int siteID) throws E5Exception,
			SQLException {
		boolean result;
		//flat=1，表示取扁平化的评论列表
		if (flat == 1)
			result = discussApiManager.getDiscussFlat(id, page, source,siteID);
		else
			result = discussApiManager.getDiscussView(id, page, source,siteID);
		
		InfoHelper.outputText(String.valueOf(result), response);
	}

    /**
     * 评论列表
     * @param isOrderByPraise 是否按照点赞数排序，直播评论的需求，默认为按照点赞数排序
     */
    @RequestMapping(value = "discussViewOrderByPraise.do")
    public void discussViewOrderByPraise(HttpServletResponse response, int id,
                            @RequestParam(defaultValue = "1") int isOrderByPraise,
                            int page, int source, int siteID) throws E5Exception,
            SQLException {
        boolean result = discussApiManager.getDiscussViewOrderByPraise(id, page, source,siteID,isOrderByPraise);

        InfoHelper.outputText(String.valueOf(result), response);
    }

	/**
	 * 获取评论的最新评论
	 */
	@RequestMapping(value = "discussReply.do")
	public void discussReply(HttpServletResponse response, long id, int page, int siteID) throws E5Exception, SQLException {
		boolean result = discussApiManager.getDiscussReply(id, page,siteID);
		InfoHelper.outputText(String.valueOf(result), response);
	}

	/**
	 * 取各种数据的评论数、点赞数
	 */
	@RequestMapping(value = "getDiscussCount.do")
	public void getDiscussCount(HttpServletRequest request,
							 HttpServletResponse response, int id, int source, int siteID,String type)
			throws E5Exception {
		String result = discussApiManager.getDiscussCount(id, type, source);
		InfoHelper.outputText(result, response);
	}

	/**
	 * 获取投票数
	 */
	@RequestMapping(value = "voteCount.do")
	public void voteCount(HttpServletResponse response, int id, int siteID)
			throws E5Exception {
		boolean result = otherApiManager.voteCount(id);
		InfoHelper.outputText(String.valueOf(result), response);
	}

	/**
	 * 查看投票结果
	 */
	@RequestMapping(value = "voteResult.do")
	public void voteResult(HttpServletResponse response, int id, int siteID)
			throws E5Exception {
		boolean result = otherApiManager.voteResult(id);
		InfoHelper.outputText(String.valueOf(result), response);
	}

	/**
	 * 提交投票
	 */
	@RequestMapping(value = "vote.do")
	public void vote(HttpServletResponse response, String data)
			throws E5Exception {
		String result = otherApiManager.vote(data);
		InfoHelper.outputText(result, response);
	}

	/**
	 * 订阅
	 */
	@RequestMapping(value = "topicSub.do")
	public void subscribe(HttpServletResponse response, String data)
			throws E5Exception {
		boolean result = otherApiManager.subscribe(data);
		InfoHelper.outputText(String.valueOf(result), response);
	}

	/**
	 * 订阅取消
	 */
	@RequestMapping(value = "topicSubCancel.do")
	public void topicSubCancel(HttpServletResponse response, String data)
			throws E5Exception {
		boolean result = otherApiManager.subCancel(data);
		InfoHelper.outputText(String.valueOf(result), response);
	}

	/**
	 * 报料
	 */
	@RequestMapping(value = "tipoff.do")
	public void tipoff(HttpServletResponse response, String data)
			throws E5Exception {
		boolean result = otherApiManager.tipoff(data);
		InfoHelper.outputText(String.valueOf(result), response);
	}
	//问政列表
	@RequestMapping(value = "qaList.do")
	public void qaList(HttpServletResponse response,
			@RequestParam(value = "siteID", defaultValue = "1")int siteID, 
			int page, int groupId)throws E5Exception{
		boolean result = qaApiManager.qaList(siteID,page,groupId);
		InfoHelper.outputJson(String.valueOf(result), response);
	}
	//问政提交(改为由外网写入延迟入库，不需要内网api)
	/*
	@RequestMapping(value = "saveQa.do")
	public void saveQa(HttpServletResponse response, String data)
			throws E5Exception {
		boolean result = otherApiManager.saveQa(data);
		InfoHelper.outputText(String.valueOf(result), response);
	}*/
	//问政详情
	@RequestMapping(value = "qaDetail.do")
	public void qaDetai(HttpServletResponse response, 
			@RequestParam(value = "siteId", defaultValue = "1")int siteID,int fileId)
			throws E5Exception {
		boolean result = qaApiManager.qaDetail(siteID,fileId);
		InfoHelper.outputText(String.valueOf(result), response);
	}
    //我的问政
    @RequestMapping(value = "myQA.do")
    public void myQA(HttpServletResponse response,
            @RequestParam(defaultValue = "0")int page,
            @RequestParam(value = "siteId", defaultValue = "1")int siteID,
            int userID)
            throws E5Exception {
        boolean result = qaApiManager.myQA(siteID, userID,page);
        InfoHelper.outputText(String.valueOf(result), response);
    }


	/**
	 * 浏览我的报料
	 */
	@RequestMapping(value = "myTipoff.do")
	public void myTipoff(HttpServletResponse response, int userID,int siteID) throws E5Exception {
		boolean result = otherApiManager.myTipoff(userID, siteID);
		InfoHelper.outputText(String.valueOf(result), response);
	}
	/**
	 * 浏览我的报料
	 */
	@RequestMapping(value = "tipoffContent.do")
	public void tipoffContent(HttpServletResponse response, int docID,int siteID) throws E5Exception {
		boolean result = otherApiManager.tipoffContent(docID, siteID);
		InfoHelper.outputText(String.valueOf(result), response);
	}

	/**
	 * 意见反馈
	 */
	@RequestMapping(value = "feed.do")
	public void feed(HttpServletResponse response, String data)
			throws E5Exception {
		boolean result = otherApiManager.feed(data);
		InfoHelper.outputText(String.valueOf(result), response);
	}

	/**
	 * 上传手机图片，视频
	 * 主要干了三件事情
	 * 1. 写文件
	 * 2. 把文件路径封装成json
	 * 3. 为抽图做些准备
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "upload.do")
	public void upload(HttpServletResponse response, HttpServletRequest request)
			throws Exception {

		long start = System.currentTimeMillis();
		// 获得目录
		Date date = Calendar.getInstance().getTime();
		String ymdAddr = new SimpleDateFormat("yyyyMM/dd/").format(date);
		String ymdAddrExt = new SimpleDateFormat("yyyyMM~dd~").format(date);

		//获得类型
		String fileType = request.getParameter("fileType");

		// 获得文件数据流
		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setSizeMax(1024 * 1024 * 50); // 50兆

		List<DiskFileItem> items = upload.parseRequest(request);
		Iterator<DiskFileItem> iter = items.iterator();

		//获得路径
		StorageDevice device_pic = InfoHelper.getDevice("互动", "手机图片存储设备");
		String path_pic = device_pic.getHttpDeviceURL();
		String webroot_pic = device_pic.getNtfsDevicePath();

		StorageDevice device_vio = InfoHelper.getDevice("互动", "手机视频存储设备");
		String path_vio = device_vio.getHttpDeviceURL();
		StorageDeviceManager sdManager = SysFactory.getStorageDeviceManager();
		InputStream fis = null;
		long end = System.currentTimeMillis();
		System.out.println("上传使用了" + (end - start) + " ms");

		JSONArray jsonArr = new JSONArray();		// fileList
		JSONObject rtnJson = new JSONObject();		// resultJson
		try {
			if ("video".equals(fileType)) {
				if (iter.hasNext()) {
					uploadFile(iter, fis, sdManager, ymdAddr, jsonArr,
							device_vio, path_vio, null, ymdAddrExt, fileType);
				}
				if (iter.hasNext()) {
					uploadFile(iter, fis, sdManager, ymdAddr, jsonArr,
							device_pic, path_pic, webroot_pic, ymdAddrExt,
							fileType);
				}
			} else {
				while (iter.hasNext()) {
					uploadFile(iter, fis, sdManager, ymdAddr, jsonArr,
							device_pic, path_pic, webroot_pic, ymdAddrExt,
							fileType);
				}
			}
			rtnJson.put("success", true);
			rtnJson.put("errorInfo", "");
			rtnJson.put("fileList", jsonArr);
		} catch (Exception ex) {
			ex.printStackTrace();
			rtnJson.put("errorInfo", ex.getMessage());
			rtnJson.put("success", false);
		} finally {
			ResourceMgr.closeQuietly(fis);
		}
		InfoHelper.outputText(rtnJson.toString(), response);
	}

	/**
	 * 获得储存路径，并放到redis里面
	 */
	@RequestMapping(value = "getPathInfo.do")
	public void getPathInfo(HttpServletResponse response)
			throws E5Exception {
		StorageDevice picDevice = InfoHelper.getDevice("互动", "手机图片存储设备");
		String webPath = picDevice.getHttpDeviceURL();
		String folderPath = picDevice.getNtfsDevicePath();
		String extractPath = folderPath + "/extracting/";
		
		JSONObject pathInfoJson = new JSONObject();
		pathInfoJson.put("webPath", webPath);
		pathInfoJson.put("folderPath", folderPath);
		pathInfoJson.put("extractPath", extractPath);
		
		RedisManager.set(RedisKey.APP_UPLOAD_PATH, pathInfoJson.toString());
		
		InfoHelper.outputText(String.valueOf(true), response);
	}


	/**
	 * 登录信息，为了推送服务而收集的客户端信息。
	 * 改为使用个推自己的功能，因此此接口不再需要。
	 */
	@RequestMapping(value = "loginfo.do")
	public void loginfo(HttpServletResponse response, String data)
			throws E5Exception {
		/*
		boolean result = otherApiManager.loginfo(data);
		InfoHelper.outputText(String.valueOf(result), response);
		*/
	}

	/**
	 * 我的评论
	 */
	@RequestMapping(value = "myDiscuss.do")
	public void myDiscuss(HttpServletResponse response, int userID, 
			int siteID) throws E5Exception {
		boolean result = discussApiManager.getMyDiscuss(userID,siteID);
		InfoHelper.outputText(String.valueOf(result), response);
	}
	/**
	 * 评论我的
	 */
	@RequestMapping(value = "myDiscussReply.do")
	public void myDiscussReply(HttpServletResponse response, int userID,
			int siteID) throws E5Exception {
		boolean result = discussApiManager.myDiscussReply(userID);
		InfoHelper.outputText(String.valueOf(result), response);
	}

	/**
	 * 上传手机图片，视频,图片还要创建抽图信息文件
	 */
	private void uploadFile(Iterator<DiskFileItem> iter, InputStream fis,
			StorageDeviceManager sdManager, String ymdAddr, JSONArray jsonArr,
			StorageDevice device_, String path_, String webroot_pic,
			String ymdAddrExt, String fileType) throws Exception {

		JSONObject json = new JSONObject();
		DiskFileItem item = iter.next();
		fis = item.getInputStream();
		String fileName = item.getName();
		fileName = UUID.randomUUID()
				+ fileName.substring(fileName.lastIndexOf("."));

		sdManager.write(device_, ymdAddr + fileName, fis);
		json.put("url", path_ + "/" + ymdAddr + fileName);
		json.put("fileName", item.getName());
		jsonArr.add(json);

		if (webroot_pic != null) {
			// 生成抽图信息文件
			String destPath = webroot_pic + "/extracting/";
			File dest = new File(destPath);
			if (!dest.exists()) {
				dest.mkdirs();
			}
			File file = new File(destPath + ymdAddrExt + fileName);
			file.createNewFile();
		}

		if(webroot_pic != null){
			int index = webroot_pic.lastIndexOf("/");
			String nisPic = webroot_pic.substring(index+1);
			PublishHelper.writePath(nisPic + "~" + ymdAddrExt + fileName,PublishHelper.getTransPath());
		}
	}

	/**
	 * 获取话题列表
	 */
	@RequestMapping(value = "subjectList.do")
	public void getSubjectList(HttpServletRequest request,
			HttpServletResponse response, int siteID, int page ,
			Long userid) throws E5Exception {
		boolean result = subjectApiManager.getSubjectList(siteID, 0, page,userid,0);
		InfoHelper.outputText(String.valueOf(result), response);
	}

	/**
	 * 获取分类话题列表
	 */
	@RequestMapping(value = "subjectListWithCat.do")
	public void getSubjectListWithCat(HttpServletRequest request,
			HttpServletResponse response, int siteID, int catID, int page, Long userid) throws E5Exception {
		boolean result = subjectApiManager.getSubjectList(siteID, catID,page,userid,1);
		InfoHelper.outputText(String.valueOf(result), response);
	}

	/**
	 * 获取单个话题
	 */
	@RequestMapping(value = "getSubject.do")
	public void getSubject(HttpServletRequest request,
			HttpServletResponse response, int siteID, int id)
			throws E5Exception {
		boolean result = subjectApiManager.getSubject(siteID, id);
		InfoHelper.outputText(String.valueOf(result), response);
	}
	/**
	 * 我的问吧（发起）
	 */
	@RequestMapping(value = "mySubject.do")
	public void getmySubject(HttpServletRequest request,
							HttpServletResponse response, int siteID, Long userid, int page) throws E5Exception {
		boolean result = subjectApiManager.getSubjectList(siteID,0,page,userid,2);
		InfoHelper.outputText(String.valueOf(result), response);
	}

	/**
	 * 我的问吧（关注）
	 */
	@RequestMapping(value = "mySubjectSubscribe.do")
	public void getmySubjectSubscribe(HttpServletRequest request,
			HttpServletResponse response, int siteID, Long userid, int page) throws E5Exception {
		boolean result = subjectApiManager.getMySubscribes(siteID, userid, page);
		InfoHelper.outputText(String.valueOf(result), response);
	}
	/**
	 * 我关注的话题ID
	 */
	@RequestMapping(value = "mySubjectIDsSubscribe.do")
	public void getmySubjectIDsSubscribe(HttpServletRequest request,
									  HttpServletResponse response, Long userid) throws E5Exception {
		boolean result = subjectApiManager.getMySubscribeIDs(userid);
		InfoHelper.outputText(String.valueOf(result), response);
	}

	/**
	 * 热门提问列表
	 */
	@RequestMapping(value = "questionListHot.do")
	public void getQuestionListHot(HttpServletRequest request,
			HttpServletResponse response, int siteID, int subjectID, int page
			) throws E5Exception {
		// 以评论数倒序
		boolean result = subjectApiManager.getQuestionList(siteID, subjectID,
				page, "hot");
		InfoHelper.outputText(String.valueOf(result), response);
	}

	/**
	 * 提问列表
	 */
	@RequestMapping(value = "questionList.do")
	public void getQuestionList(HttpServletRequest request,
			HttpServletResponse response, int siteID, int subjectID, int page
			) throws E5Exception {
		// 以创建时间倒序
		boolean result = subjectApiManager.getQuestionList(siteID, subjectID,
                page, "new");
		InfoHelper.outputText(String.valueOf(result), response);
	}
	/**
	 * 问答详情
	 */
	@RequestMapping(value="questionDetail.do")
	public void getQuestionDetail(HttpServletRequest request,
								  HttpServletResponse response,int siteID,int subjectQAID) throws E5Exception{
		boolean result=subjectApiManager.getQuestionDetail(siteID,subjectQAID);
		InfoHelper.outputText(String.valueOf(result), response);
	}

	/**
	 * 我的提问列表
	 */
	@RequestMapping(value = "myQuestion.do")
	public void getMyQuestion(HttpServletRequest request,
			HttpServletResponse response, int siteID, Long userID, int page
			) throws E5Exception {
		// 以创建时间倒序
		boolean result = subjectApiManager.getMyQuestions(siteID, userID, page);
		InfoHelper.outputText(String.valueOf(result), response);
	}

	/**
	 * 交汇点同步原创稿件
	 */
	@RequestMapping(value = "originalArticle.do")
	public void getOriginalArticle(HttpServletRequest request,
			HttpServletResponse response, int siteID, int page, int size,
			String startTime, String endTime) throws E5Exception {
		String result = articleApiManager.getArticlesCopyright(siteID, page, size,
				startTime, endTime);
		InfoHelper.outputJson(result, response);
	}

	/**
	 * 交汇点同步栏目信息
	 */
	@RequestMapping(value = "examineColumns.do")
	public void getExamineColumns(HttpServletRequest request,
			HttpServletResponse response, int siteID) throws E5Exception {
		String result = columnApiManager.getColumns(siteID);
		InfoHelper.outputJson(result, response);
	}

	/**
	 * 移动app接口
	 */
	@RequestMapping(value = "mobileApp.do")
	public void getMobileApp(HttpServletRequest request,
			HttpServletResponse response, String appKey, String channel)
			throws E5Exception {
		// 以创建时间倒序
		boolean result = mobileAppApiManager.getMobileApp(appKey, channel);
		InfoHelper.outputJson(String.valueOf(result), response);
	}
    //活动列表
	@RequestMapping(value = "activityList.do")
	public void activityList(HttpServletResponse response,
			@RequestParam(value = "siteID", defaultValue = "1")int siteID,
			int start, int count)throws E5Exception{
		boolean result = activityApiManager.activityList(siteID,start, count);
		InfoHelper.outputJson(String.valueOf(result), response);
	}

	//活动详情
	@RequestMapping(value = "activityDetail.do")
	public void activityDetail(HttpServletResponse response,
			@RequestParam(value = "siteId", defaultValue = "1")int siteID, 
			int fileId)throws E5Exception{
		boolean result = activityApiManager.activityDetail(siteID,fileId);
		InfoHelper.outputJson(String.valueOf(result), response);
	}

	@RequestMapping(value = "entryList.do")
	public void activityEntryList(HttpServletResponse response, int fileId)throws E5Exception{
		boolean result = activityApiManager.activityEntryList(fileId);
		InfoHelper.outputJson(String.valueOf(result), response);
	}
	@RequestMapping(value = "myActivityList.do")
	public void myActivityList(HttpServletResponse response, int siteID, int userID,int page)throws E5Exception{
		boolean result = activityApiManager.myActivityList(siteID, userID, page);
		InfoHelper.outputJson(String.valueOf(result), response);
	}

	/**
	 * 获取我的收藏
	 */
	@RequestMapping(value = "myFav.do")
	public void myFav(HttpServletRequest request,
							 HttpServletResponse response, int siteID, int userID)
			throws E5Exception {

		boolean result = otherApiManager.myFav(siteID, userID);
		InfoHelper.outputText(String.valueOf(result), response);
	}

	/**
	 * 取各种数据的互动计数
	 */
	@RequestMapping(value = "getCounts.do")
	public void getCounts(HttpServletRequest request,
							 HttpServletResponse response, int id, int source)
			throws E5Exception {
		boolean result = otherApiManager.getCounts(id, source);
		InfoHelper.outputText(String.valueOf(result), response);
	}

	@RequestMapping(value = "hotTopics.do")
	public void hotTopics(HttpServletResponse response, int siteID)throws E5Exception{
		boolean result = topicsApiManager.hotTopics(siteID);
		InfoHelper.outputJson(String.valueOf(result), response);
	}

	@RequestMapping(value = "topicsByGroup.do")
	public void topicsByGroup(HttpServletResponse response, int siteID)throws E5Exception{
		boolean result = topicsApiManager.topicsByGroup(siteID);
		InfoHelper.outputJson(String.valueOf(result), response);
	}
	
	/**
	 * 删除评论
	 */
	@RequestMapping(value = "discussDelete.do")
	public void discussDelete(HttpServletResponse response, String data)
			throws E5Exception {
		boolean result = discussApiManager.discussDelete(data);
		InfoHelper.outputText(String.valueOf(result), response);
	}
	
	@RequestMapping(value = "topics.do")
	public void topics(HttpServletResponse response, int siteID)throws E5Exception{
		boolean result = topicsApiManager.topics(siteID);
		InfoHelper.outputJson(String.valueOf(result), response);
	}
	
	@RequestMapping(value = "articleTopics.do")
	public void articleTopics(HttpServletResponse response, int articleID,int channel)throws E5Exception{
		boolean result = topicsApiManager.articleTopics(articleID,channel);
		InfoHelper.outputJson(String.valueOf(result), response);
	}

	@RequestMapping(value = "articleTopicsByGroup.do")
	public void articleTopicsByGroup(HttpServletResponse response, int articleID,int channel,int groupID)throws E5Exception{
		boolean result = topicsApiManager.articleTopicsByGroup(articleID,channel,groupID);
		InfoHelper.outputJson(String.valueOf(result), response);
	}

    @RequestMapping(value = "webTopicsByGroup.do")
    public void webTopicsByGroup(HttpServletResponse response, int siteID, int groupID)throws E5Exception{
        boolean result = topicsApiManager.webTopicsByGroup(siteID, groupID);
        InfoHelper.outputJson(String.valueOf(result), response);
    }
}
