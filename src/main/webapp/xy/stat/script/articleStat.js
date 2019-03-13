var article_stat = {
	colDialog : null,
	colDialogWebApp : null,
	init : function() {
		
		$('#colSelect,#colSelect_App').click(article_stat.colSelect);
		$('li.channelTab').click(article_stat.channelTab);
		// 日期控件
		article_stat.initDateTimePickerWeb();
		// 初始化日历
		article_stat.initCalender();
		// 自动提示控件
		article_stat.autoCompleter.init();
		article_stat.autoCompleterApp.init();
		
		if ($("#ul1 .channelTab").length <= 1) $("#ul1").hide();
		if ($("#ul2 .channelTab").length <= 1) $("#ul2").hide();
		if ($("#ul3 .channelTab").length <= 1) $("#ul3").hide();
		
		$("#ul1 .channelTab").first().click();
		$("#ul2 .channelTab").first().click();
		$("#ul3 .channelTab").first().click();
	},
	initDateTimePickerWeb : function(){
		$('[id^="pubTime_from"]').datetimepicker({
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
		
		$('[id^="pubTime_from"]').datetimepicker().on('changeDate', function(ev) {});
		
		$('[id^="pubTime_to"]').datetimepicker({
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
		
		$('[id^="pubTime_to"]').datetimepicker().on('changeDate', function(ev) {});
	},
	
	// 切换tab
	channelTab: function(evt){
		var id = $(evt.target)[0].id;
		if(id.indexOf('Web') > -1){
			$('#' + id).addClass("select1" + id.substr(10));
			$('#' + id.replace('channelWeb', 'detail')).css('display', 'inline');
			$('#' + id.replace('Web', 'App')).removeClass("select1" + id.substr(10));
			$('#' + id.replace('channelWeb', 'detail_App')).css('display', 'none');
			
		}else if(id.indexOf('App') > -1){
			$('#' + id).addClass("select1" + id.substr(10));
			$('#' + id.replace('App', 'Web')).removeClass("select1" + id.substr(10));
			$('#' + id.replace('channelApp', 'detail_App')).css('display', 'inline');
			$('#' + id.replace('channelApp', 'detail')).css('display', 'none');
			
		}else if(id.indexOf('deptStat') > -1){
			$('#' + id).addClass("select");
			$('#' + id.replace('dept', 'src')).removeClass("select");
			$('#' + id.replace('dept', 'col')).removeClass("select");
			$('#dept').css("display", "inline");
			$('#src').css("display", "none");
			$('#column').css("display", "none");
			
		}else if(id.indexOf('srcStat') > -1){
			$('#' + id).addClass("select");
			$('#' + id.replace('src', 'dept')).removeClass("select");
			$('#' + id.replace('src', 'col')).removeClass("select");
			$('#src').css("display", "inline");
			$('#dept').css("display", "none");
			$('#column').css("display", "none");
		}else{
			$('#' + id).addClass("select");
			$('#' + id.replace('col', 'src')).removeClass("select");
			$('#' + id.replace('col', 'dept')).removeClass("select");
			$('#column').css("display", "inline");
			$('#dept').css("display", "none");
			$('#src').css("display", "none");
		}
	},
	// 点击查看 eg thisMonth, _col, _App
	search: function(type, tab, webApp){
		if(tab == '_col' && $("#colID" + webApp).val() == ''){
			alert("请先选择栏目再进行查询！");
			return;
		}
		var url = "../../xy/stat/ArticleSearch.do";
		var params = {
			channel : $('li.select1' + tab).attr('id'),
			type : type,
			tab : tab,
			colID : $("#colID" + webApp).val(),
			srcName : $("#srcName" + webApp).val(),
			srcID : $("#srcID" + webApp).val(),
			siteID : $("#siteID").val()
		}
		if (type.indexOf('searchBtn') > -1 || type.indexOf('colSelect') > -1){
			if($('#pubTime_from' + tab + webApp).val() > $('#pubTime_to' + tab + webApp).val()){
				alert("结束日期不能小于开始日期！");
				return;
			}
			params["pubTime_from"] = $('#pubTime_from' + tab + webApp).val();
			params["pubTime_to"] = $('#pubTime_to' + tab + webApp).val();
		} else {
			article_stat.setCalender(type, tab + webApp);
		}
		article_stat.setMonthColor(type, tab + webApp);
		
		$.ajax({
			url: url,
			data : params,
			type: "POST",
			dataType: "json",
			async: false,
			//data:{"data": JSON.stringify(datas)},
			error: function (XMLHttpRequest, textStatus, errorThrown) {
				alert(errorThrown + ':' + textStatus);  // 错误处理
			},
			success: function (data) {
				article_stat.showResult(data, tab, webApp);
			}
		});
	},
	showResult : function(data, tab, webApp) {
		var statList = data.statList;
		var totalCount=totalCount1=totalCount2 = 0;
		var table = $('#loadId' + tab + webApp);
		table.html('');
		
		var kind = (tab == '_src') ? '来源' : (tab == '_col' ? '栏目' : '部门');
		var str = '<tr class="tdtr">'
				+ '<td class="title tdtr">' + kind + '</td>'
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
			+ '<td class="total tdtr">总计（' + statList.length + '个' + kind + '）</td>'
			+ '<td class="total tdtr">' + totalCount + '</td>'
			+ '<td class="total tdtr">' + totalCount1 + '</td>'
			+ '<td class="total tdtr">' + totalCount2 + '</td>'
			+ '</tr>';
		table.append(str);		
	},
	outputcsv: function(tab, webApp){
		if(tab == '_col' && $("#colID" + webApp).val() == ''){
			alert("请先选择栏目再导出数据！");
			return;
		}
		var td = $('#loadId' + tab + webApp + ' td');
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
		$('#csvName').val('稿件统计.csv');
		$("#form").attr("action", "../../xy/stat/outputcsv.do");
		$("#form").submit();
	},
	// 点击上月本月要同时改变日历的值
	setCalender : function(type, tab) {
		var now = new Date();
		if(type == 'thisMonth'){
			var ym = now.getFullYear() + "-" + article_stat.add_zero(now.getMonth() + 1) + "-";
			$('#pubTime_from' + tab).val(ym + "01");
			$('#pubTime_to' + tab).val(ym + article_stat.add_zero(now.getDate()));
		}else{
		    var now = new Date();
		    var year = now.getFullYear();
		    var month = now.getMonth();
		    if(month == 0){
		    	year -= 1;
		    	month = 12;
		    }
			var ym = year + "-" + article_stat.add_zero(month) + "-";
			$('#pubTime_from' + tab).val(ym + "01");
		    now.setDate(1);
		    now.setMonth(now.getMonth());
			var cdt = new Date(now.getTime() - 1000 * 60 * 60 * 24);
			$('#pubTime_to' + tab).val(ym + cdt.getDate());
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
		var ym = year + "-" + article_stat.add_zero(month) + "-";
		$('[id^="pubTime_from"]').val(ym + "01");
		
	    now.setDate(1);
	    now.setMonth(now.getMonth());
		var cdt = new Date(now.getTime() - 1000 * 60 * 60 * 24);
		$('[id^="pubTime_to"]').val(ym + cdt.getDate());
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
		}else if(type.indexOf('searchBtn') > -1){
			$('#thisMonth' + webApp + ',#lastMonth' + webApp).css('color', '');
			$('#thisMonth' + webApp + ',#lastMonth' + webApp).css('background', '');
		}
	},
	//打开主栏目选择对话框
	colSelect: function(evt){
		var ch = 1;
		article_stat.colDialogWebApp = '_App';
		var id = $('li.select1_col').attr('id');
		if(id && id.indexOf('Web') > -1){
			ch = 0;
			article_stat.colDialogWebApp = '';
		}
		var dataUrl = "../../xy/column/ColumnCheck.jsp?type=op&siteID=" + $('#siteID').val()
				+ "&ids=" + $("#colID" + article_stat.colDialogWebApp).val() + "&ch=" + ch;
		var pos = article_stat._getDialogPos(document.getElementById("colName" + article_stat.colDialogWebApp));

		article_stat.colDialog = e5.dialog({
			type : "iframe",
			value : dataUrl
		}, {
			showTitle : false,
			width : "450px",
			height : "430px",
			pos : pos,
			resizable : false
		});
		article_stat.colDialog.show();
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
	}
};
//---------来源名称查找框-------------
article_stat.autoCompleter = {
	url : null,
	init : function(evt) {
		article_stat.autoCompleter.url = "../../xy/stat/Find.do?siteID=" + $('#siteID').val();
		
		var s = $("#srcName");
		s.autocomplete(article_stat.autoCompleter.url, article_stat.autoCompleter.options).result(article_stat.autoCompleter.getSelectedID);
		s.focus();
	},
	options : {
		minChars : 1,
		delay : 400,
		autoFill : true,
		selectFirst : true,
		matchContains: true,
		cacheLength : 1,
		dataType:'json',
		//把data转换成json数据格式
		parse: function(data) {
			if (!data)
				return [];
			
			return $.map(eval(data), function(row) {
				return {
					data: row,
					value: row.value,
					result: row.value
				}
			});
		},
		//显示在下拉框中的值
		formatItem: function(row, i,max) { return row.value; },
		formatMatch: function(row, i,max) { return row.value; },
		formatResult: function(row, i,max) { return row.value; }
	},
	getSelectedID : function(event, data, formatted){
		$("#srcID").val(data.key);
	}
};
article_stat.autoCompleterApp = {
	url : null,
	init : function(evt) {
		article_stat.autoCompleterApp.url = "../../xy/stat/Find.do?siteID=" + $('#siteID').val();
		
		var s = $("#srcName_App");
		s.autocomplete(article_stat.autoCompleterApp.url, article_stat.autoCompleterApp.options).result(article_stat.autoCompleterApp.getSelectedID);
		s.focus();
	},
	options : {
		minChars : 1,
		delay : 400,
		autoFill : true,
		selectFirst : true,
		matchContains: true,
		cacheLength : 1,
		dataType:'json',
		//把data转换成json数据格式
		parse: function(data) {
			if (!data)
				return [];
			
			return $.map(eval(data), function(row) {
				return {
					data: row,
					value: row.value,
					result: row.value
				}
			});
		},
		//显示在下拉框中的值
		formatItem: function(row, i,max) { return row.value; },
		formatMatch: function(row, i,max) { return row.value; },
		formatResult: function(row, i,max) { return row.value; }
	},
	getSelectedID : function(event, data, formatted){
		$("#srcID_App").val(data.key);
	}
}
$(function(){
	article_stat.init();
});

//当用户提交选择的栏目,实现栏目选择树的接口
function columnClose(filterChecked, allFilterChecked) {
	$("#colName" + article_stat.colDialogWebApp).val(allFilterChecked[1]);
	$("#colID" + article_stat.colDialogWebApp).val(allFilterChecked[0]);
	columnCancel();
	article_stat.search('colSelect', '_col', article_stat.colDialogWebApp)
}
//点取消
function columnCancel() {
	article_stat.colDialog.close();
}