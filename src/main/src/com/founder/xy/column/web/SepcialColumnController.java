package com.founder.xy.column.web;

import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;

import com.founder.e5.sys.org.User;
import com.founder.e5.sys.org.UserReader;
import com.founder.e5.web.WebUtil;
import com.founder.e5.workspace.ProcHelper;
import com.founder.xy.column.Column;
import com.founder.xy.column.ColumnManager;
import com.founder.xy.column.ColumnReader;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.set.web.AbstractResourcer;
import com.founder.xy.system.site.SiteUserManager;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 专题栏目导航树特殊需求功能
 *
 */
@Controller
@RequestMapping("/xy/special")
public class SepcialColumnController extends AbstractResourcer {

	@Autowired
	private ColumnReader columnReader;
	@Autowired
	private ColumnManager colManager;
	@Autowired
	private SiteUserManager userManager;

	/** 栏目树（第一层以及子层），无权限判断 专题导航用*/
	@RequestMapping(value = "Tree.do", params = "parentID")
	public void specialTree(HttpServletRequest request, HttpServletResponse response,
			@RequestParam int siteID, @RequestParam int parentID) throws Exception {

		ColumnTreeHelper.initIcon(request);
		
		int colLibID = LibHelper.getColumnLibID(request);
		
		Document[] cols;
		if (parentID == 0) {
			int channelType = WebUtil.getInt(request, "ch", 0);
			cols = colManager.getRoot(colLibID, siteID, channelType);
		} else {
			cols = colManager.getSub(colLibID, parentID);
		}
		String result = specialJsonTree(cols,colLibID);

		InfoHelper.outputJson(result, response);
	}

