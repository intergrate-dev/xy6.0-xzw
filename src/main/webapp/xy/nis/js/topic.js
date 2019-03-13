e5_form.dataReader._getDataUrl = function() {
	var theURL = "e5workspace/manoeuvre/FormDocFetcher.do?FormID=" + $("#FormID").val()
		+ "&DocLibID=" + $("#DocLibID").val()
		+ "&DocID=" + $("#DocID").val()
		+ "&dateFull=1";
	return theURL;
}
var topic_form = {
	init : function() {
		
		//给定制的表单自动添加站点ID
		var siteID = e5_form.getParam("siteID");
		$("#t_siteID").val(siteID);
		
		//初始订阅数
		$("#t_countInitial").addClass("validate[min[0],max[10000000]]");
		
		//发布时间：去掉date格式验证
		$("#t_pubDate").removeClass("validate[required,custom[dateFormat]]");
		$("#t_pubDate").addClass("validate[required,custom[dateTimeFormat1]]");
		
		//默认以当前时间为发布时间
		if (!$("#t_pubDate").val()) {
			var today = new Date();
			var defaultDate = today.getFullYear() + "-" + (today.getMonth() + 1) + "-" + today.getDate()
				+ " " + today.getHours() + ":" + today.getMinutes() + ":" + today.getSeconds();
			$("#t_pubDate").val(defaultDate);
		}
		$('#t_pubDate')[0].onclick = null;
		
		$('#t_pubDate').datetimepicker({
			language : 'zh-CN',
			weekStart : 0,
			todayBtn : 1,
			autoclose : 1,
			todayHighlight : true,
			startView : 2,
			minView : 0,
			disabledDaysOfCurrentMonth : 0,
			forceParse : 0,
			pickerPosition: "bottom-left",
			format : 'yyyy-mm-dd hh:ii:ss'
		});
		$('#t_pubDate').datetimepicker().on('changeDate', function(ev) {
		});
	}
}
$(function(){
	topic_form.init();
});