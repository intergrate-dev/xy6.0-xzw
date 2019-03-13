        <%@include file="../../e5include/IncludeTag.jsp"%>
                <i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
                <%@page pageEncoding="UTF-8"%>
                <html>
                <head>
                <title>复制移动活动</title>
                <meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
                <script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
                <script type="text/javascript" src="../../xy/script/cookie.js"></script>
                <style>
                .frm {
                border: 0;
                width: 100%;
                height: 430px;
                }
                </style>
                </head>
                <body>

                <iframe name="frmColumn" id="frmColumn" src="" class="frm"></iframe>
                <script>
                //获取参数
                var type = "<c:out value="${param.type}"/>";
                var siteID = "<c:out value="${param.siteID}"/>";
                var docIDs = "<c:out value="${param.DocIDs}"/>";
                var UUID = "<c:out value="${param.UUID}"/>";
                var DocLibID = "<c:out value="${param.DocLibID}"/>";
                var colType = "radio";
                var colID = "";

                //初始化-直接在弹出窗口中设置frame的链接地址
                $(function() {
                var url = "";
                if(type == "copy" || type == "move"){
                url = "../../xy/column/TopicTreeDialog.jsp?type=" + colType + "&opType=" + type + "&siteID="
                + siteID + "&ids=0&DocLibID=" + DocLibID;
                }else if(type == "qacopy" || type == "qamove"){
                url = "../../xy/column/QaFavorite.jsp?type=" + colType + "&opType=" + type + "&siteID="
                + siteID + "&ids=0&DocLibID=" + DocLibID;
                }
                $("#frmColumn").attr("src", url);
                window.onbeforeunload = operationFailure;

                });
                //当用户提交选择的栏目,实现columnClose方法
                function columnClose(colID) {
                if(colID == null){
                alert("您未选中任何栏目！");
                }
                var theURL = "";
                if( type == "copy"){
                theURL = "../../xy/topic/topicCopy.do";
                }else if(type == "move"){
                theURL = "../../xy/topic/topicMove.do";
                }

                $.ajax({
                url : theURL,
                type : 'POST',
                data : {
                "DocIDs" : docIDs,
                "DocLibID" : DocLibID,
                "ColIDs" : colID
                },
                dataType : 'html',
                success:function(msg, status){
                if (status == "success") {
                if (msg.substr(0, 7) == "success") {//推送成功
                operationSuccess(msg.substr(7));
                } else if(msg.substr(0, 7) == "samecol"){
                alert("您选择的是自身栏目，请重新选择");
                } else if(msg.substr(0, 7) == "rootcol"){
                alert("您选择的是活动根栏目，请重新选择");
                }
                else {
                alert("操作失败");
                operationFailure();
                }
                } else {
                operationFailure();
                }
                }});
                }

                //操作成功了调用
                function operationSuccess(opnions){
                var url = "../../e5workspace/after.do?UUID=" + UUID + "&DocIDs=" + docIDs
                + "&DocLibID=" + DocLibID +"&Opinion="
                + encodeU(opnions);
                $("#frmColumn").attr("src", url);
                }

                //操作失败了调用
                function operationFailure() {
                window.onbeforeunload = null;

                var url = "../../e5workspace/after.do?UUID=" + UUID;
                $("#frmColumn").attr("src", url);
                }

                //点击取消按钮时
                function columnCancel() {
                operationFailure();
                }

                /** 对特殊字符和中文编码 */
                function encodeU(param1) {
                if (!param1)
                return "";
                var res = "";
                for ( var i = 0; i < param1.length; i++) {
                switch (param1.charCodeAt(i)) {
                case 0x20://space
                case 0x3f://?
                case 0x23://#
                case 0x26://&
                case 0x22://"
                case 0x27://'
                case 0x2a://*
                case 0x3d://=
                case 0x5c:// \
                case 0x2f:// /
                case 0x2e:// .
                case 0x25:// .
                res += escape(param1.charAt(i));
                break;
                case 0x2b:
                res += "%2b";
                break;
                default:
                res += encodeURI(param1.charAt(i));
                break;
                }
                }
                return res;
                }

                </script>
                </body>
                </html>