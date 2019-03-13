var webFXTreeConfig = {
	rootIcon        : 'xtree/images/foldericon.png',
	openRootIcon    : 'xtree/images/openfoldericon.png',
	folderIcon      : 'xtree/images/foldericon.png',
	openFolderIcon  : 'xtree/images/openfoldericon.png',
	fileIcon        : 'xtree/images/file.png',
	iIcon           : 'xtree/images/I.png',
	lIcon           : 'xtree/images/L.png',
	lMinusIcon      : 'xtree/images/Lminus.png',
	lPlusIcon       : 'xtree/images/Lplus.png',
	tIcon           : 'xtree/images/T.png',
	tMinusIcon      : 'xtree/images/Tminus.png',
	tPlusIcon       : 'xtree/images/Tplus.png',
	blankIcon       : 'xtree/images/blank.png',

	defaultText     : 'Tree Item',
	defaultAction   : 'javascript:void(0);',
	defaultBehavior : 'classic',
	usePersistence	: false,

	/**
	 * images root directory
	 * default target
	 * popup menu
	 * extended properties
	 * use icon directory
	 * drag & drop
	 * @modifier Gong Lijie
	 * @date 2005.8
	 */
	debug					: false,		//debug flag
	multiple				: false,		//flag for whether support multiple nodes select.
	draggable				: false,		//flag for node drag and drop
	rootPath				: '../',		//images directory, set before use xtree.
	defaultTarget			: '',			//target for <A>
	defaultClickAction		: null,			//<A>'s onclick
	defaultContextAction 	: 'javascript:void(0);',	//popup menu
	defaultDragStartAction	: null,			//drag start
	defaultDragOverAction 	: null,			//drag over
	defaultDropAction		: null,			//drop

	activeFontColor			: 'highlighttext',	//font color for node selected
	activeBackground		: 'highlight',		//background color for node selected
	inactiveFontColor		: '#FFFFFF',	//font color for node inactive
	inactiveBackground		: 'gray',		//background color for node inactive
	deselectFontColor		: 'black',		//font color for node deselected
	deselectBackground		: '',		//background color for node deselected

	getRootIcon      : function() { return webFXTreeConfig.rootPath + webFXTreeConfig.rootIcon; },
	getOpenRootIcon  : function() { return webFXTreeConfig.rootPath + webFXTreeConfig.openRootIcon; },
	getFolderIcon    : function() { return webFXTreeConfig.rootPath + webFXTreeConfig.folderIcon; },
	getOpenFolderIcon: function() { return webFXTreeConfig.rootPath + webFXTreeConfig.openFolderIcon; },
	getFileIcon      : function() { return webFXTreeConfig.rootPath + webFXTreeConfig.fileIcon; },
	getIIcon         : function() { return webFXTreeConfig.rootPath + webFXTreeConfig.iIcon; },
	getLIcon         : function() { return webFXTreeConfig.rootPath + webFXTreeConfig.lIcon; },
	getLMinusIcon    : function() { return webFXTreeConfig.rootPath + webFXTreeConfig.lMinusIcon; },
	getLPlusIcon     : function() { return webFXTreeConfig.rootPath + webFXTreeConfig.lPlusIcon; },
	getTIcon         : function() { return webFXTreeConfig.rootPath + webFXTreeConfig.tIcon; },
	getTMinusIcon    : function() { return webFXTreeConfig.rootPath + webFXTreeConfig.tMinusIcon; },
	getTPlusIcon     : function() { return webFXTreeConfig.rootPath + webFXTreeConfig.tPlusIcon; },
	getBlankIcon     : function() { return webFXTreeConfig.rootPath + webFXTreeConfig.blankIcon; },

	/**
	 * support checkbox
	 * cbRootable	:if checkbox can appear in the root
	 * cbCount		:how many checkboxes on the tree node?
	 * cbLabels		:labels for each checkbox.if checkbox count is 1 and no label set, checkbox will appear before the tree node name
	 * cbPrefix		:the name-prefix for each checkbox. eg, <input type='checkbox' id='read-...'>
	 * cbRefAttribute	:refer attribute when naming the checkbox.
	 * 			eg, use catType,  <input type='checkbox' id='read-27'>
	 * @modifier Gong Lijie
	 * @date 2006.1.24
	 */
	cbRootable		: false,
	cbCount			: 0,
	cbLabels		: [],
	cbPrefix		: [],
	cbRefAttribute	: '',

	/**
	 * support pre-select nodes the second time the tree opened.
	 * @modifier Gong Lijie
	 * @date 2006.4.6
	 */
	preselect_enable	: false,
	preselect_attr		: "",
	preselect_values	: [],

	/**
	 * call back method for dynamic load.
	 * Mainly used in xloadtree, when finish loading, call the outside method to do something.
	 * @modifier Gong Lijie
	 * @date 2006.12.11
	 */
	loadCallBack            : function(){
	}
};

