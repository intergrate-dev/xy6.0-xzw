/*!
* 流程自定义
*
* Copyright 2012, wang.yq
*
* Copyright 2012, The Founder
* Released under the MIT, BSD, and GPL Licenses.
*
* Date: 2012-5-31
*/

var idindex            = 0;
//结点列表 
var nodeList           = new Array();
//node 拖动对象 
var nodeUI             = null;
//文档类型id
var docTypeID          = null;
//当前连接线对象
var currentConn        = null;
//判断是否初始化
var isInit             = false;
//曲线的样式 Flowchart Straight Bezier
var connectionLineType = "Flowchart";
//相对于左上角的宽和高
var basePos            = { left: 157, top: 123 };

$(function () {

	//验证
	$("#flowform1").validationEngine({
		autoPositionUpdate: true,
		onValidationComplete: function (from, r) {
			if (r) {
				saveFlowData();
			}
		}
	});
	//得到文档类型id
	docTypeID = $.query.get("docTypeID");
	//设置不可选中
	$(".container").select(function () { return false; });
	//设置工具栏可以拖动
	setToolDragable();
	//保存数据
	$("#btnFlowSave").click(function () {
		if (window.document.readyState == "complete") {
			$("#flowform1").submit();
		}
	});
	//设置工作区可以拖放
	setContainerDropable();
	
	//初始化流程图
	initJsPlumb();
   
	//初始化数据
	if ($.query.get("flowid") != "") {
		initFlowVisual();
	}

	$(document.body).bind("resize",resizeHandle);
	resizeHandle();
});

function initJsPlumb() {
	/// <summary>初始化流程图</summary>
	jsPlumb.bind("ready", function () {

		jsPlumb.importDefaults({
			Endpoint: ["Dot", { radius: 2 }],
			ConnectionOverlays: [
				["Arrow", {
					location: 1,
					id: "arrow",
					length: 15,
					width: 10,
					foldback: 0.8
				}]
				, ["Label", { label: " ", id: "label" }]
			]
		});

		jsPlumb.bind("jsPlumbConnection", function (conn) {

			conn.connection.setPaintStyle({ strokeStyle: e5.utils.getNextColour() });

			//删除自己指向自己的连线
			if (conn.sourceId == conn.targetId) {
				jsPlumb.detach(conn);
				return;
			}
			//找到所有从源node到目标node的conn
			var c_direct = getConnections(conn.sourceId, conn.targetId);
			if (c_direct.length > 1) {
				//删除当前连接 
				jsPlumb.detach(conn);

			}
			currentConn = getConnections(conn.sourceId, conn.targetId)[0];

			if (!isInit) {
				//设置属性

				var param = "docTypeID=" + docTypeID + "&selfnodeid=" + currentConn.sourceId + "&nodeid=" + currentConn.targetId + "&flowid=" + $("#J_flowid").val();
				showSelfActionWindow(param);
			}

			$(".node").css("z-index", 200);
		});
	});
}

function setToolDragable() {
	/// <summary>设置工具栏可以拖动</summary>
	$(".tools").draggable({
		helper: "clone",
		cursor: "move",
		zIndex: 9999,
		start: function (event, ui) {
			ui.helper.css("width", $(event.target).width() + "px");
		}
	});
}

function setContainerDropable() {
	/// <summary>设置工作区可以拖放</summary>
	$(".node-container").droppable({
		accept: $(".tools"),
		activeClass: "ui-state-hover",
		drop: function (event, ui) {
			nodeUI = ui;

			//判断只能拉一个start
			if (ui.draggable[0].id == "start" && $(".node-container").find(".node-start").length == 1) {
				alert(i18n.exist);
				return false;
			}
			var param = "docTypeID=" + docTypeID;
			showNodeWindow(param);
			return false;
		}
	});
}

function showSelfActionWindow(params) {
	/// <summary>自身操作窗口</summary>
	e5.dialog(
			{ type: 'iframe', value: 'FlowSelfFlowActionWindow.jsp?' + params },
			{
				title: i18n.operationdefine,
				id: "action-selfflow-window", width: 600, height: 270, resizable: false, showClose: false
			}
			).show();
}

function showNodeWindow(params) {
	/// <summary>结点窗口</summary>
	e5.dialog(
			{ type: 'iframe', value: "FlowNodeWindow.jsp?" + params },
			{ title: i18n.define, id: "node-window", width: 540, height: 180, resizable: false, minH: 135, minW: 330, showClose: false }
			).show();
}

