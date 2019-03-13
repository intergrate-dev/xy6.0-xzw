/**
 * Created by isaac_gu on 2016/1/6.
 */
(function(window, $, LE){
    LE.options["LineChart"] = {
        selector: "#lineChartLi", //折线图选择器
        tag: "plugin",
        viewHtml: [
            '<div class="le-lineChart plugin_entity plugin-hint" data-name="line" style="height: 420px" >',
            '</div>'
        ].join("")
    };

    LE.plugins["LineChart"] = function(){
        var initContainer = function(){
            $("#container").on({
                click: function(e){
                    e.preventDefault();
                    e.stopPropagation();
                    var options = {
                        object: $(this)
                    };
                    LEStyle.destroyAll($(this).attr("id"));
                   //LEStyle.run("Position", options, true).run("BolderSetting", options, true);
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
                initContainer();
            },
            afterDrag: function(_id){
                //拖拽之后生成默认折线图
                var $t = $("#" + _id);
                var myChart = echarts.init(document.getElementById(_id));
                // 指定图表的配置项和数据
                var option = {
                /*    title: {
                        text: '折线图堆叠',
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
                    },   */
                    tooltip: {
                        show:true,
                        trigger: 'axis',
                        axisPointer:{
                            lineStyle:{
                                color:"#555555"
                            }
                        },
                        borderWidth:0,
                        backgroundColor:'rgba(50,50,50,0.7)',
                        textStyle:{
                            color: '#fff'
                        }
                    },
                    legend: {
                        data:['邮件营销','联盟广告','视频广告','直接访问','搜索引擎'],
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
                    xAxis: {
                        type: 'category',
                        boundaryGap: false,
                        data: ['周一','周二','周三','周四','周五','周六','周日'],
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
                    },
                    yAxis: {
                        type: 'value',
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
                    },
                    series: [
                        {
                            name:'邮件营销',
                            areaStyle:{},
                            type:'line',
                            stack: '总量',
                            data:[120, 132, 101, 134, 90, 230, 210],
                            label:{
                                normal:{
                                    show:false
                                }
                            },
                            smooth:false,
                            lineStyle:{
                                normal:{
                                    width:2
                                }
                            },
                            symbol:'emptyCircle'

                        },
                        {
                            name:'联盟广告',
                            areaStyle:{},
                            type:'line',
                            stack: '总量',
                            data:[220, 182, 191, 234, 290, 330, 310],
                            label:{
                                normal:{
                                    show:false
                                }
                            },
                            smooth:false,
                            lineStyle:{
                                normal:{
                                    width:2
                                }
                            },
                            symbol:'emptyCircle'
                        },
                        {
                            name:'视频广告',
                            areaStyle:{},
                            type:'line',
                            stack: '总量',
                            data:[150, 232, 201, 154, 190, 330, 410],
                            label:{
                                normal:{
                                    show:false
                                }
                            },
                            smooth:false,
                            lineStyle:{
                                normal:{
                                    width:2
                                }
                            },
                            symbol:'emptyCircle'
                        },
                        {
                            name:'直接访问',
                            areaStyle:{},
                            type:'line',
                            stack: '总量',
                            data:[320, 332, 301, 334, 390, 330, 320],
                            label:{
                                normal:{
                                    show:false
                                }
                            },
                            smooth:false,
                            lineStyle:{
                                normal:{
                                    width:2
                                }
                            },
                            symbol:'emptyCircle'
                        },
                        {
                            name:'搜索引擎',
                            areaStyle:{},
                            type:'line',
                            stack: '总量',
                            data:[820, 932, 901, 934, 1290, 1330, 1320],
                            label:{
                                normal:{
                                    show:false
                                }
                            },
                            smooth:false,
                            lineStyle:{
                                normal:{
                                    width:2
                                }
                            },
                            symbol:'emptyCircle'
                        }
                    ],
                    color:['#c23531','#2f4554', '#61a0a8', '#d48265', '#91c7ae','#749f83',  '#ca8622', '#bda29a','#6e7074', '#546570', '#c4ccd3'],
                    backgroundColor:'transparent'
                };
                // 使用刚指定的配置项和数据显示图表。
                myChart.setOption(option);
                lineChart_Map.put(_id,option);

                /*$("#lineChart_script").attr("type","text/plain");
                lineChart_Map.each(function (key, value, index) {
                    value=JSON.stringify(value);
                    $("#lineChart_script").append("var myChart"+index+" = echarts.init(document.getElementById(*//**id**//*'"+key+"'*//**id**//*));");
                    $("#lineChart_script").append("\nvar option"+index+"=*//**option**//*"+value+"*//**option**//*;");
                    $("#lineChart_script").append("\nmyChart"+index+".setOption(option"+index+");"+"*//**nextJs**//*");
                });*/
            },
            afterSort: function(){

            },
            afterDragClone: function(_id){
                var $t = $("#" + _id);
                if($t.attr("data-mapkey")){
                    var mapkey=$t.attr("data-mapkey");
                    var myChart = echarts.init(document.getElementById(_id));
//                    var _lineOption = cloneLineChartOptionMap.get(mapkey);
                    var _lineOption = jQuery.extend(true,{},cloneLineChartOptionMap.get(mapkey));
                    myChart.setOption(_lineOption);
                    lineChart_Map.put(_id,_lineOption);
                    $t.attr("data-mapkey","");
                }
            }
        }
    };

})(window, jQuery, LE, undefined);