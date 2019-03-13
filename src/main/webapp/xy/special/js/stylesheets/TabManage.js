/**
 * Created by isaac_gu on 2016/1/14.
 */
(function (window, $, LE) {
    LE.stylesheets["TabManage"] = function () {
        //当前功能面板的对象
        var $PS = $("#tabManageSection");
        var $tabUl = $("#tmTabs");

        var initTabLiEvent = function () {
            $tabUl.on({
                dblclick: function (e) {
                    $(this).find(".editarea").attr("contenteditable", true).focus();
                    LEReset.MoveCunsorToEnd($(this).children(".editarea"));
                },
                blur: function (e) {
                    $(this).find(".editarea").attr("contenteditable", false);
                    var _id = $(this).data("refid");
                    $("#" + _id).children("div").text($(this).find("span").text());
                    LEHistory.trigger();
                    return false;
                },
                paste :function(){
                    return false;
                }
            }, "li");
        };

        var initTabSpanEvent = function () {
            $tabUl.on({
                keypress: function (e) {
                    if (e.keyCode == 13) {
                        $(this).attr("contenteditable", false);
                        var _id = $(this).parent().data("refid");
                        $("#" + _id).children("div").text($(this).text());
                        LEHistory.trigger();
                    }
                }
            }, ".editarea");

        };

        var initEditBtnEvent = function () {
            $tabUl.on({
                click: function (e) {
                    $(this).parent().siblings(".editarea").attr("contenteditable", true).focus();
                }
            }, ".tmedit");
        };

        var initCleanBtnEvent = function () {
            $tabUl.on({
                click: function (e) {
                    var _$parent = $(this).parent().parent();
                    var _aId = _$parent.data("refid");
                    var _$a = $("#" + _aId);
                    var _divId = _$a.attr("href");
                    var _$aUl = _$a.parent().parent();
                    if (_$aUl.find("li").size() > 1) {
                        $(_divId).remove();
                        _$a.parent().remove();
                        _$parent.remove();
                        _$aUl.find("a:first").click();
                        _$aUl.parent().tabs("refresh");
                    }
                    LEHistory.trigger();
                }
            }, ".tmclean");
        };

        var initAddTabBtn = function () {
            /**
             * 增加一个标签以及一个div，增加一个项
             */
            $("#addTabBtn").click(function () {
                var _$con = LECurrentObject.filter(".le-tabs");
                var _$ul = _$con.find(".le-tabs-ul");
                var _liHtml = LE.options.Tabs.liHtml;
                var _divHtml = LE.options.Tabs.divHtml;
                var _date = new Date();
                var _divId = _$con.attr("id") + "_" + _date.getTime();
                _$ul.append(_liHtml.replace(/#\{divId\}/g, "#" + _divId).replace(/#\{name\}/g, "新标签").replace(/#\{aId\}/g, "le_tabs_a_" + LEDrag.getNextId()));
                _$con.append(_divHtml.replace(/#\{id\}/g, _divId));
                _$con.tabs("refresh");

                LEDrag.columnDraggable();
                resetTabs();
                $tabUl.find("li:last").trigger("dblclick");

                var currentWidth = _$ul.children().eq(0).find("div").css("width");
                var currentHeight = _$ul.children().eq(0).find("div").css("height");
                _$ul.find("div").css("width", currentWidth);
                _$ul.find("div").css("height", currentHeight);


            });
        };


        /*
         点击标签样式按钮
         为目标对象添加class 同时去掉map构造的行间样式 每次添加记住class 下次点击先移除 再添加
         */
        var initChangeStyleBlock = function () {
            //$PS.find(".displayStyle").click(function(e){
            $("#modelDiv").find(".displayStyle").click(function (e) {
                var tarId = LECurrentObject.attr("id");
                var removeId = "#" + tarId + " ";
                //清除style标签中的内容
                StyleManager.removeStyle(removeId);

                //标签border相关data-样式还原
                LECurrentObject.attr("data-6-radius", "0px");
                LECurrentObject.attr("data-7-radius", "0px");
                LECurrentObject.attr("data-8-radius", "0px");

                LECurrentObject.attr("data-6-style", "none");
                LECurrentObject.attr("data-7-style", "none");
                LECurrentObject.attr("data-8-style", "none");

                LECurrentObject.attr("data-6-width", "3px");
                LECurrentObject.attr("data-7-width", "3px");
                LECurrentObject.attr("data-8-width", "3px");

                LECurrentObject.attr("data-6-color", "rgb(51, 51, 51)");
                LECurrentObject.attr("data-7-color", "rgb(51, 51, 51)");
                LECurrentObject.attr("data-8-color", "rgb(51, 51, 51)");

                LECurrentObject.attr("data-6-position", "border");
                LECurrentObject.attr("data-7-position", "border");
                LECurrentObject.attr("data-8-position", "border");

                /*
                 为目标对象添加class 每次添加记住class 下次点击先移除 再添加
                 */
                var _$this = $(this);
                var $target = LECurrentObject.children("ul");
                var _style = _$this.data("ref");
                $target.removeClass($target.attr("data-prestyle"));
                $target.attr("data-prestyle", _style);
                $target.addClass(_style);
                LEHistory.trigger();
                LECurrentObject.trigger("click");
            });
        };

        function getTabOption(tabName, refId) {
            var _html = [
                    '<li class="padl20 setHeight btn-hint" data-refid="' + refId + '">',
                    '<span class="span160 editarea">' + tabName + '</span>',
                '<span class="pull-right mgr15">',
                '<span class="glyphicon glyphicon-pencil mgr10 cursor tmedit"></span>',
                '<span class="glyphicon glyphicon-trash cursor tmclean"></span>',
                '</span>',
                '</li>'
            ];
            return _html.join("");
        }

        var resetTabs = function () {
            var $target = LECurrentObject;
            $tabUl.html('');
            $target.find(".le-tabs-ul").find("a").each(function () {
                $tabUl.append(getTabOption($(this).text(), $(this).attr("id")));
            });
        };

        return {
            //onload
            init: function () {
                initTabLiEvent();
                initTabSpanEvent();
                initEditBtnEvent();
                initCleanBtnEvent();
                initAddTabBtn();
                initChangeStyleBlock();
            },
            run: function (options, doHide) {
                resetTabs();
                //切换组件时，样式设置部分回顶部
                sliderbarToTop();
                LEDisplay.show($PS, doHide);
            },
            destroy: function () {
                //把对齐按钮恢复成原样
                $PS.hide();
                $("#modelDiv").animate({right: "-250px"}, "fast", "linear");
                $("#modelGoBack").animate({right: "-250px"}, "fast", "linear");
            }
        };
    }
    ;
})
(window, jQuery, LE, undefined);