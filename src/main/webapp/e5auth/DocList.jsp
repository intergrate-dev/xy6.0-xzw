<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5auth" changeResponseLocale="false"/>
<HTML>
<HEAD>
	<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
	<link type="text/css" rel="stylesheet" href="../e5style/style.css"/>
	<Script type="text/javascript" src="../e5script/Function.js"></Script>
	<Script type="text/javascript">
		function showPermission()
		{
			var theURL = "./treeFolderView.do?Type=Main&fvID=0&DocLibID=" + selDocLib.value;
			getIframe("FolderPermission").src = theURL;
		}

		function Init()
		{
			var ele = null;
			<c:forEach var="one" items="${doc.manageTypes}" varStatus="status1">
				ele = document.getElementById('<c:out value="${one}"/>');
				if (ele != null) ele.checked = true;
			</c:forEach>
			<c:forEach var="one" items="${doc.permTypes}" varStatus="status1">
				ele = document.getElementById('<c:out value="${one}"/>');
				if (ele != null) ele.checked = true;
			</c:forEach>
		}
	</Script>
	<Style>
		select{width:300px;}
		iframe{width:100%; height=95%;}
		#docLibDiv{
			background:	url(../images/menu_bg1.gif);
			height: 30px;
			border:	0px;
			padding-top : 3px;
			padding-left: 10px;
			margin: 0px;
			color: white;
		}
	</Style>
</HEAD>
<BODY 
	<c:if test="${not sessionScope.DocInfos.docType}">onload="showPermission()"</c:if> 
	<c:if test="${sessionScope.DocInfos.docType}">onload="Init()"</c:if> >

	<c:if test="${sessionScope.DocInfos.docType}">
		<IFrame id="iframe" name="iframe" style="display:none"></IFrame>
		<Form Name="otherForm"  Target="iframe" Action="./submitPart.do" Method="Post">
		<Table class="onlyborder">
			<Input Type="hidden" Name="Type"   Value="2">	
			<Input Type="hidden" Name="DocClass"   Value="DocType">
			<TR>
				<TD><i18n:message key="perm.title"/></TD>
				<TD><i18n:message key="perm.manage"/></TD>
				<TD><i18n:message key="perm.perm"/></TD>
			</TR>
			<c:forEach var="type" items="${sessionScope.FlowInfos.types}">
			<TR>
				<TD><c:out value="${type.docTypeName}"/></TD>
				<TD>
					<input type=checkbox name='<c:out value="${type.docTypeID}_1"/>' id='<c:out value="${type.docTypeID}_1"/>'/>
				</TD>
				<TD>
					<input type=checkbox name='<c:out value="${type.docTypeID}_2"/>' id='<c:out value="${type.docTypeID}_2"/>'/>
				</TD>
			</TR>
			</c:forEach>
		</Table>
		<DIV><input type="submit" value="<i18n:message key="button.submit"/>" class="button"></DIV>
		</FORM>
	</c:if>

	<c:if test="${not sessionScope.DocInfos.docType}">
		<div id="docLibDiv">
		<i18n:message key="folder.select"/>
		<Select Name="selDocLib" onchange="showPermission()">
			<c:forEach var="docLib" items="${sessionScope.DocInfos.doclibs}">
				<OPTION value="<c:out value="${docLib.docLibID}"/>"><c:out value="${docLib.docLibName}"/></OPTION>
			</c:forEach>
			<OPTION VALUE="-1"><i18n:message key="folder.allFolder"/></OPTION>
		</SELECT>
		</DIV><BR>
		<IFRAME Name="FolderPermission" id="FolderPermission" frameborder=0></Iframe>
	</c:if>
</BODY>
</HTML>
