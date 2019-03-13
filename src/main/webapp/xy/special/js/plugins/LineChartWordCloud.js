/**
 * Created by qianxuemin on 2016/11/22.
 */
(function(window, $, LE){
    LE.options["LineChartWordCloud"] = {
        selector: "#lineChartLiWordCloud", //标签云选择器
        tag: "plugin",
        viewHtml: [
            '<div class="le-lineChart plugin_entity plugin-hint" data-name="wordCloud" style="height: 420px" >',
            '</div>'
        ].join("")
    };

    LE.plugins["LineChartWordCloud"] = function(){
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
                //拖拽之后生成默认折线图
                var $t = $("#" + _id);
                var myChart = echarts.init(document.getElementById(_id));
                var textStyleVal=function(){
                    return {
                        normal: {
                            color: LE.plugins["LineChartWordCloud"]().randomColor.colorStyle()
                        },
                        emphasis: {
                            shadowBlur: 10,
                            shadowColor: '#333'
                        }
                    }
                };
                var option = {
                  /*  title: {
                        text: '标签云',
                        textStyle:{
                            fontSize:"14",
                            color:"#333333"
                        },
                        show:true,
                        top:'auto',
                        subtext:'副标题',
                        subtextStyle:{
                            fontSize:"12",
                            color:"#333333"
                        }
                    },   */
                    tooltip: {
                        show: true,
                        backgroundColor:'rgba(50,50,50,0.7)',
                        textStyle:{
                            color: '#fff'
                        }
                    },
                    toolbox: {
                        feature: {
                            saveAsImage: {}
                        }
                    },
                    series: [ {
                        /*name: '标签云',*/
                        type: 'wordCloud',
                        gridSize:2,
                        center:['10%', '50%'],
                        size:['100%', '100%'],
                        autoSize:{
                            enable: true,
                            minSize: 12
                        },
                        sizeRange: [12, 30],
                        rotationRange: [-90,90],
                        rotationStep: 45,
                        shape: 'circle',
                        /*shape: 'cardioid',*/
                        /*shape: 'circle',
                         *
                         circle	圆形
                         cardioid	心形
                         star	星形
                         diamond	钻石形
                         triangle-forward	三角形
                         triangle	三角形
                         pentagon	五边形*/
                        /*width: '100%',
                         height: '100%',*/
                       /* textStyle: {
                            normal: {
                                color: LE.plugins["LineChartWordCloud"]().randomColor.colorStyle
                            },
                            emphasis: {
                                shadowBlur: 10,
                                shadowColor: '#333'
                            }
                        },*/
                        data: [
                            {
                                name: '保护环境  美化家园',
                                value: 20,
                                 textStyle: textStyleVal()
                            },
                            {
                                name: '转变工作作风  提高行政效能',
                                value: 19,
                                textStyle: textStyleVal()
                            },
                            {
                                name: 'Amy 清正廉洁  反腐倡廉',
                                value: 18,
                                textStyle: textStyleVal()
                            },
                            {
                                name: '遵守法规  绿色出行',
                                value: 17,
                                textStyle: textStyleVal()
                            },
                            {
                                name: '关注未成年人健康成长',
                                value: 16,
                                textStyle: textStyleVal()
                            },
                            {
                                name: '国家好 民族好 大家才会更好',
                                value: 15,
                                textStyle: textStyleVal()
                            },
                            {
                                name: '诚信 内心的力量',
                                value: 14,
                                textStyle: textStyleVal()
                            },
                            {
                                name: '中国好司机',
                                value: 13,
                                textStyle: textStyleVal()
                            },
                            {
                                name: '友善是中国的名片',
                                value: 12,
                                textStyle: textStyleVal()
                            },
                            {
                                name: '文明中国礼 仪容仪表要得体',
                                value: 11,
                                textStyle: textStyleVal()
                            },
                            {
                                name: '减少空气污染，保护多彩城市',
                                value: 10,
                                textStyle: textStyleVal()
                            },
                            {
                                name: '公益广告也是一盏灯',
                                value: 9,
                                textStyle: textStyleVal()
                            },
                            {
                                name: '争做中国好司机',
                                value: 8,
                                textStyle: textStyleVal()
                            },
                            {
                                name: '绿色公交 纯净呼吸',
                                value: 7,
                                textStyle: textStyleVal()
                            },
                            {
                                name: '中国梦 我的梦',
                                value: 6,
                                textStyle: textStyleVal()
                            },
                            {
                                name: '勤为本',
                                value: 5,
                                textStyle: textStyleVal()
                            },
                            {
                                name: '分享爱 温暖心',
                                value: 4,
                                textStyle: textStyleVal()
                            },
                            {
                                name: '善作魂',
                                value: 3,
                                textStyle: textStyleVal()
                            },
                            {
                                name: '青春守护 美丽中国',
                                value: 2,
                                textStyle: textStyleVal()
                            },
                            {
                                name: '书也是海洋',
                                value: 1,
                                textStyle: textStyleVal()
                            },
                            {
                                name: '命里有时终须有',
                                value: 10,
                                textStyle: textStyleVal()
                            },
                            {
                                name: '人生梦想',
                                value: 10,
                                textStyle: textStyleVal()
                            },{
                                name: '机器猫',
                                value: 10,
                                textStyle: textStyleVal()
                            },
                            {
                                name: '心灵鸡汤',
                                value: 12,
                                textStyle: textStyleVal()
                            },
                            {
                                name: '健康美食',
                                value: 12,
                                textStyle: textStyleVal()
                            },
                            {
                                name: '网红',
                                value: 12,
                                textStyle: textStyleVal()
                            },{
                                name: '时代新闻',
                                value: 12,
                                textStyle: textStyleVal()
                            },{
                                name: '用户体验',
                                value: 10,
                                textStyle: textStyleVal()
                            },
                            {
                                name: '中国百科',
                                value: 2,
                                textStyle: textStyleVal()
                            },
                            {
                                name: '再不疯狂我们就老了',
                                value: 5,
                                textStyle: textStyleVal()
                            },{
                                name: '小幸运',
                                value: 5,
                                textStyle: textStyleVal()
                            },{
                                name: '哈利波特',
                                value: 5,
                                textStyle: textStyleVal()
                            },
                            {
                                name: '好好学习 天天向上',
                                value: 8,
                                textStyle: textStyleVal()
                            },
                            {
                                name: '创业',
                                value: 8,
                                textStyle: textStyleVal()
                            },
                            {
                                name: '呼伦贝尔',
                                value: 7,
                                textStyle: textStyleVal()
                            },
                            {
                                name: '爱是心灵的呼唤',
                                value: 7,
                                textStyle: textStyleVal()
                            },
                            {
                                name: '关注留守儿童',
                                value: 7,
                                textStyle: textStyleVal()
                            },
                            {
                                name: '在路上',
                                value: 9,
                                textStyle: textStyleVal()
                            },
                            {
                                name: '花儿与少年',
                                value: 7,
                                textStyle: textStyleVal()
                            },
                            {
                                name: '中关村',
                                value: 9,
                                textStyle: textStyleVal()
                            },
                            {
                                name: '九零后',
                                value: 5,
                                textStyle: textStyleVal()
                            },
                            {
                                name: '漫漫人生路',
                                value: 3,
                                textStyle: textStyleVal()
                            },
                            {
                                name: '可怜天下父母心',
                                value: 6,
                                textStyle: textStyleVal()
                            },
                            {
                                name: '圣诞节',
                                value: 6,
                                textStyle: textStyleVal()
                            },
                            {
                                name: '老干部',
                                value: 8,
                                textStyle: textStyleVal()
                            }
                        ]
                    } ]
                };

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
            },
            randomColor:{
                redStyle:function () {
                    return 'rgb(' + [
                            Math.round(Math.random() * 255)+1,
                            Math.round(Math.random() * 0),
                            Math.round(Math.random() * 0)
                        ].join(',') + ')';
                },
                greenStyle:function () {
                    return 'rgb(' + [
                            Math.round(Math.random() * 0),
                            Math.round(Math.random() * 255)+1,
                            Math.round(Math.random() * 0)
                        ].join(',') + ')';
                },
                blueStyle:function () {
                    return 'rgb(' + [
                            Math.round(Math.random() * 0),
                            Math.round(Math.random() * 0),
                            Math.round(Math.random() * 255)+1
                        ].join(',') + ')';
                },
                colorStyle:function () {
                    return 'rgb(' + [
                            Math.round(Math.random() * 160)+1,
                            Math.round(Math.random() * 160)+1,
                            Math.round(Math.random() * 160)+1
                        ].join(',') + ')';
                }
            }
        }
    };

})(window, jQuery, LE, undefined);