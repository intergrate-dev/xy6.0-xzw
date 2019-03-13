package com.founder.xy.commons;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.fusesource.hawtbuf.ByteArrayInputStream;

import com.founder.e5.cat.CatManager;
import com.founder.e5.cat.CatReader;
import com.founder.e5.cat.Category;
import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.Log;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.context.EUID;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.doc.util.E5docHelper;
import com.founder.e5.dom.DocLib;
import com.founder.e5.rel.service.RelTableReader;
import com.founder.e5.sys.StorageDevice;
import com.founder.e5.sys.StorageDeviceManager;
import com.founder.e5.sys.StorageDeviceReader;
import com.founder.e5.sys.SysConfigReader;
import com.founder.e5.sys.SysFactory;
import com.founder.xy.config.SecurityHelper;
import com.founder.xy.system.Tenant;

/**
 * 系统通用类
 *
 * @author Gong Lijie
 */
public class InfoHelper {

    private static final String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // 定义script的正则表达式
    private static final String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // 定义style的正则表达式
    private static final String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
    private static final String regEx_space = "\\s*|\t|\r|\n";//定义空格回车换行符

    private static Log log = Context.getLog("xy");
    private static CatReader catReader;
    private static CatManager catManager;
    

    /**
     * 取稿件的文档类型ID。 常用方法
     */
    public static int getArticleTypeID() {
        return DocTypes.ARTICLE.typeID();
    }

    /**
     * 按分类类型的编码得到根分类数组
     *
     * @param code   分类类型的编码
     * @param siteID
     * @return
     */
    public static Category[] getCatGroups(HttpServletRequest request, String code, int siteID) {
        int catTypeID = (Enum.valueOf(CatTypes.class, "CAT_" + code)).typeID();
        return getCatGroups(request, catTypeID, siteID);
    }
    
    /**
     * 按分类类型的编码得到根分类数组
     *
     * @param code   分类类型的编码
     * @param siteID
     * @return
     */
    public static Category[] getCatGroups(HttpServletRequest request, String code, int siteID, int len) {
        int catTypeID = (Enum.valueOf(CatTypes.class, "CAT_" + code)).typeID();
        return getCatGroups(request, catTypeID, siteID, len);
    }

