package com.founder.xy.system.job;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.founder.e5.commons.DomHelper;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.scheduler.BaseJob;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.system.Tenant;

/**
 * 图片文件清理服务。
 * 每天执行一次，清理特定间隔前的一天（如三天前）上传的未引用的图片文件。
 * 
 * 扫描图片文件存储目录，对每个文件检查：
 * 判断是否出现在附件表、互动附件表、图片库、视频库（关键帧地址）
 * 
 * @author Gong Lijie
 */
public class PicCleanJob extends BaseJob{

	public PicCleanJob() {
		super();
		log = Context.getLog("xy.picClean");
	}

	@Override
	protected void execute() throws E5Exception {
		log.info("-----图片文件清理服务，开始启动-----");
		
		//保险起见，清理30天前的
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -30);
		Date date = cal.getTime();
		
		String datePath = formatDate("yyyyMM/dd/", date);
		
		//图片存储设备的根目录
		String devName = InfoHelper.getConfig("存储设备", "图片存储设备");
		String devPath = InfoHelper.getDevicePath(InfoHelper.getPicDevice());
		
		//取出所有已部署的租户
		List<String> tenantList = getTenantList();
		for (String tenantCode : tenantList) {
			// 扫描所有文件
			String relativePath = tenantCode + "/" + datePath;////相对路径
			File[] fileArr = scanPicFiles(devPath, relativePath);
			if (fileArr == null || fileArr.length == 0) continue;
			
			log.info(relativePath + "：发现" + fileArr.length + "张图片");
			
			//读出该租户的图片库ID、视频库ID、附件表ID、互动附件表ID
			int[] libIDs = {
				LibHelper.getLibID(DocTypes.PHOTO.typeID(), tenantCode),
				LibHelper.getLibID(DocTypes.VIDEO.typeID(), tenantCode),
				LibHelper.getLibID(DocTypes.ATTACHMENT.typeID(), tenantCode),
				LibHelper.getLibID(DocTypes.NISATTACHMENT.typeID(), tenantCode)
			};
			
			// 将没被引用的文件删除
			int count = 0;
			for (int i = 0; i < fileArr.length; i++) {
				if (i > 0 && i % 100 == 0) log.info("已扫描" + i + "张图片");
				
				// 检查图片文件是否被引用，无引用就删除
				File file = fileArr[i];
				if (!exist(devName, relativePath, file.getName(), libIDs)){
					count++;
					log.info("清理无用图片：" + file.getName());
					clear(file);
				}
			}
			log.info(relativePath + "：清理图片共计" + count);
			
		}
		log.info("清理完毕");
	}
	
	/**
	 * 读租户表，得到所有已部署的租户的代号。
	 * 若该表为空，则认为只有一个默认租户，代号为Tenant.DEFAULTCODE
	 */
	private List<String> getTenantList() throws E5Exception {
		
		List<String> list = new ArrayList<String>();
		
		int tLibID = DomHelper.getDocLibID(DocTypes.TENANT.typeID());
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] docs = docManager.find(tLibID, "te_deployed=1 AND SYS_DELETEFLAG=0", null);

		int length = docs.length;
		if(0 == length){
			list.add(Tenant.DEFAULTCODE);
		} else {
			for (int i = 0; i < length; i++) {
				list.add(docs[i].getString("te_code"));
			}
		}
		return list;
	}
	
	private String formatDate(String type, Date date) throws E5Exception {
		return new SimpleDateFormat(type).format(date);
	}

	/**
	 * 获取前N天图片存储文件夹的物理地址
	 */
	private File[] scanPicFiles(String devPath, String relativePath) throws E5Exception {
		String picPath = devPath + File.separator + relativePath;
		File[] fileArr = new File(picPath).listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				name = name.toLowerCase();
				
				boolean filtered = name.endsWith(".0")
								|| name.endsWith(".0.jpg")
								|| name.endsWith(".1")
								|| name.endsWith(".1.jpg")
								|| name.endsWith(".2")
								|| name.endsWith(".2.jpg")
								;
				return !filtered;
			}
		});
		
		return fileArr;
	}

	/**
	 * @param relativePath
	 * @param fileName
	 * @param libIDs 库ID数组：0——图片库ID，1——视频库ID，2——附件表ID，3——互动附件表ID
	 * @return
	 */
	private boolean exist(String devName, String relativePath, String fileName, int[] libIDs) {
		String path = devName + ";" + relativePath + fileName;
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Object[] params = new Object[]{path};
		Document[] docs = null;
		try {
			//查附件库
			docs = docManager.find(libIDs[2], "att_path=?", params);
			if (docs != null && docs.length > 0) return true;
			
			//若不是标题图片，则再从下面的库查一次
			if (!fileName.startsWith("t0_") && !fileName.startsWith("t1_") && !fileName.startsWith("t2_") ) {
				//查图片库
				docs = docManager.find(libIDs[0], "p_path=?", params);
				if (docs != null && docs.length > 0) return true;
				
				//查互动附件库
				docs = docManager.find(libIDs[3], "att_path=?", params);
				if (docs != null && docs.length > 0) return true;
				
				//查视频库
				docs = docManager.find(libIDs[1], "v_picPath=?", params);
				if (docs != null && docs.length > 0) return true;
			}
			return false;
		} catch (E5Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	//删掉没被引用的图片文件，同时删掉抽图文件
	private void clear(File file) {
		try {
			if (file.exists()) file.delete();

			String fileName = file.getCanonicalPath();
			
			file = new File(fileName + ".2");
			if (file.exists()) file.delete();
			
			file = new File(fileName + ".2.jpg");
			if (file.exists()) file.delete();
			
			file = new File(fileName + ".1");
			if (file.exists()) file.delete();
			
			file = new File(fileName + ".1.jpg");
			if (file.exists()) file.delete();
			
			file = new File(fileName + ".0");
			if (file.exists()) file.delete();
			
			file = new File(fileName + ".0.jpg");
			if (file.exists()) file.delete();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
