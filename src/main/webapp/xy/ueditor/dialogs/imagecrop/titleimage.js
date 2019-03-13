var orignalWidth, orignalHeight, resizedWidth, resizedHeight, widthRadio, heightRadio;
var smallAR, midAR, bigAR;//宽高比
var _smallRadio=smallRadio, _midRadio=midRadio, _bigRadio=bigRadio;//尺寸，比如：123*123  单位是px.
//是否使用原图
var useOrignal = true;
//使用原图按钮
var isuseOrignal = false;
/**
 * 初始化的时候，先隐藏预览图，以免当没有预览图的时候，破坏样式
 * 注意：通过本地上传并设置标题图是先上传，再显示修改；如果想先使用预览，修改过后再上传可以参考svn:3057的代码
 */
$(function(){
	window.parent.channel_frame.resetImageProperties();
	//初始化预览图片的样式
	initPreviewDivStyle();
	//开始的时候隐藏所有的预览div，后面再判断需要哪个，就用哪一个
	// $("#previewImgDiv .preview-pane").hide();

	//初始化上传本地文件按钮
	$("#localFile").change(function(){
	//当浏览器是 ie9以下时，使用先上传，再显示；如果是其他浏览器，直接显示预览图片，等到用户提交的时候，再上传图片
		$("#mainDiv").hide();
		$("#tabUl").hide();
		$("#detailDiv").hide();
		$("#waitingDiv").show();
		$("#picForm").submit();
	});
	//判断是否有底图
	if(imgName!="undefined" && imgName!=""){
		$("#target").show();
		$("#useOrignalBtn").attr("disabled", false);
		$("#submitBtn").attr("disabled", false);
		//查看是否有原图。如果有的话就显示原图；如果没有就显示正常的标题图；
		_imgName = imgName.substr(imgName.lastIndexOf("/")+1);
		if(_imgName.indexOf("t")==0 && _imgName.indexOf("_")==2){
			//用_imgPath记录当前标题图的路径；当发现原图没有的时候，仍然用当前标题图的路径
			_imgPath = imgPath;
			imgPath = imgPath.replace("t0_","").replace("t1_","").replace("t2_","");
			//url路径
			imgPath = imgPath.substr(0, imgPath.lastIndexOf("/")+1) + _imgName.substr(_imgName.lastIndexOf(")")+1);
			var _image = new Image();
			//如果报错的话，仍然用当前标题图的路径
			$(_image).error(function(){
				imgPath = _imgPath;
				//不使用原图
				useOrignal = false;
			});
			_image.src = imgPath;
			//需要等图片完全加载完，才会触发error事件，所以只能等error触发完，再调预览图初始化
			imgLoad(_image,function(){
				//给每一个预览赋底图
				$(".preview-pane img").each(function(){
					$(this).attr("src", imgPath);
				});
				//显示出来
				$("#previewImgDiv .preview-pane").show();
				//初始化预览图
				setImage();
			});
		}else{
			//给每一个预览赋底图
			$(".preview-pane img").each(function(){
				$(this).attr("src",imgPath );
			});
			//显示出来
			$("#previewImgDiv .preview-pane").show();
			//初始化预览图
			setImage();
		}
	}

    //创建cookie
    function setCookie(name, value, expires) {
        var exp = new Date();
        exp.setTime(exp.getTime() + 24 * 60 * 60 * 1000);
        document.cookie = name + "=" + escape(value) + ";expires=" + exp.toGMTString();
    }
    //获取cookie
    function getCookie(name){
        var regExp = new RegExp("(^| )" + name + "=([^;]*)(;|$)");
        var arr = document.cookie.match(regExp);
        if (arr == null) {
            return null;
        }
        return unescape(arr[2]);
    }
	//创建触发cookie函数
	function triggerSetcookie(_type){
		//点击确定按钮时触发
		$("#submitBtn").on("click",function(){
			var select_value = $("input[name='img_"+_type+"']:checked").val();
			if(select_value == "自定义"){
				setCookie('preview-word_'+_type, select_value);
				var _text = $("#preview-word-select-"+_type).text();
				select_value = _text.substring(_text.lastIndexOf("(") +1 , _text.lastIndexOf(")") );
			}
			var radioArr = select_value?select_value.split("*"):"".split("*");
			var select_radio = radioArr[0]/radioArr[1];
			setCookie("select_radio_"+_type, select_radio);
			setCookie('preview-word-select_'+_type, select_value);
			setCookie('preview-container_small', $("#pp-small").width());
			setCookie('preview-container_mid', $("#pp-mid").width());
			setCookie('preview-container_big', $("#pp-big").width());
		});
	}
	// triggerSetcookie("small");
	// triggerSetcookie("mid");
	// triggerSetcookie("big");
	//保存cookie的值
	function saveCookieValue(_type){
		var customized = false,
			_select = getCookie("preview-word_"+_type),
			selectValue = getCookie("preview-word-select_"+_type),
			imgBorderWidth = getCookie("preview-container_"+_type);
		if(_select != null && _select.indexOf("自定义")!=-1) {
			customized = true;
		}
		if(customized && selectValue != null){
			var selectArr = selectValue.split("*");
			$("#preview-word-select-" + _type).text("(" + selectValue + ")");
			$("#preview-word-" + _type + "-select_2").attr("checked", "checked");
			if(_type == "small"){
				// smallRadio = selectValue;
				// smallAR = selectRadio;
				$("#preview-pane-small-input_1").val(selectArr[0]);
				$("#preview-pane-small-input_2").val(selectArr[1]);
			} else if(_type == "mid"){
				// midRadio = selectValue;
				// midAR = selectRadio;
				$("#preview-pane-mid-input_1").val(selectArr[0]);
				$("#preview-pane-mid-input_2").val(selectArr[1]);
			}else if(_type == "big"){
				// bigRadio = selectValue;
				// bigAR = selectRadio;
				$("#preview-pane-big-input_1").val(selectArr[0]);
				$("#preview-pane-big-input_2").val(selectArr[1]);
			}
		}
		if(imgBorderWidth != null) {
			if (_type=="small" && $("#pp-small")) {
				$("#pp-small").width(imgBorderWidth).height(126);
			}
			if(_type=="mid" && $("#pp-mid")){
				$("#pp-mid").width(imgBorderWidth).height(126);
			}
			if(_type=="big" && $("#pp-big")){
				$("#pp-big").width(imgBorderWidth).height(126);
			}
		}
	}
	// saveCookieValue("small");
	// saveCookieValue("mid");
	// saveCookieValue("big");
	//获取select值
	// var selectedIndex = getCookie("preview-word-select_1");
	// var selectedIndex2 = getCookie("preview-word-select_2");
	// var selectedIndex3 = getCookie("preview-word-select_3");
	// if(selectedIndex != null ) {
	// 	document.getElementById("preview-word-select_1").selectedIndex = selectedIndex;
	// }
	// if(selectedIndex2 != null ) {
	// 	document.getElementById("preview-word-select_2").selectedIndex = selectedIndex2;
	// }
	// if(selectedIndex3 != null ) {
	// 	document.getElementById("preview-word-select_3").selectedIndex = selectedIndex3;
	// }
	// //获取input输入框值
	// var selectedIndex_text= getCookie("preview-word-select_1_text");
	// var selectedIndex2_text= getCookie("preview-word-select_2_text");
	// var selectedIndex3_text = getCookie("preview-word-select_3_text");
	// if(selectedIndex_text != null && selectedIndex_text.indexOf("自定义")!=-1) {
	// 	$("#preview-word-select_1").children().eq(3).text(selectedIndex_text);
	// }
	// if(selectedIndex2_text != null && selectedIndex2_text.indexOf("自定义")!=-1 ) {
	// 	$("#preview-word-select_2").children().eq(3).text(selectedIndex2_text);
	// }
	// if(selectedIndex3_text != null && selectedIndex3_text.indexOf("自定义")!=-1 ) {
	// 	$("#preview-word-select_3").children().eq(3).text(selectedIndex3_text);
	// }
	// //获取边框的宽度
	// var  scaleBorderWidth = getCookie("preview-container");
	// var  scaleBorderWidth1 = getCookie("preview-container1");
	// var  scaleBorderWidth2 = getCookie("preview-container2");
    //
	// if(scaleBorderWidth !=null){
	// 	$("#pp-small").width(scaleBorderWidth).height(126);
	// }
	// if(scaleBorderWidth1 != null){
	// 	$("#pp-mid").width(scaleBorderWidth1).height(126);
	// }
	// if(scaleBorderWidth2 != null){
	// 	$("#pp-big").width(scaleBorderWidth2).height(126);
	// }

});



