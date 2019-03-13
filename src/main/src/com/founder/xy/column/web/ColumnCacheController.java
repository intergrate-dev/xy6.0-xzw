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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 读缓存的栏目树，用于发布库
 * @author Gong Lijie
 */
@Controller
@RequestMapping("/xy/colcache")
public class ColumnCacheController {

	@Autowired
	private ColumnManager colManager;
	@Autowired
	private ColumnReader colReader;
	
	/** 栏目树（第一层），带操作权限 */
	@RequestMapping(value = "Tree.do", params = "op")
	public void tree(HttpServletRequest request, HttpServletResponse response) throws Exception {

		ColumnTreeHelper.initIcon(request);
		
		int siteID = WebUtil.getInt(request, "siteID", 0);
		int userID = ProcHelper.getUserID(request);
	
		int ch = WebUtil.getInt(request, "ch", 0);

		int colLibID = LibHelper.getColumnLibID(request);
		int roleID = ProcHelper.getRoleID(request);
		//若是站点管理员，则可操作所有栏目
		if (isAdmin(userID)) {
			Document[] cols = colManager.getRoot(colLibID, siteID, ch);
			String result = ColumnTreeHelper.jsonTree(cols);
			InfoHelper.outputJson(result, response);
		}
		else {
			Column[] cols = colReader.getOpColumns(colLibID, userID, siteID, ch, roleID);
			String result = jsonTreeWithParent(cols);
			InfoHelper.outputJson(result, response);
		}
	}


	/** 栏目树（子层），无权限判断 */
	@RequestMapping(value = "Tree.do", params = "parentID")
	public void tree(HttpServletRequest request, HttpServletResponse response,
			@RequestParam int parentID) throws Exception {

		ColumnTreeHelper.initIcon(request);
		
		int colLibID = LibHelper.getColumnLibID(request);
		List<Column> cols = colReader.getSub(colLibID, parentID);
		Collections.sort(cols, new Comparator<Column>() {
			public int compare(Column me1, Column me2) {
				return new Integer(me1.getOrder()).compareTo(me2.getOrder());
			}
		});
		String result = ColumnTreeHelper.jsonTree(cols);

		InfoHelper.outputJson(result, response);
	}
	
	/** 栏目树查找 */
	@RequestMapping(value = "Find.do")
	public void find(HttpServletRequest request, HttpServletResponse response,
			@RequestParam int siteID, @RequestParam String q) throws Exception {

		int colLibID = LibHelper.getColumnLibID(request);
		int ch = WebUtil.getInt(request, "ch", 0);
		
		if (q != null) q = q.trim();
		boolean flag=q.matches("[0-9]+");
		Document[] cols = colManager.find(colLibID, siteID, q, ch,flag);
		String result = ColumnTreeHelper.json(cols);
		InfoHelper.outputJson(result, response);
	}

	//带父节点（无权限）的栏目树的json
	private String jsonTreeWithParent(Column[] cols) throws E5Exception {
		List<Column> roots = getRoots(cols);

		return ColumnTreeHelper.jsonTreeWithParent(roots);
	}

	//根据指定的栏目，得到从根栏目开始的栏目树对象
	private List<Column> getRoots(Column[] cols) throws E5Exception {
		if (cols == null)
			return null;

		//保证顺序
		Map<Integer, Column> tree = new HashMap<Integer, Column>();

		for (Column col : cols) {
			//把无权限的父节点也带上
			int[] path = StringUtils.getIntArray(col.getCasIDs(), "~");
			Column pCol = tree.get(path[0]);
			if (pCol == null) {
				pCol = getCol(col.getLibID(), path[0]);
				if (pCol != null)
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
					son = getCol(col.getLibID(), path[i]);
					if (son != null)
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

		Column parent = colReader.get(colLibID, id);
		if (parent == null) return null;
		
		return new Column(id, parent.getName(), parent.getCasIDs(), parent.getCasNames(),
				enable, parent.isExpandable(), parent.getOrder(), parent.isForbidden());
	}
	
	//加载我收藏的栏目 type 区别web发布库和app发布库
	@RequestMapping(value = "myFavorite.do")
	public void getMyFav(HttpServletRequest request, HttpServletResponse response) throws E5Exception{
		int siteID = WebUtil.getInt(request, "siteID", 0);
		int userID = ProcHelper.getUserID(request);
		int colLibID = LibHelper.getColumnLibID(request);
		//收藏夹的type = ch+6
		int type = WebUtil.getInt(request, "ch", 0) +6;
		Column[] cols = colReader.getPersonalCollection(colLibID,userID,siteID,type);
		//原来是从缓存中qu
		String result = ColumnTreeHelper.parseMyCollToJOSN(cols);
		InfoHelper.outputJson(result, response);
	}


	/**
	 * 判断当前用户是否管理员。
	 * @param userID
	 * @return
	 */
	private boolean isAdmin(int userID) {
		UserReader userReader = (UserReader) Context.getBean(UserReader.class);
		try {
			User user = userReader.getUserByID(userID);
			return "1".equals(user.getProperty2());
		} catch (E5Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
