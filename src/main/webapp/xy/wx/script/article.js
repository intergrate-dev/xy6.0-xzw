$(function(){
	article_form.init();
});
var article_form = {
	editor : null,
	init : function() {
		$("#btnSave").click(article_form.doSave);
		$("#btnCancel").click(article_form.doCancel);
		
		var content = $("#a_content").val();
		
		editor = UE.getEditor("simpleEditor");
		editor.ready(function() {
			editor.setContent(article_form.decode(content));
		});
	},
	//保存提交
    doSave : function(){
		$("#a_content").val(editor.getContent());
		
        if (!$("#form").validationEngine("validate")){
            // 验证提示
            $("#form").validationEngine("updatePromptsPosition");
            return false;
        }
		
        $("#form").submit();
    },
	//退出按钮
	doCancel : function() {
		window.onbeforeunload = null;
		var dataUrl = "../../e5workspace/after.do?UUID=" + $("#form #UUID").val();
		window.location.href = dataUrl;
	},
	decode : function(content) {
		content = content.replace(/&amp;/g, "&");
		content = content.replace(/&lt;/g, "<");
		content = content.replace(/&gt;/g, ">");
		//content = content.replace(/&quot;/g, "\"");
		content = content.replace(/&#034;/g, "\"");

		return content;
	}
}
