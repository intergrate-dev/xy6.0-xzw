var select_DS = {	
	init : function() {
		$('#btnSubmit').click(select_DS.doSubmit);
		$("#btnCancel").click(e5_form_event.doCancel);		//取消按钮点击事件
		//默认选中第一个数据源
		$('input:radio[name="sourceDS"][value="1"]').prop('checked', true);
	},
	doSubmit: function(){
		$('#btnSubmit').attr('disabled','true');
		var source = $('input[name="sourceDS"]:checked ').val(); 
		if(typeof (source) == "undefined"){
			alert("请选择发布范围！");
			return;
		}
		var theURL = "";
		if( selectDS.type == "deploy"){
			theURL = "../../xy/tenant/Deploy.do";
		}else if(selectDS.type == "archive"){
			theURL = "../../xy/tenant/DeployArchive.do";
		}
		
		$.ajax({				
			url : theURL,
			type : 'POST',
			data : {
				"DocIDs" : selectDS.DocIDs,
				"DocLibID" : selectDS.DocLibID,
				"datasource" : source
			},
			dataType : 'html', 
			success:function(msg, status){	
				select_DS.close(msg);
		}});
	},
	close : function(msg) {
		window.onbeforeunload = null;
		alert(msg);
		var url = "../../e5workspace/after.do?UUID=" + selectDS.UUID + "&DocIDs=" + selectDS.DocIDs;
		$("#frmSelectDS").attr("src", url);
	}
}

$(function(){
	select_DS.init();
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
		var dataUrl = "../../e5workspace/after.do?UUID=" + selectDS.UUID;
		
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
