/*===============================================
		cssexpr.js
		merge into the xmenu.js
 ===============================================
function constExpression(x) {
	return x;
}
function simplifyCSSExpression() {
	try {
		var ss,sl, rs, rl;
		ss = document.styleSheets;
		sl = ss.length

		for (var i = 0; i < sl; i++) {
			simplifyCSSBlock(ss[i]);
		}
	}
	catch (exc) {
		alert("Got an error while processing css. The page should still work but might be a bit slower");
		throw exc;
	}
}
function simplifyCSSBlock(ss) {
	var rs, rl;

	for (var i = 0; i < ss.imports.length; i++)
		simplifyCSSBlock(ss.imports[i]);

	if (ss.cssText.indexOf("expression(constExpression(") == -1)
		return;

	rs = ss.rules;
	rl = rs.length;
	for (var j = 0; j < rl; j++)
		simplifyCSSRule(rs[j]);
}
function simplifyCSSRule(r) {
	var str = r.style.cssText;
	var str2 = str;
	var lastStr;
	do {
		lastStr = str2;
		str2 = simplifyCSSRuleHelper(lastStr);
	} while (str2 != lastStr)

	if (str2 != str)
		r.style.cssText = str2;
}
function simplifyCSSRuleHelper(str) {
	var i, i2;
	i = str.indexOf("expression(constExpression(");
	if (i == -1) return str;
	i2 = str.indexOf("))", i);
	var hd = str.substring(0, i);
	var tl = str.substring(i2 + 2);
	var exp = str.substring(i + 27, i2);
	var val = eval(exp)
	return hd + val + tl;
}
if (/msie/i.test(navigator.userAgent) && window.attachEvent != null) {
	window.attachEvent("onload", function () {
		simplifyCSSExpression();
	});
}

/*===============================================
 		XMenu
 ==============================================*/

// check browsers
var ua = navigator.userAgent;
var opera = /opera [56789]|opera\/[56789]/i.test(ua);
var ie = !opera && /MSIE/.test(ua);
var ie50 = ie && /MSIE 5\.[01234]/.test(ua);
var ie6 = ie && /MSIE [6789]/.test(ua);
var ieBox = ie && (document.compatMode == null || document.compatMode != "CSS1Compat");
var moz = !opera && /gecko/i.test(ua);
var nn6 = !opera && /netscape.*6\./i.test(ua);

/********************** Config **********************/
/**
 * define the default values
 * change to use config
 * @date 2006.02.07
 * @modifier Gong Lijie
 */
var webFXMenuConfig = {
	defaultWidth			: 100,
	defaultEmptyText		: "Empty",
	defaultUseAutoPosition	: nn6 ? false : true,

	imagePath				: "",

	defaultBorderLeft		: 1,
	defaultBorderRight		: 1,
	defaultBorderTop		: 1,
	defaultBorderBottom		: 1,
	defaultPaddingLeft		: 1,
	defaultPaddingRight		: 1,
	defaultPaddingTop		: 1,
	defaultPaddingBottom	: 1,

	defaultShadowLeft		: 0,
	defaultShadowRight		: ie && !ie50 && /win32/i.test(navigator.platform) ? 4 :0,
	defaultShadowTop		: 0,
	defaultShadowBottom		: ie && !ie50 && /win32/i.test(navigator.platform) ? 4 : 0,

	itemDefaultHeight		: 18,
	itemDefaultText			: "Untitled",
	itemDefaultHref			: null,//"javascript:void(0)",

	separatorDefaultHeight	: 6,
	useMouseOut				: true,	//2006-2-23 when menubar, then do not use mouse out

	// other global constants
	useHover				: opera ? true : false,
	hideTime				: 500,	// > 0 normally
	showTime				: 100
};

/********************** Handler **********************/

