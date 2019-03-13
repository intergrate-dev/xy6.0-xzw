package com.founder.xy.set.web;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.founder.e5.web.SysUser;
import com.founder.e5.workspace.ProcHelper;
import com.founder.e5.workspace.app.form.LogHelper;
import com.founder.xy.commons.LibHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.founder.e5.commons.Pair;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.web.WebUtil;
import com.founder.e5.workspace.app.form.FormSaver;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;
import org.springframework.web.servlet.ModelAndView;

/**
 * 移动平台设置
 * @author Deng Chaochen
 */
@Controller
@RequestMapping("/xy/mobileos")
public class MobileosController extends AbstractResourcer{
	
	/**
	 * 表单提交时，检查文件的扩展名
	 */
	@RequestMapping("formSubmit.do")
	public String formSubmit(HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> model) throws Exception {
		String[] files = new String[]{
				request.getParameter("os_picSmall"),
				request.getParameter("os_picUrlMiddle"),
				request.getParameter("os_picUrlBig"),
				request.getParameter("os_templateZip"),
				request.getParameter("os_logoPic"),
				request.getParameter("os_appDownloadImage")
		};
		//检查启动图片
		if(!isImgFile(files[0]) || !isImgFile(files[1]) || !isImgFile(files[2]) || !isImgFile(files[4]) || !isImgFile(files[5])){
			model.put("error", "对不起，启动图片格式只支持jpg,png");
			return "/xy/site/error";
		}
		//检查模板上传文件
		if (!StringUtils.isBlank(files[3]) && !isExtension(files[3], "zip")){
			model.put("error", "对不起，模板打包文件只能上传zip文件");
			return "/xy/site/error";
		}
		
		long docID = WebUtil.getLong(request, "DocID", 0);
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = docID == 0 ? null : docManager.get(docLibID, docID);
		
		//判断是否新上传了文件，若有新上传的文件，需检查资源目录
		String[] dirs = null;
		boolean[] pathChanged = pathChanged(doc, files);
		if (pathChanged[0] || pathChanged[1] || pathChanged[2] || pathChanged[3]|| pathChanged[4] || pathChanged[5]) {
			int pubDir = WebUtil.getInt(request, "os_dir_ID", 0);
			if (pubDir == 0) pubDir = doc.getInt("os_dir_ID");//操作“上传启动图片”中无发布目录，需取一次
			
			dirs = getDirs(pubDir);
			
			boolean noSiteDir = (StringUtils.isBlank(dirs[0]) || StringUtils.isBlank(dirs[1]));
			if (noSiteDir) {
				model.put("error", "请先检查站点的资源目录设置");
				return "/xy/site/error";
			}
		}
		
		//保存表单
		FormSaver formSaver = (FormSaver) Context.getBean(FormSaver.class);
		Pair changed = null;
		try {
			if (docID == 0) {
				docID = formSaver.handle(request);
			} else {
				changed = formSaver.handleChanged(request);
			}
		} catch (Exception e) {
			model.put("error", e.getLocalizedMessage());
			return "/xy/site/error";
		}

		//文件改名，并发布到外网
		publishAll(docLibID, docID, dirs, files, pathChanged);
		
		//清掉redis中的旧缓存
		RedisManager.clear(RedisKey.APP_START_KEY + docID);
		
		//after.do
		String url = returnUrl(request, docID, changed);
		return url;
	}
	//去掉gif，启动图片不允许是gif，苹果手机不支持
	protected boolean isImgFile(String fileName) {
		if(!StringUtils.isBlank(fileName)){
			String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length())
					.trim().toLowerCase();
			return ( "jpg".equalsIgnoreCase(fileExt)
					|| "png".equalsIgnoreCase(fileExt)
					|| "jpeg".equalsIgnoreCase(fileExt)
					|| "gif".equalsIgnoreCase(fileExt)
					);
		}
		return true;
	}
	
	//检查是否上传了文件
	private boolean[] pathChanged(Document doc, String[] files) {
		boolean[] result = new boolean[]{true, true, true, true, true, true};
		if (doc != null) {
			//若文件名不同，则认为是新上传的文件。原文件名是上传后改的uuid，因此不太可能与新上传的文件同名
			result[0] = !StringUtils.isBlank(files[0]) && !files[0].equals(doc.getString("os_picSmall"));
			result[1] = !StringUtils.isBlank(files[1]) && !files[1].equals(doc.getString("os_picUrlMiddle"));
			result[2] = !StringUtils.isBlank(files[2]) && !files[2].equals(doc.getString("os_picUrlBig"));
			result[3] = !StringUtils.isBlank(files[3]) && !files[3].equals(doc.getString("os_templateZip"));
			result[4] = !StringUtils.isBlank(files[4]) && !files[4].equals(doc.getString("os_logoPic"));
			result[5] = !StringUtils.isBlank(files[5]) && !files[5].equals(doc.getString("os_appDownloadImage"));
		} else {
			result[0] = !StringUtils.isBlank(files[0]);
			result[1] = !StringUtils.isBlank(files[1]);
			result[2] = !StringUtils.isBlank(files[2]);
			result[3] = !StringUtils.isBlank(files[3]);
			result[4] = !StringUtils.isBlank(files[4]);
			result[5] = !StringUtils.isBlank(files[5]);
		}
		return result;
	}

	/**
	 * 文件改名，并发布到外网
	 */
	private void publishAll(int docLibID, long docID, String[] dirs, String[] files, boolean[] pathChanged)
			throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = docManager.get(docLibID, docID);
		
		String destPath = dirs == null ? null : dirs[0] + dirs[1];
		if (pathChanged[0])
			publish(files[0], destPath, dirs[0], doc, "os_picSmall");
		if (pathChanged[1])
			publish(files[1], destPath, dirs[0], doc, "os_picUrlMiddle");
		if (pathChanged[2])
			publish(files[2], destPath, dirs[0], doc, "os_picUrlBig");
		if (pathChanged[3])
			publish(files[3], destPath, dirs[0], doc, "os_templateZip");
		if (pathChanged[4])
			publish(files[4], destPath, dirs[0], doc, "os_logoPic");
		if (pathChanged[5])
			publish(files[5], destPath, dirs[0], doc, "os_appDownloadImage");
		
		docManager.save(doc);
	}
	/**
	 * 发布，并改原文件的名为uuid，避免日后被错误覆盖
	 * @param srcPath 附件存储;201510/23/glj_ad9.jpg
	 * @param destPath z:\webroot/xy/resource
	 * @param webRoot z:\webroot
	 * @param doc
	 */
	private void publish(String srcPath, String destPath, String webRoot, Document doc, String field) {
		if (StringUtils.isBlank(srcPath)) return;
		
		//生成随机文件名，UUID
		String fileName = randomName(srcPath);
		
		//存储设备里的文件改名
		srcPath = changeFileName(srcPath, fileName);
		
		//改文档字段
		doc.set(field, srcPath);
		
		//发布
		publishTo(srcPath, destPath, fileName, webRoot);
	}
	
	//生成随机文件名，保持后缀不变
	private String randomName(String srcPath) {
		int pos = srcPath.lastIndexOf(".");
		String ext = pos >= 0 ? srcPath.substring(pos).toLowerCase() : "";
		String fileName = UUID.randomUUID() + ext;
		
		return fileName;
	}
	
	//存储设备里的文件改名，以免以后被同名文件覆盖。（发布到外网后文件不带日期目录，更容易重名覆盖）
	private String changeFileName(String srcPath, String newFileName) {
		int pos = srcPath.indexOf(";");
		String deviceName = srcPath.substring(0, pos); //存储设备名：附件存储
		String relFilePath = srcPath.substring(pos + 1); //存储文件路径：201510/23/glj_ad9.jpg
		
		//存储的文件改名为uuid
		String devicePath = InfoHelper.getDevicePath(InfoHelper.getDeviceByName(deviceName));
		pos = relFilePath.lastIndexOf("/");
		String newFilePath = relFilePath.substring(0, pos + 1) + newFileName;

		File srcFile = new File(devicePath, relFilePath);
		File newFile = new File(devicePath, newFilePath);
		srcFile.renameTo(newFile);
		
		//新的存储路径
		srcPath = deviceName + ";" + newFilePath;
		return srcPath;
	}

	/**
	 * 保存头部背景图
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "saveBackGround.do")
	public void saveBackGround(HttpServletRequest request, HttpServletResponse response,
							   Map<String, Object> model) throws Exception {
		long docID = WebUtil.getLong(request, "DocIDs", 0);
		String picUrl = WebUtil.get(request, "picUrl");
		int docLibID = LibHelper.getMobileosID();
		try {
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			Document doc = docID == 0 ? null : docManager.get(docLibID, docID);
			if(doc != null){
				doc.set("os_picBackGround", picUrl);
				docManager.save(doc);
				log(request, docLibID, docID, "保存头部背景图", null);

				//判断是否新上传了文件，若有新上传的文件，需检查资源目录
				String[] dirs = null;
				if (!StringUtils.isBlank(picUrl)) {
					int pubDir = doc.getInt("os_dir_ID");//操作“上传启动图片”中无发布目录，需取一次

					dirs = getDirs(pubDir);

					boolean noSiteDir = (StringUtils.isBlank(dirs[0]) || StringUtils.isBlank(dirs[1]));
					if (noSiteDir) {
						System.out.println("发布图片至外网失败，请先检查站点的资源目录设置");
					}
				}
				String destPath = dirs == null ? null : dirs[0] + dirs[1];
				//文件改名，并发布到外网
				publish(picUrl, destPath, dirs[0], doc, "os_picBackGround");
				docManager.save(doc);
			}
			String key = RedisKey.APP_START_KEY + docID;
			if(RedisManager.exists(key))
				RedisManager.del(key);
			InfoHelper.outputText("ok", response);
		}catch (Exception e) {
			InfoHelper.outputText(e.getLocalizedMessage(), response);
		}

	}

	/**
	 * 获取当前头部背景图
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "picBackGround.do")
	public ModelAndView picBackGround(HttpServletRequest request, HttpServletResponse response) throws Exception {

		Map<String, Object> model = new HashMap<String, Object>();
		long docID = WebUtil.getLong(request, "DocIDs", 0);
		int docLibID = LibHelper.getMobileosID();
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = docID == 0 ? null : docManager.get(docLibID, docID);
		if(doc != null){
			model.put("picUrl", doc.get("os_picBackGround"));
			model.put("docID", doc.get("SYS_DOCUMENTID"));
			model.put("UUID", WebUtil.get(request, "UUID"));
		}

		return new ModelAndView("/xy/site/setBackgroundImg", model);
	}

	//e5日志记录方法
	private void log(HttpServletRequest request, int colLibID, long docID, String procName, String detail) {

		SysUser user = ProcHelper.getUser(request);

		LogHelper.writeLog(colLibID, docID, user, procName, detail);
	}
}