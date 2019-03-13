package com.founder.xy.workspace;

import java.text.Collator;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.founder.e5.flow.Flow;
import com.founder.e5.flow.FlowNode;
import com.founder.e5.flow.FlowReader;
import com.founder.e5.permission.FlowPermissionReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.founder.e5.cat.Category;
import com.founder.e5.commons.ArrayUtils;
import com.founder.e5.commons.DomHelper;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.e5.web.DomInfo;
import com.founder.e5.web.WebUtil;
import com.founder.e5.workspace.ProcHelper;
import com.founder.xy.app.MobileAppManager;
import com.founder.xy.article.Article;
import com.founder.xy.column.Column;
import com.founder.xy.column.ColumnReader;
import com.founder.xy.commons.CatTypes;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.commons.web.GroupManager;
import com.founder.xy.config.Channel;
import com.founder.xy.config.ConfigReader;
import com.founder.xy.config.SecurityHelper;
import com.founder.xy.config.SubTab;
import com.founder.xy.config.Tab;
import com.founder.xy.system.Tenant;
import com.founder.xy.system.site.Site;
import com.founder.xy.system.site.SiteUserManager;
import com.founder.xy.system.site.SiteUserReader;
import com.founder.xy.weibo.WeiboManager;
import com.founder.xy.workspace.service.ListProcService;
import com.founder.xy.wx.WeixinManager;
import com.founder.xy.wx.data.Account;

/**
 * 工作平台主界面
 * 
 * @author Gong Lijie
 */
@Controller
@RequestMapping("/xy")
public class Main {
	@Autowired
	ListProcService listProcService;
	@Autowired
	SiteUserReader siteUserReader;
	@Autowired
	private ColumnReader colReader;
	@Autowired
	private GroupManager groupManager;
	@Autowired
	private SiteUserManager userManager;
	
	@Autowired
	private MobileAppManager mobileAppManager;

	/**
	 * 登录后的主界面
	 */
	@RequestMapping("Entry.do")
	public ModelAndView entry(HttpServletRequest request,
			@RequestParam("s") int siteID) throws Exception {
		// 读有权限的Tab
		List<Tab> tabs = MainHelper.getRoleTabs(ProcHelper.getRoleID(request));
		if (tabs == null) {
			return null;
		}
		// 读有权限的站点
		List<Site> sites = MainHelper.getSites(request);
		if (sites == null || sites.size() == 0)
			return null;
		//按拼音排序
		Collections.sort(sites, new Comparator<Site>() {
			@Override
			public int compare(Site o1, Site o2) {
				return Collator.getInstance(java.util.Locale.CHINA).compare(o1.getName(),o2.getName());
			}
		});
		// 检验当前站点是否有权限，防止url输入方式访问
		if (!MainHelper.siteEnable(sites, siteID))
			siteID = sites.get(0).getId();

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("sites", sites);
		model.put("siteCount", sites.size());
		model.put("siteID", siteID);

		if ("1".equals(WebUtil.get(request, "app"))) {
			// 是移动端登录
			return mainApp(request, model, siteID);
		} else {
			model.put("tabs", tabs);
			return new ModelAndView("/xy/Entry", model);
		}
	}

	/**
	 * 主界面：发布库
	 */
	@RequestMapping(value = { "Main.do" })
	public ModelAndView main(HttpServletRequest request) throws Exception {

		Map<String, Object> model = MainHelper.fillMainModel(request);

		// 渠道
		int ch = WebUtil.getInt(request, "ch", 0);
		if (ch == 1) {
			DocLib[] channelLibs = channelArticleLibs(request);

			DomInfo domInfo = (DomInfo) model.get("domInfo");
			domInfo.setDocLibID(channelLibs[1].getDocLibID());
			domInfo.setFolderID(channelLibs[1].getFolderID());
		}
		model.put("ch", ch);

		// 添加“栏目稿件关联”分类类型ID。
		int catTypeID = CatTypes.CAT_COLUMNARTICLE.typeID();
		model.put("catTypeID", catTypeID);

		return new ModelAndView("/xy/Main", model);
	}

	/**
	 * 主界面：原稿库，带分类
	 */
	@RequestMapping(value = { "MainOriginal.do" })
	public ModelAndView mainOriginal(HttpServletRequest request) throws Exception {

		Map<String, Object> model = MainHelper.fillMainModel(request);

		model.put("catTypeID", CatTypes.CAT_ORIGINAL.typeID());

		return new ModelAndView("/xy/MainOriginalNew", model);
	}

	/**
	 * 主界面：不带左边导航树的
	 */
	@RequestMapping(value = { "MainSimple.do" })
	public ModelAndView mainSimple(HttpServletRequest request) throws Exception {

		Map<String, Object> model = MainHelper.fillMainModel(request);
		return new ModelAndView("/xy/MainSimple", model);
	}

	/**
	 * 主界面：会员-邮件订阅
	 */
	@RequestMapping(value = { "MainEmailSubscribe.do" })
	public ModelAndView mainEmailSubscribe(HttpServletRequest request) throws Exception {

		Map<String, Object> model = MainHelper.fillMainModel(request);

		return new ModelAndView("/xy/MainEmailSubscribe", model);
	}


	/**
	 * 主界面：我的稿件
	 */
	@RequestMapping(value = { "MainSelf.do" })
	public ModelAndView mainSelf(HttpServletRequest request) throws Exception {

		Map<String, Object> model = MainHelper.fillMainModel(request);

		// 取出web发布库、App发布库的参数
		int articleTypeID = DocTypes.ARTICLE.typeID();
		int listID = DomHelper.getListID(articleTypeID, "我的稿件列表");
		int queryID = DomHelper.getQuery(articleTypeID, "qMyArticle").getId();

		// 按渠道
		List<DomInfo> domInfos = new ArrayList<DomInfo>();

		Channel[] channels = ConfigReader.getChannels();
		DocLib[] docLibs = channelArticleLibs(request);
		for (int i = 0; i < channels.length; i++) {
			DomInfo domInfo = new DomInfo();
			domInfo.setDocTypeID(articleTypeID);
			domInfo.setListID(listID);
			domInfo.setListIDs(String.valueOf(listID));
			domInfo.setQueryID(queryID);
			domInfo.setDocLibID(docLibs[i].getDocLibID());
			domInfo.setFolderID(docLibs[i].getFolderID());

			domInfos.add(domInfo);
		}
		model.put("channels", channels);
		model.put("domInfos", domInfos);

		return new ModelAndView("/xy/MainSelf", model);
	}

	/**
	 * 主界面：撤稿中心
	 */
	@RequestMapping(value = { "MainRevoke.do" })
	public ModelAndView mainRevoke(HttpServletRequest request) throws Exception {

		Map<String, Object> model = fillChannelModel(request);

		return new ModelAndView("/xy/MainRevoke", model);
	}

	/**
	 * 主界面：回收站
	 */
	@RequestMapping(value = { "MainGarbage.do" })
	public ModelAndView mainGarbage(HttpServletRequest request)
			throws Exception {

		Map<String, Object> model = fillChannelModel(request);

		return new ModelAndView("/xy/MainGarbage", model);
	}

	/**
	 * 主界面：审核稿件
	 */
	@RequestMapping(value = { "MainAudit.do" })
	public ModelAndView mainAudit(HttpServletRequest request) throws Exception {

		// 防止非法访问
		SubTab subTab = MainHelper.accessSubTab(request);
		if (subTab == null)
			return null;

		List<Column> result = getAuditColumns(request);
		//取有审批权限的流程节点
		List<FlowNode> nodes = getAuditNodes(request);

		Map<String, Object> model = fillChannelModel(request);
		model.put("cols", result);
		model.put("nodes",nodes);

		return new ModelAndView("/xy/MainAudit", model);
	}

