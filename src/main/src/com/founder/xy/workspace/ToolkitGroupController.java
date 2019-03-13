package com.founder.xy.workspace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;

import com.founder.e5.commons.DomHelper;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.flow.Operation;
import com.founder.e5.flow.Proc;
import com.founder.e5.flow.ProcReader;
import com.founder.e5.web.SysUser;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;

public class ToolkitGroupController extends com.founder.e5.workspace.controller.ToolkitGroupController {
	protected int[] opIDs_hide_when_rel = null; //web发布渠道时不显示的操作，如移动
	
	public ToolkitGroupController() {
		super();
	}
	
	@SuppressWarnings("rawtypes")
	protected void handle(HttpServletRequest request, HttpServletResponse response, Map model) throws Exception {
		if (log.isDebugEnabled()) log.debug("[Toolkit]" + request.getQueryString());
		SysUser user = getUserInfo(request);
		if (user == null) {
			log.error("[Toolbar]No user in session!");
			output(response, "");
			return;
		}
		String code = get(request, "code");
		if (!StringUtils.isBlank(code)) {
			handleMultiCode(request, response, user);
			return;
		}
		int fvID = getInt(request, "FVID");
		int[] docLibArr = StringUtils.getIntArray(get(request, "DocLibID"));
		if (!sameDocLib(docLibArr)) {
			output(response, "");
			return;
		}
		int docLibID = docLibArr[0];
		long[] docArr = StringUtils.getLongArray(get(request, "DocIDs"));
		boolean isQuery = "true".equals(get(request, "IsQuery"));

		log.debug("[ToolkitController]Get proc from service. ");
		Proc[] procs = service.getProcs(user.getRoleID(), docLibID, fvID, docArr, isQuery);

		boolean canCustomize = service.canCustomize();
		if (canCustomize && !isQuery) {
			Proc[] procs_filtered = getCustomizeProcs(user, docLibID, fvID, docArr, isQuery, procs);
			if (procs_filtered != null) {
				procs = procs_filtered;
			}
		}
		
		String colID = get(request, "colID");
		if (DomHelper.getDocTypeIDByLibID(docLibID) == DocTypes.ARTICLE.typeID()
				&& docArr != null && docArr.length > 0 && colID != null && !"".equals(colID)) {
			init();
			procs = filterProcs(procs, docLibID, docArr, Integer.valueOf(colID));
		}
		output(response, getProcOutputString(procs, user));
	}
	
	private Proc[] filterProcs(Proc[] procs, int docLibID, long[] docArr, long colID) {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			Document[] docs = docManager.get(docLibID, docArr);
			for(Document doc:docs){
				if(doc.getLong("a_columnID") != colID){
					return filterProcs(opIDs_hide_when_rel, procs);
				}
			}
		} catch (E5Exception e) {
			e.printStackTrace();
		}
		return procs;
	}
	
	private synchronized void init() {
		try {
			int docTypeID = InfoHelper.getArticleTypeID();
			
			ProcReader procReader = (ProcReader)Context.getBean(ProcReader.class);
			Operation[] ops = procReader.getOperations(docTypeID);
			if (ops == null) return;
			List<String> relHideList = new ArrayList<String>(Arrays.asList("移动"));
			opIDs_hide_when_rel = findProcs(ops, relHideList.toArray(new String[0]));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Proc[] filterProcs(int[] opIDs, Proc[] procs) {
		if (procs == null || opIDs == null) return null;
		List<Proc> returnList = new ArrayList<>();
		for(int i = 0 ; i < procs.length ; i++){
			if (!ArrayUtils.contains(opIDs, procs[i].getOpID())) {
				returnList.add(procs[i]);
			}
		}
		return (Proc[])returnList.toArray(new Proc[0]);
	}
	
	//按名字查找稿件的操作，得到操作ID数组
	private int[] findProcs(Operation[] ops, String[] names) {
		List<Integer> list = new ArrayList<>();
		for (int i = 0; i < ops.length; i++) {
			if (ArrayUtils.contains(names, ops[i].getName())) {
				list.add(ops[i].getID());
			}
		}
		return InfoHelper.getIntArray(list);
	}
}
