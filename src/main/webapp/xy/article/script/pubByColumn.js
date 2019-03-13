var pub_column = {	
	init : function() {
		
		$('#doPub').click(pub_column.doPub);
		$('#doCancel').click(pub_column.doCancel);
		// 日期控件
		pub_column.initDateTimePickerWeb();
		// 初始化日历
		pub_column.initCalender();
	},
	initDateTimePickerWeb : function(){
		$('#pubTime_from').datetimepicker({
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
		
		$('#pubTime_from').datetimepicker().on('changeDate', function(ev) {
//			priority_day = ev.date.formatToUTC("yyyy-MM-dd hh:mm:00");
//			$("#pubTime").val( priority_day );
		});
		
		$('#pubTime_to').datetimepicker({
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
		
		$('#pubTime_to').datetimepicker().on('changeDate', function(ev) {
//			priority_day = ev.date.formatToUTC("yyyy-MM-dd hh:mm:00");
//			$("#pubTime").val( priority_day );
		});
	},
	// 点击查看
	doPub: function(){
		var pubType = $('input[name="pubType"]:checked ').val();

		if(typeof (pubType) == "undefined"){
			alert("请选择发布范围！");
			return;
		}
		var pubContent = $('input[name="pubContent"]:checked ').val();
		if($('#pubTime_from').val() > $('#pubTime_to').val() && pubContent == 0){
			alert("结束日期不能小于开始日期！");
			return;
		}

		if(typeof (pubContent) === "undefined"){
			alert("请选择发布内容！");
			return;
		}

		if($('#pubCount').val()<1 && pubContent == 1){
			alert("发布数量不能小于1！");
			return;
		}
		if(pubContent == 0){//按时间发布
			var url = "../../xy/article/pubColOperation.do?ch=" + ch
				+ "&colID=" + colID+ "&type=" + pubType + "&pubTime_from=" + $('#pubTime_from').val() + "&pubTime_to=" + $('#pubTime_to').val()+"&pubContent="+pubContent;
		}
		else if(pubContent == 1){//按数量发布
			var url = "../../xy/article/pubColOperation.do?ch=" + ch
				+ "&colID=" + colID+ "&type=" + pubType + "&pubCount=" + $('#pubCount').val() + "&pubContent="+pubContent;
		}
		else if(pubContent == 2){//只发布栏目页 = 发0篇稿件
            var url = "../../xy/article/pubColOperation.do?ch=" + ch
                + "&colID=" + colID+ "&type=" + pubType + "&pubCount=" + 0+ "&pubContent="+1;
        }
		$.ajax({
			url: url,
			dataType: "text",
			async: true,
			timeout:3000,
			error: function(xhr,textStatus)
			{
                if(textStatus=='timeout'){
                    //发布子孙栏目过多，会造成卡死，设置延迟3秒后关闭窗口
					alert("子孙栏目过多，后台正在处理。。。");
                    parent.col_menu.close();
                }else {
                    //请求失败处理函数
                    alert("error");
                }
			},
			success: function (data) {
				if (data.substr(0, 2) == "ok") {//发布成功
					parent.col_menu.close();
				}else{
					alert("error");
				}
			}
		});
	},
	//默认获取前一天为开始时间，
	initCalender : function() {	
	    $('#pubTime_from').val(pub_column.GetDateStr(0));
		$('#pubTime_to').val(pub_column.GetDateStr(+1));
	},
	GetDateStr : function(AddDayCount) {
	    var dd = new Date();
	    dd.setDate(dd.getDate()+AddDayCount);//获取AddDayCount天后的日期
	    var y = dd.getFullYear();
	    var m = dd.getMonth()+1;//获取当前月份的日期
	    var d = dd.getDate();
	    return y+"-"+pub_column.add_zero(m)+"-"+pub_column.add_zero(d);
	},
	add_zero:function(param){
		if(param < 10){
			return "0" + param;
		}
		return param;
	},
	doCancel : function() {
		parent.col_menu.close();
	}
}

$(function(){
	pub_column.init();
});
