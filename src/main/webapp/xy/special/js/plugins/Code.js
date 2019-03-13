/**
 * Created by isaac_gu on 2016/1/6.
 */
(function(window, $, LE){
    LE.options["Code"] = {
        selector: "#CodeLi", //标签选择器
        tag: "plugin",
        viewHtml: [
            '<div class="le-code plugin-hint plugin_entity">',
            '</div>'
        ].join("")
    };

    LE.plugins["Code"] = function(){
        var initEntityEvent = function(){
            $("#container").on({
                click: function(e){
                    e.preventDefault();
                    e.stopPropagation();
                    var options = {
                        object: $(this),
                        type: "all"
                    };
                    LEStyle.destroyAll($(this).attr("id"));
                    LEStyle.run("Code", options);
                }
            }, ".le-code");
        };
        return {
            init: function(){
                initEntityEvent();
            }
        }
    };
})(window, jQuery, LE, undefined);