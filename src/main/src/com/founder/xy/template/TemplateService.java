package com.founder.xy.template;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.founder.e5.commons.ArrayUtils;
import com.founder.e5.commons.DomHelper;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.DBType;
import com.founder.e5.db.IResultSet;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.web.SysUser;
import com.founder.e5.web.WebUtil;
import com.founder.e5.workspace.app.form.LogHelper;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;

@Service("templateService")
public class TemplateService {
	private TemplateDao templateDao;

	public TemplateDao getTemplateDao() {
		return templateDao;
	}

	@Resource(name = "templateDao")
	public void setTemplateDao(TemplateDao templateDao) {
		this.templateDao = templateDao;
	}

	/**
	 * 1. 先根据模版的id查出来他是什么类型的模版，例如手机模版或者栏目模版 2.
	 * 根据模版的类型确定查询栏目表中的哪个字段，例如0-col_template 1-col_templatearticle 3.
	 * 在xy_column表中查询 相应模版字段中等于模版id的栏目列表
	 */
	public GrantInfo findIds(GrantInfo parameters) throws E5Exception {
		// 1. 先根据模版的id查出来他是什么类型的模版，例如手机模版或者栏目模版
		// 2. 根据模版的类型确定查询栏目表中的哪个字段，例如0-col_template 1-col_templatearticle
		// -在设置parameter中的rt_templateType时设置
		parameters = templateDao.findTemplateCode(parameters);

		if (parameters.getDb_templateColumnName() != null
				&& !parameters.getDb_templateColumnName().isEmpty()) {
			parameters = templateDao.findIds(parameters);
		}
		// 3. 在xy_column表中查询 相应模版字段中等于模版id的栏目列表

		return parameters;
	}

	/**
	 * 对用户的挂接操作进行处理 1. if： ids == null && newIds == null：不进行操作，直接返回 2. if: ids
	 * == null && newIds != null: 说明是第一次挂接栏目，直接添加newIds 3. if: ids != null &&
	 * newIds == null: 说明去掉了所有的栏目，对ids进行删除操作 4. if: ids != null && newIds !=
	 * null: 需要判断前后两次操作列表是否是一样的 4.1 如果一样，不需要操作，直接返回。 4.2
	 * 如果不一样，对比两个列表，得到添加列表以及删除列表。 4.2 1. 取出旧数组中的一个数据 4.2 2.
	 * 如果新数组中包含的话，把新数组中这个数据去掉 - 说明不需要进行操作 4.2 3.
	 * 如果新数组中没有的话，说明这个数据已经被取消，把这个数据放到删除数组当中 4.2 4. 最后经过删除旧数据的新数组就是添加数组了 5.
	 * 进行添加与删除操作
	 */
	public GrantInfo changeColumnTemplate(GrantInfo parameters)
			throws Exception {
		// 不进行操作，直接返回
		if (parameters == null) {
			parameters = new GrantInfo();
			parameters.setRt_operationResult("对不起，无法获取参数，请重新进行操作！");
			return parameters;
		}
		// 不进行操作，直接返回
		if (parameters.getIds() == null && parameters.getNewIds() == null) {
			parameters.setRt_operationResult("NoOperation");
			return parameters;
		}
		long[] ids2 = filterOldIDs(parameters.getIds(), parameters.getNewIds(),
				parameters.getNotExpanded(),
				Integer.parseInt(parameters.getGroupID()));
		String ids = parameters.getNewIds();
		if (ids2 != null && ids2.length > 0) {
			if (!StringUtils.isBlank(ids))
				ids += ",";
			ids += StringUtils.join(ids2);
		}
		parameters.setNewIds(ids);

		// 获得添加列表和删除列表
		Map<String, List<String>> addAndDelListMap = InfoHelper
				.assembleAddAndDelArray(parameters.getIds(),
						parameters.getNewIds());

		// 进行添加与删除操作
		if (addAndDelListMap.get("addList") != null
				&& !addAndDelListMap.get("addList").isEmpty()) {
			templateDao.addNewColumns(parameters,
					addAndDelListMap.get("addList"));
		}
		if (addAndDelListMap.get("delList") != null
				&& !addAndDelListMap.get("delList").isEmpty()) {
			templateDao.delColumns(parameters, addAndDelListMap.get("delList"));
		}
		if (addAndDelListMap != null && !addAndDelListMap.isEmpty()) {
			parameters.setRt_AddAndDelMap(addAndDelListMap);
		}

		/************************ 以下是为了写日志 *************************/
		int colLibID = LibHelper.getLibIDByOtherLib(DocTypes.COLUMN.typeID(),
				Integer.parseInt(parameters.getDocLibID()));
		// 显示新挂接栏目的名称
		StringBuilder operationResult = new StringBuilder("【目前挂接的栏目】：");
		List<String> newList = Arrays.asList(parameters.getNewIds().split(","));
		// 目前挂接的栏目
		// 如果不为空，就把目前挂接的栏目贴在后面
		if (newList != null && !newList.isEmpty()) {
			operationResult.append(templateDao.findNewColumnNames(colLibID,
					newList) + "；");
		} else {
			operationResult.append("无；");
		}

		// 新挂接的栏目
		operationResult.append("【新挂接的栏目】：");
		if (addAndDelListMap.get("addList") != null
				&& !addAndDelListMap.get("addList").isEmpty()) {
			operationResult.append(templateDao.findNewColumnNames(colLibID,
					addAndDelListMap.get("addList")) + "；");
		} else {
			operationResult.append("无；");
		}
		// 取消挂接的栏目
		operationResult.append("【取消挂接的栏目】：");
		if (addAndDelListMap.get("delList") != null
				&& !addAndDelListMap.get("delList").isEmpty()) {
			operationResult.append(templateDao.findNewColumnNames(colLibID,
					addAndDelListMap.get("delList")) + "；");
		} else {
			operationResult.append("无；");
		}

		parameters
				.setRt_operationResult("success" + operationResult.toString());
		return parameters;
	}

