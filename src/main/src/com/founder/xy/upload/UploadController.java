package com.founder.xy.upload;

import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.E5Exception;
import com.founder.e5.sys.StorageDevice;
import com.founder.e5.sys.StorageDeviceManager;
import com.founder.e5.sys.SysFactory;
import com.founder.xy.commons.InfoHelper;
import net.sf.json.JSONObject;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

/**
 * Created by isaac_gu on 2017/3/6.
 * 主要是用于图片及文件上传
 */
@Controller
@RequestMapping("/xy/upload")
public class UploadController {
    @RequestMapping(value = "/uploadFileF.do", method = RequestMethod.POST)
    public void uploadFileF(
            HttpServletRequest request, HttpServletResponse response,
            @RequestParam(defaultValue = "file") String fileName, String targetPath) {
        JSONObject resultJson = new JSONObject();
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if (!isMultipart) {
            resultJson.put("code", 1);
            resultJson.put("error", "提交的表单不是MultiFile！");
            InfoHelper.outputJson(resultJson.toString(), response);
            return;
        }

        saveFiles(request, resultJson);
        InfoHelper.outputJson(resultJson.toString(), response);

    }

    private void saveFiles(HttpServletRequest request, JSONObject resultJson) {
        ServletFileUpload upload = UploadFactories.getFileUpload("file");
        try {
            List<FileItem> items = upload.parseRequest(request);
            String reg = "(?i).+?\\.(jpg|jpeg|png|gif|bmp|zip)";
            for (FileItem item : items) {
                String fileName = item.getName();
                if(!StringUtils.isBlank(fileName)&&!fileName.matches(reg)){
                    resultJson.put("code", 2);
                    resultJson.put("error", "文件格式不符合！");
                    return;
                }

            }

            for (FileItem item : items) {
                if (!item.isFormField()) {
                    String fileName = item.getName();
                    List<String> list = null;
                    if ("zip".equals(FilenameUtils.getExtension(fileName).toLowerCase())) {
                        list = saveZip(request, item, fileName);
                        resultJson.put("type", "zip/rar");
                    }
                    /*else if ("rar".equals(FilenameUtils.getExtension(fileName).toLowerCase())) {
                        list = saveRar(request, item, fileName);
                        resultJson.put("type", "zip/rar");
                    }*/
                    else {
                        String path = savePic(request, item, fileName);
                        list = new ArrayList<>(1);
                        list.add(path);
                        resultJson.put("type", "image");
                        resultJson.put("fileSize", item.getSize());
                        resultJson.put("path", path);

                    }
                    resultJson.put("list", list);
                }
            }
            resultJson.put("code", 0);
            resultJson.put("info", "上传成功！");
        } catch (Exception e) {
            resultJson.put("code", 2);
            resultJson.put("error", "保存文件时出错！");
            e.printStackTrace();
        }
    }

