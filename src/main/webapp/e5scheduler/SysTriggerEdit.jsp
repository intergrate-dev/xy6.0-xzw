<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5context" changeResponseLocale="false"/>
<html>
	<head>
		<title><i18n:message key="scheduler.sysTrigger.edit"/></title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<script type="text/javascript" src="../e5script/Function.js"></script>
		<script type="text/javascript">
			function checkInput(){
				var f1 = document.forms[0];  
				var errors = "";
				// alert(""+f1.elements["name"].value);
				if(f1.elements["name"].value == ''){
					errors = errors + '<i18n:message key="error.sysTrigger.name.required"/>\n';    
				}else if(getLength(f1.elements["name"].value)>40){
					errors = errors + "<i18n:message key="error.sysTrigger.name.maxlength"/>\n";    
				}
				if(f1.cronExpression.value == ''){
					errors = errors + '<i18n:message key="error.sysTrigger.cronExpression.required"/>\n';
				}
				if(getLength(f1.cronExpression.value)>200){
						errors = errors + "<i18n:message key="error.sysTrigger.cronExpression.maxlength"/>\n";
				}
				if(errors!=""){
					alert("<i18n:message key="errors.header"/>\n"+errors);
					return false;
				}
				return true;
			}
			function openHelper(){
				var w1 = window.open('SysTriggerHelper.jsp?expression='+form1.cronExpression.value,'','width=350,height=350');
				var x = (screen.width-350)/2;
				var y = (screen.height-350)/2;
				w1.moveTo(x,y);
			}
			function hideDescription() {
				var d = document.getElementById("descriptionTR");
				if (d) d.style.display = "none";
			}
		</script>
		<link type="text/css" rel="stylesheet" href="../e5style/reset.css"/>
		<link type="text/css" rel="stylesheet" href="../e5style/sys-main-body-style.css"/>
	</head>
	<body onload="form1.elements['name'].focus();">
		<form name="form1" method="post" action="SysTriggerEdit.do?action=save" onSubmit="return checkInput();">
		<input type="hidden" name="triggerID" value="<c:out value="${item.triggerID}"/>">
		<input type="hidden" name="jobID" value="<c:out value="${item.jobID}"/>">	
		<table cellspacing="0" cellpadding="0" class="table">
			<caption><i18n:message key="scheduler.sysTrigger.edit"/></caption>
			<tr>
				<td><span class="field-required">*</span><i18n:message key="scheduler.sysTrigger.name"/></td>
				<td><input type="text" name="name" value="<c:out value="${item.name}"/>" style="width:200px"></td>
			</tr>
			<tr>
				<td><span class="field-required">*</span><i18n:message key="scheduler.sysTrigger.cronExpression"/></td>
				<td>
					<input type="text" name="cronExpression" value="<c:out value="${item.cronExpression}"/>" style="width:200px">
					<input type="button" value="<i18n:message key="scheduler.button.editTrigger"/>" onclick="openHelper();" class="button">
				</td>
			</tr>
			<tr id="descriptionTR">
				<td></td>
				<td style="color:gray;white-space:normal;font-style:italic;"><c:out value="${item.description}"/></td>
			</tr>
			<tr>
				<td><i18n:message key="scheduler.sysTrigger.server"/></td>
				<td>
					<select name="server" style="width:200px">
						<option value="ALL" selected><i18n:message key="scheduler.allServer"/></option>
						<c:forEach var="serverItem" items="${servers}">
							<option value="<c:out value="${serverItem.name}"/>" <c:if test="${serverItem.name==item.server}">selected</c:if>><c:out value="${serverItem.name}"/></option>
						</c:forEach>        
					</select>
				</td>
			</tr>
			<tr>
				<td><i18n:message key="scheduler.active"/></td>
				<td><input id="activeY" type="radio" name="active" value="Y" <c:if test="${item.active=='Y'}">checked</c:if> style="border:none"><label for="activeY"><i18n:message key="scheduler.trigger.active.Y"/></label>
				<input id="activeN" type="radio" name="active" value="N" style="border:none" <c:if test="${item.active=='N'}">checked</c:if>><label for="activeN"><i18n:message key="scheduler.trigger.active.N"/></label></td>
			</tr>  
			<tr>
				<td colspan="2" class="alignCenter">
					<input type="submit" name="Submit" value="<i18n:message key="scheduler.button.save"/>" class="button">
					<input type="button" name="Submit" value="<i18n:message key="scheduler.button.cancel"/>" class="button" onclick="location.href='SysTrigger.do?jobID=<c:out value="${item.jobID}"/>'">
				</td>
			</tr>
		</table>
		</form>
	</body>
</html>
<%@include file="../e5include/Error.jsp"%>