package com.founder.amuc.api.vote;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.net.URLDecoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import net.sf.json.JSONObject;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Jedis;

import com.founder.amuc.api.vote.entity.VoteSettings;
import com.founder.amuc.commons.Constant;
import com.founder.amuc.commons.InfoHelper;
import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.doc.util.E5docHelper;
import com.founder.e5.dom.DocLib;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.redis.RedisKey;
import com.founder.amuc.commons.RedisManager;
import com.founder.amuc.commons.JedisClient;

@Controller
@RequestMapping("/api/vote/recordVote")
public class VoteWriteAdapter {

	

	@Autowired
	VoteInfoManager voteInfoManager;
	@Autowired
	JedisClient jedisClient;
	/**
	 * 统计投票的访问量
	 * 
	 * @param voteID
	 * @throws Exception
	 */
	@RequestMapping("/recordAccessCount.do")
	  public void recordAccessCount(HttpServletRequest request,
				HttpServletResponse response,  Map model) throws Exception{
		response.setHeader("Access-Control-Allow-Origin","*");
		int voteid = Integer.parseInt(request.getParameter("voteid"));
		String time = request.getParameter("time");
	    String sign = request.getParameter("sign");
	    
		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("voteid", Integer.toString(voteid));
		params.put("time", time);
		String paramResult = checkRequest(sign, params);
		if (paramResult != null) {
			JSONObject json = new JSONObject();
			json.put("code", "1000");
			json.put("result", paramResult);
			System.out.println("统计投票的访问量，校验结果：" + String.valueOf(json));
			outputJson(String.valueOf(json), response);
			System.out.println("内网接口obj：-->" + String.valueOf(json));
			return;
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		long accesscount = jedisClient.incr(RedisKey.VOTE_ACCESSCOUNT + voteid);
		jedisClient.set(RedisKey.VOTE_ACCESSCOUNT + voteid,String.valueOf(accesscount));
		if(jedisClient.get(RedisKey.VOTE_ACCESSCOUNT + voteid)!=null){
			map.put("errcode",1 );
			map.put("errmsg", "ok");
			JSONObject jsonObj = JSONObject.fromObject(map);
			outputJson(String.valueOf(jsonObj), response);
		}else {
			map.put("errcode",0 );
			map.put("errmsg", "false");
			JSONObject jsonObj = JSONObject.fromObject(map);
			outputJson(String.valueOf(jsonObj), response);
		}
		
	}

	/**
	 *  每此提交一个选项，记录投票
	 */
	@SuppressWarnings({ "rawtypes" })
	@RequestMapping("/buttonRecordVoteCount.do")
	  public void buttonRecordVoteCount(HttpServletRequest request,
				HttpServletResponse response,  Map model) throws Exception{
		response.setHeader("Access-Control-Allow-Origin", "*");
		int voteid = Integer.parseInt(request.getParameter("voteid"));
		String vote_optionid = request.getParameter("vote_optionid");
		int userid = Integer.parseInt(request.getParameter("userid"));
		int vote_type = Integer.parseInt(request.getParameter("vote_type"));
		int vote_mode = Integer.parseInt(request.getParameter("vote_mode"));
		int vt_most_choose_num = Integer.parseInt(request.getParameter("vt_most_choose_num"));
		String time = request.getParameter("time");
	    String sign = request.getParameter("sign");
	    
		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("voteid", Integer.toString(voteid));
		params.put("vote_optionid", vote_optionid);
		params.put("userid", Integer.toString(userid));
		params.put("vote_type", Integer.toString(vote_type));
		params.put("vote_mode",Integer.toString(vote_mode));
		params.put("vt_most_choose_num", Integer.toString(vt_most_choose_num));
		params.put("time", time);
		String paramResult = checkRequest(sign, params);
		if (paramResult != null) {
			JSONObject json = new JSONObject();
			json.put("code", "1000");
			json.put("result", paramResult);
			System.out.println("记录投票button，校验结果：" + String.valueOf(json));
			outputJson(String.valueOf(json), response);
			System.out.println("内网接口obj：-->" + String.valueOf(json));
			return;
		}
		
		String crttime = new SimpleDateFormat("yyyy-MM-dd").format(new Date()); // 当前时间
		System.out.println("--- buttonRecordVoteCount参数：voteid=" + voteid+ "&vote_optionid=" + vote_optionid + "&userid=" + userid+ "&vote_type=" + vote_type + "&vote_mode=" + vote_mode);
		System.out.println("--- 当前时间：time="+ new SimpleDateFormat("yyyy-MM-dd") + "&crttime=" + crttime);
		Map<String, Object> map = new HashMap<String, Object>();
		String ipAddr = "";  //用户IP
		String voted_options =""; //已经投过的选项列表
		
		//得到数据库中的vote信息
		VoteSettings vs=voteInfoManager.getVoteSettingsByVoteID(voteid);
		
		//判断是否符合活动时间
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");     
		Date dnow=new Date();
		Date dbegin=sdf.parse(vs.getVs_startime());
		long flag=dnow.getTime()-dbegin.getTime();
		if(flag<0){
			map.put("errcode",0 );
			map.put("errmsg", "投票还没开始！");			
			JSONObject jsonObj = JSONObject.fromObject(map);
			outputJson(String.valueOf(jsonObj), response);
			return;
		}
		Date dend=sdf.parse(vs.getVs_endtime());
		flag=dend.getTime()-dnow.getTime();
		if(flag<0){
			map.put("errcode",0 );
			map.put("errmsg", "投票已结束！");			
			JSONObject jsonObj = JSONObject.fromObject(map);
			outputJson(String.valueOf(jsonObj), response);
			return;
		}
		//判断第一个主题能投的最多选项数和传入参数是否相等,不相等直接赋值
		int numFromDB=vs.getVs_votetheme_list().get(0).getVt_most_choose_num();
		if(vt_most_choose_num!=numFromDB)
			vt_most_choose_num=numFromDB;
		
		//判断不允许投票的其他形式
		if (vote_type == 0) { // 实名投票
			System.out.println("---实名投票：uid=" + userid+ "&crttime="+ crttime);
			if(vote_mode == 1){  //周期模式，判断当天是否投够票
				voted_options = jedisClient.hget(RedisKey.VOTE_PERSON_OPTIONID+voteid+"." + userid,String.valueOf(crttime)); // 取出当前用户投过的选项
				if(voted_options!=null && voted_options.split(",").length >=vt_most_choose_num){   //字符串数组中如果包含当前时间的字符串，则值不等于-1
					map.put("errcode",0 );
					map.put("errmsg", "您今天已经投够"+vt_most_choose_num+"票了哦");			
					JSONObject jsonObj = JSONObject.fromObject(map);
					outputJson(String.valueOf(jsonObj), response);
					return;
				}
				if(voted_options!=null && vs.getVs_vote_repeat_one()==0 &&voted_options.indexOf(vote_optionid)!=-1){
					map.put("errcode",0 );
					map.put("errmsg", "您今天已经投过该选项了哦");			
					JSONObject jsonObj = JSONObject.fromObject(map);
					outputJson(String.valueOf(jsonObj), response);
					return;
				}
			}else{//一般模式，判断之前是否投够票
				voted_options = jedisClient.hget(RedisKey.VOTE_PERSON_OPTIONID+voteid+"." + userid,String.valueOf(userid)); // 取出当前用户投过的选项	
				if(voted_options!=null &&voted_options.split(",").length >=vt_most_choose_num){   //字符串数组中如果包含当前时间的字符串，则值不等于-1
					map.put("errcode",0 );
					map.put("errmsg", "您已经投够"+vt_most_choose_num+"票了哦");			
					JSONObject jsonObj = JSONObject.fromObject(map);
					outputJson(String.valueOf(jsonObj), response);
					return;
				}
				if(voted_options!=null && vs.getVs_vote_repeat_one()==0 && voted_options.indexOf(vote_optionid)!=-1){
					map.put("errcode",0 );
					map.put("errmsg", "您已经投过该选项了哦");			
					JSONObject jsonObj = JSONObject.fromObject(map);
					outputJson(String.valueOf(jsonObj), response);
					return;
				}
			}
			// 可以投票
			// 获取用户文档，根据用户ID获取用户名称，并将其存进该投票的reids缓存中
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			DocLib mb_DocLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER);
			String mb_cdtion = "SYS_DELETEFLAG=0 and SYS_DOCUMENTID=?";
			String[] mb_column = { "mName" };
			Document[] mb_docs_0 = docManager.find(mb_DocLib.getDocLibID(),mb_cdtion, new Object[] { userid }, mb_column);
			if (mb_docs_0.length == 0) {
				map.put("errcode", 0);
				map.put("errmsg", "非系统注册用户，不允许投票");
				JSONObject jsonObj = JSONObject.fromObject(map);
				outputJson(String.valueOf(jsonObj), response);
				return;
			}
			if (StringUtils.isBlank(voted_options)) { // 第一次投票
				voted_options = vote_optionid;
				jedisClient.hset(RedisKey.VOTE_USERID+voteid, String.valueOf(userid), crttime);
				long personCount = jedisClient.incr(RedisKey.VOTE_PERSONCOUNT + voteid);
				jedisClient.set(RedisKey.VOTE_PERSONCOUNT + voteid,String.valueOf(personCount));
			} else {// 非第一次投票
				String vote_time=jedisClient.hget(RedisKey.VOTE_USERID+voteid, String.valueOf(userid));
				if(vote_time.indexOf(crttime)==-1){
					jedisClient.hset(RedisKey.VOTE_PERSONCOUNT+voteid, String.valueOf(userid), vote_time+ "," + crttime);
				}
				voted_options = voted_options + "," + vote_optionid;
			}
			if(vote_mode == 1){
				jedisClient.hset(RedisKey.VOTE_PERSON_OPTIONID+voteid+"." + userid, crttime,voted_options); // 周期存储已经投过的选项
			}else{
				jedisClient.hset(RedisKey.VOTE_PERSON_OPTIONID+voteid+"." + userid, String.valueOf(userid),voted_options); // 非周期存储已经投过的选项
			}
		}else{  //匿名投票
			if(vote_type==1){
				ipAddr = request.getParameter("ip");
			}else{
				ipAddr = request.getParameter("cookie");
			}
			//String cookieVal_time = jedisCluster.hget("amuc.vote.cookie."+voteid,cookieCode);  //cookie存储的值,时间字符串
			//System.out.println("--- 匿名投票：cookieCode="+cookieCode+"&cookieVal_time="+cookieVal_time);
			System.out.println("--- 匿名投票：ipAddr="+ipAddr+"&crttime="+crttime);
				if(vote_mode == 1){  //周期模式，判断当天是否投够票
					voted_options = jedisClient.hget(RedisKey.VOTE_IP_OPTIONID+voteid+"." + ipAddr,String.valueOf(crttime)); // 取出当前用户投过的选项
					if(voted_options!=null && voted_options.split(",").length >=vt_most_choose_num){   //字符串数组中如果包含当前时间的字符串，则值不等于-1
						map.put("errcode",0 );
						map.put("errmsg", "您今天已经投够"+vt_most_choose_num+"票了哦");			
						JSONObject jsonObj = JSONObject.fromObject(map);
						outputJson(String.valueOf(jsonObj), response);
						return;
					}
					if(voted_options!=null && vs.getVs_vote_repeat_one()==0 &&voted_options.indexOf(vote_optionid)!=-1){
						map.put("errcode",0 );
						map.put("errmsg", "您今天已经投过该选项了哦");			
						JSONObject jsonObj = JSONObject.fromObject(map);
						outputJson(String.valueOf(jsonObj), response);
						return;
					}
				}else{//一般模式，判断之前是否投够票
					voted_options = jedisClient.hget(RedisKey.VOTE_IP_OPTIONID+voteid+"." + ipAddr,this.getIpAddr(request)); // 取出当前用户投过的选项	
					if(voted_options!=null &&voted_options.split(",").length >=vt_most_choose_num){   //字符串数组中如果包含当前时间的字符串，则值不等于-1
						map.put("errcode",0 );
						map.put("errmsg", "您已经投够"+vt_most_choose_num+"票了哦");			
						JSONObject jsonObj = JSONObject.fromObject(map);
						outputJson(String.valueOf(jsonObj), response);
						return;
					}
					if(voted_options!=null && vs.getVs_vote_repeat_one()==0 && voted_options.indexOf(vote_optionid)!=-1){
						map.put("errcode",0 );
						map.put("errmsg", "您已经投过该选项了哦");			
						JSONObject jsonObj = JSONObject.fromObject(map);
						outputJson(String.valueOf(jsonObj), response);
						return;
					}
				}
			//可以投票
			if (StringUtils.isBlank(voted_options)) { // 第一次投票
				voted_options = vote_optionid;
				long personCount = jedisClient.incr(RedisKey.VOTE_PERSONCOUNT + voteid);
				jedisClient.set(RedisKey.VOTE_PERSONCOUNT + voteid,String.valueOf(personCount));
			} else {// 非第一次投票
				voted_options = voted_options + "," + vote_optionid;
			}
			if(vote_mode == 1){
				jedisClient.hset(RedisKey.VOTE_IP_OPTIONID+voteid+"." + ipAddr, crttime,voted_options); // 周期存储已经投过的选项
			}else{
				jedisClient.hset(RedisKey.VOTE_IP_OPTIONID+voteid+"." + ipAddr, this.getIpAddr(request),voted_options); // 非周期存储已经投过的选项
			}
		}
		
		
		// 记录投票数
		long voteCount = jedisClient.incr(RedisKey.VOTE_VOTECOUNT + voteid);
		long optionCount = jedisClient.incr(RedisKey.VOTE_OPTIONCOUNT + voteid+ "." + vote_optionid);
		// 从redis中获取该投票的用户的ID
		String userIDs = jedisClient.hmget(RedisKey.VOTE_OPTION_PERSONINFO + voteid + "." + vote_optionid,"userid").get(0);
		StringBuffer userIDsArr = new StringBuffer();
		if (StringUtils.isBlank(userIDs)) { // 如果之前的选项没有会员ID，则直接加上当前用户就行
			userIDsArr.append(userid);
		} else { // 否则，先加上之前的会员ID，在加上该用户的ID
			userIDsArr.append(userIDs).append(",").append(userid);
		}
		Map<String, String> vmap = new HashMap<String, String>();
		// 获取用户文档，根据用户ID获取用户名称，并将其存进该投票的reids缓存中
		if (vote_type == 0) { // 实名投票才需要记录投票人
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			DocLib mb_DocLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER);
			String mb_cdtion = "SYS_DELETEFLAG=0 and SYS_DOCUMENTID=?";
			String[] mb_column = { "mName" };
			Document[] mb_docs = docManager.find(mb_DocLib.getDocLibID(),
					mb_cdtion, new Object[] { userid }, mb_column);
			String mName = mb_docs[0].getString("mName");
			if (StringUtils.isBlank(mName)) { // 当用户名为null时，默认赋值为空字符串
				mName = "";
			}
			String username = jedisClient.hmget(RedisKey.VOTE_OPTION_PERSONINFO + voteid + "." + vote_optionid,"username_uid").get(0);
			StringBuffer usernameArr = new StringBuffer();
			if (StringUtils.isBlank(username)) {
				usernameArr.append(mName).append("(").append(userid).append(")");
			} else {
				usernameArr.append(username).append(",").append(mName).append("(").append(userid).append(")");
			}
			vmap.put("username_uid", usernameArr.toString());
		}
		vmap.put("vote" + voteid, String.valueOf(voteid));
		vmap.put("voteOption" + vote_optionid, String.valueOf(vote_optionid));
		vmap.put(RedisKey.VOTE_VOTECOUNT + voteid, String.valueOf(voteCount));
		vmap.put(RedisKey.VOTE_OPTIONCOUNT + voteid + "." + vote_optionid,String.valueOf(optionCount));
		vmap.put("userid", userIDsArr.toString());
		jedisClient.hmset(RedisKey.VOTE_OPTION_PERSONINFO + voteid + "."+ vote_optionid, vmap);
		jedisClient.set(RedisKey.VOTE_VOTECOUNT+ voteid, String.valueOf(voteCount));

