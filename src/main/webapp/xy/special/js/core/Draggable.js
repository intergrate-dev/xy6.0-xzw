/**
 * Created by isaac_gu on 2016/1/7.
 * 这个类负责拖动效果
 */
(function(window, $, LE){
    LE._leId = 0;
    LE.cores["Draggable"] = function(){
        var _id = new Date().getTime();
        // var isDrag = true;

        var initId = function(){
            LE._leId = new Date().getTime();
        };
        /**
         * 工具栏当中的拖动效果
         */
        var initPluginDrag = function(){
            /**
             * 把组件从栏目中拖出来
             */
            $(".plugin").draggable({
                //拖动到demo 的div上,并且进行sort
                connectToSortable: "#container,#container.column",
                //拖动时显示的效果
                helper: "clone",
                start: function(event, ui){
                    //去掉 鼠标悬浮效果
                    LEHandler.handler_enableMouseisOver = false;
                    LECurrentObject = $("#container");
                    //添加一个标记
                    $(this).addClass("drag_target");
                },
                drag: function(event, ui){
                    // isDrag = true;
                    ui.helper.css({
                        "opacity": 1
                    });
                },
                stop: function(event, ui){

                    // 新生成的对象
                    var _$this = $("#container").find(".drag_target");
                    // 组建的类型
                    var _type = _$this.attr("data-type");
                    // 组建的实体
                    var _$targetDiv = _$this.find(".view").children(".plugin_entity");
                    // 添加新的id

                    //如果拖拽的是复制的组件
                    if(_$this.attr("data-clone")=="true"){
                        //获取新ID
                        var _clone=getNextId();

                        //获取旧样式 替换id生成新样式
                        var _newCSs=_$this.find(".view").attr("data-css");
                        if(_newCSs!="#"){
                            _$this.find(".plugin_entity").each(function(){
                            var _thisID=$(this).attr("id");
                                var re =new RegExp(_thisID+" ","gim");
                                _newCSs=_newCSs.replace(re,_thisID+_clone+" ");
                            });
                            $("#special_style").append(_newCSs);
                        }
                        //为复制的组件增加新ID
                        _$this.find("[id]").each(function(){
                            var _this=$(this);
                            var oldId=_this.attr("id");
                            _this.attr("id",oldId+_clone).attr("data-mark",oldId+_clone);
                        });
                        _$this.find(".view").attr("data-css","");
                        //遍历所有plugin，不仅仅是最外层，对需要初始化的plugin执行回调方法   轮播图、标签。
                        _$this.parent().find(".plugin").each(function(){
                            var obj=$(this);
                            var _divFirst=obj.find(".view").children(".plugin_entity").eq(0);
                            var _idFirst=_divFirst.attr("id");
                            var typeClone = obj.attr("data-type");
                            typeof LE.plugins[typeClone] == "function" && LE.plugins[typeClone].call().afterDragClone && LE.plugins[typeClone].call().afterDragClone(_idFirst,_divFirst);
                        });
                        //通过正则 修改复制的动态更新组件的替换标记，确保生成正确的模板
                        _$this.parent().find(".plugin_entity[data-update='true']").each(function(){
                            var obj=$(this);
                            var _idFirst=obj.attr("id");

                            var reg=/le_{1}[a-zA-Z]+_{1}[0-9]+----->{1}/g;
                            var oldHtml=obj.html();
                            var newHtml=oldHtml.replace(reg,_idFirst+"----->");
                            obj.html(newHtml);
                        });

                        var _$leimg = _$this.find(".le-image_img");
                        if(_$leimg.size() > 0){
                            _$leimg.each(function(){
                                var __$this = $(this);
                                __$this.css("position", "");
                                __$this.siblings(".ui-resizable-handle").remove();
                                var _h = __$this.prop("outerHTML");
                                __$this.parent().parent().html(_h);
                                refreshImageResizable(_$this);
                            });
                        }

                    }else{

                        var _id = "le_" + _type + "_" + getNextId();
                        _$targetDiv.attr("id", _id).attr("data-mark", _id);
                        var _$leaves = _$targetDiv.find(".plugin_entity");
                        _$leaves.each(function(_index){
                            $(this).attr("id", _id + "_inner_" + _index).attr("data-mark", _id + "_inner_" + _index);
                        });

                        //如果有回掉方法，调用
                        typeof LE.plugins[_type] == "function" && LE.plugins[_type].call().afterDrag && LE.plugins[_type].call().afterDrag(_id);
                    }
                    //去掉所有的 拖动标记
                    $(".drag_target").removeClass("drag_target");

                    _$targetDiv.trigger("click");
                    // LEHistory.trigger();
                    // isDrag = false;
                    //触发点击事件
                    resetSortable();

                    LEHandler.handler_enableMouseisOver = true;

                }
            });
        };
        function refreshImageResizable(_object){
            var _maxWidth = _object.width();
            var keepRatio = _object.find(".le-image_img").attr("data-unlocked");
            keepRatio = !(keepRatio == "true");
            _object.find(".le-image_img").resizable({
                aspectRatio: keepRatio,
                ghost: false,
                maxWidth : _maxWidth,
                start: function(){
                    LEHandler.handler_enableMouseisOver = false;
                    $(this).parent().parent().css("height", "");
                    _object.find(".le-image_img").css("height",_object.find(".le-image_img").parent().css("height")).css("width",_object.find(".le-image_img").parent().css("width"));
                    _object.find(".le-image_img").parent().css("position","relative");
                    LEDrag.hideHint();
                },
                create:function(){
                    _object.find(".le-image_img").css("height",_object.find(".le-image_img").parent().css("height")).css("width",_object.find(".le-image_img").parent().css("width"));
                    _object.find(".le-image_img").parent().css("position","relative");
                },
                resize:function(){
                    _object.find(".le-image_img").css("height",_object.find(".le-image_img").parent().css("height")).css("width",_object.find(".le-image_img").parent().css("width"));
                    _object.find(".le-image_img").parent().css("position","relative");
                },
                stop: function(){
                    LEHandler.handler_enableMouseisOver = true;

                    var _$this = $(this);
                    _object.find(".le-image_img").css("height",_object.find(".le-image_img").parent().css("height")).css("width",_object.find(".le-image_img").parent().css("width"));
                    _object.find(".le-image_img").parent().css("position","relative");
                    LEHistory.trigger();
                    _$this.click();
                    LEDrag.hideHint();
                }
            });
            _object.find(".le-image_img").css("height",_object.find(".le-image_img").parent().css("height")).css("width",_object.find(".le-image_img").parent().css("width"));
            _object.find(".le-image_img").parent().css("position","relative");
        }
        /**
         * 容器中的排序 - 在draggable之前
         */
        var resetSortable = function(){
            $("#container,#container .column").sortable({
                connectWith: ".column,#container",
                opacity: .35,
                handle: ".move",
                tolerance: "pointer",
                start: function(event, ui){
                    LEHandler.handler_enableMouseisOver = false;
                },
                sort: function(event, ui){
                    //ui.helper.width(200);
                    ui.helper.offset().left = "800px";
                },
                stop: function(event, ui){
                    // if(!isDrag){
                        //需要恢复
                        LEHistory.trigger();
                    // }
                    LEHandler.handler_enableMouseisOver = true;
                    LEReset.resetEntityWH();
                    //LEReset.changeContainerHeight();
                    LEReset.changeColumnBoxHeight();

                    var _closestPlungin=LECurrentObject.closest(".view").parent();
                    _closestPlungin.find(".plugin_entity").each(function(){
                        var _obj=$(this);
                        if(_obj.hasClass("le-lineChart")){
                            var dragId=_obj.attr("id");
                            var myChart = echarts.init(document.getElementById(dragId));
                            var lineOption=lineChart_Map.get(dragId);
                            myChart.setOption(lineOption);
                        }
                    });
                }
            });

        };
        var getNextId = function(){
            return LE._leId++;
        };

        var hideHint = function(){
            $(".drag_hint").hide();
        };
        return {
            init: function(){
                initPluginDrag();
                resetSortable();

                initId();
            },
            columnDraggable: function(){
                 resetSortable();
            },
            getNextId: function(){
                return getNextId();
            },
            hideHint: function(){
                hideHint();
            }
        }
    };
})(window, jQuery, LE, undefined);
window.LEDrag = LE.cores["Draggable"]();


