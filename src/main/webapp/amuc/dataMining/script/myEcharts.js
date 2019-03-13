var ECharts = { 

    ChartConfig: function (container, option) {
        var chart_path = "../script/ECharts/js/echarts"; //配置图表请求路径 
        var map_path = "../script/ECharts/js/echarts-map"; //配置地图的请求路径 echarts/config

        require.config({//引入常用的图表类型的配置
            paths: {
                echarts: chart_path,
                'echarts/chart/bar': chart_path,
                'echarts/chart/pie': chart_path,
                'echarts/chart/line': chart_path,
                'echarts/chart/k': chart_path,
                'echarts/chart/scatter': chart_path,
                'echarts/chart/radar': chart_path,
                'echarts/chart/chord': chart_path,
                'echarts/chart/force': chart_path,
                'echarts/chart/map': map_path
            }
        });

        this.option = { chart: {}, option: option, container: container };
        return this.option;

    },
     
    ChartDataFormate: {
        FormateNOGroupData: function (data) {
            //data的格式如上的Result1，这种格式的数据，多用于饼图、单一的柱形图的数据源
            var categories = [];
            var datas = [];
            for (var i = 0; i < data.length; i++) {
                categories.push(data[i].name || "");   //data[i].name为各地区名 ，data[i].value为各地区的数量
                datas.push({ name: data[i].name, value: data[i].value || 0 });
            }
            return { category: categories, data: datas };   //categories为总的地区名，以“,”分割
        },

        FormateGroupData: function (data, type, is_stack) {   
            //data的格式如上的Result2，type为要渲染的图表类型：可以为line，bar，is_stack表示为是否是堆积图，这种格式的数据多用于展示多条折线图、分组的柱图
            var chart_type = 'line';
            if (type)
                chart_type = type || 'line';

            var xAxis = [];
            var group = [];
            var series = [];

            for (var i = 0; i < data.length; i++) {
                for (var j = 0; j < xAxis.length && xAxis[j] != data[i].name; j++);
                if (j == xAxis.length)
                    xAxis.push(data[i].name);

                for (var k = 0; k < group.length && group[k] != data[i].group; k++);
                if (k == group.length)
                    group.push(data[i].group);
            }

            for (var i = 0; i < group.length; i++) {
                var temp = [];
                for (var j = 0; j < data.length; j++) {
                    if (group[i] == data[j].group) {
                        if (type == "map") {
                            temp.push({ name: data[j].name, value: data[i].value });
                        } else {
                            temp.push(data[j].value);
                        }
                    }

                }

                switch (type) { 
                    case 'bar': 
                        var series_temp = { name: group[i], data: temp, type: chart_type }; 
                        if (is_stack) 
                            series_temp = $.extend({}, { stack: 'stack' }, series_temp); 
                        break;

                    case 'map': 
                        var series_temp = { 
                            name: group[i], type: chart_type, mapType: 'china', selectedMode: 'single', 
                            itemStyle: { 
                                normal: { label: { show: true} },
                                emphasis: { label: { show: true} } 
                              }, 
                            data: temp 
                        }; 
                        break;

                    case 'line': 
                        var series_temp = { name: group[i], data: temp, type: chart_type }; 
                        if (is_stack) 
                            series_temp = $.extend({}, { stack: 'stack' }, series_temp); 
                        break;

                    default: 
                      var series_temp = { name: group[i], data: temp, type: chart_type }; 
                }
                 series.push(series_temp); 
                } 
            return { category: group, xAxis: xAxis, series: series }; 
        }
    },
  
    ChartOptionTemplates: {
        CommonOption: {
        //通用的图表基本配置 
            tooltip: { 
                trigger: 'axis'//tooltip触发方式:axis以X轴线触发,item以每一个数据项触发 
            }, 
            toolbox: { 
                show: true, //是否显示工具栏 
                feature: { 
                    mark: true, 
                    dataView: { readOnly: false }, //数据预览 
                    restore: true, //复原 
                    saveAsImage: true //是否保存图片 
                } 
            } 
         },

        CommonLineOption: {//通用的折线图表的基本配置 
            tooltip: { 
                trigger: 'axis'
            },
            calculable: true,
            toolbox: { 
                show: true, 
                feature: { 
                    dataView: { readOnly: false }, //数据预览
                    restore: true, //复原
                    saveAsImage: true, //是否保存图片
                    magicType: ['line', 'bar']//支持柱形图和折线图的切换 
                } 
            } 
        },

        Pie: function (data, name) {
            //data:数据格式：{name：xxx,value:xxx}...
            var pie_datas = ECharts.ChartDataFormate.FormateNOGroupData(data);

            var option = {
                tooltip: {
                    trigger: 'item',
                    formatter: '{b} : {c} ({d}/%)',
                    show: true
                }, 

                legend: {
                    orient: 'vertical',
                    x : 'left',
                    //y : 70,
                    data: pie_datas.category
                },

                calculable: true, 

                toolbox: {
                    show: true,
                    x : 'right',
                    //y : 70,
                    feature: {
                        mark: { show: true },
                        dataView: { show: true, readOnly: true },
                        restore: { show: true },
                        saveAsImage: { show: true }
                    }
                },
                series: [
                    {
                        name: name || "",
                        type: 'pie',
                        radius: '50%',
                        itemStyle : {
                            normal : {
                                label : {
                                    show : true
                                  
                                },
                                labelLine : {
                                  show : true,
                                  length: 20
                                }
                            },
                            emphasis : {
                                label : {
                                    show : true,
                                    position : 'center',
                                    textStyle : {
                                        fontSize : '30',
                                        fontWeight : 'bold'
                                    }
                                }
                            }
                        },
                        center: ['50%', '50%'],
                        data: pie_datas.data
                    }
                ]
            };
            return $.extend({}, ECharts.ChartOptionTemplates.CommonOption, option);
        },

        Lines: function (data, name, is_stack) { 
            //data:数据格式：{name：xxx,group:xxx,value:xxx}... 
            var stackline_datas = ECharts.ChartDataFormate.FormateGroupData(data, 'line', is_stack); 
            var option = {
                legend: { data: stackline_datas.category
                   },

                xAxis: [{ 
                    type: 'category', //X轴均为category，Y轴均为value 
                    data: stackline_datas.xAxis,
                     boundaryGap: false//数值轴两端的空白策略 
                }],

                yAxis: [{ 
                    name: name || '', 
                    type: 'value', 
                    splitArea: { show: true } 
                }], 
                series: stackline_datas.series 
            }; 
            return $.extend({}, ECharts.ChartOptionTemplates.CommonLineOption, option); 
        },

        Bars: function (data, name, xName, is_stack) {
            //data:数据格式：{name：xxx,group:xxx,value:xxx}...
            var bars_dates = ECharts.ChartDataFormate.FormateGroupData(data, 'bar', is_stack); 
            var option = {
        		legend: { data: bars_dates.category },
                xAxis: [{ 
                    type: 'category', 
                    data: bars_dates.xAxis, 
                    name: xName || '',
                    axisLabel: { 
                        show: true, 
                        interval: 'auto', 
                        rotate: 0, 
                        margion: 8 
                    } 
                }],

                yAxis: [{ 
                    type: 'value', 
                    name: name || '', 
                    splitArea: { show: true } 
                }], 
                series: bars_dates.series 
            }; 
//            if (bars_dates.category.length == 1)
//                option.legend = bars_dates.category;
            return $.extend({}, ECharts.ChartOptionTemplates.CommonLineOption, option); 
        },
        Map: function (data, name) {
        	
            //data:数据格式：{name：xxx,value:xxx}...
            var map_datas = ECharts.ChartDataFormate.FormateNOGroupData(data);
            var option = {
            		title: {
				        text : '全国34个省市自治区',
				        subtext : 'china （点击切换）'
				    },
                    tooltip: {   //小提示
                        trigger: 'item',
                        formatter: '{b}:{c0}',   //{b}<br/>{a0}:{c0}
                        show: true
                    },
                    dataRange: {
                        orient: 'horizontal',
                        x: 'center',
                        y: 'top',
                        min: 0,
                        max: 50000,
                        text: ['高', '低'],
                        color:['orange','yellow'],
                        splitNumber: 1000,
                        calculable: true,
                        textStyle: {
                            color: 'black'
                        }
                    },  
                   
                    series: [
                        {
                            name: name || "",
                            type: 'map',
                            mapType: 'china',
                            /*mapLocation: {   //控制地图的位置
                                x: 'left',
                                y: 'top',
                               // width: '30%'
                            },*/
                            //roam: true,   //地图能否自由移动
                            selectedMode: 'single',  //选择类型single、double
                            itemStyle: {
                                normal: {     //正常的状态是否显示地名
                                    label: { show: true },
                                    //color: '#EDBCA0'
                                }, //地图上是否显示城市名称
                                emphasis: { label: { show: true} }
                            },
                            data: map_datas.data
                        }
                    ]
                };
            return $.extend({}, ECharts.ChartOptionTemplates.CommonOption, option);
        }
    },
 
    Charts: { 
        RenderChart: function (option) { 
            require([
             
                'echarts', 
                'echarts/chart/line', 
                'echarts/chart/bar', 
                'echarts/chart/pie', 
                'echarts/chart/k', 
                'echarts/chart/scatter', 
                'echarts/chart/radar', 
                'echarts/chart/chord', 
                'echarts/chart/force',
                'echarts/chart/map' 
                ],

				function (ec) { 
            	//alert(staticChart.datas);
					echarts = ec; 
					if (option.chart && option.chart.dispose) 
						option.chart.dispose();

					option.chart = echarts.init(option.container); 
					window.onresize = option.chart.resize; 
					option.chart.setOption(option.option, true); 
					
					var myChart = option.chart;
					var ecConfig = require('echarts/config');
					var zrEvent = require('zrender/tool/event');
					var curIndx = 0;
					var mapType = [
					    'china',
					    // 23个省
					    '广东', '青海', '四川', '海南', '陕西', 
					    '甘肃', '云南', '湖南', '湖北', '黑龙江',
					    '贵州', '山东', '江西', '河南', '河北',
					    '山西', '安徽', '福建', '浙江', '江苏', 
					    '吉林', '辽宁', '台湾',
					    // 5个自治区
					    '新疆', '广西', '宁夏', '内蒙古', '西藏', 
					    // 4个直辖市
					    '北京', '天津', '上海', '重庆',
					    // 2个特别行政区
					    '香港', '澳门'
					];
					/*document.getElementById('echart').onmousewheel = function (e){
					    var event = e || window.event;
					    curIndx += zrEvent.getDelta(event) > 0 ? (-1) : 1;
					    if (curIndx < 0) {
					        curIndx = mapType.length - 1;
					    }
					    var mt = mapType[curIndx % mapType.length];
					    if (mt == 'china') {
					    	option.option.tooltip.formatter = '{b}';
					    }
					    else{
					    	option.option.tooltip.formatter = '{b}';
					    }
					    option.option.series[0].mapType = mt;
					    option.option.title.subtext = mt + ' （滚轮或点击切换）';
					    myChart.setOption(option.option, true);
					    
					    zrEvent.stop(event);
					};*/
					myChart.on(ecConfig.EVENT.MAP_SELECTED, function (param){
					    var len = mapType.length;
					    var mt = mapType[curIndx % len];
					    if (mt == 'china') {
					        // 全国选择时指定到选中的省份
					        var selected = param.selected;
					        for (var i in selected) {
					            if (selected[i]) {
					                mt = i;
					                while (len--) {
					                    if (mapType[len] == mt) {
					                        curIndx = len;
					                    }
					                }
					                break;
					            }
					        }
					        option.option.tooltip.formatter = '{b}:{c0}';
					    }
					    else {
					        curIndx = 0;
					        mt = 'china';
					        option.option.tooltip.formatter = '{b}:{c0}';
					    }
					    option.option.series[0].mapType = mt;
					    option.option.title.subtext = mt + ' （点击切换）';
					    myChart.setOption(option.option, true);
					});
					option.option = {
					    title: {
					        text : '全国34个省市自治区',
					        subtext : 'china （点击切换）'
					    },
					    tooltip : {
					        trigger: 'item',
					        formatter: '{b}:{c0}',
					        //show: true
					    },
					    toolbox: { 
			                show: true, //是否显示工具栏 
			                feature: { 
			                    mark: true, 
			                    dataView: { readOnly: false }, //数据预览 
			                    restore: true, //复原 
			                    saveAsImage: true //是否保存图片 
			                } 
			            }, 
					    /*legend: {
					        orient: 'vertical',
					        //color: '#22a0fb',
					        x:'left',
					        //y:'bottom',
					        data:['会员数量（人）']
					    },*/
					    dataRange: {
					    	orient: 'horizontal',
	                        x: 'center',
	                        y: 'top',
					        min: 0,
					        max: 50000,
					        splitNumber: 1000,
					        color:['orange','yellow'],
					        text:['高','低'],           // 文本，默认为数值文本
					        calculable : true,
					        textStyle: {
		                        color: 'black'
		                    }
					    },
					    series : [
					        {
					            name: '会员数量（人）',
					            type: 'map',
					            mapType: 'china',
					            selectedMode : 'single',
					            itemStyle:{
					                normal:{label:{show:true}},
					                emphasis:{label:{show:true}}
					            },
					            data:staticChart.datas
					        }
					    ]
					};
				}
			); 
        } 
    },
   
    RenderMap: function (option) { }
	
};
  