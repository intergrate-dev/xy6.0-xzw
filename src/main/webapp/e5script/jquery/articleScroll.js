(function($){
	$.fn.articleScroll = function() {
		var isFF = navigator.userAgent.toLowerCase().match(/firefox\/([\d.]+)/),
			minH = 20,
			html = ['<div class="scroll">',
			'<em class="scroll_top"></em><em class="scroll_slider"></em>',
			'<em class="scroll_bottom"></em></div>'].join(""),
			container = this,
			scroll = function() {
				var root = $(html).appendTo(container),
					slider = root.find(".scroll_slider");
				
				this.container = container;
				this._root = root;
				this._slider = slider;
				this.scrollAble;
				this.startPercent = 0;
				this.currentPercent;
				this.scrollDis;
				this.sliderDis;
				this.startY;
				this.draging = false;
				this.hideTimer;

				this.bindDomEvent();
			};
		scroll.prototype = {
			init:function() {
				this.containerClientHeight = this.container.innerHeight();
				this.containerScrollHeight = this.container[0].scrollHeight;
				this.scrollAble = this.containerClientHeight < this.containerScrollHeight;
				if (this.scrollAble) {
					var scrollHeightScale = this.containerClientHeight / this.containerScrollHeight,
						scrollHeight = parseInt(this.containerClientHeight * scrollHeightScale > minH ? this.containerClientHeight * scrollHeightScale : minH);
					this._slider.height(scrollHeight - 7);
					this.sliderDis = this.containerClientHeight - scrollHeight;
					this.scrollDis = this.containerScrollHeight - this.containerClientHeight;
					var positionScale = this.container.scrollTop() / this.scrollDis;
					// if(positionScale > 1){
					// 	positionScale = 1
					// }else if(positionScale < 0){
					// 	positionScale = 0
					// }
					// this.setPos(positionScale);
					this.setPos(0);
				}
			},
			hide: function() {
				this._root.hide()
			},
			show: function() {
				this._root.show()
			},
			start: function(e) {
				if (!this.draging) {
					this.draging = true;
					this.startY = e.clientY;
					e.preventDefault();
				}
			},
			move: function(e) {
				if (this.draging) {
					var curY = e.clientY;
					this.currentPercent = (curY - this.startY) / this.sliderDis + this.startPercent;
					if(this.currentPercent > 1){
						this.currentPercent = 1
					}else if(this.currentPercent < 0){
						this.currentPercent = 0
					}
					this.setPos(this.currentPercent);
					e.preventDefault();
				}
			},
			end: function(e) {
				if (this.draging) {
					this.draging = false;
					this.startPercent = this.currentPercent
				}
			},
			over: function(e) {
				var scroll = this;
				if(this.hideTimer){
					clearTimeout(this.hideTimer);
				}
				if(this.scrollAble){
					this.hideTimer = setTimeout(function() {
						scroll.show()
					}, 500)
				}
			},
			out: function(e) {
				var scroll = this;
				if(this.hideTimer){
					clearTimeout(this.hideTimer);
				}
				this.hideTimer = setTimeout(function() {
					scroll.hide()
				}, 500)
			},
			scrollFun: function(e,delta) {
				if (this.scrollAble) {
					this.startPercent = (this.container.scrollTop() + (-delta) * 16) / this.scrollDis;
					if(this.startPercent > 1){
						this.startPercent = 1
					}else if(this.startPercent < 0){
						this.startPercent = 0
					}
					this.setPos(this.startPercent);
					e.preventDefault()
				}
			},
			setPos: function(positionScale) {
				this.container.scrollTop(positionScale * this.scrollDis);
				this._root.css("top",positionScale * this.sliderDis + this.container.scrollTop());
				this.currentPercent = positionScale
			},
			scrollPercent: function(a) {
				this.scrollToByPercent(this.currentPercent + a)
			},
			scrollToByPercent: function(a) {
				this.setPos(a);
			},
			bindDomEvent: function() {
				var scroll = this;
				this.container.bind({
					"mouseover":function(e){scroll.over(e)},
					"mouseout":function(e){scroll.out(e)}
					// "mousewheel":function(e,delta){scroll.scrollFun(e,delta)}
				});
				// mousewheel需要专门的控件支持，jquery本身是不支持mousewheel的
				// 如果没有mousewheel支持，需要做兼容个类似如下的：
				// var isFF = navigator.userAgent.toLowerCase().match(/firefox\/([\d.]+)/);
				// addEvent(this.container[0], isFF ? "DOMMouseScroll" : "mousewheel", function(e) {
					// scroll.scrollFun(e)
				// });
				// function addEvent(elm, evtType, handler){
					// elm = typeof elm == "string" ? document.getElementById(elm) : elm;
					// if (elm == null) return false;
					// if (typeof d != "function") return false;
					// elm.addEventListener ? elm.addEventListener(evtType, handler, false) : elm.attachEvent ? elm.attachEvent("on" + evtType, handler) : b["on" + evtType] = handler;
					// return true
				// }
				this._slider.bind("mousedown",function(e){
					scroll.start(e)
				});
				$(document).bind({
					"mousemove":function(e){scroll.move(e)},
					"mouseup":function(e){scroll.end(e)}
				});
			}
		};
		return new scroll;
	};
})(jQuery);