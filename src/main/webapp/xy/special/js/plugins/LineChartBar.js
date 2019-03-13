/**
 * Created by qianxuemin on 2016/11/22.
 */
(function(window, $, LE){
    LE.options["LineChartBar"] = {
        selector: "#lineChartLiBar", //柱形图选择器
        tag: "plugin",
        viewHtml: [
            '<div class="le-lineChart plugin_entity plugin-hint" data-name="bar" style="height: 420px" >',
            '</div>'
        ].join("")
    };

    LE.plugins["LineChartBar"] = function(){
        var initContainer = function(){
            $("#container").on({
                click: function(e){
                    e.preventDefault();
                    e.stopPropagation();
                    var options = {
                        object: $(this)
                    };
                    LEStyle.destroyAll($(this).attr("id"));
                    LEStyle.run("WholeSetting",options).run("LineChartSetting", options, true).run("LineChartSettingTwo", options, true);
                },
                mousedown: function(e){
                    e.preventDefault();
                    e.stopPropagation();
                    $(this).trigger("click");
                }
            }, ".le-lineChart");
        };
        return {
            init: function(){
                //initContainer();
            },
            afterDrag: function(_id){
                //拖拽之后生成默认柱形图
                var $t = $("#" + _id);
                var myChart = echarts.init(document.getElementById(_id));
                // 指定图表的配置项和数据
                var option = {
                /*    title: {
                        text: '柱形图',
                        textStyle:{
                            fontSize:"14",
                            color:"#333333"
                        },
                        show:true,
                        top:'auto',
                        subtext:'统计图',
                        subtextStyle:{
                            fontSize:"12",
                            color:"#333333"
                        }
                    },  */
                    tooltip : {
                        show: true,
                        trigger: 'axis',
                        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                            type : 'shadow',        // 默认为直线，可选为：'line' | 'shadow'
                            shadowStyle:{
                                color:"rgba(150,150,150,0.3)",
                                opacity:0.3
                            }
                        },
                        backgroundColor:'rgba(50,50,50,0.7)',
                        textStyle:{
                            color: '#fff'
                        }
                    },
                    legend: {
                        data:['直接访问','邮件营销','联盟广告','视频广告','搜索引擎','百度','谷歌','必应','其他'],
                        show: true,
                        textStyle:{
                            color:"#333333"
                        }
                    },
                    grid: {
                        show:false,
                        left: '3%',
                        right: '4%',
                        bottom: '3%',
                        containLabel: true,
                        backgroundColor:'transparent'
                    },
                    toolbox: {
                        feature: {
                            saveAsImage: {}
                        }
                    },
                    xAxis : [
                        {
                            type : 'category',
                            data : ['周一','周二','周三','周四','周五','周六','周日'],
                            name:'X轴',
                            nameLocation:'end',
                            axisLabel:{
                                textStyle:{
                                    fontStyle:'normal',
                                    color:'#333'
                                }
                            },
                            axisLine:{
                                lineStyle:{
                                    color:'#333',
                                    width:1
                                }
                            }
                        }
                    ],
                    yAxis : [
                        {
                            type : 'value',
                            name:'Y轴',
                            nameLocation:'end',
                            axisLabel:{
                                textStyle:{
                                    fontStyle:'normal',
                                    color:'#333'
                                }
                            },
                            axisLine:{
                                lineStyle:{
                                    color:'#333',
                                    width:1
                                }
                            }
                        }
                    ],
                    series : [
                        {
                            name:'直接访问',
                            type:'bar',
                            barWidth : "自适应",
                            stack:null,
                            data:[320, 332, 301, 334, 390, 330, 320],
                            label:{
                                normal:{ }
                            }
                        },
                        {
                            name:'邮件营销',
                            type:'bar',
                            barWidth : "自适应",
                            stack: '广告',
                            data:[120, 132, 101, 134, 90, 230, 210],
                            label:{
                                normal:{ }
                            }
                        },
                        {
                            name:'联盟广告',
                            type:'bar',
                            barWidth : "自适应",
                            stack: '广告',
                            data:[220, 182, 191, 234, 290, 330, 310],
                            label:{
                                normal:{ }
                            }
                        },
                        {
                            name:'视频广告',
                            type:'bar',
                            barWidth : "自适应",
                            stack: '广告',
                            data:[150, 232, 201, 154, 190, 330, 410],
                            label:{
                                normal:{ }
                            }
                        },
                        {
                            name:'搜索引擎',
                            type:'bar',
                            barWidth : "自适应",
                            stack:'搜索',
                            data:[862, 1018, 964, 1026, 1679, 1600, 1570],
                            markLine : {
                                lineStyle: {
                                    normal: {
                                        type: 'dashed'
                                    }
                                },
                                data : [
                                    [{type : 'min'}, {type : 'max'}]
                                ]
                            },
                            label:{
                                normal:{ }
                            }
                        },
                        {
                            name:'百度',
                            type:'bar',
                            barWidth : "自适应",
                            stack: '搜索引擎',
                            data:[620, 732, 701, 734, 1090, 1130, 1120],
                            label:{
                                normal:{ }
                            }
                        },
                        {
                            name:'谷歌',
                            type:'bar',
                            barWidth : "自适应",
                            stack: '搜索引擎',
                            data:[120, 132, 101, 134, 290, 230, 220],
                            label:{
                                normal:{ }
                            }
                        },
                        {
                            name:'必应',
                            type:'bar',
                            barWidth : "自适应",
                            stack: '搜索引擎',
                            data:[60, 72, 71, 74, 190, 130, 110],
                            label:{
                                normal:{ }
                            }
                        },
                        {
                            name:'其他',
                            type:'bar',
                            barWidth : "自适应",
                            stack: '搜索引擎',
                            data:[62, 82, 91, 84, 109, 110, 120],
                            label:{
                                normal:{ }
                            }
                        }
                    ],
                    color:['#c23531','#2f4554', '#61a0a8', '#d48265', '#91c7ae','#749f83',  '#ca8622', '#bda29a','#6e7074', '#546570', '#c4ccd3'],
                    backgroundColor:'transparent'
                };
                // 使用刚指定的配置项和数据显示图表。
                myChart.setOption(option);
                lineChart_Map.put(_id,option);
            },
            afterSort: function(){

            },
            afterDragClone: function(_id){
            var $t = $("#" + _id);
            if($t.attr("data-mapkey")){
                var mapkey=$t.attr("data-mapkey");
                var myChart = echarts.init(document.getElementById(_id));
//                var _lineOption = cloneLineChartOptionMap.get(mapkey);
                var _lineOption = jQuery.extend(true,{},cloneLineChartOptionMap.get(mapkey));
                myChart.setOption(_lineOption);
                lineChart_Map.put(_id,_lineOption);
                $t.attr("data-mapkey","");
            }
        }
        }
    };

})(window, jQuery, LE, undefined);