function showActionWindow(params) {
	/// <summary>操作窗口</summary>

	e5.dialog(
			{ type: 'iframe', value: "FlowActionWindow.jsp?" + params },
			{ title: i18n.addselfaction, id: "action-window", width: 600, height: 270, resizable: false, showClose: false }
			).show();
}

function showFlowActionWindow(params) {
	/// <summary>跨流程操作窗口</summary>
	e5.dialog(
			{ type: 'iframe', value: "FlowActionFlowWindow.jsp?" + params },
			{ title: i18n.addflowsaction, id: "action-flow-window", width: 600, height: 270, resizable: false, showClose: false }).show();
}

function createNewTempID() {
	/// <summary>创建一个新的id</summary>
	idindex++;
	return "fw_" + idindex.toString();
}

function initFlowVisual() {
	/// <summary>初始化流程定义 </summary>
	isInit = true;
	$.ajax({
		url: "../e5flow/cust_FlowNodeReader.do?method=getFlowInfo&?docType=" + $.query.get("docTypeID") + "&flowid=" + $.query.get("flowid"),
		async: false,
		dataType: "json",
		success: function (data) {
			if (data == null) {
				return;
			}
			//设置流程信息
			$("#J_flowid").val(data.visualFlow.ID);
			$("#txtFlowName").val(data.visualFlow.name);
			//设置结点信息 
			$.each(data.nodes, function (i, n) {

				//组装结点数据
				var nodeData = {
					id: n.visualFlowNode.ID,
					name: n.visualFlowNode.name,
					note: n.visualFlowNode.description,
					doing: n.visualFlowNode.doingStatus,
					dopre: n.visualFlowNode.waitingStatus,
					done: n.visualFlowNode.doneStatus
				};
				var left = n.visualFlowNode.description.toString().split(':')[1] + "px";
				var top = n.visualFlowNode.description.toString().split(':')[0] + "px";
				var isStart = false;

				if (n.visualFlowNode.preNodeID == 0) {
					isStart = true;
				}
				addNode(nodeData, left, top, isStart);

			});
			//设置操作信息
			$.each(data.nodes, function (p, k) {
				//画出本结点为源的操作和连线
				//当前结点id
				var selfnodeid = k.visualFlowNode.ID;

				$.each(k.actions, function (m, v) {
					//组装操作数据
					var actionData = {
						id: v.procFlow.procID,
						name: v.procFlow.procName,
						note: "",
						selfnodeid: selfnodeid,
						nodeid: v.procFlow.nextFlowNodeID,
						flowid: v.procFlow.nextFlowID,
						moduleid: v.procFlow.opID,
						modulename: "",
						icon: "..//" + v.iconUrl,
						iconid: v.procFlow.iconID
					};

					if (v.procFlow.flowNodeID != v.procFlow.nextFlowNodeID && v.procFlow.flowID == v.procFlow.nextFlowID) {
						//添加连线
						var conn = null;
						var pix = "node-";
						//先查找是否有线
						var lines = getConnections(pix + v.procFlow.flowNodeID, pix + v.procFlow.nextFlowNodeID);
						if (lines != null && lines.length > 0) {
							conn = lines[0];
						}
						//如果没有找到线就画一条线
						if (conn == null) {
							conn = jsPlumb.connect({
								source: "node-" + v.procFlow.flowNodeID,
								target: "node-" + v.procFlow.nextFlowNodeID
							});
						}

						setFlowAction(actionData, conn);

					} else {
						//添加本身操作
						setNodeAction(actionData);
					}

				});

			});


			isInit = false;
		}
	});
}

function getConnections(sourceID, targetID) {
	/// <summary>得到source 到 target的连接</summary>
	/// <param name="data" type="Object">结点数据</param>
	/// <param name="ui" type="Object">当前拖动ui</param>
	var conns = null;

	if (sourceID != null && sourceID != "" && targetID != null && targetID != "") {

		conns = jsPlumb.getConnections({
			source: sourceID,
			target: targetID
		});
		return conns;
	}

	if (sourceID != null && sourceID != "") {
		var tempConns = new Array();
		conns = jsPlumb.getAllConnections();

		try {
			$.each(conns.jsPlumb_DefaultScope, function (i, n) {

				if (n.sourceId == "node-" + sourceID) {
					tempConns.push(n);
				}
			});
		} catch (e) { }


		return tempConns;
	}

	if (targetID != null && targetID != "") {

		var tempConns = new Array();
		conns = jsPlumb.getAllConnections();
		try {
			$.each(conns.jsPlumb_DefaultScope, function (i, n) {

				if (n.targetId == "node-" + targetID) {
					tempConns.push(n);
				}
			});
		} catch (e) { }
		return tempConns;
	}
	return conns;

}

