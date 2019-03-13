package com.founder.xy.ueditor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.baidu.ueditor.PathFormat;
import com.baidu.ueditor.define.AppInfo;
import com.baidu.ueditor.define.BaseState;
import com.baidu.ueditor.define.FileType;
import com.baidu.ueditor.define.State;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.sys.StorageDevice;
import com.founder.xy.commons.InfoHelper;

/**
 * 百度编辑器图片上传功能类
 */
public class XYBinaryUploader {

	/** 上传文件 */
	public static final State save(HttpServletRequest request,
			Map<String, Object> conf) {
		FileItemStream fileStream = null;
		boolean isAjaxUpload = request.getHeader("X_Requested_With") != null;

		if (!ServletFileUpload.isMultipartContent(request)) {
			return new BaseState(false, AppInfo.NOT_MULTIPART_CONTENT);
		}
		ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
		if (isAjaxUpload) {
			upload.setHeaderEncoding("UTF-8");
		}
		try {
			FileItemIterator iterator = upload.getItemIterator(request);
			while (iterator.hasNext()) {
				fileStream = iterator.next();
				if (!fileStream.isFormField())
					break;
				fileStream = null;
			}
			if (fileStream == null) {
				return new BaseState(false, AppInfo.NOTFOUND_UPLOAD_DATA);
			}
			// /xy/ueditor/jsp/upload/image/{yyyy}{mm}{dd}/{time}{rand:6}
			String savePath = (String) conf.get("savePath");
			// Tulips.jpg
			String originFileName = fileStream.getName();
			// .jpg
			String suffix = FileType.getSuffixByFilename(originFileName);
			// Tulips
			originFileName = originFileName.substring(0,
					originFileName.length() - suffix.length());
			// /xy/ueditor/jsp/upload/image/{yyyy}{mm}{dd}/{time}{rand:6}.jpg
			savePath = savePath + suffix;

			long maxSize = ((Long) conf.get("maxSize")).longValue();

			if (!validType(suffix, (String[]) conf.get("allowFiles"))) {
				return new BaseState(false, AppInfo.NOT_ALLOW_FILE_TYPE);
			}

			// /xy/ueditor/jsp/upload/image/20150526/1432603626674021790.jpg
			savePath = PathFormat.parse(savePath, originFileName);
			// D:/java/workspace/cms50/WEB//xy/ueditor/jsp/upload/image/20150526/1432603626674021790.jpg
			String physicalPath = (String) conf.get("rootPath") + savePath;
			// 图片存储设备的名称
			StorageDevice device = InfoHelper.getPicDevice();
			if("editorFileUpload".equals(conf.get("isBase64")))
				device = InfoHelper.getAttachDevice();
			// 构造存储的路径和文件名，目录为201505/13/，文件名用uuid
			String usavePath = InfoHelper.getPicSavePath(request)+suffix;

			// 返回路径
			String picPath = "../../xy/image.do?path=" + device.getDeviceName() + ";" + usavePath.replaceAll("\\\\", "/");
			if("editorFileUpload".equals(conf.get("isBase64")))
				picPath = "/xy/file.do?path=" + device.getDeviceName() + ";" + usavePath.replaceAll("\\\\", "/");
			State storageState = null;
			InputStream is = fileStream.openStream();// 文件流
			try {
				storageState = XYStorageManager.saveFileByInputStream(is,
						physicalPath, maxSize, device, usavePath);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				ResourceMgr.closeQuietly(is);
			}
			
			if (storageState.isSuccess()) {
				//针对图片添加抽图任务
				if(!"editorFileUpload".equals(conf.get("isBase64")))
					InfoHelper.prepare4Extract(device, usavePath);

				storageState.putInfo("url", picPath);
				storageState.putInfo("type", suffix);
				storageState.putInfo("original", originFileName + suffix);
				storageState.putInfo("title", originFileName + suffix);
			}
			return storageState;
		} catch (FileUploadException e) {
			return new BaseState(false, AppInfo.PARSE_REQUEST_ERROR);
		} catch (IOException e) {
		}
		return new BaseState(false, AppInfo.IO_ERROR);
	}

	/** 验证上传文件后缀名 */
	private static boolean validType(String type, String[] allowTypes) {
		List<String> list = Arrays.asList(allowTypes);

		return list.contains(type);
	}
}
