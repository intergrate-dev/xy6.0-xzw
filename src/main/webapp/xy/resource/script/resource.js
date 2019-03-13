var imgType = "image/gif,image/jpeg,image/jpg,image/png";
var res_form = {
	init : function() {

		// 修改表单的提交功能
		$("#form").attr("target", "iframe");
		$("#form").attr("action", "../../xy/resource/formSubmit.do");

		$("#res_dir").prop("readonly", true);
		
		if (parseInt($("#DocID").val()) > 0) {
			$("input[type='radio']").prop("disabled", true);
			$("#btnDir").prop("disabled", true);
		}
		
		var _res_type = $(":radio[id^=res_type]:checked").attr("id");
		// 1. 初始化时，给res_file 添加accept属性
		if ($("#res_type").val() && $("#res_type").val() != "") {
			if ($("#res_type").val() == "0") {
				$("#res_file").attr("accept", "*.*");
				$("#DIV_res_file").append("<br/><br/><font color='gray'></font>");
			} else {
				$("#res_file").attr("accept", imgType);
				$("#DIV_res_file").append("<br/><br/><font color='gray'>*文件类型限制："+imgType.split("image/").join("")+"</font>");
				
			}
		}
		// 2. radio 添加click事件
		$(":radio[id^=res_type_]").click(function() {
			if ($(this).val() != "0") {
				$("#res_file").attr("accept", imgType);
				$("#DIV_res_file").find("font").html("*文件类型限制："+imgType.split("image/").join("")+"</font>");
				$("#DIV_res_file").find("font").attr("color","gray");
			} else if ($(this).val() == "0") {
				$("#res_file").attr("accept", "*.*");
				$("#DIV_res_file").find("font").html("");
				$("#DIV_res_file").find("font").attr("color","gray");
			}
		});
		
		//选择文件后，若资源名称为空，则把文件名自动填写到资源名称输入框中
		$("#res_file").change(function(){
			if($("#res_name").val()==""){
				_fileName = $("#res_file").val();
				_fileName = _fileName.substr((_fileName.lastIndexOf("/")==-1?_fileName.lastIndexOf("\\"):_fileName.lastIndexOf("/"))+1);
				$("#res_name").val(_fileName);
			}
		});
		
		// 禁用按钮的提交功能
		$("#btnFormSave").attr("onclick", "return false;");
		
		var option = $("#res_fileType").html();
		var fileTypeValue = $("#res_fileType").val();
		$("#res_fileType").html("<option></option>"+option);
		if($("#res_fileName").val()){
			$("#res_fileType").val(fileTypeValue);
			$("#DIV_res_file").find("font").html("*要求同名文件："+$("#res_fileName").val());
		}else{
			$("#res_fileType").val("");
		}
		//如果是新建，默认点击第一个radio
		if (!(parseInt($("#DocID").val()) > 0)) {
			c_res_type = xy_cookie.getCookie("res_type");
			c_res_dir = xy_cookie.getCookie("res_dir");
			c_res_dir_ID = xy_cookie.getCookie("res_dir_ID");
			
			if(c_res_type){
				$("#res_type_"+c_res_type).click();
				$("#res_dir").val(c_res_dir);
				$("#res_dir_ID").val( c_res_dir_ID );
			}else{
				$(":radio[id^=res_type_]:first").click();
			}
		}
	},
	reset : function() {
		$("input[type='radio']").prop("disabled", false);
		
		var oldValue = $("#res_type").attr("oldValue");
		if (oldValue != "-") $("#res_type").val(oldValue);
	}
}
e5_form.event.doSave = function() {
	if (!$("#form").validationEngine("validate")) {
		// 验证提示
		$("#form").validationEngine("updatePromptsPosition");
		return false;
	}
	
	var fileName = $("#res_file").val();
	if (!fileName){
		fileName = $("#res_file").attr("oldvalue");
		if (fileName == "-") fileName = "";
	}
	if (fileName) {
		res_form.reset();
		
		if (!e5_form.file.upload()) {
			return false;
		}
		return true;
	}
	
	alert("请选择文件");
	return false;
};
/**
 * 提交文件时检查扩展名
 * 
 * @author guzm
 */
