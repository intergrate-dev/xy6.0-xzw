<%@ page language="java" pageEncoding="UTF-8"%>
 <%
    String path = request.getContextPath();
    String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
 %>

    <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
    <html>
    <head>
    <title>插入代码</title>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
    <script type="text/javascript" src="../internal.js"></script>
    <link rel="stylesheet" type="text/css" href="code.css" />
    <script src="../../src/adapter/editorui.js"></script>
    </head>
    <body>
    <div class="wrapper">

    <!-- 标题 -->
    <div id="tabHeads" class="tabhead">
    <!--<span tabSrc="upload" data-content-id="upload"><var id="lang_tab_uploadV"></var></span>-->
    <span tabSrc="codelib" class="tab" data-content-id="online"><var id="lang_tab_online"></var></span>
    </div><!-- END 标题 -->

    <div id="tabBodys" class="tabbody">
    <!-- 插入代码 -->
    <div class="content-header">
    <p>插入代码</p>
    </div>
    <div id="code-content">
    <textarea name="code" id="code-textarea" ></textarea>
    </div>


    </div>>
    </div>>


    <!-- jquery -->
    <script type="text/javascript" src="../../third-party/jquery-1.10.2.min.js"></script>

    <!-- webuploader -->
    <script type="text/javascript" src="../../third-party/webuploader/webuploader.min.js"></script>
    <link rel="stylesheet" type="text/css" href="../../third-party/webuploader/webuploader.css">

    <!-- video -->
    <script type="text/javascript" src="code.js"></script>
    </body>
</html>