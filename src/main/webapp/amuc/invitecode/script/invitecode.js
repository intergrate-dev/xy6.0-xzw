var icode = {
	init : function(){
		$("#form").validationEngine('attach',{
			promptPosition:'bottomRight',//��֤��ʾ��Ϣ��λ��
		    scroll: false,//��Ļ�Զ���������һ����֤��ͨ����λ��
			autoPositionUpdate:true,//�Ƿ��Զ�������ʾ���λ��
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
//	btnSave : function(){//���ñ��水ťֻ�����һ�Σ����ʧЧ
//		$("#btnSave").attr("disabled", true);
//		$("#form").submit();
//	}
}
$(function() {
	icode.init();
	//$("#btnSave").click(icode.btnSave);
	$("#btnCancel").click(icode.close);  
});