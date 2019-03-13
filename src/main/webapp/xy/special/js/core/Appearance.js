/**
 * Created by isaac_gu on 2016/1/14.
 */
(function(window, $, LE){
    LE.cores["Appearance"] = function(){
        var initAppearance = function(){

        };
        var initAutoScale = function(){
            var screenWidth = $(window).width();
            var width = parseInt($("#mainDiv").css("width")) + 450;
            if(width > screenWidth){
                $(".scaleLeft").addClass("hide1").siblings().removeClass("hide1");
                $("#mainDiv").addClass("mgl80").removeClass("mgl210");
                $(".scaleLeft").parent().css({"width": "80px"});
                $(".ScaleBtmLine").css({"width": "80px"});
                $(".assembly").css({"width": "80px"});
                $("#sliderBar").css({"width": "80px"});
                $("ul.layout-content").children("li").removeClass("left").css({"width": "100%"});
                if($("#mainDiv").css("margin-left") == "80px"){
                    $("#mainDiv").css({"width": $(window).width() - 320});
                } else{
                    $("#mainDiv").css("width", $(window).width() - 450);
                }
            } else{
                $("#mainDiv").css("width", "");
                $(".assembly").css({"width": "210px"});
            }
        };
        //组件容器的收缩、展开
        var pluginBoxScaleEvent = function(){
            $(".iconAreaShow").click(function(){
                $(this).addClass("hide1").siblings().removeClass("hide1");
                if($(this).hasClass("scaleLeft")){
                    /*$(this).parent().siblings().find("ul.layout-content").children("li").removeClass("left").css("width","100%");
                     $(this).parent().parent().parent().css("width","80px");
                     $("#mainDiv").css("margin-left","80px");
                     $("#mainDiv").css("width", $(window).width() - 330);*/

                    $("#mainDiv").addClass("mgl80").removeClass("mgl210");
                    $(".scaleLeft").parent().animate({"width": "80px"}, 100);
                    $(".ScaleBtmLine").animate({"width": "80px"}, 100);
                    $("#sliderBar").animate({"width": "80px"}, 100);
                    $(".assembly").animate({"width": "80px"}, 100);
                    //$("#mainDiv").animate({"margin-left":"80px"},100);
                    $("ul.layout-content").children("li").removeClass("left").css({"width": "100%"});
                    $("#mainDiv").css({"width": $(window).width() - 320});

                } else{
                    /*$("#mainDiv").css("margin-left","210px");
                     $(this).parent().parent().parent().css("width","210px");
                     $(this).parent().siblings().find("ul.layout-content").children("li").addClass("left").css("width","33%");
                     $("#mainDiv").css("width", $(window).width() - 460);*/

                    $("#mainDiv").addClass("mgl210").removeClass("mgl80");
                    //$("#mainDiv").animate({"margin-left":"210px"},100);
                    $(".scaleLeft").parent().animate({"width": "210px"}, 100);
                    $(".ScaleBtmLine").animate({"width": "210px"}, 100);
                    $("#sliderBar").animate({"width": "210px"}, 100);
                    $(".assembly").animate({"width": "210px"}, 100);
                    $("ul.layout-content").children("li").addClass("left").animate({"width": "33%"}, 100);
                    $("#mainDiv").css({"width": $(window).width() - 450});

                    //剪切板位置
                    if(parseInt($(".clipboard-box").css("left"))<226){
                        $(".clipboard-box").css("left",226);
                    }

                }

            });
        };
        //给当前选中的元素添加背景颜色
        var bgSelect = function(selector, className){
            $(selector).click(function(){
                $(this).addClass(className).siblings().removeClass(className);
            });
        };
        /*var bgSelect = function(selector, className){
         $(document).on({"click":function(){
         $(this).addClass(className).siblings().removeClass(className);
         }},selector);
         };
         var bgHover = function(selector, className){
         $(document).on({"mouseover":function(){
         $(this).addClass(className);
         },"mouseout":function(){
         $(this).removeClass(className);
         }},selector);
         };*/
        //收起展开效果
        var hideArea = function(selector){
            $(selector).each(function(){
                $(this).click(function(){
                    $(this).hide().parent().siblings().slideToggle();
                    $(this).siblings('img').show();
                });
            });
        };

        //手风琴效果
        var hideEle = function(selector){
            $(selector).each(function(){
                $(this).click(function(){
                    $(this).hide().parent().find('ul').slideToggle();
                    $(this).siblings('img').show();
                });
            });
        };

        //select下拉框效果
        var divselect = function(divSelector, inputSelectot){
            var $div = $(divSelector);
            //显示
            $div.click(function(event){
                event.stopPropagation();
                var $this = $(this);
                var $ul = $this.children('ul');
                //$ul.slideToggle();
                $this.siblings().children('ul').hide();
                $ul.fadeToggle(100);
            });
            //选择
            $div.find('a').click(function(){
                var $this = $(this);
                var $city = $this.parents('ul').siblings("cite");
                var txt = $this.text();
                $city.html(txt);
                var value = $div.attr("selectid");
                $this.children('ul').hide();
            });
            $(document).on("click", function(){
                $div.children('ul').hide();
            });

        };

        //获取当前浏览器的高度，设置左右功能栏高度为浏览器默认高度
        /* var setHeight = function(selectorId, selecorClass){
         var h = $(window).height();
         var th = $("#" + selectorId).outerHeight();
         $(selecorClass).css({
         "height": (h - th) + 'px',
         "overflow": "hidden"
         })
         };*/
        //左侧silderBar高度
        var setHeight = function(num, selecorClass){
            var h = $(window).height();
            $(selecorClass).css({
                "height": (h - num) + 'px',
                "overflow": "hidden"
            });
        };


        //右侧选项卡切换效果
        var tab = function(selector, className, selector1, selector2){
            $(selector).click(function(){
                $(this).addClass(className).siblings().removeClass(className);
                var index = $(selector).index(this);
                $(selector1).children(selector2).eq(index).show().siblings().hide();
            });

        };

        //点击关闭效果
        var hideObj = function(className){
            $(className).bind("click", function(){
                $(this).parent().hide();
            });

        };
        //导航菜单关闭
        var hideoDiv = function(className, selector){
            $(className).bind("click", function(){
                $(this).parents(selector).hide();
            });

        };

        /**
         * 隐藏section
         * 1. 隐藏图片
         * 2. 隐藏同级
         * guzm
         */
        function hideSection(){
            $("#sidebar-panel,#sidebar-panel-II,#sidebar-panel-III").find(".iconArea").click(function(e){
                var $this = $(this);
                var _p = $this.parent();
                var doHide = !_p.find(".hide1").is(":visible");
                if(doHide){
                    //同级img
                    _p.children("img:first").hide();
                    _p.children("img:last").show();
                    _p.siblings().slideUp();
                } else{
                    _p.children("img:first").show();
                    _p.children("img:last").hide();
                    _p.siblings().slideDown();
                }
            });
        }

        return {
            init: function(){
                window.onresize = function(){
                    // setHeight("headerArea", ".setBarHeight");
                    setHeight("50", ".setBarHeight");
                    setHeight("22", ".setBarHeightSlide");
                    setHeight("0", ".setBarHeightContain");
                    setHeight("96", ".setBarHeightModel");
                    //$("#container").css("min-height", $(window).height() - 62 + 150);
                    var isShow = $("#container").parent().hasClass("mgr240");
                    if(isShow){
                        if($("#mainDiv").css("margin-left") == "80px"){
                            $("#mainDiv").css({"width": $(window).width() - 320});
                        } else if($("#mainDiv").css("margin-left") == "210px"){
                            $("#mainDiv").css("width", $(window).width() - 450);
                        }
                    }

                    LEReset.changeContainerHeight();

                };
                initAutoScale();
                initAppearance();
                pluginBoxScaleEvent();
                hideObj(".closeBtn");
                hideoDiv(".btn-hide", ".navMenuSet");
                hideEle(".toggleBtn");
                bgSelect(".btn-hint", 'select');
                bgSelect(".displayStyle", 'box-shadow');
                //bgSelect(".btn-act", 'act');
                //bgSelect(".btn-cur", 'activ');
                //bgSelect(".btn-curr", 'activt');
                //bgHover(".btn-act", 'act');
                //hideArea(".iconArea");
                divselect(".divselect", ".inputselect");
                //setHeight("headerArea", ".setBarHeight");
                setHeight("50", ".setBarHeight");
                setHeight("22", ".setBarHeightSlide");
                setHeight("0", ".setBarHeightContain");
                setHeight("96", ".setBarHeightModel");
                // $("#mainDiv").css("width", $(window).width() - 460);
                tab(".tab_menu li", "selected", ".tab_box", "div");

                LEReset.changeContainerHeight();

                $('.setBarHeight').perfectScrollbar();
                $('.setBarHeightSlide').perfectScrollbar();
                $('.setBarHeightContain').perfectScrollbar();
                $('.setBarHeightModel').perfectScrollbar();
                hideSection();
            }
        };
    };
})(window, jQuery, LE, undefined);