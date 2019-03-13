package com.founder.xy.config;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.founder.e5.commons.ArrayUtils;
import com.founder.e5.commons.DomHelper;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.E5Exception;
import com.founder.e5.dom.queryForm.QueryForm;
import com.founder.xy.commons.DocTypes;

/**
 * 读配置文件，得到工作界面的菜单项组织
 * 配置文件若有改动，需重启应用服务器。
 * 
 * @author Gong Lijie
 */
@SuppressWarnings("unchecked")
public class ConfigReader {
	private static int[] typeIDs_hide_when_no_web = null; //无web稿件时隐藏的文档类型
	private static int[] typeIDs_hide_when_no_app = null; //无app稿件时隐藏的文档类型
	private static int[] typeIDs_hide_when_no_article = null; //无稿件时隐藏的文档类型
	private static int[] typeIDs_hide_when_no_paper = null; //无数字报时隐藏的文档类型
	private static int[] typeIDs_hide_when_no_magazine = null; //无期刊时隐藏的文档类型
	private static int[] typeIDs_hide_when_no_nis = null; //无互动时隐藏的文档类型
	private static int[] typeIDs_hide_when_no_wbwx = null; //无两微时隐藏的文档类型
	
	private static int[] typeIDs_hide_when_no_paperPay = null; //无数字报收费墙时隐藏的文档类型
	private static int[] typeIDs_hide_when_no_batman = null;
	private static int[] typeIDs_hide_when_no_member = null;
	/**TAB*/
	private static List<Tab> tabList;
	private static Channel[] channels = null;
	
	//-----------对外接口-----------------------------
	
	//读主TAB
	public static List<Tab> getTabs() {
		return tabList;
	}
	public static Channel[] getChannels() {
		return channels;
	}

	/** 只有web渠道。使前端只调用本类 */
	public static boolean onlyWeb() {
		return SecurityHelper.webUsable() && !SecurityHelper.appUsable();
	}
	/** 只有app渠道。使前端只调用本类 */
	public static boolean onlyApp() {
		return SecurityHelper.appUsable() && !SecurityHelper.webUsable();
	}
	//------------以下是读配置文件的过程，一次性-------------
	static {
		String configFile = "xy-template/xy5.0-config.xml";
		
        InputStream in = Thread.currentThread().getContextClassLoader()
			.getResourceAsStream(configFile);
	    if (in == null)
	    	throw new RuntimeException("No configuration file!!" + configFile);
	    
		SAXReader reader = new SAXReader();
		Document doc = null;
	    try {
	    	doc = reader.read(in);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(in);
		}
		
	    init();
		tabList = readTabs(doc, "//tabs/tab");
		channels = readChannels(doc, "//publish-channels/channel");
	}
	private static List<Tab> readTabs(Node doc, String rootName) {
		List<Tab> tabList = new ArrayList<Tab>(6);
		
		List<Element> list = doc.selectNodes(rootName);
		
		for (Element node : list) {
			List<SubTab> children = getSubTabs(node);
			if (children.size() == 0) {
				continue;
			}
			
			Tab tab = new Tab();
			tab.setId(node.attributeValue("id"));
			tab.setName(node.attributeValue("name"));
			tab.setUrl(node.attributeValue("url"));
			tab.setIcon(node.attributeValue("icon"));
			tab.setFree("true".equalsIgnoreCase(node.attributeValue("free")));
			
			tab.setChildren(children);
			
			tabList.add(tab);
		}
		return tabList;
	}
	
