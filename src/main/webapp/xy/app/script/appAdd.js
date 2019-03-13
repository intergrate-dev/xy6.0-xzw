/**
 * 期刊的导航
 */
var app_add = {
	init : function(sandbox) {
		//平台id
		$("#ma_moID").val($(window.parent.document).find("#moId").val());
		$("#btnFormCancel").click(app_add.btnCancel);
		$("#ma_dir").attr("readonly","readonly");
		/*$("#form").attr("target", "iframe");
		$("#form").attr("action", "../../xy/mobileAppformSave.do");*/
		//app_add.btnCancel();
		//添加change事件
		$("#ma_type").change(app_add.typeChange);
		app_add.typeChange();
		
		$("#btnFormCancel").click(app_add.btnCancel);
		$("#btnFormSave").click(app_add.doSubmit);
		
	},
	typeChange : function(){
		if($("#ma_type").val()==0){
			$("#ma_md5").parents("tr:eq(0)").hide();
		}else{
			$("#ma_md5").parents("tr:eq(0)").show();
		}
	},
	btnCancel : function() {
		parent.mobile_app.dialog.close();	
	},
	//表单保存后仍保留页面
	refresh : function() {
		parent.mobile_app.dialog.close();	
	},
	doSubmit : function(){
		if ($("#form").validationEngine("validate")) {
			$.ajax({
				async: false,
				url : "../../xy/mobileAppformSave.do",
				type : 'POST',
				data:$("form").serialize(),
				dataType : 'json',
				success : function(data, status) {
					window.parent.location.reload();
					app_add.btnCancel();
				},
				error : function(xhr, textStatus, errorThrown) {
					alert("对不起，通信出现异常！无法保存！");
				}
			});
		}
	}
	
};
$(function() {
	app_add.init();
});