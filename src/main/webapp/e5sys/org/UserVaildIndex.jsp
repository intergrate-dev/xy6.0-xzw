<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5org" changeResponseLocale="false"/>
<html>
	<head>
		<title><i18n:message key="org.role.grant.user"/></title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link type="text/css" rel="StyleSheet" href="../../e5style/reset.css"/>
		<link type="text/css" rel="StyleSheet" href="../../e5style/sys-main-body-style.css"/>
		<script type="text/javascript" src="./js/xmlhttps.js"></script>
		<script type="text/javascript">
			window.returnValue=-1;
			var roleid="<c:out value="${param.RoleID}"/>";
			function grantNewRole(){
				if(leftFrame.selid==-1)
				{
					alert("<i18n:message key="org.role.select.user.alert"/>	");
						return ;
				}

				var urlsrc="RoleMgrUserAction.do?invoke=grantRole"
				+"&RoleID="+ roleid
				+"&UserID="+ leftFrame.selid
				+mainBody.getDatePara();
				
				var sInf=invokeGetXmlHttpDoForResponse(urlsrc);
				if(sInf=="1")
				{
					refreshPage();
					alert("OK!");
					//window.close();
				}
				else
				{
					alert("<i18n:message key="org.role.grant.user.operr"/>");
				}
			}
			function getFrame(name){
				return document.getElementById(name);
			}
			function getRoleVaildForm(userid){
				document.getElementById("mainBody").src="RoleValidAction.do?invoke=form&UserID="+userid+"&RoleID="+roleid;
			}
		</script>
	</head>
	<body>
		<div class="mainBodyWrap">
		<table cellPadding="0" cellSpacing="0" class="table">
			<caption><i18n:message key="org.role.grant.user"/></caption>
			<tr>
				<td colspan="2" class="aligncCnter">
					<input type="button"  class="button" value="<i18n:message key="org.sort.submit"/>" onClick="javascript:grantNewRole();">
					<input type="button"  class="button" value="<i18n:message key="org.sort.cancel"/>" onClick="javascript:window.close();">
				</td>
			</tr>
			<tr>
				<td>
					<iframe style="height:460px;width:200px;" name="leftFrame"  id="leftFrame" src="OrgTreeGenerate.do?invoke=orgUserRootNode&RoleID=<c:out value="${param.RoleID}"/>" frameborder="0"></iframe>
				</td>
				<td>
					<iframe style="height:460px;width:400px;" name="mainBody" id="mainBody" src="" frameborder="0"></iframe>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<input type="button"  class="button" value="<i18n:message key="org.sort.submit"/>" onClick="javascript:grantNewRole();">
					<input type="button"  class="button" value="<i18n:message key="org.sort.cancel"/>" onClick="javascript:window.close();">
				</td>
			</tr>
		</table>
		</div>
	</body>
</html>
