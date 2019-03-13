/**
 * Created by isaac_gu on 2016/1/14.
 */
(function (window, $, LE) {
    LE.stylesheets["ActiveTextStyle"] = function () {
        //当前功能面板的对象
        var $PS=$("#textSection-style");
        var $PSTab=$("#panel-III-styleTab");
        var $fontFmStyle=$("#fontFm-style");
        var $fmCite=$fontFmStyle.find("cite");
        var $fontSizStyle=$("#fontSiz-style");
        var $fsCite=$fontSizStyle.find("cite");
        var $headColorPickStyle=$("#headColorPick-style");
        var $fontBoldStyle=$("#fontBold-style");
        var $fontItalicStyle=$("#fontItalic-style");
        var $fontUnderStyle=$("#fontUnder-style");
        var $fontThroughStyle=$("#fontThrough-style");

        var $styleNormal = $("#style-normal");
        var $styleHover = $("#style-hover");
        var $styleClick= $("#style-click");

        /*单击普通、悬停、点击三个按钮时，通过data-0、data-1、data-2记录状态*/
       /* var initStyleTabEvent = function () {
            $("#styletab-Ul").find("li").bind("click", function () {
                var _this=$(this);
                _this.addClass("activt").siblings().removeClass("activt");
                $fmCite.html($fmCite.attr("data-" + _this.index()));
                $fsCite.html($fsCite.attr("data-" + _this.index()));
                $headColorPickStyle.spectrum("set",$headColorPickStyle.attr("data-" + _this.index()));
                tabResetFmStatue(_this,$fontBoldStyle);
                tabResetFmStatue(_this,$fontItalicStyle);
                tabResetFmStatue(_this,$fontUnderStyle);
                tabResetFmStatue(_this,$fontThroughStyle);
                //LECurrentObject.trigger("click");
            });

        };*/

        /*单击普通、悬停、点击三个按钮时，通过data-0、data-1、data-2记录状态*/
        var resetTextStyleButton=function(_this){
            _this.addClass("activt").siblings().removeClass("activt");
            $fmCite.html($fmCite.attr("data-" + _this.index()));
            $fsCite.html($fsCite.attr("data-" + _this.index()));
            $headColorPickStyle.spectrum("set",$headColorPickStyle.attr("data-" + _this.index()));
            tabResetFmStatue(_this,$fontBoldStyle);
            tabResetFmStatue(_this,$fontItalicStyle);
            tabResetFmStatue(_this,$fontUnderStyle);
            tabResetFmStatue(_this,$fontThroughStyle);
            //LECurrentObject.trigger("click");
        };

        /*对加粗、倾斜、下划线、删除线状态记录的封装*/
        function tabResetFmStatue(_this,$trigger){
            var _value = $trigger.attr("data-value");
            if($trigger.attr("data-" + _this.index())==_value){
                $trigger.addClass("select");
            } else {
                $trigger.removeClass("select");
            }
        }

        /*对style标签进行操作 调用了StyleManager对象的setSty方法实现增加或修改styl标签内容*/

        function changeMainStyle(partSelector, sccName, cssValue) {
            var id = LECurrentObject.attr("id");
            var selector = "#" + id + " " + partSelector;
            StyleManager.setStyle(selector, sccName, cssValue);
        }

        /*设置字体font-family、font-size ，区分不同状态的a标签进行设置*/
        var initFontEvent = function (selector,dataBox,styleName,unit) {
            $(selector).find("li").bind("click",function(){
                var _$this = $(this);
                var txt = _$this.find("a").attr("value")+unit;
                dataBox.html(txt);
                if ($styleNormal.hasClass("activt")) {
                    dataBox.attr("data-0", txt);
//
                    writeStyle(styleName, txt,".nav-pills > li:not(.active):not(:hover) > a",".nav-pills li ul.dropdown-menu li a:not(:hover)",".ui-tabs-nav a");
//
                    //changeMainStyle(".nav-pills > li:not(.active):not(:hover) > a", styleName, txt);
                } else if ($styleHover.hasClass("activt")) {
                    dataBox.attr("data-1", txt);
                    writeStyle(styleName, txt,".nav-pills > li > a:hover",".nav-pills li ul.dropdown-menu li a:hover",".ui-state-hover a:hover");
                    //changeMainStyle(".nav-pills > li > a:hover", styleName, txt);
                } else if ($styleClick.hasClass("activt")) {
                    dataBox.attr("data-2", txt);
                    writeStyle(styleName, txt,".nav-pills > li.active > a",".nav-pills li ul.dropdown-menu li a:focus",".ui-tabs-active a:link");
                    //changeMainStyle(".nav-pills > li.active > a", styleName, txt);
                }
                LEHistory.trigger();
            })

        };
                //区分主导航与子导航，切换作用对象 SlideBar.js 处添加标记
        function writeStyle(styleName, txt,mainSelector,subSelector,tabSelector){
            var partSelectorVal="";
            var _setting = LECurrentObject.attr("data-setting");
            if(_setting=="mainNav"){
                partSelectorVal=mainSelector;
            }else if(_setting=="subNav"){
                partSelectorVal=subSelector;
            }else if(_setting=="tab"){
                partSelectorVal=tabSelector;
            }

            changeMainStyle(partSelectorVal, styleName, txt);
        }

        /*设置字体颜色，区分不同状态的a便签进行设置*/
        var initFontColorEvent = function (styleName) {
            $headColorPickStyle.spectrum(
                LEColorPicker.getOptions(function (tinycolor) {
                    var _c = tinycolor ? tinycolor.toRgbaString() : "";
                    if ($styleNormal.hasClass("activt")) {
                        $headColorPickStyle.attr("data-0", _c);
                        writeStyle(styleName, _c,".nav-pills > li:not(.active):not(:hover) > a",".nav-pills li ul.dropdown-menu li a:not(:hover)",".ui-tabs-nav a");
                       // changeMainStyle(".nav-pills > li:not(.active):not(:hover) > a", "color", _c);
                    } else if ($styleHover.hasClass("activt")) {
                        $headColorPickStyle.attr("data-1", _c);
                        writeStyle(styleName, _c,".nav-pills > li > a:hover",".nav-pills li ul.dropdown-menu li a:hover",".ui-state-hover a:hover");
                       // changeMainStyle(".nav-pills > li > a:hover", "color", _c);
                    } else if ($styleClick.hasClass("activt")) {
                        $headColorPickStyle.attr("data-2", _c);
                        writeStyle(styleName, _c,".nav-pills > li.active > a",".nav-pills li ul.dropdown-menu li a:focus",".ui-tabs-active a:link");
                       // changeMainStyle(".nav-pills > li.active > a", "color", _c);
                    }
                    LEHistory.trigger();
                })
            );
        };


        /*设置字体加粗、倾斜、下划线、删除线，区分不同状态的a标签进行设置*/
        var initFontStyleEvent = function (selector,dataBox) {
            $(selector).bind("click",function(){
                var _$this = $(this);
                var _style = _$this.attr("data-fontstyle");
                var _value = _$this.attr("data-value");
                var _reset = _$this.attr("data-reset");
                if ($styleNormal.hasClass("activt")) {
                    var data="0";
                    var txt =setValue(_$this,_value,_reset,data);
                    dataBox.attr("data-0", txt);
                    writeStyle(_style, txt,".nav-pills > li:not(.active):not(:hover) > a",".nav-pills li ul.dropdown-menu li a:not(:hover)",".ui-tabs-nav a");
                   // changeMainStyle(".nav-pills > li:not(.active):not(:hover) > a", _style, txt);
                } else if ($styleHover.hasClass("activt")) {
                    var data="1";
                    var txt =setValue(_$this,_value,_reset,data);
                    dataBox.attr("data-1", txt);
                    writeStyle(_style, txt,".nav-pills > li > a:hover",".nav-pills li ul.dropdown-menu li a:hover",".ui-state-hover a:hover");
                   // changeMainStyle(".nav-pills > li > a:hover", _style, txt);
                } else if ($styleClick.hasClass("activt")) {
                    var data="2";
                    var txt =setValue(_$this,_value,_reset,data);
                    dataBox.attr("data-2", txt);
                    writeStyle(_style, txt,".nav-pills > li.active > a",".nav-pills li ul.dropdown-menu li a:focus",".ui-tabs-active a:link");
                    //changeMainStyle(".nav-pills > li.active > a", _style, txt);
                }
                LEHistory.trigger();
            })

        };

        function setValue(_$this,_value,_reset,data) {
            var txt="";
            if (_$this.hasClass("select")) {
                txt=_reset;
                _$this.removeClass("select");
            } else {
                if(_reset=="none"){
                    $(".throughFontStyle").attr("data-"+data,"none");
                    $(".throughFontStyle").removeClass("select");
                }
                txt=_value;
                _$this.addClass("select");
            }

            return txt;
        }



        /*初始化主导航样式的按钮状态 字体
         * 根据样式优先级
         * 1、判断有没有自定义样式，如果有,将style标签中的自定义样式初始化到按钮
         * 2、没有自定义样式 则判断有没有模板样式，如果有，将对应的模板样式初始化到按钮
         * 3、否则 将默认样式初始化到按钮
         * */
            function readStyle(dataBox,styleName,defaultVal){
                var _setting = LECurrentObject.attr("data-setting");
                var target = LECurrentObject.attr("id");
                if(_setting=="mainNav"){
                    //获取主导航初始化值
                    var FmNormal = LECurrentObject.find(".nav-pills > li:not(.active):not(:hover) > a").css(styleName);
                    var FmLink = LECurrentObject.find(".nav-pills > li.active > a").css(styleName);

                    var FmLink = !FmLink ? dataBox.attr("data-2") : LECurrentObject.find(".nav-pills > li.active > a").css(styleName);
                    var FmNormal = !FmNormal ? dataBox.attr("data-0") : LECurrentObject.find(".nav-pills > li:not(.active):not(:hover) > a").css(styleName);

                    var FmHover = StyleManager.getStyle("#" + target + " .nav-pills > li > a:hover", styleName);
                    if (FmHover == "none") {
                        if (LECurrentObject.find("ul.nav-pills").attr("data-prestyle")) {
                            //2、否则如果样式中存在,当前对象的最外层ul是否具有"data-prestyle"属性 属性值对应class名
                            var dataPrestyle = LECurrentObject.find("ul.nav-pills").attr("data-prestyle");
                            FmHover = modelStyleManager.getStyle("." + dataPrestyle + " > li > a:hover", styleName);
                        } else {
                            //3否则默认样式
                            FmHover = defaultVal;
                        }
                    } else {
                        //1、如果html中存在自定义选择器
                        FmHover = StyleManager.getStyle("#" + target + " .nav-pills > li > a:hover", styleName);
                    }
                }else if(_setting=="subNav"){
                         //获取子导航初始化值
                        var FmNormal = LECurrentObject.find(".nav-pills li ul.dropdown-menu li a:not(:hover)").css(styleName);
                        var FmNormal = !FmNormal ? dataBox.attr("data-0") : LECurrentObject.find(".nav-pills li ul.dropdown-menu li a:not(:hover)").css(styleName);

                        var FmHover = StyleManager.getStyle("#" + target + " .nav-pills li ul.dropdown-menu li a:hover", styleName);
                        var FmLink = StyleManager.getStyle("#" + target + " .nav-pills li ul.dropdown-menu li a:focus", styleName);
                        if (FmHover == "none") {
                            if (LECurrentObject.find("ul.dropdown-menu").attr("data-prestyle")) {
                                //2、否则如果样式中存在,当前对象的最外层ul是否具有"data-prestyle"属性 属性值对应class名
                                var dataPrestyle = LECurrentObject.find("ul.dropdown-menu").attr("data-prestyle");
                                FmHover = modelStyleManager.getStyle("." + dataPrestyle + " li a:hover", styleName);
                            } else {
                                //3否则默认样式
                                FmHover = defaultVal;
                            }
                        } else {
                            //1、如果html中存在自定义选择器
                            FmHover = StyleManager.getStyle("#" + target + " .nav-pills li ul.dropdown-menu li a:hover", styleName);
                        }

                        if (FmLink == "none") {
                            if (LECurrentObject.find("ul.dropdown-menu").attr("data-prestyle")) {
                                //2、否则如果样式中存在,当前对象的最外层ul是否具有"data-prestyle"属性 属性值对应class名
                                var dataPrestyle = LECurrentObject.find("ul.dropdown-menu").attr("data-prestyle");
                                FmLink = modelStyleManager.getStyle("." + dataPrestyle + " li a:focus", styleName);
                            } else {
                                //3否则默认样式
                                FmLink = defaultVal;
                            }
                        } else {
                            //1、如果html中存在自定义选择器
                            FmLink = StyleManager.getStyle("#" + target + " .nav-pills li ul.dropdown-menu li a:focus", styleName);
                        }
                    }else if(_setting=="tab"){
                    //获取标签初始化值
                    var FmNormal = LECurrentObject.find(".ui-tabs-nav a:not(:first)").css(styleName);
                    var FmNormal = !FmNormal ? dataBox.attr("data-0") : LECurrentObject.find(".ui-tabs-nav a:not(:first)").css(styleName);

                    var FmHover = StyleManager.getStyle("#" + target + " .ui-state-hover a:hover", styleName);
                    var FmLink = StyleManager.getStyle("#" + target + " .ui-tabs-active a:link", styleName);
                    if (FmHover == "none") {
                        if (LECurrentObject.find("ul.ui-tabs-nav").attr("data-prestyle")) {
                            //2、否则如果样式中存在,当前对象的最外层ul是否具有"data-prestyle"属性 属性值对应class名
                            var dataPrestyle = LECurrentObject.find("ul.ui-tabs-nav").attr("data-prestyle");
                            FmHover = modelStyleManager.getStyle(".ui-tabs ." + dataPrestyle + " > li.ui-state-hover > a", styleName);
                        } else {
                            //3否则默认样式
                            FmHover = defaultVal;
                        }
                    } else {
                        //1、如果html中存在自定义选择器
                        FmHover = StyleManager.getStyle("#" + target + " .ui-state-hover a:hover", styleName);
                    }

                    if (FmLink == "none") {
                        if (LECurrentObject.find("ul.ui-tabs-nav").attr("data-prestyle")) {
                            //2、否则如果样式中存在,当前对象的最外层ul是否具有"data-prestyle"属性 属性值对应class名
                            var dataPrestyle = LECurrentObject.find("ul.ui-tabs-nav").attr("data-prestyle");
                            FmLink = modelStyleManager.getStyle(".ui-tabs ." + dataPrestyle + " > li.ui-tabs-active > a", styleName);
                        } else {
                            //3否则默认样式
                            FmLink = defaultVal;
                        }
                    } else {
                        //1、如果html中存在自定义选择器
                        FmLink = StyleManager.getStyle("#" + target + " .ui-tabs-active a:link", styleName);
                    }
                }
                 return [FmNormal,FmHover,FmLink]
                }




        var resetFontStatus = function (dataBox,styleName,defaultVal,isColor,$trigger) {
            var _value=readStyle(dataBox,styleName,defaultVal);
            var FmNormal=_value[0];
            var FmHover=_value[1];
            var FmLink=_value[2];
            /*var target = LECurrentObject.attr("id");
            var FmNormal = LECurrentObject.find(".nav-pills > li:not(.active):not(:hover) > a").css(styleName);
            var FmLink = LECurrentObject.find(".nav-pills > li.active > a").css(styleName);

            var FmLink = !FmLink ? dataBox.attr("data-2") : LECurrentObject.find(".nav-pills > li.active > a").css(styleName);
            var FmNormal = !FmNormal ? dataBox.attr("data-0") : LECurrentObject.find(".nav-pills > li:not(.active):not(:hover) > a").css(styleName);
            var FmHover = StyleManager.getStyle("#" + target + " .nav-pills > li > a:hover", styleName);
            if (FmHover == "none") {
                if (LECurrentObject.find("ul.nav-pills").attr("data-prestyle")) {
                    //2、否则如果样式中存在,当前对象的最外层ul是否具有"data-prestyle"属性 属性值对应class名
                    var dataPrestyle = LECurrentObject.find("ul.nav-pills").attr("data-prestyle");
                    FmHover = modelStyleManager.getStyle("." + dataPrestyle + " > li > a:hover", styleName);
                } else {
                    //3否则默认样式
                    FmHover = defaultVal;
                }
            } else {
                //1、如果html中存在自定义选择器
                FmHover = StyleManager.getStyle("#" + target + " .nav-pills > li > a:hover", styleName);
            }*/

            dataBox.attr("data-0", FmNormal);
            dataBox.attr("data-1", FmHover);
            dataBox.attr("data-2", FmLink);

            if ($styleNormal.hasClass("activt")) {
                statusReset(isColor,$trigger,FmNormal,dataBox);
              //isColor ? $trigger.spectrum("set", FmNormal) : dataBox.html(FmNormal);
            } else if ($styleHover.hasClass("activt")) {
                statusReset(isColor,$trigger,FmHover,dataBox);
                //isColor ? $trigger.spectrum("set", FmNormal) : dataBox.html(FmHover);
            } else if ($styleClick.hasClass("activt")) {
                statusReset(isColor,$trigger,FmLink,dataBox);
                //isColor ? $trigger.spectrum("set", FmNormal) : dataBox.html(FmLink);
            }
        };

        function statusReset(isColor,$trigger,FmNormal,dataBox){
            if(isColor=="style"){
                if($trigger.attr("data-value")==FmNormal){
                    $trigger.addClass("select");
                }else{
                    $trigger.removeClass("select");
                }
            }else if(isColor=="color"){
                $trigger.spectrum("set", FmNormal)
            }else if(isColor=="family"){
                dataBox.html(FmNormal)
            }
        }




        return {
            //onload
            init: function () {
                //initStyleTabEvent();
                initFontEvent("#fontFa-style",$fmCite,"font-family","");
                initFontEvent("#fontSiz-style",$fsCite,"font-size","px");
                initFontColorEvent("color");
                initFontStyleEvent("#fontBold-style",$fontBoldStyle);
                initFontStyleEvent("#fontItalic-style",$fontItalicStyle);
                initFontStyleEvent("#fontUnder-style",$fontUnderStyle);
                initFontStyleEvent("#fontThrough-style",$fontThroughStyle);


            },
            run: function (options, doHide, doSlide) {
                if (LECurrentObject.children().length > 0) {
                    resetFontStatus($fmCite,"font-family","微软雅黑","family");
                    resetFontStatus($fsCite,"font-size","12px","family");
                    resetFontStatus($headColorPickStyle,"color","rgba(27%, 27%, 27%, 1)","color",$headColorPickStyle);
                    resetFontStatus($fontBoldStyle,"font-weight","200","style",$fontBoldStyle);
                    resetFontStatus($fontItalicStyle,"font-style","normal","style",$fontItalicStyle);
                    resetFontStatus($fontUnderStyle,"text-decoration","none","style",$fontUnderStyle);
                    resetFontStatus($fontThroughStyle,"text-decoration","none","style",$fontThroughStyle);
                    //切换组件时，样式设置部分回顶部
                    sliderbarToTop();
                    LEDisplay.show($PS, doHide, doSlide);
                    LEDisplay.show($PSTab, doHide, doSlide);
                }
            },
            destroy: function () {
                $PS.hide();
                $PSTab.hide();

            },
            buttonReset:function(_this){

                resetTextStyleButton(_this);
            }


        };
    };
})(window, jQuery, LE, undefined);
