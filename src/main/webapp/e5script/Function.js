/**
 * =====Function.js方法列表=================
 * doInitSelection(e, strValue):按某值对一个select下拉框进行初始化
 * initSelectionByText(e, strText):对一个select下拉框进行初始化,按Text值
 * getWorkspaceFrame()：找到工作平台主窗口
 * getDataProvider(dataUrl, loadfunction, isXML, isAnsync)：xmlhttp调用方法
 * getDataProviderOri(dataUrl, loadfunction, isXML, isAnsync)：dojo的xmlhttp调用方法，已经废弃
 * isInt(x)：判断是否整数, 可以为空
 * isDouble(x)：判断是否浮点数, 可以为空
 * getLength(x)：取得x的unicode长度,ascii长度=1，中文长度=3
 * isDate(DateString, Dilimeter)：判断是否日期型。要求是年月日的顺序，分隔符可以不同，如 yyyy-mm-dd , yyyy/mm/dd
 * transformCatIDs(catIDs, direct)：处理分类ID的格式。当调用分类选择树时使用，在级联ID串的格式等方面有不同
 * contextControl()：页面右键控制,当没有ctrl时不能显示右键菜单
 * encodeSpecialCode(param1)：对特殊字符和中文编码
 * GetFrameByName(w,name1)：查找窗口内某名字的Frame
 * getIframe(name)：按ID得到窗口内的iframe对象
 * doSetCookie(sName, sValue)：保存cookie
 * doGetCookie(strName)：读cookie
 * trim(str)：去掉字符串开始和结尾的空格
 * isIE()：判断是否IE浏览器
 * showAtElement(target, el):把一个HTML元素target在另一个HTML元素el的下方对齐显示，如把日历显示在日期框下面
 *
 * autoLimitInputs() ： 自动查找页面内的全部文本输入框，对有maxLength限制的进行约束绑定。
 * setLimitInput(id) : 限制文本输入框的输入字符串个数
 * 
 * formatMoney: Money值四舍五入，保留小数点后两位。formatMoney(12345.678)== 12345.68
 * formatNumber : 对浮点数进行四舍五入，按需要保留小数位数。formatNumber(12345.678, 2) == 12345.68
 * =========================================
 */

/**
 * 按某值对一个select下拉框进行初始化
 */
function doInitSelection(e, strValue)
{
	if (!e || !strValue) return;

	for (var i = 0; i < e.options.length; i++)
	{
		if (e.options[i].value == strValue)
		{
			e.selectedIndex = i;
			break;
		}
	}
}
/**
 * 对一个select下拉框进行初始化,按Text值

 */
function initSelectionByText(e, strText)
{
	if (!e || !strText) return;

	for (var i = 0; i < e.options.length; i++)
	{
		if (e.options[i].text == strText)
		{
			e.selectedIndex = i;
			break;
		}
	}
}
/**
 * 找到工作平台主窗口
 * 按addListener函数找
 * 该方法用在工作平台的各Frame中，用来找到上层窗口
 */
function getWorkspaceFrame() {
	var frmTop = window;
	while (!frmTop.addListener){
		if (frmTop == frmTop.parent) break;
		frmTop = frmTop.parent;
	}
	return frmTop;
}

/**
 * 新的xmlhttp调用方法，不依赖于dojo
 * @param dataUrl : 需要请求的地址
 * @param loadfunction : 当请求返回时，处理返回数据的方法名
 * @param isXML:返回的数据是否xml格式
 * @param isAnsync：调用时是否异步，缺省时表示同步调用
 */
function getDataProvider(dataUrl, loadfunction, isXML, isAnsync) {
	var xmlHttp = XmlHttp.create();
	
	var bSync = true;
	if (isAnsync) bSync = false;
	
	xmlHttp.open("GET", dataUrl, !bSync);
	xmlHttp.onreadystatechange = function () {
		if (xmlHttp.readyState == 4) {
			if (loadfunction) {
				if (isXML)
					loadfunction(null, xmlHttp.responseXML);
				else
					loadfunction(null, xmlHttp.responseText);
			}
		}
	};
	xmlHttp.send(null);
}

function XmlHttp() {}
XmlHttp.create = function () {
	try {
		if (window.ActiveXObject) return new ActiveXObject("MsXML2.XmlHttp");
	} catch (ex) {}
		
	try {
		if (window.ActiveXObject) return new ActiveXObject("Microsoft.XmlHttp");
	} catch (ex) {}
		
	try {
		if (window.XMLHttpRequest) return new XMLHttpRequest();
	} catch (ex) {}
		
	throw new Error("Your browser does not support XmlHttp objects");
};