var webFXMenuHandler = {
	idCounter		:	0,
	idPrefix		:	"menu-",
	all				:	{},
	getId			:	function () { return this.idPrefix + this.idCounter++; },

	overMenuItem	:	function (oItem) {
		if (this.showTimeout != null)
			window.clearTimeout(this.showTimeout);
		if (this.hideTimeout != null)
			window.clearTimeout(this.hideTimeout);
		var jsItem = this.all[oItem.id];
		if (webFXMenuConfig.showTime <= 0)
			this._over(jsItem);
		else
			//this.showTimeout = window.setTimeout(function () { webFXMenuHandler._over(jsItem) ; }, webFXMenuConfig.showTime);
			// I hate IE5.0 because the piece of shit crashes when using setTimeout with a function object
			this.showTimeout = window.setTimeout("webFXMenuHandler._over(webFXMenuHandler.all['" + jsItem.id + "'])", webFXMenuConfig.showTime);
	},
	outMenuItem	:	function (oItem) {
		if (this.showTimeout != null)
			window.clearTimeout(this.showTimeout);
		if (this.hideTimeout != null)
			window.clearTimeout(this.hideTimeout);
		var jsItem = this.all[oItem.id];

		if (webFXMenuConfig.hideTime <= 0)
			this._out(jsItem);
		else
			//this.hideTimeout = window.setTimeout(function () { webFXMenuHandler._out(jsItem) ; }, webFXMenuConfig.hideTime);
			this.hideTimeout = window.setTimeout("webFXMenuHandler._out(webFXMenuHandler.all['" + jsItem.id + "'])", webFXMenuConfig.hideTime);
	},
	blurMenu		:	function (oMenuItem) {
		window.setTimeout("webFXMenuHandler._blur(webFXMenuHandler.all['" + oMenuItem.id + "']);", webFXMenuConfig.hideTime);
	},
	//use _blur to control 	(menuItem.subMenu == null)
	_blur	:	function (menuItem) {
		if (menuItem.subMenu)
			menuItem.subMenu.hide();
	},
	_over	:	function (jsItem) {
		//when mouse on it: first hide all other one, then show its sub
		jsItem.parentMenu.hideAllSubs();
		if (jsItem.subMenu) {
			jsItem.subMenu.show();
		}
	},
	_out	:	function (jsItem) {
		// find top most menu
		var root = jsItem;
		var m;
		if (root instanceof WebFXMenuButton)
			m = root.subMenu;
		else {
			m = jsItem;
//			m = jsItem.parentMenu;
			while (m.parentMenu != null && !(m.parentMenu instanceof WebFXMenuBar))
				m = m.parentMenu;
		}
		if (m != null)
			m.hide();
	},
	//for user call, and menu mouseout
	hideMenu	:	function (menu) {
		if (this.showTimeout != null)
			window.clearTimeout(this.showTimeout);
		if (this.hideTimeout != null)
			window.clearTimeout(this.hideTimeout);
		this.hideTimeout = window.setTimeout("webFXMenuHandler.all['" + menu.id + "'].hide()", webFXMenuConfig.hideTime);
	},
	//for user call
	showMenu	:	function (menu, src, dir) {
		if (this.showTimeout != null)
			window.clearTimeout(this.showTimeout);
		if (this.hideTimeout != null)
			window.clearTimeout(this.hideTimeout);
		if (arguments.length < 3)
			dir = "vertical";
		menu.show(src, dir);
	},
	/**
	 * get the main menu.
	 * note that the param is HTML element "<A>"
	 * for use call
	 * @author Gong Lijie
	 * @date 2005.8.23
	 */
	getMainMenu	:	function (menuItem) {
		if (!menuItem) return null;

		var jsItem = this.all[menuItem.id];
		if (!jsItem) return null;

		//while ((jsItem.parentMenu) && !(jsItem.parentMenu instanceof WebFXMenuBar))
		while (jsItem.parentMenu)
			jsItem = jsItem.parentMenu;
		return jsItem;
	},
	getArrowPath:   function(){
		return webFXMenuConfig.imagePath + "arrow.down.png";
	}
};

