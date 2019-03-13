package com.founder.xy.special;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.E5Exception;
import com.founder.e5.context.EUID;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.sys.StorageDevice;
import com.founder.e5.sys.StorageDeviceManager;
import com.founder.e5.sys.SysFactory;
import com.founder.e5.web.SysUser;
import com.founder.e5.workspace.ProcHelper;
import com.founder.e5.workspace.app.form.LogHelper;
import com.founder.xy.api.dw.DwApiController;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.jms.PublishTrigger;
import com.founder.xy.jms.data.DocIDMsg;
import com.founder.xy.jpublish.PublishHelper;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;
import com.founder.xy.system.Tenant;

/**
 * 专题管理器
 *
 * @author Gong Lijie
 */
@Service
public class SpecialManager {
	private static final Logger log = LoggerFactory.getLogger(DwApiController.class);

    /**
     * 把专题模板html转换为可视化图片。
     * <p/>
     * TODO：目前问题：1）中文乱码，2）无处设置外网代理
     *
     * 模板文件在存储设备上的地址，如：模板存储;201601/28/5510ae4f-2489-435b-95e3-adfccb2ebea7.html
     *                     return 生成图片的地址，与模板文件同名，后缀为png。如：模板存储;201601/28/5510ae4f-2489-435b-95e3-adfccb2ebea7.html.png
     */
    /*public String convertDeviceFile(String templatePath) {
        String picPath = templatePath + ".png";
        templatePath = InfoHelper.getFilePathInDevice(templatePath);
        String picRealPath = templatePath + ".png";
        convertImage(templatePath, picRealPath);
        return picPath;
    }

    private void convertImage(String templatePath, String picPath) {
        UeditorControl.setProxy();
        String file = "file:" + templatePath;
        FileOutputStream out = null;

        long t0 = System.currentTimeMillis();

        ImageRenderer render = new ImageRenderer();
        try {
            out = new FileOutputStream(new File(picPath));
            render.renderURL(file, out, ImageRenderer.Type.PNG);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(out);
        }
        long t1 = System.currentTimeMillis();
        System.out.println("转换时间:" + (t1 - t0));
    }

    public static void main(String[] args) {
        String file = "Z:\\template\\201606\\28\\show_ef21431b-d605-42cb-8751-3ada65c55905.html";
        String saveFile = "f:\\mypic.png";
        SpecialManager sm = new SpecialManager();
        sm.convertImage(file, saveFile);
        *//*String path = "z:\\template/201605/18/ddf30a84-b05b-44ad-8a8f-17b67764bad9.html";
        SpecialManager sm = new SpecialManager();
        String s = sm.readFileData(path);
        System.out.println(s);*//*
    }*/

