var form_embedded = false;	//是否嵌入式表单。若是嵌入式，则不独自提交
var form_embedded_OK = false;

var e5_form = {
	/*
	 * 默认为表单路径在/e5workspace/manoeure/，因此访问其它路径时都加../../。
	 * 若深度不对，则修改pathPrefix，如"../"
	 */
	pathPrefix : "../../",
	
	//处理Url的路径深度
	dealUrl : function(url) {
		return e5_form.pathPrefix + url;
	},
	//表单初始化
	init : function() {
		e5_form.defineEvent();
		
		//动态取值的情况初始化
		e5_form.dynamicReader.init();
		
		//auto-complete框设置响应
		e5_form.autoCompleter.init();
		
		//从后台读出每个字段的值，并填写到界面上
		e5_form.dataReader.init();
		
		//附件的初始化显示
		e5_form.file.init();
	},
	defineEvent : function() {
		//设置验证
		$("#form").validationEngine({
			autoPositionUpdate:true,
			promptPosition:"bottomLeft",
			onValidationComplete:function(from,r){
				if (r){
					form_embedded_OK = true;
					if (form_embedded) return false;//若是嵌入式表单，则验证后不提交
					window.onbeforeunload = null;
					if($("#btnFormSave").hasClass("noDisabled")) {
						$("#btnFormSave").attr("disabled", false);
					}else {
						$("#btnFormSave").attr("disabled", true);
					}
					
					$("#btnFormCancel").attr("disabled", true);
					$("#btnFormSaveSubmit").attr("disabled", true);
					if(from)
						from[0].submit();
					else
						$("#form").submit();
				} else {
					//在提交按钮时取消了beforeunload事件，验证不成功时应还原回去
					window.onbeforeunload = e5_form.event.beforeExit;
				}
			}
		});
		
		//after.do
		window.onbeforeunload = e5_form.event.beforeExit;
		//按钮事件
		$("#btnFormSave").click(e5_form.event.doSave);
		$("#btnFormSaveSubmit").click(e5_form.event.doSaveSubmit);
		$("#btnFormCancel").click(e5_form.event.doCancel);
	},
	checkDuplicate : function(field, rules, i, options) {
		field.val(field.val().trim());
		if (!field.val()) return;
		
		var theURL = e5_form._duplicateUrl(field);
		theURL = e5_form.dealUrl(theURL);
		
		var result = null;
		$.ajax({url: theURL, async:false, success: function(data) {
			result = data;
		}});
		if (result == 1)
			return "已存在";
	},
	_duplicateUrl : function(field) {
		var theURL = "e5workspace/Data.do?action=duplicate"
			+ "&DocLibID=" + $("#DocLibID").val()
			+ "&DocIDs=" + $("#DocID").val()
			+ "&field=" + field.attr("id")
			+ "&value=" + e5_form.encode(field.val());
		return theURL;
	},
	checkValidDir : function(field, rules, i, options) {
		if (!field.val()) return;
		
		if(/.*[\u4e00-\u9fa5]+.*$/.test(field.val())){ 
			return "不能包含汉字";
		} 

		var filterString = "\\/:*?\"<>|";    
		for (var i = 0; i <= (filterString.length - 1); i++) {  
		     var ch = filterString.charAt(i);  
		     var temp = field.val().indexOf(ch);  
		     if (temp != -1) {  
		    	 return "不能包含下列任何字符：\\ / : * ? \" < > |";   
		     }  
		}
	},
	checkValidRoot : function(field, rules, i, options) {
		if (!field.val()) return;
		
		if(/.*[\u4e00-\u9fa5]+.*$/.test(field.val())){ 
			return "不能包含汉字";
		} 

		var filterString = "*?\"<>|";    
		for (var i = 0; i <= (filterString.length - 1); i++) {  
		     var ch = filterString.charAt(i);  
		     var temp = field.val().indexOf(ch);  
		     if (temp != -1) {  
		    	 return "不能包含下列任何字符： * ? \" < > |";   
		     }  
		}
	},
	/** 对特殊字符和中文编码 */
	encode : function(param1){
		if (!param1) return "";

		var res = "";
		for (var i = 0;i < param1.length;i ++){
			switch (param1.charCodeAt(i)){
				case 0x20://space
				case 0x3f://?
				case 0x23://#
				case 0x26://&
				case 0x22://"
				case 0x27://'
				case 0x2a://*
				case 0x3d://=
				case 0x5c:// \
				case 0x2f:// /
				case 0x2e:// .
				case 0x25:// .
					res += escape(param1.charAt(i)); break;
				case 0x2b:
					res += "%2b"; break;
				default:
					res += encodeURI(param1.charAt(i)); break;
			}
		}
		return res;
	},
	//从url中读参数。url类似于<OperationURL>?DocLibID=..&DocIDs=...&...
	getParam : function(name) {
		var params = window.location.href;
		params = params.substring(params.indexOf("?") + 1, params.length);
		params = params.split("&");
		
		for (var i = 0; i < params.length; i++) {
			var arr = params[i].split("=");
			if (arr[0] == name) {
				return params[i].substring(name.length + 1, params[i].length);
			}
		}
		return null;
	}
}

