/**
 * Created by isaac_gu on 2016/1/14.
 */
(function(window, $, LE){
    LE.stylesheets["Width"] = function(){
        var $panel = $("#widthSection");
        var $container = $("#container");
        var $w = $("#containerWidth");

        var initWidth = function(){
            $w.onlyNum().keydown(function(e){
                //当设置高度时，去掉min-height
                LEKey.numInputKeyDown(e, $(this), LECurrentObject);
                $container.attr("data-w", $(this).val());
            });

            //失去焦点的时候，置0
            $w.blur(function(e){
                LEKey.numInputBlur(e, $(this), LECurrentObject);
                $container.attr("data-w", $(this).val());
                LEHistory.trigger();
            });

            $w.focus(function(e){
                LEKey.numInputFocus(e, $(this), LECurrentObject);
                $container.attr("data-w", $(this).val());
            });
        };

        var resetWidth = function(){
            var w =  $container.css("width");
            $w.val($container.attr("data-w"));
        };
        return {
            init: function(){
                initWidth();
            },
            run: function(options, doHide){
                resetWidth();
                LEDisplay.show($panel, doHide);
            },
            destroy: function(){
                //console.info("PageSetting destroy")
                $panel.hide();
            }
        };
    };
})(window, jQuery, LE, undefined);

