<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
	<title>按栏目发布</title>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="../script/bootstrap-3.3.4/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="../script/bootstrap-datetimepicker/bootstrap-datetimepicker.min.js" charset="UTF-8"></script>
	<script type="text/javascript" src="../script/bootstrap-datetimepicker/locales/bootstrap-datetimepicker.zh-CN.js" charset="UTF-8"></script>
	<script type="text/javascript" src="script/pubByColumn.js"></script>
		
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
    	.fieldset
    	{
    		border: none;
    		 
    		font-size: 12px;
    		color:#666; 
    		margin-left:15px;
    	}
    	
    	.legend
    	{
    		font-size:13px;
    		margin-bottom:10px;
    		border: none;
    		padding-top: 12px;
    		color: #646464;
    	}
    	.label
    	{
    		font-weight: normal;
    		color:#666 ;
    	}
    	.radio
    	{
    		float:left;
    	}
    	.div
    	{
    		margin-left:22px;
    	}
    	.div1
    	{
    		margin-left: 20px;
    	}
    	.div2
    	{
    		margin:10px 0;
    		margin-left: 20px;
    	}
    	.div3
    	{
    		margin-bottom:10px;
    		margin-left:22px;
    	}
		.div4
		{
			font-size:12px;
			margin-bottom:10px;
			border: none;
			padding-top: 12px;
			color: #646464;
		}
    	.input
    	{
    		border-radius:3px; 
    		border: 1px solid #ccc;
    		padding-left: 10px;
    	}
    	#doPub
    	{
    		
    		font-size: 12px; 
    		margin: 20px 20px 0 45px;
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
    </style>
</head>
<body>
	<fieldset class="fieldset">
		<legend class="legend">发布范围：</legend>
		<div class="div1">
			<input class="radio" type="radio" value="0" name="pubType" id="pubType0" checked="checked"/>
			<label class="label" for="pubType0">本栏目</label>
		</div>
		<div class="div2" style=''>
			<input class="radio" type="radio" value="1" name="pubType" id="pubType1"/>
			<label class="label" for="pubType1">本栏目及子栏目</label>
		</div>
		<div class="div1">
			<input class="radio" type="radio" value="2" name="pubType" id="pubType2"/>
			<label class="label" for="pubType2">本栏目及子孙栏目</label>
		</div>
	</fieldset>

	<fieldset class="fieldset">
		<legend class="legend">发布内容：</legend>
		<div class="div4">
			<input class="radio" type="radio" value="2" name="pubContent" id="pubColumn" checked = "checked"/>
			<label class="label" for="pubColumn">只发布栏目页</label>
		</div>

		<div class="div4">
			<input class="radio" type="radio" value="0" name="pubContent" id="pubByTime" />
			<label class="label" for="pubByTime">按时间发布</label>
		</div>
		<div class="div3">
			<label class="label">起始日期</label>
			<input class="input" type="text" id="pubTime_from"/> 
		</div>
		<div class="div">
			<label class="label">结束日期</label>
			<input class="input" type="text" id="pubTime_to"/>
		</div>
		<div class="div4">
			<input class="radio" type="radio" value="1" name="pubContent" id="pubByCount"/>
			<label class="label" for="pubByCount">按数量发布</label>
		</div>
		<div class="div3">
			<label class="label">发布数量</label>
			<input class="input" type="number" id="pubCount" value="20"/>
		</div>

	</fieldset>
	<input type='button' id="doPub" value='确定'/>
	<input type='button' id="doCancel" value='取消'/>
	<script>
		var ch = "<c:out value="${param.ch}"/>";
		var colID = "<c:out value="${param.colID}"/>";
	</script>
</body>
</html>