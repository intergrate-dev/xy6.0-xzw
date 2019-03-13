<%@page contentType="text/xml;charset=UTF-8"%><%
	response.setHeader("Cache-Control","no-store");
	response.setHeader("Pragma","no-cache");
	response.setDateHeader("Expires",0);
	
	String jndi  = request.getParameter("jndi");
	String dbType= request.getParameter("dbType");
	com.founder.e5.context.E5DataSource ds = new com.founder.e5.context.E5DataSource();
	ds.setDbType(dbType);
	ds.setDataSource(jndi);
	boolean ret = com.founder.e5.commons.VerifyUtil.testDataSource(ds);
	out.print("<ret>"+ret+"</ret>");
%>