package com.founder.xy.redis;


public class RedisKey {
	//--------------- 发布服务中使用 ---------------------------
	/** 模板的文件，hash类型，《templateID, file》 */
	public static final String TEMPLATE_FILE_KEY = "template.file";
	/** 模板的组件实例，hash类型，《templateID, 模板组件ID字符串》 */
	public static final String TEMPLATE_CO_KEY = "template.co";
	/** 区块的组件实例，hash类型，《blockID, 模板组件ID字符串》 */
	public static final String BLOCK_CO_KEY = "block.co";
	
	/** 栏目更新时间，hash类型，《columnID, 最后变化时间》 */
	public static final String COLUMNS_TIME_KEY = "columns.time";
	
	/** 组件实例的属性，hash类型，《组件实例ID, 属性》  */
	public static final String CO_KEY = "co";
	
	/** "组件实例-稿件列表"的Key前缀，后接.<当前页数>.<组件实例ID>.<指定栏目ID的串> */
	public static final String CO_ARTICLELIST_KEY = "co.articlelist";
	
	/** 记录专题稿件的相关栏目ID的Hash，hash类型，《专题稿ID, 相关栏目、过期时间等属性》*/
	public static final String SPECIAL_ARTICLES = "special.articles";

	//---------------====== App接口中使用 ======---------------------------
	/** 栏目列表的key前缀，后接siteN.colID，因为可能colID=0所以要有siteID区分 */
	public static final String APP_COLLIST_KEY = "app.collist.";
	/** 栏目的子孙栏目列表的key前缀，后接siteN.colID，因为可能colID=0所以要有siteID区分 */
	public static final String APP_COLLISTALL_KEY = "app.collistall.";
	/** 栏目的key前缀，后接colID */
	public static final String APP_COL_KEY = "app.col.";
		
	/** 站点参数前缀，后接siteID，如site.config.1 */
	public static final String SITE_CONF_KEY = "site.config.";
	/** 站点评论参数前缀，后接siteID，如site.config.discuss.1 */
	public static final String SITE_CONFIG_DISCUSS_KEY = "site.config.discuss.";
	
	/** 获取启动页的key前缀，后接appID */
	public static final String APP_START_KEY = "app.start.";
	/** 移动应用key前缀，后接appKey.渠道*/
	public static final String APP_MOBILEAPP = "app.mobileapp.";

	/** 获取分类的key前缀，后接siteN.catCode */
	public static final String APP_CATS_KEY = "app.cats.";
	
	/** 上传存储路径 */
	public static final String APP_UPLOAD_PATH = "app.upload.pathinfo";
	
	/** 登录Token和ID的映射关系，Hash，《token, userID》 */
	public static final String APP_TOKEN_USER = "app.token.user";
	/** 登录Token和ID的映射关系，Hash，《userID, token》 */
	public static final String APP_USER_TOKEN = "app.user.token";
	/** 稿件的关键词的key前缀，后接稿件ID*/
	public static final String APP_ARTICLE_WORDS_KEY="app.words.";

	//------------------稿件--------------------
	/** 稿件列表的key前缀，后接colID.start */
	public static final String APP_ARTICLELIST_AD_KEY = "app.alist.";
	/** 微信稿件列表的key前缀，后接colID.start */
	public static final String APP_ARTICLELIST_AD_WX_KEY = "app.wx.alist.";
	
	/** 给稿件列表用的一个稿件的Key前缀，后接articleID */
	public static final String APP_ARTICLELIST_ONE_KEY = "app.alist.one.";
	/** 最热稿件列表，后接siteID（>1时）、类型（24小时/48小时）*/
	public static final String APP_ARTICLELIST_HOT_KEY = "app.alist.hot.";
	/** 子栏目稿件列表Key，后接栏目ID、类型 */
	public static final String APP_ARTICLELIST_SUBCOLUMN_KEY = "app.alist.subcolumn.";
	
	/** 稿件的key前缀，后接稿件ID */
	public static final String APP_ARTICLE_KEY = "app.article.";