/**
 * 初始化 预览图的边框样式 - 即根据后台的配置，设定预览图的比例
 */
function initPreviewDivStyle(){
	//获取预览图的高度（高度设为固定以免影响样式，至于宽度，已经预留出来很多了）
	var height = $(".preview-container").css("height").replace("px","");
	//根据后台配置的参数来确定预览图比例（高度固定，重新计算宽度）
	var ss = _smallRadio.split("*");
	var ms = _midRadio.split("*");
	var bs = _bigRadio.split("*");
	smallAR = ss[0]/ss[1];
	midAR = ms[0]/ms[1];
	bigAR = bs[0]/bs[1];
	$(".preview-pane div[id$=small]").css({"width": smallAR*parseInt(height) +"px", "height": height +"px"});
	$(".preview-pane div[id$=mid]").css({"width": midAR*parseInt(height) +"px", "height": height +"px"});
	$(".preview-pane div[id$=big]").css({"width": bigAR*parseInt(height) +"px", "height": height +"px"});
	$(".preview-pane").find("img").css({"margin-left":"1000px"});
}

//设置预览图
function setImage(){
	//定义一个img对象，主要是为了获取原图的宽高左侧图片
	var img = new Image();
	img.onload = function(){
		//设置显示原图大小
		orignalWidth = this.width;
		orignalHeight = this.height;
		//对原图进行缩放以适应容器的大小
		showPreviewCropImage(this); //显示的左侧图片
		var imgSrcLoad=this.src;
		window.curentOrignalImgSrc=imgSrcLoad.slice(imgSrcLoad.indexOf(";")+1);
		sessionStorage.setItem("useOrignalImgOnly",false);
        sessionStorage.setItem("useOrignalImgOnlySmall",false);
        sessionStorage.setItem("useOrignalImgOnlyMid",false);
        sessionStorage.setItem("useOrignalImgOnlyBig",false);
		//改变大小后的宽高
		resizedWidth = this.width;
		resizedHeight = this.height;
		//获得缩放的比例
		widthRadio = orignalWidth/resizedWidth;
		heightRadio = orignalHeight/resizedHeight;
		//在页面上显示原图的宽高
		$("#orignalW").html(orignalWidth+" px");
		$("#orignalH").html(orignalHeight+" px");

		//初始化预览图 - 确定图片完全加载完之后，对图片进行初始化
		imgLoad(this,function(){
			initJcrop(
			$('#preview-pane-small .preview-container'),
			$('#preview-pane-mid .preview-container'),
			$('#preview-pane-big .preview-container'),
			"target"
			);
			//如果图片已经被自定义了尺寸大小，则按自定义的显示
			if(smallResize!=null && ""!=smallResize && smallResize != _smallRadio){
				_smallRadio = smallResize;
				showSetting(_smallRadio, "small");
			}
			if(midResize!=null && ""!=midResize && midResize != _midRadio){
				_midRadio = midResize;
				showSetting(_midRadio, "mid");
			}
			if(bigResize!=null && ""!=bigResize && bigResize != _bigRadio){
				_bigRadio = bigResize;
				showSetting(_bigRadio, "big");
			}

			//点击第一个
			// var _itype = $("[class=preview-pane]:first").attr("itype");
			// chooseCropImage(_itype);
			var _itype = $("[class=preview-pane]:last").attr("itype");
			if(_imgPath){
				_itype = _imgPath.indexOf("t0_")!=-1 ? "small" : _imgPath.indexOf("t1_")!=-1? "mid" : "big";
			}
			//点击选中的预览图
			chooseCropImage(_itype);
			if(useOrignal){
				var _r = _imgName.substring(_imgName.lastIndexOf("(") +1 , _imgName.lastIndexOf(")") );
				if(_r && $.trim(_r)!=""){
					var _ra = _r.split("X");
					animateImg(_ra);
				}
			}
		})
	}
	img.src = imgPath;
}
function animateImg(_ra){
	var jcropTimer = setInterval(function(){
		if(jcrop_api){
			jcrop_api.animateTo([ parseInt(_ra[0]) , parseInt(_ra[1]) , parseInt(_ra[2]) , parseInt(_ra[3]) ]);
			clearInterval(jcropTimer);
		}
	}, 50);

}
//当图片加载完之后，回调方法
function imgLoad(img, callback) {
    var timer = setInterval(function() {
        if (img.complete) {
            callback(img)
            clearInterval(timer)
        }
    }, 50)
}
//显示预览图
function showPreviewCropImage(img) {
	var maxWidth = cropImageMaxWidth;
	var maxHeight = cropImageMaxHeight;
	//缩放图片
	scaleCropImg(img, maxWidth, maxHeight);
	$("#imgContainDiv").html('<img border=1 id="target" src="' + img.src + '" width="' + img.width + '" height="' + img.height + '" />');
}
//缩放图片
function scaleCropImg(img, maxW, maxH) {
    var width = 0, height = 0, percent, orignalWidth = img.width, orignalHeight = img.height;
    if (orignalWidth > maxW || orignalHeight > maxH) {
        if (orignalWidth/orignalHeight >= maxW/maxH) {
            if (width = orignalWidth - maxW) {
                percent = (width / orignalWidth).toFixed(2);
                img.height = orignalHeight - orignalHeight * percent;
                img.width = maxW;
            }
        } else {
            if (height = orignalHeight - maxH) {
                percent = (height / orignalHeight).toFixed(2);
                img.width = orignalWidth - orignalWidth * percent;
                img.height = maxH;
            }
        }
    }
}
//初始化切图
var jcrop_api, smallC, midC, bigC;
function initJcrop( $spcnt, $mpcnt, $bpcnt, target ){
	//各个预览图的大小
    var boundx, boundy,
        sxsize = $spcnt.width(),
        sysize = $spcnt.height(),
        mxsize = $mpcnt.width(),
        mysize = $mpcnt.height(),
        bxsize = $bpcnt.width(),
        bysize = $bpcnt.height()
        ;

    //初始化第一张预览图的比例
    // _tab = $("[class=previw-pane]:first").attr("itype");
	// var _iar = _tab =="small"? sxsize/sysize : _tab =="mid"? mxsize/mysize : bxsize/bysize;
	//初始化被e选择的预览图的比例
	var _tab, _iar;
	if(_imgPath){
		_tab = _imgPath.indexOf("t0_") != -1 ? "small" : _imgPath.indexOf("t1_") != -1 ? "mid" : "big";
		_iar = _tab == "small" ? sxsize / sysize : _tab == "mid" ? mxsize / mysize : bxsize / bysize;
	}
    $('#'+target).Jcrop({
      onChange: function(c){
		  boundx, boundy,
		  sxsize = $spcnt.width(),
		  sysize = $spcnt.height(),
		  mxsize = $mpcnt.width(),
		  mysize = $mpcnt.height(),
		  bxsize = $bpcnt.width(),
		  bysize = $bpcnt.height();
	      if (parseInt(c.w) > 0){
	      	//选择器选图了之后，改变预览图的显示
	      	var srx = sxsize / c.w;
	        var sry = sysize / c.h;

	        var mrx = mxsize / c.w;
	        var mry = mysize / c.h;

	        var brx = bxsize / c.w;
	        var bry = bysize / c.h;
	        if(now == "small"){
	        	$('#preview-pane-small .preview-container img').css({
		          width: Math.round(srx * boundx) + 'px',
		          height: Math.round(sry * boundy) + 'px',
		          marginLeft: '-' + Math.round(srx * c.x) + 'px',
		          marginTop: '-' + Math.round(sry * c.y) + 'px'
		        });
		        smallC=c;
	        }

	        if(now == "mid"){
	        	$('#preview-pane-mid .preview-container img').css({
		          width: Math.round(mrx * boundx) + 'px',
		          height: Math.round(mry * boundy) + 'px',
		          marginLeft: '-' + Math.round(mrx * c.x) + 'px',
		          marginTop: '-' + Math.round(mry * c.y) + 'px'
		        });
		        midC=c;

	        }

	        if(now == "big"){
	        	$('#preview-pane-big .preview-container img').css({
		          width: Math.round(brx * boundx) + 'px',
		          height: Math.round(bry * boundy) + 'px',
		          marginLeft: '-' + Math.round(brx * c.x) + 'px',
		          marginTop: '-' + Math.round(bry * c.y) + 'px'
		        });
		        bigC=c;
	        }
	      }
	      //显示选择器的大小
	      $("#selectorW").html(Math.round(c.w*widthRadio)+" px");
	      $("#selectorH").html(Math.round(c.h*heightRadio)+" px");
	      window.parent.channel_frame.setImageProperties(smallC, midC, bigC, $("#" + target).attr("src"), widthRadio, heightRadio, _smallRadio, _midRadio, _bigRadio);
		  //使用原图
		  if(isuseOrignal){
			  originalImgReturn(imgPath);
			  isuseOrignal = false;
		  }
	    },
      onSelect: function(c){
	      if (parseInt(c.w) > 0){
	      	var srx = sxsize / c.w;
	        var sry = sysize / c.h;

	        var mrx = mxsize / c.w;
	        var mry = mysize / c.h;

	        var brx = bxsize / c.w;
	        var bry = bysize / c.h;

	        if(now == "small"){
	        	$('#preview-pane-small .preview-container img').css({
		          width: Math.round(srx * boundx) + 'px',
		          height: Math.round(sry * boundy) + 'px',
		          marginLeft: '-' + Math.round(srx * c.x) + 'px',
		          marginTop: '-' + Math.round(sry * c.y) + 'px'
		        });
	        }

	        if(now == "mid"){
	        	$('#preview-pane-mid .preview-container img').css({
		          width: Math.round(mrx * boundx) + 'px',
		          height: Math.round(mry * boundy) + 'px',
		          marginLeft: '-' + Math.round(mrx * c.x) + 'px',
		          marginTop: '-' + Math.round(mry * c.y) + 'px'
		        });
	        }

	        if(now == "big"){
	        	$('#preview-pane-big .preview-container img').css({
		          width: Math.round(brx * boundx) + 'px',
		          height: Math.round(bry * boundy) + 'px',
		          marginLeft: '-' + Math.round(brx * c.x) + 'px',
		          marginTop: '-' + Math.round(bry * c.y) + 'px'
		        });
	        }
		      window.parent.channel_frame.setImageProperties(smallC, midC, bigC, $("#" + target).attr("src"), widthRadio, heightRadio, _smallRadio, _midRadio, _bigRadio);
	      }
	    }
      ,aspectRatio: _iar	//第一个图的比例

    },function(){
      //获得图片的大小
      var bounds = this.getBounds();
      boundx = bounds[0];
      boundy = bounds[1];
      //获得切图对象
      jcrop_api = this;
		jcrop_api.setOptions({ aspectRatio: (now=="small"?smallAR:now=="mid"?midAR:bigAR) });
    });
  }

