/**
 * Created by isaac_gu on 2016/1/14.  文件名带Old及XXX的为不用的js
 */
var ActiveSet_Map = new Map();//全局变量
var tabNBgSet_Map = new Map();
var tabHBgSet_Map = new Map();
var tabLBgSet_Map = new Map();
/*var tabBgSet_Map=new Map();*/
(function (window, $, LE) {
    LE.stylesheets["ActiveSetting"] = function () {
        var $panel = $("#activeSection");
        var $tabLitxColor = $("#tabLitxColor");
        var $tabLibgColor = $("#tabLibgColor");
        var $modelDiv = $("#modelDiv");

        /*标签栏更多样式面板出现消失*/
        var initMoreModelShow = function () {
            $("#modelMore").bind("click", function () {
                $modelDiv.animate({right: "0"}, "100", "linear");
            });
            $("#modelGoBack").bind("click", function () {
                $modelDiv.animate({right: "-250px"}, "100", "linear");
            });
        };

        /*将html中id为setTabStyle的style标签的内容解析为map对象，存储标签栏的文字和背景颜色*/
        var initGetActiveStyle = function () {
            var str = $("#setTabStyle").html();
            var arr = str.split("/*id*/");
            var l = arr.length;
            for (var i = 1; i < l; i++) {
                var thisK = arr[i].split("/*0*/")[0].replace("#", "");
                var thisV = {
                    normalA: arr[i].split("/*1*/")[1],
                    normalB: arr[i].split("/*2*/")[1],
                    hoverA: arr[i].split("/*3*/")[1],
                    hoverB: arr[i].split("/*1*/")[3],
                    linkA: arr[i].split("/*2*/")[3],
                    linkB: arr[i].split("/*3*/")[3]
                };
                ActiveSet_Map.put(thisK, thisV);
            }
        };

        /*将html中id为setTabNBgStyle的style标签的内容解析为map对象 存储普通a标签的背景图片url*/
        var initGetTabNBgStyle = function () {
            var str = $("#setTabNBgStyle").html();
            var arr = str.split("/*id*/");
            var l = arr.length;
            for (var i = 1; i < l; i++) {
                var thisK = arr[i].split("/*0*/")[0].replace("#", "");
                var thisV = arr[i].split("/*0*/")[2];
                ActiveSet_Map.put(thisK, thisV);
            }
        };

        /*将html中id为setTabHBgStyle的style标签的内容解析为map对象 存储hover时a标签的背景图片url*/
        var initGetTabHBgStyle = function () {
            var str = $("#setTabHBgStyle").html();
            var arr = str.split("/*id*/");
            var l = arr.length;
            for (var i = 1; i < l; i++) {
                var thisK = arr[i].split("/*0*/")[0].replace("#", "");
                var thisV = arr[i].split("/*0*/")[2];
                ActiveSet_Map.put(thisK, thisV);
            }
        };

        /*将html中id为setTabLBgStyle的style标签的内容解析为map对象 存储点击状态下a标签的背景图片url*/
        var initGetTabLBgStyle = function () {
            var str = $("#setTabLBgStyle").html();
            var arr = str.split("/*id*/");
            var l = arr.length;
            for (var i = 1; i < l; i++) {
                var thisK = arr[i].split("/*0*/")[0].replace("#", "");
                var thisV = arr[i].split("/*0*/")[2];
                ActiveSet_Map.put(thisK, thisV);
            }
        };

        /*动态创建style标签中的内容，用于动态设置tab中a标签的样式。
         1、构造全局变量ActiveSet_Map，用于存储键值对，目标对象的ID作为键，值以对象的形式存储，包括需要设置的各个属性值：
         六个变量存储在输入框中，每次改变内容时动态变化
         a       color:        $("#tabLitxColor").attr("data-0")  
         a       background:   $("#tabLibgColor").attr("data-0")
         a:hover color：       $("#tabLitxColor").attr("data-1")
         a:hover background:   $("#tabLibgColor").attr("data-1")
         a:link  color:        $("#tabLitxColor").attr("data-2")
         a:link  background:   $("#tabLibgColor").attr("data-2")
         2、设置颜色时，新的键值对直接插入，已有键值对的话，更新值的内容（键是唯一标识，值的内容可变）*/
        function changTabStyle() {
            var $target = LECurrentObject;
            var thisId = $target.attr("id");
            var thisData = {
                normalA: $tabLitxColor.attr("data-0"),
                normalB: $tabLibgColor.attr("data-0"),
                hoverA: $tabLitxColor.attr("data-1"),
                hoverB: $tabLibgColor.attr("data-1"),
                linkA: $tabLitxColor.attr("data-2"),
                linkB: $tabLibgColor.attr("data-2")
            };
            ActiveSet_Map.put(thisId, thisData);
            var tabStr = "";
            ActiveSet_Map.each(function (key, value, index) {
                var tabValue = '/*id*/#' + key + '/*0*/ .ui-tabs-nav   a{color:/*1*/' + value.normalA + '/*1*/;background-color:/*2*/' + value.normalB + '/*2*/;}#' + key + ' .ui-state-hover a:hover{color:/*3*/' + value.hoverA + '/*3*/;background-color:/*1*/' + value.hoverB + '/*1*/;}#' + key + ' .ui-tabs-active a:link{color:/*2*/' + value.linkA + '/*2*/;background-color:/*3*/' + value.linkB + '/*3*/;}\n';
                /*var tabValue ='#'+key+' .ui-tabs-nav   a{color:'+value.normalA+';background:'+value.normalB+';}#'+key+' .ui-state-hover a:hover{color:'+value.hoverA+';background:'+value.hoverB+';}#'+key+' .ui-tabs-active a:link{color:'+value.linkA+';background:'+value.linkB+';}';*/
                tabStr += tabValue;
                $("#setTabStyle").html(tabStr);
            });
        }

        /*字体超链接的初始化颜色 蓝色*/
        var initDefaultColor = function () {
            $("#tabLitxColorPick").spectrum("set", "#0069D6");
        };

        /*单击普通、悬停、点击三个按钮时，输入框颜色切换，值等于input框对应的data-0、data-1、data-2*/
        var initTabEvent = function () {
            $("#tabUl").find("li").bind("click", function () {
                $(this).addClass("activt").siblings().removeClass("activt");
                $tabLitxColor.val($tabLitxColor.attr("data-" + $(this).index()));
                $tabLibgColor.val($tabLibgColor.attr("data-" + $(this).index()));
                $("#tabLitxColorPick").spectrum("set", $tabLitxColor.attr("data-" + $(this).index()));
                $("#tabLibgColorPick").spectrum("set", $tabLibgColor.attr("data-" + $(this).index()));
                LECurrentObject.trigger("click");
            });

        };

        /*修改字体颜色输入框的值,通过正则表达式判断输入的值，格式匹配则更新，否则不更改*/
        var initTxcInputChange = function () {
            $tabLitxColor.bind("change", function () {
                var reg = /^#([0-9a-fA-F]{3}|[0-9a-fA-F]{6})$/;
                if (reg.test($(this).val())) {
                    if ($("#tabNormal").hasClass("activt")) {
                        $tabLitxColor.attr("data-0", $(this).val());
                    } else if ($("#tabHover").hasClass("activt")) {
                        $tabLitxColor.attr("data-1", $(this).val());
                    } else if ($("#tabClick").hasClass("activt")) {
                        $tabLitxColor.attr("data-2", $(this).val());
                    }
                    $("#tabLitxColorPick").spectrum("set", $(this).val());
                    changTabStyle();
                } else {
                    if ($("#tabNormal").hasClass("activt")) {
                        $tabLitxColor.attr("data-0", "#0069D6");
                        $(this).val("#0069D6");
                        $("#tabLitxColorPick").spectrum("set", "#0069D6");
                    } else if ($("#tabHover").hasClass("activt")) {
                        $tabLitxColor.attr("data-1", "#0069D6");
                        $(this).val("#0069D6");
                        $("#tabLitxColorPick").spectrum("set", "#0069D6");
                    } else if ($("#tabClick").hasClass("activt")) {
                        $tabLitxColor.attr("data-2", "#555");
                        $(this).val("#555");
                        $("#tabLitxColorPick").spectrum("set", "#555");
                    }
                    changTabStyle();
                }
            })
        };

        /*修改背景颜色输入框的值，通过正则表达式过滤，匹配则更新，负责不改变*/
        var initBgInputChange = function () {
            $tabLibgColor.bind("change", function () {
                var reg = /^#([0-9a-fA-F]{3}|[0-9a-fA-F]{6})$/;
                if (reg.test($(this).val())) {
                    if ($("#tabNormal").hasClass("activt")) {
                        $tabLibgColor.attr("data-0", $(this).val());
                    } else if ($("#tabHover").hasClass("activt")) {
                        $tabLibgColor.attr("data-1", $(this).val());
                    } else if ($("#tabClick").hasClass("activt")) {
                        $tabLibgColor.attr("data-2", $(this).val());
                    }
                    $("#tabLibgColorPick").spectrum("set", $(this).val());
                    changTabStyle();
                } else {
                    if ($("#tabNormal").hasClass("activt")) {
                        $tabLibgColor.attr("data-0", "#ffffff");
                        $(this).val("#ffffff");
                        $("#tabLibgColorPick").spectrum("set", "#ffffff");
                    } else if ($("#tabHover").hasClass("activt")) {
                        $tabLibgColor.attr("data-1", "#f5f5f5");
                        $(this).val("#f5f5f5");
                        $("#tabLibgColorPick").spectrum("set", "#f5f5f5");
                    } else if ($("#tabClick").hasClass("activt")) {
                        $tabLibgColor.attr("data-2", "#ffffff");
                        $(this).val("#ffffff");
                        $("#tabLibgColorPick").spectrum("set", "#ffffff");
                    }
                    changTabStyle();
                }
            })
        };

        /*设置字体颜色，区分不同状态的a便签进行设置*/
        var initcolorEvent = function () {
            $("#tabLitxColorPick").spectrum(
                LEColorPicker.getOptions(function (tinycolor) {
                    $tabLitxColor.val(tinycolor);
                    if ($("#tabNormal").hasClass("activt")) {
                        $tabLitxColor.attr("data-0", tinycolor);
                    } else if ($("#tabHover").hasClass("activt")) {
                        $tabLitxColor.attr("data-1", tinycolor);
                    } else if ($("#tabClick").hasClass("activt")) {
                        $tabLitxColor.attr("data-2", tinycolor);
                    }
                    changTabStyle();
                })
            );
        };

        /*设置标签背景颜色 区分不同状态的a便签进行设置*/
        var initbackgroundEvent = function () {
            $("#tabLibgColorPick").spectrum(
                LEColorPicker.getOptions(function (tinycolor) {
                    $tabLibgColor.val(tinycolor);
                    if ($("#tabNormal").hasClass("activt")) {
                        $tabLibgColor.attr("data-0", tinycolor);
                    } else if ($("#tabHover").hasClass("activt")) {
                        $tabLibgColor.attr("data-1", tinycolor);
                    } else if ($("#tabClick").hasClass("activt")) {
                        $tabLibgColor.attr("data-2", tinycolor);
                    }
                    changTabStyle();
                })
            );
        };

        function changeTabBgImg() {
            var $target = LECurrentObject;
            var thisId = $target.attr("id");
            var normalUrl = "";
            var hoverUrl = "";
            var linkUrl = "";
            var thisData = {
                nUrl: normalUrl,
                hUrl: hoverUrl,
                lUrl: linkUrl
            };
            tabNBgSet_Map.put(thisId, thisData);
            var tabBgstr = "";
            tabNBgSet_Map.each(function (key, value, index) {
                var tabBgValue = "#" + key + " ul[id^='le_Tabs_'] li[aria-selected='false'] a{background-image:url(" + value.nUrl + ");}#" + key + " ul[id^='le_Tabs_'] li.ui-state-hover a{background-image:url(" + value.hUrl + ");}#" + key + " ul[id^='le_Tabs_'] li[aria-selected='true'] a{background-image:url(" + value.lUrl + ");}\n";
                tabBgstr += tabBgValue;
                $("#setTabNBgStyle").html(tabBgstr);
            });
        }
           /*设置背景图片的过程是通过对map对象的增删改查来实现的*/
        function setBgImg(mapName,dataUrl,keyStr,ID){
            var $target = LECurrentObject;
            var LId = $target.attr("id");
            var LData = "url(" + dataUrl + ")";
            mapName.put(LId, LData);
            var tabLBgstr = "";
            mapName.each(function (key, value, index) {
                var tabLBgValue = "/*id*/#" + key +keyStr+ value + "/*0*/;}";
                tabLBgstr += tabLBgValue;
                $(ID).html(tabLBgstr);
            });
        }

        /*
            设置标签背景图片
            1、如果没有背景图片，调用添加背景图片模态框
            2、如果有背景图片，调用编辑背景图片模态框
            通过判断“普通”、“悬停”、“点击”三个控制按钮的状态，确定给哪个标签添加或修改背景图
        */
        var initTabBgUpload = function () {
            $("#tabBgPreviewImg").click(function (e) {
                var $target = LECurrentObject;
                var keyStrN="/*0*/ ul[id^='le_Tabs_'] li[aria-selected='false'] a{background-image:/*0*/";
                var keyStrH="/*0*/ ul[id^='le_Tabs_'] li.ui-state-hover a{background-image:/*0*/";
                var keyStrL="/*0*/ ul[id^='le_Tabs_'] li[aria-selected='true'] a{background-image:/*0*/";
                        var isNew = $("#tabBgPreviewImg").attr("src") != "export/images/sliderPanel/sliderPanel1.png";
                        if(isNew){
                                var data = [];
                                data.push($("#tabBgPreviewImg").attr("src"));
                                LEDialog.toggleDialog(LE.options["Dialog"].picEditDialog, function (imgList) {
                                        if (imgList.length > 0) {
                                            if ($("#tabNormal").hasClass("activt")) {
                                                setBgImg(tabNBgSet_Map,imgList[0].path,keyStrN,"#setTabNBgStyle");
                                            }else if ($("#tabHover").hasClass("activt")) {
                                                setBgImg(tabHBgSet_Map,imgList[0].path,keyStrH,"#setTabHBgStyle");
                                            } else if ($("#tabClick").hasClass("activt")) {
                                                setBgImg(tabLBgSet_Map,imgList[0].path,keyStrL,"#setTabLBgStyle");
                                            }
                                            LECurrentObject.find("ul").find("li").find("a").css("backgroundSize", "cover");
                                            LECurrentObject.find("ul").find("li").find("a").css("backgroundPosition", "center");
                                            LECurrentObject.trigger("click");
                                        }
                                    },
                                    data
                                );

                        } else{
                            LEDialog.toggleDialog(LE.options["Dialog"].picUploadDialog, function (url) {
                                var $target = LECurrentObject;
                                if ($("#tabNormal").hasClass("activt")) {
                                    setBgImg(tabNBgSet_Map,url,keyStrN,"#setTabNBgStyle");
                                } else if ($("#tabHover").hasClass("activt")) {
                                    setBgImg(tabHBgSet_Map,url,keyStrH,"#setTabHBgStyle");
                                } else if ($("#tabClick").hasClass("activt")) {
                                    setBgImg(tabLBgSet_Map,url,keyStrL,"#setTabLBgStyle");
                                }
                                LECurrentObject.find("ul").find("li").find("a").css("backgroundSize", "cover");
                                LECurrentObject.find("ul").find("li").find("a").css("backgroundPosition", "center");
                                LECurrentObject.trigger("click");
                            });
                        }

               /* LEDialog.toggleDialog(LE.options["Dialog"].picUploadDialog, function (url) {
                    var $target = LECurrentObject;
                    if ($("#tabNormal").hasClass("activt")) {
                        var NId = $target.attr("id");
                        var NData = "url(" + url + ")";
                        tabNBgSet_Map.put(NId, NData);
                        var tabNBgstr = "";
                        tabNBgSet_Map.each(function (key, value, index) {
                            var tabNBgValue = "*//*id*//*#" + key + "*//*0*//* ul[id^='le_Tabs_'] li[aria-selected='false'] a{background-image:*//*0*//*" + value + "*//*0*//*;}";
                            tabNBgstr += tabNBgValue;
                            $("#setTabNBgStyle").html(tabNBgstr);
                        });
                        //LECurrentObject.find("ul[id^='le_Tabs_']").find("li[aria-selected='false']").find("a").css("backgroundImage", "url(" + url + ")");
                    } else if ($("#tabHover").hasClass("activt")) {
                        var HId = $target.attr("id");
                        var HData = "url(" + url + ")";
                        tabHBgSet_Map.put(HId, HData);
                        var tabHBgstr = "";
                        tabHBgSet_Map.each(function (key, value, index) {
                            var tabHBgValue = "*//*id*//*#" + key + "*//*0*//* ul[id^='le_Tabs_'] li.ui-state-hover a{background-image:*//*0*//*" + value + "*//*0*//*;}";
                            tabHBgstr += tabHBgValue;
                            $("#setTabHBgStyle").html(tabHBgstr);
                        });
                        // LECurrentObject.find("ul").find("li").find("a").css("backgroundImage", "url(" + url + ")");
                    } else if ($("#tabClick").hasClass("activt")) {
                        var LId = $target.attr("id");
                        var LData = "url(" + url + ")";
                        tabLBgSet_Map.put(LId, LData);
                        var tabLBgstr = "";
                        tabLBgSet_Map.each(function (key, value, index) {
                            var tabLBgValue = "*//*id*//*#" + key + "*//*0*//* ul[id^='le_Tabs_'] li[aria-selected='true'] a{background-image:*//*0*//*" + value + "*//*0*//*;}";
                            tabLBgstr += tabLBgValue;
                            $("#setTabLBgStyle").html(tabLBgstr);
                        });
                    }
                    // var tabBgstr="ul[id^='le_Tabs_'] li[class*='ui-tabs-active'] a{background-image:url("+url+");}"
                    //$("#setTabBgStyle").html(tabBgstr);
                    //LECurrentObject.find("ul[id^='le_Tabs_']").find("li[class*='ui-tabs-active']").find("a").css("backgroundImage", "url(" + url + ")");
                    // LECurrentObject.find("ul").find("li").find("a").css("backgroundImage", "url(" + url + ")");
                    LECurrentObject.find("ul").find("li").find("a").css("backgroundSize", "cover");
                    LECurrentObject.find("ul").find("li").find("a").css("backgroundPosition", "center");
                    LECurrentObject.trigger("click");
                });*/
            });
            $("#tabBgFileLabel").hover(function () {
                $.trim($("#tabBgPreviewImg").attr("src")) != "export/images/sliderPanel/sliderPanel1.png" && $("#tabBgDeleteImg").show();
            }, function () {
                $("#tabBgDeleteImg").hide();
            });
        };
        /*
        通过对map对象的某条记录进行删除，实现删除背景图片
         */
        function deleteMapStyle(myMap,id,str1,str2){
            var tar = LECurrentObject;
            if(myMap.size() > 0){
                if(myMap.get(tar.attr("id"))){
                    /*如果map对象存在且存储了当前目标对象的属性值，则删除该条记录*/
                    myMap.remove(tar.attr("id"));
                }
                    var tabHBgstr = "";
                    if(myMap.size() > 0){
                        myMap.each(function (key, value, index) {
                            var tabHBgValue = "/*id*/#" + key +str1+ value +str2;
                            tabHBgstr += tabHBgValue;
                            $(id).html(tabHBgstr);
                        });
                    } else{
                        $(id).html("");
                    }

            } else{
                $(id).html("");
            }

        }
        /*
        删除标签栏背景图片：通过判断普通、悬停、点击三个按钮的状态实现对不同状态a标签背景图的删除
        */
        var initDeleteTabImage = function(){
            $("#tabBgDeleteImg").click(function(){
                var keyN="/*0*/ ul[id^='le_Tabs_'] li[aria-selected='false'] a{background-image:/*0*/";
                var keyH="/*0*/ ul[id^='le_Tabs_'] li.ui-state-hover a{background-image:/*0*/";
                var keyL="/*0*/ ul[id^='le_Tabs_'] li[aria-selected='true'] a{background-image:/*0*/";
                var _end='/*0*/;';

                if ($("#tabNormal").hasClass("activt")) {
                    deleteMapStyle(tabNBgSet_Map,"#setTabNBgStyle",keyN,_end);
                }else if ($("#tabHover").hasClass("activt")) {
                    deleteMapStyle(tabHBgSet_Map,"#setTabHBgStyle",keyH,_end);
                } else if ($("#tabClick").hasClass("activt")) {
                    deleteMapStyle(tabLBgSet_Map,"#setTabLBgStyle",keyL,_end);
                }
                $("#tabBgPreviewImg").attr("src", "");

            });
        };
        /*
         点击标签栏模板按钮的同时，更新样式预览处的html。
         */
        function initModelPreview() {
            $modelDiv.find("ul").bind("click", function () {
                $("#modelPreview").html($(this).html());
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
                $("#modelPreview").html($modelDiv.children("ul").eq(2).html());
            } else {
                for (var i = 0; i < l; i++) {
                    var className = $modelDiv.find("ul").eq(i).attr("data-ref");
                    if (tabClassName == className) {
                        $("#modelPreview").html($modelDiv.children("ul").eq(i).html());
                    }
                }

            }
        }


        function activeColorChange(NormalA, HoverA, LinkA, NormalB, HoverB, LinkB) {
            /*将目标对象的属性值赋予input框的data- 确保点击“普通”、“悬停”、“点击”按钮时值得状态正确改变*/
            $tabLitxColor.attr("data-0", NormalA);
            $tabLitxColor.attr("data-1", HoverA);
            $tabLitxColor.attr("data-2", LinkA);
            $tabLibgColor.attr("data-0", NormalB);
            $tabLibgColor.attr("data-1", HoverB);
            $tabLibgColor.attr("data-2", LinkB);

            if ($("#tabNormal").hasClass("activt")) {
                $tabLitxColor.val(NormalA);
                $("#tabLitxColorPick").spectrum("set", NormalA);
                $tabLibgColor.val(NormalB);
                $("#tabLibgColorPick").spectrum("set", NormalB);
            } else if ($("#tabHover").hasClass("activt")) {
                $tabLitxColor.val(HoverA);
                $("#tabLitxColorPick").spectrum("set", HoverA);
                $tabLibgColor.val(HoverB);
                $("#tabLibgColorPick").spectrum("set", HoverB);
            } else if ($("#tabClick").hasClass("activt")) {
                $tabLitxColor.val(LinkA);
                $("#tabLitxColorPick").spectrum("set", LinkA);
                $tabLibgColor.val(LinkB);
                $("#tabLibgColorPick").spectrum("set", LinkB);
            }
        }

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

        /*初始化时获取当前标签样式a链接的颜色*/
        function getModelColor() {
            colorHexFn();
            var $target = LECurrentObject;
            var arr1 = ["#ffffff", "#2DBBC4", "#0069D6"];
            var arr2 = ["#FF4500", "#cccccc", "#f5f5f5"];
            var l = $modelDiv.find("ul").length;
            var tabClassName = $target.find("ul[id^='le_Tabs_']").attr("data-prestyle");
            for (var i = 0; i < l; i++) {
                var className = $modelDiv.find("ul").eq(i).attr("data-ref");
                if (tabClassName == className) {
                    var hTxtColor = arr1[i];
                    var hBgColor = arr2[i];
                }
            }

            var nTxtColor = $target.find("ul[class*=" + tabClassName + "]").find("li[aria-selected='false']").find("a").css("color").colorHex();
            var nBgColor = $target.find("ul[class*=" + tabClassName + "]").find("li[aria-selected='false']").find("a").css("background-color").colorHex();
            //var hTxtColor=$target.find("ul[class*="+tabClassName+"]").find("li[class*='ui-state-hover']").find("a").css("color").colorHex();
            //var hBgColor=$target.find("ul[class*="+tabClassName+"]").find("li[class*='ui-state-hover']").find("a").css("background-color").colorHex();
            var lTxtColor = $target.find("ul[class*=" + tabClassName + "]").find("li[class*='ui-tabs-active']").find("a").css("color").colorHex();
            var lBgColor = $target.find("ul[class*=" + tabClassName + "]").find("li[class*='ui-tabs-active']").find("a").css("background-color").colorHex();
            activeColorChange(nTxtColor, hTxtColor, lTxtColor, nBgColor, hBgColor, lBgColor);
        }

        /*初始化交互样式的按钮状态 字体及背景颜色*/
        var resetActColorStatus = function () {
            var $target = LECurrentObject;
            if (ActiveSet_Map.size() > 0) {
                if (ActiveSet_Map.get($target.attr("id"))) {
                    /*如果map对象存在且存储了当前目标对象的属性值，则读取目标对象属性值*/
                    var activeValue = ActiveSet_Map.get($target.attr("id"));
                    var avtiveNormalA = activeValue.normalA;
                    var avtiveNormalB = activeValue.normalB;
                    var avtiveHoverA = activeValue.hoverA;
                    var avtiveHoverB = activeValue.hoverB;
                    var avtiveLinkA = activeValue.linkA;
                    var avtiveLinkB = activeValue.linkB;
                    activeColorChange(avtiveNormalA, avtiveHoverA, avtiveLinkA, avtiveNormalB, avtiveHoverB, avtiveLinkB);
                } else if ($target.find("ul[id^='le_Tabs_']").attr("data-prestyle")) {
                    getModelColor();
                } else {
                    activeColorChange("#0069D6", "#0069D6", "#555", "#ffffff", "#f5f5f5", "#ffffff");
                }
            } else if ($target.find("ul[id^='le_Tabs_']").attr("data-prestyle")) {
                getModelColor();
            } else {
                activeColorChange("#0069D6", "#0069D6", "#555", "#ffffff", "#f5f5f5", "#ffffff");
            }
        };
        /*初始化时背景图片预览按钮的状态
        普通a标签、点击状态a标签的背景直接通过css属性读取，hover状态a标签的背景图从map对象中读取*/
        var resetTabBackground = function(){
            var tar = LECurrentObject;
            var _imgH="";
            if(tabHBgSet_Map.size() > 0 && tabHBgSet_Map.get(tar.attr("id"))){
                    //如果map对象存在且存储了当前目标对象的属性值，则读取该条记录
                     _imgH=tabHBgSet_Map.get(tar.attr("id"));
            } else{
                _imgH="export/images/sliderPanel/sliderPanel1.png";
            }
            var _imgN = LECurrentObject.find("ul[id^='le_Tabs_'] li[aria-selected='false'] a").css("backgroundImage") == "none" ? "export/images/sliderPanel/sliderPanel1.png" : LECurrentObject.find("ul[id^='le_Tabs_'] li[aria-selected='false'] a").css("backgroundImage");
            var _imgL = LECurrentObject.find("ul[id^='le_Tabs_'] li[aria-selected='true'] a").css("backgroundImage") == "none" ? "export/images/sliderPanel/sliderPanel1.png" : LECurrentObject.find("ul[id^='le_Tabs_'] li[aria-selected='true'] a").css("backgroundImage");
            if(_imgN){
                _imgN = _imgN.replace(/url\([\"\']?/g,"").replace(/[\"\']?\)/g,"");
            }
            if(_imgH){
                _imgH = _imgH.replace(/url\([\"\']?/g,"").replace(/[\"\']?\)/g,"");
            }
            if(_imgL){
                _imgL = _imgL.replace(/url\([\"\']?/g,"").replace(/[\"\']?\)/g,"");
            }
            if ($("#tabNormal").hasClass("activt")) {
                _imgN && $("#tabBgPreviewImg").attr("src", _imgN);
            } else if ($("#tabHover").hasClass("activt")) {
                _imgH && $("#tabBgPreviewImg").attr("src", _imgH);
            } else if ($("#tabClick").hasClass("activt")) {
                _imgL && $("#tabBgPreviewImg").attr("src", _imgL);
            }
        };
        return {
            init: function () {
                initTabEvent();
                initTxcInputChange();
                initBgInputChange();
                initcolorEvent();
                initbackgroundEvent();
                initGetActiveStyle();
                initDefaultColor();
                initMoreModelShow();
                initTabBgUpload();
                initGetTabNBgStyle();
                initGetTabHBgStyle();
                initGetTabLBgStyle();
                initModelPreview();
                initDeleteTabImage();

                //console.info("PageSetting init")
            },
            run: function (options) {
                resetActColorStatus();
                resetModelPreview();
                resetTabBackground();

                //console.info("PageSetting run")
                $panel.show();
            },
            destroy: function () {
                //console.info("PageSetting destroy")
                $panel.hide();
            }
        };
    };
})(window, jQuery, LE, undefined);