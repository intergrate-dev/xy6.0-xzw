<%@include file="../../e5include/IncludeTag.jsp" %>
<%@ page language="java" pageEncoding="UTF-8" %>
<%
    String path = request.getContextPath();
%>
<html lang="zh-CN">
<head>
    <title>稿件详情</title>
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
        var img = '<c:out value="${pcImageArray}" escapeXml="{true}" />';
        var showTab = '<c:out value="${libName}"/>';
        var nowTab = '<c:out value="${libName}"/>';
        var isCharity = '<c:out value="${isCharity}"/>';
        var charityDocId = '<c:out value="${charityDocId}"/>';
        var charityLibId = '<c:out value="${charityLibId}"/>';
        $(function(){
            initJPhoto(showTab);
            showTab = "#" + showTab + "A";
            $(showTab).click();
            /*var libId =
            <c:out value="${pcArticle.docLibID}"/>;
             var docId =
            <c:out value="${pcArticle.docID}"/>;
             var authorId =
            <c:out value="${pcArticle.docID}"/>;*/

            if(isCharity == "true"){
                findCountPraise();
            }
            $(".getTrace").attr("href","");
            $(".getTrace").on("click",function(){
                var _this=$(this);
                var search=window.location.search;
                _this.attr("href","../../xy/ueditor/getTrace.do"+search);
            })
            $(".seeNotes").attr("href","");
            $(".seeNotes").on("click",function(){
                var _this=$(this);
                var search=window.location.search.replace(/&UUID=[0-9]*/,"");
                _this.attr("href","../../xy/article/Article.do"+search+"&tonotes=sure");
            });
            $('.endTime').each(function(){
                var timeVal = $(this).html();
                if (timeVal != '&nbsp;') {
                    var endTime = timeVal.substring(0, timeVal.length - 2);
                    $(this).html(endTime);
                }
            });
        });

        function findCountPraise(){
            var param = {
                id: charityDocId,
                type: 2,
                siteID: 1,
                source: 3
            };
            $.ajax({
                url: "../../api/app/getDiscussCount.do",
                type: "post",
                dataType: "json",
                data: param,
                async: false,
                success: function(json){
                    var count = 0;
                    if(json && json.count){
                        count = json.count;
                    }
                    $("#appExtTable").append('<tr><th width="150px;">当前爱心数</th><td>'+count+'</td></tr>');
                }
            });
        }

        function showDialog(libId, docId){
            var dataUrl = "../../e5workspace/DocView.do?DocLibID=" + libId + "&DocIDs=" + docId;
            var editDialog = e5.dialog({
                type: "iframe",
                value: dataUrl
            }, {
                title: "更多信息",
                width: "650px",
                height: "700px",
                resizable: false,
                fixed: true
            });
            editDialog.show();
        }

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
            /* margin-right: 0;*/
            margin-left: 0;
            background-color: #fff;
            /*border-left: 1px solid #ddd;*/
            /*border-right: 1px solid #ddd;*/
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
            white-space: normal;
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

        .piclist {
            width: 100%;
            background-color: #fff;
            border: 1px solid #ddd;
        }

        .paddingrow {
            margin-left: 0;
            margin-right: 0;
        }

        .marginright {
            margin-right: 0;
        }

        .widgetUl {
            list-style: none;
            margin-bottom: 5px;
            line-height: 20px;
            box-sizing: inherit;
        }

        .summary_style {
            border-left: 4px solid blue;
            padding-left: 4px;
            margin-left: 4px;
        }

        .content {
            font-size: 16px;
        }
        .getTrace{
            font-size:12px;
        }
        .seeNotes{
            font-size:12px;
        }
        /* .col-md-2{
            width:11%;
        } */
        #pcShowDiv .w250,#appShowDiv .w250{
            width:250px;
        }
        .seeNotes {
        	display: none;
        }
    </style>

</head>

