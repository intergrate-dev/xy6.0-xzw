var pcard = {
	init : function() {
		pcard.validateEvent();  //表单验证
		//点击事件
		$("#btnCancel").click(xgenerate.close);
	},
	validateEvent: function(){
		$("#form").validationEngine('attach', {
			promptPosition : 'bottomRight',// 验证提示信息的位置
			scroll : false,// 屏幕自动滚动到第一个验证不通过的位置
			autoPositionUpdate : true,// 是否自动调整提示层的位置
			onValidationComplete : function(from, r) {
				if (r) {
					window.onbeforeunload = null;
					$("#btnSave").attr("disabled", true);
					if (flag)
						from[0].submit();
				}
			},
		});
	},
	close : function() {
		window.close();
	},
	in_array : function(array , e){
		for(i=0;i<array.length;i++)  
		{  
			if(array[i] == e)  
				return i;  
		}  
		return -1;  
	}
}
$(function() {
	pcard.init();
});