var webFXTreeHandler = {
	idCounter : 0,
	idPrefix  : '', /**"tree-",*/
	all       : {},
	selected  : null,
	onSelect  : null, /* should be part of tree, not handler */
	behavior  : null,
	getId     : function() { return this.idPrefix + this.idCounter++; },

	toggle    : function (oItem) { this.all[oItem.id.replace('-plus','')].toggle(); },
	select    : function (oItem) { this.all[oItem.id.replace('-icon','')].select(); },
	focus     : function (oItem) { this.all[oItem.id.replace('-anchor','')].focus(); },
	blur      : function (oItem) { this.all[oItem.id.replace('-anchor','')].blur(); },
	keydown   : function (oItem, e) { return this.all[oItem.id].keydown(e.keyCode); },

	cookies   : new WebFXCookie(),
	insertHTMLBeforeEnd	:	function (oElement, sHTML) {
		if (oElement.insertAdjacentHTML != null) {
			oElement.insertAdjacentHTML("BeforeEnd", sHTML)
			return;
		}
		var df;	// DocumentFragment
		var r = oElement.ownerDocument.createRange();
		r.selectNodeContents(oElement);
		r.collapse(false);
		df = r.createContextualFragment(sHTML);
		oElement.appendChild(df);
	},

	/**
	 * 2005.8.17 : Add get prefix length
	 * 2005.8.22 : Add multiple seleted
	 * 2005.8.23 : Add getNode (by anchor id)
	 * 2005.8.25 : Drag & Drop
	 * -----and others...
	 * @updater Gong Lijie
	 */
	multi_selected  : [],	//multiple selected nodes array
	drag_src		: null,	//the selected one for drag & drop
	drag_dest		: null,	//the selected one for drag & drop
	getPrefixLength : function() { return idPrefix.length;},
	//multiple add
	addSelected		: function(oTreeItem) {
		for (var i = 0; i < this.multi_selected.length; i++)
			if (this.multi_selected[i].id == oTreeItem.id) return;
		this.multi_selected[this.multi_selected.length] = oTreeItem;
	},
	//multiple remove
	removeSelected	: function(oTreeItem) {
		for (var i = 0; i < this.multi_selected.length; i++)
			if (this.multi_selected[i].id == oTreeItem.id) {
				for (var i = 0; i < this.multi_selected.length; i++) {
					if (this.multi_selected[i].id == oTreeItem.id) {
						for (var j = i; j < this.multi_selected.length; j++) {
							this.multi_selected[j] = this.multi_selected[j+1];
						}
						this.multi_selected.length -= 1;
						break;
				}	}
				return true;
			}
		return false;
	},
	//get node by anchor id, used when anchor click finished and callback.
	getNode		: function(id) {
		return this.all[id.replace('-anchor','')];
	},
	//get anchor by node id.
	getAnchor   : function(id) {
		return document.getElementById(id + '-anchor');
	},
	//get plus node(expand) by node id.
	getPlus   : function(id) {
		return document.getElementById(id + '-plus');
	},

	dragStart	: function(oItem) {
		var treeItem = this.all[oItem.id.replace('-icon','').replace('-anchor', '')];
		webFXTreeHandler.drag_src = treeItem;
	},
	dragOver	: function(oItem) {
	},
	//judge before drop, record the drag destination
	isDroppable	: function(oItem) {
		var treeItem = this.all[oItem.id.replace('-icon','').replace('-anchor', '')];

		//same node, don't drop
		if (webFXTreeHandler.drag_src == treeItem) return false;

		//parent not change, don't drop
		var parentNode = webFXTreeHandler.drag_src.parentNode;
		if (parentNode.id == treeItem.id) return false;

		//src is dest's ancestor, don't drop
		if (webFXTreeHandler.isAncestor(webFXTreeHandler.drag_src, treeItem))
			return false;

		webFXTreeHandler.drag_dest = treeItem;
		return true;
	},
	//after the user event, do the tree node exchange.
	finishDrag	: function() {
		if (webFXTreeHandler.drag_src && webFXTreeHandler.drag_dest)
			webFXTreeHandler.drag_src.moveTo(webFXTreeHandler.drag_dest);
	},
	//when drag, judge if src is dest's ancestor
	isAncestor	: function(srcItem, destItem) {
		var item = destItem;
		while (item.parentNode) {
			item = item.parentNode;
			if (item.id == srcItem.id)
				return true;
		}
		return false;
	},
	//add checkbox before the node name(only valid for one checkbox without name.)
	checkboxInsertBefore	: function(oItem) {
		var retValue = "";
		if (webFXTreeConfig.cbCount == 0 || oItem.noCheckbox) return retValue;

		if ((webFXTreeConfig.cbCount == 1)
			&& ((webFXTreeConfig.cbLabels == null)
				|| (webFXTreeConfig.cbLabels.length == 0)))
		{
			var name = webFXTreeConfig.cbPrefix[0] + "-" + oItem.getAttribute(webFXTreeConfig.cbRefAttribute);
			retValue = "<input type=\"checkbox\" class=\"webfx-tree-checkbox webfx-tree-checkbox0\""
				+ " name=\"" + name + "\"";
			var checked = oItem.getAttribute(name);
			if (checked != null && (checked == "1" || checked=="true"))
				retValue += " checked";
			retValue += "/>";
		}
		return retValue;
	},
	//add checkboxes after the node name
	checkboxInsertAfter		: function(oItem) {
		var retValue = "";
		if (webFXTreeConfig.cbCount == 0 || oItem.noCheckbox) return retValue;

		if ((webFXTreeConfig.cbCount > 0)
			&& (webFXTreeConfig.cbLabels != null)
			&& (webFXTreeConfig.cbLabels.length > 0))
		{
			var refAttribute = oItem.getAttribute(webFXTreeConfig.cbRefAttribute);
			for (var i = 0; i < webFXTreeConfig.cbCount; i++)
			{
				var name = webFXTreeConfig.cbPrefix[i] + "-" + refAttribute;
				retValue += "<input type=\"checkbox\" class=\"webfx-tree-checkbox  webfx-tree-checkbox" + i + "\""
					+ " name=\"" + name + "\""
					+ " id=\"" + name + "\"";
				var checked = oItem.getAttribute(name);
				if (("true" == checked) || ("1" == checked))
					retValue += " checked";
				retValue += "/>";
				retValue += "<label for=\"" + name + "\" class=\"webfx-tree-label webfx-tree-label" + i + "\">"
					+ webFXTreeConfig.cbLabels[i] + "</label>";
			}
		}
		return retValue;
	},
	/**
	 * change a tree node text
	 */
	changeText			: function(itemID, newText) {
		if (itemID == null) return;

		var itemID = itemID.replace('-anchor','') + "-anchor";
		var theElement = document.getElementById(itemID);
		if (theElement)
			theElement.innerHTML = newText;

		var treeNode = webFXTreeHandler.getNode(itemID);
		if (treeNode)
			treeNode.text = newText;
	},
	/**
	 * pre-select nodes when open tree the second time.
	 * for all expanded.
	 */
	setAllPreselect		: function() {
		if (!webFXTreeConfig.preselect_enable) return;

		var sels = webFXTreeConfig.preselect_values;
		if ((!sels) || (sels.length == 0)) return;

		var attr = webFXTreeConfig.preselect_attr;

		for (var i in webFXTreeHandler.all)
		{
			if (webFXTreeHandler._inArr(webFXTreeHandler.all[i].getAttribute(attr), sels))
				webFXTreeHandler.all[i].focus();
		}
	},
	/**
	 * pre-select nodes when open tree the second time.
	 * for automatic expanded.
	 */
	setAutoPreselect		: function(nodeParent) {
		if (!webFXTreeConfig.preselect_enable) return;

		var sels = webFXTreeConfig.preselect_values;
		if ((!sels) || (sels.length == 0)) return;

		var attr = webFXTreeConfig.preselect_attr;

		var sel_count = nodeParent.childNodes.length;
		for (var i = 0; i < sel_count; i++)
		{
			if (webFXTreeHandler._inArr(nodeParent.childNodes[i].getAttribute(attr), sels))
				nodeParent.childNodes[i].focus();
		}
	},
	_inArr	: function(a, arrs)
	{
		if (!a) return false;
		for (var i = 0; i < arrs.length; i++)
			if (arrs[i] == a) return true;
		return false;
	}
};

