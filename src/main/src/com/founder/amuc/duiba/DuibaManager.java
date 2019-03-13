package com.founder.amuc.duiba;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import com.founder.amuc.commons.Constant;
import com.founder.amuc.commons.DateHelper;
import com.founder.amuc.commons.InfoHelper;
import com.founder.e5.commons.Log;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import org.springframework.stereotype.Component;
import com.founder.amuc.commons.DocTypes;
import com.founder.xy.commons.LibHelper;

@Component
public class DuibaManager {
	
	private Log log = com.founder.e5.context.Context.getLog("amuc.duiba");
	
	/**
	 * 将兑吧返回的信息存储进ucDBConsumeRecord库中
	 * @throws E5Exception 
	 */
	public JSONObject saveDBOrder(CreditConsumeParams params,String tenantCode) throws E5Exception{
		String uid=params.getUid();//用户id
		long credits=params.getCredits();//本次兑换扣除的积分
	    String app_Key = params.getAppKey(); //应用的唯一标识
	    Date timestamp = params.getTimestamp(); //1970-01-01开始的时间戳，毫秒为单位。
	    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    String orderTime = simpleDateFormat.format(timestamp);  //将时间戳转换为时间
	    String description = params.getDescription(); //本次积分消耗的描述
	    String orderNum = params.getOrderNum(); //兑吧订单号
	    String type=params.getType();//获取兑换类型
	    int facePrice = params.getFacePrice(); //兑换商品的市场价值，单位是分，请自行转换单位
	    int actualPrice = params.getActualPrice(); //此次兑换实际扣除开发者账户费用，单位为分
	    String ip = params.getIp(); //用户ip，不保证获取到
	    boolean waitAudit = params.isWaitAudit(); //是否需要审核(如需在自身系统进行审核处理，请记录下此信息)
	    //详情参数，不同的类型，返回不同的内容，中间用英文冒号分隔。(支付宝类型带中文，请用utf-8进行解码) 实物商品：返回收货信息(姓名:手机号:省份:城市:区域:详细地址)、支付宝：返回账号信息(支付宝账号:实名)、话费：返回手机号、QB：返回QQ号
	    String params1 = params.getParams(); //参数，根据不同的type，有不同的含义
	    //String alipay=params.getAlipay();//获取支付宝账号
	    JSONObject obj = new JSONObject();
	    DocumentManager docManager = DocumentManagerFactory.getInstance();
		//DocLib docLib_mb = InfoHelper.getLib(Constant.DOCTYPE_MEMBER,tenantCode);  //会员表
		String condition_mb = "SYS_DELETEFLAG = 0 and SYS_DOCUMENTID = ?";
		String[] column_mb = {"mScore","m_siteID"};
		//Document[] docs_mb = docManager.find(docLib_mb.getDocLibID(), condition_mb, new Object[]{uid},column_mb);
		DocLib docLib_mb = LibHelper.getLib(DocTypes.DOCTYPE_MEMBER.typeID(),"xy");
		Document[] docs_mb = docManager.find(docLib_mb.getDocLibID(),condition_mb,new Object[] {uid},column_mb);
		
		if(docs_mb.length == 0 || docs_mb == null){
			obj.put("code", "0");
			obj.put("msg", "兑换失败：非系统注册用户");
			obj.put("remNum", 0);
			return obj;
		}
		
		long mScore = docs_mb[0].getLong("mScore");  //会员积分
		int m_siteID = docs_mb[0].getInt("m_siteID");  //站点id
		if(credits < 0 || mScore < 0){
			obj.put("code", "0");
			obj.put("msg", "兑换失败：积分有异常");
			obj.put("remNum", mScore);
			return obj;
		}
		if(credits > mScore){
			obj.put("code", "0");
			obj.put("msg", "兑换失败：积分不足");
			obj.put("remNum", mScore);
			return obj;
		}
		
		//DocLib docLib_dbcr = InfoHelper.getLib(Constant.DOCTYPE_CONSUMERECORD, tenantCode);  //兑吧消费记录表
	    String condition_dbcr = "SYS_DELETEFLAG = 0 and dbOrderNum = ?";
		//Document[] docs_dbcr = docManager.find(docLib_dbcr.getDocLibID(), condition_dbcr, new Object[]{orderNum});
		DocLib docLib_dbcr = LibHelper.getLib(DocTypes.DOCTYPE_MEMBERCONSUMERECORD.typeID(),"xy");
		Document[] docs_dbcr = docManager.find(docLib_dbcr.getDocLibID(),condition_dbcr,new Object[] {orderNum});
		
		if(docs_dbcr.length > 0 && docs_dbcr != null){
			obj.put("code", "0");
			obj.put("msg", "兑换失败：该订单号已经被创建了");
			obj.put("remNum", mScore);
			return obj;
		}
		
		//更新会员表中的积分值
		long remNum = mScore - credits;  //会员积分余额
		docs_mb[0].set("mScore", remNum);
		docManager.save(docs_mb[0]);
		
		if(docs_dbcr.length == 0 || docs_dbcr == null){  //在兑吧消费表中，新建一条记录
			Document doc_dbcr = docManager.newDocument(docLib_dbcr.getDocLibID(), InfoHelper.getID(docLib_dbcr.getDocTypeID()));
			doc_dbcr.setFolderID(docLib_dbcr.getFolderID());
			doc_dbcr.setDeleteFlag(0);
			doc_dbcr.set("dbUid", uid);
			doc_dbcr.set("dbCredits", credits);
			doc_dbcr.set("dbAppKey", app_Key);
			doc_dbcr.set("dbOrderTime", orderTime);
			doc_dbcr.set("dbDescription", description);
			doc_dbcr.set("dbOrderNum", orderNum);
			doc_dbcr.set("dbType", type);
			doc_dbcr.set("dbFacePrice", facePrice);
			doc_dbcr.set("dbActualPrice", actualPrice);
			doc_dbcr.set("dbIp", ip);
			doc_dbcr.set("dbWaitAudit", waitAudit);
			doc_dbcr.set("dbParams", params1);
			doc_dbcr.set("m_siteID", m_siteID);
			
			//订单号生成规则：当前时间(yyyyMMddHHmmss) + 用户id
			String xtOrderNum = DateHelper.getSysTime() + uid;
			doc_dbcr.set("xtOrderNum", xtOrderNum);  //本系统生成的订单号，需要返回给兑吧
			doc_dbcr.set("dbCreditsBefore", mScore);  //用户积分余额（兑换前）
			doc_dbcr.set("dbCreditsAfter", remNum); //用户积分余额（兑换后）
			docManager.save(doc_dbcr);
			
			obj.put("xtOrderNum", xtOrderNum);  //系统订单号
			obj.put("remNum", remNum);  //用户积分余额
			obj.put("code", "1");
			obj.put("msg", "兑换成功");
		}
		
	    return obj;
	}
	
