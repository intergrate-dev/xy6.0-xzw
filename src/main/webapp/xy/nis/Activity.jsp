<%@include file="../../e5include/IncludeTag.jsp"%>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
	<title><%=com.founder.e5.workspace.ProcHelper.getProcName(request)%></title>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
	<meta content="IE=edge" http-equiv="X-UA-Compatible" />
	<link type="text/css" rel="stylesheet" href="../../e5script/jquery/jQuery-Validation-Engine/css/validationEngine.jquery.css"/>
	<link type="text/css" rel="stylesheet" href="../script/bootstrap/css/bootstrap.min.css">
	<link type="text/css" rel="stylesheet" href="../script/bootstrap-datetimepicker/css/datetimepicker.css" media="screen">
	<link type="text/css" rel="stylesheet" href="../script/jquery-autocomplete/styles.css"/>
	<link type="text/css" rel="stylesheet" href="../script/jquery-autocomplete/autoComplete.css"/>
	<link type="text/css" rel="stylesheet" href="../../e5script/jquery/dialog.style.css"/>
	<link rel="stylesheet" type="text/css" href="../../e5script/jquery/uploadify/uploadify.css"> 
	<link type="text/css" rel="stylesheet" href="../article/css/article.css"/>
	
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="../../e5script/jquery/jQuery-Validation-Engine/js/languages/jquery.validationEngine-zh_CN.js"></script>
	<script type="text/javascript" src="../../e5script/jquery/jQuery-Validation-Engine/js/jquery.validationEngine.js"></script>
	<script type="text/javascript" src="../script/bootstrap/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="../script/bootstrap-datetimepicker/new-bootstrap-datetimepicker.js" charset="UTF-8"></script>
	<script type="text/javascript" src="../script/bootstrap-datetimepicker/locales/bootstrap-datetimepicker.zh-CN.js" charset="UTF-8"></script>
	<script type="text/javascript" src="../script/jquery-autocomplete/jquery.autocomplete.min.js"></script>
	<script type="text/javascript" src="../script/jquery-autocomplete/autoComplete.js"></script>
	<script type="text/javascript" src="../../e5script/jquery/jquery.dialog.js"></script> 
	<script type="text/javascript" src="../../e5script/jquery/uploadify/jquery.uploadify-3.2.min.js"></script>
	<script type="text/javascript" src="../script/cookie.js"></script>
	<script type="text/javascript" src="../article/script/json2.js"></script>
	<script type="text/javascript" src="js/activity.js"></script>
	<script type="text/javascript">
        $(function(){
            UE.type="activity";
        })
		top.window.moveTo(0,0);  
		var docElm = document.documentElement;
		if (docElm.requestFullscreen) {
			//W3C
			top.window.resizeTo(screen.availWidth,screen.availHeight);  
		} else if (docElm.mozRequestFullScreen) {//FireFox
			top.window.resizeTo(screen.availWidth,screen.availHeight);  
		} else if (docElm.webkitRequestFullScreen) {//Chrome等
			 top.window.resizeTo(screen.availWidth,screen.availHeight); 
		} else if (document.all) {//IE
		    top.window.resizeTo(screen.availWidth,screen.availHeight);  
		}
		
		var article = {
			UUID : "<c:out value="${UUID}"/>",
			docID : "<c:out value="${article.docID}"/>",
			docLibID : "<c:out value="${article.docLibID}"/>",
			siteID : "<c:out value="${article.siteID}"/>",
			sid : "<c:out value="${sessionID}"/>"
		};
		channel_frame.content = "<c:out value="${article.content}"/>";
	</script>
<style>
	.input{
		font-size:12px !important;
		padding:0 !important; 
		width: 130px !important; 
		background-color: #fff !important;
	}
	#SYS_TOPIC{
		border: 1px solid #ddd;
	}
	.clear{
		clear: both;
	}
	#topicPicSmallDiv{
		margin-top: 10px; 
		width: 160px;
	}
	#topicPicMidDiv{
		width: 240px;
	}
	#topicPicBigDiv{
		width:306px;
	}
	#topicPicBigDiv,#topicPicMidDiv,#topicPicSmallDiv{
		position: relative;
	}
	
	#label{
		float: left;
	    margin-right: 5px;
	}
	.li1{
		margin-top:-10px; 
		margin-bottom:1px;
		border: 1px solid #ddd;
		border-left: 2px solid #2690e6;
		padding-top: 10px;	
		background-color: #fff	
	}
	.icon-remove{
		position: absolute;
		right: 0;
		top: 0;
		z-index: 100;
		display: none;
	}
	#a_location{
		width:500px;
	}
	#a_organizer{
		width:500px;
	}
	.a_abstract{
		height: 80px !important;
	}
	#edui1_toolbarbox{
		background-color: #fafafa;
	}
