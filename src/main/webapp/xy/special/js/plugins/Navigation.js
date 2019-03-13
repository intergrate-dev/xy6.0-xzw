/**
 * Created by isaac_gu on 2016/3/22.
 */
(function(window, $, LE){
    LE.options["Navigation"] = {
        selector: "#navigationLi", //标签选择器
        tag: "plugin",
        viewHtml: [
            '<div class="le-nav plugin-hint plugin_entity">' +
                /*'<ul class="le-nav-ul nav nav-pills">' +
                 '<li role="presentation" class="active"><a href="#">Home</a></li>' +
                 '<li role="presentation"><a href="#">Profile</a></li>' +
                 '<li role="presentation" class="dropdown"><a class="dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-haspopup="true" aria-expanded="false">Messages<span class="caret"></span></a>' +
                 '<ul class="dropdown-menu" aria-labelledby="drop6">' +
                 '<li><a href="#">Action</a></li>' +
                 '<li><a href="#">Another action</a></li>' +
                 '<li><a href="#">Something else here</a></li>' +
                 '</ul>' +
                 '</li>' +
                 '</ul>' +*/
            '</div>'
        ].join(""),
        liHtml: '<li role="presentation"><a id="#{aId}" data-itemid="#{refId}" href="#{link}"><div class="navStyle" style="min-width:10px;white-space: nowrap;vertical-align: middle;display: table-cell;text-align: center;">#{title}</div></a></li>',
        aClass: 'class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false"'
    };

    LE.plugins["Navigation"] = function(){
        var initContainer = function(){
            $("#container").on({
                click: function(e){
                    e.preventDefault();
                    e.stopPropagation();
                    var options = {
                        object: $(this)
                    };
                    LEStyle.destroyAll($(this).attr("id"));
                    LEStyle.run("MainMenu", options, true).run("SubMenu", options, true).run("NavManage", options);
                }
            }, ".le-nav");
        };
        return {
            init: function(){
                initContainer();
            },
            afterDrag: function(_id){
                var $t = $("#" + _id);
                var $ul = $t.find("ul").filter(".le-nav-ul");
                _id += "_ul";
                //给ul设id
                $ul.attr("id", _id);
                //给a标签设id
                $ul.find("a").each(function(index){
                    $(this).attr("id", _id + "_a_" + index);
                });
                //给dropdown的li的aria-labelledby设id
                $ul.children(".dropdown").find("ul").each(function(){
                    $(this).attr("aria-labelledby", $(this).siblings("a").attr("id"));
                });
            }
        };
    };
})(window, jQuery, LE, undefined);