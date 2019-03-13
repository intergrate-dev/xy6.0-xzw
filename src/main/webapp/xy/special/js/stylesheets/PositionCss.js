/**
 * Created by qianxm on 2016/12/20.
 * position-type 从entity中的 position-type中取，如果没有，就从option中取
 */
(function(window, $, LE){
    LE.stylesheets["css-Position"] = function(){
        var $PC = $("#css-positionSection");
        return {
            init: function(){
                LE.cssPosition = new OPosition($PC, 0);
            },
            run: function(options, doHide, doSlide){
                LE.cssPosition.resetLabels();
                LEDisplay.show($PC, doHide, doSlide);
    },
            destroy: function(){
                $PC.hide();
            }
        };
    };

    LE.stylesheets["css-PositionII"] = function(){
        var $PC = $("#css-positionSection-II");
        return {
            init: function(){
                LE.cssPositionII = new OPosition($PC, 1);
            },
            run: function(options, doHide, doSlide){
                LE.cssPositionII.resetLabels();
                LEDisplay.show($PC, doHide, doSlide);
            },
            destroy: function(){
                $PC.hide();
            }
        };
    };


    LE.stylesheets["css-PositionIII"] = function(){
        var $PC = $("#css-positionSection-III");
        return {
            init: function(){
                LE.cssPositionIII = new OPosition($PC, 2);
            },
            run: function(options, doHide, doSlide){
                LE.cssPositionIII.resetLabels();
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
                    //var s = obj.setTargetPosition($this.data("direct"), num);
                    //$this.text(parseInt(s));
                    var _selector = obj.cssSetTargetPosition($this.data("direct"), num+"px");
                    var s=StyleManager.getStyle(_selector,$this.data("direct"));
                    $this.text(parseInt(s));
                }
            });

            //失去焦点的时候，置0
            $po.blur(function(){
                var $this = $(this);
                $.trim($this.text()) == "" && $this.text(0);
                //处理输入框里面是以0开头的问题
                var num = parseInt($this.text());
                //var s = obj.setTargetPosition($this.data("direct"), num);
                //$this.text(parseInt(s));
                var _selector = obj.cssSetTargetPosition($this.data("direct"), num+"px");
                var s=StyleManager.getStyle(_selector,$this.data("direct"));
                if(s=="auto"){
                    s="auto";
                }else{
                    s=parseInt(s);
                }
                $this.text(s);
                LEHistory.trigger();
            });

        },
        cssSetTargetPosition:function(attr, dis){
            var obj = this;
            var $target = LEStyle.getObject(this.layer);
            //把对应的样式写到css文件
            var partSelector=obj.getPartSelector($target);
            var selector=obj.setCssStyle(partSelector,attr, dis);
            return selector;
        },
        getPartSelector:function($target){
            var partSelector;
            if($target.hasClass("le-list-group")){              //列表最外层
                partSelector="";
            }else if($target.hasClass("list-group-item")){       //列表每一行
                partSelector=".list-group-item";
            }else if($target.parent().hasClass("media-body") && $target.children("li").hasClass("titList")){ //列表标题
                partSelector=".list-group-item > .media > .media-body > ul";
            }else if($target.hasClass("listSummaryBox")){          //列表摘要
                partSelector=".list-group-item > .media > .media-body > .listSummary > .listSummaryBox";
            }else if($target.hasClass("listSourceDetailedStyle")){   //列表来源
                partSelector=".list-group-item .listSourceDetailedStyle";
            }else if($target.hasClass("listTimeDetailedStyle")){     //列表时间
                partSelector=".list-group-item .listTimeDetailedStyle";
            }else if($target.hasClass("moreListShow")){           //列表右侧链接
                partSelector=".moreListShow";
            }else if($target.hasClass("le-carousel")){            //轮播图
                partSelector="";
            }else if($target.hasClass("le-gallery")){            //多图
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
                    /*_dis = obj.setTargetPosition(_attr, _dis);
                    _$po.text(parseInt(_dis));*/
                    //将属性值写入style标签
                    var _selector = obj.cssSetTargetPosition(_attr, _dis+"px");
                    //读取style标签中对应的属性值
                    _dis=StyleManager.getStyle(_selector,_attr);
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
                   // LEKey.numInputKeyDown(e, $(this), $target);
                    obj.cssNumInputKeyDown(e, $(this), $target);
                    LE.stylesheets["Gallery"]().changeImageHeight();
                } else if($target.hasClass("le-carousel")){
                    //LEKey.numInputKeyDown(e, $(this), $target);
                    obj.cssNumInputKeyDown(e, $(this), $target);
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
                    obj.cssNumInputKeyDown(e, $(this), $target);
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
                   // LEKey.numInputBlur(e, $(this), $target);
                    obj.cssNumInputBlur(e, $(this), $target);
                    LE.stylesheets["Gallery"]().changeImageHeight();
                } else if($target.hasClass("le-carousel")){
                    //LEKey.numInputBlur(e, $(this), $target);
                    obj.cssNumInputBlur(e, $(this), $target);
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
                    //LEKey.numInputBlur(e, $(this), $target);
                    obj.cssNumInputBlur(e, $(this), $target);
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
                    //LEKey.numInputFocus(e, $(this), $target);
                    obj.cssNumInputFocus(e, $(this), $target);
                    LE.stylesheets["Gallery"]().changeImageHeight();
                } else if($target.hasClass("le-carousel")){
                   // LEKey.numInputFocus(e, $(this), $target);
                    obj.cssNumInputFocus(e, $(this), $target);
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
                    /*LEKey.numInputFocus(e, $(this), $target);*/
                    obj.cssNumInputFocus(e, $(this), $target);
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
            if($target.hasClass("le-gallery") || $target.hasClass("le-carousel") || $target.hasClass("le-image")|| $target.hasClass("le-lineChart")){
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
            var obj=this;
            var $target = LEStyle.getObject(this.layer);
            //组件的话，不能超过容器
            var $con = $target.parent().closest(".column").size() > 0 ? $target.parent().closest(".column") : $("#container");
            var _pw = parseInt($con.css("width")); //容器的宽

            var _w = parseInt($target.outerWidth(true));   //组件的宽
            if(_w > _pw - 2){   //如果组件 大于 容器
                /*$target.css("width", "");
                _$this.val($target.css("width"));*/
                $target.css("width", "");
                var _selector = obj.cssSetTargetPosition("width", "");
                var s=s?StyleManager.getStyle(_selector,"width"):$target.css("width");
                //_$this.text(parseInt(s));
                _$this.val(s);
            }
        },
        resetWWColor: function(_$target, _$this){
            var obj=this;
            /*var styleStr = _$target.attr("style");
            var _param = _$this.attr("data-direct");
            if(styleStr){
                var hasWidth = styleStr.indexOf(_param) != "-1";
                hasWidth ? _$this.css("color", "#333") : _$this.css("color", "#7d7d7d");
            } else{
                _$this.css("color", "#7d7d7d");
            }*/
            var _partSelect=obj.getPartSelector(_$target);
            var id = LECurrentObject.attr("id");
            var _param = _$this.attr("data-direct");
            var _selector = $.trim("#" + id + " " + _partSelect);
            if(_param=="height" && StyleManager.getStyle(_selector,"height")=="none"){
                _param="min-height";
            }

            var hasSet=StyleManager.getStyle(_selector,_param);
            if(hasSet!="none"){
                _$this.css("color", "#333");
            }else{
                _$this.css("color", "#7d7d7d");
            }
        },
        initConMiddle: function($po){
            var obj = this;
            this.$pc.find(".conMiddle").click(function(){
                var $target = LEStyle.getObject(obj.layer);
                //$target.css({"margin-left": "auto", "margin-right": "auto"});
                obj.cssSetTargetPosition("margin-left", "auto");
                obj.cssSetTargetPosition("margin-right", "auto");
                $po.filter("[data-direct='margin-left'],[data-direct='margin-right']").text("auto");
                LEHistory.trigger();
            });

            this.$pc.find(".conClear").click(function(){
                var $target = LEStyle.getObject(obj.layer);
                /*$target.css({
                    "margin-top": "0",
                    "margin-bottom": "0",
                    "margin-left": "0",
                    "margin-right": "0",
                    "padding-top": "0",
                    "padding-bottom": "0",
                    "padding-left": "0",
                    "padding-right": "0"
                });*/
                obj.cssSetTargetPosition("margin", "0");
                obj.cssSetTargetPosition("padding", "0");
                $po.text(0);
                LEHistory.trigger();
                /*$po.filter("[data-direct='margin-left'],[data-direct='margin-right']").text("auto");*/
            });
        },
        resetLabels: function(){
            var obj=this;
            var $po = this.$pc.find(".positionData");
            var $target = LEStyle.getObject(this.layer);
            var $width = this.$pc.find(".positionwh").filter("[data-direct='width']");
            var $height = this.$pc.find(".positionwh").filter("[data-direct='height']");

            if(!$target) return;
            //先让input disabled
            //$width.attr("disabled", "disabled");
            //$height.attr("disabled", "disabled");

            $po.each(function(){
                /*var $this = $(this);
                var _attr = $this.data("direct");
                var _v = parseInt($target.css(_attr)) || 0;
                if(_attr.indexOf("margin-left") != -1){
                    _v = $target[0].style.marginLeft == "auto" ? "auto" : _v;
                }
                if(_attr.indexOf("margin-right") != -1){
                    _v = $target[0].style.marginRight == "auto" ? "auto" : _v;
                }
                $this.text(_v);*/

                var $this = $(this);
                var _partSelect=obj.getPartSelector($target);
                var id = LECurrentObject.attr("id");
                var _param = $this.attr("data-direct");
                var _selector = $.trim("#" + id + " " + _partSelect);
                var _v=StyleManager.getStyle(_selector,_param)/*=="none"? $target.css(_param) || 0:parseInt(StyleManager.getStyle(_selector,_param));*/
                if(_v=="none" && $target.css(_param) != "auto"){
                    _v=parseInt($target.css(_param))|| 0;
                }else if(_v=="auto" || $target.css(_param) == "auto"){
                    _v="auto";
                }else{
                    _v=parseInt(StyleManager.getStyle(_selector,_param))
                }

                if(_param.indexOf("margin-left") != -1){
                    _v = _v == "auto" ? "auto" : _v;
                }
                if(_param.indexOf("margin-right") != -1){
                    _v =_v == "auto" ? "auto" : _v;
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