package com.founder.xy.api.dw;

import java.io.File;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.DomHelper;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.xy.commons.InfoHelper;

import org.springframework.stereotype.Service;

import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.DBType;
import com.founder.e5.db.IResultSet;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.web.WebUtil;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.system.site.Site;

/**
 * Dreamwaver插件接口的处理器
 */
@Service
public class DwApiManager {
	private static final String FIELD_T_NAME = "t_name";
	private static final String FIELD_T_CHANNEL = "t_channel";
	private static final String FIELD_T_TYPE = "t_type";
	private static final String FIELD_T_DESCRIPTION = "t_description";
	private static final String FIELD_T_FILETYPE = "t_fileType";
	private static final String FIELD_T_SITEID = "t_siteID";
	private static final String FIELD_T_GROUPID = "t_groupID";
	
	/**根据模板组ID、站点ID查模板列表
	 * @param request */
	@SuppressWarnings({ "unused", "static-access" })
	public Document[] getTemplates(int siteID, int groupID, HttpServletRequest request) throws Exception {
		int tplLibID = LibHelper.getTemplateLibID();
		String beginTimestr = WebUtil.get(request, "beginTime"); 
		String endTimestr = WebUtil.get(request, "endTime"); 
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 
		Date beginTime = null;
		Date endTime = null;
		if(!StringUtils.isBlank(beginTimestr))
			beginTime = format.parse(beginTimestr);
		if(!StringUtils.isBlank(endTimestr)){
			endTime = format.parse(endTimestr);
			if(isOracle()){
				Calendar   calendar   =   new   GregorianCalendar(); 
			    calendar.setTime(endTime);
			    calendar.add(calendar.DATE,1);
			    endTime=calendar.getTime();
			}
		}
		String sql = " t_siteID=? and t_groupID=? and SYS_DELETEFLAG=0";
		Object[] obj = null;
		obj = new Object[] {siteID, groupID};

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		if(siteID != -1){
			if(beginTime != null || endTime != null){
				if(beginTime != null && endTime != null){
					if(isOracle())
						sql = sql + " and SYS_CREATED between ? and ? ";
					else
						sql = sql + " and SYS_CREATED between ? and DATE_ADD( ? ,INTERVAL 1 DAY) ";
					obj = new Object[] {siteID, groupID, beginTime, endTime};
				}else if(beginTime != null){
					sql = sql + " and SYS_CREATED >= ?  ";
					obj = new Object[] {siteID, groupID, beginTime};
				}else if(beginTime != null){
					if(isOracle())
						sql = sql + " and SYS_CREATED <= ?  ";
					else
						sql = sql + " and SYS_CREATED <= DATE_ADD( ? ,INTERVAL 1 DAY)  ";
				obj = new Object[] {siteID, groupID, endTime};
				}
			}
		Document[] tpls = docManager.find(tplLibID,	sql, obj);
		return tpls;
		}else{

			Document[] tpls = docManager.find(tplLibID,
					"t_groupID=? and SYS_DELETEFLAG=0", new Object[] { groupID });
			return tpls;
			
		}
	}
	
	private boolean isOracle() {
		String dbType = DomHelper.getDBType();
        return dbType.equals(DBType.ORACLE);
	}