//点击预览图，设置当前预览图
var now = "";
var isFirst = true;
function chooseCropImage(tab){

	// if(tab=="small"){
	// 	$("#preview-word-small-select_1").prop("checked",true);
	// }else if(tab=="mid"){
     //    $("#preview-word-mid-select_1").prop("checked",true);
	// }else if(tab=="big"){
     //    $("#preview-word-big-select_1").prop("checked",true);
	// }
	//防止点击多次
	if(now != tab){

	//选择预览图，添加相应的样式
		$(".preview-pane").removeClass("chosen");
		now = tab;
		$('#preview-pane-'+now).addClass("chosen");

		//获得选择器坐标点
		var _c = now=="small"?smallC: now=="mid"?midC:bigC;
		//如果没有坐标，说明是第一次，释放选择器
		if(_c){
			//有坐标的话，直接指向这个地方
			jcrop_api.animateTo([_c.x,_c.y,_c.x2,_c.y2]);
		}else{
			//如果没有坐标，释放选择器
			if(jcrop_api)
				jcrop_api.release();
		}
		//重新设置比例
		if(jcrop_api)
			jcrop_api.setOptions({ aspectRatio: (now=="small"?smallAR:now=="mid"?midAR:bigAR) });
	}
}
// //使用原图调用
// function chooseCropImageOnly(tab){
//
//     //防止点击多次
//     if(now != tab){
//
//         //选择预览图，添加相应的样式
//         $(".preview-pane").removeClass("chosen");
//         now = tab;
//         $('#preview-pane-'+now).addClass("chosen");
//
//         //获得选择器坐标点
//         var _c = now=="small"?smallC: now=="mid"?midC:bigC;
//         //如果没有坐标，说明是第一次，释放选择器
//         if(_c){
//             //有坐标的话，直接指向这个地方
//             jcrop_api.animateTo([_c.x,_c.y,_c.x2,_c.y2]);
//         }else{
//             //如果没有坐标，释放选择器
//             if(jcrop_api)
//                 jcrop_api.release();
//         }
//         //重新设置比例
//         if(jcrop_api)
//             jcrop_api.setOptions({ aspectRatio: (now=="small"?smallAR:now=="mid"?midAR:bigAR) });
//     }
// }
//修改标题图的时候，显示图片之前的设置
function showSetting(selectValue, _type){
	var selectArr = selectValue.split("*");
	$("#preview-word-select-" + _type).text("(" + selectValue + ")");
	$("#preview-word-" + _type + "-select_2").attr("checked", "checked");
	if(_type == "small"){
		$("#preview-pane-small-input_1").val(selectArr[0]);
		$("#preview-pane-small-input_2").val(selectArr[1]);
	} else if(_type == "mid"){
		$("#preview-pane-mid-input_1").val(selectArr[0]);
		$("#preview-pane-mid-input_2").val(selectArr[1]);
	}else if(_type == "big"){
		$("#preview-pane-big-input_1").val(selectArr[0]);
		$("#preview-pane-big-input_2").val(selectArr[1]);
	}
	synScale(_type, selectValue);
}
//自定义标题图比例还是默认比例
$("input[name='img_small']").on("click",function(){
	ShowOrHide("small");
});
$("input[name='img_mid']").on("click",function(){
	ShowOrHide("mid");
});
$("input[name='img_big']").on("click",function(){
	ShowOrHide("big");
});

