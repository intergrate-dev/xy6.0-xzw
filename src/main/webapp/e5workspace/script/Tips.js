function Tips(){};
Tips.prototype.set=function() {//最小化与恢复状态切换
		var set = this.minbtn.data("status") == 1?[0,1,'block',this.char[0],'最小化']:[1,0,'none',this.char[1],'恢复'];
		this.minbtn.data("status", set[0]).html(set[3]).attr("title",set[4]);
		this.win.css("border-bottom-width",set[0]);
		this.content.css("display",set[2]);
		if(set[0]==0){
			this.moveTo(this.getY().top)
			}else{
			this.moveTo(this.getY().foot)
		}
	 }
Tips.prototype.remove=function() {//关闭\
	clearTimeout(this.autoCloseTimer);
	this.wrap.empty();
	this.wrap.remove();
	delete this.wrap;
}
Tips.prototype.setOpacity=function(x) {//设置透明度 
	var v = x >= 100 ? '': 'Alpha(opacity=' + x + ')';
	this.wrap[0].style.visibility = x<=0?'hidden':'visible';//IE有绝对或相对定位内容不随父透明度变化的bug
	this.wrap[0].style.filter = v;
	this.wrap[0].style.opacity = x / 100;
}
Tips.prototype.show=function() {//渐显
		  clearInterval(this.timer2);
		  var me = this,fx = this.fx(0, 100, 0.1),t = 0;
		  this.timer2 = setInterval(function() {
		   t = fx();
		   me.setOpacity(t[0]);
		   if (t[1] == 0) {clearInterval(me.timer2) }
		  },10);
	 }
Tips.prototype.fx=function(a, b, c) {//缓冲计算
		  var cMath = Math[(a - b) > 0 ? "floor": "ceil"],c = c || 0.1;
		  return function() {return [a += cMath((b - a) * c), a - b]}
	 }

Tips.prototype.getY=function() {//计算移动坐标
	var d = document,b = document.body, e = document.documentElement;
	var s = Math.max(b.scrollTop, e.scrollTop);
	var h = /BackCompat/i.test(document.compatMode)?b.clientHeight:e.clientHeight;
	var h2 = this.wrap[0].offsetHeight;
	var titleH= this.title[0].offsetHeight ;
		return {foot: s + h - titleH + 2+'px',top: s + h - h2 - 2+'px'}
}
Tips.prototype.moveTo=function(y) {//移动动画
	clearInterval(this.timer);
	var me = this,
		a = parseInt(this.wrap.css("top"))||0;
	var fx = this.fx(a, parseInt(y));
	var t = 0 ;
	this.timer = setInterval(function() {
		t = fx();
		me.wrap.css("top",t[0]+'px');
		if (t[1] == 0) {
			clearInterval(me.timer);
			// me.bind();
		}
	},10);
}
Tips.prototype.resizeBind=function (){//绑定窗口滚动条与大小变化事件
	var me = this , rt;
	clearTimeout(rt);
	rt = setTimeout(function() {
		if(me.wrap){
			me.wrap.css("top",me.getY().top)
		}
	},100);
}
Tips.prototype.init=function(option) {//创建HTML
	var inner = '<div class="icos"><a href="#" title="最小化" class="msg_min">0</a><a href="#" title="关闭" class="msg_close">r</a></div><div class="msg_title"></div><div class="msg_content"></div>',
		wrap = $('<div/>').appendTo(document.body)
				.attr("id","Msg_" + option.id)
				.addClass("msg_wrap")
				.css({
					"width":option.width+"px",
					"z-index" : option.id
				})
				.bgiframe({left:0,top:0,width:410,height:202}),
		win = $('<div/>').appendTo(wrap)
				.addClass("msg_win")
				.append(inner),
		set = {
			minbtn: 'msg_min',
			closebtn: 'msg_close',
			title: 'msg_title',
			content: 'msg_content'
		},
		self = this,
		i;
	for (i in set) {
		self[i] = $("."+set[i], wrap);
	};
	self.wrap = wrap;
	self.win = win;
	self.minbtn.click(function() {
		self.set();
		return false;
	});
	self.closebtn.click(function() {
		self.remove();
		return false;
	});
	self.char=navigator.userAgent.toLowerCase().indexOf('firefox')+1?['_','::','×']:['0','2','r'];//FF不支持webdings字体
	self.minbtn.html(self.char[0]);
	self.closebtn.html(self.char[2]);
	setTimeout(function() {//初始化最先位置
		self.wrap.css({
			"display":'block',
			"top":self.getY().foot
		});
		self.moveTo(self.getY().top);
	},500);
	self.title.html(option.title);
	self.content.html(option.msg).css("height", option.height - self.title.height() + "px");
	self.autoClose(option.autoClose);
}
Tips.prototype.autoClose = function(time){
	var self = this;
	if(time){
		self.autoCloseTimer = setTimeout(function(){
			self.remove();
		},time);
	}
}
function initMsgBox(option){
	var tip = new Tips(),
		_default = {
			id:1,
			msg:'',
			width:412,
			height:204,
			title:'消息提醒',
			autoClose:false
		};
	option = $.extend(_default,option);
	tip.init(option);
	return tip;
}