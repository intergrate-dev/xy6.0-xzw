<%@include file="../../e5include/IncludeTag.jsp"%>
<%@ page pageEncoding="UTF-8"%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/>
	<link type="text/css" rel="stylesheet" href="../script/bootstrap/css/bootstrap.min.css">	
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
	
	<!-- 栏目树所需 js css -->
	<script type="text/javascript" src="../../e5script/jquery/jquery-autocomplete/jquery.autocomplete.min.js"></script> 
	<link rel="stylesheet" type="text/css" href="../../e5script/jquery/jquery-autocomplete/jquery.autocomplete.css"/>
	<script type="text/javascript" src="../../e5script/jquery/ztree/jquery.ztree.all-3.3.min.js"></script>
	<link rel="stylesheet" type="text/css" href="../../e5script/jquery/ztree/zTreeStyle/zTreeStyle.css"/>
	<style>
		.ztree *{font-family: "微软雅黑";font-size:12px;}
		#colSearch {border-radius:3px;border:1px solid #ccc;width: 122px;height:24px;padding-left: 5px;margin-left: 5px;margin-top: 5px;}
		#colSearchById{border-radius:3px; border:1px solid #ccc;width: 122px;height:24px;padding-left: 5px;margin-left: 5px;margin-top: 5px;}
		#rs_tree {min-height:250px;}
	</style>
	<script language="javascript" type="text/javascript" src="../column/script/tree.js"></script>
	
	<!-- dialog所需 js css -->
	<link type="text/css" rel="stylesheet" href="../../e5script/jquery/dialog.style.css"/>
	<script type="text/javascript" src="../../e5script/jquery/jquery.dialog.js"></script> 
	
	<link type="text/css" rel="stylesheet" href="../../e5script/jquery/jQuery-Validation-Engine/css/validationEngine.jquery.css"/>
	<script type="text/javascript" src="../../e5script/jquery/jQuery-Validation-Engine/js/languages/jquery.validationEngine-zh_CN.js"></script>
	<script type="text/javascript" src="../../e5script/jquery/jQuery-Validation-Engine/js/jquery.validationEngine.js"></script>
	
	<link rel="stylesheet" type="text/css" href="css/video-article.css"/>
	<script type="text/javascript">
		var article = {
			isNew : "<c:out value="${isNew}"/>",
			type : "<c:out value="${type}"/>",
			UUID : "<c:out value="${UUID}"/>",
			siteID : "<c:out value="${siteID}"/>",
			videoID : "<c:out value="${videoID}"/>",
			videoLibID : "<c:out value="${videoLibID}"/>",
			ch : "<c:out value="${ch}"/>",
			webDocLibID : "<c:out value="${webDocLibID}"/>",
			appDocLibID : "<c:out value="${appDocLibID}"/>",
			DocID : "<c:out value="${DocID}"/>",
			title : "<c:out value="${title}"/>",
			content : "<c:out value="${content}"/>",
		};
	</script>
</head>
<body>
	<div class="header">
		<ul id="channels">
			<li class="channel select" ch="0">Web发布库</li>
			<!-- <li class="channel" ch="1">App发布库</li> -->
		</ul>
	</div>
	<div class="main">
		<div class="leftSide">
			<div class="step1">1 请选择要发布的栏目</div>
			<div class="columnTree">
				<div class="div">
					<div id="divColSearch">
						<input  id="colSearch" type="text" value="" title="请输入栏目名进行查询" size="8"/>
					</div>
				</div>
				<div id="rs_tree" class="ztree"></div>
				<script language="javascript" type="text/javascript" src="../column/script/tree.js"></script>
			</div>
		</div>
		<div class="rightSide">
			<div class="step2">2  请设置稿签与标题图（暂只支持以下稿件字段，如果完善请至具体栏目中进行稿件设置）</div>
			<%@include file="inc/VideoArticleRight.inc"%>
		</div>
	</div>
	<div style="clear:both;"></div>
	<div class="footer">
		<input type="button" id="btnSubmit" class="btn btn-primary" value="发布" /> 
		<input type="button" id="btnCancel" class="btn btn-cancel" value="取消" /> 
	</div>
	<script type="text/javascript" src="script/video-form.js"></script>
	<script type="text/javascript">
		$(function() {
			video_form.init();	
		});
	</script>
</body>
</html>