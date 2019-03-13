package com.founder.xy.article.trace;

import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.jsoup.helper.StringUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baidu.ueditor.define.BaseState;
import com.founder.allmedia.difftrace.Exception.TraceException;
import com.founder.allmedia.difftrace.bean.TraceVersion;
import com.founder.allmedia.difftrace.version.ReviseManifest;
import com.founder.allmedia.difftrace.version.ReviseManifestImpl;
import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.Log;
import com.founder.e5.commons.Pair;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.IClob;
import com.founder.e5.doc.DocID;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.doc.FlowRecord;
import com.founder.e5.doc.FlowRecordManager;
import com.founder.e5.web.SysUser;
import com.founder.e5.web.WebUtil;
import com.founder.e5.workspace.ProcHelper;

/**
 * 自采编系统移植的修改痕迹功能
 * @author Gong Lijie
 */
@Controller
@RequestMapping({ "/xy/ueditor" })
public class ArticleTraceController {
	String templatefile = "xy-template/articleTraceTemplate.html";
	
	private final static String title_label = "{founder:title}";
	private final static String trace_label = "{founder:trace_content}";
	private final static String version_label = "{founder:trace_version}";
	private final static String tip_label = "{founder:trace_tip}";
	private final static String style_label = "{founder:trace_style}";
	private final static String script_label = "{founder:poshytip}";
	private final static String topic_label = "{founder:topic}";
	private final static String subtopic_label = "{founder:subtopic}";
	private final static String introtopic_label = "{founder:introtopic}";
	private final static String openMode_label = "{founder:open_method}";

	private static final String[] COLORS = { "black", "red", "blue", "green",
			"#BF0060", "#9932CD", "#8E236B", "#32CD99", "#7F00FF", "deeppink",
			"cyan", "springgreen", "#CCCCCC" };
	
	private Log log = Context.getLog("xy");

	/**
	 * 新建版本
	 */
	@RequestMapping(value = "addVersion.do", method = RequestMethod.POST)
	@ResponseBody
	public String addVersion(HttpServletRequest request,
			HttpServletResponse response, int UserID, String UserName,
			long DocID, int DocLibID, String Note, String Content)
			throws Exception {
		ReviseManifest rm = new ReviseManifestImpl();
		TraceVersion ver = new TraceVersion();
		ver.setContent(Content);
		ver.setCreateTime(new Date());
		ver.setNote(Note);
		ver.setUserId(UserID);
		ver.setUserName(UserName);
		ver.setVersion(1);

		String oldVerXml = DocTraceUtils.getVerXml(DocID, DocLibID);
		if (StringUtils.isBlank(oldVerXml)) {
			oldVerXml = null;
		}
		int nextVer = rm.getNextVersion(oldVerXml);
		ver.setVersion(nextVer);
		String newXml = rm.updateVersion(oldVerXml, ver);
		DocTraceUtils.saveNewXml(newXml, DocID, DocLibID);
		return new BaseState(true).toJSONString();
	}

	/**
	 * 修改痕迹，在编辑器上看修改痕迹
	 */
	@RequestMapping(value = "getTrace.do")
	public void getTrace(HttpServletRequest req, HttpServletResponse res) {
		long docID = WebUtil.getLong(req, "docID", 0);
		int docLibID = WebUtil.getInt(req, "docLibID", 0);
		if (docID <= 0) {
			docID = WebUtil.getLong(req, "DocIDs", 0);
			docLibID = WebUtil.getInt(req, "DocLibID", 0);
		}
		getTrace(req, res, docID, docLibID, genFilterUserGenerateTrace());
	}

	@RequestMapping(value = "getFullTrace.do")
	public void getFullTrace(HttpServletRequest req, HttpServletResponse res,
			long docID, int docLibID) {
		getTrace(req, res, docID, docLibID, genFullGenerateTrace());
	}