	/**
	 * 主界面：带分组的
	 */
	@RequestMapping(value = { "MainGroup.do" })
	public ModelAndView mainGroup(HttpServletRequest request) throws Exception {

		Map<String, Object> model = MainHelper.fillMainModel(request);

		SubTab curTab = (SubTab) model.get("subTab");
		String code = curTab.getDocTypeCode(); // 分类类型的常量定义成与对应的文档类型编码一样

		int catTypeID = (Enum.valueOf(CatTypes.class, "CAT_" + code)).typeID();
		DocTypes docType = Enum.valueOf(DocTypes.class, code);

		int siteID = MainHelper.getSiteEnable(request);
		// 若是扩展字段，需要显示“挂件栏目”操作
		boolean isExtField = code.equals("EXTFIELD");
		Category[] AllGroups = InfoHelper.getCatGroups(request, catTypeID, siteID);
		//@wenkx 增加分组权限
		if("TEMPLATE".equals(code) || "SPECIAL".equals(code) || "PHOTO".equals(code)) {
			AllGroups = groupManager.getGroupsWithPower(request, siteID,AllGroups);
		}
		model.put("groups",AllGroups );

		model.put("catTypeID", catTypeID);
		model.put("isExtField", isExtField);
		model.put("groupField", docType.groupField());
		model.put("siteField", docType.siteField());

		return new ModelAndView("/xy/MainGroup", model);
	}
	
	 /**
   * 主界面：投票
   */
  @RequestMapping(value = { "MainGroupVote.do" })
  public ModelAndView mainGroupVote(HttpServletRequest request) throws Exception {

    Map<String, Object> model = MainHelper.fillMainModel(request);

    SubTab curTab = (SubTab) model.get("subTab");
    String code = curTab.getDocTypeCode(); // 分类类型的常量定义成与对应的文档类型编码一样

    int catTypeID = (Enum.valueOf(CatTypes.class, "CAT_" + code)).typeID();
    DocTypes docType = Enum.valueOf(DocTypes.class, code);

    int siteID = MainHelper.getSiteEnable(request);
    // 若是扩展字段，需要显示“挂件栏目”操作
    boolean isExtField = code.equals("EXTFIELD");
    Category[] AllGroups = InfoHelper.getCatGroups(request, catTypeID, siteID);
    //@wenkx 增加分组权限
    if("TEMPLATE".equals(code) || "SPECIAL".equals(code) || "PHOTO".equals(code)) {
      AllGroups = groupManager.getGroupsWithPower(request, siteID,AllGroups);
    }
    model.put("groups",AllGroups );

    model.put("catTypeID", catTypeID);
    model.put("isExtField", isExtField);
    model.put("groupField", docType.groupField());
    model.put("siteField", docType.siteField());

    return new ModelAndView("/xy/MainGroupVote", model);
  }
  
	@RequestMapping("/checkGroup.do")
   public ModelAndView checkGroup (HttpServletRequest request,
           HttpServletResponse response,
           Integer DocLibID, Long DocIDs, Integer siteID, Integer groupID, String UUID)throws Exception {


		int catTypeID = (Enum.valueOf(CatTypes.class, "CAT_SPECIAL")).typeID();
		DocTypes docType = Enum.valueOf(DocTypes.class, "SPECIAL");

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("groups", InfoHelper.getCatGroups(request, catTypeID, siteID));
		model.put("catTypeID", catTypeID);
		model.put("groupField", docType.groupField());
		model.put("siteField", docType.siteField());
   	    model.put("docLibID", DocLibID);
        model.put("docID", DocIDs);
        model.put("siteID", siteID);
        model.put("groupID", groupID);
        model.put("UUID", UUID);

        return new ModelAndView("/xy/special/dialog/copy", model);
   }

	/**
	 * 主界面：页面区块内容管理
	 */
	@RequestMapping(value = { "MainBlock.do" })
	public ModelAndView mainBlock(HttpServletRequest request) throws Exception {

		// 防止非法访问
		SubTab subTab = MainHelper.accessSubTab(request);
		if (subTab == null)
			return null;

		int siteID = MainHelper.getSiteEnable(request);

		DomInfo domInfo = new DomInfo();

		int docTypeID = DocTypes.BLOCKARTICLE.typeID();
		String tenantCode = InfoHelper.getTenantCode(request);

		DocLib docLib = LibHelper.getLib(docTypeID, tenantCode);

		domInfo.setDocTypeID(docTypeID);
		domInfo.setRule("SYS_DELETEFLAG_EQ_0");
		domInfo.setDocLibID(docLib.getDocLibID());
		domInfo.setFolderID(docLib.getFolderID());

		String blockIDField = "ba_blockID";
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("blockIDField", blockIDField);
		model.put("siteID", siteID);
		model.put("domInfo", domInfo);

		return new ModelAndView("/xy/MainBlock", model);
	}

	/**
	 * 主界面：互动中的多个界面，列表操作的形式不同
	 */
	@RequestMapping(value = { "MainNisAudit.do" })
	public ModelAndView mainNisAudit(HttpServletRequest request)
			throws Exception {
		Map<String, Object> model = MainHelper.fillMainModel(request);
		return new ModelAndView("/xy/MainNisAudit", model);
	}

	/**
	 * 主界面：互动话题
	 */
	@RequestMapping(value = { "MainNisSubject.do" })
	public String mainNisSubject(HttpServletRequest request) throws Exception {
		return "/xy/nis/MainSubject";
	}

	@RequestMapping(value = { "MainNisTipoff.do" })
	public ModelAndView mainNisQA(HttpServletRequest request)
			throws Exception {
		Map<String, Object> model = MainHelper.fillMainModel(request);
		int qaTypeID = DocTypes.TIPOFF.typeID();
		model.put("siteID",request.getParameter("siteID"));
		model.put("ListID1", DomHelper.getListID(qaTypeID, "爆料列表"));
		model.put("ListID2", DomHelper.getListID(qaTypeID, "爆料列表"));
		model.put("ListID3", DomHelper.getListID(qaTypeID, "爆料列表"));
		return new ModelAndView("/xy/MainNisTipoff", model);
	}


	/**
	 * 稿件统计导出
	 */
	@RequestMapping(value = { "MainStat0.do" })
	public ModelAndView mainStatArticle(HttpServletRequest request)
			throws Exception {

		Map<String, Object> model = MainHelper.fillMainModel(request);

		Channel[] channels = ConfigReader.getChannels();
		DocLib[] docLibs = channelArticleLibs(request);

		model.put("channels", channels);
		model.put("docLibs", docLibs);

		return new ModelAndView("/xy/MainStat0", model);
	}

	/**
	 * 主界面：数字报版面
	 */
	@RequestMapping(value = { "MainPaper.do" })
	public ModelAndView mainPaper(HttpServletRequest request) throws Exception {

		Map<String, Object> model = MainHelper.fillMainModel(request);
		return new ModelAndView("/xy/MainPaper", model);
	}

	/**
	 * 主界面：数字报稿件
	 */
	@RequestMapping(value = { "MainPaperArticle.do" })
	public ModelAndView mainPaperArticle(HttpServletRequest request)
			throws Exception {

		Map<String, Object> model = MainHelper.fillMainModel(request);
		model.put("isArticle", 1);
		return new ModelAndView("/xy/MainPaper", model);
	}

	/**
	 * 主界面：期刊稿件
	 */
	@RequestMapping(value = { "MainMagArticle.do" })
	public ModelAndView mainMagArticle(HttpServletRequest request)
			throws Exception {

		Map<String, Object> model = MainHelper.fillMainModel(request);
		model.put("isArticle", 1);
		return new ModelAndView("/xy/MainMagazine", model);
	}

