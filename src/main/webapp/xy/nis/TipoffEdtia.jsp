<%@include file="../../e5include/IncludeTag.jsp" %>
<%@taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@page pageEncoding="UTF-8" %>
<html>
<head>
    <title><%=com.founder.e5.workspace.ProcHelper.getProcName(request)%>
    </title>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
    <meta content="IE=edge" http-equiv="X-UA-Compatible"/>
    <link type="text/css" rel="stylesheet" href="../../e5style/reset.css"/>
    <link type="text/css" rel="stylesheet" href="../../e5script/jquery/jQuery-Validation-Engine/css/validationEngine.jquery.css"/>
    <link type="text/css" rel="stylesheet" href="../../e5style/e5form-custom.css"/>
    <link type="text/css" rel="stylesheet" href="../../xy/css/form-custom.css"/>
    <link type="text/css" rel="stylesheet" href="../../e5script/jquery/dialog.style.css"/>
    <link type="text/css" rel="stylesheet" href="../script/bootstrap-3.3.4/css/bootstrap.min.css">
    <link rel="stylesheet" href="./css/swiper.min.css">
    <script type="text/javascript" src="../script/jquery/jquery.min.js"></script>
    <script type="text/javascript" src="../script/bootstrap-3.3.4/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="../script/jquery-validation-engine/js/languages/jquery.validationEngine-zh_CN.js"></script>
    <script type="text/javascript" src="../script/jquery-validation-engine/js/jquery.validationEngine.js"></script>
    <script type="text/javascript" src="../../e5workspace/script/form-custom.js"></script>
    <script type="text/javascript" src="../../e5script/jquery/jquery.dialog.js"></script>
    <script type="text/javascript" src="../script/picupload/upload_api.js"></script>
    <script src="./js/swiper.min.js"></script>
    <script src="./js/tipoffEdtia.js"></script>
    <%--<script type="text/javascript" src="./js/tipoffs.js"></script>--%>
    <%--<script type="text/javascript" src="../nis/js/discloseDetail.js" async="async"></script>--%>

    <style>
    	li{
    		list-style: none;
    	}
    	#form{
    		width:99%;
    	}
        .out-border{
            margin-bottom: 14px;
        }
        #btnFormSave{
        	margin-right: 10px;
        	margin-left: 117px;
        }
        .btngroup{
        	margin-bottom: 10px;
        }
        #LABEL_a_hideVideo{
        	margin-left: 116px;
        }
        .custform-label{
        	text-align: right;
        }
        #ulvideo{
            width: 300px;
            margin-left: 80px;
        }
        .header{
            margin-left: 20px;
            margin-top: 10px;
            margin-bottom: 10px;
        }
        /*图集展示*/
        .showImg{
            display: flex;
            align-items: center;
            flex-wrap: wrap;
            width: 550px;
        }
        .showImg li{
            list-style-type: none;
            margin-bottom: 15px;
            position: relative;
        }
        .image-border-box{
            width: 160px;
            height: 121px;
            display: flex;
            align-items: center;
            justify-content: center;
            box-sizing: content-box;
            border: 1px solid #ccc;
            padding: 1px;
            margin: 0 5px 5px 0;
        }
        .image-box{
            height: calc(100% - 2px);
            width: calc(100% - 2px);
            overflow: hidden;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .image-box img, .showImg img{
            width: 100%;
        }
        /*滚动图*/
        .swiper-container {
            width: 600px;
            height: 300px;
        }
        .swiper-slide {
            text-align: center;
            font-size: 18px;
            background: #fff;

            /* Center slide text vertically */
            display: -webkit-box;
            display: -ms-flexbox;
            display: -webkit-flex;
            display: flex;
            -webkit-box-pack: center;
            -ms-flex-pack: center;
            -webkit-justify-content: center;
            justify-content: center;
            -webkit-box-align: center;
            -ms-flex-align: center;
            -webkit-align-items: center;
            align-items: center;
        }
        #imgbtn {
            border-width: 0px;
            position: absolute;
            left: 0px;
            top: 0px;
            width: 21px;
            height: 21px;
        }

        .adopt,.noadopt {
            border-width: 0px;
            width: 70px;
            height: 34px;
            background: inherit;
            background-color: /*rgba(255, 102, 0, 1)*/#00a0e6;
            border: none;
            border-radius: 5px;
            -moz-box-shadow: none;
            -webkit-box-shadow: none;
            box-shadow: none;
            color: #ffffff;
        }
        .btAnswers {
            border-width: 0px;
            height: 34px;
            background: inherit;
            background-color: rgba(255, 255, 255, 1);
            box-sizing: border-box;
            border-width: 1px;
            border-style: solid;
            border-color: rgba(121, 121, 121, 1);
            border-radius: 5px;
            -moz-box-shadow: none;
            -webkit-box-shadow: none;
            box-shadow: none;
        }
        #btupdate {
            border-width: 0px;
            background: inherit;
            background-color: /*rgba(254, 205, 171, 1)*/#81cced;
            box-sizing: border-box;
            border-width: 1px;
            border-style: solid;
            border-color: rgba(121, 121, 121, 1);
            border-radius: 5px;
            -moz-box-shadow: none;
            -webkit-box-shadow: none;
            box-shadow: none;
        }

        /*视频播放*/
        .content-video {
            border-width: 0px;
            position: absolute;
            left: 141px;
            top: 20px;
            width: 721px;
            height: 437px;
            background: inherit;
            background-color: rgba(0, 0, 0, 1);
            box-sizing: border-box;
            border-width: 1px;
            border-style: solid;
            border-color: rgba(153, 153, 153, 1);
            border-radius: 0px;
            -moz-box-shadow: none;
            -webkit-box-shadow: none;
            box-shadow: none;
            font-family: "微软雅黑";
            font-weight: 400;
            font-style: normal;
            font-size: 14px;
            color: #ffffff;
        }
        /*轮播图*/
        .carousel{
            background-color: rgb(102, 102, 102);
            height: 2500px;
            width: 100%;
            position: absolute;
            left: 0px;
            top: 0px;
            z-index: 100;
            /*display:none;*/
        }
        /*视频查看*/
        .con_video{
            background-color: rgb(102, 102, 102);
            height: 2500px;
            width: 100%;
            position: absolute;
            left: 0px;
            top: 0px;
            z-index: 100;
            display:none;
        }

    </style>
