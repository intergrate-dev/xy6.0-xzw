//编辑样式使用
var edit_style = {
		editorcontent : '',
		init : function() {
			edit_style.setContent();
		},
		setContent : function() {
			if (typeof UE == "undefined") return;
				
			var editor = UE.getEditor("simpleEditor");
			editor.ready(function() {
				var content = edit_style.editorcontent;
				
				content = content.replace(/&lt;/g, "<");
				content = content.replace(/&gt;/g, ">");
				//content = content.replace(/&quot;/g, "\"");
				content = content.replace(/&#034;/g, "\"");
				content = content.replace(/&nbsp;/g, " ");
				// alert(111)
				editor.setContent(content);
			});
		},
		//读编辑器的内容
		getContent : function() {
			if (typeof UE == "undefined") return "";
			
			var editor = UE.getEditor("simpleEditor");
			var content = editor.getContent();
			return content;
		}	
};
$(function() {
	edit_style.init();
});