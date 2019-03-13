<%@ include file="./IncludeTag.jsp"%>
<i18n:locale locale="<%=request.getLocale()%>" changeResponseLocale="false"/>
<Script>
	function changeDealer()
	{
		var x = window.parent;
		var i = window.Dealer.selectedIndex;
		x.form.FLOW_HANDLERID.value= window.Dealer.options(i).value;
		x.form.FLOW_HANDLERNAME.value = window.Dealer.options(i).text;
	}
</Script>
<Select Name="Dealer" size=15 Class="100" onchange="changeDealer()">
	<c:forEach items="${userlist}" var="user">
	<option value="<c:out value="${user.userID}"/>"><c:out value="${user.userName}"/></option>
	</c:forEach>
</Select>
<Script>
		if (window.parent.form.selDealer.checked){
			window.Dealer.disabled= false;
		}
		else{
			window.Dealer.disabled= true;
		}
</Script>
