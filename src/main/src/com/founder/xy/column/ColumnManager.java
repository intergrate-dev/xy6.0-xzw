package com.founder.xy.column;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.flow.FlowNode;
import com.founder.e5.flow.FlowReader;
import com.founder.e5.permission.FlowPermissionReader;
import com.founder.e5.sys.StorageDeviceManager;
import com.founder.e5.sys.SysFactory;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.config.TabHelper;
import com.founder.xy.jms.PublishTrigger;
import com.founder.xy.jms.data.DocIDMsg;
import com.founder.xy.system.site.SiteUserManager;
import com.founder.xy.template.parser.TemplateParser;

/**
 * 栏目管理器
 * 
 * @author Gong Lijie
 */
@Component
public class ColumnManager {
	static final char SEPARATOR = '~';

	@Autowired
	private SiteUserManager siteUserManager;
	@Autowired
	private TemplateParser templateParser;
	@Autowired
	private ColumnReader colReader;

	/**
	 * 根据栏目ID获得栏目对象
	 */
	public Document get(int colLibID, long colID) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		return docManager.get(colLibID, colID);
	}

	/**
	 * 按栏目名称查找。用于栏目树上的查找动作
	 */
	public Document[] find(int colLibID, int siteID, String name, int channelType,boolean flag) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		System.out.println("%" + name + "%");
		if(flag==false){
			Document[] cols = docManager.find(colLibID,
					"(col_name like ? or col_pinyin like ?) and col_siteID=? and col_channel=? and SYS_DELETEFLAG=0", 
					new Object[] {"%" + name + "%", "%" + name + "%", siteID, channelType});
			return cols;
		}else{
			int sys_documentId=Integer.parseInt(name);
			Document[] cols = docManager.find(colLibID,"(SYS_DOCUMENTID like ? or (col_name like ? or col_pinyin like ?)) and col_siteID=? and col_channel=? and SYS_DELETEFLAG=0  ",
				new Object[] {sys_documentId+"%", "%" + name + "%", "%" + name + "%",  siteID, channelType });
			return cols;
		}
	}

	/**
	 * 取栏目的子栏目
	 * 
	 * @param parentID 栏目ID
	 */
	public Document[] getSub(int colLibID, long parentID) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] cols = docManager.find(colLibID,
					"col_parentID=? and SYS_DELETEFLAG=0 order by col_displayOrder",
					new Object[] { parentID });
		return cols;
	}
	
	/**
	 * 按站点和渠道类型取根栏目
	 * @param siteID 站点ID
	 * @param channelType 渠道类型：0——Web版，1——App版
	 */
	public Document[] getRoot(int colLibID, int siteID, int channelType) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();

		Document[] cols = docManager.find(colLibID,
				"col_siteID=? and col_parentID=0 and col_channel=? and SYS_DELETEFLAG=0 order by col_displayOrder",
				new Object[] { siteID, channelType });
		return cols;
	}
	
	/**
	 * 取用户的可管理的栏目
	 */
	public Document[] getAdminColumns(int colLibID, int userID, int siteID, int channelType, int roleID) throws E5Exception {
		//若是移动版，则可管理栏目的type是5
		int type = channelType == 0 ? 1 : 5;
		
		int urLibID = LibHelper.getLibIDByOtherLib(DocTypes.USERREL.typeID(), colLibID);
		String sIDs = siteUserManager.getRelated(urLibID, userID, siteID, type);
		long[] ids = StringUtils.getLongArray(sIDs);

		//取出按角色设置的栏目权限，准备合并
		String sourceType = getSourceType(siteID,channelType,"Admin");
		long[] roleColIDs = TabHelper.getColIDsByRole(roleID,sourceType);
		
		//合并
		ids = unite(ids,roleColIDs);
		
		return getColumns(colLibID, ids);
	}

	/**
	 * 取用户的可操作的栏目
	 */
	public Document[] getOpColumns(int colLibID, int userID, int siteID, int channelType,int roleID) throws E5Exception {
		//若是移动版，则可操作栏目的type是4
		int type = channelType == 0 ? 0 : 4;
		
		int userLibID = LibHelper.getLibIDByOtherLib(DocTypes.USERREL.typeID(), colLibID);
		String sIDs = siteUserManager.getRelated(userLibID, userID, siteID, type);
		long[] ids = StringUtils.getLongArray(sIDs);
		
		//取出按角色设置的栏目权限，准备合并
		String sourceType = getSourceType(siteID,channelType,"Op");
		long[] roleColIDs = TabHelper.getColIDsByRole(roleID,sourceType);
		
		//合并
		ids = unite(ids,roleColIDs);
		
		return getColumns(colLibID, ids);
	}

	//两个long数组取并集
	private long[] unite(long[] data1,long[] data2){
		Set<Long> set = new HashSet<>();
		for (int i = 0; data1 != null && i < data1.length; i++) {
			set.add(data1[i]);
		}
		for (int i = 0; data2 != null && i < data2.length; i++) {
			set.add(data2[i]);
		}

		long[] result = new long[set.size()];
		int j = 0;
		for (long i : set) {
			result[j] = i;
			j++;
		}
		return result;
	}

	private String getSourceType(int siteID, int channelType, String type) {
		String result = "Column"+type;
		switch (channelType){
			case 0:
				return result+"Web"+siteID;
			default:
				return result+"App"+siteID;
		}
	}



	/**
	 * 为栏目表单补足属性。
	 * 创建时：父栏目的childCount+1，自身的级联ID和级联名称补全。 
	 * 修改时：若名称有变化，要修改子栏目的所有级联名 强事务控制
	 */
	public void fill4Form(int colLibID, boolean isNew, long colID, String colName) throws E5Exception {
		Document doc = get(colLibID, colID);
		if (!StringUtils.isBlank(colName)){
		    doc.set("col_name",colName);
		    if(colName.length() > 20){
		    	colName = colName.substring(0, 20);
		    }
		    String pinyin = Pinyin4jUtil.getAllPinYin(colName);
		    if(pinyin != null && pinyin.length() > 1000){
				doc.set("col_pinyin",pinyin.substring(0,1000));
			} else {
				doc.set("col_pinyin",pinyin);
			}
        }
		//设置最后修改时间
		doc.set("SYS_LASTMODIFIED", DateUtils.getTimestamp());
		
		Document parent = null;
		DocumentManager docMgr = DocumentManagerFactory.getInstance();

		int parentID = doc.getInt("col_parentID");
		if (isNew && parentID > 0) {
			parent = docMgr.get(doc.getDocLibID(), parentID);
		}

		DBSession conn = null;
		try {
			conn = Context.getDBSession();
			conn.beginTransaction();

			if (isNew) {
				if (parentID > 0) {
					//父栏目childCount+1，这里有一点风险，若并发有新增子栏目的动作，则childCount不正确
					parent.set("col_childCount", parent.getInt("col_childCount") + 1);
					docMgr.save(parent, conn);

					//设置级联ID和级联名称
					doc.set("col_cascadeID",
							parent.getString("col_cascadeID") + SEPARATOR + doc.getDocID());
					doc.set("col_cascadeName", parent.getString("col_cascadeName") + SEPARATOR
							+ doc.getString("col_name"));
				} else {
					doc.set("col_cascadeID", doc.getDocID());
					doc.set("col_cascadeName", doc.getString("col_name"));
				}
				if(doc.getInt("col_channel") ==1 ) doc.set("col_appShow",1); //APP栏目 默认显示字段设为1,方便操作
				int order = getMaxSubOrder(doc.getDocLibID(), parentID, doc.getInt("col_siteID"));
				doc.set("col_displayOrder", ++order);//不能简单使用DocID作为排序号，取最大序号
			} else {
				changeCascadeName(doc, conn); //非新建的栏目，只要检查是否名称有变化，若有则需要修改子栏目的级联名称
			}
			docMgr.save(doc, conn);
			
			conn.commitTransaction();
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			throw new E5Exception(e);
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
	}
	
	@SuppressWarnings("unused")
	private void changeArticleColumn(Document doc, DBSession conn) throws E5Exception {

		//此处需修改全部稿件的栏目名称
		String sqlChangeArticleColumn ="update xy_article set a_column=? where a_columnID=?";
		InfoHelper.executeUpdate(sqlChangeArticleColumn,
				new Object[] { doc.getString("col_cascadeName"), doc.getDocID() },conn);
	}

	/**
	 * 只用于新建栏目过程中异常时再删掉刚新建的栏目
	 */
	public void undoSave(int colLibID, long colID) {
		try {
			DocumentManager docMgr = DocumentManagerFactory.getInstance();
			docMgr.delete(colLibID, colID);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 栏目删除：栏目和子栏目做删除标记、父栏目的childCount - 1
	 */
	public void delete(int colLibID, long colID) throws E5Exception {
		deleteFlag(colLibID, colID, 1);
	}

	/**
	 * 设过时栏目：栏目和子栏目做删除标记2、父栏目的childCount - 1
	 */
	public void outOfDate(int colLibID, long colID) throws E5Exception {
		deleteFlag(colLibID, colID, 2);
	}

	/**
	 * 已删除栏目的恢复：栏目和子栏目去掉删除标记、父栏目的childCount + 1
	 */
	public void restore(int colLibID, long colID) throws E5Exception {
		deleteFlag(colLibID, colID, 0);
	}

	/**
	 * 栏目复制：复制栏目和子栏目、父栏目的childCount + 1
	 * @return 复制栏目
	 */
	public Document copy(int colLibID, long colID, String newColName, Boolean copyTemp, String[] newTemName) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document oldColumn = docManager.get(colLibID, colID);

		int parentID = oldColumn.getInt("col_parentID");
		Document parent = (parentID <= 0) ? null : docManager.get(colLibID, parentID);
		//对第一个栏目，改名，改排序号。父栏目的子个数+1
		int order = getMaxSubOrder(colLibID, parentID, oldColumn.getInt("col_siteID"));
		if (StringUtils.isBlank(newColName))
			newColName = "（复制）" + oldColumn.getString("col_name");

		HashMap<Long, Long> idMap = new HashMap<>();
		List<Document> newCols = new ArrayList<>();
		//先改下栏目名称，否则级联栏目名称还是旧的
		oldColumn.set("col_name", newColName);

		//取出所有要复制的栏目
		getCopyColumns(colLibID, oldColumn, parent, newCols,idMap);

		Document newColumn = newCols.get(0);
		newColumn.set("col_name", newColName);
		newColumn.set("col_displayOrder", ++order);//不能简单使用DocID作为排序号，取最大序号
		if (parent != null) {
			int childCount = parent.getInt("col_childCount") + 1;
			parent.set("col_childCount", childCount);
			newColumn.set("col_cascadeName", parent.getString("col_cascadeName") + SEPARATOR + newColName);
		} else {
			newColumn.set("col_cascadeName", newColName);
		}
		//处理模板
		if(copyTemp) {
			String templates[] = {"template", "templatePAD"};
			int temDocLibID = LibHelper.getTemplateLibID();
			for (int i = 0; i < templates.length; i++) {
				Document oldTem = docManager.get(temDocLibID, oldColumn.getLong("col_" + templates[i] + "_ID"));
				if (oldTem != null) {
					Document newTem = copyTemplate(oldTem, idMap,newTemName[i]);
					//设置栏目模板
					newColumn.set("col_" + templates[i] + "_ID", newTem.getDocID());
					newColumn.set("col_" + templates[i], newTem.getString("t_name"));
				}
			}
		}
		Timestamp now = DateUtils.getTimestamp();
		
		DBSession db = Context.getDBSession();
		try {
			db.beginTransaction();
			for (Document col : newCols) {
				col.setCreated(now);
				col.setLastmodified(now);
				
				docManager.save(col, db);
			}
			if (parent != null) {
				docManager.save(parent);
			}
			db.commitTransaction();
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(db);
			throw new E5Exception("[栏目复制]", e);
		} finally {
			ResourceMgr.closeQuietly(db);
		}

		return newColumn;
	}
	protected Document copyTemplate(Document oldTem, HashMap<Long, Long> idMap, String newTemName){
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		String oldTemPath = oldTem.getString("t_file");
		Document newTem = null;
		try {
			//读原模板内容
			int pos = oldTemPath.indexOf(";");
			//类型
			String deviceName = oldTemPath.substring(0, pos);
			//保存路径
			String savePath = oldTemPath.substring(pos + 1);
			StorageDeviceManager sdManager = SysFactory.getStorageDeviceManager();
			//为防止存储设备是编码的，先解码
			deviceName = URLDecoder.decode(deviceName, "UTF-8");
			InputStream in = sdManager.read(deviceName, savePath);
			String templateContent = IOUtils.toString(in,"UTF-8");

			//修改模板内容
			String newTemStr = chengeID(templateContent,idMap);

			long newTemID = InfoHelper.getNextDocID(DocTypes.TEMPLATE.typeID());
			String newTemPath = DateUtils.format("yyyyMM/dd/") + newTemID + "/" + newTemID + oldTemPath.substring(oldTemPath.lastIndexOf("."));
			

			//保存模板文件
			sdManager.write(deviceName,newTemPath,  new ByteArrayInputStream(newTemStr.getBytes("UTF-8")));

			//数据库中复制一个模板
			 newTem = docManager.newDocument(oldTem, oldTem.getDocLibID(), newTemID);

			 //修改新模板的不同属性
			Timestamp now = DateUtils.getTimestamp();
			newTem.set("t_file",deviceName+";"+newTemPath);
			if(!StringUtils.isBlank(newTemName)) {
				newTem.set("t_name", newTemName);
			}
			else
				newTem.set("t_name","（复制"+newTemID+"）"+oldTem.getString("t_name"));
			newTem.setCreated(now);
			newTem.setLastmodified(now);
			docManager.save(newTem);
			String reliPath = DateUtils.format("yyyyMM/dd/") + newTemID  ;
			String siteId = newTem.getString("t_siteID");
			int siteID = Integer.parseInt(siteId);
			String[] siteDir = readSiteInfo(siteID);
			
			//复制模板的资源文件
			try {
				copyResourceFile(oldTem, newTem);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			//解析新模板
			templateParser.parse(newTem, siteDir, reliPath);
			//通知发布服务
			PublishTrigger.otherData(newTem.getDocLibID(), newTem.getDocID(), DocIDMsg.TYPE_TEMPLATE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (E5Exception e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return newTem;
	}

	private void copyResourceFile(Document oldDoc, Document newDoc) throws ParseException, IOException {
		String tFileNew = newDoc.getString("T_FILE");
		String tFileOld = oldDoc.getString("T_FILE");
		String siteId = newDoc.getString("t_siteID");
		int siteID = Integer.parseInt(siteId);
		String[] siteDir = readSiteInfo(siteID);
		long docIDNew = newDoc.getDocID();
		long docIDOld = oldDoc.getDocID();
		Date dateNew = formatFileDate(tFileNew);
		Date dateOld = formatFileDate(tFileOld);
		String newresource = siteDir[1] + File.separator + "templateRes" + File.separator + DateUtils.format(dateNew, "yyyyMM/dd/") + docIDNew;
		String oldResource = siteDir[1] + File.separator + "templateRes" + File.separator + DateUtils.format(dateOld, "yyyyMM/dd/") + docIDOld;
		
		File oldResFile = new File(oldResource);
		if(oldResFile.exists() && oldResFile.listFiles().length>0)
			copyDir(oldResource, newresource);
		
	}

	private void copyDir(String oldPath, String newPath) throws IOException {
        File file = new File(oldPath);
        String[] filePath = file.list();
        
        if (!(new File(newPath)).exists()) {
            (new File(newPath)).mkdir();
        }
        for (int i = 0; i < filePath.length; i++) {
            if ((new File(oldPath + File.separator + filePath[i])).isDirectory()) {
                copyDir(oldPath  + File.separator  + filePath[i], newPath  + File.separator + filePath[i]);
            }
            if (new File(oldPath  + File.separator + filePath[i]).isFile()) {
                copyFile(oldPath + File.separator + filePath[i], newPath + File.separator + filePath[i]);
            }
        }
	}

	private void copyFile(String oldPath, String newPath) throws IOException {
		File oldFile = new File(oldPath);
        File file = new File(newPath);
        FileInputStream in = null;
        FileOutputStream out = null; 
		try {
		in = new FileInputStream(oldFile);
        out = new FileOutputStream(file);;

        byte[] buffer=new byte[2097152];
        
        while((in.read(buffer)) != -1){
            out.write(buffer);
        }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}finally{
			ResourceMgr.closeQuietly(in);
			ResourceMgr.closeQuietly(out);
		}
	}

	private String[] readSiteInfo(int siteID) {
		return InfoHelper.readSiteInfo(siteID);
	}

	private Date formatFileDate(String tFile) throws ParseException {
		String tplDate = tFile.substring(tFile.indexOf(";")+1, tFile.indexOf(";")+11).replaceAll("\\\\", "/");
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMM/dd/");//格式化模板日期路径  
		Date date = sdf.parse(tplDate);  
		return date;
	}

	protected String chengeID(String content,  HashMap<Long, Long> idMap) {
		StringBuffer sb = new StringBuffer();
		if (!StringUtils.isBlank(content)) {
			final Pattern PARSER_PATTERN =
					Pattern.compile("(<FOUNDER-XY[\\s]*?type=\"[\\s\\S]*?\"[\\s]*?data=\"[\\s\\S]*?\'columnid\':\\[)([\\s\\S]*?)(][\\s\\S]*?\">[\\s\\S]*?</FOUNDER-XY>)");
			Matcher contentMatcher = PARSER_PATTERN.matcher(content);
			while(contentMatcher.find()){
				String oldColStre = contentMatcher.group(2);
				if (!StringUtils.isBlank(oldColStre)) {
					long[] oldColID = StringUtils.getLongArray(oldColStre);
					for (int i = 0; i <oldColID.length ; i++) {
						if(idMap.containsKey(oldColID[i]))
							oldColID[i]=idMap.get(oldColID[i]);
					}
					String replacement = "$1"+ StringUtils.join(oldColID)+"$3";
					contentMatcher.appendReplacement(sb,replacement);
				}
			}
			contentMatcher.appendTail(sb);
		}
		return sb.toString();
	}



	private void getCopyColumns(int colLibID, Document oldColumn, Document parent,
								List<Document> list, HashMap<Long, Long> idMap) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		
		//根据旧栏目，复制一个新栏目。聚合栏目清空
		long newColID = InfoHelper.getNextDocID(DocTypes.COLUMN.typeID());
		Document newColumn = docManager.newDocument(oldColumn, colLibID, newColID);
		newColumn.set("col_aggregateIDs", "");
		newColumn.set("col_pubTime", null);
		newColumn.set("col_pubTimeColumn", null);
		idMap.put(oldColumn.getDocID(),newColID);
		//设置父ID、级联名和级联ID
		if (parent != null) {
			newColumn.set("col_parentID", parent.getDocID());
			newColumn.set("col_cascadeName", 
					parent.getString("col_cascadeName") + SEPARATOR + newColumn.getString("col_name"));
			newColumn.set("col_cascadeID", 
					parent.getString("col_cascadeID") + SEPARATOR + newColID);
		} else {
			newColumn.set("col_cascadeName", newColumn.getString("col_name"));
			newColumn.set("col_cascadeID", newColID);
		}
		list.add(newColumn);
		
		//复制子栏目
		Document[] subs = getSub(colLibID, oldColumn.getDocID());
		for (Document sub : subs) {
			getCopyColumns(colLibID, sub, newColumn, list, idMap);
		}
	}
	/**
	 * 检查一个栏目的父栏目是否被删除，用于栏目管理中的恢复
	 * 
	 * @throws E5Exception
	 */
	public boolean parentDeleted(int colLibID, long colID) throws E5Exception {
		Document cat = get(colLibID, colID);
		String casID = cat.getString("col_cascadeID");
		int[] path = StringUtils.getIntArray(casID, String.valueOf(SEPARATOR));

		for (int i = 0; i < path.length - 1; i++) {
			Document parent = get(colLibID, path[i]);
			if (parent.getDeleteFlag() > 0)
				return true;
		}
		return false;
	}

	/**
	 * @param srcID
	 * @param destID
	 * @param type
	 *            0--inner,作为子，1--prev,作为前一个，2--next,作为后一个
	 * @throws E5Exception
	 */
	public void move(int colLibID, long srcID, long destID, int type) throws E5Exception {
		if (type == 0) {
			asChild(colLibID, srcID, destID);
		} else {
			asBrother(colLibID, srcID, destID, type);
		}
	}

	/**
	 * 读出所有的栏目，用于栏目缓存刷新
	 */
	public Map<Long, Column> getColumnAll(int colLibID) throws E5Exception {
		Map<Long, Column> result = new HashMap<Long, Column>();
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] cols = docManager .find(colLibID,
						"SYS_DELETEFLAG=0 order by col_parentID,col_displayOrder",
						null);
		for (int i = 0; i < cols.length; i++) {
			Document doc = cols[i];
			Column col = new Column(doc);
			result.put(doc.getDocID(), col);
		}
		return result;
	}
	

	/**
	 * 读出每个栏目的子栏目，用于栏目缓存刷新
	 */
	public Map<Long, List<Column>> getColumnsOfParent(int colLibID) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] cols = docManager .find(colLibID,
						"SYS_DELETEFLAG=0 order by col_parentID,col_displayOrder",
						null);

		Map<Long, List<Column>> result = new HashMap<>();
		for (int i = 0; i < cols.length; i++) {
			Document doc = cols[i];
			long parentID = doc.getLong("col_parentID");
			Column col = new Column(doc);
			
			List<Column> brothers = result.get(parentID);
			if (brothers == null) {
				brothers = new ArrayList<Column>();
				result.put(parentID, brothers);
			}
			brothers.add(col);
		}
		return result;
	}
	
	/**
	 * 同步到子孙栏目
	 * @param colLibID
	 * @param colID
	 * @param syncType 同步类型（1：网站属性、2：触屏属性、3：App属性）
	 */
	public void syncChildren(int colLibID, long colID, int syncType) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document column = docManager.get(colLibID, colID);
		
		String casID = column.getString("col_cascadeID") + SEPARATOR + "%";
		
		String tableName = LibHelper.getLibByID(colLibID).getDocLibTable();
		String sql = null;
		Object[] params = null;
		switch (syncType) {
		case 1:
			sql = "col_template=?,col_template_ID=?,col_templateArticle=?,col_templateArticle_ID=?"
				+ ",col_templatePic=?,col_templatePic_ID=?,col_templateVideo=?,col_templateVideo_ID=?"
					+ ",col_pubRule=?,col_pubRule_ID=?";
			params = new Object[]{column.getString("col_template"), column.getInt("col_template_ID"),
					column.getString("col_templateArticle"), column.getInt("col_templateArticle_ID"),
					column.getString("col_templatePic"), column.getInt("col_templatePic_ID"),
					column.getString("col_templateVideo"), column.getInt("col_templateVideo_ID"),
					column.getString("col_pubRule"), column.getInt("col_pubRule_ID"),
					casID};
			break;
		case 2:
			sql = "col_templatePad=?,col_templatePad_ID=?,col_templateArticlePad=?,col_templateArticlePad_ID=?"
				+ ",col_templatePicPad=?,col_templatePicPad_ID=?,col_templateVideoPad=?,col_templateVideoPad_ID=?"
					+ ",col_pubRulePad=?,col_pubRulePad_ID=?";
			params = new Object[]{column.getString("col_templatePad"), column.getInt("col_templatePad_ID"),
					column.getString("col_templateArticlePad"), column.getInt("col_templateArticlePad_ID"),
					column.getString("col_templatePicPad"), column.getInt("col_templatePicPad_ID"),
					column.getString("col_templateVideoPad"), column.getInt("col_templateVideoPad_ID"),
					column.getString("col_pubRulePad"), column.getInt("col_pubRulePad_ID"),
					casID};
			break;
		case 3:
			sql = "col_appType=?,col_appType_ID=?,col_appStyle=?,col_appStyle_ID=?,col_topCount=?";
			params = new Object[]{
					column.getString("col_appType"), column.getInt("col_appType_ID"), 
					column.getString("col_appStyle"), column.getInt("col_appStyle_ID"), 
					column.getInt("col_topCount"),
					casID};
			break;
		default:
			break;
		}
		
		String sqlSync = "update " + tableName + " set " + sql + " where col_cascadeID like ?";
		InfoHelper.executeUpdate(colLibID, sqlSync, params);
	}

	/**
	 * 同步到子孙栏目
	 * @param colLibID
	 * @param colID
	 * @param siterule 是否同步发布规则
	 * @param columnpl 是否同步栏目模板
	 * @param articlepl 是否同步文章模板
	 * @param picpl 是否同步组图模板
	 * @param videopl 是否同步视频模板
	 * @param syncType 同步类型（1：网站属性、2：触屏属性、3：App属性）
	 * @param columntype 是否同步栏目类型（App属性）
	 * @param columnstyle 是否同步栏目样式（App属性）
	 * @param columntopcount 是否同步栏目头条个数（App属性）
	 */
	public void syncChildren(int colLibID, long colID, int syncType, 
			int siterule, int columnpl, int articlepl, int picpl, int videopl, int isShowInNav, 
			int columntype, int columnstyle, int columntopcount, int process,int sourcegroup,
			int searchName) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document column = docManager.get(colLibID, colID);
		
		String casID = column.getString("col_cascadeID") + SEPARATOR + "%";
		
		String tableName = LibHelper.getLibByID(colLibID).getDocLibTable();
		
		String sql = null;	
		Object[] params = null;
		StringBuilder sb = new StringBuilder();
		List<Object> list = new ArrayList<>();
		switch (syncType) {
			case 0:
				if(process==1){
					sb.append("col_flow=?,col_flow_ID=?,");
					list.add(column.getString("col_flow"));
					list.add(column.getInt("col_flow_ID"));
				}
				if(sourcegroup==1){
					sb.append("col_source=?,");
					list.add(column.getString("col_source"));
				}
				if(searchName==1){
					sb.append("col_searchName=?,");
					list.add(column.getString("col_searchName"));
				}
				sql = sb.toString().substring(0, sb.toString().length()-1);
				list.add(casID);
				params = list.toArray();
				break;
		case 1:
			if(siterule==1){
				sb.append("col_pubRule=?,col_pubRule_ID=?,");
				list.add(column.getString("col_pubRule"));
				list.add(column.getInt("col_pubRule_ID"));
			}
			if(columnpl==1){
				sb.append("col_template=?,col_template_ID=?,");
				list.add(column.getString("col_template"));
				list.add(column.getInt("col_template_ID"));
			}
			if(articlepl==1){
				sb.append("col_templateArticle=?,col_templateArticle_ID=?,");
				list.add(column.getString("col_templateArticle"));
				list.add(column.getInt("col_templateArticle_ID"));
			}
			if(picpl==1){
				sb.append("col_templatePic=?,col_templatePic_ID=?,");
				list.add(column.getString("col_templatePic"));
				list.add(column.getInt("col_templatePic_ID"));
			}
			if(videopl==1){
				sb.append("col_templateVideo=?,col_templateVideo_ID=?,");
				list.add(column.getString("col_templateVideo"));
				list.add(column.getInt("col_templateVideo_ID"));
			}
			if(isShowInNav==1){
				sb.append("col_isIncom=?,");
				list.add(column.getInt("col_isIncom"));
			}

			sql = sb.toString().substring(0, sb.toString().length()-1);
			list.add(casID);
			params = list.toArray();
			break;
		case 2:
			if(siterule==1){
				sb.append("col_pubRulePad=?,col_pubRulePad_ID=?,");
				list.add(column.getString("col_pubRulePad"));
				list.add(column.getInt("col_pubRulePad_ID"));
			}
			if(columnpl==1){
				sb.append("col_templatePad=?,col_templatePad_ID=?,");
				list.add(column.getString("col_templatePad"));
				list.add(column.getInt("col_templatePad_ID"));
			}
			if(articlepl==1){
				sb.append("col_templateArticlePad=?,col_templateArticlePad_ID=?,");
				list.add(column.getString("col_templateArticlePad"));
				list.add(column.getInt("col_templateArticlePad_ID"));
			}
			if(picpl==1){
				sb.append("col_templatePicPad=?,col_templatePicPad_ID=?,");
				list.add(column.getString("col_templatePicPad"));
				list.add(column.getInt("col_templatePicPad_ID"));
			}
			if(videopl==1){
				sb.append("col_templateVideoPad=?,col_templateVideoPad_ID=?,");
				list.add(column.getString("col_templateVideoPad"));
				list.add(column.getInt("col_templateVideoPad_ID"));
			}
			if(isShowInNav==1){
				sb.append("col_isIncomPad=?,");
				list.add(column.getInt("col_isIncomPad"));
			}

			sql = sb.toString().substring(0, sb.toString().length()-1);
			list.add(casID);
			params = list.toArray();
			break;
		case 3:
			/*
			sql = "col_appType=?,col_appType_ID=?,col_appStyle=?,col_appStyle_ID=?,col_topCount=?";
			params = new Object[]{
					column.getString("col_appType"), column.getInt("col_appType_ID"), 
					column.getString("col_appStyle"), column.getInt("col_appStyle_ID"), 
					column.getInt("col_topCount"),
					casID};
			break;
			*/
			if(columntype==1){
				sb.append("col_appType=?,col_appType_ID=?,");
				list.add(column.getString("col_appType"));
				list.add(column.getInt("col_appType_ID"));
			}
			if(columnstyle==1){
				sb.append("col_appStyle=?,col_appStyle_ID=?,");
				list.add(column.getString("col_appStyle"));
				list.add(column.getInt("col_appStyle_ID"));
			}
			if(columntopcount==1){
				sb.append("col_topCount=?,");
				list.add(column.getString("col_topCount"));
			}
			sql = sb.toString().substring(0, sb.toString().length()-1);
			list.add(casID);
			params = list.toArray();
			break;
		default:
			break;
		}
		
		String sqlSync = "update " + tableName + " set " + sql + " where col_cascadeID like ?";
		InfoHelper.executeUpdate(colLibID, sqlSync, params);
	}

	
	
    /**
     * 得到栏目的发布地址。
     * 返回两个字符串的数组，第一个表示网站版地址，第二个表示触屏版地址
     */
    public String[] getUrls(int colLibID, long colID) throws Exception {
		String[] result = new String[2];
		
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        Document col = docManager.get(colLibID, colID);
        
        if (col != null) {
            int ruleLibID = LibHelper.getLibIDByOtherLib(DocTypes.SITERULE.typeID(), colLibID);

            int ruleID = col.getInt("col_pubRule_ID");
            int padRuleID = col.getInt("col_pubRulePad_ID");
            
            result[0] = getColPath(ruleLibID, ruleID, colID, col.getString("col_fileName"), col.getInt("col_template_ID"));
            result[1] = getColPath(ruleLibID, padRuleID, colID, col.getString("col_fileNamePad"), col.getInt("col_templatePad_ID"));
        }
        return result;
    }

    /*
	 * 作为子栏目 a) 原父栏目的childCount - 1 b) 新父栏目的childCount + 1 c) 设置新的父ID、设置displayOrder为新父的maxChildOrder + 1，修改级联名称 d)
	 * 修改所有子的级联名称
	 */
	private void asChild(int colLibID, long srcID, long destID) throws E5Exception {
		AsChildInfo asChildInfo = asChildInfo(colLibID, srcID, destID);

		DBSession conn = null;
		try {
			conn = Context.getDBSession();
			conn.beginTransaction();

			asChildSave(asChildInfo, conn);

			conn.commitTransaction();
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			e.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
	}

	//组织一个栏目拖拽成子栏目时需要修改的数据项
	private AsChildInfo asChildInfo(int colLibID, long srcID, long destID) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();

		Document src = docManager.get(colLibID, srcID);
		Document dest = (destID == 0) ? null : docManager.get(colLibID, destID);

		int maxOrder = getMaxSubOrder(colLibID, destID, src.getInt("col_siteID"));
		int parentID = src.getInt("col_parentID");
		Document parent = null;
		if (parentID > 0) {
			parent = docManager.get(colLibID, parentID);
			parent.set("col_childCount", parent.getInt("col_childCount") - 1);
		}
		if (dest != null)
			dest.set("col_childCount", dest.getInt("col_childCount") + 1);

		src.set("col_parentID", destID);
		src.set("col_displayOrder", maxOrder + 1);

		//修改级联名称
		String oldCasName = src.getString("col_cascadeName");
		String newCasName = (dest != null) ? dest.getString("col_cascadeName") + SEPARATOR
				+ src.getString("col_name") : src.getString("col_name");
		src.set("col_cascadeName", newCasName);

		//修改级联ID
		String oldCasID = src.getString("col_cascadeID");
		String newCasID = (dest != null) ? dest.getString("col_cascadeID") + SEPARATOR
				+ src.getDocID() : String.valueOf(src.getDocID());
		src.set("col_cascadeID", newCasID);

		return new AsChildInfo(parent, src, dest, oldCasID, newCasID, oldCasName, newCasName);
	}

	//一个栏目拖拽成子栏目时的数据项的保存，同一事务
	private void asChildSave(AsChildInfo asChild, DBSession conn) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();

		if (asChild.parent != null)
			docManager.save(asChild.parent, conn);
		docManager.save(asChild.src, conn);
		if (asChild.dest != null)
			docManager.save(asChild.dest, conn);

		//栏目名在不同站点下可能相同，所以需加站点条件。栏目ID全局唯一，所以对级联ID的整体修改不需要加siteID控制
		String tableName = LibHelper.getLibByID(asChild.src.getDocLibID()).getDocLibTable();
		String sqlChangeCasName = "update " + tableName
				+ " set col_cascadeName=REPLACE(col_cascadeName,?,?) where col_siteID=? and col_cascadeName like ?";
		String sqlChangeCasID = "update " + tableName
				+ " set SYS_LASTMODIFIED=?,col_cascadeID=REPLACE(col_cascadeID,?,?) where col_cascadeID like ?";
		
		//修改所有子的级联名称和级联ID
		InfoHelper.executeUpdate(sqlChangeCasID, new Object[] { DateUtils.getTimestamp(), 
				asChild.oldCasID + SEPARATOR, asChild.newCasID + SEPARATOR,
				asChild.oldCasID + SEPARATOR + "%"}, conn);
		InfoHelper.executeUpdate(sqlChangeCasName, new Object[] {
				asChild.oldCasName + SEPARATOR, asChild.newCasName + SEPARATOR,
				asChild.src.getInt("col_siteID"), asChild.oldCasName + SEPARATOR + "%"}, conn);
	}

	private void asBrother(int colLibID, long srcID, long destID, int type) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document dest = docManager.get(colLibID, destID);

		//取出目标节点的兄弟list
		Document[] brothers = getDestBrothers(dest, colLibID, srcID);

		//从兄弟list中取出排在目标节点后面的那些（这些是需要调整顺序的）
		//前节点的Order+1作为order。若无前节点，则设为1。依次判断后面的Order是否需要修改
		int pos = indexOf(brothers, destID);
		List<Document> followers = asBrotherFollowers(brothers, type, pos, srcID);
		int order = asBrotherOrder(brothers, type, pos, dest);

		//若目标的父ID与本身的父ID不同，则先改变父节点
		AsChildInfo asChildInfo = null;
		Document src = docManager.get(colLibID, srcID);
		if (dest.getInt("col_parentID") != src.getInt("col_parentID")) {
			asChildInfo = asChildInfo(colLibID, srcID, dest.getInt("col_parentID"));
			src = asChildInfo.src;
		}

		//数据库操作
		DBSession conn = null;
		try {
			conn = Context.getDBSession();
			conn.beginTransaction();

			//改变父节点
			if (asChildInfo != null)
				asChildSave(asChildInfo, conn);

			Timestamp now = DateUtils.getTimestamp();
			//然后继续改order
			src.set("col_displayOrder", order);
			src.set("SYS_LASTMODIFIED", now);
			docManager.save(src, conn);

			for (Document f : followers) {
				if (order < f.getInt("col_displayOrder"))
					break;
				order++;
				f.set("col_displayOrder", order);
				f.set("SYS_LASTMODIFIED", now);

				docManager.save(f, conn);
			}
			conn.commitTransaction();
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			e.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
	}
	private Document[] getDestBrothers(Document dest, int colLibID, long srcID) throws E5Exception {
		//取出目标节点的父节点和兄弟list
		int parentID = dest.getInt("col_parentID");
		int siteID = dest.getInt("col_siteID");
		
		Document[] brothers = null;
		if (parentID == 0) {
			//若是根节点，需要按渠道区分栏目
			Document src = get(colLibID, srcID);
			int ch = src.getInt("col_channel");
			brothers = getRoot(colLibID, siteID, ch);
		} else {
			brothers = getSub(colLibID, parentID);
		}
		return brothers;
	}
	
	private List<Document> asBrotherFollowers(Document[] brothers, int type, int pos, long srcID) {
		List<Document> followers = null;
		if (type == 1) {
			//若要加在之前，则从目标节点开始，它的前一个节点作为前节点preCol
			followers = copyList(brothers, pos, srcID);
		} else {
			//若要加在之后，则从目标的后一个开始，目标节点作为前节点
			followers = copyList(brothers, pos + 1, srcID);
		}
		return followers;
	}
	
	private int asBrotherOrder(Document[] brothers, int type, int pos, Document dest) {
		Document preCol = null;
		if (type == 1) {
			//若要加在之前，则从目标节点开始，它的前一个节点作为前节点preCol
			if (pos > 0)
				preCol = brothers[pos - 1];
		} else {
			//若要加在之后，则从目标的后一个开始，目标节点作为前节点
			preCol = dest;
		}
		//前节点的Order+1作为order。若无前节点，则设为1。依次判断后面的Order是否需要修改
		int order = (preCol == null) ? 1 : preCol.getInt("col_displayOrder") + 1;
		
		return order;
	}
	
	private int indexOf(Document[] brothers, long destID) {
		for (int i = 0; i < brothers.length; i++) {
			if (brothers[i].getDocID() == destID)
				return i;
		}
		return -1;
	}

	private List<Document> copyList(Document[] brothers, int from, long excludeID) {
		List<Document> followers = new ArrayList<Document>();

		for (int i = from; i < brothers.length; i++) {
			if (brothers[i].getDocID() != excludeID)
				followers.add(brothers[i]);
		}
		return followers;
	}

	//级联名称变化，需要修改所有的子
	private void changeCascadeName(Document doc, DBSession conn) throws E5Exception {
		String oldCasName = doc.getString("col_cascadeName");
		if ("".equals(oldCasName)) oldCasName = doc.getString("col_name");

		int pos = oldCasName.lastIndexOf(SEPARATOR);
		String newName = (pos < 0) ? doc.getString("col_name")
				: (oldCasName.substring(0, pos + 1) + doc.getString("col_name"));

		if (!newName.equals(oldCasName)) {
			doc.set("col_cascadeName", newName);//这里设置，在外面save

			//子栏目的前缀：从A~B~C~改成A~B~C'~
			newName += SEPARATOR;
			oldCasName += SEPARATOR;

			String tableName = LibHelper.getLibTable(doc.getDocLibID());
			String sqlChangeCasName = "update " + tableName 
					+ " set col_cascadeName=REPLACE(col_cascadeName,?,?) where col_siteID=?";
			
			InfoHelper.executeUpdate(sqlChangeCasName, 
					new Object[] { oldCasName, newName, doc.getInt("col_siteID") },
					conn);
			/*//此处需修改子栏目全部稿件的栏目名称
			if("xy_column".equals(tableName)){
			String sqlChangeArticleColumn ="update xy_article set a_column=REPLACE(a_column,?,?) where a_siteID=?";
			InfoHelper.executeUpdate(sqlChangeArticleColumn,
					new Object[] { oldCasName, newName, doc.getInt("col_siteID") },
					conn);
			changeArticleColumn(doc, conn);
			}*/
		}
	}

	//设置栏目的删除标记（恢复/删除），并刷新栏目缓存
	private void deleteFlag(int colLibID, long colID, int deleteFlag) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document column = docManager.get(colLibID, colID);
		
		int parentID = column.getInt("col_parentID");
		
		String casID = column.getString("col_cascadeID");
		casID += SEPARATOR + "%";
		
		//读出子孙栏目，以备恢复childCount
		Document[] children = null;
		if (deleteFlag == 0) {
			String sql = "col_cascadeID like ? order by col_cascadeID";
			children = docManager.find(colLibID, sql, new Object[]{casID});
		}
		
		//由于栏目ID全局唯一，所以对级联ID的整体修改其实不需要加siteID控制
		String tableName = LibHelper.getLibByID(colLibID).getDocLibTable();
		String sqlDelete = "update " + tableName + " set SYS_DELETEFLAG=?,SYS_LASTMODIFIED=? where SYS_DOCUMENTID=? or col_cascadeID like ?";
		String sqlChangeCount = "update " + tableName + " set col_childCount=col_childCount+?,SYS_LASTMODIFIED=? where SYS_DOCUMENTID=?";

		int count = deleteFlag > 0 ? -1 : 1;
		Timestamp now = DateUtils.getTimestamp();
		
		DBSession db = Context.getDBSession();
		try {
			db.beginTransaction();
			db.executeUpdate(sqlDelete, new Object[] { deleteFlag, now, colID, casID });
			db.executeUpdate(sqlChangeCount, new Object[] { count, now, parentID });
			if (deleteFlag == 0) {
				//若是恢复，则需要重新计算子孙栏目的childCount
				calChildCount(tableName, colID, db);
				for (int i = 0; children != null && i < children.length; i++) {
					calChildCount(tableName, children[i].getDocID(), db);
				}
			}
			db.commitTransaction();
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(db);
			throw new E5Exception("[栏目删除/恢复]", e);
		} finally {
			ResourceMgr.closeQuietly(db);
		}
	}
	//重新计算分类的child_count字段
	private void calChildCount(String tableName, long colID,DBSession db) throws SQLException,E5Exception {	
		int count = 0;
		String sql = "select count(*) from " + tableName + " where col_parentID=? and SYS_DELETEFLAG=0";
		IResultSet rs = db.executeQuery(sql, new Object[]{colID});
		if (rs.next()) 
			count = rs.getInt(1);
		rs.close();
		
		sql = "update " + tableName + " set col_childCount=? where SYS_DOCUMENTID=?";
		db.executeUpdate(sql, new Object[]{new Integer(count), colID});
	}
	
	/**
	 * 取栏目的子栏目的最大排序号
	 * @param parentID  栏目ID
	 * @param siteID 站点ID，仅在读根栏目时需要(parentID=0)
	 */
	private int getMaxSubOrder(int colLibID, long parentID, int siteID) throws E5Exception {
		String sql = null;
		Object[] params = null;
		String tableName = getColLibTable(colLibID);
		if (parentID > 0) {
			sql = "select max(col_displayOrder) from " + tableName + " where col_parentID=?";
			params = new Object[] { parentID };
		} else {
			sql = "select max(col_displayOrder) from " + tableName + " where col_siteID=? and col_parentID=0";
			params = new Object[] { siteID };
		}
		DBSession conn = null;
		IResultSet rs = null;
		int order = 0;
		try {
			conn = Context.getDBSession();
			rs = conn.executeQuery(sql, params);
			if (rs.next())
				order = rs.getInt(1);
		} catch (SQLException e) {
			throw new E5Exception(e);
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}

		return order;
	}

	private Document[] getColumns(int colLibID, long[] ids) throws E5Exception {
		if (ids == null)
			return null;

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		
		List<Document> docs = new ArrayList<Document>();

		for (int i = 0; i < ids.length; i++) {
			Document doc = docManager.get(colLibID, ids[i]);
			if (doc != null && doc.getDeleteFlag() == 0)
				docs.add(doc);
		}

		Collections.sort(docs, new Comparator<Document>() {
			public int compare(Document me1, Document me2) {
				return new Integer(me1.getInt("col_displayOrder")).compareTo(me2.getInt("col_displayOrder"));
			}
		});

		return docs.toArray(new Document[0]);
	}

	private String getColLibTable(int colLibID) {
		return LibHelper.getLibByID(colLibID).getDocLibTable();
	}
    //取出栏目页html路径
    private String getColPath(int ruleLibID, long ruleID, long colID, String fileName, int templateID) throws E5Exception {
    	if (ruleID > 0 && templateID > 0) {
            //该栏目没有发布规则
            DocumentManager docManager = DocumentManagerFactory.getInstance();
            Document rule = docManager.get(ruleLibID, ruleID);
            if (rule != null) {
                String path = rule.getString("rule_column_dir");
                if (!path.endsWith("/")) path += "/";

                boolean colByDate = rule.getInt("rule_column_date") == 1;
                if (colByDate) path += DateUtils.format("yyyyMM/dd/");

                if (!StringUtils.isBlank(fileName))
                    path += fileName + ".html";
                else
                	path += "col" + colID + ".html";
                return path;
            }
        }
    	return null;
    }

	private class AsChildInfo {
		Document parent;
		Document src;
		Document dest;
		String oldCasID;
		String newCasID;
		String oldCasName;
		String newCasName;

		public AsChildInfo(Document parent, Document src, Document dest, String oldCasID,
				String newCasID, String oldCasName, String newCasName) {
			super();
			this.parent = parent;
			this.src = src;
			this.dest = dest;
			this.oldCasID = oldCasID;
			this.newCasID = newCasID;
			this.oldCasName = oldCasName;
			this.newCasName = newCasName;
		}
	}

	/**
	 * 用于 ColumnCache.refresh()
	 * 获取所有的聚合栏目的map <br>
	 * 1. 获得doc的结构是 <br>
	 * ｛SYS_DOCUMENTID= column_id , col_aggregateIDs=[col_aggregateID1, col_aggregateID2,col_aggregateID3...]} 2.
	 * 转成Map<Long:col_id, List<Long:aggregated_col_id>><br>
	 * 例如：{col_aggregateID1=[column_id1, column_id2,column_id3....], col_aggregateID2=[column_id1,
	 * column_id3,column_id4....] }
	 * 
	 * @return
	 * @throws E5Exception
	 */
	public Map<Long, Set<Long>> findcolumnAggregateMap(int colLibID) throws E5Exception {

		//返回结果
		Map<Long, Set<Long>> columnAggregateMap = new HashMap<>();
		//每一个
		Set<Long> _aggregatedSet = null;

		String conditions = "col_aggregateIDs is not null and SYS_DELETEFLAG=0";
		//查询当前栏目聚合了哪些栏目
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] documents = docManager.find(colLibID, conditions, new Object[] {});
		if (documents != null && documents.length > 0) {
			//把被聚合到的栏目塞到相应栏目的list里面去
			for (Document doc : documents) {
				long _id = doc.getDocID();
				String _col_aggregateIDs = doc.getString("col_aggregateIDs");

				//如果col_aggregateIDs不为空,取出来
				if (!StringUtils.isBlank(_col_aggregateIDs)) {
					//List<Long>， 取出col_aggregateIDs
					long[] _col_aggregateIDsArr = StringUtils.getLongArray(_col_aggregateIDs);
					for (long _aggregateID : _col_aggregateIDsArr) {
						//取出被聚合的栏目，把栏目加进去
						_aggregatedSet = columnAggregateMap.get(_aggregateID);
						if (_aggregatedSet == null) {
							_aggregatedSet = new HashSet<Long>();
							columnAggregateMap.put(_aggregateID, _aggregatedSet);
						}
						_aggregatedSet.add(_id);
					}
				}
			}
		}

		return columnAggregateMap;
	}
	
	/**
	 * 设置聚合栏目
	 * @param colID
	 * @param ids
	 * @throws Exception
	 */
	public void changeAggregateIDs(int colLibID, long colID, String ids) throws Exception {
		Document col = get(colLibID, colID);
		col.set("col_aggregateIDs", ids);
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		docManager.save(col);
	}

	public List<Long> getChildrenIDs(Column column) throws E5Exception {
		String tableName = LibHelper.getLib(DocTypes.COLUMN.typeID()).getDocLibTable();
		String sql = "select SYS_DOCUMENTID from " + tableName
				+ " where col_cascadeID like ? order by col_cascadeID";

		String casID = column.getCasIDs() + ColumnManager.SEPARATOR + "%";

		DBSession db = Context.getDBSession();
		IResultSet rs = null;
		List<Long> result = new ArrayList<>();
		try {
			rs = db.executeQuery(sql, new Object[] { casID });
			while (rs.next()) {
				//读ID rs.getLong("SYS_DOCUMENTID");
				result.add(rs.getLong("SYS_DOCUMENTID"));
			}
		} catch (Exception e) {
			throw new E5Exception("Read DocumentID Error!", e);
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(db);
		}
		return result;
	}
	
    //获取当前登录用户权限下的全部栏目
	public Document[] getRoot(int colLibID, int siteID, int channelType, long id) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();

		Document[] cols = docManager.find(colLibID,
				"col_siteID=? and col_parentID=? and col_channel=? and SYS_DELETEFLAG=0 order by col_displayOrder",
				new Object[] { siteID, id, channelType });
		return cols;
	}
    //判断是否有栏目流程权限
	public String canPuborApr(int docLibID, int colID, int roleID) throws E5Exception{
		int colLibID = getColLibID(docLibID);
		int flowID = getFlowID(colLibID, colID);

		//一个流程的所有的流程节点
		FlowReader flowReader = (FlowReader)Context.getBean(FlowReader.class);
		FlowNode[] nodes = flowReader.getFlowNodes(flowID);

		if (1 == flowID){//无审批流程
			//若主栏目是无审批流程，那么得需要判断是否有发布权限
			FlowPermissionReader fpReader = (FlowPermissionReader)Context.getBean(FlowPermissionReader.class);
			boolean canPublish = fpReader.hasPermission(roleID, nodes[0].getFlowID(), nodes[0].getID(), "发布");

			if(canPublish){
				return "1";
			}else{
				return "0";
			}
		} else{//审批流程
			//若主栏目是审批流程，那么得需要判断是否有送审权限
			FlowPermissionReader fpReader = (FlowPermissionReader)Context.getBean(FlowPermissionReader.class);
			boolean canApr = fpReader.hasPermission(roleID, nodes[0].getFlowID(), nodes[0].getID(), "送审");
			if(canApr){
				return "2";
			}else{
				return "0";
			}
		}
	}
	//获取全部栏目
	public Document[] getAllRoot(int colLibID, int siteID, int channelType) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();

		Document[] cols = docManager.find(colLibID,
				"col_siteID=? and col_channel=? and col_parentID=0 and SYS_DELETEFLAG=0 order by col_displayOrder",
				new Object[] { siteID, channelType });
		return cols;
	}
	private int getColLibID(int otherLibID) {
		return LibHelper.getLibIDByOtherLib(DocTypes.COLUMN.typeID(), otherLibID);
	}

	private int getFlowID(int colLibID, long colID) throws E5Exception {
		Column col = colReader.get(colLibID, colID);
		return (col == null) ? 0 : col.getFlowID();
	}

	public String getAllRootStr(int colLibID, int siteID, int channelType) {
		DBSession db = null;
		IResultSet rs = null;
		String rootStr = "";
		int i = 0;
		String sql = "select sys_documentid from xy_column where col_siteID=? and col_channel=? and SYS_DELETEFLAG=0 order by col_displayOrder";
		try {
			db = InfoHelper.getDBSession(colLibID);
			rs = db.executeQuery(sql, new Object[] { siteID, channelType });
			while (rs != null && rs.next()) {
				long tplId = rs.getInt("sys_documentid");
				if(i==0)
					rootStr = rootStr + tplId;
				else
					rootStr = rootStr + "," + tplId;
				i++;
			}

		} catch (Exception e) {
			System.out.println("ColumnManager.getAllRootStr exception:"
					+ e.getLocalizedMessage() + ".SQL:" + sql);
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(db);
		}
		return rootStr;
	}
}