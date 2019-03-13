var colDialog = null;
window.onload = function() {
	// 点击确认按钮
	$('input#confirm').click(function(){
		
		var li0 = $('#ul1').children("li");	// 用来获得left和top算出先后顺序
		var div0 = '';
		var jsonParam = '';
		var datas = [];

		for(var a = 0; a < li0.length - 1; a++){

			div0 = $(li0[a]).children("div");
			
			jsonParam = {
					"left":$(li0[a]).css('left').replace('px', ''),// 图片位置
					"top":$(li0[a]).css('top').replace('px', ''),// 图片位置
					"isIndex":$(div0[0]).children("input").attr("checked"),// 是否索引图
					"picPath":$(div0[1]).children("input").val(),// 图片路径
					"content":$(div0[2]).children("textarea").val()// 图片说明
			}
			var pic = $(div0[1]).children("img").attr("pic");
			if (pic) jsonParam.pic = pic;

			datas.push(jsonParam);
		}
	});
	
	// 点击取消按钮
	$('#editCancel').click(function(){
		location.href = "../../e5workspace/after.do?UUID=" + $('#UUID').val();
	});
}

//弹出上传flash页面
function setOverlay(){
	if("visible" == $('#modal-overlay').css('visibility')){
		$('#modal-overlay').css('visibility', 'hidden');
		$('body').css('overflow-x', 'auto');
		$('body').css('overflow-y', 'auto');
	}else{
		$('#modal-overlay').css('visibility', 'visible');
		$('body').css('overflow-x', 'hidden');
		$('body').css('overflow-y', 'hidden');
	}
}

// 解决不同浏览器多次进入方法
var chroIEUploadflag = false;

//点击"上传图片"
function linkliclick(){
	if (chroIEUploadflag) {
		return;
	}
	chroIEUploadflag = true;
	$("#flash").html('');
	var str = '<object classid="clsid:d27cdb6e-ae6d-11cf-96b8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=11,0,0,0" width="900" height="450" id="update" align="middle">'
		+ '<param name="allowFullScreen" value="false" />'
		+ '<param name="allowScriptAccess" value="always" />'
		+ '<param name="movie" value="../pic/update.swf" />'
		+ '<param name="quality" value="high" />'
		+ '<param name="bgcolor" value="#ffffff" />'
		+ '<embed src="../pic/update.swf" quality="high" bgcolor="#ffffff" width="900" height="440" name="update" align="middle" allowScriptAccess="always" allowFullScreen="false" type="application/x-shockwave-flash" pluginspage="http://www.macromedia.com/go/getflashplayer" />'
		+ '</object>';
	$("#flash").append(str);
	// 弹出上传flash层
	setOverlay();
	// 滚动条返回顶部 否则遮盖层遮盖不完全
	document.documentElement.scrollTop = 0;
	// 解决上面那一句chrome不支持问题
	document.body.scrollTop = 0;
}

var pic_group = {
	drawImg : function(fromPage, index, picInfo) {
		var imgPath = "../../xy/image.do?path=" + picInfo.picPath;
		
		weixinEditor.setImage(imgPath);
	},
	addUploadBtn : function() {
	}
}
// 子窗口关闭按钮调用
function preViewCancel() {
	colDialog.close();
}
function setDragInit() {
}

var online_image = {
	//图片库选择：“确定”按钮
	picClose : function(docLibID, docIDs, imgPath) {
		chroIEUploadflag = false;
		setOverlay();
		$("#flash").html('');
		
		imgPath = "../" + imgPath;
		weixinEditor.setImage(imgPath);
	},
	
	//图片库选择：“取消”按钮
	picCancel : function() {
		// 解决不同浏览器多次进入方法
		chroIEUploadflag = false;
		// 去掉上传flash层
		setOverlay();
		
		$("#flash").html('');
	}
}