/**
 * Created by isaac_gu on 2016/5/3.
 */
(function(window, $, LE){
    LE.cores["Clean"] = function(){
        var $container = $("#container");
        var headHtml = [
            '<!doctype html>',
            '<html>',
            '<head>',
            '<meta charset="UTF-8"/>',
           // '<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">',
            '<meta http-equiv="X-UA-Compatible" content="IE=edge">',
            //'<title>#{title}</title>'].join("");
            '<title><FOUNDER-XY type="article" data="">${article.title}</FOUNDER-XY></title>'].join("");

        var cssHtml = [
            '<link rel="stylesheet" href="http://cdn.bootcss.com/bootstrap/3.3.5/css/bootstrap.min.css">',
            '<link rel="stylesheet" href="#{location}third/jquery-ui-bootstrap-1.0/css/custom-theme/jquery-ui-1.10.3.custom.css">',
            '<link rel="stylesheet" href="#{location}third/jquery-ui-bootstrap-1.0/assets/css/font-awesome.min.css">',
            '<!--[if IE 7]>',
            '<link rel="stylesheet" href="#{location}third/jquery-ui-bootstrap-1.0/assets/css/font-awesome-ie7.min.css">',
            '<![endif]-->',
            '<!--[if lt IE 9]>',
            '<link rel="stylesheet" href="#{location}third/jquery-ui-bootstrap-1.0/css/custom-theme/jquery.ui.1.10.3.ie.css">',
            '<![endif]-->',
            '<link rel="stylesheet" href="#{location}third/jquery-ui-bootstrap-1.0/assets/js/google-code-prettify/prettify.css">',
            '<link rel="stylesheet" type="text/css" href="#{location}third/export/css/reset.css"/>',
            '<link id="modelLink" rel="stylesheet" type="text/css" href="#{location}third/export/css/model.css"/>'
            //,'<link id="modelLink" rel="stylesheet" type="text/css" href="#{location}third/export/css/editor.css"/>'

        ].join("");

        var jsHtml = ['<!--[if lt IE 9]>',
            '<script src="#{location}third/jquery-ui-bootstrap-1.0/assets/js/vendor/html5shiv.js" type="text/javascript"></script>',
            '<script src="#{location}third/jquery-ui-bootstrap-1.0/assets/js/vendor/respond.min.js" type="text/javascript"></script>',
            '<![endif]-->',
            '<script src="#{location}third/jquery-ui-bootstrap-1.0/assets/js/vendor/jquery-1.9.1.min.js" type="text/javascript"></script>',
            '<script src="#{location}third/jquery-ui-bootstrap-1.0/assets/js/vendor/jquery-migrate-1.2.1.min.js" type="text/javascript"></script>',
            '<script src="#{location}third/bootstrap-3.3.5-dist/js/bootstrap.min.js" type="text/javascript"></script>',
            '<script src="#{location}third/jquery-ui-bootstrap-1.0/assets/js/vendor/holder.js" type="text/javascript"></script>',
            '<script src="#{location}third/jquery-ui-bootstrap-1.0/assets/js/vendor/jquery-ui-1.10.3.custom.min.js" type="text/javascript"></script>',
            '<script src="#{location}third/jquery-ui-bootstrap-1.0/assets/js/google-code-prettify/prettify.js" type="text/javascript"></script>',
            '<script type="text/javascript" src="#{location}third/export/js/jquery.ui.touch-punch.min.js"></script>',
            '<script src="#{location}third/export/js/init.js" type="text/javascript"></script>',
            '<script type="text/javascript" src="#{location}third/echarts/js/echarts.js"></script>',
            '<script type="text/javascript" src="#{location}third/echarts/js/echarts-wordcloud.js"></script>',
            '<script type="text/javascript" src="#{location}third/echarts/js/china.js"></script>',
            ''].join("");
        var bodyHtml = "</head><body>";
        var footHtml = "</body>";

        //图表js的导入
        var lineChartJsHtml="";
        var lineChartJs="";
        //遍历画布中图表 过滤撤销恢复后删除的图表配置
        $("#container").find(".le-lineChart").each(function(index){
            var key=$(this).attr("id");
            if(key){
                var value=JSON.stringify(lineChart_Map.get(key));
                var lineChartStr="\nvar myChart"+index+" = echarts.init(document.getElementById('"+key+"'));"+"\nvar option"+index+"="+value+";"+"\nmyChart"+index+".setOption(option"+index+");";
                lineChartJsHtml+=lineChartStr;
            }
        });
        /*lineChart_Map.each(function (key, value, index) {
            value=JSON.stringify(value);
            var lineChartStr="\nvar myChart"+index+" = echarts.init(document.getElementById('"+key+"'));"+"\nvar option"+index+"="+value+";"+"\nmyChart"+index+".setOption(option"+index+");";
            lineChartJsHtml+=lineChartStr;
        });*/
        lineChartJs='<script type="text/javascript" id="special_script">'+lineChartJsHtml+'</script>';


        var cleanTabsLi = function(){

        };
        var cleanScrollbar = function(){
            $container.children(".ps-scrollbar-x-rail").remove();
            $container.children(".ps-scrollbar-y-rail").remove();
        };

        var getSpecialHtml = function(){
            var _html = [
                headHtml,
                cssHtml,
                $("#special_style").prop("outerHTML"),
                jsHtml,
                bodyHtml,
                cleanedHtml(),
                lineChartJs,
                footHtml
            ];
            return _html.join("").replace(/#\{location\}/g, sourceURL).replace(/#\{title\}/g, $("#projectName").val());
        };

        function cleanedHtml(){
            var $c = $("#cleanDiv");
            $c.html($container.prop("outerHTML"));
            $c.find(".demo").css("padding-bottom", "");
            $c.find(".ps-scrollbar-x-rail").remove();
            $c.find(".ps-scrollbar-y-rail").remove();
            $c.find(".preview").remove();
            $c.find(".drag_handler").remove();
            $c.children("div").css("overflow", "");
            $c.find(".view").each(function(){
                $(this).parent().html($(this).html());
            });

            $c.find(".ui-resizable-handle").remove();

            $c.find(".ui-draggable").removeClass("ui-draggable");
            $c.find(".ui-sortable").removeClass("ui-sortable");

            $c.children(".demo").attr("min-height", "");

            //iframe不能点击的bug
            $c.find("iframe").removeAttr("allowtransparency");
            $c.find("iframe").parent().removeClass("le-iframe");

            //导航一级目录点击
            $(".dropdown-toggle").removeAttr("data-toggle");

            ImageToMedia("#cleanDiv .le-audio_media");
            ImageToMedia("#cleanDiv .le-video_media");
            ImageToMedia("#cleanDiv .le-flash_media");
            ImageToShare("#cleanDiv .le-share");

            //文字变为不可编辑状态
            $c.find(".le-text").attr("contenteditable", false);
            $c.find(".textCanEdit").removeClass("textCanEdit");

            //局部编辑文字隐藏
            $(".partText-set").fadeOut(100);

            var formatSrc = $c.html();
            /*var formatSrc = $.htmlClean($c.html(), {
             format: true
             });*/

            $c.html("");
            return formatSrc;
        }

        var getDesignHtml = function(){
            var _h = [
                $("#special_style").html(),
                "$$$$STYLEHTML$$$$",
                $container.html(),
                "$$$$STYLEHTML$$$$",
                $container.attr("style"),
                "$$$$STYLEHTML$$$$",
                $("#lineChart_script").html()
            ].join("");
            return _h;

        };

        function resetEntityEvent(){
            //初始化标签
            $(".le-tabs").tabs();

            //导航，悬浮一级菜单 显示二级菜单
            $(".dropdown-ul").hover(function(){
                clearTimeout(timer);
                $(".dropdown-ul").removeClass("open");
                $(this).addClass("open");
            }, function(){
                var _this = $(this);
                clearTimeout(timer);
                timer = setTimeout(function(){
                    _this.removeClass("open");
                }, 500);
            });

            //图片调整
            $container.find(".le-image_img").each(function(){
                var _$img = $(this);
                //如果还有组件的div，先去掉
                if(_$img.parent().hasClass("ui-wrapper")){
                    _$img.removeClass("ui-resizable");
                    _$img.css("resize", "");
                    _$img.css("position", "");
                    var _imgHtml = _$img.prop("outerHTML");
                    _$img.parent().parent().html(_imgHtml);
                }else if(_$img.parent()[0].tagName == "A"){
                    //如果图片加了链接
                    _$img.removeClass("ui-resizable");
                    _$img.css("resize", "");
                    _$img.css("position", "");
                    var _imgHtml = _$img.parent().prop("outerHTML");
                    _$img.parent().parent().parent().html(_imgHtml);
                }

            });

            $container.find(".le-image_img").each(function(){
                var _$this = $(this);
                //var _$column = _$this.parent().parent().parent().parent().attr("id") == "container" ? _$this.parent().parent().parent().parent() : _$this.closest(".column");

                //初始化img组件
                _$this.resizable({
                    aspectRatio: false,
                    maxWidth: _$this.parent().width(),
                    ghost: false,
                    start: function(){
                        LEHandler.handler_enableMouseisOver = false;
                        $(this).parent().css("height", "");
                        LEDrag.hideHint();
                    },
                    stop: function(){
                        LEHandler.handler_enableMouseisOver = true;
                        LEHistory.trigger();
                        $(this).click();
                        LEDrag.hideHint();
                    }
                });

                _$this.attr("data-radio", parseInt(_$this.css("width")) / parseInt(_$this.css("height")));
                _$this.attr("data-unlocked", true);

                //处理图片的a链接 放置到ui-wrapper内层
                if(_$this.parent().parent()[0].tagName == "A"){
                    var oldA=_$this.parent().parent();
                    var _href=oldA.attr("href");
                    var _target=oldA.attr("target");
                    _$this.parent().unwrap();
                    _$this.wrap('<a href="'+_href+'" target="'+_target+'"></a>');
                }
            });


            //$container.find(".le-image_img").parent().css("display", "inline-block");
            $container.find(".le-image_img").parent().parent().parent().find(".ui-wrapper").css("display", "inline-block");
            $container.click();

            $container.css("padding-bottom", 150);
            $container.css("overflow", "");
            $container.css("height", "");

            LEReset.changeContainerHeight();
            LEDrag.columnDraggable();
        }

        return {
            init: function(){

            },
            clean: function(){
                cleanTabsLi();
                cleanScrollbar();
            },
            getSpecialHtml: function(){
                return getSpecialHtml();
            },
            getStyleHtml: function(){
                return getStyleHtml();
            },
            getDesignHtml: function(){
                return getDesignHtml();
            },
            resetEntityEvent: resetEntityEvent
        };
    };
})(window, jQuery, LE, undefined);
window.LEClean = LE.cores["Clean"]();