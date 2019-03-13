var e5_queryform = {
	/*
	 * 默认为查询表单路径在/e5workspace/，因此访问其它路径时都加../。
	 * 若深度不对，则修改pathPrefix，如"../../"
	 */
	pathPrefix : "../",
	//处理Url的路径深度
	dealUrl : function(url) {
		return e5_queryform.pathPrefix + url;
	},
	//初始化函数，由查询条件展示页面调用
	init : function() {
		try {
			$("#list_common").attr("title", "");
			//动态取值的情况初始化
			e5_queryform.dynamicReader.init();
			//auto-complete框设置响应
			e5_queryform.autoCompleter.init();
			
			//对所有的label标签添加清空查询条件事件
			e5_queryform.reset.init();
			
			//设置验证
			$("#queryForm").validationEngine({
				autoPositionUpdate:true,
				promptPosition:"bottomLeft",
				onValidationComplete:function(from,r){
				}
			});
		} catch (e) {
			alert(e.message);
		}
	},
	//读查询条件
	getQuery : function() {
		return e5_queryform.paramReader.read();
	}
}

e5_queryform.reset = {
	//--------清空查询条件的相关定义--------
	init : function() {
		e5_queryform.reset.defReset();
		
		//重置按钮的事件，可在这里直接定义
		$("#queryReset").click(e5_queryform.reset.resetAll);
		
		$(".custform-label-radio").click(e5_queryform.reset.radioClick);
	},
	defReset : function() {
		//---查询label上定义click事件，清空输入
		var labels = $(".custform-label");//根据样式查询所有查询条件的label
		jQuery.each(labels, function(i, sel){
			//添加清空事件
			$("#" + sel.id).click(function() {
				e5_queryform.reset.resetField(sel.id);
			});
		});
	},
	defKeydown : function(func) {
		//---查询框定义keydown，回车时调用search。可在外部调用这个函数，改变回车时调用的函数
		if (func) {
			//每个查询条件添加keydown事件
			var labels = $(".custform-label");
			jQuery.each(labels, function(i, sel){
				e5_queryform.reset.keydownField(sel.id, func);
			});
		}
	},
	radioClick : function(evt) {
		var src = $(evt.target);
		src.parent().find(".custform-label-radio").removeClass("select");
		src.addClass("select");
	},
	resetAll : function(){
		//清空全部查询条件
		var labels = $(".custform-label");
		jQuery.each(labels, function(i, sel){
			e5_queryform.reset.resetField(sel.id);
		});
	},
	resetField : function(labelID) {
		//清空一个查询条件
		if (labelID) {
			var id = labelID.substr("LABEL_".length);
			
			e5_queryform.reset._reset(id);
			e5_queryform.reset._reset(id + "_ID");
			e5_queryform.reset._reset(id + "ID");
			e5_queryform.reset._reset(id + "_0");
			e5_queryform.reset._reset(id + "_1");
		}
	},
	keydownField : function(labelID, func) {
		//对一个查询条件添加keydown事件
		if (labelID) {
			var id = labelID.substr("LABEL_".length);
			e5_queryform.reset._keydown(id, func);
			e5_queryform.reset._keydown(id + "_ID", func);
			e5_queryform.reset._keydown(id + "ID", func);
			e5_queryform.reset._keydown(id + "_0", func);
			e5_queryform.reset._keydown(id + "_1", func);
			
			//radio类型的条件点击时响应查询
			e5_queryform.reset._keydownRadio(id, func);
		}
	},
	_keydown : function(id, func) {
		var sel = $("#" + id);
		if (sel.length == 0) return;
		
		if (sel.is("select")) {
			sel.change(func);
		} else {
			sel.keydown(function(evt){
				evt = evt || event;
				if (evt.keyCode == 13) {
					func();
				}
			});
		}
	},
	_keydownRadio : function(id, func) {
		var radios = $(".custform-radio[name='" + id + "']");
		if (radios.length == 0) return;
		
		radios.click(func);
	},
	_reset : function(id) {
		$("#" + id).val("");
	}
}
e5_queryform.dynamicReader = {
	//----------"动态读取数据"的初始化---------------
	init : function (){
		//定义日期输入框的click事件
		e5_queryform.dynamicReader._defineDateClick();

		//找<select url=””>元素
		e5_queryform.dynamicReader._readSelectUrl();
		
		//找<input radio="true" url=””>元素
		e5_queryform.dynamicReader._readRadioUrl();
		
		//找<input check="true" url=””>元素
		e5_queryform.dynamicReader._readCheckUrl();
		
		//找<select catType=””>元素
		e5_queryform.dynamicReader._readCatSelect();
	},
	_defineDateClick : function () {
		var sels = $("#queryForm .custform-input-date");
		sels.each( function(i){
			var id = $(this).attr("id");
			$(this).bind("click", function(){
				$("#" + id).val("");
                var curStartTime = "";
                var curEndTime = "";
                //不同的界面用的时间的input框的id不一样
                if($("#a_pubTime_0").length > 0){//这个是针对web和app的
                    curStartTime = $("#a_pubTime_0").val();
                    curEndTime = $("#a_pubTime_1").val();
                }else if($("#SYS_CREATED_0").length > 0){//针对话题库的
                    curStartTime = $("#SYS_CREATED_0").val();
                    curEndTime = $("#SYS_CREATED_1").val();
				}
                //将开始时间和结束时间传入插件，用来在插件中点确定时判断
				showCalendar(id,'y-mm-dd',curStartTime,curEndTime)
			});
		});
	},
	_readSelectUrl : function() {
		var sels = $("#queryForm select[url]");
		for (var i = 0; i < sels.length; i++) {
			var sel = sels[i];
			while (sel.options.length > 0)
				sel.remove(0);
					
			var dataUrl = sel.getAttribute("url");
			if (!dataUrl) continue;
			
			dataUrl = e5_queryform.dealUrl(dataUrl);
			
			//增加全选
			var showAll = sel.getAttribute("show-all");
			if (showAll == "true") {
				var op = document.createElement("OPTION");
				op.value = "";
				op.text = "";
				sel.options.add(op);			
			}
			
			//返回数据格式：[{key:”…”, value:”…”},{….}, {…}]
			$.ajax({url: dataUrl, async:false, dataType:"json", success: function(datas) {
				for (var i = 0; i < datas.length; i++) {
					var op = document.createElement("OPTION");
					op.value = datas[i].key;
					op.text = datas[i].value;
					sel.options.add(op);
				}
			}});
		}
	},
	_readRadioUrl : function() {
		var sels = $("#queryForm input[radio='true']");
		for (var i = 0; i < sels.length; i++) {
			var sel = $(sels[i]);
			var dataUrl = sel.attr("url");
			if (!dataUrl) continue;
			
			dataUrl = e5_queryform.dealUrl(dataUrl);

			var parent = sel.parent();
			var fName = sel.attr("name");

			//增加全选
			var showAll = sel.attr("show-all");
			if (showAll == "true") {
				var radio = $("<input type='radio'/>")
						.val("")
						.attr("name", fName)
						.attr("id", fName + "_");
				var label = $("<label/>")
						.html("全部")
						.attr("for", fName + "_")
						.addClass("radio inline");
				parent.append(label.append(radio));
			}
			
			//返回数据格式：[{key:”…”, value:”…”},{….}, {…}]
			$.ajax({url: dataUrl, async:false, dataType:"json", success: function(datas) {
				for (var i = 0; i < datas.length; i++) {
					var radio = $("<input type='radio'/>")
							.val(datas[i].key)
							.attr("name", fName)
							.attr("id", fName + "_" + i);
					var label = $("<label/>")
							.html(datas[i].value)
							.attr("for", fName + "_" + i)
							.addClass("radio inline");
					parent.append(label.append(radio));
				}
			}});
		}
	},
	_readCheckUrl : function() {
		var sels = $("#queryForm input[check='true']");
		for (var i = 0; i < sels.length; i++) {
			var sel = $(sels[i]);
			var dataUrl = sel.attr("url");
			if (!dataUrl) continue;
			
			dataUrl = e5_queryform.dealUrl(dataUrl);

			var parent = sel.parent();
			var fName = sel.attr("name");
			//返回数据格式：[{key:”…”, value:”…”},{….}, {…}]
			$.ajax({url: dataUrl, async:false, dataType:"json", success: function(datas) {
				for (var i = 0; i < datas.length; i++) {
					var radio = $("<input type='checkbox'/>")
							.val(datas[i].key)
							.attr("name", fName)
							.attr("id", fName + "_" + i);
					var label = $("<label/>")
							.html(datas[i].value)
							.attr("for", fName + "_" + i)
							.addClass("checkbox inline");
					parent.append(label.append(radio));
				}
			}});
		}
	},
	_readCatSelect : function() {
		//找单层分类
		var sels = $("#queryForm select[catType]");
		for (var i = 0; i < sels.length; i++) {
			var sel = sels[i];
			while (sel.options.length > 0)
				sel.remove(0);
			var catType = sel.getAttribute("catType");
			if (!catType) continue;
			
			//增加全选
			var showAll = sel.getAttribute("show-all");
			if (showAll == "true") {
				var op = document.createElement("OPTION");
				op.value = "";
				op.text = "";
				sel.options.add(op);
			}
			var dataUrl = "e5workspace/manoeuvre/CatFinder.do?action=single&catType=" + catType;
			dataUrl = e5_queryform.dealUrl(dataUrl);

			$.ajax({url: dataUrl, async:false, dataType:"json", success: function(datas) {
				if(datas != null && datas.length > 0){
					for (var i = 0; i < datas.length; i++) {
						var op = document.createElement("OPTION");
						op.value = datas[i].catID;
						op.text = datas[i].catName;
						sel.options.add(op);
					}
				}
			}});
		}
	}
}