function saveFlowData() {
	/// <summary>保存流程数据</summary>

	var prix = "";
	if ($.query.get("flowid") != "") {
		prix = "node-";
	}
	var sb = new Array();
	sb.push("<?xml version='1.0' encoding='UTF-8'?>");
	sb.push("<WORKFLOW>");
	sb.push("	<DOCTYPE ID='" + $.query.get("docTypeID") + "'>");
	sb.push("		<FLOW ID='" + $("#J_flowid").val() + "' NAME='" + $("#txtFlowName").val() + "' DESCRIPTION=''>");
	//找到所有的节点
	$(".node-container").find(".node").each(function (i, n) {
		//nodeData
		var node = $(this);
		var nodeData = node.data("nodeData");
		if (nodeData != null) {
			var node_id = nodeData.id;
			if (nodeData.id.toString().indexOf("node-") > -1) {
				node_id = "";
			}
			if (nodeData.id.toString().indexOf("fw_") > -1) {

				node_id = "";
			}
			//保存结点位置到结点说明
			var nodePosition = node.position().top + ":" + node.position().left;
			//设置节点数据 
			sb.push("			<NODE ID='" + node_id + "' NAME='" + nodeData.name + "' DESCRIPTION='" + nodePosition + "' WAITINGNAME='" + nodeData.dopre + "' DOINGNAME='" + nodeData.doing + "' DONENAME='" + nodeData.done + "'>");

		}
		//设置操作

		//
		//1.找到自身节点操作数据
		var actionDatas = new Array();
		var actionContainer = $("#btn-action-" + nodeData.id);
		if (actionContainer != null) {
			//得到所有图标的数据 
			$("img", actionContainer).each(function (h, g) {

				if ($(this).data("iconData") != null) {

					actionDatas.push($(this).data("iconData"));
				}
			});

		}
		//2.找到连接操作数据
		//找到所有节点与本节点出发的连接线

		var conns = getConnections(nodeData.id, null);
		$.each(conns, function (p, k) {

			var connDataStr = k.sourceId + "-" + k.targetId;
			//找到连接上的数据 
			if (node.data(connDataStr) != null) {

				$.each(node.data(connDataStr), function (m, n) {
					actionDatas.push(n);
				})
			}
		});

		//3.生成操作数据
		//保存操作数据
		$.each(actionDatas, function (t, r) {
			//判断id是否为空  
			var id = r.id;
			var nodeName = "";
			var targetNodeID = r.nodeid;

			if ($.query.get("flowid") == null || $.query.get("flowid") == "") {
				id = "";
			}
			if (parseInt(id, 10).toString() == "NaN") {
				id = "";
			}
			if (id.toString().indexOf("fw_") > -1) {
				id = "";
			}
			//1.流程id不一样，显示结点id
			if ($("#J_flowid").val() != r.flowid) {
				targetNodeID = r.nodeid.toString().replace("node-", "");
				nodeName = "";
			}
			else {
				//2.流程id一样，显示结点名称
				if (r.nodeid.toString().indexOf("node-") > -1) {
					nodeName = $("#" + r.nodeid).find(".node-col-left span").html();
				}
				else {
					nodeName = $("#node-" + r.nodeid).find(".node-col-left span").html();
				}

				targetNodeID = "";
			}
			var iconid = "";
			if(r.iconid!=null){
				iconid = r.iconid;
			}
			sb.push("<JUMP ID='" + id + "' NAME='" + r.name + "' OPERATIONID='" + r.moduleid + "' ICONID='" + iconid + "' FLOWID='" + r.flowid + "' FLOWNODE='" + nodeName + "' FLOWNODEID='" + targetNodeID + "'/>")
		});

		sb.push("</NODE>")


	});
	sb.push("		</FLOW>")
	sb.push("	</DOCTYPE>")
	sb.push("</WORKFLOW>");

	var dataxml = {
		flowxml: sb.join('')
	}

	//ajax提交
	postXml(dataxml);
}
function postXml(dataxml) {
	/// <summary>提交数据</summary>

	$.ajax({
		type: "POST",
		url: "../e5flow/cust_submit.do",
		data: dataxml,
		beforeSend: function () {
			//设置按钮不可用，防止重复提交
			$("#btnFlowSave").attr("disabled", true);
		},
		success: function (data, textStatus) {
			$("#btnFlowSave").attr("disabled", false);
			if (data == "OK") {
				alert(i18n.success);
				window.parent.leftFrame.reloadFlow();

			}
			else {
				alert(i18n.faield);
				window.parent.leftFrame.reloadFlow();
			}
		},
		error: function () {
			//请求出错处理
			$("#btnFlowSave").attr("disabled", false);
			alert(i18n.faield);
			window.parent.leftFrame.reloadFlow();
		}
	});
}
function addNode(data, left, top, isstart) {
	/// <summary>增加一个结点</summary>
	/// <param name="data" type="Object">结点数据</param>
	/// <param name="ui" type="Object">当前拖动ui</param>
	if (data == null) {
		return false;
	}
	if (data.id == null || data.id == "") {

		var newid = createNewTempID();
		var nData = { id: newid, name: data.name };
		nodeList.push(nData);

		data.id = newid;
	}
	if ($("#node-" + data.id)[0] && $("#node-" + data.id).length > 0) {
		//修改
		$("#node-" + data.id).removeData("nodeData");
		$("#node-" + data.id).data("nodeData", data);
		$("#node-" + data.id).find(".node-col-left span").html(data.name);
		return;
	}

	var nodeClass = "";
	if (isstart) {
		nodeClass = "node-start";
	}
	else {
		nodeClass = "node-end";
	}


	var nodecotent = "";
	nodecotent = nodecotent + " <div class=\"node " + nodeClass + "\" id='node-" + data.id + "'>";
	nodecotent = nodecotent + "     <div class=\"overflow\">";
	nodecotent = nodecotent + "         <div class=\"node-col-left\"><span>" + data.name + "</span>";
	nodecotent = nodecotent + "             <div title=\"" + i18n.dragtooltip + "\" class=\"node-col-right\"></div>" + "</div>";
	nodecotent = nodecotent + "     </div>";
	nodecotent = nodecotent + "     <div class=\"node-buttons\">";
	nodecotent = nodecotent + "         <div class=\"node-button-toggle\"><span id=\"ex-" + data.id + "\" title=\"" + i18n.actionsshowtooltip + "\" class=\"ui-icon ui-icon-carat-1-n\">&nbsp;</span>";
	nodecotent = nodecotent + "         <ul id=\"btn-action-" + data.id + "\" class=\"node-buttons-container clearfix\" isFold=\"1\"></ul></div>";
	nodecotent = nodecotent + "     </div>";
	nodecotent = nodecotent + "</div>";
	$(".node-container").append(nodecotent);
	var node = $("#node-" + data.id.toString());
	//保存数据
	node.data("nodeData", data);
	//设置拖动位置

	node.css({
		top: top,
		left: left
	});
	//设置右键菜单
	node.contextMenu('nodeMenu', {
		bindings: {
			'node_edit': function (t) {
				var nodeData = null;
				nodeData = node.data("nodeData");
				var param = $.param(nodeData);
				showNodeWindow(param);
			},
			'node_delete': function (t) {
				if (confirm(i18n.tooltip)) {

					//删除结点的所有连线 
					jsPlumb.removeAllEndpoints(node.attr("id"));
					//删除结点
					node.remove();
				}

			},
			'action_add': function (t) {

				var param = "docTypeID=" + docTypeID + "&selfnodeid=" + node.data("nodeData").id + "&flowid=" + $("#J_flowid").val();
				showActionWindow(param);
			}
			,
			'action_flow': function (t) {
				var param = "docTypeID=" + docTypeID + "&selfnodeid=" + node.data("nodeData").id + "&flowid=";
				showFlowActionWindow(param);
			}
		}
	}).dblclick(function (event) {
		if (~event.target.className.indexOf("ui-icon")) {
			return;
		}
		var nodeData = null;
		nodeData = node.data("nodeData");
		showNodeWindow($.param(nodeData));

	});
	//设置拖动点
	node.hover(
			  function () {
				  $(this).find(".node-col-right").show();
			  },
			  function () {
				  $(this).find(".node-col-right").hide();
			  }
			);
	//设置可以拖动
	jsPlumb.draggable(node, { containment: ".node-container" });
	//设置拖动点可以拖动
	jsPlumb.makeSource($(".node-col-right", node), {
		parent: node,
		anchor: "Continuous",
		connector: connectionLineType,
		connectorStyle: { strokeStyle: e5.utils.getNextColour(), lineWidth: 3 },
		maxConnections: -1
	});
	//设置结点可以拖放
	jsPlumb.makeTarget(node, {
		dropOptions: { hoverClass: "dragHover" },
		anchor: "Continuous"
	});
	$("#ex-" + data.id).toggle(
	  function () {
		  $(this).removeClass("ui-icon-carat-1-n");
		  $(this).addClass("ui-icon-carat-1-s");
		  $("#btn-action-" + data.id).attr("isFold", 0);
		  $("#btn-action-" + data.id + " span").show();
		  $("#btn-action-" + data.id + " li").css("float", "none");
	  },
	  function () {
		  $(this).removeClass("ui-icon-carat-1-s");
		  $(this).addClass("ui-icon-carat-1-n");
		  $("#btn-action-" + data.id).attr("isFold", 1);
		  $("#btn-action-" + data.id + " span").hide();
		  $("#btn-action-" + data.id + " li").css("float", "left");
	  }
	);
}

