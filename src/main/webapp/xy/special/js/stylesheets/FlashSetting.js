/**
 * Created by isaac_gu on 2016/1/14.
 */
(function (window, $, LE) {
    LE.stylesheets["Flash"] = function () {
        //当前功能面板的对象
        var $panelF = $("#flashSection");
        var $target = null;
        var $addFlashBtn = $("#addFlash");
        var $resetFlashBtn = $("#resetFlash");
        var $flashUrlText = $("#flashUrlText");
        var $flashWidth = $("#fWidth");
        var $flashHeight = $("#fHeight");
        var $whFlash = $("#fWidth,#fHeight");
        var $linkFlashDialog = $("#linkFlashDialog");

        /*
         Flash地址输入框的显示隐藏
         */
        var initAddFlashEvent = function () {
            $addFlashBtn.click(function () {
                $linkFlashDialog.modal("show");
            });
            $resetFlashBtn.click(function () {
                $linkFlashDialog.modal("show");
                $flashUrlText.val($(this).attr("data-url"))
            });
            $("#flashDivClose").click(function () {
                $linkFlashDialog.modal("hide");
            });
            $("#flashUrlBtn").click(function () {
                var imgStr='<div data-imgsrc="export/images/sliderPanel/media_flash.png" data-mediasrc="" class="le-flash_media"></div>';
                LECurrentObject.html(imgStr);
                LECurrentObject.find(".le-flash_media").attr("data-mediasrc", $flashUrlText.val());
                LECurrentObject.find(".le-flash_media").css({"width": $flashWidth.val(), "height": $flashHeight.val()});
                LECurrentObject.find(".le-flash_media").css({"background":"#000 url(export/images/sliderPanel/media_flash.png) no-repeat center","background-size":"60px 60px"});
                $linkFlashDialog.modal("hide");
                $resetFlashBtn.attr("data-url", $flashUrlText.val());
                $addFlashBtn.hide();
                $resetFlashBtn.show();
            });
            $flashUrlText.keypress(function (e) {
                if (e.keyCode == 13) {
                    var imgStr='<div data-imgsrc="export/images/sliderPanel/media_flash.png" data-mediasrc="" class="le-flash_media"></div>';
                    LECurrentObject.html(imgStr);
                    LECurrentObject.find(".le-flash_media").attr("data-mediasrc", $flashUrlText.val());
                    LECurrentObject.find(".le-flash_media").css({"width": $flashWidth.val(), "height": $flashHeight.val()});
                    LECurrentObject.find(".le-flash_media").css({"background":"#000 url(export/images/sliderPanel/media_flash.png) no-repeat center","background-size":"60px 60px"});
                    $linkFlashDialog.modal("hide");
                    $resetFlashBtn.attr("data-url", $flashUrlText.val());
                    $addFlashBtn.hide();
                    $resetFlashBtn.show();
                }
            });
        };

        /*
         宽高输入框调节Flash大小
         */
        var initFlashWHEvent = function () {
            $whFlash.keydown(function (e) {
                LEKey.numInputKeyDown(e, $(this), LECurrentObject.find('div'));
            });
            $whFlash.blur(function (e) {
                LEKey.numInputBlur(e, $(this), LECurrentObject.find('div'));
                LEHistory.trigger();
            });
            $whFlash.focus(function (e) {
                LEKey.numInputFocus(e, $(this), LECurrentObject.find('div'));
            });
        };

        /*
         初始化Flash大小调节框的值
         */
        var resetFlashLabels = function () {
            var resetWidth = LECurrentObject.find('div').css("width");
            var resetHeight = LECurrentObject.find('div').css("height");
            if (resetWidth) {
                $flashWidth.val(resetWidth);
                $flashHeight.val(resetHeight);
            } else {
                $flashWidth.val("100%");
                $flashHeight.val("300px");
            }


        };

        /*
         初始化添加Flash按钮、修改Flash按钮的显示状态
         */
        var resetFlashBtns = function () {
            _$obj = LECurrentObject;
            if ($.trim(_$obj.find("div").attr("data-mediasrc")) != "") {
                $addFlashBtn.hide();
                $resetFlashBtn.show();
            } else {
                $addFlashBtn.show();
                $resetFlashBtn.hide();
            }
        };

        return {
            init: function () {
                initAddFlashEvent();
                initFlashWHEvent();
            },
            run: function (options, doHide) {
                resetFlashBtns();
                resetFlashLabels();
                //切换组件时，样式设置部分回顶部
                sliderbarToTop();

                LEDisplay.show($panelF, doHide);
            },
            destroy: function () {
                $panelF.hide();
            }
        };
    };
})(window, jQuery, LE, undefined);