package com.founder.xy.magazine.web;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.founder.e5.commons.StringUtils;
import com.founder.e5.web.WebUtil;
import com.founder.e5.workspace.ProcHelper;
import com.founder.e5.workspace.app.form.LogHelper;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.FormViewHelper;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.magazine.MagazineManager;
import com.founder.xy.set.web.AbstractResourcer;

/**
 * 期刊相关操作 
 * @author binLee
 */
@Controller
@RequestMapping("/xy/magazine")
public class MagazineController extends AbstractResourcer {

	@Autowired
	private MagazineManager magazineManager;
	
	/**
	 * 修改期刊稿件
	 */
	@RequestMapping(value = "Article.do")
	public ModelAndView article(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Map<String, Object> model = new HashMap<String, Object>();
		
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		long docID = WebUtil.getLong(request, "DocIDs", 0);
		String uuid = WebUtil.get(request, "UUID");
		
		// 取出定制的表单，数组中0是js和css的引用文件，1是form的字段内容
		String[] jspStr = FormViewHelper.getFormJsp(docLibID, docID, "formMagazineArticle", uuid);
		model.put("formHead", jspStr[0]);
		model.put("formContent", jspStr[1]);
		
		return new ModelAndView("xy/magazine/Article", model);
	}
	
	@RequestMapping(value = "DeleteArticle.do")
	public String deleteMagArticle(HttpServletRequest request, HttpServletResponse response) throws Exception{
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		long[] docIDs = StringUtils.getLongArray(WebUtil.get(request, "DocIDs"));
		
		StringBuilder successDocIDs = new StringBuilder();
		String result = null;
		for(long docID : docIDs){
			result = magazineManager.articleDelete(docLibID, docID);
			if(result != null) break;
			if(successDocIDs.length() > 0) successDocIDs.append(",");
			successDocIDs.append(docID);
		}
		
		String url = "/e5workspace/after.do?UUID=" + WebUtil.get(request, "UUID");
		if(successDocIDs.length()>0)
			url += "&DocIDs=" + successDocIDs.toString();
		if(result != null){
			url += "&Info=" +URLEncoder.encode("操作失败：" + result, "UTF-8");
		}
		
		return "redirect:" + url;
	}
	
	/**
	 * 刊期删除
	 * 
	 * 清理刊期下所有的数据，再删除刊期表数据。
	 */
	@RequestMapping(value = "DeleteDate.do")
	public void deletePaperDate(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int magazineLibID = WebUtil.getInt(request, "magazineLibID", 0);//注意前端传入的其实是期刊稿件库ID
		int magazineID = WebUtil.getInt(request, "magazine", 0);
		String magDate = WebUtil.get(request, "date");
		
		//得到期刊库ID
		int paperLibID = LibHelper.getLibIDByOtherLib(DocTypes.MAGAZINE.typeID(), magazineLibID);
		
		//调用删除操作
		String result = magazineManager.dateDelete(paperLibID, magazineID, magDate);
		
		//添加操作日志
		if (result == null) {
			LogHelper.writeLog(paperLibID, magazineID, ProcHelper.getUser(request), "删除刊期", "刊期:" + magDate);
		}
		
        InfoHelper.outputText(result, response);
	}
	
	/**
	 * 刊期重新发布
	 */
	@RequestMapping(value = "PublishDate.do")
	public void publishDate(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int magazineLibID = WebUtil.getInt(request, "magazineLibID", 0);//注意前端传入的其实是期刊稿件库ID
		magazineLibID = LibHelper.getLibIDByOtherLib(DocTypes.MAGAZINE.typeID(), magazineLibID);
		
		int magazineID = WebUtil.getInt(request, "magazine", 0);
		String magDate = WebUtil.get(request, "date");
		
		magazineManager.pubByDate(magazineLibID, magazineID, magDate);
		
		//在报纸上添加操作日志
		LogHelper.writeLog(magazineLibID, magazineID, ProcHelper.getUser(request), "刊期发布", "刊期:" + magDate);
		
        InfoHelper.outputText(null, response);
	}
	
	/**
	 * 期刊发布页查看
	 */
	@RequestMapping(value = "View.do")
	public ModelAndView view(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int docLibID = WebUtil.getInt(request, "magazineLibID", 0); //注意前端传入的其实是期刊稿件库ID
		long docID = WebUtil.getLong(request, "magazine", 0);
		String magDate = WebUtil.get(request, "date");
		
		String[] urls = magazineManager.getUrls(docLibID, docID, magDate);
        
        Map<String, Object> model = new HashMap<String, Object>();
        if (!StringUtils.isBlank(urls[0])) model.put("path0", urls[0]);
        if (!StringUtils.isBlank(urls[1])) model.put("path1", urls[1]);
        
        //复用栏目的查看
        return new ModelAndView("/xy/article/ColumnView", model);
	}

}
