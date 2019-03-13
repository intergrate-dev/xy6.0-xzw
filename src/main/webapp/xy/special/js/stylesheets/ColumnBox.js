/**
 * 分栏设置
 * Created by isaac_gu on 2016/1/6.
 */
(function(window, $, LE){
    LE.stylesheets["ColumnBox"] = function(){
        var $cbDiv = $("#columnboxSection");   //分栏的div
        var $options = $("#cbOptionUl").find("li").not(".column-equal"); //分栏选项
        var $columnSizeInput = $("#cbColumnSize");    //分栏数
        var $columnMarginInput = $("#cbColumnMargin");    //分栏数
        var colHtml = [
            '<div class="w-col">',
            '<div class="column column_style plugin-hint plugin_entity" position-type="phw"  style="padding:10px;margin:0px;" ></div>',
            '<div class="diy-col-ctrl" style="left: 100%;"></div>',
            '</div>'
        ].join("");

        function resizeChart(){
            /* 初始化分栏里的所有图表 */
            LECurrentObject.find(".le-lineChart").each(function(){
                var _this=$(this);
                var _id=_this.attr("id");
                var myChart = echarts.init(document.getElementById(_id));
                var lineOption=lineChart_Map.get(_id);
                myChart.setOption(lineOption);
            });
        }
        //初始化选项
        var initOption = function(){
            //选项的点击事件
            $options.click(function(e){
                var _$this = $(this);
                //2. 获得栏目分隔格式
                var _columnWidths = _$this.data("hint").split(":");
                //设置分栏数
                var _columnSize = _columnWidths.length;
                $columnSizeInput.val(_columnSize);

                var _$currentContainer = $(".obj_click").find(".diy-inner");
                var $columns = _$currentContainer.children(".w-col");
                $columns.attr("class", "w-col");
                var _contentArray = [];

                //3. 重新规划布局 - 把column里面的内容放到 _contentArray中
                $.each(_columnWidths, function(i){
                    var _h = $columns.eq(i).prop("outerHTML") || colHtml;
                    _contentArray.push(_h);
                });
                _$currentContainer.html(_contentArray.join(""));


                //4. 重新设置class
                $columns = _$currentContainer.children(".w-col");
                $columns.each(function(index){
                    $(this).addClass("diy-col-" + _columnWidths[index]);
                });

                //5. 去掉w-col中的width
                $columns.each(function(){
                    $(this).css("width", "");
                });

                //6. 重新设置栏间距
                handleColumnMargin(parseInt($columnMarginInput.val()) / 2);
                //7. 重新设置拖动
                LEDrag.columnDraggable();

                //8. 把最后一个拖动去掉
                $(".diy-col-ctrl_disabled").addClass("diy-col-ctrl").removeClass("diy-col-ctrl_disabled");
                $columns.last().children(".diy-col-ctrl").addClass("diy-col-ctrl_disabled").removeClass("diy-col-ctrl");

                resizeChart();
                LEHistory.trigger();

            });
        };


        /**
         * 初始化栏间距的事件
         */
        var initColumnMargin = function(){
            $columnMarginInput.onlyNum().keydown(function(e){
                var _keycode = e.which;
                //点击回车的时候 也可设置间距
                if(_keycode === 13){
                    if(parseInt($columnMarginInput.val()) % 2 != 0){
                        $columnMarginInput.val(parseInt($columnMarginInput.val()) + 1);
                    }
                    handleColumnMargin(parseInt($columnMarginInput.val()) / 2);
                }
                if(_keycode === 38){
                    $columnMarginInput.val(parseInt($columnMarginInput.val()) + 2);
                    handleColumnMargin(parseInt($columnMarginInput.val()) / 2);
                } else if(_keycode === 40){
                    $columnMarginInput.val(parseInt($columnMarginInput.val()) - 2);
                    handleColumnMargin(parseInt($columnMarginInput.val()) / 2);
                }
            });

            $columnMarginInput.keyup(function(e){
                resizeChart();
                LEHistory.trigger();
            });
        };
        //设置栏间距
        function handleColumnMargin(n){
            var _$currentContainer = LECurrentObject;
            var _$right = _$currentContainer.children(".w-col:not(:last)").children(".column");
            var _$left = _$currentContainer.children(".w-col:not(:first)").children(".column");
            _$right.css("margin-right", n);
            _$left.css("margin-left", n);
        }

        var initColumnSize = function(){
            $columnSizeInput.onlyNum().keydown(function(event){
                if(event.keyCode == 13){
                    var _$this = $(this);
                    var _num = _$this.val();
                    var _$currentContainer = $(".obj_click").find(".diy-inner");
                    var _$columns = _$currentContainer.children(".w-col");
                    //最低为2
                    if(!_num || _num < 2){
                        _num = 2;
                        _$this.val(2);
                    }
                    changeColumnsStyle(_$currentContainer, _$columns, _num);

                }
            });

            $columnSizeInput.keyup(function(e){
                resizeChart();
                LEHistory.trigger();
            });
        };

        var initColumnEqual = function(){
            $(".column-equal").click(function(){
                var _$currentContainer = $(".obj_click").find(".diy-inner");
                var _$columns = _$currentContainer.children(".w-col");
                var _num = $columnSizeInput.val();
                changeColumnsStyle(_$currentContainer, _$columns, _num);

                resizeChart();
                LEHistory.trigger();
            });
        };

        /**
         * 修改column：直接修改成相应的百分比
         * @param _$currentContainer
         * @param _$columns
         * @param _num
         */
        function changeColumnsStyle(_$currentContainer, _$columns, _num){
            //1.保存 column里面的信息
            var _contentArray = [];
            //2. 重新规划布局 - 把column里面的内容放到 _contentArray中
            for(var i = 0; i < _num; i++){
                var _h = _$columns.eq(i).prop("outerHTML") || colHtml;
                _contentArray.push(_h);
            }
            _$currentContainer.html(_contentArray.join(""));
            var _averagePercent = 100 / _num;
            _averagePercent = _averagePercent.toFixed(4) + "%";
            _$columns = _$currentContainer.children(".w-col");
            _$columns.each(function(){
                $(this).attr("class", "w-col");
                $(this).css("width", _averagePercent);
            });
            //6. 重新设置栏间距
            handleColumnMargin(parseInt($columnMarginInput.val()) / 2);
            //7. 重新设置拖动
            LEDrag.columnDraggable();

            //8. 把最后一个拖动去掉
            $(".diy-col-ctrl_disabled").addClass("diy-col-ctrl").removeClass("diy-col-ctrl_disabled");
            _$columns.last().children(".diy-col-ctrl").addClass("diy-col-ctrl_disabled").removeClass("diy-col-ctrl");
        }

        return {
            options: {},
            init: function(){
                initOption();
                //间距
                initColumnMargin();
                //初始化分栏数的事件
                initColumnSize();
                //初始化等分
                initColumnEqual();
            },
            /**
             * options {}
             * @param options
             */
            run: function(options, doHide){
                var _$columnContainer = LECurrentObject;
                //设置 分栏数
                $columnSizeInput.val(_$columnContainer.children(".w-col").size());
                //设置栏间距
                var _$preM = _$columnContainer.children(".w-col:first").children(".column").css("margin-right");
                var _$nextM = _$columnContainer.children(".w-col:eq(1)").children(".column").css("margin-left");
                var _cm = parseInt(_$preM) + parseInt(_$nextM);
                $columnMarginInput.val(_cm);
                //显示
                LEDisplay.show($cbDiv, doHide);
                //切换组件时，样式设置部分回顶部
                sliderbarToTop();
            },
            destroy: function(){
                //$options.first().click();
                $columnSizeInput.val(0);
                $columnMarginInput.val(0);
                $cbDiv.hide();
            }
        }
    };

})(window, jQuery, LE);
