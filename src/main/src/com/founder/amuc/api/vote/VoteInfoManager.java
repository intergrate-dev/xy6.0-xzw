package com.founder.amuc.api.vote;

import java.text.SimpleDateFormat;
import java.util.*;

import com.founder.amuc.commons.Constant;
import com.founder.amuc.commons.InfoHelper;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.xy.commons.LibHelper;
import org.apache.axis.utils.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Jedis;
import net.sf.json.JSONObject;

import com.founder.amuc.api.vote.entity.VoteCount;
import com.founder.amuc.api.vote.entity.VoteImage;
import com.founder.amuc.api.vote.entity.VoteOptionCount;
import com.founder.amuc.api.vote.entity.VoteOptions;
import com.founder.amuc.api.vote.entity.VoteSettings;
import com.founder.amuc.api.vote.entity.VoteThemes;
import com.founder.amuc.commons.RedisManager;
import com.founder.amuc.commons.JedisClient;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.sys.SysConfigReader;
import com.founder.xy.redis.RedisKey;


@Component
public class VoteInfoManager {

	//private static JedisCluster jedisCluster = RedisManager.getJedisCluster();
	private static final String dataName = "amuc.vote.voteInfo";  //缓存进redis数据库中的表名
	@Autowired
	JedisClient jedisClient;
	/**
	 * @author fei.lai
	 * @param id 投票选项ID
	 * @return 选票图片实体
	 */
	public VoteImage getOptionsVoteImageByID(Integer id){
		DBSession conn = null;
		IResultSet rs = null;
		try {
			conn = Context.getDBSession();
			String selectSql = "select * from xy_membervoteimage where "
					+ "SYS_DELETEFLAG=0 and viOptionsID=? and viClassification=1";
			Object[] selParams = new Object[]{id};
			rs = conn.executeQuery(selectSql, selParams);
			VoteImage vi = null;
			while(rs.next()){
				vi = new VoteImage();
				vi.setVi_id(rs.getInt("SYS_DOCUMENTID"));
				vi.setVi_address(rs.getString("viAddress").startsWith("http")?rs.getString("viAddress"):getUrl()+rs.getString("viAddress"));
				vi.setVi_classification(rs.getInt("viClassification"));
				vi.setVi_height(rs.getInt("viHeight"));
				vi.setVi_name(rs.getString("viName"));
				vi.setVi_optionsid(rs.getInt("viOptionsID"));
				vi.setVi_size(rs.getInt("viSize"));
				vi.setVi_type(rs.getString("viType"));
				vi.setVi_uploadtime(rs.getString("viUploadTime"));
				vi.setVi_voteid(rs.getInt("viVoteID"));
				vi.setVi_width(rs.getInt("viWidth"));
			}
			return vi;
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			e.printStackTrace();
			return null;
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		} 
	}
	
	/**
	 * @author fei.lai
	 * @param id 投票设置ID
	 * @return 页眉图片实体
	 */
	public VoteImage getHeadVoteImageByVoteID(Integer id){
		DBSession conn = null;
		IResultSet rs = null;
		try {
			conn = Context.getDBSession();
			String selectSql = "select * from xy_membervoteimage where "
					+ "SYS_DELETEFLAG=0 and viVoteID=? and viClassification=2";
			Object[] selParams = new Object[]{id};
			rs = conn.executeQuery(selectSql, selParams);
			VoteImage vi = null;
			while(rs.next()){
				vi = new VoteImage();
				vi.setVi_id(rs.getInt("SYS_DOCUMENTID"));
				vi.setVi_address(rs.getString("viAddress").startsWith("http")?rs.getString("viAddress"):getUrl()+rs.getString("viAddress"));
				vi.setVi_classification(rs.getInt("viClassification"));
				vi.setVi_height(rs.getInt("viHeight"));
				vi.setVi_name(rs.getString("viName"));
				vi.setVi_optionsid(rs.getInt("viOptionsID"));
				vi.setVi_size(rs.getInt("viSize"));
				vi.setVi_type(rs.getString("viType"));
				vi.setVi_uploadtime(rs.getString("viUploadTime"));
				vi.setVi_voteid(rs.getInt("viVoteID"));
				vi.setVi_width(rs.getInt("viWidth"));
			}
			return vi;
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			e.printStackTrace();
			return null;
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		} 
	}
	
