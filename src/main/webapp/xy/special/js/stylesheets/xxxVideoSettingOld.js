/**
 * Created by isaac_gu on 2016/1/14.文件名带Old及XXX的为不用的js
 */
(function (window, $, LE) {
    LE.stylesheets["Video"] = function () {
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
        var initAddVedioEvent = function () {
            $addVedioBtn.click(function () {
                $linkDialog.modal("show");
                $videoUrlText.val("http://wear.techbrood.com/assets/love1314.mp4")
            });
            $resetVideoBtn.click(function () {
                $linkDialog.modal("show");
                $videoUrlText.val($(this).attr("data-url"))
            });
            $("#videoDivClose").click(function () {
                $linkDialog.modal("hide");
            });
            $("#videoUrlBtn").click(function () {
                LECurrentObject.html(LE.options["Video"].videoHtml);
                LECurrentObject.find(".le-video_media").attr("src", $videoUrlText.val());
                LECurrentObject.find(".le-video_media").css({"width": $videoWidth.val(), "height": $videoHeight.val()});
                $linkDialog.modal("hide");
                $resetVideoBtn.attr("data-url", $videoUrlText.val());
                $addVedioBtn.hide();
                $resetVideoBtn.show();
            });
            $videoUrlText.keypress(function (e) {
                if (e.keyCode == 13) {
                    LECurrentObject.html(LE.options["Video"].videoHtml);
                    LECurrentObject.find(".le-video_media").attr("src", $videoUrlText.val());
                    LECurrentObject.find(".le-video_media").css({"width": $videoWidth.val(), "height": $videoHeight.val()});
                    $linkDialog.modal("hide");
                    $resetVideoBtn.attr("data-url", $videoUrlText.val());
                    $addVedioBtn.hide();
                    $resetVideoBtn.show();
                }
            });
        };

        /*
         宽高输入框调节视频大小
         */
        var initVideoWHEvent = function () {
            $whVideo.keydown(function (e) {
                LEKey.numInputKeyDown(e, $(this), LECurrentObject.find('embed'));
            });
            $whVideo.blur(function (e) {
                LEKey.numInputBlur(e, $(this), LECurrentObject.find('embed'));
                LEHistory.trigger();
            });
            $whVideo.focus(function (e) {
                LEKey.numInputFocus(e, $(this), LECurrentObject.find('embed'));
            });
        };

        /*
         初始化视频大小调节框的值
         */
        var resetVideoLabels = function () {
            var resetWidth = LECurrentObject.find('embed').css("width");
            var resetHeight = LECurrentObject.find('embed').css("height");
            if (resetWidth) {
                $videoWidth.val(resetWidth);
                $videoHeight.val(resetHeight);
            } else {
                $videoWidth.val("100%");
                $videoHeight.val("300px");
            }


        };

        /*
         初始化添加视频按钮、修改视频按钮的显示状态
         */
        var resetBtns = function () {
            _$obj = LECurrentObject;
            if ($.trim(_$obj.find("embed").attr("src")) != "") {
                $addVedioBtn.hide();
                $resetVideoBtn.show();
            } else {
                $addVedioBtn.show();
                $resetVideoBtn.hide();
            }
        };

        return {
            init: function () {
                initAddVedioEvent();
                initVideoWHEvent();
            },
            run: function (options, doHide) {
                resetBtns();
                resetVideoLabels();
                //切换组件时，样式设置部分回顶部
                sliderbarToTop();

                LEDisplay.show($panel, doHide);
            },
            destroy: function () {
                $panel.hide();
            }
        };
    };
})(window, jQuery, LE, undefined);