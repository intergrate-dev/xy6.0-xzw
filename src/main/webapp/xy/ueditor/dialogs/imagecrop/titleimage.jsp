<%@include file="../../../../e5include/IncludeTag.jsp"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<html lang="zh-CN">
  <head>
    <title>稿件详情</title>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <%--<script  type="text/javascript" src="<%=path %>/xy/ueditor/dialogs/imagecrop/jquery/jcrop-12/js/jquery.cookie.js"></script>--%>
    <link type="text/css" rel="stylesheet" href="<%=path %>/xy/script/bootstrap-3.3.4/css/bootstrap.min.css">
  <link rel="stylesheet" href="<%=path %>/xy/ueditor/dialogs/imagecrop/jquery/jcrop-12/css/jquery.Jcrop.css" type="text/css" />
  <style type="text/css">
    body
    {
        display: none;
        font-family: "微软雅黑";
    }

    .preview-pane {
        display: block;
        position: absolute;
        z-index: 2000;
        padding: 2px;
        border: 1px rgba(0,0,0,.4) solid;
        background-color: white;
        margin-left: 30px;

    }

    .preview-container {
        width: 260px;
        height: 120px;
        overflow: hidden;
    }
    .chosen{
        padding: 6px;
        -webkit-border-radius: 6px;
        -moz-border-radius: 6px;
        border-radius: 6px;

        -webkit-box-shadow: 1px 1px 5px 2px rgba(0, 0, 0, 0.2);
        -moz-box-shadow: 1px 1px 5px 2px rgba(0, 0, 0, 0.2);
        box-shadow: 1px 1px 5px 2px rgba(0, 0, 0, 0.2);
    }
    .preview-word{
        display: block;
        position: absolute;
        z-index: 2000;
        margin-left: 30px;
    }
    input.preview-word-select{
        margin: 4px 5px 0px;
    }
    .preview-word-assign{
        position: absolute;
        top: 0px;
        left: 202px;
        float:right;
        display:none;
        margin-left: 34px;
        margin-top: -2px;
    }
    /*border: 1px solid #ddd;
        background: #f6f6f6;*/
    .upload {
        width: 120px;
        height: 40px;
        line-height: 40px;
        position: relative;
        text-align: center;
        color: #333;
        overflow: hidden;
        cursor:pointer;
        margin-left: 7px;
    }
    #localFile {
        position: absolute;
        bottom: 0;
        left: 0;
        font-size: 40px;
        height: 130%;
        width: 100%;
        z-index: 1;
        opacity:0;
        filter:alpha(opacity=0);
        cursor:pointer;
    }
    .btngroup {
        margin: 5px 5px;
        font-family: microsoft yahei;
        color: #fff;
        border: none;
        background: #b1b1b1;
        border-radius: 3px;

        font-size: 12px;
        opacity: 1 !important;

    }
    #submitBtn {
        background: #00a0e6;
    }
    #useOrignalBtn{
        background-color: green;
        cursor: pointer;
    }
      input.preview-pane-input{
          position: absolute;
          width:54px;
      }
      input.preview-pane-btn{
          position: absolute;
          width: 45px;
          left: 220px;
          top: 0px;
      }
      span.preview-pane-span-first{
          position: absolute;
          left: 36px;
          margin-top: 1px;
          color: gray;
      }
      span.preview-pane-span-second{
          position: absolute;
          left: 106px;
          margin-top: 1px;
          color: gray;
      }
      input.optionWidth{
        left: 218px;
        top:0px;
      }
      .selectClass{
        position: relative;
        left: 128px;
        top: 0px;
        height: 26px;
        top: 0px;
        width: 86px !important;
      }
      .col-md-2{
        width: 13.66666667%;
      }
  </style>
</head>

<body>
<div id="waitingDiv" style="display:none; margin: auto;text-align: center; vertical-align: middle;">
    <img src="dialogs/imagecrop/loading_16.gif" />
</div>
<!-- Nav tabs -->
<ul id="tabUl" class="nav nav-tabs" role="tablist">
    <li role="presentation" class="active"><a href="#customImage" role="tab" data-toggle="tab">自定义</a></li>
    <%--<c:if test='${ itype!="all"}'>--%>
        <li role="presentation"><a href="#imageLib" role="tab" data-toggle="tab">图片库</a></li>
    <%--</c:if>--%>
