package com.founder.xy.api.ext;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.system.Tenant;

/**
 * 项目中扩展的稿件功能
 */
@Service
public class ArticleExtApiManager {

	/**
	 * 输出原创稿件
	 */
	public String getArticlesCopyright(int siteID,int page, int size, String startTime,
			String endTime) throws E5Exception {

		int start = page < 1 ? 1 : (page - 1) * size;

		String tenantCode = Tenant.DEFAULTCODE;
		int columnLibId = LibHelper.getLibID(DocTypes.COLUMN.typeID(),
				tenantCode);
		boolean hasNext = this.isHasNext(siteID,page, size, startTime, endTime);

		StringBuffer sqlData = new StringBuffer(
				"select xar.SYS_DOCUMENTID,case when xar.a_sourceType = 3 then 1 else 0 end as isbatman,"
						+ " xar.a_urlPad,xar.SYS_AUTHORID,xar.SYS_AUTHORS,"
						+ " xar.a_collaborator,xar.a_linkTitle,xar.a_subTitle,xar.a_leadTitle,xar.SYS_CREATED,"
						+ " xar.a_pubTime,xar.a_source,xar.a_wordCount,xar.a_isExclusive,xar.a_columnID,"
						+ " xc.col_code as colCode,xar.a_column,xcf.SYS_DOCUMENTID a_columnIDF,"
						+ " xcf.col_code colCodeF,xcf.col_name a_columnF,xar.a_countClick,"
						+ " xar.a_countDiscuss,xar.a_countShare,xar.a_countShareClick,"
						+ " xar.SYS_HAVEATTACH,xar.a_content,xar.a_editor,xar.a_liability from  "
						+ LibHelper.getLibTable(LibHelper.getArticleAppLibID())
						+ " xar left join "
						+ LibHelper.getLibTable(columnLibId)
						+ " xc on xc.SYS_DOCUMENTID = xar.a_columnID left join "
						//取一级目录信息
						+ LibHelper.getLibTable(columnLibId)
						+ " xcf on xcf.SYS_DOCUMENTID = SUBSTR(xc.col_cascadeID,1,"
						+ " case LOCATE('~',xc.col_cascadeID)-1 when -1 then LENGTH(xc.col_cascadeID) "
						+ " else LOCATE('~',xc.col_cascadeID)-1 end)"	
						+ " where xar.a_status = 1 and xar.a_copyright = 1 and xar.a_siteid = ? ");

		List<String> params = new ArrayList<String>();
		params.add(String.valueOf(siteID));
		if (StringUtils.hasText(getNotNull(startTime)) && StringUtils.hasText(getNotNull(endTime))) {
			sqlData.append(" and xar.a_pubTime > DATE_FORMAT(?,'%Y-%m-%d %T')");
			sqlData.append(" and xar.a_pubTime < DATE_FORMAT(?,'%Y-%m-%d %T')");
		
			params.add(startTime);
			params.add(endTime);
		} else {
			if (StringUtils.hasText(getNotNull(startTime))) {
				sqlData.append(" and xar.a_pubTime > DATE_FORMAT(?,'%Y-%m-%d %T')");
				params.add(startTime);
			}
			if (StringUtils.hasText(getNotNull(endTime))) {
				sqlData.append(" and xar.a_pubTime < DATE_FORMAT(?,'%Y-%m-%d %T')");
				params.add(endTime);
			}
		}
		sqlData.append(" order by xar.a_pubTime");
		StringBuffer datas = new StringBuffer();
		datas.append("<?xml version=\"1.0\" encoding=\"utf-8\"?><result><success><head><hasnext>");
		datas.append(hasNext);
		datas.append("</hasnext></head><body><docs>");
		DBSession conn = null;
		IResultSet rsData = null;
		try {
			conn = Context.getDBSession();
			String querySql = conn.getDialect().getLimitString(
					sqlData.toString(), start, size);
			// 如果size为-1则查询所有 否则按照分页查询
			rsData = conn.executeQuery(-1 == size ? sqlData.toString()
					: querySql, params.toArray());
			while (rsData.next()) {
				datas.append(this.madeDate(rsData, LibHelper.getArticleAppLibID()));
			}
			datas.append("</docs></body></success></result>");

		} catch (SQLException e) {
			throw new E5Exception(e);
		} finally {
			ResourceMgr.closeQuietly(rsData);
			ResourceMgr.closeQuietly(conn);
		}
		return datas.toString();
	}

