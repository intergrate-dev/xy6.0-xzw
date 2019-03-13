//微博稿件：导航 + 列表
e5.mod("workspace.resourcetree", function() {
	var api;
	
	var init = function(sandbox) {
		$('#tab-wb-component').load('weibo/WeiboCenter.jsp?timespan='+new Date().getTime(),function(){
		//getHeader();
	});
		api = sandbox;
		$(".group").click(treeClick);
	},
	onload = function() {
		defaultClick();
	}

	var treeClick = function(evt) {
		$(".group").removeClass("select");
		$(this).addClass("select");
		
		//main_param中记录groupID（账号ID）
		var groupID = $(evt.target).attr("groupID");
		main_param.groupID = groupID;
		
		var param = new ResourceParam();
		for ( var name in main_param)
			param[name] = main_param[name];

		if (!param.ruleFormula)
			param.ruleFormula = "wb_accountID_EQ_" + param.groupID;
		else
			param.ruleFormula = "wb_accountID_EQ_" + param.groupID + "_AND_" + param.ruleFormula;

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
		
		setListAtts(); //加附件显示
		setListOps(); //加列表上的操作
	}
	var setListOps = function() {
		var trs = $("#listing").find("tr");
		for (var i = 0; i < trs.length; i++) {
			var opDiv = $(trs[i]).find("td .listOps");
			setProcs(opDiv);
		}
	}
	//一行加列操作
	var setProcs = function(opDiv) {
		var flowNodeID = 0;
		var procs = findByFlowNode(flowNodeID);
		if (!procs) return;
		
		//opDiv.html("");
		for (var i = 0; i < procs.length; i++) {
			var op = $("<span/>")
						.attr("procid",procs[i].procid)
						.attr("flownode",flowNodeID)
						.html(procs[i].name)
						.addClass("listop")
						.click(procClick);
			opDiv.append(op);
		}
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
		var docID = span.parent().attr("docID");
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
		var trs = $("#listing").find("tr");
		for (var i = 0; i < trs.length; i++) {
			var opDiv = $(trs[i]).find("td .listAttachments");
			if (!opDiv) continue;
			
			setAtts(opDiv);
		}
	}
	var setAtts = function(opDiv) {
		opDiv = $(opDiv);
		
		var att = opDiv.attr("att");
		if (!att) return;
		
		att = eval("(" + att + ")");
		if (att.pics) {
			showPic(opDiv, att.pics);
		}
	}
	var showPic = function(opDiv, pics) {
		var count = pics.length;
		var liClass = (count == 1) ? "listLargePic" : "listSmallPic"; //只有一个图时稍微大一些
		var picClass = (count == 1) ? "picLarge" : "picSmall"; //只有一个图时稍微大一些
		for (var i = 0; i < count; i++) {
			var url = pics[i];
			if (url.toLowerCase().indexOf("http://") < 0) {
				url = "image.do?path=" + url;
			}
			var pic = $("<a/>")
				.attr("href", url + ".2.jpg")
				.attr("target", "_blank")
				.html("<image src='" + url + ".0.jpg" + "' class='" + picClass + "'/>");
			
			var picDiv = $("<li class='picDiv'/>").addClass(liClass);
			picDiv.append(pic);
			
			opDiv.append(picDiv);
			
			//若4张图，则2张换行
			if (count == 4) {
				if (i == 1) opDiv.append($("<br class='clearfix'/>"));
			} else {
				if ((i + 1) % 3 == 0) {
					opDiv.append($("<br class='clearfix'/>"))
				}
			}
		}
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