/*
 * WebFXCookie class
 */

function WebFXCookie() {
	if (document.cookie.length) { this.cookies = ' ' + document.cookie; }
}

WebFXCookie.prototype.setCookie = function (key, value) {
	document.cookie = key + "=" + escape(value);
}

WebFXCookie.prototype.getCookie = function (key) {
	if (this.cookies) {
		var start = this.cookies.indexOf(' ' + key + '=');
		if (start == -1) { return null; }
		var end = this.cookies.indexOf(";", start);
		if (end == -1) { end = this.cookies.length; }
		end -= start;
		var cookie = this.cookies.substr(start,end);
		return unescape(cookie.substr(cookie.indexOf('=') + 1, cookie.length - cookie.indexOf('=') + 1));
	}
	else { return null; }
}

/**
 * WebFXTreeAbstractNode class
 * 2005.8.17 : popup menu, extended properties
 * 2005.8.18 : click & contextmenu
 * 2005.8.25 : drag & drop
 * @modifier Gong Lijie
 */
function WebFXTreeAbstractNode(sText, sAction) {
	this.childNodes  = [];
	this.id     = webFXTreeHandler.getId();
	this.text   = sText || webFXTreeConfig.defaultText;
	this.action = sAction || webFXTreeConfig.defaultAction; //A's href, useless now.
	this._last  = false;
	this.noCheckbox = false;//when use checkbox,set "noCheckbox" to true will remove the node check.

	webFXTreeHandler.all[this.id] = this;

	this.target = webFXTreeConfig.defaultTarget;				//'', no <A>'s href,useless now.
	this.exProperties = '';
	this._properties = {};	//storage for extended properties. for read and write.

	this.click   = webFXTreeConfig.defaultClickAction;;			//null
	this.contextAction = webFXTreeConfig.defaultContextAction;	//void(0)
	if (webFXTreeConfig.draggable)
	{
		this.dragStartAction 	= webFXTreeConfig.defaultDragStartAction;	//void(0)
		this.dragOverAction 	= webFXTreeConfig.defaultDragOverAction;	//void(0)
		this.dropAction 		= webFXTreeConfig.defaultDropAction;		//void(0)
	}
}

/**
 * 2005.8.18 : Add extended properties
 * @author Gong Lijie
 */
WebFXTreeAbstractNode.prototype.setAttribute = function(sKey, sValue)
{
	if (this._properties[sKey] == null)
	{
		this.exProperties += " " + sKey + "=\"" + treeEncode(sValue) + "\"";
		this._properties[sKey] = sValue;
	}
}

//把"转码，以便exProperties中不出错
function treeEncode(param1)
{
	if (!param1) return "";
	
	var res = "";
	for(var i = 0;i < param1.length;i ++){
		switch (param1.charCodeAt(i)){
			case 0x22://"
				res += "&quot;";
				break;
			default:
				res += param1.charAt(i);
		}
	}
	return res;
}

