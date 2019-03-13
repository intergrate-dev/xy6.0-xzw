<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5context" changeResponseLocale="false"/>
<html>
	<head>
		<title><i18n:message key="scheduler.sysJob.edit"/></title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<script type="text/javascript" src="../e5script/Function.js"></script>
		<c:if test="${success=='true'}">
		<script type="text/javascript">
				//父窗口刷新，该窗口将为空白页面 :)
				if (parent.doReload)
					parent.doReload();
		</script>
		</c:if>
		<script type="text/javascript">
			function checkInput(){
				var f1 = document.forms[0];  
				var errors = "";

				//jobName
				if(f1.elements["name"].value == ''){
					errors = errors + '<i18n:message key="error.sysJob.name.required"/>\n';    
				}else if(getLength(f1.elements["name"].value)>40){
					errors = errors + "<i18n:message key="error.sysJob.name.maxlength"/>\n";    
				} 
				if(f1.jobClass.value == ''){
						 errors = errors + '<i18n:message key="error.sysJob.jobClass.required"/>\n';
				}
				if(getLength(f1.jobClass.value)>200){
						errors = errors + "<i18n:message key="error.sysJob.jobClass.maxlength"/>\n";
				}
				if(getLength(f1.configUrl.value)>200){
					 errors = errors +  "<i18n:message key="error.sysJob.configUrl.maxlength"/>\n";			
				}
				if(getLength(f1.logUrl.value)>200){
					 errors = errors +  "<i18n:message key="error.sysJob.logUrl.maxlength"/>\n";			
				}  
				if(getLength(f1.description.value)>200){
					 errors = errors +  "<i18n:message key="error.sysJob.description.maxlength"/>\n";			
				}
				if(errors!=""){
					alert("<i18n:message key="errors.header"/>\n"+errors);
					return false;
				}
				return true;
			}
		</script>
		<link type="text/css" rel="stylesheet" href="../e5style/reset.css"/>
		<link type="text/css" rel="stylesheet" href="../e5style/sys-main-body-style.css"/>
	</head>
	<body onload="form1.elements['name'].focus();">
		<form name="form1" method="post" action="SysJobEdit.do?action=save" onSubmit="return checkInput();">
			<input type="hidden" name="jobID" value="<c:out value="${item.jobID}"/>">
			<table cellspacing="0" cellpadding="0" class="table">
				<caption><i18n:message key="scheduler.sysJob.edit"/></caption>
				<tr>
					<th class="w90"><span class="field-required">*</span><i18n:message key="scheduler.sysJob.name"/></th>
					<td><input type="text" name="name" value="<c:out value="${item.name}"/>" style="width:400px"></td>
				</tr>
				<tr>
					<th><span class="field-required">*</span><i18n:message key="scheduler.sysJob.class"/></th>
					<td><input type="text" name="jobClass" value="<c:out value="${item.jobClass}"/>" style="width:400px"></td>
				</tr>
				<tr>
					<th><i18n:message key="scheduler.sysJob.config"/>URL</th>
					<td><input type="text" name="configUrl" value="<c:out value="${item.configUrl}"/>"  style="width:400px"></td>
				</tr>
				<tr>
					<th><i18n:message key="scheduler.sysJob.log"/>URL</th>
					<td><input type="text" name="logUrl" value="<c:out value="${item.logUrl}"/>"  style="width:400px"></td>
				</tr>
				<tr>
					<th><i18n:message key="scheduler.active"/></th>
					<td>
						<input type="radio" name="active" value="Y" <c:if test="${item.active=='Y'}">checked</c:if> style="border:none" id="activeY"><label for="activeY"><i18n:message key="scheduler.active.Y"/></label>
						<input type="radio" name="active" value="A" style="border:none" id="activeA" <c:if test="${item.active=='A'}">checked</c:if>><label for="activeA"><i18n:message key="scheduler.active.A"/></label>
						<input type="radio" name="active" value="N" style="border:none" id="activeN" <c:if test="${item.active=='N'}">checked</c:if>><label for="activeN"><i18n:message key="scheduler.active.N"/></label>
					</td>
				</tr>  
				<tr>
					<th><i18n:message key="scheduler.sysJob.description"/></th>
					<td><textarea name="description" style="width:400px"><c:out value="${item.description}"/></textarea></td>
				</tr>
				<tr>
					<td class="alignCenter" colspan="2">
						<input type="submit" name="Submit" value="<i18n:message key="scheduler.button.save"/>" class="button">
						<input type="button" name="Submit" value="<i18n:message key="scheduler.button.cancel"/>" onclick="window.document.write('<br>');" class="button">
					</td>
				</tr>
			</table>
		</form>
	</body>
</html>
