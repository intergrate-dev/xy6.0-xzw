var duprule_form = {
	init : function() {
		$("#drField").change(duprule_form.fillName);
		$("#btnFormSave").click(duprule_form.checkRepeat);
	},
	fillName : function() {
		var text = "";
		$("#drField option:selected").each(function(){
			if (text) text += "，";
			text += $(this).text();
		});
		$("#drFieldName").val(text);
	},
	checkRepeat : function(){
		var docID=$("#DocID").val();
		var drFieldName = $("#drFieldName").val();
		var url= encodeURI("../../amuc/member/Member.do?a=duprule&dfn=" + drFieldName+"&docID="+docID);
		var result ="";
		$.ajax({
			url: url,
			async:false,
			cache:false,
			success: function(data) {
				result = data;
				
		    },
		    error:function (XMLHttpRequest, textStatus, errorThrown) {
		    	alert(textStatus);
		    }
		});
		if(result == "false"){
			return true;
		}
		else{
			alert("查重规则重复，不能添加");
			return false;
		}
	}
}
$(function(){
	duprule_form.init();
});