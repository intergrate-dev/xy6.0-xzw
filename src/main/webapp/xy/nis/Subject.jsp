﻿<%@include file="../../e5include/IncludeTag.jsp"%>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
  <title><%=com.founder.e5.workspace.ProcHelper.getProcName(request)%></title>
  <meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
  <meta content="IE=edge" http-equiv="X-UA-Compatible" />
  <link type="text/css" rel="stylesheet" href="../../e5style/reset.css"/>
  <link type="text/css" rel="stylesheet" href="../../e5script/jquery/jQuery-Validation-Engine/css/validationEngine.jquery.css"/>
  <link type="text/css" rel="stylesheet" href="../../e5script/jquery/dialog.style.css"/>
  <link type="text/css" rel="stylesheet" href="../../e5style/e5form-custom.css"/>
  <link type="text/css" rel="stylesheet" href="../../xy/css/form-custom.css"/>
  <script type="text/javascript" src="../script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="../script/jquery-validation-engine/js/languages/jquery.validationEngine-zh_CN.js"></script>
	<script type="text/javascript" src="../script/jquery-validation-engine/js/jquery.validationEngine.js"></script>
  <script type="text/javascript" src="../../e5script/jquery/jquery.dialog.js"></script> 
  <script type="text/javascript" src="../../e5workspace/script/form-custom.js"></script> 
  
  <link href="../script/jquery-textext/src/css/textext.core.css" rel="stylesheet">
  <link href="../script/jquery-textext/src/css/textext.plugin.autocomplete.css" rel="stylesheet">
  <link href="../script/jquery-textext/src/css/textext.plugin.clear.css" rel="stylesheet">
  <link href="../script/jquery-textext/src/css/textext.plugin.focus.css" rel="stylesheet">
  <link href="../script/jquery-textext/src/css/textext.plugin.prompt.css" rel="stylesheet">
  <link href="../script/jquery-textext/src/css/textext.plugin.tags.css" rel="stylesheet">

  <script type="text/javascript" src="../script/jquery-textext/src/js/textext.core.js"></script>
  <script type="text/javascript" src="../script/jquery-textext/src/js/textext.plugin.ajax.js"></script>
  <script type="text/javascript" src="../script/jquery-textext/src/js/textext.plugin.autocomplete.js"></script>
  <script type="text/javascript" src="../script/jquery-textext/src/js/textext.plugin.clear.js"></script>
  <script type="text/javascript" src="../script/jquery-textext/src/js/textext.plugin.filter.js"></script>
  <script type="text/javascript" src="../script/jquery-textext/src/js/textext.plugin.focus.js"></script>
  <script type="text/javascript" src="../script/jquery-textext/src/js/textext.plugin.prompt.js"></script>
  <script type="text/javascript" src="../script/jquery-textext/src/js/textext.plugin.suggestions.js"></script>
  <script type="text/javascript" src="../script/jquery-textext/src/js/textext.plugin.tags.js"></script>
  
  
  <link rel="stylesheet" type="text/css" href="../../e5script/jquery/uploadify/uploadify.css">
  <link type="text/css" rel="stylesheet" href="../script/bootstrap-3.3.4/css/bootstrap.min.css">
  
  <script type="text/javascript" src="../../e5script/jquery/uploadify/jquery.uploadify-3.1.min.js"></script>
  <script type="text/javascript" src="../script/bootstrap-3.3.4/js/bootstrap.min.js"></script>
  <script type="text/javascript" src="../script/picupload/upload_api.js"></script>
  
  <link type="text/css" rel="stylesheet" href="css/live.css"/>
  <script>
    var article = {
      isNew : "${isNew}",
      siteID : "${siteID}",
      author : "${author}",
      topic : "${topic}"
    }
  </script>
  <script type="text/javascript" src="./js/memberSelect.js"></script>
  <script type="text/javascript" src="./js/subject.js"></script>
  <style>
    .out-border{
      padding: 9px 14px;
      margin-bottom: 14px;
      background-color: #f7f7f9;
      border: 1px solid #e1e1e8;
      border-radius: 4px;
      margin-left: 113px;
    }
    .out-border div{
      margin-bottom: 10px;
    }
    .out-border div:last-child{
      margin-bottom: 0;
    }
    button{
      font-family: "microsoft yahei";
    }
    #label_pic{
   	  position: relative;
      left: 45px;
      top: -50px;
    }
  </style>
  
</head>
<body>
<form id="form" name="form" method="post" action="SubjectSubmit.do">
  <%=request.getAttribute("formContent")%>
  <input type="hidden" id="isNew" name="isNew" value="${isNew}">
  <input type="hidden" id="parentID" name="parentID" value="${parentID}">
  <input type="hidden" id="fromPage" name="fromPage" value="${fromPage}">
  
  <input type="hidden" id="a_answererID" name="a_answererID" value="${a_answererID}">
  <input type="hidden" id="a_answererIcon" name="a_answererIcon" value="${a_answererIcon}">
  <div id="label_pic"><label class="custform-label">背景图片</label></div>
  <div id="picAreaDiv">
    <div class="out-border"  style="width: 600px;">
      <div style="overflow: hidden;">问吧banner设置</div>
      <div class="out-border"  style="width: 500px;margin-left: 16px;border: 0px;">
        <div style="overflow: hidden;">
          <label class="custform-label custform-label-require" style="width: 200px;">APP客户端&触屏（200*100）</label>
        </div>
        <div class="out-border"  style="width: 500px;margin-left: 0px; ">
          <input id="appPicInput" type="file" name="file" multiple>
          <input id="appPicSrcHidden" type="hidden" value=""/>
        </div>
      </div>
    </div>
  </div>
  <div class="underTop">
    <div>
      <li class="btngroup">
        <input class="dosave" type="button" id="btnSave" value="保存"/>
        <input class="docancle" type="button" id="btnCancel" value="关闭"/>
      </li>
    </div>
  </div>
  
</form>
</body>
</html>