	/**稿件行业分类点击数*/
	public static final String NIS_EVENT_CLICK_TRADE = "nis.event.click.trade";
	
	//------------------直播--------------------
	/** 
	 * 一个直播的报道列表的key前缀，后接直播话题ID和页数。
	 * 页数为0时，包含main和list两个属性；page>0时包含list一个属性
	 */
	public static final String APP_LIVELIST_KEY = "app.livelist.";
	
	/** 直播话题的key前缀，后接直播话题ID */
	public static final String APP_LIVE_MAIN_KEY = "app.live.main.";
	/** 直播话题的报道列表的key前缀，后接直播话题ID和page */
	public static final String APP_LIVE_SUBLIST_KEY = "app.live.list.";
	/** 直播预告的key，站点ID>1时后需接站点ID */
	public static final String APP_LIVE_COMING_KEY = "app.live.coming";
	/** 直播流的状态的key，后接streamID */
	public static final String APP_LIVE_STATUS_KEY = "app.live.status.";
	
	//------------------互动话题（问吧）--------------------
	/** 话题列表的key前缀，后接siteID（>1时）和页数 */
	public static final String APP_SUBJECT_LIST_KEY = "app.subject.list.";
	/** 话题按分类的列表的key前缀，后接分类ID和页数 */
	public static final String APP_SUBJECT_CAT_KEY = "app.subject.cat.";
	/** 单个话题的key前缀，后接话题ID */
	public static final String APP_SUBJECT_KEY = "app.subject.";
	/** 话题的问题回答列表的key前缀，后接话题ID和页数 */
	public static final String APP_SUBJECT_QALIST_KEY = "app.subject.qalist.";
	/** 话题的问题回答详情的key前缀，后接话题问答ID */
	public static final String APP_SUBJECT_QA_KEY = "app.subject.qa.";
	/** 话题的最热问题的key前缀，后接话题ID和页数 */
	public static final String APP_SUBJECT_HOT_KEY = "app.subject.qa.hot.";
	
	//“我的”话题系列的Key，见后面“我的”部分
	
	//------------------互动问答（问政）--------------------
	/** 问答列表的key，后接siteID（>1时）、分类ID（无分类时为-1）、页数 。加siteID是因为无分类时要做区分*/
	public static final String APP_QALIST_KEY = "app.qa.list.";
	/** 问答详情的key，后接问答ID */
	public static final String APP_QA_KEY = "app.qa.";
	
	//------------------活动--------------------
	/** 活动列表的key，后接siteID（>1时）和start，这是长列表 */
	public static final String APP_ACTIVITY_KEY = "app.activity.list.";
	/** 活动详情的key，后接活动ID */
	public static final String APP_ACTIVITY_DETAIL_KEY = "app.activity.";
	/** 活动报名名单列表，后接活动ID */
	public static final String APP_ACTIVITY_ENTRY = "app.activity.entry.";
	/** 活动报名数key前缀，后接活动ID*/
	public static final String APP_ACTIVITY_ENTRYNUM = "app.activity.count.";
	//------------------爆料--------------------
	/** 爆料详情的key，后接爆料ID */
	public static final String APP_TIPOFF_KEY = "app.tipoff.";
	
	//------------------评论--------------------
	/** 热门评论的key前缀，后接来源类型（0稿件1直播等）、稿件ID */
	public static final String APP_DISCUSS_HOT_KEY = "app.discuss.hot.";
	/** 最新评论的key前缀，后接来源类型（0稿件1直播等）、稿件ID、页数 */
	public static final String APP_DISCUSS_VIEW_KEY = "app.discuss.view.";
	/** 评论的最新评论的key前缀，后接评论ID、页数 */
	public static final String APP_DISCUSS_REPLY_KEY = "app.discuss.reply.";
	
	/** 禁言的用户集合key */
	public static final String APP_NIS_SHUTUP_USER_KEY = "nis.shutup.user";
	/** 禁言的IP集合key */
	public static final String APP_NIS_SHUTUP_IP_KEY = "nis.shutup.ip";
	/** 白名单用户集合key */
	public static final String APP_NIS_WHITELIST_KEY = "nis.whitelist";
	