/**********************Class : Menu**********************/

function WebFXMenu() {
	this._menuItems	= [];
	this._subMenus	= [];
	this.id			= webFXMenuHandler.getId();
	this.top		= 0;
	this.left		= 0;
	this.shown		= false;
	this.parentMenu	= null;
	this.width			= webFXMenuConfig.defaultWidth; //2006-2-22 for change easily.
	webFXMenuHandler.all[this.id] = this;
}

WebFXMenu.prototype.emptyText		= webFXMenuConfig.defaultEmptyText;
WebFXMenu.prototype.useAutoPosition	= webFXMenuConfig.defaultUseAutoPosition;

WebFXMenu.prototype.borderLeft		= webFXMenuConfig.defaultBorderLeft;
WebFXMenu.prototype.borderRight		= webFXMenuConfig.defaultBorderRight;
WebFXMenu.prototype.borderTop		= webFXMenuConfig.defaultBorderTop;
WebFXMenu.prototype.borderBottom	= webFXMenuConfig.defaultBorderBottom;

WebFXMenu.prototype.paddingLeft		= webFXMenuConfig.defaultPaddingLeft;
WebFXMenu.prototype.paddingRight	= webFXMenuConfig.defaultPaddingRight;
WebFXMenu.prototype.paddingTop		= webFXMenuConfig.defaultPaddingTop;
WebFXMenu.prototype.paddingBottom	= webFXMenuConfig.defaultPaddingBottom;

WebFXMenu.prototype.shadowLeft		= webFXMenuConfig.defaultShadowLeft;
WebFXMenu.prototype.shadowRight		= webFXMenuConfig.defaultShadowRight;
WebFXMenu.prototype.shadowTop		= webFXMenuConfig.defaultShadowTop;
WebFXMenu.prototype.shadowBottom	= webFXMenuConfig.defaultShadowBottom;

WebFXMenu.prototype.add = function (menuItem) {
	this._menuItems[this._menuItems.length] = menuItem;
	if (menuItem.subMenu) {
		this._subMenus[this._subMenus.length] = menuItem.subMenu;
		menuItem.subMenu.parentMenu = this;
	}
	menuItem.parentMenu = this;
};

/**
 * Menu show.
 * Do not use directly. Use webFXMenuHandler.showMenu instead
 */
WebFXMenu.prototype.show = function (relObj, sDir) {
	if (this.useAutoPosition)
		this.position(relObj, sDir);

	var divElement = document.getElementById(this.id);
	divElement.style.left = opera ? this.left : this.left + "px";
	divElement.style.top = opera ? this.top : this.top + "px";
	divElement.style.visibility = "visible";
	this.shown = true;
	if (this.parentMenu)
		this.parentMenu.show();
};

/**
 * 2006.2.17 generate the HTML fragment of menu
 */
WebFXMenu.prototype.generate = function () {
	//if is ie, change to 100% to avoid the sub-menu's left position error.
	if (ie) document.write("<style>.webfx-menu a {width:100%;}</style>");
	document.write(this);
};

WebFXMenu.prototype.hide = function () {
	this.hideAllSubs();
	var divElement = document.getElementById(this.id);
	divElement.style.visibility = "hidden";
	this.shown = false;
	//ie7 bug
	divElement.style.position = "fixed";
	divElement.style.position = "absolute";
};

WebFXMenu.prototype.hideAllSubs = function () {
	for (var i = 0; i < this._subMenus.length; i++) {
		if (this._subMenus[i].shown)
			this._subMenus[i].hide();
	}
};
/**
 * use onmouseout to control the main menu hide
 * note : hidetime must > 0
 * @modifier Gong Lijie
 * @date 2005.8.24
 */
