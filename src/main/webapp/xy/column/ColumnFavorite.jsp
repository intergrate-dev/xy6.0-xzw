<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
	<title>栏目树checkbox选择</title>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
</head>
<body style="margin-bottom:0">
	<%@include file="TreeFavorite.inc"%>
	<script>
		//渠道
		var parentID="<c:out value="${param.parentID}"/>";
		var colID="<c:out value="${param.ids}"/>";
		col_tree.ch = "<c:out value="${param.ch}"/>";
        var DocLibID = "<c:out value="${param.DocLibID}"/>";

		//url : xy/column/ColumnCheck.jsp?type=&ids=
		
		//设置栏目树需要的参数
		col_tree.check.ids = "<c:out value="${param.ids}"/>";
		if (col_tree.check.ids==""){
			col_tree.check.ids=sessionStorage.getItem("ids");
		}
		col_tree.siteID = "<c:out value="${param.siteID}"/>";
		if (!col_tree.siteID) col_tree.siteID = 1;
		col_tree.check.enable = true;

		//是否使用缓存
		var usecache = "<c:out value="${param.cache}"/>";
		if (usecache == "1") {
			col_tree.rootUrl = "../../xy/colcache/Tree.do";
			col_tree.rootPath = "../../xy/colcache/";
		} else {
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
			if (parentID){
				col_tree.rootUrl += "?ch=" + col_tree.ch + "&parentID=" + parentID;
			}else{
				col_tree.rootUrl += "?op=1&ch=" + col_tree.ch;
			}
			col_tree.check.chkStyle = "radio";
		} else {
			col_tree.rootUrl += "?admin=1&ch=" + col_tree.ch;
		}
		//默认是复选，需单选时可加参数style=radio
		if ("<c:out value="${param.style}"/>" == "radio") {
			col_tree.check.chkStyle = "radio";
		}
        
        //发布送审选择
		var opType = "<c:out value="${param.opType}"/>";
		var cookiePuborApr = "<c:out value="${param.puborApr}"/>";
		if(opType == "ToSite"){
		    $("#btnColCancel").hide();
            $("#btnColOK").hide();
		}
		if(opType == "copy"){
            col_tree.check.chkStyle = "checkbox";
            col_tree.colClick0 = colClick0;
            col_tree._check = _check;
           if( cookiePuborApr === "1" ||cookiePuborApr === "2")
            {
                $("#chkColPub").attr("checked", true);
                $("#chkColtoApr").attr("checked", true);
            }
            if(col_tree.check.ids != ""){
                var puboraprURL = "../../xy/column/canPuborApr.do?colID=" + col_tree.check.ids + "&siteID=" + col_tree.siteID + "&DocLibID=" + DocLibID;
                $.ajax({url:puboraprURL, async:false,  success:puborapr})
			}
		}

		//按钮
		$("#divColBtn").show();
		$("#btnColCancel").click(doCancel);
		$("#btnColOK").click(getChecks);
        
        
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
				parent.columnClose(col_tree.getFilterChecks(), col_tree.getChecks());
			} catch (e) {
				var hint = "父窗口应实现columnClose(filterChecked, checked)方法供栏目树关闭时调用。"
				//	+ "\n   每个参数的格式是:  [ids, names, cascadeIDs]"
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
			return col_tree.getChecks();
		}

        function colClick0(event, treeId, treeNode, clickFlag){
        	var puboraprURL = "../../xy/column/canPuborApr.do?colID=" + treeNode.id + "&siteID=" + col_tree.siteID + "&DocLibID=" + DocLibID;
            $.ajax({url:puboraprURL, async:false,  success:puborapr})
        }
        function  _check(event, treeId, treeNode){
            var puboraprURL = "../../xy/column/canPuborApr.do?colID=" + treeNode.id + "&siteID=" + col_tree.siteID + "&DocLibID=" + DocLibID;
            $.ajax({url:puboraprURL, async:false,  success:puborapr})
        }

        //回调：1：发布 2：送审 0：无权限
        function puborapr(data) {
            $("#divColPuborApr").show();
            if (data == "1") {
                $("#labColPub").show();
                $("#labColtoApr").hide();
            } else if(data == "2"){
                $("#labColPub").hide();
                $("#labColtoApr").show();
            } else {
                $("#labColPub").hide();
                $("#labColtoApr").hide();
            }
        }

	</script>
	<script type="text/javascript">
		$(function(){
			//$(parent.window).height() + "px"
			//document.getElementById("rs_tree").style.height = 500;
			$("#rs_tree", window.parent.document).css("height",500);
		});
	</script>
</body>
</html>