$(function() {
	article_form.init();
	$(":file").bind(
			"change",
			function() {
				if (file.notImgFile($(this).attr("id"))) {
					alert("对不起，请上传jpg格式");
					return;
				}
				;
				uploadOneFileAndShow(this, $(this).attr("docid"), $(this).attr(
						"docPath"));
			});
	$(".text_").focus(function(){
		  $(this).css({
				"height": 120,
				"position":"absolute",
				"z-index":"1199",
				"left":"0"
			});
		});
	$(".text_").blur(function(){
		  $(this).css({
			  "height": 43,
			  "position":""
		  });
		});
	/*
	 * $(".attPic").bind("change",function(){
	 * uploadOneFileAndShow(this,"AttImg"); });
	 */
});
var article_form = {
	init : function() {
		$("#btnSave").click(article_form.doSave);
		$("#btnCancel").click(article_form.doCancel);
	},
	// 保存提交
	doSave : function() {

		var formData = $("#form").serialize();
		$.ajax({
			type : "POST",
			url : "../../xy/paper/SaveAttachment.do",
			data : formData,
			success : function(data) {
				if (data == "empty") {
					alert("对不起，该报纸稿件没有图片");
					return;
				} else if (data == "error") {
					alert("保存失败");
					return;
				} else {
					var url = "../../" + data;
					window.location.href = url;
				}
			}
		})
	},

	// 退出按钮
	doCancel : function() {
		window.onbeforeunload = null;
		var dataUrl = "../../e5workspace/after.do?UUID=" + $("#UUID").val();
		window.location.href = dataUrl;
	}
}

function uploadOneFileAndShow(inputId, imgId, docPath) {
	return file._uploadOneFileAndShow(inputId, imgId, docPath);
}

var file = {
	// 判断非图片类型？
	notImgFile : function(id) {
		var icon = $("#" + id).val();
		if (icon && icon != "-") {
			var ext = icon.substring(icon.lastIndexOf(".") + 1, icon.length)
					.toLowerCase();
			return (ext && ext != "jpg");
		}
		return false;
	},
	// 实际的提交
	send : function(fileInput, url, docPath, completeCallback) {
		var form = $('<form style="display:none;"></form>');
		form.attr('accept-charset', "UTF-8");
		// IE versions below IE8 cannot set the name property of
		// elements that have already been added to the DOM,
		// so we set the name along with the iframe HTML markup:
		var counter = 1;
		var iframe = $('<iframe src="" name="iframe-transport-' + counter
				+ '"></iframe>');
		iframe.bind('load', function() {
			iframe.unbind('load').bind('load', function() {
				var response, success;
				// Wrap in a try/catch block to catch exceptions thrown
				// when trying to access cross-domain iframe contents:
				try {
					response = iframe.contents();
					// Google Chrome and Firefox do not throw an
					// exception when calling iframe.contents() on
					// cross-domain requests, so we unify the response:
					if (!response.length || !response[0].firstChild) {
						throw new Error();
					}
					response = response.find("body").text();
					// 格式为 1;附件存储设备;/201504/19/glj_myfilename.txt
					success = (response.charAt(0) == "1");
					response = response.substring(2);
				} catch (e) {
					response = undefined;
				}
				// The complete callback returns the
				// iframe content document as response object:
				completeCallback(success, response);
				// Fix for IE endless progress bar activity bug
				// (happens on form submits to iframe targets):
				$('<iframe src=""></iframe>').appendTo(form);
				window.setTimeout(function() {
					// Removing the form in a setTimeout call
					// allows Chrome's developer tools to display
					// the response result
					form.remove();
				}, 0);
			});
			form.prop('target', iframe.prop('name')).prop('action',
					url + "&docPath=" + docPath).prop('method', "POST");
			// Appending the file input fields to the hidden form
			// removes them from their original location:
			form.append(fileInput).prop('enctype', 'multipart/form-data')
			// enctype must be set as encoding for IE:
			.prop('encoding', 'multipart/form-data');
			// Remove the HTML5 form attribute from the input(s):
			fileInput.removeAttr('form');

			form.submit();
		});
		form.append(iframe).appendTo(document.body);
	},
	// 上传一张图片并显示缩略图（该上传按钮对象，要显示缩略图的imgid）
	_uploadOneFileAndShow : function(inputId, imgId, docPath) {
		var dataUrl = "../../xy/paper/Data.do?action=upload&DocLibID="
				+ $("#DocLibID").val();
		var fileInput = $(inputId);
		var inputName = fileInput.attr("name");
		file.send(fileInput, dataUrl, docPath, function(success, result) {
			if (success) {
				if ($("#AttImg" + imgId).is(":hidden")) {
					$("#AttImg" + imgId).show();
				}
				$("#edit" + imgId).hide();
				$("#AttImg" + imgId).attr("src",
						"../../xy/image.do?path=" + result);
				$("#Path" + imgId).attr("value", result);
			} else {
				alert("上传失败！" + result + "\n请确认已配置好存储设备（自身存储或通用的<附件存储>）。");
			}
		});
	}
}
