<!--created by hudie on 2018/4/18-->
<%@page pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>头部背景图</title>
  <script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
  <script type="text/javascript" src="../../xy/script/picupload/upload_api.js"></script>
</head>
<body>
<div class="container">
  <div class="details-div">
    <div class="inform-text content-left">上传</div>
    <div class="content-div">
      <div style="display: none;">
        <input id="uploadInput" type="file" name="uploadInput">
      </div>
      <div id="bgImageDiv" onclick="uploadImg()" title="插入图片">
        <div class="glyphiconDiv">
          <p class="glyphicon-plus">+</p>
        </div>
        <img src="" id="bgImage" style="display: none;" />
      </div>
    </div>
  </div>
  <div class="buttons">
    <button id="saveSubmit" class="submit-bnt">保 存</button>
    <button id="toCancel" onclick="">取 消</button>
  </div>
</div>
<input type="hidden" id="UUID" value="${UUID}">
<input type="hidden" id="DocIDs" value="${docID}">
<input type="hidden" id="picUrl" value="${picUrl}">
<script type="text/javascript">
    var setbgImg = {
        init: function () {
            setbgImg.initPicPlugin();
            //图片回显
            setbgImg.showPic();
        },
        initPicPlugin: function () {
            var param = {
                uploadUrl: '../../xy/upload/uploadFileF.do',
                deleteUrl: "../../xy/upload/deletePreviewThumb.do",
                showContent: false,
                autoCommit: true,
                showEditor: false,
                maxFileCount: 1,
                allowedFileExtensions: [ 'jpg', 'jpeg', 'gif', 'bmp', 'png' ],
                showRemove: false,
                showUpload: false,
                showSort: true,
                rootUrl: '../../'
            };
            param.fileUploaded = function (e, data) {
                $("#bgImage").attr("src", "../../xy/image.do?path=" + data.path);
                $("#picUrl").val(data.path);
                //隐藏添加 + 图标
                $("#bgImageDiv").attr('title', '修改图片');
                $("#bgImageDiv .glyphiconDiv").hide();
                $("#bgImage").show();
            };

            var _imgsrc = $("#bgImage").attr("src");
            if (_imgsrc && $.trim(_imgsrc) != "") {
                var initialPreview = [];
                var initialPreviewConfig = [];
                if ($.trim(_imgsrc).indexOf("http") == 0) {
                    initialPreview.push(_imgsrc);
                } else {
                    initialPreview.push("../../xy/image.do?path=" + _imgsrc + ".0.jpg");
                }
                initialPreviewConfig.push({
                    url: "../../xy/upload/deletePreviewThumb.do",
                    imagePath: _imgsrc,
                    key: 0
                });
                param.initialPreview = initialPreview;
                param.initialPreviewConfig = initialPreviewConfig;
            }
            setbgImg.upload = new Upload("#uploadInput", param);
        },
        showPic: function () {
            var imgSrc = $('#picUrl').val();
            //如果有图片则要回显
            if(imgSrc){
                $("#bgImage").attr("src", "../../xy/image.do?path=" + imgSrc);
                //隐藏添加 + 图标
                $("#bgImageDiv").attr('title', '修改图片');
                $("#bgImageDiv .glyphiconDiv").hide();
                $("#bgImage").show();
            }
        },
        save: function () {
            var _params = setbgImg.getData();
            if (!_params) {
                return;
            }
            setbgImg.doSave(_params);
        },
        cancel: function () {
            var UUID = $("#UUID").val();
            if (UUID != '' && UUID != undefined) {
                var dataUrl = "../../e5workspace/after.do?UUID=" + UUID;
                window.location.href = dataUrl;
            }
        },
        //收集数据
        getData: function () {
            var param = {};
            param.DocIDs = $("#DocIDs").val();
            param.picUrl = $("#picUrl").val();
            param.UUID = $("#UUID").val();
            return param;
        },

        doSave: function (params) {
            $.ajax({
                url: "../../xy/mobileos/saveBackGround.do",
                type: 'post',
                data: params,
                success: function (res) {
                    if (res && res == 'ok') {
                        var UUID = $("#UUID").val();
                        if (UUID != '' && UUID != undefined) {
                            var dataUrl = "../../e5workspace/after.do?UUID=" + UUID;
                            window.location.href = dataUrl;
                        }
                    }
                },
            });
        }

    }
    //上传图片
    function uploadImg() {
        document.getElementById("uploadInput").click();
    }

    $(function () {
        setbgImg.init();
        //确定
        $("#saveSubmit").on('click', setbgImg.save);
        //取消
        $("#toCancel").on('click', setbgImg.cancel);
    });
</script>
<style>
  .container {
    padding: 20px 30px;
  }

  .details-div {
    min-height: 35px;
    display: flex;
    align-items: center;
  }

  .inform-text {
    width: 100px;
    color: #666666;
    font-size: 14px;
    margin-right: 5px;
  }

  #bgImageDiv {
    width: 250px;
    height: 150px;
    border: 1px solid #cccccc;
    border-radius: 4px;
    cursor: pointer;
    position: relative;
  }

  .glyphiconDiv {
    background: transparent; /*背景透明*/
    width: 100%;
    height: 100%;
    position: absolute;
    top: 0;
    left: 0;
    z-index: 2;
  }

  .glyphicon-plus {
    color: #cccccc;
    font-size: 50px;
    text-align: center;
    margin-top: 15px;
  }

  #bgImageDiv:hover {
    border: 1px dashed #8e8e8e;
  }

  #bgImageDiv:hover .glyphiconDiv {
    background-color: rgba(0, 0, 0, 0.1);
  }

  #bgImageDiv:hover .glyphiconDiv {
    display: inline-block !important;
  }

  #bgImageDiv:hover .glyphicon-plus {
    color: #8e8e8e;
  }

  #bgImage {
    width: 100%;
    height: 100%;
  }

  .buttons {
    text-align: center;
    position: fixed;
    top: 80%;
    left: calc(50% - 124px);
  }

  .buttons button {
    width: 97px;
    height: 35px;
    border-radius: 3px;
    color: #000000;
    background: #b1b1b1;
    border: none;
    font-size: 14px;
    cursor: pointer;
  }

  .buttons button.submit-bnt {
    color: #fff;
    background: #1bb8fa;
  }

  .buttons button + button {
    margin-left: 50px;
  }

  #kvFileinputModal {
    display: none;
  }
</style>
</body>
</html>