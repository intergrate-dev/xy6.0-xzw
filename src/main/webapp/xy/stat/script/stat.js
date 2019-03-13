var load_stat = {
		
	colDialog : null,//主栏目对话框	
	colDialogWebApp : null,
	artType: stat_data.statTypes,
	serialMonth: [],
	init : function() {
		load_stat.serialMonth = stat_data.serialMonth.split(",");
		
		$('#colSelect,#colSelect_App').click(load_stat.colSelect);
		$('li.channelTab').click(load_stat.channelTab);
		$('#artSelect').change(function(){load_stat.search('artSelect', '')});
		$('#artSelect_App').change(function(){load_stat.search('artSelect_App', '_App')});
		// 本月工作量 饼状图
		load_stat.pieWeb();
		load_stat.pieApp();
		// 各月工作量 线状图
		load_stat.lineWeb();
		load_stat.lineApp();
		// 日期控件
		load_stat.initDateTimePickerWeb();
		load_stat.initCalender();
		
		if ($("#ul .channelTab").length <= 1) $("#ul").hide();
		if ($("#ul1 .channelTab").length <= 1) $("#ul1").hide();
		if ($("#ul2 .channelTab").length <= 1) $("#ul2").hide();
		
		$("#ul .channelTab").first().click();
		$("#ul1 .channelTab").first().click();
		$("#ul2 .channelTab").first().click();
	},
	initDateTimePickerWeb : function(){
		$('#pubTime_from,#pubTime_from_App').datetimepicker({
            language : 'zh-CN',
            weekStart : 0,
            todayBtn : 1,
            autoclose : true,
            todayHighlight : true,
            startView : 2,
            minView : 0,
            disabledDaysOfCurrentMonth : 0,
            forceParse : 0,
            pickerPosition: "bottom-left",
            format : 'yyyy-mm-dd hh:ii:00'
		});
		
		$('#pubTime_from,#pubTime_from_App').datetimepicker().on('changeDate', function(ev) {});
		
		$('#pubTime_to,#pubTime_to_App').datetimepicker({
            language : 'zh-CN',
            weekStart : 0,
            todayBtn : 1,
            autoclose : true,
            todayHighlight : true,
            startView : 2,
            minView : 0,
            disabledDaysOfCurrentMonth : 0,
            forceParse : 0,
            pickerPosition: "bottom-left",
            format : 'yyyy-mm-dd hh:ii:00'
		});
		
		$('#pubTime_to,#pubTime_to_App').datetimepicker().on('changeDate', function(ev) {});
	},
	
	pieWeb :function() {
		load_stat.pie("pieWeb_", "");
	},
	pieApp :function() {
		load_stat.pie("pieApp_", "_App");
	},
	pie : function(divID, monthID) {
		var div = document.getElementById(divID);
		if (!div) return;
		
		// 路径配置
        require.config({
            paths: {
                echarts: '../script/echarts'
            }
        });
        // 使用
        require(
            [
                'echarts',
                'echarts/chart/pie'
            ],
            function (ec) {
                // 基于准备好的dom，初始化echarts图表
                var myChart = ec.init(div); 
                var monthData = load_stat.getMonthData(monthID);
				option = {
				    title : {
				        text: '本月工作量',
				        //subtext: '纯属虚构',
				        x:'center'
				    },
				    tooltip : {
				        trigger: 'item',
				        formatter: "{a} <br/>{b} : {c} ({d}%)"
				    },
				    legend: {
				        orient : 'vertical',
				        x : 'right',
				        data:load_stat.artType
				    },
				    toolbox: {
				        show : false,
				        feature : {
				            mark : {show: true},
				            dataView : {show: true, readOnly: false},
				            magicType : {
				                show: true, 
				                type: ['pie', 'funnel'],
				                option: {
				                    funnel: {
				                        x: '25%',
				                        width: '50%',
				                        funnelAlign: 'left',
				                        max: 1548
				                    }
				                }
				            },
				            restore : {show: true},
				            saveAsImage : {show: true}
				        }
				    },
				    calculable : false,
				    series : [
				        {
				            name: '稿件类型',
				            type: 'pie',
				            radius: '55%',
				            center: ['50%', '60%'],
				            data: monthData
				        }
				    ]
				};
				if(monthData.length == 0){
					myChart.showLoading({
						text: '本月工作量\n暂无数据',
					    effect : 'bubble',
					    textStyle : {
					        fontSize : 25,
					        fontWeight: 'bold'
					    }
					});
				}else{
					// 为echarts对象加载数据 
					myChart.setOption(option); 
				}
            }
        );
	},
	
	lineWeb :function() {
		load_stat.line("lineWeb_", "");
	},
	lineApp :function() {
		load_stat.line("lineApp_", "_App");
	},
	line :function(divID, suffix) {
		var div = document.getElementById(divID);
		if (!div) return;
		
		// 路径配置
        require.config({
            paths: {
                echarts: '../script/echarts'
            }
        });
        // 使用
        require(
            [
                'echarts',
                'echarts/chart/line'
            ],
            function (ec) {
                var types = load_stat.artType;
				var _s = [];
				for (var i = 0; i < types.length; i++) {
					var one = {
						name:types[i],
						type:'line',
						//stack: '总量',
						data: stat_data["serialDatas" + suffix][i].split(",")
					};
					_s.push(one);
				}
                option = {
                		title : {
					        text: '各月工作量统计',
					        x:'center'
					    },
                	    tooltip : {trigger: 'axis'},
                	    legend: {
					        orient : 'vertical',
					        x : 'right',
					        data:load_stat.artType
					    },
                	    toolbox: {
                	        show : false,
                	        feature : {
                	            mark : {show: true},
                	            dataView : {show: true, readOnly: false},
                	            magicType : {show: true, type: ['line', 'bar', 'stack', 'tiled']},
                	            restore : {show: true},
                	            saveAsImage : {show: true}
                	        }
                	    },
                	    calculable : false,
                	    xAxis : [{
                	            type : 'category',
                	            boundaryGap : false,
                	            data : load_stat.serialMonth
                	    }],
                	    yAxis : [ {type : 'value'} ],
                	    series : _s
                	};
                // 基于准备好的dom，初始化echarts图表
                var myChart = ec.init(div); 
                // 为echarts对象加载数据 
                myChart.setOption(option); 
            }
        );	
	},
	getMonthData :function(suffix) {
		var datas = stat_data["monthDatas" + suffix];
		if (!datas) return;
		
		var result = [];
		for (var i in datas) {
			var name = stat_data.typeDatas[i];
			var data = {
				value : datas[i],
				name : name
			};
			result.push(data);
		}
		return result;
	},
	// 切换web app
	channelTab: function(evt){
		var id = $(evt.target)[0].id;
		$('#' + id).addClass("select");
		
		var oldID;
		if (id.indexOf('Web') > -1){
			oldID = id.replace('Web', 'App');
		} else {
			oldID = id.replace('App', 'Web');
		}
		$('#' + oldID).removeClass("select");
		$('#' + oldID + '_').css('display', 'none');
		
		$('#' + id + '_').css('display', 'block');
	},
	//打开主栏目选择对话框
	colSelect: function(evt){
		load_stat.colDialogWebApp = $(evt.target)[0].id.substr(9);
		var ch = 1;
		if(load_stat.colDialogWebApp == ''){
			ch = 0;
		}
		var dataUrl = "../../xy/column/ColumnCheck.jsp?type=radio&siteID=" + stat_data.siteID
				+ "&ids=" + $("#colID" + load_stat.colDialogWebApp).val() + "&ch=" + ch;
		var pos = load_stat._getDialogPos(document.getElementById("colName" + load_stat.colDialogWebApp));

		load_stat.colDialog = e5.dialog({
			type : "iframe",
			value : dataUrl
		}, {
			showTitle : false,
			width : "450px",
			height : "430px",
			pos : pos,
			resizable : false
		});
		load_stat.colDialog.show();
	},
	// 点击查看
	search: function(type, webApp){
		var url = "../../xy/stat/Search.do?type=" + type
				+ "&colID=" + $('#colID' + webApp).val()+ "&artSelect=" + $('#artSelect' + webApp).val()
				+ "&webApp=" + webApp + "&siteID=" + stat_data.siteID;
		if(type == 'searchBtn' + webApp || type == 'artSelect' + webApp || type == 'colSelect' + webApp){
			if($('#pubTime_from' + webApp).val() > $('#pubTime_to' + webApp).val()){
				alert("结束日期不能小于开始日期！");
				return;
			}
			url += "&pubTime_from=" + $('#pubTime_from' + webApp).val() + "&pubTime_to=" + $('#pubTime_to' + webApp).val();
		}else{
			load_stat.setCalender(type, webApp);
		}
		load_stat.setMonthColor(type, webApp);
		$.ajax({
			url: url,
			type: "POST",
			dataType: "json",
			async: false,
			//data:{"data": JSON.stringify(datas)},
			error: function() {//请求失败处理函数
				alert("error")
			},
			success: function (data) {
				var statInfo = data.detailMapList;
				var totalCount = 0;
				var day = statInfo.length;
				$('#loadId' + webApp).html('');
				
				var str = '<tr class="tdtr">'
						+ '<td class="title tdtr">时间</td>'
						+ '<td class="title tdtr">稿件量</td>'
						+ '<td class="title tdtr">点击量</td>'
						+ '<td class="title tdtr">评论量</td></tr>';
				$('#loadId' + webApp).append(str);
				
				if(day <= 31){
					$.each(statInfo, function(i, bean){   
						str = '<tr class="tdtr">'
							+ '<td class="tdtr">' + bean.date + '</td>'
							+ '<td class="tdtr">' + bean.count + '</td>'
							+ '<td class="tdtr">' + bean.click + '</td>'
							+ '<td class="tdtr">' + bean.discuss + '</td></tr>';
						$('#loadId' + webApp).append(str);
						totalCount += parseInt(bean.count);
					});
				}else{
					totalCount = data.count;
				}
				str = '<tr class="tdtr">'
					+ '<td class="total tdtr">总计（' + day + '天）</td>'
					+ '<td class="total tdtr">' + totalCount + '</td>'
					+ '<td class="total tdtr">' + totalClick + '</td>'
					+ '<td class="total tdtr">' + totalDiscuss + '</td></tr>';
				$('#loadId' + webApp).append(str);
			}
		});
	},
	outputcsv: function(webApp){
		var td = $('#loadId' + webApp + ' td');
		var jsonParam = '';
		var datas = [];
		var length = td.length;
		for(var a = 0; a < length; a++){
			jsonParam = {
				"1" : $(td[a++]).text(), // 时间
				"2" : $(td[a++]).text(), // 稿件量
				"3" : $(td[a++]).text(), // 点击量
				"4" : $(td[a]).text() // 评论量
			};
			datas.push(jsonParam);
		}
		
		$('#jsonData').val(JSON.stringify(datas));
		$('#csvName').val('我的工作量.csv');
		$("#form").attr("action", "../../xy/stat/outputcsv.do");
		$("#form").submit();
	},
	//获取对话框显示的位置
	_getDialogPos : function(el) {
		function Pos (x, y) {
			this.x = x;
			this.y = y;
		}
		function getPos(el) {
			var r = new Pos(el.offsetLeft, el.offsetTop);
			if (el.offsetParent) {
				var tmp = getPos(el.offsetParent);
				r.x += tmp.x;
				r.y += tmp.y;
			}
			return r;
		}
		var p = getPos(el);
		
		//决定弹出窗口的高度和宽度
		var dWidth = 400;
		var dHeight = 300;

		var sWidth = document.body.clientWidth; //窗口的宽和高
		var sHeight = document.body.clientHeight;
		
		if (dWidth + 10 > sWidth) dWidth = sWidth - 10;//用e5.dialog时会额外加宽和高
		if (dHeight + 30 > sHeight) dHeight = sHeight - 30;
		
		//顶点位置
		var pos = {left : p.x +"px", 
			top : (p.y + el.offsetHeight - 1)+"px",
			width : dWidth,
			height : dHeight
			};
		if (pos.left + dWidth > sWidth)
			pos.left = sWidth - dWidth;
		if (pos.top + dHeight > sHeight)
			pos.top = sHeight - dHeight;
		
		return pos;
	},
	// 点击今天昨天最近一周要同时改变日历的值
	setCalender : function(type, webApp) {
		var now = new Date();
		if(type == 'today'){
			var ym = now.getFullYear() + "-" + load_stat.add_zero(now.getMonth() + 1) + "-";
			$('#pubTime_from' + webApp).val(ym + load_stat.add_zero(now.getDate()));
			$('#pubTime_to' + webApp).val(ym + load_stat.add_zero(now.getDate()));
		}else if(type == 'yesterday'){
			now.setDate(now.getDate() - 1);
			var ym = now.getFullYear() + "-" + load_stat.add_zero(now.getMonth() + 1) + "-";
			$('#pubTime_from' + webApp).val(ym + load_stat.add_zero(now.getDate()));
			$('#pubTime_to' + webApp).val(ym + load_stat.add_zero(now.getDate()));
		}else{
			var ym = now.getFullYear() + "-" + load_stat.add_zero(now.getMonth() + 1) + "-";
			$('#pubTime_to' + webApp).val(ym + load_stat.add_zero(now.getDate()));
			now.setDate(now.getDate() - 6);
			ym = now.getFullYear() + "-" + load_stat.add_zero(now.getMonth() + 1) + "-";
			$('#pubTime_from' + webApp).val(ym + load_stat.add_zero(now.getDate()));
		}
	},
	initCalender : function() {
		var now = new Date();
		var ym = now.getFullYear() + "-" + load_stat.add_zero(now.getMonth() + 1) + "-";
		$('#pubTime_from,#pubTime_from_App').val(ym + "01");
		$('#pubTime_to,#pubTime_to_App').val(ym + load_stat.add_zero(now.getDate()));
	},
	add_zero:function(param){
		if(param < 10) return "0" + param;
		return "" + param;
	},
	setMonthColor : function(type, webApp){
		if(type == 'today'){
			$('#today' + webApp).css('color', '#fff');
			$('#today' + webApp).css('background', '#e4a744');
			$('#yesterday' + webApp + ',#thisWeek' + webApp).css('color', '');
			$('#yesterday' + webApp + ',#thisWeek' + webApp).css('background', '');
			
		}else if(type == 'yesterday'){
			$('#yesterday' + webApp).css('color', '#fff');
			$('#yesterday' + webApp).css('background', '#e4a744');
			$('#today' + webApp + ',#thisWeek' + webApp).css('color', '');
			$('#today' + webApp + ',#thisWeek' + webApp).css('background', '');
			
		}else if(type == 'thisWeek'){
			$('#thisWeek' + webApp).css('color', '#fff');
			$('#thisWeek' + webApp).css('background', '#e4a744');
			$('#today' + webApp + ',#yesterday' + webApp).css('color', '');
			$('#today' + webApp + ',#yesterday' + webApp).css('background', '');
			
		}else if(type.indexOf('searchBtn') > -1){
			$('#today' + webApp + ',#yesterday' + webApp + ',#thisWeek' + webApp).css('color', '');
			$('#today' + webApp + ',#yesterday' + webApp + ',#thisWeek' + webApp).css('background', '');
		}
	}
}

$(function(){
	load_stat.init();
});
// 当用户提交选择的栏目,实现栏目选择树的接口
function columnClose(filterChecked, allFilterChecked) {
	$("#colName" + load_stat.colDialogWebApp).val(allFilterChecked[1]);
	$("#colID" + load_stat.colDialogWebApp).val(allFilterChecked[0]);
	columnCancel();
	load_stat.search('colSelect' + load_stat.colDialogWebApp, load_stat.colDialogWebApp)
}
// 点取消
function columnCancel() {
	load_stat.colDialog.close();
}
