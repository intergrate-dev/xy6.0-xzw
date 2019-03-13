function uploadMoreImages(){
	$("#sudokuUpload").sudokuUpload({
		width : "270px", // 宽度
		height : "", // 宽度
		itemWidth : "80px", // 文件项的宽度
		itemHeight : "80px", // 文件项的高度
		url : "../xy/pic/Upload.do", // 上传文件的路径
		multiple : true, // 是否可以多个文件上传
		del : true, // 是否可以删除文件
		finishDel : false, // 是否在上传文件完成后删除预览
		/* 外部获得的回调接口 */
		onSelect : function(files, allFiles) { // 选择文件的回调方法
		},
		onDelete : function(file, surplusFiles) { // 删除一个文件的回调方法
		},
		onSuccess : function(file) { // 文件上传成功的回调方法
		},
		onFailure : function(file) { // 文件上传失败的回调方法
		},
		onComplete : function(responseInfo) { // 上传完成的回调方法
		}
	});
	// 置weiboType为2-图片
	$("#weiboType").attr("value", 2);
}

function closeUploadMoreImages(){
	$("#sudokuUpload").empty().hide();
	$("#weiboType").attr("value", 1);
	
	SUDOKUFILE.uploadFile = [];
	SUDOKUFILE.curUploadFile = [];
	SUDOKUFILE.fileIndex = 0;
	SUDOKUFILE.fileNum = 0;
}
