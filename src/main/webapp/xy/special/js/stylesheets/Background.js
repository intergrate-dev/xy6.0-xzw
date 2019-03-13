/**
 * Created by isaac_gu on 2016/1/14.
 * 背景设置
 */
(function(window, $, LE){
    LE.stylesheets["BackGround"] = function(){
        var $Bg = $("#bgSection");
        return {
            init: function(){
                LE.obackground = new OBackground($Bg, 0);
            },
            run: function(options, doHide, doSlide){
                //重设背景
                LE.obackground.reset();
                //重设重复
                //切换组件时，样式设置部分回顶部
                sliderbarToTop();
                LEDisplay.show($Bg, doHide, doSlide);
            },
            destroy: function(){
                LE.obackground.destroy();
            }
        };
    };

    LE.stylesheets["BackGroundII"] = function(){
        var $Bg = $("#bgSection-II");
        return {
            init: function(){
                LE.obackgroundII = new OBackground($Bg, 1);
            },
            run: function(options, doHide, doSlide){
                //重设背景
                LE.obackgroundII.reset();
                //重设重复
                //切换组件时，样式设置部分回顶部
                sliderbarToTop();
                LEDisplay.show($Bg, doHide, doSlide);
            },
            destroy: function(){
                LE.obackgroundII.destroy();
            }
        };
    };

    LE.stylesheets["BackGroundIII"] = function(){
        var $Bg = $("#bgSection-III");
        return {
            init: function(){
                LE.obackgroundIII = new OBackground($Bg, 2);
            },
            run: function(options, doHide, doSlide){
                //重设背景
                LE.obackgroundIII.reset();
                //重设重复
                //切换组件时，样式设置部分回顶部
                sliderbarToTop();
                LEDisplay.show($Bg, doHide, doSlide);
            },
            destroy: function(){
                LE.obackgroundIII.destroy();
            }
        };
    };

    function OBackground($PC, layer){
        this.$pc = $PC;
        this.layer = layer;
        this.$x = this.$pc.find("[id^=bgX]");
        this.$y = this.$pc.find("[id^=bgY]");
        this.$ps = this.$pc.find(".bgPosition");
        this.$bgRepeat = this.$pc.find(".bgRepeat");
        this.$deleteImg = this.$pc.find("[id^=bgDeleteImg]");
        this.$preImg = this.$pc.find("[id^=bgPreviewImg]");
        this.$bgColorPick = this.$pc.find("[id^=bgColorPick]");
        this.$bgColorPickInput = this.$pc.find("[id^=inputBGColorPick]");
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
        initColorPick: function($PC, layer){
            var obj = this;
            this.$bgColorPick.spectrum(
                LEColorPicker.getOptions(function(tinycolor){
                    var _c = tinycolor ? tinycolor.toRgbaString() : "";
                    var $target = LEStyle.getObject(obj.layer);
                    $target.css("background-color", _c);
                    var _v = tinycolor ? tinycolor.toHexString() : "#000000";
                    obj.$bgColorPickInput.val(_v);
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
                                $target.css("backgroundImage", "url('" + imgList[0].path + "')");
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
                        $target.css("backgroundImage", "url('" + url + "')");
                        obj.$bgRepeat.filter("[data-ref=no-repeat]").click();
                        LEHistory.trigger();
                        obj.resetBackground();
                    });
                }

            });
            this.$pc.find("[id^=bgFileLabel]").hover(function(){
                $.trim(obj.$preImg.attr("src")) != "export/images/sliderPanel/sliderPanel1.png" && obj.$deleteImg.show();
            }, function(){
                obj.$deleteImg.hide();
            });
        },
        resetBackground: function(){
            var _$cp = this.$bgColorPick;
            var $target = LEStyle.getObject(this.layer);
            _$cp.spectrum("set", $target.css("background-color"));

            this.$bgColorPickInput.val(_$cp.spectrum("get").toHexString());
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
                $target.css("backgroundPosition", _$this.data("x") + " " + _$this.data("y"));
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
                    _$this.removeClass("select");
                    $target.css("background-repeat", "");
                } else{
                    _$this.addClass("select");
                    $target.css("background-repeat", _$this.data("ref"));
                }
                LEHistory.trigger();
            });

        },
        initXY: function(){
            var obj = this;
            this.$pc.find(".bgxy").keydown(function(e){
                var $target = LEStyle.getObject(obj.layer);
                LEKey.numInputKeyDown(e, $(this), $target);
            });
            //失去焦点的时候，置0
            this.$pc.find(".bgxy").blur(function(e){
                var $target = LEStyle.getObject(obj.layer);
                LEKey.numInputBlur(e, $(this), $target);
                LEHistory.trigger();
            });

            this.$pc.find(".bgxy").focus(function(e){
                var $target = LEStyle.getObject(obj.layer);
                LEKey.numInputFocus(e, $(this), $target);
            });
        },
        initDeleteImageEvent: function(){
            var obj = this;
            this.$deleteImg.click(function(){
                obj.$preImg.attr("src", "");
                var $target = LEStyle.getObject(obj.layer);
                $target.css("backgroundImage", "");
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
            this.$ps.filter("[data-x='" + _x + "']").filter("[data-y='" + _y + "']").addClass("select");
            this.$x.val(_x);
            this.$y.val(_y);
        }
    };
})(window, jQuery, LE, undefined);