/**
 * Created by isaac_gu on 2016/1/14.
 */
var navManage = null;
(function(window, $, LE){
    LE.stylesheets["NavManage"] = function(){
        var tabManager = null;
        //当前功能面板的对象
        var $PS = $("#navManageSection");
        var $tabUl = $("#nmTabs");
        var $addBtn = $("#addnmTabBtn");
        var $mainModelMoreBox = $("#mainModelMoreBox");
        var $navDialog=$("#navDialog");
        var $navConfirmBtn=$("#navConfirmBtn");
        var $navNameText=$("#navNameText");
        var $navLinkText=$("#navLinkText");

        var initTabLiEvent = function(){
            $tabUl.on({
                dblclick: function(e){
                    e.preventDefault();
                    e.stopPropagation();
                    $(this).children(".editarea").attr("contenteditable", true).focus();
                    LEReset.MoveCunsorToEnd($(this).children(".editarea"));
                },
                blur: function(e){
                    e.preventDefault();
                    e.stopPropagation();
                    $(this).children(".editarea").attr("contenteditable", false);
                    var _id = $(this).data("refid");
                    $("#" + _id).find("div").text($(this).children(".editarea").text());

                    LEHistory.trigger();
                }
            }, "li");
        };



        var initTabSpanEvent = function(){
            $tabUl.on({
                keypress: function(e){
                    if(e.keyCode == 13){
                        $(this).attr("contenteditable", false);
                        var _id = $(this).parent().data("refid");
                        $("#" + _id).find("div").text($(this).text());
                        LEHistory.trigger();
                    }
                }
            }, ".editarea");

        };

        var initEditBtnEvent = function(){
            $tabUl.on({
                click: function(e){
                   // $(this).parent().siblings(".editarea").attr("contenteditable", true).focus();
                    var _this=$(this);
                    $navDialog.modal("show");
                    var _id = _this.parent().parent().data("refid");
                    $navConfirmBtn.attr("data-id",_id);
                    _this.attr("id","navEditing");
                    var navName=_this.parent().parent().find(".editarea").eq(0).text();
                    var navLink=$("#" + _id).attr("href");
                    $navNameText.val(navName);
                    $navLinkText.val(navLink);
                }
            }, ".nmedit");

            $("#navDivClose").click(function () {
                $navDialog.modal("hide");
            });

            $navConfirmBtn.click(function () {
                var _id = $(this).attr("data-id");
                var navName=$navNameText.val();
                var navLink=$navLinkText.val();
                $("#" + _id).find("div").text(navName);
                $("#navEditing").parent().parent().find(".editarea").eq(0).text(navName);
                $("#" + _id).attr("href",navLink);
                $("#navEditing").attr("id","");

                LEHistory.trigger();

                $navDialog.modal("hide");
                })
        };

        var initCleanBtnEvent = function(){
            $tabUl.on({
                click: function(e){
                    var _$parent = $(this).parent().parent();
                    var _aId = _$parent.attr("data-refid");
                    var _$a = $("#" + _aId);
                    var _$aUl = _$a.parent().parent();
                    if(_$aUl.find("li").size() > 1){
                        _$a.parent().remove();
                        _$parent.remove();
                        /*if(!_$aUl.hasClass("dropdown-menu")){
                         _$aUl.find("li:first").addClass("active");
                         }*/
                    }else if(_$aUl.find("li").size() == 1){
                        if(_$a.parent().parent().hasClass("dropdown-menu")){
                            //删除的是最后一个子导航
                            _$a.parent().parent().parent().removeClass("dropdown-ul");
                            _$a.parent().parent().prev("a").children("div").find("span").remove();
                            _$a.parent().parent().prev("a").removeClass("dropdown-toggle");
                            _$a.parent().parent().prev("a").attr("data-toggle", "");
                            _$a.parent().parent().prev("a").attr("role", "");
                            _$a.parent().parent().prev("a").attr("aria-haspopup", "");
                            _$a.parent().parent().prev("a").attr("aria-expanded", "");
                            _$a.parent().parent().remove();
                            _$parent.remove();
                            window.LEStyle.destroy("SubMenu");
                        }else{
                            //删除的是最后一个主导航
                            LECurrentObject.html("");
                            _$parent.remove();
                            window.LEStyle.destroy("MainMenu");

                        }
                        resetTabs()

                    }
                    LEHistory.trigger();
                }
            }, ".nmclean");
        };
        var initFlagBtnEvent = function(){
            $tabUl.on({
                click: function(e){
                    var _$parent = $(this).parent();
                    var _aId = _$parent.attr("data-refid");
                    var _$a = $("#" + _aId);
                    var _$aUl = _$a.parent().parent();
                    if(_$aUl.find("li").size() > 1){
                        if($(this).hasClass("flagHover")){
                            _$a.parent().addClass("active");
                            $(this).addClass("flagActive").removeClass("flagHover");
                        } else if($(this).hasClass("flagActive")){
                            _$a.parent().removeClass("active");
                            $(this).addClass("flagHover").removeClass("flagActive");
                        }
                    }
                    LEHistory.trigger();
                }
            }, ".flag");
        };

        var initAddTabBtn = function(){
            /**
             * 增加一个标签以及一个div，增加一个项
             */
            $addBtn.click(function(){
                var data = getData();
                LEDialog.toggleDialog(LE.options["Dialog"].navigationDialog, function(json){
                        addItems(json);
                        LEHistory.trigger();
                    },
                    data
                );


            });

            $tabUl.on({
                mouseover: function(e){
                    e.preventDefault();
                    e.stopPropagation();
                    $(this).children(".toolspan").show();
                    $(this).children(".flag").show();


                },
                mouseout: function(e){
                    e.preventDefault();
                    e.stopPropagation();
                    $(this).children(".toolspan").hide();
                    if($(this).children(".flag").hasClass("flagHover")){
                        $(this).children(".flag").hide();
                    }
                }
            }, "li");

            $tabUl.on({
                click: function(e){
                    var data = getData();
                    var $this = $(this);
                    var _itemId = $(this).parent().parent().attr("data-itemid");
                    var _aId = $(this).parent().parent().attr("data-refid");
                    LEDialog.toggleDialog(LE.options["Dialog"].navigationDialog, function(json){
                            //如果li下没有ul 添加 如果有 追加
                            if($this.parent().parent().children().length == 3){
                                addSubItem(json, _aId, _itemId);
                            } else{
                                //把子导航菜单padding和高度带到追加后
                                var PdLeft =LECurrentObject.find(".nav-pills li ul.dropdown-menu li a").css("padding-left");
                                var PdRight = LECurrentObject.find(".nav-pills li ul.dropdown-menu li a").css("padding-right");
                                var Height= LECurrentObject.find(".nav-pills li ul.dropdown-menu li a div").css("height");
                                appendSubItem(json, _aId, _itemId);
                                LECurrentObject.find(".nav-pills li ul.dropdown-menu li a").css("padding-left",PdLeft);
                                LECurrentObject.find(".nav-pills li ul.dropdown-menu li a").css("padding-right",PdRight);
                                LECurrentObject.find(".nav-pills li ul.dropdown-menu li a div").css("height",Height);

                            }
                            LEHistory.trigger();
                        },
                        data
                    );

                }
            }, ".nmplus");

        };

        function getData(){
            var ids = [];
            $tabUl.children("li").each(function(){
                var _id = $(this).attr("data-itemid");
                ids.push(_id);
            });
            return ids;
        }

        var timer = null;
        var initDrapMenu = function(){
            $("#container").on({
                mouseover: function(e){
                    clearTimeout(timer);
                    $(".dropdown-ul").removeClass("open");
                    $(this).addClass("open");
                },
                mouseout: function(e){
                    var _this = $(this);
                    clearTimeout(timer);
                    timer = setTimeout(function(){
                        _this.removeClass("open");
                    }, 500);

                }
            }, ".dropdown-ul");
        };

        var initReselectEvent = function(){
            $("#nmReselect").click(function(e){
                var data = getData();
                LEDialog.toggleDialog(LE.options["Dialog"].navigationDialog, function(json){
                        reselectItems(json);
                        LEHistory.trigger();
                    },
                    data
                );

            });
        };
        /**
         * 追加主导航*/
        var initAppendEvent = function(){
            $("#nmAppend").click(function(){
                var data = getData();
                LEDialog.toggleDialog(LE.options["Dialog"].navigationDialog, function(json){
                        appendItems(json);
                        LEHistory.trigger();
                    },
                    data
                );


            });
        };

        function appendItems(json){
            if(json.length == 0) return;
            var _$con = LECurrentObject.children("ul.le-nav-ul");

            var _ha = [];
            var _id = LECurrentObject.attr("id");
            _id += "_ul";
            var _liHtml = LE.options.Navigation.liHtml;
            for(var i = 0, len = json.length; i < len; i++){
                _ha.push(getLiHtmlAppend(_liHtml, json[i], _id, i));
            }
            //把主导航菜单padding和高度带到追加后
            var menuPadLeft = LECurrentObject.find(".nav-pills > li > a").css("padding-left");
            var menuPadRight = LECurrentObject.find(".nav-pills > li > a").css("padding-right");
            var menuHeight = LECurrentObject.find(".nav-pills > li > a > div").css("height");

            _$con.append(_ha.join(""));

            LECurrentObject.find(".nav-pills > li > a").css("padding-left",menuPadLeft);
            LECurrentObject.find(".nav-pills > li > a").css("padding-right",menuPadRight);
            LECurrentObject.find(".nav-pills > li > a > div").css("height",menuHeight);
            resetTabs();
        }

        function getLiHtmlAppend(_liHtml, json, _id, i){
            //追加的主导航id  通过时间戳 + i
            var timeStamp = new Date().getTime();
            //alert(_id + "_a_" +LEDrag.getNextId());
            return _liHtml.replace(/#\{link\}/g, json.link).replace(/#\{title\}/g, json.title)
                .replace(/#\{aId\}/g, _id + "_a_" + timeStamp + i).replace(/#\{refId\}/g, json.id);
        }

        /**
         * 向
         * @param json
         * @param _id
         */
        function addSubItem(json, _aId, _itemId){
            var $a = $("#" + _aId);
            //<span class="caret"></span>
            //加向下的箭头
            if(!$a.children("div").find("span").hasClass("caret")){
                $a.children("div").append('<span class="caret"></span>');
            }
            if($a.siblings("ul").size() == 0){
                $a.parent().append('<ul class="dropdown-menu" aria-labelledby="#{aId}"></ul>'.replace(/#\{aId\}/g, _aId));
            }
            var _h = [];
            //添加ul

            var _liHtml = LE.options.Navigation.liHtml;
            for(var i = 0, len = json.length; i < len; i++){
                _h.push(_liHtml.replace(/#\{link\}/g, json[i].link).replace(/#\{title\}/g, json[i].title)
                    .replace(/#\{aId\}/g, _aId + "_a_" + i).replace(/#\{refId\}/g, json[i].id));
            }

            addAttrsForA($a);

            $a.parent().addClass("dropdown-ul");
            //添加尾巴
            $a.siblings("ul").html(_h.join(""));
            resetTabs();
            var options = {
                object: LECurrentObject
            };
            LEStyle.run("MainMenu", options, true).run("SubMenu", options, true);

        }

        //追加子导航
        function appendSubItem(json, _aId, _itemId){
            var $a = $("#" + _aId);
            var _h = [];
            //添加ul
            var _liHtml = LE.options.Navigation.liHtml;
            for(var i = 0, len = json.length; i < len; i++){
                var timeStamp = new Date().getTime();
                _h.push(_liHtml.replace(/#\{link\}/g, json[i].link).replace(/#\{title\}/g, json[i].title)
                    .replace(/#\{aId\}/g, _aId + "_a_" + timeStamp + i).replace(/#\{refId\}/g, json[i].id));
            }
            $a.siblings("ul").append(_h.join(""));
            resetTabs();
        }

        function addAttrsForA($a){
            $a.addClass("dropdown-toggle");
            $a.attr("data-toggle", "dropdown");
            $a.attr("role", "button");
            $a.attr("aria-haspopup", "true");
            $a.attr("aria-expanded", "false");
        }

        /**
         * 点击添加按钮的时候，先添加显示的li 然后再调用reset方法
         * @param json
         */
        function addItems(json){
            if(json.length == 0) return;

            var _$con = LECurrentObject.filter(".le-nav");
            var _id = LECurrentObject.attr("id");
            _id += "_ul";
            var _ha = [];
            _ha.push('<ul id="#{ulId}" class="le-nav-ul nav nav-pills">'.replace(/#\{ulId\}/g, _id));
            var _liHtml = LE.options.Navigation.liHtml;
            for(var i = 0, len = json.length; i < len; i++){
                _ha.push(getLiHtml(_liHtml, json[i], _id, i));
            }
            _ha.push("</ul>");
            _$con.html(_ha.join(""));
            _$con.find(".le-nav-ul").children("li:first").addClass("active");

            resetTabs();

            var options = {
                object: LECurrentObject
            };
            LEStyle.run("MainMenu", options, true).run("SubMenu", options, true);

        }

        function getLiHtml(_liHtml, json, _id, i){
            return _liHtml.replace(/#\{link\}/g, json.link).replace(/#\{title\}/g, json.title)
                .replace(/#\{aId\}/g, _id + "_a_" + i).replace(/#\{refId\}/g, json.id);
        }

        //重选 - 根据json
        /**
         * 根据json，来确定第一层
         * 1.判断json：如果有
         *
         * @param json
         */
        function reselectItems(jsonArray){
            var oldArr = getOldIdArray();  //li data-itemid
            var newArr = getNewIdArray(jsonArray); // json  id
            var addArr = getCuArray(oldArr, newArr);
            var deleteArr = getCuArray(newArr, oldArr);
            var _$ul = LECurrentObject.children("ul");
            for(var i in addArr){
                var json = getJson(addArr[i], jsonArray);
                _$ul.append(getLiHtml(LE.options.Navigation.liHtml, json, LECurrentObject.attr("id"), utils.getId()));
            }
            for(var i in deleteArr){
                _$ul.find("a[data-itemid='" + deleteArr[i] + "']").parent().remove();
            }
            LECurrentObject.filter(".le-nav").find(".le-nav-ul").children("li:first").addClass("active");
            resetTabs();
        }

        function getJson(_id, arr){
            _id += "";
            for(var i in arr){
                if(_id == arr[i].id){
                    return arr[i];
                }
            }
        }

        function getNewIdArray(jsonArray){
            var arr = [];
            for(var i in jsonArray){
                arr.push(jsonArray[i].id + "");
            }
            return arr;
        }

        function getOldIdArray(){
            var arr = [];
            $tabUl.children("li").each(function(e){
                arr.push($(this).attr("data-itemid") + "");
            });
            return arr;
        }

        function getCuArray(array, targetArray){
            var a = [];
            for(var i in targetArray){
                if(!utils.contains(targetArray[i], array)){
                    a.push(targetArray[i]);
                }
            }
            return a;
        }

        function getTabOption(tabName, refId, itemId, hasAdd){
            /* var _html = [
             '<li class="padl20 setHeight btn-hint relative" data-refid="' + refId + '" data-itemid="' + itemId + '">',
             '<img class="flag absHover" src="export/images/sliderBar/sliderBar34.png" alt="" />'+
             '<img class="flag absActive" src="export/images/sliderBar/sliderBar35.png" alt="" />'+
             '<span class="span124 mgl24 editarea">' + tabName + '</span>',
             '<span class="pull-right mgr15 toolspan">'];
             if(hasAdd){
             _html.push('<span class="glyphicon glyphicon-plus mgr10 cursor nmplus"></span>');
             }*/

            var _html = [
                '<li class="padl20 setHeight btn-hint relative" data-refid="' + refId + '" data-itemid="' + itemId + '">'
            ];
            if(hasAdd){
                _html.push('<em class="flag flagHover"></em>');
            }
            _html.push('<span class="span124 mgl24 editarea">' + tabName + '</span>' +
                '<span class="pull-right mgr15 toolspan">');

            if(hasAdd){
                _html.push('<span class="glyphicon glyphicon-plus mgr10 cursor nmplus"></span>');
            }


            _html.push('<span class="glyphicon glyphicon-pencil mgr10 cursor nmedit"></span>');
            _html.push('<span class="glyphicon glyphicon-trash cursor nmclean"></span>');
            _html.push('</span>');
            _html.push('</li>');

            return _html;
        }

        /**
         * 需要
         */
        var resetTabs = function(){
            var $target = LECurrentObject;
            //有东西的时候
            if($target.find(".le-nav-ul").size() > 0){
                $tabUl.html('');
                $target.find(".le-nav-ul").children("li").children("a").each(function(){
                    var _$this = $(this);
                    var _h = getTabOption(_$this.text(), _$this.attr("id"), _$this.attr("data-itemid"), true);
                    //判断是否有子菜单,有的话就粘贴
                    if(_$this.parent().children("ul").hasClass("dropdown-menu")){
                        _h.pop();
                        _h.push("<ul class='dropdown'>");
                        _$this.parent().children("ul").filter(".dropdown-menu").find("a").each(function(){
                            _h.push(getTabOption($(this).text(), $(this).attr("id"), $(this).attr("data-itemid")).join(""));
                        });
                        _h.push("</ul>");
                        //_h.push("<li>");
                    }
                    $tabUl.append(_h.join(""));
                    //$tabUl.find(".dropdown").find(".span148").addClass("span128").removeClass("span148");

                });

                var leng = $target.find(".le-nav-ul").children().length;
                for(var i = 0; i < leng; i++){
                    $("#nmTabs").children().eq(i).find("em.flag").hide();
                    if($target.find(".le-nav-ul").children().eq(i).hasClass("active")){
                        $("#nmTabs").children().eq(i).find("em.flag").addClass("flagActive").removeClass("flagHover");
                        $("#nmTabs").children().eq(i).find("em.flagActive").show();
                    }
                    $("#nmTabs").children().eq(i).find("em.flagHover").hide();
                }

                $tabUl.show();
                $addBtn.hide();
                $mainModelMoreBox.show();
                $("#nmReselect").show();
                $("#nmAppend").show();
                if($target.find(".dropdown-menu").size() > 0){
                    $("#subModelMoreBox").show();
                } else{
                    $("#subModelMoreBox").hide();
                }
            } else{
                $tabUl.hide();
                $addBtn.show();
                $mainModelMoreBox.hide();
                $("#subModelMoreBox").hide();
                $("#nmReselect").hide();
                $("#nmAppend").hide();
            }
        };


        return {
            //onload
            init: function(){

               // initTabLiEvent();
               // initTabSpanEvent();
                initEditBtnEvent();
                initCleanBtnEvent();
                initAddTabBtn();
                initDrapMenu();
                initReselectEvent();
                initFlagBtnEvent();
                initAppendEvent();
                /* initChangeStyleBlock();*/
            },
            run: function(options, doHide, doSlide){
                resetTabs();
                //切换组件时，样式设置部分回顶部
                sliderbarToTop();
                //var tm = new TabManager($("#nmTabs"));
                //this.tabManager.resetTabs();
                LEDisplay.show($PS, doHide, doSlide);
            },
            destroy: function(){
                //把对齐按钮恢复成原样
                $PS.hide();
                $("#modelDiv").animate({right: "-250px"}, "100", "linear");
            }
        };
    }
    ;
})
(window, jQuery, LE, undefined);

//导航菜单的json
nmJson = [
    {
        id: "6788",
        link: "http://localhost:8080/xy/xy/article/View.do?DocIDs=6788&DocLibID=1",
        title: "北京"
    },
    {
        id: "6782",
        link: "http://localhost:8080/xy/xy/article/Article.do?DocLibID=1&DocIDs=6782&FVID=1&UUID=1463102704004&siteID=1&colID=1&ch=0",
        title: "Chromebook "
    },
    {
        id: 6677,
        link: "http://www.baidu.com",
        title: "百度"
    },
    {
        id: 6435,
        link: "http://localhost:8080/xy/xy/article/View.do?DocIDs=6435&DocLibID=1",
        title: "郭明錤"
    }
];

reselectJson = [
    {
        id: "6788",
        link: "http://localhost:8080/xy/xy/article/View.do?DocIDs=6788&DocLibID=1",
        title: "北京"
    },
    {
        id: 6677,
        link: "http://www.baidu.com",
        title: "百度"
    },
    {
        id: 6435,
        link: "http://localhost:8080/xy/xy/article/View.do?DocIDs=6435&DocLibID=1",
        title: "郭明錤"
    },
    {
        id: "6783",
        link: "http://localhost:8080/xy/xy/article/Article.do?DocLibID=1&DocIDs=6782&FVID=1&UUID=1463102704004&siteID=1&colID=1&ch=0",
        title: "德云社 "
    }
];
