<%--
  Created by IntelliJ IDEA.
  User: isaac_gu
  Date: 2017/4/10
  Time: 15:25
  To change this template use File | Settings | File Templates.
--%>
<%@include file="../../e5include/IncludeTag.jsp" %>
<%@page contentType="text/html;charset=UTF-8"%>
<html>
<head>
    <title><%=com.founder.e5.workspace.ProcHelper.getProcName(request)%>
    </title>
    <meta charset="UTF-8">
    <link type="text/css" rel="stylesheet" href="../../e5style/reset.css"/>
    <link type="text/css" rel="stylesheet" href="../script/bootstrap-3.3.4/css/bootstrap.min.css">
    <link type="text/css" rel="stylesheet" href="../../e5script/jquery/jQuery-Validation-Engine/css/validationEngine.jquery.css"/>
    <link type="text/css" rel="stylesheet" href="../../e5script/jquery/dialog.style.css"/>
    <link type="text/css" rel="stylesheet" href="../../e5style/e5form-custom.css"/>
    <link type="text/css" rel="stylesheet" href="../../xy/css/form-custom.css"/>
    <script type="text/javascript" src="../script/jquery/jquery.min.js"></script>
    <script type="text/javascript" src="../script/bootstrap-3.3.4/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="../script/jquery-validation-engine/js/languages/jquery.validationEngine-zh_CN.js"></script>
    <script type="text/javascript" src="../script/jquery-validation-engine/js/jquery.validationEngine.js"></script>
    <script type="text/javascript" src="../../e5script/jquery/jquery.dialog.js"></script>
    <script type="text/javascript" src="../../e5workspace/script/form-custom.js"></script>
    <script src="../script/picupload/upload_api.js" type="text/javascript"></script>
    <style>
        .file-drop-zone {
            height: auto;
        }

        .btngroup {
            margin-top: 6px;
        }

        .nav-tabs > li {
            margin-top: 11px;
        }

        #setting {
            height: 48px;
        }

        .dosave {
            background-color: #00A0E6;
            border: none;
            padding: 5px 25px;
            margin-right: 26px;
            line-height: 16px;
            color: #fff;
            border-radius: 3px;
        }

        .docancle {
            background-color: #b1b1b1;
            border: none;
            color: #fff;
            padding: 5px 25px;
            line-height: 16px;
            border-radius: 3px;
        }
    </style>
</head>
<body>
<div class="container" style="margin-top: 10px;">
<!-- 	<iframe name="iframe" id="iframe" src="" class="frm"></iframe> -->
	<iframe id="iframe" name="iframe" style="display:none;"></iframe>
    <form id="form1" class="form-horizontal" name="form1" method="post" action="messageSave.do">
        <input type="hidden" id="DocLibID" name="DocLibID" value="<c:out value="${DocLibID}"/>"/>
        <input type="hidden" id="UUID" name="UUID" value="<c:out value='${UUID}'/>"/>
        <input type="hidden" id="a_siteID" name="a_siteID" value="<c:out value='${siteID}'/>"/>
        <div class="form-group">
            <label for="a_content" class="col-sm-2 control-label">内容</label>
            <div class="col-sm-10">
                <textarea type="text" class="form-control custform-input" id="a_content" name="a_content" placeholder="" style="height: 100px;"><c:out value='${a_content}'/></textarea>
                <input type="hidden" id="a_attachments" name="a_attachments" value="<c:out value='${a_attachments}'/>"/>
            </div>
        </div>
        <div class="form-group">
            <label class="col-sm-2 control-label">附件</label>
            <div class="col-sm-10">
                <!-- Nav tabs -->
                <ul class="nav nav-tabs" role="tablist">
                    <li role="presentation" class="active">
                        <a href="#home" aria-controls="home" role="tab" data-toggle="tab">图片</a></li>
                    <li role="presentation">
                        <a href="#profile" aria-controls="profile" role="tab" data-toggle="tab">视频</a>
                    </li>
                </ul>

                <!-- Tab panes -->
                <div class="tab-content">
                    <div role="tabpanel" class="tab-pane active" id="home">
                        <div class="form-group ">
                            <input id="picInput" type="file" name="file" class="validate[maxSize[255],required]" multiple>
                        </div>
                    </div>
                    <div role="tabpanel" class="tab-pane" id="profile">
                        <%@include file="../video/inc/videoUpload.inc" %>
                    </div>
                </div>
            </div>
        </div>
        <div class="form-group">
            <div class="col-sm-offset-2 col-sm-10">
                <ul>
                    <li class="btngroup">
                        <input class="dosave" type="button" id="btnSave" value="保存"/>
                        <input class="docancle" type="button" id="btnCancel" value="关闭"/>
                    </li>
                </ul>
            </div>
        </div>
    </form>
</div>
<script>
    $(function(){

        var picUpload = new Upload("#picInput", {
            uploadUrl: "../../xy/upload/uploadFileF.do",
            rootUrl:'../../',
            showRemove: false,
            showUpload: true,
            showSort: true,
            autoCommit: true,
            maxFileCount: 40,
            minFileCount: 1,
            rootUrl: '../../'

        });

        $("#btnSave").click(function(){
            if($.trim($("#a_content").val()) == ""){
                alert("内容不能为空！");
                return ;
            }
            var vidoes = getVideos();
            var pics = getPics() || [];
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
                                $("#form1").submit();
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
        function getPics(){
            var _arr = [];
            if(picUpload.getFileList().config){
                var _data = picUpload.getFileList().config;
                for(var i in _data){
                    _arr.push(_data[i].imagePath);
                }
            }
            return _arr;
        }

        function getVideos(){
            var vidoes = new Array();
            var vs = video_form.getVideoInfo();
            if(vs && vs.length > 0){
                for(var i = 0; i < vs.length; i++){
                    vidoes.push(vs[i].urlApp);
                }
            }
            return vidoes;
        }

        $(".file-drop-zone").addClass('clearfix');

        $("#btnCancel").click(function(){
            window.onbeforeunload = null;

            $("#btnFormSave").disabled = true;
            $("#btnFormCancel").disabled = true;
            $("#btnFormSaveSubmit").disabled = true;
            
            var dataUrl = "e5workspace/after.do?UUID=" + $("#UUID").val();
    		dataUrl = e5_form.dealUrl(dataUrl);
    		document.getElementById("iframe").contentWindow.location.href = dataUrl;
        });
    });
</script>
</body>
</html>
