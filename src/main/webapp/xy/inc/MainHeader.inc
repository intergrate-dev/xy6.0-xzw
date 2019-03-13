<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
	<meta content="text/html;charset=utf-8" http-equiv="Content-Type" />
	<meta content="IE=edge" http-equiv="X-UA-Compatible" />
	<title><%=com.founder.e5.context.Context.getSystemName()%><c:out value="${subTab.name}"/></title>
	<script type="text/javascript" src="../e5script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="../e5script/e5.min.js"></script>
	<script type="text/javascript" src="../e5script/e5.utils.js"></script>
	<script type="text/javascript" src="../e5workspace/script/Param.js"></script>

	<link rel="stylesheet" type="text/css" href="script/bootstrap/css/bootstrap.css"/>
	<link rel="stylesheet" type="text/css" href="css/main.css">
</head>
<script type="text/javascript">
	var main_param = {
		docTypeID : "<c:out value="${domInfo.docTypeID}"/>",
		docLibID : "<c:out value="${domInfo.docLibID}"/>",
		fvID : "<c:out value="${domInfo.folderID}"/>",
		ruleFormula : "<c:out value="${domInfo.rule}"/>",
		listID : "<c:out value="${domInfo.listIDs}"/>",
		queryID : "<c:out value="${domInfo.queryID}"/>",
		curTab : "<c:out value="${subTab.id}"/>",
		exportable : "<c:out value="${subTab.exportable}"/>",
		groupID:"<c:out value="${groupID}"/>",
		siteID : "<c:out value="${siteID}"/>"
	}
	//取出列表列上的操作
	var main_procs = {
		procArr : [],
		
		init : function() {
			var procUrl = main_procs.getUrl(main_param.docLibID, main_param.fvID, main_param.ruleFormula);
			main_procs.readData(procUrl);
		},
		readData : function(procUrl) {
			$.ajax({url:procUrl, async:false, dataType:'json', success:function(data){
				main_procs.procArr = data;
			}});
		},
		getUrl : function(docLibID, fvID, rule, opFlow) {
			var procUrl = "ListProcs.do?docLibID=" + docLibID + "&fvID=" + fvID;
			if (opFlow || rule.indexOf("@AUDIT") >= 0) {
				procUrl += "&opFlow=1";
			} else {
				procUrl += "&opFlow=0";
			}
			return procUrl;
		}
	};
	$(function() {
		main_procs.init();
	});
</script>