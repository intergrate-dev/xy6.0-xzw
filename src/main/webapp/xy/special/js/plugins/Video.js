/**
 * Created by isaac_gu on 2016/1/6.
 */

(function(window, $, LE){
    LE.options["Video"] = {
        selector: "#videoLi", //标签选择器
        tag: "plugin",
        viewHtml: [
            '<div class="le-video plugin_entity plugin-hint " >',
            //thumbnail
            //'<a href="#" >',
            //'</a>',
            '</div>'
        ].join(""),
        /*videoHtml: '<img class="le-video_img align_mark" src="" alt="...">',*/
        videoHtml: '<embed class="le-video_media" wmode="transparent" src=""  style="margin-left: auto; margin-right: auto; width: 500px; height: 300px;"><param name="wmode" value="transparent"></embed>'
    };

    LE.plugins["Video"] = function(){
        var initContainer = function(){
            $("#container").on({
                click: function(e){
                    e.preventDefault();
                    e.stopPropagation();
                    var options = {
                        object: $(this)
                    };
                    LEStyle.destroyAll($(this).attr("id"));
                    LEStyle.run("Video", options).run("Align", options, true).run("Position", options, true).run("BolderSetting", options, true);
                },
                mousedown: function(e){
                    $(this).trigger("click");
                }
            }, ".le-video");
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