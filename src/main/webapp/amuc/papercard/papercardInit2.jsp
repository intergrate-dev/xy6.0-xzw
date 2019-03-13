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
	<script type="text/javascript">
		$(function(){
			$("input[type='checkbox']").click(function(){
				var paperName = "";
				var paperMark = "";
				$("input[type='checkbox']").each(function(){
					//alert($(this).next().text());
					if($(this).attr("checked")){
						paperName+=","+$(this).next().text();
						paperMark+=","+$(this).val();
					}
				})
				paperName = paperName.substring(1);
				paperMark = paperMark.substring(1);
				//alert(paperName);
				$("#pcPaperName").val(paperName);
				$("#pcPaperMark").val(paperMark);
			})
			
			//点击保存按钮
			$("#btnFormSave").click(function(){
				if(checkLevel()){
					$("#form").submit();
				}
				var pcEffectTime = $("#pcEffectTime").val();
				//var pcExpireTime = $("#pcExpireTime").val();
				var areaLen = $("#pcArea").val().length;
				
				if(pcEffectTime==""){
					$("#pcEffectTime").removeClass();
					$("#pcEffectTime").addClass("custform-input validate[required,custom[dateFormat]]");
				}
				/* if(pcExpireTime==""){
					$("#pcExpireTime").removeClass();
					$("#pcExpireTime").addClass("custform-input validate[required,custom[dateFormat]]");
				} */
				if(areaLen!=4){
					$("#pcArea").removeClass();
					$("#pcArea").addClass("custform-input validate[required],funcCall[foo]]")
				}
				
			})
			
			//时间失去焦点
			$("#pcEffectTime").blur(function(){
				$("#pcEffectTime").removeClass();
				$("#pcEffectTime").addClass("custform-input validate[custom[dateFormat]]");
			})
			/* $("#pcExpireTime").blur(function(){
				$("#pcExpireTime").removeClass();
				$("#pcExpireTime").addClass("custform-input validate[custom[dateFormat]]");
			}) */
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
				/* if(aa!=4 && aa!=0){
					$(this).removeClass();
					$(this).addClass("custform-input validate[required],funcCall[foo]]")
				}else{
					$(this).removeClass();
					$(this).addClass("custform-input validate[required]]")
				} */
			}) 
			
		})
		
	//检验区域代码
	function foo(){
		return "请输入正确四位区域代码!";
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
	
	</script>
	<style type="text/css">
	.formError{margin-top: 88px;}
	</style>
</head>
<body>
	<iframe id="iframe" name="iframe" style="display:none;"></iframe>
	<form id="form" method="post" action="/amuc/amuc/papercard/CreatePaperCode.do?a=create">
		<input type="hidden" id="DocLibID" name="DocLibID" value="<%=request.getParameter("DocLibID")%>"/>
		<input type="hidden" id="DocID" name="DocID" value="<%=request.getParameter("DocIDs")%>"/>
		<input type="hidden" id="FVID" name="FVID" value="<%=request.getParameter("FVID")%>"/>
		<input type="hidden" id="UUID" name="UUID" value="<%=request.getParameter("UUID")%>"/>
		
		<input type="hidden" id="SaveSubmit" name="SaveSubmit" value=""/>
		<div class="mainBodyWrap">
			<table class="tablecontent">
			

<tbody customwidth="794" customheight="289">
  <!-- <tr>
    <td class="ui-droppable" style="width: 792px;">
      <span id="SPAN_pcYear" class="custform-span"> 
        <label id="LABEL_pcYear" class="custform-label custform-label-require">年份</label>  
        <div class="custform-from-wrap" id="DIV_pcYear"> 
          <input type="text" id="pcYear" name="pcYear" value="" class="custform-input validate[maxSize[255],required]" style="width:300px;"/>  
          <span class="custform-postfix">
            <span class="custform-aftertxt ui-draggable"/>
          </span> 
        </div> 
      </span>
    </td> -->
  </tr>  
  <tr>
    <td class="ui-droppable">
      <span class="custform-aftertxt ui-draggable"/>
      <span class="custform-aftertxt ui-draggable"/>
      <span id="SPAN_pcPaperMark" class="custform-span"> 
        <label id="LABEL_pcPaperMark" class="custform-label ">报刊标识</label>  
          <input type="hidden" name="pcPaperName" id="pcPaperName" oldValue="-"/>  
        <div class="custform-from-wrap" id="DIV_pcPaperMark"> 
          <input type="hidden" check="true" name="pcPaperMark" id="pcPaperMark" oldValue="-"/>  
          <input type="checkbox" name="pcPaperMark" id="pcPaperMark_0" value="1"/>
          <label for="pcPaperMark_0">中国石油报</label>
          <input type="checkbox" name="pcPaperMark" id="pcPaperMark_1" value="2"/>
          <label for="pcPaperMark_1">金秋周刊</label>
          <input type="checkbox" name="pcPaperMark" id="pcPaperMark_2" value="3"/>
          <label for="pcPaperMark_2">石油商报</label>
          <input type="checkbox" name="pcPaperMark" id="pcPaperMark_3" value="4"/>
          <label for="pcPaperMark_3">汽车生活报</label> 
        </div> 
      </span>
    </td>
  </tr>  
   <tr>
    <td class="ui-droppable" style="width: 792px;">
      <span id="SPAN_pcArea" class="custform-span"> 
        <label id="LABEL_pcArea" class="custform-label custform-label-require">区域代码</label>  
        <div class="custform-from-wrap" id="DIV_pcArea"> 
          <input type="text" id="pcArea" name="pcArea" value="" class="custform-input validate[required]" style="width:133px;"/>  
          <span class="custform-postfix" style="color: gray;"><font style="color: red;">*</font>两位大写字母+两位数字，如大庆油田代码为DQ01
            <span class="custform-aftertxt ui-draggable"/>
          </span> 
        </div> 
      </span>
    </td>
  </tr>   
  <tr>
    <td class="ui-droppable">
      <span class="custform-aftertxt ui-draggable"/>
      <span id="SPAN_pcEffectTime" class="custform-span"> 
        <label id="LABEL_pcEffectTime" class="custform-label ">生效时间</label>  
        <div class="custform-from-wrap" id="DIV_pcEffectTime"> 
          <input type="text" id="pcEffectTime" name="pcEffectTime" value="" class="custform-input validate[required,custom[dateFormat]]" style="width:133px;" onclick="selectDate('pcEffectTime')"/>  
          <span class="custform-postfix">
            <span class="custform-aftertxt ui-draggable"/>
          </span> 
        </div> 
      </span>
    </td>
  </tr>  
  <tr>
    <td class="ui-droppable">
      <!-- <span class="custform-aftertxt ui-draggable"/>
      <span id="SPAN_pcExpireTime" class="custform-span"> 
        <label id="LABEL_pcExpireTime" class="custform-label ">失效时间</label>  
        <div class="custform-from-wrap" id="DIV_pcExpireTime"> 
          <input type="text" id="pcExpireTime" name="pcExpireTime" value="" class="custform-input validate[required,custom[dateFormat]]" style="width:133px;" onclick="selectDate('pcExpireTime')"/>  
          <span class="custform-postfix">
            <span class="custform-aftertxt ui-draggable"/>
          </span> 
        </div> 
      </span> -->
      <span id="SPAN_mSex" class="custform-span"> 
        <label id="LABEL_mSex" class="custform-label ">报卡时效</label>  
        <div class="custform-from-wrap" id="DIV_mSex"> 
          <select id="pcMultiple" name="pcMultiple" oldValue="-" class="custform-select validate[required]" style="width:133px;">
            <option value="0">请选择订阅时长</option>
            <option value="1">半年</option>
            <option value="2">一年</option>
            <option value="3">一年半</option>
            <option value="4">两年</option>
            <option value="5">两年半</option>
            <option value="6">三年</option>
            <option value="7">三年半</option>
            <option value="8">四年</option>
            <option value="9">四年半</option>
          </select>  
          <span class="custform-postfix"/> 
        </div> 
      </span>
    </td>
  </tr> 
   <tr>
    <td class="ui-droppable" style="width: 792px;">
      <span id="SPAN_pcTotal" class="custform-span"> 
        <label id="LABEL_pcTotal" class="custform-label custform-label-require">生成数量</label>  
        <div class="custform-from-wrap" id="DIV_pcTotal"> 
          <input type="text" id="pcTotal" name="pcTotal" value="" class="custform-input validate[custom[integer],min[1],required]" style="width:133px;"/>  
          <span class="custform-postfix">
            <span class="custform-aftertxt ui-draggable"/>
          </span> 
        </div> 
      </span>
    </td>
  </tr>   
  <tr>
    <td class="ui-droppable">
      <span class="ui-draggable" id="txtFormSave" fieldtype="-1" fieldcode="insertsave">
        <input class="button btn" id="btnFormSave" value="保存" type="button"/>
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
                <input type="text" id="pcOperator" name="pcOperator" value="${sessionScope.sysUser.userName}" class="custform-input validate[maxSize[255]]" style="width:133px;"/>  
                <input type="text" id="pcOperatorID" name="pcOperatorID" value="${sessionScope.sysUser.userID}" class="custform-input validate[maxSize[255]]" style="width:133px;"/>  
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
