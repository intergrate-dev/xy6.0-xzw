/**
 * Created by qianxm on 2016/12/21.
 * LEHistory.trigger();//撤销方法
 */
(function (window, $, LE) {
    LE.stylesheets["css-TextSettingWhole"] = function(){
        var $PC = $("#css-textSection");
        return {
            init: function(){
                LE.oCssTextSetting = new OTextSetting($PC,0);
                LE.oCssTextSetting.addColorPickerAttr();
            },
            run: function(options, doHide, doSlide){
                LE.oCssTextSetting.reset();
                LEDisplay.show($PC, doHide, doSlide);
            },
            destroy: function(){
                LE.oCssTextSetting.destroy();
            }
        };
    };

    LE.stylesheets["css-TextSettingWholeIII"] = function(){
        var $PC = $("#css-textSection-III");
        return {
            init: function(){
                LE.oCssTextSettingIII = new OTextSetting($PC,2);
                LE.oCssTextSettingIII.addColorPickerAttr();
            },
            run: function(options, doHide, doSlide){
                LE.oCssTextSettingIII.reset();
                LEDisplay.show($PC, doHide, doSlide);
            },
            destroy: function(){
                LE.oCssTextSettingIII.destroy();
            }
        };
    };

    function OTextSetting($PC, layer){
        this.$pc = $PC;
        this.layer = layer;
        this.$decorationUl= this.$pc.find("[id^=css-fontSt]");
        this.$fontfamilyLi = this.$pc.find("[id^=css-fontFa]");
        this.$fontSz = this.$pc.find("[id^=css-fontSiz]");
        this.$alignUl = this.$pc.find("[id^=css-alignUl]");
        this.$txtLineHeight = this.$pc.find("[id^=css-txtLineHeight]");
        this.$txtIndent = this.$pc.find("[id^=css-txtIndent]");
        this.$headColorPick=this.$pc.find("[id^=css-headColorPick]");
        this.$textColorEditor=this.$pc.find("[id^=css-textColorEditor]");
        this.$fontFm=this.$pc.find("[id^=css-fontFm]");

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
        /*getTextObject :function(){
            var $target = LECurrentObject;
            if($target.hasClass("le-carousel")){
                $target = LECurrentObject.find(".carousel-caption").find("h4");
            }else if($target.hasClass("le-gallery")){
                $target = LECurrentObject.find("p").children();
            }else{
                $target = LECurrentObject;
            }
            return $target;
        },*/
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
            /*if($target==LECurrentObject){
                $target=obj.getTextObject();
            }*/
            //列表标题文字
            if($target.parent().hasClass("media-body") && $target.children("li").hasClass("titList")){
                $target=$target.find("a");
            }

            if (_$this.hasClass("select")) {
                //$target.css(fontStyle, fontReset);
                obj.cssSetTargetPosition(fontStyle,fontReset);
                _$this.removeClass("select");
            } else {
                //$target.css(fontStyle, fontValue);
                obj.cssSetTargetPosition(fontStyle,fontValue);
                _$this.addClass("select");
            }
        },
        //如果是对多图的字体进行设置，设置完毕需要触发调整多图宽高的方法，
        changeGalleryHeight:function (){
            var obj=this;
            var $target = LEStyle.getObject(obj.layer);
            /*if($target==LECurrentObject){
                $target=obj.getTextObject();
            }*/
            //if($target.parent().hasClass("gallery-title")){
            if($target.hasClass("le-gallery")){
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
               /* if($target==LECurrentObject){
                    $target=obj.getTextObject();
                }*/

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

                var _value = obj.getValue($target,"text-decoration");
                //var _value=$target.css("text-decoration");
                if ( _value== "underline") {
                    obj.$decorationUl.find('li[data-value="line-through"]').removeClass("select");
                }else if (_value == "line-through") {
                    obj. $decorationUl.find('li[data-value="underline"]').removeClass("select");
                }

                $target.find("span").css(fontStyle, "");
                /*$target.find("span").removeClass();*/

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
                /*if($target==LECurrentObject){
                    $target=obj.getTextObject();
                }*/

                //列表标题
                if($target.parent().hasClass("media-body") && $target.children("li").hasClass("titList")){
                    $target=$target.find("a");
                }
                var txt = _$this.find("a").attr("value");
                 obj.$fontFm.find("cite").html(txt);
                //$target.css('font-family', _$this.find('a').attr("value"));
                obj.cssSetTargetPosition('font-family',txt);
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
               /* if($target==LECurrentObject){
                    $target=obj.getTextObject();
                }*/
                //列表标题
                if($target.parent().hasClass("media-body") && $target.children("li").hasClass("titList")){
                    $target=$target.find("a");
                }

                /*var txt = _$this.find("a").text();
                 $fontSz.find("cite").html(txt);*/

                //$target.css('font-size', _$this.find('a').attr("value") + "px");
                var _text=_$this.find('a').attr("value") + "px";
                obj.cssSetTargetPosition('font-size',_text);
                $target.find("span").css("font-size", "");
                //$target.attr("data-fontsize", _$this.find("a").text());
                //改变字体大小的同时，行距和首行缩进动态变化
//                var _fontFz = parseInt($target.attr("data-fontsize"));
                var _fontFz = parseInt(_text);
                var _lineHeight=Math.round(_fontFz*1.4);
                var num=obj.$txtIndent.val();
                var _textIndent=_fontFz*num;

               /* $target.css("line-height",_lineHeight+"px");
                $target.css("text-indent",_textIndent+"px");*/
                obj.cssSetTargetPosition("line-height",_lineHeight+"px");
                obj.cssSetTargetPosition("text-indent",_textIndent+"px");

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
               /* if($target==LECurrentObject){
                    $target=obj.getTextObject();
                }*/
                //$target.css("text-align", _$this.attr("data-align"));
                obj.cssSetTargetPosition("text-align",_$this.attr("data-align"));
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
                    /*if($target==LECurrentObject){
                        $target=obj.getTextObject();
                    }*/

                    //列表标题
                    if($target.parent().hasClass("media-body") && $target.children("li").hasClass("titList")){
                        $target=$target.find("a");
                    }

                    //$target.css("color", "#" + tinycolor.toPercentageRgbString());
                    obj.cssSetTargetPosition("color",tinycolor.toPercentageRgbString());
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
                /*if($target==LECurrentObject){
                    $target=obj.getTextObject();
                }*/

                //列表标题
                if($target.parent().hasClass("media-body") && $target.children("li").hasClass("titList")){
                    $target=$target.find("a");
                }
                //LEKey.numInputKeyDown(e, $(this), $target);
                obj.cssNumInputKeyDown(e, $(this), $target);
                obj.changeGalleryHeight();
            });
            //失去焦点的时候，置0
            obj.$txtLineHeight.blur(function (e) {

                var $target = LEStyle.getObject(obj.layer);
                /*if($target==LECurrentObject){
                    $target=obj.getTextObject();
                }*/
                //列表标题
                if($target.parent().hasClass("media-body") && $target.children("li").hasClass("titList")){
                    $target=$target.find("a");
                }

                //LEKey.numInputBlur(e, $(this), $target);
                obj.cssNumInputBlur(e, $(this), $target);
                obj.changeGalleryHeight();
                LEHistory.trigger();
            });

            obj.$txtLineHeight.focus(function (e) {

                var $target = LEStyle.getObject(obj.layer);
                /*if($target==LECurrentObject){
                    $target=obj.getTextObject();
                }*/
                //列表标题
                if($target.parent().hasClass("media-body") && $target.children("li").hasClass("titList")){
                    $target=$target.find("a");
                }

                //LEKey.numInputFocus(e, $(this), $target);
                obj.cssNumInputFocus(e, $(this), $target);
                obj.changeGalleryHeight();
            });
        },
        cssSetTargetPosition:function(attr, dis){
            var obj = this;
            var $target = LEStyle.getObject(this.layer);
            if(attr!="text-indent" && attr!="text-align"){//缩进需要加在文字的
                //列表标题文字
                if($target.parent().hasClass("media-body") && $target.children("li").hasClass("titList")){
                    $target=$target.find("a");
                }
            }

            //把对应的样式写到css文件
            var partSelector=obj.getPartSelector($target);
            var selector=obj.setCssStyle(partSelector,attr, dis);
            return selector;
        },
        getPartSelector:function($target){
            var partSelector;
            if($target.hasClass("le-list-group")){ //列表最外层
                partSelector="";
            }else if($target.hasClass("list-group-item")){ //每一行
                partSelector=".list-group-item";
            }else if($target.parent().hasClass("media-body") && $target.children("li").hasClass("titList")){ //标题缩进
                partSelector=".list-group-item > .media > .media-body > ul";
            }else if($target.hasClass("listTitle")){//标题文字
                partSelector=".list-group-item > .media > .media-body > ul > .titList .listTitle";
            }else if($target.hasClass("listSummaryBox")){//摘要
                partSelector=".list-group-item > .media > .media-body > .listSummary > .listSummaryBox";
            }else if($target.hasClass("listSourceDetailedStyle")){//来源
                partSelector=".list-group-item .listSourceDetailedStyle";
            }else if($target.hasClass("listTimeDetailedStyle")){//时间
                partSelector=".list-group-item .listTimeDetailedStyle";
            }else if($target.hasClass("moreListShow")){//右侧链接
                partSelector=".moreListShow";
            }else if($target.hasClass("le-carousel")){ //轮播图文字
                partSelector=".carousel-caption > h4";
            }else if($target.hasClass("le-gallery")){ //多图文字
                partSelector=".col-md-3 .gallery-title > .gallery-title-text";
            }
            return partSelector;
        },
        setCssStyle:function(partSelector, sccName, cssValue){
            /*
             对style标签进行操作 调用了StyleManager对象的setSty方法实现增加或修改styl标签内容
             */
            var id = LECurrentObject.attr("id");
            var selector = $.trim("#" + id + " " + partSelector);
            StyleManager.setStyle(selector, sccName, cssValue);
            return selector;
        },
        cssNumInputKeyDown:function(event, $this, $target){
            var obj=this;
            //如果是上下的话，执行以下的操作
            if(event.keyCode == 38 || event.keyCode == 40 || event.keyCode == 13){
                var _value = $this.val();
                var num = parseInt(_value);
                if(isNaN(num)) return;

                obj.checkUnit(_value, $this);

                var _unit = $this.attr("data-unit") || "px";
                //点击上下按钮，来改变当前的值
                event.keyCode == 38 && ++num;
                event.keyCode == 40 && --num;
                //设定目标的css样式
                //$target.css($this.attr("data-ref"), num + _unit);
                obj.cssSetTargetPosition($this.attr("data-ref"), num + _unit);
                $this.val(num + _unit);
                return num;
            }
            return null;
        },
        cssNumInputBlur:function(event, $this, $target){
            var obj=this;
            var _value = $this.val();
            $.trim(_value) == "" && $this.val($this.data("dv"));
            if($.trim(_value) == "")  return;
            obj.checkUnit(_value, $this);
            var num = parseInt(_value);
            var _unit = $this.attr("data-unit") || "px";
            /*$target.css($this.attr("data-ref"), num + _unit);
             $this.val(num + _unit);*/
            obj.cssSetTargetPosition($this.attr("data-ref"), num + _unit);
            $this.val(num + _unit);
        },
        cssNumInputFocus:function(event, $this, $target){
            var _value=$target.css($this.attr("data-ref"));
            $this.attr("data-dv",_value);
        },
        checkUnit:function(_value, $this){
            //如果包含px，就以px为主
            if(_value.indexOf("px") != -1){
                $this.attr("data-unit", "px");
            } else if(_value.indexOf("%") != -1){
                $this.attr("data-unit", "%");
            }
        },
        cssNumInputKeyDownPad: function (event, $this, $target) {//设置首行缩进
            var obj=this;
            //如果是上下的话，执行以下的操作
            if (event.keyCode == 38 || event.keyCode == 40 || event.keyCode == 13) {
                var num = parseInt($this.val());
                if (isNaN(num)) return;
                var _unit = $this.attr("data-unit") || "px";
                //点击上下按钮，来改变当前的值
                event.keyCode == 38 && ++num;
                event.keyCode == 40 && --num;
                if ($this.attr("data-ref") == "padding-left") {
                    $target.css({"padding-left": num + _unit, "padding-right": num + _unit});
                    $this.val(num + _unit);
                } else if($this.attr("data-ref") == "text-indent"){
                    //文字首行缩进的计算 获取字体大小时区分轮播、多图及文字组件
                    var _target = LECurrentObject;
                    if(_target.hasClass("le-carousel")){
                        _target = LECurrentObject.find(".carousel-caption").find("h4");
                    }else if(_target.hasClass("le-gallery")){
                        _target = LECurrentObject.find("p").children();
                    }else{
                        _target = LECurrentObject;
                    }

                    var fontSize = obj.getValue($target,"font-size");
                    fontSize=parseInt(fontSize);
                    //var fontSize=parseInt(_target.css("font-size"));
                    //$target.css($this.data("ref"), num*fontSize+"px");
                    obj.cssSetTargetPosition($this.attr("data-ref"), num*fontSize+"px");
                    $this.val(num);

                }else {
                    $target.css($this.data("ref"), num + _unit);
                    $this.val(num + _unit);
                }

                return num;
            }
            return null;
        },
        cssNumInputBlurPad: function (event, $this, $target) {
            var obj=this;
            $.trim($this.val()) == "" && $this.val($this.data("dv"));
            if ($.trim($this.val()) == "")  return;
            var num = parseInt($this.val());
            var _unit = $this.attr("data-unit") || "px";
            if ($this.attr("data-ref") == "padding-left") {
                $target.css({"padding-left": num + _unit, "padding-right": num + _unit});
                $this.val(num + _unit);
            } else if($this.attr("data-ref") == "text-indent"){
                //文字首行缩进的计算 获取字体大小时区分轮播、多图及文字组件
                var _target = LECurrentObject;
                if(_target.hasClass("le-carousel")){
                    _target = LECurrentObject.find(".carousel-caption").find("h4");
                }else if(_target.hasClass("le-gallery")){
                    _target = LECurrentObject.find("p").children();
                }else{
                    _target = LECurrentObject;
                }
                var fontSize = obj.getValue($target,"font-size");
                fontSize=parseInt(fontSize);
                //var fontSize=parseInt(_target.css("font-size"));
                //$target.css($this.data("ref"), num*fontSize+"px");
                obj.cssSetTargetPosition($this.attr("data-ref"), num*fontSize+"px");
                $this.val(num);
            }else {
                $target.css($this.data("ref"), num + _unit);
                $this.val(num + _unit);
            }

        },
        cssNumInputFocusPad: function (event, $this, $target) {
            var _value=$target.css($this.attr("data-ref"));
            $this.attr("data-dv",_value);
        },
        /*设置首行缩进*/
        inittextIndentEvent:function(){
            var obj=this;
            obj.$txtIndent.onlyNum().keydown(function (e) {

                var $target = LEStyle.getObject(obj.layer);
                /*if($target==LECurrentObject){
                    $target=obj.getTextObject();
                }*/
                //LE.stylesheets["MainMenu"]().numInputKeyDownPad(e, $(this), $target);
                obj.cssNumInputKeyDownPad(e, $(this), $target);
                obj.changeGalleryHeight();
            });
            //失去焦点的时候，置0
            obj.$txtIndent.blur(function (e) {

                var $target = LEStyle.getObject(obj.layer);
                /*if($target==LECurrentObject){
                    $target=obj.getTextObject();
                }
*/
                //LE.stylesheets["MainMenu"]().numInputBlurPad(e, $(this), $target);
                obj.cssNumInputBlurPad(e, $(this), $target);
                obj.changeGalleryHeight();
                LEHistory.trigger();
            });

            obj.$txtIndent.focus(function (e) {

                var $target = LEStyle.getObject(obj.layer);
                /*if($target==LECurrentObject){
                    $target=obj.getTextObject();
                }*/

                //LE.stylesheets["MainMenu"]().numInputFocusPad(e, $(this), $target);
                obj.cssNumInputFocusPad(e, $(this), $target);
                obj.changeGalleryHeight();
            });
        },
        /*初始化首行缩进*/
        resettextIndent :function () {
            var obj=this;
            var $target = LEStyle.getObject(obj.layer);
            /*if($target==LECurrentObject){
                $target=obj.getTextObject();
            }*/
            /**！！！初始化标题文字缩进值时，font-size从a标签取值，text-indent从a标签向上的父元素ul上取值 ，设置该字体样式时同理*/
            var _indent = obj.getValue($target,"text-indent");
            var _fontSize=obj.getValue($target,"font-size");

             _indent = parseInt(_indent);
             _fontSize=parseInt(_fontSize);
            var num=_indent/_fontSize;
            if(isNaN(num)){
                obj.$txtIndent.val("0");
            }else{
                obj.$txtIndent.val(num);
            }

        },
        getValue:function($target,cssName){
            if(cssName!="text-indent" && cssName!="text-align"){//缩进需要加在文字的向上父元素ul上
                //列表标题文字
                if($target.parent().hasClass("media-body") && $target.children("li").hasClass("titList")){
                    $target=$target.find("a");
                }
            }
            var obj=this;
            var _partSelect=obj.getPartSelector($target);
            var id = LECurrentObject.attr("id");
            var _selector = $.trim("#" + id + " " + _partSelect);
            var _indent=StyleManager.getStyle(_selector,cssName);
            if(_partSelect==".carousel-caption > h4"){ //轮播文字
                $target=$target.find(_partSelect);
            }
            _indent = _indent == "none" ? $target.css(cssName) : StyleManager.getStyle(_selector,cssName);
            return _indent

        },
        /*初始化文字行间距*/
        resetLineHeight:function () {
            var obj=this;

            var $target = LEStyle.getObject(obj.layer);
            /*if($target==LECurrentObject){
                $target=obj.getTextObject();
            }*/
            //列表标题
            if($target.parent().hasClass("media-body") && $target.children("li").hasClass("titList")){
                $target=$target.find("a");
            }

            //var _lineHeight=$target.css("line-height");
            var _lineHeight = obj.getValue($target,"line-height");
            var _ref = parseInt(_lineHeight);
            if(isNaN(_ref)){
                obj.$txtLineHeight.val("17px");
            }else{
                obj.$txtLineHeight.val(_ref+"px");
            }

        },
        /*获得当前对象的字体样式，并且初始化到按钮上面*/
        resetDecorationStyle :function () {
            var obj=this;

            var $target = LEStyle.getObject(obj.layer);
            /*if($target==LECurrentObject){
                $target=obj.getTextObject();
            }*/

            //列表标题
            if($target.parent().hasClass("media-body") && $target.children("li").hasClass("titList")){
                $target=$target.find("a");
            }

            var _textFw = obj.getValue($target,"font-weight");
            var _textFs = obj.getValue($target,"font-style");
            var _textTd = obj.getValue($target,"text-decoration");
           /* var _textFw = $target.css("font-weight");
            var _textFs = $target.css("font-style");
            var _textTd = $target.css("text-decoration");*/
            if (_textFw == "700") {
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
            /*if($target==LECurrentObject){
                $target=obj.getTextObject();
            }*/
            //列表标题
            if($target.parent().hasClass("media-body") && $target.children("li").hasClass("titList")){
                $target=$target.find("a");
            }

            var _fontFm = obj.getValue($target,"font-family");
            //var _fontFm = $target.css("font-family");
            obj.$fontFm.find("cite").text(_fontFm);
        },
        /*初始化按钮显示字体大小*/
        resetfontSizeStyle:function () {
            var obj=this;

            var $target = LEStyle.getObject(obj.layer);
            /*if($target==LECurrentObject){
                $target=obj.getTextObject();
            }*/

            //列表标题
            if($target.parent().hasClass("media-body") && $target.children("li").hasClass("titList")){
                $target=$target.find("a");
            }

            var _fontFz = obj.getValue($target,"font-size");
            if (_fontFz) {
                obj.$fontSz.find("cite").text(_fontFz);
            } else {
                obj.$fontSz.find("cite").text("12px");
            }
        },
        /*获得当前对象的对齐值，并且初始化到按钮上面*/
        resetAlignStyle:function () {
            var obj=this;

            var $target = LEStyle.getObject(obj.layer);
            /*if($target==LECurrentObject){
                $target=obj.getTextObject();
            }*/
            //导航不显示文字颜色和居中设置
            if($target.parent().hasClass("nav")||$target.parent().hasClass("dropdown-menu")){
                obj.$alignUl.hide();
                obj.$textColorEditor.hide();
            }else{
                obj.$alignUl.show();
                obj.$textColorEditor.show();
            }

            var _align = obj.getValue($target,"text-align");
            _align = _align || "left";
            $(".clearbg").removeClass("select selected");
            obj.$alignUl.find("li[data-align=" + _align + "]").addClass("select selected");
        },
        /*颜色初始化*/
        resetColor:function(){
            //如果当前对象是轮播图，只改变标题的字体
            var obj=this;

            var $target = LEStyle.getObject(obj.layer);
           /* if($target==LECurrentObject){
                $target=obj.getTextObject();
            }*/
            //列表标题
            if($target.parent().hasClass("media-body") && $target.children("li").hasClass("titList")){
                $target=$target.find("a");
            }
            var contenteditable = $target.attr("contenteditable")
                && $target.attr("contenteditable") == "true" ? true : false;

            var _color = obj.getValue($target,"color");
            //_color=$target.css("color");
            if (!contenteditable) {
                obj.$headColorPick.spectrum("set",_color);
            }
        }
    }
})(window, jQuery, LE, undefined);