package com.founder.amuc.commons.attachment;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 附件处理工具类
 * 
 */
public class AttachUtil {
	/**
	 * 取得附件的路径
	 * 
	 * @param attach
	 * @return 附件的路径
	 */
	static public String getAttachPath()
	{
		java.sql.Date currentDate = new java.sql.Date( System.currentTimeMillis() );
		SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd" );
		return format.format( currentDate );
	}

	/**
	 * 取得附件的文件名称
	 * 
	 * @param attach
	 * @return
	 */
	static public String getAttachName( Attach attach )
	{  
		//return attach.getAttContent();
		//return "att_" + attach.getGuid() + "." + "pdf";
		String path = attach.getAttPath();
		if(path==null || "".equals(path)) return "unknown";
		return path.substring(path.lastIndexOf("/")+1,path.length());
	}

	/**
	 * 取得附件的URL
	 * 
	 * @param id
	 * @return
	 */
	static public String getAttachUrl( long id )
	{
		return "XinhuaMMS://" + id + ",0,0,5";
	}

	/* 匹配URI */
	public static final Pattern uriPattern = Pattern.compile(
			"XinhuaMMS://([\\d]+?),0,0,5",
			Pattern.CASE_INSENSITIVE );

	public static List<String> MAIN_TYPES = new ArrayList<String>();
	static
	{
		MAIN_TYPES.add( "pic" );
		MAIN_TYPES.add( "word" );
		MAIN_TYPES.add( "pdf" );
		MAIN_TYPES.add( "excel" );
		MAIN_TYPES.add( "graphic" );
	}

	/**
	 * 媒体类型转换对照,可能会持续增加，所以设置为public，可临时从外部增加
	 */
	public static Map<String, String> MEDIA_TYPES = new HashMap<String, String>();
	static
	{
		MEDIA_TYPES.put( "jpg", "pic" );
		MEDIA_TYPES.put( "jpeg", "pic" );
		MEDIA_TYPES.put( "bmp", "pic" );
		MEDIA_TYPES.put( "png", "pic" );
		MEDIA_TYPES.put( "tif", "pic" );
		MEDIA_TYPES.put( "tiff", "pic" );
		MEDIA_TYPES.put( "psd", "pic" );
		MEDIA_TYPES.put( "gif", "pic" );
		MEDIA_TYPES.put( "pic", "pic" );
		MEDIA_TYPES.put( "doc", "word" );
		MEDIA_TYPES.put( "rtf", "word" );
		MEDIA_TYPES.put( "txt", "word" );
		MEDIA_TYPES.put( "pdf", "pdf" );
		MEDIA_TYPES.put( "xls", "excel" );
		MEDIA_TYPES.put( "cdr", "graphic" );
	}

	/**
	 * 根据编辑器传递的附件类型，转换程e5这边的附件类型 编辑器与E5附件类型转换关系： "audio" (音频) -> Audio
	 * "vedio"(视频) -> Video "word"(word文档) -> Complexdata "pic"(图片) -> Photo
	 * "flash" (FLASH) -> Complexdata "excel"(Excel图表)-> Complexdata
	 * "pdf"(PDF文档) -> Complexdata
	 * 
	 * @param editorType - 编辑器的附件类型
	 * @return e5的附件类型
	 */
	static public String getMainType( String editorType )
	{
		// 使用编辑器的类型
		if ( editorType == null || editorType.trim().equals( "" ) )
			return "unknown";
		else if ( MAIN_TYPES.contains( editorType.toLowerCase() ) )
		{
			return editorType.toLowerCase();
		}
		if ( MEDIA_TYPES.get( editorType.toLowerCase() ) != null )
		{// 可识别类型
			return ( String ) MEDIA_TYPES.get( editorType.toLowerCase() );
		}
		else
		{
			try
			{// 如果是完整文件名,则拆出后缀进行解析
				editorType = editorType.substring(
						editorType.lastIndexOf( "." ) + 1,
						editorType.length() );
				if ( MEDIA_TYPES.get( editorType.toLowerCase() ) != null )
					return ( String ) MEDIA_TYPES.get( editorType.toLowerCase() );
				else
					return "unknown";
			}
			catch ( Exception ex )
			{
				return "unknown";
			}
		}
	}

	/**
	 * 取得文件扩展名称
	 * 
	 * @param filename
	 * @return
	 */
	static public String getFileExtName( String filename )
	{
		if ( filename == null || "".equals( filename ) )
			return filename;

		int dot = filename.lastIndexOf( "." );

		if ( dot > 0 && dot != filename.length() - 1 )
			return filename.substring( dot + 1, filename.length() );
		else
			return "";
	}

	/**
	 * 从下载一个流到本地
	 * @param in
	 * @param tempName
	 * @throws Exception 
	 */
	public static void downLoad( InputStream in, String tempName ) throws Exception
	{	
		  int  byteRead = 0;  
		  FileOutputStream  fileOut = new FileOutputStream( tempName );   
		  byte[] buffer = new byte[1024];
		  while ( (byteRead=in.read(buffer)) != -1 )   
		  {   
			  fileOut.write( buffer, 0, byteRead );   
		  } 
		  fileOut.close();
	}
	/**
	 * 获取文件大小
	 * 
	 * @param size
	 * @return
	 */
	public static String getFileSize(long size) {
		if (size <= 0)
			return "0";
		java.text.DecimalFormat df = new java.text.DecimalFormat("##.##");
		float temp = (float)size / 1024;
		if (temp >= 1024) {
			return df.format(temp / 1024) + "M";
		} else {
			return df.format(temp) + "K";
		}
	}
}