</ul>
<!-- Tab panes -->
<div id="detailDiv" class="tab-content">
    <div role="tabpanel" class="tab-pane active" id="customImage">
        <div id="mainDiv" class="container-fluid">
            <div class="row">
                <div>
                    <div class="upload" >
                        <input style="cursor:pointer;" type="button" id="upBtn"  class="btn btn-primary" value="上传本地图片"  />

                        <form id="picForm" name="picForm" enctype="multipart/form-data" action="<%=path %>/xy/ueditor/uploadPic.do" method="post">
                            <input id="localFile" name="localFile" type="file" accept="image/gif, image/jpeg, image/gif, image/png"  />
                            <input id="imageName" name="imageName" type="hidden"  value="<c:out value="${fileName}"/>" />
                            <input name="itype" type="hidden"  value="<c:out value="${itype}"/>" />
                        </form>
                    </div>
                </div>
            </div>
            <div class="row">
                <div id="imgContainDiv" style="margin: 0px 10px 0px 10px; padding: 0px;float: left; border: 1px rgba(0,0,0,.4) solid;width: 600px; height: 440px;">
                    <!--<img  id="target" style="max-width: 550px; max-height: 500px;"  src="../../../../xy/image.do?path=<c:out value="${imagePath}"/>" />-->
                    <img  id="target" style="max-width: 600px; max-height: 440px;display: none;"  src="" />
                </div>
                <div id="previewImgDiv" style="float: left;">
                    <c:if test='${ itype=="all" || itype=="small"}'>
                        <div class="row" >
                            <div class="preview-word" style="top: 47px;">
                                <input type="radio" name="img_small" class="preview-word-select" id="preview-word-small-select_1" checked value="<c:out value="${smallRadio}"/>"><c:out value="${smallRadio}"/>
                                <%--<input type="radio" name="img_small" class="preview-word-select" id="smallUseOrignalBtn" value="使用原图">使用原图--%>
                                <input type="radio" name="img_small" class="preview-word-select" id="preview-word-small-select_2" value="自定义">自定义<span id="preview-word-select-small"></span>
                                <span id="preview-word-assign_small" class="preview-word-assign">
                                    <input id="preview-pane-small-input_1" class="preview-pane-input" type="text" value="" placeholder="0000" maxlength="4" style="left:0px;">
                                    <span class="preview-pane-span-first">px</span>
                                    <span style="position: absolute; left: 42px;top: 3px;margin: 0px 0px 0px 15px;">×</span>
                                    <input id="preview-pane-small-input_2" class="preview-pane-input" type="text" value="" placeholder="0000" maxlength="4" style="left:70px;">
                                    <span class="preview-pane-span-second">px</span>
                                    <select class ="selectClass" id="smallImgselect" onchange="outputSelect(this)">
                                        <option value="noData">暂无历史</option>
                                    </select>
                                    <input class="preview-pane-btn" onclick="setImgRadio('small')" id="preview-pane-btn_small" value="确定" type="button">
								</span>
                            </div>
                            <div id="preview-pane-small" itype="small" class="preview-pane" style="top: 72px; " onclick="chooseCropImage('small')">
                                <div id="pp-small" class="preview-container">
                                    <img src="" class="jcrop-preview" alt="Preview" />
                                </div>
                            </div>
                        </div>
                    </c:if>
                    <c:if test='${itype=="all" || itype=="mid"}'>
                        <div class="row">
                            <div class="preview-word" style="top: 212px;">
                                <input type="radio" name="img_mid" class="preview-word-select" id="preview-word-mid-select_1" checked value="<c:out value="${midRadio}"/>"><c:out value="${midRadio}"/>
                                <%--<input type="radio" name="img_mid" class="preview-word-select" id="preview-word-mid-useOrignalBtn" value="使用原图">使用原图--%>
                                <input type="radio" name="img_mid" class="preview-word-select" id="preview-word-mid-select_2" value="自定义">自定义<span id="preview-word-select-mid"></span>
                                <span id="preview-word-assign_mid" class="preview-word-assign" style="">
                                    <input id="preview-pane-mid-input_1" class="preview-pane-input" type="text" value="" placeholder="0000" maxlength="4" style="left:0px;">
                                    <span class="preview-pane-span-first">px</span>
                                    <span style="position: absolute;left: 42px;top: 3px;margin: 0px 0px 0px 15px;">×</span>
                                    <input id="preview-pane-mid-input_2" class="preview-pane-input" type="text" value="" placeholder="0000" maxlength="4" style="left:70px;">
									<span class="preview-pane-span-second">px</span>
                                    <select class="selectClass" id="midImgselect" onchange="outputSelect(this)">
                                        <option value="noData">暂无历史</option>
                                    </select>
                                    <input class="preview-pane-btn" onclick="setImgRadio('mid')" id="preview-pane-btn_mid" value="确定" type="button">
									</span>
                            </div>
                            <div id="preview-pane-mid" itype="mid"  class="preview-pane"  style="top: 237px; display: block" onclick="chooseCropImage('mid')">
                                <div id="pp-mid" class="preview-container">
                                    <img src="" class="jcrop-preview" alt="Preview" />
                                </div>
                            </div>
                        </div>
                    </c:if>
                    <c:if test='${itype=="all" || itype=="big" }'>
                        <div class="row">
                            <div class="preview-word" style="top: 377px;">
                                <input type="radio" name="img_big" class="preview-word-select" id="preview-word-big-select_1" checked value="<c:out value="${bigRadio}"/>"><c:out value="${bigRadio}"/>
                                <%--<input type="radio" name="img_big" class="preview-word-select" id="preview-word-big-useOrignalBtn" value="使用原图" >使用原图--%>
                                <input type="radio" name="img_big" class="preview-word-select" id="preview-word-big-select_2" value="自定义">自定义<span id="preview-word-select-big"></span>
                                <span id="preview-word-assign_big" class="preview-word-assign" style="">
                                    <input id="preview-pane-big-input_1" class="preview-pane-input" type="text" value="" placeholder="0000" maxlength="4" style="left:0px;">
                                    <span class="preview-pane-span-first">px</span>
                                    <span style="position: absolute;left: 42px;top: 3px;margin: 0px 0px 0px 15px;">×</span>
                                    <input id="preview-pane-big-input_2" class="preview-pane-input" type="text" value="" placeholder="0000" maxlength="4" style="left:70px;" >
                                    <span class="preview-pane-span-second">px</span>
                                    <select class="selectClass" id="bigImgselect" onchange="outputSelect(this)">
                                        <option value="noData">暂无历史</option>
                                    </select>
                                    <input class="preview-pane-btn" onclick="setImgRadio('big')" value="确定" id="preview-pane-btn_big" type="button">
								</span>
                            </div>
                            <div id="preview-pane-big" itype="big"  class="preview-pane"  style="top: 402px; display: block" onclick="chooseCropImage('big')">
                                <div id="pp-big" class="preview-container">
                                    <img src="" class="jcrop-preview" alt="Preview" />
                                </div>
                            </div>
                        </div>
                    </c:if>
                </div>
            </div>
            <div>
                <div class="row">
                    <div class="col-md-2">
                        截图宽：<span id="selectorW"/>0&nbsp;px</span>
                    </div>
                    <div class="col-md-2">
                        截图高：<span id="selectorH"/>0&nbsp;px</span>
                    </div>
                    <div class="col-md-2">
                        原图宽：<span id="orignalW"/>0 px</span>
                    </div>
                    <div class="col-md-2">
                        原图高：<span id="orignalH"/>0 px</span>
                    </div>

                </div>
            </div>
            <div class="row" style="text-align: right;">
                <%--<input class="btn btn-default btngroup" type="button" id="useOrignalBtn" value="全部使用原图" onclick="useOrignalImg()" style="margin: 10px;width: 95px;" disabled="disabled" />--%>
                <input class="btn btn-default btngroup" type="button" id="submitBtn" value="确定" onclick="submitCropImg()" style="margin: 10px;width: 80px;" disabled="disabled" />
                <input class="btn btn-default btngroup" type="button" id="cancelBtn" value="取消" onclick="cancelCropImg()" style="margin: 10px;width: 80px;"  />
            </div>
        </div>
    </div>

	<div role="tabpanel" class="tab-pane" id="imageLib">
	<iframe src="../GroupSelect.do?type=0&siteID=${param.siteID}&all=1" frameborder="0" style="width: 980px; height: 550px;" ></iframe>
	</div>
	</div>
