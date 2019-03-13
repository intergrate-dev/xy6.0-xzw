//查是否重名时，增加站点条件
e5_form._duplicateUrl = function(field) {
	var siteID = e5_form.getParam("siteID");
	var parentID = e5_form.getParam("parentID");
	var theURL = "xy/Duplicate.do"
		+ "?DocLibID=" + $("#DocLibID").val()
		+ "&DocIDs=" + $("#DocID").val()
		+ "&field=" + field.attr("id")
		+ "&value=" + e5_form.encode(field.val())
		+ "&siteID=" + siteID
		+ "&parentID=" + parentID;
	return theURL;
}
var dir_form = {
	init : function() {
		//站点ID赋值
		var siteID = e5_form.getParam("siteID");
		$("#dir_siteID").val(siteID);
		$("#btnFormCancel").click(dir_form.btnCancel);
		
		//若是新建子栏目，则给父栏目ID赋值
		var isNew = e5_form.getParam("new");
		if (isNew == "1") {
			var parentID = e5_form.getParam("parentID");
			$("#dir_parentID").val(parentID);
			
			$("#DocID").val("");
			$("#dir_name").val("");
			$("#dir_url").val("");
			$("#dir_big5Url").val("");
		} else {
			var DocID = e5_form.getParam("DocID");
			var parentID = e5_form.getParam("parentID");
			var name = e5_form.getParam("name");
			var url = e5_form.getParam("url");
			$("#dir_parentID").val(parentID);
			
			$("#DocID").val(DocID);
			$("#dir_name").val(name);
			$("#dir_url").val(url);
		}
		
		$("#dir_name").addClass("validate[funcCall[checkValidDir]]");
		//url查重、验证格式；隐藏big5Url因为没用到
		$("#dir_url").addClass("validate[funcCall[checkDuplicate]]");
		$("#dir_url").addClass("validate[custom[url]]");
		$("#SPAN_dir_big5Url").parent().parent().hide();
		//$("#dir_big5Url").addClass("validate[custom[url]]");
		
		$("#form").attr("target", "iframe");
		$("#form").attr("action", "../../xy/site/FormSave.do");
	},
	btnCancel : function() {
		parent.dir_menu.dialog.close();	
	},
	//表单保存后仍保留页面
	refresh : function() {
		parent.dir_menu.dialog.close();	
	}
}

$(function(){
	dir_form.init();
});