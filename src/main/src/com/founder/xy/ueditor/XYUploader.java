package com.founder.xy.ueditor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.baidu.ueditor.define.State;
import com.baidu.ueditor.upload.Base64Uploader;

/**
 * 百度编辑器上传功能类
 */
public class XYUploader {
	private HttpServletRequest request = null;
	private Map<String, Object> conf = null;

	/** 构造方法 */
	public XYUploader(HttpServletRequest request, Map<String, Object> conf) {
		this.request = request;
		this.conf = conf;
	}

	/** 上传功能入口方法 */
	public final State doExec() {
		String filedName = (String) this.conf.get("fieldName");
		State state = null;
		//editorFileUpload代表编辑器添加附件
		if("editorFileUpload".equals(this.conf.get("isBase64"))){

			state = XYBinaryUploader.save(this.request, this.conf);
		
		}else if ("true".equals(this.conf.get("isBase64"))) {
			state = Base64Uploader.save(this.request.getParameter(filedName),
					this.conf);
		} 
		else {
			state = XYBinaryUploader.save(this.request, this.conf);
		}
		return state;
	}
}
