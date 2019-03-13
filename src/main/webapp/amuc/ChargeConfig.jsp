<%@page import="java.math.BigDecimal"%>
<%@include file="../e5include/IncludeTag.jsp"%>
<%@page import="java.util.Set"%>
<%@page import="com.founder.e5.sys.SysConfig"%>
<%@page import="java.util.Map"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false" />
<%@page pageEncoding="UTF-8"%>
<html>
<head><title>全媒体客户资源运营平台-支付方式管理</title>
	<meta content="text/html;charset=utf-8" http-equiv="Content-Type" />
	<meta content="IE=edge" http-equiv="X-UA-Compatible" />
	<script type="text/javascript" src="../e5script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="../e5script/jquery/jquery-autocomplete/jquery.autocomplete.min.js"></script>
	<script type="text/javascript" src="../e5script/e5.min.js"></script>
	<script type="text/javascript" src="../e5script/e5.utils.js"></script>
	<script type="text/javascript" src="../amuc/script/bootstrap/js/bootstrap.min.js"></script>


	<link type="text/css" rel="stylesheet" href="../e5script/jquery/jquery-autocomplete/jquery.autocomplete.css"/>
	<link rel="stylesheet" type="text/css" href="../amuc/script/bootstrap/css/bootstrap.css" />
	<link rel="stylesheet" type="text/css" href="../amuc/css/main.css">
	<link rel="stylesheet" type="text/css" href="../amuc/script/lhgcalendar/lhgcalendar.bootstrap.css"/>
	<link type="text/css" rel="StyleSheet" href="../e5style/reset.css"/>
	<link type="text/css" rel="StyleSheet" href="../amuc/css/sys-main-body-style.css"/>
	<link rel="stylesheet" type="text/css" href="../e5script/jquery/dialog.style.css" />
	<style type="text/css">
		.table td.fold{
			background:#F5F5F5;
			border-right:1px solid #e8e8e8;
			vertical-align:top;

		}
		tr {
		    display: table-row;
		    vertical-align: inherit;
		    border-color: inherit;
		}
	</style>
</head>
<body>
<%@include file="../amuc/inc/Header.inc"%>
<div class="mainBodyWrap" style="height:80%; overflow:auto;">
	<div class="mainWrap" style="padding-bottom: 10px;">
		<input type="button" class="button" onClick="refreshPage()" value="刷新"/>
		<input type="button" class="button" onClick="modifySysConfig()" value="修改"/>
	</div>
	<span id="initConfig"></span>
