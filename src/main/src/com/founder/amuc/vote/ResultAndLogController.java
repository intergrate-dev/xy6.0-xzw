package com.founder.amuc.vote;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFHeader;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.Jedis;

import com.founder.amuc.commons.AttachHelper;
import com.founder.amuc.commons.Constant;
import com.founder.amuc.commons.DateHelper;
import com.founder.amuc.commons.InfoHelper;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.doc.util.E5docHelper;
import com.founder.e5.dom.DocLib;
import com.founder.e5.web.BaseController;
import com.founder.xy.redis.RedisKey;
import com.founder.amuc.commons.RedisManager;
import com.founder.amuc.commons.JedisClient;

@Controller
@RequestMapping("/amuc/resultAndLog")
public class ResultAndLogController extends BaseController {
	
	@Autowired
	private VoteManagerAmuc voteManager;
	@Autowired
	JedisClient jedisClient;

	public VoteManagerAmuc getVoteManager() {
		return voteManager;
	}

	public void setVoteManager(VoteManagerAmuc voteManager) {
		this.voteManager = voteManager;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected void handle(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception {
		String action = get(request, "action");
		if("look".equals(action)){
			queryResultAndLog(request,response,model);
		}else if("export".equals(action)){  //导出投票选项信息
			export(request,response,model);
		}else if("exportUserMsg".equals(action)){  //导出收集用户信息
			exportUserMsg(request,response,model);
		}else if("exportEveOptUser".equals(action)){  //导出选项的投票详情
			exportEveOptUser(request,response,model);
		}else if("exportUserByTime".equals(action)){  //根据时间来导出这段时间参与投票的用户
			exportUserByTime(request,response,model);
		}
	}

	/**
	 * 为查询结果进行冒泡排序
	 * @param list
	 * @param sortType
	 */
	private void bubbleSort(List<VoteOptions> list, String sortType){
		boolean flag = "asc".equalsIgnoreCase(sortType) ? true : false;
		for (int out = list.size() - 1; out > 0; out--) {
			for (int in = 0; in < out; in++) {
				VoteOptions obj = list.get(in);
				int inObj = list.get(in).getVoVotes();
				int _inObj = list.get(in + 1).getVoVotes();
	
				if (flag && inObj > _inObj) {
					list.set(in, list.get(in + 1));
					list.set(in + 1, obj);
				}
	
				if (!flag && !(inObj > _inObj)) {
					list.set(in, list.get(in + 1));
					list.set(in + 1, obj);
				}
			}
		}
	}
	
	@RequestMapping("/look.do")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private ModelAndView queryResultAndLog(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception {
		int docID = getInt(request, "DocIDs", 0);
		int DocLibID = getInt(request, "DocLibID", 0);
		String sorttype = get(request,"sorttype");
		List<VoteOptions> opList = voteManager.getOptionsByVoteId(docID);
		model.put("docID", docID);
		model.put("DocLibID", DocLibID);
		model.put("sorttype", sorttype);
		
		//获取投票设置的文档id
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib vs_DocLib = InfoHelper.getLib(Constant.DOCTYPE_VOTE);
		String vs_cdtion = "SYS_DELETEFLAG=0 and SYS_DOCUMENTID=?";
		String[] vs_column = {"SYS_DOCUMENTID","vsVoteType","vsVoteMode"};
		Document[] vs_docs = docManager.find(vs_DocLib.getDocLibID(), vs_cdtion, new Object[]{docID},vs_column);
		if(vs_docs.length != 0){
			int vsVoteType = vs_docs[0].getInt("vsVoteType");  //投票方式，0：实名，1：匿名
			int vsVoteMode = vs_docs[0].getInt("vsVoteMode");  //投票模式，0：一般模式，1：周期模式
			model.put("vsVoteType", vsVoteType);
			model.put("vsVoteMode", vsVoteMode);
		}
		//获取会员表中的所有字段
		/*MemberReader memberReader = new MemberReader();
		List<Pair> result = memberReader.getFields();
		model.put("memberFields", JSONArray.fromObject(result).toString());*/
		
		String accesscount = jedisClient.get(RedisKey.VOTE_ACCESSCOUNT + docID);
		String votecount = jedisClient.get(RedisKey.VOTE_VOTECOUNT+docID);   //投票总数量
		String peoplecount = jedisClient.get(RedisKey.VOTE_PERSONCOUNT+docID);  //该投票参与人数
		if(opList!=null){
			int opList_len = opList.size();
			for(int i=0;i<opList_len;i++){
				if(jedisClient.hmget(RedisKey.VOTE_OPTION_PERSONINFO + docID +"."+ opList.get(i).getVoteOpId(),RedisKey.VOTE_OPTIONCOUNT+docID+"."+opList.get(i).getVoteOpId()).get(0)!=null){
					opList.get(i).setVoVotes(Integer.parseInt(jedisClient.hmget(RedisKey.VOTE_OPTION_PERSONINFO + docID +"."+ opList.get(i).getVoteOpId(),RedisKey.VOTE_OPTIONCOUNT+docID+"."+opList.get(i).getVoteOpId()).get(0)));
				}
				
			}
			if(!StringUtils.isBlank(sorttype)){  //当sorttype是desc为降序，asc为升序
				bubbleSort(opList, sorttype);
			}
			model.put("opList", opList);
		}
		model.put("accesscount", accesscount);  //浏览量
		model.put("votecount", votecount);  //投票总数量
		model.put("peoplecount", peoplecount);  //参与投票的总人数
		model.put("@VIEWNAME@", "amuc/vote/ResultAndLog");
		return new ModelAndView("amuc/vote/ResultAndLog", model);
	}
	
	@RequestMapping("/export.do")
	@SuppressWarnings("rawtypes")
	private void export(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception {
			int docID = getInt(request, "docID", 0);
			DBSession conn = null;
			IResultSet rs = null;
			DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_VOTE, "uc");
			VoteSettings voteSettings = new VoteSettings();
			try {
				conn = E5docHelper.getDBSession(docLib.getDocLibID());
				String sql = "select * from ucVoteSettings where SYS_DOCUMENTID=? and SYS_DELETEFLAG=0"; 
				Object[] params = new Object[]{docID};
				rs = conn.executeQuery(sql, params);
				
				while(rs.next()){
					voteSettings.setVsTitle(rs.getString("vsTitle"));
					voteSettings.setVsTypes(rs.getInt("vsTypes"));
					voteSettings.setVsOptionType(rs.getInt("vsOptionType"));
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
					voteSettings.setVsActivityIntro(rs.getString("vsActivityIntro"));
					voteSettings.setVsUserInfoRule(rs.getString("vsUserInfoRule"));
				}
				// 创建一个新的Excel
				HSSFWorkbook workBook = new HSSFWorkbook();
				// 创建sheet页
				HSSFSheet sheet = workBook.createSheet();
				// sheet页名称
				workBook.setSheetName(0, "result");
				// 创建header页
				HSSFHeader header = sheet.getHeader();
				// 设置标题居中
				header.setCenter("标题");

				// 设置第一行为Header
				HSSFRow row = sheet.createRow(0);
				HSSFCell cell0 = row.createCell(0);
				HSSFCell cell1 = row.createCell(1);
				HSSFCell cell2 = row.createCell(2);
				HSSFCell cell3 = row.createCell(3);

				sheet.setColumnWidth(0, 4000);
				sheet.setColumnWidth(1, 4000);
				sheet.setColumnWidth(2, 4000);
				sheet.setColumnWidth(3, 4000);
				sheet.setColumnWidth(4, 4000);
				sheet.setColumnWidth(5, 4000);
				sheet.setColumnWidth(6, 4000);

				HSSFFont font = workBook.createFont();
				font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);

				HSSFCellStyle cellStyle = workBook.createCellStyle();
				cellStyle.setFont(font);

				cell0.setCellValue(voteSettings.getVsTitle());
				cell0.setCellStyle(cellStyle);
				cell1.setCellValue("序号");
				cell1.setCellStyle(cellStyle);
				cell2.setCellValue("选项");
				cell2.setCellStyle(cellStyle);
				cell3.setCellValue("投票数");
				cell3.setCellStyle(cellStyle);

				List<VoteOptions> opList = voteManager.getOptionsByVoteId(docID);
				for (int i = 0; i < opList.size(); i++) {
					row = sheet.createRow(i+1);
					cell0 = row.createCell(0);
					cell1 = row.createCell(1);
					cell2 = row.createCell(2);
					cell3 = row.createCell(3);
					cell1.setCellValue(i+1);
					cell2.setCellValue(opList.get(i).getVoName());
					String voteNum = jedisClient.hmget(RedisKey.VOTE_OPTION_PERSONINFO + docID +"."+ opList.get(i).getVoteOpId(),RedisKey.VOTE_OPTIONCOUNT+docID+"."+opList.get(i).getVoteOpId()).get(0);
					if(StringUtils.isBlank(voteNum)){  //如果投票数为空，则默认赋值为0
						cell3.setCellValue(0);
					}else{
						cell3.setCellValue(voteNum);
					}
				}
						
					
				response.reset();
				response.setContentType("application/msexcel;charset=UTF-8");
				response.addHeader(
						"Content-Disposition",
						"attachment;filename=\""
								+ new String(("投票结果" + ".xls")
										.getBytes("GBK"), "ISO8859_1")
								+ "\"");
				OutputStream out = response.getOutputStream();
				if (out != null) {
					workBook.write(out);
					out.flush();
					out.close();
				}
			} catch (Exception e) {
				ResourceMgr.rollbackQuietly(conn);
				e.printStackTrace();
			} finally {
				ResourceMgr.closeQuietly(conn);
			} 
	}
	
	/**
	 * 收集用户信息填写的详情，并导出为excel表单
	 * @param request
	 * @param response
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping("/exportUserMsg.do")
	@SuppressWarnings("rawtypes")
	private void exportUserMsg(HttpServletRequest request,HttpServletResponse response, Map model) throws Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		String docID=request.getParameter("docID");  //投票ID
		String DocLibID = request.getParameter("DocLibID");
		Document doc = docManager.get(Integer.valueOf(DocLibID), Long.valueOf(docID));
		String vsUserInfoRule = doc.getString("vsUserInfoRule");
		if(vsUserInfoRule.equals("{\"\":\"\"}") || StringUtils.isBlank(vsUserInfoRule)){   //表示不收集用户信息
			output("该投票没有收集用户信息，不能导出",response);
			return;
		}
		//第一步，获取该投票需要收集的用户信息，作为excel表头
		JSONObject jsonObj = JSONObject.fromObject(vsUserInfoRule);
		Iterator it = jsonObj.keys();
		ArrayList<String> userMsg_arr1 =new ArrayList<String>();   //收集用户信息，作为excel的标题
		int vsVoteType = doc.getInt("vsVoteType");
		String uid = "";
		String voteOpt = "投票选项";
		Map<String, String> userIDs = new HashMap<String, String>();

		if(vsVoteType == 0){  //0：实名投票，1：匿名投票
			uid = "会员ID";   //excel表表头信息,userMsg_arr1 为表头内容
			userIDs = jedisClient.hgetAll(RedisKey.VOTE_USERINFO+docID);   //返回值为key:value，其中key代表用户ID
		}else{
			uid = "用户IP";
			userIDs = jedisClient.hgetAll(RedisKey.VOTE_USERINFO+docID);   //返回值为key:value，其中key代表用户IP地址
		}
		userMsg_arr1.add(uid);
		while (it.hasNext()){
			 String key = (String)it.next();
			 userMsg_arr1.add(key);
		}

        userMsg_arr1.add(voteOpt);
		//第二步，获取有哪些人参与过该投票，并且参与该投票所选的选项为哪些记录下来
		//Map<String, String> userIDs = jedisCluster.hgetAll("amuc.vote.userid."+docID);   //返回值为key:value，其中key代表用户ID
		List<ArrayList<String>> userList = new ArrayList<ArrayList<String>>();
		ArrayList<String> user = null;
		for(String userID : userIDs.keySet()){  //得到该投票下的所有参与用户ID,user 为每一个用户的投票信息，userList 为全部的投票信息
			//String userMsgVal = jedisCluster.hmget("userinfo:userid:"+userID+":vote:"+docID,userMsg).get(0);
			//取出当前会员存储过的用户的收集信息;形式类似于{"会员名称":"qq2","移动电话1":"13211223344"}，{"会员名称":"qq1","移动电话1":"13111223344"}
			user = new ArrayList<String>();
			String userMsgVal = jedisClient.hget(RedisKey.VOTE_USERINFO+docID, userID);
			if(StringUtils.isBlank(userMsgVal)){
				userMsgVal = "";
				user.add(userMsgVal);
			}else{
				String[] uinfoArr = userMsgVal.split("&,&");   //将该用户的收集信息分成数组
				for (String uinfo : uinfoArr) {
					if("1".equals(uinfo)){
						continue;
					}
					for (String userMsg : userMsg_arr1) {// {"姓名":"张婕","手机":"18925714187"}
						if(userMsg.equals(uid)){   //表头第一列
							user.add(userID);
                        } else if (userMsg.equals(voteOpt)) {
							String optStr = "";
							Long maxLogId = Long.valueOf(jedisClient.get("logID"));
                            for (int i=1; i<=maxLogId; i++) {
								if (jedisClient.exists(RedisKey.VOTE_LOG + i)) {
									List<String> list = jedisClient.hmget(RedisKey.VOTE_LOG + i, "voteID" + docID);
									if ( list.size() >0 && list.get(0) != null && list.get(0).equals(docID)
											&& jedisClient.hmget(RedisKey.VOTE_LOG + i, "info").get(0).equals(userMsgVal)) {
										List<String> infos = jedisClient.hmget(RedisKey.VOTE_LOG + i, "result");
										if (infos != null && infos.size() > 0) {
											optStr = infos.get(0);
										}
										break;
									}
								}
                            }
							user.add(optStr);
						} else {
                            JSONObject uinfo_json = JSONObject.fromObject(uinfo);
                            String uinfoVal = "";
                            if (uinfo_json.containsKey(userMsg)) {  //json对象有这个key，没有key时，就赋值为空
                                uinfoVal = uinfo_json.getString(userMsg);
                            } else {
                                uinfoVal = "";
                            }
                            user.add(uinfoVal);
                        }
                    }
				}
			}
			userList.add(user);
		}
		AttachHelper.setExcelMsg(response,userMsg_arr1,userList,"收集用户信息表");
	}
	
	/**
	 * 将每一个选项的详情信息导出为excel表
	 * @param request
	 * @param response
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping("/exportEveOptUser.do")
	@SuppressWarnings("rawtypes")
	private void exportEveOptUser(HttpServletRequest request,HttpServletResponse response, Map model) throws Exception {
		String docID=request.getParameter("docID");  //投票ID
		String sorttype = get(request,"sorttype");  //获取排序类型，desc为降序，asc为升序
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		//获取投票选项文档
		DocLib vo_DocLib = InfoHelper.getLib(Constant.DOCTYPE_VOTEOPTION);
		String vo_cdtion = "voVoteID=? order by SYS_DOCUMENTID DESC";
		String[] vo_column = {"SYS_DOCUMENTID","voName","SYS_DELETEFLAG"};
		Document[] vo_docs = docManager.find(vo_DocLib.getDocLibID(), vo_cdtion, new Object[]{docID},vo_column);
		//第一步，设定好excel的表头["选项ID","选项名称","票数","投票对象"]
		ArrayList<String> voTitle =new ArrayList<String>();
		voTitle.add("选项ID");
		voTitle.add("选项名称");
		voTitle.add("票数");
		voTitle.add("状态");
		/*voTitle.add("投票对象ID");*/
		/*voTitle.add("投票对象名称(对象ID值)");*/
		//第二步，根据表头来写每行的具体内容 [[150, 13717874086], [23, ], [54, 666 ]]，每行根据选项ID来区分
		List<ArrayList<String>> voContent = new ArrayList<ArrayList<String>>();
		for (int i = 0; i < vo_docs.length; i++) {
			ArrayList<String> eveVoContent =new ArrayList<String>();
			Document vo_doc = vo_docs[i];
			String vo_id = vo_doc.getString("SYS_DOCUMENTID");   //选项id
			String vo_name = vo_doc.getString("voName");  // 选项名称
			String vo_vote_num = jedisClient.hmget(RedisKey.VOTE_OPTION_PERSONINFO+docID+"."+vo_id,RedisKey.VOTE_OPTIONCOUNT+docID+"."+vo_id).get(0);  //每一个选项获得的票数
			if(StringUtils.isBlank(vo_vote_num)){   //如果该选项的得票数为空，则将其默认赋值为0
				vo_vote_num = "0";
			}
			Integer flag = vo_doc.getInt("SYS_DELETEFLAG");  // 选项名称
			//String userIDs = jedisCluster.hmget("votecount:vote:"+docID+":option:"+vo_id,"userid").get(0);  //每一个选项具体的投票对象的ID
			/*String username = jedisCluster.hmget("votecount:vote:"+docID+":option:"+vo_id,"username_uid").get(0);  //每一个选项具体的投票对象的名称
			if(StringUtils.isBlank(username)){
				username = "";
			}*/
			String status = flag == 0 ? "启用" : "禁用";
			eveVoContent.add(vo_id);
			eveVoContent.add(vo_name);
			eveVoContent.add(vo_vote_num);
			eveVoContent.add(status);
			/*eveVoContent.add(userIDs);*/
			/*if(!StringUtils.isBlank(username)){
				String[] usernameArr = username.split(",");
				StringBuffer unameArr = new StringBuffer();
				for(int j=0;j<usernameArr.length;j++){
					eveVoContent.add(usernameArr[j]);
				}
			}else{
				eveVoContent.add(username);
			}*/
			voContent.add(eveVoContent);
		}
		if(!StringUtils.isBlank(sorttype)){
			bubbleSort2(voContent,sorttype);
		}
		AttachHelper.setExcelMsg(response,voTitle,voContent,"投票详情");
	}
	
	@RequestMapping("/exportUserByTime.do")
	private void exportUserByTime(HttpServletRequest request,HttpServletResponse response, Map model) throws Exception{
		String sdf = "yyyy-MM-dd";
		String dtp_start = get(request,"dtp_start");  //查询开始时间
		String dtp_end = get(request,"dtp_end");  //查询截止时间
		String docID = get(request,"docID");  //投票的文档id
		//String DocLibID = get(request,"DocLibID");  //投票的文档docLibId
		long start_time = DateHelper.StrToDate(dtp_start, sdf).getTime();  //开始时间的时间戳
		long end_time = DateHelper.StrToDate(dtp_end, sdf).getTime();  //截止时间的时间戳
		//获取会员文档
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib mem_DocLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER);
		String mem_cdtion = "SYS_DELETEFLAG=0 and SYS_DOCUMENTID=?";
		String[] mem_column = {"SYS_DOCUMENTID","mName","mMobile"};
		//excel头部
		ArrayList<String> umsg =new ArrayList<String>();   //收集用户信息，作为excel的标题
		umsg.add("会员ID");
		umsg.add("姓名");
		umsg.add("手机号");
		umsg.add("投票时间");
		//excel内容
		Map<String, String> userIDs = jedisClient.hgetAll(RedisKey.VOTE_USERID+docID);   //返回值为key:value，其中key代表用户ID
		List<ArrayList<String>> ucontent = new ArrayList<ArrayList<String>>();  //作为excel的内容
		for (Map.Entry<String, String> entry : userIDs.entrySet()) {  
			System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
			String uid = entry.getKey();  //用户id
			String vote_time = entry.getValue();  //投票时间字符串，形如：2016-09-01,2016-09-02,2016-09-03
			//将投票时间字符串数组转换为数组，并将里面的值与开始截止日期进行对比，符合要求的则输出用户id
			String[] vote_time_arr = vote_time.split(",");
			//boolean isAddContent = false;  //是否有符合时间要求的数据
			for (String voteTime : vote_time_arr) {
				ArrayList<String> user =new ArrayList<String>();
				long vtime = DateHelper.StrToDate(voteTime, sdf).getTime();
				if((start_time <= vtime) && (vtime <= end_time)){  //符合时间要求
					System.out.println(uid);
					Document[] mem_docs = docManager.find(mem_DocLib.getDocLibID(), mem_cdtion, new Object[]{uid},mem_column);
					if(mem_docs.length > 0){
						user.add(uid);
						String mName = mem_docs[0].getString("mName")==null?"":mem_docs[0].getString("mName");  //会员姓名
						String mMobile = mem_docs[0].getString("mMobile")==null?"":mem_docs[0].getString("mMobile");  //会员手机号
						user.add(mName);
						user.add(mMobile);
						user.add(voteTime);
						//isAddContent = true;  //有符合时间要求的数据
					}
					ucontent.add(user);  //加入到content中
					//break;
				}
			}
			/*if(isAddContent){
				ucontent.add(user);  //加入到content中
			}*/
		}
		System.out.println("导出的excel标题和内容：umsg="+umsg+"&ucontent="+ucontent);
		AttachHelper.setExcelMsg(response,umsg,ucontent,"用户信息表（按投票时间查询）");
	}
	
