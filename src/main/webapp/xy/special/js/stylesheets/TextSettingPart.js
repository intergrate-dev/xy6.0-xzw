/**
 * Created by isaac_gu on 2016/1/14.
 * LEHistory.trigger();//撤销方法
 */
(function (window, $, LE) {
    LE.stylesheets["TextSettingPart"] = function () {
        var $PS = $("#textSection");
        var $partDecorationUl = $("#partSt");
        var $partFamilyLi = $("#partFamily");
        var $partSz = $("#partSize");
        var $hoverUnder = $("#hoverUnder");
        var $hoverColor = $("#hoverColor");
        var $linkUnder = $("#linkUnder");
        var $linkColor = $("#linkColor");
        var bookmark;

        // 获取对象区分轮播图、多图、文字
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
        var initPartDecorationEventOld = function () {
            $partDecorationUl.find("li").bind("click", function () {
                var _$this = $(this);
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
                LEHistory.trigger();
            });
        };

        var initPartDecorationEvent = function () {
            $partDecorationUl.find("li").bind("click", function () {
                var _$this = $(this);
                var $target = LECurrentObject;
                var contenteditable = $target.attr("contenteditable") && $target.attr("contenteditable") == "true" ? true : false;
                if (contenteditable) {
                    if (_$this.data("reset") == "200") {
                        document.execCommand("Bold");
                    }
                    else if (_$this.data("reset") == "normal") {//局部斜体
                        document.execCommand("Italic");
                    }
                    else if (_$this.data("line") == "under") { //局部下划线
                        document.execCommand("Underline");
                    }
                    else if (_$this.data("line") == "through") { //局部删除线
                        document.execCommand("StrikeThrough");
                    }
                }
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
                    $(".throughFont").removeClass("selecter");
                }
                _$this.addClass("selecter");
                _$this.attr("data-kg", "false");
            } else {
                FstyleP(fontStyle, fontReset);
                _$this.removeClass("selecter");
                _$this.attr("data-kg", "true");
            }
                //恢复下划线、删除线kg=true状态data-kg=true状态
            if($("#partUnder").hasClass("selecter")){
                $("#partThrough").attr("data-kg", "true");
            }else  if($("#partThrough").hasClass("selecter")){
                $("#partUnder").attr("data-kg", "true");
            }
        };

        /*改变字体*/
        var initPartFamilyEvent = function () {
            $partFamilyLi.find("li").bind("click", function () {
                var _$this = $(this);
                var $target=getObject();

                var contenteditable = $target.attr("contenteditable")
                    && $target.attr("contenteditable") == "true" ? true : false;
               /* var txt = _$this.find("a").attr("value");
                $("#fontFm").find("cite").html(txt);*/
                if (contenteditable) {
                    FstyleP("font-family", _$this.find('a').attr("value"));
                }

                LEHistory.trigger();
            })
        };

        /*改变字体大小*/
        var initPartSizeEvent = function () {
            $partSz.find("li").bind("click", function () {
                var _$this = $(this);  //点击的设置字体样式的按钮
                var $target=getObject();
                var contenteditable = $target.attr("contenteditable")
                    && $target.attr("contenteditable") == "true" ? true : false;
                /*var txt = _$this.find("a").text();
                $partSz.find("cite").html(txt);*/
                if (contenteditable) {
                    FstyleP("font-size", _$this.find('a').attr("value") + "px")
                }
                //改变字体大小的同时，行距和首行缩进动态变化
                /*var _fontFz = parseInt($target.attr("data-fontsize"));
                var _lineHeight=Math.round(_fontFz*1.4);
                var num=$("#txtIndent").val();
                var _textIndent=_fontFz*num;

                $target.css("line-height",_lineHeight+"px");
                $target.css("text-indent",_textIndent+"px");*/

                LEHistory.trigger();
            })
        };

        /*局部对齐方式 */
        var initPartAlignEvent = function(){
            $("#partAlign").find("li").click(function () {
                var _this=$(this);
                var alignStyle=_this.attr("data-align");
                document.execCommand(alignStyle,false,null);
                LEHistory.trigger();
            });
         };

        //清除格式
        var initFormatEvent=function(){
            $("#formatBlock").click(function () {
                document.execCommand("RemoveFormat");
                document.execCommand("Unlink");
                LEHistory.trigger();
            });
        };

        /*改变颜色*/
        var initPartColorEvent = function () {
            $("#partColorPick").spectrum(
                LEColorPicker.getOptions(function (tinycolor) {
                    var _$this = $(this);
                    var $target=getObject();
                    var contenteditable = $target.attr("contenteditable")
                        && $target.attr("contenteditable") == "true" ? true : false;
                    if (contenteditable) {
                        FstyleP("color", "#" + tinycolor.toPercentageRgbString())
                    }
                    LEHistory.trigger();
                })
            );
        };

        /*
         对style标签进行操作 调用了StyleManager对象的setSty方法实现增加或修改styl标签内容
         */
        function changeLinkStyle(partSelector, sccName, cssValue) {
            var id = LECurrentObject.attr("id");
            var selector = "#" + id + " " + partSelector;
            StyleManager.setStyle(selector, sccName, cssValue);
        }

        /*超链接*/
        var initPartLinkEvent=function(){
            $("#partLink").click(function(){
                var $target = LECurrentObject;
                $(".partlink-box").fadeToggle(100);
                bookmark = rangy.getSelection().getBookmark($target[0]);
            });

            $("#partLinkBtn").click(function () {
                var $target = LECurrentObject;
                var contenteditable = $target.attr("contenteditable") && $target.attr("contenteditable") == "true" ? true : false;
                if (contenteditable) {
                   /* var bookmark;
                    bookmark = rangy.getSelection().getBookmark($target[0]);
                    $target.focus();
                    rangy.getSelection().moveToBookmark(bookmark);*/
                    //a链接的href值
                    rangy.getSelection().moveToBookmark(bookmark);
                    var _openNew  = $("#openNewWindowBtn").is(":checked");
                    document.execCommand("CreateLink", false, $("#partLinkUrl").val() + ( _openNew ? "#le-link-selected" : ""));
                    if( _openNew ){
                        $target.find("[href$='#le-link-selected']").attr("target", "_blank").attr("href", $("#partLinkUrl").val());
                    }
                }

                changeLinkStyle("a", "text-decoration", $linkUnder.attr("data-value"));
                changeLinkStyle("a", "color", $linkColor.val());
                changeLinkStyle("a:hover", "text-decoration", $hoverUnder.attr("data-value"));
                changeLinkStyle("a:hover", "color", $hoverColor.val());
                $(".partlink-box").fadeOut(100);
               /* resetLinkColorStatus();
                resetLinkDecStatus();
                resetHoverColorStatus();
                resetHoverDecStatus();*/
                LEHistory.trigger();
            });

            //取消超链接
            $("#partunlink").click(function () {
                document.execCommand("Unlink");
                LEHistory.trigger();
            });

        };

        /*初始化按钮*/
        var resetPartTextEvent = function () {
            $("#partFamilyBox").find("cite").html("字体");
            $("#partSize").find("cite").html("字号");
        };


        return {
            init: function () {
                rangy.init();
                initPartDecorationEvent();
                initPartFamilyEvent();
                initPartSizeEvent();
                initPartAlignEvent();
                initPartColorEvent();
                initPartLinkEvent();
                initFormatEvent();
            },
            run: function (options, doHide, doSlide) {
                resetPartTextEvent();
                //切换组件时，样式设置部分回顶部
                sliderbarToTop();
                /*var $target = LECurrentObject;
                $target = options.object;*/
                LEDisplay.show($PS, doHide, doSlide);
            },
            destroy: function () {
                $partDecorationUl.find("li").removeClass("selecter").removeClass("selected");
                $PS.hide();
                //$("div[contenteditable=true]").attr("contenteditable", false).removeClass("plugin_entity_disabled").addClass("plugin_entity");
            }
        };
    };
})(window, jQuery, LE, undefined);