//如果选择器选择的是"自定义"，则显示input和button，否则弹出
function ShowOrHide(_type){
	var _value = $("input[name='img_"+_type+"']:checked").val();
	if((_value == "自定义")){
		$("#preview-word-assign_"+_type).show();
		$("#preview-word-select-"+_type).hide();
		var _text = $("#preview-word-select-"+_type).text();
		_value = _text.substring(_text.lastIndexOf("(") +1 , _text.lastIndexOf(")") );
        if(_type=="small"){
            sessionStorage.setItem("useOrignalImgOnlySmall",false);
        }else if(_type=="mid"){
            sessionStorage.setItem("useOrignalImgOnlyMid",false);
        }else if(_type=="big"){
            sessionStorage.setItem("useOrignalImgOnlyBig",false);
        }
	}else if((_value == "使用原图")){
		sessionStorage.setItem("useOrignalImgOnly",true);
        if(_type=="small"){
            sessionStorage.setItem("useOrignalImgOnlySmall",true);
        }else if(_type=="mid"){
            sessionStorage.setItem("useOrignalImgOnlyMid",true);
        }else if(_type=="big"){
            sessionStorage.setItem("useOrignalImgOnlyBig",true);
        }
        useOrignalImgOnly(_type);

	}else{
		$("#preview-word-assign_"+_type).hide();
		$("#preview-word-select-"+_type).show();
        if(_type=="small"){
            sessionStorage.setItem("useOrignalImgOnlySmall",false);
        }else if(_type=="mid"){
            sessionStorage.setItem("useOrignalImgOnlyMid",false);
        }else if(_type=="big"){
            sessionStorage.setItem("useOrignalImgOnlyBig",false);
        }
	}
	synScale(_type, _value);
}
//图片框跟随单选框（自定义或者默认）的选择的改变而改变
function synScale(_type, _value){
	var userWidth ,userHeight, radio, userRadio;
	radio = _value.split("*");
	userWidth = radio[0];
	userHeight = radio[1];
	if(userWidth && userHeight){
		userRadio = userWidth/userHeight;
		if(_type.indexOf("small") != -1){
			var _imgBorder = $("#pp-small");
			smallAR = userRadio;
			_smallRadio = _value;
		}else if(_type.indexOf("mid") != -1){
			var _imgBorder = $("#pp-mid");
			midAR = userRadio;
			_midRadio = _value;
		}else if(_type.indexOf("big") != -1){
			var _imgBorder = $("#pp-big");
			bigAR = userRadio;
			_bigRadio = _value;
		}
		var userScaleWidth = (userWidth * 126) / userHeight;
		//_imgBorder.width(userScaleWidth).height(126);
		if (!jcrop_api) return;
		//预览图改变的同时选择预览图,也改变切图选框的大小,并且左侧的选择框取消选中状态
		jcrop_api.setOptions({ aspectRatio: (now=="small" ? smallAR : now=="mid"?midAR:bigAR) });


        chooseCropImage(_type);

		jcrop_api.release();
	}
}

