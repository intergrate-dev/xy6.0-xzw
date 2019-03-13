/**
 * Created by qianxm 2016/11/4.
 */
(function(window, $, LE){
    LE.stylesheets["LineChartSetting"] = function(){
        var  $panel = $("#LineChartSection");
        var $chartColorPreview = $("#chartColorPreview");
        var $chartColorGoBack = $("#chartColorGoBack");
        var $chartColorDiv = $("#chartColorDiv");

        var $editDataBtn=$("#editDataBtn");
        var $chartPicGoBack = $("#chartPicGoBack");
        var $lineChartModelPreview = $("#lineChartModelPreview");
        var $chartPicDiv= $("#chartPicDiv");
        var $chartTableSet=$("#chartTableSet");
        var $lineChartGoBack = $("#lineChartGoBack");
        var $lineChartDiv= $("#lineChartDiv");
        var $columnTitle=$("#columnTitle");
        var $chartTitleName=$("#chartTitleName");
        var $columnTitlePick=$("#columnTitlePick");
        var $chartTitleColor=$("#chartTitleColor");
        var $exportDataBtn=$("#exportDataBtn");

        function importPieAndCloudData(data,_getOption){
            var pieOptionLen = _getOption.series[0].data.length;
            data.push(["",""]);
            for(var k=0;k<pieOptionLen;k++){
                data.push([]);
                data[k+1].push(_getOption.series[0].data[k].name);
                data[k+1].push(_getOption.series[0].data[k].value);
            }
        }

        function importMapData(data,_getOption){
            data.push([]);
            data[0].push("");
            var length=_getOption.series.length;
            for (var i = 0; i < length; i++) {
                data[0].push(_getOption.legend.data[i]);
                var mapOptionLen = _getOption.series[i].data.length;
                if(i==0){
                    for(var k=0;k<mapOptionLen;k++) {
                        data.push([]);
                        data[k + 1].push(_getOption.series[i].data[k].name);
                        data[k + 1].push(_getOption.series[i].data[k].value);
                    }
                }else{
                    for(var k=0;k<mapOptionLen;k++){
                        data[k+1].push(_getOption.series[i].data[k].value);
                    }
                }
            }
        }

        function importLineAndBarData(data,_getOption,_name){
            data.push([]);
            data[0].push("");
            if (_name == "line") {
                var _lenX = _getOption.xAxis.data.length;
                for (var n = 0; n < _lenX; n++) {
                    data[0].push(_getOption.xAxis.data[n]);
                }
            } else if (_name == "bar") {
                var x = _getOption.xAxis;
                var y = _getOption.yAxis;
                var absoluteY = [{type: 'value'}];
                if (y[0].type == absoluteY[0].type) {
                    var _lenX = _getOption.xAxis[0].data.length;
                    for (var n = 0; n < _lenX; n++) {
                        data[0].push(_getOption.xAxis[0].data[n]);
                    }
                } else if (y[0].type != absoluteY[0].type) {
                    var _lenX = _getOption.yAxis[0].data.length;
                    for (var n = 0; n < _lenX; n++) {
                        data[0].push(_getOption.yAxis[0].data[n]);
                    }
                }
            }

            var length=_getOption.series.length;
            for(var i= 0;i<length;i++){
                data.push([]);
                var _len=_getOption.series[i].data.length;
                data[i+1].push(_getOption.legend.data[i]);
                //数值
                for(var m=0;m<_len;m++){
                    data[i+1].push(_getOption.series[i].data[m]);
                }
            }
        }

        function getLengend(_name,lineOption,_leng,jsonData){
            if(_name == "line" || _name == "bar" ||_name == "pie"){
                var legendArr=[];
                var _num=(_name == "pie" && jsonData[0][0])?0:1;
                for(var i=_num;i<_leng;i++){
                    legendArr.push(jsonData[i][0]);
                }
                lineOption.legend.data=JSON.parse(JSON.stringify(legendArr));
            }
            return legendArr;
        }
        function getMapData(lineOption,_leng,jsonData){
            /**
             * 修改series分以下几步
             * 1.定义空数组seriesArray
             * 2.复制series[0]作为每个对象的模板 在此基础上修改
             * 3.修改每个series中的data   并修改原有option的图例
             * 4.将每个series塞到seriesArray中
             * 5.清空原有的series
             * 6.新生成的seriesArray赋给series
             * 7.重新修改每个series中的name
             * 8.修改VisualMap中的最大值与最小值
             * */
            var series0=JSON.stringify(lineOption.series[0]);
            var seriesArray=[];

            var mapLen=jsonData[0].length;
            var legendArr=[];
            for(var n=1;n<mapLen;n++){
                seriesArray.push(JSON.parse(series0));

                var mapDataArray=[];
                for(var i=1;i<_leng;i++){
                    var pieJson={"name":"","value":""};
                    pieJson.name=jsonData[i][0];
                    pieJson.value=jsonData[i][n];
                    mapDataArray.push(pieJson);
                }
                legendArr.push(jsonData[0][n]);
                var _clonemapDataArray=mapDataArray.concat();
                seriesArray[n-1].data=_clonemapDataArray;
            }
            lineOption.legend.data=JSON.parse(JSON.stringify(legendArr));
            lineOption.series.length=0;
            lineOption.series=JSON.parse(JSON.stringify(seriesArray));

            for(var n=1;n<mapLen;n++){
                lineOption.series[n-1].name=jsonData[0][n];
            }

            minAndMaxValueToVisualMap(jsonData,lineOption);
        }

        function minAndMaxValueToVisualMap(jsonData,lineOption){
            //获取地图数据的最大值和最小值赋给VisualMap
            var oneArr=jsonData.join(",").split(",");
            var numArr=oneArr.filter(function(x){
                var reg=/^[-]*[0-9]+[.]*[0-9]*$/;
                return reg.test(x)
            });
            var minNum=Math.floor(Math.min.apply(null,numArr));
            var maxNum=Math.ceil(Math.max.apply(null,numArr));
            if(minNum<0 ||minNum>100){
                lineOption.visualMap.min=minNum;
            }else{
                lineOption.visualMap.min=0;
            }

            lineOption.visualMap.max=maxNum
        }
        function getPieAndCloudData(lineOption,_leng,jsonData,_name){
            var pieArray=[];
            var _num=!!jsonData[0][0]?0:1;
                for(var i=_num;i<_leng;i++){

                    var pieJson={};
                    if(_name=="pie") {
                         pieJson = {"name": "", "value": ""};
                    }else if(_name=="wordCloud") {
                        var oldColor=lineOption.series[0].data[0].textStyle.normal.color;
                        var redReg=/^rgb\([0-9]{1,3},0,0\)$/;
                        var greenReg=/^rgb\(0,[0-9]{1,3},0\)$/;
                        var blueReg=/^rgb\(0,0,[0-9]{1,3}\)$/;
                        var _color;
                        if(redReg.test(oldColor)){
                            _color=LE.plugins["LineChartWordCloud"]().randomColor.redStyle()
                        }else if(greenReg.test(oldColor)){
                            _color=LE.plugins["LineChartWordCloud"]().randomColor.greenStyle()
                        }else if(blueReg.test(oldColor)){
                            _color=LE.plugins["LineChartWordCloud"]().randomColor.blueStyle()
                        }else{
                            _color=LE.plugins["LineChartWordCloud"]().randomColor.colorStyle()
                        }

                         pieJson = {
                            "name": "",
                            "value": "",
                            textStyle: {
                                normal: {
                                    color: _color
                                },
                                emphasis: {
                                    shadowBlur: 10,
                                    shadowColor: '#333'
                                }
                            }
                        };
                    }
                    pieJson.name=jsonData[i][0];
                    pieJson.value=jsonData[i][1];
                    pieArray.push(pieJson);
                }
            lineOption.series[0].data.length=0;
            lineOption.series[0].data=pieArray;
        }

        function getLineAndBarData(lineOption,_getOption,_name,_leng,jsonData){
            if(_name=="line"){
                lineOption.xAxis.data=JSON.parse(JSON.stringify(jsonData[0].slice(1)));
            }else if(_name=="bar"){
                var x=_getOption.xAxis;
                var y=_getOption.yAxis;
                var absoluteY=[{type : 'value'}];
                if(y[0].type==absoluteY[0].type){
                    lineOption.xAxis[0].data=JSON.parse(JSON.stringify(jsonData[0].slice(1)));
                }else if(y[0].type!=absoluteY[0].type){
                    lineOption.yAxis[0].data=JSON.parse(JSON.stringify(jsonData[0].slice(1)));
                }
            }

            // 修改数据
            var series0=JSON.stringify(lineOption.series[0]);

            var seriesArray=[];
            /*seriesArray.map(function(item,i){
             item.name = legendArr[i];
             })*/
            for(var m=0;m<_leng-1;m++){
                //获取legendArr的返回值
                var legendArr=getLengend(_name,lineOption,_leng,jsonData);
                seriesArray.push(JSON.parse(series0));
                seriesArray[m].name=legendArr[m];
                seriesArray[m].data=jsonData[m+1].slice(1);
            }
            lineOption.series.length=0;
            lineOption.series=JSON.parse(JSON.stringify(seriesArray));
        }

        function dataToChart(jsonData,_name,_getOption){
            // 初始化
            var _id=LECurrentObject.attr("id");
            var myChart = echarts.init(document.getElementById(_id));
            var lineOption=lineChart_Map.get(_id);
            var _leng=jsonData.length;
            //生成图例
            getLengend(_name,lineOption,_leng,jsonData);
            if(_name=="map"){
                //地图数据修改
                getMapData(lineOption,_leng,jsonData);
            }else if(_name=="pie" || _name=="wordCloud"){
                //饼图修改数据，标签云修改数据
                getPieAndCloudData(lineOption,_leng,jsonData,_name);
            }else if(_name=="line"||_name=="bar" ){
                //柱形图与折线图
                getLineAndBarData(lineOption,_getOption,_name,_leng,jsonData);
            }
            myChart.setOption(lineOption);
            // 重写图表的配置项和数据
            lineChart_Map.put(_id,lineOption);
        }


        var initImportDataEvent=function(){
            $editDataBtn.on("click",function() {
                //option中data提取[         ["","周一","周二"],
                // ["第一条数据","123","123"],
                // ["第二条数据","123","123"]]
                var _id = LECurrentObject.attr("id");
                var _getOption = lineChart_Map.get(_id);
                var data = [];
                //判断区分图表
                var _name = LECurrentObject.attr("data-name");
                if(_name == "pie" || _name == "wordCloud"){
                    //饼图数,标签云的数据解析
                    importPieAndCloudData(data,_getOption);
                }else if(_name == "map"){
                    //地图的数据解析 生成图例
                    importMapData(data,_getOption);
                }else if(_name == "line"||_name == "bar"){
                    //折线图 柱形图数据解析
                    importLineAndBarData(data,_getOption,_name);
                }

                var timeStamp = new Date().getTime();
                LEDialog.toggleDialog(LE.options["Dialog"].lineChartDataSettingDialog + "timestamp=" + timeStamp, function (jsonData) {
                    dataToChart(jsonData,_name,_getOption);
                },data)
                LEHistory.trigger();
            })
        };


        var initexportDataEvent=function(){
            $exportDataBtn.on("click",function() {
                var data=[];
                var _id = LECurrentObject.attr("id");
                var _getOption = lineChart_Map.get(_id);
                var _name = LECurrentObject.attr("data-name");
                var timeStamp = new Date().getTime();
                LEDialog.toggleDialog(LE.options["Dialog"].excelUploadDialog + "timestamp=" + timeStamp,
                    function(jsonExcel){
                        dataToChart(jsonExcel,_name,_getOption);
//                        console.log(jsonExcel);
                    },
                    data
                );
                LEHistory.trigger();
            })
        };

        //图表配色模板滑出
        var initChartColorModel = function(){
            $chartColorPreview.on("click",function(){
                $chartColorGoBack.animate({right: "0"}, "fast", "linear");
                $chartColorDiv.animate({right: "0"}, "fast", "linear");
            });
            $chartColorGoBack.on("click", function (){
                $chartColorGoBack.animate({right: "-250px"}, "fast", "linear");
                $chartColorDiv.animate({right: "-250px"}, "fast", "linear");
            });
        };

        //图表配色样式修改
        var initChartColorChange = function(){
            $chartColorDiv.find(".displayStyle").click(function () {
                var _this=$(this);
                $chartColorPreview.html(_this.prop("outerHTML"));
                $chartColorPreview.children("ul").removeClass("displayStyle").removeClass("box-shadow").removeClass("mgt10");

                LECurrentObject.attr("data-ref",_this.attr("data-ref"));
                // 初始化
                var _id=LECurrentObject.attr("id");
                var myChart = echarts.init(document.getElementById(_id));
                // 获取图表的配置项和数据
                var lineOption=lineChart_Map.get(_id);
                // 修改color
                var divLen=_this.find("div").length;
                var oldColorArr=lineOption.color;
                var oldColorArrLen=oldColorArr.length;
                for(var i=0;i<divLen;i++){
                    var _c=_this.find("div").eq(i).css("background-color");
                    if(oldColorArrLen>divLen){
                        oldColorArr.splice(i,1,_c);
                    }else{
                        oldColorArr.splice(i,0,_c);
                    }
                }
                lineOption.color=oldColorArr;
                // 修改bgcolor
                var bgColor=_this.find("li").css("background-color");
                lineOption.backgroundColor=bgColor;
                //修改文本颜色
                var textColor= _this.attr("data-textcolor");
                var subTextColor=_this.attr("data-subtextcolor");
                var eitherColor=_this.attr("data-eithercolor");
                /*    lineOption.title.textStyle.color=textColor;
                lineOption.title.subtextStyle.color=subTextColor;  */
                lineOption.legend.textStyle.color=eitherColor;
                //判断区分折线图与柱形图
                var _name=LECurrentObject.attr("data-name");
                if(_name=="line") {
                    lineOption.xAxis.axisLabel.textStyle.color = eitherColor;
                    lineOption.xAxis.axisLine.lineStyle.color = eitherColor;
                    lineOption.yAxis.axisLabel.textStyle.color = eitherColor;
                    lineOption.yAxis.axisLine.lineStyle.color = eitherColor;
                }else if(_name=="bar"){
                    lineOption.xAxis[0].axisLabel.textStyle.color = eitherColor;
                    lineOption.xAxis[0].axisLine.lineStyle.color = eitherColor;
                    lineOption.yAxis[0].axisLabel.textStyle.color = eitherColor;
                    lineOption.yAxis[0].axisLine.lineStyle.color = eitherColor;
                }
                //图表为地图时，区块颜色，填充颜色，字体颜色的修改
                if(_name=="map"){
                    //修改visualMap中颜色的修改
                   lineOption.visualMap.inRange.color=oldColorArr.slice(0,3);
                    //字体颜色的修改
                   lineOption.series[0].label.normal.textStyle.color=oldColorArr[4];
                   //悬停时区域颜色的修改
                   lineOption.series[0].itemStyle.emphasis.areaColor=oldColorArr[3];
                }

                myChart.setOption(lineOption);
                // 重写图表的配置项和数据
                lineChart_Map.put(_id,lineOption);
                LEHistory.trigger();
            });
        };

        var initTypeChange=function(){
            //图表模板滑出
            $lineChartModelPreview.bind("click", function () {
                //判断显示折线图还是柱状图样式还是饼图
                var listName=LECurrentObject.attr("data-name");
                if(listName=="line"){
                    $('.barList').hide();
                    $('.pieList').hide();
                    $('.lineList').show();
                    $('.wordCloudList').hide();
                }else if(listName=="bar"){
                    $('.lineList').hide();
                    $('.pieList').hide();
                    $('.barList').show();
                    $('.wordCloudList').hide();
                }else if(listName=="pie"){
                    $('.lineList').hide();
                    $('.barList').hide();
                    $('.pieList').show();
                    $('.wordCloudList').hide();
                }else if(listName=="wordCloud"){
                    $('.lineList').hide();
                    $('.barList').hide();
                    $('.pieList').hide();
                    $('.wordCloudList').show();
                }
                //滚动条置顶
                $chartPicDiv.scrollTop(0);
                $chartPicDiv.perfectScrollbar("update");

                $chartPicGoBack.animate({right: "0"}, "fast", "linear");
                $chartPicDiv.animate({right: "0"}, "fast", "linear");
            });

            $chartPicGoBack.bind("click", function () {
                $chartPicGoBack.animate({right: "-250px"}, "fast", "linear");
                $chartPicDiv.animate({right: "-250px"}, "fast", "linear");
            });

            //图表设置滑出
            $chartTableSet.on("click", function () {
                $lineChartDiv.scrollTop(0);
                $lineChartDiv.perfectScrollbar("update");

                $lineChartGoBack.animate({right: "0"}, "fast", "linear");
                $lineChartDiv.animate({right: "0"}, "fast", "linear");
            });

            $lineChartGoBack.on("click", function () {
                $lineChartGoBack.animate({right: "-250px"}, "fast", "linear");
                $lineChartDiv.animate({right: "-250px"}, "fast", "linear");
            });
        };

        //图表样式修改
        var initChangeChartStyle=function(){
            $chartPicDiv.find(".displayStyle").click(function (e) {
                //判断显示折线图还是柱状图和饼图样式
                var listName=LECurrentObject.attr("data-name");
                var _this=$(this);
                $lineChartModelPreview.find("img").attr("src",_this.find("img").attr("src"));

                // 初始化
                var _id=LECurrentObject.attr("id");
                var myChart = echarts.init(document.getElementById(_id));
                // 获取图表的配置项和数据
                var lineOption=lineChart_Map.get(_id);

                var length=lineOption.series.length;
                if(listName=="line"){
                    for(var i= 0;i<length;i++){
                        if(_this.attr("data-ref")=="chartPic_style1"){
                            lineOption.series[i].areaStyle={};
                        }else if(_this.attr("data-ref")=="chartPic_style2"){
                            lineOption.series[i].areaStyle={normal:{}};
                        }
                    }
                }else if(listName=="bar"){
                    var x=lineOption.xAxis;
                    var y=lineOption.yAxis;
                    var absoluteY=[{type : 'value'}];

                    var arr=['null','广告','广告','广告','搜索','搜索引擎','搜索引擎','搜索引擎','搜索引擎'];
                    if(_this.attr("data-ref")=="chartPic_style4"){
                        for(var i=0;i<length;i++){
                            lineOption.series[i].stack=arr[i];
                        }
                        if(y[0].type!=absoluteY[0].type){
                            lineOption.xAxis =y;
                            lineOption.yAxis =x;
                        }

                    }else if(_this.attr("data-ref")=="chartPic_style3"){
                        for(var i=0;i<length;i++) {
                            lineOption.series[i].stack = '配置';
                        }
                        if(y[0].type!=absoluteY[0].type){
                            lineOption.xAxis =y;
                            lineOption.yAxis =x;
                        }
                    }else if(_this.attr("data-ref")=="chartPic_style5"){
                        for(var i=0;i<length;i++) {
                            lineOption.series[i].stack = arr[i];
                        }
                        if(y[0].type==absoluteY[0].type){
                            lineOption.xAxis =y;
                            lineOption.yAxis =x;
                        }

                    }else if(_this.attr("data-ref")=="chartPic_style6"){
                        for(var i=0;i<length;i++) {
                            lineOption.series[i].stack = '配置';
                        }
                        if(y[0].type==absoluteY[0].type){
                            lineOption.xAxis =y;
                            lineOption.yAxis =x;
                        }
                    }
                }else if(listName=="pie"){
                    if(_this.attr("data-ref")=="chartPic_style7"){
                        for(var i= 0;i<length;i++){
                            lineOption.series[i].roseType=false;
                        }
                    }else if(_this.attr("data-ref")=="chartPic_style8"){
                        for(var i= 0;i<length;i++) {
                            lineOption.series[i].roseType = 'angle';
                        }
                    }
                }else if(listName=="wordCloud"){
                    var len=lineOption.series[0].data.length;
                    if(_this.attr("data-ref")=="chartPic_style9"){
                        lineOption.series[0].rotationRange=[0,0];
                        for(var i=0;i<len;i++){
                            lineOption.series[0].data[i].textStyle.normal.color=LE.plugins["LineChartWordCloud"]().randomColor.redStyle();
                        }
                    }else if(_this.attr("data-ref")=="chartPic_style10"){
                        lineOption.series[0].rotationRange=[90,90];
                        for(var i=0;i<len;i++){
                            lineOption.series[0].data[i].textStyle.normal.color=LE.plugins["LineChartWordCloud"]().randomColor.greenStyle();
                        }
                    }else if(_this.attr("data-ref")=="chartPic_style11"){
                        lineOption.series[0].rotationRange=[-45,45];
                        for(var i=0;i<len;i++){
                            lineOption.series[0].data[i].textStyle.normal.color=LE.plugins["LineChartWordCloud"]().randomColor.blueStyle();
                        }
                    }else if(_this.attr("data-ref")=="chartPic_style12"){
                        lineOption.series[0].rotationRange=[-90,90];
                        for(var i=0;i<len;i++){
                            lineOption.series[0].data[i].textStyle.normal.color=LE.plugins["LineChartWordCloud"]().randomColor.colorStyle();
                        }
                    }
                }

                myChart.setOption(lineOption);
                // 重写图表的配置项和数据
                lineChart_Map.put(_id,lineOption);
                resetOption();
                LEHistory.trigger();
            });
        };
        //标题修改
        var initTitleShowChangeEvent=function(){
            $columnTitle.on("change",function () {
                var _id=LECurrentObject.attr("id");
                var myChart = echarts.init(document.getElementById(_id));
                var lineOption=lineChart_Map.get(_id);

                if($columnTitle.is(':checked')){
                    lineOption.title.textStyle.fontSize="14";
                }else{
                    lineOption.title.textStyle.fontSize="0";
                }
                myChart.setOption(lineOption);
                // 重写图表的配置项和数据
                lineChart_Map.put(_id,lineOption);
                resetOption();
                LEHistory.trigger();
            })
        };

        //标题内容修改
        var initTitleTextChangeEvent=function(){
            $chartTitleName.on("change",function () {
                var _id=LECurrentObject.attr("id");
                var myChart = echarts.init(document.getElementById(_id));
                var lineOption=lineChart_Map.get(_id);

                lineOption.title.text=$(this).val();

                myChart.setOption(lineOption);
                // 重写图表的配置项和数据
                lineChart_Map.put(_id,lineOption);
                resetOption();
                LEHistory.trigger();
            })
        };

        //标题颜色修改
        var initTitleColorChangeEvent=function(){
            $columnTitlePick.spectrum(
                LEColorPicker.getOptions(function (tinycolor) {
                    var _v = tinycolor ? tinycolor.toHexString() : "#000000";
                    var _id=LECurrentObject.attr("id");
                    var myChart = echarts.init(document.getElementById(_id));
                    var lineOption=lineChart_Map.get(_id);

                    lineOption.title.textStyle.color=_v;
                    $chartTitleColor.val(_v);

                    myChart.setOption(lineOption);
                    // 重写图表的配置项和数据
                    lineChart_Map.put(_id,lineOption);
                    resetOption();

                    LEHistory.trigger();
                })
            );
        };

        var initinputColorChangeEvent = function () {
            $chartTitleColor.bind("change", function () {
                var reg = /^#([0-9a-fA-F]{3}|[0-9a-fA-F]{6})$/;
                var _id=LECurrentObject.attr("id");
                var myChart = echarts.init(document.getElementById(_id));
                var lineOption=lineChart_Map.get(_id);

                if (reg.test($(this).val())) {
                    lineOption.title.textStyle.color=$(this).val();
                    $columnTitlePick.spectrum("set", $(this).val())
                } else {
                    lineOption.title.textStyle.color="#333333";
                    $columnTitlePick.spectrum("set", "#333333")
                }
                myChart.setOption(lineOption);
                lineChart_Map.put(_id,lineOption);
                resetOption();
                LEHistory.trigger();
            });
        };


        var resetOption=function(){
            //判断显示折线图还是柱状图样式
            var listName=LECurrentObject.attr("data-name");
            var _id=LECurrentObject.attr("id");
            var json=lineChart_Map.get(_id);
            if(listName=="line"){
                var areaStyle=JSON.stringify(json.series[0].areaStyle);
                if(areaStyle=='{}'){
                    $lineChartModelPreview.find("img").attr("src",$chartPicDiv.children(".displayStyle").eq(0).find("img").attr("src"));
                }else if(areaStyle=='{"normal":{}}'){
                    $lineChartModelPreview.find("img").attr("src",$chartPicDiv.children(".displayStyle").eq(1).find("img").attr("src"));
                }
            }else if(listName=="bar"){
                var len=json.series.length;
                var y=json.yAxis;
                var absoluteY=[{type : 'value'}];
                for(var i= 0;i<len;i++){
                    var _s=json.series[i].stack;
                    if((_s!='配置')&& (y[0].type==absoluteY[0].type)){
                        $lineChartModelPreview.find("img").attr("src",$chartPicDiv.children(".displayStyle").eq(3).find("img").attr("src"));
                    }else if((_s=='配置')&& (y[0].type==absoluteY[0].type)){
                        $lineChartModelPreview.find("img").attr("src",$chartPicDiv.children(".displayStyle").eq(2).find("img").attr("src"));
                    }else if((_s!='配置')&& (y[0].type!=absoluteY[0].type)){
                        $lineChartModelPreview.find("img").attr("src",$chartPicDiv.children(".displayStyle").eq(4).find("img").attr("src"));
                    }else if((_s=='配置')&& (y[0].type!=absoluteY[0].type)){
                        $lineChartModelPreview.find("img").attr("src",$chartPicDiv.children(".displayStyle").eq(5).find("img").attr("src"));
                    }
                }
            }else if(listName=="pie"){
                var roseType=json.series[0].roseType;
                if(roseType==false){
                    $lineChartModelPreview.find("img").attr("src",$chartPicDiv.children(".displayStyle").eq(6).find("img").attr("src"));
                }else if(roseType=='angle'){
                    $lineChartModelPreview.find("img").attr("src",$chartPicDiv.children(".displayStyle").eq(7).find("img").attr("src"));
                }
            }else if(listName=="wordCloud"){
                var range=json.series[0].rotationRange;
                if(range.toString()==[-90,90].toString()){
                    $lineChartModelPreview.find("img").attr("src",$chartPicDiv.children(".displayStyle").eq(11).find("img").attr("src"));
                }else if(range.toString()==[0,0].toString()){
                    $lineChartModelPreview.find("img").attr("src",$chartPicDiv.children(".displayStyle").eq(8).find("img").attr("src"));
                }else if(range.toString()==[90,90].toString()){
                    $lineChartModelPreview.find("img").attr("src",$chartPicDiv.children(".displayStyle").eq(9).find("img").attr("src"));
                }else if(range.toString()==[-45,45].toString()){
                    $lineChartModelPreview.find("img").attr("src",$chartPicDiv.children(".displayStyle").eq(10).find("img").attr("src"));
                }
            }
        };

        var resetChartColorOption = function(){
            var _id=LECurrentObject.attr("id");
            var json=lineChart_Map.get(_id);
            var colorArr=json.color;
            var _dataRef=LECurrentObject.attr("data-ref");
            var l = $chartColorDiv.find("ul").length;
            if(!_dataRef){
                $chartColorPreview.html($chartColorDiv.children("ul").eq(0).prop("outerHTML"));
                $chartColorPreview.children("ul").removeClass("displayStyle").removeClass("box-shadow").removeClass("mgt10");
            }else{
                for(var i=0;i<l;i++){
                    var _chartRef = $chartColorDiv.find("ul.displayStyle").eq(i).attr("data-ref");
                    if (_dataRef == _chartRef) {
                        $chartColorPreview.html($chartColorDiv.children("ul").eq(i).prop("outerHTML"));
                        $chartColorPreview.children("ul").removeClass("displayStyle").removeClass("box-shadow").removeClass("mgt10");
                    }
                }
            }
        };

        var resetTitleOption=function(){
            var _id=LECurrentObject.attr("id");
            var json=lineChart_Map.get(_id);
            var titleShow=json.title.textStyle.fontSize;
            if(titleShow==='14'){
                $columnTitle.attr("checked", true);
            }else if(titleShow==='0'){
                $columnTitle.attr("checked", false);
            }
        };

        var resetTitleTextOption=function(){
            var _id=LECurrentObject.attr("id");
            var json=lineChart_Map.get(_id);
            var titleShow=json.title.text;
            if(titleShow){
                $chartTitleName.val(titleShow);
            }
        };

        var resetTitleColorOption=function(){
            var _id=LECurrentObject.attr("id");
            var json=lineChart_Map.get(_id);
            var titleColor=json.title.textStyle.color;
            if(titleColor){
                $chartTitleColor.val(titleColor);
                $columnTitlePick.spectrum("set", titleColor)
            }
        };

        return {
            init: function () {
                initImportDataEvent();
                initChartColorModel();
                initChartColorChange();
                initTypeChange();
                initChangeChartStyle();
                initTitleShowChangeEvent();
                initTitleTextChangeEvent();
                initTitleColorChangeEvent();
                initinputColorChangeEvent();
                initexportDataEvent();
            },
            run: function (options, doHide) {
                resetOption();
                resetChartColorOption();
            //    resetTitleOption();
            //    resetTitleTextOption();
            //    resetTitleColorOption();
                //切换组件时，样式设置部分回顶部
                sliderbarToTop();
                LEDisplay.show($panel, doHide);
            },
            destroy: function () {
                $panel.hide();
                $chartPicGoBack.animate({right: "-250px"}, "fast", "linear");
                $chartPicDiv.animate({right: "-250px"}, "fast", "linear");

                $chartColorGoBack.animate({right: "-250px"}, "fast", "linear");
                $chartColorDiv.animate({right: "-250px"}, "fast", "linear");

                $lineChartGoBack.animate({right: "-250px"}, "fast", "linear");
                $lineChartDiv.animate({right: "-250px"}, "fast", "linear");
            }
        };
    };
})(window, jQuery, LE, undefined);