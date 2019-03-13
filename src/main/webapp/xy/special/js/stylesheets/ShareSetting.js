/**
 * Created by isaac_gu on 2016/1/14.
 */
(function(window, $, LE){
    LE.stylesheets["ShareSetting"] = function(){
        var $panel = $("#shareSection");
        var $shareStyle=$("#shareStyle");
        var $shareSize=$("#shareSize");

       /* var initShareStyleEvent = function(){
            $shareStyle.find("li").click(function(){
                var scriptStr= LECurrentObject.find("script").html();
                var styleNum=$(this).attr("data-style");
                var changeStr="";
                if(scriptStr.indexOf('"bdStyle":"0"')!=-1){
                    changeStr=scriptStr.replace('"bdStyle":"0"','"bdStyle":"'+styleNum+'"');
                }else if(scriptStr.indexOf('"bdStyle":"1"')!=-1){
                    changeStr=scriptStr.replace('"bdStyle":"1"','"bdStyle":"'+styleNum+'"');
                }
                LECurrentObject.attr("data-style",styleNum);
                LECurrentObject.find("script").html(changeStr);
                LEHistory.trigger();
            });

        };

        var initShareSizeEvent = function(){
            $shareSize.find("li").click(function(){
                var scriptStr= LECurrentObject.find("script").html();
                var styleNum=$(this).attr("data-size");
                var changeStr="";
                if(scriptStr.indexOf('"bdSize":"16"')!=-1){
                    changeStr=scriptStr.replace('"bdSize":"16"','"bdSize":"'+styleNum+'"');
                }else if(scriptStr.indexOf('"bdSize":"24"')!=-1){
                    changeStr=scriptStr.replace('"bdSize":"24"','"bdSize":"'+styleNum+'"');
                }else if(scriptStr.indexOf('"bdSize":"32"')!=-1){
                    changeStr=scriptStr.replace('"bdSize":"32"','"bdSize":"'+styleNum+'"');
                }
                LECurrentObject.attr("data-size",styleNum);
                LECurrentObject.find("script").html(changeStr);
                LEHistory.trigger();
            });

        };
*/
        var initShareStyleEvent = function(){
            $shareStyle.find("li").click(function(){
                var _style=$(this).attr("data-style");
                var _size=LECurrentObject.find("img").attr("data-size");
                LECurrentObject.find("img").attr("data-style",_style);
              //  LECurrentObject.find("img").attr("data-imgsrc","export/images/sliderBar/share"+_style+_size+".png");
                LECurrentObject.find("img").attr("src","export/images/sliderBar/share"+_style+_size+".png");
                LEHistory.trigger();
            });

        };

        var initShareSizeEvent = function(){
            $shareSize.find("li").click(function(){
                var _style=LECurrentObject.find("img").attr("data-style");
                var _size=$(this).attr("data-size");
                LECurrentObject.find("img").attr("data-size",_size);
               // LECurrentObject.find("img").attr("data-imgsrc","export/images/sliderBar/share"+_style+_size+".png");
                LECurrentObject.find("img").attr("src","export/images/sliderBar/share"+_style+_size+".png");
                LEHistory.trigger();
            });

        };

        var resetStyle = function(){
            var _style=LECurrentObject.find("img").attr("data-style");
            var _size=LECurrentObject.find("img").attr("data-size");
            $shareStyle.find("li").eq(_style).addClass("box-shadow").siblings().removeClass("box-shadow");
            var sizeNum=parseInt(_size)/8-2;
            $shareSize.find("li").eq(sizeNum).addClass("select").siblings().removeClass("select");

        };
        return {
            init: function(){
                initShareStyleEvent();
                initShareSizeEvent();
            },
            run: function(options, doHide, doSlide){
                resetStyle();

                    //切换组件时，样式设置部分回顶部
                sliderbarToTop();
                LEDisplay.show($panel, doHide, doSlide);
            },
            destroy: function(){
                $panel.hide();
            }
        };
    };
})(window, jQuery, LE, undefined);

var ImageToShare=function(selector){
    jQuery(selector).each(function(){
        var _this=jQuery(this);
        var _style=_this.find("img").attr("data-style");
        var _size=_this.find("img").attr("data-size");
        var mediaStr='<div class="bdsharebuttonbox"><a href="#" class="bds_tsina" data-cmd="tsina" title="分享到新浪微博"></a><a href="#" class="bds_sqq" data-cmd="sqq" title="分享到QQ好友"></a><a href="#" class="bds_weixin" data-cmd="weixin" title="分享到微信"></a><a href="#" class="bds_qzone" data-cmd="qzone" title="分享到QQ空间"></a><a href="#" class="bds_tieba" data-cmd="tieba" title="分享到百度贴吧"></a><a href="#" class="bds_more" data-cmd="more"></a></div><script>window._bd_share_config={"common":{"bdSnsKey":{},"bdText":"","bdMini":"2","bdPic":"","bdStyle":"'+_style+'","bdSize":"'+_size+'"},"share":{}};with(document)0[(getElementsByTagName(\'head\')[0]||body).appendChild(createElement(\'script\')).src=\'http://bdimg.share.baidu.com/static/api/js/share.js?v=89860593.js?cdnversion=\'+~(-new Date()/36e5)];</script>';
        _this.html(mediaStr);
    })
};
