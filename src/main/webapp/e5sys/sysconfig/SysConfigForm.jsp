<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5sysconfig" changeResponseLocale="false"/>
<html>
<head><title><i18n:message key="sysconfig.form.title"/></title>
<script type="text/javascript" src="../org/js/xmlhttps.js"></script>
<script type="text/javascript" src="../../e5script/Function.js"></script>
<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script> 
<script type="text/javascript" src="../../e5script/jquery/jQuery-Validation-Engine/js/languages/jquery.validationEngine-zh_CN.js"></script>
<script type="text/javascript" src="../../e5script/jquery/jQuery-Validation-Engine/js/jquery.validationEngine.js"></script>
<script src="../../e5script/calendar/usecalendar.js"></script>
<script src="../../e5script/calendar/calendar.js"></script>
<link type="text/css" rel="styleSheet" href="../../e5style/reset.css"/>
<link type="text/css" rel="stylesheet" href="../../e5script/jquery/jQuery-Validation-Engine/css/validationEngine.jquery.css"/>
<link type="text/css" rel="stylesheet" href="../../e5script/calendar/calendar.css"/>
<link type="text/css" rel="styleSheet" href="../../e5style/sys-main-body-style.css"/>
<style type="text/css">
	.broadinput{
		width:360px;
	}
