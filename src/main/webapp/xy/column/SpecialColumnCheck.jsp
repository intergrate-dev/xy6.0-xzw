<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
	<title>栏目树checkbox选择</title>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
</head>
<body style="margin-bottom:70px">
	<%@include file="Tree.inc"%>
	<script>
		//渠道
		col_tree.ch = "<c:out value="${param.ch}"/>";

		//url : xy/column/ColumnCheck.jsp?type=&ids=
		
		//设置栏目树需要的参数
		col_tree.check.ids = "<c:out value="${param.ids}"/>";
		col_tree.siteID = parent.siteID;
		if (!col_tree.siteID) col_tree.siteID = 1;
		//是否使用缓存
		var usecache = "<c:out value="${param.cache}"/>";
		if (usecache == "1") {
			col_tree.rootUrl = "../../xy/colcache/Tree.do";
			col_tree.rootPath = "../../xy/colcache/";
		} else if(usecache == "") {
			col_tree.rootUrl = "../../xy/colcache/Tree.do";
			col_tree.rootPath = "../../xy/colcache/";
		} else{
			col_tree.rootUrl = "../../xy/column/Tree.do";
		
		}
		
		var type = "<c:out value="${param.type}"/>";
		if (type == "all") {
			col_tree.rootUrl += "?parentID=0&ch=" + col_tree.ch;
		} else if (type == "admin") {
			//取有管理权限的树
			col_tree.rootUrl += "?admin=1&ch=" + col_tree.ch;
		} else if (type == "op") {
			//取有操作权限的树
			col_tree.rootUrl += "?op=1&ch=" + col_tree.ch;
			col_tree.check.chkboxType = {"Y":"", "N":""};
		} else if (type == "radio") {
			//取有操作权限的树，单选
			col_tree.rootUrl += "?op=1&ch=" + col_tree.ch;
			col_tree.check.chkStyle = "radio";
		} else if(type == 0 || type == 1){//type：0--专题选稿图册列表；1--专题选稿列表
			col_tree.rootUrl += "?op=1&ch=" + col_tree.ch;
			col_tree.check.chkboxType = {"Y":"", "N":""};
		} else{
			col_tree.rootUrl += "?admin=1&ch=" + col_tree.ch;
		
		}
		//默认是复选，需单选时可加参数style=radio
		if ("<c:out value="${param.style}"/>" == "radio") {
			col_tree.check.chkStyle = "radio";
		}

		function getChecks() {
			try {
				parent.columnClose(col_tree.getFilterChecks(), col_tree.getChecks());
			} catch (e) {
				//var hint = "父窗口应实现columnClose(filterChecked, checked)方法供栏目树关闭时调用。"
				//	+ "\n   每个参数的格式是:  [ids, names, cascadeIDs]"
				//alert(hint);
			}
		}
		
		function doCancel() {
			try {
				parent.columnCancel();
			} catch (e) {
				//var hint = "父窗口应实现columnCancel()方法供栏目树取消时调用。";
				//alert(hint);
			}
		}
		
		//提供一个接口供父级窗口调用
		function  getAllFilterChecked(){
			return col_tree.getChecks();
		}
		var col_param = {
				colID : 0, //当前选中的栏目ID
		};
		var _treeClick = function(treeNode) {
			if (treeNode.nocheck)
				return false;
			col_param.colID = treeNode.id; // 给columnColor.js使用
		};
		
		var colClick0 = function(event, treeId, treeNode, clickFlag) {
			var colID = treeNode.id;
			var url = "../SpecialArticle.do?ch=" + col_tree.ch
					+ "&colID=" + colID
					+ "&siteID=" + col_tree.siteID
					+ "&type=" + type 
					;
			window.parent.frames["frmRight"].location.href = url;
			_treeClick(treeNode);
		};
		col_tree.colClick0 = colClick0;
		//按钮
		$("#divColBtn").hide();
		
		
	</script>
	<script type="text/javascript">
		function getFirstNodeId(){
			var nodes = col_tree.tree.getNodes();
			if (nodes && nodes.length > 0) {
				return nodes[0].id;
			}
			return -2;
		}
		function checkFirstNode(){
			var nodes = col_tree.tree.getNodes();
			if (nodes && nodes.length > 0) {
				col_tree._nodeClick(nodes[0]);
			}
		}
		$(function(){
			//$(parent.window).height() + "px"
			//document.getElementById("rs_tree").style.height = 500;
			$("#rs_tree", window.parent.document).css("height","400px");
			checkFirstNode();
		});
	</script>
</body>
</html>