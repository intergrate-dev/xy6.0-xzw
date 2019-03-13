package com.founder.amuc.vote;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

import bsh.StringUtil;
import org.apache.axis.utils.StringUtils;
import org.apache.commons.fileupload.FileItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.fusesource.hawtbuf.ByteArrayInputStream;

import com.founder.amuc.commons.Constant;
import com.founder.amuc.commons.InfoHelper;
import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.Log;
import com.founder.e5.commons.ResourceMgr;
import com.founder.amuc.commons.DateFormatAmend;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.doc.util.E5docHelper;
import com.founder.e5.dom.DocLib;
import com.founder.e5.sys.StorageDeviceManager;
import com.founder.e5.sys.StorageDevice;
import com.founder.e5.sys.SysFactory;


/**
 * @author libin1.dz
 * 投票系统使用的方法类
 */
@Component
public class VoteManagerAmuc {
	public static final String ATTACH_NAME = "头像存储";
	private Log log = Context.getLog("amuc.vote");
	/**
	 * 增加一条投票选项
	 * @param voteId
	 * @param optext
	 * @return
	 * @throws Exception
	 */
	public Document saveVoteOption(int voteId ,String optext,int themeId) throws Exception{
		
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_VOTEOPTION, "xy");
		
		DocumentManager documentManager = DocumentManagerFactory.getInstance();
		Document docVop = documentManager.newDocument(docLib.getDocLibID(), InfoHelper.getID(docLib.getDocTypeID()));
		docVop.setFolderID(docLib.getFolderID());
		docVop.setDeleteFlag(0);
		docVop.set("voVoteID", voteId);
		docVop.set("voCreated",DateUtils.getTimestamp());
		docVop.set("voLastModified",DateUtils.getTimestamp());
		docVop.set("voName",optext);
		docVop.set("voIndex", this.getIndexByVoteId(voteId));
		docVop.set("voVotes", 0);
		docVop.set("voThemeID", themeId);
		docVop.set("voShowOpImgOnpage", 1);
		documentManager.save(docVop);
		
		
		return docVop;
	}
	
	/**
	 * 根据选项ID更新其内容
	 * @param opId
	 * @param optext
	 * @return
	 * @throws Exception
	 */
	public VoteOptions updateOptionTextById(int opId ,String optext) throws Exception{
		DBSession conn = null;
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_VOTEOPTION, "xy");
		VoteOptions voteOptions = new VoteOptions();
		try {
			conn = E5docHelper.getDBSession(docLib.getDocLibID());
			conn.beginTransaction(); // 开始事务
			
			String exeSqlStr = "update xy_membervoteoptions set voName=?,voLastModified=? where SYS_DOCUMENTID=?";
			Object[] params = new Object[]{optext,DateUtils.getTimestamp(),opId};
		
			conn.executeUpdate(exeSqlStr, params);
			conn.commitTransaction();
			voteOptions = this.getOptionById(opId);
			
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			e.printStackTrace();
			return null;
		} finally {
			ResourceMgr.closeQuietly(conn);
		} 
		return voteOptions;
	}
	/**
	 * 根据选项ID更新其内容voVideoAdd
	 * @param opId
	 * @param optext
	 * @return
	 * @throws Exception
	 */
	public VoteOptions updateOptionVideoById(int opId ,String voVideoAdd) throws Exception{
		DBSession conn = null;
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_VOTEOPTION, "xy");
		VoteOptions voteOptions = new VoteOptions();
		try {
			conn = E5docHelper.getDBSession(docLib.getDocLibID());
			conn.beginTransaction(); // 开始事务
			
			String exeSqlStr = "update xy_membervoteoptions set voVideoAdd=?,voLastModified=? where SYS_DOCUMENTID=?";
			Object[] params = new Object[]{voVideoAdd,DateUtils.getTimestamp(),opId};
		
			conn.executeUpdate(exeSqlStr, params);
			conn.commitTransaction();
			voteOptions = this.getOptionById(opId);
			
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			e.printStackTrace();
			return null;
		} finally {
			ResourceMgr.closeQuietly(conn);
		} 
		return voteOptions;
	}
	/**
	 * 根据选项ID更新其内容voVideoAdd
	 * @param opId
	 * @param optext
	 * @return
	 * @throws Exception
	 */
	public VoteOptions updateOptionVideoIsNull(int opId) throws Exception{
		DBSession conn = null;
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_VOTEOPTION, "xy");
		VoteOptions voteOptions = new VoteOptions();
		try {
			conn = E5docHelper.getDBSession(docLib.getDocLibID());
			conn.beginTransaction(); // 开始事务
			
			String exeSqlStr = "update xy_membervoteoptions set voVideoAdd=null,voLastModified=? where SYS_DOCUMENTID=?";
			Object[] params = new Object[]{DateUtils.getTimestamp(),opId};
		
			conn.executeUpdate(exeSqlStr, params);
			conn.commitTransaction();
			voteOptions = this.getOptionById(opId);
			
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			e.printStackTrace();
			return null;
		} finally {
			ResourceMgr.closeQuietly(conn);
		} 
		return voteOptions;
	}
	/**
	 * 根据选项ID修改排序码
	 * @param opId
	 * @param optionIndex
	 * @return
	 * @throws Exception
	 */
	public VoteOptions updateOptionIndex(int opId , int optionIndex) throws Exception{
		DBSession conn = null;
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_VOTEOPTION, "xy");
		VoteOptions voteOptions = new VoteOptions();
		try {
			conn = E5docHelper.getDBSession(docLib.getDocLibID());
			conn.beginTransaction(); // 开始事务
			
			String exeSqlStr = "update xy_membervoteoptions set voIndex=?,voLastModified=? where SYS_DOCUMENTID=?";
			Object[] params = new Object[]{optionIndex,DateUtils.getTimestamp(),opId};
		
			conn.executeUpdate(exeSqlStr, params);
			conn.commitTransaction();
			voteOptions = this.getOptionById(opId);
			
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			e.printStackTrace();
			return null;
		} finally {
			ResourceMgr.closeQuietly(conn);
		} 
		return voteOptions;
	}
	
	/**
	 * 根据选项ID获取当前选项数据
	 * @param opId
	 * @return
	 * @throws Exception
	 */
	public VoteOptions getOptionById(int opId) throws Exception{
		DBSession conn = null;
		IResultSet rs = null;
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_VOTEOPTION, "xy");
		VoteOptions voteOptions = new VoteOptions();
		try {
			conn = E5docHelper.getDBSession(docLib.getDocLibID());
			String selectSql = "select * from xy_membervoteoptions where SYS_DOCUMENTID=? and SYS_DELETEFLAG=0"; 
			Object[] selParams = new Object[]{opId};
			rs = conn.executeQuery(selectSql, selParams);
			
			while(rs.next()){
				voteOptions.setDeleteFlag(rs.getInt("SYS_DELETEFLAG"));
				voteOptions.setVoteOpId(rs.getInt("SYS_DOCUMENTID"));
				voteOptions.setVoteId(rs.getInt("voVoteID"));
				voteOptions.setVoName(rs.getString("voName"));
				voteOptions.setVoImgAdd(rs.getString("voImgAdd"));
				voteOptions.setVoClassification(rs.getString("voClassification"));
				voteOptions.setVoType(rs.getInt("voType"));
				voteOptions.setVoIndex(rs.getInt("voIndex"));
				voteOptions.setVoThemeId(rs.getInt("voThemeID"));
				voteOptions.setVoViewContent(rs.getString("voViewPageContent"));
				voteOptions.setVoShowOpImgFlag(rs.getInt("voShowOpImgOnpage"));
				voteOptions.setVoVideoAdd(rs.getString("voVideoAdd"));
			}
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			e.printStackTrace();
			return null;
		} finally {
			ResourceMgr.closeQuietly(conn);
		} 
		return voteOptions;
	}
	
	/**
	 * 根据选项ID获取选项和图片信息
	 * @param opId
	 * @return
	 * @throws Exception
	 */
	public VoteOptions getOptionAndImgInfoById(int opId) throws Exception{
		DBSession conn = null;
		IResultSet rs = null;
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_VOTEOPTION, "xy");
		VoteOptions voteOptions = new VoteOptions();
		VoteImageInfo voteImageInfo = new VoteImageInfo();
		try {
			conn = E5docHelper.getDBSession(docLib.getDocLibID());
			String selectSql = "select * from xy_membervoteoptions where SYS_DOCUMENTID=? and SYS_DELETEFLAG=0"; 
			Object[] selParams = new Object[]{opId};
			rs = conn.executeQuery(selectSql, selParams);
			
			while(rs.next()){
				voteOptions.setDeleteFlag(rs.getInt("SYS_DELETEFLAG"));
				voteOptions.setVoteOpId(rs.getInt("SYS_DOCUMENTID"));
				voteOptions.setVoteId(rs.getInt("voVoteID"));
				voteOptions.setVoName(rs.getString("voName"));
				voteOptions.setVoImgAdd(rs.getString("voImgAdd"));
				voteOptions.setVoClassification(rs.getString("voClassification"));
				voteOptions.setVoType(rs.getInt("voType"));
				voteOptions.setVoIndex(rs.getInt("voIndex"));
				voteOptions.setVoThemeId(rs.getInt("voThemeID"));
				voteOptions.setVoViewContent(rs.getString("voViewPageContent"));
				voteOptions.setVoShowOpImgFlag(rs.getInt("voShowOpImgOnpage"));
				voteOptions.setVoVideoAdd(rs.getString("voVideoAdd"));
				
				voteImageInfo = this.getImageInfoByOptionId(rs.getInt("SYS_DOCUMENTID"));
				if(voteImageInfo!=null){
					voteOptions.setVoImgAdd(voteImageInfo.getViAddress());
					voteOptions.setVoImgInfoId(voteImageInfo.getVoteImageId());
				}else{
					voteOptions.setVoImgAdd("");
					voteOptions.setVoImgInfoId(0);
				}
				
			}
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			e.printStackTrace();
			return null;
		} finally {
			ResourceMgr.closeQuietly(conn);
		} 
		return voteOptions;
	}
	
	/**
	 * 删除选项
	 * @param opId
	 * @param operate
	 * @return
	 * @throws Exception
	 */
	// 删除一条选项，逻辑上删除，SYS_DELETEFLAG=1
	public boolean deleteVoteOption(int opId, int operate) throws Exception{
		DBSession conn = null;
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_VOTEOPTION, "xy");
		try {
			conn = E5docHelper.getDBSession(docLib.getDocLibID());
			conn.beginTransaction(); // 开始事务
			
			String exeSqlStr = "update xy_membervoteoptions set SYS_DELETEFLAG=?,voLastModified=? where SYS_DOCUMENTID=?";
			Object[] params = new Object[]{operate, DateUtils.getTimestamp(), opId};
		
			conn.executeUpdate(exeSqlStr, params);
			conn.commitTransaction();
			
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			e.printStackTrace();
			return false;
		} finally {
			ResourceMgr.closeQuietly(conn);
		} 
		return true;
	}
	
	/**
	 * 通过投票ID查询选项表中未删除的选项的个数，然后加1，即为当前即将添加数据的排序码
	 * @param voteId
	 * @return
	 * @throws Exception
	 */
	public int getIndexByVoteId(int voteId) throws Exception{
		DBSession conn = null;
		IResultSet rs = null;
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_VOTEOPTION, "xy");
		int voteIndex = 0;
		try {
			conn = E5docHelper.getDBSession(docLib.getDocLibID());
			String selectSql = "select count(*) from xy_membervoteoptions where voVoteID=? and SYS_DELETEFLAG=0"; 
			Object[] selParams = new Object[]{voteId};
			rs = conn.executeQuery(selectSql, selParams);
			
			if(rs.next()){
				voteIndex = rs.getInt(1);
			}
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			e.printStackTrace();
			return 1;
		} finally {
			ResourceMgr.closeQuietly(conn);
		} 
		return voteIndex+1;
	}
	
	/**
	 * 根据投票ID初始化所有投票选项
	 * @param voteId
	 * @return
	 * @throws Exception
	 */
	public List<VoteOptions> getOptionsByThemeId(int themeId) throws Exception{
		
		List<VoteOptions> optionList = new ArrayList<VoteOptions>();
		VoteImageInfo voteImageInfo = new VoteImageInfo();
		DBSession conn = null;
		IResultSet rs = null;
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_VOTEOPTION, "xy");
		try {
			conn = E5docHelper.getDBSession(docLib.getDocLibID());
			String selectSql = "select * from xy_membervoteoptions where voThemeID=? and SYS_DELETEFLAG=0 order by SYS_DOCUMENTID DESC"; 
			Object[] selParams = new Object[]{themeId};
			rs = conn.executeQuery(selectSql, selParams);
			
			while(rs.next()){
				VoteOptions voteOptions = new VoteOptions();
				voteOptions.setDeleteFlag(rs.getInt("SYS_DELETEFLAG"));
				voteOptions.setVoteOpId(rs.getInt("SYS_DOCUMENTID"));
				voteOptions.setVoteId(rs.getInt("voVoteID"));
				voteOptions.setVoName(rs.getString("voName"));
				voteOptions.setVoClassification(rs.getString("voClassification"));
				voteOptions.setVoType(rs.getInt("voType"));
				voteOptions.setVoIndex(rs.getInt("voIndex"));
				voteOptions.setVoVotes(rs.getInt("voVotes"));
				voteOptions.setVoThemeId(rs.getInt("voThemeID"));
				voteImageInfo = this.getImageInfoByOptionId(rs.getInt("SYS_DOCUMENTID"));
				voteOptions.setVoViewContent(rs.getString("voViewPageContent"));
				voteOptions.setVoShowOpImgFlag(rs.getInt("voShowOpImgOnpage"));
				voteOptions.setVoVideoAdd(rs.getString("voVideoAdd"));
				if(voteImageInfo!=null){
					voteOptions.setVoImgAdd(voteImageInfo.getViAddress());
					voteOptions.setVoImgInfoId(voteImageInfo.getVoteImageId());
				}else{
					voteOptions.setVoImgAdd("");
					voteOptions.setVoImgInfoId(0);
				}
				optionList.add(voteOptions);
			}
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			e.printStackTrace();
			return optionList;
		} finally {
			ResourceMgr.closeQuietly(conn);
		} 
		return optionList;
	}
	
	/**
	 * 根据投票ID获取所有选项
	 * @param voteId
	 * @return
	 * @throws Exception
	 */
	public List<VoteOptions> getOptionsByVoteId(int voteId) throws Exception{
		
		List<VoteOptions> optionList = new ArrayList<VoteOptions>();
		VoteImageInfo voteImageInfo = new VoteImageInfo();
		DBSession conn = null;
		IResultSet rs = null;
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_VOTEOPTION, "xy");
		try {
			conn = E5docHelper.getDBSession(docLib.getDocLibID());
			//String selectSql = "select * from xy_membervoteoptions where voVoteID=? and SYS_DELETEFLAG=0 order by SYS_DOCUMENTID DESC";
			String selectSql = "select * from xy_membervoteoptions where voVoteID=? order by SYS_DOCUMENTID DESC";
			Object[] selParams = new Object[]{voteId};
			rs = conn.executeQuery(selectSql, selParams);
			
			while(rs.next()){
				VoteOptions voteOptions = new VoteOptions();
				voteOptions.setDeleteFlag(rs.getInt("SYS_DELETEFLAG"));
				voteOptions.setVoteOpId(rs.getInt("SYS_DOCUMENTID"));
				voteOptions.setVoteId(rs.getInt("voVoteID"));
				voteOptions.setVoName(rs.getString("voName"));
				voteOptions.setVoClassification(rs.getString("voClassification"));
				voteOptions.setVoType(rs.getInt("voType"));
				voteOptions.setVoIndex(rs.getInt("voIndex"));
				voteOptions.setVoVotes(rs.getInt("voVotes"));
				voteOptions.setVoThemeId(rs.getInt("voThemeID"));
				voteImageInfo = this.getImageInfoByOptionId(rs.getInt("SYS_DOCUMENTID"));
				if(voteImageInfo!=null){
					voteOptions.setVoImgAdd(voteImageInfo.getViAddress());
					voteOptions.setVoImgInfoId(voteImageInfo.getVoteImageId());
				}else{
					voteOptions.setVoImgAdd("");
					voteOptions.setVoImgInfoId(0);
				}
				optionList.add(voteOptions);
			}
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			e.printStackTrace();
			return optionList;
		} finally {
			ResourceMgr.closeQuietly(conn);
		} 
		return optionList;
	}
	
	/**
	 * 初始化投票主题以及其选项，返回的数据格式为 ：{主题字段……,{该主题下所有选项的集合}}
	 * @param voteId
	 * @return
	 * @throws Exception
	 */
	public List<VoteThemes> initThemeAndOptions(int voteId) throws Exception{
		
		List<VoteThemes> themeList = new ArrayList<VoteThemes>();
		DBSession conn = null;
		IResultSet rs = null;
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_VOTETHEME, "xy");
		try {
			conn = E5docHelper.getDBSession(docLib.getDocLibID());
			String selectSql = "select * from xy_membervotethemes where vtVoteID=? and SYS_DELETEFLAG=0 order by SYS_DOCUMENTID DESC"; 
			Object[] selParams = new Object[]{voteId};
			rs = conn.executeQuery(selectSql, selParams);
			
			while(rs.next()){
				VoteThemes voteTheme = new VoteThemes();
				voteTheme.setThemeId(rs.getInt("SYS_DOCUMENTID"));
				voteTheme.setThemeName(rs.getString("vtName"));
				voteTheme.setVoteId(rs.getInt("vtVoteID"));
				voteTheme.setThemeIndex(rs.getInt("vtIndex"));
				voteTheme.setOptionNums(rs.getInt("vtOptionNum"));
				voteTheme.setMostChooseNums(rs.getInt("vtMostChooseNum"));
				voteTheme.setMinChooseNums(rs.getInt("vtMinChooseNum"));
				// 根据查询的主题ID去选项表中获取该ID下的所有未删除的选项
				List<VoteOptions> optionList = this.getOptionsByThemeId(rs.getInt("SYS_DOCUMENTID"));
				if(optionList.size()>0){
					voteTheme.setVoteOptionList(optionList);
				}else{
					voteTheme.setVoteOptionList(null);
				}
				themeList.add(voteTheme);
			}
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			e.printStackTrace();
			return themeList;
		} finally {
			ResourceMgr.closeQuietly(conn);
		} 
		return themeList;
	}
	
	/**
	 * 根据前台上传的图片流获取图片信息
	 * @return
	 * @throws Exception
	 */
	public VoteImageInfo getImageInfoByFile(List<FileItem> items) throws Exception{
		
		VoteImageInfo voteImageInfo = new VoteImageInfo();
		try{
		if(items!=null && items.size()>0){
			for(FileItem item:items){
				String filedName = item.getFieldName();
				if("opid".equals(filedName)){
					voteImageInfo.setViOptionId(Integer.parseInt(item.getString()));
				}else if("voteId".equals(filedName)){
					voteImageInfo.setViVoteId(Integer.parseInt(item.getString()));
				}else if("height".equals(filedName)){
					voteImageInfo.setViHeight(Integer.parseInt(item.getString()));
				}else if("width".equals(filedName)){
					voteImageInfo.setViWidth(Integer.parseInt(item.getString()));
				}else if("name".equals(filedName)){
					voteImageInfo.setViName(item.getString());
				}else if("type".equals(filedName)){
					voteImageInfo.setViType(item.getString());
				}else if("size".equals(filedName)){
					voteImageInfo.setViSize(Integer.parseInt(item.getString()));
				}else if("viClassification".equals(filedName)){
					voteImageInfo.setViClassification(Integer.parseInt(item.getString()));
				}
			}
		}else{
			return null;
		}
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		
		return voteImageInfo;
	}
	
	/**
	 * 将图片写到磁盘上
	 * @param request
	 * @param realPath
	 * @return
	 * @throws Exception
	 */
	public boolean imageSaveDisk(String realPath,List<FileItem> items) throws Exception{	
		log.info("---开始向磁盘存图片，items是否为空----"+Boolean.toString(items!=null && items.size()>0));
		if(items!=null && items.size()>0){
			for(FileItem item:items){
				if(!item.isFormField()){
					InputStream in = item.getInputStream();
					log.info("---获取输入流的大小----"+String.valueOf(in.available()));
					log.info("---获取存储路径----"+realPath);
						if(in!=null){
							upload(realPath, in);
							in.close();
						}
						log.info("---存储完毕----");
				}
			}
		}else{
			return false;
		}
	return true;
	}
	
	/**
	 * 上传图片到磁盘，修改版
	 * @param items
	 * @param savePath
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public boolean upload(List items, String savePath) throws Exception {
		for (int i = 0; i < items.size(); i++)
		{
			FileItem file = (FileItem) items.get(i);
			if(file == null) break;
			String fileName = file.getName();
			if(fileName != null && fileName.length() > 0){
				if (!file.isFormField())
				{
					//当前输入流
					InputStream in = file.getInputStream();
					if(in != null){
						upload(savePath, in);
						in.close();
					}
				}
			}
		}
		return true;
	}
	
	/**
     * 正文图片上传后，为抽图服务做准备：在extracting目录下加空文件名。
     *
     * @param picPath xy/201507/20/.........jpg
     *                <p>
     *                改成extracting/xy~201507~20~77010550-ead6-4dda-a5e9-4aa54804b6a4.jpg的形式
     */
    public static void prepare4Extract(StorageDevice device, String picPath) {
    	picPath = "extracting/" + picPath.replaceAll("/", "~");

        StorageDeviceManager sdManager = SysFactory.getStorageDeviceManager();

        //写空文件
        InputStream in = new ByteArrayInputStream(new byte[0]);
        try {
            sdManager.write(device, picPath, in);
        } catch (Exception e) {
            System.out.println("加抽图任务异常：" + e.getLocalizedMessage() + picPath);
        } finally {
            ResourceMgr.closeQuietly(in);
        }
    }
	
	/**
	 * 根据上传路径上传附件
	 * @param savePath
	 * @param is
	 * @throws E5Exception
	 */
	public void upload(String savePath, InputStream is) throws E5Exception{
		if(is == null){
			return;
		}
		StorageDeviceManager storageDeviceManager = SysFactory.getStorageDeviceManager();
		storageDeviceManager.write(VoteManagerAmuc.ATTACH_NAME, savePath, is);//上传附件到指定位置
		log.info("---图片写到磁盘上成功---");
	}
	
	/**
	 * 给文件夹命名，命名规则：日期+vote+投票ID，如：xinhua_vote_1
	 * @return
	 * @throws Exception
	 */
	public String voteFolderSetName(int voteId) throws Exception{
		
//		Date date = new Date();
//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		String folderName =  "xinhua_vote_" + String.valueOf(voteId);
		
		return folderName;
	}
	
    /**
     * 投票选项图片命名，命名规则：option+选项id+日期时间+图片类型，如：option_1_20160101010101.jpg
     * 如果是页眉图片，命名规则：head+投票id+日期时间+图片类型，如：head_1_20160101010101.jpg
     * viClassification 0：投票描述,1：投票选项,2：页眉

     * @param voteId
     * @return
     * @throws Exception
     */
	public String voteImageSetName(VoteImageInfo voteImageInfo) throws Exception{
		
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HHmmss");
		int viClassification = voteImageInfo.getViClassification();
		String imgName = voteImageInfo.getViName();
		//获取图片后缀
		String ext = imgName.substring(imgName.lastIndexOf("."));
		StringBuilder sbBuilder  = new StringBuilder();
		if(viClassification==2){
			sbBuilder.append("head_").append(String.valueOf(voteImageInfo.getViVoteId())+"_");
		}else{
			sbBuilder.append("option_").append(String.valueOf(voteImageInfo.getViOptionId())+"_");
		}
		
		sbBuilder.append(dateFormat.format(date).replace(" ", "")).
		append(ext);
		
		return sbBuilder.toString();
	}
	
	/**
	 * 保存选项图片信息
	 * @param voteImageInfo
	 * @param imgName
	 * @param realPath
	 * @return
	 * @throws Exception
	 */
	public Document saveOptionImgInfo(VoteImageInfo voteImageInfo ,String imgName) throws Exception{
		
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_VOTEIMAGE, "xy");
		
		DocumentManager documentManager = DocumentManagerFactory.getInstance();
		Document docVop = documentManager.newDocument(docLib.getDocLibID(), InfoHelper.getID(docLib.getDocTypeID()));	
		docVop.setFolderID(docLib.getFolderID());
		docVop.setDeleteFlag(0);
		docVop.set("viVoteID", voteImageInfo.getViVoteId());
		docVop.set("viWidth", voteImageInfo.getViWidth());
		docVop.set("viHeight", voteImageInfo.getViHeight());
		docVop.set("viAddress", voteImageInfo.getViAddress());
		docVop.set("viSize", voteImageInfo.getViSize());
		docVop.set("viClassification", voteImageInfo.getViClassification());
		docVop.set("viName", imgName);
		docVop.set("viUploadTime", DateUtils.getTimestamp());
		docVop.set("viType", voteImageInfo.getViType());
		docVop.set("viOptionsID", voteImageInfo.getViOptionId());
		documentManager.save(docVop);
		
		return docVop;
	}
	/**
	 * 
	 * @param vsHeadersImgID 页眉图片ID
	 * @param docID 投票设置表ID
	 * @throws Exception
	 */
	public void setHeadersImgID(int vsHeadersImgID,int docID)throws Exception{
		DBSession dbSession = Context.getDBSession();;
		dbSession.beginTransaction();//开始事务
		String selRelSql="update xy_membervotesettings set vsHeaderImgID="+vsHeadersImgID+" where SYS_DOCUMENTID="+docID;		
		dbSession.executeDDL(selRelSql);
		dbSession.commitTransaction();
		ResourceMgr.closeQuietly(dbSession);
	}
	
	/**
	 * 获取图片路径
	 * @param voteID
	 * @param viClassification
	 * @return
	 * @throws Exception
	 */
	public List<VoteImageInfo> getPathByVoteID(int voteID,int viClassification)throws Exception{
		
		List<VoteImageInfo> pathList = new ArrayList<VoteImageInfo>();
		
		DBSession conn = Context.getDBSession();
		StringBuilder selRelSql = new StringBuilder();
		selRelSql.append("select * from xy_membervoteimage where viVoteID=").append(voteID)
		.append(" and viClassification=").append(viClassification);
		IResultSet rs = conn.executeQuery(selRelSql.toString());
		while(rs.next()){
			VoteImageInfo voteImageInfo = new VoteImageInfo();
			voteImageInfo.setVoteImageId(rs.getInt("SYS_DOCUMENTID"));
			voteImageInfo.setImgDeleteFlag(rs.getInt("SYS_DELETEFLAG"));
			voteImageInfo.setViVoteId(rs.getInt("viVoteID"));
			voteImageInfo.setViHeight(rs.getInt("viHeight"));
			voteImageInfo.setViWidth(rs.getInt("viWidth"));
			voteImageInfo.setViAddress(rs.getString("viAddress"));
			voteImageInfo.setViSize(rs.getInt("viSize"));
			voteImageInfo.setViClassification(rs.getInt("viClassification"));
			voteImageInfo.setViName(rs.getString("viName"));
			voteImageInfo.setViUploadTimeString(rs.getString("viUploadTime"));
			voteImageInfo.setViType(rs.getString("viType"));
			voteImageInfo.setViOptionId(rs.getInt("viOptionsID"));
			pathList.add(voteImageInfo);
		}
		ResourceMgr.closeQuietly(rs);
		ResourceMgr.closeQuietly(conn);

		
		return pathList;
	}
	
	/**
	 * 将图片从磁盘上删除
	 * @param imgID
	 * @return
	 */
	public boolean delImgToDisk(int imgID) throws Exception{
		// 根据图片ID 删除磁盘对应图片
		VoteImageInfo voteImageInfo = this.getImageInfoById(imgID);
		if(voteImageInfo !=null){
			StorageDeviceManager storageDeviceManager = SysFactory.getStorageDeviceManager();
			String realPath = storageDeviceManager.getByName(ATTACH_NAME).getNtfsDevicePath() +"/"+ voteImageInfo.getViAddress();
			String realFolder = "";
			realFolder = realPath.substring(0,realPath.lastIndexOf("/"));
	        try{
	        File files = new File(realFolder);
	         if(files.exists()){
	        	 if(files.isDirectory()){
	        		 String[] imgAll = files.list();
	        		 for(int i=0;i<imgAll.length;i++){
	        			 File file = new File(realPath);
	        			 if(file.isFile()){
	                         file.delete();
	        			 }
	        		 }}}
	        }catch(Exception e){
				e.printStackTrace();
				return false;
			 }
		}	
		return true;
	}
	
	/**
	 * 删除图片表中的一条记录
	 * @param imgID
	 * @throws Exception
	 */
	public void delImgToData(int imgID) throws Exception{
		DBSession dbSession = Context.getDBSession();;
		dbSession.beginTransaction();//开始事务
		String selRelSql="delete from xy_membervoteimage where SYS_DOCUMENTID="+imgID;		
		dbSession.executeDDL(selRelSql);
		dbSession.commitTransaction();
		ResourceMgr.closeQuietly(dbSession);
	}
	
	/**
	 * 删除图片表时，修改投票表中的页眉图片ID
	 * @param imgID
	 * @param voteID
	 * @throws Exception
	 */
	public void changeVoteSetByHeadImgID(int imgID,int voteID)throws Exception{
		DBSession dbSession = Context.getDBSession();
		dbSession.beginTransaction();//开始事务
		String selRelSql="update xy_membervotesettings set vsHeaderImgID=0 where SYS_DOCUMENTID="+voteID+" and vsHeaderImgID="+imgID;		
		dbSession.executeDDL(selRelSql);
		dbSession.commitTransaction();
		ResourceMgr.closeQuietly(dbSession);
	}
	
	/**
	 * 根据图片信息ID获取该条数据
	 * @param imageId
	 * @return
	 * @throws Exception
	 */
	public VoteImageInfo getImageInfoById(int imageId) throws Exception{
		DBSession conn = null;
		IResultSet rs = null;
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_VOTEIMAGE, "xy");
		VoteImageInfo voteImageInfo = new VoteImageInfo();
		try {
			conn = E5docHelper.getDBSession(docLib.getDocLibID());
			String selectSql = "select * from xy_membervoteimage where SYS_DOCUMENTID=? and SYS_DELETEFLAG=0"; 
			Object[] selParams = new Object[]{imageId};
			rs = conn.executeQuery(selectSql, selParams);
			
			while(rs.next()){
				voteImageInfo.setImgDeleteFlag(rs.getInt("SYS_DELETEFLAG"));
				voteImageInfo.setViAddress(rs.getString("viAddress"));
				voteImageInfo.setViVoteId(rs.getInt("viVoteID"));
				voteImageInfo.setViOptionId(rs.getInt("viOptionsID"));
				voteImageInfo.setVoteImageId(rs.getInt("SYS_DOCUMENTID"));
			}
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			e.printStackTrace();
			return null;
		} finally {
			ResourceMgr.closeQuietly(conn);
		} 
		return voteImageInfo;
	}
	
	/**
	 * 根据选项ID更新选项所属分类
	 * @param opId
	 * @param className
	 * @return
	 * @throws Exception
	 */
	public VoteOptions updateOptionClass(int opId ,String className) throws Exception{
		DBSession conn = null;
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_VOTEOPTION, "xy");
		VoteOptions voteOptions = new VoteOptions();
		try {
			conn = E5docHelper.getDBSession(docLib.getDocLibID());
			conn.beginTransaction(); // 开始事务
			
			String exeSqlStr = "update xy_membervoteoptions set voClassification=?,voLastModified=? where SYS_DOCUMENTID=?";
			Object[] params = new Object[]{className,DateUtils.getTimestamp(),opId};
		
			conn.executeUpdate(exeSqlStr, params);
			conn.commitTransaction();
			voteOptions = this.getOptionById(opId);
			
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			e.printStackTrace();
			return null;
		} finally {
			ResourceMgr.closeQuietly(conn);
		} 
		return voteOptions;
	}
	
	/**
	 * 根据选项ID获取图片表中相应数据
	 * @param optionId
	 * @return
	 * @throws Exception
	 */
	public VoteImageInfo getImageInfoByOptionId(int optionId) throws Exception{
		DBSession conn = null;
		IResultSet rs = null;
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_VOTEIMAGE, "xy");
		VoteImageInfo voteImageInfo = new VoteImageInfo();
		try {
			conn = E5docHelper.getDBSession(docLib.getDocLibID());
			String selectSql = "select * from xy_membervoteimage where viOptionsID=? and viClassification=1 and SYS_DELETEFLAG=0"; 
			Object[] selParams = new Object[]{optionId};
			rs = conn.executeQuery(selectSql, selParams);
			
			while(rs.next()){
				voteImageInfo.setImgDeleteFlag(rs.getInt("SYS_DELETEFLAG"));
				voteImageInfo.setViAddress(rs.getString("viAddress"));
				voteImageInfo.setViVoteId(rs.getInt("viVoteID"));
				voteImageInfo.setViOptionId(rs.getInt("viOptionsID"));
				voteImageInfo.setVoteImageId(rs.getInt("SYS_DOCUMENTID"));
			}
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			e.printStackTrace();
			return null;
		} finally {
			ResourceMgr.closeQuietly(conn);
		} 
		return voteImageInfo;
	}
	
	/**
	 * 新增一条主题
	 * @param voteThemes
	 * @return
	 * @throws Exception
	 */
	public Document saveVoteTheme(VoteThemes voteThemes) throws Exception{
		
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_VOTETHEME, "xy");
		
		DocumentManager documentManager = DocumentManagerFactory.getInstance();
		Document docVop = documentManager.newDocument(docLib.getDocLibID(), InfoHelper.getID(docLib.getDocTypeID()));	
		docVop.setFolderID(docLib.getFolderID());
		docVop.setDeleteFlag(0);
		docVop.set("vtName", voteThemes.getThemeName());
		docVop.set("vtVoteID", voteThemes.getVoteId());
		docVop.set("vtCreated", DateFormatAmend.timeStampDispose(voteThemes.getCreatedTime()));
		docVop.set("vtLastModified", DateFormatAmend.timeStampDispose(voteThemes.getLastModifiedTime()));
		docVop.set("vtOptionNum", 0);
		if(voteThemes.getThemeIndex()==0){
			docVop.set("vtIndex", this.getThemeIndexByVoteId(voteThemes.getVoteId()));
		}else{
			docVop.set("vtIndex", voteThemes.getThemeIndex());
		}
		docVop.set("vtMostChooseNum", voteThemes.getMostChooseNums());
		docVop.set("vtMinChooseNum", voteThemes.getMinChooseNums());
		documentManager.save(docVop);
		
		return docVop;
	}
	
	/**
	 * 根据投票ID获取该投票下的主题个数
	 * @param voteId
	 * @return
	 * @throws Exception
	 */
	public int getThemeIndexByVoteId(int voteId) throws Exception{
		DBSession conn = null;
		IResultSet rs = null;
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_VOTETHEME, "xy");
		int themeIndex = 0;
		try {
			conn = E5docHelper.getDBSession(docLib.getDocLibID());
			String selectSql = "select count(*) from xy_membervotethemes where vtVoteID=? and SYS_DELETEFLAG=0"; 
			Object[] selParams = new Object[]{voteId};
			rs = conn.executeQuery(selectSql, selParams);
			
			if(rs.next()){
				themeIndex = rs.getInt(1);
			}
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			e.printStackTrace();
			return 1;
		} finally {
			ResourceMgr.closeQuietly(conn);
		} 
		return themeIndex+1;
	}
	
	/**
	 * 修改一条投票主题数据
	 * @param themeId
	 * @param themeContent
	 * @param themeIndex
	 * @return
	 * @throws Exception
	 */
	public VoteThemes updateTheme(int themeId ,String themeContent ,int themeIndex,int chooseNums,int chooseNums_min) throws Exception{
		DBSession conn = null;
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_VOTETHEME, "xy");
		VoteThemes voteThemes = new VoteThemes();
		try {
			conn = E5docHelper.getDBSession(docLib.getDocLibID());
			conn.beginTransaction(); // 开始事务
			
			String exeSqlStr = "update xy_membervotethemes set vtName=?,vtIndex=?,vtLastModified=?,vtMostChooseNum=?,vtMinChooseNum=? where SYS_DOCUMENTID=?";
			Object[] params = new Object[]{themeContent,themeIndex,DateUtils.getTimestamp(),chooseNums,chooseNums_min,themeId};
		
			conn.executeUpdate(exeSqlStr, params);
			conn.commitTransaction();
			voteThemes = this.getThemeById(themeId);
			
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			e.printStackTrace();
			return null;
		} finally {
			ResourceMgr.closeQuietly(conn);
		} 
		return voteThemes;
	}
	
	/**
	 * 根据ID获取一条主题
	 * @param themeId
	 * @return
	 * @throws Exception
	 */
	public VoteThemes getThemeById(int themeId) throws Exception{
		DBSession conn = null;
		IResultSet rs = null;
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_VOTETHEME, "xy");
		VoteThemes voteThemes = new VoteThemes();
		try {
			conn = E5docHelper.getDBSession(docLib.getDocLibID());
			String selectSql = "select * from xy_membervotethemes where SYS_DOCUMENTID=? and SYS_DELETEFLAG=0"; 
			Object[] selParams = new Object[]{themeId};
			rs = conn.executeQuery(selectSql, selParams);
			
			while(rs.next()){
				voteThemes.setThemeId(rs.getInt("SYS_DOCUMENTID"));
				voteThemes.setThemeIndex(rs.getInt("vtIndex"));
				voteThemes.setThemeName(rs.getString("vtName"));
				voteThemes.setVoteId(rs.getInt("vtVoteID"));
				voteThemes.setMostChooseNums(rs.getInt("vtMostChooseNum"));
				voteThemes.setMinChooseNums(rs.getInt("vtMinChooseNum"));
			}
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			e.printStackTrace();
			return null;
		} finally {
			ResourceMgr.closeQuietly(conn);
		} 
		return voteThemes;
	}
	
	/**
	 * 删除一个主题
	 * @param opId
	 * @return
	 * @throws Exception
	 */
	// 删除一条主题，逻辑上删除，SYS_DELETEFLAG=1
	public boolean deleteVoteTheme(int themeId) throws Exception{
		DBSession conn = null;
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_VOTETHEME, "xy");
		try {
			conn = E5docHelper.getDBSession(docLib.getDocLibID());
			conn.beginTransaction(); // 开始事务
			
			String exeSqlStr = "update xy_membervotethemes set SYS_DELETEFLAG=1,vtLastModified=? where SYS_DOCUMENTID=?";
			Object[] params = new Object[]{DateUtils.getTimestamp(),themeId};
		
			conn.executeUpdate(exeSqlStr, params);
			conn.commitTransaction();
			
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			e.printStackTrace();
			return false;
		} finally {
			ResourceMgr.closeQuietly(conn);
		} 
		return true;
	}
	
	/**
	 * 根据主题ID更改选项个数，删除-1，添加+1
	 * @param themeId
	 * @param flagstr
	 * @throws Exception
	 */
	public void updateOptionNum(int themeId ,String flagstr) throws Exception{
		DBSession conn = null;
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_VOTETHEME, "xy");
		String operationStr = "";
		try {
			conn = E5docHelper.getDBSession(docLib.getDocLibID());
			conn.beginTransaction(); // 开始事务
			
			if("add".equals(flagstr)){
				operationStr = "vtOptionNum+1";
			}else{
				operationStr = "vtOptionNum-1";
			}
			String exeSqlStr = "update xy_membervotethemes set vtOptionNum="+operationStr+",vtLastModified=? where SYS_DOCUMENTID=?";
			Object[] params = new Object[]{DateUtils.getTimestamp(),themeId};
		
			conn.executeUpdate(exeSqlStr, params);
			conn.commitTransaction();
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			e.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(conn);
		} 
	}
	
	/**
	 * 添加或者修改选项查看页
	 * @param opId
	 * @param viewPageText
	 * @param showImgFlag
	 * @return
	 * @throws Exception
	 */
	public boolean updateViewPageTextById(int opId ,String viewPageText,int showImgFlag) throws Exception{
		DBSession conn = null;
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_VOTEOPTION, "xy");
		try {
			conn = E5docHelper.getDBSession(docLib.getDocLibID());
			conn.beginTransaction(); // 开始事务
			
			String exeSqlStr = "update xy_membervoteoptions set voViewPageContent=?,voShowOpImgOnpage=?,voLastModified=? where SYS_DOCUMENTID=?";
			Object[] params = new Object[]{viewPageText,showImgFlag,DateUtils.getTimestamp(),opId};
		
			conn.executeUpdate(exeSqlStr, params);
			conn.commitTransaction();
			
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			e.printStackTrace();
			return false;
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
		return true;
	}

	public boolean removeVoteOption(int opId) throws E5Exception {
		DBSession conn = null;
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_VOTEOPTION, "xy");
		try {
			conn = E5docHelper.getDBSession(docLib.getDocLibID());
			conn.beginTransaction(); // 开始事务
			String exeSqlStr = "delete from xy_membervoteoptions where SYS_DOCUMENTID=?";
			Object[] params = new Object[]{opId};
			conn.executeUpdate(exeSqlStr, params);
			conn.commitTransaction();
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			e.printStackTrace();
			return false;
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
		return true;
	}

}