	//------------------投票--------------------
	/** 已投票人数的key前缀，后接投票ID */
	public static final String APP_VOTE_COUNT_KEY = "app.vote.count.";
	/** 投票结果的key前缀，后接投票ID */
	public static final String APP_VOTE_RESULT_KEY = "app.vote.result.";
	
	//------------------领导人（人物）--------------------
	/** 领导人列表key前缀，后接siteN.start */
	public static final String APP_LEADERLIST_KEY = "app.leaderlist.";
	/** 地区领导人列表key前缀，后接siteN.regionID */
	public static final String APP_LEADERLIST_REGION_KEY = "app.leaderlist.region.";
	/** 领导人key前缀，后接领导人ID */
	public static final String APP_LEADER_KEY = "app.leader.";
	
	//------------------记者--------------------
	/**记者文章数、粉丝数key前缀，后接记者ID*/
	public static final String APP_AUTHOR_COUNT_KEY = "app.author.count.";
	/**记者稿件列表key前缀，后接记者ID*/
	public static final String APP_AUTHOR_ARTICLES_KEY = "app.author.articles.";
	/**记者粉丝集合的key前缀，后接记者ID， set类型*/
	public static final String APP_AUTHOR_FANS_KEY = "app.author.fans.";
	
	//------------------数字报--------------------
	/**数字报纸key前缀，后接siteID*/
	public static final String APP_PAPER_KEY = "app.paper.papers.";
	/**数字报报纸日期key前缀，后接报纸ID*/
	public static final String APP_PAPER_DATE_KEY = "app.paper.dates.";
	/**数字报报纸期次key前缀，后接报纸ID.日期*/
	public static final String APP_PAPER_LAYOUT_KEY = "app.paper.layouts.";
	/**数字报稿件列表key前缀，后接版次ID*/
	public static final String APP_PAPER_ARTICLELIST_KEY = "app.paper.articles.";
	/**数字报稿件key前缀，后接稿件ID*/
	public static final String APP_PAPER_ARTICLE_KEY = "app.paper.article.";
	
	//---------互动事件-----------
	/** 提交了事件（点击/分享/点赞等）的稿件ID集合 */
	public static final String NIS_EVENT_INDEX_ARTICLES = "nis.event.index.articles";
	/** 提交了事件（点击/分享/点赞等）的直播ID集合 */
	public static final String NIS_EVENT_INDEX_LIVES = "nis.event.index.lives";
	/** 提交了事件（回复/点赞等）的评论ID集合 */
	public static final String NIS_EVENT_INDEX_DISCUSS = "nis.event.index.discuss";
	/** 提交了事件（点击/分享/点赞等）的数字报稿件ID集合 */
	public static final String NIS_EVENT_INDEX_PAPERARTICLE = "nis.event.index.paperarticle";
	/** 提交了事件（点击/分享/点赞等）的活动ID集合 */
	public static final String NIS_EVENT_INDEX_ACTIVITY = "nis.event.index.activity";
	/** 提交了事件（点击/分享/点赞等）的互动话题问答ID集合 */
	public static final String NIS_EVENT_INDEX_SUBJECTQA = "nis.event.index.subjectqa";
	/** 提交了事件（点击/分享/点赞等）的互动问答ID集合 */
	public static final String NIS_EVENT_INDEX_QA = "nis.event.index.qa";
	
	/** 稿件事件数前缀 */
	public static final String NIS_EVENT_ARTICLE = "evt.a.";
	/** 直播事件数前缀 */
	public static final String NIS_EVENT_LIVE = "evt.live.";
	/** 评论事件数前缀 */
	public static final String NIS_EVENT_DISCUSS = "evt.discuss.";
	/** 数字报稿件事件数前缀 */
	public static final String NIS_EVENT_PAPERARTICLE = "evt.pa.";
	/** 活动事件数前缀 */
	public static final String NIS_EVENT_ACTIVITY = "evt.act.";
	/** 互动话题问答事件数前缀 */
	public static final String NIS_EVENT_SUBJECTQA = "evt.sqa.";
	/** 互动问答事件数前缀 */
	public static final String NIS_EVENT_QA = "evt.qa.";