	/**
	 * 主界面：微信图文
	 */
	@RequestMapping(value = { "MainWXGroup.do" })
	public ModelAndView mainWXGroup(HttpServletRequest request)
			throws Exception {

		Map<String, Object> model = MainHelper.fillMainModel(request);

		// 查出微信账号
		int siteID = (Integer) model.get("siteID");

		WeixinManager wxManager = (WeixinManager) Context.getBean("weixinManager");
		List<Account> acs = wxManager.getAdminAccounts(
				InfoHelper.getTenantCode(request), ProcHelper.getUserID(request), siteID, false, false);
		model.put("accounts", acs);

		return new ModelAndView("/xy/MainWX", model);
	}

	/**
	 * 主界面：微信菜单稿件
	 */
	@RequestMapping(value = { "MainWXArticle.do" })
	public ModelAndView mainWXArticle(HttpServletRequest request)
			throws Exception {

		Map<String, Object> model = MainHelper.fillMainModel(request);

		// 查出微信账号
		int siteID = (Integer) model.get("siteID");

		WeixinManager wxManager = (WeixinManager) Context.getBean("weixinManager");
		List<Account> acs = wxManager.getAdminAccounts(
				InfoHelper.getTenantCode(request), ProcHelper.getUserID(request), siteID, true, false);
		model.put("accounts", acs);
		model.put("isArticle", true);

		return new ModelAndView("/xy/MainWX", model);
	}

	/**
	 * 对话框：微信选稿界面
	 */
	@RequestMapping(value = { "MainWXSelect.do" })
	public ModelAndView mainWXSelect(HttpServletRequest request)
			throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		Channel[] chs = ConfigReader.getChannels();
		DocLib[] channelLibs = channelArticleLibs(request);
		if (channelLibs != null) {
			model.put("channels", chs);
			model.put("channelLib0", channelLibs[0]);
			model.put("channelLib1", channelLibs[1]);
		}
		int siteID = MainHelper.getSiteEnable(request);
		int roleID = ProcHelper.getRoleID(request);
		DomInfo domInfo = new DomInfo();
		// 根据渠道获取相应的渠道稿件库
		int docTypeID = DocTypes.ARTICLE.typeID();
		String tenantCode = InfoHelper.getTenantCode(request);
		List<DocLib> articleLibs = LibHelper.getLibs(docTypeID, tenantCode);
		DocLib docLib = articleLibs.get(chs[0].getId());
		int colLibID = LibHelper.getColumnLibID(request);

		int userID = ProcHelper.getUserID(request);
		String rule = "a_siteID_EQ_" + siteID + "_AND_a_status_EQ_"
				+ Article.STATUS_PUB_DONE;
		long colID = 0;
		String colName = null;
		Column[] cols = colReader.getOpColumns(colLibID, userID, siteID,
				chs[0].getId(), roleID);
		if (cols.length > 0) {
			colID = cols[0].getId();
			colName = cols[0].getName();
			rule = "a_columnID_EQ_" + colID + "_AND_" + rule;
		}
		int listID = DomHelper.getListID(docTypeID, "选稿列表");
		domInfo.setDocTypeID(docTypeID);
		domInfo.setRule(rule);
		domInfo.setDocLibID(docLib.getDocLibID());
		domInfo.setFolderID(docLib.getFolderID());
		domInfo.setListID(listID);
		domInfo.setListIDs(String.valueOf(listID));

		model.put("siteID", siteID);
		model.put("domInfo", domInfo);
		model.put("colID", colID);
		model.put("colName", colName);
		model.put("ch", chs[0].getId());

