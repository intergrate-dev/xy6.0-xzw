<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
	<title>推送客户端</title>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="../ueditor/src/ui/dialog.js"></script>
	<script type="text/javascript" src="../script/bootstrap-3.3.4/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="../script/bootstrap-datetimepicker/bootstrap-datetimepicker.min.js" charset="UTF-8"></script>
	<script type="text/javascript" src="../script/bootstrap-datetimepicker/locales/bootstrap-datetimepicker.zh-CN.js" charset="UTF-8"></script>
		
	<link rel="stylesheet" href="../script/bootstrap-3.3.4/css/bootstrap.min.css">
	<link href="../script/bootstrap-datetimepicker/css/datetimepicker.css" rel="stylesheet" media="screen">
	<link type="text/css" rel="stylesheet" href="../../e5script/jquery/dialog.style.css"/>
	<style type="text/css">
    	body
    	{
    		font-family:"microsoft yahei"; 
    		color:#666 ;
    	}
    	body input{
    		font-family:"microsoft yahei";
    	}
    	.label
    	{
    		font-weight: normal;
    		color:#666 ;
    		font-size: 13px;
    		
    	}
    	.left{float: left;}
    	.input
    	{
    		border-radius:3px; 
    		border: 1px solid #ccc;
    		padding-left: 10px;
    		
    	}
    	textarea.input{
    		width:300px; 
    		height: 80px;
    		resize: none;
    		margin-left: 3px;
    	}
    	#doSave
    	{
    		font-size: 12px; 
    		margin: 0px 20px 0 79px;
    		border-radius: 3px;
		  	color: #fff;
		  	background: #1bb8fa;
		  	width: 64px;
		  	height: 30px;
		  	border: none;
		  	font-size: 12px;
		  	cursor: pointer;
    	}
    	#doCancel
    	{
    		 border-radius: 3px;
			  color: #fff;
			  background: #b1b1b1;
			  width: 65px;
			  height: 25px;
			  border: none;  
			  font-size: 12px;
			  cursor: pointer;
    		  font-size: 12px;
    	}
    	.push{
    		 margin: 15px 0;
    	}
    </style>
	<script>
		var siteID = "<c:out value="${siteID}"/>";
		if (!siteID) siteID = 1;
		
		var type = "<c:out value="${type}"/>";
		var articleID = "<c:out value="${DocIDs}"/>";
		var docLibID = "<c:out value="${DocLibID}"/>";
		var UUID = "<c:out value="${UUID}"/>";
		var description = "<c:out value="${description}"/>";	
		var topicID = "<c:out value="${topicID}"/>";
	</script>
</head>
<body>
	<script type="text/javascript" src="../../xy/article/script/pushApp.js"></script>
	<script type="text/javascript" src="../../e5workspace/script/form-custom.js"></script>		
	<script type="text/javascript" src="../../e5script/jquery/jquery.dialog.js"></script>
	<script type="text/javascript" src="../../e5script/jquery/jQuery-Validation-Engine/js/jquery.validationEngine.js"></script>
	<div id="divDescription" class="push">
		<label class="left label">推送内容：</label>
		<textarea class="input" type="text" id="pushDescription"/></textarea> 
	</div>
	
	<div id="divPushTime" class="push" style="display:none">
		<label class="label">定时推送：</label>
		<input class="input" type="text" id="pushTime" />  
	</div>
	<div id="divPush">
		<label class="label">目标用户：</label>
		<label class="checkbox-inline"><input id="all-user-radio" type="radio" name=targetUser value="0" data-linkage="a-1" checked=""> 全部用户</label>
        <label class="checkbox-inline"><input id="tag-user-radio" type="radio" name="targetUser" value="1" data-linkage="a-2" onclick="e5_form_event.selectCat('push_region', 'push_regionIDS', '${cattypeID}', true)"> 标签用户</label>
    </div>
	<div id="divPushSel">
       	<input type="hidden" id="push_region" name="push_region"  value=""/>
        <input type="hidden" id="push_regionIDS" name="push_regionIDS" value=""/>  
	</div>
	<div>
	</div>
	<input type='button' id="doSave" value='确定'/>
	<input type='button' id="doCancel" value='取消'/>
</body>
</html>