WebFXMenu.prototype.toString = function () {
	var top = this.top + this.borderTop + this.paddingTop;
	var str = "<div id='" + this.id + "' class='webfx-menu' "
					+ (webFXMenuConfig.useMouseOut ? " onmouseout=\"webFXMenuHandler.hideMenu(this);\"" : "")
					+ " style='" + "width:" + (!ieBox
						? this.width - this.borderLeft - this.paddingLeft - this.borderRight - this.paddingRight
						: this.width) + "px;"
						+ (this.useAutoPosition ? "left:" + this.left + "px;" + "top:" + this.top + "px;" : "")
						+ (ie50 ? "filter: none;" : "")
				+ "'>";

	if (this._menuItems.length == 0) {
		str +=	"<span class='webfx-menu-empty'>" + this.emptyText + "</span>";
	}
	else {
		// loop through all menuItems
		for (var i = 0; i < this._menuItems.length; i++) {
			var mi = this._menuItems[i];
			str += mi;
			if (!this.useAutoPosition) {
				if (mi.subMenu && !mi.subMenu.useAutoPosition)
					mi.subMenu.top = top - mi.subMenu.borderTop - mi.subMenu.paddingTop;
				top += mi.height;
			}
		}
	}
	str += "</div>";

	for (var i = 0; i < this._subMenus.length; i++) {
		this._subMenus[i].left = this.left + this.width - this._subMenus[i].borderLeft;
		str += this._subMenus[i];
	}
	return str;
};

/**********************Class : Menu Item**********************/

function WebFXMenuItem(sText, sHref, sToolTip, oSubMenu) {
	this.text = sText || webFXMenuConfig.itemDefaultText;
	this.href = (sHref == null || sHref == "") ? webFXMenuConfig.itemDefaultHref : sHref;
	this.subMenu = oSubMenu;
	if (oSubMenu)
		oSubMenu.parentMenuItem = this;
	this.toolTip = sToolTip;
	this.id = webFXMenuHandler.getId();
	webFXMenuHandler.all[this.id] = this;
};

WebFXMenuItem.prototype.height = webFXMenuConfig.itemDefaultHeight;

/**
 * no target
 * change href to click
 * @modifier Gong Lijie
 * @date 2005.8.24
 */
WebFXMenuItem.prototype.toString = function () {
	return	"<a id=\"" + this.id + "\""
				//+ " href=\"" + this.href + "\""
				//+ (this.target ? " target=\"" + this.target + "\"" : "")
				+ " href=\"#\""
				+ (this.href ? " onclick=\"" + this.href + ";webFXMenuHandler.outMenuItem(this);return false;\"" : "")
				+ (this.toolTip ? " title=\"" + this.toolTip + "\"" : "")
				+ " onmouseover='webFXMenuHandler.overMenuItem(this)'"
				+ (webFXMenuConfig.useHover ? " onmouseout='webFXMenuHandler.outMenuItem(this)'" : "")
				+ (this.subMenu ? " unselectable='on' tabindex='-1'" : "")
			+ ">"
			+ (this.subMenu ? "<img class='arrow' src=\"" + webFXMenuConfig.imagePath + "arrow.right.png\">" : "")
			+ this.text //image first, text next.
			+ "</a>";
};

/**********************Class : Menu Separator**********************/

function WebFXMenuSeparator() {
	this.id = webFXMenuHandler.getId();
	webFXMenuHandler.all[this.id] = this;
};
WebFXMenuSeparator.prototype.height = webFXMenuConfig.separatorDefaultHeight;
WebFXMenuSeparator.prototype.toString = function () {
	return	"<DIV id='" + this.id + "'"
			+ (webFXMenuConfig.useHover ? " onmouseover='webFXMenuHandler.overMenuItem(this)'"
				+ " onmouseout='webFXMenuHandler.outMenuItem(this)'"
				: "")
			+ "></DIV>"
};

/**********************Class : Menu Bar**********************/