	private static List<SubTab> getSubTabs(Node doc) {
		List<Element> list = doc.selectNodes("sub-tab");
		if (list == null || list.size() == 0)
			return null;
		List<SubTab> children = new ArrayList<SubTab>(7);
		for (Element node : list) {
			SubTab tab = new SubTab();
			tab.setId(node.attributeValue("id"));
			
			tab.setName(node.attributeValue("name"));
			tab.setUrl(node.attributeValue("url"));
			tab.setFree("true".equalsIgnoreCase(node.attributeValue("free")));
			tab.setRule(node.attributeValue("rule"));
			tab.setQuery(node.attributeValue("query"));
			tab.setList(node.attributeValue("list"));
			tab.setIcon(node.attributeValue("icon"));
			tab.setExportable("1".equals(node.attributeValue("export")));
			
			//文档类型
			String docTypeCode = node.attributeValue("docType");
			if (!StringUtils.isBlank(docTypeCode)) {
				try {
					int docTypeID = Enum.valueOf(DocTypes.class, docTypeCode).typeID();
					if (docTypeID > 0) {
						tab.setDocTypeID(docTypeID);
						tab.setDocTypeCode(docTypeCode);
						
						fillOthers(tab); //补充列表、查询条件
					} else {
						//若一个tab指定了DOCTYPE，但没查到docTypeID，则系统内没有此文档类型（实际项目），此时不显示该tab
						tab = null;
					}
				} catch (Exception e) {
					tab = null;
				}
			}
			if (tab != null) {
				//判断是否可见
				if (!canShow(tab)) continue;
				
				children.add(tab);
			}
		}
		return children;
	}
	
	private static void fillOthers(SubTab subTab) {
		//读列表方式
		if (!StringUtils.isBlank(subTab.getList())) {
			//列表方式可以指定多个
			String[] lists = subTab.getList().split(",");
			String listIDs = "";
			for (String list : lists) {
				int listID = DomHelper.getListID(subTab.getDocTypeID(), list);
				if (listID > 0) {
					if (listIDs.length() > 0) listIDs += ",";
					listIDs += listID;
				}
			}
			if (listIDs.length() == 0) listIDs = "0";
			subTab.setListID(listIDs);
		} else {
			//domInfo.setListID(DomHelper.getListID(subTab.getDocTypeID()));
			subTab.setListID("0");
		}
		
		//读查询条件，返回查询条件中定义的js引用
		try {
			QueryForm query = null;
			if (!StringUtils.isBlank(subTab.getQuery()))
				query = DomHelper.getQuery(subTab.getDocTypeID(), subTab.getQuery());
			else
				query = DomHelper.getQuery(subTab.getDocTypeID());
			if (query != null) {
				subTab.setQueryID(query.getId());
				subTab.setQueryScripts(StringUtils.split(query.getPathJS(), ","));
			} else {
				subTab.setQueryID(-1); //设置为-1，则不显示
			}
		} catch (E5Exception e) {
			e.printStackTrace();
		}
	}
	
