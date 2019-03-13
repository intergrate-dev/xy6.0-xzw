/**
 * Created by isaac_gu on 2016/1/6.
 */
(function(window, $, LE){
    LE.options["Column"] = {
        selector: "#columnLi", //标签选择器
        tag: "plugin",
        viewHtml: [
            '<div class="span12 plugin-hint plugin_entity column column_style" style="padding:10px;margin:0px;"></div>'
        ].join("")
    };

    LE.plugins["Column"] = function(){
        var initContainer = function(){
            $("#container").on({
                click: function(e){
                    e.preventDefault();
                    e.stopPropagation();
                    var options = {
                        object: $(this),
                        type: "all"
                    };
                    LEStyle.destroyAll($(this).attr("id"));
                    LEStyle.run("SlideBar", options).run("Position", options).run("BackGround", options, true).run("BolderSetting", options, true);
                }
            }, ".column");
        };
        return {
            init: function(){
                initContainer();
            }
        };
    };

})(window, jQuery, LE, undefined);