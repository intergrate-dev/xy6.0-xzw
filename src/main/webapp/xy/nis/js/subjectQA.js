var xy_subject = {
	init : function() {
		$("#a_content").attr("readonly", true);
		
		$("#form").attr("target", "iframe");
		$("#form").attr("action", "../../xy/nis/AnswerSubmit.do");
	}
};
$(function(){
	xy_subject.init();
});