	/** 栏目树（第一层），带管理权限 专题导航用*/
	@RequestMapping(value = "Tree.do", params = "special")
	public void treeAdmin(HttpServletRequest request, HttpServletResponse response,
			@RequestParam int siteID) throws Exception {

		ColumnTreeHelper.initIcon(request);
		
		int userID = ProcHelper.getUserID(request);
		int channelType = WebUtil.getInt(request, "ch", 0);
		
		int colLibID = LibHelper.getColumnLibID(request);
		
		//若是站点管理员，则可管理所有栏目
		if (isAdmin(userID)) {
			Document[] cols = colManager.getRoot(colLibID, siteID, channelType);
			String result = specialJsonTree(cols,colLibID);

			InfoHelper.outputJson(result, response);
		} else {
			int roleID = ProcHelper.getRoleID(request);
			Document[] cols = colManager.getAdminColumns(colLibID, userID, siteID,channelType,roleID);
			String result = specialJsonTreeWithParent(cols,colLibID);

			InfoHelper.outputJson(result, response);
		}
	}
	/*
	 * 专题获取导航栏目json
	 */
	@RequestMapping(value = "getColumnsJson.do")
	public void getColumnJson(HttpServletRequest request, HttpServletResponse response
							  ) throws Exception {

		String docIDs = WebUtil.getStringParam(request, "docIDs");
		int colLibID = LibHelper.getColumnLibID(request);
		String result = getColumnsJson(colLibID,docIDs);
		InfoHelper.outputJson(result,response);
	}
	/**
	 * 判断当前用户是否管理员。
	 * @param userID
	 * @return
	 */
	private boolean isAdmin(int userID) {
		UserReader userReader = (UserReader)Context.getBean(UserReader.class);
		try {
			User user = userReader.getUserByID(userID);
			return "1".equals(user.getProperty2());
		} catch (E5Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	//带父节点（无权限）的栏目树的json
	private String specialJsonTreeWithParent(Document[] cols,int colLibID) throws E5Exception {
		List<Column> roots = getRoots(cols);
		return specialJsonTreeWithParent(roots,colLibID);
	}

	//根据指定的栏目，得到从根栏目开始的栏目树对象
	private List<Column> getRoots(Document[] cols) throws E5Exception {
		if (cols == null)
			return null;

		//保证顺序
		Map<Integer, Column> tree = new HashMap<Integer, Column>();

		for (Document col : cols) {
			//把无权限的父节点也带上
			int[] path = StringUtils.getIntArray(col.getString("col_cascadeID"), "~");
			Column pCol = tree.get(path[0]);
			if (pCol == null) {
				pCol = getCol(col.getDocLibID(), path[0]);
				tree.put(path[0], pCol);
			} else if (pCol.isEnable()) {
				//栏目拖动会造成栏目的父子结构发生变化。若发现父栏目已经有权限，则不必加子栏目
				continue;
			}

			if (path.length == 1) {
				pCol.setEnable(true);
				pCol.removeChildren(); //节点设置为enable后就可动态展开，不需要设置children
				continue;
			}

			Column parent = pCol;
			for (int i = 1; i < path.length; i++) {
				Column son = parent.getChild(path[i]);
				if (son == null) {
					son = getCol(col.getDocLibID(), path[i]);
					parent.addChild(son);
				} else if (son.isEnable()) {
					break;
				}
				//最后一级的栏目，是确实有权限的，所以enable=true
				if (i == path.length - 1) {
					son.setEnable(true);
				}
				parent = son;
			}
		}
		return ColumnTreeHelper.sortColByOrder(tree);
	}

	//根据栏目ID得到Col对象
	private Column getCol(int colLibID, long id) throws E5Exception {
		boolean enable = false;

		Document parent = colManager.get(colLibID, id);

		return new Column(id, parent.getString("col_name"),
				parent.getString("col_cascadeID"), parent.getString("col_cascadeName"),
				enable, parent.getInt("col_childCount") > 0,
				parent.getInt("col_displayOrder"), parent.getInt("col_status") > 0,parent.getString("col_linkUrl"));
	}
	private String getColumnsJson(int docLibID,String docIDs) throws E5Exception {
		String[] ids = docIDs.split(",");
		JSONArray cols = new JSONArray();
		for (String id : ids) {
			long docID = Long.parseLong(id);
			try {
				Document doc = colManager.get(docLibID, docID);
				String[] urls = columnReader.getUrls(docLibID, docID);
				JSONObject col = new JSONObject();
				col.put("id", docID);
				col.put("title", doc.getString("col_name"));
				col.put("link", urls[0]);
				col.put("linkPad", urls[1]);
				cols.add(col);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return cols.toString();
	}

	public String specialJsonTree(Document[] cols,int colLibID) throws E5Exception {
		JSONArray jsonArr  = new JSONArray();

		for (Document col : cols) {
			JSONObject json = jsonOneCol(colLibID,col);
			jsonArr.add(json);
		}
		return jsonArr.toString();
	}

	private JSONObject jsonOneCol(int colLibID,Document col)throws E5Exception{
		String name = col.getString("col_name");

		JSONObject json = new JSONObject();
		long docID = col.getDocID();
		json.put("id", col.getDocID());
		json.put("name", name);
		json.put("title", name + " [" + col.getDocID() + "]");
		json.put("casID", col.getString("col_cascadeID"));
		json.put("casName", col.getString("col_cascadeName"));
		json.put("code", col.getString("col_code"));
		json.put("isParent", col.getInt("col_childCount") > 0 ? "true" : "false");
		String[] urls = columnReader.getUrls(colLibID, docID);
		if(urls==null || urls.length<1 || urls[0]==null){
			json.put("nocheck", "true");
		}
		return json;
	}

	public String specialJsonTreeWithParent(List<Column> roots,int colLibId) throws E5Exception {
		JSONArray jsonArr  = new JSONArray();

		if (roots != null) {
			for (Column col : roots) {
				//检查子栏目是否有linURL
				JSONObject json = jsonOneCol(col, true,colLibId);
				jsonArr.add(json);
			}
		}
		return jsonArr.toString();
	}

	//处理一个栏目的json转换
	private JSONObject jsonOneCol(Column col, boolean check,int colLibId) throws E5Exception {

		JSONObject json = new JSONObject();
		json.put("id", col.getId());
		json.put("name", col.getName());
		json.put("title", col.getName() + " [" + col.getId() + "]");
		json.put("casID", col.getCasIDs());
		json.put("casName", col.getCasNames());
		if (check && !col.isEnable())
			json.put("nocheck", "true");//no checkbox

		String[] urls = columnReader.getUrls(colLibId, col.getId());
		if(urls==null || urls.length<1 || urls[0]==null){
			json.put("nocheck", "true");
		}
		if (col.isExpandable()) json.put("isParent", "true");//有子节点，可展开

		//子栏目
		if (col.getChildren() != null) {
			JSONArray children = new JSONArray();

			List<Column> cols = col.getChildren();
			for (int i = 0; i < cols.size(); i++) {
				Column son = cols.get(i);
				children.add(jsonOneCol(son,check,colLibId));
			}
			json.put("children", children);
		}

		return json;
	}

	public void setUserManager(SiteUserManager userManager) {
		this.userManager = userManager;
	}

}
