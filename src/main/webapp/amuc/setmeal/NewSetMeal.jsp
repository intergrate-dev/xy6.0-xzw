<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@page import="com.founder.e5.web.WebUtil"%>
<%
	String path = WebUtil.getRoot(request);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>My JSP 'NewSetMeal.jsp' starting page</title>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">
<link type="text/css" rel="stylesheet" href="../../e5style/reset.css" />
<link type="text/css" rel="stylesheet" href="../../e5script/jquery/jQuery-Validation-Engine/css/validationEngine.jquery.css" />
<link type="text/css" rel="stylesheet" href="../../e5script/jquery/dialog.style.css" />
<link type="text/css" rel="stylesheet" href="../../e5script/jquery/jquery-autocomplete/jquery.autocomplete.css" />
<link type="text/css" rel="stylesheet" href="../../e5script/lhgcalendar/lhgcalendar.bootstrap.css">
<link type="text/css" rel="stylesheet" href="../../e5style/e5form-custom.css" />
<link type="text/css" rel="stylesheet" href="../../amuc/script/bootstrap/css/bootstrap.css" />
<link type="text/css" rel="stylesheet" href="../../amuc/css/form-custom.css" />
<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
<script type="text/javascript" src="../../e5script/jquery/jQuery-Validation-Engine/js/languages/jquery.validationEngine-zh_CN.js"></script>
<script type="text/javascript" src="../../e5script/jquery/jQuery-Validation-Engine/js/jquery.validationEngine.js"></script>
<script type="text/javascript" src="../../e5script/jquery/jquery.dialog.js"></script>
<script type="text/javascript" src="../../e5script/jquery/jquery-autocomplete/jquery.autocomplete.min.js"></script>
<script type="text/javascript" src="../../e5script/calendar/usecalendar.js"></script>
<script type="text/javascript" src="../../e5script/lhgcalendar/lhgcalendar.js"></script>
<script type="text/javascript" src="../../e5workspace/script/form-custom.js"></script>
<script type="text/javascript" src="../../amuc/member/script/member.js"></script>
</head>
<body>
<style type="text/css">
	.defalttest {
		color: #CCCCCC
	}
	#tab tr {
		height: 50px;
		font-size: 13px;
	}
	input {
		border: 1px solid #ddd;
		border-radius: 4px;
	}
	#tab{
		font-size: 13px;
		margin-left: 50px;
	    margin-top: 15px;
	}
	.defalttest{
		height: 30px;
	    width: 300px;
	    padding-left: 5px;
	    color: #666;
	    border-radius: 0;
	}
	#EffectTime,#ExpireTime{
		height: 30px;
	    width: 130px;
	    background: #fff;
	    border-radius: 0;
	}
	#expiryDate{
		height: 30px;
	    width: 190px;
	    background: #fff;
	   	margin-left: 2px;
	}
	.saveBtn{
		background-color: #1bb8fa !important;
	    color: #FFFFFF;
	    width: 100px !important;
	    height: 30px;
	    border-radius: 3px;
	    border: none;
	    margin-right: 10px;
	}
	.cancelBtn{
		height: 30px;
	    background: #b1b1b1;
	    border: none;
	    color: #fff;
	    border-radius: 3px;
	    padding: 0 27px;
	    text-shadow: none;
	}
	.mr10{
		margin-right: 10px;
	}
	.mr3{
		margin-right: 3px !important;
	}
