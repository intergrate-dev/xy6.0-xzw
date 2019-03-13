//报纸稿件的细览
$(function(){
	article_form.init();
});
var article_form = {
	init : function() {
		try {
			//直接显示的内容是带标签的，转换使标签（如<p>等）起作用
			var old = $("#a_content");
			var html = old.text();
			if (!html) return;
			
			var newOne = $("<span/>")
				.addClass("custview-field")
				.append($(html));
			
			var parent = $("#a_content").parent();
			parent.append(newOne);
			
			old.hide();
		} catch(e) {
		}
	}
}
