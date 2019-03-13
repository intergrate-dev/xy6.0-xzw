package com.founder.amuc.commons;
/**
 * @author leijj
 * @date 2014-8-29
 * Description: 附件存储设置
 */
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFHeader;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.founder.amuc.commons.attachment.Attach;
import com.founder.amuc.commons.attachment.AttachManager;
import com.founder.amuc.commons.attachment.AttachManagerFactory;
import com.founder.e5.commons.FileUtils;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.db.LaterDataTransferException;
import com.founder.e5.sys.StorageDeviceManager;
import com.founder.e5.sys.SysFactory;
import com.founder.e5.workspace.ProcHelper;
@SuppressWarnings("rawtypes")
public class AttachHelper {
	public static final String ATTACH_NAME = "附件存储";
	
	/**
	 * 上传附件到存储设备
	 * @param request
	 * @param savePath
	 * @return
	 * @throws Exception
	 */
	public static List<String> upload(List items, String savePath) throws Exception {
		List<String> resultList = new ArrayList<String>();
		for (int i = 0; i < items.size(); i++)
		{
			FileItem file = (FileItem) items.get(i);
			if(file == null) break;
			String fileName = file.getName();
			if(fileName != null && fileName.length() > 0){
				String fn = fileName.substring(fileName.lastIndexOf(File.separator) + 1);
				String realFile = "";
				if(fn != null && fn.length() > 0){
					realFile = getTimestamp() + "_" + fn;
				}
				
				if (!file.isFormField())
				{
					//当前输入流
					InputStream in = file.getInputStream();
					if(in != null){
						String resultPath = savePath + File.separator + realFile;
						resultList.add(resultPath);
						upload(resultPath, in);
						in.close();
					}
				}
			}
			
		}
		return resultList;
	}
	
	/**
	 * 上传附件到存储设备同时保存记录到附件表。
	 * @param request
	 * @param savePath
	 * @param addAtta 是否保存到附件表
	 * @param docLibID
	 * @param docID
	 * @param topic 给活动表用 1=活动策划案，2=审批附件
	 * @return
	 * @throws Exception
	 */
	public static boolean upload(List items, int userID, String savePath, 
			boolean addAtta, int docLibID, Long docID, String topic) throws Exception {
		if(items == null || items.size() <= 0) return false;
		
		boolean flag = false;
		for (int i = 0; i < items.size(); i++)
		{
			FileItem file = (FileItem) items.get(i);
			if(file == null) break;
			
			String fileName = file.getName();
			if(fileName != null && fileName.length() > 0){
				String fn = fileName.substring(fileName.lastIndexOf(File.separator) + 1);
				String realFile = "";
				if(fn != null && fn.length() > 0){
					realFile = docID + "_" + getTimestamp() + "_" + fn;
				}
				file = (FileItem) items.get(i);
				if (!file.isFormField())
				{
					//当前输入流
					InputStream in = file.getInputStream();
					if(in != null){
						upload(savePath + File.separator + realFile, in);
						if(addAtta) {
							addAttach(savePath + File.separator + realFile, docLibID, docID, userID, file, topic);
						}
						in.close();
						flag = true;
					} else{
						flag = false;
					}
				}
			}
		}
		return flag;
	}
	
	/**
	 * 根据上传路径上传附件
	 * @param savePath
	 * @param is
	 * @throws E5Exception
	 */
	public static void upload(String savePath, InputStream is) throws E5Exception{
		if(is == null){
			return;
		}
		StorageDeviceManager storageDeviceManager = SysFactory.getStorageDeviceManager();
		storageDeviceManager.write(AttachHelper.ATTACH_NAME, savePath, is);//上传附件到指定位置
	}
	
	/**
	 * 往附件表中加入数据
	 * @param addAtta
	 * @param savePath
	 * @param docLibId
	 * @param docId
	 * @param userID
	 * @param file
	 * @param topic
	 * @throws E5Exception
	 */
	public static void addAttach(String savePath, int docLibID, Long docID, 
			int userID, FileItem file, String topic) throws E5Exception {
		String fileExt = "";
		if(savePath != null && savePath.indexOf(".") > 0)
			fileExt = savePath.substring(savePath.lastIndexOf("."));
		Attach attach = new Attach();
        attach.setAttContent(file.getFieldName());								//文件原名
        attach.setAttTopic(topic);//这里为一个文档有多个类型附件时区分使用。
        attach.setDocID(docID);
        attach.setDocLibID(docLibID);
        attach.setAttSize(file.getSize());
        attach.setAttPath(savePath);								//保存路径 和文件名（唯一文件名）
        attach.setAttFormat(fileExt);   							//文件扩展名
        attach.setUser(userID);				//设置用户
        AttachManager dam =  AttachManagerFactory.getInstance();
        dam.addAtta(attach);
	}
	