	/**
	 * @author fei.lai
	 * @param id 主题ID
	 * @return 选项集合
	 */
	public List<VoteOptions> getVoteOptionsByThemeID(Integer id){
		DBSession conn = null;
		IResultSet rs = null;
		try {
			conn = Context.getDBSession();
			String selectSql = "select * from xy_membervoteoptions where "
					+ "SYS_DELETEFLAG=0 and voThemeID=?";
			Object[] selParams = new Object[]{id};
			rs = conn.executeQuery(selectSql, selParams);
			ArrayList<VoteOptions> voList = new ArrayList<VoteOptions>();
			while(rs.next()){
				VoteOptions vo = new VoteOptions();
				vo.setVo_id(rs.getInt("SYS_DOCUMENTID"));
				vo.setVo_classification(rs.getString("voClassification"));
				vo.setVo_created(rs.getString("voCreated"));
				vo.setVo_index(rs.getInt("voIndex"));
				vo.setVo_last_modified(rs.getString("voLastModified"));
				vo.setVo_name(rs.getString("voName"));
				vo.setVo_voteid(rs.getInt("voVoteID"));
				vo.setVo_voteimage(this.getOptionsVoteImageByID(rs.getInt("SYS_DOCUMENTID")));
				vo.setVo_type(rs.getInt("voType"));
				vo.setVo_videoadd(rs.getString("voVideoAdd"));
				vo.setVo_votes(rs.getInt("voVotes"));
				vo.setVo_themeid(rs.getInt("voThemeID"));
				vo.setVo_view_pagecontent(rs.getString("voViewPageContent"));
				vo.setVo_show_op_imgonpage(rs.getInt("voShowOpImgOnpage"));
				voList.add(vo);
			}
			return voList;
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			e.printStackTrace();
			return null;
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		} 
	}
	/**
	 * @author sq
	 * @param id 选项ID
	 * @return 选项信息
	 */
	public VoteOptions getVoteOptionByOptionID(Integer id){
		DBSession conn = null;
		IResultSet rs = null;
		try {
			conn = Context.getDBSession();
			String selectSql = "select * from xy_membervoteoptions where "
					+ "SYS_DELETEFLAG=0 and SYS_DOCUMENTID=?";
			Object[] selParams = new Object[]{id};
			rs = conn.executeQuery(selectSql, selParams);
			VoteOptions vo = null;
			while(rs.next()){
				vo = new VoteOptions();
				vo.setVo_id(rs.getInt("SYS_DOCUMENTID"));
				vo.setVo_classification(rs.getString("voClassification"));
				vo.setVo_created(rs.getString("voCreated"));
				vo.setVo_index(rs.getInt("voIndex"));
				vo.setVo_last_modified(rs.getString("voLastModified"));
				vo.setVo_name(rs.getString("voName"));
				vo.setVo_voteid(rs.getInt("voVoteID"));
				vo.setVo_voteimage(this.getOptionsVoteImageByID(rs.getInt("SYS_DOCUMENTID")));
				vo.setVo_type(rs.getInt("voType"));
				vo.setVo_videoadd(rs.getString("voVideoAdd"));
				vo.setVo_votes(rs.getInt("voVotes"));
				vo.setVo_themeid(rs.getInt("voThemeID"));
				vo.setVo_view_pagecontent(rs.getString("voViewPageContent"));
				vo.setVo_show_op_imgonpage(rs.getInt("voShowOpImgOnpage"));
			}
			return vo;
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			e.printStackTrace();
			return null;
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		} 
	}
	/**
	 * @author fei.lai
	 * @param id 投票设置ID
	 * @return 主题实体集合
	 */
	public List<VoteThemes> getVoteThemesByVoteID(Integer id){
		DBSession conn = null;
		IResultSet rs = null;
		try {
			conn = Context.getDBSession();
			String selectSql = "select * from xy_membervotethemes where "
					+ "SYS_DELETEFLAG=0 and vtVoteID=?";
			Object[] selParams = new Object[]{id};
			rs = conn.executeQuery(selectSql, selParams);
			ArrayList<VoteThemes> vtList = new ArrayList<VoteThemes>();
			while(rs.next()){
				VoteThemes vt = new VoteThemes();
				vt.setVt_id(rs.getInt("SYS_DOCUMENTID"));
				vt.setVt_voteoption_list(this.getVoteOptionsByThemeID(rs.getInt("SYS_DOCUMENTID")));
				vt.setVt_created(rs.getString("vtCreated"));
				vt.setVt_index(rs.getInt("vtIndex"));
				vt.setVt_last_modified(rs.getString("vtLastModified"));
				vt.setVt_name(rs.getString("vtName"));
				vt.setVt_voteid(rs.getInt("vtVoteID"));
				vt.setVt_option_num(rs.getInt("vtOptionNum"));
				vt.setVt_most_choose_num(rs.getInt("vtMostChooseNum"));
				vt.setVt_min_choose_num(rs.getInt("vtMinChooseNum"));
				vtList.add(vt);
			}
			return vtList;
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			e.printStackTrace();
			return null;
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		} 
	}
	