    private List<String> saveZip(HttpServletRequest request, FileItem item, String fileName) throws IOException {
        InputStream is = null;
        FileOutputStream os = null;
        String reg = "(?i).+?\\.(jpg|jpeg|png|gif|bmp)";
        // 附件存储设备的名称
        StorageDevice device = InfoHelper.getPicDevice();
        // 构造存储的路径和文件名，目录为201505/13/，文件名用uuid
        String picPath = InfoHelper.getPicFolderPath(request);
        String savePath = FilenameUtils.normalize(
                InfoHelper.getDevicePath(device) + "//" + picPath);
        String tempFile = savePath + UUID.randomUUID() + ".zip";
        try {
            is = item.getInputStream();
            os = new FileOutputStream(tempFile);
            IOUtils.copy(is, os);
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);
            List<String> list = unzipFile(tempFile, savePath, true, true, reg);
            List<String> _list = new ArrayList<>(list.size());
            String deviceName = device.getDeviceName();
            for (int i = 0, len = list.size(); i < len; i++) {
                _list.add(i, deviceName + ";" + picPath + list.get(i));
                InfoHelper.prepare4Extract(device, picPath + list.get(i));
            }
            return _list;
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);
        }
    }

    private String savePic(HttpServletRequest request, FileItem item, String fileName)
            throws E5Exception, IOException {
        InputStream is = item.getInputStream();
        // 附件存储设备的名称
        StorageDevice device = InfoHelper.getPicDevice();
        // 构造存储的路径和文件名，目录为201505/13/，文件名用uuid
        String savePath = InfoHelper.getPicSavePath(request) + UUID.randomUUID() + "." + FilenameUtils.getExtension(
                fileName);
        // 开始存储到存储设备上
        StorageDeviceManager sdManager = SysFactory.getStorageDeviceManager();
        try {
            sdManager.write(device, savePath, is);
        } finally {
            ResourceMgr.closeQuietly(is);
        }
        InfoHelper.prepare4Extract(device, savePath);
        return device.getDeviceName() + ";" + savePath;
    }

    private String getParam(String paramName, List<FileItem> items) {
        for (FileItem item : items) {
            if (paramName.equals(item.getFieldName()))
                return item.getString();
        }
        return null;
    }

    @RequestMapping("/deletePreviewThumb.do")
    public void deletePreviewThumb(HttpServletResponse response) {
        JSONObject json = new JSONObject();
        json.put("code", 0);
        InfoHelper.outputJson(json.toString(), response);
    }

    /**
     * 新增加的方法 - 1. 不再删除原目标文件夹；2. 直接将文件解压到文件夹下；3. 可以删除原zip包;
     *
     * @param archive       zip文件路径
     * @param decompressDir 解压的路径
     * @param doDeleteZip   是否删除原zip包
     * @param useUUID       是否使用UUID命名文件
     * @param reg           文件类型的正则
     *                      做了一下调整可能不适合通用了：1. 如果遇到文件夹什么都不做；2. 如果不是使用uuid，那压缩包下面的文件可能会出错
     * @throws IOException
     */
    public static List<String> unzipFile(
            String archive, String decompressDir, boolean doDeleteZip, boolean useUUID, String reg) throws IOException {
        BufferedInputStream bi = null;
        BufferedOutputStream bos = null;
        List<String> list = new ArrayList<>();
        ZipFile zf = new ZipFile(archive, "UTF-8");// 支持中文
        try {
            Enumeration e = zf.getEntries();
            while (e.hasMoreElements()) {
                ZipEntry ze2 = (ZipEntry) e.nextElement();
                String entryName = ze2.getName();
                //如果不是图片 直接过
                if (!entryName.matches(reg)) {
                    continue;
                }
                String path = decompressDir + "/" + entryName;
                if (ze2.isDirectory()) {
                   /* File decompressDirFile = new File(path);
                    if (!decompressDirFile.exists()) {
                        decompressDirFile.mkdirs();
                    }*/
                } else {
                    String fileDir = FilenameUtils.getFullPath(path);
                    File fileDirFile = new File(fileDir);
                    if (!fileDirFile.exists()) {
                        fileDirFile.mkdirs();
                    }
                    if (useUUID) {
                        entryName = UUID.randomUUID().toString() + "." + FilenameUtils.getExtension(entryName);
                    }
                    bos = new BufferedOutputStream(
                            new FileOutputStream(decompressDir + "/" + entryName));

                    bi = new BufferedInputStream(zf.getInputStream(ze2));
                    byte[] readContent = new byte[1024];
                    int readCount = bi.read(readContent);
                    while (readCount != -1) {
                        bos.write(readContent, 0, readCount);
                        readCount = bi.read(readContent);
                    }
                    bi.close();
                    bos.close();
                    list.add(entryName);
                }
            }
        } finally {
            IOUtils.closeQuietly(bi);
            IOUtils.closeQuietly(bos);
            IOUtils.closeQuietly(zf);
        }
        if (doDeleteZip) {
            File f = new File(archive);
            if (f.exists()) {
                f.delete();
            }
        }
        return list;
    }

    public static void main(String[] args) {
        String[] sources = new String[]{"picture1.JPG", "picture2.gif", "picture3.BMP", "picture4.zip", "picture5.png"};
        String reg = "(?i).+?\\.(jpg|jpeg|png|gif|bmp)";
        for (int i = 0; i < sources.length; i++) {
            System.out.println(sources[i].matches(reg));
        }
    }
}
