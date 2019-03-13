<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5context" changeResponseLocale="false"/>
	
<html>
	<head>
		<title><i18n:message key="scheduler.sysTrigger.title"/></title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<script type="text/javascript">
			var selectedTr = '';
			//选中一个记录的函数
			function selected(src){
				if(selectedTr!=''){
			    	document.getElementById(selectedTr).style.backgroundColor='';
				}
				src.style.backgroundColor="#E4E8EB";
				selectedTr = src.getAttribute("id");	
			}
			function doModify(id){
				location.href="SysTriggerEdit.do?action=edit&id="+id;
			}
			function doDelete(id){
				if(confirm("<i18n:message key="sysJob.confirm.delete"/>")){
					location.href="SysTrigger.do?action=delete&id="+id+"&jobID="+<c:out value="${jobID}"/>;
				}
			}
		</script>
		<link type="text/css" rel="stylesheet" href="../e5style/reset.css"/>
		<link type="text/css" rel="stylesheet" href="../e5style/sys-main-body-style.css"/>
		<style type="text/css">
			.bluetext{
				color:blue;
				cursor: pointer;
			}
			.prom{
				color:gray;
				margin-top:10px;
			}
		</style>
	</head>
<body>
<table cellpadding="0" cellspacing="0" class="table">
	<caption><i18n:message key="scheduler.sysTrigger.title"/></caption>
	<tr>
		<th>ID</th>
		<th><i18n:message key="scheduler.sysTrigger.name"/></th>
		<th><i18n:message key="scheduler.sysTrigger.server"/></th>
		<th><i18n:message key="scheduler.sysTrigger.cronExpression"/></th>    
		<th>&nbsp;</th>
	</tr>
	<c:forEach var="item" items="${list}">
		<tr id="<c:out value="${item.triggerID}"/>" onclick="selected(this)" style="cursor:hand">
		<td><c:out value="${item.triggerID}"/></td>
		<td>
			<c:out value="${item.name}"/>
			<c:if test="${item.active=='Y'}">
				(<i18n:message key="scheduler.trigger.active.Y"/>)
			</c:if>
			<c:if test="${item.active=='N'}">
				(<i18n:message key="scheduler.trigger.active.N"/>)
			</c:if>
		</td>
		<td><c:out value="${item.server}"/></td>
		<td style="white-space:normal;">
			<c:out value="${item.cronExpression}"/>
			<br/>
			<span style="color:gray;font-style:italic;"><c:out value="${item.description}"/></span>
		</td>
		<td>
			<span class="bluetext" onclick="doModify(<c:out value="${item.triggerID}"/>)"><i18n:message key="scheduler.button.modify"/></span>
			<span class="bluetext" onclick="doDelete(<c:out value="${item.triggerID}"/>)"><i18n:message key="scheduler.button.delete"/></span>
		</td>
		</tr>
	</c:forEach>
	<tr>
		<td colspan="5" class="alignCenter">
			<input type="button" name="Submit" value="<i18n:message key="scheduler.button.add"/>" onclick="location.href='SysTriggerEdit.do?jobID=<c:out value="${jobID}"/>'" class="button">
		</td>
 	</tr>
</table>
<div class="prom">
	<i18n:message key="scheduler.sysTrigger.note"/>
</div>
</body>
</html>