//---------初始化时，auto-complete框设置响应-------------
e5_queryform.autoCompleter = {
	init : function() {
		//找到所有的auto-complete输入框
		var sels = $("#queryForm input[auto-complete='true']");
		for (var i = 0; i < sels.length; i++) {
			var sel = $(sels[i]);
			var dataUrl = sel.attr("url");
			//var dataUrl = "e5workspace/manoeuvre/FormDocFetcher.do?test=1";
			if (!dataUrl) return;
			
			dataUrl = e5_queryform.dealUrl(dataUrl);
			
			sel.autocomplete(dataUrl, e5_queryform.autoCompleter.options);
			//若是ID/NAME两个输入框，则设置对ID域的赋值
			if (e5_queryform.autoCompleter.hasIdField(sel[0])) {
				sel.result(e5_queryform.autoCompleter.select);
				sel.blur(e5_queryform.autoCompleter.blur);
			}
		}
	},
	select : function(event, row, formatted){ 
		var src = event.target;
		if (e5_queryform.autoCompleter.hasIdField(src)) {
			var idField = e5_queryform.autoCompleter.getIdField(src);
			idField.val(row.key);
			idField.attr("nameValue", row.value);
		}
	},
	//若不是从选项选的而是手输的，则blur时把之前的ID清空
	blur : function(event){
		var src = event.target;
		if (e5_queryform.autoCompleter.hasIdField(src)) {
			var idField = e5_queryform.autoCompleter.getIdField(src);
			
			var nameValue = idField.attr("nameValue");
			if (nameValue == null) nameValue = "";
			
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
		matchContains: true,
		cacheLength : 1,
		//dataType:'json',
		//需要把data转换成json数据格式
		parse: function(data) {
			if (!data || ~data.indexOf("No Records")){
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
}

//---------------读查询条件-------------------
e5_queryform.paramReader = {
	read : function() {
		var values = [];
		e5_queryform.paramReader._oneTypeParams(values, "#queryForm select");
		e5_queryform.paramReader._oneTypeParams(values, "#queryForm input[type='checkbox']");
		e5_queryform.paramReader._oneTypeParams(values, "#queryForm input[type='radio']");
		e5_queryform.paramReader._oneTypeParams(values, "#queryForm input[type='hidden']");
		e5_queryform.paramReader._textParams(values, "#queryForm input[type='text']");
		
		if (values.length < 1) return "";
		
		// 若有定制的查询条件，且填了值，则做格式验证
		if (!$("#queryForm").validationEngine("validate")){
            $("#queryForm").validationEngine("updatePromptsPosition");
            return false;
        }
		
		var result = "@QUERYCODE@=" + $("#queryForm").attr("code");
		for (var i = 0; i < values.length; i++) {
			result += "&" + values[i].name + "=" + e5.utils.encodeSpecialCode(values[i].value);
		}
		
		return result;
	},
	_oneTypeParams : function(values, oneType) {
		var fields = $(oneType).serializeArray();
		jQuery.each( fields, function(i, sel){
			if (sel.value) {
				values.push(sel);
			}
		});
	},
	//对input text的处理：若hidden的ID域没有值，才加text值
	_textParams : function(values, oneType) {
		var fields = $(oneType).serializeArray();
		jQuery.each( fields, function(i, sel){
			if (sel.value) {
				if (!e5_queryform.paramReader._hasHiddenValue(values, sel.name))
					values.push(sel);
			}
		});
	},
	_hasHiddenValue : function(values, name) {
		for (var i = 0; i < values.length; i++) {
			if (values[i].name == name + "_ID" || values[i].name == name + "ID" )
				return true;
		}
		return false;
	}
}

e5_queryform.treeHandler = {
	curDialog : null,
	curIDField : null,
	curNameField : null,

	_select : function(nameField, idField, dataUrl){
		e5_queryform.treeHandler.curIDField = idField;
		e5_queryform.treeHandler.curNameField = nameField;
		
		//顶点位置
		var pos = e5_queryform.treeHandler._getDialogPos(document.getElementById(nameField));
		
		dataUrl = e5_queryform.dealUrl(dataUrl);
		
		e5_queryform.treeHandler.curDialog = e5.dialog({
			type:"iframe",
			value:dataUrl
		}, {
			title:false,
			width:pos.width,
			height:pos.height,
			left:pos.left,
			top:pos.top,
			resize:false
		});
				
		e5_queryform.treeHandler.curDialog.show();
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
		var pos = {left : p.x, 
			top : p.y + el.offsetHeight - 1,
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
		return urlRoot + "?userIDs=&multiple=" + multiple;
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
	}
}

//部门选择树
function selectDept(nameField, idField, multiple) {
	var theURL = e5_queryform.treeHandler._getUserUrl(idField, multiple, "e5workspace/manoeuvre/OrgSelect.do");
	e5_queryform.treeHandler._select(nameField, idField, theURL);
}
//用户选择树
function selectUser(nameField, idField, multiple) {
	var theURL = e5_queryform.treeHandler._getUserUrl(idField, multiple, "e5workspace/manoeuvre/UserSelect.do");
	e5_queryform.treeHandler._select(nameField, idField, theURL);
}
//角色选择树
function selectRole(nameField, idField, multiple) {
	var theURL = e5_queryform.treeHandler._getUserUrl(idField, multiple, "e5workspace/manoeuvre/RoleSelect.do");
	e5_queryform.treeHandler._select(nameField, idField, theURL);
}
//分类选择树
function selectCat(nameField, idField, catType, multiple) {
	var theURL = "e5workspace/manoeuvre/CatSelect.do?noPermission=1&catType=" + catType;
	if (!multiple) theURL += "&multiple=false";

	e5_queryform.treeHandler._select(nameField, idField, theURL);
}
function userWindowSelect(selectID, selectName){
	$("#" + e5_queryform.treeHandler.curIDField).val(selectID);
	$("#" + e5_queryform.treeHandler.curNameField).val(selectName);

	userWindowHidden();
}
function userWindowHidden() {
	if (e5_queryform.treeHandler.curDialog) 
		e5_queryform.treeHandler.curDialog.close();
	else
		e5_queryform.treeHandler.curDialog = null;
}
//分类选择窗口点“确定”
function catWindowSelect(catIDs, catNames, cascadeIDs, cascadeNames) {
	var ids = e5_queryform.treeHandler._transformCatIDs(cascadeIDs, 1);
	var names = cascadeNames;//e5_queryform.treeHandler._transformCatIDs(cascadeNames, 1);

	$("#" + e5_queryform.treeHandler.curIDField).val(ids);
	$("#" + e5_queryform.treeHandler.curNameField).val(names);

	catWindowHidden();
}
//分类选择窗口点“取消”
function catWindowHidden() {
	userWindowHidden();
}
