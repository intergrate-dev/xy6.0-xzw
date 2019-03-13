var leader_form = {
	init : function() {
		//站点ID赋值
		var siteID = e5_form.getParam("siteID");
		$("#l_siteID").val(siteID);
		
		$("#l_column").prop("readonly", true);
		$("#btnColumn").click(leader_form.selectColumn); 	//栏目
		
		// 修改表单的提交功能
		$("#form").attr("target", "iframe");
		$("#form").attr("action", "../../xy/leader/formSubmit.do");
		
		//显示头像
		icon_form.showIcon("l_icon");
	},
	// 点击选择栏目
	selectColumn : function(evt) {
		// 顶点位置
		var pos = e5_form.event._getDialogPos(document.getElementById("l_column"));

		var dataUrl = "../../xy/column/ColumnCheck.jsp?type=all&style=radio&ids=" + $("#l_columnID").val()
				+ "&ch=1&siteID=" + $("#l_siteID").val();
		e5_form.event.curDialog = e5.dialog({
			type : "iframe",
			value : dataUrl
		}, {
			showTitle : false,
			width : pos.width,
			height : "300px",
			pos : pos,
			resizable : false,
			esc:true
		});
		e5_form.event.curDialog.show();
	},
}
var icon_form = {
	//显示头像
	showIcon : function(id) {
		var url = $("#" + id).attr("oldvalue");
		if (url && url != "-") {
			url = "../../xy/image.do?path=" + url;
			$("#iconImg").attr("src", url);
			$("#iconImg").css("max-width", 150);
			$("#iconImg").css("max-height", 150);
			
			$("#img_" + id).hide();
		} else {
			$("#iconImg").hide();
		}
		$("#labelIconImg").hide();
	}
}

e5_form.event.otherValidate = function() {
	if (e5_form.file.notImgFile("l_icon")){
		alert("请选择jpg, png, gif类型的文件");
		return false;
	}
	return true;
}
// 栏目选择窗口关闭，回调函数
function columnClose(filterChecked, checks) {
	// [ids, names, cascadeIDs]
	$("#l_columnID").val(checks[0]);
	$("#l_column").val(checks[1]);
	columnCancel();
}
function columnCancel() {
	if (e5_form.event.curDialog)
		e5_form.event.curDialog.closeEvt();
	
	e5_form.event.curDialog = null;
}

$(function() {
	leader_form.init();
});