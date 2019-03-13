$(document).ready(function () {
	$("#btnSave").click(function(){
		$("#form").submit();
	});
	//初始化
	apply.init();
	$("#form").validationEngine({
		promptPosition:'bottomLeft',
	    scroll: false,
		autoPositionUpdate:true,
		onValidationComplete:function(from,r){
			if (r){
				window.onbeforeunload = null;
				$("#btnSave").attr("disabled", true);
				apply.submit();
			}
		}
	});
});
//系统当前时间补零函数
function appendZero(s){return ("00"+ s).substr((s+"").length);} 

var apply = {
	init : function(){//根据活动id获取活动详细信息
		apply.aEndTime();
		$("#mMobile").change(apply.isApply);
	},
	aEndTime :function(){
		var id = $("#id").val();
		var tc = $("#tc").val();
		var param = "id=" + id + "&tc=" + tc ;
		var url = amucUrl + "act/getActEndTime?" + param;
		jQuery.support.cors = true;
		$.ajax({
	        type:"GET", //请求方式  
	        url: url, //请求路径  
	        cache: false,
	        dataType: 'TEXT',   //返回值类型  
	        success:function(data){
			    var d = new Date();
				var nowT = d.getFullYear() + "-" + appendZero(d.getMonth() + 1) + "-" + appendZero(d.getDate());
				var nowTime = new Date(nowT.replace("-", "/").replace("-", "/"));  
		        var actEndTime = new Date(data.replace("-", "/").replace("-", "/"));  
		        if(nowTime > actEndTime){
		        	layer.alert("活动已经结束!", 8,"消息");
		        	$("#btnSave").attr("disabled", true);
		        	$("#btnSave").unbind("click");
		        }
	        } ,
			error:function (XMLHttpRequest, textStatus, errorThrown) {
				alert(errorThrown);
			}
	    });
	},
	isApply : function(){//根据活动id获取活动详细信息
		var id = $("#id").val();
		var tc = $("#tc").val();
		var mMobile = $("#mMobile").val();
		var param = "id=" + id + "&tc=" + tc + "&mMobile=" + mMobile + "&stage=apply"
		var url = amucUrl + "act/status?" + param;
		jQuery.support.cors = true;
		$.ajax({
	        type:"GET", //请求方式  
	        url: url, //请求路径  
	        cache: false,
	        async:false,
	        dataType: 'TEXT',   //返回值类型  
	        success:function(data){
	    		if(data != null && data == 'true'){
	    			$("#btnSave").attr("disabled", true);
	    			layer.alert('该手机号已报名！', 6,"消息");
	    		} else {
	    			$("#btnSave").attr("disabled", false);
	    		}
	        } ,
			error:function (XMLHttpRequest, textStatus, errorThrown) {
				layer.alert(errorThrown, 8,"消息");
			}
	    });
	},
	submit : function(){
		var theURL = amucUrl + "act/apply";// + param;
		jQuery.support.cors = true;
		$.ajax({
	        type:"GET", //请求方式  
	        url:theURL, //请求路径  
	        cache: false,
	        data:$('#form').serialize(),  //传参 
	        dataType: 'TEXT',   //返回值类型  
	        success:function(data){
	    		if(data != null && data == 'true'){
	    			layer.alert('报名成功！', 6,"消息");
	    			$("#btnSave").unbind("click");
	    		} else {
	    			layer.alert(data, 8,"报名失败");
	    		}
	        } ,
			error:function (XMLHttpRequest, textStatus, errorThrown) {
				   layer.alert(errorThrown, 8,"消息");
			}
	    });
	}
}