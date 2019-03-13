<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<%
String siteID = request.getParameter("siteID");
%>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
	<title>站点域名管理之树</title>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
	<style>
		body
		{
			font-family: "microsoft yahei";
			color: #666;
			background-color: #fff;
		}
		#rMenu{
			position:absolute;
			visibility:hidden;
			background-color:#f8f8f8;
			font-size:12px;
			padding-left:0;
			padding-right: 40px;
			border-radius: 5px;
		}
		#rMenu ul
		{
			cursor: pointer;
		}
		#rMenu ul:hover
		{
			color:#003BB3;
		}
		
		#Button{
			width:300px;
			height:50px;
		}
		#Button ul{
			list-style:none;
			
		} 
		#Button ul li{
			float:left;
			margin:5px;
			cursor: pointer;
			font-size: 14px;
		}
		
		#Button ul li:hover
		{
			color:#ff5337;
			
		} 
		.btn{
			display: inline-block;
		    padding: 4px 12px;
		    margin-bottom: 0;
		    font-size: 14px;
		    line-height: 20px;
		    color: #333333;
		    text-align: center;
		    text-shadow: 0 1px 1px rgba(255, 255, 255, 0.75);
		    vertical-align: middle;
		    cursor: pointer;
		    background-color: #f5f5f5;
		    background-image: -moz-linear-gradient(top, #ffffff, #e6e6e6);
		    background-image: -webkit-gradient(linear, 0 0, 0 100%, from(#ffffff), to(#e6e6e6));
		    background-image: -webkit-linear-gradient(top, #ffffff, #e6e6e6);
		    background-image: -o-linear-gradient(top, #ffffff, #e6e6e6);
		    background-image: linear-gradient(to bottom, #ffffff, #e6e6e6);
		    background-repeat: repeat-x;
		    border: 1px solid #cccccc;
		    border-color: #e6e6e6 #e6e6e6 #bfbfbf;
		    border-color: rgba(0, 0, 0, 0.1) rgba(0, 0, 0, 0.1) rgba(0, 0, 0, 0.25);
		    border-bottom-color: #b3b3b3;
		    -webkit-border-radius: 4px;
		    -moz-border-radius: 4px;
		    border-radius: 4px;
		    filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#ffffffff', endColorstr='#ffe6e6e6', GradientType=0);
		    filter: progid:DXImageTransform.Microsoft.gradient(enabled=false);
		    -webkit-box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.2), 0 1px 2px rgba(0, 0, 0, 0.05);
		    -moz-box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.2), 0 1px 2px rgba(0, 0, 0, 0.05);
		    box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.2), 0 1px 2px rgba(0, 0, 0, 0.05);
		}
		.bs-callout-info {
			border-left-color: #00A0E6!important;
			border: 1px solid #eee;
			border-left-width: 5px;
			border-radius: 3px;
			min-width: 400px;
			max-width: 600px;
			position: absolute;
			left: 400px;
			top: 77px;
		}
	</style>
</head>
<body>
	<div id="Button" style="width:100%;">
  		<ul style="margin-left: -37px;">
    		<li class="btn" id="buttonAddRoot">新增域名</li>
    		<li class="btn" id="buttonAdd">新增目录</li>
			<li class="btn" id="buttonDelete">删除</li>
			<li class="btn" id="buttonResPath" title="站点下小资源文件（如头像、栏目图标等）的统一存放目录">作为资源目录</li>
  		</ul>
	</div>
	<div id="currentDirSpan" class="bs-callout-info">
		<ul style="margin-right: 10px;">
			<li id="pathLi"></li>
			<li id="urlLi"></li>
		</ul>
	</div>
	<%@include file="Tree.inc"%>
	
	<div id="rMenu">
		<ul id="menuAdd">增加子目录</ul>
		<ul id="menuDelete">删除</ul>
		<ul id="menuSwitch">域名切换</ul>
		<ul id="menuAddRoot">增加域名目录</ul>	
	</div>
	<script language="javascript" type="text/javascript" src="script/manage.js"></script>
	<script>
		dir_tree.siteID = <%=siteID%>;
	
	</script>

</body>
</html>