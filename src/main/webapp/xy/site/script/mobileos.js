var mobile_os={
	init : function() {
		if (!$("#os_siteID").val())
			$("#os_siteID").val(e5_form.getParam("siteID"));
		
		$("#os_pageTime").addClass("validate[min[0],max[100]]");//启动页显示秒数，>=0
		//$("#os_pageUrl").addClass("validate[custom[url]]");
		
		$("#os_dir").attr("readonly", true); //发布目录只读
		
		// 修改表单的提交功能
		$("#form").attr("target", "iframe");
		$("#form").attr("action", "../../xy/mobileos/formSubmit.do");
		
		var pageType = $("#os_pageType").val();
		mobile_os.changeType(pageType, true);
		
		$("#DIV_os_picSmall").append("<br/><br/><font color='gray'></font>");
		$("#DIV_os_picUrlMiddle").append("<br/><br/><font color='gray'></font>");
		$("#DIV_os_picUrlBig").append("<br/><br/><font color='gray'></font>");
		$("#DIV_os_templateZip").append("<br/><br/><font color='gray'></font>");
		$("#DIV_os_logoPic").append("<br/><br/><font color='gray'></font>");
		$("#DIV_os_appDownloadImage").append("<br/><br/><font color='gray'></font>");
		
		$('#os_pageType').change(function(){ 
			var type = $("#os_pageType").val();
			mobile_os.changeType(type);
		})
        debugger;
		mobile_os.addClearBtns();
	},
	addClearBtns : function(){
		mobile_os.addClearBtn("os_picSmall");
		mobile_os.addClearBtn("os_picUrlMiddle");
		mobile_os.addClearBtn("os_picUrlBig");
		mobile_os.addClearBtn("os_logoPic");
		mobile_os.addClearBtn("os_appDownloadImage");
	},
	//添加清理按钮
	addClearBtn : function(name1){
		var clearBtn = $('<img class="close" src="../../images/tab_del.gif"/>')
			.attr("for", name1)
			.attr("id", "cb_" + name1)
			.click(mobile_os.doClear)
			.css("left","250px")
			.css("top","14px")
			.css("display","block");
		var input = $("#" + name1);

		input.after(clearBtn);
		input.change(function () {
			$("#" +"img_" +name1).show();
		})

	},
	//清理事件响应
	doClear : function(evt) {
		debugger;
		var name = $(evt.target).attr("for");
		$("#" + name).attr("oldvalue","");
		$("#" + name).val("");
		$("#" +"img_" +name).hide();
	},
	changeType : function(pageType, init){
		if ($("#os_name").length == 0) return;
		
		if ( pageType  == 0) {
			$("#os_pageUrl").val("");//若有样式提示，隐藏后还会显示。因此清空
			$("#os_pageUrl").focus();
			
			$("#SPAN_os_pageUrl").hide();
			$("#SPAN_os_picName").show();
			$("#SPAN_os_picSmall").show();
			$("#SPAN_os_picUrlMiddle").show();
			$("#SPAN_os_picUrlBig").show();
		} else {
			$("#SPAN_os_pageUrl").show();
			$("#SPAN_os_picName").hide();
			$("#SPAN_os_picSmall").hide();
			$("#SPAN_os_picUrlMiddle").hide();	
			$("#SPAN_os_picUrlBig").hide();
		}
	},
	//判断非图片类型？
	notImgFile : function(id) {
		var icon = $("#" + id).val();
		if (icon && icon != "-") {
			var ext = icon.substring(icon.lastIndexOf(".") + 1, icon.length).toLowerCase();
			return (ext && ext != "jpg" && ext != "png" && ext != "jpeg" && ext != "gif");
		}
		return false;
	}
};
$(function() {
	mobile_os.init();
});

e5_form.event.otherValidate = function() {
	var isValidate = true;
	
	if (mobile_os.notImgFile("os_picSmall")){
		$("#DIV_os_picSmall").find("font").html("* 图片上传格式支持jpg,png");
		$("#DIV_os_picSmall").find("font").attr("color","red");
		isValidate = false;
	}
	if (mobile_os.notImgFile("os_picUrlMiddle")){
		$("#DIV_os_picUrlMiddle").find("font").html("* 图片上传格式支持jpg,png");
		$("#DIV_os_picUrlMiddle").find("font").attr("color","red");
		isValidate = false;
	}
	if (mobile_os.notImgFile("os_picUrlBig")){
		$("#DIV_os_picUrlBig").find("font").html("* 图片上传格式支持jpg,png");
		$("#DIV_os_picUrlBig").find("font").attr("color","red");
		isValidate = false;
	}
	if (mobile_os.notImgFile("os_logoPic")){
		$("#DIV_os_logoPic").find("font").html("* 图片上传格式支持jpg,png");
		$("#DIV_os_logoPic").find("font").attr("color","red");
		isValidate = false;
	}
	if (mobile_os.notImgFile("os_appDownloadImage")){
		$("#DIV_os_appDownloadImage").find("font").html("* 图片上传格式支持jpg,png");
		$("#DIV_os_appDownloadImage").find("font").attr("color","red");
		isValidate = false;
	}
	if ($("#os_templateZip").length > 0) {
		var templateZipfileName = $("#os_templateZip").val();
		if (!templateZipfileName){
			templateZipfileName = $("#os_templateZip").attr("oldvalue"); 
		}
		if (!templateZipfileName){
			$("#DIV_os_templateZip").find("font").html("*对不起,没有上传模板打包文件");
			$("#DIV_os_templateZip").find("font").attr("color","red");
			isValidate = false;
		}
		var templateZipfileExtension = templateZipfileName.substring(templateZipfileName.lastIndexOf(".") + 1,
				templateZipfileName.length).toLocaleLowerCase();
		// 判断文件格式
		if (templateZipfileExtension != "zip" ) {
			$("#DIV_os_templateZip").find("font").html("*对不起，模板打包文件格式要求为zip");
			$("#DIV_os_templateZip").find("font").attr("color","red");
			isValidate = false;
		}
	}
	
	return isValidate;
};
//查是否重名时，增加站点条件
e5_form._duplicateUrl = function(field) {
	var siteID = e5_form.getParam("siteID");
	
	var theURL = "xy/Duplicate.do"
		+ "?DocLibID=" + $("#DocLibID").val()
		+ "&DocIDs=" + $("#DocID").val()
		+ "&siteID=" + siteID
		+ "&value=" + e5_form.encode(field.val());
	return theURL;
}
