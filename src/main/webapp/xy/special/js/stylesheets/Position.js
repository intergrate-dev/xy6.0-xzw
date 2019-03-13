/**
 * Created by isaac_gu on 2016/1/14.
 * position-type 从entity中的 position-type中取，如果没有，就从option中取
 */
(function(window, $, LE){
    LE.stylesheets["Position"] = function(){
        var $PC = $("#positionSection");
        return {
            init: function(){
                LE.Position = new OPosition($PC, 0);
            },
            run: function(options, doHide, doSlide){
                LE.Position.resetLabels();
                LEDisplay.show($PC, doHide, doSlide);
            },
            destroy: function(){
                $PC.hide();
            }
        };
    };

    LE.stylesheets["PositionII"] = function(){
        var $PC = $("#positionSection-II");
        return {
            init: function(){
                LE.PositionII = new OPosition($PC, 1);
            },
            run: function(options, doHide, doSlide){
                LE.PositionII.resetLabels();
                LEDisplay.show($PC, doHide, doSlide);
            },
            destroy: function(){
                $PC.hide();
            }
        };
    };


    LE.stylesheets["PositionIII"] = function(){
        var $PC = $("#positionSection-III");
        return {
            init: function(){
                LE.PositionIII = new OPosition($PC, 2);
            },
            run: function(options, doHide, doSlide){
                LE.PositionIII.resetLabels();
                LEDisplay.show($PC, doHide, doSlide);
            },
            destroy: function(){
                $PC.hide();
            }
        };
    };


    function OPosition($PC, layer){
        this.$pc = $PC;
        this.layer = layer;
        this.init($PC);
    }

    OPosition.prototype = {
        init: function($PC){
            this.initSpanEvent($PC.find(".positionData"));
            this.initHandlerEvent($PC.find(".pHandler"), $PC.find(".positionData"));
            this.initWHEvent($PC.find(".positionwh"));
            this.initConMiddle($PC.find(".positionData"));
        },
        initSpanEvent: function($po){
            var obj = this;
            $po.onlyNum().keydown(function(e){
                if(e.keyCode == 38 || e.keyCode == 40 || e.keyCode == 13){
                    var $this = $(this);
                    var num = parseInt($this.text());
                    //点击上下按钮，来改变当前的值
                    e.keyCode == 38 && ++num;
                    e.keyCode == 40 && --num;

                    //设定目标的css样式
                    var s = obj.setTargetPosition($this.data("direct"), num);
                    $this.text(parseInt(s));
                    var $target = LEStyle.getObject(obj.layer);
                    if($target.hasClass("le-lineChart")){
                        var _id=$target.attr("id");
                        var myChart = echarts.init(document.getElementById(_id));
                        var lineOption=lineChart_Map.get(_id);
                        myChart.setOption(lineOption)
                    } else if($target.hasClass("column")) {
                        $target.find(".le-lineChart").each(function(){
                            var _id = $(this).attr("id");
                            var myChart = echarts.init(document.getElementById(_id));
                            var lineOption = lineChart_Map.get(_id);
                            myChart.setOption(lineOption)
                        });
                    }
                }
            });

            //失去焦点的时候，置0
            $po.blur(function(){
                var $this = $(this);
                $.trim($this.text()) == "" && $this.text(0);
                //处理输入框里面是以0开头的问题
                var num = parseInt($this.text());
                var s = obj.setTargetPosition($this.data("direct"), num);
                $this.text(parseInt(s));
                var $target = LEStyle.getObject(obj.layer);
                if($target.hasClass("le-lineChart")){
                    var _id=$target.attr("id");
                    var myChart = echarts.init(document.getElementById(_id));
                    var lineOption=lineChart_Map.get(_id);
                    myChart.setOption(lineOption)
                } else if($target.hasClass("column")) {
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
        setTargetPosition: function(attr, dis){
            var $target = LEStyle.getObject(this.layer);
            $target.css(attr, dis + "px");
            return $target.css(attr);
        },
        initHandlerEvent: function($poHandler, $po){
            var obj = this;
            $poHandler.mousedown(function(event){
                var _$document = $(document);
                var _type = $(this).data("ref");
                var isX = _type == "x";
                //获得y轴的
                var _p = isX ? event.pageX : event.pageY;
                //获得需要修改的属性
                var _attr = $(this).data("direct");
                //获得相应的span
                var _$po = $po.filter("[data-direct=" + _attr + "]");
                //获得初始值
                var _pd = isNaN(parseInt(_$po.text())) ? 0 : parseInt(_$po.text());

                //把鼠标的样式设为resize
                $(document.body).css("cursor", _type == "x" ? "col-resize" : "row-resize");

                _$document.mousemove(function(e){
                    var _mp = isX ? e.pageX : e.pageY;
                    var _dis = isX && _attr != "margin-left" && _attr != "padding-left" ? _pd - _mp + _p : _pd + _mp - _p;
                    _dis = obj.setTargetPosition(_attr, _dis);
                    _$po.text(parseInt(_dis));
                    return false;
                });

                /**
                 * 清空绑定事件
                 */
                _$document.one("mouseup", function(){
                    LEHandler.handler_enableMouseisOver = true;
                    $(document.body).css("cursor", "default");
                    _$document.unbind("mousemove");
                    var $target = LEStyle.getObject(obj.layer);
                    if($target.hasClass("le-lineChart")){
                        var _id=$target.attr("id");
                        var myChart = echarts.init(document.getElementById(_id));
                        var lineOption=lineChart_Map.get(_id);
                        myChart.setOption(lineOption)
                    } else if($target.hasClass("column")) {
                        $target.find(".le-lineChart").each(function(){
                            var _id = $(this).attr("id");
                            var myChart = echarts.init(document.getElementById(_id));
                            var lineOption = lineChart_Map.get(_id);
                            myChart.setOption(lineOption)
                        });
                    }
                    LEHistory.trigger();
                });
            });
        },
        initWHEvent: function($wh){
            var obj = this;
            $wh.keydown(function(e){
                //当设置高度时，去掉min-height
                obj.changeRefForWH($(this));
                var $target = LEStyle.getObject(obj.layer);
                if($target.hasClass("le-gallery")){
                    //$(this).data("direct") == "height" && $target.css("min-height", "0px");
                    LEKey.numInputKeyDown(e, $(this), $target);
                    LE.stylesheets["Gallery"]().changeImageHeight();
                } else if($target.hasClass("le-carousel")){
                    LEKey.numInputKeyDown(e, $(this), $target);
                    LE.stylesheets["PicManage"]().changeItemHeight();
                } else if($target.hasClass("le-image")){
                    LEKey.numInputKeyDown(e, $(this), $target);
                    resetImageEntiryWH($(this))
                } else if($target.hasClass("le-lineChart")){
                    LEKey.numInputKeyDown(e, $(this), $target);
                    var _id=$target.attr("id");
                    var myChart = echarts.init(document.getElementById(_id));
                    var lineOption=lineChart_Map.get(_id);
                    myChart.setOption(lineOption)
                } else if($target.hasClass("column")) {
                    LEKey.numInputKeyDown(e, $(this), $target);
                    $target.find(".le-lineChart").each(function(){
                        var _id = $(this).attr("id");
                        var myChart = echarts.init(document.getElementById(_id));
                        var lineOption = lineChart_Map.get(_id);
                        myChart.setOption(lineOption)
                   });
                }else{
                    LEKey.numInputKeyDown(e, $(this), $target);
                }
                if(e.keyCode == 38 || e.keyCode == 40 || e.keyCode == 13){
                    obj.reviewWH($(this));
                    obj.resetWWColor($target, $(this));
                }
            });

            //失去焦点的时候，置0
            $wh.blur(function(e){
                // 防止多次重复操作
                var item = document.activeElement.id;
                var _thisId = e.currentTarget.id;
                if(item && item===_thisId){
                    return;
                }
                obj.changeRefForWH($(this));
                var $target = LEStyle.getObject(obj.layer);
                var _v = $(this).val();
                if(!_v || $.trim(_v) === ""){
                    $target.css($(this).data("ref"), "");
                    return;
                }

                if($target.hasClass("le-gallery")){
                    LEKey.numInputBlur(e, $(this), $target);
                    LE.stylesheets["Gallery"]().changeImageHeight();
                } else if($target.hasClass("le-carousel")){
                    LEKey.numInputBlur(e, $(this), $target);
                    LE.stylesheets["PicManage"]().changeItemHeight();
                } else if($target.hasClass("le-image")){
                    LEKey.numInputBlur(e, $(this), $target);
                    //resetImageEntiryWH($(this));
                    //LEReset.resizeImage($target.find(".le-image_img"));
                } else if($target.hasClass("le-lineChart")){
                    LEKey.numInputBlur(e, $(this), $target);
                    var _id=$target.attr("id");
                    var myChart = echarts.init(document.getElementById(_id));
                    var lineOption=lineChart_Map.get(_id);
                    myChart.setOption(lineOption)
                }else if($target.hasClass("column")) {
                    LEKey.numInputBlur(e, $(this), $target);
                    $target.find(".le-lineChart").each(function(){
                        var _id = $(this).attr("id");
                        var myChart = echarts.init(document.getElementById(_id));
                        var lineOption = lineChart_Map.get(_id);
                        myChart.setOption(lineOption)
                    });
                } else{
                    $target.attr("data-positionset", true);
                    LEKey.numInputBlur(e, $(this), $target);
                }
                //LEReset.changeContainerHeight();
                LEReset.changeColumnBoxHeight();

                obj.reviewWH($(this));
                obj.resetWWColor($target, $(this));
                obj.resetWH($target, $(this));
                LEHistory.trigger();

                $('#mainDiv').perfectScrollbar('update');
            });

            $wh.focus(function(e){
                var $target = LEStyle.getObject(obj.layer);
                if($target.hasClass("le-gallery")){
                    LEKey.numInputFocus(e, $(this), $target);
                    LE.stylesheets["Gallery"]().changeImageHeight();
                } else if($target.hasClass("le-carousel")){
                    LEKey.numInputFocus(e, $(this), $target);
                    LE.stylesheets["PicManage"]().changeItemHeight();
                    $("#position_height").val($target.css("height"));
                }else if($target.hasClass("le-lineChart")){
                    LEKey.numInputFocus(e, $(this), $target);
                    var _id=$target.attr("id");
                    var myChart = echarts.init(document.getElementById(_id));
                    var lineOption=lineChart_Map.get(_id);
                    myChart.setOption(lineOption)
                }else if($target.hasClass("column")) {
                    LEKey.numInputFocus(e, $(this), $target);
                    $target.find(".le-lineChart").each(function(){
                        var _id = $(this).attr("id");
                        var myChart = echarts.init(document.getElementById(_id));
                        var lineOption = lineChart_Map.get(_id);
                        myChart.setOption(lineOption)
                    });
                } else{
                    LEKey.numInputFocus(e, $(this), $target);
                }
            });

            function resetImageEntiryWH(_$this){
                var $target = LEStyle.getObject(this.layer);
                var _radio = parseFloat($target.find(".le-image_img").attr("data-radio"));
                var keepRatio = $target.find(".le-image_img").attr("data-unlocked");
                keepRatio = keepRatio + "" != "true";
                if(_$this.data("direct") == "height"){
                    //容器的高度

                    var _ch = parseInt($target.height());

                    var _dw = $target.find(".le-image_img").outerWidth(true);
                    var _dh = $target.find(".le-image_img").outerHeight(true);
                    //如果，组件的高大于容器的高,按照高设置
                    if(_dh > _ch){
                        _dh = _ch;
                        var option = {
                            height: _dh
                        };

                        if(keepRatio){
                            _dw = (_dh * _radio).toFixed(4);
                            option.width = _dw;
                        }

                        $target.find(".ui-wrapper").css(option);
                        $target.find(".le-image_img").css(option);
                    }

                } else{
                    var _lw = parseInt($target.width());
                    var _iw = $target.find(".le-image_img").outerWidth(true);
                    if(_lw < _iw){
                        //获得容器的宽
                        var _cw = parseInt($target.width());
                        var _dw = _cw;
                        var option = {
                            width: _dw
                        };
                        if(keepRatio){
                            var _dh = (_dw / _radio).toFixed(4);
                            option.height = _dh;
                        }
                        $target.find(".ui-wrapper").css(option);
                        $target.find(".le-image_img").css(option);
                    }
                }
            }
        },

        changeRefForWH: function(_$this){
            /**
             * 如果是容器的话，height设为 min-height
             */
            if(_$this.data("direct") == "height"){
                this.changeHeight(_$this);
            } else if(_$this.data("direct") == "width"){
                this.changeWidth(_$this);
            }
        },
        changeWidth: function(_$this){
            var $target = LEStyle.getObject(this.layer);
            //容器设 min-width，组件设 width
            if(!$target.hasClass("column") && !$target.hasClass("le-columnbox")){
                _$this.attr("data-ref", "width");
            } else{
                _$this.attr("data-ref", "width");
            }
            return true;
        },
        /**
         * 如果是容器就设置 min-height，其他的组件设置正常高度 - 先改成min-height
         * @param _$this
         * @returns {boolean}
         */
        changeHeight: function(_$this){
            var $target = LEStyle.getObject(this.layer);
            if($target.hasClass("le-gallery") || $target.hasClass("le-carousel")/* || $target.hasClass("le-image")*/|| $target.hasClass("le-lineChart")){
                _$this.attr("data-ref", "height");
            } else{
                _$this.attr("data-ref", "min-height");
            }
        },
        // 用一个属性记录下修改过的容器的宽或者高
        resetWH: function(_$target, _$this){
            var _direct = _$this.attr("data-direct");
            if(_$this.val()){
                _$target.attr("data-item_" + _direct, _$this.val());
            }
        },
        //如果是组件的话，不能超过容器。如果是容器的话， 不能超过 container
        reviewWH: function(_$this){
            var $target = LEStyle.getObject(this.layer);
            //组件的话，不能超过容器
            var $con = $target.parent().closest(".column").size() > 0 ? $target.parent().closest(".column") : $("#container");
            var _pw = parseInt($con.css("width")); //容器的宽

            var _w = parseInt($target.outerWidth(true));   //组件的宽

            if(_w > _pw - 2){   //如果组件 大于 容器
                $target.css("width", "");
                _$this.val($target.css("width"));


            }
        },
        resetWWColor: function(_$target, _$this){
            var styleStr = _$target.attr("style");
            var _param = _$this.attr("data-direct");
            if(styleStr){
                var hasWidth = styleStr.indexOf(_param) != "-1";
                hasWidth ? _$this.css("color", "#333") : _$this.css("color", "#7d7d7d");
            } else{
                _$this.css("color", "#7d7d7d");
            }
        },
        initConMiddle: function($po){
            var obj = this;
            this.$pc.find(".conMiddle").click(function(){
                var $target = LEStyle.getObject(obj.layer);
                $target.css({"margin-left": "auto", "margin-right": "auto"});
                $po.filter("[data-direct='margin-left'],[data-direct='margin-right']").text("auto");
                if($target.hasClass("le-lineChart")){
                    var _id=$target.attr("id");
                    var myChart = echarts.init(document.getElementById(_id));
                    var lineOption=lineChart_Map.get(_id);
                    myChart.setOption(lineOption)
                } else if($target.hasClass("column")) {
                    $target.find(".le-lineChart").each(function(){
                        var _id = $(this).attr("id");
                        var myChart = echarts.init(document.getElementById(_id));
                        var lineOption = lineChart_Map.get(_id);
                        myChart.setOption(lineOption)
                    });
                }
                LEHistory.trigger();
            });

            this.$pc.find(".conClear").click(function(){
                var $target = LEStyle.getObject(obj.layer);
                $target.css({
                    "margin-top": "0",
                    "margin-bottom": "0",
                    "margin-left": "0",
                    "margin-right": "0",
                    "padding-top": "0",
                    "padding-bottom": "0",
                    "padding-left": "0",
                    "padding-right": "0"
                });
                $po.text(0);
                var $target = LEStyle.getObject(obj.layer);
                if($target.hasClass("le-lineChart")){
                    var _id=$target.attr("id");
                    var myChart = echarts.init(document.getElementById(_id));
                    var lineOption=lineChart_Map.get(_id);
                    myChart.setOption(lineOption)
                } else if($target.hasClass("column")) {
                    $target.find(".le-lineChart").each(function(){
                        var _id = $(this).attr("id");
                        var myChart = echarts.init(document.getElementById(_id));
                        var lineOption = lineChart_Map.get(_id);
                        myChart.setOption(lineOption)
                    });
                }
                LEHistory.trigger();
                /*$po.filter("[data-direct='margin-left'],[data-direct='margin-right']").text("auto");*/
            });
        },
        resetLabels: function(){
            var $po = this.$pc.find(".positionData");
            var $target = LEStyle.getObject(this.layer);
            var $width = this.$pc.find(".positionwh").filter("[data-direct='width']");
            var $height = this.$pc.find(".positionwh").filter("[data-direct='height']");

            if(!$target) return;
            //先让input disabled
            //$width.attr("disabled", "disabled");
            //$height.attr("disabled", "disabled");

            $po.each(function(){
                var $this = $(this);
                var _attr = $this.data("direct");
                var _v = parseInt($target.css(_attr)) || 0;
                if(_attr.indexOf("margin-left") != -1){
                    _v = $target[0].style.marginLeft == "auto" ? "auto" : _v;
                }
                if(_attr.indexOf("margin-right") != -1){
                    _v = $target[0].style.marginRight == "auto" ? "auto" : _v;
                }

                $this.text(_v);
            });
            var _width = $target.attr("data-item_width") ? $target.attr("data-item_width") : $target.css("width");
            var _height = $target.attr("data-item_height") ? $target.attr("data-item_height") : $target.css("height");
            $width.val(_width);
            $height.val(_height);
            // $width.val($target.css("width"));
            // $height.val($target.css("height"));

            if($target.hasClass("column") && $target.parent().hasClass("w-col")){
                $width.attr("disabled", "disabled");
                $width.css("color", "gray");
                $width.removeClass("unitOption");
            } else{
                $width.attr("disabled", false);
                $width.css("color", "#333");
                $width.addClass("unitOption");
            }

            /*
             如果没有设置宽高属性, 宽高变色
             */
            this.resetWWColor($target, $width);
            this.resetWWColor($target, $height);
        }

    }
    ;
})(window, jQuery, LE, undefined);