/**
 * Created by isaac_gu on 2016/1/14. 文件名带Old及XXX的为不用的js
 */
(function (window, $, LE) {
    LE.stylesheets["Audio"] = function () {
        //当前功能面板的对象
        var $panelA = $("#audioSection");
        var $target = null;
        var $addAudioBtn = $("#addAudio");
        var $resetAudioBtn = $("#resetAudio");
        var $audioUrlText = $("#audioUrlText");
        var $audioWidth = $("#aWidth");
        var $audioHeight = $("#aHeight");
        var $whAudio = $("#aWidth,#aHeight");
        var $linkAudioDialog = $("#linkAudioDialog");

        /*
         音频地址输入框的显示隐藏
         */
        var initAddAudioEvent = function () {
            $addAudioBtn.click(function () {
                $linkAudioDialog.modal("show");
            });
            $resetAudioBtn.click(function () {
                $linkAudioDialog.modal("show");
                $audioUrlText.val($(this).attr("data-url"))
            });
            $("#audioDivClose").click(function () {
                $linkAudioDialog.modal("hide");
            });
            $("#audioUrlBtn").click(function () {
                LECurrentObject.html(LE.options["Audio"].audioHtml);
                LECurrentObject.find(".le-audio_media").attr("src", $audioUrlText.val());
                LECurrentObject.find(".le-audio_media").css({"width": $audioWidth.val(), "height": $audioHeight.val()});
                $linkAudioDialog.modal("hide");
                $resetAudioBtn.attr("data-url", $audioUrlText.val());
                $addAudioBtn.hide();
                $resetAudioBtn.show();
            });
            $audioUrlText.keypress(function (e) {
                if (e.keyCode == 13) {
                    LECurrentObject.html(LE.options["Audio"].audioHtml);
                    LECurrentObject.find(".le-audio_media").attr("src", $audioUrlText.val());
                    LECurrentObject.find(".le-audio_media").css({"width": $audioWidth.val(), "height": $audioHeight.val()});
                    $linkAudioDialog.modal("hide");
                    $resetAudioBtn.attr("data-url", $audioUrlText.val());
                    $addAudioBtn.hide();
                    $resetAudioBtn.show();
                }
            });
        };

        /*
         宽高输入框调节音频大小
         */
        var initAudioWHEvent = function () {
            $whAudio.keydown(function (e) {
                LEKey.numInputKeyDown(e, $(this), LECurrentObject.find('embed'));
            });
            $whAudio.blur(function (e) {
                LEKey.numInputBlur(e, $(this), LECurrentObject.find('embed'));
                LEHistory.trigger();
            });
            $whAudio.focus(function (e) {
                LEKey.numInputFocus(e, $(this), LECurrentObject.find('embed'));
            });
        };

        /*
         初始化音频大小调节框的值
         */
        var resetAudioLabels = function () {
            var resetWidth = LECurrentObject.find('embed').css("width");
            var resetHeight = LECurrentObject.find('embed').css("height");
            if (resetWidth) {
                $audioWidth.val(resetWidth);
                $audioHeight.val(resetHeight);
            } else {
                $audioWidth.val("100%");
                $audioHeight.val("300px");
            }


        };

        /*
         初始化添加音频按钮、修改音频按钮的显示状态
         */
        var resetAudioBtns = function () {
            _$obj = LECurrentObject;
            if ($.trim(_$obj.find("embed").attr("src")) != "") {
                $addAudioBtn.hide();
                $resetAudioBtn.show();
            } else {
                $addAudioBtn.show();
                $resetAudioBtn.hide();
            }
        };

        return {
            init: function () {
                initAddAudioEvent();
                initAudioWHEvent();
            },
            run: function (options, doHide) {
                resetAudioBtns();
                resetAudioLabels();
                //切换组件时，样式设置部分回顶部
                sliderbarToTop();

                LEDisplay.show($panelA, doHide);
            },
            destroy: function () {
                $panelA.hide();
            }
        };
    };
})(window, jQuery, LE, undefined);