var move_top = {
		init : function(){
			move_top.initDateTimePickerWeb() ;
			move_top.expire() ;
			$("#isExpire").on("click",function(){
				move_top.expire() ;
			}) ;
			$("#btnSave").click(function(){
	            $("#caForm").submit();
			}) ;
			$("#btnCancel").click(function(){
				move_top.doCancel() ;
			}) ;
		},
		initDateTimePickerWeb : function(){
			$("#expireTime").datetimepicker({
				language : 'zh-CN', //语言
				format : 'yyyy-mm-dd hh:ii' , //日期格式
				startDate:new Date(), //开始时间
				autoclose:true, //选择完后关闭选择器
				startView:2, //开始视图
				weekStart : 0,
				todayBtn:1, //链接当前日期
				todayHighlight:true,//高亮当前时间
				pickerPosition: "bottom-left",
				disabledDaysOfCurrentMonth : 0,
				forceParse : 0,
				initialDate: move_top.initalDate(), //初始化过期时间,为第二天零时
			});
		},
		expire : function(){
			if($("#isExpire").is(':checked')){
				$("#expireTime").removeAttr("disabled") ;
				$("#expireTime").val(move_top.getDateStr(1)) ;
				$(".custform-label-cue").text("置顶时间过期,该稿件将自动取消置顶") ;
			}else{
				$("#expireTime").attr("disabled","disabled") ;
				$("#expireTime").val("") ;
				$(".custform-label-cue").text("若需设置置顶时限，请先选中") ;
			}
		},
		initalDate : function(){
			 var dd = new Date();
			 dd.setHours(24) ;
			 dd.setMinutes(0) ;
			 dd.setSeconds(0) ;
			 return dd;
		},
		getDateStr : function(AddDayCount) {
			var expireTime = $("#timeTemp").val() ;
			if("" != expireTime && null != expireTime){
				return $("#timeTemp").val() ;	
			}
		    var dd = new Date();
		    dd.setDate(dd.getDate()+AddDayCount);//获取AddDayCount天后的日期
		    var y = dd.getFullYear();
		    var m = dd.getMonth()+1;//获取当前月份的日期
		    var d = dd.getDate();
		    var h = dd.getHours();
		    var i = dd.getMinutes();
		    return y+"-"+move_top.add_zero(m)+"-"+move_top.add_zero(d)+" "+"00:00";
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
			move_top.beforeExit();
		},
		beforeExit : function(e) {
			var dataUrl = "../../e5workspace/after.do?UUID=" + $("#UUID").val();

			//若是直接点窗口关闭，并且是chrome浏览器，则单独打开窗口
			if (e && move_top.isChrome())
				window.open(dataUrl, "_blank", "width=10,height=10");
			else
				window.location.href = dataUrl;
		},
		isChrome : function() {
			var nav = move_top.navigator();
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
	move_top.init() ;
});

