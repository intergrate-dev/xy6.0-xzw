/**
 * 专题扩展的Statusbar.js的功能，选中稿件后显示在上方“已选”区域
 */
e5.mod("workspace.doclistMain",function() {
	var api,_super, ready = false;
	var _superCustomListPage, _superSelectDoc;
	
	//选中
	var selectDoc = function(src, evt) {
		_superSelectDoc(src, evt);
		
		//收集选中的ID
		var selectedIDs = [];
		var selectedHtmls = {};
		$(".doclistframe .selected").each(function(i){
			var tr = $(this);
			if (tr.attr("libid")) {
				selectedIDs.push(tr.attr("id"));
				selectedHtmls[tr.attr("id")] = tr.html();
			}
		});
		//收集没选中的ID
		var unselectedIDs = [];
		$(".doclistframe [libid]").each(function(i){
			var tr = $(this);
			if (!tr.hasClass("selected")) {
				unselectedIDs.push(tr.attr("id"));
			}
		});
		addSelected(selectedIDs, selectedHtmls, unselectedIDs);
	}
	
	//改变方法：去掉双击细览、全选的响应
	var dumb = function(id, libid) {
		return;
	}
	
	//-------- 标题显示样式 --------
	var showCustomListpage = function() {
		_superCustomListPage();

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
		
		//改方法
		_superSelectDoc = _super.selectDoc;
		_super.selectDoc = selectDoc;
		
		_superCustomListPage = _super.showCustomListpage;
		_super.showCustomListpage = showCustomListpage;
		
		//不响应：双击细览、全选
		_super.selectAll = dumb;
		_super.dClickDoc = dumb;
		
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