var template_form = {
	init : function() {
		var docID = parseInt($("#DocID").val());
		if (docID > 0) {
			$("[name='t_type']").prop("disabled", true);
			$("[name='t_channel']").prop("disabled", true);
		}
		$("#form").attr("target", "iframe");
		$("#form").attr("action", "../../xy/template/FormSave.do");

		$("#t_file").change(function(){
			if($("#t_name").val()==""){
				_fileName = $(this).val();
				_fileName = _fileName.substring((_fileName.lastIndexOf("/")==-1?_fileName.lastIndexOf("\\"):_fileName.lastIndexOf("/"))+1, _fileName.lastIndexOf(".") );
				$("#t_name").val(_fileName);
			}

		});

//		$.ajax({url: "../../xy/template/Channels.do", async:false, success: function (data) {
//				data = data.split(",");
//				if (data.length > 1) return;
//				
//				data = parseInt(data[0]);
//				if (data == 0) {
//					if (docID == 0) {
//						$("#t_channel_0").attr("checked", "checked");
//					}
//				} else {
//					$("label[for='t_channel_0']").hide();
//					$("#t_channel_1").attr("checked", "checked");
//				}
//			}
//		});		
	},
	reset : function() {
		$("[name='t_type']").prop("disabled", false);
		$("[name='t_channel']").prop("disabled", false);
		
		var oldValue = $("#t_type").attr("oldValue");
		if (oldValue != "-") $("#t_type").val(oldValue);
		
		var oldValue = $("#t_channel").attr("oldValue");
		if (oldValue != "-") $("#t_channel").val(oldValue);
	}
}

$(function() {
	template_form.init();
	$("#inserttxt_034d7a4422208b63ffedec2e562c4fe2").children("span").html("模板文件要求：html文件或Zip压缩包");
	$("#img_t_file").attr("path","");
	$("#img_t_file").click(function(){
		var DocLibID = $("#DocLibID").val();
		var DocID = $("#DocID").val();
		var urlDown = "../../xy/template/getDoc.do?doclibID="+DocLibID+"&DocID="+DocID;
		$.ajax({url: urlDown, async:false, success: function (data) {
			if(data == "false"){
				/*var html = $("#DIV_t_file").html();
				var errHtml = "<span style='color:red;' id='errHtml'>未找到相应模板，请尝试重新上传！</span>"
				$("#DIV_t_file").html(html+errHtml);*/	
				alert("未找到相应模板，请尝试重新上传！");
			}else{
				window.location.href="../../xy/template/loadZip.do?doclibID="+DocLibID+"&DocID="+DocID;
			}
		},
		error: function() {
            alert("未找到相应模板，请尝试重新上传！");
        }
	});	
	});
});

e5_form.event.doSave = function() {
	if (!$("#form").validationEngine("validate")) {
		// 验证提示
		$("#form").validationEngine("updatePromptsPosition");
		return false;
	}
	template_form.reset();
	
	var fileName = $("#t_file").val();
	if (fileName == null || fileName == ""){
		fileName = $("#t_file").attr("oldvalue"); 
	}
	var fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1,
			fileName.length).toLocaleLowerCase();
	// 判断文件格式
	if ((fileExtension == "html" || fileExtension == "htm" 
		|| fileExtension == "xml" || fileExtension == "json" || fileExtension == "shtml" || fileExtension == "zip") && fileName.split(".").length<=2) {//|| fileExtension == "zip"
		$("#t_fileType").val(fileExtension);
		//若有附件，则先提交--调用新接口上传
		/*if (!e5_form.file.uploadNew()) {
			return false;
		}*/
		if (!e5_form.file.upload()) {
			return false;
		}
		$("#t_type").prop("disabled", false);
		$("#t_channel").prop("disabled", false);
		return true;
	}
	alert("请选择正确格式的模板文件");
	return false;
};
