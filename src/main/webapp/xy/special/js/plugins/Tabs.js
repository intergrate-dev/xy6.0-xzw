/**
 * Created by isaac_gu on 2016/1/6.
 */
(function(window, $, LE){
    LETabs = LE.options["Tabs"] = {
        selector: "#tabsLi", //标签选择器
        tag: "plugin",
        viewHtml: [
            '<div class="le-tabs plugin-hint plugin_entity">',
            '<ul id="#{name}" class="le-tabs-ul nav nav-tabs">',
            '<li role="presentation"><a href="#{divId}">#{name}</a></li>',
            '<li role="presentation"><a href="#{divId}">#{name}</a></li>',
            '</ul>',
            '</div>'
        ].join(""),
        liHtml: '<li role="presentation"><a href="#{divId}" id="#{aId}"><div style="vertical-align: middle;display: table-cell;text-align: center">#{name}</div></a></li>',
        divHtml: '<div id="#{id}"><div class="le-tabs-div span12 plugin_entity plugin-hint column column_style" style="padding:10px;margin:0px;"></div></div>'
    };

    LE.plugins["Tabs"] = function(){
        var initTabsEvent = function(){
            $("#container").on({
                click: function(e){
                    e.preventDefault();
                    e.stopPropagation();
                    var options = {
                        object: $(this)
                    };
                    LEStyle.destroyAll($(this).attr("id"));
                    //LEStyle.run("Position", options, true).run("TabSetting", options, true).run("ActiveSetting", options, true).run("TabManage", options).run("BackGround", options, true).run("BolderSetting", options, true);
                    LEStyle.run("TabSetting", options, true).run("ActiveSetting", options, true).run("TabManage", options).run("WholeSetting",options);
                }
            }, ".le-tabs");
        };
        return {
            init: function(){
                initTabsEvent();
            },
            afterDrag: function(_id){
                var $t = $("#" + _id);
                var $ul = $t.find("ul");
                $ul.attr("id", _id + "_ul");
                $ul.html("");
                for(var i = 0; i < 3; i++){
                    var _divId = _id + "_div_" + i;
                    var _lh = LETabs.liHtml;
                    var _dh = LETabs.divHtml;
                    $ul.append(_lh.replace(/#\{divId\}/g, "#" + _divId).replace(/#\{name\}/g, "标签 " + (i + 1)).replace(/#\{aId\}/g, "le_tabs_a_" + LEDrag.getNextId()));
                    $t.append(_dh.replace(/#\{id\}/g, _divId));
                }
                $t.tabs();
                LEDrag.columnDraggable();
                $ul.addClass("tabmanage_style3");

            },
            afterDragClone: function(_id,_divFirst){
                var leng=_divFirst.find("ul").find("li").length;
                for(var i=0;i<leng;i++){
                    var divId=_divFirst.children("div").eq(i).attr("id");
                    _divFirst.find("ul").find("li.ui-corner-top").eq(i).children("a").attr("href","#"+divId);
                }
                var $t = $("#" + _id);
                $t.tabs();
               // LEDrag.columnDraggable();
            }
        }
    };

})(window, jQuery, LE, undefined);