	/** 
	* @author  leijj 
	* 功能： 附件存储
	* @param savePath
	* @param docLibID
	* @param docID
	* @param userID
	* @param file
	* @throws E5Exception 
	*/ 
	public static void addAttach(String savePath, int docLibID, Long docID, 
			int userID, File file, String topic) throws E5Exception {
		String fileExt = "";
		if(savePath != null && savePath.indexOf(".") > 0)
			fileExt = savePath.substring(savePath.indexOf("."));
		Attach attach = new Attach();
        attach.setAttContent(file.getName());//文件原名
        attach.setDocID(docID);
        attach.setDocLibID(docLibID);
        attach.setAttSize(file.length());
        attach.setAttTopic(topic);//这里为一个文档有多个类型附件时区分使用。
        attach.setAttPath(savePath);//保存路径 和文件名（唯一文件名）
        attach.setAttFormat(fileExt); //文件扩展名
        attach.setUser(userID);//设置用户
        AttachManager dam =  AttachManagerFactory.getInstance();
        dam.addAtta(attach);
	}
	/**
	 * 获取年月日时分秒时间戳
	 * @return
	 */
	public static String getTimestamp(){
		Date dNow = new Date();
		//保存目录名称
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		//保存文件名称后缀
		SimpleDateFormat format2 = new SimpleDateFormat("HHmmss");
		
		StringBuilder sb = new StringBuilder();
		//当前日期
		sb.append(format.format(dNow));
		//当前时分秒
		sb.append(format2.format(dNow));
		return sb.toString();
	}
	
	public static void delUpload(Long docID,int docLibID){
		
		String sql="delete from DOM_ATTACHMENT where docid=? and doclibid=?";
		Object[] param=new Object[]{docID,docLibID};
		DBSession dbSession = null;
		IResultSet rs = null;
		try {
			dbSession = Context.getDBSession();
			dbSession.beginTransaction();
			dbSession.executeUpdate(sql, param);
			dbSession.commitTransaction();
		} catch (E5Exception e) {
			e.printStackTrace();
		} catch (LaterDataTransferException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(dbSession);
		}
	}
	
	//获取附件地址
	public static List<String> getAttachPath(int docLibID, Long docID) {
		List<String> attPaths = new ArrayList<String>();
		String sql = "SELECT ATT_ATTPATH FROM DOM_ATTACHMENT WHERE DOCID="
				+ docID + " AND DOCLIBID=" + docLibID;
		DBSession dbSession = null;
		IResultSet rs = null;
		try {
			dbSession = Context.getDBSession();
			rs = dbSession.executeQuery(sql);
			while (rs.next()) {
				attPaths.add(rs.getString(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(dbSession);
		}
		return attPaths;
	}
	
	//保存文件
	public static File writeFile(InputStream in, String name) throws IOException {
		File newFile = new File(name);
		try {
	    	if(!newFile.getParentFile().exists())
	    	{
	    		newFile.getParentFile().mkdirs();
	    	}
			FileUtils.writeFile(in, newFile);
		} finally {
			ResourceMgr.closeQuietly(in);
		}
		return newFile;
	}
	
	public static float getSizeKB(double size) {
		double kiloByte = size/1024;  
        BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));  
        return result1.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();  
	}
	public static String getFormatSize(double size) {  
        double kiloByte = size/1024;  
        if(kiloByte < 1) {  
            return size + "Byte(s)";  
        }  
          
        double megaByte = kiloByte/1024;  
        if(megaByte < 1) {  
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));  
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";  
        }  
          
        double gigaByte = megaByte/1024;  
        if(gigaByte < 1) {  
            BigDecimal result2  = new BigDecimal(Double.toString(megaByte));  
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";  
        }  
          
