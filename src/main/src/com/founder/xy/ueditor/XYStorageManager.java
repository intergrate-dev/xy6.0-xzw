package com.founder.xy.ueditor;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.PicHelper;
import net.coobird.thumbnailator.Thumbnails;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;

import com.baidu.ueditor.define.AppInfo;
import com.baidu.ueditor.define.BaseState;
import com.baidu.ueditor.define.State;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.sys.StorageDevice;
import com.founder.e5.sys.StorageDeviceManager;
import com.founder.e5.sys.SysFactory;

/**
 * 百度编辑器图片上传功能管理器
 */
public class XYStorageManager {
	public static final int BUFFER_SIZE = 8192;

	/** 构造方法 */
	public XYStorageManager() {
	}

	public static State saveBinaryFile(byte[] data, String path) {
		File file = new File(path);
		State state = valid(file);
		if (!state.isSuccess()) {
			return state;
		}
		try {
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(file));
			bos.write(data);
			bos.flush();
			bos.close();
		} catch (IOException ioe) {
			return new BaseState(false, AppInfo.IO_ERROR);
		}
		state = new BaseState(true, file.getAbsolutePath());
		state.putInfo( "size", data.length );
		state.putInfo( "title", file.getName() );
		return state;
	}

	/** 保存文件 */
	public static State saveFileByInputStream(InputStream is, String path,
			long maxSize, StorageDevice device, String usavePath) {
		
		State state = null;
		File tmpFile = getTmpFile();
		byte[] dataBuf = new byte[ 2048 ];
		BufferedInputStream bis = new BufferedInputStream(is, XYStorageManager.BUFFER_SIZE);
		try {
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(tmpFile), XYStorageManager.BUFFER_SIZE);
			int count = 0;
			while ((count = bis.read(dataBuf)) != -1) {
				bos.write(dataBuf, 0, count);
			}
			bos.flush();
			bos.close();


			long temFileLen = tmpFile.length();
			if (temFileLen > maxSize) {
				tmpFile.delete();
				return new BaseState(false, AppInfo.MAX_SIZE);
			}
			int userMaxSize =0;
			boolean zipFlag = false;
			String userMaxSizeStr = InfoHelper.getConfig( "写稿", "上传图片大小限制");
			if(userMaxSizeStr != null &&! "".equals(userMaxSizeStr)){
				userMaxSize= Integer.parseInt(userMaxSizeStr);
				if(userMaxSize > 0)
					zipFlag = true;
			}

			int pos = usavePath.lastIndexOf(".");
			String picSuffix = "";
			if (pos != -1) {
				picSuffix = usavePath.substring(usavePath.lastIndexOf("."));
			}
			if(zipFlag && !".gif".equals(picSuffix) && tmpFile.length() >1024 * 1024 * userMaxSize){
				tmpFile = PicHelper.picZip(tmpFile,800,picSuffix);
			}
			state = saveTmpFile(tmpFile, path, device, usavePath);
			if (!state.isSuccess()) {
				tmpFile.delete();
			}
			return state;
		} catch (IOException e) {
		}
		return new BaseState(false, AppInfo.IO_ERROR);
	}

	/** 取得临时文件 */
	private static File getTmpFile() {
		File tmpDir = FileUtils.getTempDirectory();
		String tmpFileName = (Math.random() * 10000 + "").replace(".", "");
		return new File(tmpDir, tmpFileName);
	}

	/** 保存文件到服务器上面 */
	private static State saveTmpFile(File tmpFile, String path,
			StorageDevice device, String usavePath) {
		
		State state = null;
		File targetFile = new File(path);
		if (targetFile.canWrite()) {
			return new BaseState(false, AppInfo.PERMISSION_DENIED);
		}
		//开始存储到存储设备上
		StorageDeviceManager sdManager = SysFactory.getStorageDeviceManager();
		InputStream in = null;
		try {
			in = new FileInputStream(tmpFile);
			sdManager.write(device, usavePath, in);
			
		} catch (Exception e) {
			return new BaseState(false, AppInfo.IO_ERROR);
		} finally {
			ResourceMgr.closeQuietly(in);
		}

		state = new BaseState(true);
		state.putInfo( "size", tmpFile.length() );
//		state.putInfo( "title", usavePath.substring(10) );
		return state;
	}

	private static State valid(File file) {
		File parentPath = file.getParentFile();
		if ((!parentPath.exists()) && (!parentPath.mkdirs())) {
			return new BaseState(false, AppInfo.FAILED_CREATE_FILE);
		}
		if (!parentPath.canWrite()) {
			return new BaseState(false, AppInfo.PERMISSION_DENIED);
		}
		return new BaseState(true);
	}
}