function WebFXMenuBar() {
	this._parentConstructor = WebFXMenu;
	this._parentConstructor();
}
WebFXMenuBar.prototype = new WebFXMenu;
WebFXMenuBar.prototype.toString = function () {
	var str = "<div id='" + this.id + "' class='webfx-menu-bar'>";

	// loop through all menuButtons
	for (var i = 0; i < this._menuItems.length; i++)
		str += this._menuItems[i];

	str += "</div>";

	for (var i = 0; i < this._subMenus.length; i++)
		str += this._subMenus[i];

	return str;
};

/**********************Class : Menu Button**********************/

function WebFXMenuButton(sText, sHref, sToolTip, oSubMenu) {
	this._parentConstructor = WebFXMenuItem;
	this._parentConstructor(sText, sHref, sToolTip, oSubMenu);
}
WebFXMenuButton.prototype = new WebFXMenuItem;
/**
 * no target
 * no onblur
 * change href to click
 * href=#, for the menu scroll effect
 * @modifier Gong Lijie
 * @date 2005.8.24
 */
WebFXMenuButton.prototype.toString = function () {
	webFXMenuConfig.useMouseOut = false;
	return	"<a id='" + this.id + "'"
			//+ " href=\"" + this.href + "\""
			//+ (this.target ? " target=\"" + this.target + "\"" : "")
			+ " href=\"#\""
			+ (this.href ? " onclick=\"" + this.href + ";return false;\"" : " onclick=\"webFXMenuHandler.overMenuItem(this)\"")
			+ (this.toolTip ? " title=\"" + this.toolTip + "\"" : "")
			+ (webFXMenuConfig.useHover
				? (" onmouseover='webFXMenuHandler.overMenuItem(this)'"
					+ " onmouseout='webFXMenuHandler.outMenuItem(this)'"
				  )
				: (" onfocus='webFXMenuHandler.overMenuItem(this)'"
					+ (this.subMenu
						? " onblur='webFXMenuHandler.blurMenu(this)'"
						: ""
				  )
				)) +
			">"
			+ (this.subMenu ? " <img class='arrow' src=\"" + webFXMenuHandler.getArrowPath() + "\" align='absmiddle'>" : "")
			+ "&nbsp;&nbsp;"
			+ this.text
			+ "</a>";
};

/*************************** Position functions ***************************************/

function getInnerLeft(el) {
	if (el == null) return 0;
	if (ieBox && el == document.body || !ieBox && el == document.documentElement) return 0;
	return getLeft(el) + getBorderLeft(el);
}

function getLeft(el) {
	if (el == null) return 0;
	return el.offsetLeft + getInnerLeft(el.offsetParent);
}

function getInnerTop(el) {
	if (el == null) return 0;
	if (ieBox && el == document.body || !ieBox && el == document.documentElement) return 0;
	return getTop(el) + getBorderTop(el);
}

function getTop(el) {
	if (el == null) return 0;
	return el.offsetTop + getInnerTop(el.offsetParent);
}

function getBorderLeft(el) {
	return ie ?
		el.clientLeft :
		parseInt(window.getComputedStyle(el, null).getPropertyValue("border-left-width"));
}

function getBorderTop(el) {
	return ie ?
		el.clientTop :
		parseInt(window.getComputedStyle(el, null).getPropertyValue("border-top-width"));
}

function opera_getLeft(el) {
	if (el == null) return 0;
	return el.offsetLeft + opera_getLeft(el.offsetParent);
}

function opera_getTop(el) {
	if (el == null) return 0;
	return el.offsetTop + opera_getTop(el.offsetParent);
}

function getOuterRect(el) {
	return {
		left:	(opera ? opera_getLeft(el) : getLeft(el)),
		top:	(opera ? opera_getTop(el) : getTop(el)),
		width:	el.offsetWidth,
		height:	el.offsetHeight
	};
}

