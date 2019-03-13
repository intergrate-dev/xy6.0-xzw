package com.founder.xy.upload;

import javax.servlet.ServletContext;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.FileCleanerCleanup;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileCleaningTracker;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

/**
 * Created by guzm on 2016/12/13.
 */
public class UploadFactories {
    private static volatile DiskFileItemFactory diskFileItemFactory = null;
    private static final int CACHE_FILE_SIZE = 1024 * 100;      //文件在内存中缓存的大小（b）
    private static final int REQUEST_MAX_SIZE = 1024 * 1024 * 50;     //request的最大值（b）

    //获得文件处理factory
    public static DiskFileItemFactory getFileItemFactory() {
        if (diskFileItemFactory == null) {
            synchronized (UploadController.class) {
                if (diskFileItemFactory == null) {
                    diskFileItemFactory = new DiskFileItemFactory();
                    diskFileItemFactory.setSizeThreshold(CACHE_FILE_SIZE);
                    WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
                    ServletContext servletContext = webApplicationContext.getServletContext();
                    FileCleaningTracker fileCleaningTracker
                            = FileCleanerCleanup.getFileCleaningTracker(servletContext);
                    diskFileItemFactory.setFileCleaningTracker(fileCleaningTracker);
                }
            }
        }
        return diskFileItemFactory;
    }

    public static ServletFileUpload getFileUpload(final String mark) {
        DiskFileItemFactory factory = getFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setSizeMax(REQUEST_MAX_SIZE);
        /*
        //上传监听
        upload.setProgressListener(new ProgressListener() {
            long start = System.currentTimeMillis();
            public void update(long pBytesRead, long pContentLength, int pItems) {
                if (pBytesRead == pContentLength) {
                    long end = System.currentTimeMillis();
                    System.out.printf("%s - item %d cost %d ms , total size is %d kb \n", mark, pItems, (end - start),
                                      pContentLength / 1024);
                }
            }
        });
        */
        return upload;
    }
}