e5_form.dataReader = {
	//读值
	init : function() {
		var docID = $("#DocID").val();
		if (docID == "0") {
			//新建表单，设置字段权限
			e5_form.dataReader.fieldAuth();
		} else {
			//修改表单：设置值和字段权限
			var theURL = e5_form.dataReader._getDataUrl();
			theURL = e5_form.dealUrl(theURL);
			$.ajax({url: theURL, async:false, dataType:'json', success: function(datas) {
				if (!datas) return;
				try {
					var fv = datas.value;
					for (var f in fv) {
						var input = $("#" + f);
						if (input.attr("oldValue"))
							input.attr("oldValue", fv[f]);
						else {
							input.val(fv[f]);
						}
					}
					
					var fields = datas.fields;
					if (!fields) return;
					for (var i = 0; i < fields.length; i++) {
						var input = $("#SPAN_" + fields[i]);
						//input.css("display", "none");
						input.remove();
					}
				}catch (e){alert(e.message);}
			}});
		}
		//select选中项、checkbox的初始化
		e5_form.dataReader.selectInit();
		e5_form.dataReader.checkInit();
	},
	_getDataUrl : function() {
		var theURL = "e5workspace/manoeuvre/FormDocFetcher.do?FormID=" + $("#FormID").val()
			+ "&DocLibID=" + $("#DocLibID").val()
			+ "&DocID=" + $("#DocID").val();
		return theURL;
	},
	//检查是否有字段分组权限，若有则进行显示限制
	fieldAuth : function() {
		var theURL = "e5workspace/manoeuvre/FormDocFetcher.do?fieldGroup=1&FormID=" + $("#FormID").val();
		theURL = e5_form.dealUrl(theURL);
		$.ajax({url: theURL, async:false, dataType:'json', success: function(datas) {
			if (!datas) return;
			
			for (var i = 0; i < datas.length; i++) {
				var input = $("#SPAN_" + datas[i]);
				//input.css("display", "none");
				input.remove();
			}
		}});
	},
	//把所有的select按值做初始化选中
	selectInit : function() {
		var sels = $("#form select");
		for (var idx = 0; idx < sels.length; idx++) {
			var sel = sels[idx];
			var oldValue = sel.getAttribute("oldValue");
			if (sel.multiple){//处理多选下拉框的选中
				var datas = new Array();
				if (oldValue != null && oldValue.length > 0){
					if(oldValue.indexOf(",")){
						datas = oldValue.split(",");
					}else{
						datas.push(oldValue);
					}
				}
				var options = sel.options;
				if (options != null && options.length > 0){
					for(var i=0;i<options.length;i++){
						for(var j=0;j<datas.length;j++){
							if(options[i].value==datas[j]){
								options[i].selected="selected";
							}
						}
					}
				}
			} else if (oldValue != "-"){
				//sel.value = oldValue;若oldValue不在选项里，会造成提交时sel.val()为null
				$(sel).val(oldValue);
			}
		}
		
		e5_form.dataReader.radioInit();
	},
	//把所有的radio按值做初始化选中
	radioInit : function() {
		var sels = $("#form input[radio='true']");
		for (var i = 0; i < sels.length; i++) {
			var sel = sels[i];
			var oldValue = sel.getAttribute("oldValue");
			if (oldValue != "-") {
				var idx = 0;
				while (true) {
					var ck = document.getElementById(sel.id + "_" + idx++);
					if (!ck) break;
					
					if (ck.value == oldValue)
						ck.checked="true"; 
				}
			} else {
				//默认选中第一个选项
				var ck = document.getElementById(sel.id + "_0");
				if (ck) ck.checked="true"; 
			}
		}
	},
	//把所有的checkbox按值做初始化选中
	checkInit : function() {
		var sels = $("#form input[type='checkbox']");
		for (var i = 0; i < sels.length; i++) {
			var sel = sels[i];
			var oldValue = sel.getAttribute("oldValue");
			if (oldValue=="1")
				sel.checked="true";
		}
		e5_form.dataReader.checkMultiInit();
	},
	//多选的checkbox按值做初始化选中
	checkMultiInit : function() {
		var sels = $("#form input[check='true']");
		for (var i = 0; i < sels.length; i++) {
			var sel = sels[i];
			var oldValue = sel.getAttribute("oldValue");
			if (oldValue != "-") {
				var id = sel.id;
				var values = oldValue.split(",");
				var idx = 0;
				while (true) {
					var ck = document.getElementById(id + "_" + idx++);
					if (!ck) break;
					
					for (var j = 0; j < values.length; j++) {
						if (values[j] == ck.value)
							ck.checked="true"; 
					}
				}
			}
		}
	}
}
e5_form.dynamicReader = {
	//----------"动态读取数据"的初始化---------------
	init : function (){
		//定义有ID/NAME对的onchange事件："其它数据"类型、"单层分类"类型
		var sels = $("#form select[pair='true']");
		for (var i = 0; i < sels.length; i++) {
			var sel = sels[i];
			$(sel).change(e5_form.dynamicReader.changeDataName);
		}
		//找<select url=””>元素
		e5_form.dynamicReader._readSelectUrl();
		
		//找<input radio="true" url=””>元素
		e5_form.dynamicReader._readRadioUrl();
		
		//找<input check="true" url=””>元素
		e5_form.dynamicReader._readCheckUrl();
		
		//找<select catType=””>元素
		e5_form.dynamicReader._readCatSelect();
	},
	_readSelectUrl : function() {
		var sels = $("#form select[url]");
		for (var i = 0; i < sels.length; i++) {
			var sel = sels[i];
			var dataUrl = sel.getAttribute("url");
			if (!dataUrl) continue;
			
			while (sel.options.length > 0)
				sel.remove(0);
			if(dataUrl.indexOf("?")>-1){
				dataUrl = dataUrl + "&siteID="+getUrlVars("siteID");
			}else{
				dataUrl = dataUrl + "?siteID="+getUrlVars("siteID");
			}		
			dataUrl = e5_form.dealUrl(dataUrl);
			//返回数据格式：[{key:”…”, value:”…”},{….}, {…}]
			$.ajax({url: dataUrl, async:false, dataType:"json", success: function(datas) {
				if (datas != null){
					for (var i = 0; i < datas.length; i++) {
						var op = document.createElement("OPTION");
						op.value = datas[i].key;
						op.text = datas[i].value;
						sel.options.add(op);
					}
					
					var id = sel.getAttribute("id");
					var name = id.substring(0, id.length - 3);
					var hiddenID = document.getElementById(name);
					if (hiddenID && datas.length > 0) {
						hiddenID.value = datas[0].value;
					}
				}
			}});
		}
	},
	_readRadioUrl : function() {
		var sels = $("#form input[radio='true']");
		for (var i = 0; i < sels.length; i++) {
			var sel = $(sels[i]);
			var dataUrl = sel.attr("url");
			if (!dataUrl) continue;
			
			dataUrl = e5_form.dealUrl(dataUrl);
			
			var parent = sel.parent();
			var fName = sel.attr("name");
			//返回数据格式：[{key:”…”, value:”…”},{….}, {…}]
			$.ajax({url: dataUrl, async:false, dataType:"json", success: function(datas) {
				if(datas!=null){
					for (var i = 0; i < datas.length; i++) {
						var radio = $("<input type='radio'/>")
								.val(datas[i].key)
								.attr("name", fName)
								.attr("id", fName + "_" + i);
						var label = $("<label/>")
								.html(datas[i].value)
								.attr("for", fName + "_" + i);
						parent.append(radio);
						parent.append(label);
					}
				}
			}});
		}
	},
	_readCheckUrl : function() {
		var sels = $("#form input[check='true']");
		for (var i = 0; i < sels.length; i++) {
			var sel = $(sels[i]);
			var dataUrl = sel.attr("url");
			if (!dataUrl) continue;
			
			dataUrl = e5_form.dealUrl(dataUrl);
			
			var parent = sel.parent();
			var fName = sel.attr("name");
			//返回数据格式：[{key:”…”, value:”…”},{….}, {…}]
			$.ajax({url: dataUrl, async:false, dataType:"json", success: function(datas) {
				if(datas!=null){
					for (var i = 0; i < datas.length; i++) {
						var radio = $("<input type='checkbox'/>")
								.val(datas[i].key)
								.attr("name", fName)
								.attr("id", fName + "_" + i);
						var label = $("<label/>")
								.html(datas[i].value)
								.attr("for", fName + "_" + i);
						parent.append(radio);
						parent.append(label);
					}
				}
			}});
		}
	},
	_readCatSelect : function() {
		//找单层分类
		var sels = $("#form select[catType]");
		for (var i = 0; i < sels.length; i++) {
			var sel = sels[i];
			var catType = sel.getAttribute("catType");
			if (!catType) continue;
			
			while (sel.options.length > 0)
				sel.remove(0);
				
			var isNull = sel.getAttribute("isNull");
			var op = null;
			if (isNull == "1") {
				op = document.createElement("OPTION");
				op.value = "";
				op.text = "";
				sel.options.add(op);
			}
			var dataUrl = e5_form.dynamicReader._readCatUrl(catType);
			$.ajax({url: dataUrl, async:false, dataType:"json", success: function(datas) {
				if(datas != null && datas.length > 0){
					for (var i = 0; i < datas.length; i++) {
						op = document.createElement("OPTION");
						op.value = datas[i].catID;
						op.text = datas[i].catName;
						sel.options.add(op);
					}
				}
				if (sel.options.length > 0) {
					var id = sel.getAttribute("id");
					var name = id.substring(0, id.length - 3);
					var hiddenID = document.getElementById(name);
					if (hiddenID) {
						hiddenID.value = sel.options[0].text;
					}
				}
			}});
		}
	},
	_readCatUrl : function(catType) {
		var dataUrl = "e5workspace/manoeuvre/CatFinder.do?action=single&catType=" + catType;
		dataUrl = e5_form.dealUrl(dataUrl);
		return dataUrl;
	},
	//切换时，给NAME域赋值
	changeDataName : function(event) {
		var src = event.target;
		//"AAA_ID"去掉"_ID"，得到NAME字段的域
		var id = src.id;
		var nameField = id.substring(0, id.length - 3);
		var text = (src.selectedIndex >= 0) ? src.options[src.selectedIndex].text : "";
		$("#" + nameField).val(text);
	}
}
e5_form.autoCompleter = {
	init : function() {
		e5_form.autoCompleter.autoCompleteInit();
		//设置分类可以搜索输入
		e5_form.autoCompleter.rtFinderInit();
	},
	//auto-complete框设置响应
	autoCompleteInit : function() {
		//找到所有的auto-complete输入框
		var sels = $("#form input[auto-complete='true']");
		for (var i = 0; i < sels.length; i++) {
			var sel = $(sels[i]);
			var dataUrl = sel.attr("url");
			//var dataUrl = "e5workspace/manoeuvre/FormDocFetcher.do?test=1";
			if (!dataUrl) return;
			
			dataUrl = e5_form.dealUrl(dataUrl);
			
			sel.autocomplete(dataUrl, e5_form.autoCompleter.ac_setting.options);
			//若是ID/NAME两个输入框，则设置对ID域的赋值
			if (e5_form.autoCompleter.ac_setting.hasIdField(sel[0])) {
				sel.result(e5_form.autoCompleter.ac_setting.select);
				sel.blur(e5_form.autoCompleter.ac_setting.blur);
			}
		}
	},
	//初始化，每个分类框都添加搜索事件
	rtFinderInit : function(){
		return; //暂时不使用此功能
		
		var dataUrl = "e5workspace/manoeuvre/CatFinder.do?noPermission=1&catType=";
		dataUrl = e5_form.dealUrl(dataUrl);
		
		//找到所有的auto-complete输入框
		var sels = $("#form input[catType]");//不会找到单层分类，那是select[catType]
		for (var i = 0; i < sels.length; i++) {
			var sel = $(sels[i]);
			sel.autocomplete(dataUrl + sel.attr("catType"), e5_form.autoCompleter.rtFinder.options);
			sel.result(e5_form.autoCompleter.rtFinder.select);
			sel.blur(e5_form.autoCompleter.rtFinder.blur);
		}
	},
	//---------初始化时，auto-complete框设置响应-------------
	ac_setting : {
		select : function(event, row, formatted){ 
			var src = event.target;
			if (e5_form.autoCompleter.ac_setting.hasIdField(src)) {
				var idField = e5_form.autoCompleter.ac_setting.getIdField(src);
				idField.val(row.key);
				idField.attr("nameValue", row.value);
			}
		},
		//若不是从选项选的而是手输的，则blur时把之前的ID清空
		blur : function(event){
			var src = event.target;
			if (e5_form.autoCompleter.ac_setting.hasIdField(src)) {
				var idField = e5_form.autoCompleter.ac_setting.getIdField(src);
				
				var nameValue = idField.attr("nameValue");
				if (nameValue == null) {
					//若还没有namevalue属性，则可能是新增或者修改表单时。
					//此时若ID已经有值，则应该是修改表单，自动初始化namevalue
					if (idField.val()) {
						idField.attr("nameValue", src.value);
						return;
					}
					
					nameValue = "";
				}
				
				if (src.value != nameValue) {
					idField.val("");
					idField.attr("nameValue", "");
				}
			}
		},
		hasIdField : function(src) {
			return (src.getAttribute("pair") == "true");
		},
		getIdField : function(src) {
			return $("#" + src.id + "_ID");
		},
		//auto-complete控件需要的参数
		options : {
			minChars : 1,
			delay : 400,
			autoFill : true,
			selectFirst : true,
			cacheLength : 1,
			matchSubset : false,
			extraParams: {siteID:getUrlVars("siteID")},
			//dataType:'json',
			//需要把data转换成json数据格式
			parse: function(data) {
				if(!data||~data.indexOf("No Records")){
					return [];
				}
				return $.map(eval(data), function(row) {
					return {
						data: row,
						value: row.value,
						result: row.value
					}
				});
			},
			//显示在下拉框中的值
			formatItem: function(row, i,max) { return row.value; },
			formatMatch: function(row, i,max) { return row.value; },
			formatResult: function(row, i,max) { return row.value; }
		}
	},
	//---------按输入词搜索分类并显示选择框---------
	rtFinder : {
		select : function(event, row, formatted){ 
			var src = event.target;
			var idField = $("#" + src.id + "ID");
			
			idField.val(row.cascadeID);
			idField.attr("casName", row.cascadeName);
		},
		//若不是从选项选的而是手输的，则blur时把Name框也清空
		blur : function(event){
			var src = event.target;
			var sel = $("#" + src.id);
			
			var idField = $("#" + src.id + "ID");
			if (sel.val() != idField.attr("casName")) {
				sel.val("");
				idField.val("");
				idField.attr("casName", "");
			}
		},
		options : {
			minChars : 1,
			delay : 400,
			autoFill : true,
			selectFirst : true,
			matchContains: true,
			cacheLength : 1,
			parse: function(data) {
				return $.map(eval(data), function(row) {
					return {
						data: row,
						value: row.cascadeName,
						result: row.cascadeName
					}
				})
			},
			formatItem: function(row, i,max) { return row.cascadeName; },
			formatMatch: function(row, i,max) { return row.cascadeName + row.catCode; },
			formatResult: function(row, i,max) { return row.cascadeName; }
		}
	}
}
e5_form.event = {
	//---各种事件、回调事件---
	curDialog : null,
	curIDField : null,
	curNameField : null,
	//部门选择树
	selectDept : function(nameField, idField, multiple) {
		var theURL = e5_form.event._getUserUrl(idField, multiple, "e5workspace/manoeuvre/OrgSelect.do");
		e5_form.event._select(nameField, idField, theURL);
	},
	//用户选择树
	selectUser : function(nameField, idField, multiple) {
		var theURL = e5_form.event._getUserUrl(idField, multiple, "e5workspace/manoeuvre/UserSelect.do");
		e5_form.event._select(nameField, idField, theURL);
	},
	//角色选择树
	selectRole : function(nameField, idField, multiple) {
		var theURL = e5_form.event._getUserUrl(idField, multiple, "e5workspace/manoeuvre/RoleSelect.do");
		e5_form.event._select(nameField, idField, theURL);
	},
	//分类选择树
	selectCat : function(nameField, idField, catType, multiple) {
		//把"1_23_323"转成"1~23~323"
		var catIDs = e5_form.event._transformCatIDs(document.getElementById(idField).value);
		var theURL = "e5workspace/manoeuvre/CatSelect.do?noPermission=1&catType=" + catType + "&catIDs=" + catIDs;
		if (!multiple) theURL += "&multiple=false";

		e5_form.event._select(nameField, idField, theURL);
	},

	_select : function(nameField, idField, dataUrl){
		e5_form.event.curIDField = idField;
		e5_form.event.curNameField = nameField;
		
		//顶点位置
		var pos = e5_form.event._getDialogPos(document.getElementById(nameField));
		
		dataUrl = e5_form.dealUrl(dataUrl);
		
		e5_form.event.curDialog = e5.dialog({type:"iframe", value:dataUrl}, 
				{showTitle:false, width:pos.width, height:pos.height, pos:pos,resizable:false});
		e5_form.event.curDialog.show();
	},
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

	_getUserUrl : function(idField, multiple, urlRoot) {
		var oldValue = $("#" + idField).val();
		var theURL = urlRoot + "?userIDs=" + oldValue;
		if (multiple) 
			theURL += "&multiple=true";
		else
			theURL += "&multiple=false";
		return theURL;
	},
	userWindowSelect : function(selectID, selectName){
		$("#" + e5_form.event.curIDField).val(selectID);
		$("#" + e5_form.event.curNameField).val(selectName);

		e5_form.event.userWindowHidden();
	},
	userWindowHidden : function() {
		if (e5_form.event.curDialog)
			e5_form.event.curDialog.closeEvt();
		
		e5_form.event.curDialog = null;
	},
	_transformCatIDs : function(catIDs, direct) {
		if (!catIDs) return catIDs;

		if (direct) { //从分类选择返回
			catIDs = catIDs.replace(/~/g, "_");
			catIDs = catIDs.replace(/,/g, ";");
		}
		else {
			var catIDArr = catIDs.split(";");
			catIDs = "";
			for (var i = 0; i < catIDArr.length; i++) {
				var idArr = catIDArr[i].split("_");
				if ((idArr.length > 0) && idArr[idArr.length - 1])
					catIDs += idArr[idArr.length - 1] + ",";
			}
			if (catIDs) catIDs = catIDs.substring(0, catIDs.length - 1);
		}
		return catIDs;
	},
	//分类选择窗口点“确定”
	catWindowSelect : function(catIDs, catNames, cascadeIDs, cascadeNames) {
		var ids = e5_form.event._transformCatIDs(cascadeIDs, 1);
		var names = cascadeNames;//e5_form.event._transformCatIDs(cascadeNames, 1);

		$("#" + e5_form.event.curIDField).val(ids);
		$("#" + e5_form.event.curNameField).val(names);

		e5_form.event.userWindowHidden();
	},
	//日期选择
	selectDate : function(field) {
		showCalendar(field, 'y-mm-dd');
	},

	//-----操作窗口退出时的after.do控制----------
	doSave : function() {
		if (!$("#form").validationEngine("validate")) {
			// 验证提示
			$("#form").validationEngine("updatePromptsPosition");
			return false;
		}
		if (!e5_form.event.otherValidate()) {
			return false;
		}
		window.onbeforeunload = null;
		
		//若有附件，则先提交
		if (!e5_form.file.upload()) {
			return false;
		}
	},
	doSaveSubmit : function() {
		if (!$("#form").validationEngine("validate")) {
			// 验证提示
			$("#form").validationEngine("updatePromptsPosition");
			return false;
		}
		if (!e5_form.event.otherValidate()) {
			return false;
		}
		
		window.onbeforeunload = null;
		document.getElementById("SaveSubmit").value = "true";
		
		//若有附件，则先提交
		if (!e5_form.file.upload()) {
			document.getElementById("SaveSubmit").value = "false";
			return false;
		}
	},
	//其它验证，用于上层应用扩展
	otherValidate : function() {
		return true;
	},
	//取消按钮。调after.do解锁
	doCancel : function() {
		window.onbeforeunload = null;
		
		$("#btnFormSave").disabled = true;
		$("#btnFormCancel").disabled = true;
		$("#btnFormSaveSubmit").disabled = true;
		e5_form.event.beforeExit();
	},

	//关闭窗口。调after.do解锁
	beforeExit : function() {
		var dataUrl = "e5workspace/after.do?UUID=" + $("#form #UUID").val();
		dataUrl = e5_form.dealUrl(dataUrl);
		
		document.getElementById("iframe").contentWindow.location.href = dataUrl;
	}
}
e5_form.embed = {
	/**
	 * -----------------------------------------------------------
	 * 这里是嵌入式表单的处理.
	 * 外部界面以如下形式嵌入一个定制的表单：
	 * <iframe id="subform" src="../e5workspace/manoeuvre/Form.do?code=Form1&<%=request.getQueryString()%>"></iframe>
	 * 然后在外部提交的时候，以如下方式调用嵌入式表单的验证和数据提取：
			var form = $("#form");  //外部页面中的待提交表单
			var subform = window.frames["subform"];
			var appendOK = subform.appendFields(form);
			if (appendOK)
				form.submit();
	 * 则嵌入式表单中的Field都合并到外部待提交表单中。
	 * 在提交后的保存处理时，用如下形式保存嵌入式表单中的字段：
			FormSaver saver = (FormSaver)Context.getBean(FormSaver.class);
			docID = saver.handle(doc, request);
	 * 则完成保存，并返回docID（在新建类型的操作时比较有用）
	 */
	appendFields : function(otherForm) {
		form_embedded = true;//标识为嵌入式表单
		form_embedded_OK = false;//验证之前，重置标记
		window.onbeforeunload = null;//嵌入式表单不做窗口关闭的处理

		$("#form").submit();	//验证一下表单有效性（只验证，不提交）
		
		//若验证通过，则可以提交给上级表单
		if (form_embedded_OK) {
			var div = $("<div/>").css("display", "none");
			
			e5_form.embed.appendOneType(div, "#form select", "<select/>");
			e5_form.embed.appendOneType(div, "#form textarea", "<textarea/>");
			e5_form.embed.appendOneType(div, "#form input[type='text']", "<input type='text'/>");
			e5_form.embed.appendOneType(div, "#form input[type='hidden']", "<input type='hidden'/>");
			e5_form.embed.appendOneType(div, "#form input[type='checkbox']", "<input type='checkbox'/>");
			e5_form.embed.appendOneType(div, "#form input[type='radio']", "<input type='radio'/>");
			
			otherForm.append(div);
			
			return true;
		} else {
			return false;
		}
	},
	appendOneType : function(div, oneType, oneTypeName) {
		var sels = $(oneType);
		for (var i = 0; i < sels.length; i++) {
			var sel = $(sels[i]);
			var itemName = sel.attr("name");
			if (itemName == "UUID") continue;
			
			var newOne = $(oneTypeName);
			newOne.attr("id", sel.attr("id"))
				.attr("name", itemName)
				.val(sel.val());
			div.append(newOne);
		}
	}
}

