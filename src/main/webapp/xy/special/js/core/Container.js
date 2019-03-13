/**
 * Created by isaac_gu on 2016/1/14.
 */
(function(window, $, LE){
    LE.cores["Container"] = function(){
        //初始化container
        var initContainerStyle = function(){
//      var containerWidth = $(window).width() - $("#sliderBar").width() - $("#sidebar-panel").width();
//   	$("#container").css("width", $(window).width() - $("#sliderBar").width() - $("#sidebar-panel").width());


        };
        var initContainerEvent = function(){
            //初始化contain的点击事件
            $("#container").click(function(e){
                var options = {
                    object: $(this)
                };
                LEStyle.destroyAll($(this).attr("id"));
                LEStyle.run("BackGround", options).run("Width", options);

                if($(this).prop("id") == "container"){
                    $(".drag_handler[class!=obj_click]").hide();
                    $(".obj_click").removeClass("obj_click");
                    $("div[contenteditable=true]").attr("contenteditable", false).removeClass("plugin_entity_disabled").addClass("plugin_entity");
                    $(".drag_hint_click").removeClass("drag_hint_click");
                    $(".textCanEdit").removeClass("textCanEdit");
                    //局部编辑文字隐藏
                    $(".partText-set").fadeOut(100);

                }

            });
        };

        function initExitEvent(){
            $("#existBtn").click(function(){
                var dataUrl = "../../e5workspace/after.do?UUID=" + special.UUID + "&DocIDs="
                    + special.docID + "&DocLibID=" + special.docLibID;
                //若是直接点窗口关闭，并且是chrome浏览器，则单独打开窗口
                /*if(isChrome())
                 window.open(dataUrl, "_blank", "width=10,height=10");
                 else*/
                window.location.href = dataUrl;
            });
            var isChrome = function(){
                var nav = navigator();
                return nav.browser == "chrome";
            };
            var navigator = function(){
                var ua = window.navigator.userAgent.toLowerCase();
                // trident IE11
                var re = /(trident|msie|firefox|chrome|opera|version).*?([\d.]+)/;
                var m = ua.match(re);

                var Sys = {};
                Sys.browser = m[1].replace(/version/, "'safari");
                Sys.ver = m[2];
                return Sys;
            };
            $(window).bind('beforeunload', function(){
                return '';
            });

        }

        return {
            init: function(){
                initContainerStyle();
                initContainerEvent();
                initExitEvent();
                $('#tabs').tabs();
            }
        };
    };
})(window, jQuery, LE, undefined);