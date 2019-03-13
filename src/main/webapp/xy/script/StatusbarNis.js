/**
 * 评论列表js，扩展：操作按钮显示在内容的下方
 */
e5.mod("workspace.doclistMain",function() {
	var api,_super, ready = false;
	var _superCustomListPage, _superSelectDoc, _superRefresh;
	var _setInterval = false;
	
	//改变方法：双击
	var refreshDocView = function(id, libid) {
		if (main_param.curTab == "nislive") {
			view('../xy/nis/LiveView.do', id, libid); //直播详情
		}
		return false;
	}
	var view = function(url, id, libid) {
		var url = url + "?DocIDs=" + id + "&DocLibID=" + libid + "&siteID=" + e5.utils.getParam("siteID");

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
	//问吧导航：选中话题时右侧显示问答
	var selectDoc = function(src, evt) {
		_superSelectDoc(src, evt);
		
		refreshRightFrame();
	}
	//问吧导航：刷新列表时右侧刷新
	var refreshPage = function() {
		_superRefresh();
		
		refreshRightFrame();
	}
	//刷新右侧列表
	var refreshRightFrame = function() {
		var frmRight = parent.frames["frmLiveRight"];
		if (frmRight) {
			var ids = _super._ids();
			if (!ids) return;
			
			var id = (ids.split(","))[0];
			var url = "../e5workspace/DataMain.do?type=SUBJECTQA&rule=(a_rootID_EQ_@DOCID@)"
				+ "&DocLibID=" + main_param.docLibID
				+ "&DocIDs=" + id;
			frmRight.location.href = url;
		}
	}
	//判断是否互动话题导航
	var isSubjectLeft = function() {
		return (e5.utils.getParam("subject") == 0);
	}
	//--------------加操作列---------------
	var showCustomListpage = function() {
		_superCustomListPage();

		setListOps(); //加列表上的操作
		setShutup(); //加禁言事件
		setListAtts(); //直播：加附件显示
		
		setTitleStyles();//敏感词显示
	}
	var setListOps = function() {
		//每行找class=listOps的div
		var trs = $("#listing").find("tr");
		for (var i = 0; i < trs.length; i++) {
			var opDiv = $(trs[i]).find("td .listOps");
			setProcs(opDiv);
		}
	}
	//一行加列操作
	var setProcs = function(opDiv, hasFlow) {
		opDiv = $(opDiv);
		
		var flowNodeID = opDiv.attr("flowNodeID");
		var procs = findByFlowNode(flowNodeID);
		if (!procs) return;
		
		opDiv.html("");
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
	
	//----设置禁言操作
	var setShutup = function() {
		$(".shutOps").mouseover(showShutOps);
		$(".shutup").click(shutup);
		$(".shutip").click(shutIP);
	}
	var showShutOps = function(evt) {
		$("body").unbind("mousedown", _onShutOpsLeave);
			
		var td = $(evt.target).parent().parent();
		var ul = $("#shutOpArea");
		
		var userID = td.attr("userID");
		var userIP = td.attr("ip");
		//菜单项控制
		if (userID > 0) {
			ul.find(".shutup").show();
		} else {
			ul.find(".shutup").hide();
		}
		if (userIP) {
			ul.find(".shutip").show();
		} else {
			ul.find(".shutip").hide();
		}
		//菜单显示
		ul.attr("userID", userID);
		ul.attr("userName", td.attr("userName"));
		ul.attr("ip", userIP);
		
		ul.show();
		ul.css({"top":evt.pageY, "left":evt.pageX + 10})
		
		$("body").bind("mousedown", _onShutOpsLeave); //鼠标点在其它区域时隐藏
	}
	var _onShutOpsLeave = function(evt){
		if (!($(evt.target).closest("#shutOpArea").length>0)) {
			$("#shutOpArea").hide();
			$("body").unbind("mousedown", _onShutOpsLeave);
		}
	}
	var shutup = function(evt) {
		$("#shutOpArea").hide();
		if (!confirm("确定要禁止该用户发言吗？")) return;
		
		var ul = $("#shutOpArea");
		var userID = ul.attr("userID");
		var userName = ul.attr("userName");
		
		if (!userID) {
			alert("没有会员ID，无法设置");
			return;
		}
		var theURL = "nis/Shutup.do?siteID=" + main_param.siteID
			+ "&userID=" + userID
			+ "&userName=" + e5.utils.encodeSpecialCode(userName);
		$.ajax({url:theURL, async:false, success: function() {
			_super.refreshPage();
		}});
	}
	var shutIP = function(evt) {
		$("#shutOpArea").hide();
		if (!confirm("确定要禁止该IP发言吗？")) return;
		
		var ul = $("#shutOpArea");
		var ip = ul.attr("ip");
		if (!ip) {
			alert("没有IP，无法设置");
			return;
		}
		var theURL = "nis/Shutup.do?siteID=" + main_param.siteID
			+ "&ip=" + ip
		$.ajax({url:theURL, async:false, success: function() {
			_super.refreshPage();
		}});
	}
	
	//---直播：设置图片和视频附件显示
	var setListAtts = function() {
		//每行找class=listOps的div
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
		var smallUrl;
		for (var i = 0; i < att.pics.length; i++) {
			var url = att.pics[i];
			if (url.toLowerCase().indexOf("http://") < 0) {
				url = "image.do?path=" + url;
				smallUrl = url + ".0.jpg";
			} else {
				smallUrl = url + ".0";
			}
			
			var pic = $("<a/>")
				.attr("href", url)
				.attr("target", "_blank")
				.html("<image src='" + smallUrl + "' class='listAttPic'/>");
			opDiv.append(pic);
		}
		
		if (!att.videos) return;
		for (var i = 0; i < att.videos.length; i++) {
			var url = att.videos[i];
			if (url["urlApp"]) url = url["urlApp"];
			
			var pic = $("<a/>")
				.attr("href", url)
				.attr("target", "_blank")
				.html("<image src='../images/video2.png' class='listAttPic'/>");
			opDiv.append(pic);
		}
	}
	
	//-----敏感词样式显示-----
	var setTitleStyles = function() {
		//找到操作显示的列（使用流程节点或文档库（简单文档类型）
		var order = -1;

		var ths = $("#tablePinHeader").find("th");
		for (var i = 0; i < ths.length; i++) {
			var id = $(ths[i]).attr("id");

			if (id == "TH_a_content") {
				order = i;
				break;
			}
		}
		if (order < 0) return;

		//显示列上的标题
		var trs = $("#listing").find("tr");
		for (var i = 0; i < trs.length; i++) {
			var _$td = $(trs[i]).children().eq(order);
			decodeHtml(_$td);
		}
	}

	function decodeHtml(_$td){
		var _$span = _$td.find(".sensitiveSpan");
		var _html = $('<div/>').html(_$span.html()).text();
		_$span.html(_html);
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
		
		var subjectLeft = isSubjectLeft();
		if (subjectLeft) {
			_superSelectDoc = _super.selectDoc;
			_super.selectDoc = selectDoc;
			
			_superRefresh = _super.refreshPage;
			_super.refreshPage = refreshPage;
		}
		
		_super.refreshDocView = refreshDocView;
		
		//每分钟刷新审核列表
		if (!subjectLeft && !_setInterval) {
			setInterval(_super.refreshPage, 60000);
			_setInterval = true;
		}
		//允许评论列表和爆料上做复制
		if (main_param.curTab == "nisdis" ||main_param.curTab == "nistip")
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