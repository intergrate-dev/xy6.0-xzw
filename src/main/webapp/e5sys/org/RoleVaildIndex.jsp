<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5org" changeResponseLocale="false"/>
<html>
	<head>
		<title><i18n:message key="org.user.role.grant"/></title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link type="text/css" rel="StyleSheet" href="../../e5style/reset.css"/>
		<link type="text/css" rel="StyleSheet" href="../../e5style/sys-main-body-style.css"/>
		<script type="text/javascript" src="./js/xmlhttps.js"></script>
		<script language="javascript">
			var userid="<c:out value="${param.UserID}"/>";
			var treeid="<c:out value="${param.treeid}"/>";
			function grantNewRole(){
				if(leftFrame.selid==-1)
				{
					alert("<i18n:message key="org.user.select.role.alert"/>	");
					return ;
				}
				var urlsrc="RoleMgrUserAction.do?invoke=grantRole"
				+"&RoleID="+ leftFrame.selid
				+"&UserID="+ userid
				+mainBody.getDatePara();

				var sInf=invokeGetXmlHttpDoForResponse(urlsrc);
				if(sInf=="1")
				{
					if(treeid=="-1")
					{
						document.location.href="UserMgrAction.do?invoke=UserFormList&UserID="+userid+"&treeid=-1";
					}
					else
					{
						refreshPage();
						window.close();
					}
				}
				else
				{
					alert("<i18n:message key="org.user.grant.role.operr"/>");
				}

		}
		function getFrame(name)
		{
			return document.getElementById(name);
		}
		function getRoleVaildForm(roleid)
		{
				getFrame("mainBody").src="RoleValidAction.do?invoke=form&UserID="+userid+"&RoleID="+roleid;
		}
		</script>
	</head>
	<body>
		<div class="mainBodyWrap">
		<table cellPadding="0" cellSpacing="0" class="table">
			<caption><i18n:message key="org.user.role.grant"/></caption>
			<tr>
				<td colspan="2" class="alignCenter">
					<input type="button" class="button" value="<i18n:message key="org.sort.submit"/>" onClick="javascript:grantNewRole()">
					<input type="button"  class="button" value="<i18n:message key="org.sort.cancel"/>" onClick="javascript:window.close();">
				</td>
			</tr>
			<tr>
				<td><iframe style="height:460px;width:200px;" name="leftFrame" id="leftFrame" src="OrgTreeGenerate.do?invoke=orgRoleRootNode&UserID=<c:out value="${param.UserID}"/>" frameborder="0"></iframe></td>
				<td><iframe style="height:460px;width:400px;" name="mainBody" id="mainBody" src="blank.htm" frameborder="0"></iframe></td>
			</tr>
			<tr>
				<td colspan="2" class="alignCenter">
					<input type="button" class="button" value="<i18n:message key="org.sort.submit"/>" onClick="javascript:grantNewRole();">
					<input type="button" class="button" value="<i18n:message key="org.sort.cancel"/>" onClick="javascript:window.close();">
				</td>
			</tr>
		</table>
		</div>
	</body>
</html>

