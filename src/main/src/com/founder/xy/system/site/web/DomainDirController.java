package com.founder.xy.system.site.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.founder.e5.commons.Pair;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.doc.util.E5docHelper;
import com.founder.e5.web.WebUtil;
import com.founder.e5.workspace.app.form.FormSaver;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.jms.PublishTrigger;
import com.founder.xy.jms.data.DocIDMsg;
import com.founder.xy.system.Tenant;
import com.founder.xy.system.site.DomainDirManager;

/**
 * 域名目录功能
 * @author Deng Chaochen
 */
@Controller
@RequestMapping("/xy/site")
public class DomainDirController {

	@Autowired
	private DomainDirManager domaindirManager;

	private String[] dirnames = {"column", "content", "pic", "attachment",
			"template", "block", "resource", "vote"};

	//获取某个站点的所有域名目录
	@RequestMapping(value = "DomainDir.do")
	public void tree(HttpServletRequest request, HttpServletResponse response,
			@RequestParam int siteID) throws Exception {

		Document[] domaindirs = domaindirManager.getDomainDir(siteID);

		JSONArray dirs = getJSONArr(domaindirs);

		InfoHelper.outputJson(dirs.toString(), response);
	}
	
	/** 域名目录（第一层以及子层）  异步加载域名树方法*/
	@RequestMapping(value = "DomainDirByPId.do", params = "parentID")
	public void tree(HttpServletRequest request, HttpServletResponse response,
			@RequestParam int siteID, @RequestParam int parentID) throws Exception {
		
		Document[] domaindirs = null;
		domaindirs = domaindirManager.getDomainDirByPId(siteID,parentID);
		JSONArray dirs = getJSONArr(domaindirs);

		InfoHelper.outputJson(dirs.toString(), response);
		
	}
	
	@RequestMapping(value = "SiteInit.do")
	public void menuInit(HttpServletRequest request, HttpServletResponse response, int siteId) throws Exception {
		int docLibID = LibHelper.getDomainDirLibID();
		JSONObject json = domaindirManager.getResourceDir(siteId);
		//String result = "{\"docLibID\":\"" + docLibID + "\"}";
		json.accumulate("docLibID", docLibID);
		InfoHelper.outputJson(json.toString(), response);
	}

	/**保存目录后，自己处理保存后的目录选项*/
	@RequestMapping(value = "FormSave.do")
	public ModelAndView formSave(HttpServletRequest request, HttpServletResponse response) throws Exception {

		Map<String, Object> model = new HashMap<String, Object>();
		Document parent = null; // parent for subs.
		try {
			parent = save(request);
		} catch (Exception e) {
			model.put("error", e.getLocalizedMessage());
			return new ModelAndView("/xy/site/Submit", model);
		}

		String id = request.getParameter("DocID");
		boolean isNew = (StringUtils.isBlank(id) || "0".equals(id));

		long docID = parent.getDocID();

		String dirUrl = request.getParameter("dir_url");
		String dirName = "";
		if(!StringUtils.isBlank(dirUrl)){
			dirName = request.getParameter("dir_name") + " ( " + dirUrl + " ) ";
		}
		else{
			dirName = request.getParameter("dir_name");
		}

		//若是新建的是域名目录，则自动添加常用的几个子目录
		String[] subDirs = null;
		boolean isNewRoot = false;
		if (isNew && !StringUtils.isBlank(dirUrl)) {
			subDirs = autoAddsubDir(parent);
			if (subDirs != null) {
				isNewRoot = true;
			}
		}

		//通知发布服务
		PublishTrigger.otherData(parent.getDocLibID(), parent.getDocID(), DocIDMsg.TYPE_DOMAINDIR);

		if (isNewRoot) {
			model.put("subIDs", subDirs[0]);
			model.put("subNames", subDirs[1]);
		}

		//返回
		model.put("dirID", docID);
		model.put("dirName", dirName);
		model.put("parentID", request.getParameter("dir_parentID"));
		model.put("isNew", isNew);
		model.put("isNewRoot", isNewRoot);

		return new ModelAndView("/xy/site/Submit", model);
	}

