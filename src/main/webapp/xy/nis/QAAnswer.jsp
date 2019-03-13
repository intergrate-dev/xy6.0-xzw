﻿
<%@include file="../../e5include/IncludeTag.jsp" %>
<%@page pageEncoding="UTF-8" %>
<html>
<head>
    <title><%=com.founder.e5.workspace.ProcHelper.getProcName(request)%>
    </title>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
    <meta content="IE=edge" http-equiv="X-UA-Compatible"/>
    <link type="text/css" rel="stylesheet" href="../../e5style/reset.css"/>
    <link type="text/css" rel="stylesheet" href="../../e5script/jquery/jQuery-Validation-Engine/css/validationEngine.jquery.css"/>
    <link type="text/css" rel="stylesheet" href="../../e5style/e5form-custom.css"/>
    <link type="text/css" rel="stylesheet" href="../../xy/css/form-custom.css"/>
    <link type="text/css" rel="stylesheet" href="../../e5script/jquery/dialog.style.css"/>
    <link type="text/css" rel="stylesheet" href="../script/bootstrap-3.3.4/css/bootstrap.min.css">
    <script type="text/javascript" src="../script/jquery/jquery.min.js"></script>
    <script type="text/javascript" src="../script/bootstrap-3.3.4/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="../script/jquery-validation-engine/js/languages/jquery.validationEngine-zh_CN.js"></script>
    <script type="text/javascript" src="../script/jquery-validation-engine/js/jquery.validationEngine.js"></script>
    <script type="text/javascript" src="../../e5workspace/script/form-custom.js"></script>
    <script type="text/javascript" src="../../e5script/jquery/jquery.dialog.js"></script>
    <script src="../script/picupload/upload_api.js" type="text/javascript"></script>
    <script>
        var article = {
            siteID: "${siteID}"
        }
    </script>
    <script type="text/javascript" src="./js/qa.js"></script>
    <style>
    	li{
    		list-style: none;
    	}
        .out-border{
            margin-bottom: 14px;
        }
        #btnFormSave{
        	margin-right: 10px;
        	margin-left: 117px;
        }
        .btngroup{
        	margin-bottom: 10px;
        }
        #LABEL_a_hideVideo{
        	margin-left: 116px;
        }
        .custform-label{
        	text-align: right;
        }
    </style>
</head>
<body>
<iframe id="iframe" style="display:none;" src=""></iframe>
<form id="form" name="form" method="post" action="AnswerSubmit.do">
	<input id="type" name="type" type="hidden" value="${type}">
	<input id="siteID" name="siteID" type="hidden" value="${siteID}">
    <%=request.getAttribute("formContent")%>
	<div id="livePicAreaDiv" style="overflow: hidden; display: block;">
        <div class="out-border"  style="width: 640px;">
            <div class="out-border"  style="margin-left: 16px;border: 0px;">
                <div style="overflow: hidden;width: 102px;float: left;">
                    <label class="custform-label" style="">附件</label>
                </div>
                <div class="out-border"  style="width: 518px;margin-left: 0px;float: left; ">
                    <input id="uploadInput" type="file" name="file" multiple>
                </div>
            </div>
        </div>
    </div>
    <div class="underTop">
        <div>
            <li class="btngroup">
                <input class="dosave pull-left" type="button" id="btnFormSave" value="保存"/>
                <input class="docancle" type="button" id="btnFormCancel" value="关闭"/>
            </li>
        </div>
    </div>
</form>
</body>
</html>
