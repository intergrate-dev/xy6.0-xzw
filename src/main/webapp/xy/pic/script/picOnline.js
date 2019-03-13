var online_image = {
	//图片库选择：“确定”按钮
	picClose : function(docLibID, docIDs) {
		if (!docIDs) return;
		docIDs = docIDs.split(",");
		
		for (var i = 0; i < docIDs.length; i++) {
			//根据索引图的DocLibID，DocID获取到这组的所有组图
			$.ajax({
				url: "../../xy/pic/getPics.do",
				dataType: "json",
				async: false,
				data : {
					"DocID" : docIDs[i],
					"DocLibID" : docLibID
				},
				error: function (XMLHttpRequest, textStatus, errorThrown) {
					alert(errorThrown + ':' + textStatus);
				},
				success: function (data) {
					for(var a = 0; a < data.length; a++){
						var jsonParam = {
								"picPath":data[a].path,
								"pic":data[a].DocLibID + "," + data[a].DocID,
								"content":data[a].content
							};
						picInfoList.push(jsonParam);
					}
				}
			});
		}
        challs_flash_onCompleteAll(0);
	},
	//图片库选择：“取消”按钮
	picCancel : function() {
		// 解决不同浏览器多次进入方法
		chroIEUploadflag = false;
		// 去掉上传flash层
		setOverlay();
		$("#flash").html('');
		picInfoList = [];
	}
}