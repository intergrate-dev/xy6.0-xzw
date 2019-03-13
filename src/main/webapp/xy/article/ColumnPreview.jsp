<%@include file="../../e5include/IncludeTag.jsp" %>
<%@ page language="java" pageEncoding="UTF-8" %>
<html lang="zh-CN">
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
        h3  {
            font-size: 18px;
            color: #6b6b6b;
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
    </style>
    <script type="text/javascript" src="../script/jquery/jquery.min.js"></script>
    <script type="text/javascript" src="../script/bootstrap-3.3.4/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="../script/qrcode.js"></script>
    <script type="text/javascript">
        $(function(){
            $("a[id$=A]:visible:first").tab("show");

            getFrameHeight();

            /*用浏览器内部转换器实现html编解码*/
            var HtmlUtil = {
                htmlEncode:function (html){
                    var temp = document.createElement ("div");
                    (temp.textContent != undefined ) ? (temp.textContent = html) : (temp.innerText = html);
                    var output = temp.innerHTML;
                    temp = null;
                    return output;
                },
                htmlDecode:function (text){
                    var temp = document.createElement("div");
                    temp.innerHTML = text;
                    var output = temp.innerText || temp.textContent;
                    temp = null;
                    return output;
                }
            };
            var _sitehtml=$('#siteFrameVal').val();

            // var _iframe=document.createElement('iframe');
            // var blob=new Blob([decodeSiteHtml],{'type':'text/xml'});
            // $('#siteFrame').attr("src",URL.createObjectURL(blob))
            // document.getElementById('pcShowDiv').appendChild(_iframe);
            if(_sitehtml && $('#siteFrame') && $('#siteFrame')[0]){
                //var decodeSiteHtml = HtmlUtil.htmlDecode(_sitehtml);
                var decodeSiteHtml =_sitehtml;
                var _siteFrame=$('#siteFrame')[0].contentWindow.document;
                _siteFrame.open();
                _siteFrame.write(decodeSiteHtml);
                _siteFrame.close();
            }


            var _mobilehtml=$('#mobileFrameVal').val()

            if(_mobilehtml && $('#mobileFrame') && $('#mobileFrame')[0]){
               // var decodeMobileHtml = HtmlUtil.htmlDecode(_mobilehtml);
                var decodeMobileHtml = _mobilehtml;
                var _mobileFrame=$('#mobileFrame')[0].contentWindow.document;
                _mobileFrame.open();
                _mobileFrame.write(decodeMobileHtml);
                _mobileFrame.close();
            }
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
    </script>
</head>
<body onresize="getFrameHeight()" onload="getFrameHeight()">
<div class="container-fluid">
	<ul class="nav nav-tabs">
		<c:if test="${page0 == null && page1 == null}">
			<li>没有发布页</li>
		</c:if>
		<c:if test="${page0!=null||page1!=null}">
            <li >
                <a id="pcA" href="#pcShowDiv"  data-toggle="tab">页面</a>
            </li>
        </c:if>
        <c:if test="${page0!=null||page1!=null}">
            <li>
                <a id="appA" href="#appShowDiv"  data-toggle="tab">触屏页面</a>
            </li>
        </c:if>
		
	</ul>
	<div class="tab-content div-border-bottom">
		<c:if test="${page0 != null}">
		<div class="tab-pane active" id="pcShowDiv" style="height: 100%;">
			<input id="siteFrameVal" type="hidden" value="<c:out value="${page0}"/>">
            <iframe id="siteFrame" name="siteFrame"  frameborder="0" width="100%" height="100%" src=""></iframe>
            <%--<iframe id="siteFrame" name="siteFrame" srcdoc="<c:out value="${page0}"/>" frameborder="0" width="100%" height="100%" src=""></iframe>--%>
		</div>
		</c:if>
		<c:if test="${page1 != null}">
		<div class="tab-pane w-mobile-phone" id="appShowDiv">
			<div class="w-mobile-phone-screen" style="display: inline-block">
			<input id="mobileFrameVal" type="hidden" value="<c:out value="${page1}"/>">
            <iframe id="mobileFrame" name="mobileFrame" frameborder="0" width="100%" height="100%" src=""></iframe>
            <%--<iframe id="mobileFrame" name="mobileFrame" srcdoc="<c:out value="${page1}"/>" frameborder="0" width="100%" height="100%" src=""></iframe>--%>
			</div>
		</div>
		</c:if>
	</div>
</div>
</body>
</html>
