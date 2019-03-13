/**
 * Created by isaac_gu on 2016/1/14.
 */
(function(window, $, LE){
    LE.stylesheets["ClipBoardSetting"] = function(){
        var $panel = $("#clipBoard");

        /*剪切板拖放事件*/
        var initMoveEvent=function(){
            $(".clipboard-title").bind("mousedown",function(e) {
                //获取鼠标当前坐标
                var pageX = e.clientX;
                var pageY = e.clientY;
                var winHeight=$(window).height();//浏览器时窗口可视区域高度
                var winWidth=$(window).width();//浏览器时窗口可视区域宽度
                var maxHeight=winHeight-232;
                var maxWidth=winWidth-326;
                //获取block的坐标，左边界和上边界
                var offX = parseInt($(".clipboard-box").offset().left) || 0;
                var offY = parseInt($(".clipboard-box").offset().top) || 0;
                //计算出鼠标坐标相对于block坐标的间距
                var offLX = pageX - offX;
                var offLY = pageY - offY;

                $(document).bind("mousemove",function(e) {
                    var posX=e.clientX - offLX;
                    var posY=e.clientY - offLY;

                    if(posX<96 && $("#mainDiv").hasClass("mgl80")){
                        posX=96;
                    }else if(posX<226 && $("#mainDiv").hasClass("mgl210")){
                            posX=226;
                    }else if(posX>maxWidth){
                        posX=maxWidth
                    }

                    if(posY<5){
                        posY=5;
                    }else if(posY>maxHeight){
                        posY=maxHeight
                    }
                    $(".clipboard-box").css("left",posX); //设置block的X坐标
                    $(".clipboard-box").css("top",posY);
                    return false;
                })
            });

            $(document).bind("mouseup",function() {
                $(document).unbind("mousemove");
            });//鼠标弹起
        };

        /*剪切板隐藏事件*/
        var initHideEvent=function(){
            $("#clipboard-hide").click(function(e) {
                $panel.hide();
                $panel.attr("data-show","false");
            })
        };

        return {
            init: function(){
                initMoveEvent();
                initHideEvent();
            },
            run: function(){
                $panel.show();
                $panel.attr("data-show","true");
            },
            destroy: function(){

            }
        };
    };
})(window, jQuery, LE, undefined);