var app_package = {
	init : function() {
		//获得主界面应用id
		$("#mp_maId").val(($(window.parent.document).find("div[name='applist'][class*='select']").attr("appID")));
		var type = $(window.parent.document).find("div[name='applist'][class*='select']").attr("apptype");
		//只能上传apk文件
		$("#mp_package").attr("accept",".apk");
		// 修改表单的提交功能
		$("#form").attr("target", "iframe");
		$("#form").attr("action", "../../xy/mPackage/formSubmit.do");
		$("#form").append("<input type='hidden' name='mp_type' value = "+type+">");
		$("#form").append("<input type='hidden' name='oldChannel' value = "+$("#mp_channel_ID").attr("oldValue")+">"); 
		$("#form").append("<input type='hidden' name='oldPackage' value = "+$("#mp_package").attr("oldValue")+">"); 
		//添加change事件
		//$("#mp_type").change(app_package.typeChange);
		$("#mp_channel_ID").change(app_package.channelChange);
		//默认调用设备类型改变事件
		app_package.typeChange(type);
		//如果为修改 则去掉包的必填验证
		if($("#DocID").val()!="0" && $("#DocID").val()!=""){
			$("#LABEL_mp_package").removeClass("custform-label-require");
		}
	},
	typeChange : function(type){
		$("tr").show();
		$("#mp_maId").parents("tr:eq(0)").hide();
		if(type==0){
			$("#mp_channel_ID").parents("tr:eq(0)").hide();
			$("#mp_package").parents("tr:eq(0)").hide();
		}else{
			$("#mp_versionCode").parents("tr:eq(0)").hide();
			$("#mp_url").parents("tr:eq(0)").hide();
		}
	},
	channelChange : function(){
		$("#mp_channel").attr("value",($("#mp_channel_ID").find("option:selected").text()));
	}
}

e5_form.event.otherValidate = function() {
	if(!$("#LABEL_mp_package").is(":hidden") && $("#LABEL_mp_package").hasClass('custform-label-require')){
		if($("#mp_package").val()==""){
			alert("请上传Apk包");
			$("#mp_package").focus();
			return false;
		}
	}
	return true;
}

$(function() {
	app_package.init();
});
