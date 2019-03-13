package com.founder.amuc.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Jedis;
import net.sf.json.JSONObject;

import com.founder.amuc.collection.CollectHelper;
import com.founder.amuc.collection.ScoreHelper;
import com.founder.amuc.commons.Constant;
import com.founder.amuc.commons.DateHelper;
import com.founder.amuc.commons.InfoHelper;
import com.founder.amuc.commons.RedisManager;
import com.founder.amuc.commons.RedisToolUtil;
import com.founder.amuc.member.MemberHelper;
import com.founder.amuc.member.MemberReader;
import com.founder.amuc.score.ScoreManager;
import com.founder.amuc.tenant.TenantManager;
import com.founder.e5.cat.CatReader;
import com.founder.e5.cat.Category;
import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.Log;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.e5.dom.DocTypeField;
import com.founder.e5.workspace.ImportSaver;
import com.founder.e5.workspace.app.form.LogHelper;
import com.founder.amuc.commons.DateFormatAmend;
import com.founder.amuc.commons.JedisClient;

/**
 * 行为实时入库接口
 * 访问方式：<webroot>/event/
 * @author Gong Lijie
 * 2014-8-4
 */
@Controller
@RequestMapping("/api/event")
public class EventAdapter {
	
	//private static JedisCluster jedisCluster = RedisManager.getJedisCluster();
	
