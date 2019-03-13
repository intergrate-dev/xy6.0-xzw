var push_app = {
		init : function(){
			if(type == 0){
				$('#divPushTime').show();
			}
			$('#pushDescription').val(push_app.decode(description));
			$('#doSave').click(push_app.doSave);
			$('#doCancel').click(push_app.doCancel);
			
			push_app.initDateTimePickerWeb();
			
			window.onbeforeunload = e5_form_event.beforeExit;
		},
		decode : function(content) {
			content = content.replace(/&amp;/g, "&");
			content = content.replace(/&lt;/g, "<");
			content = content.replace(/&gt;/g, ">");
			//content = content.replace(/&quot;/g, "\"");
			content = content.replace(/&#034;/g, "\"");
			
			return content;
		},
		initDateTimePickerWeb : function(){
			$('#pushTime').datetimepicker({
				language : 'zh-CN',
				weekStart : 0,
				todayBtn : 1,
				autoclose : 1,
				todayHighlight : true,
				startView : 2,
				minView : 0,
				disabledDaysOfCurrentMonth : 0,
				forceParse : 0,
				pickerPosition: "bottom-left",
				format : 'yyyy-mm-dd hh:ii'
			});
					
			$('#pushTime').datetimepicker().on('changeDate', function(ev) {

			});
		},
		doSave : function(){
			$('#doSave').attr("disabled",true);
			//console.log("radiozhi" + $("input[name='targetUser']:checked").val()) ;
			$.ajax({type: "POST", url: "../../xy/article/AddPushTask.do", async:false, 
				data : {
					"DocIDs" : articleID,
					"DocLibID" : docLibID,
					"UUID" : UUID,
					"siteID" : siteID,
					"type" : type,
					"topicID" : topicID,
					"pushTime" : $('#pushTime').val(),
					"description" : $('#pushDescription').val(),
					"targetUser" : $("input[name='targetUser']:checked").val(),
					"push_regionIDS" : $('#push_regionIDS').val()
				},
				success: push_app.close,
				error: function (XMLHttpRequest, textStatus, errorThrown) {
					alert(errorThrown + ':' + textStatus);  // 错误处理
				}
			});
		},
		doCancel : function(){
			window.onbeforeunload = null;
			
			$("#btnSave").disabled = true;
			$("#btnCancel").disabled = true;
			
			e5_form_event.beforeExit();
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
}

$(function(){
	push_app.init();
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
	},
	selectCat : function(nameField, idField, catType, multiple) {
		//把"1_23_323"转成"1~23~323"
		var catIDs = e5_form.event._transformCatIDs(document.getElementById(idField).value);
		var theURL = "e5workspace/manoeuvre/CatSelect.do?noPermission=1&catType=" + catType + "&catIDs=" + catIDs;
		if (!multiple) theURL += "&multiple=false";

		e5_form_event._select(nameField, idField, theURL);
	},
	_select : function(nameField, idField, dataUrl){
		e5_form.event.curIDField = idField;
		e5_form.event.curNameField = nameField;
		
		//顶点位置
//		var pos = e5_form.event._getDialogPos(document.getElementById(nameField));
		var pos = {left : "0px",top : "0px",width : "1000px",height : "500px"};
		dataUrl = e5_form.dealUrl(dataUrl);
		
		e5_form.event.curDialog = e5.dialog({type:"iframe", value:dataUrl}, 
				{showTitle:false, width:pos.width, height:pos.height, pos:pos,resizable:false});
		e5_form.event.curDialog.show();
	},
};