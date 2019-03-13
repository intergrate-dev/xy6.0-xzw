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
	var colID = "<c:out value="${colID}"/>";
	var colName = "<c:out value="${colName}"/>";
	var ch = "<c:out value="${ch}"/>";
	var type = "<c:out value="${type}"/>";
    var UUID = "<c:out value="${UUID}"/>";
    
	//当用户提交选择的栏目,实现栏目选择树的接口
	function columnClose(filterChecked, allFilterChecked) {
		var colIds = allFilterChecked[0];
		var colNames = allFilterChecked[1];
		search_art.columnClose(colIds,colNames);
	}
	function columnCancel() {
		search_art.columnCancel();
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
		process = function(event){
			var srcButton = $(this);
			srcButton.unbind("click");
			setTimeout(function(){
				srcButton.click(event.data, process)
			}, 500);
			$("#DocLibID").val(toolbarparam.docLibID);
			$("#DocIDs").val(toolbarparam.docIDs);
			$("#siteArticle").submit() ;
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
<body>
<div id="wrapMain">
    <div>
        <%@include file="inc/SiteSearchArticle.inc" %>
        <div id="main">
            <div id="panContent" class="panContent">
                <%@include file="inc/Statusbar.inc" %>
            </div>
        </div>
    </div>
</div>
<div style="display:none">
	<form id="siteArticle" action="${type == 0 ? '../xy/article/SiteArticle.do':'../xy/article/relSiteCloumn.do'}" method="post">
		<input type="hidden" id="UUID" name="UUID" value="${UUID}">
		<input type="hidden" id="colID" name="colID" value="${colID}">
        <input type="hidden" id="siteID" name="siteID" value="${siteID}">
        <input type="hidden" id="originalSiteID" name="originalSiteID" value="${siteID}">
        <input type="hidden" id="DocLibID" name="DocLibID">
		<input type="hidden" id="DocIDs" name="DocIDs">
	</form>
</div>
</body>
<%@include file="inc/MainFooter.inc" %>
