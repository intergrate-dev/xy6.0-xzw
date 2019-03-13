//站点地图配置
var seo_form = {
	init : function() {
		$("#btnSave").click(seo_form.doSave);
		$("#btnCancel").click(seo_form.doCancel);
		
		var dirApp = $("#dirApp");
		dirApp.val(dirApp.attr("oldValue"));
		
		var dirWeb = $("#dirWeb");
		dirWeb.val(dirWeb.attr("oldValue"));
	},
	//保存提交
    doSave : function(){
        $("#form").submit();
    },
	//退出按钮
	doCancel : function() {
		var dataUrl = "../../e5workspace/after.do?UUID=" + $("#UUID").val();
		document.getElementById("iframe").contentWindow.location.href = dataUrl;
	}
};
$(function() {
	seo_form.init();
});