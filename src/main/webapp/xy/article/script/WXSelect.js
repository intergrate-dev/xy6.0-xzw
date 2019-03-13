var push_WX = {
		DocIDs : 0,
		DocLibID : 0,
		init : function(){
			$('#btnSubmit').click(push_WX.doSubmit);
			$('#btnCancel').click(push_WX.doCancel);
			
			if (tmpPadID <= 0){
				$('#btnSubmit').hide();
			}
			window.onbeforeunload = e5_form_event.beforeExit;
		},
		doSubmit : function(){
			var param = push_WX.collectData();
			if (!param) return false;
			var paramString = JSON.stringify(param);
			$('#btnSubmit').attr("disabled",true);
			
			$.ajax({type: "POST", url: "../../xy/article/DealWXPush.do", async:false, 
				data : {
					"DocIDs" : push_WX.DocIDs,
					"DocLibID" : push_WX.DocLibID,
					"paramData" : paramString
				},
				success: push_WX.close,
				error: function (XMLHttpRequest, textStatus, errorThrown) {
					alert(errorThrown + ':' + textStatus);  // 错误处理
				}
			});
		},
		doCancel : function(){
			window.onbeforeunload = null;
			
			$("#btnSubmit").disabled = true;
			$("#btnCancel").disabled = true;
			
			e5_form_event.beforeExit();
		},
		close : function(msg) {
			if (msg != "ok") {
				alert("保存时异常：" + msg);
			} 
			window.onbeforeunload = null;
			$("#btnSubmit").disabled = true;
			$("#btnCancel").disabled = true;
			
			e5_form_event.beforeExit();
		},
		collectData : function(){	
			var dataArray = [];
	        var form0 = $("input[type='checkbox']:checked");
	        for(var i = 0; i < form0.length; i++){
	           //如果这个checkbox被选中了，在json中的这个对象后面添加
				var menuid = form0[i].id;
				//var accoutid = $("#" + menuid).attr("accountID");
				var accoutid = $(form0[i]).attr("accountID");
				
				var data ={id:menuid,accountID:accoutid};
				dataArray.push(data);
	        }      
	        return dataArray;
		}
}

$(function(){
	push_WX.init();
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
		var dataUrl = "../../e5workspace/after.do?UUID=" + UUID;
		
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