	private static boolean canShow(SubTab tab) {
		//互动模块，检查加密点。无app渠道时，也不显示；无会员时，也不显示
		if ((!SecurityHelper.nisUsable() || !SecurityHelper.appUsable() || !SecurityHelper.memberUsable())
				&& contains(typeIDs_hide_when_no_nis, tab.getDocTypeID())) {
			return false;
		}
		//数字报系统，检查加密点
		if (!SecurityHelper.epaperUsable()
				&& contains(typeIDs_hide_when_no_paper, tab.getDocTypeID())) {
			return false;
		}
		//微博微信，检查加密点
		if (!SecurityHelper.wbwxUsable()
				&& contains(typeIDs_hide_when_no_wbwx, tab.getDocTypeID())) {
			return false;
		}
		
		//无app渠道时的不显示。App发布库也不显示。
		if (!SecurityHelper.appUsable()) {
			if (contains(typeIDs_hide_when_no_app, tab.getDocTypeID())
					|| "capp".equals(tab.getId())) {
				return false;
			}
		}
		//无web渠道，不显示：Web发布库、其它相关文档类型模块（区块内容、页面区块、专题）
		if (!SecurityHelper.webUsable()) {
			if (contains(typeIDs_hide_when_no_web, tab.getDocTypeID())
					|| "cweb".equals(tab.getId())) {
				return false;
			}
		}
		//无web也无app时的不显示。分类管理也不显示。
		if (!SecurityHelper.appUsable() && !SecurityHelper.webUsable()) {
			if (contains(typeIDs_hide_when_no_article, tab.getDocTypeID()))
				return false;
			if ("scat".equals(tab.getId()) && !SecurityHelper.nisUsable())
				return false;
		}
		//会员
		if (!SecurityHelper.memberUsable()
				&& contains(typeIDs_hide_when_no_member, tab.getDocTypeID())) {
			return false;
		}
		//期刊系统，检查加密点
		if (!SecurityHelper.magazineUsable()
				&& contains(typeIDs_hide_when_no_magazine, tab.getDocTypeID())) {
			return false;
		}
		//专题设计模块
		if (!SecurityHelper.specialUsable() 
				&& DocTypes.SPECIAL.typeID() == tab.getDocTypeID()) {
			return false;
		}
		//木疙瘩
		if (!SecurityHelper.mugedaUsable()) {
			if ("myH5".equals(tab.getId())
					|| "h5".equals(tab.getId())) {
				return false;
			}
		}
		//数字报收费墙（前提：有会员、数字报）
		if ((!SecurityHelper.paperPayUsable() || !SecurityHelper.epaperUsable() || !SecurityHelper.memberUsable())
				&& contains(typeIDs_hide_when_no_paperPay, tab.getDocTypeID())) {
			return false;
		}
		//敏感词
		if (!SecurityHelper.sensitiveUsable()
				&& DocTypes.SENSITIVE.typeID() == tab.getDocTypeID()) {
			return false;
		}
		//直播
		if (!SecurityHelper.liveUsable()
				&& DocTypes.LIVE.typeID() == tab.getDocTypeID()) {
			return false;
		}
		//视频
		if (!SecurityHelper.videoUsable()
				&& DocTypes.VIDEO.typeID() == tab.getDocTypeID()) {
			return false;
		}
		//通讯员
		if (!SecurityHelper.batmanUsable()
				&& contains(typeIDs_hide_when_no_batman, tab.getDocTypeID())) {
			return false;
		}
		
		/*
		//领导人模块
		if (!SecurityHelper.leaderUsable() 
				&& contains(new int[]{DocTypes.LEADER.typeID()}, tab.getDocTypeID())) {
			return false;
		}
		//选题模块
		if (!SecurityHelper.topicUsable() 
				&& contains(new int[]{DocTypes.TOPIC.typeID()}, tab.getDocTypeID())) {
			return false;
		}
		*/
		return true;
	}
	private static boolean contains(int[] types, int typeID) {
		if (typeID <= 0) return false;
		
		return ArrayUtils.contains(types, typeID);
	}
	