</style>
</head>
<body>
    <form name="sysconfigform" id="sysconfigform" action="SysConfigureMgrAction.do" method="post">
	<table cellpadding="0" cellspacing="0" class="table">
		<caption><i18n:message key="sysconfig.list.title.title"/></caption>
		<c:if test="${sysconfig.sysConfigID==0}">
		<input type="hidden" name="invoke" value="addConfigItem">
		<tr>
			<th width="20%"><span class="field-required">*</span><i18n:message key="sysconfig.form.project"/></th>
			<td width="80%" colspan='2'><input type="text" id="project" name="project" style="width:250px" class="validate[required,maxSize[30]]" value="<c:out value="${sysconfig.project}"/>"></td>
		</tr>
		<tr>
			<th><span class="field-required">*</span><i18n:message key="sysconfig.form.item"/></th>
			<td colspan='2'><input type="text" name="item" id="item" style="width:250px" class="validate[required,maxSize[60]]" value="<c:out value="${sysconfig.item}"/>"></td>
		</tr>
		<tr>
			<th><i18n:message key="sysconfig.form.itemType"/></th>
			<td id="colspan1" colspan="2">
				<select id="itemType" name="itemType" onchange="itemTypeChange();" style="width:260px">
					<option value="0"><i18n:message key="sysconfig.form.itemType.simple"/></option>
					<option value="1"><i18n:message key="sysconfig.form.itemType.url"/></option>
					<option value="2"><i18n:message key="sysconfig.form.itemType.enumerate"/></option>
					<option value="3"><i18n:message key="sysconfig.form.itemType.int"/></option>
					<option value="4"><i18n:message key="sysconfig.form.itemType.date"/></option>
				</select>
			</td>
		</tr>
		<tr id="dis1" style="display:none">
			<th><span class="field-required">*</span><i18n:message key="sysconfig.form.itemOptions"/></th>
			<td colspan='2'>
			<input type="text" id="itemOptions" name="itemOptions" style="width:250px" value="<c:out value="${sysconfig.itemOptions}"/>">
			<br/>
			<span style="color:red"><i18n:message key="sysconfig.form.itemOptionsInfo"/></span>
			</td>
		</tr>
		</c:if>
		<c:if test="${sysconfig.sysConfigID!=0}">
		<tr>
			<th width="20%"><i18n:message key="sysconfig.form.project"/></th>
			<td width="80%" colspan='2'><c:out value="${sysconfig.project}"/></td>
		</tr>
		<tr>
			<th><i18n:message key="sysconfig.form.item"/></th>
			<td colspan='2'><c:out value="${sysconfig.item}"/></td>
		</tr>
		<tr>
			<th><i18n:message key="sysconfig.form.itemType"/></th>
			<td id="colspan1" colspan="2">
				<c:if test="${sysconfig.itemType==0}"><i18n:message key="sysconfig.form.itemType.simple"/></c:if>
				<c:if test="${sysconfig.itemType==1}"><i18n:message key="sysconfig.form.itemType.url"/></c:if>
				<c:if test="${sysconfig.itemType==2}"><i18n:message key="sysconfig.form.itemType.enumerate"/></c:if>
				<c:if test="${sysconfig.itemType==3}"><i18n:message key="sysconfig.form.itemType.int"/></c:if>
				<c:if test="${sysconfig.itemType==4}"><i18n:message key="sysconfig.form.itemType.date"/></c:if>
			</td>
		</tr>
		<tr id="dis1" style="display:none">
			<th><i18n:message key="sysconfig.form.itemOptions"/></th>
			<td colspan='2'> </td>
		</tr>
		<input type="hidden" name="invoke" value="updateConfigItem">
		<input type="hidden" name="project" value="<c:out value="${sysconfig.project}"/>">
		<input type="hidden" name="item" value="<c:out value="${sysconfig.item}"/>">
		<input type="hidden" id="itemType" name="itemType" value="<c:out value="${sysconfig.itemType}"/>">
		<input type="hidden" id="itemOptions" name="itemOptions" value="<c:out value="${sysconfig.itemOptions}"/>">
		<c:if test="${sysconfig.itemType==2}"><input type="hidden" name="value" id="value" value="<c:out value="${sysconfig.value}"/>"></c:if>
		</c:if>

		<tr>
			<th><span class="field-required">*</span><i18n:message key="sysconfig.form.value"/></th>
			<td id="colspan3" <c:if test="${!(sysconfig.sysConfigID!=0&&sysconfig.itemType==1)}">colspan='2'</c:if>>			
				<input type="text" name="value" id="value" style="width:250px" class="
				<c:if test="${sysconfig.sysConfigID==0}">validate[required,maxSize[254]];</c:if>
				<c:if test="${sysconfig.sysConfigID!=0}">
					<c:if test="${sysconfig.itemType==0}">validate[required,maxSize[254]];</c:if>
					<c:if test="${sysconfig.itemType==1}">validate[required,maxSize[254]];</c:if>
					<c:if test="${sysconfig.itemType==2}">validate[required,maxSize[254]];</c:if>
					<c:if test="${sysconfig.itemType==3}">validate[required,maxSize[254],custom[integer]];</c:if>
					<c:if test="${sysconfig.itemType==4}">validate[required,maxSize[254],custom[date]];</c:if>
				</c:if>
				broadinput" value="<c:out value="${sysconfig.value}"/>"
				<c:if test="${sysconfig.sysConfigID!=0&&sysconfig.itemType==4}"> onClick="showCalendar('value', 'y-mm-dd')"</c:if>
				/>
			</td>
			<td id="dis3" style="display:<c:if test="${!(sysconfig.sysConfigID!=0&&sysconfig.itemType==1)}">none</c:if>">
				<input class="button" type="button" name="url" value="<i18n:message key="sysconfig.form.itemType.urlValidate"/>" onclick="urlValidate();"/>
			</td>
		</tr>
		<tr>
			<th><i18n:message key="sysconfig.form.note"/></th>
			<td colspan="2"><textarea  class="broadinput" id="note" name="note" style="height:50px;width:250px" class="validate[maxSize[254]];"><c:out value="${sysconfig.note}"/></textarea></td>
		</tr>
</table>
<input type="hidden" name="appID" value="<c:out value="${sysconfig.appID}"/>">
<input type="hidden" name="sysConfigID" value="<c:out value="${sysconfig.sysConfigID}"/>">
<input type="hidden" id="flag" name="flag" value="false"/>
<br/>
<center>
	<input class="button" type="submit" name="dosubmit" value="<i18n:message key="sysconfig.form.submit"/>">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	<input class="button" type="button" name="docancel" value="<i18n:message key="sysconfig.form.cancel"/>" onClick='window.parent.configItemFormClose();'>
