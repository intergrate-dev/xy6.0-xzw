/**
 * Created by isaac_gu on 2016/1/14.文件名带Old及XXX的为不用的js
 * LEHistory.trigger();//撤销方法
 */
(function (window, $, LE) {
    LE.stylesheets["TextSetting"] = function () {
        var $PS = $("#textSection");
        var $decorationUl = $("#fontSt");
        var $fontfamilyLi = $("#fontFa");
        var $fontSz = $("#fontSiz");
        var $alignUl = $("#alignUl");
        var $alignUlhead = $("#alignUlhead");

        /*局部字体样式
         1、为选中的内容增加span标签和font-mark的class；
         2、为含有该class的元素添加行内样式style="font-family: "等等;
         3、移除class名
         */
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

        /*设置字体样式
         1、判断目标对象是否为可编辑状态；
         2、设置加粗、斜体、下划线、删除线；
         3、判断字体是否具有样式为按钮增删背景色class
         */
        var initDecorationEvent = function () {
            $decorationUl.find("li").bind("click", function () {
                var _$this = $(this);
                //获取当前对象
                var $target = LECurrentObject;
                var contenteditable = $target.attr("contenteditable")
                    && $target.attr("contenteditable") == "true" ? true : false;//string
                var fontStyle = _$this.data("fontstyle");
                var fontValue = _$this.data("value");
                var fontReset = _$this.data("reset");
                var kg = _$this.attr("data-kg");
                //判断是否为可编辑状态，确定执行局部样式还是整体样式
                if (contenteditable) {
                    if (_$this.data("reset") == "200") {
                        fontPartSty(kg, fontStyle, fontValue, fontReset, _$this, "#fontBoldhead");
                    }
                    else if (_$this.data("reset") == "normal") {//局部斜体功能
                        fontPartSty(kg, fontStyle, fontValue, fontReset, _$this, "#fontItalichead");
                    }
                    else if (_$this.data("line") == "under") { //局部下划线功能
                        fontPartSty(kg, fontStyle, fontValue, fontReset, _$this, "#fontUnderhead", true);
                    }
                    else if (_$this.data("line") == "through") { //局部删除线功能
                        fontPartSty(kg, fontStyle, fontValue, fontReset, _$this, "#fontThroughhead", true);
                    }
                }
                //否则执行整体样式调节
                else {
                    if (_$this.data("reset") == "200") {
                        fontAllSty(_$this, fontStyle, fontReset, "#fontBoldhead", fontValue);
                    } else if (_$this.data("reset") == "normal") {
                        fontAllSty(_$this, fontStyle, fontReset, "#fontItalichead", fontValue);
                    } else if (_$this.data("line") == "under") { //局部下划线功能
                        fontAllSty(_$this, fontStyle, fontReset, "#fontUnderhead", fontValue);
                    } else if (_$this.data("line") == "through") { //局部删除线功能
                        fontAllSty(_$this, fontStyle, fontReset, "#fontThroughhead", fontValue);
                    };
                    if ($target.css("text-decoration") == "underline") {
                        $decorationUl.find('li[data-value="line-through"]').removeClass("select");
                        $("#fontThroughhead").removeClass("select");
                    }
                    else if ($target.css("text-decoration") == "line-through") {
                        $decorationUl.find('li[data-value="underline"]').removeClass("select");
                        $("#fontUnderhead").removeClass("select");
                    }
                    $target.find("span").css(fontStyle, "");
                    $target.find("span").removeClass()
                };
                LEHistory.trigger();
            });
        };
        /*设置局部字体样式 加粗、斜体、下划线、删除线
         1、为li标签添加data-属性作为开关，值在true和false之间切换，并且头部与右侧按钮的值联动，互相可以改变对方的值；
         2、若值为true，改变样式，并且为li按钮增加类select；
         3、若值为false，恢复样式，并且去掉li按钮的类select；
         4、删除线和下划线需要特殊设置，二者只能有一个拥有select类 通过开关p的值判断是否为下划线和删除线。
         */
        var fontPartSty = function (kg, fontStyle, fontValue, fontReset, _$this, id, p) {
            if (kg == "true") {
                FstyleP(fontStyle, fontValue);
                if (p) {
                    //实现下划线和删除线只能有一个被选中
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

        /*设置整体字体样式 加粗、斜体、下划线、删除线
         1、通过判断是否有select类确定现在的样式和按钮状态；
         2、若拥有select，改变字体样式，头部和右侧按钮为按下状态；
         3、若没有select，恢复样式，头部和右侧按钮为弹起状态；
         */
        var fontAllSty = function (_$this, fontStyle, fontReset, id, fontValue) {
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

        /*改变字体*/
        var initfontFamilyEvent = function () {
            $fontfamilyLi.find("li").bind("click", function () {
                var _$this = $(this);
                var $target = LECurrentObject;
                var contenteditable = $target.attr("contenteditable")
                    && $target.attr("contenteditable") == "true" ? true : false;//string
                var txt = _$this.find("a").attr("value");
                $("#fontFmhead").find("cite").html(txt);
                $("#fontFm").find("cite").html(txt);
                if (contenteditable) {
                    FstyleP("font-family", _$this.find('a').attr("value"))
                }
                else {
                    $target.css('font-family', _$this.find('a').attr("value"));
                    $target.find("span").css("font-family", "");
                }
                LEHistory.trigger();
            })
        };

        /*改变字体大小*/
        var initfontSizeEvent = function () {
            $fontSz.find("li").bind("click", function () {
                var _$this = $(this);  //点击的设置字体样式的按钮
                var $target = LECurrentObject; //获取当前对象
                var contenteditable = $target.attr("contenteditable")
                    && $target.attr("contenteditable") == "true" ? true : false;//string
                var txt = _$this.find("a").text();
                $("#fontSizhead").find("cite").html(txt);
                $("#fontSiz").find("cite").html(txt);
                if (contenteditable) {             //判断是否为可编辑状态，确定执行局部样式还是整体样式
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

        /*改变对齐方式 不联动时*/
        /*var initAlignEvent = function(){
         $alignUl.find("li").bind("click", function(e){
         var _$this = $(this);
         var $target = LECurrentObject;
         $target.css("text-align", _$this.attr("data-align"));
         });
         };*/
        /*改变对齐方式 上下联动*/
        var initAlignEvent = function () {
            $alignUl.find("li").bind("click", function () {
                var _$this = $(this);
                var $target = LECurrentObject;
                $target.css("text-align", _$this.attr("data-align"));
                $(".clearbg").removeClass("select");
                if (_$this.attr("data-align") == "left") {
                    _$this.addClass("select");
                    $("#alignUlhead>li").eq(0).addClass("select");
                } else if (_$this.attr("data-align") == "center") {
                    _$this.addClass("select");
                    $("#alignUlhead>li").eq(1).addClass("select");
                } else if (_$this.attr("data-align") == "right") {
                    _$this.addClass("select");
                    $("#alignUlhead>li").eq(2).addClass("select");
                }
                LEHistory.trigger();
            });
        };
        var initColorEvent = function () {
            $("#headColorPick").spectrum(
                LEColorPicker.getOptions(function (tinycolor) {
                    var _$this = $(this);
                    var $target = LECurrentObject;
                    var contenteditable = $target.attr("contenteditable")
                        && $target.attr("contenteditable") == "true" ? true : false;
                    $("#headColorPickhead").spectrum("set", tinycolor);
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
        /*
        为所有的调色器增加属性unselectable="on" onselectstart="return false;" style="-webkit-user-select: none;-moz-user-select: none;" 点击时选中的对象不失去焦点
         */
        var addColorPickerAttr=function(){
            $("div.sp-replacer").attr('unselectable','on')
                .css({'-moz-user-select':'-moz-none',
                    '-moz-user-select':'none',
                    '-o-user-select':'none',
                    '-webkit-user-select':'none',
                    '-ms-user-select':'none'
                });

            $("div.sp-palette-container").attr('unselectable','on')
                .css({'-moz-user-select':'-moz-none',
                    '-moz-user-select':'none',
                    '-o-user-select':'none',
                    '-webkit-user-select':'none',
                    '-ms-user-select':'none'
                });

            $(document).on({'selectstart':function(){
                return false;
            }},".sp-replacer,.sp-palette-container");

           /* $("div.sp-palette-container").attr('unselectable','on')
                .css({'-moz-user-select':'-moz-none',
                    '-moz-user-select':'none',
                    '-o-user-select':'none',
                    '-webkit-user-select':'none',
                    '-ms-user-select':'none'
                }).bind('selectstart',function(){
                    return false;
                });*/
        };
        /*获得当前对象的对齐值，并且初始化到按钮上面*/
        var resetDecorationStyle = function () {
            var $target = LECurrentObject;
            var _textFw = $target.css("font-weight");
            var _textFs = $target.css("font-style");
            var _textTd = $target.css("text-decoration");
            if (_textFw == "bold") {
                $decorationUl.find('li[data-value = "700"]').addClass("select");
            } else {
                $decorationUl.find('li[data-value = "700"]').removeClass("select");
            };
            if (_textFs == "italic") {
                $decorationUl.find('li[data-value="italic"]').addClass("select");
            } else {
                $decorationUl.find('li[data-value="italic"]').removeClass("select");
            };
            if (_textTd == "underline") {
                $decorationUl.find('li[data-value="underline"]').addClass("select");
            } else {
                $decorationUl.find('li[data-value="underline"]').removeClass("select");
            };
            if (_textTd == "line-through") {
                $decorationUl.find('li[data-value="line-through"]').addClass("select");
            } else {
                $decorationUl.find('li[data-value="line-through"]').removeClass("select");
            };
        };

        /*初始化按钮显示字体样式*/
        var resetfontFamilyStyle = function () {
            var $target = LECurrentObject;
            var _fontFm = $target.css("font-family");
            $("#fontFm").find("cite").text(_fontFm);
        };
        /*初始化按钮显示字体大小*/
        var resetfontSizeStyle = function () {
            var $target = LECurrentObject;
            if ($target.data("fontsize")) {
                var _fontFz = $target.attr("data-fontsize");
                //var _fontFz=$target.data("fontsize"); 这种读法有问题！！
                $("#fontSiz").find("cite").text(_fontFz);
            } else {
                /*$("#fontSiz").find("cite").text("小五");*/
                $("#fontSiz").find("cite").text("12px");
            }
        };
        /*获得当前对象的对齐值，并且初始化到按钮上面*/
        var resetAlignStyle = function () {
            var $target = LECurrentObject;
            var _align = $target.css("text-align") || "left";
            $(".clearbg").removeClass("select selected");
            $alignUl.find("li[data-align=" + _align + "]").addClass("select selected");
            $alignUlhead.find("li[data-align=" + _align + "]").addClass("select selected");
        };

        return {
            init: function () {
                rangy.init();
                initDecorationEvent();
                initfontFamilyEvent();
                initfontSizeEvent();
                initAlignEvent();
                initColorEvent();
                addColorPickerAttr();
            },
            run: function (options, doHide, doSlide) {
                resetDecorationStyle();
                resetfontFamilyStyle();
                resetfontSizeStyle();
                resetAlignStyle();
                //切换组件时，样式设置部分回顶部
                sliderbarToTop();
                var $target = LECurrentObject;
                var contenteditable = $target.attr("contenteditable")
                    && $target.attr("contenteditable") == "true" ? true : false;
                $target = options.object;
                if (!contenteditable) {
                    $("#headColorPick").spectrum("set", $target.css("color"));
                }
                LEDisplay.show($PS, doHide, doSlide);
            },
            destroy: function () {
                $decorationUl.find("li").removeClass("select").removeClass("selected");
                //   $PS.hide();
                //把对齐按钮恢复成原样
                $alignUl.find("li").removeClass("select").removeClass("selected");
                $PS.hide();
                //$("div[contenteditable=true]").attr("contenteditable", false).removeClass("plugin_entity_disabled").addClass("plugin_entity");
            }
        };
    };
})(window, jQuery, LE, undefined);