	/**判断域名目录是否被引用*/
	@RequestMapping(value = "DomainDirIsUsed.do")
	public void domainDirIsUsed(HttpServletRequest request, @RequestParam long dirID, HttpServletResponse response) throws Exception {
		try {
			String result = "no";
			boolean bUsedforRule = domaindirManager.dirUsedforRule(dirID);
			boolean bUsedforBlock = domaindirManager.dirUsedforBlock(dirID);
			boolean bUsedforRes = domaindirManager.dirUsedforRes(dirID);
			if((!bUsedforRule) && (!bUsedforBlock) && (!bUsedforRes)){
				result = "ok";
			}
	
			InfoHelper.outputText(result, response);
		} catch (Exception e) {
			InfoHelper.outputText(e.getLocalizedMessage(), response);
		}
	}

	/**目录删除*/
	@RequestMapping(value = "Deletedir.do")
	public void delete(HttpServletRequest request, @RequestParam long dirID, HttpServletResponse response) throws Exception {
		try {
			int docLibID = LibHelper.getLibID(DocTypes.DOMAINDIR.typeID(), Tenant.DEFAULTCODE);
			domaindirManager.delete(docLibID, dirID);
	
			InfoHelper.outputText("ok", response);
		} catch (Exception e) {
			InfoHelper.outputText(e.getLocalizedMessage(), response);
		}
	}

	/** 作为资源目录 */
	@RequestMapping(value = "resdir.do")
	public void resDir(HttpServletRequest request, @RequestParam long dirID, int siteID, HttpServletResponse response) {
	    try {
	        JSONObject json = domaindirManager.resDir(dirID, siteID);
	        InfoHelper.outputJson(json.toString(), response);
	    } catch (Exception e) {
	        InfoHelper.outputText(e.getLocalizedMessage(), response);
	    }
	}

	/** 域名切换 */
	@RequestMapping(value = "Switch.do")
	public ModelAndView domainSwitch(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> model = new HashMap<String, Object>();
		
		Document domain = getDocument(request);
		if (domain != null) {
			model.put("url", domain.getString("dir_url"));
		}
		
		return new ModelAndView("/xy/site/DomainSwitch", model);
	}

