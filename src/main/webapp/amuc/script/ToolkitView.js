//细览页的Toolkit
e5.mod("workspace.toolkit",function() {
	var api, toolbarparam, 
		defaultOperationWidth= 400, //操作没有设置窗口大小时的缺省窗口宽度
		defaultOperationHeight= 300,//操作没有设置窗口大小时的缺省窗口高度
		defaultCountPerRow = 18, //每行可显示的操作数
		curOpDialog,

		toolbar = function(param){
			var theURL = self._getUrl(5)
				+ "?DocLibID=" + param.docLibID
				+ "&FVID=" + param.fvID
				+ "&DocIDs=" + param.docIDs;
			$.get(theURL, showToolkit, "xml");
		},

		showToolkit = function(oXmlDoc) {
			if ((oXmlDoc == null) || (oXmlDoc.documentElement == null)){
				$("#main_toolbar").hide();
				return;
				return;
			}

			var cs = oXmlDoc.documentElement.childNodes,
				l = cs.length,
				opcount = 0,
				otr = $("#toolTR"),
				opCount_perRow = 0, //每行中的操作个数
				i,j,procs,procCount,newop,showtype,configImg;
			for (i = 0; i < l; i++) {
				if (cs[i].tagName == "group") {
					procs = cs[i].childNodes;
					procCount = procs.length;

					for (j = 0; j < procCount; j++){
						if (procs[j].tagName == "proc") {
							newop = addOperation(procs[j], opcount);
							parseOperation(otr, newop, opcount, opCount_perRow);
							opcount++;
							//约定：细览上可显示的操作，是配置为右键菜单的操作
							if (newop.canMenu) {
								opCount_perRow++; //工具栏操作每行计数
							}
						}
					}
				}else if (cs[i].tagName == "iconstyle") {
					showtype = cs[i].getAttribute("showstyle");
					toolbarparam.iconStyle = showtype;
				}
			}
			if (opCount_perRow == 0) {
				$("#main_toolbar").hide();
				return;
			}
		},
		//显示一个操作
		parseOperation = function(otr, newop, i, opCount_perRow){
			//约定：细览上可显示的操作，是配置为右键菜单的操作
			if (!newop.canMenu) {
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

		tool_canMenuShow = function(cs) {
			var showtype = cs.getAttribute("showtype");
			if ((showtype & 2) == 2) {
				return true;
			}
			return false;
		},
		tool_canToolShow = function(cs) {
			var showtype = cs.getAttribute("showtype");
			if ((showtype & 1) == 1) return true;
			return false;
		},

		addOperation = function(oNode, i){
			var newop = assembleOperation(i, oNode);
			toolbarparam.add(newop);
			toolbarparam.flowNodeID = newop.flownode;

			return newop;
		},
		addIcon = function(imgurl, oCell){
			imgurl = self.pathPrefix + imgurl;
			imgurl = "url('../" + imgurl + "')";
			oCell.find("i").css("background-image",imgurl);
		},
		addText = function(optext, oCell, topIconBottomText){
			// 暂时还未提供对按钮内部的结构进行修改方法，如需修改暂时先在这里对optext进行修改
			// if(topIconBottomText){
				// optext = "<table cellspacing='0' cellpadding='0' class='fillet btn'><tr><td class='LT'> </td><td class='CT'> </td><td class='RT'> </td></tr><tr><td class='LC'> </td><td class='CC'><div>"+ optext +"</div></td><td class='RC'> </td></tr><tr><td class='LB'> </td><td class='CB'> </td><td class='RB'> </td></tr></table>"
			// }else{
				optext = "<button type='button' class='btn btn-small'><i class='icon'></i> "+ optext +"</button>"
			// }
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

			newop.canMenu = tool_canMenuShow(oNode);
			newop.canTool = tool_canToolShow(oNode);
			
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
			
			//调用BeforeProcess 1
			var theURL = _getBeforeURL(op1);
			toolbarparam.uuid = "";
			toolbarparam.before = theURL;

			$.ajax({url:theURL, async:false, success:before1 ,dataType:'text'});

			if (toolbarparam.uuid) canCallProc = true;

			//调用操作
			if (canCallProc) {
				//callOp(callMode, aWidth, aHeight, resizable, procType, theDocID)
				var opurl = _getOpURL(op1);
				callOp(op1.callmode, op1.opwidth, op1.opheight, op1.resizable, op1.proctype,
						toolbarparam.before_docIDs, opurl, op1.text);
			}
		},
		_getBeforeURL = function(op1) {
			//调用BeforeProcess 1
			var theURL = self._getUrl(3)
				+ "?DocLibID=" + toolbarparam.docLibID
				+ (op1.dealcount > 0 ? "&DocIDs=" + toolbarparam.docIDs : "")
				+ "&ProcType=" + op1.proctype
				+ "&ProcID=" + op1.procid
				+ "&FlowNodeID=" + op1.flownode
				+ "&OpID=" + op1.opid
				;
			return theURL;
		},
		_getOpURL = function(op1) {
			var opurl = "../" + op1.opurl;
			opurl += (opurl.indexOf("?") < 0) ? "?" : "&";

			opurl += "DocLibID=" + toolbarparam.docLibID
					+ (op1.dealcount > 0 ? ("&DocIDs=" + dealDocIDs(toolbarparam.before_docIDs)) : "")
					+ "&FVID=" + toolbarparam.fvID
					+ "&UUID=" + toolbarparam.uuid
					;
			if (toolbarparam.extParams)
				opurl += "&" + toolbarparam.extParams;
			
			return opurl;
		},
		dealDocIDs = function(docIDs){
			if (docIDs) return docIDs.substring(0, docIDs.length - 1);
			return docIDs;
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
						data = data.substring(9);
						if (data) alert(data);
						
						closeOpDialog("OK");
					} else {
						if (data) alert(data);
					}
				});
			} else { //对话框
				aWidth = parseInt(aWidth);  //操作要求的宽和高
				aHeight = parseInt(aHeight);

				var sWidth = document.body.clientWidth; //窗口的宽和高
				var sHeight = document.body.clientHeight;
				
				if (aWidth + 10 > sWidth) aWidth = sWidth - 10;  //用e5.dialog时会额外加宽和高
				if (aHeight + 70 > sHeight) aHeight = sHeight - 70;
				
				curOpDialog = e5.dialog({type:"iframe", value:opurl},{title:opname, width:aWidth, height:aHeight, resizable:(resizable=="true")});
				curOpDialog.show();
			}
		},
		closeOpDialog = function(ret, callMode) {
			if (callMode == 2) {
				if (curOpDialog) curOpDialog.closeEvt();
				curOpDialog = null;
			}
			if (ret == "OK") {
				window.location.reload();
			}
		},
		/**==取打开窗口的风格==*/
		getFeature = function(callMode, aWidth, aHeight, resizable){
			var feature;
			if (aWidth <= 0) aWidth= defaultOperationWidth;
			if (aHeight <= 0) aHeight= defaultOperationHeight;

		    if (callMode == '1'){ //独立窗口模式
				feature = 'directories=no,location=no,titlebar=no,toolbar=no,menubar=no,status=no,scrollbars=yes';

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
		],
		_getUrl : function(index) {
			return self.pathPrefix + self.urls[index];
		},
		closeOpDialog : function(ret, callMode) {
			if (callMode == 2) {
				if (curOpDialog) curOpDialog.closeEvt();
				curOpDialog = null;
			}
			if (ret == "OK") {
				window.location.reload();
			}
		},
		//细览页有操作，可能改变文档状态，因此关闭时，刷新列表。
		exit : function() {
			if ($("#main_toolbar").css("display") == "none") {
				return;
			}
			//取opener（主界面操作栏）的刷新，而不是本页直接发消息，否则可能被本页的列表响应
			try {
				if (window.opener && window.opener.e5) {
					var tool = window.opener.e5.mods["workspace.toolkit"];
					tool.self.closeOpDialog("OK", 1);
				}
			} catch (e){}
		}
	}
	//-----init & onload--------
	var init = function(sandbox){
			api = sandbox;
			
			toolbarparam = new ToolkitParam();
			toolbarparam.docLibID = currentDocLibID;
			toolbarparam.docIDs = currentDocID;
			toolbarparam.fvID = currentFVID;
			
			toolbar(toolbarparam);
			
			window.onbeforeunload = self.exit;
		};

	return {
		init: init,
		self: self
	}
});
