<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<%@ page pageEncoding="UTF-8"%>

<html>
<head>
	<title>站点</title>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="../../e5script/jquery/jquery-autocomplete/jquery.autocomplete.min.js"></script> 
	<link rel="stylesheet" type="text/css" href="../../e5script/jquery/jquery-autocomplete/jquery.autocomplete.css"/>
<style>
	body{
		background-color: #fff;
	}
	.frm {
	border: 0;
	width: 400px;
	height: 500px;
	}
	#sites ul{
		list-style:none;
		font-family: "microsoft yahei";
		font-size: 15px;
	}
	
	.select{
		background-color: #73C7EC;
		/*color: #fff;*/
		font-family: "microsoft yahei";
		padding: 3px 0;
    	width: 200px;
    	margin: 5px 0;
	} 
</style>
</head>

<body>
	<script language="javascript" type="text/javascript" src="script/site.js"></script>
	<script>
		site_list.type = "<c:out value="${type}"/>";
		site_list.domaindirUrl = "DomainDirTree.jsp?";
		site_list.ruleUrl = "../../e5sys/DataMain.do?type=SITERULE";
	</script>
	<div style="border-bottom:1px solid #D5D5D8;padding-bottom:5px;">
		查找：<input id="domainSearch" type="text" value="" title="请输入站点名进行查询" />
	</div>
	<div id ="sites">
		<ul id="sitelist">
			<c:forEach var="site" items="${sites}">
				<li value="${site.id}"><c:out value="${site.name}"/></li>
			</c:forEach>
		</ul>
	</div>
	</div>
</body>
</html>