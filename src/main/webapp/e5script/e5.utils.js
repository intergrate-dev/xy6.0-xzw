e5.namespace("e5.utils",function() {
	var curColourIndex = 1, maxColourIndex = 24;
	/** 对特殊字符和中文编码 */
	var encodeSpecialCode = function(param1){
		if (!param1) return "";

		var res = "";
		for(var i = 0;i < param1.length;i ++){
			switch (param1.charCodeAt(i)){
				case 0x20://space
				case 0x3f://?
				case 0x23://#
				case 0x26://&
				case 0x22://"
				case 0x27://'
				case 0x2a://*
				case 0x3d://=
				case 0x5c:// \
				case 0x2f:// /
				case 0x2e:// .
				case 0x25:// .
					res += escape(param1.charAt(i));
					break;
				case 0x2b:
					res += "%2b";
					break;
				default:
					res += encodeURI(param1.charAt(i));
			}
		}
		return res;
	},
	/** 把一个HTML元素target在另一个HTML元素el的下方对齐显示，如把日历显示在日期框下面 */
	showAtElement = function(target, el, widthAlign) {
		function Pos (x, y) {
			this.x = x;
			this.y = y;
		}
		function getPos(el) {
			var r = new Pos(el.offsetLeft, el.offsetTop);
			if (el.offsetParent) {
				var tmp = getPos(el.offsetParent);
				r.x += tmp.x;
				r.y += tmp.y;
			}
			return r;
		}
		var p = getPos(el);
		
		if (widthAlign)
			_showAt(target, p.x, p.y + el.offsetHeight - 1, el.scrollWidth);
		else
			_showAt(target, p.x, p.y + el.offsetHeight - 1);
	},
	_showAt = function(target, x, y, w) {
		var s = target.style;
		s.left = x + "px";
		s.top = y + "px";
		if (w) s.width = w;
		
		$(target).show();
	},
	
	/**在Cookie中设置某值，保持一个月*/
	setCookie = function(sName, sValue){
		var date = new Date();
		date.setMonth(1 + parseInt(date.getMonth()));

		if (sValue == "") sValue = "0";
		document.cookie = sName + "=" + escape(sValue) + ";expires=" + date.toGMTString() + ";path=/";
	},

	/**从Cookie中提取某值*/
	getCookie = function(strName){
		var theValue = "";
		var aCookie = document.cookie.split("; ");

		for (var i=0; i < aCookie.length; i++){
			var aCrumb = aCookie[i].split("=");
			if (strName == aCrumb[0]) theValue = unescape(aCrumb[1]);
		}
		if (theValue == "0") theValue = "";
		return theValue;
	},
	
	isIE = function(){
		return navigator.appName.indexOf("Microsoft")!= -1;
	},
	isChrome = function() {
		return (getNavigator().browser == "chrome");
	},
	//浏览器信息，返回格式{browser:chrom,ver:19.2}
	getNavigator = function(){
		var ua = navigator.userAgent.toLowerCase();
		// trident IE11
		var re =/(trident|msie|firefox|chrome|opera|version).*?([\d.]+)/;
		var m = ua.match(re);
		
		var Sys = {};
		Sys.browser = m[1].replace(/version/, "'safari");
		Sys.ver = m[2];
		return Sys;
	},
	/*
	 * 
	 * 测试正则表达式
	 */
	regExpTest = function(pattern,s){
		
		 if (!pattern.test(s)) {
			 
             return false;
         }
		 else{
			 return true;
		 }
	},
	/*
	 * 
	 * 得到系一个随机色
	 */
	getNextColour = function(){
		var R, G, B;
		R = parseInt(128 + Math.sin((curColourIndex * 3 + 0) * 1.3) * 128);
		G = parseInt(128 + Math.sin((curColourIndex * 3 + 1) * 1.3) * 128);
		B = parseInt(128 + Math.sin((curColourIndex * 3 + 2) * 1.3) * 128);
		curColourIndex = curColourIndex + 1;
		if (curColourIndex > maxColourIndex) curColourIndex = 1;
		return "rgb(" + R + "," + G + "," + B + ")";
	},
	xmlNodeText = function(oNode){
		return (oNode.text) ? oNode.text : oNode.textContent;
	},
	//设一个元素为readonly，并置灰背景色
	setReadonly = function (id) {
		var src = $("#" + id);
		src.prop("readonly", true)/*.addClass("disabled")*/;
		
		// src[0].style.backgroundColor = "#cccccc";
		
		return src;
	},
	//取消元素的readonly
	removeReadonly = function(id) {
		var src = $("#" + id);
		src.prop("readonly", false)/*.removeClass("disabled")*/;
		
		//src[0].style.backgroundColor = "white";
		
		return src;
	},

	// Money值四舍五入，保留小数点后两位。
	formatMoney = function(amount) {
		var i = parseFloat(amount);
		if(isNaN(i)) { return 0.00; }
		
		var minus = '';
		if (i < 0) { minus = '-'; }
		
		i = parseInt((Math.abs(i) + .005) * 100);
		i = i / 100;
		
		var s = new String(i);
		if (s.indexOf('.') < 0) { 
			s += '.00'; 
		} else if (s.indexOf('.') == (s.length - 2)) {
			s += '0'; 
		}
		s = minus + s;
		return s;
	},
	 
	//对浮点数进行四舍五入，按需要保留小数位数
	formatNumber = function(pnumber,decimals){
		if (isNaN(pnumber) || pnumber=='') { return 0};
		 
		var snum = new String(pnumber);
		var sec = snum.split('.');
		var whole = parseFloat(sec[0]);
		var result = '';
		 
		if (sec.length > 1){
			var dec = new String(sec[1]);
			dec = String(parseFloat(sec[1])/Math.pow(10,(dec.length - decimals)));
			dec = String(whole + Math.round(parseFloat(dec))/Math.pow(10,decimals));
			var dot = dec.indexOf('.');
			if (dot == -1){
				dec += '.';
				dot = dec.indexOf('.');
			}
			while(dec.length <= dot + decimals) { dec += '0'; }
			result = dec;
		} else{
			var dot;
			var dec = new String(whole);
			dec += '.';
			dot = dec.indexOf('.');    
			while(dec.length <= dot + decimals) { dec += '0'; }
			result = dec;
		}  
		return result;
	},
	date_monthBefore = function(interval) {
		var now = new Date();
		now.setMonth(now.getMonth() - interval);
		return dateFormat(now);
	},
	date_dayBefore = function(interval) {
		var now = new Date();
		now.setDate(now.getDate() - interval);
		return dateFormat(now);
	},
	date_now = function() {
		var now = new Date();
		return dateFormat(now);
	},
	//把一个YYYY-MM-DD格式的字符串转成Date对象
	dateParse = function(str) {
		var year = parseInt(str.substring(0, 4), 10);
		var month = parseInt(str.substring(5, 7), 10) - 1;
		var day = parseInt(str.substring(8), 10);
		
		var date = new Date(year, month, day);
		return date;
	},
	//把一个Date对象转成YYYY-MM-DD格式的字符串
	dateFormat = function(date) {
		var year = date.getFullYear();
		var month = date.getMonth() + 1;
		var day = date.getDate();
		if (month < 10) month = "0" + month;
		if (day < 10) day = "0" + day;
		
		return year + "-" + month + "-" + day;
	},
	//从Url中取一个参数的值
	getParam = function(name) {
		var params = window.location.href;
		params = params.substring(params.indexOf("?") + 1, params.length);
		params = params.split("&");
		
		for (var i = 0; i < params.length; i++) {
			var arr = params[i].split("=");
			if (arr[0] == name) {
				return params[i].substring(name.length + 1, params[i].length);
			}
		}
		return null;
	}
	;
	return {
		encodeSpecialCode : encodeSpecialCode,
		setCookie : setCookie,
		getCookie : getCookie,
		isIE : isIE,
		isChrome : isChrome,
		
		regExpTest:regExpTest,
		getNextColour:getNextColour,
		xmlNodeText : xmlNodeText,
		showAtElement: showAtElement,
		setReadonly : setReadonly,
		removeReadonly : removeReadonly,
		formatMoney : formatMoney,
		formatNumber : formatNumber,
		
		dateParse : dateParse,
		dateFormat : dateFormat,
		date_monthBefore : date_monthBefore,
		date_dayBefore : date_dayBefore,
		date_now : date_now,
		
		getParam : getParam
	};
});