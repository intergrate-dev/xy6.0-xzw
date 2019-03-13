package com.founder.xy.commons;

import com.founder.e5.commons.ResourceMgr;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FileUtils;

import java.io.*;

/**
 * 图片工具类，提供图片压缩功能
 * Created by Wenkx on 2016/12/13.
 */
public class PicHelper {
    /**
     * Pic zip file.
     *
     * @param is        图片的输入流
     * @param size      压缩后图片的尺寸（长和宽中最长的一边的长度）
     * @param picSuffix 图片格式
     * @return the file
     */
    public static File picZip(InputStream is,int size ,String picSuffix){
        File tmpFile = getTmpFile();
        byte[] dataBuf = new byte[2048];
        BufferedInputStream bis = new BufferedInputStream(is, 2048);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(tmpFile), 2048);
            int count;
            while ((count = bis.read(dataBuf)) != -1) {
                bos.write(dataBuf, 0, count);
            }
            bos.flush();
            bos.close();
            tmpFile =  PicHelper.picZip(tmpFile,size,picSuffix);

        } catch(IOException e){
            e.printStackTrace();
        }
        finally {
            ResourceMgr.closeQuietly(is);
        }
        return tmpFile;
    }

    /**
     * Pic zip file.
     *
     * @param tmpFile   原图片文件
     * @param size      压缩后图片的尺寸（长和宽中最长的一边的长度）
     * @param picSuffix 图片格式
     * @return the file
     */
    public static File picZip(File tmpFile,int size ,String picSuffix){
        try{
            Thumbnails.of(tmpFile.getAbsolutePath()).size(size, size)
                    .toFile(tmpFile.getAbsolutePath() + "_l." + picSuffix);
            tmpFile = new File(tmpFile.getAbsolutePath() + "_l." + picSuffix);

        } catch(IOException e){
            e.printStackTrace();
        }
        return tmpFile;
    }

    /** 取得临时文件 */
    public static File getTmpFile() {
        File tmpDir = FileUtils.getTempDirectory();
        String tmpFileName = (Math.random() * 10000 + "").replace(".", "");
        return new File(tmpDir, tmpFileName);
    }
}
