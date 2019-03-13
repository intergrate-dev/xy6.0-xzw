/**
 * Created by isaac_gu on 2016/1/6.
 */
(function(window, $, LE){
    LE.options["Hr"] = {
        selector: "#hrLi", //标签选择器
        tag: "plugin",
        viewHtml: [
            '<div class="le-hr plugin_entity plugin-hint" style="padding: 1px;">',
            '<hr style="border-top-style: solid; border-top-width: 1px; border-top-color: rgb(204, 204, 204); margin-top: 5px; margin-bottom: 5px;">',
            '</div>'
        ].join("")
    };

    LE.plugins["Hr"] = function(){
        var initContainer = function(){
        };
        return {
            init: function(){
                $(".demo").on({
                    click: function(e){
                        var options = {
                            object: $(this),
                            type: "all"
                        };
                        LEStyle.destroyAll($(this).attr("id"));
                        LEStyle.run("BolderSetting", options).run("Position", options);
                    }
                }, ".le-hr");
            },
            afterDrag: function(_id){
            }
        }
    };

})(window, jQuery, LE, undefined);