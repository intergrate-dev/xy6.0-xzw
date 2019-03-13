<%
//用于刷新远程节点的缓存
response.setHeader("Cache-Control","no-store");
response.setHeader("Pragma","no-cache");
response.setDateHeader("Expires",0);
response.setContentType("text/plain; charset=UTF-8");
String Index = request.getParameter("Index");
String Checks = request.getParameter("Checks");
String url = request.getParameter("serverURL");
String ret = com.founder.e5.commons.HttpClient.doGet(url+"/e5sys/cacheSubmit.do?Index="+Index+"&Checks="+Checks);
if(ret!=null) ret = ret.trim().replaceAll("\r\n", "");
out.print(ret);
%>