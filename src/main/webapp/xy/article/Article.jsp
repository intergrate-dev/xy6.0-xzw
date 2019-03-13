<%@include file="../../e5include/IncludeTag.jsp"%>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
	<title><%=com.founder.e5.workspace.ProcHelper.getProcName(request)%></title>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
	<meta content="IE=edge" http-equiv="X-UA-Compatible" />
	<meta name="referrer" content="never">
	<link type="text/css" rel="stylesheet" href="../../e5script/jquery/jQuery-Validation-Engine/css/validationEngine.jquery.css"/>
	<link type="text/css" rel="stylesheet" href="../script/bootstrap/css/bootstrap.min.css">
    <!--<link type="text/css" rel="stylesheet" href="../../xy/script/bootstrap-3.3.4/css/bootstrap.min.css">-->
	<link type="text/css" rel="stylesheet" href="../script/bootstrap-datetimepicker/css/datetimepicker.css" media="screen">
	<link type="text/css" rel="stylesheet" href="../script/jquery-autocomplete/styles.css"/>
	<link type="text/css" rel="stylesheet" href="../script/jquery-autocomplete/autoComplete.css"/>
	<link type="text/css" rel="stylesheet" href="../../e5script/jquery/dialog.style.css"/>
	<link rel="stylesheet" type="text/css" href="../../e5script/jquery/uploadify/uploadify.css"> 
	<link type="text/css" rel="stylesheet" href="css/article.css"/>
	
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="../script/jquery-ui-1.11.4.custom/jquery-ui.js"></script>
	<script type="text/javascript" src="../../e5script/jquery/jQuery-Validation-Engine/js/languages/jquery.validationEngine-zh_CN.js"></script>
	<script type="text/javascript" src="../../e5script/jquery/jQuery-Validation-Engine/js/jquery.validationEngine.js"></script>
	<script type="text/javascript" src="../script/bootstrap/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="../script/bootstrap-datetimepicker/bootstrap-datetimepicker.js" charset="UTF-8"></script>
	<script type="text/javascript" src="../script/bootstrap-datetimepicker/locales/bootstrap-datetimepicker.zh-CN.js" charset="UTF-8"></script>
	<script type="text/javascript" src="../script/jquery-autocomplete/jquery.autocomplete.min.js"></script>
	<script type="text/javascript" src="../script/jquery-autocomplete/autoComplete.js"></script>
	<script type="text/javascript" src="../../e5script/jquery/jquery.dialog.js"></script> 
	<script type="text/javascript" src="../../e5script/jquery/uploadify/jquery.uploadify-3.2.min.js"></script>
	<script type="text/javascript" src="../script/cookie.js"></script>
	<script type="text/javascript">
		var article = {
			UUID : "<c:out value="${UUID}"/>",
            currentColID:"<c:out value="${colID}"/>",
			colID : "<c:out value="${article.columnID}"/>",
			docID : "<c:out value="${article.docID}"/>",

			docLibID : "<c:out value="${article.docLibID}"/>",
			type : "<c:out value="${article.type}"/>",
			isNew : "<c:out value="${isNew}"/>",
			siteID : "<c:out value="${siteID}"/>",
			sourceType : "<c:out value="${sourceType}"/>",//来源是否可手工输入
			ch : "<c:out value="${ch}"/>",
			copyright : "<c:out value="${article.copyright}"/>",
			requireSensitive :"<c:out value="${hasSensitive}"/>",
			requireIllegal :"<c:out value="${hasIllegal}"/>",
			videoPlugin : "<c:out value="${videoPlugin}"/>", //视频播放控件地址
			canEditStyle : "<c:out value="${canEditStyle}"/>",//启用编辑样式
			sid : "<c:out value="${sessionID}"/>",
			userName: "<c:out value="${userName}"/>",
            userPanName: "<c:out value="${userPanName}"/>",
			userId: "<c:out value="${userId}"/>",
			url: "<c:out value="${URL}"/>",
			
			tradeCatType : "<c:out value="${tradeCatType}"/>",//稿件行业参数
			tradeRootIDs : "<c:out value="${tradeRootIDs}"/>",
			isAdmin : "<c:out value="${isAdmin}"/>", //是否为管理员用户

			//图像页跳转的前缀后缀 
			//prefixUrl:"<c:out value="${prefixUrl}"/>",
			//suffixUrl:"<c:out value="${suffixUrl}"/>",
		};

		top.window.moveTo(0,0);  
		var docElm = document.documentElement;
		//W3C  
		if (docElm.requestFullscreen) {  
			top.window.resizeTo(screen.availWidth,screen.availHeight);  
		}
		//FireFox  
		else if (docElm.mozRequestFullScreen) {  
			top.window.resizeTo(screen.availWidth,screen.availHeight);  
		}
		//Chrome等  
		else if (docElm.webkitRequestFullScreen) {  
			 top.window.resizeTo(screen.availWidth,screen.availHeight); 
		}
		//IE
		else if (document.all)   
		{  
		    top.window.resizeTo(screen.availWidth,screen.availHeight);  
		}  
	</script>
	<script type="text/javascript" src="script/json2.js"></script>
	<script type="text/javascript" src="script/article.js"></script>
	<script type="text/javascript" src="script/wordcount.js"></script>
