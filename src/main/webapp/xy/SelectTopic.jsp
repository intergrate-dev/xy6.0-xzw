<%@include file="../e5include/IncludeTag.jsp" %>
<%@ page pageEncoding="UTF-8" %>
<%@include file="inc/MainHeader.inc" %>
<style type="text/css">
    body{
        overflow: auto;
    }
    #wrapMain {
        margin-left: 0px;
        padding-left: 0px;
        width: 98%;
        margin: 0 auto;
        color: #333;
    }
    #main .allGroup{
        float: left;
        /*margin-left: 30px;*/
        /*border:solid 1px #ddd;*/
        overflow: auto;
        width : 273px;
        height: 499px;
        /*height : 260px;*/
        margin-right: 27px;
        border: solid 1px #cdcdcd;
    }
    #siteList tr td{
        width: 253px;
        text-overflow: ellipsis;
        overflow: hidden;
        white-space: nowrap;
        display: block;
    }
    /*#siteList tr:hover td{*/
        /*!*width: 253px;*!*/
        /*text-overflow: clip;*/
        /*overflow: visible;*/
        /*display: inline-block;*/
        /*white-space: normal;*/
        /*!*display: block;*!*/
    /*}*/
    #panContent{
        float:left;
        height: 350px;
        width: 514px;
    }
    .topicGroup{
        color: #000;
        cursor: pointer;
    }
    .topicNone{
        color: #000;
        cursor: pointer;
        font-weight: bold;
        /*background: #f5f5f5;*/
    }
    /*#siteList tr:hover > td{*/
        /*background: #f5f5f5;*/
    /*}*/
    #siteList .selectTopic{
        /*background: #d4ebf8;*/
        background: #00A0E6;
        color: #fff;
    }
</style>

<script type="text/javascript" src="../e5script/jquery/jquery-1.9.1.min.js"></script>
<script type="text/javascript" src="script/bootstrap-3.3.4/js/bootstrap.min.js"></script>
<script src="script/bootstrap-datetimepicker/bootstrap-datetimepicker.js" charset="UTF-8"></script>
<script src="script/bootstrap-datetimepicker/locales/bootstrap-datetimepicker.zh-CN.js" charset="UTF-8"></script>
<body>
<div id="wrapMain">
    <div>
        <%@include file="inc/SiteSelectTopic.inc" %>
        <div id="main">
            <div class="allGroup">
                <table id="siteList">
                    <%--<tr><td class="topicNone" sid="" id=""><span></span></td></tr>--%>
                </table>
            </div>
            <div id="panContent" class="panContent">
                <%@include file="inc/StatusbarTopicSelect.inc" %>
            </div>
        </div>
    </div>
</div>
<div style="display:none">
    <form id="siteTopic" action="../xy/topic/TopicMergeSubmit.do" method="post">
        <input type="hidden" id="UUID" name="UUID" value="${UUID}">
        <input type="hidden" id="siteID" name="siteID" value="${siteID}">
        <input type="hidden" id="docID" name="docID" value="${docID}">
        <input type="hidden" id="DocLibID" name="DocLibID">
        <input type="hidden" id="DocIDs" name="DocIDs">
    </form>
</div>
</body>
<%@include file="inc/MainFooter.inc" %>

<script>
    var siteID = "<c:out value="${siteID}"/>";
    var idData = '<c:out value="${topicIDString}" escapeXml="false"/>';
    var nameData = '<c:out value="${topicNameDString}" escapeXml="false"/>';

    $(function(){
        <!--父页面的值回显在已选择话题start -->
        showTopics();
        <!--父页面的值回显在已选择话题end -->

        <!--请求全部的话题组start -->
        getTopicGroups();
        <!--请求全部的话题组end -->
        //点击话题组，添加类或者移除类（样式）
        $("#siteList .topicGroup").on("click", function(){
            $(this).parent().siblings().find("td").removeClass("selectTopic");
            $(this).toggleClass("selectTopic");
            search_art.Search();
        });

    })
    //请求全部的话题组
    function getTopicGroups(){
        var url = "../../xy/column/TopicTree.do?parentID=0&siteID=" + siteID;
        $.ajax({
            url: url,
            // type: "POST",
            dataType: "json",
            async: false,
            // contentType: "application/json;charset=utf-8",
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                console.log(errorThrown + ':' + textStatus);
            },
            success: function (data) {
                //显示话题组
                var html = "";
                $.each(data, function(index, item){
                    html +='<tr><td class="topicGroup" id="'+item.id+'" title="'+ item.name + '"><span>'+item.name+'</span></td></tr>';
                });
                $("#siteList").append(html);

            }
        });
    }
    //父页面的值回显在已选择话题
    function showTopics(){
        var idDataArray = [];
        var nameDataArray = [];
        //如果上一个页面没有传值
        if(idData == "undefined" || idData == "" || idData == "[]"){
            return;
        }else if(idData){
            idDataArray = JSON.parse(idData);
            nameDataArray = JSON.parse(nameData);
        }
        //得到要添加的html代码
        var newLabelHTML = "";
        for(var i =0; i < idDataArray.length; i++){
            newLabelHTML += "<li data='"+idDataArray[i]+"' name='"+nameDataArray[i]+"'>"+nameDataArray[i]+"<span>x</span></li>";
        }
        $("#topics").html(newLabelHTML);
    }

    e5.mod("workspace.toolkit", function(){
        var api, toolbarparam,
            //订阅响应
            listening = function(msgName, callerId, param){
                for(var name in param){
                    toolbarparam[name] = param[name];
                }
                if(toolbarparam.docLibIDs){
                    toolbarparam.docLibID = toolbarparam.docLibIDs;
                }
                toolbarparam.docLibID = dealDocLibIDs(toolbarparam.docLibID);
                toolbarparam.docIDs = toolbarparam.docIDs;
            },
            //保存（确定）
            process = function(event){
                //防连击
                var srcButton = $(this);
                srcButton.unbind("click");
                setTimeout(function(){
                    srcButton.click(event.data, process)
                }, 500);
                // $("#DocLibID").val(toolbarparam.docLibID);
                // $("#DocIDs").val(toolbarparam.docIDs);
                // $("#siteTopic").submit() ;
                // var idData = [];
                // var nameData = [];
                // $("#topics li").each(function(){
                //     idData.push($(this).attr("data"));
                //     nameData.push($(this).attr("name"));
                // });
                parent.article_topic.closeTopic($("#topics").html());
                // parent.article_topic.closeTopic(idData, nameData);
                // parent.article_topic.winTmp.hide();
            },
            cancel = function(event){
                // var dataUrl = "../../e5workspace/after.do?UUID=" + UUID;
                // window.location.href = dataUrl;
                parent.article_topic.winTmp.hide();
            },
            dealDocLibIDs = function(docLibIDs){
                if(!docLibIDs) return "";

                var libArr = (docLibIDs + "").split(",");
                docLibID = libArr[0];

                for(var i = 1; i < libArr.length; i++){
                    if(libArr[i] && libArr[i] != docLibID){
                        return docLibIDs;
                    }
                }
                return docLibID;
            };
        //-----init & onload--------
        var init = function(sandbox){
            api = sandbox;
            $("#doSave").click(process);
            $("#doCancel").click(cancel);

            toolbarparam = new ToolkitParam();
            api.listen("workspace.doclist:doclistTopic", listening);
        };
        return {
            init: init
        }
    }, {
        requires: [
            "../e5script/e5.utils.js",
            "../e5workspace/script/Param.js"
        ]
    });
</script>
