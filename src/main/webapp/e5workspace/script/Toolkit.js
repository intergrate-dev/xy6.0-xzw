e5.mod("workspace.toolkit",function() {
	var api, toolbarparam, 
		defaultOperationWidth= 400, //操作没有设置窗口大小时的缺省窗口宽度
		defaultOperationHeight= 300,//操作没有设置窗口大小时的缺省窗口高度
		defaultCountPerRow = 18, //每行可显示的操作数
		curOpDialog,
		listMenuID = "toolMenu",
		//垃圾箱的几个操作：恢复、彻底删除、日志
		/**(i, proctype, procid, flownode, callmode, text,
		opheight, opwidth, resizable, opid, opurl, needprompt, imgurl, dealcount);**/
		garbagebin_restore = function() {
			return new Operation(0, "5", 0, 0, 3, toolhint.garbageRestore,
			0, 0, "true", 0, self._getUrl(0), "true", "Icons/Restore.gif", 2);
		},
		garbagebin_delete = function() {
			return new Operation(1, "5", 0, 0, 3, toolhint.garbageDelete,
			0, 0, "true", 0, self._getUrl(1), "true", "Icons/DocDelete.gif", 2);
		},
		garbagebin_log = function() {
			return new Operation(2, "5", 0, 0, 1, toolhint.garbageLog,
			350, 800, "true", 0, self._getUrl(2), "false", "Icons/DocLog.gif", 1);
		},
		//订阅响应
		listening = function(msgName, callerId, param) {
			for (var name in param){
				toolbarparam[name] = param[name];
			}
			toolbarparam.operations = {};
			
			if (toolbarparam.docLibIDs){
				toolbarparam.docLibID = toolbarparam.docLibIDs;
			}
			toolbarparam.docLibID = dealDocLibIDs(toolbarparam.docLibID);
			
			toolbarparam.before_docIDs = param.docIDs;//从beforeProcess返回的docIDs
			toolbarparam.before_docLibIDs = param.docLibIDs;

			toolbar(toolbarparam);
		},
		toolbar = function(param){
			//若是垃圾箱，则单独显示。垃圾箱不用取操作，也不用BeforeProcess响应
			if (toolbarparam.extType == 1) {
				return showGarbageBin();
			}

			var theURL = self.getToolUrl(param);

			//对于多库一起显示的情况。要把工具栏上的docLibID固定为第一个文档的库ID。
			//因为若库不一致，则任何操作都不显示，所以这么简单的指定不会有问题
			//toolbarparam.docLibID = getToolDocLibID(param.docLibIDs, param.docLibID);
			$.get(theURL, showToolkit, "xml");
		},
		getToolDocLibID = function(docLibs, docLib){
			if (!docLibs) {
				return docLib;
			}
			var docLibArr = docLibs.split(",");
			if (docLibArr && docLibArr.length > 0){
				return docLibArr[0];
			}else {
				return docLib;
			}
		},
		/////////////////////////////////////////////////////////////

		//每一行是否显示的标志，用于在列表刷新时保持按钮行的显示方式不变。设置10行
		showFlag = [true, false, false, false, false, false, false, false, false, false],
		tabNames = ["主操作", "扩展操作", "其他操作", "操作组四", "操作组五", "操作组六", "操作组七", "操作组八", "操作组九", "操作组十"],

		showToolkit = function(oXmlDoc) {
			rowClear();
			toolbarparam.flowNodeID = 0;//每次新显示工具条前，先把保存的flowNodeID清空
			
			if ((oXmlDoc == null) || (oXmlDoc.documentElement == null)){
				alert("no xmldoc");
				return;
			}

			var cs = oXmlDoc.documentElement.childNodes,
				l = cs.length,
				opcount = 0,
				otr = $("#toolTR"),
				customize = "",
				rowCount = 0,	//行数
				groupCount = 0, //组数
				opCount_perRow = 0, //每行中的操作个数
				i,j,procs,procCount,newop,showtype,configImg;
			
			toolbarparam.groupSeparator = [];
			for (i = 0; i < l; i++) {
				if (cs[i].tagName == "group") {
					procs = cs[i].childNodes;
					procCount = procs.length;

					if (groupCount > 0){//新的一组，加一行
						rowCount++;
						otr = newTR(rowCount);
						opCount_perRow = 0; //重置
					}
					for (j = 0; j < procCount; j++){
						if (procs[j].tagName == "proc") {
							newop = addOperation(procs[j], opcount);
							parseOperation(otr, newop, opcount, opCount_perRow);
							opcount++;
							if (newop.canTool) {
								opCount_perRow++; //工具栏操作每行计数
							}
						}
					}
					if (procCount > 0) {
						toolbarparam.groupSeparator.push(opcount);
					}
					groupCount++;
				}else if (cs[i].tagName == "iconstyle") {
					showtype = cs[i].getAttribute("showstyle");
					toolbarparam.iconStyle = showtype;
				}else if (cs[i].tagName == "customize") {
					customize = cs[i].getAttribute("value");
				}
			}
			configImg = document.getElementById("toolkitConfig");
			if (configImg) {
				configImg.style.display = customize ? "block" : "none";
			}
			_prepareMenu();
			api.broadcast("resize");
		},
		newTR = function(rowCount){
			var otable = $("#toolTable"),
				newRow = $("<ul></ul>");
			otable.append(newRow);
			newRow.attr("id", "toolTR" + rowCount);
			if (!showFlag[rowCount]){
				newRow[0].style.display = "none";
			}
			$("#toolTabTR").show();
			addFirstTab();
			addTab(rowCount);
			return newRow;
		},
		//增加一个TAB页---对应一个新的操作组
		addTab = function(rowCount){
			var oCell = $("<li></li>");
			$("#toolTabTR").append(oCell);
			oCell.attr({"id":"toolTab" + rowCount,"rowCount":rowCount}).mouseover(rowCount,tabClick);
			
			if (rowCount == 0){
				oCell.addClass("tabShow fst");
			}
			else{
				oCell.addClass("tabHide");
			}

			addText(tabNames[rowCount], oCell);
		},
		addFirstTab = function(){
			if ($("#toolTabTR").children().length == 0){
				addTab(0);
				$("#toolTable").addClass("tabHr toolTabContent");
			}
		},
		//TAB点击：显示某一行，隐藏其他行。TAB的显示风格调整
		tabClick = function(event){
			var rowCount = event.data,
				curRowId = rowCount === 0 ? "#toolTR" : "#toolTR"+rowCount,
				curTabId = "#toolTab" + rowCount;
			hideAllRows();
			resetTabStyle();
			
			$(curTabId).removeClass("tabHide").addClass("tabShow");

			// src.className = "tabShow";
			
			$(curRowId).show();
			// var curRowID = "toolTR" + rowCount;
			// if (rowCount == 0){
			// 	curRowID = "toolTR";
			// }
			// var curRow = document.getElementById(curRowID);
			// if (curRow){
			// 	curRow.style.display = "block";
			// }
		},
		hideAllRows = function(){
			$("#toolTable").children().hide();
			// for (var i = 1; i < 10; i++){
			// 	var curRow = document.getElementById("toolTR" + i);
			// 	if (curRow){
			// 		curRow.style.display = "none";
			// 	}
			// }
			// var curRow = document.getElementById("toolTR");
			// if (curRow){
			// 	curRow.style.display = "none";
			// }
		},
		resetTabStyle = function(){
			for (var i = 0; i < 10; i++){
				var tab = $("#toolTab" + i);
				if (tab.length){
					tab.removeClass().addClass("tabHide");
					if(i === 0){
						tab.addClass("fst");
					}
				}	
			}
		},
		//第二次显示操作按钮前，先把上一次的清除
		rowClear = function(){
			var rows = $("#toolTable").children(),
				l = rows.length,
				row,
				i;
			for ( i = 0; i < l; i++) {
				row = $(rows[i]);
				row.empty();
				if(i==0){
					continue;
				}
				row.remove();
			}
			var mainTool = $("#toolTR");
			
			mainTool[0].style.display = "block";
			//清除TAB
			var tabTR = $("#toolTabTR").removeClass().empty();
			$("#toolTable").removeClass();
			$("#toolTabTR").hide();
		},
		//显示一个操作
		parseOperation = function(otr, newop, i, opCount_perRow){
			if (!newop.canTool) {
				return;
			}
			var text = newop.text,
			//增加一个表格，把操作显示出来
				cellPos = opCount_perRow,
				oCell = $("<li></li>"),
				iconStyle = toolbarparam.iconStyle;
			otr.append(oCell);

			oCell.attr({"opid":i,"title":text}).addClass("toolButton").click(i,process);

			//1：只有文字；2：只有图标；3:图左文右 4：图上文下
			switch(parseInt(iconStyle)){
				case 1 :
					addText(newop.text, oCell);
					oCell.addClass("onlyText");
					break;
				case 2 :
					addText(newop.text, oCell);
					addIcon(newop.imgurl, oCell);
					oCell.addClass("onlyIcon");
					break;
				case 3 :
					addText(newop.text, oCell);
					addIcon(newop.imgurl, oCell);
					oCell.addClass("lIconRText");
					break;
				default :
					addText(newop.text, oCell,true);
					addIcon(newop.imgurl, oCell);
					oCell.addClass("tIconBText");
					break;
			}
		},

		/**
			判断是否选择了多个文档——用在垃圾箱判断中。 
			把docIDs最后的逗号去掉后，看是否还有逗号 . 
		**/
		isMultiDoc = function(){
			if (!toolbarparam.docIDs) return false;

			var tmp = toolbarparam.docIDs.substring(0, toolbarparam.docIDs.length - 1);
			if (tmp.indexOf(",") < 0) return false;
			else return true;
		},
		//显示垃圾箱的操作
		showGarbageBin = function(){
			if (toolbarparam.docIDs){
				toolbarparam.add(garbagebin_restore());
				toolbarparam.add(garbagebin_delete());
				if (!isMultiDoc()) toolbarparam.add(garbagebin_log());
			}
			rowClear();
			if (toolbarparam.docIDs){
				var otr = $("#toolTR");
				parseOperation(otr, garbagebin_restore(), 0, 0);
				parseOperation(otr, garbagebin_delete(), 1, 1);
				if (!isMultiDoc()) parseOperation(otr, garbagebin_log(), 2, 2);
			}
			_prepareMenu();
		},
		addOperation = function(oNode, i){
			var newop = assembleOperation(i, oNode);
			toolbarparam.add(newop);
			toolbarparam.flowNodeID = newop.flownode;

			return newop;
		},
		addSeparator = function(oCell){
			oCell.appendChild(document.createElement("br"));
			
			var blankDiv = document.createElement("div");
			blankDiv.setAttribute("class", "toolButtonSeparator");
			blankDiv.className = "toolButtonSeparator";

			oCell.appendChild(blankDiv);
		},
		addIcon = function(imgurl, oCell){
			imgurl = self.pathPrefix + imgurl;
			imgurl = "url('../" + imgurl + "')";
			oCell.find("div").css("background-image",imgurl);
		},
		addText = function(optext, oCell, topIconBottomText){
			// 暂时还未提供对按钮内部的结构进行修改方法，如需修改暂时先在这里对optext进行修改
			if(topIconBottomText){
				optext = "<table cellspacing='0' cellpadding='0' class='fillet btn'><tr><td class='LT'> </td><td class='CT'> </td><td class='RT'> </td></tr><tr><td class='LC'> </td><td class='CC'><div>"+ optext +"</div></td><td class='RC'> </td></tr><tr><td class='LB'> </td><td class='CB'> </td><td class='RB'> </td></tr></table>"
			}else{
				optext = "<table cellspacing='0' cellpadding='0' class='fillet btn'><tr><td class='L'></td><td class='C'><div>"+ optext +"</div></td><td class='R'></td></tr></table>"
			}
			oCell.html(optext);
		},
		//根据xml,创建一个操作对象,以放到toolkitparam参数中保存
		assembleOperation = function(i, oNode){
			var text 		= oNode.getAttribute("name");
			var opurl 		= oNode.getAttribute("opurl");
			var proctype 	= oNode.getAttribute("type");
			var procid 		= oNode.getAttribute("procid");
			var flownode 	= oNode.getAttribute("flownode");
			var opid		= oNode.getAttribute("opid");
			var callmode	= oNode.getAttribute("callmode");
			var opheight 	= oNode.getAttribute("height");
			var opwidth 	= oNode.getAttribute("width");
			var needprompt 	= oNode.getAttribute("needprompt");
			var resizable 	= oNode.getAttribute("resizable");
			var imgurl   	= oNode.getAttribute("imgurl");
			var dealcount   = oNode.getAttribute("dealcount");

			var newop = new Operation(i, proctype, procid, flownode, callmode, text,
				opheight, opwidth, resizable, opid, opurl, needprompt, imgurl, dealcount);

			newop.canMenu = self.tool_canMenuShow(toolbarparam, oNode);
			newop.canTool = self.tool_canToolShow(toolbarparam, oNode);
			
			return newop;
		},

		//工具栏上点击一个按钮后的响应
		process = function(event){
			//防连击
			var srcButton = $(this);
			srcButton.unbind("click");
			setTimeout(function(){srcButton.click(event.data, process)}, 500);

			callOperation(event.data);
		},

		//根据唯一的ID，调用操作
		callOperation = function(uniqueid){
			//取得操作本身的属性
			var op1 = toolbarparam.operations[uniqueid];
			if (!op1){
				alert("No operation object:" + uniqueid);
				return;
			}
			if ((op1.needprompt == "true") && !confirm(toolhint.sure + op1.text + "?"))	return;

			var canCallProc = false;
			//垃圾箱操作时，不经过BeforeProcess
			if (toolbarparam.extType == 1)
				canCallProc = true;
			else {
				//调用BeforeProcess 1
				var theURL = self.getBeforeURL(toolbarparam, op1);
				toolbarparam.uuid = "";
				toolbarparam.before = theURL;

				$.ajax({url:theURL, async:false, success:before1 ,dataType:'text'});

				if (toolbarparam.uuid) canCallProc = true;
			}

			//调用操作
			if (canCallProc) {
				//callOp(callMode, aWidth, aHeight, resizable, procType, theDocID)
				var opurl = self.getOpURL(toolbarparam, op1);
				callOp(op1.callmode, op1.opwidth, op1.opheight, op1.resizable, op1.proctype,
						toolbarparam.before_docIDs, opurl, op1.text);
			}
		},
		_fetchDocLibID = function() {
			return (toolbarparam.before_docLibIDs 
				? dealDocLibIDs(toolbarparam.before_docLibIDs)
				: toolbarparam.docLibID);
		},
		_fetchDocIDs = function() {
			return dealDocIDs(toolbarparam.before_docIDs);
		},
		//在调用操作前处理一下文档ID串，若是以逗号结尾，则去掉逗号
		dealDocIDs = function(docIDs){
			if (docIDs)
				docIDs = docIDs.substring(0, docIDs.length - 1);
			return docIDs;
		},
		//若是同库，则只返回一个ID
		dealDocLibIDs = function(docLibIDs){
			if (!docLibIDs) return "";

			var libArr = (docLibIDs + "").split(","),
				docLibID = libArr[0];

			for (var i = 1; i < libArr.length; i++){
				if (libArr[i] && libArr[i] != docLibID) {
					return docLibIDs.substring(0, docLibIDs.length - 1);
				}
			}
			
			return docLibID;
		},
		setBeforeResult = function(data){
			//data:uuid=....&docids=...&doclibids=...
			var splitArr = data.split("&");
			toolbarparam.uuid = splitArr[0].substring(5);
			toolbarparam.before_docIDs = splitArr[1].substring(7);//从before返回来的docIDs
			if (splitArr[2])
				toolbarparam.before_docLibIDs = splitArr[2].substring(10);//从before返回来的docLibIDs
		},
		before1 = function(data){
			//正常:直接返回
			if (data.indexOf("uuid=") == 0){
				setBeforeResult(data);
				return;
			}
			//各种异常的报告
			if ((data == "NOEXIST") || (data == "STATECHANGE"))
				alert(toolhint.stateChange);
			else if (data == "ALLLOCKED")
				alert(toolhint.allLocked);
			else if ((data == "ISLOCKED") && (confirm(toolhint.lockContinue))){
				//调用BeforeProcess 2
				var theURL = toolbarparam.before + "&action=next";
				$.ajax({url:theURL, async:false, success:before2});
			}
		},
		before2 = function(data){
			if (data.indexOf("uuid=") == 0)
				setBeforeResult(data);
			else
				alert(toolhint.exception);
		},
		callOp = function(callMode, aWidth, aHeight, resizable, procType, theDocID, opurl, opname){
			var feature = getFeature(callMode, aWidth, aHeight, resizable);
			if ('1' == callMode){//独立窗口
				var theWindowName = "_blank";
				if ((parseInt(procType) != 5) && theDocID)
					theWindowName = "WND" + theDocID.replace(/,/g, "_");
				var hWindow = window.open(opurl, theWindowName, feature, true);
				hWindow.focus();
				return;
			} else if ('3' == callMode){ //无窗口
				$.get(opurl, function(data){
					if (data.indexOf("@refresh@") >= 0) {
						self.closeOpDialog("OK");
						data = data.substring(9);
					}
					if (data) {
						alert(data);
					}
				});
			} else { //对话框
				aWidth = parseInt(aWidth);  //操作要求的宽和高
				aHeight = parseInt(aHeight);

				var sWidth = document.body.clientWidth; //窗口的宽和高
				var sHeight = document.body.clientHeight;
				
				if (aWidth + 10 > sWidth) aWidth = sWidth - 10;  //用e5.dialog时会额外加宽和高
				if (aHeight + 70 > sHeight) aHeight = sHeight - 70;
				
				//chrome下点击窗口的关闭时无法正确执行after.do，因此隐藏窗口关闭按钮，并不允许esc关闭
				//artDialog.defaults.esc = false;
				var showClose = !theDocID || !e5.utils.isChrome();
				curOpDialog = e5.dialog({type:"iframe", value:opurl},
						{title:opname, width:aWidth, height:aHeight, resizable:(resizable=="true")
						,showClose:showClose});
				curOpDialog.show();
			}
		},
		/**==取打开窗口的风格==*/
		getFeature = function(callMode, aWidth, aHeight, resizable){
			var feature;
			if (aWidth <= 0) aWidth= defaultOperationWidth;
			if (aHeight <= 0) aHeight= defaultOperationHeight;

		    if (callMode == '1'){ //独立窗口模式
				feature = 'directories=no,location=no,titlebar=no,toolbar=no,menubar=no,status=no';

				//打开窗口在中央
				var sWidth = screen.width - 50; //--去掉边--
				var sHeight = screen.height - 50;
				var x, y;
				if (sWidth > aWidth) x = (sWidth - aWidth)/2;
				else x = 0;
				if (sHeight > aHeight) y = (sHeight-aHeight)/2;
				else y = 0;

				feature += ', width=' + aWidth + ', height=' + aHeight + ', left=' + x + ', top=' + y;

				//调整大小
				if (resizable=='true') feature += ', resizable=1';
				else feature += ', resizable=0';
			} else { //对话框，只对IE有效
				feature = 'status=no; center=1; dialogWidth:' + aWidth + 'px; dialogHeight:' + aHeight + 'px;';
				if (resizable == 'true') feature += 'resizable=1;';
				else feature += 'resizable=0;';
			}
			return feature;
		},
		toolkitConfig = function(){
			var feature = 'directories=no,location=no,titlebar=no,toolbar=no,menubar=no,status=no, '
				+ 'width=800, height=160, left=10, top=10, resizable=1';
			var url = self._getUrl(4)
				+ "?docLibID=" + toolbarparam.docLibID
				+ "&flowNodeID=" + toolbarparam.flowNodeID
				+ "&fvID=" + toolbarparam.fvID;
			var wndToolkitConfig = window.open(url, "ToolkitConfig", feature);
			wndToolkitConfig.focus();
		};

	//---------操作组成的右键菜单：列表的右键菜单--------------
	var _prepareMenu = function() {
			var rMenu = $("#" + listMenuID + " ul");
			rMenu.children().empty();
			rMenu.children().remove();
			
			var group_i = 0;
			var group_sep = toolbarparam.groupSeparator;
			var i = 0;
			while (true) {
				var op1 = toolbarparam.operations[i];
				if (!op1) break;
				
				if (op1.canMenu) {
					if (group_sep && group_sep.length > group_i && group_sep[group_i] == i) {
						group_i++;
					}
					var li = $("<li rmenu_id='" + i + "'>" + op1.text + "</li>");
					li.click(_menuClick);
					
					rMenu.append(li);
				}
				i++;
			}
		},
		_menuClick = function(evt) {
			if (evt) src = evt.target;
			else src = event.srcElement;
			
			var opidx = src.getAttribute("rmenu_id");
			
			var rMenu = $("#" + listMenuID);
			rMenu.css({"visibility" : "hidden"});

			callOperation(opidx);
		},
		_showRMenu = function(msgId, msgName, data) {
			$("#" + listMenuID + " ul").show();
			
			var rMenu = $("#" + listMenuID);
			var reviseX = rMenu.offset().left - rMenu.position().left;
			var reviseY = rMenu.offset().top - rMenu.position().top;
			var left = data.x - reviseX;
			var top = data.y - reviseY;
			rMenu.css({"top":top+"px", "left":left+"px", "visibility":"visible"});

			$("body").bind("mousedown", _onBodyMouseDown);
		},
		_hideRMenu = function() {
			var rMenu = $("#" + listMenuID);
			if (rMenu) rMenu.css({"visibility": "hidden"});
			$("body").unbind("mousedown", _onBodyMouseDown);
		},
		_onBodyMouseDown = function(event){
			if (!(event.target.id == listMenuID || $(event.target).parents("#" + listMenuID).length>0)) {
				var rMenu = $("#" + listMenuID);
				rMenu.css({"visibility" : "hidden"});
			}
		};

	//跨页选中
	var recordList = [],
		_listRemember = function(msgId, msgName, data) {
			recordList = data;
		},
		//取得跨页选中记录的DocLibIDs
		_rememberLibIDs = function() {
			var ids = "";
			for (var i = 0; i < recordList.length; i++) {
				ids += recordList[i].libid;
			}
			return ids;
		},
		//取得跨页选中记录的DocIDs
		_rememberIDs = function() {
			var ids = "";
			for (var i = 0; i < recordList.length; i++) {
				ids += recordList[i].id;
			}
			return ids;
		};
	var self = {
		pathPrefix : "",
		urls : [
			"e5workspace/manoeuvre/CommonOp.do?action=restore", //0
			"e5workspace/manoeuvre/CommonOp.do?action=clean",	//1
			"e5workspace/manoeuvre/FlowRecordList.do",			//2
			"../e5workspace/before.do",							//3
			"../e5workspace/e5profile/toolbarCfg.do",			//4
			"../e5workspace/toolkit.do",						//5
			"../e5workspace/toolkitFree.do",					//6
		],
		_getUrl : function(index) {
			return self.pathPrefix + self.urls[index];
		},
		getToolUrl : function(param) {
			//增加不带权限的处理
			var theURL = (param["opFree"]) ? self._getUrl(6) : self._getUrl(5);
			theURL += "?DocLibID=" + param.docLibID//去读工具栏操作时，仍然传入多库ID
				+ "&FVID=" + param.fvID
				+ "&DocIDs=" + param.docIDs
				+ "&IsQuery=" + param.isQuery
				+ "&IsRule=" + param.isRule;
			if (param["extType"]){
				theURL += "&extType=" + param["extType"];
			}
			if (param["code"]){
				theURL += "&code=" + param["code"];
			}
			return theURL;
			
		},
		getBeforeURL : function(param, op1) {
			//调用BeforeProcess 1
			var theURL = self._getUrl(3)
				+ "?DocLibID=" + param.docLibID
				+ (op1.dealcount > 0 ? "&DocIDs=" + param.docIDs : "")
				+ "&ProcType=" + op1.proctype
				+ "&ProcID=" + op1.procid
				+ "&FlowNodeID=" + op1.flownode
				+ "&OpID=" + op1.opid
				;
			return theURL;
		},
		getOpURL : function(param, op1) {
			var opurl = self.pathPrefix + "../" + op1.opurl;
			opurl += (opurl.indexOf("?") < 0) ? "?" : "&";

			//opurl += "DocLibID=" + param.docLibID
			opurl += "DocLibID=" + _fetchDocLibID()
					+ (op1.dealcount > 0 ? ("&DocIDs=" + _fetchDocIDs()) : "")
					+ "&FVID=" + param.fvID
					+ "&UUID=" + param.uuid
					;
			if (param.extParams)
				opurl += "&" + param.extParams;
			
			return opurl;
		},
		tool_canMenuShow : function(param, cs) {
			var showtype = cs.getAttribute("showtype");
			if ((showtype & 2) == 2) {
				return true;
			}
			return false;
		},
		tool_canToolShow : function(param, cs) {
			var showtype = cs.getAttribute("showtype");
			if ((showtype & 1) == 1) return true;
			return false;
		},
		closeOpDialog : function(ret, callMode) {
			if (ret == "OK") {
				api.broadcast("refreshTopic", "true");
			}
			if (callMode == 2) {
				if (curOpDialog) curOpDialog.closeEvt();
				curOpDialog = null;
			}
		}
	}
	//-----init & onload--------
	var init = function(sandbox){
			api = sandbox;
			toolbarparam = new ToolkitParam();
			
			api.listen("workspace.doclist:doclistTopic", listening);
			//响应列表上的右键菜单消息，把操作组装成右键菜单
			api.listen("workspace.doclist:showMenu", _showRMenu);
			//跨页选中功能
			api.listen("workspace.doclist:listRemember", _listRemember);
		};

	return {
		init: init,
		self: self
	}
},{requires:["../e5script/jquery/jquery.min.js", 
"../e5script/e5.utils.js", 
"../e5workspace/script/Param.js", 
"../e5script/jquery/jquery.dialog.js", 
"../e5script/jquery/dialog.style.css"//,
]});