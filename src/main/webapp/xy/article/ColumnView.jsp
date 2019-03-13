<%@include file="../../e5include/IncludeTag.jsp" %>
<%@ page language="java" pageEncoding="UTF-8" %>
<html lang="zh-CN">
<head>
    <title>查看发布页</title>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
    <link type="text/css" rel="stylesheet" href="../script/bootstrap-3.3.4/css/bootstrap.min.css">
    <style type="text/css">
        body {
            font-family: "微软雅黑";
        }
        .div-border-bottom {
            margin-right: 0;
            margin-left: 0;
            background-color: #fff;
            border-bottom: 1px solid #ddd;
            -webkit-box-shadow: none;
            box-shadow: none;
        }
        .div-border-side {
            margin-right: 0;
            margin-left: 0;
            background-color: #fff;
            -webkit-box-shadow: none;
            box-shadow: none;
        }
        .meta_title_div {
            background-color: #F1F1F1;
            margin: 0 0 0 0;
            padding: 10px 15px 10px 15px;
            font-weight: bold;
            border-left: 1px solid #ddd;
            border-right: 1px solid #ddd;
        }
        .gray {
            font-weight: 100;
            color: #4e4c4c;
            font-size: 12px;
        }
        h3 .title {

            font-size: 18px;
            color: #6b6b6b;
        }
        .paddingrow {
            margin-left: 0;
            margin-right: 0;
        }

        .w-mobile-phone {
            background: transparent url("../../images/iphone5-frame.png") no-repeat scroll 0% 0%;
            padding: 117px 40px 238px 31px;
            position: relative;
            margin: 0px auto;
            display: block;
            width: 600px;
        }
        .w-mobile-phone-screen {
            width: 320px;
            height: 568px;
            position: relative;
        }
         #qrcode img{
        	border: 1px solid #ddd;
        	padding: 5px;
        }
         #qrcode .surpise{
         	width: 108px;
         	text-align: center;
         	background-color: #F7F4F4;
         	border: 1px solid #ddd;
         	border-bottom: none;
         }
    </style>
    <script type="text/javascript" src="../script/jquery/jquery.min.js"></script>
    <script type="text/javascript" src="../script/bootstrap-3.3.4/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="../script/qrcode.js"></script>
    <script type="text/javascript">
        $(function(){
            $("a[id$=A]:visible:first").tab("show");
            getFrameHeight();
			
			<c:if test="${page0 != null}">$("#form0").submit();</c:if>
			<c:if test="${page1 != null}">$("#form1").submit();</c:if>

            //当页面内某个a标签链接未被添加时 给出相应提示
            $("#siteFrame").on("load", function(event){
                $("#container ul li a", this.contentDocument).on('click', function() {
                    if ($(this).prop('href').indexOf('undefined') > -1) {
                        alert('您未对此添加相应的链接，无法跳转。');
                        return false;
                    }
                })
            });
        });

        function getFrameHeight(){
            if($("#siteFrame").size() > 0){
                var _ulH = Math.ceil($(".nav.nav-tabs").height());
                var _winH = $(window).height();
                var _frameH = _winH - _ulH - 7;
                $("#siteFrame").height(_frameH);
            }
        }
        var showed = false;
        function hideTab(type,hasHtml,hasHtmlPad){
            if(hasHtml == "true"){
                $("#pcA").show();
            }
            if(hasHtmlPad == "true"){
                $("#appA").show();
            }
            if(!showed){
                $("a[id$=A]:visible:first").tab("show");
                showed = true;
            }
        }
    </script>
</head>
<body onresize="getFrameHeight()" onload="getFrameHeight()">
<form id="form0" target="siteFrame" method="post" action="previewhtml.jsp" style="display:none;">
	<textarea name="html"><c:out value="${path0}"/></textarea>
</form>
<form id="form1" target="mobileFrame" method="post" action="previewhtml.jsp" style="display:none;">
	<textarea name="html"><c:out value="${path1}"/></textarea>
</form>
<div class="container-fluid">
	<ul class="nav nav-tabs">
		<c:if test="${path0 != null}">
		<li>
			<a id="pcA" href="#pcShowDiv" data-href="<c:out value="${path0}"/>" data-toggle="tab">网站</a>
		</li>
		</c:if>
		<c:if test="${path1 != null}">
		<li>
			<a id="appA" href="#appShowDiv" data-href="<c:out value="${path1}"/>" data-toggle="tab">触屏</a>
		</li>
		</c:if>
		<c:if test="${path0 == null && path1 == null}">
			<li>没有发布页</li>
		</c:if>
	</ul>

	<div class="tab-content div-border-bottom">
		<c:if test="${path0 != null}">
		<div class="tab-pane active" id="pcShowDiv" style="height: 100%;">
			<iframe id="siteFrame" name="siteFrame" frameborder="0" width="100%" height="100%" src="<c:out value="${path0}"/>"></iframe>
		</div>
		</c:if>
		<c:if test="${path1 != null}">
		<div class="tab-pane w-mobile-phone" id="appShowDiv">
			<div class="w-mobile-phone-screen" style="display: inline-block">
				<iframe id="mobileFrame" name="mobileFrame" frameborder="0" width="100%" height="100%" src="<c:out value="${path1}"/>"></iframe>
			</div>
		</div>
		</c:if>
	</div>
</div>
</body>
</html>