/**
 * 2006.1.24 : get Attribute
 * @author Gong Lijie
 */
WebFXTreeAbstractNode.prototype.getAttribute = function(sKey) {
	return this._properties[sKey];
}
/**
 * 2005.8.18 : user-defined id
 * @author Gong Lijie
 * @deprecated
 */
WebFXTreeAbstractNode.prototype.setID = function(sValue) {
	this.id = sValue;
}

/**
 * Node drag & drop
 * @author Gong Lijie
 * @date 2005.8.25
 */
WebFXTreeAbstractNode.prototype.moveTo = function(dest) {
	//1. Remove from its parent node
	this.doRemove(false);
	//2. Add to dest. Set noindent to true, otherwise it will be more lines.
	//if the tree is not xloadtree, or the loadtree is loaded, doAdd.
	if ((dest.loaded == null) || dest.loaded)
		dest.add(this, true);
	//3. For xloadtree. No need now!
	//if (dest.reload) dest.reload();
}

/*
 * To speed thing up if you're adding multiple nodes at once (after load)
 * use the bNoIndent parameter to prevent automatic re-indentation and call
 * the obj.indent() method manually once all nodes has been added.
 */

WebFXTreeAbstractNode.prototype.add = function (node, bNoIndent) {
	this.childNodes[this.childNodes.length] = node;
	node.parentNode = this;
	if (this.childNodes.length >= 2) {
		this.childNodes[this.childNodes.length - 2]._last = false;
	}
	var root = this;
	while (root.parentNode) { root = root.parentNode; }
	if (root.rendered) {
		if (this.childNodes.length >= 2) {
			var oldLastChild = this.childNodes[this.childNodes.length - 2];
			document.getElementById(oldLastChild.id + '-plus').src = ((oldLastChild.folder)?((oldLastChild.open)?webFXTreeConfig.getTMinusIcon():webFXTreeConfig.getTPlusIcon()):webFXTreeConfig.getTIcon());
			oldLastChild.plusIcon = webFXTreeConfig.getTPlusIcon();
			oldLastChild.minusIcon = webFXTreeConfig.getTMinusIcon();
			oldLastChild._last = false;
		}
		this._last = true; //for child(will do toString()) to use.
		var foo = this;
		while (foo.parentNode) {
			for (var i = 0; i < foo.parentNode.childNodes.length; i++) {
				if (foo.id == foo.parentNode.childNodes[i].id) { break; }
			}
			if (i == foo.parentNode.childNodes.length - 1) { foo.parentNode._last = true; }
			else { foo.parentNode._last = false; }
			foo = foo.parentNode;
		}
		webFXTreeHandler.insertHTMLBeforeEnd(document.getElementById(this.id + '-cont'), node.toString());
		if ((!this.folder) && (!this.openIcon)) {
			this.icon = webFXTreeConfig.getFolderIcon();
			this.openIcon = webFXTreeConfig.getOpenFolderIcon();
		}
		if (!this.folder) { this.folder = true; this.collapse(true); }
		if (!bNoIndent) { this.indent(); }
	}
	return node;
}
/**
 * when click + / -
*/
WebFXTreeAbstractNode.prototype.toggle = function() {
	if (this.folder) {
		if (this.open) { this.collapse(); }
		else { this.expand(); }
}	}

/**
 * when click icon(folder or file).
 * call <A>'s click
 */
WebFXTreeAbstractNode.prototype.select = function() {
	var anc = document.getElementById(this.id + '-anchor');
	if (anc["onclick"])
	{
		anc["onclick"]();
	}
}

/**
 * when another one focus
 */
WebFXTreeAbstractNode.prototype.deSelect = function() {
	//document.getElementById(this.id + '-anchor').className = 'selected-lose';
	webFXTreeHandler.selected = null;
	var theNode = document.getElementById(this.id + '-anchor');
	if (theNode)
	{
		theNode.style.background = webFXTreeConfig.deselectBackground;
		theNode.style.color = webFXTreeConfig.deselectFontColor;
	}
}
/**
 * Inactive the old seleted when multiple allowed
 * @author Gong Lijie
 * @date 2005.8.22
 */
WebFXTreeAbstractNode.prototype.inactiveSelected = function() {
	//document.getElementById(this.id + '-anchor').className = 'selected-inactive';
	var theNode = document.getElementById(this.id + '-anchor');
	if (theNode)
	{
		theNode.style.background = webFXTreeConfig.inactiveBackground;
		theNode.style.color = webFXTreeConfig.inactiveFontColor;
	}
}

/**
 * Inactive the old seleted when multiple allowed
 * The second select means deselect
 * @modifier Gong Lijie
 * @date 2005.8.22
 */
