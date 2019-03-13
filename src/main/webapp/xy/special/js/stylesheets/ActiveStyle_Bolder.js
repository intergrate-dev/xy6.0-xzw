/**
 * Created by isaac_gu on 2016/1/14.
 */
(function (window, $, LE) {
    LE.stylesheets["ActiveBolderStyle"] = function () {
        var $PS=$("#bolderSection-style");
        var $bolderColorPickStyle=$("#bolderColorPick-style");
        var $bBolderColorStyle=$("#bBolderColor-style");
        var $bBolderWidthStyle=$("#bBolderWidth-style");
        var $bdPUlStyle=$("#bdPUl-style");
        var $bolderStyleUlStyle=$("#bolderStyleUl-style");
        var $bBolderRadiusStyle=$("#bBolderRadius-style");

        var $styleNormal = $("#style-normal");
        var $styleHover = $("#style-hover");
        var $styleClick= $("#style-click");


        //交互边框宽度、圆角输入框
        function numInputKeyDown(event, $this, $target,_style){
            //如果是上下的话，执行以下的操作
            if(event.keyCode == 38 || event.keyCode == 40 || event.keyCode == 13){
                var _value = $this.val();
                var num = parseInt(_value);
                if(isNaN(num)) return;
                checkUnit(_value, $this);

                var _unit = $this.attr("data-unit") || "px";
                //点击上下按钮，来改变当前的值
                event.keyCode == 38 && ++num;
                event.keyCode == 40 && --num;

                //设定目标的样式
                if ($styleNormal.hasClass("activt")) {
                    numInputKeyChangeStyle($target,"0",_style,num,_unit,".nav-pills > li:not(.active):not(:hover) > a",".nav-pills li ul.dropdown-menu li a:not(:hover)",".ui-tabs-nav a");
                } else if ($styleHover.hasClass("activt")) {
                    numInputKeyChangeStyle($target,"1",_style,num,_unit,".nav-pills > li > a:hover",".nav-pills li ul.dropdown-menu li a:hover",".ui-state-hover a:hover");
                } else if ($styleClick.hasClass("activt")) {
                    numInputKeyChangeStyle($target,"2",_style,num,_unit,".nav-pills > li.active > a",".nav-pills li ul.dropdown-menu li a:focus",".ui-tabs-active a:link");
                }
                $this.val(num + _unit);
                return num;
            }
            return null;
        }

        //交互边框宽度、圆角输入框失去焦点
        function numInputBlur(event, $this, $target,_style){
            if ($styleNormal.hasClass("activt")) {
                numInputBlurChangeStyle($this,"0",_style,$target,".nav-pills > li:not(.active):not(:hover) > a",".nav-pills li ul.dropdown-menu li a:not(:hover)",".ui-tabs-nav a");
            } else if ($styleHover.hasClass("activt")) {
                numInputBlurChangeStyle($this,"1",_style,$target,".nav-pills > li > a:hover",".nav-pills li ul.dropdown-menu li a:hover",".ui-state-hover a:hover");
            } else if ($styleClick.hasClass("activt")) {
                numInputBlurChangeStyle($this,"2",_style,$target,".nav-pills > li.active > a",".nav-pills li ul.dropdown-menu li a:focus",".ui-tabs-active a:link");
            }

        }
        //交互边框宽度、圆角输入框获取焦点
        function numInputFocus(event, $this, $target,_style){
            if ($styleNormal.hasClass("activt")) {
                numInputFocusChangeStyle($this,"0",$target,_style)
            } else if ($styleHover.hasClass("activt")) {
                numInputFocusChangeStyle($this,"1",$target,_style)
            } else if ($styleClick.hasClass("activt")) {
                numInputFocusChangeStyle($this,"2",$target,_style)
            }
        }

        function checkUnit(_value, $this){
            //如果包含px，就以px为主
            if(_value.indexOf("px") != -1){
                $this.attr("data-unit", "px");
            } else if(_value.indexOf("%") != -1){
                $this.attr("data-unit", "%");
            }
        }

        function numInputKeyChangeStyle($target,j,_style,num,_unit,mainSelector,subSelector,tabSelector){
            var partSelectorVal="";
            var _setting = LECurrentObject.attr("data-setting");
            if(_setting=="mainNav"){
                j=j;
                partSelectorVal=mainSelector;
            }else if(_setting=="subNav"){
                j=parseInt(j)+3;
                partSelectorVal=subSelector;
            }else if(_setting=="tab"){
                j=parseInt(j)+6;
                partSelectorVal=tabSelector;
            }

            $target.attr("data-"+j+"-"+_style, num + _unit);
            var _p = $target.attr("data-"+j+"-position");
            _p = _p.split(",");
            for(var i in _p){
                if(_style=="radius"){
                    changeMainStyle(partSelectorVal, "border" + "-" + _style, num + _unit);
                }else{
                    changeMainStyle(partSelectorVal, _p[i] + "-" + _style, num + _unit);
                }
            }
        }


        function numInputBlurChangeStyle($this,j,_style,$target,mainSelector,subSelector,tabSelector){
            var partSelectorVal="";
            var _setting = LECurrentObject.attr("data-setting");
            if(_setting=="mainNav"){
                j=j;
                partSelectorVal=mainSelector;
            }else if(_setting=="subNav"){
                j=parseInt(j)+3;
                partSelectorVal=subSelector;
            }else if(_setting=="tab"){
                j=parseInt(j)+6;
                partSelectorVal=tabSelector;
            }

            var _value = $this.val();
            $.trim(_value) == "" && $this.val($this.attr("data-"+j+"-"+_style));
            if($.trim(_value) == "")  return;
            checkUnit(_value, $this);
            var num = parseInt(_value);
            var _unit = $this.attr("data-unit") || "px";
            // $target.css($this.attr("data-ref"), num + _unit);
            var _p = $target.attr("data-"+j+"-position");
            _p = _p.split(",");
            for(var i in _p){
                if(_style=="radius"){
                    changeMainStyle(partSelectorVal, "border" + "-" + _style, num + _unit);
                }else{
                    changeMainStyle(partSelectorVal, _p[i] + "-" + _style, num + _unit);
                }
            }
            $this.val(num + _unit);
        }

        function numInputFocusChangeStyle($this,j,$target,_style){
            var _setting = $target.attr("data-setting");
            if(_setting=="mainNav"){
                j=j;
            }else if(_setting=="subNav"){
                j=parseInt(j)+3;
            }else if(_setting=="tab"){
                j=parseInt(j)+6;
            }

            $this.val( $target.attr("data-"+j+"-"+_style));
        }

        /*单击导航交互普通、悬停、点击三个按钮时，边框各输入框值得切换，值等于input框对应的data-0、data-1、data-2*/
        function resetBolderStyleButton(_this){
            resetBoldPosition();
            resetBoldColorPicker();
            resetBolderWidth();
            resetBolderStyle();
            resetBolderRadius();

            var j=_this.index();

            var _setting = LECurrentObject.attr("data-setting");
            if(_setting=="mainNav"){
                j=j;
            }else if(_setting=="subNav"){
                j=parseInt(j)+3;
            }else if(_setting=="tab"){
                j=parseInt(j)+6;
            }


            //颜色
            $bolderColorPickStyle.spectrum("set",LECurrentObject.attr("data-" +j+"-color"));
            if(LECurrentObject.attr("data-" + j+"-color")){
                $bBolderColorStyle.val($bolderColorPickStyle.spectrum("get").toHexString());
            }else{
                $bBolderColorStyle.val("");
            }

            //交互边框位置
            $bdPUlStyle.children("li").removeClass("select");
            var _position = LECurrentObject.attr("data-"+j+"-position");
            if(_position){
                $bdPUlStyle.children("li[data-ref='" + _position + "']").addClass("select");
            }else{
                $bdPUlStyle.children("li:last").addClass("select");
                LECurrentObject.attr("data-"+j+"-position", $bdPUlStyle.children("li:last").attr("data-ref"));
            }
            //交互边框厚度
            $bBolderWidthStyle.val(LECurrentObject.attr("data-"+j+"-width"));
            //样式
            $bolderStyleUlStyle.children("li").removeClass("select");
            var _style = LECurrentObject.attr("data-"+j+"-style");
            if(_style){
                $bolderStyleUlStyle.children("li[data-ref='" + _style + "']").addClass("select");
            }else{
                $bolderStyleUlStyle.children("li:last").addClass("select");
                LECurrentObject.attr("data-"+j+"-_style", $bolderStyleUlStyle.children("li:last").attr("data-ref"));
            }
            //半径
            $bBolderRadiusStyle.val(LECurrentObject.attr("data-"+j+"-radius"));
        }

        /*对style标签进行操作 调用了StyleManager对象的setSty方法实现增加或修改styl标签内容*/
        function changeMainStyle(partSelector, sccName, cssValue) {
            var id = LECurrentObject.attr("id");
            var selector = "#" + id + " " + partSelector;
            StyleManager.setStyle(selector, sccName, cssValue);
        }

        function borderColorInput(j,_c,_v){
            var _setting = LECurrentObject.attr("data-setting");
            if(_setting=="mainNav"){
                j=j;
            }else if(_setting=="subNav"){
                j=parseInt(j)+3;
            }else if(_setting=="tab"){
                j=parseInt(j)+6;
            }
            LECurrentObject.attr("data-"+j+"-color", _c);
            $bBolderColorStyle.val(_v);
        }

        /*设置边框颜色，区分不同状态的a便签进行设置*/
        var initBolderColorEvent = function (styleName) {
            $bolderColorPickStyle.spectrum(
                LEColorPicker.getOptions(function (tinycolor) {
                    var _c = tinycolor ? tinycolor.toRgbaString() : "";
                    var _v = tinycolor ? tinycolor.toHexString() : "#333333";
                    if ($styleNormal.hasClass("activt")) {
                        borderColorInput('0',_c,_v);
                        setStyle("0",".nav-pills > li:not(.active):not(:hover) > a",".nav-pills li ul.dropdown-menu li a:not(:hover)",".ui-tabs-nav a","color", _c);
                    } else if ($styleHover.hasClass("activt")) {
                        borderColorInput('1',_c,_v);
                        setStyle("1",".nav-pills > li > a:hover",".nav-pills li ul.dropdown-menu li a:hover",".ui-state-hover a:hover","color", _c);
                    } else if ($styleClick.hasClass("activt")) {
                        borderColorInput('2',_c,_v);
                        setStyle("2",".nav-pills > li.active > a",".nav-pills li ul.dropdown-menu li a:focus",".ui-tabs-active a:link","color", _c);
                    }
                    LEHistory.trigger();
                })
            );
        };

            function isSet(){
                if ($styleNormal.hasClass("activt")) {
                    return LECurrentObject.attr("data-0-set") ? true : false;
                } else if ($styleHover.hasClass("activt")) {
                    return LECurrentObject.attr("data-1-set") ? true : false;
                } else if ($styleClick.hasClass("activt")) {
                    return LECurrentObject.attr("data-2-set") ? true : false;
                }

            }
             function bdColorReset(j){
                 var _setting = LECurrentObject.attr("data-setting");
                 if(_setting=="mainNav"){
                     j=j;
                 }else if(_setting=="subNav"){
                     j=parseInt(j)+3;
                 }else if(_setting=="tab"){
                     j=parseInt(j)+6;
                 }

                 !LECurrentObject.attr("data-"+j+"-color") && LECurrentObject.attr("data-"+j+"-color","rgb(51, 51, 51)");
                     $bolderColorPickStyle.spectrum("set", LECurrentObject.attr("data-"+j+"-color"));
                     $bBolderColorStyle.val($bolderColorPickStyle.spectrum("get").toHexString());
             }
        //初始化边框颜色
        var resetBoldColorPicker=function(){
            if ($styleNormal.hasClass("activt")) {
                bdColorReset("0")
            } else if ($styleHover.hasClass("activt")) {
                bdColorReset("1")
            } else if ($styleClick.hasClass("activt")) {
                bdColorReset("2")
            }

        };

            function bdPositionReset(j){
                var _setting = LECurrentObject.attr("data-setting");
                if(_setting=="mainNav"){
                    j=j;
                }else if(_setting=="subNav"){
                    j=parseInt(j)+3;
                }else if(_setting=="tab"){
                    j=parseInt(j)+6;
                }

                $bdPUlStyle.find("li").removeClass("select");
                var _position = LECurrentObject.attr("data-"+j+"-position");
                if(_position){
                    $bdPUlStyle.children("li[data-ref='" + _position + "']").addClass("select");
                } else{
                    $bdPUlStyle.children("li:last").addClass("select");
                    LECurrentObject.attr("data-"+j+"-position",$bdPUlStyle.children("li:last").attr("data-ref"));
                }
            }

        //初始化边框位置
        var resetBoldPosition=function(){
            if ($styleNormal.hasClass("activt")) {
                bdPositionReset("0")
            } else if ($styleHover.hasClass("activt")) {
                bdPositionReset("1")
            } else if ($styleClick.hasClass("activt")) {
                bdPositionReset("2")
            }
        };

            function bdWidthReset(j){
                var _setting = LECurrentObject.attr("data-setting");
                if(_setting=="mainNav"){
                    j=j;
                }else if(_setting=="subNav"){
                    j=parseInt(j)+3;
                }else if(_setting=="tab"){
                    j=parseInt(j)+6;
                }

                !LECurrentObject.attr("data-"+j+"-width")&& LECurrentObject.attr("data-"+j+"-width","3px");
                $bBolderWidthStyle.val(LECurrentObject.attr("data-"+j+"-width"));
            }

        //初始化边框宽度
        var resetBolderWidth=function(isReset){
            if ($styleNormal.hasClass("activt")) {
                bdWidthReset("0")
            } else if ($styleHover.hasClass("activt")) {
                bdWidthReset("1")
            } else if ($styleClick.hasClass("activt")) {
                bdWidthReset("2")
            }
        };
            function bdStyleReset(j){
                var _setting = LECurrentObject.attr("data-setting");
                if(_setting=="mainNav"){
                    j=j;
                }else if(_setting=="subNav"){
                    j=parseInt(j)+3;
                }else if(_setting=="tab"){
                    j=parseInt(j)+6;
                }

                $bolderStyleUlStyle.find("li").removeClass("select");
                !LECurrentObject.attr("data-"+j+"-style") && LECurrentObject.attr("data-"+j+"-style", "none");
                    $bolderStyleUlStyle.find("li[data-ref='" + LECurrentObject.attr("data-"+j+"-style") + "']").addClass("select");
            }
        //初始化边框样式
        var resetBolderStyle=function(){
            if ($styleNormal.hasClass("activt")) {
                bdStyleReset("0")
            } else if ($styleHover.hasClass("activt")) {
                bdStyleReset("1")
            } else if ($styleClick.hasClass("activt")) {
                bdStyleReset("2")
            }
        };
            function bdRadiusReset(j){
                var _setting = LECurrentObject.attr("data-setting");
                if(_setting=="mainNav"){
                    j=j;
                }else if(_setting=="subNav"){
                    j=parseInt(j)+3;
                }else if(_setting=="tab"){
                    j=parseInt(j)+6;
                }

                !LECurrentObject.attr("data-"+j+"-radius") && LECurrentObject.attr("data-"+j+"-radius", "0px");
                    $bBolderRadiusStyle.val(LECurrentObject.attr("data-"+j+"-radius"));
            }

        //初始化边框圆角
        var resetBolderRadius=function(){
            if ($styleNormal.hasClass("activt")) {
                bdRadiusReset("0")
            } else if ($styleHover.hasClass("activt")) {
                bdRadiusReset("1")
            } else if ($styleClick.hasClass("activt")) {
                bdRadiusReset("2")
            }
        };

            function resetValue(){
            var _p = LECurrentObject.attr("data-position");
            _p = _p.split(",");
            for(var i in _p){
                changeMainStyle(".nav-pills > li:not(.active):not(:hover) > a", _p[i] + "-" + "color", LECurrentObject.attr("data-color"));
                changeMainStyle(".nav-pills > li:not(.active):not(:hover) > a", _p[i] + "-" + "width", LECurrentObject.attr("data-width"));
                changeMainStyle(".nav-pills > li:not(.active):not(:hover) > a", _p[i] + "-" + "style", LECurrentObject.attr("data-style"));
                changeMainStyle(".nav-pills > li:not(.active):not(:hover) > a", _p[i] + "-" + "radius", LECurrentObject.attr("data-radius"));
            }
        }

        //边框宽度设置
        var initBolderWidthEvent=function(){
            $bBolderWidthStyle.onlyNum().keydown(function(e){
                var $target =LECurrentObject;
                numInputKeyDown(e, $(this), $target,"width");
            });

            $bBolderWidthStyle.blur(function(e){
                var $target = LECurrentObject;
                numInputBlur(e, $(this), $target,"width");
                LEHistory.trigger();
            });

            $bBolderWidthStyle.focus(function(e){
                var $target =LECurrentObject;
                numInputFocus(e, $(this), $target,"width");
            });
        };

        function cleanBorderStyleMainSub(mainSelector,subSelector,tabSelector,borderType,style){
            var partSelectorVal="";
            var _setting = LECurrentObject.attr("data-setting");
            if(_setting=="mainNav"){
                partSelectorVal=mainSelector;
            }else if(_setting=="subNav"){
                partSelectorVal=subSelector;
            }else if(_setting=="tab"){
                partSelectorVal=tabSelector;
            }
            changeMainStyle(partSelectorVal,borderType+"-"+style, "");
        }
            //设置边框样式前清除边框样式
            function cleanBorderStyle(style){
                if ($styleNormal.hasClass("activt")) {
                    cleanBorderStyleMainSub(".nav-pills > li:not(.active):not(:hover) > a",".nav-pills li ul.dropdown-menu li a:not(:hover)",".ui-tabs-nav a","border",style);
                    cleanBorderStyleMainSub(".nav-pills > li:not(.active):not(:hover) > a",".nav-pills li ul.dropdown-menu li a:not(:hover)",".ui-tabs-nav a","border-top",style);
                    cleanBorderStyleMainSub(".nav-pills > li:not(.active):not(:hover) > a",".nav-pills li ul.dropdown-menu li a:not(:hover)",".ui-tabs-nav a","border-bottom",style);
                    cleanBorderStyleMainSub(".nav-pills > li:not(.active):not(:hover) > a",".nav-pills li ul.dropdown-menu li a:not(:hover)",".ui-tabs-nav a","border-left",style);
                    cleanBorderStyleMainSub(".nav-pills > li:not(.active):not(:hover) > a",".nav-pills li ul.dropdown-menu li a:not(:hover)",".ui-tabs-nav a","border-right",style);
                } else if ($styleHover.hasClass("activt")) {
                    cleanBorderStyleMainSub(".nav-pills > li > a:hover",".nav-pills li ul.dropdown-menu li a:hover",".ui-state-hover a:hover","border",style);
                    cleanBorderStyleMainSub(".nav-pills > li > a:hover",".nav-pills li ul.dropdown-menu li a:hover",".ui-state-hover a:hover","border-top",style);
                    cleanBorderStyleMainSub(".nav-pills > li > a:hover",".nav-pills li ul.dropdown-menu li a:hover",".ui-state-hover a:hover","border-bottom",style);
                    cleanBorderStyleMainSub(".nav-pills > li > a:hover",".nav-pills li ul.dropdown-menu li a:hover",".ui-state-hover a:hover","border-left",style);
                    cleanBorderStyleMainSub(".nav-pills > li > a:hover",".nav-pills li ul.dropdown-menu li a:hover",".ui-state-hover a:hover","border-right",style);
                } else if ($styleClick.hasClass("activt")) {
                    cleanBorderStyleMainSub(".nav-pills > li.active > a",".nav-pills li ul.dropdown-menu li a:focus",".ui-tabs-active a:link","border",style);
                    cleanBorderStyleMainSub(".nav-pills > li.active > a",".nav-pills li ul.dropdown-menu li a:focus",".ui-tabs-active a:link","border-top",style);
                    cleanBorderStyleMainSub(".nav-pills > li.active > a",".nav-pills li ul.dropdown-menu li a:focus",".ui-tabs-active a:link","border-bottom",style);
                    cleanBorderStyleMainSub(".nav-pills > li.active > a",".nav-pills li ul.dropdown-menu li a:focus",".ui-tabs-active a:link","border-left",style);
                    cleanBorderStyleMainSub(".nav-pills > li.active > a",".nav-pills li ul.dropdown-menu li a:focus",".ui-tabs-active a:link","border-right",style);
                }
            }

        function changeDataStyle(j,bdStyle,_ref){
            var _setting = LECurrentObject.attr("data-setting");
            if(_setting=="mainNav"){
                j=j;
            }else if(_setting=="subNav"){
                j=parseInt(j)+3;
            }else if(_setting=="tab"){
                j=parseInt(j)+6;
            }

            LECurrentObject.attr("data-"+j+"-"+bdStyle,_ref);
        }

        //设置边框样式
        var initBolderStyle=function(){
                $bolderStyleUlStyle.find("li").click(function(){
                    $(this).addClass("select").siblings().removeClass("select");
                    var _ref = $(this).attr("data-ref");
                    if ($styleNormal.hasClass("activt")) {
                        cleanBorderStyle("style");
                        changeDataStyle("0","style",_ref);
                        setStyle("0",".nav-pills > li:not(.active):not(:hover) > a",".nav-pills li ul.dropdown-menu li a:not(:hover)",".ui-tabs-nav a","style");
                        setStyle("0",".nav-pills > li:not(.active):not(:hover) > a",".nav-pills li ul.dropdown-menu li a:not(:hover)",".ui-tabs-nav a","color");
                        setStyle("0",".nav-pills > li:not(.active):not(:hover) > a",".nav-pills li ul.dropdown-menu li a:not(:hover)",".ui-tabs-nav a","width");

                    } else if ($styleHover.hasClass("activt")) {
                        cleanBorderStyle("style");
                        changeDataStyle("1","style",_ref);
                        setStyle("1",".nav-pills > li > a:hover",".nav-pills li ul.dropdown-menu li a:hover",".ui-state-hover a:hover","style");
                        setStyle("1",".nav-pills > li > a:hover",".nav-pills li ul.dropdown-menu li a:hover",".ui-state-hover a:hover","color");
                        setStyle("1",".nav-pills > li > a:hover",".nav-pills li ul.dropdown-menu li a:hover",".ui-state-hover a:hover","width");

                    } else if ($styleClick.hasClass("activt")) {
                        cleanBorderStyle("style");
                        changeDataStyle("2","style",_ref);
                        setStyle("2",".nav-pills > li.active > a",".nav-pills li ul.dropdown-menu li a:focus",".ui-tabs-active a:link","style");
                        setStyle("2",".nav-pills > li.active > a",".nav-pills li ul.dropdown-menu li a:focus",".ui-tabs-active a:link","color");
                        setStyle("2",".nav-pills > li.active > a",".nav-pills li ul.dropdown-menu li a:focus",".ui-tabs-active a:link","width");
                    }
                LEHistory.trigger();
            });
        };

        var initBolderRadius=function(){
            $bBolderRadiusStyle.onlyNum().keydown(function(e){
                var $target =LECurrentObject;
                numInputKeyDown(e, $(this), $target,"radius");
            });

            $bBolderRadiusStyle.blur(function(e){
                var $target =LECurrentObject;
                numInputBlur(e, $(this), $target,"radius");
                LEHistory.trigger();
            });

            $bBolderRadiusStyle.focus(function(e){
                var $target =LECurrentObject;
               numInputFocus(e, $(this), $target,"radius");
            });
        };

        var initBolderPositionEvent=function(){
            $bdPUlStyle.find("li").click(function(){
                $(this).addClass("select").siblings().removeClass("select");
                var _ref = $(this).attr("data-ref");
                if ($styleNormal.hasClass("activt")) {
                    changeDataStyle("0","position",_ref);
                    cleanStyle();
                    setStyle("0",".nav-pills > li:not(.active):not(:hover) > a",".nav-pills li ul.dropdown-menu li a:not(:hover)",".ui-tabs-nav a","color");
                    setStyle("0",".nav-pills > li:not(.active):not(:hover) > a",".nav-pills li ul.dropdown-menu li a:not(:hover)",".ui-tabs-nav a","width");
                    setStyle("0",".nav-pills > li:not(.active):not(:hover) > a",".nav-pills li ul.dropdown-menu li a:not(:hover)",".ui-tabs-nav a","style");
                } else if ($styleHover.hasClass("activt")) {
                    changeDataStyle("1","position",_ref);
                    cleanStyle();
                    setStyle("1",".nav-pills > li > a:hover",".nav-pills li ul.dropdown-menu li a:hover",".ui-state-hover a:hover","color");
                    setStyle("1",".nav-pills > li > a:hover",".nav-pills li ul.dropdown-menu li a:hover",".ui-state-hover a:hover","width");
                    setStyle("1",".nav-pills > li > a:hover",".nav-pills li ul.dropdown-menu li a:hover",".ui-state-hover a:hover","style");
                } else if ($styleClick.hasClass("activt")) {
                    changeDataStyle("2","position",_ref);
                    cleanStyle();
                    setStyle("2",".nav-pills > li.active > a",".nav-pills li ul.dropdown-menu li a:focus",".ui-tabs-active a:link","color");
                    setStyle("2",".nav-pills > li.active > a",".nav-pills li ul.dropdown-menu li a:focus",".ui-tabs-active a:link","width");
                    setStyle("2",".nav-pills > li.active > a",".nav-pills li ul.dropdown-menu li a:focus",".ui-tabs-active a:link","style");
                }
                LEHistory.trigger();
            });

        };

        function cleanStyle(){
            cleanBorderStyle("color");
            cleanBorderStyle("width");
            cleanBorderStyle("style");
        }


        function setStyle(j,mainSelector,subSelector,tabSelector,key, value){
            var partSelectorVal="";
            var _setting = LECurrentObject.attr("data-setting");
            if(_setting=="mainNav"){
                j=j;
                partSelectorVal=mainSelector;
            }else if(_setting=="subNav"){
                j=parseInt(j)+3;
                partSelectorVal=subSelector;
            }else if(_setting=="tab"){
                j=parseInt(j)+6;
                partSelectorVal=tabSelector;
            }

            value = value || LECurrentObject.attr("data-"+j+"-"+ key);
            var selector =$bdPUlStyle.children(".select").attr("data-ref");
            var selectors = selector.split(",");
            for(var i in selectors){
                changeMainStyle(partSelectorVal, selectors[i] + "-" + key, value);
            }
        }
        return {
            init: function () {
                initBolderColorEvent();
                initBolderWidthEvent();
                initBolderStyle();
                initBolderRadius();
                initBolderPositionEvent();
            },
            run: function (options, doHide, doSlide) {
                if (LECurrentObject.children().length > 0) {
                    resetBoldPosition();
                    resetBoldColorPicker();
                    resetBolderWidth();
                    resetBolderStyle();
                    resetBolderRadius();
                    //切换组件时，样式设置部分回顶部
                    sliderbarToTop();
                    LEDisplay.show($PS, doHide, doSlide);
                }
            },
            destroy: function () {
                $bolderStyleUlStyle.find("li").removeClass("select");
                $bBolderColorStyle.val("");
                $bBolderWidthStyle.val("");
                $bBolderRadiusStyle.val("");
                $bolderColorPickStyle.spectrum("set", "");
                $PS.hide();
            },
            buttonReset:function(_this){
                resetBolderStyleButton(_this);
            }


        };
    };
})(window, jQuery, LE, undefined);