	/**根据区块组ID、站点ID查询列表*/
	public Document[] getBlocks(int siteID, int groupID) throws E5Exception {
		int blockLibID = LibHelper.getBlockLibID();

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] blocks = docManager.find(blockLibID,
				"b_siteID=? and b_groupID = ? and SYS_DELETEFLAG=0", new Object[] { siteID,
				groupID });
		return blocks;
	}
	public Document[] getResources(int siteID, int groupID) throws E5Exception {
		int resourceLibID = LibHelper.getResourceLibID();
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] resources = docManager.find(resourceLibID,
				"res_siteID=? and res_groupID = ? and SYS_DELETEFLAG=0", new Object[] { siteID,
				groupID });
		return resources;
	
	}
	/**处理dw登录返回数据,返回xml字符串信息*/
	public String getSiteInfoXML(String msg, List<Site> sites, List<Integer> siteRoles){
		StringBuffer result = new StringBuffer(2550);
		result.append("\t\n");
		result.append("<登录>\t\n");
		result.append("<信息 msg=\"").append(msg).append("\" type=\"内容管理\" />\t\n");
		if (sites != null ){
			result.append("<站点 id=\"0\" name=\"稿件类型管理\" />\t\n");
			
			for (int i = 0; i < sites.size(); i++) {
				Site site = sites.get(i);
				int roleID = siteRoles.get(i);
				if (roleID > 0) {
					String siteName = site.getName();
					if(!StringUtils.isBlank(siteName))
						siteName = siteName.replaceAll("\"", "&#34;");
					result.append("<站点 id=\"").append(site.getId());
					result.append("\" name=\"").append(siteName);
					result.append("\"/>\t\n");
				}
			}
			result.append("<配置>\t\n");
			result.append("<外联文件方式>0</外联文件方式>\t\n");
			String maxPicSize = InfoHelper.getConfig("写稿","上传图片大小限制");
			maxPicSize = StringUtils.isBlank(maxPicSize)?"-1":maxPicSize;
			result.append("<上传图片大小限制>");
			result.append(maxPicSize);
			result.append("</上传图片大小限制>\t\n");
			result.append("</配置>\t\n");
		}
		result.append("</登录>");
		return result.toString();
	}
	
	/**将模板数据 组织成xml字符串*/
	/*
	 * 模板属性说明：
	 * id : 模板ID
	 * name : 模板名称
	 * path : 模板路径
	 * type : 模板类型  -- 取值含义：0：栏目模板/1:文章模板
	 * channel : 渠道-- 取值含义0：网站/1：触屏
	 */
	public String getTemplateXML(Document template,String siteUrl){
		StringBuffer result = new StringBuffer(255);
		
		long tplId = template.getDocID();
		int doclibID = (int)template.get("sys_doclibid");
		int tplType = (int)template.get("t_type");
		int tplChannel = (int)template.get("t_channel");
		String tplName = template.getString("t_name");
		String tplPath = template.getString("t_file");
		
		if(!StringUtils.isBlank(tplName))
			tplName = tplName.replaceAll("\"", "&#34;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
		System.out.println(tplName);
		if(!StringUtils.isBlank(tplPath))
			tplPath = tplPath.replaceAll("\"", "&#34;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");

		Timestamp createTime = (Timestamp) template.get("sys_created");
		
		result.append("<template id=\"").append(tplId);
		result.append("\" doclibid=\"").append(doclibID);
		result.append("\" name=\"").append(tplName);
		result.append("\" path=\"").append(tplPath);
		result.append("\" type=\"").append(tplType);
		result.append("\" channel=\"").append(tplChannel);
		result.append("\" createTime=\"").append(createTime);
		result.append("\" resUrl=\"").append(siteUrl+File.separator + "templateRes" + File.separator+DateUtils.format(createTime, "yyyyMM/dd/")+tplId);
		result.append("\" />\t\n");
		
		return result.toString();
	}

	/**将区块信息组织成xml字符串*/
	/*
	 * 区块属性说明：
	 * id : 区块ID
	 * name : 区块名称
	 * type : 区块类型 -- 取值含义 0：自动/1:手动
	 * channel : 渠道 -- 取值含义0：网站/1：触屏
	 * outputType : 输出类型 --取值html:html/json:json/xml:xml
	 * pubdir : 发布目录
	 * createTime : 创建时间
	 */
	public String getBlockXML(Document block){
		StringBuffer result = new StringBuffer(255);
		
		long docID = block.getDocID();
		String bName = block.getString("b_name");
		int bType = (int)block.get("b_type");
		int bChannel = (int)block.get("b_channel");
		String bDir = block.getString("b_dir");
		String outType = block.getString("b_format");
		
		Object createTime = block.get("sys_created");
		
		if(!StringUtils.isBlank(bName))
			bName = bName.replaceAll("\"", "&#34;");
		
		result.append("<block id=\"").append(docID);
		result.append("\" name=\"").append(bName);
		result.append("\" type=\"").append(bType);
		result.append("\" channel=\"").append(bChannel);
		result.append("\" outputType=\"").append(outType);
		result.append("\" pubdir=\"").append(bDir);
		result.append("\" createTime=\"").append(createTime);
		result.append("\" />\t\n");
		
		return result.toString();
	}
	
	/**将公共资源信息组织成xml字符串*/
	/*
	 * 资源属性说明：
	 * id : 资源ID
	 * name : 资源名称
	 * type : 资源类型 -- 取值含义0：文件/1:栏目图标/3:来源图标
	 * docType : 文件类型 -- 取值含义 html:html/js:js/图片:图片/其它：其它
	 * pubPath : 发布路径 
	 */
	public String getResourceXML(Document resource){
		StringBuffer result = new StringBuffer(255);
		
		long docID = resource.getDocID();
		String resName = resource.getString("res_name");
		int resType = (int)resource.get("res_type");
		String resDocType = resource.getString("res_fileType");
		String resPubPath = resource.getString("res_dir");
		String resFileName = resource.getString("res_fileName");

		Object createTime = resource.get("sys_created");
		
		result.append("<resource id=\"").append(docID);
		result.append("\" name=\"").append(resName);
		result.append("\" type=\"").append(resType);
		result.append("\" docType=\"").append(resDocType);
		result.append("\" pubPath=\"").append(resPubPath+"/"+resFileName);
		result.append("\" createTime=\"").append(createTime);
		result.append("\" />\t\n");
		
		
		return result.toString();
	}
	
	public Document parse(HttpServletRequest request, Document doc) throws Exception{
		
		String[] params = {FIELD_T_NAME, FIELD_T_CHANNEL, FIELD_T_TYPE, FIELD_T_DESCRIPTION, FIELD_T_FILETYPE, FIELD_T_SITEID, FIELD_T_GROUPID};
		for(int t=0; t<params.length; ++t){
			String value = request.getParameter(params[t]);
			if(value==null){
				value = "";
			}
			String decodeVal = value;
			if(FIELD_T_DESCRIPTION.equals(params[t]) || FIELD_T_NAME.equals(params[t])){
				decodeVal = URLDecoder.decode(value, "UTF-8");
			}
			doc.set(params[t], decodeVal);
		}
		return doc;
		
	}

	public Document[] getTemplatesForPage(int siteID, int groupID,
			HttpServletRequest request) throws Exception {
		int tplLibID = LibHelper.getTemplateLibID();

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		if(siteID != -1){
		Document[] tpls = docManager.find(tplLibID,	"t_siteID=? and t_groupID=? and SYS_DELETEFLAG=0", new Object[] {siteID, groupID});
		return tpls;
		}else{
			Document[] tpls = docManager.find(tplLibID,
					"t_groupID=? and SYS_DELETEFLAG=0", new Object[] { groupID });
			return tpls;
			
		}
	}

	public Document[] getTemplatesForPage(int siteID, int groupID,
			HttpServletRequest request, int type) throws Exception {
		int tplLibID = LibHelper.getTemplateLibID();

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		if(siteID != -1){
		Document[] tpls = docManager.find(tplLibID,	"t_siteID=? and t_groupID=? and t_type=? and SYS_DELETEFLAG=0", new Object[] {siteID, groupID, type});
		return tpls;
		}else{
			Document[] tpls = docManager.find(tplLibID,
					"t_groupID=? and t_type=? and SYS_DELETEFLAG=0", new Object[] { groupID, type});
			return tpls;
			
		}
	}

	public StringBuffer getCountType(int siteID, int groupID, HttpServletRequest request) {
		int tplLibID = LibHelper.getTemplateLibID();
		StringBuffer templateResult = new StringBuffer(1024);
		String sql = "select t_type as type, count(t_type) as count from xy_template where t_siteID=? and t_groupID=? and SYS_DELETEFLAG=0 group by t_type";
		Object[] obj = new Object[] {siteID, groupID};
		templateResult = queryType(tplLibID, sql, obj, String.valueOf(siteID));
		return templateResult;
	}

	private StringBuffer queryType(int tplLibID, String sql, Object[] obj,
			String siteID) {
		DBSession db = null;
		IResultSet rs = null;
		StringBuffer templateResult = new StringBuffer(1024);
		try {
			db = InfoHelper.getDBSession(tplLibID);
			rs = db.executeQuery(sql, obj);
			while (rs != null && rs.next()) {
				int type = rs.getInt("type");
				int count = rs.getInt("count");
				templateResult.append("<template type=\"").append(type).append("\" count=\"").append(count).append("\" >\t\n").append("</template>\t\n");
				
			}

		} catch (Exception e) {
			System.out.println("TemplateService.queryArticle exception:"
					+ e.getLocalizedMessage() + ".SQL:" + sql);
			if (obj != null) {
				for (int i = 0; i < obj.length; i++) {
					System.out.println("param" + i + ":" + obj[i]);
				}
			}
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(db);
		}
		return templateResult;
	}
}