</style>
	<iframe id="iframe" name="iframe" style="display:none;"></iframe>
	<form action="<%=path%>/amuc/setmeal/CreateSetMeal.do"
		id="sub" method="post">
		<input type="hidden" id="DocIDs" name="DocIDs"
			value="<%=request.getParameter("DocIDs")%>" /> <input type="hidden"
			id="UUID" name="UUID" value="<%=request.getParameter("UUID")%>" />
			<input type="hidden"
			id="flag" name="flag" value="" />
			<input type="hidden"
			id="FVID" name="FVID" value="<%=request.getParameter("FVID")%>" />
			<input type="hidden"
			id="HASCHANGE" name="HASCHANGE" value="<%=request.getParameter("HasChange")%>" />
			<input type="hidden"
			id="siteID" name="siteID" value="<%=request.getParameter("siteID")%>" />
			<table id="tab">
				<tr>
					<td width="85px">套餐名称：</td>
					<td><input value="请输入套餐名称" class="defalttest" name="setMealNmae" id="setMealNmae" onfocus="if(value=='请输入套餐名称'){value='';}" onblur="if(value==''){value='请输入套餐名称';}"></td>
				</tr>
				<!-- <tr>
					<td>套餐内容:</td>
					<td style="width:600px;"><span id="cen"></span> <input type="button" value="+ 添加报纸"
						style="margin-top:10px;border-radius: 0px;border: 1px dashed  #cccccc;height:40px;width:100px;background-color:#F0F0F0"
						id="tjbz" onclick="selectCat('setMealContent', 'mRegionID', '16', true)"/> <input id="mRegionID" name="mRegionID"
						value="" type="hidden" /> <input name="setMealContent"
						id="setMealContent" style="height:0px;width:0px"> <input
						name="paperNumber" id="paperNumber" type="hidden"></td>
				</tr> -->
				<tr>
					<td width="85px" valign="top" style="padding-top:10px;">套餐内容：</td>
					<td id="setMealContent"><!-- <input type="checkbox" name="setMealContent" value="1">
						1天前<input type="checkbox" name="setMealContent" value="7"> 7天前 <input
						type="checkbox" name="setMealContent" value="14"> 14天前 <input
						type="checkbox" name="setMealContent" value="30">30天前  -->
					</td>
				</tr>
				<tr>
					<td width="85px">套餐金额：</td>
					<td><input value="￥0.00" class="defalttest" name="setMealMoney" id="setMealMoney" onfocus="if(value=='￥0.00'){value='';}" onblur="if(value==''){value='￥0.00';}"></td>
				</tr>
				<tr>
					<td width="85px">有效期：</td>
					<td><input style="margin-top: 7px; margin-right: 5px;" class="pull-left" type="radio" checked="true" name="yxq" id="yxq" onclick="kong(1)" value="ding"> 固定日期
						
					
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <input id="EffectTime"
						name="EffectTime" readonly="true" style="height:30px;width:130px"
						onclick="showCalendar('EffectTime', 'yyyy-mm-dd');"> -- <input
						name="ExpireTime" id="ExpireTime" readonly="true"
						style="height:30px;width:130px"
						onclick="showCalendar('ExpireTime', 'yyyy-mm-dd');"></td>
				</tr>
				<tr>
					<td></td>
					<td>
						<input class="pull-left"  style="margin-top: 7px; margin-right: 5px;" type="radio" name="yxq" onclick="kong(2)"value="zi"> 连续自然日&nbsp;&nbsp;&nbsp;&nbsp;
						<input value="请输入天数" id="expiryDate" name="expiryDate" readonly="true" class="defalttest" onfocus="if(value=='请输入天数'){value='';}" onblur="if(value==''){value='请输入天数';}">
						<span class="defalttest"><br />
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
						<span class="tip" style="margin-left: -5px; color: #999; font-size: 12px;display: inline-block; margin-top: 10px;">购买之日起计算所设天数</span></span></td>
				</tr>
				<tr>
					<td width="85px">销售渠道：</td>
					<td>
						<div class="pull-left mr10">
							<input class="pull-left mr3" type="radio" checked="true" name="Channel" value="pc 移动">全部渠道  
						</div>
						<div class="pull-left mr10">
							<input class="pull-left mr3" type="radio" name="Channel" value="pc">pc
						</div>
						<div class="pull-left mr10">
							<input class="pull-left mr3" type="radio" name="Channel" value="移动">移动
						</div>
					</td>
				</tr>
				<tr>
					<td width="85px">销售状态：</td>
					<td>
						<div class="pull-left mr10">
							<input class="pull-left mr3" type="radio" checked="true" name="Status" value="待售"> 待售
						</div>
						<div class="pull-left mr10">
							<input class="pull-left mr3" type="radio" name="Status" value="在售"> 在售
						</div>
						<div class="pull-left mr10">
							<input class="pull-left mr3" type="radio" name="Status" value="停售"> 停售
						</div>
					</td>
				</tr>
				<tr style="display:none;"><!-- 隐藏过期提醒 -->
					<td width="85px" valign="top" style="padding-top:10px;">过期提醒：</td>
					<td style="line-height: 38px;">
						<div class="pull-left mr10">
							<input style="margin-top: 13px;" class="pull-left mr3" type="checkbox" name="Remind" value="0" checked>
							<span>0天前</span>
						</div>
						<div class="pull-left mr10">
							<input style="margin-top: 13px;" class="pull-left mr3" type="checkbox" name="Remind" value="7">
							<span>7天前 </span>
						</div>
						<div class="pull-left mr10">
							<input style="margin-top: 13px;" class="pull-left mr3" type="checkbox" name="Remind" value="14">
							<span>14天前</span> 
						</div>
						<div class="pull-left mr10">
							<input style="margin-top: 13px;" class="pull-left mr3" type="checkbox" name="Remind" value="30">
							<span>30天前</span>
						</div> 
						<input class="pull-left mr3"   style="margin-top: 13px;" type="checkbox" name="Remind" id="weizhi" value="">
						<input style="margin-top: 5px; width: 120px;" value="请输入天数" id="fjts" class="defalttest pull-left"><br /> 
						<span class="defalttest" style="color: #999; font-size: 12px; margin-left: -5px;"> 过期时间前按所设置的规则下发短信提醒</span>
					</td>
				</tr>
				<tr>
					<td></td>
					<td>
						<br />
						<input class="saveBtn" type="button" value="保存" onclick="save(0)" />
						<input  class="saveBtn" type="button" value="保存并新建" onclick="save(1)" />
						<input class="cancelBtn" type="button" value="取消" onclick="clos()" />
					</td>	
				</tr>
			</table>
	</form>

