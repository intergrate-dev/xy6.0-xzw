<%@include file="../../e5include/IncludeTag.jsp"%>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8">
	<link type="text/css" rel="stylesheet" href="../../e5style/style.css"/>
	<style>
		th{background-color:#EEEEEE; width:1%; white-space:nowrap;cursor:pointer;}
		#hint{color:gray;}
		.btnarea{
			margin:20px 0;
		}
	</style>
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
	<script type="text/javascript">
		//整行选中和取消
		function nodeClick(id) {
			var checks = $("input[typeIndex='" + id + "']");
			if (checks.length == 0) return;
			
			var setcheck = !checks[0].checked;

			for (var i = 0; i < checks.length; i++)
				checks[i].checked = setcheck;
		}
		//初始化：默认是选中，把已设置的TAB改成不选中
		function doInit() {
			var ids = "<c:out value="${ids}"/>";
			var checks = ids.split(",");
			for (var i = 0; i < checks.length; i++) {
				var check = $("#" + checks[i]);
				check.prop("checked", true);
			}
			if (typeof refreshCache != "undefined")
				$("#btnCache").click(refreshCache);
		}
		function beforeSubmit() {
			var result = "";

			var checks = $("input[type='checkbox']");
			for (var i = 0; i < checks.length; i++) {
				if (checks[i].checked) {
					if (result.length > 0) result += ",";
					result += checks[i].id;
				}
			}
			$("#resource").val(result);
		}
	</script>
</head>
<body onload="doInit()">
	<iframe id="iframe" name="iframe" style="display:none"></iframe>
	<form Name="permissionForm" Target="iframe" Action="./MainPermissionSubmit.do" Method="Post" onsubmit="beforeSubmit()">
		<input Type="hidden" Name="roleID" Value="<c:out value="${sessionScope.permissionRoleID}"/>">
		<input Type="hidden" Name="resource" id="resource" Value="">
		<!--每个主菜单-->
		<table border="0" cellpadding="4" cellspacing="0">
		<c:forEach var="main" items="${tabs}" varStatus="tabIndex">
		<tr>
			<th class="bottomlinetd" onclick="nodeClick(<c:out value="${tabIndex.index}"/>)"><c:out value="${main.name}"/></th>
			<td class="bottomlinetd">
			<!--每个TAB-->
			<c:forEach var="tab" items="${main.children}">
				<c:if test="${tab.id != 'separator'}">
					<c:if test="${!tab.free}">
				<label for="<c:out value="${tab.id}"/>">
					<input type="checkbox" name="<c:out value="${tab.id}"/>" id="<c:out value="${tab.id}"/>"
						typeIndex="<c:out value="${tabIndex.index}"/>"/>
					<c:out value="${tab.name}"/>
				</label>
					</c:if>
				</c:if>
			</c:forEach>
			</td>
			</tr>
		</c:forEach>
		</table>
		<div class="btnarea">
			<input Type="submit" AccessKey="S" class="submitBtn" value="保存"/>
		</div>
	</form>
	<div id="hint">提示：在左边名称列上单击可以选中/取消整行</div>
</body>
</html>
