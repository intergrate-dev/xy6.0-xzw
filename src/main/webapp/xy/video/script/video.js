var nowFile, onQueueFile = null;
var video_form = {
	videoDialog: null,
	sessionID: 0,
	init: function(){
		var groupID = e5_form.getParam("groupID");
		$("#v_catID").val(groupID);

		var siteID = e5_form.getParam("siteID");
		$("#v_siteID").val(siteID);
		$("#v_fileName").attr("readonly", true);

		video_form.loadInitParam();
		$("#videoSelect").click(video_form.videoSelect);

		$("#v_bitrate").addClass("validate[min[1]]");
		$("#v_resWidth").addClass("validate[min[1]]");
		$("#v_resHeight").addClass("validate[min[1]]");

		$("#form").attr("target", "iframe");
		$("#form").attr("action", "../../xy/video/FormSave.do");

		video_form.queryVideoPath();
		video_form.initUploadPlugin();


	},
	videoSelect: function(){
		var url = "../../xy/video/Add.do";
		video_form.videoDialog = e5.dialog({
			type: "iframe",
			value: url
		}, {
			title: "上传视频",
			width: "800px",
			height: "600px",
			resizable: false,
			fixed: true
		});
		video_form.videoDialog.show();
	},
	loadInitParam: function(){
		$.ajax({
			url: "../../xy/video/getInitParam.do",
			dataType: "json",
			async: false,
			error: function(XMLHttpRequest, textStatus, errorThrown){
				alert("读取初始化参数失败。" + errorThrown + ':' + textStatus);  // 错误处理
			},
			success: function(data){
				$("#v_resWidth").val(data.resWidth);
				$("#v_resHeight").val(data.resHeight);
				$("#v_bitrate").val(data.bitrate);
				$("#v_format").val(data.format);
				if(data.VJ=="否"){
					$("#v_bitrate").parent().parent().parent().parent().hide();
					$("#v_resWidth").parent().parent().parent().parent().hide();
					$("#v_resHeight").parent().parent().parent().parent().hide();
				}
			}
		});
	},
	initUploadPlugin: function(){
		var $list = $("#thelist");
		var uploader = WebUploader.create({
			// swf文件路径
			swf: 'Uploader.swf',
			// 文件接收服务端。
			server: '../../xy/upload/uploadFile.do',
			// 选择文件的按钮。可选。
			// 内部根据当前运行是创建，可能是input元素，也可能是flash.
			pick: '#picker',
			formData: {
				targetPath: "video.upload.path",
				salt:video_form.salt
			},
			chunked: true,  //分片处理
			chunkSize: 1 * 1024 * 1024, //每片1M
			chunkRetry: false,//如果失败，则不重试
			threads: 1,//上传并发数。允许同时最大上传进程数。
			// runtimeOrder: 'flash',
			// 禁掉全局的拖拽功能。这样不会出现图片拖进页面的时候，把图片打开。
			disableGlobalDnd: true,
			fileNumLimit: 1,
			auto: false,
			accept :{
				//mimeTypes: 'video/*'
				mimeTypes: 'video/x-flv,video/mp4,application/x-mpegURL,video/MP2T,video/3gpp,video/quicktime,video/x-msvideo,video/x-ms-wmv,video/x-m4v,video/mpeg,'
			},
			// 不压缩image, 默认如果是jpeg，文件上传前会压缩一把再上传！
			resize: false
		});


		// 当有文件被添加进队列的时候
		uploader.on('fileQueued', function(file){
			onQueueFile = file;

			$("#tempInput").attr("placeholder", file.name);
			$list.append(
				'<div id="' + file.id + '" class="item">' +
				'<div class="form-group">' +
				/*'<input type="text" class="custform-input" id="" style="height: 34px; width: 400px;" placeholder="' +
				 file.name +
				 '" value="" readonly="readonly">' +*/
				'<span class="glyphicon glyphicon-remove form-control-feedback" id="removeSpan" aria-hidden="true" style="cursor: pointer!important;pointer-events: auto;"></span> ' +
				'</div>' +
				'<p class="state">等待上传...</p>' +
				'</div>');
		});

		// 文件上传过程中创建进度条实时显示。
		uploader.on('uploadProgress', function(file, percentage){
			var $li = $('#' + file.id),
				$percent = $li.find('.progress .progress-bar');

			// 避免重复创建
			if(!$percent.length){
				$percent = $('<div class="progress progress-striped active">' +
					'<div class="progress-bar" role="progressbar" style="width: 0%">' +
					'</div>' +
					'</div>').appendTo($li).find('.progress-bar');
			}

			$li.find('p.state').text('上传中');
			$percent.css('width', percentage * 100 + '%');
		});

		uploader.on('uploadSuccess', function(file, response){
			$("#tempInput").val($("#tempInput").attr("placeholder"));
			$('#' + file.id).find('p.state').text('已上传');
		});


		uploader.on('beforeFileQueued', function(file){
			if(nowFile){
				$('#' + nowFile.id).remove();
				uploader.removeFile(nowFile.id);
			}
			if(onQueueFile){
				$('#' + onQueueFile.id).remove();
				uploader.removeFile(onQueueFile.id);
			}
			$('#tempInput').val('').attr('placeholder', '');
		});

		uploader.on('uploadError', function(file){
			$('#' + file.id).find('p.state').text('上传出错');
		});

		uploader.on('uploadComplete', function(file){
			if(video_form.videoPath.lastIndexOf("\/")>0){
				var Slash = "/";
			}else{
				var Slash = "\\";
			}
			nowFile = file;
			var path = file.name;
			var suffix = path.substring(path.lastIndexOf(".")+1);
			
			$("#v_path").val(video_form.videoPath + Slash + file.name.substr(0, file.name.lastIndexOf(".")) +"."+ suffix);
			
			$("#v_format").val(suffix);
			
			$("#v_transPath").val(video_form.tranPath + Slash + file.name.substr(0, file.name.lastIndexOf(".")) );
			
			$("#v_fileName").val(file.name);
			$('#' + file.id).find('.progress').fadeOut();
		});


		$("#ctlBtn").click(function(){
			uploader.upload();
		});

		$("#cancelBtn").click(function(){
			uploader.reset();
		});
		$("#thelist").on("click", ".glyphicon-remove", function(){
			if(onQueueFile){
				$('#' + onQueueFile.id).remove();
				uploader.removeFile(onQueueFile.id);
				onQueueFile = null;
			}
			if(nowFile){
				$('#' + nowFile.id).remove();
				uploader.removeFile(nowFile.id);
				nowFile = null;
			}
			$('#tempInput').val('').attr('placeholder', '');
		});
	},
	queryVideoPath: function(){
		$.ajax({
			url: "../../xy/video/queryVideoPath.do",
			dataType: "json",
			data: {},
			async: false,
			error: function(XMLHttpRequest, textStatus, errorThrown){
				alert("读取初始化参数失败。" + errorThrown + ':' + textStatus);  // 错误处理
			},
			success: function(data){
				if(data.code == 0){
					video_form.uploadPath = data.uploadPath;
					video_form.videoPath = data.sourcePath;
					video_form.tranPath = data.tranPath;
					video_form.salt = data.uploadPath.substring(data.uploadPath.replace(/\\/g,"/").lastIndexOf("/")+1,data.uploadPath.length);
				} else{
					alert(data.error);
				}
			}
		});

	}

};

