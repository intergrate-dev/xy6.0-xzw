/**
 * Created by isaac_gu on 2016/3/22.
 */
(function(window, $, LE){
    LE.options["Frame"] = {
        selector: "#frameLi", //标签选择器
        tag: "plugin",
        viewHtml: [
            '<div class="n-widget le-iframe plugin-hint plugin_entity"></div>'
        ].join("")
    };

    LE.plugins["Frame"] = function(){
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
                    LEStyle.run("Frame", options).run("Position", options).run("Align", options, true);
                }
            }, ".le-iframe");
        };
        return {
            init: function(){
                initContainer();
            },
            afterDrag: function(){

            }
        }
    };
})(window, jQuery, LE, undefined);