package com.founder.xy.commons;

import com.founder.e5.commons.DomHelper;
import com.founder.e5.dom.DocType;

/**
 * 文档类型的常量定义，
 * 定义了文档类型名称、文档类型ID、其表示名称的字段、其表示站点的字段、表示分组的字段。<br/>
 * 
 * 使用方式：<br/>
 * int docTypeID = DocTypes.ARTICLE.typeID()<br/>
 * DocTypes oneType = Enum.valueOf(DocTypes.class, code);<br/>
 * 
 * @author Gong Lijie
 */
public enum DocTypes {
	//不需要查重时，或需要查重但直接使用E5的查重，不需要区分站点：dupField和siteField是空的
	ARTICLE("稿件"),
	ORIGINAL("原稿"),
	ARTICLEEXT("稿件扩展字段"),
	WIDGET("稿件挂件"),
	ATTACHMENT("稿件内容附件"),
	ARTICLEREL("相关稿件"),
	DRAFT("草稿"),
	ARTICLEEXPIRE("稿件顺序时效"),
	PHOTO("图片", "", "", "p_catID"),
	VIDEO("视频", "", "", "v_catID"),
	VIDEOTASK("视频转码任务"),
	HISTORYORI("源稿历史版本"),
	
	SPECIAL("专题", "s_name", "s_siteID", "s_groupID"),
	BLOCKARTICLE("页面区块内容"),
	COMPONENTOBJ("模板组件实例"),
	TENANT("租户"),
	
	SITE("站点"),
	USEREXT("用户", "u_name", "u_siteID", ""),
	USERREL("用户关联信息"),
	MOBILEOS("站点移动平台", "os_name", "os_siteID", ""),
	MOBILEAPP("站点移动应用"),
	MOBILEPACKAGE("移动站点应用包"),
	
	PUSHTASK("消息推送任务"),
	APPLOGIN("客户端登录信息"),

	SELFMEDIA("自媒体", "sm_name", "sm_siteID", ""),
	XYACCOUNT("翔宇号"),
	RSS("自媒体内容源"),
	SMNOTICE("自媒体通知"),

	//互动系统
	FORUM("互动论坛"),
	LIVE("互动直播"),
	NISATTACHMENT("互动附件"),
	TIPOFF("互动报料"),
	DISCUSS("互动评论"),
	PRAISE("互动点赞"),
	VOTE("互动投票", "", "vote_siteID", "vote_groupID"),
	VOTEOPTION("互动投票选项"),
	VOTERESULT("互动投票结果"),
	SHUTUP("互动禁言用户"),
	TOPIC("选题"),
	SUBSCRIBE("选题订阅"),
	FEEDBACK("意见反馈"),
	
	SUBJECT("互动话题", "", "a_siteID", "a_group_ID"),
	SUBJECTQA("互动话题问答"),
	QA("互动问答", "", "a_siteID", "a_group_ID"),
	ACTIVITY("互动活动"),
	ENTRY("互动报名"),
	MESSAGE("系统消息"),
	
	SITERULE("站点发布规则", "rule_name", "rule_siteID", ""),
	DOMAINDIR("站点域名目录", "dir_name", "dir_siteID", ""),
	COLUMN("站点栏目", "col_name", "col_siteID", ""),
	TEMPLATE("模板", "t_name", "t_siteID", "t_groupID"),
	BLOCK("页面区块", "b_name", "b_siteID", "b_groupID"),
	SOURCE("来源", "src_name", "src_siteID", "src_groupID"),
	RESOURCE("公共资源", "res_name", "res_siteID", "res_groupID"),
	EXTFIELD("扩展字段定义", "ext_name", "ext_siteID", "ext_groupID"),
	COLUMNORI("源稿栏目", "col_name", "col_siteID", ""),
	COLUMNTOPIC("话题组", "col_name", "col_siteID", ""),
	TOPICS("话题"),
	
	LEADER("领导人", "l_name", "l_siteID", ""),
	FAN("用户关注"),
	
