var icode = {
		
	close : function() {
		window.onbeforeunload = "javascript:void(0);";
		window.location.href = "../../e5workspace/after.do?UUID=" + $("#UUID").val();
	},
}
$(function() {
	
	$("#btnCancel").click(icode.close);  
});