	// 组装数据
	private String madeDate(IResultSet rsData,Integer docLibId) throws E5Exception {
		StringBuffer sb = new StringBuffer();
		try {
			
			
			sb.append("<doc id=\"");
			sb.append(getNotNull(rsData.getString("SYS_DOCUMENTID")));
			sb.append("\" uniqueid=\"");
			sb.append("\" isbatman=\"");
			sb.append(getNotNull(rsData.getString("isbatman")));
			sb.append("\">");
			sb.append("<docpuburl><![CDATA[");
			sb.append(getNotNull(rsData.getString("a_urlPad")));
			sb.append("]]></docpuburl>");
			sb.append("<docauthorid>");
			//此为写稿人id 不是作者id
			//sb.append(getNotNull(rsData.getString("SYS_AUTHORID")));
			sb.append("</docauthorid>");
			sb.append("<docauthor>");
			sb.append(getNotNull(rsData.getString("SYS_AUTHORS")));
			sb.append("</docauthor>");
			sb.append("<docauthors>");
			sb.append(getNotNull(rsData.getString("a_collaborator")));
			sb.append("</docauthors>");

			sb.append("<docEditorId>");
			sb.append("</docEditorId>");
			sb.append("<responsibilityEditorId>");
			sb.append("</responsibilityEditorId>");
			
			sb.append("<docEditor>");
			sb.append(getNotNull(rsData.getString("a_editor")));
			sb.append("</docEditor>");
			sb.append("<responsibilityEditor>");
			sb.append(getNotNull(rsData.getString("a_liability")));
			sb.append("</responsibilityEditor>");
			
			sb.append("<doctopic>");
			sb.append(getNotNull(rsData.getString("a_linkTitle")));
			sb.append("</doctopic>");
			sb.append("<docsubtopic>");
			sb.append(getNotNull(rsData.getString("a_subTitle")));
			sb.append("</docsubtopic>");
			sb.append("<docintrotopic>");
			sb.append(getNotNull(rsData.getString("a_leadTitle")));
			sb.append("</docintrotopic>");
			sb.append("<doccreated>");
			sb.append(getNotNull(rsData.getString("SYS_CREATED")));
			sb.append("</doccreated>");
			sb.append("<docpubtime>");
			sb.append(getNotNull(rsData.getString("a_pubTime")));
			sb.append("</docpubtime>");
			sb.append("<docsource>");
			sb.append(getNotNull(rsData.getString("a_source")));
			sb.append("</docsource>");
			sb.append("<docwords>");
			sb.append(getNotNull(rsData.getString("a_wordCount")));
			sb.append("</docwords>");
			sb.append("<exclusive>");
			sb.append(getNotNull(rsData.getString("a_isExclusive")));
			sb.append("</exclusive>");
			sb.append("<columnid>");
			sb.append(getNotNull(rsData.getString("a_columnID")));
			sb.append("</columnid>");
			sb.append("<columncode>");
			sb.append(getNotNull(rsData.getString("colCode")));
			sb.append("</columncode>");
			sb.append("<columnname>");
			sb.append(getNotNull(rsData.getString("a_column")));
			sb.append("</columnname>");
			sb.append("<firstColumnid>");
			sb.append(getNotNull(rsData.getString("a_columnIDF")));
			sb.append("</firstColumnid>");
			sb.append("<firstcolumncode>");
			sb.append(getNotNull(rsData.getString("colCodeF")));
			sb.append("</firstcolumncode>");
			sb.append("<firstcolumnname>");
			sb.append(getNotNull(rsData.getString("a_columnF")));
			sb.append("</firstcolumnname>");
			sb.append("<docviewcount>");
			sb.append(getNotNull(rsData.getString("a_countClick")));
			sb.append("</docviewcount>");
			sb.append("<doccommentcount>");
			sb.append(getNotNull(rsData.getString("a_countDiscuss")));
			sb.append("</doccommentcount>");
			sb.append("<forwardcount>");
			sb.append(getNotNull(rsData.getString("a_countShare")));
			sb.append("</forwardcount>");
			sb.append("<forwardviewcount>");
			sb.append(getNotNull(rsData.getString("a_countShareClick")));
			sb.append("</forwardviewcount>");
			sb.append("<content><![CDATA[");
			sb.append(getNotNull(rsData.getString("a_content")));
			sb.append("]]></content>");
			
			sb.append("<contentiterm ishaveattach=\"");
			sb.append(getNotNull(rsData.getString("SYS_HAVEATTACH"))==""?0:rsData.getString("SYS_HAVEATTACH"));
			sb.append("\">");
			sb.append(this.jsonAttachments(docLibId,rsData.getLong("SYS_DOCUMENTID")));
			sb.append("</contentiterm></doc>");
		} catch (SQLException e) {
			throw new E5Exception(e);
		}
		return sb.toString();
	}
	
