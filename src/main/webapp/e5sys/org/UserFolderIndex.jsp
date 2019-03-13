<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5org" changeResponseLocale="false"/>
<html>
	<head>
		<title><i18n:message key="org.user.default.folder.title"/></title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link type="text/css" rel="StyleSheet" href="../../e5style/reset.css"/>
		<link type="text/css" rel="StyleSheet" href="../../e5style/sys-main-body-style.css"/>
		<script type="text/javascript" src="./js/xmlhttps.js"></script>
		<script language="javascript">
			var userid = "<c:out value="${userfolder.userID}"/>";
			var treeid = "<c:out value="${param.treeid}"/>";
			var oldFolderID = "<c:out value="${userfolder.folderID}"/>";
			function setFolder(){
				if(leftFrame.attr_id == -1)
				{
					alert("<i18n:message key="org.folder.tree.select.alert"/>");
					return;
				}
				if (oldFolderID == "-5")
				{
					urlsrc = "DefaultFolderAction.do?invoke=setFolder"
					+ "&OrgID=" + userid
					+ "&DocLibID=" + leftFrame.attr_id
					+ "&DocTypeID=<c:out value="${userfolder.docTypeID}"/>"
					+ "&FolderID=" + leftFrame.selid;
				}
				else
					urlsrc = "UserMgrFolderAction.do?invoke=setFolder"
						+ "&UserID=" + userid
						+ "&DocLibID=" + leftFrame.attr_id
						+ "&DocTypeID=<c:out value="${userfolder.docTypeID}"/>"
						+ "&FolderID=" + leftFrame.selid;
				var sInf = invokeGetXmlHttpDoForResponse(urlsrc);
				if(sInf == "1")
				{
					if(treeid == "-1")
					{
						document.location.href = "UserMgrAction.do?invoke=UserFormList&UserID=" + userid
							+ "&treeid=-1";
					}
					else
					{
						refreshPage();
						window.close();
					}
				}
				else
				{
					alert("<i18n:message key="org.user.set.default.folder"/>");
				}
			}
		</script>
	</head>
	<body>
		<div class="mainBodyWrap" style="margin:0">
			<table cellpadding="0" cellspacing="0" class="table">
				<caption id="nameDIV"><i18n:message key="org.user.default.folder.title"/></caption>
				<tr>
					<td>
						<iframe style="height:420px;width:300px;" name="leftFrame" id="leftFrame" frameborder="0" src="FolderTreeGenerate.do?invoke=folderRootNode&DocTypeID=<c:out value="${userfolder.docTypeID}"/>&UserID=<c:out value="${userfolder.userID}"/>"></iframe>
					</td>
					<td style="vertical-align:top; text-align:left;">
						<br/><br/>
						<input type='button' class="button" value='<i18n:message key="org.sort.submit"/>' onclick='setFolder()'>
						<br/><br/>
						<input type="button" class="button" value="<i18n:message key="org.sort.cancel"/>" onClick="javascript:window.close();">
					</td>
				</tr>
			</table>
		</div>
	</body>
</html>

