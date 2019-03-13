package com.founder.xy.api.jabbar;

import com.founder.e5.cat.CatReader;
import com.founder.e5.cat.Category;
import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.DomHelper;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.context.EUID;
import com.founder.e5.db.DBSession;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.permission.PermissionHelper;
import com.founder.e5.sso.SSO;
import com.founder.e5.sys.StorageDevice;
import com.founder.e5.sys.StorageDeviceManager;
import com.founder.e5.sys.SysFactory;
import com.founder.e5.sys.org.Role;
import com.founder.e5.sys.org.RoleReader;
import com.founder.e5.sys.org.User;
import com.founder.e5.sys.org.UserReader;
import com.founder.e5.web.WebUtil;
import com.founder.e5.web.org.StringValueUtils;
import com.founder.e5.workspace.ProcHelper;
import com.founder.e5.workspace.app.CatRoleService;
import com.founder.e5.workspace.app.form.LogHelper;
import com.founder.e5.workspace.mergerole.controller.SysUser;
import com.founder.xy.api.dw.DwApiController;
import com.founder.xy.article.Article;
import com.founder.xy.article.ArticleManager;
import com.founder.xy.column.Column;
import com.founder.xy.column.ColumnReader;
import com.founder.xy.commons.*;
import com.founder.xy.config.Channel;
import com.founder.xy.config.ConfigReader;
import com.founder.xy.config.TabHelper;
import com.founder.xy.jms.PublishTrigger;
import com.founder.xy.system.Tenant;
import com.founder.xy.system.TenantManager;
import com.founder.xy.system.site.Site;
import com.founder.xy.system.site.SiteManager;
import com.founder.xy.system.site.SiteUserManager;
import com.founder.xy.system.site.SiteUserReader;
import org.apache.commons.lang.ArrayUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.*;

@Service
public class JabbarApiManager {

    @Autowired
    private ColumnReader colReader;
    private static final Logger log = LoggerFactory.getLogger(DwApiController.class);

    @Autowired
    private SiteUserReader siteUserReader;
    @Autowired
    private TenantManager tenantManager;
    @Autowired
    private SiteManager siteManager;
    @Autowired
    private ArticleManager articleManager;
    @Autowired
    private SiteUserManager userManager;

    private SSO sso;

    private int WebChannel = 1;
    private int APPChannel = 2;