	/** 稿件按小时统计的点击数前缀，后面+hour */
	public static final String NIS_EVENT_ARTICLE_CLICK_HOUR = "nis.event.article.click.hour.";
	/** 稿件按天统计的点击数前缀，后面+day */
	public static final String NIS_EVENT_ARTICLE_CLICK_DAY = "nis.event.article.click.day.";
	/** 稿件按小时统计的评论数前缀，后面+hour */
	public static final String NIS_EVENT_ARTICLE_DISCUSS_HOUR = "nis.event.article.discuss.hour.";
	/** 稿件按天统计的评论数前缀，后面+day */
	public static final String NIS_EVENT_ARTICLE_DISCUSS_DAY = "nis.event.article.discuss.day.";
	/** 稿件按小时统计的分享数前缀，后面+hour */
	public static final String NIS_EVENT_ARTICLE_SHARE_HOUR = "nis.event.article.share.hour.";
	/** 稿件按天统计的分享数前缀，后面+day */
	public static final String NIS_EVENT_ARTICLE_SHARE_DAY = "nis.event.article.share.day.";

	/** 栏目按小时统计的点击数前缀，后面+hour */
	public static final String NIS_EVENT_COLUMN_CLICK_HOUR = "nis.event.column.click.hour.";
	/** 栏目按天统计的点击数前缀，后面+day */
	public static final String NIS_EVENT_COLUMN_CLICK_DAY = "nis.event.column.click.day.";
	/** 栏目按小时统计的订阅数前缀，后面+hour */
	public static final String NIS_EVENT_COLUMN_SUBSCRIBE_HOUR = "nis.event.column.sub.hour.";
	/** 栏目按天统计的订阅数前缀，后面+day */
	public static final String NIS_EVENT_COLUMN_SUBSCRIBE_DAY = "nis.event.column.sub.day.";
	/** 互动计数回写时间（long型），在回写任务中填写，在统计界面显示 */
	public static final String NIS_EVENT_WRITEBACK_TIME = "nis.event.writeback.time";
	
	//------------------首页推荐模块--------------------
	/** 推荐模块项，记录一个模块的列表，后接模块ID*/
	public static final String APP_MODULEITEM_LIST = "app.column.module.";
	/** 推荐模块，记录一个栏目的推荐模块的属性数组，后接栏目ID*/
	public static final String APP_MODULE_LIST = "app.column.modules.";

	/** 问吧话题关注数key，hash，《话题ID，关注数》*/
	public static final String NIS_EVENT_SUBCRIBE_SUBJECT = "nis.event.subcribe.subject";
	/**栏目订阅数Key，hash，《栏目ID，订阅数》 */
	public static final String NIS_EVENT_SUBSCRIBE_COLUMN = "nis.event.subscribe.col";
	/**用户的智能推荐列表Key，后接userID */
	public static final String NIS_EVENT_RECOMMEND = "nis.recommend.";

	//-------------------广告，目前未实际使用-------------------------
	/** 启动页广告 */
	public static final String ADV_STARTUP_KEY = "adv.startup";
	/** 栏目页广告key前缀，后接栏目ID */
	public static final String ADV_COLUMN_KEY = "adv.column.";
	/** 栏目页列表广告key前缀，后接栏目ID */
	public static final String ADV_COLUMN_LIST_KEY = "adv.column.list.";
	/** 文章页广告key前缀 */
	public static final String ADV_PAGE_KEY = "adv.page.";
	/** 组图页广告key前缀 */
	public static final String ADV_PAGE_ALBUM_KEY = "adv.page.album.";
	/** 广告详情key前缀 */
	public static final String ADV_INFO_KEY = "adv.info.";
	