e5_form.file = {
	counter : 0, //counter for form.
	counter_uploaded : 0, // counter for files submitted.
	counter_files : 0, //counter for files all.
	//附件初始化：有附件的话，显示文件图标，并可下载
	init : function() {
		var files = $("#form input[type='file']");
		if (files.length == 0) return;
		
		files.each(e5_form.file._initOneFile);
	},
	//判断非图片类型？
	notImgFile : function(id) {
		var icon = $("#" + id).val();
		if (icon && icon != "-") {
			var ext = icon.substring(icon.lastIndexOf(".") + 1, icon.length).toLowerCase();
			return (ext && ext != "jpg" && ext != "gif" && ext != "png" && ext != "jpeg");
		}
		return false;
	},
	//打开表单时，把已有附件用图标显示出来，并准备好下载方法
	_initOneFile : function(index) {
		var file = $(this);
		var filePath = file.attr("oldValue");
		if (filePath && filePath != "-") {
			var img = $("#img_" + file.attr("id"));
			//filePath:附件存储;201504/18/glj_<myfilename>
			var fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
			fileName = fileName.substring(fileName.indexOf("_") + 1);
			img.attr("path", filePath)
				.attr("title", fileName)
				.click(e5_form.file.download)
				.show();
		}
	},
	//下载附件
	download : function(evt) {
		var path = $(evt.target).attr("path");
		
		var dataUrl = "e5workspace/Data.do?action=download&path=" + e5_form.encode(path);
		dataUrl = e5_form.dealUrl(dataUrl);
		if(path != undefined && path !="" && path != null)
		window.open(dataUrl);
	},
	
	//表单提交前先上传附件。一个表单里可能有多个附件
	upload : function() {
		var files = $("#form input[type='file']");
		if (files.length == 0) return true;
		
		e5_form.file.counter_files = files.length;
		e5_form.file.counter_uploaded = 0;
		
		files.each(e5_form.file._uploadOneFile);
		
		return false;
	},
	//表单提交前先上传附件。一个表单里可能有多个附件
	uploadNew : function() {
		var files = $("#form input[type='file']");
		if (files.length == 0) return true;
		
		e5_form.file.counter_files = files.length;
		e5_form.file.counter_uploaded = 0;
		
		files.each(e5_form.file._uploadOneFileNew);
		
		return false;
	},
	_uploadUrlNew : function() {
		return "xy/template/uploadFile.do";
	},
	//上传一个附件
	_uploadOneFileNew : function() {
		var dataUrl = e5_form.file._uploadUrlNew();
		dataUrl = e5_form.dealUrl(dataUrl);
		
		var fileInput = $(this);
		var inputName = fileInput.attr("name");
		//若没选择文件，则当做成功
		if (!fileInput.val()) {
			var oldValue = fileInput.attr("oldValue");
			if (oldValue && oldValue != "-") {
				//若是修改操作，原来有附件，则需要用hidden域仍传回去
				//fileInput.removeAttr("form");
				fileInput.remove();
				
				var newInput = $("<input type='hidden'/>")
					.attr("name", inputName)
					.attr("id", inputName)
					.val(oldValue);
				$("#form").append(newInput);
			}
			e5_form.file._oneSuccess();
		} else {
			e5_form.file.send(fileInput, dataUrl, function(success, result) {
				//若有上传失败的，则整体认为失败了
				if (e5_form.file.counter_uploaded < 0)
					return;
				if (success) {
					var newInput = $("<input type='hidden'/>")
						.attr("name", inputName)
						.attr("id", inputName)
						.val(result);
					$("#form").append(newInput);
					
					e5_form.file._oneSuccess();
				} else {
					alert("上传失败！" + result 
						+ "\n请确认已配置好存储设备（自身存储或通用的<附件存储>）。");
					
					e5_form.file.counter_uploaded = -1;
				}
			});
		}
	},
	_uploadUrl : function() {
		return "e5workspace/Data.do?action=upload&DocLibID=" + $("#DocLibID").val();
	},
	//上传一个附件
	_uploadOneFile : function() {
		var dataUrl = e5_form.file._uploadUrl();
		dataUrl = e5_form.dealUrl(dataUrl);
		
		var fileInput = $(this);
		var inputName = fileInput.attr("name");
		//若没选择文件，则当做成功
		if (!fileInput.val()) {
			var oldValue = fileInput.attr("oldValue");
			if (oldValue && oldValue != "-") {
				//若是修改操作，原来有附件，则需要用hidden域仍传回去
				//fileInput.removeAttr("form");
				fileInput.remove();
				
				var newInput = $("<input type='hidden'/>")
					.attr("name", inputName)
					.attr("id", inputName)
					.val(oldValue);
				$("#form").append(newInput);
			}
			e5_form.file._oneSuccess();
		} else {
			e5_form.file.send(fileInput, dataUrl, function(success, result) {
				//若有上传失败的，则整体认为失败了
				if (e5_form.file.counter_uploaded < 0)
					return;
					
				if (success) {
					var newInput = $("<input type='hidden'/>")
						.attr("name", inputName)
						.attr("id", inputName)
						.val(result);
					$("#form").append(newInput);
					
					e5_form.file._oneSuccess();
				} else {
					alert("上传失败！" + result 
						+ "\n请确认已配置好存储设备（自身存储或通用的<附件存储>）。");
					
					e5_form.file.counter_uploaded = -1;
				}
			});
		}
	},
	_oneSuccess : function() {
		//若所有的附件都上传了，则提交表单
		if (++e5_form.file.counter_uploaded == e5_form.file.counter_files) {
			$("#form").submit();
		}
	},
	//实际的提交
	send : function (fileInput, url, completeCallback) {
		var form = $('<form style="display:none;"></form>');
		form.attr('accept-charset', "UTF-8");
		// IE versions below IE8 cannot set the name property of
		// elements that have already been added to the DOM,
		// so we set the name along with the iframe HTML markup:
		e5_form.file.counter += 1;
		var iframe = $('<iframe src="" name="iframe-transport-' + e5_form.file.counter + '"></iframe>');
		iframe.bind('load', function () {
			iframe
				.unbind('load')
				.bind('load', function () {
					var response, success;
					// Wrap in a try/catch block to catch exceptions thrown
					// when trying to access cross-domain iframe contents:
					try {
						response = iframe.contents();
						// Google Chrome and Firefox do not throw an
						// exception when calling iframe.contents() on
						// cross-domain requests, so we unify the response:
						if (!response.length || !response[0].firstChild) {
							throw new Error();
						}
						response = response.find("body").text();
						//格式为   1;附件存储设备;/201504/19/glj_myfilename.txt
						success = (response.charAt(0) == "1");
						response = response.substring(2);
					} catch (e) {
						response = undefined;
					}
					// The complete callback returns the
					// iframe content document as response object:
					completeCallback(success, response);
					// Fix for IE endless progress bar activity bug
					// (happens on form submits to iframe targets):
					$('<iframe src=""></iframe>')
						.appendTo(form);
					window.setTimeout(function () {
						// Removing the form in a setTimeout call
						// allows Chrome's developer tools to display
						// the response result
						form.remove();
					}, 0);
				});
			form
				.prop('target', iframe.prop('name'))
				.prop('action', url)
				.prop('method', "POST");
			// Appending the file input fields to the hidden form
			// removes them from their original location:
			form
				.append(fileInput)
				.prop('enctype', 'multipart/form-data')
				// enctype must be set as encoding for IE:
				.prop('encoding', 'multipart/form-data');
			// Remove the HTML5 form attribute from the input(s):
			fileInput.removeAttr('form');
			
			form.submit();
		});
		form.append(iframe).appendTo(document.body);
	},
	
	//上传一张图片并显示缩略图（该上传按钮对象，要显示缩略图的imgid）	
	uploadOneFileAndShow : function (inputId,imgId) {
		var dataUrl = e5_form.file._uploadUrl();
		dataUrl = e5_form.dealUrl(dataUrl);
		var fileInput = $(inputId);
		var inputName = fileInput.attr("name");
		
		e5_form.file.send(fileInput, dataUrl, function(success, result) {
				if (success) {
					if($("#"+imgId).is(":hidden")){
						$("#"+imgId).css({"max-width": "150px","max-height": "150px;"});
						$("#"+imgId).show();
					}
					$("#"+imgId).attr("src",e5_form.dealUrl("xy/image.do?path="+result));
					var newInput = $("<input type='hidden'/>")
						.attr("name", inputName)
						.attr("id", inputName)
						.val(result);
					$("#form").append(newInput);
				} else {
					alert("上传失败！" + result 
						+ "\n请确认已配置好存储设备（自身存储或通用的<附件存储>）。");
				}
			});
	}
}
//---以下是暴露在命名空间外的函数，供外部引用---
//查重。验证框架不支持e5_form.checkDuplicate，只好放在空间外
function checkDuplicate(field, rules, i, options) {
	return e5_form.checkDuplicate(field, rules, i, options);
}
//目录名称验证
function checkValidDir(field, rules, i, options) {
	return e5_form.checkValidDir(field, rules, i, options);
}
//网络路径验证
function checkValidRoot(field, rules, i, options) {
	return e5_form.checkValidRoot(field, rules, i, options);
}
//部门选择树
function selectDept(nameField, idField, multiple) {
	e5_form.event.selectDept(nameField, idField, multiple);
}
//用户选择树
function selectUser(nameField, idField, multiple) {
	e5_form.event.selectUser(nameField, idField, multiple);
}
//角色选择树
function selectRole(nameField, idField, multiple) {
	e5_form.event.selectRole(nameField, idField, multiple);
}
//分类选择树
function selectCat(nameField, idField, catType, multiple) {
	e5_form.event.selectCat(nameField, idField, catType, multiple);
}
function userWindowSelect(selectID, selectName){
	e5_form.event.userWindowSelect(selectID, selectName);
}
function userWindowHidden() {
	e5_form.event.userWindowHidden();
}
//分类选择窗口点“确定”
function catWindowSelect(catIDs, catNames, cascadeIDs, cascadeNames) {
	e5_form.event.catWindowSelect(catIDs, catNames, cascadeIDs, cascadeNames);
}
//分类选择窗口点“取消”
function catWindowHidden() {
	e5_form.event.userWindowHidden();
}
//日期选择
function selectDate(field) {
	e5_form.event.selectDate(field);
}

