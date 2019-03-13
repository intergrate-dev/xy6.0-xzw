// JavaScript Document
var index = 0;
var num = 5;
var num2 = Math.ceil(num / 2);
//自动播放
var w1 = 0;
var w2 = 0;
var len1 = 0;
var len2 = 0;
var timer, oPicLi, oPicUl, oListLi, oListUl, $oCommentsP;
//初始化组图轮播组件
function initJPhoto(showTab){
	//当前显示的是哪个div
	$(".nav.nav-tabs").find("a").click(function(){
		//获得当前模块id，例如 original， pc， app
		_thisTabId = $(this).attr("id");
		_thisTabId = _thisTabId.replace("A","");
		//清空之前的一些变量
		index = 0;
		clearInterval(timer);
		
		$("div[id$=ShowDiv]").hide();
		$("#"+ _thisTabId + "ShowDiv").show();
		
		//如果存在组图这个模块，初始化四个参数，按钮， 自动轮播
		if($("#"+ _thisTabId + "PicDiv").size()>0){
			//初始化变量
			initVariables(_thisTabId);
			
			//开启自动轮播
			timer=setInterval(autoPlay,4000);
			//点击第一个图片
			$("#listBox_"+ _thisTabId).find("li :first").click();
		}
	});
	//给按钮添加事件
	$("span[id^=next]").click(topNextClickFn);
	$("span[id^=next]").mouseover(mouseOverFn);
	$("span[id^=next]").mouseout(mouseOutFn);
	
	$("span[id^=prev]").mouseover(mouseOverFn);
	$("span[id^=prev]").mouseout(mouseOutFn);
	$("span[id^=prev]").click(topPrevClickFn);
	
}

//初始化变量 初始化参数:timer, oPicLi, oPicUl, oListLi, oListUl, $oCommentsP，
//按钮， 自动轮播
function initVariables(_thisTabId){
	//初始化，几个参数
	//w1 大图的 offsetWidth
	//w2 小图的 offsetWidth
	//len1 imageList的长度
//	debugger;
	var $oPicLi = $("#picBox_"+ _thisTabId).find("li");
	oPicLi = $oPicLi[0];
//	w1 = oPicLi.offsetWidth;
	w1 = $oPicLi.eq(0).width();
	len1 = $oPicLi.size();
	
	var $oListLi = $("#listBox_"+ _thisTabId).find("li");
	oListLi = $oListLi[0];
//	w2 = oListLi.offsetWidth;
	w2 = $oListLi.eq(0).width();
	len2 = $oListLi.size();
	
	var $oListUl = $("#listBox_"+ _thisTabId).find("ul");
	oListUl = $oListUl[0];
	
	var $oPicUl = $("#picBox_"+ _thisTabId).find("ul");
	oPicUl = $oPicUl[0];
	//设定上下图片滑动的ul宽度
	oPicUl.style.width = w1 * len1 + "px";
	oListUl.style.width = (w2 * len2 +20) + "px";
	//图片注释P
	$oCommentsP = $("#comments_"+ _thisTabId+" p");
	
	//初始化小图标的点击事件
	$oListLi.unbind("click");
	for (var i = 0; i < $oListLi.size(); i++) {
		$oListLi[i].index = i;
		$oListLi[i].onclick = function(){
			index = this.index;
			Change();
		}
	}
}

//自动播放
function autoPlay(){
    index ++;
	index = index == len2 ? 0 : index;
	Change();
}
//每一张图片的点击事件
function liClickFn(){
	for (var i = 0; i < oListLi.length; i++) {
		oListLi[i].index = i;
		oListLi[i].onclick = function(){
			index = this.index;
			Change();
		}
	}
}
//下一个按钮的触发事件
function topNextClickFn(){
	index ++;
	//index==3？0：index
	index = index == len2 ? 0 : index;
	Change();
}
//上一个按钮的触发事件
function topPrevClickFn(){
	index --;
	index = index == -1 ? len2 -1 : index;
	Change();
}
/**
 * 鼠标移上来的时候，停止自动播放；移走继续；
 */
function mouseOverFn(){
	clearInterval(timer);
}

function mouseOutFn(){
	timer=setInterval(autoPlay,4000);
}


//换图片
function Change(){
	Animate(oPicUl, {left: - index * w1});
	//小于一半的时候，不动
	if(index < num2){
		Animate(oListUl, {left: 0});
	}else if(index + num2 <= len2){
		//在中间的时候，平移
		Animate(oListUl, {left: - (index - num2 + 1) * w2});
	}else{
		//在尾巴的时候，不平移
		Animate(oListUl, {left: - (len2 - num) * w2});
	}

//添加样式
	$(oListLi).parent().find("li").removeClass("on");
	$(oListLi).parent().find("li").eq(index).addClass("on");
//添加文字
	$oCommentsP.hide();
	$oCommentsP.eq(index).show();
}

//获得样式
function getStyle(obj, attr){
	if(obj.currentStyle){
		return obj.currentStyle[attr];
	}else{
		return getComputedStyle(obj, false)[attr];
	}
}

//轮播滚动
function Animate(obj, json){
	if(obj.timer){
		clearInterval(obj.timer);
	}
	//30秒滚一次
	obj.timer = setInterval(function(){
		for(var attr in json){
			var iCur = parseInt(getStyle(obj, attr));
			iCur = iCur ? iCur : 0;
			var iSpeed = (json[attr] - iCur) / 5;
			iSpeed = iSpeed > 0 ? Math.ceil(iSpeed) : Math.floor(iSpeed);
			//滚动一个图片的距离
			obj.style[attr] = iCur + iSpeed + 'px';
			if(iCur == json[attr]){
				clearInterval(obj.timer);
			}
		}
	}, 30);
}