</head>
<body>
	<c:choose>
		<c:when test="${article.type == 0}"><%@include file="inc/Article.inc"%></c:when>
		<c:when test="${article.type == 1}"><%@include file="inc/Pic.inc"%></c:when>
		<c:when test="${article.type == 2}"><%@include file="inc/Video.inc"%></c:when>
		<c:when test="${article.type == 3}"><%@include file="inc/Special.inc"%></c:when>
		<c:when test="${article.type == 4}"><%@include file="inc/Link.inc"%></c:when>
		<c:when test="${article.type == 6}"><%@include file="inc/Live.inc"%></c:when>
		<c:when test="${article.type == 7}"><%@include file="inc/Article.inc"%></c:when>
		<c:when test="${article.type == 8}"><%@include file="inc/Link.inc"%></c:when>
		<c:when test="${article.type == 9}"><%@include file="inc/File.inc"%></c:when>
		<c:when test="${article.type == 10}"><%@include file="inc/Subject.inc"%></c:when>
		<c:when test="${article.type == 11}"><%@include file="inc/Pic.inc"%></c:when>
		<c:when test="${article.type == 12}"><%@include file="inc/H5.inc"%></c:when>
		<c:when test="${article.type == 13}"><%@include file="inc/WXMenuArticle.inc"%></c:when>
	</c:choose>
	<div id="channel_frameContent" style="display:none"><c:out value="${article.content}"/></div>
	<div id="channel_frameTopic" style="display:none"><c:out value="${article.topic}"/></div>
	<div id="channel_frameAuthor" style="display:none"><c:out value="${article.author}"/></div>
	<div id="channel_frameSource" style="display:none"><c:out value="${article.source}"/></div>
	<div id="channel_frameSourceUrl" style="display:none"><c:out value="${article.sourceUrl}"/></div>
    <script type="text/javascript" src="../script/picupload/upload_api.js"></script>
	<script type="text/javascript" src="script/article-form.js"></script>
	<script type="text/javascript" src="script/article-widget.js"></script>
	<script type="text/javascript" src="script/article-rel.js"></script>
	<script type="text/javascript" src="script/channel.js"></script>
	<script type="text/javascript">


		channel_frame.content = $('#channel_frameContent').text();
		channel_frame.topic = $('#channel_frameTopic').text();
		channel_frame.author = $('#channel_frameAuthor').text();
		channel_frame.source = $('#channel_frameSource').text();
		channel_frame.sourceUrl = $('#channel_frameSourceUrl').text();

		column_form.dc = "<c:out value="${dc}"/>";
		jabbarArticle = "<c:out value="${jabbarArticle}"/>";
	</script>
    <script type="text/javascript">
        window.onload=function(){
            var tonotes=getQueryString("tonotes");
            if(tonotes=="sure"){
				$("#headerButton").hide();
                setTimeout(function(){
                    $("#tabComment").trigger('click');
                    $("#edui1_toolbarbox").remove();
                    //$("#hideLayer").show();
					$("input").attr("disabled",true);
					$("#ueditor_0").contents().find("body").attr("contenteditable","false");
                    $(".tabs li").unbind("click");
                    $(".tabs li").hide();
                    $("#tabComment").show();
                    $("#divRight>.li1").hide();
                    $("#divRight>div").css("margin-top","-20px");
                },300)

            }
        }
        function getQueryString(name){
            var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
            var r = window.location.search.substr(1).match(reg);
            if(r != null)return unescape(r[2]);
            return null;
        }
    </script>
    <div id="hideLayer" style="display:none;position:fixed;top:0;left:0;width:960px;height:3000px;z-index:1111;background:red;opacity:0"></div>
</body>
</html>