	/**
	 * 当挂接栏目操作成功之后，写栏目日志，包括添加操作和删除操作的日志
	 * 
	 * @param request
	 * @param sysUser
	 * @param parameters
	 * @throws Exception
	 */
	public void writeColumnLog(HttpServletRequest request, SysUser sysUser,
			GrantInfo parameters) throws Exception {
		// 获取添加栏目的列表
		List<String> addList = parameters.getRt_AddAndDelMap().get("addList");
		int libID = LibHelper.getColumnLibID(request);
		// 如果添加列表不为空，为每一个栏目添写日志
		if (addList != null && !addList.isEmpty()) {
			for (String id : addList) {
				if (id != null && !id.trim().equals("")) {
					LogHelper.writeLog(libID, Long.parseLong(id), sysUser,
							"模版挂接栏目",
							"挂接到模版：【" + parameters.getDb_templateRealName()
									+ "(" + parameters.getDocIDs() + ")】");
				}
			}
		}
		// 获取删除栏目的列表
		List<String> delList = parameters.getRt_AddAndDelMap().get("delList");
		if (delList != null && !delList.isEmpty()) {
			for (String id : delList) {
				if (id != null && !id.trim().equals("")) {
					LogHelper.writeLog(libID, Long.parseLong(id), sysUser,
							"模版挂接栏目",
							"取消模版：【" + parameters.getDb_templateRealName()
									+ "(" + parameters.getDocIDs() + ")】");
				}
			}
		}

	}

