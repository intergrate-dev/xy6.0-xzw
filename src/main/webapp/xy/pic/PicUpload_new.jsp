<%@include file="../../e5include/IncludeTag.jsp" %>
<%@ page pageEncoding="UTF-8" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/>
    <link type="text/css" rel="stylesheet" href="../script/bootstrap-3.3.4/css/bootstrap.min.css">
    <link type="text/css" rel="stylesheet" href="../../e5script/jquery/dialog.style.css"/>
    <link type="text/css" rel="stylesheet" href="../pic/css/pic.css"/>
    <link type="text/css" rel="stylesheet" href="../../e5script/jquery/dialog.style.css"/>
    <script type="text/javascript" src="../script/jquery/jquery.min.js"></script>
    <script type="text/javascript" src="../script/bootstrap-3.3.4/js/bootstrap.min.js"></script>
    <script src="../script/picupload/upload_api.js" type="text/javascript"></script>
    <script type="text/javascript" src="../../e5script/jquery/jquery.dialog.js"></script>
    </head>
<body>
<div class="container kv-main">
    <form enctype="multipart/form-data">
        <div class="form-group">
            <input id="uploadInput" type="file" name="file" multiple>
        </div>
        <hr>
    </form>
</div>
<div id="headline" class="headline" style="display: block">
    <span class="picintroduce">描述</span>
    <textarea class="topicintro" id="topic" name="topic" rows="3" cols="20">${topic}</textarea>
    <div style="clear: both;"></div>
    <input class="confirmbtn" type="button" value="确定" id="confirm" name="confirm"/>
    <input class="editCancelbtn" type="button" value="取消" id="editCancel" name="editCancel"/>
</div>
<input type="hidden" id="currentNum" name="currentNum"/>
<input type="hidden" id="totalNum" name="totalNum"/>
<input type="hidden" id="filePath" name="filePath"/>
<input type="hidden" id="siteID" name="siteID" value="${siteID}"/>
<input type="hidden" id="DocLibID" name="DocLibID" value="${DocLibID}"/>
<input type="hidden" id="FVID" name="FVID" value="${FVID}"/>
<input type="hidden" id="UUID" name="UUID" value="${UUID}"/>
<input type="hidden" id="picInfoList" name="picInfoList" value='${picInfoList}'/>
<input type="hidden" id="p_groupID" name="p_groupID" value="${p_groupID}"/>
<input type="hidden" id="p_articleID" name="p_articleID" value="${p_articleID}"/>
<input type="hidden" id="fromPage" name="fromPage" value="${fromPage}"/>
<input type="hidden" id="p_catID" name="p_catID" value='${p_catID}'/>
<input type="hidden" id="overall" name="overall" value='${overall}'/>

<script>
        var isNew = '${isNew}';
        var upload;
        $(function(){
            initUploadPlugins();
            initBtnClick();
        });


        var oldKey,oldUpload,oldPath,imgLiList;
        var pic = {};
        function initUploadPlugins(){
            var param = {
                uploadUrl: '../../xy/upload/uploadFileF.do',
                rootUrl:'../../',
                showSort: true,
                allowedFileExtensions:['jpg', 'jpeg', 'gif', 'bmp', 'png','zip'],
                showContent: true,
                autoCommit: true,
                showApplyAll: true,
                showEditor: true
            };
            param.modifyPicture = function(key, path, _upload,_list){
                oldKey = key;
                oldPath = path;
                oldUpload = _upload;
                imgLiList = _list;
                imageEditor();
            };
            if(isNew != "true"){
                var list = JSON.parse($("#picInfoList").val());
                var initialPreview = [];
                var initialPreviewConfig = [];
                for(var i = 0, li = null; li = list[i++];){
                    initialPreview.push("../../xy/image.do?path=" + li.picPath  + ".0.jpg");
                    initialPreviewConfig.push({url: "../../xy/upload/deletePreviewThumb.do",
                        imagePath: li.picPath,
                        key: i, caption: li.content});
                }
                param.initialPreview = initialPreview;
                param.initialPreviewConfig = initialPreviewConfig;
            }
            upload = new Upload("#uploadInput", param);
        }
        function imageEditor(event){

            var url = "../../xy/script/picupload/imageeditor.jsp";
            var pos = {left : "100px",top : "50px",width : "1000px",height : "500px"};
            pic.dialog = e5.dialog({type : "iframe", value : url}, {
                showTitle : true,
                title: "图片编辑",
                width : "1000px",
                height : "500px",
                pos : pos,
                resizable : false
            });
            pic.dialog.show();
        }

        //图片编辑之后回调
        function resetImageMsg(key, path, showPath, _upload, imageList){
            _upload.doModifyPicture(key, path, showPath, imageList);
        }

        //选择窗口：取消后
        function groupSelectCancel() {
            if(pic.dialog) {
                pic.dialog.close();
            }
        }
        function initBtnClick(){
            // 点击确认按钮
            $('input#confirm').click(function(){
                if(upload.getDataList().length == 0){
                    alert("未选择图片！");
                    return;
                }
                var list = [];
                for(var i = 0, li = null; li = upload.getFileList().config[i++];){
                    list.push({
                        picPath: li.imagePath,
                        content: li.caption
                    });
                }

                var jsonParam = {
                    "siteID": $('#siteID').val(),
                    "DocLibID": $('#DocLibID').val(),
                    "FVID": $('#FVID').val(),
                    "UUID": $('#UUID').val(),
                    "topic": $('#topic').val(),
                    "overall": $('#overall').val(),
                    "p_catID": $('#p_catID').val(),
                    "fromPage": $('#fromPage').val(),
                    "p_groupID": $('#p_groupID').val()
                };

                var param = {
                    list: list,
                    doc: jsonParam
                };
                //console.info(param);
                $.ajax({
                    url: "../../xy/pic/savePics.do",
                    type: "POST",
                    dataType: "json",
                    async: false,
                    data: {"data": JSON.stringify(param)},
                    error: function(){//请求失败处理函数
                        alert("error");
                    },
                    success: function(result){
                        location.href = "../../e5workspace/after.do?DocIDs="
                                + result.DocIDs + "&DocLibID=" + result.docLibID
                                + "&UUID=" + result.UUID;
                    }
                });
            });

            // 点击取消按钮
            $('#editCancel').click(function(){
                location.href = "../../e5workspace/after.do?UUID=" + $('#UUID').val();
            });
        }
</script>
</body>
</html>

