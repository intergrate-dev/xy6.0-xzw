package com.founder.xy.nis;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import com.founder.e5.commons.DomHelper;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.context.CacheReader;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.doc.util.E5docHelper;
import com.founder.e5.sys.StorageDevice;
import com.founder.e5.sys.StorageDeviceManager;
import com.founder.e5.sys.SysFactory;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.jpublish.BaseDataCache;
import com.founder.xy.jpublish.PublishHelper;

/**
 * 论坛管理器
 */
@Component
public class ForumManager {
	
	/**对帖子评论进行加1*/
	public void addForumCount( int docLibID, long docID) throws E5Exception {
		String sql = null;
		//若是话题问答，则传入的是话题问答库ID 和 话题ID
		if (DomHelper.getDocTypeIDByLibID(docLibID) == DocTypes.SUBJECTQA.typeID()) {
			docLibID = LibHelper.getLibIDByOtherLib(DocTypes.SUBJECT.typeID(), docLibID);
			String docLibTable = LibHelper.getLibTable(docLibID);
			//话题提问数+1
			sql = "update " + docLibTable + " set a_countQuestion=a_countQuestion+1 where SYS_DOCUMENTID=?";
		} else {
			String docLibTable = LibHelper.getLibTable(docLibID);
			sql = "update " + docLibTable + " set a_countDiscuss=a_countDiscuss+1 where SYS_DOCUMENTID=?";
		}
		
		InfoHelper.executeUpdate(docLibID, sql, new Object[]{docID});
	}
	
	/**对帖子评论进行减1*/
	public void decreaseForumCount( int docLibID, long docID) throws E5Exception {
		String sql = null;
		
		//若是话题问答，则传入的是话题问答库ID 和 话题ID
		if (DomHelper.getDocTypeIDByLibID(docLibID) == DocTypes.SUBJECTQA.typeID()) {
			docLibID = LibHelper.getLibIDByOtherLib(DocTypes.SUBJECT.typeID(), docLibID);
			String docLibTable = LibHelper.getLibTable(docLibID);
			//话题提问数-1
			sql = "update " + docLibTable + " set a_countQuestion=a_countQuestion-1 where SYS_DOCUMENTID=?";
		} else {
			String docLibTable = LibHelper.getLibTable(docLibID);
			sql = "update " + docLibTable + " set a_countDiscuss=a_countDiscuss-1 where SYS_DOCUMENTID=?";
		}
		InfoHelper.executeUpdate(docLibID, sql, new Object[]{docID});
	}
	
