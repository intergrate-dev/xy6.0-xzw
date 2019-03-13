<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5org" changeResponseLocale="false"/>
<html> 
	<head>
		<title>Add Org</title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<script type="text/javascript" src="../../e5script/Function.js"></script>
		<link type="text/css" rel="StyleSheet" href="../../e5style/reset.css"/>
		<link type="text/css" rel="StyleSheet" href="../../e5style/sys-main-body-style.css"/>
		<link type="text/css" rel="StyleSheet" href="../../e5style/e5sys-org-OrgForm.css"/>
		<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
	</head>
	<body>
		<div class="mainBodyWrap">
		<form name="loginform" action="OrgMgrAction.do" method="post" onsubmit="return submitFrm();"> 
		<table cellPadding="0" cellSpacing="0" class="table">
			<caption>
				<c:if test="${orgmap.orginfo.operate=='add'}"><i18n:message key="org.menu.addorg"/></c:if>
				<c:if test="${orgmap.orginfo.operate=='update'}"><i18n:message key="org.menu.updateorg"/></c:if>
			</caption>
		<c:if test="${orgmap.orginfo.name!=''}">
		<c:if test="${orgmap.orginfo.operate=='add'}">
			<tr>
				<td><span class="beforeMsg"><i18n:message key="org.new.name"/></span><font color="red"><c:out value="${orgmap.orginfo.name}"/></font></td>
			</tr>
		</c:if>
		</c:if>
			<tr>
				<td><span class="titleTD"><i18n:message key="org.name"/></span><input type="text" name="OrgName" class="inputBS" value="<c:out value="${orgmap.orginfo.name}"/>"/></td>
			<tr>
			<tr>
				<td><span class="titleTD"><i18n:message key="org.code"/></span><input type="text" name="code"  class="inputBS" value="<c:out value="${orgmap.orginfo.code}"/>"/></td>
			</tr>
			<tr>
				<td>
					<span class="titleTD"><i18n:message key="org.type"/></span>
					<select  class="inputBS"  name="OrgTypeID">
						<c:forEach items="${orgmap.typelist}" var="typeinfo">
						<option value=<c:out value="${typeinfo.id}"/><c:if test="${orgmap.orginfo.type==typeinfo.id}">selected</c:if> ><c:out value="${typeinfo.name}"/></option>
						</c:forEach>
					</select>
					<input type="hidden" name="OrgID" value="<c:out value="${orgmap.orginfo.orgID}"/>">
					<input type="hidden" name="ParentOrgID" value="<c:out value="${orgmap.orginfo.parentID}"/>">
					<input type="hidden" name="invoke" value=""> 
					<!--here user property1 to set treeid,only by wangchaoyang user-->
					<input type="hidden" name="treeid" value="<c:out value="${orgmap.orginfo.treeid}"/>">
				</td>
			</tr>
			<tr>
				<td><span class="titleTD"><input type="submit" class="button" value="<i18n:message key="org.submit"/>"/><c:if test="${orgmap.orginfo.treeid=='-1'}"><input type="button" class="button" value="<i18n:message key="org.cancel"/>" onClick="window.close();"/></c:if>
				</span></td>
			</tr>
		</table>
		</form>
		</div>
		<script type="text/javascript">
		var operate="<c:out value="${orgmap.orginfo.operate}"/>"
		if(operate=="add"){
			document.loginform.invoke.value="addOrg";
			var isfresh="<c:out value="${orgmap.orginfo.fresh}"/>"
			if(isfresh=="true")
			{
					parent.leftFrame.addOrgNode("<c:out value="${orgmap.orginfo.name}"/>","<c:out value="${orgmap.orginfo.orgID}"/>","<c:out value="${orgmap.orginfo.treeid}"/>");
			}
		}else if(operate=="update"){
			document.loginform.invoke.value="updateOrg";
		}
		function submitFrm(){
			var val = loginform.OrgName.value.trim();
			loginform.OrgName.value = val;
				
			if(loginform.OrgName.value==""){
				alert("<i18n:message key="org.org.form.alert"/>");
				return false;
			}

			if(getLength(loginform.OrgName.value)>254){
				alert("<i18n:message key="org.org.form.name.input.length.alert"/>");
				return false;
			}

			if(getLength(loginform.code.value)>254){
				alert("<i18n:message key="org.org.form.code.input.length.alert"/>");
				return false;
			}
			
			//查重
			var url = "../../xy/user/CheckOrgRole.do"
				+ "?parentID=" + loginform.ParentOrgID.value
				+ "&name=" + loginform.OrgName.value
				+ "&type=0"
			<c:if test="${orgmap.orginfo.operate=='update'}">
				+ "&id=" + loginform.OrgID.value
			</c:if>
			
			var result;
			$.ajax({type: "POST", url: url, async:false, 
				success: function(data) {
					result = data;
				},
				error: function (XMLHttpRequest, textStatus, errorThrown) {
					alert(errorThrown + ':' + textStatus);  // 错误处理
				}
			});
			if (result == "1") {
				alert("已存在同名部门");
				return false;
			} else
				return true;
		}
		</script>
	</body> 
</html>