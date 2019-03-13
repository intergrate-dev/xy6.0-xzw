/**
 * Created by isaac_gu on 2016/1/6.
 *
 * 这个类主要是负责处理 每个element的工具栏的响应事件
 * 需要显示 hint hover事件的标签 加 .plugin-hint
 * 需要加拖动的句柄 加.plugin_entity
 *
 */
//MAP存放图表option
var lineChart_Map = new Map();
var cloneLineChartOptionMap = new Map();
(function($, LE){
    LE.cores["DragHandler"] = function(){
        var dh = new DragHandler();
        var diyEvent = function(){
            $("#container").on({
                mouseover: function(e){
                    e.preventDefault();
                    e.stopPropagation();
                    LEHandler.handler_enableMouseisOver = false;
                    $(".drag_hint_hover").removeClass("drag_hint_hover");
                },
                mouseout: function(e){
                    e.preventDefault();
                    e.stopPropagation();
                    LEHandler.handler_enableMouseisOver = true;
                },
                click: function(e){
                    e.preventDefault();
                    e.stopPropagation();
                }
            }, ".diy-ctrl");

        };
        return {
            handler_enableMouseisOver: true,
            init: function(){
                dh.init();
                diyEvent();
            },
            hideHint: function(){
                dh.hideHint();
            },
            hideDragHandler: function(){
                dh.hideDragHandler();
            },
            disableDragHandler: function(){
                dh.disableDragHandler();
            },
            ableDragHandler: function(){
                dh.ableDragHandler();
            },
            disableAllDragHandler: function(){
                dh.disableAllDragHandler();
            },
            ableAllDragHandler: function(){
                dh.ableAllDragHandler();
            }

        };
    };

    var DragHandler = function(){
        //初始化
    };
    DragHandler.prototype = {
        init: function(){

            this.initHandlerHtml();
            this.initHandlerEvent();
            this.initToolsEvent();
        },
        _html: [
            '<ul class="diy-ctrl-ul">',
            '<i class="diy-ctrl remove" data-hint="移除"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></i>',
            '<i class="diy-ctrl copy" data-hint="复制"><span class="glyphicon glyphicon-bookmark"></span></i>',
            //'<i class="diy-ctrl save" data-hint="保存为区块"><span class="glyphicon glyphicon-floppy-saved"></span></i>',
            '<i class="diy-ctrl move" data-hint="拖动"><span class="glyphicon glyphicon-move"></span></i>',
            '</ul>'

        ].join(""),
        /**
         * 给每一个组件添加一个 handler
         */
        initHandlerHtml: function(){
            $(".drag_hint").size() < 1 && $(document.body).append('<div class="drag_hint"></div>');
            /**
             * 给每一个view添加工具栏
             */
            var $obj = this;
            $("#sliderBar .view").each(function(e){
                var _$this = $(this);
                if(_$this.siblings(".diy-ctrl-ul").size() < 1){
                    _$this.append('<div class="drag_handler">' + $obj._html + '</div>');
                }
            });
        },
        /**​
         * 拖动句柄的事件
         */
        initHandlerEvent: function(){
            /*var _dowmLeft;
            var _downTop;
            var downTime;
            this.isClick=true;*/
            var _obj = this;
            var $hint = $(".drag_hint");
            $("#container").on({
                //点击鼠标时，
                click: function(e){
                    _obj.elementClick(e);
                }
            }, ".plugin_entity");

            $("#container").on({
                click: function(e){
                    var _this=$(this);
                    var options = {
                        object: LECurrentObject
                    };
                    LEStyle.run("ClipBoardSetting", options);
                    //剪切板功能
                    _obj.clipBoarddrag(_this);

                    // LEHistory.trigger();
                }
            }, ".copy");


          /* $("#container").on({
                mousedown: function(e){
                    _dowmLeft = e.clientX;
                    _downTop = e.clientY;
                    downTime = new Date().getTime();
                },mouseup: function(e){
                   //e.preventDefault();
                   //e.stopPropagation();
                    var _left = e.clientX;
                    var _top = e.clientY;
                    var upTime = new Date().getTime();
                   console.log(e.toElement)
                    var isText= $(e.toElement).hasClass("le-text");
                  if((upTime - downTime > 150) || (_left!= _dowmLeft) || (_top!=_downTop) || isText){
                      _obj.isClick=false;
                  }else{
                      _obj.isClick=true;
                  }
                  console.info((upTime - downTime > 150) +"  "+ (_left!= _dowmLeft)+"  "+(_top!=_downTop)+"  "+isText+"   "+_obj.isClick)
                }
            }, ".plugin_entity");*/

            $("#container").on({
                //悬浮的时候 hint显示
                mouseover: function(e){
                    e.preventDefault();
                    e.stopPropagation();
                    if(LEHandler.handler_enableMouseisOver){
                        $(".drag_hint_hover").removeClass("drag_hint_hover");
                        $(this).addClass("drag_hint_hover");
                    }
                },
                //鼠标移走的时候 hint 消失
                mouseout: function(e){
                    e.preventDefault();
                    e.stopPropagation();
                    $(".drag_hint_hover").removeClass("drag_hint_hover");
                }
            }, ".plugin-hint");
        },

        /**
         * 点击元素时,显示工具栏,并且显示外线
         * @param e
         */
        elementClick: function(e){
            e.preventDefault();
            e.stopPropagation();
            var _$target = $(e.target);
            var _$parent = _$target.closest(".view");
            var _$handler = _$parent.children(".drag_handler");

            //添加外线
            $(".obj_click").removeClass("obj_click");
            _$parent.addClass("obj_click");

            //隐藏其他工具栏
            $(".drag_handler").hide();
            //显示当前工具栏
            _$handler.show();

            //给选中的entity加个样式
            $(".drag_hint_click").removeClass("drag_hint_click");
          //  if(this.isClick){
                //给元素作个性化的
                $("div[contenteditable=true]").not(_$target).attr("contenteditable", false).removeClass("plugin_entity_disabled").addClass("plugin_entity");
                $(".textCanEdit").removeClass("textCanEdit");
                //局部编辑文字隐藏
                $(".partText-set").fadeOut(100);
                $(".divselect").find("ul").hide();
                $(".partlink-box").hide();
          //  }

            $(_$target).hasClass("plugin-hint") && $(_$target).addClass("drag_hint_click");

        },
        clipBoarddrag: function(_this){
            var $clipboardCup=$("#clipboard-cup");
            var _clonePlungin=_this.closest(".view").parent();
            var dataType=_clonePlungin.attr("data-type");
            var _clonePreview=_clonePlungin.children(".preview").html();
            var _cloneView=_clonePlungin.children(".view").html();

            var firstPreview=$clipboardCup.children().eq(0).children(".preview");
            var secondPreview=$clipboardCup.children().eq(1).children(".preview");
            var thirdPreview=$clipboardCup.children().eq(2).children(".preview");
            var firstView=$clipboardCup.children().eq(0).children(".view");
            var secondView=$clipboardCup.children().eq(1).children(".view");
            var thirdView=$clipboardCup.children().eq(2).children(".view");

            var num=$clipboardCup.children().length;
            var _getPlungin=_this.closest(".view").parent();

            var _thirdCupBoard= $clipboardCup.children().eq(2);
            var _hasLineChart = _thirdCupBoard.find(".view").children(".plugin_entity");

            //如果剪切板最后一个元素中含有lineChart，num=3，删除图表对应Option
            _hasLineChart.each(function(){
                if(num ==3 && $(this).hasClass("le-lineChart")){
                    var _removeKey=$(this).attr("data-mapkey");
                    cloneLineChartOptionMap.remove(_removeKey);
                }
            });

            //复制组件中包含的css样式 从#special_style中获取
            var arrStyle=$("#special_style").html().split("#").slice(1);
            var copyCss=[];
            _clonePlungin.children(".view").find(".plugin_entity").each(function(m){
                var _this=$(this);
                var outerId=_this.attr("id");
                $.each(arrStyle,function(n,value){
                     if(value.indexOf($.trim(outerId)+" ")!=-1){
                         copyCss.push(arrStyle[n]);
                     }
                 });
            });
            var _cloneCss="#"+copyCss.join("#");
            var firstCss=$clipboardCup.children().eq(0).children(".view").attr("data-css");
            var secondCss=$clipboardCup.children().eq(1).children(".view").attr("data-css");
            var thirdCss=$clipboardCup.children().eq(2).children(".view").attr("data-css");


            var isOne=$.trim(firstPreview.html())=="" && $.trim(secondPreview.html())=="" && $.trim(thirdPreview.html())=="";
            var isTwo=$.trim(firstPreview.html())!="" && $.trim(secondPreview.html())=="" && $.trim(thirdPreview.html())=="";
            var isThree=$.trim(firstPreview.html())!="" && $.trim(secondPreview.html())!="" && $.trim(thirdPreview.html())=="";

            if(isOne){
                $clipboardCup.children().eq(0).attr("data-type",dataType);
                firstPreview.html(_clonePreview);
                firstView.html(_cloneView);
                //为复制的组件加上css样式
                firstView.attr("data-css",_cloneCss);
            }else if(isTwo){
                //注释部分代码为后进先出
                /*$clipboardCup.children().eq(1).attr("data-type",dataType);
                secondPreview.html(_clonePreview);
                secondView.html(_cloneView);*/
                 var dataTypeOne=$clipboardCup.children().eq(0).attr("data-type");

                 $clipboardCup.children().eq(1).attr("data-type",dataTypeOne);
                 secondPreview.html(firstPreview.html());
                 secondView.html(firstView.html());
                //复制并添加的css移动
                 secondView.attr("data-css",firstCss);


                $clipboardCup.children().eq(0).attr("data-type",dataType);
                firstPreview.html(_clonePreview);
                firstView.html(_cloneView);
                //为复制的组件加上css样式
                firstView.attr("data-css",_cloneCss);
            }else if(isThree){
                /*$clipboardCup.children().eq(2).attr("data-type",dataType);
                thirdPreview.html(_clonePreview);
                thirdView.html(_cloneView);*/
                var dataTypeOne=$clipboardCup.children().eq(0).attr("data-type");
                var dataTypeTwo=$clipboardCup.children().eq(1).attr("data-type");

                $clipboardCup.children().eq(2).attr("data-type",dataTypeTwo);
                thirdPreview.html(secondPreview.html());
                thirdView.html(secondView.html());
                //复制并添加的css移动
                thirdView.attr("data-css",secondCss);

                $clipboardCup.children().eq(1).attr("data-type",dataTypeOne);
                secondPreview.html(firstPreview.html());
                secondView.html(firstView.html());
                //复制并添加的css移动
                secondView.attr("data-css",firstCss);

                $clipboardCup.children().eq(0).attr("data-type",dataType);
                firstPreview.html(_clonePreview);
                firstView.html(_cloneView);
                //为复制的组件加上css样式
                firstView.attr("data-css",_cloneCss);

            }else{
               /* var dataTypeTwo=$clipboardCup.children().eq(1).attr("data-type");
                var dataTypeThree=$clipboardCup.children().eq(2).attr("data-type");
                $clipboardCup.children().eq(0).attr("data-type",dataTypeTwo);
                $clipboardCup.children().eq(1).attr("data-type",dataTypeThree);
                $clipboardCup.children().eq(2).attr("data-type",dataType);
                firstPreview.html(secondPreview.html());
                firstView.html(secondView.html());
                secondPreview.html(thirdPreview.html());
                secondView.html(thirdView.html());
                thirdPreview.html(_clonePreview);
                thirdView.html(_cloneView);*/
                 var dataTypeOne=$clipboardCup.children().eq(0).attr("data-type");
                 var dataTypeTwo=$clipboardCup.children().eq(1).attr("data-type");

                 $clipboardCup.children().eq(2).attr("data-type",dataTypeTwo);
                 $clipboardCup.children().eq(1).attr("data-type",dataTypeOne);
                 $clipboardCup.children().eq(0).attr("data-type",dataType);

                 thirdPreview.html(secondPreview.html());
                 thirdView.html(secondView.html());
                 //复制并添加的css移动
                 thirdView.attr("data-css",secondCss);

                 secondPreview.html(firstPreview.html());
                 secondView.html(firstView.html());
                 //复制并添加的css移动
                 secondView.attr("data-css",firstCss);

                 firstPreview.html(_clonePreview);
                 firstView.html(_cloneView);
                 //为复制的组件加上css样式
                 firstView.attr("data-css",_cloneCss);
            }
            $clipboardCup.children().attr("data-clone","true");

            //操作剪切板中新增组件 如果组件及组件内部含有图表  1、给图表添加标记 2、新增map中Option
            _getPlungin.find(".view").children(".plugin_entity").each(function(index){
                var _objEntity=$(this);
                var _clone=LE._leId++;
                var _flag="cloneOPtion_"+_clone;
                if(_objEntity.hasClass("le-lineChart")) {
                    //获取被复制的图表option
                    var _id = _objEntity.attr("id");
//                    var _lineOption = lineChart_Map.get(_id);
//                    var _lineOption = JSON.parse(JSON.stringify(lineChart_Map.get(_id)));
                     var _lineOption = jQuery.extend(true,{},lineChart_Map.get(_id));

                    //剪切板中第一个组件
                    var _FirstCupBoard = $clipboardCup.children().eq(0);
                    var hasLineChart = _FirstCupBoard.find(".view").children(".plugin_entity").eq(index);
                    hasLineChart.attr("data-mapkey",_flag);
                    cloneLineChartOptionMap.put(_flag, _lineOption);

                }
            });

        },
        getPosition: function(_$this){
            return {
                left: _$this.offset().left,
                top: _$this.offset().top,
                width: _$this.outerWidth(),
                height: _$this.outerHeight()
            };
        },
        hideHint: function(){
            $(".drag_hint_hover").removeClass("drag_hint_hover");
        },
        hideDragHandler: function(){
            $(".drag_handler:visible").hide();
        },
        initToolsEvent: function(){
            $("#container").on({
                //点击鼠标时，
                click: function(e){
                    e.preventDefault();
                    var _$target = $(e.target);
                    var _$parent = _$target.closest(".view").parent();
                    //删除元素的同时删除图表对应配置
                    _$parent.children(".view").find(".plugin_entity").each(function(){
                        var _this=$(this);
                        if(_this.hasClass("le-lineChart")){
                            var _id=_this.attr("id");
                            lineChart_Map.remove(_id);
                        }
                    });

                    //遍历style标签，找到所有样式信息
                    var arrStyle=$("#special_style").html().split("#").slice(1);
                    //删除元素的同时删除对应<style>标签中的样式
                    _$parent.children(".view").find(".plugin_entity").each(function(m){
                        var _this=$(this);
                        var outerId=_this.attr("id");
                        //StyleManager.removeStyle("#"+outerId+" ");
                        StyleManager.removeStyle("#"+outerId);
                       /* $.each(arrStyle,function(n,value){
                            if(value.indexOf($.trim(outerId)+" ")!=-1){
                                var _key=value.split("{")[0];
                                var _selector="#"+ $.trim(_key);
                                StyleManager.removeStyle(_selector);
                            }
                        });*/
                    });

                    //删除元素
                    _$parent.remove();
                    $("#container").trigger("click");
                    LEReset.changeColumnBoxHeight();
                    LEHistory.trigger();
                }
            }, ".remove");
        },
        disableDragHandler: function(){
            var _$target = $(".drag_handler:visible");
            _$target.siblings(".plugin_entity").removeClass("plugin_entity").addClass("plugin_entity_disabled");
            _$target.hide();
        },
        ableDragHandler: function(){
            var _$target = $(".plugin_entity_disabled");
            _$target.removeClass("plugin_entity_disabled").addClass("plugin_entity");
        },
        disableAllDragHandler: function(){
            var _$target = $(".drag_handler");
            _$target.siblings(".plugin_entity").removeClass("plugin_entity").addClass("plugin_entity_disabled");
            _$target.hide();
        },
        ableAllDragHandler: function(){
            this.ableDragHandler();
        }
    };
})(jQuery, LE, undefined);

window.LEHandler = LE.cores["DragHandler"]();
