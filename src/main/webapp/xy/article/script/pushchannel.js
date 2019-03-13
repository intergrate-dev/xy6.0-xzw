//推送渠道
var push_channel = {
		columnFor : null, // 选择栏目的对应input名
		init : function() {
			//按钮的响应事件
			$(".btnColumn").click(push_channel.selectColumn); 	//主栏目
			$("#btnColumn").hide();
			$("#btnSubmit").click(push_channel.save);				//保存按钮点击事件
			$("#btnCancel").click(e5_form_event.doCancel);		//取消按钮点击事件	
			
			if( pushchannel.channelCount == 1 ){
				$("input[type='checkbox']").each(function(){
					   $(this).attr("checked",true);
					   $(this).hide();
				});	
			}
			
			//已推送到所有渠道，则不需要“确定”按钮了
			if( pushchannel.channelCount == 0 ){
				$("#btnSubmit").hide();
			}
			
			window.onbeforeunload = e5_form_event.beforeExit;
			
			push_channel.cookieColumns(); 
		},
		cookieColumns : function() {
			for (var i = 0; i < 10; i++) {
				var col = $("#" + i + "_columnID");
				if (col.length > 0) {
					var value = xy_cookie.getCookie("a_col" + i);
					if (!value) continue;
					
					var pos = value.indexOf(",");
					value = [value.substring(0,pos), value.substring(pos+1)];
					if (value[1] == "undefined") continue;
					if (value[1] == "") continue;
					console.log("开始判断栏目--> ["+value[0]+"] "+value[1]);
					if(push_channel.isOpCol(value[0],i)){
						col.val(value[0]);
						$("#" + i + "_column").val(value[1]);
					}
				}
			}
		},
		//判断Cookie中存储的栏目是否为可操作栏目
		isOpCol : function(colID,ch){
			var ret = false;
			$.ajax({
				url: "../../xy/column/isOpCol.do", 
				async:false,
				data : {"siteID" : pushchannel.siteID, "colID" : colID, "ch" : ch},
				success:function (data){
					if (data && data == 'true') ret = true;
					console.log("1-->"+ret);
				}
			});
			console.log("2-->"+ret);
			return ret;
		},
		// 点击选择主栏目
		selectColumn : function(evt) {
			var src = $(evt.target);
			var name = src.attr("for");
			var id = name + "ID";
			var ch = src.attr("ch");
			push_channel.columnFor = name;
			// 顶点位置
			var pos = e5_form_event._getDialogPos(document.getElementById(name));
			pos.left = (parseInt(pos.left) - 100) + "px";

			var dataUrl = "../../xy/column/ColumnCheck.jsp?cache=0&type=radio&ids=" + $("#" + id).val()
					+ "&ch=" + ch + "&siteID=" + pushchannel.siteID;
			e5_form_event.curDialog = e5.dialog({
				type : "iframe",
				value : dataUrl
			}, {
				showTitle : false,
				width : pos.width,
				height : 250,
				pos : pos,
				resizable : false
			});
			e5_form_event.curDialog.show();
		},
		//取消按钮。调after.do解锁
		doCancel : function(e) {
			window.onbeforeunload = null;
			
			$("#btnSave").disabled = true;
			$("#btnCancel").disabled = true;
			
			e5_form_event.beforeExit();
		},
		//点击保存按钮
		save : function() {
			var param = push_channel._collectData();
			if (!param) return false;
			
			push_channel._post(param);
		},
		_collectData : function(){
			// 读出form各字段，组织成对象 {'name1':'value1', 'name2':'value2'}
			var form = {};

			var form0 = $("#form select, #form :hidden, #form :text, #form textarea").serializeArray();
			for ( var i = 0; i < form0.length; i++) {
				form[form0[i].name] = form0[i].value;
			}

			var form0 = $("#form input[type='checkbox']");
			for ( var i = 0; i < form0.length; i++) {
				//如果这个checkbox被选中了，在json中的这个对象后面添加
				if (form0[i].checked) {
					var value = form[form0[i].name];
//					value = (value) ? value + "," + form0[i].value : form0[i].value;
					value = form0[i].value;
					form[form0[i].name] = value;
				}
			}
			return form;
		},
		_post : function(param) {
			push_channel._writeCookie(); 
			//Post方式下参数是字符串形式
			var paramString = JSON.stringify(param);
			
			$.ajax({type: "POST", url: "../../xy/article/DealChannel.do", async:false, 
				data : {
					"DocIDs" : pushchannel.DocIDs,
					"DocLibID" : pushchannel.DocLibID,
					"paramData" : paramString
				},
				success: push_channel.close,
				error: function (XMLHttpRequest, textStatus, errorThrown) {
					alert(errorThrown + ':' + textStatus);  // 错误处理
				}
			});

		},
		_writeCookie : function() {
			for (var i = 0; i < 10; i++) {
				var col = $("#" + i + "_columnID");
				if (col) {
					col = col.val() + "," + $("#" + i + "_column").val();
					xy_cookie.setCookie("a_col" + i, col);
				}
			}
		},
		close : function(msg) {
			if (msg != "ok") {
				alert("保存时异常：" + msg);
			} 
			window.onbeforeunload = null;
			$("#btnSave").disabled = true;
			$("#btnCancel").disabled = true;
			
			e5_form_event.beforeExit();
		}
};
$(function() {
	push_channel.init();
});

