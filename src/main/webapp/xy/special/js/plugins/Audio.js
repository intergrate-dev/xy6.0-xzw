/**
 * Created by isaac_gu on 2016/1/6.
 */
(function(window, $, LE){
    LE.options["Audio"] = {
        selector: "#audioLi", //标签选择器
        tag: "plugin",
        viewHtml: [
            '<div class="le-audio plugin_entity plugin-hint" >',
            // thumbnail
            //'<a href="#" >',
            //'</a>',
            '</div>'
        ].join(""),
        /*videoHtml: '<img class="le-video_img align_mark" src="" alt="...">',*/
        audioHtml: '<embed class="le-audio_media" src="">'
    };

    LE.plugins["Audio"] = function(){
        var initContainer = function(){
            $("#container").on({
                click: function(e){
                    e.preventDefault();
                    e.stopPropagation();
                    var options = {
                        object: $(this)
                    };
                    LEStyle.destroyAll($(this).attr("id"));
                    LEStyle.run("Audio", options).run("Align", options, true).run("Position", options, true).run("BolderSetting", options, true);
                }
            }, ".le-audio");
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