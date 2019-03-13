<%
java.util.ArrayList list = (java.util.ArrayList)request.getAttribute("errors");
if(list!=null)
{
	%>
	<font color="red"><i18n:message key="errors.header"/>
<%
	for(int i=0;i<list.size();i++)
	{	
		com.founder.e5.web.ErrorMessage em = (com.founder.e5.web.ErrorMessage)list.get(i);
%>
	<li><i18n:message key="<%=em.getKey()%>"/> <%if(em.getMessage()!=null) out.print(em.getMessage());%>
<%
	}
  %>
	</font>
<%
	}  
%>