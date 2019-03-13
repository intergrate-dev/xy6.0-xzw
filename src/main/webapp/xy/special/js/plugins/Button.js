/**
 * Created by isaac_gu on 2016/1/6.
 */
(function(window, $, LE){
    LE.options["Button"] = {
        selector: "#ButtonLi", //标签选择器
        tag: "plugin",
        viewHtml: [
            '<div class="le-button plugin-hint">',
            '<button class="btn btn-default" type="submit">Button</button>',
            '</div>'
        ].join("")
    };

    LE.plugins["Button"] = function(){
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
                    LEStyle.run("Position", options);
                }
            }, ".le-button");
        };
        return {
            init: function(){
                initEntityEvent();
            },
            afterDrag: function(_id){
            },
            afterSort: function(){

            }
        }
    };

})(window, jQuery, LE, undefined);