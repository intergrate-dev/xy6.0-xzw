package com.founder.amuc.vote;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.founder.amuc.api.vote.VoteInfoManager;
import org.apache.commons.fileupload.FileItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import net.sf.json.JSONObject;

import com.founder.amuc.commons.Constant;
import com.founder.amuc.commons.FormHelper;
import com.founder.amuc.commons.InfoHelper;
import com.founder.xy.jpublish.PublishHelper;
import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.Log;
import com.founder.e5.context.Context;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.e5.sys.StorageDevice;
import com.founder.e5.sys.StorageDeviceReader;
import com.founder.e5.web.BaseController;
/**
 * 投票选项的相关处理控制层
 */
@Controller
@RequestMapping("/amuc/voteOption")
public class VoteOptionsController extends BaseController {
	@Autowired
	VoteInfoManager voteInfoManager ;

	private Log log = Context.getLog("amuc.vote");
	@Autowired
	private VoteManagerAmuc voteManagerAmuc;

	public VoteManagerAmuc getVoteManager() {
		return voteManagerAmuc;
	}

	public void setVoteManager(VoteManagerAmuc voteManager) {
		this.voteManagerAmuc = voteManager;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected void handle(HttpServletRequest request, HttpServletResponse response,
			Map model) throws Exception {
	}
	
	@RequestMapping("/getVoteUrl.do")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void getVoteUrl(HttpServletRequest request, HttpServletResponse response,Map map) throws Exception{
		String vid = get(request, "voteID", "");  //投票ID
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib voteDocLib =  InfoHelper.getLib(Constant.DOCTYPE_VOTE);
		String con = "SYS_DELETEFLAG=0 and SYS_DOCUMENTID=?";
		String[] column = {"vsAddress"};
		Document[] ms_docs = docManager.find(voteDocLib.getDocLibID(), con, new Object[]{vid},column);
		JSONObject jsonObject = new JSONObject();
		if(ms_docs.length == 0){   //代表没有查到该投票活动
			map.put("errormsg", "没有查到该投票活动");
			map.put("vsAddress", null);
			jsonObject.put("ret", "0");
			jsonObject.put("retinfo", map);
		}
		String vsAddress = ms_docs[0].getString("vsAddress");
		map.put("errormsg", "");
		map.put("vsAddress", vsAddress);
		jsonObject.put("ret", "1");
		jsonObject.put("retinfo", map);
		
		output(jsonObject.toString(), response);
	}
	
	/**
	 * 添加投票选项
	 */
	@RequestMapping("/addVoteOptionByOne.do")
	@SuppressWarnings("unchecked")
	private void addVoteOptions(HttpServletRequest request, HttpServletResponse response,Map map) throws Exception{
		String optext = get(request, "voteoptext", "");
		int voteId = getInt(request, "voteid");
		int themeId = getInt(request, "themeid", 0);
		JSONObject jsonObject = new JSONObject();
		VoteOptions voteOptions = new VoteOptions();
		if(voteId>0 && themeId>0){
			Document doc = voteManagerAmuc.saveVoteOption(voteId, optext,themeId);
			voteOptions.setDeleteFlag(doc.getDeleteFlag());
			voteOptions.setVoteOpId(doc.getInt("SYS_DOCUMENTID"));
			voteOptions.setVoteId(doc.getInt("voVoteID"));
			voteOptions.setVoName(doc.getString("voName"));
			voteOptions.setVoImgAdd(doc.getString("voImgAdd"));
			voteOptions.setVoClassification(doc.getString("voClassification"));
			voteOptions.setVoType(doc.getInt("voType"));
			voteOptions.setVoIndex(doc.getInt("voIndex"));
			voteOptions.setVoThemeId(doc.getInt("voThemeID"));
			if(voteOptions!=null){
				voteManagerAmuc.updateOptionNum(themeId, "add");
			}
			map.put("errormsg", "");
			map.put("opinfo", voteOptions);
			jsonObject.put("ret", "1");
			jsonObject.put("retinfo", map);
		}else{
			map.put("errormsg", "投票参数获取失败！");
			map.put("opinfo", null);
			jsonObject.put("ret", "0");
			jsonObject.put("retinfo", map);
		}
		output(jsonObject.toString(), response);
	}
	
	/**
	 * 修改投票选项文字
	 */
	@RequestMapping("/updatevoteoptiontext.do")
	@SuppressWarnings("unchecked")
	private void updateVoteOptionText(HttpServletRequest request, HttpServletResponse response,Map map) throws Exception{
		String optext = get(request, "voteoptext", "");
		int opId = getInt(request, "opid");
		JSONObject jsonObject = new JSONObject();
		VoteOptions voteOptions = new VoteOptions();
		if(opId>0){
			voteOptions= voteManagerAmuc.updateOptionTextById(opId, optext);
			map.put("errormsg", "");
			map.put("opinfo", voteOptions);
			jsonObject.put("ret", "1");
			jsonObject.put("retinfo", map);
		}else{
			map.put("errormsg", "投票选项参数获取失败！");
			map.put("opinfo", null);
			jsonObject.put("ret", "0");
			jsonObject.put("retinfo", map);
		}
		output(jsonObject.toString(), response);
	}
	/**
	 * 修改投票选项视频
	 */
	@RequestMapping("/updateVoteOptionVideo.do")
	@SuppressWarnings("unchecked")
	private void updateVoteOptionVideo(HttpServletRequest request, HttpServletResponse response,Map map) throws Exception{
		String voVideaAdd = get(request, "voVideaAdd", "");
		int opId = getInt(request, "opid");
		JSONObject jsonObject = new JSONObject();
		VoteOptions voteOptions = new VoteOptions();
		if(opId>0){
			voteOptions= voteManagerAmuc.updateOptionVideoById(opId, voVideaAdd);
			map.put("errormsg", "");
			map.put("opinfo", voteOptions);
			jsonObject.put("ret", "1");
			jsonObject.put("retinfo", map);
		}else{
			map.put("errormsg", "投票选项参数获取失败！");
			map.put("opinfo", null);
			jsonObject.put("ret", "0");
			jsonObject.put("retinfo", map);
		}
		output(jsonObject.toString(), response);
	}
	/**
	 * 删除投票选项视频
	 */
	@RequestMapping("/delVideoAddr.do")
	@SuppressWarnings("unchecked")
	private void delVideoAddr(HttpServletRequest request, HttpServletResponse response,Map map) throws Exception{
		int opId = getInt(request, "opid");
		JSONObject jsonObject = new JSONObject();
		VoteOptions voteOptions = new VoteOptions();
		if(opId>0){
			voteOptions= voteManagerAmuc.updateOptionVideoIsNull(opId );
			map.put("errormsg", "");
			map.put("opinfo", voteOptions);
			jsonObject.put("ret", "1");
			jsonObject.put("retinfo", map);
		}else{
			map.put("errormsg", "投票选项参数获取失败！");
			map.put("opinfo", null);
			jsonObject.put("ret", "0");
			jsonObject.put("retinfo", map);
		}
		output(jsonObject.toString(), response);
	}
	/**
	 * 修改投票选项排序码
	 */
	@RequestMapping("/updateoptionindex.do")
	@SuppressWarnings("unchecked")
	private void updateOptionIndex(HttpServletRequest request, HttpServletResponse response,Map map) throws Exception{
		int opId = getInt(request, "opid");
		int optionIndex = getInt(request, "newindex");
		JSONObject jsonObject = new JSONObject();
		VoteOptions voteOptions = new VoteOptions();
		if(opId>0){
			voteOptions= voteManagerAmuc.updateOptionIndex(opId, optionIndex);
			map.put("errormsg", "");
			map.put("opinfo", voteOptions);
			jsonObject.put("ret", "1");
			jsonObject.put("retinfo", map);
		}else{
			map.put("errormsg", "投票选项参数获取失败！");
			map.put("opinfo", null);
			jsonObject.put("ret", "0");
			jsonObject.put("retinfo", map);
		}
		output(jsonObject.toString(), response);
	}
	
	/**
	 * 删除投票选项/启用投票选项
	 * @param request
	 * @param response
	 * @param map
	 * @throws Exception
	 */
	@RequestMapping("/deleteVoteOption.do")
	@SuppressWarnings("unchecked")
	private void deleteVoteOption(HttpServletRequest request, HttpServletResponse response,Map map) throws Exception{
		long docID = getInt(request, "voteID", 0);
		int opId = getInt(request, "opid");
		int themeId = getInt(request, "themeid", 0);
		int operate = getInt(request, "operate", 0);
		String operateDesc = operate == 0 ? "add" : "delete";
		JSONObject jsonObject = new JSONObject();
		if(opId>0 && themeId>0 && voteManagerAmuc.deleteVoteOption(opId, operate)){
			voteManagerAmuc.updateOptionNum(themeId, operateDesc);
			map.put("errormsg", "");
			jsonObject.put("ret", "1");
			jsonObject.put("retinfo", map);
		}else{
			map.put("errormsg", "删除选项失败！");
			jsonObject.put("ret", "0");
			jsonObject.put("retinfo", map);
		}
		// 更新投票统计
		voteInfoManager.refreshVoteRedis(docID, opId, operateDesc);
		output(jsonObject.toString(), response);
	}

	/**
	 * 投票刷新缓存
	 * @param request
	 * @param response
	 * @param map
	 * @throws Exception
	 */
	@RequestMapping("/refreshVoteCache.do")
	@SuppressWarnings("unchecked")
	private void refreshVoteCache(HttpServletRequest request, HttpServletResponse response,Map map) throws Exception{
		long docID = getInt(request, "voteID", 0);
		voteInfoManager.clearRedis((int)docID);
		voteInfoManager.getInfoByRedis((int)docID);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("code", "1");
		jsonObject.put("errorMsg", "缓存刷新完成");
		output(jsonObject.toString(), response);
	}

	/**
	 * 删除投票选项
	 * @param request
	 * @param response
	 * @param map
	 * @throws Exception
	 */
	@RequestMapping("/removeVoteOption.do")
	@SuppressWarnings("unchecked")
	private void removeVoteOption(HttpServletRequest request, HttpServletResponse response,Map map) throws Exception{
		long docID = getInt(request, "voteID", 0);
		int opId = getInt(request, "opid");
		int themeId = getInt(request, "themeid", 0);
		JSONObject jsonObject = new JSONObject();
		Boolean needDecr = voteInfoManager.removeOptRedis(docID, opId);
		if(opId>0 && themeId>0 && voteManagerAmuc.removeVoteOption(opId)){
			if(needDecr)
				voteManagerAmuc.updateOptionNum(themeId, "delete");
			map.put("errormsg", "");
			jsonObject.put("ret", "1");
			jsonObject.put("retinfo", map);
		}else{
			map.put("errormsg", "删除选项失败！");
			jsonObject.put("ret", "0");
			jsonObject.put("retinfo", map);
		}
		// 更新投票统计
		// voteManagerAmuc.refreshVoteRedis(docID, opId, "delete");
		output(jsonObject.toString(), response);
	}

	/**
	 * 根据投票ID初始化投票选项
	 * @param request
	 * @param response
	 * @param map
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/initOptions.do")
	private ModelAndView initOptions(HttpServletRequest request, HttpServletResponse response,Map map) throws Exception{
		int voteID = getInt(request, "voteID");
		if(voteID>0){
			List<VoteThemes> themeList = voteManagerAmuc.initThemeAndOptions(voteID);  
		    if(themeList.size()>0){
		    	map.put("themelist", themeList);
		    }else{
		    	map.put("themelist", null);     
		    }
		    map.put("success", "1");
		    map.put("errormsg", "");
		}else{
			map.put("themelist", null);
			map.put("success", "0");
			map.put("errormsg", "投票参数获取失败！");
		}
		return new ModelAndView("amuc/vote/VoteOptions", map);
	}
	
	/**
	 * 修改编号时，异步查询该条数据
	 * @param request
	 * @param response
	 * @param map
	 * @throws Exception
	 */
	@RequestMapping("/getoption.do")
	@SuppressWarnings("unchecked")
	private void getOptionById(HttpServletRequest request, HttpServletResponse response,Map map) throws Exception{
		int opId = getInt(request, "opid");
		JSONObject jsonObject = new JSONObject();
		VoteOptions voteOptions = new VoteOptions();
		if(opId>0){
			voteOptions= voteManagerAmuc.getOptionById(opId);
			map.put("errormsg", "");
			map.put("opinfo", voteOptions);
			jsonObject.put("ret", "1");
			jsonObject.put("retinfo", map);
		}else{
			map.put("errormsg", "投票选项参数获取失败！");
			map.put("opinfo", null);
			jsonObject.put("ret", "0");
			jsonObject.put("retinfo", map);
		}
		output(jsonObject.toString(), response);
	}
	
	/**
	 * 获取选项和图片信息
	 * @param request
	 * @param response
	 * @param map
	 * @throws Exception
	 */
	@RequestMapping("/getOptionAndImgInfo.do")
	@SuppressWarnings("unchecked")
	private void getOptionAndImgInfo(HttpServletRequest request, HttpServletResponse response,Map map) throws Exception{
		int opId = getInt(request, "opid", 0);
		JSONObject jsonObject = new JSONObject();
		VoteOptions voteOptions = new VoteOptions();
		if(opId>0){
			voteOptions= voteManagerAmuc.getOptionAndImgInfoById(opId);
			map.put("errormsg", "");
			map.put("opinfo", voteOptions);
			jsonObject.put("ret", "1");
			jsonObject.put("retinfo", map);
		}else{
			map.put("errormsg", "投票选项参数获取失败！");
			map.put("opinfo", null);
			jsonObject.put("ret", "0");
			jsonObject.put("retinfo", map);
		}
		output(jsonObject.toString(), response);
	}
	
	/**
	 * 上传一张图片到磁盘，并将数据信息存入数据库
	 * @param request
	 * @param response
	 * @param map
	 * @throws Exception
	 */
	@RequestMapping("/addOptionImg.do")
	@SuppressWarnings("unchecked")
	private void addOptionImage(HttpServletRequest request, HttpServletResponse response,Map map) throws Exception{
		
		request.setCharacterEncoding("UTF-8");
		// request中封装数据，此处应注意，items在E5中为一静态变量，会被覆盖，导致items只能读取一次
		List<FileItem> items = FormHelper.getFileItem(request);
		VoteImageInfo voteImageInfo = voteManagerAmuc.getImageInfoByFile(items);
		String imageName = "";
		JSONObject jsonObject = new JSONObject();
		String retFlag = "0";
		
		// 首先从request中读取图片所需信息
		if(voteImageInfo!=null && voteImageInfo.getViSize()>0){
            imageName = voteManagerAmuc.voteImageSetName(voteImageInfo);
			// 获取图片路径
            StorageDeviceReader deviceReader = (StorageDeviceReader)com.founder.e5.context.Context.getBean(StorageDeviceReader.class);
            StorageDevice device = deviceReader.getByName("头像存储");
			String realHttpPath = device.getHttpDeviceURL()+"/upload/vote/" + voteManagerAmuc.voteFolderSetName(voteImageInfo.getViVoteId())+"/"+imageName;
			String realPath = "upload/vote/" + voteManagerAmuc.voteFolderSetName(voteImageInfo.getViVoteId())+"/"+imageName;
			voteImageInfo.setViAddress(realHttpPath);
			//保存图片数据到数据库
			if(voteImageInfo.getViClassification()==2){//页眉图片
				if(voteImageInfo.getViWidth()!=960){//图片保存到磁盘上失败
					retFlag="0";
					map.put("errormsg", "页眉图片宽度应为960px");
					map.put("imageinfo", null);
					jsonObject.put("ret", retFlag);
					jsonObject.put("retinfo", map);
					output(jsonObject.toString(), response);
					return ;
				}
				Document doc = voteManagerAmuc.saveOptionImgInfo(voteImageInfo, imageName);
				int docID = (Integer)doc.get("viVoteID");
				int vsHeadersImgID = (int)doc.getDocID();
				voteManagerAmuc.setHeadersImgID(vsHeadersImgID,docID);
				log.info("---页眉信息写进数据库成功---");
			}else{// 投票选项图片
				Document doc = voteManagerAmuc.saveOptionImgInfo(voteImageInfo, imageName);
				voteImageInfo.setVoteImageId(doc.getInt("SYS_DOCUMENTID"));
				if(doc.getDocID()>0){
					retFlag = "1"; //保存成功
				}
				log.info("---选项图片信息写进数据库成功---");
			}	
			
			// 存图片到磁盘
			boolean saveImageSuccess = voteManagerAmuc.upload(items, realPath);
			//抽图服务
			voteManagerAmuc.prepare4Extract(device, realPath);
			//VoteManagerAmuc.prepare4Extract(device, realPath);

			//增加trans
			String webroot_pic = device.getHttpDeviceURL()+"~upload~vote~" + voteManagerAmuc.voteFolderSetName(voteImageInfo.getViVoteId());
			String nisPic = webroot_pic.substring(webroot_pic.lastIndexOf("/")+1);
			//String imageFormat = imageName.substring(imageName.lastIndexOf("."));
			PublishHelper.writePath(nisPic + "~" + imageName,PublishHelper.getTransPath());
			PublishHelper.writePath(nisPic + "~" + imageName + ".0.jpg", PublishHelper.getTransPath());
			PublishHelper.writePath(nisPic + "~" + imageName + ".2.jpg", PublishHelper.getTransPath());

			if(!saveImageSuccess){//图片保存到磁盘上失败
				retFlag="0";
				map.put("errormsg", "图片写入失败！");
				map.put("imageinfo", null);
				jsonObject.put("ret", retFlag);
				jsonObject.put("retinfo", map);
			}else{
			    map.put("errormsg", "");
			    map.put("imageinfo", voteImageInfo);
			    jsonObject.put("ret", retFlag);
			    jsonObject.put("retinfo", map);
			}
		}else{
			map.put("errormsg", "读取图片信息失败！");
			map.put("imageinfo", null);
			jsonObject.put("ret", "0");
			jsonObject.put("retinfo", map);
		}
		output(jsonObject.toString(), response);
	}
	
	/**
	 * 修改时获取图片路径
	 * @param request
	 * @param response
	 * @param map
	 * @throws Exception
	 */
	@RequestMapping("/getPathByVoteID.do")
	private void getPathByVoteID(HttpServletRequest request, HttpServletResponse response,Map map)throws Exception{
		JSONObject jsonObject = new JSONObject();
		int voteID = getInt(request, "voteID", 0);
		int viClassification = getInt(request, "viClassification", 3);//值为3则视为没有获取到值
		if(voteID!=0&&viClassification<3){
			List<VoteImageInfo> pathList = voteManagerAmuc.getPathByVoteID(voteID, viClassification);
			jsonObject.put("pathList", pathList);
			output(jsonObject.toString(), response);
		}else{
			jsonObject.put("pathList", "");
		}
		
	}
	
	/**
	 * 删除图片
	 * @param request
	 * @param response
	 * @param map
	 * @throws Exception
	 */
	@RequestMapping("/delImg.do")
	@SuppressWarnings("unchecked")
	private void delImg(HttpServletRequest request, HttpServletResponse response,Map map)throws Exception{
		int voteID = getInt(request, "voteID", 0);
		int imgID = getInt(request, "imgID", 0);
		int viClassification = getInt(request, "viClassification", 0);
		boolean delSuccess = true;
		JSONObject jsonObject = new JSONObject();
		if(imgID!=0){
			if(viClassification==2){
				delSuccess = voteManagerAmuc.delImgToDisk(imgID);
				voteManagerAmuc.changeVoteSetByHeadImgID(imgID, voteID);
				voteManagerAmuc.delImgToData(imgID);
			}else{
				delSuccess = voteManagerAmuc.delImgToDisk(imgID);
				voteManagerAmuc.delImgToData(imgID);
				if(delSuccess){
					map.put("errormsg", "");
				    map.put("imageinfo", null);
				    jsonObject.put("ret", "1");
				    jsonObject.put("retinfo", map);
				}else{
					map.put("errormsg", "删除图片失败！");
				    map.put("imageinfo", null);
				    jsonObject.put("ret", "0");
				    jsonObject.put("retinfo", map);
				}
				output(jsonObject.toString(), response);
			}
		}
	}
	
	/**
	 * 修改选项所属分类
	 * @param request
	 * @param response
	 * @param map
	 * @throws Exception
	 */
	@RequestMapping("/updateoptionclass.do")
	@SuppressWarnings("unchecked")
	private void updateVoteCla(HttpServletRequest request, HttpServletResponse response,Map map) throws Exception{
		String className = get(request, "newclass", "");
		int opId = getInt(request, "opid");
		JSONObject jsonObject = new JSONObject();
		VoteOptions voteOptions = new VoteOptions();
		if(opId>0){
			voteOptions= voteManagerAmuc.updateOptionClass(opId, className);
			map.put("errormsg", "");
			map.put("opinfo", voteOptions);
			jsonObject.put("ret", "1");
			jsonObject.put("retinfo", map);
		}else{
			map.put("errormsg", "投票选项参数获取失败！");
			map.put("opinfo", null);
			jsonObject.put("ret", "0");
			jsonObject.put("retinfo", map);
		}
		output(jsonObject.toString(), response);
	}
	
	/**
	 * 修改一条主题
	 * @param request
	 * @param response
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping("/eidtOneTheme.do")
	@SuppressWarnings("unchecked")
	private void eidtOneTheme(HttpServletRequest request, HttpServletResponse response,Map model) throws Exception{
		
		String themeContent = get(request, "votethemetext", "");
		int themeIndex = getInt(request, "themeindex", 0);
		int voteId = getInt(request, "voteid", 0);
		int themeId = getInt(request, "themeid", 0);
		int chooseType = getInt(request,"choosetype",0);
		String chooseNumberStr = get(request, "choosenumbers", "");
		String chooseNumberStr_min = get(request, "minchoosenumbers", "");
		int chooseNumber = -1;  //默认为-1，代表最多选择数是空的
		int chooseNumber_min = -1;
		if(chooseType == 1){ // 多选
			chooseNumber = "".equals(chooseNumberStr)? -1 :Integer.parseInt(chooseNumberStr);
			chooseNumber_min = "".equals(chooseNumberStr_min)? -1 :Integer.parseInt(chooseNumberStr_min);
		}
		JSONObject jsonObject = new JSONObject();
		VoteThemes voteThemes = new VoteThemes();
			if(themeId>0 && voteId>0){
				themeIndex = themeIndex==0?voteManagerAmuc.getThemeIndexByVoteId(voteId)-1:themeIndex;
				voteThemes = voteManagerAmuc.updateTheme(themeId, themeContent, themeIndex,chooseNumber,chooseNumber_min);
				model.put("errormsg", "");
				model.put("themeinfo", voteThemes);
				jsonObject.put("ret", "1");
				jsonObject.put("retinfo", model);
			}else{
				model.put("errormsg", "投票参数获取失败！");
				model.put("themeinfo", null);
				jsonObject.put("ret", "0");
				jsonObject.put("retinfo", model);
			}
		output(jsonObject.toString(), response);
	}
	
	/**
	 * 添加一个投票主题
	 * @param request
	 * @param response
	 * @param model
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/addOneTheme.do")
	private void addOneTheme(HttpServletRequest request, HttpServletResponse response,Map model) throws Exception{
		
		String themeContent = get(request, "votethemetext", "");
		int themeIndex = getInt(request, "themeindex", 0);
		int voteId = getInt(request, "voteid", 0);
		int chooseType = getInt(request,"choosetype",0);
		String chooseNumberStr = get(request, "choosenumbers", "");
		String chooseNumberStr_min = get(request, "minchoosenumbers", "");
		int chooseNumber = -1;
		int chooseNumber_min = -1;
		if(chooseType == 1){ // 多选
			chooseNumber = "".equals(chooseNumberStr)? -1 :Integer.parseInt(chooseNumberStr);
			chooseNumber_min = "".equals(chooseNumberStr_min)? -1 :Integer.parseInt(chooseNumberStr_min);
		}
		JSONObject jsonObject = new JSONObject();
		VoteThemes voteThemes = new VoteThemes();
		    if(voteId>0){
				voteThemes.setCreatedTime(DateUtils.getTimestamp().toString());
				voteThemes.setLastModifiedTime(DateUtils.getTimestamp().toString());
				voteThemes.setVoteId(voteId);
				voteThemes.setThemeName(themeContent);
				voteThemes.setThemeIndex(themeIndex==0?0:themeIndex);
				voteThemes.setMostChooseNums(chooseNumber);
				voteThemes.setMinChooseNums(chooseNumber_min);
				voteThemes.setOptionNums(0);
				Document doc = voteManagerAmuc.saveVoteTheme(voteThemes);
				VoteThemes retVoteThemes = new VoteThemes();
				if(doc.getInt("SYS_DOCUMENTID")>0){
					retVoteThemes.setThemeId(doc.getInt("SYS_DOCUMENTID"));
					retVoteThemes.setThemeIndex(doc.getInt("vtIndex"));
					retVoteThemes.setThemeName(doc.getString("vtName"));
					retVoteThemes.setVoteId(doc.getInt("vtVoteID"));
					retVoteThemes.setOptionNums(doc.getInt("vtOptionNum"));
					retVoteThemes.setMostChooseNums(doc.getInt("vtMostChooseNum"));
					retVoteThemes.setMinChooseNums(doc.getInt("vtMinChooseNum"));
				}
				model.put("errormsg", "");
				model.put("themeinfo", retVoteThemes);
				jsonObject.put("ret", "1");
				jsonObject.put("retinfo", model);
			}else{
				model.put("errormsg", "投票活动参数获取失败！");
				model.put("themeinfo", null);
				jsonObject.put("ret", "0");
				jsonObject.put("retinfo", model);
			}

		output(jsonObject.toString(), response);
	}
	
	/**
	 * 删除投票主题
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("/deleteVoteTheme.do")
	@SuppressWarnings("unchecked")
	private void deleteVoteTheme(HttpServletRequest request, HttpServletResponse response,Map model) throws Exception{
		int themeId = getInt(request, "themeid");
		JSONObject jsonObject = new JSONObject();
		if(themeId>0 && voteManagerAmuc.deleteVoteTheme(themeId)){
			model.put("errormsg", "");
			jsonObject.put("ret", "1");
			jsonObject.put("retinfo", model);
		}else{
			model.put("errormsg", "删除主题失败！");
			jsonObject.put("ret", "0");
			jsonObject.put("retinfo", model);
		}
		output(jsonObject.toString(), response);
	}
	
	/**
	 * 编辑选项查看页内容
	 * @param request
	 * @param response
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping("/editOptionPageText.do")
	@SuppressWarnings("unchecked")
	private void editOptionPageText(HttpServletRequest request, HttpServletResponse response,Map model) throws Exception{
		
		int opId = getInt(request, "opid", 0);
		int showImgFlag = getInt(request, "showimgonpage", 1);
		String pageContent = get(request, "pagetextcontent", "");
		JSONObject jsonObject = new JSONObject();
		if(opId>0){
			boolean updateSuccess = voteManagerAmuc.updateViewPageTextById(opId, pageContent, showImgFlag);
			if(updateSuccess){
				model.put("errormsg", "");
				jsonObject.put("ret", "1");
				jsonObject.put("retinfo", model);
			}else{
				model.put("errormsg", "编辑查看页内容失败");
				jsonObject.put("ret", "0");
				jsonObject.put("retinfo", model);
			}
			
		}else{
			model.put("errormsg", "选项参数获取失败");
			jsonObject.put("ret", "0");
			jsonObject.put("retinfo", model);
		}
		output(jsonObject.toString(), response);
	}
	
	
}
