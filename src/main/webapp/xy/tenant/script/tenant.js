var t_form = {
	init : function() {
		$("#te_code").blur(t_form.codeBlur);
	},
	//租户代号要保证小写
	codeBlur : function() {
		var code = $("#te_code").val().toLowerCase();
		$("#te_code").val(code);
	}
}

$(function(){
	t_form.init();
});