/**
 * Created by isaac_gu on 2016/5/3.
 */
(function(window, $, LE){
    LE.cores["Preview"] = function(){
        var $scaleLeft=$(".scaleLeft");
        var $ScaleBtmLine=$(".ScaleBtmLine");
        var $sliderBar=$("#sliderBar");
        var $sidebarPanel=$("#sidebar-panel");
        var $functionalMenu=$(".functional-menu");

        var initPrewiew = function(){
            var clipBoardisShow= $("#clipBoard").is(":visible");
            $("#previewLi").click(function(e){
                var isShow = $("#container").parent().hasClass("mgr240");
                if(isShow){
                    //$(this).find("div").html("取消预览");
                    $(this).addClass("previewdo");
                    $scaleLeft.parent().animate({left: '-210px'}, "fast");
                    $ScaleBtmLine.animate({marginLeft: '-210px'}, "fast");
                    $sliderBar.animate({marginLeft: '-210px'}, "fast");
                    $sidebarPanel.animate({right: '-240px'}, "fast");
                    $functionalMenu.animate({right: '-240px'}, "fast");
                    $(this).css({"position": "fixed", "right": "0px", "top": "10px"});
                    $(this).attr("title", "取消预览");
                    disableItems();
                    //视频音频flash标签转换
                    ImageToMedia(".le-flash_media");
                    ImageToMedia(".le-video_media");
                    ImageToMedia(".le-audio_media");

                    LEStyle.destroy("SlideBarII").destroy("SlideBarIII");

                    $("#clipBoard").hide();
                    // ImageToShare(".le-share_media")
                } else{
                    //$(this).find("div").html("预览");
                    $(this).css({"position": "", "right": "", "top": ""});
                    $(this).attr("title", "预览");
                    $functionalMenu.animate({right: '0px'}, "fast");
                    $(this).removeClass("previewdo");
                    enableItems();
                    //视频音频flash标签转换
                    MediaToImage(".le-flash_media");
                    MediaToImage(".le-video_media");
                    MediaToImage(".le-audio_media");
                    // ShareToImage(".le-share_media");
                    $("#container").trigger("click");
                    $scaleLeft.parent().animate({left: '0px'}, "fast");
                    $ScaleBtmLine.animate({marginLeft: '0px'}, "fast");
                    $sliderBar.animate({marginLeft: '0px'}, "fast");
                    $sidebarPanel.animate({right: '0px'}, "fast");

                    //剪切板的显示状态
                    if($("#clipBoard").attr("data-show")=="true"){
                        $("#clipBoard").show();
                    }else if($("#clipBoard").attr("data-show")=="false"){
                        $("#clipBoard").hide();
                    }

                }
            });
        };

        var initCheckEvent = function(){
            $("#checkBtn").click(function(e){
                window.open("../special/preview.do");
            });
        };

        function disableItems(){
            LEHandler.disableAllDragHandler();

            //文字变为不可编辑
            $(".le-text").attr("contenteditable", false);
            $(".textCanEdit").removeClass("textCanEdit");
            //局部编辑文字隐藏
            $(".partText-set").fadeOut(100);

            //列表编辑小笔标记 预览时hover不显示
            $(".le-list-group").addClass("edit-disable");
            $(".le-gallery").addClass("edit-disable");

            $(".le-list-group").addClass("edit-flag");
            $(".le-gallery").addClass("edit-flag");


            //轮播图去掉class  le-carousel 解决超链接不能跳转
            $(".carousel").addClass("carousel_disabled").removeClass("le-carousel");
            //导航去掉class le-nav  一级主菜单去掉data-toggle="dropdown"
            $(".le-nav").addClass("navl_disabled").removeClass("le-nav");
            $(".dropdown-toggle").attr("data-toggle", "");
            //图片去掉classs le-image
            $(".le-image").addClass("image_disabled").removeClass("le-image");
            //文字去掉classs le-text
            $(".le-text").addClass("text_disabled").removeClass("le-text");
            //列表去掉classs le-list-group
            $(".le-list-group").addClass("list-group_disabled").removeClass("le-list-group");
            //标签栏去掉classs le-tabs-div column le-tabs
            $(".le-tabs-div").addClass("le-tabs-div_disabled").removeClass("le-tabs-div");
            $(".column").addClass("column-tab_disabled").removeClass("column");
            $(".le-tabs").addClass("le-tabs_disabled").removeClass("le-tabs");
            //分栏去掉class le-columnbox diy-inner
            $(".le-columnbox").addClass("le-columnbox_disabled").removeClass("le-columnbox");
            $(".diy-inner").addClass("diy-inner_disabled").removeClass("diy-inner");
            //预览去掉边框 去掉class column_style
            $(".column_style").addClass("column_style_disabled").removeClass("column_style");
            //flash去掉classs le-flash
            $(".le-flash").addClass("le-flash_disabled").removeClass("le-flash");
            //多图去掉classs gallery-edit
            $(".gallery-edit").addClass("gallery-edit-empty").removeClass("gallery-edit");
            //代码去掉classs le-code
            $(".le-code").addClass("le-code_disabled").removeClass("le-code");


            // $(".column").addClass("column_disabled").removeClass("column_style");
            $(".plugin-hint").addClass("plugin-hint_disabled").removeClass("plugin-hint");

            $("#container").parent().removeClass("mgl210").removeClass("mgr240").removeClass("mgl80");
            $("#mainDiv").attr("data-ow", $("#mainDiv").css("width"));
            $("#mainDiv").css("width", "");
            //去掉click
            $(".drag_hint_click").removeClass("drag_hint_click");
            $(".obj_click").removeClass("obj_click");

            $(".plugin_entity").addClass("plugin_entity_disabled").removeClass("plugin_entity");
            $(".diy-col-ctrl").hide();


            //隐藏样式列表
            $("#modelGoBack").trigger("click");
            $("#listModelGoBack").trigger("click");
            $("#carModelGoBack").trigger("click");
            $("#mainModelGoBack").trigger("click");
            $("#subModelGoBack").trigger("click");
        }

        function enableItems(){
            LEHandler.ableAllDragHandler();

            //列表、多图编辑小笔标记 取消预览时hover显示
            $(".edit-disable").removeClass("edit-disable");

            //$(".column_disabled").addClass("column_style").removeClass("column_disabled");
            $(".plugin-hint_disabled").addClass("plugin-hint").removeClass("plugin-hint_disabled");

            if($scaleLeft.css("display") == "none"){
                $("#container").parent().addClass("mgl80").addClass("mgr240");
            } else{
                $("#container").parent().addClass("mgl210").addClass("mgr240");
            }

            // var _ow = $("#mainDiv").attr("data-ow");
            //  $("#mainDiv").css("width", _ow);
            //轮播图加回class  le-carousel
            $(".carousel_disabled").addClass("le-carousel").removeClass("carousel_disabled");
            //导航加回class le-nav 一级主菜单加回data-toggle="dropdown"
            $(".navl_disabled").addClass("le-nav").removeClass("navl_disabled");
            $(".dropdown-toggle").attr("data-toggle", "dropdown");
            //图片加回classs le-image
            $(".image_disabled").addClass("le-image").removeClass("image_disabled");
            //文字加回classs le-text
            $(".text_disabled").addClass("le-text").removeClass("text_disabled");
            //列表加回classs le-list-group
            $(".list-group_disabled").addClass("le-list-group").removeClass("list-group_disabled");
            //标签栏加回classs le-tabs-div column le-tabs
            $(".le-tabs-div_disabled").addClass("le-tabs-div").removeClass("le-tabs-div_disabled");
            $(".column-tab_disabled").addClass("column").removeClass("column-tab_disabled");
            $(".le-tabs_disabled").addClass("le-tabs").removeClass("le-tabs_disabled");
            //分栏加回class le-columnbox diy-inner
            $(".le-columnbox_disabled").addClass("le-columnbox").removeClass("le-columnbox_disabled");
            $(".diy-inner_disabled").addClass("diy-inner").removeClass("diy-inner_disabled");
            //取消预览加回边框 加回class column_style
            $(".column_style_disabled").addClass("column_style").removeClass("column_style_disabled");
            //flash加回classs le-flash
            $(".le-flash_disabled").addClass("le-flash").removeClass("le-flash_disabled");
            //多图加回classs gallery-edit
            $(".gallery-edit-empty").addClass("gallery-edit").removeClass("gallery-edit-empty");
            $(".plugin_entity_disabled").addClass("plugin_entity").removeClass("plugin_entity_disabled");
            //代码加回classs le-code
            $(".le-code_disabled").addClass("le-code").removeClass("le-code_disabled");

            $(".diy-col-ctrl").show();


        }

        return {
            init: function(){
                initPrewiew();
                initCheckEvent();
            }
        };
    };
})(window, jQuery, LE, undefined);