	/** 
	 * 域名切换提交
	 * 域名改变后，自动修改四个方面：
	 * 修改其下子目录的URL、
	 * 修改发布规则中引用了的这些目录的Url、
	 * 修改引用了这些目录的区块和公共资源的Url。
	 * 
	 * 若旧稿件也要使用新域名，需手工重新发布。 
	 */
	@RequestMapping(value = "SwitchSubmit.do")
	public ModelAndView domainSwitchSubmit(HttpServletRequest request, HttpServletResponse response) {
		String newUrl = WebUtil.get(request, "dir_url"); //新域名
		Document domain = getDocument(request);
		Object[] params = getSwitchParams(domain, newUrl);

		DBSession conn = null;
		try {
			conn = Context.getDBSession();
			conn.beginTransaction();

			//域名替换：修改发布规则
			changePubRules(domain, newUrl, params, conn);
			
			//域名替换：修改区块
			changeBlocks(domain, newUrl, params, conn);
			
			//域名替换：修改公共资源
			changeResources(domain, newUrl, params, conn);

			//域名替换：最后修改域和子目录的Url
			changeDomainDir(domain, newUrl, params, conn);
			
			conn.commitTransaction();
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			e.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
		
		//发消息，通知发布服务以及其它web
		int ruleLibID = LibHelper.getLibIDByOtherLib(DocTypes.SITERULE.typeID(), domain.getDocLibID());
		int blockLibID = LibHelper.getLibIDByOtherLib(DocTypes.BLOCK.typeID(), domain.getDocLibID());
		PublishTrigger.otherData(domain.getDocLibID(), domain.getDocID(), DocIDMsg.TYPE_DOMAINDIR);
		PublishTrigger.otherData(ruleLibID, 0, DocIDMsg.TYPE_SITERULE);
		PublishTrigger.blockChanged(blockLibID, 0);

		//返回
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("refresh", true);
		return new ModelAndView("/xy/site/Submit", model);
	}
	//域名替换：把发布规则中涉及到的域或子目录的Url替换成新地址
	private void changePubRules(Document domain, String newUrl, Object[] params, DBSession conn) throws E5Exception {
		String tableName = LibHelper.getLibTable(
				LibHelper.getLibIDByOtherLib(DocTypes.SITERULE.typeID(), domain.getDocLibID()));

		//发布规则中涉及到所有子目录的，改url
		String[] sqlRules = new String[]{
				"update " + tableName + " set rule_column_dir=REPLACE(rule_column_dir,?,?) where rule_siteID=? and rule_column_dir like ?",
				"update " + tableName + " set rule_article_dir=REPLACE(rule_article_dir,?,?) where rule_siteID=? and rule_article_dir like ?",
				"update " + tableName + " set rule_photo_dir=REPLACE(rule_photo_dir,?,?) where rule_siteID=? and rule_photo_dir like ?",
				"update " + tableName + " set rule_attach_dir=REPLACE(rule_attach_dir,?,?) where rule_siteID=? and rule_attach_dir like ?",
		};
		for (String sqlRule : sqlRules) {
			InfoHelper.executeUpdate(sqlRule, params, conn);
		}
		
		//发布规则中涉及到所有域本身的，改url
		sqlRules = new String[]{
				"update " + tableName + " set rule_column_dir=? where rule_column_dir_ID=?",
				"update " + tableName + " set rule_article_dir=? where rule_article_dir_ID=?",
				"update " + tableName + " set rule_photo_dir=? where rule_photo_dir_ID=?",
				"update " + tableName + " set rule_attach_dir=? where rule_attach_dir_ID=?",
		};
		params = new Object[] {newUrl, domain.getDocID()};
		for (String sqlRule : sqlRules) {
			InfoHelper.executeUpdate(sqlRule, params, conn);
		}
	}
	
	//域名替换：页面区块
	private void changeBlocks(Document domain, String newUrl, Object[] params, DBSession conn) throws E5Exception {
		String tableName = LibHelper.getLibTable(
				LibHelper.getLibIDByOtherLib(DocTypes.BLOCK.typeID(), domain.getDocLibID()));
		String sql = "update " + tableName + " set b_dir=REPLACE(b_dir,?,?) where b_siteID=? and b_dir like ?";
		InfoHelper.executeUpdate(sql, params, conn);
		
		sql = "update " + tableName + " set b_dir=? where b_dir_ID=?";
		params = new Object[] {newUrl, domain.getDocID()};
		InfoHelper.executeUpdate(sql, params, conn);
		
	}
	
	//域名替换：公共资源
	private void changeResources(Document domain, String newUrl, Object[] params, DBSession conn) throws E5Exception {
		String tableName = LibHelper.getLibTable(
				LibHelper.getLibIDByOtherLib(DocTypes.RESOURCE.typeID(), domain.getDocLibID()));
		String sql = "update " + tableName + " set res_dir=REPLACE(res_dir,?,?) where res_siteID=? and res_dir like ?";
		InfoHelper.executeUpdate(sql, params, conn);

		sql = "update " + tableName + " set res_dir=? where res_dir_ID=?";
		params = new Object[] {newUrl, domain.getDocID()};
		InfoHelper.executeUpdate(sql, params, conn);
	}
	
	//域名替换：修改域和子目录的Url
	private void changeDomainDir(Document domain, String newUrl, Object[] params, DBSession conn) throws E5Exception {
		String sqlDomain = "update " + LibHelper.getLibTable(domain.getDocLibID())
				+ " set dir_url=REPLACE(dir_url,?,?) where dir_siteID=? and dir_url like ?";
		
		//域下的所有子目录，改url
		InfoHelper.executeUpdate(sqlDomain, params, conn);
		
		//域本身，改url
		domain.set("dir_url", newUrl);
		DocumentManagerFactory.getInstance().save(domain, conn);
	}

	//域名替换：组织参数（旧域名前缀、新域名前缀、站点ID、域名匹配串）
	private Object[] getSwitchParams(Document domain, String newUrl) {
		String oldUrl = domain.getString("dir_url"); //旧域名
		
		String url0 = oldUrl.endsWith("/") ? oldUrl : oldUrl + "/";
		String url1 = newUrl.endsWith("/") ? newUrl : newUrl + "/";
		
		Object[] params = new Object[] {url0, url1, domain.getInt("dir_siteID"), url0 + "%"};
		return params;
	}

	private Document getDocument(HttpServletRequest request) {
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		long docID = WebUtil.getLong(request, "DocIDs", 0);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			return docManager.get(docLibID, docID);
		} catch (E5Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private JSONArray getJSONArr(Document[] domaindirs){
	
		JSONArray dirs = new JSONArray();
	
		for (Document domaindir : domaindirs) {
			Map<String, String> json = new HashMap<String, String>();
	
			json.put("id", String.valueOf(domaindir.getDocID()));
			if(domaindir.getInt("dir_parentID") == 0){
				StringBuilder result = new StringBuilder();
				result.append(InfoHelper.filter4Json(domaindir.getString("dir_name")))
				.append(" ( ")
				.append(InfoHelper.filter4Json(domaindir.getString("dir_url")))
				.append(" ) ");
				json.put("name", result.toString());
			}
			else
			{
				json.put("name", InfoHelper.filter4Json(domaindir.getString("dir_name")));
			}
			//查询是否有子节点 用于前台异步加载
			int childCount = 0;
			try {
				childCount = domaindirManager.getDomainDirByPId(domaindir.getInt("dir_siteID"),domaindir.getInt("sys_documentID")).length;
			} catch (E5Exception e) {
				e.printStackTrace();
				return null;
			}
			String isParent = childCount>0?"true":"false";
			json.put("pid", InfoHelper.filter4Json(domaindir.getString("dir_parentID")));
			json.put("dirname", InfoHelper.filter4Json(domaindir.getString("dir_name")));
			json.put("dirurl", InfoHelper.filter4Json(domaindir.getString("dir_url")));
			json.put("isParent", isParent);
	
			dirs.add(json);
		}
	
		return dirs;
	
	}

	//保存域名目录，并且根据父，修改自己的路径和Url
	private Document save(HttpServletRequest request) throws Exception {
		FormSaver formSaver = (FormSaver)Context.getBean(FormSaver.class);
		Pair changed = formSaver.handleChanged(request);

		long docID = Long.parseLong(changed.getKey());
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document dir = docManager.get(docLibID, docID);

		String name = dir.getString("dir_name");

		int parentID = WebUtil.getInt(request, "dir_parentID", 0);
		if (parentID > 0) {
			Document parent = docManager.get(docLibID, parentID);

			dir.set("dir_url", parent.get("dir_url") + "/" + name);
			dir.set("dir_path", parent.get("dir_path") + "/" + name);
		} else {
			dir.set("dir_path", "/" + name);
		}
		docManager.save(dir);

		return dir;
	}

	//自动添加域名的几个常用的子目录
	private String[] autoAddsubDir(Document parent) throws E5Exception {
		int domainDirLibID = LibHelper.getDomainDirLibID();
		List<Document> dirs = new ArrayList<Document>();

		int siteID = parent.getInt("dir_siteID");

		StringBuilder subIDs = new StringBuilder();
		StringBuilder subNames = new StringBuilder();

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		for (String dirname : dirnames) {
			if (subIDs.length() != 0) {
				subIDs.append(",");
				subNames.append(",");
			}
			long subID = InfoHelper.getNextDocID(DocTypes.DOMAINDIR.typeID());
			Document dir = docManager.newDocument(domainDirLibID, subID);
			subIDs.append(subID);
			subNames.append(dirname);

			dir.set("dir_siteID", siteID);
			dir.set("dir_name", dirname);
			dir.set("dir_parentID", parent.getDocID());
			dir.setFolderID(parent.getFolderID());

			dir.set("dir_url", parent.getString("dir_url") + "/" + dirname);
			dir.set("dir_path", parent.getString("dir_path") + "/" + dirname);

			dirs.add(dir);
		}

		String[] subDirs = new String[]{"", ""};
		String error = save(domainDirLibID, dirs);
		if (error == null) {
			subDirs[0] = subIDs.toString();
			subDirs[1] = subNames.toString();
		}
		return subDirs;
	}

	//统一用事物保存文档
	private String save(int docLibID, List<Document> docs) {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DBSession conn = null;
		try {
			conn = E5docHelper.getDBSession(docLibID);;
			conn.beginTransaction();

			for (Document doc : docs) {
				docManager.save(doc, conn);
			}
			conn.commitTransaction();
			return null;
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			return "操作中出现错误：" + e.getLocalizedMessage();
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
	}
}
