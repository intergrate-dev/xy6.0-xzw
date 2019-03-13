/**
 * Created by isaac_gu on 2016/1/14.文件名带Old及XXX的为不用的js
 */
(function (window, $, LE) {
    LE.stylesheets["PageSetting"] = function () {
        var $PS = $("#pageSettingDiv");
        return {
            init: function () {
                //console.info("PageSetting init")
            },
            run: function (options, doHide) {
                //console.info("PageSetting run")
                LEDisplay.show($PS, doHide);
            },
            destroy: function () {
                //console.info("PageSetting destroy")
                $PS.hide();

            }
        };
    };
})(window, jQuery, LE, undefined);