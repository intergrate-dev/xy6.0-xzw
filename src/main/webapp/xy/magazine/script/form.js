//报纸表单，模仿栏目扩展属性的js
var column_form = {
	templateFields : [
		["pa_template", "pa_templateArticle"],
		["pa_templatePad", "pa_templateArticlePad"]
	],
	init : function() {
		//站点ID赋值
		var siteID = e5_form.getParam("siteID");
		$("#pa_siteID").val(siteID);
		
		//只读
		$("#pa_template").prop("readonly", true); // 栏目模板
		$("#pa_templateArticle").prop("readonly", true); // 文章模板
		$("#pa_templatePad").prop("readonly", true); // 触屏版栏目模板
		$("#pa_templateArticlePad").prop("readonly", true); // 触屏版文章模板
		$("#pa_pubRule").prop("readonly", true); // 发布规则
		$("#pa_pubRulePad").prop("readonly", true); // 触屏版发布规则
		
		//选择按钮事件
		$("[templateType]").click(column_form.selTemplate);
		$("[ruleType]").click(column_form.selRule);
		$("#btnFormSave")[0].onclick = column_form.doSave;
		
		// 修改表单的提交功能
		$("#form").attr("target", "iframe");
		$("#form").attr("action", "../../xy/paper/formSubmit.do");
		
		//显示图标
		icon_form.showIcon("pa_iconBig", 0);
		icon_form.showIcon("pa_iconSmall", 1);
	},

	//保存前检查网站发布规则和触屏发布规则是否会导致发布时覆盖
	doSave : function() {
		if (!$("#form").validationEngine("validate")) {
			// 验证提示
			$("#form").validationEngine("updatePromptsPosition");
			return false;
		}
		
		if ($("#pa_pubRule_ID").val() && $("#pa_pubRule_ID").val() == $("#pa_pubRulePad_ID").val()) {
			alert("网站和触屏的发布规则相同，请修改");
			return false;
		}
		
		window.onbeforeunload = null;
	},
	// 选择模板
	winTmp : {},
	selTemplate : function(evt) {
		var type = $(evt.target).attr("templateType");
		var channel = $(evt.target).attr("channel");
		if (!channel) channel = 0;
		
		var nameField = column_form._fieldTemplate(channel, type);
		// 根据类型不同，显示出不同的模板选择树，以组为第一级
		var winId = type + "," + channel;
		if (column_form.winTmp[winId]) {
			column_form.winTmp[winId].show();
		} else {
			var siteID = e5_form.getParam("siteID");
			var dataUrl = "xy/template/TemplateSelect.jsp?type=" + type
					+ "&channel=" + channel
					+ "&siteID=" + siteID;
			dataUrl = e5_form.dealUrl(dataUrl);
			// 顶点位置
			var pos = e5_form.event._getDialogPos(document.getElementById(nameField));

			column_form.winTmp[winId] = e5.dialog({
				type : "iframe",
				value : dataUrl
			}, {
				showTitle : false,
				width : "350px",
				height : pos.height,
				pos : pos,
				resizable : false
			});
			column_form.winTmp[winId].show();
		}
			
		//隐藏input目录框上的验证信息
		$("#" + nameField).validationEngine("hide");
	},
	closeTemplate : function(channel, type, name, docID) {
		var nameField = column_form._fieldTemplate(channel, type);
		var idField = nameField + "_ID";
		
		$("#" + idField).val(docID);
		$("#" + nameField).val(name);
				
		var winId = type + "," + channel;
		if (column_form.winTmp[winId]) {
			column_form.winTmp[winId].hide(); // 调用dialog.close()会造成浏览器无响应，因此改成hide
		}
	},
	cancelTemplate : function(type) {
		var winId = type + ",0";
		if (column_form.winTmp[winId]) {
			column_form.winTmp[winId].hide(); // 调用dialog.close()会造成浏览器无响应，因此改成hide
		}
		var winId = type + ",1";
		if (column_form.winTmp[winId]) {
			column_form.winTmp[winId].hide();
		}
	},
	//确定当前要的是哪个模板字段
	_fieldTemplate : function(channel, type) {
		channel = parseInt(channel);
		type = parseInt(type);
		
		return column_form.templateFields[channel][type];
	},
	// ----以下选择发布规则---
	winRule : {},
	selRule : function(evt) {
		var type = $(evt.target).attr("ruleType");
		var nameField = (type == "0") ? "pa_pubRule" : "pa_pubRulePad";
		
		if (column_form.winRule[type]) {
			column_form.winRule[type].show();
		} else {
			var siteID = e5_form.getParam("siteID");
			var dataUrl = "xy/MainRuleTree.do?type=" + type + "&siteID=" + siteID;
			dataUrl = e5_form.dealUrl(dataUrl);

			// 顶点位置
			var pos = e5_form.event._getDialogPos(document.getElementById(nameField));
			column_form.winRule[type] = e5.dialog({
				type : "iframe",
				value : dataUrl
			}, {
				showTitle : false,
				width : "600px",
				height : pos.height,
				pos : pos,
				resizable : false
			});
			column_form.winRule[type].show();
		}
			
		//隐藏input目录框上的验证信息
		$("#" + nameField).validationEngine("hide");
	},
	closeRule : function(type, name, docID) {
		var nameField = (type == "0") ? "pa_pubRule" : "pa_pubRulePad";
		
		$("#" + nameField + "_ID").val(docID);
		$("#" + nameField).val(name);
		
		column_form.cancelRule(type);
	},
	cancelRule : function(type) {
		if (column_form.winRule[type]) {
			column_form.winRule[type].hide(); // 调用dialog.close()会造成浏览器无响应，因此改成hide
		}
	}
}
var icon_form = {
	//显示头像
	showIcon : function(id, flag) {
		$(".labelIconImg").hide(); //提示Label去掉
		
		var img = $("#iconImg" + flag);
		
		var url = $("#" + id).attr("oldvalue");
		if (url && url != "-") {
			img.attr("src", url);
			img.css("max-width", 150);
			img.css("max-height", 150);
			
			$("#img_" + id).hide();
		} else {
			img.hide();
		}
	}
}

e5_form.event.otherValidate = function() {
	if (e5_form.file.notImgFile("l_icon")){
		alert("请选择jpg, png, gif类型的文件");
		return false;
	}
	return true;
}

//查是否重名时，增加站点条件
e5_form._duplicateUrl = function(field) {
	var siteID = e5_form.getParam("siteID");
	var theURL = "xy/Duplicate.do"
		+ "?DocLibID=" + $("#DocLibID").val()
		+ "&DocIDs=" + $("#DocID").val()
		+ "&siteID=" + siteID
		+ "&value=" + e5_form.encode(field.val());
	return theURL;
}

$(function() {
	column_form.init();
});