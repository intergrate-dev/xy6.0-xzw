<%@include file="../../e5include/IncludeTag.jsp" %>
<%@ page language="java" pageEncoding="UTF-8" %>
<%
    String path = request.getContextPath();
%>
<html lang="zh-CN">
<head>
    <title>阅读窗口</title>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">

    <link type="text/css" rel="stylesheet" href="<%=path %>/xy/script/bootstrap-3.3.4/css/bootstrap.min.css">
    <link type="text/css" rel="stylesheet" href="<%=path %>/xy/script/jqPhoto/css/jqueryPhoto.css">
    <link type="text/css" rel="stylesheet" href="../../e5script/jquery/dialog.style.css"/>

    <script type="text/javascript" src="<%=path %>/xy/script/jquery/jquery.min.js"></script>
    <script type="text/javascript" src="<%=path %>/xy/script/bootstrap-3.3.4/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="<%=path %>/xy/script/jqPhoto/js/jqueryPhoto.custom.js"></script>
    <script type="text/javascript" src="../../e5script/jquery/jquery.dialog.js"></script>
    <script type="text/javascript">
        $(function(){
            initJPhoto();
            //初始化轮播图
            var _thisTabId = "orignal";
            //清空之前的一些变量
            index = 0;
            clearInterval(timer);

            //如果存在组图这个模块，初始化四个参数，按钮， 自动轮播
            if($("#"+ _thisTabId + "PicDiv").size()>0){
                //初始化变量
                initVariables(_thisTabId);

                //开启自动轮播
                timer=setInterval(autoPlay,4000);
                //点击第一个图片
                $("#listBox_"+ _thisTabId).find("li :first").click();
            }
        });
    </script>
    <style type="text/css">
        body {
            font-family: "微软雅黑";
            overflow-x: hidden;
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
            /*border-left: 1px solid #ddd;*/
            /*border-right: 1px solid #ddd;*/
            -webkit-box-shadow: none;
            box-shadow: none;
        }
        .div-border-side img{
            max-width: 100%;
        }
        h3 .title {

            font-size: 18px;
            color: #6b6b6b;
        }
        .explain {
            font-weight: 100;
            color: #b7b7b7;
            font-size: 12px;
        }
        .creatTime {
            margin: 0 20px;
        }
        .keywords {
            font-weight: 100;
        }
        .dutyeditor {
            font-weight: 100;
        }
        .content {
            font-size: 16px;
        }
        #pcShowDiv .w250,#appShowDiv .w250{
            width:250px;
        }
        .tab-content>.tab-pane{
            display:block;
        }
        .main{ width:498px; margin:0 auto;}
        .mod18{width:498px;position:relative;}

        .word{  width: 329px; margin: 0 auto; line-height: 23px; text-indent: 2em;text-align:center;}

        .mod18 .cf a{display:block;width:326px;height:377px;position:absolute;color:#fff;}
        .mod18 .picBox{ width:326px; height:414px; margin-left:75px; margin-right:76px;position:relative;overflow:hidden; padding-top:40px;text-align: center;}
        .mod18 .picBox ul{height:377px;position:absolute; left:0;}
        .mod18 .picBox li{ width:326px;height:377px; text-align: center;}
        .mod18 .listBox{width:356px;height:110px;margin:0 auto;position:relative; overflow:hidden;background: rgb(209, 209, 209);}
        .mod18 .listBox li{width:67px;height:88px;cursor:pointer;position:relative;text-align: center;}
        .mod18 .listBox li a{display:block;width:108px;height:77px;}
        .left { width:498px; float:left; padding-bottom:34px; }
        .num {left:20px;}
        .mod18 #next_orignal {right: 8px;}
    body {
	  overflow-y: auto;
	  /*overflow-x:scroll;*/
	}
	body::-webkit-scrollbar {
	  width: 0px !important;
	  /*æ»šåŠ¨æ¡å½åº¦*/
	}
	body::-webkit-scrollbar-track-piece {
	  background-color: #ddd;
	  /*æ»‘é“*/
	  -webkit-border-radius: 10px;
	  /*æ»‘é“åœ†è§’å½åº¦*/
	}
	body::-webkit-scrollbar-thumb {
	  background-color: #ddd;
	  /*æ»‘åŠ¨æ¡è¡¨é¢*/
	  /*æ»‘åŠ¨æ¡è¾¹æ¡†*/
	  border-radius: 4px;
	  /*æ»‘åŠ¨æ¡åœ†è§’å½åº¦*/
	}
	/*æ¨ªç«–æ»šåŠ¨æ¡äº¤è§’*/
	body::-webkit-scrollbar-corner {
	  background-color: #ddd;
	}
	/*æ¨ªç«–æ»šåŠ¨æ¡äº¤è§’å›¾æ¡ˆ*/
	body::-webkit-resizer {
	  background-repeat: no-repeat;
	  background-position: bottom right;
	}
	/*é¼ æ ‡æ»‘è¿‡æ»‘åŠ¨æ¡*/
	body::-webkit-scrollbar-thumb:hover {
	  background-color: #ddd;
	}
    </style>
</head>

<body>
<div class="container-fluid">
    <div style="border-bottom: 0;" class="tab-content div-border-bottom">
        <!-- ******************************************** 原稿 ******************************************* -->
        <c:if test="${article!=null}">
            <div class="tab-pane div-border-side" id="orignalShowDiv">
                <div class="row div-border-side">
                    <!-- 文章 div -->
                    <div class="col-md-8 center-block articleDiv">
                        <!-- 标题 -->
                        <div class="row  text-center"><h3>
                            <strong class='title'><c:out value="${article.topic}" escapeXml="false"/></strong></h3>
                        </div>
                        <!-- END 标题 -->
                        <!-- 副标题 -->
                        <div class="row text-right">
                            <h4><strong><c:out value="${article.subTitle}" escapeXml="false"/></strong></h4></div>
                        <!-- END 副标题 -->

                        <!-- 作者 -->
                        <div class="row text-center">
                            <strong class="explain">作者：</strong><strong class="explain"><c:out value="${article.author}"/></strong>
                            <span class="creatTime"><strong class="explain">创建日期：</strong><strong class="explain"><c:out value="${article.createDate}"/></strong></span>
                            <strong class="explain">来源：</strong><strong class="explain"><c:out value="${article.source}"/></strong>
                        </div>
                        <!-- END 作者 -->

                        <div class="row text-justify">
                            <blockquote><p style="font-size: 80%;">
                                <strong>摘要：</strong><c:out value="${article.summary}" escapeXml="false"/></p>
                            </blockquote>
                        <!-- 组图轮播 -->
                        <c:if test="${imageArray!=null && imageArray.size()>0}">
                            <!--效果html开始-->
                            <div id="orignalPicDiv" class="row main">
                                <div class="left">
                                    <div class="mod18">
                                        <span id="prev_orignal" class="btn prev"></span>
                                        <span id="next_orignal" class="btn next"></span>
                                        <span id="prevTop_orignal" class="btn prev"></span>
                                        <span id="nextTop_orignal" class="btn next"></span>

                                        <div id="picBox_orignal" class="picBox">
                                            <ul class="cf">
                                                <c:forEach items="${imageArray}" var="item">
                                                    <li>
                                                    <a href="${item.src==null?"javascript:void(0);":item.src}" target="_blank"><span style="height:100%;display:inline-block;vertical-align:middle;"></span><img style="max-width: 326px;max-height: 377px; vertical-align:middle;" src="${item.src}" alt=""></a>

                                                    </li>
                                                </c:forEach>
                                            </ul>
                                        </div>
                                        <div id="comments_orignal" class="text-justify" style="height: 72px;overflow: auto;">

                                            <c:forEach items="${imageArray}" var="item">

                                                <p class="word" style="display:none;">${item.title}</p>
                                            </c:forEach>
                                        </div>
                                        <div id="listBox_orignal" class="listBox">
                                            <ul style="width: 1792px; left: -512px;" class="cf">
                                                <c:forEach items="${imageArray}" var="item" varStatus="status">
                                                    <li><i class="arr2" style="height:100%;display:inline-block;vertical-align:middle;"></i>
                                                        <img style="max-width: 54px; max-height: 83px;vertical-align:middle;" src="${item.src}" alt="">
                                                        <span class="num">${status.index + 1}/<c:out value="${imageArray.size()}"/></span>
                                                    </li>
                                                </c:forEach>
                                            </ul>
                                        </div>
                                        <div class="clear"></div>
                                    </div>
                                    <div class="clear"></div>
                                </div>
                                <div class="clear"></div>
                            </div>
                            <!--效果html结束-->
                        </c:if><!-- END 组图轮播 -->
                        <!-- 视频预览 -->
                        <div class="row text-center">
                            <p>
                                <c:forEach items="${videoList}" var="item" varStatus="status">
                                <embed type="application/x-shockwave-flash" class="edui-faked-video"
                                       pluginspage="http://www.macromedia.com/go/getflashplayer" width="420"
                                       height="280" wmode="transparent" play="true" loop="false" menu="false"
                                       allowscriptaccess="never" allowfullscreen="true"
                                       src="${videoplugin}?src=${item}"
                                       title="Adobe Flash Player">
                                    </c:forEach>
                            </p>
                        </div><!-- END 视频预览 -->
                        <!-- 正文 -->
                        <div class="row text-justify content" style="overflow: hidden;overflow-x: auto;width:99%;margin:0 auto;">
                            <c:out value="${article.content}" escapeXml="false"/>
                        </div>
                        <!-- END 正文 -->
                        <div class="row" style="margin-bottom: 10px;">
                            <strong class="col-xs-6 keywords">关键词：<c:out value="${article.keyword}"/></strong>
                            <strong class="col-xs-6 dutyeditor">责任编辑: <c:out value="${article.liability}"/></strong>
                        </div>
                    </div>
                    <!-- 文章 div -->
                </div>
                <!-- END 文章 + 附属信息 div -->
            </div>
            <!-- END 原稿 -->
        </c:if>
    </div>
</div>
</body>
</html>
