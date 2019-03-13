<%@include file="../../e5include/IncludeTag.jsp" %>
<%@ page language="java" pageEncoding="UTF-8" %>
<%
    String path = request.getContextPath();
%>
<html lang="zh-CN">
<head>
    <title>源稿预览</title>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">

    <link type="text/css" rel="stylesheet" href="<%=path %>/xy/script/bootstrap-3.3.4/css/bootstrap.min.css">
    <style type="text/css">
        body {
            font-family: "microsoft yahei";
        }
        .div-border-bottom {
            margin-right: 0;
            margin-left: 0;
            background-color: #fff;
            border-bottom: 1px solid #ddd;
            -webkit-box-shadow: none;
            box-shadow: none;
        }
        #atricleUrl{
            line-height: 42px;
            margin-left: 50px;
        }
        .w-mobile-phone {
            background: transparent url("../../images/iphone5-frame.png") no-repeat scroll 0% 0%;
            padding: 117px 40px 238px 31px;
            position: relative;
            margin: 0px auto;
            display: block;
            width: 600px;
            /*height: 568px;*/
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
         	background-color: #f7f4f4;
         	border: 1px solid #ddd;
         	border-bottom: none;
         }
    </style>
    <script type="text/javascript" src="<%=path %>/xy/script/jquery/jquery.min.js"></script>
    <script type="text/javascript" src="<%=path %>/xy/script/bootstrap-3.3.4/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="<%=path %>/xy/script/qrcode.js"></script>
    <script type="text/javascript">
        var preview_info = {
            status: '<c:out value="${status}"/>',
            htmlStatus: '<c:out value="${result.status}"/>',
            webUrl: '${result.webUrl}',
            appUrl: '${result.appUrl}',
        };

        $(function(){
            //
//      	alert(preview_info.htmlStatus);
            if(preview_info.htmlStatus==1){
                $("a[id$=A]:visible:first").tab("show");
            }else{
                $("a[id$=A]").hide();
            }
            getFrameHeight();

            if($("#mobileFrame").size() > 0){
				var divqrcode = document.getElementById("qrcode");
				if (divqrcode) {
					var qrcode = new QRCode(divqrcode, {
						width: 96,//设置宽高
						height: 96
					});
					qrcode.makeCode(preview_info.webUrlPad);
					qrcode.makeCode(preview_info.appUrlPad);
				}
            }

            // $("#nav-tabs>li").first().find("a").append("<span></span>");
            setTimeout(function () {
                $("#nav-tabs>li").first().find("a")[0].click();
            },200)


        });

        function getFrameHeight(){
            if($("#siteFrame").length > 0){
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

        $(function(){
            $('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
                // 获取已激活的标签页的id
                var activeTab = $(e.target).attr('id');
                var url;
                if('web_url'==activeTab){
                     $("#atricleUrl").text("地址 : " + preview_info.webUrl);
                     $("#atricleUrl").attr("href",preview_info.webUrl);
                    url=preview_info.webUrl;
                }

                else if('app_url'==activeTab){
                    $("#atricleUrl").text("地址 : "+ preview_info.appUrl);
                    $("#atricleUrl").attr("href", preview_info.appUrl);
                    url=preview_info.appUrl;
                }
                $("#siteFrame").attr("src", url);
                $("#mobileFrame").attr("src", url);
            });
        });
    </script>
</head>

<body onresize="getFrameHeight()" onload="getFrameHeight()">
<c:if test="${(result.atype==3 || result.atype==4 || result.atype==5 || result.atype==6 || result.atype==8 )}">
	<c:redirect url="${result.url}"/>
</c:if>

<div class="container-fluid">
    <c:choose>
        <c:when test="${status=='success'}">
            <ul id="nav-tabs" class="nav nav-tabs">
                <c:if test="${(result.webUrl!=null||result.pcHtml!=null)}">
                    <li >
                        <a id="web_url" href="#pcShowDiv" data-href="<c:out value="${result.webUrl}"  escapeXml="false"/>" data-toggle="tab">WEB发布库</a>
                    </li>
                </c:if>
                <c:if test="${(result.appUrl!=null||result.pcHtml!=null)}">
                    <li >
                        <a id="app_url" href="#pcShowDiv" data-href="<c:out value="${result.appUrl}"  escapeXml="false"/>" data-toggle="tab">APP发布库</a>
                    </li>
                </c:if>

                <c:if test="${result.pcHtml==null &&result.appHtml==null&&result.web-url==null&&result.app-urlPad==null}">
                    <li>没有预览</li>
                </c:if>

            </ul>
            <div class="tab-content div-border-bottom">
                <div class="tab-pane active" id="pcShowDiv" style="height: 100%;">
                    <iframe id="siteFrame" frameborder="0" width="100%" height="100%" src="<c:out value="${result.url}"  escapeXml="false"/>"></iframe>
                </div>
                <div class="tab-pane w-mobile-phone" id="appShowDiv">
                    <div class="w-mobile-phone-screen" style="display: inline-block">
                        <iframe id="mobileFrame" frameborder="0" width="100%" height="100%" src="<c:out value="${result.urlPad}"  escapeXml="false"/>"></iframe>
                    </div>
                    <c:if test="${result.status==1}">
                        <div id="qrcode" tyle="display: inline-block" style="width: 150px; float: right;">
                            <canvas width="96" height="96" style="display: none;"></canvas>
                           	 <div class="surpise">扫描二维码查看</div>
                        </div>
                    </c:if>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <c:out value="${failure_message}" escapeXml="false"/>
        </c:otherwise>
    </c:choose>
</div>
</body>

</html>