//复制以使用表单定制中的方法
e5_form_event = {
	//---各种事件、回调事件---
	curDialog : null,
	_getDialogPos : function(el) {
		function Pos (x, y) {
			this.x = x;
			this.y = y;
		}
		function getPos(el) {
			var r = new Pos(el.offsetLeft, el.offsetTop);
			if (el.offsetParent) {
				var tmp = getPos(el.offsetParent);
				r.x += tmp.x;
				r.y += tmp.y;
			}
			return r;
		}
		var p = getPos(el);
		
		//决定弹出窗口的高度和宽度
		var dWidth = 400;
		var dHeight = 300;

		var sWidth = document.body.clientWidth; //窗口的宽和高
		var sHeight = document.body.clientHeight;
		
		if (dWidth + 10 > sWidth) dWidth = sWidth - 10;//用e5.dialog时会额外加宽和高
		if (dHeight + 30 > sHeight) dHeight = sHeight - 30;
		
		//顶点位置
		var pos = {left : p.x +"px", 
			top : (p.y + el.offsetHeight - 1)+"px",
			width : dWidth,
			height : dHeight
			};
		if (pos.left + dWidth > sWidth)
			pos.left = sWidth - dWidth;
		if (pos.top + dHeight > sHeight)
			pos.top = sHeight - dHeight;
		
		return pos;
	},
	//取消按钮。调after.do解锁
	doCancel : function(e) {
		window.onbeforeunload = null;
		
		$("#btnSave").disabled = true;
		$("#btnCancel").disabled = true;
		
		e5_form_event.beforeExit();
	},
	//关闭窗口。调after.do解锁
	beforeExit : function(e) {
		var dataUrl = "../../e5workspace/after.do?UUID=" + pushchannel.UUID;
		
		//若是直接点窗口关闭，并且是chrome浏览器，则单独打开窗口
		if (e && e5_form_event.isChrome())
			window.open(dataUrl, "_blank", "width=10,height=10");
		else
			window.location.href = dataUrl;
	},
	isChrome : function() {
		var nav = e5_form_event.navigator();
		return nav.browser == "chrome";
	},
	navigator : function(){
		var ua = navigator.userAgent.toLowerCase();
		// trident IE11
		var re =/(trident|msie|firefox|chrome|opera|version).*?([\d.]+)/;
		var m = ua.match(re);
		
		var Sys = {};
		Sys.browser = m[1].replace(/version/, "'safari");
		Sys.ver = m[2];
		return Sys;
	}
};


//栏目选择窗口关闭，回调函数
function columnClose(filterChecked, checks) {
	// [ids, names, cascadeIDs]
	var name = push_channel.columnFor;
	$("#" + name + "ID").val(checks[0]);
	$("#" + name).val(checks[1]);
	
	columnCancel();
}

function columnCancel() {
	if (e5_form_event.curDialog)
		e5_form_event.curDialog.closeEvt();
	
	e5_form_event.curDialog = null;
}