//微信：导航 + 列表
e5.mod("workspace.resourcetree", function() {
	var api;
	
	var init = function(sandbox) {
		api = sandbox;
		$(".group").click(treeClick);
	},
	onload = function() {
		defaultClick();
	}

	var treeClick = function(evt) {
		$(".group").removeClass("select");
		$(this).addClass("select");
		
		var param = new ResourceParam();
		for ( var name in main_param)
			param[name] = main_param[name];

		var groupID = $(evt.target).attr("groupID");
		if (!param.ruleFormula)
			param.ruleFormula = "wxg_accountID_EQ_" + groupID;
		else
			param.ruleFormula = "wxg_accountID_EQ_" + groupID + "_AND_" + param.ruleFormula;
		param.groupID = groupID;

		api.broadcast("resourceTopic", param);
	}
	var defaultClick = function() {
		var statusReady = e5.mods["workspace.doclistMain"].isReady;
		var searchReady = e5.mods["workspace.search"].isReady;
		var ready = !!statusReady && !!searchReady && statusReady() && searchReady();
		if (!ready) {
			setTimeout(defaultClick, 100);
			return;
		}

		$(".group").first().click();
	}
	return {
		init : init,
		onload : onload
	};
});

/**
 * 列表
 */
e5.mod("workspace.doclistMain",function() {
	var api,_super, ready = false;
	var _superCustomListPage;
	var _setInterval = false;
	
	//改变方法：双击
	var refreshDocView = function(id, libid) {
		var tr = $("#listing table").find("tr[id='" + id + "']");
		if (tr.length == 0) {
			if (!clickEditProc(id))
				return false;
			return;
		}

		var foundEdit = false;
		tr.find("td span[procid]").each(function(i) {
			//找到列表列的"修改"操作，点击
			var proc = $(this);
			var name = proc.html();
			if (name == "修改") {
				proc.click();
				foundEdit = true;
			}
		});
		
		if (!foundEdit) {
			if (!clickEditProc(id))
				return false;
		}
	}
	//--------------加操作列---------------
	var showCustomListpage = function() {
		_superCustomListPage();
		
		setListAtts(); //加图文稿件显示
		setListOps(); //加列表上的操作
	}
	var setListOps = function() {
		var uls = $("#listing").find("ul");
		for (var i = 0; i < uls.length; i++) {
			var ul = $(uls[i]);
			
			setProcs(ul);
		}
	}
	//一行加列操作
	var setProcs = function(ul) {
		var flowNodeID = ul.attr("flowNodeID");
		var procs = findByFlowNode(flowNodeID);
		if (!procs) return;
		
		var li = $("<li class='opli'/>");
		for (var i = 0; i < procs.length; i++) {
			var op = $("<span/>")
						.attr("procid",procs[i].procid)
						.attr("flownode",flowNodeID)
						.html(procs[i].name)
						.click(procClick);
			li.append(op);
		}
		ul.append(li);
	}
	//从main_param里取出当前流程节点的操作
	var findByFlowNode = function(flowNode) {
		for (var i = 0; i < main_procs.procArr.length; i++) {
			if (main_procs.procArr[i].flowNode == flowNode) {
				return main_procs.procArr[i].procs;
			}
		}
		return null;
	}
	var findByProcID = function(flowNode, procID) {
		var procs = findByFlowNode(flowNode);
		if (!procs) return null;
		
		for (var i = 0; i < procs.length; i++) {
			if (procs[i].procid == procID) {
				return procs[i];
			}
		}
		return null;
	}
	//列操作点击
	var procClick = function(evt) {
		//evt.preventDefault();
		//evt.stopPropagation();
		
		var span = $(evt.target);
		
		var procid = span.attr("procid");
		var flownode = span.attr("flownode");
		var docID = span.parent().parent().attr("docID");
		
		var proc = findByProcID(flownode, procid);
		if (!proc) return;
		
		proc["text"] = proc.name;
		
		var tool = e5.mods["workspace.toolkit"];
		//使用toolkit.js中的toolbarparam变量，因为有些内部方法直接引用了这个变量
		var _param = tool.self.getParam();
		_param.docIDs = docID;
		
		tool.self.callOperation(proc, _param);
	}
	//双击修改的一种情况：当无流程时，可以从main_param里直接找到修改操作
	var clickEditProc = function(docID) {
		if (typeof main_procs == "undefined") return null;
		
		var proc = null;
		if (main_procs.procArr.length == 1) {
			var procs = main_procs.procArr[0].procs;
			for (var i = 0; i < procs.length; i++) {
				if (procs[i].name == "修改") {
					proc = procs[i];
					break;
				}
			}
		}
		if (!proc) return;
		
		proc["text"] = proc.name;
		
		var tool = e5.mods["workspace.toolkit"];
		//使用toolkit.js中的toolbarparam变量，因为有些内部方法直接引用了这个变量
		var _param = tool.self.getParam();
		_param.docIDs = docID;
		
		tool.self.callOperation(proc, _param);
		
		return true;
	}	
	
	//---设置稿件显示
	var setListAtts = function() {
		var uls = $("#listing").find("ul");
		for (var i = 0; i < uls.length; i++) {
			var ul = $(uls[i]);
			
			setAtts(ul);
		}
	}
	var setAtts = function(ul) {
		var members = ul.attr("members");
		if (!members) return;
			
		members = eval("(" + members + ")");
		members = members.list;
		
		var count = members.length;
		
		if (count > 0) one(ul, members[0], true); //显示主图
		if (count > 1) {
			one(ul, members[1], false);//再显示一篇
		} else {
			var li = $("<li/>").addClass("listli");
			ul.append(li);
		}
		
		//其它不显示，只提示总数
		ul.find(".dateli").find(".count").html("（共" + count + "篇）");
	}
	//微信图文：列表中显示图文里的一个稿件，一个li
	var one = function(ul, main, isMain) {
		//pic url
		var url = main.pic;
		if(url.indexOf("image.do?path=") != -1){
			url = url.split("image.do?path=")[1]
		}
		if (url.toLowerCase().indexOf("http://") < 0) {
			url = "image.do?path=" + url;
		}
		url += isMain ? ".2.jpg" : ".0.jpg";
		
		//pic and title
		var pic = $("<img/>").attr("src", url);
		var title = main.title;
		var lenght = (isMain) ? 40 : 28;
		if (title.length > lenght) title = title.substring(0, lenght) + "...";
		var title = $("<span/>").text(title).attr("title", main.title);
		
		//li
		var li = $("<li/>")
				.addClass(isMain ? "mainli" : "listli");
		li.append(title);
		li.append(pic);
		
		//li --> ul
		ul.append(li);
	}

	//---------end.
	
	//标记当前js是否加载完成
	var isReady = function() {
		return ready;
	}
	var replaceFunc = function() {
		//判断Statusbar.js是否在页面加载完成。若没有完成，则等待一下
		_super = e5.mods["workspace.doclist"].self;
		if (!_super) {
			setTimeout(replaceFunc, 100);
			return;
		}
		
		//增加操作列
		_superCustomListPage = _super.showCustomListpage;
		_super.showCustomListpage = showCustomListpage;
		
		_super.refreshDocView = refreshDocView;
		
		ready = true;
	}
	var init = function(sandbox){
		api = sandbox;
		
		replaceFunc();
	}
	return {
		init : init,
		isReady : isReady
	}
});