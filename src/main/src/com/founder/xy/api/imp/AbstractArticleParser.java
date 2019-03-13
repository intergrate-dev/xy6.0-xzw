package com.founder.xy.api.imp;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import com.founder.e5.doc.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.founder.xy.commons.FilePathUtil;

public abstract class AbstractArticleParser{
	
	public static final SimpleDateFormat format = new SimpleDateFormat("yyyyMM/dd");
	
	protected final static String SmallTitlePic = "smalltitlepic";
	
	protected final static String MiddleTitlePic = "middletitlepic";
	
	protected final static String BigTitlePic = "bigtitlepic";
	
	public static byte[] JPG = {0x4a, 0x46, 0x49, 0x46};
	
	public static byte[] GIF = {0x47, 0x49, 0x46};
	
	public static byte[] PNG = {(byte)0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a};
	
	private String storeBasePath;//翔宇图片存储
	
	private String articleTime;//翔宇稿件顺序起点日期
	
	enum AttType{
		AttArticle,  //文章
		AttVideo,    //视频
		AttPic       //组图
	}
	
	class FileNamePair{
		
		public String abstractPath;
		
		public String recordPath;

		public String storePath;
		
		FileNamePair(String abstractPath, String recordPath, String storePath){
			this.abstractPath = abstractPath;
			this.recordPath = recordPath;
			this.storePath = storePath;
		}

		public String toString() {
			
			StringBuffer buf = new StringBuffer();
			buf.append("{abstractPath: " + abstractPath + ", ");
			buf.append("recordPath: " + recordPath + ", ");
			buf.append("storePath: " + storePath + "}");
			return buf.toString();
			
		}
		
	}
	
	protected FileNamePair generateFilePath(String type){
		
		return generateFilePath(type, false);
		
	}
	
	protected FileNamePair generateFilePath(String type, boolean isAttr){
		
		String recordPath = "xy";
		recordPath = FilePathUtil.normalPath(recordPath, format.format(new Date()));
		String uuid = UUID.randomUUID().toString();
		recordPath = FilePathUtil.normalPath(recordPath, uuid + "." + type);
		String absPath = FilePathUtil.normalPath(storeBasePath, recordPath);
		FileNamePair fileNamePair = new FileNamePair(absPath, "图片存储;" + recordPath,"/" + recordPath);
		if(!isAttr){
			return fileNamePair;
		}
		fileNamePair.recordPath = "../../xy/image.do?path=" + fileNamePair.recordPath;
		return fileNamePair;
		
	}
	
	protected String replaceAttPath(String content, AttType attType, String filePath, String recordPath){

		org.jsoup.nodes.Document html = Jsoup.parse(content);
		Elements list = html.select("img");
		if (list.isEmpty()) return content;
		for (Element img : list) {
			if(img.attr("src").contains("image.do?path=")) continue;
			img.replaceWith(Jsoup.parse("<img src=\"" + recordPath + "\">").select("img").get(0));
			break;
		}
		return html.body().html();
	}
	
	public String getImageType(byte[] data){
		if(data != null && data.length > PNG.length){
			boolean result = true;
			for(int t=0; t<PNG.length; ++t){
				if(data[t]!=PNG[t]){
					result = false;
					break;
				}
			}
			if(result){
				return "png";
			}
		}
		if(data != null && data.length > 10){
			int offset = 6;
			boolean result = true;
			for(int t=0; t<JPG.length; ++t){
				if(data[t+offset]!=JPG[t]){
					result = false;
					break;
				}
			}
			if(result){
				return "jpg";
			}
		}
		if(data != null && data.length > 3){
			boolean result = true;
			for(int t=0; t<GIF.length; ++t){
				if(data[t]!=GIF[t]){
					result = false;
					break;
				}
			}
			if(result){
				return "gif";
			}
		}
		return "jpg";
		
	}
	
