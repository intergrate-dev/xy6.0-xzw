/**
 * Created by isaac_gu on 2016/1/14.
 * LEHistory.trigger();//撤销方法
 */
(function (window, $, LE) {
    LE.stylesheets["TextSettingWhole"] = function(){
        var $PC = $("#textSection");
        return {
            init: function(){
                LE.oTextSetting = new OTextSetting($PC,0);
                LE.oTextSetting.addColorPickerAttr();
            },
            run: function(options, doHide, doSlide){
                LE.oTextSetting.reset();
                LEDisplay.show($PC, doHide, doSlide);
            },
            destroy: function(){
                LE.oTextSetting.destroy();
            }
        };
    };

    LE.stylesheets["TextSettingWholeIII"] = function(){
        var $PC = $("#textSection-III");
        return {
            init: function(){
                LE.oTextSettingIII = new OTextSetting($PC,2);
                LE.oTextSettingIII.addColorPickerAttr();
            },
            run: function(options, doHide, doSlide){
                LE.oTextSettingIII.reset();
                LEDisplay.show($PC, doHide, doSlide);
            },
            destroy: function(){
                LE.oTextSettingIII.destroy();
            }
        };
    };

    function OTextSetting($PC, layer){
        this.$pc = $PC;
        this.layer = layer;
        this.$decorationUl= this.$pc.find("[id^=fontSt]");
        this.$fontfamilyLi = this.$pc.find("[id^=fontFa]");
        this.$fontSz = this.$pc.find("[id^=fontSiz]");
        this.$alignUl = this.$pc.find("[id^=alignUl]");
        this.$txtLineHeight = this.$pc.find("[id^=txtLineHeight]");
        this.$txtIndent = this.$pc.find("[id^=txtIndent]");
        this.$headColorPick=this.$pc.find("[id^=headColorPick]");
        this.$textColorEditor=this.$pc.find("[id^=textColorEditor]");
        this.$fontFm=this.$pc.find("[id^=fontFm]");

        this.init($PC);
    }

    OTextSetting.prototype = {
        init:function(){
            this.initDecorationEvent();
            this.initfontFamilyEvent();
            this.initfontSizeEvent();
            this.initAlignEvent();
            this.initColorEvent();
            this.initLineHeightEvent();
            this.inittextIndentEvent();


        },
        reset:function(){
            this.resettextIndent();
            this.resetLineHeight();
            this.resetDecorationStyle();
            this.resetfontFamilyStyle();
            this.resetfontSizeStyle();
            this.resetAlignStyle();
            this.resetColor();
            //切换组件时，样式设置部分回顶部
            sliderbarToTop();

        },
        destroy:function(){
            var obj=this;
            obj.$decorationUl.find("li").removeClass("select").removeClass("selected");
            //把对齐按钮恢复成原样
            obj.$alignUl.find("li").removeClass("select").removeClass("selected");
            obj.$pc.hide();
        },
        // 获取对象 如果当前对象是轮播图，只改变标题的字体
        getTextObject :function(){
        var $target = LECurrentObject;
        if($target.hasClass("le-carousel")){
            $target = LECurrentObject.find(".carousel-caption").find("h4");
        }else if($target.hasClass("le-gallery")){
            $target = LECurrentObject.find("p").children();
        }else{
            $target = LECurrentObject;
        }
        return $target;
    },
        /*设置整体字体样式 加粗、斜体、下划线、删除线
         1、通过判断是否有select类确定现在的样式和按钮状态；
         2、若拥有select，改变字体样式，头部和右侧按钮为按下状态；
         3、若没有select，恢复样式，头部和右侧按钮为弹起状态；
         */
        fontAllSty :function (_$this, fontStyle, fontReset, fontValue) {
            var obj=this;
            //var $target = LECurrentObject;
            //如果当前对象是轮播图，只改变标题的字体
            var $target = LEStyle.getObject(obj.layer);
            if($target==LECurrentObject){
                $target=obj.getTextObject();
            }
            //列表标题
            if($target.parent().hasClass("media-body") && $target.children("li").hasClass("titList")){
                $target=$target.find("a");
            }

            if (_$this.hasClass("select")) {
                $target.css(fontStyle, fontReset);
                _$this.removeClass("select");
            } else {
                $target.css(fontStyle, fontValue);
                _$this.addClass("select");
            }
        },
        //如果是对多图的字体进行设置，设置完毕需要触发调整多图宽高的方法，
        changeGalleryHeight:function (){
            var obj=this;
            var $target = LEStyle.getObject(obj.layer);
            if($target==LECurrentObject){
                $target=obj.getTextObject();
            }
        if($target.parent().hasClass("gallery-title")){
            LE.stylesheets["Gallery"]().changeImageHeight();
        }

    },
        /*设置字体样式
         1、设置加粗、斜体、下划线、删除线；
         2、判断字体是否具有样式为按钮增删背景色class
         */
        initDecorationEvent:function () {
            var obj=this;
            obj.$decorationUl.find("li").bind("click", function () {
                var _$this = $(this);
                //如果当前对象是轮播图，只改变标题的字体

                var $target = LEStyle.getObject(obj.layer);
                if($target==LECurrentObject){
                    $target=obj.getTextObject();
                }

                //列表标题
                if($target.parent().hasClass("media-body") && $target.children("li").hasClass("titList")){
                    $target=$target.find("a");
                }

                var fontStyle = _$this.attr("data-fontstyle");
                var fontValue = _$this.attr("data-value");
                var fontReset = _$this.attr("data-reset");
                var kg = _$this.attr("data-kg");

                if (_$this.attr("data-reset") == "200") {
                    obj.fontAllSty(_$this, fontStyle, fontReset, fontValue);
                } else if (_$this.attr("data-reset") == "normal") {
                    obj.fontAllSty(_$this, fontStyle, fontReset, fontValue);
                } else if (_$this.attr("data-line") == "under") {   // 下划线功能
                    obj.fontAllSty(_$this, fontStyle, fontReset, fontValue);
                } else if (_$this.attr("data-line") == "through") {   // 删除线功能
                    obj.fontAllSty(_$this, fontStyle, fontReset, fontValue);
                }

                if ($target.css("text-decoration") == "underline") {
                    obj.$decorationUl.find('li[data-value="line-through"]').removeClass("select");
                }
                else if ($target.css("text-decoration") == "line-through") {
                    obj. $decorationUl.find('li[data-value="underline"]').removeClass("select");
                }

                $target.find("span").css(fontStyle, "");
                $target.find("span").removeClass();

                obj.changeGalleryHeight();

                LEHistory.trigger();
            });
        },
        /*改变字体*/
        initfontFamilyEvent:function () {
            var obj=this;
            obj.$fontfamilyLi.find("li").bind("click", function () {
                var _$this = $(this);
                //如果当前对象是轮播图，只改变标题的字体
                var $target = LEStyle.getObject(obj.layer);
                if($target==LECurrentObject){
                    $target=obj.getTextObject();
                }

                //列表标题
                if($target.parent().hasClass("media-body") && $target.children("li").hasClass("titList")){
                    $target=$target.find("a");
                }
                var txt = _$this.find("a").attr("value");
                 obj.$fontFm.find("cite").html(txt);
                $target.css('font-family', _$this.find('a').attr("value"));
                $target.find("span").css("font-family", "");

                obj.changeGalleryHeight();

                LEHistory.trigger();
            })
        },
        /*改变字体大小*/
        initfontSizeEvent: function () {
            var obj=this;
            obj.$fontSz.find("li").bind("click", function () {
                var _$this = $(this);  //点击的设置字体样式的按钮

                var $target = LEStyle.getObject(obj.layer);
                if($target==LECurrentObject){
                    $target=obj.getTextObject();
                }
                //列表标题
                if($target.parent().hasClass("media-body") && $target.children("li").hasClass("titList")){
                    $target=$target.find("a");
                }

                /*var txt = _$this.find("a").text();
                 $fontSz.find("cite").html(txt);*/

                $target.css('font-size', _$this.find('a').attr("value") + "px");
                $target.find("span").css("font-size", "");
                $target.attr("data-fontsize", _$this.find("a").text());

                //改变字体大小的同时，行距和首行缩进动态变化
                var _fontFz = parseInt($target.attr("data-fontsize"));
                var _lineHeight=Math.round(_fontFz*1.4);
                var num=obj.$txtIndent.val();
                var _textIndent=_fontFz*num;

                $target.css("line-height",_lineHeight+"px");
                $target.css("text-indent",_textIndent+"px");

                obj.resetLineHeight();
                obj.resettextIndent();

                obj.changeGalleryHeight();

                LEHistory.trigger();
            })
        },
        /*改变对齐方式 */
        initAlignEvent : function () {
            var obj=this;
            obj.$alignUl.find("li").bind("click", function () {
                var _$this = $(this);

                //如果当前对象是轮播图，只改变标题的字体
                var $target = LEStyle.getObject(obj.layer);
                if($target==LECurrentObject){
                    $target=obj.getTextObject();
                }
                $target.css("text-align", _$this.attr("data-align"));
                $(".clearbg").removeClass("select");
                if (_$this.attr("data-align") == "left") {
                    _$this.addClass("select");
                } else if (_$this.attr("data-align") == "center") {
                    _$this.addClass("select");
                } else if (_$this.attr("data-align") == "right") {
                    _$this.addClass("select");
                }

                obj.changeGalleryHeight();

                LEHistory.trigger();
            });
        },
        /*改变颜色*/
        initColorEvent : function () {
            var obj=this;
            obj.$headColorPick.spectrum(
                LEColorPicker.getOptions(function (tinycolor) {
                    var _$this = $(this);

                    //如果当前对象是轮播图，只改变标题的字体
                    var $target = LEStyle.getObject(obj.layer);
                    if($target==LECurrentObject){
                        $target=obj.getTextObject();
                    }

                    //列表标题
                    if($target.parent().hasClass("media-body") && $target.children("li").hasClass("titList")){
                        $target=$target.find("a");
                    }

                    $target.css("color", "#" + tinycolor.toPercentageRgbString());
                    $target.find("span").css("color", "");

                    LEHistory.trigger();
                })
            );
        },
        /*
         为所有的调色器增加属性unselectable="on" onselectstart="return false;" style="-webkit-user-select: none;-moz-user-select: none;" 点击时选中的对象不失去焦点
         */
        addColorPickerAttr : function () {
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
        },
        /*设置文字行距*/
        initLineHeightEvent:function(){
            var obj=this;
            obj.$txtLineHeight.onlyNum().keydown(function (e) {

                var $target = LEStyle.getObject(obj.layer);
                if($target==LECurrentObject){
                    $target=obj.getTextObject();
                }

                //列表标题
                if($target.parent().hasClass("media-body") && $target.children("li").hasClass("titList")){
                    $target=$target.find("a");
                }
                LEKey.numInputKeyDown(e, $(this), $target);
                obj.changeGalleryHeight();
            });
            //失去焦点的时候，置0
            obj.$txtLineHeight.blur(function (e) {

                var $target = LEStyle.getObject(obj.layer);
                if($target==LECurrentObject){
                    $target=obj.getTextObject();
                }
                //列表标题
                if($target.parent().hasClass("media-body") && $target.children("li").hasClass("titList")){
                    $target=$target.find("a");
                }

                LEKey.numInputBlur(e, $(this), $target);
                obj.changeGalleryHeight();
                LEHistory.trigger();
            });

            obj.$txtLineHeight.focus(function (e) {

                var $target = LEStyle.getObject(obj.layer);
                if($target==LECurrentObject){
                    $target=obj.getTextObject();
                }
                //列表标题
                if($target.parent().hasClass("media-body") && $target.children("li").hasClass("titList")){
                    $target=$target.find("a");
                }

                LEKey.numInputFocus(e, $(this), $target);
                obj.changeGalleryHeight();
            });
        },
        /*设置首行缩进*/
        inittextIndentEvent:function(){
            var obj=this;
            obj.$txtIndent.onlyNum().keydown(function (e) {

                var $target = LEStyle.getObject(obj.layer);
                if($target==LECurrentObject){
                    $target=obj.getTextObject();
                }
                LE.stylesheets["MainMenu"]().numInputKeyDownPad(e, $(this), $target);
                obj.changeGalleryHeight();
            });
            //失去焦点的时候，置0
            obj.$txtIndent.blur(function (e) {

                var $target = LEStyle.getObject(obj.layer);
                if($target==LECurrentObject){
                    $target=obj.getTextObject();
                }

                LE.stylesheets["MainMenu"]().numInputBlurPad(e, $(this), $target);
                obj.changeGalleryHeight();
                LEHistory.trigger();
            });

            obj.$txtIndent.focus(function (e) {


                var $target = LEStyle.getObject(obj.layer);
                if($target==LECurrentObject){
                    $target=obj.getTextObject();
                }

                LE.stylesheets["MainMenu"]().numInputFocusPad(e, $(this), $target);
                obj.changeGalleryHeight();
            });
        },
        /*初始化首行缩进*/
        resettextIndent :function () {
            var obj=this;

            var $target = LEStyle.getObject(obj.layer);
            if($target==LECurrentObject){
                $target=obj.getTextObject();
            }

            var _ref = parseInt($target.css("text-indent"));
            var _fontSize=parseInt($target.css("font-size"));
            var num=_ref/_fontSize;
            if(isNaN(num)){
                obj.$txtIndent.val("0");
            }else{
                obj.$txtIndent.val(num);
            }

        },
        /*初始化文字行间距*/
        resetLineHeight:function () {
            var obj=this;

            var $target = LEStyle.getObject(obj.layer);
            if($target==LECurrentObject){
                $target=obj.getTextObject();
            }
            //列表标题
            if($target.parent().hasClass("media-body") && $target.children("li").hasClass("titList")){
                $target=$target.find("a");
            }
            var _ref = parseInt($target.css("line-height"));
            if(isNaN(_ref)){
                obj.$txtLineHeight.val("17px");
            }else{
                obj.$txtLineHeight.val(_ref+"px");
            }

        },
        /*获得当前对象的对齐值，并且初始化到按钮上面*/
        resetDecorationStyle :function () {
            var obj=this;

            var $target = LEStyle.getObject(obj.layer);
            if($target==LECurrentObject){
                $target=obj.getTextObject();
            }

            //列表标题
            if($target.parent().hasClass("media-body") && $target.children("li").hasClass("titList")){
                $target=$target.find("a");
            }
            var _textFw = $target.css("font-weight");
            var _textFs = $target.css("font-style");
            var _textTd = $target.css("text-decoration");
            if (_textFw == "bold") {
                obj.$decorationUl.find('li[data-value = "700"]').addClass("select");
            } else {
                obj.$decorationUl.find('li[data-value = "700"]').removeClass("select");
            }

            if (_textFs == "italic") {
                obj.$decorationUl.find('li[data-value="italic"]').addClass("select");
            } else {
                obj.$decorationUl.find('li[data-value="italic"]').removeClass("select");
            }

            if (_textTd == "underline") {
                obj.$decorationUl.find('li[data-value="underline"]').addClass("select");
            } else {
               obj.$decorationUl.find('li[data-value="underline"]').removeClass("select");
            }

            if (_textTd == "line-through") {
                obj.$decorationUl.find('li[data-value="line-through"]').addClass("select");
            } else {
                obj.$decorationUl.find('li[data-value="line-through"]').removeClass("select");
            }
        },
        /*初始化按钮显示字体样式*/
        resetfontFamilyStyle:function () {
            var obj=this;

            var $target = LEStyle.getObject(obj.layer);
            if($target==LECurrentObject){
                $target=obj.getTextObject();
            }
            //列表标题
            if($target.parent().hasClass("media-body") && $target.children("li").hasClass("titList")){
                $target=$target.find("a");
            }
            var _fontFm = $target.css("font-family");
            obj.$fontFm.find("cite").text(_fontFm);
        },
        /*初始化按钮显示字体大小*/
        resetfontSizeStyle:function () {
            var obj=this;

            var $target = LEStyle.getObject(obj.layer);
            if($target==LECurrentObject){
                $target=obj.getTextObject();
            }

            //列表标题
            if($target.parent().hasClass("media-body") && $target.children("li").hasClass("titList")){
                $target=$target.find("a");
            }

            if ($target.attr("data-fontsize")) {
                var _fontFz = $target.attr("data-fontsize");
                //var _fontFz=$target.data("fontsize"); 这种读法有问题！！缓存
                obj.$fontSz.find("cite").text(_fontFz);
            } else {
                /*$("#fontSiz").find("cite").text("小五");*/
                obj.$fontSz.find("cite").text("12px");
            }
        },
        /*获得当前对象的对齐值，并且初始化到按钮上面*/
        resetAlignStyle:function () {
            var obj=this;

            var $target = LEStyle.getObject(obj.layer);
            if($target==LECurrentObject){
                $target=obj.getTextObject();
            }
            //导航不显示文字颜色和居中设置
            /*if($target.hasClass("le-nav")){
                obj.$alignUl.hide();
                obj.$textColorEditor.hide();
            }else{
                obj.$alignUl.show();
                obj.$textColorEditor.show();
            }*/
            if($target.parent().hasClass("nav")||$target.parent().hasClass("dropdown-menu")){
                obj.$alignUl.hide();
                obj.$textColorEditor.hide();
            }else{
                obj.$alignUl.show();
                obj.$textColorEditor.show();
            }
            var _align = $target.css("text-align") || "left";
            $(".clearbg").removeClass("select selected");
            obj.$alignUl.find("li[data-align=" + _align + "]").addClass("select selected");
        },
        /*颜色初始化*/
        resetColor:function(){
            //如果当前对象是轮播图，只改变标题的字体
            var obj=this;

            var $target = LEStyle.getObject(obj.layer);
            if($target==LECurrentObject){
                $target=obj.getTextObject();
            }
            //列表标题
            if($target.parent().hasClass("media-body") && $target.children("li").hasClass("titList")){
                $target=$target.find("a");
            }
            var contenteditable = $target.attr("contenteditable")
                && $target.attr("contenteditable") == "true" ? true : false;
            if (!contenteditable) {
                obj.$headColorPick.spectrum("set", $target.css("color"));
            }
        }
    }
})(window, jQuery, LE, undefined);