	/**
	 * 对导出的excel表中内容进行排序--投票详情
	 * @param list
	 * @param sortType
	 */
	private void bubbleSort2(List<ArrayList<String>> list, String sortType){
		boolean flag = "asc".equalsIgnoreCase(sortType) ? true : false;
		for (int out = list.size() - 1; out > 0; out--) {
			for (int in = 0; in < out; in++) {
				ArrayList<String> obj = list.get(in);
				int inObj = Integer.parseInt(list.get(in).get(2));
				int _inObj = Integer.parseInt(list.get(in + 1).get(2));
	
				if (flag && inObj > _inObj) {
					list.set(in, list.get(in + 1));
					list.set(in + 1, obj);
				}
	
				if (!flag && !(inObj > _inObj)) {
					list.set(in, list.get(in + 1));
					list.set(in + 1, obj);
				}
			}
		}
	}
	/**
	 * 将信息导出为excel表
	 * @param response
	 * @param listTitle 表头 ，ArrayList<String>，类似 “[会员ID, 会员名称]”
	 * @param listContent  表内容，List<ArrayList<String>>，类似“[[150, 13717874086], [23, ], [54, 666 ]]”
	 * @param sheetName 导出表的名称
	 * @throws Exception
	 */
	//
	@SuppressWarnings("unused")
	private void setExcelMsg(HttpServletResponse response,ArrayList<String> listTitle,List<ArrayList<String>> listContent,String sheetName) throws Exception{
		// 创建一个新的Excel
		HSSFWorkbook workBook = new HSSFWorkbook();
		// 创建sheet页
		HSSFSheet sheet = workBook.createSheet();
		// sheet页名称
		workBook.setSheetName(0, "result");
		// 创建header页
		HSSFHeader header = sheet.getHeader();
		// 设置标题居中
		/*header.setCenter("标题");*/
		//单元格样式
		HSSFCellStyle cellStyle = workBook.createCellStyle();
		//设置字体
		HSSFFont font = workBook.createFont();
		font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
		cellStyle.setFont(font);
		// 设置第一行为Header
		HSSFRow row = sheet.createRow(0);   //excel表第一行
		int listNum = listTitle.size();
		for(int i=0;i<listNum;i++){
			HSSFCell cell = row.createCell(i);  //创建单元格
			sheet.setColumnWidth(i, 4000);  //设置每列宽度
			cell.setCellValue(listTitle.get(i));   //设置单元格内容
			cell.setCellStyle(cellStyle);  //设置每个单元格样式
		}
		//设置其他行的内容
		int listContentNum = listContent.size();
		for(int m=0;m<listContentNum;m++){
			row = sheet.createRow(m+1);  //创建行
			ArrayList<String> listcont = listContent.get(m);
			int listcontNum = listcont.size();
			for(int n=0;n<listcontNum;n++){
				HSSFCell cell = row.createCell(n);  //创建单元格
				cell.setCellValue(listcont.get(n));  //设置单元格内容
			}
		}
			
		response.reset();
		response.setContentType("application/msexcel;charset=UTF-8");
		response.addHeader(
				"Content-Disposition",
				"attachment;filename=\""
						+ new String((sheetName + ".xls")
								.getBytes("GBK"), "ISO8859_1")
						+ "\"");
		OutputStream out = response.getOutputStream();
		if (out != null) {
			workBook.write(out);
			out.flush();
			out.close();
		}
	}
}