	/**
	 * @author fei.lai
	 * @param id 投票设置ID
	 * @return 投票设置实体
	 */
	public VoteSettings getVoteSettingsByVoteID(Integer id){
		DBSession conn = null;
		IResultSet rs = null;
		try {
			conn = Context.getDBSession();
			String selectSql = "select * from xy_membervotesettings where "
					+ "SYS_DELETEFLAG=0 and SYS_DOCUMENTID=?";
			Object[] selParams = new Object[]{id};
			rs = conn.executeQuery(selectSql, selParams);
			VoteSettings vs = null;
			while(rs.next()){
				vs = new VoteSettings();
				vs.setVs_id(id);;
				vs.setVs_head_voteimage(this.getHeadVoteImageByVoteID(id));
				vs.setVs_votetheme_list(this.getVoteThemesByVoteID(id));
				vs.setVs_activity_intro(rs.getString("vsActivityIntro"));
				vs.setVs_aftershow_content(rs.getString("vsAfterShowContent"));
				vs.setVs_created(rs.getString("vsCreated"));
				vs.setVs_endtime(rs.getString("vsEndTime"));
				vs.setVs_footersword(rs.getString("vsFootersWord"));
				vs.setVs_hide_voteresult(rs.getInt("vsHideVoteResult"));
				vs.setVs_hostunit(rs.getString("vsHostUnit"));
				vs.setVs_last_modified(rs.getString("vsLastModified"));
				vs.setVs_openvote(rs.getInt("vsOpenVote"));
				vs.setVs_optiontype(rs.getInt("vsOptionType"));
				vs.setVs_pageview(rs.getInt("vsPageView"));
				vs.setVs_startime(rs.getString("vsStarTime"));
				vs.setVs_title(rs.getString("vsTitle"));
				vs.setVs_totalvotes(rs.getInt("vsTotalVotes"));
				vs.setVs_types(rs.getInt("vsTypes"));
				vs.setVs_user_inforule(rs.getString("vsUserInfoRule"));
				vs.setVs_validate_code(rs.getInt("vsValidateCode"));
				vs.setVs_option_isnull(rs.getInt("vsOptionIsNull"));
				vs.setVs_vote_type(rs.getInt("vsVoteType"));
				vs.setVs_vote_mode(rs.getInt("vsVoteMode"));  //模式
				vs.setVs_vote_inapp(rs.getInt("vsVoteInApp"));
				vs.setVs_vote_page_style(rs.getInt("vsVotePageStyle"));//页面样式
				vs.setVs_vote_repeat_one(rs.getInt("vsVoteRepeatOne"));//是否重复
			}
			return vs;
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			e.printStackTrace();
			return null;
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		} 
	}
	
	/**
	 * @author fei.lai
	 * @param begin 活动开始时间
	 * @param end 活动结束时间
	 * @return 0:活动进行中,1:活动未开始,2:活动以结束
	 * @throws Exception
	 */
	public Integer TimeState(String begin,String end)throws Exception{
		Date currentTime = new Date();
		Date beginTime = null;
		Date endTime = null;
			
		beginTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(begin);
		endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(end);
		
		if(beginTime.getTime()<currentTime.getTime()&&endTime.getTime()>currentTime.getTime()){
			return 0;//活动进行中
		}else if(beginTime.getTime()>=currentTime.getTime()){
			return 1;//活动未开始
		}else if(endTime.getTime()<=currentTime.getTime()){
			return 2;//活动已结束
		}
		return null;
		
	}
	