//	用于把输入框的值传入单选框按钮，并且隐藏输入框和确定按钮
function btn_assign(_this, _type, radio){
	$("#preview-word-select-"+_type).text("("+radio+")");
	$("#preview-word-select-"+_type).show();
	_this.parent().hide();
}
//	自定义标题图比例时规范输入内容
$("input.preview-pane-input").on("change",function(){
	var inputValue = $(this).val();
	//判断输入的是不是数字，否则弹出警告框
	if(isNaN(inputValue) || inputValue <= 0){
		alert("请输入大于零的数字！");
		$(this).focus();
	}
});
//	自定义比例的确定按钮的onclick事件 提交自定义的比例
function setImgRadio(_type){
	var _this = $("#preview-pane-btn_"+_type);
	var input_width = $("#preview-pane-"+_type+"-input_1").val();
	var input_height = $("#preview-pane-"+_type+"-input_2").val();
	var w_h_radio = input_width/input_height;
	//宽高要正数 且宽高比最大不能超过2.8
	if(input_height && input_width){
		if(w_h_radio <= 2.8){
			var radio = input_width + "*" + input_height;

            //数组去重排序(可优化)
            var temp = [];
            function uniq(array){
                temp = [];
                var index = [];
                var l = array.length;
                for(var i = 0; i < l; i++) {
                    for(var j = i + 1; j < l; j++){
                        if (array[i] === array[j]){
                            i++;
                            j = i;
                        }
                    }
                    temp.push(array[i]);
                    index.push(i);
                }
                return temp;
            }
            if(!localStorage.getItem("optionJion")){
                var optionObj = {
                    small: [],
                    mid: [],
                    big: []
                };
                localStorage.setItem('optionJion',JSON.stringify(optionObj));
            }else{

            }
            //直接去获取目前local的值
            var obj2 = JSON.parse(localStorage.getItem("optionJion"));

            if(_type == 'small'){
                obj2.small.push(input_width +'*'+ input_height);
                if( obj2.small.length > 0 ){
                    //判断里面是否有重复的
                    uniq(obj2.small)
                    if(temp.length > 5){
                        //删除第一个 保留最后一个
                        temp.shift();
                    }
                };
                obj2.small = temp;
                localStorage.setItem('optionJion',JSON.stringify(obj2));
                againOption('small');
                // $("#smallImgselect").append("<option value='"+ input_width +'*'+ input_height +"'>"+ input_width +'*'+ input_height +"</option>");
            }else if (_type == 'mid'){
                obj2.mid.push(input_width +'*'+ input_height);
                if( obj2.mid.length > 0 ){
                    //判断里面是否有重复的
                    uniq(obj2.mid)
                    if(temp.length > 5){
                        //删除第一个 保留最后一个
                        temp.shift();
                    }
                };
                obj2.mid = temp;
                localStorage.setItem('optionJion',JSON.stringify(obj2));
                againOption('mid');
                // $("#midImgselect").append("<option value='"+ input_width +'*'+ input_height +"'>"+ input_width +'*'+ input_height +"</option>");
            }else{
                obj2.big.push(input_width +'*'+ input_height);
                if( obj2.big.length > 0 ){
                    //判断里面是否有重复的
                    uniq(obj2.big)
                    if(temp.length > 5){
                        //删除第一个 保留最后一个
                        temp.shift();
                    }
                };
                obj2.big = temp;
                localStorage.setItem('optionJion',JSON.stringify(obj2));
                againOption('big');
                // $("#bigImgselect").append("<option value='"+ input_width +'*'+ input_height +"'>"+ input_width +'*'+ input_height +"</option>");
            };
			//重新再写入
			function againOption(type) {
                var optionObj2 = JSON.parse(localStorage.getItem("optionJion"));
				if(optionObj2 && optionObj2[type]){
					$('#'+ type +'Imgselect').empty();
					// var str = "<option value='"+ optionObj2[type][i] +"'>"+ optionObj2[type][i] +"</option>";
					var str = "<option style='display:none' selected>"+'选择尺寸'+"</option>";
					for(var i = 0; i < optionObj2[type].length; i ++){
						str += "<option value='"+ optionObj2[type][i] +"'>"+ optionObj2[type][i] +"</option>"
					}
					$('#'+ type +'Imgselect').append(str)
				}
            }
            // var optionObj2 = JSON.parse(localStorage.getItem("optionJion"));
            // if(optionObj2.small){
            //     $("#smallImgselect").empty();
            //     var str = '';
            //     for(var i = 0; i < optionObj2.small.length; i ++){
            //         str += "<option value='"+ optionObj2.small[i] +"'>"+ optionObj2.small[i] +"</option>"
            //     }
            //     $("#smallImgselect").append(str)
            // }
            // if(optionObj2.mid){
            //     $("#midImgselect").empty();
            //     var str = '';
            //     for(var i = 0; i < optionObj2.mid.length; i ++){
            //         str += "<option value='"+ optionObj2.mid[i] +"'>"+ optionObj2.mid[i] +"</option>"
            //     }
            //     $("#midImgselect").append(str)
            // }
            // if(optionObj2.big){
            //     $("#bigImgselect").empty();
            //     var str = '';
            //     for(var i = 0; i < optionObj2.big.length; i ++){
            //         str += "<option value='"+ optionObj2.big[i] +"'>"+ optionObj2.big[i] +"</option>"
            //     }
            //     $("#bigImgselect").append(str)
            // }

			btn_assign(_this, _type, radio);
			synScale(_type, radio);
		}else {
			alert("输入的宽高比最大不能超过2.8！");
			$("#preview-pane-"+_type+"-input_1").focus();
		}
	}
	else{
		alert("请输入大于零的数字");
		$("#preview-pane-"+_type+"-input_1").focus();
	}
}

