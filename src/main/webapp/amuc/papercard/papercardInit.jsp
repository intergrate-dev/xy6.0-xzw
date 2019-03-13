<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>报卡表单</title>
	<meta content="IE=edge" http-equiv="X-UA-Compatible" />
	<link type="text/css" rel="stylesheet" href="../../e5style/reset.css"/>
	<link type="text/css" rel="stylesheet" href="../../e5script/jquery/jQuery-Validation-Engine/css/validationEngine.jquery.css"/>
	<link type="text/css" rel="stylesheet" href="../../e5script/jquery/dialog.style.css"/>
	<link type="text/css" rel="stylesheet" href="../../e5script/jquery/jquery-autocomplete/jquery.autocomplete.css"/>
	<link type="text/css" rel="stylesheet" href="../../e5script/lhgcalendar/lhgcalendar.bootstrap.css">
	<link type="text/css" rel="stylesheet" href="../../e5style/e5form-custom.css"/>
	
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script> 
	<script type="text/javascript" src="../../e5script/jquery/jQuery-Validation-Engine/js/languages/jquery.validationEngine-zh_CN.js"></script>
	<script type="text/javascript" src="../../e5script/jquery/jQuery-Validation-Engine/js/jquery.validationEngine.js"></script>
	<script type="text/javascript" src="../../e5script/jquery/jquery.dialog.js"></script> 
	<script type="text/javascript" src="../../e5script/jquery/jquery-autocomplete/jquery.autocomplete.min.js"></script> 
	<script type="text/javascript" src="../../e5script/calendar/usecalendar.js"></script>
	<script type="text/javascript" src="../../e5script/lhgcalendar/lhgcalendar.js"></script>
	<script type="text/javascript" src="../../e5workspace/script/form-custom.js"></script> 
	<style type="text/css">
		.formError{margin-top: 88px;}
		.tablecontent td{
			border-bottom: none;
			font-size: 13px;
		}
		#pcTotal{
			
		}
		.border{
			border:1px solid #ddd !important;
			border-radius: 0 !important;
			height: 20px !important;
		}
		#pcPaperName{
			margin-top: 10px;
    		float: left;
		}
		.pull-left{
			float: left;
		}
		.mt3{
			margin-top: 3px !important;
		}
		.mr3{
			margin-right: 3px !important;
		}
		.mr5{
			margin-right: 5px;
		}
		.mr10{
			margin-right: 10px;
		}
		#pcPaperName input:first-child{
			margin-left: -5px;
		}
		.mt13{
			margin-top: 13px;
		}
		.mt8{
			margin-top: 8px;
		}
		#btnFormCancel{
			height: 30px;
		    background: #b1b1b1;
		    border: none;
		    color: #fff;
		    border-radius: 3px;
		    padding: 0 27px;
		    text-shadow: none;
		}
		#btnFormSave{
			background-color: #1bb8fa !important;
		    color: #FFFFFF;
		    width: 100px !important;
		    height: 30px;
		    border-radius: 3px;
		    border: none;
		    margin-right: 10px;
		    margin-left: 96px;
		}
		input.custform-input, input.custform-input-address, input.custform-input-date{
			border-radius: 0;
			border:1px solid #ddd;
			height: 20px;
		}
		.mr20{
			margin-right: 20px;
		}
	</style>
	<script type="text/javascript">
		$(function(){
			var DocIDs = $("#DocID").val();
			var siteID = $("#siteID").val();
			$.ajax({
				url:"../papercard/selectPaper.do",
				type:'get',
				data:{"DocIDs":DocIDs,"siteID":siteID,"type":"0"},
				dataType:"json",
				cache:false,
				async:false,
				success:function(data){
					$("#pcPaperName").empty();
					for(var i=0;i<data.length;i++){
						$("#pcPaperName").append('<div class="pull-left mr20"><input class="pull-left mr3 mt3" type="checkbox" name="pcPaperName" value="'+data[i].pa_name+'">'+ '<span>' + data[i].pa_name + '</span></div>');
					} 
				},
				complete: function(XMLHttpRequest, textStatus){
			        this;  // 调用本次AJAX请求时传递的options参数
			    },
			    error: function (XMLHttpRequest, textStatus, errorThrown) {
			    	alert("error status : " + XMLHttpRequest.status+","+textStatus+","+errorThrown);
			    }
			});
			
			//点击保存按钮
			$("#btnFormSave").click(function(){
			
				var pcPaperName="";
				var paperNumber=0;
				$("input[name=bz]").each(function(){
					if(this.value!=''){
						pcPaperName+=this.value+",";
						paperNumber+=1;
					}
				})
			
				$("#paperNumber").val(paperNumber);
				$("#pcPaperName").val(pcPaperName);

            
                //alert(pcPaperName);
				/* if(pcPaperName==""){
					alert("请添加报纸");
					return ;
				} */
				
				var int=0;
				$('input[name="pcPaperName"]:checked').each(function(){int++;}); 
				if(int==0){alert("请选择报纸");return;}
				
				var areaLen = $("#pcArea").val().length;
				
				var pcEffectTime=$("#pcEffectTime").val(); 
				var pcExpireTime=$("#pcExpireTime").val(); 
				var pcExpiryDate=$("#pcExpiryDate").val();
				var pcMoney=$("#pcMoney").val();
				
				if($("#yxq").attr("checked")=="checked"){
					if(pcEffectTime==""){
					 alert("请输入固定日期");
					   return ;
					}
					if(pcExpireTime==""){
					 alert("请输入固定日期");
					   return ;
					}
					if(new Date(pcEffectTime)>new Date(pcExpireTime)){
					
					 alert("固定日期输入错误");
					  return ;
					}
					$("#pcExpiryDate").val("")
				}else{
				
				if(pcExpiryDate=="请输入天数"||pcExpiryDate==""){
				alert("请输入连续自然日");
				return ;
				}else{
					if (Number(pcExpiryDate) <= 0)
					{
					   alert("连续自然日应大于1");
					   return 
					}
					if (isNaN( Number(pcExpiryDate)))
					{
					   alert("连续自然日输入错误");
					   return 
					}
				}
				}
				
				if(pcMoney=="￥0.00"||pcMoney==""){
					   alert("请输入报卡金额");
					   return ;
				return; 
				}else{
				var n = Number(pcMoney);
					if (isNaN(n))
					{
				alert("报卡金额请输错误");
					   return ;
					}
				}
				
				if(areaLen!=4){
					$("#pcArea").removeClass();
					$("#pcArea").addClass("custform-input validate[required],funcCall[foo]]")
				}
				
				var int=0;
				$('input[name="pcRemind"]:checked').each(function(){int++;}); 
				if(int==0){alert("请选择过期提醒时间");return;}
				
				var vreadway=$("input[name='pcReadway']:checked").val();
				if(vreadway==undefined || vreadway==null || vreadway==''){alert("阅读渠道未选");return;}
				
				if($("#weizhi").attr("checked")=="checked"){
				
				if($("#weizhi").val()==""){
					alert("请填写自定义提醒时间");
					return ;
					}
				if(Number($("#weizhi").val())<=0){
					alert("请填写正确自定义提醒时间");
					return ;
					}
				}
				if(checkLevel()){
					$("#form").submit();
				}
				
			})
			
			//时间失去焦点
			$("#pcEffectTime").blur(function(){
				$("#pcEffectTime").removeClass();
				$("#pcEffectTime").addClass("custform-input validate[custom[dateFormat]]");
			})
			$("#pcExpireTime").blur(function(){
				$("#pcExpireTime").removeClass();
				$("#pcExpireTime").addClass("custform-input validate[custom[dateFormat]]");
			})
			 $("#pcArea").blur(function(){
				var aa = $(this).val().length;
				 var reg=/^[A-Z]{2}[0-9]{2}$/;
				 if (!reg.test($(this).val())){
					 $(this).removeClass();
					$(this).addClass("custform-input validate[required],funcCall[foo]]")
				 }else{
					 $(this).removeClass();
					$(this).addClass("custform-input validate[required]]")
				 }
			}) 
			$("#pcTypeCode").blur(function(){
				var aa = $(this).val().length;
				 var reg=/^[A-Z]{2}[0-9]{2}$/;
				 if (!reg.test($(this).val())){
					 $(this).removeClass();
					$(this).addClass("custform-input validate[required],funcCall[foo]]")
				 }else{
					 $(this).removeClass();
					$(this).addClass("custform-input validate[required]]")
				 }
			})
			
		})
		
	//检验区域代码
	function foo(){
		return "请输入正确四位代码!";
	}
	//检查是否报刊种类
	function checkLevel(){
		var flag = true;
		if($(":checkbox[checked='checked']").length == 0){
			alert("请选择报刊种类!");
			return false;
		}
		
		//验证是否选择时长
		if($("#pcMultiple").val()==0){
			alert("请选择报卡时效!");
			return false;
		}
		return flag;
	}
	
	function kong(ind,is) {
		if (ind == 1) {
			$("#pcExpiryDate").attr("readonly", "readonly");
			$("#pcEffectTime").attr("onclick","selectDate('pcEffectTime')");
			$("#pcExpireTime").attr("onclick","selectDate('pcExpireTime')");
			$("#pcExpiryDate").val("");
		}

		if (ind == 2) {
			$("#pcExpiryDate").removeAttr("readonly");
			$("#pcEffectTime").attr("onclick", "");
			$("#pcExpireTime").attr("onclick", "");
			$("#pcEffectTime").val("");
			$("#pcExpireTime").val("");
		}
		

	}
		
	</script>
	