        double teraBytes = gigaByte/1024;  
        if(teraBytes < 1) {  
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));  
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";  
        }  
        BigDecimal result4 = new BigDecimal(teraBytes);  
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";  
    }
	
	public static void upload(MultipartHttpServletRequest request, 
			String savePath, int docLibId, Long docId)
			throws Exception {
		String filePrefix = (ProcHelper.getUserCode(request) == null ? "" : ProcHelper.getUserCode(request).concat("_"));//文件前缀
		// 获得文件：     
        MultipartFile file = request.getFile("attach");
        String fileName=file.getOriginalFilename();
        String rltPath = "";
        String fileExt = "";
        String path = request.getSession().getServletContext().getRealPath(savePath); 
        path += "/"+DateUtil.YearNum()+"/"+DateUtil.MonthNum()+"/"+DateUtil.DayNum();
        Long size=file.getSize();
        byte[] b=file.getBytes();
    	Date dNow = new Date();
		SimpleDateFormat format3 = new SimpleDateFormat("HHmmssss");// 文件名称
		StringBuilder sb = new StringBuilder();
		sb.append(path);		//项目根路径
		sb.append("\\" + filePrefix);// 当前用户
		String fmt3=format3.format(dNow);
		sb.append(fmt3);
		// 当前文件扩展名
		if (fileName.lastIndexOf(".") >= 0) {
			fileExt = fileName.substring(fileName.lastIndexOf("."));
		}
		sb.append(fileExt);
		rltPath = sb.toString();
		File uploadedFile = new File(rltPath);
		File parent=uploadedFile.getParentFile();
		if(parent!=null&&!parent.exists()){ 
			parent.mkdirs(); 
		} 
	    FileCopyUtils.copy(b, uploadedFile);
	    String path2=savePath+"/"+DateUtil.YearNum()+"/"+DateUtil.MonthNum()+"/"+DateUtil.DayNum()+"/"+filePrefix+fmt3+fileExt;
        Attach attach=new Attach();
        attach.setAttContent(fileName);								//文件原名
        attach.setDocID(docId);
        attach.setDocLibID(docLibId);
        attach.setAttSize(size);
        attach.setAttPath(path2);								//保存路径 和文件名（唯一文件名）
        attach.setAttFormat(fileExt);   							//文件扩展名
        attach.setUser(ProcHelper.getUserID(request));				//设置用户
        AttachManager dam=  AttachManagerFactory.getInstance();
        dam.addAtta(attach);
	}
	/**
	 * 将信息导出为excel表
	 * @param response
	 * @param listTitle 表头 ，ArrayList<String>，类似 “[会员ID, 会员名称]”
	 * @param listContent  表内容，List<ArrayList<String>>，类似“[[150, 13717874086], [23, ], [54, 666 ]]”
	 * @param sheetName 导出表的名称
	 * @throws Exception
	 */
	//
	@SuppressWarnings("unused")
	public static void setExcelMsg(HttpServletResponse response,ArrayList<String> listTitle,List<ArrayList<String>> listContent,String sheetName) throws Exception{
		// 创建一个新的Excel
		HSSFWorkbook workBook = new HSSFWorkbook();
		// 创建sheet页
		HSSFSheet sheet = workBook.createSheet();
		// sheet页名称
		workBook.setSheetName(0, "result");
		// 创建header页
		HSSFHeader header = sheet.getHeader();
		// 设置标题居中
		/*header.setCenter("标题");*/
		//单元格样式
		HSSFCellStyle cellStyle = workBook.createCellStyle();
		cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框    
		cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框    
		cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框    
		cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框   
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中    
		//设置字体
		HSSFFont font = workBook.createFont();
		font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
		cellStyle.setFont(font);
		// 设置第一行为Header
		HSSFRow row = sheet.createRow(0);   //excel表第一行
		row.setHeight((short) 400);
		int listNum = listTitle.size();
		for(int i=0;i<listNum;i++){
			HSSFCell cell = row.createCell(i);  //创建单元格
			sheet.setColumnWidth(i, 4000);  //设置每列宽度
			cell.setCellValue(listTitle.get(i));   //设置单元格内容
			cell.setCellStyle(cellStyle);  //设置每个单元格样式
		}
		//设置其他行的内容
		if(listContent!=null){
			int listContentNum = listContent.size();
			for(int m=0;m<listContentNum;m++){
				row = sheet.createRow(m+1);  //创建行
				row.setHeight((short) 400);
				ArrayList<String> listcont = listContent.get(m);
				int listcontNum = listcont.size();
				for(int n=0;n<listcontNum;n++){
					HSSFCell cell = row.createCell(n);  //创建单元格
					cell.setCellValue(listcont.get(n));  //设置单元格内容
					cell.setCellStyle(cellStyle);  //设置每个单元格样式
				}
			}
		}
		response.reset();
		response.setContentType("application/msexcel;charset=UTF-8");
		response.addHeader(
				"Content-Disposition",
				"attachment;filename=\""
						+ new String((sheetName + ".xls")
								.getBytes("GBK"), "ISO8859_1")
						+ "\"");
		OutputStream out = response.getOutputStream();
		if (out != null) {
			workBook.write(out);
			out.flush();
			out.close();
		}
	}
}