	//--------后加的活动、小红点等----
	/** 小红点功能：被回复的评论人ID集合的Key */
	public static final String RED_DOT_DISCUSS = "red.dot.discuss";
	/** 小红点功能：被回复的话题提问人ID集合的Key */
	public static final String RED_DOT_SUBJECTQA = "red.dot.subjectqa";
	/** 小红点功能：被回复的问答提问人ID集合的Key */
	public static final String RED_DOT_QA = "red.dot.qa";
	/** 小红点功能：已公布名单的活动报名人ID集合的Key */
	public static final String RED_DOT_ACTIVITY = "red.dot.activity";
	/** 小红点功能：栏目最新发布时间的Key，Hash，《栏目ID，最新发布时间》 */
	public static final String RED_DOT_ARTICLE = "red.dot.article";
	/** 小红点功能：系统消息的最新发布时间（long值）的Key */
	public static final String RED_DOT_MESSAGE = "red.dot.message";

	/** 系统消息列表，后接siteID.page */
	public static final String APP_MESSAGE_LIST_KEY = "app.message.list.";
	
	//-----------延迟入库的互动列表数据-----------------
	/** 延迟入库的评论列表，列表类型，固定长度2000*/
	public static final String APP_DELAY_DISCUSS_KEY = "app.delay.discuss";
	/** 延迟入库的话题提问列表，列表类型，固定长度200*/
	public static final String APP_DELAY_SUBJECTQA_KEY = "app.delay.subjectqa";
	/** 延迟入库的回答提问列表，列表类型，固定长度200*/
	public static final String APP_DELAY_QA_KEY = "app.delay.qa";
	/** 延迟入库的收藏列表，列表类型，固定长度1000*/
	public static final String APP_DELAY_FAVORITE_KEY = "app.delay.favorite";
	/** 延迟入库的举报列表，列表类型，固定长度100*/
	public static final String APP_DELAY_EXPOSE_KEY = "app.delay.expose";
	/** 延迟入库的活动报名列表，列表类型，固定长度100*/
	public static final String APP_DELAY_ENTRY_KEY = "app.delay.entry";
	/** 延迟入库的意见反馈列表，列表类型，固定长度100*/
	public static final String APP_DELAY_FEEDBACK_KEY = "app.delay.feedback";
	/** 延迟入库的报料列表，列表类型，固定长度100*/
	public static final String APP_DELAY_TIPOFF_KEY = "app.delay.tipoff";

	//--------------------我的-----------------------------
	/**我的评论，后接userID */
	public static final String MY_DISCUSS_KEY = "my.dis.";
	/**评论我的，后接userID*/
	public static final String MY_REPLY_KEY = "my.reply.";
	/**我的收藏，后接userID */
	public static final String MY_FAVORITE_KEY = "my.fav.";

	/**我的爆料，后接站点id大于1加站点id,userID */
	public static final String MY_TIPOFF_KEY = "my.tipoff.";

	/** 我的提问（话题），后接userID.page */
	public static final String MY_SUBJICTQA_KEY = "my.sub.qa.";
	/** 我的话题（发起）的key前缀，后接userID.page */
	public static final String MY_SUBJECT_KEY = "my.sub.";
	/** 我的话题（关注）的key前缀，后接userID.page */
	public static final String MY_SUBJECT_SUBS_KEY = "my.sub.subs.";
	/** 我关注的话题IDs的key前缀，后接userID*/
	public static final String MY_SUBJECT_SUBIDS_KEY="my.sub.subids.";
	
	/**我的问题（问答），后接userID.page*/
	public static final String MY_QA_KEY = "my.qa.";
	
	/**我报名的活动，后接userID.page*/
	public static final String MY_ENTRY_KEY = "my.entry.";

	/**我关注的记者，后接userID.page*/
	public static final String MY_AUTHOR_KEY = "my.author.";
	
	/**我订阅的栏目ID，hash类型，存放《userID，订阅栏目ID列表》*/
	public static final String MY_COLUMN_KEY = "my.col";

