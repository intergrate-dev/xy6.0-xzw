/**
 * Created by Administrator on 2016/11/28.
 */
(function(window, $, LE){
    LE.options["LineChartPie"] = {
        selector: "#lineChartLiPie", //饼图选择器
        tag: "plugin",
        viewHtml: [
            '<div class="le-lineChart plugin_entity plugin-hint" data-name="pie" style="height: 420px" >',
            '</div>'
        ].join("")
    };

    LE.plugins["LineChartPie"] = function(){
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
              //  initContainer();
            },
            afterDrag: function(_id){
                //拖拽之后生成默认饼图
                var $t = $("#" + _id);
                var myChart = echarts.init(document.getElementById(_id));
                // 指定图表的配置项和数据
                option = {
                 /*   title : {
                        text: '某站点用户访问来源',
                        textStyle:{
                            fontSize:"14",
                            color:"#333333"
                        },
                        show:true,
                        subtext: '虚构数据',
                        subtextStyle:{
                            fontSize:"12",
                            color:"#333333"
                        },
                        x:'center'
                    },   */
                    tooltip : {
                        show: true,
                        trigger: 'item',
                        backgroundColor:'rgba(50,50,50,0.7)',
                        textStyle:{
                            color: '#fff'
                        },
                        /*formatter: "{a} <br/>{b} : {c} ({d}%)"*/
                        formatter: "{b} : {c} ({d}%)"
                    },
                    legend: {
                        orient: 'vertical',
                        left: 'left',
                        data: ['直接访问','邮件营销','联盟广告','视频广告','搜索引擎'],
                        show: true,
                        textStyle:{
                            color:"#333333"
                        }
                    },
                    toolbox: {
                        feature: {
                            saveAsImage: {}
                        }
                    },
                    series : [
                        {
                            /*name: '访问来源',*/
                            type: 'pie',
                            radius : '55%',
                            center: ['50%', '60%'],
                            data:[
                                {value:335, name:'直接访问'},
                                {value:310, name:'邮件营销'},
                                {value:274, name:'联盟广告'},
                                {value:235, name:'视频广告'},
                                {value:600, name:'搜索引擎'}
                            ],
                            roseType:false,
                            itemStyle: {
                                emphasis: {
                                    shadowBlur: 10,
                                    shadowOffsetX: 0,
                                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                                }
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