function hasSameNode(data) {
	/// <summary>判断是否有相同的节点</summary>
	/// <param name="data" type="Object">结点数据</param>
	var ret = false;
	$(".node").each(function (i, n) {
		if (data.id == "") {
			//新建过程
			if ($(this).find(".node-col-left span").html() == data.name) {
				ret = true;
			}
		}
		else {
			//修改过程 
			if ($(this).find(".node-col-left span").html() == data.name && $(this).attr("id") != "node-" + data.id) {
				ret = true;
			}
		}


	});
	return ret;
}
function e5NodeWindowClose(data) {
	/// <summary>关闭窗口，并保存数据</summary>
	/// <param name="data" type="Object">结点数据</param>
	e5.dialog.close("node-window");
	if (data != null) {
		//addNode(data, nodeUI);
		var isStart = false;
		if (nodeUI) {
			if (nodeUI.draggable[0].id == "start") {
				isStart = true;
			}
			//alert(nodeUI.position.left+"="+nodeUI.position.top);
			var nLeft = parseInt(nodeUI.position.left, 10) - parseInt(basePos.left, 10);
			var nTop = parseInt(nodeUI.position.top, 10) - parseInt(basePos.top, 10);
			addNode(data, nLeft, nTop, isStart)
		}
		addNode(data);
	}
	nodeUI = null;
}
function e5ActionWindowClose(data) {
	/// <summary>关闭窗口，并保存数据</summary>
	/// <param name="data" type="Object">结点数据</param>

	e5.dialog.close("action-window");
	if (data != null) {
		var actionContainer = $("#btn-action-" + data.selfnodeid);
		if (data.id == "") {
			//根据名字来判断是否重复 
			if (actionContainer.find("img[title='" + data.name + "']").length == 1) {

				alert(i18n.existaction + ":" + data.name);
				return false;
			}

			setNodeAction(data);
		}
		else {
			//根据名字来判断是否重复 
			if (actionContainer.find("img[title='" + data.name + "']").length > 0 && actionContainer.find("img[title='" + data.name + "']").attr("id") != "action-" + data.id) {

				alert(i18n.existaction + ":" + data.name);
				return false;

			}
			setNodeAction(data);
		}
	}

}