	// 查询数量 用于返回是否已展示完所有数据
	private boolean isHasNext(int siteID,int page, int size, String startTime,
			String endTime) throws E5Exception {

		boolean hasNext = true;
		if (size != -1) {

			StringBuffer sqlCount = new StringBuffer("select count(1) from "
					+ LibHelper.getLibTable(LibHelper.getArticleAppLibID()) + " xar where xar.a_status = 1 and xar.a_copyright = 1 and xar.a_siteID = ? ");
			List<String> params = new ArrayList<String>();
			params.add(String.valueOf(siteID));

			if (StringUtils.hasText(getNotNull(startTime)) && StringUtils.hasText(getNotNull(endTime))) {
				sqlCount.append(" and xar.a_pubTime > DATE_FORMAT(?,'%Y-%m-%d %T')");
				sqlCount.append(" and xar.a_pubTime < DATE_FORMAT(?,'%Y-%m-%d %T')");
			
				params.add(startTime);
				params.add(endTime);
			} else {
				if (StringUtils.hasText(getNotNull(startTime))) {
					sqlCount.append(" and xar.a_pubTime > DATE_FORMAT(?,'%Y-%m-%d %T')");
					params.add(startTime);
				}
				if (StringUtils.hasText(getNotNull(endTime))) {
					sqlCount.append(" and xar.a_pubTime < DATE_FORMAT(?,'%Y-%m-%d %T')");
					params.add(endTime);
				}
			}

			DBSession conn = null;
			IResultSet rsCount = null;
			try {
				conn = Context.getDBSession();
				// 判断数据是否取完
				if (-1 != size) {
					rsCount = conn.executeQuery(sqlCount.toString(), params.toArray());
					while (rsCount.next()) {
						int count = rsCount.getInt(1);
						hasNext = count > page * size ? true : false;
					}
				}
			} catch (SQLException e) {
				throw new E5Exception(e);
			} finally {
				ResourceMgr.closeQuietly(rsCount);
				ResourceMgr.closeQuietly(conn);
			}
		}
		return hasNext;
	}

	/**
	 * 取互动的附件
	 */
	private String jsonAttachments(int articleLibID, long articleID) throws E5Exception {
		StringBuffer sb = new StringBuffer();
		int attLibID = LibHelper.getLibIDByOtherLib(DocTypes.ATTACHMENT.typeID(), articleLibID);
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] docs = docManager.find(attLibID, "att_articleID=? and att_articleLibID=?",
				new Object[]{articleID, articleLibID});
		sb.append("<attachs>");
		for (Document doc : docs) {
			sb.append(" <attach id=\"");
			sb.append(doc.getString("SYS_DOCUMENTID"));
			sb.append("\" type=\"");
			sb.append(doc.getString("att_type"));
			sb.append("\" location=\"1\">");
			
			sb.append("<atttopic></atttopic>");
			sb.append("<attsize></attsize>");
			sb.append("<attformat>");
			
			String att_url = getNotNull(doc.getString("att_urlPad"));
			String att_path = getNotNull(doc.getString("att_path"));
				
			//取附件后缀名
			if(att_path.indexOf("?")==-1){
				sb.append(att_path.substring(att_path.lastIndexOf(".")+1,att_path.length()));
			}else{
				int b = att_path.lastIndexOf(".")+1;
				int e = att_path.indexOf("?", att_path.lastIndexOf("."));
				sb.append(att_path.substring(b,e));	
			}
			
			
			
			sb.append("</attformat>");
			sb.append("<attpath><![CDATA[");
			sb.append(att_url);
			sb.append("]]></attpath>");
			sb.append("</attach>");
		}
		sb.append("</attachs>");
		return sb.toString();
	}
	
	private static String getNotNull( String input ) {
		return (( input == null || "null".equals(input)) ? "" : input );
	}
}
