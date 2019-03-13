<%@include file="../../e5include/IncludeTag.jsp"%>
<%@page import="com.founder.e5.cat.*"%>
<%@page import="com.founder.e5.context.*"%>
<%
	response.setHeader("Cache-Control","no-store");
	response.setHeader("Pragma","no-cache");
	response.setDateHeader("Expires",0);
%>
<%
	if (request.getParameter("catIDs") != null)
	{
		response.sendRedirect("../../e5workspace/after.do?UUID=" + request.getParameter("UUID")
		+ "&DocIDs=" + request.getParameter("DocIDs"));
		return;
	}
	CatReader catReader = (CatReader)Context.getBean(CatReader.class);
	int catTypeID = 0;

	CatType catType = catReader.getType("分类");
	if (catType != null) catTypeID = catType.getCatType();
%>
<HTML>
<HEAD>
<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
<TITLE>修改</TITLE>
	<link rel="stylesheet" type="text/css" href="../../e5style/work.css">
</HEAD>
<body>
<div id="nameDiv" style="height:30px;">
这里只演示分类选择树的功能，不做实际的修改动作。
</div>
<div id=catCont style="position:absolute;visibility:hidden" >
	<iframe frameborder="0" style="height:460px;width:400px;border-style:window-inset;" id="catSel"></iframe>
</div>
<iframe name="iframe" style="display:none"></iframe>
<form name="postForm" action="./DocEdit.jsp" target="iframe">
分类：
	<Input Type="Text" Name="catNames" Value="" readonly>
	<Input Type="hidden" Name="catIDs" Value="">
	<BUTTON onclick="doCategory()">打开分类选择树</BUTTON>

	<Input Type="hidden" Name="DocIDs" Value="<c:out value="${param.DocIDs}"/>">
	<Input Type="hidden" Name="UUID" Value="<c:out value="${param.UUID}"/>">
	<br/>
	<hr />
	<input type="submit" value="提交" class="button"/>
</form>

<script type="text/javascript">
//选择分类
function doCategory()
{
	var catIDs = transformCatIDs(postForm["catIDs"].value);
	var theURL = "./CatSelect.do?catType=<%=catTypeID%>&catIDs=" + catIDs;
	window.frames("catSel").location=theURL;
	var cont = document.getElementById("catCont");
	cont.style.left = 70;
	cont.style.top = 35;
	cont.style.visibility="visible";
	cont.focus();
}
/**
 * 处理分类ID
 * direct = 0: 在数据库中以21_233_222;11_333_22的形式存在

 * 在传递给分类选择时，改成222,22。只要最后的ID
 * direct = 1: 从分类选择返回时，级联串是21~233~222,11~333~22的形式

 * 改成21_233_222;11_333_22的形式

 *
 */
function transformCatIDs(catIDs, direct)
{
	if (!catIDs) return catIDs;

	if (direct) { //从分类选择返回
		catIDs = catIDs.replace(/~/g, "_");
		catIDs = catIDs.replace(/,/g, ";");
	}
	else {
		var catIDArr = catIDs.split(";");
		catIDs = "";
		for (var i = 0; i < catIDArr.length; i++)
		{
			var idArr = catIDArr[i].split("_");
			if ((idArr.length > 0) && idArr[idArr.length - 1])
				catIDs += idArr[idArr.length - 1] + ",";
		}
		if (catIDs) catIDs = catIDs.substring(0, catIDs.length - 1);
	}
	return catIDs;
}
//选择分类窗口点“取消”

function catWindowHidden()
{
	document.getElementById("catCont").style.visibility="hidden";
}
//选择分类窗口点“确定”

function catWindowSelect(catIDs, catNames, cascadeIDs)
{
	var ids = transformCatIDs(cascadeIDs, 1);
	postForm["catIDs"].value = ids;
	postForm["catNames"].value = catNames;
}
</script>
</body>
</html>
