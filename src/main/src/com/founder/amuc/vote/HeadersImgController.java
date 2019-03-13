package com.founder.amuc.vote;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.founder.amuc.api.vote.VoteInfoManager;
import com.founder.amuc.commons.Constant;
import com.founder.amuc.commons.FormViewerHelper;
import com.founder.amuc.commons.InfoHelper;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.context.Context;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.e5.web.BaseController;
import com.founder.xy.redis.RedisKey;
import com.founder.amuc.commons.RedisManager;
import com.founder.amuc.commons.JedisClient;

@Controller
@RequestMapping("/amuc/headersImg")
@SuppressWarnings({ "unchecked", "rawtypes" })
public class HeadersImgController extends BaseController{

	@Autowired
	VoteInfoManager voteInfoManager ;
	@Autowired
	JedisClient jedisClient;
	
	@Override
	protected void handle(HttpServletRequest request, HttpServletResponse response,
			Map model) throws Exception {
		
		String action = get(request, "action");
		
		if("uploadadd".equals(action)){
			uploadadd(request,response,model);
		}else if("save".equals(action)){
			save(request,response,model);
		}else if("clear".equals(action)){
			clear(request,response,model);
		}else if("delete".equals(action)){
			delete(request,response,model);
		}
		
		
		
	}
	
	@RequestMapping("/uploadadd.do")
	private ModelAndView uploadadd(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception {
		
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_VOTE, "uc");
		//long docID = getInt(request, "DocIDs", 0);
		long docID = getInt(request, "voteID", 0);
		String uuid = get(request, "UUID");
		if(docLib != null){
			String[] formJsp = FormViewerHelper.getFormJsp(docLib.getDocLibID(), docID, "vsHandFrom", uuid);
			model.put("formHead", formJsp[0]);
			model.put("formContent", FormViewerHelper.delFormStr(formJsp[1]));
		}
		return new ModelAndView("amuc/vote/HandersImgadd",model);
	}
	
	@RequestMapping("/save.do")
	private void save(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception{
		
		long docID = getInt(request, "voteID", 0);
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_VOTE, "uc");
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] docs = docManager.find(docLib.getDocLibID(), "SYS_DOCUMENTID=? and  SYS_DELETEFLAG=0 ", new Object[]{docID});
		if(docs.length>0){
			docs[0].set("vsFootersWord", get(request, "vsFootersWord"));
			docManager.save(docs[0]);
			voteInfoManager.clearRedis((int)docID);
			voteInfoManager.getInfoByRedis((int)docID);
		}
		/*String viewName = "redirect:/e5workspace/after.do?DocLibID=" + docLib.getDocLibID()
				+ "&DocIDs=" + docLib.getDocLibID()+ "&UUID=" + get(request, "UUID");
		model.put("@VIEWNAME@", viewName);*/
		String viewName ="redirect:/e5workspace/after.do?UUID="+ get(request, "UUID");
		model.put("@VIEWNAME@", viewName);
	}
	
	@RequestMapping("/clear.do")
	private void clear(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception{
		long docID = getInt(request, "DocIDs", 0);
		this.clearRedisCount(docID);//清除缓存统计总数
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_VOTE, "uc");
		if(docID>0){
			DBSession dbSession = Context.getDBSession();;
			dbSession.beginTransaction();//开始事务
			String selRelSql="update xy_membervotesettings set vsTotalVotes=0 where SYS_DOCUMENTID="+docID;	
			String optionsSql="update xy_membervoteoptions set voVotes=0 where voVoteID="+docID;
			dbSession.executeDDL(selRelSql);
			dbSession.executeDDL(optionsSql);
			dbSession.commitTransaction();
			ResourceMgr.closeQuietly(dbSession);
		}
		String viewName = "redirect:/e5workspace/after.do?DocLibID=" + docLib.getDocLibID()
				+ "&DocIDs="
			 + docLib.getDocLibID()+ "&UUID=" + get(request, "UUID");
		model.put("@VIEWNAME@", viewName);
	}

	@RequestMapping("/delete.do")
	private void delete(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception{
		long docID = getInt(request, "DocIDs", 0);
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_VOTE, "uc");
		if(docID>0){
			DBSession dbSession = Context.getDBSession();
			dbSession.beginTransaction();//开始事务
			String selRelSql="update xy_membervotesettings set SYS_DELETEFLAG=1 where SYS_DOCUMENTID="+docID;		
			dbSession.executeDDL(selRelSql);
			dbSession.commitTransaction();
			ResourceMgr.closeQuietly(dbSession);
		}
		output("@refresh@", response);
	}
	
	private void clearRedisCount(Long voteid) throws Exception{
		jedisClient.clear(RedisKey.VOTE_PERSONCOUNT+voteid);//统计清零
		jedisClient.clear(RedisKey.VOTE_ACCESSCOUNT+voteid);
		DBSession conn = null;
		IResultSet rs = null;
		try {
			conn = Context.getDBSession();
			String selectSql = "select * from xy_membervoteoptions where voVoteID=?";
			Object[] selParams = new Object[]{voteid};
			rs = conn.executeQuery(selectSql, selParams);
			while(rs.next()){
				//投票活动下所有选项投票数清零
				jedisClient.clear(RedisKey.VOTE_OPTION_PERSONINFO + voteid +"."+rs.getInt("SYS_DOCUMENTID"));
				jedisClient.clear(RedisKey.VOTE_VOTECOUNT+voteid);
				jedisClient.clear(RedisKey.VOTE_USERID+voteid);
				jedisClient.clear(RedisKey.VOTE_IP+voteid);
				jedisClient.clear(RedisKey.VOTE_OPTIONCOUNT+voteid+"."+rs.getInt("SYS_DOCUMENTID"));
			}
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			e.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		} 
	}
}
