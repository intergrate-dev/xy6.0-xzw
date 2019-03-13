var articleBatman_stat = {
		
	init : function() {
		$('li.channelTab').click(articleBatman_stat.channelTab);
		// 日期控件
		articleBatman_stat.initDateTimePickerWeb();
		// 初始化日历
		articleBatman_stat.initCalender();
		
		if ($("#ul1 .channelTab").length <= 1) $("#ul1").parent().hide();
		$("#ul1 .channelTab").first().click();
		
		articleBatman_stat.search('thisMonth', '');
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
	
	// 切换web app
	channelTab: function(evt){
		
		var id = $(evt.target)[0].id;
		$('#' + id).addClass("select");
		if(id.indexOf('Web') > -1){
			$('#' + id.replace('Web', 'App')).removeClass("select");
			$('#detail_Web').css('display', 'inline');
			$('#detail_App').css('display', 'none');
		}else{
			$('#' + id.replace('App', 'Web')).removeClass("select");
			$('#detail_Web').css('display', 'none');
			$('#detail_App').css('display', 'inline');
		}
	},
	// 点击查看
	search: function(type, webApp){
		var url = "../../xy/stat/ArticleBatmanSearch.do";
		var params = {
				type : type,
				siteID : $("#siteID").val(),
				channel:$('li.select').attr('id')
		}
		if(type == 'searchBtn' + webApp){
			if($('#pubTime_from' + webApp).val() > $('#pubTime_to' + webApp).val()){
				alert("结束日期不能小于开始日期！");
				return;
			}
			params["pubTime_from"] = $('#pubTime_from' + webApp).val();
			params["pubTime_to"] = $('#pubTime_to' + webApp).val();}else{
			articleBatman_stat.setCalender(type, webApp);
		}
		articleBatman_stat.setMonthColor(type, webApp);
		
		$.ajax({
			url: url,
			type: "POST",
			dataType: "json",
			async: false,
			data: params,
			error: function() {//请求失败处理函数
				alert("error")
			},
			success: function (data) {
				articleBatman_stat.showResult(data, webApp);
			}
		});
	},
	showResult : function(data, webApp) {
		var statList = data.statList;
		var table = $('#loadId' + webApp);
		table.html('');
		
		var str = '<tr class="tdtr">'
				+ '<td class="title tdtr">发稿人</td>'
				+ '<td class="title tdtr">发稿量</td>'
				+ '<td class="title tdtr">发布量</td></tr>';
		table.append(str);
		
		$.each(statList, function(i, bean){
			str = '<tr class="tdtr">'
				+ '<td class="tdtr">' + (bean.name ? bean.name : "（空）") + '</td>'
				+ '<td class="tdtr">' + bean.count + '</td>'
				+ '<td class="tdtr">' + bean.countRelease + '</td>'
				+ '</tr>';
			table.append(str);
		});	
	},
	outputcsv: function(webApp){
		var td = $('#loadId' + webApp + ' td');
		var jsonParam = '';
		var datas = [];
		var length = td.length;
		for(var a = 0; a < length; a++){
			jsonParam = {
				"1" : $(td[a++]).text(), // 发稿人
				"2" : $(td[a++]).text(), // 发稿量
				"3" : $(td[a]).text()// 发布量
			};
			datas.push(jsonParam);
		}
		$('#jsonData').val(JSON.stringify(datas));
		$('#csvName').val('发稿量统计.csv');
		$("#form").attr("action", "../../xy/stat/outputcsv.do");
		$("#form").submit();
	},
	// 点击上月本月要同时改变日历的值
	setCalender : function(type, webApp) {
		var now = new Date();
		if(type == 'thisMonth'){
			var ym = now.getFullYear() + "-" + articleBatman_stat.add_zero(now.getMonth() + 1) + "-";
			$('#pubTime_from' + webApp).val(ym + "01");
			$('#pubTime_to' + webApp).val(ym + articleBatman_stat.add_zero(now.getDate()));
		}else if(type == 'lastMonth'){
			var year = now.getFullYear();
		    var month = now.getMonth();
		    if(month == 0){
		    	year -= 1;
		    	month = 12;
		    }
			var ym = year + "-" + articleBatman_stat.add_zero(month) + "-";
			$('#pubTime_from' + webApp).val(ym + "01");
		    now.setDate(1);
		    now.setMonth(now.getMonth());
			var cdt = new Date(now.getTime() - 1000 * 60 * 60 * 24);
			$('#pubTime_to' + webApp).val(ym + cdt.getDate());
		}else if(type == 'thisYear'){
			var ym = now.getFullYear() + "-" + articleBatman_stat.add_zero(now.getMonth() + 1) + "-";
			$('#pubTime_from' + webApp).val(now.getFullYear() + "-01-01");
			$('#pubTime_to' + webApp).val(ym + articleBatman_stat.add_zero(now.getDate()));
		}else if(type == 'thisWeek'){
			
			var Nowdate=new Date();
			var WeekFirstDay=new Date(Nowdate-(Nowdate.getDay()-1)*86400000);
			var ym = now.getFullYear() + "-" + articleBatman_stat.add_zero(now.getMonth() + 1) + "-";
			$('#pubTime_from' + webApp).val(WeekFirstDay.getFullYear()+"-"+articleBatman_stat.add_zero(WeekFirstDay.getMonth() + 1)+"-"+WeekFirstDay.getDate());
			$('#pubTime_to' + webApp).val(ym + articleBatman_stat.add_zero(now.getDate()));
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
		var ym = year + "-" + articleBatman_stat.add_zero(month) + "-";
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
		$(".time").css({"color":"","background":""});
		if(type != 'searchBtn' + webApp){
			$("#"+type).css({"color":"#fff","background":"#e4a744"});
		}
	}
}

$(function(){
	articleBatman_stat.init();
});
