/**
 * Created by isaac_gu on 2016/1/14.
 */
(function(window, $, LE){
    LE.stylesheets["css-BolderSetting"] = function(){
        var $panel = $("#css-bolderSection");
        return {
            init: function(){
                LE.oBorderCss = new OBolder($panel, 0);
            },
            run: function(options, doHide, doSlide){
                var $t = LE.oBorderCss.getCurrentObject();
                if($t.size() > 0){
                    LE.oBorderCss.reset();
                    //切换组件时，样式设置部分回顶部
                    sliderbarToTop();
                }

                LEDisplay.show($panel, doHide, doSlide);
            },
            destroy: function(){
                LE.oBorderCss.destroy();
            }
        };
    };

    LE.stylesheets["css-BolderSettingII"] = function(){
        var $panel = $("#css-bolderSection-II");
        return {
            init: function(){
                LE.oBorderIICss = new OBolder($panel, 1);
            },
            run: function(options, doHide, doSlide){
                var $t = LE.oBorderIICss.getCurrentObject();
                if($t.size() > 0){
                    LE.oBorderIICss.reset();
                    //切换组件时，样式设置部分回顶部
                    sliderbarToTop();
                }

                LEDisplay.show($panel, doHide, doSlide);
            },
            destroy: function(){
                LE.oBorderIICss.destroy();
            }
        };
    };

    LE.stylesheets["css-BolderSettingIII"] = function(){
        var $panel = $("#css-bolderSection-III");
        return {
            init: function(){
                LE.oBorderIIICss = new OBolder($panel, 2);
            },
            run: function(options, doHide, doSlide){
                var $t = LE.oBorderIIICss.getCurrentObject();
                if($t.size() > 0){
                    LE.oBorderIIICss.reset();
                    //切换组件时，样式设置部分回顶部
                    sliderbarToTop();
                }

                LEDisplay.show($panel, doHide, doSlide);
            },
            destroy: function(){
                LE.oBorderIIICss.destroy();
            }
        };
    };

    function OBolder($PC, layer){
        this.$pc = $PC;
        this.layer = layer;
        this.$positionUl = this.$pc.find("[id^=css-bdPUl]");
        this.$colorPicker = $PC.find("[id^=css-bolderColorPick]");
        this.$styleUl = this.$pc.find("[id^=css-bolderStyleUl]");
        this.$bolderWidth = this.$pc.find("[id^=css-bBolderWidth]");
        this.$bolderColor = this.$pc.find("[id^=css-bBolderColor]");
        this.$bolderRaduis = this.$pc.find("[id^=css-bBolderRadius]");
        this.init($PC);
    }

    OBolder.prototype = {
        init: function(){
            this.initColorPicker();
            this.initBolderWidth();
            this.initBolderStyle();
            this.initBolderRadius();
            this.initPosition();
        },
        reset: function(){
            this.resetPosition();
            this.resetColorPicker();
            this.resetBolderWidth();
            this.resetBolderStyle();
            this.resetBolderRadius();
            this.resetHrWidth();
        },
        cssSetTargetBolder:function(attr, val){
            var obj = this;
            var $target = LEStyle.getObject(this.layer);
            //把对应的样式写到css文件
            var partSelector=obj.getPartSelector($target);
            var selector=obj.setCssStyle(partSelector,attr, val);
        },
        getPartSelector:function($target){
            var partSelector;
            if($target.hasClass("le-list-group")){           //列表最外层
                partSelector="";
            }else if($target.hasClass("list-group-item")){           //每一行
                partSelector=".list-group-item";
            }else if($target.parent().hasClass("media-body") && $target.children("li").hasClass("titList")){          //标题
                partSelector=".list-group-item > .media > .media-body > ul";
            }else if($target.hasClass("listSummaryBox")){                //摘要
                partSelector=".list-group-item > .media > .media-body > .listSummary > .listSummaryBox";
            }else if($target.hasClass("listSourceDetailedStyle")){                     //来源
                partSelector=".list-group-item .listSourceDetailedStyle";
            }else if($target.hasClass("listTimeDetailedStyle")){                 //时间
                partSelector=".list-group-item .listTimeDetailedStyle";
            }else if($target.hasClass("moreListShow")){                      //右侧链接
                partSelector=".moreListShow";
            }else if($target.hasClass("le-carousel")){           //轮播图
                partSelector="";
            }else if($target.hasClass("le-gallery")){           //多图
                partSelector="";
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
                obj.cssSetTargetBolder($this.attr("data-ref"), num + _unit);
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
            obj.cssSetTargetBolder($this.attr("data-ref"), num + _unit);
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

        partSelect:function($target){
            var obj=this;
            var partSelect=obj.getPartSelector($target);
            var dataSetFlag;
            if(partSelect==""){
                dataSetFlag="outer";       //列表最外层
            }else if(partSelect==".list-group-item"){
                dataSetFlag="ul";           //每一行
            }else if(partSelect==".list-group-item > .media > .media-body > ul"){
                dataSetFlag="title";          //标题
            }else if(partSelect==".list-group-item > .media > .media-body > .listSummary > .listSummaryBox"){
                dataSetFlag="summary";           //摘要
            }else if(partSelect==".list-group-item .listSourceDetailedStyle"){
                dataSetFlag="source";             //来源
            }else if(partSelect==".list-group-item .listTimeDetailedStyle"){
                dataSetFlag="time";               //时间
            }else if(partSelect==".moreListShow"){
                dataSetFlag="more";                  //右侧链接
            };
            return dataSetFlag;
        },

        initColorPicker: function(){
            var obj = this;
            //输入框颜色修改
            obj.$bolderColor.on('change',function(){
                var tinycolor=$(this).val();
                obj.$colorPicker.spectrum("set", tinycolor);
                var $target = obj.getCurrentObject();
                var dataSetFlag=obj.partSelect($target);
                LECurrentObject.attr("data-"+dataSetFlag+"-color", tinycolor);

                var _$target = LEStyle.getObject();
                var isHr = _$target.hasClass("le-hr");
                if(isHr){
                    //  $target.css("border-top-color", _c);
                    obj.cssSetTargetBolder("border-top-color", tinycolor);
                } else{
                    obj.setStyle("color", tinycolor);
                }

                //如果没有被修改过,点击之后初始化全部
                obj.modified(dataSetFlag,true);
                LEHistory.trigger();
            });

            obj.$colorPicker.spectrum(
                LEColorPicker.getOptions(function(tinycolor){
                    //如果tinycolor无效时，设置透明
                    var _c = tinycolor ? tinycolor.toHexString() : "";
                    var $target = obj.getCurrentObject();
                    var dataSetFlag=obj.partSelect($target);
                    LECurrentObject.attr("data-"+dataSetFlag+"-color", _c);
                    obj.$bolderColor.val(_c);

                    var _$target = LEStyle.getObject();
                    var isHr = _$target.hasClass("le-hr");
                    if(isHr){
                        //  $target.css("border-top-color", _c);
                        obj.cssSetTargetBolder("border-top-color", _c);
                    } else{
                        obj.setStyle("color", _c);
                    }

                    //如果没有被修改过,点击之后初始化全部
                    obj.modified(dataSetFlag,true);
                    LEHistory.trigger();
                })
            );
        },
        getCurrentObject: function(){
            var $target = LEStyle.getObject(this.layer);
            try{
                if($target.hasClass("le-hr")){
                    return $target.find("hr");
                }else if($target.hasClass("le-image")){
                    return $target.find("img");
                }

            } catch(e){
                LECurrentObject.click();
            }
            return $target;
        },
        setStyle: function(key, value){
            var obj=this;
            var $target = obj.getCurrentObject();
            var dataSetFlag=obj.partSelect($target);
            value = value || LECurrentObject.attr("data-"+dataSetFlag+"-"+ key) || $target.css("border-" + key);
            var selector = obj.$positionUl.children(".select").attr("data-ref");
            var selectors = selector.split(",");
            for(var i in selectors){
                //  $target.css(selectors[i] + "-" + key, value);
                obj.cssSetTargetBolder(selectors[i] + "-" + key, value);
            }
        },
        modified: function(dataSetFlag,resetW){
            if(!this.isSet(dataSetFlag)){
                var $target = this.getCurrentObject();
                /*$target.attr("data-set", true);*/
                //$target.attr("data-"+dataSetFlag+"-set", true);
                LECurrentObject.attr("data-"+dataSetFlag+"-set", true);
                this.resetColorPicker();
                this.resetBolderWidth(dataSetFlag,resetW);
                this.resetBolderStyle();
                this.resetBolderRadius();
                this.resetValue();
            }

        },
        isSet: function(dataSetFlag){
            /*var $target = this.getCurrentObject();
             return $target.attr("data-set") ? true : false;*/
            return LECurrentObject.attr("data-"+dataSetFlag+"-set") ? true : false;
        },
        resetColorPicker: function(){
            var obj=this;
            var $target = this.getCurrentObject();
            var dataSetFlag=obj.partSelect($target);
            //获得位置
            var _p = LECurrentObject.attr("data-"+dataSetFlag+"-position");
            _p = _p.split(",");
            _p = _p[0];
            !LECurrentObject.attr("data-"+dataSetFlag+"-color") && LECurrentObject.attr("data-"+dataSetFlag+"-color", $target.css(_p + "-color"));

            if(this.isSet(dataSetFlag)){
                this.$colorPicker.spectrum("set", LECurrentObject.attr("data-"+dataSetFlag+"-color"));
                this.$bolderColor.val(this.$colorPicker.spectrum("get"));
            }
        },
        resetBolderWidth: function(isReset){
            var obj=this;
            var $target = this.getCurrentObject();
            var dataSetFlag=obj.partSelect($target);
            var isHr = $target.parent().hasClass("le-hr");
            var $bolderWidth = this.$bolderWidth;
            if(isHr){
                $bolderWidth.val($target.css("border-top-width"));
            } else{
                var _p = LECurrentObject.attr("data-"+dataSetFlag+"-position");
                _p = _p.split(",");
                _p = _p[0];

                //border显示为拆分开的数字时，显示最大值
                var arr=$target.css(_p + "-width");
                if(arr){
                    arr=$target.css(_p + "-width").split(" ")[0];
                }
                //(!$target.attr("data-width") || isReset) && $target.attr("data-width", $target.css(_p + "-width"));
                (!LECurrentObject.attr("data-"+dataSetFlag+"-width") || isReset) && LECurrentObject.attr("data-"+dataSetFlag+"-width", arr);

                if(this.isSet(dataSetFlag)){
                    $bolderWidth.val(LECurrentObject.attr("data-"+dataSetFlag+"-width"));
                }
            }
        },
        resetBolderStyle: function(){
            var obj=this;
            var $target = this.getCurrentObject();
            var dataSetFlag=obj.partSelect($target);
            var $ul = this.$styleUl;
            //   $ul.children("li:last").addClass("select");
            if($target.hasClass("le-hr")){
                $ul.find("li[data-ref='none']").hide();
            } else{
                $ul.find("li[data-ref='none']").show();
            }
            var _p = LECurrentObject.attr("data-"+dataSetFlag+"-position");
            !LECurrentObject.attr("data-"+dataSetFlag+"-style") && LECurrentObject.attr("data-"+dataSetFlag+"-style", "dashed");
            _p = _p.split(",");
            _p = _p[0];

            if(this.isSet(dataSetFlag)){
                $ul.find("li").removeClass("select");
                //  $ul.find("li").addClass("select").siblings().removeClass("select");
                $ul.find("li[data-ref='" + $target.css(_p + "-style") + "']").addClass("select");
            }

        },
        resetBolderRadius: function(){
            var obj=this;
            var $target = this.getCurrentObject();
            var dataSetFlag=obj.partSelect($target);
            var isHr = $target.parent().hasClass("le-hr");
            if(isHr){
                this.$pc.find("[id^=css-bRadiusDiv]").hide();
            } else{
                this.$pc.find("[id^=css-bRadiusDiv]").show();

                !LECurrentObject.attr("data-"+dataSetFlag+"-radius") && LECurrentObject.attr("data-"+dataSetFlag+"-radius", "0px");

                if(this.isSet(dataSetFlag)){
                    //  this.$bolderRaduis.val($target.css("border-radius"));
                    this.$bolderRaduis.val(LECurrentObject.attr("data-"+dataSetFlag+"-radius"));
                }
            }

        },

        resetValue: function(){
            var obj=this;
            var $target = this.getCurrentObject();
            var dataSetFlag=obj.partSelect($target);
            var _p = LECurrentObject.attr("data-"+dataSetFlag+"-position");
            _p = _p.split(",");
            for(var i in _p){
                /*   $target.css((_p[i] + "-color"), $target.attr("data-color"));
                 $target.css((_p[i] + "-width"), $target.attr("data-width"));
                 $target.css((_p[i] + "-style"), $target.attr("data-style"));
                 $target.css((_p[i] + "-radius"), $target.attr("data-radius"));  */
                obj.cssSetTargetBolder((_p[i] + "-color"), LECurrentObject.attr("data-"+dataSetFlag+"-color"));
                obj.cssSetTargetBolder((_p[i] + "-width"), LECurrentObject.attr("data-"+dataSetFlag+"-width"));
                obj.cssSetTargetBolder((_p[i] + "-style"), LECurrentObject.attr("data-"+dataSetFlag+"-style"));
                obj.cssSetTargetBolder((_p[i] + "-radius"), LECurrentObject.attr("data-"+dataSetFlag+"-radius"));
            }
        },
        initBolderWidth: function(){
            var $bolderWidth = this.$bolderWidth;
            var obj = this;
            $bolderWidth.onlyNum().keydown(function(e){
                var $target = obj.getCurrentObject();
                var dataSetFlag=obj.partSelect($target);
                //   LEKey.numInputKeyDown(e, $(this), $target);
                obj.cssNumInputKeyDown(e, $(this), $target);
                LECurrentObject.attr("data-"+dataSetFlag+"-width", $(this).val());
                //根据边框样式调整轮播图图片高度
                if($target.hasClass("le-carousel")){
                    LE.stylesheets["PicManage"]().changeItemHeight();
                    //obj.changeCarouselHeight($target);
                } else if($target.hasClass("le-gallery")){
                    LE.stylesheets["Gallery"]().changeImageHeight();
                }else if($target.hasClass("le-lineChart")){
                    var _id=$target.attr("id");
                    var myChart = echarts.init(document.getElementById(_id));
                    var lineOption=lineChart_Map.get(_id);
                    myChart.setOption(lineOption)
                }else if($target.hasClass("column")) {
                    $target.find(".le-lineChart").each(function(){
                        var _id = $(this).attr("id");
                        var myChart = echarts.init(document.getElementById(_id));
                        var lineOption = lineChart_Map.get(_id);
                        myChart.setOption(lineOption)
                    });
                }
                obj.resetBolderStyle();
            });
            //失去焦点的时候，置0
            $bolderWidth.blur(function(e){
                var $target = obj.getCurrentObject();
                var dataSetFlag=obj.partSelect($target);
                //   LEKey.numInputBlur(e, $(this), $target);
                obj.cssNumInputBlur(e, $(this), $target);
                LECurrentObject.attr("data-"+dataSetFlag+"-width", $(this).val());
                //根据边框样式调整轮播图图片高度
                if($target.hasClass("le-carousel")){
                    LE.stylesheets["PicManage"]().changeItemHeight();
                    //obj.changeCarouselHeight($target);
                } else if($target.hasClass("le-gallery")){
                    LE.stylesheets["Gallery"]().changeImageHeight();
                }else if($target.hasClass("le-lineChart")){
                    var _id=$target.attr("id");
                    var myChart = echarts.init(document.getElementById(_id));
                    var lineOption=lineChart_Map.get(_id);
                    myChart.setOption(lineOption)
                }else if($target.hasClass("column")) {
                    $target.find(".le-lineChart").each(function(){
                        var _id = $(this).attr("id");
                        var myChart = echarts.init(document.getElementById(_id));
                        var lineOption = lineChart_Map.get(_id);
                        myChart.setOption(lineOption)
                    });
                }
                obj.modified(dataSetFlag);
                obj.resetBolderStyle();
                LEHistory.trigger();
            });

            $bolderWidth.focus(function(e){
                var $target = obj.getCurrentObject();
                var dataSetFlag=obj.partSelect($target);
                //   LEKey.numInputFocus(e, $(this), $target);
                obj.cssNumInputFocus(e, $(this), $target);
                LECurrentObject.attr("data-"+dataSetFlag+"-width", $(this).val());
                //根据边框样式调整轮播图图片高度
                if($target.hasClass("le-carousel")){
                    LE.stylesheets["PicManage"]().changeItemHeight();
                    //obj.changeCarouselHeight($target);
                } else if($target.hasClass("le-gallery")){
                    LE.stylesheets["Gallery"]().changeImageHeight();
                }else if($target.hasClass("le-lineChart")){
                    var _id=$target.attr("id");
                    var myChart = echarts.init(document.getElementById(_id));
                    var lineOption=lineChart_Map.get(_id);
                    myChart.setOption(lineOption)
                }else if($target.hasClass("column")) {
                    $target.find(".le-lineChart").each(function(){
                        var _id = $(this).attr("id");
                        var myChart = echarts.init(document.getElementById(_id));
                        var lineOption = lineChart_Map.get(_id);
                        myChart.setOption(lineOption)
                    });
                }
            });
        },
        initBolderStyle: function(){
            var obj = this;
            this.$styleUl.find("li").click(function(){
                var _ref = $(this).data("ref");
                var $target = obj.getCurrentObject();
                var dataSetFlag=obj.partSelect($target);
                var _p = LECurrentObject.attr("data-"+dataSetFlag+"-position");
                _p = _p.split(",");
                obj.cleanBorderStyle();
                for(var i in _p){
                    //  $target.css(_p[i] + "-style", _ref);
                    obj.cssSetTargetBolder(_p[i] + "-style", _ref);
                }
                LECurrentObject.attr("data-"+dataSetFlag+"-style", _ref);
                //根据边框样式调整轮播图图片高度
                if($target.hasClass("le-carousel")){
                    LE.stylesheets["PicManage"]().changeItemHeight();
                    //obj.changeCarouselHeight($target);
                } else if($target.hasClass("le-gallery")){
                    LE.stylesheets["Gallery"]().changeImageHeight();
                }else if($target.hasClass("le-lineChart")){
                    var _id=$target.attr("id");
                    var myChart = echarts.init(document.getElementById(_id));
                    var lineOption=lineChart_Map.get(_id);
                    myChart.setOption(lineOption);
                }else if($target.hasClass("column")) {
                    $target.find(".le-lineChart").each(function(){
                        var _id = $(this).attr("id");
                        var myChart = echarts.init(document.getElementById(_id));
                        var lineOption = lineChart_Map.get(_id);
                        myChart.setOption(lineOption)
                    });
                }
                obj.modified(dataSetFlag,true);
                LEHistory.trigger();
            });
        },
        cleanBorderStyle: function(){
            var obj = this;
            var $target = this.getCurrentObject();
            /*   $target.css("border-style", "");
             $target.css("border-top-style", "");
             $target.css("border-bottom-style", "");
             $target.css("border-left-style", "");
             $target.css("border-right-style", "");  */
            obj.cssSetTargetBolder("border-style", "");
            obj.cssSetTargetBolder("border-top-style", "");
            obj.cssSetTargetBolder("border-bottom-style", "");
            obj.cssSetTargetBolder("border-left-style", "");
            obj.cssSetTargetBolder("border-right-style", "");
        },
        initBolderRadius: function(){
            var $bolderRadius = this.$bolderRaduis;
            var obj = this;
            $bolderRadius.onlyNum().keydown(function(e){
                var $target = obj.getCurrentObject();
                var dataSetFlag=obj.partSelect($target);
                //    LEKey.numInputKeyDown(e, $(this), $target);
                obj.cssNumInputKeyDown(e, $(this), $target);
                LECurrentObject.attr("data-"+dataSetFlag+"-radius", $(this).val());
            });
            //失去焦点的时候，置0
            $bolderRadius.blur(function(e){
                var $target = obj.getCurrentObject();
                var dataSetFlag=obj.partSelect($target);
                //   LEKey.numInputBlur(e, $(this), $target);
                obj.cssNumInputBlur(e, $(this), $target);
                LECurrentObject.attr("data-"+dataSetFlag+"-radius", $(this).val());
                obj.modified(dataSetFlag);
                LEHistory.trigger();
            });

            $bolderRadius.focus(function(e){
                var $target = obj.getCurrentObject();
                var dataSetFlag=obj.partSelect($target);
                //  LEKey.numInputFocus(e, $(this), $target);
                obj.cssNumInputFocus(e, $(this), $target);
                LECurrentObject.attr("data-"+dataSetFlag+"-radius", $(this).val());
            });
        },
        initPosition: function(){
            var obj = this;
            this.$positionUl.find("li").click(function(){
                var $target = obj.getCurrentObject();
                var dataSetFlag=obj.partSelect($target);
                LECurrentObject.attr("data-"+dataSetFlag+"-position", $(this).attr("data-ref"));
                obj.cleanStyle();
                obj.setStyle("color");
                obj.setStyle("width");
                obj.setStyle("style");
                obj.modified(dataSetFlag);
                //根据边框样式调整轮播图图片高度
                if($target.hasClass("le-carousel")){
                    LE.stylesheets["PicManage"]().changeItemHeight();
                    //obj.changeCarouselHeight($target);
                } else if($target.hasClass("le-gallery")){
                    LE.stylesheets["Gallery"]().changeImageHeight();
                }else if($target.hasClass("le-lineChart")){
                    var _id=$target.attr("id");
                    var myChart = echarts.init(document.getElementById(_id));
                    var lineOption=lineChart_Map.get(_id);
                    myChart.setOption(lineOption)
                }else if($target.hasClass("column")) {
                    $target.find(".le-lineChart").each(function(){
                        var _id = $(this).attr("id");
                        var myChart = echarts.init(document.getElementById(_id));
                        var lineOption = lineChart_Map.get(_id);
                        myChart.setOption(lineOption)
                    });
                }
                LEHistory.trigger();
            });

        },
        changeCarouselHeight:function($target){
            var obj = this;
            var bdTopWidth=parseInt($target.css("border-top-width"));
            var bdBtmWidth=parseInt($target.css("border-bottom-width"));
            var width=parseInt($target.css("height"));
            var imgHeight=parseInt(width-bdTopWidth-bdBtmWidth);
            obj.setCssStyle("div.carousel-inner .item img","height", imgHeight+"px");
            //LECurrentObject.find("div.carousel-inner .item img").css("height", imgHeight);
        },
        cleanStyle: function(){
            var obj = this;
            var $target = this.getCurrentObject();
            $target.removeClass("column_style");
            /*   $target.css("border-color", "");
             $target.css("border-top-color", "");
             $target.css("border-bottom-color", "");
             $target.css("border-left-color", "");
             $target.css("border-right-color", ""); */
            obj.cssSetTargetBolder("border-color", "");
            obj.cssSetTargetBolder("border-top-color", "");
            obj.cssSetTargetBolder("border-bottom-color", "");
            obj.cssSetTargetBolder("border-left-color", "");
            obj.cssSetTargetBolder("border-right-color", "");

            this.cleanBorderStyle();

            /*    $target.css("border-width", "");
             $target.css("border-top-width", "");
             $target.css("border-bottom-width", "");
             $target.css("border-left-width", "");
             $target.css("border-right-width", "");  */
            obj.cssSetTargetBolder("border-width", "");
            obj.cssSetTargetBolder("border-top-width", "");
            obj.cssSetTargetBolder("border-bottom-width", "");
            obj.cssSetTargetBolder("border-left-width", "");
            obj.cssSetTargetBolder("border-right-width", "");
        },
        resetPosition: function(){
            var obj=this;
            var $target = this.getCurrentObject();
            var dataSetFlag=obj.partSelect($target);
            var isHr = $target.parent().hasClass("le-hr");
            if(isHr){
                LECurrentObject.attr("data-"+dataSetFlag+"-position", "border-top");
                this.$pc.find("[id^=css-bPositionDiv]").hide();
            } else{
                var _position = LECurrentObject.attr("data-"+dataSetFlag+"-position");
                this.$positionUl.children("li").removeClass("select");
                if(_position){
                    this.$positionUl.children("li[data-ref='" + _position + "']").addClass("select");
                } else{
                    this.$positionUl.children("li:last").addClass("select");
                    LECurrentObject.attr("data-"+dataSetFlag+"-position", this.$positionUl.children("li:last").attr("data-ref"));
                }
                this.$pc.find("[id^=css-bPositionDiv]").show();
            }
        },
        resetHrWidth: function(){
            var $target = this.getCurrentObject();
            var isHr = $target.parent().hasClass("le-hr");
            if(isHr){
                this.$pc.find("[id^=css-bWidth]").val($target.css("width"));
                this.$pc.find("[id^=css-bWidthDiv]").show();
            } else{
                this.$pc.find("[id^=css-bWidthDiv]").hide();
            }

        },
        destroy: function(){
            this.$styleUl.find("li").removeClass("select");
            this.$bolderColor.val("");
            this.$bolderWidth.val("");
            this.$bolderRaduis.val("");
            this.$colorPicker.spectrum("set", "");
            this.$pc.hide();
        }

    };

})(window, jQuery, LE, undefined);