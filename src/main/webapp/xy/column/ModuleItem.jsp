<%@include file="../../e5include/IncludeTag.jsp"%>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
	<style>
	.frm {
		border: 0;
		width: 100%;
		height: 475px;
	}
	</style>
</head>
<body>
	<iframe name="frmArt" id="frmArt" src="" class="frm"></iframe>
	<script>
		//从后台获取参数
		var siteID = "<c:out value="${siteID}"/>";
		var itemLibID = "<c:out value="${docLibID}"/>";
		var UUID = "<c:out value="${UUID}"/>";
		var type = "<c:out value="${type}"/>";
		var moduleID = "<c:out value="${moduleID}"/>";
		var targetColID = "<c:out value="${targetColID}"/>";
		
		$(function() {
			type = parseInt(type);
			var dataUrl = null;
			if (type == 0) {
				dataUrl = "../../xy/MainArticle.do?type=6&ch=1&siteID=" + siteID + "&colID=" + targetColID;
			} else if (type == 1) {
				dataUrl = "../../xy/column/ColumnCheck.jsp?cache=1&type=radio&ch=1&style=1&parentID=" + targetColID;
                //dataUrl = "../../xy/column/ColumnCheck.jsp?cache=1&type=radio&ids=&ch=1&siteID="+ siteID;
			} else if (type == 2) {
				dataUrl = "../../xy/GroupSelect.do?type=3&siteID=" + siteID;
			} else if (type == 3) {
				dataUrl = "../../xy/SimpleSelect.do?type=3&siteID=" + siteID;
			} else if (type == 4) {
				dataUrl = "../../xy/SimpleSelect.do?type=2&siteID=" + siteID;
			}else if (type == 5){
                dataUrl = "../../xy/column/ColumnCheck.jsp?cache=1&style=checkbox&type=op&ids=&ch=1&siteID="+ siteID;
			}
			$("#frmArt").attr("src", dataUrl);
		});
	
	//选稿窗口点“确定”
    function articleClose(docLibID, docIDs) {
		if (!docIDs) {
			articleCancel();
			return;
		}
        $.ajax({
			url : "../../xy/module/SaveModuleItem.do",
            async : false,
            data : {
                "docIDs" : docIDs,
                "docLibID" : docLibID,
                "itemLibID" : itemLibID,
                "type" : type,
                "moduleID" : moduleID
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                alert("操作失败。" + errorThrown + ':' + textStatus);  // 错误处理
            },
            success : function(datas) {
				if (datas != "ok") {
					alert("保存时异常：" + datas);
				} else {
					var dataUrl = "../../e5workspace/after.do?UUID=" + UUID + "&&DocIDs=" + moduleID;
					window.location.href = dataUrl;
				}
            }
        });
    }
	//栏目选择窗口点“确定”
	function columnClose(filterChecks, checks) {
		articleClose(1, checks[0]);
	}
	//问吧/问答/活动选择窗口点“确定”
	function groupSelectOK(docLibID, docIDs) {
		articleClose(docLibID, docIDs);
	}
	
	//选稿窗口点“取消”
	function articleCancel() {
		var url = "../../e5workspace/after.do?UUID=" + UUID;
		$("#frmArt").attr("src", url);
	}
	//栏目选择窗口点“取消”
	function columnCancel() {
		articleCancel();
	}
	//问吧/问答/活动选择窗口点“取消”
	function groupSelectCancel() {
		articleCancel();
	}
</script>
</body>
</html>