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
			//加栏目ID
			if (param["colID"]) {
				theURL += "&colID=" + param["colID"];
			}

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
		// showFlag = [true, false, false, false, false, false, false, false, false, false],
		// tabNames = ["主操作", "扩展操作", "其他操作", "操作组四", "操作组五", "操作组六", "操作组七", "操作组八", "操作组九", "操作组十"],

		showToolkit = function(oXmlDoc) {
			rowClear();
			toolbarparam.flowNodeID = 0;//每次新显示工具条前，先把保存的flowNodeID清空
			
			if ((oXmlDoc == null) || (oXmlDoc.documentElement == null)){
				alert("no xmldoc");
				return;
			}

			hasArticleBtn = false; //重置写稿组操作标记
			var cs = oXmlDoc.documentElement.childNodes,
				l = cs.length,
				opcount = 0,
				otr = $("#toolTR"),
				customize = "",
				// rowCount = 0,	//行数
				// groupCount = 0, //组数
				opCount_perRow = 0, //每行中的操作个数
				i,j,procs,procCount,newop,showtype,configImg;
			toolbarparam.groupSeparator = [];
			for (i = 0; i < l; i++) {
				if (cs[i].tagName == "group") {
					procs = cs[i].childNodes;
					procCount = procs.length;

					for (j = 0; j < procCount; j++){
						if (procs[j].tagName == "proc") {
							newop = addOperation(procs[j], opcount);
							parseOperation(otr, newop, opcount/*, opCount_perRow*/);
							opcount++;
						}
					}
					if (procCount > 0) {
						toolbarparam.groupSeparator.push(opcount);
					}
				} else if (cs[i].tagName == "proc") {
					newop = addOperation(cs[i], opcount);
					parseOperation(otr, newop, opcount);
					opcount++;
				} else if (cs[i].tagName == "iconstyle") {
					showtype = cs[i].getAttribute("showstyle");
					toolbarparam.iconStyle = showtype;
				}else if (cs[i].tagName == "customize") {
					customize = cs[i].getAttribute("value");
				}
			}
			toolkitOverflow();
			_prepareMenu();
		},
		toolkitOverflow = function(){
			var ul = $("#toolTR"),
				div = $("#toolTable"),
				ulw = ul.width(),
				divw = div.width();
			if(ulw>divw){
				div.addClass("overflow");
			}else if(div.hasClass("overflow")){
				div.removeClass("overflow");
				ul.css("left",0);
			}
		},
		moveLeft = function(){
			var ul = $("#toolTR"),
				div = $("#toolTable");
			var sLeft = parseInt(ul.css("left"));
			if(sLeft>=0){
				ul.css("left",0);
				clearTimeout(timer);
				return;
			}
			if(!moveAble){
				clearTimeout(timer);
				return;
			}
			ul.css("left",sLeft+5);
			timer = setTimeout(function(){moveLeft()},16);
		},
		moveRight = function(){
			var ul = $("#toolTR"),
				div = $("#toolTable");
			var sLeft = parseInt(ul.css("left"));
			var borderRight = div.width()-ul.outerWidth(true);
			if(sLeft<=borderRight){
				ul.css("left",borderRight);
				clearTimeout(timer);
				return;
			}
			if(!moveAble){
				clearTimeout(timer);
				return;
			}
			ul.css("left",sLeft-5);
			timer = setTimeout(function(){moveRight()},16);
		},
		moveAble = true,
		//第二次显示操作按钮前，先把上一次的清除
		rowClear = function(){
			$("#toolTR").empty();
			$("#toolArticleBtns").empty();
		},
		
		//显示一个操作
		parseOperation = function(otr, newop, i/*, opCount_perRow*/){
			if (!newop.canTool) {
				return;
			}
			
			//若是写稿一组的操作，并且不是第一个，则创建到组区域，并显示下拉按钮
			if (isArticleBtn(newop) && hasArticleBtn) {
				$(".downIcon").css("display", "inline-block");

				otr = $("#toolArticleBtns"); //使操作改为创建到组区域
				newop.imgurl = null; //去掉图标显示
			}
			
			var text = newop.text,
				oCell = $("<li></li>");
			otr.append(oCell);

			oCell.attr({"opid":i,"title":text}).addClass("toolButton").click(i,process);

			if (newop.imgurl) {
				addText(newop.text, oCell, true);
				addIcon(newop.imgurl, oCell);
				oCell.addClass("lIconRText");
			} else {
				addText(newop.text, oCell, false);
				oCell.addClass("onlyText");
			}
			
			//若是第一个写稿组操作，加下拉箭头。为了IE9兼容，只能使用button元素
			if (isArticleBtn(newop) && !hasArticleBtn) {
				hasArticleBtn = true;
				var downIcon = $("<button class='btn btn-small' ><i class='caret'/></button>").addClass("downIcon").addClass("moreBtnn")
						.attr("title", "更多类型")
						.mouseover(showArticleBtns)
						.click(showArticleBtns)
						.hide(); //先隐藏，有多个写稿操作时才显示
				oCell.append(downIcon);
			}
		},

		/** 
		 * 写稿组操作
		 */
		hasArticleBtn = false, //写稿组操作的标记
		//是不是写稿操作
		isArticleBtn = function(newop) {
			return (newop.imgurl == "Icons/add.gif");
		},
		//写稿组操作列表显示
		showArticleBtns = function(evt) {
			evt.stopPropagation();
			evt.preventDefault();
			
			$("#toolArticleBtns").show();
			
			//位置，先找到写稿操作，然后做相对位置
			var oCell = $("#toolTR").find(".downIcon").parent().find("button:first");
			$("#toolArticleBtns").css({"top":oCell.offset().top + 25, "left":oCell.offset().left})
			
			$("body").bind("mousedown", _onArticleBtnsLeave); //鼠标点在其它区域时隐藏写稿操作下拉列表
		},
		_hideArticleBtns = function() {
			$("#toolArticleBtns").hide();
			$("body").unbind("mousedown", _onArticleBtnsLeave);
		},
		_onArticleBtnsLeave = function(event){
			if (!($(event.target).closest("#toolArticleBtns").length>0)) {
				_hideArticleBtns();
			}
		},
		
		/**
			判断是否选择了多个文档——用在垃圾箱判断中。 
		**/
		isMultiDoc = function(){
			if (!toolbarparam.docIDs) return false;

			var tmp = toolbarparam.docIDs;
			return(tmp.indexOf(",") > 0);
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
				parseOperation(otr, garbagebin_restore(), 0/*, 0*/);
				parseOperation(otr, garbagebin_delete(), 1/*, 1*/);
				if (!isMultiDoc()) parseOperation(otr, garbagebin_log(), 2/*, 2*/);
			}
			toolkitOverflow();
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
			oCell.find("i").css("background-image",imgurl);
		},
		addText = function(optext, oCell, topIconBottomText){
			if (topIconBottomText)
				optext = "<button type='button' class='btn btn-small'><i class='icon'></i> "+ optext +"</button>";
			else
				optext = "<button type='button' class='btn btn-small'>"+ optext +"</button>";
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
			_hideArticleBtns();
			
			//取得操作本身的属性
			var op1 = toolbarparam.operations[uniqueid];
			if (!op1){
				alert("No operation object:" + uniqueid);
				return;
			}
			_callOperation(op1, toolbarparam);
		},
		//列表上的列操作可以调用
		_callOperation = function(op1, param){
			// if ((op1.needprompt == "true") && !confirm(toolhint.sure + op1.text + "?"))	return;
            var _content;
            if (op1.text == "解锁") {
            	var lastOne = $("#"+param.docIDs+" td.lastEditor").text();
                lastOne = lastOne ? " "+lastOne+" " : "别人";
                _content = "该稿件正在被" + lastOne + "操作，请谨慎操作！您确定要解锁吗?";
            } else {
                _content = toolhint.sure + op1.text + "?";
            }
            if ((op1.needprompt == "true") && !confirm(_content))	return;
			var canCallProc = false;
			//垃圾箱操作时，不经过BeforeProcess
			if (param.extType == 1)
				canCallProc = true;
			else {
				//调用BeforeProcess 1
				var theURL = self.getBeforeURL(param, op1);
				param.uuid = "";
				param.before = theURL;

				$.ajax({url:theURL, async:false, success:function(data){before1(data,param)} ,dataType:'text'});

				if (param.uuid) canCallProc = true;
			}

			//调用操作
			if (canCallProc) {
				var opurl = self.getOpURL(param, op1);
				callOp(op1.callmode, op1.opwidth, op1.opheight, op1.resizable, op1.proctype,
						param.before_docIDs, opurl, op1.text);
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
			if (docIDs && docIDs.charAt(docIDs.length - 1) == ',')
				return docIDs.substring(0, docIDs.length - 1);
			return docIDs;
		},
		//若是同库，则只返回一个ID
		dealDocLibIDs = function(docLibIDs){
			if (!docLibIDs) return "";

			var libArr = (docLibIDs + "").split(","),
				docLibID = libArr[0];

			for (var i = 1; i < libArr.length; i++){
				if (libArr[i] && libArr[i] != docLibID) {
					return dealDocIDs(docLibIDs);
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
		before1 = function(data,param){
			var _content;
			//正常:直接返回
			if (data.indexOf("uuid=") == 0){
				setBeforeResult(data);
				return;
			}
			//各种异常的报告
			if ((data == "NOEXIST") || (data == "STATECHANGE"))
				alert(toolhint.stateChange);
			else if (data == "ALLLOCKED"){
//				alert(toolhint.allLocked);
				var lastOne = $("#"+param.docIDs+" td.lastEditor").text();
                lastOne = lastOne ? " "+lastOne+" " : "别人";
                _content = "文档正在被" + lastOne + "处理，您现在不能进行操作！";
				alert(_content)
			}

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
			//alert('callMode, aWidth, aHeight, resizable, procType, theDocID, opurl, opname'+callMode+"---"+ aWidth+"---"+aHeight+"---"+resizable+"---"+procType+"---"+theDocID+"---"+opurl+"---"+opname)
			var feature = getFeature(callMode, aWidth, aHeight, resizable);
			if ('1' == callMode){//独立窗口
				var theWindowName = "_blank";
				if ((parseInt(procType) != 5) && theDocID)
					theWindowName = "WND" + theDocID.replace(/,/g, "_");
				var hWindow = window.open(opurl, theWindowName, feature, true);

				//监听写稿页面关闭事件(点击浏览器关闭按钮) 页面关闭需要调after.do解锁 
				var _UUID=opurl.split('UUID=')[1].split("&")[0];
				if(opname=="写稿" || opname=="修改"|| opname=="重改"){
					var loop=setInterval(function(){
						if(hWindow.closed){
							clearInterval(loop);
							var dataUrl = "../e5workspace/after.do?UUID=" + _UUID;
							$.ajax({url:dataUrl,async:false});
						}
					},1000)
				}
				hWindow.focus();
				return;
			} else if ('3' == callMode){ //无窗口
				//下载稿件二维码,ajax请求是个字符型的,不使用ajax
				if(opurl.indexOf("download") >= 0){
					window.location.href=opurl;
				}else{
					$.get(opurl, function(data){
						if (data.indexOf("@refresh@") >= 0) {
							self.closeOpDialog("OK");
							data = data.substring(9);
						}
						if (data) {
							alert(data);
						}
					});
				}
			} else { //对话框
				aWidth = parseInt(aWidth);  //操作要求的宽和高
				aHeight = parseInt(aHeight);

				var sWidth = document.body.clientWidth; //窗口的宽和高
				var sHeight = document.body.clientHeight;
				
				if (aWidth + 10 > sWidth) aWidth = sWidth - 10;  //用e5.dialog时会额外加宽和高
				if (aHeight + 70 > sHeight) aHeight = sHeight - 70;
				
				//chrome下点击窗口的关闭时无法正确执行after.do，因此隐藏窗口关闭按钮，并不允许esc关闭
				var showClose = !theDocID || !e5.utils.isChrome();
				curOpDialog = e5.dialog({type:"iframe", value:opurl},
						{title:opname, width:aWidth, height:aHeight, resizable:(resizable=="true")
						,showClose:showClose,esc:false});
				curOpDialog.show();
			}
		},
		/**==取打开窗口的风格==*/
		getFeature = function(callMode, aWidth, aHeight, resizable){
			var feature;
			if (aWidth <= 0) aWidth= defaultOperationWidth;
			if (aHeight <= 0) aHeight= defaultOperationHeight;

		    if (callMode == '1'){ //独立窗口模式
				// 不写scrollbars=yes新窗口没导航条啊
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
			var rMenu = $("#" + listMenuID);
			rMenu.empty();
			
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
					var li = $("<li rmenu_id='" + i + "'><a href='#'>" + op1.text + "</a></li>");
					li.click(_menuClick);
					
					rMenu.append(li);
				}
				i++;
			}
			
			//检查是否需调整位置
			if (rMenu.is(':visible')) {
				var _clientH = document.body.clientHeight;
				
				var menuHeight =  Math.round((rMenu.css("height") +"").replace("px",""));
				menuHeight = menuHeight > 26? menuHeight : 30;
				
				//如果，鼠标处于浏览器边缘，重新调整右键menu的位置
				if(rMenu.position().top + menuHeight > _clientH){
					var top = _clientH - menuHeight - 20;
					if (top < 0) top = 0;
					
					//设定menu的位置
					rMenu.css({"top":top});
				}
			}
		},
		_menuClick = function(evt) {
			var src = $(evt.target);
			if (!src.attr("rmenu_id")) src = src.parent();
			var opidx = src.attr("rmenu_id");
			
			_hideRMenu();
			
			callOperation(opidx);
			
			evt.stopPropagation();
			evt.preventDefault();
		},
		_showRMenu = function(msgId, msgName, data) {
			var rMenu = $("#" + listMenuID).show();
			
			var reviseX = rMenu.offset().left - rMenu.position().left;
			var reviseY = rMenu.offset().top - rMenu.position().top;
			var left = data.x - reviseX;
			var top = data.y - reviseY;
			
			//浏览器的宽高
			var _clientW = document.body.clientWidth;
			var _clientH = document.body.clientHeight;
			
			var menuHeight =  Math.round((rMenu.css("height") +"").replace("px",""));
            menuHeight = menuHeight > 26? menuHeight : 30;
            
            var menuWidth =  Math.round((rMenu.css("width") +"").replace("px",""));
			
            //如果，鼠标处于浏览器边缘，重新调整右键menu的位置
            if(_clientH - top < menuHeight ){
                top -= menuHeight + 2;
                if(top<0) top=0;
            }

            if(_clientW - left < menuWidth ){
                left -= menuWidth +2;
            }
            //设定menu的位置
            rMenu.css({"top":top, "left":left});
            $("body").bind("mousedown", _onBodyMouseDown);
		},
		_hideRMenu = function() {
			$("#" + listMenuID).hide();
			$("body").unbind("mousedown", _onBodyMouseDown);
		},
		_onBodyMouseDown = function(event){
			if (!($(event.target).closest("#" + listMenuID).length>0)) {
				_hideRMenu();
			}
		},
		_setPrivateNums = function(msgName, callerId, callerData){
			callerData = parseInt(callerData,10);
			if(typeof callerData == "number"){
				$("#privateNums").html(callerData);
			}
		}
		/*noSearchAdvArea = function(){
			$("#toggleSearchAdvList").addClass("disabled");
		}*/;

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
			"xy/article/Delete.do?g=1",							//1
			"e5workspace/manoeuvre/FlowRecordList.do",			//2
			"../e5workspace/before.do",							//3
			"../e5workspace/e5profile/toolbarCfg.do",			//4
			"../e5workspace/toolkit.do",						//5
			"../e5workspace/toolkitFree.do",					//6
			"../xy/toolkitFlow.do",					//7
			"../xy/toolOriginalFlow.do",					//8
		],
		_getUrl : function(index) {
			return self.pathPrefix + self.urls[index];
		},
		getToolUrl : function(param) {
			var theURL = (param["opFree"]) //不判断操作权限
					? self._getUrl(6) 
					: (param["opFlow"])//只显示流程操作
						? self._getUrl(7)
						: self._getUrl(5)
					;
			if(param.curTab=="cori"){//如果是源稿库
				theURL = self._getUrl(8);
			}
			theURL += "?DocLibID=" + param.docLibID//去读工具栏操作时，仍然传入多库ID
				+ "&FVID=" + param.fvID
				+ "&DocIDs=" + param.docIDs
				+ "&IsQuery=" + param.isQuery
				+ "&IsRule=" + param.isRule
				+ "&siteID=" + param.siteID//源稿库需要站点ID
				+ "&groupID=" + param.groupID;//源稿库需要分类栏目ID
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
			
			opurl += "DocLibID=" + _fetchDocLibID()
					+ (op1.dealcount > 0 ? ("&DocIDs=" + _fetchDocIDs(toolbarparam.before_docIDs)) : "")
					+ "&FVID=" + param.fvID
					+ "&UUID=" + param.uuid;
			
			if (opurl.indexOf("require-query-condition=true") >= 0){
				opurl += "&FilterID=" + param.filterID
						+ "&Query=" + e5.utils.encodeSpecialCode(param.query)
						+ "&RuleFormula=" + e5.utils.encodeSpecialCode(param.ruleFormula);
			}
			//翔宇中对操作增加的参数
			opurl = self.addParams(opurl, param);
			
			if (param.extParams)
				opurl += "&" + param.extParams;
			return opurl;
		},
		addParams : function(opurl, param) {
			//加站点ID
			if (typeof main_param != "undefined" && main_param["siteID"]) {
				opurl += "&siteID=" + main_param["siteID"];
			} else if (param["siteID"]) {
				opurl += "&siteID=" + param["siteID"];
			}
			//加栏目ID
			if (param["colID"]) {
				opurl += "&colID=" + param["colID"];
			}
			//加渠道
			if (param["ch"]) {
				opurl += "&ch=" + param["ch"];
			}
			//加分组ID
			if (param["groupID"]) {
				opurl += "&groupID=" + param["groupID"];
				if (param["siteField"]) opurl += "&siteField=" + param["siteField"];
				if (param["groupField"]) opurl += "&groupField=" + param["groupField"];
			}
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
		callOperation : function(op1, param){
			_callOperation(op1, param);
		},
		getParam : function(){
			return toolbarparam;
		},
		
		closeOpDialog : function(ret, callMode) {
			if (ret == "OK") {
				api.broadcast("refreshTopic", "true");
			}
			if (callMode == 2) {
				if (curOpDialog) curOpDialog.closeEvt();
				curOpDialog = null;
			}
		},
		closeDialog : function(ret, callMode,notClose) {
			if (ret == "OK") {
				api.broadcast("refresh", "true");
			}
			if (callMode == 2 && notClose != "true") {
				if (curOpDialog) curOpDialog.closeEvt();
				curOpDialog = null;
			}
		},
		//打开一个窗口。供列表上调用
		callOp : function(callMode, aWidth, aHeight, resizable, procType, theDocID, opurl, opname){
			callOp(callMode, aWidth, aHeight, resizable, procType, theDocID, opurl, opname);
		}
	}
	//-----init & onload--------
	var init = function(sandbox){
			api = sandbox;
			toolbarparam = new ToolkitParam();
			$("#toolTRLeft").mousedown(function(){moveAble=true;moveLeft();}).mouseup(function(){moveAble=false;});
			$("#toolTRRight").mousedown(function(){moveAble=true;moveRight();}).mouseup(function(){moveAble=false;});
			$("#showSearchArea").click(function(){
				var elm = $(this).children("i");
				if(elm.hasClass("icon-chevron-up")){
					elm.addClass("icon-chevron-down").removeClass("icon-chevron-up");
				}else{
					elm.addClass("icon-chevron-up").removeClass("icon-chevron-down");					
				}
				api.broadcast("showSearchArea");
			});
			api.listen("workspace.doclist:doclistTopic", listening);
			api.listen("workspace.doclist:showMenu", _showRMenu);
			api.listen("workspace.resize:windowResize",toolkitOverflow);
			api.listen("workspace.doclist:setPrivateNums",_setPrivateNums);
		};
	return {
		init: init,
		self: self
	}
},{requires:["../e5script/jquery/jquery.min.js", 
"../e5script/jquery/jquery.dialog.js", 
"../e5script/jquery/dialog.style.css"
]});