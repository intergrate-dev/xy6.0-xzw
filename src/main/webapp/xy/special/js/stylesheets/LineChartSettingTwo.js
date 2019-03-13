/**
 * Created by qianxm 2016/11/4.
 */
(function(window, $, LE){
    LE.stylesheets["LineChartSettingTwo"] = function(){

        var $columnTitleII = $("#columnTitle-II");
        var $inputColumnTitleII = $("#inputColumnTitle-II");
        var $columnTitlePickII = $("#columnTitlePick-II");
        var $chartTitleNameII = $("#chartTitleName-II");
        var $picChart = $("#picChart");
        var $inputWord = $("#inputWord");
        var $inputWordPick = $("#inputWordPick");
        var $alertChart = $("#alertChart");
        var $tipChartPick = $("#tipChartPick");
        var $inputTipBg = $("#inputTipBg");
        var $tipBgPick = $("#tipBgPick");
        var $inputTipText = $("#inputTipText");
        var $tipTextPick = $("#tipTextPick");
        var $inputTipChart = $("#inputTipChart");
        //图表设置
        var $bgGrid = $("#bgGrid");
        var $inputBgGrid = $("#inputBgGrid");
        var $bgGridPick = $("#bgGridPick");
        var $smoothCurve = $("#smoothCurve");
        var $inputLineWidth = $("#inputLineWidth");
        var $symbolStyle = $("#symbolStyle").find("li");
        var $inputLineX = $("#inputLineX");
        var $inputLineY = $("#inputLineY");
        var $optionXY = $(".namePosition");
        var $inputColorXY = $("#inputColorXY");
        var $colorXYPick = $("#colorXYPick");
        var $inputWidthXY = $("#inputWidthXY");
        var $lineChartSetStyle = $("#LineChartSetStyle");
        //坐标轴字体倾斜
        var $chartFontX = $("#chartFontX");
        var $chartFontY = $("#chartFontY");
        var $inputFontColorXY = $("#inputFontColorXY");
        var $fontColorXYPick = $("#fontColorXYPick");
        var $chartTableSet=$("#chartTableSet");
        var $chartColorPreview=$("#chartColorPreview");

        //  副标题是否显示
        var initTitleIIShowEvent = function(){
            $columnTitleII.on("change",function(){
                var _id=LECurrentObject.attr("id");
                var myChart = echarts.init(document.getElementById(_id));
                var lineOption=lineChart_Map.get(_id);

                if($columnTitleII.is(':checked')){
                    lineOption.title.subtextStyle.fontSize="12";
                }else{
                    lineOption.title.subtextStyle.fontSize="0";
                }
                myChart.setOption(lineOption);
                // 重写图表的配置项和数据
                lineChart_Map.put(_id,lineOption);
                LEHistory.trigger();
            })
        };

        //  副标题颜色修改
        var initTitleColorIIEvent = function(){
            $columnTitlePickII.spectrum(
                LEColorPicker.getOptions(function (tinycolor) {
                    var _v = tinycolor ? tinycolor.toHexString() : "#000000";
                    var _id=LECurrentObject.attr("id");
                    var myChart = echarts.init(document.getElementById(_id));
                    var lineOption=lineChart_Map.get(_id);

                    lineOption.title.subtextStyle.color=_v;
                    $inputColumnTitleII.val(_v);

                    myChart.setOption(lineOption);
                    // 重写图表的配置项和数据
                    lineChart_Map.put(_id,lineOption);
                    LEHistory.trigger();
                })
            )
        };

        var initInputColorIIEvent =function(){
            $inputColumnTitleII.on("change", function () {
                var reg = /^#([0-9a-fA-F]{3}|[0-9a-fA-F]{6})$/;
                var _id=LECurrentObject.attr("id");
                var myChart = echarts.init(document.getElementById(_id));
                var lineOption=lineChart_Map.get(_id);

                if (reg.test($(this).val())) {
                    lineOption.title.subtextStyle.color=$(this).val();
                    $columnTitlePickII.spectrum("set", $(this).val())
                } else {
                    lineOption.title.subtextStyle.color="#333333";
                    $columnTitlePickII.spectrum("set", "#333333")
                }
                myChart.setOption(lineOption);
                lineChart_Map.put(_id,lineOption);

                LEHistory.trigger();
            });
        }

        //  副标题的内容修改
        var initTitleNameIIEvent = function(){
            $chartTitleNameII.on("change",function () {
                var _id=LECurrentObject.attr("id");
                var myChart = echarts.init(document.getElementById(_id));
                var lineOption=lineChart_Map.get(_id);

                lineOption.title.subtext=$(this).val();

                myChart.setOption(lineOption);
                // 重写图表的配置项和数据
                lineChart_Map.put(_id,lineOption);
                LEHistory.trigger();
            })
        };

        // 图例文字是否显示
        var initPicChartEvent = function(){
            $picChart.on("change",function(){
                var _id=LECurrentObject.attr("id");
                var myChart = echarts.init(document.getElementById(_id));
                var lineOption=lineChart_Map.get(_id);

                if($picChart.is(':checked')){
                    lineOption.legend.show=true;
                }else{
                    lineOption.legend.show=false;
                }
                myChart.setOption(lineOption);
                // 重写图表的配置项和数据
                lineChart_Map.put(_id,lineOption);
                LEHistory.trigger();
            })
        };

        // 图例文字的颜色修改
        var initWordPickColorEvent= function(){
            $inputWordPick.spectrum(
                LEColorPicker.getOptions(function (tinycolor) {
                    var _v = tinycolor ? tinycolor.toHexString() : "#000000";
                    var _id=LECurrentObject.attr("id");
                    var myChart = echarts.init(document.getElementById(_id));
                    var lineOption=lineChart_Map.get(_id);

                    lineOption.legend.textStyle.color=_v;
                    $inputWord.val(_v);

                    myChart.setOption(lineOption);
                    // 重写图表的配置项和数据
                    lineChart_Map.put(_id,lineOption);

                    LEHistory.trigger();
                })
            )
        };

        var initInputWordColorEvent = function(){
            $inputWord.on("change", function () {
                var reg = /^#([0-9a-fA-F]{3}|[0-9a-fA-F]{6})$/;
                var _id=LECurrentObject.attr("id");
                var myChart = echarts.init(document.getElementById(_id));
                var lineOption=lineChart_Map.get(_id);

                if (reg.test($(this).val())) {
                    lineOption.legend.textStyle.color=$(this).val();
                    $inputWordPick.spectrum("set", $(this).val())
                } else {
                    lineOption.legend.textStyle.color="#333333";
                    $inputWordPick.spectrum("set", "#333333")
                }
                myChart.setOption(lineOption);
                lineChart_Map.put(_id,lineOption);

                LEHistory.trigger();
            });
        };

        // 提示框是否显示
        var initAlertChartEvent = function(){
            $alertChart.on("change",function(){
                var _id=LECurrentObject.attr("id");
                var myChart = echarts.init(document.getElementById(_id));
                var lineOption=lineChart_Map.get(_id);

                if($alertChart.is(':checked')){
                    lineOption.tooltip.show=true;
                }else{
                    lineOption.tooltip.show=false;
                }
                myChart.setOption(lineOption);
                // 重写图表的配置项和数据
                lineChart_Map.put(_id,lineOption);
                LEHistory.trigger();
            })
        };

        //指示线的颜色修改
        var initTipChartColorEvent = function(){
            $tipChartPick.spectrum(
                LEColorPicker.getOptions(function (tinycolor) {
                    var _v = tinycolor ? tinycolor.toHexString() : "#000000";
                    var _id=LECurrentObject.attr("id");
                    var myChart = echarts.init(document.getElementById(_id));
                    var lineOption=lineChart_Map.get(_id);

                    //判断显示折线图还是柱状图样式
                    var listName=LECurrentObject.attr("data-name");
                    if(listName=="line"){
                        lineOption.tooltip.axisPointer.lineStyle.color=_v;
                    }else if(listName=="bar"){
                        lineOption.tooltip.axisPointer.shadowStyle.color=_v;
                    }
                    $inputTipChart.val(_v);

                    myChart.setOption(lineOption);
                    // 重写图表的配置项和数据
                    lineChart_Map.put(_id,lineOption);

                    LEHistory.trigger();
                })
            )
        };

        var initInputTipColorEvent =function(){
            $inputTipChart.on("change", function () {
                var reg = /^#([0-9a-fA-F]{3}|[0-9a-fA-F]{6})$/;
                var _id=LECurrentObject.attr("id");
                var myChart = echarts.init(document.getElementById(_id));
                var lineOption=lineChart_Map.get(_id);

                if (reg.test($(this).val())) {

                    //判断显示折线图还是柱状图样式
                    var listName=LECurrentObject.attr("data-name");
                    if(listName=="line"){
                        lineOption.tooltip.axisPointer.lineStyle.color=$(this).val();
                    }else if(listName=="bar"){
                        lineOption.tooltip.axisPointer.shadowStyle.color=$(this).val();
                    }
                    $tipChartPick.spectrum("set", $(this).val())
                } else {
                    //判断显示折线图还是柱状图样式
                    var listName=LECurrentObject.attr("data-name");
                    if(listName=="line"){
                        lineOption.tooltip.axisPointer.lineStyle.color="#555555";
                        $tipChartPick.spectrum("set", "#555555");
                    }else if(listName=="bar"){
                        lineOption.tooltip.axisPointer.shadowStyle.color="rgba(150,150,150,0.3)";
                        $tipChartPick.spectrum("set", "rgba(150,150,150,0.3)");
                    }
                }
                myChart.setOption(lineOption);
                lineChart_Map.put(_id,lineOption);

                LEHistory.trigger();
            });
        };

        //提示框背景的颜色修改
        var initTipBgPickEvent = function(){
            $tipBgPick.spectrum(
                LEColorPicker.getOptions(function (tinycolor) {
                    var _v = tinycolor ? tinycolor.toHexString() : "#000000";
                    var _id=LECurrentObject.attr("id");
                    var myChart = echarts.init(document.getElementById(_id));
                    var lineOption=lineChart_Map.get(_id);

                    lineOption.tooltip.backgroundColor=_v;
                    $inputTipBg.val(_v);

                    myChart.setOption(lineOption);
                    // 重写图表的配置项和数据
                    lineChart_Map.put(_id,lineOption);

                    LEHistory.trigger();
                })
            )
        };

        var initInputTipBgEvent = function(){
            $inputTipBg.on("change", function () {
                var reg = /^#([0-9a-fA-F]{3}|[0-9a-fA-F]{6})$/;
                var _id=LECurrentObject.attr("id");
                var myChart = echarts.init(document.getElementById(_id));
                var lineOption=lineChart_Map.get(_id);

                if (reg.test($(this).val())) {
                    lineOption.tooltip.backgroundColor=$(this).val();
                    $tipBgPick.spectrum("set", $(this).val())
                } else {
                    lineOption.tooltip.backgroundColor="rgba(50,50,50,0.7)";
                    $tipBgPick.spectrum("set", "rgba(50,50,50,0.7)")
                }
                myChart.setOption(lineOption);
                lineChart_Map.put(_id,lineOption);

                LEHistory.trigger();
            });
        };
        //提示框文字的颜色修改
        var initTipTextPickEvent=function(){
            $tipTextPick.spectrum(
                LEColorPicker.getOptions(function (tinycolor) {
                    var _v = tinycolor ? tinycolor.toHexString() : "#000000";
                    var _id=LECurrentObject.attr("id");
                    var myChart = echarts.init(document.getElementById(_id));
                    var lineOption=lineChart_Map.get(_id);

                    lineOption.tooltip.textStyle.color=_v;
                    $inputTipText.val(_v);

                    myChart.setOption(lineOption);
                    // 重写图表的配置项和数据
                    lineChart_Map.put(_id,lineOption);

                    LEHistory.trigger();
                })
            )
        };

        var initInputTipTextEvent=function(){
            $inputTipText.on("change", function () {
                var reg = /^#([0-9a-fA-F]{3}|[0-9a-fA-F]{6})$/;
                var _id=LECurrentObject.attr("id");
                var myChart = echarts.init(document.getElementById(_id));
                var lineOption=lineChart_Map.get(_id);

                if (reg.test($(this).val())) {
                    lineOption.tooltip.textStyle.color=$(this).val();
                    $tipTextPick.spectrum("set", $(this).val())
                } else {
                    lineOption.tooltip.textStyle.color="#fff";
                    $tipTextPick.spectrum("set", "#fff")
                }
                myChart.setOption(lineOption);
                lineChart_Map.put(_id,lineOption);

                LEHistory.trigger();
            });
        }

        //  背景网格是否显示
        var initBgGridEvent = function(){
            $bgGrid.on("change",function(){
                var _id=LECurrentObject.attr("id");
                var myChart = echarts.init(document.getElementById(_id));
                var lineOption=lineChart_Map.get(_id);

                if($bgGrid.is(':checked')){
                    lineOption.grid.show=true;
                }else{
                    lineOption.grid.show=false;
                }

                myChart.setOption(lineOption);
                // 重写图表的配置项和数据
                lineChart_Map.put(_id,lineOption);
                LEHistory.trigger();
            })
        };
        //  背景网格的颜色修改
        var initBgGridColorEvent = function(){
            $bgGridPick.spectrum(
                LEColorPicker.getOptions(function (tinycolor) {
                    var _v = tinycolor ? tinycolor.toHexString() : "#000000";
                    var _id=LECurrentObject.attr("id");
                    var myChart = echarts.init(document.getElementById(_id));
                    var lineOption=lineChart_Map.get(_id);

                    lineOption.grid.backgroundColor=_v;
                    $inputBgGrid.val(_v);

                    myChart.setOption(lineOption);
                    // 重写图表的配置项和数据
                    lineChart_Map.put(_id,lineOption);

                    LEHistory.trigger();
                })
            )
        };

        var initInputBgGridEvent = function(){
            $inputBgGrid.on("change", function () {
                var reg = /^#([0-9a-fA-F]{3}|[0-9a-fA-F]{6})$/;
                var _id=LECurrentObject.attr("id");
                var myChart = echarts.init(document.getElementById(_id));
                var lineOption=lineChart_Map.get(_id);

                if (reg.test($(this).val())) {
                    lineOption.grid.backgroundColor=$(this).val();
                    $bgGridPick.spectrum("set", $(this).val())
                } else {
                    lineOption.grid.backgroundColor="transparent";
                    $bgGridPick.spectrum("set", "transparent")
                }
                myChart.setOption(lineOption);
                lineChart_Map.put(_id,lineOption);

                LEHistory.trigger();
            });
        };

        //  平滑曲线是否显示
        var initSmoothCurveEvent = function(){
            $smoothCurve.on("change",function(){
                var _id=LECurrentObject.attr("id");
                var myChart = echarts.init(document.getElementById(_id));
                var lineOption=lineChart_Map.get(_id);

                var len=lineOption.series.length;
                for(var i= 0;i<len;i++){
                    if($smoothCurve.is(':checked')){
                        lineOption.series[i].smooth=true;
                    }else{
                        lineOption.series[i].smooth=false;
                    }
                }

                myChart.setOption(lineOption);
                lineChart_Map.put(_id,lineOption);
                LEHistory.trigger();
            })
        };

        // 线条厚度的修改
        var initLineWidthEvent=function(){
            $inputLineWidth.onlyNum().keydown(function(e){
                var _id=LECurrentObject.attr("id");
                var myChart = echarts.init(document.getElementById(_id));
                var lineOption=lineChart_Map.get(_id);

                var _this=$(this);
                var $target = LECurrentObject;
                numInputKeyDown(e, _this, $target);

                myChart.setOption(lineOption);
                lineChart_Map.put(_id,lineOption);
                LEHistory.trigger();
            });
            $inputLineWidth.blur(function(e){
                var _id=LECurrentObject.attr("id");
                var myChart = echarts.init(document.getElementById(_id));
                var lineOption=lineChart_Map.get(_id);

                var _this=$(this);
                var $target = LECurrentObject;
                numInputBlur(e, _this, $target);

                myChart.setOption(lineOption);
                lineChart_Map.put(_id,lineOption);
                LEHistory.trigger();
            });
            $inputLineWidth.focus(function(e){
                var _id=LECurrentObject.attr("id");
                var myChart = echarts.init(document.getElementById(_id));
                var lineOption=lineChart_Map.get(_id);

                var _this=$(this);
                var $target = LECurrentObject;
                numInputFocus(e, _this, $target);

                myChart.setOption(lineOption);
                lineChart_Map.put(_id,lineOption);
                LEHistory.trigger();
            });
        };
        //图形形状
        var initSymbolStyleEvent=function(){
            $(".symbol").on("click",function(){
                var _id=LECurrentObject.attr("id");
                var myChart = echarts.init(document.getElementById(_id));
                var lineOption=lineChart_Map.get(_id);

                _this=$(this);
                var len=lineOption.series.length;
                for(var i= 0;i<len;i++){
                    $symbolStyle.each(function(){
                        lineOption.series[i].symbol=_this.val();
                    });
                }

                myChart.setOption(lineOption);
                lineChart_Map.put(_id,lineOption);
                LEHistory.trigger();
            })
        };
        //X轴名称，Y轴名称
        var initTextNameXEvent=function(){
            $inputLineX.on("change",function () {
                var _id=LECurrentObject.attr("id");
                var myChart = echarts.init(document.getElementById(_id));
                var lineOption=lineChart_Map.get(_id);
                //判断显示折线图还是柱状图样式
                var listName = LECurrentObject.attr("data-name");
                if(listName == "line"){
                    lineOption.xAxis.name=$(this).val();
                }else if(listName == "bar"){
                    lineOption.xAxis[0].name=$(this).val();
                }
                myChart.setOption(lineOption);
                // 重写图表的配置项和数据
                lineChart_Map.put(_id,lineOption);
                LEHistory.trigger();
            })
        };
        var initTextNameYEvent=function(){
            $inputLineY.on("change",function () {
                var _id=LECurrentObject.attr("id");
                var myChart = echarts.init(document.getElementById(_id));
                var lineOption=lineChart_Map.get(_id);

                //判断显示折线图还是柱状图样式
                var listName = LECurrentObject.attr("data-name");
                if(listName == "line"){
                    lineOption.yAxis.name=$(this).val();
                }else if(listName == "bar"){
                    lineOption.yAxis[0].name=$(this).val();
                }
                myChart.setOption(lineOption);
                // 重写图表的配置项和数据
                lineChart_Map.put(_id,lineOption);
                LEHistory.trigger();
            })
        };
       // 轴名称位置的修改
        var initOptionXYEvent=function(){
            $optionXY.on("change",function () {
                var _id=LECurrentObject.attr("id");
                var myChart = echarts.init(document.getElementById(_id));
                var lineOption=lineChart_Map.get(_id);
                //判断显示折线图还是柱状图样式
                var listName = LECurrentObject.attr("data-name");
                $optionXY.each(function(){
                    if(listName == "line"){
                        lineOption.xAxis.nameLocation=$(this).val();
                        lineOption.yAxis.nameLocation=$(this).val();
                    }else if(listName == "bar"){
                        lineOption.xAxis[0].nameLocation=$(this).val();
                        lineOption.yAxis[0].nameLocation=$(this).val();
                    }
                })

                myChart.setOption(lineOption);
                // 重写图表的配置项和数据
                lineChart_Map.put(_id,lineOption);
                LEHistory.trigger();
            })
        };
        //轴线颜色的修改
        var initLineColorXYPickEvent=function(){
            $colorXYPick.spectrum(
                LEColorPicker.getOptions(function (tinycolor) {
                    var _v = tinycolor ? tinycolor.toHexString() : "#000000";
                    var _id=LECurrentObject.attr("id");
                    var myChart = echarts.init(document.getElementById(_id));
                    var lineOption=lineChart_Map.get(_id);
                    //判断显示折线图还是柱状图样式
                    var listName = LECurrentObject.attr("data-name");
                    if(listName == "line"){
                        lineOption.xAxis.axisLine.lineStyle.color=_v;
                        lineOption.yAxis.axisLine.lineStyle.color=_v;
                    }else if(listName == "bar"){
                        lineOption.xAxis[0].axisLine.lineStyle.color=_v;
                        lineOption.yAxis[0].axisLine.lineStyle.color=_v;
                    }
                    $inputColorXY.val(_v);

                    myChart.setOption(lineOption);
                    // 重写图表的配置项和数据
                    lineChart_Map.put(_id,lineOption);

                    LEHistory.trigger();
                })
            )
        };
        var initLineInputColorXYEvent=function(){
            $inputColorXY.on("change", function () {
                var reg = /^#([0-9a-fA-F]{3}|[0-9a-fA-F]{6})$/;
                var _id=LECurrentObject.attr("id");
                var myChart = echarts.init(document.getElementById(_id));
                var lineOption=lineChart_Map.get(_id);
                //判断显示折线图还是柱状图样式
                var listName = LECurrentObject.attr("data-name");
                if (reg.test($(this).val())) {
                    if(listName == "line"){
                        lineOption.xAxis.axisLine.lineStyle.color=$(this).val();
                        lineOption.yAxis.axisLine.lineStyle.color=$(this).val();
                    }else if(listName == "bar"){
                        lineOption.xAxis[0].axisLine.lineStyle.color=$(this).val();
                        lineOption.yAxis[0].axisLine.lineStyle.color=$(this).val();
                    }
                    $colorXYPick.spectrum("set", $(this).val())
                } else {
                    if(listName == "line"){
                        lineOption.xAxis.axisLine.lineStyle.color="#333";
                        lineOption.yAxis.axisLine.lineStyle.color="#333";
                    }else if(listName == "bar"){
                        lineOption.xAxis[0].axisLine.lineStyle.color="#333";
                        lineOption.yAxis[0].axisLine.lineStyle.color="#333";
                    }
                    $colorXYPick.spectrum("set", "#333")
                }
                myChart.setOption(lineOption);
                lineChart_Map.put(_id,lineOption);

                LEHistory.trigger();
            });
        };
        //轴线的厚度--X轴,Y轴
        var initInputWidthXYEvent=function(){
            $inputWidthXY.onlyNum().keydown(function(e){
                var _id=LECurrentObject.attr("id");
                var myChart = echarts.init(document.getElementById(_id));
                var lineOption=lineChart_Map.get(_id);

                var _this=$(this);
                var $target = LECurrentObject;
                numInputKeyDownXY(e, _this, $target);

                myChart.setOption(lineOption);
                lineChart_Map.put(_id,lineOption);
                LEHistory.trigger();
            });
            $inputWidthXY.blur(function(e){
                var _id=LECurrentObject.attr("id");
                var myChart = echarts.init(document.getElementById(_id));
                var lineOption=lineChart_Map.get(_id);

                var _this=$(this);
                var $target = LECurrentObject;
                numInputBlurXY(e, _this, $target);

                myChart.setOption(lineOption);
                lineChart_Map.put(_id,lineOption);
                LEHistory.trigger();
            });
            $inputWidthXY.focus(function(e){
                var _id=LECurrentObject.attr("id");
                var myChart = echarts.init(document.getElementById(_id));
                var lineOption=lineChart_Map.get(_id);

                var _this=$(this);
                var $target = LECurrentObject;
                numInputFocusXY(e, _this, $target);

                myChart.setOption(lineOption);
                lineChart_Map.put(_id,lineOption);
                LEHistory.trigger();
            });
        };

        //坐标轴字体--X,Y轴文字倾斜的修改
        var initFontXStyleEvent=function(){
            $chartFontX.on("change",function(){
                var _id=LECurrentObject.attr("id");
                var myChart = echarts.init(document.getElementById(_id));
                var lineOption=lineChart_Map.get(_id);
                //判断显示折线图还是柱状图样式
                var listName = LECurrentObject.attr("data-name");
                if(listName == "line"){
                    if($chartFontX.is(':checked')){
                        lineOption.xAxis.axisLabel.textStyle.fontStyle="italic";
                    }else{
                        lineOption.xAxis.axisLabel.textStyle.fontStyle="normal";
                    }
                }else if(listName == "bar"){
                    if($chartFontX.is(':checked')){
                        lineOption.xAxis[0].axisLabel.textStyle.fontStyle="italic";
                    }else{
                        lineOption.xAxis[0].axisLabel.textStyle.fontStyle="normal";
                    }
                }
                myChart.setOption(lineOption);
                lineChart_Map.put(_id,lineOption);
                LEHistory.trigger();
            })
        };
        var initFontYStyleEvent=function(){
            $chartFontY.on("change",function(){
                var _id=LECurrentObject.attr("id");
                var myChart = echarts.init(document.getElementById(_id));
                var lineOption=lineChart_Map.get(_id);
                //判断显示折线图还是柱状图样式
                var listName = LECurrentObject.attr("data-name");
                if(listName == "line"){
                    if($chartFontY.is(':checked')){
                        lineOption.yAxis.axisLabel.textStyle.fontStyle="italic";
                    }else{
                        lineOption.yAxis.axisLabel.textStyle.fontStyle="normal";
                    }
                }else if(listName == "bar"){
                    if($chartFontY.is(':checked')){
                        lineOption.yAxis[0].axisLabel.textStyle.fontStyle="italic";
                    }else{
                        lineOption.yAxis[0].axisLabel.textStyle.fontStyle="normal";
                    }
                }
                myChart.setOption(lineOption);
                lineChart_Map.put(_id,lineOption);
                LEHistory.trigger();
            })
        };
        //坐标轴字体--文字颜色的修改
        var initColorXYPickEvent=function(){
            $fontColorXYPick.spectrum(
                LEColorPicker.getOptions(function (tinycolor) {
                    var _v = tinycolor ? tinycolor.toHexString() : "#000000";
                    var _id=LECurrentObject.attr("id");
                    var myChart = echarts.init(document.getElementById(_id));
                    var lineOption=lineChart_Map.get(_id);
                    //判断显示折线图还是柱状图样式
                    var listName = LECurrentObject.attr("data-name");
                    if(listName == "line"){
                        lineOption.xAxis.axisLabel.textStyle.color=_v;
                        lineOption.yAxis.axisLabel.textStyle.color=_v;
                    }else if(listName == "bar"){
                        lineOption.xAxis[0].axisLabel.textStyle.color=_v;
                        lineOption.yAxis[0].axisLabel.textStyle.color=_v;
                    }
                    $inputFontColorXY.val(_v);

                    myChart.setOption(lineOption);
                    // 重写图表的配置项和数据
                    lineChart_Map.put(_id,lineOption);

                    LEHistory.trigger();
                })
            )
        };
        var initInputColorXYEvent=function(){
            $inputFontColorXY.on("change", function () {
                var reg = /^#([0-9a-fA-F]{3}|[0-9a-fA-F]{6})$/;
                var _id=LECurrentObject.attr("id");
                var myChart = echarts.init(document.getElementById(_id));
                var lineOption=lineChart_Map.get(_id);
                //判断显示折线图还是柱状图样式
                var listName = LECurrentObject.attr("data-name");
                if (reg.test($(this).val())) {
                    if(listName == "line"){
                        lineOption.xAxis.axisLabel.textStyle.color=$(this).val();
                        lineOption.yAxis.axisLabel.textStyle.color=$(this).val();
                    }else if(listName == "bar"){
                        lineOption.xAxis[0].axisLabel.textStyle.color=$(this).val();
                        lineOption.yAxis[0].axisLabel.textStyle.color=$(this).val();
                    }
                    $fontColorXYPick.spectrum("set", $(this).val())
                } else {
                    if(listName == "line"){
                        lineOption.xAxis.axisLabel.textStyle.color="#333";
                        lineOption.yAxis.axisLabel.textStyle.color="#333";
                    }else if(listName == "bar"){
                        lineOption.xAxis[0].axisLabel.textStyle.color="#333";
                        lineOption.yAxis[0].axisLabel.textStyle.color="#333";
                    }
                    $fontColorXYPick.spectrum("set", "#333")
                }
                myChart.setOption(lineOption);
                lineChart_Map.put(_id,lineOption);

                LEHistory.trigger();
            });
        };

        //线条厚度输入框
        function numInputKeyDown(event, $this,$target){
                //如果是上下的话，执行以下的操作
                if(event.keyCode == 38 || event.keyCode == 40 || event.keyCode == 13){
                    var _value = $this.val();
                    var num = parseInt(_value);
                    if(isNaN(num)) return;

                    checkUnit(_value, $this);
                    var _unit = $this.attr("data-unit") || "px";
                    //点击上下按钮，来改变当前的值
                    event.keyCode == 38 && ++num;
                    event.keyCode == 40 && --num;

                    var _id=LECurrentObject.attr("id");
                    var myChart = echarts.init(document.getElementById(_id));
                    var lineOption=lineChart_Map.get(_id);

                    var len=lineOption.series.length;
                    for(var i= 0;i<len;i++){
                        lineOption.series[i].lineStyle.normal.width=num;
                    }

                    myChart.setOption(lineOption);
                    lineChart_Map.put(_id,lineOption);

                    $this.val(num+_unit);
                    return num;

                }
                return null;
        };
        //线条厚度输入框失去焦点
        function numInputBlur(event, $this,$target){
            var _value = $this.val();
            var num = parseInt(_value);
            if(isNaN(num)) return;
            checkUnit(_value, $this);
            var num = parseInt(_value);
            var _unit = $this.attr("data-unit") || "px";

            var _id=LECurrentObject.attr("id");
            var myChart = echarts.init(document.getElementById(_id));
            var lineOption=lineChart_Map.get(_id);

            var len=lineOption.series.length;
            for(var i= 0;i<len;i++){
                lineOption.series[i].lineStyle.normal.width=num;
            }

            myChart.setOption(lineOption);
            lineChart_Map.put(_id,lineOption);
        };
        //线条厚度输入框获取焦点
        function numInputFocus(event, $this, $target){
            var _value = $this.val();
            var num = parseInt(_value);

            var _id=LECurrentObject.attr("id");
            var myChart = echarts.init(document.getElementById(_id));
            var lineOption=lineChart_Map.get(_id);

            var len=lineOption.series.length;
            for(var i= 0;i<len;i++){
               lineOption.series[i].lineStyle.normal.width=num;
            }

            myChart.setOption(lineOption);
            lineChart_Map.put(_id,lineOption);
        };
        function checkUnit(_value, $this){
            //如果包含%，就以%为主
            if(_value.indexOf("%") != -1){
                $this.attr("data-unit", "%");
            } else if(_value.indexOf("px") != -1){
                $this.attr("data-unit", "px");
            }
        };
        //轴线厚度输入框
        function numInputKeyDownXY(event, $this,$target){
            //如果是上下的话，执行以下的操作
            if(event.keyCode == 38 || event.keyCode == 40 || event.keyCode == 13){
                var _value = $this.val();
                var num = parseInt(_value);
                if(isNaN(num)) return;

                checkUnit(_value, $this);
                var _unit = $this.attr("data-unit") || "px";
                //点击上下按钮，来改变当前的值
                event.keyCode == 38 && ++num;
                event.keyCode == 40 && --num;

                var _id=LECurrentObject.attr("id");
                var myChart = echarts.init(document.getElementById(_id));
                var lineOption=lineChart_Map.get(_id);
                //判断显示折线图还是柱状图样式
                var listName = LECurrentObject.attr("data-name");
                if(listName == "line"){
                    lineOption.xAxis.axisLine.lineStyle.width=num;
                    lineOption.yAxis.axisLine.lineStyle.width=num;
                }else if(listName == "bar"){
                    lineOption.xAxis[0].axisLine.lineStyle.width=num;
                    lineOption.yAxis[0].axisLine.lineStyle.width=num;
                }

                myChart.setOption(lineOption);
                lineChart_Map.put(_id,lineOption);

                $this.val(num+_unit);
                return num;

            }
            return null;
        };
        //轴线厚度输入框失去焦点
        function numInputBlurXY(event, $this,$target){
            var _value = $this.val();
            var num = parseInt(_value);
            if(isNaN(num)) return;
            checkUnit(_value, $this);
            var num = parseInt(_value);
            var _unit = $this.attr("data-unit") || "px";

            var _id=LECurrentObject.attr("id");
            var myChart = echarts.init(document.getElementById(_id));
            var lineOption=lineChart_Map.get(_id);
            //判断显示折线图还是柱状图样式
            var listName = LECurrentObject.attr("data-name");
            if(listName == "line"){
                lineOption.xAxis.axisLine.lineStyle.width=num;
                lineOption.yAxis.axisLine.lineStyle.width=num;
            }else if(listName == "bar"){
                lineOption.xAxis[0].axisLine.lineStyle.width=num;
                lineOption.yAxis[0].axisLine.lineStyle.width=num;
            }

            myChart.setOption(lineOption);
            lineChart_Map.put(_id,lineOption);
        };
        //轴线厚度输入框获取焦点
        function numInputFocusXY(event, $this, $target){
            var _value = $this.val();
            var num = parseInt(_value);

            var _id=LECurrentObject.attr("id");
            var myChart = echarts.init(document.getElementById(_id));
            var lineOption=lineChart_Map.get(_id);
            //判断显示折线图还是柱状图样式
            var listName = LECurrentObject.attr("data-name");
            if(listName == "line"){
                lineOption.xAxis.axisLine.lineStyle.width=num;
                lineOption.yAxis.axisLine.lineStyle.width=num;
            }else if(listName == "bar"){
                lineOption.xAxis[0].axisLine.lineStyle.width=num;
                lineOption.yAxis[0].axisLine.lineStyle.width=num;
            }
            myChart.setOption(lineOption);
            lineChart_Map.put(_id,lineOption);
        };

        var resetTitleIIShowEvent =function(){
             var _id=LECurrentObject.attr("id");
             var json=lineChart_Map.get(_id);
             var titleShow=json.title.subtextStyle.fontSize;
             if(titleShow==='12'){
                 $columnTitleII.attr("checked", true);
             }else if(titleShow==='0'){
                 $columnTitleII.attr("checked", false);
             }
        };

        var resetTitleColorIIEvent = function(){
            var _id=LECurrentObject.attr("id");
            var json=lineChart_Map.get(_id);
            var titleColor=json.title.subtextStyle.color;
            if(titleColor){
                $inputColumnTitleII.val(titleColor);
                $columnTitlePickII.spectrum("set", titleColor);
            }
        };

        var resetTitleNameIIEvent = function(){
            var _id=LECurrentObject.attr("id");
            var json=lineChart_Map.get(_id);
            var titleShow=json.title.subtext;
            if(titleShow){
                $chartTitleNameII.val(titleShow);
            }
        };

        var resetPicChartEvent = function(){
            var _id=LECurrentObject.attr("id");
            var json=lineChart_Map.get(_id);
            var legendShow=json.legend.show;
            if(legendShow==true){
                $picChart.attr("checked", true);
            }else if(legendShow==false){
                $picChart.attr("checked", false);
            }
        };

        var resetPicWordColorEvent = function(){
            var _id=LECurrentObject.attr("id");
            var json=lineChart_Map.get(_id);
            var legendColor=json.legend.textStyle.color;
            if(legendColor){
                $inputWord.val(legendColor);
                $inputWordPick.spectrum("set",legendColor);
            }
        };

        var resetAlertChartEvent =function(){
            var _id=LECurrentObject.attr("id");
            var json=lineChart_Map.get(_id);
            var tipShow=json.tooltip.show;
            if(tipShow==true){
                $alertChart.attr("checked", true);
            }else if(tipShow==false){
                $alertChart.attr("checked", false);
            }
        };
        var resetTipChartColorEvent = function(){
            var _id=LECurrentObject.attr("id");
            var json=lineChart_Map.get(_id);
            //判断显示折线图还是柱状图样式
            var listName=LECurrentObject.attr("data-name");
            if(listName=="line"){
                var lineColor=json.tooltip.axisPointer.lineStyle.color;
                $inputTipChart.val(lineColor);
                $tipChartPick.spectrum("set",lineColor);
            }else if(listName=="bar"){
                var lineColor=json.tooltip.axisPointer.shadowStyle.color;
                $tipChartPick.spectrum("set",lineColor);
                $inputTipChart.val($tipChartPick.spectrum("get").toHexString());
            }
        };

        var resetTipBgPickEvent = function(){
            var _id=LECurrentObject.attr("id");
            var json=lineChart_Map.get(_id);
            var tipBg=json.tooltip.backgroundColor;
            if(tipBg){
                $tipBgPick.spectrum("set",tipBg);
                $inputTipBg.val($tipBgPick.spectrum("get").toHexString());
            }
        };

        var resetTipTextPickEvent=function(){
            var _id=LECurrentObject.attr("id");
            var json=lineChart_Map.get(_id);
            var tipText=json.tooltip.textStyle.color;
            if(tipText){
                $inputTipText.val(tipText);
                $tipTextPick.spectrum("set",tipText);
            }
        };

        var resetBgGridEvent = function(){
            var _id=LECurrentObject.attr("id");
            var json=lineChart_Map.get(_id);
            var gridShow=json.grid.show;
            if(gridShow==true){
                $bgGrid.attr("checked", true);
            }else if(gridShow==false){
                $bgGrid.attr("checked", false);
            }
        };

        var resetBgGridColorEvent = function(){
            var _id=LECurrentObject.attr("id");
            var json=lineChart_Map.get(_id);
            var bgGridColor=json.grid.backgroundColor;
            if(bgGridColor){
                $bgGridPick.spectrum("set",bgGridColor);
                $inputBgGrid.val($bgGridPick.spectrum("get").toHexString());
            }
        };
        //图形设置 提示线 图表设置的显示隐藏
        var resetLineChartSetStyleEvent=function() {
            //判断显示折线图还是柱状图样式
            var listName = LECurrentObject.attr("data-name");
            if(listName == "line"){
                $lineChartSetStyle.show();
            }else if(listName == "bar"){
                $lineChartSetStyle.hide();
            };
            if(listName == "pie" || listName == "wordCloud" || listName == "map"){
                $(".tip").hide();
                $chartTableSet.hide();
            }else{
                $(".tip").show();
                $chartTableSet.show();
            };
            if(listName == "wordCloud"){
                //图例文字隐藏
                $(".picLegend").hide();
                //图表配色隐藏
                $(".echartColor").hide();
                $chartColorPreview.hide();
            }else{
                $(".picLegend").show();
                $(".echartColor").show();
                $chartColorPreview.show();
            };
            if(listName == "map"){
                $(".styleManager").hide();
                $("#lineChartModelPreview").hide();
            }else{
                $(".styleManager").show();
                $("#lineChartModelPreview").show();
            }
        };
        var resetSmoothCurveEvent =function(){
            var _id=LECurrentObject.attr("id");
            var json=lineChart_Map.get(_id);

            var len=json.series.length;
            for(var i= 0;i<len;i++){
                var smoothShow=json.series[i].smooth;
                if(smoothShow==true){
                    $smoothCurve.attr("checked", true);
                }else if(smoothShow==false){
                    $smoothCurve.attr("checked", false);
                }
            }
        };
        var resetLineWidthEvent=function(){
            var _id=LECurrentObject.attr("id");
            var json=lineChart_Map.get(_id);

            var len=json.series.length;
            for(var i= 0;i<len;i++){
                var lineWidth=json.series[i].lineStyle.normal.width;
                if(lineWidth){
                    $inputLineWidth.val(lineWidth);
                }
            }
        };
        var resetSymbolStyleEvent=function(){
            var _id=LECurrentObject.attr("id");
            var json=lineChart_Map.get(_id);
            var symbolShow = json.series[0].symbol;
            $symbolStyle.each(function(){
                if (symbolShow==$(this).find("input").val()) {
                    $(this).find("input").attr("checked","checked");
                }
            })
        };

        var resetTextNameXEvent=function(){
            var _id=LECurrentObject.attr("id");
            var json=lineChart_Map.get(_id);
            //判断显示折线图还是柱状图样式
            var listName = LECurrentObject.attr("data-name");
            if(listName == "line"){
                var nameShow=json.xAxis.name;
            }else if(listName == "bar"){
                var nameShow=json.xAxis[0].name;
            }
            if(nameShow){
                $inputLineX.val(nameShow);
            }
        };
        var resetTextNameYEvent=function(){
            var _id=LECurrentObject.attr("id");
            var json=lineChart_Map.get(_id);
            //判断显示折线图还是柱状图样式
            var listName = LECurrentObject.attr("data-name");
            if(listName == "line"){
                var nameShow=json.yAxis.name;
            }else if(listName == "bar"){
                var nameShow=json.yAxis[0].name;
            }
            if(nameShow){
                $inputLineY.val(nameShow);
            }
        };

        var resetOptionXYEvent=function(){
            var _id=LECurrentObject.attr("id");
            var json=lineChart_Map.get(_id);
            //判断显示折线图还是柱状图样式
            var listName = LECurrentObject.attr("data-name");
            if(listName == "line"){
                var localShow=json.xAxis.nameLocation;
                var localShow=json.yAxis.nameLocation;
            }else if(listName == "bar"){
                var localShow=json.xAxis[0].nameLocation;
                var localShow=json.yAxis[0].nameLocation;
            }
            if(localShow){
                $optionXY.val(localShow);
            }
        };
        var resetLineColorXYEvent=function(){
            var _id=LECurrentObject.attr("id");
            var json=lineChart_Map.get(_id);
            //判断显示折线图还是柱状图样式
            var listName = LECurrentObject.attr("data-name");
            if(listName == "line"){
                var lineColor=json.xAxis.axisLine.lineStyle.color;
                var lineColor=json.yAxis.axisLine.lineStyle.color;
            }else if(listName == "bar"){
                var lineColor=json.xAxis[0].axisLine.lineStyle.color;
                var lineColor=json.yAxis[0].axisLine.lineStyle.color;
            }
            if(lineColor){
                $inputColorXY.val(lineColor);
                $colorXYPick.spectrum("set",lineColor);
            }
        };
        var resetInputWidthXYEvent=function(){
            var _id=LECurrentObject.attr("id");
            var json=lineChart_Map.get(_id);
            //判断显示折线图还是柱状图样式
            var listName = LECurrentObject.attr("data-name");
            if(listName == "line"){
                var lineWidth=json.xAxis.axisLine.lineStyle.width;
                var lineWidth=json.yAxis.axisLine.lineStyle.width;
            }else if(listName == "bar"){
                var lineWidth=json.xAxis[0].axisLine.lineStyle.width;
                var lineWidth=json.yAxis[0].axisLine.lineStyle.width;
            }
            if(lineWidth){
                $inputWidthXY.val(lineWidth);
            }
        };

        var resetFontXStyleEvent =function(){
            var _id=LECurrentObject.attr("id");
            var json=lineChart_Map.get(_id);
            //判断显示折线图还是柱状图样式
            var listName = LECurrentObject.attr("data-name");
            if(listName == "line"){
                var fontStyle=json.xAxis.axisLabel.textStyle.fontStyle;
            }else if(listName == "bar"){
                var fontStyle=json.xAxis[0].axisLabel.textStyle.fontStyle;
            }
            if(fontStyle=='italic'){
                $chartFontX.attr("checked", true);
            }else if(fontStyle=='normal'){
                $chartFontX.attr("checked", false);
            }
        };
        var resetFontYStyleEvent=function(){
            var _id=LECurrentObject.attr("id");
            var json=lineChart_Map.get(_id);
            //判断显示折线图还是柱状图样式
            var listName = LECurrentObject.attr("data-name");
            if(listName == "line"){
                var fontStyle=json.yAxis.axisLabel.textStyle.fontStyle;
            }else if(listName == "bar"){
                var fontStyle=json.yAxis[0].axisLabel.textStyle.fontStyle;
            }
            if(fontStyle=='italic'){
                $chartFontY.attr("checked", true);
            }else if(fontStyle=='normal'){
                $chartFontY.attr("checked", false);
            }
        };
        var resetFontXYColorStyleEvent=function(){
            var _id=LECurrentObject.attr("id");
            var json=lineChart_Map.get(_id);
            //判断显示折线图还是柱状图样式
            var listName = LECurrentObject.attr("data-name");
            if(listName == "line"){
                var fontXColor=json.xAxis.axisLabel.textStyle.color;
                var fontYColor=json.yAxis.axisLabel.textStyle.color;
            }else if(listName == "bar"){
                var fontXColor=json.xAxis[0].axisLabel.textStyle.color;
                var fontYColor=json.yAxis[0].axisLabel.textStyle.color;
            }
            if(fontXColor){
                $inputFontColorXY.val(fontXColor);
                $fontColorXYPick.spectrum("set",fontXColor);
            }
        };

        return {
            init: function () {
                initTitleIIShowEvent();
                initTitleColorIIEvent();
                initInputColorIIEvent();
                initTitleNameIIEvent();
                initPicChartEvent();
                initWordPickColorEvent();
                initInputWordColorEvent();
                initAlertChartEvent();
                initTipChartColorEvent();
                initInputTipColorEvent();
                initTipBgPickEvent();
                initInputTipBgEvent();
                initTipTextPickEvent();
                initInputTipTextEvent();
                initBgGridEvent();
                initInputBgGridEvent();
                initBgGridColorEvent();
                initSmoothCurveEvent();
                initLineWidthEvent();
                initSymbolStyleEvent();
                initTextNameXEvent();
                initTextNameYEvent();
                initOptionXYEvent();
                initLineColorXYPickEvent();
                initLineInputColorXYEvent();
                initInputWidthXYEvent();
                initFontXStyleEvent();
                initFontYStyleEvent();
                initColorXYPickEvent();
                initInputColorXYEvent();

            },
            run: function (options, doHide) {
            //    resetTitleIIShowEvent();
            //    resetTitleColorIIEvent();
            //    resetTitleNameIIEvent();
                var listName = LECurrentObject.attr("data-name");
                if(listName != "wordCloud"){
                    //图例文字的显示和颜色改变
                    resetPicChartEvent();
                    resetPicWordColorEvent();
                }
                resetAlertChartEvent();
                resetTipBgPickEvent();
                resetTipTextPickEvent();

                resetLineChartSetStyleEvent();
                if(listName == "line"){
                    //平滑曲线 线条厚度 图形形状
                    resetSmoothCurveEvent();
                    resetLineWidthEvent();
                    resetSymbolStyleEvent();
                }
              //如果是折线图和柱状图 执行
                if(listName == "line" || listName == "bar"){
                    resetTipChartColorEvent();
                    //背景网格初始化
                    resetBgGridEvent();
                    resetBgGridColorEvent();
                    //坐标轴初始化
                    resetTextNameXEvent();
                    resetTextNameYEvent();
                    resetOptionXYEvent();
                    resetLineColorXYEvent();
                    resetInputWidthXYEvent();
                    resetFontXStyleEvent();
                    resetFontYStyleEvent();
                    resetFontXYColorStyleEvent();
                }
                //切换组件时，样式设置部分回顶部
                sliderbarToTop();
            },
            destroy: function () {

            }
        };
    };
})(window, jQuery, LE, undefined);