// mozilla bug! scrollbars not included in innerWidth/height
function getDocumentRect(el) {
	return {
		left:	0,
		top:	0,
		width:	(ie ?
					(ieBox ? document.body.clientWidth : document.documentElement.clientWidth) :
					window.innerWidth
				),
		height:	(ie ?
					(ieBox ? document.body.clientHeight : document.documentElement.clientHeight) :
					window.innerHeight
				)
	};
}

function getScrollPos(el) {
	return {
		left:	(ie ?
					(ieBox ? document.body.scrollLeft : document.documentElement.scrollLeft) :
					window.pageXOffset
				),
		top:	(ie ?
					(ieBox ? document.body.scrollTop : document.documentElement.scrollTop) :
					window.pageYOffset
				)
	};
}

/* end position functions */

WebFXMenu.prototype.position = function (relEl, sDir) {
	var dir = sDir;
	// find parent item rectangle, piRect
	var piRect;
	if (!relEl) {
		var pi = this.parentMenuItem;
		if (!this.parentMenuItem)
			return;

		relEl = document.getElementById(pi.id);
		if (dir == null)
			dir = pi instanceof WebFXMenuButton ? "vertical" : "horizontal";

		piRect = getOuterRect(relEl);
	}
	else if (relEl.left != null && relEl.top != null && relEl.width != null && relEl.height != null) {	// got a rect
		piRect = relEl;
	}
	else
		piRect = getOuterRect(relEl);

	var menuEl = document.getElementById(this.id);
	var menuRect = getOuterRect(menuEl);
	var docRect = getDocumentRect();
	var scrollPos = getScrollPos();
	var pMenu = this.parentMenu;

	if (dir == "vertical") {
		if (piRect.left + menuRect.width - scrollPos.left <= docRect.width)
			this.left = piRect.left;
		else if (docRect.width >= menuRect.width)
			this.left = docRect.width + scrollPos.left - menuRect.width;
		else
			this.left = scrollPos.left;

		if (piRect.top + piRect.height + menuRect.height <= docRect.height + scrollPos.top)
			this.top = piRect.top + piRect.height;
		else if (piRect.top - menuRect.height >= scrollPos.top)
			this.top = piRect.top - menuRect.height;
		else if (docRect.height >= menuRect.height)
			this.top = docRect.height + scrollPos.top - menuRect.height;
		else
			this.top = scrollPos.top;
	}
	else {
		if (piRect.top + menuRect.height - this.borderTop - this.paddingTop <= docRect.height + scrollPos.top)
			this.top = piRect.top - this.borderTop - this.paddingTop;
		else if (piRect.top + piRect.height - menuRect.height + this.borderTop + this.paddingTop >= 0)
			this.top = piRect.top + piRect.height - menuRect.height + this.borderBottom + this.paddingBottom + this.shadowBottom;
		else if (docRect.height >= menuRect.height)
			this.top = docRect.height + scrollPos.top - menuRect.height;
		else
			this.top = scrollPos.top;

		var pMenuPaddingLeft = pMenu ? pMenu.paddingLeft : 0;
		var pMenuBorderLeft = pMenu ? pMenu.borderLeft : 0;
		var pMenuPaddingRight = pMenu ? pMenu.paddingRight : 0;
		var pMenuBorderRight = pMenu ? pMenu.borderRight : 0;

		if (piRect.left + piRect.width + menuRect.width + pMenuPaddingRight +
			pMenuBorderRight - this.borderLeft + this.shadowRight <= docRect.width + scrollPos.left)
			this.left = piRect.left + piRect.width + pMenuPaddingRight + pMenuBorderRight - this.borderLeft;
		else if (piRect.left - menuRect.width - pMenuPaddingLeft - pMenuBorderLeft + this.borderRight + this.shadowRight >= 0)
			this.left = piRect.left - menuRect.width - pMenuPaddingLeft - pMenuBorderLeft + this.borderRight + this.shadowRight;
		else if (docRect.width >= menuRect.width)
			this.left = docRect.width  + scrollPos.left - menuRect.width;
		else
			this.left = scrollPos.left;
	}
};
