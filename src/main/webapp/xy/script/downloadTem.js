$(function() {
	$("#SPAN_SYS_DOCUMENTID").attr("style","display:none");
	$("#SPAN_SYS_DOCLIBID").attr("style","display:none");
	var DocLibID = $("#SYS_DOCLIBID").html();
	var DocID = $("#SYS_DOCUMENTID").html();
	var urlDown = "../../xy/template/getDoc.do?doclibID="+DocLibID+"&DocID="+DocID;
	$("#t_file").children("a").attr("href","javascript:void(0)");
	$("#t_file").children("a").click(function(){
		var urlDown = "../../xy/template/getDoc.do?doclibID="+DocLibID+"&DocID="+DocID;
		$.ajax({url: urlDown, async:false, success: function (data) {
			if(data == "false"){
				alert("未找到相应模板，请尝试重新上传！");
			}else{
				window.location.href="../../xy/template/loadZip.do?doclibID="+DocLibID+"&DocID="+DocID;
			}
		},
		error: function() {
            alert("未找到相应模板，请尝试重新上传！");
        }
	});	
	})
});