	/** 多个帖子的统一提交， 出错时返回错误信息 */
	public String save(int docLibID, List<Document> frums) {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DBSession conn = null;
		try {
			conn = E5docHelper.getDBSession(docLibID);;
			conn.beginTransaction();
			for (Document frum : frums) {
				docManager.save(frum, conn);
			}	
			conn.commitTransaction();
			return null;
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			return "error:" + e.getLocalizedMessage();
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
	}
	
	/**删除论坛帖子*/
	public String delete(int docLibID, List<Document> forums){
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DBSession conn = null;
		try {
			conn = E5docHelper.getDBSession(docLibID);;
			conn.beginTransaction();	
			for(Document forum : forums){
				docManager.delete(forum);
			}
			conn.commitTransaction();

			return null;
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			return "操作中出现错误：" + e.getLocalizedMessage();
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
	}

	/**
	 * 获得order的最大值
	 * @param DocLibID
	 * @param a_order
	 * @return
	 * @throws E5Exception
	 */
	public BigDecimal getMaxOrder(int DocLibID, BigDecimal a_order) throws E5Exception {
		String relTable = LibHelper.getLibTable(DocLibID);
		String sql = "select max(a_order+1) max_order from " + relTable;
		DBSession conn = null;
		BigDecimal max_order;
		try {
			conn = E5docHelper.getDBSession(DocLibID);
			max_order = (BigDecimal) conn.getObject(sql, null);
			max_order = max_order == null ? new BigDecimal("0") : max_order;

			//取A和B的较大者作为新的a_order。
			a_order = a_order.compareTo(max_order) > 0 ? a_order : max_order;

		} catch (SQLException e) {
			throw new E5Exception(e);
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
		return a_order;
	}

	/**
	 * 审批通过时，处理附件。
	 * 
     * 若是后台提交的图片（目标存储设备与源存储设备不同），判断是否已经抽图，先转到手机存储设备，再生成trans
     * 若是手机端提交的图片，判断是否已经抽图，直接生成trans
     * 
     * 若是后台提交的视频（在纳加系统），不处理
     * 若是手机端提交的视频，生成trans
     * 
	 * @param doc
	 * @throws E5Exception
	 * @return 0：正常，-1：没有抽图，-2：复制中异常
	 */
	public int transWhenPass(Document doc) throws E5Exception {
		Document[] atts = readAtts(doc);
		if (atts == null || atts.length == 0) return 0;
		
		//先处理图片
		int result = transWhenPass(doc, atts, 1);
		if (result != 0) return result;
		
		//再处理视频。后台编辑提交的视频在纳加系统里，不需要转发。
		result = transWhenPass(doc, atts, 2);
		if (result != 0) return result;

		//处理标题图：标题图(大)需抽图
		result = transWhenPass(doc, atts, 3);
		if (result != 0) return result;
		result = transWhenPass(doc, atts, 4);
		if (result != 0) return result;
		result = transWhenPass(doc, atts, 5);
		/*
		//从图片中取一张作为列表图（优先标题图小，其次标题图中，再次标题图大，最后附件图的第一张）
		String imgUrl = getImgUrl(atts);
		doc.set("a_imageUrl", imgUrl);
		*/
		return result;
	}
	
	/*
	private String getImgUrl(Document[] atts) {
		//列表图优先取小图
		Document[] pics = getAttByType(atts, 5);
		if (pics == null || pics.length == 0) {
			//其次中图
			pics = getAttByType(atts, 4);
			if (pics == null || pics.length == 0) {
				//再次大图
				pics = getAttByType(atts, 3);
				
				if (pics == null || pics.length == 0) {
					//再次附件图片
					pics = getAttByType(atts, 1);
				}
			}
		}
		if (pics == null || pics.length == 0)
			return "";
		else
			return pics[0].getString("att_url");
	}
	*/
	
	private int transWhenPass(Document doc, Document[] atts, int type) throws E5Exception {
		//读出附件表中的附件记录，以便修改url为外网地址
		Document[] attDocs = getAttByType(atts, type);
		if (attDocs == null || attDocs.length == 0) return 0;
		
		StorageDevice device = getDevice(type);
		
		//站点根目录，trans的根目录
		String webRoot = getWebRoot(doc);
		
		for (int i = 0; i < attDocs.length; i++) {
			String path = attDocs[i].getString("att_path");
			String url = attDocs[i].getString("att_url");
			if (url != null && url.toLowerCase().startsWith("http")) continue;
			if (path == null || path.toLowerCase().startsWith("http")) continue;
			
			int result = (type == 2)  
					? transVideo(path, device, webRoot)
					: copyAndTransPic(path, device, webRoot, attDocs[i], type);
			if (result < 0)
				return result;
		}
		return 0;
	}
	
	/**
	 * 若图片已经抽图，则把图片文件复制到手机存储设备、生成分发文件，并修改url
	 */
	private int copyAndTransPic(String srcPathFile, StorageDevice destDevice, String webRoot, Document attDoc, int type) {
		int pos = srcPathFile.indexOf(";");
		if (pos < 0) return 0;
		
		String srcDeviceName = srcPathFile.substring(0, pos);
		String filePath = srcPathFile.substring(pos + 1);
		
		StorageDeviceManager sdManager = SysFactory.getStorageDeviceManager();
		try {
			//存储设备可能是编码过的，先解码
			srcDeviceName = URLDecoder.decode(srcDeviceName, "UTF-8");
			StorageDevice srcDevice = sdManager.getByName(srcDeviceName);
			
			//先判断是否抽图完毕：只判断大图是否存在即可
			String srcDevicePath = InfoHelper.getDevicePath(srcDevice);
			if (type == 1 || type == 3) {
				//是普通图片附件、或标题图大图，则检查抽图；否则（是标题图中图和小图，则不检查）
				boolean isExtracted = extracted(srcDevicePath, filePath);
				if (!isExtracted) {
					return -1; //未完成抽图
				}
			}
			
			String destDevicePath = InfoHelper.getDevicePath(destDevice);
			
			boolean sameDevice = srcDeviceName.equals(destDevice.getDeviceName());
			if (sameDevice) {
				//手机端上传的图片
				String destFilePath = destDevicePath + "/" + filePath;
				//手机端上传的都是jpg？若以后发现手机端上传格式不止jpg，则仍需从.0.jpg复制为.0
				//copyTo(destFilePath);
				//分发
				PublishHelper.writeTransPath(destFilePath, webRoot);
				PublishHelper.writeTransPath(destFilePath + ".0", webRoot);
				PublishHelper.writeTransPath(destFilePath + ".1", webRoot);
				PublishHelper.writeTransPath(destFilePath + ".2", webRoot);
			} else {
				//后台上传的图片，需要复制和分发
				oneFileCopyTrans(srcDevicePath, filePath, destDevicePath, webRoot, null);
				oneFileCopyTrans(srcDevicePath, filePath, destDevicePath, webRoot, ".0");
				oneFileCopyTrans(srcDevicePath, filePath, destDevicePath, webRoot, ".1");
				oneFileCopyTrans(srcDevicePath, filePath, destDevicePath, webRoot, ".2");
			}
			
			//修改附件的Url
			String url = destDevice.getHttpDeviceURL() + "/" + filePath;
			changeUrl(attDoc, url);
			return 0;
		} catch (E5Exception | IOException e1) {
			e1.printStackTrace();
			return -2;
		}
	}
	//是否已抽图：只判断大图是否存在即可（优先用.2.jpg，无时再判断.2）
	private boolean extracted(String devicePath, String savePath) {
		boolean extracted = new File(devicePath, savePath + ".2.jpg").exists();
		if (!extracted) extracted = new File(devicePath, savePath + ".2").exists();
		
		return extracted;
	}
	
	/**
	 * 手机端提交的视频文件，生成分发文件。
	 * 手机端提交后api里就写了url，所以不必再改url
	 */
	private int transVideo(String srcPathFile, StorageDevice destDevice, String webRoot) {
		int pos = srcPathFile.indexOf(";");
		if (pos < 0) return 0;
		
		String filePath = srcPathFile.substring(pos + 1);
		
		String destDevicePath = InfoHelper.getDevicePath(destDevice);
		String destFilePath = destDevicePath + "/" + filePath;
		
		PublishHelper.writeTransPath(destFilePath, webRoot);
		return 0;
	}
	
	//复制文件到手机存储目录，生成trans文件
	private void oneFileCopyTrans(String srcDevicePath, String srcFile, String destDevicePath, 
			String webRoot, String suffix) {
		String destFilePath = destDevicePath + "/" + srcFile;
		if (suffix != null) destFilePath += suffix;
		
		try {
			File file = getSaveFile(srcDevicePath, srcFile, suffix);
			if (file.exists()) {
				//复制
				FileUtils.copyFile(file, new File(destFilePath));
				
				//生成trans分发信息文件
				PublishHelper.writeTransPath(destFilePath, webRoot);
			}
		} catch (IOException e) {
			System.out.println(e.getLocalizedMessage());
			
			System.out.println("-------params :----------");
			System.out.println("srcDevicePath:" + srcDevicePath);
			System.out.println("destFilePath:" + destFilePath);
			System.out.println("srcFile:" + srcFile);
			System.out.println("webRoot:" + webRoot);
		}
	}
	//取源图片文件供复制。优先用.2.jpg，无时再用.2
	private File getSaveFile(String devicePath, String savePath, String suffix) {
		File file = null;
		if (suffix != null) {
			file = new File(devicePath, savePath + suffix + ".jpg");
			if (!file.exists())
				file = new File(devicePath, savePath + suffix);
		} else {
			file = new File(devicePath, savePath);
		}
		return file;
	}
	

	private StorageDevice getDevice(int type) throws E5Exception {
		String paramName = (type == 2) ? "手机视频存储设备" : "手机图片存储设备";
		StorageDevice device = InfoHelper.getDevice("互动", paramName);
		if (device == null) {
			throw new E5Exception("没有配置" + paramName + "？");
		}
		return device;
	}

	private String getWebRoot(Document liveDoc) {
		int siteID = liveDoc.getInt("a_siteID");
		int libID = LibHelper.getLibIDByOtherLib(DocTypes.SITE.typeID(), liveDoc.getDocLibID());
		
		BaseDataCache siteCache = (BaseDataCache) (CacheReader.find(BaseDataCache.class));
		return siteCache.getSiteWebRootByID(libID, siteID);
	}

	/**
	 * 读出所有附件
	 */
	private Document[] readAtts(Document doc) {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		int attLibID = LibHelper.getLibIDByOtherLib(DocTypes.NISATTACHMENT.typeID(), doc.getDocLibID());
		try {
			Document[] attDocs = docManager.find(attLibID, "att_articleID=? and att_articleLibID=?", 
					new Object[]{doc.getDocID(), doc.getDocLibID()});
			return attDocs;
		} catch (E5Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	private Document[] getAttByType(Document[] atts, int type) {
		if (atts == null || atts.length == 0) return null;
		
		List<Document> result = new ArrayList<>();
		for (Document att : atts) {
			if (att.getInt("att_type") == type)
				result.add(att);
		}
		return result.toArray(new Document[0]);
	}

	private void changeUrl(Document attach, String url) throws E5Exception {
		//String path = (String)pics.get(index);
		//修改json中的url
		//pics.set(index, url);
		
		//修改附件表中的url
		attach.set("att_url", url);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		docManager.save(attach);
	}
}