</body>
</html>
<script type="text/javascript" language="utf-8">
function setText(obj)
{
    if(obj.checked==true)
    {
        document.getElementById("fjts").disabled=false;
    }
    else
    {
        document.getElementById("fjts").disabled=true;
        document.getElementById("fjts").value="";
    }
}
$(function(){
	var DocIDs = $("#DocIDs").val();
	var siteID = $("#siteID").val();
	$.ajax({
		url:"../papercard/selectPaper.do",
		type:'get',
		data:{"DocIDs":DocIDs,"siteID":siteID,"type":"1"},
		dataType:"json",
		cache:false,
		async:false,
		success:function(data){
			$("#setMealContent").empty();
			for(var i=0;i<data.length;i++){
				$("#setMealContent").append('<div class="pull-left" style="height:30px; margin-right:10px;"><input style="margin-right:3px;margin-top:3px;" class="pull-left" type="checkbox" name="setMealContent" value="'+data[i].pa_name+'">'+data[i].pa_name + '</div>');
			} 
		},
		complete: function(XMLHttpRequest, textStatus){
	        this;  // 调用本次AJAX请求时传递的options参数
	    },
	    error: function (XMLHttpRequest, textStatus, errorThrown) {
	    	alert("error status : " + XMLHttpRequest.status+","+textStatus+","+errorThrown);
	    }
	});
});

setInterval( "remainTime() " , 500 );
 var setMealContent="";
