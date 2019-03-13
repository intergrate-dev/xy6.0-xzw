/**
 * User: likg
 * Date: 13-01-17
 * crop image.
 */
var imageCrop = {};
(function () {
	var g = $G;
	var jcropobj = {} ;
	var currentOper = 'crop';
    
	//初始化
	imageCrop.init = function () {
		var srcImg = editor.selection.getRange().getClosedNode();
		if (srcImg) {
            setImageCropInfo(srcImg);
        }
	};

	//显示裁剪图片区域
	imageCrop.showCropArea = function(obj) {
		$(obj).parent().find('input').attr('disabled', false);
		$(obj).attr('disabled', true);
		$('#imgPump').hide();
		$('#imgPumpParam').hide();
		$('#imgCrop').show();
		$('#imgCropParam').show();
		
		currentOper = 'crop';
	}
	//显示抽大小图区域
	imageCrop.showPumpArea = function(obj) {
		$(obj).parent().find('input').attr('disabled', false);
		$(obj).attr('disabled', true);
		$('#imgCrop').hide();
		$('#imgCropParam').hide();
		$('#imgPump').show();
		$('#imgPumpParam').show();
		
		imageCrop.changePumpImgSize($('#imgPumpZoomSize'));
		currentOper = 'pump';
	}
	
	//改变抽图大小
	imageCrop.changePumpImgSize = function(obj) {
		var maxWidth = 260;
		var maxHeight = 260;
		var percent = Number($(obj).val());
		var oImgW = Number($('#oImagePumpW').val());
		var oImgH = Number($('#oImagePumpH').val());
		var zoomW = parseInt(oImgW*percent);
		var zoomH = parseInt(oImgH*percent);
		
		if(zoomW<maxWidth && zoomH<maxHeight){
			$('#imgPumpBox').attr('width', zoomW);
			$('#imgPumpBox').attr('height', zoomH);
		}else{
			$('#imgPumpBox').attr('width', $('#imgCropBox').attr('width'));
			$('#imgPumpBox').attr('height', $('#imgCropBox').attr('height'));
		}
		$('#imagePumpW').val(zoomW);
		$('#imagePumpH').val(zoomH);
	}
	
	//手动输入抽图的宽度
	imageCrop.changeImgPumpW = function(obj) {
		var thisValue = $(obj).val();
		var oImgW = Number($('#oImagePumpW').val());
		if(thisValue==null || thisValue=='' || isNaN(thisValue)){
			$(obj).val('0');
		}else if(Number(thisValue) > oImgW){
			$(obj).val(oImgW);
		}
		imageCrop.drawPreviewPumpImg();
	}
	//手动输入抽图的高度
	imageCrop.changeImgPumpH = function(obj) {
		var thisValue = $(obj).val();
		var oImgH = Number($('#oImagePumpH').val());
		
		if(thisValue==null || thisValue=='' || isNaN(thisValue)){
			$(obj).val('0');
			return ;
		}else if(Number(thisValue) > oImgH){
			$(obj).val(oImgH);
		}
		imageCrop.drawPreviewPumpImg();
	}
	//重新调整预览图的大小
	imageCrop.drawPreviewPumpImg = function() {
		var maxWidth = 760;
		var maxHeight =560;
		var zoomW = parseInt($('#imagePumpW').val());
		var zoomH = parseInt($('#imagePumpH').val());
		
		if(zoomW<maxWidth && zoomH<maxHeight){
			$('#imgPumpBox').attr('width', zoomW);
			$('#imgPumpBox').attr('height', zoomH);
		}else{
			$('#imgPumpBox').attr('width', $('#imgCropBox').attr('width'));
			$('#imgPumpBox').attr('height', $('#imgCropBox').attr('height'));
		}
	}
	
	/**
	 * 绑定确认按钮
	 */
	imageCrop.cropImgOk = function() {
		if(currentOper == 'crop'){
			imgCrop();
		}else if(currentOper == 'pump'){
			imgPump();
		}
	}
	
	/**
	 * 绑定取消按钮
	 */
	imageCrop.cropImgCancle = function() {
    	  //dialog.close();
		imageCrop.cropImgCancle();
		return ;
	}
     
	/**
	 * 设置裁剪图片的信息
	 */
	function setImageCropInfo(img) {
		//获取图片路径
		if (!img.getAttribute("src") || !img.src) return;
		var wordImgFlag = img.getAttribute("word_img");
		var imagePath = wordImgFlag ? wordImgFlag.replace("&amp;", "&") : (img.getAttribute('data_ue_src') || img.getAttribute("src", 2).replace("&amp;", "&"));
		$('#imageCropPath').val(imagePath);
		$('#imgTitle').val(img.title || "");
		
		//网络图片不能裁剪
		//var reg = new RegExp("^/data");
		//if(!reg.test(imagePath)) return false;
		
		//加载图片
		var img = new Image();
		img.onload = function(){
			//设置显示原图大小
			g("oImagePumpW").value = this.width;
			g("oImagePumpH").value = this.height;
			showPreviewCropImage(this); //显示图片
			g("imageW").value = this.width;
			g("imageH").value = this.height;
		}
		img.src = imagePath;
		g("imgCropBox").src = imagePath;
	}
      
	/**
     * 把裁剪后的图片保存到服务器，并在编辑器中显示
     */
	function imgCrop() {
		var param = {};
		var selectorW = $('#selectorW').val();
		var selectorH = $('#selectorH').val();
		if(selectorW==0 || selectorH==0){
			alert('请选择裁剪区域！');
			return;
		}
		param.articleId = editor.currentArticleId;
		param.selectorW = selectorW;
		param.selectorH = selectorH;
		param.selectorX = $('#selectorX').val();
		param.selectorY = $('#selectorY').val();
		param.imageW = $('#imageW').val();
		param.imageH = $('#imageH').val();
		param.imagePath = $('#imageCropPath').val();
		param.imgTitle = $('#imgTitle').val();
		$.get("../../ueditor/jsp/cropImage.jsp", param, function(json){
			var result = eval("(" + json + ")");
			//插入裁剪后的图片
			var imgObj = {};
			imgObj.src = result.imgPath;
			imgObj.data_ue_src = result.imgPath;
			imgObj.width = selectorW;
			imgObj.height = selectorH;
			imgObj.style = "width:" + selectorW + "px;height:" + selectorH + "px;";
			insertImage(imgObj);
			
			//关闭窗口
			imageCrop.cropImgCancle();
		});
	}
     
    /**
      * 把抽取的小图片保存到服务器，并在编辑器中显示
      */
 	function imgPump() {
 		var param = {};
 		var imagePumpW = $('#imagePumpW').val();
 		var imagePumpH = $('#imagePumpH').val();
 		if(imagePumpW=='' || isNaN(imagePumpW) || Number(imagePumpW)==0 || imagePumpH=='' || isNaN(imagePumpH) || Number(imagePumpH)==0){
 			alert('请输入有效的图片大小！');
 			return;
 		}
 		param.articleId = editor.currentArticleId;
 		param.imagePumpW = imagePumpW;
 		param.imagePumpH = imagePumpH;
 		param.imagePath = $('#imageCropPath').val();
 		param.imgTitle = $('#imgTitle').val();
 		$.get("../../ueditor/jsp/pumpImage.jsp", param, function(json){
 			var result = eval("(" + json + ")");
 			//插入裁剪后的图片
 			var imgObj = {};
 			imgObj.src = result.imgPath;
 			imgObj.data_ue_src = result.imgPath;
 			imgObj.width = imagePumpW;
 			imgObj.height = imagePumpH;
 			imgObj.border = 0;
 			imgObj.style = "width:" + imagePumpW + "px;height:" + imagePumpH + "px;";
 			
 			var opt = {};
 	    	opt.href = param.imagePath;
 	    	opt.target = "_blank";
 	    	insertLinkImage(imgObj, opt);
 			
 			//关闭窗口
 			imageCrop.cropImgCancle();
 		});
 	}
     
    //插入图片
	function insertImage(imgObjs) {
		editor.fireEvent('beforeInsertImage', imgObjs);
		editor.execCommand("insertImage", imgObjs);
	}
    //插入带链接的图片
    function insertLinkImage(imgObjs, linkOpt) {
    	editor.fireEvent('beforeInsertImage', imgObjs);
    	editor.execCommand("insertImage", imgObjs);
    	editor.execCommand("link", linkOpt);
    }
	
	/**
     * 图片缩放
     * @param img
     * @param maxW 最大宽度
     * @param maxH 最大高度
     */
	function scaleCropImg(img, maxW, maxH) {
        var width = 0, height = 0, percent, ow = img.width, oh = img.height;
        if (ow > maxW || oh > maxH) {
            if (ow/oh >= maxW/maxH) {
                if (width = ow - maxW) {
                    percent = (width / ow).toFixed(2);
                    img.height = oh - oh * percent;
                    img.width = maxW;
                }
            } else {
                if (height = oh - maxH) {
                    percent = (height / oh).toFixed(2);
                    img.width = ow - ow * percent;
                    img.height = maxH;
                }
            }
        }
    }
      
	/**
	  * 将img显示在裁剪预览框
	  * @param img
	  */
	function showPreviewCropImage(img) {
		var maxWidth = 760;
		var maxHeight = 560;
		scaleCropImg(img, maxWidth, maxHeight);
		g("imgCropBoxDiv").innerHTML = '<img border=1 id="imgCropBox" src="' + img.src + '" width="' + img.width + '" height="' + img.height + '" />';
		g("imgPumpBoxDiv").innerHTML = '<img border=1 id="imgPumpBox" src="' + img.src + '" width="' + img.width + '" height="' + img.height + '" />';
  		
		jcropobj = $.Jcrop('#imgCropBox',{
			onChange: showCoords,
			onSelect: showCoords
		});
	}
	  
	//刷新裁剪区域
	imageCrop.refreshSelect = function (){
		var imageW = Number($('#imageW').val());
		var imageH = Number($('#imageH').val());
		var selectorX = $('#selectorX').val();
		var selectorY = $('#selectorY').val();
		var inputW = $('#selectorWShow').val();
		var inputH = $('#selectorHShow').val();
		var x1=0, y1=0;
		
		if(inputW==null || inputW=='' || inputH==null || inputH=='' || isNaN(inputW) || isNaN(inputH)){
			return ;
		}
		inputW = Number(inputW);
		inputH = Number(inputH);
		if(selectorX!=null && selectorX!='' && Number(selectorX)+inputW<=imageW){
			x1 = Number(selectorX);
		}
		if(selectorY!=null && selectorY!='' && Number(selectorY)+inputH<=imageH){
			y1 = Number(selectorY);
		}
		jcropobj.setSelect( [x1, y1, x1+inputW, y1+inputH] );
		e.stopPropagation();
		e.preventDefault();
		return false;
	}

	/**
	 * 回填裁剪信息
	 * @param c
	 */
	function showCoords(c) {
		jQuery('#selectorX').val(c.x);
		jQuery('#selectorY').val(c.y);
		jQuery('#selectorW').val(c.w);
		jQuery('#selectorH').val(c.h);
		jQuery('#selectorWShow').val(c.w);
		jQuery('#selectorHShow').val(c.h);
	};

})();