$(function(){
	video_form.init();
});

//处理视频上传对话框的关闭事件
function uploadCancel(){
	video_form.videoDialog.close();
}

//处理视频上传对话框的关闭事件
function uploadClose(sessionID){
	video_form.sessionID = sessionID;
	//根据sessionid获取上传的视频然后关闭进行转码
	$.ajax({
		url: "../../xy/video/getUploadFileInfo.do",
		dataType: "json",
		data: {
			"sessionID": sessionID
		},
		async: false,
		error: function(XMLHttpRequest, textStatus, errorThrown){
			alert("读取初始化参数失败。" + errorThrown + ':' + textStatus);  // 错误处理
		},
		success: function(data){
			$("#v_fileName").val(data.fileName);
			$("#v_path").val(data.path);
			$("#v_transPath").val(data.transPath);
			$("#v_time").val(data.time);
		}
	});

	video_form.videoDialog.close();
}

e5_form.event.doCancel = function(){
	window.onbeforeunload = null;

	$("#btnFormSave").disabled = true;
	$("#btnFormCancel").disabled = true;
	$("#btnFormSaveSubmit").disabled = true;

	if(video_form.sessionID != 0){
		$.ajax({
			url: "../../xy/video/VideoCancel.do",
			dataType: "text",
			data: {
				"path": $("#v_path").val()
			},
			async: false,
			error: function(XMLHttpRequest, textStatus, errorThrown){
				alert("读取初始化参数失败。" + errorThrown + ':' + textStatus);  // 错误处理
			},
			success: function(data){
			}
		});
	}
	e5_form.event.beforeExit(); //调after.do
}
