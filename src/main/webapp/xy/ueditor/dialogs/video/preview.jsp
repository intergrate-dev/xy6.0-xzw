<%--
  Created by IntelliJ IDEA.
  User: isaac_gu
  Date: 2015/10/21
  Time: 19:07
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String url = request.getParameter("url");
    String type = request.getParameter("type");
%>
<html>
<head>
    <title></title>
</head>
<body>
<%
    if(type!=null && "video".equals(type)){
%>
<embed type="application/x-shockwave-flash" class="edui-faked-video"
       pluginspage="http://www.macromedia.com/go/getflashplayer"
       src="<%=url %>" width="420" height="280"
       wmode="transparent" play="true" loop="false" menu="false" allowscriptaccess="never" allowfullscreen="true"
        />
<%
    }else{
%>

<audio width="300" height="50" style="width:300px;height:50px;" class="edui-faked-audio" src="<%=url %>" controls="controls" autoplay="autoplay">
    您的浏览器不支持 audio 标签。
</audio>

<%
    }
%>
</body>
</html>