</body>
<script type="text/javascript" src="<%=path %>/e5script/jquery/jquery-1.9.1.min.js"></script>
<script src="<%=path %>/xy/ueditor/dialogs/imagecrop/jquery/jcrop-12/js/jquery.Jcrop.js"></script>
<script type="text/javascript" src="<%=path %>/xy/script/bootstrap-3.3.4/js/bootstrap.min.js"></script>
<script src="<%=path %>/xy/ueditor/dialogs/imagecrop/uploadPreview.js"></script>
<script type="text/javascript">
//图片路径
var imgPath = '../../xy/image.do?path=<c:out value="${imagePath}"/>';
var _imgPath = "";
//分别判断标题图是否已经被自定义了尺寸大小
var smallResize = '<c:out value="${smallResize}"/>';
var midResize = '<c:out value="${midResize}"/>';
var bigResize = '<c:out value="${bigResize}"/>';
//图片名称
var imgName = '<c:out value="${imagePath}"/>';
//图片种类，即是 小，中，大
var itype = '<c:out value="${itype}"/>';
//以下是每种标题图的尺寸，例如：123*123
var smallRadio = '<c:out value="${smallRadio}"/>';
var midRadio = '<c:out value="${midRadio}"/>';
var bigRadio = '<c:out value="${bigRadio}"/>';
//目标图片的最大宽高
var cropImageMaxWidth = 600;
var cropImageMaxHeight = 440;

