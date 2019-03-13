/**
 * Created by isaac_gu on 2016/1/6.
 */
(function(window, $, LE){
    LE.options["Share"] = {
        selector: "#shareLi", //标签选择器
        tag: "plugin",
        viewHtml: [
            '<div class="le-share plugin_entity plugin-hint">',
            '<img style="margin:6px 0" data-imgsrc="export/images/sliderBar/share024.png" data-style="0" data-size="24" class="le-share_media" src="export/images/sliderBar/share024.png"/>',
//'<div class="bdsharebuttonbox"><a href="#" class="bds_tsina" data-cmd="tsina" title="分享到新浪微博"></a><a href="#" class="bds_sqq" data-cmd="sqq" title="分享到QQ好友"></a><a href="#" class="bds_weixin" data-cmd="weixin" title="分享到微信"></a><a href="#" class="bds_qzone" data-cmd="qzone" title="分享到QQ空间"></a><a href="#" class="bds_tieba" data-cmd="tieba" title="分享到百度贴吧"></a><a href="#" class="bds_more" data-cmd="more"></a></div>',
            // '<script>window._bd_share_config={"common":{"bdSnsKey":{},"bdText":"","bdMini":"2","bdPic":"","bdStyle":"0","bdSize":"16"},"share":{}};with(document)0[(getElementsByTagName(\'head\')[0]||body).appendChild(createElement(\'script\')).src=\'http://bdimg.share.baidu.com/static/api/js/share.js?v=89860593.js?cdnversion=\'+~(-new Date()/36e5)];</script>',
            '</div>'
        ].join("")
    };
    LE.plugins["Share"] = function(){
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
                        LEStyle.run("ShareSetting", options);
                    }
                }, ".le-share");
            }
        }
    };

})(window, jQuery, LE, undefined);