<body>
<div class="container-fluid">
    <ul class="nav nav-tabs">
        <c:if test="${orignal!=null}">
            <li><a id="orignalA" href="#orignalShowDiv" data-toggle="tab">原稿</a></li>
        </c:if>
        <c:if test="${pcArticle!=null}">
            <li><a id="pcA" href="#pcShowDiv" data-toggle="tab">Web稿件</a></li>
        </c:if>
        <c:if test="${appArticle!=null}">
            <li><a id="appA" href="#appShowDiv" data-toggle="tab">App稿件</a></li>
        </c:if>
    </ul>

    <div style="border-bottom: 0;" class="tab-content div-border-bottom">
        <!-- ******************************************** 原稿 ******************************************* -->
        <c:if test="${orignal!=null}">
            <div class="tab-pane div-border-side" id="orignalShowDiv">
                <!-- 文章属性信息 div -->
                <div class="row div-border-bottom" style="padding: 10px 0 10px 0;white-space: nowrap;">
                    <div class="col-md-3">
                        <strong class="gray">稿件ID：</strong><strong class="gray"><c:out value="${orignal.docID}"/></strong>
                    </div>
                    <div class="col-md-3">
                        <strong class="gray">稿件类型：</strong><strong class="gray"><c:out value="${orignal.typeName}"/></strong>
                    </div>
            <c:if test="${orignal.type ==0}">
                <div class="col-md-3">
                    <a class="getTrace" target="_blank" href="">修改痕迹</a>
                </div>
                <div class="col-md-3">
                    <a class="seeNotes" target="_blank" href="">查看批注</a>
                </div>
            </c:if>
                <%--<div class="col-md-4"> <strong>稿件状态：</strong> <c:out value="${orignal.statusName}"/></div>
            --%></div>
                <!-- END 文章属性信息 div -->

                <!-- 文章 + 附属信息 div -->
                <div class="row div-border-side">
                    <!-- 文章 div -->
                    <div class="col-md-8 center-block articleDiv">
                        <!-- 标题 -->
                        <div class="row  text-center"><h3>
                            <strong class='title'><c:out value="${orignal.topic}" escapeXml="false"/></strong></h3>
                        </div>
                        <!-- END 标题 -->
                        <!-- 副标题 -->
                        <div class="row text-right">
                            <h4><strong><c:out value="${orignal.subTitle}" escapeXml="false"/></strong></h4></div>
                        <!-- END 副标题 -->

                        <!-- 作者 -->
                        <div class="row text-center">
                            <strong class="explain">作者：</strong><strong class="explain"><c:out value="${orignal.author}"/></strong>
                            <span class="creatTime"><strong class="explain">创建日期：</strong><strong class="explain"><c:out value="${orignal.createDate}"/></strong></span>
                            <strong class="explain">来源：</strong><strong class="explain"><c:out value="${orignal.source}"/></strong>
                        </div>
                        <!-- END 作者 -->

                        <div class="row text-justify">
                            <blockquote><p style="font-size: 80%;">
                                <strong>摘要：</strong><c:out value="${orignal.summary}" escapeXml="false"/></p>
                            </blockquote>
                        </div>

                        <!-- 组图轮播 -->
                        <c:if test="${orignalImageArray!=null && orignalImageArray.size()>0}">
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
                                                <c:forEach items="${orignalImageArray}" var="item">
                                                    <li>
                                                        <a href="${item.src==null?"javascript:void(0);":item.src}" target="_blank"><img style="max-width: 526px;max-height: 377px;" src="${item.src}" alt=""></a>

                                                    </li>
                                                </c:forEach>
                                            </ul>
                                        </div>
                                        <div id="comments_orignal" class="text-justify" style="height: 72px;overflow: auto;">

                                            <c:forEach items="${orignalImageArray}" var="item">

                                                <p class="word" style="display:none;">${item.title}</p>
                                            </c:forEach>
                                        </div>
                                        <div id="listBox_orignal" class="listBox">
                                            <ul style="width: 1792px; left: -512px;" class="cf">
                                                <c:forEach items="${orignalImageArray}" var="item" varStatus="status">
                                                    <li><i class="arr2"></i>
                                                        <img style="max-width: 114px; max-height: 83px;" src="${item.src}" alt="">
                                                        <span class="num">${status.index + 1}/<c:out value="${orignalImageArray.size()}"/></span>
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
                                <c:forEach items="${orignalVideoList}" var="item" varStatus="status">
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
                            <c:out value="${orignal.content}" escapeXml="false"/>
                        </div>
                        <!-- END 正文 -->
                        <div class="row" style="margin-bottom: 10px;">
                            <strong class="col-xs-6 keywords">关键词：<c:out value="${orignal.keyword}"/></strong>
                            <strong class="col-xs-6 dutyeditor">责任编辑: <c:out value="${orignal.liability}"/></strong>
                        </div>
                        <!--<div class="row text-right">
                            
                        </div>-->

                    </div>
                    <!-- 文章 div -->
                    <!-- 文章附属信息 -->
                    <div class="col-md-4 div-border-side">
                        <!-- 标题图 -->
                        <div class="row paddingrow">
                            <div class="meta_title_div">标题图</div>
                            <div class="col-xs-12 piclist" style="margin-bottom: 20px;">
                                <c:choose>
                                    <c:when test="${smallTitlePic != null || midTitlePic != null || bigTitlePic != null}">
                                        <c:if test="${smallTitlePic!=null}">
                                            <div>
                                                <div>
                                                    <div style="text-align: left;">
                                                        <span>小</span>
                                                    </div>
                                                    <div>
                                                        <a target="_blank" href="<c:out value="${smallTitlePic}"/>" class="thumbnail">
                                                            <img style="width:auto;" src="<c:out value="${smallTitlePic}"/>"/>
                                                        </a>
                                                    </div>
                                                </div>
                                            </div>
                                        </c:if>
                                        <c:if test="${midTitlePic!=null}">
                                            <tr>
                                                <td>
                                                    <div style="text-align: left;">
                                                        <span>中</span>
                                                    </div>
                                                    <div>
                                                        <a target="_blank" href="<c:out value="${midTitlePic}"/>" class="thumbnail">
                                                            <img style="width:auto;" src="<c:out value="${midTitlePic}"/>"/>
                                                        </a>
                                                    </div>
                                                </td>
                                            </tr>
                                        </c:if>
                                        <c:if test="${bigTitlePic!=null}">
                                            <tr>
                                                <td>
                                                    <div style="text-align: left;">
                                                        <span>大</span>
                                                    </div>
                                                    <div>
                                                        <a target="_blank" href="<c:out value="${bigTitlePic}"/>" class="thumbnail">
                                                            <img style="width:auto;" src="<c:out value="${bigTitlePic}"/>"/>
                                                        </a>
                                                    </div>
                                                </td>
                                            </tr>
                                        </c:if>
                                    </c:when>
                                    <c:otherwise>
                                        <tr>
                                            <td><span style="color: lightgray">无标题图</span></td>
                                        </tr>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                        <!-- END 标题图 -->

                        <!-- 扩展字段 -->
                        <c:if test="${orignal.type != 4 and orignal.type != 8 and orignal.type != 3 and orignal.type != 6 }">
                            <div class="row paddingrow">
                                <div style="border-top: 1px solid #ddd;" class="meta_title_div">扩展字段</div>
                                <table class="table table-bordered">
                                    <c:choose>
                                        <c:when test="${orignalExSet.size()>0}">
                                            <c:forEach items="${orignalExSet}" var="item">
                                                <tr>
                                                    <th width="150px;">${item.ext_name==null?"&nbsp;":item.ext_name}</th>
                                                    <td>${item.ext_value==null?"&nbsp;":item.ext_value}</td>
                                                </tr>
                                            </c:forEach>
                                        </c:when>
                                        <c:otherwise>
                                            <tr>
                                                <td><span style="color: lightgray">无扩展字段</span></td>
                                            </tr>
                                        </c:otherwise>
                                    </c:choose>
                                </table>
                            </div>
                        </c:if>
                        <!-- END 扩展字段 -->
                    </div>
                    <!-- END 文章附属信息 -->
                </div>
                <!-- END 文章 + 附属信息 div -->

                <!-- 流程记录 -->
                <div class="row div-border-side marginright">
                    <div style="border-top: 1px solid #ddd;" class="meta_title_div">流程记录</div>
                    <table class="table table-bordered">
                        <c:choose>
                            <c:when test="${(orignalRecord != null)}">
                                <tr>
                                    <th style="width: 80px;">姓名</th>
                                    <th style="width: 180px;">操作</th>
                                    <th style="width: 180px;">操作时间</th>
                                    <th>备注</th>
                                </tr>
                                <c:forEach items="${orignalRecord}" var="item">
                                    <tr>
                                        <td>${item.operator==null?"&nbsp;":item.operator}</td>
                                        <td>${item.operation==null?"&nbsp;":item.operation}</td>
                                        <td class="endTime">${item.endTime==null?"&nbsp;":item.endTime}</td>
                                        <td>${item.detail==null?"&nbsp;":item.detail}</td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td><span style="color: lightgray">无流程记录</span></td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                    </table>
                </div>
                <!-- END 流程记录 -->
            </div>
            <!-- END 原稿 -->
        </c:if>


        <!-- ******************************************** PC稿件 ******************************************* -->
        <c:if test="${pcArticle!=null}">
            <div class="tab-pane div-border-side" id="pcShowDiv">
                <!-- 文章属性信息 div -->
                <div class="row div-border-bottom" style="padding: 10px 0 10px 0;white-space: nowrap;">
                    <div class="col-md-2">
                        <strong class="gray">稿件ID：</strong><strong class="gray"><c:out value="${pcArticle.docID}"/></strong>
                    </div>
                    <div class="col-md-2">
                        <strong class="gray">稿件类型：</strong><strong class="gray"><c:out value="${pcArticle.typeName}"/></strong>
                    </div>
                    <div class="col-md-2">
                        <strong class="gray">栏目：</strong><strong class="gray"><c:out value="${pcArticle.column}"/>&nbsp;(<c:out value="${pcArticle.columnID}"/>)</strong>
                    </div>
                    <div class="col-md-2">
                        <strong class="gray">稿件状态：</strong><strong class="gray"><c:out value="${pcArticle.statusName}"/></strong>
                    </div>
                    <c:if test="${batManInfo!=null}">
                        <div class="col-md-2 w250">
                            <strong class="gray">通讯员：</strong><strong class="gray" id="bm_info"><c:out value="${bt_name}"></c:out>&nbsp;<c:out value="${bt_phone}"></c:out></strong>
                        </div>
                    </c:if>
                    <c:if test="${batManInfo!=null}">
                        <div class="col-md-2">
                            <strong class="gray">QQ：</strong><strong class="gray"><a href="http://wpa.qq.com/msgrd?V=1&uin=<c:out value="${bt_qq}"/>&exe=qq&Site=qq&menu=yes"><c:out value="${bt_qq}"></c:out></a></strong>
                        </div>
                    </c:if>
                    <c:if test="${pcArticle.type ==0}">
                        <div class="col-md-2">
                            <a class="getTrace" target="_blank" href="">修改痕迹</a>
                        </div>
                        <div class="col-md-2">
                            <a class="seeNotes" target="_blank" href="">查看批注</a>
                        </div>
                    </c:if>
                </div>
                <!-- END 文章属性信息 div -->

                <!-- 文章 + 附属信息 div -->
                <div class="row div-border-side">
                    <!-- 文章 div -->
                    <div class="col-md-8 center-block articleDiv">
                        <!-- 标题 -->
                        <div class="row  text-center articleDiv"><h3>
                            <strong class="title"><c:out value="${pcArticle.topic}" escapeXml="false"/></strong></h3>
                        </div>
                        <!-- END 标题 -->
                        <!-- 副标题 -->
                        <div class="row text-right articleDiv">
                            <h4><strong><c:out value="${pcArticle.subTitle}" escapeXml="false"/></strong></h4></div>
                        <!-- END 副标题 -->

                        <!-- 作者 -->
                        <div class="row text-center">
                            <strong class="explain">作者：</strong><strong class="explain"><c:out value="${pcArticle.author}"/></strong>
                            <span class="creatTime"><strong class="explain">创建日期：</strong><strong class="explain"><c:out value="${pcArticle.createDate}"/></strong></span>
                            <strong class="explain">来源：</strong><strong class="explain"><c:out value="${pcArticle.source}"/></strong>
                            <strong class="explain">话题：</strong><strong class="explain"><c:out value="${pcArticle.topics}"/></strong>
                        </div>
                        <!-- END 作者 -->

                        <div class="row text-justify">
                            <blockquote><p style="font-size: 80%;">
                                <strong>摘要：</strong><c:out value="${pcArticle.summary}" escapeXml="false"/></p>
                            </blockquote>
                        </div>

                        <!-- 组图轮播 -->
                        <c:if test="${pcImageArray!=null && pcImageArray.size()>0}">
                            <!--效果html开始-->
                            <div id="pcPicDiv" class="row main">
                                <div class="left">
                                    <div class="mod18">
                                        <span id="prev_pc" class="btn prev"></span>
                                        <span id="next_pc" class="btn next"></span>
                                        <span id="prevTop_pc" class="btn prev"></span>
                                        <span id="nextTop_pc" class="btn next"></span>

                                        <div id="picBox_pc" class="picBox">
                                            <ul class="cf">
                                                <c:forEach items="${pcImageArray}" var="item" varStatus="status">
                                                    <li>
                                                        <a href="${item.src==null?"javascript:void(0);":item.src}" target="_blank"><img style="max-width: 526px;max-height: 377px;" src="${item.src}" alt=""></a>
                                                    </li>
                                                </c:forEach>
                                            </ul>
                                        </div>
                                        <div id="comments_pc" class="text-justify" style="height: 72px;overflow: auto;">
                                            <c:forEach items="${pcImageArray}" var="item" varStatus="status">
                                                <p class="word" style="display:none;">
                                                    <span class="num">${status.index + 1}/<c:out value="${pcImageArray.size()}"/></span>${item.title}
                                                </p>
                                            </c:forEach>
                                        </div>
                                        <div id="listBox_pc" class="listBox">
                                            <ul style="width: 1792px; left: -512px;" class="cf">
                                                <c:forEach items="${pcImageArray}" var="item" varStatus="status">
                                                    <li><i class="arr2"></i>
                                                        <img style="max-width: 114px; max-height: 83px;" src="${item.src}" alt="">
                                                        <span class="num">${status.index + 1}/<c:out value="${pcImageArray.size()}"/></span>
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
                                <c:forEach items="${pcVideoList}" var="item" varStatus="status">
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
                        <div class="row text-justify content" style="overflow: hidden;overflow-x: auto; width: 99%; margin: 0 auto;">
                            <c:out value="${pcArticle.content}" escapeXml="false"/>
                        </div>
                        <!-- END 正文 -->

                        <div class="row articleDiv" style="margin-bottom: 10px;">
                            <strong class="col-xs-6 keywords">关键词：<c:out value="${pcArticle.keyword}"/></strong>
                            <strong class="col-xs-6 dutyeditor">责任编辑: <c:out value="${pcArticle.liability}"/></strong>
                        </div>
                        <!--<div class="row text-right articleDiv">

                        </div>-->

                    </div>
                    <!-- 文章 div -->
                    <!-- 文章附属信息 -->
                    <div class="col-md-4 div-border-side">
                        <!-- 标题图 -->
                        <div class="row paddingrow">
                            <div class="meta_title_div">标题图</div>
                            <div class="col-xs-12 piclist">
                                <c:choose>
                                    <c:when test="${pcSmallTitlePic != null || pcMidTitlePic != null || pcBigTitlePic != null}">
                                        <c:if test="${pcSmallTitlePic!=null}">
                                            <div>
                                                <div>
                                                    <div style="text-align: left;">
                                                        <span>小</span>
                                                    </div>
                                                    <div>
                                                        <a target="_blank" href="<c:out value="${pcSmallTitlePic}"/>" class="thumbnail">
                                                            <img style="width:auto;" src="<c:out value="${pcSmallTitlePic}"/>"/>
                                                        </a>
                                                    </div>
                                                </div>
                                            </div>
                                        </c:if>
                                        <c:if test="${pcMidTitlePic!=null}">
                                            <tr>
                                                <td>
                                                    <div style="text-align: left;">
                                                        <span>中</span>
                                                    </div>
                                                    <div>
                                                        <a target="_blank" href="<c:out value="${pcMidTitlePic}"/>" class="thumbnail">
                                                            <img style="width:auto;" src="<c:out value="${pcMidTitlePic}"/>"/>
                                                        </a>
                                                    </div>
                                                </td>
                                            </tr>
                                        </c:if>
                                        <c:if test="${pcBigTitlePic!=null}">
                                            <tr>
                                                <td>
                                                    <div style="text-align: left;">
                                                        <span>大</span>
                                                    </div>
                                                    <div>
                                                        <a target="_blank" href="<c:out value="${pcBigTitlePic}"/>" class="thumbnail">
                                                            <img style="width:auto;" src="<c:out value="${pcBigTitlePic}"/>"/>
                                                        </a>
                                                    </div>
                                                </td>
                                            </tr>
                                        </c:if>
                                    </c:when>
                                    <c:otherwise>
                                        <tr>
                                            <td><span style="color: lightgray">无标题图</span></td>
                                        </tr>
                                    </c:otherwise>
                                </c:choose>
                                </table>
                            </div>
                            <!-- END 标题图 -->

                            <c:if test="${pcArticle.type != 4 and pcArticle.type != 8 and pcArticle.type != 3 and pcArticle.type != 6 }">
                                <div class="row  paddingrow" style="margin-top: 42px;">
                                    <div style="border-top: 1px solid #ddd;" class="meta_title_div">扩展字段</div>
                                    <table class="table table-bordered">
                                        <c:choose>
                                            <c:when test="${pcExSet.size()>0}">
                                                <c:forEach items="${pcExSet}" var="item">
                                                    <tr>
                                                        <th width="150px;">${item.ext_name==null?"&nbsp;":item.ext_name}</th>
                                                        <td>${item.ext_value==null?"&nbsp;":item.ext_value}</td>
                                                    </tr>
                                                </c:forEach>
                                            </c:when>
                                            <c:otherwise>
                                                <tr>
                                                    <td><span style="color: lightgray">无扩展字段</span></td>
                                                </tr>
                                            </c:otherwise>
                                        </c:choose>
                                    </table>
                                </div>
                            </c:if>
                            <!-- 稿件发布地址 -->
                            <div class="row paddingrow">
                                <div style="border-top: 1px solid #ddd;" class="meta_title_div">稿件发布地址</div>
                                <table class="table table-bordered">
                                    <tr>
                                        <th nowrap>网站</th>
                                        <td style="word-break: break-all;">
                                            <a href="<c:out value="${pcArticle.url}" escapeXml="false" />" target="_blank"><c:out value="${pcArticle.url}" escapeXml="false"/></a>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th>触屏</th>
                                        <td style="word-break: break-all;">
                                            <a href="<c:out value="${pcArticle.urlPad}" escapeXml="false" />" target="_blank"><c:out value="${pcArticle.urlPad}" escapeXml="false"/></a>
                                        </td>
                                    </tr>
                                </table>
                            </div>
                            <!-- END 稿件链接  -->
                            <div class="row paddingrow">
                                <div class="meta_title_div">话题</div>
                                    <table class="table table-bordered">
                                    <tr>
                                        <td width="150px;">
                                            ${pcArticle.topics==null||"".equals(pcArticle.topics)?"<span style=\"color: lightgray\">当前稿件无话题</span>":pcArticle.topics}
                                            <c:if test="${pcArticle.topics != null && pcArticle.topics != ''}">

                                            </c:if>
                                        </td>
                                    </table>
                            </div>
                            <!-- 模板 -->
                                <%----%>
                            <div class="row paddingrow">
                                <div style="border-top: 1px solid #ddd;" class="meta_title_div">模板</div>
                                <table class="table table-bordered">
                                    <tr>
                                        <th nowrap>网站</th>
                                        <td style="word-break: break-all;"><c:out value="${pGroupName}"/> ---
                                            <c:out value="${pTempName}"/></td>
                                    </tr>
                                    <tr>
                                        <th>触屏</th>
                                        <td style="word-break: break-all;"><c:out value="${pGroupPadName}"/> ---
                                            <c:out value="${pTempPadName}"/></td>
                                    </tr>
                                </table>
                            </div>
                            <!-- END 模版  -->
                            <div class="row paddingrow">
                                <div style="border-top: 1px solid #ddd;" class="meta_title_div">关联栏目</div>
                                <table class="table table-bordered">
                                    <tr>
                                        <td width="150px;">
                                                ${pcArticle.columnRel==null||"".equals(pcArticle.columnRel)?"<span style=\"color: lightgray\">无关联栏目</span>":pcArticle.columnRel}<c:if test="${pcArticle.columnRel != null && pcArticle.columnRel != ''}">&nbsp;(${pcArticle.columnRelID})</c:if></td>
                                </table>
                            </div>
                            <c:if test="${pcArticle.type != 4 and pcArticle.type != 8 and pcArticle.type != 3 and pcArticle.type != 6 }">
                                <div class="row paddingrow">
                                    <div style="border-top: 1px solid #ddd;" class="meta_title_div">相关稿件</div>
                                    <table class="table table-bordered">
                                        <c:choose>
                                            <c:when test="${pcRelSet.size()>0}">
                                                <c:forEach items="${pcRelSet}" var="item" varStatus="index">
                                                    <tr>
                                                        <td>${index.index + 1}.
                                                            <a href='${item.contentURL==null||item.contentURL==""?"javascript:void(0);":item.contentURL}' target="_blank">${item.topic==null?"":item.topic}</a>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </c:when>
                                            <c:otherwise>
                                                <tr>
                                                    <td><span style="color: lightgray">无相关稿件</span></td>
                                                </tr>
                                            </c:otherwise>
                                        </c:choose>
                                    </table>
                                </div>
                            </c:if>

                            <div class="row paddingrow" style="margin-bottom: 20px;">
                                <a href="javascript:void(0);" onclick="showDialog(<c:out
                                        value="${pcArticle.docLibID}"/>,
                                    <c:out value="${pcArticle.docID}"/> );">更多..</a>
                            </div>

                        </div>
                        <!-- END 文章附属信息 -->
                    </div>
                    <!-- END 文章 + 附属信息 div -->
                    <div style="clear:both"></div>
                    <!-- 挂件 -->
                    <c:if test="${pcArticle.type < 3 }">
                        <div style="margin-right: 15px;" class="div-border-side">
                            <div style="border-top: 1px solid #ddd;" class="meta_title_div">挂件</div>
                            <table class="table table-bordered">
                                <c:choose>
                                    <c:when test="${pcWidgetSet.size()>0 or pcPicset != null or pcVideoset != null}">
                                        <c:if test="${pcWidgetSet != null}">
                                            <tr>
                                                <th>附件</th>
                                                <td>
                                                    <ul>
                                                        <c:forEach items="${pcWidgetSet}" var="item">
                                                            <c:if test="${item.type==0}">
                                                                <li style="margin-left: 5px;">
                                                                    <div>
                                                                        <a target="_blank" href="../../e5workspace/Data.do?action=download&path=<c:out value="${item.path}"  />">${item.content==null?"&nbsp;":item.content}</a>
                                                                    </div>
                                                                </li>
                                                            </c:if>
                                                        </c:forEach>
                                                    </ul>
                                                </td>
                                            </tr>
                                        </c:if>
                                        <c:if test="${pcPicset != null}">
                                            <tr>
                                                <th>组图</th>
                                                <td>
                                                        <%--<ul>
                                                            <c:forEach items="${pcWidgetSet}" var="item">
                                                                <c:if test="${item.type==1}">
                                                                    <li style="float: left;margin-left: 5px;list-style: none;">
                                                                        <a target="_blank" href="../image.do?path=${item.path==null?"&nbsp;":item.path}">
                                                                            <div>
                                                                                <img class="top1" style="max-width: 200px;max-height: 130px;" alt="图片" src="../image.do?path=${item.path==null?"&nbsp;":item.path}"/>
                                                                            </div>
                                                                        </a>
                                                                    </li>
                                                                </c:if>
                                                            </c:forEach>
                                                        </ul>--%>

                                                    <div style="border: 1px solid #ddd; margin: 10px; text-align: left; padding: 10px;">
                                                        <div style="display: inline-block;">
                                                            <img style="max-width: 206px;max-height: 150px;vertical-align: middle;border: 0;" src="../../xy/image.do?path=<c:out value="${pcPicset.imgPath}"/>"/>
                                                        </div>
                                                        <div style="display: inline-block;margin-left: 15px;">
                                                            <ul class="widgetUl">

                                                                <li style="font-size: large;">
                                                                    <strong><c:out value="${pcPicset.topic}" escapeXml="false"/></strong>
                                                                </li>
                                                                <li>编辑：<c:out value="${pcPicset.author}"/></li>
                                                                <li>时间：<c:out value="${pcPicset.createDate}"/></li>

                                                            </ul>
                                                        </div>
                                                    </div>

                                                </td>
                                            </tr>
                                        </c:if>
                                        <c:if test="${pcVideoset != null}">
                                            <tr>
                                                <th>视频</th>
                                                <td>
                                                        <%--<ul>
                                                            <c:forEach items="${pcWidgetSet}" var="item">
                                                                <c:if test="${item.type==2&&item.path!=null}">
                                                                    <li style="float: left;margin-left: 5px;list-style: none;">

                                                                        <div>
                                                                            <img class="top1" style="max-width: 200px;max-height: 130px;" alt="图片" src="../image.do?path=${item.path==null?"&nbsp;":item.path}"/>
                                                                        </div>

                                                                    </li>
                                                                </c:if>
                                                            </c:forEach>
                                                        </ul>--%>

                                                    <div style="border: 1px solid #ddd; margin: 10px; text-align: left; padding: 10px;">
                                                        <div style="display: inline-block;">
                                                            <img style="max-width: 206px;max-height: 150px;vertical-align: middle;border: 0;" src="../../xy/image.do?path=<c:out value="${pcVideoset.imgPath}"/>"/>
                                                        </div>
                                                        <div style="display: inline-block;margin-left: 15px;">
                                                            <ul class="widgetUl">
                                                                <li style="font-size: large;">
                                                                    <strong><c:out value="${pcVideoset.topic}" escapeXml="false"/></strong>
                                                                </li>
                                                                <li>编辑：<c:out value="${pcVideoset.author}"/></li>
                                                                <li>时间：<c:out value="${pcVideoset.createDate}"/></li>

                                                            </ul>
                                                        </div>
                                                    </div>
                                                </td>
                                            </tr>
                                        </c:if>
                                    </c:when>
                                    <c:otherwise>
                                        <tr>
                                            <td><span style="color: lightgray">无挂件</span></td>
                                        </tr>
                                    </c:otherwise>
                                </c:choose>
                            </table>
                        </div>
                    </c:if>
                    <!-- END 挂件 -->

                    <!-- 流程记录 -->
                    <div style="margin-right: 15px;" class="div-border-side">
                        <div style="border-top: 1px solid #ddd;" class="meta_title_div">流程记录</div>
                        <table class="table table-bordered">
                            <c:choose>
                                <c:when test="${(pcRecord != null)}">
                                    <tr>
                                        <th style="width: 80px;">姓名</th>
                                        <th style="width: 180px;">操作</th>
                                        <th style="width: 180px;">操作时间</th>
                                        <th>备注</th>
                                    </tr>
                                    <c:forEach items="${pcRecord}" var="item">
                                        <tr>
                                            <td>${item.operator==null?"&nbsp;":item.operator}</td>
                                            <td>${item.operation==null?"&nbsp;":item.operation}</td>
                                            <td class="endTime">${item.endTime==null?"&nbsp;":item.endTime}</td>
                                            <td>${item.detail==null?"&nbsp;":item.detail}</td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td><span style="color: lightgray">无流程记录</span></td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                        </table>
                    </div>
                    <!-- END 流程记录 -->
                </div>
                <!-- END PC稿件 -->
            </div>
        </c:if>

        <!-- ******************************************** app稿件 ******************************************* -->
        <c:if test="${appArticle!=null}">
            <div class="tab-pane div-border-side" id="appShowDiv">
                <!-- 文章属性信息 div -->
                <div class="row div-border-bottom" style="padding: 10px 0 10px 0;white-space: nowrap;">
                    <div class="col-md-2">
                        <strong class="gray">稿件ID：</strong><strong class="gray"><c:out value="${appArticle.docID}"/></strong>
                    </div>
                    <div class="col-md-2">
                        <strong class="gray">稿件类型：</strong><strong class="gray"><c:out value="${appArticle.typeName}"/></strong>
                    </div>
                    <div class="col-md-2">
                        <strong class="gray">栏目：</strong><strong class="gray"><c:out value="${appArticle.column}"/>&nbsp;(<c:out value="${appArticle.columnID}"/>)</strong>
                    </div>
                    <div class="col-md-2">
                        <strong class="gray">稿件状态：</strong><strong class="gray"><c:out value="${appArticle.statusName}"/></strong>
                    </div>
                    <c:if test="${batManInfo!=null}">
                        <div class="col-md-2 w250">
                            <strong class="gray">通讯员：</strong><strong class="gray" id="bm_info"><c:out value="${bt_name}"></c:out>&nbsp;<c:out value="${bt_phone}"></c:out></strong>
                        </div>
                    </c:if>
                    <c:if test="${batManInfo!=null}">
                        <div class="col-md-2">
                            <strong class="gray">QQ：</strong><strong class="gray"><a href="http://wpa.qq.com/msgrd?V=1&uin=<c:out value="${bt_qq}"/>&exe=qq&Site=qq&menu=yes"><c:out value="${bt_qq}"></c:out></a></strong>
                        </div>
                    </c:if>
                    <c:if test="${appArticle.type ==0}">
                        <div class="col-md-2">
                            <a class="getTrace" target="_blank" href="">修改痕迹</a>
                        </div>
                        <div class="col-md-2">
                            <a class="seeNotes" target="_blank" href="">查看批注</a>
                        </div>
                    </c:if>
                </div>
                <!-- END 文章属性信息 div -->

                <!-- 文章 + 附属信息 div -->
                <div class="row div-border-side marginright">
                    <!-- 文章 div -->
                    <div class="col-md-8 center-block articleDiv">
                        <!-- 标题 -->
                        <div class="row  text-center"><h3>
                            <strong class="title"><c:out value="${appArticle.topic}" escapeXml="false"/></strong></h3>
                        </div>
                        <!-- END 标题 -->
                        <!-- 副标题 -->
                        <div class="row text-right">
                            <h4><strong><c:out value="${appArticle.subTitle}" escapeXml="false"/></strong></h4></div>
                        <!-- END 副标题 -->

                        <!-- 作者 -->
                        <div class="row text-center">
                            <strong class="explain">作者：</strong><strong class="explain"><c:out value="${appArticle.author}"/></strong>
                            <span class="creatTime"><strong class="explain">创建日期：</strong><strong class="explain"><c:out value="${appArticle.createDate}"/></strong></span>
                            <strong class="explain">来源：</strong><strong class="explain"><c:out value="${appArticle.source}"/></strong>
                            <strong class="explain">话题：</strong><strong class="explain"><c:out value="${appArticle.topics}"/></strong>
                        </div>
                        <!-- END 作者 -->

                        <div class="row text-justify">
                            <blockquote><p style="font-size: 80%;">
                                <strong>摘要：</strong><c:out value="${appArticle.summary}" escapeXml="false"/></p>
                            </blockquote>
                        </div>

                        <!-- 组图 -->
                        <c:if test="${appImageArray!=null && appImageArray.size()>0}">
                            <!--效果html开始-->
                            <div id="appPicDiv" class="row main">
                                <div class="left">
                                    <div class="mod18">
                                        <span id="prev_app" class="btn prev"></span>
                                        <span id="next_app" class="btn next"></span>
                                        <span id="prevTop_app" class="btn prev"></span>
                                        <span id="nextTop_app" class="btn next"></span>

                                        <div id="picBox_app" class="picBox">
                                            <ul class="cf">
                                                <c:forEach items="${appImageArray}" var="item" varStatus="status">
                                                    <li>
                                                        <a href="${item.src==null?"javascript:void(0);":item.src}" target="_blank"><img style="max-width: 526px;max-height: 377px;" src="${item.src}" alt=""></a>
                                                    </li>
                                                </c:forEach>
                                            </ul>
                                        </div>
                                        <div id="comments_app" class="text-justify" style="height: 72px;overflow: auto;">
                                            <c:forEach items="${appImageArray}" var="item">
                                                <p class="word" style="display:none;">${item.title}</p>
                                            </c:forEach>
                                        </div>
                                        <div id="listBox_app" class="listBox">
                                            <ul style="width: 1792px; left: -512px;" class="cf">
                                                <c:forEach items="${appImageArray}" var="item" varStatus="status">
                                                    <li><i class="arr2"></i>
                                                        <img style="max-width: 114px; max-height: 83px;" src="${item.src}" alt="">
                                                        <span class="num">${status.index + 1}/<c:out value="${appImageArray.size()}"/></span>
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
                        </c:if>
                        <!-- END 组图 -->
                        <!-- 视频预览 -->
                        <div class="row text-center">
                            <p>
                                <c:forEach items="${appVideoList}" var="item" varStatus="status">
                                <embed type="application/x-shockwave-flash" class="edui-faked-video"
                                       pluginspage="http://www.macromedia.com/go/getflashplayer" width="420"
                                       height="280" wmode="transparent" play="true" loop="false" menu="false"
                                       allowscriptaccess="never" allowfullscreen="true"
                                       src="${videoplugin}?src=${item}"
                                       title="Adobe Flash Player">
                                    </c:forEach>
                            </p>
                        </div> <!-- END 视频预览 -->

                        <!-- 正文 -->
                        <div class="row text-justify content" style="overflow: hidden;overflow-x: auto; width: 99%; margin: 0 auto;">
                            <c:out value="${appArticle.content}" escapeXml="false"/>

                        </div>
                        <!-- END 正文 -->

                        <div class="row">
                            <strong class="col-xs-6 keywords">关键词：<c:out value="${appArticle.keyword}"/></strong>
                            <strong class="col-xs-6 dutyeditor">责任编辑:<c:out value="${appArticle.liability}"/> </strong>
                        </div>
                        <!--<div class="row text-right">

                        </div>-->

                    </div>
                    <!-- 文章 div -->
                    <!-- 文章附属信息 -->
                    <div class="col-md-4 div-border-side">

                        <!-- 标题图 -->
                        <div class="row">
                            <div class="meta_title_div">标题图</div>
                            <div class="col-xs-12">
                                <c:choose>
                                    <c:when test="${appSmallTitlePic != null || appMidTitlePic != null || appBigTitlePic != null}">
                                        <c:if test="${appSmallTitlePic!=null}">
                                            <div>
                                                <div>
                                                    <div style="text-align: left;">
                                                        <span>小</span>
                                                    </div>
                                                    <div>
                                                        <a target="_blank" href="<c:out value="${appSmallTitlePic}"/>" class="thumbnail">
                                                            <img style="width:auto;" src="<c:out value="${appSmallTitlePic}"/>"/>
                                                        </a>
                                                    </div>
                                                </div>
                                            </div>
                                        </c:if>
                                        <c:if test="${appMidTitlePic!=null}">
                                            <tr>
                                                <td>
                                                    <div style="text-align: left;">
                                                        <span>中</span>
                                                    </div>
                                                    <div>
                                                        <a target="_blank" href="<c:out value="${appMidTitlePic}"/>" class="thumbnail">
                                                            <img style="width:auto; " src="<c:out value="${appMidTitlePic}"/>"/>
                                                        </a>
                                                    </div>
                                                </td>
                                            </tr>
                                        </c:if>
                                        <c:if test="${appBigTitlePic!=null}">
                                            <tr>
                                                <td>
                                                    <div style="text-align: left;">
                                                        <span>大</span>
                                                    </div>
                                                    <div>
                                                        <a target="_blank" href="<c:out value="${appBigTitlePic}"/>" class="thumbnail">
                                                            <img style="width:auto;" src="<c:out value="${appBigTitlePic}"/>"/>
                                                        </a>
                                                    </div>
                                                </td>
                                            </tr>
                                        </c:if>
                                    </c:when>
                                    <c:otherwise>
                                        <tr>
                                            <td><span style="color: lightgray">无标题图</span></td>
                                        </tr>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                        <!-- END 标题图 -->

                        <c:if test="${appArticle.type != 4 and appArticle.type != 8 and appArticle.type != 3 and appArticle.type != 6 }">
                            <div class="row">
                                <div class="meta_title_div">扩展字段</div>

                                <table id="appExtTable" class="table table-bordered">
                                    <c:choose>
                                        <c:when test="${appExSet.size()>0}">
                                            <c:forEach items="${appExSet}" var="item">
                                                <tr>
                                                    <th width="150px;">${item.ext_name==null?"&nbsp;":item.ext_name}</th>
                                                    <td>${item.ext_value==null?"&nbsp;":item.ext_value}</td>
                                                </tr>
                                            </c:forEach>
                                        </c:when>
                                        <c:otherwise>
                                            <tr>
                                                <td><span style="color: lightgray">无扩展字段</span></td>
                                            </tr>
                                        </c:otherwise>
                                    </c:choose>
                                </table>
                            </div>
                        </c:if>
                        <!-- 稿件链接 -->
                        <div class="row">
                            <div style="border-top: 1px solid #ddd;" class="meta_title_div">稿件发布地址</div>
                            <table class="table table-bordered">
                                <tr>
                                    <th>触屏</th>
                                    <td style="word-break: break-all;">
                                        <a href="<c:out value="${appArticle.urlPad}" escapeXml="false" />" target="_blank"><c:out value="${appArticle.urlPad}" escapeXml="false"/></a>
                                    </td>
                                </tr>
                            </table>
                        </div>
                        <!-- END 稿件链接  -->
                        <div class="row">
                            <div class="meta_title_div">话题</div>
                            <table class="table table-bordered">
                            <tr>
                                <td width="150px;">
                                    ${appArticle.topics==null||"".equals(appArticle.topics)?"<span style=\"color: lightgray\">当前稿件无话题</span>":appArticle.topics}
                                    <c:if test="${appArticle.topics != null && appArticle.topics != ''}">

                                    </c:if>
                                </td>
                            </table>
                        </div>
                        <div class="row">
                            <div class="meta_title_div">关联栏目</div>
                            <table class="table table-bordered">
                                <tr>
                                    <td width="150px;">
                                            ${appArticle.columnRel==null||"".equals(appArticle.columnRel)?"<span style=\"color: lightgray\">无关联栏目</span>":appArticle.columnRel}<c:if test="${appArticle.columnRel != null && appArticle.columnRel != ''}">&nbsp;(${appArticle.columnRelID})</c:if>
                                    </td>
                            </table>
                        </div>
                        <c:if test="${appArticle.type != 4 and appArticle.type != 8 and appArticle.type != 3 and appArticle.type != 6 }">
                            <div class="row">
                                <div class="meta_title_div">相关稿件</div>
                                <table class="table table-bordered">
                                    <c:choose>
                                        <c:when test="${appRelSet.size()>0}">
                                            <c:forEach items="${appRelSet}" var="item" varStatus="index">
                                                <tr>
                                                    <td>
                                                            ${index.index + 1}.
                                                        <a target="_blank" href='${item.contentURL==null||item.contentURL==""?"javascript:void(0);":item.contentURL}'>${item.topic==null?"":item.topic}</a>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </c:when>
                                        <c:otherwise>
                                            <tr>
                                                <td><span style="color: lightgray">无相关稿件</span></td>
                                            </tr>
                                        </c:otherwise>
                                    </c:choose>
                                </table>
                            </div>
                        </c:if>

                        <div class="row paddingrow" style="margin-bottom: 20px;">
                            <a href="javascript:void(0);" onclick="showDialog(<c:out value="${appArticle.docLibID}"/>,
                                <c:out value="${appArticle.docID}"/> );">更多..</a>
                        </div>
                    </div>
                    <!-- END 文章附属信息 -->
                </div>
                <!-- END 文章 + 附属信息 div -->
                <!-- 挂件 -->
                <c:if test="${appArticle.type < 3 }">
                    <div class="row div-border-side marginright">
                        <div class="meta_title_div">挂件</div>
                        <table class="table table-bordered">
                            <c:choose>
                                <c:when test="${appWidgetSet.size()>0 or appPicset != null or appVideoset != null}">
                                    <c:if test="${appWidgetSet.size()>0}">
                                        <tr>
                                            <th>附件</th>
                                            <td>
                                                <ul>
                                                    <c:forEach items="${appWidgetSet}" var="item">
                                                        <c:if test="${item.type==0}">
                                                            <li style="margin-left: 5px;">
                                                                <div>
                                                                    <a target="_blank" href="../../e5workspace/Data.do?action=download&path=<c:out value="${item.path}"   />">${item.content==null?"&nbsp;":item.content}</a>
                                                                </div>
                                                            </li>
                                                        </c:if>
                                                    </c:forEach>
                                                </ul>
                                            </td>
                                        </tr>
                                    </c:if>
                                    <c:if test="${appPicset != null}">
                                        <tr>
                                            <th>组图</th>
                                            <td>
                                                    <%--<ul>
                                                        <c:forEach items="${appWidgetSet}" var="item">
                                                            <c:if test="${item.type==1}">
                                                                <li style="float: left;margin-left: 5px;list-style: none;">
                                                                    <a target="_blank" href="../image.do?path=${item.path==null?"&nbsp;":item.path}">
                                                                        <div>
                                                                            <img class="top1" style="max-width: 200px;max-height: 130px;" alt="图片" src="../image.do?path=${item.path==null?"&nbsp;":item.path}"/>
                                                                        </div>
                                                                    </a>
                                                                </li>
                                                            </c:if>
                                                        </c:forEach>
                                                    </ul>--%>

                                                <div style="border: 1px solid #ddd; margin: 10px; text-align: left; padding: 10px;">
                                                    <div style="display: inline-block;">
                                                        <img style="max-width: 206px;max-height: 150px;vertical-align: middle;border: 0;" src="../../xy/image.do?path=<c:out value="${appPicset.imgPath}"/>"/>
                                                    </div>
                                                    <div style="display: inline-block;margin-left: 15px;">
                                                        <ul class="widgetUl">
                                                            <li style="font-size: large;">
                                                                <strong><c:out value="${appPicset.topic}" escapeXml="false"/></strong>
                                                            </li>
                                                            <li>编辑：<c:out value="${appPicset.author}"/></li>
                                                            <li>时间：<c:out value="${appPicset.createDate}"/></li>
                                                        </ul>
                                                    </div>
                                                </div>

                                            </td>
                                        </tr>
                                    </c:if>
                                    <c:if test="${appVideoset != null}">
                                        <tr>
                                            <th>视频</th>
                                            <td>
                                                    <%--<ul>
                                                        <c:forEach items="${appWidgetSet}" var="item">
                                                            <c:if test="${item.type==2&&item.path!=null}">
                                                                <li style="float: left;margin-left: 5px;list-style: none;">
                                                                    <div>
                                                                        <img class="top1" style="max-width: 200px;max-height: 130px;" alt="图片" src="../image.do?path=${item.path==null?"&nbsp;":item.path}"/>
                                                                    </div>
                                                                </li>
                                                            </c:if>
                                                        </c:forEach>
                                                    </ul>--%>
                                                <div style="border: 1px solid #ddd; margin: 10px; text-align: left; padding: 10px;">
                                                    <div style="display: inline-block;">
                                                        <img style="max-width: 206px;max-height: 150px;vertical-align: middle;border: 0;" src="../../xy/image.do?path=<c:out value="${appVideoset.imgPath}"/>"/>
                                                    </div>
                                                    <div style="display: inline-block;margin-left: 15px;">
                                                        <ul class="widgetUl">
                                                            <li style="font-size: large;">
                                                                <strong><c:out value="${appVideoset.topic}" escapeXml="false"/></strong>
                                                            </li>
                                                            <li>编辑：<c:out value="${appVideoset.author}"/></li>
                                                            <li>时间：<c:out value="${appVideoset.createDate}"/></li>
                                                        </ul>
                                                    </div>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:if>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td><span style="color: lightgray">无挂件</span></td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>

                        </table>
                    </div>
                </c:if>
                <!-- END 挂件 -->

                <!-- 流程记录 -->
                <div class="row div-border-side marginright">
                    <div class="meta_title_div">流程记录</div>
                    <table class="table table-bordered">
                        <c:choose>
                            <c:when test="${(appRecord != null)}">
                                <tr>
                                    <th style="width: 80px;">姓名</th>
                                    <th style="width: 180px;">操作</th>
                                    <th style="width: 180px;">操作时间</th>
                                    <th>备注</th>
                                </tr>
                                <c:forEach items="${appRecord}" var="item">
                                    <tr>
                                        <td>${item.operator==null?"&nbsp;":item.operator}</td>
                                        <td>${item.operation==null?"&nbsp;":item.operation}</td>
                                        <td class="endTime">${item.endTime==null?"&nbsp;":item.endTime}</td>
                                        <td>${item.detail==null?"&nbsp;":item.detail}</td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td><span style="color: lightgray">无流程记录</span></td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                    </table>
                </div>
                <!-- END 流程记录 -->
            </div>
        </c:if>
    </div>
</div>
</body>
</html>
