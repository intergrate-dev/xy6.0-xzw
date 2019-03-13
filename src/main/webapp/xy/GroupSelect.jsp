<%@include file="../e5include/IncludeTag.jsp" %>
<%@ page pageEncoding="UTF-8" %>
<%@include file="inc/MainHeader.inc" %>
<style type="text/css">
    #panContent {
        height: 350px;
		border-top: 1px solid #ddd;
    }
</style>
<!-- 按组显示的数据的选择窗口，如选择图片库、选择视频库、选择专题 -->
<body>
<%@include file="inc/ResourceGroup.inc"%>
<div id="wrapMain">
    <div>
        <%@include file="inc/SearchGroupSelect.inc"%>
        <div id="main">
            <div id="panContent" class="panContent">
                <%@include file="inc/Statusbar.inc" %>
            </div>
        </div>
    </div>
</div>
</body>
<%@include file="inc/MainFooter.inc" %>
<script>
    var toolbarparam = new ToolkitParam();
    e5.mod("workspace.toolkit", function(){
        var api;
		var type = "${type}"; //图片、视频、专题设计
		
        //订阅响应
		var listening = function(msgName, callerId, param){
			for(var name in param){
				toolbarparam[name] = param[name];
			}

			if(toolbarparam.docLibIDs){
				toolbarparam.docLibID = toolbarparam.docLibIDs;
			}
			toolbarparam.docLibID = dealDocLibIDs(toolbarparam.docLibID);
		},
		save = function(event){
			//防连击
			var srcButton = $(this);
			srcButton.unbind("click");
			setTimeout(function(){
				srcButton.click(event.data, save)
			}, 500);
			var docLibID = toolbarparam.docLibID;
			var docIDs = toolbarparam.docIDs;
			if (!docIDs) {
				alert("请先做选择");
				return;
			}

			if (type == 0) { //选图片库
				try {
					//把选中的图片地址也传回去。用于单选图的场景，如微信图文
					var imgPath = getImgSelected();
					parent.online_image.picClose(docLibID, docIDs, imgPath);
				} catch(e){ alert("父窗口应实现picClose(docLibID,docID)方法"); }
			} else if (type == 1) {//选视频库
				try { parent.videoClose(docLibID, docIDs); } catch(e){ alert("父窗口应实现videoClose(docLibID,docID)方法"); }
			} else { //选其它带组的库
				try {
                    parent.groupSelectOK(docLibID, docIDs, type);
				} catch(e){ alert("父窗口应实现groupSelectOK(docLibID,docID)方法"); }
			}
		},
		cancel = function(event){
			if (type == 0) {
				try { parent.online_image.picCancel(); } catch(e){ alert("父窗口应实现picCancel()方法"); }
			} else if (type == 1) {
				try { parent.videoCancel(); } catch(e){ alert("父窗口应实现videoCancel()方法"); }
			} else {
				try {
                    parent.groupSelectCancel(type);
				} catch(e){ alert("父窗口应实现groupSelectCancel()方法"); }
			}
		},
		getImgSelected = function() {
			var td = $("#listing .selected .album_image_td");
			var imgPath = td.find("img").attr("src");
			if (imgPath)
				imgPath = imgPath.replace(".0.jpg", "");
			return imgPath;
		},
        //若是同库，则只返回一个ID
		dealDocLibIDs = function(docLibIDs){
			if(!docLibIDs) return "";

			var libArr = (docLibIDs + "").split(","),
					docLibID = libArr[0];

			for(var i = 1; i < libArr.length; i++){
				if(libArr[i] && libArr[i] != docLibID){
					return docLibIDs;
				}
			}
			return docLibID;
		}
        //-----init & onload--------
        var init = function(sandbox){
            api = sandbox;
            $("#doSave").click(save);
            $("#doCancel").click(cancel);

            api.listen("workspace.doclist:doclistTopic", listening);
        };
        return {
            init: init
        }
	});
</script>
