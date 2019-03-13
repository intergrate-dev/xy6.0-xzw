<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5org" changeResponseLocale="false"/>
<%@page pageEncoding="UTF-8"%>
<html> 
	<head>
		<title><i18n:message key="org.user.form.list.title"/></title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link type="text/css" rel="StyleSheet" href="../../e5style/reset.css"/>
		<link type="text/css" rel="StyleSheet" href="../../e5style/sys-main-body-style.css"/>
		<link type="text/css" rel="StyleSheet" href="../../e5style/e5sys-org-DefaultFolder.css"/>
		<script>
			function setFolder(doctypeid,userid){
				var folderUrl = "FolderTreeGenerate.do?invoke=folderDlg&DocTypeID=" + doctypeid
					+ "&UserID=" + userid
					+ "&FolderID=-5"; //以-5来表示机构缺省目录设置
				var wndTarget = "setDefaultFolder";
				var wndDefaultFolder = window.open(folderUrl, wndTarget, "width=420,height=520");
				wndDefaultFolder.focus();
			}
		</script>
	</head> 
	<body>
		<div class="mainBodyWrap">
			<table cellPadding="0" cellSpacing="0" class="table">
				<caption><i18n:message key="org.user.default.folder.title"/></caption>
				<tr>
					<th class="titleTD"><i18n:message key="org.user.default.doctype"/></th>
					<th><i18n:message key="org.user.default.folder"/></th>
					<th></th>
				</tr>
				<c:forEach items="${dfList}" var="folderinfo">
					<tr>
						<td class="titleTD">
							<c:out value="${folderinfo.docTypeName}"/>
						</td>
						<td class="fpth" alt="<c:out value="${folderinfo.folderNamePath}"/>" >
							<c:out value="${folderinfo.folderNamePath}"/>
						</td>
						<td>
							<input class="button" type="button" value="<i18n:message key="org.user.default.folder.setup"/>" onclick="setFolder('<c:out value="${folderinfo.docTypeID}"/>', '<c:out value="${folderinfo.userID}"/>')">
						</td>
					</tr>
				</c:forEach>
			</table>
		</div>
	</body> 
</html>