//确定按钮
function submitCropImg(){
	window.parent.channel_frame.setTitleImage();

}
//取消按钮
function cancelCropImg(){
	window.parent.channel_frame.titleDialog.close()
}
//单个使用原图按钮
function useOrignalImgOnly(type){
	   _smallRadio = smallRadio,
		_midRadio = midRadio,
		_bigRadio = bigRadio;
	//每张预览图的抽图高度，与默认的比例的高度保持一致
	var nowRadio,
		sh = _smallRadio.substring(_smallRadio.indexOf("*")+1),
		mh = _midRadio.substring(_midRadio.indexOf("*")+1),
		bh = _bigRadio.substring(_bigRadio.indexOf("*")+1),
	//原图的宽高
		imgW = parseInt($("#target").css("width"))*widthRadio,
		imgH = parseInt($("#target").css("height"))*heightRadio,
	//每张预览图的高度
		sw = Math.round(imgW/imgH*sh),
		mw = Math.round(imgW/imgH*mh),
		bw = Math.round(imgW/imgH*bh);
	_smallRadio = sw + "*" + sh;
	_midRadio = mw + "*" + mh;
	_bigRadio = bw + "*" + bh;

	//$.ajax({
	//	url: "../../xy/ueditor/prepare4Extract.do",
	//	dataType: "text",
	//	async: false,
	//	data : {
	//		"imagePath" : imgName
	//	},
	//	error: function (XMLHttpRequest, textStatus, errorThrown) {
	//		alert(errorThrown + ':' + textStatus);
	//	},
	//	success: function (data) {}
	//});
	//让抽图的比例为原图
	if(type=="small"){
		itype="small";
		chooseCropImage(itype);
	}else if(type=="mid"){
		itype="mid";
        chooseCropImage(itype);
	}else if(type=="big"){
		itype="big";
        chooseCropImage(itype);
	}

		if(now == "small")
			nowRadio = _smallRadio;
		else if(now == "mid")
			nowRadio = _midRadio;
		else if(now == "big")
			nowRadio = _bigRadio;

	if(!jcrop_api)
		return;


	synScale(now, nowRadio);
	if(type=="small"){
		$('#topicPicSmallDiv', window.parent.document).html('<img id="picSmall" itype="small" src="../image.do?path=图片存储;'+window.curentOrignalImgSrc+'"/><span class="icon-remove"></span>');
        $("#pp-small img").css({marginLeft:"0",marginTop:"0",width:"100%",height:"100%"});
	}else if(type=="mid"){
		$('#topicPicMidDiv', window.parent.document).html('<img id="picSmall" itype="small" src="../image.do?path=图片存储;'+window.curentOrignalImgSrc+'"/><span class="icon-remove"></span>');
        $("#pp-mid img").css({marginLeft:"0",marginTop:"0",width:"100%",height:"100%"});
	}else if(type=="big"){
		$('#topicPicBigDiv', window.parent.document).html('<img id="picSmall" itype="small" src="../image.do?path=图片存储;'+window.curentOrignalImgSrc+'"/><span class="icon-remove"></span>');
        $("#pp-big img").css({marginLeft:"0",marginTop:"0",width:"100%",height:"100%"});
	}

	//$("#topicPicSmallDiv").html('<img id="picSmall" itype="small" src="1.png"/><span class="icon-remove"></span>')
	//channel_frame.titleDelListener("#topicPicSmallDiv");
	//isuseOrignal = true;
	//debugger
	//jcrop_api.setSelect([0, 0, imgW, imgH]);
	// animateImg([0, 0, imgW, imgH]);


}
//全部使用原图按钮
function useOrignalImg(){
    sessionStorage.setItem("useOrignalImgOnly",false); //用于关闭对话框
	_smallRadio = smallRadio,
	_midRadio = midRadio,
	_bigRadio = bigRadio;
	//每张预览图的抽图高度，与默认的比例的高度保持一致
	var nowRadio,
		sh = _smallRadio.substring(_smallRadio.indexOf("*")+1),
		mh = _midRadio.substring(_midRadio.indexOf("*")+1),
		bh = _bigRadio.substring(_bigRadio.indexOf("*")+1),
	//原图的宽高
		imgW = parseInt($("#target").css("width"))*widthRadio,
		imgH = parseInt($("#target").css("height"))*heightRadio,
	//每张预览图的高度
		sw = Math.round(imgW/imgH*sh),
		mw = Math.round(imgW/imgH*mh),
		bw = Math.round(imgW/imgH*bh);
	_smallRadio = sw + "*" + sh;
	_midRadio = mw + "*" + mh;
	_bigRadio = bw + "*" + bh;
	$.ajax({
		url: "../../xy/ueditor/prepare4Extract.do",
		dataType: "text",
		async: false,
		data : {
			"imagePath" : imgName
		},
		error: function (XMLHttpRequest, textStatus, errorThrown) {
			alert(errorThrown + ':' + textStatus);
		},
		success: function (data) {}
	});
	//让抽图的比例为原图
	if(itype != "all"){
		chooseCropImage(itype);
		if(now == "small")
			nowRadio = _smallRadio;
		else if(now == "mid")
			nowRadio = _midRadio;
		else if(now == "big")
			nowRadio = _bigRadio;
	} else {
		chooseCropImage("big");
		nowRadio = _bigRadio;
	}
	if(!jcrop_api)
		return;
	synScale(now, nowRadio);
	isuseOrignal = true;
	jcrop_api.setSelect([0, 0, imgW, imgH]);
	// animateImg([0, 0, imgW, imgH]);
}
function originalImgReturn(imgPath) {
	if(!imgPath)
		return;
	/*if(itype=="all")
		smallC = midC = bigC;
		window.parent.channel_frame.setImageProperties(smallC, midC, bigC, $("#target").attr("src"), widthRadio, heightRadio, _smallRadio, _midRadio, _bigRadio);
		window.parent.channel_frame.setTitleImage();*/
	var imgs = {};
	imgs.imgSmall = $("#preview-pane-small").length > 0 && imgPath;
	imgs.imgMid = $("#preview-pane-mid").length > 0 && imgPath;
	imgs.imgBig = $("#preview-pane-big").length > 0 && imgPath;
	window.parent.channel_frame.handleTitleImgHtml(imgs);
}
var online_image = {
	//图片库选择：“确定”按钮
	picClose : function(docLibID, docIDs) {
		if (!docIDs) return;
		docIDs = docIDs.split(",");
		//根据索引图的DocLibID，DocID获取到这组的所有组图
		$.ajax({
			url: "../../xy/pic/getPics.do",
			dataType: "json",
			async: false,
			data : {
				"DocID" : docIDs[0],
				"DocLibID" : docLibID
			},
			error: function (XMLHttpRequest, textStatus, errorThrown) {
				alert(errorThrown + ':' + textStatus);
			},
			success: function (data) {
				for (var i = 0; i < data.length; i++) {
					if (data[i].DocID == docIDs[0]) {
						var imgPath = "../image.do?path=" + data[i].path;
						originalImgReturn(imgPath);
						break;
					}
				}
			}
		});
	},
	//图片库选择：“取消”按钮
	picCancel : function() {
		cancelCropImg();
	}
}
/*function changeValue(index){
	var value_1=$("#preview-word-select_1").val();
	var value_2=$("#preview-word-select_2").val();
	var value_3=$("#preview-word-select_3").val();
	if((value_1=="自定义")||(value_2=="自定义")||(value_3=="自定义")){
		$(".preview-word-assign").show();
	}else{
		$(".preview-word-assign").hide();
	}
	// if(value_2=="自定义"){
	// 	$("#preview-word-assign_2").show();
	// }else{
	// 	$("#preview-word-assign_2").hide();
	// }
	// if(value_3=="自定义"){
	// 	$("#preview-word-assign_3").show();
	// }else{
	// 	$("#preview-word-assign_3").hide();
	// }
}*/
/*function btn_assign(){
	var input_value=$("#preview-pane-small-input_1").val();
	$("#preview-word-select_1").val(input_value);
	$(".preview-word-assign").hide();
}*/


