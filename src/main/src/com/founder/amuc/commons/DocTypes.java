package com.founder.amuc.commons;

import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.dom.DocType;
import com.founder.e5.dom.DocTypeReader;

/**
 * 常量定义：文档类型名称。<br/>
 * 每个常量有三个方法：<br/>
 * DocTypes.DOCTYPE_LISTING.typeName();<br/>
 * DocTypes.DOCTYPE_LISTING.typeID();<br/>
 * DocTypes.DOCTYPE_LISTING.type();<br/><br/>
 * 
 * 可以用如下方式得到常量：<br/>
 * DocTypes docType = Enum.valueOf(DocTypes.class, "DOCTYPE_" + code);<br/><br/>
 * 
 * 引用时，可以使用静态导入：<br/>
 * import static com.founder.sfa.constant.DocTypes.*;<br/>
 * 则程序内可以直接写：<br/>
 * someMethod(DOCTYPE_LISTING, otherVars)<br/><br/>
 * 
 * @author Gong Lijie
 * 2011-11-9
 */
public enum DocTypes {
	
	DOCTYPE_CONTACT("联系人"),
	DOCTYPE_COMMUNITY("社区定义"),
	DOCTYPE_ACCOUNT("账号定义"),
	DOCTYPE_MSGASSORT("消息模板管理"),
	DOCTYPE_MENU("菜单管理"),
	DOCTYPE_MESSAGE("消息"),
	DOCTYPE_MESSAGELOG("消息日志"),
	
	DOCTYPE_CUSTOMER("微信关注客户"),
	DOCTYPE_SIGNIN("签到记录"),
	DOCTYPE_TIPOFF("爆料"),
	DOCTYPE_MSG("消息提醒"),
	DOCTYPE_ADV("广告"),
	DOCTYPE_STAT("数据统计"),
	DOCTYPE_GAME("游戏管理"),
	DOCTYPE_MSGATT("消息附件"),
	DOCTYPE_QSTOCK("题库管理"),
	DOCTYPE_VOTE("投票"),
	DOCTYPE_VOTERECORT("投票记录"),
	DOCTYPE_GAMERESULT("比赛结果"),
	DOCTYPE_COMPETITION("赛程"),
	DOCTYPE_GIVING("微信活动"),
	DOCTYPE_GIVINGGOODS("微信活动礼品"),
	DOCTYPE_GIVINGLOG("微信活动记录"),
	DOCTYPE_VIPSIGNIN("会员签到"),
	DOCTYPE_GOODS("兑换礼品"),
	DOCTYPE_GOODSRECORD("礼品兑换记录"),
	DOCTYPE_COUPON("优惠券"),
	DOCTYPE_SHOPINFO("优惠券门店"),
	DOCTYPE_COUPONLOG("优惠券领取兑换记录"),
	DOCTYPE_REDPACKMANAGER("微信商户管理"),
	DOCTYPE_MEMBERCONSUMERECORD("兑吧消费记录"),
	DOCTYPE_MEMBER("会员")
	;
	
	private String typeName;
	private DocTypes(String typeName) {
		this.typeName = typeName;
	}
	public String typeName() {
		return this.typeName;
	}
	public DocType type() {
		DocType docType = null;
		DocTypeReader docTypeReader = (DocTypeReader) Context.getBean(DocTypeReader.class);
		try {
			docType = docTypeReader.get(typeName());
		} catch (E5Exception e) {
			e.printStackTrace();
		}
		return docType;
	}
	public int typeID() {
		return type().getDocTypeID();
	}
}