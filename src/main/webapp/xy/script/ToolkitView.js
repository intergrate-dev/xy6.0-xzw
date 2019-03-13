//细览页的Toolkit
e5.mod("workspace.toolkit",function() {
	var api, toolbarparam, 
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
				return;
			}
			var cs = oXmlDoc.documentElement.childNodes,
				l = cs.length,
				opcount = 0,
				otr = $("#toolTR"),
				opCount_perRow = 0, //每行中的操作个数
				i,j,procs,procCount,newop,showtype,configImg;
			
			addBackBtn(otr);
			
			for (i = 0; i < l; i++) {
				if (cs[i].tagName == "group") {
					procs = cs[i].childNodes;
					procCount = procs.length;

					for (j = 0; j < procCount; j++){
						if (procs[j].tagName == "proc") {
							newop = addOperation(procs[j], opcount);
							parseOperation(otr, newop, opcount, opCount_perRow);
							opcount++;
							//约定：细览上可显示的操作，是列操作
							if (newop.canMenu) {
								opCount_perRow++; //工具栏操作每行计数
							}
						}
					}
				}
			}
		},
		//显示返回按钮
		addBackBtn = function(otr){
			var text = "返回",
				oCell = $("<li></li>");
			otr.append(oCell);
			oCell.attr({"opid":0, "title":text}).addClass("toolButton").click(goBack);
			addText(text, oCell);
			oCell.addClass("lIconRText");
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
				oCell = $("<li></li>");
			otr.append(oCell);

			oCell.attr({"opid":i,"title":text}).addClass("toolButton").click(i,process);
			addText(newop.text, oCell);
			oCell.addClass("lIconRText");
		},
		tool_canMenuShow = function(cs) {
			//细览上显示的操作是可显示在操作栏的操作（流程操作）
			var showtype = cs.getAttribute("showtype");
			return ((showtype & 1) == 1);
		},
		addOperation = function(oNode, i){
			var newop = assembleOperation(i, oNode);
			toolbarparam.add(newop);
			toolbarparam.flowNodeID = newop.flownode;

			return newop;
		},
		addText = function(optext, oCell, topIconBottomText){
			optext = "<button type='button' class='btn btn-small'>"+ optext +"</button>"
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
					+ (op1.dealcount > 0 ? ("&DocIDs=" + toolbarparam.before_docIDs) : "")
					+ "&FVID=" + toolbarparam.fvID
					+ "&UUID=" + toolbarparam.uuid
					;
			if (toolbarparam.extParams)
				opurl += "&" + toolbarparam.extParams;
			
			return opurl;
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
			if ('1' == callMode){//独立窗口
				window.open(opurl);
				return;
			} else if ('3' == callMode){ //无窗口
				$.get(opurl, function(data){
					if (data.indexOf("@refresh@") >= 0) {
						closeOpDialog("OK");
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
				
				curOpDialog = e5.dialog({type:"iframe", value:opurl},
					{title:opname, width:aWidth, height:aHeight, resizable:(resizable=="true")});
				curOpDialog.show();
			}
		},
		goBack = function() {
			window.close();
		},
		closeOpDialog = function(ret, callMode) {
			if (ret == "OK") {
				//刷新列表
				try {
					if (window.opener && window.opener.e5) {
						var doclist = window.opener.e5.mods["workspace.doclist"];
						doclist.self.refreshPage();
					}
				} catch (e){}
				
				window.close();
			}
		};
	var self = {
		pathPrefix : "",
		urls : [
			"", //0
			"",	//1
			"e5workspace/manoeuvre/FlowRecordList.do",	//2
			"../e5workspace/before.do",					//3
			"",	//4
			"../xy/toolkitFlow.do",						//5
		],
		_getUrl : function(index) {
			return self.pathPrefix + self.urls[index];
		},
		closeOpDialog : function(ret, callMode) {
			if (callMode == 2) {
				if (curOpDialog) curOpDialog.closeEvt();
				curOpDialog = null;
			}
			closeOpDialog(ret, callMode);
		}
	}
	//-----init & onload--------
	var init = function(sandbox){
			api = sandbox;
			
			toolbarparam = new ToolkitParam();
			toolbarparam.docLibID = view_info.docLibID;
			toolbarparam.docIDs = view_info.docID;
			toolbarparam.fvID = view_info.fvID;
			
			toolbar(toolbarparam);
		};
	return {
		init: init,
		self: self
	}
});
