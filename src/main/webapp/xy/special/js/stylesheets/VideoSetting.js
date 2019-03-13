/**
 * Created by isaac_gu on 2016/1/14.
 */
(function(window, $, LE){
    LE.stylesheets["Video"] = function(){
        //当前功能面板的对象
        var $panel = $("#videoSection");
        var $target = null;
        var $addVedioBtn = $("#addVideo");
        var $resetVideoBtn = $("#resetVideo");
        var $videoUrlText = $("#videoUrlText");
        var $videoWidth = $("#vWidth");
        var $videoHeight = $("#vHeight");
        var $whVideo = $("#vWidth,#vHeight");
        var $linkDialog = $("#linkDialog");

        /*
         视频地址输入框的显示隐藏
         */
        var initAddVedioEvent = function(){
            $addVedioBtn.click(function(){
                $linkDialog.modal("show");
            });
            $resetVideoBtn.click(function(){
                $linkDialog.modal("show");
                var oldSrc=LECurrentObject.find("div").attr("data-mediasrc");
                //$videoUrlText.val($(this).attr("data-url"))
                $videoUrlText.val(oldSrc)
            });
            $("#videoDivClose").click(function(){
                $linkDialog.modal("hide");
            });
            $("#videoUrlBtn").click(function(){
                var imgStr = '<div data-imgsrc="export/images/sliderPanel/media_video.png" data-mediasrc="" class="le-video_media"></div>';
                LECurrentObject.html(imgStr);
                LECurrentObject.find(".le-video_media").attr("data-mediasrc", $videoUrlText.val());
                LECurrentObject.find(".le-video_media").css({"width": $videoWidth.val(), "height": $videoHeight.val()});
                LECurrentObject.find(".le-video_media").css({
                    "background": "#000 url(export/images/sliderPanel/media_video.png) no-repeat center",
                    "background-size": "60px 60px"
                });
                $linkDialog.modal("hide");
               // $resetVideoBtn.attr("data-url", $videoUrlText.val());
                $addVedioBtn.hide();
                $resetVideoBtn.show();
            });
            $videoUrlText.keypress(function(e){
                if(e.keyCode == 13){
                    var imgStr = '<div data-imgsrc="export/images/sliderPanel/media_video.png" data-mediasrc="" class="le-video_media"></div>';
                    LECurrentObject.html(imgStr);
                    LECurrentObject.find(".le-video_media").attr("data-mediasrc", $videoUrlText.val());
                    LECurrentObject.find(".le-video_media").css({
                        "width": $videoWidth.val(),
                        "height": $videoHeight.val()
                    });
                    LECurrentObject.find(".le-video_media").css({
                        "background": "#000 url(export/images/sliderPanel/media_video.png) no-repeat center",
                        "background-size": "60px 60px"
                    });
                    $linkDialog.modal("hide");
                   // $resetVideoBtn.attr("data-url", $videoUrlText.val());
                    $addVedioBtn.hide();
                    $resetVideoBtn.show();
                }
            });

            $("#addVideoLib").click(function(e){
                LEDialog.toggleDialog("../../xy/GroupSelect.do?type=1&siteID="+ special.siteID, function(videoInfo){
                  //  alert(videoInfo.plugin + "?src=" + videoInfo.url);
                    var mediasrc=videoInfo.plugin + "?src=" + videoInfo.url;
                    var imgStr = '<div data-imgsrc="export/images/sliderPanel/media_video.png" data-mediasrc="'+mediasrc+'" class="le-video_media"></div>';
                    LECurrentObject.html(imgStr);
                    LECurrentObject.find(".le-video_media").attr("data-mediasrc", mediasrc);
                    LECurrentObject.find(".le-video_media").css({"width": $videoWidth.val(), "height": $videoHeight.val()});
                    LECurrentObject.find(".le-video_media").css({
                        "background": "#000 url(export/images/sliderPanel/media_video.png) no-repeat center",
                        "background-size": "60px 60px"
                    });
                    $addVedioBtn.hide();
                    $resetVideoBtn.show();
                });
            });
        };

        /*
         宽高输入框调节视频大小
         */
        var initVideoWHEvent = function(){
            $whVideo.keydown(function(e){
                LEKey.numInputKeyDown(e, $(this), LECurrentObject.find('div'));
            });
            $whVideo.blur(function(e){
                LEKey.numInputBlur(e, $(this), LECurrentObject.find('div'));
                LEHistory.trigger();
            });
            $whVideo.focus(function(e){
                LEKey.numInputFocus(e, $(this), LECurrentObject.find('div'));
            });
        };

        /*
         初始化视频大小调节框的值
         */
        var resetVideoLabels = function(){
            var resetWidth = LECurrentObject.find('div').css("width");
            var resetHeight = LECurrentObject.find('div').css("height");
            if(resetWidth){
                $videoWidth.val(resetWidth);
                $videoHeight.val(resetHeight);
            } else{
                $videoWidth.val("100%");
                $videoHeight.val("300px");
            }


        };

        /*
         初始化添加视频按钮、修改视频按钮的显示状态
         */
        var resetBtns = function(){
            _$obj = LECurrentObject;
            if($.trim(_$obj.find("div").attr("data-mediasrc")) != ""){
                $addVedioBtn.hide();
                $resetVideoBtn.show();
            } else{
                $addVedioBtn.show();
                $resetVideoBtn.hide();
            }
        };

        return {
            init: function(){
                initAddVedioEvent();
                initVideoWHEvent();
            },
            run: function(options, doHide){
                resetBtns();
                resetVideoLabels();
                //切换组件时，样式设置部分回顶部
                sliderbarToTop();

                LEDisplay.show($panel, doHide);
            },
            destroy: function(){
                $panel.hide();
            }
        };
    };
})(window, jQuery, LE, undefined);