</script>
<script src="<%=path %>/xy/ueditor/dialogs/imagecrop/titleimage.js?t=12"></script>

</html>
<script>
$(function(){

    if(window.parent.document.URL.indexOf('OriginalWX') != -1){
        $('#preview-pane-small').parent().hide();
        $('#preview-pane-mid').parent().hide();

        $('#preview-pane-big').prev().css({"top": "47px"});
        $('#preview-pane-big').css({"top": "80px"});
    }
    var obj2 = JSON.parse(localStorage.getItem("optionJion"));
    if(obj2){
        if(obj2.small.length){
            typeImgselect(obj2,'small');
        }
        if(obj2.mid.length){
            typeImgselect(obj2,'mid');
        }
        if(obj2.big.length){
            typeImgselect(obj2,'big');
        }
    }
    //重新再写入
    function typeImgselect(type,_type){

        $('#'+ _type +'Imgselect').empty();

        var str = "<option style='display:none' selected>"+'选择尺寸'+"</option>";

        for(var i = 0; i < type[_type].length; i ++){
            str += "<option value='"+  type[_type][i] +"'>"+  type[_type][i] +"</option>"
        }
        $('#'+ _type +'Imgselect').append(str)
    }
    //每次设置话题图片窗口弹出时设置其样式
    if ($(".aui_title", parent.document).eq(0).html() == '设置话题图片') {
        $(".preview-word").css('top', '83px');
        $("#preview-pane-big").css('top', '120px');
        $("#submitBtn").parent().css('margin-top', '-30px');
        $("#useOrignalBtn").val('使用原图');
    }
    $('body').show();
});
    function outputSelect(e){
    //获取id
        var selectId = $(e).attr('id');
        //获取text/value
        var selectText = $('#'+ selectId).find("option:selected").text();
        var selectValue = $('#'+ selectId).val();

        if(selectValue == 'noData'){
            return;
        }else{
            var selectJion = selectText.split('*')
            var inputSelect = $('#'+ selectId).parent().find('.preview-pane-input');
            $(inputSelect[0]).val(selectJion[0]);
            $(inputSelect[1]).val(selectJion[1]);
        }
    }
</script>
