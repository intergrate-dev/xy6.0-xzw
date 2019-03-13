/**
 * Created by isaac_gu on 2016/1/6.
 */
(function(window, $, LE){
    LE.options["ColumnBox"] = {
        selector: "#columnboxLi", //标签选择器
        tag: "plugin",
        viewHtml: [
            '<div class="le-columnbox diy-inner plugin_entity plugin-hint clearfix">',
            '<div class="w-col diy-col-6">',
            '<div class="column column_style plugin-hint plugin_entity" position-type="phw"  style="padding:10px;margin:0px;" ></div>',
            '<div class="diy-col-ctrl" style="left: 100%;"></div>',
            '</div>',
            '<div class="w-col diy-col-6">',
            '<div class="column column_style plugin-hint plugin_entity"  position-type="phw"  style="padding:10px;margin:0px;"></div>',
            '<div class="diy-col-ctrl_disabled" style="left: 100%;"></div>',
            '</div>',
            '</div>'
        ].join("")
    };


    LE.plugins["ColumnBox"] = function(){
        //调整间距的组件
        var colCtrlEvent = function(){
                $("#container").on({
                        mouseover: function(e){
                            var _$this = $(this);
                            _$this.show();
                        },
                        /**
                         * 移动调节前后两块的大小
                         * @param e
                         */
                        mousedown: function(e){
                            //关掉浮动事件
                            LEHandler.handler_enableMouseisOver = false;
                            var _$this = $(this);
                            var _$pre = _$this.parent();
                            var _$next = _$pre.next();
                            var _$parent = _$this.closest(".plugin_entity");
                            var _$preImg = _$pre.find(".le-image_img");
                            var _$nextImg = _$next.find(".le-image_img");

                            //总体的宽度
                            var _totalWidth = _$parent.outerWidth();
                            //锁定宽度
                            var _targetWidth = _$pre.outerWidth() + _$next.outerWidth();
                            var _targetPercent = 0;

                            /**
                             *  1. 准备移动时，把所有的容器都加上百分比
                             */

                            var _restWidth = 0;
                            _$parent.children(".w-col").not(_$pre).not(_$next).each(function(){
                                var _$this = $(this);
                                var _w = (_$this.outerWidth() * 100 / _totalWidth).toFixed(2);
                                _$this.css("width", _w + "%");
                                _restWidth += parseFloat(_w);
                            });

                            _targetPercent = 100 - _restWidth;
                            var _pwp = (parseInt(_$pre.outerWidth()) * 100 / _totalWidth).toFixed(2);
                            _$pre.width(_pwp + "%");
                            _$next.width((_targetPercent - _pwp) + "%");

                            /**
                             * 鼠标移动事件
                             */
                            _$document.mousemove(function(e){
                                //为正 向左移动的距离
                                var _dis = _$this.offset().left - e.pageX + 3;
                                //前一个偏移后的宽度
                                var _pw = parseInt(_$pre.outerWidth() - _dis);
                                if(_pw > 30 && ( _targetWidth - _pw) > 30){
                                    //设置成百分比
                                    var _pp = (_pw * 100 / _totalWidth).toFixed(2);
                                    var _np = _targetPercent - _pp;

                                    _$pre.width(_pp + "%");
                                    _$next.width(_np + "%");

                                }

                                return false;
                            });

                            /**
                             * 清空绑定事件
                             */
                            _$document.one("mouseup", function(e){
                                LEHandler.handler_enableMouseisOver = true;
                                _$document.unbind("mousemove");

                                _$preImg.each(function(){
                                    LEReset.resizeImage($(this));
                                });

                                _$nextImg.each(function(){
                                    LEReset.resizeImage($(this));
                                });
                               /**
                                * 1.找到当前分栏里边的所有图表；
                                *2、初始化分栏里的所有图表 */
                               var _box=$(e.target).closest(".view");
                                _box.find(".le-lineChart").each(function(){
                                    var _this=$(this);
                                    var _id=_this.attr("id");
                                    var myChart = echarts.init(document.getElementById(_id));
                                    var lineOption=lineChart_Map.get(_id);
                                    myChart.setOption(lineOption);
                                });
                                LEHistory.trigger();
                            });

                            /**
                             * 鼠标的移动事件
                             */
                        }
                    }, ".diy-col-ctrl"
                )
                ;
                var _$document = $(document);
                /**
                 * 选中
                 */
                $("#container").on({
                    click: function(e){
                        e.preventDefault();
                        e.stopPropagation();
                        var options = {
                            object: $(this)
                        };
                        LEStyle.destroyAll($(this).attr("id"));
                        LEStyle.run("ColumnBox", options).run("BackGround", options, true).run("Position", options, true).run("BolderSetting", options, true);
                    }
                }, ".diy-inner");   //, .diy-inner .column

            }
            ;
        return {
            init: function(){
                colCtrlEvent();
            }
        }
    }
    ;

    LE.settings["ColumnBox"] = function(){

    };
})(window, jQuery, LE, undefined);