e5_form.file.upload = function() {
	var files = $("#form input[type='file']");
	if (files.length == 0)
		return false;
	// 判断文件扩展名
	if (checkExtension(files))
		return true;
	e5_form.file.counter_files = files.length;
	e5_form.file.counter_uploaded = 0;

	files.each(e5_form.file._uploadOneFile);

	return false;
};

e5_form.file._oneSuccess = function() {
	// 若所有的附件都上传了，则提交表单
	if (++e5_form.file.counter_uploaded == e5_form.file.counter_files) {
		// if (!checkExtension($("#form input[type='file']"))) {
		if(e5_form.file.counter_files!=0 && currentFileName!=""){
			$("#res_fileName").val(currentFileName);
			// 如果是图片的话，那filetype就设置为图片
			imgTypeLow = $.trim(imgType.toLocaleLowerCase());
			if (imgTypeLow.indexOf(fileExtensionLow) != -1) {
				$("#res_fileType").val("图片");
			} else if (fileExtensionLow == "html" || fileExtensionLow == "htm") {
				$("#res_fileType").val("html");
			} else if (fileExtensionLow == "js") {
				$("#res_fileType").val("js");
			} else {
				$("#res_fileType").val("其它");
			}
		}
		
		xy_cookie.setCookie("res_type",$(":radio[name=res_type]:checked").val(),1);
		xy_cookie.setCookie("res_dir",$("#res_dir").val(),1);
		xy_cookie.setCookie("res_dir_ID",$("#res_dir_ID").val(),1);
		
		$("#form").submit();
		// }
	}
};

/**
 * 检查扩展名
 * 
 * @param files
 * @returns {Boolean}
 */
var currentFileName = "";
var currentFileExtension = "";

function checkExtension(files) {
	for ( var i = 0, size = files.length; i < size; i++) {
		// 获取扩展名
		var fileName = $(files).eq(i).val();
		var fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1,
				fileName.length);
		// 如果是js 或者是jsp不能提交fileExtension == "js" ||
		if (fileExtension == "jsp") {
			//alert("对不起，不能提交jsp文件");
			$("#DIV_res_file").find("font").html("*对不起，不能提交jsp文件!");
			$("#DIV_res_file").find("font").attr("color","red");
			// $("#extensionIllegal").val("true");
			return true;
		}
		imgTypeLow = $.trim(imgType.toLocaleLowerCase());
		fileExtensionLow = $.trim(fileExtension.toLocaleLowerCase());
		// 如果是图片的话，扩展名需要在imgtype里
		if ($("input[name=res_type]:radio:checked").val() != "0"
				&& $("input[name=res_type]:radio:checked").val() != 0
				&& imgTypeLow.indexOf(fileExtensionLow) == -1) {
			//alert("对不起，您上传的文件需要是" + imgType.split("image/").join("") + "！");
			$("#DIV_res_file").find("font").html("*文件类型限制："+ imgType.split("image/").join(""));
			$("#DIV_res_file").find("font").attr("color","red");
			return true;
		}
		var newName = fileName.substring(fileName.lastIndexOf("/") + 1,
				fileName.length).substring(fileName.lastIndexOf("\\") + 1,
				fileName.length);
		// 重新上传的文件要和原文件名一样？
		if ($.trim($(files).eq(i).attr("oldvalue")) != "-"
				&& $.trim($(files).eq(i).attr("oldvalue")) != ""
				&& $.trim(fileName) != "") {
			// 获得原文件的名字 用户名_图片名
			var oldName = $(files).eq(i).attr("oldvalue");
			oldName = oldName.substring(oldName.lastIndexOf("/") + 1,
					oldName.length);
			oldName = oldName.substring(oldName.indexOf("_") + 1,
					oldName.length);
			// 获得新文件的名字
			
			if ($.trim(oldName) != $.trim(newName)) {
				//alert("对不起，新提交的文件必须跟原文件同名！");
				$("#DIV_res_file").find("font").html("*新提交的文件必须跟原文件同名！");
				$("#DIV_res_file").find("font").attr("color","red");
				return true;
			}
		}

		currentFileName = newName;
		currentFileExtension = fileExtensionLow;

	}
	return false;
}

$(function() {
	res_form.init();
});