WebFXTreeAbstractNode.prototype.focus = function() {

	if (webFXTreeConfig.multiple)
	{
		//reselect = unselect
//		if ((document.getElementById(this.id + '-anchor').className == 'selected')
//			|| (document.getElementById(this.id + '-anchor').className == 'selected-inactive')) {
		if (webFXTreeHandler.removeSelected(this))
		{
			//just change the selected
			if ((webFXTreeHandler.selected) && (webFXTreeHandler.selected != this)) {
				webFXTreeHandler.selected.inactiveSelected();
			}
			//note deselect will also clear webFXTreeHandler.selected
			this.deSelect();

			return false;
		}
	}
	//old one deselect
	if ((webFXTreeHandler.selected) && (webFXTreeHandler.selected != this)) {
		//Gong Lijie : multiple seleted.
		if (!webFXTreeConfig.multiple) { webFXTreeHandler.selected.deSelect(); }
		else { webFXTreeHandler.selected.inactiveSelected(); }
	}
	webFXTreeHandler.selected = this;
	if (webFXTreeConfig.multiple) webFXTreeHandler.addSelected(this);

	if ((this.openIcon) && (webFXTreeHandler.behavior != 'classic')) {
		document.getElementById(this.id + '-icon').src = this.openIcon;
	}
	//document.getElementById(this.id + '-anchor').className = 'selected';
	document.getElementById(this.id + '-anchor').style.background = webFXTreeConfig.activeBackground;
	document.getElementById(this.id + '-anchor').style.color = webFXTreeConfig.activeFontColor;
	//document.getElementById(this.id + '-anchor').focus(); //useless
	if (webFXTreeHandler.onSelect) { webFXTreeHandler.onSelect(this); }
}

/**
 * when selected node not active.
 * only valid when use <A>'s href='...'
 */
WebFXTreeAbstractNode.prototype.blur = function() {
	if ((this.openIcon) && (webFXTreeHandler.behavior != 'classic')) {
		document.getElementById(this.id + '-icon').src = this.icon;
	}
	//document.getElementById(this.id + '-anchor').className = 'selected-inactive';
}

WebFXTreeAbstractNode.prototype.doExpand = function() {
	if (webFXTreeHandler.behavior == 'classic') {
		document.getElementById(this.id + '-icon').src = this.openIcon;
	}
	if (this.childNodes.length) {
		document.getElementById(this.id + '-cont').style.display = 'block';
	}
	this.open = true;
	if (webFXTreeConfig.usePersistence) {
		var prefixLength = webFXTreeHandler.getPrefixLength;
		webFXTreeHandler.cookies.setCookie(this.id.substr(prefixLength,this.id.length - prefixLength), '1');
}	}

WebFXTreeAbstractNode.prototype.doCollapse = function() {
	if (webFXTreeHandler.behavior == 'classic') {
		document.getElementById(this.id + '-icon').src = this.icon;
	}
	if (this.childNodes.length) {
		document.getElementById(this.id + '-cont').style.display = 'none';
	}
	this.open = false;
	if (webFXTreeConfig.usePersistence) {
		var prefixLength = webFXTreeHandler.getPrefixLength;
		webFXTreeHandler.cookies.setCookie(this.id.substr(prefixLength,this.id.length - prefixLength), '0');
}	}

WebFXTreeAbstractNode.prototype.expandAll = function() {
	this.expandChildren();
	if ((this.folder) && (!this.open)) { this.expand(); }
}

WebFXTreeAbstractNode.prototype.expandChildren = function() {
	for (var i = 0; i < this.childNodes.length; i++) {
		this.childNodes[i].expandAll();
} }

WebFXTreeAbstractNode.prototype.collapseAll = function() {
	this.collapseChildren();
	if ((this.folder) && (this.open)) { this.collapse(true); }
}

WebFXTreeAbstractNode.prototype.collapseChildren = function() {
	for (var i = 0; i < this.childNodes.length; i++) {
		this.childNodes[i].collapseAll();
} }

WebFXTreeAbstractNode.prototype.indent = function(lvl, del, last, level, nodesLeft) {
	/*
	 * Since we only want to modify items one level below ourself,
	 * and since the rightmost indentation position is occupied by
	 * the plus icon we set this to -2
	 */
	if (lvl == null) { lvl = -2; }
	var state = 0;
	for (var i = this.childNodes.length - 1; i >= 0 ; i--) {
		state = this.childNodes[i].indent(lvl + 1, del, last, level);
		if (state) { return; }
	}
	if (del) {
		if ((level >= this._level) && (document.getElementById(this.id + '-plus'))) {
			if (this.folder) {
				document.getElementById(this.id + '-plus').src = (this.open)?webFXTreeConfig.getLMinusIcon():webFXTreeConfig.getLPlusIcon();
				this.plusIcon = webFXTreeConfig.getLPlusIcon();
				this.minusIcon = webFXTreeConfig.getLMinusIcon();
			}
			else if (nodesLeft) { document.getElementById(this.id + '-plus').src = webFXTreeConfig.getLIcon(); }
			return 1;
	}	}
	var foo = document.getElementById(this.id + '-indent-' + lvl);
	if (foo) {
		if ((foo._last) || ((del) && (last))) { foo.src =  webFXTreeConfig.getBlankIcon(); }
		else { foo.src =  webFXTreeConfig.getIIcon(); }
	}
	return 0;
}

/*
 * WebFXTree class
 */

function WebFXTree(sText, sAction, sBehavior, sIcon, sOpenIcon) {
	this.base = WebFXTreeAbstractNode;
	this.base(sText, sAction);
	this.icon      = sIcon || webFXTreeConfig.getRootIcon();
	this.openIcon  = sOpenIcon || webFXTreeConfig.getOpenRootIcon();
	/* Defaults to open */
	if (webFXTreeConfig.usePersistence) {
		var prefixLength = webFXTreeHandler.getPrefixLength;
		this.open  = (webFXTreeHandler.cookies.getCookie(this.id.substr(prefixLength,this.id.length - prefixLength)) == '0')?false:true;
	} else { this.open  = true; }
	this.folder    = true;
	this.rendered  = false;
	this.onSelect  = null;
	if (!webFXTreeHandler.behavior) {  webFXTreeHandler.behavior = sBehavior || webFXTreeConfig.defaultBehavior; }
}

