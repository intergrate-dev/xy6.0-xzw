<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
    <title>活动栏目树checkbox选择</title>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
    <script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
    <style>

        body{
            font-family: "微软雅黑";
            font-size : 14px;
        }
        input{border: 0;}

        .pull-left{
            float:left;
        }
        #hint{color:gray;}
        .th{
            cursor:pointer;
            width:100px;
            white-space:nowrap;
            font-weight: 100;
        }
        label{
            margin: 5px 20px;
        }
        .list{
            width: 93.5%;
        }
        .checkbox{
            margin: 2px;
        }
        .glyphicon-stop{
            font-size : 18px;
            margin: 0 5px 0 15px;
        }
        table{
            margin-top: 10px;
        }
        .font{
            color: #00a0e6;
            float: left;
            margin-left: 15px;
        }
        .clearfix:after {
            content:"";
            display: block;
            clear:both;
        }
        .tipGroup{
            margin: 10px 0 0 25px;
            height: 50px;
        }
        .tipTxt{
            margin: 0 40px 5px 31px;
        }
        #flowDivs{
            margin-left: 200px;
        }
        #flows,.tipGroup{
            margin-left: 6px;
            display: block;
            font-size: 12px;
        }
        #flows{
            margin-left: 33px;
        }
        #unFlows{
            margin-left: 12px;
            display: BLOCK;
        }
        .ml40{
            margin-left: 40px;
        }
        .ths{
            margin-right: 40px;
            display: block;
        }
        #rs_tree .edit, #rs_tree .remove, #rs_tree .add{
            display: none;
        }
        #rs_tree li span.add {
            background-position: -143px 0;
        }
        #rs_tree li span.button.ico_docu{
            background: url(../../xy/img/home.png) 0 0 no-repeat !important;
        }
    </style>

</head>
<body style="margin-bottom:0">
<div class = "allDiv clearfix">
    <div  style="float:left">
        <%@include file="TreeTopic.inc"%>
    </div>
    <script>
        //渠道
        var parentID="<c:out value="${param.parentID}"/>";
        var colID="<c:out value="${param.ids}"/>";
        var DocLibID = "<c:out value="${param.DocLibID}"/>";
        var roleID = "<c:out value="${param.roleID}"/>";

        //设置栏目树需要的参数
        topic_tree.check.ids = "<c:out value="${param.ids}"/>";
        if (topic_tree.check.ids==""){
            topic_tree.check.ids=sessionStorage.getItem("ids");
            topic_tree.check.parent=sessionStorage.getItem("parentIDs");
        }
        topic_tree.siteID = "<c:out value="${param.siteID}"/>";
        if (!topic_tree.siteID) topic_tree.siteID = 1;

        topic_tree.rootUrl = "../../xy/column/TopicTree.do";

        var type = "<c:out value="${param.type}"/>";
        if (type == "all") {
            setTimeout(function(){
                $("#flowDivs").hide();
            },10);
            $("#btnColCancel").hide();//隐藏取消按钮
            topic_tree.check.enable = true;
            topic_tree.rootUrl += "?parentID=0";
        } else if (type == "role") {
            $("#btnColCancel").hide();//隐藏取消按钮
            //取有权限的树 不带勾选
            topic_tree.rootUrl += "?role=1&roleID="+roleID;
            topic_tree.colClick0 = colClick1;
        } else if (type == "radio") {
            setTimeout(function(){
                $("#flowDivs").hide();
            },10);
            //取有操作权限的树，单选
            if (parentID){
                topic_tree.rootUrl += "?parentID=" + parentID;
            }else{
                topic_tree.rootUrl += "?admin=1";
            }
            topic_tree.check.chkStyle = "radio";
            topic_tree.colClick0 = colClick2;
        } else if (type == "op") {
            if (parentID){
                topic_tree.rootUrl += "?parentID=" + parentID;
            }else{
                topic_tree.rootUrl += "?admin=1";
            }
            topic_tree.check.enable = true;
        } else {
            topic_tree.rootUrl += "?admin=1";
        }
        //默认是复选，需单选时可加参数style=radio
        if ("<c:out value="${param.style}"/>" == "radio") {
            topic_tree.check.chkStyle = "radio";
        }
        if(opType == "copy"){
            topic_tree.check.chkStyle = "radio";
        }
        function getChecks() {
            try {
                if(opType == "copy"){
                    if ($("#chkColPub").is(":checked")  && $("#labColPub").is(":visible")){
                        parent.puborApr = "1";
                    } else if ($("#chkColtoApr").is(":checked")  && $("#labColtoApr").is(":visible") ){
                        parent.puborApr = "2";
                    } else {
                        parent.puborApr = "0";
                    }
                }
                if(type == "radio"){
                    if ($('.aui_title', parent.parent.document).html() == '复制') {
                        parent.columnClose(topic_tree.getFilterChecks()[0], topic_tree.getChecks()[0]);
                    }else{
                        parent.columnClose($("#nodeID").val());
                    }
                }else{
                    parent.columnClose(topic_tree.getFilterChecks(), topic_tree.getChecks());
                }
            } catch (e) {
                var hint = "父窗口应实现columnClose(filterChecked, checked)方法供栏目树关闭时调用。";
                alert(hint);
            }
        }

        function doCancel() {
            try {
                parent.columnCancel();
            } catch (e) {
                var hint = "父窗口应实现columnCancel()方法供栏目树取消时调用。";
                alert(hint);
            }
        }

        //提供一个接口供父级窗口调用
        function  getAllFilterChecked(){
            return topic_tree.getChecks();
        }

        var opType = "<c:out value="${param.opType}"/>";

        //按钮
        $("#divColBtn").show();
        $("#btnColCancel").click(doCancel);
        $("#btnColOK").click(getChecks);

        function colClick2(event, treeId, treeNode, clickFlag){
            $("#nodeID").val(treeNode.id);
        }

        function colClick1(event, treeId, treeNode, clickFlag){
        }
    </script>
    <script type="text/javascript">
        $(function(){
            $("#rs_tree", window.parent.document).css("height",500);
        });
    </script>
</div>
<IFrame id="iframe" name="iframe" style="display:none"></IFrame>
<input type="hidden" id="nodeID" value="">


</body>
</html>