    public String htmlTohumbnail(String htmlPath, String canvasurl) {
        String picPath = htmlPath + ".jpg";
        htmlPath = InfoHelper.getFilePathInDevice(htmlPath);
        if (htmlPath == null) {
            return null;
        }
        FileOutputStream os = null;
        /*
        File file = null;
        try {
            String jpgFile = htmlPath + UUID.randomUUID() + ".jpg";
            file = new File(jpgFile);
            os = new FileOutputStream(jpgFile);
            ImageRenderer r = new ImageRenderer();
            r.renderURL("file:" + htmlPath, os, htmlPath);
            Thumbnails.of(jpgFile)
                    .sourceRegion(Positions.TOP_LEFT, 1200, 1200)
                    .size(1200, 1200)
                    .keepAspectRatio(false)
                    .toFile(htmlPath + ".jpg");
            System.out.println("Done.");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (file != null) {
                    file.delete();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        */
        try {
            String jpgFile = htmlPath + ".jpg";
            if (canvasurl.indexOf("data:image/png;base64,") != -1) {
                canvasurl = canvasurl.replace("data:image/png;base64,", "");
            }
            // 生成jpeg图片
            byte[] b = Base64.decodeBase64(canvasurl.getBytes());
            os = new FileOutputStream(jpgFile);
            os.write(b);
            os.flush();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return picPath;
    }

    public String saveAsDesignFile(String filePath, String htmlData) {
        return saveSpecialFile(filePath, htmlData, "design");
    }

    public String saveAsShowFile(String filePath, String htmlData, String str) {
    	String code = "show";
    	if("pad".equals(str))
    		code = code + str;
        return saveSpecialFile(filePath, htmlData, code);
    }

    /**
     * 保存模版文件
     */
    public String saveSpecialFile(String filePath, String htmlData, String flag) {
        InputStream in = null;
        String savePath = null;
        try {
            in = new ByteArrayInputStream(htmlData.getBytes("UTF-8"));
            StorageDevice device = InfoHelper.getTemplateDevice();
            StorageDeviceManager sdManager = SysFactory.getStorageDeviceManager();

            //保存路径；tenant + File.separator +
            if (!StringUtils.isBlank(filePath)) {
                savePath = filePath.substring(filePath.indexOf(";") + 1); //相对路径和文件名
            } else {
                savePath = DateUtils.format("yyyyMM/dd/") + flag + "_" + UUID.randomUUID().toString() + ".html";
            }
            sdManager.write(device, savePath, in, device.getDeviceType());
            savePath = device.getDeviceName() + ";" + savePath;
        } catch (Exception e) {
            e.printStackTrace();
            savePath = null;
        } finally {
            ResourceMgr.closeQuietly(in);
        }


        return savePath;
    }

    public void checkInfo(
            String code, InputStream in, StorageDevice device, StorageDeviceManager sdManager, String savePath) {
        System.out.println("in is null: " + in);
        System.out.println("device is null: " + device);
        System.out.println("sdManager is null" + sdManager);
        System.out.println("savePath: " + savePath);
        System.out.println("********************************" +
                                   code +
                                   " END *********************************");
    }

    /**
     * 保存special对象
     * @param templatePADID 
     */
    public Long saveSpecialModel(
            Document special, String specialPath, Integer docLibID,long docID, Integer siteID, Integer groupID,
            Long templateID, Long templatePADID, String s_name, String s_picPath,String expdate) {
        try {
            //保存
            DocumentManager docManager = DocumentManagerFactory.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");

            //获得 专题的model对象
            if (special == null) {
                special = docManager.newDocument(docLibID, docID);
                ProcHelper.initDoc(special);
            }
            special.set("s_siteID", siteID);
            special.set("s_groupID", groupID);
            special.set("s_name", s_name);
            special.set("s_templateID", templateID);
            if(templatePADID != null && templatePADID != 0)
            special.set("s_templatePADID", templatePADID);
            special.set("s_picPath", s_picPath);
            special.set("s_file", specialPath);
            special.setLastmodified(Timestamp.valueOf(sdf.format(new Date())));

            //有效日期字段
            special.set("s_expireDate",sdf1.parse(expdate));
            docManager.save(special);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return special.getDocID();

    }

    /**
     * 保存模版
     * 5）	模板记录保存在模板表中，名称为“专题模板：<专题设计的名称>”，模板路径为上面一步的存储路径，groupID设为0。
     * 组ID=0不会在模板管理界面中按组展示时出现。
     * 若希望能看到，则应保存在单独的“专题模板组”中（保存时检查和自动创建这个组）
     */
    public Document saveTemplate(
            Document template, String templatePath, Integer siteID, Integer groupID,
            String s_name,String expdate) {
        //设为0
        groupID = 0;
        //保存
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        boolean isNew = template == null;
        try {
            //获得 专题的model对象
            template = saveTemplate(template, templatePath, siteID, groupID, s_name, docManager, isNew,expdate);
        } catch (E5Exception e) {
            //保存不上就再存一次
            try {
            	log.debug("---专题保存不成功，再存一次---:" + templatePath);
                template = saveTemplate(template, templatePath, siteID, groupID, s_name, docManager, isNew,expdate);
            } catch (E5Exception e1) {
                e1.printStackTrace();
            }
        }
        return template;
    }

    private Document saveTemplate(
            Document template, String templatePath, Integer siteID, Integer groupID, String s_name,
            DocumentManager docManager, boolean isNew,String expdate) throws E5Exception {
        if (isNew) {
            long docID = EUID.getID("DocID" + DocTypes.TEMPLATE.typeID());
            template = docManager.newDocument(LibHelper.getTemplateLibID(), docID);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        template.set("t_siteID", siteID);
        template.set("t_groupID", groupID);
        template.set("t_name", "专题模板：<" + s_name + ">");
        template.set("t_fileType", "html");
        template.set("t_file", templatePath);

        template.set("t_type", 0);
        template.set("t_channel", 0);
        //有效日期
        try{
            template.set("t_expireDate",sdf.parse(expdate));
        }catch (Exception e){
            e.printStackTrace();
        }


        docManager.save(template);
        return template;
    }

    /**
     * 参考 templatecontroller
     *
     * @param doc
     * @throws Exception
     */
    /*public void rename(Document doc) throws Exception {
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        String fileName = doc.getString("t_file");

        int pos = fileName.indexOf(";");
        String device = fileName.substring(0, pos); //存储设备
        String rltPath = fileName.substring(fileName.indexOf(";") + 1); //相对路径和文件名

        //文件的全路径
        String devicePath = InfoHelper.getDevicePath(InfoHelper.getDeviceByName(device));
        String oldPath = devicePath + File.separator + rltPath;

        //文件的新路径：用UUID给文件重新命名，按日期做目录
        String newFileName = UUID.randomUUID().toString();
        String newrltPath = DateUtils.format("yyyyMM/dd/") + newFileName + "." + doc.getString("t_fileType");
        String newPath = devicePath + File.separator + newrltPath;

        //改名并且写回数据库
        File oldFile = new File(oldPath);
        if (oldFile.exists()) {
            File newFile = new File(newPath);
            if (oldFile.renameTo(newFile)) {
                String newName = device + ";" + newrltPath;
                doc.set("t_file", newName);
                docManager.save(doc);
            }
        }
    }*/

    /**
     * 获得document 对象
     */
    public Document getDocument(int libId, long docId) {
        DocumentManager dm = DocumentManagerFactory.getInstance();
        Document document = null;
        try {
            document = dm.get(libId, docId);
        } catch (E5Exception e) {
            e.printStackTrace();
        }
        return document;
    }

    /**
     * 读取文件中的数据 - 字符串
     */
    public String readFileData(String path) {
        String s = "";
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(new File(path));
            s = IOUtils.toString(fis, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(fis);
        }

        return s;
    }

    /**
     * 获取稿件json
     */
    public String getArticlesJson(int docLibID, String docIDs, int ch, int style) throws E5Exception {
        String[] ids = docIDs.split(",");
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        int attLibId = LibHelper.getLibID(DocTypes.ATTACHMENT.typeID(), Tenant.DEFAULTCODE);
        JSONArray arr = new JSONArray();
        for (int i = 0; i < ids.length; i++) {

            try {
                long docID = Long.parseLong(ids[i]);
                Document doc = docManager.get(docLibID, docID);
                int status = doc.getInt("a_status");
                if (status != 1) continue;//稿件未发布，不返回，继续下一个
                JSONObject inObj = new JSONObject();
                inObj.put("id", docID);
                String condition = "att_articleID=? ";
                if (StringUtils.getNotNull(doc.getString("a_picBig")) != "") {
                    condition += "and att_type=2";
                } else if (StringUtils.getNotNull(doc.getString("a_picMiddle")) != "") {
                    condition += "and att_type=3";
                } else if (StringUtils.getNotNull(doc.getString("a_picSmall")) != "") {
                    condition += "and att_type=4";
                } else {
                    condition = "";
                }
                String imagePath = "";
                if (condition != "") {
                    Document[] attDoc = docManager.find(attLibId, "att_type=2 and att_articleID=? and  att_articleLibID=?",
                                                        new Object[]{docID,docLibID});

                    if (attDoc.length > 0) {
                        if (ch == 0)
                            imagePath = StringUtils.getNotNull(attDoc[0].getString("att_url"));
                        else
                            imagePath = StringUtils.getNotNull(attDoc[0].getString("att_urlPad"));
                    }
                }
                inObj.put("imgPath", imagePath);
                String title = StringUtils.getNotNull(doc.getString("a_linkTitle"));
                title = title.replaceAll("</?[^<]+>", "");
                inObj.put("title", title);
                inObj.put("src", StringUtils.getNotNull(doc.getString("a_source")));
                inObj.put("summary", StringUtils.getNotNull(doc.getString("a_abstract")));
               // if (ch == 0) {
                    inObj.put("link", StringUtils.getNotNull(doc.getString("a_url")));
               // } else if (ch == 1) {
                    inObj.put("linkPad", StringUtils.getNotNull(doc.getString("a_urlPad")));
               // }
                if (style == 0 || style == 1) {
                    String pubtime = doc.getString("a_pubTime");
                    inObj.put("PublishTime", pubtime.substring(0, pubtime.lastIndexOf(".")));
                }
                if (style == 2) {
                    inObj.put("pubStatus", doc.getInt("a_status"));
                    inObj.put("type", doc.getInt("a_type"));
                    inObj.put("linkTitle", StringUtils.getNotNull(doc.getString("a_linkTitle")));
                    String pubtime = doc.getString("a_pubTime");
                    inObj.put("pubTime", pubtime.substring(0, pubtime.lastIndexOf(".")));
                    inObj.put("editor", doc.getString("a_editor"));
                    String modifyTime = doc.getString("SYS_LASTMODIFIED");
                    inObj.put("modifyTime", modifyTime.substring(0, modifyTime.lastIndexOf(".")));
                    inObj.put("bigPic", StringUtils.getNotNull(doc.getString("a_picBig")));
                }
                arr.add(inObj);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
        return arr.toString();
    }

    public void specialDelete(int docLibID, long[] docIDs, HttpServletRequest request) throws E5Exception {
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        int templateLibID = LibHelper.getTemplateLibID();
        for (long docID : docIDs) {
            Document special = docManager.get(docLibID, docID);
            long templateId = special.getLong("s_templateID");
            Long templatePADId = special.getLong("s_templatePADID");
            //删除专题PC模板,删除到回收站
            Document template = docManager.get(templateLibID, templateId);
            refreshCache(special, template);
            template.set("SYS_DELETEFLAG", 1);
            docManager.save(template);
            // 记录日志
         	writeLog(templateLibID, templateId, ProcHelper.getUser(request));
            //删除专题触屏模板,删除到回收站
            if(templatePADId != null && templatePADId != 0){
	            Document templatePAD = docManager.get(templateLibID, templatePADId);
	            refreshCache(special, templatePAD);
	            templatePAD.set("SYS_DELETEFLAG", 1);
	            docManager.save(templatePAD);
	            writeLog(templateLibID, templatePADId, ProcHelper.getUser(request));
            }
            //删除专题,删除到回收站
            special.set("SYS_DELETEFLAG", 1);
            docManager.save(special);
            writeLog(special.getDocLibID(), special.getDocID(), ProcHelper.getUser(request));
        }
    }
    private void writeLog(int docLibID, Long docID, SysUser user) throws E5Exception {
		LogHelper.writeLog(docLibID, docID, user, "删除", "删除模板");
	}

	//删除专题模板同时清空缓存中模板的组件实例
    private void refreshCache(Document special, Document template) {
		String docID = template.getString("sys_documentid");
		String docLibID = template.getString("sys_doclibid");
		
		//把模板从Redis缓存中去掉
		RedisManager.hclear(RedisKey.TEMPLATE_FILE_KEY, docID);
		//删除模板全部本地文件
		deleteTpl(special, template.getString("t_file"));
		//发送删除模板消息
		PublishTrigger.otherData(Integer.parseInt(docLibID), Long.parseLong(docID), DocIDMsg.TYPE_TEMPLATE);
	}

	private void deleteTpl(Document doc, String parseName) {

		String fileName = doc.getString("s_file");
		int pos = fileName.indexOf(";");
		String device = fileName.substring(0, pos); // 存储设备
		String rltPath = fileName.substring(fileName.indexOf(";") + 1); // 相对路径和文件名
		String parsePath = parseName.substring(parseName.indexOf(";") + 1); // 相对路径和文件名

		// 文件的绝对路径
		String devicePath = InfoHelper.getDevicePath(InfoHelper
				.getDeviceByName(device));
		String oldPath = devicePath + File.separator + rltPath;
		String oldparsePath = devicePath + File.separator + parsePath + ".parsed";
		String oldhtmlPath = devicePath + File.separator + parsePath;

		// 删除实体文件
		deleteFile(oldPath);
		deleteFile(oldparsePath);
		deleteFile(oldhtmlPath);
	}

	private void deleteFile(String filePath) {
		File file = new File(filePath);
		if (file != null) {
			file.delete();
		}
	}

	public boolean isExist(Integer docLibId, String specialName) throws E5Exception {
        boolean status = false;
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        Object[] params = new Object[]{specialName};
        String conditions = "s_name=? and SYS_DELETEFLAG=0";
        Document[] doc = docManager.find(docLibId, conditions, params);
        if (doc != null && doc.length > 0) {
            status = true;
        }
        return status;
    }
    public boolean isExists(Integer docLibId, String specialName,String date,int docId) throws E5Exception {
        boolean status = false;
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        Object[] params = new Object[]{specialName};
        String conditions = "s_name=? and SYS_DELETEFLAG=0";
        Document[] doc = docManager.find(docLibId, conditions, params);
        try{
            if (doc != null && doc.length > 0&&doc[0].getDate("s_expireDate").getTime()==sdf.parse(date).getTime()&&docId!=doc[0].getInt("SYS_DOCUMENTID")) {
                status = true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return status;
    }

    public String copyFile(String targetPath, String templatePath) {
        if (targetPath != null && !targetPath.contains(";")) {
            return "../xy/special/images/special.png";
        }
        String picPath = templatePath + ".png";
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            templatePath = InfoHelper.getFilePathInDevice(templatePath);
            targetPath = InfoHelper.getFilePathInDevice(targetPath);
            String picRealPath = templatePath + ".png";
            fis = new FileInputStream(new File(targetPath));
            fos = new FileOutputStream(new File(picRealPath));
            IOUtils.copy(fis, fos);
        } catch (IOException e) {
            e.printStackTrace();
            picPath = "../xy/special/images/special.png";
        } finally {
            ResourceMgr.closeQuietly(fis);
            ResourceMgr.closeQuietly(fos);
        }


        return picPath;
    }

	public Document[] findAllDocs(int articleLibID, long templateId) throws E5Exception {
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        Object[] params = new Object[]{templateId};
        String conditions = "SYS_DELETEFLAG=0 and a_templateID=?";
        Document[] doc = docManager.find(articleLibID, conditions, params);
        return doc;
	}

	public void specialPicDelete(int docLibID, long[] docIDs) throws E5Exception, IOException {
        for (long docID : docIDs) {
        	publishALLPics(docLibID, docID);
        	String sql = "update xy_attachment set SYS_DELETEFLAG=1 where att_articleID=? and att_articleLibID=?";
			Object[] params = new Object[]{docID, docLibID};
			InfoHelper.executeUpdate(sql, params);
			//trans发送空文件
        }
	}

	private void publishALLPics(int docLibID, long docID) throws E5Exception, IOException {
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        Document special = docManager.get(docLibID, docID);
        int attLibID = LibHelper.getAttaLibID();
        String[] siteDir = readSiteInfo(special.getString("s_siteID"));
        Object[] params = new Object[]{docID, docLibID};
        String conditions = "SYS_DELETEFLAG=0 and att_articleID=? and att_articleLibID=?";
        Document[] docs = docManager.find(attLibID, conditions, params);
        for(Document doc:docs)
        	if(doc != null){
        		String filePath = doc.getString("att_path");
        		if(!StringUtils.isBlank(filePath)){
        			File file = new File(filePath);
        			if(file.exists())
        				file.delete();
        			if(!file.getParentFile().exists())
        				file.getParentFile().mkdirs();
        			file.createNewFile();
        			PublishHelper.writeTransPath(filePath, siteDir[0]);
        		}
        			
        	}
	
		
	}

	private String[] readSiteInfo(String siteID) {
		return InfoHelper.readSiteInfo(Long.parseLong(siteID));
	}

	public String saveAsLocalFile(String filePath, String htmlData) {
        return saveSpecialFile(filePath, htmlData, "local");
        }
}
