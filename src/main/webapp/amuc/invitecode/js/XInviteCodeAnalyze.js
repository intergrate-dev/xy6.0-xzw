var icode = {
	//city: ['南京','镇江','泰州','宿迁','无锡','徐州','常州','苏州','南通','连云港','淮安','盐城','扬州','省直','集团'],
	city: [],
	level1: '',
	level2: '',
	level3: '',
	level4: '',
	level: [],  //城市名
	icNum: [],  //完成数量
	icMetrics: [],  //指标数量
	//number: ['A','L','M','N','B','C','D','E','F','G','H','J','K','S','Z'],
	init: function(){
		icode.initCity();
		icode.initSelect();  // 初始化第一层级下拉框
		icode.setSelContent('','','','all','iscontent');  //初始化图表 --默认为全部省市
		icode.LoadChart(icode.level,icode.icNum,icode.icMetrics);  //获取图表数据
		//第一层级下拉框改变
		$("#level1").change(function(){
			$("#level2").children('option').remove();  //清空level2
			$("#level3").children('option').remove();  //清空level3
			$("#level4").children('option').remove();  //清空level4
			icode.level1 = $(this).val();
			if(icode.level1 != 'all'){
				icode.setSelContent(icode.level1,'','',2,'iscontent');  //给第二层级下拉框重新赋值
			}else{  //加载全部的
				icode.setSelContent('','','','all','iscontent');
			}
		});
		//第二层级下拉框改变
		$("#level2").change(function(){
			$("#level3").children('option').remove();  //清空level3
			$("#level4").children('option').remove();  //清空level4
			icode.level2 = $(this).val();
			if(icode.level2 == 'nocontent'){
				icode.setSelContent(icode.level1,'','',2,'isnocontent');  //给第三层级下拉框重新赋值
			}else{
				icode.setSelContent(icode.level1,icode.level2,'',3,'iscontent');  //给第三层级下拉框重新赋值
			}
		});
		//第三层级下拉框改变
		$("#level3").change(function(){
			$("#level4").children('option').remove();  //清空level4
			icode.level3 = $(this).val();
			if(icode.level3 == 'nocontent'){
				icode.setSelContent(icode.level1,icode.level2,'',3,'isnocontent');  //给第四层级下拉框重新赋值
			}else{
				icode.setSelContent(icode.level1,icode.level2,icode.level3,4,'iscontent');  //给第四层级下拉框重新赋值
			}
		});
		//第四层级下拉框改变
		$("#level4").change(function(){
			icode.level4 = $(this).val();
		});
		//查询
		$("#btn").click(function(){
			var mylevel1 = $("#level1").val();
			var mylevel2 = $("#level2").val();
			var mylevel3 = $("#level3").val();
			icode.LoadChart(icode.level,icode.icNum,icode.icMetrics);  //获取图表数据
		});
	},
	initSelect: function(){
		//初始化第一层级
		for(var i=0;i<icode.city.length;i++){
			$("#level1").append('<option value="'+icode.city[i]+'">'+icode.city[i]+'</option>');
		}
	},
	//给下拉框赋值  level1、level2、level3 分别表示第一、二、三层级的值,type代表给第几层级的下拉框赋值
	setSelContent: function(level1,level2,level3,type,iscontent){
		if(type == 'all'){  //取全部时，默认赋值0
			icode.level = icode.city;
			icode.icNum = [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0];
			icode.icMetrics = [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0];
		}else{
			icode.level = [];  //城市名
			icode.icNum = [];  //完成数量
			icode.icMetrics = [];  //指标数量
		}
		var siteID = getUrlVars("siteID");
		var url = '../invitecode/xAnalyze.do';
		$.ajax({
			url:url,
			type:'post',
			data:{"level1":level1,"level2":level2,"level3":level3,"type":type,"siteID":siteID},
			dataType:"json",
			cache:false,
			async:false,
			success:function(data){  
				//alert(JSON.stringify(data));
				if(iscontent == 'iscontent'){
					$("#level"+type).append('<option value="nocontent"></option>');
				}
				if(JSON.stringify(data) == '[]'){
					icode.level.push('');
					icode.icNum.push(0);
					icode.icMetrics.push(0);
				}else{
					for(var i=0;i<data.length;i++){
						if(type != 'all'){  //不是选择全部
							if(iscontent == 'iscontent'){
								$("#level"+type).append('<option value="'+data[i].level+'">'+data[i].level+'</option>');
							}
							icode.level.push(data[i].level);
							icode.icNum.push(data[i].icNum);
							icode.icMetrics.push(data[i].icMetrics);
						}else{  //选择全部
							for(var j=0;j<icode.level.length;j++){
								if(data[i].level == icode.level[j]){  //返回值与初始值相等，则赋值，否则为0
									icode.icNum[j] = data[i].icNum;
									icode.icMetrics[j] = data[i].icMetrics;
								}
							}
						}
					}
				}
				console.log(icode.level+'--'+icode.icNum+'--'+icode.icMetrics+'--'+JSON.stringify(data));
			},
		    error: function (XMLHttpRequest, textStatus, errorThrown) {
		        console.log("error status : " + XMLHttpRequest.status);
		        alert(XMLHttpRequest.status);
		    }
		});
	},
	LoadChart:function(level,icNum,icMetrics){
		 // 路径配置
	    require.config({
	        paths: {
	            echarts: '../script/ECharts/js/echarts',
	            'echarts/chart/bar': '../script/ECharts/js/echarts',
	            'echarts/chart/line': '../script/ECharts/js/echarts',
	        }
	    });
	    require(
	            [
	                'echarts',
	                'echarts/chart/bar' ,// 使用柱状图就加载bar模块，按需加载
	                'echarts/chart/line'
	            ],
	            function (ec) {
	                // 基于准备好的dom，初始化echarts图表
	                var myChart = ec.init(document.getElementById('chart')); 
	                var option = {
	                		title : {
	                	        text: '',
	                	    },
	                	    tooltip : {
	                	        trigger: 'axis'
	                	    },
	                	    legend: {
	                	        data:['指标量','使用量']
	                	    },
	                	    toolbox: {
	                	        show : true,
	                	    },
	                	    calculable : true,
	                	    xAxis : [
	                	        {
	                	            type : 'category',
	                	            boundaryGap : true,
	                	            data : level
	                	        }
	                	    ],
	                	    yAxis : [
	                	        {
	                	        	 type : 'value',
	                	             axisLabel : {
	                	                 formatter: '{value}'
	                	             }
	                	        }
	                	    ],
	                	    series : [
	                	        {
	                	            name:'指标量',
	                	            type:'bar',
	                	            data:icMetrics
	                	        },{
	                	        	name:'使用量',
	                	            type:'bar',
	                	            data:icNum
	                    	        }
	                	    ]
	                };
	                // 为echarts对象加载数据 
	                myChart.setOption(option); 
	            }
	        );
	},
	initCity: function(){
		var siteID = getUrlVars("siteID");
		var url = '../invitecode/getLevel1.do';
		var city1="";
		$.ajax({
			url:url,
			type:'post',
			data:{"siteID":siteID},
			dataType:"json",
			cache:false,
			async:false,
			success:function(data){
				icode.city = data;
			},
		    error: function (XMLHttpRequest, textStatus, errorThrown) {
		        console.log("error status : " + XMLHttpRequest.status);
		        alert(XMLHttpRequest.status);
		    }
		});
	}
}

function getUrlVars(name){
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
    var r = window.location.search.substr(1).match(reg);  //匹配目标参数
    if (r != null) return unescape(r[2]); return null; //返回参数值
}

$(function(){
	icode.init();
	$(".tip").mouseover(
		function (e) {  //鼠标移上事件
			$(".help_content").css("display","block"); 
		}
	).mouseout(
		function () {  //鼠标移出事件
			$(".help_content").css("display","none"); 
		}
	);
});