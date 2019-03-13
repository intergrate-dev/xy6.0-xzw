var score_form = {
	init : function() {
		//页面加载时进行校验
		$("#srLimit").removeClass();
		$("#srLimit").addClass("custform-input validate[required,custom[positiveNumber]]");
		//设置有效时间，修改开始时间和结束时间的class
		$("#srStartTime").removeClass();
		$("#srStartTime").addClass("custform-input");
		$("#srEndTime").removeClass();
		$("#srEndTime").addClass("custform-input");
		//规则类型切换
		$("#srType").change(score_form.ruleChange);
		//非负值限制
		$("#srScore").addClass("validate[min[0]]");
		$("#srExperience").addClass("validate[min[0]]");
		//按钮添加验证，开始日期、截至日期验证
		$("#btnFormSave").click(score_form.Beforesubmit);
		$("#srEndTime").focus(function(){
			$("#srEndTime").removeClass();
			$("#srEndTime").addClass("custform-input validate[maxSize[255]]");
		});
	},
	ruleChange : function() {
		var value = $("#srType").val();
		if (value == 3 || value == 5) {
			$("#srLimit").val("1");
		}
		switch(value){
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
				$("#srLimit").removeClass();
				$("#srLimit").addClass("custform-input validate[required,custom[positiveNumber]]");
				break;
			case '6':
				$("#srLimit").removeClass();
				$("#srLimit").addClass("custform-input validate[required,custom[range1]]");
				break;
			default:
				break;
		}
	},
	Beforesubmit : function(){
		$("#srEndTime").removeClass();
		$("#srEndTime").addClass("custform-input validate[maxSize[255],funcCall[checkEndDate]]");
	}
}
function checkEndDate (field, rules, i, options){
	var srStartTime =  $("#srStartTime").val();
	var srEndTime =  $("#srEndTime").val();
	srStartTime = new Date(srStartTime.replace(/\-/g, "\/"));  
	srEndTime = new Date(srEndTime.replace(/\-/g, "\/"));   
	
	if(srStartTime!="" && srEndTime!="" && srStartTime > srEndTime){
		return options.allrules.validateEndDate.alertText;
	}
}
//获取url地址中的参数
function getUrlVars(name){
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
    var r = window.location.search.substr(1).match(reg);  //匹配目标参数
    if (r != null) return unescape(r[2]); return null; //返回参数值
}
$(function(){
	score_form.init();
	var siteID = getUrlVars("siteID");
	$("#m_siteID").val(siteID);
});