	/**
	 * @author jz.li
	 * 说明：检查redis，如果有则从redis取出json字符串
	 * 如果没有则创建json字符串并保存到redis
	 * @return json字符串
	 */
	public String getInfoByRedis(String voteid){
		Integer vid = null;
		try{
			vid = Integer.parseInt(voteid);
		}catch(Exception e){
			e.printStackTrace();
			return JSONObject.fromObject(new VoteInfo("参数类型错误","error")).toString();
		}
		String info = jedisClient.hget(dataName, dataName+vid);
		if(info==null){
			VoteSettings vs = this.getVoteSettingsByVoteID(vid);
			if(vs!=null){//判断是否有投票
				Integer isBegin = null;
				try{
					isBegin = this.TimeState(vs.getVs_startime(), vs.getVs_endtime());
				}catch(Exception e){
					e.printStackTrace();
					return JSONObject.fromObject(new VoteInfo("日期转换错误","error")).toString();
				}
				 
				String entityStr = JSONObject.fromObject(vs).toString();
				String jsonStr = JSONObject.fromObject(new VoteInfo(entityStr,"success")).toString();
				jedisClient.hset(dataName+".start", dataName+vid, vs.getVs_startime());
				jedisClient.hset(dataName+".end", dataName+vid, vs.getVs_endtime());
				jedisClient.hset(dataName, dataName+vid, jsonStr);
				return this.getFinalJsonStr(jsonStr, isBegin, jsonStr.indexOf("{")+1);
			}else{
				return JSONObject.fromObject(new VoteInfo("投票不存在","error")).toString();
			}
		}else{//存在info缓存时
			String start = jedisClient.hget(dataName+".start", dataName+vid);
			String end = jedisClient.hget(dataName+".end", dataName+vid);
			//String jsonStr = jedisClient.hget(dataName, dataName+vid);
			VoteSettings vs1 = this.getVoteSettingsByVoteID(vid);
			String entityStr = JSONObject.fromObject(vs1).toString();
			String jsonStr = JSONObject.fromObject(new VoteInfo(entityStr,"success")).toString();
			if(start==null&&end==null){//判断是否有时间缓存
				VoteSettings vs = this.getTimeByVoteID(vid);
				try{
					Integer isBegin = this.TimeState(vs.getVs_startime(), vs.getVs_endtime());
					jedisClient.hset(dataName+".start", dataName+vid, vs.getVs_startime());
					jedisClient.hset(dataName+".end", dataName+vid, vs.getVs_endtime());
					return this.getFinalJsonStr(jsonStr, isBegin, jsonStr.indexOf("{")+1);
				}catch(Exception e){
					e.printStackTrace();
					return JSONObject.fromObject(new VoteInfo("缓存日期转换错误","error")).toString();
				}
			}else{//有时间缓存时
				if(start==null||end==null){
					return JSONObject.fromObject(new VoteInfo("开始时间或结束时间为空","error")).toString();
				}else{
					try{
						Integer isBegin = this.TimeState(start, end);
						return this.getFinalJsonStr(jsonStr, isBegin, jsonStr.indexOf("{")+1);
					}catch(Exception e){
						e.printStackTrace();
						return JSONObject.fromObject(new VoteInfo("缓存日期转换错误","error")).toString();
					}
				}
			}
		}
		
	}
	
	/**
	 * @author jz.li
	 * 说明：检查redis，如果有则从redis取出json字符串
	 * 如果没有则创建json字符串并保存到redis
	 * @param vid 投票设置ID
	 * @return json字符串
	 */
	public String getInfoByRedis(int vid){
		String info = jedisClient.hget(dataName, dataName+vid);
		if(true){
			VoteSettings vs = this.getVoteSettingsByVoteID(vid);
			if(vs!=null){//判断是否有投票
				Integer isBegin = null;
				try{
					isBegin = this.TimeState(vs.getVs_startime(), vs.getVs_endtime());
				}catch(Exception e){
					e.printStackTrace();
					return JSONObject.fromObject(new VoteInfo("日期转换错误","error")).toString();
				}
				 
				String entityStr = JSONObject.fromObject(vs).toString();
				String jsonStr = JSONObject.fromObject(new VoteInfo(entityStr,"success")).toString();
				jedisClient.hset(dataName+".start", dataName+vid, vs.getVs_startime());
				jedisClient.hset(dataName+".end", dataName+vid, vs.getVs_endtime());
				jedisClient.hset(dataName, dataName+vid, jsonStr);
				return this.getFinalJsonStr(jsonStr, isBegin, jsonStr.indexOf("{")+1);
			}else{
				return JSONObject.fromObject(new VoteInfo("投票不存在","error")).toString();
			}
		}
		return "";
	}
	