WebFXTree.prototype = new WebFXTreeAbstractNode;

WebFXTree.prototype.setBehavior = function (sBehavior) {
	webFXTreeHandler.behavior =  sBehavior;
};

WebFXTree.prototype.getBehavior = function (sBehavior) {
	return webFXTreeHandler.behavior;
};

WebFXTree.prototype.getSelected = function() {
	if (webFXTreeHandler.selected) { return webFXTreeHandler.selected; }
	else { return null; }
}
/**
 * Get all seleted when multiple allowed
 * @author Gong Lijie
 * @date 2005.8.22
 */
WebFXTree.prototype.getAllSelected = function() {
	if (webFXTreeHandler.multi_selected) { return webFXTreeHandler.multi_selected; }
	else { return null; }
}

WebFXTree.prototype.remove = function() { }

WebFXTree.prototype.expand = function() {
	this.doExpand();
}

WebFXTree.prototype.collapse = function(b) {
	//if (!b) { this.focus(); }
	this.doCollapse();
}

WebFXTree.prototype.getFirst = function() {
	return null;
}

WebFXTree.prototype.getLast = function() {
	return null;
}

WebFXTree.prototype.getNextSibling = function() {
	return null;
}

WebFXTree.prototype.getPreviousSibling = function() {
	return null;
}
/**
 * arrow key event
 * only valid when use <A>'s href='...'
 */
WebFXTree.prototype.keydown = function(key) {
	//->
	if (key == 39) {
		if (!this.open) { this.expand(); }
		else if (this.childNodes.length) { this.childNodes[0].select(); }
		return false;
	}
	//<-
	if (key == 37) { this.collapse(); return false; }
	//down arrow
	if ((key == 40) && (this.open) && (this.childNodes.length)) {
		this.childNodes[0].select();
		return false;
	}
	return true;
};

/*
 * 2006.2.17 : tree show
 */
WebFXTree.prototype.show = function() {
	document.write(this);
	//set pre-select nodes.
	webFXTreeHandler.setAllPreselect();
};

/*
 * 2005.8.17 : add popup menu
 * 2005.8.19 : add <A>'s click
 * 2005.8.22 : deprecate the <A>'s href, only click used.
 * @modifier Gong Lijie
 */
WebFXTree.prototype.toString = function() {
	var str = "<div id=\"" + this.id + "\" class=\"webfx-tree-item\""
					+ " ondblclick=\"webFXTreeHandler.toggle(this);\""
					+ " onkeydown=\"return webFXTreeHandler.keydown(this, event)\">"
				+ "<img id=\"" + this.id + "-icon\" class=\"webfx-tree-icon\""
					+ " src=\"" + ((webFXTreeHandler.behavior == 'classic' && this.open)?this.openIcon:this.icon) + "\""
					+ " onclick=\"webFXTreeHandler.select(this);\">"

				+ (webFXTreeConfig.cbRootable ? webFXTreeHandler.checkboxInsertBefore(this) : "")

				+ "<a id=\"" + this.id + "-anchor\"" + this.exProperties
					//+ (this.action ? " href=\"" + this.action + "\"" : "")
					//+ " onfocus=\"webFXTreeHandler.focus(this);\""
					+ " href=\"javascript:void(0);\""
					+ " onblur=\"webFXTreeHandler.blur(this);\""
					//+ (this.target ? " target=\"" + this.target + "\"" : "")
					+ (this.click
						? " onclick=\"webFXTreeHandler.focus(this); " + this.click + "\""
						: "")
					+ (this.contextAction ? " oncontextmenu=\"" + this.contextAction + "; return false;\"" : "")
					+ ">" + this.text
				+ "</a>"
				+ (webFXTreeConfig.cbRootable ? webFXTreeHandler.checkboxInsertAfter(this) : "")
			+"</div>"
			+ "<div id=\"" + this.id + "-cont\" class=\"webfx-tree-container\""
				+ " style=\"display: " + ((this.open)?'block':'none') + ";\">";
	var sb = [];
	for (var i = 0; i < this.childNodes.length; i++) {
		sb[i] = this.childNodes[i].toString(i, this.childNodes.length);
	}
	this.rendered = true;//

	var theTree = str + sb.join("") + "</div>";
	return theTree;
};

/*
 * WebFXTreeItem class
 */

function WebFXTreeItem(sText, sAction, eParent, sIcon, sOpenIcon) {
	this.base = WebFXTreeAbstractNode;
	this.base(sText, sAction);
	/* Defaults to close */
	if (webFXTreeConfig.usePersistence) {
		var prefixLength = webFXTreeHandler.getPrefixLength;
		this.open = (webFXTreeHandler.cookies.getCookie(this.id.substr(prefixLength,this.id.length - prefixLength)) == '1')?true:false;
	} else { this.open = false; }
	if (sIcon) { this.icon = sIcon; }
	if (sOpenIcon) { this.openIcon = sOpenIcon; }
	if (eParent) { eParent.add(this); }
}

