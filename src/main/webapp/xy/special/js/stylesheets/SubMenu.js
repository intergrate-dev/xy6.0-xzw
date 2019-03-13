/**
 * Created by isaac_gu on 2016/1/14.
 */
(function (window, $, LE) {
    LE.stylesheets["SubMenu"] = function () {
        //当前功能面板的对象
        var $PS = $("#subMenuSection");
        var $subLitxColor = $("#subLitxColor");
        var $subLibgColor = $("#subLibgColor");
        var $subModelDiv = $("#subModelDiv");
        var $subMenuNormal = $("#subMenuNormal");
        var $subMenuHover = $("#subMenuHover");
        var $subMenuClick = $("#subMenuClick");
        var $subLitxColorPick = $("#subLitxColorPick");
        var $subLibgColorPick = $("#subLibgColorPick");
        var $subModelPreview = $("#subModelPreview");
        var $subModelGoBack = $("#subModelGoBack");
        var $subAlignUl = $("#subAlignUl");
        var $subTabWH = $(".subTabWH");
        var $subTabPadding = $("#subTabPadding");

        /*标签栏更多样式面板出现消失*/
        var initSubModelShow = function () {
            /*$("#subModelMore")*/
            $subModelPreview.bind("click", function () {
                $subModelGoBack.animate({right: "0"}, "fast", "linear");
                $subModelDiv.animate({right: "0"}, "fast", "linear");
            });
            $subModelGoBack.bind("click", function () {
                $subModelGoBack.animate({right: "-250px"}, "fast", "linear");
                $subModelDiv.animate({right: "-250px"}, "fast", "linear");
            });
        };
        /*
         点击标签栏模板按钮的同时，更新样式预览处的html。
         */
        function initSubModelPreview() {
            $subModelDiv.find("ul").bind("click", function () {
                $subModelPreview.html($(this).html());
            })
        }

        /*
         点击标签样式按钮
         为目标对象添加class 同时去掉<style>标签中的样式 每次添加记住class 下次点击先移除 再添加
         */
        var initChangeSubStyleBlock = function () {
            $subModelDiv.find(".displayStyle").click(function (e) {
                var tarId = LECurrentObject.attr("id");
                var removeId = "#" + tarId + " .nav-pills li ul.dropdown-menu li a";
                //清除style标签中的内容
                StyleManager.removeStyle(removeId);
                //主导航border相关data-样式还原
                LECurrentObject.attr("data-3-radius", "0px");
                LECurrentObject.attr("data-4-radius", "0px");
                LECurrentObject.attr("data-5-radius", "0px");

                LECurrentObject.attr("data-3-style", "none");
                LECurrentObject.attr("data-4-style", "none");
                LECurrentObject.attr("data-5-style", "none");

                LECurrentObject.attr("data-3-width", "3px");
                LECurrentObject.attr("data-4-width", "3px");
                LECurrentObject.attr("data-5-width", "3px");

                LECurrentObject.attr("data-3-color", "rgb(51, 51, 51)");
                LECurrentObject.attr("data-4-color", "rgb(51, 51, 51)");
                LECurrentObject.attr("data-5-color", "rgb(51, 51, 51)");

                LECurrentObject.attr("data-3-position", "border");
                LECurrentObject.attr("data-4-position", "border");
                LECurrentObject.attr("data-5-position", "border");
                /*
                 为目标对象添加class 每次添加记住class 下次点击先移除 再添加
                 */
                var _$this = $(this);
                var $target = LECurrentObject.find("ul.dropdown-menu");
                var _style = _$this.data("ref");
                $target.removeClass($target.attr("data-prestyle"));
                $target.attr("data-prestyle", _style);
                $target.addClass(_style);
                //LECurrentObject.trigger("click");
                /*设置样式时清除自定义的菜单宽高*/
                //LECurrentObject.find(".nav-pills li ul.dropdown-menu li a div").css({"width":"","height":""});
                /*设置样式时清除自定义的菜单高度和padding*/
                //LECurrentObject.find(".nav-pills li ul.dropdown-menu li a div").css({"height":""});
                //LECurrentObject.find(".nav-pills li ul.dropdown-menu li a").css({"padding-left":"","padding-right":""});
                resetSubBackground();
                resetSubColorStatusNew();
                resetSubAlignStyle();
                resetSubWH();
                resetSubModelPreview();
                LEHistory.trigger();
            });
        };
        /*
         标签栏样式预览 实现方法  如果设置了样式 让预览处的html为按钮的html 否则为第3个标签样式（默认样式）
         */
        function resetSubModelPreview() {
            var $target = LECurrentObject;
            var l = $subModelDiv.find("ul").length;
            var tabClassName = $target.find("ul.dropdown-menu").attr("data-prestyle");
            if (!tabClassName) {
                $subModelPreview.html($subModelDiv.children("ul").eq(2).html());
            } else {
                for (var i = 0; i < l; i++) {
                    var className = $subModelDiv.find("ul").eq(i).attr("data-ref");
                    if (tabClassName == className) {
                        $subModelPreview.html($subModelDiv.children("ul").eq(i).html());
                    }
                }

            }
        }

        /**
         * padding 的初始化值为  左padding 加 右padding 除以2  保证对齐按钮调整左右padding后结果不变
         */
        var resetSubPadding = function () {
            var $target = LECurrentObject.find(".nav-pills li ul.dropdown-menu li a");
            var PdLeft = parseInt($target.css("padding-left"));
            var PdRight = parseInt($target.css("padding-right"));
            var pd = parseInt(PdLeft + PdRight) / 2;
            $subTabPadding.val(pd + "px");
        };

        /*单击普通、悬停、点击三个按钮时，输入框颜色切换，值等于input框对应的data-0、data-1、data-2*/
        var initSubEvent = function () {
            $("#subUl").find("li").bind("click", function () {
                $(this).addClass("activt").siblings().removeClass("activt");
                $subLitxColor.val($subLitxColor.attr("data-" + $(this).index()));
                $subLibgColor.val($subLibgColor.attr("data-" + $(this).index()));
                $subLitxColorPick.spectrum("set", $subLitxColor.attr("data-" + $(this).index()));
                $subLibgColorPick.spectrum("set", $subLibgColor.attr("data-" + $(this).index()));
                //LECurrentObject.trigger("click");
                resetSubBackground();
                resetSubColorStatusNew();
                resetSubAlignStyle();
                resetSubWH();
                resetSubModelPreview();
            });

        };
        /*
         对style标签进行操作 调用了StyleManager对象的setSty方法实现增加或修改styl标签内容
         */
        function changeSubStyle(partSelector, sccName, cssValue) {
            var id = LECurrentObject.attr("id");
            var selector = "#" + id + " .nav-pills li ul.dropdown-menu li " + partSelector;
            StyleManager.setStyle(selector, sccName, cssValue);
        }

        /*
         /*设置字体颜色，区分不同状态的a便签进行设置*/
        var initSubColorEvent = function () {
            $subLitxColorPick.spectrum(
                LEColorPicker.getOptions(function (tinycolor) {
                    var _c = tinycolor ? tinycolor.toRgbaString() : "";
                    var _v = tinycolor ? tinycolor.toHexString() : "#000000";
                    $subLitxColor.val(_v);
                    if ($subMenuNormal.hasClass("activt")) {
                        $subLitxColor.attr("data-0", _v);
                        changeSubStyle("a:not(:hover)", "color", _c);
                    } else if ($subMenuHover.hasClass("activt")) {
                        $subLitxColor.attr("data-1", _v);
                        changeSubStyle("a:hover", "color", _c);
                    } else if ($subMenuClick.hasClass("activt")) {
                        $subLitxColor.attr("data-2", _v);
                        changeSubStyle("a:focus", "color", _c);
                    }
                    LEHistory.trigger();
                })
            );
        };
        /*设置标签背景颜色 区分不同状态的a标签进行设置*/
        var initSubBackground = function () {
            $subLibgColorPick.spectrum(
                LEColorPicker.getOptions(function (tinycolor) {
                    var _c = tinycolor ? tinycolor.toRgbaString() : "";
                    var _v = tinycolor ? tinycolor.toHexString() : "#000000";
                    $subLibgColor.val(_v);
                    if ($subMenuNormal.hasClass("activt")) {
                        $subLibgColor.attr("data-0", _v);
                        changeSubStyle("a:not(:hover)", "background-color", _c);
                    } else if ($subMenuHover.hasClass("activt")) {
                        $subLibgColor.attr("data-1", _v);
                        changeSubStyle("a:hover", "background-color", _c);
                    } else if ($subMenuClick.hasClass("activt")) {
                        $subLibgColor.attr("data-2", _v);
                        changeSubStyle("a:focus", "background-color", _c);
                    }
                    LEHistory.trigger();
                })
            );
        };
        /*修改字体颜色输入框的值,通过正则表达式判断输入的值，格式匹配则更新，否则不更改*/
        var initSubTxChange = function () {
            $subLitxColor.bind("change", function () {
                var reg = /^#([0-9a-fA-F]{3}|[0-9a-fA-F]{6})$/;
                if (reg.test($(this).val())) {
                    if ($subMenuNormal.hasClass("activt")) {
                        $subLitxColor.attr("data-0", $(this).val());
                        changeSubStyle("a:not(:hover)", "color", $(this).val());
                    } else if ($subMenuHover.hasClass("activt")) {
                        $subLitxColor.attr("data-1", $(this).val());
                        changeSubStyle("a:hover", "color", $(this).val());
                    } else if ($subMenuClick.hasClass("activt")) {
                        $subLitxColor.attr("data-2", $(this).val());
                        changeSubStyle("a:focus", "color", $(this).val());
                    }
                    $subLitxColorPick.spectrum("set", $(this).val());
                } else {
                    if ($subMenuNormal.hasClass("activt")) {
                        $subLitxColor.attr("data-0", "#0069D6");
                        $(this).val("#0069D6");
                        changeSubStyle("a:not(:hover)", "color", "#0069D6");
                        $subLitxColorPick.spectrum("set", "#0069D6");
                    } else if ($subMenuHover.hasClass("activt")) {
                        $subLitxColor.attr("data-1", "#0069D6");
                        $(this).val("#0069D6");
                        changeSubStyle("a:hover", "color", "#0069D6");
                        $subLitxColorPick.spectrum("set", "#0069D6");
                    } else if ($subMenuClick.hasClass("activt")) {
                        $subLitxColor.attr("data-2", "#555");
                        $(this).val("#555");
                        changeSubStyle("a:focus", "color", "#555");
                        $subLitxColorPick.spectrum("set", "#555");
                    }
                }
                LEHistory.trigger();
            })
        };
        /*修改背景颜色输入框的值，通过正则表达式过滤，匹配则更新，负责不改变*/
        var initSubBgChange = function () {
            $subLibgColor.bind("change", function () {
                var reg = /^#([0-9a-fA-F]{3}|[0-9a-fA-F]{6})$/;
                if (reg.test($(this).val())) {
                    if ($subMenuNormal.hasClass("activt")) {
                        $subLibgColor.attr("data-0", $(this).val());
                        changeSubStyle("a:not(:hover)", "background-color", $(this).val());
                    } else if ($subMenuHover.hasClass("activt")) {
                        $subLibgColor.attr("data-1", $(this).val());
                        changeSubStyle("a:hover", "background-color", $(this).val());
                    } else if ($subMenuClick.hasClass("activt")) {
                        $subLibgColor.attr("data-2", $(this).val());
                        changeSubStyle("a:focus", "background-color", $(this).val());
                    }
                    $subLibgColorPick.spectrum("set", $(this).val());
                } else {
                    if ($subMenuNormal.hasClass("activt")) {
                        $subLibgColor.attr("data-0", "#ffffff");
                        $(this).val("#ffffff");
                        changeSubStyle("a:not(:hover)", "background-color", "#ffffff");
                        $subLibgColorPick.spectrum("set", "#ffffff");
                    } else if ($subMenuHover.hasClass("activt")) {
                        $subLibgColor.attr("data-1", "#f5f5f5");
                        $(this).val("#f5f5f5");
                        changeSubStyle("a:hover", "background-color", "#f5f5f5");
                        $subLibgColorPick.spectrum("set", "#f5f5f5");
                    } else if ($subMenuClick.hasClass("activt")) {
                        $subLibgColor.attr("data-2", "#ffffff");
                        $(this).val("#ffffff");
                        changeSubStyle("a:focus", "background-color", "#ffffff");
                        $subLibgColorPick.spectrum("set", "#ffffff");
                    }
                }
                LEHistory.trigger();
            })
        };
        /*
         设置标签背景图片
         1、如果没有背景图片，调用添加背景图片模态框
         2、如果有背景图片，调用编辑背景图片模态框
         通过判断“普通”、“悬停”、“点击”三个控制按钮的状态，确定给哪个标签添加或修改背景图
         */
        var initSubBgUpload = function () {
            $("#subBgPreviewImg").click(function (e) {
                var isNew = $("#subBgPreviewImg").attr("src") != "export/images/sliderPanel/sliderPanel1.png";
                if (isNew) {
                    var data = [];
                    data.push($("#subBgPreviewImg").attr("src"));
                    LEDialog.toggleDialog(LE.options["Dialog"].picEditDialog, function (imgList) {
                            if (imgList.length > 0) {
                                if ($subMenuNormal.hasClass("activt")) {
                                    changeSubStyle("a:not(:hover)", "background-image", "url(" + imgList[0].path + ")");
                                } else if ($subMenuHover.hasClass("activt")) {
                                    changeSubStyle("a:hover", "background-image", "url(" + imgList[0].path + ")");
                                } else if ($subMenuClick.hasClass("activt")) {
                                    changeSubStyle("a:focus", "background-image", "url(" + imgList[0].path + ")");
                                }
                                LECurrentObject.find("ul").find("li").find("a").css("backgroundSize", "cover");
                                LECurrentObject.find("ul").find("li").find("a").css("backgroundPosition", "center");
                                //LECurrentObject.trigger("click");
                                LEHistory.trigger();
                                resetSubBackground();
                                resetSubColorStatusNew();
                                resetSubAlignStyle();
                                resetSubWH();
                                resetSubModelPreview();
                            }
                        },
                        data
                    );

                } else {
                    LEDialog.toggleDialog(LE.options["Dialog"].picUploadDialog, function (jsonUploadImg) {
                        var url = jsonUploadImg.imgPath;
                        if ($subMenuNormal.hasClass("activt")) {
                            changeSubStyle("a:not(:hover)", "background-image", "url(" + url + ")");
                        } else if ($subMenuHover.hasClass("activt")) {
                            changeSubStyle("a:hover", "background-image", "url(" + url + ")");

                        } else if ($subMenuClick.hasClass("activt")) {
                            changeSubStyle("a:focus", "background-image", "url(" + url + ")");
                        }
                        LECurrentObject.find("ul").find("li").find("a").css("backgroundSize", "cover");
                        LECurrentObject.find("ul").find("li").find("a").css("backgroundPosition", "center");
                        //LECurrentObject.trigger("click");
                        LEHistory.trigger();
                        resetSubBackground();
                        resetSubColorStatusNew();
                        resetSubAlignStyle();
                        resetSubWH();
                        resetSubModelPreview();
                    });
                }
            });
            $("#subBgFileLabel").hover(function () {
                $.trim($("#subBgPreviewImg").attr("src")) != "export/images/sliderPanel/sliderPanel1.png" && $("#subBgDeleteImg").show();
            }, function () {
                $("#subBgDeleteImg").hide();
            });
        };
        /*
         删除标签栏背景图片：通过判断普通、悬停、点击三个按钮的状态实现对不同状态a标签背景图的删除
         */
        var initDeleteSubImage = function () {
            $("#subBgDeleteImg").click(function () {
                if ($subMenuNormal.hasClass("activt")) {
                    changeSubStyle("a:not(:hover)", "background-image", "none")
                } else if ($subMenuHover.hasClass("activt")) {
                    changeSubStyle("a:hover", "background-image", "none");
                } else if ($subMenuClick.hasClass("activt")) {
                    changeSubStyle("a:focus", "background-image", "none");
                }
                $("#subBgPreviewImg").attr("src", "");
                LEHistory.trigger();
            });
        };
        /*初始化时背景图片预览按钮的状态*/
        var resetSubBackground = function () {
            var target = LECurrentObject.attr("id");

            var imgN = StyleManager.getStyle("#" + target + " .nav-pills li ul.dropdown-menu li a:not(:hover)", "backgroundImage");
            var imgH = StyleManager.getStyle("#" + target + " .nav-pills li ul.dropdown-menu li a:hover", "backgroundImage");
            var imgL = StyleManager.getStyle("#" + target + " .nav-pills li ul.dropdown-menu li a:focus", "backgroundImage");


            // var _imgN = LECurrentObject.find(".nav-pills li ul.dropdown-menu li a:not(:hover)").css("backgroundImage") == "none" ? "export/images/sliderPanel/sliderPanel1.png" : LECurrentObject.find(".nav-pills li ul.dropdown-menu li a:not(:hover)").css("backgroundImage");
            var _imgN = imgN == "none" ? "export/images/sliderPanel/sliderPanel1.png" : StyleManager.getStyle("#" + target + " .nav-pills li ul.dropdown-menu li a:not(:hover)", "backgroundImage");
            var _imgH = imgH == "none" ? "export/images/sliderPanel/sliderPanel1.png" : StyleManager.getStyle("#" + target + " .nav-pills li ul.dropdown-menu li a:hover", "backgroundImage");
            var _imgL = imgL == "none" ? "export/images/sliderPanel/sliderPanel1.png" : StyleManager.getStyle("#" + target + " .nav-pills li ul.dropdown-menu li a:focus", "backgroundImage");

            //alert(_imgN+"---"+_imgN+"----"+_imgL);
            if (_imgN != "") {
                _imgN = _imgN.replace(/url\([\"\']?/g, "").replace(/[\"\']?\)/g, "");
            }
            if (_imgH != "") {
                _imgH = _imgH.replace(/url\([\"\']?/g, "").replace(/[\"\']?\)/g, "");
            }
            if (_imgL != "") {
                _imgL = _imgL.replace(/url\([\"\']?/g, "").replace(/[\"\']?\)/g, "");
            }
            if ($subMenuNormal.hasClass("activt")) {
                _imgN && $("#subBgPreviewImg").attr("src", _imgN);
            } else if ($subMenuHover.hasClass("activt")) {
                _imgH && $("#subBgPreviewImg").attr("src", _imgH);
            } else if ($subMenuClick.hasClass("activt")) {
                _imgL && $("#subBgPreviewImg").attr("src", _imgL);
            }
        };

        function getSubResetColor(HoverA, partMSelector, style, defaultStyle, partOwnerSelector) {
            var target = LECurrentObject.attr("id");

            if (HoverA == "none") {
                if (LECurrentObject.find("ul.dropdown-menu").attr("data-prestyle")) {
                    //2、否则如果样式中存在,当前对象的最外层ul是否具有"data-prestyle"属性 属性值对应class名
                    var dataPrestyle = LECurrentObject.find("ul.dropdown-menu").attr("data-prestyle");
                    HoverA = modelStyleManager.getStyle("." + dataPrestyle + partMSelector, style);
                } else {
                    //3否则默认样式
                    HoverA = defaultStyle;
                }
            } else {
                //1、如果html中存在自定义选择器
                HoverA = StyleManager.getStyle("#" + target + partOwnerSelector, style);
            }
        }

        /*初始化主导航样式的按钮状态 字体及背景颜色
         * 根据样式优先级
         * 1、判断有没有自定义样式，如果有,将style标签中的自定义样式初始化到按钮
         * 2、没有自定义样式 则判断有没有模板样式，如果有，将对应的模板样式初始化到按钮
         * 3、否则 将默认样式初始化到按钮
         * */
        var resetSubColorStatusNew = function () {
            var target = LECurrentObject.attr("id");
            var NormalA = LECurrentObject.find(".nav-pills li ul.dropdown-menu li a").css("color");
            var NormalB = LECurrentObject.find(".nav-pills li ul.dropdown-menu li a").css("background-color");

            var HoverA = StyleManager.getStyle("#" + target + " .nav-pills li ul.dropdown-menu li a:hover", "color");
            var LinkA = StyleManager.getStyle("#" + target + " .nav-pills li ul.dropdown-menu li a:focus", "color");
            var HoverB = StyleManager.getStyle("#" + target + " .nav-pills li ul.dropdown-menu li a:hover", "backgroundColor");
            var LinkB = StyleManager.getStyle("#" + target + " .nav-pills li ul.dropdown-menu li a:focus", "backgroundColor");

            if (HoverA == "none") {
                if (LECurrentObject.find("ul.dropdown-menu").attr("data-prestyle")) {
                    //2、否则如果样式中存在,当前对象的最外层ul是否具有"data-prestyle"属性 属性值对应class名
                    var dataPrestyle = LECurrentObject.find("ul.dropdown-menu").attr("data-prestyle");
                    HoverA = modelStyleManager.getStyle("." + dataPrestyle + " li a:hover", "color");
                } else {
                    //3否则默认样式
                    HoverA = "#262626";
                }
            } else {
                //1、如果html中存在自定义选择器
                HoverA = StyleManager.getStyle("#" + target + " .nav-pills li ul.dropdown-menu li a:hover", "color");
            }

            if (LinkA == "none") {
                if (LECurrentObject.find("ul.dropdown-menu").attr("data-prestyle")) {
                    //2、否则如果样式中存在,当前对象的最外层ul是否具有"data-prestyle"属性 属性值对应class名
                    var dataPrestyle = LECurrentObject.find("ul.dropdown-menu").attr("data-prestyle");
                    LinkA = modelStyleManager.getStyle("." + dataPrestyle + " li a:focus", "color");
                } else {
                    //3否则默认样式
                    LinkA = "#262626";
                }
            } else {
                //1、如果html中存在自定义选择器
                LinkA = StyleManager.getStyle("#" + target + " .nav-pills li ul.dropdown-menu li a:focus", "color");
            }

            if (HoverB == "none") {
                if (LECurrentObject.find("ul.dropdown-menu").attr("data-prestyle")) {
                    //2、否则如果样式中存在,当前对象的最外层ul是否具有"data-prestyle"属性 属性值对应class名
                    var dataPrestyle = LECurrentObject.find("ul.dropdown-menu").attr("data-prestyle");
                    HoverB = modelStyleManager.getStyle("." + dataPrestyle + " li a:hover", "backgroundColor");
                } else {
                    //3否则默认样式
                    HoverB = "#f5f5f5";
                }
            } else {
                //1、如果html中存在自定义选择器
                HoverB = StyleManager.getStyle("#" + target + " .nav-pills li ul.dropdown-menu li a:hover", "backgroundColor");
            }

            if (LinkB == "none") {
                if (LECurrentObject.find("ul.dropdown-menu").attr("data-prestyle")) {
                    //2、否则如果样式中存在,当前对象的最外层ul是否具有"data-prestyle"属性 属性值对应class名
                    var dataPrestyle = LECurrentObject.find("ul.dropdown-menu").attr("data-prestyle");
                    LinkB = modelStyleManager.getStyle("." + dataPrestyle + " li a:focus", "backgroundColor");
                } else {
                    //3否则默认样式
                    LinkB = "#f5f5f5";
                }
            } else {
                //1、如果html中存在自定义选择器
                LinkB = StyleManager.getStyle("#" + target + " .nav-pills li ul.dropdown-menu li a:focus", "backgroundColor");
            }
            $subLitxColor.attr("data-0", NormalA);
            $subLitxColor.attr("data-1", HoverA);
            $subLitxColor.attr("data-2", LinkA);
            $subLibgColor.attr("data-0", NormalB);
            $subLibgColor.attr("data-1", HoverB);
            $subLibgColor.attr("data-2", LinkB);
            if ($subMenuNormal.hasClass("activt")) {
                $subLitxColorPick.spectrum("set", NormalA);
                $subLitxColor.val($subLitxColorPick.spectrum("get").toHexString());
                $subLibgColorPick.spectrum("set", NormalB);
                $subLibgColor.val($subLibgColorPick.spectrum("get").toHexString());
            } else if ($subMenuHover.hasClass("activt")) {
                $subLitxColorPick.spectrum("set", HoverA);
                $subLitxColor.val($subLitxColorPick.spectrum("get").toHexString());
                $subLibgColorPick.spectrum("set", HoverB);
                $subLibgColor.val($subLibgColorPick.spectrum("get").toHexString());
            } else if ($subMenuClick.hasClass("activt")) {
                $subLitxColorPick.spectrum("set", LinkA);
                $subLitxColor.val($subLitxColorPick.spectrum("get").toHexString());
                $subLibgColorPick.spectrum("set", LinkB);
                $subLibgColor.val($subLibgColorPick.spectrum("get").toHexString());
            }
        };
        /*初始化交互样式的按钮状态 字体及背景颜色*/
        var resetSubColorStatus = function () {
            var target = LECurrentObject.attr("id");
            var HoverA = StyleManager.getStyle("#" + target + " .nav-pills li ul.dropdown-menu li a:hover", "color");
            var LinkA = StyleManager.getStyle("#" + target + " .nav-pills li ul.dropdown-menu li a:focus", "color");
            var NormalA = LECurrentObject.find(".nav-pills li ul.dropdown-menu li a").css("color");

            var HoverB = StyleManager.getStyle("#" + target + " .nav-pills li ul.dropdown-menu li a:hover", "backgroundColor");

            var LinkB = StyleManager.getStyle("#" + target + " .nav-pills li ul.dropdown-menu li a:focus", "backgroundColor");
            var NormalB = LECurrentObject.find(".nav-pills li ul.dropdown-menu li a").css("background-color");

            var LinkA = LinkA == "none" ? "#000000" : StyleManager.getStyle("#" + target + " .nav-pills li ul.dropdown-menu li a:focus", "color");
            var LinkB = LinkB == "none" ? "#ffffff" : StyleManager.getStyle("#" + target + " .nav-pills li ul.dropdown-menu li a:focus", "backgroundColor");

            var HoverA = HoverA == "none" ? "#000000" : StyleManager.getStyle("#" + target + " .nav-pills li ul.dropdown-menu li a:hover", "color");
            var HoverB = HoverB == "none" ? "#ffffff" : StyleManager.getStyle("#" + target + " .nav-pills li ul.dropdown-menu li a:hover", "backgroundColor");
            var NormalB = NormalB == "none" ? "#FFFFFF" : LECurrentObject.find(".nav-pills li ul.dropdown-menu li a").css("background-color");
            //alert(NormalA+"-NormalA-"+LinkA+"-LinkA-"+NormalB+"-NormalB-"+LinkB+"-LinkB-");

            $subLitxColor.attr("data-0", NormalA);
            $subLitxColor.attr("data-1", HoverA);
            $subLitxColor.attr("data-2", LinkA);
            $subLibgColor.attr("data-0", NormalB);
            $subLibgColor.attr("data-1", HoverB);
            $subLibgColor.attr("data-2", LinkB);
            if ($subMenuNormal.hasClass("activt")) {
                $subLitxColorPick.spectrum("set", NormalA);
                $subLitxColor.val($subLitxColorPick.spectrum("get").toHexString());
                $subLibgColorPick.spectrum("set", NormalB);
                $subLibgColor.val($subLibgColorPick.spectrum("get").toHexString());
            } else if ($subMenuHover.hasClass("activt")) {
                $subLitxColorPick.spectrum("set", HoverA);
                $subLitxColor.val($subLitxColorPick.spectrum("get").toHexString());
                $subLibgColorPick.spectrum("set", HoverB);
                $subLibgColor.val($subLibgColorPick.spectrum("get").toHexString());
            } else if ($subMenuClick.hasClass("activt")) {
                $subLitxColorPick.spectrum("set", LinkA);
                $subLitxColor.val($subLitxColorPick.spectrum("get").toHexString());
                $subLibgColorPick.spectrum("set", LinkB);
                $subLibgColor.val($subLibgColorPick.spectrum("get").toHexString());
            }
        };
        /*初始化 对齐按钮的事件，当点击按钮的时候，设置当前对象的 text-align*/
        var initSubAlignEvent = function () {
            $subAlignUl.find("li").bind("click", function (e) {
                var _$this = $(this);  //点击的设置对齐方式的按钮
                var $target = LECurrentObject.find(".nav-pills li ul.dropdown-menu li a div"); //获取当前对象
                $target.css("text-align", _$this.attr("data-align"));
                LEHistory.trigger();
            });
        };
        /*初始化 对齐按钮的事件，当点击按钮的时候，设置当前对象的 padding-left padding-right*/
        var initSubAlignByPadding = function () {
            $subAlignUl.find("li").bind("click", function (e) {
                var _$this = $(this);  //点击的设置对齐方式的按钮
                var $target = LECurrentObject.find(".nav-pills li ul.dropdown-menu li a"); //获取当前对象
                var paddingValue = parseInt($subTabPadding.val());
                if (_$this.attr("data-align") == "left") {
                    $target.css({"padding-left": "0", "padding-right": paddingValue * 2 + "px"});
                    $target.css({"text-align": "-webkit-left"});
                    //$target.css({"text-align":"left"});
                } else if (_$this.attr("data-align") == "right") {
                    $target.css({"padding-left": paddingValue * 2 + "px", "padding-right": "0"});
                    $target.css({"text-align": "-webkit-right"});
                    //$target.css({"text-align":"right"});
                } else {
                    $target.css({"padding-left": paddingValue + "px", "padding-right": paddingValue + "px"});
                    $target.css({"text-align": "-webkit-center"});
                    //$target.css({"text-align":"center"});
                }
                LEHistory.trigger();
            });
        };
        //获得当前对象的对齐值，并且初始化到按钮上面
        var resetSubAlignStyle = function () {
            $subAlignUl.find("li").removeClass("select selected");
            var $target = LECurrentObject.find(".nav-pills li ul.dropdown-menu li a div");
            var _align = $target.css("text-align") || "left";
            $subAlignUl.find("li[data-align=" + _align + "]").addClass("select selected");
        };
        //对比当前对象的padding值，并且初始化到对齐按钮上面
        var resetSubAlignByPadding = function () {
            $subAlignUl.find("li").removeClass("select selected");
            var $target = LECurrentObject.find(".nav-pills li ul.dropdown-menu li a"); //获取当前对象
            var PdLeft = parseInt($target.css("padding-left"));                              //获取当前对象的左padding
            var PdRight = parseInt($target.css("padding-right"));
            if (PdLeft < PdRight) {
                $subAlignUl.find("li[data-align='left']").addClass("select selected");
            } else if (PdLeft > PdRight) {
                $subAlignUl.find("li[data-align='right']").addClass("select selected");
            } else {
                $subAlignUl.find("li[data-align='center']").addClass("select selected");
            }
        };
        var initSubWEvent = function () {
            $subTabWH.onlyNum().keydown(function (e) {
                LEKey.numInputKeyDown(e, $(this), LECurrentObject.find(".nav-pills li ul.dropdown-menu li a div"));
            });
            //失去焦点的时候，置0
            $subTabWH.blur(function (e) {
                LEKey.numInputBlur(e, $(this), LECurrentObject.find(".nav-pills li ul.dropdown-menu li a div"));
                LEHistory.trigger();
            });

            $subTabWH.focus(function (e) {
                LEKey.numInputFocus(e, $(this), LECurrentObject.find(".nav-pills li ul.dropdown-menu li a div"));
            });
        };
        /*
         * 菜单内边距 padding-left padding-right*/
        var initSubPaddingEvent = function () {
            $subTabPadding.onlyNum().keydown(function (e) {
                LE.stylesheets["MainMenu"]().numInputKeyDownPad(e, $(this), LECurrentObject.find(".nav-pills li ul.dropdown-menu li a"));
                //设置完padding值之后初始化对齐状态
                resetSubAlignByPadding();
            });
            //失去焦点的时候，置0
            $subTabPadding.blur(function (e) {
                LE.stylesheets["MainMenu"]().numInputBlurPad(e, $(this), LECurrentObject.find(".nav-pills li ul.dropdown-menu li a"));
                resetSubAlignByPadding();
                LEHistory.trigger();
            });

            $subTabPadding.focus(function (e) {
                LE.stylesheets["MainMenu"]().numInputFocusPad(e, $(this), LECurrentObject.find(".nav-pills li ul.dropdown-menu li a"));
            });
        };

        var resetSubWH = function () {
            $subTabWH.each(function () {
                var $this = $(this);
                var $target = LECurrentObject.find(".nav-pills li ul.dropdown-menu li a div");
                var _ref = $this.data("ref");
                $this.val(parseInt($target.css(_ref)) + "px");
            });
        };

        /**
         * guzm
         */
        var initSubMenuBasicMain = function () {
            $("#subMenuBasicMain").click(function () {
                var _t = [];
                _t.push('MainMenu');
                _t.push('SubMenu');
                _t.push('NavManage');

                var options = {
                    object: LECurrentObject.children("ul").children("li").children("a"),
                    target: _t
                };
                $("#sidebar-panel").find("section").hide();
                LEStyle.run("Back", options, false).run("Position", options, false, true).run("BolderSetting", options, false, true).run("TextSetting", options, false, true);
            });
        };

        return {
            //onload
            init: function () {
                initSubEvent();
                initSubColorEvent();
                initSubBackground();
                //initSubTxChange(); //文字颜色input框交互
                //initSubBgChange(); //背景颜色input框交互
                initSubBgUpload();
                initDeleteSubImage();
                //initSubAlignEvent();
                initSubWEvent();
                initSubModelShow();
                initSubModelPreview();
                initChangeSubStyleBlock();
                initSubMenuBasicMain();
                initSubPaddingEvent();
                initSubAlignByPadding();
            },
            run: function (options, doHide, doSlide) {
                if (LECurrentObject.children().children().find("ul").children().length > 0) {
                    resetSubBackground();
                    resetSubColorStatusNew();
                    //resetSubColorStatus();
                    //resetSubAlignStyle();
                    resetSubWH();
                    resetSubModelPreview();
                    resetSubPadding();
                    resetSubAlignByPadding();
                    //切换组件时，样式设置部分回顶部
                    sliderbarToTop();
                    //LEDisplay.show($PS, doHide, doSlide);
                }

            },
            destroy: function () {
               // $PS.hide();
                $subModelGoBack.animate({right: "-250px"}, "fast", "linear");
                $subModelDiv.animate({right: "-250px"}, "fast", "linear");
            }
        };
    };
})(window, jQuery, LE, undefined);