var article_priority = {
		init : function(){
			article_priority.titileOperation() ;
			article_priority.initDateTimePickerWeb() ;
			$("#btnSave").click(function(){
	            $("#caForm").submit();
			}) ;
			$("#btnCancel").click(function(){
				article_priority.doCancel() ;
			}) ;
		},
		titileOperation:function(){
			var title = $("#linkTitle").text() ;
			$("#linkTitle").attr("title",title) ;
		},
		initDateTimePickerWeb : function(){
			var priority = $("#priority").val();
			var publishTime = $("#pubTimeLable").text()
	        var _initialDate = "";
	        if(priority && priority != "" && priority != "null"){
	            var _day = priority.substring(0, priority.length - 1);
	            var _e = publishTime.split("-");
	            _initialDate = _e[0] + "-" + _e[1] + "-" + ( parseInt(_e[2]) + parseInt(_day) );
	            var nd = article_priority.getDateStr(1);
	            if(_initialDate <= nd){
                    _initialDate = nd;
				}
	        } else{
	            priority = "0a";
                _initialDate = article_priority.getDateStr(1);
	            //_initialDate = publishTime;
	        }
	        $("#priorityDay").val(_initialDate);
            var _startDate = new Date();
            var _endDate = "";
            var _year = parseInt(_startDate.getFullYear()) + 1;
            var _month = _startDate.getMonth()+1;//获取当前月份的日期
            var _day = _startDate.getDate();
            _endDate = _year + "-" + article_priority.add_zero(_month) + "-" + article_priority.add_zero(_day);
			$("#priorityDay").datetimepicker({
				language : 'zh-CN', //语言
	            format: 'yyyy-mm-dd', //日期格式
				startDate:new Date(), //开始时间
				autoclose:true, //选择完后关闭选择器
				startView:2, //开始视图
				weekStart : 0,
				todayBtn:1, //链接当前日期
				todayHighlight:false,//高亮当前时间
				pickerPosition: "bottom-left",
				disabledDaysOfCurrentMonth : 1,
				forceParse : 0,
				minView: 2,
	            daysOfWeekDisabled: [],
	            initialDate: _initialDate,
	            startDate: _initialDate,
	            endDate: _endDate,
			});
		},
		getDateStr : function(AddDayCount) {
		    var dd = new Date();
		    dd.setDate(dd.getDate()+AddDayCount);//获取AddDayCount天后的日期
		    var y = dd.getFullYear();
		    var m = dd.getMonth()+1;//获取当前月份的日期
		    var d = dd.getDate();
            return y+"-"+article_priority.add_zero(m)+"-"+article_priority.add_zero(d);
		},
		add_zero:function(param){
			if(param < 10){
				return "0" + param;
			}
			return param;
		},
		doCancel : function(){
			window.onbeforeunload = null;
			
			$("#btnSave").disabled = true;
			$("#btnCancel").disabled = true;
			article_priority.beforeExit();
		},
		beforeExit : function(e) {
			var dataUrl = "../../e5workspace/after.do?UUID=" + $("#UUID").val();

			//若是直接点窗口关闭，并且是chrome浏览器，则单独打开窗口
			if (e && article_priority.isChrome())
				window.open(dataUrl, "_blank", "width=10,height=10");
			else
				window.location.href = dataUrl;
		},
		isChrome : function() {
			var nav = article_priority.navigator();
			return nav.browser == "chrome";
		},
		navigator : function(){
			var ua = navigator.userAgent.toLowerCase();
			// trident IE11
			var re =/(trident|msie|firefox|chrome|opera|version).*?([\d.]+)/;
			var m = ua.match(re);
			
			var Sys = {};
			Sys.browser = m[1].replace(/version/, "'safari");
			Sys.ver = m[2];
			return Sys;
		},
}
$(function(){
	article_priority.init() ;
});