WebFXTreeItem.prototype = new WebFXTreeAbstractNode;

WebFXTreeItem.prototype.remove = function() {
	this.doRemove(true);
}

WebFXTreeItem.prototype.doRemove = function(removeDeep) {

	var iconSrc = document.getElementById(this.id + '-plus').src;
	var parentNode = this.parentNode;
	var prevSibling = this.getPreviousSibling(true);
	var nextSibling = this.getNextSibling(true);
	var folder = this.parentNode.folder;
	var last = ((nextSibling) && (nextSibling.parentNode) && (nextSibling.parentNode.id == parentNode.id))?false:true;
	//avoid dynamic load focus...(in xloadtree)
	//if (!webFXTreeConfig.multiple) this.getPreviousSibling().focus();

	this._remove(removeDeep);

	if (parentNode.childNodes.length == 0) {
		document.getElementById(parentNode.id + '-cont').style.display = 'none';
		parentNode.doCollapse();
		parentNode.folder = false;
		parentNode.open = false;
	}
	if (!nextSibling || last) { parentNode.indent(null, true, last, this._level, parentNode.childNodes.length); }
	if ((prevSibling == parentNode) && !(parentNode.childNodes.length)) {
		prevSibling.folder = false;
		prevSibling.open = false;
		iconSrc = document.getElementById(prevSibling.id + '-plus').src;
		iconSrc = iconSrc.replace('minus', '').replace('plus', '');
		document.getElementById(prevSibling.id + '-plus').src = iconSrc;
		document.getElementById(prevSibling.id + '-icon').src =
			(prevSibling.getAttribute("fileIcon") ? prevSibling.getAttribute("fileIcon") : webFXTreeConfig.getFileIcon());
	}
	if (document.getElementById(prevSibling.id + '-plus')) {
		if (parentNode == prevSibling.parentNode) {
			iconSrc = iconSrc.replace('minus', '').replace('plus', '');
			document.getElementById(prevSibling.id + '-plus').src = iconSrc;
	}	}
	if (webFXTreeHandler.selected == this) 	webFXTreeHandler.selected = null;	//remove seleted
}

/**
 * add a parameter to point if really remove.
 * when drag & drop, it is not real remove.
 * @modifier Gong Lijie
 * @date 2005.8.26
 */
WebFXTreeItem.prototype._remove = function(removeDeep) {
	if (removeDeep)
	{
		for (var i = this.childNodes.length - 1; i >= 0; i--) {
			this.childNodes[i]._remove(removeDeep);
	 	}
	}

	for (var i = 0; i < this.parentNode.childNodes.length; i++) {
		if (this == this.parentNode.childNodes[i]) {
			for (var j = i; j < this.parentNode.childNodes.length; j++) {
				this.parentNode.childNodes[j] = this.parentNode.childNodes[j+1];
			}
			this.parentNode.childNodes.length -= 1;
			if (i + 1 == this.parentNode.childNodes.length) { this.parentNode._last = true; }
			break;
	}	}
	if (removeDeep) webFXTreeHandler.all[this.id] = null;

	var tmp = document.getElementById(this.id);
	if (tmp) { tmp.parentNode.removeChild(tmp); }
	tmp = document.getElementById(this.id + '-cont');
	if (tmp) { tmp.parentNode.removeChild(tmp); }
}

WebFXTreeItem.prototype.expand = function() {
	this.doExpand();
	document.getElementById(this.id + '-plus').src = this.minusIcon;
}

WebFXTreeItem.prototype.collapse = function(b) {
	//if (!b) { this.focus(); }
	this.doCollapse();
	document.getElementById(this.id + '-plus').src = this.plusIcon;
}

WebFXTreeItem.prototype.getFirst = function() {
	return this.childNodes[0];
}

WebFXTreeItem.prototype.getLast = function() {
	if (this.childNodes[this.childNodes.length - 1].open) { return this.childNodes[this.childNodes.length - 1].getLast(); }
	else { return this.childNodes[this.childNodes.length - 1]; }
}

WebFXTreeItem.prototype.getNextSibling = function() {
	for (var i = 0; i < this.parentNode.childNodes.length; i++) {
		if (this == this.parentNode.childNodes[i]) { break; }
	}
	if (++i == this.parentNode.childNodes.length) { return this.parentNode.getNextSibling(); }
	else { return this.parentNode.childNodes[i]; }
}
/**
 * get the previous sibling
 * first get the position of this node in its parent's children.
 * if it is the first child, just return parent.
 * if need expand previous, then return the previous child's last
 * otherwise return previous child.
 */
WebFXTreeItem.prototype.getPreviousSibling = function(b) {
	for (var i = 0; i < this.parentNode.childNodes.length; i++) {
		if (this == this.parentNode.childNodes[i]) { break; }
	}
	if (i == 0) { return this.parentNode; }
	else {
		i--;
		if ((this.parentNode.childNodes[i].open) || (b && this.parentNode.childNodes[i].folder)) {
			return this.parentNode.childNodes[i].getLast();
		}
		else { return this.parentNode.childNodes[i]; }
} }

/**
 * arrow key event
 * only valid when use <A>'s href='...'
 */
