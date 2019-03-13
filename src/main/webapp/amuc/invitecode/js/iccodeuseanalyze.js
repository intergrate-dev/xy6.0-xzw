var iccodeuse = {
	region:['广州','中山','珠海','东莞','深圳','惠州','河源','汕尾','汕头','梅州','潮州','揭阳','清远','韶关','肇庆','佛山','云浮','江门','阳江','茂名','湛江'],
	init:function(){
		iccodeuse.initEvent();
		iccodeuse.initTable();
		iccodeuse.getChartData();
	},
	initEvent:function(){
		//绑定按钮事件
		$("#btn").click(function() {  
		    var queryParam =  $("#query").val();
		    var reg=/^[A-Z]{2,2}([a-zA-Z0-9]{0,4})$/;  
	        if(!reg.test(queryParam)){    
	            alert("对不起，您输入的邀请码格式不正确!");    
	            return;
	        } 
			if(queryParam != null && queryParam != ''){
				iccodeuse.getTableData(queryParam);
			}else{
				alert("请输入邀请码");
				return ;
			}
		}); 
		//查询全部地区
		$("#btnAll").click(function() {  
			
		}); 
		
	},
	initTable:function(){//初始化列表表头
		$('#table').bootstrapTable({
	         cache: false,
	         height: 430,
	         striped: true,
	         pageSize: 10,
	         pageList: [50],
	         singleSelect : true,
	         clickToSelect: true,
	         pagination: true,
	         showHeader:true,
			 showFooter:false,
	         columns: [{
	             field: 'id',
	             title: '编号',
	             align: 'center',
	             valign: 'bottom',
	         }, {
	             field: 'form_code',
	             title: '邀请码',
	             align: 'center',
	             valign: 'middle',
	         }, {
	             field: 'form_region',
	             title: '地区',
	             align: 'center',
	             valign: 'middle',
	         }, {
	             field: 'form_num',
	             title: '使用次数',
	             align: 'center',
	             valign: 'middle',
	         	},
	         ],
	   }).on('check.bs.table', function (e, name, args) {
        console.log(name.form_code);
        iccodeuse.selected = name.form_code;
	   });
	},
	getChartData : function(){
		//获取趋势图数据
		$.ajax({
			  type: "POST",
			  url: '/amuc/amuc/invitecode/inviteCode.do?a=GetRegionIcCode',
			  dataType : "json",
			  async:false,
			  success: function(data){
				  
				  iccodeuse.assembleChartData(data);
			  }
		});
	},
	getTableData : function(queryParam){//查询列表数据
		
		//ajax调用
		$.ajax({
			  type: "POST",
			  url: '/amuc/amuc/invitecode/inviteCode.do?a=AnalysisUseIcCode&query='+queryParam,
			  dataType : "json",
			  async:false,
			  success: function(data){
				  
				  iccodeuse.assembleTableData(data);
			  }
		});
		
	},
	region_in_array : function(array , e)  {  
		for(i=0;i<array.length;i++)  
		{  
			if(array[i].unit == e)  
				return i;  
		}  
		return -1;  
	},
	assembleChartData:function(data){//组装图表数据
		
		var chart_data_cre = [];
		var chart_data_use = [];
		
		for(r in iccodeuse.region){
			var exist = iccodeuse.region_in_array(data, iccodeuse.region[r]);
			if(exist > -1 ){
				chart_data_cre.push(data[exist].cnum);
				chart_data_use.push(data[exist].unum);
			}else{
				chart_data_cre.push(0);
				chart_data_use.push(0);
			}
		}
		console.log(iccodeuse.region);
		console.log(chart_data_cre);
		console.log(chart_data_use);
		iccodeuse.LoadChart(chart_data_cre,chart_data_use);
	},
	assembleTableData:function(data){//组装列表数据
		
		var table_data = [];
		var createNum = 0;
		var useNum = 0;
		var index = 0;
		for(i in data){
			var temp = {};
			temp.id = (++index) ;
			temp.form_code = data[i].code;
			temp.form_num = data[i].num;
			temp.form_region = data[i].region;
			
			table_data.push(temp);
			useNum = useNum + data[i].num;
			createNum++;
		}
		//加载列表
		iccodeuse.LoadForm(table_data);
		 
		//更新关键指标
		iccodeuse.LoadKPI(createNum,useNum);
	},
	LoadKPI:function(createNum,useNum){
		//更新关键指标
		$('#createNum').html(createNum);
		$('#useNum').html(useNum);
	},
	LoadForm:function(table_data){
		//重新加载数据
		$("#table").bootstrapTable("load", table_data);
	},
	LoadChart:function(chart_data_cre,chart_data_use){
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
	                	        text: '广东省各地区邀请码趋势图',
	                	    },
	                	    tooltip : {
	                	        trigger: 'axis'
	                	    },
	                	    legend: {
	                	        data:['新增量','使用量']
	                	    },
	                	    toolbox: {
	                	        show : true,
	                	    },
	                	    calculable : true,
	                	    xAxis : [
	                	        {
	                	            type : 'category',
	                	            boundaryGap : true,
	                	            data : iccodeuse.region
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
	                	            name:'新增量',
	                	            type:'bar',
	                	            data:chart_data_cre
	                	        },{
	                	        	name:'使用量',
	                	            type:'bar',
	                	            data:chart_data_use
	                    	        }
	                	    ]
	                };
	                // 为echarts对象加载数据 
	                myChart.setOption(option); 
	            }
	        );
	}
}

$(function(){
	iccodeuse.init();
	$(".tip").mouseover(
				function (e) {  //鼠标移上事件
					
					$(".help_content").css("display","block"); 
					}
				).mouseout(
				function () {  //鼠标移出事件
					$(".help_content").css("display","none"); 
				});
	
});