    /**
     * 按名字读存储设备
     */
    public static StorageDevice getDeviceByName(String device) {
        try {
            //存储设备可能是编码过的，先解码
            device = URLDecoder.decode(device, "UTF-8");

            StorageDeviceReader sdReader = (StorageDeviceReader) Context
                    .getBean(StorageDeviceReader.class);
            return sdReader.getByName(device);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 读出存储设备的路径
     */
    public static String getDevicePath(StorageDevice device) {
        String path = null;
        if (device.getDeviceType() == StorageDevice.DEVICE_TYPE_NFS) {
            path = device.getNfsDevicePath();
        } else if (device.getDeviceType() == StorageDevice.DEVICE_TYPE_NTFS) {
            path = device.getNtfsDevicePath();
        } else if (device.getDeviceType() == StorageDevice.DEVICE_TYPE_FTP) {
            path = device.getFtpDeviceURL();
        }
        return path;
    }

    /**
     * 读出一个保存在存储设备上的文件的完整路径
     *
     * @param path 格式类似于"图片存储;201508/10/32235ssdf.jpg"
     * @return
     */
    public static String getFilePathInDevice(String path) {
        String[] paths = StringUtils.split(path, ";");

        String devicePath = getDevicePath(getDeviceByName(paths[0]));

        return devicePath + "/" + paths[1];
    }


    /**
     * 读图片存储设备
     */
    public static StorageDevice getPicDevice() {
        // 用系统参数配置来管理，这样当一个存储设备满了时，可以指到新设备
        return getDevice("存储设备", "图片存储设备");
    }

    /**
     * 读报纸存储设备
     */
    public static StorageDevice getPaperDevice() {
        // 用系统参数配置来管理，这样当一个存储设备满了时，可以指到新设备
        return getDevice("存储设备", "报纸存储设备");
    }

    /**
     * 读视频存储设备
     */
    public static StorageDevice getVideoDevice() {
        return getDevice("存储设备", "视频存储设备");
    }

    /**
     * 读附件存储设备
     */
    public static StorageDevice getAttachDevice() {
        return getDevice("存储设备", "附件存储设备");
    }

    /**
     * 读模板存储设备
     */
    public static StorageDevice getTemplateDevice() {
        return getDevice("存储设备", "模板存储设备");
    }

    /**
     * 读水印存储设备
     */
    public static StorageDevice getWaterMarkDevice() {
        // 用系统参数配置来管理，这样当一个存储设备满了时，可以指到新设备
        return getDevice("存储设备", "水印存储设备");
    }

    /**
     * 读全媒体存储设备
     */
    public static StorageDevice getNewsEditDevice() {
        // 用系统参数配置来管理，这样当一个存储设备满了时，可以指到新设备
    	StorageDevice device = getDevice("存储设备", "全媒体存储设备");
    	if (device == null) {
    		device = getOtherSystemDevice();
    	}
    	return device;
    }

    /**
     * 读其它系统投稿存储设备
     */
    public static StorageDevice getOtherSystemDevice() {
        // 用系统参数配置来管理，这样当一个存储设备满了时，可以指到新设备
        return getDevice("存储设备", "外部系统稿件存储");
    }

    /**
     * 读参数配置中指定的存储设备
     */
    public static StorageDevice getDevice(String project, String item) {
        // 用系统参数配置来管理，这样当一个存储设备满了时，可以指到新设备
        try {
            String device = getConfig(project, item);
            StorageDeviceReader sdReader = (StorageDeviceReader) Context
                    .getBean(StorageDeviceReader.class);
            return sdReader.getByName(device);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获得图片的存储路径, 没有扩展名
     *
     * @param request
     * @param extension
     * @return
     */
    public static String getPicSavePath(HttpServletRequest request) {
        // 构造存储的路径和文件名，目录为201505/13/，文件名用uuid
        String savePath = InfoHelper.getTenantCode(request) + File.separator
                + DateUtils.format("yyyyMM/dd/") + UUID.randomUUID().toString();
        return savePath.replaceAll("\\\\", "/");
    }

    public static String getPicFolderPath(HttpServletRequest request) {
        // 构造存储的路径和文件名，目录为201505/13/，文件名用uuid
        String savePath = InfoHelper.getTenantCode(request) + File.separator
                + DateUtils.format("yyyyMM/dd/");
        return savePath.replaceAll("\\\\", "/");
    }
    /**
     * 读一个分类类型下的站点根分类ID（分类码=siteID），用于按站点读分类。
     * <p>
     * 找不到时：若siteID=1，则为了兼容已有app，返回0，此时读整个分类树
     */
    public static int getSiteCatID(String tenantCode, int catTypeID, int siteID) {
    	/*
    	 * 2017.7.12.
    	 * 栏目类型、栏目样式改为全局分类。所以不会找到站点根分类，返回0
    	 * 已有项目：仍使用“默认站点”下的分类，自动改为siteID=1。（已有项目基本都只一个站点）
    	 */
        if (siteID > 1 && (catTypeID == CatTypes.CAT_COLUMN.typeID() 
        		|| catTypeID == CatTypes.CAT_COLUMNSTYLE.typeID()))
        	siteID = 1;
        
        CatManager catManager = (CatManager) Context.getBean(CatManager.class);
        Category root = null;
        try {
            //从根分类中找出分类码为站点ID的分类
            Category[] roots = catManager.getSubCats(catTypeID, 0);
            if (roots != null) {
                String code = String.valueOf(siteID);
                for (Category cat : roots) {
                    if (code.equals(cat.getCatCode())) {
                        root = cat;
                        break;
                    }
                }
            }
            if (root == null) {
                //为兼容已有app，是默认站点1时直接读所有的分类
                if (siteID == 1 && roots != null && roots.length > 0) return 0;
            }
            return root != null ? root.getCatID() : -1;
        } catch (E5Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 按分类类型得到根分类数组。 注意不是读缓存，而是直接读数据库
     */
    public static Category[] getCatGroups(HttpServletRequest request, int catTypeID, int siteID) {
        Category root = null;
        try {
            // 以站点ID作为分类编码，找到站点对应的根分类
            CatManager catManager = (CatManager) Context.getBean(CatManager.class);
            Category[] roots = catManager.getSubCats(catTypeID, 0);
            if (roots != null) {
                String code = String.valueOf(siteID);
                for (Category cat : roots) {
                    if (code.equals(cat.getCatCode())) {
                        root = cat;
                        break;
                    }
                }
            }
            if (root == null) {
                String tenantCode = getTenantCode(request);
                root = createSiteCat(tenantCode, catTypeID, siteID);
            }

            return catManager.getSubCats(catTypeID, root.getCatID());
        } catch (E5Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static Category[] getCatGroups(HttpServletRequest request, int catTypeID, int siteID, int len) {
        Category root = null;
        try {
            // 以站点ID作为分类编码，找到站点对应的根分类
            CatManager catManager = (CatManager) Context.getBean(CatManager.class);
            Category[] roots = catManager.getSubCats(catTypeID, 0);
            if(siteID != -1){
	            if (roots != null && roots.length>0) {
	                String code = String.valueOf(siteID);
	                for (Category cat : roots) {
	                    if (code.equals(cat.getCatCode())) {
	                        root = cat;
	                        break;
	                    }
	                }
	            }
	            if (root == null) {
	                String tenantCode = getTenantCode(request);
	                root = createSiteCat(tenantCode, catTypeID, siteID);
	            }
	
	            return catManager.getSubCats(catTypeID, root.getCatID());
            }else{
            	Category[] cates = new Category[len];
	            if (roots != null && roots.length>0) {
	            	int i=0;
	                for (Category cat : roots) {
                        root = cat;
                        if (root == null) {
                        	String tenantCode = getTenantCode(request);
                        	root = createSiteCat(tenantCode, catTypeID, siteID);
                        }
                      if(catManager.getSubCats(catTypeID, root.getCatID()) != null)
	                      for(Category cate : catManager.getSubCats(catTypeID, root.getCatID())){
	                    	  cates[i] = cate;
	                    	  i++;
	                      }
	                }
	            }
	
	            return cates;
            }
        } catch (E5Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 按分类类型得到根分类数组。 注意不是读缓存，而是直接读数据库
     */
    public static Category createSiteCat(String tenantCode, int catTypeID, int siteID) {
        try {
            // 以站点ID作为分类编码，找到站点对应的根分类
            int siteLibID = LibHelper.getLibID(DocTypes.SITE.typeID(), tenantCode);

            DocumentManager docManager = DocumentManagerFactory.getInstance();
            Document site = docManager.get(siteLibID, siteID);

            Category root = new Category();
            root.setCatCode(String.valueOf(siteID));
            root.setCatName(site.getString("site_name"));
            root.setCatType(catTypeID);

            CatManager catManager = (CatManager) Context.getBean(CatManager.class);
            catManager.createCat(root);

            return root;
        } catch (E5Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 通用的executeUpdate方法
     *
     * @throws Exception
     */
    public static int executeUpdate(String sql, Object[] params) throws E5Exception {
        DBSession conn = null;
        try {
            conn = Context.getDBSession();
            return conn.executeUpdate(sql, params);
        } catch (SQLException e) {
            if (params != null)
                for (int i = 0; i < params.length; i++) {
                    System.out.println("params[" + i + "]:" + params[i]);
                }
            throw new E5Exception(e);
        } finally {
            ResourceMgr.closeQuietly(conn);
        }
    }

    /**
     * 通用的executeUpdate方法，指定文档库ID。
     * 互动等模块可能拆分到不同的数据库
     */
    public static int executeUpdate(int docLibID, String sql, Object[] params) throws E5Exception {
        DBSession conn = null;
        try {
            conn = E5docHelper.getDBSession(docLibID);
            return conn.executeUpdate(sql, params);
        } catch (SQLException e) {
            if (params != null)
                for (int i = 0; i < params.length; i++) {
                    System.out.println("params[" + i + "]:" + params[i]);
                }
            throw new E5Exception(e);
        } finally {
            ResourceMgr.closeQuietly(conn);
        }
    }

    public static int executeUpdate(String sql, Object[] params, DBSession conn) throws E5Exception {
        try {
            return conn.executeUpdate(sql, params);
        } catch (SQLException e) {
            if (params != null)
                for (int i = 0; i < params.length; i++) {
                    System.out.println("params[" + i + "]:" + params[i]);
                }
            throw new E5Exception(e);
        }
    }

    /**
     * 向response输出数据，xml格式或text/plain格式
     */
    public static void outputXML(String result, HttpServletResponse response) {
        if (result == null) return;

        response.setContentType("text/xml; charset=UTF-8");

        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            out.write(result);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(out);
        }
    }

    /**
     * 向response输出text/plain数据
     */
    public static void outputText(String result, HttpServletResponse response) {
        if (result == null) return;

        response.setContentType("text/plain; charset=UTF-8");

        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.write(result);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(out);
        }
    }

    /**
     * 向response输出json数据
     */
    public static void outputJson(String result, HttpServletResponse response) {
        if (result == null) return;

        response.setContentType("application/json; charset=UTF-8");

        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.write(result);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(out);
        }
    }

    /**
     * 从一个List<Integer>对象得到int[]，由于转换不方便而提供通用方法
     */
    public static int[] getIntArray(List<Integer> ids) {
        int[] result = new int[ids.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = ids.get(i);
        }
        return result;
    }

    public static long[] getLongArray(List<Long> ids) {
        long[] result = new long[ids.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = ids.get(i);
        }
        return result;
    }

    /**
     * 转换json非法符号，如回车换行、引号
     */
    public static String filter4Json(String value) {
        if (value == null) return "";

        value = value.replace("\r", "\\r");
        //value = value.replace("\n", "\\n");
        value = value.replace("\n", "&#13;&#10;");
        //value = value.replace("\"", "\\\"");
        value = value.replace("\"", "&quot;");
        value = value.replace("\\", "\\\\");
        return value;
    }

    /**
     * 读一个系统参数
     */
    public static String getConfig(String project, String item) {
        int appID = 1;
        try {
            SysConfigReader sysReader = (SysConfigReader) Context.getBean(SysConfigReader.class);
            return sysReader.get(appID, project, item);
        } catch (Exception e) {
            log.error("读取系统参数失败:" + appID + "," + project + "," + item, e);
            return "";
        }
    }
    
    /**
     * 取表类型的下一个文档ID ，用于复制，新建等
     */
    public static long getNextDocID(int docTypeID) {
        try {
            return EUID.getID("DocID" + docTypeID);
        } catch (E5Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取两天相差的天数
     */
    public static int daysBetween(String startDate, String endDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(startDate));
        long time1 = cal.getTimeInMillis();
        cal.setTime(sdf.parse(endDate));
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);
        return Integer.parseInt(String.valueOf(between_days));
    }

    /**
     * 取稿件库对应的关联表（不同稿件库对应的栏目关联表不同）
     */
    public static String getRelTable(int docLibID) {
        RelTableReader relReader = (RelTableReader) Context.getBean(RelTableReader.class);
        int catTypeID = CatTypes.CAT_COLUMNARTICLE.typeID();

        return relReader.getRelTableName(docLibID, catTypeID);
    }

    /**
     * 从session中取得租户代号
     *
     * @param request
     * @return
     */
    public static String getTenantCode(HttpServletRequest request) {
    	if (request == null) return Tenant.DEFAULTCODE;
    	
        Tenant tenant = (Tenant) request.getSession().getAttribute(Tenant.SESSIONNAME);
        if (tenant == null)
            return Tenant.DEFAULTCODE;
        else
            return tenant.getCode();
    }

    public static DBSession getDBSession(int docLibID) throws E5Exception {
        DocLib docLib = LibHelper.getLibByID(docLibID);
        return Context.getDBSession(docLib.getDsID());
    }

    /**
     * 把 给定map转成url参数 例如 转成 ?d=4&b=2&c=3&a=1
     */
    public static String assembleURLParameters(Map<String, Object> map) {
        // 如果map为空直接返回
        if (map.isEmpty())
            return "";
        // 结果
        StringBuilder result = new StringBuilder();
        result.append("?");
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            result.append(entry.getKey() + "=" + entry.getValue() + "&");
        }
        return result.toString().substring(0, result.length() - 1);
    }

    // TODO 去掉string中为null 为“” 的项目
    // 1. 取出旧数组中的一个数据
    // 2. 如果新数组中包含的话，把新数组中这个数据去掉 - 说明不需要进行操作
    // 3. 如果新数组中没有的话，说明这个数据已经被取消，把这个数据放到删除数组当中
    // 4. 最后经过删除旧数据的新数组就是添加数组了
    public static Map<String, List<String>> assembleAddAndDelArray(
            String oldIdsStr,
            String newIdsStr) {
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        List<String> oldList = new LinkedList<String>(Arrays.asList(oldIdsStr.split(",")));
        List<String> newList = new LinkedList<String>(Arrays.asList(newIdsStr.split(",")));
        List<String> delList = new ArrayList<String>(oldList.size());
        for (String id : oldList) {
            if (id != null && !id.trim().equals("")) {
                if (newList.contains(id)) {
                    newList.remove(id);
                } else {
                    delList.add(id);
                }
            }
        }
        map.put("delList", delList);
        map.put("addList", newList);
        return map;
    }

    /**
     * 去除Ids中的空项
     *
     * @param Ids
     * @param seperator
     * @return
     */
    public static String removeItemsFromIds(String Ids, String seperator, String[] removeItems) {
        if (Ids == null || "".equals(Ids))
            return "";
        Set<String> idSet = new HashSet<String>();
        CollectionUtils.addAll(idSet, Ids.split(seperator));
        if (idSet.isEmpty())
            return "";
        if (removeItems != null && removeItems.length > 0) {
            for (String _Item : removeItems) {
                idSet.remove(_Item);
            }
        }
        StringBuilder _Ids = new StringBuilder();
        for (String s : idSet) {
            _Ids.append(seperator + s);
        }
        return _Ids.length() > 0 ? _Ids.toString().substring(seperator.length()) : _Ids.toString();
    }

    public static String getTextFromHtml(String htmlStr) {
        htmlStr = delHTMLTag(htmlStr);
        htmlStr = htmlStr.replaceAll("&nbsp;", "");
        return htmlStr;
    }

    /**
     * 把存储设备上的文件复制到另一个路径下，使用原来的相对路径和文件名
     *
     * @param path     存储设备路径，格式如“附件存储;201507/30/abc.png”
     * @param destPath 目标目录
     */
    public static boolean copyFile(String path, String destPath) {
        return copyFile(path, destPath, null);
    }

    /**
     * 把存储设备上的文件复制到另一个路径下，指定复制后的文件名
     *
     * @param srcPathFile  存储设备路径，格式如“附件存储;201507/30/abc.png”
     * @param destPath     目标目录
     * @param destFileName 指定的相对路径和文件名
     */
    public static boolean copyFile(String srcPathFile, String destPath, String destFileName) {
        int pos = srcPathFile.indexOf(";");
        if (pos < 0) return false;

        String deviceName = srcPathFile.substring(0, pos);
        String savePath = srcPathFile.substring(pos + 1);

        if (!destPath.endsWith("/")) destPath += "/";

        destPath += (destFileName == null) ? savePath : destFileName;

        File destFile = new File(destPath);

        StorageDeviceManager sdManager = SysFactory.getStorageDeviceManager();
        InputStream in = null;
        try {
            //存储设备可能是编码过的，先解码
            deviceName = URLDecoder.decode(deviceName, "UTF-8");

            in = sdManager.read(deviceName, savePath);
            if (in != null) {
                FileUtils.copyInputStreamToFile(in, destFile);
            }
            return true;
        } catch (E5Exception | IOException e1) {
            System.out.println("复制文件异常：" + e1.getLocalizedMessage()
                                       + "，srcPathFile=" + srcPathFile);
            return false;
        } finally {
            ResourceMgr.closeQuietly(in);
        }
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
     * 删除Html标签
     */
    private static String delHTMLTag(String htmlStr) {
        Pattern p_script = Pattern.compile(regEx_script,
                                           Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(htmlStr);
        htmlStr = m_script.replaceAll(""); // 过滤script标签

        Pattern p_style = Pattern
                .compile(regEx_style, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(htmlStr);
        htmlStr = m_style.replaceAll(""); // 过滤style标签

        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(htmlStr);
        htmlStr = m_html.replaceAll(""); // 过滤html标签

        Pattern p_space = Pattern
                .compile(regEx_space, Pattern.CASE_INSENSITIVE);
        Matcher m_space = p_space.matcher(htmlStr);
        htmlStr = m_space.replaceAll(""); // 过滤空格回车标签
        return htmlStr.trim(); // 返回文本字符串
    }

    public static String MD5(String sourceStr) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            try {
                md.update(sourceStr.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString();
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e);
        }
        return result;
    }
    
    /**
     * 写稿中是否要进行敏感词检查
     * @return
     */
    public static boolean sensitiveInArticle() {
    	//没有敏感词加密点
    	if (!SecurityHelper.sensitiveUsable()) return false;
    	
    	//开关没启动
		String _hasSensitive = InfoHelper.getConfig("写稿服务", "是否启用敏感词分析");
		return "是".equals(_hasSensitive);
    }
    
    /**
     * 写稿中是否要进行非法词检查
     * @return
     */
    public static boolean illegalInArticle() {
    	//没有敏感词加密点
    	if (!SecurityHelper.sensitiveUsable()) return false;
    	
    	//开关没启动
		String _hasSensitive = InfoHelper.getConfig("写稿服务", "是否启用非法词分析");
		return "是".equals(_hasSensitive);
    }
    
    /**
     * 互动中是否要进行敏感词检查
     * @return
     */
    public static boolean sensitiveInNis() {
    	//没有敏感词加密点
    	if (!SecurityHelper.sensitiveUsable()) return false;
    	
    	//开关没启动
		String _hasSensitive = InfoHelper.getConfig("互动", "是否启用敏感词分析");
		return "是".equals(_hasSensitive);
    }
    
    /**
     * 取分类的分类码
     */
    public static String getCatCode(int catTypeID, int catID) {
        try {
            if (catReader == null) catReader = (CatReader) Context.getBean(CatReader.class);
            Category cat = catReader.getCat(catTypeID, catID);
            if (cat != null)
                return cat.getCatCode();

            //若缓存中没有，可能是新加的，用CatManager取一次
            if (catManager == null) catManager = (CatManager) Context.getBean(CatManager.class);
            cat = catManager.getCat(catTypeID, catID);
            return (cat == null) ? "" : cat.getCatCode();
        } catch (E5Exception e) {
            System.out.println(e.getLocalizedMessage());
            return "";
        }
    }
    
	/**
	 * 日期时间格式化：yyyy-MM-dd HH:mm:ss.S
	 */
    public static String formatDate() {
		return formatDate(DateUtils.getTimestamp());
	}
    
	/**
	 * 日期时间格式化：yyyy-MM-dd HH:mm:ss.S
	 */
    public static String formatDate(Date time) {
		return DateUtils.format(time, "yyyy-MM-dd HH:mm:ss.S");
	}

	/**
	 * 日期时间格式化：yyyy-MM-dd HH:mm:ss.S
	 */
    public static String formatDate(IResultSet rs, String field) {
		try {
			Timestamp time = rs.getTimestamp(field);
			if (time == null) return "";
			
			return formatDate(time);
		} catch (SQLException e) {
			return "";
		}
	}
    
	/**
	 * 时间格式化：HH:mm:ss
	 */
	public static String formatTime(int time) {
		int intHour = time / 3600;
		String hour = intHour <= 0 ? "00" : (intHour >= 10 ? String.valueOf(intHour) : "0" + intHour);
		
		int intMinute = time / 60 % 60;
		String minute = intMinute <= 0 ? "00" : (intMinute >= 10 ? String.valueOf(intMinute) : "0" + intMinute);
		
		int intSecond = time % 60;
		return hour + ":" + minute + ":" + (intSecond >= 10 ? intSecond : ("0" + intSecond));
	}
    
	/**
	 * 时间解析：从HH:mm:ss解析成整数
	 */
	public static int parseTime(String time) {
		if (StringUtils.isBlank(time)) return 0;
		
		int[] split = StringUtils.getIntArray(time, ":");
		return split[0] * 3600 + split[1] * 60 + split[2];
	}

    /**
     * 读站点的发布根目录、资源根目录、资源根发布路径
     * @param siteID
     */
    public static String[] readSiteInfo(long siteID) {
        String[] result = new String[3];

        DocumentManager docManager = DocumentManagerFactory.getInstance();
        //获取站点,然后获取站点发布根目录
        int siteLibID = LibHelper.getLib(DocTypes.SITE.typeID()).getDocLibID();
        try {
            Document site = docManager.get(siteLibID, siteID);
            
            String webRoot = getWebRoot(site.getDocID());
    		if (StringUtils.isBlank(webRoot))
    			webRoot = site.getString("site_webRoot");
    		
            result[0] = webRoot;
            result[1] = site.getString("site_resPath");
            result[2] = site.getString("site_resUrl");
        } catch (E5Exception e) {
            e.printStackTrace();
        }

        return result;
    }
    
    /**
     * 读站点的发布根目录
     * @param siteID
     */
    public static String getWebRoot(long siteID) {
    	return getConfig("发布服务", "发布根目录");
    }
}