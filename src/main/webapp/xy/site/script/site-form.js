var site_form = {
	init : function() {
		$("#site_webRoot").addClass("validate[funcCall[checkValidRoot]]");
		
		$("#form").attr("target", "iframe");
		$("#form").attr("action", "../../xy/site/FormSubmit.do");
	}
};
$(function() {
	site_form.init();
});