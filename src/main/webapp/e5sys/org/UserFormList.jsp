<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5org" changeResponseLocale="false"/>
<html> 
<head>
	<title><i18n:message key="org.user.form.list.title"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<script type="text/javascript" src="./js/xmlhttps.js"></script>
	<link type="text/css" rel="StyleSheet" href="../../e5style/reset.css"/>
	<link type="text/css" rel="StyleSheet" href="../../e5style/sys-main-body-style.css"/>
	<link type="text/css" rel="StyleSheet" href="../../e5style/e5sys-org-UserFormList.css"/>
	<style type="text/css">.hidden{display:none;}</style>
</head> 
<body style="overflow:auto;" scroll="yes">
	<div class="mainBodyWrap">
	<form name="userform" action="UserMgrFormAction.do"  method="post">
		<c:if test="${usermap.userinfo.treeid=='-1'}">
			<script> 
			//以下从检索结果进行修改，则关闭窗口
				var opType="<c:out value="${usermap.userinfo.opType}"/>";
				if(opType=="update")
				{
					alert("<i18n:message key="org.query.user.refresh.alert"/>");
					window.close();
				}
			</script>
			<input type="button" class="button" value="<i18n:message key="org.user.form.list.return"/>" onClick="window.close();">
		</c:if>
		<table cellPadding="0" cellSpacing="0" class="table">
			<tr>
				<th class="titleTD"><i18n:message key="org.user.name"/></th>
				<th><c:out value="${usermap.userinfo.userName}"/></th>
			</tr>
			<tr>
				<td class="titleTD"><i18n:message key="org.user.form.code"/></td>
				<td><c:out value="${usermap.userinfo.userCode}"/></td>
			</tr>
			<tr>
				<td class="titleTD"><i18n:message key="org.user.form.orgname"/></td>
				<td><c:out value="${usermap.userinfo.orgName}"/></td>
			</tr>
			<tr>
				<td class="titleTD"><i18n:message key="org.user.form.email"/></td>
				<td><c:out value="${usermap.userinfo.emailAddress}"/></td>
			</tr>
			<tr>
				<td class="titleTD"><i18n:message key="org.user.form.address"/></td>
				<td><c:out value="${usermap.userinfo.address}"/></td>
			</tr>
			<tr>
				<td class="titleTD"><i18n:message key="org.user.form.postcode"/></td>
				<td><c:out value="${usermap.userinfo.postCode}"/></td>
  			</tr>
			<tr>
				<td class="titleTD"><i18n:message key="org.user.form.bpnumber"/></td>
				<td><c:out value="${usermap.userinfo.callNumber}"/></td>
			</tr>
			<tr>
				<td class="titleTD"><i18n:message key="org.user.form.homenumber"/></td>
				<td><c:out value="${usermap.userinfo.telHomeNumber}"/></td>
			</tr>
			<tr>
				<td class="titleTD"><i18n:message key="org.user.form.officenumber"/></td>
				<td><c:out value="${usermap.userinfo.telOffNumber}"/></td>
			</tr>
			<tr>
				<td class="titleTD"><i18n:message key="org.user.form.mobilenumber"/></td>
				<td><c:out value="${usermap.userinfo.handSetNumber}"/></td>
  			</tr>
			<!-- 扩展属性 -->
			<tr class="hidden">
				<td class="titleTD"><i18n:message key="org.user.form.property1"/></td>
				<td><c:out value="${usermap.userinfo.property1}"/></td>
  			</tr>
			<tr class="hidden">
				<td class="titleTD"><i18n:message key="org.user.form.property2"/></td>
				<td><c:out value="${usermap.userinfo.property2}"/></td>
  			</tr>
			<tr class="hidden">
				<td class="titleTD"><i18n:message key="org.user.form.property3"/></td>
				<td><c:out value="${usermap.userinfo.property3}"/></td>
  			</tr>
			<tr class="hidden">
				<td class="titleTD"><i18n:message key="org.user.form.property4"/></td>
				<td><c:out value="${usermap.userinfo.property4}"/></td>
  			</tr>
			<tr class="hidden">
				<td class="titleTD"><i18n:message key="org.user.form.property5"/></td>
				<td><c:out value="${usermap.userinfo.property5}"/></td>
  			</tr>
			<tr class="hidden">
				<td class="titleTD"><i18n:message key="org.user.form.property6"/></td>
				<td><c:out value="${usermap.userinfo.property6}"/></td>
  			</tr>
			<tr class="hidden">
				<td class="titleTD"><i18n:message key="org.user.form.property7"/></td>
				<td><c:out value="${usermap.userinfo.property7}"/></td>
  			</tr>
			<!-- end.扩展属性 -->
		</table>

		<table cellPadding="0" cellSpacing="0" class="table mt">
			<tr>
				<th><i18n:message key="role.name"/></th>
				<th><i18n:message key="org.role.orgpath"/></th>
				<th><i18n:message key="org.user.role.valid.time"/></th>
				<th><i18n:message key="org.role.status"/></th>
				<th> </th>
			</tr>
			<c:forEach items="${usermap.roleorg}" var="roleinfo">
			<tr>
				<td alt="<c:out value="${roleinfo.roleName}"/>"><c:out value="${roleinfo.roleName}"/></td>
				<td><c:out value="${roleinfo.orgNamePath}"/></td>
				<td><c:out value="${roleinfo.endTime}"/></td>
				<td>
					<c:choose><c:when test="${roleinfo.vaild==false}">
					<font color="red"><i18n:message key="org.role.status.invalid"/></font>
					</c:when>
					<c:when test="${roleinfo.vaild==true}">
					<i18n:message key="org.role.status.valid"/>
					</c:when></c:choose>
				</td>
				<td>
					<input class="button" type="button" value="<i18n:message key="org.user.role.valid.revoke"/>" onclick="revokeRole('<c:out value="${roleinfo.roleID}"/>','<c:out value="${usermap.userinfo.userID}"/>')">
					<input  class="button" type="button" value="<i18n:message key="org.user.role.valid"/>" onclick="updateValid('<c:out value="${roleinfo.roleID}"/>','<c:out value="${usermap.userinfo.userID}"/>')">
				</td>
			</tr>
			</c:forEach>
			<tr>
				<td colspan="5" class="alignCenter">
					<input class="button" type="button" value="<i18n:message key="org.user.role.grant"/>" onclick="selectRole()">
				</td>
			</tr>
		</table>

		<table cellPadding="0" cellSpacing="0" class="table mt" style="display:none;">
			<tr>
				<th><i18n:message key="org.user.default.doctype"/></th>
				<th><i18n:message key="org.user.default.doclib"/></th>
				<th><i18n:message key="org.user.default.folder"/></th>
				<th> </th>
			</tr>
			<c:forEach items="${usermap.userfolder}" var="folderinfo">
			<tr>
				<td><c:out value="${folderinfo.docTypeName}"/></td>
				<td><c:out value="${folderinfo.docLibName}"/></td>
				<td><c:if test="${folderinfo.folderNamePath==''}"><i18n:message key="org.user.default.folder.none"/></c:if><c:out value="${folderinfo.folderNamePath}"/>
				</td>
				<td>
					<input class="button" type="button" value="<i18n:message key="org.user.default.folder.setup"/>" onclick="setFolder('<c:out value="${folderinfo.docTypeID}"/>','<c:out value="${folderinfo.folderID}"/>','<c:out value="${usermap.userinfo.userID}"/>','<c:out value="${folderinfo.docLibID}"/>')">
					<c:if test="${folderinfo.folderNamePath!=''}">
					<input  class="button" type="button" value="<i18n:message key="org.user.default.folder.clear"/>" onclick="clearFolder('<c:out value="${folderinfo.docTypeID}"/>','<c:out value="${folderinfo.docLibID}"/>','<c:out value="${folderinfo.folderID}"/>','<c:out value="${usermap.userinfo.userID}"/>')">
					</c:if>
				</td>
			</tr>
			</c:forEach>
		</table>
		<input type="hidden" name="orgID" value="<c:out value="${usermap.userinfo.orgID}"/>">
		<input type="hidden" name="treeid" value="<c:out value="${usermap.userinfo.treeid}"/>">
		<input type="hidden" name="userID" value="<c:out value="${usermap.userinfo.userID}"/>">
	</form>
	</div>
	<script type="text/javascript">
		<c:if test="${usermap.userinfo.treeid!='-1'}">
		var opType="<c:out value="${usermap.userinfo.opType}"/>";
		if(opType=="update"){
			parent.leftFrame.updateUserNode("<c:out value="${usermap.userinfo.userName}"/>","<c:out value="${usermap.userinfo.treeid}"/>");
		}
		</c:if>
		var treeid = "<c:out value="${usermap.userinfo.treeid}"/>";
		function selectRole(){
			var url="RoleVaildIndex.jsp?UserID=<c:out value="${usermap.userinfo.userID}"/>&treeid="+treeid;
			window.open(url,"_blank", "width=720,height=560,resizable=1");
			if(treeid=="-1"){
				//检索时候修改
				window.close();
			}	
		}
		function refreshFrm(){
			document.location.href="UserMgrAction.do?invoke=UserFormList&UserID=<c:out value="${usermap.userinfo.userID}"/>&treeid="+treeid;
		}
		function clearFolder(doctypeid,doclibid,folderid,userid){
				urlsrc="UserMgrFolderAction.do?invoke=clearFolder"
				+"&UserID="
				+userid
				+"&DocTypeID="
				+doctypeid
				+"&DocLibID="
				+doclibid
				+"&FolderID="
				+folderid;
				localUrl="UserMgrAction.do?invoke=UserFormList&UserID=<c:out value="${usermap.userinfo.userID}"/>&treeid="+treeid;
				invokeGetXmlHttpUpdate(urlsrc,localUrl);
		}
		function setFolder(doctypeid,folderid,userid,doclibid){
			var folderUrl="FolderTreeGenerate.do?invoke=folderDlg&DocTypeID="
			+doctypeid+"&UserID=<c:out value="${usermap.userinfo.userID}"/>"
			+"&FolderID="+folderid+"&treeid=<c:out value="${usermap.userinfo.treeid}"/>"+"&docLibID="+doclibid;
			window.open(folderUrl,"_blank", "width=420,height=520");
			if(treeid=="-1"){
				//检索时候修改
				window.close();
			}
		}
		function revokeRole(roleid,userid){
			var urlsrc="RoleMgrUserAction.do?invoke=revokeRole"
				+"&RoleID="+roleid
				+"&UserID="+userid;
				localUrl="UserMgrAction.do?invoke=UserFormList&UserID=<c:out value="${usermap.userinfo.userID}"/>&treeid="+treeid;
				invokeGetXmlHttpUpdate(urlsrc,localUrl);
		}
		function updateValid(roleid,userid){
			var vaildUrl = "RoleValidAction.do?invoke=show&OpType=update&UserID="+userid+"&RoleID="+roleid+"&treeid=<c:out value="${usermap.userinfo.treeid}"/>";
			window.open(vaildUrl,"_blank", "width=400,height=400");
			if(treeid=="-1"){
				//检索时候修改
				window.close();
			}
		}
	</script>
</body> 
</html>