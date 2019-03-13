<%@include file="../../e5include/IncludeTag.jsp" %>
    <%@ page language="java" pageEncoding="UTF-8" %>
        <%
    String path = request.getContextPath();
%>
    <html lang="zh-CN">
    <head>
    <title>源稿微信详情</title>
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

    <%--<script type="text/javascript">--%>
    <%--$(function(){--%>
    <%--$("#orignalA").click();--%>
    <%--});--%>
    <%----%>

    <%--function pubShow(signType){--%>
    <%--var theURL = "../../xy/article/SignInfo.do?DocIDs=" + $("#originalID").val()--%>
    <%--+ "&DocLibID=" + $("#originalLibID").val();--%>
    <%--+ "&SignType=" + signType;--%>
    <%--$.ajax({url:theURL, async:false, success:function(data){--%>
    <%--alert(data.signInfo);//微信01,微信02,微信03--%>
    <%--}});--%>
    <%--}--%>


    <%--function flowShow(){--%>
    <%--var theURL = "../../xy/article/CensorshipLog.do?DocIDs=" + $("#originalID").val()--%>
    <%--+ "&DocLibID=" + $("#originalLibID").val()  + "&Type=1";--%>
    <%--$.ajax({url:theURL, async:false, success:function(data){--%>
    <%--drawTable(data.logList);--%>
    <%--}});--%>
    <%--}--%>
    <%----%>
    <%--function drawTable(data){--%>
    <%--if(data.length>0){--%>
    <%--var logTableHtml = "<div>流程记录:</div><br><table class='table table-striped' border='1' style='width:510px'><thead><tr><td>审核人</td>"--%>
    <%--+ "<td>审核操作</td><td>审核时间</td><td>审核意见</td></tr></thead><tbody>";--%>
    <%--for(var i=0;i<data.length;i++){--%>
    <%--logTableHtml += "<tr><td>" + data[i].operator + "</td>"--%>
    <%--+ "<td>" + data[i].fromPosition + data[i].operation + "</td>"--%>
    <%--+ "<td sytle='width:200px'>" + formatCSTDate(data[i].startTime,"yyyy-MM-dd hh:mm:ss") + "</td>";--%>
    <%--if(data[i].detail==null){--%>
    <%--logTableHtml += "<td></td></tr>";--%>
    <%--}else{--%>
    <%--logTableHtml += "<td>" + data[i].detail + "</td></tr>";--%>
    <%--}--%>
    <%--}--%>
    <%--logTableHtml += "</tbody></table>";--%>
    <%--$("#logTable").append(logTableHtml);--%>
    <%--}--%>
    <%--}--%>
    <%----%>
    <%--function formatCSTDate(strDate,format){--%>
    <%--return formatDate(new Date(strDate),format);--%>
    <%--}--%>
    <%----%>
    <%--function formatDate(date,format){--%>
    <%--var paddNum = function(num){--%>
    <%--num += "";--%>
    <%--return num.replace(/^(\d)$/,"0$1");--%>
    <%--};--%>
    <%--//指定格式字符--%>
    <%--var cfg = {--%>
    <%--yyyy : date.getFullYear() //年 : 4位--%>
    <%--,yy : date.getFullYear().toString().substring(2)//年 : 2位--%>
    <%--,M  : date.getMonth() + 1  //月 : 如果1位的时候不补0--%>
    <%--,MM : paddNum(date.getMonth() + 1) //月 : 如果1位的时候补0--%>
    <%--,d  : date.getDate()   //日 : 如果1位的时候不补0--%>
    <%--,dd : paddNum(date.getDate())//日 : 如果1位的时候补0--%>
    <%--,hh : paddNum(date.getHours())  //时--%>
    <%--,mm : paddNum(date.getMinutes()) //分--%>
    <%--,ss : paddNum(date.getSeconds()) //秒--%>
    <%--};--%>
    <%--format || (format = "yyyy-MM-dd hh:mm:ss");--%>
    <%--return format.replace(/([a-z])(\1)*/ig,function(m){return cfg[m];});--%>
    <%--} --%>
    <%--</script>--%>

    <%--<style type="text/css">--%>
    <%--body {--%>
    <%--font-family: "微软雅黑";--%>
    <%--overflow-x: hidden;--%>
    <%--}--%>

    <%--.div-border-bottom {--%>
    <%--margin-right: 0;--%>
    <%--margin-left: 0;--%>
    <%--background-color: #fff;--%>
    <%--border-bottom: 1px solid #ddd;--%>
    <%---webkit-box-shadow: none;--%>
    <%--box-shadow: none;--%>
    <%--}--%>

    <%--.div-border-side {--%>
    <%--/* margin-right: 0;*/--%>
    <%--margin-left: 0;--%>
    <%--background-color: #fff;--%>
    <%--/*border-left: 1px solid #ddd;*/--%>
    <%--/*border-right: 1px solid #ddd;*/--%>
    <%---webkit-box-shadow: none;--%>
    <%--box-shadow: none;--%>
    <%--}--%>

    <%--.meta_title_div {--%>
    <%--background-color: #F1F1F1;--%>
    <%--margin: 0 0 0 0;--%>
    <%--padding: 10px 15px 10px 15px;--%>
    <%--font-weight: bold;--%>
    <%--border-left: 1px solid #ddd;--%>
    <%--border-right: 1px solid #ddd;--%>
    <%--}--%>

    <%--.gray {--%>
    <%--font-weight: 100;--%>
    <%--color: #4e4c4c;--%>
    <%--font-size: 12px;--%>
    <%--white-space: normal;--%>
    <%--}--%>

    <%--h3 .title {--%>

    <%--font-size: 18px;--%>
    <%--color: #6b6b6b;--%>
    <%--}--%>

    <%--.explain {--%>
    <%--font-weight: 100;--%>
    <%--color: #b7b7b7;--%>
    <%--font-size: 12px;--%>
    <%--}--%>

    <%--.creatTime {--%>
    <%--margin: 0 20px;--%>
    <%--}--%>

    <%--.keywords {--%>
    <%--font-weight: 100;--%>
    <%--}--%>

    <%--.dutyeditor {--%>
    <%--font-weight: 100;--%>
    <%--}--%>

    <%--.piclist {--%>
    <%--width: 100%;--%>
    <%--background-color: #fff;--%>
    <%--border: 1px solid #ddd;--%>
    <%--}--%>

    <%--.paddingrow {--%>
    <%--margin-left: 0;--%>
    <%--margin-right: 0;--%>
    <%--}--%>

    <%--.marginright {--%>
    <%--margin-right: 0;--%>
    <%--}--%>

    <%--.widgetUl {--%>
    <%--list-style: none;--%>
    <%--margin-bottom: 5px;--%>
    <%--line-height: 20px;--%>
    <%--box-sizing: inherit;--%>
    <%--}--%>

    <%--.summary_style {--%>
    <%--border-left: 4px solid blue;--%>
    <%--padding-left: 4px;--%>
    <%--margin-left: 4px;--%>
    <%--}--%>

    <%--.content {--%>
    <%--font-size: 16px;--%>
    <%--}--%>
    <%--.getTrace{--%>
    <%--font-size:12px;--%>
    <%--}--%>
    <%--.seeNotes{--%>
    <%--font-size:12px;--%>
    <%--}--%>
    <%--/* .col-md-2{--%>
    <%--width:11%;--%>
    <%--} */--%>
    <%--#pcShowDiv .w250,#appShowDiv .w250{--%>
    <%--width:250px;--%>
    <%--}--%>
    <%--</style>--%>

    <style>
    *{
    padding: 0;
    margin: 0;
    }
    body{
    background: #666666;
    padding: 15px 10px;
    }
    #container_box:after{
    display: block;
    content: "";
    clear: both;
    }
    #proof_tip{
    float: left;
    width: 30%;
    height: 1px;
    }
    #proof_tip span{
    font-size: 13px;
    padding: 1px 5px;
    display: none;
    }
    .wx_tip{
    color: #323232;
    background: #F1F2F1;
    }
    .wx_sensitiveWord{
    color: #FEDD55;
    background: #fff;
    border: 1px solid #FEDD55;
    }
    .wx_illegalWord{
    color: #FF2323;
    background: #F1F2F1;
    border:1px solid #FF2323;
    }
    .wx_spellError{
    color: #1818FF;
    background: #F1F2F1;
    border:1px solid #1818FF;
    }
    #wx_container{
    float: left;
    }
    #wx_content{
    /*iphone6背景图的大小要和这个width保持一致*/
    height: 774px;
    width: 383px;
    background: url(./image/bg.png);
    padding: 82px 23px;
    position: relative;
    box-sizing: border-box
    }
    #wxBox{
	    background: #fff;
	    border-left: 1px solid #000;
	    border-right: 1px solid #000;
	    border-bottom: 1px solid #000;
	    height: 100%;
	    overflow: hidden;
	    overflow-y: auto;
    }
    #wxBox_title{
    background:#000000 ;
    color: #ffffff;
    font-size: 14px;
    text-align: center;
    height:40px ;
    line-height:40px ;
    }
    #wxBox_content{
	    overflow-y: auto;
	    overflow-x: hidden;
	    padding: 10px;
    }
    #wxBox_content img {
    	width: 100%;
    }
    #wxBox_content ol {
    	margin-left: 25px;
    }
    #QRcode{
    width: 171px;
    height: 225px;
    position: absolute;
    background: #F1F2F1;
    bottom: 105px;
    left: -165px;
    padding: 10px;
    box-sizing: border-box;
    font-size: 13px;
    }
    .QRcode_img{
    width: 110px;
    height: 110px;
    margin-left: 18px;
    margin: 10px 18px;
    }
    .QRcode_select{
    width: 110px;
    height: 40px;
    line-height:40px;
    border: 1px solid #AEAEAE;
    margin-left: 18px;
    position:relative;
    }
    #QRcode_option{
    display:none;
    border:1px solid #AEAEAE;
    background:#AEAEAE;

    }
    #QRcode_option p{
    margin:0;
    text-indent: 1rem;
    }
    #QRcode_default{
    padding: 0 5px;
    background:#fff url("<%=path %>/xy/article/image/wxSelectDown.png") no-repeat;
    background-position:right center;
    }
    .QRcode_text span{
    display: block;
    width: 100%;
    text-align: center;
    }
    .columChannel{
    text-align: center;
    position:absolute;
    font-weight: 600;
    color: #000;
    line-height: 30px;
    left: -165px;
    top: 70px;
    width: 130px;
    background: #fff;
    height: 30px;
    border-radius: 5px;
    }
    .option_btn{
    position: absolute;
    right: -46px;
    text-align: center;
    }
    .option_btn p{
    width: 60px;
    font-size: 12px;
    }
    .option_btn1{
    bottom: 450px;
    }
    .option_btn1 p{
	    border-radius: 5px;
	    background: #fff;
	    width: 50px;
	    height: 40px;
	    line-height: 40px;
	    text-align: center;
	    cursor: pointer;
    }
    .option_btn2{
    bottom: 70px;
    }
    .option_btn2 p{
	    border-radius: 1px;
	    background: #F1F2F1;
	    width: 50px;
	    height: 50px;
	    margin-bottom: 10px;
	    cursor: pointer;

    }
    .option_btn2 p span{
    display: inline-block;
    }
    .option_btn2 p span.span1{
    margin-top: 10px;
    }
    #option_close,#annotationInfo{
    width: 50px;
    height: 40px;
    text-align: center;
    background: #ffffff;
    border-radius: 5px;
    }
    #option_close{
    line-height: 40px;
    }
    #option_close a{
    display:block;
    color: #000;
    }
    #annotationInfo span.span1{
    margin-top: 5px;
    }
    #logTable,#drawHistoryTable{
        position: absolute;
	    width: 590px;
	    height: 743px;
	    top: 5px;
	    right: -660px;
	    opacity: 1;
	    overflow-y: auto;
    }
    #columChaTable{
    position: absolute;
    display: block;
    background:#ccc;
    top: 110px;
    width: 300px;
    left: -335px;
    }
    #SignInfoTable{
    bottom: 259px;
    right: -353px;
    width: 289px;
    background:#FFFFFF;
    position:absolute;
    }
    .SigDiv{
    padding:10px;

    }
    .sigtitle{
    /*margin-bottom:10px;*/
    }
    .sigspan{
    margin-right:6px;
    margin-top: 10px;
    border: 1px solid #ccc;
    padding: 5px 10px;
    display: inline-block;
    background:#F2F2F2;
    }
    .nav-pills>li{
    width:65px;
    }
    .nav-pills>li+li{
    border-bottom: 1px solid #ddd;
    }
    .nav-pills > li.active > a, .nav-pills > li.active > a:hover, .nav-pills > li.active > a:focus{
    background:#fff;
    border-bottom: none;
    }
    
    .selected-comment {
    	 background-color: transparent !important;
    	 border: none !important;
    }
    
    </style>
    </head>


    <%--<input type="hidden" id="originalID" value="${orignal.docID}">--%>
    <%--<input type="hidden" id="originalLibID" value="${orignal.docLibID}">--%>
    <div class="container-fluid">
    <div id="container_box">
    <div id="proof_tip">
    <!--<span class="wx_tip">校对敏感词</span>-->
    <span class="wx_sensitiveWord">敏感词</span>
    <span class="wx_illegalWord">非法词</span>
    <span class="wx_spellError">拼写错误</span>
    </div>
    <div id="wx_container">
    <div id="wx_content">
    <div id="wxBox">
    <div id="wxBox_title">
    <c:out value="${orignal.catName}"/>
    </div>
    <div id="wxBox_content">
    <c:out value="${orignal.topic}" escapeXml="false"/>
    <c:out value="${orignal.content}" escapeXml="false"/>
    </div>
    </div>
    <c:if test="${orignal.status == 1}">
        <div class="columChannel">
        <p id="columChannel" onclick="pubShow(0)">预签发栏目与渠道</p>
        </div>
    </c:if>
    <div class="option_btn option_btn1">
    <p style="margin-bottom: 10px" id="modifyBtn">修改</p>
    <%--<p>送审</p>--%>
    </div>
    <div class="option_btn option_btn2">
    <c:if test="${orignal.status == 3}"><p id="IssuingBtn" onclick="pubShow(1)"><span class="span1">签发</span></br><span>情况</span></p></c:if>
    <p id="drawHistoryInfo"><span class="span1">历史</span></br><span>版本</span></p>
    <p id="flowInfo"><span class="span1">流程</span></br><span>记录</span></p>
    <!--<p id="annotationInfo"><span class="span1">查看</span></br><span>批注</span></p>-->
    <p id="option_close"><a href="javascript:window.opener=null;window.open('','_self');window.close();">关闭</a></p>
    </div>
    <!--预签栏目-->
    <div id="columChaTable" style="display:none"></div>
    <%--签发情况--%>
    <div id="SignInfoTable" style="display:none"></div>
    <!--流程记录-->
    <div id="logTable" style="display:none"></div>
    <div id="drawHistoryTable" style="display:none"></div>
    </div>
    </div>

    </div>
    </div>

    <input type="hidden" value="${orignal.docLibID}" id="docLibID">
    <%--<div class="container-fluid">--%>
    <%--<ul class="nav nav-tabs">--%>
    <%--<c:if test="${orignal!=null}">--%>
    <%--<li><a id="orignalA" href="#orignalShowDiv" data-toggle="tab">原稿</a></li>--%>
    <%--</c:if>--%>
    <%--</ul>--%>

    <%--<div class="checkArt">--%>
    <%--<div id="logTable"></div>--%>
    <%--</div>--%>
    <%--<div style="border-bottom: 0;" class="tab-content div-border-bottom">--%>
    <%--<c:if test="${orignal!=null}">--%>
    <%--<div class="tab-pane div-border-side" id="orignalShowDiv">--%>
    <%--<!-- 文章属性信息 div -->--%>
    <%--<div class="row div-border-bottom" style="padding: 10px 0 10px 0;white-space: nowrap;">--%>
    <%--<div class="col-md-3">--%>
    <%--<strong class="gray">稿件ID：</strong><strong class="gray"><c:out value="${orignal.docID}"/></strong>--%>
    <%--<input type="hidden" id="originalID" value="${orignal.docID}">--%>
    <%--<input type="hidden" id="originalLibID" value="${orignal.docLibID}">--%>
    <%--</div>--%>
    <%--<c:if test="${orignal.status == 1}"><!-- 待审核 -->--%>
    <%--<div class="col-md-3">--%>
    <%--<a class="getTrace" id="reserveInfo" target="_blank" onclick="pubShow(0)">预签发渠道与栏目</a>--%>
    <%--</div>--%>
    <%--<div class="col-md-3">--%>
    <%--<a class="getTrace" id="historyInfo" target="_blank" onclick="">历史版本</a>--%>
    <%--</div>--%>
    <%--</c:if>--%>
    <%--<c:if test="${orignal.status == 3}"><!-- 已签发 -->--%>
    <%--<div class="col-md-3">--%>
    <%--<a class="getTrace" id="pubInfo" target="_blank" onclick="pubShow(1)">签发情况</a>--%>
    <%--</div>--%>
    <%--</c:if>--%>
    <%--<div class="col-md-3">--%>
    <%--<a class="getTrace" id="flowInfo" target="_blank" onclick="flowShow()">流程记录</a>--%>
    <%--</div>--%>
    <%--<div class="col-md-3">--%>
    <%--<a class="seeNotes" id="annotationInfo" target="_blank" onclick="">查看批注</a>--%>
    <%--</div>--%>
    <%--</div>   --%>
    <%--<!-- END 文章属性信息 div -->--%>

    <%--<!-- 文章 + 附属信息 div -->--%>
    <%--<div class="row div-border-side">--%>
    <%--<!-- 文章 div -->--%>
    <%--<div class="col-md-8 center-block articleDiv">--%>
    <%--<!-- 栏目 -->--%>
    <%--<strong class="explain"></strong><strong class="explain"><c:out value="${orignal.catName}"/></strong>--%>
    <%--<!-- END 栏目 -->--%>
    <%----%>
    <%--<!-- 标题 -->--%>
    <%--<div class="row  text-center"><h3>--%>
    <%--<strong class='title'><c:out value="${orignal.topic}" escapeXml="false"/></strong></h3>--%>
    <%--</div>--%>
    <%--<!-- END 标题 -->--%>

    <%--<!-- 栏目 -->--%>
    <%--<div class="row text-center">--%>
    <%--<span class="creatTime"><strong class="explain">创建日期：</strong><strong class="explain"><c:out value="${orignal.createDate}"/></strong></span>--%>
    <%--<strong class="explain"></strong><strong class="explain"><c:out value="${orignal.catName}"/></strong>--%>
    <%--</div>--%>
    <%--<!-- END 栏目 -->--%>

    <%--<div class="row text-justify">--%>
    <%--<blockquote><p style="font-size: 80%;">--%>
    <%--<strong>摘要：</strong><c:out value="${orignal.summary}" escapeXml="false"/></p>--%>
    <%--</blockquote>--%>
    <%--</div>--%>

    <%--<!-- 组图轮播 -->--%>
    <%--<c:if test="${orignalImageArray!=null && orignalImageArray.size()>0}">--%>
    <%--<!--效果html开始-->--%>
    <%--<div id="orignalPicDiv" class="row main">--%>
    <%--<div class="left">--%>
    <%--<div class="mod18">--%>
    <%--<span id="prev_orignal" class="btn prev"></span>--%>
    <%--<span id="next_orignal" class="btn next"></span>--%>
    <%--<span id="prevTop_orignal" class="btn prev"></span>--%>
    <%--<span id="nextTop_orignal" class="btn next"></span>--%>

    <%--<div id="picBox_orignal" class="picBox">--%>
    <%--<ul class="cf">--%>
    <%--<c:forEach items="${orignalImageArray}" var="item">--%>
    <%--<li>--%>
    <%--<a href="${item.src==null?"javascript:void(0);":item.src}" target="_blank"><img style="max-width: 526px;max-height: 377px;" src="${item.src}" alt=""></a>--%>

    <%--</li>--%>
    <%--</c:forEach>--%>
    <%--</ul>--%>
    <%--</div>--%>
    <%--<div id="comments_orignal" class="text-justify" style="height: 72px;overflow: auto;">--%>

    <%--<c:forEach items="${orignalImageArray}" var="item">--%>

    <%--<p class="word" style="display:none;">${item.title}</p>--%>
    <%--</c:forEach>--%>
    <%--</div>--%>
    <%--<div id="listBox_orignal" class="listBox">--%>
    <%--<ul style="width: 1792px; left: -512px;" class="cf">--%>
    <%--<c:forEach items="${orignalImageArray}" var="item" varStatus="status">--%>
    <%--<li><i class="arr2"></i>--%>
    <%--<img style="max-width: 114px; max-height: 83px;" src="${item.src}" alt="">--%>
    <%--<span class="num">${status.index + 1}/<c:out value="${orignalImageArray.size()}"/></span>--%>
    <%--</li>--%>
    <%--</c:forEach>--%>
    <%--</ul>--%>
    <%--</div>--%>
    <%--<div class="clear"></div>--%>
    <%--</div>--%>
    <%--<div class="clear"></div>--%>
    <%--</div>--%>
    <%--<div class="clear"></div>--%>
    <%--</div>--%>
    <%--<!--效果html结束-->--%>

    <%--</c:if><!-- END 组图轮播 -->--%>

    <%--<!-- 视频预览 -->--%>
    <%--<div class="row text-center">--%>
    <%--<p>--%>
    <%--<c:forEach items="${orignalVideoList}" var="item" varStatus="status">--%>
    <%--<embed type="application/x-shockwave-flash" class="edui-faked-video"--%>
    <%--pluginspage="http://www.macromedia.com/go/getflashplayer" width="420"--%>
    <%--height="280" wmode="transparent" play="true" loop="false" menu="false"--%>
    <%--allowscriptaccess="never" allowfullscreen="true"--%>
    <%--src="${videoplugin}?src=${item}"--%>
    <%--title="Adobe Flash Player">--%>
    <%--</c:forEach>--%>
    <%--</p>--%>
    <%--</div><!-- END 视频预览 -->--%>


    <%--<!-- 正文 -->--%>
    <%--<div class="row text-justify content" style="overflow: hidden;overflow-x: auto;width:99%;margin:0 auto;">--%>
    <%--<c:out value="${orignal.content}" escapeXml="false"/>--%>
    <%--</div>--%>
    <%--<!-- END 正文 -->--%>
    <%--</div>--%>
    <%--<!-- 文章 div -->--%>
    <%--</div>--%>
    <%--<!-- END 文章 + 附属信息 div -->--%>
    <%--</div>--%>
    <%--<!-- END 原稿 -->--%>
    <%--</c:if>--%>
    <%--</div>--%>
    <%--</div>--%>
    <script>
    //签发情况
    function pubShow(signType){
	    var theURL = "../../xy/article/SignInfo.do?DocIDs=" + ${orignal.docID}
	    + "&DocLibID=" + $("#docLibID").val()
	    + "&SignType=" + signType;
	    $.ajax({url:theURL, async:false, success:function(data){
	    //微信01,微信02,微信03
	    if(data.signInfo){
	        if(signType==1){
	            if($("#SignInfoTable").css("display")=="none"){
	            $("#SignInfoTable").css({display:"block"})
	            }else{
	            $("#SignInfoTable").css({display:"none"})
	            }
	            drawSignInfo(data.signInfo);
	        }else if(signType==0){
	            if($("#columChaTable").css("display")=="none"){
	            $("#columChaTable").css({display:"block"})
	            }else{
	            $("#columChaTable").css({display:"none"})
	            }
	            drawCoumChaDiv(data.signInfo);
	            
	            <c:if test="${orignal.status == 1}">
	            	if(data.signInfo == '没有预签或签发信息！'){
	            		$(".columChannel").hide();
			       		$("#columChaTable").hide();
	            	}
			    </c:if>
	        }
	
	
	    }

    }});
    }
    $("#SignInfoTable").click(function(){
    $("#SignInfoTable").css({display:'none'})
    });
    <c:if test="${orignal.status == 1}">
       pubShow(0)
    </c:if>
    //栏目与渠道
    function drawCoumChaDiv(data){
        var html='<div class="SigDiv"><div class="sigtitle">微信公众号</div>';
        console.log(data)
        if(data == "没有预签或签发信息！"){
        	for(var i=0;i<data.length;i++){
	        	html+= ' <span class="sigspan">'+data[i]+'</span>';
	        }
        }else {
          		var dataArr = [];
	        var newData = data.split("：");
	        newData[0] = newData[0]+"：";
	        dataArr.push(newData[0]);
	
	        if(newData[1].indexOf(",") != -1)  {
	        	var newDataArr = newData[1].split(",");
	        	for(var i=0; i<newDataArr.length;i++) {
	        	dataArr.push(newDataArr[i]);
	        }
	        }else {
	        	dataArr.push(newData[1])
	        }
	        
	        for(var i=0;i<dataArr.length;i++){
	        	html+= ' <span class="sigspan">'+dataArr[i]+'</span>';
	        }
        }

        html+='</div>';
        $("#columChaTable").html(html);
    }
    //渲染签发情况
    function drawSignInfo(data){
        var html='<div class="SigDiv"><div class="sigtitle">微信公众号</div>';
        var dataArr = [];
        var newData = data.split("：");
        newData[0] = newData[0]+"：";
        dataArr.push(newData[0]);

        if(newData[1].indexOf(",") != -1)  {
        	var newDataArr = newData[1].split(",");
        	for(var i=0; i<newDataArr.length;i++) {
        	dataArr.push(newDataArr[i]);
        }
        }else {
        	dataArr.push(newData[1])
        }
        
        for(var i=0;i<dataArr.length;i++){
        html+= ' <span class="sigspan">'+dataArr[i]+'</span>';
        }
        html+='</div>';
        $("#SignInfoTable").html(html);
    }
    <%--修改--%>
    $("#modifyBtn").click(function(){
    var currentUrl=window.location.href;
    currentUrl = currentUrl.replace('View.do', 'Article.do');
    window.open(currentUrl,'_blank')
    });

    <%--// 获取url参数--%>
    var currenturl=window.location.href;
    var getParam = function(key){
    var lot = location.search;
    var reg = new RegExp(".*" + key + "\\s*=([^=&#]*)(?=&|#|).*","g");
    return decodeURIComponent(lot.replace(reg, "$1"));
    };
    <%--历史版本和流程记录点击事件--%>
    $("#drawHistoryInfo").click(function(){
    var checkID = getParam("DocIDs");
    var checkLibID = getParam("DocLibID");
    var historyPage='./history.html?checkID='+checkID+'&checkLibID='+checkLibID;
    window.open(historyPage,'_blank')
    });
    $("#drawHistoryTable").click(function(){
    $("#drawHistoryTable").css({display:'none'})
    });
    <%--流程记录--%>
    $("#flowInfo").click(function(){
    if($("#logTable").css("display")=='block'){
    $("#logTable").css({display:'none'})
    }else{
    $("#logTable").css({display:'block'})
    }

    });
    $("#logTable").click(function(){
    $("#logTable").css({display:'none'})
    });
    //流程记录
    flowShow();
    function flowShow(){
    var theURL = "../../xy/article/CensorshipLog.do?DocIDs=" + ${orignal.docID}
    	+ "&DocLibID="+$("#docLibID").val() + "&Type=1";
    $.ajax({url:theURL, async:false, success:function(data){
    drawTable(data.logList);
    }});
    }

    //渲染流程记录
    function drawTable(data){
    if(data.length>0){
    var logTableHtml = "<table class='table table-striped table-bordered' style='background:#fff'><thead><tr><td>操作人</td>"
    + "<td>操作</td><td>操作时间</td><td>备注(校对意见和审核意见在此显示)</td></tr></thead><tbody>";
    for(var i=0;i<data.length;i++){
    logTableHtml += "<tr><td>" + data[i].operator + "</td>"
    + "<td>" + data[i].fromPosition + data[i].operation + "</td>"
    + "<td>" + formatCSTDate(data[i].startTime,"yyyy-MM-dd hh:mm:ss") + "</td>";
    if(data[i].detail==null){
    logTableHtml += "<td></td></tr>";
    }else{
    logTableHtml += "<td>" + data[i].detail + "</td></tr>";
    }
    }
    logTableHtml += "</tbody></table>";
    $("#logTable").append(logTableHtml);
    }
    }
    <%--//历史版本--%>
    <%--historyShow();--%>
    <%--function historyShow(){--%>
    <%--var page = 1;--%>
    <%--var theURL = "../../xy/article/HistoryInfo.do?DocIDs=" + ${orignal.docID}--%>
    <%--+ "&Page=" + page;--%>
    <%--$.ajax({url:theURL, async:false, success:function(data){--%>
    <%--drawHistoryTable(data.list);--%>
    <%--alert()--%>
    <%--debugger--%>
    <%--}});--%>
    <%--}--%>
    <%--//渲染历史版本--%>
    <%--function drawHistoryTable(data){--%>
    <%--if(data.length>0){--%>
    <%--var logTableHtml = "<table class='table ' border='1' style='width:510px;background:#fff'><thead><tr><td>审核人</td>"--%>
    <%--+ "<td>审核操作</td><td>审核时间</td></tr></thead><tbody>";--%>
    <%--for(var i=0;i<data.length;i++){--%>
    <%--logTableHtml += "<tr><td>" + data[i].operator + "</td>"--%>
    <%--+ "<td>" + data[i].operation + "</td>"--%>
    <%--+ "<td sytle='width:200px'>" + data[i].created+ "</td>";--%>
    <%--}--%>
    <%--logTableHtml += "</tbody></table>";--%>
    <%--$("#drawHistoryTable").append(logTableHtml);--%>
    <%--}--%>
    <%--}--%>

    function formatCSTDate(strDate,format){
    return formatDate(new Date(strDate),format);
    }

    function formatDate(date,format){
    var paddNum = function(num){
    num += "";
    return num.replace(/^(\d)$/,"0$1");
    };
    //指定格式字符
    var cfg = {
    yyyy : date.getFullYear() //年 : 4位
    ,yy : date.getFullYear().toString().substring(2)//年 : 2位
    ,M : date.getMonth() + 1 //月 : 如果1位的时候不补0
    ,MM : paddNum(date.getMonth() + 1) //月 : 如果1位的时候补0
    ,d : date.getDate() //日 : 如果1位的时候不补0
    ,dd : paddNum(date.getDate())//日 : 如果1位的时候补0
    ,hh : paddNum(date.getHours()) //时
    ,mm : paddNum(date.getMinutes()) //分
    ,ss : paddNum(date.getSeconds()) //秒
    };
    format || (format = "yyyy-MM-dd hh:mm:ss");
    return format.replace(/([a-z])(\1)*/ig,function(m){return cfg[m];});
    }


    <%--var qrcode = new QRCode(document.getElementById("qrcode"), { width : 110, height : 110 }); var token='params'; var QRCodeUrl='http:\\www.baidu.com'+'/Share/ScanQRCode?token='+token; qrcode.makeCode(QRCodeUrl);--%>
    </script>

    </body>
    </html>