    // 登陆 并将用户信息写入session
    int jabbarLogin(HttpServletRequest request) {
        ssoInit();

        String usercode = request.getParameter("UserCode");
        String sPass2 = request.getParameter("UserPassword");

        try {

            int ret = sso.verifyUserPassword(usercode, sPass2);
            if (ret != 0)
                return -1;

            else {

                UserReader userReader = (UserReader) Context.getBean(UserReader.class);
                User curUser = userReader.getUserByCode(usercode);

                int loginSiteID = 0;
                Role[] roles = null;
                int userLibID = LibHelper.getUserExtLibID(request);
                int userID = curUser.getUserID();
                if ("1".equals(curUser.getProperty3())) {
                    // 若是租户管理员，则不判断站点权限，取出租户下的所有站点，取出管理员角色
                    loginSiteID = getAdminSite(curUser);
                    RoleReader roleReader = (RoleReader) Context.getBean(RoleReader.class);
                    roles = roleReader.getRolesByUser(curUser.getUserID());
                    if (roles == null || roles.length == 0) {
                        roles = getRolesBySite(userLibID, curUser.getUserID(), loginSiteID);
                    }
                } else {
                    // 取用户可管理的站点列表

                    List<Site> sites = siteUserReader.getSites(userLibID, userID);
                    if (sites.size() > 0) {
                        loginSiteID = sites.get(0).getId();
                        roles = getRolesBySite(userLibID, userID, loginSiteID);
                    }
                }
                if (roles == null || roles.length == 0)

                    return -1;
                else {
                    int roleID = roles[0].getRoleID();
                    int newRoleID = (roles.length == 1) ? roleID : PermissionHelper.mergeRoles(roles);

                    String[] rets = sso.login(usercode, roleID, request.getRemoteAddr(), request.getServerName(), true);
                    int nID = Integer.parseInt(rets[0]);
                    if (nID > 0) {
                        // clearSession(request);
                        putSession(curUser, sPass2, roleID, nID, newRoleID, request);

                    }
                    return 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public String getSiteInfo(HttpServletRequest request) throws E5Exception {
        int userLibID = LibHelper.getUserExtLibID(request);
        int userID = ProcHelper.getUserID(request);
        Document document = DocumentHelper.createDocument();
        document.setXMLEncoding("utf-8");
        Element root = document.addElement("LIST");
        // 站点数据
        List<Site> siteList = siteUserReader.getSites(userLibID, userID);
        for (int i = 0; i < siteList.size(); i++) {
            // 创建站点节点元素
            Site site = siteList.get(i);
            Element item = root.addElement("ITEM");
            item.addAttribute("id", site.getId() + "");
            item.addElement("TITLE").addCDATA(site.getName());
        }
        return document.asXML();
    }

    /**
     * 获取指定站点下的指定栏目下的节点信息； 根据用户名，站点id和父节点id得到用户在该站点下所具有的栏目权限的栏目信息
     *
     * @param userID   用户ID
     * @param siteid   站点id
     * @param parentID 父节点id
     * @param channel
     * @return
     */

    public String getSiteNodeInfo(int userID, int siteid, int parentID, int channel, int roleID) throws E5Exception {

        int colLibID = LibHelper.getLibID(DocTypes.COLUMN.typeID(), Tenant.DEFAULTCODE);
        Document document = DocumentHelper.createDocument();
        document.setXMLEncoding("utf-8");
        Element root = document.addElement("LIST");
        if (channel == -1) {
            int catTypeID = CatTypes.CAT_ORIGINAL.typeID();
            Category[] cats = null;
            if (parentID == 0) {
                CatRoleService catRoleService = new CatRoleService();
                cats = catRoleService.getCatsByRole(roleID, catTypeID);
            } else {
                CatReader catReader = (CatReader) Context.getBean(CatReader.class);
                cats = catReader.getSubCats(catTypeID, parentID);
            }
            return jsonTreeWithParent(cats);
        }

        // 从根节点获取时，直接获取从根节点到可操作节点间的所有节点
        if (parentID == 0) {
            Column[] subList = null;
            // 取出用户可操作的栏目
            int type = channel == 0 ? 0 : 4;
            subList = colReader.getOpColumns(colLibID, userID, siteid, type, roleID);
            return XMLTreeWithParent(subList);
        }
        // 从非根节点获取时，由于已经拥有此节点及其所有子节点的操作权限，直接返回此节点的所有子节点
        else {
            List<Column> subList = null;

            try {
                subList = colReader.getSub(colLibID, parentID);
            } catch (NullPointerException e) {

                subList = null;
                e.printStackTrace();
            }

            if (subList != null) {
                for (Column column : subList) {
                    Element item = root.addElement("ITEM");
                    item.addAttribute("id", column.getId() + "");
                    item.addElement("TITLE").addCDATA(column.getName());
                }
            }
            return document.asXML();
        }

    }

    //带父节点（无权限）的栏目树的json
    private String jsonTreeWithParent(Category[] cats) throws E5Exception {
        Document document = DocumentHelper.createDocument();
        document.setXMLEncoding("utf-8");
        Element root = document.addElement("LIST");
        if (cats != null) {
            for (Category cat : cats) {
                XMLOneCat(cat, root);
            }
        }
        return document.asXML();
    }

    //把一个分类转换成XNL节点
    private void XMLOneCat(Category cat, Element root) {
        Element item = root.addElement("ITEM");
        item.addAttribute("id", cat.getCatID() + "");
        item.addElement("TITLE").addCDATA(cat.getCatName());
        item.addElement("casID").addCDATA(cat.getCascadeID());
        item.addElement("nocheck").addCDATA("false");
        item.addElement("isParent").addCDATA(String.valueOf(cat.getChildCount()>0));
    }

    // 带父节点（无权限）的栏目树的XML
    private String XMLTreeWithParent(Column[] cols) throws E5Exception {
        List<Column> roots = getRoots(cols);
        Document document = DocumentHelper.createDocument();
        document.setXMLEncoding("utf-8");
        Element root = document.addElement("LIST");
        if (roots != null) {
            for (Column column : roots) {
                XMLOneCol(column, root);
            }
        }
        return document.asXML();
    }

    // 根据指定的栏目，得到从根栏目开始的栏目树对象
    private List<Column> getRoots(Column[] cols) throws E5Exception {
        if (cols == null)
            return null;

        // 保证顺序
        Map<Integer, Column> tree = new HashMap<Integer, Column>();

        for (Column col : cols) {
            // 把无权限的父节点也带上
            int[] path = StringUtils.getIntArray(col.getCasIDs(), "~");
            Column pCol = tree.get(path[0]);
            if (pCol == null) {
                pCol = getCol(col.getLibID(), path[0]);
                if (pCol != null)
                    tree.put(path[0], pCol);
            } else if (pCol.isEnable()) {
                // 栏目拖动会造成栏目的父子结构发生变化。若发现父栏目已经有权限，则不必加子栏目
                continue;
            }

            if (path.length == 1) {
                pCol.setEnable(true);
                pCol.removeChildren(); // 节点设置为enable后就可动态展开，不需要设置children
                continue;
            }

            Column parent = pCol;
            for (int i = 1; i < path.length; i++) {
                Column son = parent.getChild(path[i]);
                if (son == null) {
                    son = getCol(col.getLibID(), path[i]);
                    if (son != null)
                        parent.addChild(son);
                } else if (son.isEnable()) {
                    break;
                }
                // 最后一级的栏目，是确实有权限的，所以enable=true
                if (i == path.length - 1) {
                    son.setEnable(true);
                }
                parent = son;
            }
        }
        return sortColByOrder(tree);
    }

    /**
     * 按设置的栏目顺序读出栏目
     *
     * @param map
     * @return
     */
    private List<Column> sortColByOrder(Map<Integer, Column> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        List<Column> cols = Arrays.asList(map.values().toArray(new Column[0]));
        Collections.sort(cols, new Comparator<Column>() {
            public int compare(Column me1, Column me2) {
                return new Integer(me1.getOrder()).compareTo(new Integer(me2.getOrder()));
            }
        });
        return cols;
    }

    // 处理一个栏目的XML转换
    private void XMLOneCol(Column col, Element root) throws E5Exception {

        Element item = root.addElement("ITEM");
        item.addAttribute("id", col.getId() + "");
        item.addElement("TITLE").addCDATA(col.getName());
        item.addElement("casID").addCDATA(col.getCasIDs());
        item.addElement("nocheck").addCDATA(Boolean.toString(!col.isEnable()));
        item.addElement("isParent").addCDATA(Boolean.toString(col.isExpandable()));
        Element childrn = item.addElement("childrn");
        if (col.getChildren() != null) {

            List<Column> cols = col.getChildren();
            for (int i = 0; i < cols.size(); i++) {
                Column son = cols.get(i);
                XMLOneCol(son, childrn);
            }
        }
    }

    // 根据栏目ID得到Col对象
    private Column getCol(int colLibID, long id) throws E5Exception {
        boolean enable = false;

        Column parent = colReader.get(colLibID, id);
        if (parent == null)
            return null;

        return new Column(id, parent.getName(), parent.getCasIDs(), parent.getCasNames(), enable, parent.isExpandable(),
                parent.getOrder(), parent.isForbidden());
    }

    void ssoInit() {
        if (sso == null) {
            sso = (SSO) Context.getBeanByID("ssoReader");
        }
    }

    // 验证通过后，把用户的信息存到session里
    void putSession(User curUser, String sPass2, int roleID, int loginID, int newRoleID, HttpServletRequest request) {
        SysUser user = new SysUser();

        user.setUserID(curUser.getUserID());
        user.setUserName(curUser.getUserName());
        user.setUserCode(curUser.getUserCode());

        user.setAdmin(true); // 设为系统管理员，使前台可以设置操作权限（此时不判断文档类型管理权限）
        // user.setUserPassword(sPass2);
        user.setRoleID(roleID);
        user.setLoginID(loginID);
        user.setIp(request.getRemoteAddr());

        user.setRealRoleID(roleID);
        user.setRoleID(newRoleID);

        request.getSession().setAttribute(SysUser.sessionName, user);

        if (needSysAdmin(newRoleID)) {
            // 为了能在前台设置分类、部门角色和权限，加管理端需要的session
            request.getSession().setAttribute(SysUser.sessionAdminName, user);
        }

        // 把租户放在session中（租户代号用于读对应文档库，租户机构ID用于管理部门和角色）
        // 扩展字段1中保存租户代号，扩展字段2保存的是否管理员
        Tenant tenant = tenantManager.get(curUser.getProperty1());
        request.getSession().setAttribute(Tenant.SESSIONNAME, tenant);
    }

    // 判断角色是否有部门角色的主界面权限
    private boolean needSysAdmin(int roleID) {
        if (roleID <= 0)
            return false;

        // 读取tabs
        String[] roleTabs = TabHelper.readRoleTabs(roleID, "MainPermission");
        return ArrayUtils.contains(roleTabs, "scat") || ArrayUtils.contains(roleTabs, "sorg");
    }

    Role[] getRolesBySite(int userLibID, int userID, int siteID) {
        int[] roleIDs = siteUserReader.getRoles(userLibID, userID, siteID);
        if (roleIDs == null || roleIDs.length == 0)
            return null;

        Role[] roles = new Role[roleIDs.length];
        for (int i = 0; i < roles.length; i++) {
            roles[i] = new Role();
            roles[i].setRoleID(roleIDs[i]);
        }
        return roles;
    }

    /**
     * 生成新的会话，防止客户端操纵会话标识
     */
    void clearSession(HttpServletRequest request) {
        try {
            request.getSession().invalidate();

            if (request.getCookies() != null) {
                Cookie cookie = request.getCookies()[0];// 获取cookie
                cookie.setMaxAge(0);// 让cookie过期
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 取租户管理员的站点中的第一个站点ID
    int getAdminSite(User curUser) throws E5Exception {
        // 若是租户管理员，则不判断权限，取出租户下的所有站点，取出租户角色
        String tenantCode = curUser.getProperty1();

        com.founder.e5.doc.Document[] docs = siteManager.getSites(tenantCode);
        if (docs == null || docs.length == 0)
            return 0;
        return (int) docs[0].getDocID();
    }

    // 写稿时，读系统参数
    void putConfigParam(Map<String, Object> model) {
        int sourceType = "可手填".equals(InfoHelper.getConfig("写稿", "来源填写方式")) ? 1 : 0;
        model.put("sourceType", sourceType);

        model.put("hasKeyword", !StringUtils.isBlank(UrlHelper.keyWordsServiceUrl()));
        model.put("hasSummary", !StringUtils.isBlank(UrlHelper.summaryServiceUrl()));
        model.put("hasSensitive", InfoHelper.sensitiveInArticle());

        boolean canEditStyle = "是".equals(InfoHelper.getConfig("写稿", "启用编辑样式"));
        model.put("canEditStyle", canEditStyle);

        String _videoPluginUrl = InfoHelper.getConfig("视频系统", "视频播放控件地址");
        model.put("videoPlugin", _videoPluginUrl);
    }

    List<Channel> getChannels(boolean isNew, int channel) {
        Channel[] allChs = ConfigReader.getChannels();

        if (isNew)
            return Arrays.asList(allChs);

        List<Channel> chs = new ArrayList<Channel>();
        for (int i = 0; i < allChs.length; i++) {
            if (allChs[i] != null && ((int) Math.pow(2, i) & channel) == 0) {
                chs.add(allChs[i]);
            }
        }
        return chs;
    }

    /**
     * 保存稿件
     *
     * @param article
     * @param attachementList
     * @return
     */
    private String save(com.founder.e5.doc.Document article, List<com.founder.e5.doc.Document> attachementList) {
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        DBSession conn = null;
        try {
            conn = Context.getDBSession();
            conn.beginTransaction();
            docManager.save(article, conn);
            for (com.founder.e5.doc.Document attachement : attachementList) {
                docManager.save(attachement, conn);
            }
            // 提交transaction
            conn.commitTransaction();
            return null;
        } catch (Exception e) {
            ResourceMgr.rollbackQuietly(conn);
            e.printStackTrace();
            return "操作中出现错误：" + e.getLocalizedMessage();
        } finally {
            ResourceMgr.closeQuietly(conn);
        }
    }

    com.founder.e5.doc.Document assembleNewArticle(HttpServletRequest request) throws E5Exception, IOException {

        int ch = WebUtil.getInt(request, "channel", -1);// 默认-1 原稿库
        int docLibID = getDocLibIDByCh(ch);
        int siteID = StringValueUtils.getInt(request.getParameter("SiteID"), 0);
        int FVID = DomHelper.getFVIDByDocLibID(docLibID);
        int type = WebUtil.getInt(request, "Type", 0);// 稿件类型（文章/组图/视频等）
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        // 打开写稿界面时就预取稿件ID
        long docID = InfoHelper.getNextDocID(DocTypes.ARTICLE.typeID()); // 提前取稿件ID
        com.founder.e5.doc.Document article = docManager.newDocument(docLibID, docID);

        if (ch != -1) {
            //设置栏目相关字段
            setColumn(request, article);
            article.set("a_channel", ch == 0 ? WebChannel : APPChannel); // 渠道，按位
        } else {
            int catID = 3;
            String NodeID = request.getParameter("NodeID");
            if(!StringUtils.isBlank(NodeID)) {
                String[] ColumnIDs = NodeID.split("\\|");
                catID = StringValueUtils.getInt(ColumnIDs[0]);
            }
            article.set("a_catID", catID);
            article.set("a_channel", 0);
        }
        article.set("a_siteID", siteID);
        article.set("a_type", type);
        article.set("a_editor", ProcHelper.getUserName(request));
        article.set("a_orgID", userManager.getUser(LibHelper.getUserExtLibID(), ProcHelper.getUserID(request)).getInt("u_orgID"));
        // JSONObject jsonObject = getArticleJSON(request);
        article.setLastmodified(DateUtils.getTimestamp());
        article.setLocked(false);
        article.setCurrentUserName(ProcHelper.getUserName(request));
        article.setCurrentUserID(ProcHelper.getUserID(request));
        article.setFolderID(FVID);
        article.set("a_docLibID", docLibID);
        article.set("a_abstract", request.getParameter("Abstract"));
        String Title = request.getParameter("Title");
        article.setTopic(Title.replaceAll("[\n\r]", ""));
        article.setAuthors(request.getParameter("Author"));
        article.set("a_keyword", request.getParameter("keyword").replaceAll(" ", ","));

        article.set("a_source", request.getParameter("Source"));
        article.set("a_isSensitive", 0);
        article.set("a_source", request.getParameter("Source"));
        article.set("a_templatePadID", WebUtil.getInt(request, "templatePadID", 0));
        article.set("a_templateID", WebUtil.getInt(request, "templateID", 0));

        String content = WebUtil.getStringParam(request, "Content");

        if (content != null) {
            //增加翔宇分页符
            content = content.replaceAll("<hr>", "_ueditor_page_break_tag_");
            content = content.replaceAll("<hr/>", "_ueditor_page_break_tag_");
            article.set("a_content", content);

        }
        return article;
    }

    private void setColumn(HttpServletRequest request, com.founder.e5.doc.Document article) throws E5Exception {
        int colLibID = LibHelper.getColumnLibID(request);
        int ColumnID = 0;// 栏目
        String NodeID = request.getParameter("NodeID");
        StringBuilder ColumnRel = new StringBuilder();
        StringBuilder ColumnRelIDs = new StringBuilder();
        if (!StringUtils.isBlank(NodeID)) {
            String[] ColumnIDs = NodeID.split("\\|");
            ColumnID = StringValueUtils.getInt(ColumnIDs[0]);
            if (ColumnIDs.length > 1) {// 关联栏目
                for (int i = 1; i < ColumnIDs.length; i++) {
                    int ColumnRelID = StringValueUtils.getInt(ColumnIDs[i]);
                    Column colRel = colReader.get(colLibID, ColumnRelID);
                    if (i == 1) {
                        ColumnRelIDs = new StringBuilder("" + ColumnRelID);
                        ColumnRel = new StringBuilder(colRel.getCasNames());
                    } else {
                        ColumnRel.append(',').append(colRel.getCasNames());
                        ColumnRelIDs.append(',' + ColumnRelID);
                    }
                }
            }
        }
        article.set("a_columnID", ColumnID);
        article.set("a_columnRel", ColumnRel.toString());
        article.set("a_columnRelID", ColumnRelIDs.toString());
        Column col = colReader.get(colLibID, ColumnID);

        if (col != null) {
            article.set("a_column", col.getCasNames());
            article.set("a_extFieldGroup", col.getExtFieldGroup());
            article.set("a_extFieldGroupID", col.getExtFieldGroupID());
        }
    }

    void saveAttachment(HttpServletRequest request, List<com.founder.e5.doc.Document> attachementList, com.founder.e5.doc.Document article) throws E5Exception {
        // piccount图片附件数量，小于0没有图片 不需要上传
        int piccount = WebUtil.getInt(request, "piccount", -1);
        log.error("-----稿件图片数量：JabbarApiManager.java：635---"+ piccount);
        if (piccount < 0) return;
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        StorageDeviceManager sdManager = SysFactory.getStorageDeviceManager();
        // 其它系统存储设备的名称
        StorageDevice otherSystemDevice = InfoHelper.getOtherSystemDevice();
        log.error("-----外部系统稿件存储路径：JabbarApiManager.java：641---"+ otherSystemDevice.getFtpDeviceURL());
        // 附件存储设备的名称
        StorageDevice device = InfoHelper.getPicDevice();
        String content = article.getString("a_content");
        boolean first = true;
        //默认稿件无图
        article.set("SYS_HAVEATTACH",0);
        for (int i = piccount - 1; i >= 0; i--) {
            String oldPicName = request.getParameter("filename" + i);
            String oldPicContent = request.getParameter("description" + i);
            log.error("-----稿件图片名称：JabbarApiManager.java：649---"+ oldPicName);
            //正文中包含图片再处理
            if (content.contains(oldPicName) || "1".equals(request.getParameter("Type"))) {
                long attDocID = InfoHelper.getNextDocID(DocTypes.ATTACHMENT.typeID());
                int attLibID = LibHelper.getLibIDByOtherLib(DocTypes.ATTACHMENT.typeID(), article.getDocLibID());
                com.founder.e5.doc.Document atta = docManager.newDocument(attLibID, attDocID);
                String attachHref = request.getParameter("filecode" + i);
                log.error("-----attachHref：JabbarApiManager.java：657---"+ attachHref);
                int pos = attachHref.lastIndexOf(".");
                String picSuffix = "";
                if (pos != -1) {
                    picSuffix = attachHref.substring(attachHref.lastIndexOf("."));
                }
                String newfilename = UUID.randomUUID().toString() + picSuffix;

                // 构造存储的路径和文件名，目录为201505/13/，文件名用uuid
                String tenantCode = InfoHelper.getTenantCode(request);
                String savePath = tenantCode + "/" + DateUtils.format("yyyyMM/dd/") + newfilename;
                log.error("-----稿件图片路径：JabbarApiManager.java：649---"+ savePath);
                InputStream is = null;
                InputStream islen = null;
                try {
                    is = sdManager.read(otherSystemDevice, attachHref);
                    islen = sdManager.read(otherSystemDevice, attachHref);
                    log.error("-----图片是否读取到：JabbarApiManager.java：672---"+ (islen != null));
                    try {
                    	if(islen != null)
						log.error("-----图片大小：JabbarApiManager.java：672---"+ islen.read(new byte[1024]));
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
                    int temp = 0;
                    //有时候天钩上传图片未完成 多试几次读取
                    while (temp < 3) {
                        if (is != null) {
                        	log.error("-----开始保存图片至图片存储：JabbarApiManager.java：677---"+ device+"---"+savePath);
                            sdManager.write(device, savePath, is);
                            temp = 4;
                            // 加抽图任务
                            InfoHelper.prepare4Extract(device, savePath);
                        } else {
                            try {
                                Thread.sleep(100);
                                temp++;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } finally {
                    ResourceMgr.closeQuietly(is);
                    ResourceMgr.closeQuietly(islen);
                }

                String newPicNames = device.getDeviceName() + ";" + savePath;

                if (first && i == 0) {
                    //第一张图片设置为标题图，设置稿件为有图稿件
                    attachementList.addAll(setTitlePic(article, newPicNames));
                    article.set("SYS_HAVEATTACH",1);
                    first = false;
                }
                atta.set("att_articleID", article.getDocID()); // 所属稿件
                atta.set("att_articleLibID", article.getDocLibID());
                atta.set("att_path", newPicNames);
                atta.set("att_type", 0); // 0:正文图片;1:正文视频
                atta.set("oldPicContent", oldPicContent);
                attachementList.add(atta);
                // 替换正文中的图片链接
                oldPicName = escapeExprSpecialWord(oldPicName);
                content = content.replaceAll(oldPicName, "../../xy/image.do?path=" + newPicNames);
            }
        }
        article.set("a_content", content);
    }

    private List<com.founder.e5.doc.Document> setTitlePic(com.founder.e5.doc.Document article, String newPicNames) throws E5Exception {
        List<com.founder.e5.doc.Document> titlePics = new ArrayList<>();
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        // 第一张图片设置为标题图
        int attLibID = LibHelper.getLibIDByOtherLib(DocTypes.ATTACHMENT.typeID(), article.getDocLibID());
        article.set("a_picBig", newPicNames);
        com.founder.e5.doc.Document _atta = docManager.newDocument(attLibID,
                InfoHelper.getNextDocID(DocTypes.ATTACHMENT.typeID()));
        _atta.set("att_articleID", article.getDocID()); // 所属稿件
        _atta.set("att_articleLibID", article.getDocLibID());
        _atta.set("att_path", newPicNames);
        _atta.set("att_type", 2); // 0:正文图片;1:正文视频 2,3,4 大中小
        titlePics.add(_atta);

        article.set("a_picMiddle", newPicNames);
        _atta = docManager.newDocument(attLibID, InfoHelper.getNextDocID(DocTypes.ATTACHMENT.typeID()));
        _atta.set("att_articleID", article.getDocID()); // 所属稿件
        _atta.set("att_articleLibID", article.getDocLibID());
        _atta.set("att_path", newPicNames);
        _atta.set("att_type", 3); // 0:正文图片;1:正文视频 2,3,4 大中小
        titlePics.add(_atta);

        article.set("a_picSmall", newPicNames);
        _atta = docManager.newDocument(attLibID, InfoHelper.getNextDocID(DocTypes.ATTACHMENT.typeID()));
        _atta.set("att_articleID", article.getDocID()); // 所属稿件
        _atta.set("att_articleLibID", article.getDocLibID());
        _atta.set("att_path", newPicNames);
        _atta.set("att_type", 4); // 0:正文图片;1:正文视频 2,3,4 大中小
        titlePics.add(_atta);
        return titlePics;
    }

    private int getDocLibIDByCh(int ch) {
        switch (ch) {
            case -1:
                return LibHelper.getOriginalLibID();
            case 0:
                return LibHelper.getArticleLibID();
            default:
                return LibHelper.getArticleAppLibID();
        }
    }

    private String escapeExprSpecialWord(String keyword) {
        if (!StringUtils.isBlank(keyword)) {
            String[] fbsArr = {"\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|"};
            for (String key : fbsArr) {
                if (keyword.contains(key)) {
                    keyword = keyword.replace(key, "\\" + key);
                }
            }
        }
        return keyword;
    }

    public String publish(HttpServletRequest request) throws E5Exception, IOException {

        // 生成稿件
        List<com.founder.e5.doc.Document> attachmentList = new ArrayList<>();
        com.founder.e5.doc.Document article = assembleNewArticle(request);
        saveAttachment(request, attachmentList, article);
        // 设置相关发布信息
        article = prepublish(article, request);
        // CheckType 0定时发布,1发布,3只入库不发布
        int CheckType = StringValueUtils.getInt(request.getParameter("CheckType"), 0);
        // 保存并发布稿件
        String result = null;
        try {
            // 保存稿件
            result = save(article, attachmentList);
            if (result == null) {
                if (CheckType != 3) {
                    // 发布稿件
                    LogHelper.writeLog(article.getDocLibID(), article.getDocID(), ProcHelper.getUser(request),
                            "天钩抓取并发布", "");
                    PublishTrigger.article(article);
                } else
                    LogHelper.writeLog(article.getDocLibID(), article.getDocID(), ProcHelper.getUser(request), "天钩抓取",
                            "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result == null ? "发布成功" : "发布失败:" + result;

    }

    private com.founder.e5.doc.Document prepublish(com.founder.e5.doc.Document article, HttpServletRequest request)
            throws E5Exception {

        // 按主栏目设置流程。
        articleManager.setFlowByColumn(article);
        int CheckType = StringValueUtils.getInt(request.getParameter("CheckType"), 0);
        if (CheckType != 3) {
            articleManager.tryPublish(article, article.getInt("a_columnID"), true);
        } else {
            article.set("a_status", Article.STATUS_PUB_NOT);
        }
        articleManager.setColumnAll(article);
        // 设置发布时间
        String formatDate = request.getParameter("SetPubTime");
        if (!StringUtils.isBlank(formatDate) && CheckType == 0) {
            Timestamp pubTime = new Timestamp(DateUtils.parse(formatDate, "yyyy-MM-dd HH:mm").getTime());
            article.set("a_pubTime", pubTime);
        } else
            article.set("a_pubTime", DateUtils.getTimestamp());

        // 处理定时发布,若稿件已经设置为发布，并且发布时间置后，则改成定时发布状态
        articleManager.changeTimedPublish(article);

        article.set("a_linkTitle", article.getTopic()); // 链接标题
        Timestamp createTime = DateUtils.getTimestamp();
        //天钩抓取的Nsdate时间不准确，设置为当前服务器时间
        /*String Nsdate = request.getParameter("Nsdate");
		if (!StringUtils.isBlank(Nsdate)) {
			String date0 = "0000-00-00 00:00:00";
			String dateform = "yyyy-MM-dd HH:mm:ss";
			if (Nsdate.contains("年") || Nsdate.contains("月") || Nsdate.contains("日")) {
				date0 = "2016年01月01日 00:00:00";
				dateform = "yyyy年MM月dd日 HH:mm:ss";
			} else if (Nsdate.contains("/")) {
				date0 = "2016/01/01 00:00:00";
				dateform = "yyyy/MM/dd HH:mm:ss";
			}
			Nsdate = Nsdate.concat(date0.substring(Nsdate.length()));
			createTime = new Timestamp(DateUtils.parse(Nsdate, dateform).getTime());
		}
*/
        article.setCreated(createTime);

        // 设置顺序
        double order = articleManager.getNewOrder(article);
        article.set("a_order", order);

        return article;
    }

	public void saveAttachmentPic(
			List<com.founder.e5.doc.Document> attachmentList,
			com.founder.e5.doc.Document articleDoc) {

		if (attachmentList != null && attachmentList.size() > 0) {
			int docLibID = LibHelper.getLibIDByOtherLib(
					DocTypes.ATTACHMENT.typeID(), articleDoc.getDocLibID());
			int articleType = articleDoc.getInt("a_type");

			long idStart;
			try {
				idStart = EUID.getID("DocID" + DocTypes.ATTACHMENT.typeID(), attachmentList.size());
			
				DocumentManager docManager = DocumentManagerFactory.getInstance();
				for (int i = 0; i < attachmentList.size(); i++) {
					if("0".equals(attachmentList.get(i).getString("att_type")))
					addNewPic(articleDoc, attachmentList.get(i), docManager, docLibID,
								articleType, i, idStart++);
				}
			} catch (E5Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void addNewPic(com.founder.e5.doc.Document articleDoc,
			com.founder.e5.doc.Document picInfo, DocumentManager docManager,
			int attLibID, int articleType, int i, long attID) {
		// 组装附件表的Document对象
		com.founder.e5.doc.Document attach;
		try {
			attach = docManager.newDocument(attLibID, attID);
			attach.set("att_articleID", articleDoc.getDocID()); // 所属稿件
			attach.set("att_articleLibID", articleDoc.getDocLibID());
			attach.set("att_path", picInfo.get("att_path"));
			attach.set("att_type", 0); // 0:正文图片;1:正文视频
			attach.set("att_order", i);
	
			// 若是外网图片，则把url字段也填好
			if (picInfo.get("att_path") != null
					&& ((String) picInfo.get("att_path")).toLowerCase().startsWith("http")) {
				attach.set("att_url", picInfo.get("att_path"));
				attach.set("att_urlPad", picInfo.get("att_path"));
			}
	
			// 若是组图稿，则图片附件有说明、顺序、是否索引的属性
			if (articleType == Article.TYPE_PIC
					|| articleType == Article.TYPE_PANORAMA) {
				attach.set("att_content", picInfo.get("oldPicContent"));
				attach.set("att_indexed", 0);
			}
	
			docManager.save(attach);
		} catch (E5Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}