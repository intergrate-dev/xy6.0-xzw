<%@include file="../../e5include/IncludeTag.jsp"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
    <script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
    <script type="text/javascript" src="../script/cookie.js"></script>

    <style>
        *, body {
            font-family: "microsoft yahei";
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        ul, ol, li { list-style:none; }
        .clearfix:after {
            content: ".";
            display: block;
            clear: both;
            visibility:hidden;
            line-height: 0;
            height: 0;
            font-size:0;
        }
        .selectedTopics{
            height: 340px;
            position: relative;
            padding: 15px;

        }
        .topic-select {
            float: left;
            margin-left: 160px;
            border: 1px solid #ddd;
        }
        .topic-select ul {
            border: 1px solid #ddd;
            border-top: none;
            max-height: 280px;
            overflow: auto;
        }
        .topic-select ul li {
            width: 230px;
            height: 30px;
            line-height: 30px;
            text-align: left;
            border-bottom: 1px solid #ddd;
            padding: 4px 0 4px 10px;
            cursor: pointer;
            font-size: 14px;
            font-weight: normal;
            background: none;

            text-overflow: ellipsis;
            word-break: break-all;
            word-wrap: break-word;
            overflow: hidden;
            white-space:nowrap;
        }
        .selected{
            /*background-color:#E4F2F9;*/
            BACKGROUND: #1bb8fa !important;
            COLOR: #fff !important;
            border: none !important;
            line-height: 30px !important;
        }
        .footer{
            text-align: center;
            padding: 15px;
            border-top: 1px solid #e5e5e5;
        }
        .btn-primary {
            color: #fff;
            background-color: #337ab7;
            border-color: #2e6da4;
        }
        .btn {
            display: inline-block;
            padding: 6px 12px;
            margin-bottom: 0;
            font-size: 14px;
            font-weight: 400;
            line-height: 1.42857143;
            text-align: center;
            white-space: nowrap;
            vertical-align: middle;
            -ms-touch-action: manipulation;
            touch-action: manipulation;
            cursor: pointer;
            -webkit-user-select: none;
            -moz-user-select: none;
            -ms-user-select: none;
            user-select: none;
            background-image: none;
            border: 1px solid transparent;
            border-radius: 4px;
        }
        .btn-default {
            color: #333;
            background-color: #fff;
            border-color: #ccc;
        }
        .btn-default{
            margin-left: 5px;
        }

    </style>
</head>
<body>
<div class="container">
    <div class="search" style="display:none">

    </div>
    <div class="selectedTopics clearfix">
        <div class="topic-select" id="topic-select">
            <%--<input id="departmentSearch" type="text" placeholder="输入话题ID或关键字查询">--%>
            <ul>
                <!--<li>部门一</li>
                <li>部门二</li>
                <li>部门三</li>
                <li>部门四</li>-->
            </ul>
        </div>
    </div>
    <div class="footer">
        <button type="button" class="btn btn-primary" id="doSave">确定</button>
        <button type="button" class="btn btn-default" id="doCancel">取消</button>
    </div>

</div>

</body>
<script>
    //获取参数
    var siteID = "<c:out value="${param.siteID}"/>";

    $(function(){
        getTopics(siteID);

        /*<!--选中某个话题 start -->*/
        $("#topic-select").on("click","li",function(){
            $(this).addClass("selected").siblings().removeClass("selected");
            // if($(this).attr("id")=="alltopic"){
            //     $(this).addClass("selected").siblings().removeClass("selected");
            // }else{
            //     $(this).toggleClass("selected");
            //     $("#alltopic").removeClass("selected");
            // }
        });
        /*<!--选中某个话题 end -->*/
        //点击保存按钮
        $('#doSave').click(function(){
            doSave();
        });
        //点击取消按钮
        $('#doCancel').click(function(){
            parent.topic_stat.cancelTopic();
        });

    });

    /*<!--确认选择的话题，可以扩展成选择多个话题 start -->*/
    function doSave(){
//		var _dept=$("#department").find(".selected").text();
//		var _partid=$("#department").find(".selected").attr("departmentid");

        var _topics=[];
        $("#topic-select").find(".selected").each(function(){
            _topics.push($(this).text())
        });
        var _topic=_topics.join(",");
        if(!_topic){
            alert("未选择话题！");
            return;
        }

        var _topicids = [];
        $("#topic-select").find(".selected").each(function(){
            _topicids.push($(this).attr("topicid"));
        });
        var _topicid=_topicids.join(",");

        try{
            parent.topicSelectOK(_topic, _topicid);
        } catch (e) {
            var hint = "父窗口应实现topicSelectOK(_topic, _topicid)方法供话题选择窗口关闭时调用。"
            //	+ "\n   每个参数的格式是:  [ids, names, cascadeIDs]"
            alert(hint);
        }
    }
    /*<!--确认选择的话题 end -->*/

    /*<!--获取话题名称 start -->*/
    function getTopics(){
        var url = "../../xy/topic/getTopics.do";
        // var siteID=getQueryString("siteID");
        var params = {
            "siteID": siteID,
        }
        $.ajax({
            url: url,
            data: params,
            type: "GET",
            dataType: "json",
            async: false,
            // contentType: "application/json;charset=utf-8",
            error: function(XMLHttpRequest, textStatus, errorThrown) {
                alert(errorThrown + ':' + textStatus); // 错误处理
            },
            success: function(data) {
                // console.log(data);
                var len=data.length;
                // var topicMsg='<li style="font-weight: 700;" id="alltopic" departmentid="">全部</li>';
                var topicMsg='';
                for (var i = 0; i < len; i++) {
                    topicMsg +='<li topicID="'+data[i].topicID+'">'+data[i].topicName+'</li>';
                }
                $("#topic-select").find("ul").html(topicMsg);
                //缓存部门信息
                // articleOverview_stat.saveDepartmentMsg(data);

            }
        })
    }
    /*<!--获取话题名称 end -->*/
</script>
</html>
