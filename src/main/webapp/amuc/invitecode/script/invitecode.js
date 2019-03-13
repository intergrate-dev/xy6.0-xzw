var icode = {
	init : function(){
		$("#form").validationEngine('attach',{
			promptPosition:'bottomRight',//验证提示信息的位置
		    scroll: false,//屏幕自动滚动到第一个验证不通过的位置
			autoPositionUpdate:true,//是否自动调整提示层的位置
			onValidationComplete:function(from,r){
				if (r){
					window.onbeforeunload = null;
					$("#btnSave").attr("disabled", true);
					if(flag) from[0].submit();
				}
			},
		});
	},
	close : function() {
		window.onbeforeunload = "javascript:void(0);";
		window.location.href = "../../e5workspace/after.do?UUID=" + $("#UUID").val();
	},
//	btnSave : function(){//设置保存按钮只允许点一次，点后失效
//		$("#btnSave").attr("disabled", true);
//		$("#form").submit();
//	}
}
$(function() {
	icode.init();
	//$("#btnSave").click(icode.btnSave);
	$("#btnCancel").click(icode.close);  
});