	protected String getImageType(String fileName){
		
		int index = fileName.lastIndexOf(".");
		if(index > 0){
			return fileName.substring(index + 1,index + 4);
		}else{
			return "jpg";
		}
		
	}
	
	/**
	 * 计算稿件排序字段值
	 */
//	public double getNewOrder(long articleID, String artPubTime) {
	public double getNewOrder(Document article) {
		Timestamp pubTime = article.getTimestamp("a_pubTime");
//		Timestamp pubTime = Timestamp.valueOf(artPubTime);
		if (pubTime == null) return 0;
		
		Calendar ca = Calendar.getInstance();
		ca.setTime(pubTime);

		double order = createDisplayOrder(ca, 0, 0, article.getDocID());
//		double order = createDisplayOrder(ca, 0, 0, articleID);
		return order;
	}
	
	public double createDisplayOrder(Calendar cd, int daycnt, int ord, long id) {
		if (cd == null)
		    cd = Calendar.getInstance();

        int nHour = cd.get(Calendar.HOUR_OF_DAY);
        int nMinute = cd.get(Calendar.MINUTE);

		cd.set(Calendar.HOUR_OF_DAY, 0);
		cd.set(Calendar.MINUTE, 0);
		cd.set(Calendar.SECOND, 0);
		cd.set(Calendar.MILLISECOND, 0);
		
		Calendar dd = Calendar.getInstance();
		
		if(articleTime.trim().equals("")||articleTime==null){
			dd.set(2015, 4, 13, 0, 0, 0); //新起点~
			//dd.set(2000, 9, 12, 0, 0, 0);		
			//dd.set(2000, 2, 12, 0, 0, 0);
		}else{
			String[] atime = articleTime.trim().split("-");
			dd.set(Integer.parseInt(atime[0]), Integer.parseInt(atime[1]), Integer.parseInt(atime[2]), 0, 0, 0);
		}
				
        dd.set(java.util.Calendar.HOUR_OF_DAY, 0);
        dd.set(java.util.Calendar.MINUTE, 0);
        dd.set(java.util.Calendar.SECOND, 0);
        dd.set(java.util.Calendar.MILLISECOND, 0);

		long tt = cd.getTimeInMillis() - dd.getTimeInMillis();
		double ret = 0;
		
	/*	
		87654321.12345678：
		[------][0][00][0.0][000000] 
		万位以上是天数，千位的是优先级，百位和十位是小时数，个位和小数点后第一位是分钟，小数点后第二位开始连续6位是ID
		 long days = (tt / 1000 * 60 * 60 * 24); //毫秒数转成天数
		 days += daycnt;		//天数再加上置顶天数
		 ret = days * 10000 + priority * 1000 + hour * 10 + minutes * 0.1 + ID后六位放在小数点后第二位开始 
	 */		
		ret = (long) ( (double) (tt) / 8640.0)
				+ daycnt * 10000
				+ (double)ord * 1000 
				+ (double)nHour*10.0 + (double)nMinute*0.1
				+ (double)(id % 1000000)*0.0000001;
		return ret * -1;
	}
	
	/**
	 * 生成抽图文件信息
	 */
	public void extractingImg(String attrPath){
		int index = attrPath.indexOf(";");
		if(index > -1){
			String path = attrPath.substring(index + 1);
			path = path.replace("/", "~");
			File infoDir = new File(FilePathUtil.normalPath(storeBasePath,"extracting"));
			if(!infoDir.exists()){
				infoDir.mkdirs();
			}
			File infoFile = new File(infoDir, path);
			if(!infoFile.exists()){
				try {
					infoFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	public String getStoreBasePath() {
		return storeBasePath;
	}

	public void setStoreBasePath(String storeBasePath) {
		this.storeBasePath = storeBasePath;
	}

	public String getArticleTime() {
		return articleTime;
	}

	public void setArticleTime(String articleTime) {
		this.articleTime = articleTime;
	}
	
}
