/**
 * Created by isaac_gu on 2016/1/14.
 */
(function (window, $, LE) {
    LE.stylesheets["Align"] = function () {
        //当前功能面板的对象
        var $PS = $("#textAlignSection");
        var $alignUl = $("#aAlignUl");//获取对齐按钮面板

        /*初始化 对齐按钮的事件，当点击按钮的时候，设置当前对象的 text-align*/
        var initAlignEvent = function () {
            $alignUl.find("li").bind("click", function (e) {
                var _$this = $(this);  //点击的设置对齐方式的按钮
                var $target = LECurrentObject; //获取当前对象
                $target.css("text-align", _$this.attr("data-align"));
                if (LECurrentObject.find(".align_mark").size() > 0) {
                    LECurrentObject.find(".align_mark").css("display", "inline-block");
                    LECurrentObject.find(".align_mark").parent().css("display", "inline-block");
                }
                LEHistory.trigger();
            });
        };
        //获得当前对象的对齐值，并且初始化到按钮上面
        var resetAlignStyle = function () {
            var $target = LECurrentObject; //获取当前对象
            var _align = $target.css("text-align") || "left";
            $alignUl.find("li[data-align=" + _align + "]").addClass("select selected");
        };

        return {
            //onload
            init: function () {
                initAlignEvent();
            },
            run: function (options, doHide) {
                resetAlignStyle();
                //切换组件时，样式设置部分回顶部
                sliderbarToTop();
                LEDisplay.show($PS, doHide);
            },
            destroy: function () {
                //把对齐按钮恢复成原样
                $alignUl.find("li").removeClass("select").removeClass("selected");
                $PS.hide();
            }
        };
    };
})(window, jQuery, LE, undefined);