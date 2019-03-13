/**
 * Created by isaac_gu on 2016/1/6.
 */
/**
 * Created by isaac_gu on 2016/1/6.
 */
(function(window, $, LE){
    LE.options["Summary"] = {
        selector: "#SummaryLi", //标签选择器
        tag: "plugin",
        viewHtml: [
            //contenteditable="true"
            '<div class="plugin-hint">',
            '<blockquote class="plugin_entity">',
            '<p>github是一个全球化的开源社区.</p>',
            '<small>关键词 <cite title="Source Title">开源</cite></small>',
            '</blockquote>',
            '</div>'
        ].join("")
    };

    LE.plugins["Summary"] = function(){
        var initText = function(){

            $(".demo").on({
                click: function(e){
                    e.preventDefault();
                    e.stopPropagation();
                    var options = {
                        object: $(this)
                    };
                    LEStyle.destroyAll($(this).attr("id"));
                    LEStyle.run("Position", options).run("TextSetting", options).run("Link", options);

                },
                dblclick: function(e){
                    var _$this = $(this);
                    _$this.attr("contenteditable", true);
                },
                blur: function(e){
                    var _$this = $(this);
                    _$this.attr("contenteditable", false);
                }
            }, "blockquote");
        };
        return {
            init: function(){
                initText();
            },
            afterDrag: function(){

            }
        }
    };
})(window, jQuery, LE, undefined);