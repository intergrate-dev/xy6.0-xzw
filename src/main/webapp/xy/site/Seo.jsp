<%@include file="../../e5include/IncludeTag.jsp"%>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
	<title><%=com.founder.e5.workspace.ProcHelper.getProcName(request)%></title>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
	<link type="text/css" rel="stylesheet" href="../script/bootstrap/css/bootstrap.min.css">
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="script/seo.js"></script>
	<style type="text/css">
		.btngroup {
		    margin: 5px 5px;
		    font-family: "microsoft yahei";
		    color: #fff;
		    border: none;
		   
		    border-radius: 3px;
		    padding: 5px 20px;
		    font-size: 12px;
		}
		#btnCancel{
			 background: #b1b1b1;
		}
		#btnSave{
			 background: #00a0e6;
		}
		body{
			font-family: "microsoft yahei";
		}
		.wrapper{
			text-align: center;
			margin-top: 50px;
		}
	</style>
</head>
<body>
<iframe id="iframe" src="" style="display:none;"></iframe>
	<div class="wrapper">
		<form id="form" name="form" method="post" action="SeoSubmit.do">
		<input type="hidden" id="DocLibID" name="DocLibID" value="<c:out value="${DocLibID}"/>">
		<input type="hidden" id="DocID" name="DocID" value="<c:out value="${DocID}"/>">
		<input type="hidden" id="UUID" name="UUID" value="<c:out value="${UUID}"/>">
		
		<c:if test="${!onlyApp}">
		<span>站点地图发布目录（Web）：</span>
		<select id="dirWeb" name="dirWeb" oldValue="${dirWeb}">
			<option value="0"></option>
			<c:forEach var="dir" items="${dirs}" varStatus="status">
			<option value="${dir.id}">${dir.name}</option>
			</c:forEach>
		</select><br/>
		</c:if>
		
		<c:if test="${!onlyWeb}">
		<span>站点地图发布目录（App）：</span>
		<select id="dirApp" name="dirApp" oldValue="${dirApp}">
			<option value="0"></option>
			<c:forEach var="dir" items="${dirs}" varStatus="status">
			<option value="${dir.id}">${dir.name}</option>
			</c:forEach>
		</select>
		</c:if>
	</form>
	<div>
		<input class="dosave btngroup" type="button" id="btnSave" value="保存"/>
		<input class="docancle btngroup" type="button" id="btnCancel" value="关闭"/>
	</div>
</div>

</body>
</html>
