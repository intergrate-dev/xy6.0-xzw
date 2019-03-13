/**
 * Created by isaac_gu on 2016/1/14.
 */
(function (window, $, LE) {
    LE.stylesheets["ActiveSetting"] = function () {
        var $panel = $("#activeSection");
        var $tabLitxColor = $("#tabLitxColor");
        var $tabLibgColor = $("#tabLibgColor");
        var $modelDiv = $("#modelDiv");
        var $tabLitxColorPick = $("#tabLitxColorPick");
        var $tabLibgColorPick = $("#tabLibgColorPick");
        var $modelPreview = $("#modelPreview");
        var $modelGoBack = $("#modelGoBack");
        var $tabNormal = $("#tabNormal");
        var $tabHover = $("#tabHover");
        var $tabClick = $("#tabClick");
        var $tabBgPreviewImg = $("#tabBgPreviewImg");

        /*
         对style标签进行操作 调用了StyleManager对象的setSty方法实现增加或修改styl标签内容
         */
        function changeTabStyle(partSelector, sccName, cssValue) {
            var id = LECurrentObject.attr("id");
            var selector = '#' + id + ' ' + partSelector;
            StyleManager.setStyle(selector, sccName, cssValue);
        }

        /*标签栏更多样式面板出现消失*/
        var initMoreModelShow = function () {
            /*$("#modelMore")*/
            $modelPreview.bind("click", function () {
                $modelGoBack.animate({right: "0"}, "fast", "linear");
                $modelDiv.animate({right: "0"}, "fast", "linear");
            });
            $modelGoBack.bind("click", function () {
                $modelGoBack.animate({right: "-250px"}, "fast", "linear");
                $modelDiv.animate({right: "-250px"}, "fast", "linear");
            });
        };

        /*字体超链接的初始化颜色 蓝色*/
        var initDefaultColor = function () {
            $tabLitxColorPick.spectrum("set", "#0069D6");
        };

        /*单击普通、悬停、点击三个按钮时，输入框颜色切换，值等于input框对应的data-0、data-1、data-2*/
        var initTabEvent = function () {
            $("#tabUl").find("li").bind("click", function () {
                $(this).addClass("activt").siblings().removeClass("activt");
                $tabLitxColor.val($tabLitxColor.attr("data-" + $(this).index()));
                $tabLibgColor.val($tabLibgColor.attr("data-" + $(this).index()));
                $tabLitxColorPick.spectrum("set", $tabLitxColor.attr("data-" + $(this).index()));
                $tabLibgColorPick.spectrum("set", $tabLibgColor.attr("data-" + $(this).index()));
                LECurrentObject.trigger("click");
            });
        };

        /*修改字体颜色输入框的值,通过正则表达式判断输入的值，格式匹配则更新，否则不更改*/
        var initTxcInputChange = function () {
            $tabLitxColor.bind("change", function () {
                var reg = /^#([0-9a-fA-F]{3}|[0-9a-fA-F]{6})$/;
                if (reg.test($(this).val())) {
                    if ($tabNormal.hasClass("activt")) {
                        $tabLitxColor.attr("data-0", $(this).val());
                        changeTabStyle('.ui-tabs-nav a', 'color', $(this).val());
                    } else if ($tabHover.hasClass("activt")) {
                        $tabLitxColor.attr("data-1", $(this).val());
                        changeTabStyle('.ui-state-hover a:hover', 'color', $(this).val());
                    } else if ($tabClick.hasClass("activt")) {
                        $tabLitxColor.attr("data-2", $(this).val());
                        changeTabStyle('.ui-tabs-active a:link', 'color', $(this).val());
                    }
                    $tabLitxColorPick.spectrum("set", $(this).val());
                } else {
                    if ($tabNormal.hasClass("activt")) {
                        $tabLitxColor.attr("data-0", "#0069D6");
                        $(this).val("#0069D6");
                        changeTabStyle('.ui-tabs-nav a', 'color', "#0069D6");
                        $tabLitxColorPick.spectrum("set", "#0069D6");
                    } else if ($tabHover.hasClass("activt")) {
                        $tabLitxColor.attr("data-1", "#0069D6");
                        $(this).val("#0069D6");
                        changeTabStyle('.ui-state-hover a:hover', 'color', "#0069D6");
                        $tabLitxColorPick.spectrum("set", "#0069D6");
                    } else if ($tabClick.hasClass("activt")) {
                        $tabLitxColor.attr("data-2", "#555");
                        $(this).val("#555");
                        changeTabStyle('.ui-tabs-active a:link', 'color', "#555");
                        $tabLitxColorPick.spectrum("set", "#555");
                    }
                }
                LEHistory.trigger();
            });
        };

        /*修改背景颜色输入框的值，通过正则表达式过滤，匹配则更新，否则不改变*/
        var initBgInputChange = function () {
            $tabLibgColor.bind("change", function () {
                var reg = /^#([0-9a-fA-F]{3}|[0-9a-fA-F]{6})$/;
                if (reg.test($(this).val())) {
                    if ($tabNormal.hasClass("activt")) {
                        $tabLibgColor.attr("data-0", $(this).val());
                        changeTabStyle('.ui-tabs-nav a', 'background-color', $(this).val());
                    } else if ($tabHover.hasClass("activt")) {
                        $tabLibgColor.attr("data-1", $(this).val());
                        changeTabStyle('.ui-state-hover a:hover', 'background-color', $(this).val());
                    } else if ($tabClick.hasClass("activt")) {
                        $tabLibgColor.attr("data-2", $(this).val());
                        changeTabStyle('.ui-tabs-active a:link', 'background-color', $(this).val());
                    }
                    $tabLibgColorPick.spectrum("set", $(this).val());
                } else {
                    if ($tabNormal.hasClass("activt")) {
                        $tabLibgColor.attr("data-0", "#ffffff");
                        $(this).val("#ffffff");
                        changeTabStyle('.ui-tabs-nav a', 'background-color', "#ffffff");
                        $tabLibgColorPick.spectrum("set", "#ffffff");
                    } else if ($tabHover.hasClass("activt")) {
                        $tabLibgColor.attr("data-1", "#f5f5f5");
                        $(this).val("#f5f5f5");
                        changeTabStyle('.ui-state-hover a:hover', 'background-color', "#f5f5f5");
                        $tabLibgColorPick.spectrum("set", "#f5f5f5");
                    } else if ($tabClick.hasClass("activt")) {
                        $tabLibgColor.attr("data-2", "#ffffff");
                        $(this).val("#ffffff");
                        changeTabStyle('.ui-tabs-active a:link', 'background-color', "#ffffff");
                        $tabLibgColorPick.spectrum("set", "#ffffff");
                    }
                }
                LEHistory.trigger();
            });
        };

        /*设置字体颜色，区分不同状态的a便签进行设置*/
        var initcolorEvent = function () {
            $tabLitxColorPick.spectrum(
                LEColorPicker.getOptions(function (tinycolor) {
                    var _c = tinycolor ? tinycolor.toRgbaString() : "";
                    var _v = tinycolor ? tinycolor.toHexString() : "#000000";

                    $tabLitxColor.val(_v);
                    if ($tabNormal.hasClass("activt")) {
                        $tabLitxColor.attr("data-0", _v);
                        changeTabStyle('.ui-tabs-nav a', 'color', _c)
                    } else if ($tabHover.hasClass("activt")) {
                        $tabLitxColor.attr("data-1", _v);
                        changeTabStyle('.ui-state-hover a:hover', 'color', _c)
                    } else if ($tabClick.hasClass("activt")) {
                        $tabLitxColor.attr("data-2", _v);
                        changeTabStyle('.ui-tabs-active a:link', "color", _c)
                    }
                    LEHistory.trigger();
                })
            );
        };

        /*设置标签背景颜色 区分不同状态的a便签进行设置*/
        var initbackgroundEvent = function () {
            $tabLibgColorPick.spectrum(
                LEColorPicker.getOptions(function (tinycolor) {

                    var _c = tinycolor ? tinycolor.toRgbaString() : "";
                    var _v = tinycolor ? tinycolor.toHexString() : "#000000";

                    $tabLibgColor.val(_v);
                    if ($tabNormal.hasClass("activt")) {
                        $tabLibgColor.attr("data-0", _v);
                        changeTabStyle('.ui-tabs-nav a', 'background-color', _c)
                    } else if ($tabHover.hasClass("activt")) {
                        $tabLibgColor.attr("data-1", _v);
                        changeTabStyle('.ui-state-hover a:hover', 'background-color', _c)
                    } else if ($tabClick.hasClass("activt")) {
                        $tabLibgColor.attr("data-2", _v);
                        changeTabStyle('.ui-tabs-active a:link', 'background-color', _c)
                    }
                    LEHistory.trigger();
                })
            );
        };

        /*
         设置标签背景图片
         1、如果没有背景图片，调用添加背景图片模态框
         2、如果有背景图片，调用编辑背景图片模态框
         通过判断“普通”、“悬停”、“点击”三个控制按钮的状态，确定给哪个标签添加或修改背景图
         */
        var initTabBgUpload = function () {
            $tabBgPreviewImg.click(function (e) {
                var isNew = $tabBgPreviewImg.attr("src") != "export/images/sliderPanel/sliderPanel1.png";
                if (isNew) {
                    var data = [];
                    data.push($tabBgPreviewImg.attr("src"));
                    LEDialog.toggleDialog(LE.options["Dialog"].picEditDialog, function (imgList) {
                            if (imgList.length > 0) {
                                if ($tabNormal.hasClass("activt")) {
                                    changeTabStyle('ul[id^="le_Tabs_"] li[aria-selected="false"] a', 'background-image', "url(" + imgList[0].path + ")");
                                } else if ($tabHover.hasClass("activt")) {
                                    changeTabStyle('ul[id^="le_Tabs_"] li.ui-state-hover a', 'background-image', "url(" + imgList[0].path + ")");
                                } else if ($tabClick.hasClass("activt")) {
                                    changeTabStyle('ul[id^="le_Tabs_"] li[aria-selected="true"] a', 'background-image', "url(" + imgList[0].path + ")");
                                }
                                LECurrentObject.find("ul").find("li").find("a").css("backgroundSize", "cover");
                                LECurrentObject.find("ul").find("li").find("a").css("backgroundPosition", "center");
                                LEHistory.trigger();
                                LECurrentObject.trigger("click");
                            }
                        },
                        data
                    );
                } else {
                    LEDialog.toggleDialog(LE.options["Dialog"].picUploadDialog, function (jsonUploadImg) {
                        var url = jsonUploadImg.imgPath;
                        var $target = LECurrentObject;
                        if ($tabNormal.hasClass("activt")) {
                            changeTabStyle('ul[id^="le_Tabs_"] li[aria-selected="false"] a', 'background-image', "url(" + url + ")");
                        } else if ($tabHover.hasClass("activt")) {
                            changeTabStyle('ul[id^="le_Tabs_"] li.ui-state-hover a', 'background-image', "url(" + url + ")");
                        } else if ($tabClick.hasClass("activt")) {
                            changeTabStyle('ul[id^="le_Tabs_"] li[aria-selected="true"] a', "background-image", "url(" + url + ")");
                        }
                        LECurrentObject.find("ul").find("li").find("a").css("backgroundSize", "cover");
                        LECurrentObject.find("ul").find("li").find("a").css("backgroundPosition", "center");
                        LEHistory.trigger();
                        LECurrentObject.trigger("click");
                    });
                }


            });
            $("#tabBgFileLabel").hover(function () {
                $.trim($tabBgPreviewImg.attr("src")) != "export/images/sliderPanel/sliderPanel1.png" && $("#tabBgDeleteImg").show();
            }, function () {
                $("#tabBgDeleteImg").hide();
            });
        };

        /*
         删除标签栏背景图片：通过判断普通、悬停、点击三个按钮的状态实现对不同状态a标签背景图的删除
         */
        var initDeleteTabImage = function () {
            $("#tabBgDeleteImg").click(function () {
                if ($tabNormal.hasClass("activt")) {
                    changeTabStyle('ul[id^="le_Tabs_"] li[aria-selected="false"] a', 'background-image', "none");
                } else if ($tabHover.hasClass("activt")) {
                    changeTabStyle('ul[id^="le_Tabs_"] li.ui-state-hover a', 'background-image', "none");
                } else if ($tabClick.hasClass("activt")) {
                    changeTabStyle('ul[id^="le_Tabs_"] li[aria-selected="true"] a', "background-image", "none");
                }
                $tabBgPreviewImg.attr("src", "");
                LEHistory.trigger();

            });
        };
        /*
         点击标签栏模板按钮的同时，更新样式预览处的html。
         */
        function initModelPreview() {
            $modelDiv.find("ul").bind("click", function () {
                $modelPreview.html($(this).html());
                /*$(this).prop("outerHTML")*/
            })
        }

        /*
         标签栏样式预览 实现方法  如果设置了样式 让预览处的html为按钮的html 否则为第3个标签样式（默认样式）
         */
        function resetModelPreview() {
            var $target = LECurrentObject;
            var l = $modelDiv.find("ul").length;
            var tabClassName = $target.find("ul[id^='le_Tabs_']").attr("data-prestyle");
            if (!tabClassName) {
                $modelPreview.html($modelDiv.children("ul").eq(2).html());
            } else {
                for (var i = 0; i < l; i++) {
                    var className = $modelDiv.find("ul").eq(i).attr("data-ref");
                    if (tabClassName == className) {
                        $modelPreview.html($modelDiv.children("ul").eq(i).html());
                    }
                }

            }
        }

        /*初始化标签样式的按钮状态 字体及背景颜色
         * 根据样式优先级
         * 1、判断有没有自定义样式，如果有,将style标签中的自定义样式初始化到按钮
         * 2、没有自定义样式 则判断有没有模板样式，如果有，将对应的模板样式初始化到按钮
         * 3、否则 将默认样式初始化到按钮
         * */
        var resetActColorStatus = function () {
            var target = LECurrentObject.attr("id");
            var NormalA = LECurrentObject.find("ul[id^='le_Tabs_'] li[aria-selected='false'] a").css("color");
            var LinkA = LECurrentObject.find("ul[id^='le_Tabs_'] li[aria-selected='true'] a").css("color");
            var NormalB = LECurrentObject.find("ul[id^='le_Tabs_'] li[aria-selected='false'] a").css("background-color");
            var LinkB = LECurrentObject.find("ul[id^='le_Tabs_'] li[aria-selected='true'] a").css("background-color");

             NormalA = !NormalA ? $("#tabLitxColor").attr("data-0") : LECurrentObject.find("ul[id^='le_Tabs_'] li[aria-selected='false'] a").css("color");
             NormalB = !NormalB ? $("#tabLibgColor").attr("data-0") : LECurrentObject.find("ul[id^='le_Tabs_'] li[aria-selected='false'] a").css("background-color");

            var HoverA = StyleManager.getStyle("#" + target + " .ui-state-hover a:hover", "color");
            var HoverB = StyleManager.getStyle("#" + target + " .ui-state-hover a:hover", "backgroundColor");
            if (HoverA == "none") {
                if (LECurrentObject.find("ul[id^='le_Tabs_']").attr("data-prestyle")) {
                    //2、否则如果样式中存在,当前对象的最外层ul是否具有"data-prestyle"属性 属性值对应class名
                    var dataPrestyle = LECurrentObject.find("ul[id^='le_Tabs_']").attr("data-prestyle");
                    HoverA = modelStyleManager.getStyle(".ui-tabs ." + dataPrestyle + " > li.ui-state-hover > a", "color");
                } else {
                    //3否则默认样式
                    HoverA = "#0069D6";
                }
            } else {
                //1、如果html中存在自定义选择器
                HoverA = StyleManager.getStyle("#" + target + " .ui-state-hover a:hover", "color");
            }

            if (HoverB == "none") {
                if (LECurrentObject.find("ul[id^='le_Tabs_']").attr("data-prestyle")) {
                    //2、否则如果样式中存在,当前对象的最外层ul是否具有"data-prestyle"属性 属性值对应class名
                    var dataPrestyle = LECurrentObject.find("ul[id^='le_Tabs_']").attr("data-prestyle");
                    HoverB = modelStyleManager.getStyle(".ui-tabs ." + dataPrestyle + " > li.ui-state-hover > a", "backgroundColor");
                } else {
                    //3否则默认样式
                    HoverB = "#f5f5f5";
                }
            } else {
                //1、如果html中存在自定义选择器
                HoverB = StyleManager.getStyle("#" + target + " .ui-state-hover a:hover", "backgroundColor");
            }

            $tabLitxColor.attr("data-0", NormalA);
            $tabLitxColor.attr("data-1", HoverA);
            $tabLitxColor.attr("data-2", LinkA);
            $tabLibgColor.attr("data-0", NormalB);
            $tabLibgColor.attr("data-1", HoverB);
            $tabLibgColor.attr("data-2", LinkB);

            if ($tabNormal.hasClass("activt")) {
                $tabLitxColorPick.spectrum("set", NormalA);
                $tabLitxColor.val($tabLitxColorPick.spectrum("get").toHexString());
                $tabLibgColorPick.spectrum("set", NormalB);
                $tabLibgColor.val($tabLibgColorPick.spectrum("get").toHexString());
            } else if ($tabHover.hasClass("activt")) {
                $tabLitxColorPick.spectrum("set", HoverA);
                $tabLitxColor.val($tabLitxColorPick.spectrum("get").toHexString());
                $tabLibgColorPick.spectrum("set", HoverB);
                $tabLibgColor.val($tabLibgColorPick.spectrum("get").toHexString());
            } else if ($tabClick.hasClass("activt")) {
                $tabLitxColorPick.spectrum("set", LinkA);
                $tabLitxColor.val($tabLitxColorPick.spectrum("get").toHexString());
                $tabLibgColorPick.spectrum("set", LinkB);
                $tabLibgColor.val($tabLibgColorPick.spectrum("get").toHexString());
            }
        };
        /*初始化时背景图片预览按钮的状态
         普通a标签、点击状态a标签的背景直接通过css属性读取，hover状态a标签的背景图从style标签中读取*/
        var resetTabBackground = function () {
            var target = LECurrentObject.attr("id");
            var _imgH = StyleManager.getStyle('#' + target + ' ul[id^="le_Tabs_"] li.ui-state-hover a', "backgroundImage");
            var _imgH = _imgH == "none" ? "export/images/sliderPanel/sliderPanel1.png" : StyleManager.getStyle('#' + target + ' ul[id^="le_Tabs_"] li.ui-state-hover a', "backgroundImage");

            var _imgN = LECurrentObject.find("ul[id^='le_Tabs_'] li[aria-selected='false'] a").css("backgroundImage") == "none" ? "export/images/sliderPanel/sliderPanel1.png" : LECurrentObject.find("ul[id^='le_Tabs_'] li[aria-selected='false'] a").css("backgroundImage");
            var _imgL = LECurrentObject.find("ul[id^='le_Tabs_'] li[aria-selected='true'] a").css("backgroundImage") == "none" ? "export/images/sliderPanel/sliderPanel1.png" : LECurrentObject.find("ul[id^='le_Tabs_'] li[aria-selected='true'] a").css("backgroundImage");
            if (_imgN) {
                _imgN = _imgN.replace(/url\([\"\']?/g, "").replace(/[\"\']?\)/g, "");
            }
            if (_imgH) {
                _imgH = _imgH.replace(/url\([\"\']?/g, "").replace(/[\"\']?\)/g, "");
            }
            if (_imgL) {
                _imgL = _imgL.replace(/url\([\"\']?/g, "").replace(/[\"\']?\)/g, "");
            }
            if ($tabNormal.hasClass("activt")) {
                _imgN && $tabBgPreviewImg.attr("src", _imgN);
            } else if ($tabHover.hasClass("activt")) {
                _imgH && $tabBgPreviewImg.attr("src", _imgH);
            } else if ($tabClick.hasClass("activt")) {
                _imgL && $tabBgPreviewImg.attr("src", _imgL);
            }
        };
        return {
            init: function () {
               // initTabEvent();
               // initTxcInputChange();
                //initBgInputChange();
                //initcolorEvent();
                //initbackgroundEvent();
                //initDefaultColor();
                initMoreModelShow();
               // initTabBgUpload();
                initModelPreview();
               // initDeleteTabImage();

                //console.info("PageSetting init")
            },
            run: function (options, doHide) {
               // resetActColorStatus();
                resetModelPreview();
               // resetTabBackground();
                //切换组件时，样式设置部分回顶部
                sliderbarToTop();

              //  LEDisplay.show($panel, doHide);
            },
            destroy: function () {
                //console.info("PageSetting destroy")
                $panel.hide();
            }
        };
    };
})(window, jQuery, LE, undefined);