//源稿签发
var original_submit = {
		columnFor : null, // 选择栏目的对应input名
		init : function() {
			//按钮的响应事件
			$(".btnColumn").click(original_submit.selectColumn); 	//主栏目
			$("#btnColumn").hide();
			$(".btnColumnRel").click(original_submit.selectColumns);	//关联栏目
			$(".btnPreview").click(original_submit.preview);	//预览
			$("#btnSubmit").click(original_submit.save);				//保存按钮点击事件
			$("#btnCancel").click(e5_form_event.doCancel);		//取消按钮点击事件
			
			if( original.channelCount == 1 ){
				$("input[type='checkbox']").each(function(){
					   $(this).attr("checked",true);
					   $(this).hide();
				});
				
			}
			
			//已签发所有渠道，则不需要“确定”按钮了
			if( original.channelCount == 0 ){
				$("#btnSubmit").hide();
			}
			
			window.onbeforeunload = e5_form_event.beforeExit;

			original_submit.cookieColumns();
			
		},
		//读cookie设原稿的发布渠道，不超过10个渠道。cookie名:a_col0,a_col1,...
		cookieColumns : function() {
			for (var i = 0; i < 10; i++) {
				var col = $("#" + i + "_columnID");
				if (col.length > 0) {
					var value = xy_cookie.getCookie("a_col" + i);
					if (!value) continue;
					
					var pos = value.indexOf(",");
					value = [value.substring(0,pos), value.substring(pos+1)];
					if (value[1] == "undefined") continue;
				
					col.val(value[0]);
					$("#" + i + "_column").val(value[1]);
				}
			}
		},
		// 点击选择主栏目
		selectColumn : function(evt) {
			var src = $(evt.target);
			var name = src.attr("for");
			var id = name + "ID";
			var ch = src.attr("ch");
			original_submit.columnFor = name;
			
			// 顶点位置
			var pos = e5_form_event._getDialogPos(document.getElementById(name));

			var dataUrl = "../../xy/column/ColumnCheck.jsp?cache=1&type=radio&ids=" + $("#" + id).val()
					+ "&ch=" + ch + "&siteID=" + original.siteID;
			e5_form_event.curDialog = e5.dialog({
				type : "iframe",
				value : dataUrl
			}, {
				showTitle : false,
				width : pos.width,
				height : 300,
				pos : pos,
				resizable : false
			});
			e5_form_event.curDialog.show();
		},
		// 点击选择关联栏目
		selectColumns : function(evt) {
			var src = $(evt.target);
			var name = src.attr("for");
			var id = name + "ID";
			var ch = src.attr("ch");
			
			original_submit.columnFor = name;
			// 顶点位置
			var pos = e5_form_event._getDialogPos(document.getElementById(name));

			var dataUrl = "../../xy/column/ColumnCheck.jsp?cache=1&type=op&ids=" + $("#" + id).val()
					+ "&ch=" + ch + "&siteID=" + original.siteID;
			e5_form_event.curDialog = e5.dialog({
				type : "iframe",
				value : dataUrl
			}, {
				showTitle : false,
				width : pos.width,
				height : 300,
				pos : pos,
				resizable : false
			});
			e5_form_event.curDialog.show();
		},
		preview : function(evt) {
			var src = $(evt.target);
			var ch = src.attr("ch");
			var colID = $("#" + ch + "_columnID").val();
			
			if(colID == ''){
				alert("确定主栏目后才可预览");
			} else {
				var dataUrl = "../../xy/article/Preview.do?DocLibID=" + original.DocLibID
				+ "&DocIDs=" + original.DocIDs + "&refCol=" + colID + "&ch=" + ch;
				window.open(dataUrl, "_blank");
			}
		},
		// 关联栏目中，自动滤掉主栏目
		filterCol : function(col, cols) {
			if (!cols || !col)
				return cols;

			cols = "," + cols + ","; // 前后加上逗号, 格式变成",1,2,3,"的样子
			cols = cols.replace("," + col + ",", ","); // 替换掉",1,"

			// 去掉前后的逗号
			if (cols.charAt(0) == ',') {
				cols = cols.substring(1);
			}
			if (cols.charAt(cols.length - 1) == ',') {
				cols = cols.substring(0, cols.length - 1);
			}
			return cols;
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
			var param = original_submit._collectData();
			if (!param) return false;
			
			original_submit._post(param);
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
			original_submit._writeCookie();
			//Post方式下参数是字符串形式
			var paramString = JSON.stringify(param);
			
			$.ajax({type: "POST", url: "../../xy/article/OriginalSign.do", async:false, 
				data : {
					"DocIDs" : original.DocIDs,
					"DocLibID" : original.DocLibID,
					"paramData" : paramString
				},
				success: original_submit.close,
				error: function (XMLHttpRequest, textStatus, errorThrown) {
					alert(errorThrown + ':' + textStatus);  // 错误处理
				}
			});

		},
		//写cookie：原稿发布选择的栏目
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
	original_submit.init();
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
		var dataUrl = "../../e5workspace/after.do?UUID=" + original.UUID;
		
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
	var name = original_submit.columnFor;
	
	if (original_submit.columnFor.indexOf("columnRel") > 0 ) {
		// 关联栏目中，自动滤掉主栏目
		var colIDs = original_submit.filterCol($("#" + name.slice(0,-3) + "ID").val(), checks[0]);
		var cols = original_submit.filterCol($("#" + name.slice(0,-3)).val(), checks[1]);

		$("#" + name + "ID").val(colIDs);
		$("#" + name).val(cols);
	} else {
		// 关联栏目中，自动滤掉主栏目
		var colIDs = original_submit.filterCol($("#" + name + "ID").val(), checks[0]);
		var cols = original_submit.filterCol($("#" + name).val(), checks[1]);
		
		$("#" + name + "RefID").val(colIDs);
		$("#" + name + "Ref").val(cols);
		
		$("#" + name + "ID").val(checks[0]);
		$("#" + name).val(checks[1]);
	}
	columnCancel();
}

function columnCancel() {
	if (e5_form_event.curDialog)
		e5_form_event.curDialog.closeEvt();
	
	e5_form_event.curDialog = null;
}