$(function() {
				
	var docID = $("#DocID").val();
	var imgID = 0;
	var viClassification = 2;
	if(docID>0){
		if(addOrEdit==0){
			$(".checkvotePageTab").addClass("disabled");//置灰查看投票页
		}
		$("#votesetTab").attr("href","../createVote/editVote.do?action=editVote&voteID="+voteId+"&UUID="+UUID+"&vsOptionType="+vsOptionType+"&DocIDs="+docID+"&addOrEdit=1"+"&chooseNumType="+chooseNumType+"&siteID="+getUrlVars("siteID"));
		$("#voteoptionTab").attr("href","../voteOption/initOptions.do?action=initOptions&voteID="+voteId+"&UUID="+UUID+"&vsOptionType="+vsOptionType+"&DocIDs="+docID+"&addOrEdit=1"+"&chooseNumType="+chooseNumType+"&siteID="+getUrlVars("siteID"));
		
		$("#queueListid").append("<ul id='filelist' " +
				"class='filelist'><li id='WU_FILE_0'>" +
				"<p class='imgWrap'><img id='imgid'" +
				" src='' /></p><div class='file-panel' " +
				"style='height: 0px;'>" +
				"</div></li></ul>");
		
		$.ajax({
			url:rootURL+"amuc/voteOption/getPathByVoteID.do?action=getImgPath&voteID="+docID+"&viClassification=2",
			type:"post",
			dataType:"JSON",
			async: false,
			success:function(data){
				//alert(data.pathList=="");
				//alert(data.pathList[0].viAddress);
				if(data.pathList!=""){
					$("#dndArea").hide();
					$("#subBtn").hide();
					$(".info").hide();
					$("#imgid").attr("src",data.pathList[0].viAddress);
					imgID = data.pathList[0].voteImageId;
					$(".file-panel").remove();//提交时判断是否上传图片
				}else{
					$("#filelist").hide();
					$("#delBtn").hide();
				}
				
			}
		});
	}else{
		//显示删除项
		$("#filelist").hide();
		$("#delBtn").hide();
	}
	
	$("#delBtn").click(function(){
		$("#imgid").attr("src","");
		$("#dndArea").show();
		$("#subBtn").show();
		$(".info").show();
		$("#delBtn").hide();
		$("#filelist").hide();
		//alert(imgID);
		clip.reposition();
		$.ajax({
			url:rootURL+'amuc/voteOption/delImg.do?action=delImg&imgID='
			+imgID+"&viClassification="+viClassification+"&voteID="+docID,
			type:"post",
			dataType:"JSON",
			success:function(data){
				//alert();
			}
		});
	});
	
	//点击文本框复制其内容到剪贴板上
	// 设置SWF文件的路径  
	ZeroClipboard.setMoviePath("../vote/js/ZeroClipboard.swf");
	// 创建ZeroClipboard对象  
	var clip = new ZeroClipboard.Client();    
	function copyUrl(){
		clip.setHandCursor(true);   
		// 要复制的内容  
		var content = $("#votefullurl").val();
		clip.setText(content);    
		clip.glue("copyvoteurl");  
		//这个是复制成功后的提示    
		clip.addEventListener( "complete", function(){    
			alert("已经复制到剪切板！"+"\n"+content);     
		});
	}
	
	$(document).resize(function(){
		   clip.reposition();
	});
	
	//查看投票地址
	$.ajax({
		url:rootURL+'amuc/voteOption/getVoteUrl.do?action=getVoteUrl&voteID='+docID,
		type:"get",
		dataType:"json",
		success:function(data){
			//alert(data.retinfo.vsAddress)
			var code = data.ret;  // 0表示没有查到该投票活动，1表示查到了
			if(code == "1"){
				var vsAddress = data.retinfo.vsAddress;
				$("#votefullurl").val(vsAddress);
				$("#votefullurl2").prop("href",vsAddress);
				copyUrl();
				//将URL转成二维码
				var qrcode = new QRCode(document.getElementById("qrcode"), {
			        width : 100,//设置宽高
			        height : 100
			    });
				qrcode.makeCode(vsAddress);
			}
		},
		complete: function(XMLHttpRequest, textStatus){
	        this;  // 调用本次AJAX请求时传递的options参数
	    },
	    error: function (XMLHttpRequest, textStatus, errorThrown) {
	    	if(textStatus == 'abort'){
	        	XMLHttpRequest.abort();
	        }else{
	        	alert("setvsAddress:"+errorThrown+","+textStatus+","+XMLHttpRequest.status);
	        }
	    }
	});
	
	//$("#copyvoteurl").hover(copyUrl); 
});

//获取url地址中的参数
function getUrlVars(name){
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
    var r = window.location.search.substr(1).match(reg);  //匹配目标参数
    if (r != null) return unescape(r[2]); return null; //返回参数值
}
