/**
 * @author twoCat
 */
(function($){
	var _setting={
			tabs:".tab",
			panes:".pane",
			current:"current",
			onBeforeClick:null,
			onClick:null,
			onAjax:null,
			effect:"simple",
			initialIndex:0,
			event:"click",
			rotate:false,
			slideUpSeed:400,
			slideDownSeed:400,
			fadeInSeed:400,
			fadeOutSeed:400,
			api:false,
			debug:false
		},
		effects={
			simple:function(pane,done){
				this.getPanes().hide();
				pane.show();
				done();
			},
			fade:function(pane,done){
				var setting=this.getSetting(),
					fadeInSeed=setting.fadeInSeed,
					fadeOutSeed=setting.fadeOutSeed,
				panes=this.getPanes();
				panes.fadeOut(fadeOutSeed);
				pane.fadeIn(fadeInSeed,done);
			},
			silde:function(pane,done){
				var setting=this.getSetting(),
					slideUpSeed=setting.slideUpSeed,
					slideDownSeed=setting.slideDownSeed,
				panes=this.getPanes();
				panes.slideUp(slideUpSeed);
				pane.slideDown(slideDownSeed,done);
			}
		},
		debug;
	function Tabs(tabsSelector,panesSelector,setting){
		var self=this,
			tabs=tabsSelector.find(setting.tabs),
			panes=panesSelector.find(setting.panes),
			trigger=tabsSelector.add(this),
			current;
		$.extend(this,{
			click:function(e,i){
				var tab=tabs.eq(i),
					pane,
					a=tab.find("[href]");

				if(setting.rotate){
					var last=tabs.length-1;
					if(i>last){return self.click(e,0);}
					else if(i<0){return self.click(e,last)}
				}

				if(!tab.length){
					if(current>=0)return self;
					i=setting.initialIndex;
					tab=tabs.eq(i);
				}

				if(i===current)return self;
				e = e || $.Event();
				e.type = "onBeforeClick";
				trigger.trigger(e,[i]);
				if (e.isDefaultPrevented()){return;}
				
				if(!!a.length){
					var link=a.attr("href"),reg=/^#/,isAnchor=reg.test(link);
					if(isAnchor){
						pane=$(link).parent()
					}else{
						pane=panes.eq(i);
						self.ajax(pane,link);
					}
				}else{
					pane=panes.eq(i);
				}
				effects[setting.effect].call(self, pane, function() {
					current = i;
					e.type = "onClick";
					trigger.trigger(e,[i]);
				});
				tabs.removeClass(setting.current);
				tab.addClass(setting.current);
			},
			ajax:function(pane,link){
				if(!setting.onAjax||typeof setting.onAjax !=="function"){
					pane.load(link);
					return;
				}
				setting.onAjax.call(self,pane,link);
			},
			getSetting: function(){
				return setting;
			},
			getTabs: function(){
				return tabs;
			},
			getPanes: function(){
				return panes;
			},
			getCurrentPane: function(){
				return panes.eq(current);
			},
			getCurrentTab: function(){
				return tabs.eq(current);
			},
			getCurrent: function(){
				return current;
			},
			next: function(){
				return self.click(undefined,current + 1);
			},
			prev: function(){
				return self.click(undefined,current - 1);
			},
			destroy:function(){
				tabs.unbind(setting.event).removeClass(setting.current);
				//panes.find("a[href^=#]").unbind("click.T");
				return self
			},
			addEffect:function(name, fn) {
				effects[name] = fn;
			}
		});
		$.each("onBeforeClick,onClick".split(","), function(i, name) {
			if ($.isFunction(setting[name])) {
				$(self).bind(name, setting[name]); 
			}
			// api
			self[name] = function(fn) {
				if (fn) { $(self).bind(name, fn); }
				return self;	
			};
		});
		tabs.each(
			function(i){
				$(this).bind(setting.event,function(e){
						self.click(e,i);
						$("a",this).blur();
						return e.preventDefault();
					}
				)
			}
		)
		if (setting.initialIndex >= 0)self.click(null,setting.initialIndex);
	}
	$.fn.tabs=function(panesSelector,setting){
		var self=this,el,panes=$(panesSelector);
		if($.isPlainObject(setting)||!setting)setting=$.extend({},_setting,setting);
		if(setting.debug){
			var debug={
				data:[],
				_error:function(msg){
					this.data.push("error:"+msg)
				},
				_log:function(msg){
					this.data.push("log:"+msg)
				}
			};
		}
		if(self.length==1&&panes.length==1){
			el=self.data("tabs");
			if(el){
				self.removeData("tabs");
			}
			el=new Tabs(self,panes,setting);
			self.data("tabs",el);
			return setting.api?el:self;
		}else{
			var msg=self.length==1?"panes选择器错误":"tabs选择器错误";
			debug._error(msg);
			return;
		}
	}
})(jQuery)