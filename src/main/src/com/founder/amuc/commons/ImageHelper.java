package com.founder.amuc.commons;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import com.founder.amuc.member.HeadImage;

/**
 * @author Ren Yanfang
 * 2014-7-3
 */
public class ImageHelper {
	/*
     * 对图片裁剪，并把裁剪完蛋新图片保存 。
     */
    public static void cut(HeadImage headImage) throws IOException{ 
        FileInputStream is = null ;
        ImageInputStream iis =null ;
        String srcPath = headImage.getSrcPath();
        String subPath=headImage.getSubPath();
        int x=headImage.getX();
        int y=headImage.getY();
        int width=headImage.getWidth();
        int height=headImage.getHeight();
        String fileExt="jpg";
        try{   
            is = new FileInputStream(srcPath); 

            if (srcPath.lastIndexOf(".") >= 0) 
				fileExt = srcPath.substring(srcPath.lastIndexOf(".")+1);
            /*
             * 返回包含所有当前已注册 ImageReader 的 Iterator，这些 ImageReader 
             * 声称能够解码指定格式。 参数：formatName - 包含非正式格式名称 .
             *（例如 "jpeg" 或 "tiff"）等 。 
             */
            Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName(fileExt);  
            ImageReader reader = it.next(); 
            //获取图片流 
            iis = ImageIO.createImageInputStream(is);
               
            /*
             * <p>iis:读取源.true:只向前搜索 </p>.将它标记为 ‘只向前搜索’。
             * 此设置意味着包含在输入源中的图像将只按顺序读取，可能允许 reader
             *  避免缓存包含与以前已经读取的图像关联的数据的那些输入部分。
             */
            reader.setInput(iis,true) ;
            
            /*
             * <p>描述如何对流进行解码的类<p>.用于指定如何在输入时从 Java Image I/O 
             * 框架的上下文中的流转换一幅图像或一组图像。用于特定图像格式的插件
             * 将从其 ImageReader 实现的 getDefaultReadParam 方法中返回 
             * ImageReadParam 的实例。  
             */
            ImageReadParam param = reader.getDefaultReadParam(); 
             
            /*
             * 图片裁剪区域。Rectangle 指定了坐标空间中的一个区域，通过 Rectangle 对象
             * 的左上顶点的坐标（x，y）、宽度和高度可以定义这个区域。 
             */ 
            if(x<0) x=0;
            if(y<0) y=0;
            Rectangle rect = new Rectangle(x, y, width, height); 
                        
            //提供一个 BufferedImage，将其用作解码像素数据的目标。 
            param.setSourceRegion(rect); 

            /*
             * 使用所提供的 ImageReadParam 读取通过索引 imageIndex 指定的对象，并将
             * 它作为一个完整的 BufferedImage 返回。
             */
            BufferedImage bi = reader.read(0,param);                
      
            //保存新图片 
            ImageIO.write(bi, fileExt, new File(subPath));     
        }
        finally{
            if(is!=null)
               is.close() ;       
            if(iis!=null)
               iis.close();  
        } 
    }
    
    public static void ZoomTheImage(String fileUrl,String newUrl,int width,int height){
    	//读入刚才上传的文件
    	java.io.File file = new java.io.File(fileUrl);         
	    Image src = null;
		try {
			src = javax.imageio.ImageIO.read(file);
			//构造Image对象
		    BufferedImage tag = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
		    
		    //绘制缩小后的图
		    tag.getGraphics().drawImage(src,0,0,width,height,null);        
		    String formatName = newUrl.substring(newUrl.lastIndexOf(".") + 1);
			ImageIO.write(tag, formatName, new File(newUrl));
			file.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}  
	}
}
