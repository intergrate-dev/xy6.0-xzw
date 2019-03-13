/**
 * Created by isaac_gu on 2016/1/14.
 */
(function(window, $, LE){
    LE.stylesheets["BolderSetting"] = function(){
        var $panel = $("#bolderSection");

        /*var initBWidth = function(){
         $bWidth.onlyNum().keydown(function(e){
         var $target = LECurrentObject.find("hr");
         LEKey.numInputKeyDown(e, $(this), $target);
         });
         //失去焦点的时候，置0
         $bWidth.blur(function(e){
         var $target = LECurrentObject.find("hr");
         LEKey.numInputBlur(e, $(this), $target);
         modified();
         LEHistory.trigger();
         });

         $bWidth.focus(function(e){
         var $target = LECurrentObject.find("hr");
         LEKey.numInputFocus(e, $(this), $target);
         });
         };*/


        return {
            init: function(){
                LE.oBorder = new OBolder($panel, 0);
            },
            run: function(options, doHide, doSlide){
                var $t = LE.oBorder.getCurrentObject();
                if($t.size() > 0){
                    LE.oBorder.reset();
                    //切换组件时，样式设置部分回顶部
                    sliderbarToTop();
                }

                LEDisplay.show($panel, doHide, doSlide);
            },
            destroy: function(){
                LE.oBorder.destroy();
            }
        };
    };

    LE.stylesheets["BolderSettingII"] = function(){
        var $panel = $("#bolderSection-II");
        return {
            init: function(){
                LE.oBorderII = new OBolder($panel, 1);
            },
            run: function(options, doHide, doSlide){
                var $t = LE.oBorderII.getCurrentObject();
                if($t.size() > 0){
                    LE.oBorderII.reset();
                    //切换组件时，样式设置部分回顶部
                    sliderbarToTop();
                }

                LEDisplay.show($panel, doHide, doSlide);
            },
            destroy: function(){
                LE.oBorderII.destroy();
            }
        };
    };

    LE.stylesheets["BolderSettingIII"] = function(){
        var $panel = $("#bolderSection-III");
        return {
            init: function(){
                LE.oBorderIII = new OBolder($panel, 2);
            },
            run: function(options, doHide, doSlide){
                var $t = LE.oBorderIII.getCurrentObject();
                if($t.size() > 0){
                    LE.oBorderIII.reset();
                    //切换组件时，样式设置部分回顶部
                    sliderbarToTop();
                }

                LEDisplay.show($panel, doHide, doSlide);
            },
            destroy: function(){
                LE.oBorderIII.destroy();
            }
        };
    };

    function OBolder($PC, layer){
        this.$pc = $PC;
        this.layer = layer;
        this.$positionUl = this.$pc.find("[id^=bdPUl]");
        this.$colorPicker = $PC.find("[id^=bolderColorPick]");
        this.$styleUl = this.$pc.find("[id^=bolderStyleUl]");
        this.$bolderWidth = this.$pc.find("[id^=bBolderWidth]");
        this.$bolderColor = this.$pc.find("[id^=bBolderColor]");
        this.$bolderRaduis = this.$pc.find("[id^=bBolderRadius]");
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
        initColorPicker: function(){
            var obj = this;
            obj.$colorPicker.spectrum(
                LEColorPicker.getOptions(function(tinycolor){
                    //如果tinycolor无效时，设置透明
                    var _c = tinycolor ? tinycolor.toHexString() : "";
                    var $target = obj.getCurrentObject();
                    $target.attr("data-color", _c);
                    obj.$bolderColor.val(_c);

                    var _$target = LEStyle.getObject();
                    var isHr = _$target.hasClass("le-hr");
                    if(isHr){
                        $target.css("border-top-color", _c);
                    } else{
                        obj.setStyle("color", _c);
                    }

                    //如果没有被修改过,点击之后初始化全部
                    obj.modified();
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
            var $target = this.getCurrentObject();
            value = value || $target.attr("data-" + key) || $target.css("border-" + key);
            var selector = this.$positionUl.children(".select").attr("data-ref");
            var selectors = selector.split(",");
            for(var i in selectors){
                $target.css(selectors[i] + "-" + key, value);
            }
        },
        modified: function(resetW){
            if(!this.isSet()){
                var $target = this.getCurrentObject();
                $target.attr("data-set", true);
                this.resetColorPicker();
                this.resetBolderWidth(resetW);
                this.resetBolderStyle();
                this.resetBolderRadius();
                this.resetValue();
            }

        },
        isSet: function(){
            var $target = this.getCurrentObject();
            return $target.attr("data-set") ? true : false;
        },
        resetColorPicker: function(){
            var $target = this.getCurrentObject();
            //获得位置
            var _p = $target.attr("data-position");
            _p = _p.split(",");
            _p = _p[0];
            !$target.attr("data-color") && $target.attr("data-color", $target.css(_p + "-color"));

            if(this.isSet()){
                this.$colorPicker.spectrum("set", $target.attr("data-color"));
                this.$bolderColor.val(this.$colorPicker.spectrum("get"));
            }
        },
        resetBolderWidth: function(isReset){
            var $target = this.getCurrentObject();
            var isHr = $target.parent().hasClass("le-hr");
            var $bolderWidth = this.$bolderWidth;
            if(isHr){
                $bolderWidth.val($target.css("border-top-width"));
            } else{
                var _p = $target.attr("data-position");
                _p = _p.split(",");
                _p = _p[0];

                //border显示为拆分开的数字时，显示最大值
                var arr=$target.css(_p + "-width");
                if(arr){
                    arr=$target.css(_p + "-width").split(" ")[0];
                }
                //(!$target.attr("data-width") || isReset) && $target.attr("data-width", $target.css(_p + "-width"));
                (!$target.attr("data-width") || isReset) && $target.attr("data-width", arr);


                if(this.isSet()){
                    $bolderWidth.val($target.attr("data-width"));
                }
            }
        },
        resetBolderStyle: function(){
            var $target = this.getCurrentObject();
            var $ul = this.$styleUl;
            if($target.hasClass("le-hr")){
                $ul.find("li[data-ref='none']").hide();
            } else{
                $ul.find("li[data-ref='none']").show();
            }
            var _p = $target.attr("data-position");
            !$target.attr("data-style") && $target.attr("data-style", "dashed");
            _p = _p.split(",");
            _p = _p[0];

            if(this.isSet()){
                $ul.find("li[data-ref='" + $target.css(_p + "-style") + "']").addClass("select");
            }

        },
        resetBolderRadius: function(){
            var $target = this.getCurrentObject();
            var isHr = $target.parent().hasClass("le-hr");
            if(isHr){
                this.$pc.find("[id^=bRadiusDiv]").hide();
            } else{
                this.$pc.find("[id^=bRadiusDiv]").show();

                !$target.attr("data-radius") && $target.attr("data-radius", "0px");

                if(this.isSet()){
                    this.$bolderRaduis.val($target.css("border-radius"));
                }
            }

        },

        resetValue: function(){
            var $target = this.getCurrentObject();
            var _p = $target.attr("data-position");
            _p = _p.split(",");
            for(var i in _p){
                $target.css((_p[i] + "-color"), $target.attr("data-color"));
                $target.css((_p[i] + "-width"), $target.attr("data-width"));
                $target.css((_p[i] + "-style"), $target.attr("data-style"));
                $target.css((_p[i] + "-radius"), $target.attr("data-radius"));
            }
        },
        initBolderWidth: function(){
            var $bolderWidth = this.$bolderWidth;
            var obj = this;
            $bolderWidth.onlyNum().keydown(function(e){
                var $target = obj.getCurrentObject();
                LEKey.numInputKeyDown(e, $(this), $target);
                $target.attr("data-width", $(this).val());
                //根据边框样式调整轮播图图片高度
                if($target.hasClass("le-carousel")){
                    LE.stylesheets["PicManage"]().changeItemHeight();
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
            //失去焦点的时候，置0
            $bolderWidth.blur(function(e){
                var $target = obj.getCurrentObject();
                LEKey.numInputBlur(e, $(this), $target);
                $target.attr("data-width", $(this).val());
                //根据边框样式调整轮播图图片高度
                if($target.hasClass("le-carousel")){
                    LE.stylesheets["PicManage"]().changeItemHeight();
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
                obj.modified();
                LEHistory.trigger();
            });

            $bolderWidth.focus(function(e){
                var $target = obj.getCurrentObject();
                LEKey.numInputFocus(e, $(this), $target);
                $target.attr("data-width", $(this).val());
                //根据边框样式调整轮播图图片高度
                if($target.hasClass("le-carousel")){
                    LE.stylesheets["PicManage"]().changeItemHeight();
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
                var _p = $target.attr("data-position");
                _p = _p.split(",");
                obj.cleanBorderStyle();
                for(var i in _p){
                    $target.css(_p[i] + "-style", _ref);
                }
                $target.attr("data-style", _ref);
                //根据边框样式调整轮播图图片高度
                if($target.hasClass("le-carousel")){
                    LE.stylesheets["PicManage"]().changeItemHeight();
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
                obj.modified(true);
                LEHistory.trigger();
            });
        },
        cleanBorderStyle: function(){
            var $target = this.getCurrentObject();
            $target.css("border-style", "");
            $target.css("border-top-style", "");
            $target.css("border-bottom-style", "");
            $target.css("border-left-style", "");
            $target.css("border-right-style", "");
        },
        initBolderRadius: function(){
            var $bolderRadius = this.$bolderRaduis;
            var obj = this;
            $bolderRadius.onlyNum().keydown(function(e){
                var $target = obj.getCurrentObject();
                LEKey.numInputKeyDown(e, $(this), $target);
            });
            //失去焦点的时候，置0
            $bolderRadius.blur(function(e){
                var $target = obj.getCurrentObject();
                LEKey.numInputBlur(e, $(this), $target);
                obj.modified();
                LEHistory.trigger();
            });

            $bolderRadius.focus(function(e){
                var $target = obj.getCurrentObject();
                LEKey.numInputFocus(e, $(this), $target);
            });
        },
        initPosition: function(){
            var obj = this;
            this.$positionUl.find("li").click(function(){
                var $target = obj.getCurrentObject();
                $target.attr("data-position", $(this).attr("data-ref"));
                obj.cleanStyle();
                obj.setStyle("color");
                obj.setStyle("width");
                obj.setStyle("style");
                obj.modified();
                //根据边框样式调整轮播图图片高度
                if($target.hasClass("le-carousel")){
                    LE.stylesheets["PicManage"]().changeItemHeight();
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
        cleanStyle: function(){
            var $target = this.getCurrentObject();
            $target.removeClass("column_style");
            $target.css("border-color", "");
            $target.css("border-top-color", "");
            $target.css("border-bottom-color", "");
            $target.css("border-left-color", "");
            $target.css("border-right-color", "");

            this.cleanBorderStyle();

            $target.css("border-width", "");
            $target.css("border-top-width", "");
            $target.css("border-bottom-width", "");
            $target.css("border-left-width", "");
            $target.css("border-right-width", "");
        },
        resetPosition: function(){
            var $target = this.getCurrentObject();
            var isHr = $target.parent().hasClass("le-hr");
            if(isHr){
                $target.attr("data-position", "border-top");
                this.$pc.find("[id^=bPositionDiv]").hide();
            } else{
                var _position = $target.attr("data-position");
                this.$positionUl.children("li").removeClass("select");
                if(_position){
                    this.$positionUl.children("li[data-ref='" + _position + "']").addClass("select");
                } else{
                    this.$positionUl.children("li:last").addClass("select");
                    $target.attr("data-position", this.$positionUl.children("li:last").attr("data-ref"));
                }
                this.$pc.find("[id^=bPositionDiv]").show();
            }
        },
        resetHrWidth: function(){
            var $target = this.getCurrentObject();
            var isHr = $target.parent().hasClass("le-hr");
            if(isHr){
                this.$pc.find("[id^=bWidth]").val($target.css("width"));
                this.$pc.find("[id^=bWidthDiv]").show();
            } else{
                this.$pc.find("[id^=bWidthDiv]").hide();
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


    }
    ;


})(window, jQuery, LE, undefined);