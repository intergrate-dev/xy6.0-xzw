/**
 * Created by isaac_gu on 2016/1/14.
 */
(function (window, $, LE) {
    LE.stylesheets["ActiveCommonStyle"] = function () {


        /*单击普通、悬停、点击三个按钮时，通过data-0、data-1、data-2记录状态*/
        var initStyleTabEvent = function () {
            $("#styletab-Ul").find("li").bind("click", function () {
                var _this=$(this);
                LE.stylesheets["ActiveTextStyle"]().buttonReset(_this);
                LE.stylesheets["ActiveBackgroundStyle"]().buttonReset(_this);
                LE.stylesheets["ActiveBolderStyle"]().buttonReset(_this);
            });

        };


        return {
            init: function () {
                initStyleTabEvent();
            },
            run: function (options, doHide, doSlide) {

            },
            destroy: function () {

            }
        };
    };
})(window, jQuery, LE, undefined);