// Integer? 可以为空
function isInt(x){
	if (!x) return true;

	var i, y;
	for (i = 0; i < x.length; i++){
		y = x.charCodeAt(i);
		if ((y > 57) || (y < 48))
			return false;
	}
	return true;
}
//Double? 可以为空
function isDouble(x){
	if (!x) return true;

	var i, y;
	var dotCount = 0;
	var eCount = 0;
	for (i = 0; i < x.length; i++){
		y = x.charCodeAt(i);
		if (46 == y){ //'.'
			dotCount++;
			continue;
		}
		if (69 == y){ //'E'
			eCount++;
			continue;
		}
		if ((y > 57) || (y < 48))
			return false;
	}
	if (dotCount > 1) return false;
	if (eCount > 1) return false;
	return true;
}
/**
 * 取得x的unicode长度,ascii长度=1，中文长度=3
 */
function getLength(x)
{
  var i,y,n;
  n = 0;
  if (x == '') return n;

  for (i=0; i< x.length; i++)
  {
		y= x.charCodeAt(i);
		if (y<=128)
			n++;
    else
      n = n + 3;
	}
  return n;
}
/**
 * 判断是否日期型
 * 要求是年月日的顺序，分隔符可以不同
 * 如 yyyy-mm-dd , yyyy/mm/dd
 */
function isDate(DateString, Dilimeter)
{
	if  (DateString == null)  return  false;
	if  (DateString.length < 8 || DateString.length>10) return  false;

	if  (Dilimeter == '' || Dilimeter == null) Dilimeter = '-';

	var  tempArray = DateString.split(Dilimeter);
	if  (tempArray.length!=3)  return false;

	var  tempy = tempArray[0];
	var  tempm = tempArray[1];
	var  tempd = tempArray[2];

	var  tDateString = tempy + '/' + tempm + '/' + tempd;
	var  tempDate = new  Date(tDateString);

	if  (isNaN(tempDate))  return  false;

	if  ((tempDate.getFullYear() == tempy)
		&&  (tempDate.getMonth() == myparseInt(tempm) - 1)
		&&  (tempDate.getDate() == myparseInt(tempd)))
		return  true;
	else return  false;
}

//去掉月份或日期前的0，比如2003-02-04  ->2003-2-4
function  myparseInt(num)
{
	var  tempnum = num  +  "";
	while(tempnum.substr(0,1) == "0")  {
		tempnum = tempnum.substr(1);
	}
	return(parseInt(tempnum));
}

/**
 * 处理分类ID
 * direct = 0: 在数据库中以21_233_222;11_333_22的形式存在
 * 在传递给分类选择时，改成222,22。只要最后的ID
 * direct = 1: 从分类选择返回时，级联串是21~233~222,11~333~22的形式
 * 改成21_233_222;11_333_22的形式
 */
function transformCatIDs(catIDs, direct)
{
	if (!catIDs) return catIDs;

	if (direct) { //从分类选择返回
		catIDs = catIDs.replace(/~/g, "_");
		catIDs = catIDs.replace(/,/g, ";");
	}
	else {
		var catIDArr = catIDs.split(";");
		catIDs = "";
		for (var i = 0; i < catIDArr.length; i++)
		{
			var idArr = catIDArr[i].split("_");
			if ((idArr.length > 0) && idArr[idArr.length - 1])
				catIDs += idArr[idArr.length - 1] + ",";
		}
		if (catIDs) catIDs = catIDs.substring(0, catIDs.length - 1);
	}
	return catIDs;
}
/**
 * 页面右键控制,当没有ctrl时不能显示右键菜单
 */
function contextControl()
{
	if (!event.ctrlKey)
		return false;
}
/**
 * 对特殊字符和中文编码
 */
function encodeSpecialCode(param1)
{
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
}
/** 把一个HTML元素target在另一个HTML元素el的下方对齐显示，如把日历显示在日期框下面 */
function showAtElement(target, el, widthAlign) {
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
}
function _showAt(target, x, y, w) {
	var s = target.style;
	s.left = x + "px";
	s.top = y + "px";
	if (w) s.width = w;
	
	target.style.display = "block";
}

/**
 * 查找窗口内某名字的Frame
 */
function GetFrameByName(w,name1)
{
	var f = w.frames;
	if(f == null)
		return null;
	var i;
	var ww;
	var www;
	for(i = 0;i < f.length;i ++)
	{
		ww = f[i];
		if(name1 == ww.name) return ww;

		www = GetFrameByName(ww,name1);
		if(www != null) return www;
	}
	return null;
}

/**
 * 查找本窗口内某名字的Iframe
 * 需要用document.getElementById，不能使用window.frames("")
 * 因为FireFox中不认


 */
function getIframe(name)
{
	return document.getElementById(name);
}
/**
 * 在Cookie中设置某值，保持一个月
 */
