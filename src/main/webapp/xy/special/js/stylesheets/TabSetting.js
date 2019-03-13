/**
 * Created by isaac_gu on 2016/1/14.
 */
(function (window, $, LE) {
    LE.stylesheets["TabSetting"] = function () {
        //var $panel = $("#tabSettingSection");
        var $panel = $("#panel-III-tabWH");

        var $tabWH = $panel.find(".tabWH");

        var initWHEvent = function () {
            $tabWH.onlyNum().keydown(function (e) {
                LEKey.numInputKeyDown(e, $(this), LECurrentObject.find(".ui-tabs-anchor").find("div"));
            });
            //失去焦点的时候，置0
            $tabWH.blur(function (e) {
                LEKey.numInputBlur(e, $(this), LECurrentObject.find(".ui-tabs-anchor").find("div"));
                LEHistory.trigger();
            });

            $tabWH.focus(function (e) {
                LEKey.numInputFocus(e, $(this), LECurrentObject.find(".ui-tabs-anchor").find("div"));
            });


        };
        var resetTabWH = function () {
            $tabWH.each(function () {
                var $this = $(this);
                var $target = LECurrentObject.find("[role='tab']").not(".active").find(".ui-tabs-anchor").find("div");
                var _ref = $this.data("ref");
                $this.val(parseInt($target.css(_ref)) + "px");
            });
        };
        return {
            init: function () {
                initWHEvent();
            },
            run: function (options, doHide) {
                //console.info("PageSetting run")
                resetTabWH();
                //切换组件时，样式设置部分回顶部
                sliderbarToTop();
                LEDisplay.show($panel, doHide);
            },
            destroy: function () {
                //console.info("PageSetting destroy")
                $panel.hide();
            }
        };
    };
})(window, jQuery, LE, undefined);