/**
 * Created by isaac_gu on 2016/1/6.
 */
(function(window, $, LE){
    LE.options["Flash"] = {
        selector: "#flashLi", //标签选择器
        tag: "plugin",
        viewHtml: [
            '<div class="le-flash plugin_entity plugin-hint" >',
            // thumbnail
            //'<a href="#" >',
            //'</a>',
            '</div>'
        ].join(""),
        /*videoHtml: '<img class="le-video_img align_mark" src="" alt="...">',*/
        flashHtml: '<embed class="le-flash_media" wmode="transparent" src="" quality="high" allowscriptaccess="never" allownetworking="internal" allowfullscreen="true" style="margin-left: auto; margin-right: auto; width: 500px; height: 300px;" ><param name="wmode" value="transparent" ></embed>'
    };

    LE.plugins["Flash"] = function(){
        var initContainer = function(){
            $("#container").on({
                click: function(e){
                    e.preventDefault();
                    e.stopPropagation();
                    var options = {
                        object: $(this)
                    };
                    LEStyle.destroyAll($(this).attr("id"));
                    LEStyle.run("Flash", options).run("Align", options, true).run("Position", options, true).run("BolderSetting", options, true);
                },
                mousedown: function(e){
                    e.preventDefault();
                    e.stopPropagation();
                    $(this).trigger("click");
                }
            }, ".le-flash");
        };
        return {
            init: function(){
                initContainer();
            },
            afterDrag: function(_id){

            },
            afterSort: function(){

            }
        }
    };

})(window, jQuery, LE, undefined);