	/**
	 * 根据type与siteID获取模板
	 */
	public Document[] getTempaltes(int siteID, int type, int channel)
			throws E5Exception {
		int tplLibID = LibHelper.getTemplateLibID();

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] tpls = docManager
				.find(tplLibID,
						"t_siteID=? and t_type=? and t_channel=? and t_groupID>0 and SYS_DELETEFLAG=0 order by sys_documentid desc",
						new Object[] { siteID, type, channel });
		return tpls;
	}

	/**
	 * 根据docID判断模板是否被引用
	 */
	public boolean tplUsed(int docLibID, long docID) throws E5Exception {
		try {
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			Document[] cols = null;
			String sql = "col_template_ID = ? "
					+ "or col_templateArticle_ID = ? "
					+ "or col_templatePic_ID = ? "
					+ "or col_templateVideo_ID = ? "
					+ "or col_templatePad_ID = ? "
					+ "or col_templateArticlePad_ID = ? "
					+ "or col_templatePicPad_ID = ? "
					+ "or col_templateVideoPad_ID = ? ";
			int columnLibID = LibHelper.getLibIDByOtherLib(
					DocTypes.COLUMN.typeID(), docLibID);

			cols = docManager.find(columnLibID, sql, new Object[] { docID,
					docID, docID, docID, docID, docID, docID, docID });
			if ((cols == null || cols.length == 0)) {
				return false;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 根据docID删除一个模板
	 * @param isNew 
	 */
	public void deleteTpl(int docLibID, long docID, boolean isNew) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = docManager.get(docLibID, docID);

		String fileName = doc.getString("t_file");
		int pos = fileName.indexOf(";");
		String device = fileName.substring(0, pos); // 存储设备
		String rltPath = fileName.substring(fileName.indexOf(";") + 1); // 相对路径和文件名

		// 文件的绝对路径
		String devicePath = InfoHelper.getDevicePath(InfoHelper
				.getDeviceByName(device));
		String oldPath = devicePath + File.separator + rltPath;

		// 删除实体文件
		deleteFile(oldPath);
		// 删除数据库记录--逻辑删除，设置删除标识为1
		doc.set("SYS_DELETEFLAG", 1);
		docManager.save(doc);
		if(isNew)
			docManager.delete(doc);
	}

	/** 文件合法与否最后都要删除文件，这里将来想办法处理，若是有别的资源占用文件，删除不了，该怎么处理 */
	public void deleteFile(String filePath) {
		File file = new File(filePath);
		if (file != null) {
			file.delete();
		}
	}

	/**
	 * 查询区块列表
	 */
	public Document[] find(int siteID, int type, int channel, String name, boolean flag) throws E5Exception {
		int tplLibID = LibHelper.getTemplateLibID();
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] tpls;
		if(flag) {
			//按id检索
			int sys_documentId=Integer.parseInt(name);
			tpls = docManager.find(tplLibID,
					" t_siteID=? and t_type=? and t_channel=? and (t_name like ? or SYS_DOCUMENTID like ? ) and SYS_DELETEFLAG=0",
					new Object[]{siteID, type, channel, name + "%",sys_documentId+"%"});
		}
		else {
			tpls = docManager.find(tplLibID,
					"t_siteID=? and t_type=? and t_channel=? and t_name like ? and SYS_DELETEFLAG=0",
					new Object[]{siteID, type, channel, name + "%"});
		}
		return tpls;
	}

	/**
	 * 判断该分组下是否有type的模板，若没有返回false,有则返回true
	 */
	public boolean hasChild(int siteID, int channel, int catID, int type)
			throws E5Exception {

		try {
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			Document[] tpls = null;
			String sql = "t_siteID=? " + "and t_channel=? "
					+ "and t_groupID=? " + "and t_type=? ";
			int tplLibID = LibHelper.getTemplateLibID();

			tpls = docManager.find(tplLibID, sql, new Object[] { siteID,
					channel, catID, type });
			if ((tpls == null || tpls.length == 0)) {
				return false;
			}
			return true;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	// 取出没被这次选中的旧栏目ID，找出藏在未展开里面的。
	private long[] filterOldIDs(String oldIDs, String ids, String notExpanded,
			int colLibID) throws E5Exception {

		long[] idArr = StringUtils.getLongArray(ids);
		long[] oldIDArr = StringUtils.getLongArray(oldIDs);

		if (oldIDArr == null)
			return null;
		// 取出没被这次选中的旧栏目ID
		List<Long> result = new ArrayList<>();
		for (long oldID : oldIDArr) {
			if (!ArrayUtils.contains(idArr, oldID)) {
				result.add(oldID);
			}
		}
		if (result.size() == 0)
			return null;

		// 检查是否在未展开节点里
		long[] notExpandedArr = StringUtils.getLongArray(notExpanded);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		for (int i = result.size() - 1; i >= 0; i--) {
			long oldID = result.get(i);
			Document col = docManager.get(colLibID, oldID);
			long[] path = StringUtils.getLongArray(
					col.getString("col_cascadeID"), "~");
			if (!contains(path, notExpandedArr)) {
				result.remove(i);
			}
		}
		return InfoHelper.getLongArray(result);
	}

	// 检查栏目的父路径中是否有未展开的
	private boolean contains(long[] path, long[] notExpandedArr) {
		for (long l : path) {
			if (ArrayUtils.contains(notExpandedArr, l))
				return true;
		}
		return false;
	}

	@SuppressWarnings({ "unused", "static-access" })
	public Document[] findAll(HttpServletRequest request) throws Exception {
		int tplLibID = LibHelper.getTemplateLibID();
		String beginTimestr = WebUtil.get(request, "beginTime");
		String endTimestr = WebUtil.get(request, "endTime");
		String sql = "SYS_DELETEFLAG=0";
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date beginTime = null;
		Date endTime = null;
		if (!StringUtils.isBlank(beginTimestr))
			beginTime = format.parse(beginTimestr);
		if (!StringUtils.isBlank(beginTimestr)) {
			endTime = format.parse(endTimestr);
			if (isOracle()) {
				Calendar calendar = new GregorianCalendar();
				calendar.setTime(endTime);
				calendar.add(calendar.DATE, 1);
				endTime = calendar.getTime();
			}
		}

		Object[] obj = null;
		if (beginTime != null || endTime != null) {
			if (beginTime != null && endTime != null) {
				if (isOracle())
					sql = sql + " and SYS_CREATED between ? and ? ";
				else
					sql = sql
							+ " and SYS_CREATED between ? and DATE_ADD( ? ,INTERVAL 1 DAY) ";
				obj = new Object[] { beginTime, endTime };
			} else if (beginTime != null) {
				sql = sql + " and SYS_CREATED >= ?  ";
				obj = new Object[] { beginTime };
			} else if (beginTime != null) {
				if (isOracle())
					sql = sql + " and SYS_CREATED <= ?  ";
				else
					sql = sql
							+ " and SYS_CREATED <= DATE_ADD( ? ,INTERVAL 1 DAY)  ";
				obj = new Object[] { endTime };
			}
		}

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] tpls = docManager.find(tplLibID, sql, obj);
		return tpls;
	}

	private boolean isOracle() {
		String dbType = DomHelper.getDBType();
		return dbType.equals(DBType.ORACLE);
	}

	public Document[] findByParams(int docID, String docTitle, int siteID,
			HttpServletRequest request, boolean bo) throws E5Exception {
		int tplLibID = LibHelper.getTemplateLibID();
		Document[] tpls = null;
		String pno = WebUtil.get(request, "pageNo");
		String psize = WebUtil.get(request, "pageSize");
		int pageNum = StringUtils.isBlank(pno) ? 0 : Integer.parseInt(pno);
		int pageSize = StringUtils.isBlank(psize) ? 0 : Integer.parseInt(psize);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		String sql = " t_siteID=? and SYS_DELETEFLAG=0 and t_groupID != 0";
		Object[] obj = null;
		int i = 0;
		int limitBegin = (pageNum - 1) * pageSize;
		int limitEnd = pageSize;
		// TODO
		// Oracle分页待调整
		if (isOracle() && pageNum > 0 && pageSize > 0 && bo) {
			sql = " select * from xy_template where t_siteID=? and SYS_DELETEFLAG=0 and t_groupID != 0";
			limitBegin = (pageNum - 1) * pageSize;
			obj = new Object[] { siteID };
			if (docID != 0 || !StringUtils.isBlank(docTitle)) {
				sql = sql + " and ( ";
				if (docID != 0) {
					sql = sql + " SYS_DOCUMENTID=? ";
					obj = new Object[] { siteID, docID };
					i++;
				}
				if (!StringUtils.isBlank(docTitle)) {
					docTitle = "%" + docTitle + "%";
					obj = new Object[] { siteID, docID, docTitle };
					if (i == 0) {
						obj = new Object[] { siteID, docTitle };
					} else
						sql = sql + " or ";
					sql = sql + " t_name like ? ";
				}
				sql = sql + " ) ";
			}
			sql = sql + " order by sys_documentid asc ";
			tpls = queryArticle(tplLibID, sql, obj, limitBegin, pageSize,
					String.valueOf(siteID));

		} else {
			obj = new Object[] { siteID };
			if (docID != 0 || !StringUtils.isBlank(docTitle)) {
				sql = sql + " and ( ";
				if (docID != 0) {
					sql = sql + " SYS_DOCUMENTID=? ";
					obj = new Object[] { siteID, docID };
					i = 1;
				}
				if (!StringUtils.isBlank(docTitle)) {
					docTitle = "%" + docTitle + "%";
					obj = new Object[] { siteID, docID, docTitle };
					if (i == 0) {
						obj = new Object[] { siteID, docTitle };
					} else
						sql = sql + " or ";
					sql = sql + " t_name like ? ";
					i = i == 1 ? 3 : 2;
				}
				sql = sql + " ) ";
			}
			sql = sql + " order by sys_documentid asc";
			if (pageNum > 0 && pageSize > 0 && bo) {
				sql = sql + " limit ?,?";
				switch (i) {
				case 1:
					obj = new Object[] { siteID, docID, limitBegin, limitEnd };
					break;
				case 2:
					obj = new Object[] { siteID, docTitle, limitBegin, limitEnd };
					break;
				case 3:
					obj = new Object[] { siteID, docID, docTitle, limitBegin,
							limitEnd };
					break;
				default:
					obj = new Object[] { siteID, limitBegin, limitEnd };
					break;
				}
			}
			tpls = docManager.find(tplLibID, sql, obj);
		}
		return tpls;
	}

	public Document[] findAll() throws E5Exception {
		int tplLibID = LibHelper.getTemplateLibID();

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] tpls = docManager.find(tplLibID, "SYS_DELETEFLAG=0", null);
		return tpls;
	}

	public Document[] findById(int docID) throws E5Exception {
		int tplLibID = LibHelper.getTemplateLibID();

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] tpls = docManager.find(tplLibID,
				"SYS_DOCUMENTID=? AND SYS_DELETEFLAG=0 and t_groupID != 0 ",
				new Object[] { docID });
		return tpls;
	}

	public Document[] findPage(HttpServletRequest request) throws Exception {

		Document[] tpls = null;
		int tplLibID = LibHelper.getTemplateLibID();
		String groupID = WebUtil.get(request, "groupID");
		String siteID = WebUtil.get(request, "siteID");
		String type = WebUtil.get(request, "type");
		int pageNum = Integer.parseInt(WebUtil.get(request, "pageNo"));
		int pageSize = Integer.parseInt(WebUtil.get(request, "pageSize"));
		String sql = " t_siteID=? and SYS_DELETEFLAG=0";

		Object[] obj = null;
		int limitBegin = (pageNum - 1) * pageSize;
		int limitEnd = pageSize;
		// TODO
		// Oracle分页待调整
		if (isOracle()) {
			sql = " select * from xy_template where t_siteID=? and SYS_DELETEFLAG=0";
			limitBegin = (pageNum - 1) * pageSize;
			obj = new Object[] { Integer.parseInt(siteID) };
			if (!StringUtils.isBlank(groupID)) {
				sql = sql + " and t_groupid=? ";
				obj = new Object[] { Integer.parseInt(siteID),
						Integer.parseInt(groupID) };
			} else
				sql = sql + " and t_groupID != 0 ";
			if (!StringUtils.isBlank(type)) {
				sql = sql + " and t_type=? ";
				if (!StringUtils.isBlank(groupID))
					obj = new Object[] { Integer.parseInt(siteID),
							Integer.parseInt(groupID), Integer.parseInt(type) };
				else
					obj = new Object[] { Integer.parseInt(siteID),
							Integer.parseInt(type) };
			}
			sql = sql + " order by sys_documentid asc ";
			tpls = queryArticle(tplLibID, sql, obj, limitBegin, pageSize,
					siteID);

		} else {
			if (!StringUtils.isBlank(type))
				sql = sql + " and t_type=? ";
			if (!StringUtils.isBlank(groupID)) {
				sql = sql
						+ " and t_groupid=? order by sys_documentid asc limit ?,?";
				if (!StringUtils.isBlank(type))
					obj = new Object[] { Integer.parseInt(siteID),
							Integer.parseInt(type), Integer.parseInt(groupID),
							limitBegin, limitEnd };
				else
					obj = new Object[] { Integer.parseInt(siteID),
							Integer.parseInt(groupID), limitBegin, limitEnd };
			} else {
				sql = sql
						+ " and t_groupID != 0 order by sys_documentid asc limit ?,?";
				if (!StringUtils.isBlank(type))
					obj = new Object[] { Integer.parseInt(siteID),
							Integer.parseInt(type), limitBegin, limitEnd };
				else
					obj = new Object[] { Integer.parseInt(siteID), limitBegin,
							limitEnd };
			}
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			tpls = docManager.find(tplLibID, sql, obj);
		}

		return tpls;

	}

	private Document[] queryArticle(int tplLibID, String sql, Object[] obj,
			int start, int count, String siteID) {
		DBSession db = null;
		IResultSet rs = null;
		Document[] tplsAll = null;
		Document[] tpls = new Document[count];
		int num = 0;
		try {
			db = InfoHelper.getDBSession(tplLibID);
			sql = db.getDialect().getLimitString(sql, start, count);
			rs = db.executeQuery(sql, obj);
			while (rs != null && rs.next()) {
				long tplId = rs.getInt("sys_documentid");
				int tplType = rs.getInt("t_type");
				int tplChannel = rs.getInt("t_channel");
				int groupId = rs.getInt("t_groupid");
				String tplName = rs.getString("t_name");
				String tplPath = rs.getString("t_file");
				Timestamp createTime = (Timestamp) rs
						.getTimestamp("sys_created");
				DocumentManager docManager = DocumentManagerFactory
						.getInstance();
				Document tpl = null;
				try {
					tpl = docManager.newDocument(LibHelper.getTemplateLibID(),
							tplId);
				} catch (E5Exception e) {
					e.printStackTrace();
				}
				tpl.set("t_siteid", siteID);
				tpl.set("t_type", tplType);
				tpl.set("t_channel", tplChannel);
				tpl.set("t_name", tplName);
				tpl.set("t_file", tplPath);
				tpl.set("sys_created", createTime);
				tpl.set("t_groupid", groupId);
				tpls[num] = tpl;
				num++;
			}
			if (num > 0) {
				tplsAll = new Document[num];
				for (int i = 0; i < num; i++)
					tplsAll[i] = tpls[i];
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
		return tplsAll;
	}

	public Document[] findByParamsForPage(String groupID, String typestr,
			int siteID, HttpServletRequest request, boolean bo) throws Exception {
		int tplLibID = LibHelper.getTemplateLibID();
		Document[] tpls = null;
		int groupId = StringUtils.isBlank(groupID) ? 0 : Integer.parseInt(groupID);
		int type = StringUtils.isBlank(typestr) ? 0 : Integer.parseInt(typestr);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		String sql = " t_siteID=? and SYS_DELETEFLAG=0 and t_groupID != 0";
		Object[] obj = null;
		int i = 0;
		obj = new Object[] { siteID };
		if (groupId != 0 || type != 0) {
			sql = sql + " and ( ";
			if (groupId != 0) {
				sql = sql + " t_groupID=? ";
				obj = new Object[] { siteID, groupId };
				i = 1;
			}
			if (type != 0) {
				obj = new Object[] { siteID, groupId, type };
				if (i == 0) {
					obj = new Object[] { siteID, type };
				} else
					sql = sql + " or ";
				sql = sql + " t_type=? ";
			}
			sql = sql + " ) ";
		}
		sql = sql + " order by sys_documentid asc";
		tpls = docManager.find(tplLibID, sql, obj);
		return tpls;
	}

	public Document[] findBySite(HttpServletRequest request) throws Exception {
		int tplLibID = LibHelper.getTemplateLibID();
		int siteID = Integer.parseInt(request.getParameter("siteID"));
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] tpls = docManager.find(tplLibID,	"t_siteID=? and SYS_DELETEFLAG=0", new Object[] {siteID});
		return tpls;
		
	}

	public Document[] find(int siteID, String name) throws E5Exception {
		int tplLibID = LibHelper.getTemplateLibID();
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] tpls;
		//按id检索
		int groupid=Integer.parseInt(name);
		tpls = docManager.find(tplLibID,
				" t_siteID=? and t_groupid like ? and SYS_DELETEFLAG=0",
				new Object[]{siteID, groupid + "%"});
		return tpls;
	}
}
