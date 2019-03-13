var source_form = {
	init : function() {
		//站点ID赋值
		var siteID = e5_form.getParam("siteID");
		$("#src_siteID").val(siteID);
		
		$("#src_url").addClass("validate[custom[urlVal]]");
		
		// 修改表单的提交功能
		$("#form").attr("target", "iframe");
		$("#form").attr("action", "../../xy/source/formSubmit.do");
		
		//显示来源图标
		icon_form.showIcon("src_icon");
	}
}

var icon_form = {
	//显示头像
	showIcon : function(id) {
		var url = $("#" + id).attr("oldvalue");
		if (url && url != "-") {
			url = "../../xy/image.do?path=" + url;
			$("#iconImg").attr("src", url);
			$("#iconImg").css("max-width", 150);
			$("#iconImg").css("max-height", 150);
			
			$("#img_" + id).hide();
		} else {
			$("#iconImg").hide();
		}
		$("#labelIconImg").hide();
	}
}

e5_form.event.otherValidate = function() {
	if (e5_form.file.notImgFile("src_icon")){
		alert("请选择jpg, png, gif类型的文件");
		return false;
	}
	return true;
}
$(function() {
	source_form.init();
});