</center>
</form>
<iframe name="forsubmit" src="" Style="Display: none"></iframe>
</body>
<script type="text/javascript">
$(document).ready(function(){
	assembleOptions();
	$("#sysconfigform").validationEngine({
		autoPositionUpdate:true,
		onValidationComplete:function(from,r){
			if(r){
				window.onbeforeunload=null;
				sysconfigform.dosubmit.disabled=true;
				sysconfigform.docancel.disabled=true;
				
				var itemType = document.getElementById("itemType").value;
				if(itemType == 1){
					submitUrlValidate();
					var flag = $("#flag").val();
					if(flag == "true"){
						from[0].submit();
					} else {
						alert("<i18n:message key="sysconfig.form.itemType.url.false.alert"/>");
						sysconfigform.dosubmit.disabled = false;
						sysconfigform.docancel.disabled = false;
					}
				} else {
					from[0].submit();
				}
			}
		}
	});
});
function submitUrlValidate(){
	var value = $("#value").val();
	$.ajax({url:"UrlValidate.do?invoke=urlValidate&value=" + value, async:false, success:function(data) {
         if(data != null){
             if(data.indexOf("urlValidateError") >= 0){
                 $("#flag").val("false");
             } else {
             	$("#flag").val("true");
             }
         }
	 }});
}

window.returnValue=0;
//参数类型发生变化时相应其他标签的改变
function itemTypeChange(){
	$("#sysconfigform").validationEngine("hideAll");
	$("#value").unbind("click");
	$("#value").val("");
	$("#itemOptions").removeAttr("class");
	$("#itemOptions").val("");
	var itemType = document.getElementById("itemType").value;
	if(itemType==0){//如果是简单类型，则只做非空判断
		$("#value").attr("class","validate[required,maxSize[254]];broadinput");
	}
	if(itemType==1){//如果为url类型时，增加验证url按钮，点击“验证url”，则对url进行连接验证
		$("#value").attr("class","validate[required,maxSize[254]];broadinput");
		$("#dis3").show();
		$("#colspan3").attr("colspan","1");
	}else{
		$("#dis3").hide();
		$("#colspan3").attr("colspan","2");
	}
	if(itemType==2){//参数类型是枚举值时，将枚举值串显示，否则隐藏
		$("#value").attr("class","validate[required,maxSize[254]];broadinput");
		$("#itemOptions").attr("class","validate[required,maxSize[254]];");
		$("#dis1").show();
		$("#colspan1").val("colspan", "1");
	}else{
		$("#dis1").hide();
		$("#colspan1").attr("colspan",2);
	}
	if(itemType==3){//如果为整数类型时，增加对value值是否整型验证
		$("#value").attr("class","validate[required,maxSize[254],custom[integer]];broadinput");
	}
	if(itemType==4){//如果为日期类型时，增加对value值是否正确日期格式的验证：日期格式 yyyy-MM-dd
		$("#value").click(function (){showCalendar('value', 'y-mm-dd')});
		$("#value").attr("class","validate[required,maxSize[254],custom[date]];broadinput");
	}
}
//url验证
function urlValidate(){
	var value = sysconfigform.value.value;
	if(value=="")
	{
		alert("<i18n:message key="sysconfig.form.value.alert"/>");
		return false;
	}else{
		$.ajax({url:"UrlValidate.do?invoke=urlValidate&value="+value, async:false, success:function(data) {
            if(data!=null){
                if(data.indexOf("urlValidateError")>=0){
                    alert("<i18n:message key="sysconfig.form.itemType.url.false.alert"/>");
                }else{
                	alert("<i18n:message key="sysconfig.form.itemType.url.true.alert"/>");
                }
            }
  		 }});
	}
}

//修改时如果是枚举类型，则需要将枚举值拆分为数组组装为下拉列表
function assembleOptions(){
	var itemType = $("#itemType").val();
	var value = $("#value").val();
	if(itemType==2){
		var itemOptions = $("#itemOptions").val();
		if(itemOptions!=null&&itemOptions!=""){
			var array = itemOptions.split(",");
			if(array!=null&&array.length>0){
				var htmlVal = '<select id="itemOptionsChoose" name="itemOptionsChoose" class="validate[required]" style="width:260px" onchange="setValue();">';
				htmlVal+='<option value="" style="width:100px"><i18n:message key="sysconfig.form.itemType.default"/></option>';
				for(var i=0;i<array.length;i++){
					htmlVal+='<option value="' + array[i]+'" style="width:100px" ';
					if(value == array[i]){
						htmlVal+= " selected";
					}
					htmlVal += '>' + array[i] + "</option>";
				}
				htmlVal+='</select>';
				$("#colspan3").html(htmlVal);
			}
		}
	}
}
//如果是枚举类型，则当选择枚举值时，动态填写value的值
function setValue(){
	var itemOptionsChoose = $("#itemOptionsChoose").val();
	var value = $("#value").val(itemOptionsChoose);
}
</script>
</html>