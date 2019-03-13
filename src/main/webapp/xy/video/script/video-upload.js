//视频上传
var video_upload = {
	init : function() {
		$("#uploadSubmit").click(video_upload.videoSubmit);
		$("#uploadCancel").click(video_upload.videoCancel);
	},
	videoSubmit : function(){
		//根据sessionid获取上传的视频然的上传进度，上传完毕后再关闭
		$.ajax({
			url: "../../xy/video/getFileUploadProgress.do",
			dataType: "text",
			data : {
				"sessionID" : sessionID
			},
			async: false,
			error: function (XMLHttpRequest, textStatus, errorThrown) {
				alert("读取初始化参数失败。" + errorThrown + ':' + textStatus);  // 错误处理
			},
			success: function (data) {
				if (data.substr(0, 7) == "success") {//推送成功
					parent.uploadClose(sessionID);
				} else {
					alert("文件未上传完毕");
				}
			}
		});
	},
	videoCancel : function(){
		$.ajax({
			url: "../../xy/video/UploadCancel.do",
			dataType: "text",
			data : {
				"sessionID" : sessionID
			},
			async: false,
			error: function (XMLHttpRequest, textStatus, errorThrown) {
				alert("读取初始化参数失败。" + errorThrown + ':' + textStatus);  // 错误处理
			},
			success: function (data) {
				parent.uploadCancel();
			}
		});
	} 
	
};

$(function() {
	video_upload.init();
});