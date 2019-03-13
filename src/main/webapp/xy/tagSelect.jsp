<%@include file="../e5include/IncludeTag.jsp" %>
<%@ page language="java" pageEncoding="UTF-8" %>
<%
    String path = request.getContextPath();
%>
<html lang="zh-CN">
<style>
    #body{
        font-family: "Microsoft YaHei";
    }
    .tagGroup{
        background:#B0E0E6;
        height:25px;
        line-height: 25px;
        color:white;
        margin-bottom: 5px;
        margin-top: 5px;
        padding-left: 10px;
    }
    .tagGroup:hover{
      background-color: #4169E1;
        cursor: pointer;
    }
    .selectedTag{
        background:#B0E0E6;
        color:white;
        margin-right: 5px;
        display: inline-block;
    }
    .selectedTag:hover{
        background-color: #980c10;
    }
    .tagCheckbox{
        float:left;
        height:20px;
        width:100px;
        overflow: hidden;
        text-overflow:ellipsis;
        white-space: nowrap;
    }
    .btnGroup{
        text-align: right;
        margin-top: 10px;
        margin-right: 10px;
        margin-bottom: 10px;
    }
    .btnGroup input{
        font-size: 12px;
    }
    #doSave,#doCancel{
        padding:6px 45px;
    }
</style>
<head> 
<title></title>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">

    <link type="text/css" rel="stylesheet" href="<%=path %>/xy/script/bootstrap-3.3.4/css/bootstrap.min.css">
    <link type="text/css" rel="stylesheet" href="<%=path %>/xy/script/jqPhoto/css/jqueryPhoto.css">


    <script type="text/javascript" src="<%=path %>/xy/script/jquery/jquery.min.js"></script>
    <script type="text/javascript" src="<%=path %>/xy/script/bootstrap-3.3.4/js/bootstrap.min.js"></script>

</head> 
<body id="body">
<div id="div1">
    <div class="btnGroup">
        <input type='button' class="btn btn-success" id="doSave"  value='保存' />
        <input type='button' class="btn btn-danger" id="doCancel"  value='取消'/>
    </div>
    <div id="div11"></div>
    <div id="div12"></div>
</div>
</body>
<script type="text/javascript" src="<%=path %>/xy/script/tagSelect.js"></script>
<script>
    var siteID = '<c:out value="${param.siteID}"/>';
    var tags = '<c:out value="${param.tags}"/>';
    $(document).ready(function () {
        showDiv();
        setChecked();
    });
</script>

</html>