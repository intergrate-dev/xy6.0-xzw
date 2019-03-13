<%@include file="../../e5include/IncludeTag.jsp" %>
<html>
<head>
    <title>报纸叠管理</title>
    <link type="text/css" rel="stylesheet" href="../../e5script/jquery/jQuery-Validation-Engine/css/validationEngine.jquery.css"/>
    <link type="text/css" rel="stylesheet" href="../script/bootstrap-3.3.4/css/bootstrap.min.css">
    <link type="text/css" rel="stylesheet" href="../nis/css/vote.css">
</head>
<body>
<iframe id="closeFrame" name="closeFrame" src="" style="display: none;"></iframe>
<!-- 主容器 -->
<div class="container-fluid">
    <form name="postForm" id="postForm" target="closeFrame" method="post" action="Piles.do">
        <input type="hidden" name="UUID" id="UUID" value="${param.UUID}"/>
        <input type="hidden" name="DocLibID" id="DocLibID" value="${param.DocLibID}"/>
        <input type="hidden" name="DocIDs" id="DocID" value="${param.DocIDs}"/>
        <input type="hidden" name="piles" id="piles" value='${piles}'/>

        <div class="form-horizontal" style="margin-top: 10px;">
            <div class="form-group">
                <div style="margin-left: -14px;" id="containerDiv" data-no="0" class="col-sm-10"></div>
            </div>
            <div class="form-group col-sm-2">
				<button type="button" class="btn btn-warning" onclick="paper_piles.addNew(this);">+添加</button>
            </div>
            <div class="form-group col-sm-2 btnwidth">
                <button type="button" class="btn btn-info control-label dosave" onclick="paper_piles.doSubmit();">确定</button>
            </div>
            <div class="form-group col-sm-2">
                <button type="button" class="btn btn-default control-label docancle" onclick="paper_piles.doCancel();">取消</button>
            </div>
        </div>
    </form>
</div>
<!-- END 主容器 -->

<!-- 选项的代码块 - 每添加一个选项就从这个模块中copy过去 -->
<div id="backupDiv" style="display:none;">
    <div class="form-inline div-border">
        <div class="divinfo">
            <div class="form-group">
                <label class="custform-label-require"><span id="No_Span_"></span>叠名称：</label>
                <input name="name_" id="name_" class="form-control validate[required,checkMaxInput[30]]">
            </div>
            <div class="form-group">
                <label class="custform-label-require">叠代号：</label>
                <input id="code_" name="code_" class="form-control validate[required,checkMaxInput[10]]">
            </div>
        </div>
        <div style="display: inline-block;">
            <div class="form-group">
                <input type="button" id="deleteBtn_" class="delete" onclick="paper_piles.deleteBtnListener(this)" value="删除"/>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
<script type="text/javascript" src="../../e5script/jquery/jQuery-Validation-Engine/js/jquery.validationEngine.js"></script>
<script type="text/javascript" src="../../e5script/jquery/jQuery-Validation-Engine/js/languages/jquery.validationEngine-zh_CN.js"></script>
<script type="text/javascript" src="../article/script/json2.js"></script>
<script type="text/javascript" src="script/piles.js"></script>
</body>
</html>
