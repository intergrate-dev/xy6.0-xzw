/**
 * Created by isaac_gu on 2016/1/14.
 */
(function(window, $, LE){
    LE.stylesheets["Link"] = function(){
        var $PC = $("#linkSection");
        return {
            init: function(){
                LE.Link = new OLink($PC, 0);
                LE.Link.initTextDialog();
            },
            run: function(options, doHide, doSlide){
                LE.Link.reset();
                LEDisplay.show($PC, doHide, doSlide);
            },
            destroy: function(){
                $PC.hide();
            }
        };
    };

    LE.stylesheets["LinkIII"] = function(){
        var $PC = $("#linkSection-III");
        return {
            init: function(){
                LE.oLinkIII = new OLink($PC, 2);
            },
            run: function(options, doHide, doSlide){
                LE.oLinkIII.reset();
                LEDisplay.show($PC, doHide, doSlide);
            },
            destroy: function(){
                $PC.hide();
            }
        };
    };

    function OLink($PC, layer){
        this.$pc = $PC;
        this._layer = layer;
        this.$fontActive = this.$pc.find("[id^=fontActive]");
        this.$linkTextDialog = $("#linkTextDialog");
        this.$hoverColorPick = this.$pc.find("[id^=hoverColorPick]");
        this.$hoverUnder = this.$pc.find("[id^=hoverUnder]");
        this.$hoverColor = this.$pc.find("[id^=hoverColor]");
        this.$linkUnder = this.$pc.find("[id^=linkUnder]");
        this.$linkColor = this.$pc.find("[id^=linkColor]");
        this.$linkColorPick = this.$pc.find("[id^=linkColorPick]");
        this.init($PC);
    }

    OLink.prototype = {
        layer: 0,
        init: function(){
            this.initLinkEvent();
            this.initLinkColorEvent();
            this.initHoverColorEvent();
            this.linkUnder();
            this.hoverUnder();
            this.initLinkColorChange();
            this.initHoverColorChange();
            this.linkColorDefaultStatus();
            this.hoverColorDefaultStatus();
        },
        reset: function(){
            this.resetLinkColorStatus();
            this.resetLinkDecStatus();
            this.resetHoverColorStatus();
            this.resetHoverDecStatus();
            OLink.layer = this._layer;
            //切换组件时，样式设置部分回顶部
            sliderbarToTop();
        },
        initTextDialog: function(){
             var obj = this;
            $("#TextUrlBtn").click(function(){
                var $target = LEStyle.getObject(OLink.layer);
                $target.find("a").each(function(){
                    $(this).after($(this).html());
                    $(this).remove();
                });
               // debugger
                $target.wrapInner('<a href="' + $("#TextUrlText").val() + '"' +
                    ($("#openNewWindowBtn2").is(":checked")?'target="_blank"': "") +
                    '></a>');
                obj.changeLinkStyle("a", "text-decoration", obj.$linkUnder.attr("data-value"));
                obj.changeLinkStyle("a", "color", obj.$linkColor.val());
                obj.changeLinkStyle("a:hover", "text-decoration", obj.$hoverUnder.attr("data-value"));
                obj.changeLinkStyle("a:hover", "color", obj.$hoverColor.val());
                obj.$linkTextDialog.modal("hide");
                obj.resetLinkColorStatus();
                obj.resetLinkDecStatus();
                obj.resetHoverColorStatus();
                obj.resetHoverDecStatus();
                LEHistory.trigger();
            });

            $("#TextDivClose").click(function(){
                obj.$linkTextDialog.modal("hide");
            });
        },
        initLinkEvent: function(){
            var obj = this;
            obj.$fontActive.bind("click", function(){
                obj.$linkTextDialog.modal("show");
            });

            /*$("#TextUrlText").keypress(function (e) {
             if (e.keyCode == 13) {
             var $target = LECurrentObject;
             $target.find("a").each(function(){
             $(this).after($(this).html());
             $(this).remove();
             });
             $target.wrapInner("<a href='http://www.baidu.com'></a>");
             //  }
             changeLinkStyle("a", "text-decoration", $linkUnder.attr("data-value"));
             changeLinkStyle("a", "color", $linkColor.val());
             changeLinkStyle("a:hover", "text-decoration", $hoverUnder.attr("data-value"));
             changeLinkStyle("a:hover", "color", $hoverColor.val());
             $linkTextDialog.modal("hide");
             resetLinkColorStatus();
             resetLinkDecStatus();
             resetHoverColorStatus();
             resetHoverDecStatus();
             LEHistory.trigger();
             }
             });*/
        },
        changeLinkStyle: function(partSelector, sccName, cssValue){
            var obj = this;
            var $target = LEStyle.getObject(OLink.layer);
            var id = $target.attr("id");
            var selector = "#" + id + " " + partSelector;
            StyleManager.setStyle(selector, sccName, cssValue);
        },
        /*修改输入框的值改变超链接颜色和颜色选择框默认样式*/
        initLinkColorChange: function(){
            var obj = this;
            obj.$linkColor.change(function(){
                var reg = /^#([0-9a-fA-F]{3}|[0-9a-fA-F]{6})$/;
                if(reg.test($(this).val())){
                    obj.$linkColorPick.spectrum("set", $(this).val());
                    obj.changeLinkStyle("a", "color", $(this).val());
                } else{
                    $(this).val("#0000ff");
                    obj.$linkColorPick.spectrum("set", "#0000ff");
                    obj.changeLinkStyle("a", "color", "#0000ff");
                }
                LEHistory.trigger();
            })
        },
        /*修改输入框的值改变超链接悬停颜色和颜色选择框默认样式*/
        initHoverColorChange: function(){
            var obj = this;
            obj.$hoverColor.change(function(){
                var reg = /^#([0-9a-fA-F]{3}|[0-9a-fA-F]{6})$/;
                if(reg.test($(this).val())){
                    obj.$hoverColorPick.spectrum("set", $(this).val());
                    obj.changeLinkStyle("a:hover", "color", $(this).val());
                } else{
                    $(this).val("#ff0000");
                    obj.$hoverColorPick.spectrum("set", "#ff0000");
                    obj.changeLinkStyle("a:hover", "color", "#ff0000");
                }
                LEHistory.trigger();
            })
        },
        /*a标签的默认下划线有无*/
        linkUnder: function(){
            var obj = this;
            obj.$linkUnder.bind("click", function(){
                if(obj.$linkUnder.hasClass("select")){
                    $(this).removeClass("select");
                    $(this).attr("data-value", "none");
                } else{
                    $(this).addClass("select");
                    $(this).attr("data-value", "underline");
                }
                obj.changeLinkStyle("a", "text-decoration", obj.$linkUnder.attr("data-value"));
                LEHistory.trigger();
            });

        },
        /*a标签悬浮时的默认下划线有无*/
        hoverUnder: function(){
            var obj = this;
            obj.$hoverUnder.bind("click", function(){
                if(obj.$hoverUnder.hasClass("select")){
                    $(this).removeClass("select");
                    $(this).attr("data-value", "none");
                } else{
                    $(this).addClass("select");
                    $(this).attr("data-value", "underline");
                }
                obj.changeLinkStyle("a:hover", "text-decoration", obj.$hoverUnder.attr("data-value"));
                LEHistory.trigger();
            });
        },
        /*字体超链接的初始化颜色 蓝色*/
        linkColorDefaultStatus: function(){
            var obj = this;
            obj.$linkColorPick.spectrum("set", "#00f");
        },
        /*a标签的默认颜色，点击可以改变，原理是通过点击按钮动态改变html中style标签的内容*/
        initLinkColorEvent: function(){
            var obj = this;
            obj.$linkColorPick.spectrum(
                LEColorPicker.getOptions(function(tinycolor){
                    var _c = tinycolor ? tinycolor.toRgbaString() : "";
                    var _v = tinycolor ? tinycolor.toHexString() : "#000000";
                    //获取颜色值填到input
                    obj.$linkColor.val(_v);
                    //changeLinkStyle("a", "color", $linkColor.val());
                    obj.changeLinkStyle("a", "color", _c);
                    LEHistory.trigger();
                })
            );
        },
        /*字体超链接的初始化颜色 红色*/
        hoverColorDefaultStatus: function(){
            var obj = this;
            obj.$hoverColorPick.spectrum("set", "#f00");
        },

        /*a标签悬浮时的默认颜色，点击可以改变，原理是通过点击按钮动态改变html中style标签的内容*/
        initHoverColorEvent: function(){
            var obj = this;
            obj.$hoverColorPick.spectrum(
                LEColorPicker.getOptions(function(tinycolor){
                    var _c = tinycolor ? tinycolor.toRgbaString() : "";
                    var _v = tinycolor ? tinycolor.toHexString() : "#000000";
                    //获取颜色值填到input
                    obj.$hoverColor.val(_v);
                    //changeLinkStyle("a:hover", "color", $hoverColor.val());
                    obj.changeLinkStyle("a:hover", "color", _c);
                    LEHistory.trigger();
                })
            );
        },
        /*初始化颜色输入框的值
         1、判断刚开始目标是否有a标签颜色 没有设置为蓝色 有的话为当前颜色
         2、将获得的颜色从rgb格式转换为十六进制*/
        resetLinkColorStatus: function(){
            var obj = this;
            var $target = LEStyle.getObject(obj._layer);
            var target = $target.attr("id");
            var NormalA = StyleManager.getStyle("#" + target + " a", "color");
            NormalA = NormalA == "none"? $target.find("a").css("color"):StyleManager.getStyle("#" + target + " a", "color");
            if(!NormalA){
                NormalA="rgb(0,0,255)"
            }
            obj.$linkColorPick.spectrum("set", NormalA);
            obj.$linkColor.val(obj.$linkColorPick.spectrum("get").toHexString());
            /*if(NormalA == "none"){
                //2否则默认样式
                obj.$linkColor.val("#0000ff");
                obj.$linkColorPick.spectrum("set", "#00f");
            } else{
                //1、如果html中存在自定义选择器
                NormalA = StyleManager.getStyle("#" + target + " a", "color");
                obj.$linkColorPick.spectrum("set", NormalA);
                obj.$linkColor.val(obj.$linkColorPick.spectrum("get").toHexString());
            }*/
        },
        resetLinkDecStatus: function(){
            var obj = this;
            var $target = LEStyle.getObject(obj._layer);
            var target = $target.attr("id");
            var NormalADec = StyleManager.getStyle("#" + target + " a", "text-decoration");
            if(NormalADec == "none"){
                obj.$linkUnder.removeClass("select");
            } else{
                obj.$linkUnder.addClass("select");
            }
        },
        resetHoverColorStatus: function(){
            var obj = this;
            var $target = LEStyle.getObject(obj._layer);
            var target = $target.attr("id");
            var HoverA = StyleManager.getStyle("#" + target + " a:hover", "color");
            if(HoverA == "none"){
                if($target.hasClass("moreListShow")){
                    obj.$hoverColor.val("#23527c");
                    obj.$hoverColorPick.spectrum("set", "#23527c");
                }else{
                    //2否则默认样式
                    obj.$hoverColor.val("#ff0000");
                    obj.$hoverColorPick.spectrum("set", "#f00");
                }
            } else{
                //1、如果html中存在自定义选择器
                HoverA = StyleManager.getStyle("#" + target + " a:hover", "color");
                obj.$hoverColorPick.spectrum("set", HoverA);
                obj.$hoverColor.val(obj.$hoverColorPick.spectrum("get").toHexString());
            }
        },
        resetHoverDecStatus: function(){
            var obj = this;
            var $target = LEStyle.getObject(obj._layer);
            var target = $target.attr("id");
            var HoverADec = StyleManager.getStyle("#" + target + " a:hover", "text-decoration");
            if(HoverADec == "none"){
                if($target.hasClass("moreListShow")){
                    obj.$hoverUnder.addClass("select");
                }else{
                    obj.$hoverUnder.removeClass("select");
                }
            } else{
                obj.$hoverUnder.addClass("select");
            }
        }
    }

})(window, jQuery, LE, undefined);