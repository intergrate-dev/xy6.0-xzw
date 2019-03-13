/**
 * Created by isaac_gu on 2016/1/14.文件名带Old及XXX的为不用的js
 */
(function (window, $, LE) {
    LE.stylesheets["HeadTextSetting"] = function () {
        var $fontfamilyLihead = $("#fontFahead");
        var $fontSzhead = $("#fontSizhead");
        var $decorationUlhead = $("#fontSthead");
        var $alignUl = $("#alignUl");
        var $alignUlhead = $("#alignUlhead");

        /*功能描述参照Textsetting.js*/
        var FstyleP = function (Fstyle, value) {
            var applier;
            var classApplierModule = rangy.modules.ClassApplier;
            if (rangy.supported && classApplierModule && classApplierModule.supported) {
                applier = rangy.createClassApplier("font-mark", {
                    tagNames: "span"
                });
                applier && applier.toggleSelection();
                var _mark = $(".font-mark");
                _mark.css(Fstyle, value);
                _mark.removeClass("font-mark");
                return false;
            }
        };


        /*改变字体*/
        var initfontFamilyHeadEvent = function () {
            $fontfamilyLihead.find("li").bind("click", function () {
                var _$this = $(this);
                var $target = LECurrentObject;
                var $city = _$this.parents('ul').siblings("cite");
                var contenteditable = $target.attr("contenteditable")
                    && $target.attr("contenteditable") == "true" ? true : false;
                var txt = _$this.find("a").attr("value");
                //var txt = _$this.find("a").text();
                $("#fontFmhead").find("cite").html(txt);
                $("#fontFm").find("cite").html(txt);
                if (contenteditable) {
                    FstyleP("font-family", _$this.find('a').attr("value"));
                }
                else {
                    $target.css('font-family', _$this.find('a').attr("value"));
                    $target.find("span").css("font-family", "");
                };
                LEHistory.trigger();
            })
        };


        /*改变字体大小*/
        var initfontSizeHeadEvent = function () {
            $fontSzhead.find("li").bind("click", function () {
                var _$this = $(this);
                var $target = LECurrentObject;
                var contenteditable = $target.attr("contenteditable")
                    && $target.attr("contenteditable") == "true" ? true : false;//string
                var txt = _$this.find("a").text();
                $("#fontSizhead").find("cite").html(txt);
                $("#fontSiz").find("cite").html(txt);
                if (contenteditable) {
                    FstyleP("font-size", _$this.find('a').attr("value") + "px")
                }
                else {
                    $target.css('font-size', _$this.find('a').attr("value") + "px");
                    $target.find("span").css("font-size", "");
                    $target.attr("data-fontsize", _$this.find("a").text());
                }
                LEHistory.trigger();
            })
        };
        /*改变字体样式*/
        var initDecorationHeadEvent = function () {
            $decorationUlhead.find("li").bind("click", function () {
                var _$this = $(this);
                var $target = LECurrentObject;
                var contenteditable = $target.attr("contenteditable")
                    && $target.attr("contenteditable") == "true" ? true : false;
                var fontStyle = _$this.data("fontstyle");
                var fontValue = _$this.data("value");
                var fontReset = _$this.data("reset");
                var kg = _$this.attr("data-kg");
                //判断是否为可编辑状态，确定执行局部样式还是整体样式
                if (contenteditable) {
                    if (_$this.data("reset") == "200") {
                        fontpartheadSty(kg, fontStyle, fontValue, fontReset, _$this, "#fontBold");
                    } else if (_$this.data("reset") == "normal") {//局部斜体功能
                        fontpartheadSty(kg, fontStyle, fontValue, fontReset, _$this, "#fontItalic");
                    } else if (_$this.data("line") == "under") { //局部下划线功能
                        fontpartheadSty(kg, fontStyle, fontValue, fontReset, _$this, "#fontUnder", true);
                    } else if (_$this.data("line") == "through") { //局部删除线功能
                        fontpartheadSty(kg, fontStyle, fontValue, fontReset, _$this, "#fontThrough", true);
                    }
                }//否则执行整体样式调节
                else {
                    if (_$this.data("reset") == "200") {
                        fontallheadSty(_$this, fontStyle, fontReset, "#fontBold", fontValue);
                    } else if (_$this.data("reset") == "normal") {
                        fontallheadSty(_$this, fontStyle, fontReset, "#fontItalic", fontValue);
                    } else if (_$this.data("line") == "under") { //局部下划线功能
                        fontallheadSty(_$this, fontStyle, fontReset, "#fontUnder", fontValue);
                    } else if (_$this.data("line") == "through") { //局部删除线功能
                        fontallheadSty(_$this, fontStyle, fontReset, "#fontThrough", fontValue);
                    };

                    if ($target.css("text-decoration") == "underline") {
                        $decorationUlhead.find('li[data-value="line-through"]').removeClass("select");
                        $("#fontThrough").removeClass("select");
                    }
                    else if ($target.css("text-decoration") == "line-through") {
                        $decorationUlhead.find('li[data-value="underline"]').removeClass("select");
                        $("#fontUnder").removeClass("select");
                    }
                    $target.find("span").css(fontStyle, "");
                    $target.find("span").removeClass()
                };
                LEHistory.trigger();
            });
        };

        var fontpartheadSty = function (kg, fontStyle, fontValue, fontReset, _$this, id, p) {
            if (kg == "true") {
                FstyleP(fontStyle, fontValue);
                if (p) {
                    $(".throughFont").removeClass("select");
                }
                $(id).addClass("select");
                _$this.addClass("select");
                _$this.attr("data-kg", "false");
                $(id).attr("data-kg", "false");
            } else {
                FstyleP(fontStyle, fontReset);
                _$this.removeClass("select");
                $(id).removeClass("select");
                _$this.attr("data-kg", "true");
                $(id).attr("data-kg", "true");
            }
        };

        var fontallheadSty = function (_$this, fontStyle, fontReset, id, fontValue) {
            if (_$this.hasClass("select")) {
                $target.css(fontStyle, fontReset);
                _$this.removeClass("select");
                $(id).removeClass("select");
            } else {
                $target.css(fontStyle, fontValue);
                _$this.addClass("select");
                $(id).addClass("select");
            };
        };
        /*字体对齐*/
        var initAlignHeadEvent = function () {
            $alignUlhead.find("li").bind("click", function () {
                var _$this = $(this);
                var $target = LECurrentObject;
                $target.css("text-align", _$this.attr("data-align"));
                $(".clearbg").removeClass("select");
                if (_$this.attr("data-align") == "left") {
                    _$this.addClass("select");
                    $("#alignUl>li").eq(0).addClass("select");
                } else if (_$this.attr("data-align") == "center") {
                    _$this.addClass("select");
                    $("#alignUl>li").eq(1).addClass("select");
                } else if (_$this.attr("data-align") == "right") {
                    _$this.addClass("select");
                    $("#alignUl>li").eq(2).addClass("select");
                }
                LEHistory.trigger();
            });
        };
        var initColorHeadEvent = function () {
            $("#headColorPickhead").spectrum(
                LEColorPicker.getOptions(function (tinycolor) {
                    var _$this = $(this);
                    var $target = LECurrentObject;
                    var contenteditable = $target.attr("contenteditable")
                        && $target.attr("contenteditable") == "true" ? true : false;
                    $("#headColorPick").spectrum("set", tinycolor);//设置右侧颜色按钮的样式
                    if (contenteditable) {
                        FstyleP("color", "#" + tinycolor.toPercentageRgbString())
                    }
                    else {
                        $target.css("color", "#" + tinycolor.toPercentageRgbString());
                        $target.find("span").css("color", "");
                    }
                    LEHistory.trigger();
                })
            );
        };
        /*初始化按钮显示字体*/
        var resetfontFamilyHeadStyle = function () {
            var $target = LECurrentObject;
            var _fontFm = $target.css("font-family");
            $("#fontFmhead").find("cite").text(_fontFm);
        };

        /*初始化按钮显示字体大小*/
        var resetfontSizeHeadStyle = function () {
            var $target = LECurrentObject;
            if ($target.data("fontsize")) {
                var _fontFz = $target.attr("data-fontsize");
                //var _fontFz=$target.data("fontsize"); 这种读法有问题！！
                $("#fontSizhead").find("cite").text(_fontFz);
            } else {
                /*$("#fontSizhead").find("cite").text("小五");*/
                $("#fontSizhead").find("cite").text("12px");
            }
        };
        /*初始化按钮显示字体样式*/
        var resetDecorationHeadStyle = function () {
            var $target = LECurrentObject;
            var _textFw = $target.css("font-weight");
            var _textFs = $target.css("font-style");
            var _textTd = $target.css("text-decoration");
            if (_textFw == "bold") {
                $decorationUlhead.find('li[data-value = "700"]').addClass("select");
            } else {
                $decorationUlhead.find('li[data-value = "700"]').removeClass("select");
            };
            if (_textFs == "italic") {
                $decorationUlhead.find('li[data-value="italic"]').addClass("select");
            } else {
                $decorationUlhead.find('li[data-value="italic"]').removeClass("select");
            };
            if (_textTd == "underline") {
                $decorationUlhead.find('li[data-value="underline"]').addClass("select");
            } else {
                $decorationUlhead.find('li[data-value="underline"]').removeClass("select");
            };
            if (_textTd == "line-through") {
                $decorationUlhead.find('li[data-value="line-through"]').addClass("select");
            } else {
                $decorationUlhead.find('li[data-value="line-through"]').removeClass("select");
            };
        };
        /*初始化按钮显示字体对齐按钮*/
        var resetAlignHeadStyle = function () {
            var $target = LECurrentObject;
            var _align = $target.css("text-align") || "left";
            $(".clearbg").removeClass("select selected");
            $alignUl.find("li[data-align=" + _align + "]").addClass("select selected");
            $alignUlhead.find("li[data-align=" + _align + "]").addClass("select selected");
        };

        return {
            init: function () {
                initfontFamilyHeadEvent();
                initfontSizeHeadEvent();
                initDecorationHeadEvent();
                initAlignHeadEvent();
                initColorHeadEvent();
            },
            run: function (options) {
                resetfontFamilyHeadStyle();
                resetfontSizeHeadStyle();
                resetDecorationHeadStyle();
                resetAlignHeadStyle();
                //切换组件时，样式设置部分回顶部
                sliderbarToTop();
                var contenteditable = $target.attr("contenteditable")
                    && $target.attr("contenteditable") == "true" ? true : false;
                $target = options.object;
                if (!contenteditable) {
                    $("#headColorPickhead").spectrum("set", $target.css("color"));
                }
            },
            destroy: function () {
                //console.info("PageSetting destroy")
            }
        };
    };
})(window, jQuery, LE, undefined);



