var _css_postfix; //避免别处引用时失败
try {_css_postfix = css_postfix;} catch (e) {_css_postfix = "";}

e5.mod("workspace.resourcetree",function() {
	var api, 
		PRESERVED = ["doctype", "doclibid", "fvid", "ruleformula", "exttype", "cattypeid", 
			"src", "pcode", "text", "title","icon", "openIcon", "fileIcon", 
			"isParent", "iconOpen", "iconClose"],
		_myprops = [],
		_contains = function(name){
			for (var i = 0; i < PRESERVED.length; i++)
				if (PRESERVED[i] == name) return true;
			for (var i = 0; i < _myprops.length; i++)
				if (_myprops[i] == name) return true;
			
			return false;
		},
		treeClick = function(event, treeId, treeNode, clickFlag) {
			var param = new ResourceParam();
			param.docTypeID = treeNode["doctype"];
			param.docLibID = treeNode["doclibid"];
			param.fvID = treeNode["fvid"];
			param.ruleFormula = treeNode["ruleformula"];

			var extType = treeNode["exttype"];
			if (extType) param.extType = extType;

			extType = treeNode["cattypeid"];
			if (extType) param.catTypeID = extType;
			
			//取自定义的属性
			for (var i = 0; i < _myprops.length; i++) {
				var prop = _myprops[i];
				param[prop] = treeNode[prop];
			}
			
			api.broadcast("resourceTopic", param);
		},
		isArray = e5.lang.isArray,
		//.do返回的是xml，转换成ztree可认的json格式
		change2Json = function(data){
			var datas = $.xml2json(data);
			//alert(JSON.stringify(datas));
			
			//xml2json转换后是{tree:[{text:...,src:...,doclibid:....}, {},{}]}的格式
			var tree = datas.tree;
			if (!tree) return null;
			
			if (!isArray(tree)) {//只有一个子时，不表示为数组
				tree = [tree];
			}
			//my properties.
			for (var i = 0; i < tree.length; i++) {
				tree[i].text = tree[i].text[0];//text属性会变成2个元素的数组，其第一个元素是实际可用的
				if (tree[i].src) tree[i].isParent = true;//设置为父节点
				if (tree[i].openIcon) {
					tree[i].iconOpen = tree[i].openIcon;
					tree[i].iconClose = tree[i].icon;
				}
				if (tree[i].fileIcon) {
					tree[i].icon = tree[i].fileIcon;
				}
				for (var j in tree[i]) {
					if (!_contains(j)) _myprops.push(j);//检查扩展属性
				}
			}
			return tree;
		},
		//从resource.do读资源树，然后转换xml为json，套用ztree进行显示
		showTree = function (data) {
			var tree = change2Json(data);
			if (!tree) return;
			
			var setting = {
				data:{key:{name: "text"}},//text为显示名
				callback: {onClick: treeClick},//点击事件
				async: {
					enable : true,
					url : getSrc,
					dataFilter : showSubTree,
					dataType : "xml"
				},
				view: {
					showLine: false,
					// showIcon: showIconForTree,
					selectedMulti: false,
					dblClickExpand: false
				}
			};
			$.fn.zTree.init($("#rs_tree"), setting, tree);
		},
		//节点展开时，取读子节点的url
		getSrc = function(treeId, treeNode) {
			if (treeNode && treeNode.src) return "../e5workspace/" + treeNode.src;
		},
		//读取子节点
		showSubTree = function(treeId, parentNode, childNodes) {
			return change2Json(childNodes);
		};
		
	var defaultClick = function() {
		var statusReady = e5.mods["workspace.doclist"].self;
		var searchReady = e5.mods["workspace.search"].init;
		if (!statusReady || !searchReady) {
			setTimeout(defaultClick, 100);
			return;
		}
		//缺省选中第一个节点，展开所有第一层节点
		var treeObj = $.fn.zTree.getZTreeObj("rs_tree");
		var nodes = treeObj.getNodes();
		if (nodes) {
			treeObj.selectNode(nodes[0]);//选中没触发click事件
			var aNode = $("#rs_tree_1_a");
			if (aNode) aNode.click();
			
			//treeObj.expandNode(nodes[0], true, false, true);
			for ( var i = 0; i < nodes.length; i++) {
				treeObj.expandNode(nodes[i], true, false, true);
			}
		}
		// function showIconForTree(treeId, treeNode) {
		// 	return treeNode.exttype == "2";
		// };
	}
	var init = function(sandbox) {
			api = sandbox;
		},
		onload = function() {
			var theURL = "../e5workspace/resource.do";
			//$.get(theURL, showTree, "xml");
			$.ajax({url:theURL, async:false, dataType:"xml", success:showTree});
			
			//缺省选中第一个节点
			defaultClick();
		};
	return {
		init: init,
		onload: onload
	}
},{requires:["../e5script/jquery/jquery.min.js",
	"../e5workspace/script/Param.js",
	"../e5script/jquery/jquery.xml2json.js",
	"../e5script/jquery/ztree/jquery.ztree.all-3.3.min.js",
	"../e5script/jquery/ztree/zTreeStyle/zTreeStyle.css",
	"../e5style/ws-zTree-style" + _css_postfix + ".css"
]});