/**
 * -----------------------------------------------------------
 * 嵌入式表单的处理.
 * 外部界面以如下形式嵌入一个定制的表单：
 * <iframe id="subform" src="../e5workspace/manoeuvre/Form.do?code=Form1&<%=request.getQueryString()%>"></iframe>
 * 然后在外部提交的时候，以如下方式调用嵌入式表单的验证和数据提取：
  		var form = $("#form");  //外部页面中的待提交表单
  		var subform = window.frames["subform"];
  		var appendOK = subform.appendFields(form);
  		if (appendOK)
  			form.submit();
 * 则嵌入式表单中的Field都合并到外部待提交表单中。
 * 在提交后的保存处理时，用如下形式保存嵌入式表单中的字段：
		FormSaver saver = (FormSaver)Context.getBean(FormSaver.class);
		docID = saver.handle(doc, request);
 * 则完成保存，并返回docID（在新建类型的操作时比较有用）
 */
function appendFields(otherForm) {
	return e5_form.embed.appendFields(otherForm);
}
//上传图片并显示缩略图
function uploadOneFileAndShow(inputId,imgId) {
	return e5_form.file.uploadOneFileAndShow(inputId,imgId);
}
$(function() {
	e5_form.init();
});
//获取url地址中的参数
function getUrlVars(name){
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
    var r = window.location.search.substr(1).match(reg);  //匹配目标参数
    if (r != null) return unescape(r[2]); return null; //返回参数值
}