<%--
  Created by IntelliJ IDEA.
  User: codingnuts
  Date: 2017/3/16
  Time: 上午10:31
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title><%=com.founder.e5.workspace.ProcHelper.getProcName(request)%>
    </title>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
    <meta content="IE=edge" http-equiv="X-UA-Compatible"/>
    <link type="text/css" rel="stylesheet" href="../../e5style/reset.css"/>
    <link type="text/css" rel="stylesheet" href="../../e5script/jquery/jQuery-Validation-Engine/css/validationEngine.jquery.css"/>
    <link type="text/css" rel="stylesheet" href="../../e5script/jquery/dialog.style.css"/>
    <link type="text/css" rel="stylesheet" href="../../e5style/e5form-custom.css"/>
    <link type="text/css" rel="stylesheet" href="../../xy/css/form-custom.css"/>
    <link type="text/css" rel="stylesheet" href="../script/bootstrap-3.3.4/css/bootstrap.min.css">
    <link type="text/css" rel="stylesheet" href="../script/bootstrap-datetimepicker/css/datetimepicker.css" media="screen">
    <link type="text/css" rel="stylesheet" href="css/live.css"/>

    <script type="text/javascript" src="../script/jquery/jquery.min.js"></script>
    <script type="text/javascript" src="../script/bootstrap-3.3.4/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="../script/jquery-validation-engine/js/languages/jquery.validationEngine-zh_CN.js"></script>
    <script type="text/javascript" src="../script/jquery-validation-engine/js/jquery.validationEngine.js"></script>
    <script type="text/javascript" src="../../e5script/jquery/jquery.dialog.js"></script>
    <script type="text/javascript" src="../../e5workspace/script/form-custom.js"></script>
    <script src="../script/cookie.js" type="text/javascript"></script>
    <script src="../script/picupload/upload_api.js" type="text/javascript"></script>
    <script>
        var liveItemForm = {
            isUpdate : "${isUpdate}",
            author : "${author}",
            sourceType : "${sourceType}"
        }
        var article = {
            videoPlugin : "${videoPlugin}" //模拟视频稿中使用的参数：视频控件播放地址
        }
    </script>
    <style>
        .file-footer-buttons .kv-file-upload {
            display: none !important;
        }

        .file-drop-zone {
            height: 300px;
            overflow: auto !important;
        }
    </style>

</head>
<body>
<div class="container">
    <form id="form" name="form" method="post" action="LiveItemSubmit.do">
        <input id="userName" type="hidden" value="${userName}"/>
        <input id="docLibID" name="docLibID" type="hidden" value="${docLibID}"/>
        <input id="groupID" name="groupID" type="hidden" value="${groupID}"/>
        <input id="siteID" name="siteID" type="hidden" value="${siteID}"/>
        <input id="isUpdate" type="hidden" name="isUpdate" value="${isUpdate}">
        <input id="attachments" type="hidden" name="attachments" value="${attachments}">
        <input id="title" type="hidden" name="title" value="${title}">
        <input id="content" type="hidden" name="content" value="${content}">
        <%=request.getAttribute("formContent")%>

        <div class="mainBodyWrap" style="padding-left: 50px;">
            <!-- Nav tabs -->
            <ul class="nav nav-tabs" role="tablist">
                <li role="presentation" class="active">
                    <a href="#home" aria-controls="home" role="tab" data-toggle="tab">图片</a></li>
                <li role="presentation"><a href="#profile" aria-controls="profile" role="tab" data-toggle="tab">视频</a>
                </li>
            </ul>

            <!-- Tab panes -->
            <div class="tab-content">
                <div role="tabpanel" class="tab-pane active" id="home">
                    <div class="form-group ">

                        <input id="picInput" type="file" name="file" multiple>
                        <input type="hidden" id="picSrcHidden"/>
                    </div>
                </div>
                <div role="tabpanel" class="tab-pane" id="profile">
                    <%@include file="../video/inc/videoUpload.inc" %>
                </div>
            </div>

        </div>
        <div>
            <li class="btngroup">
                <input class="dosave" type="button" id="btnSave" value="保存"/>
                <input class="docancle" type="button" id="btnCancel" value="关闭"/>
            </li>
        </div>
    </form>