function setNodeAction(data) {
	/// <summary>关闭窗口，并保存数据</summary>
	/// <param name="data" type="Object">结点数据</param>

	var actionContainer = $("#btn-action-" + data.selfnodeid);
	if (data.id == "") {
		data.id = createNewTempID();
	}
	else {
		//存在
		if ($("#action-" + data.id, actionContainer).length > 0) {

			$("#action-" + data.id, actionContainer).parent().remove();
		}
	}

	actionContainer.append("<li class=\"clearfix\" id='action-item-" + data.id + "' ><img id=\"action-" + data.id + "\"  title=\"" + data.name + "\" src=\"" + data.icon + "\" />" + "<span>" + data.name + "</span>" + "</li>");
	if (actionContainer.attr("isFold") == 0) {
		actionContainer.find("span").last().show();
		actionContainer.find("li").last().css("float", "none")
	}
	//保存数据
	var actionNode = $("#action-" + data.id, actionContainer);
	actionNode.data("iconData", data);
	var actionItemNode = $("#action-item-" + data.id, actionContainer);
	//右键菜单
	//actionNode.contextMenu('actionMenu', {
	actionItemNode.contextMenu('actionMenu', {
		bindings: {
			'action_edit': function (t) {
				var imgdata = $(t).find("img");
				if (data.selfnodeid == data.nodeid) {
					//自身操作
					var param = "docTypeID=" + docTypeID + "&" + $.param(imgdata.data("iconData"));
					showActionWindow(param);

				}
				else {
					//跳转操作
					var param = "docTypeID=" + docTypeID + "&" + $.param(imgdata.data("iconData"));
					showFlowActionWindow(param);

				}

			},
			'action_delete': function (t) {
				if (confirm(i18n.tooltip)) {
					//$(t).parent().remove();
					$(t).remove();
				}

			}
		}
	}).dblclick(function (event) {

		event.preventDefault();
		event.stopPropagation();
		var imgdata = $(event.target).parent().find("img");
		if (data.selfnodeid == data.nodeid) {
			//自身操作
			var param = "docTypeID=" + docTypeID + "&" + $.param(imgdata.data("iconData"));
			showActionWindow(param);
		}
		else {
			//跳转操作
			var param = "docTypeID=" + docTypeID + "&" + $.param(imgdata.data("iconData"));
			showFlowActionWindow(param);
		}

	});
}

