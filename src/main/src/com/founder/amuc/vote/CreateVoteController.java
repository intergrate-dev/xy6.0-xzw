package com.founder.amuc.vote;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import net.sf.json.JSONObject;

import com.founder.amuc.action.ActionHelper;
import com.founder.amuc.commons.Constant;
import com.founder.amuc.commons.DateHelper;
import com.founder.amuc.commons.InfoHelper;
import com.founder.amuc.commons.DateFormatAmend;
import com.founder.amuc.member.MemberReader;
import com.founder.xy.jpublish.PublishHelper;
import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.Pair;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.doc.util.E5docHelper;
import com.founder.e5.dom.DocLib;
import com.founder.e5.web.BaseController;
import com.founder.e5.web.WebUtil;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import com.founder.xy.commons.JsonHelper;
import com.founder.e5.sys.StorageDevice;
import com.founder.e5.sys.StorageDeviceManager;
import com.founder.e5.sys.StorageDeviceReader;
import java.io.InputStream;
import com.founder.e5.commons.DomHelper;
import com.founder.e5.db.DBType;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.commons.DocTypes;
import com.founder.e5.commons.StringUtils;

@Controller
@RequestMapping("/amuc/createVote")
public class CreateVoteController extends BaseController{

	@RequestMapping("/add.do")
	@SuppressWarnings({ "rawtypes" })
	private void add(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception {
		String myEditor = get(request, "myEditor");
		String vsHostUnit = get(request, "vsHostUnit");
		String vsTitle = get(request, "vsTitle");
		String vsTypes = get(request, "vsTypes");
		String vsOptionIsNull = get(request, "vsOptionIsNull");
		String vsVoteType = get(request, "vsVoteType");
		String vsVoteMode = get(request, "vsVoteMode");  //投票模式
		String vsVoteInApp = get(request, "vsVoteInApp");  //是否只能在app中参与
		String vsVotePageStyle = get(request, "vsVotePageStyle");  //投票页样式
		String vsVoteRepeatOne = get(request, "vsVoteRepeatOne");  //是否允许重复提交单个选项
		String vsOptionType = get(request, "vsOptionType");
		String vsEndTime = get(request, "vsEndTime");
		String vsStarTime = get(request, "vsStarTime");
		String UUID = get(request, "UUID");
		String userFiled = get(request, "userFiled");
		String ifRequired = get(request, "ifRequired");
		String str1[] = userFiled.split(",");
		String str2[] = ifRequired.split(",");
		int siteID = Integer.parseInt(get(request, "siteID"));
		String vsUserInfoRule = "{";
		for (int i = 0; i < str1.length; i++) {
			if(i!=str1.length-1){
				vsUserInfoRule+="\""+str1[i]+"\":\""+str2[i]+"\"";
				vsUserInfoRule+=",";
			}else{
				vsUserInfoRule+="\""+str1[i]+"\":\""+str2[i]+"\"";
			}
			
		}
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_VOTE);
		long newDocID = InfoHelper.getID(docLib.getDocTypeID());
		Document doc = docManager.newDocument(docLib.getDocLibID(), newDocID);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		doc.set("SYS_FOLDERID", docLib.getFolderID());
		doc.set("SYS_DELETEFLAG", 0);
        doc.set("vsTitle", vsTitle);
        doc.set("vsHostUnit", vsHostUnit);
        doc.set("vsVoteType", Integer.parseInt(vsVoteType));
        doc.set("vsVoteMode", Integer.parseInt(vsVoteMode));
        doc.set("vsVoteInApp", Integer.parseInt(vsVoteInApp));
        doc.set("vsVotePageStyle", vsVotePageStyle);
        doc.set("vsVoteRepeatOne", Integer.parseInt(vsVoteRepeatOne));
        //存储该投票的地址
        String root_url = ActionHelper.getSysPara("互动", "外网资源地址");  //获取推广根地址
        //其中voteid表示投票的ID，vType表示投票的类型（0代表实名投票，1代表用户IP投票），uid=必须要带上，与新华APP联调时用到了
        String vsAddress = root_url + "amuc/vote/vote.html?voteid=" + newDocID + "&siteID=" + siteID + "&vOptionType=" + vsOptionType+ "&vPageStyle=" + vsVotePageStyle + "&vType=" + vsVoteType+ "&uid=";
        doc.set("vsAddress", vsAddress);  //存储投票地址
        
        if(!myEditor.equals("")||myEditor!=null){
        	 doc.set("vsActivityIntro", myEditor);
        }
        doc.set("vsTypes", Integer.parseInt(vsTypes));
        doc.set("vsOptionType", Integer.parseInt(vsOptionType));
        doc.set("vsOptionIsNull", Integer.parseInt(vsOptionIsNull));
        doc.set("m_siteID", siteID);
        if(vsEndTime!=null&&!vsEndTime.equals("")){
        	 doc.set("vsEndTime", DateFormatAmend.timeStampDispose(vsEndTime));
        }
        if(vsStarTime!=null&&!vsStarTime.equals("")){
       	 doc.set("vsStarTime", DateFormatAmend.timeStampDispose(vsStarTime));
       }
        doc.set("vsCreated",DateFormatAmend.timeStampDispose(df.format(new Date())));
        if(userFiled!=null&&!userFiled.equals("")){
        	doc.set("vsUserInfoRule", vsUserInfoRule+"}");
        }
        docManager.save(doc);
        
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("voteID", doc.getDocID());
        jsonObject.put("UUID", UUID);
        jsonObject.put("vsOptionType", vsOptionType);
        jsonObject.put("vsTypes", vsTypes);
        response.getWriter().write(jsonObject.toString());
        response.setContentType("text/json;charset=UTF-8");
	}
	