</div>
<script>
    var picUpload;
    $(function(){
        init();
        initUpload();//初始化图片上传插件
        initAuthors();
        if(liveItemForm&&liveItemForm.isUpdate) {
            initUpdate();//如果是修改，则对页面进行赋值
        }
    });

    function initAuthors(){
        if(xy_cookie.getCookie("liveItem_sourceType")){
            $("input:radio[name=a_sourceType][value=" +
                xy_cookie.getCookie("liveItem_sourceType") +
                "]").attr("checked", true);
        }

        if(xy_cookie.getCookie("liveItem_authors")){
            $("#SYS_AUTHORS").val(xy_cookie.getCookie("liveItem_authors"));
        }else{
            var userName = $("#userName").val();
            if(!$.isEmptyObject(userName)){
                $("#SYS_AUTHORS").val(userName);
            }
        }
    }

    function init(){
        $(".file-drop-zone").addClass('clearfix');

        $("#btnCancel").click(function(){
            window.onbeforeunload = null;
            var dataUrl = "../../e5workspace/after.do?UUID=" + $("#form #UUID").val();
            window.location.href = dataUrl;
        })

        $("#btnSave").click(function(){
            var vidoes = getVideos();
            var pics = getPics() || [];

            if(vidoes!=null&&pics.length!=0){
                alert("不允许同时上传图片和视频，请修改后再进行保存！");
                return false;
            }

            var attachments = {
                pics: pics,
                videos: vidoes
            };

            $.ajax({
                url: "../../xy/pic/checkExtractIsFinished.do",
                dataType: "json",
                data: {
                    paths: JSON.stringify(pics)
                },
                async: false,
                success: function(json){
                    if(json){
                        if(json.code == 0){
                            if(json.isFinished){
                                $("#a_attachments").val(JSON.stringify(attachments));
                                xy_cookie.setCookie("liveItem_authors", $("#SYS_AUTHORS").val());
                                xy_cookie.setCookie("liveItem_sourceType", $("input:radio[name=a_sourceType]:checked").val());
                                $("#form").submit();
                            } else{
                                alert("图片抽图还未完成！请稍后再试！");
                            }
                        } else{
                            alert(json.msg);
                        }
                    }
                }
            });
        });
    }

    function getPics(){
        var _arr = [];
        var _data = picUpload.getDataList();
        for(var i in _data){
            _arr.push(_data[i].imagePath);
        }

        return _arr;
    }

    function getVideos(){
        var vs = video_form.getVideoInfo();
        return vs;

        var vidoes = new Array();
        if(vs && vs.length > 0){
            for(var i = 0; i < vs.length; i++){
                vidoes.push(vs[i].urlApp);
            }
        }
        return vidoes;
    }

    //修改，对页面进行赋值
    function initUpdate() {
        $("#SYS_AUTHORS").val(liveItemForm.author);
        $("#a_title").val($("#title").val());
        $("#a_content").val($("#content").val());
        var sourceType = new Number(liveItemForm.sourceType);//单选项，值需要为数值类型
        $("#a_sourceType").val(sourceType);
        loadVideo();
    }

    function initUpload(){
        var param = {//初始化图片插件
            uploadUrl: "../../xy/upload/uploadFileF.do",
            showRemove: false,
            showUpload: true,
            autoCommit: true,
            rootUrl:'../../',
            maxFileCount: 40,
            minFileCount: 1,
            rootUrl: '../../'
        };
        //修改页面存在图片附件的，铺到页面
        var attachments = $("#attachments").val();
        if(attachments==null||attachments==""){
            attachments = {pics: [], videos: []};
        }else{
            attachments = eval("(" + attachments + ")");
        }

        var list = attachments.pics;
        var initialPreview = [];
        var initialPreviewConfig = [];
        for(var i = 0, li = null; li = list[i++];){
            initialPreview.push("../../xy/image.do?path=" + li);
            initialPreviewConfig.push({
                width: "120px",
                url: "../../xy/upload/deletePreviewThumb.do",
                imagePath : li,
                key: i});
        }
        param.initialPreview = initialPreview;
        param.initialPreviewConfig = initialPreviewConfig;

        picUpload = new Upload("#picInput", param);

    }

    function loadVideo() {
        var theURL = "./Videos.do?DocLibID=" + $("#docLibID").val() + "&DocIDs=" + $("#DocID").val();
        $.ajax({url: theURL, async:true,
            success: function(datas) {
                video_form.setVideoInfo(datas);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                alert(errorThrown + ':' + textStatus);  // 错误处理
            }
        });
    }
</script>
</body>
</html>
