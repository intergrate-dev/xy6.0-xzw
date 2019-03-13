
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Title</title>
    <style>
        body{
        background-color:#fff;
        }
        .import-btn{
        margin-top: 50px;
        text-align:center;
        }
        .import-txt{
        margin:0 auto;
        text-align:center;
        }
        .import-txt input{
        margin-left: 85px;
        width:300px;
        height:30px;
        font-family:"微软雅黑";
        font-size:14px;
        padding:4px;
        }
        .import-btn input{
        width:100px;
        height:30px;
        font-family:"微软雅黑";
        font-size:14px;
        }
    </style>
</head>
<script src="../special/third/jquery-ui-bootstrap-1.0/assets/js/vendor/jquery-1.9.1.min.js" type="text/javascript"></script>
<body>
   <div style="margin-top:50px;">
    <form id="importForm" name="importForm" method="post" enctype="multipart/form-data" action="${pageContext.request.contextPath }/xy/wordList/ImportXmlSave.do" >
        <div class="import-txt">
            <input id="import-uuid" type="hidden" name="UUID" value="${UUID}">
            <input type="file" id="file" name="file" >
        </div>
        <div class="import-btn">
            <input type="button" id="btn-confirm" value="确定" />
            <input type="button" id="btn-cancle" value="取消" onClick="doCancel()"/>
        </div>
    </form>
   </div>
</body>
<script type="text/javascript">

    $(function(){
        $("#btn-confirm").click(function(){
            $("#importForm").submit();
        });
    });

    function doCancel() {
        window.onbeforeunload = null;

        $("#btn-confirm").disabled = true;
        $("#btn-cancle").disabled = true;

        beforeExit();
    };

    function beforeExit() {
        var uuid = $("#import-uuid").val();
        var dataUrl = "../../e5workspace/after.do?UUID=" + uuid;

        window.location.href = dataUrl;
    }


</script>

</html>
