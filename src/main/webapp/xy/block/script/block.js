var block_form = {
	init : function() {
		$("#b_dir").prop("readonly", true);
		$("#b_status").prop("checked",false);
	}
}
$(function(){
	block_form.init();
});