</head>
<body>
	<iframe id="iframe" name="iframe" style="display:none;"></iframe>
	<form id="form" method="post" action="CreatePaperCode.do">
		<input type="hidden" id="DocLibID" name="DocLibID" value="<%=request.getParameter("DocLibID")%>"/>
		<input type="hidden" id="DocID" name="DocID" value="<%=request.getParameter("DocIDs")%>"/>
		<input type="hidden" id="FVID" name="FVID" value="<%=request.getParameter("FVID")%>"/>
		<input type="hidden" id="UUID" name="UUID" value="<%=request.getParameter("UUID")%>"/>
		<input type="hidden" id="siteID" name="siteID" value="<%=request.getParameter("siteID")%>"/>
		
		<input type="hidden" id="SaveSubmit" name="SaveSubmit" value=""/>
		<div class="mainBodyWrap">
			<table class="tablecontent">
			

<tbody customwidth="794" customheight="289"> 
  <tr>
    <td class="ui-droppable" style="width: 792px;">
      <span id="SPAN_pcTotal" class="custform-span"> 
        <label id="LABEL_pcTotal" class="custform-label custform-label-require">报卡数量</label>  
        <div class="custform-from-wrap" id="DIV_pcTotal"> 
          <input type="text" id="pcTotal" name="pcTotal" value="" class="border custform-input validate[custom[integer],min[1],required]" style="width:133px;" onBlur="$('#pcMoney1').val($('#pcTotal').val());"/>  
          <span class="custform-postfix">
            <span class="custform-aftertxt ui-draggable"/>
          </span> 
        </div> 
      </span>
    </td>
  </tr> 
  <tr>
    <td class="ui-droppable" style="width: 792px;">
      <span id="SPAN_pcPaperName" class="custform-span"> 
        <label id="LABEL_pcPaperName" class="custform-label custform-label-require">选择报纸</label> 
        <div class="custform-from-wrap" id="pcPaperName"> 
          <!-- <input id="" value="1" name="pcPaperName" type="checkbox">1天前</input>  -->
        </div> 
      </span>
    </td>
  </tr>
  <tr>
    <td class="ui-droppable">
      <span class="custform-aftertxt ui-draggable"/>
      <span id="SPAN_pcEffectTime" class="custform-span"> 
        <label id="LABEL_pcEffectTime" class="custform-label custform-label-require">生效时间</label>  
        <div class="custform-from-wrap" id="DIV_pcEffectTime"> 
          <input class="pull-left mt13" id="yxq" value="" name="time" type="radio" onclick="kong(1)" checked >
            <label>固定日期</label>
            <input  type="text" id="pcEffectTime" name="pcEffectTime" value="" readonly="true" class="border custform-input" style="width:133px;" onclick="selectDate('pcEffectTime')"/>  
            <label>--</label>
            <input  type="text" id="pcExpireTime" name="pcExpireTime" value="" readonly="true" class="border custform-input" style="width:133px;" onclick="selectDate('pcExpireTime')"/>
          </input>
          <span class="custform-postfix">
            <span class="custform-aftertxt ui-draggable"/>
          </span> 
        </div> 
        <div class="custform-from-wrap" id="DIV_pcEffectTime"> 
          <input class="pull-left mt13" id="" value="" name="time" onclick="kong(2)" type="radio">
            <label>连续自然日</label>
            <input type="text" id="pcExpiryDate" name="pcExpiryDate" value="请输入天数" readonly="true" class="border custform-input" style="width:133px;color:#999;" onFocus="if(value==defaultValue){value='';this.style.color='#000'}" onBlur="if(!value){value=defaultValue; this.style.color='#999'}"/>  
            <span class="custform-postfix" style="color: #999; display: block; margin-left: 7px;">购买之日起计算所设天数
            </span>
          </input>     
          <span class="custform-postfix">
            <span class="custform-aftertxt ui-draggable"/>
          </span> 
        </div>
      </span>
    </td>
  </tr>  
   <tr>
    <td class="ui-droppable" style="width: 792px;">
      <span id="SPAN_pcArea" class="custform-span"> 
        <label id="LABEL_pcArea" class="custform-label custform-label-require">报卡生成规则</label> 
        <div class="custform-from-wrap" id="DIV_pcArea"> 
          <input type="text" id="pcTypeCode" name="pcTypeCode" value="报纸种类代码" class="border custform-input validate[required]" style="width:75px;color:#999; font-size: 12px;" onFocus="if(value==defaultValue){value='';this.style.color='#000'}" onBlur="if(!value){value=defaultValue; this.style.color='#999'}" />
          <input type="text" id="pcArea" name="pcArea" value="报纸地区代码" class="border custform-input validate[required]" style="border-radius: 0; border: 1px solid #ddd; width:75px;color:#999; font-size: 12px;" onFocus="if(value==defaultValue){value='';this.style.color='#000'}" onBlur="if(!value){value=defaultValue; this.style.color='#999'}" />  
          <span class="custform-postfix" style="color: #999; display: block; margin-left: 5px;">两位大写字母+两位数字，如代码为DQ01
            <span class="custform-aftertxt ui-draggable"/>
          </span> 
        </div> 
      </span>
    </td>
  </tr>   
  <tr>
    <td class="ui-droppable" style="width: 792px;">
      <span id="SPAN_pcArea" class="custform-span"> 
        <label id="LABEL_pcArea" class="custform-label custform-label-require">报卡金额</label> 
        <div class="custform-from-wrap" id="DIV_pcArea"> 
          <input type="text" id="pcMoney" name="pcMoney" value="￥0.00" class="custform-input" style="width:75px;color:#999;" onFocus="if(value==defaultValue){value='';this.style.color='#000'}" onBlur="if(!value){value=defaultValue; this.style.color='#999'}$('#pcTotalMoney').val($('#pcMoney').val()*$('#pcMoney1').val());val=$(this).val();var n = Number(val);if (isNaN(n)){alert('请输入数字')}" />
          <label>X</label>
          <input class="border" type="text" id="pcMoney1" name="" value="" style="width:75px;color:#999;" readonly="readonly"/>  
          <label>=</label>
          <input class="border" type="text" id="pcTotalMoney" name="pcTotalMoney" value="￥0.00" style="width:75px;color:#999;" readonly="readonly"/>  
          <div class="custform-postfix" style="color: gray; display: block; margin-left: 5px;">请输入报卡单价
            <span class="custform-aftertxt ui-draggable"/>
          </div> 
        </div> 
      </span>
    </td>
  </tr>
  <tr>
    <td class="ui-droppable" style="width: 792px;">
      <span id="SPAN_pcArea" class="custform-span"> 
        <label id="LABEL_pcArea" class="custform-label custform-label-require">阅读渠道</label> 
        <div class="custform-from-wrap" id="DIV_pcArea">
          <div class="pull-left mr10 mt8">
          	<input class="pull-left mr3" id="" value="0" name="pcReadway" type="radio" />
          	<span>全部渠道</span>
          </div>	
          <div class="pull-left mr10 mt8">
         	 <input class="pull-left mr3" id="" value="1" name="pcReadway" type="radio" />
         	 <span>PC</span>
          </div>	
          <div class="pull-left mr10 mt8">
          	<input class="pull-left mr3" id="" value="2" name="pcReadway" type="radio" />
          	<span>移动</span>
          </div>	
        </div> 
      </span>
    </td>
  </tr>
  <tr style="display:none;"><!-- 隐藏过期提醒 -->
    <td class="ui-droppable" style="width: 792px;">
      <span id="SPAN_pcArea" class="custform-span"> 
        <label id="LABEL_pcArea" class="custform-label custform-label-require">过期提醒</label> 
        <div class="custform-from-wrap" id="DIV_pcArea"> 
          <div class="pull-left mr10 mt8" style="margin-left: 5px;">	
          	<input class="pull-left mr3" id="" value="0" name="pcRemind" type="checkbox" checked>
          	<span>0天前 </span>
          </div>
          <div class="pull-left mr10 mt8">	
          	<input class="pull-left mr3" id="" value="7" name="pcRemind" type="checkbox">
          	<span>7天前</span>
          </div>
          <div  class="pull-left mr10 mt8">
          	<input class="pull-left mr3" id="" value="14" name="pcRemind" type="checkbox">
          	<span>14天前</span>
          </div>
         <div  class="pull-left mr10 mt8">
         	<input class="pull-left mr3" id="" value="30" name="pcRemind" type="checkbox">
          	<span>30天前</span>
         </div>
         <div  class="pull-left mr10 mt8">
         	<input class="pull-left mr3"  id="weizhi" value="" name="pcRemind" type="checkbox"  onclick="setText(this)">
          	<input style="width: 120px; margin-top: -10px;" class="border" id="fjts" value="请输入天数"  disabled="true" name="" type="text" style="width:66px;color:#999;" onFocus="if(value==defaultValue){value='';this.style.color='#000'}" onBlur="if(!value){value=defaultValue; this.style.color='#999'};val=$(this).val();var n = Number(val);if (!isNaN(n)){$('#weizhi').val(val);}else{alert('请输入数字')}"></input></input>
         </div>
          
        </div> 
      </span>
    </td>
  </tr>
  <tr>
    <td class="ui-droppable" style="width: 792px;">
      <span id="SPAN_pcArea" class="custform-span"> 
        <label id="LABEL_pcArea" class="custform-label custform-label-require">支付方式</label> 
        <div class="custform-from-wrap" id="DIV_pcArea"> 
          <div  class="pull-left mr10 mt8">	
          	<input id="" value="免费" name="pcPay" type="radio" checked />免费
          </div>
          <div  class="pull-left mr10 mt8">	
          	<input id="" value="银行转账(全款)" name="pcPay" type="radio">银行转账(全款)
          </div>
          <div  class="pull-left mr10 mt8">	
          	<input id="" value="现金(全款)" name="pcPay" type="radio">现金(全款)
          </div>
        </div> 
      </span>
    </td>
  </tr>     
  <tr>
    <td class="ui-droppable">
      <span class="ui-draggable" id="txtFormSave" fieldtype="-1" fieldcode="insertsave">
        <input class="button btn" id="btnFormSave" value="生成报卡号" type="button"/>
      </span>
      <span class="custform-aftertxt ui-draggable"/>
      <span class="ui-draggable" id="txtFormCancel" fieldtype="-3" fieldcode="insertcancel">
        <input class="button btn" id="btnFormCancel" value="取消" type="button"/>
      </span>
      <span class="custform-aftertxt ui-draggable"/> 
    </td>
  </tr>  
  <tr style="display:none">
    <td>
      <div id="hiddenDiv"> 
        <ul id="tablehide-ul" class="ui-droppable">
          <li>
            <span id="SPAN_pcOperator" class="custform-span"> 
              <label id="LABEL_pcOperator" class="custform-label ">操作人</label>  
              <div class="custform-from-wrap" id="DIV_pcOperator"> 
                <input type="text" id="pcOperator" name="pcOperator" value="${sessionScope.sysUser.userName}" class="border custform-input validate[maxSize[255]]" style="width:133px;"/>  
                <input type="text" id="pcOperatorID" name="pcOperatorID" value="${sessionScope.sysUser.userID}" class="border custform-input validate[maxSize[255]]" style="width:133px;"/>  
                <span class="custform-postfix">
                  <span class="custform-aftertxt"/>
                </span> 
              </div> 
            </span>
          </li> 
        </ul> 
      </div>
    </td>
  </tr>
