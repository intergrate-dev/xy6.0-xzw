var imp = {
	sheets : [], //工作簿的名称
	sheetHeaders : [], //每个工作簿的表头列名称
	relatedFields : [], //做对应的文档类型字段code
	relatedHeaders : [], //做对应的表头列的index
	
	init : function() {
		
		//判断是从潜在会员/会员 界面进入后台程序
		var RuleFormula = imp.getUrlParam('RuleFormula');
		if(RuleFormula!=null){
			var Potential = RuleFormula.substr(14, 1);
			$("#Potential").val(Potential);
		}
		
		//若是群会员导入，则修改响应action
		var group = $("#isGroup").val();
		if (group == "1") {
			$("#postForm").attr("action", "ImportGroup.do");
		}
		//若是会员导入，则修改响应action
		var member = $("#isMember").val();
		if (member == "1"){
			$("#postForm").attr("action", "ImportMember.do");//需要自己写controller
		}
		var isAction = $("#isAction").val();
		if (isAction == "1"){
			$("#postForm").attr("action", "Import.do");//需要自己写controller
		}
		//若是活动会员导入，则对DocIDs和DocLibID赋值
		var isAction = $("#isAction").val();
		var isApply = $("#isApply").val();
		var isSignin = $("#isSignin").val();
		if (isAction == "1" || isApply == "1" || isSignin == "1") {
			var ruleFormula = $("#ruleFormula").val();
			ruleFormula = ruleFormula.substr(ruleFormula.indexOf("maActionID_EQ_"), ruleFormula.length);
			if(ruleFormula != null && ruleFormula.indexOf("maActionID_EQ_") >= 0){
				var ruleArray = ruleFormula.split("_EQ_");
				if(ruleArray != null && ruleArray.length == 2)
					$("#DocIDs").val(ruleArray[1]);
			}
		}
		//读出会员字段
		imp.getFields();
		
		$("#btnSubmit").attr("disabled", true);
		$("#btnAutoMatch").attr("disabled", true);
		$("#btnMatch").attr("disabled", true);
		
		//设置事件响应
		$("#uploadForm").submit(imp.doUpload);
		$("#sheets").bind("change", imp.changeSheet);
		$("#btnMatch").click(imp.doMatch);
		$("#btnAutoMatch").click(imp.autoMatch);
		$("#btnSubmit").click(imp.doSubmit);
		$("#btnCancel").click(imp.doCancel);
		
	},
	getUrlParam : function (name){
	    var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
	    var r = window.location.search.substr(1).match(reg);  //匹配目标参数
	    if (r!=null) return unescape(r[2]); return null; //返回参数值
	},
	//上传文件后，显示工作簿
	loaded : function(file, sheets, sheetHeaders){
		imp.hideWaiting();
		
		$("#upFile").val(file);
		imp.sheets = sheets;
		imp.sheetHeaders = sheetHeaders;
		
		imp.setOptions("sheets", imp.sheets, 0);
		$("#btnSubmit").attr("disabled", false);
		$("#btnAutoMatch").attr("disabled", false);
		$("#btnMatch").attr("disabled", false);
	},
	//读出会员字段
	getFields : function() {
		var theURL = $("#postForm").attr("action");
		theURL += "?a=fields";
		$.ajax({url: theURL, async:false, dataType:'json', success: function(datas) {
			imp.setOptions("fields", datas, 1);
		}});
	},
	//flag:0--value=index; 1--value=data.key,text=data.value
	setOptions : function(id, datas, flag) {
		var sel = document.getElementById(id);
		if (!sel) return;
		
		while (sel.options.length > 0)
			sel.options.remove(0);

		for (var i = 0; i < datas.length; i++) {
			if (flag == 0) {
				imp.addOption(sel, i, datas[i]);
			} else if (flag == 1) {
				imp.addOption(sel, datas[i].key, datas[i].value);
			}
		}
		$(sel).trigger("change");
	},
	//对select加一个option
	addOption : function(sel, value, text) {
		var op = document.createElement("OPTION");
		op.value = value;
		op.text = text;
		sel.options.add(op);
	},
	//对select减一个option
	removeOption : function(id, value) {
		var sel = document.getElementById(id);
		if (!sel) return;
		
		for (var i = 0; i < sel.options.length; i++) {
			if (sel.options[i].value == value) {
				sel.options.remove(i);
				break;
			}
		}
	},
	//上传之前：检查是否已选择
	doUpload : function() {
		var file = $("#excelFile").val();
		if (!file) return false;
		
		var pos = file.lastIndexOf(".xls");
		if (pos != file.length - 4 && pos != file.length - 5) {
			alert("请选择excel文件");
			return false;
		}
		imp.getFields();//重新加载会员字段
		imp.showWaiting();
	},
	//切换工作簿：切换表头名
	changeSheet : function() {
		var idx = $("#sheets").val();
		imp.setOptions("headers", imp.sheetHeaders[idx], 0);
		
		imp.relatedFields = [];
		imp.relatedHeaders = [];
		$("#matchDiv").html("");
		
		var sheet = $("#sheets").find("option:selected").text();
		$("#upSheet").val(sheet);
	},
	//自动对应：自动匹配同名的表头和字段
	autoMatch : function() {
		var headers = document.getElementById("headers").options;
		var fields = document.getElementById("fields").options;
		for (var i = 0; i < headers.length;) {
			var headerIndex = headers[i].value;
			var header = headers[i].text;
			var found = false;
			
			for (var j = 0; j < fields.length; j++) {
				var field = fields[j].text;
				var fieldCode = fields[j].value;
                if (field == header){
					imp.checkPush(fieldCode, field, headerIndex, header);
					found = true;
					break;
                }
			}
			if (!found) i++; //若有匹配，则会删除该option
		}
	},
	//检查是否已对应，若没有则添加对应
	checkPush : function(fieldCode, field, headerIndex, header) {
		var ret = false;
		for (var i = 0; i < imp.relatedFields.length; i++) {
			if (imp.relatedFields[i] == fieldCode && imp.relatedHeaders[i] == headerIndex)
				ret = true;
		}
		if (!ret) {
			imp.relatedFields.push(fieldCode);
			imp.relatedHeaders.push(headerIndex);
			
			imp.removeOption("fields", fieldCode);
			imp.removeOption("headers", headerIndex);
			
			imp.appendMatch(fieldCode, field, headerIndex, header);
		}
		return ret;
	},
	//对应的字段显示在区域里
	appendMatch : function(fieldCode, field, headerIndex, header){
		var oDiv = document.createElement("div");
		
		var oSpan1 = document.createElement("span");
		oSpan1.innerHTML = field +" <---> "+ header;
		oSpan1.name = field +":"+ header;
		oSpan1.className="fieldSpan";
		oDiv.appendChild(oSpan1);

		var oSpan2 = document.createElement("span");
		oSpan2.innerHTML = " ---------------------  [delete]";
		oSpan2.style.cssText = "color:black";
		oSpan2.onmouseover = function() {
			this.style.cssText = "cursor:pointer;color:red";
		};
		oSpan2.onmouseout = function(){
			this.style.cssText = "color:black";
		};
		//去掉对应
		oSpan2.onclick = function() {
			this.parentNode.parentNode.removeChild(this.parentNode);
			for (var i = 0; i < imp.relatedFields.length; i++) {
				if (imp.relatedFields[i] == fieldCode 
					&& imp.relatedHeaders[i] == headerIndex){
					//从[]中去掉
					imp.relatedFields.splice(i,1);
					imp.relatedHeaders.splice(i,1);
					
					//去掉对应后把字段加回select
					var sel = document.getElementById("fields");
					imp.addOption(sel, fieldCode, field);
					
					var sel = document.getElementById("headers");
					imp.addOption(sel, headerIndex, header);
				}
			}
		};
		oDiv.appendChild(oSpan2);
		
		document.getElementById("matchDiv").appendChild(oDiv);
	},
	//点击对应按钮
	doMatch : function() {
		var fieldCode = $("#fields").val();
		var field = $("#fields").find("option:selected").text();
		var headerIndex = $("#headers").val();
		var header = $("#headers").find("option:selected").text();
		
		if (!headerIndex || !fieldCode) return;
		
		imp.checkPush(fieldCode, field, headerIndex, header);
	},
	//点击保存按钮
	doSubmit : function() {
		if (imp.relatedFields.length == 0) {
			alert("请先设置好表头对应。");
			return;
		}
		
		var values = imp._join(imp.relatedFields);
		$("#upFields").val(values);
		
		values = imp._join(imp.relatedHeaders);
		$("#upHeaders").val(values);
		
		$("#btnSubmit").attr("disabled", true);
		$("#postForm").submit();
		imp.showWaiting();
	},
	_join : function(arr) {
		var result = "";
		for (var i = 0; i < arr.length; i++) {
			if (result) result += ",";
			result += arr[i];
		}
		return result;
	},
	doCancel : function() {
		window.close();
		var tool = parent.e5.mods["workspace.toolkit"];
		tool.self.closeOpDialog(null, 2);
	},
	showWaiting : function() {
		$("#import_hint").show();
	},
	hideWaiting : function() {
		$("#import_hint").hide();
	}
}
window.onload = imp.init;