function doSetCookie(sName, sValue)
{
	var date = new Date();
	date.setMonth(1 + parseInt(date.getMonth()));

	if (sValue == "") sValue = "0";
	document.cookie = sName + "=" + escape(sValue) + ";expires=" + date.toGMTString() + ";path=/";
}

/**
 * 从Cookie中提取某值


 */
function doGetCookie(strName)
{
	var theValue = null;
	var aCookie = document.cookie.split("; ");

	for (var i=0; i < aCookie.length; i++)
	{
		var aCrumb = aCookie[i].split("=");
		if (strName == aCrumb[0]) theValue = unescape(aCrumb[1]);
	}
	if (theValue == "0") theValue = "";
	return theValue;
}
function trim(str)
{
	var ch;
	var string="";
	var min=0;
	var max=str.length-1;
	ch=str.charAt(min);

	while(ch==' ')
	 {
		 min++;
		 ch=str.charAt(min);
	  }
	 ch=str.charAt(max);

	 while(ch==' ')
	  {
		 max--;
		 ch=str.charAt(max);
	  }

	if(min>max)
		 string="";
	 else
		string=str.substring(min,max+1);
	return string;
}
/**
 * 脚本错误时的错误窗口处理
 */
window.onerror = doError;
document._error_messages = new Array();

var errorWindow;

function doError(msg,url,ln) {
	var _error_obj = new Object();
	_error_obj.msg = msg;
	_error_obj.url = url;
	_error_obj.ln = ln;

	document._error_messages[document._error_messages.length] = _error_obj;

	str = ""
	str += "<title>Error Dialogue</title>"
	str += "<scr" + "ipt>window.onload=new Function('showError()');"
	str += 'var nr=0;'
	str += 'function next() {'
	str += '   nr=Math.min(window.opener.document._error_messages.length-1,nr+1);'
	str += '   showError();'
	str += '}'
	str += 'function previous() {'
	str += '   nr=Math.max(0,nr-1);'
	str += '   showError();'
	str += '}'
	str += 'function showError() {'
	str += '   errorArray = window.opener.document._error_messages;'
	str += '   if (errorArray.length != 0 && nr >= 0 && nr < errorArray.length) {'
	str += '      url.innerHTML = errorArray[nr].url;'
	str += '      msg.innerHTML = errorArray[nr].msg;'
	str += '      ln.innerHTML = errorArray[nr].ln;'
	str += '   }'
	str += '}</script>'
	str += "<style>"
	str += "body {background: white; color: black; border: 5 solid gray; font-family: tahoma, arial, helvitica; font-size: 12px; margin: 0;}"
	str += "p {font-family: tahoma, arial, helvitica; font-size: 12px; margin-left: 10px; margin-right: 10px;}"
	str += "h1	{font-family: arial black; font-style: italic; margin-bottom: -15; margin-left: 10; color:navy}"
	str += "button {margin: 0; border: 1 solid #dddddd; background: #eeeeee; color: black; font-family: tahoma, arial; width: 100}"
	str += "a {color: navy;}"
	str += "a:hover {color: blue;}"
	str += "</style>"
	str += '<body scroll="no">'
	str += "<h1>Error!</h1>"
	str += '<p>URL:<strong id="url"></strong></p>'
	str += '<table style="width: 100%;" cellspacing=0 cellpadding=10><tr><td>'
	str += '<button onclick=\'if (infoArea.style.display!="block") {infoArea.style.display = "block";window.resizeTo(400,308);this.innerHTML="Hide Error";}else {infoArea.style.display="none";window.resizeTo(400,219);this.innerHTML="Show Error";}\''
	str += 'onmouseover="this.style.borderColor=\'black\'" onmouseout="this.style.borderColor=\'#dddddd\'">Hide Error</button>'
	str += '</td><td align="RIGHT"><button onclick="window.close()" onmouseover="this.style.borderColor=\'black\'" onmouseout="this.style.borderColor=\'#dddddd\'">Ok</button>'
	str += '</td></tr></table>'
	str += '<div id="infoArea" style="display: block;">'
	str += '<div id="info" style="background: #eeeeee; margin: 10; margin-bottom: 0; border: 1 solid #dddddd;">'
	str += '<table>'
	str += '<tr><td><p>Message:</p></td><td><p id="msg"></p></td></tr>'
	str += '<tr><td><p>Line:</p></td><td><p id="ln"></p></td></tr>'
	str += '</table>'
	str += '</div>'
	str += '<table style="width: 100%;" cellspacing=0 cellpadding=10><tr><td>'
	str += '<button onclick="previous()" onmouseover="this.style.borderColor=\'black\'" onmouseout="this.style.borderColor=\'#dddddd\'"><<</button>'
	str += '</td><td align=right><button onclick="next()" onmouseover="this.style.borderColor=\'black\'" onmouseout="this.style.borderColor=\'#dddddd\'">>></button>'
	str += '</td></tr></table>'
	str += '</div>'

	str += '</body>'

	if (!errorWindow || errorWindow.closed) {
//		errorWindow = window.open("","JSErrorWindow","width=390,height=190");
		errorWindow = window.open("","JSErrorWindow","width=400,height=308");
		var d = errorWindow.document;
		d.open();
		d.write(str);
		d.close();
		errorWindow.focus();
	}
	return true;
}

