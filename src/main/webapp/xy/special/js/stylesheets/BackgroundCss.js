/**
 * Created by admin on 2016/12/22.
 * 背景设置
 */
(function(window, $, LE){
    LE.stylesheets["css-BackGround"] = function(){
        var $Bg = $("#css-bgSection");
        return {
            init: function(){
                LE.obackgroundCss = new OBackground($Bg, 0);
            },
            run: function(options, doHide, doSlide){
                //重设背景
                LE.obackgroundCss.reset();
                //重设重复
                //切换组件时，样式设置部分回顶部
                sliderbarToTop();
                LEDisplay.show($Bg, doHide, doSlide);
            },
            destroy: function(){
                LE.obackgroundCss.destroy();
            }
        };
    };

    LE.stylesheets["css-BackGroundII"] = function(){
        var $Bg = $("#css-bgSection-II");
        return {
            init: function(){
                LE.obackgroundIICss = new OBackground($Bg, 1);
            },
            run: function(options, doHide, doSlide){
                //重设背景
                LE.obackgroundIICss.reset();
                //重设重复
                //切换组件时，样式设置部分回顶部
                sliderbarToTop();
                LEDisplay.show($Bg, doHide, doSlide);
            },
            destroy: function(){
                LE.obackgroundIICss.destroy();
            }
        };
    };

    LE.stylesheets["css-BackGroundIII"] = function(){
        var $Bg = $("#css-bgSection-III");
        return {
            init: function(){
                LE.obackgroundIIICss = new OBackground($Bg, 2);
            },
            run: function(options, doHide, doSlide){
                //重设背景
                LE.obackgroundIIICss.reset();
                //重设重复
                //切换组件时，样式设置部分回顶部
                sliderbarToTop();
                LEDisplay.show($Bg, doHide, doSlide);
            },
            destroy: function(){
                LE.obackgroundIIICss.destroy();
            }
        };
    };

    function OBackground($PC, layer){
        this.$pc = $PC;
        this.layer = layer;
        this.$x = this.$pc.find("[id^=css-bgX]");
        this.$y = this.$pc.find("[id^=css-bgY]");
        this.$ps = this.$pc.find(".bgPosition");
        this.$bgRepeat = this.$pc.find(".bgRepeat");
        this.$deleteImg = this.$pc.find("[id^=css-bgDeleteImg]");
        this.$preImg = this.$pc.find("[id^=css-bgPreviewImg]");
        this.$bgColorPick = this.$pc.find("[id^=css-bgColorPick]");
        this.$bgColorPickInput = this.$pc.find("[id^=css-inputBGColorPick]");
        this.init($PC, layer);
    }
    OBackground.prototype = {
        init: function($PC, layer){
            this.initColorPick($PC, layer);
            this.initUpload();
            this.initBgPositionEvent();
            this.initXY();
            this.initDeleteImageEvent();
        },
        reset: function(){
            this.resetRepeat();
            this.resetBackground();
            //重设位置
            this.resetPosition();
        },
        destroy: function(){
            this.$preImg.attr("src", "");
            this.$ps.removeClass("select");
            this.$bgRepeat.removeClass("select");
            this.$pc.hide();
        },
        cssSetTargetBg:function(attr,val){
            var obj = this;
            var $target = LEStyle.getObject(this.layer);
            //把对应的样式写到css文件
            var partSelector=obj.getPartSelector($target);
            var selector=obj.setCssStyle(partSelector,attr, val);
        },
        getPartSelector:function($target){
            var partSelector;
            if($target.hasClass("le-list-group")){          //列表最外层
                partSelector="";
            }else if($target.hasClass("list-group-item")){          //每一行
                partSelector=".list-group-item";
            }else if($target.parent().hasClass("media-body") && $target.children("li").hasClass("titList")){   //标题
                partSelector=".list-group-item > .media > .media-body > ul";
            }else if($target.hasClass("listSummaryBox")){          //摘要
                partSelector=".list-group-item > .media > .media-body > .listSummary > .listSummaryBox";
            }else if($target.hasClass("listSourceDetailedStyle")){      //来源
                partSelector=".list-group-item .listSourceDetailedStyle";
            }else if($target.hasClass("listTimeDetailedStyle")){         //时间
                partSelector=".list-group-item .listTimeDetailedStyle";
            }else if($target.hasClass("moreListShow")){                   //右侧链接
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
                obj.cssSetTargetBg($this.attr("data-ref"), num + _unit);
                $this.val(num + _unit);
                return num;
                obj.resetPosition();
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
            obj.cssSetTargetBg($this.attr("data-ref"), num + _unit);
            $this.val(num + _unit);
            obj.resetPosition();
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
        initColorPick: function($PC, layer){
            var obj = this;

            //输入框颜色修改
            obj.$bgColorPickInput.on('change',function(){
                var tinycolor=$(this).val();
                obj.$bgColorPick.spectrum("set", tinycolor);
                obj.cssSetTargetBg("background-color",tinycolor);
                LEHistory.trigger();
            });

            this.$bgColorPick.spectrum(
                LEColorPicker.getOptions(function(tinycolor){
                    var _c = tinycolor ? tinycolor.toRgbaString() : "";
                    obj.cssSetTargetBg("background-color",tinycolor);
                    var _v = tinycolor ? tinycolor.toHexString() : "#000000";
                    obj.$bgColorPickInput.val(tinycolor);
                    LEHistory.trigger();
                })
            );

        },
        initUpload: function(){
            var obj = this;
            this.$preImg.click(function(e){
                var isNew = obj.$preImg.attr("src") != "export/images/sliderPanel/sliderPanel1.png";
                if(isNew){
                    var data = [];
                    data.push(obj.$preImg.attr("src"));
                    LEDialog.toggleDialog(LE.options["Dialog"].picEditDialog, function(imgList){
                            var $target = LEStyle.getObject(obj.layer);
                            if(imgList.length > 0){
                                //   $target.css("backgroundImage", "url('" + imgList[0].path + "')");
                                obj.cssSetTargetBg("background-image", "url('" + imgList[0].path + "')");
                                LEHistory.trigger();
                                obj.resetBackground();
                            }
                        },
                        data
                    );
                } else{
                    LEDialog.toggleDialog(LE.options["Dialog"].picUploadDialog, function(jsonUploadImg){
                        var url = jsonUploadImg.imgPath;
                        var $target = LEStyle.getObject(obj.layer);
                        //   $target.css("backgroundImage", "url('" + url + "')");
                        obj.cssSetTargetBg("background-image", "url('" + url + "')");
                        obj.$bgRepeat.filter("[data-ref=no-repeat]").click();
                        LEHistory.trigger();
                        obj.resetBackground();
                    });
                }

            });
            this.$pc.find("[id^=css-bgFileLabel]").hover(function(){
                $.trim(obj.$preImg.attr("src")) != "export/images/sliderPanel/sliderPanel1.png" && obj.$deleteImg.show();
            }, function(){
                obj.$deleteImg.hide();
            });
        },
        resetBackground: function(){
            var _$cp = this.$bgColorPick;
            var $target = LEStyle.getObject(this.layer);
            _$cp.spectrum("set", $target.css("background-color"));
            // this.$bgColorPickInput.val(_$cp.spectrum("get").toHexString());
            //保留rgba色值
            this.$bgColorPickInput.val(_$cp.spectrum("get"));
            var _img = $target.css("backgroundImage") == "none" ? "export/images/sliderPanel/sliderPanel1.png" : $target.css("backgroundImage");
            if(_img != ""){
                _img = _img.replace(/url\([\"\']?/g, "").replace(/[\"\']?\)/g, "");
            }
            _img && this.$preImg.attr("src", _img);
        },
        initBgPositionEvent: function(){
            var obj = this;
            this.$ps.click(function(e){
                var _$this = $(this);
                var $target = LEStyle.getObject(obj.layer);
                //   $target.css("backgroundPosition", _$this.data("x") + " " + _$this.data("y"));
                obj.cssSetTargetBg("background-position", _$this.data("x") + " " + _$this.data("y"));
                obj.$x.val(_$this.data("x"));
                obj.$y.val(_$this.data("y"));
                LEHistory.trigger();
            });

            this.$bgRepeat.click(function(){
                var _$this = $(this);
                var $target = LEStyle.getObject(obj.layer);
                var selected = _$this.hasClass("select");
                _$this.siblings(".bgRepeat").removeClass("select");
                if(selected){
                    _$this.siblings().removeClass("select");
                    //   $target.css("background-repeat", "");
                    obj.cssSetTargetBg("background-repeat", "");
                } else{
                    _$this.addClass("select");
                    //   $target.css("background-repeat", _$this.data("ref"));
                    obj.cssSetTargetBg("background-repeat",  _$this.data("ref"));
                }
                LEHistory.trigger();
            });

        },
        initXY: function(){
            var obj = this;
            this.$pc.find(".bgxy").keydown(function(e){
                var $target = LEStyle.getObject(obj.layer);
                // LEKey.numInputKeyDown(e, $(this), $target);
                obj.cssNumInputKeyDown(e, $(this), $target);
            });
            //失去焦点的时候，置0
            this.$pc.find(".bgxy").blur(function(e){
                var $target = LEStyle.getObject(obj.layer);
                //  LEKey.numInputBlur(e, $(this), $target);
                obj.cssNumInputBlur(e, $(this), $target);
                LEHistory.trigger();
            });

            this.$pc.find(".bgxy").focus(function(e){
                var $target = LEStyle.getObject(obj.layer);
                //  LEKey.numInputFocus(e, $(this), $target);
                obj.cssNumInputFocus(e, $(this), $target);
            });
        },
        initDeleteImageEvent: function(){
            var obj = this;
            this.$deleteImg.click(function(){
                obj.$preImg.attr("src", "");
                var $target = LEStyle.getObject(obj.layer);
                //    $target.css("backgroundImage", "");
                obj.cssSetTargetBg("background-image", "");
                LEHistory.trigger();

            });
        },
        resetRepeat: function(){
            var $target = LEStyle.getObject(this.layer);
            this.$bgRepeat.filter("[data-ref=" + $target.css("background-repeat") + "]").addClass("select");
        },
        resetPosition: function(){
            var $target = LEStyle.getObject(this.layer);
            var _x = $target.css("background-position-x");
            var _y = $target.css("background-position-y");
            this.$ps.filter("[data-x='" + _x + "']").filter("[data-y='" + _y + "']").addClass("select").siblings().removeClass("select");
            this.$x.val(_x);
            this.$y.val(_y);
        }
    };
})(window, jQuery, LE, undefined);