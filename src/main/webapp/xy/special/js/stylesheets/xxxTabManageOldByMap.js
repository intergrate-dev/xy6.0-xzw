/**
 * Created by isaac_gu on 2016/1/14.文件名带Old及XXX的为不用的js
 */
(function(window, $, LE){
    LE.stylesheets["TabManage"] = function(){
        //当前功能面板的对象
        var $PS = $("#tabManageSection");
        var $tabUl = $("#tmTabs");

        var initTabLiEvent = function(){
            $tabUl.on({
                dblclick: function(e){
                    $(this).find(".editarea").attr("contenteditable", true).focus();
                },
                blur: function(e){
                    $(this).find(".editarea").attr("contenteditable", false);
                    var _id = $(this).data("refid");
                    $("#" + _id).text($(this).find("span").text());
                }
            }, "li");
        };

        var initTabSpanEvent = function(){
            $tabUl.on({
                keypress: function(e){
                    if(e.keyCode == 13){
                        $(this).attr("contenteditable", false);
                        var _id = $(this).parent().data("refid");
                        $("#" + _id).text($(this).text());
                    }
                }
            }, ".editarea");

        };

        var initEditBtnEvent = function(){
            $tabUl.on({
                click: function(e){
                    $(this).parent().siblings(".editarea").attr("contenteditable", true).focus();
                }
            }, ".tmedit");
        };

        var initCleanBtnEvent = function(){
            $tabUl.on({
                click: function(e){
                    var _$parent = $(this).parent().parent();
                    var _aId = _$parent.data("refid");
                    var _$a = $("#" + _aId);
                    var _divId = _$a.attr("href");
                    var _$aUl = _$a.parent().parent();
                    if(_$aUl.find("li").size() > 1){
                        $(_divId).remove();
                        _$a.parent().remove();
                        _$parent.remove();
                        _$aUl.find("a:first").click();
                        _$aUl.parent().tabs("refresh");
                    }
                }
            }, ".tmclean");
        };

        var initAddTabBtn = function(){
            /**
             * 增加一个标签以及一个div，增加一个项
             */
            $("#addTabBtn").click(function(){
                if(LECurrentObject.hasClass("le-nav")){
                    var data = [6622, 6798, 6369];
                    LEDialog.toggleDialog(LE.options["Dialog"].thumbListDialog, function(json){
                            console.info(json);
                        },
                        data
                    );
                } else{
                    var _$con = LECurrentObject.filter(".le-tabs");
                    var _$ul = _$con.find(".le-tabs-ul");
                    var _liHtml = LE.options.Tabs.liHtml;
                    var _divHtml = LE.options.Tabs.divHtml;
                    var _date = new Date();
                    var _divId = _$con.attr("id") + "_" + _date.getTime();
                    _$ul.append(_liHtml.replace(/#\{divId\}/g, "#" + _divId).replace(/#\{name\}/g, "标签 " + (_$ul.find("li").size() + 1)));
                    _$con.append(_divHtml.replace(/#\{id\}/g, _divId));
                    _$con.tabs("refresh");
                    LEDrag.columnDraggable();
                    resetTabs();
                    $tabUl.find("li:last").dblclick();
                }


            });
        };

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
         点击标签样式按钮
         为目标对象添加class 同时去掉map构造的行间样式 每次添加记住class 下次点击先移除 再添加
         */
        var initChangeStyleBlock = function(){
            //$PS.find(".displayStyle").click(function(e){
            $("#modelDiv").find(".displayStyle").click(function(e){
                var tar = LECurrentObject;
                /*
                清除map构造的当前对象的字体颜色及背景颜色样式（删除map中当前对象的记录）
                 */
                if(ActiveSet_Map.size() > 0 ){
                    if(ActiveSet_Map.get(tar.attr("id"))){
                        //如果map对象存在且存储了当前目标对象的属性值，则删除该条记录
                        ActiveSet_Map.remove(tar.attr("id"));
                    }
                        var tabStr = "";
                        if(ActiveSet_Map.size() > 0){
                            ActiveSet_Map.each(function(key, value, index){
                                var tabValue = '/*id*/#' + key + '/*0*/ .ui-tabs-nav   a{color:/*1*/' + value.normalA + '/*1*/;background-color:/*2*/' + value.normalB + '/*2*/;}#' + key + ' .ui-state-hover a:hover{color:/*3*/' + value.hoverA + '/*3*/;background-color:/*1*/' + value.hoverB + '/*1*/;}#' + key + ' .ui-tabs-active a:link{color:/*2*/' + value.linkA + '/*2*/;background-color:/*3*/' + value.linkB + '/*3*/;}\n';
                                tabStr += tabValue;
                                $("#setTabStyle").html(tabStr);
                            });
                        } else{
                            $("#setTabStyle").html("");
                        }
                } else{
                    $("#setTabStyle").html("");
                }
                /*
                 清除map构造的当前对象的普通a标签背景图（删除map中当前对象的记录）
                 */
                var keyN="/*0*/ ul[id^='le_Tabs_'] li[aria-selected='false'] a{background-image:/*0*/";
                var keyH="/*0*/ ul[id^='le_Tabs_'] li.ui-state-hover a{background-image:/*0*/";
                var keyL="/*0*/ ul[id^='le_Tabs_'] li[aria-selected='true'] a{background-image:/*0*/";
                var _end='/*0*/;';
                deleteMapStyle(tabNBgSet_Map,"#setTabNBgStyle",keyN,_end);
                /*
                 清除map构造的当前对象的hover a标签背景图（删除map中当前对象的记录）
                 */
                deleteMapStyle(tabHBgSet_Map,"#setTabHBgStyle",keyH,_end);
                /*
                 清除map构造的当前对象的link点击状态下 a标签背景图（删除map中当前对象的记录）
                 */
                deleteMapStyle(tabLBgSet_Map,"#setTabLBgStyle",keyL,_end);
                /*
                 为目标对象添加class 每次添加记住class 下次点击先移除 再添加
                 */
                var _$this = $(this);
                var $target = LECurrentObject.find("ul");
                var _style = _$this.data("ref");
                $target.removeClass($target.attr("data-prestyle"));
                $target.attr("data-prestyle", _style);
                $target.addClass(_style);
                LECurrentObject.trigger("click");
            });
        };

        function getTabOption(tabName, refId){
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

        var resetTabs = function(){
            var $target = LECurrentObject;
            $tabUl.html('');
            $target.find(".le-tabs-ul").find("a").each(function(){
                $tabUl.append(getTabOption($(this).text(), $(this).attr("id")));
            });
        };

        return {
            //onload
            init: function(){
                initTabLiEvent();
                initTabSpanEvent();
                initEditBtnEvent();
                initCleanBtnEvent();
                initAddTabBtn();
                initChangeStyleBlock();
            },
            run: function(options){
                resetTabs();
                $PS.show();
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