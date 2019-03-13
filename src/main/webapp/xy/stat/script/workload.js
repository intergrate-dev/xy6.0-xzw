var workLoad_stat = {
		
	init : function() {
		$('li.channelTab').click(workLoad_stat.channelTab);
		$('#usrSelect').change(function(){workLoad_stat.search('usrSelect', '')});
		$('#usrSelect_App').change(function(){workLoad_stat.search('usrSelect_App', '_App')});
		// 日期控件
		workLoad_stat.initDateTimePickerWeb();
		// 初始化日历
		workLoad_stat.initCalender();
		
		if ($("#ul1 .channelTab").length <= 1) $("#ul1").parent().hide();
		$("#ul1 .channelTab").first().click();
	},
	initDateTimePickerWeb : function(){
		$('#pubTime_from,#pubTime_from_App').datetimepicker({
			language : 'zh-CN',
			weekStart : 0,
			todayBtn : 1,
			autoclose : 1,
			todayHighlight : true,
			startView : 2,
			minView : 2,
			disabledDaysOfCurrentMonth : 0,
			forceParse : 0,
			pickerPosition: "bottom-left",
			format : 'yyyy-mm-dd'
		});
		
		$('#pubTime_from,#pubTime_from_App').datetimepicker().on('changeDate', function(ev) {});
		
		$('#pubTime_to,#pubTime_to_App').datetimepicker({
			language : 'zh-CN',
			weekStart : 0,
			todayBtn : 1,
			autoclose : 1,
			todayHighlight : true,
			startView : 2,
			minView : 2,
			disabledDaysOfCurrentMonth : 0,
			forceParse : 0,
			pickerPosition: "bottom-left",
			format : 'yyyy-mm-dd'
		});
		
		$('#pubTime_to,#pubTime_to_App').datetimepicker().on('changeDate', function(ev) {});
	},
	
	// 切换web app
	channelTab: function(evt){
		
		var id = $(evt.target)[0].id;
		$('#' + id).addClass("select");
		if(id.indexOf('Web') > -1){
			$('#' + id.replace('Web', 'App')).removeClass("select");
			$('#workload_Web').css('display', 'inline');
			$('#workload_App').css('display', 'none');
		}else{
			$('#' + id.replace('App', 'Web')).removeClass("select");
			$('#workload_Web').css('display', 'none');
			$('#workload_App').css('display', 'inline');
		}
	},
	// 点击查看
	search: function(type, webApp){
		var url = "../../xy/stat/WorkloadSearch.do?channel=" + $('li.select').attr('id')
				+ "&usrSelect=" + $('#usrSelect' + webApp).val()
				+ "&type=" + type + "&siteID=" + $('#siteID').val();

		if(type == 'searchBtn' + webApp || type == 'usrSelect' + webApp){
			if($('#pubTime_from' + webApp).val() > $('#pubTime_to' + webApp).val()){
				alert("结束日期不能小于开始日期！");
				return;
			}
			url += "&pubTime_from=" + $('#pubTime_from' + webApp).val() + "&pubTime_to=" + $('#pubTime_to' + webApp).val();
		}else{
			workLoad_stat.setCalender(type, webApp);
		}
		workLoad_stat.setMonthColor(type, webApp);
		
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
				workLoad_stat.showResult(data, webApp);
			}
		});
	},
	showResult : function(data, webApp) {
		var statList = data.detailMap;
		var totalCount=totalCount1=totalCount2 = 0;
		var table = $('#loadId' + webApp);
		table.html('');
		
		var str = '<tr class="tdtr">'
				+ '<td class="title tdtr">用户名</td>'
				+ '<td class="title tdtr">稿件量</td>'
				+ '<td class="title tdtr">点击量</td>'
				+ '<td class="title tdtr">评论量</td></tr>';
		table.append(str);
		
		$.each(statList, function(i, bean){
			str = '<tr class="tdtr">'
				+ '<td class="tdtr">' + (bean.name ? bean.name : "（空）") + '</td>'
				+ '<td class="tdtr">' + bean.count + '</td>'
				+ '<td class="tdtr">' + bean.countClick + '</td>'
				+ '<td class="tdtr">' + bean.countDiscuss + '</td>'
				+ '</tr>';
			table.append(str);
			totalCount += parseInt(bean.count);
			totalCount1 += parseInt(bean.countClick);
			totalCount2 += parseInt(bean.countDiscuss);
		});
		str = '<tr class="tdtr">'
			+ '<td class="total tdtr">总计（' + statList.length + '人）</td>'
			+ '<td class="total tdtr">' + totalCount + '</td>'
			+ '<td class="total tdtr">' + totalCount1 + '</td>'
			+ '<td class="total tdtr">' + totalCount2 + '</td>'
			+ '</tr>';
		table.append(str);		
	},
	outputcsv: function(webApp){
		var td = $('#loadId' + webApp + ' td');
		var jsonParam = '';
		var datas = [];
		var length = td.length;
		for(var a = 0; a < length; a++){
			jsonParam = {
				"1" : $(td[a++]).text(), // 用户名
				"2" : $(td[a++]).text(), // 稿件量
				"3" : $(td[a++]).text(), // 点击量
				"4" : $(td[a]).text() // 评论量
			};
			datas.push(jsonParam);
		}
		$('#jsonData').val(JSON.stringify(datas));
		$('#csvName').val('工作量统计.csv');
		$("#form").attr("action", "../../xy/stat/outputcsv.do");
		$("#form").submit();
	},
	// 点击上月本月要同时改变日历的值
	setCalender : function(type, webApp) {
		var now = new Date();
		if(type == 'thisMonth'){
			var ym = now.getFullYear() + "-" + workLoad_stat.add_zero(now.getMonth() + 1) + "-";
			$('#pubTime_from' + webApp).val(ym + "01");
			$('#pubTime_to' + webApp).val(ym + workLoad_stat.add_zero(now.getDate()));
		}else{
			var year = now.getFullYear();
		    var month = now.getMonth();
		    if(month == 0){
		    	year -= 1;
		    	month = 12;
		    }
			var ym = year + "-" + workLoad_stat.add_zero(month) + "-";
			$('#pubTime_from' + webApp).val(ym + "01");
		    now.setDate(1);
		    now.setMonth(now.getMonth());
			var cdt = new Date(now.getTime() - 1000 * 60 * 60 * 24);
			$('#pubTime_to' + webApp).val(ym + cdt.getDate());
		}
	},
	// 获取上个月一号到最后一号
	initCalender : function() {
	    var now = new Date();
	    var year = now.getFullYear();
	    var month = now.getMonth();
	    if(month == 0){
	    	year -= 1;
	    	month = 12;
	    }
		var ym = year + "-" + workLoad_stat.add_zero(month) + "-";
		$('#pubTime_from,#pubTime_from_App').val(ym + "01");
		
	    now.setDate(1);
	    now.setMonth(now.getMonth());
	    var cdt = new Date(now.getTime()-1000*60*60*24);
		$('#pubTime_to,#pubTime_to_App').val(ym + cdt.getDate());
	},
	add_zero:function(param){
		if(param < 10) return "0" + param;
		return "" + param;
	},
	setMonthColor : function(type, webApp){
		if(type == 'thisMonth'){
			$('#thisMonth' + webApp).css('color', '#fff');
			$('#thisMonth' + webApp).css('background', '#e4a744');
			$('#lastMonth' + webApp).css('color', '');
			$('#lastMonth' + webApp).css('background', '');
		}else if(type == 'lastMonth'){
			$('#lastMonth' + webApp).css('color', '#fff');
			$('#lastMonth' + webApp).css('background', '#e4a744');
			$('#thisMonth' + webApp).css('color', '');
			$('#thisMonth' + webApp).css('background', '');
		}else if(type == 'searchBtn' + webApp){
			$('#thisMonth' + webApp + ',#lastMonth' + webApp).css('color', '');
			$('#thisMonth' + webApp + ',#lastMonth' + webApp).css('background', '');
		}
	}
}

$(function(){
	workLoad_stat.init();
});