	@Autowired
	JedisClient jedisClient;

	
	

	
	/** 
	* @author  leijj 
	* 功能： 获取行为类型
	* 访问方式：<webroot>/event/eventTypes
	* @param tc 租户代号（可选的）
	* @return 行为类型字符串，以英文逗号“,”做分割
	* @throws E5Exception 
	*/
	@RequestMapping("/eventTypes.do")
	public void eventTypes(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception{
		CatReader catreader =(CatReader)com.founder.e5.context.Context.getBean(CatReader.class);//取出数据来源
		Category[] categorys = catreader.getCats("行为类型");
		StringBuilder eventTypes = new StringBuilder("");
		if(categorys != null && categorys.length > 0){
			int  i = 0;
			for(Category category : categorys){
				if(i > 0){
					eventTypes.append(",");
				}
				eventTypes.append(category.getCatName());
				i ++;
			}
		}
		outputJson(String.valueOf(eventTypes), response);
	}
	
	/** 
	* @author  leijj 
	* 功能： 获取积分规则列表
	* 访问方式：<webroot>/event/scoreRuleList?source=
	* @param source 数据来源，如“网站”
	* @param tc 租户代号（可选的）
	* @return 行为类型字符串，以英文逗号“,”做分割
	* @throws E5Exception 
	*/
	@RequestMapping("/scoreRuleList.do")
	public void scoreRuleList(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception{
		//找到会员数据来源ID
		String source = request.getParameter("source");
		String tc = request.getParameter("tc");
		ScoreManager scoreManager = new ScoreManager();
		String scoreRuleList = scoreManager.scoreRuleList(source, tc);
		outputJson(String.valueOf(scoreRuleList), response);
	}
	
	/**
	 * 会员的兑换行为入库
	 * 访问方式：<webroot>/event/convert?source=&member=&info=
	 * @param source 数据来源，如“网站”
	 * @param tc 租户代号（可选的）
	 * @param member 源系统中的会员ID
	 * @param info 兑换数据，json格式，每个key与会员积分类型的字段名（ColumnCode）一致
	 * 		如：{msTime:"2014/10/12 10:00:00",msEvent:"百货类",msMemo:"兑换鲁花花生油10斤装一桶",msScore:"2000"}
	 * @return
	 * @throws E5Exception
	 */
	@RequestMapping("/convert.do")
	public void convert(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception{
		//System.out.println("---------请求兑换行为接口---------"+time);
		String source = request.getParameter("source");
		String tc = request.getParameter("tc");
		String memberID = request.getParameter("member");
		String info = request.getParameter("info");
		if (tc == null) tc = TenantManager.DEFAULTCODE;
		if (source != null && memberID != null && info != null) {
			//找到会员数据来源ID
			int eSourceID = InfoHelper.getEventSourceCat(source);
			MemberReader memberReader = new MemberReader();
			//修改为根据会员ID在会员表中查找对应会员
			Document member =memberReader.findMemberById(source, memberID, tc,null );
			if(member == null)
				outputJson(String.valueOf("根据会员ID：" + memberID + "，找不到相关会员。"), response);
			//添加会员积分表记录
			DocLib scoreLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBERSCORE, tc);
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			Document doc = docManager.newDocument(scoreLib.getDocLibID(), InfoHelper.getID(scoreLib.getDocTypeID()));
			doc.setFolderID(scoreLib.getFolderID());
			doc.setDeleteFlag(0);
			doc.set("msMember_ID", member.getDocID());
			doc.set("msMember", member.getString("mName"));
			doc.set("msType", 3);//兑换
			doc.set("msIsTrading", 1);
			doc.set("msSource", source);
			doc.set("msSource_ID", eSourceID);
			
			JSONObject jinfo = JSONObject.fromObject(info);
			doc.set("msEvent", jinfo.get("msEvent"));//兑换类型
			doc.set("msTime", DateFormatAmend.timeStampDispose(String.valueOf(jinfo.get("msTime"))));	//兑换时间
			doc.set("msMemo", jinfo.get("msMemo"));	//兑换详情描述
			doc.set("msScore", jinfo.get("msScore"));//花费积分
			
			int score = doc.getInt("msScore");
			if (score > 0) score = -1 * score;
			
			doc.set("msScore", score);
			int curScore = member.getInt("mScore");
			//改变会员的积分属性
			int total = curScore;
			total += score;
			member.set("mScore", total);
			if(total < 0)
				outputJson(String.valueOf("您目前的剩余积分值是：" + curScore + "，不能兑换。"), response);
			DBSession conn = null;
			try {
				conn = com.founder.e5.context.Context.getDBSession();
				conn.beginTransaction();
				
				docManager.save(doc, conn);
				docManager.save(member, conn);
				
				conn.commitTransaction();
				outputJson(String.valueOf("null"), response);
			} catch (Exception e) {
				ResourceMgr.rollbackQuietly(conn);
				outputJson(String.valueOf("保存兑换记录时异常：" + e.getLocalizedMessage()), response);
			} finally {
			
				ResourceMgr.closeQuietly(conn);
			}
		}else{
			outputJson(String.valueOf("接口调用失败，请查看参数传递是否正确"), response);
		}
	}
	
	//计算积分
	private  String createScore(String tenantCode, Document data, int siteID) {
		try {
			String memberName = CollectHelper.getMemberName(tenantCode, data.getLong("eMemberID"));
			
			String rescore = ScoreHelper.createScore(tenantCode, memberName, data, false, siteID);
			return rescore;
		} catch (Exception e) {
			System.out.println("计算行为积分时错误（原ID=" + data.getString("eOriID") + "）：" + e.getLocalizedMessage());
			return "error";
		}
	}

	private Document assembleData(String tc, String info, String memberID) throws Exception {
		
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBEREVENT, tc);
		
		//所有非平台字段
		DocTypeField[] all = CollectHelper.getFields(docLib.getDocTypeID());
		Document data = CollectHelper.newData(docLib, 0, null);
		data.set("eMemberID", memberID);
		
		List<DocTypeField> fields = new ArrayList<DocTypeField>(); //汇总出设置的字段
		//解析info的值
		setValues(data, info, fields, all);
		data.set("eStartTime", DateFormatAmend.timeStampDispose(DateUtils.format().substring(0, 19)));
		//检查
		//String webRoot = getWebRoot();
		String webRoot = "";
		ImportSaver saver = new ImportSaver();
		
		//容忍有错的字段，只要数据库不会报错就不打回去
		CollectHelper.checkFields(saver, data, fields, webRoot);		
		return data;
	}
	
	@SuppressWarnings("unchecked")
	private void setValues(Document data, String info, List<DocTypeField> fields, 
			DocTypeField[] all) {
		JSONObject jinfo = JSONObject.fromObject(info);
		Iterator<String> keys = jinfo.keys();
		
		while (keys.hasNext()) {
			String key = (String) keys.next();
			data.set(key, jinfo.get(key));
			fields.add(CollectHelper.getField(all, key));
		}
	}
	
	//判断修改详情和是否有需要重新计算积分
	@SuppressWarnings("unused")
	private void whatChanged(Document oldData, Document data, boolean isTrading) throws E5Exception {
		if (oldData != null) {
			String diff = LogHelper.whatChanged(oldData, data);
			data.set("whatChanged", diff); //暂时把修改详情记录在一个不存在的字段里，传递给调用者
			
			//若是交易类行为，则判断是否有金额变化，相应改积分。
			//若之前并没有产生积分，则认为是一次性采集来的，不再判断
			if (isTrading && (oldData.getInt("eHasScore") > 0)) {
				float oldMoney = oldData.getFloat("eTotalPrice");
				float newMoney = data.getFloat("eTotalPrice");
				
				if (oldMoney >= 0 && newMoney >= 0 && Math.abs(newMoney - oldMoney) >= 0.01) {
					data.set("scoreChanged", 1); //告诉上一级有积分变换
				}
			}
		}
	}

	
	/**
	 * 新华app行为入库接口，和南方分开
	 * @return
	 * @throws E5Exception
	 */
	@RequestMapping("/event1.do")
	public void event1(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception{
		response.setHeader("Access-Control-Allow-Origin","*");
		
		String eType = request.getParameter("eType");
		String tc = request.getParameter("tc");
		String member = request.getParameter("member");
		String time = request.getParameter("time");
	    String sign = request.getParameter("sign");
	    int siteID = Integer.parseInt(request.getParameter("siteID"));
	    
	    System.out.println("eType:"+eType+",tc:"+tc+ ",member:"+member+",time:"+time+",sign:"+sign+",siteID:"+siteID);
	    
    
	    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");//设置日期格式
	    Date dateBegin=new Date();
	    long startTime=System.currentTimeMillis();
	    System.out.println("内网进入时间： 标准形式-->"+df.format(dateBegin)+"     long值形式-->"+startTime);
		
		if(StringUtils.isBlank(eType) || StringUtils.isBlank(member)){
			JSONObject obj = new JSONObject();
			obj.put("code", "1000");//失败
			obj.put("msg", "接口调用失败，请查看参数传递是否正确");
			System.out.println("内网接口obj：-->"+String.valueOf(obj));
			outputJson(String.valueOf(obj), response);
			return;
		}
		
		TreeMap<String,String> params=new TreeMap<String,String>();
	      params.put("eType", eType);
	      params.put("member",member);
	      params.put("time", time);
	      String paramResult=checkRequest(sign,params);
	      if(paramResult!=null){
	    	  JSONObject json = new JSONObject();
	    	  json.put("code", "1000");
	          json.put("result", paramResult);
	          System.out.println("积分入库，校验结果："+String.valueOf(json));
	    	  outputJson(String.valueOf(json), response);
	    	  System.out.println("内网接口obj：-->"+String.valueOf(json));
	    	  return;
	      }
		
		
		Document mDoc = MemberHelper.getMember(tc,Long.parseLong(member));
		if(mDoc == null){
			JSONObject obj = new JSONObject();
			obj.put("code", "1011");
			obj.put("msg", "用户不存在");
			outputJson(String.valueOf(obj), response);
			System.out.println("内网接口obj：-->"+String.valueOf(obj));
			return;
		}
		if(mDoc != null && mDoc.getInt("mStatus") == 0){
			JSONObject obj = new JSONObject();
			obj.put("code", "1010");
			obj.put("msg", "该用户被禁用");
			outputJson(String.valueOf(obj), response);
			System.out.println("内网接口obj：-->"+String.valueOf(obj));
			return;
		}
		
		JSONObject obj = new JSONObject();
		obj.put("eType", eType);
		obj.put("eStartTime", DateHelper.getFormat());
		String info = obj.toString();
		
		if(member != null){
			Document data = assembleData(tc, info, member);
			
			boolean isNew = data.isNew();
			
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			String result = null;
			try {
				docManager.save(data);
				//非交易类行为，计算积分
				if (isNew) {
					result = createScore(tc, data, siteID);
				}
				if("null".equals(result) || "error".equals(result)){
					obj.put("code", "1001");
					obj.put("msg", "计算行为积分时错误");
				}else{
					obj.put("code", "1002");
					obj.put("result",result);
				}
			} catch (E5Exception e) {
				obj.put("code", "1005");
				obj.put("msg", "数据库操作时出错：" + e.getLocalizedMessage());
			}
			
		}
		System.out.println("内网接口obj：-->"+String.valueOf(obj));
		System.out.println();
		Date dateEnd=new Date();
		long endTime=System.currentTimeMillis();
		System.out.println("内网返回时间： 标准形式-->"+df.format(dateEnd)+"     long值形式-->"+endTime);
		System.out.println("内网方法执行时间-->  "+(endTime-startTime)+"ms");
		outputJson(String.valueOf(obj), response);
	}
	
	/**
	 * 添加或修改非交易类行为
	 * 访问方式：<webroot>/event/event?info=&member=
	 * 		如：{eStartTime:"2014/10/12 10:00:00",eType:"登录",eChannel:2}
	 *      eChannel取值： 0-网站、1-触屏、2-APP
	 * @param member 会员ID（修改时不需要）
	 * @return 成功入库时为null，否则返回错误描述
	 * @throws E5Exception
	 */
	@RequestMapping("/event.do")
  public void event(HttpServletRequest request, HttpServletResponse response,
      Map model) throws Exception {
	request.setCharacterEncoding("utf-8");
    JSONObject json = new JSONObject();
    try {
      String tc = request.getParameter("tc");
      String info = request.getParameter("info");
      String member = request.getParameter("member");
      String time = request.getParameter("time");
      String sign = request.getParameter("sign");
      int siteID = Integer.parseInt(request.getParameter("siteID"));
      
      TreeMap<String,String> params=new TreeMap<String,String>();
      params.put("info", info);
      params.put("member",member);
      params.put("time", time);
      String paramResult=checkRequest(sign,params);
      if(paramResult!=null){
    	  json.put("code", "1000");
          json.put("result", paramResult);
          System.out.println("积分入库，校验结果："+String.valueOf(json));
    	  outputJson(String.valueOf(json), response);
    	  return;
      }

      // MultivaluedMap<String, String> map = uriInfo.getQueryParameters();

      json.put("eStartTime", DateHelper.getFormat());

      if (member != null && info != null) {

        Document data = assembleData(tc, info, member);

        boolean isNew = data.isNew();
        String operation = (isNew) ? "接口-添加" : "接口-修改";
        String detail = (isNew) ? "" : data.getString("whatChanged");

        DocumentManager docManager = DocumentManagerFactory.getInstance();
        String result = null;
        try {
          docManager.save(data);
          // 非交易类行为，在新入库时计算积分
          if (isNew) {
            result = createScore(tc, data, siteID);
          }
          LogHelper.writeLog(data.getDocLibID(), data.getDocID(), "接口", 0,
              operation, detail);

          if ("null".equals(result) || "error".equals(result)) {
            json.put("code", "1001");
            json.put("msg", "计算行为积分时错误");
          } else {
            json.put("code", "1002");
            json.put("result", result);
          }

        } catch (E5Exception e) {
          json.put("code", "1005");
          json.put("msg", "数据库操作时出错：" + e.getLocalizedMessage());
        }
      } else {
        json.put("code", "1000");
        json.put("msg", "接口调用失败，请查看参数传递是否正确");
      }
    } catch (E5Exception e) {
      json.put("code", 1);
      json.put("error", "保存失败");
      json.put("e", e.getLocalizedMessage());
      e.printStackTrace();
    }
    outputJson(String.valueOf(json), response);
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
			result="非法请求，超时!";
			return result;
		}
		String currentSign=getCurrentSign(param.toString());
		if(!sign.equals(currentSign)){
			result="非法请求，签名错误！";
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