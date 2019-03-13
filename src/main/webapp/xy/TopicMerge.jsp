<%@include file="../e5include/IncludeTag.jsp" %>
<%@ page pageEncoding="UTF-8" %>
<%@include file="inc/MainHeader.inc" %>
<style type="text/css">
    #wrapMain {
        margin-left: 0px;
        padding-left: 0px;
        width: 98%;
        margin: 0 auto;
    }
    #panContent {
        height: 350px;
    }
</style>
<script>
    var siteID = "<c:out value="${siteID}"/>";
    var UUID = "<c:out value="${UUID}"/>";
    var docID = "<c:out value="${docID}"/>";

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
            process = function(event){
                var srcButton = $(this);
                srcButton.unbind("click");
                setTimeout(function(){
                    srcButton.click(event.data, process)
                }, 500);
                $("#DocLibID").val(toolbarparam.docLibID);
                $("#DocIDs").val(toolbarparam.docIDs);
                $("#siteTopic").submit() ;
            },
            cancel = function(event){
                var dataUrl = "../../e5workspace/after.do?UUID=" + UUID;
                window.location.href = dataUrl;
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
<script type="text/javascript" src="../e5script/jquery/jquery-1.9.1.min.js"></script>
<script type="text/javascript" src="script/bootstrap-3.3.4/js/bootstrap.min.js"></script>
<script src="script/bootstrap-datetimepicker/bootstrap-datetimepicker.js" charset="UTF-8"></script>
<script src="script/bootstrap-datetimepicker/locales/bootstrap-datetimepicker.zh-CN.js" charset="UTF-8"></script>
<body>
<div id="wrapMain">
    <div>
        <%@include file="inc/SiteSearchTopic.inc" %>
        <div id="main">
            <div id="panContent" class="panContent">
                <%@include file="inc/Statusbar.inc" %>
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