	//----------------------------会员中心投票相关------------------------
	/**  记录该投票访问数量的key前缀，后接voteId  */
	public static final String  VOTE_ACCESSCOUNT="vote.accesscount.";
	/** 实名投票记录有没投过票 前缀，后接voteId */
	public static final String  VOTE_USERID="vote.userid.";
	/**记录投票的人数，后接voteId */
	public static final String  VOTE_PERSONCOUNT="vote.personcount.";
	/** 匿名投票记录投票IP，后接voteId */
	public static final String  VOTE_IP="vote.ip.";
	/** 该投票有多少人投，后接voteId */
	public static final String  VOTE_VOTECOUNT="vote.voteCount.";
	/** 该选项有多少人投，后接voteId,optionId */
	public static final String  VOTE_OPTIONCOUNT="vote.optionCount.";
	/** 投该选项的用户id等信息，后接voteId,optionId */
	public static final String  VOTE_OPTION_PERSONINFO="vote.option.personinfo.";
	/** 该用户在该投票下选的选项ID，后接voteId,optionId */
	public static final String  VOTE_PERSON_OPTIONID="vote.persion.optionid.";
	/** 取出当前IP投过的选项,后接voteid、ipAddr*/
	public static final String  VOTE_IP_OPTIONID="amuc.vote.";
	/** 投票日志,后接logID*/
	public static final String  VOTE_LOG="voteLog:logid.";
	/** 记录收集的用户信息,后接voteID*/
	public static final String  VOTE_USERINFO="amuc.vote.userinfo.";
	/** 投票配置信息,后接voteID*/
	public static final String  VOTE_VOTEINFO="amuc.vote.voteInfo";

	/** 第三方系统信息,hash类型，<外部系统ID，外部系统密码> */
	public static final String EXTERNAL_KEY = "external";

	/** 移动采编app扫码登录信息,hash类型，<用户名，token> */
	public static final String APP_USERCODE_TOKEN = "app.usercode.token";

	/**热门话题的ID，后接siteID */
	public static final String APP_HOT_TOPICS_KEY = "app.hot.topics.";
	/**话题按组的ID，后接siteID */
	public static final String APP_TOPICSBYGROUP_KEY = "app.topicsbygroup.";

	/** 最热APP稿件列表，后接siteID（>1时）、查询稿件类型（新闻/百格视频）、查询时间类型（6小时/24小时/3天）、排序类型（点击量降序/留言数降序/分享数降序） */
	public static final String APP_NEW_HOT_ARTICLELIST_KEY = "app.alist.new.hot.";

	/** 最热WEB稿件列表，后接siteID（>1时）、查询稿件类型（新闻/百格视频）、查询时间类型（6小时/24小时/3天）、排序类型（点击量降序/留言数降序/分享数降序） */
	public static final String WEB_NEW_HOT_ARTICLELIST_KEY = "web.alist.new.hot.";

	/**子栏目第一篇稿件列表 */
	public static final String APP_ARTICLELIST_SUBCOL_KEY = "app.alist.subcol.";
	/** 话题稿件列表的key前缀，后接siteID.channel.topicID.type.start */
	public static final String APP_ARTICLELIST_TOPIC_KEY = "app.alist.topics.";
	/** 给稿件列表用的一个稿件的Key前缀，后接articleID */
	public static final String WEB_ARTICLELIST_ONE_KEY = "web.alist.one.";
	/**网站话题列表的ID，后接siteID */
	public static final String WEB_TOPICS_KEY = "web.topics.";
	/**网站稿件话题列表的ID，后接channel.articleID */
	public static final String ARTICLE_TOPICS_KEY = "article.topics.";

    /** 栏目的子孙栏目列表的key前缀，后接siteid.colParentID，因为可能colID=0所以要有siteID区分 */
    public static final String WEB_COLLISTALL_KEY = "web.collistall.";

    /**网站稿件话题列表的ID，后接channel.articleID.groupID */
    public static final String ARTICLE_TOPICSGROUP_KEY = "article.topicsbygroup.";

    /**网站话题列表的ID，后接siteID groupID */
    public static final String WEB_TOPICSBYGROUP_KEY = "web.topicsbygroup.";

}