function e5ActionFlowWindowClose(data) {
	/// <summary>关闭窗口，并保存数据</summary>
	/// <param name="data" type="Object">结点数据</param>
	e5.dialog.close("action-flow-window");
	if (data != null) {
		var actionContainer = $("#btn-action-" + data.selfnodeid);
		if (data.id == "") {
			//根据名字来判断是否重复 
			if (actionContainer.find("img[title='" + data.name + "']").length == 1) {

				alert(i18n.existaction + ":" + data.name);
				return false;

			}

			setNodeAction(data);
		}
		else {
			//根据名字来判断是否重复 
			if (actionContainer.find("img[title='" + data.name + "']").length > 0 && actionContainer.find("img[title='" + data.name + "']").attr("id") != "action-" + data.id) {

				alert(i18n.existaction + ":" + data.name);
				return false;

			}
			setNodeAction(data);

		}
	}
}
function e5SelfFlowActionWindowClose(data) {
	/// <summary>关闭窗口，并保存数据</summary>
	/// <param name="data" type="Object">结点数据</param>
	e5.dialog.close("action-selfflow-window");
	if (data != null) {
		if (data.name != "") {
			setFlowAction(data, null);
		}
		else {
			//删除连接线
			var currentNode = "";
			if (data.selfnodeid.toString().indexOf("node-") == -1) {
				currentNode = "node-";
			}
			var connDataStr = currentNode + data.selfnodeid + "-" + currentNode + data.nodeid;
			var conn_s2t = getConnections(currentNode + data.selfnodeid, currentNode + data.nodeid);
			if (conn_s2t.length == 1) {
				//判断是否有数据,如果没有数据就删除
				var node = $("#" + currentNode + data.selfnodeid);
				if (node.data(connDataStr) == null) {
					//如果是最后一个就删除连接线
					jsPlumb.detach(conn_s2t[0]);
				}
			}
		}

	}
}