	@RequestMapping("/addVote.do")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private ModelAndView addVote(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception {
		MemberReader memberReader = new MemberReader();
		List<Pair> memberInfo = memberReader.getFields();
		model.put("memberInfo", memberInfo);
		return new ModelAndView("amuc/vote/add", model);
	}
	
	@SuppressWarnings({ "rawtypes" })
	@RequestMapping("/editVoteOp.do")
	private void editVoteOp(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception {
		String myEditor = get(request, "myEditor");
		String vsHostUnit = get(request, "vsHostUnit");
		String vsTitle = get(request, "vsTitle");
		String vsTypes = get(request, "vsTypes");
		String vsOptionIsNull = get(request, "vsOptionIsNull");
		String vsOptionType = get(request, "vsOptionType");
		String vsVoteType = get(request, "vsVoteType");
		String vsVoteMode = get(request, "vsVoteMode");  //投票模式
		String vsVoteInApp = get(request, "vsVoteInApp");  //是否只能在app中参与
		String vsVotePageStyle = get(request, "vsVotePageStyle");  //投票页样式
		String vsVoteRepeatOne = get(request, "vsVoteRepeatOne");  //是否允许重复提交单个选项
		String vsEndTime = get(request, "vsEndTime");
		String vsStarTime = get(request, "vsStarTime");
		String UUID = get(request, "UUID");
		String docID = get(request, "docID");
		int siteID = Integer.parseInt(get(request, "siteID"));
		//存储该投票的地址
        String root_url = ActionHelper.getSysPara("互动", "外网资源地址");  //获取推广根地址
        String vsAddress = root_url + "amuc/vote/vote.html?voteid=" + docID + "&siteID=" + siteID + "&vOptionType=" + vsOptionType+ "&vPageStyle=" + vsVotePageStyle + "&vType=" + vsVoteType+ "&uid=";
		String userFiled = get(request, "userFiled");
		String ifRequired = get(request, "ifRequired");
		String str1[] = userFiled.split(",");
		String str2[] = ifRequired.split(",");
		String vsUserInfoRule = "{";
		for (int i = 0; i < str1.length; i++) {
			if(i!=str1.length-1){
				vsUserInfoRule+="\""+str1[i]+"\":\""+str2[i]+"\"";
				vsUserInfoRule+=",";
			}else{
				vsUserInfoRule+="\""+str1[i]+"\":\""+str2[i]+"\"";
			}
			
		}
		DBSession conn = null;
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_VOTE, "uc");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		try {
			conn = E5docHelper.getDBSession(docLib.getDocLibID());
			conn.beginTransaction(); // 开始事务

			if(userFiled!=null&&!"".equals(userFiled)){
				String sql = "update xy_membervotesettings set vsTitle=?,vsVoteType=?,vsVoteMode=?,vsVoteInApp=?,vsVotePageStyle=?,vsVoteRepeatOne=?,vsOptionIsNull=?,vsHostUnit=?,vsLastModified=?,vsTypes=?,vsEndTime=?,vsStarTime=?,vsOptionType=?,vsUserInfoRule=?,vsActivityIntro=?,vsAddress=? where SYS_DOCUMENTID=?";
				Object[] params = new Object[]{vsTitle,Integer.parseInt(vsVoteType),Integer.parseInt(vsVoteMode),Integer.parseInt(vsVoteInApp),vsVotePageStyle,Integer.parseInt(vsVoteRepeatOne),Integer.parseInt(vsOptionIsNull),vsHostUnit,DateFormatAmend.timeStampDispose(df.format(new Date())),Integer.parseInt(vsTypes),DateFormatAmend.timeStampDispose(vsEndTime),DateFormatAmend.timeStampDispose(vsStarTime),Integer.parseInt(vsOptionType),vsUserInfoRule+"}",myEditor,vsAddress,Integer.parseInt(docID)};
				conn.executeUpdate(sql, params);
			}else{
				String sql = "update xy_membervotesettings set vsTitle=?,vsVoteType=?,vsVoteMode=?,vsVoteInApp=?,vsVotePageStyle=?,vsVoteRepeatOne=?,vsOptionIsNull=?,vsHostUnit=?,vsLastModified=?,vsTypes=?,vsEndTime=?,vsStarTime=?,vsOptionType=?,vsUserInfoRule=?,vsActivityIntro=?,vsAddress=? where SYS_DOCUMENTID=?";
				Object[] params = new Object[]{vsTitle,Integer.parseInt(vsVoteType),Integer.parseInt(vsVoteMode),Integer.parseInt(vsVoteInApp),vsVotePageStyle,Integer.parseInt(vsVoteRepeatOne),Integer.parseInt(vsOptionIsNull),vsHostUnit,DateFormatAmend.timeStampDispose(df.format(new Date())),Integer.parseInt(vsTypes),DateFormatAmend.timeStampDispose(vsEndTime),DateFormatAmend.timeStampDispose(vsStarTime),Integer.parseInt(vsOptionType),"",myEditor,vsAddress,Integer.parseInt(docID)};
				conn.executeUpdate(sql, params);
			}
			
			conn.commitTransaction();
			
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			e.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(conn);
		} 
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("voteID", docID);
		jsonObject.put("UUID", UUID);
		jsonObject.put("vsOptionType", vsOptionType);
		jsonObject.put("vsTypes", vsTypes);
        response.getWriter().write(jsonObject.toString());
        response.setContentType("text/json;charset=UTF-8");
	}
	
	@RequestMapping(value = "/editVote.do")
	@SuppressWarnings({ "rawtypes", "unchecked", "finally" })
	private ModelAndView editVote(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception {
		String docID = request.getParameter("DocIDs");
		DBSession conn = null;
		IResultSet rs = null;
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_VOTE, "");
		VoteSettings voteSettings = new VoteSettings();
		try {
			conn = E5docHelper.getDBSession(docLib.getDocLibID());
			String sql = "select * from xy_membervotesettings where SYS_DOCUMENTID=? and SYS_DELETEFLAG=0"; 
			Object[] params = new Object[]{docID};
			rs = conn.executeQuery(sql, params);
			
			while(rs.next()){
				voteSettings.setVsTitle(rs.getString("vsTitle"));
				voteSettings.setVsTypes(rs.getInt("vsTypes"));
				voteSettings.setVsOptionType(rs.getInt("vsOptionType"));
				voteSettings.setVsHostUnit(rs.getString("vsHostUnit"));
				voteSettings.setVsVoteType(rs.getInt("vsVoteType"));
				voteSettings.setVsVoteMode(rs.getInt("vsVoteMode"));
				voteSettings.setVsVoteInApp(rs.getInt("vsVoteInApp"));
				voteSettings.setVsVotePageStyle(rs.getInt("vsVotePageStyle"));
				voteSettings.setVsVoteRepeatOne(rs.getInt("vsVoteRepeatOne"));
				voteSettings.setVsOptionIsNull(rs.getInt("vsOptionIsNull"));
				if(rs.getString("vsEndTime")!=null){
					voteSettings.setVsEndTime(rs.getString("vsEndTime").substring(0, rs.getString("vsEndTime").length()-2));
				}else{
					voteSettings.setVsEndTime("");
				}
				if(rs.getString("vsStarTime")!=null){
					voteSettings.setVsStarTime(rs.getString("vsStarTime").substring(0, rs.getString("vsStarTime").length()-2));	
				}else{
					voteSettings.setVsStarTime("");	
				}
				if(rs.getString("vsActivityIntro")!=null&&!rs.getString("vsActivityIntro").equals("")){
					voteSettings.setVsActivityIntro(rs.getString("vsActivityIntro"));
				}
				if(rs.getString("vsUserInfoRule")!=null&&!rs.getString("vsUserInfoRule").equals("")){
					voteSettings.setVsUserInfoRule(rs.getString("vsUserInfoRule"));
				}
			}
			MemberReader memberReader = new MemberReader();
			List<Pair> memberInfos = memberReader.getFields();
			if(voteSettings.getVsUserInfoRule()!=null){
				JSONObject jsonObj = JSONObject.fromObject(voteSettings.getVsUserInfoRule());
				Iterator it = jsonObj.keys();
				List<Temp> list = new ArrayList<Temp>();
				while (it.hasNext()) {  
		            String key = (String) it.next();  
		            String value = jsonObj.getString(key);  
		            for (int i = 0; i < memberInfos.size(); i++) {
		            	 if(key.equals(memberInfos.get(i).getValue())){
		            		 Temp a = new Temp();
		                 	a.setKey(key);
		                 	a.setValue(value);
		                 	list.add(a);
		                 }
		    		}
		        }  
				for (int i = 0; i < memberInfos.size(); i++) {
					for(int k=0;k<list.size();k++){
						if(list.get(k).getKey().equals(memberInfos.get(i).getValue())){
							memberInfos.remove(i);
						}
					}
				}
				model.put("list", list);
			}
			
			
			model.put("memberInfos", memberInfos);
			model.put("voteSettings", voteSettings);
			model.put("@VIEWNAME@", "amuc/vote/add");
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			e.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(conn);
			return new ModelAndView("amuc/vote/add",model);
		} 
	}

	@Override
	protected void handle(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception {
		
	}
	
	@RequestMapping("/upload.do")
	private void upload(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception {
    	Map<String, Object> maps = new HashMap<String, Object>();
    	String code = "";
		StringBuilder msg = new StringBuilder();
    	FileItem file = getFileItem(request);

		if (file==null||file.getSize() <= 0) {
			maps.put("msg", "上传头像接口：图片文件为空");
			maps.put("code", "0");
			JSONObject jsonres = JSONObject.fromObject(maps);
			outputJson(String.valueOf(jsonres), response);
			return;
		}
		HashMap<String, String> resultUp  =  uploadPortraitImp(file);
		if("sucess".equals(resultUp.get("code"))){
			
			saveScoreConfig(request,resultUp.get("httppath"));
			code = "1";
			msg.append(resultUp.get("httppath"));
			
		}else{
			code = "0";
			msg.append(resultUp.get("error"));
		}
		maps.put("code", code);
		maps.put("msg", msg.toString());
		JSONObject result = JSONObject.fromObject(maps);
		outputJson(String.valueOf(result), response);
    }
    
    public HashMap<String, String> uploadPortraitImp(FileItem file) throws Exception{
		
		//1.上传头像图片到头像发布服务器
		StorageDeviceReader deviceReader = (StorageDeviceReader)com.founder.e5.context.Context.getBean(StorageDeviceReader.class);
		StorageDevice device = deviceReader.getByName("头像存储");
		StorageDeviceManager deviceManager = (StorageDeviceManager)com.founder.e5.context.Context.getBean(StorageDeviceManager.class);
		
		HashMap<String, String> resu = new HashMap<String, String>();
		
		try {
			InputStream in = file.getInputStream();
			
			String fileName = file.getName();
			String reg = ".+(.JPEG|.jpeg|.JPG|.jpg|.GIF|.gif|.BMP|.bmp|.PNG|.png)$";
	        Pattern pattern = Pattern.compile(reg);
	        Matcher matcher = pattern.matcher(fileName.toLowerCase());
	        //System.out.println("--------------------"+matcher.find());
	        //判断图片后缀
	        if(matcher.find()){
	        	SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmm");
				String newPrefix = formatter.format(new Date());
				String fileExt = fileName.substring(fileName.lastIndexOf(".")+1).toLowerCase();
				String resfilename = "portraitat" + newPrefix + DateUtils.getTimestamp().getTime() + "." + fileExt;
	 			
				deviceManager.write(device, resfilename , in);
				
				String imgHttppath = device.getHttpDeviceURL()+"/"+resfilename;
				String imgFtppath = device.getFtpDeviceURL()+"/"+resfilename;
				//增加trans
				String webroot_pic = device.getNtfsDevicePath();
				if(webroot_pic != null){
					Date date = Calendar.getInstance().getTime();
					String ymdAddrExt = new SimpleDateFormat("yyyyMM~dd~").format(date);
					int index = webroot_pic.lastIndexOf("/");
					String nisPic = webroot_pic.substring(index+1);
					PublishHelper.writePath(nisPic + "~" + resfilename,PublishHelper.getTransPath());
				}
				resu.put("code", "sucess");
				resu.put("httppath", imgHttppath);
				resu.put("ftppath", imgFtppath);
	        }else{
	        	resu.put("code", "fail");
				resu.put("error", "头像文件格式不正确");
	        }
				
			
		} catch (Exception e) {
			e.printStackTrace();
			resu.put("code", "fail");
			resu.put("error", "头像文件上传失败");
		}
		return resu;		
	}
    
    //保存App的参数配置
  	@RequestMapping(value = {"SaveAppConfig.do"})
  	public void saveScoreConfig(HttpServletRequest request,String httppath) throws Exception {
  		//int siteLibID = WebUtil.getInt(request, "siteLibID", 0);
  		int siteID = WebUtil.getInt(request, "siteID", 1);
  		//int siteID = request.getParameter("siteID");
  		//int siteLibID = request.getParameter("siteLibID");
  		String AppName = WebUtil.get(request, "AppName");
  		String AppDownload = WebUtil.get(request, "AppDownload");
  		
  		//取出站点的配置json，替换其中的积分设置json。
  		DocumentManager docManager = DocumentManagerFactory.getInstance();
  		DocLib docLib = LibHelper.getLib(DocTypes.SITE.typeID(),"xy");
  		Document site = docManager.get(docLib.getDocLibID(), siteID);
  		String siteConfig = site.getString("site_config");
  		
  		JSONObject jsonConfig = null;
  		JSONObject jsonobj = null;
  		if (StringUtils.isBlank(siteConfig)) {
  			jsonConfig = new JSONObject();
  		} else {
  			jsonConfig = JsonHelper.getJson(siteConfig);
  		}
  		if(jsonConfig.has("membervote")){
  			jsonobj = JsonHelper.getJson(jsonConfig.getString("membervote"));
  		}else{
  			jsonobj = new JSONObject();
  		}
  		
  		jsonobj.put("AppName", AppName);
  		jsonobj.put("AppHttppath", httppath);
  		jsonobj.put("AppDownload", AppDownload);
  		jsonConfig.put("membervote", jsonobj);
  		site.set("site_config", jsonConfig.toString());
  		
  		docManager.save(site);
  	}
  	
  	/**
	 * 获取上传文件
	 * 
	 * @param request
	 * @return
	 * @throws FileUploadException
	 */
	@SuppressWarnings("unchecked")
	private static FileItem getFileItem(HttpServletRequest request) throws Exception {
		ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
		upload.setHeaderEncoding("UTF-8");
		upload.setSizeMax(10000000L);

		List<FileItem> items = upload.parseRequest(request);
		if(items.size()==0){
			return null;
		}
		FileItem file = (FileItem) items.get(0);
		return file;
	}
}

