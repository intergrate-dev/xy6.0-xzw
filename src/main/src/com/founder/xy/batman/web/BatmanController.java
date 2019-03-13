package com.founder.xy.batman.web;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.founder.e5.cat.CatManager;
import com.founder.e5.cat.CatReader;
import com.founder.e5.cat.Category;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.sys.org.UserFrozenManager;
import com.founder.e5.web.WebUtil;
import com.founder.e5.workspace.ProcHelper;
import com.founder.e5.workspace.app.form.FormSaver;
import com.founder.xy.commons.CatTypes;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.set.web.AbstractResourcer;
import com.founder.xy.set.web.ResDir;

@Controller
@RequestMapping("/xy/batman")
public class BatmanController extends AbstractResourcer{
	@Autowired
	private UserFrozenManager userFrozenManager;
	/** 查找地区邵阳市用于前台默认地址  */
	@RequestMapping(value = "getRegion.do")
	public void getRegion(HttpServletRequest request, HttpServletResponse response) throws Exception {
		//String value = WebUtil.get(request, "value");
		//String tenantCode = InfoHelper.getTenantCode(request);
		CatReader catReader = (CatReader) Context.getBean(CatManager.class);
		Category[] corp_regions = catReader.getCats(CatTypes.CAT_REGION.typeID());
		
		JSONObject result = new JSONObject();
		for (Category corp_region : corp_regions) {
			if("邵阳市".equals(corp_region.getCatName())){
				result.put("regionName", corp_region.getCascadeName());
				result.put("regionId", corp_region.getCascadeID().replace("~", "_"));
			}
		}
		InfoHelper.outputJson(result.toString(), response);
	}
	@RequestMapping(value="FormSubmit.do")
	public String getBatManForm(HttpServletRequest request, HttpServletResponse response, Map<String, Object> model) throws Exception{
		int docLibID = docLibID(request);
		long docID = docID(request);
		int siteID = WebUtil.getInt(request, "bm_siteID", 0);
		//检查文件名的合法性
		String filePath =  request.getParameter("bm_icon");
		if (!isImgFile(filePath)){
			model.put("error", "对不起，请上传jpg,gif,png格式");
			return "/xy/site/error";
		}
		//检查站点的资源目录是否已配置
		ResDir siteDir = getSiteDirs(filePath, docLibID, docID, siteID, "bm_icon");
		if (siteDir.noSiteDir) {
			model.put("error", "请先检查站点的资源目录设置");
			return "/xy/site/error";
		}
		//保存用户扩展信息表 userext
		FormSaver formSaver = (FormSaver) Context.getBean(FormSaver.class);
		String UUID = request.getParameter("UUID");
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document bm_document = docManager.get(docLibID, docID);
		if(bm_document == null){
			docID = InfoHelper.getNextDocID(DocTypes.USEREXT.typeID()) ;
			bm_document = docManager.newDocument(docLibID, docID) ;
			ProcHelper.initDoc(bm_document);
		}
		String bm_ori_password = bm_document.getString("BM_PASSWORD");
		String bm_password = request.getParameter("bm_password");
		
		try {
			formSaver.handle(bm_document, request);
		} catch (Exception e) {
			model.put("error", e.getLocalizedMessage());
			return "redirect:e5workspace/after.do?UUID=" + UUID;
		}
		Document bm_doc = afterDealPass(docManager,docLibID, docID, bm_ori_password, bm_password);
		docManager.save(bm_doc);
		return "redirect:../../e5workspace/after.do?DocIDs=" + docID + "&UUID=" + UUID;
	}
	
	@RequestMapping(value="unFrozenBatman.do")
	public String unFrozenBatman(HttpServletRequest request,HttpServletResponse response) throws E5Exception, UnsupportedEncodingException{
		
		int docID = Integer.valueOf(request.getParameter("DocIDs"));
		boolean frozen = userFrozenManager.isFrozen(docID);
		String url = "/e5workspace/after.do?UUID=" + WebUtil.get(request, "UUID");
		if(frozen){
			userFrozenManager.validationSucceed(docID);
			//调用after.do进行后处理：改变流程状态、解锁、刷新列表
	        String redirectUrl = url +  "&Info=" + URLEncoder.encode("解冻成功! ", "UTF-8");
	        return "redirect:"+redirectUrl;
		}else{
		    String redirectUrl = url + "&Info=" + URLEncoder.encode("解冻失败，可能该用户未曾被冻结! ", "UTF-8");
		    return "redirect:"+redirectUrl;
		}
		
	}
	private Document afterDealPass(DocumentManager docManager,int docLibID,long docID,String bm_ori_password,String bm_password) throws E5Exception{
		Document doc = docManager.get(docLibID, docID);
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<bm_password.length();i++){
			sb.append("*");
		}
		if(sb.toString().equals(bm_password)){
			doc.set("bm_password", bm_ori_password);
		}else{
			doc.set("bm_password", UserEncryptUtil.encrypt(bm_password));
		}
		return doc;
		
	}
	
	
	

}