</div>
</body>
<script type="text/javascript">
	$(document).ready(function(){
		initConfig();
		mouseClose();
		  
	});
	function initConfig() {
		//var siteID = getUrlVars("siteID");
		var siteID = 1;
		$.ajax({
			url:"charge/getConfigList.do",
			type:'get',
			data:{siteID:siteID},
			dataType:"json",
			cache:false,
			async:false,
			success:function(datas){
				var code = datas.code;
				var data = datas.data;
				if(code == '1'){
					$("#initConfig").empty();
					var html =
						'<table id="tab1" style="table-layout:fixed" cellpadding="0" cellspacing="0" class="table"><tr>'
						+'<th width="100">支付类型</th>'
						+'<th width="150">配置项</th>'
						+'<th width="300">值</th>'
						+'<th width="400">说明</th>'
						+'</tr>';
					
						html = html +
							'<tr id="alipay" onclick="SelectID1(this)">'
							+'<td id="" onclick="" rowspan="'+data.length + 1 +'" class="fold">支付宝</td>'
						+'</tr>';
					for(var i=0;i<data.length;i++){				
						html = html +
							'<tr id="'+data[i].SYS_DOCUMENTID+'" onclick="SelectID1(this)">'
								+'<td id="'+data[i].SYS_DOCUMENTID+'_1">'+data[i].configuration+'</td>'
								+'<td id="'+data[i].SYS_DOCUMENTID+'_2" style="white-space: nowrap;text-overflow: ellipsis;overflow: hidden;">'+data[i].parameter+'</td>'
								+'<td id="'+data[i].SYS_DOCUMENTID+'_3">'+data[i].Description+'</td>'
								+'</tr>';		
						
					}
					$("#initConfig").append(html + '</table>');
				}else if(code == '0'){  //参数错误
					$.MsgBox.Alert("温馨提示",data);
				}
			},
			complete: function(XMLHttpRequest, textStatus){
		        this;  // 调用本次AJAX请求时传递的options参数
		    },
		    error: function (XMLHttpRequest, textStatus, errorThrown) {
		    	alert("error status : " + XMLHttpRequest.status);
		    }
		});
	};
	function UpdateConfig() {
		var DocID = $("#DocID").val();
		var parameter = $("#parameter").val();
		var Description = $("#Description").val();

		$.ajax({
			url:"charge/UpdateConfig.do",
			type:'get',
			data:{DocID:DocID,parameter:parameter,Description:Description},
			dataType:"json",
			cache:false,
			async:false,
			success:function(datas){
				refreshPage();
			},
			complete: function(XMLHttpRequest, textStatus){
		        this;  // 调用本次AJAX请求时传递的options参数
		    },
		    error: function (XMLHttpRequest, textStatus, errorThrown) {
		    	alert("error status : " + XMLHttpRequest.status);
		    }
		});
	};
	function mouseClose() {
		$("#configItemWinClose").on('mouseenter',
				function() {
					$(this).css("background-image", "url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABwAAAAcCAYAAAByDd+UAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAJwSURBVEhLvZbLSiNBFIb7DVyKiIgb17oQRRAXgor6CIIIeQKXMksfxYUbFbMZRh0Yb6ODMgEddCVmoWkRLzFekukxfay/+lRbqSqTVob+4CyqzuVPV59TaS8JYRhmhM0Ly5MB9tiX4fDPIQq0CpsT9sC1G4JYzmnlMskQCRPCrrnOh0EuanC5+ojAL5wXc5/LUW5qitba2ynreTWGPfgQY4JaXNaNKfZ0dkY7g4OWyHuGWOTovCuKI+AYib+8TF+bmpyF6xlykKuD2iwTITbQIPE7Q4Kr2EdMF0VtaLCcFJxjnzySzzyZaaihHy80WE4Kxq3vemcns7PStzsyYvn+zMxQUCzSRne35UMtBTSUWIb3ZKeZSRCrBoH0lwsF2u7vj32/JyepWi5L3/3hIW319dXkwvTuhRYE53kt29tMMAlub2lvdJRy09MUVqu8G3GxsGDlo6YCWhCMryvXnO0OD1PF9zkiQj5VGPIqonhwQOsdHVY+aiqgVfMIZrCy7YEBCm5uOMqmdHTkFFOmk0gQ9nNoiF4eHznyjed8nr41NztzlOkkFsQ7cwmWz89ps6fHmaNMJ5Gg7MZKhaNs/pVK8thduTCdhk2DOVNjoXg6PaW/V1e8ikBj7Y2NWflW06BVee0cC/x6nYfjY/nOfnR1yRHRucxmrXzXWNQdfNwgGGpwt79Pa21tsQ+XAC4D4K+s0GpLS00uzBp8vm3qXm1bvb1UWFyk752dlu/X+Dj5S0vOTnVebUAsUr+80/17AmIjvT9ghXCk94mhMEUBOg3t7ZpT7MGnd6OioZgCRyAsnc9EhUhI70PYRBT4T5/6nvcKYG1hElXAZggAAAAASUVORK5CYII=)");
				});
		$("#configItemWinClose").on('mouseleave',
				function() {
					$(this).css("background-image", "url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABwAAAAcCAYAAAByDd+UAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAJeSURBVEhLvZbPq2lRFMf9B4bSTTIxZiBSMlCI9ycoKX+Bod7w/il3YIL4NyhFmYmBKD2Sp0ix3vqes/e529n74t33Op9astevr3PO2tvxvcLtdquzfbAtyAV8IlYX6d+DG7yxvbP9Fr2fglxR8ybavAYX/GD7Jfr8NahFD9HuMZz4U9Q5jEYjqlarFA6HiVPuDD7EkOMGvTjna9xi8/mcstmsJvKVIRc1Kl+K4haIHItut0t+v9/Y+JGhBrUq6M2xT9iBAXGeGQrY/U+miqI3NNhvw4t3EbNuyXeuzG3ood5eaLDfhhfO6JueWbPZtGKFQkGLNRoN2u/3FI/HtRh6SaDBPkusLnzWpMlkaRC7XC5WfLVaUTqddmKVSoVOp5MVG4/HlEql7mph6vRCC4IfYm2Nt7vAzW63o2KxSLVaja7Xq/DatFotrR49JdCCoHNcmfZZPp+n9XotMmxwVVwnVjbD4ZAikYhWj54SaN1dgjtZWiaToe12K7J0JpOJUUyaykuCsFwuR8fjUWR+slgsKBAIGGukqbwsiGdmElwul5RIJIw10lReEsQ0ns9nkaVzOBys226qhak8HRrsM7ktJLPZjDabjVjZYLBKpZJWrw0NfzzcFvj1KtPp1HpmsVjM2iIq/X5fqzdti4cbHycINjUYDAYUCoWcGA4BHAag1+tRMBi8q4VpGx/wl4dHWzKZpHa7TdFoVIuVy2XqdDrGSTUebYAXnh/e3v49AXZ49wcs4YB3rxgStyjApGG8TfsUPsTUaZQ8FZPgFrB585oo4QLvXoTdcIP/9Krv8/0BDUSOirKWU6wAAAAASUVORK5CYII=)");
				});
	};
	var sysConfigID="0";
	var olde=null;
	function SelectID1(e)
	{
		if(olde!=null)olde.style.backgroundColor=document.bgColor
		e.style.backgroundColor="#e8e8e8"
		olde=e
		sysConfigID=e.id;
	}

	function modifySysConfig(){
		var selid=sysConfigID;
		if(selid=='0')
			alert("请选择要修改的配置项！！！");
		else{
			var url="charge/UpdateConfig.do";
			//$(".posR").show();
			configItemWin.showWin(url,600,310,selid);
		}
	}

	function refreshPage() {
		document.location.href=document.location.href;
	}
	var configItemWin = {
			scrolling: 'auto',//是否显示滚动条 no,yes,auto
			closeCallBack:null,
			id:"configItemForm1",
			showWin: function(src, width, height,id) {
				var titleDivHeight=0;
				var iframeHeight = height - titleDivHeight;
				var marginLeft = width / 2;
				var marginTop = height;
				var inntHtml = '';
				inntHtml += '<div id="mask" style="width:100%; height:100%; position:fixed; top:0; left:0; z-inde:1999;background:rgb(0, 0, 0); filter:alpha(opacity=50); -moz-opacity:0.5; -khtml-opacity: 0.5; opacity:0.5;"></div>'
				inntHtml += '<div id="maskTop" style="width: ' + width + 'px; height: ' + height + 'px; border: #999999 1px solid; background: #fff; color: #333; position: fixed; top: 65%; left: 50%; margin-left: -' + marginLeft + 'px; margin-top: -' + marginTop + 'px; z-index: 2999; filter: progid:DXImageTransform.Microsoft.Shadow(color=#909090,direction=120,strength=4); -moz-box-shadow: 2px 2px 10px #909090; -webkit-box-shadow: 2px 2px 10px #909090; box-shadow: 2px 2px 10px #909090;">'
				inntHtml += '<div id="maskTitle" style=" height: '+titleDivHeight+'px; line-height: 50px; font-family: Microsoft Yahei; font-size: 20px; color: #333333; padding-left: 20px; border-bottom: 1px solid #999999; position: relative;">'
				inntHtml += ''
				inntHtml += '<div id="configItemWinClose" style="width: 28px; height: 28px; cursor: pointer; position: absolute; top: -12px; right: -9px;"></div>'
				inntHtml += '</div>'
				//inntHtml += '<iframe id= "'+id+'" width="' + width + '" height="' + iframeHeight + '" frameborder="0" scrolling="' + this.scrolling + '" src="' + src + '"></iframe>';
				inntHtml += '<form action="'+ src +'" method="get" class="cmxform" id="signupForm" style="text-align:center;margin-top:63px;">'
				inntHtml += '<input id="DocID" name="DocID" minlength="2" autofocus="autofocus" autocomplete="off" type="text" class="color9 mt87" type="text" value="'+id+'" style="display:none"/>'
				inntHtml += '<span>配置项：</span><input id="" name="" minlength="2" autofocus="autofocus" autocomplete="off" type="text" class="color9 mt87" type="text" value="'+$("#"+id+"_1").text()+'" readonly="readonly" style="padding: 8px;margin-left:24px;"/><br/>'
				inntHtml += '<span style="margin-right: 7px;">配置项值：</span><input id="parameter" name="parameter" minlength="2" autofocus="autofocus" autocomplete="off" type="text" class="color9 mt87" type="text" value="'+$("#"+id+"_2").text()+'" style="padding: 8px;margin: 16px 12px;"/><br/>'
				inntHtml += '<span>配置项说明：</span><input id="Description" name="Description" minlength="2" autofocus="autofocus" autocomplete="off" type="text" class="color9 mt87" type="text" value="'+$("#"+id+"_3").text()+'" style="padding: 8px;"/><br/>'
				inntHtml += '<input id="btn1" type="button" onClick="refreshPage()" style="margin-top:20px;" class="btn activeBtn mt30" value="取消"></input>'
				inntHtml += '<input id="btn" type="button" onClick="UpdateConfig()" style="margin-top:20px;margin-left:20px;" class="btn activeBtn mt30" value="修改"></input>'
				inntHtml += '</form>'
				inntHtml += '</div>'
				$("body").append(inntHtml);
			}
		};
	
	function getUrlVars(name){
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
        var r = window.location.search.substr(1).match(reg);  //匹配目标参数
        if (r != null) return unescape(r[2]); return null; //返回参数值
	}
</script>
</html>