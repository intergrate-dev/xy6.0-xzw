//源稿签发、送审、签发并送审
var original_censorship = {
		columnFor : null, // 选择栏目的对应input名
		reason : "",
		init : function() {
			$("#main").hide();
			$("#form").hide();
			$("#doSubmitAndPub").hide();
			//如果是送审、预签并送审  ----展示意见填写框 和 审核日志
			//alert(original.articleType);
			if(original.type==0||original.type==2){
				$("#main").show();
				var theURL = "../../xy/article/CensorshipLog.do?DocIDs=" + original.DocIDs
					+ "&DocLibID=" + original.DocLibID;
				$.ajax({url:theURL, async:false, success:function(data){
					drawTable(data.logList);
					drawSelect(data.flowNames);
				}});
			}
			//如果是签发、预签并送审
			if(original.type==1||original.type==2){
				$("#form").show();
				if(original.type==1){//如果是签发
					$("#doSubmit").val("签发");
					if(original.channelCount > 0){//微信稿 隐藏 签发并发布
						$("#doSubmitAndPub").show();
					}
				}
			}

			//按钮的响应事件
			$(".btnColumn").click(original_censorship.selectColumn); 	//主栏目
			$("#btnColumn").hide();
			$(".btnColumnRel").click(original_censorship.selectColumns);	//关联栏目
			$(".wxColumn").click(original_censorship.selectWeixin); 	//微信公众号
			$("#doSubmit").click(original_censorship.save);				//保存按钮点击事件
			$("#doSubmitAndPub").click(original_censorship.saveAndPub);		//签发并发布按钮点击事件
			$("#doCancel").click(e5_form_event.doCancel);		//取消按钮点击事件
				
			if( original.channelCount == 1 ){
				$("input[type='checkbox']").each(function(){
					   $(this).attr("checked",true);
					   $(this).hide();
				});
					
			}
			if(original.UUID) window.onbeforeunload = e5_form_event.beforeExit;

			//original_censorship.cookieColumns();
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
			original_censorship.columnFor = name;
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
		// 点击选择微信公众号
		selectWeixin : function(evt) {
			var src = $(evt.target);
			var name = src.attr("for");
			original_censorship.columnFor = name;
			
			// 顶点位置
			var pos = e5_form_event._getDialogPos(document.getElementById(name));

			var dataUrl = "../../xy/wx/getAccounts.do?checkType=1&checkIds=" + $("#oldWeixinID").val()
					+ "&siteID=" + original.siteID;
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
			
			original_censorship.columnFor = name;
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
		/*
		doCancel : function(e) {
			alert(original.UUID);
			if (original.UUID) {
				window.onbeforeunload = null;
				
				$("#btnSave").disabled = true;
				$("#btnCancel").disabled = true;
				
				e5_form_event.beforeExit();
			} else {
				window.close();
			}
		},
		*/
		//点击保存按钮
		save : function() {
			var param = original_censorship._collectData();
			
			if (!param) return false;
			
			original_censorship._post(param);
		},
		//点击签发并发布按钮
		saveAndPub : function() {
			var param = original_censorship._collectData();
			
			if (!param) return false;
			original.type = 3;
			original_censorship._post(param);
		},
		_collectData : function(){
			// 读出form各字段，组织成对象 {'name1':'value1', 'name2':'value2'}
			var form = {};

			var form0 = $("#form select, #form :hidden, #form :text, #form textarea").serializeArray();
			for ( var i = 0; i < form0.length; i++) {
				form[form0[i].name] = form0[i].value;
			}
			
			var isTrue = false;
			var form0 = $("#form input[type='checkbox']");
			for ( var i = 0; i < form0.length; i++) {
				//如果这个checkbox被选中了，在json中的这个对象后面添加
				if (form0[i].checked) {
					var value = form[form0[i].name];
//					value = (value) ? value + "," + form0[i].value : form0[i].value;
					value = form0[i].value;
					form[form0[i].name] = value;
					isTrue = true;
				}
			}
			
			if(original.type==1){
				if(isTrue){
					if(original.channelCount > 0){
						var column1 = $("#0_column").val();
						var column2 = $("#1_column").val();
						if(column1=="" && column2=="" && weixinName==""){
							alert("请先选择需要签发的栏目！");
							return null;
						}
					} else {
						var weixinName = $("#weixinName").val();
						if(weixinName==""){
							alert("请先选择需要签发的栏目！");
							return null;
						}
					}
				}else{
					alert("请先勾选需要签发的渠道！");
					return null;
				}
			}
			
			return form;
		},
		_post : function(param) {
			//original_censorship._writeCookie();
			//Post方式下参数是字符串形式
			var paramString = JSON.stringify(param);
			
			reason = $("#censorshipReason").val();
			
			$.ajax({type: "POST", url: "../../xy/article/Censorship.do", async:false, 
				data : {
					"DocIDs" : original.DocIDs,
					"DocLibID" : original.DocLibID,
					"IsEditor" : original.IsEditor,
					"Reason" : reason,
					"paramData" : paramString,
					"scope" : $('input:radio[name="scope"]:checked').val(),
					"flownode" : $("#selectCensorship").val(),
					"type" : original.type
				},
				success: function(msg){
					if(msg == "uncheck"){
						alert("校对尚未完成，无法签发！");
					}else if(msg == "failed"){
						original.type = 1;
						alert("没有正确选择签发的渠道和栏目，无法签发！");
					}else{
						var successStr = msg.split("success")[1];
						operationSuccess(successStr);
					}
				},
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
		}
		/*,
		close : function(msg) {
			alert(original.UUID);
			if (original.UUID) {
				if (msg != "ok") {
					alert("保存时异常：" + msg);
				} 
				window.onbeforeunload = null;
				$("#btnSave").disabled = true;
				$("#btnCancel").disabled = true;
				
				e5_form_event.beforeExit();
			} else {
				window.close();
			}
		}*/
};
$(function() {
	original_censorship.init();
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
		if (original.UUID) {
			window.onbeforeunload = null;
			
			$("#btnSave").disabled = true;
			$("#btnCancel").disabled = true;
			
			e5_form_event.beforeExit();
		} else {
			window.close();
		}
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
	var name = original_censorship.columnFor;
	
	if (original_censorship.columnFor.indexOf("columnRel") > 0 ) {
		// 关联栏目中，自动滤掉主栏目
		var colIDs = original_censorship.filterCol($("#" + name.slice(0,-3) + "ID").val(), checks[0]);
		var cols = original_censorship.filterCol($("#" + name.slice(0,-3)).val(), checks[1]);

		$("#" + name + "ID").val(colIDs);
		$("#" + name).val(cols);
	} else {
		// 关联栏目中，自动滤掉主栏目
		var colIDs = original_censorship.filterCol($("#" + name + "ID").val(), checks[0]);
		var cols = original_censorship.filterCol($("#" + name).val(), checks[1]);
		
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