	/**
	 * @author fei.lai
	 * 说明：检查redis，如果有则从redis取出json字符串
	 * 如果没有则创建json字符串并保存到redis
	 * @return json字符串
	 */
	public String getInfoByRedis1(String voteid){
		Integer vid = null;
		try{
			vid = Integer.parseInt(voteid);
		}catch(Exception e){
			e.printStackTrace();
			return JSONObject.fromObject(new VoteInfo("参数类型错误","error")).toString();
		}
		String info = jedisClient.hget(dataName, dataName+vid);
		if(info==null){
			VoteSettings vs = this.getVoteSettingsByVoteID(vid);
			if(vs!=null){//判断是否有投票
				Integer isBegin = null;
				try{
					isBegin = this.TimeState(vs.getVs_startime(), vs.getVs_endtime());
				}catch(Exception e){
					e.printStackTrace();
					return JSONObject.fromObject(new VoteInfo("日期转换错误","error")).toString();
				}
				 
				String entityStr = JSONObject.fromObject(vs).toString();
				String jsonStr = JSONObject.fromObject(new VoteInfo(entityStr,"success")).toString();
				jedisClient.hset(dataName+".start", dataName+vid, vs.getVs_startime());
				jedisClient.hset(dataName+".end", dataName+vid, vs.getVs_endtime());
				jedisClient.hset(dataName, dataName+vid, jsonStr);
				return this.getFinalJsonStr(jsonStr, isBegin, jsonStr.indexOf("{")+1);
			}else{
				return JSONObject.fromObject(new VoteInfo("投票不存在","error")).toString();
			}
		}else{//存在info缓存时
			String start = jedisClient.hget(dataName+".start", dataName+vid);
			String end = jedisClient.hget(dataName+".end", dataName+vid);
			String jsonStr = jedisClient.hget(dataName, dataName+vid);
			if(start==null&&end==null){//判断是否有时间缓存
				VoteSettings vs = this.getTimeByVoteID(vid);
				try{
					Integer isBegin = this.TimeState(vs.getVs_startime(), vs.getVs_endtime());
					jedisClient.hset(dataName+".start", dataName+vid, vs.getVs_startime());
					jedisClient.hset(dataName+".end", dataName+vid, vs.getVs_endtime());
					return this.getFinalJsonStr(jsonStr, isBegin, jsonStr.indexOf("{")+1);
				}catch(Exception e){
					e.printStackTrace();
					return JSONObject.fromObject(new VoteInfo("缓存日期转换错误","error")).toString();
				}
			}else{//有时间缓存时
				if(start==null||end==null){
					return JSONObject.fromObject(new VoteInfo("开始时间或结束时间为空","error")).toString();
				}else{
					try{
						Integer isBegin = this.TimeState(start, end);
						return this.getFinalJsonStr(jsonStr, isBegin, jsonStr.indexOf("{")+1);
					}catch(Exception e){
						e.printStackTrace();
						return JSONObject.fromObject(new VoteInfo("缓存日期转换错误","error")).toString();
					}
				}
			}
		}
		
	}
	
	/**
	 * @author fei.lai
	 * @param voteinfo 缓存json字符串 
	 * @param isBegin 投票状态码
	 * @param index 插入点
	 * @return 接口最终json包
	 */
	public String getFinalJsonStr(String voteinfo,Integer isBegin,Integer index){
		StringBuilder sb = new StringBuilder();
		String isbegin = "\"isbegin\":"+isBegin+",";
		sb.append(voteinfo).insert(index, isbegin); 
		return sb.toString();
	}
	
