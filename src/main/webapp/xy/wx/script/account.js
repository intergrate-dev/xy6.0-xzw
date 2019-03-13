//微信账号表单，模仿栏目扩展属性的js
var column_form = {
	init : function() {
		//站点ID赋值
		var siteID = e5_form.getParam("siteID");
		$("#wxa_siteID").val(siteID);
		
		//只读
		$("#wxa_dir").prop("readonly", true);
		$("#wxa_template").prop("readonly", true);
		
		//选择按钮事件
		$("[templateType]").click(column_form.selTemplate);
	},
	// 选择模板
	winTmp : {},
	selTemplate : function(evt) {
		var type = $(evt.target).attr("templateType");
		var channel = $(evt.target).attr("channel");
		if (!channel) channel = 0;
		
		var nameField = "wxa_template";
		// 根据类型不同，显示出不同的模板选择树，以组为第一级
		if (column_form.winTmp[type]) {
			column_form.winTmp[type].show();
		} else {
			var siteID = e5_form.getParam("siteID");
			var dataUrl = "xy/template/TemplateSelect.jsp?type=" + type
					+ "&channel=" + channel
					+ "&siteID=" + siteID;
			dataUrl = e5_form.dealUrl(dataUrl);
			// 顶点位置
			var pos = e5_form.event._getDialogPos(document.getElementById(nameField));

			column_form.winTmp[type] = e5.dialog({
				type : "iframe",
				value : dataUrl
			}, {
				showTitle : false,
				width : "350px",
				height : pos.height,
				pos : pos,
				resizable : false
			});
			column_form.winTmp[type].show();
		}
			
		//隐藏input目录框上的验证信息
		$("#" + nameField).validationEngine("hide");
	},
	closeTemplate : function(channel, type, name, docID) {
		var nameField = "wxa_template";
		var idField = nameField + "_ID";
		
		$("#" + idField).val(docID);
		$("#" + nameField).val(name);
				
		if (column_form.winTmp[type]) {
			column_form.winTmp[type].hide(); // 调用dialog.close()会造成浏览器无响应，因此改成hide
		}
	},
	cancelTemplate : function(type) {
		if (column_form.winTmp[type]) {
			column_form.winTmp[type].hide(); // 调用dialog.close()会造成浏览器无响应，因此改成hide
		}
	}
}
$(function() {
	column_form.init();
});