	public String saveOrderStatus(CreditNotifyParams params,String tenantCode) throws E5Exception{
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		String orderNum=params.getOrderNum();
		//DocLib docLib_dbcr = InfoHelper.getLib(Constant.DOCTYPE_CONSUMERECORD, "xy");  //兑吧消费记录表
	    String condition_dbcr = "SYS_DELETEFLAG = 0 and dbOrderNum = ?";
		//Document[] docs_dbcr = docManager.find(docLib_dbcr.getDocLibID(), condition_dbcr, new Object[]{orderNum});
	    DocLib docLib_dbcr = LibHelper.getLib(DocTypes.DOCTYPE_MEMBERCONSUMERECORD.typeID(),"xy");
		Document[] docs_dbcr = docManager.find(docLib_dbcr.getDocLibID(),condition_dbcr,new Object[] {orderNum});
		if(docs_dbcr.length == 0 || docs_dbcr == null){
			//return "fail: Order does not exist";
			log.info("订单不存在，订单号orderNum="+orderNum);
			return "ok";
		}
		String result = "";
	    if(params.isSuccess()){  //兑换订单交易成功
	        //兑换成功，将订单的状态修改为1，并返回ok字符串
			docs_dbcr[0].set("dbOrderStatus", "成功");
			docs_dbcr[0].set("dbOrderIsHandle", "已处理");
			docManager.save(docs_dbcr[0]);
			log.info("订单交易成功！");
			result = "ok";
	    }else{  //兑吧订单交易失败，用户积分需要回滚
	    	String dbOrderIsHandle = docs_dbcr[0].getString("dbOrderIsHandle");  //订单是否已经被处理过
	    	//兑换失败，根据orderNum，对用户的金币进行返还，回滚操作
	    	String dbUid = docs_dbcr[0].getString("dbUid");
	    	long dbCredits = docs_dbcr[0].getLong("dbCredits");  //兑换该商品扣除的积分
	    	long dbCreditsBefore = docs_dbcr[0].getLong("dbCreditsBefore");  //兑换前该用户拥有的积分
	    	//更新会员表
	    	DocLib docLib_mb = InfoHelper.getLib(Constant.DOCTYPE_MEMBER,tenantCode);  //会员表
			String condition_mb = "SYS_DELETEFLAG = 0 and SYS_DOCUMENTID = ?";
			Document[] docs_mb = docManager.find(docLib_mb.getDocLibID(), condition_mb, new Object[]{dbUid});
			if(docs_mb.length == 0 || docs_mb == null){
				//return "fail: user does not exist";
				log.info("该订单的用户不存在，dbUid="+dbUid);
				return "ok";
			}
	    	if("已处理".equals(dbOrderIsHandle)){  //已经处理的订单，则不做任何处理，直接返回错误提示
	    		//result = "fail: Order is handle";
	    		log.info("该订单已经被处理了");
	    		return "ok";
	    	}else if("未处理".equals(dbOrderIsHandle) || StringUtils.isBlank(dbOrderIsHandle)){
	    		long mScore = docs_mb[0].getLong("mScore") + dbCredits;  //用户积分余额回滚：用户现有积分 + 扣除积分
	    		log.info("订单交易失败：订单处理前用户余额：dbCreditsBefore="+dbCreditsBefore+"&回滚前用户余额：mScoreBefore="+docs_mb[0].getLong("mScore")+"&回滚后用户余额：mScore="+mScore);
	    		/* 因有些积分消费 状态 需要时间才能确定，为避免发生不能回滚的现象，所以取消判断
	    		  if(mScore != dbCreditsBefore){  //回滚后的积分如果与兑换前的积分不一致，则直接返回错误提示
	    			return "fail: credits rollback exception";
	    		}*/
				docs_mb[0].set("mScore", mScore);
				docManager.save(docs_mb[0]);
				//更新兑吧消费记录表
		    	docs_dbcr[0].set("dbCreditsAfter", mScore);  //将记录中的兑换后的积分修改为兑换前的积分
		    	docs_dbcr[0].set("dbOrderStatus", "失败");
		    	docs_dbcr[0].set("dbOrderIsHandle", "已处理");
		    	docManager.save(docs_dbcr[0]);
				//result = "success: User credits rollback";
		    	log.info("用户积分回滚成功！mScore="+mScore);
		    	result = "ok";
	    	}
	    }
		return result;
	}
}
