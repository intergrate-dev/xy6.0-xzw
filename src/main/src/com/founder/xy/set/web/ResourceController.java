package com.founder.xy.set.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.founder.e5.workspace.ProcHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.founder.e5.commons.Pair;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.web.WebUtil;
import com.founder.e5.workspace.app.form.FormSaver;
/**
 * 公共资源管理
 * @author Gong Lijie
 */
@Controller
@RequestMapping("/xy/resource")
public class ResourceController extends AbstractResourcer{

	/**
	 * 公共资源的表单提交，提交前检查文件的扩展名
	 */
	@RequestMapping("formSubmit.do")
	public String formSubmit(HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> model) throws Exception {

		//获得文件名
		String fileName =  request.getParameter("res_fileName");
		if (isExtension(fileName, "jsp")){
			model.put("error", "对不起，不能提交jsp文件");
			return "/xy/column/Submit";
		}
		
		//检查站点的资源目录是否已配置（公共资源修改时上传的文件时还是同名，所以不判断是否修改，每次都发布）
		int pubDir = WebUtil.getInt(request, "res_dir_ID", 0);
		String[] dirs = getDirs(pubDir);
		boolean noSiteDir = (StringUtils.isBlank(dirs[0]) || StringUtils.isBlank(dirs[1]));
		if (noSiteDir) {
			model.put("error", "请先检查站点的资源目录设置");
			return "/xy/site/error";
		}
		
		//保存表单
		long docID = docID(request);
		boolean isNew = (docID == 0);
		FormSaver formSaver = (FormSaver) Context.getBean(FormSaver.class);
		Pair changed = null;
		try {
			if (isNew) {
				docID = formSaver.handle(request);
			} else {
				changed = formSaver.handleChanged(request);
			}
		} catch (Exception e) {
			model.put("error", e.getLocalizedMessage());
			return "/xy/column/Submit";
		}
		
		//发布到外网
		String userCode = ProcHelper.getUserCode(request);
		String destPath = dirs[0] + dirs[1];
		String filePath =  WebUtil.get(request, "res_file");
		publish(userCode, filePath, destPath, dirs[0]);
		
		String url = returnUrl(request, docID, changed);
		return url;
	}
	
	private void publish(String userCode, String srcPath, String destPath, String webRoot) {
		if (StringUtils.isBlank(srcPath)) return;

		int pos = srcPath.lastIndexOf(userCode + "_");
		String fileName = (pos < 0) ? srcPath : srcPath.substring(pos + userCode.length() + 1, srcPath.length()).trim();
		
		publishTo(srcPath, destPath, fileName, webRoot);
	}
}
