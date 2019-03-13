/**
 * 扩展的Statusbar.js的功能
 * 
 * 这里演示如何修改e5的主界面js
 * 先用e5.mod("workspace.doclistMain",...)为自己的模块起名，起名别冲突。
 * 然后写一个replaceFunc函数，里面是希望替换掉E5本身的哪些函数。注意观察replaceFunc的写法。
 * 然后在init函数里调用replaceFunc。这里init是e5.mod要求的。
 */
e5.mod("workspace.doclistMain",function() {
	var api,_super, ready = false;
	var _superCustomListPage;
	var timerID;
	
	//改变方法：双击细览的响应url变化
	var refreshDocView = function(id, libid) {

		var tr = $("#listing table").find("tr[id='" + id + "']");
		if (tr.length == 0) {
			if (!clickEditProc(id))
				view(id, libid);
			return;
		}

		var foundEdit = false;
		tr.find("td span[procid]").each(function(i) {
			//找到列表列的"修改"操作，点击
			var proc = $(this);
			var name = proc.html();
			if (name == "修改"||name == "重改") {
				proc.click();
				foundEdit = true;
			}
		});
		
		//若没有"修改"操作，则显示稿件细览 TODO
		if (!foundEdit) {
			if (!clickEditProc(id))
				view(id, libid);
		}
	}
	var view = function(id, libid) {
		var url = "../xy/View.do?DocIDs=" + id + "&DocLibID=" + libid + "&siteID=" + e5.utils.getParam("siteID");

		var winHeight = screen.availHeight - 80;
		var winWidth = screen.availWidth;
		if (winWidth > 1070) winWidth = 1070;
		var winLeft = (screen.availWidth - winWidth) / 2;
		var winTop = 0;

		var feature = "scrollbars=yes,status=no,toolbar=no,location=no,menubar=no,resizable=1"
			+ ",width=" + winWidth + ",height=" + winHeight
			+ ",left=" + winLeft + ",top=" + winTop;
		var docViewWnd = window.open(url,"_blank", feature);
		docViewWnd.focus();
	}
	//--------------加操作列---------------
	var showCustomListpage = function() {
		_superCustomListPage();
		setListOps(); //列上加操作
		
		setTitleStyles();//稿件的标题显示样式
		
		//视频列表刷新
		if (_super.statusparam.docTypeID == 4) {
			var spans = $.find(".trans-process");
			if (spans.length > 0) {
				//有转码中视频，则加定时刷新timer，每10秒刷新列表
				if (!timerID) timerID = setInterval(_super.refreshPage, 10000);
			} else if (timerID) {
				//没有转码中视频，则清除timer
				clearInterval(timerID);
				timerID = null;
			}
		}
	}
	var setListOps = function() {
		//找到操作显示的列（使用流程节点或文档库（简单文档类型）
		var order = -1, hasFlow = false;
		
		var ths = $("#tablePinHeader").find("th");
		for (var i = 0; i < ths.length; i++) {
			var id = $(ths[i]).attr("id");
			
			if (id == "TH_SYS_DOCLIBID" || id == "TH_SYS_CURRENTNODE") {
				order = i;
				hasFlow = (id == "TH_SYS_CURRENTNODE");
				break;
			}
		}
		if (order < 0) return;
		
		//在列表上加操作
		var trs = $("#listing").find("tr");
		for (var i = 0; i < trs.length; i++) {
			var td = $(trs[i]).children().get(order);
			setProcs(td, hasFlow);
		}
	}
	//一行加列操作
	var setProcs = function(td, hasFlow) {
		td = $(td);
		
		var flowNodeID = hasFlow ? td.text() : 0;
		var procs = findByFlowNode(flowNodeID);
		if (!procs) return;
		
		td.html("");
		for (var i = 0; i < procs.length; i++) {
			var op = $("<span/>")
						.attr("procid",procs[i].procid)
						.attr("flownode",flowNodeID)
						.html(procs[i].name)
						.addClass("listop")
						.click(procClick);
			td.append(op);
		}
	}
	//从main_param里取出当前流程节点的操作
	var findByFlowNode = function(flowNode) {
		if (typeof main_procs == "undefined") return null;
		
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
		var docID = span.parent().parent().attr("id");
		
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
	//---------end.
	
	//-------- 标题显示样式 --------
	var setTitleStyles = function() {
		//找到操作显示的列（使用流程节点或文档库（简单文档类型）
		var order = -1;
		
		var ths = $("#tablePinHeader").find("th");
		for (var i = 0; i < ths.length; i++) {
			var id = $(ths[i]).attr("id");
			
			if (id == "TH_SYS_TOPIC" || id == "TH_a_linkTitle") {
				order = i;
				break;
			}
		}
		if (order < 0) return;
		
		//显示列上的标题
		var trs = $("#listing").find("tr");
		for (var i = 0; i < trs.length; i++) {
			//var td = $(trs[i]).children().get(order);
			//setTitleStyle(td);

			var _$td = $(trs[i]).children().eq(order);
            decodeHtml(_$td);
		}
	}

    function decodeHtml (_$td){
        var _$span = _$td.find("span[title]");
        var _html = $('<div/>').html(_$span.html()).text();
        _$span.html(_html);
		
		_$span.attr("title", _$span.text());
		/*
		if(_$span.find("a").size() > 0){
			_$span.attr("title", "");
			_$span.find("a").each(function(){
				$(this).attr("title",$(this).html() + " ");
			});

		}
		*/
    }
	//对一行转换标题样式
	var setTitleStyle = function(td) {
		td = $(td);
		try {
			var text = $(td.text());
			if (text.length > 0) {
				//若该td里有多个字段，则应把每个字段都加进去。发布库里还有手工增加的栏目ID，也做处理
				var html0 = $("<span>" + td.html() + "</span>");
				var spans = html0.find("span[id],.hiddenColID");
				
				td.html("");
				td.append(spans).append(text);
			}
		} catch (e){}
	}
	//end.
	
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
		//显示导出列表按钮
		if (typeof main_param != "undefined" && main_param.exportable == "true") {
			$("#ListExportSpan").parent().show();
		}
		
		//改变E5方法：双击细览
		_super.refreshDocView = refreshDocView;
		
		//增加操作列
		_superCustomListPage = _super.showCustomListpage;
		_super.showCustomListpage = showCustomListpage;

        //允许公共资源上做复制
        if (main_param.curTab == "resr")
            $("#doclistframe")[0].onselectstart = null;
		
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