	/**
	 * @author fei.lai
	 * 说明：后台创建投票活动保存时调用，清空redis中数据
	 * 并保存最新数据
	 */
	public void clearRedis(Integer vid){
		jedisClient.hclear(dataName, dataName+vid);
		jedisClient.hclear(dataName+".start", dataName+vid);
		jedisClient.hclear(dataName+".end", dataName+vid);
		VoteSettings vs = this.getVoteSettingsByVoteID(vid);
		String entityStr = JSONObject.fromObject(vs).toString();
		String jsonStr = JSONObject.fromObject(new VoteInfo(entityStr,"success")).toString();
		jedisClient.hset(dataName, dataName+vid, jsonStr);
		jedisClient.hset(dataName+".start", dataName+vid, vs.getVs_startime());
		jedisClient.hset(dataName+".end", dataName+vid, vs.getVs_endtime());
	}

	/**
	 * @param rootURL 项目根路径
	 *//*
	public void setRootURL(String rootURL) {
		this.rootURL = rootURL;
	}*/
	
	/**
	 * 把投票开始时间和结束时间存入缓存，返回活动进行状态
	 * @param vid 投票设置ID
	 * @return 时间对比JSON字符串
	 */
	public String getInfoTimeByRedis(Integer vid){
		String start = jedisClient.hget(dataName+".start", dataName+vid);
		String end = jedisClient.hget(dataName+".end", dataName+vid);
		if(start==null&&end==null){
			VoteSettings vs = this.getTimeByVoteID(vid);
			if(vs!=null){
				try{
					Integer isBegin = this.TimeState(vs.getVs_startime(), vs.getVs_endtime());
					jedisClient.hset(dataName+".start", dataName+vid, vs.getVs_startime());
					jedisClient.hset(dataName+".end", dataName+vid, vs.getVs_endtime());
					return JSONObject.fromObject(new VoteInfoTime(isBegin,"success","")).toString();
				}catch(Exception e){
					e.printStackTrace();
					return JSONObject.fromObject(new VoteInfoTime(3,"error","日期转换出错")).toString();
				}
			}else{
				return JSONObject.fromObject(new VoteInfoTime(3,"error","活动不存在")).toString();
			}
		}else{
			if(start==null||end==null){
				return JSONObject.fromObject(new VoteInfoTime(3,"error","开始时间或结束时间为空")).toString();
			}else{
				try{
					Integer isBegin = this.TimeState(start, end);
					return JSONObject.fromObject(new VoteInfoTime(isBegin,"success","")).toString();
				}catch(Exception e){
					e.printStackTrace();
					return JSONObject.fromObject(new VoteInfoTime(3,"error","缓存日期转换出错")).toString();
				}
			}
			
		}
	}
	
	/**
	 * @param id 活动设置ID
	 * @return 只有开始时间和结束时间的活动设置实体类
	 */
	public VoteSettings getTimeByVoteID(Integer id){
		DBSession conn = null;
		IResultSet rs = null;
		try {
			conn = Context.getDBSession();
			String selectSql = "select * from xy_membervotesettings where "
					+ "SYS_DELETEFLAG=0 and SYS_DOCUMENTID=?";
			Object[] selParams = new Object[]{id};
			rs = conn.executeQuery(selectSql, selParams);
			VoteSettings vs = null;
			while(rs.next()){
				vs = new VoteSettings();
				vs.setVs_id(id);
				vs.setVs_startime(rs.getString("vsStarTime"));
				vs.setVs_endtime(rs.getString("vsEndTime"));
			}
			return vs;
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			e.printStackTrace();
			return null;
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		} 
	}
	
