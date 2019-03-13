var _artPicNum = 4; // 组图稿每行显示几张图
var _libPicNum = 5; // 图片库每行显示几张图
var _artPicWidth = '900px';
var colDialog = null;
$(function() {
	if($('#fromPage').val() != 'pic'){
		$('#ul1').css('width', _artPicWidth);
	}else{
		var li0 = $('#ul1').children("li");
		setPicUploadDivHeight(li0.length, $('#fromPage').val());
	}
	
	setDragInit();
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
		
		jsonParam = {
				"siteID": $('#siteID').val(),
				"DocLibID": $('#DocLibID').val(),
				"FVID": $('#FVID').val(),
				"UUID": $('#UUID').val(),
				"topic": $('#topic').val(),
				"overall": $('#overall').val(),
				"p_catID": $('#p_catID').val(),
				"fromPage": $('#fromPage').val(),
				"p_groupID": $('#p_groupID').val()
		};
		datas.push(jsonParam);
	
		$.ajax({
			url: "../../xy/pic/InsertDB.do",
			type: "POST",
			dataType: "json",
			async: false,
			data:{"data":JSON.stringify(datas)},
			error: function() {//请求失败处理函数
				alert("error");
			},
			success: function (data) {
				var result = eval(data);
				location.href = "../../e5workspace/after.do?DocIDs="
					+ result.DocIDs + "&DocLibID=" + result.docLibID
					+ "&UUID=" + result.UUID;
			}
		});
	});
	
	// 点击取消按钮
	$('#editCancel').click(function(){
		location.href = "../../e5workspace/after.do?UUID=" + $('#UUID').val();
	});
	
	//图片库的修改操作时，添加按钮事件
	if ($('#fromPage').val() == 'pic'){
		// 点击删除图片图标
		$('[name="delBtn_"]').click(function(evt){
			$(this).parent().parent().remove();
			delbtnclick(evt);
		});
		// 点击同步上传图片图标
		$('[name="synBtn_"]').click(function(evt){
			synbtnclick(evt);
		});
		// 点击设为标题图片图标
		$('[name="titleBtn_"]').click(function(evt){
			titlebtnclick(evt);
		});
		// 点击修改图标
		$('[name="modifyBtn_"]').click(function(evt){
			modifybtnclick(evt);
		});
	}
	// 点击继续上传图片按钮
	$('#linkli').click(function(){
		linkliclick();
	});
	
	// 一括输入框
	$('#overall').bind('input propertychange', function() {
		var elem = $('[name=text_]');
		for(var a = 0; a < elem.length; a++){
			$(elem[a]).val($('#overall').val());
		}
	});
});
//通过class获取元素
function getClass(cls){
    var ret = [];
    var els = document.getElementsByTagName("*");
    for (var i = 0; i < els.length; i++){
        //判断els[i]中是否存在cls这个className;.indexOf("cls")判断cls存在的下标，如果下标>=0则存在;
        if(els[i].className === cls || els[i].className.indexOf("cls")>=0 || els[i].className.indexOf(" cls")>=0 || els[i].className.indexOf(" cls ")>0){
            ret.push(els[i]);
        }
    }
    return ret;
}
function getStyle(obj,attr){//解决JS兼容问题获取正确的属性值
	return obj.currentStyle?obj.currentStyle[attr]:getComputedStyle(obj,false)[attr];
}
function startMove(obj,json,fun){
	clearInterval(obj.timer);
	obj.timer = setInterval(function(){
		var isStop = true;
		for(var attr in json){
			var iCur = 0;
			//判断运动的是不是透明度值
			if(attr=="opacity"){
				iCur = parseInt(parseFloat(getStyle(obj,attr))*100);
			}else{
				iCur = parseInt(getStyle(obj,attr));
			}
			var ispeed = (json[attr]-iCur)/8;
			//运动速度如果大于0则向下取整，如果小于0想上取整；
			ispeed = ispeed>0?Math.ceil(ispeed):Math.floor(ispeed);
			//判断所有运动是否全部完成
			if(iCur!=json[attr]){
				isStop = false;
			}
			//运动开始
			if(attr=="opacity"){
				obj.style.filter = "alpha:(opacity:"+(json[attr]+ispeed)+")";
				obj.style.opacity = (json[attr]+ispeed)/100;
			}else{
				obj.style[attr] = iCur+ispeed+"px";
			}
		}
		//判断是否全部完成
		if(isStop){
			clearInterval(obj.timer);
			if(fun){
				fun();
			}
		}
	},30);
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

// 加载拖动事件
function setDragInit(){
	var oUl= document.getElementById("ul1");
	if(!oUl){ // 防止preview.jsp报错
		return;
	}
	var aLi = oUl.getElementsByTagName("li");
	var aLilength = aLi.length;
	// 至少有一张那图片
	if(aLilength > 1){
		// 计算上传图片位置
		var num = _artPicNum;
		if($('#fromPage').val() == 'pic'){
			num = _libPicNum;
		}
		var floor = Math.ceil(aLilength / num) - 1;
		var remainder = aLilength % num - 1;
		if(remainder == -1){
			remainder = num - 1;
		}
		// 计算上传图片图标位置
		$('#linkli').css('top', (196 * floor) +'px');
		$('#linkli').css('left', (0 + 210 * remainder) + 'px');
		$('#headline').css('display', 'inline');
		// 默认选中第一个
		if($('input:radio[name="radio_"]:checked').length == 0){
			$('#radio_1').attr('checked', 'checked');
		}
	}else{
		// 一张图片都没有
		$('#headline').css('display', 'none');
		if($('#fromPage').val() == 'pic'){
			$('#modal-overlay').css('visibility', 'visible');
		}
	}

	var disX = 0;
	var disY = 0;
	var minZindex = 999;
	var aPos = [];
	for(var i = 0; i < aLilength - 1; i++){
		var t = aLi[i].offsetTop;
		var l = aLi[i].offsetLeft;
		aLi[i].style.top = t + "px";
		aLi[i].style.left = l + "px";
		aPos[i] = {left:l,top:t};
		aLi[i].index = i;
	}
	for(var i = 0; i < aLilength - 1; i++){
		aLi[i].style.position = "absolute";
		aLi[i].style.margin = 0;
		setDrag(aLi[i]);
	}
	
	//拖拽
	function setDrag(obj){
		obj.onmouseover = function(){
			obj.style.cursor = "";
		}
		obj.onmousedown = function(event){
			if($(event.target)[0].tagName == 'TEXTAREA'
				|| $(event.target)[0].tagName == 'INPUT'){
				return;
			}
			var scrollTop = document.documentElement.scrollTop||document.body.scrollTop;
			var scrollLeft = document.documentElement.scrollLeft||document.body.scrollLeft;
			obj.style.zIndex = minZindex++;
			//当鼠标按下时计算鼠标与拖拽对象的距离
			disX = event.clientX + scrollLeft-obj.offsetLeft;
			disY = event.clientY + scrollTop-obj.offsetTop;
			document.onmousemove = function(event){
				//当鼠标拖动时计算div的位置
				var l = event.clientX -disX +scrollLeft;
				var t = event.clientY -disY + scrollTop;
				obj.style.left = l + "px";
				obj.style.top = t + "px";
				/*for(var i=0;i<aLilength;i++){
					aLi[i].className = "";
					if(obj==aLi[i])continue;//如果是自己则跳过自己不加红色虚线
					if(colTest(obj,aLi[i])){
						aLi[i].className = "active";
					}
				}*/
				for(var i=0;i<aLilength-1;i++){
					aLi[i].className = "";
				}
				var oNear = findMin(obj);
				if(oNear){
					oNear.className = "active";
				}
			}
			document.onmouseup = function(event){
				document.onmousemove = null;//当鼠标弹起时移出移动事件
				document.onmouseup = null;//移出up事件，清空内存
				//检测是否普碰上，在交换位置
				var oNear = findMin(obj);
				if(oNear){
					oNear.className = "";
					oNear.style.zIndex = minZindex++;
					obj.style.zIndex = minZindex++;
					var objindex = obj.index;
					var oNearindex = oNear.index;
					// 从后往前拖
					if(objindex > oNearindex){
						for(var i = objindex - 1; i > oNearindex - 1; i--){
							for(var a = 0; a < aLilength-1; a++){
								if(aLi[a].index == i){
									startMove(aLi[a], aPos[i + 1]);
									aLi[a].index++;
									break;
								}
							}
						}
						startMove(obj, aPos[oNearindex]);
						obj.index = oNearindex;
					// 从前往前拖
					}else{
						for(var i = objindex + 1; i < oNearindex + 1; i++){
							for(var a = 0; a < aLilength-1; a++){
								if(aLi[a].index == i){
									startMove(aLi[a], aPos[i - 1]);
									aLi[a].index--;
									break;
								}
							}
						}
						startMove(obj, aPos[oNearindex]);
						obj.index = oNearindex;
					}
					/* 仅图片位置互换用这一段代码
					startMove(oNear,aPos[obj.index]);
					startMove(obj,aPos[oNear.index]);
					//交换index
					oNear.index += obj.index;
					obj.index = oNear.index - obj.index;
					oNear.index = oNear.index - obj.index; */
				}else{
					startMove(obj,aPos[obj.index]);
				}
			}
			clearInterval(obj.timer);
			return false;//低版本出现禁止符号
		}
	}
	//碰撞检测
	function colTest(obj1,obj2){
		var t1 = obj1.offsetTop;
		var r1 = obj1.offsetWidth+obj1.offsetLeft;
		var b1 = obj1.offsetHeight+obj1.offsetTop;
		var l1 = obj1.offsetLeft;

		var t2 = obj2.offsetTop;
		var r2 = obj2.offsetWidth+obj2.offsetLeft;
		var b2 = obj2.offsetHeight+obj2.offsetTop;
		var l2 = obj2.offsetLeft;

		if(t1>b2||r1<l2||b1<t2||l1>r2){
			return false;
		}else{
			return true;
		}
	}
	//勾股定理求距离
	function getDis(obj1,obj2){
		var a = obj1.offsetLeft-obj2.offsetLeft;
		var b = obj1.offsetTop-obj2.offsetTop;
		return Math.sqrt(Math.pow(a,2)+Math.pow(b,2));
	}
	//找到距离最近的
	function findMin(obj){
		var minDis = 999999999;
		var minIndex = -1;
		for(var i=0;i<aLilength-1;i++){
			if(obj==aLi[i])continue;
			if(colTest(obj,aLi[i])){
				var dis = getDis(obj,aLi[i]);
				if(dis<minDis){
					minDis = dis;
					minIndex = i;
				}
			}
		}
		if(minIndex==-1){
			return null;
		}else{
			return aLi[minIndex];
		}
	}
}

// 解决不同浏览器多次进入方法
var chroIEUploadflag = false;
//点击上传图片
function linkliclick(){
	if(chroIEUploadflag){
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

// 点击同步按钮
function synbtnclick(evt){
	var id = $(evt.target)[0].id;
	var val = $('#' + id.replace('synBtn', 'text')).val();
	if('' == trim(val)){
		var text_ = $('[name="text_"]');
		var flag = true;
		for(var a = 0; a < text_.length; a++){
			if(trim($(text_[a]).val()) != ''){
				flag = false;
				break;
			}
		}
		if(flag) alert('请先输入图片说明再进行同步！');
	}
	$('[name="text_"]').val(trim(val));
}

//双击图片
function imgdblclick(evt){
	var scrollTop = document.documentElement.scrollTop||document.body.scrollTop;
	var target = $(evt.target)[0];
	var dataUrl = '../pic/Preview.jsp?src=' + target.src + '&width=' + 
		target.naturalWidth + '&height=' + target.naturalHeight;
	var pos = {/*aui_titleBar*/
		top : scrollTop + 'px',
		left : '100px'
	};
	colDialog = e5.dialog({
		type : "iframe",
		value : dataUrl
	}, {
		showTitle : false,
		width : "1000px",
		height : "550px",
		pos : pos,
		resizable : false
	});
	colDialog.show();
}

//点击设为标题图片按钮
function titlebtnclick(evt){
	var id = $(evt.target)[0].id;
	var div = $('#' + id.replace('titleBtn_', 'linkli_')).children("div");
	var path = $(div[1]).children("input").val();
	
	var url = '../../xy/ueditor/initTitleDialog.do?imagePath=' + path + "&itype=all";
	var title = "设置标题图片";
	var dialogName = "titleDialog";
	
	showOneDialog(dialogName, url, title);
}
//打开一个窗口，提取以图复用
function showOneDialog(dialogName, url, title) {
	var scrollTop = document.documentElement.scrollTop||document.body.scrollTop;
	var pos = {
		left : "100px",
		top : scrollTop + "px",
		width : "1000px",
		height : "500px"
	};
	// dialog
	channel_frame[dialogName] = e5.dialog({
		type : "iframe",
		value : url
	}, {
		showTitle : true,
		title: title,
		width : "1000px",
		height : "560px",
		pos : pos,
		resizable : false
	});
	channel_frame[dialogName].show();
}

var chosenImg = null;
var chosenInput = null;
var imgLiList = null;
//点击修改按钮
function modifybtnclick(evt){
	var id = $(evt.target)[0].id;
	var div = $('#' + id.replace('modifyBtn_', 'linkli_')).children("div");
	chosenImg = $(div[1]).children("img")[0];
	chosenInput = $(div[1]).children("input")[0];

	//同级所有的图片
	imgLiList = $("#ul1").find("li[id^=linkli_]");
	
	var iParentHeight = window.parent.document.documentElement.clientHeight || window.parent.document.body.clientHeight;
	iParentHeight -= 120;
	iParentHeight = iParentHeight >= 590 ? 590 : iParentHeight;
	var _cssRules = "width:800px;height:590px;";
	if ($('#fromPage').val() == 'pic'){
		_cssRules = "width:800px;height:"+ iParentHeight + "px;";
	}
	
	var url = '../ueditor/dialogs/imagecrop/imagecrop.jsp';
	var title = "图片修改";
	
	var _editor = UE.getEditor("simpleEditor");
	//var dialog = _editor.getDialog("picModify");
	var dialog =_editor.ui._dialogs["picModify"];
	if(!dialog){
		dialog = new UE.ui.Dialog({
			//指定弹出层中页面的路径，这里只能支持页面,因为跟addCustomizeDialog.js相同目录，所以无需加路径
			iframeUrl: url,
			//需要指定当前的编辑器实例
			editor:_editor,
			//指定dialog的名字
			name:"picModify",
			//dialog的标题
			title:title,
			//指定dialog的外围样式
			cssRules : _cssRules
		});
		dialog.render();
		_editor.ui._dialogs["picModify"] = dialog;
	}
	dialog.open();
	
	//showOneDialog("imagecropDialog", url, title);
}

function resetImageInfo(_src){
	chosenImg.src = _src;
	chosenInput.value = _src.substr(_src.indexOf('image.do?path=')).replace('image.do?path=', '');
}
function resetImageListInfo(_imgList){
	_$imgs = imgLiList.find(".picfather").find("img[class=imgsize]");
	_$input = imgLiList.find(".picfather").find("input");
	for(var _i = 0, _size = _$imgs.size(); _i < _size; _i++){
		_imagePath = decodeURI(_imgList[_i]);
		_imagePath = "../.." + _imagePath.substr(_imagePath.lastIndexOf("/xy"));
		_$imgs.eq(_i).attr("src", _imagePath);
		_$input.eq(_i).val(_imagePath.substr(_imagePath.lastIndexOf("path=")+5));
	}
}
// 算出有几行图片撑开外面div高度
function setPicUploadDivHeight(len, fromPage){
	var ceil = Math.ceil(len / _artPicNum);
	var height = '158px';
	if(ceil > 1) {
		ceil -= 2;
		height = (352 + 196 * ceil) + 'px';
	}
	if('pic' == fromPage){
		ceil = Math.ceil(len / _libPicNum);
		height = '200px';
		if(ceil > 1) {
			ceil -= 2;
			height = (394 + 196 * ceil) + 'px';
		}
	}
	$('#picUploadDiv,#headline').css('height', height);
}
//点击删除图片
function delbtnclick(evt){
	var delIndex = $(evt.target).parent().parent()[0].index; // 被删除图片的index
	// 解决不同浏览器多次进入方法
	chroIEUploadflag = false;
	var ul0 = $('#ul1');
	var li0 = ul0.children('li');
	var len = li0.length;
	var fromPage = $('#fromPage').val();
	
	if(len > 1){
		var picInfo = [];
		var div0 = '';
		for(var b = 0; b < len; b++){ // 每个li的index
			if(b == delIndex) continue;
			for(var a = 0; a < len - 1; a++){ // 每个li
				if(b == li0[a].index){
					div0 = $(li0[a]).children("div");
					break;
				}
			}
			var isIndex = '2';
			if($(div0[0]).children("input").attr("checked")) isIndex = '0';
			
			var jsonParam = {
				"isIndexed":isIndex,// 是否索引图
				"path":$(div0[1]).children("input").val(),// 图片路径
				"content":$(div0[2]).children("textarea").val()// 图片说明
			}
			
			var pic = $(div0[1]).children("img").attr("pic");
			if (pic) jsonParam.pic = pic;
			
			picInfo.push(jsonParam);
		}
		$('#ul1').html(''); // 放在上面的话IE会有bug
		for(var a = 0; a < picInfo.length; a++){
			pic_group.drawImg(fromPage, a + 1, picInfo[a], true);
		}
	}else{
		$('#ul1').html('');
	}
	pic_group.addUploadBtn();
	setDragInit();
	setPicUploadDivHeight(len, fromPage);
}

// 删除按钮绑定click事件
function _delBtnClick(evt) {
	var src = $(evt.target);
	src.parent().parent().remove();
	delbtnclick(evt);
}

// 同步按钮绑定click事件
function _synBtnClick(evt) {
	synbtnclick(evt);
}

function _titleBtnClick(evt) {
	titlebtnclick(evt);
}

function _modifyBtnClick(evt) {
	modifybtnclick(evt);
}

function _imgdblclick(evt) {
	imgdblclick(evt);
}

//上传图标绑定click事件
function _linkliClick() {
	linkliclick();
}
function trim(s){
    return rtrim(ltrim(s));
}

function ltrim(s){
    return s.replace( /^[" "|"　"|"\n"]*/, "");
}

function rtrim(s){
    return s.replace( /[" "|"　"|"\n"]*$/, "");
}

var pic_group = {
	getPicInfo : function(){
		var li0 = $('#ul1').children("li");
		var len = li0.length - 1;
		var div0 = '';
		var jsonParam = '';
		var datas = [];
		var isIndex = '';
		var pic = '';

		for(var b = 0; b < len; b++){ // 每个li的index
			for(var a = 0; a < len; a++){ // 每个li
				if(b == li0[a].index){
					div0 = $(li0[a]).children("div");
					break;
				}
			}
			if($(div0[0]).children("input").attr("checked")){
				isIndex = '0';
			}else{
				isIndex = '2';
			}
			
			jsonParam = {
				"isIndexed":isIndex,// 是否索引图
				"path":$(div0[1]).children("input").val(),// 图片路径
				"content":$(div0[2]).children("textarea").val()// 图片说明
			}
			
			pic = $(div0[1]).children("img").attr("pic");
			if (pic) jsonParam.pic = pic;
			
			datas.push(jsonParam);
		}
		return datas;
	},
	setPicInfo : function(datas) {
		$('#ul1').html('');
		pic_group.addPicInfo(datas);
	},
	addPicInfo : function(datas) {
		/*
		//先去掉最后的上传按钮
		var lis = $('#ul1 li');
		if (lis.length > 0) {
			lis.get(lis.length - 1).remove();
		}
		*/
		var oldCount = $('#ul1 li').length;
		if (oldCount > 0) oldCount = oldCount - 1;
		
		var len = datas.length;
		var fromPage = $('#fromPage').val();
		for(var a = 0; a < len; a++){
			if (!datas[a].pic) datas[a].pic = '';
			pic_group.drawImg(fromPage, oldCount + a + 1, datas[a], true);
		}
		pic_group.addUploadBtn();
		
		setDragInit();
		setPicUploadDivHeight(oldCount + len + 1, fromPage);
	},
	//上传按钮附加在最后
	addUploadBtn : function() {
		var str = '<li id="linkli" class="linkli">'
			+ '<img class="imgsize1" alt="上传图片" src="../pic/img/upload.jpg">'
			+ '</li>';
		$('#ul1').append(str);
		$("#linkli").click(_linkliClick);
	},
	//显示一个图片，添加按钮，并设事件
	drawImg : function(fromPage, index, picInfo, useLittle) {
		//按钮html：设为标题图片
		var btnTitlePic = '';
		if (fromPage != 'pic')
			btnTitlePic = '<input class="title" type="button" title="设为标题图片" name="titleBtn_" id="titleBtn_' + index + '" />'
						+ '<span class="icon-picture"></span>';
		//按钮html：同步图片说明
		var btnSync = '<input class="synBtn_" type="button" title="同步图片说明" name="synBtn_" id="synBtn_' + index + '" />'
			+	'<span class="icon-retweet"></span>';
		//按钮html：删除
		var btnDelete = '<input class="delBtn_" value="删除" title="删除图片" type="button" name="delBtn_" id="delBtn_' + index + '" />'
			+ 	'<span class="icon-trash"></span>';
		//按钮html：修改
		var btnEdit = '<input class="cut" type="button" title="修改" name="modifyBtn_" id="modifyBtn_' + index + '" />'
			+ 	'<span class="icon-pencil"></span>';

		if (!picInfo.picPath) picInfo.picPath = picInfo.path; //不同调用者传入的不同，有picPath有path
		
		//图片显示地址
		var picUrl = picInfo.picPath || picInfo.path;
		if (picUrl.toLowerCase().indexOf("http:") < 0)
			picUrl = "../image.do?path=" + picUrl;
		if (useLittle) picUrl += ".0"; //使用小图标（第二次打开时，用小图标）
			
		//图片显示区域html
		var divShowImg = '<div class="picfather">'
			+ 	'<img class="imgsize" alt="图片" src="' + picUrl + (picInfo.pic ? '" pic="' + picInfo.pic : '') + '">'
			+ 	'<input value="' + picInfo.picPath + '" type="hidden" />'
			+ '</div>';
		//图片说明区域html
		var divPicContent = '<div>'
			+ 	'<textarea placeholder="输入图片说明..." type="text" class="text_" id="text_' + index + '" name="text_">' + picInfo.content + '</textarea>'
			+ '</div>';
		
		var str;
		if (fromPage != "live"){
			str = '<li class="linkli_" id="linkli_' + index + '">'
			+ '<div class="picDiv">'
			+	btnSync + btnDelete + btnTitlePic + btnEdit
			+ '</div>'
			+ divShowImg
			+ divPicContent
			+ '</li>';
		} else {
			str = '<li class="linkli_" id="linkli_' + index + '">'
			+ '<div class="picDiv">'
			+ 	btnDelete + btnEdit
			+ '</div>'
			+ divShowImg
			+ '</li>';
		}
		
		$('#ul1').append(str);
		$('#synBtn_' + index).click(_synBtnClick);
		$('#delBtn_' + index).click(_delBtnClick);
		$('#titleBtn_' + index).click(_titleBtnClick);
		$('#modifyBtn_' + index).click(_modifyBtnClick);
		
		pic_group.addTextEvent(index);
	},
	hText : 34,
	textFocus : function(evt) {
		$(evt.target).css({
			"height": pic_group.hText * 3,
			"position":"absolute",
			"z-index":"1199",
			"left":"0"
		});
	},
	textBlur : function(evt) {
		$(evt.target).css("height", pic_group.hText);
	},
	//图片说明输入框，改变高度事件
	addTextEvent : function(index) {
		var t = $("#text_" + index);
		t.focus(pic_group.textFocus);
		t.blur(pic_group.textBlur);
		var div = $("#linkli_" + index).find(".picfather");
		div.click(function(){
			$("#text_" + index).blur();
		});
	}
}
// 子窗口关闭按钮调用
function preViewCancel() {
	colDialog.close();
}