	/**
	 * 作者自己的所有修改操作
	 */
	@RequestMapping(value = "getMyTrace.do")
	public void getMyTrace(HttpServletRequest req, HttpServletResponse res) {
		try {
			long DocID = WebUtil.getLong(req, "docID", 0);
			int DocLibID = WebUtil.getInt(req, "docLibID", 0);
			
			getMyTrace(req, res, DocID, DocLibID);
	
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 只获取两个版本的信息
	 */
	@RequestMapping(value = "getLastVersionTrace.do")
	public void getLastVersionTrace(HttpServletRequest req,
			HttpServletResponse res) {
		ReviseManifest rm = new ReviseManifestImpl();
		try {
			long DocID = WebUtil.getLong(req, "DocID", 0);
			int DocLibID = WebUtil.getInt(req, "DocLibID", 0);
			String versions = req.getParameter("versions");
			String reviseXml = DocTraceUtils.getVerXml(DocID, DocLibID);
	
			/* 修改为只比较不同人的修改痕迹 */
			String trace = "";
			if (reviseXml == null) {
				trace = getXhtml(DocID, DocLibID);
			} else {
				int[] verArray = StringUtils.getIntArray(versions);
				trace = rm.generateSelectedVersionTrace(reviseXml, verArray);
				if (verArray.length > 0) {
					trace += getBackGroundScript(verArray[verArray.length - 1]);
				}
			}
			writeTextServlet(res, trace);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	
	}

	/**
	 * 获取稿件原始内容。
	 */
	@RequestMapping(value = "getVersionXhtml.do")
	public void getVersionXhtml(HttpServletRequest req, HttpServletResponse res) {
		ReviseManifest rm = new ReviseManifestImpl();
		try {
			long DocID = WebUtil.getLong(req, "DocID", 0);
			int DocLibID = WebUtil.getInt(req, "DocLibID", 0);
			String version = req.getParameter("version");
			String reviseXml = DocTraceUtils.getVerXml(DocID, DocLibID);
			int v = Integer.parseInt(version);
	
			String xhtml = "";
			TraceVersion tv1 = rm.getVersion(reviseXml, v);
			if (tv1 != null) {
				xhtml = tv1.getXhtml();
				if (xhtml.contains("<p><p")) {
					xhtml = xhtml.replace("<p><p", "<p");
					xhtml = xhtml.replace("</p></p>", "</p>");
				}
				if (xhtml.contains("<br/>")) {
					xhtml = xhtml.replace("<br/>", "</p><p>");
				}
				if (xhtml.contains("<p>")) {
					xhtml = xhtml.replace("<p>",
						"<p style='width: 100%;margin: 0 0 10px;font-family: microsoft yahei;padding-top: 15px;'>");
				}
			}
			String url = "rollback.do?docID=" + DocID + "&docLibID="
					+ DocLibID + "&version=" + version;
			String backHtml = "<a href='" + url + "'>回滚</a>";
			int index = xhtml.lastIndexOf("</body>");
			if (index != -1) {
				xhtml = xhtml.substring(0, index) + backHtml
						+ xhtml.substring(index);
			} else {
				xhtml = xhtml + backHtml;
			}
			writeTextServlet(res, xhtml);
	
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@RequestMapping(value = "getSelectVersionTrace.do")
	public void compareTwoVersion(HttpServletRequest req,
			HttpServletResponse res) {
		long DocID = WebUtil.getLong(req, "DocID", 0);
		int DocLibID = WebUtil.getInt(req, "DocLibID", 0);
		String versions = req.getParameter("versions");
		String openMode = req.getParameter("openMode");
	
		ReviseManifest rm = new ReviseManifestImpl();
		try {
			String reviseXml = DocTraceUtils.getVerXml(DocID, DocLibID);
			if (openMode == null) {
				openMode = "getTrace.do";
			}
	
			/* 修改为只比较不同人的修改痕迹 */
			String xml = "";
			if (reviseXml == null) {
				xml = getXhtml(DocID, DocLibID);
			} else {
				int[] verArray = StringUtils.getIntArray(versions);
				String trace = rm.generateSelectedVersionTrace(reviseXml,
						verArray);
	
				List<TraceVersion> list = new ArrayList<TraceVersion>();
				for (int ver : verArray) {
					TraceVersion tv = rm.getVersion(reviseXml, ver);
					if (tv != null) {
						list.add(tv);
					}
				}
				xml = generateTraceHtml(new DocID(DocLibID, DocID), trace,
						list, true, openMode, false);
				writeTextServlet(res, xml);
			}
	
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	
	}

	/**
	 * 获取修改痕迹xml文件
	 */
	@RequestMapping(value = "getVersionXml.do", method = RequestMethod.POST)
	public void getVersionXml(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		long docID = WebUtil.getLong(req, "docID", 0);
		int docLibID = WebUtil.getInt(req, "docLibID", 0);
		String xml = DocTraceUtils.getVerXml(docID, docLibID);
		if (xml != null) {
			org.dom4j.Document xmlDoc = DocumentHelper.parseText(xml);
			Element ej = (Element) xmlDoc
					.selectSingleNode("traceManifest/ori-xhtmls");
			if (ej != null) {
				xmlDoc.remove(ej);
			}
			ej = (Element) xmlDoc.selectSingleNode("traceManifest/contents");
			if (ej != null) {
				xmlDoc.remove(ej);
			}
			xml = xmlDoc.asXML();
			writeTextServlet(res, xml);
		}
	}

	@RequestMapping(value = "getContent.do")
	public void getContentXhtml(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		long docID = WebUtil.getLong(req, "docID", 0);
		int docLibID = WebUtil.getInt(req, "docLibID", 0);
		String version = req.getParameter("version");
		String xhtml = "";
		String xml = DocTraceUtils.getVerXml(docID, docLibID);
		if (!StringUtil.isBlank(xml)) {
			try {
				org.dom4j.Document xmlDoc = DocumentHelper.parseText(xml);
				Element ej = (Element) xmlDoc
						.selectSingleNode("traceManifest/contents/content[@verId='"
								+ version + "']");
				if (ej != null) {
					String text = ej.getText();
					xhtml = textToHTML(text);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		writeTextServlet(res, xhtml);
	}

	@RequestMapping(value = "rollback.do")
	public void rollbackVersion(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		String xhtml = getOriXhtml(req);
		if (xhtml == null) {
			writeTextServlet(res, "0");
			return;
		}
		long docID = WebUtil.getLong(req, "docID", 0);
		int docLibID = WebUtil.getInt(req, "docLibID", 0);
	
		DocumentManager docMgr = DocumentManagerFactory.getInstance();
		Document doc = docMgr.get(docLibID, docID);
		if (doc != null) {
			SysUser sysUser = ProcHelper.getUser(req);
			
			doc.setClob("a_content", xhtml);
			//String newXml = traceWhenRollback(xhtml, doc, sysUser);
			//doc.setBlob("a_trace", newXml.getBytes("UTF-16"));//修改痕迹中记录回滚
			docMgr.save(doc);
			
			String version = req.getParameter("version");
			createRevokeLog(doc, "版本回滚", sysUser, "回滚至版本" + version);
			writeTextServlet(res, "<script>alert('回滚版本成功');window.close();</script>");
			return;
		} else {
			writeTextServlet(res, "<script>alert('回滚版本失败');window.close();</script>");
		}
	}

	@RequestMapping(value = "getXhtml.do", method = RequestMethod.POST)
	public void getOriXhtml(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
	
		boolean isoriXhtml = true;
		String xhtml = getOriXhtml(req);
		if (xhtml == null) {
			getContentXhtml(req, res);
			isoriXhtml = false;
		}
		if (isoriXhtml) {
			int i = 0;
			Pattern maindocXMLPattern = Pattern.compile("<p[^<>]*>");
			Matcher maindocXMLMatcher = maindocXMLPattern.matcher(xhtml);
			while (maindocXMLMatcher.find()) {
				i++;
			}
			if (i > 0) {
				writeTextServlet(res, xhtml);
			} else {
				String value = xhtml.replaceAll("\n", "<br/>");
				//DocViewController.replaceTxtBlank()
				writeTextServlet(res, value);
			}
		}
	}
	
	@SuppressWarnings("unused")
	private String traceWhenRollback(String xhtml, Document doc, SysUser sysUser)
			throws TraceException {
		ReviseManifest rm = new ReviseManifestImpl();
		TraceVersion ver = new TraceVersion();
		ver.setContent(HTMLUtil.xhtml2Text(xhtml));
		ver.setCreateTime(new Date());
		ver.setNote("回滚版本");
		ver.setUserId(sysUser.getUserID());
		ver.setUserName(sysUser.getUserName());
		ver.setTopic(doc.getTopic());
		ver.setIntroTopic(doc.getString(""));
		ver.setSubTopic(doc.getString(""));
		ver.setVersion(1);
		ver.setXhtml(xhtml);
	
		String oldVerXml = null;
		oldVerXml = DocTraceUtils.getVerXml(doc);
		if (StringUtils.isBlank(oldVerXml)) {
			oldVerXml = null;
		}
		int nextVer = rm.getNextVersion(oldVerXml);
		ver.setVersion(nextVer);
	
		String newXml = rm.updateVersion(oldVerXml, ver);
		return newXml;
	}

	private String getXhtml(long docID, int docLibID) throws E5Exception {
		String xml = "";
		Document doc = DocumentManagerFactory.getInstance()
				.get(docLibID, docID);
		if (doc != null) {
			IClob clob = doc.getClob("a_content");
			if (clob != null) {
				xml = clob.toString();
				//xml = textToHTML(xml);
			}
		}
		return xml;

	}

	private void getTrace(HttpServletRequest req, HttpServletResponse res,
			long docID, int docLibID, GenerateTrace traceGen) {

		try {
			String reviseXml = DocTraceUtils.getVerXml(docID, docLibID);

			/* 修改为只比较不同人的修改痕迹 */
			String xml = "";
			if (reviseXml == null) {
				xml = getXhtml(docID, docLibID);
				xml += "<script>alert('没有修改痕迹');window.close();</script>";
			} else {
				String trace = traceGen.getTrace(reviseXml);
				List<TraceVersion> list = traceGen
						.getTraceVersionList(reviseXml);
				xml = generateTraceHtml(new DocID(docLibID, docID), trace,
						list, true, traceGen.getOpenMethod(), true);
			}
			if (StringUtil.isBlank(xml)) {
				xml = "<p>(无内容)</p>";
			}
			writeTextServlet(res, xml);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private String getColor(int index) {
		int colorIndex = index;
		if (colorIndex > COLORS.length - 1) {
			colorIndex = index % COLORS.length;
		}
		if (colorIndex < 0) {
			colorIndex = 0;
		}
		return COLORS[colorIndex];
	}

	private void getMyTrace(HttpServletRequest req, HttpServletResponse res,
			long DocID, int DocLibID) {
		ReviseManifest rm = new ReviseManifestImpl();
		try {
			int userID = ProcHelper.getUserID(req);
			String reviseXml = DocTraceUtils.getVerXml(DocID, DocLibID);

			/* 修改为只比较不同人的修改痕迹 */
			String xml = "";
			if (reviseXml == null) {
				xml = getXhtml(DocID, DocLibID);
			} else {

				String trace = rm.generateSameUserTrace(reviseXml, userID);
				List<TraceVersion> list = rm.getSameUserVersionList(reviseXml,
						userID);
				xml = generateTraceHtml(new DocID(DocLibID, DocID), trace,
						list, true, "getMyTrace.do", false);// 我的修改 更改为默认显示修改者
				writeTextServlet(res, xml);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	private String getBackGroundScript(int ver) {
		StringBuffer buf = new StringBuffer();
		buf.append("<script>")
				.append("$('.ex_rv" + ver + "').css('color','#666666');")
				.append(("</script>"));
		return buf.toString();

	}

	/**
	 * 鼠标放到修改位置显示的内容。
	 */
	private String getScriptFromTrace(String trace, String op, String opName) {
		String label = "<" + op + " id=\"";
		StringBuffer scriptBuf = new StringBuffer();
		int index = trace.indexOf(label);
		while (index != -1) {
			if (index == -1) {
				break;
			}
			index += label.length();
			int index2 = trace.indexOf("\"", index);
			if (index2 != -1) {
				String id = trace.substring(index, index2);
				scriptBuf.append("$('#").append(id).append("').poshytip({")
						.append("className: 'tip-yellowsimple',")
						.append("content: ").append("vers[$('#").append(id)
						.append("').attr('rid')]+' ").append(opName)
						.append("'").append("});\r\n");
			}

			index = trace.indexOf(label, index);

		}
		return scriptBuf.toString();
	}

	private String generateTraceHtml(DocID docID, String trace,
			List<TraceVersion> list, boolean isShowUser, String openMode,
			boolean isShowTip) {
		DocumentManager docMgr = DocumentManagerFactory.getInstance();
		Title title = null;
		try {
			Document doc = docMgr.get(docID.docLibID, docID.docID);
			title = new Title(doc.getTopic(), doc.getString("a_subTitle"),
					doc.getString("a_leadTitle"));

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		StringBuffer buf = new StringBuffer();
		StringBuffer tipBuf = new StringBuffer();
		StringBuffer styleBuf = new StringBuffer();
		StringBuffer scriptBuf = new StringBuffer();

		if (isShowTip) {
			scriptBuf.append(getScriptFromTrace(trace, "ins", "增加"));
			scriptBuf.append(getScriptFromTrace(trace, "del", "删除"));
		}

		tipBuf.append("var docLibID=\"").append(docID.docLibID).append("\";");
		tipBuf.append("var docID=\"").append(docID.docID).append("\";");
		tipBuf.append("var vers={};");
		buf.append("<ul id=\"verUl\">");
		int i = 0;
		for (TraceVersion ver : list) {
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			String szDate = format.format(ver.getCreateTime());
			tipBuf.append("vers[").append(ver.getVersion()).append("]=\"")
					.append(szDate).append(",版本：").append(ver.getVersion())
					.append("  ");
			if (isShowUser) {
				tipBuf.append(" 操作者：").append(ver.getUserName());
			}
			tipBuf.append("\";");

			buf.append("<li version=\"").append(ver.getVersion()).append("\"")
					.append(" class=\"rv").append(ver.getVersion())
					.append("\">");
			buf.append("<input type=checkbox class=\"rvCheckBox").append("\">");
			buf.append("</input>");
			if (isShowUser) {
				buf.append("<span class=\"rvName").append("\">");
				buf.append(ver.getUserName());
				buf.append("</span>");
			}
			buf.append("<span class=\"rvTime").append("\">");
			buf.append(szDate);
			buf.append("</span>");

			buf.append("<span class=\"rvOriContentClass\" version=\"")
					.append(ver.getVersion()).append("\">");
			if(ver.getVersion() == 1) buf.append("原稿") ;
			buf.append("</span>");

			buf.append("</li>");
			styleBuf.append(".rv").append(ver.getVersion()).append("{ color: ")
					.append(getColor(i)).append("; } ").append("\r\n");
			styleBuf.append(".ex_rv").append(ver.getVersion())
					.append("{ color: ").append(getColor(i)).append("; } ")
					.append("\r\n");
			i++;
		}

		buf.append("</ul>");

		if (title == null) {
			title = new Title("", "", "");
		}
		String pageTitle = "修改痕迹";
		if (!StringUtils.isBlank(openMode) && openMode.equals("getMyTrace.do"))
			pageTitle = "我的修改";
		else if (!isShowTip) {
			pageTitle = "只比较内容差别，不表达选择人员改动情况";
		} else if (openMode.equals("getTrace.do") && isShowTip) {
			pageTitle = "不同人的修改比较";
		} else if (openMode.equals("getFullTrace.do")) {
			pageTitle = "所有版本的修改痕迹";
		}
		int index = trace.indexOf("[标题]");
		if (index == -1)
			index = trace.length() - 1;
		String headString = trace.substring(0, index);

		int index2 = headString.indexOf("[引题]");
		String modTrace = trace;
		if (index2 != -1) {
			StringBuffer modbuf = new StringBuffer();
			modbuf.append(headString.substring(0, index2)).append("　　")
					.append(headString.substring(index2))
					.append(trace.substring(index));
			modTrace = modbuf.toString();
		}

		Pair[] labelExs = new Pair[] {
				new Pair(title_label, pageTitle),
				new Pair(trace_label, modTrace),
				new Pair(version_label, buf.toString()),
				new Pair(tip_label, tipBuf.toString()),
				new Pair(style_label, styleBuf.toString()),
				new Pair(script_label, scriptBuf.toString()),
				new Pair(topic_label, title.topic),
				new Pair(subtopic_label, title.subTopic),
				new Pair(introtopic_label, title.introTopic),
				new Pair(openMode_label, openMode) };
		TemplateService templateService = (TemplateService) Context.getBean("TemplateService");
		String xml = templateService.getResultHtml(labelExs, templatefile);
		return xml;
	}

	private String getOriXhtml(HttpServletRequest req) {
		long docID = WebUtil.getLong(req, "docID", 0);
		int docLibID = WebUtil.getInt(req, "docLibID", 0);
		String version = req.getParameter("version");
		String xhtml = null;
		String xml = DocTraceUtils.getVerXml(docID, docLibID);
		if (!StringUtil.isBlank(xml)) {
			try {
				org.dom4j.Document xmlDoc = DocumentHelper.parseText(xml);
				Element ej = (Element) xmlDoc
						.selectSingleNode("traceManifest/ori-xhtmls/xhtml[@verId='"
								+ version + "']");
				if (ej != null) {
					xhtml = ej.getText();
				} else {
					return null;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return xhtml;
	}

	private void writeTextServlet(HttpServletResponse response,
			String text) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		response.setHeader("Cache-Control", "no-store");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);
		Writer writer = response.getWriter();
		writer.write(text);
		writer.flush();
		writer.close();
	}

	private void createRevokeLog(Document doc, String opName,
			SysUser user, String msg) {

		// 4、记录日志
		FlowRecord fr = new FlowRecord();
		fr.setCurrentFlowNode(doc.getCurrentFlow());// 当前节点的ID
		fr.setLastFlowNode(0); // 上一个流程节点的ID
		fr.setToPosition(""); // 当前流程节点的名称
		fr.setFromPosition(""); // 上一个流程节点的名称
		fr.setOperation(opName); // 操作名称
		fr.setOperator(user.getUserName()); // 操作人
		fr.setOperatorID(user.getUserID()); // 操作人ID

		fr.setDetail(msg); // 操作意见
		fr.setStartTime(DateUtils.getTimestamp()); // 操作开始时间
		fr.setEndTime(DateUtils.getTimestamp()); // 操作结束时间
		try {
			FlowRecordManager recordManager = (FlowRecordManager) Context
					.getBean(FlowRecordManager.class);
			recordManager.createFlowRecord(doc, fr);
		} catch (E5Exception e) {
			log.error("添加新流程记录时异常！", e);
		}
	}

	/**
	 * 将纯文本转换成html
	 */
	private String textToHTML(String text) {
		if (text == null || text.trim().length() == 0)
			return text;
	
		text = text.replaceAll("&", "&amp;");
		text = text.replaceAll("<", "&lt;");
		text = text.replaceAll(">", "&gt;");
		text = text.replaceAll("\"", "&quot;");
		text = text.replaceAll(" ", "&nbsp;");
		text = text.replaceAll("\r\n", "\n");
		text = text.replaceAll("\n    ", "<br/>　　　　");
		text = text.replaceAll("\n  ", "<br/>　　");
		text = text.replaceAll("\n", "<br/>");
	
		return text;
	}

	private GenerateTrace genFilterUserGenerateTrace() {
		return new GenerateTrace() {
			ReviseManifest rm = new ReviseManifestImpl();
	
			public List<TraceVersion> getTraceVersionList(String reviseXml)
					throws TraceException {
				return rm.getAlternateUserTraceVersionList(reviseXml);
	
			}
	
			public String getTrace(String reviseXml) throws TraceException {
				return rm.generateAlternateUserTrace(reviseXml);
			}
	
			public String getOpenMethod() {
				return "getTrace.do";
			}
		};
	}

	private GenerateTrace genFullGenerateTrace() {
		return new GenerateTrace() {
			ReviseManifest rm = new ReviseManifestImpl();
	
			public List<TraceVersion> getTraceVersionList(String reviseXml)
					throws TraceException {
				return rm.getTraceVersionList(reviseXml);
	
			}
	
			public String getTrace(String reviseXml) throws TraceException {
				return rm.generateTrace(reviseXml);
			}
	
			public String getOpenMethod() {
				return "getFullTrace.do";
			}
		};
	}
}
interface GenerateTrace {
	List<TraceVersion> getTraceVersionList(String reviseXml)
			throws TraceException;

	String getTrace(String reviseXml) throws TraceException;

	String getOpenMethod();
}
class Title {
	public String topic = "";
	public String subTopic = "";
	public String introTopic = "";

	public Title(String topic, String subTopic, String introTopic) {
		if (topic != null) {
			this.topic = topic;
		}
		if (subTopic != null) {
			this.subTopic = subTopic;
		}
		if (introTopic != null) {
			this.introTopic = introTopic;
		}
	}
}