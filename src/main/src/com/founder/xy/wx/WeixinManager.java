package com.founder.xy.wx;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.Log;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.CacheReader;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.e5.flow.Flow;
import com.founder.e5.flow.FlowNode;
import com.founder.e5.flow.FlowReader;
import com.founder.e5.flow.Operation;
import com.founder.e5.flow.ProcFlow;
import com.founder.e5.flow.ProcReader;
import com.founder.e5.permission.FlowPermissionReader;
import com.founder.e5.permission.PermissionHelper;
import com.founder.e5.sys.org.Role;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.JsonHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.system.site.SiteUserCache;
import com.founder.xy.system.site.SiteUserManager;
import com.founder.xy.wx.data.Account;
import com.founder.xy.wx.data.Menu;

/**
 * 微信处理器
 * @author Gong Lijie
 */
@Service
public class WeixinManager {
	@Autowired
	private SiteUserManager userManager;
	
	/**
	 * 得到微信账号
	 */
	public List<Account> getAccounts(String tCode, int siteID, boolean withMenu, boolean showLinkMenu) {
		List<Account> result = new ArrayList<>();
		try {
			int docLibID = LibHelper.getLibID(DocTypes.WXACCOUNT.typeID(), tCode);
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			Document[] accounts = docManager.find(docLibID, "wxa_siteID=? and SYS_DELETEFLAG=0", new Object[]{siteID});
			for (Document account : accounts) {
				Account ac = new Account(account);
				//是否需要带菜单
				if (withMenu) {
					ac.setMenus(getMenus(docLibID, ac.getId(), showLinkMenu));
				}
				result.add(ac);
			}
		} catch (E5Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public List<Account> getAdminAccounts(String tCode, int userID, int siteID, boolean withMenu, boolean showLinkMenu) throws Exception{
		int userRelLibID = LibHelper.getLibID(DocTypes.USERREL.typeID(), tCode);
		List<Account> result = new ArrayList<>();
		String colIDs = userManager.getRelated(userRelLibID, userID, siteID, 10);
		if(!StringUtils.isBlank(colIDs)) {
			try {
				int docLibID = LibHelper.getLibID(DocTypes.WXACCOUNT.typeID(), tCode);
				DocumentManager docManager = DocumentManagerFactory.getInstance();
				Document[] accounts = docManager.find(docLibID, 
						"wxa_siteID=? and SYS_DELETEFLAG=0 and SYS_DOCUMENTID in (" + colIDs + ")", 
						new Object[]{siteID});
				for (Document account : accounts) {
					Account ac = new Account(account);
					result.add(ac);
					if (withMenu) {
						ac.setMenus(getMenus(docLibID, ac.getId(), showLinkMenu));
					}
				}
			} catch (E5Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	/**
	 * 得到指定的微信账号
	 * @return
	 */
	public Account getAccount(int docLibID, long docID) {
		try {
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			Document doc = docManager.get(docLibID, docID);
			
			return new Account(doc);
		} catch (E5Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 得到指定的文档对象
	 * @return
	 */
	public Document getDocument(int docLibID, long docID) {
		try {
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			Document doc = docManager.get(docLibID, docID);
			
			return doc;
		} catch (E5Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 得到指定的微信菜单。用于发布
	 * @return
	 */
	public Menu getMenu(int docLibID, long docID) {
		try {
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			Document doc = docManager.get(docLibID, docID);
			
			return new Menu(doc);
		} catch (E5Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 根据微信账号ID得到账号的微信菜单
	 */
	public List<Menu> getMenus(int accountLibID, long accountID, boolean showAll) {
		List<Menu> result = new ArrayList<>();
		
		int docLibID = LibHelper.getLibIDByOtherLib(DocTypes.WXMENU.typeID(), accountLibID);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			Document[] menus = docManager.find(docLibID, "wxm_accountID=? order by wxm_parentID, wxm_order", 
					new Object[]{accountID});
			
			for (Document menuDoc : menus) {
				int parentID = menuDoc.getInt("wxm_parentID");
				Menu menu = new Menu(menuDoc);
				
				if (parentID == 0) {
					//一级菜单：显示有二级菜单的
					if (showAll || StringUtils.isBlank(menuDoc.getString("wxm_type")))
						result.add(menu);
				} else {
					//二级菜单：显示有菜单稿件的
					if (showAll || menuDoc.getString("wxm_type").equals("view0")) {
						Menu parent = findParent(result, parentID);
						if (parent != null)
							parent.addChild(menu);
					}
				}
			}
		} catch (E5Exception e) {
			e.printStackTrace();
		}
		//把没带子的一级菜单再删掉
		if (!showAll) {
			for (int i = 0; i < result.size(); i++) {
				Menu menu = result.get(i);
				if (menu.getChildren().size() == 0)
					result.remove(i--);
			}
		}
		return result;
	}
	private Log log = Context.getLog("xy.weixin");

	/**
	 * 上传图文素材
	 */
	public String groupMaterialUpload(Document group, String data) {
		//读账号
		int accountLibID = LibHelper.getLibIDByOtherLib(DocTypes.WXACCOUNT.typeID(), group.getDocLibID());
		Account account = getAccount(accountLibID, group.getLong("wxg_accountID"));
		if (account == null) return "找不到账号！";

		String result;
		try {
			//上传，得到mediaID
			JSONObject datajson = JSONObject.fromObject(data);
			String datalist = datajson.getString("list");
			
			result =  WeixinUtil.groupMaterialUpload(account, datalist);
			String mediaID = JSONObject.fromObject(result).getString("media_id");

			JSONObject json = JSONObject.fromObject(result);
			if (json.getInt("errcode") == 0) {
				//发布成功，则更新数据库状态
				group.set("wxg_mediaID", mediaID); //图文消息的mediaID
				group.set("wxg_status", 5); //已上传
				DocumentManager docManager = DocumentManagerFactory.getInstance();
				docManager.save(group);
				result = "ok";
			} else {
				result = json.getString("errmsg");
			}
		} catch (Exception e) {
			log.error("【微信图文消息发布】", e);
			result = e.getMessage();
		}
		return result;
	}
	
	/**
	 * 发布消息
	 */
	public String groupPublish(long accountID, int groupLibID, long groupID, String data){
		//读账号
		int accountLibID = LibHelper.getLibIDByOtherLib(DocTypes.WXACCOUNT.typeID(), groupLibID);
		Account account = getAccount(accountLibID, accountID);
		if (account == null) return "找不到账号！";
		
		String result;
		try {
			//上传，得到mediaID
			result =  WeixinUtil.groupUpload(account, data);
			String mediaID = JSONObject.fromObject(result).getString("media_id");

			//发布
			result = WeixinUtil.groupPublish(account, mediaID);

			JSONObject json = JSONObject.fromObject(result);
			if (json.getInt("errcode") == 0) {
				//发布成功，则更新数据库状态
				DocumentManager docManager = DocumentManagerFactory.getInstance();
				Document group = docManager.get(groupLibID, groupID);
				changeStatus(group, mediaID, 3);
				docManager.save(group);
				result = "ok";
			} else {
				result = json.getString("errmsg");
			}
		} catch (Exception e) {
			log.error("【微信图文消息发布】", e);
			result = e.getMessage();
		}
		return result;
	}

	/**
	 * 预览消息
	 */
	public String groupPreview(long accountID, int groupLibID, long groupID, String data, String wxName){
		//读账号
		int accountLibID = LibHelper.getLibIDByOtherLib(DocTypes.WXACCOUNT.typeID(), groupLibID);
		Account account = getAccount(accountLibID, accountID);
		if (account == null) return "找不到账号！";
		
		String result;
		try {
			//上传，得到mediaID
			result =  WeixinUtil.groupUpload(account, data);
			String mediaID = JSONObject.fromObject(result).getString("media_id");

			//再预览
			WeixinUtil.groupPreview(account, mediaID, wxName);

			result = "ok";
			
		} catch (Exception e) {
			log.error("【微信图文消息预览】", e);
			result = e.getMessage();
		}
		return result;
	}

	/**
	 * 删除图文素材（取消上传）
	 */
	public String groupMaterialDelete(int groupLibID, long groupID){
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		String result;
		try {
			Document group = docManager.get(groupLibID, groupID);
			int accountID = group.getInt("wxg_accountID");
			String mediaID = group.getString("wxg_mediaID");
			if (StringUtils.isBlank(mediaID))
				return "上传后的素材ID空";
			
			result = groupMaterialDelete(groupLibID, accountID, mediaID);
			JSONObject json = JSONObject.fromObject(result);
			if (json.getInt("errcode") == 0) {
				group.set("wxg_mediaID", mediaID); //图文消息的mediaID
				group.set("wxg_status", 2); //未发布
				docManager.save(group);
				result = "ok";
			} else {
				result = json.getString("errmsg");
			}
		} catch (E5Exception e) {
			log.error(e);
			return e.getLocalizedMessage();
		}
		return result;
	}
	
	/**
	 * 撤回消息
	 */
	public String groupRevoke(int groupLibID, long groupID){
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			Document group = docManager.get(groupLibID, groupID);
			int accountID = group.getInt("wxg_accountID");
			String mediaID = group.getString("wxg_mediaID");
			if (StringUtils.isBlank(mediaID))
				return "没有mediaID";
			
			return revokeGroup(groupLibID, accountID, mediaID);
		} catch (E5Exception e) {
			log.error(e);
			return e.getLocalizedMessage();
		}
	}

	/**
	 * 删除消息
	 */
	public String groupDelete(int groupLibID, long groupID){
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			Document group = docManager.get(groupLibID, groupID);
			if (group == null) return null;
			
			//已发布，则需要撤回
			if (group.getInt("wxg_status") == 1) {
				int accountID = group.getInt("wxg_accountID");
				String mediaID = group.getString("wxg_mediaID");
				
				String result = revokeGroup(groupLibID, accountID, mediaID);
				if (result != null) return result;
			}
			
			//正常撤回后，删除数据库记录
			docManager.delete(group);
			//删除图文稿件
			DocLib gaLib = LibHelper.getLib(DocTypes.WXGROUPARTICLE.typeID(), 
					LibHelper.getTenantCodeByLib(groupLibID));
			String sql = "delete from " + gaLib.getDocLibTable() + " where wx_groupID=?";
			InfoHelper.executeUpdate(gaLib.getDocLibID(), sql, new Object[]{groupID});
			
			return null;
		} catch (E5Exception e) {
			log.error(e);
			return e.getLocalizedMessage();
		}
	}

	/**
	 * 更新一个用户下的所有菜单,会将原有菜单全部删除
	 */
	public String saveMenus(int accountLibID, int accountID, String menu) {
		//读出账号
		Account account = getAccount(accountLibID, accountID);
		
		//把提交的json解析成对象
		List<Menu> menus = parseMenus(account, menu);
		
		//菜单先保存到数据库
		saveMenus(account, menus);
		
		//再发布到微信
		return publishMenus(account, menus);
	}

	private void changeStatus(Document group, String mediaID, int status) throws E5Exception {
		//上传或发布后改变流程状态
		group.set("wxg_mediaID", mediaID); //图文消息的mediaID
		group.set("wxg_status", status); //已发布 或 已上传
		FlowReader flowReader = (FlowReader) Context.getBean(FlowReader.class);
		FlowNode node = flowReader.getNextFlowNode(group.getCurrentNode());
		group.setCurrentNode(node.getID());
		group.setCurrentStatus(node.getWaitingStatus());
	}

	//删除已上传的图文素材
	private String groupMaterialDelete(int groupLibID, int accountID, String mediaID) {
		//读账号
		int accountLibID = LibHelper.getLibIDByOtherLib(DocTypes.WXACCOUNT.typeID(), groupLibID);
		Account account = getAccount(accountLibID, accountID);
		if (account == null) return "找不到账号！";
			
		String result;
		try {
			result = WeixinUtil.groupMaterialDelete(account, mediaID);
		} catch (Exception e) {
			log.error("【微信图文素材删除】", e);
			return e.getMessage();
		}
		JSONObject jsonObj = JSONObject.fromObject(result);
		int errcode = JsonHelper.getInt(jsonObj, "errcode");
		if (errcode != 0) {
			return JsonHelper.getString(jsonObj, "errmsg");
		}
		return null;
	}
	
	//撤回已发布的图文消息
	private String revokeGroup(int groupLibID, int accountID, String mediaID) {
		//读账号
		int accountLibID = LibHelper.getLibIDByOtherLib(DocTypes.WXACCOUNT.typeID(), groupLibID);
		Account account = getAccount(accountLibID, accountID);
		if (account == null) return "找不到账号！";
		
		String result;
		try {
			result = WeixinUtil.groupDelete(account, mediaID);
		} catch (Exception e) {
			log.error("【微信图文消息删除】", e);
			return e.getMessage();
		}
		JSONObject jsonObj = JSONObject.fromObject(result);
		int errcode = JsonHelper.getInt(jsonObj, "errcode");
		if (errcode != 0) {
			return JsonHelper.getString(jsonObj, "errmsg");
		}
		return null;
	}

	private void saveMenus(Account account, List<Menu> menus) {
		List<Menu> oldMenus = getMenus(account.getLibID(), account.getId(), true);
		
		saveMenus(account, menus, oldMenus);
	}

	private void saveMenus(Account account, List<Menu> newMenus, List<Menu> oldMenus) {
		int menuLibID = LibHelper.getLibIDByOtherLib(DocTypes.WXMENU.typeID(), account.getLibID());
		for (Menu newMenu : newMenus) {
			//找到旧菜单
			int oldIndex = findOldMenu(oldMenus, newMenu);
			if (oldIndex >= 0) {
				Menu oldMenu = oldMenus.get(oldIndex);
				//若有修改，则保存旧菜单
				if (changed(oldMenu, newMenu)) {
					updateMenu(oldMenu, newMenu);
				}
				//比较并保存二级菜单
				saveMenus(account, newMenu.getChildren(), oldMenu.getChildren());

				//处理后remove，这样最后剩下的就是没用的
				oldMenus.remove(oldIndex);
			} else {
				//创建新菜单
				createMenu(menuLibID, account, null, newMenu);
				//新菜单的子菜单
				for (Menu subMenu : newMenu.getChildren()) {
					createMenu(menuLibID, account, newMenu, subMenu);
				}
			}
		}
		
		//剩余的旧菜单，要删除
		for (Menu oldMenu : oldMenus) {
			deleteMenu(menuLibID, oldMenu);
		}
	}
	
	//找到旧菜单，返回index
	private int findOldMenu(List<Menu> oldMenus, Menu newMenu) {
		if (newMenu.getId() <= 0 || oldMenus.isEmpty()) return -1;
		
		for (int i = 0; i < oldMenus.size(); i++) {
			if (oldMenus.get(i).getId() == newMenu.getId())
				return i;
		}
		return -1;
	}
	
	private List<Menu> parseMenus(Account account, String json){
		JSONObject _menu = JSONObject.fromObject(json);
		JSONArray buttons = _menu.getJSONArray("button");
	
		List<Menu> menus = new ArrayList<Menu>();
		for (int i = 0; i < buttons.size(); i++) {
			JSONObject btn = buttons.getJSONObject(i);
			
			Menu menu = parseMenu(account, btn, null);
			menu.setOrder(i);
			
			//子菜单
			String parentName = btn.getString("name");
			JSONArray subs = btn.getJSONArray("sub_button");
			
			for (int j = 0; j < subs.size(); j++) {
				JSONObject sub = subs.getJSONObject(j);
				
				Menu subMenu = parseMenu(account, sub, parentName);
				subMenu.setOrder(j);
				subMenu.setParentID(menu.getId());
				
				menu.addChild(subMenu);
			};
			
			menus.add(menu);
		}
		return menus;
	}

	private Menu parseMenu(Account account, JSONObject btn, String parentName) {
		String type = JsonHelper.getString(btn, "type");
		String name = JsonHelper.getString(btn, "name");
		int id = JsonHelper.getInt(btn, "id");
		
		Menu menu = new Menu();
		menu.setName(name);
		menu.setType(type);
		menu.setId(id);
		
		if ("click".equals(type)){
			menu.setKey(JsonHelper.getString(btn, "key"));
		} else if ("view".equals(type)){
			menu.setUrl(JsonHelper.getString(btn, "url"));
		} else if ("view0".equals(type)){
			if (id > 0) changeViewUrl(account, menu); //按menuID生成菜单url
		}
		return menu;
	}

	private void createMenu(int menuLibID, Account account, Menu parentMenu, Menu newMenu) {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			long menuID = InfoHelper.getNextDocID(DocTypes.WXMENU.typeID());
			newMenu.setId(menuID);
			//可能需要改url
			changeViewUrl(account, newMenu);
			
			Document menu = docManager.newDocument(menuLibID, menuID);
			menu.setLastmodified(DateUtils.getTimestamp());
			
			menu.set("wxm_parentID", parentMenu == null ? newMenu.getParentID() : parentMenu.getId());
			menu.set("wxm_accountID", account.getId());
			
			menu.set("wxm_name", newMenu.getName());
			menu.set("wxm_type", newMenu.getType());
			menu.set("wxm_order", newMenu.getOrder());
			menu.set("wxm_url", newMenu.getUrl());
			menu.set("wxm_key", newMenu.getKey());
			
			docManager.save(menu);
		} catch (E5Exception e) {
			e.printStackTrace();
		}
	}

	private void updateMenu(Menu oldMenu, Menu newMenu) {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			Document menu = docManager.get(oldMenu.getLibID(), oldMenu.getId());
			menu.setLastmodified(DateUtils.getTimestamp());
			
			menu.set("wxm_name", newMenu.getName());
			menu.set("wxm_type", newMenu.getType());
			menu.set("wxm_order", newMenu.getOrder());
			menu.set("wxm_url", newMenu.getUrl());
			menu.set("wxm_key", newMenu.getKey());
			
			docManager.save(menu);
		} catch (E5Exception e) {
			e.printStackTrace();
		}
	}
	
	private void deleteMenu(int menuLibID, Menu oldMenu) {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			docManager.delete(menuLibID, oldMenu.getId());
		} catch (E5Exception e) {
			e.printStackTrace();
		}
	}
	
	//比较菜单是否有修改：名称、类型、顺序、链接地址、点击推key
	private boolean changed(Menu oldMenu, Menu newMenu) {
		return oldMenu.getOrder() != newMenu.getOrder()
				|| !equals(oldMenu.getName(), newMenu.getName())
				|| !equals(oldMenu.getType(), newMenu.getType())
				|| "view".equals(oldMenu.getType()) && !equals(oldMenu.getUrl(), newMenu.getUrl())
				|| "click".equals(oldMenu.getType()) && !equals(oldMenu.getKey(), newMenu.getKey())
				;
	}
	//菜单显示稿件列表时，自动生成url
	private void changeViewUrl(Account account, Menu menu) {
		if ("view0".equals(menu.getType())) {
			String url = account.getDir() + "/" + menu.getId() + ".html";
			menu.setUrl(url);
		}
	}
	private boolean equals(String v0, String v1) {
		//都为空
		if (StringUtils.isBlank(v0) && StringUtils.isBlank(v1)) return true;
		
		return !StringUtils.isBlank(v0) && v0.equals(v1);
	}
 	
	private String publishMenus(Account account, List<Menu> menus) {
		//to json
		JSONArray buttons = new JSONArray();
		for (Menu menu : menus) {
			buttons.add(menu.json4Publish());
		}
		String json = "{\"button\":" + buttons.toString() + "}";
		
		//publish
		try {
			WeixinAPI.createMenu(account, json);
		} catch (Exception e) {
			return "{\"status\":\"fail\", \"message\":\"" + e.getMessage() + "\"}";
		}
		return "{\"status\":\"success\"}";
	}

	private Menu findParent(List<Menu> list, int parentID) {
		for (Menu menu : list) {
			if (menu.getId() == parentID)
				return menu;
		}
		return null;
	}
	
	/**
	 * 查询微信公众号列表
	 */
	public Document[] find(int siteID, String name, String tCode) throws E5Exception {
		int docLibID = LibHelper.getLibID(DocTypes.WXACCOUNT.typeID(), tCode);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] accounts = docManager.find(docLibID,
				"wxa_siteID=? and wxa_name like ? and SYS_DELETEFLAG=0", new Object[] { siteID,
						name + "%" });
		return accounts;
	}

	/**
	 * 设置流程  驳回设置为最后一个流程  其他设置为下一个流程
	 * type： 1审核通过  2驳回
	 */
	public void setNextFlow(Document document, int type) {
		try {
			FlowReader flowReader = (FlowReader) Context.getBean(FlowReader.class);
//			FlowNode node = flowReader.getNextFlowNode(document.getCurrentNode());
//			document.setCurrentNode(node.getID());
//			document.setCurrentStatus(node.getWaitingStatus());
			FlowNode currNode = flowReader.getFlowNode(document.getCurrentNode());
			FlowNode[] nodes = flowReader.getFlowNodes(currNode.getFlowID());
			FlowNode nextNode = type == 2 ? nodes[nodes.length-1] 
					: flowReader.getNextFlowNode(currNode.getID());
			document.setCurrentFlow(nextNode.getFlowID());
			document.setCurrentNode(nextNode.getID());
			document.setCurrentStatus(nextNode.getWaitingStatus());
			document.setLastmodified(new Timestamp((new Date()).getTime()));
		} catch (E5Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 给微信图文组中的图文稿件设置状态  用于图文组的 审核通过 和 驳回
	 * @param groupArticles 微信图文组中的图文稿件
	 * @param status 状态
	 * @throws E5Exception 
	 */
	public void setAllStatus(DocumentManager docManager, Document[] groupArticles, 
			int status) throws E5Exception {
		for (Document document : groupArticles) {
			document.set("wx_status", status);
			docManager.save(document);
		}
	}
	
	/**
	 * 判断微信图文组中的稿件 是否全部是指定状态
	 * @param groupArticles 微信图文组中的图文稿件
	 * @param status 状态
	 * @return
	 */
	public boolean isAllSameStatus(Document[] groupArticles, int status) {
		boolean isAllpub = true;
		for (Document doc : groupArticles) {
			if(doc.getInt("wx_status") != status){
				isAllpub = false;
				break;
			} 
		}
		return isAllpub;
	}

	public String getGroupListDetail(String sql, int page, int paramCount) {
		JSONArray array = new JSONArray();
		JSONObject rtnJson = new JSONObject();
		
        DBSession db = null;
        IResultSet rs = null;
        DBSession countdb = null;
        IResultSet countrs = null;
        try {
            db = Context.getDBSession();
            String countSql = "SELECT COUNT(*) " + sql.substring(sql.indexOf("FROM"));
            sql = db.getDialect().getLimitString(sql, (page - 1) * paramCount, paramCount);
            rs = db.executeQuery(sql, null);
            while (rs.next()) {
                JSONObject json = new JSONObject();
                json.put("materialID", rs.getInt("SYS_DOCUMENTID"));
                json.put("materialLibID", rs.getInt("SYS_DOCLIBID"));
                json.put("title", StringUtils.getNotNull(rs.getString("SYS_TOPIC")));
                json.put("createTime", StringUtils.getNotNull(rs.getString("SYS_CREATED")));
                json.put("author", StringUtils.getNotNull(rs.getString("SYS_AUTHORS")));
                json.put("lastPerson", StringUtils.getNotNull(rs.getString("SYS_CURRENTUSERNAME")));
                json.put("url", StringUtils.getNotNull(rs.getString("wx_url")));
                json.put("abstract", StringUtils.getNotNull(rs.getString("wx_abstract")));
                json.put("checkID", StringUtils.getNotNull(rs.getString("wx_articleID")));
                json.put("checkLibID", StringUtils.getNotNull(rs.getString("wx_articleLibID")));
                json.put("pic", StringUtils.getNotNull(rs.getString("wx_pic")));
                json.put("belongto", StringUtils.getNotNull(rs.getString("wx_belongto")));
                array.add(json);
            }
            int materialCount = 0;
            countdb = Context.getDBSession();
			countrs = countdb.executeQuery(countSql);
			if (countrs.next()) {
				materialCount = countrs.getInt(1);
            }
			int pagecount = materialCount/paramCount;
			if(materialCount%paramCount > 0) pagecount++;
			rtnJson.put("page", page);
			//rtnJson.put("pagesize", paramCount);
			rtnJson.put("pagecount", pagecount);
			rtnJson.put("list", array);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            ResourceMgr.closeQuietly(rs);
            ResourceMgr.closeQuietly(db);
            ResourceMgr.closeQuietly(countrs);
            ResourceMgr.closeQuietly(countdb);
        }
        return rtnJson.toString();
	}

	/**
	 * 微信图文组 添加当前流程下 有操作权限的流程节点
	 */
	public void setFlowPower(Document group, int groupLibID,
			Map<String, Object> model, int userID, int siteID) {
		int userLibID = LibHelper.getUserExtLibID();
		Role[] roles = getRolesBySite(userLibID, userID, siteID);
        try {
			//按角色个数，单个取角色ID，多个取合并后的角色ID
			int roleID = roles[0].getRoleID();
			int newRoleID = (roles.length == 1) ? roleID : PermissionHelper.mergeRoles(roles);
			FlowReader flowReader = (FlowReader) Context.getBean(FlowReader.class);
			FlowNode currNode = null;
			if(group == null){
				Flow[] flows = flowReader.getFlows(DocTypes.WXGROUP.typeID());
				int firstFlowNodeID = flows[0].getFirstFlowNodeID();
				currNode = flowReader.getFlowNode(firstFlowNodeID);
			}else{
				currNode = flowReader.getFlowNode(group.getCurrentNode());
			}
			ProcReader procReader = (ProcReader)Context.getBean(ProcReader.class);
			ProcFlow[] procs = procReader.getProcs(currNode.getID());
			FlowPermissionReader fpReader = (FlowPermissionReader)Context.getBean(FlowPermissionReader.class);
			for(ProcFlow proc : procs){
				Operation op = procReader.getOperation(proc.getOpID());
				if(op.getName().equals("送审")) {
					boolean isCensorship = fpReader.hasPermission(newRoleID, currNode.getFlowID(), 
							currNode.getID(), proc.getProcName());
					model.put("isCensorship", isCensorship);
				}else if(op.getName().equals("审核通过")) {
					boolean isCensorshipThrough = fpReader.hasPermission(newRoleID, currNode.getFlowID(), 
							currNode.getID(), proc.getProcName());
					model.put("isCensorshipThrough", isCensorshipThrough);
				}else if(op.getName().equals("驳回")) {
					boolean isReback = fpReader.hasPermission(newRoleID, currNode.getFlowID(), 
							currNode.getID(), proc.getProcName());
					model.put("isReback", isReback);
				}
			}
		} catch (E5Exception e) {
			e.printStackTrace();
		}
        
	}
	
	private Role[] getRolesBySite(int userLibID, int userID, int siteID) {
		SiteUserCache siteUserCache = (SiteUserCache)(CacheReader.find(SiteUserCache.class));
		int[] roleIDs =  siteUserCache.getRoles(userLibID, userID, siteID);
		if (roleIDs == null || roleIDs.length == 0) return null;

		Role[] roles = new Role[roleIDs.length];
		for (int i = 0; i < roles.length; i++) {
			roles[i] = new Role();
			roles[i].setRoleID(roleIDs[i]);
		}
		return roles;
	}
	
	/**
	 * 读图文稿件
	 */
	public Document[] getArticles(int groupLibID, long groupID) {
		int groupArticleTypeID = DocTypes.WXGROUPARTICLE.typeID();
		int groupArticleLibID = LibHelper.getLibIDByOtherLib(groupArticleTypeID, groupLibID);
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			Document[] docs = docManager.find(groupArticleLibID, "wx_groupID=? order by SYS_DOCUMENTID", 
					new Object[]{groupID});
			return docs;
		} catch (E5Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
