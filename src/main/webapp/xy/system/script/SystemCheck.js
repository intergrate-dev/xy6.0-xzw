var systemTest = {
	
	init : function(){
		systemTest.initParam() ;
		systemTest.tabClick() ;
		systemTest.license() ;
		systemTest.btnClick() ;
		systemTest.trClick() ;
	},
	initParam : function(){
		$.post("../../xy/system/initCheckParam.do",{},function(param){
			$("#siteID").val(param.siteID?param.siteID:0) ;
			$("#colID").val(param.colID?param.colID:0) ;
			$("#articleID").val(param.articleID?param.articleID:0) ;
			$("#liveID").val(param.liveID?param.liveID:0) ;
			$("#activeID").val(param.activeID?param.activeID:0) ;
			$("#paperID").val(param.paperID?param.paperID:0) ;
			$("#userID").val(param.userID?param.userID:0) ;
			$("#ROOTURL").val(param.ROOTURL?param.ROOTURL:$("#ROOTURL").val()) ;
			$("#urlRoot").val(param.ROOTURL?param.ROOTURL:null) ;
			if(param.siteID != null)
				$.post("../../xy/system/initParam.do",{siteID:param.siteID},function(){}) ;
		},"json") ;
	},
	editParam : function(){
		$("#urlRoot").val($("#ROOTURL").val()) ;
		$.post("../../xy/system/initParam.do",{siteID:$("#siteID").val()},function(){}) ;
		$.ajax({
			url : "../../xy/system/editCheckParam.do",
			type : "post",
			datatype:"json",
			data :{
				ROOTURL : $("#ROOTURL").val() ,
				siteID : $("#siteID").val() ,
				colID : $("#colID").val() ,
				articleID : $("#articleID").val() ,
				activeID : $("#activeID").val() ,
				paperID : $("#paperID").val() ,
				userID : $("#userID").val() ,
				liveID : $("#liveID").val()
			},
			success:function(data){}
    	});
		$("#paramInfo").modal("toggle") ;
		$("#paramCancel").show() ;
	},
	license : function(){
		$.post("../../xy/system/LicenseView.do",{},function(obj){
			if(obj.expireTime != ""){
				$("#license div").eq(0).removeClass("hide") ;
				$("#expireTime").val(obj.expireTime) ;
			}
			$("#siteCount").val(obj.siteCount) ;
			$("#userCount").val(obj.userCount) ;
			for(var i = 0 ; i < obj.channel.length ; i++){
				var checked = obj.channel[i].usable?"checked":"" ;
				var tr = "<li><input type='checkbox' "+ checked+" disabled/>&nbsp;"+obj.channel[i].item+"</li>" ;
                $("#channel").append(tr) ;
            }
			for(var i = 0 ; i < obj.module.length ; i++){
                var checked = obj.module[i].usable?"checked":"" ;
                var tr = "<li><input type='checkbox' "+ checked+" disabled/>&nbsp;"+obj.module[i].item+"</li>" ;
				$("#module").append(tr) ;
			}
		},"json") ;
	},
	test : function(){
		var type = $("#currTab").val().split("Tab")[0] ;
		var url = null ;
		var checkUrl = null ;
		if(type == "paramCheck"){
			url = "../../xy/system/getParamNames.do" ;
			checkUrl = "../../xy/system/paramSingleCheck.do" ;
		}else{
			url = "../../xy/system/getApiNames.do" ;
			checkUrl = "../../xy/system/apiSingleCheck.do" ;
		}
		$.post(url,{},function(names){
			$("#"+type).find("table").empty() ;
			for(var i = 0 ; i < names.length ; i++){
				var name = names[i] ;
                $.ajax({
                    url : checkUrl,
                    type : "post",
                    datatype : "json",
                    data : {name:name},
                    async : true ,
                    success:function(obj){
                        // console.log("name = " + name + ", obj = " + obj ) ;
                        $("#"+type).find("table").append(systemTest.trOption(obj)) ;
                        systemTest.trClick(obj.name) ;
                    }
                });
			}
		},"json") ;
	},
	testSingle:function(){
		var name = $("#name").val() ;
		var type = $("#currTab").val().split("Tab")[0] ;
		var url = null ;
		var checkUrl = null ;
		if(type == "paramCheck"){
			url = "../../xy/system/getParamNames.do" ;
			checkUrl = "../../xy/system/paramSingleCheck.do" ;
		}else{
			url = "../../xy/system/getApiNames.do" ;
			checkUrl = "../../xy/system/apiSingleCheck.do" ;
		}
		$.post(checkUrl,{name:name},function(obj){
			var c = null ;
			var status = null ;
			if(obj.status != undefined){
				c = obj.status == '200' && obj.result == '0' ? 'test-success':'test-danger' ;
				status = obj.status != '200'? obj.status : obj.result == '0'?'正常':'异常' ;
			}else{
				c = obj.result == '0' ? 'test-success':'test-danger' ;
				status = obj.result == '0'?'正常':'异常' ;
			}
			$("#"+name).attr("class",c) ;
			$("#"+name).find("td").eq(3).html("用时:&nbsp;"+obj.time+"ms&nbsp;") ;
			$("#"+name).find("td").eq(4).html("状态:"+status+"") ;
		},"json") ;
	},
	tabClick : function(){
		$("li.testTab").click(function(evt){
			var id = $(evt.target)[0].id;
			systemTest.reset() ;
			$("li.select").removeClass("select") ;
			$("#" + id).addClass("select") ;
			$("#currTab").val(id) ;
			if(id.indexOf("paramCheck") > -1){
				$("#paramCheck").removeClass("hide") ;
				$("#apiCheck").addClass("hide") ;
				$("#apiTest").addClass("hide") ;
				$("#license").addClass("hide") ;
				
				$("#btnTest").removeClass("hide") ;
				$("#btnParam").removeClass("hide") ;
			}else if(id.indexOf("apiCheck") > -1){
				$("#paramCheck").addClass("hide") ;
				$("#apiCheck").removeClass("hide") ;
				$("#apiTest").addClass("hide") ;
				$("#license").addClass("hide") ;
				
				$("#btnTest").removeClass("hide") ;
				$("#btnParam").removeClass("hide") ;
			}else if(id.indexOf("apiTest") > -1){
				$("#paramCheck").addClass("hide") ;
				$("#apiCheck").addClass("hide") ;
				$("#apiTest").removeClass("hide") ;
				$("#license").addClass("hide") ;
				
				$("#btnTest").addClass("hide") ;
				$("#btnParam").removeClass("hide") ;
			}else if(id.indexOf("licenseTab") > -1){
				$("#paramCheck").addClass("hide") ;
				$("#apiCheck").addClass("hide") ;
				$("#apiTest").addClass("hide") ;
				$("#license").removeClass("hide") ;
				
				$("#btnTest").addClass("hide") ;
				$("#btnParam").addClass("hide") ;
			}
		}) ;
	},
	btnClick : function(){
		$("#btnTest").click(function(){
			var name = $("#name").val() ;
			if(name == "" || name == null){
				systemTest.test()
			}else{
				systemTest.testSingle() ;
			}
			systemTest.reset() ;
		}) ;
		$("#btnParam").click(function(){
			$("#paramInfo").modal("toggle") ;
		}) ;
		$("#paramSubmit").on("click",function(){
			systemTest.editParam() ;
		});
	},
	trClick:function(id){
		$("#"+id).on("click",function(){
			var name = $("#name").val() ;
			if(name != null){
				$("#"+name).attr("class",$("#c").val()) ;
			}
			$("#name").val(this.id) ;
			$("#c").val($(this).attr("class")) ;
			$(this).attr("class","test-default") ;
		}) ;
	},
	trOption : function(obj){
		var c = null ;
		var status = null ;
		if(obj.status != undefined){
			c = obj.status == '200' && obj.result == '0' ? 'test-success':'test-danger' ;
			status = obj.status != '200'? obj.status : obj.result == '0'?'正常':'异常' ;
		}else{
			c = obj.result == '0' ? 'test-success':'test-danger' ;
			status = obj.result == '0'?'正常':'异常' ;
		}
		var tr = "<tr class='"+c+"' id='"+obj.name+"'>"
			+"<td width='3%'>GET</td>"
			+"<td width='22%'>"+obj.item+"</td>"
			+"<td >"+obj.value+"</td>"
			+"<td width='15%'>用时:&nbsp;"+obj.time+"ms&nbsp;</td>"
			+"<td width='10%'>状态:"+status+"</td>"
			+"<td width='3%'><span class='glyphicon glyphicon-stop'></span></td>"
		+"</tr>" ;
		return tr ;
	},
	operateAlert:function(flag,suctext,faltext) {
		if (flag) {
			$("#alertDiv").attr("class","alert alert-success") ;
			$("#alertText").text(suctext) ;
		} else {
			$("#alertDiv").attr("class","alert alert-danger") ;
			$("#alertText").text(faltext) ;
		}
		$("#alertDiv").fadeIn(1000,function(){
	        $("#alertDiv").fadeOut(3) ;
	    }) ;
	},
	reset:function(){
		var name = $("#name").val() ;
		if(name != null){
			$("#"+name).attr("class",$("#c").val()) ;
		}
		$("#name").val(null) ;
		$("#c").val(null) ;
	},
}
$(function(){
	systemTest.init() ;
})