	private static synchronized void init() {
		if (typeIDs_hide_when_no_web != null) return;
		
		//无web渠道，不显示：Web发布库、区块内容、页面区块、专题
		typeIDs_hide_when_no_web = new int[]{
				DocTypes.BLOCKARTICLE.typeID(),
				DocTypes.BLOCK.typeID(),
				DocTypes.SPECIAL.typeID(),
		};
		
		//无app渠道，不显示：
		//移动平台设置、稿内投票、App发布库、分类管理（前台的分类管理只显示栏目类型、栏目样式、话题分类，都是app的）
		typeIDs_hide_when_no_app = new int[]{
				DocTypes.MOBILEOS.typeID(),
				DocTypes.AD.typeID(),
				DocTypes.VOTE.typeID(),
		};
		
		//无web也无app，则额外不显示的：
		typeIDs_hide_when_no_article = new int[]{
				DocTypes.ORIGINAL.typeID(),
				DocTypes.ARTICLE.typeID(),
				DocTypes.COLUMN.typeID(),
				DocTypes.EXTFIELD.typeID(),
				DocTypes.SOURCE.typeID(),
		};
		
		//无互动加密点
		typeIDs_hide_when_no_nis = new int[]{
				//DocTypes.DISCUSS.typeID(),评论
				//DocTypes.TIPOFF.typeID(), 报料
				//DocTypes.FEEDBACK.typeID(),意见反馈
				//DocTypes.LIVE.typeID(),
				DocTypes.SUBJECT.typeID(), //问吧
				DocTypes.SUBJECTQA.typeID(),
				DocTypes.QA.typeID(), //问政
				DocTypes.ACTIVITY.typeID(), //活动
				DocTypes.MEMBERVOTE.typeID(), //投票
		};
		//无微博微信加密点
		typeIDs_hide_when_no_wbwx = new int[]{
				DocTypes.WXGROUP.typeID(),
				DocTypes.WXARTICLE.typeID(),
				DocTypes.WXACCOUNT.typeID(),
				DocTypes.WBARTICLE.typeID(),
				DocTypes.WBACCOUNT.typeID(),
		};
		
		//无数字报加密点
		typeIDs_hide_when_no_paper = new int[]{
				DocTypes.PAPERLAYOUT.typeID(),
				DocTypes.PAPERARTICLE.typeID(),
				DocTypes.PAPER.typeID(),
		};
		//无期刊加密点
		typeIDs_hide_when_no_magazine = new int[]{
				DocTypes.MAGAZINE.typeID(),
				DocTypes.MAGAZINEARTICLE.typeID(),
		};
		//无数字报收费墙
		typeIDs_hide_when_no_paperPay = new int[]{
				DocTypes.MEMBERORDERS.typeID(),
				DocTypes.MEMBERSETMEAL.typeID(),
				DocTypes.MEMBERPAPERCARD.typeID(),
				DocTypes.MEMBERPAPERCARDLOG.typeID(),
				DocTypes.MEMBERSTATIC.typeID(),
				DocTypes.MEMBERSTATIC.typeID(),
				DocTypes.PAYLOG.typeID(),
		};
		//无通讯员
		typeIDs_hide_when_no_batman = new int[]{
				DocTypes.BATMAN.typeID(),
				DocTypes.CORPORATION.typeID(),
		};
		//无会员时
		typeIDs_hide_when_no_member = new int[]{
				DocTypes.MEMBER.typeID(),
				DocTypes.MEMBERSCORE.typeID(),
				DocTypes.MEMBERSCOREUNUSUAL.typeID(),
				DocTypes.MEMBERSCORERULE.typeID(),
				DocTypes.MEMBERINVITECODE.typeID(),
				DocTypes.MEMBERINVITECODELOG.typeID(),
				
				//无会员时，无任何互动功能
				DocTypes.DISCUSS.typeID(),//评论
				DocTypes.LIVE.typeID(), //直播
				DocTypes.TIPOFF.typeID(), //报料
				DocTypes.FEEDBACK.typeID(),//意见反馈
				DocTypes.SUBJECT.typeID(), //问吧
				DocTypes.SUBJECTQA.typeID(),
				DocTypes.QA.typeID(), //问政
				DocTypes.ACTIVITY.typeID(), //活动
				DocTypes.MEMBERVOTE.typeID(), //投票
		};
	}
	/**
		<!-- 稿件的发布渠道配置 -->
		<publish-channels>
			<channel id="0" code="Web" name="Web版"/>
			<channel id="1" code="App" name="App版"/>
		</publish-channels>
	 */
	private static Channel[] readChannels(Node doc, String nodeName) {
		Channel[] cs = new Channel[2];
		
		if (SecurityHelper.webUsable()) {
			cs[0] = new Channel(0, "Web", "Web版");
		}
		if (SecurityHelper.appUsable()) {
			cs[1] = new Channel(1, "App", "App版");
		}
		return cs;
	}
}