WebFXTreeItem.prototype.keydown = function(key) {
	//->
	if ((key == 39) && (this.folder)) {
		if (!this.open) { this.expand(); }
		else { this.getFirst().select(); }
		return false;
	}
	//<-
	else if (key == 37) {
		if (this.open) { this.collapse(); }
		else { this.parentNode.select(); }
		return false;
	}
	// down arrow
	else if (key == 40) {
		if (this.open) { this.getFirst().select(); }
		else {
			var sib = this.getNextSibling();
			if (sib) { sib.select(); }
		}
		return false;
	}
	// up arrow
	else if (key == 38) {
		this.getPreviousSibling().select();
		return false;
	}
	return true;
}

/*
 * 2005.8.17 : add popup menu
 * 2005.8.19 : add <A>'s click
 * 2005.8.22 : deprecate the <A>'s href, only click used.
 * 2005.8.25 : add drag & drop
 * @modifier Gong Lijie
 */
WebFXTreeItem.prototype.toString = function (nItem, nItemCount) {
	var foo = this.parentNode;
	var indent = '';
	if (nItem + 1 == nItemCount) { this.parentNode._last = true; }
	var i = 0;
	while (foo.parentNode) {
		foo = foo.parentNode;
		indent = "<img id=\"" + this.id + "-indent-" + i
				+ "\" src=\"" + ((foo._last)?webFXTreeConfig.getBlankIcon():webFXTreeConfig.getIIcon()) + "\">"
				+ indent;
		i++;
	}
	this._level = i;//not set until this time
	if (this.childNodes.length) { this.folder = 1; }
	else { this.open = false; }
	if ((this.folder) || (webFXTreeHandler.behavior != 'classic')) {
		if (!this.icon) { this.icon = webFXTreeConfig.getFolderIcon(); }
		if (!this.openIcon) { this.openIcon = webFXTreeConfig.getOpenFolderIcon(); }
	}
	else if (!this.icon) { this.icon = webFXTreeConfig.getFileIcon(); }
	var label = this.text.replace(/</g, '&lt;').replace(/>/g, '&gt;');

	//drag & drop
	var imgdragstr = "";
	if (webFXTreeConfig.draggable) {
		imgdragstr
			= " ondragstart=\"webFXTreeHandler.dragStart(this);"
				+ (this.dragStartAction ? this.dragStartAction : "")
				+ "\""
			+ " ondragover=\"webFXTreeHandler.dragOver(this);"
				+ (this.dragOverAction ? this.dragOverAction : "")
				+ ";return false;\""
			+ (this.dropAction
				? " ondrop=\"if (webFXTreeHandler.isDroppable(this)){ if (" + this.dropAction
					+ ") webFXTreeHandler.finishDrag();} return false;\""
				: "");
	}
	if (webFXTreeConfig.debug)
		alert(this.text + ": parentLast-" + this.parentNode._last);
	var str = "<div id=\"" + this.id + "\" class=\"webfx-tree-item\""
						+ " ondblclick=\"webFXTreeHandler.toggle(this);\""
						+ " onkeydown=\"return webFXTreeHandler.keydown(this, event)\">"
						+ indent /**==indent==*/
					+ "<img id=\"" + this.id + "-plus\""
						+ " src=\"" + ((this.folder)?((this.open)?((this.parentNode._last)?webFXTreeConfig.getLMinusIcon():webFXTreeConfig.getTMinusIcon()):((this.parentNode._last)?webFXTreeConfig.getLPlusIcon():webFXTreeConfig.getTPlusIcon())):((this.parentNode._last)?webFXTreeConfig.getLIcon():webFXTreeConfig.getTIcon())) + "\""
						+ " onclick=\"webFXTreeHandler.toggle(this);\">"
					+ "<img id=\"" + this.id + "-icon\" class=\"webfx-tree-icon\""
						+ " src=\"" + ((webFXTreeHandler.behavior == 'classic' && this.open)?this.openIcon:this.icon) + "\""
						+ " onclick=\"webFXTreeHandler.select(this);\""
						+ imgdragstr
						+ ">"
					+ webFXTreeHandler.checkboxInsertBefore(this)
					+ "<a id=\"" + this.id + "-anchor\"" + this.exProperties
						//+ " href=\"javascript:void(0);\""	//use # href to show the mouse effect
						+ " href=\"" + webFXTreeConfig.defaultAction + "\""	//use # href to show the mouse effect
						+ " onblur=\"webFXTreeHandler.blur(this);\""
						+ (this.click ? " onclick=\"webFXTreeHandler.focus(this); " + this.click + "\"" : "")
						+ (this.contextAction ? " oncontextmenu=\"" + this.contextAction + "; return false;\"" : "")
						+ imgdragstr
						+ ">"
						+ label
					+ "</a>"
					+ webFXTreeHandler.checkboxInsertAfter(this)
				+"</div>"
				+ "<div id=\"" + this.id + "-cont\" class=\"webfx-tree-container\""
					+ " style=\"display: " + ((this.open)?'block':'none') + ";\">";
	var sb = [];

	this._last = false;	//important for redraw after drag!

	for (var i = 0; i < this.childNodes.length; i++) {
		sb[i] = this.childNodes[i].toString(i,this.childNodes.length);
	}
	this.plusIcon = ((this.parentNode._last)?webFXTreeConfig.getLPlusIcon():webFXTreeConfig.getTPlusIcon());
	this.minusIcon = ((this.parentNode._last)?webFXTreeConfig.getLMinusIcon():webFXTreeConfig.getTMinusIcon());
	return str + sb.join("") + "</div>";
}