	/**
	 * 
	 * @param voteid 投票设置ID
	 * @param vote_optionid 投票选项ID
	 * @return 
	 */
	public String getVoteCountsByRedis(String voteid,String vote_optionid){
		
		String[] vote_optionids = vote_optionid.split(",");
		//投票总数
		String voteCount = jedisClient.get(RedisKey.VOTE_ACCESSCOUNT+voteid);
		String voteAccessCount = jedisClient.get(RedisKey.VOTE_VOTECOUNT+voteid);
		if(voteAccessCount==null){
			voteAccessCount = "0";
		}
		if(voteCount==null){
			voteCount = "0";
		}
		
		VoteCount vc = new VoteCount();
		List<VoteOptionCount> vocList = new ArrayList<VoteOptionCount>();
		
		for(int i = 0; i<vote_optionids.length;i++){
			VoteOptionCount voc = new VoteOptionCount();
			String opCount = jedisClient.hmget(RedisKey.VOTE_OPTION_PERSONINFO + voteid +"."+ vote_optionids[i], RedisKey.VOTE_OPTIONCOUNT+voteid+"."+vote_optionids[i]).get(0);
			if(opCount==null){
				opCount = "0";
			}
			voc.setOptionid(vote_optionids[i]);
			voc.setOption_count(opCount);
			vocList.add(voc);
		}
		vc.setVote_access_count(voteAccessCount);
		vc.setVote_count(voteCount);
		vc.setOption_list(vocList);
		
		String jsonStr = JSONObject.fromObject(vc).toString();
		return JSONObject.fromObject(new VoteInfo(jsonStr,"success")).toString();
	}
	/**
	 * 获取系统地址
	 * @return
	 * @throws E5Exception
	 */
	private String getUrl() throws E5Exception{
		int appID = 1;
		SysConfigReader configReader = (SysConfigReader)Context.getBean(SysConfigReader.class);
		String rActionUrl = configReader.get(appID, "会员中心", "根地址");
		rActionUrl = rActionUrl.replace("amucsite", "amuc");
		return rActionUrl;
	}

	/**
	 * 更新缓存，投票总数减一，投票选项修改
	 * @param docID
	 * @param opId
	 * @param operateDesc
	 */
	public void refreshVoteRedis(long docID, int opId, String operateDesc) throws E5Exception {
		Integer incr = operateDesc.equals("add") ? 1 : -1;
		Document voteOpt = this.getVoteOptNum(opId);
		if (voteOpt == null)
			return;
		incr *= (Integer) voteOpt.get("voVotes");
		String votecount = jedisClient.get(RedisKey.VOTE_VOTECOUNT + docID);   //vote.voteCount. voteId
		// vote.option.personinfo. voteId		vote.optionCount. optId
		String voteOptCount = jedisClient.hmget(RedisKey.VOTE_OPTION_PERSONINFO + docID + "." + opId, RedisKey.VOTE_OPTIONCOUNT + docID + "." + opId).get(0);
		if(StringUtils.isEmpty(votecount) || StringUtils.isEmpty(voteOptCount))
			return;
		int voteCount = Integer.valueOf(votecount) + incr;
		int optCount = Integer.valueOf(voteOptCount) + incr;
		if (voteCount < 0 || optCount < 0)
			return;
		jedisClient.set(RedisKey.VOTE_VOTECOUNT + docID, String.valueOf(voteCount));

		Map<String, String> vmap = new HashMap<String, String>();
		vmap.put("username_uid", jedisClient.hmget(RedisKey.VOTE_OPTION_PERSONINFO + docID + "." + opId,"username_uid").get(0));
		vmap.put("vote" + docID, String.valueOf(docID));
		vmap.put("voteOption" + opId, String.valueOf(opId));
		vmap.put(RedisKey.VOTE_VOTECOUNT + docID, String.valueOf(voteCount));
		vmap.put(RedisKey.VOTE_OPTIONCOUNT + docID + "." + opId, String.valueOf(optCount));
		vmap.put("userid", jedisClient.hmget(RedisKey.VOTE_OPTION_PERSONINFO + docID + "." + opId,"userid").get(0));
		jedisClient.hmset(RedisKey.VOTE_OPTION_PERSONINFO + docID + "."+ opId, vmap);
	}

	private Document getVoteOptNum(int opId) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_VOTEOPTION, "xy");
		return docManager.get(docLib.getDocLibID(), opId);
	}

	/**
	 * 删除缓存的选项，投票数减一
	 * @param docID
	 * @param opId
	 */
	public Boolean removeOptRedis(long docID, int opId) throws E5Exception {
		String votecount = jedisClient.get(RedisKey.VOTE_VOTECOUNT + docID);
		Document voteOpt = this.getVoteOptNum(opId);
		if (voteOpt == null || voteOpt.getInt("SYS_DELETEFLAG") == 1)
			return false;
		if(StringUtils.isEmpty(votecount))
			return true;
		Integer voteCount = Integer.valueOf(votecount) - (Integer) voteOpt.get("voVotes");
		if (voteCount < 0)
			return true;
		jedisClient.set(RedisKey.VOTE_VOTECOUNT + docID, String.valueOf(voteCount));
		jedisClient.clear(RedisKey.VOTE_OPTION_PERSONINFO + docID + "."+ opId);
		return true;
	}
}
