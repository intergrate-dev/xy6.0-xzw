package com.founder.xy.ueditor;

import java.io.File;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import com.baidu.ueditor.ConfigManager;
import com.baidu.ueditor.define.ActionMap;
import com.baidu.ueditor.define.AppInfo;
import com.baidu.ueditor.define.BaseState;
import com.baidu.ueditor.define.State;
import com.baidu.ueditor.hunter.ImageHunter;
import com.baidu.ueditor.upload.Uploader;
import com.founder.e5.sys.StorageDevice;
import com.founder.xy.commons.InfoHelper;
import org.apache.commons.io.FilenameUtils;

/**
 * 百度编辑器初始化及上传功能类
 */
public class XYActionEnter {

	private HttpServletRequest request = null;
	private String rootPath = null;
	private String contextPath = null;
	private String actionType = null;
	private ConfigManager configManager = null;

	/** 构造方法 */
	public XYActionEnter(HttpServletRequest request, String rootPath) {

		this.request = request;
		this.rootPath = rootPath;
		this.actionType = request.getParameter("action");
		this.contextPath = request.getContextPath();
		this.configManager = ConfigManager.getInstance(this.rootPath,
				this.contextPath, request.getRequestURI());
	}

	/** 百度编辑器初始化及上传功能函数入口 */
	public String exec() {

		String callbackName = this.request.getParameter("callback");
		if (callbackName != null) {

			if (!validCallbackName(callbackName)) {
				return new BaseState(false, AppInfo.ILLEGAL).toJSONString();
			}
			return callbackName + "(" + this.invoke() + ");";
		} else {
			return this.invoke();
		}
	}

	/** 判断上传各种类型文件分支 */
	public String invoke() {

		if (actionType == null || !ActionMap.mapping.containsKey(actionType)) {
			return new BaseState(false, AppInfo.INVALID_ACTION).toJSONString();
		}
		if (this.configManager == null || !this.configManager.valid()) {
			return new BaseState(false, AppInfo.CONFIG_ERROR).toJSONString();
		}
		State state = null;
		int actionCode = ActionMap.getType(this.actionType);
		Map<String, Object> conf = null;
		
		switch (actionCode) {
			case ActionMap.CONFIG:
				return this.configManager.getAllConfig().toString();
	
			// 上传图片
			case ActionMap.UPLOAD_IMAGE:
				conf = this.configManager.getConfig(actionCode);
				state = new XYUploader(request, conf).doExec();
				break;
				
			case ActionMap.UPLOAD_SCRAWL:
				conf = this.configManager.getConfig(actionCode);

				String rootPath = "";

				// 附件存储设备的名称
				StorageDevice device = InfoHelper.getPicDevice();

				int deviceType = device.getDeviceType();
				switch(deviceType) {
					case 1:
						rootPath = device.getNfsDevicePath() + File.separator;
						break;
					case 2:
						rootPath = device.getNtfsDevicePath() + File.separator;
				}
				conf.put("rootPath",rootPath);
				// 构造存储的路径和文件名，目录为201505/13/，文件名用uuid
				String savePath = InfoHelper.getPicSavePath(request) + UUID.randomUUID();
				conf.put("savePath",savePath);
				state = new XYUploader(request, conf).doExec();
				InfoHelper.prepare4Extract(device, savePath + ".jpg");
				state.putInfo("url",device.getDeviceName() + ";" + savePath + ".jpg");
				break;

			case ActionMap.UPLOAD_VIDEO:
			/*case ActionMap.UPLOAD_FILE:
				conf = this.configManager.getConfig(actionCode);
				state = new Uploader(request, conf).doExec();
				break;*/
	
			case ActionMap.CATCH_IMAGE:
				conf = configManager.getConfig(actionCode);
				String[] list = this.request.getParameterValues((String) conf
						.get("fieldName"));
				state = new ImageHunter(conf).capture(list);
				break;
	
			case ActionMap.LIST_IMAGE:
			case ActionMap.LIST_FILE:
				conf = configManager.getConfig(actionCode);
				int start = this.getStartIndex();
				state = new FileManager(conf).listFile(start);
				break;
			case ActionMap.UPLOAD_FILE:
				conf = this.configManager.getConfig(4);
				conf.put("isBase64", "editorFileUpload");
				state = new XYUploader(request, conf).doExec();
				break;
		}
		return state.toJSONString();
	}

	public int getStartIndex() {
		
		String start = this.request.getParameter("start");
		try {
			return Integer.parseInt(start);
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * callback参数验证
	 */
	public boolean validCallbackName(String name) {
		if (name.matches("^[a-zA-Z_]+[\\w0-9_]*$")) {
			return true;
		}
		return false;
	}
}