		return new ModelAndView("/xy/MainWXSelect", model);

	}

	/**
	 * 主界面：微博稿件
	 */
	@RequestMapping(value = { "MainWB.do" })
	public ModelAndView mainWB(HttpServletRequest request) throws Exception {

		Map<String, Object> model = MainHelper.fillMainModel(request);

		// 查出账号
		int siteID = (Integer) model.get("siteID");

		WeiboManager wbManager = (WeiboManager) Context.getBean("weiboManager");
		model.put("accounts", wbManager.getAccounts(
				InfoHelper.getTenantCode(request), siteID));

		return new ModelAndView("/xy/MainWB", model);
	}

	/**
	 * 栏目回收站
	 */
	@RequestMapping(value = { "MainColGarbage.do" })
	public ModelAndView colGarbage(HttpServletRequest request) throws Exception {
		int siteID = MainHelper.getSiteEnable(request);
		int type = WebUtil.getInt(request, "type", 0);
		int ch = WebUtil.getInt(request, "ch", 0);

		DomInfo domInfo = new DomInfo();

		int docTypeID = DocTypes.COLUMN.typeID();
		String tenantCode = InfoHelper.getTenantCode(request);

		DocLib docLib = LibHelper.getLib(docTypeID, tenantCode);

		domInfo.setDocTypeID(docTypeID);
		domInfo.setDocLibID(docLib.getDocLibID());
		domInfo.setFolderID(docLib.getFolderID());
		
		int listID = 0;
		if (type == 0) {
			domInfo.setRule("col_siteID_EQ_" + siteID + "_AND_col_channel_EQ_" + ch);
			listID = DomHelper.getListID(docTypeID, "栏目使用情况列表");
		} else {
			String rule = "SYS_DELETEFLAG_EQ_" + type 
					+ "_AND_col_siteID_EQ_" + siteID
					+ "_AND_col_channel_EQ_" + ch;
			domInfo.setRule(rule);
			listID = DomHelper.getListID(docTypeID, "已删栏目列表");
		}
		domInfo.setListID(listID);
		domInfo.setListIDs(String.valueOf(listID));
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("siteID", siteID);
		model.put("type", type);
		model.put("domInfo", domInfo);

		return new ModelAndView("/xy/MainColGarbage", model);
	}

	
	/**
	 * 源稿栏目回收站
	 */
	@RequestMapping(value = { "MainOrgGarbage.do" })
	public ModelAndView orgGarbage(HttpServletRequest request) throws Exception {
		int siteID = MainHelper.getSiteEnable(request);
		int type = WebUtil.getInt(request, "type", 0);
		DomInfo domInfo = new DomInfo();

		int docTypeID = DocTypes.COLUMNORI.typeID();
		String tenantCode = InfoHelper.getTenantCode(request);

		DocLib docLib = LibHelper.getLib(docTypeID, tenantCode);

		domInfo.setDocTypeID(docTypeID);
		domInfo.setDocLibID(docLib.getDocLibID());
		domInfo.setFolderID(docLib.getFolderID());
		
		int listID = 0;
		if (type == 0) {
			domInfo.setRule("col_siteID_EQ_" + siteID);
			listID = DomHelper.getListID(docTypeID, "栏目使用情况列表");
		} else {
			String rule = "SYS_DELETEFLAG_EQ_" + type 
					+ "_AND_col_siteID_EQ_" + siteID;
			domInfo.setRule(rule);
			listID = DomHelper.getListID(docTypeID, "已删栏目列表");
		}
		domInfo.setListID(listID);
		domInfo.setListIDs(String.valueOf(listID));
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("siteID", siteID);
		model.put("type", type);
		model.put("domInfo", domInfo);

		return new ModelAndView("/xy/MainOrgGarbage", model);
	}
	
	/**
	 * 发布规则选择：利用发布规则列表
	 */
	@RequestMapping(value = { "MainRuleTree.do" })
	public ModelAndView ruleTree(HttpServletRequest request) throws Exception {
		int siteID = MainHelper.getSiteEnable(request);

		DomInfo domInfo = new DomInfo();

		int docTypeID = DocTypes.SITERULE.typeID();
		String tenantCode = InfoHelper.getTenantCode(request);

		DocLib docLib = LibHelper.getLib(docTypeID, tenantCode);

		domInfo.setDocTypeID(docTypeID);
		domInfo.setRule("rule_siteID_EQ_" + siteID + "_AND_SYS_DELETEFLAG_EQ_0");
		domInfo.setDocLibID(docLib.getDocLibID());
		domInfo.setFolderID(docLib.getFolderID());

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("siteID", siteID);
		model.put("domInfo", domInfo);
		model.put("type", WebUtil.get(request, "type"));

		return new ModelAndView("/xy/MainRuleTree", model);
	}

	/**
	 * 发布规则详细信息
	 */
	@RequestMapping(value = { "MainRuleInfo.do" })
	@ResponseBody
	public Map<String, Object> ruleInfo(HttpServletRequest request)
			throws Exception {

		String ruleID = WebUtil.get(request, "ruleID");
		int docTypeID = DocTypes.SITERULE.typeID();
		String tenantCode = InfoHelper.getTenantCode(request);
		DocLib docLib = LibHelper.getLib(docTypeID, tenantCode);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] docs = docManager.find(docLib.getDocLibID(),
				"SYS_DOCUMENTID = ? and SYS_DELETEFLAG = 0", new Object[] {ruleID});
		Map<String, Object> model = new HashMap<String, Object>();
		if (docs.length == 1) {
			model.put("column_dir", docs[0].get("rule_column_dir"));
			model.put("article_dir", docs[0].get("rule_article_dir"));
			model.put("photo_dir", docs[0].get("rule_photo_dir"));
			model.put("attach_dir", docs[0].get("rule_attach_dir"));
		}
		return model;
	}

	/**
	 * 已发布稿件选择：发布库稿件列表
	 * 合成多标题时候type为0，
	 * 区块内容选稿时候type为1，
	 * 挂件选择组图稿件时候type为2，
	 * 挂件选择视频稿件的时候type为3,
	 * 选择相关稿件的时候type为4,
	 * 选题选择关联稿件的时候type为5，
	 * 推荐模块选稿时type为6
	 */
	@RequestMapping(value = { "MainArticle.do" })
	public ModelAndView articleSelect(HttpServletRequest request)
			throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();

		int siteID = MainHelper.getSiteEnable(request);
		int groupID=WebUtil.getInt(request, "groupID", 0);
		String UUID=WebUtil.getStringParam(request, "UUID");

		DomInfo domInfo = new DomInfo();

		// 根据渠道获取相应的渠道稿件库
		int docTypeID = DocTypes.ARTICLE.typeID();
		String tenantCode = InfoHelper.getTenantCode(request);
		List<DocLib> articleLibs = LibHelper.getLibs(docTypeID, tenantCode);
		int ch = WebUtil.getInt(request, "ch", 0);
		DocLib docLib = articleLibs.get(ch);

		long colID = WebUtil.getLong(request, "colID", 0);
		int type = WebUtil.getInt(request, "type", 0);

		int colLibID = LibHelper.getColumnLibID(request);
		Column col = colReader.get(colLibID, colID);
		String colName = (col == null) ? "" : col.getName();

		String rule = "";
		int listID = DomHelper.getListID(docTypeID);
		rule = "a_siteID_EQ_" + siteID + "_AND_a_status_EQ_"
				+ Article.STATUS_PUB_DONE;

		long docID_rel = 0;
        if (type == 0 || type == 6) {
            // 合成多标题 or 推荐栏目选稿
            rule = "a_columnID_EQ_" + colID + "_AND_" + rule;
            listID = DomHelper.getListID(docTypeID, "选稿列表");
        } else if(type == 4){
            //相关稿件
            docID_rel = WebUtil.getLong(request, "docID", 0);
            rule = "a_columnID_EQ_" + colID + "_AND_" + rule + "_AND_SYS_DOCUMENTID!="+docID_rel;
            listID = DomHelper.getListID(docTypeID, "选稿列表");
        }else if (type == 1) {
			// 区块内容选稿
			int userID = ProcHelper.getUserID(request);
			int roleID = ProcHelper.getRoleID(request);
			Column[] cols = colReader
					.getOpColumns(colLibID, userID, siteID, ch, roleID);
			if (cols.length > 0) {
				colID = cols[0].getId();
				colName = cols[0].getName();
				rule = "a_columnID_EQ_" + colID + "_AND_" + rule;
			}
			listID = DomHelper.getListID(docTypeID, "选稿列表");
		} else if (type == 2) {
			// 挂件选择：组图稿
			rule = "a_columnID_EQ_" + colID + "_AND_a_type_EQ_1_AND_" + rule;
			listID = DomHelper.getListID(docTypeID, "挂件选择列表");
		} else if (type == 3) {
			// 挂件选择：视频稿
			rule = "a_columnID_EQ_" + colID + "_AND_a_type_EQ_2_AND_" + rule;
			listID = DomHelper.getListID(docTypeID, "挂件选择列表");
		} else if (type == 5) {
			// 选题的关联稿件
			listID = DomHelper.getListID(docTypeID, "挂件选择列表");
		}

		domInfo.setDocTypeID(docTypeID);
		domInfo.setRule(rule);
		if (type == 5) {
			domInfo.setDocLibID(LibHelper.getArticleAppLibID());
			domInfo.setFolderID(DomHelper.getFVIDByDocLibID(LibHelper
					.getArticleAppLibID()));
		} else {
			domInfo.setDocLibID(docLib.getDocLibID());
			domInfo.setFolderID(docLib.getFolderID());
		}
		domInfo.setListID(listID);
		domInfo.setListIDs(String.valueOf(listID));
		if(type==6){
			model.put("groupID", groupID);
			model.put("UUID",UUID);
		}

		model.put("siteID", siteID);
		model.put("domInfo", domInfo);
		model.put("colID", colID);
		model.put("colName", colName);
		model.put("type", type);
		model.put("ch", ch);
        model.put("docIDRel", docID_rel);

        // 供数据库查询字段
		return new ModelAndView("/xy/MainArticle", model);
	}
	
	/**
	 * 跨站点选稿
	 */
	@RequestMapping(value = { "SiteArticle.do" })
	public ModelAndView SiteArticle(HttpServletRequest request) throws Exception{
		Map<String, Object> model = new HashMap<String, Object>();
		int siteID = MainHelper.getSiteEnable(request);
		long colID = WebUtil.getLong(request, "colID", 0);
		int colLibID = LibHelper.getColumnLibID(request);
		int type = WebUtil.getInt(request, "type", 0);

		int docTypeID = DocTypes.ARTICLE.typeID();
		List<DocLib> articleLibs = LibHelper.getLibs(docTypeID, InfoHelper.getTenantCode(request));
		int ch = WebUtil.getInt(request, "ch", 0);
		DocLib docLib = articleLibs.get(ch);
		
		Column col = colReader.get(colLibID, colID);
		String colName = (col == null) ? "" : col.getName();
		String rule = "a_columnID_EQ_" + colID + "_AND_a_type_EQ_0_AND_a_siteID_EQ_" + siteID;
		int listID = DomHelper.getListID(docTypeID, "选稿列表");
		
		int userID = ProcHelper.getUserID(request);
		int userLibID = LibHelper.getUserExtLibID(request);
		List<Site> sites = userManager.getUserSites(userLibID, userID);

		DomInfo domInfo = new DomInfo();
		domInfo.setDocTypeID(docTypeID);
		domInfo.setRule(rule);
		domInfo.setDocLibID(docLib.getDocLibID());
		domInfo.setFolderID(docLib.getFolderID());
		domInfo.setListID(listID);
		domInfo.setListIDs(String.valueOf(listID));
		
		model.put("siteID", siteID);
		model.put("sites", sites);
		model.put("colID", colID);
		model.put("colName", colName);
		model.put("ch", ch);
		model.put("domInfo", domInfo);
		model.put("type", type);
		model.put("UUID",WebUtil.getStringParam(request, "UUID"));
		return new ModelAndView("/xy/SiteArticle", model);
	}
	
	/**
	 * 专题选稿
	 */
	@RequestMapping(value = { "SpecialArticle.do" })
	public ModelAndView SpecialArticle(HttpServletRequest request)
			throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();

		int siteID = MainHelper.getSiteEnable(request);

		DomInfo domInfo = new DomInfo();

		// 根据渠道获取相应的渠道稿件库
		int docTypeID = DocTypes.ARTICLE.typeID();
		String tenantCode = InfoHelper.getTenantCode(request);
		List<DocLib> articleLibs = LibHelper.getLibs(docTypeID, tenantCode);
		int ch = WebUtil.getInt(request, "ch", 0);
		DocLib docLib = articleLibs.get(ch);

		long colID = WebUtil.getLong(request, "colID", 0);
		// 合成多标题时候type为0，区块内容选稿时候type为1，挂件选择组图稿件时候type为2，挂件选择视频稿件的时候type为3,选择相关稿件的时候type为4,
		// 选题选择关联稿件的时候type为5
		int type = WebUtil.getInt(request, "type", 0);

		int colLibID = LibHelper.getColumnLibID(request);
		Column col = colReader.get(colLibID, colID);
		String colName = (col == null) ? "" : col.getName();

		String rule = "";
		int listID = DomHelper.getListID(docTypeID);
		rule = "a_siteID_EQ_" + siteID + "_AND_a_status_EQ_1";
		if (type == 0) {
			rule = "CLASS_1_EQ_" + colID + "_AND_" + rule + "_AND_a_type_LT__GT_5";
			listID = DomHelper.getListID(docTypeID, "专题选稿图册列表");
		} else if (type == 1) {
			rule = "CLASS_1_EQ_" + colID + "_AND_" + rule + "_AND_a_type_LT__GT_5";
			listID = DomHelper.getListID(docTypeID, "专题选稿列表");
		}

		domInfo.setDocTypeID(docTypeID);
		domInfo.setRule(rule);
		if (ch == 1) {
			domInfo.setDocLibID(LibHelper.getArticleAppLibID());
			domInfo.setFolderID(DomHelper.getFVIDByDocLibID(LibHelper
					.getArticleAppLibID()));
		} else {
			domInfo.setDocLibID(docLib.getDocLibID());
			domInfo.setFolderID(docLib.getFolderID());
		}
		domInfo.setListID(listID);
		domInfo.setListIDs(String.valueOf(listID));

		model.put("siteID", siteID);
		model.put("domInfo", domInfo);
		model.put("colID", colID);
		model.put("colName", colName);
		model.put("type", type);
		model.put("ch", ch);

		// 添加“栏目稿件关联”分类类型ID。
		int catTypeID = CatTypes.CAT_COLUMNARTICLE.typeID();
		model.put("catTypeID", catTypeID);

		// 供数据库查询字段
		return new ModelAndView("/xy/SpecialArticle", model);
	}

	/**
	 * 按组显示的数据的选择窗口，如选择图片库、选择视频库、选择专题
	 */
	@RequestMapping(value = { "GroupSelect.do" })
	public ModelAndView groupSelect(HttpServletRequest request)
			throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();

		int siteID = MainHelper.getSiteEnable(request);
		int type = WebUtil.getInt(request, "type", 0); // 0表示图片，1表示视频
		int all = WebUtil.getInt(request, "all", 0); // 1表示显示所有图片，包括组图里的每张图
        int moduleID = WebUtil.getInt(request, "moduleID", 0);//首页推荐模块ID
        String UUID=WebUtil.getStringParam(request,"UUID");

		DocLib docLib = null;
		String rule = null;
		int catTypeID = 0;
		DocTypes docType = null;
		if (type == 0) { //选择图片
			docType = DocTypes.PHOTO;
			catTypeID = CatTypes.CAT_PHOTO.typeID();
			rule = "p_siteID_EQ_" + siteID;
			if (all == 1)
				rule += "_AND_SYS_DELETEFLAG_GE_0";
		} else if (type == 1) { //选择视频
			if (!SecurityHelper.videoUsable()) {
				return new ModelAndView("/xy/site/NoLicense", null);
			}
			docType = DocTypes.VIDEO;
			catTypeID = CatTypes.CAT_VIDEO.typeID();
			rule = "v_status_EQ_2_AND_v_siteID_EQ_" + siteID; // 视频要已发布
		} else if (type == 2) { //选择专题
			docType = DocTypes.SPECIAL;
			catTypeID = CatTypes.CAT_SPECIAL.typeID();
			rule = "s_siteID_EQ_" + siteID;
		} else if (type == 3) { //选择互动话题
			docType = DocTypes.SUBJECT;
			catTypeID = CatTypes.CAT_DISCUSSTYPE.typeID();
			rule = "a_status_EQ_1_AND_a_siteID_EQ_" + siteID;
		} else if (type == 4) { //选择来源
			docType = DocTypes.SOURCE;
			catTypeID = CatTypes.CAT_SOURCE.typeID();
			rule = "src_siteID_EQ_" + siteID;
		} else if (type == 5) { //选择标签
			docType = DocTypes.TAG;
			catTypeID = CatTypes.CAT_TAG.typeID();
			rule = "a_siteID_EQ_" + siteID;
		}

		docLib = LibHelper.getLib(docType.typeID(),
				InfoHelper.getTenantCode(request));

		DomInfo domInfo = new DomInfo();
		domInfo.setDocTypeID(docLib.getDocTypeID());
		domInfo.setDocLibID(docLib.getDocLibID());
		domInfo.setFolderID(docLib.getFolderID());
		domInfo.setRule(rule);

		model.put("isGroupSelect", true); // 表示是按组选择窗口

		model.put("siteID", siteID);
		model.put("domInfo", domInfo);
		model.put("type", type);

		Category[] AllGroups = InfoHelper.getCatGroups(request, catTypeID, siteID);
		//@wenkx 增加分组权限
		if(type == 2 || type == 0){
			AllGroups = groupManager.getGroupsWithPower(request, siteID,AllGroups);
		}

		model.put("groups", AllGroups);
		model.put("catTypeID", catTypeID);
		model.put("groupField", docType.groupField());
		model.put("siteField", docType.siteField());

        if(moduleID!=0){
            model.put("moduleID",moduleID);
            model.put("UUID",UUID);
        }

		return new ModelAndView("/xy/GroupSelect", model);
	}

	/**
	 * 无组的数据的选择窗口，如选择直播话题
	 * type:0直播话题选择（直播稿中），1会员选择（问吧题主）
	 */
	@RequestMapping(value = { "SimpleSelect.do" })
	public ModelAndView simpleSelect(HttpServletRequest request)
			throws Exception {
		Map<String, Object> model = fillByType(request);

		return new ModelAndView("/xy/SimpleSelect", model);
	}

	/**
	 * 操作：这是调用主界面的操作。
	 * 用于：查看评论（稿件的、直播的，等）
	 */
	@RequestMapping(value = { "DataMain.do" })
	public ModelAndView dataMain(HttpServletRequest request) throws Exception {
		Map<String, Object> model = null;
		String tabID = WebUtil.get(request, "t");
		if ("nisdis".equals(tabID))
			model = fillDiscussModel(request, tabID);
		else {
			//用于需要显示附件的互动类的界面，如直播详情中的直播报道列表
			model = fillByType(request);
		}

		return new ModelAndView("/xy/MainNisAudit", model);
	}

	private Map<String, Object> fillByType(HttpServletRequest request) {
		Map<String, Object> model = new HashMap<String, Object>();
	
		int siteID = MainHelper.getSiteEnable(request);
		String rule = "a_siteID_EQ_" + siteID;
		
		DocTypes docType = null;
		int type = WebUtil.getInt(request, "type", 0);
		switch (type) {
			case 0 : { //选择直播
				docType = DocTypes.LIVE;
				break;
			}
			case 1 : { //选择会员
				docType = DocTypes.MEMBER;
				rule = "m_siteID_EQ_" + siteID;
				break;
			}
			case 2 : { //推荐模块中选择活动（来自SimpleSelect）
				docType = DocTypes.ACTIVITY;
				rule = "a_status_EQ_1";
				break;
			}
			case 3 : { //推荐模块中选择问答（来自SimpleSelect）
				docType = DocTypes.QA;
				rule = "a_status_EQ_1";
				break;
			}
			case 4 : { //直播详情里显示直播的报道列表
				docType = DocTypes.LIVEITEM;
				rule = "a_rootID_EQ_" + WebUtil.get(request, "groupID");
				break;
			}
			case 5 : { //用户权限复制选择权限来源用户
				docType = DocTypes.USEREXT;
				rule = "u_siteID_EQ_" + siteID;
				break;
			}
			default : {
				break;
			}
		}
	
		DocLib docLib = LibHelper.getLib(docType.typeID(), InfoHelper.getTenantCode(request));
	
		DomInfo domInfo = new DomInfo();
		domInfo.setDocTypeID(docLib.getDocTypeID());
		domInfo.setDocLibID(docLib.getDocLibID());
		domInfo.setFolderID(docLib.getFolderID());
		domInfo.setRule(rule);
	
		String groupID = WebUtil.get(request, "groupID");
		String UUID = WebUtil.get(request, "UUID");
		if (!StringUtils.isBlank(groupID)) model.put("groupID", groupID);
		if (!StringUtils.isBlank(UUID)) model.put("UUID", UUID);
	
		model.put("siteID", siteID);
		model.put("domInfo", domInfo);
		model.put("type", type);
		
		return model;
	}

	private Map<String, Object> fillDiscussModel(HttpServletRequest request, String id) throws E5Exception {
		// 按指定的tab，不判断权限
		List<Tab> tabs = ConfigReader.getTabs();
		SubTab subTab = MainHelper.getSubTab(tabs, id);

		// 操作传入的参数
		int siteID = WebUtil.getInt(request, "siteID", 0);
		long docID = WebUtil.getLong(request, "DocIDs", 0);
		int type = WebUtil.getInt(request, "type", 1); // 是查看稿件评论还是直播评论

		// 检查siteID合法性
		siteID = MainHelper.getSiteEnable(request, siteID);

		DomInfo domInfo = MainHelper.getDomInfo(request, subTab);

		// 查看评论的规则公式
		String rule = "a_articleID_EQ_" + docID + "_AND_a_sourceType_EQ_" + type;
		domInfo.setRule(rule);

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("subTab", subTab);
		model.put("domInfo", domInfo);
		model.put("siteID", siteID); // 当前站点ID
		return model;
	}

	/**
	 * 取列表列的操作
	 */
	@RequestMapping(value = "ListProcs.do")
	public void listProcs(HttpServletRequest request,
			HttpServletResponse response, @RequestParam int docLibID,
			@RequestParam int fvID, @RequestParam int opFlow) throws Exception {
		int roleID = ProcHelper.getRoleID(request);
		String procs = listProcService.getProcs(roleID, docLibID, fvID, opFlow);

		InfoHelper.outputJson(procs, response);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = { "appList.do" })
	public ModelAndView appList(HttpServletRequest request) throws Exception {

		String moId = request.getParameter("DocIDs");
		Map<String, Object> model = new HashMap<String, Object>();
		Document[] doc = mobileAppManager.findByMoId(moId);
		Map<String, Object>[] maps = new HashMap[doc.length];
		for (int i = 0; i < doc.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();

			map.put("docId", doc[i].get("SYS_DOCUMENTID"));
			map.put("maName", doc[i].get("ma_name"));
			map.put("maType", doc[i].get("ma_type"));

			maps[i] = map;
		}

		DomInfo domInfo = new DomInfo();

		int docTypeID = DocTypes.MOBILEPACKAGE.typeID();
		String tenantCode = Tenant.DEFAULTCODE;

		DocLib docLib = LibHelper.getLib(docTypeID, tenantCode);
		domInfo.setDocTypeID(docTypeID);
		// domInfo.setRule("SYS_DELETEFLAG_EQ_0");
		domInfo.setDocLibID(docLib.getDocLibID());
		domInfo.setFolderID(docLib.getFolderID());

		model.put("apps", maps);
		model.put("domInfo", domInfo);
		model.put("moId", moId);
		return new ModelAndView("/xy/app/MobileApp", model);
	}

	/**
	 * 统计--稿件明细。
	 * 调用方式：xy/StatArticles.do?siteID=1&t=stat1&type=2
	 * 其中t是当前菜单项（用在部门稿件明细、个人稿件明细页的调用）
	 * type是页面区分，0稿件明细，1部门稿件明细，2个人稿件明细。type可以控制查询条件的细微差别
	 */
	@RequestMapping(value = { "StatArticles.do" })
	public ModelAndView statDeptArticle(HttpServletRequest request)
			throws Exception {
		/**
		 * 过滤规则、列表等配置在菜单上。
		 * 所以这里可以直接读菜单配置，与上面其它方法里获取参数的行为一致。
		 * 只是简化代码而已，不配置在菜单里也可以，在此处手工写。
		 */
		Map<String, Object> model = MainHelper.fillMainModel(request);

		Channel[] channels = ConfigReader.getChannels();
		DocLib[] docLibs = channelArticleLibs(request);


        int qaTypeID = DocTypes.ARTICLE.typeID();
        int articlelistID = DomHelper.getListID(qaTypeID, "稿件统计明细列表");
        int topiclistID = DomHelper.getListID(qaTypeID, "稿件明细话题稿件列表");

        // 按web稿件/app稿件/话题稿件来区分
        List<DomInfo> domInfos = new ArrayList<DomInfo>();
        for (int i = 0; i < 3; i++) {
            DomInfo domInfo = new DomInfo();
            int listID;//列表id
            if(i==0){//web稿件
                listID = articlelistID;
                domInfo.setDocLibID(docLibs[0].getDocLibID());
                domInfo.setFolderID(docLibs[0].getFolderID());
            }else if (i==1){//app稿件
                listID = articlelistID;
                domInfo.setDocLibID(docLibs[1].getDocLibID());
                domInfo.setFolderID(docLibs[1].getFolderID());
            }else{//话题稿件
                listID = topiclistID;
                domInfo.setDocLibID(docLibs[0].getDocLibID());
                domInfo.setFolderID(999888);
            }
            domInfo.setListID(listID);

            domInfos.add(domInfo);
        }

        model.put("channels", channels);
        model.put("docLibs", domInfos);
        model.put("type", WebUtil.getInt(request, "type", 0));
        model.put("ListID3", DomHelper.getListID(qaTypeID, "稿件明细话题稿件列表"));

        return new ModelAndView("/xy/StatArticles", model);
	}

	/**
	 * 移动端主界面：显示发布库，带栏目
	 */
	private ModelAndView mainApp(HttpServletRequest request,
			Map<String, Object> model, int siteID) throws Exception {
		DomInfo domInfo = new DomInfo();

		DocLib[] channelLibs = channelArticleLibs(request);

		// 读文档库和文件夹
		domInfo.setDocTypeID(DocTypes.ARTICLE.typeID());
		domInfo.setDocLibID(channelLibs[1].getDocLibID());
		domInfo.setFolderID(channelLibs[1].getFolderID());

		// 读列表方式
		int listID = DomHelper.getListID(domInfo.getDocTypeID(), "移动列表");
		domInfo.setListID(listID);
		domInfo.setListIDs(String.valueOf(listID));

		// 取出有权限的栏目
		List<Column> columns = getColumns(request, siteID);

		model.put("domInfo", domInfo);

		model.put("ch", 1);
		model.put("catTypeID", CatTypes.CAT_COLUMNARTICLE.typeID());
		model.put("columns", columns);

		return new ModelAndView("/xy/MainApp", model);
	}

	// 读出用户有权限的栏目，用于移动端
	private List<Column> getColumns(HttpServletRequest request, int siteID)
			throws E5Exception {
		List<Column> result = new ArrayList<Column>();

		int userID = ProcHelper.getUserID(request);
		int userLibID = LibHelper.getUserExtLibID(request);

		// 取出本站点下所有的审批栏目
		int colLibID = LibHelper.getColumnLibID(request);

		// 用户Web版可操作的栏目ID
		// getColumns(siteID, userLibID, userID, colLibID, 0, result);

		// 用户App版可操作的栏目ID
		getColumns(siteID, userLibID, userID, colLibID, 4, result);

		return result;
	}

	// 读出某发布渠道的用户有权限的栏目
	private void getColumns(int siteID, int userLibID, int userID,
			int colLibID, int relType, List<Column> result) throws E5Exception {
		long[] ids = siteUserReader.getRelated(userLibID, userID, siteID,
				relType);
		if (ids != null) {
			for (long colID : ids) {
				Column col = colReader.get(colLibID, colID);
				if (col != null)
					result.add(col);
			}
		}
	}

	private List<Column> getAuditColumns(HttpServletRequest request)
			throws E5Exception, Exception {
		List<Column> result = new ArrayList<Column>();

		int siteID = MainHelper.getSiteEnable(request);
		int userID = ProcHelper.getUserID(request);
		int userLibID = LibHelper.getUserExtLibID(request);

		// 取出本站点下所有的审批栏目
		int colLibID = LibHelper.getColumnLibID(request);
		List<Column> cols = colReader.getAuditColumns(colLibID, siteID);

		// 用户Web版可操作的栏目ID
		long[] ids = siteUserReader.getRelated(userLibID, userID, siteID, 0);
		filter(result, cols, ids);

		// 用户App版可操作的栏目ID
		ids = siteUserReader.getRelated(userLibID, userID, siteID, 4);
		filter(result, cols, ids);

		return result;
	}

	private void filter(List<Column> result, List<Column> cols, long[] ids) {
		for (Column col : cols) {
			// 栏目的父路径。任意一级路径有权限即可
			int[] path = StringUtils.getIntArray(col.getCasIDs(), "~");
			for (long colID : path) {
				if (ArrayUtils.contains(ids, colID)) {
					result.add(col);
					break;
				}
			}
		}
	}

	// 组织带渠道稿件库的参数
	private Map<String, Object> fillChannelModel(HttpServletRequest request)
			throws Exception {
		Map<String, Object> model = MainHelper.fillMainModel(request);

		Channel[] chs = ConfigReader.getChannels();

		DocLib[] channelLibs = channelArticleLibs(request);
		if (channelLibs != null) {
			model.put("channels", chs);

			model.put("channelLib0", channelLibs[0]);
			model.put("channelLib1", channelLibs[1]);
		}
		return model;
	}

	/**
	 * 取Web版发布库和移动版发布库。
	 * 
	 * @return
	 */
	private DocLib[] channelArticleLibs(HttpServletRequest request) {
		String tenantCode = InfoHelper.getTenantCode(request);

		int docTypeID = DocTypes.ARTICLE.typeID();
		try {
			List<DocLib> docLibs = LibHelper.getLibs(docTypeID, tenantCode);
			return docLibs.toArray(new DocLib[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 稿件挂件选择投票
	 */
	@RequestMapping(value = { "VoteCheck.do" })
	public ModelAndView voteCheck(HttpServletRequest request) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		int siteID = MainHelper.getSiteEnable(request);
		// 获取区块分组类型
		int catType = CatTypes.CAT_VOTE.typeID();
		// 获取区块分组
		Category[] voteGroups = InfoHelper.getCatGroups(request, catType,
				siteID);

		DomInfo domInfo = new DomInfo();

		int docTypeID = DocTypes.VOTE.typeID();
		String rule = "vote_siteID_EQ_" + siteID;
		int listID = DomHelper.getListID(docTypeID, "投票挂件列表");

		domInfo.setDocTypeID(docTypeID);
		domInfo.setRule(rule);
		domInfo.setDocLibID(LibHelper.getLib(docTypeID).getDocLibID());
		domInfo.setFolderID(LibHelper.getLib(docTypeID).getFolderID());
		domInfo.setListID(listID);
		domInfo.setListIDs(String.valueOf(listID));

		model.put("siteID", siteID);
		model.put("domInfo", domInfo);
		model.put("voteGroups", voteGroups);
		// 供数据库查询字段
		model.put("vSiteIDField", "vote_siteID");
		model.put("vGroupIDField", "vote_groupID");

		return new ModelAndView("/xy/VoteCheck", model);
	}

	//取出有权限的审批节点的ID
	private List<FlowNode>  getAuditNodes(HttpServletRequest request) {
		List<FlowNode> result = new ArrayList<FlowNode>();

		int roleID = ProcHelper.getRoleID(request);

		FlowPermissionReader fpReader = (FlowPermissionReader)Context.getBean(FlowPermissionReader.class);
		FlowNode[] nodes = getAuditFlowNodes();
		if (nodes != null) {
			try {
				for (int j = 0; j < nodes.length; j++) {
					//过滤出有权限的流程节点ID集合
					int permission = fpReader.get(roleID, nodes[j].getFlowID(), nodes[j].getID());
					if (permission > 0)
						result.add(nodes[j]);
				}
			} catch (E5Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	//取出流程的审批节点
	private FlowNode[] getAuditFlowNodes() {
		List<FlowNode> result = new ArrayList<FlowNode>();

		int docTypeID = InfoHelper.getArticleTypeID();

		FlowReader flowReader = (FlowReader)Context.getBean(FlowReader.class);
		try {
			Flow[] flows = flowReader.getFlows(docTypeID);
			if (flows != null) {
				//找出稿件的所有流程，不包括第一个“无审批流程”
				for (int i = 1; i < flows.length; i++) {
					int flowID = flows[i].getID();
					//得到这些流程的中间流程节点ID，也就是去掉第一个节点（第一个是未发布阶段，不是审批阶段）
					//和最后两个节点（在发布阶段、已发布阶段）
					FlowNode[] nodes = flowReader.getFlowNodes(flowID);
					if (nodes != null) {
						for (int j = 1; j < nodes.length - 2; j++) {
							//把流程名称暂时存到节点描述中 供前台展示用
							nodes[j].setDescription(flows[i].getName());
							result.add(nodes[j]);
						}
					}
				}
			}
		} catch (E5Exception e) {
			e.printStackTrace();
		}
		return result.toArray(new FlowNode[0]);
	}

	/**
	 * 发布规则详细信息
	 */
	@RequestMapping(value = { "MainArticleInfo.do" })
	@ResponseBody
	public Map<String, Object> articleInfo(HttpServletRequest request)
			throws Exception {

		String ruleID = WebUtil.get(request, "articleID","-1");
        String articleChannel = WebUtil.get(request, "articleChannel","1");

		int docTypeID = DocTypes.ARTICLE.typeID();
		String tenantCode = InfoHelper.getTenantCode(request);

		List<DocLib> docLibs = LibHelper.getLibs(docTypeID, tenantCode);

        DocLib docLib = null;
        Map<String, Object> model = new HashMap<String, Object>();

        if (docLibs!=null && docLibs.size()>0){
            if("1".equals(articleChannel)){//web稿件
                docLib = docLibs.get(0);
            }else if("2".equals(articleChannel)&&docLibs.size()>1){//app稿件
                docLib = docLibs.get(1);
            }
        }
        if(docLib==null){
            model.put("articleTitle", "无");
            model.put("column", "无");
            model.put("columnRel", "无");
            return model;
        }

        DocumentManager docManager = DocumentManagerFactory.getInstance();
        Document[] docs = docManager.find(docLib.getDocLibID(),
				"SYS_DOCUMENTID = ? and SYS_DELETEFLAG = 0", new Object[] {ruleID});
		if (docs.length == 1) {
			String column = (String) docs[0].get("a_column") + "(" + docs[0].get("a_columnID") + ")";
			if(StringUtils.isBlank(column) || column == null){
				column = "无";
				int tempLibID = LibHelper.getColumnLibID();
				Document[] cols = docManager.find(tempLibID,
						"SYS_DOCUMENTID = ? and SYS_DELETEFLAG = 0", new Object[] {docs[0].get("a_columnid")});
				if (cols.length == 1)
					column = StringUtils.isBlank((String) cols[0].get("col_name"))?column:(((String) cols[0].get("col_name")) + "(" + cols[0].getDocID() + ")");
			}
			model.put("column", column);
			String columnRel = (String) docs[0].get("a_columnRel");
			String columnRelID = (String) docs[0].get("a_columnRelID");
			if(StringUtils.isBlank(columnRel) || columnRel == null)
				columnRel = "无";
			else{
				String[] columnRels = columnRel.split(",");
				String[] columnRelIDs = columnRelID.split(",");
				columnRel = "";
				for(int i=0;i<columnRels.length; i++){
					if(i == 0)
						columnRel = columnRels[i] + "(" + columnRelIDs[i] + ")";
					else
						columnRel = columnRel + " , " + columnRels[i] + "(" + columnRelIDs[i] + ")";
				}
			}
			model.put("columnRel", columnRel);
			model.put("articleTitle", docs[0].get("a_linktitle"));
		}else{
			model.put("articleTitle", "无");
			model.put("column", "无");
			model.put("columnRel", "无");
		}
		return model;
	}
	/**主界面：广告*/
	@RequestMapping(value = {"MainAd.do"})
	public ModelAndView mainAd(HttpServletRequest request) throws Exception {

		Map<String, Object> model = MainHelper.fillMainModel(request);
		return new ModelAndView("/xy/MainAd", model);
	}

	/**
	 * 主界面：话题，带话题库
	 */
	@RequestMapping(value = { "MainTopic.do" })
	public ModelAndView mainTopic(HttpServletRequest request) throws Exception {

		Map<String, Object> model = MainHelper.fillMainModel(request);

//		int catTypeID = CatTypes.CAT_COLUMNQA.typeID();
//		model.put("catTypeID", catTypeID);

		return new ModelAndView("/xy/MainTopic", model);
	}

	@RequestMapping(value = { "TopicMerge.do" })
	public ModelAndView TopicMerge(HttpServletRequest request) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		int siteID = MainHelper.getSiteEnable(request);
//		long colID = WebUtil.getLong(request, "colID", 0);
//		int colLibID = LibHelper.getColumnLibID(request);
//		int type = WebUtil.getInt(request, "type", 0);
		long docID = WebUtil.getLong(request, "DocIDs", 0);

		int docTypeID = DocTypes.TOPICS.typeID();
		DocLib docLib = LibHelper.getLib(docTypeID, InfoHelper.getTenantCode(request));

//		Column col = colReader.get(colLibID, colID);
//		String colName = (col == null) ? "" : col.getName();
		String rule = "a_siteID_EQ_" + siteID + "_AND_a_status_EQ_0_AND_SYS_DOCUMENTID!_EQ_" + docID;
		int listID = DomHelper.getListID(docTypeID, "话题列表");

//		int userID = ProcHelper.getUserID(request);
//		int userLibID = LibHelper.getUserExtLibID(request);
//		List<Site> sites = userManager.getUserSites(userLibID, userID);

		DomInfo domInfo = new DomInfo();
		domInfo.setDocTypeID(docTypeID);
		domInfo.setRule(rule);
		domInfo.setDocLibID(docLib.getDocLibID());
		domInfo.setFolderID(999886);
		domInfo.setListID(listID);
		domInfo.setListIDs(String.valueOf(listID));

		model.put("siteID", siteID);
//		model.put("sites", sites);
//		model.put("colID", colID);
//		model.put("colName", colName);
//		model.put("ch", ch);
		model.put("domInfo", domInfo);
//		model.put("type", type);
		model.put("UUID",WebUtil.getStringParam(request, "UUID"));
		model.put("docID",docID);
		return new ModelAndView("/xy/TopicMerge", model);
	}

	@RequestMapping(value = { "articleTopic.do" })
	public ModelAndView articleTopic(HttpServletRequest request) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		int siteID = MainHelper.getSiteEnable(request);
		String topicIDString = request.getParameter("topicIDString");
		String topicNameDString = request.getParameter("topicNameDString");
//		long colID = WebUtil.getLong(request, "colID", 0);
//		int colLibID = LibHelper.getColumnLibID(request);
//		int type = WebUtil.getInt(request, "type", 0);
//		long docID = WebUtil.getLong(request, "DocIDs", 0);

		int docTypeID = DocTypes.TOPICS.typeID();
		DocLib docLib = LibHelper.getLib(docTypeID, InfoHelper.getTenantCode(request));

//		Column col = colReader.get(colLibID, colID);
//		String colName = (col == null) ? "" : col.getName();
		String rule = "a_siteID_EQ_" + siteID + "_AND_a_status_EQ_0";
		int listID = DomHelper.getListID(docTypeID, "稿件选择话题列表");

//		int userID = ProcHelper.getUserID(request);
//		int userLibID = LibHelper.getUserExtLibID(request);
//		List<Site> sites = userManager.getUserSites(userLibID, userID);

		DomInfo domInfo = new DomInfo();
		domInfo.setDocTypeID(docTypeID);
		domInfo.setRule(rule);
		domInfo.setDocLibID(docLib.getDocLibID());
		domInfo.setFolderID(docLib.getFolderID());
		domInfo.setListID(listID);
		domInfo.setListIDs(String.valueOf(listID));

		model.put("siteID", siteID);
//		model.put("sites", sites);
//		model.put("colID", colID);
//		model.put("colName", colName);
//		model.put("ch", ch);
		model.put("domInfo", domInfo);
//		model.put("type", type);
//		model.put("UUID",WebUtil.getStringParam(request, "UUID"));
//		model.put("docID",docID);
		model.put("topicIDString",topicIDString);
		model.put("topicNameDString",topicNameDString);
		return new ModelAndView("/xy/SelectTopic", model);
	}

	@RequestMapping(value = "TopicArticle.do")
	public ModelAndView topicArticle(HttpServletRequest request, HttpServletResponse response){
		String tenantCode = InfoHelper.getTenantCode(request);
		int articleTypeID = DocTypes.ARTICLE.typeID();
		List<DocLib> docLibs = LibHelper.getLibs(articleTypeID, tenantCode);
		int listID = DomHelper.getListID(articleTypeID, "话题库话题稿件列表");
        String topicID = request.getParameter("DocIDs");

        DomInfo domInfo = new DomInfo();
        domInfo.setDocTypeID(articleTypeID);
        domInfo.setListID(listID);
        domInfo.setListIDs(String.valueOf(listID));
        domInfo.setQueryID(-1);
        domInfo.setDocLibID(docLibs.get(0).getDocLibID());
        domInfo.setFolderID(999887);
        String rule = "a_topicID_EQ_" + topicID;
        domInfo.setRule(rule);
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("domInfo", domInfo);
        model.put("topicID", topicID);

		return new ModelAndView("/xy/TopicArticle",model);
	}

}