</style>
</head>
<body>
<form id="form" method="post" action="SubjectSubmit.do">
	<input type="hidden" id="isNew" name="isNew" value="<c:out value="${isNew}"/>">  
	<input type="hidden" id="a_siteID" name="a_siteID" value="<c:out value="${article.siteID}"/>">
	<input type="hidden" id="a_content" name="a_content" value="<c:out value="${article.content}"/>">
	<input type="hidden" id="a_picSmall" name="a_picSmall" value="<c:out value="${article.picSmall}"/>">
	<input type="hidden" id="a_picMiddle" name="a_picMiddle" value="<c:out value="${article.picMiddle}"/>">
	<input type="hidden" id="a_picBig" name="a_picBig" value="<c:out value="${article.picBig}"/>">
	<input type="hidden" id="a_attachments" name="a_attachments" value="">
	
	<input type="hidden" id="SYS_AUTHORS" name="SYS_AUTHORS" value="<c:out value="${article.author}"/>">
	<input type="hidden" id="DocLibID" name="DocLibID" value="<c:out value="${article.docLibID}"/>">  
	<input type="hidden" id="DocID" name="DocID" value="<c:out value="${article.docID}"/>">  
	<input type="hidden" id="UUID" name="UUID" value="<c:out value="${UUID}"/>">
	
<div class="clearfix" id="headerButton">
	<li class="idName">ID：<c:out value="${article.docID}"/></li>
	<li class="btngroup">
		<input class="btn btn-success button" type="button" id="btnSave" value="提交"/>
		<input class="btn btn-danger" type="button" id="btnCancel" value="关闭"/>
	</li>
</div>
<div class="underTop">
	<div id="divMain">
		<li class="title1" id="liTitle"><span class="left require">*</span><span class="left title1">活动主题：</span>
			<input style="text-align:left;" type="text" id="SYS_TOPIC" name="SYS_TOPIC" value="<c:out value="${article.topic}"/>"
				class="validate[maxSize[1024],required]">
		</li>
		<div style="clear: both;"></div>
		<li class="mgl"><span class="left mgr">活动规则：</span>
			<textarea id="a_abstract" name="a_abstract" class="validate[maxSize[2000]] a_abstract"><c:out value="${article.summary}"/></textarea>
		</li>
		<div style="clear: both;"></div>
		<li class="mgl"><span class="left title1 mgr">&nbsp;&nbsp;&nbsp;主办方：</span>
			<input style="text-align:left;margin-top:5px;" type="text" id="a_organizer" name="a_organizer" value="<c:out value="${article.organizer}"/>"
				class="validate[maxSize[255]]">
		</li>
		<li class="" id="liTitle"><span class="left require">*</span><span class="left title1 mgr">活动地点：</span>
			<input style="text-align:left;margin-top:5px;" type="text" id="a_location" name="a_location" value="<c:out value="${article.location}"/>"
				class="validate[maxSize[255],required]">
		</li>
		<div style="clear: both;"></div>
		<li class=""><span class="left require">*</span><span class="title1">活动时间：</span>
			<input style="margin-left: 0;" class="artMeg" type="text" data-notmodifyleft="true" placeholder="起始时间"
				id="a_startTime" name="a_startTime" readonly value="<c:out value="${article.startTime}"/>">
			-
			<input class="artMeg" type="text" data-notmodifyleft="true" placeholder="结束时间"
				id="a_endTime" name="a_endTime" readonly value="<c:out value="${article.endTime}"/>">
			<span class="title1" style="margin-left: 30px;">人数限制：</span><input class="artMeg validate[max[1000000]]" type="text" id="a_countLimited" name="a_countLimited" value="<c:out value="${article.countLimited}"/>"
					class="validate[maxSize[255]]">
		</li>
		<%@include file="../ueditor/index.html"%>
	</div>
	<div id="divRight">
		﻿<div style="border: 1px solid #ddd; background-color: #fff;">
			<div class="rightTab" id="tab1" style="display:block;">
				<div id="topicPicSmallDiv" itype="small" class="picTopic" title="列表图片">
					<c:choose>
						<c:when test="${article.picSmall != null and article.picSmall != ''}">
							<img id="picSmall" itype="small" src="../image.do?path=<c:out value="${article.picSmall}"/>"/>
							<span class="icon-remove"></span>
						</c:when>
						<c:otherwise>
							<p class="plus">+</p>
							<p class="word">小图</p>
							<span class="icon-remove"></span>
						</c:otherwise>
					</c:choose>
				</div>
				<div  id="topicPicMidDiv" itype="mid" class="picTopic" title="标题图片">
					<c:choose>
						<c:when test="${article.picMiddle != null and article.picMiddle != ''}">
							<img id="picMiddle"  src="../image.do?path=<c:out value="${article.picMiddle}"/>"/>
							<span class="icon-remove"></span>
						</c:when>
						<c:otherwise>
							<p class="plus">+</p>
							<p class="word">自定义图</p>
							<span class="icon-remove"></span>
						</c:otherwise>
					</c:choose>
				</div>
				<div id="topicPicBigDiv" itype="big" class="picTopic" title="焦点图片">
					<c:choose>
						<c:when test="${article.picBig != null and article.picBig != ''}">
							<img id="picBig"  src="../image.do?path=<c:out value="${article.picBig}"/>"/>
							<span class="icon-remove"></span>
						</c:when>
						<c:otherwise>
							<p class="plus">+</p>
							<p class="word">大图</p>
							<span class="icon-remove"></span>
						</c:otherwise>
					</c:choose>
				</div>
				<div id="localFileDiv"></div>
			</div>
		</div>
	</div>
</div>
</form>
</body>
</html>