</tbody>

			</table>
		</div>
	</form>
</body>
</html>
<script type="text/javascript">
setInterval( "remainTime() " , 1000 );
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
function remainTime(){  
  $("input[name=bz]").each(function(){
  if($(this).val().indexOf(",")>0){
 	 $("#cen").html("");
 	 var vals=$(this).val().split(",")
 	 for ( var int = 0; int < vals.length; int++) {
		$("#cen").html($("#cen").html()+"<input type='button' value='"+vals[int]+"'  name='bz'value style='margin-bottom:10px;margin-top:10px;border-radius: 0px;border: 1px solid  #cccccc;height:40px;width:100px;color:#000;margin-right: 10px;vertical-align: middle;'onclick='$(this).remove()' /> ")
	}
		return;
	  }
  })
    $("input[name=bz]").each(function(){
	    var d=this;
		var i=0;
		$("input[name=bz]").each(function(){
			if($(this).val()== $(d).val()){
				i+=1;
				if(i>1){ $(this).remove() }
	 		}
		 })
	 })
    
}
$("#tjbz").click(function(){
	//alert(12) 
	if($("#mRegion").val()!="undefined" && $("#mRegion").val()!=''){
		$("#mRegion").attr("id","");
	}
	if($("#mRegion").val()==''){
		//什么也不做,不增加格子，以应对点取消的状态
	}else{
		$("#cen").html($("#cen").html()+"<input type='button' id='mRegion' name='bz' style='border-radius: 0px;border: 1px solid  #cccccc;height:40px;width:100px;color:#000;margin-right: 10px;vertical-align: middle;'onclick='$(this).remove()' /> ")
	}
	selectCat('mRegion', 'mRegionID', '16', true);
	
	});

</script>