var ImageToMedia = function(selector){
    jQuery(selector).each(function(){
        var _this = jQuery(this);
        var tagname = _this[0].tagName;
        var _mediaSrc = _this.attr("data-mediasrc");
        var _imgSrc = _this.attr("data-imgsrc");
        var _width = _this.css("width");
        var _height = _this.css("height");
        var _textAlign = _this.parent().css("text-align");
        if(selector.indexOf("flash") != -1){
            var mediaStr = '<embed src="' + _mediaSrc + '" data-imgsrc="' + _imgSrc + '" class="le-flash_media" pluginspage="http://www.macromedia.com/go/getflashplayer" wmode="transparent" allowscriptaccess="never" allownetworking="internal" allowfullscreen="true"></embed>';
            // var mediaStr='<embed src="'+_mediaSrc+'" data-imgsrc="'+_imgSrc+'" class="le-flash_media" type="application/x-shockwave-flash" pluginspage="http://www.macromedia.com/go/getflashplayer" wmode="transparent" quality="high" allowScriptAccess="always" allownetworking="internal" allowfullscreen="true"></embed>';
        } else if(selector.indexOf("video") != -1){
            var mediaStr = '<embed src="' + _mediaSrc + '" data-imgsrc="' + _imgSrc + '" class="le-video_media" pluginspage="http://www.macromedia.com/go/getflashplayer" wmode="transparent" allowscriptaccess="never" allownetworking="internal" allowfullscreen="true"></embed>';
            // var mediaStr='<video src="'+_mediaSrc+'" data-imgsrc="'+_imgSrc+'"  class="le-video_media" controls="controls" autoplay="autoplay">您的浏览器不支持video标签，请使用google浏览器浏览</video>';
        } else if(selector.indexOf("audio") != -1){
            var mediaStr = '<embed src="' + _mediaSrc + '" data-imgsrc="' + _imgSrc + '" class="le-audio_media" pluginspage="http://www.macromedia.com/go/getflashplayer" wmode="transparent" play="true" loop="false" allowscriptaccess="never" allownetworking="internal" allowfullscreen="true"></embed>';
            // var mediaStr='<audio src="'+_mediaSrc+'" data-imgsrc="'+_imgSrc+'"  class="le-audio_media" controls="controls" autoplay="autoplay">您的浏览器不支持audio标签，请使用google浏览器浏览</audio>';
        }

        _this.after(mediaStr);
        _this.next().css({"width": _width, "height": _height, "text-align": _textAlign});
        _this.remove()
    })

};

var MediaToImage = function(selector){
    jQuery(selector).each(function(){
        var _this = jQuery(this);
        var _imgSrc = _this.attr("data-imgsrc");
        var _src = _this.attr("src");
        var _width = _this.css("width");
        var _height = _this.css("height");
        var _textAlign = _this.parent().css("text-align");
        if(selector.indexOf("flash") != -1){
            var class_Name = "le-flash_media";
        } else if(selector.indexOf("video") != -1){
            var class_Name = "le-video_media";
        } else if(selector.indexOf("audio") != -1){
            var class_Name = "le-audio_media";
        }
        var imgStr = '<div style="background:#000 url(' + _imgSrc + ') no-repeat center ;background-size:60px 60px" data-imgsrc="' + _imgSrc + '" data-mediasrc="' + _src + '" class="' + class_Name + '"></div>';
        _this.after(imgStr);
        _this.next().css({"width": _width, "height": _height, "text-align": _textAlign});
        _this.remove()
    })
};

var videoClose = function(docLibID, docID){
    if(!docID) return;

    jQuery.ajax({
        url: "../video/Info.do", async: false,
        data: {
            "docID": docID,
            "docLibID": docLibID
        },
        error: function(XMLHttpRequest, textStatus, errorThrown){
            alert(errorThrown + ':' + textStatus);
        },
        success: function(data){
           // debugger;
            data = data.split(",");
            var videoID = (docLibID + "," + docID);

            var videoInfo = {
                plugin: data[0],
                url: data[1],
                urlApp: data[2],
                videoID: videoID
            };
            if(!videoInfo.url) videoInfo.url = videoInfo.urlApp;
            if(!videoInfo.urlApp) videoInfo.urlApp = videoInfo.url;
            LEDialog.dialogConfirm(videoInfo);
        }
    });
};

var videoCancel = function(){
    LEDialog.closeDialog();
};