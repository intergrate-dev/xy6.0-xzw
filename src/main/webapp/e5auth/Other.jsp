<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5auth" changeResponseLocale="false"/>
<HTML>
<HEAD>
	<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
	<link type="text/css" rel="stylesheet" href="../e5style/style.css"/>
	<style>
		input{border: 0;}
	</style>
</HEAD>
<BODY>
<IFrame id="iframe" name="iframe" style="display:none"></IFrame>
<Form Name="otherForm"  Target="iframe" Action="./submitPart.do" Method="Post">
<Input Type="hidden" Name="Type"   Value="7"/>
<Table border="0" cellpadding="4" cellspacing="0" class="onlyBorder">	
	<c:if test="${sessionScope.adminOption.online}">
		<TR><TD class="bottomlinetd">
		<input type="checkbox" name="online" id="online" <c:if test="${other.online}">checked</c:if>><i18n:message key="other.online"/>	
		</TD></TR>
	</c:if>
	<c:if test="${sessionScope.adminOption.logger}">
		<TR><TD class="bottomlinetd">
		<input type="checkbox" name="logger" id="logger" <c:if test="${other.logger}">checked</c:if>><i18n:message key="other.logger"/>	
		</TD></TR>
	</c:if>
	<c:if test="${sessionScope.adminOption.config}">
		<TR><TD class="bottomlinetd">
		<input type="checkbox" name="config" id="config" <c:if test="${other.config}">checked</c:if>><i18n:message key="other.config"/>	
		</TD></TR>
	</c:if>
	<c:if test="${sessionScope.adminOption.service}">
		<TR><TD class="bottomlinetd">
		<input type="checkbox" name="service" id="service" <c:if test="${other.service}">checked</c:if>><i18n:message key="other.service"/>	
		</TD></TR>
	</c:if>
	<c:if test="${sessionScope.adminOption.cache}">
		<TR><TD class="bottomlinetd">
		<input type="checkbox" name="cache" id="cache" <c:if test="${other.cache}">checked</c:if>><i18n:message key="other.cache"/>	
		</TD></TR>
	</c:if>
</Table>
<DIV><input type="submit" value="<i18n:message key="button.submit"/>" class="button"></DIV>
</FORM>
</BODY>
</HTML>