		// 记录日志
		Map<String, String> logMap = new HashMap<String, String>();
		long logID = jedisClient.incr("logID");
		logMap.put("logID", String.valueOf(logID));
		logMap.put("userID" + userid, String.valueOf(userid));
		logMap.put("voteID" + voteid, String.valueOf(voteid));
		logMap.put("voteTime", String.valueOf(DateUtils.getTimestamp()));
		logMap.put("result", vote_optionid);

		jedisClient.hmset(RedisKey.VOTE_LOG + logID, logMap);
		if (jedisClient.hmget(RedisKey.VOTE_LOG + logID, "result").size() > 0) {
			map.put("errcode", 1);
			map.put("errmsg", "提交成功");
			JSONObject jsonObj = JSONObject.fromObject(map);
			outputJson(String.valueOf(jsonObj), response);
			return;
		}
		/* } */
		map.put("errcode", 0);
		map.put("errmsg", "提交失败");
		JSONObject jsonObj = JSONObject.fromObject(map);
		outputJson(String.valueOf(jsonObj), response);
		return;
	}
	
	
	
	/**
	 * 统计投票数 记录日志
	 * 
	 * @param voteID
	 * @param voteOptionID
	 * @param userID
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes"})
	@RequestMapping("/recordVoteCountAndLog.do")
	public void recordVoteCountAndLog(HttpServletRequest request,
			HttpServletResponse response,  Map model) throws Exception{
		
		response.setHeader("Access-Control-Allow-Origin","*");
		int voteid = Integer.parseInt(request.getParameter("voteid"));
		String vote_optionid = request.getParameter("vote_optionid");
		int userid = Integer.parseInt(request.getParameter("userid"));
		int vote_type = Integer.parseInt(request.getParameter("vote_type"));
		int vote_mode = Integer.parseInt(request.getParameter("vote_mode"));
		String info = URLDecoder.decode(request.getParameter("info"),"utf-8");
		String time = request.getParameter("time");
	    String sign = request.getParameter("sign");
	    
		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("voteid", Integer.toString(voteid));
		params.put("vote_optionid", vote_optionid);
		params.put("userid", Integer.toString(userid));
		params.put("vote_type", Integer.toString(vote_type));
		params.put("vote_mode",Integer.toString(vote_mode));
		params.put("info", info);
		params.put("time", time);
		String paramResult = checkRequest(sign, params);
		if (paramResult != null) {
			JSONObject json = new JSONObject();
			json.put("code", "1000");
			json.put("result", paramResult);
			System.out.println("统计投票的访问量，校验结果：" + String.valueOf(json));
			outputJson(String.valueOf(json), response);
			System.out.println("内网接口obj：-->" + String.valueOf(json));
			return;
		}
		
		String crttime = new SimpleDateFormat("yyyy-MM-dd").format(new Date());  //当前时间
		System.out.println("--- recordVoteCountAndLog参数：voteid="+voteid+"&vote_optionid="+vote_optionid+"&userid="+userid+"&info="+info+"&vote_type="+vote_type+"&vote_mode="+vote_mode);
		System.out.println("--- 当前时间：time="+new SimpleDateFormat("yyyy-MM-dd")+"&crttime="+crttime);
		Map<String, Object> map = new HashMap<String, Object>();
		String ipAddr = "";  //用户IP
		if(vote_type==0){  //实名投票
			String vote_time = jedisClient.hget(RedisKey.VOTE_USERID+voteid, String.valueOf(userid));  //取出当前用户投过票的
			System.out.println("---实名投票：uid="+userid+"&vote_time="+vote_time);
			if(!StringUtils.isBlank(vote_time)){  //之前有投过票
				if(vote_mode == 1){  //周期模式，判断当天是否投过票
					if(vote_time.indexOf(crttime)!=-1){   //字符串数组中如果包含当前时间的字符串，则值不等于-1
						map.put("errcode",0 );
						map.put("errmsg", "您今天已经投过票了哦");			
						JSONObject jsonObj = JSONObject.fromObject(map);
						outputJson(String.valueOf(jsonObj), response);
						return;
					}
				}else{  //一般模式直接抛出已经投过票的提示信息
					map.put("errcode",0 );
					map.put("errmsg", "您已经参加过此次投票活动，请不要重复提交投票");			
					JSONObject jsonObj = JSONObject.fromObject(map);
					outputJson(String.valueOf(jsonObj), response);
					return;
				}
			}
			//可以投票
			//获取用户文档，根据用户ID获取用户名称，并将其存进该投票的reids缓存中
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			DocLib mb_DocLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER);
			String mb_cdtion = "SYS_DELETEFLAG=0 and SYS_DOCUMENTID=?";
			String[] mb_column = {"mName"};
			Document[] mb_docs_0 = docManager.find(mb_DocLib.getDocLibID(), mb_cdtion, new Object[]{userid},mb_column);
			if(mb_docs_0.length == 0){
				map.put("errcode",0 );
				map.put("errmsg", "非系统注册用户，不允许投票");
				JSONObject jsonObj = JSONObject.fromObject(map);
				outputJson(String.valueOf(jsonObj), response);
				return;
			}
			if(StringUtils.isBlank(vote_time)){  //第一次投票
				vote_time = crttime;
			}else{
				vote_time = vote_time + "," + crttime;
			}
			jedisClient.hset(RedisKey.VOTE_USERID+voteid, String.valueOf(userid), vote_time);   //将之前默认值为“true”修改为存储投票日期
			long personCount = jedisClient.incr(RedisKey.VOTE_PERSONCOUNT+voteid);
			jedisClient.set(RedisKey.VOTE_PERSONCOUNT+voteid, String.valueOf(personCount));
		}else{  //匿名投票
			// 处理逻辑：先判断cookie中是否有数据值，再判断ip是否重复
			// 获取cookie中标识值，是一个32位的UUID
			//String cookieCode = this.getVoteCookie(request);
			// redis缓存中也有32位的UUID，理论上与cookie值相等
			// 如果cookie中值不为空字符串，并且与redis中UUID相等，则判断为已投票
			//String uuidCodeStr = "";
			//uuidCodeStr = this.getUserUUID();
			//保存cookie
			//this.setVoteCookie(response,request,uuidCodeStr,voteid);
			if(vote_type==1){
				ipAddr = request.getParameter("ip");
			}else{
				ipAddr = request.getParameter("cookie");
			}
			
			String ipVal_time = jedisClient.hget(RedisKey.VOTE_IP+voteid, ipAddr);  //IP地址存储的值,时间字符串
			System.out.println("--- 匿名投票：ipAddr="+ipAddr+"&ipVal_time="+ipVal_time);
			if(!StringUtils.isBlank(ipVal_time)){  //之前有投过票
				if(vote_mode == 1){  //周期模式
					if(ipVal_time.indexOf(crttime)!=-1){
						map.put("errcode",0 );
						map.put("errmsg", "您今天已经投过票了哦");
						JSONObject jsonObj = JSONObject.fromObject(map);
						outputJson(String.valueOf(jsonObj), response);
						return;
					}
				}else{  //一般模式
					map.put("errcode",0 );
					map.put("errmsg", "您已经参加过此次投票活动，请不要重复提交投票");
					JSONObject jsonObj = JSONObject.fromObject(map);
					outputJson(String.valueOf(jsonObj), response);
					return;
				}
			}
			//可以投票
			//获取UUID
			//String uuidCodeStr = "";
			//uuidCodeStr = this.getUserUUID();
			//保存cookie
			//this.setVoteCookie(request,uuidCodeStr,voteid);
			//UUID存进redis缓存
			/*
			if(StringUtils.isBlank(cookieVal_time)){
				cookieVal_time = crttime;
			}else{
				cookieVal_time = cookieVal_time + "," + crttime;
			}
			jedisCluster.hset("amuc.vote.cookie."+voteid, uuidCodeStr, cookieVal_time);  //将值存储为投票时间
			*/
			//ip存进redis
			if(StringUtils.isBlank(ipVal_time)){
				ipVal_time = crttime;
			}else{
				ipVal_time = ipVal_time + "," + crttime;
			}
			jedisClient.hset(RedisKey.VOTE_IP+voteid, ipAddr, ipVal_time);  //将值存储为投票时间
			long personCount = jedisClient.incr(RedisKey.VOTE_PERSONCOUNT+voteid);
			jedisClient.set(RedisKey.VOTE_PERSONCOUNT+voteid, String.valueOf(personCount));
		}
		//记录投票数
		String str[] = vote_optionid.split(",");
		for (int i = 0; i < str.length; i++) {
		 	long voteCount = jedisClient.incr(RedisKey.VOTE_VOTECOUNT+voteid);
			long optionCount = jedisClient.incr(RedisKey.VOTE_OPTIONCOUNT+voteid+"."+str[i]);
			//从redis中获取该投票的用户的ID
			String userIDs = jedisClient.hmget(RedisKey.VOTE_OPTION_PERSONINFO+voteid+"."+str[i],"userid").get(0);
			StringBuffer userIDsArr = new StringBuffer();
			if(StringUtils.isBlank(userIDs)){  //如果之前的选项没有会员ID，则直接加上当前用户就行
				userIDsArr.append(userid);
			}else{   //否则，先加上之前的会员ID，在加上该用户的ID
				userIDsArr.append(userIDs).append(",").append(userid);
			}
			Map<String, String> vmap = new HashMap<String, String>();
			//获取用户文档，根据用户ID获取用户名称，并将其存进该投票的reids缓存中
			if(vote_type==0){  //实名投票才需要记录投票人
				DocumentManager docManager = DocumentManagerFactory.getInstance();
				DocLib mb_DocLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER);
				String mb_cdtion = "SYS_DELETEFLAG=0 and SYS_DOCUMENTID=?";
				String[] mb_column = {"mName"};
				Document[] mb_docs = docManager.find(mb_DocLib.getDocLibID(), mb_cdtion, new Object[]{userid},mb_column);
				String mName = mb_docs[0].getString("mName");
				if(StringUtils.isBlank(mName)){  //当用户名为null时，默认赋值为空字符串
					mName = "";
				}
				String username = jedisClient.hmget(RedisKey.VOTE_OPTION_PERSONINFO+voteid+"."+str[i],"username_uid").get(0);
				StringBuffer usernameArr = new StringBuffer();
				if(StringUtils.isBlank(username)){
					usernameArr.append(mName).append("(").append(userid).append(")");
				}else{
					usernameArr.append(username).append(",").append(mName).append("(").append(userid).append(")");
				}
				vmap.put("username_uid", usernameArr.toString());
			}
			vmap.put("vote" + voteid, String.valueOf(voteid));
			vmap.put("voteOption" + str[i], String.valueOf(str[i]));
			vmap.put(RedisKey.VOTE_VOTECOUNT+voteid, String.valueOf(voteCount));
			vmap.put(RedisKey.VOTE_OPTIONCOUNT+voteid+"."+str[i], String.valueOf(optionCount));
			vmap.put("userid", userIDsArr.toString());
			jedisClient.hmset(RedisKey.VOTE_OPTION_PERSONINFO + voteid +"."+ str[i], vmap);
			jedisClient.set(RedisKey.VOTE_VOTECOUNT+voteid,String.valueOf(voteCount));
			
			//将投票总数同步到数据库
			DocumentManager vsdocManager = DocumentManagerFactory.getInstance();
			DocLib vs_DocLib = InfoHelper.getLib(Constant.DOCTYPE_VOTE);
			Document[] vs_docs = vsdocManager.find(vs_DocLib.getDocLibID(), "SYS_DELETEFLAG=0 and SYS_DOCUMENTID=?", new Object[]{voteid});
			vs_docs[0].set("vsTotalVotes",Integer.parseInt(String.valueOf(voteCount)));
			vsdocManager.save(vs_docs[0]);
			//将单个选项投票数同步到数据库
			DocumentManager vodocManager = DocumentManagerFactory.getInstance();
			DocLib vo_DocLib = InfoHelper.getLib(Constant.DOCTYPE_VOTEOPTION);
			Document[] vo_docs = vodocManager.find(vo_DocLib.getDocLibID(), "SYS_DELETEFLAG=0 and SYS_DOCUMENTID=?", new Object[]{str[i]});
			vo_docs[0].set("voVotes",Integer.parseInt(vo_docs[0].getString("voVotes"))+1);
			vodocManager.save(vo_docs[0]);
		}
		
		//记录用户定制信息
		/*List<String> rsmap = jedisCluster.hmget("votecount:vote:" + voteid +":option:"+ str[0],"vote" + voteid, "voteOption" + str[0]);
		if (rsmap.size() > 0) {
			Map<String, String> userMap = new HashMap<String, String>();
			if(info!=null&&!info.equals("")){
				JSONObject jsonObj = JSONObject.fromObject(info);
				Iterator it = jsonObj.keys();
				while (it.hasNext()) {  
		            String key = (String) it.next();  
		            String value = jsonObj.getString(key);  
		            userMap.put(key, value);
		        }  
				jedisCluster.hmset("userinfo:userid:"+userid+":vote:"+voteid, userMap);
			}else {
				userMap.put("", "");
				jedisCluster.hmset("userinfo:userid:"+userid+":vote:"+voteid, userMap);
			}
		}*/
		//记录收集用户信息
		if(!StringUtils.isBlank(info)){  //收集用户信息不为空，则表示有收集信息
			String uinfo = "";
			if(vote_type==0){  //实名投票，按照用户ID来存储用户收集信息
				String uid = userid+"";  //将用户ID由int类型转换为string类型
				uinfo = jedisClient.hget(RedisKey.VOTE_USERINFO+voteid, uid);  //根据会员id取出当前会员存储过的用户的收集信息
				if(!StringUtils.isBlank(uinfo)){   //用户收集信息不为空
					String[] uinfoArr = uinfo.split("&,&");   //将该用户的收集信息分成数组，对比是否重复填写，重复填写的数据不需要存储下来
					boolean isSave = true;
					for (String uinfoStr : uinfoArr) {  //
						if(uinfoStr.equals(info)){  //如果有一个与传进来的相等，则不需要进行存储
							isSave = false;
							break;
						}
					}
					if(isSave){  //isSave为true，表示需要存储，为false表示不需要存储
						uinfo = uinfo+"&,&"+info;
					}
				}else{
					uinfo = info;
				}
				jedisClient.hset(RedisKey.VOTE_USERINFO+voteid, uid, uinfo);  //存储用户收集信息
			}else{  //匿名投票，按照IP地址来存储用户收集信息
				uinfo = jedisClient.hget(RedisKey.VOTE_USERINFO+voteid, ipAddr);  //取出当前IP地址存储过的用户的收集信息
				System.out.println("------------111-----------uinfo="+uinfo);

				if(!StringUtils.isBlank(uinfo)){   //用户收集信息不为空
					String[] uinfoArr = uinfo.split("&,&");   //将该用户的收集信息分成数组，对比是否重复填写，重复填写的数据不需要存储下来
					boolean isSave = true;
					for (String uinfoStr : uinfoArr) {  //
						if(uinfoStr.equals(info)){  //如果有一个与传进来的相等，则不需要进行存储
							isSave = false;
							break;
						}
					}
					if(isSave){  //isSave为true，表示需要存储，为false表示不需要存储
						uinfo = uinfo+"&,&"+info;  //通过“&,&”符号来进行区分，为避免重复
					}
				}else{
					uinfo = info;
				}
				jedisClient.hset(RedisKey.VOTE_USERINFO+voteid, ipAddr, uinfo);  //存储用户收集信息
				System.out.println("+++++++++++voteid="+voteid+"+++++++++++ipAddr="+ipAddr+"+++++++++++uinfo="+uinfo);
			}
		}
		
		//记录日志
		/*if(jedisCluster.hmget("userinfo:userid:"+userid+":vote:"+voteid, "").size()>0){*/
			Map<String, String> logMap = new HashMap<String, String>();
			long logID = jedisClient.incr("logID");
			logMap.put("logID", String.valueOf(logID));
			logMap.put("userID" + userid, String.valueOf(userid));
			logMap.put("voteID" + voteid, String.valueOf(voteid));
			logMap.put("voteTime", String.valueOf(DateUtils.getTimestamp()));
			logMap.put("result", vote_optionid);
			logMap.put("info", info);

			jedisClient.hmset(RedisKey.VOTE_LOG+logID, logMap);
			if(jedisClient.hmget(RedisKey.VOTE_LOG+logID, "result").size()>0){
				map.put("errcode",1 );
				map.put("errmsg", "提交成功");
				JSONObject jsonObj = JSONObject.fromObject(map);
				outputJson(String.valueOf(jsonObj), response);
				return;
			}
		/*}*/
		map.put("errcode",0 );
		map.put("errmsg", "提交失败");
		JSONObject jsonObj = JSONObject.fromObject(map);
		outputJson(String.valueOf(jsonObj), response);
	}
	
	@RequestMapping("/getUser.do")
	public void getUser(HttpServletRequest request,
			HttpServletResponse response,  Map model) throws Exception{
		response.setHeader("Access-Control-Allow-Origin","*");
		
			int userid = Integer.parseInt(request.getParameter("userid"));
			int docTypeID = DocTypes.MEMBER.typeID();
			DocLib docLib = LibHelper.getLib(docTypeID, "xy");
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			Document[] docs = docManager.find(docLib.getDocLibID()," SYS_DOCUMENTID="+userid+" and SYS_DELETEFLAG=0 ",new Object[]{});
			if(docs != null && docs.length > 0){
				JSONObject jsonObj = new JSONObject();
				//实名投票，该投票者的移动电话如果为空，则不允许进行投票
				String mMobile = docs[0].getString("mMobile");
				if(StringUtils.isBlank(mMobile)){
					jsonObj.put("isThird", "true");
				}else{
					jsonObj.put("isThird", "false");
				}
				jsonObj.put("会员名称", docs[0].getString("mName"));
				jsonObj.put("移动电话1", docs[0].getString("mMobile"));
				jsonObj.put("电子邮箱", docs[0].getString("mEmail"));
				jsonObj.put("QQ号", docs[0].getString("mQq"));
				jsonObj.put("证件类型", docs[0].getString("mCardType"));
				jsonObj.put("证件号码", docs[0].getString("mCardNo"));
				jsonObj.put("性别", docs[0].getInt("mSex"));
				jsonObj.put("生日", docs[0].getString("mBirthday"));
				jsonObj.put("家庭地址", docs[0].getString("mAddress"));
				jsonObj.put("交子数量", docs[0].getInt("mScore"));
				jsonObj.put("新浪微博号id", docs[0].getString("mWeiboUid"));
				jsonObj.put("新浪微博名称", docs[0].getString("mWeiboName"));
				jsonObj.put("微信号id", docs[0].getString("mWechatId"));
				jsonObj.put("微信号名称", docs[0].getString("mWechatName"));
				jsonObj.put("QQ昵称", docs[0].getString("mQQname"));
				outputJson(String.valueOf(jsonObj), response);
			}		
			outputJson(String.valueOf(""), response);;
		
	}
	
	/**
     * 获取投票者IP
     * 
     * 在一般情况下使用Request.getRemoteAddr()即可，但是经过nginx等反向代理软件后，这个方法会失效。
     * 
     * 本方法先从Header中获取X-Real-IP，如果不存在再从X-Forwarded-For获得第一个IP(用,分割)
	 * 经过多次代理可能有多个ip串，取第一个即可
     * 如果还不存在则调用Request .getRemoteAddr()。
     * 
     * @param request
     * @return
     */
    private String getIpAddr(HttpServletRequest request) throws Exception{
        String ip = request.getHeader("X-Real-IP");
        if (!StringUtils.isBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        ip = request.getHeader("X-Forwarded-For");
        if (!StringUtils.isBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个IP值，第一个为真实IP。
            int index = ip.indexOf(',');
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        } else{
		    ip = request.getHeader("Proxy-Client-IP");
		}
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("HTTP_CLIENT_IP");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");  
        }
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
           ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
           ip = request.getRemoteAddr();
        }
	    return ip;
    }
    
    /**
     * 
     * 创建一个用户客户端的cookie，与ip共同判断用户数据，如果cookie没存过，就创建一个cookie
     * @param request
     * @throws Exception
     */
    private void setVoteCookie(HttpServletResponse response, HttpServletRequest request,String uuidCode,int voteId) throws Exception{
    	
    	Cookie existVoteCookie = new Cookie("existvotecookie",uuidCode);
    	// 获取截止时间与当前时间之间的秒数
    	SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	String endTime = RedisManager.hget(RedisKey.VOTE_VOTEINFO+".end", RedisKey.VOTE_VOTEINFO+voteId);
    	Date endDate = format.parse(endTime);
    	long endDateSecond = endDate.getTime()/1000;
    	long currentDate=new Date().getTime()/1000; 
    	//获取cookie的存活时间，投票活动的截止时间与当前时间的相差秒数
    	existVoteCookie.setMaxAge((int) (endDateSecond-currentDate));
    	existVoteCookie.setPath(request.getContextPath()+"/");
    	response.addCookie(existVoteCookie);
    	
    }
    
    /**
     * 获取cookie，返回一个Boolean值，存在则返回true
     * @param request
     * @return
     * @throws Exception
     */
    private String getVoteCookie(HttpServletRequest request) throws Exception{
    	String existValue = "";
    	// 从request中获取cookie
    	Cookie[] cookies = request.getCookies();
    	if(cookies!=null && cookies.length>0){
    		// 遍历cookies
    		for(int i=0;i<cookies.length;i++){
    			Cookie curCookie = cookies[i];
    			if(curCookie.getName().equalsIgnoreCase("existvotecookie")){
    				existValue = curCookie.getValue();
    			}	
    		}
    	}
    	return existValue;
    }
    
    /**
     * 生成一个UUID,作为cookie的值
     * @return
     * @throws Exception
     */
    private String getUserUUID() throws Exception{
    	
    	UUID uuid = UUID.randomUUID();   
        String uuidStr = uuid.toString();
        uuidStr = uuidStr.replaceAll("-", "");
        return uuidStr;
    }
    public static void outputJson(String result, HttpServletResponse response) {
		if (result == null) return;
		
		response.setContentType("application/json; charset=UTF-8");

		PrintWriter out = null;
		try {
			out = response.getWriter();
			out.write(result);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(out);
		}
	}
    private String checkRequest(String sign,TreeMap<String,String> param){
		String result=null;
		long serverTime=new Date().getTime();
		long clientTime=Long.parseLong(param.get("time"));
		//时间差，毫秒为单位,要求小于60s
		long timeDiff=(serverTime-clientTime);
		if(timeDiff>60*1000){
			result="非法请求!";
			return result;
		}
		String currentSign=getCurrentSign(param.toString());
		if(!sign.equals(currentSign)){
			result="非法请求！";
			return result;
		}
		return result;
	}
    private static String getCurrentSign(String paramsList){
		String currentSign = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(paramsList.toString().getBytes("UTF-8"));
            currentSign = toHex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return currentSign;
	}
    private static String toHex(byte buffer[]) {
        StringBuffer sb = new StringBuffer(buffer.length * 2);
        for (int i = 0; i < buffer.length; i++) {
            sb.append(Character.forDigit((buffer[i] & 240) >> 4, 16));
            sb.append(Character.forDigit(buffer[i] & 15, 16));
        }
        return sb.toString();
    }
}
