/**
 * Created by isaac_gu on 2016/1/14. 文件名带Old及XXX的为不用的js
 */
(function (window, $, LE) {
    LE.stylesheets["Bolder_Common"] = function () {
        var $panel = $("#bolderSection");
        var $colorPicker = $("#bolderColorPick");
        var $bolderWidth = $("#bBolderWidth");
        var $bolderRadius = $("#bBolderRadius");
        var $bWidth = $("#bWidth");
        var $bolderStyleUl = $("#bolderStyleUl");

        var initColorPicker = function () {
            $colorPicker.spectrum(
                LEColorPicker.getOptions(function (tinycolor) {
                    //如果tinycolor无效时，设置透明
                    var _c = tinycolor ? tinycolor.toHexString() : "";
                    var $target = getCurrentObject();
                    $target.css("border-color", _c);
                    $("#bBolderColor").val(_c);
                    LEHistory.trigger();
                })
            );
        };
        var initBolderWidth = function () {
            $bolderWidth.onlyNum().keydown(function (e) {
                var $target = getCurrentObject();
                LEKey.numInputKeyDown(e, $(this), $target);
            });
            //失去焦点的时候，置0
            $bolderWidth.blur(function (e) {
                var $target = getCurrentObject();
                LEKey.numInputBlur(e, $(this), $target);
                LEHistory.trigger();
            });

            $bolderWidth.focus(function (e) {
                var $target = getCurrentObject();
                LEKey.numInputFocus(e, $(this), $target);
            });
        };
        var initBolderStyle = function () {
            $bolderStyleUl.find("li").click(function () {
                var _ref = $(this).data("ref");
                var $target = getCurrentObject();
                $target.css("borderStyle", _ref);
                $target.attr("data-set", "true");
                //resetBolderWidth();
                LEHistory.trigger();
            });
        };
        var initBolderRadius = function () {
            $bolderRadius.onlyNum().keydown(function (e) {
                var $target = getCurrentObject();
                LEKey.numInputKeyDown(e, $(this), $target);
            });
            //失去焦点的时候，置0
            $bolderRadius.blur(function (e) {
                var $target = getCurrentObject();
                LEKey.numInputBlur(e, $(this), $target);
                LEHistory.trigger();
            });

            $bolderRadius.focus(function (e) {
                var $target = getCurrentObject();
                LEKey.numInputFocus(e, $(this), $target);
            });
        };

        var initBWidth = function () {
            $bWidth.onlyNum().keydown(function (e) {
                var $target = LECurrentObject.find("hr");
                LEKey.numInputKeyDown(e, $(this), $target);
            });
            //失去焦点的时候，置0
            $bWidth.blur(function (e) {
                var $target = LECurrentObject.find("hr");
                LEKey.numInputBlur(e, $(this), $target);
                LEHistory.trigger();
            });

            $bWidth.focus(function (e) {
                var $target = LECurrentObject.find("hr");
                LEKey.numInputFocus(e, $(this), $target);
            });
        };

        var resetColorPicker = function () {
            var $target = getCurrentObject();
            $colorPicker.spectrum("set", $target.css("border-color"));
            $("#bBolderColor").val($colorPicker.spectrum("get"));
        };

        var resetBolderWidth = function () {
            var isHr = LECurrentObject.hasClass("le-hr");
            var $target = getCurrentObject();
            if (isHr) {
                $bolderWidth.val($target.css("border-top-width"));
            } else {
                $bolderWidth.val($target.css("border-width"));
            }
        };

        var resetBolderStyle = function () {
            var $target = getCurrentObject();
            if (LECurrentObject.hasClass("le-hr")) {
                $bolderStyleUl.find("li[data-ref='none']").hide();
            } else {
                $bolderStyleUl.find("li[data-ref='none']").show();
            }

            $target.attr("data-set") && $bolderStyleUl.find("li[data-ref='" + $target.css("borderStyle") + "']").addClass("select");
        };

        var resetBolderRadius = function () {
            var isHr = LECurrentObject.hasClass("le-hr");
            if (isHr) {
                //noinspection JSDuplicatedDeclaration
                var $target = LECurrentObject.find("hr");
                $bWidth.val($target.css("width"));
                $("#bWidthDiv").show();
                $("#bRadiusDiv").hide();
            } else {
                var $target = getCurrentObject();
                $target.attr("data-set") && $bolderRadius.val($target.css("border-radius"));
                $("#bRadiusDiv").show();
                $("#bWidthDiv").hide();
            }

        };

        function getCurrentObject() {
            var $target = LECurrentObject;   //.hasClass("le-hr") ? LECurrentObject.find("hr") : LECurrentObject.find("img");
            if ($target.hasClass("le-hr")) {
                return LECurrentObject.find("hr");
            } else if ($target.hasClass("le-image")) {
                return LECurrentObject.find("img");
            }
            return $target;
        }

        return {
            init: function () {
                initColorPicker();
                initBolderWidth();
                initBolderStyle();
                initBolderRadius();
                initBWidth();

            },
            run: function (options, doHide) {
                resetColorPicker();
                resetBolderWidth();
                resetBolderStyle();
                resetBolderRadius();
                //切换组件时，样式设置部分回顶部
                sliderbarToTop();
                LEDisplay.show($panel, doHide);
            },
            destroy: function () {
                $bolderStyleUl.find("li").removeClass("select");
                $panel.hide();
            }
        };
    };
})(window, jQuery, LE, undefined);