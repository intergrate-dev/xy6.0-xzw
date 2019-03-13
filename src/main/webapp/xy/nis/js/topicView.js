var topic_view = {
	init : function() {
		//把Url加在<a>元素上，使可以直接点开
		var url = $("#t_articleUrlPad").html();
		if (url) {
			var a = $("<a/>")
				.attr("href", url)
				.attr("target", "_blank")
				.html(url);
			$("#t_articleUrlPad").html(a);
		}
		
		var url = $("#t_articleUrl").html();
		if (url) {
			var a = $("<a/>")
				.attr("href", url)
				.attr("target", "_blank")
				.html(url);
			$("#t_articleUrl").html(a);
		}
	}
}
$(function(){
	topic_view.init();
});