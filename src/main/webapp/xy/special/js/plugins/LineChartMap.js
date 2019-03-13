/**
 * Created by Administrator on 2016/12/5.
 */
(function(window, $, LE){
    LE.options["LineChartMap"] = {
        selector: "#lineChartLiMap", //地图选择器
        tag: "plugin",
        viewHtml: [
            '<div class="le-lineChart plugin_entity plugin-hint" data-name="map" style="height: 420px" >',
            '</div>'
        ].join("")
    };

    LE.plugins["LineChartMap"] = function(){
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
                //拖拽之后生成默认地图
                var $t = $("#" + _id);
                var myChart = echarts.init(document.getElementById(_id));
                // 指定图表的配置项和数据
                var option = {
                 /*   title: {
                        text: 'iphone销量',
                        textStyle:{
                            fontSize:"14",
                            color:"#333333"
                        },
                        subtext: '虚构数据',
                        subtextStyle:{
                            fontSize:"12",
                            color:"#333333"
                        },
                        show:true,
                        left: 'center'
                    },   */
                    tooltip: {
                        trigger: 'item',
                        show: true,
                        backgroundColor:'rgba(50,50,50,0.7)',
                        textStyle:{
                            color: '#fff'
                        }
                    },
                    legend: {
                        orient: 'vertical',
                        left: 'left',
                        data:['iphone3'],
                        show: true,
                        textStyle:{
                            color:"#333333"
                        }
                    },
                    visualMap: {
                        min: 0,
                        max: 1000,
                        left: 'left',
                        top: 'bottom',
                        text: ['高','低'],           // 文本，默认为数值文本
                        calculable: true,
                        inRange:{
                            color: ['#c23531','#2f4554', '#61a0a8']
                        }
                    },
                    toolbox: {
                        show: true,
                        orient: 'vertical',
                        left: 'right',
                        top: 'top',
                        feature: {
                            saveAsImage: {}
                        }
                    },
                    series: [
                        {
                            name: 'iphone3',
                            type: 'map',
                            mapType: 'china',
                            roam: false,
                            label: {
                                normal: {
                                    show: true,
                                    textStyle:{
                                        color:"#91c7ae"
                                    }
                                },
                                emphasis: {
                                    show: true
                                }
                            },
                            itemStyle:{
                                emphasis:{
                                    areaColor:"#d48265"
                                }
                            },
                            data:[
                                {name: '北京',value: 845 },
                                {name: '天津',value: 230 },
                                {name: '上海',value: 758 },
                                {name: '重庆',value: 180 },
                                {name: '河北',value: 560 },
                                {name: '河南',value: 400 },
                                {name: '云南',value: 220 },
                                {name: '辽宁',value: 608 },
                                {name: '黑龙江',value: 370 },
                                {name: '湖南',value: 990 },
                                {name: '安徽',value: 350 },
                                {name: '山东',value: 810 },
                                {name: '新疆',value: 500 },
                                {name: '江苏',value: 300 },
                                {name: '浙江',value: 460 },
                                {name: '江西',value: 670 },
                                {name: '湖北',value: 570 },
                                {name: '广西',value: 900 },
                                {name: '甘肃',value: 150 },
                                {name: '山西',value: 540 },
                                {name: '内蒙古',value: 390 },
                                {name: '陕西',value: 620 },
                                {name: '吉林',value: 350 },
                                {name: '福建',value: 410 },
                                {name: '贵州',value: 110 },
                                {name: '广东',value: 950 },
                                {name: '青海',value: 390 },
                                {name: '西藏',value: 280 },
                                {name: '四川',value: 500 },
                                {name: '宁夏',value: 170 },
                                {name: '海南',value: 640 },
                                {name: '台湾',value: 408 },
                                {name: '香港',value: 675 },
                                {name: '澳门',value: 245 }
                            ]
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