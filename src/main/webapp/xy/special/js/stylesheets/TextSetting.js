/**
 * Created by isaac_gu on 2016/1/14.
 * LEHistory.trigger();//撤销方法
 */
(function (window, $, LE) {
    LE.stylesheets["TextSetting"] = function () {
        var $PS = $("#textSection");
        var $decorationUl = $("#fontSt");
        var $fontfamilyLi = $("#fontFa");
        var $fontSz = $("#fontSiz");
        var $alignUl = $("#alignUl");


        // 获取对象 如果当前对象是轮播图，只改变标题的字体
        function getObject(){
            var $target = LECurrentObject;
            if($target.hasClass("le-carousel")){
                $target = LECurrentObject.find(".carousel-caption").find("h4");
            }else if($target.hasClass("le-gallery")){
                $target = LECurrentObject.find("p").children();
            }else{
                $target = LECurrentObject;
            }
            return $target;
        }

        //如果是对多图的字体进行设置，设置完毕需要触发调整多图宽高的方法，
        function changeGalleryHeight(){
            var $target=getObject();
            if($target.parent().hasClass("gallery-title")){
                LE.stylesheets["Gallery"]().changeImageHeight();
            }

        }



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
                //var $target = LECurrentObject;
                //如果当前对象是轮播图，只改变标题的字体
                var $target=getObject();

                var contenteditable = $target.attr("contenteditable")
                    && $target.attr("contenteditable") == "true" ? true : false;
                var fontStyle = _$this.data("fontstyle");
                var fontValue = _$this.data("value");
                var fontReset = _$this.data("reset");
                var kg = _$this.attr("data-kg");
                //判断是否为可编辑状态，确定执行局部样式还是整体样式
                if (contenteditable) {
                    if (_$this.data("reset") == "200") {
                        fontPartSty(kg, fontStyle, fontValue, fontReset, _$this);
                    }
                    else if (_$this.data("reset") == "normal") {//局部斜体功能
                        fontPartSty(kg, fontStyle, fontValue, fontReset, _$this);
                    }
                    else if (_$this.data("line") == "under") { //局部下划线功能
                        fontPartSty(kg, fontStyle, fontValue, fontReset, _$this, true);
                    }
                    else if (_$this.data("line") == "through") { //局部删除线功能
                        fontPartSty(kg, fontStyle, fontValue, fontReset, _$this, true);
                    }
                }
                //否则执行整体样式调节
                else {
                    if (_$this.data("reset") == "200") {
                        fontAllSty(_$this, fontStyle, fontReset, fontValue);
                    } else if (_$this.data("reset") == "normal") {
                        fontAllSty(_$this, fontStyle, fontReset, fontValue);
                    } else if (_$this.data("line") == "under") { //局部下划线功能
                        fontAllSty(_$this, fontStyle, fontReset, fontValue);
                    } else if (_$this.data("line") == "through") { //局部删除线功能
                        fontAllSty(_$this, fontStyle, fontReset, fontValue);
                    }

                    if ($target.css("text-decoration") == "underline") {
                        $decorationUl.find('li[data-value="line-through"]').removeClass("select");
                    }
                    else if ($target.css("text-decoration") == "line-through") {
                        $decorationUl.find('li[data-value="underline"]').removeClass("select");
                    }
                    $target.find("span").css(fontStyle, "");
                    $target.find("span").removeClass()
                }

                changeGalleryHeight();

                LEHistory.trigger();
            });
        };
        /*设置局部字体样式 加粗、斜体、下划线、删除线
         1、为li标签添加data-属性作为开关，值在true和false之间切换，并且头部与右侧按钮的值联动，互相可以改变对方的值；
         2、若值为true，改变样式，并且为li按钮增加类select；
         3、若值为false，恢复样式，并且去掉li按钮的类select；
         4、删除线和下划线需要特殊设置，二者只能有一个拥有select类 通过开关p的值判断是否为下划线和删除线。
         */
        var fontPartSty = function (kg, fontStyle, fontValue, fontReset, _$this, p) {
            if (kg == "true") {
                FstyleP(fontStyle, fontValue);
                if (p) {
                    //实现下划线和删除线只能有一个被选中
                    $(".throughFont").removeClass("select");
                }
                _$this.addClass("select");
                _$this.attr("data-kg", "false");
            } else {
                FstyleP(fontStyle, fontReset);
                _$this.removeClass("select");
                _$this.attr("data-kg", "true");
            }
        };

        /*设置整体字体样式 加粗、斜体、下划线、删除线
         1、通过判断是否有select类确定现在的样式和按钮状态；
         2、若拥有select，改变字体样式，头部和右侧按钮为按下状态；
         3、若没有select，恢复样式，头部和右侧按钮为弹起状态；
         */
        var fontAllSty = function (_$this, fontStyle, fontReset, fontValue) {
            //var $target = LECurrentObject;
            //如果当前对象是轮播图，只改变标题的字体
            var $target=getObject();

            if (_$this.hasClass("select")) {
                $target.css(fontStyle, fontReset);
                _$this.removeClass("select");
            } else {
                $target.css(fontStyle, fontValue);
                _$this.addClass("select");
            }
        };

        /*改变字体*/
        var initfontFamilyEvent = function () {
            $fontfamilyLi.find("li").bind("click", function () {
                var _$this = $(this);
                //var $target = LECurrentObject;

                //如果当前对象是轮播图，只改变标题的字体
                var $target=getObject();

                var contenteditable = $target.attr("contenteditable")
                    && $target.attr("contenteditable") == "true" ? true : false;
               /* var txt = _$this.find("a").attr("value");
                $("#fontFm").find("cite").html(txt);*/
                if (contenteditable) {
                    FstyleP("font-family", _$this.find('a').attr("value"))
                }
                else {
                    $target.css('font-family', _$this.find('a').attr("value"));
                    $target.find("span").css("font-family", "");
                }

                changeGalleryHeight();

                LEHistory.trigger();
            })
        };

        /*改变字体大小*/
        var initfontSizeEvent = function () {
            $fontSz.find("li").bind("click", function () {
                var _$this = $(this);  //点击的设置字体样式的按钮
                var $target=getObject();
                //var $target = LECurrentObject; //获取当前对象
                var contenteditable = $target.attr("contenteditable")
                    && $target.attr("contenteditable") == "true" ? true : false;
                var txt = _$this.find("a").text();
                $fontSz.find("cite").html(txt);
                if (contenteditable) {             //判断是否为可编辑状态，确定执行局部样式还是整体样式
                    FstyleP("font-size", _$this.find('a').attr("value") + "px")
                }
                else {
                    $target.css('font-size', _$this.find('a').attr("value") + "px");
                    $target.find("span").css("font-size", "");
                    $target.attr("data-fontsize", _$this.find("a").text());
                }

                //改变字体大小的同时，行距和首行缩进动态变化
                var _fontFz = parseInt($target.attr("data-fontsize"));
                var _lineHeight=Math.round(_fontFz*1.4);
                var num=$("#txtIndent").val();
                var _textIndent=_fontFz*num;

                $target.css("line-height",_lineHeight+"px");
                $target.css("text-indent",_textIndent+"px");

                resetLineHeight();
                resettextIndent();

                changeGalleryHeight();

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
                //var $target = LECurrentObject;

                //如果当前对象是轮播图，只改变标题的字体
                var $target=getObject();

                $target.css("text-align", _$this.attr("data-align"));
                $(".clearbg").removeClass("select");
                if (_$this.attr("data-align") == "left") {
                    _$this.addClass("select");
                } else if (_$this.attr("data-align") == "center") {
                    _$this.addClass("select");
                } else if (_$this.attr("data-align") == "right") {
                    _$this.addClass("select");
                }

                changeGalleryHeight();

                LEHistory.trigger();
            });
        };
        /*改变颜色*/
        var initColorEvent = function () {
            $("#headColorPick").spectrum(
                LEColorPicker.getOptions(function (tinycolor) {
                    var _$this = $(this);
                    //var $target = LECurrentObject;

                    //如果当前对象是轮播图，只改变标题的字体
                    var $target=getObject();

                    var contenteditable = $target.attr("contenteditable")
                        && $target.attr("contenteditable") == "true" ? true : false;
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
        var addColorPickerAttr = function () {
            $("div.sp-replacer").attr('unselectable', 'on')
                .css({'-moz-user-select': '-moz-none',
                    '-moz-user-select': 'none',
                    '-o-user-select': 'none',
                    '-webkit-user-select': 'none',
                    '-ms-user-select': 'none'
                });

            $("div.sp-palette-container").attr('unselectable', 'on')
                .css({'-moz-user-select': '-moz-none',
                    '-moz-user-select': 'none',
                    '-o-user-select': 'none',
                    '-webkit-user-select': 'none',
                    '-ms-user-select': 'none'
                });

            $(document).on({'selectstart': function () {
                return false;
            }}, ".sp-replacer,.sp-palette-container");

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


        /*设置文字行距*/

        var initLineHeightEvent=function(){
            $("#txtLineHeight").onlyNum().keydown(function (e) {
                var _target=getObject();
                LEKey.numInputKeyDown(e, $(this), _target);
                changeGalleryHeight();
            });
            //失去焦点的时候，置0
            $("#txtLineHeight").blur(function (e) {
                var _target=getObject();
                LEKey.numInputBlur(e, $(this), _target);
                changeGalleryHeight();
                LEHistory.trigger();
            });

            $("#txtLineHeight").focus(function (e) {
                var _target=getObject();
                LEKey.numInputFocus(e, $(this), _target);
                changeGalleryHeight();
            });

        }
        /*设置首行缩进*/

        var inittextIndentEvent=function(){
            $("#txtIndent").onlyNum().keydown(function (e) {
                var _target=getObject();
                LE.stylesheets["MainMenu"]().numInputKeyDownPad(e, $(this), _target);
                changeGalleryHeight();
            });
            //失去焦点的时候，置0
            $("#txtIndent").blur(function (e) {
                var _target=getObject();
                LE.stylesheets["MainMenu"]().numInputBlurPad(e, $(this), _target);
                changeGalleryHeight();
                LEHistory.trigger();
            });

            $("#txtIndent").focus(function (e) {
                var _target=getObject();
                LE.stylesheets["MainMenu"]().numInputFocusPad(e, $(this), _target);
                changeGalleryHeight();
            });

        };
        /*初始化首行缩进*/
        var resettextIndent = function () {
            var _target=getObject();
            var _ref = parseInt(_target.css("text-indent"));
            var _fontSize=parseInt(_target.css("font-size"));
            var num=_ref/_fontSize;
            if(isNaN(num)){
                $("#txtIndent").val("0");
            }else{
                $("#txtIndent").val(num);
            }

        };
        /*初始化文字行间距*/
        var resetLineHeight = function () {
            var _target=getObject();
            var _ref = parseInt(_target.css("line-height"));
            if(isNaN(_ref)){
                $("#txtLineHeight").val("17px");
            }else{
                $("#txtLineHeight").val(_ref+"px");
            }

        };
        /*获得当前对象的对齐值，并且初始化到按钮上面*/
        var resetDecorationStyle = function () {
            //var $target = LECurrentObject;
            //如果当前对象是轮播图，只改变标题的字体
            var $target=getObject();
            var _textFw = $target.css("font-weight");
            var _textFs = $target.css("font-style");
            var _textTd = $target.css("text-decoration");
            if (_textFw == "bold") {
                $decorationUl.find('li[data-value = "700"]').addClass("select");
            } else {
                $decorationUl.find('li[data-value = "700"]').removeClass("select");
            }
            ;
            if (_textFs == "italic") {
                $decorationUl.find('li[data-value="italic"]').addClass("select");
            } else {
                $decorationUl.find('li[data-value="italic"]').removeClass("select");
            }
            ;
            if (_textTd == "underline") {
                $decorationUl.find('li[data-value="underline"]').addClass("select");
            } else {
                $decorationUl.find('li[data-value="underline"]').removeClass("select");
            }
            ;
            if (_textTd == "line-through") {
                $decorationUl.find('li[data-value="line-through"]').addClass("select");
            } else {
                $decorationUl.find('li[data-value="line-through"]').removeClass("select");
            }
            ;
        };

        /*初始化按钮显示字体样式*/
        var resetfontFamilyStyle = function () {
            //var $target = LECurrentObject;
            //如果当前对象是轮播图，只改变标题的字体
            var $target=getObject();
            var _fontFm = $target.css("font-family");
            $("#fontFm").find("cite").text(_fontFm);
        };
        /*初始化按钮显示字体大小*/
        var resetfontSizeStyle = function () {
           // var $target = LECurrentObject;
            //如果当前对象是轮播图，只改变标题的字体
            var $target=getObject();
            if ($target.data("fontsize")) {
                var _fontFz = $target.attr("data-fontsize");
                //var _fontFz=$target.data("fontsize"); 这种读法有问题！！
                $fontSz.find("cite").text(_fontFz);
            } else {
                /*$("#fontSiz").find("cite").text("小五");*/
                $fontSz.find("cite").text("12px");
            }
        };
        /*获得当前对象的对齐值，并且初始化到按钮上面*/
        var resetAlignStyle = function () {
            //导航不显示文字颜色和居中设置
            if(LECurrentObject.hasClass("le-nav")){
                $("#alignUl").hide();
                $("#textColorEditor").hide();
            }else{
                $("#alignUl").show();
                $("#textColorEditor").show();
            }
            //var $target = LECurrentObject;
            //如果当前对象是轮播图，只改变标题的字体
            var $target=getObject();
            var _align = $target.css("text-align") || "left";
            $(".clearbg").removeClass("select selected");
            $alignUl.find("li[data-align=" + _align + "]").addClass("select selected");
        };
        /*颜色初始化*/
        var resetColor=function(){
            //如果当前对象是轮播图，只改变标题的字体
            var $target=getObject();
            var contenteditable = $target.attr("contenteditable")
                && $target.attr("contenteditable") == "true" ? true : false;
            if (!contenteditable) {
                $("#headColorPick").spectrum("set", $target.css("color"));
            }
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
                initLineHeightEvent();
                inittextIndentEvent();
            },
            run: function (options, doHide, doSlide) {
                resetDecorationStyle();
                resetfontFamilyStyle();
                resetfontSizeStyle();
                resetAlignStyle();
                resetColor();
                resetLineHeight();
                resettextIndent();
                //切换组件时，样式设置部分回顶部
                sliderbarToTop();
                /*var $target = LECurrentObject;
                $target = options.object;*/
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