function isIE()
{
	return navigator.appName.indexOf("Microsoft")!= -1;
}

//-----------以下是限制输入框中字符个数的方法------
function autoLimitInputs()
{
	var inputs = document.getElementsByTagName("input");
	for(var i = 0; i < inputs.length; i++)
	{
		var txt = inputs[i];
		if (txt && (parseInt(txt.getAttribute("maxLength")) < 20000))
			setLimitInput(txt);
	}
}
function setLimitInput(txt)
{
	if (txt)
		txt.attachEvent("onpropertychange", Text_OnChanged);
}

var SingleByteLen = 1;
var MultiByteLen = 3;
var SourceDocument;

function IsMultiByte(Value) {
    return (Value > "~");
}

function GetLength(Value) {
    var len = 0;
    for(var n = 0;n < Value.length;n++) {
        if (IsMultiByte(Value.charAt(n))) {
            len += MultiByteLen;
        } else {
            len += SingleByteLen;
        }
    }
    return len;
}

function Text_OnChanged(e) {
    var src;
    var len;
    e.srcElement.detachEvent("onpropertychange", Text_OnChanged);
    if (e.propertyName == "value") {
        src = e.srcElement;
        len = GetLength(src.value);
        if (len > src.maxLength) {
			src.value = src.orgValue;
			src.attachEvent("onpropertychange", Text_OnChanged);
            return;
        }
        src.orgValue = src.value;
    }
    src.attachEvent("onpropertychange", Text_OnChanged);
}
//----------end.-限制输入框中字符个数的方法------

/**
 * Money值四舍五入，保留小数点后两位
 * Usage:  formatMoney(12345.678);
 * result: 12345.68
 */
function formatMoney(amount) {
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
}
 
/**
 * 对浮点数进行四舍五入，按需要保留小数位数
 *   Usage:  formatNumber(12345.678, 2);
 *   result: 12345.68
 */
function formatNumber(pnumber,decimals){
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
}



//----------计算高度的方法----------//

function iframeAutoHeight(id){
    var doc = document,
        p = window;
    while(p = p.parent){
        var frames = p.frames,
            frame,
            i = 0;
        while(frame = frames[i++]){
            if(frame.document == doc){
				var iheight = Math.max(idoc.body.scrollHeight, idoc.documentElement.scrollHeight); //取得其高
				iframe.style.height = iheight + "px";
                frame.frameElement.style.height = doc.body.scrollHeight;
                doc = p.document;
                break;
            }
        }
        if(p == top){
            break;
        }
    }
}

function adjustIframeHOnce(){
    var doc = document,
        p = window.parent,
        iframes = p.frames,
        iframe,
        i = 0;
        while(iframe = iframes[i++]){
            if(iframe.document == doc){
            	var iheight = doc.body.scrollHeight;//Math.max(doc.body.scrollHeight, doc.documentElement.scrollHeight);//
                iframe.frameElement.style.height = iheight + "px";                
                break;
			}
		}        
}
function adjustIframeH(id){
	if(typeof id =="object")var iframe = id;
	else var iframe = document.getElementById(id);
	var idoc = iframe.contentWindow && iframe.contentWindow.document || iframe.contentDocument;	
	var iheight = Math.max(idoc.body.scrollHeight, idoc.documentElement.scrollHeight);
	iframe.style.height = iheight + "px";
}
function adjustIframeHT(s){
	setTimeout(function(){adjustIframeH(s)},500)
}

var getTextNum = (function () {
	var count = function (h) {
		var y,len=0,charLen=0;
		for(var i=0; i< h.length; i++){
			y= h.charCodeAt(i);
			if (y > 0 && y < 128){
				charLen++;
				if(charLen%3==1){//3个字符算一个字
					len++;
				}
			}else len++;
		}
		return len;
	}
	return function (max,num,q) {
		var s = max || 140,
			p = count(q),
			j = s-p,
			span = num.find("span").html(Math.abs(j));
		if(j>=0){
			num[0].firstChild.nodeValue = "您还可以输入";
			span.removeClass();
		}else{
			num[0].firstChild.nodeValue = "已经超过";
			span.addClass("W_error");
		}
	}
})();

