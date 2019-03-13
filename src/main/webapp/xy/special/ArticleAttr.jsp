<%@include file="../../e5include/IncludeTag.jsp"%>
<%@page pageEncoding="UTF-8"%>
<i18n:bundle baseName="i18n.e5org" changeResponseLocale="false" />
<html>
<head>
<title>专题删除</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript"
	src="../../e5script/jquery/jquery-1.9.1.min.js"></script>
<script type="text/javascript"
	src="../script/bootstrap-3.3.4/js/bootstrap.min.js"></script>
<script type="text/javascript"
	src="../script/bootstrap-datetimepicker/bootstrap-datetimepicker.js"
	charset="UTF-8"></script>
<script type="text/javascript"
	src="../script/bootstrap-datetimepicker/locales/bootstrap-datetimepicker.zh-CN.js"
	charset="UTF-8"></script>
<script type="text/javascript" src="../../xy/special/js/ArticleAttr.js"></script>
<link href="../script/bootstrap-3.3.4/css/bootstrap.min.css"
	rel="stylesheet" media="screen">
<link href="../script/bootstrap-datetimepicker/css/datetimepicker.css"
	rel="stylesheet" media="screen">
<link href="../../e5style/e5form-custom.css" rel="stylesheet"
	media="screen">
<style>
#btnSave, #btnCancel {
	font-family: "microsoft yahei";
	text-shadow: none;
	font-size: 12px;
	border-radius: 3px;
	width: 70px !important;
	border: none;
	color: #fff;
	line-height: 12px;
	margin-top: 20px;
}

#btnSave {
	margin-left: 80px;
	margin-right: 20px;
	background-color: #00a0e6;
}

#btnCancel {
	background-color: #b1b1b1;
}

.tablecontent {
	width: 90%;
	margin: 0 auto;
	margin-top: 5px;
	margin-left: 15px;
	border-bottom: none;
}

.tablecontent tr {
	border-bottom: none;
}

.tablecontent td {
	border-bottom: none;
	margin-left: 80px;
}

.tablecontent input {
	border-radius: 3px;
	border: 1px solid #ccc;
	padding-left: 10px;
	margin-bottom: -2px;
	width: 120px;
	height: 25px;
}

.tablecontent lable {
	cursor: default !important;
}

.custform-label {
	width: 100%;
	font-family: "microsoft yahei";
}

.custform-label-cue {
	width: 100%;
	font-family: "microsoft yahei";
	color: #CCCCCC;
	margin-left: 120px;
}

.btn-group-sm>.btn, .btn-sm {
	padding: 5px 9px;
}
</style>
<script type="text/javascript">
	var error = <c:out value="${error}"/>;
</script>
</head>
<body>
	<iframe id="frmColumn" style="display: none;"></iframe>
	<form name="caForm" id="caForm"
		action="/xy/special/Delete.do" method="post">
		<input type="hidden" name="siteID" id="siteID"
			value="<c:out value="${siteID}"/>" /> <input type="hidden"
			name="UUID" id="UUID" value="<c:out value="${UUID}"/>" /> <input
			type="hidden" name="DocLibID" id="DocLibID"
			value="<c:out value="${DocLibID}"/>" /> <input type="hidden"
			name="FVID" id="FVID" value="<c:out value="${FVID}"/>" /> <input
			type="hidden" name="groupID" id="groupID"
			value="<c:out value="${groupID}"/>" /> <input type="hidden"
			name="DocIDs" id="DocIDs" value="<c:out value="${DocIDs}"/>" /> <input
			type="hidden" name="siteField" id="siteField"
			value="<c:out value="${siteField}"/>" /> <input type="hidden"
			name="groupField" id="groupField"
			value="<c:out value="${groupField}"/>" /> <input type="hidden"
			name="error" id="error" value="<c:out value="${error}"/>" />

		<div id="attrDiv" style="text-align: center; margin-top: 10px;">
			<c:if test="${not empty error}">专题已被专题稿件引用，删除将撤稿相关稿件！</c:if>
			<c:if test="${empty error}">确认要做操作：删除？</c:if>
		</div>
		<hr style="margin-top: 10px; margin-bottom: 10px;" />
		<c:if test="${not empty error}">
			<div id="attrDiv"
				style="text-align: left; font-weight: bold; padding: 0 15px;">稿件列表</div>
			<table class="tablecontent" style="text-align: center;">
				<tr>
					<td><div id="attrDiv">ID</div></td>
					<td><div id="attrDiv">稿件标题</div></td>
				</tr>
				<c:forEach items="${error}" var="row" varStatus="s">
					<tr>
						<td style="padding: 0px;">${row.docID}</td>
						<td style="padding: 0px;">${row.linkTitle}</td>
					</tr>
				</c:forEach>
			</table>
		</c:if>
		<span id="txSave" class="ui-draggable" fieldtype="-1"
			fieldcode="insertsave"> <input style="margin-left: 40px;"
			id="btnSave" class="button btn" value="确认" type="button" />
		</span> <span class="custform-aftertxt ui-draggable">&nbsp; </span> <input
			id="btnCancel" class="button btn" value="取消" type="button"
			style="margin-left: 45px;" />
		</td>
	</form>
</body>
</html>