function setFlowAction(data, conn) {
	/// <summary>关闭窗口，并保存数据</summary>
	/// <param name="data" type="Object">结点数据</param>
	var connData = [];
	var currentNode = "";
	if (data.selfnodeid.toString().indexOf("node-") == -1) {
		currentNode = "node-";
	}
	var connect = conn;
	if (connect == null) {
		connect = getConnections(currentNode + data.selfnodeid, currentNode + data.nodeid)[0];
	}
	var node = $("#" + currentNode + data.selfnodeid);
	var connDataStr = currentNode + data.selfnodeid + "-" + currentNode + data.nodeid;
	if (data.id == null || data.id == "") {
		data.id = createNewTempID();
		//记录一个数据
		if (node != null && node.data(connDataStr) != null) {
			connData = node.data(connDataStr);
		}
		connData.push(data);
		node.removeData(connDataStr);
		node.data(connDataStr, connData);
	}
	else {
		//保存更新数据 
		var updateConnData = [];
		if (currentNode == "") {
			//表示为设计时修改
			connData = node.data(connDataStr);
			$.each(connData, function (o, p) {
				//删除数据
				if (data.id == p.id) {
					updateConnData.push(data);
				}
				else {
					updateConnData.push(p);
				}
			});
			//node.removeData(connDataStr);
			node.data(connDataStr, updateConnData);
		}
		else {
			//表示为从数据库里读取数据

			if (node.data(connDataStr) != null) {
				connData = node.data(connDataStr);
				var isupdate = false;
				//查看是否存在
				$.each(connData, function (p, k) {
					//更新数据
					if (data.id == k.id) {
						updateConnData.push(data);
						isupdate = true;
					}
					else {
						updateConnData.push(k);
					}
				});
				if (!isupdate) {
					updateConnData.push(data);
				}
			}
			else {
				updateConnData.push(data);
			}

			node.data(connDataStr, updateConnData);
		}
	}
	var labelStr = "";
	$.each(node.data(connDataStr), function (i, n) {
		labelStr = labelStr + "<div id='lab-" + n.id + "' class='_jsPlumb_label clearfix'><img title=\"" + n.name + "\"  src=\"" + n.icon + "\" /><span>" + n.name + "</span></div>";
	});
	//画一个lable
	var label = connect.getOverlay("label");

	label.setLabel(labelStr);

	$.each(node.data(connDataStr), function (j, k) {
		//设置右键菜单
		$("#lab-" + k.id).contextMenu('connActionMenu', {
			bindings: {
				'connAction_edit': function (t) {
					var param = "docTypeID=" + docTypeID + "&" + $.param(k);
					showSelfActionWindow(param);
				},
				'connAction_delete': function (t) {
					if (confirm(i18n.tooltip)) {
						//删除保存数据
						var oldnode = $("#" + currentNode + k.selfnodeid);
						var newConnData = [];
						if (oldnode.data(connDataStr) != null) {
							$.each(oldnode.data(connDataStr), function (p, u) {
								//删除数据
								if (t.id.toString().replace("lab-", "") != u.id.toString()) {
									newConnData.push(u);
								}
							});
							oldnode.removeData(connDataStr);
							oldnode.data(connDataStr, newConnData);
						}
						var labContainers = $(t).parent();
						$(t).remove();
						var conn_s2t = getConnections(currentNode + k.selfnodeid, currentNode + k.nodeid);
						if (conn_s2t.length == 1) {
							if (labContainers.find("div").length == 0) {
								//如果是最后一个就删除连接线
								jsPlumb.detach(conn_s2t[0]);
							}
						}
					}
				}
			}
		}).dblclick(function () {
			var param = "docTypeID=" + docTypeID + "&" + $.param(k);
			showSelfActionWindow(param);
		});
	});

}
function resizeHandle(){
	var bodyH = $("body").data("resize-special-event").h;
	var container = $(".container");
	var	toolbar = $(".toolbar");
	var flowContainer = $(".flowcontent");
	var	nodeContainer = $(".node-container");
	var	nodeContainerTitle = $(".node-container-wrap h3");
	var sidebarContainer = $(".sidebar-container");
	var sidebarTitle = $(".sidebar h3");
	var	btnArea = $(".btn-area");
	var	flowContainerH = bodyH
			- parseInt(container.css("margin-top"))
			- parseInt(container.css("margin-bottom"))
			- toolbar.outerHeight(true)
			- parseInt(flowContainer.css("margin-top"))
			- parseInt(flowContainer.css("margin-bottom"))
			- btnArea.outerHeight(true);
	var	nodeContainerH = flowContainerH - nodeContainerTitle.outerHeight(true);
	var	sidebarContainerH = flowContainerH - sidebarTitle.outerHeight(true);
	flowContainer.height(flowContainerH);
	nodeContainer.height(nodeContainerH);
	sidebarContainer.height(sidebarContainerH);
}