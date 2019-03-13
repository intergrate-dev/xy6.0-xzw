var oldKey,oldUpload,oldPath,imgLiList;
var xy_subject = {
    init: function(){
		if ($("#type").val() != "2") {
			$("#SPAN_a_answer").parent().parent().hide();
			$("#a_answer").removeClass("custform-textarea validate[required]");
		}
		
        $("#a_region").next().attr("readonly", true);
        //$("#a_askTo").attr("readonly", true);
        //$("#SYS_TOPIC").attr("readonly", true);
        //$("#a_content").attr("readonly", true);
        //$("#a_realName").attr("readonly", true);
        //$("#a_phone").attr("readonly", true);
    },
    upload: null,
    loadVideo:function() {
        var theURL = "./TipoffVideos.do?DocLibID=" + $("#DocLibID").val() + "&DocIDs=" + $("#DocID").val();
        $.ajax({url: theURL, async:true,
            success: function(datas) {
                video_form.setVideoInfo(datas);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                alert(errorThrown + ':' + textStatus);  // 错误处理
            }
        });
    },
    initPicPlugin: function(){
        var param = {
            uploadUrl: '../../xy/upload/uploadFileF.do',
            deleteUrl: "../../xy/upload/deletePreviewThumb.do",
            rootUrl:'../../',
            showContent: false,
            showSort: true,
            autoCommit: true,
            showEditor: true,
            showTitleImage: true,
            rootUrl: '../../'
        };

        var _attachment = $("#a_attachments").val();
        if(_attachment && $.trim(_attachment) != ""){
            var _json = JSON.parse(_attachment);
            var list = _json.pics;
            if(list && list instanceof Array){
                var initialPreview = [];
                var initialPreviewConfig = [];
                for(var i = 0, li = null; li = list[i++];){
                    initialPreview.push("../../xy/image.do?path=" + li  + ".0.jpg");
                    initialPreviewConfig.push({
                        url: "../../xy/upload/deletePreviewThumb.do",
                        imagePath: li,
                        key: i
                    });
                }
                param.initialPreview = initialPreview;
                param.initialPreviewConfig = initialPreviewConfig;

            }
        }
        //点击修改
        param.modifyPicture = function(key, path, _upload,_list){
            oldKey=key;
            oldPath=path;
            oldUpload=_upload;
            imgLiList=_list;
            xy_subject.imageEditor();
        };
        param.completed = function(){
            if(xy_subject.indexed){
                xy_subject.upload.$selector.closest(".file-input").find(".file-preview-thumbnails").children(".file-preview-frame").find("button.kv-file-remove[data-key='" +
                    xy_subject.indexed +
                    "']").siblings("input:radio").click();
            }
        }
        xy_subject.upload = new Upload("#uploadInput", param);
        //初始化标题图的设置
        setTimeout(function(){
            var _attachment = $("#a_attachments").val();
            var _json;
            //获得json对象
            if(_attachment && $.trim(_attachment) != ""){
                _json = JSON.parse(_attachment);
            }
            if(_json.indexed && $.trim(_json.indexed)){
                var list = xy_subject.upload.getDataList();
                if(list && list instanceof Array && list.length > 0){
                    for(var i = 0, li = null; li = list[i++];){
                        if(li.imagePath == _json.indexed){
                            var key = li.key;
                            xy_subject.indexed = key;
                            xy_subject.upload.$selector.closest(".file-input").find(".file-preview-thumbnails").children(".file-preview-frame").find("button.kv-file-remove[data-key='" +
                                key +
                                "']").siblings("input:radio").click();

                        }
                    }
                }
            }
        }, 350);

        $("#livePicAreaDiv").on("click",".kv-radio", function(){
            xy_subject.indexed = $(this).siblings(".kv-file-remove").attr("data-key");
        });

    },
    imageEditor:function(event){
        var url = "../../xy/script/picupload/imageeditor.jsp";
        var pos = {left : "100px",top : "50px",width : "1000px",height : "500px"};
        xy_subject.dialog = e5.dialog({type : "iframe", value : url}, {
            showTitle : true,
            title: "图片编辑",
            width : "1000px",
            height : "500px",
            pos : pos,
            resizable : false
        });
        xy_subject.dialog.show();
    },
    //选择窗口：取消后
    groupSelectCancel : function() {
        if(xy_subject.dialog) {
            xy_subject.dialog.close();
        }
    },
    save: function(){
        var _indexed = $('input:radio[name="indexed"]:checked').siblings(".kv-file-remove").attr("data-key");
        var list = xy_subject.upload.getDataList();
        var pics = [];
        var indexed = null;
        for(var i = 0, li = null; li = list[i++];){
            if((li.key + "") == (_indexed + "")){
                indexed = li.imagePath;
            }
            pics.push(li.imagePath);
        }

        var _attachment = $("#a_attachments").val();
        var _json;
        //获得json对象
        if(_attachment && $.trim(_attachment) != ""){
            _json = JSON.parse(_attachment);
        } else{
            _json = {pics: [], videos: []}
        }
        _json.pics = pics;
        if(indexed && $.trim(indexed) != ""){
            _json.indexed = indexed;
        }
        _json.videos=getVideos();
        $("#a_attachments").val(JSON.stringify(_json));
        $.ajax({
            url: "../../xy/pic/checkExtractIsFinished.do",
            dataType: "json",
            data: {
                paths: JSON.stringify(_json.pics)
            },
            async: false,
            success: function(json){
                if(json){
                    if(json.code == 0){
                        if(json.isFinished){
                            if($("#form").validationEngine("validate")){
                                // 验证提示
                                $("#form").validationEngine("updatePromptsPosition");
                                return false;
                            }

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
    }
};
$(function(){
    xy_subject.init();
    xy_subject.initPicPlugin();
    xy_subject.loadVideo();
    $("#btnFormSave").unbind();
    $("#btnFormSave").click(function(){
        xy_subject.save();
    });
});
//填充单层分类下拉框的Option。读话题分类时加siteID
e5_form.dynamicReader._readCatUrl = function(catType){
    var dataUrl = "xy/Cats.do?catType=" + catType + "&siteID=" + e5_form.getParam("siteID");
    dataUrl = e5_form.dealUrl(dataUrl);
    return dataUrl;
};

//图片编辑之后回调
function resetImageMsg(key,path,showPath,_upload,imageList){
    _upload.doModifyPicture(key, path, showPath,imageList);
}
function groupSelectCancel(){
    xy_subject.groupSelectCancel();
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