function remainTime(){  

 if($("#setMealContent").val()!=setMealContent){
	setMealContent=$("#setMealContent").val();
 	 $("#cen").html("");
 	 if($("#setMealContent").val()!=""){
 	 var vals=$("#setMealContent").val().split(",")
 	 for ( var int = 0; int < vals.length; int++) {
		$("#cen").html($("#cen").html()+"<input type='button' value='"+vals[int]+"'  onclick='rem(this)' name='bz'value style='margin-top:10px;border-radius: 0px;border: 1px solid  #cccccc;height:40px;width:100px;color:#000;margin-right: 10px;background-color:#F0F0F0' /> ")
	}
	}
 }
 
}
function rem(im){
if($("#setMealContent").val().indexOf(","+$(im).val())>-1){
$("#setMealContent").val($("#setMealContent").val().replace(","+$(im).val(), ""))
}
if($("#setMealContent").val().indexOf($(im).val()+",")>-1){
$("#setMealContent").val($("#setMealContent").val().replace($(im).val()+",", ""))
}
if($("#setMealContent").val().indexOf($(im).val())>-1){
$("#setMealContent").val($("#setMealContent").val().replace($(im).val(), ""))
}
$(im).remove();
}



	$(".defalttest").focus(function() {
		if (($(this).css("color")) == "rgb(204, 204, 204)") {
			$(this).css("color", "#000");
			$(this).val("");
		}

	});

	
	function kong(ind,is) {
		if (ind == 1) {
			$("#expiryDate").attr("readonly", "readonly");
			$("#EffectTime").attr("onclick",
					"showCalendar('EffectTime', 'yyyy-mm-dd');");
			$("#ExpireTime").attr("onclick",
					"showCalendar('ExpireTime', 'yyyy-mm-dd');");
			$("#expiryDate").val("");
		}

		if (ind == 2) {
			$("#expiryDate").removeAttr("readonly");
			$("#EffectTime").attr("onclick", "");
			$("#ExpireTime").attr("onclick", "");
			$("#EffectTime").val("");
			$("#ExpireTime").val("");
		}
		

	}
	
	

	$("#fjts").blur(function(){
	
	val=$(this).val();
	
		var n = Number(val);
		if (!isNaN(n))
		{
		    $("#weizhi").val(val);
		}else{
			alert("请输入数字")
		}
	
	
	})
	function save(str){

var setMealContent=$("#setMealContent").val();
var paperNumber=0;
$("input[name=bz]").each(function(){
paperNumber+=1
})
$("#paperNumber").val(paperNumber);
	var setMealNmae=$("#setMealNmae").val()
	var setMealMoney=$("#setMealMoney").val()
	var EffectTime=$("#EffectTime").val(); 
	var ExpireTime=$("#ExpireTime").val(); 
	var expiryDate=$("#expiryDate").val()
	if(setMealNmae=="请输入套餐名称"||setMealNmae==""){
	alert("请输入套餐名称");
	return ;
	}
	
	/* if(setMealContent==""){
	alert("请添加报纸");
	return ;
	} */
	
	var int=0;
	$('input[name="setMealContent"]:checked').each(function(){int++;}); 
	if(int==0){alert("请选择报纸");return;}
	
	
	if(setMealMoney=="￥0.00"||setMealMoney==""){
		   alert("请输入套餐金额");
	return; 
	}else{
	var n = Number(setMealMoney);
		if (isNaN(n))
		{
	alert("套餐金额输入错误");
		   return ;
		}
	}
	if($("#yxq").attr("checked")=="checked"){
		if(EffectTime==""){
		 alert("请输入固定日期");
		   return ;
		}
		if(ExpireTime==""){
		 alert("请输入固定日期");
		   return ;
		}
		if(new Date(EffectTime)>new Date(ExpireTime)){
		
		 alert("固定日期输入错误");
		  return ;
		}
		$("#expiryDate").val("")
	}else{
	
	if(expiryDate=="请输入天数"||expiryDate==""){
	alert("请输入连续自然日");
	return ;
	}else{
	
		if (isNaN( Number(expiryDate)))
		{
		   alert("连续自然日输入错误");
		   return 
		}
	}
	}
	var int=0;
	$('input[name="Remind"]:checked').each(function(){int++;}); 
	if(int==0){alert("请选择过期提醒时间");return;}
	
	if($("#weizhi").attr("checked")=="checked"){
	
	if($("#weizhi").val()==""){
		alert("请填写自定义提醒时间");
		return;
		}
	}
	if(str==0){
		$("#flag").val("0");
	} else {
		$("#flag").val("1");
	}
	$("#sub").submit()
	//if(str==1){
		//alert(str)
		//setTimeout(function(){location.reload();},2000);
	//}
	} 
	
	function clos(){

		if($("#HASCHANGE").val()=="1"){
			document.getElementById("iframe").contentWindow.location.href = "../../e5workspace/after.do?UUID=" + $("#UUID").val() + "&DocIDs=1";
		} else {
			document.getElementById("iframe").contentWindow.location.href = "../../e5workspace/after.do?UUID=" + $("#UUID").val();
		}
	
	}
</script>