</head>
<body>
<iframe id="iframe" style="display:none;" src=""></iframe>
<%--<form id="form" name="form" method="post" action="TipoffSubmit.do">--%>
	<input id="type" name="type" type="hidden" value="${type}">
	<input id="siteID" name="siteID" type="hidden" value="${siteID}">
    <input id="isUpdate" name="isUpdate" type="hidden" value="${isUpdate}">
    <input id="docID" name="docID" type="hidden" value="${docID}">
    <input id="docLibID" name="docLibID" type="hidden" value="${docLibID}">
    <input id="UUID" name="UUID" type="hidden" value="${UUID}">
    <input id="FVID" name="FVID" type="hidden" value="${FVID}">
    <div class="header">
        <span style="display: inline-block;width: 20%;">报料人： ${SYS_AUTHORS}</span>
        <span style="display: inline-block;width: 63%;">手机号码：${a_contactNo}</span>
        <span>状态： </span>
        <c:if test="${a_status==0}">
            <span class="status">待审核</span>
        </c:if>
        <c:if test="${a_status==1}">
            <span class="status">未采用</span>
        </c:if>
        <c:if test="${a_status==2}">
            <span class="status">已采用</span>
        </c:if>
    </div>
    <div style="border-top:1px #ccc solid;"></div>
    <div>
        <span style="display: inline-block;font-weight: bold;width: 85%;text-align: center;word-wrap: break-word;padding: 20px;">${SYS_TOPIC}</span>
        <input style="" type="button" id="btupdate" value="修改爆料信息">
    </div>
    <div>
        <span style="display: inline-block;width: 65%;padding-left: 40px;word-wrap: break-word;">事发地点：${a_location}</span>
        <span style="">来源：</span>
        <c:if test="${a_sourceType==0}">
            <span>APP客户端</span>
        </c:if>
        <c:if test="${a_sourceType==1}">
            <span>触屏端</span>
        </c:if>
        <c:if test="${a_sourceType==2}">
            <span>系统内部</span>
        </c:if>
    </div>
    <div style="margin-top: 20px;">
        <p style="margin-left: 40px;text-indent: 2em;">${a_content}</p>
    </div>
    <div class="container-details" style="margin-left: 40px;margin-top: 20px;">
        <div class="inform-text content-left" style="margin-bottom:10px;margin-top: 20px;font-weight: bold;">图片：</div>
            <ul class="showImg" id="discloseListPic">
            </ul>
        <div class="inform-text content-left" style="margin-bottom:10px;margin-top: 20px;font-weight: bold;">视频：</div>
        <div class="content-div">
            <video class="videoPlayer" style="width: 350px;height: 200px;" controls poster="" src="" webkit-playsinline />
        </div>
    </div>

    <div style="margin-left: 40px;margin-top: 20px;">
        <textarea name="a_answers" class="a_answers" class="custform-textarea validate[required]" style="width:615px;height:88px;" placeholder="回复报料人">${a_answers}</textarea>
       <div>
           <input type="button" class="btAnswers" value="发送回复">

           <c:if test="${a_status==0}">
               <input type="button" class="adopt" name="" value="采用">
               <input type="button" class="noadopt" name="" value="不采用">
           </c:if>
           <c:if test="${a_status==1}">
               <input type="button" class="adopt" name="" value="采用">
               <input type="button" class="noadopt" name="" value="不采用" style="display: none">
           </c:if>
           <c:if test="${a_status==2}">
               <input type="button" class="adopt" name="" value="采用" style="display: none">
               <input type="button" class="noadopt" name="" value="不采用">
           </c:if>

       </div>
    </div>
    <%--遮盖层--%>
    <div class="cover"></div>
    <%--轮播图--%>
    <div class="carousel">
        <img class="img"  src="./img/u364.png" id="imgbtn" style="float:left;position: relative;left:440px;top: 20px;">
        <div class="swiper-container" style="width: 400px;height: 700px;margin-left: 40px;fload:left;top: 20px;">
            <div class="swiper-wrapper">

            </div>
            <!-- Add Pagination -->
            <div class="swiper-pagination"></div>
            <!-- Add Arrows -->
            <div class="swiper-button-next" <%--style="background:url(./img/u628.png)"--%>></div>
            <div class="swiper-button-prev"<%-- style="background:url(./img/u630.png)"--%>></div>
        </div>
        <div style="position: relative;top: -286px;left: 466px;width: 220px;">
            <textarea  class="a_answers" name="a_answers" class="custform-textarea validate[required]" style="width:480px;height:88px;" placeholder="回复报料人">${a_answers}</textarea>
            <div>
                <input type="button" class="btAnswers" value="发送回复">

                <c:if test="${a_status==0}">
                    <input type="button" class="adopt" name="" value="采用">
                    <input type="button" class="noadopt" name="" value="不采用">
                </c:if>
                <c:if test="${a_status==1}">
                    <input type="button" class="adopt" name="" value="采用">
                    <input type="button" class="noadopt" name="" value="不采用" style="display: none">
                </c:if>
                <c:if test="${a_status==2}">
                    <input type="button" class="adopt" name="" value="采用" style="display: none">
                    <input type="button" class="noadopt" name="" value="不采用">
                </c:if>

            </div>
        </div>
    </div>
    <%--视频播放--%>
    <div class="con_video" <%--style="background-color: rgba(102, 102, 102, 1);margin-top: -700px;height: 800px;width: 1100px;z-index:21"--%>>
        <img class="img"  src="./img/u364.png" id="imgbtn_video" style="float:left;position: relative;left:863px;top: 20px;">
        <div class="content-video">
            <video class="videoPlayer" style="width: 721px;height: 437px;" controls poster="" src="" webkit-playsinline />
        </div>
        <div style="position: relative;top: 445px;left: 141px;width: 220px;">
            <textarea class="a_answers" name="a_answers" class="custform-textarea validate[required]" style="width: 721px;height:88px;" placeholder="回复报料人">${a_answers}</textarea>
            <div>
                <input type="button" class="btAnswers" value="发送回复">

                <c:if test="${a_status==0}">
                    <input type="button" class="adopt" name="" value="采用">
                    <input type="button" class="noadopt" name="" value="不采用">
                </c:if>
                <c:if test="${a_status==1}">
                    <input type="button" class="adopt" name="" value="采用">
                    <input type="button" class="noadopt" name="" value="不采用" style="display: none">
                </c:if>
                <c:if test="${a_status==2}">
                    <input type="button" class="adopt" name="" value="采用" style="display: none">
                    <input type="button" class="noadopt" name="" value="不采用">
                </c:if>

            </div>
        </div>
    </div>


<div style="display: none">
    <%=request.getAttribute("formContent")%>
</div>


<script>
    /*$(".carousel").css("display","none");*/
</script>
</body>
</html>