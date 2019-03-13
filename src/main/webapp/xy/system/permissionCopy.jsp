<%@include file="../../e5include/IncludeTag.jsp" %>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<%@page pageEncoding="UTF-8" %>
<html>
<head>
    <title>请选择权限来源用户</title>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
    <script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
    <style>

        #selectUser{
            width: 100%;
            height: 700px;
        }
        #title{
            color: red;
            font-size: 16px;
            font-weight: bolder;
        }
        .frm {
            display: none;
        }
    </style>
</head>
<body>
<p id="title">请选择权限来源用户：</p>
<iframe name="selectUser" id="selectUser" src="../../xy/SimpleSelect.do?type=5&siteID=<c:out value="${param.siteID}"/>" ></iframe>
<div id="userInfo"></div>
<iframe name="frmPermissionCopy" id="frmPermissionCopy" src="" class="frm"></iframe>
<script>
    var docIDs = "<c:out value="${param.DocIDs}"/>";
    var UUID = "<c:out value="${param.UUID}"/>";
    var DocLibID = "<c:out value="${param.DocLibID}"/>";
    var type = "<c:out value="${param.type}"/>";
    var srcIDs;
    function groupSelectOK (docLibID, srcID) {
        if(srcID.split(",").length>1) {
            alert("只能选择一个来源用户！")
            return;
        }
        srcIDs = srcID;
        $.ajax({
            url: "../../xy/user/permissionCopy.do",
            type: 'POST',
            data: {
                "destIDs": docIDs,
                "srcID": srcID,
            },
            success: function (msg) {
                if (msg === "ok") {
                    operationSuccess();
                } else {
                    alert(msg);
                    operationFailure();
                }
            }
        })
    }

    function groupSelectCancel(){
        operationFailure()
    }

    //操作成功了调用
    function operationSuccess() {
        var url = "../../e5workspace/after.do?UUID=" + UUID + "&DocIDs=" + docIDs
            + "&DocLibID=" + DocLibID
            + "&Opinion=从用户 " + srcIDs + " 复制权限";
        $("#frmPermissionCopy").attr("src", url);

    }
    //操作失败了调用
    function operationFailure() {
        var url = "../../e5workspace/after.do?UUID=" + UUID;
        $("#frmPermissionCopy").attr("src", url);
    }
</script>

</body>
</html>