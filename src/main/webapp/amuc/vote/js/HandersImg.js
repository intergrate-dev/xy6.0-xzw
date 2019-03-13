var HeadersImg = {
						
	init : function() {
		$("#btnSave").click(HeadersImg.save);
		$("#btnCancel").click(HeadersImg.close);
	},
		
	close : function() {
		window.onbeforeunload = "javascript:void(0);";
		window.location.href = "../../e5workspace/after.do?UUID=" + $("#UUID").val();
		window.opener.location.reload();
	},
	
	save : function() {
		var voteID = $("#DocID").val();
		//提交时判断是否上传图片
		var img = $("img").attr("src");
		if(img==""){
			alert("请上传页眉图片");
			return ;
		}else{
			var fp = $(".file-panel").is(":visible");//提交时判断是否上传图片
			if(fp==true){
				alert("请上传页眉图片");
				return ;
			}
		}
		var url=rootURL+"amuc/headersImg/save.do?action=save&voteID="+voteId;
		var vsFootersWord=$("#vsFootersWord").val();
		console.log(vsFootersWord);
		$.ajax({
			type:"POST",
			url:url,
			data:{action:'save',voteID:voteId,vsFootersWord:vsFootersWord},
			dataType:"json",
			success:function(data){
				HeadersImg.close();
			}
		});
	}
		
}

$(function() {
	HeadersImg.init();    
});