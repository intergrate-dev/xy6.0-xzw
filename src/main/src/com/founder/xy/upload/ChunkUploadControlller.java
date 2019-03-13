package com.founder.xy.upload;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.context.E5Exception;
import com.founder.e5.sys.StorageDevice;
import com.founder.e5.sys.StorageDeviceHelper;
import com.founder.e5.sys.StorageDeviceManager;
import com.founder.e5.sys.SysFactory;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.workspace.Main;

import net.sf.json.JSONObject;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.security.DigestInputStream;
import java.security.MessageDigest;

/**
 * 这个controller主要是接收由webuploader上传文件
 * Created by isaac_gu on 2017/5/10.
 */
@Controller
@RequestMapping("/xy/upload")
public class ChunkUploadControlller {

	/**斜杠根据操作系统自适应*/
	public static final String Slash = System.getProperty("file.separator");
	
    /**
     * 接收上传文件  
     * 由于无法直接从参数中获得变量，只能使用multipartRequest.getParameter()方法获得
     */
    @RequestMapping(value = "/uploadFile.do", method = RequestMethod.POST)
    public void uploadFile(
            HttpServletRequest request, HttpServletResponse response,
            @RequestParam(defaultValue = "file") String fileName) {
    	
    	
        JSONObject resultJson = new JSONObject();
        // 初始化文件接收
        MultipartHttpServletRequest multipartRequest = new CommonsMultipartResolver( request.getSession().getServletContext()).resolveMultipart(request);
        MultipartFile file = multipartRequest.getFile(fileName);

        //获得路径
        String name = multipartRequest.getParameter("name");
        if(!checkFileName(name)) {
        	resultJson.put("code", 1);
            resultJson.put("error", "invalid file name!");
            resultJson.put("info", "不能包含下列任何字符：\\ / : * ? \" < > |");
            InfoHelper.outputJson(resultJson.toString(), response);
            return;
        }
        String target = multipartRequest.getParameter("targetPath");
        if(!checkFileName(target)) {
        	resultJson.put("code", 1);
        	resultJson.put("error", "invalid target path!");
        	resultJson.put("info", "无效的参数值 targetPath");
        	InfoHelper.outputJson(resultJson.toString(), response);
        	return;
        }
        String salt = multipartRequest.getParameter("salt");
        if(!check(salt) || salt.indexOf(".")>0) {
        	resultJson.put("code", 1);
        	resultJson.put("error", "invalid salt!");
        	resultJson.put("info", "无效的参数值 salt");
        	InfoHelper.outputJson(resultJson.toString(), response);
        	return;
        }
        
        String targetPath = getTargetPath(request, target,salt); 
        if(targetPath == null){
        	resultJson.put("code", 1);
            resultJson.put("error", "invalid param value of 'targetPath'!");
            resultJson.put("info", "无效的参数值 targetPath："+target);
            InfoHelper.outputJson(resultJson.toString(), response);
            return;
        }else{
         	String filename = name.substring(0,name.lastIndexOf("."));
        	name = name.replace(filename,salt);
        	targetPath += name;
        }
        //是否需要md5校验
        String canMD5 = multipartRequest.getParameter("canMD5");
        InputStream is = null;
        try {
            is = file.getInputStream();
            //判断是否需要片段上传
            //不需要
            if (StringUtils.isBlank(multipartRequest.getParameter("chunks"))) {
                saveFile(targetPath, is);
            } else {    //需要
                //删除之前因取消遗留下来的文件
                if ("0".equals(multipartRequest.getParameter("chunk"))) {
                    File oldFile = new File(targetPath);
                    if (oldFile.exists()) {
                        oldFile.delete();
                    }
                }
                randomAccessFile(name,targetPath, is);
            }

            resultJson.put("code", 0);
            //判断是否需要进行md5校验
            if ("true".equals(canMD5)) {
                resultJson.put("md5", fileMD5(targetPath));
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultJson.put("code", 1);
            resultJson.put("error", e.getLocalizedMessage());
            resultJson.put("info", "保存文件时出错，请找管理员查看日志！");
        } finally {
            IOUtils.closeQuietly(is);
        }

        InfoHelper.outputJson(resultJson.toString(), response);

    }

	
    /**
	 * 直接保存文件
	 * 
	 * @param targetPath    文件路径
	 * @param is                  输入流
	 */
    private void saveFile(String targetPath, InputStream is) throws IOException {
        String VJ = InfoHelper.getConfig("视频系统","是否经过纳加方式");
        if (VJ.equals("否")){
        	StorageDevice device = InfoHelper.getVideoDevice();
        	StorageDeviceManager sdManager = SysFactory.getStorageDeviceManager();
        	try {
            	String videoPath = InfoHelper.getDevicePath(InfoHelper.getVideoDevice());
            	CharSequence videopath = videoPath;
            	targetPath = targetPath.replace(videopath, "");
				sdManager.write(device, targetPath, is);
			} catch (E5Exception e) {
				e.printStackTrace();
			}
        }else{
        File targetFile = new File(targetPath);
        if (!targetFile.getParentFile().exists()) {
            targetFile.getParentFile().mkdirs();
        }
        OutputStream out = null;
        try {
            out = new FileOutputStream(targetFile);
            IOUtils.copy(is, out);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("saveFile throw exception!");
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(out);
        }
        }
    }



	/**
	 * 分段批量上传
	 * 
	 * @param name    文件名
	 * @param targetPath  文件路径
	 * @param sourceInputStream  IO流
	 */
    private static void randomAccessFile(String name,String targetPath, InputStream sourceInputStream) throws IOException {
        RandomAccessFile randomAccessFile = null; 
        BufferedInputStream inputStream = null;
        String VJ = InfoHelper.getConfig("视频系统","是否经过纳加方式");
        if (VJ.equals("否")){
            
        	//拼接视频文件路径
         	String videoPath = InfoHelper.getDevicePath(InfoHelper.getVideoDevice());
        	CharSequence videopath = videoPath;
        	targetPath = targetPath.replace(videopath, "");     
        	
        	StorageDevice device = InfoHelper.getVideoDevice();
        	String hostName = StorageDeviceHelper.getHostName(device);
        	String fileName = StorageDeviceHelper.getFileName(targetPath);
        	
        	String path = StorageDeviceHelper.getWholeFtpPath(device, targetPath);
        	
        	int port = StorageDeviceHelper.getFtpPort(device);
        	
        	String userName = device.getUserName();
        	String password = device.getUserPassword();
        	
        	boolean passive = device.getDeviceType() == 5;
    		path = path.replaceAll("//" + hostName, "/");
    		path = path.replaceAll("\\\\", "/");
    		if (("".equals(path)) || (path.endsWith("/"))) {
    			path = path + fileName;
    		} else {
    		path = path + "/" + fileName;
    		}
    		FTPClient client = new FTPClient();   		
    		client.setDefaultPort( port );
    		client.connect( hostName,port );
    		client.login(userName, password);
    		
    		//指定二进制流传输（否则会造成文件损坏）
    		client.setFileType(FTP.BINARY_FILE_TYPE);     
			
    		if (passive){
    	    	client.enterLocalPassiveMode();
    	    }
    	    else{
    	    	client.enterLocalActiveMode();
    	    }	
    		
            //设置文件上传偏移量
	        long remoteSize = 0; 
    		FTPFile[] files = client.listFiles(path); 
    		for(FTPFile file : files){
    			if(file.getName().equals(name)){
    				remoteSize =file.getSize();
    				break;
    			}
    		}
    		client.setRestartOffset(remoteSize);
    				
    		if(CreateDirecroty(path, client)==true){
	            OutputStream out = client.appendFileStream(name);	    		
	    		IOUtils.copy(sourceInputStream, out);
	    		out.flush();
	    		sourceInputStream.close();
	    		out.close();
	    		client.completePendingCommand();
    		}
        }else{
          try {
            File targetFile = new File(targetPath);
              if (!targetFile.getParentFile().exists()) {
                targetFile.getParentFile().mkdirs();
              }
            //以读写的方式打开目标文件
            randomAccessFile = new RandomAccessFile(targetFile, "rw");
            randomAccessFile.seek(randomAccessFile.length());
            inputStream = new BufferedInputStream(sourceInputStream);
            byte[] buf = new byte[1024];
            int length;
              while ((length = inputStream.read(buf)) != -1) {
                randomAccessFile.write(buf, 0, length);
              }
          } catch (Exception e) {
              e.printStackTrace();
            throw new IOException("randomAccessFile throw exception!");
          } finally {
              IOUtils.closeQuietly(randomAccessFile);
              IOUtils.closeQuietly(inputStream);
          }
        }
}

	/**
	 * FTP上传创建远程文件目录
	 * 
	 * @param remote    服务器上文件路径
	 * @param ftpClient  FTP服务器
	 */
    public static boolean CreateDirecroty(String remote,FTPClient ftpClient) throws IOException{
    	String directory = remote.substring(0,remote.lastIndexOf("/")+1);
    	  if(!directory.equalsIgnoreCase("/")&&!ftpClient.changeWorkingDirectory(new String(directory.getBytes("GBK"),"iso-8859-1"))){
    	  //如果远程目录不存在，则递归创建远程服务器目录
    	    int start=0;
    	    int end = 0;
    	      if(directory.startsWith("/")){
    	        start = 1;
    	      }else{
    	        start = 0;
    	      }
    	    end = directory.indexOf("/",start);
    	      while(true){
    	        String subDirectory = new String(remote.substring(start,end).getBytes("GBK"),"iso-8859-1");
    	          if(!ftpClient.changeWorkingDirectory(subDirectory)){
    	            if(ftpClient.makeDirectory(subDirectory)){
    	              ftpClient.changeWorkingDirectory(subDirectory);
    	            }else {
                	  System.out.println("创建目录失败");
    	              return false;
                	  }
    	          }

    	        start = end + 1;
            	end = directory.indexOf("/",start);

    	        //检查所有目录是否创建完毕
            	if(end <= start){
    	          break;
            	}
    	      }
    	  }
    	return true;
}
    
    
    private static String fileMD5(String inputFile) throws Exception {
        // 缓冲区大小（这个可以抽出一个参数）
        int bufferSize = 256 * 1024;
        FileInputStream fileInputStream = null;
        DigestInputStream digestInputStream = null;
        try {
            // 拿到一个MD5转换器（同样，这里可以换成SHA1）
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            // 使用DigestInputStream
            fileInputStream = new FileInputStream(inputFile);
            digestInputStream = new DigestInputStream(fileInputStream, messageDigest);
            // read的过程中进行MD5处理，直到读完文件
            byte[] buffer = new byte[bufferSize];
            while (digestInputStream.read(buffer) > 0) ;
            // 获取最终的MessageDigest
            messageDigest = digestInputStream.getMessageDigest();
            // 拿到结果，也是字节数组，包含16个元素
            byte[] resultByteArray = messageDigest.digest();
            // 同样，把字节数组转换成字符串
            return byteArrayToHex(resultByteArray);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("fileMD5 throw exception!");
        } finally {
            IOUtils.closeQuietly(digestInputStream);
            IOUtils.closeQuietly(fileInputStream);
        }
    }
    
    /**将字节数组换成16进制的字符串*/
    private static String byteArrayToHex(byte[] byteArray) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < byteArray.length; n++) {
            stmp = (Integer.toHexString(byteArray[n] & 0XFF));
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
            if (n < byteArray.length - 1) {
                hs = hs + "";
            }
        }
        return hs;
    }
    
    private static boolean checkFileName(String name){
    	return check(name) && name.indexOf(".") > 0;
    }
    private static boolean check(String name){
    	return !StringUtils.isBlank(name)
    			&& name.indexOf("/") == -1
    			&& name.indexOf("\\") == -1
    			&& name.indexOf(":") == -1
    			&& name.indexOf("*") == -1
    			&& name.indexOf("?") == -1
    			&& name.indexOf("\"") == -1
    			&& name.indexOf("<") == -1
    			&& name.indexOf(">") == -1
    			&& name.indexOf("|") == -1;
    }
    
    private static String getTargetPath(HttpServletRequest request, String target, String salt){
    	String _target = target.trim().toLowerCase();
    	String picPath = InfoHelper.getDevicePath(InfoHelper.getAttachDevice());
    	String videoPath = InfoHelper.getDevicePath(InfoHelper.getVideoDevice());
    	String tenantCode = InfoHelper.getTenantCode(request);
    	String fileDatePath = DateUtils.format("yyyyMM"+Slash+"dd"+Slash);
    	String VJ = InfoHelper.getConfig("视频系统", "是否经过纳加方式");
    	if (VJ.equals("否")){
    		String TargetpicPath = picPath + Slash + "source" + Slash + fileDatePath + salt + Slash;
    		TargetpicPath = TargetpicPath.replaceAll("\\\\", "/");
    		String TargetvideoPath = videoPath + Slash + "source" + Slash + fileDatePath + salt + Slash;
    		TargetvideoPath = TargetvideoPath.replaceAll("\\\\", "/");
    		String TargetvideotransPath = videoPath + Slash + "trans" + Slash + fileDatePath + salt + Slash;
    		TargetvideotransPath = TargetvideotransPath.replaceAll("\\\\", "/");
    		if("font.upload.path".equals(_target))
        		return TargetpicPath;
        	if("video.upload.path".equals(_target) || "audio.upload.path".equals(_target))
        		return TargetvideoPath;
        		//return TargetvideotransPath;
    	}else{
    	if("font.upload.path".equals(_target))
    		return FilenameUtils.normalize(picPath + Slash + tenantCode + Slash + "source" + Slash + fileDatePath + salt + Slash);
    	if("video.upload.path".equals(_target))
    		return FilenameUtils.normalize(videoPath + Slash + tenantCode + Slash + "source" + Slash + fileDatePath + salt + Slash);
    	if("audio.upload.path".equals(_target))
    		return FilenameUtils.normalize(videoPath + Slash + tenantCode + Slash + "trans" + Slash + fileDatePath + salt + Slash);
    	}
    	return null;
    	
    }
}
