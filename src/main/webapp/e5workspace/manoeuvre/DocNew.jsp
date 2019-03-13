<%@include file="../../e5include/IncludeTag.jsp"%>
<%@page contentType="text/html;charset=UTF-8"%>
<%
	request.setCharacterEncoding("UTF-8");
	response.setContentType("text/html;charset=UTF-8");

	response.setHeader("Cache-Control","no-store");
	response.setHeader("Pragma","no-cache");
	response.setDateHeader("Expires",0);
%>
<%@page import="com.founder.e5.doc.*"%>
<%@page import="com.founder.e5.flow.*"%>
<%@page import="com.founder.e5.dom.*"%>
<%@page import="com.founder.e5.context.*"%>
<%@page import="com.founder.e5.workspace.param.*"%>
<%@page import="java.sql.*"%>
<%
	String topic = request.getParameter("topic");
	if (topic != null)
	{
		int docLibID = Integer.parseInt(request.getParameter("DocLibID"));
		int folderID = Integer.parseInt(request.getParameter("FVID"));

		int currentFlow = Integer.parseInt(request.getParameter("FlowID"));
		int currentNode = Integer.parseInt(request.getParameter("FlowNodeID"));
		int currentUserID = 1;

		String authors = request.getParameter("author");
		String currentUsername = "ddd";

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = docManager.newDocument(docLibID);

		doc.setAuthors(authors);
		doc.setTopic(topic);
		doc.setCurrentFlow(currentFlow);
		doc.setCurrentNode(currentNode);
		doc.setCurrentStatus(request.getParameter("FlowStatus"));

		doc.setCurrentUserID(currentUserID);
		doc.setCurrentUserName(currentUsername);

		doc.setFolderID(folderID);
		doc.setDeleteFlag(0);
		doc.setLocked(false);
		doc.setCreated(new Timestamp((System.currentTimeMillis())));
		doc.setLastmodified(new Timestamp((System.currentTimeMillis())));

		docManager.save(doc);
		response.sendRedirect("../../e5workspace/after.do?UUID=" + request.getParameter("UUID")
				+ "&DocLibID=0&DocIDs=" + doc.getDocID());
	}
	int flowID = 0;
	int flowNodeID = 0;
	String status = null;
	int docLibID = Integer.parseInt(request.getParameter("DocLibID"));
	DocLibReader docLibReader = (DocLibReader)Context.getBean(DocLibReader.class);
	int docTypeID = docLibReader.get(docLibID).getDocTypeID();

	FlowReader flowReader = (FlowReader)Context.getBean(FlowReader.class);
	Flow[] flows = flowReader.getFlows(docTypeID);
	if (flows != null)
	{
		flowID = flows[0].getID();
		FlowNode[] nodes = flowReader.getFlowNodes(flowID);
		if (nodes != null)
		{
			flowNodeID = nodes[0].getID();
			status = nodes[0].getWaitingStatus();
		}
	}
	ProcParam param = (ProcParam)session.getAttribute(request.getParameter("UUID"));
	String userName = param.getUserName();
%>
<html>
<head>
	<title>New document--Just for demo</title>
	<link type="text/css" rel="stylesheet" href="../../e5style/work.css"/>
	<style type="text/css">body{margin:0px}</style>
</head>
<body>
<div id="nameDiv" style="height:25px;">New Document</div>
<FORM METHOD=POST ACTION="./DocNew.jsp" style="margin:0px;">
<TABLE>
<TR>
	<TD>标题:</TD>
	<TD><INPUT TYPE="text" NAME="topic" style="width: 300px;" value="DocNew Test..."></TD>
</TR>
<TR>
	<TD>作者:</TD>
	<TD><INPUT TYPE="text" NAME="author" value="<%=userName%>"></TD>
</TR>
<TR>
	<TD colspan="2"><hr /></TD>
</TR>
<TR>
	<TD>当前流程:</TD>
	<TD><INPUT TYPE="text" NAME="FlowID" readonly value="<%=flowID%>"><BR></TD>
</TR>
<TR>
	<TD>当前节点:</TD>
	<TD><INPUT TYPE="text" NAME="FlowNodeID" readonly value="<%=flowNodeID%>"><BR></TD>
</TR>
<TR>
	<TD>当前状态:</TD>
	<TD><INPUT TYPE="text" NAME="FlowStatus" readonly value="<%=status%>"><BR></TD>
</TR>
<TR>
	<TD colspan="2"><hr /></TD>
</TR>
<TR>
	<TD>文档库ID:</TD>
	<TD><INPUT TYPE="text" NAME="DocLibID" readonly value="<%=docLibID%>"></TD>
</TR>
<TR>
	<TD>当前文件夹ID:</TD>
	<TD><INPUT TYPE="text" NAME="FVID" readonly value="<c:out value="${param.FVID}"/>"><BR></TD>
</TR>
<TR>
	<TD>UUID:</TD>
	<TD><INPUT TYPE="text" NAME="UUID" readonly value="<c:out value="${param.UUID}"/>"></TD>
</TR>
<TR>
	<TD colspan="2"><hr /></TD>
</TR>
</TABLE>
<INPUT TYPE="submit" class="button" value="提交">
</FORM>
</body>
</html>
