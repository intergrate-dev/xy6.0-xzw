var nowFile, onQueueFile = null;
var font_form = {
	uploadPath:null,
	savePath:null,
	init: function(){
		font_form.queryFilePath();
		font_form.initUploadPlugin();
        font_form.deleteEvent();
        font_form.getList();
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
				targetPath: font_form.uploadPath
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
            //生成列表
            var name=$.trim($("#font-name").val());
            var size=Math.ceil(file.size/1024)+" KB";
            var path=font_form.savePath + "/" + file.name;
            var str='<tr><td class="font-list-content">'+name+'</td>'+
                '<td class="font-list-content">'+size+'</td>'+
                '<td class="font-list-content">'+path+'</td>'+
                '<td class="font-list-content">'+
                '<input type="button" class="delete" value="删除"/></td>'+
                '</tr>';
            $("#fontList").find("tbody").prepend(str);
            //$(".glyphicon-remove").trigger("click");
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
			nowFile = file;
			$('#' + file.id).find('.progress').fadeOut();
		});
		$("#ctlBtn").click(function(){
            var name=$.trim($("#font-name").val());
            if(!name){
                alert("请先输入字体包名称！");
                return;
            }
            font_form.queryFilePath();
            uploader.option('formData',{
                targetPath: "font.upload.path",
				salt:font_form.salt
            });
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
	queryFilePath: function(){
		$.ajax({
			url: "../../xy/nis/fontFilePath.do",
			async: false,
			error: function(XMLHttpRequest, textStatus, errorThrown){
				alert("读取初始化参数失败。" + errorThrown + ':' + textStatus);  // 错误处理
			},
			dataType:"json",
			success: function(data){
				font_form.uploadPath = data.uploadPath;
				font_form.savePath = data.savePath;
				font_form.salt = data.uploadPath.substring(data.uploadPath.replace(/\\/g,"/").lastIndexOf("/")+1,data.uploadPath.length);
			}
		});

	}
    ,
    getList:function(){
        var fontData=$.trim($("#font").val());
        if(fontData==null ||fontData=="" ||fontData=="null") return;
        fontData =eval("(" + fontData + ")");
        var str="";
        for(var i in fontData.list){
             str = str+ '<tr><td class="font-list-content">'+fontData.list[i].name+'</td>'+
                '<td class="font-list-content">'+fontData.list[i].size+'</td>'+
                '<td class="font-list-content">'+fontData.list[i].url+'</td>'+
                '<td class="font-list-content">'+
                '<input type="button" class="delete" value="删除"/></td>'+
                '</tr>';
        }
        $("#fontList").find("tbody").append(str);
    },
    deleteEvent:function(){
        //删除列表项
        $("#fontList").on("click",".delete",function(){
            $(this).closest("tr").remove();
        })
    }

};
function doInit(){
    font_form.init();
}
function beforeSubmit(){
    var fontData={'list':[]};
    var _tr=$("#fontList").find("tbody").children("tr");
    var _leng=_tr.length;
    var _arr=[];
    for(var i=0;i<_leng;i++){
        var _data={
            'name':_tr.eq(i).find("td").eq(0).text(),
            'size':_tr.eq(i).find("td").eq(1).text(),
            'url':_tr.eq(i).find("td").eq(2).text()
        };
        _arr.push(_data)
    }
    fontData.list=_arr;
    fontData=JSON.stringify(fontData);
    $("#font").val(fontData)
}