	//数字报系统
	PAPER("报纸", "pa_name", "pa_siteID", ""),
	PAPERLAYOUT("报纸版面", "", "", ""),
	PAPERARTICLE("报纸稿件", "", "a_siteID", ""),
	PAPERDATE("报纸日期", "", "", ""),
	PAPERATTACHMENT("报纸附件", "", "", ""),

	MAGAZINE("期刊", "pa_name", "pa_siteID", ""),
	MAGAZINECOLUMN("期刊栏目", "", "", ""),
	MAGAZINEARTICLE("期刊稿件", "", "a_siteID", ""),
	MAGAZINEDATE("期刊日期", "", "", ""),
	
	//微信
	WXACCOUNT("微信账号", "wxa_name", "wxa_siteID", ""),
	WXARTICLE("微信稿件"),
	WXGROUP("微信图文"),
	WXMENU("微信菜单"),
	WXGROUPARTICLE("微信图文稿件"),
	
	//微博
	WBACCOUNT("微博账号", "wba_name", "wba_siteID", ""),
	WBARTICLE("微博稿件"),
	
	//通讯员
	BATMAN("通讯员"),
	CORPORATION("单位"),
	
	SENSITIVE("敏感词"),
	TAG("标签", "tag_name", "tag_siteID", "tag_groupID"),
	
	//会员
	MEMBER("会员"),
	MEMBEREVENT("会员行为"),
	MEMBERSCORE("会员积分记录"),
	MEMBERSCOREUNUSUAL("会员异常积分记录"),
	MEMBERSCORERULE("会员积分规则"),
	MEMBERINVITECODE("会员邀请码"),
	MEMBERVOTE("会员投票"),
	MEMBERVOTEOPTION("会员投票"),
	MEMBERVOTEIMAGE("会员投票选项"),
	MEMBERVOTETHEMES("会员投票主题"),
	MEMBERPAPERCARD("会员报卡"),
	MEMBERPAPERCARDLOG("会员报卡日志"),
	PAYLOG("支付交易管理"),
	MEMBERSETMEAL("会员套餐"),
	MEMBERORDERS("会员订单"),
	MEMBERSTATIC("会员统计"),
	MEMBERINVITECODELOG("会员邀请码使用记录"),
	MEMBERINVITECODEID("会员邀请码标识"),
	MEMBERTYPECODE("会员报卡类型"),
	MEMBERCOLLECTION("会员收藏"),
	MEMBERINVITECODETREE("会员邀请码编码树"),
	MEMBERACTIVITY("我的活动"),
	MEMBERLEVEL("会员等级定义"),
	MEMBERPAYCONFIG("支付配置管理"),
    EMAILSUBSCRIBE("邮件订阅"),
	//栏目推荐模块
	COLMODULE("栏目模块"),
	COLMODULEITEM("栏目模块项"),
	
	AD("广告"),
	LIVEITEM("互动直播条目"),
	EXPOSE("互动举报"),
	FAVORITE("互动收藏"),
    NOTICE("通知"),
    NOTICEFEEDBACK("通知反馈"),
	NISTASK("互动转码任务"),
	;

	private String typeName; //文档类型名
	private String dupField; //查重字段
	private String siteField; //站点字段
	private String groupField; //分组字段
	private DocType type;
	
	private DocTypes(String typeName) {
		this.typeName = typeName;
	}
	private DocTypes(String typeName, String dupField, String siteField, String groupField) {
		this.typeName = typeName;
		this.dupField = dupField;
		this.siteField = siteField;
		this.groupField = groupField;
	}
	public String typeName() {
		return this.typeName;
	}
	public String dupField() {
		return this.dupField;
	}
	public String siteField() {
		return this.siteField;
	}
	public String groupField() {
		return this.groupField;
	}
	
	public int typeID() {
		DocType type = type();
		if (type != null)
			return type().getDocTypeID();
		else
			return 0;
	}
	public DocType type() {
		if (type == null)
			type = DomHelper.getDocType(typeName);
		return type;
	}
}
