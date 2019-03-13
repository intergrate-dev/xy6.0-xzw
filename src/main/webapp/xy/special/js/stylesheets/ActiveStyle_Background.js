/**
 * Created by isaac_gu on 2016/1/14.
 */
(function (window, $, LE) {
    LE.stylesheets["ActiveBackgroundStyle"] = function () {
        //当前功能面板的对象
        var $PS = $("#bgSection-style");
        var $PSTab = $("#panel-III-styleTab");
        var $bgColorPickStyle = $("#bgColorPick-style");
        var $inputBGColorPickStyle = $("#inputBGColorPick-style");
        var $bgPreImgStyle = $("#bgPreviewImg-style");
        var $bgDeleteImgStyle = $("#bgDeleteImg-style");
        var $bgPosition = $("#bgPosition-ul").find("li");
        var $bgXStyle = $("#bgX-style");
        var $bgYStyle = $("#bgY-style");
        var $bgPositionXY = $("#bgPosition-xy");
        var $bgRepeat = $("#bgRepeat-ul").find(".bgRepeat");


        var $styleNormal = $("#style-normal");
        var $styleHover = $("#style-hover");
        var $styleClick = $("#style-click");

        var resetBackgroundStyleButton = function (_this) {

            $bgColorPickStyle.spectrum("set",$bgColorPickStyle.attr("data-"+_this.index()));
            $bgColorPickStyle.spectrum("get").toHexString();
            $inputBGColorPickStyle.val($bgColorPickStyle.spectrum("get").toHexString());
            $bgPreImgStyle.attr("src", $bgPreImgStyle.attr("data-" + _this.index()));
            $bgDeleteImgStyle.attr("src", $bgDeleteImgStyle.attr("data-" + _this.index()));
            /**点击切换按钮时、对position的初始化
             * 1、初始化选中状态
             * 2、初始化input框的值*/
            var arr=$bgXStyle.attr("data-"+_this.index()).split(" ");
            $bgXStyle.val(arr[0]);
            $bgYStyle.val(arr[1]);

            $bgPosition.each(function(){
                var _this=$(this);
                var _x=_this.attr("data-x");
                var _y=_this.attr("data-y");
                if(_x===arr[0] && _y===arr[1]){
                    _this.addClass("select").siblings().removeClass("select");
                }
            });
            /* 点击切换按钮对repeat重复的初始化 */
            $bgRepeat.each(function(){
                var _value = $(this).attr("data-ref");
                if($(this).attr("data-" + _this.index())==_value){
                    $(this).addClass("select").siblings().removeClass("select");
                }else{
                    $(this).removeClass("select");
                }
            });

        };

        /*将样式写入style标签的公用方法*/
        function changeMainStyle(partSelector, sccName, cssValue) {
            var id = LECurrentObject.attr("id");
            var selector = "#" + id + " " + partSelector;
            StyleManager.setStyle(selector, sccName, cssValue);
        }

        //区分主导航与子导航，切换作用对象 SlideBar.js 处添加标记
        function writeStyle(styleName, bg,mainSelector,subSelector,tabSelector){
            var partSelectorVal="";
            var _setting = LECurrentObject.attr("data-setting");
            if(_setting=="mainNav"){
                partSelectorVal=mainSelector;
            }else if(_setting=="subNav"){
                partSelectorVal=subSelector;
            }else if(_setting=="tab"){
                partSelectorVal=tabSelector;
            }

            changeMainStyle(partSelectorVal, styleName, bg);
        }

        /*设置交互背景颜色，区分不同状态的a便签进行设置*/
        var initBgColorEvent = function (styleName) {
            $bgColorPickStyle.spectrum(
                LEColorPicker.getOptions(function (tinycolor) {
                    var _c = tinycolor ? tinycolor.toRgbaString() : "";
                    var _v = tinycolor ? tinycolor.toHexString() : "#000000";
                    $inputBGColorPickStyle.val(_v);
                    if ($styleNormal.hasClass("activt")) {
                        $bgColorPickStyle.attr("data-0", _c);
                        writeStyle(styleName, _c,".nav-pills > li:not(.active):not(:hover) > a",".nav-pills li ul.dropdown-menu li a:not(:hover)",".ui-tabs-nav a");
                        //   changeMainStyle(".nav-pills > li:not(.active):not(:hover) > a", "background-color", _c);
                    } else if ($styleHover.hasClass("activt")) {
                        $bgColorPickStyle.attr("data-1", _c);
                        writeStyle(styleName, _c,".nav-pills > li > a:hover",".nav-pills li ul.dropdown-menu li a:hover",".ui-state-hover a:hover");
                        //   changeMainStyle(".nav-pills > li > a:hover", "background-color", _c);
                    } else if ($styleClick.hasClass("activt")) {
                        $bgColorPickStyle.attr("data-2", _c);
                        writeStyle(styleName,  _c,".nav-pills > li.active > a",".nav-pills li ul.dropdown-menu li a:focus",".ui-tabs-active a:link");
                        //    changeMainStyle(".nav-pills > li.active > a", "background-color", _c);
                    }

                    LEHistory.trigger();
                })
            );
        };

        /*设置交互导航的背景图上传，区分不同状态的a便签进行设置*/
        var initUploadImgEvent = function (styleName) {
            $bgPreImgStyle.click(function () {
                var isNew = $bgPreImgStyle.attr("src") != "export/images/sliderPanel/sliderPanel1.png";
                if (isNew) {
                    var data = [];
                    data.push($bgPreImgStyle.attr("src"));
                    LEDialog.toggleDialog(LE.options["Dialog"].picEditDialog, function (imgList) {
                            if (imgList.length > 0) {

                                if ($styleNormal.hasClass("activt")) {
                                    $bgPreImgStyle.attr("data-0", imgList[0].path + "')");
                                    writeStyle(styleName,"url('" + imgList[0].path + "')",".nav-pills > li:not(.active):not(:hover) > a",".nav-pills li ul.dropdown-menu li a:not(:hover)",".ui-tabs-nav a");
                                    //   changeMainStyle(".nav-pills > li:not(.active):not(:hover) > a", "background-image", "url('" + imgList[0].path + "')")
                                } else if ($styleHover.hasClass("activt")) {
                                    $bgPreImgStyle.attr("data-1", "url('" + imgList[0].path + "')");
                                    writeStyle(styleName, "url('" + imgList[0].path + "')",".nav-pills > li > a:hover",".nav-pills li ul.dropdown-menu li a:hover",".ui-state-hover a:hover");
                                    //    changeMainStyle(".nav-pills > li > a:hover", "background-image", "url('" + imgList[0].path + "')")
                                } else if ($styleClick.hasClass("activt")) {
                                    $bgPreImgStyle.attr("data-2", "url('" + imgList[0].path + "')");
                                    writeStyle(styleName, "url('" + imgList[0].path + "')",".nav-pills > li.active > a",".nav-pills li ul.dropdown-menu li a:focus",".ui-tabs-active a:link");
                                    //    changeMainStyle(".nav-pills > li.active > a", "background-image", "url('" + imgList[0].path + "')")
                                }

                                $bgPreImgStyle.css("background-image", "url('" + imgList[0].path + "')");
                                LEHistory.trigger();
                            }
                        },
                        data
                    );
                } else {
                    LEDialog.toggleDialog(LE.options["Dialog"].picUploadDialog, function (jsonUploadImg) {
                        var url = jsonUploadImg.imgPath;

                        if ($styleNormal.hasClass("activt")) {
                            $bgPreImgStyle.attr("data-0", url);
                            writeStyle(styleName,"url('" + url + "')",".nav-pills > li:not(.active):not(:hover) > a",".nav-pills li ul.dropdown-menu li a:not(:hover)",".ui-tabs-nav a");
                            //   changeMainStyle(".nav-pills > li:not(.active):not(:hover) > a", "background-image", "url('" + url + "')")
                        } else if ($styleHover.hasClass("activt")) {
                            $bgPreImgStyle.attr("data-1", url);
                            writeStyle(styleName, "url('" + url + "')",".nav-pills > li > a:hover",".nav-pills li ul.dropdown-menu li a:hover",".ui-state-hover a:hover");
                            //    changeMainStyle(".nav-pills > li > a:hover", "background-image", "url('" + url + "')")
                        } else if ($styleClick.hasClass("activt")) {
                            $bgPreImgStyle.attr("data-2", url);
                            writeStyle(styleName,"url('" + url + "')",".nav-pills > li.active > a",".nav-pills li ul.dropdown-menu li a:focus",".ui-tabs-active a:link");
                            //   changeMainStyle(".nav-pills > li.active > a", "background-image", "url('" + url + "')")
                        }

                        $bgPreImgStyle.attr("src", url);
                        LEHistory.trigger();

                    });
                }

            });

            $PS.find("[id^=bgFileLabel]").hover(function () {
                $.trim($bgPreImgStyle.attr("src")) != "export/images/sliderPanel/sliderPanel1.png" && $bgDeleteImgStyle.show();
            }, function () {
                $bgDeleteImgStyle.hide();
            });

        };


        /*设置交互导航的背景图删除，区分不同状态的a便签进行设置*/
        var initDeleteImgEvent = function (styleName) {
            $bgDeleteImgStyle.click(function () {

                if ($styleNormal.hasClass("activt")) {
                    $bgPreImgStyle.attr("data-0", "export/images/sliderPanel/sliderPanel1.png");
                    writeStyle(styleName,"none",".nav-pills > li:not(.active):not(:hover) > a",".nav-pills li ul.dropdown-menu li a:not(:hover)",".ui-tabs-nav a");
                    //    changeMainStyle(".nav-pills > li:not(.active):not(:hover) > a", "background-image", "none")
                } else if ($styleHover.hasClass("activt")) {
                    $bgPreImgStyle.attr("data-1", "export/images/sliderPanel/sliderPanel1.png");
                    writeStyle(styleName, "none",".nav-pills > li > a:hover",".nav-pills li ul.dropdown-menu li a:hover",".ui-state-hover a:hover");
                    //    changeMainStyle(".nav-pills > li > a:hover", "background-image", "none")
                } else if ($styleClick.hasClass("activt")) {
                    $bgPreImgStyle.attr("data-2", "export/images/sliderPanel/sliderPanel1.png");
                    writeStyle(styleName,"none",".nav-pills > li.active > a",".nav-pills li ul.dropdown-menu li a:focus",".ui-tabs-active a:link");
                    //  changeMainStyle(".nav-pills > li.active > a", "background-image", "none")
                }

                $bgPreImgStyle.attr("src", "");
                $bgPreImgStyle.css("backgroundImage", "");
                LEHistory.trigger();

            });
        };

        /*设置交互导航的背景定位，区分不同状态的a便签进行设置*/
        var initbgPositionEvent = function(styleName){
            $("#bgPosition-ul").children("li:first").addClass("select");
            $bgPosition.click(function(){
                var _x = $(this).attr("data-x");
                var _y = $(this).attr("data-y");
                $bgXStyle.val(_x);
                $bgYStyle.val(_y);

                if ($styleNormal.hasClass("activt")) {
                    $("#bgX-style").attr("data-0",_x+" "+_y);
                    writeStyle(styleName, _x+" "+_y,".nav-pills > li:not(.active):not(:hover) > a",".nav-pills li ul.dropdown-menu li a:not(:hover)",".ui-tabs-nav a");
                    //   changeMainStyle(".nav-pills > li:not(.active):not(:hover) > a", "background-position", _x+" "+_y)
                }  else if ($styleHover.hasClass("activt")) {
                    $("#bgX-style").attr("data-1",_x+" "+_y);
                    writeStyle(styleName,  _x+" "+_y,".nav-pills > li > a:hover",".nav-pills li ul.dropdown-menu li a:hover",".ui-state-hover a:hover");
                    //    changeMainStyle(".nav-pills > li > a:hover", "background-position", _x+" "+_y)
                } else if ($styleClick.hasClass("activt")) {
                    $("#bgX-style").attr("data-2",_x+" "+_y);
                    writeStyle(styleName, _x+" "+_y,".nav-pills > li.active > a",".nav-pills li ul.dropdown-menu li a:focus",".ui-tabs-active a:link");
                    //   changeMainStyle(".nav-pills > li.active > a", "background-position", _x+" "+_y)
                }

                LEHistory.trigger();
            });

        };
        /* 设置背景定位中的横向和纵向，区分不同状态的a便签进行设置 */
        var initbgXYEvent = function(){
            $bgPositionXY.find(".bgxy").onlyNum().keydown(function(e){
                //    debugger
                var _this=$(this);
                var _x = $("#bgX-style").val();
                var _y = $("#bgY-style").val();
                var $target = LECurrentObject;
                numInputKeyDown(e, _this, $target,_x , _y);
            });

            $bgPositionXY.find(".bgxy").blur(function(e){
                var _this=$(this);
                var _x = $("#bgX-style").val();
                var _y = $("#bgY-style").val();
                var $target = LECurrentObject;
                numInputBlur(e, _this, $target,_x , _y);
                LEHistory.trigger();
            });

            $bgPositionXY.find(".bgxy").focus(function(e){
                var _this=$(this);
                var $target = LECurrentObject;
                numInputFocus(e, _this, $target);
            });

        };
        /* 设置背景定位中的重复，区分不同状态的a便签进行设置 */
        var initbgRepeatEvent = function(styleName){
            $("#bgRepeat-ul").children("li:first").addClass("select");
            $bgRepeat.click(function(){
                var selected = $(this).hasClass("select");
                if(selected){
                    $(this).siblings().removeClass("select");
                } else{
                    $(this).addClass("select");
                    $(this).siblings(".bgRepeat").removeClass("select");
                }
                var _p= $(this).attr("data-ref");

                if ($styleNormal.hasClass("activt")) {
                    $bgRepeat.attr("data-0", _p);
                    writeStyle(styleName,_p,".nav-pills > li:not(.active):not(:hover) > a",".nav-pills li ul.dropdown-menu li a:not(:hover)",".ui-tabs-nav a");
                    //   changeMainStyle(".nav-pills > li:not(.active):not(:hover) > a", "background-repeat", _p)
                }  else if ($styleHover.hasClass("activt")) {
                    $bgRepeat.attr("data-1", _p);
                    writeStyle(styleName,  _p,".nav-pills > li > a:hover",".nav-pills li ul.dropdown-menu li a:hover",".ui-state-hover a:hover");
                    //    changeMainStyle(".nav-pills > li > a:hover", "background-repeat", _p)
                } else if ($styleClick.hasClass("activt")) {
                    $bgRepeat.attr("data-2", _p);
                    writeStyle(styleName,_p,".nav-pills > li.active > a",".nav-pills li ul.dropdown-menu li a:focus",".ui-tabs-active a:link");
                    //    changeMainStyle(".nav-pills > li.active > a", "background-repeat", _p)
                }
                LEHistory.trigger();

            });
        };

        //  交互按钮的横向和纵向输入框
        function numInputKeyDown(event, $this,$target,_x , _y){
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

                //设定样式
                if ($styleNormal.hasClass("activt")) {
                    numInputKeyChangeStyle($this,$target,"data-0",num,_unit,_x , _y,".nav-pills > li:not(.active):not(:hover) > a",".nav-pills li ul.dropdown-menu li a:not(:hover)",".ui-tabs-nav a");
                } else if ($styleHover.hasClass("activt")) {
                    numInputKeyChangeStyle($this,$target,"data-1",num,_unit,_x , _y,".nav-pills > li > a:hover",".nav-pills li ul.dropdown-menu li a:hover",".ui-state-hover a:hover");
                } else if ($styleClick.hasClass("activt")) {
                    numInputKeyChangeStyle($this,$target,"data-2",num,_unit,_x , _y,".nav-pills > li.active > a",".nav-pills li ul.dropdown-menu li a:focus",".ui-tabs-active a:link");
                }

                $this.val(num + _unit);
                return num;
            }
            return null;
        };
        function checkUnit(_value, $this){
            //如果包含%，就以%为主
            if(_value.indexOf("%") != -1){
                $this.attr("data-unit", "%");
            } else if(_value.indexOf("px") != -1){
                $this.attr("data-unit", "px");
            }
        };
        //交互按钮的横向和纵向输入框失去焦点
        function numInputBlur(event, $this,$target,_x , _y){
            if ($styleNormal.hasClass("activt")) {
                numInputBlurChangeStyle($this,$target,"data-0",_x , _y,".nav-pills > li:not(.active):not(:hover) > a",".nav-pills li ul.dropdown-menu li a:not(:hover)",".ui-tabs-nav a");
            } else if ($styleHover.hasClass("activt")) {
                numInputBlurChangeStyle($this,$target,"data-1",_x , _y,".nav-pills > li > a:hover",".nav-pills li ul.dropdown-menu li a:hover",".ui-state-hover a:hover");
            } else if ($styleClick.hasClass("activt")) {
                numInputBlurChangeStyle($this,$target,"data-2",_x , _y,".nav-pills > li.active > a",".nav-pills li ul.dropdown-menu li a:focus",".ui-tabs-active a:link");
            }
        };
        //交互按钮的横向和纵向输入框获取焦点
        function numInputFocus(event, $this, $target){
            if ($styleNormal.hasClass("activt")) {
                numInputFocusChangeStyle($this,$target,"data-0")
            } else if ($styleHover.hasClass("activt")) {
                numInputFocusChangeStyle($this,$target,"data-1")
            } else if ($styleClick.hasClass("activt")) {
                numInputFocusChangeStyle($this,$target,"data-2")
            }
        };

        function numInputKeyChangeStyle($this,$target,_data,num,_unit,_x ,_y,mainSelector,subSelector,tabSelector){
            var partSelectorVal="";
            var _setting = $target.attr("data-setting");
            if(_setting=="mainNav"){
                partSelectorVal=mainSelector;
            }else if(_setting=="subNav"){
                partSelectorVal=subSelector;
            }else if(_setting=="tab"){
                partSelectorVal=tabSelector;
            }

            if($this.attr("data-ref")=="background-position-x") {
                $bgXStyle.attr(_data, num + _unit +" "+_y);
                changeMainStyle(partSelectorVal, "background-position",num + _unit +" "+_y);
            }else{
                $bgXStyle.attr(_data,  _x +" "+num + _unit);
                changeMainStyle(partSelectorVal, "background-position", _x +" "+num + _unit);
            }

        }

        function numInputBlurChangeStyle($this,$target,_data,_x , _y,mainSelector,subSelector,tabSelector){
            var partSelectorVal="";
            var _setting = $target.attr("data-setting");
            if(_setting=="mainNav"){
                partSelectorVal=mainSelector;
            }else if(_setting=="subNav"){
                partSelectorVal=subSelector;
            }else if(_setting=="tab"){
                partSelectorVal=tabSelector;
            }

            var _value = $this.val();
            var num = parseInt(_value);
            if(isNaN(num)) return;
            checkUnit(_value, $this);
            var num = parseInt(_value);
            var _unit = $this.attr("data-unit") || "px";
            //设定样式

            if($this.attr("data-ref")=="background-position-x") {
                $bgXStyle.attr(_data, num + _unit +" "+_y);
                changeMainStyle(partSelectorVal, "background-position", num + _unit +" "+_y);
            }else{
                $bgXStyle.attr(_data,  _x +" "+num + _unit);
                changeMainStyle(partSelectorVal, "background-position",  _x +" "+num + _unit);
            }
            $this.val(num + _unit);
        }

        function numInputFocusChangeStyle($this,$target,_data){
            var _setting = $target.attr("data-setting");
            var _x = $("#bgX-style").val();
            var _y = $("#bgY-style").val();
            if(_setting=="mainNav"){
                $bgXStyle.attr(_data , _x+" "+_y);
            }else if(_setting=="subNav"){
                $bgXStyle.attr(_data , _x+" "+_y);
            }else if(_setting=="tab") {
                $bgXStyle.attr(_data, _x + " " + _y);
            }
        }


        /*初始化主导航交互样式的按钮状态--背景色
         * 根据样式优先级
         * 1、判断有没有自定义样式，如果有,将style标签中的自定义样式初始化到按钮
         * 2、没有自定义样式 则判断有没有模板样式，如果有，将对应的模板样式初始化到按钮
         * 3、否则 将默认样式初始化到按钮
         * */
        var resetBackgroundColor = function (styleName,defaultHoverVal,defaultClickVal) {
            var _setting = LECurrentObject.attr("data-setting");
            var target = LECurrentObject.attr("id");
            if(_setting=="mainNav") {
                var bgColorNormal = LECurrentObject.find(".nav-pills > li:not(.active):not(:hover) a").css(styleName);
                var bgColorClick = LECurrentObject.find(".nav-pills > li.active > a").css(styleName);
                var bgColorClick = !bgColorClick ? $inputBGColorPickStyle.attr("data-2") : LECurrentObject.find(".nav-pills > li.active > a").css(styleName);
                var bgColorNormal = !bgColorNormal ? $inputBGColorPickStyle.attr("data-0") : LECurrentObject.find(".nav-pills > li:not(.active):not(:hover) a").css(styleName);
                var bgColorHover = StyleManager.getStyle("#" + target + " .nav-pills > li > a:hover", styleName);
                if (bgColorHover == "none") {
                    if (LECurrentObject.find("ul.nav-pills").attr("data-prestyle")) {
                        //2、否则如果样式中存在,当前对象的最外层ul是否具有"data-prestyle"属性 属性值对应class名
                        var dataPrestyle = LECurrentObject.find("ul.nav-pills").attr("data-prestyle");
                        bgColorHover = modelStyleManager.getStyle("." + dataPrestyle + " > li > a:hover", styleName);
                    } else {
                        //3否则默认样式
                        bgColorHover = defaultHoverVal;
                    }
                } else {
                    //1、如果html中存在自定义选择器
                    bgColorHover = StyleManager.getStyle("#" + target + " .nav-pills > li > a:hover", styleName);
                }

            }else if(_setting=="subNav"){
                var bgColorNormal = LECurrentObject.find(".nav-pills li ul.dropdown-menu li a:not(:hover)").css(styleName);
                //   var bgColorClick = LECurrentObject.find(".nav-pills li ul.dropdown-menu li a:focus").css(styleName);
                //    var bgColorClick = !bgColorClick ? $inputBGColorPickStyle.attr("data-2") : LECurrentObject.find(".nav-pills li ul.dropdown-menu li a:focus").css(styleName);
                var bgColorNormal = !bgColorNormal ? $inputBGColorPickStyle.attr("data-0") : LECurrentObject.find(".nav-pills li ul.dropdown-menu li a:not(:hover)").css(styleName);
                var bgColorHover = StyleManager.getStyle("#" + target + " .nav-pills li ul.dropdown-menu li a:hover", styleName);
                var bgColorClick = StyleManager.getStyle("#" + target + " .nav-pills li ul.dropdown-menu li a:focus", styleName);
                if (bgColorHover == "none") {
                    if (LECurrentObject.find("ul.dropdown-menu").attr("data-prestyle")) {
                        //2、否则如果样式中存在,当前对象的最外层ul是否具有"data-prestyle"属性 属性值对应class名
                        var dataPrestyle = LECurrentObject.find("ul.dropdown-menu").attr("data-prestyle");
                        bgColorHover = modelStyleManager.getStyle("." + dataPrestyle + " li a:hover" , styleName);
                    } else {
                        //3否则默认样式
                        bgColorHover = defaultHoverVal;
                    }
                } else {
                    //1、如果html中存在自定义选择器
                    bgColorHover = StyleManager.getStyle("#" + target + " .nav-pills li ul.dropdown-menu li a:hover" , styleName);
                }
                if (bgColorClick == "none") {
                    if (LECurrentObject.find("ul.dropdown-menu").attr("data-prestyle")) {
                        //2、否则如果样式中存在,当前对象的最外层ul是否具有"data-prestyle"属性 属性值对应class名
                        var dataPrestyle = LECurrentObject.find("ul.dropdown-menu").attr("data-prestyle");
                        bgColorClick = modelStyleManager.getStyle("." + dataPrestyle + " li a:focus", styleName);
                    } else {
                        //3否则默认样式
                        bgColorClick = defaultClickVal;
                    }
                } else {
                    //1、如果html中存在自定义选择器
                    bgColorClick = StyleManager.getStyle("#" + target + " .nav-pills li ul.dropdown-menu li a:focus", styleName);
                }
            }else if(_setting=="tab"){
                var bgColorNormal = LECurrentObject.find(".ui-tabs-nav a:not(:first)").css(styleName);
                //  var bgColorClick = LECurrentObject.find(".ui-tabs-active a:link").css(styleName);
                //  var bgColorClick = !bgColorClick ? $inputBGColorPickStyle.attr("data-2") : LECurrentObject.find(".ui-tabs-active a:link").css(styleName);
                var bgColorNormal = !bgColorNormal ? $inputBGColorPickStyle.attr("data-0") : LECurrentObject.find(".ui-tabs-nav a:not(:first)").css(styleName);
                var bgColorHover = StyleManager.getStyle("#" + target + " .ui-state-hover a:hover" , styleName);
                var bgColorClick = StyleManager.getStyle("#" + target + " .ui-tabs-active a:link", styleName);
                if (bgColorHover == "none") {
                    if (LECurrentObject.find("ul.ui-tabs-nav").attr("data-prestyle")) {
                        //2、否则如果样式中存在,当前对象的最外层ul是否具有"data-prestyle"属性 属性值对应class名
                        var dataPrestyle = LECurrentObject.find("ul.ui-tabs-nav").attr("data-prestyle");
                        bgColorHover = modelStyleManager.getStyle(".ui-tabs ." + dataPrestyle + " > li.ui-state-hover > a", styleName);
                    } else {
                        //3否则默认样式
                        bgColorHover = defaultHoverVal;
                    }
                } else {
                    //1、如果html中存在自定义选择器
                    bgColorHover = StyleManager.getStyle("#" + target + " .ui-state-hover a:hover" , styleName);
                }
                if (bgColorClick == "none") {
                    if (LECurrentObject.find("ul.ui-tabs-nav").attr("data-prestyle")) {
                        //2、否则如果样式中存在,当前对象的最外层ul是否具有"data-prestyle"属性 属性值对应class名
                        var dataPrestyle = LECurrentObject.find("ul.ui-tabs-nav").attr("data-prestyle");
                        bgColorClick = modelStyleManager.getStyle(".ui-tabs ." + dataPrestyle + " > li.ui-tabs-active > a", styleName);
                    } else {
                        //3否则默认样式
                        bgColorClick = defaultClickVal;
                    }
                } else {
                    //1、如果html中存在自定义选择器
                    bgColorClick = StyleManager.getStyle("#" + target + " .ui-tabs-active a:link", styleName);
                }
            }
            $bgColorPickStyle.attr("data-0", bgColorNormal);
            $bgColorPickStyle.attr("data-1", bgColorHover);
            $bgColorPickStyle.attr("data-2", bgColorClick);

            if ($styleNormal.hasClass("activt")) {
                $bgColorPickStyle.spectrum("set", bgColorNormal);
                $inputBGColorPickStyle.val($bgColorPickStyle.spectrum("get").toHexString());
            } else if ($styleHover.hasClass("activt")) {
                $bgColorPickStyle.spectrum("set", bgColorHover);
                $inputBGColorPickStyle.val($bgColorPickStyle.spectrum("get").toHexString());
            } else if ($styleClick.hasClass("activt")) {
                $bgColorPickStyle.spectrum("set", bgColorClick);
                $inputBGColorPickStyle.val($bgColorPickStyle.spectrum("get").toHexString());
            }

        };

        /*初始化主导航交互样式的按钮状态--背景图*/
        var resetBackgroundImg = function (styleName,defaultVal) {
            var target = LECurrentObject.attr("id");
            var _setting = LECurrentObject.attr("data-setting");
            if(_setting=="mainNav") {
                var bgImgNormal = LECurrentObject.find(".nav-pills > li:not(.active):not(:hover) a").css("background-image");
                var bgImgClick = LECurrentObject.find(".nav-pills > li.active > a").css("background-image");
                var bgImgClick = !bgImgClick ? $bgPreImgStyle.attr("data-2") : LECurrentObject.find(".nav-pills > li.active > a").css("background-image");
                var bgImgNormal = !bgImgNormal ? $bgPreImgStyle.attr("data-0") : LECurrentObject.find(".nav-pills > li:not(.active):not(:hover) a").css("background-image");
                var bgImgHover = StyleManager.getStyle("#" + target + " .nav-pills > li > a:hover", "background-image");
                if (bgImgHover == "none") {
                    if (LECurrentObject.find("ul.nav-pills").attr("data-prestyle")) {
                        //2、否则如果样式中存在,当前对象的最外层ul是否具有"data-prestyle"属性 属性值对应class名
                        var dataPrestyle = LECurrentObject.find("ul.nav-pills").attr("data-prestyle");
                        bgImgHover = modelStyleManager.getStyle("." + dataPrestyle + " > li > a:hover", "background-image");
                    } else {
                        //3否则默认样式
                        bgImgHover = defaultVal;
                    }
                } else {
                    //1、如果html中存在自定义选择器
                    bgImgHover = StyleManager.getStyle("#" + target + " .nav-pills > li > a:hover", "background-image");
                }
            }else if(_setting=="subNav"){
                var bgImgNormal = LECurrentObject.find(".nav-pills li ul.dropdown-menu li a:not(:hover)").css(styleName);
                //   var bgImgClick = LECurrentObject.find(".nav-pills li ul.dropdown-menu li a:focus").css(styleName);
                //    var bgImgClick = !bgImgClick ? $bgPreImgStyle.attr("data-2") : LECurrentObject.find(".nav-pills li ul.dropdown-menu li a:focus").css(styleName);
                var bgImgNormal = !bgImgNormal ? $bgPreImgStyle.attr("data-0") : LECurrentObject.find(".nav-pills li ul.dropdown-menu li a:not(:hover)").css(styleName);
                var bgImgHover = StyleManager.getStyle("#" + target + " .nav-pills li ul.dropdown-menu li a:hover", styleName);
                var bgImgClick = StyleManager.getStyle("#" + target + " .nav-pills li ul.dropdown-menu li a:focus", styleName);
                if (bgImgHover == "none") {
                    if (LECurrentObject.find("ul.dropdown-menu").attr("data-prestyle")) {
                        //2、否则如果样式中存在,当前对象的最外层ul是否具有"data-prestyle"属性 属性值对应class名
                        var dataPrestyle = LECurrentObject.find("ul.dropdown-menu").attr("data-prestyle");
                        bgImgHover = modelStyleManager.getStyle("." + dataPrestyle + " li a:hover" , styleName);
                    } else {
                        //3否则默认样式
                        bgImgHover = defaultVal;
                    }
                } else {
                    //1、如果html中存在自定义选择器
                    bgImgHover = StyleManager.getStyle("#" + target + " .nav-pills li ul.dropdown-menu li a:hover" , styleName);
                }
                if (bgImgClick == "none") {
                    if (LECurrentObject.find("ul.dropdown-menu").attr("data-prestyle")) {
                        //2、否则如果样式中存在,当前对象的最外层ul是否具有"data-prestyle"属性 属性值对应class名
                        var dataPrestyle = LECurrentObject.find("ul.dropdown-menu").attr("data-prestyle");
                        bgImgClick = modelStyleManager.getStyle("." + dataPrestyle + " li a:focus", styleName);
                    } else {
                        //3否则默认样式
                        bgImgClick = defaultVal;
                    }
                } else {
                    //1、如果html中存在自定义选择器
                    bgImgClick = StyleManager.getStyle("#" + target + " .nav-pills li ul.dropdown-menu li a:focus", styleName);
                }
            }else if(_setting=="tab"){
                var bgImgNormal = LECurrentObject.find(".ui-tabs-nav a:not(:first)").css(styleName);
                //  var bgImgClick = LECurrentObject.find(".ui-tabs-active a:link").css(styleName);
                //  var bgImgClick = !bgImgClick ? $bgPreImgStyle.attr("data-2") : LECurrentObject.find(".ui-tabs-active a:link").css(styleName);
                var bgImgNormal = !bgImgNormal ? $bgPreImgStyle.attr("data-0") : LECurrentObject.find(".ui-tabs-nav a:not(:first)").css(styleName);
                var bgImgHover = StyleManager.getStyle("#" + target + " .ui-state-hover a:hover" , styleName);
                var bgImgClick = StyleManager.getStyle("#" + target + " .ui-tabs-active a:link", styleName);
                if (bgImgHover == "none") {
                    if (LECurrentObject.find("ul.ui-tabs-nav").attr("data-prestyle")) {
                        //2、否则如果样式中存在,当前对象的最外层ul是否具有"data-prestyle"属性 属性值对应class名
                        var dataPrestyle = LECurrentObject.find("ul.ui-tabs-nav").attr("data-prestyle");
                        bgImgHover = modelStyleManager.getStyle(".ui-tabs ." + dataPrestyle + " > li.ui-state-hover > a", styleName);
                    } else {
                        //3否则默认样式
                        bgImgHover = defaultVal;
                    }
                } else {
                    //1、如果html中存在自定义选择器
                    bgImgHover = StyleManager.getStyle("#" + target + " .ui-state-hover a:hover" , styleName);
                }
                if (bgImgClick == "none") {
                    if (LECurrentObject.find("ul.ui-tabs-nav").attr("data-prestyle")) {
                        //2、否则如果样式中存在,当前对象的最外层ul是否具有"data-prestyle"属性 属性值对应class名
                        var dataPrestyle = LECurrentObject.find("ul.ui-tabs-nav").attr("data-prestyle");
                        bgImgClick = modelStyleManager.getStyle(".ui-tabs ." + dataPrestyle + " > li.ui-tabs-active > a", styleName);
                    } else {
                        //3否则默认样式
                        bgImgClick = defaultVal;
                    }
                } else {
                    //1、如果html中存在自定义选择器
                    bgImgClick = StyleManager.getStyle("#" + target + " .ui-tabs-active a:link", styleName);
                }
            }

            if (bgImgNormal != "") {
                bgImgNormal = bgImgNormal.replace(/url\([\"\']?/g, "").replace(/[\"\']?\)/g, "");
            }
            if (bgImgHover != "") {
                bgImgHover = bgImgHover.replace(/url\([\"\']?/g, "").replace(/[\"\']?\)/g, "");
            }
            if (bgImgClick != "") {
                bgImgClick = bgImgClick.replace(/url\([\"\']?/g, "").replace(/[\"\']?\)/g, "");
            }

            $bgPreImgStyle.attr("data-0", bgImgNormal);
            $bgPreImgStyle.attr("data-1", bgImgHover);
            $bgPreImgStyle.attr("data-2", bgImgClick);

            if ($styleNormal.hasClass("activt")) {
                $bgPreImgStyle.attr("src", bgImgNormal);
            } else if ($styleHover.hasClass("activt")) {
                $bgPreImgStyle.attr("src", bgImgHover);
            } else if ($styleClick.hasClass("activt")) {
                $bgPreImgStyle.attr("src", bgImgClick);
            }
        };
        /*初始化主导航交互样式的按钮状态--定位*/
        var resetBackgroundPosition = function(styleName,defaultVal){
            var _setting = LECurrentObject.attr("data-setting");
            var target = LECurrentObject.attr("id");
            if(_setting=="mainNav") {
                var bgPositionNormal = LECurrentObject.find(".nav-pills > li:not(.active):not(:hover) a").css(styleName);
                var bgPositionClick = LECurrentObject.find(".nav-pills > li.active > a").css(styleName);
                var bgPositionClick = !bgPositionClick ? $bgXStyle.attr("data-2") : LECurrentObject.find(".nav-pills > li.active > a").css(styleName);
                var bgPositionNormal = !bgPositionNormal ? $bgXStyle.attr("data-0") : LECurrentObject.find(".nav-pills > li:not(.active):not(:hover) a").css(styleName);
                var bgPositionHover = StyleManager.getStyle("#" + target + " .nav-pills > li > a:hover", styleName);
                if (bgPositionHover == "none") {
                    if (LECurrentObject.find("ul.nav-pills").attr("data-prestyle")) {
                        //2、否则如果样式中存在,当前对象的最外层ul是否具有"data-prestyle"属性 属性值对应class名
                        var dataPrestyle = LECurrentObject.find("ul.nav-pills").attr("data-prestyle");
                        //   bgPositionHover = modelStyleManager.getStyle("." + dataPrestyle + " > li > a:hover", styleName);
                        bgPositionHover = defaultVal;
                    } else {
                        //3否则默认样式
                        bgPositionHover = defaultVal;
                    }
                } else {
                    //1、如果html中存在自定义选择器
                    bgPositionHover = StyleManager.getStyle("#" + target + " .nav-pills > li > a:hover", styleName);
                }

            }else if(_setting=="subNav"){
                var bgPositionNormal = LECurrentObject.find(".nav-pills li ul.dropdown-menu li a:not(:hover)").css(styleName);
                //   var bgPositionClick = LECurrentObject.find(".nav-pills li ul.dropdown-menu li a:focus").css(styleName);
                //    var bgPositionClick = !bgPositionClick ? $bgXStyle.attr("data-2") : LECurrentObject.find(".nav-pills li ul.dropdown-menu li a:focus").css(styleName);
                var bgPositionNormal = !bgPositionNormal ? $bgXStyle.attr("data-0") : LECurrentObject.find(".nav-pills li ul.dropdown-menu li a:not(:hover)").css(styleName);
                var bgPositionHover = StyleManager.getStyle("#" + target + " .nav-pills li ul.dropdown-menu li a:hover", styleName);
                var bgPositionClick = StyleManager.getStyle("#" + target + " .nav-pills li ul.dropdown-menu li a:focus", styleName);
                if (bgPositionHover == "none") {
                    if (LECurrentObject.find("ul.dropdown-menu").attr("data-prestyle")) {
                        //2、否则如果样式中存在,当前对象的最外层ul是否具有"data-prestyle"属性 属性值对应class名
                        var dataPrestyle = LECurrentObject.find("ul.dropdown-menu").attr("data-prestyle");
                        //   bgPositionHover = modelStyleManager.getStyle("." + dataPrestyle + " li a:hover" , styleName);
                        bgPositionHover = defaultVal;
                    } else {
                        //3否则默认样式
                        bgPositionHover = defaultVal;
                    }
                } else {
                    //1、如果html中存在自定义选择器
                    bgPositionHover = StyleManager.getStyle("#" + target + " .nav-pills li ul.dropdown-menu li a:hover" , styleName);
                }
                if (bgPositionClick == "none") {
                    if (LECurrentObject.find("ul.dropdown-menu").attr("data-prestyle")) {
                        //2、否则如果样式中存在,当前对象的最外层ul是否具有"data-prestyle"属性 属性值对应class名
                        var dataPrestyle = LECurrentObject.find("ul.dropdown-menu").attr("data-prestyle");
                        //  bgPositionClick = modelStyleManager.getStyle("." + dataPrestyle + " li a:focus", styleName);
                        bgPositionClick = defaultVal;
                    } else {
                        //3否则默认样式
                        bgPositionClick = defaultVal;
                    }
                } else {
                    //1、如果html中存在自定义选择器
                    bgPositionClick = StyleManager.getStyle("#" + target + " .nav-pills li ul.dropdown-menu li a:focus", styleName);
                }
            }else if(_setting=="tab"){
                var bgPositionNormal = LECurrentObject.find(".ui-tabs-nav a:not(:first)").css(styleName);
                //  var bgPositionClick = LECurrentObject.find(".ui-tabs-active a:link").css(styleName);
                //  var bgPositionClick = !bgPositionClick ? $bgXStyle.attr("data-2") : LECurrentObject.find(".ui-tabs-active a:link").css(styleName);
                var bgPositionNormal = !bgPositionNormal ? $bgXStyle.attr("data-0") : LECurrentObject.find(".ui-tabs-nav a:not(:first)").css(styleName);
                var bgPositionHover = StyleManager.getStyle("#" + target + " .ui-state-hover a:hover" , styleName);
                var bgPositionClick = StyleManager.getStyle("#" + target + " .ui-tabs-active a:link", styleName);
                if (bgPositionHover == "none") {
                    if (LECurrentObject.find("ul.ui-tabs-nav").attr("data-prestyle")) {
                        //2、否则如果样式中存在,当前对象的最外层ul是否具有"data-prestyle"属性 属性值对应class名
                        var dataPrestyle = LECurrentObject.find("ul.ui-tabs-nav").attr("data-prestyle");
                        //   bgPositionHover = modelStyleManager.getStyle(".ui-tabs ." + dataPrestyle + " > li.ui-state-hover > a", styleName);
                        bgPositionHover = defaultVal;
                    } else {
                        //3否则默认样式
                        bgPositionHover = defaultVal;
                    }
                } else {
                    //1、如果html中存在自定义选择器
                    bgPositionHover = StyleManager.getStyle("#" + target + " .ui-state-hover a:hover" , styleName);
                }
                if (bgPositionClick == "none") {
                    if (LECurrentObject.find("ul.ui-tabs-nav").attr("data-prestyle")) {
                        //2、否则如果样式中存在,当前对象的最外层ul是否具有"data-prestyle"属性 属性值对应class名
                        var dataPrestyle = LECurrentObject.find("ul.ui-tabs-nav").attr("data-prestyle");
                        //   bgPositionClick = modelStyleManager.getStyle(".ui-tabs ." + dataPrestyle + " > li.ui-tabs-active > a", styleName);
                        bgPositionClick = defaultVal;
                    } else {
                        //3否则默认样式
                        bgPositionClick = defaultVal;
                    }
                } else {
                    //1、如果html中存在自定义选择器
                    bgPositionClick = StyleManager.getStyle("#" + target + " .ui-tabs-active a:link", styleName);
                }
            }

            //  $bgPosition.attr("data-0", bgPositionNormal);
            //   $bgPosition.attr("data-1", bgPositionHover);
            //  $bgPosition.attr("data-2", bgPositionClick);

            $bgXStyle.attr("data-0", bgPositionNormal);
            $bgXStyle.attr("data-1", bgPositionHover);
            $bgXStyle.attr("data-2", bgPositionClick);

            if ($styleNormal.hasClass("activt")) {
                bgPositionNormal= $bgXStyle.attr("data-0");
                var arr=bgPositionNormal.split(" ");
                $bgXStyle.val(arr[0]);
                $bgYStyle.val(arr[1]);

                $bgPosition.each(function(){
                    var _this=$(this);
                    var _x=_this.attr("data-x");
                    var _y=_this.attr("data-y");
                    if(_x===arr[0] && _y===arr[1]){
                        _this.addClass("select").siblings().removeClass("select");
                    }
                });

            }  else if ($styleHover.hasClass("activt")) {
                bgPositionHover= $bgXStyle.attr("data-1");
                var arr=bgPositionHover.split(" ");
                $bgXStyle.val(arr[0]);
                $bgYStyle.val(arr[1]);

                $bgPosition.each(function(){
                    var _this=$(this);
                    var _x=_this.attr("data-x");
                    var _y=_this.attr("data-y");
                    if(_x===arr[0] && _y===arr[1]){
                        _this.addClass("select").siblings().removeClass("select");
                    }
                });

            } else if ($styleClick.hasClass("activt")) {
                bgPositionClick= $bgXStyle.attr("data-2");
                var arr=bgPositionClick.split(" ");
                $bgXStyle.val(arr[0]);
                $bgYStyle.val(arr[1]);

                $bgPosition.each(function(){
                    var _this=$(this);
                    var _x=_this.attr("data-x");
                    var _y=_this.attr("data-y");
                    if(_x===arr[0] && _y===arr[1]){
                        _this.addClass("select").siblings().removeClass("select");
                    }
                });

            }

        };

        /*初始化主导航交互样式的按钮状态--重复*/
        var resetBackgroundRepeat = function(styleName,defaultVal){
            var _setting = LECurrentObject.attr("data-setting");
            var target = LECurrentObject.attr("id");
            if(_setting=="mainNav") {
                var bgRepeatNormal = LECurrentObject.find(".nav-pills > li:not(.active):not(:hover) a").css(styleName);
                var bgRepeatClick = LECurrentObject.find(".nav-pills > li.active > a").css(styleName);
                var bgRepeatClick = !bgRepeatClick ? $bgRepeat.attr("data-2") : LECurrentObject.find(".nav-pills > li.active > a").css(styleName);
                var bgRepeatNormal = !bgRepeatNormal ? $bgRepeat.attr("data-0") : LECurrentObject.find(".nav-pills > li:not(.active):not(:hover) a").css(styleName);
                var bgRepeatHover = StyleManager.getStyle("#" + target + " .nav-pills > li > a:hover", styleName);
                if (bgRepeatHover == "none") {
                    if (LECurrentObject.find("ul.nav-pills").attr("data-prestyle")) {
                        //2、否则如果样式中存在,当前对象的最外层ul是否具有"data-prestyle"属性 属性值对应class名
                        var dataPrestyle = LECurrentObject.find("ul.nav-pills").attr("data-prestyle");
                        //    bgRepeatHover = modelStyleManager.getStyle("." + dataPrestyle + " > li > a:hover", styleName);
                        bgRepeatHover = defaultVal;
                    } else {
                        //3否则默认样式
                        bgRepeatHover = defaultVal;
                    }
                } else {
                    //1、如果html中存在自定义选择器
                    bgRepeatHover = StyleManager.getStyle("#" + target + " .nav-pills > li > a:hover", styleName);
                }

            }else if(_setting=="subNav"){
                var bgRepeatNormal = LECurrentObject.find(".nav-pills li ul.dropdown-menu li a:not(:hover)").css(styleName);
                //   var bgRepeatClick = LECurrentObject.find(".nav-pills li ul.dropdown-menu li a:focus").css(styleName);
                //    var bgRepeatClick = !bgRepeatClick ? $bgRepeat.attr("data-2") : LECurrentObject.find(".nav-pills li ul.dropdown-menu li a:focus").css(styleName);
                var bgRepeatNormal = !bgRepeatNormal ? $bgRepeat.attr("data-0") : LECurrentObject.find(".nav-pills li ul.dropdown-menu li a:not(:hover)").css(styleName);
                var bgRepeatHover = StyleManager.getStyle("#" + target + " .nav-pills li ul.dropdown-menu li a:hover", styleName);
                var bgRepeatClick = StyleManager.getStyle("#" + target + " .nav-pills li ul.dropdown-menu li a:focus", styleName);
                if (bgRepeatHover == "none") {
                    if (LECurrentObject.find("ul.dropdown-menu").attr("data-prestyle")) {
                        //2、否则如果样式中存在,当前对象的最外层ul是否具有"data-prestyle"属性 属性值对应class名
                        var dataPrestyle = LECurrentObject.find("ul.dropdown-menu").attr("data-prestyle");
                        // bgRepeatHover = modelStyleManager.getStyle("." + dataPrestyle + " li a:hover" , styleName);
                        bgRepeatHover = defaultVal;
                    } else {
                        //3否则默认样式
                        bgRepeatHover = defaultVal;
                    }
                } else {
                    //1、如果html中存在自定义选择器
                    bgRepeatHover = StyleManager.getStyle("#" + target + " .nav-pills li ul.dropdown-menu li a:hover" , styleName);
                }
                if (bgRepeatClick == "none") {
                    if (LECurrentObject.find("ul.dropdown-menu").attr("data-prestyle")) {
                        //2、否则如果样式中存在,当前对象的最外层ul是否具有"data-prestyle"属性 属性值对应class名
                        var dataPrestyle = LECurrentObject.find("ul.dropdown-menu").attr("data-prestyle");
                        //  bgRepeatClick = modelStyleManager.getStyle("." + dataPrestyle + " li a:focus", styleName);
                        bgRepeatClick = defaultVal;
                    } else {
                        //3否则默认样式
                        bgRepeatClick = defaultVal;
                    }
                } else {
                    //1、如果html中存在自定义选择器
                    bgRepeatClick = StyleManager.getStyle("#" + target + " .nav-pills li ul.dropdown-menu li a:focus", styleName);
                }
            }else if(_setting=="tab"){
                var bgRepeatNormal = LECurrentObject.find(".ui-tabs-nav a:not(:first)").css(styleName);
                //  var bgRepeatClick = LECurrentObject.find(".ui-tabs-active a:link").css(styleName);
                //  var bgRepeatClick = !bgRepeatClick ? $bgRepeat.attr("data-2") : LECurrentObject.find(".ui-tabs-active a:link").css(styleName);
                var bgRepeatNormal = !bgRepeatNormal ? $bgRepeat.attr("data-0") : LECurrentObject.find(".ui-tabs-nav a:not(:first)").css(styleName);
                var bgRepeatHover = StyleManager.getStyle("#" + target + " .ui-state-hover a:hover" , styleName);
                var bgRepeatClick = StyleManager.getStyle("#" + target + " .ui-tabs-active a:link", styleName);
                if (bgRepeatHover == "none") {
                    if (LECurrentObject.find("ul.ui-tabs-nav").attr("data-prestyle")) {
                        //2、否则如果样式中存在,当前对象的最外层ul是否具有"data-prestyle"属性 属性值对应class名
                        var dataPrestyle = LECurrentObject.find("ul.ui-tabs-nav").attr("data-prestyle");
                        //   bgRepeatHover = modelStyleManager.getStyle(".ui-tabs ." + dataPrestyle + " > li.ui-state-hover > a", styleName);
                        bgRepeatHover = defaultVal;
                    } else {
                        //3否则默认样式
                        bgRepeatHover = defaultVal;
                    }
                } else {
                    //1、如果html中存在自定义选择器
                    bgRepeatHover = StyleManager.getStyle("#" + target + " .ui-state-hover a:hover" , styleName);
                }
                if (bgRepeatClick == "none") {
                    if (LECurrentObject.find("ul.ui-tabs-nav").attr("data-prestyle")) {
                        //2、否则如果样式中存在,当前对象的最外层ul是否具有"data-prestyle"属性 属性值对应class名
                        var dataPrestyle = LECurrentObject.find("ul.ui-tabs-nav").attr("data-prestyle");
                        //   bgRepeatClick = modelStyleManager.getStyle(".ui-tabs ." + dataPrestyle + " > li.ui-tabs-active > a", styleName);
                        bgRepeatClick = defaultVal;
                    } else {
                        //3否则默认样式
                        bgRepeatClick = defaultVal;
                    }
                } else {
                    //1、如果html中存在自定义选择器
                    bgRepeatClick = StyleManager.getStyle("#" + target + " .ui-tabs-active a:link", styleName);
                }
            }

            $bgRepeat.attr("data-0", bgRepeatNormal);
            $bgRepeat.attr("data-1", bgRepeatHover);
            $bgRepeat.attr("data-2", bgRepeatClick);

            if ($styleNormal.hasClass("activt")) {

                $bgRepeat.each(function(){
                    var _value = $(this).attr("data-ref");
                    if(_value ==bgRepeatNormal){
                        $(this).addClass("select").siblings().removeClass("select");
                    }else{
                        $(this).removeClass("select");
                    }
                });

            } else if ($styleHover.hasClass("activt")) {

                $bgRepeat.each(function(){
                    var _value = $(this).attr("data-ref");
                    if(_value ==bgRepeatHover){
                        $(this).addClass("select").siblings().removeClass("select");
                    }else{
                        $(this).removeClass("select");
                    }
                });


            } else if ($styleClick.hasClass("activt")) {

                $bgRepeat.each(function(){
                    var _value = $(this).attr("data-ref");
                    if(_value ==bgRepeatClick){
                        $(this).addClass("select").siblings().removeClass("select");
                    }else{
                        $(this).removeClass("select");
                    }
                });

            }

        };


        return {
            init: function ($PC, layer) {

                initBgColorEvent("background-color");
                initUploadImgEvent("background-image");
                initDeleteImgEvent("background-image");
                initbgPositionEvent("background-position");
                initbgXYEvent();
                initbgRepeatEvent("background-repeat");


            },
            run: function (options, doHide, doSlide) {
                if (LECurrentObject.children().length > 0) {
                    resetBackgroundColor("background-color","#eeeeee","#337ab7");
                    resetBackgroundImg("background-image","export/images/sliderPanel/sliderPanel1.png");
                    resetBackgroundPosition("background-position","0% 0%");
                    resetBackgroundRepeat("background-repeat","repeat");

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
            buttonReset: function (_this) {
                resetBackgroundStyleButton(_this);
            }

        };

    };

})(window, jQuery, LE, undefined);
