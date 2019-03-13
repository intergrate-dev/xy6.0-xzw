
(function(){

function renderUploader(id, ue) {
	var actionUrl = "../../importword.do";
    var uploader = WebUploader.create({
        pick: {
            id: "#" + id,
            multiple: false
        },
        accept: {
            title: "word文档",
            extensions: "doc,docx",
            mimeTypes: "application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        },
        fileSingleSizeLimit: 5120000,
        swf: './third-party/webuploader/Uploader.swf',
        server: actionUrl,
        fileVal: 'file',
        duplicate: true,

        // 强制 flash  采用 URLStream 上传文件, 默认是 fileReference
        forceURLStream: true
    });

    uploader.on('filesQueued', function(files){
    	 if (files && files[0]) {
             var msgContent = files[0] && files[0].ext == 'doc' ? '.doc文档耗时较长，正在解析...':'正在解析...';
             ue.trigger('showmessage', {
                 'id': files[0].id,
                 'type': 'info',
                 'content': msgContent,
                 'keepshow': true
             });
         }
        uploader.upload();
        uploader.disable();
    });

    uploader.on('uploadBeforeSend', function (file, data){
        data['type'] = 'w2b';
    });

    uploader.on('uploadFinished', function(files){
        setTimeout(function () {
            uploader.enable();
        }, 2000);
    });

    uploader.on('all', function(type, files){
    	 switch (type) {
         case 'startUpload':
             /* 添加额外的GET参数 */
        	 var editor = UE.getEditor('editor');
             var params = "DocID="+editor.getOpt("DocID")+"&DocLibID="+editor.getOpt("DocLibID"),
                 url =  UE.utils.formatUrl(actionUrl + (actionUrl.indexOf('?') == -1 ? '?':'&') + 'encode=utf-8&' + params);
             uploader.option('server', url);
             break;
     }
    });
    uploader.on('uploadSuccess', function (file, r) {
    	 ue.trigger('updatemessage', file.id, {
             'type': 'success',
             'content': '解析成功!',
             'timeout': 2000
         });
         ue.setContent(r._raw);
    });
    //错误文件转存
    uploader.on('uploadError', function(file){
    	showErrorMessage(file);
    });
    function showErrorMessage(file){
        ue.trigger('updatemessage', file.id, {
            'type': 'error',
            'content': file.ext == 'doc' ? '文档解析出错，请重试或转成docx格式再上传。':'文档解析出错，请重试。',
            'timeout': 2000
        });
    }
}
//renderUploader('uploadbtn',ue);
})();