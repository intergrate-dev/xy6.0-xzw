<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="com.founder.e5.doc.*"%>
<%@page import="com.founder.e5.dom.*"%>
<%@page import="com.founder.e5.context.*"%>
<%
	request.setCharacterEncoding("UTF-8");
	response.setContentType("text/html;charset=UTF-8");

	response.setHeader("Cache-Control","no-store");
	response.setHeader("Pragma","no-cache");
	response.setDateHeader("Expires",0);

    int DocLibID= Integer.parseInt(request.getParameter("DocLibID"));
	int docID = Integer.parseInt(request.getParameter("DocIDs"));

	DocumentManager docManager= DocumentManagerFactory.getInstance();
	Document doc = docManager.get(DocLibID, docID);

	DocLibReader docLibReader = (DocLibReader)Context.getBean(DocLibReader.class);
	DocLib docLib = docLibReader.get(DocLibID);

	FolderReader fvReader = (FolderReader)Context.getBean(FolderReader.class);
	FolderView fv = fvReader.get(doc.getFolderID());
%>
<HTML>
<HEAD>
	<title>显示位置信息</title>
	<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
</HEAD>
<link rel="stylesheet" type="text/css" href="../../e5style/work.css">
<BODY style="margin:0px;">
<div class="bluetd" style="height:36px;">
	<br/><font color="white">位置信息显示</font>
</div><br/>
<Table width="100%">
	<TR>
		<td>所在文档库：</td>
		<TD><Font  color="#FF0000"><%=docLib.getDocLibName()%></Font></TD>
	</TR>
	<TR bgcolor="#FFFFDD">
		<td>所在文件夹：</td>
		<TD><Font  color="#FF0000"><%=fv.getFVName()%></Font></TD>
	</TR>
	<TR>
		<td>当前/最后处理人：</td>
		<TD><Font  color="#FF0000">[<%=doc.getCurrentStatus()%>]<%=doc.getCurrentUserName()%></Font></TD>
	</TR>
	<TR><TD>&nbsp;</TD></TR>
	<TR>
		<TD></TD>
		<TD><Input Type="Button" Value="关闭" Name= "btnClose" onclick="doClose()" Class="Button"></TD>
	</TR>
</Table>
<Script type="text/javascript">
	function doClose(){
	    window.returnValue="OK";
	    window.close();
	}
</Script>
</BODY>
</HTML>
