package com.founder.amuc.api.vote;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Jedis;
import net.sf.json.JSONObject;

import com.founder.amuc.api.vote.entity.VoteOptions;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.commons.ResourceMgr;
import com.founder.xy.redis.RedisKey;
import com.founder.amuc.commons.RedisManager;
import com.founder.amuc.commons.JedisClient;

@Controller
@RequestMapping("/api/vote")
public class VoteInfoAdapter {


	@Autowired
	VoteInfoManager voteInfoManager;
	@Autowired
	JedisClient jedisClient;
	//private static JedisCluster jedisCluster = RedisManager.getJedisCluster();
	/**
	 * @author jz.li20180206
	 * @param voteid 投票ID
	 * 功能：根据投票ID查询关于投票的所有信息，包括单个选项投票数
	 * 访问方式：<webroot>/api/vote/info?voteid=;
	 * @return
	 */
	@RequestMapping("/info.do")
    public void getVoteInfo(HttpServletRequest request,
			HttpServletResponse response,  Map model) throws Exception{
		
		response.setHeader("Access-Control-Allow-Origin","*");
		String voteid = request.getParameter("voteid");
		if(StringUtils.isBlank(voteid)){
			outputJson(String.valueOf(JSONObject.fromObject(new VoteInfo("请检查参数是否为空","error")).toString()), response);
		}else{
			try{
				//voteInfoManager.setRootURL(WebUtil.getRoot(request));
				outputJson(String.valueOf(voteInfoManager.getInfoByRedis(voteid)), response);
			}catch(Exception e){
				e.printStackTrace();
				outputJson(String.valueOf(JSONObject.fromObject(new VoteInfo("获取数据失败","error")).toString()), response);
			}
		}
	}
	
	/**
	 * @author fei.lai
	 * @param voteid 投票ID
	 * 功能：根据投票ID查询关于投票的所有信息
	 * 访问方式：<webroot>/api/vote/info?voteid=;
	 * @return
	 */
	@RequestMapping("/info1.do")
    public void getVoteInfo1(HttpServletRequest request,
			HttpServletResponse response,  Map model) throws Exception{
		
		response.setHeader("Access-Control-Allow-Origin","*");
		String voteid = request.getParameter("voteid");
		if(StringUtils.isBlank(voteid)){
			outputJson(String.valueOf(JSONObject.fromObject(new VoteInfo("请检查参数是否为空","error")).toString()), response);
		}else{
			try{
				//voteInfoManager.setRootURL(WebUtil.getRoot(request));
				outputJson(String.valueOf(voteInfoManager.getInfoByRedis1(voteid)), response);
			}catch(Exception e){
				e.printStackTrace();
				outputJson(String.valueOf(JSONObject.fromObject(new VoteInfo("获取数据失败","error")).toString()), response);
			}
		}
	}
	
	/**
	 * @author fei.lai
	 * @param voteid 投票ID
	 * @param vote_optionid 投票选项ID
	 * 功能：获取浏览总量，各个选项投票数，投票总数
	 * 访问方式：<webroot>/api/vote/votecounts?voteid=&vote_optionid=;
	 * http://127.0.0.1:8080/amuc/api/vote/votecounts?voteid=10&vote_optionid=13,14,15
	 * @return
	 */
	@RequestMapping("/votecounts.do")
	public void getVoteCounts(HttpServletRequest request,
				HttpServletResponse response,  Map model) throws Exception{
		
		response.setHeader("Access-Control-Allow-Origin","*");
		String voteid = request.getParameter("voteid");
		String vote_optionid = request.getParameter("vote_optionid");
		if(StringUtils.isBlank(voteid)||StringUtils.isBlank(vote_optionid)){
			outputJson(String.valueOf(JSONObject.fromObject(new VoteInfo("请检查参数是否为空","error")).toString()), response);
		}else{
			outputJson(String.valueOf(voteInfoManager.getVoteCountsByRedis(voteid, vote_optionid)), response);
		}
		
	}
	
	
	@RequestMapping("/clear.do")
	public void clearVoteInfo(HttpServletRequest request,
			HttpServletResponse response,  Map model) throws Exception{
		jedisClient.clear(RedisKey.VOTE_VOTEINFO);
		jedisClient.clear(RedisKey.VOTE_VOTEINFO+".start");
		jedisClient.clear(RedisKey.VOTE_VOTEINFO+".end");
	}
	
	/**
	 * @author fei.lai
	 * @param voteid 投票ID
	 * 功能：查看活动进行状态
	 * 访问方式：<webroot>/api/vote/time?voteid=;
	 * @return
	 */

	@RequestMapping("/time.do")
	public void getVoteInfoTime(HttpServletRequest request,
			HttpServletResponse response,  Map model) throws Exception{
		
		response.setHeader("Access-Control-Allow-Origin","*");
		String voteid = request.getParameter("voteid");
		if(StringUtils.isBlank(voteid)){
			outputJson(String.valueOf(JSONObject.fromObject(new VoteInfoTime(3,"error","请检查参数是否为空")).toString()), response);
		}else{
			try{
				Integer vid = Integer.parseInt(voteid);
				outputJson(String.valueOf(JSONObject.fromObject(voteInfoManager.getInfoTimeByRedis(vid)).toString()), response);
			}catch(Exception e){
				e.printStackTrace();
				outputJson(String.valueOf(JSONObject.fromObject(new VoteInfoTime(3,"error","请检查参数类型")).toString()), response);
			}
		}
	}
	/**
	 * @author sq
	 * @param vote_optionid 选项ID
	 * 功能：根据投票ID查询关于投票的所有信息
	 * 访问方式：<webroot>/api/vote/optionInfo?vote_optionid=;
	 * @return
	 */
	@RequestMapping("/optionInfo.do")
	public void getOptionInfo(HttpServletRequest request,
			HttpServletResponse response,  Map model) throws Exception{
		
		response.setHeader("Access-Control-Allow-Origin","*");
		String voteid = request.getParameter("voteid");
		String vote_optionid = request.getParameter("vote_optionid");
		if(StringUtils.isBlank(vote_optionid)){
			outputJson(String.valueOf(JSONObject.fromObject(new VoteInfo("请检查参数是否为空","error")).toString()), response);
		}else{
			try{
				VoteOptions vo=voteInfoManager.getVoteOptionByOptionID(Integer.parseInt(vote_optionid));
				String optionCount = jedisClient.hmget(RedisKey.VOTE_OPTION_PERSONINFO + voteid +"."+ vote_optionid, RedisKey.VOTE_OPTIONCOUNT+voteid+"."+vote_optionid).get(0);
				Integer opCount=Integer.parseInt(optionCount==null?"0":optionCount);
				vo.setVo_votes(opCount);//此处long转int，在投票的有效范围内，可以视为正确的，不为溢出（int21亿）
				outputJson(String.valueOf(JSONObject.fromObject(vo).toString()), response);
			}catch(Exception e){
				e.printStackTrace();
				outputJson(String.valueOf(JSONObject.fromObject(new VoteInfo("获取数据失败","error")).toString()), response);
			}
		}
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
}
