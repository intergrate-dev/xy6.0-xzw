<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<%@page import="java.net.InetAddress"%>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
	<meta content="text/html;charset=utf-8" http-equiv="Content-Type" />
	<meta content="IE=edge" http-equiv="X-UA-Compatible" />
	<title><%=com.founder.e5.context.Context.getSystemName()%></title>
	<script type="text/javascript" src="../e5script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="../e5script/e5.min.js"></script>
	<script type="text/javascript" src="script/bootstrap/js/bootstrap.min.js"></script>

	<link rel="stylesheet" type="text/css" href="script/bootstrap/css/bootstrap.css"/>
	<link rel="stylesheet" type="text/css" href="css/header.css">
	<style type="text/css">
	.ui-icon-close{
		background-image: url(../images/icon/del.gif);
		width:12px;
  		height:12px;
	  	float: right;

	  	cursor: pointer;
	  	margin-left: 20px;
	  	margin-top:3px;
	  	border-radius: 6px;
	}
	
	.ui-icon-close:hover
	{
		background-image: url(../Icons/del.png);
		width:12px;
  		height:12px;
	  	float: right;
	  	
	  	cursor: pointer;
	  	margin-left:20px;
	  	margin-top: 4px;
	  	border-radius: 6px;
		
	}
	
	#tabTitleUL li a{
		
		color: #373737;
		float: left;
		padding:3px 5px;
		padding-left: 25px;
		font-size: 13px;
		border-radius:0;
		border: none;
		margin-right: 0;
		
	}
	.nav > li > a:hover, .nav > li > a:focus
	{
		background:#FAEBCC;
		border-bottom: none;
	}
	
	#tabContentDiv
	{
		width: 100.6%;
		background-color: #fff;	
	}
	#tabTitleUL
	{
		margin-bottom: 0px!important;
		width: 102%;
		margin-left: -10px;
		height:26px;
		background: #e6e5eb;
		border-bottom: 1px solid #add9f4;
		
	}
	#closeTabBtn{
		  position: absolute;
		  right: 10px;
		  
		  width: 75px;
		  height: 30px;
		  background-color: #c3c3c3;
		  border: none;
		  border-radius: 4px;
		  color: #fff;
		  font-family: "microsoft yahei";
		 
	}
	
	.nav-tabs{
		width:100%;
	}
	.nav-tabs > li {
	  margin-bottom: -1px;
	 
	  border: 1px solid #ccc;
      border-bottom: none;
      margin: 0 1px;	  
	  
	 
	  margin-bottom: 0px;
	  border-bottom: none;
	  height: 26px;
  }
  .nav-tabs > li.active{
  		border: 1px solid #add9f4;
  		border-bottom: none;
		height: 25px;  
 

  }
  .nav-tabs > .active > a, .nav-tabs > .active > a:hover, .nav-tabs > .active > a:focus{
  	border: none;
  }
	</style>
</head>
<body>
	<%@include file="inc/Header.inc"%>
	<div id="wrapMain">
		<input type="button" id="closeTabBtn" value="关闭"  title="关闭其它标签页" style="display:none;"/>
		<!-- tab -->
		<ul class="nav nav-tabs" id="tabTitleUL"></ul> 
		<!-- END tab -->
		
	    <!-- 与tab相绑定的div -->
	    <div class="tab-content" id="tabContentDiv"> </div> 
		<!-- END 与tab相绑定的div -->
	</div>
</body>
</html>