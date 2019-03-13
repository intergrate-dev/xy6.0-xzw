/**
 * Created by isaac_gu on 2016/1/14.
 */
(function (window, $, LE) {
    LE.stylesheets["MainMenu"] = function () {
        //当前功能面板的对象
        var $PS = $("#mainMenuSection");
        var $mainLitxColor = $("#mainLitxColor");
        var $mainLibgColor = $("#mainLibgColor");
        var $mainModelDiv = $("#mainModelDiv");
        var $mainModelPreview = $("#mainModelPreview");
        var $mainMenuNormal = $("#mainMenuNormal");
        var $mainMenuHover = $("#mainMenuHover");
        var $mainMenuClick = $("#mainMenuClick");
        var $mainLitxColorPick = $("#mainLitxColorPick");
        var $mainLibgColorPick = $("#mainLibgColorPick");
        var $nmTabs = $("#nmTabs");
        var $mainModelGoBack = $("#mainModelGoBack");
        var $mainBgPreviewImg = $("#mainBgPreviewImg");
        var $mainAlignUl = $("#mainAlignUl");
        var $mainBoxWH = $(".mainBoxWH");
        var $mmTabPadding = $("#mmTabPadding");
        var $mmTabMargin = $("#mmTabMargin");

        /*
         主导航拖拽排序
         */
        var initDragMainMenu = function () {

            $nmTabs.on({"mouseover": function () {
                $nmTabs.sortable({
                    items: ">li",
                    axis: "y",
                    //connectWith: ".dropdown-ul",
                    //revert: true,
                    //opacity: 0.5,
                    stop: function () {
                        //导航id存为json数组 [{主1li：id，子2ul：[{li:id},{li:id}]},{主2li：id，子2ul：[{li:id},{li:id}]}]
                        var mainLen = $("#nmTabs").children().length;
                        var sublen = [];
                        for (var i = 0; i < mainLen; i++) {
                            var subLenEach = $("#nmTabs").children().eq(i).find("li").length;
                            sublen.push(subLenEach);
                        }
                        var JsonId = [];
                        var JsonIdSon;
                        for (var j = 0; j < mainLen; j++) {
                            var subIdChild = [];
                            var subIdChildSon;
                            for (var k = 0; k < sublen[j]; k++) {
                                subIdChildSon = {
                                    "subSonId": $nmTabs.children().eq(j).children().find("li").eq(k).attr("data-refid")
                                };
                                subIdChild[k] = subIdChildSon;
                            }
                            JsonIdSon = {
                                "mainId": $nmTabs.children("li").eq(j).attr("data-refid"),
                                "subId": subIdChild
                            };
                            JsonId[j] = JsonIdSon;
                        }
                        //console.info(JsonId);
                        //Map方式存储主导航li的outerHTML  $(".test").prop("outerHTML");
                        var MainHtmlMap = new Map();
                        LECurrentObject.children("ul").children("li").each(function () {
                            var thisK = $(this).children("a").attr("id");
                            var thisV = $(this).prop("outerHTML");
                            MainHtmlMap.put(thisK, thisV);
                        });
                        //Map方式存储子导航li的outerHTML
                        var SubHtmlMap = new Map();
                        LECurrentObject.find("ul.dropdown-menu").children("li").each(function () {
                            var thisK = $(this).children("a").attr("id");
                            var thisV = $(this).prop("outerHTML");
                            SubHtmlMap.put(thisK, thisV);
                        });

                        //按照json的id顺序重写主导航html 实现主导航顺序与拖拽后一致
                        var new_mainHtml = "";
                        var new_mainHtml_son = "";
                        for (var m = 0; m < mainLen; m++) {
                            new_mainHtml_son = MainHtmlMap.get(JsonId[m].mainId);
                            new_mainHtml += new_mainHtml_son;
                        }
                        LECurrentObject.children("ul").html(new_mainHtml);

                        //子导航html存入数组
                        var new_subHtml_list = [];
                        for (var l = 0; l < mainLen; l++) {
                            var new_subHtml = "";
                            var new_subHtml_son = "";
                            var subSonLen = JsonId[l].subId.length;
                            for (var n = 0; n < subSonLen; n++) {
                                new_subHtml_son = SubHtmlMap.get(JsonId[l].subId[n].subSonId);
                                new_subHtml += new_subHtml_son;
                            }
                            new_subHtml_list.push(new_subHtml);
                        }

                        //为子导航ul写入数组中对应的html
                        LECurrentObject.find("ul.dropdown-menu").each(function () {
                            var $this = $(this);
                            var index = $this.parent().index();
                            $this.html(new_subHtml_list[index]);
                            //主导航菜单没有二级菜单时 去掉该菜单的属性
                            if ($this.children().length == 0) {
                                $this.parent().removeClass("dropdown-ul");
                                $this.prev("a").children("div").find("span").remove();
                                $this.prev("a").removeClass("dropdown-toggle");
                                $this.prev("a").attr("data-toggle", "");
                                $this.prev("a").attr("role", "");
                                $this.prev("a").attr("aria-haspopup", "");
                                $this.prev("a").attr("aria-expanded", "");
                                $this.remove();
                            }
                        })
                    }

                })
            }}, ">li");

            $nmTabs.on({"mouseover": function () {
                $nmTabs.sortable({
                    items: ".dropdown li"
                })
            }}, ".dropdown >li");

            $nmTabs.on({"dblclick": function () {
                $nmTabs.sortable({
                    disabled: true
                });
            }}, ">li");

            $nmTabs.on({"mouseover": function () {
                $nmTabs.sortable({
                    disabled: false
                });
            }}, ">li");

            $nmTabs.on({"dblclick": function () {
                $nmTabs.sortable({
                    disabled: true
                });
            }}, ".dropdown >li");

            $nmTabs.on({"mouseover": function () {
                $nmTabs.sortable({
                    disabled: false
                });
            }}, ".dropdown >li");
        };

        /*标签栏更多样式面板出现消失*/
        var initMainModelShow = function () {
            /*$("#mainModelMore")*/
            $mainModelPreview.bind("click", function () {
                $mainModelGoBack.animate({right: "0"}, "fast", "linear");
                $mainModelDiv.animate({right: "0"}, "fast", "linear");
            });
            $mainModelGoBack.bind("click", function () {
                $mainModelGoBack.animate({right: "-250px"}, "fast", "linear");
                $mainModelDiv.animate({right: "-250px"}, "fast", "linear");
            });
        };
        /*
         点击标签栏模板按钮的同时，更新样式预览处的html。
         */
        function initMainModelPreview() {
            $mainModelDiv.find("ul").bind("click", function () {
               // $mainModelPreview.html($(this).html());
                $mainModelPreview.html($(this).prop("outerHTML"));
                $mainModelPreview.children("ul").removeClass("displayStyle").removeClass("box-shadow").removeClass("mgt10");
            })
        }

        /*
         点击标签样式按钮
         为目标对象添加class 同时去掉<style>标签中的样式 每次添加记住class 下次点击先移除 再添加
         */
        var initChangeMainStyleBlock = function () {
            $mainModelDiv.find(".displayStyle").click(function (e) {
                var tarId = LECurrentObject.attr("id");
                var removeId = "#" + tarId + " .nav-pills > li";
                //清除style标签中的内容
                StyleManager.removeStyle(removeId);

                //主导航border相关data-样式还原
                LECurrentObject.attr("data-0-radius", "0px");
                LECurrentObject.attr("data-1-radius", "0px");
                LECurrentObject.attr("data-2-radius", "0px");

                LECurrentObject.attr("data-0-style", "none");
                LECurrentObject.attr("data-1-style", "none");
                LECurrentObject.attr("data-2-style", "none");

                LECurrentObject.attr("data-0-width", "3px");
                LECurrentObject.attr("data-1-width", "3px");
                LECurrentObject.attr("data-2-width", "3px");

                LECurrentObject.attr("data-0-color", "rgb(51, 51, 51)");
                LECurrentObject.attr("data-1-color", "rgb(51, 51, 51)");
                LECurrentObject.attr("data-2-color", "rgb(51, 51, 51)");

                LECurrentObject.attr("data-0-position", "border");
                LECurrentObject.attr("data-1-position", "border");
                LECurrentObject.attr("data-2-position", "border");

                /*
                 为目标对象添加class 每次添加记住class 下次点击先移除 再添加
                 */
                var _$this = $(this);
                var $target = LECurrentObject.find("ul.nav-pills");
                var _style = _$this.attr("data-ref");
                $target.removeClass($target.attr("data-prestyle"));
                $target.attr("data-prestyle", _style);
                $target.addClass(_style);
                //LECurrentObject.trigger("click");
                /*设置样式时清除自定义的菜单宽高*/
                //LECurrentObject.find(".nav-pills > li > a > div").css({"width":"","height":""});
                /*设置样式时清除自定义的菜单高度和padding*/
                //LECurrentObject.find(".nav-pills > li > a > div").css({"height":""});
                // LECurrentObject.find(".nav-pills > li > a").css({"padding-left":"","padding-right":""});
                resetMainColorStatusNew();
                resetMainBackground();
                resetMainAlignStyle();
                resetMainWH();
                resetMainMargin();
                resetMainBoxWH();
                resetMainModelPreview();
                LEHistory.trigger();
            });
        };
        /*
         标签栏样式预览 实现方法  如果设置了样式 让预览处的html为按钮的html 否则为第3个标签样式（默认样式）
         */
        function resetMainModelPreview() {
            var $target = LECurrentObject;
            var l = $mainModelDiv.find("ul").length;
            var tabClassName = $target.find("ul.nav-pills").attr("data-prestyle");
            if (!tabClassName) {
                $mainModelPreview.html($mainModelDiv.children("ul").eq(4).prop("outerHTML"));
                $mainModelPreview.children("ul").removeClass("displayStyle").removeClass("box-shadow").removeClass("mgt10");
            } else {
                for (var i = 0; i < l; i++) {
                    var className = $mainModelDiv.find("ul").eq(i).attr("data-ref");
                    if (tabClassName == className) {
                        $mainModelPreview.html($mainModelDiv.children("ul").eq(i).prop("outerHTML"));
                        $mainModelPreview.children("ul").removeClass("displayStyle").removeClass("box-shadow").removeClass("mgt10");
                    }
                }

            }
        }

        /*单击普通、悬停、点击三个按钮时，输入框颜色切换，值等于input框对应的data-0、data-1、data-2*/
        var initMainEvent = function () {
            $("#mainUl").find("li").bind("click", function () {
                $(this).addClass("activt").siblings().removeClass("activt");
                $mainLibgColor.val($mainLibgColor.attr("data-" + $(this).index()));
                $mainLitxColorPick.spectrum("set", $mainLitxColor.attr("data-" + $(this).index()));
                $mainLitxColor.val($mainLitxColor.attr("data-" + $(this).index()));
                $mainLibgColorPick.spectrum("set", $mainLibgColor.attr("data-" + $(this).index()));
                //LECurrentObject.trigger("click");
                resetMainColorStatusNew();
                resetMainBackground();
                resetMainAlignStyle();
                resetMainWH();
                resetMainMargin();
                resetMainBoxWH();
                resetMainModelPreview();
            });

        };
        /*
         对style标签进行操作 调用了StyleManager对象的setSty方法实现增加或修改styl标签内容
         */
        function changeMainStyle(partSelector, sccName, cssValue) {
            var id = LECurrentObject.attr("id");
            var selector = "#" + id + " " + partSelector;
            StyleManager.setStyle(selector, sccName, cssValue);
        }

        /*
         /*设置字体颜色，区分不同状态的a便签进行设置*/
        var initMainColorEvent = function () {
            $mainLitxColorPick.spectrum(
                LEColorPicker.getOptions(function (tinycolor) {
                    var _c = tinycolor ? tinycolor.toRgbaString() : "";
                    var _v = tinycolor ? tinycolor.toHexString() : "#000000";
                    $mainLitxColor.val(_v);
                    if ($mainMenuNormal.hasClass("activt")) {
                        $mainLitxColor.attr("data-0", _v);
                        changeMainStyle(".nav-pills > li:not(.active):not(:hover) > a", "color", _c);
                    } else if ($mainMenuHover.hasClass("activt")) {
                        $mainLitxColor.attr("data-1", _v);
                        changeMainStyle(".nav-pills > li > a:hover", "color", _c);
                    } else if ($mainMenuClick.hasClass("activt")) {
                        $mainLitxColor.attr("data-2", _v);
                        changeMainStyle(".nav-pills > li.active > a", "color", _c);
                    }
                    LEHistory.trigger();
                })
            );
        };
        /*设置标签背景颜色 区分不同状态的a便签进行设置*/
        var initMainBackground = function () {
            $mainLibgColorPick.spectrum(
                LEColorPicker.getOptions(function (tinycolor) {
                    var _c = tinycolor ? tinycolor.toRgbaString() : "";
                    var _v = tinycolor ? tinycolor.toHexString() : "#000000";
                    $mainLibgColor.val(_v);
                    if ($mainMenuNormal.hasClass("activt")) {
                        $mainLibgColor.attr("data-0", _v);
                        changeMainStyle(".nav-pills > li:not(.active):not(:hover) > a", "background-color", _c);
                    } else if ($mainMenuHover.hasClass("activt")) {
                        $mainLibgColor.attr("data-1", _v);
                        changeMainStyle(".nav-pills > li > a:hover", "background-color", _c);
                    } else if ($mainMenuClick.hasClass("activt")) {
                        $mainLibgColor.attr("data-2", _v);
                        changeMainStyle(".nav-pills > li.active > a", "background-color", _c);
                    }
                    LEHistory.trigger();
                })
            );
        };
        /*修改字体颜色输入框的值,通过正则表达式判断输入的值，格式匹配则更新，否则不更改*/
        var initMainTxChange = function () {
            $mainLitxColor.bind("change", function () {
                var reg = /^#([0-9a-fA-F]{3}|[0-9a-fA-F]{6})$/;
                if (reg.test($(this).val())) {
                    if ($mainMenuNormal.hasClass("activt")) {
                        $mainLitxColor.attr("data-0", $(this).val());
                        changeMainStyle(".nav-pills > li:not(.active):not(:hover) > a", "color", $(this).val());
                    } else if ($mainMenuHover.hasClass("activt")) {
                        $mainLitxColor.attr("data-1", $(this).val());
                        changeMainStyle(".nav-pills > li > a:hover", "color", $(this).val());
                    } else if ($mainMenuClick.hasClass("activt")) {
                        $mainLitxColor.attr("data-2", $(this).val());
                        changeMainStyle(".nav-pills > li.active > a", "color", $(this).val());
                    }
                    $mainLitxColorPick.spectrum("set", $(this).val());
                } else {
                    if ($mainMenuNormal.hasClass("activt")) {
                        $mainLitxColor.attr("data-0", "#0069D6");
                        $(this).val("#0069D6");
                        changeMainStyle(".nav-pills > li:not(.active):not(:hover) > a", "color", "#0069D6");
                        $mainLitxColorPick.spectrum("set", "#0069D6");
                    } else if ($mainMenuHover.hasClass("activt")) {
                        $mainLitxColor.attr("data-1", "#0069D6");
                        $(this).val("#0069D6");
                        changeMainStyle(".nav-pills > li > a:hover", "color", "#0069D6");
                        $mainLitxColorPick.spectrum("set", "#0069D6");
                    } else if ($mainMenuClick.hasClass("activt")) {
                        $mainLitxColor.attr("data-2", "#555");
                        $(this).val("#555");
                        changeMainStyle(".nav-pills > li.active > a", "color", "#555");
                        $mainLitxColorPick.spectrum("set", "#555");
                    }
                }
                LEHistory.trigger();
            })
        };

        /*修改背景颜色输入框的值，通过正则表达式过滤，匹配则更新，否则不改变*/
        var initMainBgChange = function () {
            $mainLibgColor.bind("change", function () {
                var reg = /^#([0-9a-fA-F]{3}|[0-9a-fA-F]{6})$/;
                if (reg.test($(this).val())) {
                    if ($mainMenuNormal.hasClass("activt")) {
                        $mainLibgColor.attr("data-0", $(this).val());
                        changeMainStyle(".nav-pills > li:not(.active):not(:hover) > a", "background-color", $(this).val());
                    } else if ($mainMenuHover.hasClass("activt")) {
                        $mainLibgColor.attr("data-1", $(this).val());
                        changeMainStyle(".nav-pills > li > a:hover", "background-color", $(this).val());
                    } else if ($mainMenuClick.hasClass("activt")) {
                        $mainLibgColor.attr("data-2", $(this).val());
                        changeMainStyle(".nav-pills > li.active > a", "background-color", $(this).val());
                    }
                    $mainLibgColorPick.spectrum("set", $(this).val());
                } else {
                    if ($mainMenuNormal.hasClass("activt")) {
                        $mainLibgColor.attr("data-0", "#ffffff");
                        $(this).val("#ffffff");
                        changeMainStyle(".nav-pills > li:not(.active):not(:hover) > a", "background-color", "#ffffff");
                        $mainLibgColorPick.spectrum("set", "#ffffff");
                    } else if ($mainMenuHover.hasClass("activt")) {
                        $mainLibgColor.attr("data-1", "#f5f5f5");
                        $(this).val("#f5f5f5");
                        changeMainStyle(".nav-pills > li > a:hover", "background-color", "#ffffff");
                        $mainLibgColorPick.spectrum("set", "#f5f5f5");
                    } else if ($mainMenuClick.hasClass("activt")) {
                        $mainLibgColor.attr("data-2", "#ffffff");
                        $(this).val("#ffffff");
                        changeMainStyle(".nav-pills > li.active > a", "background-color", "#ffffff");
                        $mainLibgColorPick.spectrum("set", "#ffffff");
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
        var initMainBgUpload = function () {
            $mainBgPreviewImg.click(function (e) {
                var isNew = $mainBgPreviewImg.attr("src") != "export/images/sliderPanel/sliderPanel1.png";
                if (isNew) {
                    var data = [];
                    data.push($mainBgPreviewImg.attr("src"));
                    LEDialog.toggleDialog(LE.options["Dialog"].picEditDialog, function (imgList) {
                            if (imgList.length > 0) {
                                if ($mainMenuNormal.hasClass("activt")) {
                                    changeMainStyle(".nav-pills > li:not(.active):not(:hover) > a", "background-image", "url(" + imgList[0].path + ")");
                                } else if ($mainMenuHover.hasClass("activt")) {
                                    changeMainStyle(".nav-pills > li > a:hover", "background-image", "url(" + imgList[0].path + ")");

                                } else if ($mainMenuClick.hasClass("activt")) {
                                    changeMainStyle(".nav-pills > li.active > a", "background-image", "url(" + imgList[0].path + ")");
                                }
                                LECurrentObject.find("ul").find("li").find("a").css("backgroundSize", "cover");
                                LECurrentObject.find("ul").find("li").find("a").css("backgroundPosition", "center");
                                //LECurrentObject.trigger("click");

                                resetMainColorStatusNew();
                                resetMainBackground();
                                resetMainAlignStyle();
                                resetMainWH();
                                resetMainMargin();
                                resetMainBoxWH();
                                resetMainModelPreview();
                            }
                        },
                        data
                    );

                } else {
                    LEDialog.toggleDialog(LE.options["Dialog"].picUploadDialog, function (jsonUploadImg) {
                        var url = jsonUploadImg.imgPath;
                        if ($mainMenuNormal.hasClass("activt")) {
                            changeMainStyle(".nav-pills > li:not(.active):not(:hover) > a", "background-image", "url(" + url + ")");
                        } else if ($mainMenuHover.hasClass("activt")) {
                            changeMainStyle(".nav-pills > li > a:hover", "background-image", "url(" + url + ")");

                        } else if ($mainMenuClick.hasClass("activt")) {
                            changeMainStyle(".nav-pills > li.active > a", "background-image", "url(" + url + ")");
                        }
                        LECurrentObject.find("ul").find("li").find("a").css("backgroundSize", "cover");
                        LECurrentObject.find("ul").find("li").find("a").css("backgroundPosition", "center");
                        //LECurrentObject.trigger("click");
                        LEHistory.trigger();
                        resetMainColorStatusNew();
                        resetMainBackground();
                        resetMainAlignStyle();
                        resetMainWH();
                        resetMainMargin();
                        resetMainBoxWH();
                        resetMainModelPreview();
                    });
                }
            });
            $("#mainBgFileLabel").hover(function () {
                $.trim($mainBgPreviewImg.attr("src")) != "export/images/sliderPanel/sliderPanel1.png" && $("#mainBgDeleteImg").show();
            }, function () {
                $("#mainBgDeleteImg").hide();
            });
        };
        /*
         删除标签栏背景图片：通过判断普通、悬停、点击三个按钮的状态实现对不同状态a标签背景图的删除
         */
        var initDeleteMainImage = function () {
            $("#mainBgDeleteImg").click(function () {
                if ($mainMenuNormal.hasClass("activt")) {
                    changeMainStyle(".nav-pills > li:not(.active):not(:hover) > a", "background-image", "none")
                } else if ($mainMenuHover.hasClass("activt")) {
                    changeMainStyle(".nav-pills > li > a:hover", "background-image", "none")
                } else if ($mainMenuClick.hasClass("activt")) {
                    changeMainStyle(".nav-pills > li.active > a", "background-image", "none")
                }
                $mainBgPreviewImg.attr("src", "");
                LEHistory.trigger();

            });
        };
        /*初始化 对齐按钮的事件，当点击按钮的时候，设置当前对象的 text-align*/
        var initMainAlignEvent = function () {
            $mainAlignUl.find("li").bind("click", function (e) {
                var _$this = $(this);  //点击的设置对齐方式的按钮
                var $target = LECurrentObject.find(".nav-pills > li > a div"); //获取当前对象
                $target.css("text-align", _$this.attr("data-align"));
                /*if(LECurrentObject.find(".align_mark").size() > 0){
                 LECurrentObject.find(".align_mark").css("display", "inline-block");
                 LECurrentObject.find(".align_mark").parent().css("display", "inline-block");
                 }*/
                LEHistory.trigger();
            });
        };
        /*初始化 对齐按钮的事件，当点击按钮的时候，设置当前对象的 padding-left padding-right*/
        var initMainAlignByPadding = function () {
            $mainAlignUl.find("li").bind("click", function (e) {
                var _$this = $(this);  //点击的设置对齐方式的按钮
                var $target = LECurrentObject.find(".nav-pills > li > a"); //获取当前对象
                var paddingValue = parseInt($mmTabPadding.val());
                if (_$this.attr("data-align") == "left") {
                    $target.css({"padding-left": "0", "padding-right": paddingValue * 2 + "px"});
                } else if (_$this.attr("data-align") == "right") {
                    $target.css({"padding-left": paddingValue * 2 + "px", "padding-right": "0"});
                } else {
                    $target.css({"padding-left": paddingValue + "px", "padding-right": paddingValue + "px"});
                }
                LEHistory.trigger();
            });
        };
        //获得当前对象的对齐值，并且初始化到按钮上面
        var resetMainAlignStyle = function () {
            $mainAlignUl.find("li").removeClass("select selected");
            var $target = LECurrentObject.find(".nav-pills > li > a div"); //获取当前对象
            var _align = $target.css("text-align") || "left";
            $mainAlignUl.find("li[data-align=" + _align + "]").addClass("select selected");
        };
        //对比当前对象的padding值，并且初始化到对齐按钮上面
        var resetMainAlignByPadding = function () {
            $mainAlignUl.find("li").removeClass("select selected");
            var $target = LECurrentObject.find(".nav-pills > li > a"); //获取当前对象
            var PdLeft = parseInt($target.css("padding-left"));
            var PdRight = parseInt($target.css("padding-right"));
            if (PdLeft < PdRight) {
                $mainAlignUl.find("li[data-align='left']").addClass("select selected");
            } else if (PdLeft > PdRight) {
                $mainAlignUl.find("li[data-align='right']").addClass("select selected");
            } else {
                $mainAlignUl.find("li[data-align='center']").addClass("select selected");
            }
        };
        /*初始化时背景图片预览按钮的状态*/
        var resetMainBackground = function () {
            var target = LECurrentObject.attr("id");
            var _imgH = StyleManager.getStyle("#" + target + " .nav-pills > li > a:hover", "backgroundImage");
            var _imgH = _imgH == "none" ? "export/images/sliderPanel/sliderPanel1.png" : StyleManager.getStyle("#" + target + " .nav-pills > li > a:hover", "backgroundImage");
            var _imgN = LECurrentObject.find(".nav-pills > li:not(.active):not(:hover) > a").css("backgroundImage") == "none" ? "export/images/sliderPanel/sliderPanel1.png" : LECurrentObject.find(".nav-pills > li:not(.active):not(:hover) > a").css("backgroundImage");
            var _imgL = LECurrentObject.find(".nav-pills > li.active > a").css("backgroundImage") == "none" ? "export/images/sliderPanel/sliderPanel1.png" : LECurrentObject.find(".nav-pills > li.active > a").css("backgroundImage");

            if (_imgN) {
                var _imgN = LECurrentObject.find(".nav-pills > li:not(.active):not(:hover) > a").css("backgroundImage") == "none" ? "export/images/sliderPanel/sliderPanel1.png" : LECurrentObject.find(".nav-pills > li:not(.active):not(:hover) > a").css("backgroundImage");
            } else {
                _imgN = "export/images/sliderPanel/sliderPanel1.png";
            }
            if (_imgL) {
                var _imgL = LECurrentObject.find(".nav-pills > li.active > a").css("backgroundImage") == "none" ? "export/images/sliderPanel/sliderPanel1.png" : LECurrentObject.find(".nav-pills > li.active > a").css("backgroundImage");
            } else {
                _imgL = "export/images/sliderPanel/sliderPanel1.png";
            }
            //alert(_imgN+"-------"+_imgL);
            if (_imgN != "") {
                _imgN = _imgN.replace(/url\([\"\']?/g, "").replace(/[\"\']?\)/g, "");
            }
            if (_imgH != "") {
                _imgH = _imgH.replace(/url\([\"\']?/g, "").replace(/[\"\']?\)/g, "");
            }
            if (_imgL != "") {
                _imgL = _imgL.replace(/url\([\"\']?/g, "").replace(/[\"\']?\)/g, "");
            }
            if ($mainMenuNormal.hasClass("activt")) {
                _imgN && $mainBgPreviewImg.attr("src", _imgN);
            } else if ($mainMenuHover.hasClass("activt")) {
                _imgH && $mainBgPreviewImg.attr("src", _imgH);
            } else if ($mainMenuClick.hasClass("activt")) {
                _imgL && $mainBgPreviewImg.attr("src", _imgL);
            }
        };
        /*RGB颜色转换为16进制*/
        function colorHexFn() {
            var reg = /^#([0-9a-fA-F]{3}|[0-9a-fA-F]{6})$/;
            String.prototype.colorHex = function () {
                var that = this;
                if (/^(rgb|RGB)/.test(that)) {
                    var aColor = that.replace(/(?:\(|\)|rgb|RGB)*/g, "").split(",");
                    var strHex = "#";
                    for (var i = 0; i < aColor.length; i++) {
                        var hex = Number(aColor[i]).toString(16);
                        if (hex === "0") {
                            hex += hex;
                        }
                        strHex += hex;
                    }
                    if (strHex.length !== 7) {
                        strHex = that;
                    }
                    return strHex;
                } else if (reg.test(that)) {
                    var aNum = that.replace(/#/, "").split("");
                    if (aNum.length === 6) {
                        return that;
                    } else if (aNum.length === 3) {
                        var numHex = "#";
                        for (var i = 0; i < aNum.length; i += 1) {
                            numHex += (aNum[i] + aNum[i]);
                        }
                        return numHex;
                    }
                } else {
                    return that;
                }
            };
        }

        /*初始化主导航样式的按钮状态 字体及背景颜色
         * 根据样式优先级
         * 1、判断有没有自定义样式，如果有,将style标签中的自定义样式初始化到按钮
         * 2、没有自定义样式 则判断有没有模板样式，如果有，将对应的模板样式初始化到按钮
         * 3、否则 将默认样式初始化到按钮
         * */
        var resetMainColorStatusNew = function () {
            var target = LECurrentObject.attr("id");
            var NormalA = LECurrentObject.find(".nav-pills > li:not(.active):not(:hover) a").css("color");
            var LinkA = LECurrentObject.find(".nav-pills > li.active > a").css("color");
            var NormalB = LECurrentObject.find(".nav-pills > li:not(.active):not(:hover) a").css("background-color");
            var LinkB = LECurrentObject.find(".nav-pills > li.active > a").css("background-color");

            var LinkA = !LinkA ? $mainLitxColor.attr("data-2") : LECurrentObject.find(".nav-pills > li.active > a").css("color");
            var LinkB = !LinkB ? $mainLibgColor.attr("data-2") : LECurrentObject.find(".nav-pills > li.active > a").css("background-color");
            var NormalA = !NormalA ? $mainLitxColor.attr("data-0") : LECurrentObject.find(".nav-pills > li:not(.active):not(:hover) a").css("color");
            var NormalB = !NormalB ? $mainLibgColor.attr("data-0") : LECurrentObject.find(".nav-pills > li:not(.active):not(:hover) a").css("background-color");
            var HoverA = StyleManager.getStyle("#" + target + " .nav-pills > li > a:hover", "color");
            var HoverB = StyleManager.getStyle("#" + target + " .nav-pills > li > a:hover", "backgroundColor");
            if (HoverA == "none") {
                if (LECurrentObject.find("ul.nav-pills").attr("data-prestyle")) {
                    //2、否则如果样式中存在,当前对象的最外层ul是否具有"data-prestyle"属性 属性值对应class名
                    var dataPrestyle = LECurrentObject.find("ul.nav-pills").attr("data-prestyle");
                    HoverA = modelStyleManager.getStyle("." + dataPrestyle + " > li > a:hover", "color");
                } else {
                    //3否则默认样式
                    HoverA = "#23527c";
                }
            } else {
                //1、如果html中存在自定义选择器
                HoverA = StyleManager.getStyle("#" + target + " .nav-pills > li > a:hover", "color");
            }

            if (HoverB == "none") {
                if (LECurrentObject.find("ul.nav-pills").attr("data-prestyle")) {
                    //2、否则如果样式中存在,当前对象的最外层ul是否具有"data-prestyle"属性 属性值对应class名
                    var dataPrestyle = LECurrentObject.find("ul.nav-pills").attr("data-prestyle");
                    HoverB = modelStyleManager.getStyle("." + dataPrestyle + " > li > a:hover", "backgroundColor");
                } else {
                    //3否则默认样式
                    HoverB = "#eee";
                }
            } else {
                //1、如果html中存在自定义选择器
                HoverB = StyleManager.getStyle("#" + target + " .nav-pills > li > a:hover", "backgroundColor");
            }

            $mainLitxColor.attr("data-0", NormalA);
            $mainLitxColor.attr("data-1", HoverA);
            $mainLitxColor.attr("data-2", LinkA);
            $mainLibgColor.attr("data-0", NormalB);
            $mainLibgColor.attr("data-1", HoverB);
            $mainLibgColor.attr("data-2", LinkB);

            if ($mainMenuNormal.hasClass("activt")) {
                $mainLitxColorPick.spectrum("set", NormalA);
                $mainLitxColor.val($mainLitxColorPick.spectrum("get").toHexString());
                $mainLibgColorPick.spectrum("set", NormalB);
                $mainLibgColor.val($mainLibgColorPick.spectrum("get").toHexString());
            } else if ($mainMenuHover.hasClass("activt")) {
                $mainLitxColorPick.spectrum("set", HoverA);
                $mainLitxColor.val($mainLitxColorPick.spectrum("get").toHexString());
                $mainLibgColorPick.spectrum("set", HoverB);
                $mainLibgColor.val($mainLibgColorPick.spectrum("get").toHexString());
            } else if ($mainMenuClick.hasClass("activt")) {
                $mainLitxColorPick.spectrum("set", LinkA);
                $mainLitxColor.val($mainLitxColorPick.spectrum("get").toHexString());
                $mainLibgColorPick.spectrum("set", LinkB);
                $mainLibgColor.val($mainLibgColorPick.spectrum("get").toHexString());
            }
        };
        /*初始化交互样式的按钮状态 字体及背景颜色*/
        var resetMainColorStatus = function () {
            colorHexFn();
            var target = LECurrentObject.attr("id");
            var HoverA = StyleManager.getStyle("#" + target + " .nav-pills > li > a:hover", "color");
            var HoverB = StyleManager.getStyle("#" + target + " .nav-pills > li > a:hover", "backgroundColor");

            var HoverA = HoverA == "none" ? "#000000" : StyleManager.getStyle("#" + target + " .nav-pills > li > a:hover", "color");
            var HoverB = HoverB == "none" ? "#ffffff" : StyleManager.getStyle("#" + target + " .nav-pills > li > a:hover", "backgroundColor");
            // alert(HoverA+HoverB);
            var NormalA = LECurrentObject.find(".nav-pills > li:not(.active):not(:hover) a").css("color");
            var NormalA = NormalA == "none" ? "#000000" : LECurrentObject.find(".nav-pills > li:not(.active):not(:hover) a").css("color");
            var LinkA = LECurrentObject.find(".nav-pills > li.active > a").css("color");
            var NormalB = LECurrentObject.find(".nav-pills > li:not(.active):not(:hover) a").css("background-color");
            var LinkB = LECurrentObject.find(".nav-pills > li.active > a").css("background-color");
            //alert(NormalA+"-NormalA-"+LinkA+"-LinkA-"+NormalB+"-NormalB-"+LinkB+"-LinkB-");
            $mainLitxColor.attr("data-0", NormalA);
            $mainLitxColor.attr("data-1", HoverA);
            $mainLitxColor.attr("data-2", LinkA);
            $mainLibgColor.attr("data-0", NormalB);
            $mainLibgColor.attr("data-1", HoverB);
            $mainLibgColor.attr("data-2", LinkB);

            if ($mainMenuNormal.hasClass("activt")) {
                $mainLitxColorPick.spectrum("set", NormalA);
                $mainLitxColor.val($mainLitxColorPick.spectrum("get").toHexString());
                $mainLibgColorPick.spectrum("set", NormalB);
                $mainLibgColor.val($mainLibgColorPick.spectrum("get").toHexString());
            } else if ($mainMenuHover.hasClass("activt")) {
                $mainLitxColorPick.spectrum("set", HoverA);
                $mainLitxColor.val($mainLitxColorPick.spectrum("get").toHexString());
                $mainLibgColorPick.spectrum("set", HoverB);
                $mainLibgColor.val($mainLibgColorPick.spectrum("get").toHexString());

            } else if ($mainMenuClick.hasClass("activt")) {

                $mainLitxColorPick.spectrum("set", LinkA);
                $mainLitxColor.val($mainLitxColorPick.spectrum("get").toHexString());
                $mainLibgColorPick.spectrum("set", LinkB);
                $mainLibgColor.val($mainLibgColorPick.spectrum("get").toHexString());
            }
        };
        /*
         * 导航整体宽高*/
        var initMainBoxWHEvent = function () {
            $mainBoxWH.onlyNum().keydown(function (e) {
                //当设置高度时，去掉min-height
                $(this).attr("data-direct") == "height" && LECurrentObject.css("min-height", "0px");
                LEKey.numInputKeyDown(e, $(this), LECurrentObject);
            });

            //失去焦点的时候，置0
            $mainBoxWH.blur(function (e) {
                LEKey.numInputBlur(e, $(this), LECurrentObject);
                LEHistory.trigger();
            });

            $mainBoxWH.focus(function (e) {
                LEKey.numInputFocus(e, $(this), LECurrentObject);
            });
        };
        /*
         * 菜单宽高*/
        var initMainWEvent = function () {
            $(".mmTabWH").onlyNum().keydown(function (e) {
                LEKey.numInputKeyDown(e, $(this), LECurrentObject.find(".nav-pills > li > a > div"));
            });
            //失去焦点的时候，置0
            $(".mmTabWH").blur(function (e) {
                LEKey.numInputBlur(e, $(this), LECurrentObject.find(".nav-pills > li > a > div"));
                LEHistory.trigger();
            });

            $(".mmTabWH").focus(function (e) {
                LEKey.numInputFocus(e, $(this), LECurrentObject.find(".nav-pills > li > a > div"));
            });
        };
        /*
         * 菜单内边距 padding-left padding-right*/
        var initMainPaddingEvent = function () {
            $mmTabPadding.onlyNum().keydown(function (e) {
                LE.stylesheets["MainMenu"]().numInputKeyDownPad(e, $(this), LECurrentObject.find(".nav-pills > li > a"));
                resetMainAlignByPadding();
            });
            //失去焦点的时候，置0
            $mmTabPadding.blur(function (e) {
                LE.stylesheets["MainMenu"]().numInputBlurPad(e, $(this), LECurrentObject.find(".nav-pills > li > a"));
                resetMainAlignByPadding();
                LEHistory.trigger();
            });

            $mmTabPadding.focus(function (e) {
                LE.stylesheets["MainMenu"]().numInputFocusPad(e, $(this), LECurrentObject.find(".nav-pills > li > a"));
            });
        };
        /*
         * 菜单外边距 margin-right*/
        var initMainMgEvent = function () {
            $mmTabMargin.onlyNum().keydown(function (e) {
                LEKey.numInputKeyDown(e, $(this), LECurrentObject.find(".nav-pills > li"));
            });
            //失去焦点的时候，置0
            $mmTabMargin.blur(function (e) {
                LEKey.numInputBlur(e, $(this), LECurrentObject.find(".nav-pills > li"));
                LEHistory.trigger();
            });

            $mmTabMargin.focus(function (e) {
                LEKey.numInputFocus(e, $(this), LECurrentObject.find(".nav-pills > li"));
            });
        };
        var resetMainWH = function () {
            $(".mmTabWH").each(function () {
                var $this = $(this);
                var $target = LECurrentObject.find(".nav-pills > li > a > div");
                var _ref = $this.attr("data-ref");
                $this.val(parseInt($target.css(_ref)) + "px");
            });
        };
        var resetMainMargin = function () {
            var $target = LECurrentObject.find(".nav-pills > li");
            var _ref = $mmTabMargin.attr("data-ref");
            $mmTabMargin.val(parseInt($target.css(_ref)) + "px");
        };
        /**
         * padding 的初始化值为  左padding 加 右padding 除以2  保证对齐按钮调整左右padding后结果不变
         */
        var resetMainPadding = function () {
            var $target = LECurrentObject.find(".nav-pills > li > a");
            var PdLeft = parseInt($target.css("padding-left"));
            var PdRight = parseInt($target.css("padding-right"));
            var pd = parseInt(PdLeft + PdRight) / 2;
            $mmTabPadding.val(pd + "px");
        };
        var resetMainBoxWH = function () {
            $mainBoxWH.each(function () {
                var $this = $(this);
                var $target = LECurrentObject;
                var _ref = $this.attr("data-ref");
                $this.val(parseInt($target.css(_ref)) + "px");
            });
        };

        /**
         * guzm
         */
        var initMMBasicMain = function () {
            $("#mmBasicMain").click(function () {
                var _t = [];
                _t.push('MainMenu');
                _t.push('SubMenu');
                _t.push('NavManage');

                var options = {
                    object: LECurrentObject,
                    target: _t
                };
                $("#sidebar-panel").find("section").hide();
                LEStyle.run("Back", options, false).run("Position", options, false, true).run("BolderSetting", options, false, true).run("BackGround", options, false, true).run("TextSetting", options, false, true);
            });
        };
        return {
            //onload
            init: function () {
                initMainEvent();
                initMainColorEvent();
                initMainBackground();
                //initMainTxChange(); //文字颜色input框交互
                //initMainBgChange(); //背景颜色input框交互
                initMainBgUpload();
                initDeleteMainImage();
                //initMainAlignEvent();
                initMainWEvent();
                initMainMgEvent();
                initMainBoxWHEvent();
                initMainModelShow();
                initMainModelPreview();
                initChangeMainStyleBlock();
                initMainPaddingEvent();
                initMainAlignByPadding();

                initDragMainMenu();

                initMMBasicMain();


                //console.info(document.styleSheets)
            },
            run: function (options, doHide, doSlide) {
                if (LECurrentObject.children().length > 0) {
                    //resetMainColorStatus();
                    resetMainColorStatusNew();
                    resetMainBackground();
                    //resetMainAlignStyle();
                    resetMainWH();
                    resetMainMargin();
                    resetMainBoxWH();
                    resetMainModelPreview();
                    resetMainPadding();
                    resetMainAlignByPadding();
                    //切换组件时，样式设置部分回顶部
                    sliderbarToTop();
                    //子导航样式3的hover背景颜色
                    // alert(document.styleSheets[13].cssRules[55].style["backgroundColor"]);
                   // LEDisplay.show($PS, doHide, doSlide);
                }
            },
            destroy: function () {
               // $PS.hide();
                $mainModelGoBack.animate({right: "-250px"}, "fast", "linear");
                $mainModelDiv.animate({right: "-250px"}, "fast", "linear");
            },
            numInputKeyDownPad: function (event, $this, $target) {
                //如果是上下的话，执行以下的操作
                if (event.keyCode == 38 || event.keyCode == 40 || event.keyCode == 13) {
                    var num = parseInt($this.val());
                    if (isNaN(num)) return;
                    var _unit = $this.attr("data-unit") || "px";
                    //点击上下按钮，来改变当前的值
                    event.keyCode == 38 && ++num;
                    event.keyCode == 40 && --num;
                    if ($this.attr("data-ref") == "padding-left") {
                        $target.css({"padding-left": num + _unit, "padding-right": num + _unit});
                        $this.val(num + _unit);
                    } else if($this.attr("data-ref") == "text-indent"){
                        //文字首行缩进的计算 获取字体大小时区分轮播、多图及文字组件
                        var _target = LECurrentObject;
                        if(_target.hasClass("le-carousel")){
                            _target = LECurrentObject.find(".carousel-caption").find("h4");
                        }else if(_target.hasClass("le-gallery")){
                            _target = LECurrentObject.find("p").children();
                        }else{
                            _target = LECurrentObject;
                        }

                        var fontSize=parseInt(_target.css("font-size"));
                        $target.css($this.attr("data-ref"), num*fontSize+"px");
                        $this.val(num);

                    }else {
                        $target.css($this.attr("data-ref"), num + _unit);
                        $this.val(num + _unit);
                    }

                    return num;
                }
                return null;
            },
            numInputBlurPad: function (event, $this, $target) {
                $.trim($this.val()) == "" && $this.val($this.attr("data-dv"));
                if ($.trim($this.val()) == "")  return;
                var num = parseInt($this.val());
                var _unit = $this.attr("data-unit") || "px";
                if ($this.attr("data-ref") == "padding-left") {
                    $target.css({"padding-left": num + _unit, "padding-right": num + _unit});
                    $this.val(num + _unit);
                } else if($this.attr("data-ref") == "text-indent"){
                    //文字首行缩进的计算 获取字体大小时区分轮播、多图及文字组件
                    var _target = LECurrentObject;
                    if(_target.hasClass("le-carousel")){
                        _target = LECurrentObject.find(".carousel-caption").find("h4");
                    }else if(_target.hasClass("le-gallery")){
                        _target = LECurrentObject.find("p").children();
                    }else{
                        _target = LECurrentObject;
                    }

                    var fontSize=parseInt(_target.css("font-size"));
                    $target.css($this.attr("data-ref"), num*fontSize+"px");
                    $this.val(num);
                }else {
                    $target.css($this.attr("data-ref"), num + _unit);
                    $this.val(num + _unit);
                }

            },
            numInputFocusPad: function (event, $this, $target) {

                $this.attr("data-dv", $target.css($this.attr("data-ref")));
            }
        };
    };
})(window, jQuery, LE, undefined);

/*
 读写#special_style，用于操作样式
 */
var $style = jQuery("#special_style");
var StyleManager = {
    setStyle: function (selector, css, value) {
        //判断当前的style是否存在, 如果存在，修改；如果不存在，添加
        //存在
        if (this.isExist(selector, css, value)) {
            this.modifyStyle(selector, css, value);
        } else {//不存在，添加
            $style.append(selector + "{" + css + ":" + value + ";}");
        }
        // LEHistory.trigger();
    },
    removeStyle: function (id) {
        //获得style标签里面的所有对象
        /*var l=document.styleSheets.length;
         var arr=[];
         for(var j=0;j<l;j++){
         if(document.styleSheets[j].ownerNode.id=="special_style"){
         arr.push(j)
         }
         }*/
        //var sheet = document.styleSheets[document.styleSheets.length - 1];
        var sheet = this.getStyleSheet("special_style");
        //获得所有的rules
        var rules = sheet.cssRules || sheet.rules;
        var _htmlArr = [];
        //获得目标rule
        for (var i in rules) {
            var _st = rules[i].selectorText + "";
           /* if (_st.indexOf(id) == -1) {
                _htmlArr.push(rules[i].cssText);
            }*/
            var idLen=id.length;
            var _stLen=_st.length;
            if(idLen<_stLen && _st.substr(0,idLen)==id && _st.indexOf(id+" ") != -1){
                continue;
            }else if(idLen<_stLen && _st.substr(0,idLen)==id && _st.indexOf(id+" ") == -1){
                _htmlArr.push(rules[i].cssText);
            }else if(_st.indexOf(id) == -1){
                _htmlArr.push(rules[i].cssText);
            }
        }
        $style.html(_htmlArr.join(""));
    },
    getStyle: function (selector, css) {
        //判断当前的style是否存在, 如果存在，获取；如果不存在，返回none
        //存在
        if (this.isExist(selector)) {
            return this.getStyle1(selector, css);
        } else {//不存在,返回字符串none
            return "none";
        }
    },
    getStyle1: function (selector, css) {
        var rule = this.getRule(selector);
        var _style = rule.style[css] + "";
        _style = jQuery.trim(_style) === "" ? "none" : _style;
        return _style;
    },
    modifyStyle: function (selector, css, value) {
        //获得style标签里面的所有对象
        //var sheet = document.styleSheets[document.styleSheets.length - 1];
        var sheet = this.getStyleSheet("special_style");
        //获得所有的rules
        var rules = sheet.cssRules || sheet.rules;
        var rule = this.getRule(selector);
        var _style = rule.style;
        _style[css] = value;
        var _htmlArr = [];
        //把改变后的rules帖到style标签里
        for (var i = 0, len = rules.length; i < len; i++) {
            _htmlArr.push(rules[i].cssText);
        }
        $style.html(_htmlArr.join(""));
        /**/
    },
    getStyleSheet: function (_id) {
        var sheets = document.styleSheets;
        for (var i in sheets) {
            if (sheets[i].ownerNode.id == _id) {
                return sheets[i];
            }
        }
        return null;
    },
    getRule: function (selector) {
        //获得style标签里面的所有对象
        //var sheet = document.styleSheets[document.styleSheets.length - 1];
        var sheet = this.getStyleSheet("special_style");
        //获得所有的rules
        var rules = sheet.cssRules || sheet.rules;
        //获得目标rule
        for (var i in rules) {
            if (rules[i].selectorText == selector) {
                return rules[i];
            }
        }
        return null
    },
    isExist: function (selector) {
        var _html = $style.html();
//        return _html.indexOf(selector) != -1; 判断选择器是否存在
        return _html.indexOf(selector+"{") != -1 || _html.indexOf(selector+" {") != -1;
    }
};
/*
 读取model.css里的样式，用于初始化按钮状态的颜色
 */
var modelStyleManager = {

    getStyle: function (selector, css) {
        return this.getStyle1(selector, css);
    },
    getStyle1: function (selector, css) {
        var rule = this.getRule(selector);
        if (!rule) return "none";
        var _style = rule.style[css] + "";
        _style = jQuery.trim(_style) === "" ? "none" : _style;
        return _style;
    },

    getRule: function (selector) {
        //获得style标签里面的所有对象
        //var sheet = document.styleSheets[13];
        var sheet = this.getStyleSheet("modelLink");
        //获得所有的rules
        var rules = sheet.cssRules || sheet.rules;
        //获得目标rule
        for (var i in rules) {
            if (rules[i].selectorText == selector) {
                return rules[i];
            }
        }
        return null
    },
    getStyleSheet: function (_id) {
        var sheets = document.styleSheets;
        for (var i in sheets) {
            if (sheets[i].ownerNode.id == _id) {
                return sheets[i];
            }
        }
        return null;
    }

};
